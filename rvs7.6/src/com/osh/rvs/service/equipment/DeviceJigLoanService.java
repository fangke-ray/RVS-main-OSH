package com.osh.rvs.service.equipment;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.equipment.DeviceJigLoanEntity;
import com.osh.rvs.bean.master.DevicesManageEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.ToolsManageEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.equipment.DeviceJigLoanForm;
import com.osh.rvs.form.qa.ServiceRepairManageForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.equipment.DeviceJigLoanMapper;
import com.osh.rvs.mapper.master.DevicesManageMapper;
import com.osh.rvs.mapper.master.ToolsManageMapper;
import com.osh.rvs.service.CheckResultService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.qa.ServiceRepairRefereeService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class DeviceJigLoanService {

	public List<DeviceJigLoanForm> getLoanedOfUser(LoginData user, Integer reason, SqlSession conn) {
		DeviceJigLoanMapper mapper = conn.getMapper(DeviceJigLoanMapper.class);
		DeviceJigLoanEntity condition = new DeviceJigLoanEntity();
		condition.setOperator_id(user.getOperator_id());
		condition.setPosition_id(user.getPosition_id());
		condition.setReason(reason);

		List<DeviceJigLoanEntity> list = mapper.getAllLoaned(condition);
		List<DeviceJigLoanForm> retList = new ArrayList<DeviceJigLoanForm>();

		BeanUtil.copyToFormList(list, retList, CopyOptions.COPYOPTIONS_NOEMPTY, DeviceJigLoanForm.class);
		return retList;
	}

	public List<String> getLoaningUnregisting(ProductionFeatureEntity pf, String operator_id, SqlSession conn) {
		DeviceJigLoanMapper mapper = conn.getMapper(DeviceJigLoanMapper.class);
		DeviceJigLoanEntity condition = new DeviceJigLoanEntity();
		condition.setOperator_id(operator_id);
		condition.setMaterial_id(pf.getMaterial_id());
		condition.setPosition_id(pf.getPosition_id());
		condition.setRework(pf.getRework());
		return mapper.getLoaningUnregisting(condition);
	}

	public List<DeviceJigLoanEntity> getLoanApplyTraceByMaterial(String material_id, String position_id, Integer rework, SqlSession conn) {
		DeviceJigLoanMapper mapper = conn.getMapper(DeviceJigLoanMapper.class);
		DeviceJigLoanEntity condition = new DeviceJigLoanEntity();
		condition.setMaterial_id(material_id);
		condition.setPosition_id(position_id);
		condition.setRework(rework);

		return mapper.getLoanApplyTraceByMaterial(condition);
	}

	public void registToMaterial(ProductionFeatureEntity pf,
			List<String> loaningUnregisting, SqlSessionManager conn) {
		DeviceJigLoanEntity condition = new DeviceJigLoanEntity();
		condition.setMaterial_id(pf.getMaterial_id());
		condition.setPosition_id(pf.getPosition_id());
		condition.setRework(pf.getRework());
		DeviceJigLoanMapper mapper = conn.getMapper(DeviceJigLoanMapper.class);
		for (String key : loaningUnregisting) {
			condition.setDevice_jig_loan_key(key);
			mapper.insertApplyTrace(condition);
		}
	}

	public void revert(String device_jig_loan_keys, SqlSessionManager conn) {
		if (device_jig_loan_keys == null) {
			return;
		}
		DeviceJigLoanMapper mapper = conn.getMapper(DeviceJigLoanMapper.class);
		mapper.finishLoan(device_jig_loan_keys.split(","));
	}

	public List<DeviceJigLoanForm> getMoreOfUser(LoginData user, SqlSession conn) {
		// 取得员工所有工位设备
		List<PositionEntity> positions = user.getPositions();

		DevicesManageMapper dmMapper = conn.getMapper(DevicesManageMapper.class);

		DevicesManageEntity condition = new DevicesManageEntity();
		List<DeviceJigLoanForm> retList = new ArrayList<DeviceJigLoanForm>();

		for (PositionEntity position : positions) {
			if (user.getPosition_id() != null && user.getPosition_id().equals(position.getPosition_id())) {
				continue;
			}
			if (PositionService.getPositionUnitizeds(conn).containsKey(position.getPosition_id())) { // 分配到映射工位的
				continue;
			}

			condition.setPosition_id(position.getPosition_id());
			List<DevicesManageEntity> list = dmMapper.searchDistribute(condition);

			if (list.size() == 0) continue;

			String section_id = list.get(0).getSection_id();
			if (section_id == null) {
				section_id = user.getSection_id();
			}
			boolean infectPassed = CheckResultService.checkInfectPass(section_id, position.getPosition_id());

			for (DevicesManageEntity dmEntity : list) {
				DeviceJigLoanForm ret = new DeviceJigLoanForm();

				ret.setObject_type("1");
				ret.setManage_id(dmEntity.getDevices_manage_id());
				ret.setManage_code(dmEntity.getManage_code());
				ret.setType_name(dmEntity.getName());
				ret.setModel_name(dmEntity.getModel_name());
				ret.setProcess_code(position.getProcess_code());
				ret.setCheck_status(infectPassed ? "OK" : "WAIT");
				retList.add(ret);
			}
		}

		ToolsManageMapper jmMapper = conn.getMapper(ToolsManageMapper.class);
		ToolsManageEntity jigDistributeEntity = new ToolsManageEntity();
		jigDistributeEntity.setResponsible_operator_id(user.getOperator_id());
		jigDistributeEntity.setStatus("1,4,5");
		List<ToolsManageEntity> list = jmMapper.searchJigDistribute(jigDistributeEntity);

		Map<String, Boolean> infectPassLocal = new HashMap<String, Boolean> ();

		PositionPanelService pfService = new PositionPanelService();

		for (ToolsManageEntity jmEntity : list) {
			DeviceJigLoanForm ret = new DeviceJigLoanForm();
			ret.setObject_type("2");
			ret.setManage_id(jmEntity.getTools_manage_id());
			ret.setManage_code(jmEntity.getManage_code());
			ret.setType_name(jmEntity.getTools_name());
			ret.setModel_name("-");
			ret.setJig_no(jmEntity.getTools_no());
			ret.setProcess_code(jmEntity.getProcess_code());

			String section_id = jmEntity.getSection_id();
			if (section_id == null) {
				section_id = user.getSection_id();
			}
			boolean infectPassed = false;
			String key = section_id + "_" + jmEntity.getPosition_id() + "_" + user.getOperator_id();
			if (infectPassLocal.containsKey(key)) {
				infectPassed = infectPassLocal.get(key);
			} else {
				infectPassed = CheckResultService.checkInfectPass(section_id, jmEntity.getPosition_id(), user.getOperator_id());
				// 如果缓存中未点检，可能未在相应画面载入判定。需要再取一次数据库
				if (!infectPassed) {
					try {
						pfService.checkPositionInfectWorkOnPass(section_id, jmEntity.getPosition_id(), null, user.getOperator_id(), conn, null);
						infectPassed = CheckResultService.checkInfectPass(section_id, jmEntity.getPosition_id(), user.getOperator_id());
					} catch (Exception e) {
					}
				}
				infectPassLocal.put(key, infectPassed);
			}

			ret.setCheck_status(infectPassed ? "OK" : "WAIT");
			retList.add(ret);
		}

		return retList;
	}

	public void loan(String manage_ids, LoginData loginData, SqlSessionManager conn) {
		if (manage_ids == null) {
			return;
		}

		String devices = "";
		String jigs = "";

		Object manageIdObject = JSON.decode(manage_ids);
		try {
			Map<String, String> manageIdMap = (Map<String, String>) manageIdObject;
			devices = manageIdMap.get("devices");
			jigs = manageIdMap.get("jigs");

		} catch(Exception e) {
			
		}

		DeviceJigLoanMapper mapper = conn.getMapper(DeviceJigLoanMapper.class);
		CommonMapper cmapper = conn.getMapper(CommonMapper.class);

		// 检查作业者的进行中作业
		PositionPanelService pfService = new PositionPanelService();
		ProductionFeatureEntity workingPf = pfService.getProcessingPf(loginData, conn);

		// 判定的情况
		if (workingPf == null && loginData.getPosition_id().equals(RvsConsts.POSITION_ANML_SR_REF)) {
			ServiceRepairRefereeService srrService = new ServiceRepairRefereeService();
			ServiceRepairManageForm mf = srrService.checkWorkingServiceRepair(loginData.getOperator_id(), conn, new ArrayList<MsgInfo>());
			if (mf != null && mf.getMaterial_id() != null && mf.getMaterial_id().startsWith("0")) {
				workingPf = new ProductionFeatureEntity();
				workingPf.setMaterial_id(mf.getMaterial_id());
				workingPf.setPosition_id(loginData.getPosition_id());
				workingPf.setRework(0);
			}
		}

		Date now = new Date();

		DeviceJigLoanEntity insertBean = new DeviceJigLoanEntity();
		insertBean.setReason(1);
		insertBean.setOn_loan_time(now);
		insertBean.setOperator_id(loginData.getOperator_id());

		List<String> deviceKeys = new ArrayList<String>();
		if (!isEmpty(devices)) {
			insertBean.setObject_type(1);
			for (String manageId : devices.split(",")) {
				insertBean.setManage_id(manageId);
				mapper.insertLoan(insertBean);

				if (workingPf != null) {
					deviceKeys.add(cmapper.getLastInsertID());
				}
			}
		}

		List<String> jigKeys = new ArrayList<String>();
		if (!isEmpty(jigs)) {
			insertBean.setObject_type(2);
			for (String manageId : jigs.split(",")) {
				insertBean.setManage_id(manageId);
				mapper.insertLoan(insertBean);

				if (workingPf != null) {
					jigKeys.add(cmapper.getLastInsertID());
				}
			}
		}

		// 插入进行品的追溯
		if (workingPf != null) {
			insertBean.setPosition_id(workingPf.getPosition_id());
			insertBean.setRework(workingPf.getRework());
			insertBean.setMaterial_id(workingPf.getMaterial_id());
			for (String key : deviceKeys) {
				insertBean.setDevice_jig_loan_key(key);
				mapper.insertApplyTrace(insertBean);
			}
			for (String key : jigKeys) {
				insertBean.setDevice_jig_loan_key(key);
				mapper.insertApplyTrace(insertBean);
			}
		}
	}

	public String checkLoaningNowText(String operator_id, SqlSession conn) {
		DeviceJigLoanMapper mapper = conn.getMapper(DeviceJigLoanMapper.class);
		List<DeviceJigLoanEntity> list = mapper.countLoanedOfOperator(operator_id);
		if (list.size() > 0) {
			int countD=0, countJ = 0;
			for (DeviceJigLoanEntity entity : list) {
				switch(entity.getObject_type()) {
				case 1 : countD++; break;
				case 2 : countJ++; break;
				}
			}
			String retText = "";
			if (countD > 0) {
				retText += "设备·一般工具 " + countD + " 件";
			}
			if (countJ > 0) {
				if (retText.length() > 0) retText += "，";
				retText += "治工具 " + countJ + " 件";
			}
			retText = "当前您有：" + retText + "借用与动物实验内镜的维修。";

			return retText;
		} else {
			return null;
		}
	}

	public void checkLoaningNow(String operator_id, HttpSession session,
			SqlSession conn) {
		DeviceJigLoanMapper mapper = conn.getMapper(DeviceJigLoanMapper.class);
		List<DeviceJigLoanEntity> list = mapper.countLoanedOfOperator(operator_id);
		if (list.size() > 0) {
			session.setAttribute("DJ_LOANING", "true");
		} else {
			session.removeAttribute("DJ_LOANING");
		}
	}
}

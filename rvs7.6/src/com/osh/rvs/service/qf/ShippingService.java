package com.osh.rvs.service.qf;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.qa.QualityAssuranceMapper;
import com.osh.rvs.mapper.qf.ShippingMapper;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.equipment.DeviceJigLoanService;
import com.osh.rvs.service.inline.PositionPanelService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.IntegerConverter;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ShippingService {
	private Logger _log = Logger.getLogger(getClass());

	public List<MaterialEntity> getWaitingMaterial(String position_id, SqlSession conn) {
		// TODO Auto-generated method stub

		ShippingMapper sDao = conn.getMapper(ShippingMapper.class);

		// 取得待品保处理对象一览 711
		List<MaterialEntity> waitings = sDao.getWaitings(position_id);

		return waitings;
	}

	public List<MaterialEntity> getFinishedMaterial(String position_id, SqlSession conn) {

		ShippingMapper sDao = conn.getMapper(ShippingMapper.class);

		// 取得今日已完成处理对象一览
		List<MaterialEntity> finished = sDao.getFinished(position_id);

		return finished;
	}

	public void updateMaterial(HttpServletRequest req, SqlSessionManager conn) throws Exception {
		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		PositionPanelService ppService = new PositionPanelService();
		// 取得当前作业中作业信息
		ProductionFeatureEntity workingPf = ppService.getWorkingPf(user, conn);

		// 检查维修对象表单 TODO
		MaterialEntity bean = new MaterialEntity();
		// 进行中的维修对象
		bean.setMaterial_id(workingPf.getMaterial_id());
		bean.setOutline_time(new Date());
		bean.setBound_out_ocm(IntegerConverter.getInstance().getAsObject(req.getParameter("bound_out_ocm")));
		bean.setPackage_no(req.getParameter("package_no"));
		bean.setOcm_shipping_date(new Date());

		// 更新维修对象。
		QualityAssuranceMapper dao = conn.getMapper(QualityAssuranceMapper.class);
		dao.updateMaterial(bean);

		// FSE 数据同步
		try{
			FseBridgeUtil.toUpdateMaterialProcess(workingPf.getMaterial_id(), "711");
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

		// 工时按标准工时：
		String sUse_seconds = RvsUtils.getZeroOverLine("_default", null, user, "711");
		Integer use_seconds = 600;
		try {
			use_seconds = Integer.parseInt(sUse_seconds) * 60;
		} catch (Exception e) {

		}

		// 作业信息状态改为，作业完成
		ProductionFeatureMapper pfdao = conn.getMapper(ProductionFeatureMapper.class);
		workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
		workingPf.setUse_seconds(use_seconds);
		// Dummy
		workingPf.setPcs_inputs(req.getParameter("pcs_inputs"));
		workingPf.setPcs_comments(req.getParameter("pcs_comments"));
		// Dummy
		pfdao.finishProductionFeatureSetFinish(workingPf);
		
	}

	public ProductionFeatureEntity scanMaterial(SqlSessionManager conn, String material_id, HttpServletRequest req, List<MsgInfo> errors,
			Map<String, Object> listResponse) throws Exception {
		PositionPanelService ppService = new PositionPanelService();

		ProductionFeatureEntity waitingPf = null;

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();// TODO
		user.setSection_id(null);
		if (user.getPosition_id() == null) {
			user.setPosition_id(RvsConsts.POSITION_SHIPPING);
			user.setProcess_code("711");
			user.setLine_id("00000000011");
		}

		// 判断维修对象已经完成出货
		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);
		if (pfMapper.checkPositionDid(material_id, user.getPosition_id(), ""+RvsConsts.OPERATE_RESULT_FINISH, null)) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_id");
			error.setErrcode("info.linework.shipped");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.shipped"));
			errors.add(error);
		} else {
		
			// 判断维修对象在等待区，并返回这一条作业信息
			waitingPf = ppService.checkMaterialId(material_id, "true", user, errors, conn);

			if (errors.size() == 0) {
				getProccessingData(listResponse, material_id, waitingPf, user, conn);

				// 作业信息状态改为，作业中
				ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
				waitingPf.setOperator_id(user.getOperator_id());
				dao.startProductionFeature(waitingPf);

				// 判断借用设备
				if (PositionService.getPositionUnitizeds(conn).containsKey(user.getPosition_id())) {
					DeviceJigLoanService djlService = new DeviceJigLoanService();

					// 现在已借用的设备治具未登记给维修品
					List<String> loaningUnregisting = djlService.getLoaningUnregisting(waitingPf,
							user.getOperator_id(), conn);

					// 在借用的前提下
					djlService.registToMaterial(waitingPf, loaningUnregisting, conn);
				}
			}
		}

		user.setSection_id(section_id); // TODO

		return waitingPf;
	}

	public void getProccessingData(Map<String, Object> listResponse, String material_id, ProductionFeatureEntity pf,
			LoginData user, SqlSession conn) throws Exception {
		// 取得维修对象信息。
		MaterialForm mform = getMaterialInfo(material_id, conn);
		listResponse.put("mform", mform);
	}

	public MaterialForm getMaterialInfo(String material_id, SqlSession conn) {
		MaterialForm materialForm = new MaterialForm();
		ShippingMapper dao = conn.getMapper(ShippingMapper.class);
		MaterialEntity materialEntity = dao.getMaterialDetail(material_id);
		BeanUtil.copyToForm(materialEntity, materialForm, CopyOptions.COPYOPTIONS_NOEMPTY);

		return materialForm;
	}

	public void putinTrolley(String material_id, String trolley_code,
			SqlSessionManager conn) {
		ShippingMapper mapper = conn.getMapper(ShippingMapper.class);
		mapper.putIntoTrolley(trolley_code, material_id);
	}

	public List<MaterialEntity> getInTrolleyMaterials(SqlSession conn) {
		ShippingMapper mapper = conn.getMapper(ShippingMapper.class);
		return mapper.getInTrolleyMaterials();
	}

	public void finishForTrolley(String trolley_code, SqlSessionManager conn) {
		ShippingMapper mapper = conn.getMapper(ShippingMapper.class);

		mapper.updateMaterialTimeNodeShipment(trolley_code);
		mapper.clearTrolley(trolley_code);
		
	}

}

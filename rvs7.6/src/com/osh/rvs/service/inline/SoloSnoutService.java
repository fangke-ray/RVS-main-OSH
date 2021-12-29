package com.osh.rvs.service.inline;

import static framework.huiqing.common.util.CommonStringUtil.joinBy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.data.SnoutEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.inline.WaitingEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.ProcessAssignEntity;
import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.PcsUtils;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.inline.SnoutForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.inline.LeaderPcsInputMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.inline.SoloProductionFeatureMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.master.PositionMapper;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.PostMessageService;
import com.osh.rvs.service.ProcessAssignService;
import com.osh.rvs.service.partial.ComponentManageService;
import com.osh.rvs.service.partial.ComponentSettingService;
import com.osh.rvs.service.proxy.ProcessAssignProxy;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class SoloSnoutService {
	protected static final Logger logger = Logger.getLogger(SoloSnoutService.class);

	private static final Integer SEARCH_STATUS_USED = 4; 
	private static final String[] WITH_ORIGIN_POSITIONS = {"00000000024"}; 

	/**
	 * 作业中发生暂停后，产生作业下一步暂停等待信息
	 * 作业中发生中断后恢复，产生作业下一步暂停等待信息
	 * @param productionFeature
	 * @param conn
	 * @throws Exception 
	 */
	public void pauseToNext(SoloProductionFeatureEntity productionFeature, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		// Pace + 1
		productionFeature.setPace(productionFeature.getPace() + 1);
		// 还没有处理者
		// productionFeature.setOperator_id(null);
		// 状态总是暂停再开
		productionFeature.setOperate_result(RvsConsts.OPERATE_RESULT_PAUSE);
		productionFeature.setAction_time(new Date());
		productionFeature.setFinish_time(null);
		// 其他沿用

		// 作成新等待记录
		dao.insert(productionFeature);
	}

	public void breakWork(SoloProductionFeatureEntity productionFeature,
			SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		productionFeature.setOperate_result(RvsConsts.OPERATE_RESULT_BREAK);
		productionFeature.setUse_seconds(null);
		dao.breakWork(productionFeature);
	}

	public void breakToNext(SoloProductionFeatureEntity productionFeature,
			SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		// Pace
		productionFeature.setPace(productionFeature.getPace() + 1);
		// (以前为什么要“保持处理者”？)
		productionFeature.setOperator_id("00000000000");
		// 状态总是暂停再开
		productionFeature.setOperate_result(RvsConsts.OPERATE_RESULT_BREAK);
		productionFeature.setAction_time(null);
		productionFeature.setFinish_time(null);
		// 其他沿用

		// 作成新等待记录
		dao.insert(productionFeature);
	}

	/**暂停
	 * @throws Exception */
	public void pauseToSelf(SoloProductionFeatureEntity productionFeatureEntity, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		productionFeatureEntity.setOperate_result(RvsConsts.OPERATE_RESULT_PAUSE);
		mapper.finish(productionFeatureEntity);

		productionFeatureEntity.setPace(productionFeatureEntity.getPace() + 1);
		productionFeatureEntity.setAction_time(new Date());
		// #{pace}, 

		mapper.insert(productionFeatureEntity);
	}

	/**暂停再开
	 * @throws Exception */
	public void pauseToResume(SoloProductionFeatureEntity productionFeatureEntity, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);

		mapper.updateToResume(productionFeatureEntity);
	}

	/**完成
	 * @throws Exception */
	public void finish(SoloProductionFeatureEntity productionFeatureEntity, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		productionFeatureEntity.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
		mapper.finish(productionFeatureEntity);
	}

	public Integer getTotalTime(SoloProductionFeatureEntity workingPf, SqlSession conn) {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		return dao.getTotalTime(workingPf);
	}

	public Date getFirstPaceOnRework(SoloProductionFeatureEntity workingPf, SqlSession conn) {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		return dao.getFirstStartTime(workingPf);
	}

	public String getSnoutsMakerReferChooser(SqlSession conn) {
		List<String[]> lst = new ArrayList<String[]>();
		
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		List<OperatorNamedEntity> allOperator = dao.getSnoutsMaker();
		
		for (OperatorNamedEntity operator: allOperator) {
			String[] p = new String[3];
			p[0] = operator.getOperator_id();
			p[1] = operator.getName();
			p[2] = operator.getRole_name();
			lst.add(p);
		}
		
		String pReferChooser = CodeListUtils.getReferChooser(lst);
		
		return pReferChooser;
	}

	public List<SnoutForm> search(ActionForm form, SqlSession conn) {
		List<SnoutForm> ret = new ArrayList<SnoutForm>();
		SnoutEntity condition = new SnoutEntity();
		BeanUtil.copyToBean(form, condition, CopyOptions.COPYOPTIONS_NOEMPTY);
		if (CommonStringUtil.isEmpty(condition.getPosition_id()))
			condition.setPosition_id("00000000024"); // 先端组件画面的默认查询

		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		List<SnoutEntity> result = dao.searchSnouts(condition);

		BeanUtil.copyToFormList(result, ret, CopyOptions.COPYOPTIONS_NOEMPTY, SnoutForm.class);
		return ret;
	}

	public SnoutForm getDetail(String serial_no, String from_position_id, SqlSession conn) {
		SnoutForm ret = new SnoutForm();

		SnoutEntity entity = null;
		if ("24".equals(from_position_id) || "00000000024".equals(from_position_id)) {
			entity = getSnoutDetailBean(from_position_id, serial_no, conn);
		} else {
			entity = getDetailBean(serial_no, from_position_id, conn);
		}

		if (entity != null) {
			BeanUtil.copyToForm(entity, ret, CopyOptions.COPYOPTIONS_NOEMPTY);
		}
		return ret;
	}

	private SnoutEntity getSnoutDetailBean(String position_id, String serial_no, SqlSession conn) {
		SnoutEntity condition = new SnoutEntity();
		condition.setSerial_no(serial_no);
		condition.setPosition_id(position_id);

		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		List<SnoutEntity> result = dao.searchSnouts(condition);
		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	private SnoutEntity getDetailBean(String serial_no, String position_id, SqlSession conn) {
		SoloProductionFeatureEntity condition = new SoloProductionFeatureEntity();
		condition.setSerial_no(serial_no);
		condition.setPosition_id(position_id);
		condition.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);

		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		List<SoloProductionFeatureEntity> list = dao.searchSoloProductionFeature(condition);
		if (list != null && list.size() > 0) {
			SnoutEntity ret = new SnoutEntity();
			ret.setStatus(list.get(0).getUsed());

			return ret;
		} else {
			return null;
		}
	}

	public SoloProductionFeatureEntity checkWorkingPfServiceRepair(String operator_id, String position_id, SqlSession conn) {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		SoloProductionFeatureEntity pfBean = new SoloProductionFeatureEntity();
		pfBean.setOperator_id(operator_id);
		pfBean.setPosition_id(position_id);
		pfBean.setAction_time_null(0);
		pfBean.setFinish_time_null(1);
		pfBean.setUsed(0);

		// 判断是否有在进行中的组装对象
		List<SoloProductionFeatureEntity> result = dao.searchSoloProductionFeature(pfBean);
		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	public SoloProductionFeatureEntity getCompPositionDetailBean(String serial_no, String section_id, String position_id, SqlSession conn) {
		SoloProductionFeatureEntity condition = new SoloProductionFeatureEntity();
		condition.setSerial_no(serial_no);
		condition.setSection_id(section_id);
		condition.setPosition_id(position_id);
		condition.setFinish_time_null(1);

		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		List<SoloProductionFeatureEntity> list = dao.searchSoloProductionFeature(condition);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public String getSnoutPcs(String serial_no, String model_name, SqlSession conn) {

		String showLine = "NS 工程";

		Map<String, String> fileTempl = PcsUtils.getXmlContents(showLine, model_name, null, conn);

		Map<String, String> fileTemplSolo = new HashMap<String, String>();
		List<String> hitkeys = new ArrayList<String>();
		for (String key : fileTempl.keySet()) {
			if (key.contains("先端预制") || key.contains("D／E组装")) {
				hitkeys.add(key);
				fileTemplSolo.put(key, fileTempl.get(key));
//				break;
			}
		}

		Map<String, String> pcsHtmls = PcsUtils.toHtmlSnout(fileTemplSolo, model_name, serial_no, "301", "00000000013", conn);

		for (String hitkey : hitkeys) {
			if (pcsHtmls.get(hitkey).length() > 0) {
				return pcsHtmls.get(hitkey);
			}
		}
		return null;
	}

	public SnoutEntity checkUpdate(HttpServletRequest req, SqlSessionManager conn, List<MsgInfo> msgInfos) {
		String serial_no = req.getParameter("serial_no");
		if (CommonStringUtil.isEmpty(serial_no)) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("dbaccess.recordNotExist");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "D/E 组件"));
			msgInfos.add(msgInfo);
			return null;
		}
		String new_model_id = req.getParameter("new_model_id");
		String new_serial_no = req.getParameter("new_serial_no");

		if (new_model_id == null && new_serial_no == null) {
			// 不更新
			return null;
		}

		SnoutEntity entity = getSnoutDetailBean("00000000024", serial_no, conn);
		if (entity == null) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("dbaccess.recordNotExist");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "D/E 组件"));
			msgInfos.add(msgInfo);
			return null;
		}

		if (CommonStringUtil.isEmpty(new_model_id)) {
			entity.setNew_model_id(entity.getModel_id());
		} else {
			entity.setNew_model_id(new_model_id);
		}

		if (CommonStringUtil.isEmpty(new_serial_no)) {
			entity.setNew_serial_no(entity.getSerial_no());
		} else {
			entity.setNew_serial_no(new_serial_no);
		}

		// 检查重复 TODO

		return entity;
	}

	public void saveLeaderInput(HttpServletRequest req, LoginData user, String material_id, SqlSessionManager conn) throws Exception {
		// 保存到线长工检票记录
		LeaderPcsInputMapper dao = conn.getMapper(LeaderPcsInputMapper.class);

		ProductionFeatureEntity pfBean = new ProductionFeatureEntity();
		pfBean.setSerial_no(req.getParameter("serial_no"));
		pfBean.setPcs_inputs(req.getParameter("pcs_inputs"));
		pfBean.setPcs_comments(req.getParameter("pcs_comments"));
		pfBean.setOperator_id(user.getOperator_id());
		pfBean.setLine_id(user.getLine_id());
		pfBean.setRework(0);

		// 已使用的工程检查票同步
		pfBean.setMaterial_id(material_id);

		dao.insert(pfBean);
	}

	public String findUsedSnoutsBySnouts(String serial, SqlSession conn) {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		return dao.findUsedSnoutsBySnouts(serial);
	}

	public void delete(String position_id, String model_id, String serial_no, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		// 删除先端组件
		dao.deleteSnouts(position_id, model_id, serial_no);

		if ("00000000024".equals(position_id)) {
			// 删除其先端来源
			dao.removeSnoutOrigin(serial_no);
		}
	}

	/**
	 * 按型号获取可使用的先端头一览
	 * 
	 * @param model_id
	 * @param conn
	 * @return
	 */
	public String getRefers(String model_id, SqlSession conn) {
		String refer =  "";
		// 寻找型号可使用的先端头一览
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		List<SoloProductionFeatureEntity> snouts = dao.getSnoutsByModel(model_id);

		for (int i = 0 ; i < snouts.size(); i++) {
			SoloProductionFeatureEntity line = snouts.get(i);
			if (i == 0 && model_id.equals(line.getModel_id())) {
				refer += "<tr class='firstMatchSnout'>"; // background-color:lightgreen;
			} else {
				refer += "<tr>";
			}
			refer += "<td class='referId' style='display:none'>" + line.getSerial_no() + "</td>";
			refer += "<td class='originId' style='display:none'>" + line.getOperator_id() + "</td>";
			refer += "<td><nobr>" + CommonStringUtil.decodeHtmlText(line.getOperator_name()) + "</nobr></td>";
			refer += "<td><nobr>" + CommonStringUtil.decodeHtmlText(line.getSerial_no()) + "</nobr></td>";
			refer += "<td><nobr>" + CommonStringUtil.decodeHtmlText(line.getProcess_code()) + "</nobr></td>";
			refer += "</tr>";
		}

		return refer;
	}

	public List<WaitingEntity> getWaitingMaterial(String section_id, String position_id,
			String operator_id, String process_code, Integer px, SqlSession conn) {
		List<WaitingEntity> ret = null;
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		if (Arrays.asList(WITH_ORIGIN_POSITIONS).contains(position_id)) {
			ret = mapper.getWaitingMaterial(section_id, position_id, operator_id);
		} else {
			ret = mapper.getWaitingComponents(section_id, position_id, operator_id);
		}

		// 取得4个工作日前
		String dateLever = DateUtil.toString(
				RvsUtils.switchWorkDate(new Date(), -4), DateUtil.ISO_DATE_PATTERN);

		if (ret != null) {
			for (WaitingEntity we : ret) {
				we.setImbalance(px);
				we.setFix_type("8");
				if ("0".equals(we.getWaitingat())) we.setWaitingat("未处理");
				else if ("4".equals(we.getWaitingat()) || "3".equals(we.getWaitingat())) { // 暂停
					// 工位特殊暂停理由
					if (we.getPause_reason() != null && we.getPause_reason() >= 70) {
						if (we.getPause_reason() == 99) {
							DryingProcessService dpService = new DryingProcessService();
	
							we.setWaitingat("烘干作业");
							String tgMaterial_id = we.getMaterial_id();
							if (tgMaterial_id == null) {
								// 取得订购来源
								ComponentManageService cmService = new ComponentManageService();
								List<ComponentManageEntity> cmList = cmService.getBySerialNo(we.getSerial_no(), conn);
								if (cmList.size() > 0) {
									tgMaterial_id = cmList.get(0).getOrigin_material_id();
								}
							}
							we.setDrying_process(
									dpService.getDryingProcessByMaterialInPosition(tgMaterial_id, position_id, conn));
						} else {
							String sReason = PathConsts.POSITION_SETTINGS.getProperty("step." + process_code + "." + we.getPause_reason());
							if (sReason == null) {
								we.setWaitingat("正常中断流程");
							} else {
								we.setWaitingat(sReason);
							}
						}
	
					} else {
						if ("3".equals(we.getWaitingat())) we.setWaitingat("中断等待再开");
						else we.setWaitingat("中断恢复");
					}
				}

				// 超时判断
				if (we.getIn_place_time() != null) {
					if (we.getIn_place_time().compareTo(dateLever) < 0) {
						we.setOvertime(2);
					}
				}
				
			}
		}

		return ret;	
	}


	/**
	 * 检查
	 * @param model_id 型号
	 * @param conn 可更新连接
	 * @throws Exception 
	 */
	public void checkBenchmark(String model_id, SqlSessionManager conn) throws Exception {
		Map<String, Integer> modelBenchmarks = RvsUtils.getSnoutModelBenchmarks(conn);
		Integer benchmark = modelBenchmarks.get(model_id);

		if (benchmark != null) {
			// 取得型号现存量
			SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
			SnoutEntity condition = new SnoutEntity();
			condition.setModel_id(model_id);
			condition.setPosition_id("00000000024");
			condition.setStatus(SEARCH_STATUS_USED);
			List<SnoutEntity> l = mapper.searchSnouts(condition);
			int available = l.size();
			// 低于安全库存
			if (available * 2 < benchmark) {
				// 发送给NS线长人员
				OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);
				OperatorEntity operator = new OperatorEntity();
				operator.setLine_id("00000000013");
				operator.setRole_id(RvsConsts.ROLE_LINELEADER);

				List<OperatorNamedEntity> leaders = oMapper.searchOperator(operator);

				// 推送需要信息制作信息
				PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
				Map<String, String> models = RvsUtils.getSnoutModels(conn);
				String modelName = models.get(model_id);

				PostMessageEntity pmEntity = new PostMessageEntity();
				pmEntity.setContent(modelName + " 机型的 D/E 组件可用数量(" + available + " 件)低于安全库存量，请安排制作。");
				pmEntity.setSender_id("0");
				pmEntity.setLevel(1);
				pmEntity.setReason(PostMessageService.SNOUT_LEAK_BY_MODEL);
				pmMapper.createPostMessage(pmEntity);

				CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
				String lastInsertId = commonMapper.getLastInsertID();

				for(OperatorNamedEntity leader: leaders){
					PostMessageEntity postMessageEntity = new PostMessageEntity();
					postMessageEntity.setPost_message_id(lastInsertId);
					postMessageEntity.setReceiver_id(leader.getOperator_id());
					pmMapper.createPostMessageSendation(postMessageEntity);
				}

			}
		}
	}

	/**
	 * 先端来源检查与返回
	 * @param material_id 先端来源
	 * @param conn
	 * @param errors
	 * @return
	 */
	public MaterialForm checkOrigin(String material_id, SqlSession conn,
			List<MsgInfo> errors) {
		MaterialForm ret = new MaterialForm();

		MaterialService ms = new MaterialService();
		MaterialEntity mBean = ms.loadMaterialDetailBean(conn, material_id);

		// 先端来源存在
		if (mBean == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_id");
			error.setErrcode("info.linework.invalidCode");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.invalidCode"));
			errors.add(error);
		} else {
			// 先端预制型号
			String model_id = mBean.getModel_id();
//			Map<String, String> snoutModels = RvsUtils.getSnoutModels(conn);
//			if (!snoutModels.containsKey(model_id)) {
//				MsgInfo error = new MsgInfo();
//				error.setComponentid("material_id");
//				error.setErrcode("info.linework.notSnoutModel");
//				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.notSnoutModel"));
//				errors.add(error);
//			}
			// 先端来源已使用
			SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
			MaterialEntity soBean =  mapper.checkSnoutOrigin(material_id, null);
			if (soBean != null && soBean.getOperate_result() == 1) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("material_id");
				error.setErrcode("info.linework.usedSnoutOrigin");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.usedSnoutOrigin"));
				errors.add(error);
			}
			if (errors.size() == 0) {
				ret.setMaterial_id(material_id);
				ret.setModel_id(model_id);
				if (soBean != null) {
					ret.setSerial_no(soBean.getSerial_no());
				}
			}
		}

		return ret;
	}

	/**
	 * 取得当月生成先端头
	 * @param snoutsByMonth 生成先端头一览，返回用
	 * @param conn 数据库会话
	 * @return 提供的先端头序列号
	 */
	public String loadSnoutsByMonth(List<MaterialEntity> snoutsByMonth,
			SqlSession conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);

		// 本月 e.g. ：1605
		String month = DateUtil.toString(new Date(), "yyMM");
		// 取得当月先端头（依照先端来源列表）
		List<MaterialEntity> snoutsByMonthInner = mapper.getSnoutOriginOnMonth(month);
		snoutsByMonth.addAll(snoutsByMonthInner);

		if (snoutsByMonthInner.size() == 0) {
			// 本月第一个号码
			return month + "001";
		} else {
			String lastestManageSerialNo = snoutsByMonthInner.get(snoutsByMonthInner.size() - 1).getSerial_no();
			try {
				int iSno = Integer.parseInt(lastestManageSerialNo);
				// 采番
				return CommonStringUtil.fillChar(("" + (iSno + 1)), '0', 7, true);
			} catch (NumberFormatException e) {
				return month + "001";
			}
		}
	}

	/**
	 * 登记先端头来源
	 * @param material_id
	 * @param serial_no
	 * @param conn
	 */
	public void registSnoutOrigin(String material_id, String serial_no,
			SqlSessionManager conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		mapper.registSnoutOrigin(material_id, null, serial_no);
	}

	/**
	 * 删除先端头来源
	 * @param material_id
	 * @param serial_no
	 * @param conn
	 */
	public void removeSnoutOrigin(String serial_no,
			SqlSessionManager conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		mapper.removeSnoutOrigin(serial_no);
	}

	public MaterialEntity getSnoutOriginBySerialNo(String serial_no, SqlSession conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		MaterialEntity me = mapper.checkSnoutOrigin(null, serial_no);
		return me;
	}
	public String getSnoutOriginNoBySerialNo(String serial_no, SqlSession conn) {
		MaterialEntity me = getSnoutOriginBySerialNo(serial_no, conn);
		if (me != null) {
			return me.getSorc_no();
		}
		return null;
	}

	public List<SnoutForm> searchSnoutsOnMonth(String month, SqlSession conn) {
		List<SnoutForm> ret = new ArrayList<SnoutForm>();

		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		String sYear = month.substring(0, 4);
		String sMonth = month.substring(4);

		Calendar cal = Calendar.getInstance();
		try {
			cal.set(Calendar.YEAR, Integer.parseInt(sYear));
			cal.set(Calendar.MONTH, Integer.parseInt(sMonth) - 1);
		} catch (NumberFormatException e) {
		}
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date startDate = cal.getTime();
		cal.add(Calendar.MONTH, 1);
		Date endDate = cal.getTime();

		List<SnoutEntity> result = dao.searchSnoutsOnMonth(startDate, endDate);

		BeanUtil.copyToFormList(result, ret, CopyOptions.COPYOPTIONS_NOEMPTY, SnoutForm.class);
		return ret;
	}

	public void startProductionFeature(SoloProductionFeatureEntity spfBean, String operator_id,
			SqlSessionManager conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		spfBean.setOperator_id(operator_id);
		mapper.startProductionFeature(spfBean);
	}

	public void pauseWaitProductionFeature(
			SoloProductionFeatureEntity spfBean, SqlSessionManager conn) {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		spfBean.setOperate_result(RvsConsts.OPERATE_RESULT_WORKING);
		dao.pauseWaitProductionFeature(spfBean);
	}

	public List<String> fingerNextPosition(SoloProductionFeatureEntity workingPf, SqlSessionManager conn, List<String> triggerList, boolean isFact) throws Exception {
		// 发动工位
		List<String> nextPositions = new ArrayList<String>();

		// NS 组件流程取得
		ProcessAssignService paService = new ProcessAssignService();
		String pat_id = paService.getDerivedId(workingPf.getModel_id(), "5", true, conn);

		ProcessAssignProxy paProxy = new ProcessAssignProxy(null, pat_id, workingPf.getSection_id(), 
				false, workingPf.getModel_id(), workingPf.getSerial_no(), conn);

		SoloProductionFeatureEntity nPf = new SoloProductionFeatureEntity();
		nPf.setSection_id(workingPf.getSection_id());
		nPf.setModel_id(workingPf.getModel_id());
		nPf.setModel_name(workingPf.getModel_name());
		nPf.setSerial_no(workingPf.getSerial_no());

		SoloProductionFeatureMapper pfDao = conn.getMapper(SoloProductionFeatureMapper.class);

		List<String> ret = new ArrayList<String>();

		getNext(paProxy, pat_id, workingPf.getPosition_id(), nextPositions);

		// 建立后续工位的初始作业信息
		for (String nextPosition_id : nextPositions) {
			nPf.setPosition_id(nextPosition_id);
			fingerPosition(workingPf.getPosition_id(), nPf, conn, pfDao, paProxy, ret, triggerList, isFact);
		}

		PositionMapper ps = conn.getMapper(PositionMapper.class);
		for (int i = 0; i < ret.size(); i++) {
			String ret_position_id = ret.get(i);
			if (ret_position_id.indexOf("[") < 0) { 
				PositionEntity position = ps.getPositionByID(ret_position_id);
				ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " B线]"); // B线
			}
		}

		return ret;
	}

	public void fingerPosition(String fromPositionId,
			SoloProductionFeatureEntity workingPf,
			SqlSessionManager conn, SoloProductionFeatureMapper pfDao,
			ProcessAssignProxy paProxy, List<String> retList,
			List<String> triggerList, boolean isFact) throws Exception {

		if (retList == null) retList = new ArrayList<String> ();

		String fingerPositionId = workingPf.getPosition_id();
		// 维修对象的课室
		String section_id = paProxy.getMaterial_section_id();
		if (section_id == null) { // (投线前) 
			section_id = workingPf.getSection_id();
		}

		{ 
			// 判断得到的工程是否有完成
			// 有完成，并且其先决也已完成，则由这个工位继续触发
			if (paProxy.checkWorked(fingerPositionId)) {
				// 取得先决
				List<String> prevPositions = new ArrayList<String>();

//				getPrev(paProxy, fingerPositionId, prevPositions);

				if (prevPositions.size() == 0 || !isFact 
						|| paProxy.getFinishedCountByPositions(prevPositions) == prevPositions.size()) {
					workingPf.setPosition_id(fingerPositionId);
					List<String> x = fingerNextPosition(workingPf, conn, triggerList, isFact);
					if (x!=null) {
						retList.addAll(x);
					}
				}
			}
			// 判断得到的工程是否有未完成。
			else if (paProxy.checkWorking(fingerPositionId) > 0) {
				// 有的话则不影响原有工作
				logger.info(fingerPositionId+"工位进行中。");
			}
			// 没有完成则判断先决的工位是否都已经结束。
			else {
				// 取得先决
				List<String> prevPositions = new ArrayList<String>();

//				getPrev(paProxy, fingerPositionId, prevPositions);

				logger.info(fingerPositionId+"工位de先决："+prevPositions.size());

				if (prevPositions.size() == 0 || !isFact
						|| paProxy.getFinishedCountByPositions(prevPositions) == prevPositions.size()) {
					// 都已经结束生成等待区信息
					logger.info("组件"+workingPf.getSerial_no()+"进行至"+fingerPositionId+"工位。( " + isFact + ")");
					SoloProductionFeatureEntity entity = new SoloProductionFeatureEntity();

					entity.setModel_id(workingPf.getModel_id());
					entity.setModel_name(workingPf.getModel_name());
					entity.setSerial_no(workingPf.getSerial_no());
					entity.setPosition_id(fingerPositionId);
					entity.setPace(0);
					entity.setSection_id(section_id);
					entity.setOperator_id("0");
					entity.setOperate_result(RvsConsts.OPERATE_RESULT_NOWORK_WAITING);

					pfDao.insert(entity);

					if (isFact) {
						// 通知
						triggerList.add("http://localhost:8080/rvspush/trigger/in/" + fingerPositionId + "/" 
				            		+ section_id + "/" + workingPf.getSerial_no() + "/" + (paProxy.isLightFix ? "1" : "0"));
					}

					logger.info("tranover");
					retList.add(fingerPositionId);
				}
			}
		}
	}

	public String getFingerString(String model_id, String serial_no, List<String> fingerList, String stockCode, SqlSession conn, boolean isFact) {
		String idNo = "(序列号" + serial_no + ")";
		if (isFact)
			if (fingerList.size() > 0) {
				return ApplicationMessage.WARNING_MESSAGES
					.getMessage("info.transfer.justFinshed", idNo, joinBy(", ", fingerList.toArray(new String[fingerList.size()])));
			} else {
				return "您现在处理中的维修对象" + idNo + "完成后请送至NS组件库位" + stockCode + "。";
			}
		else {
			if (fingerList.size() > 0) {
				return ApplicationMessage.WARNING_MESSAGES
						.getMessage("info.transfer.justNow", idNo, joinBy(", ", fingerList.toArray(new String[fingerList.size()])))
						+ "<input type='button' value='打印信息单'>";
			} else {
				return "您现在处理中的维修对象" + idNo + "完成后请送至NS组件库位。<input type='button' value='指定'><label></label><input type='button' value='打印标签'><input type='button' value='打印信息单'>";
			}
		}
	}

	public void getNext(ProcessAssignProxy paProxy, String pat_id, String position_id, List<String> nextPositions) {
		// 得到下一个工位
		List<PositionEntity> nextPositionsByPat = paProxy.getNextPositions(position_id);

		String this_position_id = null;
		if (nextPositionsByPat.size() > 0) {
			this_position_id = nextPositionsByPat.get(0).getPosition_id();
		}

		if (this_position_id == null || ((RvsConsts.PROCESS_ASSIGN_LINE_END+"").equals(this_position_id))) {
			// 下一个工位是PROCESS_ASSIGN_LINE_END
			this_position_id = RvsConsts.PROCESS_ASSIGN_LINE_END + "";
			// 用它自身的Line_id区分
			ProcessAssignEntity pa = paProxy.getProcessAssign(position_id);
			if (pa == null) return; // 未设流程时

//			// 所在流程判断是否全部完成
//			if (paProxy.getFinishedByLine(line_id)) {
//				// 如果所在流程全部完成，触发流程的下一个工位
//				getNext(paProxy, material_id, mEntity, pat_id, line_id, level, nextPositions);
//			}
		} else {
			for (PositionEntity nextPosition : nextPositionsByPat) {
				// 判断每个工位是不是分线名
				if (Integer.parseInt(nextPosition.getPosition_id()) > RvsConsts.PROCESS_ASSIGN_LINE_BASE) {
					// 是分线得到分线的由0开始的工位 ,不考虑嵌套分线
					List<String> positions = paProxy.getPartStart(nextPosition.getPosition_id());
					for (String position : positions) {
						nextPositions.add(position);
					}
				} else {
					// 增加单个工位
					nextPositions.add(nextPosition.getPosition_id());
				}
			}
		}
	}

	public void checkSettableToOrigin(MaterialEntity mEntity,
			Map<String, Object> callbackResponse, SqlSession conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		MaterialEntity origin = mapper.checkSnoutOrigin(mEntity.getMaterial_id(), null);
		int historyCnt = 0;
		if (origin == null) {
			// 根据序列号查询先端头源
			// 默认新建
//			String rootOriginId = mEntity.getMaterial_id();

			SnoutEntity hist = checkHistory(mEntity.getModel_id(), mEntity.getSerial_no(), mapper);

			if (hist != null) {
//				rootOriginId = hist.getMaterial_id();
				historyCnt = hist.getRefurbished();					
			}

			// setToOrigin(mEntity.getMaterial_id(), rootOriginId, conn);

			origin = new MaterialEntity();
		}
		String sShowSnout = "";
		if ("00000000000".equals(origin.getCustomer_id())) {
			sShowSnout = "此维修品标记为废弃 C 本体。";
		} else {
			if (origin.getSerial_no() == null) {
				if (origin.getMaterial_id() == null) {
					sShowSnout = "此维修品的 C 本体预备翻新用于 D/E 组装，已使用次数为 " + historyCnt + " 次。\n如果判定为可用请操作[回收]，判定为不可用则请操作[废弃]。";
				} else {
					sShowSnout = "此维修品的 C 本体预备翻新用于 D/E 组装，已使用次数为 " + historyCnt + " 次。\n √ 已回收";
				}
			} else if (origin.getOperate_result() != null && origin.getOperate_result() == 2) {
				sShowSnout = "此维修品的 C 本体已用于 D/E 组装，序列号为：[" + mEntity.getSerial_no() +"]。";
			} else {
				sShowSnout = "此维修品的 C 本体正在进行 D/E 组装，序列号为：[" + mEntity.getSerial_no() +"]。";
			}
		}
		callbackResponse.put("show_snout", sShowSnout);
	}

	public void setToOrigin(String originMaterialId, SqlSessionManager conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		MaterialEntity origin = mapper.checkSnoutOrigin(originMaterialId, null);
		// 根据序列号查询先端头源
		String rootOriginId = originMaterialId;

		MaterialService ms = new MaterialService();

		if (origin == null) {

			MaterialEntity mEntity = ms.getMaterialEntityByKey(originMaterialId, conn);

			SnoutEntity hist = checkHistory(mEntity.getModel_id(), mEntity.getSerial_no(), mapper);

			if (hist != null) {
				rootOriginId = hist.getMaterial_id();
			}

			mapper.registSnoutOrigin(originMaterialId, rootOriginId, null); // 未曾使用的先端头

		}
	}

	/**
	 * 检查先端头使用历史，获取来源信息
	 * 
	 * @param model_id
	 * @param serial_no
	 * @param mapper
	 * @return
	 */
	public SnoutEntity checkHistory(String model_id, String serial_no, SoloProductionFeatureMapper mapper) {
		// 查询其来源
		List<MaterialEntity> historyList = mapper.getSnoutUseHistoryBySerial(model_id, serial_no, null);
		// 最后一次使用的先端头序列号
		String snoutManageCode = null;
		for (int i = historyList.size() - 1; i >= 0; i--) {
			MaterialEntity history = historyList.get(i);
			if (history.getProcessing_position2() != null) {
				snoutManageCode = history.getProcessing_position2();
				for (int j = i; j < historyList.size(); j++) {
					MaterialEntity nexts = historyList.get(j);
					if (nexts.getProcessing_position() != null) {
						// 最后一次使用后，又拆除了
						snoutManageCode = null;
						break;
					}
				}
				break;
			}
		}
		if (snoutManageCode != null) { 
			SnoutEntity condition = new SnoutEntity();
			condition.setSerial_no(snoutManageCode);
			List<SnoutEntity> hist = mapper.searchSnouts(condition);
			if (hist != null && hist.size() > 0) {
				return hist.get(0);
			}
		}
		return null;
	}

	/**
	 * 废弃先端来源
	 * @param org_material_id
	 * @param conn
	 * @throws Exception 
	 */
	public void abandon(String org_material_id, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);

		MaterialEntity soBean = mapper.checkSnoutOrigin(org_material_id, null);
		if (soBean == null) {
			setToOrigin(org_material_id, conn);
		}

		// 使用->0，为废弃
		mapper.abandonOrigin(org_material_id);

		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);
		List<String> positions = pfMapper.checkSpecPositionDid(org_material_id, "snout_eog", null, null, "null");
		for (String position_id : positions) {
			pfMapper.removeWaiting(org_material_id, position_id);
		}

	}

	public List<SnoutEntity> getUsableOriginByModel(String model_id, SqlSession conn){
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
	
		return mapper.getUsableOriginByModel(model_id);
	}
	
	public List<MaterialEntity> getTobeOriginByModel(String model_id, SqlSession conn){
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		List<MaterialEntity> ret = mapper.getTobeOriginByModel(model_id);

		for (MaterialEntity mEntity : ret) {
			SnoutEntity so = checkHistory(model_id, mEntity.getSerial_no(), mapper);
			if (so != null) {
				mEntity.setIsHistory(so.getRefurbished());
			} else {
				mEntity.setIsHistory(0);
			}
		}
		return ret;
	}

	public void registSnoutOriginSerial(String material_id, String serial_no,
			SqlSessionManager conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		mapper.registSnoutOriginSerial(material_id, serial_no);
	}

	public List<ProductionFeatureEntity> use(String from_position_id, String from_process_code, String serial_no,
			ProductionFeatureEntity workingPf,
			LoginData user, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		// 标准作业时间
		Integer use_seconds = Integer.valueOf(RvsUtils.getZeroOverLine("_default", null, user, from_process_code)) * 60;

		String usage_material_id = workingPf.getMaterial_id();
		ProductionFeatureEntity pfBean = new ProductionFeatureEntity();
		pfBean.setSerial_no(serial_no);
		pfBean.setUse_seconds(use_seconds);
		pfBean.setMaterial_id(usage_material_id);
		pfBean.setRework(workingPf.getRework());
		pfBean.setSection_id(workingPf.getSection_id());
		pfBean.setPosition_id(workingPf.getPosition_id());

		dao.forbid(pfBean);

		pfBean.setPosition_id(from_position_id);
		dao.useto(pfBean);
		dao.use(pfBean);
		dao.leaderuseto(pfBean);
		dao.registSnoutUsageBySerialNo(serial_no, usage_material_id);

		return dao.findUsedSnoutsByMaterial(usage_material_id, from_position_id);
	}

	public void unuse(String from_position_id, 
			String serial_no, ProductionFeatureEntity workingPf,
			SqlSessionManager conn) throws Exception {

		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		// 取得当前进行中Rework
		int rework = workingPf.getRework();

		// 寻找维修对象已使用先端头
		List<ProductionFeatureEntity> used_snouts = dao.findUsedSnoutsByMaterial(workingPf.getMaterial_id(), from_position_id);
		for (ProductionFeatureEntity used_snout : used_snouts) {
			if (used_snout.getRework() == rework) {
				serial_no = used_snout.getSerial_no();
			}
		}

		dao.unuse(serial_no, from_position_id);
		dao.unuseto(workingPf.getMaterial_id(), "" + workingPf.getRework(), from_position_id);
		dao.registSnoutUsageBySerialNo(serial_no, null);

		ProductionFeatureEntity pfBean = new ProductionFeatureEntity();
		pfBean.setSerial_no(serial_no);
		dao.leaderuseto(pfBean);
		
	}

	public List<MaterialForm> getSnoutHeadHistory(String material_id, SqlSession conn) throws Exception {

		List<MaterialForm> ret = new ArrayList<MaterialForm>();

		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);

		List<MaterialEntity> retEntity = dao.getSnoutHeadHistory(material_id);
		BeanUtil.copyToFormList(retEntity, ret, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);

		return ret; 
	}

	public boolean checkUsedForOrigin(String material_id, SqlSession conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);

		MaterialEntity soBean = mapper.checkSnoutOrigin(material_id, null);
		if (soBean == null) {
			return false;
		}
		return !"00000000000".equals(soBean.getCustomer_id());
	}

	public void getRecoverPcs(String org_material_id,
			Map<String, Object> callbackResponse, SqlSession conn) {
		MaterialService mService = new MaterialService();
		MaterialEntity mOriginEntity = mService.loadSimpleMaterialDetailEntity(conn, org_material_id);

		// 取得检查票
		PositionPanelService ppService = new PositionPanelService();
		String[] showLines = new String[1];
		showLines[0] = "NS 工程";

		for (String showLine : showLines) {
			Map<String, String> fileTempl = PcsUtils.getXmlContents(showLine, mOriginEntity.getModel_name(), null, conn);

			Map<String, String> fileTemplSolo = new HashMap<String, String>();
			for (String key : fileTempl.keySet()) {
				if (key.contains("回收")) {
					fileTemplSolo.put(key, fileTempl.get(key));
					break;
				}
			}

			List<Map<String, String>> pcses = new ArrayList<Map<String, String>>();
			Map<String, String> pcsRecover = ppService.getRecoverFileBlankHtml(fileTemplSolo, mOriginEntity, conn);
			pcses.add(pcsRecover);
			callbackResponse.put("pcses", pcses);
			break;
		}		
	}

	/**
	 * 取得来源的回收记录
	 * 
	 * @param fileTempl
	 * @param material_id
	 * @param mform
	 * @param conn
	 * @return
	 */
	public Map<String, String> getRecoverFileHtml(
			Map<String, String> fileTempl, String material_id,
			MaterialForm mformUsage, SqlSession conn) {
		if (!ComponentSettingService.getSnoutCompModels(conn).containsKey(mformUsage.getModel_id())) return null;

		List<String> snouts = new ArrayList<String>();
		Map<String, String> snoutHeads = new HashMap<String, String>();

		for (String key : fileTempl.keySet()) {
			if (key.contains("先端预制") || key.contains("D／E组装")) {
				snouts.add(key);
			}
			else if (key.contains("回收")) {
				snoutHeads.put(key, fileTempl.get(key));
			}
		}

		// 如果有先端头回收工程检查票
		if (snouts.size() == 0 || snoutHeads.size() == 0) {
			return null;
		}

		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);

		// 经历过先端预制的话，显示其来源进行的先端头回收
		if (pfMapper.checkPositionDid(material_id, "00000000024", null, null)) {
			SoloProductionFeatureMapper spfMapper = conn.getMapper(SoloProductionFeatureMapper.class);
			List<String> originMaterialIds = spfMapper.traceOrigin(material_id);
			if (originMaterialIds == null || originMaterialIds.size() == 0) return null;
			MaterialService mService = new MaterialService();

			Map<String, String> fileHtml = new HashMap<String, String>();

			int iSize = originMaterialIds.size() - 1;
			for (int i = 0; i <= iSize; i++) {
				String originMaterialId = originMaterialIds.get(i);
				MaterialEntity mOriginEntity = mService.loadSimpleMaterialDetailEntity(conn, originMaterialId);

				Map<String, String> tfileHtml = PcsUtils.toHtml(snoutHeads, originMaterialId, mOriginEntity.getSorc_no(),
						mOriginEntity.getModel_name(), mOriginEntity.getSerial_no(), "" + mOriginEntity.getLevel(), "399", null, 
								false, conn);

				if (i == iSize) {
					fileHtml.putAll(tfileHtml);
				} else {
					for (String key : tfileHtml.keySet()) {
						fileHtml.put(key + " 第" + (i+1) + "次返工", tfileHtml.get(key));
					}
				}
			}

			return fileHtml;
		}

		return null;
	}

	public void getRecoverFilePdf(Map<String, String> fileTempl,
			MaterialForm mformUsage, String folderPath, SqlSession conn) throws IOException {
		if (!ComponentSettingService.getSnoutCompModels(conn).containsKey(mformUsage.getModel_id())) return;

		List<String> snouts = new ArrayList<String>();
		Map<String, String> snoutHeads = new HashMap<String, String>();

		for (String key : fileTempl.keySet()) {
			if (key.contains("先端预制") || key.contains("D／E组装")) {
				snouts.add(key);
			}
			else if (key.contains("回收")) {
				snoutHeads.put(key, fileTempl.get(key));
			}
		}

		// 如果有先端头回收工程检查票
		if (snouts.size() == 0 || snoutHeads.size() == 0) {
			return;
		}

		String material_id = mformUsage.getMaterial_id();
		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);

		// 经历过先端预制的话，显示其来源进行的先端头回收
		if (pfMapper.checkPositionDid(material_id, "00000000024", null, null)) {
			SoloProductionFeatureMapper spfMapper = conn.getMapper(SoloProductionFeatureMapper.class);
			List<String> originMaterialIds = spfMapper.traceOrigin(material_id);
			if (originMaterialIds == null || originMaterialIds.size() == 0) return;

			MaterialService mService = new MaterialService();

			int iSize = originMaterialIds.size() - 1;
			for (int i = 0; i <= iSize; i++) {
				String originMaterialId = originMaterialIds.get(i);
				MaterialEntity mOriginEntity = mService.loadSimpleMaterialDetailEntity(conn, originMaterialId);
				PcsUtils.toPdf(snoutHeads, originMaterialId, mOriginEntity.getSorc_no(),
						mOriginEntity.getModel_name(), mOriginEntity.getSerial_no(), "" + mOriginEntity.getLevel(), null, folderPath, 
								false, conn);
				if (i < iSize) {
					for (String snoutHead :snoutHeads.keySet()) {
						File pdfFile = new File(folderPath + "\\" + snoutHead + ".pdf");
						if (pdfFile.exists()) {
							pdfFile.renameTo(new File(folderPath + "\\" + snoutHead + " 第" + (i + 1) + "次返工.pdf"));
						}
					}
				}
			}
		}
	}

	/**
	 * 取得型号已入库
	 * 
	 * @param model_id
	 * @param conn
	 * @return
	 */
	public List<String> getSlotsFromSnoutComponentStorageByModel(String model_id, SqlSession conn) {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		return dao.getSlotsFromSnoutComponentStorageByModel(model_id);
	}

	public void setSnoutComponentStorage(String manage_serial_no, String slot, String model_id, SqlSession conn) {
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		dao.unsetSnoutComponentStorageBySerialNo(manage_serial_no);
		dao.unsetSnoutComponentStorageBySlot(slot, model_id);
		dao.setSnoutComponentStorage(manage_serial_no, slot);
	}

}

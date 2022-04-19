package com.osh.rvs.service.inline;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.AlarmMesssageEntity;
import com.osh.rvs.bean.data.AlarmMesssageSendationEntity;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.LineLeaderEntity;
import com.osh.rvs.bean.inline.MaterialProcessAssignEntity;
import com.osh.rvs.bean.inline.PauseFeatureEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.AlarmMesssageForm;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.inline.LineLeaderForm;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.inline.LineLeaderMapper;
import com.osh.rvs.mapper.inline.MaterialProcessAssignMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.master.PositionMapper;
import com.osh.rvs.service.AlarmMesssageService;
import com.osh.rvs.service.CustomerService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.ProcessAssignService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
// import java.util.HashMap;

public class LineLeaderService {
	Logger _log = Logger.getLogger(LineLeaderService.class);

	/**
	 * 取得当前课室+工程下处理中的全部维修对象信息
	 * @param section_id
	 * @param line_id
	 * @param checkGroup 
	 * @param today 
	 * @param conn
	 * @return
	 */
	public List<LineLeaderForm> getPerformanceList(String section_id, String line_id, String position_id, String checkGroup, String today, SqlSession conn) {
		List<LineLeaderForm> ret = new ArrayList<LineLeaderForm>();

		LineLeaderMapper dao = conn.getMapper(LineLeaderMapper.class);
		if ("".equals(position_id)) position_id = null;
		List<LineLeaderEntity> listEntities = dao.getWorkingMaterials(section_id, line_id, position_id, checkGroup, today);

		CopyOptions cos = new CopyOptions();
		cos.excludeEmptyString();
		cos.excludeNull();
		if (!"00000000013".equals(line_id)) { // 仅NS 工程判断
			cos.exclude("ns_partial_order");
		}
		AlarmMesssageMapper amDao = conn.getMapper(AlarmMesssageMapper.class);

		for (LineLeaderEntity entity : listEntities) {
			LineLeaderForm retForm = new LineLeaderForm();
			BeanUtil.copyToForm(entity, retForm, cos);
			if (entity.getOperate_result() == 3) {
				String amLevel = amDao.getBreakLevelByMaterialId(entity.getMaterial_id(), entity.getPosition_id());
				retForm.setSymbol(CodeListUtils.getValue("alarm_symbol", amLevel));
			}
			ret.add(retForm);
		}

		return ret;
	}

	/**
	 * 工位仕挂一览For图表
	 * @param user
	 * @param conn
	 * @param listResponse
	 */
	public void getChartContent(LoginData user, SqlSession conn, Map<String, Object> listResponse) {
		String section_id = user.getSection_id();
		String line_id = user.getLine_id();

		LineLeaderMapper dao = conn.getMapper(LineLeaderMapper.class);
		List<Map<String, String>> workingOfPositions = dao.getWorkingOfPositions(section_id, line_id);

		// 数据整合
		List<Map<String, String>> newWorkingOfPositions = new ArrayList<Map<String, String>>();
		for (int i=0; i < workingOfPositions.size(); i+=3){
			Map<String, String> workingOfPositionH = workingOfPositions.get(i);
			Map<String, String> workingOfPositionL = workingOfPositions.get(i+1);
			Map<String, String> workingOfPositionF = workingOfPositions.get(i+2);
			String light_division_flg = workingOfPositionH.get("LIGHT_DIVISION_FLG");
			String process_code = workingOfPositionH.get("PROCESS_CODE");

			boolean depar = false;
			boolean existsB = true;
			boolean existsC = (process_code.equals("511") || process_code.equals("521") || process_code.equals("531") || process_code.equals("541")); // TODO
			boolean existsA = !existsC; // TODO

			if ("1".equals(light_division_flg)) { // 分线
				depar = true;
				if ("0".equals(workingOfPositionL.get("material_count"))
						&& "0".equals(workingOfPositionL.get("light_fix_count"))) { // B线无仕挂
					existsB = false;
				}
			}

			if (depar) { // 分线
				if (existsA) {
					workingOfPositionH.put("PROCESS_CODE", process_code + "A");
					newWorkingOfPositions.add(workingOfPositionH);
				}

				if (existsB) {
					workingOfPositionL.put("PROCESS_CODE", process_code + "B");
					newWorkingOfPositions.add(workingOfPositionL);
				}

				if (existsC) {
					workingOfPositionF.put("PROCESS_CODE", process_code + "C");
					newWorkingOfPositions.add(workingOfPositionF);
				}
			} else { // 不分
				Float fCount = 0f;
				Float light_fix_count = 0f;
				try {
					fCount = Float.parseFloat(workingOfPositionH.get("material_count"))
							+ Float.parseFloat(workingOfPositionL.get("material_count"))
							+ Float.parseFloat(workingOfPositionF.get("material_count"));
					light_fix_count = Float.parseFloat(workingOfPositionH.get("light_fix_count"))
							+ Float.parseFloat(workingOfPositionL.get("light_fix_count"))
							+ Float.parseFloat(workingOfPositionF.get("light_fix_count"));
				} catch (NumberFormatException e) {
				}
				workingOfPositionH.put("material_count", String.valueOf(fCount));
				workingOfPositionH.put("light_fix_count", String.valueOf(light_fix_count));
				newWorkingOfPositions.add(workingOfPositionH);
			}
		}

		// 取得超时的工位
		List<String> position_list = dao.getOverTimePosition(section_id, line_id);

		List<String> positions = new ArrayList<String>();
		List<Object> counts = new ArrayList<Object>();
		List<Object> light_fix_counts = new ArrayList<Object>();

		for (Map<String, String> workingOfPosition : newWorkingOfPositions){
			String position_id = workingOfPosition.get("POSITION_ID");
			String process_code = workingOfPosition.get("PROCESS_CODE");
			boolean pxB = process_code.endsWith("B");

			if (process_code.endsWith("A") || process_code.endsWith("B") || process_code.endsWith("C")) {
				process_code = process_code.substring(0, process_code.length() - 1);
			}
			String tempStr = "<a href=\"javaScript:positionFilter('"+position_id+"')\"";
			if (position_list.contains(position_id) && !pxB) {
				tempStr = tempStr + " style=\"fill:orange;\">";
			} else {
				tempStr = tempStr + ">";
			}
			tempStr = tempStr + workingOfPosition.get("PROCESS_CODE") + " " + workingOfPosition.get("NAME");
			if ("400".equals(process_code)) {
				tempStr = tempStr + "\n(x 10)";
			}
			tempStr = tempStr + "</a>";
			positions.add(tempStr);

			// 大修理数据
			Float fCount = 0f;
			try {
				fCount = Float.parseFloat(workingOfPosition.get("material_count"));
				if ("400".equals(process_code)) {
					fCount /= 10;
				}
			} catch (NumberFormatException e) {
			}

			// 小修理数据
			Float light_fix_count = 0f;
			try {
				light_fix_count = Float.parseFloat(workingOfPosition.get("light_fix_count"));
				if ("400".equals(process_code)) {
					light_fix_count /= 10;
				}
			} catch (NumberFormatException e) {
			}

			counts.add(fCount);

			if (light_fix_count == 0) {
				light_fix_counts.add(null);
			} else {
				light_fix_counts.add(light_fix_count);
			}
		}

		// 组工位
		if ("00000000001".equals(section_id)) { // 1课按组分，二课单工位
			List<Map<String, Object>> groupedPositions = dao.getGroupedPositions(section_id, line_id);
			if (groupedPositions != null && groupedPositions.size() > 0) {
				String cursor = null;
				String categoryP = null; boolean orange = false;
				Set<String> countP = new HashSet<String>(), light_fix_countP = new HashSet<String>();
				Set<String> countF = new HashSet<String>(), light_fix_countF = new HashSet<String>();

				for (Map<String, Object> groupedPosition : groupedPositions) {
					String group_position_id = getAsString(groupedPosition.get("group_position_id"));
					if (!PositionService.isGroupPosition(group_position_id, section_id, conn)) continue;
					if (!group_position_id.equals(cursor)) {
						if (cursor != null) {
							if (orange) {
								categoryP = categoryP.replaceAll("groupTag", "style=\"fill:orange;\"");
							}
							positions.add(categoryP.replace("</a>", " 进行</a>"));
							positions.add(categoryP.replace("</a>", " 完成</a>"));
							counts.add(countP.size());
							counts.add(countF.size());
							if (light_fix_countP.size() > 0) {
								light_fix_counts.add(light_fix_countP.size());
							} else {
								light_fix_counts.add(null);
							}
							if (light_fix_countF.size() > 0) {
								light_fix_counts.add(light_fix_countF.size());
							} else {
								light_fix_counts.add(null);
							}
						}

						cursor = group_position_id;
						categoryP = null; orange = false;
						countP = new HashSet<String>(); light_fix_countP = new HashSet<String>();
						countF = new HashSet<String>(); light_fix_countF = new HashSet<String>();
					}

					String sub_position_id = getAsString(groupedPosition.get("sub_position_id"));
//					String next_position_id = getAsString(groupedPosition.get("next_position_id"));
//					Integer control_trigger = getAsInteger(groupedPosition.get("control_trigger"));
					String process_code = getAsString(groupedPosition.get("process_code"));
					String name = getAsString(groupedPosition.get("name"));
					String working_material_id = getAsString(groupedPosition.get("working_material_id")); 
					Integer working_light_fix = getAsInteger(groupedPosition.get("working_light_fix"));
					String finish_material_id = getAsString(groupedPosition.get("finish_material_id"));
					Integer finish_light_fix = getAsInteger(groupedPosition.get("finish_light_fix"));

					if (categoryP == null) {
						categoryP = "<a href=\"javaScript:positionFilter('"+group_position_id+"', true)\" groupTag>";
						categoryP = categoryP + process_code + " " + name;
						categoryP = categoryP + "</a>";
					}
					if (position_list.contains(sub_position_id)) {
						orange = true;
					}
					if (working_material_id != null) {
						if (working_light_fix == 1) {
							light_fix_countP.add(working_material_id);
						} else {
							countP.add(working_material_id);
						}
					}
					if (finish_material_id != null) {
						if (finish_light_fix == 1) {
							light_fix_countF.add(finish_material_id);
						} else {
							countF.add(finish_material_id);
						}
					}
				}

				if (cursor != null) {
					if (orange) {
						categoryP = categoryP.replaceAll("groupTag", "style=\"fill:orange;\"");
					}
					positions.add(categoryP.replace("</a>", " 进行</a>"));
					positions.add(categoryP.replace("</a>", " 完成</a>"));
					counts.add(countP.size());
					counts.add(countF.size());
					if (light_fix_countP.size() > 0) {
						light_fix_counts.add(light_fix_countP.size());
					} else {
						light_fix_counts.add(null);
					}
					if (light_fix_countF.size() > 0) {
						light_fix_counts.add(light_fix_countF.size());
					} else {
						light_fix_counts.add(null);
					}
				}
			}
		}

		listResponse.put("categories", positions);
		listResponse.put("counts", counts);
		listResponse.put("light_fix_counts", light_fix_counts);
		return;
	}

	@SuppressWarnings({"unchecked" })
	public void putTempChartContent(Map<String, Object> listResponse,
			Map<String, Object> lineResponse, SqlSession conn) {
		List<String> lineCategories = (List<String>) lineResponse.get("categories");
		
	}

	private Integer getAsInteger(Object object) {
		if (object == null) return null;
		if (object instanceof Long) return ((Long) object).intValue();
		if (object instanceof Integer) return (Integer) object;
		return null;
	}

	private String getAsString(Object object) {
		if (object == null) return null;
		return object.toString();
	}

	/**
	 * 切换维修对象在本线加急
	 * @param material_id
	 * @param line_id
	 * @param conn
	 * @throws Exception 
	 */
	public void switchLeaderExpedite(String material_id, String line_id, SqlSessionManager conn) throws Exception {
		LineLeaderMapper dao = conn.getMapper(LineLeaderMapper.class);
		if ("00000000011".equals(line_id))
			dao.switchQuotationFirst(material_id);
		else
			dao.switchLeaderExpedite(material_id, line_id);
	}

	/**
	 * 取得中断信息
	 * @param material_id
	 * @param operator_id 本人
	 * @param position_id
	 * @param conn
	 * @return
	 */
	public AlarmMesssageForm getWarning(String material_id, String operator_id, String position_id, SqlSession conn) {
		// 取得对应工位的中断信息
		AlarmMesssageMapper dao = conn.getMapper(AlarmMesssageMapper.class);
		AlarmMesssageService amService = new AlarmMesssageService();

		AlarmMesssageEntity entity = amService.getBreakAlarmMessage(material_id, position_id, conn);

		AlarmMesssageForm form = new AlarmMesssageForm();
		CopyOptions co = new CopyOptions();

		co.dateConverter("MM-dd HH:mm", "occur_time");
		co.include("alarm_messsage_id", "occur_time", "sorc_no", "model_name", "serial_no", "line_name", "process_code", "operator_name");
		BeanUtil.copyToForm(entity, form, co);

		// 取得原因
		PauseFeatureEntity pauseEntity = dao.getBreakOperatorMessageByID(entity.getAlarm_messsage_id());
		if (pauseEntity == null) {
			pauseEntity = dao.getBreakOperatorMessage(entity.getOperator_id(), material_id, position_id);
		}

		// 取得暂停信息里的记录
		Integer iReason = pauseEntity.getReason();
		// 不良理由
		String sReason = null;
		if (iReason != null && iReason < 10) {
			sReason = CodeListUtils.getValue("break_reason", "0" + iReason);
		} else {
			sReason = PathConsts.POSITION_SETTINGS.getProperty("break."+ pauseEntity.getProcess_code() +"." + iReason);
		}
		form.setReason(sReason);

		// 备注信息
		String sComments = entity.getOperator_name()+ ":" + pauseEntity.getComments();
		String sMyComments = "";

		List<AlarmMesssageSendationEntity> listSendation = dao.getBreakAlarmMessageSendation(entity.getAlarm_messsage_id());
		for (AlarmMesssageSendationEntity sendation : listSendation) {
			if (!CommonStringUtil.isEmpty(sendation.getComment())) {
				if (operator_id.equals(sendation.getSendation_id())) {
					sMyComments = sendation.getComment();
				} else {
					sComments += "\n" + sendation.getSendation_name() + ":" + sendation.getComment();
				}
			}
		}
		form.setComment(sComments);
		form.setMyComment(sMyComments);

		return form;
	}

	/**
	 * 线长完成零件相关工位
	 */
	public ProductionFeatureEntity partialResolve(String material_id, String model_name, String section_id, String position_id, 
			SqlSessionManager conn, LoginData user) throws Exception {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

		// 取得标准工作时间
		int use_seconds = 0;
		PositionMapper pDao = conn.getMapper(PositionMapper.class);
		PositionEntity position = pDao.getPositionByID(position_id);

		String sUse_seconds = RvsUtils.getZeroOverLine(model_name, null, user, position.getProcess_code());
		try {
			use_seconds = Integer.parseInt(sUse_seconds) * 60;
		} catch (Exception e) {
		}

		// 设定条件
		ProductionFeatureEntity entity = new ProductionFeatureEntity();
		entity.setOperator_id(user.getOperator_id());
		entity.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
		entity.setUse_seconds(use_seconds);
		entity.setMaterial_id(material_id);
		entity.setPosition_id(position_id);
		if (section_id != null) {
			entity.setSection_id(section_id);
		} else {
			entity.setSection_id(user.getSection_id());
		}
		entity.setPcs_inputs("{\"EN"+position.getProcess_code()+"01\":\"1\"}");

		// 更新为完成
		dao.finishPatchProductionFeature(entity);

		return entity;
	}

	/**
	 * 取得受理报价工程处理中的全部维修对象信息
	 * @param section_id
	 * @param line_id
	 * @param conn
	 * @return
	 */
	public List<MaterialForm> getBeforePerformanceList(MaterialForm form, SqlSession conn) {
		List<MaterialForm> ret = new ArrayList<MaterialForm>();
		MaterialEntity bean = new MaterialEntity();
		BeanUtil.copyToBean(form, bean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LineLeaderMapper dao = conn.getMapper(LineLeaderMapper.class);
		List<MaterialEntity> listEntities = dao.getBeforePerformanceList(bean);

		Set<String> ccdLineModels = RvsUtils.getCcdLineModels(conn);

		Set<String> anmlMaterials = MaterialTagService.getAnmlMaterials(conn);

		ProcessAssignService paService = new ProcessAssignService();
		List<String> anmlProcesses = ProcessAssignService.getAnmlProcesses(conn);
		Map<String, String> anmlProcessMap = new HashMap<String, String>();
		Map<String, String> add4DaysMapper = new HashMap<String, String>();

		for (MaterialEntity entity : listEntities) {
			MaterialForm retForm = new MaterialForm();

			BeanUtil.copyToForm(entity, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			if (entity.getScheduled_expedited() != null && entity.getScheduled_expedited() >= 4) {
				String receptionDate = retForm.getReception_time().substring(0, 10);
				if (!add4DaysMapper.containsKey(receptionDate)) {
					Date outlineDate = RvsUtils.switchWorkDate(entity.getReception_time(), 4, conn);
					add4DaysMapper.put(receptionDate, 
							DateUtil.toString(outlineDate, DateUtil.DATE_PATTERN));
				}
				retForm.setOutline_time(add4DaysMapper.get(receptionDate));
			}

			if (!ccdLineModels.contains(entity.getModel_id())) { // 非304作业对象
				retForm.setPat_id(null);
			}
			// 动物实验
			if (anmlMaterials.contains(entity.getMaterial_id())) {
				retForm.setAnml_exp("1");

				String pat_id = retForm.getPat_id();
				if (pat_id != null && anmlProcesses.size() > 0) {
					if (!anmlProcesses.contains(pat_id)) {
						retForm.setPat_id(anmlProcesses.get(0));
						if (!anmlProcessMap.containsKey(anmlProcesses.get(0))) {
							String patName = paService.getDetail(anmlProcesses.get(0), conn).getName();
							anmlProcessMap.put(anmlProcesses.get(0), patName);
						}
						retForm.setSection_name(anmlProcessMap.get(anmlProcesses.get(0)));
					}
				}
			}
			
			ret.add(retForm);
		}

		return ret;
	}

	/**
	 * 更新维修对象信息（报价线长）
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 * @throws Exception 
	 */
	public void update(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		MaterialEntity entity = new MaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		if (entity.getCustomer_name() != null) {
			CustomerService cservice = new CustomerService();
			entity.setCustomer_id(cservice.getCustomerStudiedId(entity.getCustomer_name(), entity.getOcm(), conn));
		}
		dao.updateMaterial(entity);

		MaterialForm materialForm = (MaterialForm)form;
		
		String level = materialForm.getLevel();//等级
		String fix_type = materialForm.getFix_type();//修理方式
		//小修理流水线
//		boolean isLightFix = (level != null) && 
//				("9".equals(level) || "91".equals(level) || "92".equals(level) || "93".equals(level)) && "1".equals(fix_type); 
		boolean isLightFix = RvsUtils.isLightFix(level) && "1".equals(fix_type);
		if(!isLightFix){
			MaterialProcessAssignEntity materialProcessAssignEntity = new MaterialProcessAssignEntity();
			BeanUtil.copyToBean(form, materialProcessAssignEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
			MaterialProcessAssignMapper materialProcessAssignMapper = conn.getMapper(MaterialProcessAssignMapper.class);

			String lightFixes = materialProcessAssignMapper.getLightFixesByMaterial(entity.getMaterial_id());
			if (!isEmpty(lightFixes)) {
				//删除维修对象选用小修理
				materialProcessAssignMapper.deleteMaterialLightFix(materialProcessAssignEntity.getMaterial_id());
				//删除维修对象独有修理流程
				materialProcessAssignMapper.deleteMaterialProcessAssign(materialProcessAssignEntity.getMaterial_id());

				MaterialService mService = new MaterialService();
				// 删除小修理流程说明
				mService.removeComment(entity.getMaterial_id(), "00000000001", conn);
			}
		}

		// 标签更新
		if (entity.getAnml_exp() != null) {
			MaterialTagService mtService = new MaterialTagService();
			mtService.updataTagByMaterialId(entity.getMaterial_id(), MaterialTagService.TAG_ANIMAL_EXPR, entity.getAnml_exp() == 1, conn);
		}

		// FSE 数据同步
		try{
			FseBridgeUtil.toUpdateMaterial(entity.getMaterial_id(), "ll_update");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上报中断信息
	 * @param req
	 * @param user
	 * @param conn
	 * @throws Exception
	 */
	public void hold(HttpServletRequest req, LoginData user, SqlSessionManager conn) throws Exception {
		String section_id = user.getSection_id();

		String alarm_messsage_id = req.getParameter("alarm_messsage_id");

		AlarmMesssageMapper dao = conn.getMapper(AlarmMesssageMapper.class);
		AlarmMesssageEntity alarm_messsage = new AlarmMesssageEntity();
		alarm_messsage.setAlarm_messsage_id(alarm_messsage_id);
		alarm_messsage.setLevel(2);
		// 警报等级升级
		dao.updateLevel(alarm_messsage);

		AlarmMesssageSendationEntity sendation = new AlarmMesssageSendationEntity();
		sendation.setAlarm_messsage_id(alarm_messsage_id);
		sendation.setSendation_id(user.getOperator_id());
		sendation.setComment(req.getParameter("comment"));
		sendation.setRed_flg(0);
		sendation.setResolve_time(new Date());

		// 留下本人备注
		int me = dao.countAlarmMessageSendation(sendation);
		if (me <= 0) {
			// 没有发给处理者的信息时（代理线长），新建一条
			dao.createAlarmMessageSendation(sendation);
		} else {
			dao.updateAlarmMessageSendation(sendation);
		}

		// 取得本课室经理人员
		OperatorMapper oDao = conn.getMapper(OperatorMapper.class);
		OperatorEntity oCondition = new OperatorEntity();
		oCondition.setSection_id(section_id);
		oCondition.setRole_id(RvsConsts.ROLE_MANAGER);

		// 发送警报到经理
		List<OperatorNamedEntity> managers = oDao.searchOperator(oCondition);
		for (OperatorNamedEntity manager : managers) {
			sendation = new AlarmMesssageSendationEntity();
			sendation.setAlarm_messsage_id(alarm_messsage_id);
			sendation.setSendation_id(manager.getOperator_id());
			me = dao.countAlarmMessageSendation(sendation);

			if (me == 0) {
				// 如果不存在则Insert
				dao.createAlarmMessageSendation(sendation);
			}
		}
	}


}

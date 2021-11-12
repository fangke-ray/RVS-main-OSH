package com.osh.rvs.service.inline;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.AlarmMesssageEntity;
import com.osh.rvs.bean.data.AlarmMesssageSendationEntity;
import com.osh.rvs.bean.inline.DailyKpiDataEntity;
import com.osh.rvs.bean.inline.MaterialProcessEntity;
import com.osh.rvs.bean.inline.PauseFeatureEntity;
import com.osh.rvs.bean.inline.ScheduleEntity;
import com.osh.rvs.bean.inline.ScheduleHistoryEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.master.SectionEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.AlarmMesssageForm;
import com.osh.rvs.form.inline.ScheduleForm;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.mapper.inline.DailyKpiMapper;
import com.osh.rvs.mapper.inline.RepairPlanMapper;
import com.osh.rvs.mapper.inline.ScheduleHistoryMapper;
import com.osh.rvs.mapper.inline.ScheduleMapper;
import com.osh.rvs.mapper.master.HolidayMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.service.AlarmMesssageService;
import com.osh.rvs.service.MaterialProcessService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.Converter;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateConverter;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.copy.IntegerConverter;

public class ScheduleService {


	private static Map<String, String> delayMap = new HashMap<String, String>();
	private static String delayMapOfDate = null;

	private synchronized static String getDelayWithDelayMap(String scheduled_date, String am_pm, SqlSession conn) {
		String todayString = DateUtil.toString(new Date(), DateUtil.ISO_DATE_PATTERN);
		if (!todayString.equals(delayMapOfDate)) {
			delayMapOfDate = todayString;
			delayMap.clear();
		}

		String key = scheduled_date + " " + am_pm; 
		if (delayMap.containsKey(key)) {
			return delayMap.get(key);
		} else {
			HolidayMapper hdao = conn.getMapper(HolidayMapper.class);

			Map<String, Object> condiMap = new HashMap<String, Object>();
			condiMap.put("scheduled_date", scheduled_date);
			condiMap.put("am_pm", am_pm);
			String remain_days = hdao.compareExperial(condiMap);
			delayMap.put(key, remain_days);
			return delayMap.get(key);
		}
	}

	/**
	 * 在线维修对象检索一览
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<ScheduleForm> getMaterialList(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		ScheduleMapper dao = conn.getMapper(ScheduleMapper.class);
		ScheduleEntity conditionBean = new ScheduleEntity();
		CopyOptions cos = new CopyOptions();
		cos.excludeEmptyString();
		cos.excludeNull();
		cos.exclude("level");

		BeanUtil.copyToBean(form, conditionBean, cos);

		// 复选机种
		if (conditionBean.getCategory_id() != null && conditionBean.getCategory_id().indexOf(",") >= 0) {
			conditionBean.setCategory_ids(conditionBean.getCategory_id().split(","));
			conditionBean.setCategory_id(null);
		}
		List<String> ids = dao.searchMaterialIdsByCondition(conditionBean);
		List<ScheduleForm> retForms = new ArrayList<ScheduleForm>();

		if (ids != null && ids.size() > 0) {
			List<ScheduleEntity> entities = dao.searchMaterialByIds(ids);

			// 按今天为零件到达后4天日期的推算
			Date now = new Date();
			String sToday = DateUtil.toString(now, DateUtil.DATE_PATTERN);
			String sPartialArrivalLine = DateUtil.toString(RvsUtils.switchWorkDate(now, -4, conn), DateUtil.DATE_PATTERN);

			for (ScheduleEntity entity : entities) {
				ScheduleForm retForm = new ScheduleForm();
				BeanUtil.copyToForm(entity, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);

				if ("00000000001".equals(entity.getSection_id())) {
					// 平行分线
//					Set<String> dividePositions = PositionService.getDividePositions(conn);

					String processing_position = entity.getProcessing_position();
					Integer px = entity.getPx();
					// (coalesce(mpn.px, 0) * 10 + mpc.px)
					if (processing_position.startsWith("1")) {
						retForm.setPx("");
					} else if (processing_position.startsWith("2")) {
						if (entity.getLevel() > 3)
							retForm.setPx("B");
						else
							retForm.setPx("A");
					} else {
						if (processing_position.startsWith("4") || processing_position.startsWith("5")) {
							px = px % 10;
						} else {
							px = px / 10;
						}

						entity.setPosition_id(ReverseResolution.getPositionByProcessCode(entity.getProcessing_position(), conn));

//						if (dividePositions.contains(entity.getPosition_id()) 
//								|| (dividePositions.contains(entity.getPosition_id2()) && entity.getNs_finish_date() == null)) {
							if (px == 1) {
//								if (processing_position.startsWith("4") || processing_position.startsWith("5")) {
//									retForm.setPx("B1");
//								} else {
								retForm.setPx("B");
//								}
							}
							else if (px == 4)
								retForm.setPx("B1");
							else if (px == 7)
								retForm.setPx("B2");
							else if (px == 2)
								retForm.setPx("C");
							else
								retForm.setPx("A");
//						} else {
//							retForm.setPx("");
//						}
					}
					if (entity.getNs_processing_position() != null && entity.getNs_processing_position().startsWith("3") && entity.getNs_finish_date() == null) {
						px = entity.getPx() / 10;
						entity.setPosition_id2(ReverseResolution.getPositionByProcessCode(entity.getNs_processing_position(), conn));
						
//						if (dividePositions.contains(entity.getPosition_id2())) {
							if (px == 1 || px == 3)
								retForm.setPx_ns("B");
							else if (px == 2)
								retForm.setPx_ns("C");
							else
								retForm.setPx_ns("A");
//						} else {
//							retForm.setPx_ns("");
//						}
					}
				} else {
					retForm.setPx("");
				}

				// 如果有警告信息则读取警告信息
				if ("1".equals(retForm.getBreak_message())) {
					//retForm
					String material_id = retForm.getMaterial_id();
					AlarmMesssageService service = new AlarmMesssageService();
					List<AlarmMesssageEntity> messages = service.getUnredAlarmMessagesByMaterial(material_id, conn);

					List<String> spareBreakMessages = new ArrayList<String>();
					AlarmMesssageMapper amDao = conn.getMapper(AlarmMesssageMapper.class);
					boolean levelup = false;
					for (AlarmMesssageEntity message : messages) {
						// 要求当前等级处理
						if (3 == message.getLevel()) {
							levelup = true;
						}

						// 取得原因
						PauseFeatureEntity pauseEntity = amDao.getBreakOperatorMessageByID(message.getAlarm_messsage_id());
						if (pauseEntity != null) {
							// 取得暂停信息里的记录
							Integer iReason = pauseEntity.getReason();
							// 不良理由
							String sReason = null;
							if (iReason != null && iReason < 10) {
								sReason = CodeListUtils.getValue("break_reason", "0" + iReason);
								if ("其他".equals(sReason)) {
									sReason = pauseEntity.getComments();
								}
							} else {
								sReason = PathConsts.POSITION_SETTINGS.getProperty("break."+ pauseEntity.getProcess_code() +"." + iReason);
							}
							spareBreakMessages.add(pauseEntity.getProcess_code() + ":" + sReason + " ");
						}
					}

					retForm.setBreak_message(CommonStringUtil.joinBy("\n", spareBreakMessages.toArray(new String[spareBreakMessages.size()])));
					if (levelup) retForm.setBreak_message_level("ME");
				} else {
					retForm.setBreak_message("");
				}

				// 如果已经经过总组接收 , 取得标准工时倒计时
//				if (pfdao.checkPositionDid(entity.getMaterial_id(), "00000000032", "2")) {
//					List<String> process_codes = padao.getNonfinishedPositions(entity.getMaterial_id());
//					int remain = 0;
//					for (String process_code : process_codes) {
//						// 未做各工时
//						try {
//							int overline = Integer.parseInt(RvsUtils.getLevelOverLine(entity.getModel_name(),
//									entity.getCategory_name(), retForm.getLevel(), null, process_code));
//							remain += overline;
//						} catch (Exception e) {
//							// log
//						}
//					}
//					// 剩余
//					retForm.setCountdown(new BigDecimal(remain).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).toString() + "小时");
//				}

//				if (entity.getRemain_minutes() != null) {
//					int remain_minutes = entity.getRemain_minutes();
//					if (remain_minutes > 420) {
//						int mhour = remain_minutes % 420 / 60;
//						if (mhour == 0) {
//							retForm.setCountdown((remain_minutes / 420) + "天");
//						} else {
//							retForm.setCountdown((remain_minutes / 420) + "天" + mhour + "小时");
//						}
//					} else {
//						retForm.setCountdown(new BigDecimal(remain_minutes).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).toString() + "小时");
//					}
//				}

				// 如果零件到货日或者纳期已经超过，标注
				String sCom_plan_date = retForm.getCom_plan_date();
				String sArrival_plan_date = retForm.getArrival_plan_date();
				if (!isEmpty(sCom_plan_date) && sCom_plan_date.compareTo(sToday) < 0 ) {
					retForm.setIs_late("纳期");
				} else if (!isEmpty(sArrival_plan_date) && sArrival_plan_date.compareTo(sPartialArrivalLine) < 0) {
					retForm.setIs_late("零件");
				}

				// 过期颜色
				if (retForm.getScheduled_date_end() != null) {
					retForm.setRemain_days(
							getDelayWithDelayMap(retForm.getScheduled_date_end(), retForm.getAm_pm(), conn));
				}

				// 行顺序
				for (int iId = 0; iId < ids.size(); iId++) {
					if (ids.get(iId).equals(retForm.getMaterial_id())) {
						retForm.setRow_no("" + iId);
						break;
					}
				}
				retForms.add(retForm);
			}
		}
		return retForms;
	}

	public List<ScheduleForm> getScheduleList(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		ScheduleMapper dao = conn.getMapper(ScheduleMapper.class);
		ScheduleEntity conditionBean = new ScheduleEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		List<ScheduleForm> retForms = new ArrayList<ScheduleForm>();

		if (conditionBean.getScheduled_assign_date() == null) {
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			tomorrow.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow.set(Calendar.MINUTE, 0);
			tomorrow.set(Calendar.SECOND, 0);
			tomorrow.set(Calendar.MILLISECOND, 0);
			conditionBean.setScheduled_assign_date(tomorrow.getTime());
		}

		List<ScheduleEntity> entities = dao.searchScheduleByCondition(conditionBean);
		for (ScheduleEntity entity : entities) {
			ScheduleForm retForm = new ScheduleForm();
			BeanUtil.copyToForm(entity, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			
			// 过期颜色
			if (retForm.getScheduled_date_end() != null) {
				retForm.setRemain_days(
						getDelayWithDelayMap(retForm.getScheduled_date_end(), retForm.getAm_pm(), conn));
			}

//			if (entity.getRemain_minutes() != null) {
//				int remain_minutes = entity.getRemain_minutes();
//				if (remain_minutes > 420) {
//					int mhour = remain_minutes % 420 / 60;
//					if (mhour == 0) {
//						retForm.setCountdown((remain_minutes / 420) + "天");
//					} else {
//						retForm.setCountdown((remain_minutes / 420) + "天" + mhour + "小时");
//					}
//				} else {
//					retForm.setCountdown(new BigDecimal(remain_minutes).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).toString() + "小时");
//				}
//			}

			retForms.add(retForm);
		}
		return retForms;
	}

	public void updateSchedule(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {

		ScheduleForm sForm = (ScheduleForm)form;
		String ids = sForm.getIds();
		String lineId = sForm.getLineIds();//单一值
		String[] material_ids = ids.split(",");
		String scheduled_assign_date = sForm.getScheduled_assign_date();
		// 更新后的完成安排时间
		Date dScheduledAssignDate = DateUtil.toDate(scheduled_assign_date, DateUtil.DATE_PATTERN);

		MaterialProcessEntity mpEntity = new MaterialProcessEntity(); // TODO MPE
		BeanUtil.copyToBean(form, mpEntity, null);

		if (dScheduledAssignDate == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			dScheduledAssignDate = calendar.getTime();
			scheduled_assign_date = DateUtil.toString(dScheduledAssignDate, DateUtil.DATE_PATTERN);
		}
		mpEntity.setScheduled_assign_date(dScheduledAssignDate);

		// 历史记录修改
		String todayStr = DateUtil.toString(new Date(), DateUtil.DATE_PATTERN);
		boolean todayfix = scheduled_assign_date.equals(todayStr);
		Long todayTimestamp = DateUtil.toDate(todayStr, DateUtil.DATE_PATTERN).getTime();

		ScheduleHistoryMapper shMapper = conn.getMapper(ScheduleHistoryMapper.class);
		ScheduleHistoryEntity shEntity = new ScheduleHistoryEntity();
		shEntity.setScheduled_date(dScheduledAssignDate);

		MaterialProcessService mpService = new MaterialProcessService();

		for (String material_id : material_ids) {
			MaterialProcessEntity old = mpService.loadMaterialProcessOfLine(material_id, "00000000014", conn);

			mpEntity.setMaterial_id(material_id);
			mpEntity.setLine_id(lineId);
			mpService.updateScheduleAssignDate(mpEntity, conn);

			if (todayfix) {
				// 修改到今天
				if (old == null || old.getScheduled_assign_date() == null 
						|| old.getScheduled_assign_date().getTime() != todayTimestamp) {
					shEntity.setMaterial_id(material_id);
					if (shMapper.getByKey(shEntity) != null) {
						shMapper.appendTodayAsUpdate(shEntity);
					} else {
						ScheduleHistoryEntity retNew = shMapper.getOtherInfo(material_id);
						shEntity.setIn_schedule_means(3);
						shEntity.setRemove_flg(0);
						shEntity.setScheduled_expedited(retNew.getScheduled_expedited());
						shEntity.setArrival_plan_date(retNew.getArrival_plan_date());
						shMapper.append(shEntity);
					}
				}
			} else {
				// 由今天修改到其它日
				if (old != null && old.getScheduled_assign_date() != null 
						&& old.getScheduled_assign_date().getTime() == todayTimestamp) {
					shEntity.setMaterial_id(material_id);
					shEntity.setScheduled_date(new Date());
					shMapper.removeToday(shEntity);
				}
			}
		}
	}

	public void updateSchedulePeriod(HttpServletRequest req, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		// String material_id = req.getParameter("material_id");

		ScheduleHistoryMapper shMapper = conn.getMapper(ScheduleHistoryMapper.class);

		String ids = req.getParameter("ids");

		String[] material_ids = ids.split(",");

		ScheduleHistoryEntity entity = new ScheduleHistoryEntity();
		Converter<Date> dc = DateConverter.getInstance(DateUtil.DATE_PATTERN);
		Converter<Integer> ic = IntegerConverter.getInstance();

		for (int i=0; i < material_ids.length; i++) {
			entity.setMaterial_id(material_ids[i]);
			entity.setScheduled_date(dc.getAsObject(req.getParameter("scheduled_date")));
			entity.setPlan_day_period(ic.getAsObject(req.getParameter("plan_day_period")));
			shMapper.updatePeriod(entity);
		}
	}

	public void deleteSchedule(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ScheduleMapper dao = conn.getMapper(ScheduleMapper.class);

		ScheduleForm sForm = (ScheduleForm)form;
		String ids = sForm.getIds();
		String lineIds = sForm.getLineIds();
		String scheduled_assign_date = sForm.getScheduled_assign_date();
		
		String[] material_ids = ids.split(",");
		String[] line_ids = lineIds.split(",");

		ScheduleEntity model = new ScheduleEntity();

		// 历史记录修改
		boolean todayfix = scheduled_assign_date.equals(DateUtil.toString(new Date(), DateUtil.DATE_PATTERN));
		ScheduleHistoryMapper shMapper = conn.getMapper(ScheduleHistoryMapper.class);
		ScheduleHistoryEntity shEntity = new ScheduleHistoryEntity();
		shEntity.setScheduled_date(DateUtil.toDate(scheduled_assign_date, DateUtil.DATE_PATTERN));

		for (int i=0; i < material_ids.length; i++) {
			model.setLine_id(line_ids[i]);
			model.setMaterial_id(material_ids[i]);
			dao.deleteSchedule(model);
			// 历史记录修改
			if (todayfix) {
				shEntity.setMaterial_id(material_ids[i]);
				shMapper.removeToday(shEntity);
			}
		}
	}
	
	public void updateToPuse(String material_id, String move_reason, String position_id, SqlSessionManager conn) throws Exception {
		ForSolutionAreaService fsaService = new ForSolutionAreaService();
		fsaService.create(material_id, move_reason, 8, position_id, conn, true);

		// ScheduleMapper dao = conn.getMapper(ScheduleMapper.class);
		// dao.updateToPuse(id);
	}

	public AlarmMesssageForm getWarning(String material_id, SqlSession conn) {
		// 取得对应工位的中断信息
		AlarmMesssageMapper dao = conn.getMapper(AlarmMesssageMapper.class);
		AlarmMesssageEntity entity = dao.getBreakPushedAlarmMessage(material_id);
		AlarmMesssageForm form = new AlarmMesssageForm();
		CopyOptions co = new CopyOptions();

		co.dateConverter("MM-dd HH:mm", "occur_time");
		co.include("alarm_messsage_id", "occur_time", "sorc_no", "model_name", "serial_no", "line_name", "process_code", "operator_name", "position_id");
		BeanUtil.copyToForm(entity, form, co);
		// 取得原因
		PauseFeatureEntity pauseEntity = dao.getBreakOperatorMessageByID(entity.getAlarm_messsage_id());
		if (pauseEntity == null) {
			pauseEntity = dao.getBreakOperatorMessage(entity.getOperator_id(), material_id, entity.getPosition_id());
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

		List<AlarmMesssageSendationEntity> listSendation = dao.getBreakAlarmMessageSendation(entity.getAlarm_messsage_id());
		for (AlarmMesssageSendationEntity sendation : listSendation) {
			if (!CommonStringUtil.isEmpty(sendation.getComment())) {
				sComments += "\n" + sendation.getSendation_name() + ":" + sendation.getComment();
			}
		}
		form.setComment(sComments);

		return form;
	}

	public void hold(HttpServletRequest req, LoginData user, SqlSessionManager conn) throws Exception {
		String alarm_messsage_id = req.getParameter("alarm_messsage_id");

		AlarmMesssageMapper dao = conn.getMapper(AlarmMesssageMapper.class);
		AlarmMesssageEntity alarm_messsage = new AlarmMesssageEntity();
		alarm_messsage.setAlarm_messsage_id(alarm_messsage_id);
		alarm_messsage.setLevel(3);
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

		// 取得计划人员
		OperatorMapper oDao = conn.getMapper(OperatorMapper.class);
		OperatorEntity oCondition = new OperatorEntity();
		oCondition.setRole_id(RvsConsts.ROLE_SCHEDULER);

		// 发送警报到计划人员
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

	/**
	 * 取得机种别仕挂量
	 * @param conn
	 * @return
	 */
	public String getSikakeTable(SqlSession conn) {
		String retHtml = "";
		int tdCount = 1;
		ScheduleMapper dao = conn.getMapper(ScheduleMapper.class);
		List<Map<String, Object>> workingOfCategories = dao.getWorkingOfCategories();
		BigDecimal heap_total = new BigDecimal(0);

		for (Map<String, Object> workingOfCategory : workingOfCategories) {
			retHtml += "<td class=\"ui-state-default td-title\">" + workingOfCategory.get("name") + "</td>";
			BigDecimal heap = new BigDecimal((Long) workingOfCategory.get("heap"));
			retHtml += "<td class=\"td-content\">"+ heap.divide(new BigDecimal(RvsConsts.TIME_LIMIT), 0, BigDecimal.ROUND_CEILING) 
					+ "台/日 "+ heap + "台</td>";
			heap_total = heap_total.add(heap);
			tdCount++;
			if (tdCount % 3 == 0) retHtml += "</tr><tr>";
		}

		retHtml = "<tr><td class=\"ui-state-default td-title\">总计</td><td class=\"td-content\">" 
				+ heap_total.divide(new BigDecimal(RvsConsts.TIME_LIMIT), 0, BigDecimal.ROUND_CEILING) + "台/日 "+ heap_total 
				+ "台</td>" + retHtml + "</tr>";
		return retHtml.replaceAll("<tr></tr>", "");
	}

	public List<ScheduleForm> getReportScheduleList(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		ScheduleMapper dao = conn.getMapper(ScheduleMapper.class);
		ScheduleEntity conditionBean = new ScheduleEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		List<ScheduleEntity> entities = dao.searchReportScheduleByCondition(conditionBean);
		List<ScheduleForm> retForms = new ArrayList<ScheduleForm>();
		BeanUtil.copyToFormList(entities, retForms, null, ScheduleForm.class);
		return retForms;
	}

	/**
	 * 获得本月KPI数据
	 * @param callbackResponse
	 * @param conn
	 */
	public void getDayKpiOfWeek(Map<String, Object> callbackResponse,
			SqlSession conn) {
		DailyKpiMapper dkMapper = conn.getMapper(DailyKpiMapper.class);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(cal.getTimeInMillis());

		// 取得当天位置
		int dow =cal.get(Calendar.DAY_OF_WEEK);
		if (dow == Calendar.SUNDAY) {
			callbackResponse.put("now_column", 7);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			cal.add(Calendar.WEEK_OF_MONTH, -1);
		} else {
			callbackResponse.put("now_column", dow);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}

		// 取得本周初始日期
		String weekstart = DateUtil.toString(cal.getTime(), DateUtil.DATE_PATTERN);
		callbackResponse.put("weekstart", weekstart);

		String period = RvsUtils.getBussinessHalfYearString(cal);
		callbackResponse.put("period", period);

		String pr = RvsUtils.getBussinessHalfStartDate(today);

		// 取得时期,生产计划
		getPeriodPlans(pr, callbackResponse, conn);

		List<DailyKpiDataEntity> dkdList = new ArrayList<DailyKpiDataEntity>();
		for (int i=0;i<7;i++) {
			DailyKpiDataEntity dayData = dkMapper.getByDate(cal.getTime());
			if (dayData == null) {
				dayData = new DailyKpiDataEntity();
			}
			Date count_date = cal.getTime();
			if (i != 6) {
				if (count_date.after(today.getTime())) break;
//				if (count_date.equals(today.getTime())) { // 今日动态数据
//					Map<String, Object> condition = new HashMap<String, Object>();
//					dayData.setDirect_quotation_lt_rate(dkMapper.getDirectQuotationLtRate(count_date));
//					dayData.setFinal_inspect_pass_rate(dkMapper.getFinalInspectPassRate(count_date));
//					dayData.setIntime_complete_rate(dkMapper.getIntimeCompleteRate(count_date));
//					dayData.setTotal_plan_processed_rate(dkMapper.getPlanProcessedRate(condition));
//					condition.put("section_id", "00000000001");
//					dayData.setSection1_plan_processed_rate(dkMapper.getPlanProcessedRate(condition));
//					condition.put("section_id", "00000000003");
//					dayData.setSection2_plan_processed_rate(dkMapper.getPlanProcessedRate(condition));
//					dayData.setQuotation_lt_rate(dkMapper.getQuotationLtRate(count_date));
//				}

				dkdList.add(dayData);
			} // TODO else
//			dayData.setHalf_period_complete(dkMapper.getOutCount(periodStart, count_date));
//			dayData.setMonth_complete(dkMapper.getOutCount(monthStart, count_date));
			if (i==0) {
				callbackResponse.put("comment", dayData.getComment());
			}
			cal.add(Calendar.DATE, 1);
		}

		callbackResponse.put("dkdList", dkdList);
	}

	/**
	 * 取得半期计划产出数
	 * @param pr
	 * @param callbackResponse
	 * @param conn
	 */
	private void getPeriodPlans(String pr, 
			Map<String, Object> callbackResponse, SqlSession conn) {

		RepairPlanMapper rpMapper = conn.getMapper(RepairPlanMapper.class);

		Date dHpStart = DateUtil.toDate(pr, DateUtil.DATE_PATTERN);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dHpStart);

		int shippingPlanOfHpHeavy = 0, shippingPlanOfHpLight = 0, shippingPlanOfHpPeripheral = 0;

		for (int i = 0; i < 6; i++, cal.add(Calendar.MONTH, 1)) {
			String planYear = "" + cal.get(Calendar.YEAR);
			String planMonth = "" + (cal.get(Calendar.MONTH) + 1);

			// callbackResponse.put("planMonth", planMonth);

			Map<String, Integer> shippingPlanOfMonths = rpMapper.getShippingPlan(planYear, planMonth);

			if (shippingPlanOfMonths != null) {
				shippingPlanOfHpHeavy += shippingPlanOfMonths.get("heavy_fix");
				shippingPlanOfHpLight += shippingPlanOfMonths.get("light_fix");
				shippingPlanOfHpPeripheral += shippingPlanOfMonths.get("peripheral");
			}
		}

		callbackResponse.put("shippingPlanOfHpHeavy", shippingPlanOfHpHeavy);
		callbackResponse.put("shippingPlanOfHpLight", shippingPlanOfHpLight);
		callbackResponse.put("shippingPlanOfHpPeripheral", shippingPlanOfHpPeripheral);
	}

	public String getScheduleSections(List<SectionEntity> sectionInline) {
		StringBuffer sb = new StringBuffer();
		for (SectionEntity section : sectionInline) {
			sb.append("<input type=\"radio\" name=\"schedule_section\" class=\"ui-button ui-corner-up\" id=\"section_")
					.append(section.getSection_id())
					.append("_button\" value=\"")
					.append(section.getSection_id())
					.append("\" role=\"button\"><label for=\"section_")
					.append(section.getSection_id()).append("_button\">")
					.append(section.getName()).append("</label>");
		}

		return sb.toString();
	}
}

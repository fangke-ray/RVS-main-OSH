package com.osh.rvs.service.inline;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.AlarmMesssageEntity;
import com.osh.rvs.bean.inline.DailyKpiDataEntity;
import com.osh.rvs.bean.inline.PauseFeatureEntity;
import com.osh.rvs.bean.inline.ScheduleEntity;
import com.osh.rvs.bean.inline.ScheduleHistoryEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.inline.ScheduleForm;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.mapper.inline.DailyKpiMapper;
import com.osh.rvs.mapper.inline.ScheduleHistoryMapper;
import com.osh.rvs.mapper.inline.ScheduleMapper;
import com.osh.rvs.mapper.inline.ScheduleProcessMapper;
import com.osh.rvs.mapper.master.HolidayMapper;
import com.osh.rvs.service.AlarmMesssageService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.BigDecimalConverter;
import framework.huiqing.common.util.copy.Converter;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateConverter;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.copy.IntegerConverter;

public class ScheduleProcessService {

	public List<ScheduleForm> getMaterialList(ActionForm form, SqlSession conn, List<MsgInfo> errors, Integer resolveLevel) {
		ScheduleProcessMapper dao = conn.getMapper(ScheduleProcessMapper.class);
		ScheduleMapper tdao = conn.getMapper(ScheduleMapper.class);
		HolidayMapper hdao = conn.getMapper(HolidayMapper.class);
		ScheduleEntity conditionBean = new ScheduleEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// TODO 
		List<String> ids = tdao.searchMaterialIdsByCondition(conditionBean);
		List<ScheduleForm> retForms = new ArrayList<ScheduleForm>();
		Map<String, String> delayMap = new HashMap<String, String>();

		if (ids != null && ids.size() > 0) {
			List<ScheduleEntity> entities = dao.searchMaterialByIds(ids);
			for (ScheduleEntity entity : entities) {
				ScheduleForm retForm = new ScheduleForm();
				BeanUtil.copyToForm(entity, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);
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
						if (resolveLevel <= message.getLevel()) {
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
				// 过期颜色
				if (retForm.getScheduled_date_end() != null) {
					if (delayMap.containsKey(retForm.getScheduled_date_end()
							+ retForm.getAm_pm())) {
						retForm.setRemain_days(delayMap.get(retForm
								.getScheduled_date_end() + retForm.getAm_pm()));
					} else {
						Map<String, Object> condiMap = new HashMap<String, Object>();
						condiMap.put("scheduled_date", entity.getScheduled_date_end());
						condiMap.put("am_pm", entity.getAm_pm());
						String remain_days = hdao.compareExperial(condiMap);
						delayMap.put(retForm.getScheduled_date_end() + retForm.getAm_pm(), remain_days);
						retForm.setRemain_days(remain_days);
					}
				}
				
				retForms.add(retForm);
			}
		}
		return retForms;
	}

	public List<ScheduleForm> getScheduleList(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		ScheduleProcessMapper dao = conn.getMapper(ScheduleProcessMapper.class);
		HolidayMapper hdao = conn.getMapper(HolidayMapper.class);
		ScheduleEntity conditionBean = new ScheduleEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		List<ScheduleEntity> entities = dao.searchSchedule(conditionBean);
		List<ScheduleForm> retForms = new ArrayList<ScheduleForm>();
		Map<String, String> delayMap = new HashMap<String, String>();

		if (entities.size() == 1 && entities.get(0).getMaterial_id() == null) {
			return retForms;
		}
		for (ScheduleEntity entity : entities) {
			ScheduleForm retForm = new ScheduleForm();
			BeanUtil.copyToForm(entity, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			
			// 过期颜色
			if (retForm.getScheduled_date_end() != null) {
				if (delayMap.containsKey(retForm.getScheduled_date_end()
						+ retForm.getAm_pm())) {
					retForm.setRemain_days(delayMap.get(retForm
							.getScheduled_date_end() + retForm.getAm_pm()));
				} else {
					Map<String, Object> condiMap = new HashMap<String, Object>();
					condiMap.put("scheduled_date", entity.getScheduled_date_end());
					condiMap.put("am_pm", entity.getAm_pm());
					String remain_days = hdao.compareExperial(condiMap);
					delayMap.put(retForm.getScheduled_date_end() + retForm.getAm_pm(), remain_days);
					retForm.setRemain_days(remain_days);
				}
			}
			
			retForms.add(retForm);
		}
		return retForms;
	}
	
	public List<ScheduleForm> getOutScheduleList(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		ScheduleProcessMapper dao = conn.getMapper(ScheduleProcessMapper.class);
		HolidayMapper hdao = conn.getMapper(HolidayMapper.class);
		
		List<ScheduleEntity> entities = dao.searchOutSchedule();
		List<ScheduleForm> retForms = new ArrayList<ScheduleForm>();
		Map<String, String> delayMap = new HashMap<String, String>();

		for (ScheduleEntity entity : entities) {
			ScheduleForm retForm = new ScheduleForm();
			BeanUtil.copyToForm(entity, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			
			// 过期颜色
			if (retForm.getScheduled_date_end() != null) {
				if (delayMap.containsKey(retForm.getScheduled_date_end()
						+ retForm.getAm_pm())) {
					retForm.setRemain_days(delayMap.get(retForm
							.getScheduled_date_end() + retForm.getAm_pm()));
				} else {
					Map<String, Object> condiMap = new HashMap<String, Object>();
					condiMap.put("scheduled_date", entity.getScheduled_date_end());
					condiMap.put("am_pm", entity.getAm_pm());
					String remain_days = hdao.compareExperial(condiMap);
					delayMap.put(retForm.getScheduled_date_end() + retForm.getAm_pm(), remain_days);
					retForm.setRemain_days(remain_days);
				}
			}
			
			retForms.add(retForm);
		}
		return retForms;
	}

	public void updateSchedule(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ScheduleProcessMapper dao = conn.getMapper(ScheduleProcessMapper.class);
		
		String ids = ((ScheduleForm)form).getIds();
		String lineId = ((ScheduleForm)form).getLineIds();//单一值
		String[] material_ids = ids.split(",");
		
		ScheduleEntity model = new ScheduleEntity();
		BeanUtil.copyToBean(form, model, null);

		for (String material_id : material_ids) {
			model.setMaterial_id(material_id);
			model.setLine_id(lineId);
			dao.updateSchedule(model);
		}
	}

	public void deleteSchedule(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ScheduleProcessMapper dao = conn.getMapper(ScheduleProcessMapper.class);

		String ids = ((ScheduleForm)form).getIds();
		String lineIds = ((ScheduleForm)form).getLineIds();
		
		String[] material_ids = ids.split(",");
		String[] line_ids = lineIds.split(",");

		Calendar scheduled_assign_date = Calendar.getInstance();
		scheduled_assign_date.set(Calendar.HOUR_OF_DAY, 0);
		scheduled_assign_date.set(Calendar.MINUTE, 0);
		scheduled_assign_date.set(Calendar.SECOND, 0);
		scheduled_assign_date.set(Calendar.MILLISECOND, 0);

		ScheduleEntity model = new ScheduleEntity();
		ScheduleHistoryMapper shMapper = conn.getMapper(ScheduleHistoryMapper.class);
		ScheduleHistoryEntity shEntity = new ScheduleHistoryEntity();
		shEntity.setScheduled_date(scheduled_assign_date.getTime());

		for (int i=0; i < material_ids.length; i++) {
			model.setLine_id(line_ids[i]);
			model.setMaterial_id(material_ids[i]);
			dao.deleteSchedule(model);

			shEntity.setMaterial_id(material_ids[i]);
			shMapper.removeToday(shEntity);
		}
	}
	
	public void updateToPuse(SqlSessionManager conn, String id) {
		ScheduleProcessMapper dao = conn.getMapper(ScheduleProcessMapper.class);
		dao.updateToPuse(id);
	}

	@SuppressWarnings("unchecked")
	public void updateDailyKpi(HttpServletRequest req, SqlSessionManager conn) {
		String comment = req.getParameter("comment");
		String weekstart = req.getParameter("weekstart");

		@SuppressWarnings("rawtypes")
		List<HashMap> pareForms = new AutofillArrayList<HashMap>(HashMap.class);
		Map<String, DailyKpiDataEntity> dkdeMap = new HashMap<String, DailyKpiDataEntity>();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		Map<String, String[]> parameterMap = req.getParameterMap();
		Converter<Date> dc = DateConverter.getInstance(DateUtil.DATE_PATTERN);
		Converter<Integer> ic = IntegerConverter.getInstance();
		Converter<BigDecimal> bdc = BigDecimalConverter.getInstance();

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("update".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					// 全
					if ("count_date".equals(column)) {
						if (!dkdeMap.containsKey(value[0])) {
							DailyKpiDataEntity dkde = new DailyKpiDataEntity();
							dkde.setCount_date(dc.getAsObject(value[0]));
							dkdeMap.put(value[0], dkde);
						}
						pareForms.get(icounts).put("count_date", value[0]);
					} else if ("target".equals(column)) {
						pareForms.get(icounts).put("target", value[0]);
					} else if ("val".equals(column)) {
						pareForms.get(icounts).put("val", value[0]);
					}
				}
			}
		}
		for (HashMap pareForm : pareForms) {
			DailyKpiDataEntity dkde = dkdeMap.get(pareForm.get("count_date"));

			String val = pareForm.get("val").toString();
			switch(pareForm.get("target").toString()) {
			case "half_period_complete" : dkde.setHalf_period_complete(ic.getAsObject(val)); break;
			case "half_period_light_complete" : dkde.setHalf_period_light_complete(ic.getAsObject(val)); break;
			case "half_period_peripheral_complete" : dkde.setHalf_period_peripheral_complete(ic.getAsObject(val)); break;

			case "month_complete" : dkde.setMonth_complete(ic.getAsObject(val)); break;
			case "service_repair_back" : dkde.setService_repair_back_rate(bdc.getAsObject(val)); break;

			case "final_inspect_pass" : dkde.setFinal_inspect_pass_rate(bdc.getAsObject(val)); break;
			case "intime_complete" : dkde.setIntime_complete_rate(bdc.getAsObject(val)); break;
			case "total_plan_processed" : dkde.setTotal_plan_processed_rate(bdc.getAsObject(val)); break;
			case "section1_plan_processed" : dkde.setSection1_plan_processed_rate(bdc.getAsObject(val)); break;
			case "section2_plan_processed" : dkde.setSection2_plan_processed_rate(bdc.getAsObject(val)); break;
			case "ns_regenerate" : dkde.setNs_regenerate_rate(bdc.getAsObject(val)); break;
			case "inline_passthrough" : dkde.setInline_passthrough_rate(bdc.getAsObject(val)); break;
			case "quotation_lt" : dkde.setQuotation_lt_rate(bdc.getAsObject(val)); break;
			case "direct_quotation_lt" : dkde.setDirect_quotation_lt_rate(bdc.getAsObject(val)); break;
			case "comment" : dkde.setComment(val); break;
			}
		}

		DailyKpiMapper mapper = conn.getMapper(DailyKpiMapper.class);

		for (String sCountdate : dkdeMap.keySet()) {
			DailyKpiDataEntity dkde = dkdeMap.get(sCountdate);
			if (!CommonStringUtil.isEmpty(comment) && sCountdate.equals(weekstart)) {
				dkde.setComment(comment);
				comment = null;
			}
			if (mapper.getByDate(dkde.getCount_date()) == null)
				mapper.insert(dkde.getCount_date());
			if (dkde.getService_repair_back_rate()!= null && dkde.getService_repair_back_rate().equals(BigDecimal.ZERO)) {
				mapper.update4ServiceRepairBackRateZero(dkde);
			} else {
				mapper.update(dkde);
			}
		}
//		if (comment != null) {
//			DailyKpiDataEntity dkde = new DailyKpiDataEntity();
//			dkde.setCount_date(DateUtil.toDate(weekstart, DateUtil.DATE_PATTERN));
//			dkde.setComment(comment);
//			if (mapper.getByDate(dkde.getCount_date()) == null)
//				mapper.insert(dkde.getCount_date());
//			mapper.update(dkde);
//		}
	}
}

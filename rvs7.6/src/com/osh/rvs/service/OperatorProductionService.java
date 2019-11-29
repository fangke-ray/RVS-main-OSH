package com.osh.rvs.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.OperatorProductionEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MonthFilesDownloadForm;
import com.osh.rvs.form.data.OperatorProductionForm;
import com.osh.rvs.mapper.data.OperatorProductionMapper;
import com.osh.rvs.mapper.master.HolidayMapper;
import com.osh.rvs.mapper.master.OperatorMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class OperatorProductionService {
	private Logger logger = Logger.getLogger(getClass());

//	private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final int work_start = 8;
	public static final int work_end = 17;
	public static final int work_start_min = 45;
	public static final int work_end_min = 30;

	public static final int work_forenoon_end = 11;
	public static final int work_forenoon_end_min = 55;
	public static final int work_overwork_end = 20;
	public static final int work_overwork_end_min = 30;

	/**
	 * 维修担当人一览
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<OperatorProductionForm> searchByCondition(ActionForm form, SqlSession conn) {
		OperatorProductionEntity conditionBean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		List<OperatorProductionEntity> list = new ArrayList<OperatorProductionEntity>();
		List<OperatorProductionEntity> listDirect = new ArrayList<OperatorProductionEntity>();
		List<OperatorProductionEntity> listIndirect = new ArrayList<OperatorProductionEntity>();

		boolean forIndirect = (CommonStringUtil.isEmpty(conditionBean.getSection_id())) 
				&& (CommonStringUtil.isEmpty(conditionBean.getLine_id()) 
				|| "00000000011".equals(conditionBean.getLine_id()) || "00000000020".equals(conditionBean.getLine_id()));
		// 检索条件限定日期时
		if (conditionBean.getAction_time_start() != null && conditionBean.getAction_time_start().equals(conditionBean.getAction_time_end())) {
			listDirect = dao.getProductionFeatureByConditionOfDay(conditionBean);
			list.addAll(listDirect);
		} else {
			listDirect = dao.getProductionFeatureByCondition(conditionBean);
			list.addAll(listDirect);
		}
		if (forIndirect) {
			listIndirect = dao.getAfProductionFeatureByCondition(conditionBean);
			for(OperatorProductionEntity indirect : listIndirect) {
				indirect.setPosition_name(getIndirectWorkGroup(indirect.getPosition_name()));
			}
			list.addAll(listIndirect);
		}

		List<OperatorProductionForm> rtList = new ArrayList<OperatorProductionForm>();

		BeanUtil.copyToFormList(list, rtList, null, OperatorProductionForm.class);

		return rtList;
	}

	/**
	 * 取得间接认员作业组名
	 * 
	 * @param codes
	 * @return
	 */
	private String getIndirectWorkGroup(String codes) {
		if (codes == null) return null;
		Set<String> groupModules = new HashSet<String>();
		String[] codeArr = codes.split(" ");

		for (String code : codeArr) {
			groupModules.add(code.substring(0, 2));
		}

		String ret = "";

		for(String groupModule : groupModules) {
			ret += (CodeListUtils.getValue("qf_production_module", groupModule)) + " ";
		}

		return ret;
	}

	public OperatorProductionForm getDetail(ActionForm form, SqlSession conn) {
		OperatorProductionEntity conditionBean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		OperatorProductionEntity entity = dao.getDetail(conditionBean);
		
		OperatorProductionForm rtForm = new OperatorProductionForm();
		if (entity != null) {
			BeanUtil.copyToForm(entity, rtForm, null);
		}

		return rtForm;
	}
	
	public List<OperatorProductionForm> getReportData(ActionForm form, SqlSession conn) {
		OperatorProductionEntity conditionBean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		List<OperatorProductionEntity> list = dao.getProductionFeatureByKey(conditionBean);
		
		List<OperatorProductionForm> rtList = new ArrayList<OperatorProductionForm>();
		if (list != null) {
			BeanUtil.copyToFormList(list, rtList, null, OperatorProductionForm.class);
		}
		return rtList;
	}
	
	public List<OperatorProductionForm> getProductionFeatureByKey(ActionForm form, SqlSession conn,
			HttpSession session, OperatorProductionForm detail, boolean editable, String overtime_finish, Map<String, Object> listResponse) {

		OperatorProductionEntity conditionBean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		// 取得作业/暂停/组件作业的一览(数据库原始数据)
		List<OperatorProductionEntity> list = null;

		OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);
		OperatorEntity target = oMapper.getOperatorByID(conditionBean.getOperator_id());
		
		if (target.getWork_count_flg() == RvsConsts.WORK_COUNT_FLG_INDIRECT) {
			list = dao.getAfProductionFeatureByKey(conditionBean);
			for(OperatorProductionEntity item : list) {
				item.setProcess_code(CodeListUtils.getValue("qf_production_type", item.getPosition_id()));
			}
		} else {
			list = dao.getProductionFeatureByKey(conditionBean);
		}

		// 编辑后的一览
		List<OperatorProductionEntity> newList = new ArrayList<OperatorProductionEntity>();

		// 设置可编辑
		// boolean editable = editable(session, detail, list, listResponse);//管理员，线长

		boolean isOwner = isOwner(session, detail, conn);//本人操作

		boolean editOverwork = editable || isOwner;

		listResponse.put("editable", editOverwork);

		if (list == null || list.isEmpty()) { //不存在记录
			OperatorProductionEntity entity = new OperatorProductionEntity();
			entity.setPause_start_time(getNewDate(conditionBean.getAction_time(), work_start, work_start_min, 1));
			// entity.setPause_finish_time(getNewDate(conditionBean.getAction_time(), work_end, work_end_min));
			if (editOverwork) {
				entity.setLeak("true");
			}
			newList.add(entity);
		} else {
			// 空隙时间补上行
			newList = checkLeakTime(list, editable, isOwner, overtime_finish);
		}
		
		List<OperatorProductionForm> rtList = new ArrayList<OperatorProductionForm>();
		BeanUtil.copyToFormList(newList, rtList, null, OperatorProductionForm.class);
		return rtList;
	}

	private static final int REASON_OTHER = 100;

	public void savePause(ActionForm form, SqlSession conn) throws Exception {
		OperatorProductionForm f = (OperatorProductionForm) form;
		OperatorProductionEntity conditionBean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		if ("时段结束".equals(conditionBean.getLeak())) {
			conditionBean.setPause_finish_time(new Date());
		}

//		conditionBean.setPause_finish_time(new Timestamp(format.parse(f.getPause_finish_time()).getTime()));
//		conditionBean.setPause_start_time(new Timestamp(format.parse(f.getPause_start_time()).getTime()));
		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		if (f.getPause_finish_time() == null || f.getPause_finish_time().endsWith(" 23:59:59")) { // 当日最终的数据
			dao.deletePause(conditionBean);

			Calendar startTime = Calendar.getInstance();
			startTime.setTime(conditionBean.getPause_start_time());

			boolean restAfternoon = isStartAtAm(startTime);
			conditionBean.setPause_finish_time(getAutoFinishTime(startTime));

			if (conditionBean.getReason() == null) {
				if (!CommonStringUtil.isEmpty(conditionBean.getComments())) {
					conditionBean.setReason(REASON_OTHER); // 其他
				} else {
					return;
				}
			}

			dao.savePause(conditionBean);

			if (restAfternoon) {
				conditionBean.setPause_start_time(conditionBean.getPause_finish_time());
				conditionBean.setPause_finish_time(getNewDate(startTime.getTime(), work_end, work_end_min));
				conditionBean.setReason(282); // workRecord.reason.282 = H3:休假、事/病假
				conditionBean.setComments("");
				dao.savePause(conditionBean);
			}

		} else {
			String exist = dao.existPause(conditionBean);
			if (CommonStringUtil.isEmpty(exist)) {
				dao.savePause(conditionBean);
			} else {
				logger.info("did:" + conditionBean.getPause_start_time() + "->" + conditionBean.getPause_finish_time());
				dao.updatePause(conditionBean);
			}
		}
		
	}

	/**
	 * 开始时间是上午
	 * @param startTime
	 * @return
	 */
	public boolean isStartAtAm(Calendar startTime) {
		int nowHour = startTime.get(Calendar.HOUR_OF_DAY);
		int nowMinute = startTime.get(Calendar.MINUTE);
		if (nowHour < work_forenoon_end ||
				(nowHour == work_forenoon_end && nowMinute < work_forenoon_end_min)) {
			return true;
		}
		return false;
	}
	/**
	 * 取得自动结束时间
	 * @param startTime
	 * @return
	 */
	public Date getAutoFinishTime(Calendar startTime) {
		// 4段结束时间
		// 12:00 之前，12:00结束，补上12:00～17:30的下午休假。
		// 17:30 之前，17:30结束
		// 20:30 之前，20:30结束
		// 20:30 之后，1分钟结束
		int nowHour = startTime.get(Calendar.HOUR_OF_DAY);
		int nowMinute = startTime.get(Calendar.MINUTE);
		if (nowHour < work_forenoon_end ||
				(nowHour == work_forenoon_end && nowMinute < work_forenoon_end_min)) {
			return getNewDate(startTime.getTime(), work_forenoon_end, work_forenoon_end_min);
		} else if (nowHour < work_end ||
				(nowHour == work_end && nowMinute < work_end_min)) {
			return getNewDate(startTime.getTime(), work_end, work_end_min);
		} else if (nowHour < work_overwork_end ||
				(nowHour == work_overwork_end && nowMinute < work_overwork_end_min)) {
			return getNewDate(startTime.getTime(), work_overwork_end, work_overwork_end_min);
		} else {
			startTime.add(Calendar.MINUTE, 1);
			return startTime.getTime();
		}
	}

	public void deletePauseOvertime(ActionForm form, SqlSession conn) throws Exception {
		OperatorProductionEntity conditionBean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
//		OperatorProductionForm f = (OperatorProductionForm) form;
//		conditionBean.setPause_start_time(new Timestamp(format.parse(f.getPause_start_time()).getTime()));
		
		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		dao.deletePauseOvertime(conditionBean);
	}
	
	/**
	 * 取得加班信息
	 * @param form 条件
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public OperatorProductionForm getPauseOvertime(ActionForm form, SqlSession conn) throws Exception {
		OperatorProductionEntity conditionBean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		OperatorProductionEntity entity = dao.getPauseOvertime(conditionBean);
		
		OperatorProductionForm rtForm = new OperatorProductionForm();
		if (entity != null) {
			BeanUtil.copyToForm(entity, rtForm, null);
		}

		return rtForm;
	}
	
	public void saveoverwork(ActionForm form, SqlSession conn) throws Exception {
//		OperatorProductionForm f = (OperatorProductionForm) form;
		OperatorProductionEntity conditionBean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
//		conditionBean.setPause_start_time(new Timestamp(format.parse(f.getPause_start_time()).getTime()));
//		conditionBean.setPause_finish_time(new Timestamp(format.parse(f.getPause_finish_time()).getTime()));
		conditionBean.setReason(conditionBean.getOverwork_reason());
		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		dao.savePause(conditionBean);
	}
	
	public void updatePauseOvertime(ActionForm form, SqlSession conn) throws Exception {
		OperatorProductionEntity conditionBean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
//		OperatorProductionForm f = (OperatorProductionForm) form;
//		conditionBean.setPause_start_time(new Timestamp(format.parse(f.getPause_start_time()).getTime()));
//		conditionBean.setPause_finish_time(new Timestamp(format.parse(f.getPause_finish_time()).getTime()));
		
		conditionBean.setReason(conditionBean.getOverwork_reason());
		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		dao.updatePauseOvertime(conditionBean);
	}

	/**
	 * 可以编辑
	 * @param session
	 * @param form
	 * @param list
	 * @param listResponse 
	 * @return
	 */
	private boolean editable(HttpSession session, OperatorProductionForm form, List<OperatorProductionEntity> list, Map<String, Object> listResponse) {
		OperatorProductionEntity bean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, bean, null);

		// 今天的作业报告是不能被他人编辑的
		if (bean.getAction_time()!= null 
				&& DateUtil.compareDate(bean.getAction_time(), new Date()) == 0) {
			return false;
		}
		
		LoginData loginData = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = loginData.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_ADMIN)) { // 系统管理员
			listResponse.put("isAdmin", true);//管理员可输出工作日报
			return true;
		} else if(bean.getSection_id() == null && bean.getLine_id() == null) {
			return true;
		} else if(RvsConsts.ROLE_LINELEADER.equals(loginData.getRole_id()) && 
				bean.getSection_id().equals(loginData.getSection_id()) && 
				bean.getLine_id().equals(loginData.getLine_id())) {//线长,与当前操作人员同一课室,工程 TODO who did
			listResponse.put("isAdmin", true);//线长可输出工作日报
			Date finish = list.isEmpty() ? null : list.get(list.size() -1).getPause_finish_time();
			if (finish == null) { //最后一条记录的结束时间是空,没有生成下班记录
				return true;
			} else {
				Date end = getNewDate(finish, work_end, work_end_min); //下班时间
				if (finish.before(end)) {//下班之前的记录,没有生成下班记录
					return true;
				}
			}
		} else if(privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT) && 
				bean.getLine_id().equals(loginData.getLine_id())) {//线长,与当前操作人员同一工程
			listResponse.put("isAdmin", true);//线长可输出工作日报
			Date finish = list.isEmpty() ? null : list.get(list.size() -1).getPause_finish_time();
			if (finish == null) { //最后一条记录的结束时间是空,没有生成下班记录
				return true;
			} else {
				Date end = getNewDate(finish, work_end, work_end_min); //下班时间
				if (finish.before(end)) {//下班之前的记录,没有生成下班记录
					return true;
				}
			}
		} else if(RvsConsts.ROLE_QA_MANAGER.equals(loginData.getRole_id()) && 
				bean.getLine_id().equals(loginData.getLine_id())) {//线长,与当前操作人员同一工程
			listResponse.put("isAdmin", true);//线长可输出工作日报
			Date finish = list.isEmpty() ? null : list.get(list.size() -1).getPause_finish_time();
			if (finish == null) { //最后一条记录的结束时间是空,没有生成下班记录
				return true;
			} else {
				Date end = getNewDate(finish, work_end, work_end_min); //下班时间
				if (finish.before(end)) {//下班之前的记录,没有生成下班记录
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * 补充整理日报项目
	 * @param list
	 * @param editable
	 * @param isOwner
	 * @param overtime_finish 当日加班结束时间
	 * @return
	 */
	private List<OperatorProductionEntity> checkLeakTime(List<OperatorProductionEntity> list, boolean editable,
			boolean isOwner, String overtime_finish) {
		List<OperatorProductionEntity> entities = new ArrayList<OperatorProductionEntity>();
		
		int size = list.size();
		logger.info("list.size.before=" + size);

		for(int i = 0; i < size; i++) {
			OperatorProductionEntity entity = list.get(i);
			if (!isProductionFeature(entity) && editable) {//b类管理员,线长可编辑
				entity.setLeak("true");
			} 
			Date action_time = null;
			if (i+1 < size) {
				action_time = list.get(i+1).getPause_start_time();
			}

			Date finish_time = list.get(i).getPause_finish_time();

			if (size == 1) { //只有一条记录
				checkStartTime(entities, entity, editable, isOwner);
				entities.add(entity);
				checkEndTime(entities, entity, editable, isOwner, overtime_finish);
			} else if (i == 0) {//第一条
				checkStartTime(entities, entity, editable, isOwner);
				entities.add(entity);
			} else if (i == size -1) {//最后一条
				// 有进行中的记录的话，到此结束为最后一条
				entities.add(entity);
				if (finish_time == null && CommonStringUtil.isEmpty(entity.getSorc_no())) {
					if (isOwner) entity.setLeak("true");
					break;
				}
				checkEndTime(entities, entity, editable, isOwner, overtime_finish);
			} else {
				if (CommonStringUtil.isEmpty(entity.getSorc_no()) && action_time != null) {
					if (entity.getPause_finish_time() == null) {
						entity.setPause_finish_time(new Date(action_time.getTime()));
					} else if(entity.getPause_finish_time().after(action_time)) {
						entity.getPause_finish_time().setTime(action_time.getTime()); 
					}
				}
				entities.add(entity);
			}
			
			// 有进行中的记录的话，到此结束为最后一条
			if (finish_time == null) {
				continue;
			}

			Calendar cFinish = Calendar.getInstance();
			cFinish.setTime(finish_time);

			if (i+1 < size) {
				Calendar cAction = Calendar.getInstance();
				cAction.setTime(action_time);
				// 分钟数之差
				int diffMinutes = cAction.get(Calendar.HOUR_OF_DAY) * 60 + cAction.get(Calendar.MINUTE)
						- cFinish.get(Calendar.HOUR_OF_DAY) * 60 - cFinish.get(Calendar.MINUTE);
				if (diffMinutes >= 4) {
					// 补充间歇时段
					addNewEntity(entities, finish_time, action_time, editable, isOwner);
				}
			}
		}
		
		logger.info("list.size.after=" + entities.size());
		return entities;
	}
	
	private void checkStartTime(List<OperatorProductionEntity> entities, OperatorProductionEntity source, boolean editable, boolean isOwner) {
		Date start = source.getPause_start_time();
		Date work = getNewDate(start, work_start, work_start_min, 1); //上班时间
		Calendar cAction = Calendar.getInstance();
		Calendar cFinish = Calendar.getInstance();
		cAction.setTime(start);
		cFinish.setTime(work);
		// 分钟数之差
		int diffMinutes = cAction.get(Calendar.HOUR_OF_DAY) * 60 + cAction.get(Calendar.MINUTE)
				- cFinish.get(Calendar.HOUR_OF_DAY) * 60 - cFinish.get(Calendar.MINUTE);
		if (diffMinutes >= 1) {
			// 补充间歇时段
			addNewEntity(entities, work, start, editable, isOwner);
		}
	}
	
	private void checkEndTime(List<OperatorProductionEntity> entities, OperatorProductionEntity source,
			boolean editable, boolean isOwner, String overtime_finish) {
		Date finish = source.getPause_finish_time();
		if (finish == null) {// 暂停中状态
			if (source.getSorc_no() == null) source.setLeak("true");
		} else {
			Date off_duty = null;
			if (overtime_finish == null) {
				off_duty = getNewDate(source.getPause_start_time(), work_end, work_end_min); // 下班时间
			} else {
				off_duty = DateUtil.toDate(overtime_finish, DateUtil.DATE_TIME_PATTERN);
			}

			if (finish.before(off_duty)) {
				addNewEntity(entities, finish, null, editable, isOwner);
			}
			// 如果结束时间已经不是当日（比如暂停跨日）
			if (DateUtil.compareDate(finish, off_duty) != 0) {
				Date _start = source.getPause_start_time();
				// 显示上设置为下班时间或开始时间晚的
				if (_start.after(off_duty)) {
					source.setPause_finish_time(_start);
				} else {
					source.setPause_finish_time(off_duty);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static Date getNewDate(Date source, int hour) {
		return getNewDate(source, hour , 0);
	}
	private static Date getNewDate(Date source, int hour, int minute) {
		return getNewDate(source, hour, minute , 0);
	}
	private static Date getNewDate(Date source, int hour, int minute, int limit) {
		Calendar calendar = Calendar.getInstance();
		Long nowTs = calendar.getTimeInMillis();

		calendar.setTime(source);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		if (limit == 1 && nowTs < calendar.getTimeInMillis()) { // 当前时间早于设定时间前，按当前时间
			calendar.setTimeInMillis(nowTs);
		}
		if (limit == -1 && nowTs > calendar.getTimeInMillis()) { // 当前时间晚于设定时间后，按当前时间
			calendar.setTimeInMillis(nowTs);
		}
		return calendar.getTime();
	}
	
	/**
	 * C类虚拟记录
	 * @param entities
	 * @param start
	 * @param end
	 */
	private void addNewEntity(List<OperatorProductionEntity> entities, Date start, Date end, boolean editable, boolean isOwner) {
		OperatorProductionEntity entity = new OperatorProductionEntity();
		entity.setPause_start_time(start);
		entity.setPause_finish_time(end);
		if (editable || isOwner) {
			entity.setLeak("true");
		}
		entities.add(entity);
	}

	private boolean isProductionFeature(OperatorProductionEntity entity){
		if (!CommonStringUtil.isEmpty(entity.getSorc_no()) || 
			!CommonStringUtil.isEmpty(entity.getModel_name()) || 
			!CommonStringUtil.isEmpty(entity.getProcess_code())) {
			return true;
		}
		
		return false;
	}

	/**
	 * 前一工作日MAP
	 */
	private static Map<String, Date> mapPrevWork = new HashMap<String, Date>(); 

	/***
	 * 判断是否本人在两天范围内
	 * @param session
	 * @param form
	 * @param conn 
	 * @return
	 */
	private boolean isOwner(HttpSession session, OperatorProductionForm form, SqlSession conn) {
		OperatorProductionEntity bean = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, bean, null);

		String sToday = DateUtil.toString(new Date(), DateUtil.ISO_DATE_PATTERN);
		if (!mapPrevWork.containsKey(sToday)) {
			HolidayMapper mapper = conn.getMapper(HolidayMapper.class);
			Map<String, Object> cond = new HashMap<String, Object>();
			cond.put("date", DateUtil.toDate(sToday, DateUtil.ISO_DATE_PATTERN));
			cond.put("interval", -1);
			Date prevWork;
			try {
				prevWork = mapper.addWorkdays(cond);
			} catch (Exception e) {
				return false;
			}
			mapPrevWork.put(sToday, prevWork);
		}
		Date prevWork = mapPrevWork.get(sToday);

		if (DateUtil.compareDate(bean.getAction_time(), prevWork) < 0) {
			return false;
		} //不是今天
		
		LoginData loginData = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		// 不是本人在查看
		if (form.getOperator_id().equals(loginData.getOperator_id())) {
			return true;
		}

		return false;
	}

	/**
	 * 为提交数据设定默认操作者和日期信息，并判断是否有权限发生更新
	 * @param form
	 * @param session
	 * @return 有修正权限
	 */
	public boolean setDefault(ActionForm form, HttpSession session) {
		OperatorProductionForm operatorProductionForm = (OperatorProductionForm)form;
		boolean doFix = false;

		LoginData loginData = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String submitOperatorId = operatorProductionForm.getOperator_id();
		String sessionOperatorId = loginData.getOperator_id();
		List<Integer> privacies = loginData.getPrivacies();

		if (CommonStringUtil.isEmpty(submitOperatorId) || sessionOperatorId.equals(submitOperatorId)) {
			operatorProductionForm.setOperator_id(sessionOperatorId);
			doFix = true;
		} else if (privacies.contains(RvsConsts.PRIVACY_ADMIN)
			|| privacies.contains(RvsConsts.PRIVACY_LINE)
			|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT)
			|| privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)
				){
			doFix = true;
		}

		String submitDate = operatorProductionForm.getAction_time();
		String sessionDate = DateUtil.toString(new Date(), DateUtil.DATE_PATTERN);

		if (CommonStringUtil.isEmpty(submitDate) || sessionDate.equals(submitDate)) {
			operatorProductionForm.setAction_time(sessionDate);
			doFix = false;
		}
		return doFix;
	}

	private static int WORK_START_MINUTE = 525; // 8:45
	private static int WORK_END_MINUTE = 1030; // 17:10 
	private static int WORK_END_MINUTE_DRAW = 1050; // 17:30 

	public void getOperatorFeatures(ActionForm form, Map<String, Object> listResponse, SqlSession conn) {
		OperatorProductionMapper mapper = conn.getMapper(OperatorProductionMapper.class);

		OperatorProductionEntity cond = new OperatorProductionEntity();
		BeanUtil.copyToBean(form, cond, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<Map<String, Object>> operatorFeatures = mapper.getProductionFeatureByLine(cond);
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();

		String currentJobNo = null;
		Integer currentWorkCountFlg = 0;
		long pos = WORK_START_MINUTE;
		long posInadvance = WORK_START_MINUTE - 5;

		for (Map<String, Object> feature : operatorFeatures) {
			Map<String, String> retPf = new HashMap<String, String>();
			String materialId = "" + feature.get("material_id");
			String jobNo = "" + feature.get("job_no");

			retPf.put("material_id", materialId);
			retPf.put("job_no", jobNo);
			retPf.put("operator_name", "" + feature.get("operator_name"));
			retPf.put("process_code", "" + feature.get("process_code"));
			String sSorcNo = (String)feature.get("sorc_no");
			if (sSorcNo != null) {
				retPf.put("sorc_no", sSorcNo + "/n" + feature.get("model_name"));
			}
			String sReason = (String)feature.get("reason");

			if (CommonStringUtil.isEmpty(sReason)) {
				retPf.put("d_type", "0");
			} else {
				String reasonText = PauseFeatureService.getPauseReasonByCode(sReason);
				if (!CommonStringUtil.isEmpty(reasonText)) {
					retPf.put("d_type", "" + getStopReason(reasonText));
				} else {
					retPf.put("d_type", "9");
				}
			}

			// 只有一条记录时是Long??? TODO
			Integer workCountFlg = (Integer) feature.get("WORK_COUNT_FLG");
			retPf.put("work_count_flg", "" + workCountFlg);

			if (!jobNo.equals(currentJobNo)) {
				if ((WORK_END_MINUTE - pos) > 1 && currentWorkCountFlg == 1) {
					Map<String, String> retPfEnd = new HashMap<String, String>();
					retPfEnd.put("job_no", currentJobNo);
					retPfEnd.put("action_time", "" + (WORK_END_MINUTE - WORK_START_MINUTE));
					retPfEnd.put("unknownFrom", "" + (pos - posInadvance));
					retPfEnd.put("unknownTime", "" + (WORK_END_MINUTE_DRAW - pos));
					ret.add(retPfEnd);
				}

				// 建立
				currentJobNo = jobNo;
				currentWorkCountFlg = workCountFlg;
				pos = WORK_START_MINUTE;
			}

			// 是
			Long actionTime = (Long) feature.get("action_time");
			Long finishTime = (Long) feature.get("finish_time");

			if (finishTime == null) {
				continue;
			}

			Long spareMinutes = (finishTime - actionTime);
			if (spareMinutes == 0) spareMinutes = 1l;

			if (actionTime < posInadvance) actionTime = posInadvance;
			if ((actionTime - pos) > 1 && workCountFlg == 1) {
				retPf.put("unknownFrom", "" + (pos - posInadvance));
				retPf.put("unknownTime", "" + (actionTime - pos));
			}
			pos = finishTime;

			retPf.put("action_time",  Long.toString(actionTime - posInadvance));
			retPf.put("finish_time",  Long.toString(finishTime - posInadvance));
			retPf.put("spare_minutes",  Long.toString(spareMinutes));

			ret.add(retPf);
		}

		// 放入工作记录
		listResponse.put("productionFeatures", ret);
	}

	private static final int REASON_NONE = 0;
	private static final int REASON_W = 1;
	private static final int REASON_M = 2;
	private static final int REASON_H = 3;
	private static final int REASON_UNKNOWN = 9;
	private static final int REASON_WY = 6;
	private static final int REASON_MW = 7;

	private int getStopReason(String sReason) {
		if (sReason == null) 
			return REASON_NONE;
		if (sReason.startsWith("WDT")) return REASON_W;
		if (sReason.startsWith("WY")) return REASON_WY;
		if (sReason.startsWith("MD")) return REASON_M;
		if (sReason.startsWith("MW")) return REASON_MW;
		
		if (sReason.startsWith("M")) return REASON_M;
		if (sReason.startsWith("DT")) return REASON_W;
		if (sReason.startsWith("H")) return REASON_H;
		return REASON_UNKNOWN;
	}

	/**
	 * 取得最终记录的间隔时间开始
	 * @param operator_id
	 * @param listResponse
	 * @param conn
	 */
	public void getOperatorLastPause(String operator_id, Map<String, Object> listResponse, SqlSession conn) {
		OperatorProductionEntity processingPauseStart = null;

		OperatorProductionMapper mapper = conn.getMapper(OperatorProductionMapper.class);
		processingPauseStart = mapper.getProcessingPauseStart(operator_id);

		if (processingPauseStart == null) {
			// 没有记录暂停

			processingPauseStart = new OperatorProductionEntity();
			processingPauseStart.setLeak("1");
			Date lastProceedFinish = mapper.getLastProceedFinish(operator_id);
			// 有当日前一个确定的完成时间
			if (lastProceedFinish != null) {
				processingPauseStart.setPause_start_time(lastProceedFinish);
			} else {
				// 上班时间
				processingPauseStart.setPause_start_time(getNewDate(new Date(), work_start, work_start_min, 1));
			}
		} else {
			// 记录了暂停

			processingPauseStart.setLeak("0");
			String reasonText = PauseFeatureService.getPauseReason().get("" + processingPauseStart.getReason());

			String comments = processingPauseStart.getComments();

			if (CommonStringUtil.isEmpty(comments)) {
				comments = reasonText;
			} else {
				comments = reasonText + ":" + comments;
			}

			// 写入暂停理由
			processingPauseStart.setComments(comments);
		}

		OperatorProductionForm ret = new OperatorProductionForm();
		BeanUtil.copyToForm(processingPauseStart, ret, CopyOptions.COPYOPTIONS_NOEMPTY);

		listResponse.put("processingPauseStart", ret);
	}

	public OperatorProductionForm getNewPauseStart() {
		OperatorProductionForm ret = new OperatorProductionForm();
		ret.setLeak("1");
		ret.setPause_start_time(DateUtil.toString(new Date(), DateUtil.DATE_TIME_PATTERN));
		return ret;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<MonthFilesDownloadForm> getMonthFiles(String... filters){
		List<MonthFilesDownloadForm> monthFilesDownloadForms = new ArrayList<MonthFilesDownloadForm>();
		
		String filePath = PathConsts.BASE_PATH+PathConsts.REPORT+"\\works";
		File file = new File(filePath);
		
		MonthFilesDownloadForm monthFilesDownloadForm;
		//如果该文件是个目录的话
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f:files){
				monthFilesDownloadForm = new MonthFilesDownloadForm();
				if(f.isDirectory()){
					continue;
				}
				
				String fileName = f.getName();
				boolean filtered = false;
				if (filters.length == 0) {
					filtered = true;
				} else {
					for (String filter : filters) {
						if (fileName.indexOf(filter) >= 0) {
							filtered = true;
							break;
						}
					}
				}
				if (!filtered) continue;

				long fileSize = f.length();//文件的大小(字节)
				double ds = (double)fileSize/1024;//文件字节大小/1024所得的就是以kb为单位的大小
				BigDecimal bd = new BigDecimal(ds);
				double resultSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");//文件最后修改时间
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(f.lastModified());
				String fileTime = sdf.format(cal.getTime());
				
				monthFilesDownloadForm.setFile_name(fileName);
				monthFilesDownloadForm.setFile_size(resultSize+"kb");
				monthFilesDownloadForm.setFile_time(fileTime);

				monthFilesDownloadForms.add(monthFilesDownloadForm);
			}
		}
		
		//按照文件的最后编辑时间进行倒序排列
		Collections.sort(monthFilesDownloadForms, new Comparator<MonthFilesDownloadForm>() {
			@Override
			public int compare(MonthFilesDownloadForm o1, MonthFilesDownloadForm o2) {
				
				return o2.getFile_time().compareTo(o1.getFile_time());
			}
			
		});
		
		return monthFilesDownloadForms;
	}

	/**
	 * 建立间接无关的暂停时间
	 * @param operator_id
	 * @param reason
	 * @param start_time
	 * @param comments
	 * @param writableConn 读写连接，如调用方不是可写链接，传NULL
	 * @throws Exception
	 */
	public void createSimplePauseFeature(String operator_id, Integer reason, Date start_time, String comments
			, SqlSessionManager writableConn) throws Exception {
		OperatorProductionEntity entity = new OperatorProductionEntity();
		
		// 担当人 ID
		entity.setOperator_id(operator_id);
		// 理由
		entity.setReason(reason); 
		entity.setComments(comments); 
		entity.setPause_start_time(start_time);
		entity.setPause_finish_time(null);

		if (writableConn == null) {
			// 临时采用可写连接
			writableConn = RvsUtils.getTempWritableConn();

			try {
				writableConn.startManagedSession(false);

				OperatorProductionMapper dao = writableConn.getMapper(OperatorProductionMapper.class);

				dao.savePause(entity);

				writableConn.commit();
			} catch (Exception e) {
				if (writableConn != null && writableConn.isManagedSessionStarted()) {
					writableConn.rollback();
				}
			} finally {
				try {
					writableConn.close();
				} catch (Exception e) {
				} finally {
					writableConn = null;
				}
			}
		} else {
			OperatorProductionMapper dao = writableConn.getMapper(OperatorProductionMapper.class);
			dao.savePause(entity);
		}
	}

	/**
	 * 取得min（当前时间，上班时间）
	 * @return
	 */
	public Date getAutoStartTime() {
		return getNewDate(new Date(), work_start, work_start_min, 1);
	}

	public boolean checkFinishDayWork(String operator_id, SqlSession conn) {
		// 下班时间
		Date offWork = getNewDate(new Date(), work_end, work_end_min, 0);
		OperatorProductionMapper dao = conn.getMapper(OperatorProductionMapper.class);
		OperatorProductionEntity condition = new OperatorProductionEntity();
		condition.setOperator_id(operator_id);
		condition.setPause_finish_time(offWork);

		int cnt = dao.getOperatorPauseFinishPast(condition);

		return cnt > 0;
	}

	public void assertFinishPauseFeature(String operator_id, Date assert_pause_finish, SqlSessionManager conn) {
		OperatorProductionMapper mapper = conn.getMapper(OperatorProductionMapper.class);
		OperatorProductionEntity condition = new OperatorProductionEntity();
		condition.setOperator_id(operator_id);
		condition.setPause_finish_time(assert_pause_finish);
		mapper.autoFinishPauseFeature(condition);
	}
}

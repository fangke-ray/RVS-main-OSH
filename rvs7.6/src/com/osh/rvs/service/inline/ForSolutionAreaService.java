package com.osh.rvs.service.inline;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.AlarmMesssageEntity;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.inline.ForSolutionAreaEntity;
import com.osh.rvs.bean.inline.ForSolutionAreaEventEntity;
import com.osh.rvs.bean.inline.PauseFeatureEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.inline.ForSolutionAreaForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.inline.ForSolutionAreaMapper;
import com.osh.rvs.mapper.master.HolidayMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.master.PositionMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.service.AlarmMesssageService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.Converter;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateConverter;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.copy.IntegerConverter;

public class ForSolutionAreaService {

	public static final Integer ALRAM_POST = 1;
	public static final Integer ALRAM_RELEASE = 2;

	private Logger _log = Logger.getLogger(getClass());

	public List<ForSolutionAreaForm> getAreaList(ActionForm form, HttpServletRequest req, SqlSession conn,
			List<MsgInfo> errors, Integer resolveLevel, boolean getForReport) {
		List<ForSolutionAreaForm> ret = new ArrayList<ForSolutionAreaForm>();
		ForSolutionAreaForm mForm = (ForSolutionAreaForm) form;
		ForSolutionAreaEntity conditionEntity = new ForSolutionAreaEntity();
		BeanUtil.copyToBean(mForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		Converter<Integer> ic = IntegerConverter.getInstance();
		Converter<Date> dc = DateConverter.getInstance(DateUtil.DATE_PATTERN);
		String happen_time = req.getParameter("happen_time");
		String resolved = req.getParameter("resolved");  
		conditionEntity.setHappen_time(dc.getAsObject(happen_time));
		conditionEntity.setResolved(ic.getAsObject(resolved));

		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);

		List<ForSolutionAreaEntity> retBeans = mapper.search(conditionEntity);

		HolidayMapper hdao = conn.getMapper(HolidayMapper.class);
		Map<String, String> delayMap = new HashMap<String, String>();
		AlarmMesssageMapper amDao = conn.getMapper(AlarmMesssageMapper.class);

		for(ForSolutionAreaEntity retBean : retBeans) {

			ForSolutionAreaForm retForm = new ForSolutionAreaForm();
			BeanUtil.copyToForm(retBean, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			if (!getForReport || conditionEntity.getExpedition_diff() != null) {
				// 过期颜色
				if (retForm.getScheduled_date() != null) {
					if (delayMap.containsKey(retForm.getScheduled_date() + retBean.getAm_pm())) {
						retForm.setRemain_days(delayMap.get(retForm.getScheduled_date() + retBean.getAm_pm()));
					} else {
						Map<String, Object> condiMap = new HashMap<String, Object>();
						condiMap.put("scheduled_date", retBean.getScheduled_date());
						condiMap.put("am_pm", retBean.getAm_pm());
						String remain_days = hdao.compareExperial(condiMap);
						delayMap.put(retForm.getScheduled_date() + retBean.getAm_pm(), remain_days);
						retForm.setRemain_days(remain_days);
					}
				}

				if (conditionEntity.getExpedition_diff() != null) {
					if (conditionEntity.getExpedition_diff() == 1) {
						if (Integer.parseInt(retForm.getRemain_days()) >= 0)
							continue;
					} else if (conditionEntity.getExpedition_diff() == 0) {
						if (Integer.parseInt(retForm.getRemain_days()) < 0)
							continue;
					}
				}
			}

			// 如果有警告信息则读取警告信息
			if (!getForReport && "1".equals(retForm.getBreak_message())) {
				//retForm
				String material_id = retForm.getMaterial_id();
				AlarmMesssageService service = new AlarmMesssageService();
				List<AlarmMesssageEntity> messages = service.getUnredAlarmMessagesByMaterial(material_id, conn);

				List<String> spareBreakMessages = new ArrayList<String>();

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
				if (levelup) retForm.setBreak_level("ME");
			} else {
				retForm.setBreak_message("");
			}

			Integer reason = retBean.getReason();
			boolean breakType = reason != null && (reason == 2 || reason == 3 || reason == 4 || reason == 5);
			if (getForReport && breakType) {
				AlarmMesssageService service = new AlarmMesssageService();
				List<AlarmMesssageEntity> messages = 
						service.getAllAlarmMessagesByMaterialAndPosition(retForm.getMaterial_id(), retBean.getPosition_id(), conn);

				List<String> spareBreakMessages = new ArrayList<String>();

				for (AlarmMesssageEntity message : messages) {

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
								if (isEmpty(sReason)) sReason = "其它中断";
							}
						} else {
							sReason = PathConsts.POSITION_SETTINGS.getProperty("break."+ pauseEntity.getProcess_code() +"." + iReason);
						}
						spareBreakMessages.add(pauseEntity.getProcess_code() + ":" + sReason + " ");
					}
				}

				String causeMessage = CommonStringUtil.joinBy("\n", spareBreakMessages.toArray(new String[spareBreakMessages.size()]));

				String resolveMessage = service.getRedAlarmMessagesByMaterialAndPosition(retForm.getMaterial_id(), retBean.getPosition_id(), conn);
				retForm.setBreak_message(causeMessage + "\n" + resolveMessage);
			}

			ret.add(retForm);
		}

		return ret;
	}

	/**
	 * 解决中断
	 * @param form
	 * @param triggerList 
	 * @param logindata
	 * @param conn
	 * @throws Exception
	 */
	public void solve(ActionForm form, List<String> triggerList, LoginData logindata, SqlSessionManager conn) throws Exception {
		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
		
		ForSolutionAreaEntity entity = new ForSolutionAreaEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		entity.setResolver_id(logindata.getOperator_id());
	
		mapper.solve(entity);

		// 解决中断需
		triggerList.add("http://localhost:8080/rvspush/trigger/expected_finish_time/" + entity.getMaterial_id() + "/1");
	}

	public List<ForSolutionAreaEntity> checkBlock(String material_id, String position_id, String line_id, SqlSession conn) {
		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
		if ("00000000021".equals(position_id) || "00000000027".equals(position_id)) {
			return null; // TODO 客户要求移出
		}
		return mapper.checkOffline(material_id, position_id, line_id);
	}

	public ForSolutionAreaEntity checkBlockByAlarm(String alarm_messsage_id,
			SqlSessionManager conn) {
		AlarmMesssageMapper amMapper = conn.getMapper(AlarmMesssageMapper.class);
		AlarmMesssageEntity alarmMesssage = amMapper.getBreakAlarmMessageByKey(alarm_messsage_id);
		List<ForSolutionAreaEntity> rst = checkBlock(alarmMesssage.getMaterial_id(), alarmMesssage.getPosition_id(), null, conn);
		if (rst != null) {
			for (ForSolutionAreaEntity rsa : rst) {
				if (rsa.getReason() >= 2 && rsa.getReason() <= 4)
					return rsa;
			}
		}
		return null;
	}

	/**
	 * 建立中断，并通知相关人员
	 * @param material_id
	 * @param comment
	 * @param reason
	 * @param position_id
	 * @param conn
	 * @throws Exception
	 */
	public void create(String material_id, String comment, int reason, String position_id,
			SqlSessionManager conn, boolean addAlert) throws Exception {

		_log.info("material_id:" + material_id + " comment:" + comment);

		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
		ForSolutionAreaEntity entity = new ForSolutionAreaEntity();

		entity.setMaterial_id(material_id);
		entity.setReason(reason);
		entity.setPosition_id(position_id);
		entity.setComment(comment);
		mapper.create(entity);

		List<String> listOperator = new ArrayList<String>();

		listOperator.addAll(mapper.getLeadersByObject(material_id, position_id));

		listOperator.addAll(mapper.getInlineManagers());

		OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

		List<OperatorEntity> schedulers = oMapper.getOperatorWithRole(RvsConsts.ROLE_SCHEDULER);

		for (OperatorEntity scheduler : schedulers) {
			listOperator.add(scheduler.getOperator_id());
		}

		// 本人不推送
//		for (String operator : listOperator) {
//			if () {
//				break;
//			}
//		}

		String for_solution_area_key = getInsertedKey(conn);
		// 推送给所在线长/经理/计划员
		createEvent(for_solution_area_key, ForSolutionAreaService.ALRAM_POST, listOperator, conn);

		if (addAlert) {

			MaterialMapper mMapper = conn.getMapper(MaterialMapper.class);
			MaterialEntity me = mMapper.getMaterialEntityByKey(material_id);

			PositionMapper pMapper = conn.getMapper(PositionMapper.class);
			PositionEntity pe = pMapper.getPositionByID(position_id);
			
			HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
			httpclient.start();

			try {
				HttpGet request = new HttpGet(
						"http://localhost:8080/rvsTouch/beep/" + me.getSection_id() + "/" + pe.getLine_id());
				_log.info("finger:" + request.getURI());
				httpclient.execute(request, null);
			} catch (Exception e) {
			} finally {
				Thread.sleep(100);
				httpclient.shutdown();
			}
		}
	}

	public void updateToPushed(ForSolutionAreaEntity entity, SqlSessionManager conn) throws Exception {
		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
		mapper.updateToPushed(entity);

		String material_id = entity.getMaterial_id();
		String position_id = entity.getPosition_id();
		List<String> listOperator = new ArrayList<String>();

		listOperator.addAll(mapper.getLeadersByObject(material_id, position_id));

		listOperator.addAll(mapper.getInlineManagers());

		OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

		List<OperatorEntity> schedulers = oMapper.getOperatorWithRole(RvsConsts.ROLE_SCHEDULER);

		for (OperatorEntity scheduler : schedulers) {
			listOperator.add(scheduler.getOperator_id());
		}

		// 本人不推送
//		for (String operator : listOperator) {
//			if () {
//				break;
//			}
//		}

		// 推送给所在线长/经理/计划员
		createEvent(entity.getFor_solution_area_key(), 
				ForSolutionAreaService.ALRAM_POST, listOperator, conn);

		MaterialMapper mMapper = conn.getMapper(MaterialMapper.class);
		MaterialEntity me = mMapper.getMaterialEntityByKey(material_id);

		PositionMapper pMapper = conn.getMapper(PositionMapper.class);
		PositionEntity pe = pMapper.getPositionByID(position_id);
		
		HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
		httpclient.start();

		try {
			HttpGet request = new HttpGet(
					"http://localhost:8080/rvsTouch/beep/" + me.getSection_id() + "/" + pe.getLine_id());
			_log.info("finger:" + request.getURI());
			httpclient.execute(request, null);
		} catch (Exception e) {
		} finally {
			Thread.sleep(100);
			httpclient.shutdown();
		}
	}

	public void updateToAppend(ForSolutionAreaEntity entity, SqlSessionManager conn) throws Exception {
		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
		mapper.updateToAppend(entity);

		List<String> recievers = getRespon(entity.getMaterial_id(), entity.getPosition_id(), conn);

		for (String sReciever : recievers) {
			ForSolutionAreaEventEntity event = new ForSolutionAreaEventEntity();
			event.setFor_solution_area_key(entity.getFor_solution_area_key());
			event.setEvent_type(ALRAM_POST);
			event.setRed_flg(0);
			event.setReciever_id(sReciever);
			mapper.createEvent(event);
		}
		
		MaterialMapper mMapper = conn.getMapper(MaterialMapper.class);
		MaterialEntity me = mMapper.getMaterialEntityByKey(entity.getMaterial_id());

		PositionMapper pMapper = conn.getMapper(PositionMapper.class);
		PositionEntity pe = pMapper.getPositionByID(entity.getPosition_id());

		HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
		httpclient.start();

		try {
			HttpGet request = new HttpGet(
					"http://localhost:8080/rvsTouch/beep/" + me.getSection_id() + "/" + pe.getLine_id());
			_log.info("finger:" + request.getURI());
			httpclient.execute(request, null);
		} catch (Exception e) {
		} finally {
			Thread.sleep(100);
			httpclient.shutdown();
		}
	}

	private List<String> getRespon(String material_id, String position_id,
			SqlSessionManager conn) {
		List<String> ret = new ArrayList<>();
		OperatorMapper om = conn.getMapper(OperatorMapper.class);
		// 线长
		// 经理

		// 计划员
		List<OperatorEntity> schedulers = om.getOperatorWithRole(RvsConsts.ROLE_SCHEDULER);
		for (OperatorEntity scheduler : schedulers) {
			ret.add(scheduler.getOperator_id());
		}

		return ret;
	}

	/**
	 * 处理缺零件导致的待处理
	 * @param material_id
	 * @param occur_times
	 * @param operater_id
	 * @param conn
	 * @throws Exception
	 */
	public void solveBo(String material_id, Integer occur_times, String operater_id, SqlSessionManager conn) throws Exception {
		String bo_partials = "";
		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
		MaterialPartialMapper mpMapper = conn.getMapper(MaterialPartialMapper.class);

		List<ForSolutionAreaEntity> offlines = mapper.getOfflineOfMaterial(material_id);

		// 如果全发放则结束所有待处理
		for (ForSolutionAreaEntity offline : offlines) {

			Integer reason = offline.getReason();
			if (reason == 1 || (reason == 4 && occur_times > 1)) {

				// 查看全部该工程零件是否已发放
				bo_partials = mpMapper.getBoPartialOfLineOfPosition(material_id, offline.getPosition_id());

				if (isEmpty(bo_partials)) {
					offline.setResolver_id(operater_id);
					mapper.solve(offline);

					// 发送解决
					List<String> listOperator = new ArrayList<String>();

					listOperator.addAll(mapper.getLeadersByKey(offline.getFor_solution_area_key()));

					listOperator.addAll(mapper.getInlineManagers());

					OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

					List<OperatorEntity> schedulers = oMapper.getOperatorWithRole(RvsConsts.ROLE_SCHEDULER);

					for (OperatorEntity scheduler : schedulers) {
						listOperator.add(scheduler.getOperator_id());
					}

					// 本人不推送
//					for (String operator : listOperator) {
//						if () {
//							break;
//						}
//					}

					// String for_solution_area_key = getInsertedKey(conn);
					// 推送给所在线长/经理/计划员
					createEvent(offline.getFor_solution_area_key(), ForSolutionAreaService.ALRAM_RELEASE, listOperator, conn);

					List<String> triggerList = new ArrayList<String>();
					// 计算预估完成日
					triggerList.add("http://localhost:8080/rvspush/trigger/expected_finish_time/" + material_id + "/1");

					conn.commit();
					RvsUtils.sendTrigger(triggerList);
				}
			}
		}
	}

	public void solveBreak(ForSolutionAreaEntity entity, String material_id, String operator_id,
			List<String> triggerList, SqlSessionManager conn) throws Exception {
		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
//		Date happen_time = entity.getHappen_time();
//		if (happen_time != null && new Date().getTime() - happen_time.getTime() > 30 * 60 * 1000) {
			entity.setResolver_id(operator_id);
			mapper.solve(entity);
//		} else {
//			mapper.remove(entity);
//		}

		// 计算预估完成日
		triggerList.add("http://localhost:8080/rvspush/trigger/expected_finish_time/" + material_id + "/1");
	}

	/** 
	 * 未修理返还了
	 * @param material_id
	 * @param user
	 * @param conn
	 * @throws Exception
	 */
	public void solveAsStop(String material_id, LoginData user,
			SqlSessionManager conn) throws Exception {
		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
		ForSolutionAreaEntity entity = new ForSolutionAreaEntity();
		entity.setMaterial_id(material_id);
		entity.setResolver_id(user.getOperator_id());
		mapper.solveAsStop(entity);

		// 发送解决
		List<String> listOperator = new ArrayList<String>();

		OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

		List<OperatorEntity> leaders = oMapper.getOperatorWithRole(RvsConsts.ROLE_LINELEADER);

		for (OperatorEntity leader : leaders) {
			listOperator.add(leader.getOperator_id());
		}

		listOperator.addAll(mapper.getInlineManagers());

		List<OperatorEntity> schedulers = oMapper.getOperatorWithRole(RvsConsts.ROLE_SCHEDULER);

		for (OperatorEntity scheduler : schedulers) {
			listOperator.add(scheduler.getOperator_id());
		}

		// 本人不推送
//		for (String operator : listOperator) {
//			if () {
//				break;
//			}
//		}

		List<ForSolutionAreaEntity> fsaObjs = mapper.search(entity);
		for (ForSolutionAreaEntity fsaObj : fsaObjs) {
			// String for_solution_area_key = getInsertedKey(conn);
			// 推送给所在线长/经理/计划员
			createEvent(fsaObj.getFor_solution_area_key(), ForSolutionAreaService.ALRAM_RELEASE, listOperator, conn);
		}

	}

	public String getInsertedKey(SqlSessionManager conn) {
		CommonMapper mapper = conn.getMapper(CommonMapper.class);
		return mapper.getLastInsertID();
	}

	public void createEvent(String for_solution_area_key, Integer alramRelease,
			List<String> listOperator, SqlSessionManager conn) throws Exception {
		for (String operator_id : listOperator) {
			ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
			ForSolutionAreaEventEntity event = new ForSolutionAreaEventEntity();
			event.setFor_solution_area_key(for_solution_area_key);
			event.setEvent_type(alramRelease);
			event.setRed_flg(0);
			event.setReciever_id(operator_id);
			mapper.createEvent(event);
		}
	}

	/** 
	 * 结束工程内所有工位的中断
	 * @param material_id
	 * @param line_id
	 * @param user
	 * @param conn
	 * @throws Exception
	 */
	public void finishSolve(String material_id, String line_id, LoginData user, SqlSessionManager conn) throws Exception {
		// 取得NS工位
		ForSolutionAreaMapper mapper = conn.getMapper(ForSolutionAreaMapper.class);
		ForSolutionAreaEntity condEntity = new ForSolutionAreaEntity();
		condEntity.setMaterial_id(material_id);
		condEntity.setLine_id(line_id);
		List<String> listPosition = mapper.getSolvedPositionByLine(condEntity);

		// 更新所有工位的solved_time
		for (String position_id : listPosition) {
			ForSolutionAreaEntity updEntity = new ForSolutionAreaEntity();
			updEntity.setMaterial_id(material_id);
			updEntity.setPosition_id(position_id);
			updEntity.setResolver_id(user.getOperator_id());
			mapper.finishSolve(updEntity);
		}
	}

	/**
	 * 待处理一览导出
	 * @param lResultForm
	 * @return
	 * @throws Exception 
	 */
	public String createForSolutionAreaRecord(
			List<ForSolutionAreaForm> list) throws Exception {

		String cacheName ="待处理一览" + new Date().getTime() + ".xlsx";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 

		if(list != null){
			OutputStream out = null;
			
			try {
				// 定义文件
				File file = new File(cachePath);
				
				// 判断文件目录是否存在
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}

				// 文件不存在
				if(!file.exists()){
					file.createNewFile();
				}

				// 创建Excel
				SXSSFWorkbook work = new SXSSFWorkbook(1000);

				// 创建Sheet
				Sheet sheet = work.createSheet("PA 待处理一览");

				Row row = null;

				// 字体
				Font font = work.createFont();
				font.setFontHeightInPoints((short) 10);
				font.setFontName("微软雅黑");

				// 加粗字体
				Font titlefont = work.createFont();
				titlefont.setFontHeightInPoints((short)11);
				titlefont.setFontName("微软雅黑");
				titlefont.setBoldweight(Font.BOLDWEIGHT_BOLD);

				// 基本样式
				CellStyle cellStyle = work.createCellStyle();
				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				cellStyle.setFont(font);

				// 居中对齐
				CellStyle centerStyle = work.createCellStyle();
				centerStyle.cloneStyleFrom(cellStyle);
				centerStyle.setAlignment(CellStyle.ALIGN_CENTER);

//				// 右对齐
//				CellStyle rightStyle = work.createCellStyle();
//				rightStyle.cloneStyleFrom(cellStyle);
//				rightStyle.setAlignment(CellStyle.ALIGN_RIGHT);

				// 备注
				CellStyle commentStyle = work.createCellStyle();
				commentStyle.cloneStyleFrom(cellStyle);
				commentStyle.setWrapText(true);

				// 标题
				CellStyle titleStyle = work.createCellStyle();
				titleStyle.cloneStyleFrom(centerStyle);
				titleStyle.setFont(titlefont);
				
				Map<String,Integer> map = new LinkedHashMap<String,Integer>();
				
				map.put("序号", 256*5);
				map.put("修理单号", 256*10);
				map.put("机种", 256*15);
				map.put("型号", 256*15);
				map.put("机身号", 256*10);
				map.put("发生时间", 256*20);
				map.put("发生原因", 256*15);
				map.put("待解决情况", 256*30);
				map.put("解决时间", 256*20);
				map.put("解决者", 256*10);
				map.put("维修等级", 256*5);
				map.put("直送", 256*5);
				map.put("投线日期", 256*14);
				map.put("零件订购安排", 256*14);
				map.put("入库预定日", 256*14);
				map.put("零件BO", 256*10);
				map.put("维修课", 256*10);
				map.put("进展工位", 256*10);
				map.put("纳期", 256*14);
				map.put("总组出货安排", 256*14);
				map.put("加急", 256*10);
				map.put("中断信息", 256*45);
				map.put("备注", 256*45);

				//设置标题
				row = sheet.createRow(0);
				row.setHeightInPoints((short)18);

				int icol = 0;
				for(String title:map.keySet()){
					CellUtil.createCell(row, icol, title, titleStyle);
					
					// 设置列宽
					sheet.setColumnWidth(icol,map.get(title));
					
					icol ++;
				}

				sheet.createFreezePane(0, 1, 0, 1);

				Map<String, String> cacheMap = new HashMap<String, String>();

				ForSolutionAreaForm entity = null;
				for(int i = 0;i < list.size();){
					icol= 0;
					entity = list.get(i);
					
					row = sheet.createRow(++i);

					// 序号
					CellUtil.createCell(row, icol++, "" + i, cellStyle);

					// 修理单号
					CellUtil.createCell(row, icol++, entity.getSorc_no(), cellStyle);
					// 机种
					CellUtil.createCell(row, icol++, entity.getCategory_name(), cellStyle);
					// 型号
					CellUtil.createCell(row, icol++, entity.getModel_name(), cellStyle);
					// 机身号
					CellUtil.createCell(row, icol++, entity.getSerial_no(), cellStyle);
					// 发生时间
					CellUtil.createCell(row, icol++, entity.getHappen_time(), centerStyle);

					String reasonCode = entity.getReason();
					String sReason = null;

					if (cacheMap.containsKey("R" + reasonCode)) {
						sReason = cacheMap.get("R" + reasonCode);
					} else {
						sReason = CodeListUtils.getValue("offline_reason", reasonCode);
						if (!isEmpty(sReason)) {
							cacheMap.put("R" + reasonCode, sReason);
						} else sReason = "";
					}
					// 发生原因
					CellUtil.createCell(row, icol++, sReason, cellStyle);

					// 待解决情况
					CellUtil.createCell(row, icol++, entity.getComment(), cellStyle);
					// 解决时间
					CellUtil.createCell(row, icol++, entity.getSolved_time(), centerStyle);
					// 解决者
					CellUtil.createCell(row, icol++, entity.getResolver_name(), cellStyle);

					String levelCode = entity.getLevel();
					String sLevel = null;

					if (cacheMap.containsKey("L" + levelCode)) {
						sLevel = cacheMap.get("L" + levelCode);
					} else {
						sLevel = CodeListUtils.getValue("material_level", levelCode);
						if (!isEmpty(sLevel)) {
							cacheMap.put("L" + levelCode, sLevel);
						} else sLevel = "";
					}
					// 维修等级
					CellUtil.createCell(row, icol++, sLevel, centerStyle);

					// 直送
					if ("1".equals(entity.getDirect_flg())) {
						CellUtil.createCell(row, icol++, "直送", centerStyle);
					} else {
						CellUtil.createCell(row, icol++, "", centerStyle);
					}
					// 投线日期
					CellUtil.createCell(row, icol++, entity.getInline_time(), centerStyle);
					// 零件订购安排
					CellUtil.createCell(row, icol++, entity.getOrder_date(), centerStyle);
					// 入库预定日
					CellUtil.createCell(row, icol++, entity.getArrival_plan_date(), centerStyle);

					String boCode = entity.getBo_flg();
					String sBo = null;

					if (cacheMap.containsKey("B" + boCode)) {
						sBo = cacheMap.get("B" + boCode);
					} else {
						sBo = CodeListUtils.getValue("material_partial_bo_flg", boCode);
						if (!isEmpty(sBo)) {
							cacheMap.put("B" + boCode, sBo);
						} else sBo = "";
					}
					// 零件BO
					CellUtil.createCell(row, icol++, sBo, centerStyle);

					// 维修课
					CellUtil.createCell(row, icol++, entity.getSection_name(), cellStyle);
					// 进展工位
					CellUtil.createCell(row, icol++, entity.getProcess_code(), centerStyle);
					// 纳期
					CellUtil.createCell(row, icol++, entity.getScheduled_date(), centerStyle);
					// 总组出货安排
					CellUtil.createCell(row, icol++, entity.getScheduled_assign_date(), centerStyle);
					// 加急
					if ("2".equals(entity.getScheduled_expedited())) {
						CellUtil.createCell(row, icol++, "快速通道", centerStyle);
					} else if ("1".equals(entity.getScheduled_expedited())) {
						CellUtil.createCell(row, icol++, "加急", centerStyle);
					} else {
						CellUtil.createCell(row, icol++, "", centerStyle);
					}
					// 中断信息
					CellUtil.createCell(row, icol++, entity.getBreak_message(), commentStyle);
					// 备注
					CellUtil.createCell(row, icol++, entity.getComment(), commentStyle);

				}
				
				out = new FileOutputStream(file);
				work.write(out);
				work.dispose();
			}catch (Exception e) {
				throw e;
			} finally {
				if (out != null) {
					out.close();
					out = null;
				}
			}
		}
		
		return cacheName;

	}

}

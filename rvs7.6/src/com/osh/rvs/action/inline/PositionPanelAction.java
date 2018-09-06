/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：工位平台事件<br>
 * @author 龚镭敏
 * @version 1.01
 */
package com.osh.rvs.action.inline;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.arnx.jsonic.JSON;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.AlarmMesssageEntity;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.infect.CheckResultEntity;
import com.osh.rvs.bean.infect.PeriodsEntity;
import com.osh.rvs.bean.infect.PeripheralInfectDeviceEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.inline.DryingProcessForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.mapper.infect.CheckResultMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.report.FoundryMapper;
import com.osh.rvs.service.AlarmMesssageService;
import com.osh.rvs.service.CheckResultService;
import com.osh.rvs.service.MaterialPartialService;
import com.osh.rvs.service.MaterialProcessAssignService;
import com.osh.rvs.service.MaterialRemainTimeService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.OperatorProductionService;
import com.osh.rvs.service.PauseFeatureService;
import com.osh.rvs.service.PositionPlanTimeService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.inline.DryingProcessService;
import com.osh.rvs.service.inline.ForSolutionAreaService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.inline.SoloSnoutService;
import com.osh.rvs.service.partial.PartialReceptService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PositionPanelAction extends BaseAction {
	private static String WORK_STATUS_FORBIDDEN = "-1";
	private static String WORK_STATUS_PREPAIRING = "0";
	private static String WORK_STATUS_WORKING = "1";
	private static String WORK_STATUS_PAUSING = "2";
	private static String WORK_STATUS_WAITING_FOR_PARTIAL_RECEIVE = "3";
	/** 签完零件就完成 */
	private static String WORK_STATUS_CHECK_PARTIAL_THEN_FINISH = "3.9";
	private static String WORK_STATUS_PERIPHERAL_WORKING = "4";
	private static String WORK_STATUS_PERIPHERAL_PAUSING = "5";

	private static String DEVICE_TYPE_ID_OF_ENDOSCOPE = "00000000223"; // TODO

	private Logger log = Logger.getLogger(getClass());

	private PositionPanelService service = new PositionPanelService();
	private ProductionFeatureService pfService = new ProductionFeatureService();
	private PauseFeatureService bfService = new PauseFeatureService();

	/**
	 * 判断是否能进入的处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void entrance(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PositionPanelAction.entrance start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		String req_line_no = req.getParameter("line_id");
		// 取得处理状态
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		if (isEmpty(user.getLine_id())) {
			// 选择的不是本工程的作业
		} else if (req_line_no == null || !req_line_no.equals(user.getLine_id())) {
			// 进入的页面不是选择的工程
		}

		// String position_id = user.getPosition_id();

		listResponse.put("checkToken", errors); //TODO random

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.entrance end");
	}

	/**
	 * 工位画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PositionPanelAction.init start");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();
		String position_id = user.getPosition_id();
		String pxLevel = user.getPx();
		if ("4".equals(pxLevel)) pxLevel = "0"; // 超级员工不分线
		String process_code = user.getProcess_code();

//		if ("121".equals(process_code) || "131".equals(process_code) || "141".equals(process_code)
//				|| "171".equals(process_code) || "302".equals(process_code)) { // 现品管理科
//			section_id = "3"; // 全部暂定3课
//		}

		// 取得工位信息
		req.setAttribute("position", service.getPositionMap(section_id, position_id, pxLevel, conn));
		req.setAttribute("px", pxLevel);
		req.setAttribute("otherPx", service.getOtherPx(pxLevel, process_code));

		OperatorMapper operatorMapper = conn.getMapper(OperatorMapper.class);
		OperatorEntity operatorEntity = operatorMapper.getOperatorByID(user.getOperator_id());
		int work_count_flg = operatorEntity.getWork_count_flg();
		if (work_count_flg == 1) {
			// 判断是否代工中
			FoundryMapper foundryMapper = conn.getMapper(FoundryMapper.class);
			Date foundry_start_time = foundryMapper.getStartTime(user.getOperator_id());
			if (foundry_start_time != null) {
				req.setAttribute("foundry_start_time", DateUtil.toString(foundry_start_time, DateUtil.DATE_TIME_PATTERN));
			}
		}
		req.setAttribute("work_count_flg", work_count_flg);

		String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

		if (special_forward == null) {
			// 迁移到页面
			actionForward = mapping.findForward(FW_INIT);
		} else {
			if (special_forward.indexOf("peripheral") >= 0) {
				req.setAttribute("peripheral", true);
			}

			if ("result".equals(special_forward)) {
				actionForward = mapping.findForward("result");
				req.setAttribute("oManageNo", service.getManageNo(position_id,conn));
			} else if ("simple".equals(special_forward)) {
				actionForward = mapping.findForward("simple");
			} else if ("snout".equals(special_forward)) {
				actionForward = mapping.findForward("snout");
			} else if (special_forward.indexOf("use_snout") >= 0) {
				special_forward = special_forward.replaceAll(".*decom\\[(.*)\\].*", "$1");
				String skipPosition = ReverseResolution.getPositionByProcessCode(special_forward, conn);
				req.setAttribute("skip_position", skipPosition);
				actionForward = mapping.findForward("usesnout");
			} else if (special_forward.indexOf("decom") >= 0) {
				special_forward = special_forward.replaceAll(".*decom\\[(.*)\\].*", "$1");
				String skipPosition = ReverseResolution.getPositionByProcessCode(special_forward, conn);
				req.setAttribute("skip_position", skipPosition);
				actionForward = mapping.findForward("decom");
			} else {
				// 迁移到页面
				actionForward = mapping.findForward(FW_INIT);
			}
		}

		log.info("PositionPanelAction.init end");
	}

	/**
	 * 工位画面初始取值处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void jsinit(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSession conn)
			throws Exception {

		log.info("PositionPanelAction.jsinit start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();
		String position_id = user.getPosition_id();
		String line_id = user.getLine_id();
		String process_code = user.getProcess_code();
		String pxLevel = user.getPx();
		if ("4".equals(pxLevel)) pxLevel = "0"; // 超级员工不分线

		// 设定待点检信息
		CheckResultService crService = new CheckResultService();
		CheckResultMapper crMapper = conn.getMapper(CheckResultMapper.class);
		CheckResultEntity condEntity = new CheckResultEntity();
		PeriodsEntity periodsEntity = CheckResultService.getPeriodsOfDate(DateUtil.toString(new Date(), DateUtil.ISO_DATE_PATTERN), conn);
		try {
			crService.getDevices(null, section_id, null, position_id, null, line_id, condEntity, periodsEntity, conn, crMapper, -1);

			crService.getTorsionDevices(null, section_id, null, position_id, null, condEntity, periodsEntity, conn, crMapper, -1);
			crService.getElectricIronDevices(null, section_id, null, position_id, null, condEntity, periodsEntity, conn, crMapper, -1);

		} catch(Exception tex) {
			log.error("dmmm:" + tex.getMessage(), tex);
		}

		// 取得待点检信息
		String infectString = service.getInfectMessageByPosition(section_id,
				position_id, line_id, conn);
		infectString += service.getAbnormalWorkStateByOperator(user.getOperator_id(), conn);

		listResponse.put("infectString", infectString);
		if (infectString.indexOf("限制工作") >= 0) {
			listResponse.put("workstauts", WORK_STATUS_FORBIDDEN);
		} else {

			// 判断是否有特殊页面效果
			String special_forward = PathConsts.POSITION_SETTINGS
					.getProperty("page." + process_code);

			if (!"snout".equals(special_forward)) { // 非先端预制，取得等待区

				// 取得等待区一览
				listResponse.put("waitings", service.getWaitingMaterial(section_id, user.getPosition_id(), user.getLine_id(),
								user.getOperator_id(), pxLevel, process_code, conn));

				// 取得工程工位待处理容许时间（分钟）
				listResponse.put("permitMinutes", RvsUtils.getUnproceedPermit(user.getSection_name(), user.getLine_name()));
			}

			String stepOptions = "";
			// 设定正常中断选项
			String steps = PathConsts.POSITION_SETTINGS.getProperty("steps."
					+ process_code);
			if (steps != null) {
				String[] steparray = steps.split(",");
				for (String step : steparray) {
					step = step.trim();
					String stepname = PathConsts.POSITION_SETTINGS
							.getProperty("step." + process_code + "." + step);
					stepOptions += "<option value=\"" + step + "\">" + stepname
							+ "</option>";
				}
			}
			listResponse.put("stepOptions", stepOptions);

			String breakOptions = "";
			if ("121".equals(process_code) || "131".equals(process_code)
					|| "171".equals(process_code) || "251".equals(process_code)
					|| "252".equals(process_code)) { // TODO 正规化，不会中断的
			} else {
				// 设定异常中断选项
				steps = PathConsts.POSITION_SETTINGS.getProperty("break." + process_code);
				if (steps != null) {
					String[] steparray = steps.split(",");
					for (String step : steparray) {
						step = step.trim();
						String stepname = PathConsts.POSITION_SETTINGS
								.getProperty("break." + process_code + "." + step);
						breakOptions += "<option value=\"" + step + "\">"
								+ stepname + "</option>";
					}
				}
				// 设定一般中断选项
				breakOptions += CodeListUtils.getSelectOptions("break_reason",
						null);
			}
			listResponse.put("breakOptions", breakOptions);

			// 设定暂停选项
			String pauseOptions = "";

			pauseOptions += PauseFeatureService.getPauseReasonSelectOptions();
			listResponse.put("pauseOptions", pauseOptions);
			listResponse.put("pauseComments", PauseFeatureService.getPauseReasonSelectComments());

			String triggerMaterial_id = "";
			// 判断是否有在进行中的维修对象
			ProductionFeatureEntity workingPf = service
					.getWorkingOrSupportingPf(user, conn);
			// 进行中的话
			if (workingPf != null) {
				triggerMaterial_id = workingPf.getMaterial_id();

				if (RvsConsts.OPERATE_RESULT_SUPPORT == workingPf
						.getOperate_result()) {
					MsgInfo msginfo = new MsgInfo();
					msginfo.setErrcode("info.linework.supportingRemain");
					msginfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES
							.getMessage("info.linework.supportingRemain"));
					infoes.add(msginfo);
					listResponse.put("redirect", "support.do");
				} else {
					PartialReceptService prService = new PartialReceptService();

					List<MaterialPartialDetailEntity> wentities = prService.getPartialsForPosition(
							workingPf.getMaterial_id(), workingPf.getPosition_id(), conn);
					if (wentities == null || wentities.size() == 0) {

						// 取得作业信息
						service.getProccessingData(listResponse, workingPf.getMaterial_id(), workingPf, user, conn);

						if ("use_snout".equals(special_forward)) {
							// TODO listResponse.put("light", workingPf.get);
						}

						// 取得本次返工第一次作业 的开始时间
						listResponse.put("action_time", 
								DateUtil.toString(pfService.getFirstPaceOnRework(workingPf, conn).getAction_time(), "HH:mm:ss"));

						boolean infectFinishFlag = true;
						if ("peripheral".equals(special_forward)) {

							List<PeripheralInfectDeviceEntity> resultEntities = new ArrayList<PeripheralInfectDeviceEntity>();
							// 取得周边设备检查使用设备工具 
							infectFinishFlag = service.getPeripheralData(workingPf.getMaterial_id(), workingPf, resultEntities, conn);

							if (resultEntities != null && resultEntities.size() > 0) {
								listResponse.put("peripheralData", resultEntities);
							}
						}
						if (!infectFinishFlag) {
							listResponse.put("workstauts", WORK_STATUS_PERIPHERAL_WORKING);
						} else {
							// 取得工程检查票
							if (!"simple".equals(special_forward)
									&& !"result".equals(special_forward)) {
								PositionPanelService.getPcses(listResponse, workingPf, user.getLine_id(), conn);
							}

							// 页面设定为编辑模式
							listResponse.put("workstauts", WORK_STATUS_WORKING);
						}

						// 取得维修对象备注信息
						MaterialService ms = new MaterialService();
						ms.getMaterialComment(workingPf.getMaterial_id(), listResponse, conn);

					} else {
						List<MaterialPartialDetailForm> pForms = new ArrayList<MaterialPartialDetailForm>();
						BeanUtil.copyToFormList(wentities, pForms, CopyOptions.COPYOPTIONS_NOEMPTY,
								MaterialPartialDetailForm.class);
						listResponse.put("mpds", pForms);
						listResponse.put("workstauts", WORK_STATUS_WAITING_FOR_PARTIAL_RECEIVE);
					}
				}

				// TODO 零件签收中

				// 取得烘干信息
				DryingProcessService dpService = new DryingProcessService();
				List<DryingProcessForm> dryProcesses = dpService.getJobsOnMaterial(workingPf.getMaterial_id()
						, workingPf.getSection_id(), workingPf.getPosition_id(), conn);
				if (dryProcesses.size() > 0) {
					listResponse.put("dryProcesses", dryProcesses);
				}
			} else {
				// 暂停中的话
				// 判断是否有在进行中的维修对象
				ProductionFeatureEntity pauseingPf = service.getPausingPf(user,
						conn);
				if (pauseingPf != null) {
					triggerMaterial_id = pauseingPf.getMaterial_id();

					// 取得作业信息
					service.getProccessingData(listResponse,
							pauseingPf.getMaterial_id(), pauseingPf, user, conn);

					// spent_mins
					// listResponse.put("spent_mins", (Integer)
					// listResponse.get("spent_mins") +
					// pauseingPf.getUse_seconds() / 60);
					listResponse.put("action_time", DateUtil.toString(
							pauseingPf.getAction_time(), "HH:mm:ss"));

					boolean infectFinishFlag = true;
					if ("peripheral".equals(special_forward)) {


						List<PeripheralInfectDeviceEntity> resultEntities = new ArrayList<PeripheralInfectDeviceEntity>();
						// 取得周边设备检查使用设备工具 
						infectFinishFlag = service.getPeripheralData(pauseingPf.getMaterial_id(), pauseingPf, resultEntities, conn);

						if (resultEntities != null && resultEntities.size() > 0) {
							listResponse.put("peripheralData", resultEntities);
						}
					}
					if (!infectFinishFlag) {						
						listResponse.put("workstauts", WORK_STATUS_PERIPHERAL_PAUSING);
					} else {
						// 取得工程检查票
						if (!"simple".equals(special_forward)
								&& !"result".equals(special_forward)) {
							PositionPanelService.getPcses(listResponse, pauseingPf,
									user.getLine_id(), conn);
						}

						// 页面设定为编辑模式
						listResponse.put("workstauts", WORK_STATUS_PAUSING);
					}

					// 取得维修对象备注信息
					MaterialService ms = new MaterialService();
					ms.getMaterialComment(pauseingPf.getMaterial_id(), listResponse, conn);

					// 取得烘干信息
					DryingProcessService dpService = new DryingProcessService();
					List<DryingProcessForm> dryProcesses = dpService.getJobsOnMaterial(pauseingPf.getMaterial_id()
							, pauseingPf.getSection_id(), pauseingPf.getPosition_id(), conn);
					if (dryProcesses.size() > 0) {
						listResponse.put("dryProcesses", dryProcesses);
					}

					OperatorProductionService opService = new OperatorProductionService();
					opService.getOperatorLastPause(user.getOperator_id(), listResponse, conn);
				} else {
					// 准备中
					listResponse.put("workstauts", WORK_STATUS_PREPAIRING);

					OperatorProductionService opService = new OperatorProductionService();
					opService.getOperatorLastPause(user.getOperator_id(), listResponse, conn);
				}
			}

			// 取得正在或已完成的维修对象的下位触发工位
			listResponse.put("fingers",
					session.getAttribute(RvsConsts.JUST_WORKING));
			listResponse.put("past_fingers",
					session.getAttribute(RvsConsts.JUST_FINISHED));

			if (!isEmpty(triggerMaterial_id)) {
				//查询维修对象所在工程是否排计划
				boolean isExistInPlan = false;
				if ("00000000001".equals(user.getSection_id())) {
					// 分解,只看261工位
					if ("00000000012".equals(user.getLine_id()) && !"261".equals(user.getProcess_code())) {
						isExistInPlan = false;
					} else {
						MaterialRemainTimeService materialRemainTimeService = new MaterialRemainTimeService();
						isExistInPlan = materialRemainTimeService.getMaterialPlan(triggerMaterial_id, user, listResponse, conn);
					}
				}

				listResponse.put("isExistInPlan", isExistInPlan);
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.jsinit end");
	}

	/**
	 * 扫描开始
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void doscan(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.scan start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		List<String> triggerList = new ArrayList<String>();

		String material_id = req.getParameter("material_id");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String process_code = user.getProcess_code();

		String dryingConfirmed = req.getParameter("confirmed");

		// 判断维修对象在等待区，并返回这一条作业信息
		ProductionFeatureEntity waitingPf = service.checkMaterialId(material_id, dryingConfirmed, user, errors, conn);

		// 停止之前的暂停
		bfService.finishPauseFeature(null, null, null, user.getOperator_id(), conn);

		if (errors.size() == 0) {

			// 开始作业
			waitingPf.setOperator_id(user.getOperator_id());
			pfService.startProductionFeature(waitingPf, conn);

			// 工位首次开始作业
			if (waitingPf.getOperate_result() == 0){
				MaterialService ms = new MaterialService();
				MaterialForm mEntity = ms.loadSimpleMaterialDetail(conn, waitingPf.getMaterial_id());
//				String level = mEntity.getLevel();
//				boolean isLightFix = level != null &&
//						("9".equals(level.substring(0, 1)));
				boolean isLightFix = RvsUtils.isLightFix(mEntity.getLevel());
				if (isLightFix) { // 小修理
					pfService.removeWaiting(material_id, waitingPf.getPosition_id(), conn);
				}
			} else if ("true".equals(dryingConfirmed)) {
				// 结束烘干
				DryingProcessService dpSevice = new DryingProcessService();
				dpSevice.finishDryingProcess(material_id, waitingPf.getPosition_id(), conn);
			}

			// 工位特殊动作
			service.executeActionByPosition(waitingPf, user, triggerList, conn);

			// 如果等待中信息是暂停中，则结束掉暂停记录(有可能已经被结束)
			// 只要开始做，就结束掉本人所有的暂停信息。
			bfService.finishPauseFeature(material_id, user.getSection_id(), user.getPosition_id(), user.getOperator_id(), conn);

			if (triggerList.size() > 0) {
				conn.commit();
				RvsUtils.sendTrigger(triggerList);
			}

			// 零件签收确认
			PartialReceptService prService = new PartialReceptService();
			List<MaterialPartialDetailEntity> wentities = prService
					.getPartialsForPosition(waitingPf.getMaterial_id(), waitingPf.getPosition_id(), conn);
			if (wentities == null || wentities.size() == 0) {

				service.getProccessingData(listResponse, material_id, waitingPf, user, conn);

				// 判断是否有特殊页面效果
				String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

				boolean infectFinishFlag = true;
				if ("peripheral".equals(special_forward)) {
					List<PeripheralInfectDeviceEntity> resultEntities = new ArrayList<PeripheralInfectDeviceEntity>();
					// 取得周边设备检查使用设备工具 
					infectFinishFlag = service.getPeripheralData(material_id, waitingPf, resultEntities, conn);

					if (resultEntities != null && resultEntities.size() > 0) {
						listResponse.put("peripheralData", resultEntities);
					}
				}

				if (!infectFinishFlag) {
					listResponse.put("workstauts", WORK_STATUS_PERIPHERAL_WORKING);
				} else {
					// 取得工程检查票
					if (!"simple".equals(special_forward) && !"result".equals(special_forward)) {
						waitingPf.setProcess_code(process_code);
						PositionPanelService.getPcses(listResponse, waitingPf, user.getLine_id(), conn);
					}

					// 页面设定为编辑模式
					listResponse.put("workstauts", WORK_STATUS_WORKING);
				}

				// 取得维修对象备注信息
				MaterialService ms = new MaterialService();
				ms.getMaterialComment(material_id, listResponse, conn);

			} else {
				List<MaterialPartialDetailForm> pForms = new ArrayList<MaterialPartialDetailForm>();
				BeanUtil.copyToFormList(wentities, pForms, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialPartialDetailForm.class);
				listResponse.put("mpds", pForms);
				listResponse.put("workstauts", WORK_STATUS_WAITING_FOR_PARTIAL_RECEIVE);
			}

			// 取得烘干信息
			DryingProcessService dpService = new DryingProcessService();
			List<DryingProcessForm> dryProcesses = dpService.getJobsOnMaterial(material_id
					, user.getSection_id(), user.getPosition_id(), conn);
			if (dryProcesses.size() > 0) {
				listResponse.put("dryProcesses", dryProcesses);
			}

			//查询维修对象所在工程是否排计划
			boolean isExistInPlan = false;
			if ("00000000001".equals(user.getSection_id())) {
				// 分解,只看261工位
				if ("00000000012".equals(user.getLine_id()) && !"261".equals(user.getProcess_code())) {
					isExistInPlan = false;
				} else {
					MaterialRemainTimeService materialRemainTimeService = new MaterialRemainTimeService();
					isExistInPlan = materialRemainTimeService.getMaterialPlan(material_id, user, listResponse, conn);
				}
			}

			listResponse.put("isExistInPlan", isExistInPlan);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.scan end");
	}

	/**
	 * 暂停再开
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void doendpause(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.doendpause start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("material_id");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String section_id = user.getSection_id();
		String process_code = user.getProcess_code();

		// 得到暂停的维修对象，返回这一条作业信息
		ProductionFeatureEntity workwaitingPf = service.checkPausingMaterialId(material_id, user, errors, conn);

		if (errors.size() == 0) {
			service.getProccessingData(listResponse, material_id, workwaitingPf, user, conn);

			workwaitingPf.setOperate_result(RvsConsts.OPERATE_RESULT_WORKING);
			pfService.changeWaitProductionFeature(workwaitingPf, conn);

			// 只要开始做，就结束掉本人所有的暂停信息。
			bfService.finishPauseFeature(material_id, section_id, user.getPosition_id(), user.getOperator_id(), conn);

			// 判断是否有特殊页面效果
			String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

//			// 取得工程检查票
//			if (!"simple".equals(special_forward) && !"result".equals(special_forward)) {
//				workwaitingPf.setProcess_code(process_code);
//				PositionPanelService.getPcses(listResponse, workwaitingPf, user.getLine_id(), conn);
//			}

			listResponse.put("action_time", DateUtil.toString(workwaitingPf.getAction_time(), "HH:mm:ss"));

			String workstauts = req.getParameter("workstauts");
			if ("peripheral".equals(special_forward) && WORK_STATUS_PERIPHERAL_PAUSING.equals(workstauts)) {
				listResponse.put("workstauts", WORK_STATUS_PERIPHERAL_WORKING);
			} else {
				listResponse.put("workstauts", WORK_STATUS_WORKING);
			}

			// 取得维修对象备注信息
			MaterialService ms = new MaterialService();
			ms.getMaterialComment(material_id, listResponse, conn);
		}

		user.setSection_id(section_id);
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		listResponse.put("dryProcesses", "follow"); // 沿用现有的干燥信息

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.doendpause end");
	}

	/**
	 * 作业暂停
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void dopause(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.dopause start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String process_code = user.getProcess_code();
		String comments = bfService.checkPauseForm(req.getParameter("comments"), errors);

		if (errors.size() == 0) {
			// 取得当前作业中作业信息
			ProductionFeatureEntity workingPf = service.getWorkingPf(user, conn);

			// 作业信息状态改为，暂停
			workingPf.setUse_seconds(null);
			workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_PAUSE);
			pfService.finishProductionFeature(workingPf, conn);

			// 制作暂停信息
			bfService.createPauseFeature(workingPf, req.getParameter("reason"), comments, null, conn);

			service.getProccessingData(listResponse, workingPf.getMaterial_id(), workingPf, user, conn);

			// 操作者暂停
			// 根据作业信息生成新的等待作业信息－－有开始时间（仅作标记用，重开时需要覆盖掉），说明是操作者原因暂停，将由本人重开。
			pfService.pauseToSelf(workingPf, conn);

			listResponse.put("action_time", DateUtil.toString(workingPf.getAction_time(), "HH:mm:ss"));

			// 判断是否有特殊页面效果
			String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);
			String workstauts = req.getParameter("workstauts");
			if ("peripheral".equals(special_forward) && WORK_STATUS_PERIPHERAL_WORKING.equals(workstauts)) {
				listResponse.put("workstauts", WORK_STATUS_PERIPHERAL_PAUSING);
			} else {
				listResponse.put("workstauts", WORK_STATUS_PAUSING);
			}

			OperatorProductionService opService = new OperatorProductionService();
			opService.getOperatorLastPause(user.getOperator_id(), listResponse, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.dopause end");
	}

	/**
	 * 作业中断
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void dobreak(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.dobreak start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		List<String> triggerList = new ArrayList<String>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String sReason = req.getParameter("reason");
		log.info("REASON:" + sReason);
		Integer iReason = null;

		try {
			iReason = Integer.parseInt(sReason.trim());
		} catch (Exception e) {
			// 选择不正常的中断代码
			log.error("ERROR:" + e.getMessage());
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("reason");
			msgInfo.setErrcode("validator.invalidParam.invalidIntegerValue");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidIntegerValue", "中断代码"));
			errors.add(msgInfo);
		}

		DryingProcessService dpSevice = new DryingProcessService();
		// 烘干作业数据
		if (iReason == 99) {
			dpSevice.checkDryingProcess(req, errors);
		}

		// 取得当前作业中作业信息
		ProductionFeatureEntity workingPf = service.getWorkingPf(user, conn);
		String material_id = workingPf.getMaterial_id();

		if (errors.size() == 0) {
			service.checkSupporting(material_id, workingPf.getPosition_id(), errors, conn);
		}

		if (errors.size() == 0) {

			// 中断警报序号
			String alarm_messsage_id = null;

			if (iReason <= 20) { // 异常中断
				// 制作中断警报
				AlarmMesssageService amservice = new AlarmMesssageService();
				AlarmMesssageEntity amEntity = amservice.createBreakAlarmMessage(workingPf);
				alarm_messsage_id = amservice.createAlarmMessage(amEntity, conn, false, triggerList);

				// 加入等待处理区域
				ForSolutionAreaService fsoService = new ForSolutionAreaService();
				String reasonText = sReason;
				// 不良理由
				if (iReason < 10) {
					reasonText = req.getParameter("comments");
				} else if (iReason < 10) {
					reasonText = CodeListUtils.getValue("break_reason", "0" + iReason);
				} else {
					reasonText = PathConsts.POSITION_SETTINGS.getProperty("break."+ user.getProcess_code() +"." + iReason);
				}
				fsoService.create(material_id, reasonText, 2, user.getPosition_id(), conn, false);

				triggerList.add("http://localhost:8080/rvspush/trigger/delete_finish_time/"
						+ material_id + "/" + user.getLine_id());

//				if (!"00000000014".equals(user.getLine_id())) {
//					triggerList.add("http://localhost:8080/rvspush/trigger/delete_finish_time/"
//							+ material_id + "/" + "00000000014");
//				}
			}

			// 制作暂停信息
			bfService.createPauseFeature(workingPf, sReason, req.getParameter("comments"), alarm_messsage_id, conn);

			if (iReason > 70) { // 业务流程-非直接工步操作

				// 作业信息状态改为，中断
				workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_BREAK);
				workingPf.setUse_seconds(null);
				workingPf.setPcs_inputs(req.getParameter("pcs_inputs"));
				workingPf.setPcs_comments(req.getParameter("pcs_comments"));

				pfService.finishProductionFeature(workingPf, conn);

				// 根据作业信息生成新的等待作业信息－－无开始时间，说明进行非直接工步操作，回到等待区，可由他人接手
				pfService.pauseToNext(workingPf, conn);

				// 通知 TODO
			} else if (iReason <= 20) { // 不良中断
				// 作业信息状态改为，中断
				workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_BREAK);
				workingPf.setUse_seconds(null);
				
				// 特殊工位需要工程检查票 TODO

				pfService.finishProductionFeature(workingPf, conn);

				// 根据作业信息生成新的中断作业信息
				pfService.breakToNext(workingPf, conn);

				// 通知 TODO

			} else {
				log.error(user.getName() + "在" + user.getProcess_code() + "工位发生中断,但是前台提交了暂停理由" + iReason);
				pfService.pauseToSelf(workingPf, conn); // 为 TODO
			}

			// 建立烘干作业数据
			if (iReason == 99) {
				dpSevice.createDryingProcess(material_id, req, conn);
			}

			OperatorProductionService opService = new OperatorProductionService();
			listResponse.put("processingPauseStart", opService.getNewPauseStart());

		}

		if (triggerList.size() > 0 && errors.size() == 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.dobreak end");
	}

	/**
	 * 作业完成
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void dofinish(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.dofinish start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		List<String> triggerList = new ArrayList<String>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得当前作业中作业信息
		ProductionFeatureEntity workingPf = service.getWorkingPf(user, conn); 
		// 没有进行中的作业，请刷新页面确认。
		if (workingPf == null) {
			MsgInfo info = new MsgInfo();
			info.setErrcode("info.linework.workingLost");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
			infoes.add(info);
		} else {

			PositionPlanTimeService pptS = new PositionPlanTimeService();
			pptS.checkPositionDelay(req, workingPf, infoes, user, conn, listResponse);

			// 检查对应辅助是否完成
			service.checkSupporting(workingPf.getMaterial_id(), workingPf.getPosition_id(), infoes, conn);
	
			// 检查工程检查票是否全填写
			service.checkPcsEmpty(req.getParameter("pcs_inputs"), infoes);
	
			// 检查工程是否全部完成
//			service.checkLineOver(workingPf, infoes, conn);
	
			// TODO (workingPf.getLevel() == 9 || workingPf.getLevel() == 91 || workingPf.getLevel() == 92 || workingPf.getLevel() == 93);
			MaterialService ms = new MaterialService();
			MaterialForm mEntity = ms.loadSimpleMaterialDetail(conn, workingPf.getMaterial_id());
//			String level = mEntity.getLevel();
//			boolean isLightFix = level != null &&
//					("9".equals(level.substring(0, 1))); 
			boolean isLightFix = RvsUtils.isLightFix(mEntity.getLevel());
	
			// 检查零件是否全部签收
			String process_code = user.getProcess_code();
			// 判断是否有特殊页面效果
			String special_forward = PathConsts.POSITION_SETTINGS
					.getProperty("page." + process_code);
			boolean use_snout = (special_forward != null && special_forward.indexOf("use_snout") >= 0);
	
			if (!isLightFix && ("331".equals(process_code) 
					|| "242".equals(process_code)
					|| ("252".equals(process_code) && "EndoEye".equals(mEntity.getCategory_id())) // TODO
					)) {
	
				// info.partial.withoutOrder
				MaterialPartialService mps = new MaterialPartialService();
				MaterialPartialForm mp = mps.loadMaterialPartial(conn, workingPf.getMaterial_id(), 1);
				if (mp == null || 
						(("8".equals(mp.getBo_flg()) || "9".equals(mp.getBo_flg())) && !use_snout )
						) {
					// 如果没订购零件不能结束
					// 如果没有任何发放不能结束
					MsgInfo info = new MsgInfo();
					info.setErrcode("info.partial.withoutOrder");
					info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.withoutOrder"));
					infoes.add(info);
				} else {
	
					// 检查零件是否全部签收
					PartialReceptService prService = new PartialReceptService();
		
					List<MaterialPartialDetailEntity> wentities = prService
							.getPartialsForPosition(workingPf.getMaterial_id(), workingPf.getPosition_id(), conn, use_snout);
		
					if (wentities == null || wentities.size() == 0) {
					} else {
						// 重新等待零件确认
						List<MaterialPartialDetailForm> pForms = new ArrayList<MaterialPartialDetailForm>();
						BeanUtil.copyToFormList(wentities, pForms, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialPartialDetailForm.class);
						listResponse.put("mpds", pForms);
						listResponse.put("workstauts", WORK_STATUS_WAITING_FOR_PARTIAL_RECEIVE);
						listResponse.put("notMatch", "1");
		
						int count = 0;
						for (MaterialPartialDetailEntity wentitie : wentities) {
							count += wentitie.getWaiting_receive_quantity();
						}
						MsgInfo info = new MsgInfo();
						info.setErrcode("info.partial.lineWaiting");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.lineWaiting", count	));
						infoes.add(info);
					}
	
					if (use_snout) {
						// 检查安全库存
						SoloSnoutService soloSnoutService = new SoloSnoutService();
						soloSnoutService.checkBenchmark(mEntity.getModel_id(), conn);
					}
				}
			}

			OperatorProductionService opService = new OperatorProductionService();
			listResponse.put("processingPauseStart", opService.getNewPauseStart());
		}

		// 计算一下总工时：
		/// 取得本次工时
//		Integer use_seconds = workingPf.getUse_seconds();
//
//		/// 加上本次返工内本工位所用全部时间
//		use_seconds += service.getTotalTimeByRework(workingPf, conn);

		if (infoes.size() == 0) {
			Integer use_seconds = service.getTotalTimeByRework(workingPf, conn);
	
			// 作业信息状态改为，作业完成
			workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
			workingPf.setUse_seconds(use_seconds);
			workingPf.setPcs_inputs(req.getParameter("pcs_inputs"));
			workingPf.setPcs_comments(req.getParameter("pcs_comments"));
			pfService.finishProductionFeature(workingPf, conn);

			// 本工位结束
			triggerList.add("http://localhost:8080/rvspush/trigger/out/" + workingPf.getPosition_id() + "/" + user.getSection_id() + "/" + user.getOperator_id());

			// 启动下个工位
			try {
				List<String> fingerList = pfService.fingerNextPosition(workingPf.getMaterial_id(), workingPf, conn, triggerList, true);

				String fingers = pfService.getFingerString(workingPf.getMaterial_id(), fingerList, conn, true);

				// 下个工位移动信息处理
				listResponse.put("past_fingers", fingers);
				session.setAttribute(RvsConsts.JUST_FINISHED, fingers);
				session.removeAttribute(RvsConsts.JUST_WORKING);

			} catch (Exception e) {
				if (e.getMessage() == null || e.getMessage().length() == 0) {
					throw e;
				} else {
					MsgInfo info = new MsgInfo();
					info.setErrmsg(e.getMessage());
					infoes.add(info);
				}
				conn.rollback();
			}

			// 通知 TODO
		}

		if (triggerList.size() > 0 && infoes.size() == 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.dofinish end");
	}

	/* 以下为批量特型 */

	/**
	 * 工位画面初始取值处理-消毒灭菌
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void jsinitf(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PositionPanelAction.jsinitf start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();
		String position_id = user.getPosition_id();
		String line_id = user.getLine_id();
		String process_code = user.getProcess_code();

		// 取得待点检信息
		String infectString = service.getInfectMessageByPosition(section_id,
				position_id, line_id, conn);
		infectString += service.getAbnormalWorkStateByOperator(user.getOperator_id(), conn);

		listResponse.put("infectString", infectString);
		if (infectString.indexOf("限制工作") >= 0) {
			listResponse.put("workstauts", WORK_STATUS_FORBIDDEN);
		} else {
	
			// 取得等待区一览
			listResponse.put("waitings",
					service.getWaitingMaterial(section_id, user.getPosition_id(), user.getLine_id(),
							user.getOperator_id(), user.getPx(), process_code, conn));
	
			// 取得现在处理中的批量
			service.searchWorkingBatch(listResponse, user, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.jsinitf end");
	}

	@Privacies(permit={0})
	public void doscanf(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.scanf start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("material_id");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String section_id = user.getSection_id();

		// 判断维修对象在等待区，并返回这一条作业信息
		ProductionFeatureEntity waitingPf = service.checkMaterialId(material_id, "true", user, errors, conn);

		if (errors.size() == 0) {
			// 停止之前的暂停
			bfService.finishPauseFeature(null, null, null, user.getOperator_id(), conn);

			service.getProccessingData(listResponse, material_id, waitingPf, user, conn);

			// 作业信息状态改为，批量作业中
			ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
			waitingPf.setOperator_id(user.getOperator_id());
			dao.startBatchProductionFeature(waitingPf);

			// 如果等待中信息是暂停中，则结束掉暂停记录(有可能已经被结束)
			// 只要开始做，就结束掉本人所有的暂停信息。
			bfService.finishPauseFeature(material_id, user.getSection_id(), user.getPosition_id(), user.getOperator_id(), conn);

			// 取得现在处理中的批量
			service.searchWorkingBatch(listResponse, user, conn);
		}

		user.setSection_id(section_id);
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.scanf end");
	}

	@Privacies(permit={0})
	public void dofinishf(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.dofinishf start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<String> triggerList = new ArrayList<String>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得当前作业中作业信息
		List<ProductionFeatureEntity> workingPfs = service.getWorkingPfs(user, conn);

		String sPcs_inputs = req.getParameter("pcs_inputs");
		@SuppressWarnings("unchecked")
		Map<String, LinkedHashMap<String, String>> jsonPcs_inputs = JSON.decode(sPcs_inputs, Map.class);

		for (ProductionFeatureEntity workingPf : workingPfs) {

			// 计算一下总工时：
			Integer use_seconds = 0;
			if ("00000000010".equals(workingPf.getPosition_id())) {
				/// 取得本次工时
				String sUse_seconds = RvsUtils.getZeroOverLine("_default", null, user, user.getProcess_code());
				try {
					use_seconds = Integer.parseInt(sUse_seconds) * 60;
				} catch (Exception e){
				}

				// 作业信息状态改为，作业完成
				workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
				workingPf.setUse_seconds(use_seconds);

				sPcs_inputs = JSON.encode(jsonPcs_inputs.get(workingPf.getMaterial_id()));
				workingPf.setPcs_inputs(sPcs_inputs);
				workingPf.setPcs_comments(null);
				pfService.finishProductionFeatureSetFinish(workingPf, conn);
			} else if ("00000000011".equals(workingPf.getPosition_id())) {
				use_seconds = service.getTotalTimeByRework(workingPf, conn);

				// 作业信息状态改为，作业完成
				workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
				workingPf.setUse_seconds(use_seconds);

				sPcs_inputs = JSON.encode(jsonPcs_inputs.get(workingPf.getMaterial_id()));
				workingPf.setPcs_inputs(sPcs_inputs);
				workingPf.setPcs_comments(null);
				pfService.finishProductionFeature(workingPf, conn);
			}

			// 启动下个工位
			pfService.fingerNextPosition(workingPf.getMaterial_id(), workingPf, conn, triggerList);
		}

		// 通知
		if (triggerList.size() > 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		OperatorProductionService opService = new OperatorProductionService();
		listResponse.put("processingPauseStart", opService.getNewPauseStart());

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.dofinishf end");
	}

	/* 以下为211特型 */
	/**
	 * 检查可预先跳转
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void checkProcess(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("PositionPanelAction.checkProcess start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		String materialId = req.getParameter("material_id");
		String positionId = req.getParameter("position_id");

		if (materialId == null) {
			log.error("materialId is empty!");
		} else {
			MaterialService mService = new MaterialService();
			MaterialEntity mEntity = mService.loadMaterialDetailBean(conn, materialId);
			if (mEntity == null) {
				log.error("mEntity is empty! >> " + materialId);
			} else {
				// 小修理不得跳转
				Integer level = mEntity.getLevel();
				if (RvsUtils.isLightFix(level)) {
					listResponse.put("position_exist", "-1");
				} else {

					LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

					// 取得当前作业中作业信息
					ProductionFeatureEntity workingPf = service.getWorkingPf(user, conn);
					String sRework = null;
					if (workingPf != null ) {
						sRework = "" + workingPf.getRework();
					}

					ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

					// 本次返工中已交给目标工位
					if (!dao.checkPositionDid(materialId, positionId, null, sRework)) {
						listResponse.put("position_exist", "1");
						if (workingPf.getRework() > 0) {
							// 目标工位已经完成
							if (dao.checkPositionDid(materialId, positionId, "" + RvsConsts.OPERATE_RESULT_FINISH, null)) {
								listResponse.put("position_exist", "0");
							}
						}
					} else {
						listResponse.put("position_exist", "0");
					}
				}
			}
		}

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.checkProcess end");
	}

	/**
	 * 执行预先跳转
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void doProcess(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.doProcess start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<String> triggerList = new ArrayList<String>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String position_id = req.getParameter("position_id");

		// 优先触发工位
		ProductionFeatureEntity workingPf =  service.getProcessingPf(user, conn);

		workingPf.setPosition_id(position_id);
		workingPf.setSection_id(user.getSection_id());

		pfService.fingerSpecifyPosition(workingPf.getMaterial_id(), true, workingPf, triggerList, conn);

		if (triggerList.size() > 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.doProcess end");
	}

	/* 以下为2期 */
	/**
	 * 检查可预先跳转
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void doPointOut(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.getNexts start");
		Map<String, Object> jsonResponse = new HashMap<String, Object>();

		// 检查发生错误时报告错误信息
		jsonResponse.put("errors", new ArrayList<MsgInfo>());

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);


		// 取得当前作业中作业信息
		ProductionFeatureEntity workingPf = service.getWorkingPf(user, conn);

//		Integer use_seconds = service.getTotalTimeByRework(workingPf, conn);
//		

		if (workingPf != null) {
			MaterialService ms = new MaterialService();
			MaterialEntity mBean = ms.loadMaterialDetailBean(conn, workingPf.getMaterial_id());

//			boolean isLightFix = (mBean.getLevel() != null) 
//					&& (mBean.getLevel() == 9 || mBean.getLevel() == 91 || mBean.getLevel() == 92 || mBean.getLevel() == 93); 
			boolean isLightFix = RvsUtils.isLightFix(mBean.getLevel());

			// 小修理 
			String lightFix = "";
			if (isLightFix) {

				MaterialProcessAssignService mpas = new MaterialProcessAssignService();
				String fingers = "当前小修理的工位流程为：" 
						+ CommonStringUtil.nullToAlter(mpas.getLightFixFlowByMaterial(workingPf.getMaterial_id(), workingPf.getProcess_code(), conn), "(暂未设定)");

				String lightFixStr = mpas.getLightFixesByMaterial(workingPf.getMaterial_id(), conn);
				if (!isEmpty(lightFixStr)) {
					lightFix = "当前小修理的修理内容为：" + lightFixStr;
				}

				if (!isEmpty(lightFix)) {
					fingers = lightFix + "<BR>" + fingers;
				}

				jsonResponse.put("fingers", fingers);
			} else {

				// 作业信息状态改为，作业完成
				workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
		//		workingPf.setUse_seconds(use_seconds);
				pfService.finishProductionFeature(workingPf, conn);

				// 启动下个工位
				List<String> fingerList = pfService.fingerNextPosition(workingPf.getMaterial_id(), workingPf, conn,
						new ArrayList<String>(), false);

				String fingers = pfService.getFingerString(workingPf.getMaterial_id(), fingerList, conn, false);

				session.setAttribute(RvsConsts.JUST_WORKING, fingers);
				jsonResponse.put("fingers", fingers);
			}

			jsonResponse.put("past_fingers", session.getAttribute(RvsConsts.JUST_FINISHED));
		}
		// 返回Json格式响应信息
		returnJsonResponse(res, jsonResponse);

		// 得到结果后回滚
		conn.rollback();

		log.info("PositionPanelAction.getNexts end");
	}

	/**
	 * 当日报表生成申请
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void makeReport(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PositionPanelAction.makeReport start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String position_id = user.getPosition_id(); 

		// 工位临时报表
		HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
		httpclient.start();
		try { 
			if ("00000000009".equals(position_id)) {
	            HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/preport/accept/00000000009");
				log.info("finger:"+request.getURI());
	            httpclient.execute(request, null);
			} else if ("00000000010".equals(position_id)) {
	            HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/preport/disinfect/00000000010");
				log.info("finger:"+request.getURI());
	            httpclient.execute(request, null);
			} else if ("00000000011".equals(position_id)) {
	            HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/preport/sterilize/00000000011");
				log.info("finger:"+request.getURI());
	            httpclient.execute(request, null);
			} else if ("00000000047".equals(position_id)) {
	            HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/preport/shipping/00000000047");
				log.info("finger:"+request.getURI());
	            httpclient.execute(request, null);
			}
        } catch (Exception e) {
		} finally {
			Thread.sleep(100);
			httpclient.shutdown();
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.makeReport end");
	}

	/**
	 * 工位零件使用
	 */
	@Privacies(permit={0})
	public void doPartialUse(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelAction.doPartialUse start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		PartialReceptService prService = new PartialReceptService();

		// 完成按钮确定
		String finish = req.getParameter("finish");

		// 签收所选零件
		prService.updatePartialRecept(req, conn);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 判断是否有在进行中的维修对象
		ProductionFeatureEntity workingPf = service.getWorkingOrSupportingPf(user, conn);

		// TODO (workingPf.getLevel() == 9 || workingPf.getLevel() == 91 || workingPf.getLevel() == 92 || workingPf.getLevel() == 93);
		MaterialService ms = new MaterialService();
		MaterialForm mEntity = ms.loadSimpleMaterialDetail(conn, workingPf.getMaterial_id());
//		String level = mEntity.getLevel();
//		boolean isLightFix = level != null &&
//				("9".equals(level.substring(0, 1))); 
		boolean isLightFix = RvsUtils.isLightFix(mEntity.getLevel());

		String process_code = user.getProcess_code();
		if (isLightFix &&
				(!"331".equals(process_code) && !"242".equals(process_code))) {
			List<MaterialPartialDetailEntity> wentities = prService
					.getPartialsForPosition(workingPf.getMaterial_id(), workingPf.getPosition_id(), conn);

			if (wentities == null || wentities.size() == 0) {
				// 已经完成OK
				// 页面设定为编辑模式

				// 判断是否有特殊页面效果
				String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

				// 取得作业信息
				service.getProccessingData(listResponse, workingPf.getMaterial_id(), workingPf, user, conn);

				// 取得工程检查票
				if (!"simple".equals(special_forward) && !"result".equals(special_forward)) {
					PositionPanelService.getPcses(listResponse, workingPf, user.getLine_id(), conn);
				}

				listResponse.put("workstauts", WORK_STATUS_WORKING);
			} else {
				// 重新等待零件确认
				List<MaterialPartialDetailForm> pForms = new ArrayList<MaterialPartialDetailForm>();
				BeanUtil.copyToFormList(wentities, pForms, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialPartialDetailForm.class);
				listResponse.put("mpds", pForms);
				listResponse.put("workstauts", WORK_STATUS_WAITING_FOR_PARTIAL_RECEIVE);
				listResponse.put("notMatch", "1");
			}
		} else if ("1".equals(finish)) {
			listResponse.put("workstauts", WORK_STATUS_CHECK_PARTIAL_THEN_FINISH);
		} else {
			// 取得作业信息
			service.getProccessingData(listResponse, workingPf.getMaterial_id(), workingPf, user, conn);

			//查询维修对象所在工程是否排计划
			boolean isExistInPlan = false;
			if ("00000000001".equals(user.getSection_id())) {
				// 分解,只看261工位
				if ("00000000012".equals(user.getLine_id()) && !"261".equals(user.getProcess_code())) {
					isExistInPlan = false;
				} else {
					MaterialRemainTimeService materialRemainTimeService = new MaterialRemainTimeService();
					isExistInPlan = materialRemainTimeService.getMaterialPlan(workingPf.getMaterial_id(), user, listResponse, conn);
				}
			}

			listResponse.put("isExistInPlan", isExistInPlan);

			// 判断是否有特殊页面效果
			String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

			boolean infectFinishFlag = true;
			if ("peripheral".equals(special_forward)) {
				List<PeripheralInfectDeviceEntity> resultEntities = new ArrayList<PeripheralInfectDeviceEntity>();
				// 取得周边设备检查使用设备工具 
				infectFinishFlag = service.getPeripheralData(workingPf.getMaterial_id(), workingPf, resultEntities, conn);

				if (resultEntities != null && resultEntities.size() > 0) {
					listResponse.put("peripheralData", resultEntities);
				}
			}

			if (!infectFinishFlag) {
				listResponse.put("workstauts", WORK_STATUS_PERIPHERAL_WORKING);
			} else {
				// 取得工程检查票
				PositionPanelService.getPcses(listResponse, workingPf, user.getLine_id(), conn);

				// 已经完成OK
				// 页面设定为编辑模式
				listResponse.put("workstauts", WORK_STATUS_WORKING);
			}

			// 取得维修对象备注信息
			ms.getMaterialComment(workingPf.getMaterial_id(), listResponse, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", infoes);
		listResponse.put("dryProcesses", "follow"); // 沿用现有的干燥信息

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelAction.doPartialUse end");
	}


	/**
	 * 零件清点不正确，报告线长
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doCallLeaderOfPartialMismatch(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSessionManager conn) throws Exception {

		log.info("PositionPanelAction.doCallLeaderOfPartialMismatch start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		List<String> triggerList = new ArrayList<String>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得当前作业中作业信息
		ProductionFeatureEntity workingPf = service.getWorkingPf(user, conn);

		String sReason = "08";

		// 中断警报序号
		String alarm_messsage_id = null;

		// 制作中断警报
		AlarmMesssageService amservice = new AlarmMesssageService();
		AlarmMesssageEntity amEntity = amservice.createBreakAlarmMessage(workingPf, RvsConsts.WARNING_REASON_PARTIAL_ON_POISTION);
		alarm_messsage_id = amservice.createAlarmMessage(amEntity, conn, false, triggerList);

		// 加入等待处理区域
		String reasonText = "在"+user.getProcess_code()+"工位的零件签收可能不符，请前去确认。";

//		ForSolutionAreaService fsoService = new ForSolutionAreaService();
//		fsoService.create(workingPf.getMaterial_id(), reasonText, 2,
//				user.getPosition_id(), conn, false);

		// 制作暂停信息
		bfService.createPauseFeature(workingPf, sReason, reasonText, alarm_messsage_id, conn);

		// 作业信息状态改为，中断
		workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_BREAK);
		workingPf.setUse_seconds(null);

		// 特殊工位需要工程检查票 
		pfService.finishProductionFeature(workingPf, conn);

		// 根据作业信息生成新的中断作业信息
		pfService.breakToNext(workingPf, conn);

		callbackResponse.put("errors", infoes);

		if (triggerList.size() > 0 && infoes.size() == 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		returnJsonResponse(res, callbackResponse);
		log.info("PositionPanelAction.doCallLeaderOfPartialMismatch end");
	}

	public void refreshWaitings(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("PositionPanelAction.refreshWaitings start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String pxLevel = user.getPx();
		if ("4".equals(pxLevel)) pxLevel = "0"; // 超级员工不分线

		callbackResponse.put("waitings",
				service.getWaitingMaterial(user.getSection_id(), user.getPosition_id(), user.getLine_id(),
						user.getOperator_id(), pxLevel, user.getProcess_code(), conn));
		// 取得工程工位待处理容许时间（分钟）
		callbackResponse.put("permitMinutes", RvsUtils.getUnproceedPermit(user.getSection_name(), user.getLine_name()));

		callbackResponse.put("errors", infoes);

		returnJsonResponse(res, callbackResponse);
		log.info("PositionPanelAction.refreshWaitings end");
	}

	public void pxChange(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("PositionPanelAction.pxChange start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String setPx = req.getParameter("set_px");
		user.setPx(setPx);
		session.setAttribute(RvsConsts.SESSION_USER, user);
		callbackResponse.put("redirect", "position_panel.do");

		callbackResponse.put("errors", infoes);

		returnJsonResponse(res, callbackResponse);
		log.info("PositionPanelAction.pxChange end");
	}

	/**
	 * 周边设备检查使用设备工具-点检
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void deviceCheck(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("PositionPanelAction.deviceCheck start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		CheckResultMapper crMapper = conn.getMapper(CheckResultMapper.class);
		CheckResultEntity condEntity = new CheckResultEntity();
		condEntity.setManage_id((String) req.getParameter("manage_id"));
		//获取当前时间
		SimpleDateFormat df = new SimpleDateFormat(DateUtil.ISO_DATE_PATTERN);
		Calendar cal = Calendar.getInstance();
		if (DEVICE_TYPE_ID_OF_ENDOSCOPE.equals((String) req.getParameter("device_type_id"))) {
			//周边设备（内镜）
			cal.add(Calendar.DATE, -7);
		}
		condEntity.setCheck_confirm_time_start(df.parse(df.format(cal.getTime())));
		int result_cnt = crMapper.getWeekCheck(condEntity);
		if (result_cnt > 0) {
			listResponse.put("deviceCheck", "OK");
		} else {
			listResponse.put("deviceCheck", "NG");
		}
		
		listResponse.put("errors", infoes);

		returnJsonResponse(res, listResponse);
		log.info("PositionPanelAction.deviceCheck end");
	}

	/**
	 * 周边设备检查使用设备工具-点检完成
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doFinishcheck(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSessionManager conn) throws Exception {

		log.info("PositionPanelAction.doFinishcheck start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 取得用户信息
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		service.finishcheck(req, user, conn);

		// 取得进行中的维修对象
		ProductionFeatureEntity workingPf = service.getWorkingOrSupportingPf(user, conn);

		// 取得作业信息
		service.getProccessingData(listResponse, workingPf.getMaterial_id(), workingPf, user, conn);

		// 取得工程检查票
		PositionPanelService.getPcses(listResponse, workingPf, user.getLine_id(), conn);

		listResponse.put("workstauts", WORK_STATUS_WORKING);
		// 通知报价界面不需要刷新基础信息
		listResponse.put("finish_check", "1");

		listResponse.put("errors", infoes);

		returnJsonResponse(res, listResponse);
		log.info("PositionPanelAction.doFinishcheck end");
	}

	/**
	 * 代工状态切换
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doFoundryChange(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSessionManager conn) throws Exception {

		log.info("PositionPanelAction.doFoundryChange start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 取得用户信息
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		FoundryMapper mapper = conn.getMapper(FoundryMapper.class);
		Map<String,String> map = new HashMap<String,String>();

		String user_working_status = req.getParameter("user_working_status");
		if ("1".equals(user_working_status)) {
			// 判断是否存在没有结束的代工时间
			Date foundry_start_time = mapper.getStartTime(user.getOperator_id());
			if (foundry_start_time != null) {
				map.put("operator_id", user.getOperator_id());
				map.put("start_time", DateUtil.toString(foundry_start_time, DateUtil.DATE_TIME_PATTERN));
				mapper.delete(map);
			}
			// 代工时间开始
			map.put("operator_id", user.getOperator_id());
			String nowDate = DateUtil.toString(new Date(), DateUtil.DATE_TIME_PATTERN);
			map.put("start_time", nowDate);
			mapper.insert(map);

			callbackResponse.put("foundry_start_time", nowDate);
		} else if ("2".equals(user_working_status)) {
			// 代工时间结束
			map.put("operator_id", user.getOperator_id());
			map.put("start_time", req.getParameter("foundry_start_time"));
			mapper.update(map);
		}

		callbackResponse.put("errors", infoes);

		returnJsonResponse(res, callbackResponse);
		log.info("PositionPanelAction.doFoundryChange end");
	}
}

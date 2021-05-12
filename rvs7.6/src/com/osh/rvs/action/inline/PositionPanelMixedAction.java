/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：工位平台事件<br>
 * @author 龚镭敏
 * @version 8.04
 */
package com.osh.rvs.action.inline;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.AlarmMesssageEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.infect.PeripheralInfectDeviceEntity;
import com.osh.rvs.bean.inline.DryingProcessEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.inline.WaitingEntity;
import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.inline.DryingProcessForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.service.AlarmMesssageService;
import com.osh.rvs.service.DevicesManageService;
import com.osh.rvs.service.MaterialRemainTimeService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.OperatorProductionService;
import com.osh.rvs.service.PauseFeatureService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.equipment.DeviceJigLoanService;
import com.osh.rvs.service.inline.DryingProcessService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.inline.SoloSnoutService;
import com.osh.rvs.service.partial.ComponentManageService;
import com.osh.rvs.service.partial.PartialReceptService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;


public class PositionPanelMixedAction extends BaseAction {

	private static String WORK_STATUS_FORBIDDEN = "-1";
	private static String WORK_STATUS_PREPAIRING = "0";
	private static String WORK_STATUS_WORKING = "1";
	private static String WORK_STATUS_PAUSING = "2";
	private static String WORK_STATUS_WAITING_FOR_PARTIAL_RECEIVE = "3";
	/** 签完零件就完成 */
	private static String WORK_STATUS_CHECK_PARTIAL_THEN_FINISH = "3.9";
	private static String WORK_STATUS_PERIPHERAL_WORKING = "4";
	private static String WORK_STATUS_PERIPHERAL_PAUSING = "5";

	private Logger log = Logger.getLogger(getClass());

	private PositionPanelService service = new PositionPanelService();
	private ProductionFeatureService pfService = new ProductionFeatureService();
	private SoloSnoutService sservice = new SoloSnoutService();
	private PauseFeatureService bfService = new PauseFeatureService();

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
	public void jsinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PositionPanelMixedAction.jsinit start");
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

		// 是否虚拟组工位
		String sGroupPositionId = user.getGroup_position_id();
		String infectString = "";

		// 虚拟组不在加载时判断待点检
		if (sGroupPositionId == null) {

			infectString = service.checkPositionInfectWorkOnPass(section_id, position_id, line_id, user.getOperator_id(), conn, listResponse);

		}

		// 判断操作者异常作业状态
		infectString += service.getAbnormalWorkStateByOperator(user.getOperator_id(), conn);

		listResponse.put("infectString", infectString);

		if (infectString.indexOf("限制工作") >= 0) {
			listResponse.put("workstauts", WORK_STATUS_FORBIDDEN);
		} else {

			// 判断是否有特殊页面效果
			String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

			if (sGroupPositionId == null) { // 非虚拟组

				List<WaitingEntity> waitings = new ArrayList<WaitingEntity>();
				// 取得等待区一览 comp
				waitings = sservice.getWaitingMaterial(section_id, user.getPosition_id(),
						user.getOperator_id(), process_code, (pxLevel.equals("1") ? 0 : 1), conn);
				waitings.addAll(service.getWaitingMaterial(section_id, user.getPosition_id(), user.getLine_id(),
						user.getOperator_id(), pxLevel, process_code, conn));
				// 取得等待区一览
				listResponse.put("waitings", waitings);
			} else { // 虚拟组 TODO

				// 取得完成区一览
				List<WaitingEntity> completes = service.getGroupCompleteMaterial(user.getSection_id(), user.getGroup_position_id(), 
						user.getPx(), conn);
				// 取得等待区一览
				listResponse.put("waitings", service.getGroupWaitingMaterial(section_id, user.getGroup_position_id(), user.getLine_id(),
								user.getOperator_id(), pxLevel, completes, conn));
				listResponse.put("completes", completes);
			}

			// 取得工程工位待处理容许时间（分钟）
			listResponse.put("permitMinutes", RvsUtils.getUnproceedPermit(user.getSection_name(), user.getLine_name()));

			// 设定正常中断选项
			listResponse.put("stepOptions", service.getStepOptions(process_code));

			// 设定异常中断选项
			listResponse.put("breakOptions", service.getBreakOptions(process_code));

			// 设定暂停选项
			String pauseOptions = "";

			pauseOptions += PauseFeatureService.getPauseReasonSelectOptions();
			listResponse.put("pauseOptions", pauseOptions);
			listResponse.put("pauseComments", PauseFeatureService.getPauseReasonSelectComments());

			// 取得组装进行记录
			SoloProductionFeatureEntity workingSpf = sservice.checkWorkingPfServiceRepair(user.getOperator_id(), position_id, conn);

			// 进行中的话
			if (workingSpf != null) {
				SoloProductionFeatureEntity workingPf = workingSpf;
				if (RvsConsts.OPERATE_RESULT_SUPPORT == workingPf.getOperate_result()) {
					MsgInfo msginfo = new MsgInfo();
					msginfo.setErrcode("info.linework.supportingRemain");
					msginfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.supportingRemain"));
					infoes.add(msginfo);
					listResponse.put("redirect", "support.do");
				} else if (RvsConsts.OPERATE_RESULT_WORKING == workingPf.getOperate_result()) {
					// 取得作业信息
					// getProccessingData(listResponse, workingPf.getMaterial_id(), workingPf, user, conn);
	
					// 取得工程检查票
					PositionPanelService.getSoloPcses(listResponse, workingPf, user.getLine_id(), conn);
	
					// 取得本组件第一次作业 的开始时间 TODO
					listResponse.put("action_time", DateUtil.toString(sservice.getFirstPaceOnRework(workingPf, conn), "HH:mm:ss"));
					listResponse.put("spent_mins", sservice.getTotalTime(workingPf, conn) / 60);
	
					listResponse.put("snout_origin", sservice.getSnoutOriginNoBySerialNo(workingPf.getSerial_no() ,conn));
					listResponse.put("leagal_overline", RvsUtils.getZeroOverLine(workingPf.getModel_name(), null, user, "301"));
	
					// 页面设定为编辑模式
					listResponse.put("workstauts", "1");

					// 进行对象信息
					MaterialForm mform = new MaterialForm();
					mform.setModel_id(workingPf.getModel_id());
					mform.setModel_name(workingPf.getModel_name());
					mform.setSerial_no(workingPf.getSerial_no());
					mform.setSorc_no("-");
					listResponse.put("mform", mform);
				} else if (RvsConsts.OPERATE_RESULT_PAUSE == workingPf.getOperate_result()) {
					// 暂停中的话

					// 取得工程检查票
					PositionPanelService.getSoloPcses(listResponse, workingPf, user.getLine_id(), conn);
	
					//spent_mins
					// listResponse.put("spent_mins", (Integer) listResponse.get("spent_mins") + pauseingPf.getUse_seconds() / 60);
					listResponse.put("action_time", DateUtil.toString(workingPf.getAction_time(), "HH:mm:ss"));
					listResponse.put("spent_mins", sservice.getTotalTime(workingPf, conn) / 60);
	
					listResponse.put("leagal_overline", RvsUtils.getZeroOverLine(workingPf.getModel_name(), null, user, "301"));
	
					// 页面设定为编辑模式
					listResponse.put("workstauts", "2");
	
					OperatorProductionService opService = new OperatorProductionService();
					opService.getOperatorLastPause(user.getOperator_id(), listResponse, conn);

					// 进行对象信息
					MaterialForm mform = new MaterialForm();
					mform.setModel_id(workingPf.getModel_id());
					mform.setModel_name(workingPf.getModel_name());
					mform.setSerial_no(workingPf.getSerial_no());
					mform.setSorc_no("-");
					listResponse.put("mform", mform);
				}

				// 取得烘干信息
				DryingProcessService dpService = new DryingProcessService();
				List<DryingProcessForm> dryProcesses = dpService.getJobsOnModel(workingPf.getModel_id()
						, workingPf.getSection_id(), workingPf.getPosition_id(), conn);
				if (dryProcesses.size() > 0) {
					listResponse.put("dryProcesses", dryProcesses);
				}

				String fingers = (String) session.getAttribute(RvsConsts.JUST_WORKING);
				if (fingers != null) {
					if (fingers.indexOf("<") >= 0) {
						ComponentManageService cmService = new ComponentManageService();
						List<ComponentManageEntity> l = cmService.getBySerialNo(workingSpf.getSerial_no(), conn);
						if (l.size() > 0) {
							listResponse.put("stock_code", l.get(0).getStock_code());
						}
					}

					// 取得正在或已完成的维修对象的下位触发工位
					listResponse.put("fingers", fingers);
				}

				listResponse.put("past_fingers",
						session.getAttribute(RvsConsts.JUST_FINISHED));

			} else {
				String triggerMaterial_id = "";
				// 判断是否有在进行中的维修对象
				ProductionFeatureEntity workingPf = service.getWorkingOrSupportingPf(user, conn);

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
						// 移动操作者所在工位
						if (sGroupPositionId != null) {
							if (!position_id.equals(workingPf.getPosition_id())) {
								user.setPosition_id(workingPf.getPosition_id());
								user.setProcess_code(workingPf.getProcess_code());
								user.setPosition_name(workingPf.getPosition_name());

								// 设定正常中断选项
								listResponse.put("stepOptions", service.getStepOptions(workingPf.getProcess_code()));

								// 设定异常中断选项
								listResponse.put("breakOptions", service.getBreakOptions(workingPf.getProcess_code()));
								session.setAttribute(RvsConsts.SESSION_USER, user);
							}
						}

						PartialReceptService prService = new PartialReceptService();

						// 工位上的未签收零件
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
					ProductionFeatureEntity pauseingPf = service.getPausingPf(user, conn);
					if (pauseingPf != null) {
						// 移动操作者所在工位
						if (sGroupPositionId != null) {
							if (!position_id.equals(pauseingPf.getPosition_id())) {
								user.setPosition_id(pauseingPf.getPosition_id());
								user.setProcess_code(pauseingPf.getProcess_code());
								user.setPosition_name(pauseingPf.getPosition_name());

								// 设定正常中断选项
								listResponse.put("stepOptions", service.getStepOptions(pauseingPf.getProcess_code()));

								// 设定异常中断选项
								listResponse.put("breakOptions", service.getBreakOptions(pauseingPf.getProcess_code()));
								session.setAttribute(RvsConsts.SESSION_USER, user);
							}
						}

						triggerMaterial_id = pauseingPf.getMaterial_id();

						// 取得作业信息
						service.getProccessingData(listResponse, pauseingPf.getMaterial_id(), pauseingPf, user, conn);

						// spent_mins
						// listResponse.put("spent_mins", (Integer)
						// listResponse.get("spent_mins") +
						// pauseingPf.getUse_seconds() / 60);
						listResponse.put("action_time", DateUtil.toString(pauseingPf.getAction_time(), "HH:mm:ss"));

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

						// 重置工位选择
						if (user.getGroup_position_id() != null)
							user.setPosition_id(user.getGroup_position_id());
					}
				}

				// 取得设备工具的安全手册信息
				DevicesManageService dmS = new DevicesManageService();
				listResponse.put("position_hcsgs", dmS.getOfPositionHazardousCautionsAndSafetyGuide(section_id, position_id, conn));

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

			// 判断作业者是否借用了设备工具
			if (!PositionService.getPositionUnitizeds(conn).containsKey(position_id)) {
				if (session.getAttribute("DJ_LOANING") != null) {
					DeviceJigLoanService djlService = new DeviceJigLoanService();
					String loaning = djlService.checkLoaningNowText(user.getOperator_id(), conn);
					if (loaning != null) {
						listResponse.put("djLoaning", loaning);
					}
				}
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelMixedAction.jsinit end");
	}

	@Privacies(permit={0})
	public void dostart(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelMixedAction.start start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String model_id = req.getParameter("model_id");
		String model_name = req.getParameter("model_name").replace(' ', (char)0x20); // TODO 20 -> 3F???
		String serial_no = req.getParameter("serial_no");

		String dryingConfirmed = req.getParameter("confirmed");

		SoloProductionFeatureEntity pfBean = null;

		if (CommonStringUtil.isEmpty(model_id)) {
			MsgInfo msg = new MsgInfo();
			msg.setComponentid("model_id");
			msg.setErrcode("validator.required");
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "维修对象型号"));
			errors.add(msg);
		}
		if (CommonStringUtil.isEmpty(serial_no)) {
			MsgInfo msg = new MsgInfo();
			msg.setComponentid("snout_no");
			msg.setErrcode("validator.required");
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "序列号"));
			errors.add(msg);
		} else {
			if (serial_no.length() != 13) {
				MsgInfo msg = new MsgInfo();
				msg.setComponentid("snout_no");
				msg.setErrcode("validator.invalidParam.invalidMaxLengthValue");
				msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidJustLengthValue", "序列号", "13"));
				errors.add(msg);
			}

			pfBean = sservice.getCompPositionDetailBean(serial_no, user.getSection_id(), user.getPosition_id(), conn);
			if (pfBean.getAction_time() != null) {
				MsgInfo msg = new MsgInfo();
				msg.setComponentid("snout_no");
				msg.setErrcode("info.linework.workingLost");
				msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
				errors.add(msg);
			}
		}

		if (errors.size() == 0) {

			// 如果等待中信息是暂停中，则结束掉暂停记录(有可能已经被结束)
			// 只要开始做，就结束掉本人所有的暂停信息。
			bfService.finishPauseFeature(null, user.getSection_id(), user.getPosition_id(), user.getOperator_id(), serial_no, conn);

			sservice.startProductionFeature(pfBean, user.getOperator_id(), conn);

			// 取得工程检查票
			PositionPanelService.getSoloPcses(listResponse, pfBean, user.getLine_id(), conn);

			DryingProcessService dpService = new DryingProcessService();

			// TODO 查不到前台有什么问题，因此先查一下是否有烘干记录
			// 结束烘干
			String orginMaterialId = null;

			if (pfBean.getPace() > 0 && !"true".equals(dryingConfirmed)) {
				ComponentManageService cmService = new ComponentManageService();
				List<ComponentManageEntity> cmList = cmService.getBySerialNo(serial_no, conn);
				if (cmList.size() > 0) {
					orginMaterialId = cmList.get(0).getOrigin_material_id();
				}

				DryingProcessEntity dpInfo = dpService.getDryingProcessByMaterialInPositionEntity(orginMaterialId, user.getPosition_id() ,conn);
				if (dpInfo != null) {
					dryingConfirmed = "true";
					log.error("Front put wrong state " + orginMaterialId);
				}
			}

			if (pfBean.getPace() > 0 && "true".equals(dryingConfirmed)) {
				// 结束烘干
				// 取得订购来源
				if (orginMaterialId == null) {
					ComponentManageService cmService = new ComponentManageService();
					List<ComponentManageEntity> cmList = cmService.getBySerialNo(serial_no, conn);
					if (cmList.size() > 0) {
						orginMaterialId = cmList.get(0).getOrigin_material_id();
					}
				}
				dpService.finishDryingProcess(orginMaterialId, user.getPosition_id(), conn);
			}

			// 取得全部烘干任务信息
			List<DryingProcessForm> dryProcesses = dpService.getJobsOnModel(pfBean.getModel_id()
					, pfBean.getSection_id(), pfBean.getPosition_id(), conn);
			if (dryProcesses.size() > 0) {
				listResponse.put("dryProcesses", dryProcesses);
			}

			listResponse.put("workstauts", WORK_STATUS_WORKING);
		}

		// 进行对象信息
		MaterialForm mform = new MaterialForm();
		mform.setModel_id(model_id);
		mform.setModel_name(model_name);
		mform.setSerial_no(serial_no);
		mform.setSorc_no("-");
		listResponse.put("mform", mform);

		if (pfBean == null || pfBean.getPace() == 0) {
			listResponse.put("spent_mins", "0");
		} else {
			listResponse.put("spent_mins", sservice.getTotalTime(pfBean, conn) / 60);
		}

		listResponse.put("leagal_overline", RvsUtils.getZeroOverLine(model_name, null, user, user.getProcess_code()));

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelMixedAction.start end");
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
		log.info("PositionPanelMixedAction.dopause start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得当前作业中作业信息
		SoloProductionFeatureEntity condition = new SoloProductionFeatureEntity();
		condition.setOperator_id(user.getOperator_id());
		condition.setAction_time_null(0);
		condition.setFinish_time_null(1);
		condition.setUsed(0);

		// 取得当前作业中作业信息
		SoloProductionFeatureEntity soloWorkingPf = sservice.checkWorkingPfServiceRepair(user.getOperator_id(), null, conn);

		if (soloWorkingPf != null) {

			// 制作暂停信息
			bfService.createPauseFeature(soloWorkingPf, req.getParameter("reason"), req.getParameter("comments"), user.getSection_id(), null, conn);

			// 根据作业信息生成新的等待作业信息－－有开始时间（仅作标记用，重开时需要覆盖掉），说明是操作者原因暂停，将由本人重开。
			sservice.pauseToSelf(soloWorkingPf, conn);

			listResponse.put("action_time", DateUtil.toString(soloWorkingPf.getAction_time(), "HH:mm:ss"));
			String leagal_overline = RvsUtils.getZeroOverLine(soloWorkingPf.getModel_name(), null, user, null);
			listResponse.put("spent_mins", sservice.getTotalTime(soloWorkingPf, conn) / 60);
			listResponse.put("leagal_overline", leagal_overline);

			listResponse.put("workstauts", "2");
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelMixedAction.dopause end");
	}

	@Privacies(permit={0})
	public void doendpause(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelMixedAction.doendpause start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String serial_no = req.getParameter("serial_no");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 得到暂停的维修对象，返回这一条作业信息
		SoloProductionFeatureEntity workwaitingPf = service.checkPausingSerialNo(serial_no, user, errors, conn);

		if (errors.size() == 0) {
			// getProccessingData(listResponse, material_id, workwaitingPf, user, conn);
			// 只要开始做，就结束掉本人所有的暂停信息。
			bfService.finishPauseFeature(null, user.getSection_id(), user.getPosition_id(), user.getOperator_id(), 
					serial_no, conn);

			// 作业信息状态改为，作业中
			sservice.pauseWaitProductionFeature(workwaitingPf, conn);

			String leagal_overline = RvsUtils.getZeroOverLine(workwaitingPf.getModel_name(), null, user, null);
			listResponse.put("spent_mins", sservice.getTotalTime(workwaitingPf, conn) / 60);
			listResponse.put("leagal_overline", leagal_overline);
			listResponse.put("action_time", DateUtil.toString(workwaitingPf.getAction_time(), "HH:mm:ss"));

			listResponse.put("workstauts", WORK_STATUS_WORKING);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		listResponse.put("dryProcesses", "follow"); // 沿用现有的干燥信息

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelMixedAction.doendpause end");
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
		log.info("PositionPanelMixedAction.dobreak start");
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
			// 维修对象不在用户所在等待区
			log.error("ERROR:" + e.getMessage());
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("reason");
			msgInfo.setErrcode("validator.invalidParam.invalidIntegerValue");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidIntegerValue", "中断代码"));
			errors.add(msgInfo);
		}

		DryingProcessService dpSevice = new DryingProcessService();
		// 烘干作业数据
		String originMaterialId = null;
		if (iReason == 99) {
			dpSevice.checkDryingProcess(req, errors);
			ComponentManageService cmService = new ComponentManageService();
			List<ComponentManageEntity> l = cmService.getBySerialNo(req.getParameter("serial_no"), conn);
			if (l == null || l.size() == 0) {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("serial_no");
				msgInfo.setErrmsg("此组件的序列号不能用于烘干作业。");
				errors.add(msgInfo);
			} else {
				originMaterialId = l.get(0).getOrigin_material_id();
				if ("00000000000".equals(originMaterialId)) {
					MsgInfo msgInfo = new MsgInfo();
					msgInfo.setComponentid("serial_no");
					msgInfo.setErrmsg("此组件的序列号不能用于烘干作业。");
					errors.add(msgInfo);
				}
			}
		}

		if (errors.size() == 0) {
			// 取得当前作业中作业信息
			SoloProductionFeatureEntity soloWorkingPf = sservice.checkWorkingPfServiceRepair(user.getOperator_id(), null, conn);

			if (soloWorkingPf != null) {
				ProductionFeatureEntity workingPf = new ProductionFeatureEntity();
				workingPf.setLine_id(user.getLine_id());
				workingPf.setSection_id(user.getSection_id());
				workingPf.setOperator_id(user.getOperator_id());
				workingPf.setPosition_id(user.getPosition_id());
				workingPf.setSerial_no(soloWorkingPf.getSerial_no());

				String alarm_messsage_id = null;

				if (iReason <= AlarmMesssageService.REASON_CODE_BREAK_ULIMIT) {
					// 制作中断警报
					AlarmMesssageService amservice = new AlarmMesssageService();
					AlarmMesssageEntity amEntity = amservice.createSoloBreakAlarmMessage(workingPf);
					amservice.createAlarmMessage(amEntity, conn, false, triggerList);

					// 取得插入的中断警报序号
					CommonMapper cDao = conn.getMapper(CommonMapper.class);
					alarm_messsage_id = cDao.getLastInsertID();
				}


				// 制作暂停信息
				bfService.createPauseFeature(workingPf, req.getParameter("reason"), req.getParameter("comments"), alarm_messsage_id, conn
						, soloWorkingPf.getSerial_no());

				if (iReason > 70) { // 业务流程-非直接工步操作

					// 作业信息状态改为，中断
					soloWorkingPf.setPcs_inputs(req.getParameter("pcs_inputs"));
					soloWorkingPf.setPcs_comments(req.getParameter("pcs_comments"));
					soloWorkingPf.setOperator_id(user.getOperator_id());
					soloWorkingPf.setSection_id(user.getSection_id());

					sservice.breakWork(soloWorkingPf, conn);

					// 根据作业信息生成新的等待作业信息－－无开始时间，说明进行非直接工步操作，回到等待区，可由他人接手
					sservice.breakToNext(soloWorkingPf, conn);

					// 通知 TODO
				} else if (iReason <= AlarmMesssageService.REASON_CODE_BREAK_ULIMIT) { // 不良中断
					// 作业信息状态改为，中断
					soloWorkingPf.setOperator_id(user.getOperator_id());
					soloWorkingPf.setSection_id(user.getSection_id());

					sservice.breakWork(soloWorkingPf, conn);

					// 根据作业信息生成新的中断作业信息
					sservice.breakToNext(soloWorkingPf, conn);
				}

				// 建立烘干作业数据
				if (iReason == 99 && originMaterialId != null) {
					dpSevice.createDryingProcess(originMaterialId, req, conn);
				}
			}

			OperatorProductionService opService = new OperatorProductionService();
			listResponse.put("processingPauseStart", opService.getNewPauseStart());
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionPanelMixedAction.dobreak end");
	}

	@Privacies(permit={0})
	public void dofinish(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionPanelMixedAction.dofinish start");
		Map<String, Object> callBackResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		List<String> triggerList = new ArrayList<String>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得当前作业中作业信息
		SoloProductionFeatureEntity soloWorkingPf = sservice.checkWorkingPfServiceRepair(user.getOperator_id(), null, conn);

		if (soloWorkingPf != null) {

//			Integer use_seconds = service.getTotalTimeByRework(workingPf, conn);
			String pcs_inputs = req.getParameter("pcs_inputs");

			if (errors.size() == 0) {
				// 作业信息状态改为，作业完成
				soloWorkingPf.setPcs_inputs(pcs_inputs);
				soloWorkingPf.setPcs_comments(req.getParameter("pcs_comments"));
				sservice.finish(soloWorkingPf, conn);

				String stockCode = req.getParameter("stock_code");
				if (stockCode != null) {
					// 入库
					ComponentManageService cmService = new ComponentManageService();
					ComponentManageEntity updateBean = new ComponentManageEntity();
					updateBean.setSerial_no(soloWorkingPf.getSerial_no());
					updateBean.setStock_code(stockCode);
					cmService.componentInstock(updateBean, conn);
				}

				// 本工位结束
				triggerList.add("http://localhost:8080/rvspush/trigger/out/" + soloWorkingPf.getPosition_id() + "/" + user.getSection_id() + "/" + user.getOperator_id());
				// 启动下个工位
				try {
					List<String> fingerList = sservice.fingerNextPosition(soloWorkingPf, conn, triggerList, true);
	
					String fingers = sservice.getFingerString(soloWorkingPf.getModel_id(), soloWorkingPf.getSerial_no(), fingerList, stockCode, conn, true);
	
					// 下个工位移动信息处理
					callBackResponse.put("past_fingers", fingers);
					session.setAttribute(RvsConsts.JUST_FINISHED, fingers);
					session.removeAttribute(RvsConsts.JUST_WORKING);
	
				} catch (Exception e) {
					if (e.getMessage() == null || e.getMessage().length() == 0) {
						throw e;
					} else {
						MsgInfo info = new MsgInfo();
						info.setErrmsg(e.getMessage());
						errors.add(info);
					}
					conn.rollback();
				}
	
				// 通知 TODO
			}

			if (triggerList.size() > 0 && errors.size() == 0) {
				conn.commit();
				RvsUtils.sendTrigger(triggerList);
			}
		} else {
			log.error("作业已经被结束。");
		}

		OperatorProductionService opService = new OperatorProductionService();
		callBackResponse.put("processingPauseStart", opService.getNewPauseStart());

		// 检查发生错误时报告错误信息
		callBackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callBackResponse);

		log.info("PositionPanelMixedAction.dofinish end");
	
	}

	public void refreshWaitings(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("PositionPanelMixedAction.refreshWaitings start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String pxLevel = user.getPx();
		if ("4".equals(pxLevel)) pxLevel = "0"; // 超级员工不分线

		if (user.getGroup_position_id() == null) { // 非虚拟组

			List<WaitingEntity> waitings = new ArrayList<WaitingEntity>();
			// 取得等待区一览 comp
			waitings = sservice.getWaitingMaterial(user.getSection_id(), user.getPosition_id(),
					user.getOperator_id(), user.getProcess_code(), (pxLevel.equals("1") ? 0 : 1), conn);
			waitings.addAll(service.getWaitingMaterial(user.getSection_id(), user.getPosition_id(), user.getLine_id(),
					user.getOperator_id(), pxLevel, user.getProcess_code(), conn));
			// 取得等待区一览
			callbackResponse.put("waitings", waitings);
		} else { // 虚拟组 TODO

			// 取得完成区一览
			List<WaitingEntity> completes = service.getGroupCompleteMaterial(user.getSection_id(), user.getGroup_position_id(), 
					user.getPx(), conn);
			// 取得等待区一览
			callbackResponse.put("waitings", service.getGroupWaitingMaterial(user.getSection_id(), user.getGroup_position_id(), user.getLine_id(),
							user.getOperator_id(), pxLevel, completes, conn));
			callbackResponse.put("completes", completes);

		}

		// 取得工程工位待处理容许时间（分钟）
		callbackResponse.put("permitMinutes", RvsUtils.getUnproceedPermit(user.getSection_name(), user.getLine_name()));

		callbackResponse.put("errors", infoes);

		returnJsonResponse(res, callbackResponse);
		log.info("PositionPanelMixedAction.refreshWaitings end");
	}

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
		log.info("PositionPanelMixedAction.doPointOut start");
		Map<String, Object> jsonResponse = new HashMap<String, Object>();

		// 检查发生错误时报告错误信息
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		jsonResponse.put("errors", errors);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);


		// 取得组装进行记录
		SoloProductionFeatureEntity workingSpf = sservice.checkWorkingPfServiceRepair(user.getOperator_id(), user.getPosition_id(), conn);

		// 进行中的话
		if (workingSpf != null) {

			// 作业信息状态改为，作业完成
			sservice.finish(workingSpf, conn);

			// 启动下个工位
			List<String> fingerList = sservice.fingerNextPosition(workingSpf, conn,
					new ArrayList<String>(), false);

			String fingers = sservice.getFingerString(workingSpf.getModel_id(), workingSpf.getSerial_no(), fingerList, null, conn, false);
			if (fingers.indexOf("<") >= 0) {
				ComponentManageService cmService = new ComponentManageService();
				List<ComponentManageEntity> l = cmService.getBySerialNo(workingSpf.getSerial_no(), conn);
				if (l.size() > 0) {
					jsonResponse.put("stock_code", l.get(0).getStock_code());
				}
			}

			session.setAttribute(RvsConsts.JUST_WORKING, fingers);
			jsonResponse.put("fingers", fingers);

			jsonResponse.put("past_fingers", session.getAttribute(RvsConsts.JUST_FINISHED));
		}

		// 返回Json格式响应信息
		returnJsonResponse(res, jsonResponse);

		// 得到结果后回滚
		conn.rollback();

		log.info("PositionPanelMixedAction.doPointOut end");
	}
}
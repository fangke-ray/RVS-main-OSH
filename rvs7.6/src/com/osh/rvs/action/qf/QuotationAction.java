package com.osh.rvs.action.qf;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
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
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.infect.PeripheralInfectDeviceEntity;
import com.osh.rvs.bean.inline.MaterialProcessAssignEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.inline.MaterialProcessAssignMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.service.AlarmMesssageService;
import com.osh.rvs.service.CustomerService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.PauseFeatureService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.ProcessAssignService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.equipment.DeviceJigLoanService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.qa.ServiceRepairManageService;
import com.osh.rvs.service.qf.QuotationService;
import com.osh.rvs.service.qf.WipService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class QuotationAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private static String WORK_STATUS_FORBIDDEN = "-1";
	private static String WORK_STATUS_PREPAIRING = "0";
	private static String WORK_STATUS_WORKING = "1";
	private static String WORK_STATUS_PERIPHERAL_WORKING = "4";
	private static String WORK_STATUS_PAUSING = "2";
	private static String WORK_STATUS_PERIPHERAL_PAUSING = "5";

	private PositionPanelService ppService = new PositionPanelService();
	private ProductionFeatureService pfService = new ProductionFeatureService();
	private PauseFeatureService bfService = new PauseFeatureService();

	/**
	 * 报价画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("QuotationAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String process_code = user.getProcess_code();
		String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);
		boolean isPeripheral = (special_forward != null && special_forward.indexOf("peripheral") >= 0);

		if (isPeripheral) {
			req.setAttribute("peripheral", true);
		} else {
			boolean isSimple = (special_forward != null && special_forward.indexOf("simple") >= 0);

			if (!isSimple) {
				req.setAttribute("needPcs", true);
			}
		}

		req.setAttribute("edit_ocm", CodeListUtils.getSelectOptions("material_ocm", null, null, false));
		if (isPeripheral) {
			req.setAttribute("edit_level", CodeListUtils.getSelectOptions("material_level_peripheral", null, "(未选)", false));
		} else {
			req.setAttribute("edit_level", CodeListUtils.getSelectOptions("material_level_endoscope", null, "(无故障)", false));
		}
		req.setAttribute("edit_fix_type", CodeListUtils.getSelectOptions("material_fix_type", null, null, false));
		req.setAttribute("edit_service_repair_flg", CodeListUtils.getSelectOptions("material_service_repair", null, "", false));
		req.setAttribute("options_ocm_rank", CodeListUtils.getSelectOptions("material_ocm_direct_rank", null, "(无故障)", false));
		req.setAttribute("edit_material_direct_area", CodeListUtils.getSelectOptions("material_direct_area", null, "(无)", false));
		
		// 获得维修流程选项
		ProcessAssignService paService = new ProcessAssignService();
		String paOptions = paService.getGroupOptions("", conn);
		req.getSession().setAttribute("paOptions", paOptions);

		// 判断是否动物实验用维修品工位
		if (PositionService.getPositionUnitizeds(conn).containsKey(user.getPosition_id())) {
			req.setAttribute("unitizeds", "true");
		}

		req.setAttribute("WORKINFO", ppService.getWorkInfo());

		log.info("QuotationAction.init end");
	}

	/**
	 * 报价画面初始取值处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void jsinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("QuotationAction.jsinit start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得待点检信息
		String section_id = user.getSection_id();
		String line_id = user.getLine_id();
		String position_id = user.getPosition_id();
		String process_code = user.getProcess_code();
		PositionPanelService ppservice = new PositionPanelService();
		String infectString = "";
		boolean join151And161 = "00000000013".equals(position_id) || "00000000014".equals(position_id);
		if (join151And161) {
			infectString = ppservice.checkPositionInfectWorkOnPass(
					section_id, "00000000013", line_id, user.getOperator_id(), conn, callbackResponse);

			infectString += ppservice.checkPositionInfectWorkOnPass(
					section_id, "00000000014", line_id, user.getOperator_id(), conn, callbackResponse);
		} else {
			// 设定待点检信息
			infectString = ppservice.checkPositionInfectWorkOnPass(
					section_id, position_id, line_id, user.getOperator_id(), conn, callbackResponse);
		}
		infectString += ppservice.getAbnormalWorkStateByOperator(user.getOperator_id(), conn);

		callbackResponse.put("infectString", infectString);

		if (infectString.indexOf("限制工作") >= 0) {
			callbackResponse.put("workstauts", WORK_STATUS_FORBIDDEN);
		} else {

			// 判断是否有特殊页面效果
			String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

			QuotationService qService = new QuotationService();
			qService.listRefresh(user, callbackResponse, conn);
	
			// 设定OCM文字
			callbackResponse.put("oOptions", CodeListUtils.getGridOptions("material_ocm"));
			// 设定等级文字
			boolean isPeripheral = (special_forward != null && special_forward.indexOf("peripheral") >= 0);
			if (isPeripheral) {
				callbackResponse.put("lOptions", CodeListUtils.getGridOptions("material_level_peripheral"));
			} else {
				callbackResponse.put("lOptions", CodeListUtils.getGridOptions("material_level_endoscope"));
			}
			// 设定直送文字
			callbackResponse.put("dOptions", CodeListUtils.getGridOptions("material_direct"));
			// 设定返修文字
			callbackResponse.put("sOptions", CodeListUtils.getGridOptions("material_service_repair"));
			// 设定维修分类文字
			callbackResponse.put("tOptions", CodeListUtils.getGridOptions("material_fix_type"));

			callbackResponse.put("bOptions", CodeListUtils.getGridOptions("material_direct_area"));

			// 设定暂停选项
			if (join151And161) {
				process_code = "151";
			}

			// 设定正常中断选项
			callbackResponse.put("stepOptions", ppService.getStepOptions(process_code));
			callbackResponse.put("pauseOptions", PauseFeatureService.getPauseReasonSelectOptions());
			callbackResponse.put("pauseComments", PauseFeatureService.getPauseReasonSelectComments());

			// 判断是否有在进行中的维修对象
			ProductionFeatureEntity workingPf = ppService.getWorkingPf(user, conn);
			// 进行中的话
			if (workingPf != null) {
				// 取得作业信息
				qService.getProccessingData(callbackResponse, workingPf.getMaterial_id(), user, conn);

				if (isPeripheral) {
					List<PeripheralInfectDeviceEntity> resultEntities = new ArrayList<PeripheralInfectDeviceEntity>();

					// 取得周边设备检查使用设备工具 
					boolean infectFinishFlag = ppService.getPeripheralData(workingPf.getMaterial_id(), workingPf, resultEntities, conn);

					if (resultEntities != null && resultEntities.size() > 0) {
						callbackResponse.put("peripheralData", resultEntities);
					}

					if (!infectFinishFlag) {
						callbackResponse.put("workstauts", WORK_STATUS_PERIPHERAL_WORKING);
					} else {
						// 取得工程检查票
						PositionPanelService.getPcses(callbackResponse, workingPf, user.getLine_id(), conn);

						// 页面设定为编辑模式
						callbackResponse.put("workstauts", WORK_STATUS_WORKING);
					}

				} else {
					// 取得工程检查票
					if (!"simple".equals(special_forward)) {
						PositionPanelService.getPcses(callbackResponse, workingPf, "00000000011", conn);
					}

					// 页面设定为编辑模式
					callbackResponse.put("workstauts", WORK_STATUS_WORKING);
				}

			} else {
				// 暂停中的话
				// 判断是否有在暂停中的维修对象
				ProductionFeatureEntity pauseingPf = ppService.getPausingPf(user, conn);
				if (pauseingPf != null) {
					// 取得作业信息
					qService.getProccessingData(callbackResponse, pauseingPf.getMaterial_id(), user, conn);

					if (isPeripheral) {
						List<PeripheralInfectDeviceEntity> resultEntities = new ArrayList<PeripheralInfectDeviceEntity>();

						// 取得周边设备检查使用设备工具 
						boolean infectFinishFlag = ppService.getPeripheralData(pauseingPf.getMaterial_id(), pauseingPf, resultEntities, conn);

						if (resultEntities != null && resultEntities.size() > 0) {
							callbackResponse.put("peripheralData", resultEntities);
						}

						if (!infectFinishFlag) {
							callbackResponse.put("workstauts", WORK_STATUS_PERIPHERAL_PAUSING);
						} else {
							// 取得工程检查票
							PositionPanelService.getPcses(callbackResponse, pauseingPf, user.getLine_id(), conn);

							// 页面设定为编辑模式
							callbackResponse.put("workstauts", WORK_STATUS_PAUSING);
						}

					} else {
						// 取得工程检查票
						if (!"simple".equals(special_forward)) {
							PositionPanelService.getPcses(callbackResponse, pauseingPf, "00000000011", conn);
						}

						// 页面设定为编辑模式
						callbackResponse.put("workstauts", WORK_STATUS_PAUSING);
					}
				} else {
					// 准备中
					callbackResponse.put("workstauts", WORK_STATUS_PREPAIRING);
				}
			}
	
			CustomerService service = new CustomerService();
			List<String> list = service.getAutoComplete(conn);
			
			// 查询结果放入Ajax响应对象
			callbackResponse.put("customers", list);

			// 判断作业者是否借用了设备工具
			if (!PositionService.getPositionUnitizeds(conn).containsKey(user.getPosition_id())) {
				if (session.getAttribute("DJ_LOANING") != null) {
					DeviceJigLoanService djlService = new DeviceJigLoanService();
					String loaning = djlService.checkLoaningNowText(user.getOperator_id(), conn);
					if (loaning != null) {
						callbackResponse.put("djLoaning", loaning);
					}
				}
			}
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("QuotationAction.jsinit end");
	}

	/**
	 * 扫描开始/直接暂停重开
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void doscan(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("QuotationAction.scan start");
		Map<String, Object> detailResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("material_id");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 代工间接人员时禁止开始直接人员作业
		if (user.getShift_work()) {
			if (user.getWork_count_flg().equals(""+RvsConsts.WORK_COUNT_FLG_INDIRECT)) {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("operator_id");
				msgInfo.setErrcode("info.indirectwork.shifted2Indirect");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.indirectwork.shifted2Indirect"));
				errors.add(msgInfo);
			}
		}

		String position_id = user.getPosition_id();
		String process_code = user.getProcess_code();
		String section_id = user.getSection_id();
		List<Integer> privacies = user.getPrivacies();

		// 判断维修对象在等待区，并返回这一条作业信息
		ProductionFeatureEntity waitingPf = null;

		if (errors.size() == 0) {
			boolean join151And161 = "00000000013".equals(position_id) || "00000000014".equals(position_id);
			if (join151And161) {
				user.setSection_id("9");// 报价物料课
				user.setPosition_id("00000000013");
				user.setProcess_code("151");

				waitingPf = ppService.checkMaterialId(material_id, "true", user, errors, conn);

				// 直送报价
				if (waitingPf == null) {
					user.setPosition_id("00000000014");
					user.setProcess_code("161");
					errors = new ArrayList<MsgInfo>();
					// 判断维修对象在等待区，并返回这一条作业信息
					waitingPf = ppService.checkMaterialId(material_id, "true", user, errors, conn);
				}
			} else {
				waitingPf = ppService.checkMaterialId(material_id, "true", user, errors, conn);
			}
			if (waitingPf != null && waitingPf.getProcess_code() == null) {
				log.info("工程检查票不能没有工位号" + user.getProcess_code());
				waitingPf.setProcess_code(user.getProcess_code());
			}
		}

		// 检查停止维修型号
		if(errors.size() == 0 && !privacies.contains(RvsConsts.PRIVACY_PROCESSING)){ // 经理不限制
			if (waitingPf != null && waitingPf.getOperate_result() == RvsConsts.OPERATE_RESULT_NOWORK_WAITING) {
				//验证型号是否终止
				MaterialService materialService = new MaterialService();
				MaterialForm materialForm  = materialService.loadSimpleMaterialDetail(conn, material_id);

				materialService.checkModelDepacy(materialForm, conn, errors);
			}
		}
		
		if (errors.size() == 0) {
			QuotationService qService = new QuotationService();
			qService.getProccessingData(detailResponse, material_id, user, conn);

			// 判断是否有特殊页面效果
			String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

			if ("peripheral".equals(special_forward)) {

				List<PeripheralInfectDeviceEntity> resultEntities = new ArrayList<PeripheralInfectDeviceEntity>();
				// 取得周边设备检查使用设备工具 
				boolean infectFinishFlag = ppService.getPeripheralData(material_id, waitingPf, resultEntities, conn);

				// 取得周边设备检查使用设备工具 
				if (resultEntities != null && resultEntities.size() > 0) {
					detailResponse.put("peripheralData", resultEntities);
				}

				if (!infectFinishFlag) {
					detailResponse.put("workstauts", WORK_STATUS_PERIPHERAL_WORKING);
				} else {
					// 取得工程检查票
					waitingPf.setProcess_code(process_code);
					PositionPanelService.getPcses(detailResponse, waitingPf, user.getLine_id(), conn);

					// 页面设定为编辑模式
					detailResponse.put("workstauts", WORK_STATUS_WORKING);
				}
			} else {
				// 取得工程检查票
				if (!"simple".equals(special_forward)) {
					PositionPanelService.getPcses(detailResponse, waitingPf, "00000000011", conn);
				}

				// 页面设定为编辑模式
				detailResponse.put("workstauts", WORK_STATUS_WORKING);
			}

			// 取得QA判定信息
			ServiceRepairManageService srmService = new ServiceRepairManageService();
			srmService.getQaInfo2Quotation(detailResponse, material_id, conn);

			// 作业信息状态改为，作业中
			ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
			waitingPf.setOperator_id(user.getOperator_id());
			dao.startProductionFeature(waitingPf);

			// 如果等待中信息是暂停中，则结束掉暂停记录(有可能已经被结束)
			bfService.finishPauseFeature(material_id, user.getSection_id(), user.getPosition_id(), user.getOperator_id(), conn);

			// 判断借用设备
			if (PositionService.getPositionUnitizeds(conn).containsKey(waitingPf.getPosition_id())) {
				DeviceJigLoanService djlService = new DeviceJigLoanService();

				// 现在已借用的设备治具未登记给维修品
				List<String> loaningUnregisting = djlService.getLoaningUnregisting(waitingPf,
						user.getOperator_id(), conn);

				// 在借用的前提下
				djlService.registToMaterial(waitingPf, loaningUnregisting, conn);
			}
		}

		user.setSection_id(section_id); // TODO

		// 检查发生错误时报告错误信息
		detailResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, detailResponse);

		log.info("QuotationAction.scan end");
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
		log.info("QuotationAction.dopause start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		if (errors.size() == 0) {
			String comments = bfService.checkPauseForm(req.getParameter("comments"), errors);

			if (errors.size() == 0) {
				// 取得当前作业中作业信息
				ProductionFeatureEntity workingPf = ppService.getWorkingPf(user, conn);
	
				// 作业信息状态改为，暂停
				ProductionFeatureMapper pfdao = conn.getMapper(ProductionFeatureMapper.class);
				workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_PAUSE);
				workingPf.setUse_seconds(null);
				pfdao.finishProductionFeature(workingPf);
	
				// 制作暂停信息
				bfService.createPauseFeature(workingPf, req.getParameter("reason"), comments, null, conn);
	
				// 根据作业信息生成新的等待作业信息－－有开始时间（仅作标记用，重开时需要覆盖掉），说明是操作者原因暂停，将由本人重开。
				pfService.pauseToSelf(workingPf, conn);

				listResponse.put("workstauts", WORK_STATUS_PAUSING);
			}
		}

		QuotationService qService = new QuotationService();
		qService.listRefresh(user, listResponse, conn);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("QuotationAction.dopause end");
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
		log.info("QuotationAction.dobreak start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		QuotationService qService = new QuotationService();
		MaterialService mservice = new MaterialService();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		List<String> triggerList = new ArrayList<String>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得当前作业中作业信息
		ProductionFeatureEntity workingPf = ppService.getWorkingPf(user, conn);

		if (workingPf == null) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("material_id");
			msgInfo.setErrcode("validator.invalidParam.invalidIntegerValue");
			msgInfo.setErrmsg("当前作业对象已中断或结束。");
			errors.add(msgInfo);
		}

		String sReason = req.getParameter("reason");
		Integer iReason = null;

		if (errors.size() == 0) {
			mservice.checkRepeatNo(workingPf.getMaterial_id(), form, conn, errors);
	
			log.info("REASON:" + sReason);
	
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

			// 判断竞合
			if (iReason > 70) {
				MaterialForm materialForm = (MaterialForm) form;
				if (!isEmpty(materialForm.getWip_location())) {
					materialForm.setMaterial_id(workingPf.getMaterial_id());
					WipService wipService = new WipService();
					if (wipService.checkLocateInuse(materialForm, conn, errors)) {
						MsgInfo error = new MsgInfo();
						error.setComponentid("wip_location");
						error.setErrcode("info.wipShelf.notEmpty");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.wipShelf.notEmpty"));
						errors.add(error);
					}
				}
			}
		}

		PositionPanelService service = new PositionPanelService();

		if (errors.size() == 0) {
			service.checkSupporting(workingPf.getMaterial_id(), workingPf.getPosition_id(), errors, conn);
		}

		if (errors.size() == 0) {

			// 中断警报序号
			String alarm_messsage_id = null;

			if (iReason <= AlarmMesssageService.REASON_CODE_BREAK_ULIMIT) { // 异常中断
				// 制作中断警报
				AlarmMesssageService amservice = new AlarmMesssageService();
				AlarmMesssageEntity amEntity = amservice.createBreakAlarmMessage(workingPf);
				alarm_messsage_id = amservice.createAlarmMessage(amEntity, conn, false, triggerList);
			}

			// 制作暂停信息
			bfService.createPauseFeature(workingPf, sReason, req.getParameter("comments"), alarm_messsage_id, conn);

			if (iReason > 70) { // 业务流程-非直接工步操作

				// 作业信息状态改为，中断
				ProductionFeatureMapper pfdao = conn.getMapper(ProductionFeatureMapper.class);
				workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_BREAK);
				workingPf.setUse_seconds(null);
				workingPf.setPcs_inputs(req.getParameter("pcs_inputs"));
				workingPf.setPcs_comments(req.getParameter("pcs_comments"));

				pfdao.finishProductionFeature(workingPf);

				// 根据作业信息生成新的等待作业信息－－无开始时间，说明进行非直接工步操作，回到等待区，可由他人接手
				pfService.pauseToNext(workingPf, conn);

				if(iReason == 72) { // CCD 盖玻璃作业
					ProductionFeatureService pfservice = new ProductionFeatureService();

					ProductionFeatureEntity ccdworkingPf = new ProductionFeatureEntity();
					ccdworkingPf.setPosition_id("00000000025");
					ccdworkingPf.setSection_id("00000000009"); // 暂时固定 TODO
					ccdworkingPf.setRework(pfservice.getReworkCountWithLine(workingPf.getMaterial_id(), "00000000013", conn));

					pfservice.removeWorking(workingPf.getMaterial_id(), "00000000025", conn);
					// 重做
					pfservice.fingerSpecifyPosition(workingPf.getMaterial_id(), true, ccdworkingPf
							, new ArrayList<String>(), conn);

				} else if (iReason == 73) { // 放入WIP
					log.info("WIP:" + req.getParameter("wip_location"));
				}

				// 更新维修对象。
				MaterialEntity bean = new MaterialEntity();
				BeanUtil.copyToBean(form, bean, CopyOptions.COPYOPTIONS_NOEMPTY);
				// 进行中的维修对象
				bean.setMaterial_id(workingPf.getMaterial_id());
				if (!isEmpty(bean.getCustomer_name())) {
					CustomerService cservice = new CustomerService();
					bean.setCustomer_id(cservice.getCustomerStudiedId(bean.getCustomer_name(), bean.getOcm(), conn));
				}
				qService.updateMaterial(bean, conn);

				if (iReason == 74) { // 保内判定
					ServiceRepairManageService srmService = new ServiceRepairManageService();
					// 为保内判定新建记录
					srmService.insertServiceRepairManageFromMaterial(workingPf.getMaterial_id(), conn);
					// 报价给品保时出库
					WipService wipService = new WipService();
					wipService.changelocation(conn, workingPf.getMaterial_id(), null);
				}

				// 通知 TODO
			} else if (iReason <= AlarmMesssageService.REASON_CODE_BREAK_ULIMIT) { // 不良中断
				// 作业信息状态改为，中断
				ProductionFeatureMapper pfdao = conn.getMapper(ProductionFeatureMapper.class);
				workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_BREAK);
				workingPf.setUse_seconds(null);
				pfdao.finishProductionFeature(workingPf);

				// 根据作业信息生成新的中断作业信息
				pfService.breakToNext(workingPf, conn);

				// 通知 TODO

			} else {
				log.equals(user.getName() + "在" + user.getProcess_code() + "工位发生中断,但是前台提交了暂停理由" + iReason);
				pfService.pauseToSelf(workingPf, conn); // 为 TODO
			}
			
			//更新维修对象备注
			MaterialForm materialForm = (MaterialForm)form;
			materialForm.setMaterial_id(workingPf.getMaterial_id());
			
			qService.updateComment(materialForm, user, conn);

			// 更新CCD标记
			String tagCcd = req.getParameter("tag_ccd");
			if (tagCcd != null) {
				MaterialTagService mtService = new MaterialTagService();

				mtService.updataTagByMaterialId(workingPf.getMaterial_id(), 
						MaterialTagService.TAG_FOR_CCD_REPLACE, "1".equals(tagCcd), conn);
			}
		}

		if (triggerList.size() > 0 && errors.size() == 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		if (errors.size() == 0) {
			qService.listRefresh(user, listResponse, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("QuotationAction.dobreak end");
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
		log.info("QuotationAction.doendpause start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("material_id");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String section_id = user.getSection_id();

		// 代工间接人员时禁止开始直接人员作业
		if (user.getShift_work()) {
			if (user.getWork_count_flg().equals(""+RvsConsts.WORK_COUNT_FLG_INDIRECT)) {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("operator_id");
				msgInfo.setErrcode("info.indirectwork.shifted2Indirect");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.indirectwork.shifted2Indirect"));
				errors.add(msgInfo);
			}
		}

		// 得到暂停的维修对象，返回这一条作业信息
		ProductionFeatureEntity workwaitingPf = ppService.checkPausingMaterialId(material_id, user, errors, conn);

		if (errors.size() == 0) {
			QuotationService qService = new QuotationService();
			qService.getProccessingData(callbackResponse, material_id, user, conn);

			workwaitingPf.setOperate_result(RvsConsts.OPERATE_RESULT_WORKING);
			pfService.changeWaitProductionFeature(workwaitingPf, conn);

			// 只要开始做，就结束掉本人所有的暂停信息。
			bfService.finishPauseFeature(material_id, section_id, user.getPosition_id(), user.getOperator_id(), conn);

			List<PeripheralInfectDeviceEntity> resultEntities = new ArrayList<PeripheralInfectDeviceEntity>();

			// 取得周边设备检查使用设备工具 
			boolean infectFinishFlag = ppService.getPeripheralData(material_id, workwaitingPf, resultEntities, conn);

			if (!infectFinishFlag) {
				callbackResponse.put("workstauts", WORK_STATUS_PERIPHERAL_WORKING);
			} else {
				// 页面设定为编辑模式
				callbackResponse.put("workstauts", WORK_STATUS_WORKING);
			}
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("QuotationAction.doendpause end");
	}

	/**
	 * 作业完成
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void dofinish(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("QuotationAction.dofinish start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		MaterialForm materialForm = (MaterialForm)form;

		MaterialService mservice = new MaterialService();
		QuotationService qService = new QuotationService();

		List<MsgInfo> errors = mservice.checkQuotation(materialForm, conn);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得当前作业中作业信息
		ProductionFeatureEntity workingPf = ppService.getWorkingPf(user, conn); 
		// 取得原先的同意日
		String confirmed = req.getParameter("confirmed");
		String materialId = null;
		String pcs_inputs = req.getParameter("pcs_inputs");

		MaterialEntity meOrg = null;

		if (!"true".equals(confirmed)) {
			if (workingPf == null) {
				MsgInfo error = new MsgInfo();
				error.setErrcode("info.linework.workingLost");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
				errors.add(error);
			} else {
				materialId = workingPf.getMaterial_id();
			}

			// 检查工程检查票是否全填写
			if (pcs_inputs != null) 
				ppService.checkPcsEmpty(pcs_inputs, errors);

			// 同意日与WIP存放check
			if (errors.size() == 0 && !"160".equals(user.getProcess_code())) {
	
				mservice.checkRepeatNo(materialId, materialForm, conn, errors);
				materialForm.setMaterial_id(materialId);

				meOrg = mservice.loadSimpleMaterialDetailEntity(conn, materialId);
				Date orginAgreedDate = meOrg.getAgreed_date();
				if (isEmpty(materialForm.getWip_location())) {
					// 完成报价
					if (orginAgreedDate == null) {
						if (isEmpty(materialForm.getAgreed_date())) {
							MsgInfo error = new MsgInfo();
							error.setErrcode("validator.required");
							error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "同意日"));
							errors.add(error);
						} else {
							MsgInfo error = new MsgInfo();
							error.setErrcode("info.agreedDateChanged.byIf");
							error.setErrmsg("在您先前作业期间同意日被取消，不能完成报价。");
							errors.add(error);
						}
					}
				} else {
					if (orginAgreedDate != null) { // 前台提交着无同意日的状态放入WIP
						if (isEmpty(materialForm.getAgreed_date())) {
							MsgInfo error = new MsgInfo();
							error.setErrcode("info.agreedDateChanged.byIf");
							error.setErrmsg("在您先前作业期间同意日已经更新，请确认是否仍然放WIP？");
							errors.add(error);
							listResponse.put("agreed_date", DateUtil.toString(orginAgreedDate, DateUtil.DATE_PATTERN));
						} else {
							MsgInfo error = new MsgInfo();
							error.setErrcode("info.agreedDateChanged.byIf");
							error.setErrmsg("已经有同意日，请确认是否仍然放WIP？");
							errors.add(error);
							listResponse.put("agreed_date", DateUtil.toString(orginAgreedDate, DateUtil.DATE_PATTERN));
						}
					}

					// 判断竞合
					WipService wipService = new WipService();
					if (wipService.checkLocateInuse(materialForm, conn, errors)) {
						MsgInfo error = new MsgInfo();
						error.setComponentid("wip_location");
						error.setErrcode("info.wipShelf.notEmpty");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.wipShelf.notEmpty"));
						errors.add(error);
					}
				}
			}
		} else {
			materialId = workingPf.getMaterial_id();
		}

		if (errors.size() == 0) {

			// 检查维修对象表单
			MaterialEntity bean = new MaterialEntity();
			BeanUtil.copyToBean(form, bean, CopyOptions.COPYOPTIONS_NOEMPTY);

			// 进行中的维修对象
			bean.setMaterial_id(materialId);

			// 更新维修对象。
			if (!isEmpty(bean.getCustomer_name())) {
				CustomerService cservice = new CustomerService();
				bean.setCustomer_id(cservice.getCustomerStudiedId(bean.getCustomer_name(), bean.getOcm(), conn));
			}
			if (bean.getScheduled_expedited() == null) {
				if (meOrg == null) {
					meOrg = mservice.loadSimpleMaterialDetailEntity(conn, materialId);
				}
				bean.setScheduled_expedited(meOrg.getScheduled_expedited());
			}
			qService.updateMaterial(bean, conn);

			String level = materialForm.getLevel();//等级
			String fix_type = materialForm.getFix_type();//修理方式
			//小修理流水线
			boolean isLightFix = RvsUtils.isLightFix(level) && "1".equals(fix_type); 

			/// 取得本次工时
			Integer use_seconds = ppService.getTotalTimeByRework(workingPf, conn);

			// 作业信息状态改为，作业完成
			ProductionFeatureMapper pfdao = conn.getMapper(ProductionFeatureMapper.class);
			workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
			workingPf.setUse_seconds(use_seconds);
			if (pcs_inputs != null) {
				workingPf.setPcs_inputs(pcs_inputs);
				workingPf.setPcs_comments(req.getParameter("pcs_comments"));
			}
			pfdao.finishProductionFeature(workingPf);

			// 启动下个工位 报价不启动
			pfService.fingerNextPosition(materialId, workingPf, conn, null);

			if(!isLightFix){
				MaterialProcessAssignEntity entity = new MaterialProcessAssignEntity();
				BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
				MaterialProcessAssignMapper materialProcessAssignMapper = conn.getMapper(MaterialProcessAssignMapper.class);
				
				String lightFixes = materialProcessAssignMapper.getLightFixesByMaterial(entity.getMaterial_id());
				if (!isEmpty(lightFixes)) {
					//删除维修对象选用小修理
					materialProcessAssignMapper.deleteMaterialLightFix(entity.getMaterial_id());
					//删除维修对象独有修理流程
					materialProcessAssignMapper.deleteMaterialProcessAssign(entity.getMaterial_id());
					// 删除小修理流程说明
					mservice.removeComment(entity.getMaterial_id(), "00000000001", conn);
				}
			}

			// 周边检查卡先归档一次
			if (RvsUtils.isPeripheral(level)) {
				conn.commit();

				// 推送归档
				try {
					URL url = new URL("http://localhost:8080/rvs/download.do?method=file&material_id=" + materialForm.getMaterial_id());
					url.getQuery();
					URLConnection urlconn = url.openConnection();
					urlconn.setReadTimeout(1); // 不等返回
					urlconn.connect();
					urlconn.getContentType(); // 这个就能触发
				} catch (Exception e) {
					log.error("Failed", e);
				}
			} else {
				String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + user.getProcess_code());
				boolean isSimple = (special_forward != null && special_forward.indexOf("simple") >= 0);
				if (!isSimple) {
					MaterialForm mEntity = mservice.loadSimpleMaterialDetail(conn, materialForm.getMaterial_id());
					if (mEntity.getCategory_id().equals(RvsConsts.CATEGORY_UDI)) {
						conn.commit();

						// 推送归档
						try {
							URL url = new URL("http://localhost:8080/rvs/download.do?method=file&material_id=" + materialForm.getMaterial_id());
							url.getQuery();
							URLConnection urlconn = url.openConnection();
							urlconn.setReadTimeout(1); // 不等返回
							urlconn.connect();
							urlconn.getContentType(); // 这个就能触发
						} catch (Exception e) {
							log.error("Failed", e);
						}
					}
				}
			}

			// 通知 TODO

			// FSE 数据同步
			try{
				FseBridgeUtil.toUpdateMaterial(materialId, "qt" + workingPf.getProcess_code());
				FseBridgeUtil.toUpdateMaterialProcess(materialId, "qt" + workingPf.getProcess_code());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			
			//更新维修对象备注
			qService.updateComment(materialForm, user, conn);

			// 更新CCD标记
			String tagCcd = req.getParameter("tag_ccd");
			if (tagCcd != null) {
				MaterialTagService mtService = new MaterialTagService();

				mtService.updataTagByMaterialId(workingPf.getMaterial_id(), 
						MaterialTagService.TAG_FOR_CCD_REPLACE, "1".equals(tagCcd), conn);
			}
		}

		qService.listRefresh(user, listResponse, conn);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("QuotationAction.dofinish end");
	}

	/**
	 * 切换急速修理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void doSwitchTopSpeed(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("QuotationAction.doSwitchTopSpeed start");
		Map<String, Object> cbResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String materialId = req.getParameter("material_id");

		MaterialTagService mtService = new MaterialTagService();
		mtService.switchTagByMaterialId(materialId, 
				MaterialTagService.TAG_SHIFT_CONTRACT_RELATED, conn);

		if (mtService.checkTagsXorByMaterialId(materialId, 
				MaterialTagService.TAG_CONTRACT_RELATED, MaterialTagService.TAG_SHIFT_CONTRACT_RELATED,
				conn)) {
			cbResponse.put("isTopSpeed", 1);
		}

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		log.info("QuotationAction.doSwitchTopSpeed end");
	}	

	public void doCcdTagAssign(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("QuotationAction.doCcdTagAssign start");
		Map<String, Object> cbResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String materialId = req.getParameter("material_id");
		String scheduledExpedited = req.getParameter("scheduled_expedited");

		MaterialTagService mtService = new MaterialTagService();
		mtService.updataTagByMaterialId(materialId, MaterialTagService.TAG_FOR_CCD_REPLACE, "1".equals(scheduledExpedited), conn);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		QuotationService qService = new QuotationService();
		qService.listFinishRefresh(user, cbResponse, conn);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		log.info("QuotationAction.doCcdTagAssign end");
	}
}

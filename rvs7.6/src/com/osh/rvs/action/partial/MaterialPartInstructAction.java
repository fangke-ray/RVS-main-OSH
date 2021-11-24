package com.osh.rvs.action.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.master.PartialBomEntity;
import com.osh.rvs.bean.partial.MaterialPartInstructEntity;
import com.osh.rvs.bean.partial.MaterialPartPrelistEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.master.PartialBomForm;
import com.osh.rvs.form.master.PartialPositionForm;
import com.osh.rvs.form.partial.MaterialPartInstructForm;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.UserDefineCodesService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.partial.MaterialPartInstructService;
import com.osh.rvs.service.partial.PartialBomService;
import com.osh.rvs.service.partial.PartialPositionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 维修对象工作指示单列表
 * @author Gong
 *
 */
public class MaterialPartInstructAction extends BaseAction {

	/**
	 * 画面初始化
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("MaterialPartInstructAction.init start");

		String from = (String) req.getParameter("from");

		if (from != null && !"".equals(from)) {
			req.setAttribute("from", from);
			actionForward = mapping.findForward(from + "_page");
			log.info("MaterialPartInstructAction.init end-page-" + from);
			return;
		}

		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		// 进展默认选项
		String defaultCondProcedure = "1,2";
		// 零件管理
		if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER) || privacies.contains(RvsConsts.PRIVACY_ADMIN)) {
			req.setAttribute("privacy", "pm");
		}
		// 现品 (管理/订购)
		else if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			req.setAttribute("privacy", "fact");
			defaultCondProcedure = "4";
		}
		else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING) || privacies.contains(RvsConsts.PRIVACY_LINE)) {
			req.setAttribute("privacy", "line");
			defaultCondProcedure = "3";
		}
		req.setAttribute("defaultCondProcedure", defaultCondProcedure);

		// 等级选项
		req.setAttribute("lOption", CodeListUtils.getSelectOptions("material_level_inline", null, "", false));
		req.setAttribute("goMaterial_level", CodeListUtils.getGridOptions("material_level"));

		// 在线课室选项
		SectionService sService = new SectionService();
		req.setAttribute("sOption", sService.getOptions(conn, ""));

		// 进展选项
		req.setAttribute("procedureOption", CodeListUtils.getSelectOptions("instruct_procedure", null, "", false));
		req.setAttribute("goProcedure", CodeListUtils.getGridOptions("instruct_procedure"));

		log.info("MaterialPartInstructAction.init end");
	}

	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("MaterialPartInstructAction.search start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.delete("procedure");

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			MaterialPartInstructService service = new MaterialPartInstructService();
			// 执行检索
			List<MaterialPartInstructForm> lResultForm = service.searchInstruct(form, conn, errors);

			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialPartInstructAction.search end");
	}

	/**
	 * 调取管理者关注零件
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void loadPersonalFocus(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("MaterialPartInstructAction.loadPersonalFocus start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		MaterialPartInstructService service = new MaterialPartInstructService();
		List<PartialPositionForm> list = service.getFocusPartialsByOperator(user.getOperator_id(), conn);

		listResponse.put("partial_list", list);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialPartInstructAction.loadPersonalFocus end");
	}

	/**
	 * 调取零件信息为了加入关注
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void loadPartialForFocus(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("MaterialPartInstructAction.loadPartialForFocus start");
		// Ajax回馈对象	
		Map<String, Object> cbResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		MaterialPartInstructService service = new MaterialPartInstructService();
		PartialPositionForm ret = service.loadPartialForFocus(req.getParameter("partial_id"), conn);

		if (ret == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("partial_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "零件"));
			errors.add(error);
		} else {
			cbResponse.put("partial", ret);
		}

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		log.info("MaterialPartInstructAction.loadPartialForFocus end");
	}

	/**
	 * 设定本人关注零件
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doSetFocusPartialList(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("MaterialPartInstructAction.doSetFocusPartialList start");
		// Ajax回馈对象	
		Map<String, Object> cbResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		MaterialPartInstructService service = new MaterialPartInstructService();
		service.setFocusPartialList(req.getParameterMap(), user.getOperator_id(), conn);

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		log.info("MaterialPartInstructAction.doSetFocusPartialList end");
	}

	/**
	 * 按维修品读取工作指示单格式
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void instructLoad(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) {
		log.info("MaterialPartInstructAction.instructLoad start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String model_id = request.getParameter("model_id");

		MaterialPartInstructService service = new MaterialPartInstructService();

		// 取得登录用户权限
		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);

		Integer edittype = service.getEditType(request.getParameter("procedure")
				, user.getPrivacies(), user.getLine_id());
		listResponse.put("edittype", edittype);

		PartialPositionService posService = new PartialPositionService();

		/* 查询 */
		Map<String, List<PartialPositionForm>> instructLists = posService.loadInstruct(model_id, conn);

		String level = request.getParameter("level");
		List<PartialBomEntity> rankBomList = null;
		if (level != null && !RvsUtils.isLightFix(level)) {
			// 获取RankBom信息
			PartialBomService pRankBomService = new PartialBomService();
			rankBomList = pRankBomService.searchRankBom(form, conn);
			if (!rankBomList.isEmpty()) {
				List<String> rankBomCodes = new ArrayList<String>();
				for (PartialBomEntity rankBom : rankBomList) {
					rankBomCodes.add(rankBom.getCode());
				}
				listResponse.put("rankBom", rankBomCodes);

			} else {
				List<String> partialBom = pRankBomService.searchPartialBomEntity(form, conn);
				if (!partialBom.isEmpty()) {
					listResponse.put("partialBom", partialBom);
				}
			}
		}

		listResponse.put("instructLists", instructLists);
		listResponse.put("components", posService.getComponentOfModel(model_id, conn));

		String material_id = request.getParameter("material_id");
		if (material_id != null) {
			// 指示情况
			List<MaterialPartPrelistEntity> instuctListForMaterial = service.getInstuctListForMaterial(material_id, conn);
			// 标记他人追加项目
			if (edittype != null && edittype >= 1) {
				service.signNoPriv(instuctListForMaterial, user.getJob_no(), user.getPrivacies().contains(RvsConsts.PRIVACY_LINE));
			}

			// 报价人员编辑时
			if (rankBomList != null  
					&& user.getPrivacies().contains(RvsConsts.PRIVACY_POSITION)
					&& user.getLine_id().equals("00000000011")) {
				service.tryToFill(material_id, instuctListForMaterial, rankBomList, conn);
			}

			// 追加理由的选项
			MaterialService mService = new MaterialService();
			MaterialEntity mEntity = mService.loadSimpleMaterialDetailEntity(conn, material_id);

			String procedure = service.getProcedure(mEntity, instuctListForMaterial, conn);
			if (MaterialPartInstructService.PROCEDURE_INLINE.equals(procedure)) {
				listResponse.put("callReasons", true);
			}

			listResponse.put("instuctForMaterial", instuctListForMaterial);
		}

		UserDefineCodesService usdSerice = new UserDefineCodesService();
		String sPartHighpriceLever = usdSerice.searchUserDefineCodesValueByCode("PART_HIGHPRICE_LEVER", conn);
		listResponse.put("highprice", sPartHighpriceLever);

		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("MaterialPartInstructAction.instructLoad end");
	}

	/**
	 * 按维修品读取工作指示单格式
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void instructLoadByWorking(ActionMapping mapping, ActionForm form, HttpServletRequest reques,
			HttpServletResponse response, SqlSession conn) {
		log.info("MaterialPartInstructAction.instructLoadByWorking start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		MaterialPartInstructService service = new MaterialPartInstructService();

		// 取得登录用户权限
		LoginData user = (LoginData) reques.getSession().getAttribute(RvsConsts.SESSION_USER);

		PositionPanelService posPService = new PositionPanelService();
		ProductionFeatureEntity workingPf = posPService.getProcessingPf(user, conn);

		if (workingPf == null) {
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.linework.workingLost");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
			errors.add(error);
		} else {
			String material_id = workingPf.getMaterial_id();

			MaterialService mService = new MaterialService();
			MaterialEntity mEntity = mService.loadSimpleMaterialDetailEntity(conn, material_id);

			String model_id = mEntity.getModel_id();
			Integer level = mEntity.getLevel();

			Map<String, String> resEntity = new HashMap<String, String>();
			resEntity.put("material_id", mEntity.getMaterial_id());
			resEntity.put("omr_notifi_no", mEntity.getSorc_no());
			resEntity.put("level", "" +  level);
			resEntity.put("model_name", mEntity.getModel_name());
			listResponse.put("material", resEntity);

			// 指示情况
			List<MaterialPartPrelistEntity> instuctListForMaterial = service.getInstuctListForMaterial(material_id, conn);

			String procedure = service.getProcedure(mEntity, instuctListForMaterial, conn);

			Integer edittype = service.getEditType(procedure, user.getPrivacies(), user.getLine_id());
			listResponse.put("edittype", edittype);

			// 标记他人追加项目
			if (edittype > 0) {
				service.signNoPriv(instuctListForMaterial, user.getJob_no(), user.getPrivacies().contains(RvsConsts.PRIVACY_LINE));
			}

			PartialPositionService posService = new PartialPositionService();

			if (level != null && !RvsUtils.isLightFix(level)) {
				PartialBomForm condForm = new PartialBomForm();
				condForm.setModel_id(model_id);
				condForm.setLevel("" + level);
				
				// 获取RankBom信息
				PartialBomService pRankBomService = new PartialBomService();
				List<PartialBomEntity> rankBomList = pRankBomService.searchRankBom(condForm, conn);

				if (!rankBomList.isEmpty()) {
					List<String> rankBomCodes = new ArrayList<String>();
					for (PartialBomEntity rankBom : rankBomList) {
						rankBomCodes.add(rankBom.getCode());
					}
					listResponse.put("rankBom", rankBomCodes);

					// 报价人员编辑时
					if (MaterialPartInstructService.PROCEDURE_QUOTE.equals(procedure) 
							&& user.getPrivacies().contains(RvsConsts.PRIVACY_POSITION)
							&& user.getLine_id().equals("00000000011")) {
						service.tryToFill(material_id, instuctListForMaterial, rankBomList, conn);
					}
				} else {
					List<String> partialBom = pRankBomService.searchPartialBomEntity(condForm, conn);
					if (!partialBom.isEmpty()) {
						listResponse.put("partialBom", partialBom);
					}
				}
			}

			listResponse.put("instuctForMaterial", instuctListForMaterial);

			// 追加理由的选项
			if (MaterialPartInstructService.PROCEDURE_INLINE.equals(procedure)) {
				listResponse.put("callReasons", true);
			}

			/* 查询 */
			Map<String, List<PartialPositionForm>> instructLists = posService.loadInstruct(model_id, conn);

			listResponse.put("instructLists", instructLists);
			listResponse.put("components", posService.getComponentOfModel(model_id, conn));

			UserDefineCodesService usdSerice = new UserDefineCodesService();
			String sPartHighpriceLever = usdSerice.searchUserDefineCodesValueByCode("PART_HIGHPRICE_LEVER", conn);
			listResponse.put("highprice", sPartHighpriceLever);
		}

		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("MaterialPartInstructAction.instructLoadByWorking end");
	}

	/**
	 * 按维修品读取工作指示单格式
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void reasonsLoadByWorking(ActionMapping mapping, ActionForm form, HttpServletRequest reques,
			HttpServletResponse response, SqlSession conn) {
		log.info("MaterialPartInstructAction.reasonsLoadByWorking start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		MaterialPartInstructService service = new MaterialPartInstructService();

		// 取得登录用户权限
		LoginData user = (LoginData) reques.getSession().getAttribute(RvsConsts.SESSION_USER);

		PositionPanelService posPService = new PositionPanelService();
		ProductionFeatureEntity workingPf = posPService.getProcessingPf(user, conn);

		if (workingPf != null) {
			String material_id = workingPf.getMaterial_id();
			cbResponse.put("reasons", service.getReasons(material_id, conn));
		}

		cbResponse.put("errors", errors);

		returnJsonResponse(response, cbResponse);
		log.info("MaterialPartInstructAction.reasonsLoadByWorking end");
	}

	public void reasonsLoadById(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) {
		log.info("MaterialPartInstructAction.reasonsLoadById start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		MaterialPartInstructService service = new MaterialPartInstructService();

		String material_id = request.getParameter("material_id");
		cbResponse.put("reasons", service.getReasons(material_id, conn));

		cbResponse.put("errors", errors);

		returnJsonResponse(response, cbResponse);
		log.info("MaterialPartInstructAction.reasonsLoadById end");
	}

	/**
	 * 按维修品读取工作指示单格式
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void doUpdateItem(ActionMapping mapping, ActionForm form, HttpServletRequest reques,
			HttpServletResponse response, SqlSessionManager conn) {
		log.info("MaterialPartInstructAction.doUpdateItem start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		MaterialPartInstructService service = new MaterialPartInstructService();

		// 取得登录用户权限
		LoginData user = (LoginData) reques.getSession().getAttribute(RvsConsts.SESSION_USER);

		List<String> triggerList = new ArrayList<String>();
		service.doUpdateItem(form, reques.getParameterMap(), user, triggerList, cbResponse, conn, errors);

		if (errors.size() == 0 && triggerList.size() > 0) {
			RvsUtils.sendTrigger(triggerList);
		}

		cbResponse.put("errors", errors);

		returnJsonResponse(response, cbResponse);
		log.info("MaterialPartInstructAction.doUpdateItem end");
	}


	/**
	 * 取得追加的零件清单
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void additionalOrderLoad(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) {
		log.info("MaterialPartInstructAction.additionalOrderLoad start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		MaterialPartInstructService service = new MaterialPartInstructService();

		String material_id = request.getParameter("material_id");

		service.getAdditionalOrder(request.getParameter("material_id"), cbResponse, conn);
		cbResponse.put("part_procedure", service.getMaterialPartProcedure(material_id, conn));

		UserDefineCodesService usdSerice = new UserDefineCodesService();
		String sPartHighpriceLever = usdSerice.searchUserDefineCodesValueByCode("PART_HIGHPRICE_LEVER", conn);
		cbResponse.put("highprice", sPartHighpriceLever);

		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);

		List<PartialPositionForm> list = service.getFocusPartialsByOperator(user.getOperator_id(), conn);
		cbResponse.put("focus_list", list);

		cbResponse.put("errors", errors);

		returnJsonResponse(response, cbResponse);
		log.info("MaterialPartInstructAction.additionalOrderLoad end");
	}

	/**
	 * 提交确认
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void doApplyConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) {
		log.info("MaterialPartInstructAction.doApplyConfirm start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String material_id = request.getParameter("material_id");

		MaterialPartInstructService service = new MaterialPartInstructService();
		MaterialPartInstructEntity materialPartProcedure = service.getMaterialPartProcedure(material_id, conn);

		List<String> triggerList = new ArrayList<String>();
		boolean updated = service.applyProcedureConfirm(material_id, materialPartProcedure, triggerList, user, conn);

		if (updated && triggerList.size() > 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		cbResponse.put("updated", updated);
		cbResponse.put("errors", errors);

		returnJsonResponse(response, cbResponse);
		log.info("MaterialPartInstructAction.doApplyConfirm end");
	}

	/**
	 * 导出追加零件列表
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void makeReport(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("MaterialPartInstructAction.makeReport start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		MaterialPartInstructService service = new MaterialPartInstructService();

		// 导出报表
		String filePath = service.makeAdditionalOrderFile(req.getParameter("material_id"), conn);

		listResponse.put("errors", errors);
		listResponse.put("filePath", filePath);

		returnJsonResponse(res, listResponse);
		log.info("MaterialPartInstructAction.makeReport end");
	}
}
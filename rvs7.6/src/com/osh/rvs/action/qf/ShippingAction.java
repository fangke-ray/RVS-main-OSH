package com.osh.rvs.action.qf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.qf.FactMaterialEntity;
import com.osh.rvs.bean.qf.TurnoverCaseEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.qf.ShippingMapper;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.qf.FactMaterialService;
import com.osh.rvs.service.qf.ShippingService;
import com.osh.rvs.service.qf.TurnoverCaseService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ShippingAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	private PositionPanelService ppService = new PositionPanelService();
	private ShippingService service = new ShippingService();

	/**
	 * 初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("ShippingAction.init start");

		req.setAttribute("oBoundOutOcm", CodeListUtils.getSelectOptions("material_direct_ocm", null, ""));

		Map<String, String> dmMap = CodeListUtils.getList("shipping_trolley");

		req.setAttribute("dm_styles", ppService.getDmStyles(dmMap));
//
//		String ret = "<span class=\"device_manage_select\"><select class=\"manager_no\" code=\"ER71101\">" + CodeListUtils.getSelectOptions(dmMap, "", null, false) + "</select></span>"
//				+ "<span class=\"device_manage_item ui-state-default\">推车No. 选择: </span>";

		req.setAttribute("oManageNo", dmMap);

		// req.setAttribute("oManageNo", ret);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ShippingAction.init end");
	}

	/**
	 * 工位画面初始取值处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void jsinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("ShippingAction.jsinit start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 设定OCM文字
		listResponse.put("oOptions", CodeListUtils.getGridOptions("material_direct_ocm"));
		// 设定等级文字
		listResponse.put("lOptions", CodeListUtils.getGridOptions("material_level"));

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();// TODO

		user.setSection_id(null);
		if (user.getPosition_id() == null) {
			user.setPosition_id(RvsConsts.POSITION_SHIPPING);
			user.setProcess_code("711");
			user.setLine_id("00000000011");
		}

		// 设定待点检信息
		String infectString = ppService.checkPositionInfectWorkOnPass(
				section_id, user.getPosition_id(), user.getLine_id(), user.getOperator_id(), conn, listResponse);

		infectString += ppService.getAbnormalWorkStateByOperator(user.getOperator_id(), conn);

		listResponse.put("infectString", infectString);

		// 取得等待区一览
		listRefresh(listResponse, user.getPosition_id(), conn);

		// 判断是否有在进行中的维修对象
		ProductionFeatureEntity workingPf = ppService.getWorkingOrSupportingPf(user, conn);
		// 进行中的话
		if (workingPf != null) {
			if (RvsConsts.OPERATE_RESULT_SUPPORT == workingPf.getOperate_result()) {
				MsgInfo msginfo = new MsgInfo();
				msginfo.setErrcode("info.linework.supportingRemain");
				msginfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.supportingRemain"));
				infoes.add(msginfo);
				listResponse.put("redirect", "support.do");
			} else {
				// 取得作业信息
				service.getProccessingData(listResponse, workingPf.getMaterial_id(), workingPf, user, conn);

				// 页面设定为编辑模式
				listResponse.put("workstauts", "1");
			}
		} else {
			// 准备中
			listResponse.put("workstauts", "0");
		}

		user.setSection_id(section_id); // TODO

		// 检查发生错误时报告错误信息
		listResponse.put("errors", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ShippingAction.jsinit end");
	}

	/**
	 * 扫描开始/直接暂停重开
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 0 })
	public void doscan(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("ShippingAction.scan start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("material_id");
		String trolley_code = req.getParameter("trolley_code");

		// 检查出货单是否制作
		MaterialService mService = new MaterialService();
		MaterialEntity mEntity = mService.loadSimpleMaterialDetailEntity(conn, material_id);

		if (mEntity == null) {
			// 维修对象不在用户所在等待区
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("material_id");
			msgInfo.setErrcode("info.linework.notInWaiting");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.notInWaiting"));
			errors.add(msgInfo);
		} else {
			TurnoverCaseService tcService = new TurnoverCaseService();
			TurnoverCaseEntity tce = tcService.getStorageByMaterial(material_id, conn);
			if (tce != null) {
				MsgInfo info = new MsgInfo();
				info.setErrcode("info.turnoverCase.materialShippingWithoutCase");
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.turnoverCase.materialShippingWithoutCase"));
				errors.add(info);
			}
		}

		if (errors.size() == 0) {
			if (mEntity.getBreak_back_flg() == 0) { // 维修品

				FactMaterialService factMaterialService = new FactMaterialService();
				FactMaterialEntity factMaterial = new FactMaterialEntity();
				factMaterial.setMaterial_id(material_id);
				factMaterial.setProduction_type(241);

				int len = factMaterialService.searchEntities(factMaterial, conn).size();
				if (len == 0) {
					MsgInfo info = new MsgInfo();
					info.setErrcode("info.material.shipping.sheet.empty");
					info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.material.shipping.sheet.empty"));
					errors.add(info);
				}
			}
		}

		log.info("ShippingAction.scan error" + errors.size());

		if (errors.size() == 0) {
			ShippingService service = new ShippingService();
			ProductionFeatureEntity waitingPf = service.scanMaterial(conn, material_id, req, errors, listResponse);

			if (errors.size() == 0 && trolley_code != null) {
				// 放入推车
				service.putinTrolley(material_id, trolley_code, conn);

				// 完成出货工位
				// 取得用户信息
				HttpSession session = req.getSession();
				LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

				ProductionFeatureService pfService = new ProductionFeatureService();
				// 标准作业时间
				String usString = RvsUtils.getZeroOverLine("_default", null, user, "711");
				Integer use_seconds = 300;
				try {
					use_seconds = Integer.valueOf(usString) * 60;
				} catch (Exception e) {
				}
				
				waitingPf.setUse_seconds(use_seconds);
				waitingPf.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
				pfService.finishProductionFeatureSetFinish(waitingPf, conn);

				listRefresh(listResponse, user.getPosition_id(), conn);
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ShippingAction.scan end");
	}

	/**
	 * 作业完成
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 0 })
	public void dofinish(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("ShippingAction.dofinish start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			service.updateMaterial(req, conn);

			// 取得用户信息
			HttpSession session = req.getSession();
			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

			listRefresh(listResponse, user.getPosition_id(), conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ShippingAction.dofinish end");
	}

	private void listRefresh(Map<String, Object> listResponse, String postion_id, SqlSession conn) {
		ShippingService sService = new ShippingService();
		// 取得待品保处理对象一览 711
		List<MaterialEntity> waitings = sService.getWaitingMaterial(postion_id, conn);

		// 取得今日已完成处理对象一览
		List<MaterialEntity> finished = sService.getFinishedMaterial(postion_id, conn);

		List<MaterialForm> waitingsForm = new ArrayList<MaterialForm>();
		List<MaterialForm> finishedForm = new ArrayList<MaterialForm>();

		BeanUtil.copyToFormList(waitings, waitingsForm, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);
		BeanUtil.copyToFormList(finished, finishedForm, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);

		listResponse.put("waitings", waitingsForm);
		listResponse.put("finished", finishedForm);

		listResponse.put("inTrolleyMaterials", sService.getInTrolleyMaterials(conn));
	}

	/**
	 * 推车清空
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 0 })
	public void doFinishForTrolley(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("ShippingAction.doFinishForTrolley start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String trolley_code = req.getParameter("trolley_code");

		if (errors.size() == 0) {
			service.finishForTrolley(trolley_code, conn);

			// 取得用户信息
			HttpSession session = req.getSession();
			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

			// 取得今日已完成处理对象一览
			List<MaterialEntity> finished = service.getFinishedMaterial(user.getPosition_id(), conn);

			List<MaterialForm> finishedForm = new ArrayList<MaterialForm>();

			BeanUtil.copyToFormList(finished, finishedForm, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);

			listResponse.put("finished", finishedForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ShippingAction.doFinishForTrolley end");
	}
}

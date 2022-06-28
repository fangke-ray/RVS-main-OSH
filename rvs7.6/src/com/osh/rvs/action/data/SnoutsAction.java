package com.osh.rvs.action.data;

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
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.data.SnoutEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.inline.SnoutForm;
import com.osh.rvs.form.partial.ComponentSettingForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.inline.SoloSnoutService;
import com.osh.rvs.service.partial.ComponentSettingService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class SnoutsAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

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
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		String from = (String) req.getParameter("from");

		if (from != null && !"".equals(from)) {
			req.setAttribute("from", from);
		}

		log.info("SnoutAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		req.setAttribute("mOptions", ComponentSettingService.getModelHistoryChooser(conn));
		req.setAttribute("rOptions", "<option value>全部</option><option value='0'>制造中</option><option value='1' selected>待检测</option><option value='2'>已检测</option><option value='3'>已使用</option>");

		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合

		SoloSnoutService service = new SoloSnoutService();
		// 处理者信息取得
		String oReferChooser = service.getSnoutsMakerReferChooser(conn);
		// 处理者信息设定
		req.setAttribute("oReferChooser", oReferChooser);

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			req.setAttribute("role", "fact");
			if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
				req.setAttribute("role", "both");
			}
		} else if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
			req.setAttribute("role", "line");
		}

		log.info("SnoutAction.init end");
	}

	public void getSettings(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SnoutsAction.getSettings start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> msgInfos = v.validate();

		ComponentSettingService settingService = new ComponentSettingService();
		List<ComponentSettingForm> list = settingService.getAllSnoutComponentSettings(conn);
		callbackResponse.put("list", list);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.getSettings end");
	}

	@Privacies(permit={1, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SnoutsAction.search start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> msgInfos = v.validate();

		SoloSnoutService service = new SoloSnoutService();
		List<SnoutForm> list = service.search(form, conn);
		callbackResponse.put("list", list);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.search end");
	}

	@Privacies(permit = { 1, 0 })
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("SnoutsAction.detail start");

		String serial_no = req.getParameter("serial_no");

		if (isEmpty(serial_no)) {
			actionForward = mapping.findForward("error");
		} else {
			SoloSnoutService service = new SoloSnoutService();
			SnoutForm retForm = service.getDetail(serial_no, "00000000024", conn);
			String pcs = service.getSnoutPcs(serial_no, retForm.getModel_name(), conn);
			req.setAttribute("snout", retForm);
			req.setAttribute("pcs", pcs);
			req.setAttribute("mOptions", ComponentSettingService.getModelReferChooser(conn));
		}

		actionForward = mapping.findForward("detail");

		log.info("SnoutsAction.detail end");
	}

	@Privacies(permit = { 1, 0 })
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("SnoutsAction.doUpdate start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SoloSnoutService service = new SoloSnoutService();
		SnoutEntity updateBean = service.checkUpdate(req, conn, msgInfos);

		if (msgInfos.size() == 0) {
			if (updateBean != null) {
				// TODO Update
			}
			String pcs_inputs = req.getParameter("pcs_inputs");
			String pcs_comments = req.getParameter("pcs_comments");
			if (pcs_inputs != null && pcs_comments != null) {
				HttpSession session = req.getSession();
				LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

				String used_material_id = null;
				if (!isEmpty(req.getParameter("sorc_no"))) {
					used_material_id = service.findUsedSnoutsBySnouts(req.getParameter("serial_no"), conn);
				}
				// 保存线长工程检查票记录
				service.saveLeaderInput(req, user, used_material_id, conn);
			}
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.doUpdate end");
	}

	@Privacies(permit = { 1, 0 })
	public void doDelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("SnoutsAction.doDelete start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SoloSnoutService service = new SoloSnoutService();

		if (msgInfos.size() == 0) {
			String position_id = req.getParameter("position_id");
			// 删除预置品
			service.delete(position_id, req.getParameter("model_id"), req.getParameter("serial_no"), conn);

			if ("00000000024".equals(position_id)) {
				// 检查安全库存
				service.checkBenchmark(req.getParameter("model_id"), conn);
			}
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.doDelete end");
	}

	@Privacies(permit={1, 0})
	public void searchSnoutsOnMonth(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SnoutsAction.searchSnoutsOnMonth start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SoloSnoutService service = new SoloSnoutService();
		List<SnoutForm> list = service.searchSnoutsOnMonth(req.getParameter("year_month"), conn);
		callbackResponse.put("list", list);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.searchSnoutsOnMonth end");
	}

	public void showPrepairSnoutList(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SnoutsAction.showPrepairSnoutList start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SoloSnoutService service = new SoloSnoutService();
		List<SnoutEntity> listEntities = service.getUsableOriginByModel(req.getParameter("model_id"), conn);
		List<SnoutForm> list = new ArrayList<SnoutForm>();
		BeanUtil.copyToFormList(listEntities, list, CopyOptions.COPYOPTIONS_NOEMPTY, SnoutForm.class);
		callbackResponse.put("list", list);

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
			callbackResponse.put("tobe_list", service.getTobeOriginByModel(req.getParameter("model_id"), conn));
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.showPrepairSnoutList end");
	}

	/**
	 * 操作回收先端头
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doRecover(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("SnoutsAction.doRecover start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SoloSnoutService service = new SoloSnoutService();

		String org_material_id = req.getParameter("material_id");
		String step = req.getParameter("step");

		if (msgInfos.size() == 0) {
			// 回收预置品
			if (!"3".equals(step)) { // 3 = 已登陆的清洗回收
				service.setToOrigin(org_material_id, conn);
			}

			if (!"1".equals(step)) { // 1 = 仅登录
				String pcs_inputs = req.getParameter("pcs_inputs");

				if (pcs_inputs == null) {
					service.getRecoverPcs(org_material_id, callbackResponse, conn);
				} else {
					// 取得登录用户权限
					LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
					List<Integer> privacies = user.getPrivacies();

					if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
						service.saveLeaderInput(req, user, org_material_id, conn);
					}
				}

				// 灭菌等待
				List<String> eogPositionId = PositionService.getPositionsBySpecialPage("snout_eog", conn);
				if (eogPositionId != null) {
					ProductionFeatureService pfService = new ProductionFeatureService();
					List<String> triggerList = new ArrayList<String>();
					ProductionFeatureEntity workingPf = new ProductionFeatureEntity();
					workingPf.setMaterial_id(org_material_id);
					workingPf.setPosition_id(eogPositionId.get(0));
					workingPf.setSection_id("00000000009");
					workingPf.setRework(0);
					workingPf.setPace(0);

					pfService.fingerSpecifyPosition(org_material_id, true, workingPf, triggerList, conn);
					if (triggerList.size() > 0) {
						conn.commit();
						RvsUtils.sendTrigger(triggerList);
					}
				}
			}
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.doRecover end");
	}

	public void doAbandon(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("SnoutsAction.doAbandon start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SoloSnoutService service = new SoloSnoutService();

		String org_material_id = req.getParameter("material_id");
		if (msgInfos.size() == 0) {
			// 删除预置品
			service.abandon(org_material_id, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.doAbandon end");
	}

	public void getSnoutHeadHistory(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SnoutsAction.getSnoutHeadHistory start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SoloSnoutService service = new SoloSnoutService();
		callbackResponse.put("list", service.getSnoutHeadHistory(req.getParameter("material_id"), conn));

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.getSnoutHeadHistory end");
	}

	public void getRecoverPcs(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SnoutsAction.getRecoverPcs start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SoloSnoutService service = new SoloSnoutService();
		String originId =  req.getParameter("material_id");
		service.getRecoverPcs(originId, callbackResponse, conn);

		// 检查是否已经登录
		boolean bExist = service.checkUsedForOrigin(originId, conn);
		callbackResponse.put("registed", bExist);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.getRecoverPcs end");
	}

	public void getStorage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SnoutsAction.getStorage start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SnoutEntity conBean = new SnoutEntity();
		BeanUtil.copyToBean(form, conBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		ComponentSettingService settingService = new ComponentSettingService();

		String snoutStorageHtml = settingService.getSnoutStorageHtml(conBean.getModel_id(), conn);
		callbackResponse.put("snoutStorageHtml", snoutStorageHtml);

		SoloSnoutService service = new SoloSnoutService();

		List<String> slots = service.getSlotsFromSnoutComponentStorageByModel(conBean.getModel_id(), conn);
		callbackResponse.put("slots", slots);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.getStorage end");
	}

	public void doMoveStorage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("SnoutsAction.doMoveStorage start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		SoloSnoutService service = new SoloSnoutService();

		SnoutEntity conBean = new SnoutEntity();
		BeanUtil.copyToBean(form, conBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		service.setSnoutComponentStorage(conBean.getSerial_no(), conBean.getSlot(), conBean.getModel_id(), conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SnoutsAction.doMoveStorage end");
	}

}

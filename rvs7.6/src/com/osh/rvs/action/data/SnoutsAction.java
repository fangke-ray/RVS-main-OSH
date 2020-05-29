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
import com.osh.rvs.bean.data.SnoutEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.inline.SnoutForm;
import com.osh.rvs.service.inline.SoloSnoutService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
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

		log.info("SnoutAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		req.setAttribute("mOptions", CodeListUtils.getSelectOptions(RvsUtils.getSnoutModels(conn), "", "(未选择)", false));
		req.setAttribute("rOptions", "<option value>全部</option><option value='0'>制造中</option><option value='1' selected>待检测</option><option value='2'>已检测</option><option value='3'>已使用</option>");

		SoloSnoutService service = new SoloSnoutService();
		// 处理者信息取得
		String oReferChooser = service.getSnoutsMakerReferChooser(conn);
		// 处理者信息设定
		req.setAttribute("oReferChooser", oReferChooser);

		log.info("SnoutAction.init end");
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
			req.setAttribute("mOptions", CodeListUtils.getSelectOptions(RvsUtils.getSnoutModels(conn), retForm.getModel_id(), null, false));
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

}

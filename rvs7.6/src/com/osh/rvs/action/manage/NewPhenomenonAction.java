package com.osh.rvs.action.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.manage.NewPhenomenonForm;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.manage.NewPhenomenonService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class NewPhenomenonAction extends BaseAction {

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

		log.info("NewPhenomenonAction.init start");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		if (user.getPrivacies().contains(RvsConsts.PRIVACY_LINE)) {
			req.setAttribute("enableEdit", true);
		}

		LineService lineService = new LineService();
		// 工程信息取得
		String lOptions = lineService.getOptions(conn);
		// 工程信息设定
		req.setAttribute("lOptions", lOptions);

		// 取得类别下拉框信息
		String kOptions = CodeListUtils.getSelectOptions("category_kind", null, "", false);
		req.setAttribute("kOptions", kOptions);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("NewPhenomenonAction.init end");
	}

	/**
	 * 初始化/编辑画面
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("NewPhenomenonAction.detail start");

		// 警报信息ID
		String key = req.getParameter("alarm_message_id");

		NewPhenomenonService service = new NewPhenomenonService();
		NewPhenomenonForm newPhenomenonForm = service.getNewNewPhenomenon(key, conn);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		boolean enableEdit = false, enablePost = false;

		// 还未建立新现象信息
		if (newPhenomenonForm == null) {
			newPhenomenonForm = service.getNewNewPhenomenonFormAlarm(key, conn);

			if (newPhenomenonForm != null) {
				if ("07".equals(newPhenomenonForm.getKind())) {
					// 周边
				}

				if (user.getPrivacies().contains(RvsConsts.PRIVACY_LINE)) {
					enableEdit = true;
				}
				if (user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)) {
					enableEdit = true;
					enablePost = true;
				}
			}

		} else if (newPhenomenonForm.getReturn_status() == null ||
				!"OK".equals(newPhenomenonForm.getReturn_status())) {
			if (user.getPrivacies().contains(RvsConsts.PRIVACY_LINE)) {
				enableEdit = true;
			}
			if (user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)) {
				enableEdit = true;
				enablePost = true;
			}
		}

		if (newPhenomenonForm != null) {
			if (enableEdit && !"07".equals(newPhenomenonForm.getKind())) {
				// 取得参考故障位置
				Map<String, List<String>> locationMap = service.getLocationMap(conn);
				req.setAttribute("locationMap", JSON.encode(locationMap));
			}

			req.setAttribute("newPhenomenonForm", newPhenomenonForm);
			req.setAttribute("enableEdit", new Boolean(enableEdit));
			req.setAttribute("enablePost", new Boolean(enablePost));
		}

		// 迁移到页面
		actionForward = mapping.findForward("detail");

		log.info("NewPhenomenonAction.detail end");
	}

	/**
	 * 设备类别查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("NewPhenomenonAction.search start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			NewPhenomenonService service = new NewPhenomenonService();

			// 执行检索
			List<NewPhenomenonForm> amResultForm = service.search(form, conn, errors);

			// 查询结果放入Ajax响应对象
			listResponse.put("list", amResultForm);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(res, listResponse);

		log.info("NewPhenomenonAction.search end");
	}

	/**
	 * 新现象更新实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("NewPhenomenonAction.doupdate start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			NewPhenomenonService service = new NewPhenomenonService();

			String post = req.getParameter("post");

			// 执行更新
			service.update(form, req.getSession(), post != null, conn, errors);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("NewPhenomenonAction.doupdate end");
	}
}

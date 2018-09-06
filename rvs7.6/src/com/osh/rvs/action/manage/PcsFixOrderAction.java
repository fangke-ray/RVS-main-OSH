/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：修改工程检查票填写内容<br>
 * @author 龚镭敏
 * @version 1.21
 */
package com.osh.rvs.action.manage;

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
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.manage.PcsFixOrderForm;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.manage.PcsFixOrderService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

public class PcsFixOrderAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 修改工程检查票填写内容画面初始表示处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 2, 0 })
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("PcsFixOrderAction.init start");

		req.setAttribute("today_date", DateUtil.toString(new Date(), DateUtil.DATE_PATTERN));
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("PcsFixOrderAction.init end");
	}

	/**
	 * 修改工程检查票填写内容画面初始数据取得
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void jsinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PcsFixOrderAction.jsinit start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		PcsFixOrderService service = new PcsFixOrderService();
		List<PcsFixOrderForm> waitingForms = service.findWaiting(conn);
		List<PcsFixOrderForm> finishedForms = service.findFinished(form, conn);

		lResponseResult.put("waiting", waitingForms);
		lResponseResult.put("finished", finishedForms);
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsFixOrderAction.jsinit end");
	}

	/**
	 * 建立“修改工程检查票填写内容”申请
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doCreate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PcsFixOrderAction.doCreate start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> msgInfos = v.validate();

		if (msgInfos.size() == 0) {
			PcsFixOrderService service = new PcsFixOrderService();
			service.create(form, req.getSession(), conn);
		}

		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsFixOrderAction.doCreate end");
	}

	/**
	 * 修正更新工程检查票
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doFix(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PcsFixOrderAction.doFix start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		MaterialService service = new MaterialService();
		service.saveManagerInput(req, user, conn);
		service.fixResolver(req, user, conn);

		String backDoor = req.getParameter("back_door");
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_SA)
				|| (backDoor != null && privacies.contains(RvsConsts.PRIVACY_ADMIN) )) {
			service.fixInput(req, user, conn);
		}

		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsFixOrderAction.doFix end");
	}

	/**
	 * 建立“工位操作清除”申请
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doPcCreate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PcsFixOrderAction.doPcCreate start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> msgInfos = v.validate();

		if (msgInfos.size() == 0) {
			PcsFixOrderService service = new PcsFixOrderService();
			service.createPositionClean(form, req.getSession(), conn);
		}

		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsFixOrderAction.doPcCreate end");
	}

	/**
	 * 处理“工位操作清除”画面表示
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 2, 0 })
	public void pcpage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("PcsFixOrderAction.pcpage start");

		String key = req.getParameter("pcs_fix_order_key");
		PcsFixOrderService service = new PcsFixOrderService();
		PcsFixOrderForm pcsFixOrderForm = service.getRet5(key, conn);

		String pcComment = pcsFixOrderForm.getComment();
		String position_id = pcComment.substring(0, 11);
		PositionService pService = new PositionService();
		PositionEntity pEntity =pService.getPositionEntityByKey(position_id, conn);

		req.setAttribute("position_id", position_id);
		req.setAttribute("comment", pcComment.substring(12));
		req.setAttribute("process_code", pEntity.getProcess_code());

		// 迁移到页面
		actionForward = mapping.findForward("pcpage");

		log.info("PcsFixOrderAction.pcpage end");
	}

	/**
	 * 建立“工位操作清除”处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doPcResolve(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PcsFixOrderAction.doPcResolve start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String dispatch = req.getParameter("dispatch");

		PcsFixOrderService service = new PcsFixOrderService();
		service.resolvePc(req, user, conn, dispatch);

		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsFixOrderAction.doPcResolve end");
	}

}

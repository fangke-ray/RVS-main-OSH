package com.osh.rvs.action.report;

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
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.report.RemainTimeReportForm;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.manage.RemainTimeReportService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Title RemainTimeReportAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.report
 * @ClassName: RemainTimeReportAction
 * @Description: 倒计时达成率
 * @author houp
 * @date 2017-02-22 下午1:40:33
 */
public class RemainTimeReportAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * 页面初始化
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("RemainTimeReportAction.init start");

		// 工位信息取得
		PositionService positionService = new PositionService();
		String pReferChooser = positionService.getOptions(conn);
		// 工位信息设定
		req.setAttribute("pReferChooser", pReferChooser);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		// 权限区分
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
			req.setAttribute("role", "manager");
		} else {
			req.setAttribute("role", "none");
		}
		req.setAttribute("today", DateUtil.toString(new Date(), DateUtil.DATE_PATTERN));

		// 判定理由文字
		req.setAttribute("cOptions", CodeListUtils.getSelectOptions("countdown_unreach_main_cause", null, ""));
		req.setAttribute("cGO", CodeListUtils.getGridOptions("countdown_unreach_main_cause"));

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("RemainTimeReportAction.init end");
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("RemainTimeReportAction.search start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		errors = v.validate();

		RemainTimeReportForm searchForm = (RemainTimeReportForm)form;
		RemainTimeReportService service = new RemainTimeReportService();

		if (errors.size() == 0) {
			service.searchChatData(searchForm, listResponse, conn);
		}
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("RemainTimeReportAction.search end");
	}

	/**
	 * 倒计时未达成一览查询
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchUnreach(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("RemainTimeReportAction.searchUnreach start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();

		RemainTimeReportForm searchForm = (RemainTimeReportForm)form;
		RemainTimeReportService service = new RemainTimeReportService();

		if (errors.size() == 0) {
			service.searchUnreach(searchForm, listResponse, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("RemainTimeReportAction.searchUnreach end");
	}

	/**
	 * 倒计时未达成详细查询
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getUnreachDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("RemainTimeReportAction.getUnreachDetail start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();

		RemainTimeReportForm searchForm = (RemainTimeReportForm)form;
		RemainTimeReportService service = new RemainTimeReportService();

		if (errors.size() == 0) {
			RemainTimeReportForm detail = service.getUnreachDetail(searchForm, conn);
			listResponse.put("detail", detail);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("RemainTimeReportAction.getUnreachDetail end");
	}

	/**
	 * 倒计时未达成备注填写
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdateUnreachComment(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("RemainTimeReportAction.doUpdateUnreachComment start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();

		RemainTimeReportForm updateForm = (RemainTimeReportForm)form;
		RemainTimeReportService service = new RemainTimeReportService();

		if (errors.size() == 0) {
			service.updateUnreachCommentl(updateForm, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("RemainTimeReportAction.doUpdateUnreachComment end");
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void export(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("RemainTimeReportAction.export start");

		RemainTimeReportForm exportForm = (RemainTimeReportForm)form;
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();
		
		RemainTimeReportService service = new RemainTimeReportService();
		String fileName ="倒计时达成率.xls";
		String filePath = service.createExcel(exportForm, conn);
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("RemainTimeReportAction.export end");
	}
}

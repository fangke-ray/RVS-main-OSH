package com.osh.rvs.action.infect;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.service.CheckResultService;
import com.osh.rvs.service.DevicesTypeService;
import com.osh.rvs.service.PositionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.DateUtil;

public class UsageCheckAction extends BaseAction {
	private Logger log=Logger.getLogger(getClass());

	/**
	 * 页面初始化
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("UsageCheckAction.init start");
		
		//工位对处  	线长操作
		request.setAttribute("goPositionHandle", CodeListUtils.getSelectOptions("position_handle", null, ""));

		// 工位信息取得
		PositionService positionService = new PositionService();
		String pReferChooser = positionService.getOptions(conn);
		// 工位信息设定
		request.setAttribute("pReferChooser", pReferChooser);

		//品名
		DevicesTypeService devicesTypeService = new DevicesTypeService();
		String nReferChooser=devicesTypeService.getDevicesTypeReferChooser(conn);
		request.setAttribute("nReferChooser", nReferChooser);

		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);

		// 页面跳转对应
		String from = request.getParameter("from");
		if("position".equals(from)) {
			request.setAttribute("selectedPosition", loginData.getPosition_id());
		}

		boolean isLeader = loginData.getPrivacies().contains(RvsConsts.PRIVACY_DT_MANAGE);

		request.setAttribute("isLeader", isLeader);

		actionForward=mapping.findForward(FW_INIT);

		log.info("UsageCheckAction.init end");
	}

	/**
	 * 页面打开后,判断要点检的项目,然后出一览
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doJsinit(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("UsageCheckAction.doJsinit start");
		
		// 对Ajax的响应
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		CheckResultService service = new CheckResultService();

		// 如果没有登录者目前的待点检记录，建立。
		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);
		int isLeader = 0;
		if (loginData.getPrivacies().contains(RvsConsts.PRIVACY_DT_MANAGE)) isLeader = 1;
		if (loginData.getPrivacies().contains(RvsConsts.PRIVACY_TECHNOLOGY)) isLeader = 2;

		if (isLeader < 2) {
			String ret = service.checkAndCreateItems(loginData.getSection_id(), loginData.getOperator_id(), 
					loginData.getPositions(), loginData.getPosition_id(), loginData.getLine_id(), 
					form, callbackResponse, conn, isLeader);
			if (!isEmpty(ret)) {
				MsgInfo info = new MsgInfo();
				info.setErrmsg(ret);
				infoes.add(info);
			}
		}

		// 返回提示信息
		callbackResponse.put("infoes", infoes);
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, callbackResponse);

		log.info("UsageCheckAction.doJsinit end");
	}
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("UsageCheckAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		CheckResultService service = new CheckResultService();

		// 如果没有登录者目前的待点检记录，建立。
		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);
		int isLeader = 0;
		if (loginData.getPrivacies().contains(RvsConsts.PRIVACY_DT_MANAGE)) isLeader = 1;
		if (loginData.getPrivacies().contains(RvsConsts.PRIVACY_TECHNOLOGY)) isLeader = 2;

		String ret = service.checkAndCreateItems(loginData.getSection_id(), loginData.getOperator_id(), loginData.getPositions(), loginData.getPosition_id(), loginData.getLine_id(), 
				form, callbackResponse, conn, isLeader);

		if (!isEmpty(ret)) {
			MsgInfo info = new MsgInfo();
			info.setErrmsg(ret);
			infoes.add(info);
		}

		// 返回提示信息
		callbackResponse.put("infoes", infoes);
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, callbackResponse);

		log.info("UsageCheckAction.search end");
	}

	/**
	 * 设备点检票页面
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void getChecksheet(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("UsageCheckAction.getChecksheet start");
		
		// 对Ajax的响应
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		CheckResultService service = new CheckResultService();

		String object_type = request.getParameter("object_type");

		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);
		int isLeader = 0;
		if (loginData.getPrivacies().contains(RvsConsts.PRIVACY_DT_MANAGE)) isLeader = 1;
		if (loginData.getPrivacies().contains(RvsConsts.PRIVACY_TECHNOLOGY)) isLeader = 2;

		String selectDate = request.getParameter("select_date");
		Date adjustDate = null;
		if (selectDate != null) {
			adjustDate = DateUtil.toDate(selectDate, DateUtil.DATE_PATTERN);
		}
		if (adjustDate == null) {
			adjustDate = new Date();
		}

		String content = "";
		// 治具
		if ("2".equals(object_type)) {
			content = service.getToolCheckSheet(request.getParameter("section_id"), request.getParameter("position_id"), 
					loginData.getOperator_id(), request.getParameter("operator_id"), isLeader, adjustDate, conn);
		} else {
			// 设备工具
			String check_file_manage_id = request.getParameter("check_file_manage_id");
			if ("00000000098".equals(check_file_manage_id)) {
				// 力矩工具
				content = service.getTorsionDeviceCheckSheet(request.getParameter("section_id"), request.getParameter("position_id"),
						check_file_manage_id, loginData.getOperator_id(), isLeader > 0, adjustDate, conn);
			} else if ("00000000053".equals(check_file_manage_id)) {
				// 电烙铁工具
				content = service.getElectricIronDeviceCheckSheet(request.getParameter("section_id"), request.getParameter("position_id"),
						check_file_manage_id, loginData.getOperator_id(), isLeader > 0, adjustDate, conn);
			} else {
				// 普通设备工具
				content = service.getDeviceCheckSheet(request.getParameter("manage_id"), check_file_manage_id, 
						loginData.getOperator_id(), isLeader > 0, adjustDate, conn);
			}
		}
		callbackResponse.put("check_sheet", content);

		callbackResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, callbackResponse);

		log.info("UsageCheckAction.getChecksheet end");
	}

	public void getCheckComment(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("UsageCheckAction.getCheckComment start");
		// 对Ajax的响应
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		CheckResultService service = new CheckResultService();

		String manage_id = request.getParameter("manage_id");
		String object_type = request.getParameter("object_type");
		String selectDate = request.getParameter("select_date");
		String check_file_manage_id = request.getParameter("check_file_manage_id");
		Date adjustDate = null;
		if (selectDate != null) {
			adjustDate = DateUtil.toDate(selectDate, DateUtil.DATE_PATTERN);
		}
		if (adjustDate == null) {
			adjustDate = new Date();
		}

		String comments = service.getCheckCommentInPeriod(manage_id, object_type, check_file_manage_id, adjustDate, conn);

		callbackResponse.put("comments", comments);
		callbackResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, callbackResponse);

		log.info("UsageCheckAction.getCheckComment end");
	}
	public void doCheckPoint(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("UsageCheckAction.doCheckPoint start");
		// 对Ajax的响应
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 返回Json格式响应信息
		returnJsonResponse(response, callbackResponse);

		CheckResultService service = new CheckResultService();

		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);
		boolean isLeader = loginData.getPrivacies().contains(RvsConsts.PRIVACY_DT_MANAGE);
		String selectDate = request.getParameter("select_date");
		Date adjustDate = null;
		if (selectDate != null) {
			adjustDate = DateUtil.toDate(selectDate, DateUtil.DATE_PATTERN);
		}
		if (adjustDate == null) {
			adjustDate = new Date();
		}
		service.checkPoint(request, loginData, isLeader, conn);

		String refix = request.getParameter("refix");
		if (isLeader && refix != null) {
			service.refix(request.getParameter("position_id"), loginData, conn);
		}

		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, callbackResponse);

		log.info("UsageCheckAction.doCheckPoint end");
	}
	public void doInputCheckComment(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("UsageCheckAction.doInputCheckComment start");
		// 对Ajax的响应
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		CheckResultService service = new CheckResultService();

		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String manage_id = request.getParameter("manage_id");
		String object_type = request.getParameter("object_type");
		String selectDate = request.getParameter("select_date");
		String comment = request.getParameter("comment");

		Date adjustDate = null;
		if (selectDate != null) {
			adjustDate = DateUtil.toDate(selectDate, DateUtil.DATE_PATTERN);
		}
		if (adjustDate == null) {
			adjustDate = new Date();
		}

		service.inputCheckComment(manage_id, object_type, loginData.getOperator_id(), comment, adjustDate, conn);

		callbackResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, callbackResponse);

		log.info("UsageCheckAction.doInputCheckComment end");
	}

}

package com.osh.rvs.action;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.mapper.master.OperatorMapper;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.CryptTool;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ChangePasswordAction extends BaseAction {
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
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("ChangePasswordAction.init start");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String pwdDateMessage = "";

		String operator_id = user.getOperator_id();

		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		Date pwdDate = dao.getPwdDate(operator_id);

		if (pwdDate != null) {
			Calendar pwdCal = Calendar.getInstance();
			pwdCal.setTime(pwdDate);
			Calendar valveCal = Calendar.getInstance();
			valveCal.add(Calendar.DATE, -60);
			if (valveCal.after(pwdCal)) {
				pwdDateMessage = ApplicationMessage.WARNING_MESSAGES.getMessage("info.password.timeoutPassword",
						DateUtil.toString(pwdDate, DateUtil.ISO_DATE_PATTERN), 60);
			} else {
				pwdDateMessage = ApplicationMessage.WARNING_MESSAGES.getMessage("info.password.steadyPassword",
						DateUtil.toString(pwdDate, DateUtil.ISO_DATE_PATTERN));
			}
		} else {
			pwdDateMessage = ApplicationMessage.WARNING_MESSAGES.getMessage("info.password.initPassword");
		}

		req.setAttribute("pwdDateMessage", pwdDateMessage);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ChangePasswordAction.init end");
	}

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
	public void doChange(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		log.info("ChangePasswordAction.doChange start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String job_no = user.getJob_no();

		String old_input = req.getParameter("old_input");
		String new_input = req.getParameter("new_input");
		String new_confirm = req.getParameter("new_confirm");

		// login.invalidPassword
		// 表单复制到数据对象
		OperatorEntity conditionBean = new OperatorEntity();
		// 按工号查询
		conditionBean.setJob_no(job_no);

		// 按工号密码查询
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		String password = old_input;
		password = CryptTool.encrypttoStr(password);
		password = CryptTool.encrypttoStr(password + conditionBean.getJob_no().toUpperCase());
		conditionBean.setPwd(password);
		LoginData loginData = dao.searchLoginOperator(conditionBean);

		// 密码不匹配
		if (loginData == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("old_input");
			error.setErrcode("login.invalidPassword");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("login.invalidPassword"));
			errors.add(error);
		}

		// validator.required
		if (CommonStringUtil.isEmpty(new_input)) {
			MsgInfo info = new MsgInfo();
			info.setComponentid("new_input");
			info.setErrcode("validator.required");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "新密码"));
			errors.add(info);
		} else {
			if (!new_input.equals(new_confirm)) {
				MsgInfo info = new MsgInfo();
				info.setComponentid("new_confirm");
				info.setErrcode("validator.notequals");
				info.setErrmsg("两次输入的的新密码不一致，请重新确认后输入。");
				errors.add(info);
			}
			if (new_input.toLowerCase().contains(job_no.toLowerCase()) || job_no.toLowerCase().contains(new_input.toLowerCase())) {
				MsgInfo info = new MsgInfo();
				info.setComponentid("new_input");
				info.setErrcode("validator.jobn");
				info.setErrmsg("请不要将工号作为新密码。");
				errors.add(info);
			}
		}

		if (errors.size() == 0) {
			String encryptedPwd = CryptTool.encrypttoStr(new_input);
			// 加密
			Date pwdDate = new Date();

			conditionBean.setOperator_id(user.getOperator_id());
			conditionBean.setPwd_date(pwdDate);
			conditionBean.setPwd(CryptTool.encrypttoStr(encryptedPwd + job_no.toUpperCase()));

			// 更新密码。
			dao.updatePassword(conditionBean);

//			String pwdDateMessage = ApplicationMessage.WARNING_MESSAGES.getMessage("info.password.steadyPassword",
//					DateUtil.toString(pwdDate, DateUtil.ISO_DATE_PATTERN));

			callbackResponse.put("pwdDateMessage", "更新成功！");
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("ChangePasswordAction.doChange end");
	}
}

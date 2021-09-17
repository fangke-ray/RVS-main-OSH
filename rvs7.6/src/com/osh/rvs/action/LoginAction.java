package com.osh.rvs.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.MaterialProcessEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.master.LineEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.SectionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.RedirectRes;
import com.osh.rvs.form.master.OperatorForm;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.master.SectionMapper;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.MaterialProcessService;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.RoleService;
import com.osh.rvs.service.equipment.DeviceJigLoanService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.inline.SoloSnoutService;
import com.osh.rvs.service.report.ProcedureManualService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CryptTool;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class LoginAction extends BaseAction {
	protected static final Logger _logger = Logger.getLogger("ACCOUNT");

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		_logger.info("LoginAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		_logger.info("LoginAction.init end");
	}

	public void consumable(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		_logger.info("LoginAction.consumable start");

		// 迁移到pda登陆页面
		actionForward = mapping.findForward("pda_init");

		_logger.info("LoginAction.consumable end");
	}

	public void login(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		_logger.fine("LoginAction.login start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("role_id", "name");
		v.add("pwd", v.required("密码"));

		List<MsgInfo> errors = v.validate();

		// 检查不通过的情况下
		if (!errors.isEmpty()) {
			// 检查发生错误时报告错误信息
			callbackResponse.put("errors", errors);

			// 返回Json格式响应信息
			returnJsonResponse(res, callbackResponse);
			return;
		}

		// 建立会话用户信息
		Map<String, String> roles = makeSession((OperatorForm)form, req.getSession(), errors, conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		callbackResponse.put("roles", roles);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		_logger.fine("run end");
	}

	public void logoff(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		_logger.fine("LoginAction.logoff start");
		req.getSession().removeAttribute(RvsConsts.SESSION_USER);
		req.getSession().removeAttribute(RvsConsts.DONT_CARE);
		_logger.fine("run end");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
	}

	public void sessionTimeout(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		_logger.info("LoginAction.sessionTimeout start");

		// 写跳转
		PrintWriter out;
		try {
			res.setCharacterEncoding("UTF-8");
			out = res.getWriter();
			out.print("<script type=\"text/javascript\">window.top.location.href='login.do'</script>");
			out.flush();
		} catch (IOException e) {
			// 迁移到页面
			actionForward = mapping.findForward(FW_INIT);
		}

		_logger.info("LoginAction.sessionTimeout end");
	}

	public void selectrole(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		_logger.fine("LoginAction.selectrole start");

		String role_id = req.getParameter("role_id");
		String role_name = req.getParameter("role_name");

		setRoleIntoSession(role_id, role_name, req.getSession(), conn);
		RedirectRes rres = new RedirectRes();
		// 返回Json格式响应信息
		returnJsonResponse(res, rres);

		_logger.fine("run end");
	}

	private void setRoleIntoSession(String role_id, String role_name, HttpSession session, SqlSession conn) {
		LoginData loginData = (LoginData) session.getAttribute("K" + RvsConsts.SESSION_USER);
		// TODO check role
		if (!"main".equals(role_id)) {
			loginData.setRole_id(role_id);
			loginData.setRole_name(role_name);
		}
		ProductionFeatureEntity workingPf = getWoringPf(loginData, conn);
		// 选择的角色
		setDetail(loginData, conn, workingPf);
		// 用户信息保存在会话中
		session.setAttribute(RvsConsts.SESSION_USER, loginData);
		// <Context path="/rvs" debug="0" reloadable="false" crossContext="true"/>
		session.removeAttribute("K" + RvsConsts.SESSION_USER);
		ServletContext contextMain =session.getServletContext();
		contextMain.setAttribute("login_name", loginData.getName());

		DeviceJigLoanService djlService = new DeviceJigLoanService();
		djlService.checkLoaningNow(loginData.getOperator_id(), session, conn);

		// contextMe.getContext("");
		// ServletContext contextMain= contextMe.getContext("/rvs");
	}

	private ProductionFeatureEntity getWoringPf(LoginData loginData, SqlSession conn) {
		PositionPanelService service = new PositionPanelService();
		ProductionFeatureEntity workingPf = service.getProcessingPf(loginData, conn);
		return workingPf;
	}

	private void setDetail(LoginData loginData, SqlSession conn, ProductionFeatureEntity workingPf) {
		loginData.setPrivacies(getPrivacies(loginData.getRole_id(), conn));
		setPositions(loginData, conn);
		setAfAbilities(loginData, conn);

		// 判断直接作业人员可代工间接作业(目前限定报价组)
		if (!(RvsConsts.WORK_COUNT_FLG_INDIRECT + "").equals(loginData.getWork_count_flg())
				&& "00000000011".equals(loginData.getLine_id())) {
			if (loginData.getAfAbilities().size() > 0) {
				loginData.setShift_work(true);
				AcceptFactService service = new AcceptFactService();
				if (service.getUnFinishEntity(loginData.getOperator_id(), conn) != null) {
					loginData.setWork_count_flg("" + RvsConsts.WORK_COUNT_FLG_INDIRECT);
				}
			}
		}

		// 判断有没有进行中作业
		// 取得当前作业中作业信息
		if (workingPf != null) {
			// 有的话切到进行中作业
			String now_position = workingPf.getPosition_id();

			// 平行分线
			if (workingPf.getSection_id() != null && "00000000001".equals(workingPf.getSection_id())) {
				Set<String> dividePositions = PositionService.getDividePositions(conn);
				if (dividePositions.contains(now_position)) {
					MaterialProcessService mservice = new MaterialProcessService();
					MaterialProcessEntity mEntity = mservice.loadMaterialProcessOfLine(workingPf.getMaterial_id(), workingPf.getLine_id(), conn);
					if (mEntity == null) {
						loginData.setPx("0");
					} else {
						loginData.setPx("" + (mEntity.getPx() + 1));
					}
				} else {
					loginData.setPx("0");
				}
			}

			loginData.setPosition_id(now_position);
			loginData.setPosition_name(workingPf.getPosition_name());
			loginData.setProcess_code(workingPf.getProcess_code());
			loginData.setLine_id(workingPf.getLine_id());
			loginData.setLine_name(workingPf.getLine_name());
			if (workingPf.getSection_id() != null && !"00000000000".equals(workingPf.getSection_id())
					&& !workingPf.getSection_id().equals(loginData.getSection_id())
					&& !workingPf.getLine_id().equals("00000000015")) {
				loginData.setSection_id(workingPf.getSection_id());
				loginData.setSection_name(workingPf.getSection_name());
				List<SectionEntity> mySections = loginData.getSections();
				if (mySections.size() >= 1) {
					boolean hit = false;
					for (SectionEntity mySection : mySections) {
						if (mySection.getSection_id().equals(loginData.getSection_id())) {
							hit = true;
							break;
						}
					}
					if (!hit) {
						SectionEntity workingSection = new SectionEntity();
						workingSection.setSection_id(workingPf.getSection_id());
						workingSection.setName(workingPf.getSection_name());
						mySections.add(workingSection);
					}
				} else if (mySections.size() == 0) {
					SectionEntity workingSection = new SectionEntity();
					workingSection.setSection_id(workingPf.getSection_id());
					workingSection.setName(workingPf.getSection_name());
					mySections.add(workingSection);
				}
			}
		} else if (loginData.getLine_id() != null && loginData.getLine_id().equals("00000000013")) {
			SoloSnoutService service = new SoloSnoutService();
			SoloProductionFeatureEntity sworkingPf = service.checkWorkingPfServiceRepair(loginData.getOperator_id(), null, conn);
			if (sworkingPf != null) {
				// 有的话切到进行中作业
				String now_position = sworkingPf.getPosition_id();

				// 平行分线
				if (sworkingPf.getSection_id() != null && "00000000001".equals(sworkingPf.getSection_id())) {
					Set<String> dividePositions = PositionService.getDividePositions(conn);
					if (dividePositions.contains(now_position)) {
						loginData.setPx("2"); // 固定B线
					} else {
						loginData.setPx("0");
					}
				}

				loginData.setPosition_id(now_position);
				PositionService pService = new PositionService();
				PositionEntity pEntity = pService.getPositionEntityByKey(now_position, conn);
				loginData.setPosition_name(pEntity.getName());
				loginData.setProcess_code(pEntity.getProcess_code());
				loginData.setLine_id(pEntity.getLine_id());
				loginData.setLine_name(pEntity.getLine_name());

				if (sworkingPf.getSection_id() != null && "00000000001".equals(sworkingPf.getSection_id())) {
					loginData.setSection_id(sworkingPf.getSection_id());
					SectionMapper sdao = conn.getMapper(SectionMapper.class);
					SectionEntity sbeam = sdao.getSectionByID(sworkingPf.getSection_id());
					loginData.setSection_name(sbeam.getName());
				}
			}
		}

		// 书签
		ProcedureManualService pmService = new ProcedureManualService();
		loginData.setBooks(pmService.getBooks(loginData.getOperator_id(), conn));
	}

	/**
	 * 做成登录的Session项目
	 *
	 * @param operator
	 * @param conn
	 * @param company
	 * @param errorsDto
	 * @return 是否多角色
	 */
	public Map<String, String> makeSession(OperatorForm operator, HttpSession session, List<MsgInfo> errors, SqlSession conn) {

		// 表单复制到数据对象
		OperatorEntity conditionBean = new OperatorEntity();
		// 按工号查询
		conditionBean.setJob_no(operator.getJob_no());

		OperatorMapper dao = conn.getMapper(OperatorMapper.class);

		// 从数据库中查询记录
		if ("000".equals(conditionBean.getJob_no()) || "superadmin".equalsIgnoreCase(conditionBean.getJob_no()) || "SA000000".equalsIgnoreCase(conditionBean.getJob_no())) {
			conditionBean.setJob_no("SA000000");
		} else {
			List<OperatorNamedEntity> lResultBean = dao.searchOperator(conditionBean);

			// 用户不存在
			if (lResultBean.isEmpty()) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("job_no");
				error.setErrcode("login.invalidUser");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("login.invalidUser"));
				errors.add(error);
				return null;
			}
		}

		// 按工号密码查询
		String password = operator.getPwd();
		password = CryptTool.encrypttoStr(password);
		password = CryptTool.encrypttoStr(password + conditionBean.getJob_no().toUpperCase());
		conditionBean.setPwd(password);
		LoginData loginData = dao.searchLoginOperator(conditionBean);

		// 密码不匹配
		if (loginData == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("pwd");
			error.setErrcode("login.invalidPassword");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("login.invalidPassword"));
			errors.add(error);
			return null;
		}

		if (errors.isEmpty()) {
			OperatorService oservice = new OperatorService();
			Map<String, String> uRoles = oservice.getUserRoles(loginData.getOperator_id(), conn);
			if (uRoles != null && uRoles.size() > 0) {
				// 判断是否有进行中工作，如果有则按工位直接决定角色
				ProductionFeatureEntity workingPf = getWoringPf(loginData, conn);
				if (workingPf != null) {
					PositionService.getPositionUnitizeds(conn); // 确认下取过动物内镜工位映射

					String now_position_id = workingPf.getPosition_id();

					if (RvsConsts.POSITION_ACCEPTANCE.equals(now_position_id)) { // 受理
						loginData.setWorking_role_id(RvsConsts.ROLE_ACCEPTOR);
					} else if (RvsConsts.POSITION_QUOTATION_N.equals(now_position_id)
								|| RvsConsts.POSITION_QUOTATION_D.equals(now_position_id)
								|| RvsConsts.POSITION_QUOTATION_P_181.equals(now_position_id)
								|| RvsConsts.POSITION_ANML_QUOTAION.equals(now_position_id) // IISE检查
								|| "00000000101".equals(now_position_id)) { // 报价
						loginData.setWorking_role_id(RvsConsts.ROLE_QUOTATOR);
					} else if (RvsConsts.POSITION_QA.equals(now_position_id)
							|| RvsConsts.POSITION_QA_LIGHT.equals(now_position_id)
							|| RvsConsts.POSITION_QA_P_613.equals(now_position_id)
							|| RvsConsts.POSITION_QA_P_614.equals(now_position_id)
							|| RvsConsts.POSITION_ANML_QA.equals(now_position_id)
							|| RvsConsts.POSITION_ANML_QA_UDI.equals(now_position_id)
							) { // 出检
						loginData.setWorking_role_id(RvsConsts.ROLE_QAER);
					} else if (RvsConsts.POSITION_SHIPPING.equals(now_position_id)
							|| RvsConsts.POSITION_ANML_SHPPING.equals(now_position_id)) { // 出货
						loginData.setWorking_role_id(RvsConsts.ROLE_SHIPPPER);
					} else {
						loginData.setWorking_role_id(RvsConsts.ROLE_OPERATOR);
					}

					setDetail(loginData, conn, workingPf);

					// 用户信息保存在会话中
					session.setAttribute(RvsConsts.SESSION_USER, loginData);
				} else {
					uRoles.put("main", loginData.getRole_name());
					// 多角色
					// 用户信息(暂定)保存在会话中
					session.setAttribute("K" + RvsConsts.SESSION_USER, loginData);
					return uRoles;
				}
			} else {
				// 单角色
				ProductionFeatureEntity workingPf = getWoringPf(loginData, conn);
				setDetail(loginData, conn, workingPf);

				// 用户信息保存在会话中
				session.setAttribute(RvsConsts.SESSION_USER, loginData);
			}

			DeviceJigLoanService djlService = new DeviceJigLoanService();
			djlService.checkLoaningNow(loginData.getOperator_id(), session, conn);
		}
		return null;
	}

	private List<Integer> getPrivacies(String role_id, SqlSession conn) {
		RoleService rService = new RoleService();
		return rService.getUserPrivacies(role_id, conn);
	}

	/**
	 * 设定可选工位/工程/课室
	 * @param loginData
	 * @param conn
	 */
	private void setPositions(LoginData loginData, SqlSession conn) {

		OperatorService oService = new OperatorService();
		List<PositionEntity> positionsList = new ArrayList<PositionEntity>();

		if (loginData.getPosition_id() != null) {

			PositionEntity mainPosition = new PositionEntity();
			// getPositionByID TODO
			mainPosition.setPosition_id(loginData.getPosition_id());
			mainPosition.setName(loginData.getPosition_name());
			mainPosition.setProcess_code(loginData.getProcess_code());
			mainPosition.setLine_id(loginData.getLine_id());
			mainPosition.setLine_name(loginData.getLine_name());
			// 分线判断
			if (PositionService.getDividePositions(conn).contains(loginData.getPosition_id())) {
				mainPosition.setLight_division_flg(1);
			} else {
				mainPosition.setLight_division_flg(0);
			}

			positionsList.add(mainPosition);
		}
		// positionsList.addAll();
		for (PositionEntity p : oService.getUserPositions(loginData.getOperator_id(), conn)) {
			if (!p.getPosition_id().equals(loginData.getPosition_id())) {
				positionsList.add(p);
			} else {
				positionsList.get(0).setChief(p.getChief());
				positionsList.get(0).setLine_id(p.getLine_id());
				positionsList.get(0).setLine_name(p.getLine_name());
			}
		}

		loginData.setPositions(positionsList);

		List<LineEntity> linesList = new ArrayList<LineEntity>();

		Set<String> line_ids = new HashSet<String>();
		for (PositionEntity position : positionsList) {
			// PositionEntity position =
			String line_id = position.getLine_id();
			if (!line_ids.contains(line_id) && !"00000000000".equals(line_id)) {
				line_ids.add(line_id);
				LineEntity line = new LineEntity();
				line.setLine_id(line_id);
				line.setName(position.getLine_name());
				linesList.add(line);
			}
		}
		loginData.setLines(linesList);

		List<SectionEntity> sectionList = oService.getSectionsByOperator(loginData, conn);

		loginData.setSections(sectionList);

		return;
	}

	private void setAfAbilities(LoginData loginData, SqlSession conn) {
		OperatorService oService = new OperatorService();
		loginData.setAfAbilities(oService.getUserAfAbilities(loginData.getOperator_id(), conn));
	}

	public void pdaLogin(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		_logger.fine("LoginAction.pdaLogin start");

		OperatorForm operator = (OperatorForm)form;
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(operator, BeanUtil.CHECK_TYPE_ALL);
		v.delete("role_id", "name");
		v.add("pwd", v.required("密码"));

		List<MsgInfo> errors = v.validate();

		boolean hasPrivacy = false;
		if (errors.isEmpty()) {
			req.setAttribute("isFact", false);
			req.setAttribute("isRecept", false);
			// 建立会话用户信息
			List<Integer> privacies = pdaMakeSession(operator, req.getSession(), errors, conn);
			if (privacies != null) {
				if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
					req.setAttribute("isFact", true);
					hasPrivacy = true;
				}
				if (privacies.contains(RvsConsts.PRIVACY_RECEPT_FACT)) {
					req.setAttribute("isRecept", true);
					hasPrivacy = true;
				}
			}
		}

		if (errors.isEmpty() && !hasPrivacy) {
			MsgInfo info = new MsgInfo();
			info.setComponentid("job_no");
			info.setErrcode("privacy.noPrivacy");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("privacy.noPrivacy"));
			errors.add(info);
		}
		if (!errors.isEmpty()) {
			req.setAttribute("errors", getStrMsgInfo(errors));
			// 迁移到页面
			actionForward = mapping.findForward(FW_PDA_INIT);
		} else {
			// 迁移到页面
			actionForward = mapping.findForward(FW_PDA_MENU);
		}

		_logger.fine("LoginAction.pdaLogin end");
	}

	private String getStrMsgInfo(List<MsgInfo> errors) {
		StringBuffer msgInfo = new StringBuffer();
		for (int i = 0; i < errors.size(); i++) {
			MsgInfo error = errors.get(i);
			if (!CommonStringUtil.isEmpty(error.getErrmsg())) {
				if (i >= 1) {
					msgInfo.append("\\n");
				}
				msgInfo.append(error.getErrmsg());
			}
		}
		return msgInfo.toString();
	}

	/**
	 * 做成登录的Session项目
	 *
	 * @param operator
	 * @param conn
	 * @param company
	 * @param errorsDto
	 * @return 权限集
	 */
	public List<Integer> pdaMakeSession(OperatorForm operator, HttpSession session, List<MsgInfo> errors, SqlSession conn) {

		// 表单复制到数据对象
		OperatorEntity conditionBean = new OperatorEntity();
		// 按工号查询
		conditionBean.setJob_no(operator.getJob_no());

		OperatorMapper dao = conn.getMapper(OperatorMapper.class);

		// 从数据库中查询记录
		if ("000".equals(conditionBean.getJob_no()) || "superadmin".equalsIgnoreCase(conditionBean.getJob_no())
				|| "SA000000".equalsIgnoreCase(conditionBean.getJob_no())) {
			conditionBean.setJob_no("SA000000");
		} else {
			List<OperatorNamedEntity> lResultBean = dao.searchOperator(conditionBean);

			// 用户不存在
			if (lResultBean.isEmpty()) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("job_no");
				error.setErrcode("login.invalidUser");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("login.invalidUser"));
				errors.add(error);
				return null;
			}
		}

		// 按工号密码查询
		String password = operator.getPwd();
		password = CryptTool.encrypttoStr(password);
		password = CryptTool.encrypttoStr(password + conditionBean.getJob_no().toUpperCase());
		conditionBean.setPwd(password);
		LoginData loginData = dao.searchLoginOperator(conditionBean);

		// 密码不匹配
		if (loginData == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("pwd");
			error.setErrcode("login.invalidPassword");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("login.invalidPassword"));
			errors.add(error);
			return null;
		}

		if (errors.isEmpty()) {
			List<Integer> privacies = dao.getPrivaciesOfOperator(loginData.getOperator_id());
			// 合并这个人的所有权限
			loginData.setPrivacies(privacies);

			// 取得这个人间接作业权限
			setAfAbilities(loginData, conn);

			// 用户信息保存在会话中
			session.setAttribute(RvsConsts.SESSION_USER, loginData);
			session.setAttribute("user_name", loginData.getName());
			return loginData.getPrivacies();
		}
		return null;
	}
}

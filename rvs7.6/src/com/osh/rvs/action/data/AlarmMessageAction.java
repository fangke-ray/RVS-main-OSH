package com.osh.rvs.action.data;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.osh.rvs.bean.data.AlarmMesssageSendationEntity;
import com.osh.rvs.bean.inline.ForSolutionAreaEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.AlarmMesssageForm;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.service.AlarmMesssageService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.MaterialProcessAssignService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.PositionPlanTimeService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.inline.ForSolutionAreaService;
import com.osh.rvs.service.inline.LineLeaderService;
import com.osh.rvs.service.inline.ScheduleService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 警报一览处理
 * @author Gong
 *
 */
public class AlarmMessageAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	private AlarmMesssageService service = new AlarmMesssageService();

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

		log.info("AlarmMessageAction.init start");

		req.setAttribute("lvlOptions", CodeListUtils.getSelectOptions("alarm_level", null, ""));
		req.setAttribute("rOptions", CodeListUtils.getSelectOptions("alarm_reason", null, ""));

		SectionService sectionService = new SectionService();
		// 课室信息取得
		String sOptions = sectionService.getOptions(conn, "");
		// 课室信息设定
		req.setAttribute("sOptions", sOptions);
		
		LineService lineService = new LineService();
		// 工程信息取得
		String lOptions = lineService.getOptions(conn);
		// 工程信息设定
		req.setAttribute("lOptions", lOptions);
		
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		OperatorService operatorService = new OperatorService();
		// 处理者信息取得
		String oReferChooser = operatorService.getResolverReferChooser(conn);
		// 处理者信息设定
		req.setAttribute("oReferChooser", oReferChooser);

		Calendar today  =Calendar.getInstance();
		int hour = today.get(Calendar.HOUR_OF_DAY); 
		
		if(hour < 10 ){
			today.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		req.setAttribute("today", DateUtil.toString(today.getTime(), DateUtil.DATE_PATTERN));
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("AlarmMessageAction.init end");
	}

	/**
	 * 警报查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("AlarmMessageAction.search start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<AlarmMesssageForm> amResultForm = service.search(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("list", amResultForm);
			
			listResponse.put("lOptions", CodeListUtils.getGridOptions("alarm_level"));

			listResponse.put("rOptions", CodeListUtils.getGridOptions("alarm_reason"));
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("AlarmMessageAction.search end");
	}

	/**
	 * 警报查询详细处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("AlarmMessageAction.detail start");

		String alarm_messsage_id = req.getParameter("alarm_messsage_id");

		if (CommonStringUtil.isEmpty(alarm_messsage_id)) {
			actionForward = mapping.findForward("error");
		} else {

			// 取得用户信息
			HttpSession session = req.getSession();
			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

			String operator_id = user.getOperator_id();

			AlarmMesssageMapper dao = conn.getMapper(AlarmMesssageMapper.class);

			AlarmMesssageSendationEntity bean = dao.getBreakAlarmMessageBySendation(alarm_messsage_id, operator_id);
			
			req.setAttribute("alarm_messsage_id", alarm_messsage_id);

			if (bean != null && bean.getResolve_time() == null) {

				// 已被他人处理
				if (!dao.isFixed(alarm_messsage_id)) {
					req.setAttribute("interfaced", false);

					List<Integer> privacies = user.getPrivacies();
					if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
						req.setAttribute("level", "2");
					} else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
						req.setAttribute("level", "1");
					} else if (privacies.contains(RvsConsts.PRIVACY_LINE) || privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
						req.setAttribute("level", "0");
					}
				}

				// 本人没有处理的迁移到处理页面
				actionForward = mapping.findForward("nogoodedit");
			} else {
				List<Integer> privacies = user.getPrivacies();
				if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {

					// 已被他人处理
					//if (!dao.isFixed(alarm_messsage_id)) {
						req.setAttribute("interfaced", false);
						req.setAttribute("level", "1");
					//}
					// 本人已经处理的迁移到详细页面
					actionForward = mapping.findForward("nogoodedit");
				} else {
					// 本人已经处理的迁移到详细页面
					actionForward = mapping.findForward("detail");
				}
			}
		}

		log.info("AlarmMessageAction.detail end");
	}

	/**
	 * 警报详细初始画面
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void detailInit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("AlarmMessageAction.detailInit start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		String alarm_messsage_id = req.getParameter("alarm_messsage_id");

		if (CommonStringUtil.isEmpty(alarm_messsage_id)) {
			MsgInfo e = new MsgInfo();
			e.setComponentid("alarm_messsage_id");
			e.setErrmsg("没有指定警告信息ID");
			infoes.add(e);
		} else {

			AlarmMesssageForm resform = service.getWarning(alarm_messsage_id, conn);
			callbackResponse.put("alarm", resform);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("AlarmMessageAction.detailInit end");
	}

	/**
	 * 取消中断
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doreleasebeak(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AlarmMessageAction.doreleasebeak start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<String> triggerList = new ArrayList<String>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 中断再开处理
		service.closebreak(req, conn);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		ForSolutionAreaService fsaService = new ForSolutionAreaService();
		String alarm_messsage_id = req.getParameter("alarm_messsage_id");
		ForSolutionAreaEntity fsaBean = fsaService.checkBlockByAlarm(alarm_messsage_id, conn);
		if (fsaBean != null) {
			String append_parts = req.getParameter("append_parts");
			if ("1".equals(append_parts)) {
				fsaService.updateToAppend(fsaBean, conn);
			} else {
				fsaService.solveBreak(fsaBean, req.getParameter("material_id"), user.getOperator_id(), triggerList, conn);
			}
		}

		if (triggerList.size() > 0 && errors.size() == 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("AlarmMessageAction.doreleasebeak end");
	}

	/**
	 * 上报中断
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void dohold(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AlarmMessageAction.dohold start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		List<Integer> privacies = user.getPrivacies();

		if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			// 中断上报处理
			ScheduleService service = new ScheduleService();
			service.hold(req, user, conn);
		} else if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
			// 中断上报处理
			LineLeaderService service = new LineLeaderService();
			service.hold(req, user, conn);

			ForSolutionAreaService fsaService = new ForSolutionAreaService();
			String alarm_messsage_id = req.getParameter("alarm_messsage_id");
			ForSolutionAreaEntity fsaBean = fsaService.checkBlockByAlarm(alarm_messsage_id, conn);
			if (fsaBean != null) {
				fsaService.updateToPushed(fsaBean, conn);
			}

		} else {
			log.error("Invalid PRIVACY to do push!!");
		}


		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("AlarmMessageAction.dohold end");
	}

	/**
	 * 中断了解
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void docomment(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AlarmMessageAction.docomment start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		service.comment(req, user, conn);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("AlarmMessageAction.docomment end");
	}

	/**
	 * 工序再指派
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void dorework(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AlarmMessageAction.dorework start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<String> triggerList = new ArrayList<String>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 检查发生错误时报告错误信息
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id =req.getParameter("material_id");
		String testMps = req.getParameter("material_process_assign.position_id[0]");

		if (testMps != null) {
			// 收集更新流程信息(true的时候list.size==0时不处理)
			MaterialProcessAssignService mpaService = new MaterialProcessAssignService();
			mpaService.updateProcessAssign(material_id, req, conn, true);
		}

		// 中断再开处理
		service.rework(req, triggerList, conn, errors);

		if (errors.size() == 0) {

			// 返工时，倒计时计划作为“异常中断”而设定为未达成。
			PositionPlanTimeService pptService = new PositionPlanTimeService();
			pptService.autosetUnreach(req, conn);

			// 处理PA记录
			ForSolutionAreaService fsaService = new ForSolutionAreaService();
			String alarm_messsage_id = req.getParameter("alarm_messsage_id");
			ForSolutionAreaEntity fsaBean = fsaService.checkBlockByAlarm(alarm_messsage_id, conn);
			if (fsaBean != null) {
				String append_parts = req.getParameter("append_parts");
				if ("1".equals(append_parts)) {
					fsaService.updateToAppend(fsaBean, conn); // PA中断-> 等待追加零件
				} else {
					fsaService.solveBreak(fsaBean, material_id, user.getOperator_id(), triggerList, conn); // PA中断解决
				}
			}

			if (triggerList.size() > 0 && errors.size() == 0) {
				conn.commit();
				RvsUtils.sendTrigger(triggerList);
			}
		} else {
			conn.rollback();
		}

		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("AlarmMessageAction.dorework end");
	}
}
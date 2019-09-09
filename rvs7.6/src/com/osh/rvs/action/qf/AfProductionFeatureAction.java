/**
 * 系统名：OSH-RVS<br>
 * 模块名：受理现品管理<br>
 * 机能名：间接作业记录<br>
 * @author 龚镭敏
 * @version 8.01
 */
package com.osh.rvs.action.qf;

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
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.qf.AfProductionFeatureForm;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.PauseFeatureService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;


public class AfProductionFeatureAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 受理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void getByOperator(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("AfProductionFeatureAction.getByOperator start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得当前进行中作业
		AcceptFactService service = new AcceptFactService();
		AfProductionFeatureForm processForm = service.getProcessOfOperator(user, conn);

		if (processForm != null) {
			// 如果是直接作业，取得标准工时
			if ("1".equals(processForm.getIs_working())) {
				Integer standard_minutes = service.getStandardMinutes(processForm, conn);
				if (standard_minutes != null) {
					processForm.setStandard_minutes("" + standard_minutes);
				}
			}

			callbackResponse.put("processForm", processForm);
		}

		String init = req.getParameter("init");

		if (init != null) {
			// 可做工位
			List<PositionEntity> afAbilities = user.getAfAbilities();
			callbackResponse.put("afAbilities", afAbilities);

			callbackResponse.put("pauseReasonGroup", PauseFeatureService.getPauseReasonIndirectGroupMap());
		}

		boolean isManager = user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)
				|| user.getPrivacies().contains(RvsConsts.PRIVACY_LINE)
				|| user.getPrivacies().contains(RvsConsts.PRIVACY_RECEPT_EDIT);
		callbackResponse.put("isManager", isManager);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("AfProductionFeatureAction.getByOperator end");
	}

	/**
	 * 受理添加记录实行处理
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doSwitch(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AfProductionFeatureAction.doSwitch start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		AcceptFactService service = new AcceptFactService();

		AfProductionFeatureForm processForm = service.switchTo(form, user, conn);

		callbackResponse.put("processForm", processForm);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AfProductionFeatureAction.doSwitch end");
	}

	/**
	 * 作业记录停止（管理员：停止记录/操作人员：下班）
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doEnd(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AfProductionFeatureAction.doEnd start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		AcceptFactService service = new AcceptFactService();

		service.end(form, user, conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AfProductionFeatureAction.doEnd end");
	}

	/**
	 * 取得本人零件订购单编辑记录+SAP零件单修改记录
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getPartialOrderList(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("AfProductionFeatureAction.getPartialOrderList start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		AcceptFactService service = new AcceptFactService();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		callbackResponse.put("today_partial_order_edit_by_self", service.getTodayPartialOrderEditBySelf(user.getOperator_id(), conn));

		callbackResponse.put("today_partial_order_edit_from_sap", service.getTodayPartialOrderEditFromSap(conn));

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AfProductionFeatureAction.getPartialOrderList end");
	}

	/**
	 * 提交维修对象零件订单编辑记录
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doPartialOrder(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AfProductionFeatureAction.doPartialOrder start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		AcceptFactService service = new AcceptFactService();

		boolean edited = service.editPartialOrder(req.getParameter("omr_notifi_no"), user, errors, conn);

		if(edited) {
			conn.commit();

			service.fingerOperatorRefresh(user.getOperator_id());
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AfProductionFeatureAction.doPartialOrder end");
	}

}

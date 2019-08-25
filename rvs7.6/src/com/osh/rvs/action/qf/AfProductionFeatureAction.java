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

		// 可做工位
		List<PositionEntity> afAbilities = user.getAfAbilities();
		callbackResponse.put("afAbilities", afAbilities);

		callbackResponse.put("pauseReasonGroup", PauseFeatureService.getPauseReasonIndirectGroupMap());

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
}

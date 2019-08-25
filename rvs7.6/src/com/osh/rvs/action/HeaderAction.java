/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：维修对象机种系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action;

import java.util.ArrayList;
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
import com.osh.rvs.bean.data.AlarmMesssageEntity;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.service.AlarmMesssageService;
import com.osh.rvs.service.HolidayService;
import com.osh.rvs.service.PostMessageService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.DateUtil;


public class HeaderAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 菜单初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("HeaderAction.init start");

		// 取得页面结构
		String sPart = req.getParameter("part");
		String sSub = req.getParameter("sub");

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		String retNeedMenu = "0";
		String retNeedMessageBox = "0";
		String retPartialLink = "0";

		// 需要隐藏菜单的页面结构
		if ("1".equals(sPart)) {
			retNeedMenu = "1";
		}
		// 需要信息框的权限
		if (!"3".equals(sPart)
				&& (privacies.contains(RvsConsts.PRIVACY_PROCESSING)
					|| privacies.contains(RvsConsts.PRIVACY_LINE)
					|| privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)
					|| privacies.contains(RvsConsts.PRIVACY_ACCEPTANCE)
					|| privacies.contains(RvsConsts.PRIVACY_QUALITY_ASSURANCE)
					|| privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)
					|| privacies.contains(RvsConsts.PRIVACY_RECEPT_FACT)
					|| privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)
					|| privacies.contains(RvsConsts.PRIVACY_POSITION)
					|| privacies.contains(RvsConsts.PRIVACY_DT_MANAGE))) {
			retNeedMessageBox = "1";
		}
		// 需要零件的权限
		if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)
						|| privacies.contains(RvsConsts.PRIVACY_ADMIN) 
						|| privacies.contains(RvsConsts.PRIVACY_PROCESSING) 
						|| privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			retPartialLink = "1";
		} else if ("00000000012".equals(user.getLine_id()) 
				|| "00000000013".equals(user.getLine_id())
				|| "00000000014".equals(user.getLine_id())) {
			if (privacies.contains(RvsConsts.PRIVACY_LINE) || privacies.contains(RvsConsts.PRIVACY_POSITION)) {
				List<PositionEntity> positions = user.getPositions();
				for (PositionEntity position : positions) {

					String position_id = position.getPosition_id();
					if ("00000000021".equals(position_id) || "00000000027".equals(position_id)
							|| "00000000032".equals(position_id)) {
						retPartialLink = "1";
						break;
					}

				}
			}
		}
// TODO
		retPartialLink = "1";

		req.setAttribute("needMenu", retNeedMenu);
		req.setAttribute("needMessageBox", retNeedMessageBox);
		req.setAttribute("retPartialLink", retPartialLink);
		req.setAttribute("retSub", sSub);

		String retPosition = "";
		if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
			retPosition += "您在" + user.getSection_name() + CommonStringUtil.nullToAlter(user.getLine_name(), "") + "。";
		}
		else if (privacies.contains(RvsConsts.PRIVACY_POSITION)) {
			retPosition += "您在" + user.getSection_name() + " " 
					+ CommonStringUtil.nullToAlter(user.getProcess_code(), "") + " " 
					+ CommonStringUtil.nullToAlter(user.getPosition_name(), "") + "工位。";
		}

		req.setAttribute("userPosition", retPosition);

		// 修理进度信息
		String role_id = user.getRole_id();
		if (RvsConsts.ROLE_LINELEADER.equals(role_id)) {
			req.setAttribute("message_type", "le");
		} else
		if (RvsConsts.ROLE_OPERATOR.equals(role_id)) {
			req.setAttribute("message_type", "op");
		}

		// 间接作业人员
		if ("6".equals(user.getWork_count_flg())) {
			req.setAttribute("indirect_worker", "id");
		} else {
			req.setAttribute("indirect_worker", "");
		}
		
		// 假期列表
		req.setAttribute("today_holiday", HolidayService.checkTodayHoliday(conn));
		req.setAttribute("header_holidays", HolidayService.getHolidaysOnMonthAsJson());

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("HeaderAction.init end");
	}

	/**
	 * 切换到零件功能界面
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void pinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("HeaderAction.pinit start");

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		if (privacies.contains(RvsConsts.PRIVACY_ADMIN)) {
			// 迁移到零件管理页面
			actionForward = mapping.findForward("partialManage");
		} else if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			// 迁移到零件发放页面
			actionForward = mapping.findForward("partialAssign");
		} else if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)) {
			// 迁移到零件订购页面
			actionForward = mapping.findForward("partialOrder");
		} else if (privacies.contains(RvsConsts.PRIVACY_LINE) || privacies.contains(RvsConsts.PRIVACY_POSITION)) {
			// 迁移到零件签收页面
			actionForward = mapping.findForward("partialRecept");
		} else {
			// 迁移到零件签收订购管理页面
			actionForward = mapping.findForward("partialManage");
		}

		log.info("HeaderAction.pinit end");
	}

	/**
	 * 切换到点检功能界面
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void iinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("HeaderAction.iinit start");

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		if (!privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY)) {
			// 迁移到设备管理页面
			actionForward = mapping.findForward("infect");
		} else {
			// 迁移到设备分布页面
			actionForward = mapping.findForward("infectM");
		}

		log.info("HeaderAction.iinit end");
	}

	public void getMessage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("HeaderAction.getMessage start");
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		getMessages(user, listResponse, conn);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("HeaderAction.getMessage end");
	}

	private void getMessages(LoginData user, Map<String, Object> callbackResponse, SqlSession conn) {
		String pattern = "MM月dd日 HH：mm";

		// 警报信息取得
		AlarmMesssageService amService = new AlarmMesssageService();

		int aMessageCounts = amService.getMessageCountsByOperator(conn, user.getOperator_id());

		StringBuffer messagesHtml = new StringBuffer("");

		if (aMessageCounts > 0) {
			List<AlarmMesssageEntity> alarmsEntities = amService.getMessageByOperator(conn, user.getOperator_id());
			for (AlarmMesssageEntity alarmsEntity : alarmsEntities) {
				//AlarmMesssageForm amForm = new AlarmMesssageForm();
				//BeanUtil.copyToForm(alarmsEntity, amForm, CopyOptions.COPYOPTIONS_NOEMPTY);
				messagesHtml.append("<div id=\"alarm_" + alarmsEntity.getAlarm_messsage_id() + "\">");
				messagesHtml.append("<div style=\"padding-left:16px;font-size:14px;\" class=\"m_title ui-state-active\"><span class=\"areatitle\">"); // ui-state-default
				messagesHtml.append(DateUtil.toString(alarmsEntity.getOccur_time(), pattern));
				messagesHtml.append("</span><a class=\"HeaderButton areacloser\" href=\"javascript:void(0)\" role=\"link\" refId=\"" + alarmsEntity.getAlarm_messsage_id()
						+ "\"><span class=\"ui-icon ui-icon-circle-close\"></span></a>");
				messagesHtml.append("<div class=\"clear\"></div></div>");
	
				messagesHtml.append("<div style=\"padding-left:10px;\"");
				if (alarmsEntity.getLevel() > 1) {
					messagesHtml.append("class=\"m_content ui-state-error\">");
				} else {
					messagesHtml.append("class=\"m_content ui-state-highlight\">");
				}
				if (alarmsEntity.getLine_name() != null) {
					messagesHtml.append("工程：<a href=\"javascript:void(0);\">" + alarmsEntity.getLine_name() + "</a><br>");
				}
				if (alarmsEntity.getProcess_code() != null) {
					messagesHtml.append("工位：<a href=\"javascript:popPositionDetail();\">" + alarmsEntity.getProcess_code() + "</a><br>");
				}
				if (alarmsEntity.getSorc_no() != null) {
					messagesHtml.append("维修对象：<a href=\"javascript:popMaterialDetail();\">" + alarmsEntity.getSorc_no() + "</a><br>");
					messagesHtml.append("型号：" + alarmsEntity.getModel_name() + "<br>");
				}
				messagesHtml.append("状况：" + getReasonMessage(alarmsEntity) + "</div>");
				messagesHtml.append("</div>");
			}
		}

		// 推送信息取得
		PostMessageService pmService = new PostMessageService();
		int pMessageCounts = pmService.getMessageCountsByOperator(conn, user.getOperator_id());

		if (pMessageCounts > 0) {
			List<PostMessageEntity> postEntities = pmService.getMessageByOperator(conn, user.getOperator_id());
			for (PostMessageEntity postEntity : postEntities) {
				messagesHtml.append("<div id=\"post_" + postEntity.getPost_message_id() + "\">");
				messagesHtml.append("<div style=\"padding-left:16px;font-size:14px;\" class=\"m_title ui-state-default\"><span class=\"areatitle\">");
				messagesHtml.append(DateUtil.toString(postEntity.getOccur_time(), pattern));
				messagesHtml.append("</span><a class=\"HeaderButton areacloser\" href=\"javascript:void(0)\" role=\"link\" refId=\"p_" + postEntity.getPost_message_id()
						+ "\"><span class=\"ui-icon ui-icon-circle-close\"></span></a>");
				messagesHtml.append("<div class=\"clear\"></div></div>");

				messagesHtml.append("<div style=\"padding-left:10px;\" class=\"m_content ui-widget-content\">");
				messagesHtml.append("信息：" + postEntity.getContent() + "</div>");
				messagesHtml.append("</div>");
			}
		}

		callbackResponse.put("alarm_counts",aMessageCounts + pMessageCounts);
		log.info("HeaderAction.new alarm_counts"+aMessageCounts+" + "+pMessageCounts+" = "+(aMessageCounts+pMessageCounts));

		callbackResponse.put("alarms", messagesHtml.toString());
	}

	private String getReasonMessage(AlarmMesssageEntity alarmsEntity) {
		Integer reason = alarmsEntity.getReason();
		if (RvsConsts.WARNING_REASON_BREAK == reason) {
			return "发生了不良中断。";
		} else if (RvsConsts.WARNING_REASON_QAFORBID.equals(reason)) {
			return "最终检查不合格。";
		} else if (RvsConsts.WARNING_REASON_WAITING_OVERFLOW.equals(reason)) {
			return "工位仕挂超过上限。";
		}
		return "";
	}

	public void detailPost(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("HeaderAction.detailPost start");

		String post_message_id = req.getParameter("post_message_id");

		String messageContent = "";

		PostMessageService service = new PostMessageService();

		messageContent = service.getMessageGroupContent(post_message_id, conn);

		req.setAttribute("messageContent", messageContent);

		// 迁移到页面
		actionForward = mapping.findForward("post_confirm");

		log.info("HeaderAction.detailPost end");
	}

	/**
	 * 阅读推送信息
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doReadPostMessage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{

		log.info("HeaderAction.doReadPostMessage start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		String post_message_id = req.getParameter("post_message_id");
		PostMessageService service = new PostMessageService();
		service.readPostMessage(post_message_id, user.getOperator_id(), conn);

		getMessages(user, callbackResponse, conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("HeaderAction.doReadPostMessage end");
	}

}
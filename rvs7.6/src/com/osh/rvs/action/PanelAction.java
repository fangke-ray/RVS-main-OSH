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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.master.LineEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.RoleEntity;
import com.osh.rvs.bean.master.SectionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.mapper.master.LineMapper;
import com.osh.rvs.mapper.master.RoleMapper;
import com.osh.rvs.mapper.master.SectionMapper;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.RoleService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.inline.SoloSnoutService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.message.ApplicationMessage;


public class PanelAction extends BaseAction {

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

		log.info("PanelAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("PanelAction.init end");
	}

	public void dispatch(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PanelAction.dispatch start");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String roleId = user.getWorking_role_id();
		if (roleId == null) {
			roleId = user.getRole_id();
		}

		// 迁移到页面
		if (RvsConsts.ROLE_ACCEPTOR.equals(roleId)) {
			actionForward = mapping.findForward("acceptance");
		} else if (RvsConsts.ROLE_QUOTATOR.equals(roleId)) {
			actionForward = mapping.findForward("quotation");
		} else if (RvsConsts.ROLE_FACTINLINE.equals(roleId)) {
			actionForward = mapping.findForward("fact");
		} else if (RvsConsts.ROLE_SCHEDULER.equals(roleId)) {
			actionForward = mapping.findForward("schedule");
		} else if (RvsConsts.ROLE_LINELEADER.equals(roleId)) {
			String line_id = user.getLine_id();
			if ("00000000011".equals(line_id)) { // 受理报价
				actionForward = mapping.findForward("blineleader");
			} else {
				actionForward = mapping.findForward("lineleader");
			}
		} else if (RvsConsts.ROLE_OPERATOR.equals(roleId) || RvsConsts.ROLE_SHIPPPER.equals(roleId)) {

			// 根据工位区分位置
			String specialPage = PositionService.getPositionSpecialPage(user.getPosition_id(), conn);
			if (specialPage != null) {
				actionForward = mapping.findForward(specialPage);
			}
			if (actionForward == null) 	{

				actionForward = mapping.findForward("position");

				if (user.getSection_id() != null && "00000000001".equals(user.getSection_id())) {
					String positionId = user.getPosition_id();
					String px = user.getPx();
					Set<String> dividePositions = PositionService.getDividePositions(conn);
					if (dividePositions.contains(positionId)) {
						if (px == null || "0".equals(px)) {
							if (user.getProcess_code() == null || !user.getProcess_code().startsWith("5")) { // TODO
								user.setPx("1");
							} else {
								user.setPx("3");
							}
							session.setAttribute(RvsConsts.SESSION_USER, user);
						}
					} else {
						if (!"4".equals(px)) {
							user.setPx("0");
							session.setAttribute(RvsConsts.SESSION_USER, user);
						}
					}
				}
			}
		} else if (RvsConsts.ROLE_QAER.equals(roleId) || RvsConsts.ROLE_QA_MANAGER.equals(roleId)) {
			actionForward = mapping.findForward("qualityAssurance");
//		} else if (RvsConsts.ROLE_SHIPPPER.equals(roleId)) {
//			actionForward = mapping.findForward("shipping");
		} else if (RvsConsts.ROLE_PARTIAL_MANAGER.equals(roleId)) {
			actionForward = mapping.findForward("partialm");
		} else {
			actionForward = mapping.findForward("success");
		}

		log.info("PanelAction.dispatch end");
	}

	public void changeposition(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("PanelAction.changeposition start");
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// Ajax响应对象
		Map<String, Object> callResponse = new HashMap<String, Object>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String new_section_id = req.getParameter("section_id");
		String new_line_id = req.getParameter("line_id");

		// 判断有没有进行中作业
		// 取得当前作业中作业信息
		String org_position_id = user.getPosition_id();
		String org_px = user.getPx();
		String new_position_id = req.getParameter("position_id");
		String new_px = req.getParameter("px");
		if (new_px == null) new_px = "0";

		if (new_position_id != null && new_position_id.equals(org_position_id)
				&& new_px.equals(org_px)) {

			callResponse.put("position_link", getLink(new_position_id, mapping, conn));

			// 检查发生错误时报告错误信息
			callResponse.put("errors", errors);

			// 返回Json格式响应信息
			returnJsonResponse(res, callResponse);

			log.info("PanelAction.changeposition end");

			return;
		}

		if (new_position_id == null && new_section_id == null && new_line_id == null) {
			callResponse.put("position_link", getLink(org_position_id, mapping, conn));
			// 检查发生错误时报告错误信息
			callResponse.put("errors", errors);

			// 返回Json格式响应信息
			returnJsonResponse(res, callResponse);

			log.info("PanelAction.changeposition end（unchange）");

			return;
		}

		PositionPanelService service = new PositionPanelService();
		ProductionFeatureEntity workingPf = service.getProcessingPf(user, conn);
		if (workingPf != null) {
			Map<String, String> groupSubPositions = PositionService.getGroupSubPositions(conn);
			if (groupSubPositions.containsKey(workingPf.getPosition_id())
					&& groupSubPositions.get(workingPf.getPosition_id()).equals(new_position_id)) {
			} else {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setErrcode("info.linework.workingRemain");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingRemain",
						(workingPf.getSection_name() == null ? "" : workingPf.getSection_name()) + workingPf.getProcess_code() + workingPf.getPosition_name()));
				errors.add(msgInfo);
			}
		} else {
			SoloSnoutService ssService = new SoloSnoutService();
			SoloProductionFeatureEntity sworkingPf = ssService.checkWorkingPfServiceRepair(user.getOperator_id(), null, conn);
			if (sworkingPf != null) {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setErrcode("info.linework.workingRemain");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingRemain",
						(sworkingPf.getProcess_code() + "工位独立流程")));
				errors.add(msgInfo);
			}
		}

		// 会话更新
		if (errors.size() == 0) {
			String org_role_id = user.getRole_id();
			new_section_id = req.getParameter("section_id");
			new_line_id = req.getParameter("line_id");

			if (new_position_id != null || new_line_id != null) {

				int isInline = 0;
				isInline = checkInline(new_position_id, new_line_id, user.getPositions());

				if (isInline == 1) { // 受理
					if (RvsConsts.ROLE_QUOTATOR.equals(org_role_id)
						|| RvsConsts.ROLE_ACCEPTOR.equals(org_role_id)
						|| RvsConsts.ROLE_SHIPPPER.equals(org_role_id)
						|| RvsConsts.ROLE_QAER.equals(org_role_id)) {
						user.setRole_id(RvsConsts.ROLE_OPERATOR);
						refreshRole(user, conn);
						callResponse.put("refresh", "1");
					}
				} else if (isInline == 0) {
					if (RvsConsts.ROLE_OPERATOR.equals(org_role_id)
						|| RvsConsts.ROLE_QAER.equals(org_role_id)) {
						user.setRole_id(RvsConsts.ROLE_QUOTATOR);
						refreshRole(user, conn);
						callResponse.put("refresh", "1");
					}				
				} else if (isInline == 2) {
					if (RvsConsts.ROLE_QUOTATOR.equals(org_role_id)
						|| RvsConsts.ROLE_ACCEPTOR.equals(org_role_id)
						|| RvsConsts.ROLE_SHIPPPER.equals(org_role_id)
						|| RvsConsts.ROLE_OPERATOR.equals(org_role_id)) {
						user.setRole_id(RvsConsts.ROLE_QAER);
						refreshRole(user, conn);
						callResponse.put("refresh", "1");
					}				
				}

				if (new_position_id != null)
					for (PositionEntity pe : user.getPositions()) {
						String pe_id = pe.getPosition_id();
						if (new_position_id.equals(pe_id)) {
							user.setPosition_id(pe_id);
							user.setProcess_code(pe.getProcess_code());
							user.setPosition_name(pe.getName());
							user.setLine_id(pe.getLine_id());
							user.setLine_name(pe.getLine_name());
							break;
						}
					}

				callResponse.put("position_link", getLink(new_position_id, mapping, conn));
			}

			if (new_section_id != null) {
				user.setSection_id(new_section_id);
				SectionMapper sdao = conn.getMapper(SectionMapper.class);
				SectionEntity sbeam = sdao.getSectionByID(new_section_id);
				user.setSection_name(sbeam.getName());
			}

			if (new_line_id != null) {
				user.setLine_id(new_line_id);
				LineMapper ldao = conn.getMapper(LineMapper.class);
				LineEntity lbeam = ldao.getLineByID(new_line_id);
				user.setLine_name(lbeam.getName());
			}

			if (!"4".equals(org_px))
				user.setPx(new_px); // 超级员工不分线

			// 更新会话用户信息
			session.setAttribute(RvsConsts.SESSION_USER, user);

			// 维修流程提示清除
			session.removeAttribute(RvsConsts.JUST_FINISHED);
			session.removeAttribute(RvsConsts.JUST_WORKING);
		}

		// 检查发生错误时报告错误信息
		callResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callResponse);

		log.info("PanelAction.changeposition end");
	}

	private Integer checkInline(String new_position_id, String new_line_id, List<PositionEntity> positions) {
		if (new_position_id !=null) 
			for (PositionEntity position : positions) {
				if (new_position_id.equals(position.getPosition_id())) {
					String line_id = position.getLine_id();
					if (line_id == null) {
						return 0;
					} else if ("00000000012".equals(line_id) || "00000000013".equals(line_id) || "00000000014".equals(line_id)){
						return 1;
					} else if ("00000000015".equals(line_id)){
						return 2;
					}
					return 0;
				}
			}

		if (new_line_id !=null) {
			if ("00000000012".equals(new_line_id) || "00000000013".equals(new_line_id) || "00000000014".equals(new_line_id)){
				return 1;
			} else if ("00000000015".equals(new_line_id)){
				return 2;
			}
			return 0;
		}

		return 0;
	}

	private String getLink(String new_position_id, ActionMapping mapping, SqlSession conn) {

		String specialPage = PositionService.getPositionSpecialPage(new_position_id, conn); // TODO

		String forwardStr = null;
		if (specialPage != null) {
			ActionForward forward = mapping.findForward(specialPage);
			if (forward != null)
				forwardStr = forward.getPath();
		}

		if (forwardStr != null) {
			return forwardStr.substring(1);
		}

//		if ("00000000009".equals(new_position_id)) { // TODO
//			return "acceptance.do";
//		} else if ("00000000013".equals(new_position_id) || "00000000014".equals(new_position_id) 
//				|| RvsConsts.POSITION_QUOTATION_P_181.equals(new_position_id)
//				|| "00000000101".equals(new_position_id)) {
//			return "quotation.do";
//		} else if ("00000000016".equals(new_position_id) || "00000000032".equals(new_position_id)) {
//			return "position_panel.do";
//		} else if ("00000000046".equals(new_position_id) || "00000000052".equals(new_position_id)
//				|| RvsConsts.POSITION_QA_P_613.equals(new_position_id)
//				|| RvsConsts.POSITION_QA_P_614.equals(new_position_id)) {
//			return "qualityAssurance.do";
//		} else if (RvsConsts.POSITION_QA_601.equals(new_position_id)) {
//			return "service_repair_referee.do";
//		}
		// 分流
		int rand = new Double(1.0d + Math.random() * 4).intValue();
		return "position_panel" + rand + ".do";
	}

	private void refreshRole(LoginData user, SqlSession conn) {

		RoleService rService = new RoleService();

		String role_id = user.getRole_id();

		RoleMapper dao = conn.getMapper(RoleMapper.class);
		RoleEntity resultBean = dao.getRoleByID(role_id);
		String role_name = resultBean.getName();

		user.setRole_name(role_name);
		user.setPrivacies(rService.getUserPrivacies(role_id, conn));
	}
}

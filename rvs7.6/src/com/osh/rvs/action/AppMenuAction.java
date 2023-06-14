/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：维修对象机种系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.service.PositionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.common.util.CommonStringUtil;

public class AppMenuAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());
	private static final String LINE_ACCEPT_QUOTATE = "00000000011"; // 受理报价工程
	private static final String LINE_DECOM = "00000000012"; // 分解工程
	private static final String LINE_NS = "00000000013"; // NS工程
	private static final String LINE_COM = "00000000014"; // 总组工程

	private static final String LINE_QA = "00000000015"; // 品保工程
	private static final String LINE_SHIP = "00000000017"; // 出货工程

	private static final String LINE_FACTM = "00000000020"; // 物料组

	/**
	 * 菜单初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("AppMenuAction.init start" + req.getParameter("ex"));

		if (req.getParameter("ex") != null) {
			// 迁移到页面
			actionForward = mapping.findForward(FW_INIT + "-ex");
		} else {
			// 迁移到页面
			actionForward = mapping.findForward(FW_INIT);
		}

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();
		// String now_position_id = user.getPosition_id();
		@SuppressWarnings("unused")
		String process_code = user.getProcess_code();
		List<PositionEntity> userPositions = user.getPositions();
		String section_id = user.getSection_id();
		String px = user.getPx();

		Map<String, Boolean> menuLinks = new HashMap<String, Boolean>();

		menuLinks.put("受理报价", false);

		// 受理报价全工位
		menuLinks.put("acceptance", false);
		{
			String links = getLinksByPositions(userPositions, LINE_ACCEPT_QUOTATE, section_id, px, false, conn);
			if (links.length() > 0) {
				menuLinks.put("acceptance", true);
				req.setAttribute("beforePosition", links);
				menuLinks.put("受理报价", true);
			} else {
				links = getLinksByPositions(userPositions, LINE_FACTM, section_id, px, false, conn);
				if (links.length() > 0) {
					menuLinks.put("acceptance", true);
					req.setAttribute("beforePosition", links);
					menuLinks.put("受理报价", true);
				}
			}
		}
		if (privacies.contains(RvsConsts.PRIVACY_RECEPT_FACT)) {
			menuLinks.put("acceptance", true);
			menuLinks.put("受理报价", true);
		}

		menuLinks.put("beforeline", false);
		// 受理线长
		if (LINE_ACCEPT_QUOTATE.equals(user.getLine_id())) {
			if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
				menuLinks.put("beforeline", true);
			}
		}
		if (privacies.contains(RvsConsts.PRIVACY_ACCEPTANCE)
				|| privacies.contains(RvsConsts.PRIVACY_RECEPT_FACT)
				|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT)) {
			menuLinks.put("beforeline", true);
			menuLinks.put("受理报价", true);
		}

		//////////////////////////////////////////////////////////////////////////////////

		// 现品管理
		menuLinks.put("现品管理", false);

		// WIP管理
		if (privacies.contains(RvsConsts.PRIVACY_WIP)) {
			menuLinks.put("wip", true);
			menuLinks.put("现品管理", true);
		} else {
			menuLinks.put("wip", false);
		}

		// 现品投线
		if (privacies.contains(RvsConsts.PRIVACY_WIP)
				|| privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL))
		{
			menuLinks.put("fact_material", true);
			menuLinks.put("现品管理", true);
		} else {
			menuLinks.put("fact_material", false);
		}

		// 零件BO管理
		if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			menuLinks.put("bo_partial", true);
			menuLinks.put("现品管理", true);
		} else {
			menuLinks.put("bo_partial", false);
		}

		/////////////////////////////////////////////////////////////////////////////////
		
		// 计划管理
		menuLinks.put("计划管理", false);

		// 计划管理
		if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)
				|| privacies.contains(RvsConsts.PRIVACY_PROCESSING)
				|| privacies.contains(RvsConsts.PRIVACY_SCHEDULE_VIEW)) {
			menuLinks.put("schedule", true);
			menuLinks.put("计划管理", true);
		} else {
			menuLinks.put("schedule", false);
		}

		// 进度管理
		if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)
				|| privacies.contains(RvsConsts.PRIVACY_PROCESSING)
				|| privacies.contains(RvsConsts.PRIVACY_SCHEDULE_VIEW)) {
			menuLinks.put("schedule_processing", true);
			menuLinks.put("计划管理", true);
		} else {
			menuLinks.put("schedule_processing", false);
		}

		/////////////////////////////////////////////////////////////////////////////////

		// 在线作业
		menuLinks.put("在线作业", false);

		menuLinks.put("inlinePosition", false);
		menuLinks.put("decomposeline", false);
		menuLinks.put("nsline", false);
		menuLinks.put("composeline", false);
		menuLinks.put("repairline", false);

		String inlinePosition = "";

		menuLinks.put("disassembleStorage", false);
		menuLinks.put("composeStorage", false);

		if (privacies.contains(RvsConsts.PRIVACY_ADMIN)) {
			menuLinks.put("disassembleStorage", true);
		}
//		if ("00000000003".equals(section_id)) {
//			if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
//				menuLinks.put("repairline", true);
//				menuLinks.put("在线作业", true);
//			}
//			if (privacies.contains(RvsConsts.PRIVACY_POSITION)) {
//				String links = getLinksByPositions(userPositions, LINE_DECOM, section_id, px, false, conn);
//				inlinePosition += links;
//				links = getLinksByPositions(userPositions, LINE_NS, section_id, px, false, conn);
//				inlinePosition += links;
//				links = getLinksByPositions(userPositions, LINE_COM, section_id, px, false, conn);
//				inlinePosition += links;
//			}
//		} else {
			// 分解
			if (LINE_DECOM.equals(user.getLine_id())) {
				if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
					menuLinks.put("decomposeline", true);
					menuLinks.put("在线作业", true);
				}
				if (privacies.contains(RvsConsts.PRIVACY_POSITION)) {
					String links = getLinksByPositions(userPositions, LINE_DECOM, section_id, px, false, conn);
					inlinePosition += links;
				}
				if ("00000000001".equals(section_id)) {
					menuLinks.put("disassembleStorage", true);
					menuLinks.put("composeStorage", true);
				}
			}

			// ＮＳ
			if (LINE_NS.equals(user.getLine_id())) {
				if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
					menuLinks.put("nsline", true);
					menuLinks.put("在线作业", true);
				}
				if (privacies.contains(RvsConsts.PRIVACY_POSITION)) {
					String links = getLinksByPositions(userPositions, LINE_NS, section_id, px, false, conn);
					inlinePosition += links;
				}
				if ("00000000001".equals(section_id)) {
					menuLinks.put("composeStorage", true);
				}
			}

			// 总组
			if (LINE_COM.equals(user.getLine_id())) {
				if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
					menuLinks.put("composeline", true);
					menuLinks.put("在线作业", true);
					// 总组库位
				}
				if (privacies.contains(RvsConsts.PRIVACY_POSITION)) {
					String links = getLinksByPositions(userPositions, LINE_COM, section_id, px, false, conn);
					inlinePosition += links;
				}
				if ("00000000001".equals(section_id)) {
					menuLinks.put("composeStorage", true);
				}
			}
//		}

		if (inlinePosition.length() > 0) {
			req.setAttribute("inlinePosition", inlinePosition);
			menuLinks.put("inlinePosition", true);
			menuLinks.put("在线作业", true);
		}

		// 出货
		menuLinks.put("shipping", false);

		if (privacies.contains(RvsConsts.PRIVACY_RECEPT_FACT)) {
			String links = getLinksByPositions(userPositions, LINE_SHIP, section_id, px, false, conn);
			if (links.length() > 0) {
				inlinePosition += links;
				req.setAttribute("inlinePosition", inlinePosition);
				menuLinks.put("inlinePosition", true);
				menuLinks.put("在线作业", true);
			}
		}

		// 辅助工作
		if (privacies.contains(RvsConsts.PRIVACY_POSITION)) {
			menuLinks.put("support", true);
			menuLinks.put("在线作业", true);
		} else {
			menuLinks.put("support", false);
		}

		// 清洗
		if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			menuLinks.put("wash", true);
			// menuLinks.put("在线作业", true);
		} else {
			menuLinks.put("wash", false);
		}

		menuLinks.put("在线作业（特殊）", false);
		{
			String links = getLinksByPositions(userPositions, null, section_id, px, true, conn);
			if (links.length() > 0) {
				req.setAttribute("inlineSpecPosition", links);
				menuLinks.put("在线作业（特殊）", true);
			}
		}

		///////////////////////////////////////////////////////////////////

		// 品保
		menuLinks.put("品保作业", false);

		if (privacies.contains(RvsConsts.PRIVACY_QA_VIEW)) {
			menuLinks.put("qa_view", true);
			menuLinks.put("品保作业", true);
		} else {
			menuLinks.put("qa_view", false);
		}

		String links = getLinksByPositions(userPositions, LINE_QA, section_id, px, false, conn);
		if (privacies.contains(RvsConsts.PRIVACY_QUALITY_ASSURANCE) || links.length() > 0) {
//			links = links.replaceAll("javascript:getPositionWork\\('00000000046'\\);", "qualityAssurance.do")
//					.replaceAll("javascript:getPositionWork\\('00000000051'\\);", "service_repair_referee.do")
//					.replaceAll("javascript:getPositionWork\\('00000000052'\\);", "qualityAssurance.do?position_id=00000000052")
//					.replaceAll("javascript:getPositionWork\\('00000000062'\\);", "qualityAssurance.do?position_id=" + RvsConsts.POSITION_QA_P_613);
			req.setAttribute("qaPosition", links);
			menuLinks.put("qa_work", true);
			menuLinks.put("品保作业", true);
		} else {
			menuLinks.put("qa_work", false);
		}

		if (privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)) {
			menuLinks.put("qa_manage", true);
			menuLinks.put("品保作业", true);
		} else {
			menuLinks.put("qa_manage", false);
		}

		///////////////////////////////////////////////////////////////////

		// 文档管理
		menuLinks.put("文档管理", false);
		menuLinks.put("文档管理-manager", false);

		// 归档
		if (privacies.contains(RvsConsts.PRIVACY_FILING)
				|| privacies.contains(RvsConsts.PRIVACY_READFILE)) {
			menuLinks.put("filing", true);
			menuLinks.put("文档管理", true);
		} else {
			menuLinks.put("filing", false);
		}

		// 文档管理-manager
		if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)
				|| privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)) {
			menuLinks.put("文档管理-manager", true);
		}

		///////////////////////////////////////////////////////////////////

		// 进度查询
		if (privacies.contains(RvsConsts.PRIVACY_INFO_EDIT) || privacies.contains(RvsConsts.PRIVACY_INFO_VIEW)) {
			menuLinks.put("info", true);
		} else {
			menuLinks.put("info", false);
		}

		///////////////////////////////////////////////////////////////////

		// 展示
		if (privacies.contains(RvsConsts.PRIVACY_VIEW)) {
			menuLinks.put("viewer", true);
		} else {
			menuLinks.put("viewer", false);
		}

		///////////////////////////////////////////////////////////////////

		// 系统信息管理
		if (privacies.contains(RvsConsts.PRIVACY_SA) || privacies.contains(RvsConsts.PRIVACY_ADMIN)) {
			menuLinks.put("admin", true);
		} else {
			menuLinks.put("admin", false);
		}

		// 技术文档管理
		if (privacies.contains(RvsConsts.PRIVACY_TECHNICAL_MANAGE)) {
			menuLinks.put("tech", true);
		} else {
			menuLinks.put("tech", false);
		}
		
		// 常用采购清单管理
		if ("00000000006".equals(section_id)) {
			menuLinks.put("support_admin", true);
		} else {
			menuLinks.put("support_admin", false);
		}
		
		//物品申购
		if (user.getRole_id().equals(RvsConsts.ROLE_MANAGER) 
				|| "00000000006".equals(section_id) 
				|| (!CommonStringUtil.isEmpty(section_id) && (privacies.contains(RvsConsts.PRIVACY_LINE) 
						|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT) 
						|| privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)
						|| privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY) 
						|| privacies.contains(RvsConsts.PRIVACY_TECHNICAL_MANAGE)))) {
			menuLinks.put("supplies_operation", true);
		} else {
			menuLinks.put("supplies_operation", false);
		}

		// 可用链接设定到画面
		req.setAttribute("menuLinks", menuLinks);
		req.setAttribute("linkto", req.getParameter("linkto"));

		log.info("AppMenuAction.init end");
	}

	/**
	 * 取得工位链接
	 * @param positions
	 * @param line_id
	 * @param px 
	 * @param conn 
	 * @return
	 */
	private String getLinksByPositions(List<PositionEntity> positions, String line_id, String section_id, String px,
			boolean getUnitized, SqlSession conn) {
		StringBuffer ret = new StringBuffer("");
		Map<String, String> groupSubPositions = PositionService.getGroupSubPositions(conn);
		Map<String, List<String>> groupPosSections = PositionService.getGroupPosSections(conn);
		Set<String> groupPositions = new HashSet<String>();

		for (PositionEntity position : positions) {
			if (position.getPosition_id() == null) {
				continue;
			}

			boolean isPositionUnitizeds = PositionService.getPositionUnitizeds(conn).containsKey(position.getPosition_id());
			if (getUnitized && !isPositionUnitizeds) {
				continue;
			} else if (!getUnitized && isPositionUnitizeds) {
				continue;
			}
			if (line_id == null || line_id.equals(position.getLine_id())) {
				if ("00000000001".equals(section_id) && position.getLight_division_flg() != null 
						&& position.getLight_division_flg() == 1 && !"4".equals(px)) {
					if (position.getProcess_code().startsWith("5")) {
						if ("0".equals(px)) px = "3";
						if (position.getProcess_code().startsWith("53") || position.getProcess_code().startsWith("54")) {
							ret.append("<a href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "');\">" +
									position.getProcess_code() + " " + position.getName() + 
									"</a><br>");
						}
						else if ("3".equals(px)) {
							ret.append("<a href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 3);\">" +
									position.getProcess_code() + " " + position.getName() + 
									"</a><br><px>");
							ret.append("<a href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 2);\">" + 
									" B线</a>");
							ret.append("<a class=\"px_on\" href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 3);\">" + 
									" C线</a></px><br>");
						}
						else {
							ret.append("<a href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 2);\">" +
									position.getProcess_code() + " " + position.getName() + 
									"</a><br><px>");
							ret.append("<a class=\"px_on\" href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 2);\">" + 
									" B线</a>");
							ret.append("<a href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 3);\">" + 
									" C线</a></px><br>");
						}
					} else {
						if ("0".equals(px)) px = "1";
						if ("1".equals(px)) {
							ret.append("<a href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 1);\">" +
									position.getProcess_code() + " " + position.getName() + 
									"</a><br><px>");
							ret.append("<a class=\"px_on\" href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 1);\">" +
									" A线</a>");
							ret.append("<a href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 2);\">" + 
									" B线</a></px><br>");
						}
						else {
							ret.append("<a href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 2);\">" +
									position.getProcess_code() + " " + position.getName() + 
									"</a><br><px>");
							ret.append("<a href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 1);\">" +
									" A线</a>");
							ret.append("<a class=\"px_on\" href=\"javascript:getPositionWork('" 
									+ position.getPosition_id() + "', 2);\">" + 
									" B线</a></px><br>");
						}
					}
				} else { // 无分线
					boolean show = true;
					if (groupSubPositions.containsKey(position.getPosition_id())) { // 虚拟组工位
						String groupPositionId = groupSubPositions.get(position.getPosition_id());
						if (groupPosSections.get(groupPositionId) != null
								&& groupPosSections.get(groupPositionId).contains(section_id)) {
							groupPositions.add(groupSubPositions.get(position.getPosition_id()));
							show = false;
						}
					} else if (groupPosSections.containsKey(position.getPosition_id())) {
						show = false;
					}
					if (show) {
						ret.append("<a href=\"javascript:getPositionWork('" 
								+ position.getPosition_id() + "');\">" +
								position.getProcess_code() + " " + position.getName() + 
								"</a><br>");
					}
				}
			}
		}

		if (!groupPositions.isEmpty()) {
			PositionService pService = new PositionService();
			for (String groupPositionId : groupPositions) {
				PositionEntity groupPositionEntity = pService.getPositionEntityByKey(groupPositionId, conn);
				ret.append("<a href=\"javascript:getPositionWork('" 
						+ groupPositionEntity.getPosition_id() + "');\">+ " +
						" " + groupPositionEntity.getName() + 
						"</a><br>");
			}
		}

		return ret.toString();
	}

	/**
	 * 零件菜单初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void pinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("AppMenuAction.pinit start");

		if (req.getParameter("ex") != null) {
			// 迁移到页面
			actionForward = mapping.findForward("partial-ex");
		} else {
			// 迁移到页面
			actionForward = mapping.findForward("partial");
		}
		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		Map<String, Boolean> menuLinks = new HashMap<String, Boolean>();

		// 零件管理
		if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)) {
			menuLinks.put("partial_admin", true);
		} else {
			menuLinks.put("partial_admin", false);
		}

		// 现品 (管理/订购)
		if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_ORDER)) {
			menuLinks.put("fact", true);
		} else {
			menuLinks.put("fact", false);
		}

		// 线内 (签收)
		menuLinks.put("line", false);
		if ("00000000012".equals(user.getLine_id()) || "00000000013".equals(user.getLine_id())
				|| "00000000014".equals(user.getLine_id())) {
			if (privacies.contains(RvsConsts.PRIVACY_LINE) || privacies.contains(RvsConsts.PRIVACY_POSITION)) {
				List<PositionEntity> positions = user.getPositions();
				for (PositionEntity position : positions) {

					String position_id = position.getPosition_id();
					if ("00000000021".equals(position_id) || "00000000027".equals(position_id)
							|| "00000000032".equals(position_id)) {
						menuLinks.put("line", true);
						break;
					}
				}
			}
		}

		// 零件分析
		if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)) {
			menuLinks.put("analysis", true);
			menuLinks.put("line", true);
		} else {
			menuLinks.put("analysis", false);
		}

		// 损金
		if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)
				 || privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
			menuLinks.put("loss", true);
		} else {
			menuLinks.put("loss", false);
		}

		// 可用链接设定到画面
		req.setAttribute("menuLinks", menuLinks);
		req.setAttribute("linkto", req.getParameter("linkto"));

		log.info("AppMenuAction.pinit end");
	}
	
	/**
	 * 设备工具+治具初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void tinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("AppMenuAction.tinit start");

		if (req.getParameter("ex") != null) {
			// 迁移到页面
			actionForward = mapping.findForward("tools-ex");
		} else {
			// 迁移到页面
			actionForward = mapping.findForward("tools");
		}

		Map<String, Boolean> menuLinks = new HashMap<String, Boolean>();

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		if (privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY)) {
			menuLinks.put("dt_admin", true);
		} else {
			menuLinks.put("dt_admin", false);
		}

		// 可用链接设定到画面
		req.setAttribute("menuLinks", menuLinks);
		req.setAttribute("linkto", req.getParameter("linkto"));

		log.info("AppMenuAction.tinit end");
	}

	public void pdaMenu(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("AppMenuAction.pdaMenu start");

		String flg = req.getParameter("flg");
		if (flg == null) {
			req.setAttribute("isFact", false);
			req.setAttribute("isRecept", false);
			// 得到会话用户信息
			HttpSession session = req.getSession();
			LoginData loginData = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
			List<Integer> privacies = loginData.getPrivacies();
			if (privacies != null) {
				if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
					req.setAttribute("isFact", true);
				}
				if (privacies.contains(RvsConsts.PRIVACY_RECEPT_FACT)) {
					req.setAttribute("isRecept", true);
				}
			}
		} else if ("cs".equals(flg)) {
			req.setAttribute("isFact", true);
			req.setAttribute("isRecept", false);
		} else if ("tc".equals(flg)) {
			req.setAttribute("isFact", false);
			req.setAttribute("isRecept", true);
		}

		actionForward = mapping.findForward(FW_PDA_MENU);

		log.info("AppMenuAction.pdaMenu end");
	}

}

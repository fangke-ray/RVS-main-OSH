/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：消耗品仓库管理记录<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.partial;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.partial.ConsumableApplyService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.DateUtil;

public class ConsumableManageAction extends BaseAction {

	private Logger log = Logger.getLogger(this.getClass());

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
	public void init(ActionMapping mapping, ActionForm form,HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("ConsumableManageAction.init start");

		req.setAttribute("search_page", req.getParameter("page"));
		req.setAttribute("search_key", req.getParameter("key"));

		// 取得申请课室
		SectionService sectionService = new SectionService();
		String sOptionsSection = sectionService.getAllOptions(conn);
		req.setAttribute("sOptionsSection", sOptionsSection);

		// 取得申请工程
		LineService lineService = new LineService();
		String sOptionsLine = lineService.getInlineOptions(conn);
		req.setAttribute("sOptionsLine", sOptionsLine);
		
		String sConsumableType = CodeListUtils.getGridOptions("consumable_type");
		req.setAttribute("cConsumableType", sConsumableType);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);		
		req.setAttribute("start_date", DateUtil.toString(cal.getTime(),DateUtil.DATE_PATTERN));

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		//权限区分
		req.setAttribute("fact", "0");
		req.setAttribute("process", "0");
		req.setAttribute("applier", "0");

		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			req.setAttribute("fact", "1");
		}
		if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			req.setAttribute("process", "1");
		}
		ConsumableApplyService caService = new ConsumableApplyService();
		if (caService.getSupplyOrderPrivacy(user) != null) {
			req.setAttribute("applier", "1");
		}
		if (privacies.contains(RvsConsts.PRIVACY_TECHNICAL_MANAGE)) {
			req.setAttribute("applier", "1");
		}

		actionForward = mapping.findForward(FW_INIT);

		log.info("ConsumableManageAction.init end");
	}
}

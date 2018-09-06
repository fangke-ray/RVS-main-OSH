/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：消耗品在线一览Action<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.partial;

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
import com.osh.rvs.bean.partial.ConsumableOnlineEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.partial.ConsumableOnlineForm;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.partial.ConsumableOnlineService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ConsumableOnlineAction extends BaseAction {

	/** log */  
	private Logger log = Logger.getLogger(getClass());
	
	/** 消耗品在线一览Service */
	private ConsumableOnlineService service = new ConsumableOnlineService();

	/**
	 * 消耗品在线一览初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		
		log.info("ConsumableOnlineAction.init start");

		// 取得Session
		HttpSession session = req.getSession();

		// 取得Session中USERDATA
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得section_id
		String section_id = user.getSection_id();

		// 取得line_id
		String line_id = user.getLine_id();

		// 取得课室
		SectionService sectionService = new SectionService();
		String sectionOptions = sectionService.getOptions(conn, "(全部)", section_id);

		// 取得工程
		LineService lineService = new LineService();
		String lineOptions = lineService.getOptions(conn, "(全部)", line_id);

		// 分类设定 
		req.setAttribute("gqOptions",CodeListUtils.getGridOptions("consumable_type"));

		// 课室画面设定
		req.setAttribute("sectionOptions", sectionOptions);

		// 工程画面设定
		req.setAttribute("lineOptions", lineOptions);
		
		actionForward = mapping.findForward(FW_INIT);
		log.info("ConsumableOnlineAction.init end");
	}

	/**
	 * 消耗品在线一览查询
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) 
			throws Exception {

		log.info("ConsumableOnlineAction.search start");

		// 消耗品在线一览Form
		ConsumableOnlineForm consumableOnlineForm = (ConsumableOnlineForm) form;

		// 消耗品在线一览Entity
		ConsumableOnlineEntity consumableOnlineEntity = new ConsumableOnlineEntity();

		// 错误报告信息
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 表单复制到Bean
		BeanUtil.copyToBean(consumableOnlineForm, consumableOnlineEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 查询 
		List<ConsumableOnlineForm> list = service.searchOnlineList(consumableOnlineEntity, conn);

		// 将表单发送到页面
		listResponse.put("consumable_list", list);
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ConsumableOnlineAction.search end");
	}

	/**
	 *  消耗品在线一览清点
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) 
			throws Exception {

		log.info("ConsumableOnlineAction.doupdate start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		// 错误报告信息
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 清点
		service.updateOnlineList(req.getParameterMap(), req.getSession(), conn, errors);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ConsumableOnlineAction.doupdate end");
	}

}

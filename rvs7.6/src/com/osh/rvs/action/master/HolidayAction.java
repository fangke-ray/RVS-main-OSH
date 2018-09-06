/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：假日系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.master;

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

import com.osh.rvs.service.HolidayService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.DateUtil;

public class HolidayAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 假日系统管理处理
	 */
	private HolidayService service = new HolidayService();

	/**
	 * 假日管理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("HolidayAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("HolidayAction.init end");
	}

	/**
	 * 显示月内假日查询处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void getHolidays(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("HolidayAction.getHolidays start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String month = req.getParameter("month");

		// 执行检索
		List<String> lResultForm = service.search(month, conn);
		
		// 查询结果放入Ajax响应对象
		listResponse.put("signed", lResultForm);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("HolidayAction.getHolidays end");
	}

	/**
	 * 工程数据更新实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("HolidayAction.doupdate start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 修改记录表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String date = req.getParameter("date");

		// 执行更新
		service.update(DateUtil.toDate(date, DateUtil.DATE_PATTERN), conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("HolidayAction.doupdate end");
	}
}

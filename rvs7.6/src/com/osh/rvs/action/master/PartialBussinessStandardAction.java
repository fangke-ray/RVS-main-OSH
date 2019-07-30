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

import com.osh.rvs.form.master.PartialBussinessStandardForm;
import com.osh.rvs.service.PartialBussinessStandardService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;

/**
 * 零件出入库工时标准
 * 
 * @author liuxb
 * 
 */
public class PartialBussinessStandardAction extends BaseAction {
	private final Logger log = Logger.getLogger(getClass());

	private final PartialBussinessStandardService service = new PartialBussinessStandardService();

	/**
	 * 查询零件出入库工时标准
	 * 
	 * @param mpping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mpping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialBussinessStandardAction.search start");

		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		List<PartialBussinessStandardForm> finished = service.search(conn);

		listResponse.put("errors", errors);
		listResponse.put("finished", finished);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("PartialBussinessStandardAction.search end");
	}

	/**
	 * 更新零件出入库工时标准
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PartialBussinessStandardAction.doUpdate start");

		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		service.update(conn, req, errors);

		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("PartialBussinessStandardAction.doUpdate end");
	}
}

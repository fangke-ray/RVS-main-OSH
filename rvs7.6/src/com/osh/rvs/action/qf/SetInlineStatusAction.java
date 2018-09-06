/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：在线状态统计结果<br>
 * @author 龚镭敏
 * @version 2.01
 */
package com.osh.rvs.action.qf;

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

import com.osh.rvs.service.qf.AcceptanceService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;


public class SetInlineStatusAction extends BaseAction {

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
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("SetInlineStatusAction.init start");
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("SetInlineStatusAction.init end");
	}

	/**
	 * 批量数据查询
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void get(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("SetInlineStatusAction.get start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		AcceptanceService service = new AcceptanceService();

		callbackResponse.put("status", service.getOgz(req, conn, errors));

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("SetInlineStatusAction.get end");
	}	

	/**
	 * 批量数据实际导入
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doImport(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("SetInlineStatusAction.doImport start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		AcceptanceService service = new AcceptanceService();

		service.importOgz(req, conn, errors);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("SetInlineStatusAction.doImport end");
	}	

}

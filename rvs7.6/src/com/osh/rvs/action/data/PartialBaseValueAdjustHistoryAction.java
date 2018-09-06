package com.osh.rvs.action.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.PartialBaseValueAdjustHistoryForm;
import com.osh.rvs.service.partial.PartialBaseValueAdjustHistoryService;
import com.osh.rvs.service.partial.PartialWasteModifyHistoryService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;

/**
 * 零件基准值调整历史
 * 
 * @author lxb
 * 
 */
public class PartialBaseValueAdjustHistoryAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	/**
	 * 初始话页面
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 请求
	 * @param res 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("PartialBaseValueAdjustHistoryAction.init start");
		
		PartialWasteModifyHistoryService service  = new PartialWasteModifyHistoryService();
		//所有操作者
		String opertorIdNameOptions = service.getOpertorIdNameptions(conn);
		req.setAttribute("sOpertorIdNameOptions", opertorIdNameOptions);//操作者Options
		//设定类型
		req.setAttribute("sIdentification", CodeListUtils.getSelectOptions("identification",null,""));
		req.setAttribute("goIdentification",CodeListUtils.getGridOptions("identification"));
		
		Calendar cal=Calendar.getInstance();
		
		String strDate=RvsUtils.getBussinessHalfStartDate(cal);//起效日期开始
		req.setAttribute("start_date_start", strDate);

		actionForward = mapping.findForward(FW_INIT);
		
		log.info("PartialBaseValueAdjustHistoryAction.init end");
	}
	
	/**
	 * 检索
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 请求
	 * @param res 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,SqlSession conn)throws Exception{
		log.info("PartialBaseValueAdjustHistoryAction.search start");
		//对Ajax响应
		Map<String, Object> lResponseResult=new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		
		PartialBaseValueAdjustHistoryService service=new PartialBaseValueAdjustHistoryService();
		List<PartialBaseValueAdjustHistoryForm> responseFormList=service.searchPartialBaseValueAdjustHistory(form, conn);
		
		lResponseResult.put("finished", responseFormList);
		lResponseResult.put("errors", msgInfos);
		
		// 返回Json格式回馈信息
		returnJsonResponse(res, lResponseResult);
		
		log.info("PartialBaseValueAdjustHistoryAction.search end");
	}
}

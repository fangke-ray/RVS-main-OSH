package com.osh.rvs.action.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.partial.PartialWasteModifyHistoryForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.partial.PartialWasteModifyHistoryService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;


public class PartialWasteModifyHistoryAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());
	
	private PartialWasteModifyHistoryService service  = new PartialWasteModifyHistoryService();

	/**
	 * 零件废改订/历史管理 初始化显示
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PartialWasteModifyHistoryAction.init start");
		
		ModelService modelService = new ModelService();
		
		//所有型号
		String mReferChooser = modelService.getOptions(conn);
		
		//所有操作者
		String opertorIdNameOptions = service.getOpertorIdNameptions(conn);
		
		req.setAttribute("opertorIdNameOptions", opertorIdNameOptions);//操作者Options
		req.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("PartialWasteModifyHistoryAction.init end");
	}
	/**
	 * 零件废改订/历史管理 检索
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PartialWasteModifyHistoryAction.search start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
	    List<PartialWasteModifyHistoryForm> partialWasteModifyHistoryFormList =	service.search(form,conn, errors);	    
	    
	    listResponse.put("partialWasteModifyHistoryFormList", partialWasteModifyHistoryFormList);
		listResponse.put("errors", errors);
		returnJsonResponse(res, listResponse);
		log.info("PartialWasteModifyHistoryAction.search  end");
	}

}

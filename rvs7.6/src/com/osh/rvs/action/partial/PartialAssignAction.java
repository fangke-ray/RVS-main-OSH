package com.osh.rvs.action.partial;

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

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.service.partial.PartialAssignService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;

/**
 * 零件发放
 * 
 * @author lxb
 * 
 */
public class PartialAssignAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,SqlSession conn) throws Exception {
		log.info("PartialAssignAction.init start");

		PartialAssignService service = new PartialAssignService();

		// 取得零件订购信息
		Map<String, List<MaterialPartialDetailEntity>> responseMap = service.searchMaterialPartialDetailMap(conn);
		request.getSession().setAttribute("partialMap", responseMap);

		//request.getSession().removeAttribute("partialMap");
		request.getSession().removeAttribute("partialReceptMap");
		request.getSession().removeAttribute("partialSessionMap");
		request.getSession().removeAttribute("responseSessionForms");
		request.getSession().removeAttribute("updateSessionMap");
		
		actionForward = mapping.findForward(FW_INIT);

		log.info("PartialAssignAction.init end");
	}

	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialAssignAction.search start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PartialAssignService service = new PartialAssignService();
		String occur_times=request.getParameter("occur_times");

		MaterialForm responseForm = service.searchMaterialAssignDetail(form,occur_times, conn);
		
		MaterialPartialDetailForm materialPartialDetailForm=(MaterialPartialDetailForm)form;
		String materialID=materialPartialDetailForm.getMaterial_id();
		String occureTimes=materialPartialDetailForm.getOccur_times();
		String materialIDAndoccureTimes=materialID+occureTimes;

		// TODO 没必要重复处理
		Map<String, Map<String, List<MaterialPartialDetailForm>>> finished=service.searchPartialAssignDetail(materialIDAndoccureTimes, null, null, request);
		
		listResponse.put("responseForm", responseForm);
		listResponse.put("finished", finished);
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		log.info("PartialAssignAction.search end");
	}
	
	/**
	 * 更新零件发放对象状态
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void changeMaterialStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response, SqlSession conn)throws Exception{
		log.info("PartialAssignAction.changeMaterialStatus start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		PartialAssignService service = new PartialAssignService();
		List<MaterialPartialForm> responseFormList=service.changeMaterialStatus(form,request);
		 
		listResponse.put("responseFormList", responseFormList);
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		
		log.info("PartialAssignAction.changeMaterialStatus end");
	}
	
	/**
	 * 更新零件订购签收明细
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 117})
	public void doUpdateMaterialPartialDetail(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("PartialAssignAction.updateMaterialPartialDetail start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		PartialAssignService service = new PartialAssignService();

	    service.updateMaterialPartialDetail(request,conn,errors);

		@SuppressWarnings("unchecked")
		List<MaterialPartialEntity> responseFormList=(List<MaterialPartialEntity>)request.getSession().getAttribute("responseSessionForms");

		listResponse.put("responseFormList", responseFormList);
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		log.info("PartialAssignAction.updateMaterialPartialDetail end");
	}
}

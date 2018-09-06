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
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.service.partial.PartialOrderService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;

public class PartialOrderAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private PartialOrderService service = new PartialOrderService();

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("PartialOrderAction.init start");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		
		//权限区分
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)) {
			//零件管理员 上传文件不可见
			req.setAttribute("role", "partialManager");
		}else if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			//现品人员 提交button和确认button不可见
			req.setAttribute("role", "xianpin");
		}
		
		//维修对象等级
		String oMateriaLevel = CodeListUtils.getGridOptions("material_level");
		req.setAttribute("oMateriaLevel",oMateriaLevel);
		
		actionForward = mapping.findForward(FW_INIT);

		log.info("PartialOrderAction.init end");
	}

	/**
	 * 订购零件未完成分配之前查询
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("PartialOrderAction.search start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		//根据维修对象ID和订购次数查询出未完成分配的订购零件
		List<MaterialPartialForm> partialFormList = service.searchMaterialPartialDetailWithStatus(conn);
		req.getSession().setAttribute("partialFormPositioning", partialFormList);

		listResponse.put("partialFormList", partialFormList);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.search end");
	}
	
	/**
	 * 维修对象详细信息
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("PartialOrderAction.detail start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		MaterialEntity materialEntity = new MaterialEntity();
		MaterialPartialDetailForm materialPartialDetailForm = (MaterialPartialDetailForm) form;

		materialEntity.setMaterial_id(materialPartialDetailForm.getMaterial_id());
		
		//判断双击时才出发维修详细信息的查找，其他情况就只对订购零件的信息查询
		if(req.getParameter("append").equals("true")){
			/* 查询维修对象的详细显示信息 */
			List<MaterialForm> materialDetail = service.searchMaterialDetail(materialEntity, conn);
			for (int i = 0; i < materialDetail.size(); i++) {
				MaterialForm materialForm = materialDetail.get(0);
				listResponse.put("materialDetail", materialForm);
			}
		}	
		/*根据belongs查询所有零件 */
		List<MaterialPartialDetailForm> partialOrderFormList = service.searchPartialOrder(materialPartialDetailForm,conn);
		
		/*根据belongs查询未分配零件(每一次点击除未分配的radio时，都进行一次未分配零件数据的查询) */
		//materialPartialDetailForm.setBelongs("0");
		//List<MaterialPartialDetailForm> partialOrderFormNoAssginList= service.searchPartialOrder(materialPartialDetailForm,conn);
		//listResponse.put("partialOrderFormNoAssginList", partialOrderFormNoAssginList);
		
		listResponse.put("partialOrderFormList", partialOrderFormList);
		
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.detail end");
	}

	/**
	 * 根据belongs更新数据表material_partial_detail
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doAssagin(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("PartialOrderAction.doAssagin start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		 
		MaterialPartialDetailForm  materialPartialDetailForm = (MaterialPartialDetailForm) form;
		
		//维修对象零件详细信息显示-零件追加分配的时候弹出的dialog
		List<MaterialPartialDetailForm> materialPartialDetailFormList 
			= service.getParam(materialPartialDetailForm, req, conn, errors, listResponse, req.getParameter("modelId"));

		listResponse.put("materialPartialDetailFormList", materialPartialDetailFormList);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.doAssagin end");
	}
	
	/**
	 * 双击根据KEY获取material_partial_detail详细数据
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void materialPartialDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("PartialOrderAction.materialPartialDetail start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		 
		//维修对象零件详细信息显示-零件追加分配的时候弹出的dialog
		MaterialPartialDetailForm materialPartialDetailForm= service.getDBMaterialPartialDetail(req.getParameter("material_partial_detail_key"),req.getParameter("modelID"),conn,errors,listResponse);

		listResponse.put("materialPartialDetailForm", materialPartialDetailForm);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.materialPartialDetail end");
	}
	
	/**
	 * 确定button返回前页
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void confirm(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("PartialOrderAction.confirm start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		//维修对象显示定位完毕OK
		List<MaterialPartialForm> returnFormList=service.searchPartialOrderConfirm((MaterialPartialDetailForm)form ,req);
		
		listResponse.put("returnFormList", returnFormList);
		req.getSession().setAttribute("partialFormPositioning", returnFormList);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.confirm end");
	}
	
	/**
	 * 确定button进行所有零件都已有工位 验证
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void confirmAll(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("PartialOrderAction.confirmAll start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		//维修对象显示定位完毕OK
		List<MaterialPartialForm>  partialFormList = service.confirmProcessCode((MaterialPartialDetailForm)form ,conn,req,errors);

		listResponse.put("returnFormList", partialFormList);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.confirmAll end");
	}

	/**
	 * 提交button更新到material_partial
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void dosubmit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("PartialOrderAction.dosubmit start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		//定位完成（将零件放到维修零件里，更新零件的状态是订购完成）
		List<MaterialPartialForm> partialFormList = service.positioningComplete(request,conn,errors);
	    listResponse.put("partialFormList", partialFormList);
		listResponse.put("errors", errors);

		request.getSession().setAttribute("partialFormPositioning", partialFormList);

		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.dosubmit end");
	}
	
	/**
	 * 进行追加编辑（分配件数和工位）
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("PartialOrderAction.doupdate start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		service.updateMaterialPartialDetailQuantityAndProcesscode(request,request.getParameter("belongs"), conn,errors);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.doupdate end");
	}
	
	/**
	 * 进行追加编辑（分配工位）
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdatePosition(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("PartialOrderAction.doupdatePosition start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		MaterialPartialDetailForm materialPartialDetailForm = (MaterialPartialDetailForm) form;
		
		//直接更新双击的零件的工位
		service.updateMaterialPartialDetailProcesscode(materialPartialDetailForm, conn,errors);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.doupdatePosition end");
	}

	/**
	 * 临时订购信息删除
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void docancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("PartialOrderAction.docancel start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		service.cancel(request.getParameter("material_id"),request.getParameter("occur_times"), conn,errors);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.docancel end");
	}
}
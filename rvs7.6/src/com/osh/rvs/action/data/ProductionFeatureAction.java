package com.osh.rvs.action.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.data.ProductionFeatureForm;
import com.osh.rvs.service.ProductionFeatureService;

import framework.huiqing.action.BaseAction;

public class ProductionFeatureAction extends BaseAction {

	private ProductionFeatureService service = new ProductionFeatureService();
	
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("ProductionFeatureAction.search start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		List<ProductionFeatureForm> lResultForm = service.getProductionFeatureByMaterialId(form, conn);
		listResponse.put("list", lResultForm);
		
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("ProductionFeatureAction.search end");
		
	}
	public void searchFinish(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("ProductionFeatureAction.searchFinish start");
		String id = req.getParameter("id");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		List<ProductionFeatureForm> lResultForm = service.getFinishProductionFeature(id, conn);
		listResponse.put("list", lResultForm);
		
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("ProductionFeatureAction.searchFinish end");
		
	}
	public void searchNoBeforeRework(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("ProductionFeatureAction.searchNoRework start");
		String id = req.getParameter("id");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		List<ProductionFeatureForm> lResultForm = service.getNoBeforeRework(id, conn);
		listResponse.put("list", lResultForm);
		
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("ProductionFeatureAction.searchNoRework end");
		
	}
}

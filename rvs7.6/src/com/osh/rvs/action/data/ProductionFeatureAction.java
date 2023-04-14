package com.osh.rvs.action.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.form.data.ProductionFeatureForm;
import com.osh.rvs.service.ProductionFeatureService;

import framework.huiqing.action.BaseAction;

public class ProductionFeatureAction extends BaseAction {

	private ProductionFeatureService service = new ProductionFeatureService();

	/**
	 * 查询修理品的作业记录列表（修理品详细窗口用）
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("ProductionFeatureAction.search start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Object oBean = req.getSession().getAttribute("materialDetail");
		if (oBean != null) {
			((ProductionFeatureForm) form).setMaterial_id(((MaterialEntity) oBean).getMaterial_id());
		}

		List<ProductionFeatureForm> lResultForm = service.getProductionFeatureByMaterialId(form, conn);
		listResponse.put("list", lResultForm);
		
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("ProductionFeatureAction.search end");
		
	}

	/**
	 * 查询修理品的（仅完成作业）作业记录列表
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Deprecated
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
	/**
	 * 查询修理品的（各工位最初一次）作业记录列表
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Deprecated
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

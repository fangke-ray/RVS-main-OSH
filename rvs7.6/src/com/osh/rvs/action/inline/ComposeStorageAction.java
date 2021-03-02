package com.osh.rvs.action.inline;

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

import com.osh.rvs.form.data.ProductionFeatureForm;
import com.osh.rvs.form.inline.ComposeStorageForm;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.inline.ComposeStorageService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 总组库位
 * @author Liuxb
 *
 */
public class ComposeStorageAction extends BaseAction{
	private Logger log=Logger.getLogger(getClass());
	/**
	 * 页面初始化
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ComposeStorageAction.init start");
		
		CategoryService categoryService=new CategoryService();
		String cOptions=categoryService.getEndoscopeOptions("", conn);
		request.setAttribute("cOptions", cOptions);// 维修对象机种集合
		
//		SectionService sectionService = new SectionService();
//		String sOptions = sectionService.getOptions(conn, "(全部)");
//		request.setAttribute("sOptions", sOptions);// 维修科室集合

		// 分线取得
		request.setAttribute("pxOptions", CodeListUtils.getSelectOptions("material_px", null, ""));
		request.setAttribute("pxGridOptions", CodeListUtils.getGridOptions("material_px"));

		actionForward = mapping.findForward(FW_INIT);
		log.info("ComposeStorageAction.init end");
	}
	
	/**
	 * 查询维修对象是否正在进行操作
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void find(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ComposeStorageAction.find start");
		Map<String, Object> lResponseResult=new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		
		ComposeStorageService service=new ComposeStorageService();
		ProductionFeatureForm returnform=service.find(conn, request,msgInfos);
		if(returnform!=null){
			ComposeStorageForm composeResultForm = service.checkMaterialExist(conn, returnform.getMaterial_id(), msgInfos);
			lResponseResult.put("composeResultForm", composeResultForm);
		}
		
		lResponseResult.put("finished", returnform);
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(response, lResponseResult);
		log.info("ComposeStorageAction.find end");
	}
	
	/**
	 * 一览
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ComposeStorageAction.search start");
		
		//对Ajax响应
		Map<String, Object> lResponseResult=new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		//获取表单数据
		ComposeStorageForm composeStorageForm=(ComposeStorageForm)form;
		
		ComposeStorageService service=new ComposeStorageService();
		List<ComposeStorageForm> list=service.search(composeStorageForm, conn, msgInfos);
		
		lResponseResult.put("finished", list);
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(response, lResponseResult);
		
		log.info("ComposeStorageAction.search end");
	}
	
	/**
	 * 扫描
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doscan(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ComposeStorageAction.scan start");
		
		//对Ajax响应
		Map<String, Object> lResponseResult=new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
	    msgInfos = v.validate();
		ComposeStorageService service=new ComposeStorageService();

		if(msgInfos.size()==0){
			String material_id = request.getParameter("material_id");
			//根据material_id找出扫描对象
			ComposeStorageForm resultForm = service.checkMaterialExist(conn, material_id, msgInfos);

			// 如果检查输入没有问题
		    if(msgInfos.size()==0){
		    	// 执行总组签收作业启动
		    	service.insertFeature(conn,resultForm,request);
				lResponseResult.put("finished", resultForm);
		    }
	    }
	   
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(response, lResponseResult);
		
		log.info("ComposeStorageAction.scan end");
	}
	/**
	 * 位置分布
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getcomposempty (ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ComposeStorageAction.getcomposempty start");
		
		//对Ajax响应
		Map<String, Object> lResponseResult=new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		
		ComposeStorageService service=new ComposeStorageService();
		List<ComposeStorageForm> list=service.getComposEmpty(conn, msgInfos);
		
		lResponseResult.put("finished", list);
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(response, lResponseResult);
		
		log.info("ComposeStorageAction.getcomposempty end");
	}
	
	public void doChangelocation(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ComposeStorageAction.doChangelocation start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		
		ComposeStorageService service=new ComposeStorageService();
		
		service.changelocation(conn, request.getParameter("goods_id"),request.getParameter("old_scan_code"),request.getParameter("scan_code"),msgInfos);
		if(msgInfos.size()==0){
			service.updateFeature(conn, request,request.getParameter("goods_id"));
		}
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		
		log.info("ComposeStorageAction.doChangelocation end");
	}
	/**
	 * 放入库位
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doinsert(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
		log.info("ComposeStorageAction.doinsert start");
		
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		
		ComposeStorageService service=new ComposeStorageService();
		
		service.insertCom(conn, request.getParameter("material_id"),request.getParameter("scan_code"),msgInfos);
		if(msgInfos.size()==0){
			service.updateFeature(conn, request,request.getParameter("material_id"));
		}
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		log.info("ComposeStorageAction.doinsert end");
	}
	
}

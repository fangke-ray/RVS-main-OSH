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

import com.osh.rvs.form.inline.MaterialOgzForm;
import com.osh.rvs.service.MaterialOgzService;
import com.osh.rvs.service.ModelService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.action.inline
 * @ClassName: MaterialOgzAction
 * @Description: OGZ维修对象进度信息
 * @author lxb
 * @date 2014-7-2 上午9:08:25
 * 
 */
public class MaterialOgzAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * 初始化
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("MaterialOgzAction.init start");
		
		ModelService modelService = new ModelService();
		
		//所有型号
		String mReferChooser = modelService.getOptions(conn);
		request.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合
		
		//委托处
		request.setAttribute("sMaterialOcm", CodeListUtils.getSelectOptions("material_ocm", null,""));
		request.setAttribute("goMaterialOcm",CodeListUtils.getGridOptions("material_ocm"));
		
		actionForward=mapping.findForward(FW_INIT);
		
		
		log.info("MaterialOgzAction.init end");
	}
	
	/**
	 * GZ维修对象进度信息一览
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("MaterialOgzAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		MaterialOgzService service=new MaterialOgzService();
		List<MaterialOgzForm> returnForms=service.search(form, conn);
		
		
		listResponse.put("errors", errors);
		listResponse.put("finished", returnForms);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("MaterialOgzAction.search end");
	}
	
	/**
	 *  
	 * @param mappig ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void getMaterialOgz(ActionMapping mappig,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("MaterialOgzAction.getMaterialOgz start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		MaterialOgzService service=new MaterialOgzService();
		MaterialOgzForm returnForm=service.getMaterialOgzById(form, conn);
		
		
		listResponse.put("errors", errors);
		listResponse.put("finished", returnForm);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("MaterialOgzAction.getMaterialOgz end");
	}
	
	/**
	 * 更新
	 * @param mapping
	 * @param form
	 * @param rerquest
	 * @param response
	 * @param conn
	 */
	public void doUpdate(ActionMapping mapping,ActionForm form,HttpServletRequest rerquest,HttpServletResponse response,SqlSessionManager conn){
		log.info("MaterialOgzAction.doUpdate start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			MaterialOgzService  service=new MaterialOgzService();
			service.update(form, conn);
		}
		
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("MaterialOgzAction.doUpdate end");
	}
	
}

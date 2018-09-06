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

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.master.PartialForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.service.AlarmMesssageService;
import com.osh.rvs.service.MaterialPartialService;
import com.osh.rvs.service.partial.PartialAssignService;
import com.osh.rvs.service.partial.PartialReceptService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;

/**
 * 零件签收
 * @author lxb
 *
 */
public class PartialReceptAction extends BaseAction{
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
		log.info("PartialReceptAction.init start");
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("PartialReceptAction.init end");
	}
	
	/**
	 * 查询
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("PartialReceptAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PartialReceptService service=new PartialReceptService();
		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String lineID = loginData.getLine_id();// 工程 ID
		String sectionID = loginData.getSection_id();// 工程 ID

		// 课室 ID
		List<PartialForm> responseFormList=service.secrchMaterialPartial(form, lineID, sectionID, conn);
		
		listResponse.put("responseFormList", responseFormList);
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialReceptAction.search end");
	}
	
	/**
	 * 零件签收对象详细信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void searchMaterialDetail(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("PartialReceptAction.searchMaterialDetail start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		PartialAssignService service = new PartialAssignService();
		
		String occur_times=request.getParameter("occur_times");
		MaterialForm responseForm = service.searchMaterialAssignDetail(form,occur_times,conn);
		
		PartialReceptService partialReceptService=new PartialReceptService();
		//零件一览
		List<MaterialPartialDetailForm> responeList=partialReceptService.searchPartialRecept(form, request, conn);
		
		// 中断维修对象零件可以回收
		// 目前只给NS人员
		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		if ("00000000013".equals(loginData.getLine_id())) {
			AlarmMesssageService amService = new AlarmMesssageService();
			String material_id = request.getParameter("material_id");
			String alarm_id = amService.searchInlinePartialBlock(material_id, loginData.getLine_id(), conn);
			if (alarm_id != null)
				listResponse.put("alarm_id", alarm_id);
		}

		listResponse.put("responseForm", responseForm);
		listResponse.put("responeList", responeList);
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialReceptAction.searchMaterialDetail end");
	}
	
	
	public void doUpdatePartialUnnessaray(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("PartialOrderAction.doUpdatePartialUnnessaray start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);

		PartialReceptService service=new PartialReceptService();
		service.updatePartialUnnessaray(request, conn, loginData.getOperator_id());

		try {
		// 更新可能产生的BO状态变化
		MaterialPartialService mpService=new MaterialPartialService();
		MaterialPartialEntity materialPartialEntity = new MaterialPartialEntity();
		materialPartialEntity.setMaterial_id(request.getParameter("material_id"));
		materialPartialEntity.setOccur_times(Integer.parseInt(request.getParameter("occur_times")));
		mpService.updateBoFlgWithDetail(materialPartialEntity, conn);
		} catch (Exception e) {
			log.error("MINUS BO " + e.getMessage(), e);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.doUpdatePartialUnnessaray end");
	}

	public void doUpdatePartialRecept(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("PartialOrderAction.doUpdatePartialRecept start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
//		MaterialPartialDetailForm  materialPartialDetailForm = (MaterialPartialDetailForm) form;
//		MaterialPartialDetailEntity materialPartialDetailEntity= new MaterialPartialDetailEntity();
//		BeanUtil.copyToBean(materialPartialDetailForm, materialPartialDetailEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		PartialReceptService service=new PartialReceptService();
		service.updatePartialRecept(request, conn);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderAction.doUpdatePartialRecept end");
	}
}

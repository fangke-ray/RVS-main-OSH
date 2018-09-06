package com.osh.rvs.action.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.osh.rvs.bean.manage.EchelonAllocateEntity;
import com.osh.rvs.form.manage.EchelonAllocateForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.manage.EchelonAllocateService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 
 * 梯队设定
 * @author Liwy
 *
 */
public class EchelonAllocateAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());
	
	private EchelonAllocateService service = new EchelonAllocateService();
	
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("EchelonAllocateAction.init start");
		
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合
		
		req.setAttribute("levelOptions", CodeListUtils.getSelectOptions("material_level_inline",null,""));//等级集合
		
		req.setAttribute("echelonOptions", CodeListUtils.getSelectOptions("echelon_code",null,""));//梯队集合


		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("EchelonAllocateAction.init end");
	}
	/**
	 * 梯队历史
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("EchelonAllocateAction.search start");
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		
		//梯队历史详细
		List<EchelonAllocateForm> echelonAllocateFormList = service.searchEchelonAllocate(conn);
		
		//取得最后时间
		String  strEndDate =service.searchEndDate(conn); 
		
		listResponse.put("errors", infoes);
		listResponse.put("echelonAllocateFormEndDate", strEndDate);
		listResponse.put("echelonAllocateFormList", echelonAllocateFormList);
		returnJsonResponse(response, listResponse);
		
		log.info("EchelonAllocateAction.search end");
	}
	/**
	 * 梯队双击事件(梯队历史)
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void echelonHistory(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("EchelonAllocateAction.echelonHistory start");
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		
		
		EchelonAllocateForm echelonAllocateForm = (EchelonAllocateForm) form;
		
		EchelonAllocateEntity echelonAllocateEntity = new EchelonAllocateEntity();
		BeanUtil.copyToBean(echelonAllocateForm, echelonAllocateEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//梯队划分历史查询
		List<EchelonAllocateForm> returnFormList = service.searchEchelonHistorySet(echelonAllocateEntity,conn);
		
		listResponse.put("errors", infoes);
		listResponse.put("returnFormList", returnFormList);
		returnJsonResponse(response, listResponse);
		log.info("EchelonAllocateAction.echelonHistory end");
		
	}
	/**
	 * 梯队计算
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void modelLevelSetDetail(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("EchelonAllocateAction.modelLevelSetDetail start");
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		
		EchelonAllocateForm echelonAllocateForm = (EchelonAllocateForm) form;
			
		//根据最后时间检索查询型号等级梯队
		List<EchelonAllocateForm> returnFormList = service.searchModelLevelSet(echelonAllocateForm,request,conn);	
		
		//获取当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd");
		Calendar calendar = Calendar.getInstance();
		
		listResponse.put("errors", infoes);
		listResponse.put("currentDate",df.format(calendar.getTime()));
		listResponse.put("returnFormList", returnFormList);
		returnJsonResponse(response, listResponse);
		log.info("EchelonAllocateAction.modelLevelSetDetail end");
		
	}
	/**
	 * 检索条件（开始时间和结束时间）
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void searchDate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn){
		log.info("EchelonAllocateAction.searchDate start");
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		
		//取得最后时间
		String  strEndDate =service.searchEndDate(conn); 
		
		//获取当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd");
		Calendar calendar = Calendar.getInstance();
		
		listResponse.put("errors", infoes);
		listResponse.put("startDate", strEndDate);
		listResponse.put("currentDate",df.format(calendar.getTime()));
		
		returnJsonResponse(response, listResponse);
		
		log.info("EchelonAllocateAction.searchDate end");
	}
	
	/**
	 * 梯队划分  更新梯队
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdateEchelon(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
		log.info("EchelonAllocateAction.doUpdateEchelon start");
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		//更新梯队
		service.updateEchelonHistorySet(request,conn);
		
		listResponse.put("errors", infoes);
		returnJsonResponse(response, listResponse);
		log.info("EchelonAllocateAction.doUpdateEchelon end");
	}
}

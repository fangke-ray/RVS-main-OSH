package com.osh.rvs.action.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.manage.ModelLevelSetForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.manage.LevelModelLeedsService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 型号等级拉动台数设置
 * @author lxb
 *
 */
public class LevelModelLeedsAction extends BaseAction{
	private Logger log=Logger.getLogger(getClass());
	
	/**
	 * 初始化
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("LevelModelLeedsAction.init start");

		//梯队
		request.setAttribute("sEchelonCode", CodeListUtils.getSelectOptions("echelon_code",null,""));
		request.setAttribute("goEchelon_code",CodeListUtils.getGridOptions("echelon_code"));

		//等级
		request.setAttribute("sMaterialLevelInline", CodeListUtils.getSelectOptions("material_level_inline",null,""));
		request.setAttribute("goMaterial_level_inline",CodeListUtils.getGridOptions("material_level_inline"));

		// 维修对象型号集合
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		request.setAttribute("mReferChooser", mReferChooser);
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		log.info("LevelModelLeedsAction.init end");
	}
	
	/**
	 * 检索
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("LevelModelLeedsAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		
		LevelModelLeedsService service=new LevelModelLeedsService();
		List<ModelLevelSetForm> responseFormList=service.searchModelLevelSet(form, request, conn);
		
		listResponse.put("responseFormList", responseFormList);
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("LevelModelLeedsAction.search end");
	}
	
	
	/**
	 * 拉动台数计算
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void calculate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("LevelModelLeedsAction.calculate start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		errors=v.validate();
		
		List<ModelLevelSetForm> list = new AutofillArrayList<ModelLevelSetForm>(ModelLevelSetForm.class);//零件
		Map<String,String[]> map=(Map<String,String[]>)request.getParameterMap();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		
		// 整理提交数据
		for (String parameterKey : map.keySet()) {
			 Matcher m = p.matcher(parameterKey);
			 if (m.find()) {
				 String table = m.group(1);
				 if ("forecast_sampling_record".equals(table)) {
				     String column = m.group(2);
				     int icounts = Integer.parseInt(m.group(3));
					 String[] value = map.get(parameterKey);
					 if ("model_id".equals(column)) {
						 list.get(icounts).setModel_id(value[0]);
					 }else if("level".equals(column)){
						 list.get(icounts).setLevel(value[0]);
					 }else if("start_date".equals(column)){
						 list.get(icounts).setStart_date(value[0]);
					 }else if("end_date".equals(column)){
						 list.get(icounts).setEnd_date(value[0]);
					 }
				 }
			 }
		}
		
		if(errors.size() == 0){
			LevelModelLeedsService service=new LevelModelLeedsService();
			service.calculate(form, list,listResponse,conn);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("LevelModelLeedsAction.calculate end");
	}
	
	/**
	 * 更新设置拉动台数
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdateFrecastSetting(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("LevelModelLeedsAction.doUpdateFrecastSetting start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		LevelModelLeedsService service=new LevelModelLeedsService();
		
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		errors=v.validate();
		if(errors.size()==0){
			service.updateForecastSetting(form, conn);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("LevelModelLeedsAction.doUpdateFrecastSetting end");
	}
	
	
	
	/**
	 * 详细信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("LevelModelLeedsAction.detail start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		LevelModelLeedsService service=new LevelModelLeedsService();
		service.detail(form,listResponse, conn);
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("LevelModelLeedsAction.detail end");
	}
	
	/**
	 * 下载编辑拉动台数
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void reportForecastResult(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("LevelModelLeedsAction.reportForecastResult start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		LevelModelLeedsService service=new LevelModelLeedsService();
		String fileName ="拉动台数编辑.xls";
		String filePath = service.dowloadForecastResultSet(request,conn);//文件
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("LevelModelLeedsAction.reportForecastResult end");
	}
	
}

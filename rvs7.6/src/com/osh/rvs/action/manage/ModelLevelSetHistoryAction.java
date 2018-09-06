package com.osh.rvs.action.manage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.manage.ModelLevelSetHistoryForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.manage.ModelLevelSetHistoryService;
import com.osh.rvs.service.partial.PartialWasteModifyHistoryService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;

/**
 * 型号终止、维修设定历史
 * @author lxb
 *
 */
public class ModelLevelSetHistoryAction extends BaseAction{
	Logger log=Logger.getLogger(getClass());
	
	/**
	 * 一面初始化
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 请求
	 * @param res 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,SqlSession conn)throws Exception{
		log.info("ModelLevelSetHistoryAction.init start");
		
		ModelService modelService = new ModelService();
		
		//所有型号
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合
		
		PartialWasteModifyHistoryService service  = new PartialWasteModifyHistoryService();
		//所有操作者
		String opertorIdNameOptions = service.getOpertorIdNameptions(conn);
		req.setAttribute("opertorIdNameOptions", opertorIdNameOptions);//操作者Options
		
		//等级
		req.setAttribute("sMaterialLevelInline", CodeListUtils.getSelectOptions("material_level_inline",null,""));
		req.setAttribute("goMaterial_level_inline",CodeListUtils.getGridOptions("material_level_inline"));
		
		//梯队
		req.setAttribute("goEchelon_code", CodeListUtils.getGridOptions("echelon_code"));
		
		Calendar cal=Calendar.getInstance();
		String strDate=RvsUtils.getBussinessHalfStartDate(cal);//停止修理日期开始
		req.setAttribute("avaliable_end_date", strDate);
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("ModelLevelSetHistoryAction.init end");
		
	}
	
	/**
	 * 检索
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 请求
	 * @param res 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,SqlSession conn)throws Exception{
		log.info("ModelLevelSetHistoryAction.search start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ModelLevelSetHistoryService service=new ModelLevelSetHistoryService();
		List<ModelLevelSetHistoryForm> responseFormList=service.searchModelLevelSetHistory(form, conn);
		
		
		listResponse.put("finished", responseFormList);
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("ModelLevelSetHistoryAction.search end");
	}
	
}

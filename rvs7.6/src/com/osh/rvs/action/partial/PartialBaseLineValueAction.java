package com.osh.rvs.action.partial;

import java.math.BigDecimal;
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

import com.osh.rvs.bean.partial.PartialBaseLineValueEntity;
import com.osh.rvs.form.partial.PartialBaseLineValueForm;
import com.osh.rvs.service.partial.PartialBaseLineValueService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 零件基准值设置
 * @author lxb
 *
 */
public class PartialBaseLineValueAction extends BaseAction{
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
		log.info("PartialBaseLineValueAction.init start");

		// 取得传来的零件代码
		String edit_partial_code = request.getParameter("edit_partial_code");
		if (edit_partial_code != null) {
			request.setAttribute("edit_partial_code", edit_partial_code);
		}

		request.setAttribute("goMaterial_level",CodeListUtils.getGridOptions("material_level"));
		
		PartialBaseLineValueService service=new PartialBaseLineValueService();
		
		String simpleDateEnd=service.getDate(conn, 1);
		String simpleDateStart=service.getDate(conn, 9);
		
		request.setAttribute("simpleDateStart",simpleDateStart);
		request.setAttribute("simpleDateEnd",simpleDateEnd);
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("PartialBaseLineValueAction.init end");
	}
	
	/**
	 *  检索
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("PartialBaseLineValueAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		PartialBaseLineValueService service=new PartialBaseLineValueService();
		
		List<PartialBaseLineValueForm> responseFormList=service.searchPartialBaseLineValue(form,conn);
		
		listResponse.put("responseFormList", responseFormList);
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialBaseLineValueAction.search end");
	}
	
	/**
	 * 更新基准值
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdateTotalForeboardCount(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("PartialBaseLineValueAction.doUpdateTotalForeboardCount start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		PartialBaseLineValueService service=new PartialBaseLineValueService();
		
		List<PartialBaseLineValueEntity> list=new ArrayList<PartialBaseLineValueEntity>();
		
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		errors=v.validate();
		if(errors.size()==0){
			service.checkBaseValue(form,request, conn, errors, list);
				if(errors.size()==0){
//					service.updatePartialPrepairHistroyEndDate(form, conn);
//					service.insertPartialPrepairHistroy(form, conn);
//					service.updateOshForeboardCount(form, conn);
					service.updateBaseValue(list, conn);
				}
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialBaseLineValueAction.doUpdateTotalForeboardCount end");
	}
	
	/**
	 * 下载编辑基准值
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void reportPartialBaseLineValue(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("PartialBaseLineValueAction.reportPartialBaseLineValue start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
				
		PartialBaseLineValueService service=new PartialBaseLineValueService();
		String fileName ="基准值编辑.xls";
		String filePath=service.dowloadPartialBaseLineValue(form,conn);
		
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialBaseLineValueAction.reportPartialBaseLineValue end");
	}
	
	/**
	 * 零件基准值计算详细信息
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param resp 页面响应
	 * @param conn 数据库会话
	 * @throws Exception 抛出异常
	 * @author lxb
	 */
	public void showPartialDetail(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse resp,SqlSession conn)throws Exception{
		log.info("PartialBaseLineValueAction.showPartialDetail start");
		//对Ajax的响应
		Map<String, Object> listResponse=new HashMap<String,Object>();
		//错误信息集合
		List<MsgInfo> errors=new ArrayList<MsgInfo>();
		PartialBaseLineValueService service=new PartialBaseLineValueService();
		
		PartialBaseLineValueForm returnBaseValue = service.secrchPartialBaseValue(form, conn);//零件基准值
		
		Integer supplyQuantuty=service.getSupplyQuantityOfCycle(form,conn);//采样周期内 补充量合计 	
		
		PartialBaseLineValueForm returnHalfYearQuantity=service.getTotalQuantityOfCycle(form, conn, 6);//半年
		
		PartialBaseLineValueForm returnThreeMonthQuantity=service.getTotalQuantityOfCycle(form, conn, 3);//三个月
		
		PartialBaseLineValueForm retutnCurMonthQuantity=service.getgetAverageQuantityOfCruMon(form, conn);//当月
		
		BigDecimal notStandCountOfHalfYear=service.getAverageQuantityOfNonStandardCycle(form, conn, 6);//半年非标使用
		 
		BigDecimal notStandCountOfThreeMon=service.getAverageQuantityOfNonStandardCycle(form, conn, 3);//三个月非标使用
		
		BigDecimal notStandCountOfCurMon=service.getAverageQuantityOfNonStandardCycleOfCurMonth(form, conn);
		
		Integer notSafetyCount=service.getNonBomSaftyCount(form, conn);//设定非标使用量
		
		Integer totalOfStandardUse=service.getTotalOfStandardUse(form,conn);//标配拉动数合计
		
		List<PartialBaseLineValueForm> returnForms=service.searchForecastSettingAndCountOfSnandard(form,conn);//按照拉动台数计算标准使用量
		
		Map<String,Object> returnMap=service.getChart(form, conn);//图表
		
		listResponse.put("errors", errors);
		listResponse.put("returnBaseValue", returnBaseValue);
		listResponse.put("supplyQuantuty", supplyQuantuty);
		listResponse.put("returnHalfYearQuantity", returnHalfYearQuantity);
		listResponse.put("returnThreeMonthQuantity", returnThreeMonthQuantity);
		listResponse.put("retutnCurMonthQuantity", retutnCurMonthQuantity);
		listResponse.put("notStandCountOfHalfYear", notStandCountOfHalfYear);
		listResponse.put("notStandCountOfThreeMon", notStandCountOfThreeMon);
		listResponse.put("notStandCountOfCurMon", notStandCountOfCurMon);
		
		listResponse.put("notSafetyCount", notSafetyCount);
		listResponse.put("totalOfStandardUse", totalOfStandardUse);
		listResponse.put("returnForms", returnForms);
		listResponse.put("returnMap", returnMap);
		//返回Json格式响应信息
		returnJsonResponse(resp, listResponse);
		
		log.info("PartialBaseLineValueAction.showPartialDetail end");
	}

}

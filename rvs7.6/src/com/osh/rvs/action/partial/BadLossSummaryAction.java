package com.osh.rvs.action.partial;

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

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.partial.BadLossSummaryEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.BadLossSummaryForm;
import com.osh.rvs.service.partial.BadLossSummaryService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class BadLossSummaryAction extends BaseAction {
   private Logger log = Logger.getLogger(getClass());
   
   private BadLossSummaryService service = new BadLossSummaryService();
   public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
	   log.info("BadLossSummaryAction.init start");
	   
	   // 取得登录用户权限
	   LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
	   List<Integer> privacies = user.getPrivacies();
	   
	   // 损金(零件管理员+计划员)
	   if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)
				 || privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
		   request.setAttribute("loss", "isTrue");
	   } else {
		   request.setAttribute("loss", "isFalse");
	   }
	   
	   String workPeriod =request.getParameter("period");
	   if(CommonStringUtil.isEmpty(workPeriod)){
		   //财年
		   Calendar calendar = Calendar.getInstance(); 
		   workPeriod = RvsUtils.getBussinessYearString(calendar);//页面初始化财年
	   }
	  
	   request.setAttribute("work_period",workPeriod);
	   
	   List<String> listOtherPeriods = service.getOtherPeriods(conn, workPeriod);
	   request.setAttribute("listOtherPeriods",listOtherPeriods);
	   
	   actionForward = mapping.findForward(FW_INIT);	   
	   
	   log.info("BadLossSummaryAction.init end");
   }

   /**
    *画面初始化
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @param conn
    */
   public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
	   log.info("BadLossSummaryAction.search start");
	   Map<String,Object>listResponse=new HashMap<String,Object>();
	   List<MsgInfo> errors = new ArrayList<MsgInfo>();
	   
	   String period = request.getParameter("period");
	   
	   Calendar calendar = Calendar.getInstance(); 
	   String curWorkPeriod = RvsUtils.getBussinessYearString(calendar);//当年财年
	   List<BadLossSummaryForm> badLossSummaryForms =null;
	   
	   if(CommonStringUtil.isEmpty(period) || curWorkPeriod.equals(period)){//当年
		   badLossSummaryForms = service.searchLossSummary(conn, errors);
	   }else{//其他财年
		   badLossSummaryForms = service.searchOtherPeriodsLossSummary(conn, errors, period);
	   }
	   
	   listResponse.put("badLossSummaryForms", badLossSummaryForms);
	   
	   //将查询的结果放在request中的session中---等下次需要导出时使用
	   request.getSession().setAttribute("lossSummaryResult", badLossSummaryForms);
	   listResponse.put("errors", errors);
	   returnJsonResponse(response, listResponse);
	   log.info("BadLossSummaryAction.search end");
   }
   /**
    *查看按钮事件
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @param conn
    */
   public void searchMonth(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
	   log.info("BadLossSummaryAction.searchMonth start");
	   Map<String,Object>listResponse=new HashMap<String,Object>();
	   List<MsgInfo> errors = new ArrayList<MsgInfo>();
	   BadLossSummaryForm badLossSummaryForm = (BadLossSummaryForm)form;
	   BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();
	   BeanUtil.copyToBean(badLossSummaryForm, badLossSummaryEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
	   
	   List<BadLossSummaryForm> badLossSummaryForms = service.searchLossSummary(conn,badLossSummaryEntity,errors);
	   
	   listResponse.put("badLossSummaryForms", badLossSummaryForms);
	   
	   //将查询的结果放在request中的session中---等下次需要导出时使用
	   request.getSession().setAttribute("lossSummaryResult", badLossSummaryForms);
	   listResponse.put("errors", errors);
	   returnJsonResponse(response, listResponse);
	   log.info("BadLossSummaryAction.searchMonth end");
   }

	/**
	 * 根据财年月份查询出某一月的详细数据
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void searchofmonth(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("BadLossSummaryAction.searchofmonth start");
		Map<String,Object> listResponse = new HashMap<String,Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		BadLossSummaryForm summaryForm = (BadLossSummaryForm)form;

		BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();

		BeanUtil.copyToBean(summaryForm, badLossSummaryEntity,CopyOptions.COPYOPTIONS_NOEMPTY);

	   BadLossSummaryForm badLossSummaryForm = service.searchLossSummaryOfMonth(badLossSummaryEntity, conn, errors);
	
	   listResponse.put("badLossSummaryForm", badLossSummaryForm);
	   listResponse.put("errors", errors);
	   returnJsonResponse(response, listResponse);
	
	   log.info("BadLossSummaryAction.searchofmonth end");
	}
   /**
    * 更新损金
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @param conn
    * @throws Exception
    */
   public void doupdate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
	   log.info("BadLossSummaryAction.doupdate start");
	   Map<String,Object>listResponse=new HashMap<String,Object>();
	   List<MsgInfo> errors = new ArrayList<MsgInfo>();
	   
	   //更新损金
	    service.setMonthData(request.getParameter("choose_month"),request.getParameterMap(),conn,errors);
	   
	   listResponse.put("errors", errors);
	   returnJsonResponse(response, listResponse);
	   log.info("BadLossSummaryAction.doupdate end");
   }
   /**
    * 更新备注信息
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @param conn
    * @throws Exception
    */
   public void doupdateofcomment(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
	   log.info("BadLossSummaryAction.doupdateofcomment start");
	   Map<String,Object>listResponse=new HashMap<String,Object>();
	   List<MsgInfo> errors = new ArrayList<MsgInfo>();
	   
	   BadLossSummaryForm badLossSummaryForm = (BadLossSummaryForm)form;
	   BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();
	   
	   //设置财年 中的年份
	   String year = CodeListUtils.getKeyByValue("loss_work_period",badLossSummaryForm.getWork_period(),"");
	   badLossSummaryForm.setYear(year);
	   
	   BeanUtil.copyToBean(badLossSummaryForm, badLossSummaryEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
	   //更新备注
	   service.updateComment(badLossSummaryEntity,conn);
	   
	   listResponse.put("errors", errors);
	   returnJsonResponse(response, listResponse);
	   log.info("BadLossSummaryAction.doupdateofcomment end");
   }
   /**
    * 导出
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @param conn
    * @throws Exception
    */
   public void report(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
	   log.info("BadLossSummaryAction.report start");
	   Map<String, Object> listResponse = new HashMap<String, Object>();
	   List<MsgInfo> errors = new ArrayList<MsgInfo>();
	  
	   BadLossSummaryForm badLossSummaryForm = (BadLossSummaryForm)form;
	   BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();
	   BeanUtil.copyToBean(badLossSummaryForm, badLossSummaryEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
	   
	   String workPeriodMonth = badLossSummaryForm.getWork_period()+badLossSummaryForm.getMonth();
	   String fileName =workPeriodMonth+"不良损金汇总.xls";
	   
	   String filePath = service.createLossSummaryReport(conn,badLossSummaryEntity,errors);
	   
	   listResponse.put("fileName", fileName);
	   listResponse.put("filePath", filePath);
			
	   // 检查发生错误时报告错误信息
	   listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
	   log.info("BadLossSummaryAction.report end");
   }
   /**
    * 更新汇总表的结算汇率
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @param conn
    * @throws Exception
    */
   public void doupdateSettlement(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
	   log.info("BadLossSummaryAction.doupdateSettlement start");
	   Map<String, Object> listResponse = new HashMap<String, Object>();
	   List<MsgInfo> errors = new ArrayList<MsgInfo>();
	   
	   BadLossSummaryForm badLossSummarForm = (BadLossSummaryForm)form;
	   BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();
	   
	   BeanUtil.copyToBean(badLossSummarForm, badLossSummaryEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
	   
	   //更新结算汇率
	   service.updateLossSummaryOfSettlement(badLossSummaryEntity, conn);
	   
	   // 检查发生错误时报告错误信息
	   listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
	   log.info("BadLossSummaryAction.doupdateSettlement end");
   }
   
   /**
    * 更新结算汇率后，损金总计跟着改变
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @param conn
    * @throws Exception
    */
   public void searchOneData(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
	   log.info("BadLossSummaryAction.searchOneData start");
	   Map<String, Object> listResponse = new HashMap<String, Object>();
	   List<MsgInfo> errors = new ArrayList<MsgInfo>();
	   
	   BadLossSummaryForm badLossSummarForm = (BadLossSummaryForm)form;
	   BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();
	   
	   BeanUtil.copyToBean(badLossSummarForm, badLossSummaryEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
	   
	   //取出当前修改汇率下的所有损金总计
	   BadLossSummaryForm lossPriceEntity =service.searchOneData(conn, badLossSummaryEntity, errors);
	   
	   listResponse.put("lossPrice", lossPriceEntity.getLoss_price());
	   // 检查发生错误时报告错误信息
	   listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
	   log.info("BadLossSummaryAction.searchOneData end");
   }
     
}

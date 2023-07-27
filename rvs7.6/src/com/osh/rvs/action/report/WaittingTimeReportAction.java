package com.osh.rvs.action.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.report.WaittingTimeReportService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 等待时间、中断时间统计
 * 
 * @author liuxb
 * 
 */
public class WaittingTimeReportAction extends BaseAction {
	
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * 页面初始化
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("WaittingTimeReportAction.init start");
		
		//机种
		CategoryService categoryService = new CategoryService();
		String sCategory = categoryService.getOptions(conn);
		req.setAttribute("sCategory", sCategory);
		
		//型号
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);
		
		//等级
		String sLevel = CodeListUtils.getSelectOptions("material_level_heavy", null, "");
		req.setAttribute("sLevel", sLevel);
		
		//课室
		SectionService sectionService = new SectionService();
		String sSection =  sectionService.getOptions(conn,"",null);
		req.setAttribute("sSection", sSection);
		
		//零件BO
		String sBoflg = CodeListUtils.getSelectOptions("bo_flg", null, "");
		req.setAttribute("sBoflg", sBoflg);

		//直送
		String sDirect = CodeListUtils.getSelectOptions("material_direct", null, "");
		req.setAttribute("sDirect", sDirect);
		
		//分线
		String px = CodeListUtils.getSelectOptions("operator_px", null, "");
		req.setAttribute("px", px);
		
		//分线
		String mpx = CodeListUtils.getSelectOptions("material_px_history", null, "");
		req.setAttribute("mpx", mpx);
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("WaittingTimeReportAction.init end");
	}

	/**
	 * 页面初始化
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	
	public void bold(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("WaittingTimeReportAction.bold start");
		
		// 机种
		CategoryService categoryService = new CategoryService();
		String sCategory = categoryService.getOptions(conn);
		req.setAttribute("sCategory", sCategory);

		// 型号
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		// 等级
		String sLevel = CodeListUtils.getSelectOptions("material_level_inline", null, "");
		req.setAttribute("sLevel", sLevel);

		// 课室
		SectionService sectionService = new SectionService();
		String sSection = sectionService.getOptions(conn, "", null);
		req.setAttribute("sSection", sSection);
		
		// 迁移到页面
		actionForward = mapping.findForward("bold");
		
		log.info("WaittingTimeReportAction.bold end");
	}

	/**
	 * 查询
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("WaittingTimeReportAction.search start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		errors = v.validate();
		
		if(errors.size() == 0){
			WaittingTimeReportService service = new WaittingTimeReportService();
			service.search(form, listResponse, req,conn);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("WaittingTimeReportAction.search end");
	}


	/**
	 * 查询 IDs
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchIds(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("WaittingTimeReportAction.search start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		errors = v.validate();
		
		if(errors.size() == 0){
			WaittingTimeReportService service = new WaittingTimeReportService();
			service.searchIds(form, listResponse, req,conn);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("WaittingTimeReportAction.search end");
	}

	/**
	 * 导出
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void export(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("WorkTimeAnalysisAction.export start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();
		
		WaittingTimeReportService service = new WaittingTimeReportService();
		String fileName ="等待时间、中断时间统计.xls";
		String filePath = service.createExcel(form,req,conn);
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("WorkTimeAnalysisAction.export end");
	}
	
	
	/**
	 * 导出
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void exportBold(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("WorkTimeAnalysisAction.exportBOLD start");

		// Ajax回馈对象
		Map<String, Object> cbResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();

		WaittingTimeReportService service = new WaittingTimeReportService();
		String lt_type = req.getParameter("lt_type");
		String fileName = "BOLD_修理时间点统计.xlsx";
		if ("8".equals(lt_type)) {
			fileName = "BOLD_修理时间点统计(8小时LT).xlsx";
		}
		String filePath = service.createBoldExcel(req, lt_type, conn);

		cbResponse.put("fileName", fileName);
		cbResponse.put("filePath", filePath);

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		log.info("WorkTimeAnalysisAction.exportBOLD end");
	}

	
}

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
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.report.WorkTimeAnalysisService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Title WorkTimeAnalysisAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.report
 * @ClassName: WorkTimeAnalysisAction
 * @Description: 工时分析
 * @author liuxb
 * @date 2016-10-8 下午1:40:33
 */
public class WorkTimeAnalysisAction extends BaseAction {
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
		log.info("WorkTimeAnalysisAction.init start");

		// 机种
		CategoryService categoryService = new CategoryService();
		String sCategory = categoryService.getOptions(conn);
		req.setAttribute("sCategory", sCategory);

		// 型号
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		// 等级
		String sLevel = CodeListUtils.getSelectOptions("material_level_group", null, "");
		req.setAttribute("sLevel", sLevel);

		// 课室
		SectionService sectionService = new SectionService();
		String sSection = sectionService.getAllOptions(conn);
		req.setAttribute("sSection", sSection);

		// 工位
		PositionService positionService = new PositionService();
		String pReferChooser = positionService.getOptions(conn);
		req.setAttribute("pReferChooser", pReferChooser);

		// 人员
		OperatorService operatorService = new OperatorService();
		String oReferChooser = operatorService.getAllOperatorName(conn);
		req.setAttribute("oReferChooser", oReferChooser);

		// 责任工程
		LineService lineService = new LineService();
		String sLine = lineService.getOptions(conn, "", null);
		req.setAttribute("sLine", sLine);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("WorkTimeAnalysisAction.init end");
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("WorkTimeAnalysisAction.search start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();

		WorkTimeAnalysisService workTimeAnalysisService = new WorkTimeAnalysisService();
		workTimeAnalysisService.validDate(form, errors);

		if (errors.size() == 0) {
			workTimeAnalysisService.searchWorkTime(form, listResponse, conn);
		}
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("WorkTimeAnalysisAction.search end");
	}

	/**
	 * 
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

		WorkTimeAnalysisService workTimeAnalysisService = new WorkTimeAnalysisService();
		String fileName = "工时分析.xls";
		String filePath = workTimeAnalysisService.createExcel(form);

		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("WorkTimeAnalysisAction.export end");
	}

	/***
	 * 异常工时数据导出
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void anomalyExport(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("WorkTimeAnalysisAction.anomalyExport start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();

		Validators v = BeanUtil.createBeanValidators(form,
				BeanUtil.CHECK_TYPE_ALL);
		v.add("finish_time_start", v.required("作业开始时间"));
		v.add("finish_time_end", v.required("作业结束时间"));

		errors = v.validate();

		if (errors.size() == 0) {
			WorkTimeAnalysisService workTimeAnalysisService = new WorkTimeAnalysisService();

			String fileName = "异常工时数据.xls";
			String filePath = workTimeAnalysisService.createAnomalyExcel(form, conn, errors);

			listResponse.put("fileName", fileName);
			listResponse.put("filePath", filePath);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("WorkTimeAnalysisAction.anomalyExport end");
	}

}

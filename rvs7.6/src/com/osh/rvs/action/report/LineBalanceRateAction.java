package com.osh.rvs.action.report;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

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

import com.osh.rvs.form.report.LineBalanceRateForm;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.ProcessAssignService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.report.LineBalanceRateService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Title LineBalanceRateAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.report
 * @ClassName: LineBalanceRateAction
 * @Description: 生产效率
 * @author houp
 * @date 2016-10-9 下午1:40:33
 */
public class LineBalanceRateAction extends BaseAction {
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
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("LineBalanceRateAction.init start");

		//机种
		CategoryService categoryService = new CategoryService();
		String sCategory = categoryService.getOptions(conn);
		req.setAttribute("sCategory", sCategory);

		//型号
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		//等级
		String sLevel = CodeListUtils.getSelectOptions("material_level_group", null, "");
		req.setAttribute("sLevel", sLevel);
		
		//课室
		SectionService sectionService = new SectionService();
		String sSection =  sectionService.getOptions(conn,"",null);
		req.setAttribute("sSection", sSection);
		
		// 责任工程
		LineService lineService = new LineService();
		String sLine = lineService.getInlineOptions(conn);
		req.setAttribute("sLine", sLine);
		
		// 分线
		String px = CodeListUtils.getSelectOptions("material_px_history", null, "");
		req.setAttribute("px", px);

		// 流程
		ProcessAssignService paService = new ProcessAssignService();
		req.setAttribute("paOptions", paService.getAllGroupOptions(null, conn));

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("LineBalanceRateAction.init end");
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
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("LineBalanceRateAction.search start");

		LineBalanceRateForm searchForm = (LineBalanceRateForm)form;
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(searchForm, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("section_id", v.required());
		v.add("line_id", v.required());
		List<MsgInfo> errors = v.validate();

		if (isEmpty(searchForm.getCategory_id()) && isEmpty(searchForm.getModel_id()) 
				&& isEmpty(searchForm.getPx()) && isEmpty(searchForm.getProcess_codes()) ) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("category_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "机种、型号、分线或者流程工位"));
			errors.add(error);
		}

		// validate process_codes TODO

		LineBalanceRateService service = new LineBalanceRateService();
		service.validDate(searchForm, errors);

		if (errors.size() == 0) {
			service.searchChatData(searchForm, listResponse, conn);
		}
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LineBalanceRateAction.search end");
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
		log.info("LineBalanceRateAction.export start");

		LineBalanceRateForm exportForm = (LineBalanceRateForm)form;
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();
		
		LineBalanceRateService service = new LineBalanceRateService();
		String fileName ="流水线平衡率分析.xls";
		String filePath = service.createExcel(exportForm);
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("LineBalanceRateAction.export end");
	}
}

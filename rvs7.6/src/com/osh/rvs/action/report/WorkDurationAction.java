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

import com.osh.rvs.service.LineService;
import com.osh.rvs.service.OperatorProductionService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.inline.FoundryService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 作业时间分析
 * @author gonglm
 *
 */
public class WorkDurationAction extends BaseAction{
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
		log.info("WorkDurationAction.init start");
		
		//课室
		SectionService sectionService = new SectionService();
		String sSection =  sectionService.getOptions(conn,"",null);
		req.setAttribute("sSection", sSection);
		
		//工程
		LineService lineService = new LineService();
		String sLine =  lineService.getInlineOptions(conn);
		req.setAttribute("sLine", sLine);
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("WorkDurationAction.init end");
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
		log.info("WorkDurationAction.search start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		errors = v.validate();
		
		if(errors.size() == 0){
			OperatorProductionService service = new OperatorProductionService();
			service.getOperatorFeatures(form, listResponse, conn);

			FoundryService fService = new FoundryService();
			fService.getLineFoundryOfDate(form, listResponse, conn);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("WorkDurationAction.search end");
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
		log.info("WorkDurationAction.export start");
		
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("WorkDurationAction.export end");
	}
	
}

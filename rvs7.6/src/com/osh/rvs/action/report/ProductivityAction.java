package com.osh.rvs.action.report;

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
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.report.ProductivityForm;
import com.osh.rvs.service.report.ProductivityService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Title ProductivityAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.report
 * @ClassName: ProductivityAction
 * @Description: 生产效率
 * @author houp
 * @date 2016-10-9 下午1:40:33
 */
public class ProductivityAction extends BaseAction {
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
		log.info("ProductivityAction.init start");
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("ProductivityAction.init end");
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
		log.info("ProductivityAction.search start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();

		ProductivityForm searchForm = (ProductivityForm)form;
		ProductivityService service = new ProductivityService();
		service.validDate(searchForm, errors);

		if (errors.size() == 0) {
			service.searchChatData(searchForm, listResponse, conn);
		}
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ProductivityAction.search end");
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
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ProductivityAction.doUpdate start");

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		ProductivityForm inputForm = (ProductivityForm)form;
		Map<String, Object> listResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(inputForm, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("avalible_productive", v.required());
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			ProductivityService service = new ProductivityService();
			service.update(inputForm, user, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ProductivityAction.doUpdate end");
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
		log.info("ProductivityAction.export start");

		ProductivityForm exportForm = (ProductivityForm)form;
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<>();
		
		ProductivityService service = new ProductivityService();
		String fileName ="生产效率.xls";
		String filePath = service.createExcel(exportForm);
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("ProductivityAction.export end");
	}
}

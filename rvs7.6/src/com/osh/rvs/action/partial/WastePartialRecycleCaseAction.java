package com.osh.rvs.action.partial;

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

import com.osh.rvs.form.partial.WastePartialRecycleCaseForm;
import com.osh.rvs.service.partial.WastePartialRecycleCaseService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 废弃零件回收箱
 * 
 * @Description
 * @author dell
 * @date 2019-12-27 上午10:26:35
 */
public class WastePartialRecycleCaseAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	/**
	 * 检索
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("WastePartialRecycleCaseAction.search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		WastePartialRecycleCaseService service = new WastePartialRecycleCaseService();
		List<WastePartialRecycleCaseForm> list = service.search(form, conn);
		listResponse.put("finished", list);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("WastePartialRecycleCaseAction.search end");
	}

	/**
	 * 新建记录
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("WastePartialRecycleCaseAction.doInsert start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("case_id");
		errors = v.validate();

		if (errors.size() == 0) {
			WastePartialRecycleCaseService service = new WastePartialRecycleCaseService();
			service.insert(form, conn, errors);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("WastePartialRecycleCaseAction.doInsert end");
	}

	/**
	 * 更新
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("WastePartialRecycleCaseAction.doUpdate start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("case_id", v.required("回收箱ID"));
		v.add("case_code", v.required("装箱编号"));
		errors = v.validate();

		if (errors.size() == 0) {
			WastePartialRecycleCaseService service = new WastePartialRecycleCaseService();
			service.update(form, conn,errors);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("WastePartialRecycleCaseAction.doUpdate end");
	}

	/**
	 * 更新重量
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdateWeight(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("WastePartialRecycleCaseAction.doUpdateWeight start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("case_id", v.required("回收箱ID"));
		errors = v.validate();

		if (errors.size() == 0) {
			WastePartialRecycleCaseService service = new WastePartialRecycleCaseService();
			service.updateWeight(form, conn);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("WastePartialRecycleCaseAction.doUpdateWeight end");
	}

	/**
	 * 打包
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doPackage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("WastePartialRecycleCaseAction.doPackage start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.only("case_id");
		errors = v.validate();

		if (errors.size() == 0) {
			WastePartialRecycleCaseService service = new WastePartialRecycleCaseService();
			WastePartialRecycleCaseForm wastePartialRecycleCaseForm = (WastePartialRecycleCaseForm) form;

			service.updatePackageDate(wastePartialRecycleCaseForm.getCase_id(), conn);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("WastePartialRecycleCaseAction.doPackage end");
	}

	/**
	 * 废弃
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doWaste(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("WastePartialRecycleCaseAction.doPackage start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("case_id", v.required("回收箱ID"));
		v.add("weight", v.required("重量"));
		v.add("waste_apply_date", v.required("废弃申请日期"));
		errors = v.validate();

		if (errors.size() == 0) {
			WastePartialRecycleCaseService service = new WastePartialRecycleCaseService();
			service.waste(form, conn);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("WastePartialRecycleCaseAction.doPackage end");
	}

}

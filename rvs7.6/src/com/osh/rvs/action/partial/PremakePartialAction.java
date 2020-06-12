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

import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.PremakePartialForm;
import com.osh.rvs.service.PartialService;
import com.osh.rvs.service.partial.PremakePartialService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 *
 * @Title PremakePartialAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.partial
 * @ClassName: PremakePartialAction
 * @Description: 零件预制
 * @author lxb
 * @date 2016-3-24 下午3:48:09
 */
public class PremakePartialAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PremakePartialAction.init start");

		String mReferChooser = CodeListUtils.getSelectOptions(RvsUtils.getSnoutModels(conn), null, "", false);
		request.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合

		actionForward = mapping.findForward(FW_INIT);

		log.info("PremakePartialAction.init end");
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
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,SqlSession conn) throws Exception {
		log.info("PremakePartialAction.search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PremakePartialService service = new PremakePartialService();
		List<PremakePartialForm>  finished = service.search(form, conn);

		listResponse.put("finished", finished);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("PremakePartialAction.search end");
	}

	/**
	 * 更新
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,SqlSessionManager conn) throws Exception {
		log.info("PremakePartialAction.doupdate start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if(errors.size()==0){
			PremakePartialService service = new PremakePartialService();
			service.update(form, conn);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("PremakePartialAction.doupdate end");
	}

	/**
	 * 零件编码自动检索零件ID
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getAutocomplete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("PremakePartialAction.getAutocomplete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PartialService service = new PartialService();
		List<Map<String, String>> list = service.getPartialAutoCompletes(request.getParameter("code"), conn);
		listResponse.put("sPartialCode", list);// 零件集合

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("PremakePartialAction.getAutocomplete end");
	}

	/**
	 *  新建零件预制
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("PremakePartialAction.doInsert start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.only("code","model_id","quantity","standard_flg");
		List<MsgInfo> errors = v.validate();

		if(errors.size()==0){
			PremakePartialService service = new PremakePartialService();
			service.insert(form, errors, conn);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("PremakePartialAction.doInsert end");
	}

}

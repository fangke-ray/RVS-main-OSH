package com.osh.rvs.action.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.master.StandardPartialAdditionEntity;
import com.osh.rvs.form.master.StandardPartialAdditionForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.StandardWorkTimeService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class StandardPartialAdditionAction extends BaseAction {

	StandardWorkTimeService service = new StandardWorkTimeService();

	public void init(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSession conn)
			throws Exception {

		log.info("StandardPartialAdditionAction.init start");

		PositionService positionService = new PositionService();
		req.setAttribute("pReferChooser", positionService.getOptions(conn));

		ModelService modelService = new ModelService();
		req.setAttribute("mReferChooser", modelService.getOptions(conn));

		actionForward = mapping.findForward(FW_INIT);

		log.info("StandardPartialAdditionAction.init end");
	}

	/**
	 * 零件使用标准工时补正一览查询
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("StandardPartialAdditionAction.search start");
		StandardPartialAdditionForm pisForm = (StandardPartialAdditionForm) form;

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		StandardPartialAdditionEntity condition = new StandardPartialAdditionEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(pisForm, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 查询 */
		List<StandardPartialAdditionForm> list = service.searchPartialAddition(condition, conn);

		listResponse.put("list", list);
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("StandardPartialAdditionAction.search end");
	}

	/**
	 * 取得工位的可用零件一览
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getEditForPosition(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("StandardPartialAdditionAction.getEditForPosition start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		StandardPartialAdditionForm pisForm = (StandardPartialAdditionForm) form;
		String position_id = pisForm.getPosition_id();

		/* 查询 */
		List<StandardPartialAdditionEntity> list = service.getEditForPosition(position_id, conn);

		listResponse.put("storageList", list);

		PositionService pService = new PositionService();
		listResponse.put("process_code", 
					pService.getPositionEntityByKey(position_id, conn).getProcess_code());

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("StandardPartialAdditionAction.getEditForPosition end");
	}

	/**
	 * 取得型号的BOM零件一览
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getEditForModel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("StandardPartialAdditionAction.getEditForModel start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		StandardPartialAdditionForm pisForm = (StandardPartialAdditionForm) form;

		/* 查询 */
		List<StandardPartialAdditionEntity> list = service.getEditForModel(pisForm.getModel_id(), conn);

		listResponse.put("storageList", list);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("StandardPartialAdditionAction.getEditForModel end");
	}

	/**
	 * 批量设置
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("StandardPartialAdditionAction.doUpdate start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		service.updatePatch(request.getParameterMap(), conn);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("StandardPartialAdditionAction.doUpdate end");
	}

	/**
	 * 从一览删除设置
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("StandardPartialAdditionAction.doDelete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		service.delete(form, conn);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("StandardPartialAdditionAction.doDelete end");
	}
}

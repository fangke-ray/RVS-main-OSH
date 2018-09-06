package com.osh.rvs.action.data;

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

import com.jacob.com.ComThread;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.XlsUtil;
import com.osh.rvs.form.master.ModelForm;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.ModelService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PcsTemplateAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());	

	/**
	 * 标准工时画面显示
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PcsTemplateAction.init start");		

		// 测试Jacob
		String jacobStatus = "OK";

		try {
			XlsUtil xls = new XlsUtil(PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "//周报Appedix.xls");
			xls.CloseExcel(false);
			xls.Release();
		} catch (Exception e) {
			jacobStatus = "NG" + e.getMessage();
			try {
				ComThread.Release();
			} catch (Exception e1) {
			}
		}

		req.setAttribute("jacob", jacobStatus);

		actionForward = mapping.findForward(FW_INIT);	
		ModelService service = new ModelService();
		String mReferChooser = service.getOptions(conn);
		/*型号*/
		req.setAttribute("mReferChooser", mReferChooser);	

		log.info("PcsTemplateAction.init end");
	}

	/**
	 * 工程检查票参考
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void refer(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("PcsTemplateAction.refer start");
	
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>(); 
		MaterialService service = new MaterialService();			;

		//检查前台的型号是否为空
		String modelId = req.getParameter("model_id");
		String modelName = null;
		if (isEmpty(modelId)) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "型号"));
			errors.add(error);
		} else {
			ModelService mservice = new ModelService();
			ModelForm mform = mservice.getDetail(modelId, conn);
			if (mform == null) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("model_id");
				error.setErrcode("dbaccess.recordNotExist");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "型号"));
				errors.add(error);
			} else {
				modelName = mform.getName();
			}
		}

		if (errors.size() == 0) {
			service.getPcsesBlank(callbackResponse, modelName, conn);
		}

		callbackResponse.put("errors", errors);

		returnJsonResponse(res, callbackResponse);
		log.info("PcsTemplateAction.refer end");
	}

	public void makeTemplateFiles(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("PcsTemplateAction.makeTemplateFiles start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>(); 

		callbackResponse.put("errors", errors);

		//检查前台的型号是否为空
		String modelId = req.getParameter("model_id");
		String modelName = null;
		if (isEmpty(modelId)) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "型号"));
			errors.add(error);
		} else {
			ModelService mservice = new ModelService();
			ModelForm mform = mservice.getDetail(modelId, conn);
			if (mform == null) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("model_id");
				error.setErrcode("dbaccess.recordNotExist");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "型号"));
				errors.add(error);
			} else {
				modelName = mform.getName();
			}
		}

		MaterialService service = new MaterialService();			;

		callbackResponse.put("tempFile", service.getPcsesBlankXls(modelName, conn));

		returnJsonResponse(res, callbackResponse);
		log.info("PcsTemplateAction.makeTemplateFiles end");
	}
	
}

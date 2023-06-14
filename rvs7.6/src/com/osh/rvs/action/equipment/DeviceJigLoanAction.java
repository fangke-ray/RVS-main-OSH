package com.osh.rvs.action.equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.equipment.DeviceJigLoanEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.equipment.DeviceJigLoanForm;
import com.osh.rvs.service.equipment.DeviceJigLoanService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class DeviceJigLoanAction extends BaseAction {

	/**
	 * 初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigLoanAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("DeviceJigLoanAction.init end");
	}

	/**
	 * 详细页面数据
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getRentListdata(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigLoanAction.showDetail start");

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeviceJigLoanService service = new DeviceJigLoanService();
		cbResponse.put("rentListdata", service.getLoanedOfUser(user, 1, conn));

		cbResponse.put("errors", errors);

		returnJsonResponse(res, cbResponse);
		log.info("DeviceJigLoanAction.showDetail end");
	}

	public void doLoan(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("DeviceJigLoanAction.doLoan start");

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeviceJigLoanService service = new DeviceJigLoanService();
		service.loan(req.getParameter("manage_id") , user, conn);

		session.setAttribute("DJ_LOANING", "true");

		cbResponse.put("errors", errors);

		returnJsonResponse(res, cbResponse);
		log.info("DeviceJigLoanAction.doLoan end");
	}

	public void doRevent(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("DeviceJigLoanAction.doRevent start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeviceJigLoanService service = new DeviceJigLoanService();
		service.revert(req.getParameter("device_jig_loan_key") ,conn);

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		service.checkLoaningNow(user.getOperator_id(), session, conn);

		cbResponse.put("errors", errors);

		returnJsonResponse(res, cbResponse);
		log.info("DeviceJigLoanAction.doRevent end");
	}

	public void getMore(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigLoanAction.getMore start");

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeviceJigLoanService service = new DeviceJigLoanService();
		cbResponse.put("moreListdata", service.getMoreOfUser(user, conn));

		cbResponse.put("errors", errors);

		returnJsonResponse(res, cbResponse);
		log.info("DeviceJigLoanAction.getMore end");
	}

	public void detailMaterial(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("DeviceJigLoanAction.detailMaterial start");

		DeviceJigLoanService service = new DeviceJigLoanService();

		HttpSession session = req.getSession();

		Object oBean = session.getAttribute("materialDetail");
		if (oBean != null) {
			
			List<DeviceJigLoanEntity> list = service.getLoanApplyTraceByMaterial(((MaterialEntity) oBean).getMaterial_id() ,
					null, null, conn);
			List<DeviceJigLoanForm> listdata = new ArrayList<DeviceJigLoanForm>();
			CopyOptions cos = new CopyOptions();
			cos.excludeNull();
			cos.include("object_type", "type_name", "manage_code", "process_code", "operator_name", "on_loan_time", "revent_time");

			BeanUtil.copyToFormList(list, listdata, cos, DeviceJigLoanForm.class);

			req.setAttribute("listdata", JSON.encode(listdata));
		}

		// 迁移到页面
		actionForward = mapping.findForward("detail_of_material");

		log.info("DeviceJigLoanAction.detailMaterial end");
	}
}

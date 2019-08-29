package com.osh.rvs.action.qf;

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
import com.osh.rvs.bean.qf.TurnoverCaseEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.qf.TurnoverCaseForm;
import com.osh.rvs.service.qf.FactMaterialService;
import com.osh.rvs.service.qf.TurnoverCaseService;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.ModelService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class TurnoverCaseAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	private ModelService modelService = new ModelService();

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
	@Privacies(permit = { 1, 0 })
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("TurnoverCaseAction.init start");

		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);
		req.setAttribute("boundOutOcmOptions", CodeListUtils.getSelectOptions("material_direct_ocm", null, "(全部)"));
		// 设定等级文字
		req.setAttribute("lOptions", CodeListUtils.getGridOptions("material_level"));
		// 设定发送地文字
		req.setAttribute("ocmOptions", CodeListUtils.getGridOptions("material_direct_ocm"));

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		if (privacies.contains(RvsConsts.PRIVACY_RECEPT_FACT)  // TODO quotationer
				|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT) 
				|| privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			req.setAttribute("editor", "true");
		} else {
			req.setAttribute("editor", "false");
		}

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("TurnoverCaseAction.init end");
	}

	/**
	 * 通箱库位条件查询
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 100, 0 })
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("TurnoverCaseAction.search start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			TurnoverCaseService service = new TurnoverCaseService();
			// 执行检索
			List<TurnoverCaseForm> lResultForm = service.searchTurnoverCase(form, conn);
			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("TurnoverCaseAction.search end");
	}

	@Privacies(permit = { 100, 0 })
	public void getStoargeEmpty(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("TurnoverCaseAction.getStoargeEmpty start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		// 执行检索
		TurnoverCaseService service = new TurnoverCaseService();
		List<String> wipHeaped = service.getStorageHeaped(form, conn);

		// 查询结果放入Ajax响应对象
		calbackResponse.put("heaps", wipHeaped);

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.getStoargeEmpty end");
	}

	@Privacies(permit = { 101, 109, 0 })
	public void doChangeLocation(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		log.info("TurnoverCaseAction.doChangeLocation start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		// 执行检索
		TurnoverCaseService service = new TurnoverCaseService();
		service.changelocation(conn, req.getParameter("material_id"), req.getParameter("location"));

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.doChangeLocation end");
	}

	@Privacies(permit = { 101, 109, 0 })
	public void doWarehousing(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		log.info("TurnoverCaseAction.doWarehousing start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		// 执行检索
		TurnoverCaseService service = new TurnoverCaseService();
		String location = req.getParameter("location");
		service.warehousing(conn, location);
		TurnoverCaseEntity lEntity = service.getEntityByLocation(location, conn);

		// 记录入库作业
		FactMaterialService fmsService = new FactMaterialService();
		fmsService.insertFactMaterial(user.getOperator_id(), lEntity.getMaterial_id(), 1, conn);

		// 刷新完成数量
		conn.commit();
		AcceptFactService afService = new AcceptFactService();
		afService.fingerOperatorRefresh(user.getOperator_id());

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.doWarehousing end");
	}

	@Privacies(permit = { 101, 109, 0 })
	public void doWarehousingPlanned(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		log.info("TurnoverCaseAction.doWarehousingPlanned start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		// 执行检索
		TurnoverCaseService service = new TurnoverCaseService();
		List<String> locations = service.warehousing(conn, req.getParameterMap());

		// 记录入库作业
		FactMaterialService fmsService = new FactMaterialService();
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		int updateCount = 0;
		for(String location : locations) {
			TurnoverCaseEntity lEntity = service.getEntityByLocation(location, conn);
			updateCount += fmsService.insertFactMaterial(user.getOperator_id(), lEntity.getMaterial_id(), 1, conn);
		}

		if (updateCount > 0) {
			conn.commit();
			AcceptFactService afService = new AcceptFactService();
			afService.fingerOperatorRefresh(user.getOperator_id());
		}

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.doWarehousingPlanned end");
	}

	@Privacies(permit = { 100, 0 })
	public void getWarehousingPlanList(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("TurnoverCaseAction.getWarehousingPlanList start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		// 执行检索
		TurnoverCaseService service = new TurnoverCaseService();
		List<TurnoverCaseForm> warehousingPlanList = service.getWarehousingPlanList(conn);

		// 查询结果放入Ajax响应对象
		calbackResponse.put("warehousingPlanList", warehousingPlanList);

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.getWarehousingPlanList end");
	}

	@Privacies(permit = { 101, 109, 0 })
	public void doStorage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		log.info("TurnoverCaseAction.doStorage start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		// 执行检索
		TurnoverCaseService service = new TurnoverCaseService();
		String location = req.getParameter("location");
		service.checkStorage(conn, location);

		FactMaterialService fmsService = new FactMaterialService();
		TurnoverCaseEntity lEntity = service.getEntityByLocation(location, conn);
		fmsService.insertFactMaterial(user.getOperator_id(), lEntity.getMaterial_id(), 1, conn);

		// 刷新完成数量
		conn.commit();
		AcceptFactService afService = new AcceptFactService();
		afService.fingerOperatorRefresh(user.getOperator_id());

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.doStorage end");
	}

	@Privacies(permit = { 101, 109, 0 })
	public void doStoragePlanned(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		log.info("TurnoverCaseAction.doStoragePlanned start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		// 执行检索
		TurnoverCaseService service = new TurnoverCaseService();
		List<String> locations = service.checkStorage(conn, req.getParameterMap());

		// 记录入库作业
		FactMaterialService fmsService = new FactMaterialService();
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		int updateCount = 0;
		for(String location : locations) {
			TurnoverCaseEntity lEntity = service.getEntityByLocation(location, conn);
			updateCount += fmsService.insertFactMaterial(user.getOperator_id(), lEntity.getMaterial_id(), 1, conn);
		}

		if (updateCount > 0) {
			conn.commit();
			AcceptFactService afService = new AcceptFactService();
			afService.fingerOperatorRefresh(user.getOperator_id());
		}

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.doStoragePlanned end");
	}

	@Privacies(permit = { 100, 0 })
	public void getStoragePlanList(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("TurnoverCaseAction.getStoragePlanList start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		// 执行检索
		TurnoverCaseService service = new TurnoverCaseService();
		List<TurnoverCaseForm> storagePlanList = service.getStoragePlanList(conn);

		// 查询结果放入Ajax响应对象
		calbackResponse.put("storagePlanList", storagePlanList);

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.getStoragePlanList end");
	}

	@Privacies(permit = { 100, 0 })
	public void getIdleMaterialList(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("TurnoverCaseAction.getIdleMaterialList start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		// 执行检索
		TurnoverCaseService service = new TurnoverCaseService();
		List<TurnoverCaseForm> idleMaterialList = service.getIdleMaterialList(conn);

		// 查询结果放入Ajax响应对象
		calbackResponse.put("idleMaterialList", idleMaterialList);

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.getIdleMaterialList end");
	}

	@Privacies(permit = { 101, 109, 0 })
	public void doPutin(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		log.info("TurnoverCaseAction.doPutin start");
		// Ajax回馈对象
		Map<String, Object> calbackResponse = new HashMap<String, Object>();

		// 手动放入
		TurnoverCaseService service = new TurnoverCaseService();
		service.putinManual(req.getParameter("location"), req.getParameter("material_id"), conn);

		// 检查发生错误时报告错误信息
		calbackResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, calbackResponse);

		log.info("TurnoverCaseAction.doPutin end");
	}

}

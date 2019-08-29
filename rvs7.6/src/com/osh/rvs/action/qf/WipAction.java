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
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.qa.ServiceRepairManageMapper;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.qf.FactMaterialService;
import com.osh.rvs.service.qf.WipService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class WipAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	private ModelService modelService = new ModelService();
	private WipService wipService = new WipService();

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

		log.info("WipAction.init start");

		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		// level取得
		req.setAttribute("lOptions",CodeListUtils.getSelectOptions("material_level_inline", null, "", false));

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

		log.info("WipAction.init end");
	}

	/**
	 * 维修对象条件查询
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WipAction.search start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<MaterialForm> lResultForm = wipService.searchMaterial(form, conn, errors);

			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 设定等级文字
		listResponse.put("lOptions", CodeListUtils.getGridOptions("material_level"));

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("WipAction.search end");
	}

	public void getwipempty(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WipAction.getwipempty start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 执行检索
		List<String> wipHeaped = wipService.getWipHeaped(form, conn, errors);

		// 查询结果放入Ajax响应对象
		listResponse.put("heaps", wipHeaped);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("WipAction.getwipempty end");
	}

	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("WipAction.doinsert start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		MaterialForm materialForm = (MaterialForm)form;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("model_id", materialForm.getModel_id());
		parameters.put("serial_no", materialForm.getSerial_no());
		parameters.put("wip_location", materialForm.getWip_location());

		Validators v = new Validators(parameters);
		v.add("model_id", v.required("型号"));
		v.add("serial_no", v.required("机身号"));
		v.add("wip_location", v.required("WIP库位"));

		List<MsgInfo> errors = v.validate();
		
		String id = ((MaterialForm)form).getMaterial_id();
		wipService.checkRepeatNo(id, form, conn, errors);

		if (errors.size() == 0) {
			wipService.insert(form, req.getSession(), conn, errors);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("WipAction.doinsert end");
	}

	public void doputin(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("WipAction.doputin start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		MaterialForm materialForm = (MaterialForm)form;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("wip_location", materialForm.getWip_location());

		Validators v = new Validators(parameters);
		v.add("wip_location", v.required("WIP库位"));

		List<MsgInfo> errors = v.validate();
		
		if (errors.size() == 0) {
			wipService.putin(form, req.getSession(), conn, errors);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("WipAction.doputin end");
	}

	/**
	 * 出库
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void dowarehousing(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("WipAction.dowarehousing start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		wipService.warehousing(req.getParameterMap(), conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("WipAction.dowarehousing end");
	}

	/**
	 * 系统内返还
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doresystem(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("WipAction.doresystem start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		String material_id = req.getParameter("material_id");

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("level");
		v.delete("ocm");
		v.delete("fix_type");
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			MaterialService mservice = new MaterialService();
			mservice.checkRepeatNo(material_id, form, conn, errors);
		}

		if (errors.size() == 0) {

			ProductionFeatureService featureService = new ProductionFeatureService();
			// 删除目前的等待作业
			featureService.removeWorking(material_id, null, conn);

			// 系统返还
			String new_material_id = wipService.resystem(conn, material_id, (MaterialForm) form);

			// 品保判定的对象关联更新
			ServiceRepairManageMapper srmMapper = conn.getMapper(ServiceRepairManageMapper.class);
			srmMapper.updateMaterialId(new_material_id, material_id);

			// TODO? 工作报告
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("WipAction.doresystem end");
	}

	/**
	 * 未修理返还处理 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void dostop(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("WipAction.dostop start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("material_id");

		ProductionFeatureService featureService = new ProductionFeatureService();
		int result = featureService.checkOperateResult(material_id, conn);
		if (result > 0) {
			MsgInfo info = new MsgInfo();
			info.setErrcode("info.qf.working");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.qf.working"));
			errors.add(info);
		}
		if (errors.size() == 0) {
			// 删除目前的等待作业
			featureService.removeWorking(material_id, null, conn);

			wipService.stop(conn, material_id);

			LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
			// 更新到现品作业记录（维修品）
			FactMaterialService fmsService = new FactMaterialService();
			fmsService.insertFactMaterial(user.getOperator_id(), material_id, 1, conn);

			conn.commit();

			// 刷新间接人员作业记录
			AcceptFactService afService = new AcceptFactService();
			afService.fingerOperatorRefresh(user.getOperator_id());
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("WipAction.dostop end");
	}

	public void doChangelocation(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("WipAction.doChangelocation start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		wipService.changelocation(conn, req.getParameter("material_id"), req.getParameter("wip_location"));

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("WipAction.doChangelocation end");
	}

	public void doImgCheck(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("WipAction.doImgCheck start");
		
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("material_id");

		if (wipService.checkImgCheckMaxReworking(material_id, conn)) { 
			MsgInfo info = new MsgInfo();
			info.setErrcode("");
			info.setErrmsg("维修对象已经发送到了图像检查工位。");
			errors.add(info);
		} else {
			int rework = wipService.getImgCheckMaxRework(material_id, conn);

			ProductionFeatureEntity entity = new ProductionFeatureEntity();
			entity.setMaterial_id(material_id);
			entity.setPosition_id("15");
			entity.setSection_id("00000000009");
			entity.setPace(0);
			entity.setOperate_result(0);
			entity.setRework(rework + 1); // 一个维修对象可能在这里做图像检查工位多次

			ProductionFeatureService productionFeatureService = new ProductionFeatureService();
			productionFeatureService.insert(entity, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("WipAction.doImgCheck end");
	}
}

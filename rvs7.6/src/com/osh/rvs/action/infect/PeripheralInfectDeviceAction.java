package com.osh.rvs.action.infect;

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

import com.osh.rvs.bean.infect.PeripheralInfectDeviceEntity;
import com.osh.rvs.form.infect.PeripheralInfectDeviceForm;
import com.osh.rvs.service.DevicesTypeService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.PeripheralInfectDeviceService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class PeripheralInfectDeviceAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private DevicesTypeService devicesTypeService = new DevicesTypeService();
	private ModelService modelService = new ModelService();
	private PeripheralInfectDeviceService service = new PeripheralInfectDeviceService();

	/**
	 * 周边设备点检关系初始化
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

		log.info("PeripheralInfectDeviceAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		// 周边设备的型号
		String mReferChooser = modelService.getModelReferChooser(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		// 使用设备工具品名
		String nReferChooser = devicesTypeService.getDevicesTypeReferChooser(conn);
		req.setAttribute("nReferChooser", nReferChooser);

		log.info("PeripheralInfectDeviceAction.init end");
	}

	/**
	 * 周边设备点检关系一览
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PeripheralInfectDeviceAction.search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 设备工具管理详细数据
		List<PeripheralInfectDeviceForm> peripheralInfectDevices = service.search(form, conn, errors);

		listResponse.put("peripheralInfectDevices", peripheralInfectDevices);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PeripheralInfectDeviceAction.search end");
	}

	/**
	 * 周边设备点检关系详细
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PeripheralInfectDeviceAction.detail start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 设备工具管理详细数据
		List<PeripheralInfectDeviceForm> peripheralInfectDevices = service.searchDetail(form, conn, errors);

		listResponse.put("peripheralInfectDevices", peripheralInfectDevices);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PeripheralInfectDeviceAction.detail end");
	}

	/**
	 * 新建登录实行处理
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		log.info("PeripheralInfectDeviceAction.doinsert start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		List<PeripheralInfectDeviceEntity> devices = null;
		if (errors.size() == 0) {
			devices = service.customValidateEdit(req, conn, errors);
		}

		if (errors.size() == 0) {
			// 执行删除
			service.delete(form, conn, errors);
			// 执行插入
			service.insert(devices, req.getSession(), conn, errors);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("PeripheralInfectDeviceAction.doinsert end");
	}

	/**
	 * 删除实行处理
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("PeripheralInfectDeviceAction.dodelete start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 删除记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行删除
			service.delete(form, conn, errors);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("PeripheralInfectDeviceAction.dodelete end");
	}
}

package com.osh.rvs.action.qf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.qf.FactMaterialEntity;
import com.osh.rvs.bean.qf.TurnoverCaseEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.qf.ShippingMapper;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.qf.FactMaterialService;
import com.osh.rvs.service.qf.ShippingService;
import com.osh.rvs.service.qf.TurnoverCaseService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ShippingAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	private PositionPanelService ppService = new PositionPanelService();
	private ShippingService service = new ShippingService();

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
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("ShippingAction.init start");

		req.setAttribute("oBoundOutOcm", CodeListUtils.getSelectOptions("material_direct_ocm", null, ""));
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ShippingAction.init end");
	}

	/**
	 * 工位画面初始取值处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void jsinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("ShippingAction.jsinit start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 设定OCM文字
		listResponse.put("oOptions", CodeListUtils.getGridOptions("material_direct_ocm"));
		// 设定等级文字
		listResponse.put("lOptions", CodeListUtils.getGridOptions("material_level"));

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();// TODO

		user.setSection_id(null);
		user.setPosition_id("00000000047");
		user.setProcess_code("711");
		user.setLine_id("00000000011");

		// 取得等待区一览
		listRefresh(listResponse, conn);

		// 判断是否有在进行中的维修对象
		ProductionFeatureEntity workingPf = ppService.getWorkingOrSupportingPf(user, conn);
		// 进行中的话
		if (workingPf != null) {
			if (RvsConsts.OPERATE_RESULT_SUPPORT == workingPf.getOperate_result()) {
				MsgInfo msginfo = new MsgInfo();
				msginfo.setErrcode("info.linework.supportingRemain");
				msginfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.supportingRemain"));
				infoes.add(msginfo);
				listResponse.put("redirect", "support.do");
			} else {
				// 取得作业信息
				service.getProccessingData(listResponse, workingPf.getMaterial_id(), workingPf, user, conn);

				// 页面设定为编辑模式
				listResponse.put("workstauts", "1");
			}
		} else {
			// 准备中
			listResponse.put("workstauts", "0");
		}

		user.setSection_id(section_id); // TODO

		// 检查发生错误时报告错误信息
		listResponse.put("errors", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ShippingAction.jsinit end");
	}

	/**
	 * 扫描开始/直接暂停重开
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 0 })
	public void doscan(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("ShippingAction.scan start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("material_id");

		TurnoverCaseService tcService = new TurnoverCaseService();
		TurnoverCaseEntity tce = tcService.getStorageByMaterial(material_id, conn);
		if (tce != null) {
			MsgInfo info = new MsgInfo();
			info.setErrcode("info.turnoverCase.materialShippingWithoutCase");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.turnoverCase.materialShippingWithoutCase"));
			errors.add(info);
		}

		if (errors.size() == 0) {
			// 检查出货单是否制作
			MaterialService mService = new MaterialService();
			MaterialEntity mEntity = mService.loadSimpleMaterialDetailEntity(conn, material_id);
			if (mEntity.getBreak_back_flg() == 0) { // 维修品

				FactMaterialService factMaterialService = new FactMaterialService();
				FactMaterialEntity factMaterial = new FactMaterialEntity();
				factMaterial.setMaterial_id(material_id);
				factMaterial.setProduction_type(241);

				int len = factMaterialService.searchEntities(factMaterial, conn).size();
				if (len == 0) {
					MsgInfo info = new MsgInfo();
					info.setErrcode("info.material.shipping.sheet.empty");
					info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.material.shipping.sheet.empty"));
					errors.add(info);
				}
			}
		}

		log.info("ShippingAction.scan error" + errors.size());

		if (errors.size() == 0) {
			ShippingService service = new ShippingService();
			service.scanMaterial(conn, material_id, req, errors, listResponse);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ShippingAction.scan end");
	}

	/**
	 * 作业完成
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 0 })
	public void dofinish(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("ShippingAction.dofinish start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			service.updateMaterial(req, conn);

			listRefresh(listResponse, conn);

		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ShippingAction.dofinish end");
	}

	private void listRefresh(Map<String, Object> listResponse, SqlSession conn) {
		ShippingMapper sDao = conn.getMapper(ShippingMapper.class);
		// 取得待品保处理对象一览 711
		List<MaterialEntity> waitings = sDao.getWaitings();

		// 取得今日已完成处理对象一览
		List<MaterialEntity> finished = sDao.getFinished();

		List<MaterialForm> waitingsForm = new ArrayList<MaterialForm>();
		List<MaterialForm> finishedForm = new ArrayList<MaterialForm>();

		BeanUtil.copyToFormList(waitings, waitingsForm, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);
		BeanUtil.copyToFormList(finished, finishedForm, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);

		listResponse.put("waitings", waitingsForm);
		listResponse.put("finished", finishedForm);

	}
}

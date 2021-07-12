/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：维修对象型号系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.master;

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
import com.osh.rvs.bean.master.PartialBomEntity;
import com.osh.rvs.bean.master.PartialPositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.master.PartialPositionForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.partial.PartialBomService;
import com.osh.rvs.service.partial.PartialPositionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class PartialPositionAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());
	private PartialPositionService service = new PartialPositionService();

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
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("PartialPositionAction.init start");

		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合
		
		req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level_inline",null,""));//等级集合

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		
		//权限区分
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_SA) || privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)
				|| privacies.contains(RvsConsts.PRIVACY_ADMIN)) {
			req.setAttribute("role", "operator");
		}else{
			req.setAttribute("role", "other");
		}

		req.setAttribute("kOption", CodeListUtils.getSelectOptions("repair_category_kind", null, false));

		actionForward = mapping.findForward(FW_INIT);
		log.info("PartialPositionAction.init end");
	}

	/**
	 * 零件定位查询
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialPositionAction.search start");

		PartialPositionForm partialPositionForm = (PartialPositionForm) form;

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(partialPositionForm, partialPositionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 验证型号是否输入 */
		service.modelNameValidate(partialPositionEntity, errors);

		/* 查询 */
		List<PartialPositionForm> list = service.searchPartialPosition(partialPositionEntity, conn);

		listResponse.put("partialPosition", list);
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("PartialPositionAction.search end");
	}

	/**
	 * 废改订编辑
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialPositionAction.edit start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(form, partialPositionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 查询 */
		List<PartialPositionForm> list = service.searchPartialPosition(partialPositionEntity, conn);
		if (list.size() > 0) {
			PartialPositionForm returnForm = list.get(0);
			listResponse.put("returnForm", returnForm);
		}
		listResponse.put("returnCodeActiveDate", list);
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("PartialPositionAction.edit end");
	}

	/**
	 * 废改订有效期更新
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("PartialPositionAction.doupdate start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			service.updatePartialPosition(form, request.getSession(), conn, errors);
		}

		callbackResponse.put("errors", errors);

		returnJsonResponse(response, callbackResponse);
		log.info("PartialPositionAction.doupdate end");
	}

	/**
	 * 废改订有效期和零件更新
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdatecode(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("PartialPositionAction.doupdatecode start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		// 验证零件新建零件不能相同
		service.customValidate(form, conn, errors);

		if (errors.size() == 0) {
			service.updatePartialPositionNewPartial(form, request.getSession(), conn, errors);
		}

		callbackResponse.put("errors", errors);

		returnJsonResponse(response, callbackResponse);
		log.info("PartialPositionAction.doupdatecode end");
	}

	/**
	 * 按型号读取工作指示单格式
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void instructLoad(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) {
		log.info("PartialPositionAction.instructLoad start");

		PartialPositionForm partialPositionForm = (PartialPositionForm) form;

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(partialPositionForm, partialPositionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 验证型号是否输入 */
		service.modelNameValidate(partialPositionEntity, errors);

		/* 查询 */
		Map<String, List<PartialPositionForm>> instructLists = service.loadInstruct(partialPositionEntity.getModel_id(), conn);

		String level = partialPositionEntity.getLevel();
		if (level != null && !RvsUtils.isLightFix(level)) {
			// 获取RankBom信息
			PartialBomService pRankBomService = new PartialBomService();
			List<PartialBomEntity> rankBom = pRankBomService.searchRankBom(partialPositionForm, conn);
			if (!rankBom.isEmpty()) {
				listResponse.put("rankBom", rankBom);
			} else {
				List<String> partialBom = pRankBomService.searchPartialBomEntity(partialPositionForm, conn);
				if (!partialBom.isEmpty()) {
					listResponse.put("partialBom", partialBom);
				}
			}
		}

		listResponse.put("instructLists", instructLists);
		listResponse.put("components", service.getComponentOfModel(partialPositionEntity.getModel_id(), conn));

		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("PartialPositionAction.instructLoad end");
	}

	/**
	 * 按类别读取工作指示单格式并生成文件
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void makefile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) {
		log.info("PartialPositionAction.makefile start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String kind = request.getParameter("kind");

		/* 查询 */
		String file_path = service.makeBomFile(kind, conn);

		cbResponse.put("file_path", file_path);
		cbResponse.put("errors", errors);

		returnJsonResponse(response, cbResponse);
		log.info("PartialPositionAction.makefile end");
	}
}

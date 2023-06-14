package com.osh.rvs.action.partial;

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

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.partial.ConsumableApplicationDetailService;
import com.osh.rvs.service.partial.ConsumableApplyService;
import com.osh.rvs.service.partial.ConsumablePositionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 维修对象消耗品
 * @author Gong
 *
 */
public class MaterialConsumableAction extends BaseAction {


	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("MaterialConsumableAction.search start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		if (errors.size() == 0) {
			// 当前作业
			PositionPanelService posPService = new PositionPanelService();
			ProductionFeatureEntity workingPf = posPService.getProcessingPf(user, conn);

			String type = req.getParameter("belongs");

			if ("1".equals(type) && workingPf.getRework() > 0) {
				// 重做CCD盖玻璃更换，不使用消耗品
			} else {
				ConsumablePositionService service = new ConsumablePositionService();
				List<MaterialPartialDetailForm> bomList = service.getByMaterialOfPosition(form, user.getPosition_id(), 
						user.getPrivacies().contains(RvsConsts.PRIVACY_LINE), conn);
				listResponse.put("bomList", bomList);

				if ("1".equals(type)) {
					// CCD组件提前发放
					ConsumableApplicationDetailService appService = new ConsumableApplicationDetailService();
					List<MaterialPartialDetailEntity> assembleList = appService.searchCcdAdvanced(req.getParameter("material_id"), conn);
					listResponse.put("assembleList", assembleList);
				} else {
					// 在线消耗品使用
					List<MaterialPartialDetailEntity> assembleList = service.getConsumableReceptOfMaterialForPosition(form, user, workingPf, conn);
					listResponse.put("assembleList", assembleList);
				}
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialConsumableAction.search end");
	}

	@Privacies(permit={107})
	public void doCommit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("MaterialConsumableAction.doCommit start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		// 当前作业
		PositionPanelService posPService = new PositionPanelService();
		ProductionFeatureEntity workingPf = posPService.getProcessingPf(user, conn);

		if (workingPf == null) {
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.linework.workingLost");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
			errors.add(error);
		}
		if (errors.size() == 0) {
			String type = req.getParameter("type");

			if ("3".equals(type)) {
				ConsumablePositionService service = new ConsumablePositionService();
				service.commitAssemble(req, user, workingPf.getRework(), conn);

				List<MaterialPartialDetailEntity> assembleList = service.getConsumableReceptOfMaterialForPosition(form, user, workingPf, conn);
				callbackResponse.put("assembleList", assembleList);
			} else if ("1".equals(type)) {
				ConsumableApplyService appService = new ConsumableApplyService();
				appService.commitAssemble(req, user, conn);

				// CCD组件提前发放
				ConsumableApplicationDetailService appdService = new ConsumableApplicationDetailService();
				List<MaterialPartialDetailEntity> assembleList = appdService.searchCcdAdvanced(req.getParameter("material_id"), conn);
				callbackResponse.put("assembleList", assembleList);
			}
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		log.info("MaterialConsumableAction.doCommit end");
	}

}

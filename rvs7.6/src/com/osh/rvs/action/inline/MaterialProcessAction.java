package com.osh.rvs.action.inline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.service.MaterialProcessService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class MaterialProcessAction extends BaseAction {

	private MaterialProcessService materialProcessService = new MaterialProcessService();
	
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("MaterialProcessAction.doUpdate start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if (errors.size() == 0) {
			materialProcessService.updateMaterialProcess(form, conn);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		log.info("MaterialProcessAction.doUpdate end");
	}
	
}

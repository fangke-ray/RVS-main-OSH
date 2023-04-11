package com.osh.rvs.action.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.PartialBomForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.partial.PartialBomService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PartialBomAction extends BaseAction {
	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * 页面初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("PartialBomAction.init start");

		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		request.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合

		// 等级
		request.setAttribute("smMaterialLevelInline", CodeListUtils.getSelectOptions("material_level_inline", null, ""));
		request.setAttribute("goMaterial_level_inline", CodeListUtils.getGridOptions("material_level_inline"));
		// 梯队
		request.setAttribute("goEechelon_code", CodeListUtils.getGridOptions("echelon_code"));


		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		
		//权限区分
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_SA) || privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)
				|| privacies.contains(RvsConsts.PRIVACY_ADMIN)) {
			request.setAttribute("role", "operator");
		}else{
			request.setAttribute("role", "other");
		}

		actionForward = mapping.findForward(FW_INIT);

		log.info("PartialBomAction.init end");
	}

	/**
	 * 零件BOM信息管理
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
		log.info("PartialBomAction.search start");
		// 对JAax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		PartialBomService service = new PartialBomService();
		
		PartialBomForm partialBomForm=(PartialBomForm)form;
		
		String model_id=partialBomForm.getModel_id();//型号
		String level=partialBomForm.getLevel();//等级
		String code=partialBomForm.getCode();//零件编码
		
		if(CommonStringUtil.isEmpty(model_id) && CommonStringUtil.isEmpty(level)){//型号和等级都为空
			if(CommonStringUtil.isEmpty(code)){
				MsgInfo info=new MsgInfo();
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "型号"));
				info.setComponentid("view_mode_name");
				msgInfos.add(info);
				info=new MsgInfo();
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "等级"));
				info.setComponentid("search_level_id");
				msgInfos.add(info);
			}
		}else if(CommonStringUtil.isEmpty(model_id)){
			MsgInfo info=new MsgInfo();
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "型号"));
			info.setComponentid("view_mode_name");
			msgInfos.add(info);
		}else if(CommonStringUtil.isEmpty(level)){
			MsgInfo info=new MsgInfo();
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "等级"));
			info.setComponentid("search_level_id");
			msgInfos.add(info);
		}
		
		
		if(msgInfos.size()<=0){
			List<PartialBomForm> list = service.searchRankBomForm(form, conn);
			if (list == null || list.size() == 0) {
				list = service.searchPartialBom(form, conn);
			}
			listResponse.put("finished", list);
		}
		
		listResponse.put("errors", msgInfos);
		returnJsonResponse(response, listResponse);

		log.info("PartialBomAction.search end");
	}

	/**
	 * 导出BOM表
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void reportBom(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,SqlSession conn){
		log.info("PartialBomAction.reportBom start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
				
		PartialBomService service = new PartialBomService();
		String fileName ="零件BOM.xls";
		String filePath=service.dowloadPartialBom(form, conn);
		
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("PartialBomAction.reportBom end");
	}
		
	
}

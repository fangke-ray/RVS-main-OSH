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

import com.osh.rvs.service.OptionalFixService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;

public class MaterialOptionalFixAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	/**
	 * 取得维修品选择修理
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 101, 0 })
	public void getSettingByMaterial(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("MaterialOptionalFixAction.getSettingByMaterial start");
		// Ajax回馈对象
		Map<String, Object> cbResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		OptionalFixService opfService = new OptionalFixService();

		// 查询维修品已选项结果放入Ajax响应对象
		cbResponse.put("sel_list", opfService.searchMaterialOptionalFix(form, conn));

		// 查询等级可选项结果放入Ajax响应对象
		cbResponse.put("options_list", opfService.getOptionalFixByRank(form, conn));

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		log.info("MaterialOptionalFixAction.getSettingByMaterial end");
	}

	/**
	 * 更新维修品选择修理
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 101, 0 })
	public void doUpdateSettingByMaterial(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		log.info("MaterialOptionalFixAction.updateSettingByMaterial start");
		// Ajax回馈对象
		Map<String, Object> cbResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		OptionalFixService opfService = new OptionalFixService();

		// 查询维修品已选项结果放入Ajax响应对象
		List<String> orgSelList = opfService.searchMaterialOptionalFix(form, conn);

		// 判断更新差异并做更新
		opfService.compareAndUpdate(orgSelList, form, cbResponse, conn);

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		log.info("MaterialOptionalFixAction.updateSettingByMaterial end");
	}

	/**
	 * 取得维修品选择修理名称
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 101, 0 })
	public void getLabelByMaterial(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("MaterialOptionalFixAction.getLabelByMaterial start");
		// Ajax回馈对象
		Map<String, Object> cbResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		OptionalFixService opfService = new OptionalFixService();

		opfService.getMaterialOptionalFix(req.getParameter("material_id"), null, cbResponse, conn);

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		log.info("MaterialOptionalFixAction.getLabelByMaterial end");
	}
}

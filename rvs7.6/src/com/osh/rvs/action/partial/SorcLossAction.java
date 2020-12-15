package com.osh.rvs.action.partial;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
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

import com.osh.rvs.bean.partial.SorcLossEntity;
import com.osh.rvs.form.partial.SorcLossForm;
import com.osh.rvs.service.partial.SorcLossService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class SorcLossAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	private SorcLossService service = new SorcLossService();

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("SorcLossAction.init start");

		// 分室
		request.setAttribute("hOcm", CodeListUtils.getGridOptions("material_ocm"));

		// ocm rank
		request.setAttribute("hOcmRank", CodeListUtils.getGridOptions("material_ocm_direct_rank"));

		// sorc rank
		request.setAttribute("hSorcRank", CodeListUtils.getGridOptions("material_level"));

		// 发现工程
		request.setAttribute("sDiscoverProject", CodeListUtils.getSelectOptions("partial_append_belongs", null, ""));
		request.setAttribute("hDiscoverProject", CodeListUtils.getGridOptions("partial_append_belongs"));

		// 责任区分
		request.setAttribute("sLiabilityFlg", CodeListUtils.getSelectOptions("liability_flg", null, ""));
		request.setAttribute("hLiabliityFlg", CodeListUtils.getGridOptions("liability_flg"));

		// 有偿与否
		request.setAttribute("sServiceFreeFlg", CodeListUtils.getSelectOptions("service_free_flg", null, ""));
		request.setAttribute("hServiceFreeFlg", CodeListUtils.getGridOptions("service_free_flg"));

		String strYear_month = request.getParameter("year_month");
		if (!CommonStringUtil.isEmpty(strYear_month)) {
			request.setAttribute("year_month", strYear_month);
		} else {
			// 获取当前时间
			Calendar calendar = Calendar.getInstance();
			String current_date = DateUtil.toString(calendar.getTime(), "yyyy/MM/dd");
			request.setAttribute("current_date", current_date);
		}

		request.getSession().removeAttribute("result");
		request.getSession().removeAttribute("ocm_shipping_month");
		request.getSession().removeAttribute("resultOfRepair");

		actionForward = mapping.findForward(FW_INIT);

		log.info("SorcLossAction.init end");
	}

	/**
	 * SORC损金147PA 数据查询
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("SorcLossAction.search start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		SorcLossForm sorcLossForm = (SorcLossForm) form;
		SorcLossEntity sorcLossEntity = new SorcLossEntity();

		BeanUtil.copyToBean(sorcLossForm, sorcLossEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// SORC损金 数据查询
		List<SorcLossForm> sorcLossForms = service.searchSorcLoss(sorcLossEntity, conn, errors);

		// 当出货月条件不是空时，月下载的数据就是当前检索页面显示的数据
		if (!isEmpty(sorcLossForm.getOcm_shipping_month())) {
			// 条件就是月的时候
			request.getSession().setAttribute("result", sorcLossForms);
			request.getSession().setAttribute("ocm_shipping_month", sorcLossEntity.getOcm_shipping_month());
		}

		// 如果含有SORC No.检索条件，检索结果又为空时，提示是否为保内返修对象所以不显示
		if (sorcLossForms.size() == 0 && !isEmpty(sorcLossForm.getSorc_no())) {
			if (service.checkServiceRepair(sorcLossForm.getSorc_no(), conn)) {
				MsgInfo info = new MsgInfo();
				info.setComponentid("sorc_no");
				info.setErrmsg("检索的维修对象可能为保内返修品。"); // TODO
				errors.add(info);
			}
		}

		listResponse.put("sorcLossForms", sorcLossForms);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		// 当出货日期和出货月条件不是空时，月下载的数据要进行重新检索---比如输入2014-06-01要查询出整个6月的满足条件的所有数据
		if (!isEmpty(sorcLossForm.getOcm_shipping_date()) || !isEmpty(sorcLossForm.getOcm_shipping_month())) {
			// 月损金数据--保内返品维修对象
			List<SorcLossForm> sorcLossFormMonths = service.searchSorcLossOfRepair(sorcLossEntity, conn, errors);
			// 将每次的查询结果都放在session中（为了提供数据给月损金下载）
			request.getSession().setAttribute("resultOfRepair", sorcLossFormMonths);
		}

		log.info("SorcLossAction.search end");
	}

	/**
	 * 获得不良简述的所有值
	 * 
	 * @param response
	 * @param conn
	 */
	public void getAutoComplete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("SorcLossAction.getAutoComplete start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		// 不良简述 --autoComplete
		List<String> nogoodDescriptionList = service.searchLossDetailOfNogoodDescription(conn);

		listResponse.put("nogoodDescriptionList", nogoodDescriptionList.toArray());
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		log.info("SorcLossAction.getAutoComplete end");
	}

	/**
	 * 更新不良简述
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdateNogoodDescription(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("SorcLossAction.doupdateNogoodDescription start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		SorcLossForm sorcLossForm = (SorcLossForm) form;

		SorcLossEntity sorcLossEntity = new SorcLossEntity();
		BeanUtil.copyToBean(sorcLossForm, sorcLossEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		service.updateNogoodDescription(sorcLossEntity, conn, errors);

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("SorcLossAction.doupdateNogoodDescription end");
	}

	/**
	 * SORC 损金 数据更新
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
		log.info("SorcLossAction.doupdate start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		SorcLossForm sorcLossForm = (SorcLossForm) form;

		SorcLossEntity sorcLossEntity = new SorcLossEntity();
		BeanUtil.copyToBean(sorcLossForm, sorcLossEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		service.updateSorcLoss(sorcLossEntity, conn, errors);

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("SorcLossAction.doupdate end");
	}

	/**
	 * 月损金一览表下载
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("SorcLossAction.report start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		SorcLossForm sorcLossForm = (SorcLossForm) form;
		// 验证 出货月和出货日期其中必须有一个有值
		service.customValidate(sorcLossForm, conn, errors);

		Object result = request.getSession().getAttribute("result");
		if (result == null) {
			
			MsgInfo e = new MsgInfo();
			e.setComponentid("ocm_shipping_month");
			e.setErrmsg("没有月损金一览表数据，请选择【出货月】后进行一次查询。");
			errors.add(e);
		}

		if (errors.size() == 0) {
			String fileName = "SORC损金一览.xls";
			String filePath = service.createMonthLossReport(sorcLossForm,request, conn);
			listResponse.put("fileName", fileName);
			listResponse.put("filePath", filePath);
		}
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("SorcLossAction.report end");
	}
}

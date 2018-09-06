package com.osh.rvs.action.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.PositionProductionForm;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.PositionProductionService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.SectionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

public class PositionProductionAction extends BaseAction {

	private SectionService sectionService = new SectionService();
	private LineService lineService = new LineService();
	private PositionProductionService positionProductionService = new PositionProductionService();
	private PositionService positionService = new PositionService();
	
	/**
	 * 初始化
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PositionProductionAction.init start");
		
		String lOptions = lineService.getOptions(conn);
		req.setAttribute("lOptions", lOptions);
		
		String sOptions = sectionService.getOptions(conn, "(全部)");
		req.setAttribute("sOptions", sOptions);
		
		String pReferChooser = positionService.getOptions(conn);
		req.setAttribute("pReferChooser", pReferChooser);
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		boolean isOperator = isOperator(req.getSession());
		req.setAttribute("isOperator", Boolean.toString(isOperator));
		
		log.info("PositionProductionAction.init end");
	}
	
	@Privacies(permit={1, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PositionProductionAction.search start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<PositionProductionForm> lResultForm = new ArrayList<PositionProductionForm>();
		HttpSession session = req.getSession();
		if (isOperator(session)) { //当前用户是操作人员
			lResultForm = searchByOperator(session, conn);
		} else {
			lResultForm = searchByCondition(form, conn);
		}
			// 查询结果放入Ajax响应对象
		listResponse.put("list", lResultForm);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionProductionAction.search end");
	}
	
	@Privacies(permit={1, 0})
	public void getDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PositionProductionAction.getDetial start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		
		List<MsgInfo> errors = v.validate();
		if (errors.size() == 0) {
			PositionProductionForm detail = positionProductionService.getDetail(form, conn);
		
			listResponse.put("detail", detail);
			
			List<PositionProductionForm> list = positionProductionService.getProductionFeatureByKey(form, conn);
			
			listResponse.put("list", list);
			
			String names = getOperaterList(list);
			
			listResponse.put("names", names);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("PositionProductionAction.getDetial end");
	}
	
	public void searchByOperator(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PositionProductionAction.searchByOperator start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		
		List<MsgInfo> errors = v.validate();
		if (errors.size() == 0) {
			List<PositionProductionForm> list = positionProductionService.getProductionFeatureByKey(form, conn);
			listResponse.put("list", list);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("PositionProductionAction.searchByOperator end");
	}
	
	private List<PositionProductionForm> searchByOperator(HttpSession session, SqlSession conn) {
		LoginData loginData = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		
		PositionProductionForm form = new PositionProductionForm();
		form.setOperator_id(loginData.getOperator_id());
		// 日期为2日内
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		form.setAction_time_start(DateUtil.toString(cal.getTime(), DateUtil.DATE_PATTERN));
		
		List<PositionProductionForm> lResultForm = positionProductionService.searchByCondition(form, conn);
		return lResultForm;
	}
	
	private List<PositionProductionForm> searchByCondition(ActionForm form, SqlSession conn) {
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		
		List<MsgInfo> errors = v.validate();
		
		List<PositionProductionForm> lResultForm = new ArrayList<PositionProductionForm>();
		if (errors.size() == 0) {
			lResultForm = positionProductionService.searchByCondition(form, conn);
		}
		
		return lResultForm;
	}
	
	private boolean isOperator(HttpSession session) {
		List<Integer> privacies = getPrivacies(session);
		
		if (privacies.contains(RvsConsts.PRIVACY_POSITION) && 
				!(privacies.contains(RvsConsts.PRIVACY_LINE) || privacies.contains(RvsConsts.PRIVACY_QA_MANAGER) ||
				privacies.contains(RvsConsts.PRIVACY_PROCESSING) || privacies.contains(RvsConsts.PRIVACY_ADMIN))) {
			return true;
		}
		
		return false;
	}
	private List<Integer> getPrivacies(HttpSession session) {
		LoginData loginData = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = loginData.getPrivacies();
		
		return privacies;
	}

	private String getOperaterList(List<PositionProductionForm> forms){
	
		Map<String, String> map = new TreeMap<String, String>();
		
		for (PositionProductionForm form : forms) {
			map.put(form.getOperator_id(), form.getOperator_name());
		}
		
		if (map.size() == 1) {
			return "";
		}
		return CodeListUtils.getSelectOptions(map, null, "全部担当者", false);
	}
}

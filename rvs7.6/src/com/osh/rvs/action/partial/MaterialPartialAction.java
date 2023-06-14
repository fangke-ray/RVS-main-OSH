package com.osh.rvs.action.partial;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReportMetaData;
import com.osh.rvs.common.ReportUtils;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.service.MaterialPartialService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.inline.ForSolutionAreaService;
import com.osh.rvs.service.partial.ConsumablePositionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.Converter;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.copy.IntegerConverter;
import framework.huiqing.common.util.validator.Validators;

/**
 * 维修对象零件
 * @author Gong
 *
 */
public class MaterialPartialAction extends BaseAction {

	private static SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
	
	private MaterialPartialService materialPartialService = new MaterialPartialService();
	private ModelService modelService = new ModelService();
	/**
	 * 画面初始化
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("MaterialPartialAction.init start");
		
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		// 零件管理
		if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)) {
			req.setAttribute("privacy", "pm");
		}

		// 现品 (管理/订购)
		else if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			req.setAttribute("privacy", "fact");
		}
		else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			req.setAttribute("privacy", "process");
		}

		// 线内 (签收)
		if ("00000000012".equals(user.getLine_id()) || "00000000013".equals(user.getLine_id()) || "00000000014".equals(user.getLine_id())) {
			if (privacies.contains(RvsConsts.PRIVACY_POSITION)) {
				List<PositionEntity> positions = user.getPositions();
				for (PositionEntity position : positions) {
					String position_id = position.getPosition_id();
					if ("00000000021".equals(position_id) || "00000000027".equals(position_id) || "00000000032".equals(position_id) || "00000000028".equals(position_id)) { // TODO 331 lingshijiaru
						req.setAttribute("privacy", "line");
						break;
					}
				}
			} else if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
				req.setAttribute("privacy", "line");
			}
		}

		SectionService sectionService = new SectionService();
		String sOptions = sectionService.getOptions(conn, "(全部)");
		req.setAttribute("sOptions", sOptions);

		log.info("MaterialPartialAction.init end");
	}

	/**
	 * 更新维修对象零件信息
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("MaterialPartialAction.doUpdate start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
			String conflexError = materialPartialService.updateMaterialPartial(req, user, conn);
			callbackResponse.put("conflexError", conflexError);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		log.info("MaterialPartialAction.doUpdate end");
	}

	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("MaterialPartialAction.doInsert start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if (errors.size() == 0) {
			materialPartialService.insertMaterialPartial(form, conn);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		log.info("MaterialPartialAction.doInsert end");
	}

	public void getBoRateInWeek(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("MaterialPartialAction.getBoRateInWeek start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			//取BO率
//			String[] rate = getBORate(form, lResultForm);
			MaterialPartialForm f = (MaterialPartialForm) form;

			Date from = null;
			Date to = null;

			String[] rate = materialPartialService.getBoRate(from, to, conn);
			listResponse.put("rate", "本周零件的 当天BO率：" + rate[0] + " % |  三天BO率：" + rate[1] + " %");
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialPartialAction.getBoRateInWeek end");
	}

	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("MaterialPartialAction.search start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		String fact = user.getPrivacies().contains(RvsConsts.PRIVACY_FACT_MATERIAL) ? "fact" : null;

		if (errors.size() == 0) {
			// 执行检索
			List<MaterialPartialForm> lResultForm = materialPartialService.searchMaterial(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);

			MaterialPartialForm f = (MaterialPartialForm) form;
			if (!CommonStringUtil.isEmpty(f.getOrder_date_start())) {

				Date from = null;
				if (!CommonStringUtil.isEmpty(f.getOrder_date_start())) {
					from = DateUtil.toDate(f.getOrder_date_start(), DateUtil.DATE_PATTERN);
				}
				Date to = null;
				if (!CommonStringUtil.isEmpty(f.getOrder_date_end())) {
					to = DateUtil.toDate(f.getOrder_date_end(), DateUtil.DATE_PATTERN);
				}

				String[] rate = materialPartialService.getBoRate(from, to, conn);

				if (CommonStringUtil.isEmpty(f.getOrder_date_end())) {
					listResponse.put("rate_w", f.getOrder_date_start() + "起至今 零件的 当天BO率：" + rate[0] + " % |  三天BO率：" + rate[1] + " %");
				} else {
					listResponse.put("rate_w", f.getOrder_date_start() + "起到" + f.getOrder_date_end() +
							" 零件的 当天BO率：" + rate[0] + " % |  三天BO率：" + rate[1] + " %");
				}
			}

		}

		listResponse.put("fact", fact);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialPartialAction.search end");
	}

	/**
	 * 取得详细订购信息
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void loadByTimes(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) {
		log.info("MaterialPartialAction.loadByTimes start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		String id = req.getParameter("id");
		String times = req.getParameter("times");
		
		MaterialPartialForm partialForm = materialPartialService.loadMaterialPartial(conn, id, Integer.parseInt(times));
		
		listResponse.put("partialForm", partialForm);
		
		returnJsonResponse(res, listResponse);
		
		log.info("MaterialPartialAction.loadByTimes end");
	}
	/**
	 * 取得维修对象订购信息
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void searchMaterialPartialDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) {
		log.info("MaterialPartialAction.searchMaterialPartialDetail start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		String materialId = req.getParameter("material_id");
		if (materialId == null) {
			Object oBean = req.getSession().getAttribute("materialDetail");
			if (oBean != null) {
				materialId = ((MaterialEntity) oBean).getMaterial_id();
			}			
		}
		String occurTimes = req.getParameter("occur_times");
		String lineId = req.getParameter("line_id");
		
		List<MaterialPartialDetailForm> lResultForm = materialPartialService.searchMaterialPartialDetail(conn, materialId, occurTimes, lineId);
		ConsumablePositionService cpService = new ConsumablePositionService();
		List<MaterialPartialDetailForm> cplist = cpService.getConsumableReceptOfMaterial(materialId, lineId, conn);
		if (cplist.size() > 0) {
			lResultForm.addAll(cplist);
		}

		listResponse.put("list", lResultForm);
		
		returnJsonResponse(res, listResponse);
		
		log.info("MaterialPartialAction.searchMaterialPartialDetail end");
	}

	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("MaterialPartialAction.report start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<MaterialPartialForm> lResultForm = materialPartialService.searchMaterial(form, conn, errors);
			
			String filePath = ReportUtils.createReport(lResultForm, ReportMetaData.boPartialTitles, ReportMetaData.boPartialColNames);
			listResponse.put("filePath", filePath);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("MaterialPartialAction.report end");
	}
	
	/**
	 * 建立零件订购报表
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void reportPartialOrder(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) {
		log.info("MaterialPartialAction.reportPartialOrder start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<MaterialPartialForm> lResultForm = materialPartialService.searchMaterialReport(form, conn, errors);
			
			List<MaterialPartialForm> lResultItemForm = materialPartialService.searchMaterialItemReport(form, conn, errors);
			
			String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\零件订购表.xls";
			String cacheName = "ljdgb" + new Date().getTime() + ".xls";
			String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" + cacheName;
			try {
				FileUtils.copyFile(new File(path), new File(cachePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			materialPartialService.makeOrderFile(cachePath, lResultForm,lResultItemForm);
			listResponse.put("filePath", cacheName);
			listResponse.put("fileName", "零件订购表.xls");
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("MaterialPartialAction.reportPartialOrder end");
	}
	
	/**
	 * BO缺品零件表生成
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void reportBo(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) {
		log.info("MaterialPartialAction.reportBo start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			listResponse.put("filePath", materialPartialService.makeBoFile(form,conn));
			listResponse.put("fileName", "BO缺品零件表.xls");
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("MaterialPartialAction.reportBo end");
	}

	/**
	 * 建立零件订购追加报表
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void reportAppendOrder(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) {
		log.info("MaterialPartialAction.reportAppendOrder start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			//List<PartialForm> lResultForm = materialPartialService.searchMaterialReport(form, conn, errors);
			
			List<MaterialPartialForm> lResultForm = materialPartialService.searchPartialAddtionalInf(form, conn, errors);
			
			String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\零件追加明细表模板.xls";
			String cacheName = "ljzjmxbmb" + new Date().getTime() + ".xls";
			String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" + cacheName;
			try {
				FileUtils.copyFile(new File(path), new File(cachePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			listResponse.put("filePath", materialPartialService.makePartialAddtionalInfFile(cachePath, lResultForm));
			listResponse.put("fileName", "零件追加明细表.xls");

		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("MaterialPartialAction.reportAppendOrder end");
	}

	/*private String[] getBORateNew(ActionForm form, List<PartialForm> lResultForm) throws Exception {
		PartialForm f = (PartialForm) form;
		String[] rate = new String[2];
		
		Set<String> count = new HashSet<String>();
		float a = 0, b = 0;
		
		for (PartialForm pf: lResultForm) {
			if ("1".equals(pf.getBo_flg()) || "2".equals(pf.getBo_flg())) {
				if (format.parse(pf.getOrder_date()).after(RvsUtils.switchWorkDate(new Date(), -7))) {//order_date >=今天减去一周
					a++;
				}
						
				if ()
				
			}
		}
		
	}*/
	@SuppressWarnings("unused")
	private String[] getBORate(ActionForm form, List<MaterialPartialForm> lResultForm) throws Exception {
		MaterialPartialForm f = (MaterialPartialForm) form;
		String[] rate = new String[3];
		
		if (!CommonStringUtil.isEmpty(f.getArrival_plan_date_start()) ||
			!CommonStringUtil.isEmpty(f.getArrival_plan_date_end())) {
			return null;
		}

		Set<String> count = new HashSet<String>();
		float a = 0, b = 0, c = 0;
		for (MaterialPartialForm pf: lResultForm) {
			if ("1".equals(pf.getBo_flg())) {
				a++;
				
				if (CommonStringUtil.isEmpty(pf.getArrival_plan_date()) || 
						format.parse(pf.getArrival_plan_date()).after(RvsUtils.switchWorkDate(new Date(), 3))) {
					b++;
				}
				
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.WEEK_OF_MONTH, 1);
				if (CommonStringUtil.isEmpty(pf.getArrival_plan_date()) || 
						format.parse(pf.getArrival_plan_date()).after(calendar.getTime())) {
					c++;
				}
			}
			
			count.add(pf.getMaterial_id());
		}
		
		float d = count.size();
		rate[0] = Integer.valueOf(Math.round((a/d)*100)).toString();
		rate[1] = Integer.valueOf(Math.round((b/d)*100)).toString();
		rate[2] = Integer.valueOf(Math.round((c/d)*100)).toString();
		
		return rate;
	}

	/**
	 * 取得消耗品列表
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getConsumables(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("MaterialPartialAction.getConsumables start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			callbackResponse.put("consumables_list", materialPartialService.getConsumables(form, conn, errors));
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		log.info("MaterialPartialAction.getConsumables end");
	}

	/**
	 * 消耗品替代
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdateConsumables(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("MaterialPartialAction.updateConsumables start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			materialPartialService.updateInstead(form, req.getParameterMap(), req.getSession(), conn, 5);

			// 检查工位上BO零件为解除
			LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
			ForSolutionAreaService fsoService = new ForSolutionAreaService();

			// 排除待处理
			String sOccur_times = req.getParameter("occur_times");
			Converter<Integer> ic = IntegerConverter.getInstance();
			fsoService.solveBo(req.getParameter("material_id"), ic.getAsObject(sOccur_times), user.getOperator_id(), conn);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		log.info("MaterialPartialAction.updateConsumables end");
	}

	/**
	 * 取得预制品列表
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getSnouts(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("MaterialPartialAction.getSnouts start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			// 查询可预制替代零件
			List<MaterialPartialDetailForm> snouts = materialPartialService.getSnouts(form, conn, errors);
			if (snouts == null) {
				MaterialPartialEntity dest = new MaterialPartialEntity(); 
				BeanUtil.copyToBean(form, dest, CopyOptions.COPYOPTIONS_NOEMPTY);
				// 尚未订购时警报
				if (materialPartialService.loadMaterialPartial(conn, dest.getMaterial_id(), 1) == null) {
					callbackResponse.put("never_order", "1");
				}
			} else {
				callbackResponse.put("Snouts_list", materialPartialService.getSnouts(form, conn, errors));
			}
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		log.info("MaterialPartialAction.getSnouts end");
	}

	/**
	 * 预制品替代
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdateSnouts(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("MaterialPartialAction.updateSnouts start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			materialPartialService.updateInstead(form, req.getParameterMap(), req.getSession(), conn, 6);

			// 检查工位上BO零件为解除
			LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
			ForSolutionAreaService fsoService = new ForSolutionAreaService();

			// 排除待处理
			String sOccur_times = req.getParameter("occur_times");
			Converter<Integer> ic = IntegerConverter.getInstance();
			fsoService.solveBo(req.getParameter("material_id"), ic.getAsObject(sOccur_times), user.getOperator_id(), conn);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		log.info("MaterialPartialAction.updateSnouts end");
	}

	/**
	 * 维修对象零件订购详细
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void getDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) {
		log.info("MaterialPartialAction.getDetail start");

		String id = req.getParameter("id");
		String occur_times = req.getParameter("occur_times");

		// 取得用户信息
		HttpSession session = req.getSession();

		Object oBean = session.getAttribute("materialDetail");
		Object sessionFrom = session.getAttribute("material_detail_from");
		req.setAttribute("from", sessionFrom);

		MaterialPartialForm partialForm = (MaterialPartialForm) form;

		if (oBean != null) {
			id = ((MaterialEntity) oBean).getMaterial_id();
		}

		Integer iOccur_times = null;
		if (occur_times != null) {
			iOccur_times = Integer.parseInt(occur_times);
		}
		partialForm = materialPartialService.loadMaterialPartial(conn, id, iOccur_times); //改了

		if (partialForm != null) {
			partialForm.setBo_flg(CodeListUtils.getValue("material_partial_bo_flg", partialForm.getBo_flg()));

			req.setAttribute("partialForm", partialForm);
		}

		// 迁移到页面
		actionForward = mapping.findForward("detail");

		log.info("MaterialPartialAction.getDetail end");
	}
}

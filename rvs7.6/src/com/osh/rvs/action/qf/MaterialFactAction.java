package com.osh.rvs.action.qf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.master.ModelForm;
import com.osh.rvs.form.master.ProcessAssignForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.form.qf.MaterialFactForm;
import com.osh.rvs.mapper.qf.WipMapper;
import com.osh.rvs.service.HolidayService;
import com.osh.rvs.service.MaterialPartialService;
import com.osh.rvs.service.MaterialProcessAssignService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.ProcessAssignService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.qf.FactMaterialService;
import com.osh.rvs.service.qf.MaterialFactService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 维修对象现品管理
 * 
 * @author Gong
 *
 */
public class MaterialFactAction extends BaseAction {

	private ModelService modelService = new ModelService();
	private MaterialFactService materialFactService = new MaterialFactService();
	private SectionService sectionService = new SectionService();
	private ProductionFeatureService productionFeatureService = new ProductionFeatureService();
	private Logger log = Logger.getLogger(getClass());

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
		log.info("MaterialFactAction.init start");
		
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);
	
		// 获得维修课室选项
		String sOptions = sectionService.getOptions(conn, null);
		req.getSession().setAttribute("sOptions", sOptions);

		// 获得维修流程选项
		ProcessAssignService paService = new ProcessAssignService();
		String paOptions = paService.getGroupOptions(null, conn);
		req.getSession().setAttribute("paOptions", paOptions);

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			req.setAttribute("editor", "true");
		} else {
			req.setAttribute("editor", "false");
		}
		if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			req.setAttribute("editor", "manage");
		}

		// 取得2天前 和 1天前作为的同意后投线限制的比较
		HolidayService hService = new HolidayService();
		req.setAttribute("twoDaysBefore", hService.addWorkdays(new Date(), -2, conn));
		req.setAttribute("oneDayBefore", hService.addWorkdays(new Date(), -1, conn));

		// 取得类别下拉框信息
		String kOptions = CodeListUtils.getGridOptions("category_kind");
		req.setAttribute("kOptions", kOptions);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("MaterialFactAction.init end");
	}

	/**
	 * 查询待投线一览
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("MaterialFactAction.search start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<MaterialFactForm> lResultForm = materialFactService.searchMaterial(form, conn, errors);
			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialFactAction.search end");
	}

	/**
	 * 查询已投线一览
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchInline(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("MaterialFactAction.searchInline start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 执行检索
		List<MaterialFactForm> lResultForm = materialFactService.searchInlineMaterial(conn);
		// 查询结果放入Ajax响应对象
		listResponse.put("list", lResultForm);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialFactAction.searchInline end");
	}

	/**
	 * 更新同意日
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doupdateAgreedDate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("MaterialFactAction.updateAgreedDate start");
		
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			materialFactService.updateAgreedDate(form, conn);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialFactAction.updateAgreedDate end");
	}
	
	/**
	 * 发布到图像检查
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doImgCheck(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("MaterialFactAction.doImgCheck start");
		
		WipMapper wDao = conn.getMapper(WipMapper.class);

		String ids = req.getParameter("ids");
		String[] split = ids.split(",");
		for (int i = 0; i < split.length; i++) {
			ProductionFeatureEntity entity = new ProductionFeatureEntity();
			entity.setMaterial_id(split[i]);
			entity.setPosition_id("15");
			entity.setSection_id("00000000009"); // 固定为报价物料课
			entity.setPace(0);
			entity.setOperate_result(0);
			entity.setRework(0);
			productionFeatureService.insert(entity, conn);

			MaterialEntity mBean  = new MaterialEntity();
			mBean.setMaterial_id(split[i]);
			wDao.warehousing(mBean);
		}
		
		log.info("MaterialFactAction.doImgCheck end");
	}
	/**
	 * 发布到CCD盖玻璃
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doCCDChange(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("MaterialFactAction.doCCDChange start");

		MaterialFactService mfService = new MaterialFactService();
		String ids = req.getParameter("ids");
		String[] split = ids.split(",");
		for (int i = 0; i < split.length; i++) {
//			ProductionFeatureEntity entity = new ProductionFeatureEntity();
//			entity.setMaterial_id(split[i]);
//			entity.setPosition_id("25");
//			entity.setSection_id("00000000003"); // 固定为新2课
//			entity.setPace(0);
//			entity.setOperate_result(0);
//			entity.setRework(0);
//			productionFeatureService.insert(entity, conn);
			mfService.assginCCDChange(split[i], conn);
		}
		
		log.info("MaterialFactAction.doCCDChange end");
	}

	/**
	 * 实行投线
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doInline(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("MaterialFactAction.doInline start");

		// Ajax回馈对象	
		Map<String, Object> callResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// CCD 更换线长确认 Fixed 移到302工位完成前
//		String ccdChange = req.getParameter("ccd_change");
//		if ("1".equals(ccdChange)) {
//			ProductionFeatureService pfService = new ProductionFeatureService();
//			String sGot = pfService.checkLpi(req.getParameter("material_id"), "302", conn);
//			if (sGot == null) {
//				MsgInfo error = new MsgInfo();
//				error.setComponentid("material_id");
//				error.setErrcode("info.linework.uncheckedCcd");
//				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.uncheckedCcd"));
//				errors.add(error);
//			}
//		}

		if (errors.size() == 0) {
			LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

			// 更新到现品作业记录（维修品）
			FactMaterialService fmsService = new FactMaterialService();
			fmsService.insertFactMaterial(user.getOperator_id(), req.getParameter("material_id"), 1, conn);

			materialFactService.updateInline(form, user.getOperator_id(), conn);
		}

		// 检查发生错误时报告错误信息
		callResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callResponse);

		log.info("MaterialFactAction.doInline end");
	}

	/**
	 * 原先的report方法
	 * /
	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("MaterialFactAction.report start");
		
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		List<MaterialFactForm> lResultForm = materialFactService.searchInlineMaterial(conn);
        materialFactService.createReport(conn);
		String fileName = null;
		String filePath = ReportUtils.createReport(lResultForm, ReportMetaData.factMaterialTitles, ReportMetaData.factMaterialColNames);
		listResponse.put("fileName", fileName);
		
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("MaterialFactAction.report end");
	}*/
	
 /**
  * 报表导出（更改之后的方法）
  * @param mapping
  * @param form
  * @param request
  * @param response
  * @param conn
  * @throws Exception
  */
	 public void report(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
			log.info("MaterialFactAction.report.start");
			Map<String,Object> listResponse = new HashMap<String,Object>();
			String filePath = materialFactService.createReport(conn);		
			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   
			//导出的文件的文件名
			String fileName =sdf.format(dt)+"投线一览.xls";
			listResponse.put("fileName", fileName);
			listResponse.put("filePath",filePath);	
			returnJsonResponse(response, listResponse);
			log.info("MaterialFactAction.report.end");
	    }
	/**
	 * 导出文件（原先和report配合使用的方法）
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	/*public void export(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		String filePath = req.getParameter("filePath");
		String fileName = new String("2日内投线一览.xls");
		
		DownloadService dservice = new DownloadService();
		dservice.writeFile(res, DownloadService.CONTENT_TYPE_EXCEL, fileName, filePath);
	}*/
   
	/**
	 * 取得维修流程详细信息处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 * @author 龚镭敏
	 */
	@Privacies(permit={2, 0})
	public void getPa(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("MaterialFactAction.getPa start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 小修理报价时,取得CCD对象机型
		String checkCcd = req.getParameter("lf_model_id");

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();
		ProcessAssignService paService = new ProcessAssignService();

		if (errors.size() == 0) {

			// 查询流程明细
			List<ProcessAssignForm> l = paService.getAssigns(req.getParameter("id"), conn, errors);

			// 查询结果放入Ajax响应对象
			listResponse.put("processAssigns", l);

			if (checkCcd != null) {
				Set<String> ccdModels = RvsUtils.getCcdModels(conn);
				if (ccdModels != null && ccdModels.contains(checkCcd)) {
					listResponse.put("isCcdModel", true);
				}

				// LG 目镜对应机型
				ModelService ms = new ModelService();
				ModelForm model = ms.getDetail(checkCcd, conn);
				if (model.getKind().equals("01")) {
					listResponse.put("isLgModel", true);
				}
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialFactAction.getPa end");

	}
	@Privacies(permit={2, 0})
	public void getPaByMaterial(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("MaterialFactAction.getPaByMaterial start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 小修理报价时,取得CCD对象机型
		String material_id = req.getParameter("material_id");

		MaterialProcessAssignService paService = new MaterialProcessAssignService();

		if (material_id != null) {

			// 查询流程明细
			List<ProcessAssignForm> l = paService.getAssigns(material_id, conn);

			// 查询结果放入Ajax响应对象
			listResponse.put("processAssigns", l);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialFactAction.getPaByMaterial end");

	}

	/**
	 * 加急
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doexpedite(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("MaterialFactAction.doexpedite start");
		
		// 取得所选维修对象信息
		MaterialService mService = new MaterialService();
		List<String> ids = mService.getIds(req.getParameterMap());

		materialFactService.updateExpedite(ids, conn);
		
		log.info("MaterialFactAction.doexpedite end");
	}

	public void checkPart(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("MaterialFactAction.checkPart start");
		MaterialPartialService mps = new MaterialPartialService();
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		String material_id = req.getParameter("material_id");
		MaterialPartialForm mp = mps.loadMaterialPartial(conn, material_id, null);
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		if (mp == null) { // if (mp == null || "9".equals(mp.getBo_flg())) {
			// 如果没订购零件不能结束
			// 如果没有任何发放不能结束
			MsgInfo info = new MsgInfo();
			info.setErrcode("info.partial.lineWaiting");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.withoutOrder"));
			infoes.add(info);
		}
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("MaterialFactAction.checkPart end");
	}

	public void showInlinePlan(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("MaterialFactAction.showInlinePlan start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		MaterialFactService service = new MaterialFactService();
		callbackResponse.put("materials", service.getInlinePlan(conn));
	
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("MaterialFactAction.showInlinePlan end");
	}

	public void doChangeinlinePlan(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("MaterialFactAction.doChangeinlinePlan start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		MaterialFactService service = new MaterialFactService();
		service.changeinlinePlan(form, conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("MaterialFactAction.doChangeinlinePlan end");
	}

	/**
	 * 批量入库
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doBatchInline(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("MaterialFactAction.doBatchInline start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		MaterialFactService service = new MaterialFactService();
		String ids = req.getParameter("ids");
		if (ids != null) {
			String[] material_ids = ids.split(",");

			service.doBatchInline(material_ids, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("MaterialFactAction.doBatchInline end");
	}

	/**
	 * 报表导出（投线单）
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void reportInlinePlan(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("MaterialFactAction.reportInlinePlan.start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		String filePath = materialFactService.createInlineReport(conn);

		// 导出的文件的文件名
		String fileName = "投线单.xls";
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		returnJsonResponse(response, listResponse);
		log.info("MaterialFactAction.reportInlinePlan.end");
	}
}
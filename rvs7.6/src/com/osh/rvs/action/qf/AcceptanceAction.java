/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：维修对象机种系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.qf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.qf.FactMaterialEntity;
import com.osh.rvs.bean.qf.FactReceptMaterialEntity;
import com.osh.rvs.bean.qf.TurnoverCaseEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.qf.FactReceptMaterialForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.CustomerService;
import com.osh.rvs.service.DownloadService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.UserDefineCodesService;
import com.osh.rvs.service.qf.AcceptanceService;
import com.osh.rvs.service.qf.FactMaterialService;
import com.osh.rvs.service.qf.FactReceptMaterialService;
import com.osh.rvs.service.qf.TurnoverCaseService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;


public class AcceptanceAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private AcceptanceService service = new AcceptanceService();
	private ModelService modelService = new ModelService();
	private ProductionFeatureService featureService = new ProductionFeatureService();
	
	/**
	 * 受理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("AcceptanceAction.init start");
		
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String mReferChooser = modelService.getOptions(conn);
		session.setAttribute("mReferChooser", mReferChooser);
		
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			req.setAttribute("role", "manager");
		}else{
			req.setAttribute("role", "none");
		}
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("AcceptanceAction.init end");
	}
	
	public void checkModelDepacy(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn){
		log.info("AcceptanceAction.checkModelDepacy start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("level", "ocm");
		v.add("ocm", v.integerType());
		v.add("level", v.integerType());
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		MaterialForm materialForm = (MaterialForm)form;
		MaterialService mservice = new MaterialService();
		
		if (errors.size() == 0) {
			List<MsgInfo> messages = new ArrayList<MsgInfo>();
			mservice.checkModelDepacy(materialForm, conn, messages);
			callbackResponse.put("messages", messages);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AcceptanceAction.checkModelDepacy end");
	}

	/**
	 * 受理添加记录实行处理
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AcceptanceAction.doinsert start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("level", "ocm");
		v.add("ocm", v.integerType());
		v.add("level", v.integerType());
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		MaterialForm materialForm = ((MaterialForm)form);
		String id = materialForm.getMaterial_id();
		MaterialService mservice = new MaterialService();

//		mservice.checkModelDepacy(materialForm, conn, errors);

		mservice.checkRepeatNo(id, form, conn, errors);
		
//		String ocm = materialForm.getOcm();
//		String value = CodeListUtils.getValue("material_ocm", ocm);
//		if ("OCM-SHRC".equals(value)) {
//			materialForm.setAm_pm("1");
//		} else {
//			materialForm.setAm_pm("2");
//		}
		materialForm.setAm_pm("2");

		if (errors.size() == 0) {
			if ("".equals(id) || id == null) {
				service.insert(form, false, conn, errors);
			} else {
				service.update(form, req.getSession(), conn, errors);
			}
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AcceptanceAction.doinsert end");
	}

	/**
	 * 批量数据实际导入
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doimport(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AcceptanceAction.doimport start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		service.batchinsert(req.getParameterMap(), req.getSession(), conn, errors);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AcceptanceAction.doimport end");
	}	
	/**
	 * 取得维修对象详细信息
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void loadData(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("AcceptanceAction.loadData start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MaterialForm> lResultForm = service.getMaterialDetail(conn);
		
		// 查询结果放入Ajax响应对象
		listResponse.put("list", lResultForm);
		
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("AcceptanceAction.loadData end");
	}
	
	/**
	 * 实际受理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doAccept(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("AcceptanceAction.doAccept start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		service.accept(req.getParameterMap(), req.getSession(), conn, errors);

		if (errors.size() == 0) {
			conn.commit();
			AcceptFactService afService = new AcceptFactService();
			LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
			afService.fingerOperatorRefresh(user.getOperator_id());
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AcceptanceAction.doAccept end");
	}

	/**
	 * 发送至消毒
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doDisinfection(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("AcceptanceAction.doDisinfection start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String ids = req.getParameter("ids");
		String[] split = ids.split(",");
//		String test_ids = req.getParameter("test_ids");
//		String[] test_split = new String[0];
//		if (!CommonStringUtil.isEmpty(test_ids)) {
//			test_split = test_ids.split(",");
//		}

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();

		// 测漏
//		AcceptFactService afService = new AcceptFactService();
//		FactMaterialService factService = new FactMaterialService();
//		Map<Integer, String> pfkeyMap = new HashMap<Integer, String>();
//		for (int i = 0; i < test_split.length; i++) {
//			String[] temp = test_split[i].split("_");
//			String material_id = temp[0];
//			String fix_type = temp[1];
//			int production_type = 102;
//			if ("3".equals(fix_type)) {
//				production_type = 105;
//			}
//
//			String insertId = "";
//			if (!pfkeyMap.containsKey(production_type)) {
//				AfProductionFeatureEntity afEntity = afService.getUnspecUnfinishByType(production_type, conn);
//				if (afEntity == null) {
//					Calendar cal = Calendar.getInstance();
//					cal.set(Calendar.HOUR_OF_DAY, 1);
//					cal.set(Calendar.MINUTE, 2);
//					cal.set(Calendar.SECOND, 0);
//	
//					afEntity = new AfProductionFeatureEntity();
//					afEntity.setProduction_type(production_type);
//					afEntity.setOperator_id("0");
//					afEntity.setAction_time(cal.getTime());
//					afService.insert(afEntity, conn);
//	
//					CommonMapper comMapper = conn.getMapper(CommonMapper.class);
//					insertId = comMapper.getLastInsertID();
//				} else {
//					insertId = afEntity.getAf_pf_key();
//				}
//				pfkeyMap.put(production_type, insertId);
//			} else {
//				insertId = pfkeyMap.get(production_type);
//			}
//
//			FactMaterialForm factForm = new FactMaterialForm();
//			factForm.setAf_pf_key(insertId);
//			factForm.setMaterial_id(material_id);
//			factService.insert(factForm, conn);
//		}

		MaterialTagService mtService = new MaterialTagService();

		List<String> sentMessage = new ArrayList<String>();
		// 发送至消毒或灭菌
		for (int i = 0; i < split.length; i++) {
			String material_id = split[i];

			String disinectPositionId = mtService.getDisinectFlow(material_id, conn);

			if (disinectPositionId == null) {
				sentMessage.add(material_id);
			} else {
				ProductionFeatureEntity entity = new ProductionFeatureEntity();
				entity.setMaterial_id(material_id);
				entity.setPosition_id(disinectPositionId);
				entity.setSection_id(section_id);
				entity.setPace(0);
				entity.setOperate_result(0);
				entity.setRework(0);
				featureService.insert(entity, conn);
				sentMessage.add(material_id + "_" + disinectPositionId);
			}
		}

		callbackResponse.put("sentMessage", sentMessage);

		// 发送完毕后，受理时间覆盖导入时间 rvs8 因导入实物受理功能取消
		// service.updateFormalReception(split, conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AcceptanceAction.doAccept end");
	}
	
	/**
	 * 发送至灭菌
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doSterilization(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		String ids = req.getParameter("ids");
	
		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();

		String[] split = ids.split(",");
		for (int i = 0; i < split.length; i++) {
		
			ProductionFeatureEntity entity = new ProductionFeatureEntity();
			entity.setMaterial_id(split[i]);
			entity.setPosition_id("11");
			entity.setPace(0);
			entity.setSection_id(section_id);
			entity.setOperate_result(0);
			entity.setRework(0);
			featureService.insert(entity, conn);
		}

		// 发送完毕后，受理时间覆盖导入时间
//		service.updateFormalReception(split, conn);
	}

	/**
	 * 小票打印
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doPrintTicket(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{ 
		log.info("AcceptanceAction.printTicket start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 取得所选维修对象信息
		MaterialService mService = new MaterialService();
		List<String> ids = mService.getIds(req.getParameterMap());
		List<MaterialEntity> mBeans = mService.loadMaterialDetailBeans(ids, conn);

		DownloadService dService = new DownloadService();
		String filename = dService.printTickets(mBeans, conn);
		callbackResponse.put("tempFile", filename);

		// 更新维修对象小票打印标记
		MaterialMapper mdao = conn.getMapper(MaterialMapper.class);
		mdao.updateMaterialTicket(ids);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 更新到现品作业记录（维修品）
		FactMaterialService fmsService = new FactMaterialService();
		int updateCount = 0;
		for (MaterialEntity mEntity : mBeans) {
			updateCount += fmsService.insertFactMaterial(user.getOperator_id(), mEntity.getMaterial_id(), 1, conn);
		}

		// 分配库位
		conn.commit();
		for (MaterialEntity mEntity : mBeans) {
			HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
			httpclient.start();
			try {  
				HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/assign_tc_space/" 
					+ mEntity.getMaterial_id() + "/" + mEntity.getKind() + "/"  
					+ mEntity.getFix_type() + "/"  + mEntity.getUnrepair_flg()); // with_case as unrepair_flg 
				httpclient.execute(request, null);
			} catch (Exception e) {
			} finally {
				Thread.sleep(80);
				httpclient.shutdown();
			}
		}

		if (updateCount > 0) {
			AcceptFactService afService = new AcceptFactService();
			afService.fingerOperatorRefresh(user.getOperator_id());
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("AcceptanceAction.printTicket end");
	}

	/**
	 * 生成日报表
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{ 
		log.info("AcceptanceAction.report start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		DownloadService dService = new DownloadService();
		// 建立下载用文件
		String tempFile = dService.createAcceptanceWorkReport(conn);
		callbackResponse.put("tempFile", tempFile);

		Date today = new Date();
		String fileName = "QR-B31002-59 内镜受理记录表-" + DateUtil.toString(today, DateUtil.ISO_DATE_PATTERN);
		fileName += ".xls";

		callbackResponse.put("fileName", fileName);
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("AcceptanceAction.report end");
	}

	/**
	 * 未修理发还
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doReturn(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{ 
		log.info("AcceptanceAction.return start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 取得所选维修对象信息
		MaterialService mService = new MaterialService();
		List<String> ids = mService.getIds(req.getParameterMap());

		// 受理处返还，不需要出货
		service.acceptReturn(ids, conn);
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("AcceptanceAction.return end");
	}

	/**
	 * 取得维修对象详细信息
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void getAutoComplete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("CustomerAction.getAutoComplete start"); // TODO CustomerAction
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		CustomerService service = new CustomerService();
		List<String> list = service.getAutoComplete(conn);
		
		// 查询结果放入Ajax响应对象
		listResponse.put("customers", list);

		// 配送区域信息
		listResponse.put("opt_bound_out_ocm", CodeListUtils.getGridOptions("material_direct_ocm"));

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("CustomerAction.getAutoComplete end");
	}

	/**
	 * 取得空闲的通箱库位一览信息
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={107})
	public void getTcLoad(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("AcceptanceAction.getTcLoad start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		TurnoverCaseService service = new TurnoverCaseService();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 查询预计的入库位信息
		try {
			UserDefineCodesService udcService = new UserDefineCodesService();
			String prePrintFormal = udcService.searchUserDefineCodesValueByCode("TC_PREPRINT_FORMAL", conn);
			String prePrintEndoeye = udcService.searchUserDefineCodesValueByCode("TC_PREPRINT_ENDOEYE", conn);
			String prePrintUdi = udcService.searchUserDefineCodesValueByCode("TC_PREPRINT_UDI", conn);

			int iPrePrintFormal = 25;
			int iPrePrintEndoeye = 2;
			int iPrePrintUdi = 10;
			try {
				iPrePrintFormal = Integer.parseInt("" + prePrintFormal);
				iPrePrintEndoeye = Integer.parseInt("" + prePrintEndoeye);
				iPrePrintUdi = Integer.parseInt("" + prePrintUdi);
			} catch (NumberFormatException e) {
			}

			List<TurnoverCaseEntity> nextLocations = service.getEmptyLocations("0", iPrePrintFormal, conn);
			List<TurnoverCaseEntity> nextEndoeyeLocations = service.getEmptyLocations("06", iPrePrintEndoeye, conn);
			List<TurnoverCaseEntity> nextUdiLocations = service.getEmptyLocations("738", iPrePrintUdi, conn);

			listResponse.put("iPrePrintFormal", iPrePrintFormal);
			listResponse.put("iPrePrintEndoeye", iPrePrintEndoeye);
			listResponse.put("iPrePrintUdi", iPrePrintUdi);

			listResponse.put("nextLocations", nextLocations);
			listResponse.put("nextEndoeyeLocations", nextEndoeyeLocations);
			listResponse.put("nextUdiLocations", nextUdiLocations);
		} catch (Exception e) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("location");
			error.setErrmsg("剩余空闲的通箱库位已经不足。请先处理出库。");
			errors.add(error);
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("AcceptanceAction.getTcLoad end");
	}

	@Privacies(permit={107})
	public void regainTcLabels(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("AcceptanceAction.regainTcLabels start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		TurnoverCaseService service = new TurnoverCaseService();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String kind = req.getParameter("kind");
		String count = req.getParameter("count");

		int iCount = 5;

		try {
			iCount = Integer.parseInt("" + count);
		} catch (NumberFormatException e) {
		}

		// 查询预计的入库位信息
		try {
			switch (kind) {
			case "formal" : {
				List<TurnoverCaseEntity> nextLocations = service.getEmptyLocations("0", iCount, conn);
				listResponse.put("nextLocations", nextLocations);
				break;
			}
			case "endoeye" : {
				List<TurnoverCaseEntity> nextLocations = service.getEmptyLocations("06", iCount, conn);
				listResponse.put("nextLocations", nextLocations);
				break;
			}
			case "udi" : {
				List<TurnoverCaseEntity> nextLocations = service.getEmptyLocations("738", iCount, conn);
				listResponse.put("nextLocations", nextLocations);
				break;
			}
			}
		} catch (Exception e) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("location");
			error.setErrmsg("剩余空闲的通箱库位已经不足。请先处理出库。");
			errors.add(error);
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("AcceptanceAction.regainTcLabels end");
	}
	
	@Privacies(permit = { 107 })
	public void doPrintTcLabels(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("AcceptanceAction.printTcLabels start");
		// Ajax回馈对象
		Map<String, Object> cbResponse = new HashMap<String, Object>();

		String labels = req.getParameter("labels");
		TurnoverCaseService service = new TurnoverCaseService();

		String path = service.printLabels(labels);

		String passMessage = service.setToPrepare(labels, conn);

		cbResponse.put("path", path);
		cbResponse.put("passMessage", passMessage);

		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);
		log.info("AcceptanceAction.printTcLabels end");
	}

	@Privacies(permit = { 107 })
	public void getMatch(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("AcceptanceAction.getMatch start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 取得没有实物受理记录的维修品一览
		List<MaterialForm> mBeans = service.getReceptionsWithoutFact(conn);
		listResponse.put("receptionsWithoutFact", mBeans);

		// 取得临时登录的实物受理品一览
		FactReceptMaterialService frmService = new FactReceptMaterialService();
		List<FactReceptMaterialForm> tempList = frmService.searchFactReceptMaterialTemp(null, conn);
		listResponse.put("receptionsTemp", tempList);

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("AcceptanceAction.getMatch end");
	}

	@Privacies(permit = { 107 })
	public void doFactmatch(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("AcceptanceAction.doFactmatch start");
		// Ajax回馈对象
		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("material_id");
		String fact_recept_id = req.getParameter("fact_recept_id");

		FactReceptMaterialService frmService = new FactReceptMaterialService();
		FactReceptMaterialEntity factReceptTempEntity = frmService.getFactReceptTemp(fact_recept_id, conn);

		if (factReceptTempEntity == null) {
			MsgInfo info = new MsgInfo();
			info.setComponentid("fact_recept_id");
			info.setErrmsg("此实物受理临时记录不存在，或者已经被匹配。");
			errors.add(info);
		} else {
			// 判断下是否操作了系统记录
			FactMaterialService fmService = new FactMaterialService();
			List<FactMaterialEntity> l = fmService.checkOnMaterialAndType(material_id, 102, conn);
			if (l.size() > 0) {
				MsgInfo info = new MsgInfo();
				info.setComponentid("material_id");
				info.setErrmsg("此系统受理记录已经处理了实物受理。请选择其他操作。");
				errors.add(info);
			} else {
				frmService.tempMatch(material_id, factReceptTempEntity, conn);
			}
		}

		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);
		log.info("AcceptanceAction.doFactmatch end");
	}

	public void getPlanTarget(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn){
		log.info("AcceptanceAction.getPlanTarget start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		callbackResponse.put("plan_target", service.getPlanTarget(conn));
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AcceptanceAction.getPlanTarget end");
	}

	@Privacies(permit = { 107 })
	public void doSetPlanTarget(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("AcceptanceAction.doSetPlanTarget start");
		// Ajax回馈对象
		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String plan_target = req.getParameter("plan_target");
		if (CommonStringUtil.isEmpty(plan_target)) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("plan_target");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "当日备品到货数"));
			errors.add(error);
		} else {
			if (plan_target.length() > 3) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("plan_target");
				error.setErrcode("validator.invalidParam.invalidMaxLengthValue");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMaxLengthValue", "当日备品到货数"));
				errors.add(error);
			} else {
				Integer iPlanTarget = null;
				
				try {
					iPlanTarget = Integer.parseInt(plan_target);
				} catch (Exception e) {
					MsgInfo error = new MsgInfo();
					error.setComponentid("plan_target");
					error.setErrcode("validator.invalidParam.invalidNumberValu");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidNumberValue", "当日备品到货数"));
					errors.add(error);
				}

				if (errors.size() == 0) {
					service.updateSparePlan(iPlanTarget, conn);
				}
			}
		}

		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);
		log.info("AcceptanceAction.doSetPlanTarget end");
	}
}

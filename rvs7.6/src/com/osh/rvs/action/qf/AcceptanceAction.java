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
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.service.CustomerService;
import com.osh.rvs.service.DownloadService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.qf.AcceptanceService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
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
				service.insert(form, req.getSession(), conn, errors);
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
		String ids = req.getParameter("ids");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();

		String[] split = ids.split(",");
		for (int i = 0; i < split.length; i++) {
			ProductionFeatureEntity entity = new ProductionFeatureEntity();
			entity.setMaterial_id(split[i]);
			entity.setPosition_id("10");
			entity.setSection_id(section_id);
			entity.setPace(0);
			entity.setOperate_result(0);
			entity.setRework(0);
			featureService.insert(entity, conn);
		}

		// 发送完毕后，受理时间覆盖导入时间
		service.updateFormalReception(split, conn);
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
		service.updateFormalReception(split, conn);
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

		// 分配库位
		conn.commit();
		for (MaterialEntity mEntity : mBeans) {
			HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
			httpclient.start();
			try {  
				HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/assign_tc_space/" 
					+ mEntity.getMaterial_id() + "/" + mEntity.getKind() + "/");
				httpclient.execute(request, null);
			} catch (Exception e) {
			} finally {
				Thread.sleep(80);
				httpclient.shutdown();
			}
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
	
}

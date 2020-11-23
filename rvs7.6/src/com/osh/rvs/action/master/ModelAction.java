/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：维修对象型号系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.master;

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

import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.form.master.ModelForm;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.ProcessAssignService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class ModelAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 维修对象型号系统管理处理
	 */
	private ModelService service = new ModelService();

	/**
	 * 设备类别管理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ModelAction.init start");

		CategoryService cservice = new CategoryService();
		// 取得下拉框信息
		String cOptions = cservice.getWithSpareOptions(conn);
		req.setAttribute("cOptions", cOptions);
		
		//等级
		req.setAttribute("goMaterial_level", CodeListUtils.getGridOptions("material_level"));
		//梯队
		req.setAttribute("goEchelon_code", CodeListUtils.getGridOptions("echelon_code"));
		req.setAttribute("sEchelon_code", CodeListUtils.getSelectOptions("echelon_code", null, ""));

		// 取得工程
		LineService lineService = new LineService();
		String lOptions = lineService.getInlineOptions(conn);
		req.setAttribute("lOptions", lOptions);

		// 迁移到页面
		ProcessAssignService paSevice = new ProcessAssignService();
		req.setAttribute("patOptions", paSevice.getOptions("(型号无特有流程)", conn));

		actionForward = mapping.findForward(FW_INIT);

		log.info("ModelAction.init end");
	}

	/**
	 * 设备类别查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ModelAction.search start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		for (MsgInfo error : errors) {
			log.warn("error=" + error.getErrmsg());
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ModelForm> lResultForm = service.search(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ModelAction.search end");
	}

	/**
	 * 取得详细信息处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ModelAction.detail start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 查询记录
			ModelForm resultForm = service.getDetail(form, conn, errors);
			 List<ModelForm> responseList=service.searchAbolishOfModelLevel(form, conn);
			if (resultForm != null) {
				// 查询结果放入Ajax响应对象
				listResponse.put("modelForm", resultForm);
				listResponse.put("responseList", responseList);
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ModelAction.detail end");
	}

	public void getautocomp(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ModelAction.getautocomp start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 查询记录
		Map<String, String[]> resultMap = service.getAutocomp(conn, errors);

		// 查询结果放入Ajax响应对象
		listResponse.put("autocomp", resultMap);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ModelAction.getautocomp end");
	}

	/**
	 * 设备型号数据新建登录实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{

		log.info("ModelAction.doinsert start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		service.customValidate(form, conn, errors);

		if (errors.size() == 0) {
			// 执行插入
			service.insert(form, req.getSession(), conn, errors);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("ModelAction.doinsert end");
	}

	/**
	 * 设备型号数据更新实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("ModelAction.doupdate start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		service.customValidate(form, conn, errors);

		if (errors.size() == 0) {
			// 执行更新
			service.update(form, req.getSession(), conn, errors);

			// 重新设定型号记录
			ReverseResolution.clearModelEntityRever();
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("ModelAction.doupdate end");
	}

	/**
	 * 设备型号数据删除实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("ModelAction.dodelete start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 删除记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行删除
			service.delete(form, req.getSession(), conn, errors);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("ModelAction.dodelete end");
	}
	
	/**
	 * 更新受理时间(终止,起效)
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void doUpdateAvaliableEndDate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn){
		log.info("ModelAction.doUpdateAvaliableEndDate start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		//表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();
		
		if(errors.size()==0){
			String flg=req.getParameter("flg");
			if("1".equals(flg)){//终止
				int status= service.searchExitsModelLevelSetHistory(form, conn, errors);
				if(errors.size()==0){//填写了 停止修理日期
					if(status==1){
						service.updateAvaliableEndDate(form,req, conn);
					}else{
						service.updateAvaliableEndDate(form,req, conn);
						service.insertModelLevelSetHistory(form, req, conn);
					}
				}
			}else{//起效
				service.updateAvaliableEndDate(form,req, conn);
			}
			
		}
		
		if(errors.size()==0){
			List<ModelForm> responseList=service.searchAbolishOfModelLevel(form, conn);
			callbackResponse.put("responseList", responseList);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ModelAction.doUpdateAvaliableEndDate end");
	}
	
	/**
	 * 追加
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void doInsertMoldeLevel(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn){
		log.info("ModelAction.doInsertMoldeLevel start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		//表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();
		
		if(errors.size()==0){
			service.insertMoldeLevel(form, req, conn);
			List<ModelForm> responseList=service.searchAbolishOfModelLevel(form, conn);
			callbackResponse.put("responseList", responseList);
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ModelAction.doInsertMoldeLevel start");
	}
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void change(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn)throws Exception{
		log.info("ModelAction.change start");
		
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		//表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();
		if(errors.size()==0){
			List<ModelForm> responseList=service.searchAbolishOfModelLevel(form, conn);
			callbackResponse.put("responseList", responseList);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ModelAction.change end");
	}
	
	/**
	 * 改变梯队
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doChangeEchelon(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn)throws Exception{
		log.info("ModelAction.doChangeEchelon start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			 service.changeEchelon(form, conn);
			 List<ModelForm> responseList=service.searchAbolishOfModelLevel(form, conn);
			 listResponse.put("responseList", responseList);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		
		log.info("ModelAction.doChangeEchelon end");
	}

	/**
	 * 型号快查取得
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void getModelDictionary(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ModelAction.getModelDictionary start");
		// Ajax回馈对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		callbackResponse.put("modelDict", service.getModelDictionary(conn));

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("ModelAction.getModelDictionary end");
	}
}

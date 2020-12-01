package com.osh.rvs.action.report;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.apache.struts.upload.FormFile;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.report.ProcedureManualForm;
import com.osh.rvs.service.report.ProcedureManualService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class ProcedureManualAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	/**
	 * 初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("ProcedureManualAction.init start");

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		if (user.getPrivacies().contains(RvsConsts.PRIVACY_TECHNICAL_MANAGE) // 技术文档管理;
				|| user.getPrivacies().contains(RvsConsts.PRIVACY_ADMIN)) { // 系统管理;
			req.setAttribute("tech", "yes");
		}

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ProcedureManualAction.init end");
	}

	/**
	 * 作业要领书查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ProcedureManualAction.search start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			ProcedureManualService service = new ProcedureManualService();

			LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

			List<ProcedureManualForm> lResultForm = service.search(form, user, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ProcedureManualAction.search end");
	}

	/**
	 * 提交上传作业要领书文件
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void doFilePost(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn){
		log.info("ProcedureManualAction.doFilePost start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		//表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		ProcedureManualForm procedureManualForm = (ProcedureManualForm) form;

		FormFile file = procedureManualForm.getFile();
		if (file == null || CommonStringUtil.isEmpty(file.getFileName())) {
			if (procedureManualForm.getProcedure_manual_id() == null) {
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.notExist");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.notExist"));
				errors.add(error);
			}
		} else {
			String fileName = file.getFileName();
			//判断上传附表是否是pdf格式的
			if(!fileName.endsWith(".pdf")){
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidType");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidType"));
				errors.add(error);
			} else {
				try {
					byte[] fileData = file.getFileData();
					byte[] bHead = new byte[10];
					System.arraycopy(fileData, 0, bHead, 0, 10);

					String conv = new String(bHead);
					if (!conv.startsWith("%PDF-")) {
						MsgInfo error = new MsgInfo();
						error.setErrcode("file.invalidType");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidType"));
						errors.add(error);
					}
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
					MsgInfo error = new MsgInfo();
					error.setErrcode("file.invalidType");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidType"));
					errors.add(error);
				}
			}
		}

		if(errors.size()==0){
			ProcedureManualService service = new ProcedureManualService();
			String id = service.updateProcedureManual(procedureManualForm, req.getSession(), conn);

			// 保存文件
			if (id != null) {
				service.fileStorage(id, file);
			}
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ProcedureManualAction.doFilePost end");
	}

	/**
	 * 删除作业要领书
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void doRemove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn){
		log.info("ProcedureManualAction.doRemove start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		//表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		ProcedureManualForm procedureManualForm = (ProcedureManualForm) form;

		if(errors.size()==0){
			ProcedureManualService service = new ProcedureManualService();
			service.removeProcedureManual(procedureManualForm.getProcedure_manual_id(), conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ProcedureManualAction.doRemove end");
	}

	/**
	 * 设定作业要领书书单
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 */
	public void doSetBooklist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn){
		log.info("ProcedureManualAction.doSetBooklist start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		//表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if(errors.size()==0){
			ProcedureManualService service = new ProcedureManualService();
			service.setBooklist(form, req.getSession(), conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ProcedureManualAction.doSetBooklist end");
	}

	/**
	 * 菜单书单取得
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void header(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("ProcedureManualAction.header start");

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		ProcedureManualService service = new ProcedureManualService();
		service.writeHeaderMenuResponse(res, user.getBooks());

		log.info("ProcedureManualAction.header end");
	}
}

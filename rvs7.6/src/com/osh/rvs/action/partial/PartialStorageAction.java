package com.osh.rvs.action.partial;

import static com.osh.rvs.service.UploadService.toXls2003;

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

import com.osh.rvs.bean.partial.PartialStorageEntity;
import com.osh.rvs.form.partial.PartialStorageForm;
import com.osh.rvs.service.UploadService;
import com.osh.rvs.service.partial.PartialStorageService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class PartialStorageAction extends BaseAction {

	private Logger logger = Logger.getLogger(getClass());

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		logger.info("PartialStorageAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		logger.info("PartialStorageAction.init end");
	}

	/**
	 * 上传基准值设定
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUploadStorage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		logger.info("PartialUploadAction.doUploadStorage start");

		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		// 文件名称
		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);

		// 转换2003格式
		if (tempfilename.endsWith(".xlsx")) {
			tempfilename = toXls2003(tempfilename);
		}

		if (errors.size() == 0) {
			PartialStorageService service = new PartialStorageService();
			PartialStorageEntity entity = new PartialStorageEntity();
			BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
			service.readUploadFile(tempfilename, entity, conn, errors);
		}

		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		logger.info("PartialUploadAction.doUploadStorage end");
	}

	/**
	 * 按日期查询零件库存内容
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		logger.info("PartialStorageAction.search start");

		/* Ajax回馈对象 */
		Map<String, Object> callResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PartialStorageService service = new PartialStorageService();
		PartialStorageEntity entity = new PartialStorageEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 查询 */
		List<PartialStorageEntity> slist = service.searchByDate(entity, conn);

		List<PartialStorageForm> ret = new ArrayList<PartialStorageForm>();
		BeanUtil.copyToFormList(slist, ret, CopyOptions.COPYOPTIONS_NOEMPTY, PartialStorageForm.class);
		/* 查询数据保存 */
		callResponse.put("partialStorageList", ret);

		/* errors */
		callResponse.put("errors", errors);
		returnJsonResponse(response, callResponse);

		logger.info("PartialStorageAction.search end");
	}
	
	/**
	 * 更新数量
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn	数据库会话
	 * @throws Exception
	 */
	public void doUpdateQuantity(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,SqlSessionManager conn)throws Exception{
		logger.info("PartialStorageAction.doUpdateQuantity start");
		
		/* Ajax回馈对象 */
		Map<String, Object> callResponse = new HashMap<String, Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();
		PartialStorageService service = new PartialStorageService();

		if (errors.size() == 0) {
			service.updateQuantity(form, conn);
		}

		// 检查发生错误时报告错误信息
		callResponse.put("errors", errors);
		
		returnJsonResponse(res, callResponse);
		
		logger.info("PartialStorageAction.doUpdateQuantity end");
	}

	/**
	 * 删除
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn	数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void doDelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		logger.info("PartialStorageAction.doDelete start");
		
		/* Ajax回馈对象 */
		Map<String, Object> callResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		PartialStorageService service = new PartialStorageService();

		service.delete(req, conn);

		/* errors */
		callResponse.put("errors", errors);
		returnJsonResponse(res, callResponse);

		logger.info("PartialStorageAction.doDelete end");
	}
}
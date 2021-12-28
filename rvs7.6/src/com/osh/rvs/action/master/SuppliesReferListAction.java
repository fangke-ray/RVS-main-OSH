package com.osh.rvs.action.master;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.master.SuppliesReferListForm;
import com.osh.rvs.service.SuppliesReferListService;
import com.osh.rvs.service.UploadService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.FileUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Description 常用采购清单
 * @author liuxb
 * @date 2021-11-30 上午9:45:26
 */
public class SuppliesReferListAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	
	private SuppliesReferListService service = new SuppliesReferListService();
	private UploadService uploadService = new UploadService();

	/**
	 * 画面初始表示处理
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesReferListAction.init start");
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("SuppliesReferListAction.init end");
	}
	
	/**
	 * 查询一览处理
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesReferListAction.search start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		List<SuppliesReferListForm> list = service.search(form, conn);
		listResponse.put("list", list);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
	
		log.info("SuppliesReferListAction.search end");
	}
	
	
	/**
	 * 新建登录实行处理
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesReferListAction.doInsert start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		// 表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("refer_key");
		
		List<MsgInfo> errors = v.validate();
		if (errors.size() == 0) {
			SuppliesReferListForm upfileForm = (SuppliesReferListForm) form;
			
			//检查是否存在相同【品名】并且【规格】为空的记录
			List<SuppliesReferListForm> list = service.searchEmptyModel(upfileForm, conn);
			if(list.size() !=0){
				MsgInfo error = new MsgInfo();
				error.setComponentid("model_name");
				error.setErrcode("info.supplies.modelName.exist.empty");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.supplies.modelName.exist.empty", upfileForm.getProduct_name()));
				errors.add(error);
			} else {
				// 取得上传的文件
				FormFile file = upfileForm.getFile();
				if (file != null && !CommonStringUtil.isEmpty(file.getFileName())) {
					String tempFilePath = uploadService.getFile2Local(form, errors);//单个文件
					
					File confFile = new File(tempFilePath);
					if (confFile.exists()) {
						boolean isImage = RvsUtils.isImage(confFile);
						if(!isImage){
							MsgInfo error = new MsgInfo();
							error.setComponentid("file");
							error.setErrcode("file.invalidType");
							error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidType"));
							errors.add(error);
						} else {
							UUID uuid = UUID.randomUUID();
							String tempfilename = uuid.toString().replaceAll("-", "");
							String targetPath = PathConsts.BASE_PATH + PathConsts.PHOTOS + "\\supplies_refer_list\\" + tempfilename;
							FileUtils.copyFile(tempFilePath, targetPath, true);
							
							upfileForm.setPhoto_uuid(tempfilename);
						}
					}
				}
				
				if (errors.size() == 0) {
					service.insert(upfileForm, conn);
				}
			}
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("SuppliesReferListAction.doInsert end");
	}
	
	/**
	 * 更新处理（数据）
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesReferListAction.doUpdate start");
		
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		// 表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		
		List<MsgInfo> errors = v.validate();
		if (errors.size() == 0) {
			SuppliesReferListForm suppliesReferListForm = (SuppliesReferListForm) form;
			
			//检查是否存在相同【品名】并且【规格】为空的记录
			List<SuppliesReferListForm> list = service.searchEmptyModel(suppliesReferListForm, conn);
			if(list.size() !=0){
				MsgInfo error = new MsgInfo();
				error.setComponentid("model_name");
				error.setErrcode("info.supplies.modelName.exist.empty");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.supplies.modelName.exist.empty", suppliesReferListForm.getProduct_name()));
				errors.add(error);
			} else {
				service.update(form, conn);
			}
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
	
		log.info("SuppliesReferListAction.doUpdate end");
	}
	
	/**
	 * 更新处理（照片上传）
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUploadImage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("SuppliesReferListAction.doUploadImage start");
		
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		// 表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("product_name");
		
		List<MsgInfo> errors = v.validate();
		if (errors.size() == 0) {
			// 1、传文件到临时目录
			String tempFilePath = uploadService.getFile2Local(form, errors);//单个文件
			
			if (errors.size() == 0) {
				File confFile = new File(tempFilePath);
				//判断图片上传是否成功
				if (confFile.exists()) {
					boolean isImage = RvsUtils.isImage(confFile);
					if(!isImage){
						MsgInfo error = new MsgInfo();
						error.setComponentid("file");
						error.setErrcode("file.invalidType");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidType"));
						errors.add(error);
					} else {
						SuppliesReferListForm upfileForm = (SuppliesReferListForm) form;
						
						String dirPath = PathConsts.BASE_PATH + PathConsts.PHOTOS + "\\supplies_refer_list\\";
						File dir = new File(dirPath);
						//判断目录是否存在
						if(dir.isDirectory()){
							//判断是否上传过图片
							String photoUuid = upfileForm.getPhoto_uuid();
							if(!CommonStringUtil.isEmpty(photoUuid)){
								File oldFile = new File(dirPath + photoUuid);
								//判断照片是否存在
								if(oldFile.exists()){
									//2、删除旧图片
									oldFile.delete();
								}
							}
						}
						
						//3、拷贝临时目录文件
						UUID uuid = UUID.randomUUID();
						String tempfilename = uuid.toString().replaceAll("-", "");
						String targetPath = dirPath + tempfilename;
						FileUtils.copyFile(tempFilePath, targetPath, true);
						
						//4、DB更新
						upfileForm.setPhoto_uuid(tempfilename);
						service.updatePhotoUUID(upfileForm, conn);
						
						callbackResponse.put("fileName", tempfilename);
					}
				} else {
					MsgInfo error = new MsgInfo();
					error.setErrcode("file.notExist");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.notExist"));
					errors.add(error);
				}
			}
		}
		
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
	
		log.info("SuppliesReferListAction.doUploadImage end");
	}
	
	/**
	 * 删除采购清单
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doDelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesReferListAction.doDelete start");
		
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		// 表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("product_name");
		
		List<MsgInfo> errors = v.validate();
		if(errors.size() == 0){
			SuppliesReferListForm suppliesReferListForm = service.getSuppliesRefer(form, conn);
			if(suppliesReferListForm != null){
				//1、删除照片
				String dirPath = PathConsts.BASE_PATH + PathConsts.PHOTOS + "\\supplies_refer_list\\";
				File dir = new File(dirPath);
				//判断目录是否存在
				if(dir.isDirectory()){
					//判断是否上传过图片
					String photoUuid = suppliesReferListForm.getPhoto_uuid();
					if(!CommonStringUtil.isEmpty(photoUuid)){
						File oldFile = new File(dirPath + photoUuid);
						//判断照片是否存在
						if(oldFile.exists()){
							oldFile.delete();
						}
					}
				}

				//2、删除DB数据
				service.delete(suppliesReferListForm.getRefer_key(), conn);
			} else {
				//数据不存在或者数据已经被删除
				MsgInfo error = new MsgInfo();
				error.setComponentid("refer_key");
				error.setErrcode("dbaccess.recordNotExist");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", ""));
				errors.add(error);
			}
		}
		
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
	
		log.info("SuppliesReferListAction.doDelete end");
	}
}
package com.osh.rvs.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.CheckFileManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReadInfect;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.XlsUtil;
import com.osh.rvs.form.master.CheckFileManageForm;
import com.osh.rvs.mapper.master.CheckFileManageMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.service
 * @ClassName: CheckFileManageService
 * @Description: 点检表管理Service
 * @author lxb
 * @date 2014-8-11 下午1:02:42
 * 
 */
public class CheckFileManageService {
	
	private static Logger log = Logger.getLogger(CheckFileManageService.class);

	/**
	 * 检索
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<CheckFileManageForm> search(ActionForm form, SqlSession conn) {
		CheckFileManageEntity entity = new CheckFileManageEntity();
		// 复制表单到数据对象
		CopyOptions cos = new CopyOptions();
		cos.fieldRename("manage_id", "devices_manage_id");
		BeanUtil.copyToBean(form, entity, cos);

		CheckFileManageMapper dao = conn.getMapper(CheckFileManageMapper.class);

		List<CheckFileManageEntity> entityList = dao.search(entity);
		List<CheckFileManageForm> formList = new ArrayList<CheckFileManageForm>();

		// 复制数据到表单对象
		BeanUtil.copyToFormList(entityList, formList, CopyOptions.COPYOPTIONS_NOEMPTY, CheckFileManageForm.class);

		return formList;
	}


	/**
	 * 删除
	 * 
	 * @param form
	 * @param conn
	 */
	public void delete(ActionForm form, SqlSessionManager conn) {
		CheckFileManageEntity entity = new CheckFileManageEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		CheckFileManageMapper dao = conn.getMapper(CheckFileManageMapper.class);
		dao.delete(entity);
	}

	/**
	 * 新建点检表
	 * 
	 * @param form
	 * @param conn
	 */
	public void insert(ActionForm form, HttpServletRequest request, SqlSessionManager conn,String fileName) {
		CheckFileManageEntity entity = new CheckFileManageEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		Integer access_place=entity.getAccess_place();
		if(access_place!=2){//日常/使用前    归档周期没意义
			entity.setCycle_type(0);//归档周期
		}

		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();// 最后更新人

		entity.setUpdated_by(operator_id);
		entity.setSheet_file_name(fileName);

		CheckFileManageMapper dao = conn.getMapper(CheckFileManageMapper.class);
		dao.insert(entity);

	}

	/**
	 * 检查点检表管理号是否已经存在
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public void checkManageCodeIsExist(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		CheckFileManageEntity entity = new CheckFileManageEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		CheckFileManageMapper dao = conn.getMapper(CheckFileManageMapper.class);
		
		int result=dao.checkManageCodeIsExist(entity);
		
		if(result>=1){//存在
			MsgInfo error = new MsgInfo();
			error.setComponentid("add_check_manage_code");
			error.setErrcode("dbaccess.recordDuplicated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "点检表管理号"+entity.getCheck_manage_code()));
			errors.add(error);
		}
	}
	
	
	/**
	 * 上传文件
	 * @param form
	 * @param conn 
	 * @return 文件路径
	 */
	public String getFile2Local(ActionForm form,List<MsgInfo> errors, SqlSessionManager conn) {
		CheckFileManageForm checkFileManageForm = (CheckFileManageForm) form;
		// 取得上传的文件
		FormFile file = checkFileManageForm.getFile();
		
		if(file!=null && !CommonStringUtil.isEmpty(file.getFileName())){//文件存在 check文件类型，并上传
			FileOutputStream fileOutput;
			
			String fileName=file.getFileName();//文件名
			
			if(!fileName.endsWith("xls") && !fileName.endsWith("xlsx")){
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidType");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidType"));
				errors.add(error);
				return "";
			}			
			
			String outPath=PathConsts.BASE_PATH + PathConsts.DEVICEINFECTION + "\\"+fileName;//文件输出路径 

			log.info("FileName:" + outPath);
			try {
				fileOutput = new FileOutputStream(outPath);
				fileOutput.write(file.getFileData());
				fileOutput.flush();
				fileOutput.close();
			} catch (FileNotFoundException e) {
				log.error("FileNotFound:" + e.getMessage());
			} catch (IOException e) {
				log.error("IO:" + e.getMessage());
			}
			String fileNameWithoutExt = fileName.substring(0,fileName.lastIndexOf("."));
			String fileNameXml = PathConsts.BASE_PATH
					+ PathConsts.DEVICEINFECTION + "\\xml\\"
					+ checkFileManageForm.getCheck_manage_code() + ".xml";
			// 转换成xml
			XlsUtil xls = new XlsUtil(outPath);
			xls.SaveAsXml(fileNameXml);
			xls = null;

			ReadInfect ri = new ReadInfect();
			ri.convert(fileNameXml, fileNameXml.replaceAll("\\.xml$", ".html"), checkFileManageForm.getCheck_file_manage_id(), conn, errors);

			return fileNameWithoutExt;
		}else{
			return "";
		}
		
	}
	
	/**
	 * 更新时候check 是否为当前ID,
	 * @param form
	 * @param conn
	 * @param error
	 * @returns rename
	 */
	public String checkIdIsCurrent(ActionForm form, SqlSession conn, List<MsgInfo> errors){
		CheckFileManageEntity entity = new CheckFileManageEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		CheckFileManageMapper mapper = conn.getMapper(CheckFileManageMapper.class);

		String currentID = mapper.checkIdIsCurrent(entity);

		if(!CommonStringUtil.isEmpty(currentID)){//不为空
			if(!currentID.equals(entity.getCheck_file_manage_id())){//不是当前ID
				int result = mapper.checkManageCodeIsExist(entity);

				if(result>=1){//存在
					MsgInfo error = new MsgInfo();
					error.setComponentid("add_check_manage_code");
					error.setErrcode("dbaccess.recordDuplicated");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "点检表管理号"+entity.getCheck_manage_code()));
					errors.add(error);
				}
			} else {
				return null;
			}
		}

		CheckFileManageEntity org = mapper.getByKey(entity.getCheck_file_manage_id());
		return org.getCheck_manage_code();
	}
	
	/**
	 * 更新
	 * @param form
	 * @param request
	 * @param conn
	 */
	public boolean update(ActionForm form, HttpServletRequest request, SqlSessionManager conn, String fileName){
		CheckFileManageEntity entity = new CheckFileManageEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		boolean needResample = false;
		CheckFileManageMapper mapper = conn.getMapper(CheckFileManageMapper.class);
		// 取得现有
		if (entity.getCheck_file_manage_id() != null) {
			
		}

		Integer access_place=entity.getAccess_place();
		if(access_place!=2){//日常/使用前    归档周期没意义
			entity.setCycle_type(0);//归档周期
		}
		
		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();// 最后更新人
		
		entity.setUpdated_by(operator_id);
		entity.setSheet_file_name(fileName);
		
		mapper.update(entity);

		return needResample;
	}

	/**
	 * 检查点使用前的数据是否已经存在
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public void checkDataIsExist(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		CheckFileManageEntity entity = new CheckFileManageEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		CheckFileManageMapper dao = conn.getMapper(CheckFileManageMapper.class);
		List<CheckFileManageEntity> entityList = dao.checkDataIsExist(entity);
		Map<String, String> result_map = new HashMap<String,String>();
		for (CheckFileManageEntity one : entityList) {
			if (!CommonStringUtil.isEmpty(one.getSpecified_model_name())) {
				String[] split = one.getSpecified_model_name().split(";");
				for (int i = 0; i < split.length; i++) {
					result_map.put(split[i], "");
				}
			} else {
				result_map.put("all", "");
			}
		}

		String[] split_search = new String[1];
		if (!CommonStringUtil.isEmpty(entity.getSpecified_model_name())) {
			split_search = entity.getSpecified_model_name().split(";");
		} else {
			split_search[0] = "all";
		}
		for (int i = 0; i < split_search.length; i++) {
			if (result_map.containsKey("all")
					|| ("all".equals(split_search[0]) && result_map.size() > 0)
					|| result_map.containsKey(split_search[i])) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("add_check_manage_code");
				error.setErrcode("dbaccess.recordDuplicated");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "机型"));
				errors.add(error);
				break;
			}
		}
	}

	/**
	 * 重命名页面模板
	 * @param needRename
	 * @param form
	 */
	public void rename(String orgCheckManageCode, ActionForm form) {
		CheckFileManageForm checkFileManageForm = (CheckFileManageForm) form;

		String orgfileNameXml = PathConsts.BASE_PATH
				+ PathConsts.DEVICEINFECTION + "\\xml\\"
				+ orgCheckManageCode + ".xml";

		String fileNameXml = PathConsts.BASE_PATH
				+ PathConsts.DEVICEINFECTION + "\\xml\\"
				+ checkFileManageForm.getCheck_manage_code() + ".xml";

		new File(orgfileNameXml).renameTo(new File(fileNameXml));
		new File(orgfileNameXml.replaceAll("\\.xml$", ".html")).renameTo(new File(fileNameXml.replaceAll("\\.xml$", ".html")));
	}
}

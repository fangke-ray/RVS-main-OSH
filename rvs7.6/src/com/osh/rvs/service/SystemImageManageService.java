package com.osh.rvs.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.master.SystemImageManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.master.SystemImageManageForm;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.master.SystemImageManageMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class SystemImageManageService {
	private static final int FACAL_WIDTH = 64;
	private static final int STANDARD_WIDTH = 60;
	private Logger log = Logger.getLogger(getClass());
	/**
	 * 查询所有系统图片信息
	 * @return 返回值
	 */
	public List<SystemImageManageForm> searchSystemImageName(SystemImageManageForm conditionForm,SqlSession conn) {
		List<SystemImageManageForm> formList = new ArrayList<SystemImageManageForm>();

		SystemImageManageEntity conditionEntity = new SystemImageManageEntity();
		//copy-to-bean
		BeanUtil.copyToBean(conditionForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		SystemImageManageMapper dao = conn.getMapper(SystemImageManageMapper.class);
		
		//查询数据库中的文件信息
		List<SystemImageManageEntity> returnEntityList = dao.searchImageDescription(conditionEntity);
		
		//copy-to-formList
		BeanUtil.copyToFormList(returnEntityList, formList,CopyOptions.COPYOPTIONS_NOEMPTY, SystemImageManageForm.class);
		
		//遍历本地文件夹
		Map<String,String> fileMap = this.getPcs(conditionEntity.getClassify());
		
		//遍历数据库中的数据
		for(int i =0;i<formList.size();i++){
			SystemImageManageForm resultForm = formList.get(i);
			String fName = resultForm.getFile_name();//文件名称(DB中)
			//判断本地文件名跟数据库中是否相等，如果相等,则移出本地文件名，用数据库中相同文件名的文件信息进行代替
			if(fileMap.containsKey(fName)){
				fileMap.remove(fName);
			}
		}
		
		//将本地遍历的文件夹信息 加入到list中 进行正常显示
		Set<String> set =  fileMap.keySet();
		Iterator<String> iter =set.iterator();
		
		//遍历剩余的本地文件夹
		while(iter.hasNext()){
			SystemImageManageForm resultForm  = new SystemImageManageForm();
			resultForm.setFile_name(iter.next());
			
			formList.add(resultForm);
		}
		return formList;
	}
	
	/**
	 * 验证上传文件是否正确
	 * @param request  页面请求
	 * @param errors  errors
	 */
	public void validateImportFile(SystemImageManageForm systemImageManageForm,SqlSession conn,List<MsgInfo> errors,List<MsgInfo> fileExistsErrors) {
		
		//所有上传文件名全部变成大写
		String uploadfileName = systemImageManageForm.getFile_name().toUpperCase();
		
		String req = "";
		
		//判断上传文件属于哪个类别
		if("pcs".equals(systemImageManageForm.getClassify())){
			//上传文件的文件名是否符合GI*******格式
			req = "^GI[0-9]{7}$";
		}else if("sign".equals(systemImageManageForm.getClassify())){
			//上传文件的文件名是否符合员工工号格式,例如DE******
			req = "^[A-Z]{2}[0-9]{6}$";
		}else if("tcs".equals(systemImageManageForm.getClassify())){
			//上传文件的文件名只能由数字和英文字母组合
			req = "^[A-Z0-9]+$";
		}
		
		Pattern pt = Pattern.compile(req);
		Matcher m = pt.matcher(uploadfileName);
		 
		Map<String,String> fileMap = this.getPcs(systemImageManageForm.getClassify());
		
		//判断是否选择了文件
		FormFile file = systemImageManageForm.getFile();
	    if (file == null || CommonStringUtil.isEmpty(file.getFileName())) {//为选择上传文件
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.emptyFile");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.emptyFile"));
			errors.add(error);
		}else{
			//文件名 输入不符合正确格式
			if(!m.matches()){
				MsgInfo error = new MsgInfo();
				error.setErrcode("info.invalidFileName");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.invalidFileName"));
				errors.add(error);
			//再判断文件名是否已经存在
			}else{
				for(String fileName : fileMap.values()) {
					if(fileName.equals(uploadfileName)){
						MsgInfo error = new MsgInfo();
						error.setErrcode("info.invalidFileNotUnique");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.invalidFileNotUnique"));
						fileExistsErrors.add(error);
					}
				}
			}
			
			//判断该上传的图片名是否在operator存在
			if("sign".equals(systemImageManageForm.getClassify())){
				
				OperatorMapper dao = conn.getMapper(OperatorMapper.class);

				OperatorEntity operator = new OperatorEntity();
				operator.setJob_no(uploadfileName);
				List<OperatorNamedEntity> hits = dao.searchOperator(operator);

				if(hits.size() == 0){
					MsgInfo error = new MsgInfo();
					error.setErrcode("info.notExistOperator");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.notExistOperator"));
					errors.add(error);
				}
			}
		}		
	}
	
	
	/**
	 * 上传文件
	 * @param form
	 * @return 文件路径
	 */
	public void getFileLocal(ActionForm form,List<MsgInfo> errors) {
		SystemImageManageForm systemImageManageForm = (SystemImageManageForm) form;
		// 取得上传的文件
		FormFile file = systemImageManageForm.getFile();
		
		if(file!=null && !CommonStringUtil.isEmpty(file.getFileName())){//文件存在 check文件类型，并上传
			FileOutputStream fileOutput;
			
			String outPath = PathConsts.BASE_PATH + PathConsts.IMAGES + "\\"
					+ systemImageManageForm.getClassify() + "\\"
					+ systemImageManageForm.getFile_name();// 文件输出路径

			try {
				fileOutput = new FileOutputStream(outPath);
				fileOutput.write(file.getFileData());
				fileOutput.flush();
				fileOutput.close();
			} catch (FileNotFoundException e) {
				log.error("FileNotFound:" + e.getMessage());
				return;
			} catch (IOException e) {
				log.error("IO:" + e.getMessage());
				return;
			}

			if ("sign".contentEquals(systemImageManageForm.getClassify())) {
				try {
					// resize
					File loadedImage = new File(outPath);
					BufferedImage srcImage = ImageIO.read(loadedImage);
					int gWidth = srcImage.getWidth();
					int gHeight = srcImage.getHeight();
					if (gWidth > FACAL_WIDTH) {
						int tHeight = STANDARD_WIDTH  * gHeight / gWidth;
						ImageService.fixWidthImageTo(loadedImage, outPath, STANDARD_WIDTH, tHeight);
					}

					// rotate
					ImageService.rotateImageTo(loadedImage, PathConsts.BASE_PATH + PathConsts.IMAGES + "\\"
					+ "sign_v" + "\\"
					+ systemImageManageForm.getFile_name(), 1.57f);
					
				} catch (IOException e) {
					log.error("IOException:" + e.getMessage(), e);
				} catch (Exception e) {
					log.error("Exception:" + e.getMessage(), e);
				}
			}
		}
	}	
	
	/**
	 * 编辑工程检查票内图说明
	 * @param systemImageManageForm 表单
	 * @param conn 数据库会话
	 */
	public void replaceImageDescription(SystemImageManageForm systemImageManageForm,SqlSessionManager conn){
		SystemImageManageEntity systemImageManageEntity = new SystemImageManageEntity();
		
		//copy-to-bean
		BeanUtil.copyToBean(systemImageManageForm, systemImageManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		SystemImageManageMapper dao = conn.getMapper(SystemImageManageMapper.class);
		
		//设置文件默认为工程检查票类型
		//systemImageManageEntity.setClassify("pcs");
		
		//编辑 文件信息(说明)
		dao.replaceImageDescription(systemImageManageEntity);
	}
	
	
	/**
	 * 遍历pcs文件夹下的图片文件
	 * @return
	 */
	public Map<String, String> getPcs(String classify) {
		String path = PathConsts.BASE_PATH + PathConsts.IMAGES + "\\"+classify;
		File fPcs = new File(path);

		Map<String, String> map = new HashMap<String, String>();

		if (fPcs.isDirectory()) {
			File[] files = fPcs.listFiles();

			for (File file : files) {
				if (file.isDirectory()) {
					continue;
				}
				String fileName = file.getName();// 文件名称
				map.put(fileName, fileName);
			}
		}
		return map;
	}
}
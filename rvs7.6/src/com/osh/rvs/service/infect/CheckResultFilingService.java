package com.osh.rvs.service.infect;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import com.osh.rvs.bean.infect.CheckResultFilingEntity;
import com.osh.rvs.bean.master.ToolsManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.infect.CheckResultFilingForm;
import com.osh.rvs.mapper.infect.CheckResultFilingMapper;
import com.osh.rvs.mapper.master.ToolsManageMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class CheckResultFilingService {
	private static Logger log = Logger.getLogger(CheckResultFilingService.class);
	/**
	 * 详细一览
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<CheckResultFilingForm> searchCheckResultFiling(ActionForm form, SqlSession conn) {
		List<CheckResultFilingForm> formList = new ArrayList<CheckResultFilingForm>();
		CheckResultFilingEntity checkResultFilingentity = new CheckResultFilingEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, checkResultFilingentity, CopyOptions.COPYOPTIONS_NOEMPTY);

		CheckResultFilingMapper dao = conn.getMapper(CheckResultFilingMapper.class);

		List<CheckResultFilingEntity> entityList = dao.searchCheckResultFiling(checkResultFilingentity);

		// 复制数据到表单对象
		BeanUtil.copyToFormList(entityList, formList, CopyOptions.COPYOPTIONS_NOEMPTY, CheckResultFilingForm.class);

		return formList;
	}

	public Collection<? extends CheckResultFilingForm> searchJigCheckResultFiling(
			ActionForm form, SqlSession conn) {

		List<CheckResultFilingForm> formList = new ArrayList<CheckResultFilingForm>();

		CheckResultFilingForm condForm = (CheckResultFilingForm) form;
		if (!isEmpty(condForm.getCheck_manage_code())) {
			if ("QF0601-5".indexOf(condForm.getCheck_manage_code()) < 0) {
				return formList;
			}
		}

		if (!isEmpty(condForm.getSheet_file_name())) {
			if ("QF0601-5专用工具定期清点保养记录".indexOf(condForm.getSheet_file_name()) < 0) {
				return formList;
			}
		}

		if (!isEmpty(condForm.getAccess_place())) {
			if (!"2".equals(condForm.getAccess_place())) {
				return formList;
			}
		}

		if (!isEmpty(condForm.getCycle_type())) {
			if (!"8".equals(condForm.getCycle_type())) {
				return formList;
			}
		}

		if (!isEmpty(condForm.getDevices_type_id())) {
			return formList;
		}

		CheckResultFilingForm jigForm = new CheckResultFilingForm();

		jigForm.setCheck_file_manage_id("00000000000");
		jigForm.setCheck_manage_code("QF0601-5");
		jigForm.setSheet_file_name("QF0601-5专用工具定期清点保养记录");
		jigForm.setAccess_place("2");
		jigForm.setCycle_type("8");
		jigForm.setName("专用工具");
		jigForm.setDevices_type_id("00000000000");

		formList.add(jigForm);

		return formList;
	}

	/**
	 * 双击点检结果文档一览
	 * @param form
	 * @param conn
	 * @return
	 */
   public List<CheckResultFilingForm> searchCheckedFileStorage(ActionForm form,SqlSession conn){
	   List<CheckResultFilingForm> checkResultFilingForms = new ArrayList<CheckResultFilingForm>();
	   
	   CheckResultFilingForm checkResultFilingForm = (CheckResultFilingForm) form;
	   CheckResultFilingEntity checkResultFilingEntity = new CheckResultFilingEntity();
	   //copy--to--bean
	   BeanUtil.copyToBean(checkResultFilingForm, checkResultFilingEntity, CopyOptions.COPYOPTIONS_NOEMPTY);   
	   
	   CheckResultFilingMapper dao = conn.getMapper(CheckResultFilingMapper.class);
	   List<CheckResultFilingEntity> entityList = dao.searchCheckedFileStorage(checkResultFilingEntity);
	   
	   List<CheckResultFilingEntity> resultList = new ArrayList<CheckResultFilingEntity>();
	   
	   CheckResultFilingEntity resultFilingEntity =null;
	   if(entityList.size()>0){
		   for(int i=0;i<entityList.size();i++){
			   resultFilingEntity = entityList.get(i);
			   Calendar filingDate=Calendar.getInstance();
			   filingDate.setTime(resultFilingEntity.getFiling_date());//获取点检表归档日期
			   //点检表归档日期转成SORC财年
			   String strFilingDate = RvsUtils.getBussinessYearString(filingDate);

			   //对应类型是日常+归档周期是周月
			   if(resultFilingEntity.getAccess_place()==1 || resultFilingEntity.getCycle_type()==1){
				   //获取财年中的月份
				   int month =filingDate.get(Calendar.MONTH)+1;
				   strFilingDate=strFilingDate+" "+month+"月";
			   }
			  
			   resultFilingEntity.setWork_period(strFilingDate);
			   resultList.add(resultFilingEntity);
		   }		   
	   }
	   
	   // 复制数据到表单对象
	   BeanUtil.copyToFormList(resultList, checkResultFilingForms, CopyOptions.COPYOPTIONS_NOEMPTY, CheckResultFilingForm.class);
	   
	   return checkResultFilingForms;
   }
   
   /**
    * 设备名称List
    * @param conn
    * @return
    */
   public String searchDeviceNames(SqlSession conn) {
		// 取得权限下拉框信息
		List<String[]> dList = new ArrayList<String[]>();

		CheckResultFilingMapper dao = conn.getMapper(CheckResultFilingMapper.class);
		//点检表名称list
		List<CheckResultFilingEntity> resultBeanList = dao.searchDeviceNames();
		// 建立页面返回表单
		List<CheckResultFilingForm> ldf = new ArrayList<CheckResultFilingForm>();
		if (resultBeanList != null && resultBeanList.size() > 0) {
			// 数据对象复制到表单
			BeanUtil.copyToFormList(resultBeanList, ldf, null, CheckResultFilingForm.class);
			for (CheckResultFilingForm form : ldf) {
				String[] dline = new String[3];
				dline[0] = form.getDevices_manage_id();
				dline[2] = form.getName();
				dline[1] = form.getSheet_file_name();
				dList.add(dline);
			}
			String pReferChooser = CodeListUtils.getReferChooser(dList);
			return pReferChooser;
		} else {
			return "";
		}
	}

	public String searchJigNames(SqlSession conn) {
		// 取得权限下拉框信息
		List<String[]> dList = new ArrayList<String[]>();

		ToolsManageMapper dao = conn.getMapper(ToolsManageMapper.class);
		// 点检表名称list
		List<ToolsManageEntity> resultBeanList = dao.searchToolsManage(new ToolsManageEntity());

		if (resultBeanList != null && resultBeanList.size() > 0) {
			for (ToolsManageEntity entity : resultBeanList) {
				String[] dline = new String[3];
				dline[0] = entity.getTools_manage_id();
				dline[2] = entity.getTools_no();
				dline[1] = entity.getManage_code();
				dList.add(dline);
			}
			String pReferChooser = CodeListUtils.getReferChooser(dList);
			return pReferChooser;
		} else {
			return "";
		}
	}

   /**
    * 点检表名称List
    * @param conn 数据库会话
    * @return
    */
   public String searchCheckFileNames(SqlSession conn) {
		// 取得权限下拉框信息
		List<String[]> dList = new ArrayList<String[]>();

		CheckResultFilingMapper dao = conn.getMapper(CheckResultFilingMapper.class);
		//点检表名称list
		List<CheckResultFilingEntity> resultBeanList = dao.searchCheckFileNames();
		// 建立页面返回表单
		List<CheckResultFilingForm> ldf = new ArrayList<CheckResultFilingForm>();
		if (resultBeanList != null && resultBeanList.size() > 0) {
			// 数据对象复制到表单
			BeanUtil.copyToFormList(resultBeanList, ldf, null, CheckResultFilingForm.class);
			for (CheckResultFilingForm form : ldf) {
				String[] dline = new String[2];
				dline[0] = form.getCheck_file_manage_id();
				dline[1] = form.getCheck_manage_code();
				dList.add(dline);
			}
			String pReferChooser = CodeListUtils.getReferChooser(dList);
			return pReferChooser;
		} else {
			return "";
		}
	}
  
   
   /**
    * 上传附表(pdf)
    * @param form
    * @param checkResultFilingForm
    * @param errors
    */
   public void uploadSchedule(FormFile file,CheckResultFilingForm checkResultFilingForm){
	    //将点检表归档日期转化成期年-147P
	    String filingDate = checkResultFilingForm.getFiling_date();
	    Date date = DateUtil.toDate(filingDate,DateUtil.DATE_PATTERN);
	    Calendar calendar=Calendar.getInstance();
	    calendar.setTime(date);
	   
	    //期年-如147P
	    String workPeriod = RvsUtils.getBussinessYearString(calendar);
	   	
		FileOutputStream fileOutput;

		//存放路径：D://rvs/Infections/147P/QR-B31002-24
		String tempfilename = PathConsts.BASE_PATH + PathConsts.INFECTIONS + "\\" + workPeriod +"\\"+checkResultFilingForm.getCheck_manage_code();

		File fMonthPath = new File(tempfilename);
		if (!fMonthPath.exists()) {
			fMonthPath.mkdirs();
		}
		fMonthPath = null;

		tempfilename += "\\"+file.getFileName();
		log.info("FileName:" + tempfilename);
		try {
			fileOutput = new FileOutputStream(tempfilename);
			fileOutput.write(file.getFileData());
			fileOutput.flush();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			log.error("FileNotFound:" + e.getMessage());
		} catch (IOException e) {
			log.error("IO:" + e.getMessage());
		}
   }
   
   
   /**
    * 新建点检归档记录
    * @param checkResultFilingForm
    * @param errors
    * @param conn
    */
   public void insertCheckedFileStorage(FormFile file,CheckResultFilingForm checkResultFilingForm,SqlSessionManager conn){
	   CheckResultFilingMapper dao = conn.getMapper(CheckResultFilingMapper.class);
	   
	   CheckResultFilingEntity checkResultFilingEntity = new CheckResultFilingEntity();
	   
	   //copy-to-bean
	   BeanUtil.copyToBean(checkResultFilingForm, checkResultFilingEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
	   
	   String fileName = file.getFileName();
	   
	   checkResultFilingEntity.setStorage_file_name(fileName.substring(0,fileName.lastIndexOf(".")));
	   //新建点检归档记录
	   dao.insertCheckedFileStorage(checkResultFilingEntity);
   }
}

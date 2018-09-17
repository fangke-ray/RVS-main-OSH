package com.osh.rvs.service.qa;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.qa.ServiceRepairManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.qa.ServiceRepairManageForm;
import com.osh.rvs.mapper.inline.SoloProductionFeatureMapper;
import com.osh.rvs.mapper.qa.ServiceRepairManageMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ServiceRepairManageService {

	/** 保内QIS型号名字集合 **/
	public List<String> getModelNameAutoCompletes(SqlSession conn) {
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		return dao.getModelNameAutoCompletes();
	}
	
	/**无QA判定日的数据中最早的一个QA受理日**/
	public String getMinReceptionTime(SqlSession conn){
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		String strDate="";
		Date date=dao.getMinReceptionTime();
		strDate=DateUtil.toString(date, DateUtil.DATE_PATTERN);
		return strDate;
	}
	
	/**保内QIS管理一览**/
	public List<ServiceRepairManageForm> searchServiceRepair(ServiceRepairManageEntity instance,SqlSession conn){
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		List<ServiceRepairManageForm> lResultForm=new ArrayList<ServiceRepairManageForm>();
		if(instance!=null){
			List<ServiceRepairManageEntity> lResultBean=dao.searchServiceRepair(instance);
			//复制数据对象到表单
			CopyOptions cos = new CopyOptions();
			cos.excludeNull();
			cos.excludeEmptyString();
			// cos.dateConverter(DateUtil.DATETIME_PATTERN, "qa_reception_time", "qa_referee_time");
			BeanUtil.copyToFormList(lResultBean, lResultForm, cos, ServiceRepairManageForm.class);
			return lResultForm;
		}else{
			return null;
		}
		
	}
	
	/**保内QIS管理等级集合**/
	public List<String> getRankAutoCompletes(SqlSession conn){
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		return dao.getRankAutoCompletes();
	}
	
	
	/**维修对象对象集合，集合长度=0 or >1返回null,集合长度=1返回一个对象**/
	public MaterialForm getRecept(ActionForm form, SqlSession conn,List<MsgInfo> errors) {
		//复制表单数据到对象
		ServiceRepairManageEntity enity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, enity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		MaterialForm materialForm=new MaterialForm();
		
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		
		List<MaterialEntity> sList=dao.getRecept(enity);
		int length=sList.size();
		if(length<=0||length>1){
			return null;
		}else{
			MaterialEntity materialEntity=sList.get(0);
			//复制数据到表单对象
			BeanUtil.copyToForm(materialEntity, materialForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			return materialForm;
		}
	}
	
	/**查询主键是否存在**/
	public void getPrimaryKey(ActionForm form,SqlSessionManager conn,List<MsgInfo> errors){
		//复制表单数据到对象
		ServiceRepairManageEntity serviceRepairManageEntity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, serviceRepairManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		List<ServiceRepairManageEntity> list=dao.getPrimaryKey(serviceRepairManageEntity);
		if(list.size()>=1){
			   MsgInfo error = new MsgInfo();
			   error.setComponentid("model_name");
			   error.setErrcode("dbaccess.recordDuplicated");
			   error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "保修期内返品+QIS品"));
			   errors.add(error);
		}
		
	}
	
	
	
	public void insert(ActionForm form,SqlSessionManager conn ,List<MsgInfo> errors) throws Exception{
		
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity serviceRepairManageEntity=new ServiceRepairManageEntity();
		
		//复制表单到数据对象
		BeanUtil.copyToBean(form, serviceRepairManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		List<MaterialEntity> list=dao.getMaterialIds(serviceRepairManageEntity);
		
		MaterialEntity mterialEntity=null;
		String material_id = null;
		Long closestReceptionTimeDiff = null;
		Date rc_mailsend_date = serviceRepairManageEntity.getRc_mailsend_date();

		for(int i=0;i<list.size();i++){
			mterialEntity=list.get(i);
			Date reception_time = mterialEntity.getReception_time();
			long diff = Math.abs(reception_time.getTime() - rc_mailsend_date.getTime());
			if (closestReceptionTimeDiff == null || closestReceptionTimeDiff > diff) {
				closestReceptionTimeDiff = diff;
				material_id = mterialEntity.getMaterial_id();
			}
		}
		serviceRepairManageEntity.setMaterial_id(material_id);
		
		Calendar cal = Calendar.getInstance();
		serviceRepairManageEntity.setQa_reception_time(cal.getTime());

		dao.insertServiceRepairManage(serviceRepairManageEntity);
	}
	
	
	/**创建Excel文件**/
	public String createWorkReport(HttpServletRequest request)throws Exception{
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "保修期内返品+QIS品管理日程表模板.xls";
		String cacheName ="保修期内返品 QIS品管理日程表模板" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<ServiceRepairManageForm> list=(List)request.getSession().getAttribute("result");
		OutputStream out = null;
		InputStream in = null;
		
		try {
			in = new FileInputStream(cachePath);//读取文件 
			HSSFWorkbook work=new HSSFWorkbook(in);//创建xls文件
			HSSFSheet sheet=work.getSheetAt(0);//取得第一个Sheet
			int index=0;
			HSSFFont font=work.createFont();
			font.setFontHeightInPoints((short)9);
			font.setFontName("微软雅黑");

			//只对"◎"字体的大小做改变(变大)
			HSSFFont sharpFont=work.createFont();
			sharpFont.setFontHeightInPoints((short)16);
			sharpFont.setFontName("微软雅黑");
			
			/*设置单元格内容居中显示*/
			HSSFCellStyle styleAlignCenter = work.createCellStyle();
			styleAlignCenter.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			styleAlignCenter.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			styleAlignCenter.setBorderRight(HSSFCellStyle.BORDER_THIN); 
			styleAlignCenter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleAlignCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			styleAlignCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			styleAlignCenter.setWrapText(true);
			styleAlignCenter.setFont(font);
			
			/*设置单元格内容居左显示*/
			HSSFCellStyle styleAlignLeft = work.createCellStyle();
			styleAlignLeft.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			styleAlignLeft.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			styleAlignLeft.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleAlignLeft.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleAlignLeft.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			styleAlignLeft.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			styleAlignLeft.setWrapText(true); 
			styleAlignLeft.setFont(font);

			HSSFFont fontRED = work.createFont();
			fontRED.setColor(HSSFColor.RED.index);
			fontRED.setFontHeightInPoints((short)6);
			HSSFCellStyle styleRed = work.createCellStyle();
			styleRed.cloneStyleFrom(styleAlignCenter);
			styleRed.setFont(fontRED);
			
			HSSFCellStyle sharpStyle= work.createCellStyle();
			sharpStyle.cloneStyleFrom(styleAlignCenter);
			sharpStyle.setFont(sharpFont);
			
			for(int i=0;i<list.size();i++){
				  ServiceRepairManageForm form=list.get(i);
				  index++;
				  HSSFRow row=sheet.createRow(index);//从第二行开始创建

				  int yPos = 0;

				  HSSFCell indexCell = row.createCell(yPos++);
				  indexCell.setCellValue(index);
				  indexCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell modelnameCell =  row.createCell(yPos++);
				  modelnameCell.setCellValue(form.getModel_name());
				  modelnameCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell serialnoCell =  row.createCell(yPos++);
				  serialnoCell.setCellStyle(styleAlignLeft);
				  try {
					  int iSerial_no = Integer.parseInt(form.getSerial_no());
					  serialnoCell.setCellValue(iSerial_no);
					  serialnoCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				  } catch(NumberFormatException e) {
					  serialnoCell.setCellValue(form.getSerial_no());
				  }
				  
				  HSSFCell sorcnoCell =  row.createCell(yPos++);
				  sorcnoCell.setCellValue(form.getSorc_no());
				  sorcnoCell.setCellStyle(styleAlignLeft);

				  HSSFCell kindCell =  row.createCell(yPos++);
				  kindCell.setCellValue(CodeListUtils.getValue("qa_category_kind", form.getKind()));
				  kindCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell qamaterialservicerepairCell =  row.createCell(yPos++);
				  qamaterialservicerepairCell.setCellValue(CodeListUtils.getValue("qa_material_service_repair", form.getService_repair_flg()));
				  qamaterialservicerepairCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell rcmailsenddateCell =  row.createCell(yPos++);
				  rcmailsenddateCell.setCellValue(form.getRc_mailsend_date());
				  rcmailsenddateCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell rcshipassigndateCell =  row.createCell(yPos++);
				  rcshipassigndateCell.setCellValue(form.getRc_ship_assign_date());
				  rcshipassigndateCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell receptiondateCell =  row.createCell(yPos++);
				  receptiondateCell.setCellValue(form.getReception_date());
				  receptiondateCell.setCellStyle(styleAlignCenter);
				  
				  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				  String qa_reception_time ="";
				  String qa_referee_time ="";
				  if(form.getQa_reception_time()!=null){
					  qa_reception_time=format.format(new Date(form.getQa_reception_time())); 
				  }
				  if(form.getQa_referee_time()!=null){
					  qa_referee_time= format.format(new Date(form.getQa_referee_time()));
				  }
				  HSSFCell qareceptiontimeCell =  row.createCell(yPos++);
				  qareceptiontimeCell.setCellValue(qa_reception_time);
				  qareceptiontimeCell.setCellStyle(styleAlignCenter);
				  HSSFCell qarefereetimeCell =  row.createCell(yPos++);
				  qarefereetimeCell.setCellStyle(styleAlignCenter);
				  qarefereetimeCell.setCellValue(qa_referee_time);
				  
				  HSSFCell answerindeadlineCell =  row.createCell(yPos++);
				  if(form.getQa_referee_time()!=null){
					  if(form.getAnswer_in_deadline()!=null){
						  switch(form.getAnswer_in_deadline()){
						    case "2":
					  		  answerindeadlineCell.setCellValue("◎");
					  		  answerindeadlineCell.setCellStyle(sharpStyle);
					  		  break;	
						    case "1":
						  		  answerindeadlineCell.setCellValue("○");
						  		answerindeadlineCell.setCellStyle(sharpStyle);
						  		  break;
						  	case "0":
						  		  answerindeadlineCell.setCellValue("╳");
						  		answerindeadlineCell.setCellStyle(styleRed);
						  		  break;
						  	default:
						  		 break;
						  } 
					  }			
				  }else{
					  answerindeadlineCell.setCellValue("");
					  answerindeadlineCell.setCellStyle(styleAlignCenter);
				  }
				  
				  HSSFCell qasecondaryrefereedateCell =  row.createCell(yPos++);
				  qasecondaryrefereedateCell.setCellValue(form.getQa_secondary_referee_date());
				  qasecondaryrefereedateCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell rankCell =  row.createCell(yPos++);
				  rankCell.setCellValue(form.getRank());
				  rankCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell servicefreeflgCell =  row.createCell(yPos++);
				  servicefreeflgCell.setCellValue(CodeListUtils.getValue("service_free_flg", form.getService_free_flg()));
				  servicefreeflgCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell countermeasuresCell =  row.createCell(yPos++);
				  countermeasuresCell.setCellValue(form.getCountermeasures());
				  countermeasuresCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell workshopCell =  row.createCell(yPos++);
				  workshopCell.setCellValue(CodeListUtils.getValue("workshop", form.getWorkshop())); 
				  workshopCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell quotationdateCell =  row.createCell(yPos++);
				  quotationdateCell.setCellValue(form.getQuotation_date());
				  quotationdateCell.setCellStyle(styleAlignLeft);

				  HSSFCell agreeddateCell =  row.createCell(yPos++);
				  agreeddateCell.setCellValue(form.getAgreed_date()); 
				  agreeddateCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell inlinedateCell =  row.createCell(yPos++);
				  inlinedateCell.setCellValue(form.getInline_date());
				  inlinedateCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell outlinedateCell =  row.createCell(yPos++);
				  outlinedateCell.setCellValue(form.getOutline_date());
				  outlinedateCell.setCellStyle(styleAlignCenter);

				  HSSFCell unfixbackflgCell =  row.createCell(yPos++);
				  if(form.getUnfix_back_flg()!=null){
					  switch(form.getUnfix_back_flg()){
					  	case "1":
					  		unfixbackflgCell.setCellValue(form.getOutline_date());
					  		  break;
					  	case "0":
					  		unfixbackflgCell.setCellValue("");
					  		  break;
					  	default:
					  		 break;
					  }
				  }
				  unfixbackflgCell.setCellStyle(styleAlignCenter);

				  HSSFCell qualityJudgmentCell =  row.createCell(yPos++);
				  qualityJudgmentCell.setCellValue(CodeListUtils.getValue("quality_judgment", form.getQuality_judgment()));
				  qualityJudgmentCell.setCellStyle(styleAlignLeft);

				  HSSFCell quality_info_noCell =  row.createCell(yPos++);
				  quality_info_noCell.setCellValue(form.getQuality_info_no());
				  quality_info_noCell.setCellStyle(styleAlignLeft);

				  HSSFCell qisIsusetCell =  row.createCell(yPos++);
				  qisIsusetCell.setCellValue(CodeListUtils.getValue("qis_isuse", form.getQis_isuse()));
				  qisIsusetCell.setCellStyle(styleAlignLeft);

				  HSSFCell qis_invoice_noCell =  row.createCell(yPos++);
				  qis_invoice_noCell.setCellValue(form.getQis_invoice_no());
				  qis_invoice_noCell.setCellStyle(styleAlignLeft);

				  HSSFCell qis_invoice_dateCell =  row.createCell(yPos++);
				  qis_invoice_dateCell.setCellValue(form.getQis_invoice_date());
				  qis_invoice_dateCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell include_monthCell =  row.createCell(yPos++);
				  include_monthCell.setCellValue(form.getInclude_month());
				  include_monthCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell charge_mountCell =  row.createCell(yPos++);
				  charge_mountCell.setCellValue(form.getCharge_amount());
				  charge_mountCell.setCellStyle(styleAlignLeft);

				  HSSFCell commentCell =  row.createCell(yPos++);
				  commentCell.setCellValue(form.getComment());
				  commentCell.setCellStyle(styleAlignLeft);

				  HSSFCell etq_noCell =  row.createCell(yPos++);
				  etq_noCell.setCellValue(form.getEtq_no());
				  etq_noCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell pae_noCell =  row.createCell(yPos++);
				  pae_noCell.setCellValue(form.getPae_no());
				  pae_noCell.setCellStyle(styleAlignLeft);

				  /*保内QIS分析表详细数据*/
				  HSSFCell analysis_noCell =  row.createCell(yPos++);
				  analysis_noCell.setCellValue(form.getAnalysis_no());
				  analysis_noCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell customer_nameCell =  row.createCell(yPos++);
				  customer_nameCell.setCellValue(form.getCustomer_name());
				  customer_nameCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell fix_demandCell =  row.createCell(yPos++);
				  fix_demandCell.setCellValue(form.getFix_demand());
				  fix_demandCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell trouble_discribeCell =  row.createCell(yPos++);
				  trouble_discribeCell.setCellValue(form.getTrouble_discribe());
				  trouble_discribeCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell trouble_causeCell =  row.createCell(yPos++);
				  trouble_causeCell.setCellValue(form.getTrouble_cause());
				  trouble_causeCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell analysis_resultCell =  row.createCell(yPos++);
				  analysis_resultCell.setCellValue(CodeListUtils.getValue("analysis_result", form.getAnalysis_result()));
				  analysis_resultCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell liability_flgCell =  row.createCell(yPos++);
				  liability_flgCell.setCellValue(CodeListUtils.getValue("liability_flg", form.getLiability_flg()));
				  liability_flgCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell OGZCommentCell =  row.createCell(yPos++);

				  //判断OGZ修理备注--如果service_repair_analysis-维修场所标记值是1时，则显示OGZ，否则为空
				  String OGZCommentCellValue = "";
				  String OGZComment = form.getManufactory_flg();
				  if("1".equals(OGZComment)){
					  OGZCommentCellValue ="OGZ";
				  }else{
					  OGZCommentCellValue ="";
				  }
				  OGZCommentCell.setCellValue(OGZCommentCellValue);
				  OGZCommentCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell append_componentCell =  row.createCell(yPos++);
				  append_componentCell.setCellValue(form.getAppend_component());
				  append_componentCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell quantityCell =  row.createCell(yPos++);
				  quantityCell.setCellValue(form.getQuantity());
				  quantityCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell loss_amountCell =  row.createCell(yPos++);
				  loss_amountCell.setCellValue(form.getLoss_amount());
				  loss_amountCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell last_sorc_noCell =  row.createCell(yPos++);
				  last_sorc_noCell.setCellValue(form.getLast_sorc_no());
				  last_sorc_noCell.setCellStyle(styleAlignLeft);

				  HSSFCell last_shipping_dateCell =  row.createCell(yPos++);
				  last_shipping_dateCell.setCellValue(form.getLast_shipping_date());
				  last_shipping_dateCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell last_rankCell =  row.createCell(yPos++);
				  String last_rank =  form.getLast_rank();
				  String last_rankCellValue ="";
				  if(last_rank!=null && !"".equals(last_rank)){
					  last_rankCellValue=last_rank; // CodeListUtils.getValue("material_level", last_rank) +"级"
				  }
				  last_rankCell.setCellValue(last_rankCellValue);
				  last_rankCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell last_trouble_featureCell =  row.createCell(yPos++);
				  last_trouble_featureCell.setCellValue(form.getLast_trouble_feature());
				  last_trouble_featureCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell wash_featureCell =  row.createCell(yPos++);
				  wash_featureCell.setCellValue(form.getWash_feature());
				  wash_featureCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell disinfect_featureCell =  row.createCell(yPos++);
				  disinfect_featureCell.setCellValue(form.getDisinfect_feature());
				  disinfect_featureCell.setCellStyle(styleAlignLeft);

				  HSSFCell steriliza_featureCell =  row.createCell(yPos++);
				  steriliza_featureCell.setCellValue(form.getSteriliza_feature());
				  steriliza_featureCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell usage_frequencyCell =  row.createCell(yPos++);
				  usage_frequencyCell.setCellValue(form.getUsage_frequency());
				  usage_frequencyCell.setCellStyle(styleAlignLeft);
				  
				  HSSFCell corresponse_flgCell =  row.createCell(yPos++);//处理方式
				  String corresponse_flg =  form.getCorresponse_flg();
				  String corresponse_flgCellValue = "";
				  if(!CommonStringUtil.isEmpty(corresponse_flg)){
					  corresponse_flgCellValue = CodeListUtils.getValue("qis_corresponse_flg", corresponse_flg);
				  }
				  corresponse_flgCell.setCellValue(corresponse_flgCellValue);
				  corresponse_flgCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell entity_send_flgCell =  row.createCell(yPos++);//实物处理
				  String entity_send_flg =  form.getEntity_send_flg();
				  String entity_send_flgCellValue = "";
				  if(!CommonStringUtil.isEmpty(entity_send_flg)){
					  entity_send_flgCellValue = CodeListUtils.getValue("qis_entity_send_flg", entity_send_flg);
				  }
				  entity_send_flgCell.setCellValue(entity_send_flgCellValue);
				  entity_send_flgCell.setCellStyle(styleAlignCenter);

				  if("21".equals(entity_send_flg) || "22".equals(entity_send_flg) || "23".equals(entity_send_flg)){
					  HSSFCell trouble_item_reception_dateCell =  row.createCell(yPos++);//故障品接收
					  trouble_item_reception_dateCell.setCellValue("-");
					  trouble_item_reception_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell trouble_item_in_bussiness_dateCell =  row.createCell(yPos++);//故障品提交给业务
					  trouble_item_in_bussiness_dateCell.setCellValue("-");
					  trouble_item_in_bussiness_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell trouble_item_out_bussiness_dateCell =  row.createCell(yPos++);//故障品发送（业务）
					  trouble_item_out_bussiness_dateCell.setCellValue("-");
					  trouble_item_out_bussiness_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell qis2_dateCell =  row.createCell(yPos++);//QIS2
					  qis2_dateCell.setCellValue("-");
					  qis2_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell qis3_dateCell =  row.createCell(yPos++);//QIS3
					  if("22".equals(entity_send_flg)){
						  qis3_dateCell.setCellValue("-");
					  } else {
						  qis3_dateCell.setCellValue(form.getQis3_date());
					  }
					  qis3_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell waste_certificated_dateCell =  row.createCell(yPos++);//废弃证明
					  waste_certificated_dateCell.setCellValue("-");
					  waste_certificated_dateCell.setCellStyle(styleAlignCenter);
				  }else{
					  HSSFCell trouble_item_reception_dateCell =  row.createCell(yPos++);//故障品接收
					  trouble_item_reception_dateCell.setCellValue(form.getTrouble_item_reception_date());
					  trouble_item_reception_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell trouble_item_in_bussiness_dateCell =  row.createCell(yPos++);//故障品提交给业务
					  trouble_item_in_bussiness_dateCell.setCellValue(form.getTrouble_item_in_bussiness_date());
					  trouble_item_in_bussiness_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell trouble_item_out_bussiness_dateCell =  row.createCell(yPos++);//故障品发送（业务）
					  trouble_item_out_bussiness_dateCell.setCellValue(form.getTrouble_item_out_bussiness_date());
					  trouble_item_out_bussiness_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell qis2_dateCell =  row.createCell(yPos++);//QIS2
					  qis2_dateCell.setCellValue(form.getQis2_date());
					  qis2_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell qis3_dateCell =  row.createCell(yPos++);//QIS3
					  qis3_dateCell.setCellValue(form.getQis3_date());
					  qis3_dateCell.setCellStyle(styleAlignCenter);
					  
					  HSSFCell waste_certificated_dateCell =  row.createCell(yPos++);//废弃证明
					  waste_certificated_dateCell.setCellValue(form.getWaste_certificated_date());
					  waste_certificated_dateCell.setCellStyle(styleAlignCenter);
				  }
				  
				  HSSFCell m_trouble_phenomenon_confirmCell = row.createCell(yPos++);//故障现象确认(工厂)
				  m_trouble_phenomenon_confirmCell.setCellValue(form.getM_trouble_phenomenon_confirm());
				  m_trouble_phenomenon_confirmCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell m_judgment_resultCell = row.createCell(yPos++);//判定结果(工厂)
				  m_judgment_resultCell.setCellValue(form.getM_judgment_result());
				  m_judgment_resultCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell m_analysis_result_briefCell = row.createCell(yPos++);//分析结果简述(工厂)
				  m_analysis_result_briefCell.setCellValue(form.getM_analysis_result_brief());
				  m_analysis_result_briefCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell m_correspond_methodCell = row.createCell(yPos++);//对应方法(工厂)
				  m_correspond_methodCell.setCellValue(form.getM_correspond_method());
				  m_correspond_methodCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell m_solutionsCell = row.createCell(yPos++);//对策(工厂)
				  m_solutionsCell.setCellValue(form.getM_solutions());
				  m_solutionsCell.setCellStyle(styleAlignCenter);

				  HSSFCell setup_dateCell = row.createCell(yPos++);//购买/安装日期
				  setup_dateCell.setCellValue(form.getSetup_date());
				  setup_dateCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell trouble_happen_dateCell = row.createCell(yPos++);//故障发生日期
				  trouble_happen_dateCell.setCellValue(form.getTrouble_happen_date());
				  trouble_happen_dateCell.setCellStyle(styleAlignCenter);
				  
				  HSSFCell use_countCell = row.createCell(yPos++);//使用累计次数
				  use_countCell.setCellValue(form.getUse_count());
				  use_countCell.setCellStyle(styleAlignCenter);

				  HSSFCell use_elapseCell = row.createCell(yPos++);//使用累计时间
				  use_elapseCell.setCellValue(form.getUse_elapse());
				  use_elapseCell.setCellStyle(styleAlignCenter);
				  
			}
			out= new FileOutputStream(cachePath);
			work.write(out);
		} catch (FileNotFoundException e) {
				throw e;
		} catch (IOException e) {
				throw e;
		}finally{
			if(in!=null){
				in.close();
			}
			if(out!=null){
				out.close();
			}
		}
		return cacheName;
	}
	
	/**更新service_repair_manage表
	 * @throws Exception **/
	public void updateServiceRepairManage(ActionForm form,SqlSessionManager conn) throws Exception{
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		dao.updateServiceRepairManage(entity);
	}
	
	/**删除QIS请款信息**/
	public void deleteQisPayout(ActionForm form,SqlSessionManager conn){
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		dao.deleteQisPayout(entity);
	}
	
	/**更新QIS请款信息**/
	public void updateQisPayout(ActionForm form,SqlSessionManager conn){
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		if (entity.getCharge_amount() == null && !isEmpty(entity.getInclude_month())) {
			entity.setCharge_amount(BigDecimal.ZERO);
		}
		if (entity.getQuality_info_no() == null)
			entity.setQuality_info_no("");

		dao.updateQisPayout(entity);
	}
	
	/**插入数据**/
	public void insertServiceRpairManage(ActionForm form,SqlSessionManager conn)throws Exception{
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		Integer service_repair_flg = entity.getService_repair_flg();
		
		String strMaterial_id = null;
		String startWith = "";
		if(service_repair_flg == 4){//保修期不良(OGZ)
			startWith = "G";
			strMaterial_id=dao.getMaxMaterialId(startWith);
		}else{
			startWith = "Q";
			strMaterial_id=dao.getMaxMaterialId(startWith);
		}
		
		if (strMaterial_id == null) {
			entity.setMaterial_id(startWith + "0000000001");
		}else {
			String tempMaterial_id=strMaterial_id.substring(1, strMaterial_id.length());
			int material_id=Integer.valueOf(tempMaterial_id)+1;
			String maxMaterial_id=String.valueOf(material_id);
			for(int i=maxMaterial_id.length();i<10;i++){
				maxMaterial_id="0"+maxMaterial_id;
			}
			maxMaterial_id = startWith + maxMaterial_id;
			
			entity.setMaterial_id(maxMaterial_id);
		}
		dao.insertServiceRepairManage(entity);
	}

	/**插入数据
	 * @throws Exception **/
	public void insertServiceRepairManageFromMaterial(String material_id,SqlSessionManager conn) throws Exception {
		ServiceRepairManageMapper mapper = conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		// 维修对象信息
		entity.setMaterial_id(material_id);


		// 查询维修对象信息
		List<MaterialEntity> sList=mapper.getRecept(entity);
		if (sList.size() == 1) {

			MaterialEntity mEntity = sList.get(0);

			// 如果是QIS
			if (mEntity.getService_repair_flg() == 2) {
				// 按型号+机身号匹配
				List<String> qa_material_ids = mapper.matchQis(mEntity.getModel_name(), mEntity.getSerial_no());
				
				// 按机身号匹配
				if (qa_material_ids.size() != 1) {
					qa_material_ids = mapper.matchQis(null, mEntity.getSerial_no());
				}
				// 找到就替换成RVS的material_id
				if (qa_material_ids.size() == 1) {
					mapper.updateMaterialId(material_id, qa_material_ids.get(0));
					return;
				}
			}
			
			// 确认是否已经建立保内判定作业
			List<ServiceRepairManageEntity> serviceRepairs = mapper.searchServiceRepair(entity);
			if (serviceRepairs.size() == 0) {
				entity.setModel_name(mEntity.getModel_name());
				entity.setSerial_no(mEntity.getSerial_no());
				entity.setSorc_no(mEntity.getSorc_no());
				entity.setReception_date(mEntity.getReception_time());
				entity.setService_repair_flg(mEntity.getService_repair_flg());
				if ("07".equals(mEntity.getKind())) {
					entity.setKind(7);
				} else {
					entity.setKind(0);
				}
				entity.setRc_mailsend_date(new Date());
				String levelText = CodeListUtils.getValue("material_level", "" + mEntity.getLevel());
				if (!isEmpty(levelText)) {
					entity.setRank(levelText+"级");
				} else {
					entity.setRank("未知");
				}
				if (mEntity.getDirect_flg() != null && 1 == mEntity.getDirect_flg()) {
					entity.setWorkshop(80); // 直送 
				}
				entity.setRc_ship_assign_date(new Date()); 

				// 插入
				mapper.insertServiceRepairManage(entity);
			}
		}
	}
	
	public void validate(ActionForm form, List<MsgInfo> errors){
		ServiceRepairManageForm serviceRepairManageForm=(ServiceRepairManageForm)form;
		if("1".equals(serviceRepairManageForm.getService_repair_flg()) && isEmpty(serviceRepairManageForm.getRank())){
			MsgInfo info=new MsgInfo();
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","等级"));
			info.setErrcode("validator.required");
			errors.add(info);
		}
		
		//发行QIS
		String qis_isuse =  serviceRepairManageForm.getQis_isuse();
		//QIS发送单号
		String qis_invoice_no = serviceRepairManageForm.getQis_invoice_no();
		
		//QIS发送日期
		String qis_invoice_date = serviceRepairManageForm.getQis_invoice_date();
		
		//ETQ单号
		String etq_no = serviceRepairManageForm.getEtq_no();
		
		//选择发行QIS但没填写QIS发送单号
		if("1".equals(qis_isuse) && isEmpty(qis_invoice_no)){
			MsgInfo info=new MsgInfo();
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","QIS发送单号"));
			info.setErrcode("validator.required");
			errors.add(info);
		}else if(!isEmpty(qis_invoice_no) && "2".equals(qis_isuse)){//填写了QIS发送单号但不发行QIS
			MsgInfo info=new MsgInfo();
			info.setErrmsg("请选择发行QIS");
			info.setErrcode("validator.required");
			errors.add(info);
		}
		
		// 当填写了QIS发送日期，则ETQ单号必须填写
		if(!CommonStringUtil.isEmpty(qis_invoice_date) && CommonStringUtil.isEmpty(etq_no)){
			MsgInfo info=new MsgInfo();
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","ETQ单号"));
			info.setErrcode("validator.required");
			errors.add(info);
		}
		
	}

	public void deleteServiceRepairManage(ActionForm form, SqlSessionManager conn) throws Exception {
		ServiceRepairManageMapper mapper=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		mapper.deleteServiceRepairManage(entity);
	}

	/**
	 * 取得报价需要的品保判定信息
	 * @param detailResponse
	 * @param material_id
	 * @param conn
	 */
	public void getQaInfo2Quotation(Map<String, Object> detailResponse, String material_id, SqlSession conn) {
		ServiceRepairManageMapper mapper=conn.getMapper(ServiceRepairManageMapper.class);

		// 考虑如果发生系统内返还
		List<ServiceRepairManageEntity> results = mapper.searchServiceRepairByMaterial_id(material_id);

		if (results.size() == 1) {
			ServiceRepairManageEntity result = results.get(0);
			detailResponse.put("qa_rank", result.getCountermeasures());
			detailResponse.put("qa_service_free", CodeListUtils.getValue("service_free_flg", "" + result.getService_free_flg()));
		}
	}
	/**
	 * 保期内返品+QIS品分析 详细数据
	 * @param instance
	 * @param conn
	 * @return
	 */
	public ServiceRepairManageForm searchServiceRepairAnalysis(ActionForm form,SqlSession conn,Map<String, Object> listResponse) throws Exception {
		
		ServiceRepairManageForm serviceRepairManageForm = new ServiceRepairManageForm();
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//获取分析表的数据
		ServiceRepairManageEntity resultEntity = dao.searchServiceRepairAnalysis(entity);
		
		//获取保内返品分析图像
		List<ServiceRepairManageEntity> resultAnalysisGrams = dao.searchServiceRepairAnalysisGram(entity);
		List<ServiceRepairManageForm> repairManageForms = new ArrayList<ServiceRepairManageForm>();
		if(resultAnalysisGrams!=null){
			BeanUtil.copyToFormList(resultAnalysisGrams, repairManageForms, CopyOptions.COPYOPTIONS_NOEMPTY,ServiceRepairManageForm.class);
		}
		
		listResponse.put("resultAnalysisGrams",repairManageForms);
		
		if(resultEntity!=null){
			BeanUtil.copyToForm(resultEntity, serviceRepairManageForm,CopyOptions.COPYOPTIONS_NOEMPTY);
			serviceRepairManageForm.setService_repair_flg(CodeListUtils.getValue("qa_material_service_repair", serviceRepairManageForm.getService_repair_flg()));
		}
		
		return serviceRepairManageForm;
	}
	/**
	 * 更新分析表数据
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void updateServiceRepairManageAnalysis(HttpServletRequest request,ServiceRepairManageForm serviceRepairManageForm,SqlSessionManager conn)throws Exception{
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(serviceRepairManageForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		//更新保内QIS分析表数据
		dao.updateServiceRepairAnalysis(entity);
		
		//多个图片文件上传UUID和seq_no
		List<ServiceRepairManageForm> serviceRepairManageForms = this.getPostKeys(request.getParameterMap());

		if (serviceRepairManageForms.size() == 0) return;

		//获取最大的seq_no
		String maxSeqNo = dao.getMaxSeqNo(entity);
		for(int i = 0;i<serviceRepairManageForms.size();i++){
			ServiceRepairManageForm returnForm = serviceRepairManageForms.get(i);
			
			String seq_no = returnForm.getSeq_no();
			
			//判断如果最大的seq_no为空(表示该保内返品对象还未上传过图片,则seq_no根据上传图片的个数进行分配)
			if(CommonStringUtil.isEmpty(maxSeqNo)){
				if(CommonStringUtil.isEmpty(seq_no)){
					seq_no = (i+1)+"";
				}
			//如果最大的seq_no不为空,seq_no则根据最大的seq_no往后累加
			}else{
				    if(CommonStringUtil.isEmpty(seq_no)){
				    	seq_no = (Integer.parseInt(maxSeqNo)+1)+"";
				    }
			}
			
			entity.setSeq_no(Integer.parseInt(seq_no));
			entity.setImage_uuid(returnForm.getImage_uuid());
			
			//更新保内返品分析图像
			dao.updateServiceRepairAnalysisGram(entity);
		}
	}
	
	/**
	 * 读取上传多个图片的uuid和seq_no
	 * 
	 * @param request
	 *            页面请求
	 * @return 返回值
	 */
	public List<ServiceRepairManageForm> getPostKeys(Map<String, String[]> parameters) {

		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		List<ServiceRepairManageForm>  serviceRepairManageForms= new AutofillArrayList<ServiceRepairManageForm>(ServiceRepairManageForm.class);
		// 整理提交数据
		for (String parameterKey : parameters.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("append_images".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameters.get(parameterKey);

					if ("image_uuid".equals(column)) {
						serviceRepairManageForms.get(icounts).setImage_uuid(value[0]);
					} else if ("seq_no".equals(column)) {
						serviceRepairManageForms.get(icounts).setSeq_no(value[0]);
					}
				}
			}
		}

		return serviceRepairManageForms;
	}

	/**
	 * 更新分析表数据
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void updateMention(ServiceRepairManageForm serviceRepairManageForm,SqlSessionManager conn)throws Exception{
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(serviceRepairManageForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		//更新提要内容
		dao.updateMention(entity);
	}

	/**
	 * 删除保内返品分析图像
	 * @param serviceRepairManageForm 页面表单
	 * @param conn 数据库会话
	 */
	public void deleteImage(ServiceRepairManageForm serviceRepairManageForm,
			SqlSessionManager conn) {
		
		ServiceRepairManageEntity conditionEntity = new ServiceRepairManageEntity();
		
		ServiceRepairManageMapper dao = conn.getMapper(ServiceRepairManageMapper.class);
		
		BeanUtil.copyToBean(serviceRepairManageForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//删除
		dao.deleteAnalysisGram(conditionEntity);
		
	}

	/**
	 * 已开始判定任务退回等待
	 * @throws Exception 
	 */
	public void actionBack(ActionForm form, SqlSessionManager conn,
			List<MsgInfo> errors) throws Exception {
		ServiceRepairManageEntity conditionEntity = new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		ServiceRepairManageMapper srMapper = conn.getMapper(ServiceRepairManageMapper.class);
		SoloProductionFeatureMapper spfMapper = conn.getMapper(SoloProductionFeatureMapper.class);

		// 检查是否存在进行中任务
		List<String> lis = spfMapper.checkWorkingByModelName(conditionEntity);

		// 如果存在不允许退回
		if (lis.size() > 0) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("qa_reception_time");
			msgInfo.setErrcode("info.qa.undo.working");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.qa.undo.working"));
			errors.add(msgInfo);
			return;
		}
		
		// 修改管理信息
		srMapper.undoRefeeWork(conditionEntity);
		// 删除作业信息
		spfMapper.undoWorkingByModelName(conditionEntity);
	}
}

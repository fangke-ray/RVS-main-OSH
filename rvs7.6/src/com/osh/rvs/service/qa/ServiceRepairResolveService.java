package com.osh.rvs.service.qa;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.qa.ServiceRepairResolveEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.qa.ServiceRepairResolveForm;
import com.osh.rvs.mapper.qa.ServiceRepairManageMapper;
import com.osh.rvs.mapper.qa.ServiceRepairResolveMapper;

import de.schlichtherle.io.FileOutputStream;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class ServiceRepairResolveService {
	private static Logger log = Logger.getLogger(ServiceRepairResolveService.class);
	/**
	 * 保期内返品+QIS品分析 详细数据
	 * 
	 * @param instance
	 * @param conn
	 * @return
	 */
	public List<ServiceRepairResolveForm> searchServiceRepairResolve(ActionForm form, SqlSession conn) throws Exception {
		List<ServiceRepairResolveForm> serviceRepairResolveForms = new ArrayList<ServiceRepairResolveForm>();
		ServiceRepairResolveMapper dao = conn.getMapper(ServiceRepairResolveMapper.class);
		ServiceRepairResolveEntity entity = new ServiceRepairResolveEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 获取分析表的数据
		List<ServiceRepairResolveEntity> serviceRepairManageEntities = dao.searchServiceRepairResolve(entity);
		if (serviceRepairManageEntities.size() > 0) {
			BeanUtil.copyToFormList(serviceRepairManageEntities, serviceRepairResolveForms,
					CopyOptions.COPYOPTIONS_NOEMPTY, ServiceRepairResolveForm.class);
			for (int i = 0; i < serviceRepairResolveForms.size(); i++) {
				ServiceRepairResolveForm serviceRepairResolveForm = serviceRepairResolveForms.get(i);
				serviceRepairResolveForm.setLiability_flg(CodeListUtils.getValue("liability_flg",
						serviceRepairResolveForm.getLiability_flg()));
			}
		}
		return serviceRepairResolveForms;
	}

	/** 保内QIS型号名字集合 **/
	public List<String> getModelNameAutoCompletes(SqlSession conn) {
		ServiceRepairManageMapper dao = conn.getMapper(ServiceRepairManageMapper.class);
		return dao.getModelNameAutoCompletes();
	}

	/* 更新保内返品对策对应内容 */
	public void updateServiceRepairResolve(ServiceRepairResolveForm serviceRepairResolveForm, SqlSessionManager conn,String isSolution,String isResolve)
			throws Exception {
		ServiceRepairResolveMapper dao = conn.getMapper(ServiceRepairResolveMapper.class);
		ServiceRepairResolveEntity serviceRepairResolveEntity = new ServiceRepairResolveEntity();

		Calendar calendar = Calendar.getInstance();
		String current_date = DateUtil.toString(calendar.getTime(), DateUtil.DATE_PATTERN);

		// 对策确认button点击了之后  设置对策时间是当前时间
		if (!CommonStringUtil.isEmpty(isSolution)) {
			serviceRepairResolveForm.setSolution_date(current_date);
		}
		// 对应完成确认button点击了之后 设置对策时间是当前时间
		if (!CommonStringUtil.isEmpty(isResolve)) {
			serviceRepairResolveForm.setResolve_date(current_date);
		}

		BeanUtil.copyToBean(serviceRepairResolveForm, serviceRepairResolveEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		// 更新
		dao.updateSServiceRepairResolve(serviceRepairResolveEntity);
	}

	public String createWorkReport(ActionForm form, HttpServletRequest request,SqlSession conn) throws Exception {
		String path ="";
		List<ServiceRepairResolveForm> lists = this.searchServiceRepairResolve(form, conn);
		//判断service_repair_analysis表的分析对应建议是否为空 如果是空值使用第一个模板--否则使用第二个模板
		if(CommonStringUtil.isEmpty(lists.get(0).getAnalysis_correspond_suggestion())){
			path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "保修期内返修品分析表.xls";
		}else{
			path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "保修期内返修品分析表-2.xls";
		}
		
		String cacheName = "保修期内返修品分析表(北京)" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM")
				+ "\\" + cacheName;

		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		ServiceRepairResolveMapper dao = conn.getMapper(ServiceRepairResolveMapper.class);
		ServiceRepairResolveForm resolveForm = (ServiceRepairResolveForm)form;
		ServiceRepairResolveEntity serviceRepairResolveEntity = new ServiceRepairResolveEntity();
		BeanUtil.copyToBean(resolveForm, serviceRepairResolveEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		//查询出担当人
		String jobNo = dao.searchJobNo(serviceRepairResolveEntity);
		
		OutputStream out = null;
		InputStream in = null;
		
		try {
				in = new FileInputStream(cachePath);//读取文件 
				HSSFWorkbook work=new HSSFWorkbook(in);//创建xls文件
				HSSFSheet sheet=work.getSheetAt(0);//取得第一个Sheet
				
				HSSFFont font=work.createFont();
				font.setFontHeightInPoints((short)9);
				font.setFontName("微软雅黑");
				
				//设置内容格式和字体的大小字体方向
				HSSFCellStyle fontStyle = work.createCellStyle();
				fontStyle.setFont(font);
				fontStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				fontStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_GENERAL);
				fontStyle.setWrapText(true); 
				
				if(lists.size()>0){
				ServiceRepairResolveForm serviceRepairResolveForm = lists.get(0);
	
					// 分析表编号
					HSSFCell analysisNoCell = sheet.getRow(1).getCell(1);
					analysisNoCell.setCellValue(serviceRepairResolveForm.getAnalysis_no());
	
					// 产品名称
					HSSFCell modelNameCell = sheet.getRow(3).getCell(3);
					modelNameCell.setCellValue(serviceRepairResolveForm.getModel_name());
	
					// 机身号
					HSSFCell serialNoCell = sheet.getRow(4).getCell(3);
					serialNoCell.setCellValue(serviceRepairResolveForm.getSerial_no());
					serialNoCell.setCellType(HSSFCell.CELL_TYPE_STRING);
	
					// 客户名称
					HSSFCell customerNameCell = sheet.getRow(5).getCell(3);
					customerNameCell.setCellValue(serviceRepairResolveForm.getCustomer_name());
	
					// 上次完成日
					HSSFCell lastShippingDateCell = sheet.getRow(6).getCell(3);
					lastShippingDateCell.setCellValue(serviceRepairResolveForm.getLast_shipping_date());
	
					// 上次不合格内容/用户提出--上次故障内容
					HSSFCell lastTroubleFeatureCell = sheet.getRow(6).getCell(6);
					lastTroubleFeatureCell.setCellValue(serviceRepairResolveForm.getLast_trouble_feature());
	
					// 上次修理NO.
					HSSFCell lastSorcNoCell = sheet.getRow(7).getCell(3);
					lastSorcNoCell.setCellValue(serviceRepairResolveForm.getLast_sorc_no());
					
					// 上次等级
					HSSFCell lastRankCell = sheet.getRow(8).getCell(3);
					lastRankCell.setCellValue("OCM："+(serviceRepairResolveForm.getLast_ocm_rank()==null? "":serviceRepairResolveForm.getLast_ocm_rank())+"   返修技术部："+serviceRepairResolveForm.getLast_rank());
					
					// 此次受理日
					HSSFCell receptionDateCell = sheet.getRow(9).getCell(3);
					receptionDateCell.setCellValue(serviceRepairResolveForm.getReception_time());
	
					// 此次不合格内容/用户提出--不良内容
					HSSFCell fixDemandCell = sheet.getRow(9).getCell(5);
					fixDemandCell.setCellValue(serviceRepairResolveForm.getFix_demand());
	
					// 此次修理NO.
					HSSFCell sorcNoCell = sheet.getRow(10).getCell(3);
					sorcNoCell.setCellValue(serviceRepairResolveForm.getSorc_no());
	
					// 此次使用情况
					HSSFCell usageFrequencyCell = sheet.getRow(11).getCell(3);
					usageFrequencyCell.setCellValue(serviceRepairResolveForm.getUsage_frequency());
					
					String analysis_resultStr =CodeListUtils.getValue("analysis_result",serviceRepairResolveForm.getAnalysis_result());
					
					HSSFCell str1 =null;
					HSSFCell str2 =null;
					for(int i=13;i<=16;i++){
						str1= sheet.getRow(i).getCell(3);
						str2= sheet.getRow(i).getCell(5);
						
						if(str1.getStringCellValue().contains(analysis_resultStr)){
							str1.setCellValue(str1.getStringCellValue().replace("□", "■"));
						}
						if(str2.getStringCellValue().contains(analysis_resultStr)){
							str2.setCellValue(str2.getStringCellValue().replace("□", "■"));
						}
					}
					
					//再修理方案
					HSSFCell countermeasuresCell = sheet.getRow(14).getCell(6);
					countermeasuresCell.setCellValue(serviceRepairResolveForm.getCountermeasures());
					
					try{
					    String imagePath =PathConsts.BASE_PATH + PathConsts.PCS_TEMPLATE + "\\pic\\" + "operator\\" +jobNo;
					    HSSFPatriarch patri1 = sheet.createDrawingPatriarch();
					    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					    BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
						ImageIO.write(bufferedImage, "png",byteArrayOutputStream );
					//确认人--图片
					HSSFClientAnchor anchor1 = new HSSFClientAnchor(0,0,1023,255,(short)11,15,(short)11,15);
				    patri1.createPicture(anchor1 ,work.addPicture(byteArrayOutputStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize();
				    
					    //判断service_repair_analysis表的分析对应建议是否为空 如果是空值使用第一个模板--否则使用第二个模板(导出图位置不同)
					    if(CommonStringUtil.isEmpty(lists.get(0).getAnalysis_correspond_suggestion())){
						    //担当人--图片
						    HSSFPatriarch patri2 = sheet.createDrawingPatriarch();
						    HSSFClientAnchor anchor2 = new HSSFClientAnchor(0,0,15,11,(short)11,35,(short)12,36);
						    patri2.createPicture(anchor2 ,work.addPicture(byteArrayOutputStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize();
					    }else{
					    	//第二个模板的担当人图片1
						    HSSFPatriarch patri3 = sheet.createDrawingPatriarch();
						    HSSFClientAnchor anchor3 = new HSSFClientAnchor(0,0,15,11,(short)11,48,(short)12,49);
						    patri3.createPicture(anchor3 ,work.addPicture(byteArrayOutputStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize();
						    
						    //第二个模板的担当人图片2
						    HSSFPatriarch patri4 = sheet.createDrawingPatriarch();
						    HSSFClientAnchor anchor4 = new HSSFClientAnchor(0,0,15,11,(short)11,79,(short)12,80);
						    patri4.createPicture(anchor4 ,work.addPicture(byteArrayOutputStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize();
						   
						    //第二个模板的担当人图片3
						    HSSFPatriarch patri5 = sheet.createDrawingPatriarch();
						    HSSFClientAnchor anchor5 = new HSSFClientAnchor(0,0,15,11,(short)11,103,(short)12,104);
						    patri5.createPicture(anchor5 ,work.addPicture(byteArrayOutputStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize();
						    
						    //对应
							HSSFCell solutionContentCell = sheet.getRow(57).getCell(1);
							solutionContentCell.setCellValue(serviceRepairResolveForm.getSolution_content());
							
							// 处理对策
							HSSFCell dealCell = sheet.getRow(60).getCell(1);
							dealCell.setCellValue(serviceRepairResolveForm.getAnalysis_correspond_suggestion());
							
							//对策完成确认
							HSSFCell resolveContentCell = sheet.getRow(81).getCell(1);
							resolveContentCell.setCellValue(serviceRepairResolveForm.getResolve_content());
					    }
					}catch(Exception e){
						log.info("该担当人图标不存在");
					}
					//故障描述
					HSSFCell troubleDiscribeCell = sheet.getRow(19).getCell(1);
					troubleDiscribeCell.setCellValue(serviceRepairResolveForm.getTrouble_discribe());
					troubleDiscribeCell.setCellStyle(fontStyle);
					
					//故障原因
					HSSFCell troubleCauseCell = sheet.getRow(28).getCell(1);
					troubleCauseCell.setCellValue(serviceRepairResolveForm.getTrouble_discribe());
					troubleCauseCell.setCellStyle(fontStyle);
				}
			out= new FileOutputStream(cachePath);
			work.write(out);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		return cacheName;
	}
}

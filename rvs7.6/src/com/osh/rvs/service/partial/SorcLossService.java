package com.osh.rvs.service.partial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.osh.rvs.bean.partial.BadLossSummaryEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.SorcLossEntity;
import com.osh.rvs.bean.qa.ServiceRepairManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.partial.SorcLossForm;
import com.osh.rvs.mapper.partial.BadLossSummaryMapper;
import com.osh.rvs.mapper.partial.SorcLossMapper;
import com.osh.rvs.mapper.qa.ServiceRepairManageMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;


public class SorcLossService {

  /**
   * 查询损金数据
   * @param sorcLossEntity
   * @param conn
   * @param errors
   * @return
   * @throws Exception
   */
  public List<SorcLossForm> searchSorcLoss(SorcLossEntity sorcLossEntity,SqlSession conn,List<MsgInfo> errors) throws Exception{
	 
	  List<SorcLossForm> sorcLossForms = new ArrayList<SorcLossForm>();	 
	  SorcLossMapper dao = conn.getMapper(SorcLossMapper.class);
	  
	  //查询 不属于保内返品--SORC损金 详细数据
	  List<SorcLossEntity> sorcLossEntities = dao.searchSorcLoss(sorcLossEntity);
	  
	  BeanUtil.copyToFormList(sorcLossEntities, sorcLossForms,CopyOptions.COPYOPTIONS_NOEMPTY,SorcLossForm.class);
	  
	  return sorcLossForms;
  }
  
  /**
   *保内返品--维修对象 
   * @param sorcLossEntity
   * @param conn
   * @param errors
   * @return
   * @throws Exception
   */
  public List<SorcLossForm> searchSorcLossOfRepair(SorcLossEntity sorcLossEntity,SqlSession conn,List<MsgInfo> errors) throws Exception{
	  List<SorcLossForm> sorcLossForms = new ArrayList<SorcLossForm>();	 
	  SorcLossMapper dao = conn.getMapper(SorcLossMapper.class);
	  
	  //查询 保内返品---维修对象数据
	  List<SorcLossEntity> sorcLossEntities = dao.searchSorcLossOfRepair(sorcLossEntity);
	  
	  BeanUtil.copyToFormList(sorcLossEntities, sorcLossForms,CopyOptions.COPYOPTIONS_NOEMPTY,SorcLossForm.class);
	  
	  return sorcLossForms;
  }
  
  
  /**
   * 查询月损金数据
   * @param sorcLossEntity
   * @param conn
   * @param errors
   * @return
   * @throws Exception
   */
  public List<SorcLossForm> searchSorcLossMonth(SorcLossEntity sorcLossEntity,SqlSession conn,List<MsgInfo> errors) throws Exception{
	 
	  List<SorcLossForm> sorcLossForms = new ArrayList<SorcLossForm>();	 
	  SorcLossMapper dao = conn.getMapper(SorcLossMapper.class);
	  
	  //查询 月损金 详细数据
	  List<SorcLossEntity> sorcLossEntities = dao.searchSorcLossMonth(sorcLossEntity);
	  
	  BeanUtil.copyToFormList(sorcLossEntities, sorcLossForms,CopyOptions.COPYOPTIONS_NOEMPTY,SorcLossForm.class);
	  
	  return sorcLossForms;
  }
  
  /**
   * 更新	SORC损金147PA
   * @param form
   * @param conn
   * @param errors
   * @throws Exception
   */
  public void updateSorcLoss(SorcLossEntity sorcLossEntity,SqlSessionManager conn,List<MsgInfo> errors) throws Exception{
	  SorcLossMapper dao = conn.getMapper(SorcLossMapper.class);
	 
	  //更新 SORC损金
	  dao.updateSorcLoss(sorcLossEntity);

	  // 更新零件订购分类
	  if (sorcLossEntity.getBelongs() != null) {
		  MaterialPartialDetailEntity mpdEntity = new MaterialPartialDetailEntity();
		  mpdEntity.setBelongs(sorcLossEntity.getBelongs());
		  mpdEntity.setMaterial_partial_detail_key(sorcLossEntity.getMaterial_partial_detail_key());
		  dao.updateMaterialPartialDetail(mpdEntity);
	  }
  }
  
  /**
   *更新不良简述 
   * @param sorcLossEntity
   * @param conn
   * @param errors
   * @throws Exception
   */
  public void updateNogoodDescription(SorcLossEntity sorcLossEntity,SqlSessionManager conn,List<MsgInfo> errors)throws Exception{
	  SorcLossMapper dao = conn.getMapper(SorcLossMapper.class);
		 
	  //更新 SORC损金
	  dao.updateNogoodDescription(sorcLossEntity);
  }

	/**
	 * 月损金下载
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String createMonthLossReport(SorcLossForm outputDate,HttpServletRequest request,SqlSession conn)throws Exception{

		//当前
		String ocm_shipping_month  =(String) request.getSession().getAttribute("ocm_shipping_month");
		
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "SORC损金模板.xls";
		String cacheName =ocm_shipping_month+"SORC损金" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<SorcLossForm> list=(List)request.getSession().getAttribute("result");
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<SorcLossForm> repairList=(List)request.getSession().getAttribute("resultOfRepair");
		
		BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);
		OutputStream out = null;
		InputStream in = null;
		
		try {
			in = new FileInputStream(cachePath);//读取文件 
			HSSFWorkbook work=new HSSFWorkbook(in);//创建xls文件
			HSSFSheet sheet=work.getSheetAt(0);//取得第一个Sheet
			String sheetName="";
			if(CommonStringUtil.isEmpty(outputDate.getOcm_shipping_month())){
				sheetName=outputDate.getOcm_shipping_date();
			}else{
				sheetName=outputDate.getOcm_shipping_month();
			}
			work.setSheetName(0, sheetName.replace("/", ""));
			int index=0;
			HSSFFont font=work.createFont();
			font.setFontHeightInPoints((short)9);
			font.setFontName("微软雅黑");

			HSSFDataFormat currecyDf = work.createDataFormat();
			short sCurrecyDf = currecyDf.getFormat("\"$ \"#,##0.00_);(\"$ \"#,##0.00)");

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
			
			/*设置单元格内容居右显示*/
			HSSFCellStyle styleAlignRight= work.createCellStyle();
			styleAlignRight.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			styleAlignRight.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			styleAlignRight.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleAlignRight.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleAlignRight.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			styleAlignRight.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			styleAlignRight.setWrapText(true); 
			styleAlignRight.setFont(font);

			HSSFCellStyle styleCurrecy = work.createCellStyle();
			styleCurrecy.cloneStyleFrom(styleAlignRight);
			styleCurrecy.setDataFormat(sCurrecyDf);

			String sorc_no="";
			int i=0;
			BadLossSummaryEntity badLossSummaryEntity = dao.searchLossSummaryFromOfYear(
					sheetName.substring(0, 4),
					sheetName.substring(5, 7));

			for(i=0;i<list.size();i++){
				  SorcLossForm sorcLossForm=list.get(i);

				  HSSFRow row=sheet.createRow(i+1);//从第二行开始创建
				 
				  if(!sorc_no.equals(sorcLossForm.getSorc_no())){
					  sorc_no =sorcLossForm.getSorc_no();
					  index++;
				  }
				  //索引
				  HSSFCell indexCell = row.createCell(0);
				  indexCell.setCellValue(index);
				  indexCell.setCellStyle(styleAlignLeft);
				  
				  //出货日期
				  HSSFCell shippingDateCell =  row.createCell(1);
				  shippingDateCell.setCellValue(sorcLossForm.getOcm_shipping_date());
				  shippingDateCell.setCellStyle(styleAlignLeft);
				 
				  //修理编号
				  HSSFCell sorcnoCell =  row.createCell(2);
				  sorcnoCell.setCellValue(sorcLossForm.getSorc_no());
				  sorcnoCell.setCellStyle(styleAlignLeft);
				  
				  //型号
				  HSSFCell modelNameCell =  row.createCell(3);
				  modelNameCell.setCellValue(sorcLossForm.getModel_name());
                  modelNameCell.setCellStyle(styleAlignLeft);
				  
                  //机身号
				  HSSFCell serialnoCell =  row.createCell(4);
				  serialnoCell.setCellValue(sorcLossForm.getSerial_no());
				  serialnoCell.setCellStyle(styleAlignLeft);
				  
				  //分室
				  HSSFCell ocmCell =  row.createCell(5);
				  ocmCell.setCellValue(CodeListUtils.getValue("material_ocm",sorcLossForm.getOcm()));
				  ocmCell.setCellStyle(styleAlignLeft);
				  
				  //OCM RANK
				  HSSFCell ocmRankCell =  row.createCell(6);
				  ocmRankCell.setCellValue(CodeListUtils.getValue("material_ocm_rank", sorcLossForm.getOcm_rank()));
				  ocmRankCell.setCellStyle(styleAlignCenter);
				  
				  //SORC RANK
				  HSSFCell sorcRankCell =  row.createCell(7);
				  sorcRankCell.setCellValue(CodeListUtils.getValue("material_level", sorcLossForm.getLevel()));
				  sorcRankCell.setCellStyle(styleAlignCenter);
				  
				  //等级变更
				  HSSFCell changeRankCell =  row.createCell(8);
				  String change_rank ="";
				  if(!CommonStringUtil.isEmpty(sorcLossForm.getOcm_rank())){
					  change_rank = getChanged(sorcLossForm.getOcm_rank(), sorcLossForm.getLevel());
				  }

				  changeRankCell.setCellValue(change_rank);
				  changeRankCell.setCellStyle(styleAlignCenter);
				  
				  //发现工程
				  HSSFCell lineCell =  row.createCell(9);
				  lineCell.setCellValue(CodeListUtils.getValue("partial_append_belongs",sorcLossForm.getBelongs()));
				  lineCell.setCellStyle(styleAlignLeft);
				  
				  //责任区分
				  HSSFCell liabilityFlgCell =  row.createCell(10);
				  String liabilityFlg =sorcLossForm.getLiability_flg();
				  if(CommonStringUtil.isEmpty(liabilityFlg)){
					  liabilityFlg="3";
				  }
				  liabilityFlgCell.setCellValue(CodeListUtils.getValue("liability_flg",liabilityFlg));
				  liabilityFlgCell.setCellStyle(styleAlignLeft);
				  
				  //不良简述
				  HSSFCell nogoodDescriptionCell =  row.createCell(11);
				  nogoodDescriptionCell.setCellValue(sorcLossForm.getNogood_description());
				  nogoodDescriptionCell.setCellStyle(styleAlignLeft);
				  
				  //零件型号
				  HSSFCell codeCell =  row.createCell(12);
				  codeCell.setCellValue(sorcLossForm.getCode());
				  codeCell.setCellStyle(styleAlignLeft);
				  
				  //数量
				  HSSFCell quantityCell =  row.createCell(13);
				  quantityCell.setCellValue(Integer.parseInt(sorcLossForm.getQuantity()));
				  quantityCell.setCellStyle(styleAlignRight);
				  
				  //零件单价
				  HSSFCell priceCell =  row.createCell(14);
				  
				  //零件单价
				  Double price;
				  //当检索category表的kind==06时，则汇率是美元，如果不是的话，要根据出货日期查询到当前年月的汇率
				  BigDecimal bd = new BigDecimal(sorcLossForm.getPrice());
				  if("6".equals(sorcLossForm.getKind()) && badLossSummaryEntity != null && badLossSummaryEntity.getE_u_settlement() != null){	
					  price = bd.multiply(badLossSummaryEntity.getE_u_settlement()).doubleValue();
				  }	else{
					  price = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();					 
				  }			  
				  priceCell.setCellValue(price);
				  priceCell.setCellStyle(styleCurrecy);			  
				  
				  //报价差异损金
				  HSSFCell totalPriceCell =  row.createCell(15);
				  totalPriceCell.setCellStyle(styleCurrecy);
				  totalPriceCell.setCellValue(Integer.parseInt(sorcLossForm.getQuantity())*price);
				  //totalPriceCell.setCellFormula("N"+(i+2)+" * O"+(i+2));//报价差异损金设置EXCEL内置相乘函数
				  
				  //修理原价
				  HSSFCell repairPriceCell =  row.createCell(16);
				  repairPriceCell.setCellValue("");
				  repairPriceCell.setCellStyle(styleAlignRight);
				  
				  //更新价格
				  HSSFCell updatePriceCell =  row.createCell(17);
				  updatePriceCell.setCellValue("");
				  updatePriceCell.setCellStyle(styleAlignRight);
				  
				  //有偿与否
				  HSSFCell serviceFreeFlgCell =  row.createCell(18);
				  serviceFreeFlgCell.setCellValue(CodeListUtils.getValue("service_free_flg",sorcLossForm.getService_free_flg()));
				  serviceFreeFlgCell.setCellStyle(styleAlignLeft);
				  
				  //OSH/OCM责任承担
				  HSSFCell oshOcmFlgCell =  row.createCell(19);
				  oshOcmFlgCell.setCellValue("");
				  oshOcmFlgCell.setCellStyle(styleAlignLeft);
				  
				  //备注
				  HSSFCell commentCell =  row.createCell(20);
				  commentCell.setCellValue(sorcLossForm.getComment());
				  commentCell.setCellStyle(styleAlignLeft);
				  
			}

			for(int j=0;j<repairList.size();j++){
				  SorcLossForm sorcLossForm=repairList.get(j);
				  
				  HSSFRow row=sheet.createRow(i+j+1);//从第二行开始创建
				 
				  if(!sorc_no.equals(sorcLossForm.getSorc_no())){
					  sorc_no =sorcLossForm.getSorc_no();
					  index++;
				  }
				  
				  //索引
				  HSSFCell indexCell = row.createCell(0);
				  indexCell.setCellValue(index);
				  indexCell.setCellStyle(styleAlignLeft);
				  
				  //出货日期
				  HSSFCell shippingDateCell =  row.createCell(1);
				  shippingDateCell.setCellValue(sorcLossForm.getOcm_shipping_date());
				  shippingDateCell.setCellStyle(styleAlignLeft);
				 
				  //修理编号
				  HSSFCell sorcnoCell =  row.createCell(2);
				  sorcnoCell.setCellValue(sorcLossForm.getSorc_no());
				  sorcnoCell.setCellStyle(styleAlignLeft);
				  
				  //型号
				  HSSFCell modelNameCell =  row.createCell(3);
				  modelNameCell.setCellValue(sorcLossForm.getModel_name());
                modelNameCell.setCellStyle(styleAlignLeft);
				  
                //机身号
				  HSSFCell serialnoCell =  row.createCell(4);
				  serialnoCell.setCellValue(sorcLossForm.getSerial_no());
				  serialnoCell.setCellStyle(styleAlignLeft);
				  
				  //分室
				  HSSFCell ocmCell =  row.createCell(5);
				  ocmCell.setCellValue(CodeListUtils.getValue("material_ocm",sorcLossForm.getOcm()));
				  ocmCell.setCellStyle(styleAlignLeft);
			  
				  //OCM RANK
				  HSSFCell ocmRankCell =  row.createCell(6);
				  ocmRankCell.setCellValue(CodeListUtils.getValue("material_ocm_rank", sorcLossForm.getOcm_rank()));
				  ocmRankCell.setCellStyle(styleAlignCenter);
				  
				  //SORC RANK
				  HSSFCell sorcRankCell =  row.createCell(7);
				  sorcRankCell.setCellValue(CodeListUtils.getValue("material_level", sorcLossForm.getLevel()));
				  sorcRankCell.setCellStyle(styleAlignCenter);
				  
				  //等级变更
				  HSSFCell changeRankCell =  row.createCell(8);
				  String change_rank ="";
				  if(!CommonStringUtil.isEmpty(sorcLossForm.getOcm_rank())){
					  change_rank = getChanged(sorcLossForm.getOcm_rank(), sorcLossForm.getLevel());
				  }

				  changeRankCell.setCellValue(change_rank);
				  changeRankCell.setCellStyle(styleAlignCenter);
 
				  //发现工程
				  HSSFCell lineCell =  row.createCell(9);
				  lineCell.setCellValue(CodeListUtils.getValue("partial_append_belongs","9"));
				  lineCell.setCellStyle(styleAlignLeft);
				  
				  //责任区分
				  HSSFCell liabilityFlgCell =  row.createCell(10);
				  String liability_flg = CodeListUtils.getValue("liability_flg",sorcLossForm.getLiability_flg());
				  if (CommonStringUtil.isEmpty(liability_flg)) {
					  liabilityFlgCell.setCellValue("非自责");
				  } else {
					  liabilityFlgCell.setCellValue(liability_flg);
				  }
				  liabilityFlgCell.setCellStyle(styleAlignLeft);
				  
				  //不良简述
				  HSSFCell nogoodDescriptionCell =  row.createCell(11);
				  nogoodDescriptionCell.setCellValue(sorcLossForm.getCountermeasures());
				  nogoodDescriptionCell.setCellStyle(styleAlignLeft);
				  
				  //零件型号
				  HSSFCell codeCell =  row.createCell(12);
				  codeCell.setCellValue("");
				  codeCell.setCellStyle(styleAlignLeft);
				  
				  //数量
				  HSSFCell quantityCell =  row.createCell(13);
				  quantityCell.setCellValue(Integer.parseInt(sorcLossForm.getQuantity()));
				  quantityCell.setCellStyle(styleAlignRight);
				  
				  //零件单价
				  HSSFCell priceCell =  row.createCell(14);
				  
				  //零件单价
				  Double price;
				  //当检索category表的kind==06时，则汇率是美元，如果不是的话，要根据出货日期查询到当前年月的汇率
				  BigDecimal bd = new BigDecimal(sorcLossForm.getPrice());
				  if("6".equals(sorcLossForm.getKind()) && badLossSummaryEntity != null && badLossSummaryEntity.getE_u_settlement() != null){	
					  price = bd.multiply(badLossSummaryEntity.getE_u_settlement()).doubleValue();
				  }	else{
					  price = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();					 
				  }			  
				  priceCell.setCellValue(price);
				  priceCell.setCellStyle(styleCurrecy);	
				  
				  //报价差异损金
				  HSSFCell totalPriceCell =  row.createCell(15);
				  totalPriceCell.setCellStyle(styleCurrecy);
				  totalPriceCell.setCellValue(Integer.parseInt(sorcLossForm.getQuantity())*price);
				  //totalPriceCell.setCellFormula("N"+(i+j+2)+" * O"+(i+j+2));//报价差异损金
				  
				  //修理原价
				  HSSFCell repairPriceCell =  row.createCell(16);
				  repairPriceCell.setCellValue("");
				  repairPriceCell.setCellStyle(styleAlignRight);
				  
				  //更新价格
				  HSSFCell updatePriceCell =  row.createCell(17);
				  updatePriceCell.setCellValue("");
				  updatePriceCell.setCellStyle(styleAlignRight);
				  
				  //有偿与否
				  HSSFCell serviceFreeFlgCell =  row.createCell(18);
				  serviceFreeFlgCell.setCellValue(CodeListUtils.getValue("service_free_flg",sorcLossForm.getService_free_flg()));
				  serviceFreeFlgCell.setCellStyle(styleAlignLeft);
				  
				  //OSH/OCM责任承担
				  HSSFCell oshOcmFlgCell =  row.createCell(19);
				  oshOcmFlgCell.setCellValue("");
				  oshOcmFlgCell.setCellStyle(styleAlignLeft);
				  
				  //备注
				  HSSFCell commentCell =  row.createCell(20);
				  commentCell.setCellValue(sorcLossForm.getComment());
				  commentCell.setCellStyle(styleAlignLeft);
				  
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

	private String getChanged(String ocm_rank, String level) {
		if (ocm_rank.equals(level)) return "否";
		if (level.charAt(0) - ocm_rank.charAt(0) == 5) return "否";
		return "是";
	}

	/* 验证出货月或者出货日期必须有一个值 */
	public void customValidate(SorcLossForm sorcLossForm, SqlSession conn, List<MsgInfo> errors) {
		
		if(CommonStringUtil.isEmpty(sorcLossForm.getOcm_shipping_month()) && CommonStringUtil.isEmpty(sorcLossForm.getOcm_shipping_date())){
			MsgInfo error = new MsgInfo();
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.loss.required"));
		    errors.add(error);
		}
	}
	
	public List<String> searchLossDetailOfNogoodDescription(SqlSession conn){
		List<String> list = new ArrayList<String>();
		SorcLossMapper dao = conn.getMapper(SorcLossMapper.class);
		List<SorcLossEntity> sorcLossEntities =dao.searchLossDetailOfNogoodDescription();
		for(SorcLossEntity sorcLossEntity:sorcLossEntities){
			list.add(sorcLossEntity.getNogood_description());
		}		
		return list;
	}

	public boolean checkServiceRepair(String sorc_no, SqlSession conn) {
		ServiceRepairManageMapper srmMapper = conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity = new ServiceRepairManageEntity();
		entity.setSorc_no(sorc_no);
		entity.setService_repair_flg(1);
		entity.setService_free_flg(2);
		List<ServiceRepairManageEntity> list = srmMapper.searchServiceRepair(entity);
		if (list.size() > 0) {
			return true;
		}
		entity.setService_free_flg(3);
		list = srmMapper.searchServiceRepair(entity);
		if (list.size() > 0) {
			return true;
		}
		return false;
	}
	
}

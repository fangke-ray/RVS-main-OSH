package com.osh.rvs.service.partial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.partial.PartialBaseLineValueEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.PartialBaseLineValueForm;
import com.osh.rvs.mapper.partial.PartialBaseLineValueMapping;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PartialBaseLineValueService {
	public List<PartialBaseLineValueForm> searchPartialBaseLineValue(ActionForm form,SqlSession conn) {
		PartialBaseLineValueEntity entity = new PartialBaseLineValueEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		String partialCode=entity.getPartial_code();//零件编号
		String partialName=entity.getPartial_name();//零件名称

		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		List<PartialBaseLineValueForm> responseFormList = new ArrayList<PartialBaseLineValueForm>();
		
		List<PartialBaseLineValueEntity> responseList=null;
		if(!CommonStringUtil.isEmpty(partialCode) || !CommonStringUtil.isEmpty(partialName)){
			if(CommonStringUtil.byteLengthSystem(partialCode)>=3||CommonStringUtil.byteLengthSystem(partialName)>=3){
				responseList=dao.searchPartialBaseLineValueByfactor(entity);
			}else{
				responseList= dao.searchPartialBaseLineValue(entity);
			}
		}else{
			responseList= dao.searchPartialBaseLineValue(entity);
		}
		
		// 复制数据到表单对象
		BeanUtil.copyToFormList(responseList, responseFormList, CopyOptions.COPYOPTIONS_NOEMPTY,PartialBaseLineValueForm.class);
		if (responseFormList.size() > 0) {
			return responseFormList;
		} else {
			return null;
		}
	}
	
	/**
	 * 更新基准值
	 * @param form
	 * @param conn
	 */
	/*public void updateOshForeboardCount(ActionForm form, SqlSessionManager conn){
		PartialBaseLineValueEntity entity = new PartialBaseLineValueEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		if(entity.getOsh_foreboard_count()!=null){
			dao.updateOshForeboardCount(entity);
		}
	}
	*/
	
	/**
	 * 周期折算使用量(半年、三个月)
	 * @param form
	 * @param conn
	 * @return
	 */
	public PartialBaseLineValueForm getTotalQuantityOfCycle(ActionForm form, SqlSession conn,Integer flg){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form,entity,CopyOptions.COPYOPTIONS_NOEMPTY);
		
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		
		entity.setFlg(flg);
		PartialBaseLineValueEntity tempEntity=dao.getTotalQuantityOfCycle(entity);//合计半年
		
		if(tempEntity==null){
			return null;
		}
		
		tempEntity.setEchelonOfAverage(tempEntity.getEchelonOfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		tempEntity.setEchelon1OfAverage(tempEntity.getEchelon1OfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		tempEntity.setEchelon2OfAverage(tempEntity.getEchelon2OfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		tempEntity.setEchelon3OfAverage(tempEntity.getEchelon3OfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		tempEntity.setEchelon4OfAverage(tempEntity.getEchelon4OfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		
		PartialBaseLineValueForm returnForm=new PartialBaseLineValueForm();
		BeanUtil.copyToForm(tempEntity, returnForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		return returnForm;
	}
	
	/**
	 * 周期折算使用量(当月)
	 * @param form
	 * @param conn
	 * @return
	 */
	public PartialBaseLineValueForm getgetAverageQuantityOfCruMon(ActionForm form, SqlSession conn){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form,entity,CopyOptions.COPYOPTIONS_NOEMPTY);
		
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);//获取数据库链接
		PartialBaseLineValueEntity tempEntity=dao.getTotalQuantityOfCycleOFCurMonth(entity);//当月
		
		if(tempEntity==null){
			return null;
		}
		
		tempEntity.setEchelonOfAverage(tempEntity.getEchelonOfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		tempEntity.setEchelon1OfAverage(tempEntity.getEchelon1OfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		tempEntity.setEchelon2OfAverage(tempEntity.getEchelon2OfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		tempEntity.setEchelon3OfAverage(tempEntity.getEchelon3OfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		tempEntity.setEchelon4OfAverage(tempEntity.getEchelon4OfAverage().setScale(2, BigDecimal.ROUND_HALF_UP));
		
		PartialBaseLineValueForm returnForm=new PartialBaseLineValueForm();
		//复制数据到表单对象
		BeanUtil.copyToForm(tempEntity, returnForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		return returnForm;
	}
	
	/**
	 * 非标使用（半年、三个月)
	 * @param form
	 * @param conn
	 * @param flg
	 * @return
	 */
	public BigDecimal getAverageQuantityOfNonStandardCycle(ActionForm form,SqlSession conn,Integer flg){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form,entity,CopyOptions.COPYOPTIONS_NOEMPTY);
		entity.setFlg(flg);
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		
		BigDecimal notStandardOfCount=dao.getAverageQuantityOfNonStandardCycle(entity);
		notStandardOfCount=notStandardOfCount.setScale(2, BigDecimal.ROUND_HALF_UP);
		return notStandardOfCount;
	}
	
	/**
	 * 非标使用（当月)
	 * @param form
	 * @param conn
	 * @param flg
	 * @return
	 */
	public BigDecimal getAverageQuantityOfNonStandardCycleOfCurMonth(ActionForm form,SqlSession conn){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form,entity,CopyOptions.COPYOPTIONS_NOEMPTY);
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		
		BigDecimal notStandardOfCount=dao.getAverageQuantityOfNonStandardCycleOfCurMonth(entity);
		notStandardOfCount=notStandardOfCount.setScale(2, BigDecimal.ROUND_HALF_UP);
		return notStandardOfCount;
	}
	
	/**
	 * 设定非标使用量 	
	 * @param form
	 * @param conn
	 * @return
	 */
	public Integer getNonBomSaftyCount(ActionForm form, SqlSession conn){
		PartialBaseLineValueForm partialBaseLineValueForm=(PartialBaseLineValueForm)form;
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(partialBaseLineValueForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		
		Integer returnCount=dao.getNonBomSaftyCount(entity);
		
		return returnCount;
	}
	
	/**
	 * 按照拉动台数计算标准使用量
	 * @param form
	 * @param request
	 * @param conn
	 * @return
	 */
	public List<PartialBaseLineValueForm> searchForecastSettingAndCountOfSnandard(ActionForm form,SqlSession conn){
		List<PartialBaseLineValueForm> returnFormList=new ArrayList<PartialBaseLineValueForm>();
		
		PartialBaseLineValueForm partialBaseLineValueForm=(PartialBaseLineValueForm)form;
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(partialBaseLineValueForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		
		List<PartialBaseLineValueEntity> entityList=dao.searchForecastSettingAndCountOfSnandard(entity);
		
		BeanUtil.copyToFormList(entityList, returnFormList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialBaseLineValueForm.class);
		
		if(returnFormList.size()>0){
			return returnFormList;
		}else{
			return null;
		}
	}
	
	/**
	 * 标配拉动数合计
	 * @param request
	 * @return
	 */
	public Integer getTotalOfStandardUse(ActionForm form,SqlSession conn){
		
		PartialBaseLineValueForm partialBaseLineValueForm=(PartialBaseLineValueForm)form;
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(partialBaseLineValueForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		
		List<PartialBaseLineValueEntity> entityList=dao.searchForecastSettingAndCountOfSnandard(entity);
		
		Integer totalOfStandardUse=0;
		
		if(entityList.size()>0){
			PartialBaseLineValueEntity entityTemp=null;
			for(int i=0;i<entityList.size();i++){
				entityTemp=entityList.get(i);
				Integer forecast_setting=entityTemp.getForecast_setting();//拉动台数
				Integer countOfStandardPartial=entityTemp.getCountOfStandardPartial();//零件标准使用数
				if(forecast_setting!=null && countOfStandardPartial!=null){
					totalOfStandardUse=totalOfStandardUse+forecast_setting * countOfStandardPartial;
				}
			}
		}
		
		return totalOfStandardUse;
	}
	
	
	/**
	 * 图表
	 * @param form
	 * @param conn
	 * @return
	 */
	public Map<String,Object> getChart(ActionForm form, SqlSession conn){
		PartialBaseLineValueForm partialBaseLineValueForm=(PartialBaseLineValueForm)form;
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(partialBaseLineValueForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		
		Map<String, Object> tLines = new HashMap<String, Object>();

		List<String> axisTextList = new ArrayList<String>();// X轴
		List<Double> list_average_order = new ArrayList<Double>();// 平均订购数
		List<Integer> list_base_line_value = new ArrayList<Integer>();// 基准值
		
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		
		for(int i=6;i>0;i--){
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.MONTH, -i);
			entity.setStart_date(RvsUtils.getStartDate(cal.get(Calendar.YEAR)+"",(cal.get(Calendar.MONTH)+1)+""));
			entity.setEnd_date(RvsUtils.getEndDate(cal.get(Calendar.YEAR)+"",(cal.get(Calendar.MONTH)+1)+""));
			
			Double orderCount=dao.getOrderCountOfAverage(entity);
			Integer osh_foreboard_count=dao.getTotalForeboardCount(entity);
					
			list_average_order.add(orderCount);
			list_base_line_value.add(osh_foreboard_count);
			
			if(cal.get(Calendar.MONTH)==3){
				axisTextList.add(RvsUtils.getBussinessYearString(cal)+"<br>" + (cal.get(Calendar.MONTH)+1) +"月");
			}else {
				axisTextList.add(cal.get(Calendar.MONTH)+1+"月");
			}
		}
		
		tLines.put("list_average_order", list_average_order);
		tLines.put("list_base_line_value", list_base_line_value);
		tLines.put("axisTextList", axisTextList);
		return tLines;
	}
	
	/**
	 * 下载编辑基准值
	 * @param request
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String dowloadPartialBaseLineValue(ActionForm form,SqlSession conn)throws Exception{
		
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "基准值编辑模板.xls";
		String cacheName ="基准值编辑" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		PartialBaseLineValueEntity tempEntity=new PartialBaseLineValueEntity();
		BeanUtil.copyToBean(form, tempEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		PartialBaseLineValueMapping dao=conn.getMapper(PartialBaseLineValueMapping.class);
		
		String partialCode=tempEntity.getPartial_code();//零件编号
		String partialName=tempEntity.getPartial_name();//零件名称
		
		List<PartialBaseLineValueEntity> sEneityList=null;
		
		if(!CommonStringUtil.isEmpty(partialCode) || !CommonStringUtil.isEmpty(partialName)){
			if(CommonStringUtil.byteLengthSystem(partialCode)>=3||CommonStringUtil.byteLengthSystem(partialName)>=3){
				sEneityList=dao.dowloadPartialBaseLineValueByfactor(tempEntity);
			}else{
				sEneityList= dao.dowloadPartialBaseLineValue(tempEntity);
			}
		}else{
			sEneityList= dao.dowloadPartialBaseLineValue(tempEntity);
		}
				
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		OutputStream out = null;
		InputStream in = null;
		
		try{
			in = new FileInputStream(cachePath);//读取文件 
			HSSFWorkbook work=new HSSFWorkbook(in);//创建xls文件
			HSSFSheet sheet=work.getSheetAt(0);//取得第一个Sheet
			
			if(sEneityList!=null){
				HSSFRow row01 = sheet.getRow(0);
				
				Calendar cal01=Calendar.getInstance();
				int curMon=cal01.get(Calendar.MONTH)+1;//当前月份；
				
				cal01.add(Calendar.MONTH, -6);
				int halfMon=cal01.get(Calendar.MONTH)+1;//半年前月份；
				
				Calendar cal02=Calendar.getInstance();
				cal02.add(Calendar.MONTH, -3);
				int oneMon=cal02.get(Calendar.MONTH)+1;//一个月之前月份
				

				String cellValue01=RvsUtils.getBussinessYearString(cal01)+"-"+halfMon+"月～"+RvsUtils.getBussinessYearString(cal01)+"-"+curMon+"月";
				row01.getCell(2).setCellValue(cellValue01);
				
				
				String cellValue02=RvsUtils.getBussinessYearString(cal02)+"-"+oneMon+"月～"+RvsUtils.getBussinessYearString(cal02)+"-"+curMon+"月";
				row01.getCell(4).setCellValue(cellValue02);
				
				int index=2;
				PartialBaseLineValueEntity entity=null;
				
				HSSFFont font=work.createFont();
				font.setFontHeightInPoints((short)10);
				font.setFontName("微软雅黑");
				
				HSSFCellStyle borderStyle = work.createCellStyle();
				borderStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				borderStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				borderStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				borderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				borderStyle.setFont(font);
				
				HSSFCellStyle codeStyle = work.createCellStyle();
				codeStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				codeStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				codeStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				codeStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				codeStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				codeStyle.setFont(font);
				
				HSSFCellStyle cellDigitStyle = work.createCellStyle();    
				cellDigitStyle.setDataFormat(work.createDataFormat().getFormat("0.00")); 
				cellDigitStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				cellDigitStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				cellDigitStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				cellDigitStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				cellDigitStyle.setFont(font);
				
				for(int i=0;i<sEneityList.size();i++){
					entity=sEneityList.get(i);
					HSSFRow row=sheet.createRow(index);
					index++;
					row.createCell(0).setCellValue(i+1);//编号
					row.getCell(0).setCellStyle(borderStyle);
					
					row.createCell(1).setCellValue(entity.getPartial_code());//零件编码
					row.getCell(1).setCellStyle(codeStyle);
					
					row.createCell(2).setCellValue(entity.getCountOfNotStandardOfHalfYear().doubleValue());//半年非标平均使用量
					row.getCell(2).setCellStyle(cellDigitStyle);
					
					row.createCell(3).setCellValue(entity.getQuantityOfHalfYear().doubleValue());//半年总平均使用量
					row.getCell(3).setCellStyle(cellDigitStyle);
					
					row.createCell(4).setCellValue(entity.getCountOfNotStandardOfThreeMonth().doubleValue());//三个月非标平均使用量
					row.getCell(4).setCellStyle(cellDigitStyle);
					
					row.createCell(5).setCellValue(entity.getQuantityOfThreeMonthAge().doubleValue());//三个月总平均使用量
					row.getCell(5).setCellStyle(cellDigitStyle);
					
					row.createCell(6).setCellValue(entity.getCountOfNotStandardOfOneMonth().doubleValue());//上月非标平均使用量
					row.getCell(6).setCellStyle(cellDigitStyle);
					
					row.createCell(7).setCellValue(entity.getQuantityOfBeforeOneMonthAge().doubleValue());// 上月总平均使用量
					row.getCell(7).setCellStyle(cellDigitStyle);
					
					
					row.createCell(8).setCellValue(entity.getCountOfNotStandardOfCurMonth().doubleValue());//本月非标平均使用量
					row.getCell(8).setCellStyle(cellDigitStyle);
					
					row.createCell(9).setCellValue(entity.getQuantityOfOneMonthAge().doubleValue());//本月总平均使用量
					row.getCell(9).setCellStyle(cellDigitStyle);
					
					
					row.createCell(10).setCellValue(entity.getTotalOFForecastSetting());//拉动台数合计使用量
					row.getCell(10).setCellStyle(borderStyle);
					
					
					row.createCell(11).setCellValue(entity.getNon_bom_safty_count());//非标安全库存
					row.getCell(11).setCellStyle(borderStyle);
					
					row.createCell(12).setCellValue(entity.getSorcwh_foreboard_count());//SORCWH基准量当前设置
					row.getCell(12).setCellStyle(borderStyle);
					
					row.createCell(13).setCellValue(entity.getWh2p_foreboard_count());//WH2P基准量当前设置
					row.getCell(13).setCellStyle(borderStyle);
					
					row.createCell(14).setCellStyle(borderStyle);//SORCWH更改基准量设置
					row.createCell(15).setCellStyle(borderStyle);//WH2P更改基准量设置
					row.createCell(16).setCellStyle(borderStyle);//OGZ更改基准量设置
				}
			}
			
			out= new FileOutputStream(cachePath);
			work.write(out);
		}catch(Exception e){
			throw e;
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		
		return cacheName;
	}
	
	/**
	 * 更新零件整备设定历史有效区间终了日期
	 * @param form
	 * @param conn
	 */
/*	public void updatePartialPrepairHistroyEndDate(ActionForm form,SqlSessionManager conn){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		PartialBaseLineValueMapping dao=conn.getMapper(PartialBaseLineValueMapping.class);
		
		dao.updatePartialPrepairHistroyEndDate(entity);
	}*/
	
	/**
	 * 插入零件整备设定历史
	 * @param form
	 * @param conn
	 */
	/*public void insertPartialPrepairHistroy(ActionForm form,SqlSessionManager conn){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		PartialBaseLineValueMapping dao=conn.getMapper(PartialBaseLineValueMapping.class);
		
		dao.insertPartialPrepairHistroy(entity);
		
	}*/
	
	/**
	 * 查询当前零件整备设定最近一次有效区间的开始日期
	 * @param form
	 * @param conn
	 * @param infos
	 * @return
	 */
	public void searchLastStartDate(ActionForm form,SqlSessionManager conn,List<MsgInfo> msgInfos){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		PartialBaseLineValueMapping dao=conn.getMapper(PartialBaseLineValueMapping.class);
		
		Date end_date=entity.getEnd_date();//有效区间终了日期
		Date start_date=dao.searchLastStartDate(entity);
		if(start_date!=null){
			if(end_date.after(start_date)){//有效区间结束日期大于开始日期
			}else{
				MsgInfo info=new MsgInfo();
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.setHistoryAfter",DateUtil.toString(end_date, DateUtil.DATE_PATTERN),"零件"));
				msgInfos.add(info);
			}
		}
	}
	
	/**
	 * ckech基准值
	 * @param form
	 * @param conn
	 * @param msgInfos
	 * @param list
	 */
	public void checkBaseValue(ActionForm form,HttpServletRequest request,SqlSessionManager conn,List<MsgInfo> msgInfos,List<PartialBaseLineValueEntity> list){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		//获取数据库连接
		PartialBaseLineValueMapping dao=conn.getMapper(PartialBaseLineValueMapping.class);
		Date end_date=null;
		Date start_date=null;
		
		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER); 
		String operatorId=loginData.getOperator_id();
		entity.setUpdated_by(Integer.parseInt(operatorId));
		
		if(entity.getSorcwh_foreboard_count()!=null){//SORCWH 
			entity.setIdentification(1);
			end_date=entity.getSorcwh_end_date();//有效区间终了日期
			if(end_date==null){
				MsgInfo info=new MsgInfo();
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","SORCWH 基准值 起效日期"));
				info.setComponentid("sorcwh_end_date");
				msgInfos.add(info);
			}else{
				start_date=dao.searchLastStartDate(entity);
				if(start_date==null){
					list.add(entity);
				}else{
					if(end_date.after(start_date)){//有效区间结束日期大于开始日期
						list.add(entity);
					}else{
						MsgInfo info=new MsgInfo();
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidTimeRangeValue","SORCWH 基准值 起效日期","大于",DateUtil.toString(start_date, DateUtil.DATE_PATTERN)));
						info.setComponentid("sorcwh_end_date");
						msgInfos.add(info);
					}
				}
				
			}
		}
		
		if(entity.getWh2p_foreboard_count()!=null){//WH2P
			entity.setIdentification(4);
			end_date=entity.getWh2p_end_date();//有效区间终了日期
			if(end_date==null){
				MsgInfo info=new MsgInfo();
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","WH2P 基准值 起效日期"));
				info.setComponentid("wh2p_end_date");
				msgInfos.add(info);
			}else{
				start_date=dao.searchLastStartDate(entity);
				if(start_date==null){
					list.add(entity);
				}else{
					if(end_date.after(start_date)){//有效区间结束日期大于开始日期
						list.add(entity);
					}else{
						MsgInfo info=new MsgInfo();
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidTimeRangeValue","WH2P基准值 起效日期","大于",DateUtil.toString(start_date, DateUtil.DATE_PATTERN)));
						info.setComponentid("wh2p_end_date");
						msgInfos.add(info);
					}
				}
			}
		}
		
		if(entity.getConsumable_foreboard_count()!=null){//消耗品库存
			entity.setIdentification(3);
			end_date=entity.getConsumble_end_date();//有效区间终了日期
			if(end_date==null){
				MsgInfo info=new MsgInfo();
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","消耗品库存  基准值 起效日期"));
				info.setComponentid("consumble_end_date");
				msgInfos.add(info);
			}else{
				start_date=dao.searchLastStartDate(entity);
				if(start_date==null){
					list.add(entity);
				}else{
					if(end_date.after(start_date)){//有效区间结束日期大于开始日期
						list.add(entity);
					}else{
						MsgInfo info=new MsgInfo();
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidTimeRangeValue","消耗品库存基准值 起效日期","大于",DateUtil.toString(start_date, DateUtil.DATE_PATTERN)));
						info.setComponentid("consumble_end_date");
						msgInfos.add(info);
					}
				}
			}
		}
		
		if(entity.getOgz_foreboard_count()!=null){
			entity.setIdentification(2);
			end_date=entity.getOgz_end_date();//有效区间终了日期
			if(end_date==null){
				MsgInfo info=new MsgInfo();
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","OGZ基准值 起效日期"));
				info.setComponentid("ogz_end_date");
				msgInfos.add(info);
			}else{
				start_date=dao.searchLastStartDate(entity);
				if(start_date==null){
					list.add(entity);
				}else{
					if(end_date.after(start_date)){//有效区间结束日期大于开始日期
						list.add(entity);
					}else{
						MsgInfo info=new MsgInfo();
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidTimeRangeValue","OGZ基准值 起效日期","大于",DateUtil.toString(end_date, DateUtil.DATE_PATTERN)));
						info.setComponentid("ogz_end_date");
						msgInfos.add(info);
					}
				}
				
			}
			
		}
	}
	
	/**
	 * 更新零件基准值
	 * @param list
	 * @param conn
	 */
	public void updateBaseValue(List<PartialBaseLineValueEntity> list,SqlSessionManager conn){
		//获取数据库连接
		PartialBaseLineValueMapping dao=conn.getMapper(PartialBaseLineValueMapping.class);
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				PartialBaseLineValueEntity entity=list.get(i);
					int isExist=dao.checkExist(entity);
					if(isExist==0){
						dao.insertPartialPrepair(entity);
					}
					dao.updatePartialPrepairHistroyEndDate(entity);
					dao.insertPartialPrepairHistroy(entity);
					dao.updateForeboardCount(entity);
			}
		}
	}
	
	/**
	 * 查询零件基准值
	 * @param form 表单
	 * @param conn 数据库会话
	 * @return responseForm
	 */
	public PartialBaseLineValueForm secrchPartialBaseValue(ActionForm form,SqlSession conn){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		//获取数据库连接
		PartialBaseLineValueMapping dao=conn.getMapper(PartialBaseLineValueMapping.class);
		
		PartialBaseLineValueEntity tempEntity=dao.searchPartialBaseValue(entity);
		PartialBaseLineValueForm responseForm=new PartialBaseLineValueForm();
		
		if(tempEntity==null){
			return null;
		}
		//复制数据到表单对象
		BeanUtil.copyToForm(tempEntity, responseForm,CopyOptions.COPYOPTIONS_NOEMPTY);
		
		return responseForm;
	}
	
	/**
	 * 获取采样日期
	 * @param conn 数据库会话
	 * @return
	 */
	public String getDate(SqlSession conn,Integer flg){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		entity.setFlg(flg);
		
		//获取数据库连接
		PartialBaseLineValueMapping dao=conn.getMapper(PartialBaseLineValueMapping.class);
		Date sampleDate= dao.getSampleDate(entity);	
		String strDate= DateUtil.toString(sampleDate, DateUtil.DATE_PATTERN);
		return strDate;
	}
	
	/**
	 * 采样周期内 补充量合计
	 * @param form 表单
	 * @param conn 数据库会话
	 * @return
	 */
	public Integer getSupplyQuantityOfCycle(ActionForm form,SqlSession conn){
		PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
		//复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		PartialBaseLineValueMapping dao=conn.getMapper(PartialBaseLineValueMapping.class);//获取数据库连接
		
		Integer quantity=dao.searchSupplyQuantityOfCycle(entity);
		
		return quantity;
	}
	
}

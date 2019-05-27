package com.osh.rvs.service.report;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.ibatis.session.SqlSession;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.report.WorkTimeAnalysisEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.mapper.manage.UserDefineCodesMapper;
import com.osh.rvs.mapper.master.ModelMapper;
import com.osh.rvs.mapper.report.WorkTimeAnalysisMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class WorkTimeAnalysisService {

	public void validDate(ActionForm form, List<MsgInfo> errors) {
		WorkTimeAnalysisEntity entity = new WorkTimeAnalysisEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		Date finish_time_start = entity.getFinish_time_start();// 作业开始时间
		Date finish_time_end = entity.getFinish_time_end();// 作结束业时间

		if (finish_time_end != null && finish_time_start == null) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("validator.required");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "作业开始时间"));
			errors.add(msgInfo);
		}
	}

	public void searchWorkTime(ActionForm form,Map<String, Object> listResponse, SqlSession conn) throws Exception{
		WorkTimeAnalysisEntity entity = new WorkTimeAnalysisEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		Date finish_time_start = entity.getFinish_time_start();// 作业开始时间
		Date finish_time_end = entity.getFinish_time_end();// 作结束业时间
		if(finish_time_start == null || finish_time_end == null){
			Date [] aWorkTime = formatDate(finish_time_start, finish_time_end);
			entity.setFinish_time_start(aWorkTime[0]);
			entity.setFinish_time_end(aWorkTime[1]);
		}
		
		String model_name = entity.getModel_name();//型号
		Integer level = entity.getLevel();//等级
		String process_code = entity.getProcess_code();//工位代码
		String category_name = entity.getCategory_name();//机种名称
		if (!CommonStringUtil.isEmpty(model_name) && CommonStringUtil.isEmpty(category_name)) {
			ModelMapper dao = conn.getMapper(ModelMapper.class);
			category_name = dao.getCategoryNameByModelName(RvsUtils.regfy(model_name));
		}
		
		Integer standardWorkTime = null;
		
		if(!CommonStringUtil.isEmpty(model_name) && level != null && !CommonStringUtil.isEmpty(process_code)){
			standardWorkTime = Integer.valueOf(RvsUtils.getLevelOverLine(model_name, category_name, String.valueOf(level), null, process_code));
			entity.setStandardWorkTime(standardWorkTime);

			// 不包含异常数据的时候
			if (entity.getAbnormal() == 2) {
				UserDefineCodesMapper dao = conn.getMapper(UserDefineCodesMapper.class);
				// 超出标准工时百分比
				String value = dao.searchUserDefineCodesValueByCode("WORKTIME_ANALYSIS_OVER_PERCENT");
				entity.setUpper(Integer.valueOf(value));
			}
		}
		
		WorkTimeAnalysisMapper dao = conn.getMapper(WorkTimeAnalysisMapper.class);
		List<WorkTimeAnalysisEntity> list = null;
		
		// 实际平均用时
		List<Integer> avgWorkTimeList = new ArrayList<Integer>();
		// 标准用时
		List<Integer> standardWorkTimeList = new ArrayList<Integer>();
		// X轴
		List<String> xAxisList = new ArrayList<String>();
		
		int monthSpace = getMonthSpace(entity.getFinish_time_start(), entity.getFinish_time_end());
		if(monthSpace > 2){
			list = dao.searchAvgWorkTimeByMonth(entity);
			for(int i = 0;i < list.size();i++){
				WorkTimeAnalysisEntity connd = list.get(i);
				String yearMonth = connd.getYearMonth();
				avgWorkTimeList.add(connd.getAvgWorkTime());
				xAxisList.add(yearMonth.substring(0,4) + "年<br>" + yearMonth.substring(4,6) + "月");
				standardWorkTimeList.add(standardWorkTime);
			}
		}else{
			list = dao.searchAvgWorkTimeByWeek(entity);
			
			for(int i = 0;i < list.size();i++){
				WorkTimeAnalysisEntity connd = list.get(i);
				String week = connd.getYearMonth();
				Date[] data = getFirstDayANDLastDayOfWeek(week.substring(0,4), week.substring(4,6));
				xAxisList.add(DateUtil.toString(data[0],"M/d")+ "~<br>" + DateUtil.toString(data[1],"M/d"));
				avgWorkTimeList.add(connd.getAvgWorkTime());
				standardWorkTimeList.add(standardWorkTime);
			}
		}
		
		listResponse.put("avgWorkTimeList", avgWorkTimeList);
		listResponse.put("standardWorkTimeList", standardWorkTimeList);
		listResponse.put("xAxisList", xAxisList);
	}

	private Date[] formatDate(Date startDate, Date endDate) {
		Date [] aWorkTime = new Date[2];
		
		if (startDate == null && endDate == null) {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND, 0);
			
			aWorkTime[1] = now.getTime();//结束日期为当前日期
			now.add(Calendar.MONTH, -2);
			aWorkTime[0] = now.getTime();
		}else if(startDate != null && endDate  == null){
			Calendar now = Calendar.getInstance();
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND, 0);
			
			aWorkTime[1] = now.getTime();
			aWorkTime[0] = startDate;
		}
		return aWorkTime;
	}
	
	
	private int getMonthSpace(Date startDate, Date endDate){
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		
		int sYear = start.get(Calendar.YEAR);
		int sMonth = start.get(Calendar.MONTH);
		
		int eYear = end.get(Calendar.YEAR);
		int eMonth = end.get(Calendar.MONTH);
		
		int result = (eYear - sYear) * 12 + (eMonth - sMonth);
		return result;
	}
	
	private Date[] getFirstDayANDLastDayOfWeek(String strYear,String strWeek){
		Date [] data = new Date[2];
		
		int year = Integer.valueOf(strYear);
		int week = Integer.valueOf(strWeek);
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setMinimalDaysInFirstWeek(4);

		GregorianCalendar one = new GregorianCalendar();
		one.set(Calendar.YEAR, Integer.valueOf(year));
		one.set(Calendar.MONTH, 0);
		one.set(Calendar.DATE, 1);
		one.setFirstDayOfWeek(Calendar.MONDAY);
		one.setMinimalDaysInFirstWeek(4);

		if (one.getWeekYear() != one.get(Calendar.YEAR)) {
			week += 1;
		}

		cal.setWeekDate(year, week, cal.getFirstDayOfWeek());
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		data[0] = cal.getTime();
		
		cal.add(Calendar.DATE, 6);
		data[1] = cal.getTime();
		
		return data;
		
	}
	
	public String createExcel(ActionForm form)throws Exception{
		WorkTimeAnalysisEntity entity = new WorkTimeAnalysisEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		Date finish_time_start = entity.getFinish_time_start();// 作业开始时间
		Date finish_time_end = entity.getFinish_time_end();// 作结束业时间
		if(finish_time_start == null || finish_time_end == null){
			Date [] aWorkTime = formatDate(finish_time_start, finish_time_end);
			entity.setFinish_time_start(aWorkTime[0]);
			entity.setFinish_time_end(aWorkTime[1]);
		}
		
		String chartCacheName ="工时分析" + new Date().getTime() + ".png";
		String chartPic = RvsUtils.convertSVGToPng(entity.getSvg(),chartCacheName);
		
		//Excel临时文件
		String cacheName ="工时分析" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		OutputStream out = null;
		try {
			File file = new File(cachePath);
			if(!file.exists()){
				file.createNewFile();
			}
			
			HSSFWorkbook work=new HSSFWorkbook();
			HSSFSheet sheet = work.createSheet("工时分析");
			sheet.setColumnWidth(0, 1*256);
			sheet.setColumnWidth(1, 18*256);
			sheet.setColumnWidth(2, 25*256);
			
			HSSFFont font=work.createFont();
			font.setFontHeightInPoints((short)9);
			font.setFontName("微软雅黑");
			
			HSSFFont titlefont=work.createFont();
			titlefont.setFontHeightInPoints((short)10);
			titlefont.setFontName("微软雅黑");
			titlefont.setColor(HSSFColor.WHITE.index);
			
			HSSFCellStyle cellStyle = work.createCellStyle();
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN); 
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setWrapText(true);
			cellStyle.setFont(font);
			
			HSSFCellStyle titleStyle = work.createCellStyle();
			titleStyle.cloneStyleFrom(cellStyle);
			titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			titleStyle.setFillForegroundColor(HSSFColor.GREEN.index);
			titleStyle.setFont(titlefont);
			
			int index = 0;
			//工位
			if(!CommonStringUtil.isEmpty(entity.getProcess_code())){
				++index;
				String itemValue = "";
				if(!CommonStringUtil.isEmpty(entity.getSection_name())){
					itemValue = entity.getSection_name();
				}
				itemValue= itemValue + " " + entity.getProcess_code();
				setCellValue(index, "工位", itemValue, sheet, titleStyle,cellStyle);
			}
			
			//人员
			if(!CommonStringUtil.isEmpty(entity.getOperator_name())){
				++index;
				setCellValue(index, "人员", entity.getOperator_name(), sheet,titleStyle, cellStyle);
			}
			
			//作业时间
			if(entity.getFinish_time_start()!=null){
				++index;
				String itemValue = DateUtil.toString(entity.getFinish_time_start(), "YYYY年MM月");
				if(entity.getFinish_time_end()!=null){
					itemValue = itemValue + "到" + DateUtil.toString(entity.getFinish_time_end(), "YYYY年MM月");
				}else{
					Date [] aWorkTime = formatDate(entity.getFinish_time_start(), entity.getFinish_time_end());
					itemValue = itemValue + "到" + DateUtil.toString(aWorkTime[1], "YYYY年MM月");
				}
				setCellValue(index, "作业时间", itemValue, sheet, titleStyle,cellStyle);
			}
			
			//机种
			if(!CommonStringUtil.isEmpty(entity.getCategory_name())){
				++index;
				setCellValue(index, "机种", entity.getCategory_name(), sheet, titleStyle,cellStyle);
			}
			
			//型号
			if(!CommonStringUtil.isEmpty(entity.getModel_name())){
				++index;
				setCellValue(index, "型号", entity.getModel_name(), sheet,titleStyle, cellStyle);
			}
			
			//等级
			if(entity.getLevel()!=null){
				++index;
				setCellValue(index, "等级", CodeListUtils.getValue("material_level_inline",String.valueOf(entity.getLevel())), sheet, titleStyle,cellStyle);
			}
			
			//课室
			if(CommonStringUtil.isEmpty(entity.getProcess_code()) && !CommonStringUtil.isEmpty(entity.getSection_name())){
				++index;
				setCellValue(index, "课室", entity.getSection_name(), sheet, titleStyle,cellStyle);
			}
			
			//加急
			if(entity.getScheduled_expedited() == 1){
				++index;
				setCellValue(index, "加急", "是", sheet, titleStyle,cellStyle);
			}else if(entity.getScheduled_expedited() == 2){
				++index;
				setCellValue(index, "加急", "否", sheet, titleStyle,cellStyle);
			}
			
			//是否包含返工
			if(entity.getRework() == 1){
				++index;
				setCellValue(index, "是否包含返工", "是", sheet,titleStyle, cellStyle);
			}else if(entity.getRework() == 2){
				++index;
				setCellValue(index, "是否包含返工", "否", sheet, titleStyle,cellStyle);
			}
			
			BufferedImage bufferImg=ImageIO.read(new File(chartPic));
			ByteArrayOutputStream byteArrayOut=new ByteArrayOutputStream();
			ImageIO.write(bufferImg, "png", byteArrayOut);
			
			HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
			if (patriarch == null) {
				patriarch = sheet.createDrawingPatriarch();
			}
			
			HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 1, sheet.getLastRowNum()+2, (short) 9, sheet.getLastRowNum() + 10);
			patriarch.createPicture(anchor,work.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize(1);
			
			
			//设置横向打印
			sheet.getPrintSetup().setLandscape(true);
			
			out= new FileOutputStream(file);
			work.write(out);
		}catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}finally{
			if(out!=null){
				out.close();
			}
		}
		return cacheName;
	}
	
	private void setCellValue(int rowIndex,String itemName,String itemValue,HSSFSheet sheet,HSSFCellStyle titleStyle,HSSFCellStyle cellStyle){
		HSSFRow row = sheet.createRow(rowIndex);
		HSSFCell cell = row.createCell(1);
		cell.setCellValue(itemName);
		cell.setCellStyle(titleStyle);
		
		cell = row.createCell(2);
		cell.setCellValue(itemValue);
		cell.setCellStyle(cellStyle);
	}

	/** 异常工时
	 * @param form
	 * @param conn 
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	public String createAnomalyExcel(ActionForm form, SqlSession conn,List<MsgInfo> errors) throws Exception {
		WorkTimeAnalysisEntity conndentity = new WorkTimeAnalysisEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, conndentity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		if(DateUtil.compareDate(conndentity.getFinish_time_start(), conndentity.getFinish_time_end()) > 0){
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("validator.invalidParam.invalidTimeRangeValue");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidTimeRangeValue", "作业结束时间","不早于","作业开始时间"));
			errors.add(msgInfo);
			return "";
		}
		
		int days = this.daysBetween(conndentity.getFinish_time_start(), conndentity.getFinish_time_end());
		if(days > 365){
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrmsg("作业开始时间作业结束时间不能超出一年。");
			errors.add(msgInfo);
			return "";
		}
		
		WorkTimeAnalysisMapper dao = conn.getMapper(WorkTimeAnalysisMapper.class);
		List<WorkTimeAnalysisEntity> list = new ArrayList<WorkTimeAnalysisEntity>();
		list = dao.searchAnomaly(conndentity);
		
		List<WorkTimeAnalysisEntity> resultList = new ArrayList<WorkTimeAnalysisEntity>();
		int len = list.size();
		WorkTimeAnalysisEntity entity = null;
		
		for(int i = 0;i < len; i++){
			entity = list.get(i);
			
			//实际用时
			Integer useSeconds = entity.getUse_seconds();
			if(useSeconds == null) continue;
			
			//标准工时
			Integer standardWorkTime = Integer.valueOf(RvsUtils.getLevelOverLine(entity.getModel_name(), entity.getCategory_name(), String.valueOf(entity.getLevel()), null, entity.getProcess_code()));
			if(standardWorkTime <= 0) continue;
			
			BigDecimal decimalStandardWorkTime = new BigDecimal(standardWorkTime);
			
			//实际用时大于标准工时 或者 实际用时小于标准工时的20%
			if((useSeconds > standardWorkTime) || (useSeconds < decimalStandardWorkTime.multiply(new BigDecimal("0.2")).intValue())){
				entity.setStandardWorkTime(standardWorkTime);
				resultList.add(entity);
			}
		}
		
		//Excel临时文件
		String cacheName ="异常工时数据" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName;
		
		OutputStream out = null;
		try {
			File file = new File(cachePath);
			
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			
			if(!file.exists()){
				file.createNewFile();
			}
			
			HSSFWorkbook work=new HSSFWorkbook();
			HSSFSheet sheet = work.createSheet("异常工时数据");
			sheet.setColumnWidth(0, 1*256);
			sheet.setColumnWidth(1, 10*256);
			sheet.setColumnWidth(2, 25*256);
			sheet.setColumnWidth(3, 8*256);
			sheet.setColumnWidth(4, 6*256);
			sheet.setColumnWidth(5, 6*256);
			sheet.setColumnWidth(6,10*256);
			sheet.setColumnWidth(7, 8*256);
			sheet.setColumnWidth(8, 7*256);
			sheet.setColumnWidth(9, 18*256);
			sheet.setColumnWidth(10, 18*256);
			sheet.setColumnWidth(11, 18*256);
			sheet.setColumnWidth(12, 9*256);
			sheet.setColumnWidth(13, 9*256);
			sheet.setColumnWidth(14, 10*256);
			
			HSSFRow row = null;
			HSSFCell cell = null;
			
			HSSFFont font=work.createFont();
			font.setFontHeightInPoints((short)9);
			font.setFontName("微软雅黑");
			
			HSSFFont titlefont=work.createFont();
			titlefont.setFontHeightInPoints((short)10);
			titlefont.setFontName("微软雅黑");
			titlefont.setColor(HSSFColor.WHITE.index);
			
			//内容默认样式
			HSSFCellStyle cellStyle = work.createCellStyle();
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN); 
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setWrapText(false);
			cellStyle.setFont(font);
			
			//内容居中样式
			HSSFCellStyle cellCenterStyle = work.createCellStyle();
			cellCenterStyle.cloneStyleFrom(cellStyle);
			cellCenterStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			//内容居右样式
			HSSFCellStyle cellRightStyle = work.createCellStyle();
			cellRightStyle.cloneStyleFrom(cellStyle);
			cellRightStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			
			//标题默认样式
			HSSFCellStyle titleStyle = work.createCellStyle();
			titleStyle.cloneStyleFrom(cellStyle);
			titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			titleStyle.setFillForegroundColor(HSSFColor.GREEN.index);
			titleStyle.setFont(titlefont);
			
			//标题居中样式
			HSSFCellStyle titleCenterStyle = work.createCellStyle();
			titleCenterStyle.cloneStyleFrom(titleStyle);
			titleCenterStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			titleCenterStyle.setWrapText(true);
			
			//百分比样式 
			HSSFCellStyle percentageStyle = work.createCellStyle();
			percentageStyle.cloneStyleFrom(cellStyle);
			percentageStyle.setDataFormat(work.createDataFormat().getFormat("0.0%"));
			
			Map<String,HSSFCellStyle> mapStyle = new HashMap<String, HSSFCellStyle>();
			mapStyle.put("cellStyle", cellStyle);
			mapStyle.put("cellCenterStyle", cellCenterStyle);
			mapStyle.put("cellRightStyle", cellRightStyle);
			mapStyle.put("titleStyle", titleStyle);
			mapStyle.put("titleCenterStyle", titleCenterStyle);
			mapStyle.put("percentageStyle", percentageStyle);
			
			
			int rowIndex = 0;
			
			//课室
			if(!CommonStringUtil.isEmpty(conndentity.getSection_name())){
				++rowIndex;
				setCellValue(rowIndex, "课室", conndentity.getSection_name(), sheet, titleStyle,cellStyle);
			}

			//工程
			if(!CommonStringUtil.isEmpty(conndentity.getLine_name())){
				++rowIndex;
				setCellValue(rowIndex, "工程", conndentity.getLine_name(), sheet, titleStyle,cellStyle);
			}
			
			++rowIndex;
			String itemValue = DateUtil.toString(conndentity.getFinish_time_start(), DateUtil.DATE_PATTERN);
			itemValue = itemValue + "~" + DateUtil.toString(conndentity.getFinish_time_end(), DateUtil.DATE_PATTERN);
			setCellValue(rowIndex, "作业时间", itemValue, sheet, titleStyle,cellStyle);
			
			rowIndex  = rowIndex + 2;
			row = sheet.createRow(rowIndex);
			
			cell = row.createCell(1);
			cell.setCellValue("修理单号");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(2);
			cell.setCellValue("型号");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(3);
			cell.setCellValue("机身号");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(4);
			cell.setCellValue("等级");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(5);
			cell.setCellValue("机种");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(6);
			cell.setCellValue("课室");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(7);
			cell.setCellValue("工程");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(8);
			cell.setCellValue("工位");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(9);
			cell.setCellValue("开始时间");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(10);
			cell.setCellValue("完成时间");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(11);
			cell.setCellValue("操作者");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(12);
			cell.setCellValue("实际用时\n(分钟)");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(13);
			cell.setCellValue("标准工时\n(分钟)");
			cell.setCellStyle(titleCenterStyle);
			
			cell = row.createCell(14);
			cell.setCellValue("实际用时\n/标准工时");
			cell.setCellStyle(titleCenterStyle);
			
			++rowIndex;
			int maxNum = 65536- rowIndex;
			
			if(resultList.size() > maxNum){
				HSSFSheet sheet2  = work.cloneSheet(0);
				
				setAnomalyExcelValue(sheet, row, cell, mapStyle, rowIndex, 0, maxNum-1,resultList);
				setAnomalyExcelValue(sheet2, row, cell, mapStyle, rowIndex, maxNum, resultList.size()-1,resultList);
			}else{
				setAnomalyExcelValue(sheet, row, cell, mapStyle, rowIndex, 0, resultList.size()-1,resultList);
			}
			
			out= new FileOutputStream(file);
			work.write(out);
		}catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}finally{
			if(out!=null){
				out.close();
			}
		}
		return cacheName;
		
	}
	
	/**
	 * 
	 * @param sheet 工作簿
	 * @param row 行
	 * @param cell 单元格
	 * @param mapStyle 单元格样式Map
	 * @param rowIndex 行索引
	 * @param listIndexStart 数据集开始索引
	 * @param listIndexEnd 数据集结束索引
	 * @param resultList 数据集
	 */
	private void setAnomalyExcelValue(HSSFSheet sheet ,HSSFRow row ,HSSFCell cell ,Map<String,HSSFCellStyle> mapStyle ,int rowIndex,int listIndexStart,int listIndexEnd,List<WorkTimeAnalysisEntity> resultList){
		WorkTimeAnalysisEntity entity = null;
		HSSFCellStyle cellCenterStyle = mapStyle.get("cellCenterStyle");
		HSSFCellStyle cellStyle = mapStyle.get("cellStyle");
		HSSFCellStyle cellRightStyle = mapStyle.get("cellRightStyle");
		HSSFCellStyle percentageStyle = mapStyle.get("percentageStyle");
		
		int startIndex = rowIndex;
		for(int i = listIndexStart;i <= listIndexEnd;i++){
			entity = resultList.get(i);
			row = sheet.createRow(startIndex);
			
			//修理单号
			cell = row.createCell(1);
			cell.setCellValue(entity.getOmr_notifi_no());
			cell.setCellStyle(cellCenterStyle);
			
			//型号
			cell = row.createCell(2);
			cell.setCellValue(entity.getModel_name());
			cell.setCellStyle(cellStyle);
			
			//机身号
			cell = row.createCell(3);
			cell.setCellValue(entity.getSerial_no());
			cell.setCellStyle(cellCenterStyle);
			
			//等级
			cell = row.createCell(4);
			if(entity.getLevel() != null){
				cell.setCellValue(CodeListUtils.getValue("material_level", entity.getLevel().toString()));
			}
			cell.setCellStyle(cellCenterStyle);
			
			//机种
			cell = row.createCell(5);
			cell.setCellValue(entity.getCategory_name());
			cell.setCellStyle(cellStyle);
			
			
			//课室
			cell = row.createCell(6);
			cell.setCellValue(entity.getSection_name());
			cell.setCellStyle(cellStyle);
			
			//工程
			cell = row.createCell(7);
			cell.setCellValue(entity.getLine_name());
			cell.setCellStyle(cellStyle);
			
			//工位
			cell = row.createCell(8);
			cell.setCellValue(entity.getProcess_code());
			cell.setCellStyle(cellCenterStyle);
			
			//开始时间
			cell = row.createCell(9);
			cell.setCellValue(DateUtil.toString(entity.getAction_time(), DateUtil.DATE_TIME_PATTERN));
			cell.setCellStyle(cellRightStyle);

			//完成时间
			cell = row.createCell(10);
			cell.setCellValue(DateUtil.toString(entity.getFinish_time(), DateUtil.DATE_TIME_PATTERN));
			cell.setCellStyle(cellRightStyle);
			
			//操作者
			cell = row.createCell(11);
			cell.setCellValue(entity.getOperator_name());
			cell.setCellStyle(cellStyle);
			
			//实际用时
			cell = row.createCell(12);
			cell.setCellValue(entity.getUse_seconds());
			cell.setCellStyle(cellRightStyle);
			
			//标准工时
			cell = row.createCell(13);
			cell.setCellValue(entity.getStandardWorkTime());
			cell.setCellStyle(cellStyle);
			
			//实际用时/标准工时
			cell = row.createCell(14);
			cell.setCellFormula("M" + (startIndex+1) + "/N" + (startIndex+1));
			cell.setCellStyle(percentageStyle);
			
			startIndex ++;
		}
	}
	
	
	 /**  
     * 计算两个日期之间相差的天数  
     * @param start 较小的时间 
     * @param end  较大的时间 
     * @return 相差天数 
     */    
    private int daysBetween(Date start,Date end){   
       long between_days = (end.getTime() - start.getTime()) / (1000 * 3600 * 24);  
       return Integer.parseInt(String.valueOf(between_days));           
    }    
	
}

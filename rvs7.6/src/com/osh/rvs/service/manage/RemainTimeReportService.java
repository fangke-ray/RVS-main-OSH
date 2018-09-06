package com.osh.rvs.service.manage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.osh.rvs.bean.manage.PositionPlanTimeEntity;
import com.osh.rvs.bean.report.RemainTimeReportEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.report.RemainTimeReportForm;
import com.osh.rvs.mapper.manage.PositionPlanTimeMapper;
import com.osh.rvs.mapper.report.RemainTimeReportMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class RemainTimeReportService {

	/**
	 * 
	 * @param searchForm
	 * @param listResponse
	 * @param conn
	 * @throws Exception
	 */
	public void searchChatData(RemainTimeReportForm searchForm, Map<String, Object> listResponse, SqlSession conn) throws Exception{
		RemainTimeReportEntity entity = new RemainTimeReportEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(searchForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		RemainTimeReportMapper dao = conn.getMapper(RemainTimeReportMapper.class);
		List<RemainTimeReportEntity> list = dao.searchRateList(entity);
		List<RemainTimeReportEntity> partial_list = dao.searchPartialRateList(entity);

		String remain_time_target = PathConsts.POSITION_SETTINGS.getProperty("remain_time_target");
		BigDecimal b_target = null;
		if (!CommonStringUtil.isEmpty(remain_time_target)) {
			b_target = new BigDecimal(remain_time_target);
		}

		List<String> xAxisList = new ArrayList<String>();		
		List<BigDecimal> partialList = new ArrayList<BigDecimal>();
		List<BigDecimal> decomposeLineList = new ArrayList<BigDecimal>();
		List<BigDecimal> nsLineList = new ArrayList<BigDecimal>();
		List<BigDecimal> composeLineList = new ArrayList<BigDecimal>();
		List<BigDecimal> targetList = new ArrayList<BigDecimal>();
		for (RemainTimeReportEntity oneEntity : partial_list) {
			partialList.add(oneEntity.getFinish_rate());
		}
		for (RemainTimeReportEntity oneEntity : list) {
			if ("00000000012".equals(oneEntity.getLine_id())) {
				decomposeLineList.add(oneEntity.getFinish_rate());
				String year = oneEntity.getFinish_month().substring(0,4);
				String month = oneEntity.getFinish_month().substring(4,6);
				xAxisList.add(year + "年<br>" + month + "月");
				targetList.add(b_target);
			} else if ("00000000013".equals(oneEntity.getLine_id())) {
				nsLineList.add(oneEntity.getFinish_rate());
			} else if ("00000000014".equals(oneEntity.getLine_id())) {
				composeLineList.add(oneEntity.getFinish_rate());
			}
		}

		listResponse.put("targetList", targetList);// 零件发放
		listResponse.put("partialList", partialList);// 零件发放
		listResponse.put("decomposeLineList", decomposeLineList);// 分解工程
		listResponse.put("nsLineList", nsLineList);// NS工程
		listResponse.put("composeLineList", composeLineList);// 总组工程
		listResponse.put("xAxisList", xAxisList);
	}

	/**
	 * 
	 * @param form
	 * @throws Exception
	 */
	public String createExcel(RemainTimeReportForm exportForm, SqlSession conn) throws Exception {
		//Excel临时文件
		String cacheName ="倒计时达成率" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 

		OutputStream out = null;
		try {
			File file = new File(cachePath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();

			HSSFWorkbook work = new HSSFWorkbook();

			HSSFFont font = work.createFont();
			font.setFontHeightInPoints((short) 9);
			font.setFontName("微软雅黑");
			
			HSSFFont headerfont = work.createFont();
			headerfont.setFontHeightInPoints((short)10);
			headerfont.setFontName("微软雅黑");
			headerfont.setColor(HSSFColor.WHITE.index);

			HSSFFont titlefont = work.createFont();
			titlefont.setFontHeightInPoints((short)16);
			titlefont.setFontName("微软雅黑");
			titlefont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			titlefont.setColor(HSSFColor.BLACK.index);

			HSSFCellStyle cellStyle = work.createCellStyle();
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setWrapText(true);
			cellStyle.setFont(font);

			HSSFCellStyle centerStyle = work.createCellStyle();
			centerStyle.cloneStyleFrom(cellStyle);
			centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle headerStyle = work.createCellStyle();
			headerStyle.cloneStyleFrom(cellStyle);
			headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headerStyle.setFillForegroundColor(HSSFColor.GREEN.index);
			headerStyle.setFont(headerfont);

			HSSFCellStyle titleStyle = work.createCellStyle();
			titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			titleStyle.setFont(titlefont);

			HSSFCellStyle timeStyle = work.createCellStyle();
			timeStyle.cloneStyleFrom(cellStyle);
			timeStyle.setDataFormat(work.createDataFormat().getFormat("yyyy/mm/dd hh:mm"));

			HSSFCellStyle percentStyle = work.createCellStyle();
			percentStyle.cloneStyleFrom(cellStyle);
			percentStyle.setDataFormat(work.createDataFormat().getFormat("0.0%"));

			HSSFCellStyle decimalStyle = work.createCellStyle();
			decimalStyle.cloneStyleFrom(cellStyle);
			decimalStyle.setDataFormat(work.createDataFormat().getFormat("0.0"));

			HSSFCellStyle blueCellStyle = work.createCellStyle();
			blueCellStyle.cloneStyleFrom(cellStyle);
			blueCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			blueCellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);

			HSSFCellStyle blueHeaderStyle = work.createCellStyle();
			blueHeaderStyle.cloneStyleFrom(blueCellStyle);
			blueHeaderStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle bluePercentStyle = work.createCellStyle();
			bluePercentStyle.cloneStyleFrom(blueCellStyle);
			bluePercentStyle.setDataFormat(work.createDataFormat().getFormat("0.0%"));

			Map<String, HSSFCellStyle> styleMap = new HashMap<String, HSSFCellStyle>();
			styleMap.put("cellStyle", cellStyle);
			styleMap.put("centerStyle", centerStyle);
			styleMap.put("headerStyle", headerStyle);
			styleMap.put("titleStyle", titleStyle);
			styleMap.put("timeStyle", timeStyle);
			styleMap.put("percentStyle", percentStyle);
			styleMap.put("decimalStyle", decimalStyle);
			styleMap.put("blueCellStyle", blueCellStyle);
			styleMap.put("blueHeaderStyle", blueHeaderStyle);
			styleMap.put("bluePercentStyle", bluePercentStyle);

			RemainTimeReportEntity entity = new RemainTimeReportEntity();
			// 复制表单数据到对象
			BeanUtil.copyToBean(exportForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

			RemainTimeReportMapper dao = conn.getMapper(RemainTimeReportMapper.class);
			List<RemainTimeReportEntity> list = null;
			List<RemainTimeReportEntity> partial_list = null;

			partial_list = dao.searchPartialDetailList(entity);
			createDetailSheet(partial_list, work, "零件发放倒计时达成率", styleMap);

			entity.setLine_id("00000000012");
			list = dao.searchDetailList(entity);
			createDetailSheet(list, work, "分解倒计时达成率", styleMap);

			entity.setLine_id("00000000013");
			list = dao.searchDetailList(entity);
			createDetailSheet(list, work, "NS倒计时达成率", styleMap);

			entity.setLine_id("00000000014");
			list = dao.searchDetailList(entity);
			createDetailSheet(list, work, "总组倒计时达成率", styleMap);

			// 倒计时达成率趋势图
			list = dao.searchRateList(entity);
			partial_list = dao.searchPartialRateList(entity);
			createPicSheet(exportForm, list, partial_list, work);

			out = new FileOutputStream(file);
			work.write(out);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return cacheName;
	}

	/**
	 * 按机型统计
	 * @param list
	 * @param totalMap
	 */
	private void getTotalData(List<RemainTimeReportEntity> list, Map<String, RemainTimeReportEntity> totalMap) {
		RemainTimeReportEntity list_one = null;
		for(int i = 0;i<list.size();i++){
			list_one = list.get(i);
			String category_name = list_one.getCategory_name();
			Date finish_time = list_one.getFinish_time();
			Date expected_finish_time = list_one.getExpected_finish_time();
			int rework = list_one.getRework();
			int finish_cnt = 0;
			int reached_cnt = 0;
			RemainTimeReportEntity totalEntity = null;
			if (totalMap.containsKey(category_name)) {
				totalEntity = totalMap.get(category_name);
				finish_cnt = totalEntity.getFinish_cnt();
				reached_cnt = totalEntity.getReached_cnt();
			} else {
				totalEntity = new RemainTimeReportEntity();
				totalEntity.setCategory_name(category_name);
				totalEntity.setFinish_cnt(0);
				totalEntity.setReached_cnt(0);
			}
			totalEntity.setFinish_cnt(finish_cnt + 1);
			if (rework == 0 && finish_time != null && expected_finish_time != null
					&& finish_time.compareTo(expected_finish_time) <= 0) {
				totalEntity.setReached_cnt(reached_cnt + 1);
			}
			totalMap.put(category_name, totalEntity);
		}
	}

	/**
	 * 创建明细sheet
	 * @param list
	 * @param work
	 * @param sheetName
	 * @throws Exception
	 */
	private void createDetailSheet(List<RemainTimeReportEntity> list, HSSFWorkbook work, String sheetName,
			Map<String, HSSFCellStyle> styleMap) throws Exception {
		HSSFSheet sheet = work.createSheet(sheetName);
		sheet.createFreezePane(0, 1);

		int[] rowIndex = new int[1];
		rowIndex[0] = 1;
		HSSFRow row = sheet.createRow(rowIndex[0]);

		HSSFCell cell = row.createCell(1);
		cell.setCellValue(sheetName + "报表");
		cell.setCellStyle(styleMap.get("titleStyle"));

		if (sheetName.contains("零件发放")) {
			// 倒计时达成率报表
			createPartialTotalTable(list, sheet, styleMap, rowIndex);
			// 倒计时达成率明细
			createPartialDetailTable(list, sheet, styleMap, rowIndex);
		} else {
			// 倒计时达成率报表
			createTotalTable(list, sheet, styleMap, rowIndex);
			// 倒计时达成率明细
			createDetailTable(list, sheet, styleMap, rowIndex);
		}
	}


	/**
	 * 创建零件发放倒计时达成率报表
	 * @param list
	 * @param sheet
	 * @param styleMap
	 * @param rowIndex
	 */
	private void createPartialTotalTable(List<RemainTimeReportEntity> list, HSSFSheet sheet,
			Map<String, HSSFCellStyle> styleMap, int[] rowIndex) {
		HSSFRow row = null;
		HSSFCell cell = null;
		HSSFCellStyle headerStyle = styleMap.get("headerStyle");
		HSSFCellStyle cellStyle = styleMap.get("cellStyle");
		HSSFCellStyle percentStyle = styleMap.get("percentStyle");

		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);
		
		cell = row.createCell(1);
		cell.setCellValue("发放台数");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(2);
		cell.setCellValue("达成台数");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(3);
		cell.setCellValue("未达成台数");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(4);
		cell.setCellValue("达成比率");
		cell.setCellStyle(headerStyle);

		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);
		int rowNum = row.getRowNum() + 1;
		
		cell = row.createCell(1);//发放台数
		cell.setCellValue(list.size());
		cell.setCellStyle(cellStyle);

		cell = row.createCell(2);//达成台数
		int reached_cnt = 0;
		RemainTimeReportEntity list_one = null;
		for (int i = 0; i < list.size(); i++) {
			list_one = list.get(i);
			if (list_one.getFactwork_minutes() != null && list_one.getFactwork_minutes() <= 120) {
				reached_cnt++;
			}
		}
		cell.setCellValue(reached_cnt);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(3);//未达成台数
		cell.setCellFormula("B" + rowNum + "-C" + rowNum);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(4);//达成比率
		cell.setCellFormula("C" + rowNum + "/B" + rowNum);
		cell.setCellStyle(percentStyle);
	}

	/**
	 * 创建倒计时达成率明细
	 * @param list
	 * @param sheet
	 * @param styleMap
	 * @param rowIndex
	 */
	private void createPartialDetailTable(List<RemainTimeReportEntity> list, HSSFSheet sheet,
			Map<String, HSSFCellStyle> styleMap, int[] rowIndex) {
		sheet.setColumnWidth(0, 6*256);		//序号
		sheet.setColumnWidth(1, 20*256);	//修理单号
		sheet.setColumnWidth(2, 24*256);	//内镜型号
		sheet.setColumnWidth(3, 12*256);	//等级
		sheet.setColumnWidth(4, 18*256);	//开始时间
		sheet.setColumnWidth(5, 18*256);	//完成时间
		sheet.setColumnWidth(6, 12*256);	//实际用时
		sheet.setColumnWidth(7, 12*256);	//标准时间
		sheet.setColumnWidth(8, 12*256);	//超出时间
		sheet.setColumnWidth(9, 12*256);	//是否BO
		sheet.setColumnWidth(10, 12*256);	//是否达成

		HSSFRow row = null;
		HSSFCell cell = null;
		HSSFCellStyle headerStyle = styleMap.get("headerStyle");
		HSSFCellStyle cellStyle = styleMap.get("cellStyle");
		HSSFCellStyle centerStyle = styleMap.get("centerStyle");
		HSSFCellStyle timeStyle = styleMap.get("timeStyle");
		HSSFCellStyle decimalStyle = styleMap.get("decimalStyle");
		HSSFCellStyle blueHeaderStyle = styleMap.get("blueHeaderStyle");
		HSSFCellStyle bluePercentStyle = styleMap.get("bluePercentStyle");

		rowIndex[0] = rowIndex[0] + 3;
		row = sheet.createRow(rowIndex[0]);

		cell = row.createCell(0);
		cell.setCellValue("序号");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(1);
		cell.setCellValue("修理单号");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(2);
		cell.setCellValue("内镜型号");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(3);
		cell.setCellValue("等级");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(4);
		cell.setCellValue("开始时间");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(5);
		cell.setCellValue("完成时间");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(6);
		cell.setCellValue("实际用时\r\n(分钟)");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(7);
		cell.setCellValue("标准时间\r\n(分钟)");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(8);
		cell.setCellValue("超出时间\r\n(分钟)");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(9);
		cell.setCellValue("是否BO");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(10);
		cell.setCellValue("是否达成");
		cell.setCellStyle(headerStyle);

		int rowNumStart = row.getRowNum() + 2;
		int rowNumEnd = rowNumStart + list.size() - 1;
		RemainTimeReportEntity entity = null;
		for (int i = 0; i < list.size(); i++) {
			entity = list.get(i);
			rowIndex[0] ++;
			row = sheet.createRow(rowIndex[0]);

			cell = row.createCell(0);//序号
			cell.setCellValue(i+1);
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(1);//修理单号
			cell.setCellValue(entity.getOmr_notifi_no());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(2);//内镜型号
			cell.setCellValue(entity.getModel_name());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(3);//等级
			cell.setCellValue(CodeListUtils.getValue("material_level_heavy",String.valueOf(entity.getLevel())));
			cell.setCellStyle(centerStyle);

			cell = row.createCell(4);//开始时间
			cell.setCellValue(entity.getAction_time());
			cell.setCellStyle(timeStyle);

			cell = row.createCell(5);//完成时间
			cell.setCellValue(entity.getFinish_time());
			cell.setCellStyle(timeStyle);

			cell = row.createCell(6);//实际用时
			if (entity.getFactwork_minutes() != null) {
				cell.setCellValue(entity.getFactwork_minutes());
			}
			cell.setCellStyle(cellStyle);

			cell = row.createCell(7);//标准时间
			cell.setCellValue(120);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(8);//超出时间
			if (entity.getFactwork_minutes() != null) {
				cell.setCellValue(entity.getFactwork_minutes() - 120);
			}
			cell.setCellStyle(cellStyle);

			cell = row.createCell(9);//是否BO
			if (entity.getBo_flg() == 1) {
				cell.setCellValue("是");
			} else {
				cell.setCellValue("否");
			}
			cell.setCellStyle(centerStyle);

			cell = row.createCell(10);//是否达成
			if (entity.getFactwork_minutes() != null && entity.getFactwork_minutes() <= 120) {
				cell.setCellValue("是");
			} else {
				cell.setCellValue("否");
			}
			cell.setCellStyle(centerStyle);
		}

		// 空白行
		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);
		row.setHeight((short) 50);

		// 统计平均时间
		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);

		cell = row.createCell(0);
		cell.setCellStyle(cellStyle);

		HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
		if (patriarch == null) {
			patriarch = sheet.createDrawingPatriarch();
		}
		HSSFClientAnchor anchor = null;
		HSSFSimpleShape shape = null;
		for (int i = 1; i <= 4; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(cellStyle);

			// 斜杠
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) i, row.getRowNum(), (short) i, row.getRowNum());
			shape = patriarch.createSimpleShape(anchor);
			shape.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
			shape.setLineStyle(HSSFSimpleShape.LINESTYLE_SOLID);
			shape.setRotationDegree((short) 90);
		}

		cell = row.createCell(5);
		cell.setCellValue("平均时间");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(6);
		cell.setCellFormula("AVERAGE(G" + rowNumStart + ":G" + rowNumEnd + ")");
		cell.setCellStyle(decimalStyle);

		cell = row.createCell(7);
		cell.setCellValue(120);
		cell.setCellStyle(decimalStyle);

		cell = row.createCell(8);
		cell.setCellFormula("AVERAGE(I" + rowNumStart + ":I" + rowNumEnd + ")");
		cell.setCellStyle(decimalStyle);

		cell = row.createCell(9);
		cell.setCellStyle(cellStyle);
		cell = row.createCell(10);
		cell.setCellStyle(cellStyle);

		// 统计达成率
		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);

		for (int i = 0; i <= 9; i++) {
			cell = row.createCell(i);
			cell.setCellValue("达成率");
			cell.setCellStyle(blueHeaderStyle);
		}

		// 合并单元格
		CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 9);
		sheet.addMergedRegion(region);

		cell = row.createCell(10);
		cell.setCellFormula("COUNTIF(K" + rowNumStart + ":K" + rowNumEnd + ",\"是\")/COUNTA(K" + rowNumStart + ":K" + rowNumEnd + ")");
		cell.setCellStyle(bluePercentStyle);
	}

	/**
	 * 创建倒计时达成率报表
	 * @param list
	 * @param sheet
	 * @param styleMap
	 * @param rowIndex
	 */
	private void createTotalTable(List<RemainTimeReportEntity> list, HSSFSheet sheet,
			Map<String, HSSFCellStyle> styleMap, int[] rowIndex) {
		HSSFRow row = null;
		HSSFCell cell = null;
		HSSFCellStyle headerStyle = styleMap.get("headerStyle");
		HSSFCellStyle cellStyle = styleMap.get("cellStyle");
		HSSFCellStyle percentStyle = styleMap.get("percentStyle");
		HSSFCellStyle blueCellStyle = styleMap.get("blueCellStyle");
		HSSFCellStyle blueHeaderStyle = styleMap.get("blueHeaderStyle");
		HSSFCellStyle bluePercentStyle = styleMap.get("bluePercentStyle");

		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);

		cell = row.createCell(1);
		cell.setCellValue("机型");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(2);
		cell.setCellValue("维修完成台数");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(3);
		cell.setCellValue("达成台数");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(4);
		cell.setCellValue("未达成台数");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(5);
		cell.setCellValue("达成比率");
		cell.setCellStyle(headerStyle);

		Map<String, RemainTimeReportEntity> totalMap = new HashMap<String, RemainTimeReportEntity>();
		getTotalData(list, totalMap);

		int rowNumStart = row.getRowNum() + 2;
		int rowNum = 0;
		RemainTimeReportEntity entity = null;
		for (String key : totalMap.keySet()) {
			entity = totalMap.get(key);
			rowIndex[0] ++;
			row = sheet.createRow(rowIndex[0]);
			rowNum = row.getRowNum() + 1;

			cell = row.createCell(1);//机型
			cell.setCellValue(entity.getCategory_name());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(2);//维修完成台数
			cell.setCellValue(entity.getFinish_cnt());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(3);//达成台数
			cell.setCellValue(entity.getReached_cnt());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(4);//未达成台数
			cell.setCellFormula("C" + rowNum + "-D" + rowNum);
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(5);//达成比率
			cell.setCellFormula("D" + rowNum + "/C" + rowNum);
			cell.setCellStyle(percentStyle);
		}

		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);
		rowNum = row.getRowNum() + 1;

		cell = row.createCell(1);
		cell.setCellValue("合计");
		cell.setCellStyle(blueHeaderStyle);

		cell = row.createCell(2);
		cell.setCellFormula("SUM(C" + rowNumStart + ":C" + (rowNum - 1) + ")");
		cell.setCellStyle(blueCellStyle);

		cell = row.createCell(3);
		cell.setCellFormula("SUM(D" + rowNumStart + ":D" + (rowNum - 1) + ")");
		cell.setCellStyle(blueCellStyle);

		cell = row.createCell(4);
		cell.setCellFormula("SUM(E" + rowNumStart + ":E" + (rowNum - 1) + ")");
		cell.setCellStyle(blueCellStyle);

		cell = row.createCell(5);
		cell.setCellFormula("D" + rowNum + "/C" + rowNum);
		cell.setCellStyle(bluePercentStyle);
	}

	/**
	 * 创建倒计时达成率明细
	 * @param list
	 * @param sheet
	 * @param styleMap
	 * @param rowIndex
	 */
	private void createDetailTable(List<RemainTimeReportEntity> list, HSSFSheet sheet,
			Map<String, HSSFCellStyle> styleMap, int[] rowIndex) {
		sheet.setColumnWidth(0, 6*256);		//序号
		sheet.setColumnWidth(1, 20*256);	//修理单号
		sheet.setColumnWidth(2, 24*256);	//内镜型号
		sheet.setColumnWidth(3, 12*256);	//等级
		sheet.setColumnWidth(4, 20*256);	//机型
		sheet.setColumnWidth(5, 18*256);	//开始时间
		sheet.setColumnWidth(6, 18*256);	//完成时间
		sheet.setColumnWidth(7, 12*256);	//实际用时
		sheet.setColumnWidth(8, 12*256);	//标准时间
		sheet.setColumnWidth(9, 12*256);	//超出时间
		sheet.setColumnWidth(10, 12*256);	//是否返工
		sheet.setColumnWidth(11, 12*256);	//是否BO
		sheet.setColumnWidth(12, 12*256);	//是否达成

		HSSFRow row = null;
		HSSFCell cell = null;
		HSSFCellStyle headerStyle = styleMap.get("headerStyle");
		HSSFCellStyle cellStyle = styleMap.get("cellStyle");
		HSSFCellStyle centerStyle = styleMap.get("centerStyle");
		HSSFCellStyle timeStyle = styleMap.get("timeStyle");
		HSSFCellStyle decimalStyle = styleMap.get("decimalStyle");
		HSSFCellStyle blueHeaderStyle = styleMap.get("blueHeaderStyle");
		HSSFCellStyle bluePercentStyle = styleMap.get("bluePercentStyle");

		rowIndex[0] = rowIndex[0] + 3;
		row = sheet.createRow(rowIndex[0]);

		cell = row.createCell(0);
		cell.setCellValue("序号");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(1);
		cell.setCellValue("修理单号");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(2);
		cell.setCellValue("内镜型号");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(3);
		cell.setCellValue("等级");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(4);
		cell.setCellValue("机型");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(5);
		cell.setCellValue("开始时间");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(6);
		cell.setCellValue("完成时间");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(7);
		cell.setCellValue("实际用时\r\n(分钟)");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(8);
		cell.setCellValue("标准时间\r\n(分钟)");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(9);
		cell.setCellValue("超出时间\r\n(分钟)");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(10);
		cell.setCellValue("是否返工");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(11);
		cell.setCellValue("是否BO");
		cell.setCellStyle(headerStyle);
		
		cell = row.createCell(12);
		cell.setCellValue("是否达成");
		cell.setCellStyle(headerStyle);

		int rowNumStart = row.getRowNum() + 2;
		int rowNumEnd = rowNumStart + list.size() - 1;
		RemainTimeReportEntity entity = null;
		for (int i = 0; i < list.size(); i++) {
			entity = list.get(i);
			rowIndex[0] ++;
			row = sheet.createRow(rowIndex[0]);

			cell = row.createCell(0);//序号
			cell.setCellValue(i+1);
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(1);//修理单号
			cell.setCellValue(entity.getOmr_notifi_no());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(2);//内镜型号
			cell.setCellValue(entity.getModel_name());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(3);//等级
			cell.setCellValue(CodeListUtils.getValue("material_level_heavy",String.valueOf(entity.getLevel())));
			cell.setCellStyle(centerStyle);
			
			cell = row.createCell(4);//机型
			cell.setCellValue(entity.getCategory_name());
			cell.setCellStyle(cellStyle);

			int rework = entity.getRework();
			if (rework == 0) {
				cell = row.createCell(5);//开始时间
				cell.setCellValue(entity.getAction_time());
				cell.setCellStyle(timeStyle);

				cell = row.createCell(6);// 完成时间
				cell.setCellValue(entity.getFinish_time());
				cell.setCellStyle(timeStyle);

				cell = row.createCell(7);//实际用时
				if (entity.getFactwork_minutes() != null) {
					cell.setCellValue(entity.getFactwork_minutes());
				}
				cell.setCellStyle(cellStyle);

				cell = row.createCell(8);//标准时间
				if (entity.getExpected_minutes() != null) {
					cell.setCellValue(entity.getExpected_minutes());
				}
				cell.setCellStyle(cellStyle);
	
				cell = row.createCell(9);//超出时间
				if (entity.getFactwork_minutes() != null && entity.getExpected_minutes() != null) {
					cell.setCellValue(entity.getFactwork_minutes() - entity.getExpected_minutes());
				}
				cell.setCellStyle(cellStyle);

				cell = row.createCell(10);//是否返工
				cell.setCellValue("否");
				cell.setCellStyle(centerStyle);
			} else {
				cell = row.createCell(5);//开始时间
				cell.setCellValue("-");
				cell.setCellStyle(timeStyle);

				cell = row.createCell(6);// 完成时间
				cell.setCellValue("-");
				cell.setCellStyle(timeStyle);

				cell = row.createCell(7);//实际用时
				cell.setCellValue("-");
				cell.setCellStyle(cellStyle);
	
				cell = row.createCell(8);//标准时间
				cell.setCellValue("-");
				cell.setCellStyle(cellStyle);
	
				cell = row.createCell(9);//超出时间
				cell.setCellValue("-");
				cell.setCellStyle(cellStyle);

				cell = row.createCell(10);//是否返工
				cell.setCellValue("是");
				cell.setCellStyle(centerStyle);			
			}

			cell = row.createCell(11);//是否BO
			if (entity.getBo_flg() == 1) {
				cell.setCellValue("是");
			} else {
				cell.setCellValue("否");
			}
			cell.setCellStyle(centerStyle);

			cell = row.createCell(12);//是否达成
			if (rework == 0 && entity.getFinish_time() != null && entity.getExpected_finish_time() != null
					&& entity.getFinish_time().compareTo(entity.getExpected_finish_time()) <= 0) {
				cell.setCellValue("是");
			} else {
				cell.setCellValue("否");
			}
			cell.setCellStyle(centerStyle);
		}

		// 空白行
		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);
		row.setHeight((short) 50);

		// 统计平均时间
		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);

		cell = row.createCell(0);
		cell.setCellStyle(cellStyle);

		HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
		if (patriarch == null) {
			patriarch = sheet.createDrawingPatriarch();
		}
		HSSFClientAnchor anchor = null;
		HSSFSimpleShape shape = null;
		for (int i = 1; i <= 5; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(cellStyle);

			// 斜杠
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) i, row.getRowNum(), (short) i, row.getRowNum());
			shape = patriarch.createSimpleShape(anchor);
			shape.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
			shape.setLineStyle(HSSFSimpleShape.LINESTYLE_SOLID);
			shape.setRotationDegree((short) 90);
		}

		cell = row.createCell(6);
		cell.setCellValue("平均时间");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(7);
		cell.setCellFormula("AVERAGE(H" + rowNumStart + ":H" + rowNumEnd + ")");
		cell.setCellStyle(decimalStyle);

		cell = row.createCell(8);
		cell.setCellFormula("AVERAGE(I" + rowNumStart + ":I" + rowNumEnd + ")");
		cell.setCellStyle(decimalStyle);

		cell = row.createCell(9);
		cell.setCellFormula("AVERAGE(J" + rowNumStart + ":J" + rowNumEnd + ")");
		cell.setCellStyle(decimalStyle);

		for (int i = 10; i <= 12; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(cellStyle);
		}

		// 统计达成率
		rowIndex[0] ++;
		row = sheet.createRow(rowIndex[0]);

		for (int i = 0; i <= 11; i++) {
			cell = row.createCell(i);
			cell.setCellValue("达成率");
			cell.setCellStyle(blueHeaderStyle);
		}

		// 合并单元格
		CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 11);
		sheet.addMergedRegion(region);

		cell = row.createCell(12);
		cell.setCellFormula("COUNTIF(M" + rowNumStart + ":M" + rowNumEnd + ",\"是\")/COUNTA(M" + rowNumStart + ":M" + rowNumEnd + ")");
		cell.setCellStyle(bluePercentStyle);
	}

	/**
	 * 创建倒计时达成率趋势图sheet
	 * @param exportForm
	 * @param list
	 * @param partial_list
	 * @param work
	 * @throws Exception
	 */
	private void createPicSheet(RemainTimeReportForm exportForm, List<RemainTimeReportEntity> list,
			List<RemainTimeReportEntity> partial_list, HSSFWorkbook work) throws Exception {
		HSSFFont font = work.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("微软雅黑");

		HSSFCellStyle cellStyle = work.createCellStyle();
		cellStyle.setFont(font);

		HSSFCellStyle percentStyle = work.createCellStyle();
		percentStyle.setDataFormat(work.createDataFormat().getFormat("0.0%"));
		percentStyle.setFont(font);

		HSSFSheet sheet = work.createSheet("倒计时达成率趋势图");
		sheet.setColumnWidth(0, 6 * 256);
		sheet.setColumnWidth(1, 20 * 256);

		HSSFRow row = null;
		HSSFCell cell = null;

		int rowIndex = 1;
		row = sheet.createRow(rowIndex);

		cell = row.createCell(3);
		cell.setCellValue("倒计时达成率趋势图");
		cell.setCellStyle(cellStyle);

		rowIndex ++;
		row = sheet.createRow(rowIndex);

		cell = row.createCell(1);
		cell.setCellValue("工程");
		cell.setCellStyle(cellStyle);

		// 月份
		int month_col = 0;
		RemainTimeReportEntity entity = null;
		for (int i = 0; i < list.size(); i++) {
			entity = list.get(i);
			if ("00000000012".equals(entity.getLine_id())) {
				cell = row.createCell(i + 2);
				cell.setCellValue(Integer.parseInt(entity.getFinish_month().substring(4, 6)) + "月");
				cell.setCellStyle(cellStyle);
				month_col ++;
			}
		}

		// 零件发放
		rowIndex ++;
		row = sheet.createRow(rowIndex);

		cell = row.createCell(1);
		cell.setCellValue("零件发放");
		cell.setCellStyle(cellStyle);
		for (int i = 0; i < partial_list.size(); i++) {
			entity = partial_list.get(i);
			cell = row.createCell(i + 2);
			cell.setCellValue(entity.getFinish_rate().doubleValue()/100);
			cell.setCellStyle(percentStyle);
		}

		// 分解,NS,总组
		String line_id = "";
		int cellIndex = 1;
		for (int i = 0; i < list.size(); i++) {
			entity = list.get(i);
			if (!line_id.equals(entity.getLine_id())) {
				rowIndex ++;
				row = sheet.createRow(rowIndex);
	
				cell = row.createCell(1);
				cell.setCellValue(entity.getLine_name());
				cell.setCellStyle(cellStyle);
				cellIndex = 1;
			}
			cellIndex ++;
			cell = row.createCell(cellIndex);
			cell.setCellValue(entity.getFinish_rate().doubleValue()/100);
			cell.setCellStyle(percentStyle);

			line_id = entity.getLine_id();
		}
		
		// 目标
		rowIndex ++;
		row = sheet.createRow(rowIndex);

		cell = row.createCell(1);
		cell.setCellValue("目标");
		cell.setCellStyle(cellStyle);

		String remain_time_target = PathConsts.POSITION_SETTINGS.getProperty("remain_time_target");
		BigDecimal b_target = null;
		if (!CommonStringUtil.isEmpty(remain_time_target)) {
			b_target = new BigDecimal(remain_time_target);
			for (int i = 0; i < month_col; i++) {
				cell = row.createCell(i + 2);
				cell.setCellValue(b_target.doubleValue()/100);
				cell.setCellStyle(percentStyle);
			}		
		}

		String chartCacheName ="倒计时达成率" + new Date().getTime() + ".png";
		String chartPic = RvsUtils.convertSVGToPng(exportForm.getSvg(),chartCacheName);
		BufferedImage bufferImg = ImageIO.read(new File(chartPic));
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		ImageIO.write(bufferImg, "png", byteArrayOut);

		HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
		if (patriarch == null) {
			patriarch = sheet.createDrawingPatriarch();
		}

		HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 1, sheet.getLastRowNum() + 2,
				(short) 9, sheet.getLastRowNum() + 10);
		patriarch.createPicture(anchor, work.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize(1);

		// 设置横向打印
		sheet.getPrintSetup().setLandscape(true);
	}

	/**
	 * 倒计时未达成一览查询
	 * @param searchForm
	 * @param listResponse
	 * @param conn
	 */
	public void searchUnreach(RemainTimeReportForm searchForm,
			Map<String, Object> listResponse, SqlSession conn) {
		PositionPlanTimeEntity entity = new PositionPlanTimeEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(searchForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		PositionPlanTimeMapper mapper = conn.getMapper(PositionPlanTimeMapper.class);
		List<PositionPlanTimeEntity> list = mapper.searchCountdownUnreach(entity);
		List<RemainTimeReportForm> listData = new ArrayList<RemainTimeReportForm>();

		for (PositionPlanTimeEntity ppte : list) {
			RemainTimeReportForm ptrf = new RemainTimeReportForm();
			BeanUtil.copyToForm(ppte, ptrf, CopyOptions.COPYOPTIONS_NOEMPTY);
			ptrf.setCountdown_key(ppte.getMaterial_id() + "_" + ppte.getLine_id());
			listData.add(ptrf);
		}

		listResponse.put("listData", listData);
	}

	/**
	 * 倒计时未达成详细查询
	 * @param searchForm
	 * @param listResponse
	 * @param conn
	 */
	public RemainTimeReportForm getUnreachDetail(
			RemainTimeReportForm searchForm, SqlSession conn) {
		String key = searchForm.getCountdown_key();

		PositionPlanTimeMapper mapper = conn.getMapper(PositionPlanTimeMapper.class);
		String[] material_id_line_id = key.split("_");
		PositionPlanTimeEntity detailEntity = mapper.getUnreachDetail(material_id_line_id[0], material_id_line_id[1]);

		RemainTimeReportForm ret = new RemainTimeReportForm();
		CopyOptions co = new CopyOptions();
		co.excludeEmptyString(); co.excludeNull();
		co.fieldRename("plan_end_time", "contrast_time_end");
		co.dateConverter(DateUtil.DATE_TIME_PATTERN, "plan_end_time");
		BeanUtil.copyToForm(detailEntity, ret, co);

		return ret;
	}

	public void updateUnreachCommentl(
			RemainTimeReportForm searchForm, SqlSessionManager conn) {
		String key = searchForm.getCountdown_key();
		String comment = searchForm.getComment();

		PositionPlanTimeMapper mapper = conn.getMapper(PositionPlanTimeMapper.class);
		String[] material_id_line_id = key.split("_");

		PositionPlanTimeEntity entity = new PositionPlanTimeEntity();
		entity.setComment(comment);
		entity.setMaterial_id(material_id_line_id[0]);
		entity.setLine_id(material_id_line_id[1]);
		mapper.setCountdownUnreach(entity);
	}
}

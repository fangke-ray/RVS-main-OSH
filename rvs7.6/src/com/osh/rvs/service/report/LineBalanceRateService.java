package com.osh.rvs.service.report;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.osh.rvs.bean.report.LineBalanceRateEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.report.LineBalanceRateForm;
import com.osh.rvs.mapper.report.LineBalanceRateMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class LineBalanceRateService {

	public void validDate(LineBalanceRateForm searchForm, List<MsgInfo> errors) {
		LineBalanceRateEntity entity = new LineBalanceRateEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(searchForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		Date start_date = entity.getFinish_time_start();// 作业开始时间
		Date end_date = entity.getFinish_time_end();// 作结束业时间

		if (end_date != null && start_date == null) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("validator.required");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "作业开始时间"));
			errors.add(msgInfo);
		} else if (end_date != null && start_date != null && end_date.before(start_date)) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
					"validator.invalidParam.invalidTimeRangeValue", "作业结束时间", "大于", "作业开始时间"));
			errors.add(msg);
		}
	}

	/**
	 * 
	 * @param searchForm
	 * @param listResponse
	 * @param conn
	 * @throws Exception
	 */
	public void searchChatData(LineBalanceRateForm searchForm, Map<String, Object> listResponse, SqlSession conn) throws Exception{
		LineBalanceRateEntity entity = new LineBalanceRateEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(searchForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		Date start_date = entity.getFinish_time_start();// 作业开始时间
		Date end_date = entity.getFinish_time_end();// 作结束业时间
		if (start_date == null || end_date == null) {
			Date[] aWorkTime = formatDate(start_date, end_date);
			entity.setFinish_time_start(aWorkTime[0]);
			entity.setFinish_time_end(aWorkTime[1]);
		}

		LineBalanceRateMapper dao = conn.getMapper(LineBalanceRateMapper.class);
		List<LineBalanceRateEntity> list = dao.searchList(entity);

		List<Object> avgWorkTimeList = new ArrayList<Object>();

		if (list.size() == 0) {
			if (!CommonStringUtil.isEmpty(entity.getProcess_codes())) {
				List<String> xAxisList = new ArrayList<String>();

				String[] processCodes = entity.getProcess_codes().split(",");
				for (String processCode : processCodes) {
					avgWorkTimeList.add(null);
					xAxisList.add(processCode);
				}

				listResponse.put("maxWorkTime", 0);// 最高耗时
				listResponse.put("balanceRate", "(无可分析数据)");// 平衡率
				listResponse.put("xAxisList", xAxisList);
			}
		} else {
			List<String> xAxisList = new ArrayList<String>();
			String balanceRate = "";
			int totalWorkTime = 0;
			int maxWorkTime = 0;
			for (LineBalanceRateEntity oneEntity : list) {
				int avgWorkTime = oneEntity.getAvgWorkTime();
				if (avgWorkTime > maxWorkTime) {
					maxWorkTime = avgWorkTime;
				}
				totalWorkTime += avgWorkTime;
			}
			Map<String, Object> data = null;
			for (LineBalanceRateEntity oneEntity : list) {
				xAxisList.add(oneEntity.getProcess_code() + "<br>" + oneEntity.getPosition_name());

				data = new HashMap<String, Object>();
				int avgWorkTime = oneEntity.getAvgWorkTime();
//				if (avgWorkTime == maxWorkTime) {
//					data.put("color", "#F9453D");
//				} else {
					data.put("color", "#40D079");
//				}
				data.put("y", avgWorkTime);
				avgWorkTimeList.add(data);
			}

			if (list.size() > 0) {
				BigDecimal b1 = new BigDecimal(totalWorkTime);
				BigDecimal b2 = new BigDecimal(maxWorkTime*list.size());
				if (b2.floatValue() > 0f) {
					balanceRate = b1.multiply(new BigDecimal(100)).divide(b2, 1, BigDecimal.ROUND_HALF_UP).toPlainString();
				}
			}

			listResponse.put("maxWorkTime", maxWorkTime);// 最高耗时
			listResponse.put("balanceRate", balanceRate + "%");// 平衡率
			listResponse.put("xAxisList", xAxisList);
		}

		listResponse.put("avgWorkTimeList", avgWorkTimeList);// 平均耗时
	}

	private Date[] formatDate(String startDate, String endDate) {
		Date start_date = DateUtil.toDate(startDate, DateUtil.DATE_PATTERN);
		Date end_date = DateUtil.toDate(endDate, DateUtil.DATE_PATTERN);
		return formatDate(start_date, end_date);
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

	/**
	 * 
	 * @param form
	 * @throws Exception
	 */
	public String createExcel(LineBalanceRateForm exportForm) throws Exception {
		String chartCacheName ="流水线平衡率分析" + new Date().getTime() + ".png";
		String chartPic = RvsUtils.convertSVGToPng(exportForm.getSvg(),chartCacheName);

		//Excel临时文件
		String cacheName ="流水线平衡率分析" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 

		OutputStream out = null;
		try {
			File file = new File(cachePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			HSSFWorkbook work = new HSSFWorkbook();
			HSSFSheet sheet = work.createSheet("流水线平衡率分析");
			sheet.setColumnWidth(0, 1 * 256);
			sheet.setColumnWidth(1, 18 * 256);
			sheet.setColumnWidth(2, 30 * 256);
			HSSFFont font = work.createFont();
			font.setFontHeightInPoints((short) 9);
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
			// 课室
			if (!CommonStringUtil.isEmpty(exportForm.getSection_name())) {
				++index;
				setCellValue(index, "课室", exportForm.getSection_name(), sheet, titleStyle,cellStyle);
			}

			// 工程
			if (!CommonStringUtil.isEmpty(exportForm.getLine_name())) {
				++index;
				setCellValue(index, "工程", exportForm.getLine_name(), sheet, titleStyle,cellStyle);
			}
			
			// 分线
			if (!CommonStringUtil.isEmpty(exportForm.getPx())) {
				++index;
				setCellValue(index, "分线", exportForm.getPx(), sheet, titleStyle,cellStyle);
			}
						
			// 机种
			if (!CommonStringUtil.isEmpty(exportForm.getCategory_name())) {
				++index;
				setCellValue(index, "机种", exportForm.getCategory_name(), sheet, titleStyle,cellStyle);
			}

			// 型号
			if (!CommonStringUtil.isEmpty(exportForm.getModel_name())) {
				++index;
				setCellValue(index, "型号", exportForm.getModel_name(), sheet,  titleStyle,cellStyle);
			}

			// 等级
			if (!CommonStringUtil.isEmpty(exportForm.getLevel())) {
				++index;
				setCellValue(index, "等级", exportForm.getLevel(), sheet, titleStyle,cellStyle);
			}

			// 是否包含返工
			if ("1".equals(exportForm.getRework())) {
				++index;
				setCellValue(index, "是否包含返工", "是", sheet, titleStyle,cellStyle);
			} else if ("2".equals(exportForm.getRework())) {
				++index;
				setCellValue(index, "是否包含返工", "否", sheet, titleStyle,cellStyle);
			}

			// 维修流程制定工位
			if (!CommonStringUtil.isEmpty(exportForm.getProcess_codes())) {
				++index;
				setCellValue(index, "制定工位", exportForm.getProcess_codes(), sheet, titleStyle,cellStyle);
			}

			// 作业时间
			String finish_time_start = "";
			String finish_time_end = "";
			if (CommonStringUtil.isEmpty(exportForm.getFinish_time_start()) || CommonStringUtil.isEmpty(exportForm.getFinish_time_end())) {
				Date[] aWorkTime = formatDate(exportForm.getFinish_time_start(), exportForm.getFinish_time_end());
				finish_time_start = DateUtil.toString(aWorkTime[0], "yyyy年MM月dd日");
				finish_time_end = DateUtil.toString(aWorkTime[1], "yyyy年MM月dd日");
			} else {
				SimpleDateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
				SimpleDateFormat df2 = new SimpleDateFormat("yyyy年MM月dd日");
				finish_time_start = df2.format(df1.parse(exportForm.getFinish_time_start()));
				finish_time_end = df2.format(df1.parse(exportForm.getFinish_time_end()));
			}
			++index;
			setCellValue(index, "作业时间", finish_time_start + "到" + finish_time_end, sheet, titleStyle,cellStyle);

			// 平衡率
			index = index + 2;
			setCellValue(index, "平衡率", exportForm.getBalance_rate(), sheet, titleStyle,cellStyle);

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
	
	private void setCellValue(int rowIndex, String itemName, String itemValue, HSSFSheet sheet, HSSFCellStyle titleStyle,HSSFCellStyle cellStyle) {
		HSSFRow row = sheet.createRow(rowIndex);
		HSSFCell cell = row.createCell(1);
		cell.setCellValue(itemName);
		cell.setCellStyle(titleStyle);

		if (!CommonStringUtil.isEmpty(itemValue)) {
			cell = row.createCell(2);
			cell.setCellValue(itemValue);
			cell.setCellStyle(cellStyle);
		}
	}
}

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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.report.ProductivityEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.report.ProductivityForm;
import com.osh.rvs.mapper.report.ProductivityMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ProductivityService {

	public void validDate(ProductivityForm searchForm, List<MsgInfo> errors) {
		String start_date = searchForm.getStart_date();// 作业开始时间
		String end_date = searchForm.getEnd_date();// 作结束业时间

		if (!CommonStringUtil.isEmpty(end_date) && CommonStringUtil.isEmpty(start_date)) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("validator.required");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "作业开始时间"));
			errors.add(msgInfo);
		} else if (!CommonStringUtil.isEmpty(start_date) && !CommonStringUtil.isEmpty(end_date)
				&& start_date.compareTo(end_date) > 0) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
					"validator.invalidParam.invalidTimeRangeValue", "作业结束时间", "大于", "作业开始时间"));
			errors.add(msg);
		}

		if (errors.size() == 0) {
			if (CommonStringUtil.isEmpty(end_date)) {
				String[] aWorkTime = formatDate(start_date, end_date);
				searchForm.setStart_date(aWorkTime[0]);
				searchForm.setEnd_date(aWorkTime[1]);
			}
		}
	}

	/**
	 * 
	 * @param searchForm
	 * @param listResponse
	 * @param conn
	 * @throws Exception
	 */
	public void searchChatData(ProductivityForm searchForm, Map<String, Object> listResponse, SqlSession conn) throws Exception{
		ProductivityEntity entity = new ProductivityEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(searchForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		ProductivityMapper dao = conn.getMapper(ProductivityMapper.class);
		List<ProductivityEntity> list = dao.searchList(entity);

		List<String> xAxisList = new ArrayList<String>();
		List<Integer> outlineQuantityList = new ArrayList<Integer>();
		List<BigDecimal> workTimeList = new ArrayList<BigDecimal>();
		for (ProductivityEntity oneEntity : list) {
			String outline_date = oneEntity.getOutline_date();
			String outline_year = outline_date.substring(0, 4);
			String outline_month = outline_date.substring(4, 6);
			xAxisList.add(outline_year + "年<br>" + outline_month + "月");

			outlineQuantityList.add(oneEntity.getOutline_quantity());

			// 取得每个月的有效工作日
			int workDays = dao.getworkdays(outline_date + "01",
					outline_date + getDateOfMonth(outline_year, outline_month));

			BigDecimal b1 = new BigDecimal(oneEntity.getOutline_quantity());
			BigDecimal b2 = oneEntity.getAvalible_productive();
			BigDecimal workTime = null;
			if (b2 != null && b2.floatValue() > 0f) {
				// 工作效率=产出量/有效工作日/有效出勤人
				workTime = b1.divide(new BigDecimal(workDays), 3, BigDecimal.ROUND_HALF_UP);
				workTime = workTime.divide(b2, 2, BigDecimal.ROUND_HALF_UP);
			}
			workTimeList.add(workTime);
		}

		listResponse.put("outlineQuantityList", outlineQuantityList);// 产出量
		listResponse.put("workTimeList", workTimeList);// 工作效率
		listResponse.put("xAxisList", xAxisList);
	}

	private String getDateOfMonth(String year, String month) {
		int dateOfMonth = 0;
		Calendar now = Calendar.getInstance();
		int now_year = now.get(Calendar.YEAR);
		int now_month = now.get(Calendar.MONTH) + 1;
		if (now_year == Integer.parseInt(year) && now_month == Integer.parseInt(month)) {
			dateOfMonth = now.get(Calendar.DAY_OF_MONTH);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			dateOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		if (dateOfMonth < 10) {
			return "0" + dateOfMonth;
		} else {
			return String.valueOf(dateOfMonth);
		}
	}

	private String[] formatDate(String startDate, String endDate) {
		String[] aWorkTime = new String[2];

		if (CommonStringUtil.isEmpty(startDate) && CommonStringUtil.isEmpty(endDate)) {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND, 0);
			
			aWorkTime[1] = DateUtil.toString(now.getTime(), "yyyyMM");//结束日期为当前日期
			now.add(Calendar.MONTH, -2);
			aWorkTime[0] = DateUtil.toString(now.getTime(), "yyyyMM");
		} else if (!CommonStringUtil.isEmpty(startDate) && CommonStringUtil.isEmpty(endDate)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtil.toDate(startDate, "yyyyMM"));
			cal.add(Calendar.MONTH, 2);

			aWorkTime[1] = DateUtil.toString(cal.getTime(), "yyyyMM");
			aWorkTime[0] = startDate;
		}
		return aWorkTime;
	}

	/**
	 * 
	 * @param inputForm
	 * @param user
	 * @param conn
	 * @throws Exception
	 */
	public void update(ProductivityForm inputForm, LoginData user, SqlSessionManager conn) throws Exception{
		ProductivityEntity entity = new ProductivityEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(inputForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		entity.setOutline_year(inputForm.getOutline_date().substring(0, 4));
		entity.setOutline_month(inputForm.getOutline_date().substring(4, 6));
		entity.setUpdated_by(user.getOperator_id());

		ProductivityMapper dao = conn.getMapper(ProductivityMapper.class);
		if (dao.checkMonthData(entity) > 0) {
			dao.updateMonthData(entity);
		} else {
			dao.insertMonthData(entity);
		}
	}

	/**
	 * 
	 * @param form
	 * @throws Exception
	 */
	public String createExcel(ProductivityForm exportForm) throws Exception {
		String chartCacheName ="生产效率" + new Date().getTime() + ".png";
		String chartPic = RvsUtils.convertSVGToPng(exportForm.getSvg(),chartCacheName);

		//Excel临时文件
		String cacheName ="生产效率" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 

		OutputStream out = null;
		try {
			File file = new File(cachePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			HSSFWorkbook work = new HSSFWorkbook();
			HSSFSheet sheet = work.createSheet("生产效率");
			sheet.setColumnWidth(0, 1 * 256);
			sheet.setColumnWidth(1, 18 * 256);
			sheet.setColumnWidth(2, 25 * 256);
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
			// 作业时间
			String start_date = exportForm.getStart_date();
			String end_date = exportForm.getEnd_date();
			if (CommonStringUtil.isEmpty(start_date) || CommonStringUtil.isEmpty(end_date)) {
				String[] aWorkTime = formatDate(start_date, end_date);
				start_date = aWorkTime[0].substring(0, 4) + "年" + aWorkTime[0].substring(4, 6) + "月";
				end_date = aWorkTime[1].substring(0, 4) + "年" + aWorkTime[1].substring(4, 6) + "月";
			} else {
				start_date = start_date.substring(0, 4) + "年" + start_date.substring(4, 6) + "月";
				end_date = end_date.substring(0, 4) + "年" + end_date.substring(4, 6) + "月";
			}
			++index;
			setCellValue(index, "作业时间", start_date + "到" + end_date, sheet, titleStyle,cellStyle);

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
	
	private void setCellValue(int rowIndex, String itemName, String itemValue, HSSFSheet sheet,HSSFCellStyle titleStyle,HSSFCellStyle cellStyle) {
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

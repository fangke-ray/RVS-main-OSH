package com.osh.rvs.service.inline;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
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

import com.osh.rvs.bean.report.FoundryEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.mapper.report.FoundryMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class FoundryService {
	public void search(ActionForm form, Map<String, Object> listResponse,SqlSession conn) throws Exception {
		FoundryEntity entity = new FoundryEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		FoundryMapper dao = conn.getMapper(FoundryMapper.class);
		List<FoundryEntity> list = dao.searchFoundryOfPosition(entity);

		// 工位代工时间
		List<Integer> positionFoundryWorkList = new ArrayList<Integer>();
		// 工位作业时间
		List<Integer> positionMainWorkList = new ArrayList<Integer>();
		// 工位X轴
		List<String> positionXAxisList = new ArrayList<String>();

		for (int i = 0; i < list.size(); i++) {
			FoundryEntity temp = list.get(i);
			positionFoundryWorkList.add(temp.getFoundryWork());
			positionMainWorkList.add(temp.getMainWork());
			positionXAxisList.add(temp.getProcess_code());
		}
		
		// 担当人代工时间
		List<Integer> operatorFoundryWorkList = new ArrayList<Integer>();
		// 担当人作业时间
		List<Integer> operatorMainWorkList = new ArrayList<Integer>();
		// 担当人X轴
		List<String> operatorXAxisList = new ArrayList<String>();
		list = dao.searchFoundryOfOperator(entity);
		for (int i = 0; i < list.size(); i++) {
			FoundryEntity temp =list.get(i);
			operatorFoundryWorkList.add(temp.getFoundryWork());
			operatorMainWorkList.add(temp.getMainWork());
			operatorXAxisList.add(temp.getOperator_name());
		}
		
		listResponse.put("positionFoundryWorkList", positionFoundryWorkList);
		listResponse.put("positionMainWorkList", positionMainWorkList);
		listResponse.put("positionXAxisList", positionXAxisList);
		
		listResponse.put("operatorFoundryWorkList", operatorFoundryWorkList);
		listResponse.put("operatorMainWorkList", operatorMainWorkList);
		listResponse.put("operatorXAxisList", operatorXAxisList);
	}

	public void getLineFoundryOfDate(ActionForm form, Map<String, Object> listResponse,SqlSession conn) throws Exception {
		FoundryEntity entity = new FoundryEntity();
		CopyOptions cos = new CopyOptions();
		cos.excludeNull();cos.excludeEmptyString();
		cos.fieldRename("action_date", "finish_time_start");

		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, cos);

		FoundryMapper dao = conn.getMapper(FoundryMapper.class);
		List<FoundryEntity> list = dao.searchFoundryOfLine(entity);

		listResponse.put("foundryList", list);
	}


	public String createExcel(ActionForm form,SqlSession conn) throws Exception{
		FoundryEntity entity = new FoundryEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		String chartCacheName ="代工时间统计" + new Date().getTime() + ".png";
		String chartPic = RvsUtils.convertSVGToPng(entity.getSvg(),chartCacheName);
		String chartPic2 = RvsUtils.convertSVGToPng(entity.getSvg2(),Math.random()*10+chartCacheName);
		
		//Excel临时文件
		String cacheName ="代工时间统计" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		FoundryMapper dao = conn.getMapper(FoundryMapper.class);
		List<FoundryEntity> list = dao.searchFoundryOfPositionAndOperator(entity);

		
		OutputStream out = null;
		try {
			File file = new File(cachePath);
			if(!file.exists()){
				file.createNewFile();
			}
			
			HSSFWorkbook work=new HSSFWorkbook();
			HSSFSheet sheet = work.createSheet("代工时间统计");
			
			HSSFRow row = null;
			HSSFCell cell = null;
			
			sheet.setColumnWidth(0, 1*256);
			sheet.setColumnWidth(1, 15*256);
			sheet.setColumnWidth(2, 20*256);
			sheet.setColumnWidth(3, 9*256);
			sheet.setColumnWidth(4, 16*256);
			
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
			
			setCellValue(0, "课室", entity.getSection_name(), sheet, titleStyle,cellStyle);
			setCellValue(1, "工程", entity.getLine_name(), sheet, titleStyle,cellStyle);
			setCellValue(2, "作业开始时间",  DateUtil.toString(entity.getFinish_time_start(), DateUtil.DATE_PATTERN), sheet, titleStyle,cellStyle);
			setCellValue(3, "作业结束时间", DateUtil.toString(entity.getFinish_time_end(), DateUtil.DATE_PATTERN), sheet,titleStyle, cellStyle);
			
			BufferedImage bufferImg=ImageIO.read(new File(chartPic));
			ByteArrayOutputStream byteArrayOut=new ByteArrayOutputStream();
			ImageIO.write(bufferImg, "png",byteArrayOut);
			
			HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
			if (patriarch == null) {
				patriarch = sheet.createDrawingPatriarch();
			}
			
			HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 1, sheet.getLastRowNum()+2, (short) 9, sheet.getLastRowNum() + 10);
			patriarch.createPicture(anchor,work.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize(1);
			int lastRowNum = anchor.getRow2();
			lastRowNum = lastRowNum + 2;
			
			row = sheet.createRow(lastRowNum);
			cell = row.createCell(1);
			cell.setCellValue("被代工工位");
			cell.setCellStyle(titleStyle);
			
			cell = row.createCell(2);
			cell.setCellValue("代工者");
			cell.setCellStyle(titleStyle);
			
			cell = row.createCell(3);
			cell.setCellValue("主要负责");
			cell.setCellStyle(titleStyle);
			
			cell = row.createCell(4);
			cell.setCellValue("代工时间（分钟）");
			cell.setCellStyle(titleStyle);
			
			for (int i = 0; i < list.size(); i++) {
				 lastRowNum ++;
				 entity = list.get(i);
				 
				 row = sheet.createRow(lastRowNum);
				 
				 cell = row.createCell(1);//工位
				 cell.setCellValue(entity.getProcess_code());
				 cell.setCellStyle(cellStyle);
				 cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				 
				 cell = row.createCell(2);//人员
				 cell.setCellValue(entity.getOperator_name());
				 cell.setCellStyle(cellStyle);
				 
				 cell = row.createCell(3);//主要负责
				 cell.setCellValue(entity.getMainIncharge());
				 cell.setCellStyle(cellStyle);
				 
				 cell = row.createCell(4);//代工时间
				 cell.setCellValue(entity.getFoundryWork());
				 cell.setCellStyle(cellStyle);
			}
			
			bufferImg=ImageIO.read(new File(chartPic2));
			byteArrayOut=new ByteArrayOutputStream();
			ImageIO.write(bufferImg, "png",byteArrayOut);
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 1, sheet.getLastRowNum() + 2, (short) 9, sheet.getLastRowNum() + 10);
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
	
}

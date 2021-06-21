package com.osh.rvs.common;

import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

import framework.huiqing.common.util.copy.DateUtil;

public class CopyByPoi {

	/**
	 * 复制一个单元格样式到目的单元格样式
	 * 
	 * @param fromStyle
	 * @param toStyle
	 */
	public static void copyCellStyle(HSSFCellStyle fromStyle, HSSFCellStyle toStyle) {
		toStyle.setAlignment(fromStyle.getAlignment());
		// 边框和边框颜色
		toStyle.setBorderBottom(fromStyle.getBorderBottom());
		toStyle.setBorderLeft(fromStyle.getBorderLeft());
		toStyle.setBorderRight(fromStyle.getBorderRight());
		toStyle.setBorderTop(fromStyle.getBorderTop());
		toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
		toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
		toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
		toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

		// 背景和前景
		toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
		toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

		toStyle.setDataFormat(fromStyle.getDataFormat());
		toStyle.setFillPattern(fromStyle.getFillPattern());
		toStyle.setHidden(fromStyle.getHidden());
		toStyle.setIndention(fromStyle.getIndention());// 首行缩进
		toStyle.setLocked(fromStyle.getLocked());
		toStyle.setRotation(fromStyle.getRotation());// 旋转
		toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
		toStyle.setWrapText(fromStyle.getWrapText());
	}

	/**
	 * 行复制功能
	 * 
	 * @param fromRow
	 * @param toRow
	 */
	public static void copyRow(HSSFRow fromRow, HSSFRow toRow, boolean copyValueFlag) {
		toRow.setHeight(fromRow.getHeight());

		for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext();) {
			HSSFCell tmpCell = (HSSFCell) cellIt.next();
			HSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());
			copyCell(tmpCell, newCell, copyValueFlag);
		}
	}

	/**
	 * 复制单元格
	 * 
	 * @param srcCell
	 * @param distCell
	 * @param copyValueFlag
	 *            true则连同cell的内容一起复制
	 */
	public static void copyCell(HSSFCell srcCell, HSSFCell distCell, boolean copyValueFlag) {
		// HSSFCellStyle newstyle=wb.createCellStyle();
		// copyCellStyle(srcCell.getCellStyle(), newstyle);
		// distCell.setEncoding(srcCell.getEncoding());
		// 样式
		distCell.setCellStyle(srcCell.getCellStyle());
		// 评论
		if (srcCell.getCellComment() != null) {
			distCell.setCellComment(srcCell.getCellComment());
		}
		// 不同数据类型处理
		int srcCellType = srcCell.getCellType();
		distCell.setCellType(srcCellType);
		if (copyValueFlag) {
			if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
					distCell.setCellValue(srcCell.getDateCellValue());
				} else {
					distCell.setCellValue(srcCell.getNumericCellValue());
				}
			} else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
				distCell.setCellValue(srcCell.getRichStringCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {
				// nothing21
			} else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
				distCell.setCellValue(srcCell.getBooleanCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
				distCell.setCellErrorValue(srcCell.getErrorCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
				distCell.setCellFormula(srcCell.getCellFormula());
			} else { // nothing29
			}
		}
	}
	
	/**
	 * 根据单元格不同属性返回字符串
	 * 
	 * @param cell
	 *            Excel单元格
	 * @return String 单元格数据内容
	 */
	public static String getCellStringValue(HSSFCell cell) {
		if (cell == null) {
	           return "";
	    }
		String strCell = "";
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			strCell = String.valueOf((double) cell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		default:
			strCell = "";
			break;
		}
		
		return strCell;
	}

	/**
	 * 根据单元格不同属性返回字符串
	 * 
	 * @param cell
	 *            Excel单元格
	 * @return String 单元格数据内容
	 */
	public static String getCellStringValue(XSSFCell cell) {
		if (cell == null) {
	           return "";
	    }
		String strCell = "";
		switch (cell.getCellType()) {
		case XSSFCell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		case XSSFCell.CELL_TYPE_NUMERIC:
			strCell = String.valueOf((double) cell.getNumericCellValue());
			break;
		case XSSFCell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case XSSFCell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		default:
			strCell = "";
			break;
		}
		
		return strCell;
	}

	/**
	 * 根据单元格不同属性返回日期
	 * 
	 * @param cell
	 *            Excel单元格
	 * @return String 单元格数据内容
	 */
	public static Date getCellDateValue(HSSFCell cell) {
		if (cell == null) {
	           return null;
	    }
		Date dtCell = null;
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			dtCell = DateUtil.toDate(cell.getStringCellValue(), DateUtil.DATE_PATTERN);
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			dtCell = cell.getDateCellValue();
			break;
		default:
			dtCell = null;
			break;
		}
		
		return dtCell;
	}

	/**
	 * 根据单元格不同属性返回日期
	 * 
	 * @param cell
	 *            Excel单元格
	 * @return String 单元格数据内容
	 */
	public static Date getCellDateValue(XSSFCell cell) {
		if (cell == null) {
			return null;
	    }
		Date dtCell = null;
		switch (cell.getCellType()) {
		case XSSFCell.CELL_TYPE_STRING:
			dtCell = DateUtil.toDate(cell.getStringCellValue(), DateUtil.DATE_PATTERN);
			break;
		case XSSFCell.CELL_TYPE_NUMERIC:
			dtCell = cell.getDateCellValue();
			break;
		default:
			dtCell = null;
			break;
		}
		
		return dtCell;
	}
}

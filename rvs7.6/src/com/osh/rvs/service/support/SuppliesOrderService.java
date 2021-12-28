package com.osh.rvs.service.support;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.support.SuppliesDetailEntity;
import com.osh.rvs.bean.support.SuppliesOrderEntity;
import com.osh.rvs.common.CopyByPoi;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.XlsUtil;
import com.osh.rvs.form.support.SuppliesOrderForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.support.SuppliesDetailMapper;
import com.osh.rvs.mapper.support.SuppliesOrderMapper;

import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

/**
 * 
 * @Description 物品申购单
 * @author liuxb
 * @date 2021-12-7 下午1:59:51
 */
public class SuppliesOrderService {
	private Logger _log = Logger.getLogger(getClass());
	
	/**
	 * 慧采
	 */
	private final String SPEC_SUPPLIER = "慧采";

	/**
	 * 查询订购单
	 * 
	 * @param form
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesOrderForm> search(ActionForm form, SqlSession conn) throws Exception {
		SuppliesOrderMapper dao = conn.getMapper(SuppliesOrderMapper.class);

		SuppliesOrderEntity entity = new SuppliesOrderEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<SuppliesOrderEntity> list = dao.search(entity);
		List<SuppliesOrderForm> respList = new ArrayList<SuppliesOrderForm>();

		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, SuppliesOrderForm.class);

		for (SuppliesOrderForm suppliesOrder : respList) {
			String dirPath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\supplies_order\\" + suppliesOrder.getOrder_key() + "\\";
			File dir = new File(dirPath);
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();

				List<String> fileNames = new ArrayList<String>();
				for (File f : files) {
					String fileName = f.getName();
					if (fileName.endsWith(".pdf")) {
						fileNames.add(fileName);
					}
				}
				suppliesOrder.setFile_names(fileNames);
			}
		}

		return respList;
	}

	/**
	 * 新建物品申购单
	 * 
	 * @param req
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String insert(HttpServletRequest req, ActionForm form, SqlSessionManager conn) throws Exception {
		SuppliesOrderMapper dao = conn.getMapper(SuppliesOrderMapper.class);
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);

		SuppliesOrderEntity entity = new SuppliesOrderEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 当前登录者
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		// 订购人员
		entity.setOperator_id(user.getOperator_id());
		// 订购日期取得系统日期
		entity.setOrder_date(Calendar.getInstance().getTime());

		dao.insert(entity);

		String orderKey = commonMapper.getLastInsertID();
		return orderKey;
	}

	/**
	 * 根据订购单号查询
	 * 
	 * @param orderNo 订购单号
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public SuppliesOrderForm getOrderByOrderNo(String orderNo, SqlSession conn) throws Exception {
		SuppliesOrderMapper dao = conn.getMapper(SuppliesOrderMapper.class);

		SuppliesOrderEntity entity = dao.getByOrderNo(orderNo);

		SuppliesOrderForm form = null;
		if (entity != null) {
			form = new SuppliesOrderForm();
			BeanUtil.copyToForm(entity, form, CopyOptions.COPYOPTIONS_NOEMPTY);
		}

		return form;
	}

	/**
	 * 根据订购单KEY查询
	 * 
	 * @param orderNo 订购单KEY
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public SuppliesOrderForm getOrderByOrderKey(String orderKey, SqlSession conn) throws Exception {
		SuppliesOrderMapper dao = conn.getMapper(SuppliesOrderMapper.class);

		SuppliesOrderEntity entity = dao.getByOrderKey(orderKey);

		SuppliesOrderForm form = null;
		if (entity != null) {
			form = new SuppliesOrderForm();
			BeanUtil.copyToForm(entity, form, CopyOptions.COPYOPTIONS_NOEMPTY);
		}

		return form;
	}

	/**
	 * 盖章
	 * 
	 * @param form
	 * @param req
	 * @param conn
	 */
	public void sign(ActionForm form, HttpServletRequest req, SqlSessionManager conn) throws Exception {
		SuppliesOrderMapper dao = conn.getMapper(SuppliesOrderMapper.class);

		// 当前登录者
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		SuppliesOrderForm suppliesOrderForm = (SuppliesOrderForm) form;
		// KEY
		String orderKey = suppliesOrderForm.getOrder_key();
		// 确认标记
		String confirmFlg = suppliesOrderForm.getConfirm_flg();

		SuppliesOrderEntity entity = dao.getByOrderKey(orderKey);

		if ("1".equals(confirmFlg)) {// 经理盖章
			// 经理ID
			entity.setSign_manager_id(user.getOperator_id());
			// 经理工号
			entity.setManager_job_no(user.getJob_no());
			dao.sign(entity);

			// 判断部长是否盖过章
			if (!CommonStringUtil.isEmpty(entity.getSign_minister_id())) {// 部长盖过章
				// 生成文件
				createOrderFile(entity, conn);
			}
		} else {
			// 部长ID
			entity.setSign_minister_id(user.getOperator_id());
			// 部长工号
			entity.setMinister_job_no(user.getJob_no());
			dao.sign(entity);

			// 判断经理是否盖过章
			if (!CommonStringUtil.isEmpty(entity.getSign_manager_id())) {// 经理盖过章
				// 生成文件
				createOrderFile(entity, conn);
			}
		}
	}

	private void createOrderFile(SuppliesOrderEntity entity, SqlSession conn) throws Exception {
		// 物品申购单Key
		String orderKey = entity.getOrder_key();
		// 申购单号
		String orderNo = entity.getOrder_no();

		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		// 慧采数据
		List<SuppliesDetailEntity> specList = new ArrayList<SuppliesDetailEntity>();
		// 其他数据
		List<SuppliesDetailEntity> otherList = new ArrayList<SuppliesDetailEntity>();

		List<SuppliesDetailEntity> list = dao.getDetailByOrderKey(orderKey);
		for (SuppliesDetailEntity item : list) {
			// 供应商
			String supplier = item.getSupplier();

			if (!CommonStringUtil.isEmpty(supplier) && supplier.contains(SPEC_SUPPLIER)) {
				specList.add(item);
			} else {
				otherList.add(item);
			}
		}

		// 模板文件
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "一般物品申购单.xls";
		// 目录
		String basePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\supplies_order\\" + orderKey + "\\";
		String cacheFilename = "一般物品申购单#{no}" + orderNo + ".xls";
		
		String cachePath1 = null;
		String cachePath2 = null;
		if (otherList.size() > 0 && specList.size() > 0) {// 含有慧采
			cachePath1 = basePath + cacheFilename.replaceAll("#\\{no\\}", "-1");
			// 慧采文件
			cachePath2 = basePath + cacheFilename.replaceAll("#\\{no\\}", "-2");
		} else if (otherList.size() > 0 && specList.size() == 0) {
			cachePath1 = basePath + cacheFilename.replaceAll("#\\{no\\}", "");
		} else if (otherList.size() == 0 && specList.size() > 0) {
			// 慧采文件
			cachePath2 = basePath + cacheFilename.replaceAll("#\\{no\\}", "");
		}

		if (!CommonStringUtil.isEmpty(cachePath1)) {
			try {
				FileUtils.copyFile(new File(path), new File(cachePath1));
			} catch (IOException e) {
				_log.error("模板文件拷贝失败。");
				_log.error(e.getMessage(), e);
				throw e;
			}

			setExcelContent(cachePath1, entity, otherList, false);
		}

		if (!CommonStringUtil.isEmpty(cachePath2)) {
			try {
				FileUtils.copyFile(new File(path), new File(cachePath2));
			} catch (IOException e) {
				_log.error("模板文件拷贝失败。");
				_log.error(e.getMessage(), e);
				throw e;
			}

			setExcelContent(cachePath2, entity, specList, true);
		}
	}

	/**
	 * 设置Excel内容
	 * 
	 * @param filePath 文件
	 * @param order 订购单
	 * @param list 订购明细
	 * @param specFlag 慧采标记
	 * @throws Exception
	 */
	private void setExcelContent(String filePath, SuppliesOrderEntity order, List<SuppliesDetailEntity> list, boolean specFlag) throws Exception {
		_log.info("setExcelContent方法执行开始");
		
		String pdfName = filePath.replaceAll("\\.xls", ".pdf");
		
		InputStream in = null;
		OutputStream out = null;

		try {
			_log.info("设置Excel内容开始");
			in = new FileInputStream(filePath);// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = work.getSheetAt(0);// 取得第一个Sheet

			// 设置分页区域
			sheet.setRowBreak(31);
			sheet.setColumnBreak(12);

			// OSH-No.
			HSSFRow row = sheet.getRow(2);
			HSSFCell cell = row.getCell(11);
			String originalCellValue = cell.getStringCellValue();
			cell.setCellValue(originalCellValue + order.getOrder_no());

			// 提交日期
			row = sheet.getRow(5);
			cell = row.getCell(3);
			cell.setCellValue(DateUtil.toString(order.getOrder_date(), DateUtil.DATE_PATTERN));

			// 申请部门
			row = sheet.getRow(6);
			cell = row.getCell(3);
			if(!CommonStringUtil.isEmpty(order.getSection_full_name())){
				cell.setCellValue(order.getSection_full_name());
			} else {
				cell.setCellValue("");
			}

			// 申请担当
			row = sheet.getRow(7);
			cell = row.getCell(3);
			cell.setCellValue(order.getOperator_name());

			// 经理
			String managerName = PathConsts.BASE_PATH + PathConsts.IMAGES + "\\sign\\" + order.getManager_job_no();
			insertImage(work, sheet, 10, 6, managerName);

			// 部长
			String ministerName = PathConsts.BASE_PATH + PathConsts.IMAGES + "\\sign\\" + order.getMinister_job_no();
			insertImage(work, sheet, 11, 6, ministerName);
			
			if(specFlag) {
				row = sheet.getRow(9);
				cell = row.getCell(5);
				
				originalCellValue = cell.getStringCellValue();
				originalCellValue = originalCellValue.replaceAll("商品编号", SPEC_SUPPLIER);
				cell.setCellValue(originalCellValue);
			}

			// 数据量
			int length = list.size();
			// 默认一页数据量为18条
			int normalPageNumbers = 18;
			// 最大认一页数据量为21条
			int maxPageNumbers = 21;
			// 备注行数（3行）
			int commentNumbers = 3;
			// 默认灰色行数
			int grayRowNums = 2;
			// 备注移动的行数
			int shiftRows = 0;
			
			// 默认一页数据量为18条，当数据量超过一页时，一页数据量为21条（移除三行备注到下一页）
			if (length > normalPageNumbers) {
				// 计算页数
				// 需要复制的页数(不包含第一页)
				int pageNum = 0;
				
				// 19条数据到21条数据之间，还需要一页，备注放置在最后一页就行
				if (length <= maxPageNumbers) {
					pageNum = 1;
					// 向下将备注移动（备注自身行数加上默认灰色行数）行
					shiftRows = commentNumbers + grayRowNums;
					// 需要补充的灰色行数
					grayRowNums = grayRowNums + (maxPageNumbers - length);
				} else {
					// 大于21条数据，需要动态计算需要的页数，以及需要判断最后一页能否放得下备注（备注不能跨页显示）
					// 按最大数据量统计
					int rest = length - maxPageNumbers;
					pageNum = rest % maxPageNumbers == 0 ? rest / maxPageNumbers : rest / maxPageNumbers + 1;

					// 最后一页数据量
					// pageNum因为不包含第一页，此时pageNum刚好是最后一页之前的页数总和，
					// 所以最后一页的数据量 = 总数据量【length】 - 最后一页之前的数据量总和【pageNum * maxPageNumbers】
					int lastPageNumbers = length - (pageNum * maxPageNumbers);

					// 最后一页数据量 + 备注行数，如果大于每页最大数量，则需要一页单独放置备注
					// 解决跨页问题
					if ((lastPageNumbers + commentNumbers) > maxPageNumbers) {
						// 备注单独放一页
						pageNum++;
						// 整页移动，加上备注自身行数，加上默认灰色行数
						shiftRows = (pageNum - 1) * maxPageNumbers + commentNumbers + grayRowNums;
						// 需要补充的灰色行数
						grayRowNums = grayRowNums + (maxPageNumbers - lastPageNumbers);
					} else {
						// 备注放在最后一页数据后面
						// 最后一页剩余空白行数
						int blankRowNums = maxPageNumbers - lastPageNumbers - commentNumbers;
						// 空白行数小于等于默认灰色行数
						if(blankRowNums <= grayRowNums) {
							grayRowNums = blankRowNums;
						}
						// 整页移动，加上最后一页数据量，加上备注自身行数,加上灰色行数
						shiftRows = (pageNum - 1) * maxPageNumbers + lastPageNumbers + commentNumbers + grayRowNums;
					}
				}
				
				// 向下移动备注
				sheet.shiftRows(29, 31, shiftRows, true, false);
				
				// 设置分页区域
				for (int i = 1; i <= pageNum; i++) {
					sheet.setRowBreak(31 + (i * 21));
				}
			} else {
				// 小于等于18条数据
				// 剩余空白行数
				int blankRowNums = normalPageNumbers - length;
				
				// 空白行数大于默认灰色行数
				if(blankRowNums > grayRowNums) {
					//备注向上移动（空白行数减去灰色行数）
					shiftRows = -(blankRowNums - grayRowNums);
				} else {
					grayRowNums = blankRowNums;
				}
				// 向上移动备注
				sheet.shiftRows(29, 31, shiftRows, true, false);
			}
			
			// 模板行索引
			int templateRowId = 11;
			// 模板行
			HSSFRow templateRow = sheet.getRow(templateRowId);
			// 模板行合并单元格集合
			List<CellRangeAddress> celllRangeList = new ArrayList<>();
			// 当前sheet合并单元格总数
			int sheetMergerCount = sheet.getNumMergedRegions();
			for (int i = 0; i < sheetMergerCount; i++) {
				CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
				int firstRow = cellRangeAddress.getFirstRow();
				int lastRow = cellRangeAddress.getLastRow();
				// 判断模板行合并单元格
				if (firstRow == templateRowId && lastRow == templateRowId) {
					celllRangeList.add(cellRangeAddress);
				}
			}

			int lastRowIndex = 0;
			for (int index = 0; index < length; index++) {
				SuppliesDetailEntity detail = list.get(index);

				// 行索引
				int rowIndex = index + templateRowId;
				lastRowIndex = rowIndex;
				
				row = sheet.getRow(rowIndex);
				if (rowIndex != templateRowId) {
					row = sheet.getRow(rowIndex);
					if (row == null) {
						row = sheet.createRow(rowIndex);
					}
					CopyByPoi.copyRow(templateRow, row, false);

					// 创建合并单元格
					for (CellRangeAddress cellRangeAddress : celllRangeList) {
						int firstCol = cellRangeAddress.getFirstColumn();
						int lastCol = cellRangeAddress.getLastColumn();
						CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol);
						sheet.addMergedRegion(region);
					}
				}

				// No.
				cell = row.getCell(0);
				cell.setCellValue(index + 1);

				// 商品内容
				cell = row.getCell(1);
				cell.setCellValue(detail.getProduct_name());

				// 規格
				cell = row.getCell(5);
				if (!CommonStringUtil.isEmpty(detail.getModel_name())) {
					cell.setCellValue(detail.getModel_name());
				} else {
					cell.setCellValue("");
				}

				// 数量/単位
				cell = row.getCell(8);
				int quantity = detail.getQuantity();
				String unitText = detail.getUnit_text();
				if(CommonStringUtil.isEmpty(unitText)){
					unitText = "";
				}
				cell.setCellValue(quantity + " " + unitText);
				
				// 成本中心
				cell = row.getCell(10);
				if(!CommonStringUtil.isEmpty(detail.getSection_full_name())){
					cell.setCellValue(detail.getSection_full_name() + "，" + detail.getApplicator_name());
				} else {
					cell.setCellValue(detail.getApplicator_name());
				}
				
				// 备注
				cell = row.getCell(12);
				if(!CommonStringUtil.isEmpty(detail.getComments())) {
					cell.setCellValue(detail.getComments());
				} else {
					cell.setCellValue("");
				}
			}
			
			HSSFCellStyle cellGrayStyle = work.createCellStyle();
			cellGrayStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellGrayStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			cellGrayStyle.setBorderRight(HSSFCellStyle.BORDER_THIN); 
			cellGrayStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellGrayStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellGrayStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
			
			//补充灰色行
			for(int i = 1; i <= grayRowNums; i++){
				int rowIndex = lastRowIndex + i;
				
				row = sheet.createRow(rowIndex);
				row.setHeight(sheet.getRow(templateRowId).getHeight());
				for(int colIndex = 0; colIndex< 13; colIndex++){
					cell = row.createCell(colIndex);
					cell.setCellStyle(cellGrayStyle);
				}
				
				// 创建合并单元格
				for (CellRangeAddress cellRangeAddress : celllRangeList) {
					int firstCol = cellRangeAddress.getFirstColumn();
					int lastCol = cellRangeAddress.getLastColumn();
					CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol);
					sheet.addMergedRegion(region);
				}
			}

			// 输出文件
			out = new FileOutputStream(filePath);
			// 写入文件
			work.write(out);
			_log.info("设置Excel内容结束");
			
			_log.info("保存为pdf文件开始");
			XlsUtil xls = new XlsUtil(filePath);
			xls.SaveAsPdf(pdfName);
			_log.info("保存为pdf文件结束");
		} catch (Exception e) {
			_log.error("生成文件出错");
			_log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					_log.error(e.getMessage(), e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					_log.error(e.getMessage(), e);
				}
			}
		}
		_log.info("setExcelContent方法执行结束");
	}

	/**
	 * 插入图片
	 * 
	 * @param work Excel表格
	 * @param sheet
	 * @param iCol 列位子
	 * @param iLine 行位子
	 * @param fileName 图片名称
	 */
	private void insertImage(HSSFWorkbook work, HSSFSheet sheet, int iCol, int iLine, String fileName) {
		ByteArrayOutputStream byteArrayOut = null;
		try {
			BufferedImage bufferImg = ImageIO.read(new File(fileName));
			byteArrayOut = new ByteArrayOutputStream();
			ImageIO.write(bufferImg, "jpeg", byteArrayOut);
			HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
			if (patriarch == null) {
				patriarch = sheet.createDrawingPatriarch();
			}
			HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) iCol, iLine, (short) iCol, iLine + 1);
			anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
			patriarch.createPicture(anchor, work.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG)).resize(1);
		} catch (Exception e) {
			_log.error("图片文件不存在" + fileName, e);
		} finally {
			if (byteArrayOut != null) {
				try {
					byteArrayOut.close();
				} catch (IOException e) {
					_log.error(e.getMessage(), e);
				}
			}
		}
	}
}
package com.osh.rvs.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.mapper.qf.AcceptanceMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.DateUtil;

public class DownloadService {
	public static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
	public static final String CONTENT_TYPE_EXCEL_OPENXML = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String CONTENT_TYPE_PDF = "application/pdf";
	public static final String CONTENT_TYPE_GIF = "image/gif";
	public static final String CONTENT_TYPE_ZIP = "application/zip";

	private static final int ACCEPTANCE_WORK_REPORT_START_LINE = 5;
	//private static final int ACCEPTANCE_WORK_REPORT_FILL_LINES = 27;

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 文件流输出
	 * @param res 输出目标相应
	 * @param contentType 输出上下文类型
	 * @param fileName 输出文件名
	 * @param filePath 数据源文件
	 * @throws Exception
	 */
	public void writeFile(HttpServletResponse res, String contentType, String fileName, String filePath) throws Exception {
		res.setHeader("Content-Disposition","attachment;filename=\""+ RvsUtils.charEncode(fileName) + "\"");
		res.setContentType(contentType);
		File file = new File(filePath);
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		is.close();
		
		OutputStream os = new BufferedOutputStream(res.getOutputStream());
		os.write(buffer);
		os.flush();
		os.close();
	}

//	/**
//	 * 打印小票第一版
//	 * @param mBean
//	 * @param conn
//	 * @return
//	 * @throws Exception
//	 */
//	public String printTicketV1(MaterialEntity mBean, SqlSession conn) throws Exception {
//
//		Rectangle rect = new Rectangle(215, 140);
//		Document document = new Document(rect, 0, 0, 0, 0);
//
//		Date today = new Date();
//		String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");
//		String filename = UUID.randomUUID().toString() + ".pdf";
//
//		try {
//			PdfWriter pdfWriter = PdfWriter.getInstance(document,
//					new FileOutputStream(folder + "\\" + filename));
//			document.open();
//			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
//					"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
//
//			Font titleFont = new Font(bfChinese, 16, Font.BOLD);
//			Font detailFont = new Font(bfChinese, 8, Font.BOLD);
//
//			float[] widths = { 32, 70, 20, 27 };
//			PdfPTable table = new PdfPTable(widths);
//			table.setHorizontalAlignment(PdfPTable.ALIGN_TOP);
//
//			table.setSpacingBefore(50);
//			table.setTotalWidth(213);
//			table.setLockedWidth(true);
//
//			PdfPCell cell = new PdfPCell(new Paragraph("修理编号", detailFont));
//			cell.setFixedHeight(24);
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph(CommonStringUtil.nullToAlter(mBean.getSorc_no(), " "), titleFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			cell.setColspan(3);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("型号", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph(mBean.getModel_name(), detailFont));
//			cell.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("机身号", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph(mBean.getSerial_no(), detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("等级", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph(CodeListUtils.getValue("material_level", "" + mBean.getLevel()), detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("加急", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("", detailFont));
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("顾客同意日", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph(DateUtil.toString(mBean.getAgreed_date(), DateUtil.ISO_DATE_PATTERN), detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			Integer service_repair_flg = mBean.getService_repair_flg();
//			cell = new PdfPCell(new Paragraph("备品", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph((service_repair_flg != null && service_repair_flg == 3) ? "●" : "", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("报价日", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph(DateUtil.toString(mBean.getFinish_time(), DateUtil.ISO_DATE_PATTERN), detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("普通品", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph((service_repair_flg == null) ? "●" : "", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("完成预定日", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			String sSchedulePlan = "";
//			if (mBean.getAgreed_date() != null) {
//				// 取得总组工程设定的计划产出日
//				MaterialProcessMapper dao = conn.getMapper(MaterialProcessMapper.class);
//				MaterialProcessEntity mpBean = dao.loadMaterialProcess(mBean.getMaterial_id());
//				if (mpBean != null && mpBean.getCom_plan_date() != null) {
//					sSchedulePlan = DateUtil.toString(mpBean.getCom_plan_date(), DateUtil.ISO_DATE_PATTERN);
//				}
//			}
//			cell = new PdfPCell(new Paragraph(sSchedulePlan, detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("QIS", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph((service_repair_flg != null && service_repair_flg == 2) ? "●" : "", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("维修站", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph(CodeListUtils.getValue("material_ocm", "" + mBean.getOcm()), detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("返品", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph((service_repair_flg != null && service_repair_flg == 1) ? "●" : "", detailFont));
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setVerticalAlignment(Element.ALIGN_TOP);
//			table.addCell(cell);
//
//			cell = new PdfPCell(new Paragraph("", detailFont));
//			cell.setColspan(4);
//			cell.setBorder(PdfPCell.NO_BORDER);
//			table.addCell(cell);
//
//			PdfContentByte cd = pdfWriter.getDirectContent();
//			Barcode128 code128 = new Barcode128();
//			code128.setCode(mBean.getMaterial_id());
//			Image image128 = code128.createImageWithBarcode(cd, null, null);
//			PdfPCell barcodeCell = new PdfPCell(image128);
//			barcodeCell.setColspan(4);
//			barcodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			barcodeCell.setBorder(PdfPCell.NO_BORDER);
//			table.addCell(barcodeCell);
//
//			document.add(table);
//
//		} catch (DocumentException de) {
//			log.error(de.getMessage(), de);
//			return null;
//		} catch (IOException ioe) {
//			log.error(ioe.getMessage(), ioe);
//			return null;
//		} finally {
//			document.close();
//			document = null;
//		}
//
//		// 更新维修对象
//		MaterialMapper mdao = conn.getMapper(MaterialMapper.class);
//		mBean.setTicket_flg(1);
//		mdao.updateMaterial(mBean);
//
//		return filename;
//	}

	
	/**
	 * 打印小票单张小票
	 * @param mBean
	 * @param conn
	 * @param operator 
	 * @return
	 * @throws Exception
	 */	
	public String printTicket(MaterialEntity mBean, SqlSession conn, int operator) throws Exception {

		Rectangle rect = new Rectangle(240, 184); //192
		Document document = new Document(rect, 6, 9, 10, 0);

		Date today = new Date();
		String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");
		String filename = UUID.randomUUID().toString() + ".pdf";

		try {
			PdfWriter pdfWriter = PdfWriter.getInstance(document,
					new FileOutputStream(folder + "\\" + filename));

			document.open();
			BaseFont bfChinese = BaseFont.createFont(PathConsts.BASE_PATH + "\\msyh.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//			BaseFont bfChineseSong = BaseFont.createFont("STSong-Light",
//			"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

			Font titleFont = new Font(bfChinese, 13, Font.BOLD);
			Font specialFont = new Font(bfChinese, 16, Font.BOLD);
			Font detailFont = new Font(bfChinese, 7, Font.BOLD);

			addPage(pdfWriter, document, mBean, titleFont, specialFont, detailFont, conn, operator);

		} catch (DocumentException de) {
			log.error(de.getMessage(), de);
			return null;
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			return null;
		} finally {
			document.close();
			document = null;
		}

		return filename;
	}

	/**
	 * 打印小票多张小票
	 * @param mBean
	 * @param conn
	 * @return
	 * @throws Exception
	 */	
	public String printTickets(List<MaterialEntity> mBeans, SqlSession conn) throws Exception {

		Rectangle rect = new Rectangle(240, 184); //192
		Document document = new Document(rect, 6, 9, 10, 0);

		Date today = new Date();
		String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");
		String filename = UUID.randomUUID().toString() + ".pdf";

		try {
			PdfWriter pdfWriter = PdfWriter.getInstance(document,
					new FileOutputStream(folder + "\\" + filename));
			document.open();
			BaseFont bfChinese = BaseFont.createFont(PathConsts.BASE_PATH + "\\msyh.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

			Font titleFont = new Font(bfChinese, 13, Font.BOLD);
			Font specialFont = new Font(bfChinese, 16, Font.BOLD);
			Font detailFont = new Font(bfChinese, 7, Font.BOLD);

			for (int i = 0; i < mBeans.size() - 1; i++) {
				MaterialEntity mBean = mBeans.get(i);
				addPage(pdfWriter, document, mBean, titleFont, specialFont, detailFont, conn, RvsConsts.TICKET_RECEPTOR);
				document.newPage();
			}
			MaterialEntity mBean = mBeans.get(mBeans.size() - 1);
			addPage(pdfWriter, document, mBean, titleFont, specialFont, detailFont, conn, RvsConsts.TICKET_RECEPTOR);

		} catch (DocumentException de) {
			log.error(de.getMessage(), de);
			return null;
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			return null;
		} finally {
			document.close();
			document = null;
		}

		return filename;
	}

	/**
	 * 打印小票单页
	 * @param operator 
	 * @param 
	 * @return
	 * @throws Exception
	 */	
	private void addPage(PdfWriter pdfWriter, Document document, MaterialEntity mBean, Font titleFont, Font specialFont,
			Font detailFont, SqlSession conn, int operator) throws DocumentException {
		int printTimes = mBean.getTicket_flg();
		Integer service_repair_flg = mBean.getService_repair_flg();
		if (service_repair_flg == null) service_repair_flg = 0; 
		Integer directflg = mBean.getDirect_flg();
		if (directflg == null) directflg = -1;
		// 受理在直送时打印两张
		if (RvsConsts.TICKET_RECEPTOR == operator) {
			if (directflg >= 1 || mBean.getLevel() == null) {
				if (!"07".equals(mBean.getKind())) {
					printTimes = 2;
				}
			}
		} else
		// 补打一张
		if (RvsConsts.TICKET_ADDENDA == operator) {
			printTimes =1;
		}

		String sSchedulePlan = "";
		if (mBean.getAgreed_date() != null) {
//			// 取得总组工程设定的计划产出日
//			MaterialProcessMapper dao = conn.getMapper(MaterialProcessMapper.class);
//			MaterialProcessEntity mpBean = dao.loadMaterialProcess(mBean.getMaterial_id());
//			if (mpBean != null && mpBean.getCom_plan_date() != null) {
//				sSchedulePlan = DateUtil.toString(mpBean.getCom_plan_date(), DateUtil.ISO_DATE_PATTERN);
//			}
			if (mBean.getScheduled_date() == null) {
				
				if (mBean.getLevel() == null) {
					Date dSchedulePlan = RvsUtils.switchWorkDate(mBean.getAgreed_date(), RvsConsts.TIME_LIMIT, conn);
					sSchedulePlan = DateUtil.toString(dSchedulePlan, DateUtil.ISO_DATE_PATTERN);
				} else {
					String series = mBean.getProcessing_position();
					if ("URF".equals(series)) {
						MaterialTagService tagService = new MaterialTagService();
						if (!tagService.checkTagsXorByMaterialId(mBean.getMaterial_id(), 
								MaterialTagService.TAG_CONTRACT_RELATED, MaterialTagService.TAG_SHIFT_CONTRACT_RELATED,
								conn)) {
							series = "URF-UNCONTRACT_RELATED";
						}
					}
					Date dSchedulePlan = RvsUtils.getTimeLimit(mBean.getAgreed_date(), mBean.getLevel(), 
							mBean.getFix_type(), mBean.getScheduled_expedited(), series, conn, false)[0];
					sSchedulePlan = DateUtil.toString(dSchedulePlan, DateUtil.ISO_DATE_PATTERN);
				}
			} else {
				sSchedulePlan = DateUtil.toString(mBean.getScheduled_date(), DateUtil.ISO_DATE_PATTERN);
			}
		}

		for(int i=0;i<printTimes;i++) {
			PdfPTable mainTable = new PdfPTable(1);
			mainTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			mainTable.setTotalWidth(232);
			mainTable.setLockedWidth(true);
			mainTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

			float[] topTable_widths = { 17, 43, 17, 22 };
			PdfPTable topTable = new PdfPTable(topTable_widths);
			topTable.setHorizontalAlignment(Element.ALIGN_CENTER);

			topTable.setTotalWidth(228);
			topTable.setLockedWidth(true);

			PdfPCell cell = new PdfPCell(new Paragraph("修理单号", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			String sorc_no = CommonStringUtil.nullToAlter(mBean.getSorc_no(), " ");
			if (i == 0 && RvsConsts.TICKET_RECEPTOR == operator) {
				sorc_no = "▼▼ " + sorc_no + " ▼▼";
			} else
			if (i == printTimes - 1 && RvsConsts.TICKET_RECEPTOR == operator) {
				sorc_no = "▲▲ " + sorc_no + " ▲▲";
			}
			cell = new PdfPCell(new Paragraph(sorc_no, titleFont));
			cell.setFixedHeight(20);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setColspan(3);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("型号", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			String sModelName = mBean.getModel_name();
			Chunk chModelName = new Chunk(sModelName, titleFont);
			if (sModelName.length() >= 25 && sModelName.indexOf(" ") > 0) {
				chModelName.setHorizontalScaling(0.96f-(sModelName.length() - 24)*0.05f);
			}
			cell = new PdfPCell(new Paragraph(chModelName));
			cell.setFixedHeight(22);
			cell.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setColspan(3);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("机身号", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell();
			String sSerialNo = CommonStringUtil.nullToAlter(mBean.getSerial_no(), " ");
			Chunk chSerialNo = new Chunk(sSerialNo, titleFont);
			if (sSerialNo.length() >= 12) {
				chSerialNo.setHorizontalScaling(0.93f-(sSerialNo.length() - 11)*0.05f);
			}

			cell.setPhrase(new Paragraph(chSerialNo));
			cell.setFixedHeight(18);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("等级", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(CodeListUtils.getValue("material_level", "" + mBean.getLevel()), titleFont));
			cell.setFixedHeight(18);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("维修站", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(CodeListUtils.getValue("material_ocm", "" + mBean.getOcm()), detailFont));
			cell.setFixedHeight(16);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("同意日", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(DateUtil.toString(mBean.getAgreed_date(), DateUtil.ISO_DATE_PATTERN), detailFont));
			cell.setFixedHeight(16);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("课室", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			if (MaterialTagService.getAnmlMaterials(conn).contains(mBean.getMaterial_id())) {
				cell = new PdfPCell(new Paragraph("动物实验用内镜区域", detailFont));
			} else {
				cell = new PdfPCell(new Paragraph("口１课  口２课  口３课", detailFont));
			}
			cell.setFixedHeight(16);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("报价日", detailFont));
			cell.setFixedHeight(14);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(DateUtil.toString(mBean.getFinish_time(), DateUtil.ISO_DATE_PATTERN), detailFont));
			cell.setFixedHeight(14);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("完成预定日", detailFont));
			cell.setFixedHeight(14);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(sSchedulePlan, detailFont));
			cell.setFixedHeight(14);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("发行时间", detailFont));
			cell.setFixedHeight(14);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"), detailFont));
			cell.setFixedHeight(14);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			topTable.addCell(cell);

			float[] middleTable_widths = { 9, 9, 9, 9, 10, 9, 9, 9, 9, 9 };
			PdfPTable middleTable = new PdfPTable(middleTable_widths);
			middleTable.setHorizontalAlignment(Element.ALIGN_CENTER);

			middleTable.setTotalWidth(228);
			middleTable.setLockedWidth(true);

			cell = new PdfPCell(new Paragraph("加急", detailFont));
			cell.setFixedHeight(14);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			middleTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("", detailFont));
			middleTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("备品", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			middleTable.addCell(cell);

			cell = new PdfPCell(new Paragraph((service_repair_flg == 3) ? "●" : "", specialFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingTop(-5);
			middleTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("普通品", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			middleTable.addCell(cell);

			cell = new PdfPCell(new Paragraph((service_repair_flg == 0) ? "●" : "", specialFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingTop(-5);
			middleTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("QIS", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			middleTable.addCell(cell);

			cell = new PdfPCell(new Paragraph((service_repair_flg == 2) ? "●" : "", specialFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingTop(-5);
			middleTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("返品", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			middleTable.addCell(cell);

			cell = new PdfPCell(new Paragraph((service_repair_flg == 1) ? "●" : "", specialFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingTop(-5);
			middleTable.addCell(cell);

			float[] bottomTable_widths = { 60, 20, 20 };
			PdfPTable bottomTable = new PdfPTable(bottomTable_widths);
			bottomTable.setHorizontalAlignment(Element.ALIGN_CENTER);

			bottomTable.setTotalWidth(228);
			bottomTable.setLockedWidth(true);

			PdfContentByte cd = pdfWriter.getDirectContent();
			Barcode128 code128 = new Barcode128();
			code128.setCode(mBean.getMaterial_id());
			Image image128 = code128.createImageWithBarcode(cd, null, null);
			PdfPCell barcodeCell = new PdfPCell(image128);
			barcodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			barcodeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			barcodeCell.setBorder(PdfPCell.NO_BORDER);
			barcodeCell.setRowspan(2);
			bottomTable.addCell(barcodeCell);

			cell = new PdfPCell(new Paragraph("直送", detailFont));
			cell.setFixedHeight(14);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			bottomTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("选择性报价", detailFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			bottomTable.addCell(cell);

			if (directflg == 1) {
				cell = new PdfPCell(new Paragraph("●", specialFont));
			} else if (directflg > 1) {
				cell = new PdfPCell(new Paragraph("●", specialFont)); // 快
			} else {
				cell = new PdfPCell();
			}
			cell.setFixedHeight(24);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			bottomTable.addCell(cell);

			Integer selectable = mBean.getSelectable();
			if (selectable != null && selectable == 1) {
				cell = new PdfPCell(new Paragraph("●", specialFont));
			} else {
				cell = new PdfPCell();
			} 
			cell.setFixedHeight(24);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			bottomTable.addCell(cell);

			mainTable.addCell(topTable);
			mainTable.addCell(middleTable);
			mainTable.addCell(bottomTable);

			document.add(mainTable);

			// 合同用户
			if (mBean.getContract_related() != null && mBean.getContract_related() == 1) {
				cd.rectangle(182, 155, 40, 14);
				cd.setColorFill(BaseColor.WHITE);
				cd.fillStroke();

				cd.setColorFill(BaseColor.BLACK);
				cd.beginText();
				cd.setTextMatrix(188, 160);
				cd.setFontAndSize(detailFont.getBaseFont(), detailFont.getSize());
				cd.showText("合同用户");
				cd.endText();
			}
		}
	}

	/**
	 * 受理作业报告做成
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String createAcceptanceWorkReport(SqlSession conn) throws Exception {
		// 得到作业报告记录
		AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);

		List<MaterialEntity> materials = dao.getTodayMaterialDetail();
		
		int length=materials.size();
		String path=null;
		// 建立下载文件
		if(0<=length && length<=27){
			 path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "acceptance-1.xls";
		}
		if(27<length && length<=54){
			 path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "acceptance-2.xls";
		}
		if(54<length && length<=81){
			 path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "acceptance-3.xls";
		}
		
		String cacheFilename =  "acceptance" + new Date().getTime() + ".xls";

		Date today = new Date();
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM") + "\\" + cacheFilename;
		FileUtils.copyFile(new File(path), new File(cachePath));
		
		InputStream in=null;
		OutputStream out =null;
		try{
			in=new FileInputStream(cachePath);//读取文件
			HSSFWorkbook work=new HSSFWorkbook(in);//创建Excel
			HSSFSheet sheet=work.getSheetAt(0);//取得第一个Sheet
			MaterialEntity material=null;
			if(0<=length && length<=27){
				for(int i=0;i<materials.size();i++){
					material=materials.get(i);
					int iLine = (i + ACCEPTANCE_WORK_REPORT_START_LINE);//行号索引
					setExcelCellValue(material,iLine,sheet);
				}
			}
			if(27<length && length<=54){
				for(int i=0;i<27;i++){
					material=materials.get(i);
					int iLine = (i + ACCEPTANCE_WORK_REPORT_START_LINE);//行号索引
					setExcelCellValue(material,iLine,sheet);
				}
				for(int i=27;i<materials.size();i++){
					material=materials.get(i);
					int iLine = (i + 18);//行号索引
					setExcelCellValue(material,iLine,sheet);
				}
			}
			if(54<length && length<=81){
				for(int i=0;i<27;i++){
					material=materials.get(i);
					int iLine = (i + ACCEPTANCE_WORK_REPORT_START_LINE);//行号索引
					setExcelCellValue(material,iLine,sheet);
				}
				for(int i=27;i<54;i++){
					material=materials.get(i);
					int iLine = (i + 18);//行号索引
					setExcelCellValue(material,iLine,sheet);
				}
				for(int i=54;i<materials.size();i++){
					material=materials.get(i);
					int iLine = (i + 31);//行号索引
					setExcelCellValue(material,iLine,sheet);
				}
			}
			out= new FileOutputStream(cachePath);
			work.write(out);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(in!=null){
				in.close();
			}
			if(out!=null){
				out.close();
			}
		}
		return cacheFilename;
	}
	
	/**
	 * 给Excel单元格赋值
	 * @param material 
	 * @param iLine  索引
	 * @param sheet  
	 */
	public void setExcelCellValue(MaterialEntity material,int iLine,HSSFSheet sheet){
		HSSFRow row=sheet.getRow(iLine);
		row.getCell(0).setCellValue(DateUtil.toString(material.getReception_time(), DateUtil.ISO_DATE_PATTERN));
		row.getCell(1).setCellValue(DateUtil.toString(material.getReception_time(), "HH:mm"));
		row.getCell(2).setCellValue(material.getEsas_no());
		row.getCell(3).setCellValue(material.getModel_name());
		row.getCell(4).setCellValue(material.getSerial_no());
		row.getCell(5).setCellValue(CodeListUtils.getValue("material_ocm", ""+material.getOcm()));
		row.getCell(6).setCellValue(material.getPackage_no());
		row.getCell(7).setCellValue(material.getOperator_name());
		row.getCell(8).setCellValue("");
		String direct=(material.getDirect_flg() != null && material.getDirect_flg() >= 1) ? "直送" : "";
		String flg=CodeListUtils.getValue("material_service_repair", ""+material.getService_repair_flg());
		
		String fix="";
		int type=material.getFix_type();
		if(type==1){
		}else{
			fix=CodeListUtils.getValue("material_fix_type", ""+material.getFix_type());
		}
		row.getCell(9).setCellValue(CommonStringUtil.joinBy(" ",direct,flg,fix));
	}
}

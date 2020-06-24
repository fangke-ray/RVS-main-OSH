package com.osh.rvs.service.partial;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.partial.ComponentManageForm;
import com.osh.rvs.mapper.partial.ComponentManageMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;

public class ComponentManageService {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * NS组件库存管理数据检索
	 * @param componentManageEntity
	 * @param conn
	 * @return
	 */
	public List<ComponentManageForm> searchComponentManage(ComponentManageEntity componentManageEntity, SqlSession conn) {
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		
		// NS组件状态检索条件设置（String To List）
		String steps = componentManageEntity.getSearch_step();
		List<String> searchSteps = new ArrayList<String>();
		if (steps != null) {
			String[] arrStep = steps.split(",");
			for (String step : arrStep) {
				searchSteps.add(step);
			}
			componentManageEntity.setSearch_step_list(searchSteps);
		}
		List<ComponentManageForm> resultForm = new ArrayList<ComponentManageForm>();
		List<ComponentManageEntity> resultList = dao.searchComponentManage(componentManageEntity);
		BeanUtil.copyToFormList(resultList, resultForm, null, ComponentManageForm.class);
		return resultForm;
	}
	
	/** 零件集合 **/
	public List<ComponentManageForm> getPartialAutoCompletes(String code, SqlSession conn) {
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		List<ComponentManageForm> resultForm = new ArrayList<ComponentManageForm>();
		List<ComponentManageEntity> resultList =dao.getAllPartial(code);
		BeanUtil.copyToFormList(resultList, resultForm, null, ComponentManageForm.class);
		return resultForm;
	}
	
	/**
	 * 加入消耗品库存
	 * 
	 * @param componentManageEntity
	 * @param conn
	 * @return 如果存在返回消耗品详细信息
	 */
//	public void insert(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
//			throws Exception {
//		ComponentManageEntity insertBean = new ComponentManageEntity();
//		BeanUtil.copyToBean(form, insertBean, null);
//
//		/* Consumable_manage表插入数据 */
//		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
//		dao.insertConsumable(insertBean);
//
//
//	}
//	


	/**
	 * 打印NS组件标识
	 * @param model_name 型号
	 * @param partial_code 组件代号
	 * @param serial_no 序列号
	 * @return 临时文件名
	 * @throws Exception
	 */	
	public String printNsComponentIdTag(String model_name, String partial_code,
			String serial_no) throws Exception {

		Rectangle rect = new Rectangle(85, 42); // 192
		Document document = new Document(rect, 0, 0, 0, 0);

		Date today = new Date();
		String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");
		String filename = document.getId().toString() + ".pdf";

		try {
			PdfWriter pdfWriter = PdfWriter.getInstance(document,
					new FileOutputStream(folder + "\\" + filename));

			document.open();

			PdfContentByte cb = pdfWriter.getDirectContent();
			cb.setLineWidth(1f);
			cb.setLineDash(1f, 1f, 0f);
			cb.moveTo(0, rect.getHeight());
			cb.lineTo(rect.getWidth(), rect.getHeight());
			cb.lineTo(rect.getWidth(), 0);
			cb.lineTo(0, 0);
			cb.closePath();
			cb.stroke();

			BaseFont bfChinese = BaseFont.createFont(PathConsts.BASE_PATH + "\\msyh.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

			Font boldFont = new Font(bfChinese, 10, Font.BOLD);
			cb.setLineDash(5f);
			Font slimFont = new Font(bfChinese, 7, Font.NORMAL);

			PdfPTable mainTable = new PdfPTable(1);
			mainTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			mainTable.setTotalWidth(rect.getWidth());
			mainTable.setLockedWidth(true);

			PdfPCell cell = null;

			// 型号
			Chunk chModelName = new Chunk(model_name, boldFont);
			int modelNameWidth = model_name.getBytes().length;
			if (modelNameWidth >= 13) {
				chModelName.setHorizontalScaling(1f - (modelNameWidth - 12) * 0.05f);
			}

			cell = new PdfPCell(new Paragraph(chModelName));
			cell.setFixedHeight(14.5f);
			cell.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(PdfPCell.NO_BORDER);
			mainTable.addCell(cell);

			// 组件代号
			cell = new PdfPCell(new Paragraph(partial_code, slimFont));
			cell.setFixedHeight(11);
			cell.setPadding(0);
			cell.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(PdfPCell.NO_BORDER);
			mainTable.addCell(cell);

			// 序列号
			cell = new PdfPCell(new Paragraph(serial_no, boldFont));
			cell.setFixedHeight(15f);
			cell.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(PdfPCell.NO_BORDER);
			mainTable.addCell(cell);

			document.add(mainTable);

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
}

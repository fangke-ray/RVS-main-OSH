package com.osh.rvs.service.partial;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.partial.ComponentManageForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.inline.SoloProductionFeatureMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.master.ProcessAssignMapper;
import com.osh.rvs.mapper.partial.ComponentManageMapper;
import com.osh.rvs.service.PostMessageService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;

public class ComponentManageService {
	
	public static final String POSITION_ID_28 = "28";
	
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
	 * 虚拟单号订购子零件
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void insertVirtualManage(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ComponentManageEntity insertBean = new ComponentManageEntity();
		BeanUtil.copyToBean(form, insertBean, null);

		insertBean.setOrigin_material_id("0");
		insertBean.setStep("0");
		/* Consumable_manage表插入数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.insert(insertBean);
	}
	
	/**
	 * 订购子零件
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void insert(ActionForm form, HttpSession session, SqlSessionManager conn)
			throws Exception {
		ComponentManageEntity insertBean = new ComponentManageEntity();
		BeanUtil.copyToBean(form, insertBean, null);

		/* Consumable_manage表插入数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.insert(insertBean);
	}
	
	/**
	 * 子零件入库处理
	 * 
	 * @param updateBean
	 * @param conn
	 */
	public void partialInstock(ComponentManageEntity updateBean, HttpSession session, SqlSessionManager conn)
			throws Exception {

		/* Consumable_manage表更新数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.partialInstock(updateBean);
	}	
	
	/**
	 * 子零件出库处理
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void partialOutstock(ComponentManageEntity updateBean, HttpSession session, SqlSessionManager conn)
			throws Exception {
		
		// 子零件状态
		updateBean.setStep("2");
		/* Consumable_manage表更新数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.partialOutstock(updateBean);
	}	
	
	/**
	 * 序列号生成
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public String getNewSerialNo(String identifyCode, SqlSessionManager conn)
			throws Exception {
		// 第一部分6位 为投入日期(current_date)的yyMMdd格式，如200619
		String serialNo1 = DateUtil.toString(new Date(), "yyMMdd");
		
		/* Consumable_manage表更新数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		String serialNo3 = dao.getNewSerialNo(identifyCode);
		
		// 序列号为3部分
		String maxSerialNo = serialNo1.concat(identifyCode).concat(serialNo3);
		
		return maxSerialNo;
	}
	
	/**
	 * 在（SOLO_PRODUCTION_FEATURE）表新建记录
	 * @param productionFeatureEntity
	 * @param conn
	 * @throws Exception
	 */
	public void insertToSoloProductionFeature(SoloProductionFeatureEntity productionFeatureEntity,
			SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		// section_id
		productionFeatureEntity.setSection_id("1");
		// judge_date
		productionFeatureEntity.setJudge_date(null);
		// #{pace}
		productionFeatureEntity.setPace(0);
		// operator_id
		productionFeatureEntity.setOperator_id("0");
		// operate_result
		productionFeatureEntity.setOperate_result(0);

		mapper.insert(productionFeatureEntity);
	}	
	
	/**
	 * 在（POST_MESSAGE）表新建记录
	 * @param resultMsg
	 * @param conn
	 * @throws Exception
	 */
	public void insertToPOST(String resultMsg, List<String> positionIds, 
			HttpSession session, SqlSessionManager conn) throws Exception {
		LoginData user = (LoginData)session.getAttribute(RvsConsts.SESSION_USER);
		
		PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
		
		PostMessageEntity pmEntity = new PostMessageEntity();
		pmEntity.setLevel(1);
		pmEntity.setReason(PostMessageService.NS_COMPONENT_MANAGE);
		pmEntity.setSender_id(user.getOperator_id());
		pmEntity.setContent(resultMsg);
		
		// （POST_MESSAGE）表新建记录
		pmMapper.createPostMessage(pmEntity);
		
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String lastInsertID = commonMapper.getLastInsertID();
		pmEntity.setPost_message_id(lastInsertID);

		// 查询系统管理员
		OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

		// 工位关注者
		List<String> operatorId0 = oMapper.getOperatorByPosition(positionIds);
		for (String operatorId : operatorId0) {
			pmEntity.setReceiver_id(operatorId);
			pmMapper.createPostMessageSendation(pmEntity);
		}

		// NS 线长
		List<String> operatorId2 = oMapper.getOperatorByRoleAndLine("5", "13");
		for (String operatorId : operatorId2) {
			pmEntity.setReceiver_id(operatorId);
			pmMapper.createPostMessageSendation(pmEntity);
		}
		
	}
	
	
	/**
	 * 序列号生成
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public ComponentManageEntity searchComponentManageDetail(String componentKey, SqlSessionManager conn)
			throws Exception {

		/* Consumable_manage表详细数据取得  */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		ComponentManageEntity detail = dao.searchComponentManageDetail(componentKey);

		return detail;
	}
	
	/**
	 * 判定指定作业是否正在进行中
	 * @param conn
	 * @return String
	 */
	public String getPosition(SoloProductionFeatureEntity soloEntity, SqlSessionManager conn) 
			throws Exception {

		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		List<SoloProductionFeatureEntity> allSole = dao.findPositionByModelAndSerialNo(soloEntity);
		
		if (allSole == null || allSole.size() == 0) {
			return null;
		} else {
			return allSole.get(0).getPosition_id();
		}
	}
	
	/**
	 * 独立工位操作记录删除
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void deleteSoloProductionFeature(SoloProductionFeatureEntity soloEntity, SqlSessionManager conn)
			throws Exception {

		/* 独立工位操作记录删除  */
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		dao.deleteSoloByModelAndSerialNo(soloEntity);
	}	
	
	/**
	 * 组件废弃处理
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void cancleManage(ComponentManageEntity componentManageEntity, SqlSessionManager conn)
			throws Exception {

		/* 组件废弃处理  */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.cancleManage(componentManageEntity);
	}
	
	/**
	 * 取得当前仓库所有库存编号
	 * @param conn
	 * @return List<String>
	 */
	public List<String> getNSStock(SqlSession conn) {
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		return dao.getNSStock();
	}
	
	/**
	 * DerivedId取得
	 * @param conn
	 * @return String
	 */
	public String getDerivedId(String model_id, SqlSession conn) {
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		return dao.getDerivedIdByModel(model_id, "5");
	}
	
	/**
	 * position_id取得
	 * @param conn
	 * @return List<String>
	 */
	public List<String> getPositionIds(String derivedId, SqlSession conn) {
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		return dao.getFirstPosition(derivedId, "9000000");
	}
	
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

	
	/**
	 * 打印小票单张小票(NS组件信息单)
	 * @param mBean
	 * @param conn
	 * @param operator 
	 * @return
	 * @throws Exception
	 */	
	public String printNSInfoTicket(ComponentManageEntity component, SqlSession conn) throws Exception {

        Rectangle pageSize = new Rectangle(PageSize.A5.getHeight(), PageSize.A5.getWidth());
        pageSize.rotate();
		Document document = new Document(pageSize, 6, 9, 10, 0);

		Date today = new Date();
		String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");
		String filename = UUID.randomUUID().toString() + ".pdf";

		try {
			PdfWriter pdfWriter = PdfWriter.getInstance(document,
					new FileOutputStream(folder + "\\" + filename));

			document.open();
			BaseFont bfChinese = BaseFont.createFont(PathConsts.BASE_PATH + "\\msyh.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

			Font titleFont = new Font(bfChinese, 22, Font.BOLD);

			float width[] = {0.3f, 0.7f};;//设置每列宽度比例   
			PdfPTable mainTable = new PdfPTable(width);
			mainTable.setWidthPercentage(100);
			mainTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			mainTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

			PdfPCell cell = new PdfPCell(new Paragraph("维修单", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);

			// 这列是空白的
			cell = new PdfPCell(new Paragraph("", titleFont));
			cell.setFixedHeight(78);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("组件的条形码", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);

			PdfContentByte cd = pdfWriter.getDirectContent();
			Barcode128 code128 = new Barcode128();
			code128.setCode(component.getSerial_no());
			code128.setBarHeight(70);
			code128.setSize(22);
			code128.setFont(null);
			Image image128 = code128.createImageWithBarcode(cd, null, null);
			cell = new PdfPCell(image128);
			cell.setFixedHeight(78);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("机 型 号", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);

			Chunk chModelName = new Chunk(component.getModel_name(), titleFont);
			int modelNameWidth = component.getModel_name().getBytes().length;
			if (modelNameWidth >= 13) {
				chModelName.setHorizontalScaling(1f - (modelNameWidth - 12) * 0.05f);
			}
			cell = new PdfPCell(new Paragraph(chModelName));
			cell.setFixedHeight(78);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("组件零件号", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);

			String partial_code = CommonStringUtil.nullToAlter(component.getPartial_code(), " ");
			cell = new PdfPCell(new Paragraph(partial_code, titleFont));
			cell.setFixedHeight(78);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("组件序列号", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(component.getSerial_no(), titleFont));
			cell.setFixedHeight(78);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
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

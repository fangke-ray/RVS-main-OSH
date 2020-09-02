package com.osh.rvs.service.partial;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.bean.partial.ComponentSettingEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.ComponentManageForm;
import com.osh.rvs.form.partial.ComponentSettingForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.inline.SoloProductionFeatureMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.partial.ComponentManageMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.service.PostMessageService;
import com.osh.rvs.service.ProcessAssignService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

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
	public void insert(ComponentManageEntity insertBean, SqlSessionManager conn)
			throws Exception {
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
	public void insertToSoloProductionFeature(List<String> positionIds, String model_id,
			String model_name, String getNewSerialNo, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);

		for (String positionId : positionIds) {
			SoloProductionFeatureEntity productionFeatureEntity = new SoloProductionFeatureEntity();
			productionFeatureEntity.setPosition_id(positionId);
			productionFeatureEntity.setModel_id(model_id);
			productionFeatureEntity.setModel_name(model_name);
			productionFeatureEntity.setSerial_no(getNewSerialNo);

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
	 * 移库
	 * @param componentBean
	 * @param conn
	 */
	public void moveStock(ComponentManageEntity componentBean,
			SqlSessionManager conn) {
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.update(componentBean);
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
	 * 取得已经分配给目标维修品的组建序列号
	 * @param targetMaterialId
	 * @param conn
	 * @return
	 */
	public String getSerialNosForTargetMaterial(String targetMaterialId, SqlSession conn) {
		ComponentManageMapper mapper = conn.getMapper(ComponentManageMapper.class);
		return mapper.getComponentByTargetMaterial(targetMaterialId);
	}

	/**
	 * 出库检查
	 * @param form
	 * @param conn
	 * @param errors
	 */
	public Map<String, String> validateOutstock(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		Map<String, String> collectDic = new HashMap<String, String>();

		/* 查询 */
		ComponentSettingService settingService = new ComponentSettingService();
		ComponentSettingEntity settingEntity = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, settingEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		List<ComponentSettingForm> list = settingService.searchComponentSettingDetail(settingEntity, conn);
		// 型号组件设置判定
		if (list.size() == 0) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg("该型号组件设置已被删除。无法进行出库。");
			errors.add(msg);
		} else {
			collectDic.put("identify_code", list.get(0).getIdentify_code());
			collectDic.put("model_id", list.get(0).getModel_id());
			collectDic.put("model_name", list.get(0).getModel_name());
		}

		// NS 组件流程取得
		ProcessAssignService paService = new ProcessAssignService();
		String derivedId = paService.getDerivedId(settingEntity.getModel_id(), "5", true, conn);
		if (derivedId == null || derivedId.length() == 0) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg("此型号的NS 组件流程不存在。无法进行出库。");
			errors.add(msg);
		} else {
			collectDic.put("pat_id", derivedId);
		}
		return collectDic;
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
			cb.setLineWidth(0.5f);
			cb.setLineDash(1f, 1f, 0f);
			cb.moveTo(0, rect.getHeight()-1f);
			cb.lineTo(rect.getWidth(), rect.getHeight()-1f);
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

			Font titleFont = new Font(bfChinese, 20, Font.BOLD);
			Font titleFont11 = new Font(bfChinese, 11, Font.NORMAL);
			Font titleFont18 = new Font(bfChinese, 18, Font.BOLD);
			Font titleFont14 = new Font(bfChinese, 14, Font.BOLD);
			Font titleFont12 = new Font(bfChinese, 12, Font.BOLD);

			float width[] = {0.1f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f};;//设置每列宽度比例   
			PdfPTable mainTable = new PdfPTable(width);
			mainTable.setWidthPercentage(100);
			mainTable.setHorizontalAlignment(Element.ALIGN_CENTER);

			// 第一行 信息单号
			PdfPCell cell = new PdfPCell(new Paragraph("QR-B31002-150", titleFont11));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(16);
			cell.setColspan(6);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
			// 第二行 组装组件信息单
			cell = new PdfPCell(new Paragraph("组装组件信息单", titleFont18));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(30);
			cell.setColspan(6);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
			// 第三行 空白
			cell = new PdfPCell(new Paragraph("", titleFont18));
			cell.setFixedHeight(16); // 20-8
			cell.setColspan(6);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
			float width2[] = {0.41f, 0.59f};;//设置每列宽度比例   
			PdfPTable middleTable = new PdfPTable(width2);
			middleTable.setWidthPercentage(100);
			middleTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			middleTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

			// 4-2
			cell = new PdfPCell(new Paragraph("维修单", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			// 4-3
			cell = new PdfPCell(new Paragraph("", titleFont));
			cell.setFixedHeight(40);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			// 5-2
			cell = new PdfPCell(new Paragraph("组件的条形码", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			// 5-3
			PdfContentByte cd = pdfWriter.getDirectContent();
			Barcode128 code128 = new Barcode128();
			code128.setCode(component.getSerial_no());
			code128.setBarHeight(32);
			code128.setSize(20);
			code128.setFont(null);
			Image image128 = code128.createImageWithBarcode(cd, null, null);
			cell = new PdfPCell(image128);
			cell.setFixedHeight(40);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			// 6-2
			cell = new PdfPCell(new Paragraph("机 型 号", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			// 6-3
			Chunk chModelName = new Chunk(component.getModel_name(), titleFont);
//			int modelNameWidth = component.getModel_name().getBytes().length;
//			if (modelNameWidth >= 13) {
//				chModelName.setHorizontalScaling(1f - (modelNameWidth - 12) * 0.05f);
//			}
			cell = new PdfPCell(new Paragraph(chModelName));
			cell.setFixedHeight(40);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			// 7-2
			cell = new PdfPCell(new Paragraph("组件零件号", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			// 7-3
			String partial_code = CommonStringUtil.nullToAlter(component.getPartial_code(), " ");
			cell = new PdfPCell(new Paragraph(partial_code, titleFont));
			cell.setFixedHeight(40);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			// 8-2
			cell = new PdfPCell(new Paragraph("组件序列号", titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			// 8-3
			cell = new PdfPCell(new Paragraph(component.getSerial_no(), titleFont));
			cell.setFixedHeight(40);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			middleTable.addCell(cell);

			
			// 空白
			cell = new PdfPCell(new Paragraph("", titleFont18));
			cell.setBorder(0);
			mainTable.addCell(cell);
			
			// 组装组件信息单表
			cell = new PdfPCell(new Paragraph("", titleFont));
			cell.setColspan(5);
			cell.addElement(middleTable);
			mainTable.addCell(cell);
			
			// 第9行 空白
			cell = new PdfPCell(new Paragraph("", titleFont));
			cell.setFixedHeight(22); // 30 - 8
			cell.setColspan(6);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
			// 10-1
			cell = new PdfPCell(new Paragraph("确认以上信息和实物信息一致。", titleFont12));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(40);
			cell.setColspan(3);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
			// 10-2
			cell = new PdfPCell(new Paragraph("信息确认人", titleFont14));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);
			
			// 10-3
			cell = new PdfPCell(new Paragraph("", titleFont14));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setColspan(2);
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
	 * 取得符合的维修品（在线的，同型号S1级别）
	 * @param model_id
	 * @param conn
	 * @return
	 */
	public List<MaterialForm> getTargetMaterials(ComponentManageEntity cond,SqlSession conn) {
		ComponentManageMapper mapper = conn.getMapper(ComponentManageMapper.class);

		List<MaterialEntity> targetMaterialsEntity = mapper.getTargetMaterials(cond);

		List<MaterialForm> targetMaterials = new ArrayList<MaterialForm>();
		for (MaterialEntity targetMaterial : targetMaterialsEntity) {
			MaterialForm form = new MaterialForm();
			BeanUtil.copyToForm(targetMaterial, form, CopyOptions.COPYOPTIONS_NOEMPTY);
			int package_count = 0;
			String packageNo = targetMaterial.getPackage_no();
			if (!CommonStringUtil.isEmpty(packageNo)) {
				package_count = packageNo.split(",").length;
			}
			form.setSelectable("" + package_count);

			targetMaterials.add(form);
		}
		return targetMaterials;
	}

	/**
	 * 组件入库
	 * 
	 * @param componentBean
	 * @param conn
	 */
	public void componentInstock(ComponentManageEntity updateBean,
			SqlSessionManager conn) {
		ComponentManageMapper mapper = conn.getMapper(ComponentManageMapper.class);
		// 组件入库更新
		/* Consumable_manage表更新数据 */
		mapper.componentInstock(updateBean);

	}

	/**
	 * 组件出库
	 * 
	 * @param componentBean
	 * @param conn
	 * @return 是否还要做发放
	 * @throws Exception 
	 * 
	 */
	public String componentOutstock(ComponentManageEntity updateBean,
			SqlSessionManager conn) throws Exception {
		ComponentManageMapper mapper = conn.getMapper(ComponentManageMapper.class);
		// 组件出库更新
		/* Consumable_manage表更新数据 */
		mapper.componentOutstock(updateBean);

		// 零件订单更新
		MaterialPartialMapper mpMapper = conn.getMapper(MaterialPartialMapper.class);
		boolean hit = false;
		List<MaterialPartialDetailEntity> mpdEntities = mpMapper.getMpdForComponent(updateBean.getTarget_material_id());
		for (MaterialPartialDetailEntity mpdEntity : mpdEntities) {
			// 发放时间
			if (mpdEntity.getRecent_signin_time() == null) {
				hit = true;
				// 发放
				mpMapper.updateComponentRelease(mpdEntity.getMaterial_partial_detail_key());
				break;
			}
		}
		if (!hit) return "还未为此维修设定需要发放组装组件，并收取订单中的子零件。";

		return null;
	}

	/**
	 * 检查提交的机身号(也可以是库位号)是否存在
	 * 
	 * @param serial_no
	 * @param errors 
	 * @param conn
	 * @return
	 */
	public ComponentManageEntity checkSerialNo(String serial_no, List<String> errors, SqlSession conn) {
		if (serial_no == null 
				|| (serial_no.length() != 13 && serial_no.length() != 8)) {
			errors.add(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.singledetail", "组件序列号或库位号"));
			return null;
		}
		if (serial_no.length() == 8) {
			return checkStockCode(serial_no, "3", errors, conn);
		}

		List<ComponentManageEntity> results = getBySerialNo(serial_no, conn);
		if (results.size() == 0) {
			errors.add(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "组装完成组件"));
			return null;
		}
		if (!results.get(0).getStep().equals("3")) {
			errors.add(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.component.stepMismatch", "组装完成"));
			return null;
		}

		return results.get(0);
	}

	public List<ComponentManageEntity> getBySerialNo(String serial_no, SqlSession conn) {
		ComponentManageMapper mapper = conn.getMapper(ComponentManageMapper.class);
		ComponentManageEntity cond = new ComponentManageEntity();
		cond.setSerial_no(serial_no);
		return mapper.searchComponentManageEntity(cond);
	}

	public ComponentManageEntity checkStockCode(String stock_code, String step, List<String> errors, SqlSession conn) {
		if (stock_code == null || stock_code.length() != 8) {
			errors.add(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidCode", "库位号"));
			return null;
		}

		ComponentManageMapper mapper = conn.getMapper(ComponentManageMapper.class);
		ComponentManageEntity cond = new ComponentManageEntity();
		cond.setStock_code(stock_code);
		List<ComponentManageEntity> results = mapper.searchComponentManageEntity(cond);

		// 取得组装完成品时
		if ("3".equals(step)) {
			if (results.size() == 0) {
				errors.add(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "组装完成组件"));
				return null;
			}
			if (!results.get(0).getStep().equals("3")) {
				errors.add(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.component.stepMismatch", "组装完成"));
				return null;
			}
		} else if ("0".equals(step)) {
			// 入库时库位不能占用
			if (results.size() > 0) {
				errors.add(ApplicationMessage.WARNING_MESSAGES.getMessage("info.wipShelf.notEmpty"));
				return null;
			}
		}

		return (results.size() > 0 ? results.get(0) : null);
	}

}

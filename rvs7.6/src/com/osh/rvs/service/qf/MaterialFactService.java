package com.osh.rvs.service.qf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.MaterialFactEntity;
import com.osh.rvs.bean.inline.MaterialProcessEntity;
import com.osh.rvs.bean.master.LineEntity;
import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.ProcessAssignTemplateEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.form.qf.MaterialFactForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.inline.MaterialProcessAssignMapper;
import com.osh.rvs.mapper.inline.MaterialProcessMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.master.PositionMapper;
import com.osh.rvs.mapper.master.ProcessAssignMapper;
import com.osh.rvs.mapper.qf.MaterialFactMapper;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.MaterialPartialService;
import com.osh.rvs.service.MaterialProcessAssignService;
import com.osh.rvs.service.MaterialProcessService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.ProcessAssignService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.inline.ForSolutionAreaService;
import com.osh.rvs.service.inline.PositionPanelService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class MaterialFactService {

	private static Set<String> CCD_MODEL_NAMES = null;
	private Logger log = Logger.getLogger(getClass());

	private static String SECTION_1 = "00000000001";
	private static String SECTION_2 = "00000000003";
	private static String SECTION_3 = "00000000012";

	public List<MaterialFactForm> searchMaterial(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		MaterialFactEntity conditionBean = new MaterialFactEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		if (CCD_MODEL_NAMES == null) {
			log.info("GET CCD Lists");
			getCcdModels();
		}

		// 从数据库中查询记录
		MaterialFactMapper dao = conn.getMapper(MaterialFactMapper.class);
		List<MaterialFactEntity> lResultBean = dao.searchMaterial(conditionBean);

		// 建立页面返回表单
		List<MaterialFactForm> lResultForm = new ArrayList<MaterialFactForm>();
		MaterialPartialService mps = new MaterialPartialService();

		List<String> anmlPats = ProcessAssignService.getAnmlProcesses(conn);

		// 数据对象复制到表单
		for (MaterialFactEntity resultBean : lResultBean) {
			String section_id = SECTION_1;

			MaterialFactForm resultForm = new MaterialFactForm();
			BeanUtil.copyToForm(resultBean, resultForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			if (CCD_MODEL_NAMES.contains(resultForm.getModel_name())) {
				resultForm.setCcd_model("1");
			}

			if ("9".equals(resultBean.getCcd_operate_result())) {
				resultForm.setCcd_operate_result("已指定");
			} else {
				resultForm.setCcd_operate_result("");
			}

			Integer level = resultBean.getLevel();
			boolean isLightFix = RvsUtils.isLightFix(level);
			boolean isPeripheral = RvsUtils.isPeripheral(level);

			if (isLightFix) {
				section_id = SECTION_2;
			} else if (isPeripheral) {
				section_id = SECTION_3;
			} else if (resultBean.getCategory_kind() == 3
					|| resultBean.getCategory_kind() == 6){
				section_id = SECTION_2;
			} else if (resultBean.getCategory_kind() == 7){
				section_id = SECTION_3;
			}

			if (isLightFix || isPeripheral
					|| "光学视管".equals(resultBean.getCategory_name())) {
				MaterialPartialForm mp = mps.loadMaterialPartial(conn, resultBean.getMaterial_id(), null);
				if (mp == null) {
					resultForm.setImg_operate_result(getBr(resultForm.getImg_operate_result(), "未订购零件"));
				} else if ("0".equals(mp.getBo_flg()) || "2".equals(mp.getBo_flg())){
					resultForm.setImg_operate_result(getBr(resultForm.getImg_operate_result(), "已订购零件"));
					resultForm.setCcd_operate_result(getBr(resultForm.getCcd_operate_result(), "零件到达"));
				} else {
					resultForm.setImg_operate_result(getBr(resultForm.getImg_operate_result(), "已订购零件"));
					resultForm.setCcd_operate_result(getBr(resultForm.getCcd_operate_result(), "缺零件"));
				}
			}

			// 动物内镜强制流程
			if (MaterialTagService.getAnmlMaterials(conn).contains(resultBean.getMaterial_id())) {
				resultForm.setPat_id(anmlPats.get(0));
				// section_id = SECTION_2;
			}

			// 投入课室
			resultForm.setSection_id(section_id);

			lResultForm.add(resultForm);
		}

		return lResultForm;
	}

	public List<MaterialFactForm> searchInlineMaterial(SqlSession conn) {

		MaterialFactMapper dao = conn.getMapper(MaterialFactMapper.class);
		List<MaterialFactEntity> lResultBean = dao.searchInlineMaterial();

		List<MaterialFactForm> lResultForm = new ArrayList<MaterialFactForm>();

		if (CCD_MODEL_NAMES == null) {
			log.info("GET CCD Lists");
			getCcdModels();
		}

		// 数据对象复制到表单
		for (MaterialFactEntity resultBean : lResultBean) {
			MaterialFactForm resultForm = new MaterialFactForm();
			BeanUtil.copyToForm(resultBean, resultForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			if (CCD_MODEL_NAMES.contains(resultForm.getModel_name())) {
				resultForm.setCcd_model("1");
			}
			lResultForm.add(resultForm);
		}

		return lResultForm;
	}

	public String createReport(SqlSession conn) throws Exception {
		// 模板路径
		
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "今日投线一览.xls";
		String cacheName = "今日投线一览" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" + cacheName;
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		SimpleDateFormat inlineTimeFormat=new SimpleDateFormat("HH:mm");
		SimpleDateFormat agreeDateFormat=new SimpleDateFormat("MM-dd ");
		MaterialFactMapper dao = conn.getMapper(MaterialFactMapper.class);
		List<MaterialFactEntity> lResultBean = dao.searchInlineMaterial();
		
		OutputStream out = null;
		InputStream in = null;
		try {
			in = new FileInputStream(cachePath);
			HSSFWorkbook work = new HSSFWorkbook(in);
			HSSFSheet sheet = work.getSheetAt(0);	
			
			/*设置单元格内容居中显示*/
			HSSFCellStyle styleAlignCenter = work.createCellStyle();
			styleAlignCenter.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			styleAlignCenter.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			styleAlignCenter.setBorderRight(HSSFCellStyle.BORDER_THIN); 
			styleAlignCenter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleAlignCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			styleAlignCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			styleAlignCenter.setWrapText(true);
			
			/*设置单元格内容居左显示*/
			HSSFCellStyle styleAlignLeft = work.createCellStyle();
			styleAlignLeft.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			styleAlignLeft.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			styleAlignLeft.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleAlignLeft.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleAlignLeft.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			styleAlignLeft.setWrapText(true); 
			
			int index = 0;
			for (int i = 0; i < lResultBean.size(); i++) {					
				index++;
				MaterialFactEntity materialFactEntity = lResultBean.get(i);
				HSSFRow row = sheet.createRow(index);	
				
				//序列号
				HSSFCell indexCell = row.createCell(0);
				indexCell.setCellValue(index);
				indexCell.setCellStyle(styleAlignCenter);
                
				//投线时间
				HSSFCell inlineTimeCell= row.createCell(1);
				inlineTimeCell.setCellValue(inlineTimeFormat.format(materialFactEntity.getInline_time()));
				inlineTimeCell.setCellStyle(styleAlignCenter);
				
				//SORC NO.
				HSSFCell sorcNoCell = 	row.createCell(2);
				sorcNoCell.setCellValue(materialFactEntity.getSorc_no());
				sorcNoCell.setCellStyle(styleAlignLeft);
				
				//ESAS NO.
				HSSFCell esasNoCell = row.createCell(3);
				esasNoCell.setCellValue(materialFactEntity.getEsas_no());
				esasNoCell.setCellStyle(styleAlignLeft);
				
				//型号
				HSSFCell modelNameCell = row.createCell(4);
				modelNameCell.setCellValue(materialFactEntity.getModel_name());
				modelNameCell.setCellStyle(styleAlignLeft);
				
				//机身号
				HSSFCell serialNoCell = row.createCell(5);
				serialNoCell.setCellValue(materialFactEntity.getSerial_no());
				serialNoCell.setCellStyle(styleAlignLeft);
				
				//等级
				HSSFCell levelCell = row.createCell(6);
				/*if(materialFactEntity.getLevel()==1){
					levelCell.setCellValue("S1");
				}else if(materialFactEntity.getLevel()==2){
					levelCell.setCellValue("S2");
				}else if(materialFactEntity.getLevel()==3){
					levelCell.setCellValue("S3");
				}else if(materialFactEntity.getLevel()==6){
					levelCell.setCellValue("A");
				}else if(materialFactEntity.getLevel()==7){
					levelCell.setCellValue("B");
				}else if(materialFactEntity.getLevel()==8){
					levelCell.setCellValue("C");
				}else if(materialFactEntity.getLevel()==9){
					levelCell.setCellValue("D");
				}*/
				Integer level = materialFactEntity.getLevel();
				String levelData = CodeListUtils.getValue("material_level",level.toString());
				levelCell.setCellValue(levelData);
				levelCell.setCellStyle(styleAlignCenter);
				
				//投入科室
				HSSFCell sectionNameCell = row.createCell(7);
				sectionNameCell.setCellValue(materialFactEntity.getSection_name());
				sectionNameCell.setCellStyle(styleAlignCenter);
				
				//客户同意日
				HSSFCell agreedDateCell = row.createCell(8);
				agreedDateCell.setCellValue(agreeDateFormat.format(materialFactEntity.getAgreed_date()));
				agreedDateCell.setCellStyle(styleAlignCenter);
				
				//2日内投线
				HSSFCell twoDaysOfLinesCell = row.createCell(9);
				if ("0".equals(dao.getTwoDaysOfLines(materialFactEntity.getMaterial_id()))) {
					twoDaysOfLinesCell.setCellValue("╳");
				} else{
					twoDaysOfLinesCell.setCellValue("√");
				}		
				twoDaysOfLinesCell.setCellStyle(styleAlignCenter);
			}
			out = new FileOutputStream(cachePath);
			try {
				work.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		return cacheName;
	}

	private void getCcdModels() {
		String sSnoutModels = PathConsts.POSITION_SETTINGS.getProperty("ccd.models");

		CCD_MODEL_NAMES = new LinkedHashSet<String>();
		if (sSnoutModels != null) {
			String[] model_names = sSnoutModels.split(",");

			for (String model_name : model_names) {
				CCD_MODEL_NAMES.add(model_name);
			}
		}
	}

	public void updateAgreedDate(ActionForm form, SqlSession conn) {
		// 表单复制到数据对象
		MaterialFactEntity entity = new MaterialFactEntity();
		BeanUtil.copyToBean(form, entity, null);

		MaterialFactMapper dao = conn.getMapper(MaterialFactMapper.class);
		dao.updateAgreedDate(entity);

		// FSE 数据同步
		try{
			FseBridgeUtil.toUpdateMaterialProcess(entity.getMaterial_id(), "agree");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateAgreedDateBySorc(String sorc_no, Date agreed_date, SqlSession conn) {
		// 表单复制到数据对象
		MaterialFactEntity entity = new MaterialFactEntity();
		entity.setSorc_no(sorc_no);
		entity.setAgreed_date(agreed_date);

		MaterialFactMapper dao = conn.getMapper(MaterialFactMapper.class);
		dao.updateAgreedDateBySorc(entity);
		// if (ret > 0) {
		// System.out.println(sorc_no);
		// }
	}

	public void updateUnrepairBySorc(String sorc_no, Date dAppove, SqlSession conn) {
		// 表单复制到数据对象
		MaterialFactEntity entity = new MaterialFactEntity();
		entity.setSorc_no(sorc_no);
		entity.setAgreed_date(dAppove);

		MaterialFactMapper dao = conn.getMapper(MaterialFactMapper.class);
		dao.updateUnrepairBySorc(entity);
	}

	public void updateInline(ActionForm form, String operator_id, SqlSessionManager conn) throws Exception {
		// 表单复制到数据对象
		MaterialFactEntity entity = new MaterialFactEntity();
		BeanUtil.copyToBean(form, entity, null);
		ArrayList<String> triggerList = new ArrayList<String>();
		updateInline(entity, conn, triggerList);

		// FSE 数据同步
		try{
			conn.commit();
			FseBridgeUtil.toUpdateMaterial(entity.getMaterial_id(), "200inline");
			FseBridgeUtil.toUpdateMaterialProcess(entity.getMaterial_id(), "200inline");

			RvsUtils.sendTrigger(triggerList);

			// 刷新间接人员作业记录
			AcceptFactService afService = new AcceptFactService();
			afService.fingerOperatorRefresh(operator_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateInline(MaterialFactEntity entity, SqlSessionManager conn, List<String> triggerList) throws Exception {

		MaterialProcessService mpService = new MaterialProcessService();

		Integer level = entity.getLevel();
//		boolean lightFix = (level == 9 || level == 91 || level == 92 || level == 93);
		boolean lightFix = RvsUtils.isLightFix(level);
		boolean peripheral = (level == 56 || level == 57 || level == 58 || level == 59);
		boolean isE2 = (level == 57);
		lightFix = lightFix && (entity.getFix_type() == 1);

		Date agreedDate = entity.getAgreed_date();

		ModelService mdlService = new ModelService();
		ModelEntity model = mdlService.getDetailEntity(entity.getModel_id(), conn);
		Date[] workDates = RvsUtils.getTimeLimit(agreedDate, entity.getLevel(), 
				entity.getFix_type(), entity.getScheduled_expedited(), model.getSeries(), conn, true);

		Date workDate = workDates[0];
		entity.setScheduled_date(workDate);

		// 更新维修对象的投线信息
		MaterialFactMapper dao = conn.getMapper(MaterialFactMapper.class);
		if (2 == entity.getFix_type()) {
			entity.setSection_id("88"); // TODO
		}
		if ("".equals(entity.getPat_id())) {
			entity.setPat_id(null); // TODO
		}
		entity.setQuotation_first(0);

		int lineScheduledExpedited = 0;
		// 根据流程选择加急(CCD线更换)
		if (!lightFix) {
			ProcessAssignMapper paMapper = conn.getMapper(ProcessAssignMapper.class);
			ProcessAssignTemplateEntity paEntity = paMapper.getProcessAssignTemplateByID(entity.getPat_id());
			if (paEntity != null && paEntity.getDerive_kind() != null && paEntity.getDerive_kind() == 3) {
				lineScheduledExpedited = 1;
			}
		}

		if (!peripheral) {
			entity.setWip_location(null);
		}
		dao.updateInline(entity);

		String materialId = entity.getMaterial_id();

		// 删除302工位的等待作业(如果以中断完成的话) removeWaiting
		ProductionFeatureMapper featureMapper = conn.getMapper(ProductionFeatureMapper.class);
		featureMapper.removeWaiting(materialId, "00000000025");

		// 取得维修对象详细信息
		MaterialMapper mdao = conn.getMapper(MaterialMapper.class);
		MaterialEntity mEntity = mdao.getMaterialNamedEntityByKey(materialId);

		Integer fix_type = mEntity.getFix_type();

		if (fix_type != null && fix_type == 1) { // //如果是流水线增加进展记录
			// 插入3条记录到material_process
			String pat_id = entity.getPat_id();

			MaterialProcessMapper mapper = conn.getMapper(MaterialProcessMapper.class);
			MaterialProcessEntity insertBean = new MaterialProcessEntity();
			insertBean.setMaterial_id(materialId);
			insertBean.setLine_expedited(lineScheduledExpedited);

			Date workDate4PreCom = workDates[1];

			int px = 0;
			boolean hasNs = true;
			if (lightFix) {
				// 小修理
				MaterialProcessAssignMapper mpaMapper = conn.getMapper(MaterialProcessAssignMapper.class);
				// 取得覆盖工程
				List<LineEntity> lines = mpaMapper.getWorkedLines(materialId);
				for (LineEntity line : lines) {
					if ("00000000012".equals(line.getLine_id())) {
						// 分线
						px = mpService.evalPx(mEntity.getModel_id(), "00000000012", lightFix, conn);
						insertBean.setPx(px);

						insertBean.setScheduled_date(workDate4PreCom);
						insertBean.setLine_id("00000000012");
						mapper.insertMaterialProcess(insertBean);
					} else
					if ("00000000013".equals(line.getLine_id())) {

						px = mpService.evalPx(mEntity.getModel_id(), "00000000013", lightFix, conn);
						insertBean.setPx(px);

						insertBean.setScheduled_date(workDate4PreCom);
						insertBean.setLine_id("00000000013");
						mapper.insertMaterialProcess(insertBean);
					}
				}
			} else {
				// 大修理
				// 金吉路 !"06".equals(mEntity.getKind()) && 
				if (!"00000000055".equals(mEntity.getCategory_id()) && !"07".equals(mEntity.getKind())) {
					px = mpService.evalPx(mEntity.getModel_id(), "00000000012", lightFix, conn);
					insertBean.setPx(px);

					insertBean.setScheduled_date(workDate4PreCom);
					insertBean.setLine_id("00000000012");
					mapper.insertMaterialProcess(insertBean);
				}

				ProcessAssignService pas = new ProcessAssignService();
				hasNs = pas.checkPatHasNs(pat_id, conn);

				// while S1 no NS
				if (1 != level && hasNs) {
					px = mpService.evalPx(mEntity.getModel_id(), "00000000013", lightFix, conn);
					insertBean.setPx(px);
					insertBean.setLine_id("00000000013");
					mapper.insertMaterialProcess(insertBean);
				}
			}
			/** 设定产出安排时间为 同意日7个工作日后 */
			insertBean.setLine_expedited(0);
			insertBean.setScheduled_date(workDate);
			insertBean.setLine_id("00000000014");
			px = mpService.evalPx(mEntity.getModel_id(), "00000000014", lightFix, conn);
			insertBean.setPx(px);

			// 确定有没有
			MaterialProcessEntity ret = mapper.loadMaterialProcessOfLine(materialId, "00000000014");

			if (ret == null) {
				mapper.insertMaterialProcess(insertBean);
			} else {
				// 更新纳期
				mapper.updateMaterialProcess(insertBean);
			}

			// 插入作业
			ProductionFeatureEntity featureEntity = new ProductionFeatureEntity();
			featureEntity.setMaterial_id(materialId);
			featureEntity.setOperate_result(0);
			featureEntity.setPace(0);
			featureEntity.setRework(0);

			PositionPanelService pps = new PositionPanelService();
			String sampleFirstPositionId = null;
			if (lightFix) {
				// 取得首工位
				MaterialProcessAssignService mpas = new MaterialProcessAssignService();
				String firstPosition_id = mpas.getFirstPositionId(materialId, conn);
				// 没有指定流程时
				if (firstPosition_id == null) {
					firstPosition_id = "00000000052";
				}

				featureEntity.setSection_id(entity.getSection_id());
				featureEntity.setPosition_id(firstPosition_id);

				// 确认已经是否完成作业
				ProductionFeatureService pfService = new ProductionFeatureService();
				pfService.fingerSpecifyPosition(materialId, false, featureEntity, triggerList, conn);
				featureEntity.setPosition_id(null);

				if ("00000000001".equals(entity.getSection_id())) {
					pps.notifyPosition(entity.getSection_id(), firstPosition_id, materialId, true);
				}

				sampleFirstPositionId = firstPosition_id;
			} else {
				// 302指定投线
				if (entity.getCcd_change() != null && "true".equals(entity.getCcd_change()) ) {
					featureEntity.setPosition_id("00000000025"); // TODO CCD
					featureEntity.setSection_id("00000000001");
				} else {
					ProcessAssignService pas = new ProcessAssignService();
					List<String> firstPosition_ids = pas.getFirstPositionIds(pat_id, conn);
					if (firstPosition_ids.size() > 0) {
						for (String firstPosition_id : firstPosition_ids) {
							if (!hasNs) {
								boolean isPass = false;
								int iPos = Integer.parseInt(firstPosition_id);
								for (int j = 0; j < ProcessAssignService.S1PASSES.length; j++) {
									if (ProcessAssignService.S1PASSES[j] == iPos) {
										isPass = true;
										break;
									}
								}
								if (isPass) continue;
							}
							sampleFirstPositionId = firstPosition_id;
							featureEntity.setPosition_id(firstPosition_id);
							featureEntity.setSection_id(entity.getSection_id());
							featureMapper.insertProductionFeature(featureEntity);
							pps.notifyPosition(entity.getSection_id(), firstPosition_id, materialId, false);
						}
						featureEntity.setPosition_id(null);
					} else {
						String firstPosition_id = "00000000016";
						sampleFirstPositionId = firstPosition_id;

						featureEntity.setPosition_id(firstPosition_id);
						featureEntity.setSection_id(entity.getSection_id());
						pps.notifyPosition(entity.getSection_id(), firstPosition_id, materialId, false);
					}
				}
			}

			// 
			if (lightFix
					|| (peripheral && !isE2)
					|| RvsConsts.CATEGORY_UDI.equals(mEntity.getCategory_id())) {
				// 周边非E2或光学视管判断是否订购零件
				MaterialPartialService mps = new MaterialPartialService();
				MaterialPartialForm mp = mps.loadMaterialPartial(conn, materialId, null);
				if (mp == null || 
						!("0".equals(mp.getBo_flg()) || "2".equals(mp.getBo_flg()))) {
					// 否则首工位进入PA
					ForSolutionAreaService fsoService = new ForSolutionAreaService();
					fsoService.create(materialId, "零件未到达时投线", ForSolutionAreaService.REASON_BO_OF_POSITION, sampleFirstPositionId, conn, false);
				}
			}

			if (featureEntity.getPosition_id() != null) {
				featureMapper.insertProductionFeature(featureEntity);
			}
		} else
		if (fix_type != null && fix_type == 2) { // //如果是单元进展记录
			// 判断是否EndoEye TODO
//			if (modelForm != null && "06".equals(modelForm.getKind())) {
//				// 插入1条记录到material_process
//
//				MaterialProcessMapper mapper = conn.getMapper(MaterialProcessMapper.class);
//				MaterialProcessEntity insertBean = new MaterialProcessEntity();
//				insertBean.setMaterial_id(materialId);
//
//				/** 设定产出安排时间为 同意日7个工作日后 */
//				insertBean.setSchedule_date(workDate);
//				insertBean.setLine_id("00000000014");
//				mapper.insertMaterialProcess(insertBean);
//
//				// 插入作业
//				ProductionFeatureEntity featureEntity = new ProductionFeatureEntity();
//				featureEntity.setMaterial_id(materialId);
//				featureEntity.setPosition_id("00000000032"); // TODO by PT
//				featureEntity.setSection_id(entity.getSection_id());
//				featureEntity.setOperate_result(0);
//				featureEntity.setPace(0);
//				featureEntity.setRework(0);
//				featureMapper.insertProductionFeature(featureEntity);
//			}

			// 如果是单元的话，送到611。  
			if (model != null && !"06".equals(model.getKind())) {
				String qa_position = "00000000046";
				Integer serviceRepairFlg = mEntity.getService_repair_flg();
				if (serviceRepairFlg == null) serviceRepairFlg = 0;
				if (lightFix && 
						(serviceRepairFlg != 1 && serviceRepairFlg != 2)) { // D小修 非保内返修 非QIS 单元自己出检612 单元小修理自检
					qa_position = "00000000052";
				}
				if (level == 56 || level == 57 || level == 58 || level==59) {
					qa_position = RvsConsts.POSITION_QA_P_613;
				}
				if (RvsConsts.CATEGORY_UDI.equals(mEntity.getCategory_id())) {
					qa_position = RvsConsts.POSITION_QA_P_614;
				}

				ProductionFeatureEntity nextWorkingPf = new ProductionFeatureEntity();
				nextWorkingPf.setPosition_id(qa_position);
				nextWorkingPf.setSection_id("88"); // TODO
				nextWorkingPf.setRework(0);
				ProductionFeatureService pfService = new ProductionFeatureService();
				pfService.fingerSpecifyPosition(materialId, true, nextWorkingPf, null, conn);
			}
		}

		// 小修理流程写入维修对象备注
		if (lightFix) {
			MaterialService materialService = new MaterialService();

			// 设定为系统
			String operator_id = "00000000001";
			// 得到小修理信息
			MaterialProcessAssignService mpas = new MaterialProcessAssignService();
			String lightFixStr = mpas.getLightFixesByMaterial(materialId, conn);

			String lightFlowStr = mpas.getLightFixFlowByMaterial(materialId, null, conn);
			String comment = mpas.getLightStr(lightFixStr, lightFlowStr);
			materialService.updateMaterialComment(materialId, operator_id, comment, conn);
		}
	}

	/**
	 * 标为加急
	 * 
	 * @param ids
	 * @param conn
	 * @throws Exception
	 */
	public void updateExpedite(List<String> ids, SqlSessionManager conn) throws Exception {
		MaterialMapper mDao = conn.getMapper(MaterialMapper.class);
		mDao.updateMaterialExpedite(ids);
	}

	/**
	 * 指定进行Ccd盖玻璃更换（投线后）
	 * @param material_id
	 * @param conn
	 */
	public void assginCCDChange(String material_id, SqlSessionManager conn) {
		MaterialFactMapper mfMapper = conn.getMapper(MaterialFactMapper.class);
		mfMapper.assginCCDChange(material_id);
	}

	private String getBr(String exist_result, String addi_message) {
		if(CommonStringUtil.isEmpty(exist_result)) {
			return addi_message;
		}
		return exist_result + "\n" + addi_message;
	}

	public List<MaterialForm> getInlinePlan(SqlSession conn) {
		MaterialFactMapper mfMapper = conn.getMapper(MaterialFactMapper.class);
		List<MaterialForm> resultForm = new ArrayList<MaterialForm>();

		List<MaterialEntity> result = mfMapper.getInlinePlan();

		CopyOptions cos = new CopyOptions();
		cos.excludeNull().excludeEmptyString().dateConverter("MM-dd", "agreed_date");

		for (MaterialEntity bean : result) {
			MaterialForm form = new MaterialForm();
			BeanUtil.copyToForm(bean, form, cos);
			int level = bean.getLevel();
			boolean isLightFix = RvsUtils.isLightFix(level);
//			if ((level != 9 && level != 91 && level != 92 && level != 93 && level != 56 && level != 57 && level != 58) &&
//					!RvsUtils.getCcdModels(conn).contains(form.getModel_id())) {
			if ((!isLightFix && level != 56 && level != 57 && level != 58 && level!=59) &&
					!RvsUtils.getCcdModels(conn).contains(form.getModel_id())) {
				form.setQuotation_first("-1");
			}
			form.setService_repair_flg(CodeListUtils.getValue("material_service_repair", form.getService_repair_flg()));
			resultForm.add(form);
		}

		return resultForm;
	}

	public void changeinlinePlan(ActionForm form, SqlSessionManager conn) {
		MaterialFactMapper mfMapper = conn.getMapper(MaterialFactMapper.class);

		// 表单复制到数据对象
		MaterialFactEntity conditionBean = new MaterialFactEntity();
		BeanUtil.copyToBean(form, conditionBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		if (conditionBean.getCcd_change() != null) {
			mfMapper.assginCCDChange(conditionBean.getMaterial_id());
		} else {
			mfMapper.changeInlinePlan(conditionBean);
		}
	}

	public void doBatchInline(String[] material_ids, SqlSessionManager conn) throws Exception {
		MaterialFactMapper mfMapper = conn.getMapper(MaterialFactMapper.class);

		List<MaterialFactEntity> mfEntities = mfMapper.getInlinePlanInfo(material_ids);

		ArrayList<String> triggerList = new ArrayList<String>();
		for (MaterialFactEntity mfEntity : mfEntities) {
			updateInline(mfEntity, conn, triggerList);
		}

		// FSE 数据同步
		try{
			conn.commit();
			for (String material_id : material_ids) {
				FseBridgeUtil.toUpdateMaterial(material_id, "200inline");
				FseBridgeUtil.toUpdateMaterialProcess(material_id, "200inline");
			}
			RvsUtils.sendTrigger(triggerList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String createInlineReport(SqlSession conn) throws Exception {

		// 模板路径
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "投线单一览模板.xls";
		String cacheName = "投线单一览" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" + cacheName;
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		SimpleDateFormat printFormat=new SimpleDateFormat("MM-dd HH:mm");
		SimpleDateFormat agreeDateFormat=new SimpleDateFormat("MM-dd ");
		MaterialFactMapper dao = conn.getMapper(MaterialFactMapper.class);
		List<MaterialEntity> lResultBean = dao.getInlinePlan();
		MaterialProcessAssignService mpas = new MaterialProcessAssignService();
		ProcessAssignService pas = new ProcessAssignService();
		PositionMapper pMapper = conn.getMapper(PositionMapper.class);

		Map<String, String> processCodeMap = new HashMap<String, String>();

		OutputStream out = null;
		InputStream in = null;
		try {
			in = new FileInputStream(cachePath);
			HSSFWorkbook work = new HSSFWorkbook(in);
			HSSFSheet sheet = work.getSheetAt(0);	
			
			/*设置单元格内容居中显示*/
			HSSFCellStyle styleAlignCenter = work.createCellStyle();
			styleAlignCenter.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			styleAlignCenter.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			styleAlignCenter.setBorderRight(HSSFCellStyle.BORDER_THIN); 
			styleAlignCenter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleAlignCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			styleAlignCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			styleAlignCenter.setWrapText(true);
			
			/*设置单元格内容居左显示*/
			HSSFCellStyle styleAlignLeft = work.createCellStyle();
			styleAlignLeft.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			styleAlignLeft.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			styleAlignLeft.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleAlignLeft.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleAlignLeft.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			styleAlignLeft.setWrapText(true); 

			// 打印时间
			HSSFRow row3 = sheet.getRow(2);
			HSSFCell printTimeCell = row3.createCell(6);
			printTimeCell.setCellValue(printFormat.format(new Date()));

			int index = 4;
			for (int i = 0; i < lResultBean.size(); i++) {					
				index++;
				MaterialEntity materialEntity = lResultBean.get(i);
				HSSFRow row = sheet.createRow(index);	
				
				//序号
				HSSFCell indexCell = row.createCell(0);
				indexCell.setCellValue(i + 1);
				indexCell.setCellStyle(styleAlignCenter);

				//修理单号
				HSSFCell sorcNoCell = 	row.createCell(1);
				sorcNoCell.setCellValue(materialEntity.getSorc_no());
				sorcNoCell.setCellStyle(styleAlignLeft);
			
				//型号
				HSSFCell modelNameCell = row.createCell(2);
				modelNameCell.setCellValue(materialEntity.getModel_name());
				modelNameCell.setCellStyle(styleAlignLeft);
				
				//机身号
				HSSFCell serialNoCell = row.createCell(3);
				serialNoCell.setCellValue(materialEntity.getSerial_no());
				serialNoCell.setCellStyle(styleAlignLeft);

				//等级
				HSSFCell levelCell = row.createCell(4);
				Integer level = materialEntity.getLevel();
				String levelData = CodeListUtils.getValue("material_level",level.toString());
				levelCell.setCellValue(levelData);
				levelCell.setCellStyle(styleAlignCenter);

				//客户同意日
				HSSFCell agreedDateCell = row.createCell(5);
				agreedDateCell.setCellValue(agreeDateFormat.format(materialEntity.getAgreed_date()));
				agreedDateCell.setCellStyle(styleAlignCenter);

				//WIP 库位
				HSSFCell wipLocationCell = row.createCell(6);
				wipLocationCell.setCellValue(materialEntity.getWip_location());
				wipLocationCell.setCellStyle(styleAlignCenter);

				//投入科室
				HSSFCell sectionNameCell = row.createCell(7);
				if (materialEntity.getFix_type() == 1) {
					sectionNameCell.setCellValue(materialEntity.getSection_name());
				} else {
					sectionNameCell.setCellValue("单元");
				}
				sectionNameCell.setCellStyle(styleAlignCenter);
				
				//投入工位
				HSSFCell processCodesCell = row.createCell(8);
				if (materialEntity.getFix_type() == 1) {
					boolean isLightFix = RvsUtils.isLightFix(level);
//					if (level == 9 || level == 91 || level == 92 || level == 93) {
					if (isLightFix) {
						// 取得首工位
						String firstPosition_id = mpas.getFirstPositionId(materialEntity.getMaterial_id(), conn);
						String process_code = null;
						if (!processCodeMap.containsKey(firstPosition_id)) {
							PositionEntity pEntity = pMapper.getPositionByID(firstPosition_id);
							processCodeMap.put(firstPosition_id, pEntity.getProcess_code());
						}
						process_code = processCodeMap.get(firstPosition_id);

						processCodesCell.setCellValue(process_code);

					} else {
						if (materialEntity.getQuotation_first() == 9) {
							processCodesCell.setCellValue("302");
						} else {
							List<String> firstPosition_ids = pas.getFirstPositionIds(materialEntity.getPat_id(), conn);

							String[] process_codes = new String[firstPosition_ids.size()];

							for (int iPc=0;iPc < firstPosition_ids.size();iPc++) {
								String firstPosition_id = firstPosition_ids.get(iPc);
								String process_code = null;
								if (!processCodeMap.containsKey(firstPosition_id)) {
									PositionEntity pEntity = pMapper.getPositionByID(firstPosition_id);
									processCodeMap.put(firstPosition_id, pEntity.getProcess_code());
								}
								process_code = processCodeMap.get(firstPosition_id);
								process_codes[iPc] = process_code;
							}

							processCodesCell.setCellValue(CommonStringUtil.joinBy("", process_codes));
						}
					}
				}
				processCodesCell.setCellStyle(styleAlignCenter);
			}
			out = new FileOutputStream(cachePath);
			try {
				work.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		return cacheName;
	}

}

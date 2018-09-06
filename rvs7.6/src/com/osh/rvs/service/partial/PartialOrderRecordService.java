package com.osh.rvs.service.partial;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.PartialOrderRecordEntity;
import com.osh.rvs.common.CopyByPoi;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MonthFilesDownloadForm;
import com.osh.rvs.form.partial.PartialOrderRecordForm;
import com.osh.rvs.mapper.master.CategoryMapper;
import com.osh.rvs.mapper.master.ModelMapper;
import com.osh.rvs.mapper.partial.PartialBaseLineValueMapping;
import com.osh.rvs.mapper.partial.PartialOrderRecordMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;

public class PartialOrderRecordService {

	/**
	 * 检索零件现状信息
	 * @param form
	 * @param periodDays 
	 * @param conn
	 * @return
	 */
	public List<PartialOrderRecordForm> searchPartials(ActionForm form, Integer periodDays, SqlSession conn){

		List<PartialOrderRecordEntity> lResultBean = searchPartialsEntity(form, periodDays, conn);
		List<PartialOrderRecordForm> lResultForm = new ArrayList<PartialOrderRecordForm>();
		
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, PartialOrderRecordForm.class);
		
		return lResultForm;
	}

	public List<PartialOrderRecordEntity> searchPartialsEntity(ActionForm form, Integer periodDays, SqlSession conn){
		PartialOrderRecordEntity entity = new PartialOrderRecordEntity();
		BeanUtil.copyToBean(form, entity, null);
		// 多选检索条件
		entity.setCategory_id(entity.getCategory_id().replaceAll(",", "','"));
		entity.setEchelon(entity.getEchelon().replaceAll(",", "','"));
		entity.setWorkday(periodDays);
		
		PartialOrderRecordMapper dao = conn.getMapper(PartialOrderRecordMapper.class);
		
		List<PartialOrderRecordEntity> lResultBean = dao.searchPartials(entity);
	
		return lResultBean;
	}

	public List<PartialOrderRecordForm> searchEchelons(ActionForm form, Integer periodDays, SqlSession conn){
		PartialOrderRecordEntity entity = new PartialOrderRecordEntity();
		BeanUtil.copyToBean(form, entity, null);
		entity.setCategory_id(entity.getCategory_id().replaceAll(",", "','"));
		entity.setEchelon(entity.getEchelon().replaceAll(",", "','"));
		entity.setWorkday(periodDays);
		
		PartialOrderRecordMapper dao = conn.getMapper(PartialOrderRecordMapper.class);
		
		List<PartialOrderRecordEntity> lResultBean = dao.searchEchelons(entity);

		boolean a = false;
		boolean b = false;
		boolean c = false;
		boolean d = false;

		List<PartialOrderRecordForm> lResultForm = new ArrayList<PartialOrderRecordForm>();
		for (PartialOrderRecordEntity porEntity : lResultBean) {
			if ("1".equals(porEntity.getEchelon())) {
				a = true;
			} else if ("2".equals(porEntity.getEchelon())) {
				b = true;
			} else if ("3".equals(porEntity.getEchelon())) {
				c = true;
			} else if ("4".equals(porEntity.getEchelon())) {
				d = true;
			}
			entity.setEchelon(porEntity.getEchelon());
			List<PartialOrderRecordEntity> levelModels = dao.searchLevelModels(entity);
			
			float waring = 0;
			float ldNum = 0;
			for (PartialOrderRecordEntity levelModel : levelModels) {
				if (levelModel.getTurnround_rate() != null) {
					if (levelModel.getTurnround_rate().floatValue() > 100 || levelModel.getTurnround_rate().floatValue() < 20) {
						waring +=1;
					}
				}
				if (levelModel.getLd_num() != null && levelModel.getLd_num() != 0) {
					ldNum +=1;
				}
			}

			PartialOrderRecordForm porForm = new PartialOrderRecordForm();
			BeanUtil.copyToForm(porEntity, porForm, null);
			
			porForm.setTurnround_rate(String.format("%.2f", Float.valueOf((1-(waring/ldNum))*100)));
			
			lResultForm.add(porForm);
		}
		
		if (!a) {
			PartialOrderRecordForm porForm = new PartialOrderRecordForm();
			porForm.setEchelon("1");
			lResultForm.add(0,porForm);
		}
		if (!b) {
			PartialOrderRecordForm porForm = new PartialOrderRecordForm();
			porForm.setEchelon("2");
			lResultForm.add(1,porForm);
		}
		if (!c) {
			PartialOrderRecordForm porForm = new PartialOrderRecordForm();
			porForm.setEchelon("3");
			lResultForm.add(2,porForm);
		}
		if (!d) {
			PartialOrderRecordForm porForm = new PartialOrderRecordForm();
			porForm.setEchelon("4");
			lResultForm.add(3,porForm);
		}
		
		return lResultForm;
	}
	
	public List<PartialOrderRecordEntity> searchLevelModelsEntity(ActionForm form, Integer periodDays, SqlSession conn) {
		PartialOrderRecordEntity entity = new PartialOrderRecordEntity();
		BeanUtil.copyToBean(form, entity, null);
		entity.setCategory_id(entity.getCategory_id().replaceAll(",", "','"));
		entity.setEchelon(entity.getEchelon().replaceAll(",", "','"));
		entity.setWorkday(periodDays);

		PartialOrderRecordMapper dao = conn.getMapper(PartialOrderRecordMapper.class);

		List<PartialOrderRecordEntity> lResultBean = dao.searchLevelModels(entity);
		return lResultBean;
	}

	public List<PartialOrderRecordForm> searchLevelModels(ActionForm form, Integer periodDays, SqlSession conn){
	
		List<PartialOrderRecordEntity> lResultBean = searchLevelModelsEntity(form, periodDays, conn);
		List<PartialOrderRecordForm> lResultForm = new ArrayList<PartialOrderRecordForm>();
		
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, PartialOrderRecordForm.class);
		
		return lResultForm;
	}

	public Integer getPeriodByConditions(ActionForm form, Date[] period, SqlSession conn) {
		PartialOrderRecordEntity entity = new PartialOrderRecordEntity();
		BeanUtil.copyToBean(form, entity, null);
		entity.setCategory_id(entity.getCategory_id().replaceAll(",", "','"));
		entity.setEchelon(entity.getEchelon().replaceAll(",", "','"));
		
		PartialOrderRecordMapper dao = conn.getMapper(PartialOrderRecordMapper.class);

		PartialOrderRecordEntity ret = dao.getPeriodEdges(entity);

		if (ret == null) return 0;
		Date maxd = ret.getMaxd();
		Date mind = ret.getMind();
		if (maxd == null || mind == null) {
			return 0;
		} else {
			period[0].setTime(mind.getTime());
			period[1].setTime(maxd.getTime());
			return ret.getWorkday();
		}
	}

	public String getPeriodMessage(Integer periodDays, Date[] period) {
		return "当前检索的结果中，零件订购日在 " + DateUtil.toString(period[0], DateUtil.DATE_PATTERN)
				+ " 和 " + DateUtil.toString(period[1], DateUtil.DATE_PATTERN) + " 之间，间隔 " + periodDays + " 个工作日";
	}

	/**
	 * 建立BO分析报表
	 * @param conn 
	 * @param periodDays 
	 * @param period 
	 * @param subform 
	 * @return Filename
	 * @throws Exception 
	 */
	public String createReport(ActionForm subform, Date[] period, Integer periodDays, SqlSession conn) throws Exception {
		String templateFilePath = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "BO分析表模板.xls";
		String cacheName ="BO分析表" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName;

		try {
			FileUtils.copyFile(new File(templateFilePath), new File(cachePath));
		} catch (IOException e) {
			return null;
		}

		OutputStream out = null;
		InputStream in = null;
		try{
			in = new FileInputStream(cachePath);//读取文件 
			HSSFWorkbook work=new HSSFWorkbook(in);//创建HSSF文件

			HSSFSheet summarySheet=work.getSheetAt(0);//总览工作表
			HSSFSheet lmSheet=work.getSheetAt(1);//等级型号详细
			HSSFSheet pSheet=work.getSheetAt(2);//零件详细
			HSSFSheet titleSheet=work.getSheetAt(3);//标题模板工作表

			List<PartialOrderRecordEntity> retLevelModels = searchLevelModelsEntity(subform, periodDays, conn);

			List<PartialOrderRecordEntity> retBoPartials = searchPartialsEntity(subform, periodDays, conn);

			PartialOrderRecordForm partialOrderRecordForm = (PartialOrderRecordForm)subform;
			String orgLevel = partialOrderRecordForm.getLevel();
			String orgModelId = partialOrderRecordForm.getModel_id();
			String orgPartial_code = partialOrderRecordForm.getPartial_code();

			HSSFCellStyle cellStardard = work.createCellStyle();    
			cellStardard.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			cellStardard.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			cellStardard.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellStardard.setBorderBottom(HSSFCellStyle.BORDER_THIN);

			HSSFCellStyle cellCenter = work.createCellStyle();    
			cellCenter.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			cellCenter.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			cellCenter.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellCenter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellCenter.setWrapText(true);

			HSSFCellStyle cellRight = work.createCellStyle();    
			cellRight.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			cellRight.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			cellRight.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellRight.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellRight.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			int factLine = 0;
			int mainNo = 0;
			int subNo = 0;
			
			// 历史平均同意数
			Map<String, BigDecimal> cacheOfAgreeHistoryML = new HashMap<String, BigDecimal>();
			Map<String, BigDecimal> cacheOfAgreeHistoryP = new HashMap<String, BigDecimal>();
			// 本期单日订购峰值
			Map<String, String> cacheOfOrderHillML = new HashMap<String, String>();
			Map<String, String> cacheOfOrderHillP = new HashMap<String, String>();
			// 不良追加订购次数
			Map<String, Integer> cacheOfNoGoodTimesML = new HashMap<String, Integer>();
			Map<String, Integer> cacheOfNoGoodTimesP = new HashMap<String, Integer>();

			PartialOrderRecordMapper porMapper = conn.getMapper(PartialOrderRecordMapper.class);

			int ttlRecept = 0;
			int ttlAgreed = 0;
			int ttlOrder = 0;
			int ttlBoMaterial = 0;
			int ttlBo3DaysMaterial = 0;
			int ttlBoPartial = 0;

			for (PartialOrderRecordEntity retLevelModel : retLevelModels) {

				ttlRecept += retLevelModel.getRecept_num();
				ttlAgreed += retLevelModel.getAgreed_num();
				ttlOrder += retLevelModel.getOrder_num();
				ttlBoMaterial += retLevelModel.getBo_num();
				ttlBo3DaysMaterial += retLevelModel.getBo3days_num();

				// 只显示有BO的等级型号
				if (retLevelModel.getBo_num() == 0) continue;

				// 等级型号标题
				HSSFRow lmRow = lmSheet.createRow(factLine++);
				CopyByPoi.copyRow(titleSheet.getRow(0), lmRow, true);
				subNo = 0;

				// 等级型号内容
				lmRow = lmSheet.createRow(factLine++);
				// 序号
				HSSFCell lmCell = lmRow.createCell(0);
				lmCell.setCellStyle(cellCenter);
				lmCell.setCellValue(++mainNo);

				// 等级 + 型号
				lmCell = lmRow.createCell(1);
				lmCell.setCellStyle(cellCenter);
				lmCell.setCellValue(CodeListUtils.getValue("material_level", retLevelModel.getLevel()) + retLevelModel.getModel_name());

				// 梯队
				lmCell = lmRow.createCell(3);
				lmCell.setCellStyle(cellStardard);
				lmCell.setCellValue(CodeListUtils.getValue("echelon_code", retLevelModel.getEchelon()));

				// 受理数
				lmCell = lmRow.createCell(4);
				lmCell.setCellStyle(cellStardard);
				lmCell.setCellValue(retLevelModel.getRecept_num());

				// 同意数
				lmCell = lmRow.createCell(5);
				lmCell.setCellStyle(cellStardard);
				lmCell.setCellValue(retLevelModel.getAgreed_num());

				// 零件订购数
				lmCell = lmRow.createCell(6);
				lmCell.setCellStyle(cellStardard);
				lmCell.setCellValue(retLevelModel.getOrder_num());
				// 当天BO数
				lmCell = lmRow.createCell(7);
				lmCell.setCellStyle(cellStardard);
				lmCell.setCellValue(retLevelModel.getBo_num());
				// 三天BO数
				lmCell = lmRow.createCell(8);
				lmCell.setCellStyle(cellStardard);
				if (retLevelModel.getBo3days_num() != null)
					lmCell.setCellValue(retLevelModel.getBo3days_num());
				// 等级型号拉动台数
				lmCell = lmRow.createCell(9);
				lmCell.setCellStyle(cellStardard);
				if (retLevelModel.getLd_num() != null) 
					lmCell.setCellValue(retLevelModel.getLd_num());
				else
					lmCell.setCellValue(" - ");
				String sML = retLevelModel.getModel_name() + "|" + retLevelModel.getLevel(); 

				// 历史平均同意数
				lmCell = lmRow.createCell(10);
				lmCell.setCellStyle(cellStardard);
				if (cacheOfAgreeHistoryML.containsKey(sML)) {
					lmCell.setCellValue(cacheOfAgreeHistoryML.get(sML).setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue());
				} else {
					retLevelModel.setWorkday(periodDays);
					BigDecimal agreeHistoryML = porMapper.getAgreeHistoryML(retLevelModel);
					lmCell.setCellValue(agreeHistoryML.setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue());
					cacheOfAgreeHistoryML.put(sML, agreeHistoryML);
				}
				// 本期单日订购峰值
				lmCell = lmRow.createCell(11);
				lmCell.setCellStyle(cellRight);
				if (cacheOfOrderHillML.containsKey(sML)) {
					lmCell.setCellValue(cacheOfOrderHillML.get(sML));
				} else {
					retLevelModel.setOrder_date_start(period[0]);
					retLevelModel.setOrder_date_end(period[1]);
					String orderHillML = porMapper.getOrderHillML(retLevelModel);
					lmCell.setCellValue(orderHillML);
					cacheOfOrderHillML.put(sML, orderHillML);
				}
				// 不良追加订购次数
				lmCell = lmRow.createCell(12);
				lmCell.setCellStyle(cellStardard);
				if (cacheOfNoGoodTimesML.containsKey(sML)) {
					lmCell.setCellValue(cacheOfNoGoodTimesML.get(sML));
				} else {
					retLevelModel.setOrder_date_start(period[0]);
					retLevelModel.setOrder_date_end(period[1]);
					Integer noGoodTimesML = porMapper.getNoGoodTimesML(retLevelModel);
					lmCell.setCellValue(noGoodTimesML);
					cacheOfNoGoodTimesML.put(sML, noGoodTimesML);
				}

				// 零件标题
				lmRow = lmSheet.createRow(factLine++);
				CopyByPoi.copyRow(titleSheet.getRow(1), lmRow, true);

				// 零件内容
				partialOrderRecordForm.setLevel(retLevelModel.getLevel());
				partialOrderRecordForm.setModel_id(retLevelModel.getModel_id());

				// 指定的零件
				List<PartialOrderRecordEntity> partials = searchPartialsEntity(partialOrderRecordForm, periodDays, conn);
				for (PartialOrderRecordEntity partial : partials) {
					// 只显示有BO的零件
					if (partial.getBo_num() == 0) continue;

					subNo++;

					HSSFRow pRow = lmSheet.createRow(factLine++);

					HSSFCell pCell = pRow.createCell(0);
					pCell.setCellStyle(cellStardard);
					 pCell = pRow.createCell(1);
						pCell.setCellStyle(cellStardard);
						 pCell = pRow.createCell(2);
							pCell.setCellStyle(cellStardard);
							 pCell = pRow.createCell(3);
								pCell.setCellStyle(cellStardard);

					// 零件编号	
					pCell = pRow.createCell(4);
					pCell.setCellStyle(cellStardard);
					String pCode = partial.getPartial_code();
					pCell.setCellValue(pCode);
					// 零件名称	
					pCell = pRow.createCell(5);
					pCell.setCellStyle(cellStardard);
					pCell.setCellValue(partial.getPartial_name());
					// 零件订购数	
					pCell = pRow.createCell(6);
					pCell.setCellStyle(cellStardard);
					pCell.setCellValue(partial.getOrder_num());
					// 当天BO数	
					pCell = pRow.createCell(7);
					pCell.setCellStyle(cellStardard);
					pCell.setCellValue(partial.getBo_num());
					// 三天BO数	
					pCell = pRow.createCell(8);
					pCell.setCellStyle(cellStardard);
					if (partial.getBo3days_num() != null)
						pCell.setCellValue(partial.getBo3days_num());
					// 基准数	
					pCell = pRow.createCell(9);
					pCell.setCellStyle(cellStardard);
					if (partial.getBase_num() != null) 
						pCell.setCellValue(partial.getBase_num());
					else
						pCell.setCellValue(" - ");

					// 历史平均使用数	
					pCell = pRow.createCell(10);
					pCell.setCellStyle(cellStardard);
					if (cacheOfAgreeHistoryP.containsKey(pCode)) {
						pCell.setCellValue(cacheOfAgreeHistoryP.get(pCode).setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue());
					} else {
						partial.setWorkday(periodDays);
						BigDecimal usedHistoryP = porMapper.getAgreeHistoryP(partial);
						pCell.setCellValue(usedHistoryP.setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue());
						cacheOfAgreeHistoryP.put(pCode, usedHistoryP);
					}

					// 本期单日订购峰值	
					pCell = pRow.createCell(11);
					pCell.setCellStyle(cellRight);
					if (cacheOfOrderHillP.containsKey(pCode)) {
						pCell.setCellValue(cacheOfOrderHillP.get(pCode));
					} else {
						partial.setOrder_date_start(period[0]);
						partial.setOrder_date_end(period[1]);
						String orderHillP = porMapper.getOrderHillP(partial);
						pCell.setCellValue(orderHillP);
						cacheOfOrderHillP.put(pCode, orderHillP);
					}

					// 不良追加订购件数
					pCell = pRow.createCell(12);
					pCell.setCellStyle(cellStardard);
					if (cacheOfNoGoodTimesP.containsKey(pCode)) {
						pCell.setCellValue(cacheOfNoGoodTimesP.get(pCode));
					} else {
						partial.setOrder_date_start(period[0]);
						partial.setOrder_date_end(period[1]);
						Integer noGoodTimesP = porMapper.getNoGoodTimesP(partial);
						pCell.setCellValue(noGoodTimesP);
						cacheOfNoGoodTimesP.put(pCode, noGoodTimesP);
					}
				}
				// 合并单元格
				CellRangeAddress region = new CellRangeAddress(factLine - subNo - 2, factLine - 1, 0, 0); // int firstRow, int lastRow, int firstCol, int lastCol
				lmSheet.addMergedRegion(region);
				region = new CellRangeAddress(factLine - subNo - 2, factLine - 1, 1, 2);
				lmSheet.addMergedRegion(region);
				region = new CellRangeAddress(factLine - subNo - 1, factLine - 1, 3, 3);
				lmSheet.addMergedRegion(region);
			}

			// 复原原始条件
			partialOrderRecordForm.setLevel(orgLevel);
			partialOrderRecordForm.setModel_id(orgModelId);

			factLine = 0;
			mainNo = 0;

			// BO发生零件一览工作表
			for (PartialOrderRecordEntity partial : retBoPartials) {

				// 只显示有BO的零件
				if (partial.getBo_num() == 0) continue;

				ttlBoPartial += partial.getBo_num();

				// 零件标题
				HSSFRow pRow = pSheet.createRow(factLine++);
				CopyByPoi.copyRow(titleSheet.getRow(2), pRow, true);
				subNo = 0;

				// 零件内容
				pRow = pSheet.createRow(factLine++);

				// 序号
				HSSFCell pCell = pRow.createCell(0);
				pCell.setCellStyle(cellCenter);
				pCell.setCellValue(++mainNo);

				// 零件编号	
				pCell = pRow.createCell(1);
				pCell.setCellStyle(cellCenter);
				String pCode = partial.getPartial_code();
				pCell.setCellValue(pCode);
				// 零件名称	
				pCell = pRow.createCell(2);
				pCell.setCellStyle(cellStardard);
				pCell.setCellValue(partial.getPartial_name());
				// 零件订购数	
				pCell = pRow.createCell(7);
				pCell.setCellStyle(cellStardard);
				pCell.setCellValue(partial.getOrder_num());
				// 当天BO数	
				pCell = pRow.createCell(8);
				pCell.setCellStyle(cellStardard);
				pCell.setCellValue(partial.getBo_num());
				// 三天BO数	
				pCell = pRow.createCell(9);
				pCell.setCellStyle(cellStardard);
				if (partial.getBo3days_num() != null)
					pCell.setCellValue(partial.getBo3days_num());
				// 基准数	
				pCell = pRow.createCell(10);
				pCell.setCellStyle(cellStardard);
				if (partial.getBase_num() != null) 
					pCell.setCellValue(partial.getBase_num());
				else
					pCell.setCellValue(" - ");

				// 历史平均使用数	
				pCell = pRow.createCell(11);
				pCell.setCellStyle(cellStardard);
				if (cacheOfAgreeHistoryP.containsKey(pCode)) {
					pCell.setCellValue(cacheOfAgreeHistoryP.get(pCode).setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue());
				} else {
					partial.setWorkday(periodDays);
					BigDecimal usedHistoryP = porMapper.getAgreeHistoryP(partial);
					pCell.setCellValue(usedHistoryP.setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue());
					cacheOfAgreeHistoryP.put(pCode, usedHistoryP);
				}

				// 本期单日订购峰值	
				pCell = pRow.createCell(12);
				pCell.setCellStyle(cellRight);
				if (cacheOfOrderHillP.containsKey(pCode)) {
					pCell.setCellValue(cacheOfOrderHillP.get(pCode));
				} else {
					partial.setOrder_date_start(period[0]);
					partial.setOrder_date_end(period[1]);
					String orderHillP = porMapper.getOrderHillP(partial);
					pCell.setCellValue(orderHillP);
					cacheOfOrderHillP.put(pCode, orderHillP);
				}

				// 不良追加订购件数
				pCell = pRow.createCell(13);
				pCell.setCellStyle(cellStardard);
				if (cacheOfNoGoodTimesP.containsKey(pCode)) {
					pCell.setCellValue(cacheOfNoGoodTimesP.get(pCode));
				} else {
					partial.setOrder_date_start(period[0]);
					partial.setOrder_date_end(period[1]);
					Integer noGoodTimesP = porMapper.getNoGoodTimesP(partial);
					pCell.setCellValue(noGoodTimesP);
					cacheOfNoGoodTimesP.put(pCode, noGoodTimesP);
				}

				// 型号等级标题
				pRow = pSheet.createRow(factLine++);
				CopyByPoi.copyRow(titleSheet.getRow(3), pRow, true);

				// 关联的型号等级
				partialOrderRecordForm.setPartial_code(pCode);
				List<PartialOrderRecordEntity> levelModels = searchLevelModelsEntity(partialOrderRecordForm, periodDays, conn);

				for (PartialOrderRecordEntity levelModel : levelModels) {
					// 只显示有BO的型号等级
					// if (levelModel.getBo_num() == 0) continue;

					subNo++;

					HSSFRow lmRow = pSheet.createRow(factLine++);
					
					HSSFCell lmCell = lmRow.createCell(0);
					lmCell.setCellStyle(cellStardard);
					lmCell = lmRow.createCell(1);
					lmCell.setCellStyle(cellStardard);

					// 等级 + 型号
					lmCell = lmRow.createCell(3);
					lmCell.setCellStyle(cellStardard);
					lmCell.setCellValue(CodeListUtils.getValue("material_level", levelModel.getLevel()) + levelModel.getModel_name());

					// 梯队
					lmCell = lmRow.createCell(4);
					lmCell.setCellStyle(cellStardard);
					lmCell.setCellValue(CodeListUtils.getValue("echelon_code", levelModel.getEchelon()));

					// 受理数
					lmCell = lmRow.createCell(5);
					lmCell.setCellStyle(cellStardard);
					lmCell.setCellValue(levelModel.getRecept_num());

					// 同意数
					lmCell = lmRow.createCell(6);
					lmCell.setCellStyle(cellStardard);
					lmCell.setCellValue(levelModel.getAgreed_num());

					// 零件订购数
					lmCell = lmRow.createCell(7);
					lmCell.setCellStyle(cellStardard);
					lmCell.setCellValue(levelModel.getOrder_num());
					// 当天BO数
					lmCell = lmRow.createCell(8);
					lmCell.setCellStyle(cellStardard);
					lmCell.setCellValue(levelModel.getBo_num());
					// 三天BO数
					lmCell = lmRow.createCell(9);
					lmCell.setCellStyle(cellStardard);
					if (levelModel.getBo3days_num() != null)
						lmCell.setCellValue(levelModel.getBo3days_num());
					// 等级型号拉动台数
					lmCell = lmRow.createCell(10);
					lmCell.setCellStyle(cellStardard);
					if (levelModel.getLd_num() != null) 
						lmCell.setCellValue(levelModel.getLd_num());
					else
						lmCell.setCellValue(" - ");
					String sML = levelModel.getModel_name() + "|" + levelModel.getLevel(); 

					// 历史平均同意数
					lmCell = lmRow.createCell(11);
					lmCell.setCellStyle(cellStardard);
					if (cacheOfAgreeHistoryML.containsKey(sML)) {
						lmCell.setCellValue(cacheOfAgreeHistoryML.get(sML).setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue());
					} else {
						levelModel.setWorkday(periodDays);
						BigDecimal agreeHistoryML = porMapper.getAgreeHistoryML(levelModel);
						lmCell.setCellValue(agreeHistoryML.setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue());
						cacheOfAgreeHistoryML.put(sML, agreeHistoryML);
					}
					// 本期单日订购峰值
					lmCell = lmRow.createCell(12);
					lmCell.setCellStyle(cellRight);
					if (cacheOfOrderHillML.containsKey(sML)) {
						lmCell.setCellValue(cacheOfOrderHillML.get(sML));
					} else {
						levelModel.setOrder_date_start(period[0]);
						levelModel.setOrder_date_end(period[1]);
						String orderHillML = porMapper.getOrderHillML(levelModel);
						lmCell.setCellValue(orderHillML);
						cacheOfOrderHillML.put(sML, orderHillML);
					}
					// 不良追加订购次数
					lmCell = lmRow.createCell(13);
					lmCell.setCellStyle(cellStardard);
					if (cacheOfNoGoodTimesML.containsKey(sML)) {
						lmCell.setCellValue(cacheOfNoGoodTimesML.get(sML));
					} else {
						levelModel.setOrder_date_start(period[0]);
						levelModel.setOrder_date_end(period[1]);
						Integer noGoodTimesML = porMapper.getNoGoodTimesML(levelModel);
						lmCell.setCellValue(noGoodTimesML);
						cacheOfNoGoodTimesML.put(sML, noGoodTimesML);
					}
				}
				// 合并单元格
				CellRangeAddress region = new CellRangeAddress(factLine - subNo - 2, factLine - 1, 0, 0); // int firstRow, int lastRow, int firstCol, int lastCol
				pSheet.addMergedRegion(region);
				region = new CellRangeAddress(factLine - subNo - 2, factLine - subNo - 2, 2, 6);
				pSheet.addMergedRegion(region);
				region = new CellRangeAddress(factLine - subNo - 2, factLine - 1, 1, 1);
				pSheet.addMergedRegion(region);
				region = new CellRangeAddress(factLine - subNo - 1, factLine - 1, 2, 2);
				pSheet.addMergedRegion(region);
			}

			// 删除标题模板
			work.removeSheetAt(3);

			// 总览工作表
			// 统计结果
			HSSFRow sRow = summarySheet.getRow(3);
			sRow.getCell(2).setCellValue(period[0]);
			sRow = summarySheet.getRow(4);
			sRow.getCell(2).setCellValue(period[1]);
			sRow = summarySheet.getRow(5);
			sRow.getCell(2).setCellValue(ttlRecept);
			sRow = summarySheet.getRow(6);
			sRow.getCell(2).setCellValue(ttlAgreed);
			sRow = summarySheet.getRow(7);
			sRow.getCell(2).setCellValue(ttlOrder);
			sRow = summarySheet.getRow(8);
			sRow.getCell(2).setCellValue(ttlBoMaterial);
			sRow = summarySheet.getRow(9);
			sRow.getCell(2).setCellValue(ttlBoPartial);

			sRow = summarySheet.getRow(10);
			if (ttlOrder > 0) {
				sRow.getCell(2).setCellValue((double)ttlBoMaterial / ttlOrder);
			}
			sRow = summarySheet.getRow(11);
			if (ttlOrder > 0) {
				sRow.getCell(2).setCellValue((double)ttlBo3DaysMaterial / ttlOrder);
			}

			// 检索条件
			partialOrderRecordForm.setPartial_code(orgPartial_code);
			int condRowNo = 16;
			HSSFRow sampRow = summarySheet.getRow(condRowNo);
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getOrder_date_start(), "零件订购日开始", condRowNo, summarySheet, sampRow);
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getOrder_date_end(), "零件订购日终了", condRowNo, summarySheet, sampRow);
			// 维修对象机种
			String category_ids = partialOrderRecordForm.getCategory_id();
			String category_names = "";
			CategoryMapper cdao = conn.getMapper(CategoryMapper.class);
			if (!isEmpty(category_ids)) {
				String[] category_id_array = category_ids.split(",");
				for (String category_id : category_id_array) {
					category_names += cdao.getCategoryByID(category_id).getName() + " ";
				}
				condRowNo = checkAndAddCondition(category_names, "维修对象机种", condRowNo, summarySheet, sampRow);
			}
			// 型号
			if (!isEmpty(orgModelId)) {
				ModelMapper mdao = conn.getMapper(ModelMapper.class);
				String model_names = mdao.getModelByID(orgModelId).getName();
				condRowNo = checkAndAddCondition(model_names, "维修对象型号", condRowNo, summarySheet, sampRow);
				condRowNo = checkAndAddCondition(CodeListUtils.getValue("material_level", orgLevel), "等级", condRowNo, summarySheet, sampRow);
			}

			// 客户同意日
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getAgreed_date_start(), "客户同意日开始", condRowNo, summarySheet, sampRow);
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getAgreed_date_end(), "客户同意日终了", condRowNo, summarySheet, sampRow);
			// 总组出货日
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getFinish_date_start(), "总组出货日开始", condRowNo, summarySheet, sampRow);
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getFinish_date_end(), "总组出货日终了", condRowNo, summarySheet, sampRow);
			// 零件到货日
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getArrival_date_start(), "零件到货日开始", condRowNo, summarySheet, sampRow);
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getArrival_date_end(), "零件到货日终了", condRowNo, summarySheet, sampRow);
			// 入库预定日
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getArrival_plan_date_start(), "入库预定日开始", condRowNo, summarySheet, sampRow);
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getArrival_plan_date_end(), "入库预定日终了", condRowNo, summarySheet, sampRow);

			String echelons = partialOrderRecordForm.getEchelon();
			String echelon_names = "";
			if (!isEmpty(echelons)) {
				String[] echelon_array = echelons.split(",");
				for (String echelon : echelon_array) {
					echelon_names += CodeListUtils.getValue("echelon_code", echelon) + " ";
				}
				condRowNo = checkAndAddCondition(echelon_names, "梯队", condRowNo, summarySheet, sampRow);
			}
			condRowNo = checkAndAddCondition(partialOrderRecordForm.getPartial_code(), "零件编号", condRowNo, summarySheet, sampRow);

			out= new FileOutputStream(cachePath);
			work.write(out);
		}catch(Exception e){
			throw e;
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}

		return cacheName;
	}

	private int checkAndAddCondition(String condition, String title, int condRowNo, HSSFSheet summarySheet, HSSFRow sampRow) {
		if (!isEmpty(condition)) {
			HSSFRow sRow = summarySheet.createRow(condRowNo++);
			CopyByPoi.copyRow(sampRow, sRow, false);
			sRow.getCell(1).setCellValue(title);
			sRow.getCell(2).setCellValue(condition);
			CellRangeAddress region = new CellRangeAddress(condRowNo, condRowNo, 2, 7); // int firstRow, int lastRow, int firstCol, int lastCol
			summarySheet.addMergedRegion(region);
		}
		return condRowNo;
	}

	public List<PartialOrderRecordForm> searchPartialsOnForeboard(ActionForm form, Integer periodDays, SqlSession conn) {
		PartialOrderRecordEntity entity = new PartialOrderRecordEntity();
		BeanUtil.copyToBean(form, entity, null);
		// 多选检索条件
		entity.setCategory_id(entity.getCategory_id().replaceAll(",", "','"));
		entity.setEchelon(entity.getEchelon().replaceAll(",", "','"));
		entity.setWorkday(periodDays);

		PartialOrderRecordMapper dao = conn.getMapper(PartialOrderRecordMapper.class);

		List<PartialOrderRecordEntity> lResultBean = null;

		if ((entity.getTop_stick_bo_lt() != null && entity.getTop_stick_bo_lt())
				|| (entity.getTop_stick_high() != null && entity.getTop_stick_high()) 
				|| (entity.getTop_stick_low() != null && entity.getTop_stick_low())) {

			// 置顶的查询排序
			lResultBean = dao.searchPartialsOnForeboardTopStick(entity);
			
		} else {

			// 标准的查询排序
			lResultBean = dao.searchPartialsOnForeboard(entity);
			
		}
	
		List<PartialOrderRecordForm> lResultForm = new ArrayList<PartialOrderRecordForm>();
		
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, PartialOrderRecordForm.class);
		
		return lResultForm;
	}

	public void getChartPoint(String partial_id, Calendar cal, Calendar now, List<String> weekCategories,
			List<BigDecimal> weekConsumeRates, SqlSession conn) {
		Calendar endCal = Calendar.getInstance();

		Set<String> bussinessYears = new HashSet<String>();
		Set<String> months = new HashSet<String>();
		while (cal.compareTo(now) < 0) {
			endCal.setTimeInMillis(cal.getTimeInMillis());
			endCal.add(Calendar.DATE, 7);

			String categoryText = "";
			String bussinessYear = RvsUtils.getBussinessYearString(endCal);
			if (!bussinessYears.contains(bussinessYear)) {
				bussinessYears.add(bussinessYear);
				categoryText += bussinessYear + " ";
			}
			String month = (endCal.get(Calendar.MONTH) + 1) + "月";
			if (!months.contains(month)) {
				months.add(month);
				categoryText += " " + month;
			}

			PartialOrderRecordMapper mapper = conn.getMapper(PartialOrderRecordMapper.class);

			PartialOrderRecordEntity condition = new PartialOrderRecordEntity();
			condition.setPartial_id(partial_id);
			condition.setOrder_date_start(cal.getTime());
			if (endCal.compareTo(now) < 0) {
				condition.setOrder_date_end(endCal.getTime());
			} else {
				condition.setOrder_date_end(now.getTime());
			}

			PartialOrderRecordEntity ret = mapper.getTurnroundRateOfPartialInuse(condition);

			weekCategories.add(("".equals(categoryText)) ? "　" : categoryText);
			if (ret.getTurnround_rate() != null) {
				weekConsumeRates.add(ret.getTurnround_rate().multiply(new BigDecimal(100)));
			} else {
				weekConsumeRates.add(null);
			}

			cal.setTimeInMillis(endCal.getTimeInMillis());
		}
	}

	public void getChartPoint4Supply(String partial_id, Calendar cal, Calendar now, Map<String, Object> callbackResponse, SqlSession conn) {
		Calendar endCal = Calendar.getInstance();

		List<String> weekCategories = new ArrayList<String>();
		List<BigDecimal> weekConsumeRates = new ArrayList<BigDecimal>();
		List<BigDecimal> weekConsumeRatesOsh = new ArrayList<BigDecimal>();
		List<BigDecimal> weekConsumeRatesOgz = new ArrayList<BigDecimal>();

		Set<String> bussinessYears = new HashSet<String>();
		Set<String> months = new HashSet<String>();

		PartialOrderRecordMapper mapper = conn.getMapper(PartialOrderRecordMapper.class);

		// 月平均
		Calendar cal3 = Calendar.getInstance();
		cal3.setTimeInMillis(cal.getTimeInMillis());
		cal3.add(Calendar.MONTH, 3);
		Calendar calM = Calendar.getInstance();
		calM.setTimeInMillis(now.getTimeInMillis());
		calM.set(Calendar.DAY_OF_MONTH, 1);
		//   6个月
		PartialOrderRecordEntity condition = new PartialOrderRecordEntity();
		condition.setPartial_id(partial_id);
		condition.setOrder_date_start(cal.getTime());
		condition.setOrder_date_end(now.getTime());
		PartialOrderRecordEntity ret = mapper.getTurnroundRateOfPartialSupply(condition);
		if (ret.getTurnround_rate() != null) {
			callbackResponse.put("count6", "总:" + ret.getTurnround_rate().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString() + " %<br>"
					+ "OSH:" + ret.getOsh_turnround_rate().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString() + " % "
//					+ "OGZ:" + ret.getOgz_turnround_rate().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString() + " %"
					);
		} else {
			callbackResponse.put("count6", null);
		}
		//   3个月
		condition.setOrder_date_start(cal3.getTime());
		ret = mapper.getTurnroundRateOfPartialSupply(condition);
		if (ret.getTurnround_rate() != null) {
			callbackResponse.put("count3", "总:" + ret.getTurnround_rate().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString() + " %<br>"
					+ "OSH:" + ret.getOsh_turnround_rate().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString() + " % "
//					+ "OGZ:" + ret.getOgz_turnround_rate().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString() + " %"
					);
		} else {
			callbackResponse.put("count3", null);
		}
		//   本月
		condition.setOrder_date_start(calM.getTime());
		ret = mapper.getTurnroundRateOfPartialSupply(condition);
		if (ret.getTurnround_rate() != null) {
			callbackResponse.put("countM", "总:" + ret.getTurnround_rate().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString() + " %<br>"
					+ "OSH:" + ret.getOsh_turnround_rate().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString() + " % "
//					+ "OGZ:" + ret.getOgz_turnround_rate().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString() + " %"
					);
		} else {
			callbackResponse.put("countM", null);
		}

		// 最新设定区域
		Integer availbleArea = null;
		PartialBaseLineValueMapping pblvMapper = conn.getMapper(PartialBaseLineValueMapping.class);
		Date avalibleDate = pblvMapper.getNewestDateOfOsh(partial_id);
		if (avalibleDate != null && avalibleDate.getTime() < cal.getTimeInMillis()) {
			avalibleDate = null;
		}

		int iCnt = 0;
		// 周平均
		while (cal.compareTo(now) < 0) {

			endCal.setTimeInMillis(cal.getTimeInMillis());
			endCal.add(Calendar.DATE, 7);
			if (avalibleDate != null && availbleArea == null) {
				if (avalibleDate.getTime() <= endCal.getTimeInMillis()
						&& avalibleDate.getTime() > cal.getTimeInMillis()) {
					availbleArea = iCnt;
				}
			}

			iCnt++;

			String categoryText = "";
			String bussinessYear = RvsUtils.getBussinessYearString(endCal);
			if (!bussinessYears.contains(bussinessYear)) {
				bussinessYears.add(bussinessYear);
				categoryText += bussinessYear + " ";
			}
			String month = (endCal.get(Calendar.MONTH) + 1) + "月";
			if (!months.contains(month)) {
				months.add(month);
				categoryText += " " + month;
			}

			condition.setOrder_date_start(cal.getTime());
			if (endCal.compareTo(now) < 0) {
				condition.setOrder_date_end(endCal.getTime());
			} else {
				condition.setOrder_date_end(now.getTime());
			}

			weekCategories.add(("".equals(categoryText)) ? "　" : categoryText);

			ret = mapper.getTurnroundRateOfPartialSupply(condition);
			if (ret == null) {
				weekConsumeRates.add(null);
				weekConsumeRatesOsh.add(null);
				weekConsumeRatesOgz.add(null);
			} else {
				if (ret.getTurnround_rate() != null) {
					weekConsumeRates.add(ret.getTurnround_rate().multiply(new BigDecimal(100)));
				} else {
					weekConsumeRates.add(null);
				}

				if (ret.getOsh_turnround_rate() != null) {
					weekConsumeRatesOsh.add(ret.getOsh_turnround_rate().multiply(new BigDecimal(100)));
				} else {
					weekConsumeRatesOsh.add(null);
				}

				if (ret.getOgz_turnround_rate() != null) {
					weekConsumeRatesOgz.add(ret.getOgz_turnround_rate().multiply(new BigDecimal(100)));
				} else {
					weekConsumeRatesOgz.add(null);
				}

			}
			
			cal.setTimeInMillis(endCal.getTimeInMillis());
		}

		callbackResponse.put("availbleArea", availbleArea);

		callbackResponse.put("weekCategories", weekCategories);
		callbackResponse.put("weekConsumeRates", weekConsumeRates);
		callbackResponse.put("weekConsumeRatesOsh", weekConsumeRatesOsh);
		callbackResponse.put("weekConsumeRatesOgz", weekConsumeRatesOgz);
	}


	public List<PartialOrderRecordForm> searchPartialsOutOfForeboard(ActionForm form, Integer periodDays, SqlSession conn) {
		PartialOrderRecordEntity entity = new PartialOrderRecordEntity();
		BeanUtil.copyToBean(form, entity, null);
		// 多选检索条件
		entity.setCategory_id(entity.getCategory_id().replaceAll(",", "','"));
		entity.setEchelon(entity.getEchelon().replaceAll(",", "','"));
		entity.setWorkday(periodDays);
		entity.setBase_setting(1);

		PartialOrderRecordMapper dao = conn.getMapper(PartialOrderRecordMapper.class);
		
		List<PartialOrderRecordEntity> lResultBean = dao.searchPartials(entity);
	
		List<PartialOrderRecordForm> lResultForm = new ArrayList<PartialOrderRecordForm>();
		
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, PartialOrderRecordForm.class);
		
		return lResultForm;
	}

	/**
	 * 零件基准值设定监控
	 * @author lxb
	 *
	 */
	public String createPartialMonitorReport(HttpServletRequest request){
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "零件基准设定监控模板.xls";
		String cacheName ="零件基准设定监控" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		@SuppressWarnings("unchecked")
		List<PartialOrderRecordForm> resultList=(List<PartialOrderRecordForm>)request.getSession().getAttribute("importRresult");
		
		OutputStream out = null;
		InputStream in = null;
		try{
			in = new FileInputStream(cachePath);//读取文件 
			HSSFWorkbook work=new HSSFWorkbook(in);//创建xls文件
			HSSFSheet sheet=work.getSheetAt(0);//取得第一个Sheet
			
			if(resultList!=null){
				int index=1;
				PartialOrderRecordForm tempForm=null;
				
				HSSFFont font=work.createFont();
				font.setFontHeightInPoints((short)10);
				font.setFontName("微软雅黑");
				
				HSSFFont fontWarn=work.createFont();
				fontWarn.setFontHeightInPoints((short)10);
				fontWarn.setFontName("微软雅黑");
				fontWarn.setColor(HSSFColor.RED.index);
				
				HSSFCellStyle borderStyle = work.createCellStyle();
				borderStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				borderStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				borderStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				borderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				borderStyle.setFont(font);
				borderStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				
				HSSFCellStyle cellDigitStyle = work.createCellStyle();    
				cellDigitStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				cellDigitStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				cellDigitStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				cellDigitStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				cellDigitStyle.setFont(font);
				cellDigitStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				
				HSSFCellStyle warningStyle = work.createCellStyle();
				warningStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				warningStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				warningStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				warningStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				warningStyle.setFont(fontWarn);
				warningStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);

				HSSFCellStyle overDigitStyle = work.createCellStyle();    
				overDigitStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				overDigitStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				overDigitStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				overDigitStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				overDigitStyle.setFont(font);
				overDigitStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				overDigitStyle.setFillBackgroundColor(HSSFColor.RED.index);
								
				for(int i=0;i<resultList.size();i++){
					tempForm=resultList.get(i);
					HSSFRow row=sheet.createRow(index);
					index++;

					String bo3DaysNnum=tempForm.getBo3days_num(); // 3天BO发生数
					String turnroundRate=tempForm.getTurnround_rate(); // 安全库存
					String baseSetting=tempForm.getBase_setting(); // 消耗速率

					int iBo3DaysNnum = 0;
					double dTurnroundRate = 0;
					int iBaseSetting = 0;

					try {
						iBo3DaysNnum = Integer.parseInt(bo3DaysNnum);
						iBaseSetting = Integer.parseInt(baseSetting);
						dTurnroundRate = Double.parseDouble(turnroundRate);
					} catch(Exception e) {
					}

					row.createCell(0).setCellValue(i+1);//编号
					row.getCell(0).setCellStyle(borderStyle);

					row.createCell(1).setCellValue(tempForm.getPartial_code());//零件编码

					row.createCell(2).setCellValue(tempForm.getPartial_name());//零件名称
					if (iBo3DaysNnum > 0 || 
							(iBaseSetting > 0 && (dTurnroundRate > 100))) { // TODO
						row.getCell(1).setCellStyle(warningStyle);
						row.getCell(2).setCellStyle(warningStyle);
					} else {
						row.getCell(1).setCellStyle(borderStyle);
						row.getCell(2).setCellStyle(borderStyle);
					}
					
					row.createCell(3).setCellValue(tempForm.getOrder_num());//期间订购数
					row.getCell(3).setCellStyle(cellDigitStyle);
					
					row.createCell(4).setCellValue(tempForm.getOrder_num1());//第一梯队购数
					row.getCell(4).setCellStyle(cellDigitStyle);
					
					row.createCell(5).setCellValue(tempForm.getOrder_num2());//第二梯队购数
					row.getCell(5).setCellStyle(cellDigitStyle);
					
					row.createCell(6).setCellValue(tempForm.getOrder_num3());//第三梯队购数
					row.getCell(6).setCellStyle(cellDigitStyle);
					
					row.createCell(7).setCellValue(tempForm.getOrder_num4());//第四梯队购数
					row.getCell(7).setCellStyle(cellDigitStyle);
					
					String boNum=tempForm.getBase_num();
					if(CommonStringUtil.isEmpty(boNum)){
						row.createCell(8).setCellValue(0);//BO发生数
					}else{
						row.createCell(8).setCellValue(boNum);//BO发生数
					}
					row.getCell(8).setCellStyle(cellDigitStyle);
					
					String boRate=tempForm.getBo_rate();
					if(CommonStringUtil.isEmpty(boRate)){
						row.createCell(9).setCellType(HSSFCell.CELL_TYPE_BLANK);//BO发生率
					}else{
						row.createCell(9).setCellValue(boRate+"%");//BO发生率
					}
					row.getCell(9).setCellStyle(cellDigitStyle);
					
					if(CommonStringUtil.isEmpty(bo3DaysNnum)){
						row.createCell(10).setCellValue(0);//3天BO发生数
					}else{
						row.createCell(10).setCellValue(iBo3DaysNnum);//3天BO发生数
					}
					if (iBo3DaysNnum > 0) {
						row.getCell(10).setCellStyle(overDigitStyle);
					} else {
						row.getCell(10).setCellStyle(cellDigitStyle);
					}
					
					String bo3DaysRate=tempForm.getBo3days_rate();
					if(CommonStringUtil.isEmpty(bo3DaysRate)){
						row.createCell(11).setCellType(HSSFCell.CELL_TYPE_BLANK);//3天BO发生率
					}else{
						row.createCell(11).setCellValue(bo3DaysRate+"%");//3天BO发生率
					}
					row.getCell(11).setCellStyle(cellDigitStyle);
					
					if(CommonStringUtil.isEmpty(baseSetting)){
						row.createCell(12).setCellType(HSSFCell.CELL_TYPE_BLANK);//安全库存
					}else{
						row.createCell(12).setCellValue(iBaseSetting);//安全库存
					}
					row.getCell(12).setCellStyle(cellDigitStyle);
					
					String baseNum=tempForm.getBase_num();
					if(CommonStringUtil.isEmpty(baseNum)){
						row.createCell(13).setCellType(HSSFCell.CELL_TYPE_BLANK);//期间折算基准值
					}else{
						row.createCell(13).setCellValue(baseNum);//期间折算基准值
					}
					row.getCell(13).setCellStyle(cellDigitStyle);
					
					if(CommonStringUtil.isEmpty(turnroundRate)){
						row.createCell(14).setCellType(HSSFCell.CELL_TYPE_BLANK);//消耗速率
					}else{
						row.createCell(14).setCellValue(turnroundRate+"%");//消耗速率
					}
					if (iBaseSetting > 0 && (dTurnroundRate > 100)) { // TODO
						row.getCell(14).setCellStyle(overDigitStyle);
					} else {
						row.getCell(14).setCellStyle(cellDigitStyle);
					}

				}
			}
			
			out= new FileOutputStream(cachePath);
			work.write(out);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//request.getSession().removeAttribute("importRresult");
		}
			
		return cacheName;
	}
	
	public String dowload(HttpServletRequest request){
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "零件基准设定监控模板.xls";
		String cacheName ="零件基准设定监控" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		@SuppressWarnings("unchecked")
		List<PartialOrderRecordForm> resultList=(List<PartialOrderRecordForm>)request.getSession().getAttribute("importRresult");
		String strl_high=(String)request.getSession().getAttribute("strl_high");//消耗率高于警报线
		String strl_low=(String)request.getSession().getAttribute("strl_low");//消耗率低于警报线
		
		Integer l_hign=null;
		Integer l_low=null;
		try{
			l_hign=Integer.parseInt(strl_high);
			l_low=Integer.parseInt(strl_low);
		}catch(Exception e){
		}
		
		OutputStream out = null;
		InputStream in = null;
		try{
			in = new FileInputStream(cachePath);//读取文件 
			HSSFWorkbook work=new HSSFWorkbook(in);//创建xls文件
			HSSFSheet sheet=work.getSheetAt(0);//取得第一个Sheet
			
			
			if(resultList!=null){
				int index=1;
				PartialOrderRecordForm tempForm=null;
				
				HSSFFont font=work.createFont();
				font.setFontHeightInPoints((short)10);
				font.setFontName("微软雅黑");
				
				HSSFFont fontWarn=work.createFont();
				fontWarn.setFontHeightInPoints((short)10);
				fontWarn.setFontName("微软雅黑");
				fontWarn.setColor(HSSFColor.RED.index);
				
				//基本样式
				HSSFCellStyle borderStyle = work.createCellStyle();
				borderStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				borderStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				borderStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				borderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				borderStyle.setFont(font);
				borderStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				
				//数字样式
				HSSFCellStyle cellDigitStyle = work.createCellStyle();   
				cellDigitStyle.cloneStyleFrom(borderStyle);
				cellDigitStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				cellDigitStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
				
				//警告样式
				HSSFCellStyle warningStyle = work.createCellStyle();
				warningStyle.cloneStyleFrom(borderStyle);
				warningStyle.setFont(fontWarn);
				
				//百分比样式
				HSSFCellStyle perStyle=work.createCellStyle();
				perStyle.cloneStyleFrom(borderStyle);
				perStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				perStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
				
				HSSFPalette palette = work.getCustomPalette();//创建调色板
				palette.setColorAtIndex((short)60, (byte)(255), (byte)(0), (byte)(0));
				palette.setColorAtIndex((short)61, (byte)(255), (byte)(255), (byte)(0));
				
				//货币样式
				HSSFCellStyle currencyStyle=work.createCellStyle();
				currencyStyle.cloneStyleFrom(borderStyle);
				currencyStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				HSSFDataFormat currencyFormat=work.createDataFormat();
				currencyStyle.setDataFormat(currencyFormat.getFormat("$ 0.00"));
				
				
				//红颜色背景
				HSSFCellStyle overDigitStyle = work.createCellStyle();  
				overDigitStyle.cloneStyleFrom(borderStyle);
				overDigitStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				overDigitStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				overDigitStyle.setFillForegroundColor((short)60);
				overDigitStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
				
				//黄颜色背景
				HSSFCellStyle overDigitStyleYellow = work.createCellStyle();  
				overDigitStyleYellow.cloneStyleFrom(borderStyle);
				overDigitStyleYellow.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				overDigitStyleYellow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				overDigitStyleYellow.setFillForegroundColor((short)61);
				overDigitStyleYellow.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
				
				for(int i=0;i<resultList.size();i++){
					tempForm=resultList.get(i);
					HSSFRow row=sheet.createRow(index);//创建行
					index++;
					
					String baseSetting=tempForm.getBase_setting(); //安全库存
					String strSorcwhTurnroundRate=tempForm.getOsh_turnround_rate();//SORCWH消耗速率
					String strWh2pTurnroundRate=tempForm.getWh2p_turnround_rate();//WH2P消耗速率
					String strOgzTurnroundRate=tempForm.getOgz_turnround_rate();// OGZ消耗速率
					String strTurnroundRate=tempForm.getTurnround_rate(); // 总计消耗速率
					
					int iBaseSetting = 0;
					double sorcwhTurnroundRate=0;
					double wh2pTurnroundRate=0;
					double ogzTurnroundRate=0;
					double turnroundRate = 0;
					
					try{
						if(!CommonStringUtil.isEmpty(baseSetting)){
							iBaseSetting=Integer.parseInt(baseSetting);
						}
						if(!CommonStringUtil.isEmpty(strSorcwhTurnroundRate)){
							sorcwhTurnroundRate=Double.parseDouble(strSorcwhTurnroundRate);
						}
						if(!CommonStringUtil.isEmpty(strWh2pTurnroundRate)){
							wh2pTurnroundRate=Double.parseDouble(strWh2pTurnroundRate);
						}
						if(!CommonStringUtil.isEmpty(strOgzTurnroundRate)){
							ogzTurnroundRate=Double.parseDouble(strOgzTurnroundRate);
						}
						if(!CommonStringUtil.isEmpty(strTurnroundRate)){
							turnroundRate=Double.parseDouble(strTurnroundRate);
						}
					}catch(Exception e){
					}
					
					row.createCell(0).setCellValue(i+1);//编号
					row.getCell(0).setCellStyle(borderStyle);
					
					boolean sorcflg=false;
					boolean wh2pflg=false;
					boolean ogzflg=false;
					boolean totalflg=false;
					
					row.createCell(1).setCellValue(tempForm.getPartial_code());//零件编码
					if(iBaseSetting>0){
						if(l_hign!=null && l_low!=null){
							if(!CommonStringUtil.isEmpty(strSorcwhTurnroundRate)){
								if(sorcwhTurnroundRate < l_low || sorcwhTurnroundRate > l_hign){
									sorcflg=true;
								}else{
									sorcflg=false;
								}
							}
							if(!CommonStringUtil.isEmpty(strWh2pTurnroundRate)){
								if(wh2pTurnroundRate < l_low || wh2pTurnroundRate > l_hign){
									wh2pflg=true;
								}else{
									wh2pflg=false;
								}
							}
							if(!CommonStringUtil.isEmpty(strOgzTurnroundRate)){
								if(ogzTurnroundRate < l_low || ogzTurnroundRate > l_hign){
									ogzflg=true;
								}else{
									ogzflg=false;
								}
							}
							if(!CommonStringUtil.isEmpty(strTurnroundRate)){
								if(turnroundRate < l_low || turnroundRate > l_hign){
									totalflg=true;
								}else{
									totalflg=false;
								}
							}
							if(sorcflg==true || wh2pflg==true || ogzflg==true || totalflg==true){
								row.getCell(1).setCellStyle(warningStyle);
							}else{
								row.getCell(1).setCellStyle(borderStyle);
							}
						}else{
							row.getCell(1).setCellStyle(borderStyle);
						}
					}else{
						row.getCell(1).setCellStyle(borderStyle);
					}
					
					row.createCell(2).setCellValue(tempForm.getPartial_name());//零件名称
					row.getCell(2).setCellStyle(borderStyle);
					
					String consumable_flg=tempForm.getConsumable_flg();//消耗品
					if("1".equals(consumable_flg)){
						row.createCell(3).setCellValue("消耗品");
					}else{
						row.createCell(3);
					}
					row.getCell(3).setCellStyle(borderStyle);
					
					row.createCell(4).setCellValue(Integer.parseInt(tempForm.getOrder_num1()));//SORCWH期间补充数
					row.getCell(4).setCellStyle(cellDigitStyle);
					row.getCell(4).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					
					String sorcwhForeboardCount=tempForm.getSorcwh_foreboard_count();//SORCWH安全库存
					if(CommonStringUtil.isEmpty(sorcwhForeboardCount)){
						row.createCell(5).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(5).setCellValue(Integer.parseInt(sorcwhForeboardCount));
						row.getCell(5).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(5).setCellStyle(cellDigitStyle);
					
					String orderNum4=tempForm.getOrder_num4();//期间折算基准值
					if(CommonStringUtil.isEmpty(orderNum4)){
						row.createCell(6).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(6).setCellValue(Integer.parseInt(orderNum4));
						row.getCell(6).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(6).setCellStyle(cellDigitStyle);
					
					if(CommonStringUtil.isEmpty(strSorcwhTurnroundRate)){//SORCWH消耗速率
						row.createCell(7).setCellType(HSSFCell.CELL_TYPE_BLANK);
						row.getCell(7).setCellStyle(cellDigitStyle);
					}else{
						row.createCell(7).setCellValue(Double.parseDouble(strSorcwhTurnroundRate)/100);
						row.getCell(7).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						if(iBaseSetting>0){
							if(l_hign!=null && l_low!=null){
								if(sorcwhTurnroundRate < l_low || sorcwhTurnroundRate > l_hign){
									row.getCell(7).setCellStyle(overDigitStyleYellow);
								}else{
									row.getCell(7).setCellStyle(perStyle);
								}
							}else{
								row.getCell(7).setCellStyle(perStyle);
							}
						}else{
							row.getCell(7).setCellStyle(perStyle);
						}
					}
					
					row.createCell(8).setCellValue(Integer.parseInt(tempForm.getOrder_num3()));//WH2P期间补充数
					row.getCell(8).setCellStyle(cellDigitStyle);
					row.getCell(8).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					
					String wh2pForeboardCount=tempForm.getWh2p_foreboard_count();//WH2P安全库存
					if(CommonStringUtil.isEmpty(wh2pForeboardCount)){
						row.createCell(9).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(9).setCellValue(Integer.parseInt(wh2pForeboardCount));
						row.getCell(9).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(9).setCellStyle(cellDigitStyle);
					
					String consumableForeboardCount=tempForm.getConsumable_foreboard_count();//消耗品仓库安全库存
					if(CommonStringUtil.isEmpty(consumableForeboardCount)){
						row.createCell(10).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(10).setCellValue(Integer.parseInt(consumableForeboardCount));
						row.getCell(10).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(10).setCellStyle(cellDigitStyle);
					
					String orderNum5=tempForm.getOrder_num5();//期间折算基准值
					if(CommonStringUtil.isEmpty(orderNum5)){
						row.createCell(11).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(11).setCellValue(Integer.parseInt(orderNum5));
						row.getCell(11).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(11).setCellStyle(cellDigitStyle);
					
					if(CommonStringUtil.isEmpty(strWh2pTurnroundRate)){//WH2P消耗速率
						row.createCell(12).setCellType(HSSFCell.CELL_TYPE_BLANK);
						row.getCell(12).setCellStyle(cellDigitStyle);
					}else{
						row.createCell(12).setCellValue(Double.parseDouble(strWh2pTurnroundRate)/100);
						row.getCell(12).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						if(iBaseSetting>0){
							if(l_hign!=null && l_low!=null){
								if(wh2pTurnroundRate < l_low || wh2pTurnroundRate > l_hign){
									row.getCell(12).setCellStyle(overDigitStyleYellow);
								}else{
									row.getCell(12).setCellStyle(perStyle);
								}
							}else{
								row.getCell(12).setCellStyle(perStyle);
							}
						}else{
							row.getCell(12).setCellStyle(perStyle);
						}
					}
					
					row.createCell(13).setCellValue(Integer.parseInt(tempForm.getOrder_num2()));//OGZ期间补充书
					row.getCell(13).setCellStyle(cellDigitStyle);
					row.getCell(13).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					
					String ogzForeboardCount=tempForm.getOgz_foreboard_count();//OGZ安全库存
					if(CommonStringUtil.isEmpty(ogzForeboardCount)){
						row.createCell(14).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(14).setCellValue(Integer.parseInt(ogzForeboardCount));
						row.getCell(14).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(14).setCellStyle(cellDigitStyle);
					
					String orderNum7=tempForm.getOrder_num7();//期间折算基准值
					if(CommonStringUtil.isEmpty(orderNum7)){
						row.createCell(15).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(15).setCellValue(Integer.parseInt(orderNum7));
						row.getCell(15).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(15).setCellStyle(cellDigitStyle);
					
					if(CommonStringUtil.isEmpty(strOgzTurnroundRate)){//OGZ消耗速率
						row.createCell(16).setCellType(HSSFCell.CELL_TYPE_BLANK);
						row.getCell(16).setCellStyle(cellDigitStyle);
					}else{
						row.createCell(16).setCellValue(Double.parseDouble(strOgzTurnroundRate)/100);
						row.getCell(16).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						if(iBaseSetting>0){
							if(l_hign!=null && l_low!=null){
								if(ogzTurnroundRate < l_low || ogzTurnroundRate > l_hign){
									row.getCell(16).setCellStyle(overDigitStyleYellow);
								}else{
									row.getCell(16).setCellStyle(perStyle);
								}
							}else{
								row.getCell(16).setCellStyle(perStyle);
							}
						}else{
							row.getCell(16).setCellStyle(perStyle);
						}
					}
					
					
					String orderNum=tempForm.getOrder_num();//期间补充数
					if(CommonStringUtil.isEmpty(orderNum)){
						row.createCell(17).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(17).setCellValue(Integer.parseInt(orderNum));
						row.getCell(17).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(17).setCellStyle(cellDigitStyle);
					
					if(CommonStringUtil.isEmpty(baseSetting)){//安全库存
						row.createCell(18).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(18).setCellValue(Integer.parseInt(baseSetting));
						row.getCell(18).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(18).setCellStyle(cellDigitStyle);
					
					String baseNum=tempForm.getBase_num();//期间折算基准值
					if(CommonStringUtil.isEmpty(baseNum)){
						row.createCell(19).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(19).setCellValue(Integer.parseInt(baseNum));
						row.getCell(19).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(19).setCellStyle(cellDigitStyle);
					
					if(CommonStringUtil.isEmpty(strTurnroundRate)){//总计消耗速率
						row.createCell(20).setCellType(HSSFCell.CELL_TYPE_BLANK);
						row.getCell(20).setCellStyle(cellDigitStyle);
					}else{
						row.createCell(20).setCellValue(Double.parseDouble(strTurnroundRate)/100);
						row.getCell(20).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						if(iBaseSetting>0){
							if(l_hign!=null && l_low!=null){
								if(turnroundRate < l_low || turnroundRate > l_hign){
									row.getCell(20).setCellStyle(overDigitStyle);
								}else{
									row.getCell(20).setCellStyle(perStyle);
								}
							}else{
								row.getCell(20).setCellStyle(perStyle);
							}
						}else{
							row.getCell(20).setCellStyle(perStyle);
						}
					}
					
					String price=tempForm.getPrice();//单价
					if(CommonStringUtil.isEmpty(price)){
						row.createCell(21).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}else{
						row.createCell(21).setCellValue(Double.parseDouble(price));
						row.getCell(21).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}
					row.getCell(21).setCellStyle(currencyStyle);
					
					row.createCell(22).setCellFormula("IF(ISERROR(R"+index+"*V"+index+"),\"-\",R"+index+"*V"+index+")");//总价
					row.getCell(22).setCellStyle(currencyStyle);
					row.getCell(22).setCellType(HSSFCell.CELL_TYPE_FORMULA);
				}
			}
			out= new FileOutputStream(cachePath);
			work.write(out);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//request.getSession().removeAttribute("importRresult");
		}
		
		return cacheName;
	}
	
	
	public List<MonthFilesDownloadForm> getMonthFiles(){
		List<MonthFilesDownloadForm> monthFilesDownloadForms = new ArrayList<MonthFilesDownloadForm>();
		
		String filePath = PathConsts.BASE_PATH+PathConsts.REPORT+"\\partail_bo";
		File file = new File(filePath);
		
		MonthFilesDownloadForm monthFilesDownloadForm;
		//如果该文件是个目录的话
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f:files){
				monthFilesDownloadForm = new MonthFilesDownloadForm();
				if(f.isDirectory()){
					continue;
				}
				
				String fileName = f.getName();
				
				long fileSize = f.length();//文件的大小(字节)
				double ds = (double)fileSize/1024;//文件字节大小/1024所得的就是以kb为单位的大小
				BigDecimal bd = new BigDecimal(ds);
				double resultSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");//文件最后修改时间
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(f.lastModified());
				String fileTime = sdf.format(cal.getTime());
				
				monthFilesDownloadForm.setFile_name(fileName);
				monthFilesDownloadForm.setFile_size(resultSize+"kb");
				monthFilesDownloadForm.setFile_time(fileTime);
				
				monthFilesDownloadForms.add(monthFilesDownloadForm);
			}
		}
		
		//按照文件的最后编辑时间进行倒序排列
		Collections.sort(monthFilesDownloadForms, new Comparator<MonthFilesDownloadForm>() {
			@Override
			public int compare(MonthFilesDownloadForm o1, MonthFilesDownloadForm o2) {
				
				return o2.getFile_time().compareTo(o1.getFile_time());
			}
			
		});
		
		return monthFilesDownloadForms;
	}
	
	
}

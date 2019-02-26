package com.osh.rvs.service.partial;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.ConsumableListEntity;
import com.osh.rvs.bean.partial.ConsumableSupplyRecordEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.partial.ConsumableListForm;
import com.osh.rvs.mapper.partial.ConsumableListMapper;
import com.osh.rvs.mapper.partial.ConsumableSupplyRecordMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.Converter;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.copy.IntegerConverter;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ConsumableListService {
	public List<ConsumableListForm> searchConsumableList(ConsumableListEntity consumableListEntity, SqlSession conn) {
		ConsumableListMapper dao = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListForm> resultForm = new ArrayList<ConsumableListForm>();
		List<ConsumableListEntity> resultList = dao.searchConsumableList(consumableListEntity);
		BeanUtil.copyToFormList(resultList, resultForm, null, ConsumableListForm.class);
		return resultForm;
	}
	public String getcost_rate_alram_belowline(SqlSession conn) {
		ConsumableListMapper dao = conn.getMapper(ConsumableListMapper.class);
		String result = dao.getcost_rate_alram_belowline();
		return result;
	}
	/** 零件集合 **/
	public List<ConsumableListForm> getPartialAutoCompletes(String code, SqlSession conn) {
		ConsumableListMapper dao = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListForm> resultForm = new ArrayList<ConsumableListForm>();
		List<ConsumableListEntity> resultList =dao.getAllPartial(code);
		BeanUtil.copyToFormList(resultList, resultForm, null, ConsumableListForm.class);
		return resultForm;
	}
	/** 盘点数据查询 **/
	public List<ConsumableListForm> getAdjustSearch(String code, SqlSession conn) {
		ConsumableListMapper dao = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListForm> resultForm = new ArrayList<ConsumableListForm>();
		List<ConsumableListEntity> resultList =dao.getAdjustSearch(code);
		BeanUtil.copyToFormList(resultList, resultForm, null, ConsumableListForm.class);
		return resultForm;
	}
	/** 消耗品修正数据修改 **/
	public void doAdjust(ConsumableListEntity consumableListEntity, HttpSession session,SqlSessionManager conn) {
		ConsumableListMapper dao = conn.getMapper(ConsumableListMapper.class);
		dao.doAdjust(consumableListEntity);
	}
	/** 消耗品修正数据记录 **/
	public void doAdjustInsert(ConsumableListEntity consumableListEntity,HttpSession session,SqlSessionManager conn) {
		ConsumableListMapper dao = conn.getMapper(ConsumableListMapper.class);
		dao.doAdjustInsert(consumableListEntity);
	}
	public List<ConsumableListForm> getConsumableDetail(Integer partial_id, SqlSession conn) {
		ConsumableListMapper dao = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListForm> resultForm = new ArrayList<ConsumableListForm>();
		List<ConsumableListEntity> resultList = dao.getConsumableDetail(partial_id);
		BeanUtil.copyToFormList(resultList, resultForm, null, ConsumableListForm.class);
		return resultForm;
	}
	
	/**
	 * 加入消耗品库存
	 * 
	 * @param consumableListEntity
	 * @param conn
	 * @return 如果存在返回消耗品详细信息
	 */
	public void insert(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ConsumableListEntity insertBean = new ConsumableListEntity();
		BeanUtil.copyToBean(form, insertBean, null);

		/* Consumable_manage表插入数据 */
		ConsumableListMapper dao = conn.getMapper(ConsumableListMapper.class);
		dao.insertConsumable(insertBean);


	}
	
	/**
	 * 取得消耗品详细信息
	 * 
	 * @param consumableListEntity
	 * @param conn
	 * @return 如果存在返回消耗品详细信息
	 */
	public List<ConsumableListForm> searchConsumableDetail(
			ConsumableListEntity consumableListEntity, SqlSession conn) {
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListForm> resultForm = new ArrayList<ConsumableListForm>();
		// 零件 ID
		Integer partial_id = consumableListEntity.getPartial_id();
		// 取得消耗品详细信息
		List<ConsumableListEntity> resultList = consumableListMapper.getConsumableDetail(partial_id);
		BeanUtil.copyToFormList(resultList, resultForm, null, ConsumableListForm.class);
		return resultForm;
	}
	
	/**
	 * 消耗品库存设置更新
	 * 
	 * @param request
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 */
	public void updateConsumableManage(HttpServletRequest request,
			ActionForm form, HttpSession session, SqlSessionManager conn,
			List<MsgInfo> errors) {
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		BeanUtil.copyToBean(form, consumableListEntity, null);
		// 更新消耗品管理表
		consumableListMapper.updateConsumableManage(consumableListEntity);
	}
	
	/**
	 * 移出消耗品库存
	 * 
	 * @param request
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 */
	public void delConsumable(HttpServletRequest request, ActionForm form,
			HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) {
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		BeanUtil.copyToBean(form, consumableListEntity, null);
		// 更新消耗品管理表
		consumableListMapper.deleteConsumable(consumableListEntity);
	}
	
	/**
	 * 取得消耗品计量单位信息
	 * 
	 * @param consumableListEntity
	 * @param conn
	 * @return 如果存在返回消耗品计量单位信息
	 */
	public List<ConsumableListForm> searchMeasuringUnit(
			ConsumableListEntity consumableListEntity, SqlSession conn) {
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListForm> resultForm = new ArrayList<ConsumableListForm>();
		// 零件 ID
		Integer partial_id = consumableListEntity.getPartial_id();
		// 取得消消耗品计量单位信息
		List<ConsumableListEntity> resultList = consumableListMapper.getMeasuringUnit(partial_id);
		BeanUtil.copyToFormList(resultList, resultForm, null, ConsumableListForm.class);
		return resultForm;
	}
	
	/**
	 * 消耗品计量单位信息存在check
	 * 
	 * @param consumableListEntity
	 * @param conn
	 * @return 如果存在返回消耗品计量单位信息
	 */
	public Integer getMeasurementUnitCntByPid(
			ConsumableListEntity consumableListEntity, SqlSessionManager conn) {
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		// 零件 ID
		Integer partial_id = consumableListEntity.getPartial_id();
		// 取得消消耗品计量单位信息
		return consumableListMapper.getMeasuringUnitCnt(partial_id);
	}
	
	/**
	 * 消耗品计量单位信息更新
	 * 
	 * @param request
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 */
	public void updateMeasurementUnit(HttpServletRequest request,
			ActionForm form, HttpSession session, SqlSessionManager conn,
			List<MsgInfo> errors) {
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		BeanUtil.copyToBean(form, consumableListEntity, null);
		// 更新消耗品管理表
		consumableListMapper.updateMeasurementUnit(consumableListEntity);
	}
	
	/**
	 * 消耗品计量单位信息追加
	 * 
	 * @param request
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 */
	public void insertMeasurementUnit(HttpServletRequest request,
			ActionForm form, HttpSession session, SqlSessionManager conn,
			List<MsgInfo> errors) {
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		BeanUtil.copyToBean(form, consumableListEntity, null);
		// 插入消耗品管理表
		consumableListMapper.insertMeasurementUnit(consumableListEntity);
	}

	/**
	 * 取得常用消耗品列表 to Map
	 * @param conn
	 * @return
	 */
	public List<String[]> getPopularItems(SqlSession conn) {
		List<String[]> lPopularItems = new ArrayList<String[]>();
		Map<Integer, String> gotType = new HashMap<Integer, String>();

		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListEntity> results = consumableListMapper.getPopularItems();
		Converter<Integer> ic = IntegerConverter.getInstance();
		for (ConsumableListEntity result : results) {
			String[] popularItem = new String[4];
			popularItem[0] = ic.getAsString(result.getPartial_id());
			popularItem[1] = result.getCode();
			popularItem[2] = result.getDescription();
			Integer type = result.getType();
			if (type == null) {
				popularItem[3] = "";
			} else if (gotType.containsKey(type)) {
				popularItem[3] = gotType.get(type);
			} else {
				String typeText = CodeListUtils.getValue("consumable_type", "" + type);
				gotType.put(type, typeText);
				popularItem[3] = typeText;
			}
			lPopularItems.add(popularItem);
		}
		return lPopularItems;
	}

	public String getPostSheet(SqlSession conn) {
		StringBuffer retSb = new StringBuffer("消耗品代码\t说明\t消耗品分类\t基准在库\t安全库存\t当前有效库存\t库位\r\n");
		
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListEntity> consumableList = consumableListMapper.getStatistic();
		for (ConsumableListEntity consumable : consumableList) {
			retSb.append(consumable.getCode()).append("\t");
			retSb.append(consumable.getDescription()).append("\t");
			String sType = CodeListUtils.getValue("consumable_type", "" + consumable.getType());
			retSb.append(sType).append("\t");
			retSb.append(consumable.getBenchmark()).append("\t");
			retSb.append(consumable.getSafety_lever()).append("\t");
			retSb.append(consumable.getAvailable_inventory()).append("\t");
			retSb.append(consumable.getStock_code()).append("\r\n");
		}

		return retSb.toString();
	}
	
	/**
	 * 更新消耗目标
	 */
	public void updateConsumptQuota(ConsumableListEntity consumableListEntity,SqlSessionManager conn){
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		consumableListMapper.updateConsumptQuota(consumableListEntity);
	}

	/**
	 * 查询一览带平均消耗值
	 * @param consumableListEntity
	 * @param conn
	 * @return
	 */
	public List<ConsumableListForm> searchConsumableListWithQuota(ConsumableListEntity consumableListEntity, SqlSession conn) {
		ConsumableListMapper mapper = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListForm> resultFormList = new ArrayList<ConsumableListForm>();
		List<ConsumableListEntity> resultList = mapper.searchConsumableList(consumableListEntity);

		ConsumableSupplyRecordEntity csrEntity = new ConsumableSupplyRecordEntity();
		csrEntity.setSupply_time_start(consumableListEntity.getSearch_count_period_start());
		csrEntity.setSupply_time_end(consumableListEntity.getSearch_count_period_end());
		ConsumableSupplyRecordMapper csrMapper = conn.getMapper(ConsumableSupplyRecordMapper.class);

		Integer finishCount = csrMapper.getOutLineQuantity(csrEntity);

		for (ConsumableListEntity result : resultList) {
			ConsumableListForm resultForm = new ConsumableListForm();
			BeanUtil.copyToForm(result, resultForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			if (finishCount > 0) {
				int totalUsed = 0;
				if (result.getSupply_count_quantity() != null) totalUsed += result.getSupply_count_quantity();
				if (result.getUsed_count_quantity() != null) totalUsed += result.getUsed_count_quantity();
				resultForm.setCost_avg_by_outline(new BigDecimal(totalUsed)
				.divide(new BigDecimal(finishCount), 2, BigDecimal.ROUND_CEILING).toPlainString());
			}
			resultFormList.add(resultForm);
		}
		return resultFormList;
	}
	/**
	 * 建立消耗品一览导出文档
	 * @param list
	 * @return
	 */
	public String createConsumableListRecord(ConsumableListForm condition, List<ConsumableListForm> list)
			throws Exception {

		String cacheName = "待处理一览" + new Date().getTime() + ".xlsx";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" + cacheName;

		if (list != null) {
			OutputStream out = null;

			try {
				// 定义文件
				File file = new File(cachePath);

				// 判断文件目录是否存在
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				// 文件不存在
				if (!file.exists()) {
					file.createNewFile();
				}

				// 创建Excel
				SXSSFWorkbook work = new SXSSFWorkbook(1000);

				// 创建Sheet
				Sheet sheet = work.createSheet("消耗品仓库库存一览");

				Row row = null;

				// 字体
				Font font = work.createFont();
				font.setFontHeightInPoints((short) 10);
				font.setFontName("微软雅黑");

				// 加粗字体
				Font titlefont = work.createFont();
				titlefont.setFontHeightInPoints((short) 11);
				titlefont.setFontName("微软雅黑");
				titlefont.setBoldweight(Font.BOLDWEIGHT_BOLD);

				// 基本样式
				CellStyle cellStyle = work.createCellStyle();
				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				cellStyle.setFont(font);

				// 居中对齐
				CellStyle centerStyle = work.createCellStyle();
				centerStyle.cloneStyleFrom(cellStyle);
				centerStyle.setAlignment(CellStyle.ALIGN_CENTER);

				// 数字对齐
				CellStyle rightStyle = work.createCellStyle();
				rightStyle.cloneStyleFrom(cellStyle);
				rightStyle.setAlignment(CellStyle.ALIGN_RIGHT);

				// 标题
				CellStyle titleStyle = work.createCellStyle();
				titleStyle.cloneStyleFrom(centerStyle);
				titleStyle.setFillForegroundColor(HSSFColor.AQUA.index);
				titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
				titleStyle.setFont(titlefont);

				Map<String, Integer> subTitleMap = new LinkedHashMap<String, Integer>();

				subTitleMap.put("序号", 256 * 5);
				subTitleMap.put("消耗品代码", 256 * 10);
				subTitleMap.put("说明", 256 * 25);
				subTitleMap.put("分类", 256 * 9);
				subTitleMap.put("库位", 256 * 11);

				subTitleMap.put("基准在库", 256 * 10);
				subTitleMap.put("安全库存", 256 * 10);
				subTitleMap.put("有效库存", 256 * 10);
				subTitleMap.put("补充在途量", 256 * 10);

				subTitleMap.put("入库量", 256 * 10);
				subTitleMap.put("消耗率", 256 * 10);
				subTitleMap.put("消耗总量", 256 * 10);
				subTitleMap.put("替代发放", 256 * 10);
				subTitleMap.put("在线补充", 256 * 10);
				subTitleMap.put("消耗总价", 256 * 10);

				subTitleMap.put("单位产出消耗量", 256 * 15);
				subTitleMap.put("参考消耗量", 256 * 10);

				// 设置标题
				row = sheet.createRow(0);
				row.setHeightInPoints((short) 18);
				CellUtil.createCell(row, 5, DateUtil.toString(new Date(), DateUtil.DATE_PATTERN) + " 当前", titleStyle);
				CellUtil.createCell(row, 9, 
						condition.getSearch_count_period_start() + "～" +
						condition.getSearch_count_period_end() + " 期间内", titleStyle);
				CellRangeAddress cra = new CellRangeAddress(0, 0, 5, 8); 
				sheet.addMergedRegion(cra);
		        // 使用RegionUtil类为合并后的单元格添加边框  
		        RegionUtil.setBorderTop(1, cra, sheet, work);
		        cra = new CellRangeAddress(0, 0, 9, 16); 
				sheet.addMergedRegion(cra);
		        RegionUtil.setBorderTop(1, cra, sheet, work);
		        RegionUtil.setBorderRight(1, cra, sheet, work);

				row = sheet.createRow(1);
				row.setHeightInPoints((short) 18);

				int icol = 0;
				for (String title : subTitleMap.keySet()) {
					CellUtil.createCell(row, icol, title, titleStyle);

					// 设置列宽
					sheet.setColumnWidth(icol, subTitleMap.get(title));

					icol++;
				}
				sheet.createFreezePane(0, 2, 2, 2);

				Map<String, String> cacheMap = new HashMap<String, String>();

				ConsumableListForm entity = null;
				for (int i = 0; i < list.size(); i++ ) {
					icol = 0;
					entity = list.get(i);

					row = sheet.createRow(i + 2);

					// 序号
					CellUtil.createCell(row, icol++, "" + (i + 1), cellStyle);

					// 消耗品代码
					CellUtil.createCell(row, icol++, entity.getCode(), cellStyle);
					// 说明
					CellUtil.createCell(row, icol++, entity.getDescription(), cellStyle);
					// 消耗品分类
					String typeCode = entity.getType();
					String sType = null;

					if (cacheMap.containsKey("T" + typeCode)) {
						sType = cacheMap.get("T" + typeCode);
					} else {
						sType = CodeListUtils.getValue("consumable_type", typeCode);
						if (!isEmpty(sType)) {
							cacheMap.put("T" + typeCode, sType);
						} else
							sType = "";
					}
					CellUtil.createCell(row, icol++, sType, cellStyle);
					// 库位
					CellUtil.createCell(row, icol++, entity.getStock_code(), cellStyle);

					// 基准在库
					CellUtil.createCell(row, icol++, entity.getBenchmark(), rightStyle);
					// 安全库存
					CellUtil.createCell(row, icol++, entity.getSafety_lever(), rightStyle);
					// 当前有效库存
					CellUtil.createCell(row, icol++, entity.getAvailable_inventory(), rightStyle);
					// 补充在途量
					CellUtil.createCell(row, icol++, entity.getOn_passage(), rightStyle);

					// 期间内入库量
					CellUtil.createCell(row, icol++, entity.getSupply_count_quantity(), rightStyle);
					// 消耗率
					CellUtil.createCell(row, icol++, entity.getConsumed_rate(), rightStyle);
					// 期间内消耗总量
					Cell cellCostCountQuantity = row.createCell(icol++);
					cellCostCountQuantity.setCellStyle(rightStyle);
					cellCostCountQuantity.setCellType(SXSSFCell.CELL_TYPE_FORMULA);
					cellCostCountQuantity.setCellFormula("=M" + (i+3) + "+N" + (i+3));
					// 期间内替代发放
					if (null == entity.getReleased_count_quantity()) entity.setReleased_count_quantity("0"); 
					CellUtil.createCell(row, icol++, entity.getReleased_count_quantity(), rightStyle);
					// 期间内在线补充
					if (null == entity.getUsed_count_quantity()) entity.setUsed_count_quantity("0"); 
					CellUtil.createCell(row, icol++, entity.getUsed_count_quantity(), rightStyle);

					// 消耗总价
					Cell cellTotalPrice = row.createCell(icol++);
					cellTotalPrice.setCellStyle(rightStyle);
					if (null == entity.getPrice()) {
						cellTotalPrice.setCellType(SXSSFCell.CELL_TYPE_BLANK);
					} else {
						cellTotalPrice.setCellType(SXSSFCell.CELL_TYPE_FORMULA);
						cellTotalPrice.setCellFormula("=L" + (i+3) + "*" + entity.getPrice());
					}

					// 单位产出消耗量
					CellUtil.createCell(row, icol++, entity.getCost_avg_by_outline(), rightStyle);
					// 参考消耗量
					CellUtil.createCell(row, icol++, entity.getConsumpt_quota(), rightStyle);

				}

				out = new FileOutputStream(file);
				work.write(out);
				work.dispose();
			} catch (Exception e) {
				throw e;
			} finally {
				if (out != null) {
					out.close();
					out = null;
				}
			}
		}

		return cacheName;

	}
	public String searchHeatshrinkableLength(
			String partialId, SqlSession conn) {
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		return consumableListMapper.getHeatshrinkableLengthString(partialId);
	}
	public void setHeatshrinkableLength(HttpServletRequest request,
			SqlSessionManager conn, List<MsgInfo> errors) {
		String partialId = request.getParameter("partail_id");
		String cutLengths = request.getParameter("content");

		Set<String> cutLengthSet = new HashSet<String>();
		if (!isEmpty(cutLengths)) {
			String[] cutLengthArray = cutLengths.split(";");
			for (String cutLength : cutLengthArray) {
				if (IntegerValidator.getInstance().isValid(cutLength)) {
					cutLengthSet.add(cutLength);
				} else {
					MsgInfo error = new MsgInfo();
					error.setComponentid("content");
					error.setErrcode("validator.invalidParam.invalidIntegerValue");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidIntegerValue", "剪裁长度可选项"));
					errors.add(error);
					break;
				}
			}
		}

		if (errors.size() == 0) {
			ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
			consumableListMapper.clearHeatshrinkableLength(partialId);

			for (String cutLength : cutLengthSet) {
				consumableListMapper.setHeatshrinkableLength(partialId, cutLength);
			}
		}
	}
}

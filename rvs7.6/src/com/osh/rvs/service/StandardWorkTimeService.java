package com.osh.rvs.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.master.ModelForm;
import com.osh.rvs.mapper.master.ModelMapper;
import com.osh.rvs.mapper.master.PositionMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.FileUtils;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class StandardWorkTimeService {
	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 标准工时的型号选择改变等级选项
	 * 
	 * @param ajaxResponse
	 * @param args
	 */
	public void getLevelOptionsByCategoryId(String parameter, boolean isLine, SqlSession conn,
			Map<String, Object> ajaxResponse) {
		// 查询出category_id
		ModelService mservice = new ModelService();
		// 初始设置前台传递的值是流水线的值
		// isLine=true;
		//
		boolean thisIsLine = true;
		String category_id = mservice.getDetail(parameter, conn).getCategory_id();
		String options = "";
		// 如果前台传递的单元的值是true加上category_id的 值是满足条件
		if (isLine && (category_id.equals("00000000013") || category_id.equals("00000000016"))) {
			thisIsLine = false;
			options = CodeListUtils.getSelectOptions("material_level_cell", null, "");
		} else if (!isLine && (!category_id.equals("00000000013") && !category_id.equals("00000000016"))) {
			thisIsLine = true;
			options = CodeListUtils.getSelectOptions("material_level_inline", null, "");
		} else {
			return;
		}
		// true or false
		ajaxResponse.put("isLine", thisIsLine);
		// options返回到前台
		ajaxResponse.put("level_options", options);
	}

	public List<PositionEntity> getData(String model_id, String level, SqlSession conn) throws Exception {
		ModelMapper dao = conn.getMapper(ModelMapper.class);
		// 取出name和category_name
		ModelEntity nameCategoryNameByVmodel = dao.getModelByID(model_id);
		// 获取所有相关工位的processCode和name
		List<PositionEntity> lines = dao.getPositionsOfModel(model_id);

		// 取出S1跳过工位
		if ("1".equals(level)) {
			for (int i = lines.size() - 1; i >= 0; i--) {
				PositionEntity position = lines.get(i);
				for (int j = 0; j < ProcessAssignService.S1PASSES.length; j++) {
					if (ProcessAssignService.S1PASSES[j] == Integer.parseInt(position.getPosition_id())) {
						lines.remove(i);
						break;
					}
				}
			}
		} else {
			// 先端预制对象
			if (RvsUtils.getSnoutModels(conn).containsKey(model_id)) {
				PositionEntity element = new PositionEntity();
				element.setProcess_code("301");
				element.setName("先端预制"); // TODO
				lines.add(element);
			}

			// CCD盖玻璃对象
			if (RvsUtils.getCcdModels(conn).contains(model_id)) {
				PositionEntity element = new PositionEntity();
				element.setProcess_code("302");
				element.setName("CCD 盖玻璃更换"); // TODO
				lines.add(element);
			}

			if ("9".equals(level)) {
				// LG 目镜对应机型
				ModelService ms = new ModelService();
				ModelForm model = ms.getDetail(model_id, conn);
				if (model.getKind().equals("01")) {
					PositionEntity element = new PositionEntity();
					element.setProcess_code("303");
					element.setName("LG 玻璃更换"); // TODO
					lines.add(element);
				} else if (model.getKind().equals("03")) {
					PositionEntity element = new PositionEntity();
					element.setProcess_code("361");
					element.setName("A 橡皮涂胶"); // TODO
					lines.add(element);
				}
			}
		}

		for (int i = 0; i < lines.size(); i++) {
			// 获取行数据
			PositionEntity entity = lines.get(i);
			// 获取process_code
			String positionByCode = entity.getProcess_code();
			// 将标准工时进行设值（借用Line_name）
			entity.setLine_name(formatMinute(RvsUtils.getLevelOverLine(nameCategoryNameByVmodel.getName(),
					nameCategoryNameByVmodel.getCategory_name(), level, null, positionByCode)));
		}
		return lines;

	}

	private String formatMinute(String levelOverLine) {
		if (levelOverLine == null || "-1".equals(levelOverLine)) {
			return "未设定";
		}
		return levelOverLine + " 分钟";
	}

	public void checkInput(HttpServletRequest req, List<MsgInfo> errors) {
		if(req.getParameter("level").equals("")){
			MsgInfo error = new MsgInfo();
			error.setComponentid("level");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "等级"));
			errors.add(error);
		}
		if("".equals(req.getParameter("model_id"))){
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "型号"));
			errors.add(error);
		}		
	}

	/**
	 * 构造标准工时导出文件
	 * 
	 * @param conn
	 * @return
	 */
	public String getStandardWorktimeFile(SqlSession conn) {
		InputStream in = null;

		String cacheName = "SWT " + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" + cacheName;
		FileUtils.copyFile(PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\标准工时全型号工位Swt.xls", cachePath);

		PositionMapper pMapper = conn.getMapper(PositionMapper.class);

		List<PositionEntity> lProcessCode = pMapper.getAllProcessCodeForSwt();

		String[] levelArray = {"S1","S2","S3","D", "E1","E2"};

		Map<String, Integer> posIdx = new HashMap<String, Integer>();
		for (int i = 0 ; i < lProcessCode.size(); i++) {
			posIdx.put(lProcessCode.get(i).getProcess_code(), i);
		}
		Map<String, Integer> levelIdx = new HashMap<String, Integer>();
		for (int i = 0 ; i < levelArray.length; i++) {
			levelIdx.put(levelArray[i], i);
		}

		String sql = " select mdl.MODEL_ID, CATEGORY_NAME, mdl.name as MODEL_NAME, level, pos.process_code, mdl.KIND"
		+ " from model_level_set mn"
		+ " join v_model mdl on mn.model_id = mdl.model_id"
		+ " join process_assign pa on mdl.default_pat_id = pa.refer_id and pa.refer_type = 1"
		+ " join v_position pos on ((pa.position_id = pos.position_id or (pa.position_id = 32 and pos.process_code in (302,303, 304))) "
		+ " and (level != 1 or pos.line_id != 13))"
		+ " where mn.avaliable_end_date > current_date and mn.level != 0"
		+ " order by mdl.KIND, CATEGORY_ID, MODEL_NAME, mn.level, pos.line_id, pos.process_code;";

		try {
			in = new FileInputStream(cachePath);
			HSSFWorkbook work = new HSSFWorkbook(in);
			HSSFSheet sheetMaterial = work.getSheetAt(0);

			HSSFCellStyle titleCellBlue = work.createCellStyle();
			titleCellBlue.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
			titleCellBlue.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			titleCellBlue.setVerticalAlignment((short)1);
			titleCellBlue.setBorderTop((short)1);
			titleCellBlue.setBorderBottom((short)1);
			titleCellBlue.setBorderRight((short)1);

			HSSFCellStyle titleCellBold = work.createCellStyle();
			titleCellBold.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			titleCellBold.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			titleCellBold.setBorderTop((short)1);
			titleCellBold.setBorderBottom((short)1);
			titleCellBold.setBorderRight((short)1);
			HSSFFont fontBold = work.createFont();
			fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			fontBold.setFontHeight((short) 240);
			titleCellBold.setFont(fontBold);

			HSSFCellStyle titleCellContent = work.createCellStyle();
			titleCellContent.setVerticalAlignment((short)1);
			titleCellContent.setBorderTop((short)1);
			titleCellContent.setBorderBottom((short)1);
			titleCellContent.setBorderRight((short)1);

			HSSFRow rowLine = sheetMaterial.getRow(0);
			HSSFRow rowPosition = sheetMaterial.getRow(1);
			HSSFRow rowProcessCd = sheetMaterial.getRow(2);

			sheetMaterial.setDefaultColumnStyle(0, titleCellContent);

			sheetMaterial.setDefaultColumnStyle(1, titleCellContent);

			sheetMaterial.setDefaultColumnStyle(2, titleCellContent);

			Integer linestartIndex = null;
			String nowLinename = null;

			for (int i = 0 ; i < lProcessCode.size(); i++) {
				PositionEntity posBean = lProcessCode.get(i);

				sheetMaterial.setColumnWidth(3 + i, (int) (256*14.25+184));
				sheetMaterial.setDefaultColumnStyle(3 + i, titleCellContent);

				HSSFCell cell = null;

				String processCode = posBean.getProcess_code();
				if ("571".equals(processCode) || "572".equals(processCode) || "811".equals(processCode)) {
					cell = rowLine.createCell(3 + i);
					cell.setCellValue(posBean.getName());
					cell.setCellStyle(titleCellBlue);

					cell = rowPosition.createCell(3 + i);
					cell.setCellStyle(titleCellBlue);

					sheetMaterial.addMergedRegion(new CellRangeAddress(0, 1, 3 + i, 3 + i));

					if (nowLinename != null) {
						sheetMaterial.addMergedRegion(new CellRangeAddress(0, 0, 3 + linestartIndex, 3 + i - 1));

						nowLinename = null;
					}
				} else {
					cell = rowLine.createCell(3 + i);
					cell.setCellValue(posBean.getLine_name());
					cell.setCellStyle(titleCellBlue);

					cell = rowPosition.createCell(3 + i);
					cell.setCellValue(posBean.getName());
					cell.setCellStyle(titleCellBlue);

					if (nowLinename == null) {
						nowLinename = posBean.getLine_name();
						linestartIndex = i;
					} else if (!nowLinename.equals(posBean.getLine_name())) {
						sheetMaterial.addMergedRegion(new CellRangeAddress(0, 0, 3 + linestartIndex, 3 + i - 1));

						nowLinename = posBean.getLine_name();
						linestartIndex = i;
					}
				}

				cell = rowProcessCd.createCell(3 + i);
				cell.setCellValue(posBean.getProcess_code());
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellStyle(titleCellBold);
			}

			Statement sm = conn.getConnection().createStatement();

			ResultSet ds = sm.executeQuery(sql);

			int iRow = 2;
			StringBuffer key = new StringBuffer("");

			Set<String> cc = RvsUtils.getCcdModels(conn);
			Set<String> cl = RvsUtils.getCcdLineModels(conn);

			while (ds.next()) {
				iRow = xlist(ds, lProcessCode, posIdx, sheetMaterial, iRow, key, cc, cl);
			}

			// 保存文件
			FileOutputStream fileOut = new FileOutputStream(cachePath);
			work.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		return cacheName;
	}

	private int xlist(ResultSet ds, List<PositionEntity> lProcessCode, Map<String, Integer> posIdx, 
			HSSFSheet sheetMaterial, int iRow, StringBuffer keyBf, Set<String> cc, Set<String> cl) throws Exception {
		int retRow = iRow;

		String categoryName = ds.getString("CATEGORY_NAME");
		String modelName = ds.getString("MODEL_NAME");
		String level = "" + ds.getString("level");
		String levelName = CodeListUtils.getValue("material_level", level);
		String processCode = ds.getString("process_code");

		String modelId = ds.getString("MODEL_ID");

		if (posIdx.get(processCode) == null) {
			return iRow;
		}

		if (!(modelName + "|" + level).equals(keyBf.toString())) {
			retRow = iRow + 1;
			keyBf.setLength(0);
			keyBf.append(modelName + "|" + level);
		}

		if (sheetMaterial.getRow(retRow) == null) {
			HSSFRow row = sheetMaterial.createRow(retRow);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(categoryName);
			cell = row.createCell(1);
			cell.setCellValue(modelName);
			cell = row.createCell(2);
			cell.setCellValue(levelName);
			for (int i = 0; i < lProcessCode.size(); i++) {
				cell = row.createCell(3 + i);
				cell.setCellValue("-");
			}
			logger.info(retRow + " modelName " + modelName);
		}

		if (cc.contains(modelId)) {
			setCell(sheetMaterial, retRow, posIdx, "302", modelName, categoryName, level);
		}

		if ("D".equals(level)) {
			setCell(sheetMaterial, retRow, posIdx, "303", modelName, categoryName, level);
		}

		if (cl.contains(modelId)) {
			setCell(sheetMaterial, retRow, posIdx, "304", modelName, categoryName, level);
		}

		setCell(sheetMaterial, retRow, posIdx, processCode, modelName, categoryName, level);

		return retRow;
	}

	private void setCell(HSSFSheet sheetMaterial, int retRow, Map<String, Integer> posIdx, String processCode,
			String modelName, String categoryName, String level) throws Exception {
		int idx = posIdx.get(processCode);

		HSSFRow row = sheetMaterial.getRow(retRow);
		HSSFCell cell = row.createCell(3 + idx);

		String lever = RvsUtils.getLevelOverLine(modelName, categoryName, level, null, processCode);
		if ("-1".equals(lever)) {
			cell.setCellValue("无设定");
		} else {
			cell.setCellValue(Integer.parseInt(lever));
		}
	}
}

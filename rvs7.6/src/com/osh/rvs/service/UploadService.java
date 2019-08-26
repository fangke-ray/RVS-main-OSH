package com.osh.rvs.service;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.manage.ModelLevelSetEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.PartialBomEntity;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.master.PartialPositionEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.bean.partial.PartialBaseLineValueEntity;
import com.osh.rvs.bean.partial.SorcLossEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.common.XlsUtil;
import com.osh.rvs.form.UploadForm;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.manage.ModelLevelSetForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.form.partial.PartialBaseLineValueForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.manage.LevelModelLeedsMapper;
import com.osh.rvs.mapper.master.HolidayMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.master.PartialBomMapper;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.master.PartialPositionMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.mapper.partial.PartialAssignMapper;
import com.osh.rvs.mapper.partial.PartialBaseLineValueMapping;
import com.osh.rvs.mapper.partial.PartialOrderMapper;
import com.osh.rvs.mapper.partial.SorcLossMapper;
import com.osh.rvs.mapper.qf.AcceptanceMapper;
import com.osh.rvs.service.partial.PartialAssignService;
import com.osh.rvs.service.qf.MaterialFactService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class UploadService {

	private static final Integer BELONGS_BOM = 1;
	private static final int SUMMARY_FILE_COLS = 39;
	private static Logger log = Logger.getLogger(UploadService.class);

	/**
	 * 读取受理文件
	 * 
	 * @param tempfilename
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<MaterialForm> readFile(String tempfilename, SqlSession conn, List<MsgInfo> errors) {
		ActiveXComponent xl = new ActiveXComponent("Excel.Application");
		// 不可见
		xl.setProperty("Visible", new Variant(false));
		// 不弹信息
		xl.setProperty("DisplayAlerts", new Variant(false));

		List<MaterialForm> retList = new ArrayList<MaterialForm>();

		try {
			log.info("Xls=StartE " + tempfilename);
			ComThread.InitSTA();

			Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
			Dispatch workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method, new Object[] { tempfilename, // .replaceAll("/",
																													// //
																													// "\\\\")
					new Variant(false), new Variant(false) }, new int[1]).toDispatch();

			Dispatch sheets = Dispatch.call(workbook, "Worksheets").toDispatch();
			Dispatch sheet;
			try {
				sheet = Dispatch.call(sheets, "Item", "Sheet1").toDispatch();
			} catch (Exception e) {
				sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
			}
			Dispatch.call(sheet, "Select");

			Dispatch usedRange = Dispatch.call(sheet, "UsedRange").toDispatch();
			Dispatch rows = Dispatch.get(usedRange, "Rows").toDispatch();
			int rowSize = Dispatch.get(rows, "Count").getInt();

			Dispatch columns = Dispatch.get(usedRange, "Columns").toDispatch();
			int columnSize = Dispatch.get(columns, "Count").getInt();

			log.info("rowSize= " + rowSize);
			log.info("columnSize= " + columnSize);

			if (columnSize < 16) { // 13 单元
				for (int i = 2; i <= rowSize; i++) {
					MaterialForm lineform = new MaterialForm();
					lineform.setEsas_no(GetValue(sheet, "E" + i));
					lineform.setModel_name(GetValue(sheet, "F" + i));
					if (CommonStringUtil.isEmpty(lineform.getModel_name())) {
						continue;
					}
					String model_id = ReverseResolution.getModelByName(lineform.getModel_name(), conn);
					if (model_id == null) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("dbaccess.columnNotUnique");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("model.notExist",
								lineform.getModel_name()));
						errors.add(info);
						continue;
					}
					lineform.setModel_id(model_id);

					String ocm = GetValue(sheet, "G" + i);
					if (!ocm.startsWith("OCM")) {
						ocm = GetValue(sheet, "H" + i);
					}
					lineform.setOcm(reverOcm(ocm));

					String serial_no = GetValue(sheet, "J" + i);
					if (CommonStringUtil.isEmpty(serial_no)) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("validator.required");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "第" + i
								+ "行机身号"));
						errors.add(info);
						continue;
					}
					lineform.setSerial_no(serial_no);
					lineform.setMaterial_id("Line" + i);
					lineform.setFix_type("2");

					retList.add(lineform);
				}
			} else if (columnSize > 60) { // 65 单元
				for (int i = 2; i <= rowSize; i++) {
					MaterialForm lineform = new MaterialForm();
					lineform.setEsas_no(GetValue(sheet, "H" + i));
					lineform.setSorc_no(GetValue(sheet, "I" + i));
					lineform.setModel_name(GetValue(sheet, "J" + i));
					if (CommonStringUtil.isEmpty(lineform.getModel_name())) {
						continue;
					}
					String model_id = ReverseResolution.getModelByName(lineform.getModel_name(), conn);
					if (model_id == null) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("dbaccess.columnNotUnique");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("model.notExist", lineform.getModel_name()));
						errors.add(info);
						continue;
					}
					lineform.setModel_id(model_id);

					String ocm = GetValue(sheet, "E" + i);
					lineform.setOcm(reverOcm(ocm));

					String serial_no = GetValue(sheet, "L" + i); // K
					if (CommonStringUtil.isEmpty(serial_no)) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("validator.required");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "第" + i + "行机身号"));
						errors.add(info);
						continue;
					}

					String level  = GetValue(sheet, "AF" + i);

					lineform.setLevel(CodeListUtils.getKeyByValue("material_level_cell", level, ""));

					lineform.setSerial_no(serial_no);
					lineform.setAgreed_date(parseNumFormat(GetValue(sheet, "AX" + i))); // AW
					lineform.setMaterial_id("Line" + i);
					lineform.setFix_type("2");
					retList.add(lineform);
				}				
			} else { // 34 流水线
				for (int i = 2; i <= rowSize; i++) {

					MaterialForm lineform = new MaterialForm();
					lineform.setSorc_no(GetValue(sheet, "A" + i));
					lineform.setOcm(reverOcm(GetValue(sheet, "C" + i)));
					lineform.setEsas_no(GetValue(sheet, "D" + i));
					lineform.setModel_name(GetValue(sheet, "E" + i));
					if (CommonStringUtil.isEmpty(lineform.getModel_name())) {
						continue;
					}
					String model_id = ReverseResolution.getModelByName(lineform.getModel_name(), conn);
					if (model_id == null) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("dbaccess.columnNotUnique");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("model.notExist",
								lineform.getModel_name()));
						errors.add(info);
						continue;
					}
					lineform.setModel_id(model_id);
					String serial_no = GetValue(sheet, "F" + i);
					if (CommonStringUtil.isEmpty(serial_no)) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("validator.required");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "第" + i
								+ "行机身号"));
						errors.add(info);
						continue;
					}
					lineform.setSerial_no(serial_no);
					lineform.setLevel(reverLevel(GetValue(sheet, "M" + i))); // L+1
					lineform.setAgreed_date(parseNumFormat(GetValue(sheet, "AC" + i))); // Z+1 // AA+2
					lineform.setMaterial_id("Line" + i);
					lineform.setFix_type("1");

					String cafeteriaPlan = GetValue(sheet, "J" + i);
					if (!cafeteriaPlan.trim().equals("")) {
						lineform.setSelectable("1");
					}

					String direct = GetValue(sheet, "K" + i);
					if (!direct.trim().equals("")) {
						lineform.setDirect_flg("1");
					}

					String custom = GetValue(sheet, "R" + i); // Q+1
					if (custom.trim().startsWith("OCM")) {
						lineform.setService_repair_flg("3"); // 备品
					}

					retList.add(lineform);
				}
			}

			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
		} catch (Exception e) {
			log.error("Exception= ", e);
			return null;
		} finally {
			xl.invoke("Quit", new Variant[] {});
			ComThread.Release();
		}
		return retList;
	}

	private String parseNumJavaFormat(String getValue) {
		return parseNumFormat(getValue, 2);
	}
	private String parseNumFormat(String getValue, int fix) {
		if (getValue.matches("^\\d+$")) {
			int diff = Integer.parseInt(getValue) - fix;
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 1900);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DATE, 1);

			cal.add(Calendar.DATE, diff);
			return DateUtil.toString(cal.getTime(), DateUtil.DATE_PATTERN);
		} else {
			return getValue;
		}
	}
	/**
	 * Excel 日期格式 数值型转换
	 * 
	 * @param getValue
	 * @return
	 */
	private String parseNumFormat(String getValue) {
		return parseNumFormat(getValue, 1);
	}

	/**
	 * 读取客户同意日/Unrepaired
	 * 
	 * @param tempfilename
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<MaterialForm> readAgreed(String tempfilename, SqlSession conn, List<MsgInfo> errors) {
		ActiveXComponent xl = new ActiveXComponent("Excel.Application");
		// 不可见
		xl.setProperty("Visible", new Variant(false));
		// 不弹信息
		xl.setProperty("DisplayAlerts", new Variant(false));

		List<MaterialForm> retList = new ArrayList<MaterialForm>();

		try {
			log.info("Xls=StartG " + tempfilename);
			ComThread.InitSTA();

			Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
			Dispatch workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method, new Object[] { tempfilename, // .replaceAll("/",
																													// "\\\\")
					new Variant(false), new Variant(false) }, new int[1]).toDispatch();

			Dispatch sheets = Dispatch.call(workbook, "Worksheets").toDispatch();
			Dispatch sheet;
			try {
				sheet = Dispatch.call(sheets, "Item", "Sheet1").toDispatch();
			} catch (Exception e) {
				sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
			}
			Dispatch.call(sheet, "Select");

			Dispatch usedRange = Dispatch.call(sheet, "UsedRange").toDispatch();
			Dispatch rows = Dispatch.get(usedRange, "Rows").toDispatch();
			int rowSize = Dispatch.get(rows, "Count").getInt();

			Dispatch columns = Dispatch.get(usedRange, "Columns").toDispatch();
			int columnSize = Dispatch.get(columns, "Count").getInt();

			log.info("rowSize= " + rowSize);
			log.info("columnSize= " + columnSize);

			if (columnSize < 16) {
			} else { // 34 流水线
				MaterialFactService mfService = new MaterialFactService();

				for (int i = 2; i <= rowSize; i++) {
					MaterialForm lineform = new MaterialForm();
					boolean changed = false;
					String sSorc_no = GetValue(sheet, "A" + i);
					if (CommonStringUtil.isEmpty(sSorc_no)) {
						// 可投线必须有SORC NO.
						continue;
					}
					lineform.setSorc_no(sSorc_no);

					String sAgreed_date = parseNumFormat(GetValue(sheet, "AC" + i)); // Z + 1 // AA + 2
					if (!CommonStringUtil.isEmpty(sAgreed_date)) {
						// 只需要有同意日的
						mfService.updateAgreedDateBySorc(sSorc_no,
								DateUtil.toDate(sAgreed_date, DateUtil.DATE_PATTERN), conn);
						lineform.setAgreed_date(sAgreed_date);
						changed = true;
						// 未修理返送的同意日
						String sUnrepair = parseNumFormat(GetValue(sheet, "G" + i));
						if (sUnrepair.toLowerCase().startsWith("unrepair")) {
							mfService.updateUnrepairBySorc(sSorc_no, null, conn);
						}
					}

					if (changed)
						retList.add(lineform);
				}
			}

			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
		} catch (Exception e) {
			log.error("Exception= ", e);
			return null;
		} finally {
			xl.invoke("Quit", new Variant[] {});
			ComThread.Release();
		}
		return retList;
	}

	/**
	 * 读取入库预定日
	 * 
	 * @param tempfilename
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<MaterialForm> readPatrialReach(String tempfilename, SqlSession conn, List<MsgInfo> errors) {
		ActiveXComponent xl = new ActiveXComponent("Excel.Application");
		// 不可见
		xl.setProperty("Visible", new Variant(false));
		// 不弹信息
		xl.setProperty("DisplayAlerts", new Variant(false));

		List<MaterialForm> retList = new ArrayList<MaterialForm>();

		try {
			log.info("Xls=StartG " + tempfilename);
			ComThread.InitSTA();

			Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
			Dispatch workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method, new Object[] { tempfilename, // .replaceAll("/",
																													// "\\\\")
					new Variant(false), new Variant(false) }, new int[1]).toDispatch();

			Dispatch sheets = Dispatch.call(workbook, "Worksheets").toDispatch();
			Dispatch sheet;
			try {
				sheet = Dispatch.call(sheets, "Item", "Sheet1").toDispatch();
			} catch (Exception e) {
				sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
			}
			Dispatch.call(sheet, "Select");

			Dispatch usedRange = Dispatch.call(sheet, "UsedRange").toDispatch();
			Dispatch rows = Dispatch.get(usedRange, "Rows").toDispatch();
			int rowSize = Dispatch.get(rows, "Count").getInt();

			Dispatch columns = Dispatch.get(usedRange, "Columns").toDispatch();
			int columnSize = Dispatch.get(columns, "Count").getInt();

			log.info("rowSize= " + rowSize);
			log.info("columnSize= " + columnSize);

			MaterialPartialService mfService = new MaterialPartialService();

			for (int i = 2; i <= rowSize; i++) {
				MaterialForm lineform = new MaterialForm();
				boolean changed = false;
				String sSorc_no = GetValue(sheet, "A" + i);
				if (CommonStringUtil.isEmpty(sSorc_no)) {
					// 可投线必须有SORC NO.
					continue;
				}
				lineform.setSorc_no(sSorc_no);

				String sPartial_reach_date = parseNumFormat(GetValue(sheet, "AD" + i)); // AB + 2
				if (!CommonStringUtil.isEmpty(sPartial_reach_date)) {
					// 只需要有入库预定日的
					mfService.updateReachDateBySorc(sSorc_no,
							DateUtil.toDate(sPartial_reach_date, DateUtil.DATE_PATTERN), conn);
					lineform.setAgreed_date(sPartial_reach_date);
					changed = true;
				}

				if (changed)
					retList.add(lineform);
			}

			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
		} catch (Exception e) {
			log.error("Exception= ", e);
			return null;
		} finally {
			xl.invoke("Quit", new Variant[] {});
			ComThread.Release();
		}
		return retList;
	}

	private static Map<String, String> reverOcmMap = new HashMap<String, String>();

	private String reverOcm(String gValue) {
		if (!reverOcmMap.containsKey(gValue)) {
			reverOcmMap.put(gValue, CodeListUtils.getKeyByValue("material_ocm", gValue, ""));
		}
		return reverOcmMap.get(gValue);
	}

	private static Map<String, String> reverLevelMap = new HashMap<String, String>();

	private String reverLevel(String gValue) {
		if (!reverLevelMap.containsKey(gValue)) {
			reverLevelMap.put(gValue, CodeListUtils.getKeyByValue("material_level", gValue, ""));
		}
		return reverLevelMap.get(gValue);
	}

	private static Map<String, String> reverOcmLevelMap = new HashMap<String, String>();

	private String reverOcmLevel(String gValue) {
		if (isEmpty(gValue)) return null;
		if (!reverOcmLevelMap.containsKey(gValue)) {
			reverOcmLevelMap.put(gValue, CodeListUtils.getKeyByValue("material_ocm_direct_rank", gValue.substring(0, 1), ""));
		}
		return reverOcmLevelMap.get(gValue);
	}

	// 读取值
	private static String GetValue(Dispatch sheet, String position) {
		Variant cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { position }, new int[1]);

		String value = Dispatch.get(cell.toDispatch(), "Value").toString();

		// TODO IsNumeric
		value = value.replaceAll("(\\d*)\\.0$", "$1");
		// TODO IsDate
		if (value.indexOf(" CST ") >= 0) {
			Date javaDate = Dispatch.get(cell.toDispatch(), "Value").getJavaDate();
			value = DateUtil.toString(javaDate, DateUtil.DATE_PATTERN);
		}
		if ("null".equals(value))
			value = "";
		return value;
	}

	public String readFileName(String uploadMonth, String filepath) {
		String filename = PathConsts.BASE_PATH + PathConsts.REPORT + "\\" + filepath + "\\" + uploadMonth
				+ "\\confirm\\";

		File fMonthPath = new File(filename);
		if (!fMonthPath.exists()) {
			fMonthPath.mkdirs();
		}
		fMonthPath = null;

		return filename;
	}

	/**
	 * 取得Excel文件,并插入数据
	 * 
	 * @param fileName
	 * @param conn
	 * @param errors
	 */
	public void readBomExcel(String fileName, SqlSessionManager conn, List<MsgInfo> errors) {
		PartialBomMapper dao = conn.getMapper(PartialBomMapper.class);
		// 删除BOM数据
		InputStream in = null;
		StringBuffer errorsBuffer = new StringBuffer();
		try {
			in = new FileInputStream(fileName);// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = null;

			// 获取Sheet
			for (int sheetNumber = 0; sheetNumber < work.getNumberOfSheets(); sheetNumber++) {
				sheet = work.getSheetAt(sheetNumber);
				if (sheet.getSheetName().equals("BOM-Line")) break;
			}
			if (sheet == null) return;

			dao.deleteBom();

			PartialBomEntity partialBomEntity = null;
			Map<String, PartialBomEntity> mapBom = new HashMap<String, PartialBomEntity>();
			List<PartialBomEntity> pList = null;

			String curLevelModel = "";// 当前等级型号
			String curLevel = "";// 当前等级
			String curmodel_id = "";// 当前型号ID

			boolean insertFlg = false;//数据插入标记
			for (int iRow = 1; iRow <= sheet.getLastRowNum(); iRow++) {
				partialBomEntity = new PartialBomEntity();
				HSSFRow row = sheet.getRow(iRow);
				if (row != null) {
					if (row.getCell(0) == null) {
						continue;
					}
					// 等级型号
					String level_model = getStringCellValue(row.getCell(0));
					if (CommonStringUtil.isEmpty(level_model) || !level_model.matches("^[SE].*")) {
						continue;
					}
					String level = "";

					if (!curLevelModel.equals(level_model)) {
						curLevelModel = level_model;

						level = CodeListUtils.getKeyByValue("material_level_inline", curLevelModel.substring(0, 2).trim(), null);
						if (CommonStringUtil.isEmpty(level)) {
							continue;
						}
						if (!curLevel.equals(level)) {
							curLevel = level;
						}

						String model_name = curLevelModel.substring(2, curLevelModel.length());
						String model_id = ReverseResolution.getModelByName(model_name, conn);
						if (model_id == null) {
							errorsBuffer.append("型号" + model_name + "不存在" + "\n");
							continue;
						}
						if (!curmodel_id.equals(model_id)) {
							curmodel_id = model_id;
						}

						for (String key : mapBom.keySet()) {
							PartialBomEntity pbEntity = mapBom.get(key);
							try {
								dao.insertBom(pbEntity);
								insertFlg = true;
							} catch (Exception e) {
								errorsBuffer.append("零件" + partialBomEntity.getCode() + e.getMessage() + "\n");
								log.error(e.getMessage());
							}
						}
						mapBom.clear();
					}

					partialBomEntity.setLevel(Integer.parseInt(curLevel));
					partialBomEntity.setModel_id(curmodel_id);

					// 零件
					String code = getStringCellValue(row.getCell(1));
					PartialEntity partialEntity = new PartialEntity();
					partialEntity.setCode(code);
					PartialMapper partialDao = conn.getMapper(PartialMapper.class);
					List<String> sList = partialDao.checkPartial(partialEntity);// 查询零件名称
					if (sList.size() == 0) {
						if (!CommonStringUtil.isEmpty(code))
						errorsBuffer.append("零件" + code + "不存在" + "\n");
						log.error("零件不存在");
						continue;
					}
					String partial_id = sList.get(0);
					partialBomEntity.setPartial_id(partial_id);
					partialBomEntity.setCode(code);

					// 使用数量
					Integer quantity = getNumCellValue(row.getCell(3));
					if (quantity == null) {
						continue;
					}

					if (mapBom.containsKey(partial_id)) {
						int beforeQuantity = mapBom.get(partial_id).getQuantity();
						quantity = quantity + beforeQuantity;
						partialBomEntity.setQuantity(quantity);// 使用数量
						mapBom.put(partial_id, partialBomEntity);
						continue;
					}
					partialBomEntity.setQuantity(quantity);// 使用数量
					mapBom.put(partial_id, partialBomEntity);
				}

			}

			// 最后一项
			if (mapBom!= null && mapBom.size() > 0) {
				for (String key : mapBom.keySet()) {
					PartialBomEntity pbEntity = mapBom.get(key);
					try {
						dao.insertBom(pbEntity);
					} catch (Exception e) {
						errorsBuffer.append("零件" + partialBomEntity.getCode() + e.getMessage() + "\n");
						log.error(e.getMessage());
					}
				}
			}

			// 更新新零件
			pList = dao.searchNewPartial();
			if (pList.size() > 0) {
				for (int i = 0; i < pList.size(); i++) {
					dao.updateBom(pList.get(i));
				}
			}

			// 删除旧零件
			pList = dao.searchHistoryPartial();
			if (pList.size() > 0) {
				for (int i = 0; i < pList.size(); i++) {
					dao.deleteHistoryPartialBom(pList.get(i));
				}
			}

			if (errorsBuffer.toString().length() > 0) {
				MsgInfo error = new MsgInfo();
				error.setErrmsg(errorsBuffer.toString());
				errors.add(error);
			}
			
			if(insertFlg == false){//没有满足条件
				conn.rollback();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取单元格数据
	 * 
	 * @param cell
	 *            Excel单元格
	 * @return String 单元格数据内容
	 */
	private String getStringCellValue(HSSFCell cell) {
		String strCell = "";
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			Double dValue = cell.getNumericCellValue();
			strCell = "" + dValue.intValue();
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

	private Integer getNumCellValue(HSSFCell cell) {
		return (int) cell.getNumericCellValue();
	}

	/**
	 * 零件发放导入
	 * 
	 * @param request
	 * @param partialReceptMap 文件内容
	 */
	public List<MaterialPartialForm> readPartialAssignFile(HttpServletRequest request, Map<String, Map<String, Integer>> partialReceptMap, SqlSession conn, List<MsgInfo> errors) {

		// 处理文件
		Set<String> materialIDAndOccurTimesSet = partialReceptMap.keySet();
		Iterator<String> iterator = materialIDAndOccurTimesSet.iterator();

		List<MaterialPartialEntity> responseList = new ArrayList<MaterialPartialEntity>();
		List<MaterialPartialForm> responseForms = new ArrayList<MaterialPartialForm>();
		MaterialPartialEntity materialpartialEntity = null;
		MaterialPartialMapper materialPartialDao = conn.getMapper(MaterialPartialMapper.class);
		PartialAssignMapper paMapper = conn.getMapper(PartialAssignMapper.class);

		@SuppressWarnings("unchecked")
		Map<String, List<MaterialPartialDetailEntity>> partialSessionMap = (Map<String, List<MaterialPartialDetailEntity>>) request.getSession().getAttribute("partialMap");

		// 通过可发放的检查
		Set<String> checkedKeys = new HashSet<String>();

		while (iterator.hasNext()) {
			String materialIDAndOccurTimes = iterator.next();

			String materialId = materialIDAndOccurTimes.substring(0, materialIDAndOccurTimes.length() - 1);// 维修对象ID
			String occurTimes = materialIDAndOccurTimes.substring(materialIDAndOccurTimes.length() - 1,materialIDAndOccurTimes.length());// 订购次数

			MaterialPartialForm partialForm = new MaterialPartialForm();
			partialForm.setMaterial_id(materialId);
			partialForm.setOccur_times(occurTimes); 

			if (!checkedKeys.contains(materialIDAndOccurTimes)) {
				MaterialPartialDetailEntity condition = new MaterialPartialDetailEntity();
				BeanUtil.copyToBean(partialForm, condition, CopyOptions.COPYOPTIONS_NOEMPTY);
				int result = paMapper.checkWaiting(condition);
				if (result == 1){
					checkedKeys.add(materialIDAndOccurTimes);
				} else {
					MsgInfo info=new MsgInfo();
					info.setErrcode("info.partial.allAssigned");
					info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.allAssigned",""));
					errors.add(info);
					return null;
				}
			}

			MaterialPartialEntity materialPartialEntity = new MaterialPartialEntity();
			// 复制数据到对象
			BeanUtil.copyToBean(partialForm, materialPartialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

			MaterialPartialEntity tempMaterialPartialEntity = materialPartialDao.loadMaterialPartial(materialPartialEntity);// 从维修对象零件订购表中查询
			if (tempMaterialPartialEntity == null) {// 维修对象不存在
				MsgInfo info=new MsgInfo();
				info.setErrmsg("没有该维修对象的订购信息。");
				errors.add(info);
				continue;
			} else {
				materialpartialEntity = materialPartialDao.getMaterialByKey(materialId,occurTimes);
				materialpartialEntity.setIsHistory(null);
				materialpartialEntity.setOccur_times(Integer.valueOf(occurTimes));
				responseList.add(materialpartialEntity);// 一览数据

				if (partialSessionMap.containsKey(materialIDAndOccurTimes)) {
					List<MaterialPartialDetailEntity> partialSessionList = partialSessionMap.get(materialIDAndOccurTimes);// Session中

					Map<String, Integer> partialMap = partialReceptMap.get(materialIDAndOccurTimes);// 文件里
					
					String curPartialID="";
					for (int i = 0; i < partialSessionList.size(); i++) {
						MaterialPartialDetailEntity mEntity = partialSessionList.get(i);
						String partial_id = mEntity.getPartial_id();// 零件ID
						Integer waitQuanity = mEntity.getWaiting_quantity();// 等待签收数量
						Integer alreadyQuantity=mEntity.getQuantity();//订购数量
						Integer state = mEntity.getStatus();
						if(!partialMap.containsKey(partial_id)){
							continue;
						}
						Integer quantity = partialMap.get(partial_id);// 签收数量，文件里
						
//						if(quantity>waitQuanity){
//							MsgInfo info=new MsgInfo();
//							info.setErrcode("info.partial.recieveOver");
//							info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.recieveOver",mEntity.getCode()));
//							errors.add(info);
//							return null;
//						}
						
						Integer recept = null;// 已签收件数
						Integer wait = null;// 等待件数
						if (quantity != null && quantity != 0) {
							if(!partial_id.equals(curPartialID)){
								mEntity.setCur_quantity(alreadyQuantity-waitQuanity);// 已签收数量=订购数量 - 等待签收数量
							}

							if (quantity <= waitQuanity) {
								recept = quantity;
								wait = waitQuanity - recept;
								quantity = 0;
								if (wait == 0) {
									if (state == 1)
										state = 2; // 未签收 -> 无BO签收
									if (state == 3)
										state = 4; // BO -> BO解决
								} else {
									state = 3; // 零件不足ＢＯ
								}
								mEntity.setRecept_quantity(recept);// 本次签收数量
								mEntity.setWaiting_quantity(wait);// 未签收数量
								mEntity.setStatus(state);// 签收状态
								partialMap.put(partial_id, quantity);

							} else {
								recept = waitQuanity;
								// Updated by Gonglm 2014/1/11 Start
								wait = 0;
								// wait = quantity - recept;
								quantity = quantity - recept;
								// Updated by Gonglm 2014/1/11 End

								if (wait == 0) {
									if (state == 1)
										state = 2; // 未签收 -> 无BO签收
									if (state == 3)
										state = 4; // BO -> BO解决
								} else {
									state = 3; // 零件不足ＢＯ
								}
								mEntity.setRecept_quantity(recept);// 本次签收数量
								mEntity.setWaiting_quantity(wait);// 未签收数量
								mEntity.setStatus(state);// 签收状态
								partialMap.put(partial_id, quantity);
							}
						}
					}

//				} else {// 零件不存在
//					MsgInfo info=new MsgInfo();
//					info.setErrmsg("零件不存在");
//					errors.add(info);
				}
			}
		}

		PartialAssignService paservice = new PartialAssignService();
		// 重复导入检查
		for (String materialIDAndOccurTimes : materialIDAndOccurTimesSet) {
			Map<String, Integer> partialMap = partialReceptMap.get(materialIDAndOccurTimes);// 文件里

			if (partialMap != null) {
				for (String partial_id : partialMap.keySet()) {
					Integer quantity = partialMap.get(partial_id);
					if (quantity > 0) {
						PartialMapper pMapper = conn.getMapper(PartialMapper.class);
						PartialEntity pEntity = pMapper.getPartialByID(partial_id);
						MsgInfo info=new MsgInfo();
						info.setErrcode("info.partial.recieveOver");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.recieveOver",
								(pEntity == null ? "" : pEntity.getCode())));
						errors.add(info);
						return null;
					}
				}
			}

			// 处理零件本次发放详细
			paservice.searchPartialAssignDetail(materialIDAndOccurTimes, partialSessionMap, partialReceptMap, request);
		}

		request.getSession().setAttribute("partialReceptMap", partialReceptMap);
		request.getSession().setAttribute("partialSessionMap", partialSessionMap);
		BeanUtil.copyToFormList(responseList, responseForms, null, MaterialPartialForm.class);

		request.getSession().setAttribute("responseSessionForms", responseForms);
		
		return responseForms;
	}

	/**
	 * 按行读取零件签收文件
	 * 
	 * @param fileName
	 * @param checkT 
	 * @return list
	 */
	public Map<String, Map<String, Integer>> readFileByLines(String fileName, Map<String, Map<String, Integer>> materialMap, boolean checkT, SqlSession conn, List<MsgInfo> errors) {
		File file = new File(fileName);
		String code = RvsUtils.getFileCode(fileName);// 取得文件编码

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), code));
			String tempString = null;
			String currentSorcNO = "";
			String curMaterialId = "";
			String curPartialId = "";
			String curoccurTimes="";
			Map<String, Integer> partialMap = null;
			int idx = 0;
			while ((tempString = reader.readLine()) != null) {
				if (idx == 0) {// 去除表头
					idx++;
					continue;
				}

				String[] arr = tempString.split("\t");// 制表格分割

				if (arr.length < 8)
					continue;

				String cell1value = arr[0];
				if (cell1value == null)
					continue;
				String[] sorcAndOccurTimes = getOccurTimes(trimSorc(trimQuote(cell1value)));

				if (sorcAndOccurTimes == null)
					continue;

				if (checkT && !trimQuote(cell1value).toUpperCase().matches(".*-T[0-9]{0,}$")) { // {0,1}
					continue;
				}

				String occur_times = sorcAndOccurTimes[1];// 订购次数

				String sorc_no = trimSorc(sorcAndOccurTimes[0]);
				if (!(currentSorcNO.equals(sorc_no) && curoccurTimes.equals(occur_times))) {// 维修对象ID
					currentSorcNO = sorc_no;
					curoccurTimes=occur_times;
					MaterialEntity entity = new MaterialEntity();
					MaterialMapper dao = conn.getMapper(MaterialMapper.class);
					entity.setSorc_no(currentSorcNO);
					entity.setBreak_back_flg(0); // 无返还
					entity.setFind_history("1"); // 未出货

					List<String> listTemp = dao.searchMaterialIds(entity);

					if (listTemp.size() == 0) {
						// buffer.append("维修对象" + currentSorcNO + "不存在或已经出货\t");
						continue;
					}
					String material_id = listTemp.get(0);
					partialMap = new HashMap<String, Integer>();
					curMaterialId = material_id;
					materialMap.put(curMaterialId + curoccurTimes, partialMap);
				}

				String quantity = replaceStr(arr[7]);// 订购数量
				if (CommonStringUtil.isEmpty(quantity)) {
					continue;
				}
				Integer iQuantity = Integer.parseInt(quantity);

				String partialCode = replaceStr(arr[3]);// 零件品名
				PartialEntity pEntity = new PartialEntity();
				pEntity.setCode(partialCode);
				PartialMapper pDao = conn.getMapper(PartialMapper.class);
				List<String> pList = pDao.checkPartial(pEntity);
				if (pList.size() == 0) {
					buffer.append("零件" + partialCode + "不存在\t");
					continue;
				}
				String partial_id = pList.get(0);
				if (!curPartialId.equals(partial_id)) {
					curPartialId = partial_id;
				}

				if (partialMap.containsKey(curPartialId)) {
					Integer beforeQuantity = partialMap.get(curPartialId);
					iQuantity = iQuantity + beforeQuantity;
					partialMap.put(curPartialId, iQuantity);
					continue;
				}
				partialMap.put(curPartialId, iQuantity);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(buffer.toString().isEmpty()){
			}else{
				MsgInfo info=new MsgInfo();
				info.setErrmsg(buffer.toString());
				errors.add(info);
			}
			log.info("========" + buffer.toString());
		}
		return materialMap;
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public String replaceStr(String str) {
		String tempStr = "";
		if (!CommonStringUtil.isEmpty(str)) {
			tempStr = str.replace("\"", "");
		}
		return tempStr;
	}

	/**
	 * 获取Sorc_no和订购次数
	 * 
	 * @param str
	 * @return
	 */
	public String[] getOccurTimes(String str) {
		String tempStr = "";
		String[] tempStrs = null;
		if (!CommonStringUtil.isEmpty(str)) {
			if(str.contains("\"")){		//含有引号
				tempStr = str.replace("\"", "");
				if (tempStr.contains("/")) {
					tempStrs = tempStr.split("/");
				} else {
					tempStrs = new String[2];
					tempStrs[0] = tempStr;
					tempStrs[1] = "1";
				}
			}else{
				if (str.contains("/")) {
					tempStrs = str.split("/");
				} else {
					tempStrs = new String[2];
					tempStrs[0] = str;
					tempStrs[1] = "1";
				}
			}
		}
		return tempStrs;
	}

	/**
	 * 获取Sorc_no
	 * 
	 * @param srcSorc
	 * @return
	 */
	public static String trimSorc(String srcSorc) {
		String retSorc = srcSorc.replaceAll("-.{1}[0-9]{0,}$", ""); // {0,1}
		if (srcSorc.equals(retSorc)) {
			return retSorc;
		} else {
			return trimSorc(retSorc);
		}
	}
	
	public static String trimQuote(String srcSorc) {
		return srcSorc.replace("\"", "");
	}	


	/**
	 * 读取零件编码和工位
	 * 
	 * @param tempfilename
	 * @param conn
	 * @param errors
	 * @return
	 */
	public void readCodePosition(String tempfilename, SqlSession conn, List<MsgInfo> errors) {		InputStream in = null;
	StringBuffer strBuffer = new StringBuffer("\n");

	File file = new File(tempfilename);
	//String fileName = file.getName();
	try {
		in = new FileInputStream(file);
		HSSFWorkbook work = new HSSFWorkbook(in);

		PartialPositionMapper partialPositionDao = conn.getMapper(PartialPositionMapper.class);

		HSSFSheet sheet = work.getSheetAt(0);
		String model_name = sheet.getSheetName();

		//String model_name = fileName.substring(getIndex(fileName), fileName.length() - 4);
		String model_id = ReverseResolution.getModelByName(model_name, conn);

		if (!tempfilename.contains(model_name)) {
			strBuffer.append("型号" + model_name + "与文件名不一致" + "\n");
		}
		// 验证型号
		if (CommonStringUtil.isEmpty(model_id)) {
			strBuffer.append("型号" + model_name + "不存在" + "\n");
			return;
		}

		// 删除表数据
		partialPositionDao.deletePartialPosition(model_id);

		// 导入零件记录（key = code, value = 工位）
		Map<String, String> readPartialMap = new HashMap<String, String>();

		// 最近层上级零件processCode
		String[] position_ls = {"", "", ""};
		for (int sheetNumber = 0; sheetNumber < work.getNumberOfSheets(); sheetNumber++) {
			sheet = work.getSheetAt(sheetNumber);
			if (sheetNumber > 0) {
				position_ls[0] = sheet.getSheetName();
				position_ls[1] = "";
				position_ls[2] = "";
			}

			for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
				// 从属层次
				int btLevel = sheetNumber == 0 ? 0 : 1;

				PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
				HSSFRow row = sheet.getRow(rowNumber);
				if (row == null) continue;
				// 零件code
				String code = "";
				if (row.getCell(0) != null && !CommonStringUtil.isEmpty(getCellStringValue(row.getCell(0)))) {
					code = getCellStringValue(row.getCell(0));
				} else if (row.getCell(1) != null && !CommonStringUtil.isEmpty(getCellStringValue(row.getCell(1)))) {
					code = getCellStringValue(row.getCell(1));
					btLevel += 1;
				} else if (row.getCell(2) != null && !CommonStringUtil.isEmpty(getCellStringValue(row.getCell(2)))) {
					code = getCellStringValue(row.getCell(2));
					btLevel += 2;
				}
				code = code.toUpperCase();

				 //验证是否重复插入数据 
				if (readPartialMap.containsKey(code)) {
					// strBuffer.append("重复的零件" + code + "\n");
					continue;
				}

				// 零件ID
				String partial_id = ReverseResolution.getPartialByCode(code, conn);
				if (partial_id == null) {
					strBuffer.append("零件" + code + "不存在" + "\n");
					continue;
				}

				partialPositionEntity.setPartial_id(partial_id);

				partialPositionEntity.setModel_id(model_id);
				partialPositionEntity.setNew_partial_id(partial_id);

				if (btLevel > 0) {
					String parent_partial_id = ReverseResolution.getPartialByCode(position_ls[btLevel - 1], conn);
					partialPositionEntity.setParent_partial_id(parent_partial_id);
				}

				HSSFCell cell = row.getCell(3);
				String process_code = "";
				String position_id="";

				if (cell == null) {
					process_code = "";
				} else {
					process_code = getCellStringValue(cell);
					if (CommonStringUtil.isEmpty(process_code)) {
						process_code = "";
					}
				}

				// 如果没有工位/取得上级工位
				if ("".equals(process_code) && btLevel > 0) {
					process_code = readPartialMap.get(position_ls[btLevel - 1]);
				}

				// 记录
				readPartialMap.put(code, process_code);

				if (process_code != null && !process_code.contains(";")) {

					position_id = ReverseResolution.getPositionByProcessCode(process_code, conn);
					if (position_id == null) {
						position_id = "0";
					}

					// 插入数据库(单工位)
					partialPositionEntity.setPosition_id(position_id);
					partialPositionDao.insertPartialPosition(partialPositionEntity);

				} else if (cell!=null && cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {

					String strCellvalue = getCellStringValue(cell);
					String[] strProcessCode = strCellvalue.split(";");

					for (int i = 0; i < strProcessCode.length; i++) {
						process_code = strProcessCode[i];
						position_id = ReverseResolution.getPositionByProcessCode(process_code, conn);

						partialPositionEntity.setPosition_id(position_id);

						// 插入数据库(多工位)
						partialPositionDao.insertPartialPosition(partialPositionEntity);
					}
				}
				if (btLevel > 0 && btLevel < 3) position_ls[btLevel] = code;
			}

		}
	} catch (Exception e) {
		log.error(e.getMessage(), e);
	} finally {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		log.info(strBuffer.toString());
	}
}
    /**
     * 载入零件订购信息
     * @param form
     * @param request
     * @param tempfilename
     * @param conn
     * @param errors
     * @return
     */
	public List<MaterialPartialForm> readMaterialDetail(ActionForm form,HttpServletRequest request, String tempfilename, SqlSession conn,
			List<MsgInfo> errors) {
		List<MaterialPartialForm> partialFormList = new ArrayList<MaterialPartialForm>();
		// 循环读文件行，取得订购维修对象，并插入订购维修对象零件信息
		Set<String> avalibaleMaterialDetailSet = readOrderDetail(form, tempfilename, conn, errors, request.getSession());

		MaterialPartialMapper materialPartialDao = conn.getMapper(MaterialPartialMapper.class);

		// 无效文件
		if (avalibaleMaterialDetailSet != null
				&& avalibaleMaterialDetailSet.size() == 0) {
			MsgInfo e = new MsgInfo();
			e.setComponentid("file");
			e.setErrcode("info.partial.uselessFile");
			e.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.uselessFile"));
			errors.add(e);
		}
		if (errors.size() > 0) {
			return null;
		}

		// 设定维修对象列表到页面/Session
		for (String avalibaleMaterialDetail : avalibaleMaterialDetailSet) {
			MaterialPartialForm partialForm = new MaterialPartialForm();

			String[] sorcNoOccurTime = avalibaleMaterialDetail.split("/");
			String material_id = sorcNoOccurTime[0];

			String occurTimes = sorcNoOccurTime[1];

			PartialOrderMapper partialOrderDao = conn.getMapper(PartialOrderMapper.class);
			// 查询维修对象信息
			MaterialPartialEntity materialPartialEntity = materialPartialDao.getMaterialByMaterialId(material_id);
			if (materialPartialEntity == null) {
				continue;
			}
			materialPartialEntity.setMaterial_id(material_id);
			materialPartialEntity.setOccur_times(Integer.parseInt(occurTimes));

			// 订购次数是大于1的时候，分配零件到不良
			if (!"1".equals(occurTimes)) {
				// 不良
				materialPartialEntity.setBelongs(materialPartialEntity.getOccur_times() + 4); // 6 - 2
				// 更新到不良(工位确定的零件)
				partialOrderDao.updateMaterialPartialDetailWithNoGood(materialPartialEntity);
			} else {
				// 分配零件到BOM
				materialPartialEntity.setBelongs(1);
				partialOrderDao.updateMaterialPartialDetailWithBom(materialPartialEntity);

				// 取出订购时的零件信息和BOM相差的零件
				List<MaterialPartialDetailEntity> materialPartialDetailEntityList =partialOrderDao.searchMaterialPartialDetailQuantityUnmatch(materialPartialEntity);

				// 取出订购的零件和BOM相差的件数更新成未分配
				for (int i = 0; i < materialPartialDetailEntityList.size(); i++) {
					MaterialPartialDetailEntity resultBean = materialPartialDetailEntityList.get(i);

					/* 将bom的零件数量给分配到数量(订购的零件数量-bom零件数量) */
					resultBean.setQuantity(resultBean.getQuantity() - resultBean.getBom_quantity());

					/* 未分配 */
					resultBean.setBelongs(0);

					/* 更新到未分配并且件数变成相差的件数 */
					partialOrderDao.setOverBom(resultBean);

					/* 将bom的零件数量给分配到数量(订购的零件数量-bom零件数量) */
					resultBean.setQuantity(resultBean.getBom_quantity());

					/* BOM */
					resultBean.setBelongs(1);

					/* BOM需要的分配给BOM */
					partialOrderDao.insertMaterialPartialDetail(resultBean);
				}
			}
			BeanUtil.copyToForm(materialPartialEntity, partialForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			// 文件读取到的订购次数保存到FORM
			partialForm.setOccur_times(occurTimes);
			// 上传数据待定位
			partialForm.setBo_flg("8");

			partialFormList.add(partialForm);
		}

		request.getSession().setAttribute("partialFormPositioning", partialFormList);
		return partialFormList;
	}

	/**
	 * 读取零件订购详细信息
	 * 
	 * @param tempfilename
	 * @param conn
	 * @param errors
	 * @return
	 */
	public Set<String> readOrderDetail(ActionForm form, String tempfilename, SqlSession conn,
			List<MsgInfo> errors, HttpSession session) {
		Set<String> materialDetailMap = new HashSet<String>();

		InputStream in = null;
		StringBuffer strBuffer = new StringBuffer();
		try {
			in = new FileInputStream(tempfilename);
			HSSFWorkbook work = new HSSFWorkbook(in);
			PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);
			MaterialPartialDetailEntity materialPartialDetailEntity =null;

			// 需要管理员确认信息
			StringBuffer postMessages = new StringBuffer("");

			// 工作表
			HSSFSheet sheet = work.getSheetAt(0);
			
			String curMaterialID = "";
			// 读取日期截止
			Date availableDate = null;

			HSSFRow title_row = sheet.getRow(0);
			boolean needFilter = false;
			if (title_row == null || title_row.getCell(0) == null) {
				return materialDetailMap;
			} else {
				// 文件分别
				String title01 = title_row.getCell(0).getStringCellValue();
				if ("Order No.".equals(title01)) {
					needFilter = true;
					availableDate = RvsUtils.switchWorkDate(new Date(), -2);
				}
			}

			PartialPositionMapper ppDao = conn.getMapper(PartialPositionMapper.class);

			// 维修对象信息缓存（key = SORC No.）
			Map<String, MaterialEntity> materialEntities = new HashMap<String, MaterialEntity>();

			PartialMapper partialDao = conn.getMapper(PartialMapper.class);

			// 不能处理的维修对象列表(value = sorc || sorc + ot)
			Set<String> blackList = new HashSet<>();

			Map<String, Integer> noGoodTem = new HashMap<String, Integer>(); // key = sorc_no+occur_times
			// 读文件内容
			for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
				materialPartialDetailEntity = new MaterialPartialDetailEntity();
				PartialEntity partialEntity = new PartialEntity();

				MaterialMapper materialDao = conn.getMapper(MaterialMapper.class);
				MaterialEntity materialEntity = new MaterialEntity();
				HSSFRow row = sheet.getRow(rowNumber);
				HSSFCell cell = null;

				// 定购日期有效
				if (needFilter) {
					cell = row.getCell(12);
					// 只读取当天及前一工作日的记录，读到更早信息则完成读取
					if (cell == null || cell.getDateCellValue().before(availableDate)) {
						break;
					}
				}

				// 维修对象ID
				String sorc_no = "";
				String occurTime="";
				if (needFilter) {
					cell = row.getCell(4);
				} else {
					cell = row.getCell(0);
				}

				if (cell == null || "".equals(getCellStringValue(cell))) {
					break;
				}
				if (getCellStringValue(cell).contains("/")) {
					String[] sorcNoOccurTime = trimSorc(getCellStringValue(cell)).split("/");
					sorc_no = sorcNoOccurTime[0];
					occurTime = sorcNoOccurTime[1];
					materialPartialDetailEntity.setOccur_times(Integer.parseInt(occurTime));
				} else {
					sorc_no = trimSorc(getCellStringValue(cell));
					occurTime=1+"";					
				}
				sorc_no = sorc_no.toUpperCase();

				materialPartialDetailEntity.setOccur_times(Integer.parseInt(occurTime));

				if (blackList.contains(sorc_no) || blackList.contains(sorc_no + occurTime)) {
					continue;
				}

				if (materialEntities.containsKey(sorc_no)) {
					materialEntity = materialEntities.get(sorc_no);
				} else {
					materialEntity.setSorc_no(sorc_no);
					materialEntity.setBreak_back_flg(0);
					materialEntity.setFind_history("1");
					List<String> materialIDList = materialDao.searchMaterialIds(materialEntity);
					if (materialIDList.size() == 0) {
//						MsgInfo error = new MsgInfo();
//						error.setLineno(""+rowNumber);
//						error.setErrcode("dbaccess.recordNotExist");
//						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", sorc_no));
//						errors.add(error);
						blackList.add(sorc_no);
						log.error(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", sorc_no + " 维修对象"));
						continue;
					}
					String hittedMaterialID = materialIDList.get(0);
					materialEntity = materialDao.getMaterialNamedEntityByKey(hittedMaterialID);
					materialEntities.put(sorc_no, materialEntity);
				}
				
				String material_id = materialEntity.getMaterial_id();
				String model_id = materialEntity.getModel_id();

				if (!material_id.equals(curMaterialID)) {
					curMaterialID = material_id;
				}
				materialPartialDetailEntity.setMaterial_id(curMaterialID);
				materialPartialDetailEntity.setModel_id(model_id);
				// 零件ID
				if (needFilter) {
					cell = row.getCell(1);
				} else {
					cell = row.getCell(8);
				}
				String code = getCellStringValue(cell);
				partialEntity.setCode(code);
				List<String> partialIDList = partialDao.checkPartial(partialEntity);
				if (partialIDList.size() == 0) {
					// 零件不存在时
					// 读取零件名称信息
					if (needFilter) {
						cell = row.getCell(2);
					} else {
						cell = row.getCell(9);
					}
					postMessages.append("未登记的零件 " + code + " ： " + getCellStringValue(cell) + '\n');

					blackList.add(sorc_no);
					materialDetailMap.remove(material_id+"/"+occurTime);
					dao.cancelPartialOrder(material_id, occurTime);

					MsgInfo error = new MsgInfo();
					error.setLineno(""+rowNumber);
					error.setErrcode("dbaccess.recordNotExist");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", code + " 零件"));
					errors.add(error);
					continue;
				}
				String partial_id = partialIDList.get(0);
				materialPartialDetailEntity.setPartial_id(partial_id);

				// 判断定位零件从属
				PartialPositionEntity mCondition = new PartialPositionEntity();
				mCondition.setModel_id(materialEntity.getModel_id());
				mCondition.setPartial_id(partial_id);
				List<PartialPositionEntity> hitList = ppDao.searchPartialPosition(mCondition);

				if (hitList.size() == 0) {
					postMessages.append("零件 " + code + "未关联到型号" + materialEntity.getModel_name() + '\n');
				}

				// 数量
				if (needFilter) {
					cell = row.getCell(3);
				} else {
					cell = row.getCell(10);
				}
				Integer quantity = (int) cell.getNumericCellValue();
				materialPartialDetailEntity.setQuantity(quantity);

				log.info(code+"=============="+partial_id+"==============="+quantity);
				// 价格
				double price = 0.0;
				if (needFilter) {
				} else {
					cell = row.getCell(14);
					try {
						price = cell.getNumericCellValue();
					} catch (Exception e) {
						price = 0;
					}
				}
				if (price == 0) {
					PartialEntity pe = partialDao.getPartialByID(partial_id);
					if (pe.getPrice() == null) price = 0;
					else price = pe.getPrice().doubleValue();
				}
				materialPartialDetailEntity.setPrice(new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP));

				// 查询在material_partial_detail表是否有数据
				if (materialDetailMap.contains(material_id+"/"+occurTime)) { //合法数据
				} else { // 不是本次的并且已有数据
					List<MaterialPartialDetailEntity> materialDetailList = dao.checkMaterialPartialDetailIsNull(materialPartialDetailEntity);
					if (materialDetailList.size() > 0) {
						blackList.add(sorc_no + occurTime); // 不需要导入了
						continue;
					}
				}
				
				MaterialPartialMapper materialPartialDao = conn.getMapper(MaterialPartialMapper.class);
				
				List<MaterialPartialDetailEntity> materialPartialDetailEntityList = materialPartialDao.searchPartialPosition(materialPartialDetailEntity);
				if(materialPartialDetailEntityList.size()>0){
					MaterialPartialDetailEntity resultBean = materialPartialDetailEntityList.get(0);
					//读取的零件ID到零件定位表中查看是否已经过期，过期并且有心零件ID的话，用新零件ID进行以后的操作；
					if("1".equals(resultBean.getIsOverdue()) && "1".equals(resultBean.getIsEqual())){
						partial_id = resultBean.getNew_partial_id();
					}else if("1".equals(resultBean.getIsOverdue()) && "0".equals(resultBean.getIsEqual())){
						conn.rollback();
						log.info(code+"已经过期了，并且新零件ID也没有改变!");
						return null;
					}
				}
				materialPartialDetailEntity.setPartial_id(partial_id);
				// 查询material_partial表示否有本条数据 
//				List<MaterialPartialEntity> materialPartialList = dao.searchMaterialPartial(materialPartialDetailEntity);
//				if (materialPartialList.size() > 0) {
//					// 该维修对象已完成订购
//					continue;
//				}
				//  未定位
				//materialPartialDetailEntity.setBelongs(0);				
				
				materialPartialDetailEntity.setStatus(0);
				//插入wip_quantity
				materialPartialDetailEntity.setWaiting_quantity(quantity);
				// 可定位工位
				String position_id = dao.getPositionSimply(materialPartialDetailEntity);
				materialPartialDetailEntity.setPosition_id(position_id); 

				if (materialPartialDetailEntity.getOccur_times() == 1) {
					// 增加一列belongs(E\Q\D\N 或者其他类型)
					cell = row.getCell(16);
					if (cell == null) {
						materialPartialDetailEntity.setBelongs(1); // BOM
					} else {
						String belongs = getCellStringValue(cell);

						//belongs第一个字母是E、Q、D、N、任意其中一个 后面必须是
					    if(belongs.matches("^[EQDNeqdn]{1}\\d{0,}$")){ // 
					    	materialPartialDetailEntity.setBelongs(1);

					    	if("E".equalsIgnoreCase(belongs)){
								materialPartialDetailEntity.setBelongs(2);
							}else if("Q".equalsIgnoreCase(belongs)){
								materialPartialDetailEntity.setBelongs(3);
							}else if("D".equalsIgnoreCase(belongs)){
								materialPartialDetailEntity.setBelongs(4);
							}else if("N".equalsIgnoreCase(belongs)){
								materialPartialDetailEntity.setBelongs(5);
							}
					    	int l = belongs.length();
					    	//当belongs包含数字时
					    	if(l >= 2){
					    		String belongs_num = belongs.substring(1);
					    		String belongs_one =belongs.substring(0,1);
					    		if("E".equalsIgnoreCase(belongs_one)){
									materialPartialDetailEntity.setBelongs(2);
								}else if("Q".equalsIgnoreCase(belongs_one)){
									materialPartialDetailEntity.setBelongs(3);
								}else if("D".equalsIgnoreCase(belongs_one)){
									materialPartialDetailEntity.setBelongs(4);
								}else if("N".equalsIgnoreCase(belongs_one)){
									materialPartialDetailEntity.setBelongs(5);
								}
					    		if(quantity>Integer.parseInt(belongs_num)){
					    			
					    			materialPartialDetailEntity.setQuantity(Integer.parseInt(belongs_num));
					    			// 插入新数据到material_partial_detail表 
									dao.insertMaterialPartialDetail(materialPartialDetailEntity);
									
									materialPartialDetailEntity.setBelongs(BELONGS_BOM);
					    			materialPartialDetailEntity.setQuantity(quantity-Integer.parseInt(belongs_num));
					    			
					    		}else if(quantity==Integer.parseInt(belongs_num)){
					    			materialPartialDetailEntity.setQuantity(quantity);
					    		}else{
					    			// 回滚数据库 弹出零件订购文件格式错误信息
					    			MsgInfo error = new MsgInfo();
									error.setComponentid("material_partial_detail_key");
									error.setErrcode("info.modify.partialOrderFileType");
									error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
											"info.modify.partialOrderFileType", "零件订购文件", 1,
											materialPartialDetailEntity.getPosition_id()));
									errors.add(error);
					    			conn.rollback();
					    			return null;
					    		}			    		
					    	}
							/*//TODO 弹出零件订购文件格式错误信息
					    	MsgInfo error = new MsgInfo();
							error.setComponentid("material_partial_detail_key");
							error.setErrcode("info.modify.partialOrderFileType");
							error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
									"info.modify.partialOrderFileType", "零件订购文件", 1,
									materialPartialDetailEntity.getPosition_id()));
							errors.add(error);*/
					    } else {
							materialPartialDetailEntity.setBelongs(1); // BOM
					    }
					}
				} else {
					cell = row.getCell(16);
					if (cell == null) {
						// 弹出零件订购文件格式错误信息
				    	MsgInfo error = new MsgInfo();
						error.setComponentid("material_partial_detail_key");
						error.setErrcode("info.modify.partialOrderFileType");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
								"info.partial.orderNotGoodReason", sorc_no, code));
						errors.add(error);
		    			conn.rollback();
		    			return null;
		    		} else {
						String belongs = getCellStringValue(cell);
						if("FX".equalsIgnoreCase(belongs)){ // 发现
							materialPartialDetailEntity.setBelongs(6);
						}else if("BL".equalsIgnoreCase(belongs)){ // 不良
							materialPartialDetailEntity.setBelongs(7);
						}else if("LJ".equalsIgnoreCase(belongs)){ // 零件
							materialPartialDetailEntity.setBelongs(8);
						}else if("QR".equalsIgnoreCase(belongs)){
							materialPartialDetailEntity.setBelongs(10);
						} else {
							// 弹出零件订购文件格式错误信息
					    	MsgInfo error = new MsgInfo();
							error.setComponentid("material_partial_detail_key");
							error.setErrcode("info.modify.partialOrderFileType");
							error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
									"info.partial.orderNotGoodReason", sorc_no, code));
							errors.add(error);
			    			conn.rollback();
			    			return null;
			    		}
					}
				}

				// 插入新数据到material_partial_detail表 
				dao.insertMaterialPartialDetail(materialPartialDetailEntity);

				// 维修对象ID （零件ID和价格）
				materialDetailMap.add(material_id+"/"+occurTime);
			}

			if (postMessages.length() > 0) {
				// 发送人设定为登录用户
				LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

				// 信息推送
				PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
				PostMessageEntity pmEntity = new PostMessageEntity();
				pmEntity.setSender_id(user.getOperator_id());
				String messages = postMessages.toString();
				log.warn(messages);
				if (messages.length() > 182) {
					messages = messages.substring(0, 182) + "\n"; // TODO What's 182
				}
				pmEntity.setContent("导入订购单中的问题\n" + messages + user.getName());
				pmEntity.setLevel(1);
				pmEntity.setReason(PostMessageService.PARTIAL_RELATIVE_LOST);

				pmMapper.createPostMessage(pmEntity);

				CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
				String lastInsertID = commonMapper.getLastInsertID();
				pmEntity.setPost_message_id(lastInsertID);

				// 查询系统管理员
				OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

				List<OperatorEntity> systemmers = oMapper.getOperatorWithRole(RvsConsts.ROLE_PARTIAL_MANAGER);

				for (OperatorEntity systemmer : systemmers) {
					pmEntity.setReceiver_id(systemmer.getOperator_id());
					pmMapper.createPostMessageSendation(pmEntity);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			log.info(strBuffer.toString());
		}
		return materialDetailMap;
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
			strCell = String.valueOf((int) cell.getNumericCellValue());
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
		
		if (strCell.equals("") || strCell == null) {
            return "";
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
	private static Date getCellDateValue(HSSFCell cell) {
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
	 * 更新零件入库预定日期
	 * @param fileName
	 * @param conn
	 * @param errors
	 */
	public void updateArrivePlanDate(String fileName, SqlSessionManager conn, List<MsgInfo> errors){
		Map<String, Map<String, Date>> partialBoMap = readBoExcel(fileName, conn, errors);
		Set<String> materialIDAndOccurTimesSet = partialBoMap.keySet();
		Iterator<String> iterator = materialIDAndOccurTimesSet.iterator();
		MaterialPartialDetailEntity entity=null;
		PartialAssignMapper dao=conn.getMapper(PartialAssignMapper.class);
		MaterialPartialMapper mpdao=conn.getMapper(MaterialPartialMapper.class);

		while (iterator.hasNext()) {
			entity=new MaterialPartialDetailEntity();
			String materialIDAndOccurTimes = iterator.next();
			String materialId = materialIDAndOccurTimes.substring(0, materialIDAndOccurTimes.length() - 1);// 维修对象ID
			String occurTimes = materialIDAndOccurTimes.substring(materialIDAndOccurTimes.length() - 1,materialIDAndOccurTimes.length());// 订购次数

			entity.setMaterial_id(materialId);
			entity.setOccur_times(Integer.valueOf(occurTimes));

			Map<String, Date> partialMap = partialBoMap.get(materialIDAndOccurTimes);

			Set<String> partialIDAndDateSet=partialMap.keySet();
			Iterator<String> it = partialIDAndDateSet.iterator();
			while (it.hasNext()){
				String partialID=it.next();//零件 ID
				Date date=partialMap.get(partialID); //入库预定日
				
				entity.setPartial_id(partialID);
				entity.setArrival_plan_date(date);
				dao.updateArrivePlanDate(entity);
			}
		}
		// Add by Gonglm 2014/1/24 Start
		// 根据零件的入库预定日更新维修对象入库预定日
		// conn.commit();
		mpdao.updateArrivePlanDateNoBoOnPartial();
		mpdao.updateArrivePlanDateBoOnPartial(null);
		// Add by Gonglm 2014/1/24 End
		// Add by Gonglm 2014/2/11 Start
		mpdao.updateArrivedPlanDateBoResolvedOnPartial();
		// Add by Gonglm 2014/2/11 End
	}
	
	
	/**
	 * 读取BO零件入库预定日文件
	 * @param fileName
	 * @param conn
	 * @param errors
	 */
	public Map<String, Map<String, Date>> readBoExcel(String fileName, SqlSessionManager conn, List<MsgInfo> errors){
		InputStream in = null;
		Map<String, Map<String, Date>> materialMap = new HashMap<String, Map<String, Date>>();
		StringBuffer buffer=new StringBuffer();
		try {
			in = new FileInputStream(fileName);// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = work.getSheetAt(0);// 获取Sheet
			Map<String, Date> partialMap = null;

			Set<String> notRxitsSorcs = new HashSet<String>();
			for (int iRow = 1; iRow <= sheet.getLastRowNum(); iRow++) {
				HSSFRow row = sheet.getRow(iRow);
				if (row != null) {
					if(row.getLastCellNum()<9)
						continue;

					// 入库预定日列
					Date strDate=getCellDateValue(row.getCell(6));
					if(strDate == null) {
						break; // continue;
					}
					
					String[] sorcAndOccurTimes = getOccurTimes(getCellStringValue(row.getCell(0)));
					if (sorcAndOccurTimes == null)
						continue;
					
					String occur_times = sorcAndOccurTimes[1];// 订购次数
					String sorc_no = trimSorc(sorcAndOccurTimes[0]);//SORC NO.
					sorc_no = sorc_no.toUpperCase();

					MaterialEntity entity = new MaterialEntity();
					MaterialMapper dao = conn.getMapper(MaterialMapper.class);
					entity.setSorc_no(sorc_no);
					entity.setBreak_back_flg(0); // 无返还
					entity.setFind_history("1"); // 未出货

					if (notRxitsSorcs.contains(sorc_no)) {
						continue;
					}
					
					List<String> listTemp = dao.searchMaterialIds(entity);
					if(listTemp.size()==0){
						notRxitsSorcs.add(sorc_no);//放入Set集合中
//							buffer.append("维修对象"+currentSorcNO+"不存在\n");
						log.info("维修对象 "+sorc_no+" 不存在");
						continue;
					}
					String material_id = listTemp.get(0);

					partialMap = materialMap.get(material_id + occur_times);

					if (partialMap == null) {
						partialMap = new HashMap<String, Date>();
						materialMap.put(material_id + occur_times, partialMap);
						partialMap = materialMap.get(material_id + occur_times);
					}

					String partialCode =getCellStringValue(row.getCell(8));// 零件品名
					if(CommonStringUtil.isEmpty(partialCode)){
						continue;
					}
					PartialEntity pEntity = new PartialEntity();
					pEntity.setCode(partialCode);
					PartialMapper pDao = conn.getMapper(PartialMapper.class);
					List<String> pList = pDao.checkPartial(pEntity);
					if (pList.size() == 0) {
						buffer.append("零件 "+partialCode+" 不存在\n");
						log.info("零件 "+partialCode+" 不存在");
						continue;
					}
					String partial_id = pList.get(0);

					log.info(sorc_no + "/" + occur_times + " c " + partial_id + ":" + strDate);
					partialMap.put(partial_id, strDate);
				}
			}
			
			if(buffer.toString().isEmpty()){
			}else{
				MsgInfo info=new MsgInfo();
				info.setErrmsg(buffer.toString());
				errors.add(info);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return materialMap;
	}
	
	/**
	 * 上传拉动台数设定
	 * @param fileName
	 * @param conn
	 * @param errors
	 */
	public void uploadForecastSetting(String fileName, SqlSessionManager conn, List<MsgInfo> infos)throws Exception{
		InputStream in = null;
		
		try{
			in = new FileInputStream(fileName);// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = work.getSheetAt(0);// 获取Sheet
			ModelLevelSetForm modelLevelSetForm=null;
			LevelModelLeedsMapper dao=conn.getMapper(LevelModelLeedsMapper.class);
			for (int iRow = 2; iRow <= sheet.getLastRowNum(); iRow++) {
				HSSFRow row=sheet.getRow(iRow);
				if(row==null){
					continue;
				}
				
				if(row.getLastCellNum()<26){
					continue;
				}
				
				HSSFCell cell=row.getCell(24);
				HSSFCell echelonCell=row.getCell(25);
				
				if(getCellStringValue(cell)=="" && getCellStringValue(echelonCell)==""){//单元格为空
					continue;
				}
				
				String strForecastSettingCellValue=getCellStringValue(cell);//更改拉动台数设定
				String strEchelonValue = getCellStringValue(echelonCell);//修改梯队
				String echelon = "";
				
				if("第一梯队".equals(strEchelonValue)){
					echelon = "1";
				}else if("第二梯队".equals(strEchelonValue)){
					echelon = "2";
				}else if("第三梯队".equals(strEchelonValue)){
					echelon = "3";
				}
			
				
				if(!isNum(strForecastSettingCellValue) && echelon == ""){
					continue;
				}else{
					HSSFCell levelAndModelCell=row.getCell(1);
					
					if(getCellStringValue(levelAndModelCell)==""){
						continue;
					}
					
					String levelAndModel=getCellStringValue(levelAndModelCell);//等级型号
					String levelName=levelAndModel.substring(0, 2);//等级
					String modelName=levelAndModel.substring(2,levelAndModel.length());//型号
					
					String level_id = CodeListUtils.getKeyByValue("material_level", levelName, null);
					String model_id = ReverseResolution.getModelByName(modelName, conn);//型号ID
					
					modelLevelSetForm=new ModelLevelSetForm();
					modelLevelSetForm.setLevel(level_id);
					modelLevelSetForm.setModel_id(model_id);
					modelLevelSetForm.setForecast_setting(strForecastSettingCellValue);
					modelLevelSetForm.setEchelon(echelon);
					
					ModelLevelSetEntity entity=new ModelLevelSetEntity();
					//复制表单数据到对象
					BeanUtil.copyToBean(modelLevelSetForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
					dao.updateForecastSetting(entity);
				}
			}
			MsgInfo info=new MsgInfo();
			info.setErrmsg("上传完毕");
			infos.add(info);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				
		}
	}
	
	/**
	 * 判断是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str){
		 Pattern pattern = Pattern.compile("[0-9]+");
		 Matcher isNum = pattern.matcher(str);
	     if( !isNum.matches() ){
              return false;
         }
         return true;
	}
	
	/**
	 * 上传基准值设定
	 * @param fileName
	 * @param conn
	 * @param infos
	 */
	public void uploadTotalForeboardCount(String fileName, HttpServletRequest request, 
			Date end_date, SqlSessionManager conn,
			List<MsgInfo> msgInfos, List<MsgInfo> infos,
			List<PartialBaseLineValueEntity> list) {
		InputStream in = null;
		PartialBaseLineValueMapping dao = conn.getMapper(PartialBaseLineValueMapping.class);
		try{
			in = new FileInputStream(fileName);// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = work.getSheetAt(0);// 获取Sheet

			PartialBaseLineValueForm partialBaseLineValueForm=null;
			Date start_date=null;

			StringBuffer errorsBuffer = new StringBuffer();
			for (int iRow = 2; iRow <= sheet.getLastRowNum(); iRow++) {
				partialBaseLineValueForm=new PartialBaseLineValueForm();
				
				HSSFRow row=sheet.getRow(iRow);
				if(row==null){
					errorsBuffer.append("第" + (iRow + 1) + "行数据格式不正确\n");
					continue;
				}
				
				if(row.getLastCellNum()<17){
					continue;
				}
				
				HSSFCell sorceCellValue=row.getCell(14);//SORCWH更改基准量设置
				HSSFCell wh2pCellValue=row.getCell(15);//WH2P更改基准量设置
				HSSFCell ogzCellValue=row.getCell(16);//OGZ更改基准量设置
				if(getCellStringValue(sorceCellValue)=="" && getCellStringValue(wh2pCellValue)=="" &&  getCellStringValue(ogzCellValue)==""){//SORCWH更改基准量设置和WH2P更改基准量设置都为空时继续读取下一行
					continue;
				}else{
					HSSFCell cell=row.getCell(1);
					String partialCode=getCellStringValue(cell);//零件code
					String partialID=ReverseResolution.getPartialByCode(partialCode, conn);//零件ID
					if (partialID == null) {
						errorsBuffer.append("零件" + partialCode + "不存在！\n");
						continue;
					}
					partialBaseLineValueForm.setPartial_id(partialID);
					
					LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
					String operatorId=loginData.getOperator_id();
					partialBaseLineValueForm.setUpdated_by(operatorId);
					
					String sorcwForeboardCount=getCellStringValue(sorceCellValue);//SORCWH更改基准量设置
					if(isNum(sorcwForeboardCount)){//纯数字
						partialBaseLineValueForm.setSorcwh_foreboard_count(sorcwForeboardCount);
						partialBaseLineValueForm.setIdentification("1");
						PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
						
						//复制表单到数据对象
						BeanUtil.copyToBean(partialBaseLineValueForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
						if(end_date==null){
							MsgInfo info=new MsgInfo();
							info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","基准值 起效日期"));
							info.setComponentid("partial_base_line_value");
							msgInfos.add(info);
						}else{
							start_date=dao.searchLastStartDate(entity);
							entity.setSorcwh_end_date(end_date);
							if(start_date==null){
								list.add(entity);
							}else{
								if(end_date.after(start_date)){//有效区间结束日期大于开始日期
									list.add(entity);
								}else{
									MsgInfo info=new MsgInfo();
									info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidTimeRangeValue","起效日期","大于",DateUtil.toString(start_date, DateUtil.DATE_PATTERN)));
									info.setComponentid("partial_base_line_value");
									msgInfos.add(info);
								}
							}
						}
					}
					
					if(msgInfos.size()>0){
						break;
					}
					
					String wh2pForeboardCount=getCellStringValue(wh2pCellValue);//WH2P更改基准量设置
					if(isNum(wh2pForeboardCount)){//纯数字
						partialBaseLineValueForm.setWh2p_foreboard_count(wh2pForeboardCount);
						partialBaseLineValueForm.setIdentification("4");
						PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
						//复制表单到数据对象
						BeanUtil.copyToBean(partialBaseLineValueForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
						
						if(end_date==null){
							MsgInfo info=new MsgInfo();
							info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","基准值 起效日期"));
							info.setComponentid("partial_base_line_value");
							msgInfos.add(info);
						}else{
							start_date=dao.searchLastStartDate(entity);
							entity.setWh2p_end_date(end_date);
							if(start_date==null){
								list.add(entity);
							}else{
								if(end_date.after(start_date)){//有效区间结束日期大于开始日期
									list.add(entity);
								}else{
									MsgInfo info=new MsgInfo();
									info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidTimeRangeValue","起效日期","大于",DateUtil.toString(start_date, DateUtil.DATE_PATTERN)));
									info.setComponentid("partial_base_line_value");
									msgInfos.add(info);
								}
							}
						}
					}
					
					if(msgInfos.size()>0){
						break;
					}
					
					String ogzForeboardCount=getCellStringValue(ogzCellValue);//OGZ更改基准量设置
					if(isNum(ogzForeboardCount)){//纯数字
						partialBaseLineValueForm.setOgz_foreboard_count(ogzForeboardCount);
						partialBaseLineValueForm.setIdentification("2");
						PartialBaseLineValueEntity entity=new PartialBaseLineValueEntity();
						//复制表单到数据对象
						BeanUtil.copyToBean(partialBaseLineValueForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
						
						if(end_date==null){
							MsgInfo info=new MsgInfo();
							info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","基准值 起效日期"));
							info.setComponentid("partial_base_line_value");
							msgInfos.add(info);
						}else{
							start_date=dao.searchLastStartDate(entity);
							entity.setOgz_end_date(end_date);
							if(start_date==null){
								list.add(entity);
							}else{
								if(end_date.after(start_date)){//有效区间结束日期大于开始日期
									list.add(entity);
								}else{
									MsgInfo info=new MsgInfo();
									info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidTimeRangeValue","起效日期","大于",DateUtil.toString(start_date, DateUtil.DATE_PATTERN)));
									info.setComponentid("partial_base_line_value");
									msgInfos.add(info);
								}
							}
						}
					}
					
					if(msgInfos.size()>0){
						break;
					}
					
				}
			}

			if (errorsBuffer.toString().length() > 0) {
				MsgInfo error = new MsgInfo();
				error.setErrmsg(errorsBuffer.toString());
				msgInfos.add(error);
			}

			if(msgInfos.size()==0){
				MsgInfo info=new MsgInfo();
				info.setErrmsg("上传完毕");
				infos.add(info);
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getFile2Local(ActionForm form, List<MsgInfo> errors) {
		//
		UploadForm upfileForm = (UploadForm) form;
		// 取得上传的文件
		FormFile file = upfileForm.getFile();
		FileOutputStream fileOutput;

		if (file == null || CommonStringUtil.isEmpty(file.getFileName())) {
			MsgInfo error = new MsgInfo();
			error.setErrcode("file.notExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.notExist"));
			errors.add(error);
			return "";
		}
		Date today = new Date();
		String tempfilename = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");

		File fMonthPath = new File(tempfilename);
		if (!fMonthPath.exists()) {
			fMonthPath.mkdirs();
		}
		fMonthPath = null;

		tempfilename += "\\" + today.getTime() + file.getFileName();

		log.info("FileName:" + tempfilename);
		try {
			// if (file.getFileName()
			fileOutput = new FileOutputStream(tempfilename);
			fileOutput.write(file.getFileData());
			fileOutput.flush();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			log.error("FileNotFound:" + e.getMessage());
		} catch (IOException e) {
			log.error("IO:" + e.getMessage());
		}
		return tempfilename;
	}

	public static String toXls2003(String path) {
		try {
			String target = path.replaceAll("\\.xlsx", ".xls");
			XlsUtil xlsUtil = new XlsUtil(path);
			xlsUtil.SaveAsXls2003(target);
			return target;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return path;
		}
	}

	/**
	 * 读取SORC数据
	 * @param fileName
	 * @param conn
	 * @param errors
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Integer> readSetInlineStatus(List<MaterialForm> recept, String fileName, SqlSession conn, List<MsgInfo> errors, List<MsgInfo> msgs) throws Exception {
		InputStream in = null;
		Map<String, Integer> ret = new HashMap<String, Integer>();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date today = cal.getTime();
		HolidayMapper hMapper = conn.getMapper(HolidayMapper.class);
		Map<String, Object> hcond = new HashMap<>();
		hcond.put("date", today);
		hcond.put("interval", -1);
		Date lastday = hMapper.addWorkdays(hcond);
		MaterialFactService mfService = new MaterialFactService();

		int delivered = 0;
		int shipped = 0;
		int wip_count = 0;
		int wip_overtime_count = 0;
		int approved = 0;
		int inlined = 0;

		int colSorcNo = 0;
		int colLocation = 2;
		int colOcm = 3;
		int colEsasNo = 4;
		int colModelName = 5;
		int colSerialNo = 6;
		int colType = 7;
		// OCM 修理等级
		int colOcmRank = 8;
		// OCM Decide修理等级
		int colOcmDecideRank = 9;
		int colCafeteriaPlan = 10;
		int colDirect = 11;
		int colLevel = 13;
		int colCustomer = 18;
		// OCM 出货
		int colOcmDeliver = 23;
		// int colAccepted = 24;
		int colDelivered = 25;
		int colReceived = 26;
		int colShipping = 35;
		int colQuotated = 28;
		int colAppove = 29;
		int colInline = 32;

		try {
			FileUtils.copyFile(new File(fileName), new File(fileName.replaceAll("\\" + PathConsts.LOAD_TEMP, "\\\\Imports")));

			in = new FileInputStream(fileName);// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = work.getSheetAt(0);// 获取Sheet

			// summary 39行
			
			HSSFRow row = sheet.getRow(0);
			if (row == null) {
				MsgInfo e = new MsgInfo();
				e.setComponentid("file");
				e.setErrcode("file.emptyFile");
				e.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.emptyFile"));
				errors.add(e);
				return null;
			}
			// 不是Summary文件
			int coloumNum=row.getPhysicalNumberOfCells();
			if (coloumNum < SUMMARY_FILE_COLS || coloumNum > 60) {
				errors.clear();
				MsgInfo e = new MsgInfo();
				e.setErrcode("notSummaryFile");
				errors.add(e);
				return null;
			}

			MaterialService mServ = new MaterialService();
			// 取得按列名取得列定义
			for (int iCol = 0; iCol < SUMMARY_FILE_COLS; iCol++) {
				String title = getCellStringValue(row.getCell(iCol));

				switch(title) {
				case "Order No." :
					colSorcNo = iCol; break;
				case "Location(SORC)" :
					colLocation = iCol; break;
				case "委托处（维修站）" :
					colOcm = iCol; break;
				case "修理委托人修理顺序号码" :
					colEsasNo = iCol; break;
				case "型号" :
					colModelName = iCol; break;
				case "机身号" :
					colSerialNo = iCol; break;
				case "Type" :
					colType = iCol; break;
				case "Cafeteria Plan" :
					colCafeteriaPlan = iCol; break;
				case "Direct" :
					colDirect = iCol; break;
				case "修理等级" :
					colLevel = iCol; break;
				case "顾客" :
					colCustomer = iCol; break;
//				case "到货接受" :
//					colAccepted = iCol; break;
				case "Delivered(SORC)" :
					colDelivered = iCol; break;
				case "Received(SORC)" :
					colReceived = iCol; break;
				case "修理同意" :
					colAppove = iCol; break;
				case "OCM 修理等级" :
					colOcmRank = iCol; break;
				case "OCM Decide修理等级" :
					colOcmDecideRank = iCol; break;
				case "OCM 出货" :
					colOcmDeliver = iCol; break;
				}
			}

			AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);
			MaterialMapper mDao = conn.getMapper(MaterialMapper.class);

			for (int iRow = 1; iRow <= sheet.getLastRowNum(); iRow++) {
				row = sheet.getRow(iRow);
				if (row != null) {
					// 
					String sSorc_no = getCellStringValue(row.getCell(colSorcNo));
					String sLocation = getCellStringValue(row.getCell(colLocation));
					Date dDelivered = getCellDateValue(row.getCell(colDelivered));
					Date dReceived = getCellDateValue(row.getCell(colReceived));
					String sType = getCellStringValue(row.getCell(colType));
					Date dShipping = getCellDateValue(row.getCell(colShipping));
					Date dAppove = getCellDateValue(row.getCell(colAppove));
					Date dInline = getCellDateValue(row.getCell(colInline));
					Date deOcmDeliver = getCellDateValue(row.getCell(colOcmDeliver));

					// OGZ
					if ("OGZ-SORC".equalsIgnoreCase(sLocation)) {

						// Delivered(SORC) = today OGZ受理
						if (dReceived != null && DateUtil.compareDate(dReceived, today) == 0) {
						// if (dDelivered != null && DateUtil.compareDate(dDelivered, today) == 0) {
							delivered++;
						}
	
						// Type = Approved 出货指示 = today OGZ出货
						if ("Approved".equals(sType) && dShipping != null && DateUtil.compareDate(dShipping, today) == 0) {
							shipped++;
						}
	
						// Received(SORC) NOT Empty  Type IS Empty OGZ WIP
						if (CommonStringUtil.isEmpty(sType) && dReceived != null) {
							wip_count++;
							Date dQuotated = getCellDateValue(row.getCell(colQuotated));
							if (dQuotated != null && (today.getTime() - dQuotated.getTime() > 5184000000l)) { // 5184000000 = 60*24*60*60*1000 
								wip_overtime_count++;
							}
						}
	
						// Approved 修理同意 = Today {
						if ("Approved".equals(sType) && dAppove != null && DateUtil.compareDate(dAppove, today) == 0) {
							approved++;
						}					
	
						// 投线
						if ("Approved".equals(sType) && dInline != null && DateUtil.compareDate(dInline, today) == 0) {
							inlined++;
						}

						MaterialEntity mEntity = new MaterialEntity();
						mEntity.setSorc_no(sSorc_no);
						String id = dao.checkSorcNo(mEntity);
						if (!CommonStringUtil.isEmpty(id)) {
							// OGZ移出
							List<String> ids = new ArrayList<String>();
							ids.add(id);
							mDao.updateMaterialReturn(ids);
						}						
					}
					// OSH
					else if ("OSH-SORC".equalsIgnoreCase(sLocation)) {

						// OSH 受理 判断当天或前一工作日的OCM发货信息，没有导入RVS的，进行导入
						if (deOcmDeliver != null
								&& (DateUtil.compareDate(deOcmDeliver, today) == 0 || DateUtil.compareDate(deOcmDeliver, lastday) == 0)) {
							
							// 机身号
							String serial_no = getCellStringValue(row.getCell(colSerialNo));

							if (CommonStringUtil.isEmpty(serial_no)) {
								MsgInfo info = new MsgInfo();
								info.setErrcode("validator.required");
								info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "第" + iRow
										+ "行机身号"));
								errors.add(info);
								continue;
							}
							MaterialForm lineform = new MaterialForm();

							lineform.setSerial_no(serial_no);
							lineform.setSorc_no(sSorc_no);
							lineform.setOcm(reverOcm(getCellStringValue(row.getCell(colOcm))));
							lineform.setEsas_no(getCellStringValue(row.getCell(colEsasNo)));
							lineform.setModel_name(getCellStringValue(row.getCell(colModelName)));

							if (CommonStringUtil.isEmpty(lineform.getModel_name())) {
								continue;
							}
							String model_id = ReverseResolution.getModelByName(lineform.getModel_name(), conn);
							if (model_id == null) {
								MsgInfo info = new MsgInfo();
								info.setErrcode("model.notExist");
								info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("model.notExist",
										lineform.getModel_name()));
								errors.add(info);
								continue;
							}

							MaterialEntity checkEntity = new MaterialEntity();
							checkEntity.setModel_id(model_id);
							checkEntity.setSerial_no(serial_no);
							// 判断RVS中是否已经存在
							if (dao.checkModelSerialNo(checkEntity) != null) {
								// msg
								MsgInfo info = new MsgInfo();
								info.setErrcode("dbaccess.columnNotUnique");
								info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique",
										"型号机身号", lineform.getModel_name() + " / " + serial_no, "维修对象"));
								msgs.add(info);
								continue;
							}

							String sLevel = reverLevel(getCellStringValue(row.getCell(colLevel)));
							lineform.setModel_id(model_id);
							lineform.setLevel(sLevel);
							List<MsgInfo> tsErrors = new ArrayList<MsgInfo>();
							// 是否过期
							mServ.checkModelDepacy(lineform, conn, tsErrors);
							if (tsErrors.size() > 0) {
								errors.addAll(tsErrors);
								continue;
							}

							String ocmRankDecide = getCellStringValue(row.getCell(colOcmDecideRank));
							String ocmRank = reverOcmLevel("".equals(ocmRankDecide) ? getCellStringValue(row.getCell(colOcmRank)) : ocmRankDecide);
							lineform.setOcm_rank(ocmRank);

							lineform.setOcm_deliver_date(DateUtil.toString(deOcmDeliver, DateUtil.DATE_PATTERN));
//
//							MaterialEntity mEntity = new MaterialEntity();
//							BeanUtil.copyToBean(lineform, mEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
//							String id = dao.checkSorcNo(mEntity);
//							if (!CommonStringUtil.isEmpty(id)) {
//								continue;
//							}
//							id = dao.checkEsasNo(mEntity);
//							if (!CommonStringUtil.isEmpty(id)) {
//								continue;
//							}
//							id = dao.checkModelSerialNo(mEntity);
//							if (!CommonStringUtil.isEmpty(id)) {
//								continue;
//							}

							lineform.setAgreed_date(parseNumJavaFormat(getCellStringValue(row.getCell(colAppove)))); // Z+1 // AA+2
							lineform.setMaterial_id("Line" + iRow);
							lineform.setFix_type("1");

							String cafeteriaPlan = getCellStringValue(row.getCell(colCafeteriaPlan));
							if (!cafeteriaPlan.trim().equals("")) {
								lineform.setSelectable("1");
							}

							String direct = getCellStringValue(row.getCell(colDirect));
							if (!direct.trim().equals("")) {
								lineform.setDirect_flg("1");
							}

							String custom = getCellStringValue(row.getCell(colCustomer)); // Q+1
							if (custom.trim().startsWith("OCM")) {
								lineform.setService_repair_flg("3"); // 备品
							}

							CustomerService cservice = new CustomerService();

							if (!isEmpty(custom)) {
								int iOcm = 0;
								try {
									iOcm = Integer.parseInt(lineform.getOcm());
								} catch (Exception e) {}
								lineform.setCustomer_id(cservice.getCustomerStudiedId(custom, iOcm, conn));
								lineform.setCustomer_name(custom);
							}

							recept.add(lineform);
						} else {
							if (CommonStringUtil.isEmpty(sSorc_no)) {
								continue;
							}

							// OSH Unrepair
							if (dAppove != null && sType.toLowerCase().startsWith("unrepair")) {
								mfService.updateUnrepairBySorc(sSorc_no, dAppove, conn);
							}

							if (dShipping != null) {
								MaterialEntity cond = new MaterialEntity();
								if (sSorc_no.length() == 15 && "40".equals(sSorc_no.substring(6, 8))) {
									cond.setSorc_no(sSorc_no.substring(0, 7) + sSorc_no.substring(8));
								} else {
									cond.setSorc_no(sSorc_no);
								}
								cond.setOcm_shipping_date(dShipping);
								dao.updateOcmShippingBySorc(cond);

								// FSE 数据同步
								try{
									MaterialEntity condSorc = new MaterialEntity();
									condSorc.setSorc_no(sSorc_no);
									List<String> material_ids = mDao.searchMaterialIds(condSorc);
									if (material_ids != null && material_ids.size() > 0)
										FseBridgeUtil.toUpdateMaterialProcess(material_ids.get(0), "load");
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		ret.put("delivered", delivered);
		ret.put("shipped", shipped);
		ret.put("wip_count", wip_count);
		ret.put("wip_overtime_count", wip_overtime_count);
		ret.put("approved", approved);
		ret.put("inlined", inlined);

		return ret;
	}
     
	/**
	 * 单元/保内返修损金文件导入
	 * @param tempfilename
	 * @param conn
	 * @param errors
	 * @throws Exception 
	 */
	public void readLossExcel(String fileName, SqlSessionManager conn, List<MsgInfo> errors, List<MsgInfo> msgInfo) throws Exception {
		InputStream in = null;
		StringBuffer strBuffer = new StringBuffer();
		try {
			in = new FileInputStream(fileName);
			// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = null;	
		
			MaterialMapper materialMapper = conn.getMapper(MaterialMapper.class);
			
			MaterialPartialDetailEntity materialPartialDetailEntity = new MaterialPartialDetailEntity();

			SorcLossEntity sorcLossEntity = new SorcLossEntity();
			SorcLossMapper sorcLossMapper = conn.getMapper(SorcLossMapper.class);

			MaterialEntity mCond = new MaterialEntity();

			CommonMapper cMapper = conn.getMapper(CommonMapper.class); 
			AcceptanceMapper mMapper = conn.getMapper(AcceptanceMapper.class);	

			MaterialPartialMapper materialPartialMapper = conn.getMapper(MaterialPartialMapper.class);
			
			PartialOrderMapper partialOrderMapper  = conn.getMapper(PartialOrderMapper.class);

			//遍历sheet
			for (int sheetNumber = 0; sheetNumber < 1; sheetNumber++) {
				sheet = work.getSheetAt(sheetNumber);
				if (sheet == null) return;

				//判断该单元/保内返修损金文件---导入了多少条数据
				int index=0;
				
				boolean isFlg = false;
				Map<String, String> sorcMap = new HashMap<String, String>();

				//遍历
				for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {

					MaterialEntity materialEntity = new MaterialEntity();

					sheet = work.getSheetAt(sheetNumber);
					HSSFRow row = sheet.getRow(rowNumber);
					if (row == null) continue;
					//出货日期---------------material表
					Date shipping_date =getCellDateValue(row.getCell(1));
					//String shipping = DateUtil.toString(shipping_date,DateUtil.ISO_DATE_PATTERN);
					materialEntity.setOcm_deliver_date(shipping_date);
					
					//修理编号no
					String sorc_no = getCellStringValue(row.getCell(2));
					if (isEmpty(sorc_no)) continue;

					sorc_no = sorc_no.toUpperCase();
					sorcLossEntity.setSorc_no(sorc_no);
					materialEntity.setSorc_no(sorc_no);

					String material_id ="";
					if (sorcMap.containsKey(sorc_no)) {
						material_id = sorcMap.get(sorc_no);
					} else {
						List<String> material_ids = materialMapper.searchMaterialIds(materialEntity);

						if(material_ids.size()>0){
							material_id=material_ids.get(0);
							materialEntity.setMaterial_id(material_id);
							materialPartialDetailEntity.setMaterial_id(material_id);
						}
						
						if (isEmpty(material_id)) {
							MsgInfo msg = new MsgInfo();
							msgInfo.add(msg);
							continue;
						}
					}

//					//型号
//					String model_name = getCellStringValue(row.getCell(3));
//					String model_id = ReverseResolution.getModelByName(model_name, conn);
//					materialEntity.setModel_id(model_id);
//					
//					//机身号
//					String serial_no = getCellStringValue(row.getCell(4));
//					materialEntity.setSerial_no(serial_no);
					
					//等级变更--------------loss_detail
					//String change_rank = getCellStringValue(row.getCell(8));
					
					//发现工程
					String strLine = getCellStringValue(row.getCell(9));
					String line = CodeListUtils.getKeyByValue("loss_belongs",strLine, "");
					if (line == null || line=="" ) {
						line = CodeListUtils.getKeyByValue("partial_append_belongs",strLine, "");
						if (line == null || line=="" ) {
							line ="4";
						}
					}
					sorcLossEntity.setLine_id(line);
					
					//责任区分
					String strLiability_flg = getCellStringValue(row.getCell(10));
					String liability_flg = CodeListUtils.getKeyByValue("liability_flg",strLiability_flg, "");
					if (isEmpty(liability_flg)) 
						liability_flg = "3"; // 非自责
					int liability_flg_val = Integer.parseInt(liability_flg);
					sorcLossEntity.setLiability_flg(liability_flg_val);
					
					//不良简述
					String nogood_description = getCellStringValue(row.getCell(11));
					sorcLossEntity.setNogood_description(nogood_description);
					log.info(nogood_description+"--------------------------------");
					
					//零件型号--------------material_partial_detail
					String code = getCellStringValue(row.getCell(12));
					String partial_id = ReverseResolution.getPartialByCode(code, conn);
					if (partial_id == null) {
						strBuffer.append("零件" + code + "不存在" + "\n");
						continue;
					}
					log.info("partial_id:"+partial_id+"====================================");
					
					//数量
					String quantity = getCellStringValue(row.getCell(13));
					int quantity_val = Integer.parseInt(quantity);
					
					//零件单价
					Double price=null;
				    if(row.getCell(14)!=null){
				    	price=row.getCell(14).getNumericCellValue();
				    }
				    
				    //有偿与否--------------loss_detail
				    String strService_free_flg = getCellStringValue(row.getCell(18));
				    String service_free_flg = CodeListUtils.getKeyByValue("service_free_flg",strService_free_flg,"");
				    if(service_free_flg !=""){
				    	int service_free_flg_val = Integer.parseInt(service_free_flg);
					    sorcLossEntity.setService_free_flg(service_free_flg_val);
				    }
				    
				    //备注
				    String comment = getCellStringValue(row.getCell(20));	
				    sorcLossEntity.setComment(comment);
				    
				    String belongs = CodeListUtils.getKeyByValue("partial_append_belongs",strLine,"");
				    if("6".equals(belongs)||"7".equals(belongs) || "8".equals(belongs)){
				    	materialPartialDetailEntity.setOccur_times(2);
				    }else{
				    	materialPartialDetailEntity.setOccur_times(1);
				    }
				    
				    materialPartialDetailEntity.setPartial_id(partial_id);
				    materialPartialDetailEntity.setQuantity(quantity_val);
				    materialPartialDetailEntity.setPrice(new BigDecimal(""+price)); // .intValue() ??
				    
				    if(belongs!=""){
				    	materialPartialDetailEntity.setBelongs(Integer.parseInt(belongs));
				    }else{
				    	materialPartialDetailEntity.setBelongs(0);	
				    }	
				    materialPartialDetailEntity.setPartial_id(partial_id);
				   
				    //判断material_partial_detail表是否已经存在
				    List<MaterialPartialDetailEntity> materialPartialDetailEntities  = materialPartialMapper.searchMaterialPartialDetail(materialPartialDetailEntity);

				    if(materialPartialDetailEntities.size() ==0 ){

				    	//如果material_partial_detail表如果没有数据，则重新插入新数据
				    	partialOrderMapper.insertMaterialPartialDetail(materialPartialDetailEntity);

				    	String mpdKey = cMapper.getLastInsertID();
				    	sorcLossEntity.setMaterial_partial_detail_key(mpdKey);

				    	//插入损金表数据
					    sorcLossMapper.insertSorcLoss(sorcLossEntity);

					    // 更新单元机种的出货日
					    if (!sorcMap.containsKey(sorc_no)) {
						    mCond.setMaterial_id(material_id);
						    mCond.setOcm_shipping_date(shipping_date);
						    mMapper.updateOcmShippingByID(mCond);
						    sorcMap.put(sorc_no, material_id);
							// FSE 数据同步
							try{
								FseBridgeUtil.toUpdateMaterialProcess(material_id, "loss");
							} catch (Exception e) {
								e.printStackTrace();
							}
					    }

					    index++;
					    isFlg = true;
				    }				    
				}
				if(isFlg==false){
					//文件已经被使用或者无有效数据
					MsgInfo info = new MsgInfo();
					info.setErrcode("info.partial.uselessFile");
					info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.uselessFile",null));
					msgInfo.add(info);
					continue;
				}	
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取备品文件数据
	 * @param fileName
	 * @param conn
	 * @param errors
	 * @param msgs
	 * @return
	 * @throws Exception 
	 */
	public List<MaterialForm> readSparesFile(String fileName, SqlSession conn, List<MsgInfo> errors, List<MsgInfo> msgs) throws Exception {
		InputStream in = null;
		List<MaterialForm> retList = new ArrayList<MaterialForm>();

		try {
			FileUtils.copyFile(new File(fileName), new File(fileName.replaceAll("\\" + PathConsts.LOAD_TEMP, "\\\\Imports")));

			in = new FileInputStream(fileName);// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = work.getSheetAt(0);// 获取Sheet

			HSSFRow row = sheet.getRow(0);
			if (row == null) {
				MsgInfo e = new MsgInfo();
				e.setComponentid("file");
				e.setErrcode("file.emptyFile");
				e.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.emptyFile"));
				errors.add(e);
				return null;
			}
			// 不是备品文件
			int coloumNum = row.getPhysicalNumberOfCells();
			if (coloumNum != 6 || (!"型号".equals(getCellStringValue(row.getCell(0)))
					|| !"机身号".equals(getCellStringValue(row.getCell(1))))) {
				MsgInfo e = new MsgInfo();
				e.setErrcode("notSparesFile");
				errors.add(e);
				return null;
			}

			// 在数据库里存在并且outline_time为空的维修对象
			MaterialMapper mDao = conn.getMapper(MaterialMapper.class);
			List<MaterialEntity> modelidList = mDao.searchMaterialByOutlineTime();

			String lineNo = "";
			for (int iRow = 1; iRow <= sheet.getLastRowNum(); iRow++) {
				lineNo = "第" + (iRow + 1) + "行";
				row = sheet.getRow(iRow);
				if (row != null) {
					String model_name = getCellStringValue(row.getCell(0));
					String serial_no = getCellStringValue(row.getCell(1));
					
					if (CommonStringUtil.isEmpty(model_name) && CommonStringUtil.isEmpty(serial_no)) {
						continue;
					} else if (CommonStringUtil.isEmpty(model_name) || CommonStringUtil.isEmpty(serial_no)) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("validator.required");
						if (CommonStringUtil.isEmpty(model_name)) { 
							info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", lineNo
									+ "型号"));
						} else {
							info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", lineNo
									+ "机身号"));
						}
						errors.add(info);
						continue;
					}

					String model_id = ReverseResolution.getModelByName(model_name, conn);
					if (model_id == null) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("model.notExist");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("model.notExist", model_name));
						errors.add(info);
						continue;
					}
					
					if (serial_no.length() > 20) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("validator.invalidParam.invalidMaxLengthValue");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMaxLengthValue", lineNo
								+ "机身号", "20"));
						errors.add(info);
						continue;
					}  

					// 判断RVS中是否已经存在
					boolean check = false;
					for (int j = 0; j < modelidList.size(); j++) {
						MaterialEntity checkEntity = modelidList.get(j);
						if (model_id.equals(checkEntity.getModel_id()) && serial_no.equals(checkEntity.getSerial_no())) {
							check = true;
							break;
						}
					}
					if (check) {
						continue;
					}
					
					MaterialForm lineform = new MaterialForm();
					lineform.setMaterial_id("Line" + iRow);
					lineform.setModel_id(model_id);
					lineform.setModel_name(model_name);
					lineform.setSerial_no(serial_no);
					lineform.setFix_type("3");
					
					retList.add(lineform);					
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return retList;
	}
}

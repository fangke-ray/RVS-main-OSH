package com.osh.rvs.service.equipment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.equipment.DeviceBackupEntity;
import com.osh.rvs.bean.master.DevicesManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.equipment.DeviceBackupForm;
import com.osh.rvs.mapper.equipment.DeviceBackupMapper;
import com.osh.rvs.mapper.master.DevicesManageMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.FileUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class DeviceBackupService {

	private static Logger _logger = Logger.getLogger("DeviceBackupService");

	public void getDetail(ActionForm form, SqlSession conn,
			Map<String, Object> listResponse, List<MsgInfo> errors) {
		DeviceBackupMapper mapper = conn.getMapper(DeviceBackupMapper.class);

		DeviceBackupEntity cond = new DeviceBackupEntity();

		// 复制表单数据到对象
		BeanUtil.copyToBean(form, cond, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<DeviceBackupEntity> relations = mapper.getRelation(cond.getManage_id());

		listResponse.put("relations", relations);

		DevicesManageMapper dmMapper = conn.getMapper(DevicesManageMapper.class);
		listResponse.put("rowData", dmMapper.getByKey(cond.getManage_id()));

		// 取得相同型号的一览
		DevicesManageEntity dmEntity = new DevicesManageEntity();
		dmEntity.setDevices_type_id(cond.getDevice_type_id());
		dmEntity.setStatus("1,4,5");

		List<DevicesManageEntity> devicesManageEntities = dmMapper.searchDeviceManage(dmEntity);

		List<DevicesManageEntity> lSameModel = new ArrayList<DevicesManageEntity>();
		List<DevicesManageEntity> lOtherModel = new ArrayList<DevicesManageEntity>();
		List<DevicesManageEntity> lSameModelOtherLine = new ArrayList<DevicesManageEntity>();
		List<DevicesManageEntity> lOtherModelOtherLine = new ArrayList<DevicesManageEntity>();
		for (DevicesManageEntity devicesManageEntity : devicesManageEntities) {
			if (devicesManageEntity.getDevices_manage_id().equals(cond.getManage_id())) {
				continue;
			}
			if (devicesManageEntity.getBorrowed() == 1) { // 正在借用中
				devicesManageEntity = dmMapper.getByKey(devicesManageEntity.getDevices_manage_id());
			}
			boolean isSameLine = false;
			if (cond.getLine_name() == null && devicesManageEntity.getLine_name() == null) {
				isSameLine = true;
			} else if (cond.getLine_name() != null) {
				isSameLine = cond.getLine_name().equals(devicesManageEntity.getLine_name());
			}
			String optModelName = CommonStringUtil.nullToAlter(devicesManageEntity.getModel_name(), "");
			if (optModelName.equals(cond.getModel_name())) {
				if (isSameLine) {
					lSameModel.add(devicesManageEntity);
				} else {
					lSameModelOtherLine.add(devicesManageEntity);
				}
			} else {
				if (isSameLine) {
					lOtherModel.add(devicesManageEntity);
				} else {
					lOtherModelOtherLine.add(devicesManageEntity);
				}
			}
		}
		lSameModel.addAll(lSameModelOtherLine);
		lOtherModel.addAll(lOtherModelOtherLine);

		listResponse.put("lSameModel", lSameModel);
		listResponse.put("lOtherModel", lOtherModel);

		/*
		 * 判断异品名替代品
		 */
		Set<String> manageIdsOfType = new HashSet<String>();
		Set<String> manageIdsToAdd = new HashSet<String>();
		List<DevicesManageEntity> lOtherType = new ArrayList<DevicesManageEntity>();

		for (DevicesManageEntity devicesManageEntity : devicesManageEntities) {
			manageIdsOfType.add(devicesManageEntity.getDevices_manage_id());
		}
		for (DeviceBackupEntity relation : relations) {
			// 被替换
			if (relation.getManage_id().equals(cond.getManage_id())) {
				if (!manageIdsOfType.contains(relation.getBackup_manage_id())) {
					manageIdsToAdd.add(relation.getBackup_manage_id());
				}
			} else {
				if (!manageIdsOfType.contains(relation.getManage_id())) {
					manageIdsToAdd.add(relation.getManage_id());
				}
			}
		}
		for (String manageIdToAdd : manageIdsToAdd) {
			DevicesManageEntity dEntity = dmMapper.getByKey(manageIdToAdd);
			dEntity.setModel_name(dEntity.getName() + "<br>" + dEntity.getModel_name());
			lOtherType.add(dEntity);
		}

		listResponse.put("lOtherType", lOtherType);

	}

	/**
	 * 提交更新
	 * 
	 * @param form
	 * @param req
	 * @param conn
	 * @param errors
	 */
	public void submit(ActionForm form, HttpServletRequest req,
			SqlSessionManager conn, List<MsgInfo> errors) {

		DeviceBackupEntity cond = new DeviceBackupEntity();

		// 复制表单数据到对象
		BeanUtil.copyToBean(form, cond, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<DeviceBackupForm> updates = new AutofillArrayList<DeviceBackupForm>(DeviceBackupForm.class);

		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		Map<String, String[]> parameters = req.getParameterMap();

		for (String parameterKey : parameters.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("updates".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameters.get(parameterKey);

					if ("manage_id".equals(column)) {
						updates.get(icounts).setManage_id(value[0]);
					} else if ("backup_manage_id".equals(column)) {
						updates.get(icounts).setBackup_manage_id(value[0]);
					} else if ("free_displace_flg".equals(column)) {
						updates.get(icounts).setFree_displace_flg(value[0]);
					} else if ("status".equals(column)) {
						updates.get(icounts).setStatus(value[0]);
					}
				}
			}
		}

		DeviceBackupMapper mapper = conn.getMapper(DeviceBackupMapper.class);

		for (DeviceBackupForm update : updates) {
			DeviceBackupEntity updateEntity = new DeviceBackupEntity();

			// 复制表单数据到对象
			BeanUtil.copyToBean(update, updateEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

			if (updateEntity.getManage_id() == null) {
				updateEntity.setManage_id(cond.getManage_id());
			} else {
				updateEntity.setBackup_manage_id(cond.getManage_id());
			}

			if ("1".equals(update.getStatus())) {

				// 新建替代关系
				mapper.insertRelation(updateEntity);

			} else if ("2".equals(update.getStatus())) {

				// 修改替代关系
				mapper.updateRelation(updateEntity);

			} else if ("3".equals(update.getStatus())) {

				// 删除替代关系
				mapper.deleteRelation(updateEntity);

			}
		}

		// 更新对应方案
		mapper.replaceCorresponding(cond);
	}

	public String makeReport(SqlSession conn) {
		// Excel临时文件
		String cacheName = "设备代替一览表 " + new Date().getTime() + ".xlsx";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" + cacheName;
		FileUtils.copyFile(PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\设备代替一览表模板.xlsx", cachePath);

		XSSFWorkbook book = null;

		DeviceBackupMapper mapper = conn.getMapper(DeviceBackupMapper.class);

		try {
			book = new XSSFWorkbook(new FileInputStream(cachePath));

			// 一览列表生成
			XSSFSheet listSheet = book.getSheetAt(0);

			// 标题冻结
			listSheet.createFreezePane(0, 10);

			// 检索
			List<DeviceBackupEntity> list = mapper.searchAll();
			Map<String, List<DeviceBackupEntity>> listByDeviceType = new HashMap<String, List<DeviceBackupEntity>>();

			XSSFFont font = book.createFont();
			font.setFontHeightInPoints((short) 10);

			XSSFCellStyle styleL = book.createCellStyle();
			styleL.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			styleL.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleL.setBorderTop(HSSFCellStyle.BORDER_THIN);
			styleL.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleL.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			styleL.setFont(font);

			XSSFCellStyle styleC = book.createCellStyle();
			styleC.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			styleC.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleC.setBorderTop(HSSFCellStyle.BORDER_THIN);
			styleC.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleC.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			styleC.setFont(font);

			XSSFCellStyle styleY = book.createCellStyle();
			styleY.cloneStyleFrom(styleL);
			styleY.setFillForegroundColor(IndexedColors.YELLOW.index);
			styleY.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			styleY.setFillBackgroundColor(IndexedColors.YELLOW.index);

			XSSFCellStyle styleB = book.createCellStyle();
			styleB.cloneStyleFrom(styleL);
			styleB.setFillForegroundColor(IndexedColors.LIGHT_BLUE.index);
			styleB.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			styleB.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.index);

			XSSFCellStyle styleG = book.createCellStyle();
			styleG.cloneStyleFrom(styleC);
			styleG.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
			styleG.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			styleG.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.index);

			XSSFRow row = null;
			XSSFCell cell = null;

			row = listSheet.getRow(9);
			XSSFCell item1 = row.getCell(5);
			XSSFCellStyle styleTitle = item1.getCellStyle();

			int iRow = 10;
			int iMaxItem = 0;
			for (DeviceBackupEntity deviceBackupEntity : list) {
				row = listSheet.createRow(iRow++);
				// 部门
				cell = row.createCell(0);
				String lineName = deviceBackupEntity.getLine_name();
				cell.setCellValue(lineName);
				cell.setCellStyle(styleL);

				// 管理编号
				cell = row.createCell(1);
				cell.setCellValue(deviceBackupEntity.getManage_code());
				cell.setCellStyle(styleL);

				// 评价
				cell = row.createCell(2);
				Integer evaluation = deviceBackupEntity.getEvaluation();
				cell.setCellValue(getEvaluationText(evaluation));
				cell.setCellStyle(styleC);

				// 对应
				String corresponding = deviceBackupEntity.getCorresponding();
				cell = row.createCell(3);
				if (corresponding != null) {
					cell.setCellValue(corresponding);
				}
				cell.setCellStyle(styleL);

				// 品名
				String deviceTypeName = deviceBackupEntity.getName();
				if (!listByDeviceType.containsKey(deviceTypeName)) {
					listByDeviceType.put(deviceTypeName, new ArrayList<DeviceBackupEntity>());
				}
				listByDeviceType.get(deviceTypeName).add(deviceBackupEntity);

				// 可替换品
				String alters = deviceBackupEntity.getBackup_in_manage();
				if (alters == null) {
					cell = row.createCell(4);
					cell.setCellValue(0);
					cell.setCellStyle(styleC);
				} else {
					String[] alterArray = alters.split(",");
					cell = row.createCell(4);
					int alterLength = alterArray.length;
					cell.setCellValue(alterLength);
					if (iMaxItem < alterLength) {
						iMaxItem = alterLength;
					}
					cell.setCellStyle(styleC);

					for (int i = 0; i < alterLength; i++) {
						XSSFCellStyle thisStyle = styleL;

						String[] alter = alterArray[i].split(":");

						if (lineName != null && lineName.equals(alter[0])) {
							thisStyle = styleY;
						} else if (alter[2].equals("4")) {
							thisStyle = styleB;
						}

						cell = row.createCell(5 + i * 2);
						cell.setCellValue(alter[0]);
						cell.setCellStyle(thisStyle);

						cell = row.createCell(6 + i * 2);
						String manageCode = alter[1];
						if (alter[3].equals("1")) {
							manageCode += " ☆";
						} else {
							manageCode += " △";
						}
						cell.setCellValue(manageCode);
						cell.setCellStyle(thisStyle);
					}
				}
			}

			if (iMaxItem > 1) {

				for (int i = 0; i < iMaxItem; i++) {

					if (i > 0) {
						row = listSheet.getRow(9);
						XSSFCell title1 = row.createCell(5 + i * 2);
						XSSFCell title2 = row.createCell(6 + i * 2);
						title1.setCellValue("替换品 " + (i + 1));
						title1.setCellStyle(styleTitle);
						title2.setCellStyle(styleTitle);
						CellRangeAddress region = new CellRangeAddress(9, 9, 5 + i * 2, 6 + i * 2);
						listSheet.addMergedRegion(region);
					}

					for (int l = 10; l < iRow; l++) {
						row = listSheet.getRow(l);

						cell = row.getCell(5 + i * 2);
						if (cell == null) {
							cell = row.createCell(5 + i * 2);
							cell.setCellStyle(styleL);
						}

						cell = row.getCell(6 + i * 2);
						if (cell == null) {
							cell = row.createCell(6 + i * 2);
							cell.setCellStyle(styleL);
						}
					}
				}
			}

			// 一览列表生成
			XSSFSheet pickingSheet = book.getSheetAt(1);

			iRow = 7;

			for (String deviceTypeName : listByDeviceType.keySet()) {

				List<DeviceBackupEntity> dtList = listByDeviceType.get(deviceTypeName);
				if (dtList.size() == 1)
					continue;

				row = pickingSheet.createRow(iRow);

				cell = row.createCell(0);
				cell.setCellValue(deviceTypeName);
				cell.setCellStyle(styleTitle);
				CellRangeAddress region = new CellRangeAddress(iRow, iRow, 0, 16);
				pickingSheet.addMergedRegion(region);

				iRow += 2;

				// 部门 编号 型號
				row = pickingSheet.createRow(iRow);
				cell = row.createCell(0);
				cell.setCellValue("部门");
				cell.setCellStyle(styleTitle);

				cell = row.createCell(1);
				cell.setCellValue("编号");
				cell.setCellStyle(styleTitle);

				cell = row.createCell(2);
				cell.setCellValue("型号");
				cell.setCellStyle(styleTitle);

				region = new CellRangeAddress(iRow, iRow, 2, 4);
				pickingSheet.addMergedRegion(region);

				int yPos = 5;

				Map<String, Integer> posMap = new HashMap<String, Integer>();
				for (int i = 0; i < dtList.size(); i++) {
					DeviceBackupEntity dt = dtList.get(i);
					cell = row.createCell(yPos);
					cell.setCellValue(dt.getManage_code());
					cell.setCellStyle(styleC);

					posMap.put(dt.getManage_code(), yPos);

					yPos++;
				}

				cell = row.createCell(yPos++);
				cell.setCellValue("评价");
				cell.setCellStyle(styleTitle);

				cell = row.createCell(yPos);
				cell.setCellValue("对应");
				cell.setCellStyle(styleTitle);

				region = new CellRangeAddress(iRow, iRow, yPos, yPos + 6);
				pickingSheet.addMergedRegion(region);

				int iTitleRow = iRow, iTitleCol = yPos + 7;
				int otherTypeCount = 0;

				for (int i = 0; i < dtList.size(); i++) {

					List<String> rowOtherTypeNames = new ArrayList<String>();

					DeviceBackupEntity dt = dtList.get(i);

					row = pickingSheet.createRow(++iRow);
					// 部门
					cell = row.createCell(0);
					cell.setCellValue(dt.getLine_name());
					cell.setCellStyle(styleL);

					// 编号
					cell = row.createCell(1);
					cell.setCellValue(dt.getManage_code());
					cell.setCellStyle(styleL);

					// 型號
					cell = row.createCell(2);
					cell.setCellValue(dt.getModel_name());
					cell.setCellStyle(styleL);
					cell = row.createCell(3);
					cell.setCellStyle(styleL);
					cell = row.createCell(4);
					cell.setCellStyle(styleL);

					region = new CellRangeAddress(iRow, iRow, 2, 4);
					pickingSheet.addMergedRegion(region);

					yPos = 5;

					for (int j = 0; j < dtList.size(); j++) {
						cell = row.createCell(yPos);
						cell.setCellValue("");
						if (i == j) {
							cell.setCellStyle(styleG);
						} else {
							cell.setCellStyle(styleC);
						}

						yPos++;
					}

					// 评价
					cell = row.createCell(yPos++);
					cell.setCellValue(getEvaluationText(dt.getEvaluation()));
					cell.setCellStyle(styleC);

					// 对应
					cell = row.createCell(yPos);
					cell.setCellValue(dt.getCorresponding());
					cell.setCellStyle(styleL);
					cell = row.createCell(yPos + 1);
					cell.setCellStyle(styleL);
					cell = row.createCell(yPos + 2);
					cell.setCellStyle(styleL);
					cell = row.createCell(yPos + 3);
					cell.setCellStyle(styleL);
					cell = row.createCell(yPos + 4);
					cell.setCellStyle(styleL);
					cell = row.createCell(yPos + 5);
					cell.setCellStyle(styleL);
					cell = row.createCell(yPos + 6);
					cell.setCellStyle(styleL);

					region = new CellRangeAddress(iRow, iRow, yPos, yPos + 6);
					pickingSheet.addMergedRegion(region);

					String alters = dt.getBackup_in_manage();
					if (alters != null) {
						String[] alterArray = alters.split(",");
						
						for (int j = 0; j < alterArray.length; j++) {
							String[] alter = alterArray[j].split(":");

							if (posMap.get(alter[1]) != null) {
								cell = row.getCell(posMap.get(alter[1]));
								if (alter[3].equals("1")) {
									cell.setCellValue("☆");
								} else {
									cell.setCellValue("△");
								}
							} else {
								rowOtherTypeNames.add(alter[1]);
							}
						}
					}
					if (rowOtherTypeNames.size() > 0) {
						cell = row.createCell(yPos + 7);
						cell.setCellValue(
								CommonStringUtil.joinBy("，", rowOtherTypeNames.toArray(new String[rowOtherTypeNames.size()])));

						if (otherTypeCount < rowOtherTypeNames.size()) {
							otherTypeCount = rowOtherTypeNames.size();
						}
					}
				}

				if (otherTypeCount > 0) {
					int oWidth = otherTypeCount * 14 / 10 + 1;
					if (oWidth < 6) {
						oWidth = 6;
					}
					row = pickingSheet.getRow(iTitleRow);
					cell = row.createCell(iTitleCol);
					cell.setCellValue("其他品类设备工具中的替代品");
					cell.setCellStyle(styleTitle);
					for (int iow = 1; iow < oWidth; iow ++) {
						cell = row.createCell(iTitleCol + iow);
						cell.setCellStyle(styleTitle);
					}
					region = new CellRangeAddress(iTitleRow, iTitleRow, iTitleCol, iTitleCol + oWidth);
					pickingSheet.addMergedRegion(region);

					for (int i = 0; i < dtList.size(); i++) {
						row = pickingSheet.getRow(iTitleRow + i + 1);
						cell = row.getCell(iTitleCol);
						if (cell == null) {
							cell = row.createCell(iTitleCol);
						}
						cell.setCellStyle(styleL);
						for (int iow = 1; iow <= oWidth; iow ++) {
							cell = row.createCell(iTitleCol + iow);
							cell.setCellStyle(styleL);
						}
						region = new CellRangeAddress(iTitleRow + i + 1, iTitleRow + i + 1, iTitleCol, iTitleCol + oWidth);
						pickingSheet.addMergedRegion(region);
					}
				}

				iRow += 2;

			}

			// 保存文件
			FileOutputStream fileOut = new FileOutputStream(cachePath);
			book.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			_logger.error(e.getMessage(), e);
		}

		return cacheName;
	}

	/**
	 * 
	 */
	private String getEvaluationText(Integer evaluation) {
		// ×=无替代品重点管理对象
		if (evaluation == null) {
			return "×";
		}
		// ◎=本工程有替代品 ○=他工程有替代品 △=临时共用
		if (evaluation >= 4) {
			return "◎";
		} else if (evaluation >= 2) {
			return "○";
		} else if (evaluation >= 1) {
			return "△";
		} else {
			return "×";
		}
	}
}

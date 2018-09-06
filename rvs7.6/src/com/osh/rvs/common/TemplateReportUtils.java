package com.osh.rvs.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.osh.rvs.bean.data.OperatorProductionEntity;
import com.osh.rvs.form.data.OperatorProductionForm;

import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;

public class TemplateReportUtils {
	private static Dispatch workbook;
	private static Dispatch sheet;
	
	public static String createWorkReport(OperatorProductionForm detail, List<OperatorProductionForm> list, OperatorProductionForm overtime) throws Exception {
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "workReportTmp.xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + "workReportTmp" + new Date().getTime() + ".xls";
		FileUtils.copyFile(new File(path), new File(cachePath));

		String outputFile = "";
		ActiveXComponent xl = new ActiveXComponent("Excel.Application");
		xl.setProperty("Visible", new Variant(false));
		xl.setProperty("DisplayAlerts", new Variant(false));
		try {
			ComThread.InitSTA();
			Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
			
			workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method,
					new Object[] { cachePath.replaceAll("//", "\\\\"),
					new Variant(false), new Variant(false) }, new int[1]).toDispatch();
			sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
			
			
			insertDetail(detail);

			insertMain(list, detail.getProcess_code());
			
			if (overtime != null && overtime.getAction_time() != null) {
				insertOvertime(overtime);
			}
			
			outputFile = cachePath;
			Dispatch.call(workbook, "Save");
			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
			
		}  catch (Exception e) {
			throw e;
		} finally {
			xl.invoke("Quit", new Variant[] {});
			ComThread.Release();
		}
		return outputFile;
	}
	
	private static void insertMain(List<OperatorProductionForm> list, String mainProcessCode) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		int index = 5;
		for (int i = 0; i < list.size(); i++) {
			OperatorProductionForm f = list.get(i);
			OperatorProductionEntity entity = new OperatorProductionEntity();
			BeanUtil.copyToBean(f, entity, null);
			
			setValue("C"+index, format.format(entity.getPause_start_time()), false);
			setValue("D"+index, format.format(entity.getPause_finish_time()), false);
			setValue("E"+index, f.getSorc_no(), false);
			setValue("F"+index, f.getModel_name(), false);
			setValue("G"+index, f.getProcess_code(), false);
			setValue("H"+index, getSpecialComment(f, mainProcessCode), false);
			if (index < 49) {
				index++;
			} else {
				break;
			}
		}
	}

	private static String getSpecialComment(OperatorProductionForm f, String mainProcessCode) {
		String code = f.getProcess_code();
		String result = f.getOperate_result();
		String pace = f.getPace();
		if (!CommonStringUtil.isEmpty(code) && !code.equals(mainProcessCode)) {
			if ("5".equals(result)) {
				return "辅助工作";
			} else if ("2".equals(result)) {
				if ("0".equals(pace)) {
					return "全代工";
				} else if (Integer.valueOf(pace) > 0) {
					return "半代工";
				}
			}
		}
		String reasonText = f.getReasonText();
		String comments = f.getComments();
		StringBuffer sb = new StringBuffer();
		if (!CommonStringUtil.isEmpty(reasonText)) {
			sb.append(reasonText).append(" ");
		}
		if (!CommonStringUtil.isEmpty(comments)) {
			sb.append(comments);
		}
		
		return sb.toString();
	}

	private static void insertOvertime(OperatorProductionForm overtime) {
		OperatorProductionEntity entity = new OperatorProductionEntity();
		BeanUtil.copyToBean(overtime, entity, null);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		
		setValue("B53", format.format(entity.getPause_start_time()), false);
		setValue("C53", format.format(entity.getPause_finish_time()), false);
		setValue("D53", overtime.getOverwork_reason_name(), false);
		setValue("E53", overtime.getComments(), false);
		
	}

	private static void insertDetail(OperatorProductionForm detail) {
		setValue("B2", detail.getAction_time(), true);
		setValue("D2", detail.getLine_name(), true);
		setValue("F2", detail.getProcess_code(), true);
		setValue("G2", detail.getName(), true);
	}
	
	private static void setValue(String position, String value, boolean append) {
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get,
				new Object[] { position }, new int[1]).toDispatch();
		if (append) {
			Dispatch.put(cell, "Value", Dispatch.get(cell, "Value").toString() + value);
		} else {
			Dispatch.put(cell, "Value", value);
		}
	}
}

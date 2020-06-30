package com.osh.rvs.common;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.osh.rvs.form.inline.ScheduleForm;

import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.DateUtil;

public class ReportUtils {
	private static final String[] positions = { "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z" };
	private static Dispatch workbook;
	private static Dispatch sheet;

	private static Logger logger = Logger.getLogger("Download");

	public static String createReport(List<?> forms, String[] titles, String[] colNames) {
		Date today = new Date();
		String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");

		File fMonthPath = new File(folder);
		if (!fMonthPath.exists()) {
			fMonthPath.mkdirs();
		}
		fMonthPath = null;

		String outputFileName = new Date().getTime() + ".xls";
		String outputFile = folder + "\\" + outputFileName;

		ActiveXComponent xl = new ActiveXComponent("Excel.Application");
		xl.setProperty("Visible", new Variant(false));
//		xl.setProperty("DisplayAlerts", new Variant(false));
		Dispatch xlo = xl.getObject();
		try {
			logger.info("用Jacob生成导出文件");

			ComThread.InitSTA();
			logger.info("version=" + xl.getProperty("Version"));
			logger.info("version=" + Dispatch.get(xlo, "Version"));

			Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();// 获得EXCEL对象
			workbook = Dispatch.get(workbooks, "Add").toDispatch(); // 新添加的EXCEL对象
			sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch(); // 获得SHEET对象

			logger.info("设内容");
			int row = 1;

			for (int i = 0; i < titles.length; i++) {
				SetValue(getPosition(i, row), "Value", titles[i]);
			}

			for (int i = 0; i < forms.size(); i++) {
				Object form = forms.get(i);
				row += 1;
				for (int j = 0; j < colNames.length; j++) {
					Method getMethod = form.getClass().getMethod("get" + CommonStringUtil.capitalize(colNames[j]));
					String value = (String) getMethod.invoke(form);
					try {
						Method filterSpecialValue = form.getClass().getMethod("filterSpecialValue", String.class, String.class);
						value = (String) filterSpecialValue.invoke(form, value, colNames[j]);
					} catch (Exception e) {
						System.out.println("filterSpecialValue method is not define");
					}
					SetValue(getPosition(j, row), "Value", value);
				}
			}
			logger.info("保存");
			Dispatch.invoke(workbook, "SaveAs", Dispatch.Method, new Object[] {
					outputFile, new Variant(46) }, new int[1]);
			// save(outputFile);
			logger.info("保存完了");
			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} finally {
			xl.invoke("Quit", new Variant[] {});
			ComThread.Release();
		}

		return outputFileName;
	}

	private static void SetValue(String position, String type, String value) {
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get,
				new Object[] { position }, new int[1]).toDispatch();
		Dispatch.put(cell, type, value);
		Dispatch border = Dispatch.call(cell, "Borders").toDispatch();
		Dispatch.put(border, "LineStyle", new Variant(1));
		Dispatch.put(cell, "ColumnWidth", new Variant(11));
	}

	public static void main(String[] args) {
		List<ScheduleForm> list = new ArrayList<ScheduleForm>();

		ScheduleForm form = new ScheduleForm();
		// form.setBo_contents("{'dec':'11','ns':'22','com':'33'},{'dec':'111','ns':'222','com':'333'}");
		form.setBo_contents("123");
		form.setAgreed_date("2012/12/12");
		list.add(form);
		createReport(list, ReportMetaData.materialTitles, ReportMetaData.materialColNames);
	}

	/**
	 * 取A-ZZ之间
	 * 
	 * @param i
	 *            起始值0
	 * @return
	 */
	public static String getPosition(int i, int row) {
		int multiple = i / 26;
		int index = (i % 26);

		if (multiple == 0) {// 26之内
			return positions[index] + row;
		} else if (multiple > 0 && multiple < 27) { // 最大26倍
			return positions[(multiple - 1)] + positions[index] + row;
		} else {
			return "";
		}
	}
}

package com.osh.rvs.common;


import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * 以Jocab编辑Excel的处理对象
 * @author Gong
 *
 */
public class XlsUtil {
	private ActiveXComponent xl;
	private Dispatch workbooks = null;
	private Dispatch workbook = null;
	private Dispatch sheets = null;
	private Dispatch sheet = null;
	private String filename = null;
	private boolean readonly = false;

    public static final int WORD_HTML = 8;  
    public static final int WORD_TXT = 7;  
    public static final int EXCEL_HTML = 44;  
    public static final int EXCEL_XML = 46;  
    public static final int EXCEL_EXCEL_8 = 56;  // Excel 2003

	private static Logger logger = Logger.getLogger("XlsUtils");

	public XlsUtil(String path) {
		OpenExcel(path, false);
	}

	public XlsUtil(String path,boolean visible) {
		OpenExcel(path, visible);
	}

	// 放置图片到格子
	public void sign(String sImgFile, String cellname) {
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { cellname }, new int[1])
				.toDispatch();

		Dispatch select = Dispatch.call(sheet, "Pictures").toDispatch();
		try {
			Dispatch pic = Dispatch.call(select, "Insert", sImgFile.replaceAll("/", "\\\\")).toDispatch();
			logger.info(Dispatch.get(cell, "Height").changeType(Variant.VariantInt).getInt());
			int leftPosition = getWidthPosition(cell , 56);
			Dispatch.put(pic, "Left", Dispatch.get(cell, "Left").changeType(Variant.VariantInt).getInt() + leftPosition); // 图片在Excel中显示的位置

			Dispatch.put(pic, "Top", Dispatch.get(cell, "Top").changeType(Variant.VariantInt).getInt()); // 图片在Excel中显示的高
			// Dispatch.put(pic, "Width", Dispatch.get(cell, "Width").changeType(Variant.VariantInt).getInt() - 2); // 图片在Excel中显示的宽
		} catch (com.jacob.com.ComFailException e) {
			logger.error(sImgFile + "为异常的图片" + e.getMessage());
		}
	}

	// 放置图片到格子
	public void sign(String sImgFile, Dispatch cell) {
		Dispatch select = Dispatch.call(sheet, "Pictures").toDispatch();
		try {
			Dispatch pic = Dispatch.call(select, "Insert", sImgFile.replaceAll("/", "\\\\")).toDispatch();
			int jleft = 1;
			int cellWidth = Dispatch.get(cell, "Width").changeType(Variant.VariantInt).getInt();
			int picWidth = Dispatch.get(pic, "Width").changeType(Variant.VariantInt).getInt();

			if (cellWidth > picWidth) {
				jleft = (cellWidth - picWidth) / 2;
			}
			if (jleft > 50) {
				jleft = 20;
			}
			Dispatch.put(pic, "Top", Dispatch.get(cell, "Top").changeType(Variant.VariantInt).getInt()); // 图片在Excel中显示的高
			Dispatch.put(pic, "Left", Dispatch.get(cell, "Left").changeType(Variant.VariantInt).getInt() + jleft); // 图片在Excel中显示的位置
			// Dispatch.put(pic, "Width", Dispatch.get(cell, "Width").changeType(Variant.VariantInt).getInt() - 2); // 图片在Excel中显示的宽
		} catch (com.jacob.com.ComFailException e) {
			logger.error(sImgFile + "为异常的图片" + e.getMessage());
		}
	}

	// 打开Excel文档
	public void OpenExcel(String file, boolean f) {
		try {
			ComThread.InitSTA();

			filename = file;
			xl = new ActiveXComponent("Excel.Application");
			xl.setProperty("Visible", new Variant(f));
			xl.setProperty("DisplayAlerts", new Variant(false));
			workbooks = xl.getProperty("Workbooks").toDispatch();
			workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method,
					new Object[] { filename, new Variant(false), new Variant(readonly) },// 是否以只读方式打开
					new int[1]).toDispatch();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	// 保存并关闭Excel文档
	public void SaveCloseExcel(boolean f) {
		SaveCloseExcel(f, true);
	}
	public void SaveCloseExcel(boolean f, boolean release) {
		try {
			Dispatch.call(workbook, "Save");
			Dispatch.call(workbook, "Close", new Variant(f));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			xl.invoke("Quit", new Variant[] {});
			if (release) ComThread.Release();
		}
	}

	// 关闭Excel文档
	public void CloseExcel(boolean f) {
		CloseExcel(f, true);
	}
	public void CloseExcel(boolean f, boolean release) {
		try {
			Dispatch.call(workbook, "Close", new Variant(f));
			xl.invoke("Quit", new Variant[] {});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (release) ComThread.Release();
		}
	}

	// 关闭Excel文档
	public void Release() {
		ComThread.Release();
	}

	public void SelectActiveSheet() {
		sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
	}

	// 写入值
	public void SetValue(String position, String type, String value) {
		sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { position }, new int[1])
				.toDispatch();
		Dispatch.put(cell, type, value);
	}

	// 写入值
	public void SetValue(String position, String value) {
		sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { position }, new int[1])
				.toDispatch();
		Dispatch.put(cell, "Value", value);
	}
	public void SetValue(Dispatch cell, String value) {
		Dispatch.put(cell, "Value", value);
	}

	// 取得单元格字体
	@Deprecated
	public Dispatch GetCellFont(String position) {
		sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { position }, new int[1])
				.toDispatch();
		return GetCellFont(cell);
	}
	public Dispatch GetCellFont(Dispatch cell) {
		sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
		// Dispatch selection = Dispatch.call(cell, "Select").toDispatch();
		Dispatch font = Dispatch.get(cell, "Font").toDispatch();
		return font;
	}

	// 读取值
	public String GetValue(String position) {
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { position }, new int[1])
				.toDispatch();
		String value = Dispatch.get(cell, "Value").toString();

		return value;
	}

	// 取得位置地址
	public String GetAddress(Dispatch range) {
		String address = Dispatch.get(range, "Address").toString();
		return address;
	}

	// 另存为Pdf格式
	public void SaveAsPdf(String target) {
		try {
			logger.info("转换文档到PDF " + target);
			Dispatch.invoke(workbook, "SaveAs", Dispatch.Method, new Object[] { target, new Variant(57),
					new Variant(false), new Variant(57), new Variant(57), new Variant(false), new Variant(true),
					new Variant(57), new Variant(true), new Variant(true), new Variant(true) }, new int[1]);
			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
			xl.invoke("Quit", new Variant[] {});
		} catch (Exception e) {
			logger.error("========Error:文档转换失败：" + e.getMessage());
			//SaveCloseExcel(false); // TEST TODO
			throw e;
		} finally {
			Release();
		}
	}

	// 另存为Xml格式
	public void SaveAsXml(String target) {
		try {
			logger.info("转换文档到XML " + target);
			Dispatch.invoke(workbook, "SaveAs", Dispatch.Method, new Object[] {
					target, new Variant(EXCEL_XML) }, new int[1]);
			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
		} catch (Exception e) {
			logger.error("========Error:文档转换失败：" + e.getMessage());
			throw e;
		} finally {
			Release();
		}
	}

	// 另存为Xls2003格式
	public void SaveAsXls2003(String target) {
		try {
			logger.info("转换文档到Xls2003 " + target);
			Dispatch.invoke(workbook, "SaveAs", Dispatch.Method, new Object[] {
					target, new Variant(EXCEL_EXCEL_8) }, new int[1]);
			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
		} catch (Exception e) {
			logger.error("========Error:文档转换失败：" + e.getMessage());
			throw e;
		} finally {
			Release();
		}
	}

	// 另存按原有格式
	public void SaveAs(String target) {
		try {
			logger.info("另存文档到 " + target);
			Dispatch.invoke(workbook, "SaveAs", Dispatch.Method, new Object[] {
					target }, new int[1]);
			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
		} catch (Exception e) {
			logger.error("========Error:文档转换失败：" + e.getMessage());
			throw e;
		} finally {
		}
	}

	private static final String REPLACE_RELAY_SIGNAL = "#xP_$";
	// 替换文字
	public void Replace(String source, String target) {

		if (target == null) return;
		int targetLength = target.length();

		Dispatch usedRange = Dispatch.get(sheet, "UsedRange").toDispatch();

	    Variant xlLookAt = new Variant(new Integer(2));  
		  
	    Variant xlSearchOrder = new Variant(new Integer(1));  
	  
	    Variant matchCase = new Variant(true);  

		if (targetLength <= 255) {
		    Dispatch.invoke(usedRange, "Replace", Dispatch.Method, new Object[] { source,
					target, xlLookAt, xlSearchOrder, matchCase }, new int[1]);
		} else {
			String cutHead = target.substring(0, 250);
			String cutTail = target.substring(250);
		    Dispatch.invoke(usedRange, "Replace", Dispatch.Method, new Object[] { source,
		    		cutHead + REPLACE_RELAY_SIGNAL, xlLookAt, xlSearchOrder, matchCase }, new int[1]);
		    Replace(REPLACE_RELAY_SIGNAL, cutTail);
		}
	}

	// 替换文字
	public void ReplaceInCell(Dispatch cell, String source, String target) {

	    Variant xlLookAt = new Variant(new Integer(2));  
		  
	    Variant xlSearchOrder = new Variant(new Integer(1));  
	  
	    Variant matchCase = new Variant(true);  

	    Dispatch.invoke(cell, "Replace", Dispatch.Method, new Object[] { source,
				target, xlLookAt, xlSearchOrder, matchCase }, new int[1]);

	}

	// 寻找是否存在
	public boolean Hit(String searchString) {
		Dispatch usedRange = Dispatch.get(sheet, "UsedRange").toDispatch();

		Variant result = Dispatch.invoke(usedRange, "Find", Dispatch.Method, new Object[] { searchString }, new int[1]);
		return !result.isNull();
	}
	
	// 得到命中的文本
	public String FindText(String searchString) {
		Dispatch usedRange = Dispatch.get(sheet, "UsedRange").toDispatch();

		Variant result = Dispatch.invoke(usedRange, "Find", Dispatch.Method, new Object[] { searchString }, new int[1]);
		if (!result.isNull()) {
			Dispatch find = result.toDispatch();
			return Dispatch.get(find, "Text").toString();
		} else {
			return null;
		}
	}

	// 得到命中的单元格
	public Dispatch Locate(String searchString) {
		Dispatch usedRange = Dispatch.get(sheet, "UsedRange").toDispatch();

		Variant result = Dispatch.invoke(usedRange, "Find", Dispatch.Method, new Object[] { searchString }, new int[1]);
		if (!result.isNull()) {
			return result.toDispatch();
		} else {
			return null;
		}
	}

	// 查找下一个命中单元格
	public Dispatch LocateNext(Dispatch prevLoc) {
		Dispatch usedRange = Dispatch.get(sheet, "UsedRange").toDispatch();

		Variant result = Dispatch.invoke(usedRange, "FindNext", Dispatch.Method, new Object[] { prevLoc }, new int[1]);
		if (!result.isNull()) {
			return result.toDispatch();
		} else {
			return null;
		}
	}

	private int getWidthPosition(Dispatch cell, int picWidth) {
		int cellWidth = Dispatch.get(cell, "Width").changeType(Variant.VariantInt).getInt();
		if (cellWidth < picWidth + 2) {
			return 0;
		} else {
			return (cellWidth - picWidth) / 2 - 1;
		}
	}
	public void SetNumberFormatLocal(Dispatch cell, String style) {
		Dispatch.put(cell, "NumberFormatLocal", style);
	}

	// 取得单元格背景色
	public String GetCellBackGroundColor(String position) {
		sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { position }, new int[1])
				.toDispatch();
		Dispatch interior = Dispatch.get(cell, "Interior").toDispatch();

		return Integer.toHexString( Dispatch.get(interior, "Color").changeType(Variant.VariantInt).getInt() );
	}

	// 设定单元格背景色
	public void SetCellBackGroundColor(String position, String colorCode) {
		sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { position }, new int[1])
				.toDispatch();
		SetCellBackGroundColor(cell, colorCode);
	}
	public void SetCellBackGroundColor(Dispatch cell, String colorCode) {
		if (cell == null) return;
		Dispatch interior = Dispatch.get(cell, "Interior").toDispatch();

		Dispatch.put(interior, "Color", colorCode);
	}

	/**
	 * Excel列编号转序号
	 * @param code Excel列编号(A,...,Z,AA,AB,...)
	 */
	public static Integer getExcelRowSeq(String code) {
		if (code == null || code.length() == 0) {
			return null;
		}
		int retr = 0;
		int clength = code.length()-1;
		for (int i=0;i<=clength;i++) {
			retr *= 26;
			retr += (code.charAt(i) - 'A' + (i == clength? 0: 1));
		}
		return retr;
	}
	public static String getExcelRowNo(Dispatch cell) {
		String drow = Dispatch.get(cell, "Row").toString();
		return drow;
	}
	public static String getExcelColNo(Dispatch cell) {
		String dcol = Dispatch.get(cell, "Column").toString();
		return dcol;
	}
	/**
	 * 序号转Excel列编号
	 * @param seq 序号(0开始)
	 */
	public static String getExcelColCode(String seq) {
		try {
			Integer iseq = Integer.parseInt(seq) - 1;
			return getExcelColCode(iseq);
		} catch (Exception e) {
			return null;
		}
	}
	public static String getExcelColCode(Integer seq) {
		String retr = "";
		int step = 0;
		while (seq >= 26) {
			retr = (char)('A' + seq % 26 - (step == 0? 0: 1)) + retr;
			seq /= 26;
			step++;
		}
		retr = (char)('A' + seq - (step == 0? 0: 1)) + retr;
		return retr;
	}

	public String getCellLocation(Dispatch cell) {
		String drow = Dispatch.get(cell, "Row").toString();
		String dcol = Dispatch.get(cell, "Column").toString();
		return getExcelColCode(dcol) + drow;
	}

	public Dispatch getRange(String cellName) {
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { cellName }, new int[1])
				.toDispatch();
		return cell;
	}

	public Dispatch getMergedRange(String cellName) {
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { cellName }, new int[1])
				.toDispatch();
		Dispatch mergedRange = Dispatch.get(cell, "MergeArea").toDispatch();
		return mergedRange;
	}

	public Dispatch getPageSetup(Dispatch sheet) {
		Dispatch page = Dispatch.get(sheet, "PageSetup").toDispatch();
		return page;
		// Dispatch.put(page, "Zoom", false);
	}

	public void getAndActiveSheetBySeq(int seq) {
		if (sheets == null) {
			sheets = Dispatch.get(workbook, "sheets").toDispatch();
		}
		sheet = Dispatch.invoke(sheets, "Item", Dispatch.Get, new Object[]{seq}, new int[1]).toDispatch();
		Dispatch.call(sheet, "Activate");
	}

	public Dispatch Select(String position) {
		sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
		Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[] { position }, new int[1])
				.toDispatch();
		Dispatch.call(cell, "Select");
		return xl.getProperty("Selection").toDispatch();
	}
	
	public void Print(String path) {
		 ComThread.InitSTA();
		 
		 ActiveXComponent xl = new ActiveXComponent("Excel.Application");
		 
		// 不打开文档
         Dispatch.put(xl, "Visible", new Variant(false));
         Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
         // 打开文档
         Dispatch excel = Dispatch.call(workbooks, "Open", path).toDispatch();
         // 开始打印
         Dispatch.call(excel, "PrintOut");
         xl.invoke("Quit", new Variant[] {});
         
         ComThread.Release();
	}

	public void setCheckCompatibility(){
		Dispatch.put(workbook,"CheckCompatibility",false);
	}
}

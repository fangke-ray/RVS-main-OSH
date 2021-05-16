package com.osh.rvs.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.common.CopyByPoi;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.XlsUtil;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.FileUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.Converter;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateConverter;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;


public class MaterialPartialService {

	private static Logger _log = Logger.getLogger("MaterialPartialService");

	/**
	 * 取得维修对象零件订购信息
	 * @param conn
	 * @param id
	 * @param times
	 * @return
	 */
	public MaterialPartialForm loadMaterialPartial(SqlSession conn, String id, Integer times) {

		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		conditionBean.setMaterial_id(id);
		conditionBean.setOccur_times(times);
		
		MaterialPartialForm form = null;

		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		MaterialPartialEntity entity = null;
		if (times == null) {
			entity = dao.loadMaterialPartialGroup(conditionBean);
		} else {
			entity = dao.loadMaterialPartial(conditionBean);
		}

		if (entity != null && entity.getMaterial_id() != null) {
			form = new MaterialPartialForm();
			BeanUtil.copyToForm(entity, form, null);
		}

		return form;
	}
	
	@Deprecated
	public void updateMaterialPartial(ActionForm form, int occur_times, SqlSession conn) throws Exception {

		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		conditionBean.setOccur_times(occur_times);

		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		dao.updateMaterialPartial(conditionBean);

	}

	/**
	 * 建立维修对象零件订购信息
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void insertMaterialPartial(ActionForm form, SqlSession conn) throws Exception {
		MaterialPartialEntity insertBean = new MaterialPartialEntity();
		BeanUtil.copyToBean(form, insertBean, null);
		
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		dao.insertMaterialPartial(insertBean);
	}
	
	public List<MaterialPartialForm> searchMaterial(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		CopyOptions cos = new CopyOptions();
		cos.excludeEmptyString(); cos.excludeNull();

		BeanUtil.copyToBean(form, conditionBean, cos);

//		// 现品人员显示
//		conditionBean.setType_of_bo_item(fact);
		// 从数据库中查询记录
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialEntity> lResultBean = dao.searchMaterial(conditionBean);

		// 建立页面返回表单
		List<MaterialPartialForm> lResultForm = new ArrayList<MaterialPartialForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, MaterialPartialForm.class);

		return lResultForm;
	}
	public List<MaterialPartialForm> searchMaterialReport(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		// 从数据库中查询记录
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialEntity> lResultBean = dao.searchMaterialReport(conditionBean);
		
		// 建立页面返回表单
		List<MaterialPartialForm> lResultForm = new ArrayList<MaterialPartialForm>();
		
		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, MaterialPartialForm.class);
		
		return lResultForm;
	}
	
	//外科镜
	public List<MaterialPartialForm> searchMaterialBoReport(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		if(conditionBean.getOrder_date_start()==null && conditionBean.getOrder_date_end()==null){//零件订购日
			Date date[] = getWeekFromAndEnd();
			conditionBean.setOrder_date_start(date[0]);
			conditionBean.setOrder_date_end(date[1]);
		}
		
		// 从数据库中查询记录
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialEntity> lResultBean = dao.searchMaterialBoReport(conditionBean);
		
		// 建立页面返回表单
		List<MaterialPartialForm> lResultForm = new ArrayList<MaterialPartialForm>();
		
		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, MaterialPartialForm.class);

		return lResultForm;
	}
	
	/**
	 * 零件追加明细
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<MaterialPartialForm> searchPartialAddtionalInf(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		// 从数据库中查询记录
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialEntity> lResultBean = dao.searchPartialAddtionalInf(conditionBean);
		
		// 建立页面返回表单
		List<MaterialPartialForm> lResultForm = new ArrayList<MaterialPartialForm>();
		
		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, MaterialPartialForm.class);
		
		return lResultForm;
	}


	public List<MaterialPartialForm> searchMaterialItemReport(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		// 从数据库中查询记录
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialEntity> lResultBean = dao.searchMaterialItemReport(conditionBean);
		
		// 建立页面返回表单
		List<MaterialPartialForm> lResultForm = new ArrayList<MaterialPartialForm>();
		
		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, MaterialPartialForm.class);
		
		return lResultForm;
	}
	
	public List<String> getOccurTimes(SqlSession conn, String id) {
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		return dao.getOccurTimesById(id);
	}

	/**
	 * 更新入库预定日
	 * @param sSorc_no
	 * @param arrival_plan_date
	 * @param conn
	 * @throws Exception 
	 */
	public void updateReachDateBySorc(String sSorc_no, Date arrival_plan_date, SqlSession conn) throws Exception {
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sorc_no", sSorc_no);
		paramMap.put("arrival_plan_date", arrival_plan_date);
		dao.updateReachDateBySorc(paramMap);
		
	}

	/**
	 * 计算时段内BO率
	 * @param from
	 * @param to
	 * @param conn
	 * @return
	 */
	public String[] getBoRate(Date from, Date to, SqlSession conn) {
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);

		Double todayBoRate = null;
		Double threedaysBoRate = null;

		// 如果条件有订购开始时期则按输入的查询
		if (from != null) {
			todayBoRate = dao.getTodayBoRate(from, to);
			threedaysBoRate = dao.get3daysBoRate(from, to);
		} else {
			
			// Integer totalBo = dao.getTotalBo();
			Date date[] = getWeekFromAndEnd();

			todayBoRate = dao.getTodayBoRate(date[0], date[1]);
			threedaysBoRate = dao.get3daysBoRate(date[0], date[1]);
		}

		String[] rate = new String[2];
		BigDecimal perty = new BigDecimal(100);
		rate[0] = (todayBoRate == null) ? "-" : new BigDecimal(todayBoRate).multiply(perty).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
		rate[1] = (threedaysBoRate == null) ? "-" : new BigDecimal(threedaysBoRate).multiply(perty).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
		
		return rate;
	}
	
	private Date[] getWeekFromAndEnd(){
		// Integer totalBo = dao.getTotalBo();
		Calendar thusday = Calendar.getInstance();
		Date endDate = null;
		int theday = thusday.get(Calendar.DAY_OF_WEEK);
		if (theday == Calendar.FRIDAY) { // 周五看上周信息
			Calendar now = Calendar.getInstance();
			now.setTimeInMillis(thusday.getTimeInMillis());
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND, 0);
			endDate = now.getTime();
		}
		int diff = theday % 7 + 1;
		thusday.set(Calendar.HOUR_OF_DAY, 0);
		thusday.set(Calendar.MINUTE, 0);
		thusday.set(Calendar.SECOND, 0);
		thusday.set(Calendar.MILLISECOND, 0);
		thusday.add(Calendar.DATE, -diff);
		
		Date date[] = {thusday.getTime(),endDate};
		
		return date;
		
	}
	
	public List<MaterialPartialDetailForm> searchMaterialPartialDetail(SqlSession conn, String materialId, String occurTimes, String lineId) {
		MaterialPartialDetailEntity conditionBean = new MaterialPartialDetailEntity();
		conditionBean.setMaterial_id(materialId);
		conditionBean.setLine_id(lineId);
		if (!CommonStringUtil.isEmpty(occurTimes)) 
			conditionBean.setOccur_times(Integer.parseInt(occurTimes));

		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialDetailEntity> lResultBean = dao.searchMaterialPartialDetail(conditionBean);
		
		List<MaterialPartialDetailForm> lResultForm = new ArrayList<MaterialPartialDetailForm>();
		
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, MaterialPartialDetailForm.class);

		return lResultForm;
	}

	/** 取得消耗品一览 
	 * @param errors */
	public List<MaterialPartialDetailForm> getConsumables(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		List<MaterialPartialDetailForm> retList = new ArrayList<MaterialPartialDetailForm>();
		// 表单复制到数据对象
		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		MaterialPartialMapper mapper = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialDetailEntity> result = mapper.getMpdForConsumable(conditionBean);

		if (result.size() == 0) {
			MsgInfo e = new MsgInfo();
			e.setComponentid("partial_id");
			e.setErrcode("info.partial.nothingToInstead");
			e.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.nothingToInstead"));
			errors.add(e);
			return null;
		} else {
			BeanUtil.copyToFormList(result, retList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialPartialDetailForm.class);
			return retList;
		}
	}

	/** 取得预制品一览 */
	public List<MaterialPartialDetailForm> getSnouts(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		List<MaterialPartialDetailForm> retList = new ArrayList<MaterialPartialDetailForm>();

		// 表单复制到数据对象
		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

//		// 维修对象是否使用先端组件
//		SoloProductionFeatureMapper spfMapper = conn.getMapper(SoloProductionFeatureMapper.class);
//		String retSnoutted = spfMapper.findUsedSnoutsByMaterial(conditionBean.getMaterial_id());
//		if (retSnoutted == null) {
//			MsgInfo e = new MsgInfo();
//			e.setComponentid("material_id");
//			e.setErrcode("info.partial.needUseSnoutFirst");
//			e.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.needUseSnoutFirst"));
//			errors.add(e);
//			return null;
//		}
//
		MaterialPartialMapper mapper = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialDetailEntity> result = mapper.getMpdForSnout(conditionBean);

		if (result.size() == 0) {
			// 维修对象是否有可替代零件
			MsgInfo e = new MsgInfo();
			e.setComponentid("partial_id");
			e.setErrcode("info.partial.nothingToInstead");
			e.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.nothingToInstead"));
			errors.add(e);
			return null;
		} else {
			BeanUtil.copyToFormList(result, retList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialPartialDetailForm.class);
			return retList;
		}
	}

	/**
	 * *BO缺品零件表生成
	 * @param tempPath
	 * @param lResultForm
	 * @param othesResultForm
	 * @return
	 */
	public String makeBoFile(ActionForm form,SqlSession conn) {
		//模板
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\BO缺品零件表.xls";
		// 临时文件名称
		String cacheName = "boqpljb" + new Date().getTime() + ".xls";
		// 临时文件路径
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" + cacheName;
		
		// 拷贝模板内容
		try {
			org.apache.commons.io.FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 新建查询对象
		MaterialPartialEntity conditionBean = new MaterialPartialEntity();
		// 表单复制到数据对象
		BeanUtil.copyToBean(form, conditionBean, null);
		if(conditionBean.getOrder_date_start()==null && conditionBean.getOrder_date_end()==null){//零件订购日
			Date date[] = getWeekFromAndEnd();
			conditionBean.setOrder_date_start(date[0]);
			conditionBean.setOrder_date_end(date[1]);
		}
		
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialEntity> list = null;
		
		//BO ITEMS
		conditionBean.setKind(1);
		list = dao.searchMaterialBoReport(conditionBean);
		List<MaterialPartialForm> lResultForm = new ArrayList<MaterialPartialForm>();
		// 数据对象复制到表单
		BeanUtil.copyToFormList(list, lResultForm, null, MaterialPartialForm.class);
		
		//BO ITEMS (EndoEye)
		conditionBean.setKind(2);
		list = dao.searchMaterialBoReport(conditionBean);
		List<MaterialPartialForm> othesResultForm = new ArrayList<MaterialPartialForm>();
		// 数据对象复制到表单
		BeanUtil.copyToFormList(list, othesResultForm, null, MaterialPartialForm.class);
		
		//小修理
		conditionBean.setKind(3);
		list = dao.searchMaterialBoReport(conditionBean);
		List<MaterialPartialForm> fixLightForm = new ArrayList<MaterialPartialForm>();
		// 数据对象复制到表单
		BeanUtil.copyToFormList(list, fixLightForm, null, MaterialPartialForm.class);
		
		//周边修理
		conditionBean.setKind(4);//
		list = dao.searchMaterialBoReport(conditionBean);
		List<MaterialPartialForm> peripheralForm = new ArrayList<MaterialPartialForm>();
		// 数据对象复制到表单
		BeanUtil.copyToFormList(list, peripheralForm, null, MaterialPartialForm.class);
		
		try {
			File tempExl = new File(cachePath);
			FileInputStream ins = new FileInputStream(tempExl);
			HSSFWorkbook workbook = new HSSFWorkbook(ins);
			
			HSSFSheet sheet = workbook.getSheetAt(0);
			HSSFSheet sheet2 = workbook.getSheetAt(1);
			HSSFSheet sheet3 = workbook.getSheetAt(2);
			HSSFSheet sheet4 = workbook.getSheetAt(3);
			
			int rowIndex = 1;
			int rowIndex2 = 1;
			int rowIndex3 = 1;
			int rowIndex4 = 1;
			String orderDate = "";

			HSSFCellStyle style = workbook.createCellStyle();
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			HSSFCellStyle styleAka = workbook.createCellStyle();
			styleAka.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			styleAka.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleAka.setBorderTop(HSSFCellStyle.BORDER_THIN);
			styleAka.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleAka.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
			styleAka.setTopBorderColor(HSSFColor.RED.index);
			styleAka.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFPalette palette = workbook.getCustomPalette();
			palette.setColorAtIndex((short)60, (byte)(255), (byte)(255), (byte)(153));
			palette.setColorAtIndex((short)61, (byte)(255), (byte)(204), (byte)(0));
			palette.setColorAtIndex((short)62, (byte)(153), (byte)(204), (byte)(255));
			palette.setColorAtIndex((short)63, (byte)(153), (byte)(204), (byte)(0));

			HSSFCellStyle[] styles = new HSSFCellStyle[6];
			HSSFCellStyle[] stylesAka = new HSSFCellStyle[6];

			styles[0] = workbook.createCellStyle();
			styles[0].cloneStyleFrom(style);
			styles[0].setAlignment(HSSFCellStyle.ALIGN_LEFT);

			stylesAka[0] = workbook.createCellStyle();
			stylesAka[0].cloneStyleFrom(styleAka);
			stylesAka[0].setAlignment(HSSFCellStyle.ALIGN_LEFT);

			styles[1] = workbook.createCellStyle();
			styles[1].cloneStyleFrom(styles[0]);
			styles[1].setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			styles[1].setFillForegroundColor((short)60);

			stylesAka[1] = workbook.createCellStyle();
			stylesAka[1].cloneStyleFrom(stylesAka[0]);
			stylesAka[1].setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			stylesAka[1].setFillForegroundColor((short)60);

			styles[2] = workbook.createCellStyle();
			styles[2].cloneStyleFrom(styles[1]);
			styles[2].setAlignment(HSSFCellStyle.ALIGN_CENTER);
			styles[2].setFillForegroundColor((short)61);

			stylesAka[2] = workbook.createCellStyle();
			stylesAka[2].cloneStyleFrom(stylesAka[1]);
			stylesAka[2].setAlignment(HSSFCellStyle.ALIGN_CENTER);
			stylesAka[2].setFillForegroundColor((short)61);

			styles[3] = workbook.createCellStyle();
			styles[3].cloneStyleFrom(styles[1]);
			styles[3].setAlignment(HSSFCellStyle.ALIGN_CENTER);

			stylesAka[3] = workbook.createCellStyle();
			stylesAka[3].cloneStyleFrom(stylesAka[1]);
			stylesAka[3].setAlignment(HSSFCellStyle.ALIGN_CENTER);

			styles[4] = workbook.createCellStyle();
			styles[4].cloneStyleFrom(styles[1]);
			styles[4].setFillForegroundColor((short)62);

			stylesAka[4] = workbook.createCellStyle();
			stylesAka[4].cloneStyleFrom(stylesAka[1]);
			stylesAka[4].setFillForegroundColor((short)62);

			styles[5] = workbook.createCellStyle();
			styles[5].cloneStyleFrom(styles[2]);
			styles[5].setFillForegroundColor((short)63);

			stylesAka[5] = workbook.createCellStyle();
			stylesAka[5].cloneStyleFrom(stylesAka[2]);
			stylesAka[5].setFillForegroundColor((short)63);

			for (MaterialPartialForm partialForm : lResultForm) {
				
				HSSFRow row = sheet.createRow(rowIndex);
				HSSFCell cellOrderDate = row.createCell(0);
				HSSFCell cellOrderNo = row.createCell(1);
				HSSFCell cellRandAndModel = row.createCell(2);
				HSSFCell cellTier = row.createCell(3);
				HSSFCell cellBoItem = row.createCell(4);
				HSSFCell cellTypeOfBoItem = row.createCell(5);
				HSSFCell cellLtIn3days = row.createCell(6);
				HSSFCell cellRemarks = row.createCell(7);
				HSSFCell cellSafety = row.createCell(8);
				
				boolean flag = !partialForm.getOrder_date().equals(orderDate);
				
				cellOrderDate.setCellValue(partialForm.getOrder_date());
				if ("1".equals(partialForm.getOccur_times())) {
					cellOrderNo.setCellValue(partialForm.getSorc_no());
				} else {
					cellOrderNo.setCellValue(partialForm.getSorc_no().concat("/").concat(partialForm.getOccur_times()));
				}
				cellRandAndModel.setCellValue(partialForm.getLevelName().concat(partialForm.getModel_name()));
				String echelon = partialForm.getEchelon();
				cellTier.setCellValue("1".equals(echelon) ? "1st Tier" : "2".equals(echelon) ? "2nd Tier" : "3".equals(echelon) ? "3rd Tier" : "4".equals(echelon) ? "4th Tier" : "#N/A");
				cellBoItem.setCellValue(partialForm.getBo_item());
				cellTypeOfBoItem.setCellValue("1".equals(partialForm.getType_of_bo_item()) ? "BOM" : "Non-Bom");
				cellLtIn3days.setCellValue("1".equals(partialForm.getBo_within_3days()) ? "√" : "×");
				cellRemarks.setCellValue("");
				cellSafety.setCellValue(partialForm.getSafety() == null || "".equals(partialForm.getSafety()) ? 0 : Integer.parseInt(partialForm.getSafety()));
				
				if (flag) {//日期不等
//					num = 1;
//					cellNo.setCellValue(num);
					orderDate = partialForm.getOrder_date();
				}

				HSSFCellStyle[] theStyles = null;
				//设置对齐和颜色样式
				if (flag) {//日期不等
					cellOrderDate.setCellStyle(styleAka);
					theStyles = stylesAka;
				} else {
					cellOrderDate.setCellStyle(style);
					theStyles = styles;
				}

				cellOrderNo.setCellStyle(theStyles[0]);
				
				cellRandAndModel.setCellStyle(theStyles[1]);
				
				cellTier.setCellStyle(theStyles[2]);
				
				cellBoItem.setCellStyle(theStyles[3]);
				cellSafety.setCellStyle(theStyles[3]);

				cellTypeOfBoItem.setCellStyle(theStyles[4]);
				
				cellLtIn3days.setCellStyle(theStyles[5]);
				cellRemarks.setCellStyle(theStyles[5]);
				
				rowIndex ++;
			}
			
			for (MaterialPartialForm partialForm : othesResultForm) {
				
				HSSFRow row = sheet2.createRow(rowIndex2);
				HSSFCell cellOrderDate = row.createCell(0);
				HSSFCell cellOrderNo = row.createCell(1);
				HSSFCell cellRandAndModel = row.createCell(2);
				HSSFCell cellTier = row.createCell(3);
				HSSFCell cellBoItem = row.createCell(4);
				HSSFCell cellTypeOfBoItem = row.createCell(5);
				HSSFCell cellLtIn3days = row.createCell(6);
				HSSFCell cellRemarks = row.createCell(7);
				HSSFCell cellSafety = row.createCell(8);
				
				boolean flag = !partialForm.getOrder_date().equals(orderDate);
				
				cellOrderDate.setCellValue(partialForm.getOrder_date());
				if ("1".equals(partialForm.getOccur_times())) {
					cellOrderNo.setCellValue(partialForm.getSorc_no());
				} else {
					cellOrderNo.setCellValue(partialForm.getSorc_no().concat("/").concat(partialForm.getOccur_times()));
				}
				cellRandAndModel.setCellValue(partialForm.getLevelName().concat(partialForm.getModel_name()));
				String echelon = partialForm.getEchelon();
				cellTier.setCellValue("1".equals(echelon) ? "1st Tier" : "2".equals(echelon) ? "2nd Tier" : "3".equals(echelon) ? "3rd Tier" : "4".equals(echelon) ? "4th Tier" : "#N/A");
				cellBoItem.setCellValue(partialForm.getBo_item());
				cellTypeOfBoItem.setCellValue("1".equals(partialForm.getType_of_bo_item()) ? "BOM" : "Non-Bom");
				cellLtIn3days.setCellValue("1".equals(partialForm.getBo_within_3days()) ? "√" : "×");
				cellRemarks.setCellValue("");
				cellSafety.setCellValue(partialForm.getSafety() == null || "".equals(partialForm.getSafety()) ? 0 : Integer.parseInt(partialForm.getSafety()));
				
				if (flag) {//日期不等
//					num = 1;
//					cellNo.setCellValue(num);
					orderDate = partialForm.getOrder_date();
				}

				HSSFCellStyle[] theStyles = null;
				//设置对齐和颜色样式
				if (flag) {//日期不等
					cellOrderDate.setCellStyle(styleAka);
					theStyles = stylesAka;
				} else {
					cellOrderDate.setCellStyle(style);
					theStyles = styles;
				}

				cellOrderNo.setCellStyle(theStyles[0]);
				
				cellRandAndModel.setCellStyle(theStyles[1]);
				
				cellTier.setCellStyle(theStyles[2]);
				
				cellBoItem.setCellStyle(theStyles[3]);
				cellSafety.setCellStyle(theStyles[3]);

				cellTypeOfBoItem.setCellStyle(theStyles[4]);
				
				cellLtIn3days.setCellStyle(theStyles[5]);
				cellRemarks.setCellStyle(theStyles[5]);
				
				rowIndex2 ++;
			}
			
			for (MaterialPartialForm partialForm : fixLightForm) {
				
				HSSFRow row = sheet3.createRow(rowIndex3);
				HSSFCell cellOrderDate = row.createCell(0);
				HSSFCell cellOrderNo = row.createCell(1);
				HSSFCell cellRandAndModel = row.createCell(2);
				HSSFCell cellTier = row.createCell(3);
				HSSFCell cellBoItem = row.createCell(4);
				HSSFCell cellTypeOfBoItem = row.createCell(5);
				HSSFCell cellLtIn3days = row.createCell(6);
				HSSFCell cellRemarks = row.createCell(7);
				HSSFCell cellSafety = row.createCell(8);
				
				boolean flag = !partialForm.getOrder_date().equals(orderDate);
				
				cellOrderDate.setCellValue(partialForm.getOrder_date());
				if ("1".equals(partialForm.getOccur_times())) {
					cellOrderNo.setCellValue(partialForm.getSorc_no());
				} else {
					cellOrderNo.setCellValue(partialForm.getSorc_no().concat("/").concat(partialForm.getOccur_times()));
				}
				cellRandAndModel.setCellValue(partialForm.getLevelName().concat(partialForm.getModel_name()));
				String echelon = partialForm.getEchelon();
				cellTier.setCellValue("1".equals(echelon) ? "1st Tier" : "2".equals(echelon) ? "2nd Tier" : "3".equals(echelon) ? "3rd Tier" : "4".equals(echelon) ? "4th Tier" : "#N/A");
				cellBoItem.setCellValue(partialForm.getBo_item());
				cellTypeOfBoItem.setCellValue("1".equals(partialForm.getType_of_bo_item()) ? "BOM" : "Non-Bom");
				cellLtIn3days.setCellValue("1".equals(partialForm.getBo_within_3days()) ? "√" : "×");
				cellRemarks.setCellValue("");
				cellSafety.setCellValue(partialForm.getSafety() == null || "".equals(partialForm.getSafety()) ? 0 : Integer.parseInt(partialForm.getSafety()));
				
				if (flag) {//日期不等
//					num = 1;
//					cellNo.setCellValue(num);
					orderDate = partialForm.getOrder_date();
				}

				HSSFCellStyle[] theStyles = null;
				//设置对齐和颜色样式
				if (flag) {//日期不等
					cellOrderDate.setCellStyle(styleAka);
					theStyles = stylesAka;
				} else {
					cellOrderDate.setCellStyle(style);
					theStyles = styles;
				}

				cellOrderNo.setCellStyle(theStyles[0]);
				
				cellRandAndModel.setCellStyle(theStyles[1]);
				
				cellTier.setCellStyle(theStyles[2]);
				
				cellBoItem.setCellStyle(theStyles[3]);
				cellSafety.setCellStyle(theStyles[3]);

				cellTypeOfBoItem.setCellStyle(theStyles[4]);
				
				cellLtIn3days.setCellStyle(theStyles[5]);
				cellRemarks.setCellStyle(theStyles[5]);
				
				rowIndex3 ++;
			}
			
			
			for (MaterialPartialForm partialForm : peripheralForm) {
				
				HSSFRow row = sheet4.createRow(rowIndex4);
				HSSFCell cellOrderDate = row.createCell(0);
				HSSFCell cellOrderNo = row.createCell(1);
				HSSFCell cellRandAndModel = row.createCell(2);
				HSSFCell cellTier = row.createCell(3);
				HSSFCell cellBoItem = row.createCell(4);
				HSSFCell cellTypeOfBoItem = row.createCell(5);
				HSSFCell cellLtIn3days = row.createCell(6);
				HSSFCell cellRemarks = row.createCell(7);
				HSSFCell cellSafety = row.createCell(8);
				
				boolean flag = !partialForm.getOrder_date().equals(orderDate);
				
				cellOrderDate.setCellValue(partialForm.getOrder_date());
				if ("1".equals(partialForm.getOccur_times())) {
					cellOrderNo.setCellValue(partialForm.getSorc_no());
				} else {
					cellOrderNo.setCellValue(partialForm.getSorc_no().concat("/").concat(partialForm.getOccur_times()));
				}
				cellRandAndModel.setCellValue(partialForm.getLevelName().concat(partialForm.getModel_name()));
				String echelon = partialForm.getEchelon();
				cellTier.setCellValue("1".equals(echelon) ? "1st Tier" : "2".equals(echelon) ? "2nd Tier" : "3".equals(echelon) ? "3rd Tier" : "4".equals(echelon) ? "4th Tier" : "#N/A");
				cellBoItem.setCellValue(partialForm.getBo_item());
				cellTypeOfBoItem.setCellValue("1".equals(partialForm.getType_of_bo_item()) ? "BOM" : "Non-Bom");
				cellLtIn3days.setCellValue("1".equals(partialForm.getBo_within_3days()) ? "√" : "×");
				cellRemarks.setCellValue("");
				cellSafety.setCellValue(partialForm.getSafety() == null || "".equals(partialForm.getSafety()) ? 0 : Integer.parseInt(partialForm.getSafety()));
				
				if (flag) {//日期不等
//					num = 1;
//					cellNo.setCellValue(num);
					orderDate = partialForm.getOrder_date();
				}

				HSSFCellStyle[] theStyles = null;
				//设置对齐和颜色样式
				if (flag) {//日期不等
					cellOrderDate.setCellStyle(styleAka);
					theStyles = stylesAka;
				} else {
					cellOrderDate.setCellStyle(style);
					theStyles = styles;
				}

				cellOrderNo.setCellStyle(theStyles[0]);
				
				cellRandAndModel.setCellStyle(theStyles[1]);
				
				cellTier.setCellStyle(theStyles[2]);
				
				cellBoItem.setCellStyle(theStyles[3]);
				cellSafety.setCellStyle(theStyles[3]);

				cellTypeOfBoItem.setCellStyle(theStyles[4]);
				
				cellLtIn3days.setCellStyle(theStyles[5]);
				cellRemarks.setCellStyle(theStyles[5]);
				
				rowIndex4 ++;
			}
			
			Date today = new Date();
			String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");
			File fMonthPath = new File(folder);
			if (!fMonthPath.exists()) {
				fMonthPath.mkdirs();
			}
			String outputFile = new Date().getTime() + ".xls";
			
			FileOutputStream fileOut = new FileOutputStream(folder + "\\" + outputFile);   
			workbook.write(fileOut);   
			fileOut.close(); 

			return outputFile;
		}catch (Exception e) {
			_log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 维修对象订购一览表导出
	 * @param tempPath
	 * @param lResultForm
	 * @param lResultItemForm
	 * @return
	 */
	public String makeOrderFile(String tempPath, List<MaterialPartialForm> lResultForm,List<MaterialPartialForm> lResultItemForm) {
		FileOutputStream fileOut = null;

		try {
			File tempExl = new File(tempPath);
			FileInputStream ins = new FileInputStream(tempExl);
			HSSFWorkbook workbook = new HSSFWorkbook(ins);
			
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			int num = 1;
			int rowIndex = 1;
			String orderDate = "";
			float totalTier = 0;
			Map<String, Integer> tiers = new HashMap<String, Integer>();
			
			HSSFCellStyle style = workbook.createCellStyle();
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			HSSFCellStyle styleAka = workbook.createCellStyle();
			styleAka.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			styleAka.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleAka.setBorderTop(HSSFCellStyle.BORDER_THIN);
			styleAka.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleAka.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			styleAka.setBorderTop(HSSFCellStyle.BORDER_DOUBLE);
			styleAka.setTopBorderColor(HSSFColor.RED.index);

			HSSFCellStyle style2 = workbook.createCellStyle();
			style2.cloneStyleFrom(style);
			//居中
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle styleAka2 = workbook.createCellStyle();
			styleAka2.cloneStyleFrom(styleAka);
			//居中
			styleAka2.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			for (MaterialPartialForm partialForm : lResultForm) {
				
				
				HSSFRow row = sheet.createRow(rowIndex);
				HSSFCell cellNo = row.createCell(0);
				HSSFCell cellOrderDate = row.createCell(1);
				HSSFCell cellOrderNo = row.createCell(2);
				HSSFCell cellModel = row.createCell(3);
				HSSFCell cellRank = row.createCell(4);
				HSSFCell cellRandAndModel = row.createCell(5);
				HSSFCell cellTier = row.createCell(6);
				HSSFCell cellQty = row.createCell(7);
				HSSFCell cellBo = row.createCell(8);
				HSSFCell cellBoIn3days = row.createCell(9);

				boolean flag = !partialForm.getOrder_date().equals(orderDate);
				
				cellNo.setCellValue(num);
				cellOrderDate.setCellValue(partialForm.getOrder_date());
				if ("1".equals(partialForm.getOccur_times())) {
					cellOrderNo.setCellValue(partialForm.getSorc_no());
				} else {
					cellOrderNo.setCellValue(partialForm.getSorc_no().concat("/").concat(partialForm.getOccur_times()));
				}
				cellModel.setCellValue(partialForm.getModel_name());
				cellRank.setCellValue(partialForm.getLevelName());
				cellRandAndModel.setCellValue(partialForm.getLevelName().concat(partialForm.getModel_name()));
				String echelon = partialForm.getEchelon();
				if (echelon != null && !"".equals(echelon)) {
					totalTier+=1;
					if (tiers.containsKey(echelon)) {
						int value = tiers.get(echelon).intValue()+1;
						tiers.put(echelon, value);
					} else {
						tiers.put(echelon, 1);
					}
				}
				cellTier.setCellValue(CodeListUtils.getValue("echelon_report_code", partialForm.getEchelon()));
				boolean boFlag = "1".equals(partialForm.getBo_flg()) || "2".equals(partialForm.getBo_flg());
				cellBo.setCellValue(boFlag ? "√" : "");
				cellBoIn3days.setCellValue(boFlag && "1".equals(partialForm.getBo_within_3days()) ? "√" : boFlag ? "×" : "");

				if (flag) {//日期不等
					orderDate = partialForm.getOrder_date();
					num = 1;
					cellNo.setCellValue(num);

					cellOrderNo.setCellStyle(styleAka);
					cellModel.setCellStyle(styleAka);
					cellRandAndModel.setCellStyle(styleAka);
					cellTier.setCellStyle(styleAka);

					cellNo.setCellStyle(styleAka2);
					cellOrderDate.setCellStyle(styleAka2);
					cellRank.setCellStyle(styleAka2);
					cellQty.setCellStyle(styleAka2);
					cellBo.setCellStyle(styleAka2);
					cellBoIn3days.setCellStyle(styleAka2);
				} else {
					
					//左对齐
					cellOrderNo.setCellStyle(style);
					cellModel.setCellStyle(style);
					cellRandAndModel.setCellStyle(style);
					cellTier.setCellStyle(style);
					
					cellNo.setCellStyle(style2);
					cellOrderDate.setCellStyle(style2);
					cellRank.setCellStyle(style2);
					cellQty.setCellStyle(style2);
					cellBo.setCellStyle(style2);
					cellBoIn3days.setCellStyle(style2);
				}
				
				num ++;
				rowIndex ++;
			}
			
			createTotalRow(sheet, rowIndex + 5, "计数项:Tier", "", "", "", tiers, totalTier, workbook);
			createTotalRow(sheet, rowIndex + 6, "Tier", "汇总", "订购比率", "", tiers, totalTier, workbook);
			createTotalRow(sheet, rowIndex + 7, "1st Tier", "", "", "1", tiers, totalTier,  workbook);
			createTotalRow(sheet, rowIndex + 8, "2nd Tier", "", "", "2", tiers, totalTier,  workbook);
			createTotalRow(sheet, rowIndex + 9, "3rd Tier", "", "", "3", tiers, totalTier,  workbook);
			createTotalRow(sheet, rowIndex + 10, "4th Tier", "", "", "4", tiers, totalTier,  workbook);
			createTotalRow(sheet, rowIndex + 11, "总计", totalTier+"", "", "", tiers, totalTier,  workbook);
			
			
			HSSFSheet sheet2 = workbook.getSheetAt(1);
			HSSFPalette palette = workbook.getCustomPalette();
			palette.setColorAtIndex((short)60, (byte)(255), (byte)(255), (byte)(153));
			palette.setColorAtIndex((short)61, (byte)(255), (byte)(204), (byte)(0));
			palette.setColorAtIndex((short)62, (byte)(153), (byte)(204), (byte)(255));
			palette.setColorAtIndex((short)63, (byte)(153), (byte)(204), (byte)(0));

			HSSFCellStyle[] styles = new HSSFCellStyle[8];
			HSSFCellStyle[] stylesAka = new HSSFCellStyle[8];

			styles[0] = workbook.createCellStyle();
			styles[0].cloneStyleFrom(style);
			styles[0].setAlignment(HSSFCellStyle.ALIGN_LEFT);

			stylesAka[0] = workbook.createCellStyle();
			stylesAka[0].cloneStyleFrom(styleAka);
			stylesAka[0].setAlignment(HSSFCellStyle.ALIGN_LEFT);

			styles[1] = workbook.createCellStyle();
			styles[1].cloneStyleFrom(styles[0]);
			styles[1].setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			styles[1].setFillForegroundColor((short)60);

			stylesAka[1] = workbook.createCellStyle();
			stylesAka[1].cloneStyleFrom(stylesAka[0]);
			stylesAka[1].setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			stylesAka[1].setFillForegroundColor((short)60);

			styles[2] = workbook.createCellStyle();
			styles[2].cloneStyleFrom(styles[1]);
			styles[2].setAlignment(HSSFCellStyle.ALIGN_CENTER);
			styles[2].setFillForegroundColor((short)61);

			stylesAka[2] = workbook.createCellStyle();
			stylesAka[2].cloneStyleFrom(stylesAka[1]);
			stylesAka[2].setAlignment(HSSFCellStyle.ALIGN_CENTER);
			stylesAka[2].setFillForegroundColor((short)61);

			styles[3] = workbook.createCellStyle();
			styles[3].cloneStyleFrom(styles[1]);
			styles[3].setAlignment(HSSFCellStyle.ALIGN_CENTER);

			stylesAka[3] = workbook.createCellStyle();
			stylesAka[3].cloneStyleFrom(stylesAka[1]);
			stylesAka[3].setAlignment(HSSFCellStyle.ALIGN_CENTER);

			styles[4] = workbook.createCellStyle();
			styles[4].cloneStyleFrom(styles[1]);
			styles[4].setFillForegroundColor((short)62);

			stylesAka[4] = workbook.createCellStyle();
			stylesAka[4].cloneStyleFrom(stylesAka[1]);
			stylesAka[4].setFillForegroundColor((short)62);

			styles[5] = workbook.createCellStyle();
			styles[5].cloneStyleFrom(styles[2]);
			styles[5].setFillForegroundColor((short)63);

			stylesAka[5] = workbook.createCellStyle();
			stylesAka[5].cloneStyleFrom(stylesAka[2]);
			stylesAka[5].setFillForegroundColor((short)63);

			styles[6] = workbook.createCellStyle();
			styles[6].cloneStyleFrom(styles[4]);
			styles[6].setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			stylesAka[6] = workbook.createCellStyle();
			stylesAka[6].cloneStyleFrom(stylesAka[4]);
			stylesAka[6].setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			styles[7] = workbook.createCellStyle();
			styles[7].cloneStyleFrom(styles[1]);
			styles[7].setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			stylesAka[7] = workbook.createCellStyle();
			stylesAka[7].cloneStyleFrom(stylesAka[1]);
			stylesAka[7].setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			rowIndex=1;
			for (MaterialPartialForm partialForm : lResultItemForm) {
				
				HSSFRow row = sheet2.createRow(rowIndex);
				HSSFCell cellOrderDate = row.createCell(0);
				HSSFCell cellOrderNo = row.createCell(1);
				HSSFCell cellRandAndModel = row.createCell(2);
				HSSFCell cellTier = row.createCell(3);
				HSSFCell cellBoItem = row.createCell(4);
				HSSFCell cellTypeOfBoItem = row.createCell(5);
				HSSFCell cellQty = row.createCell(6);
//				HSSFCell cellBo = row.createCell(6);
//				HSSFCell cellLTIn3days = row.createCell(7);
				HSSFCell cellRemarks = row.createCell(7);
				HSSFCell cellSafety = row.createCell(8);
				
				
				boolean flag = !partialForm.getOrder_date().equals(orderDate);
				
				cellOrderDate.setCellValue(partialForm.getOrder_date());
				if ("1".equals(partialForm.getOccur_times())) {
					cellOrderNo.setCellValue(partialForm.getSorc_no());
				} else {
					cellOrderNo.setCellValue(partialForm.getSorc_no().concat("/").concat(partialForm.getOccur_times()));
				}
				cellRandAndModel.setCellValue(partialForm.getLevelName().concat(partialForm.getModel_name()));
				String echelon = partialForm.getEchelon();
				cellTier.setCellValue(CodeListUtils.getValue("echelon_report_code", "" + echelon));
				cellBoItem.setCellValue(partialForm.getBo_item());
				cellTypeOfBoItem.setCellValue("1".equals(partialForm.getType_of_bo_item()) ? "BOM" : "Non-Bom");

				Integer qtyvalue = Integer.parseInt(partialForm.getQty());
				cellQty.setCellValue(qtyvalue);
//				boolean boFlag = "3".equals(partialForm.getBo_flg()) || "4".equals(partialForm.getBo_flg());
//				cellBo.setCellValue(boFlag ? "√" : "");
//				
//				cellLTIn3days.setCellValue(boFlag && "1".equals(partialForm.getBo_within_3days()) ? "√" : boFlag ? "×" : "");
				
				cellRemarks.setCellValue("");
				cellSafety.setCellValue(partialForm.getSafety() == null || "".equals(partialForm.getSafety()) ? 0 : Integer.parseInt(partialForm.getSafety()));
				
				if (flag) {//日期不等
					orderDate = partialForm.getOrder_date();
				}

				HSSFCellStyle[] theStyles = null;
				//设置对齐和颜色样式
				if (flag) {//日期不等
					cellOrderDate.setCellStyle(styleAka);
					theStyles = stylesAka;
				} else {
					cellOrderDate.setCellStyle(style);
					theStyles = styles;
				}

				cellOrderNo.setCellStyle(theStyles[0]);
				
				cellRandAndModel.setCellStyle(theStyles[1]);
				
				cellTier.setCellStyle(theStyles[2]);
				
				cellBoItem.setCellStyle(theStyles[3]);
				cellSafety.setCellStyle(theStyles[7]);

				cellTypeOfBoItem.setCellStyle(theStyles[4]);
				cellQty.setCellStyle(theStyles[6]);
//				cellBo.setCellStyle(theStyles[4]);
//				cellLTIn3days.setCellStyle(theStyles[4]);
				
				
				cellRemarks.setCellStyle(theStyles[5]);
				
				rowIndex ++;
			}

			fileOut = new FileOutputStream(tempPath);
			workbook.write(fileOut);   
			fileOut.close(); 
			
			return tempPath;
		}catch (Exception e) {
			_log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 零件追加明细表导出
	 * @param tempPath
	 * @param lResultForm
	 * @return
	 */
	public String makePartialAddtionalInfFile(String tempPath, List<MaterialPartialForm> lResultForm) {
		try {
			File tempExl = new File(tempPath);
			FileInputStream ins = new FileInputStream(tempExl);
			HSSFWorkbook workbook = new HSSFWorkbook(ins);
			
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			int num = 0;
			int rowIndex = 1;
			
			HSSFCellStyle style = workbook.createCellStyle();
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			HSSFCellStyle styleT = workbook.createCellStyle();
			styleT.cloneStyleFrom(style);

            HSSFDataFormat thdf= workbook.createDataFormat();
            styleT.setDataFormat(thdf.getFormat("@"));

			HSSFCellStyle style2 = workbook.createCellStyle();
			style2.cloneStyleFrom(style);

			//居中
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle styleD = workbook.createCellStyle();
			styleD.cloneStyleFrom(style2);

            HSSFDataFormat dhdf= workbook.createDataFormat();
            styleD.setDataFormat(dhdf.getFormat("yyyy-m-d"));

			HSSFCellStyle styleN = workbook.createCellStyle();
			styleN.cloneStyleFrom(style);
			//居右
			styleN.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			String sorc_no = "";
			
			for (MaterialPartialForm partialForm : lResultForm) {
				
				HSSFRow row = sheet.createRow(rowIndex);
				
				HSSFCell cellNo = row.createCell(0);
				HSSFCell cellOutlineDate = row.createCell(1);
				HSSFCell cellSorcNo = row.createCell(2);
				HSSFCell cellModelName = row.createCell(3);
				HSSFCell cellSerialNo = row.createCell(4);
				HSSFCell cellRank = row.createCell(5);
				HSSFCell cellBelongs = row.createCell(6);
				HSSFCell cellBad = row.createCell(7);			
				HSSFCell cellCode = row.createCell(8);
				HSSFCell cellQuantity = row.createCell(9);
				HSSFCell cellPrice = row.createCell(10);
				HSSFCell cellTotalPrice = row.createCell(11);
				HSSFCell cellComments = row.createCell(12);
				
				boolean flag = partialForm.getSorc_no().equals(sorc_no);
				//设置单元格的值
				cellNo.setCellValue(num);
				
				//SORC_NO不等
				if(!flag){
					num++;
					cellNo.setCellValue(num);
					sorc_no = partialForm.getSorc_no();
				}
				
				cellNo.setCellStyle(style2);
				cellOutlineDate.setCellStyle(styleD);
				cellSorcNo.setCellStyle(style);
				cellModelName.setCellStyle(style);
				cellSerialNo.setCellStyle(styleT);
				cellRank.setCellStyle(style2);
				cellBelongs.setCellStyle(style);
				cellBad.setCellStyle(style);
				cellCode.setCellStyle(style);
				cellQuantity.setCellStyle(style2);
				cellPrice.setCellStyle(styleN);
				cellTotalPrice.setCellStyle(styleN);
				cellComments.setCellStyle(style);
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				Date date= null;
				if (partialForm.getOutline_date_start() != null) {
					date = format.parse(partialForm.getOutline_date_start());
					cellOutlineDate.setCellValue(date);
				} else {
					cellOutlineDate.setCellValue("在线");
				}

				cellSorcNo.setCellValue(partialForm.getSorc_no());
				cellModelName.setCellValue(partialForm.getModel_name());
				cellSerialNo.setCellValue(partialForm.getSerial_no());
				cellRank.setCellValue(partialForm.getLevelName());
				cellBelongs.setCellValue(CodeListUtils.getValue("partial_append_belongs", partialForm.getBelongs()));
				cellBad.setCellValue("");
				cellCode.setCellValue(partialForm.getBo_item());

				cellQuantity.setCellValue(new BigInteger(partialForm.getQty()).intValue());
				String price = partialForm.getPrice();
				if (price == null) {
					cellPrice.setCellValue("-");
				} else {
					cellPrice.setCellValue(new BigDecimal(price).doubleValue());
				}

				String totalPrice = partialForm.getTotalPrice();
				if (totalPrice == null) {
					cellTotalPrice.setCellValue("-");
				} else {
					cellTotalPrice.setCellValue(new BigDecimal(totalPrice).doubleValue());
				}

				cellComments.setCellValue("");	
				
				rowIndex ++;
			}
			
			Date today = new Date();
			String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");
			File fMonthPath = new File(folder);
			if (!fMonthPath.exists()) {
				fMonthPath.mkdirs();
			}
			String outputFile = new Date().getTime() + ".xls";
			
			FileOutputStream fileOut = new FileOutputStream(folder + "\\" + outputFile);   
			workbook.write(fileOut);   
			fileOut.close(); 

			return outputFile;
		}catch (Exception e) {
			_log.error(e.getMessage(), e);
			return null;
		}
	}

	
	private void createTotalRow(HSSFSheet sheet, int rowIndex, String value1, String value2,
			String value3, String mapkey, Map<String, Integer> tiers, float totalTier,  HSSFWorkbook workbook) {
		HSSFRow row = sheet.createRow(rowIndex + 1);
		HSSFCell cellTier = row.createCell(6);
		HSSFCell cellTotal = row.createCell(7);
		HSSFCell cellRate = row.createCell(8);
		
		cellTier.setCellValue(value1);
		cellTotal.setCellValue(value2);
		cellRate.setCellValue(value3);
		
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		cellTier.setCellStyle(style);
		cellTotal.setCellStyle(style);
		cellRate.setCellStyle(style);
		
		
		if (tiers.containsKey(mapkey)) {
			cellTotal.setCellValue(tiers.get(mapkey).intValue());
			
			HSSFCellStyle style2 = workbook.createCellStyle();
			style2.cloneStyleFrom(style);
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellTotal.setCellStyle(style2);
			
			
			String value = Integer.valueOf(Math.round((tiers.get(mapkey).intValue() / totalTier) * 100))
					.toString().concat("%");
			cellRate.setCellValue(value);
			
			HSSFCellStyle style3 = workbook.createCellStyle();
			style3.cloneStyleFrom(style);
			style3.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			cellRate.setCellStyle(style3);
		}
	}

	/**
	 * 替代处理
	 * @param session 
	 * @param req
	 * @param conn
	 * @throws Exception 
	 */
	public void updateInstead(ActionForm form, Map<String, String[]> parameterMap, HttpSession session, SqlSessionManager conn, Integer means) throws Exception {
		List<MaterialPartialDetailForm> materialPartialDetails = new AutofillArrayList<MaterialPartialDetailForm>(MaterialPartialDetailForm.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("exchange".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					// TODO 全
					if ("material_partial_detail_key".equals(column)) {
						materialPartialDetails.get(icounts).setMaterial_partial_detail_key(value[0]);
					} else if ("quantity".equals(column)) {
						materialPartialDetails.get(icounts).setRecept_quantity(value[0]);
					} else if ("total".equals(column)) {
						materialPartialDetails.get(icounts).setQuantity(value[0]);
					}
				}
			}
		}

		MaterialPartialMapper mapper = conn.getMapper(MaterialPartialMapper.class);
		for (MaterialPartialDetailForm materialPartialDetail : materialPartialDetails) {
			MaterialPartialDetailEntity entity = new MaterialPartialDetailEntity();
			BeanUtil.copyToBean(materialPartialDetail, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
			// 消耗品签收
			entity.setStatus(means);
			// 签收者
			entity.setR_operator_id(user.getOperator_id());

			if (entity.getQuantity() == entity.getRecept_quantity()) {
				// 全数替代
				mapper.updateMaterialInstead(entity);

				MaterialPartialEntity mpEntity = new MaterialPartialEntity();
				BeanUtil.copyToBean(form, mpEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

				// 重新设定BO状态
				mapper.resetBoStatusByInstead(mpEntity);

			} else {
				// 部分替代
				mapper.sparedMaterialInstead(entity);
				mapper.insertMaterialInstead(entity);
			}
		}

		conn.commit();

//		try {
//			// 信息推送
//			PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
//			PostMessageEntity pmEntity = new PostMessageEntity();
//			pmEntity.setSender_id(user.getOperator_id());
//			pmEntity.setContent(user.getName() + "将" + parameterMap.get("sorc_no")[0] + "的" + materialPartialDetails.size() + "件零件的替代签收，请确认详细状况！");
//			pmEntity.setLevel(1);
//			pmEntity.setReason(7);
//	
//			// 查询零件管理员
//			OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);
//	
//			pmMapper.createPostMessage(pmEntity);
//	
//			CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
//			String lastInsertID = commonMapper.getLastInsertID();
//			pmEntity.setPost_message_id(lastInsertID);
//	
//			// TODO
//			List<String> systemmers = new ArrayList<String>();
//			systemmers.add("00000000064");
//			systemmers.add("00000000066");
//			systemmers.add("00000000091");
//	
//			for (String systemmer : systemmers) {
//				if (systemmer.equals(user.getOperator_id())) continue;
//				pmEntity.setReceiver_id(systemmer);
//				pmMapper.createPostMessageSendation(pmEntity);
//			}
//		}catch(Exception e){
//			_log.error(e.getMessage(), e);
//		}
	}

	/**
	 * 零件签收归档
	 * @param folderPath 
	 * @param entity
	 */
	public void createArchireOfPartialRecept(MaterialForm mform, String folderPath, SqlSession conn){
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "货物到达验收确认单模板.xls";
		String cacheName ="货物到达验收确认单" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 

		MaterialPartialMapper mapper = conn.getMapper(MaterialPartialMapper.class);
		List<MaterialPartialDetailEntity> list=mapper.archiveOfPartialRecept(mform.getMaterial_id());

		FileUtils.copyFile(path, cachePath);

		OutputStream out = null;
		InputStream in = null;

		MaterialPartialDetailEntity tempEntity=null;
		try{
			if(list!=null){
				if(list.size()>0){
					in = new FileInputStream(cachePath);//读取文件 
					HSSFWorkbook work=new HSSFWorkbook(in);//创建xls文件
					HSSFSheet sheet=null;
					sheet=work.getSheetAt(0);//取得第一个Sheet
					HSSFRow fromRow=null;
					fromRow=sheet.getRow(0);//第一行
					tempEntity=list.get(0);
					
					String sorcNO=mform.getSorc_no();
					fromRow.getCell(7).setCellValue(sorcNO);//SORC_NO.
					sheet.getRow(7).getCell(2).setCellValue(mform.getModel_name());//model_name
					sheet.getRow(8).getCell(2).setCellValue(mform.getSerial_no());//serial_no
					
					Date arrivalDate=tempEntity.getRecent_signin_time();
					if(arrivalDate==null){
						sheet.getRow(10).getCell(2).setCellValue("");
					}else{
						sheet.getRow(10).getCell(2).setCellValue(arrivalDate);
					}

					Set<Integer> orderTimes = new TreeSet<Integer>();
					
					int length=list.size();//数据长度
					int index=0;
					//复制行
					if(length>19){
						index=(length-19)%27==0 ? (length-19)/27 :(length-19)/27+1;//需要复制的页数(不包含第一页)
						for(int j=0;j<index;j++){
							fromRow=sheet.getRow(0);//第1行
							
							int lastRowNum=sheet.getLastRowNum();
							HSSFRow toRow=null;
							 
							int tempIndex=0;
							tempIndex=lastRowNum+1;
							toRow=sheet.createRow(tempIndex);
							CopyByPoi.copyRow(fromRow, toRow, true);
							CellRangeAddress  region = null;
							region=new CellRangeAddress(tempIndex,tempIndex,7,9);
							sheet.addMergedRegion(region);
							
							fromRow=sheet.getRow(12);//第13行
							tempIndex=lastRowNum+2;
							toRow=sheet.createRow(tempIndex);
							CopyByPoi.copyRow(fromRow, toRow, true);
							region=new CellRangeAddress(tempIndex,tempIndex,1,4);
							sheet.addMergedRegion(region);
							region=new CellRangeAddress(tempIndex,tempIndex,7,8);
							sheet.addMergedRegion(region);
							
							fromRow=sheet.getRow(13);//第14行
							for(int ii=0;ii<27;ii++){
								int dex=lastRowNum+ii+3;
								toRow=sheet.createRow(dex);
								CopyByPoi.copyRow(fromRow, toRow, true);
								region=new CellRangeAddress(dex,dex,7,8);
								sheet.addMergedRegion(region);
							}
							
							fromRow=sheet.getRow(32);//第33行
							tempIndex=lastRowNum+30;
							toRow=sheet.createRow(tempIndex);
							CopyByPoi.copyRow(fromRow, toRow, true);
							region=new CellRangeAddress(tempIndex,tempIndex,6,7);
							sheet.addMergedRegion(region);
							region=new CellRangeAddress(tempIndex,tempIndex,8,9);
							sheet.addMergedRegion(region);
						}
					}
					
					//设置字体大小
					HSSFFont font  = work.createFont();
					font.setFontHeightInPoints((short) 10);
					font.setFontName("微软雅黑");

					HSSFCellStyle signinTtimeStyle = work.createCellStyle();
					signinTtimeStyle.setDataFormat(work.createDataFormat().getFormat("mm-dd")); 
					signinTtimeStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
					signinTtimeStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					signinTtimeStyle.setFont(font);
					
					HSSFCellStyle receiveTimeStyle = work.createCellStyle();    
					receiveTimeStyle.setDataFormat(work.createDataFormat().getFormat("mm-dd h\"时\"")); 
					receiveTimeStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
					receiveTimeStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					receiveTimeStyle.setFont(font);
					
					int total=0;
					
					//插入数据
					for(int i=0;i<list.size();i++){
						tempEntity=list.get(i);
						
						Integer occurTimes=tempEntity.getOccur_times();
						orderTimes.add(occurTimes);

						int iRow=(i+8)/27*4+13+i;
						HSSFRow row=sheet.getRow(iRow);
						row.getCell(0).setCellValue(tempEntity.getPartial_code());
						row.getCell(1).setCellValue(tempEntity.getPartial_name());
						row.getCell(5).setCellValue(tempEntity.getQuantity());
						
						Date recent_signin_time=tempEntity.getRecent_signin_time();
						if(recent_signin_time==null){
							row.getCell(6).setCellValue("");
						}else{
							row.getCell(6).setCellValue(recent_signin_time);
						}
						row.getCell(6).setCellStyle(signinTtimeStyle);
						
						Date receiveTime=tempEntity.getRecent_receive_time();
						if(receiveTime==null){
							row.getCell(7).setCellValue("");
						}else{
							row.getCell(7).setCellValue(receiveTime);	
						}
						row.getCell(7).setCellStyle(receiveTimeStyle);
						
						String operator_name="";
						if(!CommonStringUtil.isEmpty(tempEntity.getJob_no())){
							operator_name = PathConsts.BASE_PATH + PathConsts.IMAGES + "\\sign\\" + tempEntity.getJob_no();
							insertImage(work,sheet,9,iRow,operator_name);
						}
						

						if (tempEntity.getJob_no() == null) {
							_log.error("KKKK" + mform.getMaterial_id());
						} else {
							insertImage(work,sheet,9,iRow,operator_name);
						}

						total=total+tempEntity.getQuantity();
					}

					// 单号
					int orderTimesSize = orderTimes.size();
					if(orderTimesSize==1){
						sheet.getRow(7).getCell(8).setCellValue(sorcNO);
					}else if(orderTimesSize==2){
						int i = 0;
						sheet.getRow(7).getCell(8).setCellValue(sorcNO);
						for (Integer orderTime : orderTimes) {
							if (i==1) {
								sheet.getRow(8).getCell(6).setCellValue(sorcNO+"/"+orderTime);
							} else if (i > 1) {
								break;
							}
							i++;
						}
					}else if(orderTimesSize==3){
						int i = 0;
						sheet.getRow(7).getCell(8).setCellValue(sorcNO);
						for (Integer orderTime : orderTimes) {
							if (i==1) {
								sheet.getRow(8).getCell(6).setCellValue(sorcNO+"/"+orderTime);
							} else if (i==2) {
								sheet.getRow(8).getCell(8).setCellValue(sorcNO+"/"+orderTime);
							} else if (i > 2) {
								break;
							}
							i++;
						}
					}else if(orderTimesSize>3){
						int i = 0;
						sheet.getRow(7).getCell(8).setCellValue(sorcNO);
						for (Integer orderTime : orderTimes) {
							if (i==1) {
								sheet.getRow(8).getCell(6).setCellValue(sorcNO+"/"+orderTime);
							} else if (i==2) {
								sheet.getRow(8).getCell(8).setCellValue(sorcNO+"/"+orderTime);
							} else if (i > 2) {
								sheet.getRow(9).getCell(6).setCellValue("等总计" + orderTimesSize + "件订货");
								break;
							}
							i++;
						}
					}

					//清空最后一页之前的页面里的数量只計，如果只有一页不清除
					for(int jj=0;jj<index;jj++){
						int iRow=(jj+1)*31+1;
						HSSFRow row=sheet.getRow(iRow);
						row.getCell(4).setCellValue("");
						row.getCell(5).setCellValue("");
					}
					
					//最后一页数量只計赋值 TODO
					HSSFRow last = sheet.getRow(sheet.getLastRowNum());
					if (last.getCell(5) == null) {
						last = sheet.getRow(sheet.getLastRowNum() - 1);
					}
					last.getCell(5).setCellValue(total);

//					CellRangeAddress allregion = new CellRangeAddress(0,sheet.getLastRowNum(),0,9);

					out = new FileOutputStream(cachePath);
					work.write(out);

					// 转换到Pdf
					XlsUtil xlsUtil = new XlsUtil(cachePath);
					xlsUtil.SaveAsPdf(folderPath + "\\货物到达验收确认单.pdf");
				}
			}
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
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 插入图片
	 * @param work Excel表格
	 * @param sheet 
	 * @param iCol 列位子
	 * @param iLine 行位子
	 * @param fileName 图片名称
	 */
	public void insertImage(HSSFWorkbook work,HSSFSheet sheet,int iCol,int iLine,String fileName){
		try{
			BufferedImage bufferImg=ImageIO.read(new File(fileName));
			ByteArrayOutputStream byteArrayOut=new ByteArrayOutputStream();
			ImageIO.write(bufferImg, "jpg",byteArrayOut);
			HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
			if (patriarch == null) {
				patriarch = sheet.createDrawingPatriarch();
			}
			
			int def = sheet.getDefaultColumnWidth()*365;  
			
			int iCurWidth =sheet.getColumnWidth(iCol);
			sheet.setColumnWidth(iCol, def);
			
			HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) iCol, iLine, (short) iCol, iLine);
			anchor.setAnchorType(5);
			patriarch.createPicture(anchor,work.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG)).resize(1);
			sheet.setColumnWidth(iCol, iCurWidth);
		}catch(Exception e){
			e.printStackTrace();
			_log.info("图片"+fileName+"不存在!");
		}
	}

	/**
	 * 更新入库预订日
	 * @param req
	 * @param conn
	 * @throws Exception 
	 */
	public String updateMaterialPartial(HttpServletRequest req, LoginData logindata, SqlSessionManager conn) throws Exception {
		List<MaterialPartialDetailEntity> paritals = new AutofillArrayList<MaterialPartialDetailEntity>(MaterialPartialDetailEntity.class);

		Map<String, String[]> parameterMap = req.getParameterMap();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		Converter<Date> dc = DateConverter.getInstance(DateUtil.DATE_PATTERN);
		String confirmed = req.getParameter("confirmed");

		// String delegateMaterial_partial_detail_key = null;
		// 查询零件管理员
		OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("update".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					// TODO 全
					if ("arrival_plan_date".equals(column)) {
						String sArrival_plan_date = value[0];
						if (CommonStringUtil.isEmpty(sArrival_plan_date))
							sArrival_plan_date = "9999/12/31";
						paritals.get(icounts).setArrival_plan_date(dc.getAsObject(sArrival_plan_date));
					} else if ("material_partial_detail_key".equals(column)) {
						paritals.get(icounts).setMaterial_partial_detail_key(value[0]);
					}
				}
			}
		}

		// 判断有被零件管理员/系统管理员更新了的入库预定日
		List<Integer> privacy = logindata.getPrivacies();
		boolean isManager = false;
		if (privacy.contains(RvsConsts.PRIVACY_ADMIN) || privacy.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)) {
			isManager = true;
		}

		MaterialPartialMapper mapper = conn.getMapper(MaterialPartialMapper.class);
		String conflexError = "";
		for (MaterialPartialDetailEntity parital : paritals) {
			// 得到修改前信息
			MaterialPartialDetailEntity orgEntity = mapper.getMaterialPartialDetailByKey(parital.getMaterial_partial_detail_key());
			parital.setCode(orgEntity.getCode());
			parital.setHistory_limit_date(orgEntity.getArrival_plan_date());
			if (orgEntity.getArrival_plan_date() == null || parital.getArrival_plan_date().equals(orgEntity.getArrival_plan_date()))
				continue;
			String orgArrivalPlanDate = dc.getAsString(orgEntity.getArrival_plan_date());
			if (confirmed == null && !isManager 
					&& orgArrivalPlanDate != null && !"9999/12/31".equals(orgArrivalPlanDate)) {
				if (parital.getArrival_plan_date().equals(parital.getHistory_limit_date()))
					continue;
				if (orgEntity.getR_operator_id() != null) {
					String operator_name = "管理员";
					OperatorEntity oE = oMapper.getOperatorByID(orgEntity.getR_operator_id());
					if (oE != null) {
						operator_name = oE.getName();
					}
					// 系统管理员已经更新过
					conflexError += ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.managerPlanned", 
							orgEntity.getCode(), operator_name, orgArrivalPlanDate) + "\n";
				}
			}
		}
		if (conflexError.length() > 0) {
			return conflexError;
		}

		String boContents = req.getParameter("bo_contents");
		if (boContents != null && !"".equals(boContents)) {
			if ("x".equals(boContents) || "/".equals(boContents) || "-".equals(boContents)) {
				boContents = null;
			}
			String material_id = req.getParameter("material_id");
			String occur_times = req.getParameter("occur_times");
			Integer iOcccrTimes = 1;
			try {
				iOcccrTimes = Integer.parseInt(occur_times);
			} catch(Exception e ) {
			}
			MaterialPartialEntity entity= new MaterialPartialEntity();
			entity.setMaterial_id(material_id);
			entity.setOccur_times(iOcccrTimes);
			entity.setBo_contents(boContents);
			mapper.updateMaterialPartialBoContent(entity);
		}

		String content = "";
		for (MaterialPartialDetailEntity parital : paritals) {
			if (parital.getArrival_plan_date().equals(parital.getHistory_limit_date()))
				continue;
			if (parital.getHistory_limit_date() != null) {
				String orgArrivalPlanDate = dc.getAsString(parital.getHistory_limit_date());
				if ("9999/12/31".equals(orgArrivalPlanDate)) orgArrivalPlanDate = "(未定)";
				content += "零件" + parital.getCode() + "入库预定日由" + orgArrivalPlanDate 
						+ "更改为" + dc.getAsString(parital.getArrival_plan_date()) + "。\n";
			}
			if (isManager) {
				parital.setR_operator_id(logindata.getOperator_id());
			}
			// 更新
			mapper.updatePartialArrivalPlanDateByKey(parital);
		}

		if (paritals.size() == 0) return conflexError;

		// 更新维修对象的入库预定日
		mapper.updateArrivePlanDateBoOnPartial(req.getParameter("material_id"));

		// 发送人设定为登录用户
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		content = user.getName() + " 修改了" + req.getParameter("sorc_no") + "的\n" + content;
		if (content.length() > 180) {
			content = content.substring(0, 180) + "……，请确认详细状况！";
		} else {
			content += "，请确认详细状况！";
		}
		// 信息推送
		PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
		PostMessageEntity pmEntity = new PostMessageEntity();
		pmEntity.setSender_id(user.getOperator_id());
		pmEntity.setContent(content);
		pmEntity.setLevel(1);
		pmEntity.setReason(PostMessageService.ARRIVAL_PLAN_DATE_CHANGED);

		pmMapper.createPostMessage(pmEntity);

		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String lastInsertID = commonMapper.getLastInsertID();
		pmEntity.setPost_message_id(lastInsertID);

		// TODO
		List<String> systemmers = new ArrayList<String>();
		systemmers.add("00000000129");
		systemmers.add("00000000010");
		systemmers.add("00000000011");
		systemmers.add("00000000009");
		systemmers.add("00000000065");
		systemmers.add("00000000070");
		systemmers.add("00000000063");

		for (String systemmer : systemmers) {
			if (systemmer.equals(user.getOperator_id())) continue;
			pmEntity.setReceiver_id(systemmer);
			pmMapper.createPostMessageSendation(pmEntity);
		}

		return null;
	}

	public void updateBoFlgWithDetail(MaterialPartialEntity materialPartialEntity, SqlSessionManager conn) throws Exception {

		MaterialPartialMapper mapper = conn.getMapper(MaterialPartialMapper.class);
		mapper.updateBoFlgWithDetail(materialPartialEntity);

	}

	public void updateBoFlgWithDetailMaintance(MaterialPartialEntity materialPartialEntity, SqlSessionManager conn) throws Exception {
		MaterialPartialMapper mapper = conn.getMapper(MaterialPartialMapper.class);
		mapper.updateBoFlgWithDetailMantains(materialPartialEntity);
	}
}

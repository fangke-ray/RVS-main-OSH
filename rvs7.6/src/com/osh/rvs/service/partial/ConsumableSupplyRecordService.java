package com.osh.rvs.service.partial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.partial.ConsumableSupplyRecordEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.partial.ConsumableSupplyRecordForm;
import com.osh.rvs.mapper.partial.ConsumableSupplyRecordMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * @Description: 消耗品发放记录
 * @author liuxb
 * @date 2018-5-18 上午9:32:02
 */
public class ConsumableSupplyRecordService {
	public List<ConsumableSupplyRecordForm> search(ActionForm form,SqlSession conn,HttpServletRequest req)throws Exception{
		ConsumableSupplyRecordEntity entity = new ConsumableSupplyRecordEntity();
		
		// 拷贝表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		List<ConsumableSupplyRecordForm> restList = new ArrayList<ConsumableSupplyRecordForm>();
		
		ConsumableSupplyRecordMapper dao = conn.getMapper(ConsumableSupplyRecordMapper.class);
		
		List<ConsumableSupplyRecordEntity> list = new ArrayList<ConsumableSupplyRecordEntity>();
		
		// 不选
		if(entity.getApply_method() == null){
			list = dao.search(entity);
			List<ConsumableSupplyRecordEntity> list2 = dao.searchConsumableSubstitute(entity);
			list.addAll(list2);
		}else if(entity.getApply_method() == 5){ //替代
			list = dao.searchConsumableSubstitute(entity);
		}else{
			list = dao.search(entity);
		}
		
		if(list!=null && list.size() >0){
			BeanUtil.copyToFormList(list, restList, CopyOptions.COPYOPTIONS_NOEMPTY, ConsumableSupplyRecordForm.class);
			
			// 将查询结果放入Session中,用于下载
			req.getSession().setAttribute("consumableSupplyRecordList", list);
		}
		
		return restList;
	}

	/**
	 * 下载
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String createConsumableSupplyRecord(HttpServletRequest request) throws Exception {
		@SuppressWarnings("unchecked")
		List<ConsumableSupplyRecordEntity> list=(List<ConsumableSupplyRecordEntity>)request.getSession().getAttribute("consumableSupplyRecordList");
		
		String cacheName ="消耗品发放记录一览" + new Date().getTime() + ".xlsx";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		if(list != null){
			OutputStream out = null;
			
			try {
				// 定义文件
				File file = new File(cachePath);
				
				// 判断文件目录是否存在
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				
				// 文件不存在
				if(!file.exists()){
					file.createNewFile();
				}
				
				// 创建Excel
				SXSSFWorkbook work = new SXSSFWorkbook(1000);
				
				// 创建Sheet
				Sheet sheet = work.createSheet("消耗品发放记录一览");
				
				Row row = null;
				
				// 字体
				Font font = work.createFont();
				font.setFontHeightInPoints((short) 10);
				font.setFontName("微软雅黑");
				
				// 加粗字体
				Font titlefont = work.createFont();
				titlefont.setFontHeightInPoints((short)11);
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
				
				// 右对齐
				CellStyle rightStyle = work.createCellStyle();
				rightStyle.cloneStyleFrom(cellStyle);
				rightStyle.setAlignment(CellStyle.ALIGN_RIGHT);
				
				// 标题
				CellStyle titleStyle = work.createCellStyle();
				titleStyle.cloneStyleFrom(centerStyle);
				titleStyle.setFont(titlefont);
				
				Map<String,Integer> map = new LinkedHashMap<String,Integer>();
				map.put("发放方式", 256*10);
				map.put("申请单编号", 256*12);
				map.put("课室", 256*10);
				map.put("工程", 256*10);
				map.put("工位代码", 256*10);
				map.put("申请日期", 256*11);
				map.put("修理单号", 256*10);
				map.put("申请理由", 256*20);
				map.put("发放者", 256*10);
				map.put("发放日期", 256*11);
				map.put("零件编码", 256*10);
				map.put("零件名称", 256*30);
				map.put("消耗品分类", 256*10);
				map.put("价格", 256*10);
				map.put("申请者", 256*10);
				map.put("申请数量", 256*10);
				map.put("发放数量", 256*10);
				map.put("总价", 256*10);
				map.put("基准库存", 256*10);
				map.put("安全库存", 256*10);
				map.put("库位编号", 256*12);
				
				//设置标题
				row = sheet.createRow(0);
				row.setHeightInPoints((short)18);
				
				int icol = 0;
				for(String title:map.keySet()){
					CellUtil.createCell(row, icol, title, titleStyle);
					
					// 设置列宽
					sheet.setColumnWidth(icol,map.get(title));
					
					icol ++;
				}
				
				sheet.createFreezePane(0, 1, 0, 1);
				
				ConsumableSupplyRecordEntity entity = null;
				for(int i = 0;i < list.size();i++){
					icol= 0;
					entity = list.get(i);
					
					row = sheet.createRow(i + 1);
					
					// 申请方式
					if(entity.getApply_method() == 5){
						CellUtil.createCell(row, icol++, "替代", cellStyle);
					}else{
						CellUtil.createCell(row, icol++, CodeListUtils.getValue("consumable_apply_method", entity.getApply_method().toString()), cellStyle);
					}
					
					// 申请单编号
					CellUtil.createCell(row, icol++, entity.getApplication_no(), cellStyle);
					
					// 课室
					CellUtil.createCell(row, icol++, entity.getSection_name(), cellStyle);
					
					// 工程
					CellUtil.createCell(row, icol++, entity.getLine_name(), cellStyle);
					
					// 工位代码
					CellUtil.createCell(row, icol++, entity.getProcess_code(), centerStyle);
					
					// 申请日期
					CellUtil.createCell(row, icol++, DateUtil.toString(entity.getApply_time(),DateUtil.DATE_PATTERN), centerStyle);
					
					// 修理单号
					CellUtil.createCell(row, icol++, entity.getOmr_notifi_no(), cellStyle);
					
					// 申请理由
					CellUtil.createCell(row, icol++, entity.getApply_reason(), cellStyle);
					
					// 发放者
					CellUtil.createCell(row, icol++, entity.getSupplier_name(), cellStyle);					
					
					// 发放时间
					CellUtil.createCell(row, icol++, DateUtil.toString(entity.getSupply_time(),DateUtil.DATE_PATTERN), centerStyle);
					
					// 零件编码
					CellUtil.createCell(row, icol++, entity.getPartial_code(), cellStyle);
					
					// 零件名称
					CellUtil.createCell(row, icol++, entity.getPartial_name(), cellStyle);
					
					// 消耗品分类
					CellUtil.createCell(row, icol++, CodeListUtils.getValue("consumable_type", entity.getType().toString()), cellStyle);
					
					// 价格
					if(entity.getPrice() != null){
						CellUtil.createCell(row, icol++, entity.getPrice().toString(), rightStyle);
					}else{
						CellUtil.createCell(row, icol++, null, rightStyle);
					}
					
					// 申请者
					CellUtil.createCell(row, icol++, entity.getPetitioner_name(), cellStyle);
					
					// 申请数量
					if( entity.getApply_quantity() != null){
						CellUtil.createCell(row, icol++, entity.getApply_quantity().toString(), rightStyle);
					}else{
						CellUtil.createCell(row, icol++, null, rightStyle);
					}
					
					// 发放数量
					CellUtil.createCell(row, icol++, entity.getSupply_quantity().toString(), rightStyle);
					
					// 总价
					if(entity.getTotal_price() != null){
						CellUtil.createCell(row, icol++, entity.getTotal_price().toString(), rightStyle);
					}else{
						CellUtil.createCell(row, icol++, null, rightStyle);
					}
					
					// 基准库存
					CellUtil.createCell(row, icol++, entity.getBenchmark().toString(), rightStyle);
					
					// 安全库存
					CellUtil.createCell(row, icol++, entity.getSafety_lever().toString(), rightStyle);
					
					// 库位编号
					CellUtil.createCell(row, icol++, entity.getStock_code(), cellStyle);
				}
				
				out = new FileOutputStream(file);
				work.write(out);
				work.dispose();
			}catch (Exception e) {
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
	
	
	/**
	 * @param form
	 * @param request
	 * @param conn
	 * @param isContinue 
	 * @param errors
	 */
	public void consumableTopTenValidate(ActionForm form,HttpServletRequest request, SqlSession conn, String isContinue, List<MsgInfo> errors)throws Exception {
		ConsumableSupplyRecordMapper dao = conn.getMapper(ConsumableSupplyRecordMapper.class);

		ConsumableSupplyRecordEntity connd = new ConsumableSupplyRecordEntity();

		// 拷贝表单数据到对象
		BeanUtil.copyToBean(form, connd, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 获取产出台数
		Integer outLineQuantity = dao.getOutLineQuantity(connd);

		// 判断产出台数
		if(outLineQuantity == null || outLineQuantity == 0){
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("info.material.outline.empty");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.material.outline.empty"));
			errors.add(msgInfo);
			return;
		}

		// 取得消耗数量前十
		connd.setOrder_by("supply_quantity");
		List<ConsumableSupplyRecordEntity> quantityList = dao.searchConsumableTopTen(connd);

		// 取得总价前十
		connd.setOrder_by("total_price");
		List<ConsumableSupplyRecordEntity> priceList = dao.searchConsumableTopTen(connd);

		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		if(user.getPrivacies().contains(RvsConsts.PRIVACY_FACT_MATERIAL) || user.getPrivacies().contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)){
			// 存储消耗目标不存在的消耗品信息
			Map<String,ConsumableSupplyRecordEntity> map = new HashMap<String,ConsumableSupplyRecordEntity>();

			for(ConsumableSupplyRecordEntity entity:quantityList){
				// 消耗目标不存在
				if(entity.getConsumpt_quota()==null){

					// 平均消耗量
					BigDecimal averageSupplyQuantity = new BigDecimal(entity.getSupply_quantity()).divide(new BigDecimal(outLineQuantity), 2, BigDecimal.ROUND_HALF_UP);

					entity.setAverage_supply_quantity(averageSupplyQuantity);
					map.put(entity.getPartial_id(), entity);
				}
			}

			for(ConsumableSupplyRecordEntity entity:priceList){
				// 消耗目标不存在
				if(entity.getConsumpt_quota()==null){
					if(!map.containsKey(entity.getPartial_id())){

						BigDecimal averageSupplyQuantity = new BigDecimal(entity.getSupply_quantity()).divide(new BigDecimal(outLineQuantity),2, BigDecimal.ROUND_HALF_UP);

						entity.setAverage_supply_quantity(averageSupplyQuantity);
						map.put(entity.getPartial_id(), entity);
					}
				}
			}

			// 消耗目标不存在的消耗品信息
			List<ConsumableSupplyRecordEntity> consumptQuotaEmptyList = new ArrayList<ConsumableSupplyRecordEntity>();

			if (isContinue == null) {
				for(String key:map.keySet()){
					consumptQuotaEmptyList.add(map.get(key));
				}
			}

			request.setAttribute("consumptQuotaEmptyList", consumptQuotaEmptyList);
		}

		request.setAttribute("outLineQuantity", outLineQuantity);
		request.setAttribute("quantityList", quantityList);
		request.setAttribute("priceList", priceList);
	}

	/**
	 * 消耗量Top10导出
	 * @param form 表单
	 * @param outLineQuantity 产出台数
	 * @param map 
	 * @return
	 * @throws Exception
	 */
	public String createConsumableTopTen(ConsumableSupplyRecordForm form,HttpServletRequest request)throws Exception {
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "consumable_supply_top_ten.xlsx";
		String cacheName ="消耗量Top10" + new Date().getTime() + ".xlsx";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		OutputStream out = null;
		InputStream in = null;
		
		try{
			try {
				FileUtils.copyFile(new File(path), new File(cachePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			in = new FileInputStream(cachePath);//读取文件 
			
			XSSFWorkbook work=new XSSFWorkbook(in);//创建xls文件
			
		    Sheet sheet=work.getSheetAt(0);//取得第一个Sheet
			
			Row row = null;
			
			Cell cell = null;
			
			XSSFFont font = work.createFont();
			font.setFontHeightInPoints((short) 9);
			font.setFontName("微软雅黑");
			
			XSSFFont titlefont=work.createFont();
			titlefont.setFontHeightInPoints((short)11);
			titlefont.setFontName("微软雅黑");
			titlefont.setColor(HSSFColor.WHITE.index);
			
			CellStyle cellStyle = work.createCellStyle();
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyle.setBorderTop(CellStyle.BORDER_THIN);
			cellStyle.setBorderRight(CellStyle.BORDER_THIN);
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			cellStyle.setFont(font);

			CellStyle currencyStyle = work.createCellStyle();
			currencyStyle.setBorderLeft(CellStyle.BORDER_THIN);
			currencyStyle.setBorderTop(CellStyle.BORDER_THIN);
			currencyStyle.setBorderRight(CellStyle.BORDER_THIN);
			currencyStyle.setBorderBottom(CellStyle.BORDER_THIN);
			currencyStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			currencyStyle.setFont(font);
			currencyStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			XSSFDataFormat currencyFormat=work.createDataFormat();
			currencyStyle.setDataFormat(currencyFormat.getFormat("$ #,##0.00"));

			CellStyle titleStyle = work.createCellStyle();
			titleStyle.cloneStyleFrom(cellStyle);
			titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			titleStyle.setFillForegroundColor(HSSFColor.GREEN.index);
			titleStyle.setFont(titlefont);
			titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
			
			Calendar cla = Calendar.getInstance();
			
			// 消耗品分类
			String types = form.getTypes();
			
			// 选择了一种消耗品分类
			if(!CommonStringUtil.isEmpty(types) && !types.contains(",")){
				sheet.getRow(8).getCell(CellReference.convertColStringToIndex("J")).setCellValue(CodeListUtils.getValue("consumable_type", types) + "消耗量 TOP10");
			}
			
			// 查询范围
			sheet.getRow(19).getCell(CellReference.convertColStringToIndex("N")).setCellValue(form.getSupply_time_start()+" ～ "+form.getSupply_time_end());
			
			// 文件出列日期
			sheet.getRow(25).getCell(CellReference.convertColStringToIndex("AD")).setCellValue(DateUtil.toString(cla.getTime(), "YYYY年MM月dd日"));
			
			// 检索条件
			
			// 发放时间
			row = sheet.createRow(31);
			cell = row.createCell(CellReference.convertColStringToIndex("A"));
			cell.setCellValue("发放期间");
			cell.setCellStyle(titleStyle);
			sheet.addMergedRegion(CellRangeAddress.valueOf("A32:C32"));
			
			row.createCell(CellReference.convertColStringToIndex("D")).setCellValue(sheet.getRow(19).getCell(CellReference.convertColStringToIndex("N")).getStringCellValue());
			sheet.addMergedRegion(CellRangeAddress.valueOf("D32:K32"));
			
			// 发放方式
			cell = row.createCell(CellReference.convertColStringToIndex("L"));
			cell.setCellValue("发放方式");
			cell.setCellStyle(titleStyle);
			sheet.addMergedRegion(CellRangeAddress.valueOf("L32:N32"));
			
			if(CommonStringUtil.isEmpty(form.getApply_method())){
				row.createCell(CellReference.convertColStringToIndex("O")).setCellValue("全部");
			}else if("5".equals(form.getApply_method())){
				row.createCell(CellReference.convertColStringToIndex("O")).setCellValue("替代");
			}else{
				row.createCell(CellReference.convertColStringToIndex("O")).setCellValue(CodeListUtils.getValue("consumable_apply_method", form.getApply_method()));
			}
			sheet.addMergedRegion(CellRangeAddress.valueOf("O32:P32"));
			
			// 消耗品分类
			cell = row.createCell(CellReference.convertColStringToIndex("Q"));
			cell.setCellValue("消耗品分类");
			cell.setCellStyle(titleStyle);
			sheet.addMergedRegion(CellRangeAddress.valueOf("Q32:T32"));
			
			// 消耗品分类没有选择
			if(CommonStringUtil.isEmpty(types)){
				row.createCell(CellReference.convertColStringToIndex("U")).setCellValue("全部");
			}else{
				// 以逗号分隔
				String [] arrTypes = types.split(",");
				List<String> typeList = new ArrayList<String>();

				for (String type : arrTypes) {
					// 分类名称
					typeList.add(CodeListUtils.getValue("consumable_type",type));
				}

				// 消耗品分类文本内容
				String typeContent = CommonStringUtil.joinBy("；", typeList.toArray(new String[typeList.size()]));

				row.createCell(CellReference.convertColStringToIndex("U")).setCellValue(typeContent);
			}
			sheet.addMergedRegion(CellRangeAddress.valueOf("U32:AL32"));
			
			// 产出台数
			row = sheet.createRow(32);
			cell = row.createCell(CellReference.convertColStringToIndex("A"));
			cell.setCellValue("期间内产出台数");
			cell.setCellStyle(titleStyle);
			sheet.addMergedRegion(CellRangeAddress.valueOf("A33:E33"));
			
			Integer outLineQuantity = (Integer)request.getAttribute("outLineQuantity");
			row.createCell(CellReference.convertColStringToIndex("F")).setCellValue(outLineQuantity);
			sheet.addMergedRegion(CellRangeAddress.valueOf("F33:K33"));
			
			// 取得Sheet中所有合并单元格
			int numMergedRegions =sheet.getNumMergedRegions();
			for(int i = 0;i < numMergedRegions;i++){
				int firstRow = sheet.getMergedRegion(i).getFirstRow();
				if(firstRow == 31 || firstRow == 32){
					// 设置合并单元格的边框
					RegionUtil.setBorderTop(1, sheet.getMergedRegion(i), sheet, work);
					RegionUtil.setBorderRight(1, sheet.getMergedRegion(i), sheet, work);
					RegionUtil.setBorderBottom(1, sheet.getMergedRegion(i), sheet, work);
					RegionUtil.setBorderLeft(1, sheet.getMergedRegion(i), sheet, work);
				}
			}
			
			// 数量
			@SuppressWarnings("unchecked")
			List<ConsumableSupplyRecordEntity> quantityList = (List<ConsumableSupplyRecordEntity>)request.getAttribute("quantityList");
			for(int i = 0;i < quantityList.size();i++){
				ConsumableSupplyRecordEntity entity = quantityList.get(i);
				// 品名
				sheet.getRow(37 + i).getCell(CellReference.convertColStringToIndex("Z")).setCellValue(entity.getPartial_code());
				
				// 消耗量
				sheet.getRow(37 + i).getCell(CellReference.convertColStringToIndex("AC")).setCellValue(entity.getSupply_quantity());
				
				// 平均消耗量（消耗量/ 产出台数）
				BigDecimal averageSupplyQuantity = new BigDecimal(entity.getSupply_quantity()).divide(new BigDecimal(outLineQuantity), 2, BigDecimal.ROUND_HALF_UP);
				sheet.getRow(37 + i).getCell(CellReference.convertColStringToIndex("AG")).setCellValue(averageSupplyQuantity.doubleValue());
				
				// 目标值
				if (entity.getConsumpt_quota() != null)
					sheet.getRow(37 + i).getCell(CellReference.convertColStringToIndex("AJ")).setCellValue(entity.getConsumpt_quota().doubleValue());
			}
			
			// 总价
			@SuppressWarnings("unchecked")
			List<ConsumableSupplyRecordEntity> priceList = (List<ConsumableSupplyRecordEntity>)request.getAttribute("priceList");
			for(int i = 0;i < priceList.size();i++){
				ConsumableSupplyRecordEntity entity = priceList.get(i);
				
				// 品名
				sheet.getRow(51 + i).getCell(CellReference.convertColStringToIndex("Z")).setCellValue(entity.getPartial_code());
				
				// 消耗总价
				if (entity.getTotal_price() != null) {
					cell = sheet.getRow(51 + i).getCell(CellReference.convertColStringToIndex("AC"));
					cell.setCellValue(entity.getTotal_price().doubleValue());
					cell.setCellStyle(currencyStyle);
				}
				
				// 平均消耗总价（消耗总价/ 产出台数）
				if (entity.getTotal_price() != null) {
					BigDecimal averageTotalPrice = entity.getTotal_price().divide(new BigDecimal(outLineQuantity), 2, BigDecimal.ROUND_HALF_UP);
					cell = sheet.getRow(51 + i).getCell(CellReference.convertColStringToIndex("AG"));
					cell.setCellValue(averageTotalPrice.doubleValue());
					cell.setCellStyle(currencyStyle);
				}
				
				// 目标值(单价 * 目标值)
				if (entity.getConsumpt_quota() != null && entity.getPrice() != null) {
					cell = sheet.getRow(51 + i).getCell(CellReference.convertColStringToIndex("AJ"));
					cell.setCellValue(entity.getPrice().multiply(entity.getConsumpt_quota()).doubleValue());
					cell.setCellStyle(currencyStyle);
				}
			}
			
			out= new FileOutputStream(cachePath);
			work.write(out);
		}catch (Exception e) {
			throw e;
		}finally{
			if(in!=null){
				in.close();
			}
			if(out!=null){
				out.close();
			}
		}
		
		return cacheName;
	}
}
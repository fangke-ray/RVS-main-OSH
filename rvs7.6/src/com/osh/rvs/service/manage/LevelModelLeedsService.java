package com.osh.rvs.service.manage;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.manage.ModelLevelSetEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.manage.ModelLevelSetForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.manage.LevelModelLeedsMapper;
import com.osh.rvs.mapper.manage.UserDefineCodesMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

/**
 * 型号等级拉动台数设置
 * 
 * @author lxb
 * 
 */
public class LevelModelLeedsService {
	/**
	 * 型号等级拉动台数一览
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<ModelLevelSetForm> searchModelLevelSet(ActionForm form, HttpServletRequest request,SqlSession conn) {
		ModelLevelSetForm modelLevelSetForm = (ModelLevelSetForm) form;
		ModelLevelSetEntity entity = new ModelLevelSetEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(modelLevelSetForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LevelModelLeedsMapper dao = conn.getMapper(LevelModelLeedsMapper.class);
		List<ModelLevelSetEntity> sEneityList = dao.searchModelLevelSet(entity);
		request.getSession().setAttribute("result", sEneityList);

		List<ModelLevelSetForm> responseFormList = new ArrayList<ModelLevelSetForm>();
		// 复制数据到表单
		BeanUtil.copyToFormList(sEneityList, responseFormList, null, ModelLevelSetForm.class);
		if (responseFormList.size() > 0) {
			return responseFormList;
		} else {
			return null;
		}
	}

	/**
	 * 模拟拉动
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<ModelLevelSetForm> searchLeed(ActionForm form, SqlSession conn) {
		ModelLevelSetForm modelLevelSetForm = (ModelLevelSetForm) form;
		ModelLevelSetEntity entity = new ModelLevelSetEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(modelLevelSetForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LevelModelLeedsMapper dao = conn.getMapper(LevelModelLeedsMapper.class);
		List<ModelLevelSetEntity> sEneityList = dao.searchLeed(entity);

		List<ModelLevelSetForm> responseFormList = new ArrayList<ModelLevelSetForm>();
		// 复制数据到表单
		BeanUtil.copyToFormList(sEneityList, responseFormList, null, ModelLevelSetForm.class);
		if (responseFormList.size() > 0) {
			return responseFormList;
		} else {
			return null;
		}
	}
	
	
	public Integer getCoverage(ActionForm form, SqlSession conn){
		ModelLevelSetForm modelLevelSetForm = (ModelLevelSetForm) form;
		ModelLevelSetEntity entity = new ModelLevelSetEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(modelLevelSetForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		LevelModelLeedsMapper dao = conn.getMapper(LevelModelLeedsMapper.class);
		ModelLevelSetEntity temp = dao.getModelLevelSet(entity);
		Integer echelon = entity.getEchelon();//梯队
		
		Integer coverage = null;
		
		if(temp!=null && temp.getCoverage()!=null){
			coverage = temp.getCoverage();
		}else if(echelon!=null){
			//根据对应梯队查找用户定义覆盖率
			
			String code = "";
			if(echelon == 1){
				code = "FIRST_ECHELON_COVERAGE";
			}else if(echelon == 2){
				code = "SECOND_ECHELON_COVERAGE";
			}else if(echelon == 3){
				code = "THIRD_ECHELON_COVERAGE";
			}

			UserDefineCodesMapper userDefineCodesMapper = conn.getMapper(UserDefineCodesMapper.class);
			coverage = Integer.valueOf(userDefineCodesMapper.searchUserDefineCodesValueByCode(code));
		}
		
		
		return coverage;
		
		
	}
	
	/**
	 * 拉动台数计算结果
	 * @param list
	 * @return
	 */
	private int getMaxForecastResult(List<ModelLevelSetForm> list,Integer coverage,Double coefficientOfVariation){
		Double max = 0.0;
		
		if(list == null || list.size() == 0) return 0;
		
		for(ModelLevelSetForm form:list){
			if(!CommonStringUtil.isEmpty(form.getOrder_count_of_period())){
				Double order_count_of_period = Double.valueOf(form.getOrder_count_of_period());
				if(order_count_of_period > max) max = order_count_of_period;
			}
		}
		
		//覆盖率百分比
		BigDecimal coveragePercentage = BigDecimal.valueOf(coverage.doubleValue()).divide(BigDecimal.valueOf(100));
		//有效波动系数 (波动系数-1) * 覆盖率% + 1
		BigDecimal effective_coefficient_of_variation = BigDecimal.valueOf(coefficientOfVariation.doubleValue()).subtract(BigDecimal.valueOf(1)).multiply(coveragePercentage).add(BigDecimal.valueOf(1));
		double coefficient = effective_coefficient_of_variation.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		//拉动台数(周期修理订购量 * 波动系数)
		BigDecimal forecast_result = BigDecimal.valueOf(max).multiply(BigDecimal.valueOf(coefficient));
		
		return forecast_result.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		
	}

	
	/**
	 * 波动系数
	 * @param form
	 * @param conn
	 * @return
	 */
	private Double getCoefficientOfVariation(ActionForm form, SqlSession conn){
		ModelLevelSetForm modelLevelSetForm = (ModelLevelSetForm) form;
		ModelLevelSetEntity entity = new ModelLevelSetEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(modelLevelSetForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		LevelModelLeedsMapper dao = conn.getMapper(LevelModelLeedsMapper.class);
		Double cefficientOfVariation=dao.getCoefficientOfVariation(entity);
		
		if (cefficientOfVariation != null) {
			return cefficientOfVariation;
		} else {
			return null;
		}
		
	}

	/**
	 * 拉动台数计算
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public void calculate(ActionForm form, List<ModelLevelSetForm> list,Map<String, Object> listResponse,SqlSession conn) {
		
		if(list.size() == 0) return;
		
		ModelLevelSetForm modelLevelSetForm = (ModelLevelSetForm) form;
		ModelLevelSetEntity entity = new ModelLevelSetEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(modelLevelSetForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		LevelModelLeedsMapper levelModelLeedsMapper = conn.getMapper(LevelModelLeedsMapper.class);
		ModelLevelSetEntity modelLevelSetEntity = levelModelLeedsMapper.getModelLevelSet(entity);
		//零件订购到货周期
		double arrival_period = 1.5;
		//种类
		int kind = modelLevelSetEntity.getKind();
		
		if(kind == 6){//ENDOEYE
			arrival_period = 2.0;
		}
		
		MaterialMapper materialMapper = conn.getMapper(MaterialMapper.class);
		
		for(int i = 0;i<list.size();i++){
			ModelLevelSetForm temp = list.get(i);
			MaterialEntity materialEntity = new MaterialEntity();
			BeanUtil.copyToBean(temp, materialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
			
			materialEntity.setReception_time_start(DateUtil.toDate(temp.getStart_date(),DateUtil.DATE_PATTERN));
			materialEntity.setReception_time_end(DateUtil.toDate(temp.getEnd_date(),DateUtil.DATE_PATTERN));
			
			Integer receptCount = materialMapper.getReceptCount(materialEntity);// 受理数
			Integer agreeCount = materialMapper.getAgreeCount(materialEntity);// 同意数
			list.get(i).setSampling_recept_count(receptCount.toString());
			list.get(i).setSampling_agree_count(agreeCount.toString());
			
			//周数
			int weeks = getWeeksOfDateDiff(temp);
			
			//周期修理订购量(同意数量 / (周数 * 零件订购到货周期))
			BigDecimal order_count_of_period = BigDecimal.valueOf(agreeCount.doubleValue()).divide(BigDecimal.valueOf(weeks * arrival_period), 1, BigDecimal.ROUND_HALF_UP);
			list.get(i).setOrder_count_of_period(order_count_of_period.toString());
		}
		
		listResponse.put("responseFormList", list);
		
		
		//覆盖率
		Integer coverage = entity.getCoverage();
		if(coverage == null){
			//根据对应梯队查找用户定义覆盖率
			Integer echelon = entity.getEchelon();//梯队
			String code = "";
			if(echelon == 1){
				code = "FIRST_ECHELON_COVERAGE";
			}else if(echelon == 2){
				code = "SECOND_ECHELON_COVERAGE";
			}else if(echelon == 3){
				code = "THIRD_ECHELON_COVERAGE";
			}

			UserDefineCodesMapper userDefineCodesMapper = conn.getMapper(UserDefineCodesMapper.class);
			coverage = Integer.valueOf(userDefineCodesMapper.searchUserDefineCodesValueByCode(code));
		}
		
		
		Integer forecastResult = this.getMaxForecastResult(list,coverage,entity.getCoefficient_of_variation().doubleValue());
		listResponse.put("forecastResult", forecastResult);
	}

	
	/**
	 * 计算两个日期之间相差周数
	 * @param entity
	 * @return
	 */
	private int getWeeksOfDateDiff(ModelLevelSetForm form){
		ModelLevelSetEntity entity = new ModelLevelSetEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		Date start_date = entity.getStart_date(); //采样区间开始
		Date end_date = entity.getEnd_date(); //采样区间终了
		
		int dates = (int)((end_date.getTime()-start_date.getTime())/(1000*60*60*24)) + 1;
		int weeks = dates/7;
		
		return weeks;
	}
	
	
	public void detail(ActionForm form,Map<String, Object> listResponse,SqlSession conn){
		//图表
		listResponse.put("responseMap", this.getChartData(form, conn));
		
		//波动系数
		Double coefficientOfVariation= this.getCoefficientOfVariation(form, conn);
		listResponse.put("coefficientOfVariation", coefficientOfVariation);
		
		//覆盖率
		Integer coverage = this.getCoverage(form, conn);
		listResponse.put("coverage", coverage);
		
		List<ModelLevelSetForm> responseFormList=this.searchLeed(form, conn);
		listResponse.put("responseFormList", responseFormList);
		
		//拉动台数计算结果
		Integer forecastResult = this.getMaxForecastResult(responseFormList,coverage,coefficientOfVariation);
		listResponse.put("forecastResult", forecastResult);
		
	}
	
	
	
	/**
	 * 更新设置拉动台数
	 * 
	 * @param form
	 * @param conn
	 */
	public void updateForecastSetting(ActionForm form, SqlSessionManager conn) {
		ModelLevelSetForm modelLevelSetForm = (ModelLevelSetForm) form;
		ModelLevelSetEntity entity = new ModelLevelSetEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(modelLevelSetForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LevelModelLeedsMapper dao = conn.getMapper(LevelModelLeedsMapper.class);
		dao.updateForecastSetting(entity);
	}

	/**
	 * 图表
	 * 
	 * @param form
	 * @param calendar
	 * @param conn
	 * @return
	 */
	private Map<String, Object> getChartData(ActionForm form,SqlSession conn) {
		ModelLevelSetForm modelLevelSetForm = (ModelLevelSetForm) form;
		MaterialEntity materialEntity = new MaterialEntity();
		// 复制表单数据到到对象
		BeanUtil.copyToBean(modelLevelSetForm, materialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		ModelLevelSetEntity modelLevelSetEntity=new ModelLevelSetEntity();
		// 复制表单数据到到对象
		BeanUtil.copyToBean(modelLevelSetForm, modelLevelSetEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		Map<String, Object> tLines = new HashMap<String, Object>();

		List<String> axisTextList = new ArrayList<String>();// X轴
		List<Double> list_average_agree = new ArrayList<Double>();// 平均同意数
		List<Integer> list_forecast_setting = new ArrayList<Integer>();// 设置拉动台数

		MaterialMapper materialDao = conn.getMapper(MaterialMapper.class);
		LevelModelLeedsMapper levelModelLeedsDao=conn.getMapper(LevelModelLeedsMapper.class);
		
		for(int i=6;i>0;i--){
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.MONTH, -i);
			materialEntity.setAgreed_date_start(RvsUtils.getStartDate(cal.get(Calendar.YEAR)+"",(cal.get(Calendar.MONTH)+1)+""));
			materialEntity.setAgreed_date_end(RvsUtils.getEndDate(cal.get(Calendar.YEAR)+"",(cal.get(Calendar.MONTH)+1)+""));
			
			Double averageCount = materialDao.getAverageAgreeOfLevelModelLeeds(materialEntity);//平均同意数
			
			modelLevelSetEntity.setStart_date(RvsUtils.getStartDate(cal.get(Calendar.YEAR)+"",(cal.get(Calendar.MONTH)+1)+""));
			modelLevelSetEntity.setEnd_date(RvsUtils.getEndDate(cal.get(Calendar.YEAR)+"",(cal.get(Calendar.MONTH)+1)+""));
			Integer forecastResultCount=levelModelLeedsDao.getForecastResultSetCount(modelLevelSetEntity);//拉动台数

			list_average_agree.add(averageCount);
			list_forecast_setting.add(forecastResultCount);
			
			if(cal.get(Calendar.MONTH)==3){
				axisTextList.add(RvsUtils.getBussinessYearString(cal)+"<br>" + (cal.get(Calendar.MONTH)+1) +"月");
			}else {
				axisTextList.add(cal.get(Calendar.MONTH)+1+"月");
			}
			
		}
		
		tLines.put("list_average_agree", list_average_agree);
		tLines.put("list_forecast_setting", list_forecast_setting);
		tLines.put("axisTextList", axisTextList);
		return tLines;
	}
	
	/**
	 * 下载编辑拉动台数
	 * @param request
	 * @return
	 */
	public String dowloadForecastResultSet(HttpServletRequest request,SqlSession conn)throws Exception{
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "拉动台数编辑模板.xls";
		String cacheName ="拉动台数编辑" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		@SuppressWarnings("unchecked")
		List<ModelLevelSetEntity> sEneityList=(List<ModelLevelSetEntity>)request.getSession().getAttribute("result");
		LevelModelLeedsMapper dao = conn.getMapper(LevelModelLeedsMapper.class);
		MaterialMapper mDao=conn.getMapper(MaterialMapper.class);
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			return null;
		}
		
		OutputStream out = null;
		InputStream in = null;
		try{
			in = new FileInputStream(cachePath);//读取文件 
			HSSFWorkbook work=new HSSFWorkbook(in);//创建xls文件
			HSSFSheet sheet=work.getSheetAt(0);//取得第一个Sheet
			
			if(sEneityList!=null){
				HSSFRow row01 = sheet.getRow(0);
				ModelLevelSetEntity entity=null;
				entity=sEneityList.get(0);
				List<ModelLevelSetEntity> sEneityLists= dao.searchLeed(entity);
				int daysOfHalfYear=0;
				int daysOfThreeMonthAge=0;
				if(sEneityLists!=null && sEneityLists.size()==2){//设置表头
					SimpleDateFormat df = new SimpleDateFormat("MM");
					
					ModelLevelSetEntity temp01=sEneityLists.get(0);
					Calendar startDate01=Calendar.getInstance();
					startDate01.setTime(temp01.getStart_date());
					Calendar endDate01=Calendar.getInstance();
					endDate01.setTime(temp01.getEnd_date());
					
					daysOfHalfYear=getDaysOfBetweenTwoDate(temp01.getStart_date(), temp01.getEnd_date()); //半年天数
					
					String cellValue01=RvsUtils.getBussinessYearString(startDate01)
										+"-"+df.format(temp01.getStart_date())
										+"月～"
										+RvsUtils.getBussinessYearString(endDate01)
										+"-"+df.format(temp01.getEnd_date())
										+"月";
					row01.getCell(3).setCellValue(cellValue01);
					
					ModelLevelSetEntity temp02=sEneityLists.get(1);
					Calendar startDate02=Calendar.getInstance();
					startDate01.setTime(temp02.getStart_date());
					Calendar endDate02=Calendar.getInstance();
					endDate01.setTime(temp02.getEnd_date());
					
					daysOfThreeMonthAge=getDaysOfBetweenTwoDate(temp02.getStart_date(), temp02.getEnd_date()); //前三个月天数
					
					String cellValue02=RvsUtils.getBussinessYearString(startDate02)
										+"-"+df.format(temp02.getStart_date())
										+"月～"
										+RvsUtils.getBussinessYearString(endDate02)
										+"-"+df.format(temp02.getEnd_date())
										+"月";
					row01.getCell(7).setCellValue(cellValue02);
					
					Calendar cal=Calendar.getInstance();
					SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMdd_HH:mm");
					Date date=cal.getTime();
					String cellTime="("+ dfs.format(date)+"时点)";
					HSSFRow row02 = sheet.getRow(1);
					row02.getCell(2).setCellValue(cellTime);
				}
				
				int index=2;
				HSSFFont font=work.createFont();
				font.setFontHeightInPoints((short)10);
				font.setFontName("微软雅黑");
				
				HSSFCellStyle cellStylePercent=work.createCellStyle();//同意率单元格格式
				cellStylePercent.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
				cellStylePercent.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				cellStylePercent.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				cellStylePercent.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				cellStylePercent.setBorderRight(HSSFCellStyle.BORDER_THIN);
				cellStylePercent.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				cellStylePercent.setFont(font);
				
				HSSFCellStyle cellDigitStyle = work.createCellStyle();    
				cellDigitStyle.setDataFormat(work.createDataFormat().getFormat("0.00")); 
				cellDigitStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				cellDigitStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				cellDigitStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				cellDigitStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				cellDigitStyle.setFont(font);
				
				HSSFCellStyle calcResultStyle = work.createCellStyle();
				calcResultStyle.setDataFormat(work.createDataFormat().getFormat("0")); 
				calcResultStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				calcResultStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				calcResultStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				calcResultStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				calcResultStyle.setFont(font);
				
				HSSFCellStyle borderStyle = work.createCellStyle();
				borderStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
				borderStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
				borderStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				borderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				borderStyle.setFont(font);
				
				//下拉框
				String[] echelonList = {"", "第一梯队", "第二梯队", "第三梯队" };  
				DVConstraint constraint = DVConstraint.createExplicitListConstraint(echelonList);  
				
				for(int i=0;i<sEneityList.size();i++){
					entity=sEneityList.get(i);
					HSSFRow row=sheet.createRow(index);
					index++;
					row.createCell(0).setCellValue(i+1);//编号
					row.getCell(0).setCellStyle(borderStyle);
					
					String levelName= CodeListUtils.getValue("material_level", "" + entity.getLevel());//等级
					String modelName=entity.getModel_name();//型号
					row.createCell(1).setCellValue(levelName+modelName);//等级+型号
					row.getCell(1).setCellStyle(borderStyle);
					
					ModelLevelSetForm tempForm=new ModelLevelSetForm();
					BeanUtil.copyToForm(entity, tempForm, CopyOptions.COPYOPTIONS_NOEMPTY);
					MaterialEntity mEntity=new MaterialEntity();
					BeanUtil.copyToBean(tempForm, mEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
					Integer wip=mDao.getWipCount(mEntity);
					if(wip==null){
						wip=0;	
					}
					row.createCell(2).setCellValue(wip);//WIP
					row.getCell(2).setCellStyle(borderStyle);
					
					//前半年和前三月
					List<ModelLevelSetEntity> modelLevelList= dao.searchLeed(entity);
					ModelLevelSetEntity mTemp=null;
					Integer sampling_recept_count=null;//受理数
					Integer sampling_agree_count=null;//同意数
					if(modelLevelList!=null && modelLevelList.size()==2){
							//前半年
							mTemp=modelLevelList.get(0);
							String echelonName01=CodeListUtils.getValue("echelon_code", "" + entity.getEchelon());//梯队
							if(echelonName01==null || echelonName01==""){
								echelonName01="";
							}
							row.createCell(3).setCellValue(echelonName01);
							row.getCell(3).setCellStyle(borderStyle);
							
							sampling_recept_count=mTemp.getSampling_recept_count();
							if(sampling_recept_count==null){
								sampling_recept_count=0;
							}
							row.createCell(4).setCellValue(sampling_recept_count);//受理数
							row.getCell(4).setCellStyle(borderStyle);
							
							sampling_agree_count=mTemp.getSampling_agree_count();
							if(sampling_agree_count==null){
								sampling_agree_count=0;
							}
							row.createCell(5).setCellValue(sampling_agree_count);//同意数
							row.getCell(5).setCellStyle(borderStyle);
							
							row.createCell(6).setCellFormula("IF(E"+index+">0, F"+index+"/E"+index+", \" - \")");
							row.getCell(6).setCellStyle(cellStylePercent);
							
							//前三月
							mTemp=modelLevelList.get(1);
							String echelonName02=CodeListUtils.getValue("echelon_code", "" + entity.getEchelon());//梯队
							if(echelonName02==null || echelonName02==""){
								echelonName02="";
							}
							row.createCell(7).setCellValue(echelonName02);
							row.getCell(7).setCellStyle(borderStyle);
							
							sampling_recept_count=mTemp.getSampling_recept_count();
							if(sampling_recept_count==null){
								sampling_recept_count=0;
							}
							row.createCell(8).setCellValue(sampling_recept_count);//受理数
							row.getCell(8).setCellStyle(borderStyle);
							
							sampling_agree_count=mTemp.getSampling_agree_count();
							if(sampling_agree_count==null){
								sampling_agree_count=0;
							}
							row.createCell(9).setCellValue(sampling_agree_count);//同意数
							row.getCell(9).setCellStyle(borderStyle);
							
							row.createCell(10).setCellFormula("IF(I"+index+">0, J"+index+"/I"+index+", \" - \")");
							row.getCell(10).setCellStyle(cellStylePercent);
					}else{
						row.createCell(3).setCellStyle(borderStyle);
						
						row.createCell(4).setCellStyle(borderStyle);
						
						row.createCell(5).setCellStyle(borderStyle);
						
						row.createCell(6).setCellStyle(cellStylePercent);
						
						row.createCell(7).setCellStyle(borderStyle);
						
						row.createCell(8).setCellStyle(borderStyle);
						
						row.createCell(9).setCellStyle(borderStyle);
						
						row.createCell(10).setCellStyle(cellStylePercent);
					}
					
					Calendar calendar=Calendar.getInstance();
					//本月日期
					int month=calendar.get(Calendar.MONTH)+1;
					int year=calendar.get(Calendar.YEAR);
					Date startDate=RvsUtils.getStartDate(String.valueOf(year), String.valueOf(month));
					Date endDate=calendar.getTime();
					
					int daysOfCurrentMonth=getDaysOfBetweenTwoDate(startDate, endDate);//本月天数
					
					//上月日期
					calendar.add(calendar.get(Calendar.MONTH), -1);
					int beforeMonth=calendar.get(Calendar.MONTH);
					int beforeYear=calendar.get(Calendar.YEAR);
					Date beforeStartDate=RvsUtils.getStartDate(String.valueOf(beforeYear), String.valueOf(beforeMonth));
					Date beforeEndtDate=RvsUtils.getEndDate(String.valueOf(beforeYear), String.valueOf(beforeMonth));
					
					int daysOfBeforeMonth=getDaysOfBetweenTwoDate(beforeStartDate, beforeEndtDate);//上个月天数
					
					//上月
					mEntity.setReception_time_start(beforeStartDate);
					mEntity.setReception_time_end(beforeEndtDate);
					Integer beforeReceptCount=mDao.getReceptCount(mEntity);//上月受理数
					Integer beforeAgreeCount=mDao.getAgreeCount(mEntity);//上月同意数
					entity.setStart_date(beforeStartDate);
					entity.setEnd_date(beforeEndtDate);
					String beforeEchelonName=CodeListUtils.getValue("echleon_code", "" + dao.getEchelon(entity));//梯队
					row.createCell(11).setCellValue(beforeEchelonName);
					row.getCell(11).setCellStyle(borderStyle);
					if(beforeReceptCount==null){
						beforeReceptCount=0;
					}
					row.createCell(12).setCellValue(beforeReceptCount);
					row.getCell(12).setCellStyle(borderStyle);
					if(beforeAgreeCount==null){
						beforeAgreeCount=0;
					}
					row.createCell(13).setCellValue(beforeAgreeCount);
					row.getCell(13).setCellStyle(borderStyle);
						
					row.createCell(14).setCellFormula("IF(M"+index+">0, N"+index+"/M"+index+", \" - \")");
					row.getCell(14).setCellStyle(cellStylePercent);
					
					//本月
					Date currentStartDate=RvsUtils.getStartDate(String.valueOf(year), String.valueOf(month));
					Date currentEndtDate=RvsUtils.getEndDate(String.valueOf(year), String.valueOf(month));
					mEntity.setReception_time_start(startDate);
					mEntity.setReception_time_end(endDate);
					Integer currentReceptCount=mDao.getReceptCount(mEntity);//本月受理数
					Integer currentAgreeCount=mDao.getAgreeCount(mEntity);//本月同意数
					entity.setStart_date(currentStartDate);
					entity.setEnd_date(currentEndtDate);
					String currentEchelonName=CodeListUtils.getValue("echelon_code", "" + dao.getEchelon(entity));//梯队
					row.createCell(15).setCellValue(currentEchelonName);
					row.getCell(15).setCellStyle(borderStyle);
					if(currentReceptCount==null){
						currentReceptCount=0;
					}
					row.createCell(16).setCellValue(currentReceptCount);
					row.getCell(16).setCellStyle(borderStyle);
					if(currentAgreeCount==null){
						currentAgreeCount=0;
					}
					row.createCell(17).setCellValue(currentAgreeCount);
					row.getCell(17).setCellStyle(borderStyle);
					
					row.createCell(18).setCellFormula("IF(Q"+index+">0, R"+index+"/Q"+index+", \" - \")");
					row.getCell(18).setCellStyle(cellStylePercent);
					
					row.createCell(19).setCellFormula("C"+index+"*MAX(O"+index+",G"+index+",K"+index+",S"+index+")");//潜在修理同意量
					row.getCell(19).setCellStyle(cellDigitStyle);
					
					row.createCell(20).setCellFormula("MAX(F"+index+"/"+daysOfHalfYear+",J"+index+"/"+daysOfThreeMonthAge+",N"+index+"/"+daysOfBeforeMonth+",R"+index+"/"+daysOfCurrentMonth+")*8");//周期修理订购量
					row.getCell(20).setCellStyle(cellDigitStyle);
					
					Double coefficientOfVariation=dao.getCoefficientOfVariation(entity);
					if(coefficientOfVariation==null){
						coefficientOfVariation=0.0;
					}
					row.createCell(21).setCellValue(coefficientOfVariation);//波动系数
					row.getCell(21).setCellStyle(cellDigitStyle);
					
					row.createCell(22).setCellFormula("ROUNDUP(MAX(T"+index+":U"+index+")*V"+index+",0)");//拉动台数计算结果
					row.getCell(22).setCellStyle(calcResultStyle);
					

					Integer forecastSettingCount=dao.getForecastSetting(entity);//拉动台数当前设置
					if(forecastSettingCount==null){
						forecastSettingCount=0;
					}
					row.createCell(23).setCellValue(forecastSettingCount);
					row.getCell(23).setCellStyle(borderStyle);
					
					row.createCell(24).setCellValue("");
					row.getCell(24).setCellStyle(borderStyle);
					
					row.createCell(25).setCellStyle(borderStyle);
					CellRangeAddressList regions = new CellRangeAddressList(index-1, index-1, 25,25);
					HSSFDataValidation data_validation = new HSSFDataValidation(regions, constraint);
					sheet.addValidationData(data_validation); 
				}
			}
	
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
			//request.getSession().removeAttribute("result");
		}
		
		return cacheName;
	}
	
		/**
		 * 计算两个日期之间相差天数
		 * @param firstDate
		 * @param secondDate
		 * @return
		 */
	   public int getDaysOfBetweenTwoDate(Date firstDate,Date secondDate)   {  
		   int day =(int)((secondDate.getTime()-firstDate.getTime())/(24*60*60*1000)>0 ? (secondDate.getTime()-firstDate.getTime())/(24*60*60*1000):(firstDate.getTime()-secondDate.getTime())/(24*60*60*1000))+1;
	       return day;  
	   }  

}

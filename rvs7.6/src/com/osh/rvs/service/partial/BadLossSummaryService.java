package com.osh.rvs.service.partial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.osh.rvs.bean.partial.BadLossSummaryEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.BadLossSummaryForm;
import com.osh.rvs.mapper.partial.BadLossSummaryMapper;

import de.schlichtherle.io.FileInputStream;
import de.schlichtherle.io.FileOutputStream;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

public class BadLossSummaryService {
	/**
	 * 初始页面的数据(当年)
	 * @param conn
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	public List<BadLossSummaryForm> searchLossSummary(SqlSession conn, List<MsgInfo> errors) throws Exception {
		BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);
		List<BadLossSummaryForm> badLossSummaryForms = new ArrayList<BadLossSummaryForm>();

		List<BadLossSummaryEntity> list = new ArrayList<BadLossSummaryEntity>();
		// 本月
		String thMonth = DateUtil.toString(new Date(), "yyyy-MM-01");
		
		//本月
		Calendar calendar =Calendar.getInstance();
		int curMonth = calendar.get(Calendar.MONTH)+1;
		int curYear = calendar.get(Calendar.YEAR);
		
		//财年;例如147P
		String workPeriod = RvsUtils.getBussinessYearString(calendar);
		
		//年份;例如2014
		//int year = RvsUtils.getBussinessYearStringRever(workPeriod);
		
		boolean viewThMonth = false;

		//判断当前选择月的之前月数据是否已经存在
		BadLossSummaryEntity judgeisExistLossSummary = null;
		//插入新数据的条件
		BadLossSummaryEntity insertLossSummary = new BadLossSummaryEntity();
		int[] months = new int[]{4,5,6,7,8,9,10,11,12,1,2,3};
		
		for(int i = 0;i<months.length;i++){
			//判断当前月之前的月份数据是否已经存在
			judgeisExistLossSummary = dao.searchLossSummaryFromOfYear(workPeriod,months[i]+"");
			if(judgeisExistLossSummary == null){
				insertLossSummary.setYear(curYear);
				insertLossSummary.setWork_period(workPeriod);
				insertLossSummary.setMonth(months[i]);

				// TODO 死锁BUG
				dao.insertLossSummary(insertLossSummary);				
			}
			if(curMonth==months[i])
				break;
		}
		
		
		// 查询损金总计详细数据
		List<BadLossSummaryEntity> badLossSummaryEntities = dao.searchLossSummaryFrom(workPeriod);

		List<BadLossSummaryEntity> badLossSummaryOfOthersEntities = null;
		
		for (BadLossSummaryEntity badLossSummaryEntity : badLossSummaryEntities) {
			String year_month = "";
			// 如果月份是小于9月
			if (badLossSummaryEntity.getMonth() <= 9) {
				year_month = badLossSummaryEntity.getYear() + "-0" + badLossSummaryEntity.getMonth() + "-01";
			} else {
				year_month = badLossSummaryEntity.getYear() + "-" + badLossSummaryEntity.getMonth() + "-01";
			}

			if (year_month.equals(thMonth)){
				viewThMonth = true;
				break;
			}
			
			// 模块上线前的旧数据
			if (year_month.compareTo("2014-07-01") < 0)
				getOldData(badLossSummaryEntity);
			else {
				badLossSummaryOfOthersEntities = dao.searchLossSummaryOfBelongs(year_month);
	
				// 查询出保内返品不良的维修对象的损金金额
				BadLossSummaryEntity summaryEntitiesOfRepair = dao.searchLossSummaryOfRepair(year_month);
				BigDecimal loss_price;
				if (summaryEntitiesOfRepair == null) {
					loss_price = BigDecimal.ZERO;
				} else {
					loss_price = summaryEntitiesOfRepair.getLoss_price();
				}
				setSummary(badLossSummaryEntity, badLossSummaryOfOthersEntities, loss_price);
			}
			list.add(badLossSummaryEntity);
		}
		//获取当前月汇率
		BadLossSummaryEntity curE_u_settlement = null;
		if (viewThMonth) {
			badLossSummaryOfOthersEntities = dao.searchLossSummaryOfBelongs(thMonth);

			BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();
			
			//如果当前月的汇率不是空
			curE_u_settlement= dao.searchLossSummaryFromOfYear(workPeriod,curMonth+"");
			if(curE_u_settlement != null){
				badLossSummaryEntity.setE_u_settlement(curE_u_settlement.getE_u_settlement());
			}
			
			setSummary(badLossSummaryEntity, badLossSummaryOfOthersEntities, null);

			Calendar cal = Calendar.getInstance();

			badLossSummaryEntity.setMonth(cal.get(Calendar.MONTH) + 1);
			badLossSummaryEntity.setYear(cal.get(Calendar.YEAR));
			badLossSummaryEntity.setWork_period(RvsUtils.getBussinessYearString(cal));

			list.add(badLossSummaryEntity);
		}

		BeanUtil.copyToFormList(list, badLossSummaryForms, CopyOptions.COPYOPTIONS_NOEMPTY,
				BadLossSummaryForm.class);
		return badLossSummaryForms;
	}

	/**
	 * 点击当前操作月时--查询当前月和前几个月
	 * @param conn
	 * @param summaryEntity
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	public List<BadLossSummaryForm> searchLossSummary(SqlSession conn, BadLossSummaryEntity workPeriodMonthEntity,List<MsgInfo> errors) throws Exception {BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);
	List<BadLossSummaryForm> badLossSummaryForms = new ArrayList<BadLossSummaryForm>();

	//财年;例如147P
	//Calendar calendar =Calendar.getInstance();
	String workPeriod = workPeriodMonthEntity.getWork_period();
	
	// 查询所有月的损金总计详细数据
	List<BadLossSummaryEntity> badLossSummaryEntities = dao.searchLossSummaryFrom(workPeriod);

	List<BadLossSummaryEntity> badLossSummaryOfOthersEntities = null;
	
	List<BadLossSummaryEntity> list = new ArrayList<BadLossSummaryEntity>();
	
	// 本月
	String thMonth = DateUtil.toString(new Date(), "yyyy-MM-01");
	boolean viewThMonth = false;
	
	for (BadLossSummaryEntity badLossSummaryEntity : badLossSummaryEntities) {
		
		String year_month = "";
		// 如果月份是小于9月
		if (badLossSummaryEntity.getMonth() <= 9) {
			year_month = badLossSummaryEntity.getYear() + "-0" + badLossSummaryEntity.getMonth() + "-01";
		} else {
			year_month = badLossSummaryEntity.getYear() + "-" + badLossSummaryEntity.getMonth() + "-01";
		}
		//根据已经取出来的年月和本月做比较
		if (year_month.equals(thMonth))
			viewThMonth = true;

		// 模块上线前的旧数据
		if (year_month.compareTo("2014-07-01") < 0)
			getOldData(badLossSummaryEntity);
		else {
			badLossSummaryOfOthersEntities = dao.searchLossSummaryOfBelongs(year_month);

			// 查询出保内返品不良的维修对象的损金金额
			BadLossSummaryEntity summaryEntitiesOfRepair = dao.searchLossSummaryOfRepair(year_month);
			BigDecimal loss_price;
			if (summaryEntitiesOfRepair == null) {
				loss_price = BigDecimal.ZERO;
			} else {
				loss_price = summaryEntitiesOfRepair.getLoss_price();
			}
			
			setSummary(badLossSummaryEntity, badLossSummaryOfOthersEntities,loss_price);	
		}
		
		list.add(badLossSummaryEntity);
		
		//到当前操作时间为止
		if(badLossSummaryEntity.getMonth()==workPeriodMonthEntity.getMonth()){   
			break;
		}
	}
	
	//当前选择年月
	String cur_year_month = "";
	// 如果月份是小于9月
	if (workPeriodMonthEntity.getMonth() <= 9) {
		cur_year_month = workPeriodMonthEntity.getYear() + "-0" + workPeriodMonthEntity.getMonth() + "-01";
	} else {
		cur_year_month = workPeriodMonthEntity.getYear() + "-" + workPeriodMonthEntity.getMonth() + "-01";
	}
	//如果当前选择年月和现在月相等+
	if (!viewThMonth && cur_year_month.equals(thMonth)) {
		badLossSummaryOfOthersEntities = dao.searchLossSummaryOfBelongs(thMonth);

		BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();
		setSummary(badLossSummaryEntity, badLossSummaryOfOthersEntities, null);

		Calendar cal = Calendar.getInstance();

		badLossSummaryEntity.setMonth(cal.get(Calendar.MONTH) + 1);
		badLossSummaryEntity.setYear(cal.get(Calendar.YEAR));
		badLossSummaryEntity.setWork_period(RvsUtils.getBussinessYearString(cal));

		list.add(badLossSummaryEntity);
	}

	BeanUtil.copyToFormList(list, badLossSummaryForms, CopyOptions.COPYOPTIONS_NOEMPTY,
			BadLossSummaryForm.class);
	return badLossSummaryForms;
}

	/**
	 * 上线前的损金数据
	 * @param badLossSummaryEntity
	 */
	private void getOldData(BadLossSummaryEntity badLossSummaryEntity) {
		
		Integer month = badLossSummaryEntity.getMonth();
		if (4 == month) {
			// 报价差异（OCAP不计）
			badLossSummaryEntity.setQuotation(new BigDecimal("4422.69"));
			// 分解追加
			badLossSummaryEntity.setDecomposition(new BigDecimal("41624.79"));
			// NS追加  
			badLossSummaryEntity.setNs(new BigDecimal("62036.95"));
			// 工程内发现
			badLossSummaryEntity.setProject_discover(new BigDecimal("16161.73"));
			// 新品零件不良（OCAP不计）
			badLossSummaryEntity.setNew_partial_nogood(new BigDecimal("20842.53"));
			// 操作不良
			badLossSummaryEntity.setProject_nogood(new BigDecimal("7263.73"));
			// 保修期内不良
			badLossSummaryEntity.setService_repair(new BigDecimal("32107.14"));
		} else if (5 == month) {
			// 报价差异（OCAP不计）
			badLossSummaryEntity.setQuotation(new BigDecimal("4602.33"));
			// 分解追加
			badLossSummaryEntity.setDecomposition(new BigDecimal("55954.11"));
			// NS追加  
			badLossSummaryEntity.setNs(new BigDecimal("37075.40"));
			// 工程内发现
			badLossSummaryEntity.setProject_discover(new BigDecimal("22590.58"));
			// 新品零件不良（OCAP不计）
			badLossSummaryEntity.setNew_partial_nogood(new BigDecimal("18404.02"));
			// 操作不良
			badLossSummaryEntity.setProject_nogood(new BigDecimal("0.00"));
			// 保修期内不良
			badLossSummaryEntity.setService_repair(new BigDecimal("14808.13"));
		} else if (6 == month) {
			// 报价差异（OCAP不计）
			badLossSummaryEntity.setQuotation(new BigDecimal("5069.18"));
			// 分解追加
			badLossSummaryEntity.setDecomposition(new BigDecimal("47142.95"));
			// NS追加  
			badLossSummaryEntity.setNs(new BigDecimal("61216.36"));
			// 工程内发现
			badLossSummaryEntity.setProject_discover(new BigDecimal("20105.00"));
			// 新品零件不良（OCAP不计）
			badLossSummaryEntity.setNew_partial_nogood(new BigDecimal("23322.94"));
			// 操作不良
			badLossSummaryEntity.setProject_nogood(new BigDecimal("0.00"));
			// 保修期内不良
			badLossSummaryEntity.setService_repair(new BigDecimal("17871.10"));
		}
	}

	private void setSummary(BadLossSummaryEntity badLossSummaryEntity,
			List<BadLossSummaryEntity> badLossSummaryOfOthersEntities,BigDecimal loss_price) {
		for (int i = 0; i < badLossSummaryOfOthersEntities.size(); i++) {
			BadLossSummaryEntity sorcLossEntity = badLossSummaryOfOthersEntities.get(i);
			if (sorcLossEntity != null) {
				if (sorcLossEntity.getBelongs() == 3) {
					badLossSummaryEntity.setQuotation(sorcLossEntity.getLoss_price());
				} else if (sorcLossEntity.getBelongs() == 4) {
					badLossSummaryEntity.setDecomposition(sorcLossEntity.getLoss_price());
				} else if (sorcLossEntity.getBelongs() == 5) {
					badLossSummaryEntity.setNs(sorcLossEntity.getLoss_price());
				} else if (sorcLossEntity.getBelongs() == 6) {
					badLossSummaryEntity.setProject_discover(sorcLossEntity.getLoss_price());
				} else if (sorcLossEntity.getBelongs() == 7) {
					badLossSummaryEntity.setProject_nogood(sorcLossEntity.getLoss_price());
				} else if (sorcLossEntity.getBelongs() == 8) {
					badLossSummaryEntity.setNew_partial_nogood(sorcLossEntity.getLoss_price());
				}
			}
		}
		badLossSummaryEntity.setService_repair(loss_price);
	}

	public BadLossSummaryForm searchLossSummaryOfMonth(BadLossSummaryEntity entity, SqlSession conn,
			List<MsgInfo> errors) throws Exception {
		BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);
		BadLossSummaryForm badLossSummaryForm = new BadLossSummaryForm();

		// 查询损金总计详细数据
		BadLossSummaryEntity badLossSummaryEntity = dao.searchLossSummaryOfMonth(entity);

		List<BadLossSummaryEntity> badLossSummaryOfOthersEntities = null;

		if (badLossSummaryEntity != null) {
			String year_month = badLossSummaryEntity.getYear() + "-" + badLossSummaryEntity.getMonth() + "-01";

			badLossSummaryOfOthersEntities = dao.searchLossSummaryOfBelongs(year_month);

			for (int i = 0; i < badLossSummaryOfOthersEntities.size(); i++) {

				BadLossSummaryEntity badLossSummaryEntity2 = badLossSummaryOfOthersEntities.get(i);
				if (badLossSummaryEntity2 != null) {
					if (badLossSummaryEntity2.getBelongs() == 3) {
						badLossSummaryEntity.setQuotation(badLossSummaryEntity2.getLoss_price());
					} else if (badLossSummaryEntity2.getBelongs() == 4) {
						badLossSummaryEntity.setDecomposition(badLossSummaryEntity2.getLoss_price());
					} else if (badLossSummaryEntity2.getBelongs() == 5) {
						badLossSummaryEntity.setNs(badLossSummaryEntity2.getLoss_price());
					} else if (badLossSummaryEntity2.getBelongs() == 6) {
						badLossSummaryEntity.setProject_discover(badLossSummaryEntity2.getLoss_price());
					} else if (badLossSummaryEntity2.getBelongs() == 7) {
						badLossSummaryEntity.setProject_nogood(badLossSummaryEntity2.getLoss_price());
					} else if (badLossSummaryEntity2.getBelongs() == 8) {
						badLossSummaryEntity.setNew_partial_nogood(badLossSummaryEntity2.getLoss_price());
					}
				}
			}
			// 查询出保内返品不良的维修对象的损金金额
			BadLossSummaryEntity summaryEntitiesOfRepair = dao.searchLossSummaryOfRepair(year_month);
			BigDecimal loss_price;
			if (summaryEntitiesOfRepair == null) {
				loss_price = BigDecimal.ZERO;
			} else {
				loss_price = summaryEntitiesOfRepair.getLoss_price();
			}
			badLossSummaryEntity.setService_repair(loss_price);
			
			BeanUtil.copyToForm(badLossSummaryEntity, badLossSummaryForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			return badLossSummaryForm;
		} else {
			return null;
		}
	}

	/*	*//**
	 * 查询belongs是其他的时候月损金数据
	 * 
	 * @param parameterMap
	 * @param conn
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	/*
	 * @SuppressWarnings("unchecked") public List<BadLossSummaryForm> searchLossSummaryOfOther(Map<String, String[]>
	 * parameterMap,SqlSession conn,List<MsgInfo> errors) throws Exception{ BadLossSummaryMapper dao =
	 * conn.getMapper(BadLossSummaryMapper.class); List<BadLossSummaryForm> badLossSummaryForms = new
	 * ArrayList<BadLossSummaryForm>();
	 * 
	 * @SuppressWarnings("rawtypes") List<HashMap> badlossSummaryInMonth = new
	 * AutofillArrayList<HashMap>(HashMap.class); Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
	 * 
	 * // 整理提交数据---(年月) for (String parameterKey : parameterMap.keySet()) { Matcher m = p.matcher(parameterKey); if
	 * (m.find()) { String entity = m.group(1); if ("weeks".equals(entity)) { String column = m.group(2); int icounts =
	 * Integer.parseInt(m.group(3)); String[] value = parameterMap.get(parameterKey);
	 * 
	 * if ("work_period".equals(column)) { badlossSummaryInMonth.get(icounts).put("work_period", value[0]); } else if
	 * ("month".equals(column)) { badlossSummaryInMonth.get(icounts).put("month", value[0]); } } } }
	 * 
	 * List<BadLossSummaryEntity> badLossSummaryEntitiesOfOthers =null; for (HashMap<String, ?> retData :
	 * badlossSummaryInMonth) { //年份 String year =
	 * CodeListUtils.getKeyByValue("loss_work_period",retData.get("work_period").toString(),""); //出货年月 String
	 * ocm_shipping_date = year+retData.get("month").toString();
	 * 
	 * badLossSummaryEntitiesOfOthers = dao.searchLossSummaryOfBelongs(ocm_shipping_date); }
	 * BeanUtil.copyToFormList(badLossSummaryEntitiesOfOthers, badLossSummaryForms, CopyOptions.COPYOPTIONS_NOEMPTY,
	 * BadLossSummaryForm.class);
	 * 
	 * return badLossSummaryForms; }
	 */

	/*
	*//**
	 * 更新损金
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	/*
	 * public void updateLossSummary(ActionForm form,SqlSessionManager conn,List<MsgInfo> errors) throws Exception{
	 * BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);
	 * 
	 * BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();
	 * 
	 * BeanUtil.copyToBean(form, badLossSummaryEntity,CopyOptions.COPYOPTIONS_NOEMPTY);
	 * 
	 * dao.updateLossSummary(badLossSummaryEntity); }
	 */

	/**
	 * 更新月损金数据
	 * 
	 * @param parameterMap
	 * @param conn
	 */
	public void setMonthData(String choose_month,Map<String, String[]> parameterMap, SqlSessionManager conn,List<MsgInfo> errors) {
		BadLossSummaryEntity badLossSummaryEntity = new BadLossSummaryEntity();
		List<BadLossSummaryForm> badLossSummaryForms = new AutofillArrayList<BadLossSummaryForm>(BadLossSummaryForm.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("weeks".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					if ("work_period".equals(column)) {
						badLossSummaryForms.get(icounts).setWork_period(value[0]);
					} else if ("month".equals(column)) {
						badLossSummaryForms.get(icounts).setMonth(value[0]);
					} else if ("ocm_check".equals(column)) {
						badLossSummaryForms.get(icounts).setOcm_check(value[0]);
					} else if ("qa_check".equals(column)) {
						badLossSummaryForms.get(icounts).setQa_check(value[0]);
					} else if ("endoeye".equals(column)) {
						badLossSummaryForms.get(icounts).setEndoeye(value[0]);
					} else if ("ccd_valid_length".equals(column)) {
						badLossSummaryForms.get(icounts).setCcd_valid_length(value[0]);
					} else if ("financy_budget".equals(column)) {
						badLossSummaryForms.get(icounts).setFinancy_budget(value[0]);
					} else if ("e_u_settlement".equals(column)) {
						badLossSummaryForms.get(icounts).setE_u_settlement(value[0]);
					}
				}
			}
		}

		BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);

		//当前年份
		Calendar calendar = Calendar.getInstance();
		String workPeriod = RvsUtils.getBussinessYearString(calendar);
		int year = RvsUtils.getBussinessYearStringRever(workPeriod);
		
		// 更新月损金数据
		for (BadLossSummaryForm badLossSummaryForm : badLossSummaryForms) {
			
			badLossSummaryForm.setYear(year+"");
			
			Validators v=BeanUtil.createBeanValidators(badLossSummaryForm, BeanUtil.CHECK_TYPE_ALL);
			List<MsgInfo> infos=v.validate();
			
			BeanUtil.copyToBean(badLossSummaryForm, badLossSummaryEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
			/*if(badLossSummaryEntity.getMonth()>Integer.parseInt(choose_month)){
				break;
			}*/

			//查询当前选择损金修改月中是否存在该条数据(如果存在则进行数据的更新--如果不存在则插入新数据)
			BadLossSummaryEntity lossSummaryEntity = dao.searchLossSummaryOfMonth(badLossSummaryEntity);
			
			if(infos.size()>0){
				for(int i=0;i<infos.size();i++){
					MsgInfo error = infos.get(i);
					error.setErrmsg("请为"+badLossSummaryForm.getMonth()+"月的"+error.getErrmsg().replaceAll("请为", "").replaceAll("的值", ",小数点为两位的值"));
					errors.add(error);
				}
				break;
			}else{
				if(lossSummaryEntity==null){
					dao.insertLossSummary(badLossSummaryEntity);
				}else{
					badLossSummaryEntity.setYear(lossSummaryEntity.getYear());
					dao.updateLossSummary(badLossSummaryEntity);
				}
			}
		}
	}

	/**
	 * 更新月损金的备注
	 * 
	 * @param badLossSummaryEntity
	 * @param conn
	 */
	public void updateComment(BadLossSummaryEntity badLossSummaryEntity, SqlSessionManager conn) {
		BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);

		dao.updateLossSummaryOfComment(badLossSummaryEntity);
	}

	public void updateLossSummaryOfSettlement(BadLossSummaryEntity badLossSummaryEntity, SqlSessionManager conn) {
		BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);
		BadLossSummaryEntity lossSummaryEntity = dao.searchLossSummaryOfMonth(badLossSummaryEntity);
		
		//当前年份
		Calendar calendar = Calendar.getInstance();
		//String workPeriod = RvsUtils.getBussinessYearString(calendar);
		//int year = RvsUtils.getBussinessYearStringRever(workPeriod);
		int curYear = calendar.get(Calendar.YEAR);
		
		if(lossSummaryEntity==null){
			badLossSummaryEntity.setYear(curYear);
			dao.insertLossSummary(badLossSummaryEntity);
		}else{
			dao.updateLossSummaryOfSettlement(badLossSummaryEntity);
		}
	}
	
	public BadLossSummaryForm searchOneData(SqlSession conn, BadLossSummaryEntity workPeriodMonthEntity,List<MsgInfo> errors) throws Exception {		
		BadLossSummaryForm badLossSummaryForm = new BadLossSummaryForm();
		
		BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);
		
		BadLossSummaryEntity badLossSummaryEntity =new BadLossSummaryEntity();
			
		String year_month = "";
		// 如果月份是小于9月
		if (workPeriodMonthEntity.getMonth() <= 9) {
			year_month = workPeriodMonthEntity.getYear() + "-0" + workPeriodMonthEntity.getMonth() + "-01";
		} else {
			year_month = workPeriodMonthEntity.getYear() + "-" + workPeriodMonthEntity.getMonth() + "-01";
		}
		
		//loss_summary的分室检查不良+最终检查不良+ENDOEYE+CCD有效长度
		BadLossSummaryEntity otherPriceEntity = dao.searchLossSummaryFromOfYear(workPeriodMonthEntity.getWork_period()+"",workPeriodMonthEntity.getMonth()+"");
		
		// 查询出损金金额(追加零件)
		BadLossSummaryEntity  addPartialPriceEntity = dao.searchTotalSummary(year_month);

		// 查询出保修期内不良的维修对象的损金金额
		BadLossSummaryEntity repairLossPriceEntity = dao.searchLossSummaryOfRepair(year_month);

		BigDecimal repairPrice=new BigDecimal("0");
		if(repairLossPriceEntity != null){
			if(repairLossPriceEntity.getLoss_price()==null){
				repairPrice=new BigDecimal("0");
			}else{
				repairPrice=repairLossPriceEntity.getLoss_price();
			}
		}
		
		BigDecimal otherPrice=new BigDecimal("0");
		if(otherPriceEntity!=null){
			if(otherPriceEntity.getLoss_price()==null){
				otherPrice=new BigDecimal("0");
			}else{
				otherPrice=otherPriceEntity.getLoss_price();
			}
		}
		
		BigDecimal addPartialOtherPrice=repairPrice.add(otherPrice);
		//保修期内不良的损金+出损金金额(除了保修期内不良)
		BigDecimal totalPrice= new BigDecimal("0");
		if(addPartialPriceEntity!=null){
			totalPrice = addPartialPriceEntity.getLoss_price().add(addPartialOtherPrice);
		}	
		
		badLossSummaryEntity.setLoss_price(totalPrice);
		
		BeanUtil.copyToForm( badLossSummaryEntity, badLossSummaryForm,CopyOptions.COPYOPTIONS_NOEMPTY);
		return badLossSummaryForm;
	}

	/**
	 * 损金导出
	 * 
	 * @param request
	 * @return
	 */
	public String createLossSummaryReport(SqlSession conn,BadLossSummaryEntity wordPeriodMonthEntity,List<MsgInfo> errors) throws Exception {
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "不良损金汇总模板.xls";
		String cacheName = "不良损金汇总" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM")
				+ "\\" + cacheName;
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 查询出当前月损金数据
		List<BadLossSummaryForm> badLossSummaryForms = this.searchLossSummary(conn, wordPeriodMonthEntity, errors);
		
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new FileInputStream(new File(cachePath));
			// 创建Excel
			HSSFWorkbook work = new HSSFWorkbook(in);
			// 获取第一个sheet表单
			HSSFSheet sheet = work.getSheetAt(0);
			sheet.setForceFormulaRecalculation(true);
			// 第二个sheet工作表单--添加饼图数据
			HSSFSheet lastSheet = work.getSheetAt(1);
			lastSheet.setForceFormulaRecalculation(true);
			
			int index = 3;
			int z = 0;
			BadLossSummaryForm badLossSummaryForm = null;
			BadLossSummaryForm lossSummaryForm = null;
			
			//查询出最近两个月的损金数据
			int a;
			if(badLossSummaryForms.size()>2){
				a =badLossSummaryForms.size() - 2;			
			}else{
				a =0;
			}
			for (int j = a; j < badLossSummaryForms.size(); j++) {
				lossSummaryForm = badLossSummaryForms.get(j);
				
				if("4".equals(lossSummaryForm.getMonth())){
					sheet.getRow(53).getCell(1).setCellValue(lossSummaryForm.getWork_period()+"-"+(Integer.parseInt(lossSummaryForm.getMonth())));
					sheet.getRow(53).getCell(6).setCellValue(lossSummaryForm.getWork_period()+"-"+(Integer.parseInt(lossSummaryForm.getMonth())+1));
				}else{
					sheet.getRow(53).getCell(1).setCellValue(lossSummaryForm.getWork_period()+"-"+(Integer.parseInt(lossSummaryForm.getMonth())-1));
					sheet.getRow(53).getCell(6).setCellValue(lossSummaryForm.getWork_period()+"-"+lossSummaryForm.getMonth());
				}
					
				// 报价差异
				String quotation = "";
				if (CommonStringUtil.isEmpty(lossSummaryForm.getQuotation())) {
					quotation = "0";
				} else {
					quotation = lossSummaryForm.getQuotation();
				}
				lastSheet.getRow(0).getCell(z).setCellValue(Double.valueOf(quotation));
				lastSheet.getRow(0).getCell(z).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				// 分解追加
				String decomposition = "";
				if (CommonStringUtil.isEmpty(lossSummaryForm.getDecomposition())) {
					decomposition = "0";
				} else {
					decomposition = lossSummaryForm.getDecomposition();
				}
				lastSheet.getRow(1).getCell(z).setCellValue(Double.valueOf(decomposition));
				lastSheet.getRow(1).getCell(z).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				// NS追加
				String ns = "";
				if (CommonStringUtil.isEmpty(lossSummaryForm.getNs())) {
					ns = "0";
				} else {
					ns = lossSummaryForm.getNs();
				}
				lastSheet.getRow(2).getCell(z).setCellValue(Double.valueOf(ns));
				lastSheet.getRow(2).getCell(z).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				// 工程内发现
				String project_discover = "";
				if (CommonStringUtil.isEmpty(lossSummaryForm.getProject_discover())) {
					project_discover = "0";
				} else {
					project_discover = lossSummaryForm.getProject_discover();
				}
				lastSheet.getRow(3).getCell(z).setCellValue(Double.valueOf(project_discover));
				lastSheet.getRow(3).getCell(z).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				// 新品零件不良
				String new_partial_nogood = "";
				if (CommonStringUtil.isEmpty(lossSummaryForm.getNew_partial_nogood())) {
					new_partial_nogood = "0";
				} else {
					new_partial_nogood = lossSummaryForm.getNew_partial_nogood();
				}
				lastSheet.getRow(4).getCell(z).setCellValue(Double.valueOf(new_partial_nogood));
				lastSheet.getRow(4).getCell(z).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				// 操作不良
				String project_nogood = "";
				if (CommonStringUtil.isEmpty(lossSummaryForm.getProject_nogood())) {
					project_nogood = "0";
				} else {
					project_nogood = lossSummaryForm.getProject_nogood();
				}
				lastSheet.getRow(5).getCell(z).setCellValue(Double.valueOf(project_nogood));
				lastSheet.getRow(5).getCell(z).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				// 分室检查不良
				String ocm_check = "";
				if (CommonStringUtil.isEmpty(lossSummaryForm.getOcm_check())) {
					ocm_check = "0";
				} else {
					ocm_check = lossSummaryForm.getOcm_check();
				}
				lastSheet.getRow(6).getCell(z).setCellValue(Double.valueOf(ocm_check));
				lastSheet.getRow(6).getCell(z).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				// 最终检查不良
				String qa_check = "";
				if (CommonStringUtil.isEmpty(lossSummaryForm.getQa_check())) {
					qa_check = "0";
				} else {
					qa_check = lossSummaryForm.getQa_check();
				}
				lastSheet.getRow(7).getCell(z).setCellValue(Double.valueOf(qa_check));
				lastSheet.getRow(7).getCell(z).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				// 保修期内不良
				String service_repair = "";
				if (CommonStringUtil.isEmpty(lossSummaryForm.getService_repair())) {
					service_repair = "0";
				} else {
					service_repair = lossSummaryForm.getService_repair();
				}
				lastSheet.getRow(8).getCell(z).setCellValue(Double.valueOf(service_repair));
				lastSheet.getRow(8).getCell(z).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				z++;
			}
			int month = 0;
			for (int i = 0; i < 12; i++) {
				index++;
				if (i < badLossSummaryForms.size()) {
					badLossSummaryForm = badLossSummaryForms.get(i);
					month = Integer.parseInt(badLossSummaryForm.getMonth());

					sheet.getRow(0).getCell(1).setCellValue(badLossSummaryForm.getWork_period());
					
					// 财年月份(12个月)
					sheet.getRow(1).getCell(index)
							.setCellValue(badLossSummaryForm.getWork_period() + "-" + badLossSummaryForm.getMonth());

					// 报价差异
					String quotation = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getQuotation())) {
						quotation = "0";
					} else {
						quotation = badLossSummaryForm.getQuotation();
					}
					sheet.getRow(2).getCell(index).setCellValue(Double.valueOf(quotation));
					sheet.getRow(2).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// 分解追加
					String decomposition = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getDecomposition())) {
						decomposition = "0";
					} else {
						decomposition = badLossSummaryForm.getDecomposition();
					}
					sheet.getRow(3).getCell(index).setCellValue(Double.valueOf(decomposition));
					sheet.getRow(3).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// NS追加
					String ns = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getNs())) {
						ns = "0";
					} else {
						ns = badLossSummaryForm.getNs();
					}
					sheet.getRow(4).getCell(index).setCellValue(Double.valueOf(ns));
					sheet.getRow(4).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// 工程内发现
					String project_discover = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getProject_discover())) {
						project_discover = "0";
					} else {
						project_discover = badLossSummaryForm.getProject_discover();
					}
					sheet.getRow(5).getCell(index).setCellValue(Double.valueOf(project_discover));
					sheet.getRow(5).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// 新品零件不良
					String new_partial_nogood = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getNew_partial_nogood())) {
						new_partial_nogood = "0";
					} else {
						new_partial_nogood = badLossSummaryForm.getNew_partial_nogood();
					}
					sheet.getRow(6).getCell(index).setCellValue(Double.valueOf(new_partial_nogood));
					sheet.getRow(6).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// 操作不良
					String project_nogood = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getProject_nogood())) {
						project_nogood = "0";
					} else {
						project_nogood = badLossSummaryForm.getProject_nogood();
					}
					sheet.getRow(7).getCell(index).setCellValue(Double.valueOf(project_nogood));
					sheet.getRow(7).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// 分室检查不良
					String ocm_check = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getOcm_check())) {
						ocm_check = "0";
					} else {
						ocm_check = badLossSummaryForm.getOcm_check();
					}
					sheet.getRow(8).getCell(index).setCellValue(Double.valueOf(ocm_check));
					sheet.getRow(8).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// 最终检查不良
					String qa_check = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getQa_check())) {
						qa_check = "0";
					} else {
						qa_check = badLossSummaryForm.getQa_check();
					}
					sheet.getRow(9).getCell(index).setCellValue(Double.valueOf(qa_check));
					sheet.getRow(9).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// 保修期内不良
					String service_repair = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getService_repair())) {
						service_repair = "0";
					} else {
						service_repair = badLossSummaryForm.getService_repair();
					}
					sheet.getRow(10).getCell(index).setCellValue(Double.valueOf(service_repair));
					sheet.getRow(10).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// endoeye
					String endoeye = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getEndoeye())) {
						endoeye = "0";
					} else {
						endoeye = badLossSummaryForm.getEndoeye();
					}
					sheet.getRow(11).getCell(index).setCellValue(Double.valueOf(endoeye));
					sheet.getRow(11).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// CCD有效长度
					String ccd_valid_length = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getCcd_valid_length())) {
						ccd_valid_length = "0";
					} else {
						ccd_valid_length = badLossSummaryForm.getCcd_valid_length();
					}
					sheet.getRow(12).getCell(index).setCellValue(Double.valueOf(ccd_valid_length));
					sheet.getRow(12).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					String financy_budget = "";
					if (CommonStringUtil.isEmpty(badLossSummaryForm.getFinancy_budget())) {
						financy_budget = "0";
					} else {
						financy_budget = badLossSummaryForm.getFinancy_budget();
					}

					// 损金总计(OSH)
					double loss_total_osh = Double.valueOf(quotation) + Double.valueOf(decomposition)
							+ Double.valueOf(ns) + Double.valueOf(project_discover)
							+ Double.valueOf(new_partial_nogood) + Double.valueOf(project_nogood)
							+ Double.valueOf(ocm_check) + Double.valueOf(qa_check) + Double.valueOf(service_repair)
							+ Double.valueOf(endoeye) + Double.valueOf(ccd_valid_length);
					sheet.getRow(13).getCell(index).setCellValue(loss_total_osh);
					sheet.getRow(13).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// 不良比率(OSH)--损金总计(OSH)/零件成本(财务数据)
					double nogood_rate_osh = loss_total_osh / Double.valueOf(financy_budget);
					if ("0".equals(financy_budget)) {
						nogood_rate_osh = 0;
					}
					sheet.getRow(14).getCell(index).setCellValue(Double.valueOf(nogood_rate_osh));

					// 损金总计(OCAP)
					double loss_total_ocap = Double.valueOf(quotation) + Double.valueOf(new_partial_nogood)
							+ Double.valueOf(service_repair);
					sheet.getRow(15).getCell(index).setCellValue(loss_total_ocap);
					sheet.getRow(15).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					// 不良比率(OCAP)- 损金总计(OCAP)/零件成本(财务数据)
					double nogood_rate_ocap = loss_total_ocap / Double.valueOf(financy_budget);
					if ("0".equals(financy_budget)) {
						nogood_rate_ocap = 0;
					}
					sheet.getRow(16).getCell(index).setCellValue(nogood_rate_ocap);

					// 零件成本(财务数据)
					sheet.getRow(17).getCell(index).setCellValue(Double.valueOf(financy_budget));
					sheet.getRow(17).getCell(index).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					
					sheet.getRow(19).getCell(1).setCellValue(badLossSummaryForm.getWork_period());
					
					sheet.getRow(80).getCell(1).setCellValue(badLossSummaryForm.getWork_period());
				} else {
					month++;
					if (month > 12) {
						month = 1;
					}
					// 财年月份(12个月)
					sheet.getRow(1).getCell(index).setCellValue(badLossSummaryForm.getWork_period() + "-" + month);
				}
			}
			out = new FileOutputStream(cachePath);
			work.write(out);

		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		return cacheName;
	}
	
	
	/**
	 * 获取所有SORC财年
	 * @param conn
	 * @return
	 */
	private Map<String,String> getAllWorkPeriods(SqlSession conn){
		BadLossSummaryMapper dao= conn.getMapper(BadLossSummaryMapper.class);
		List<String> list = dao.getAllWorkPeriods();
		
		Map<String,String> map = new HashMap<String,String>();
		for(int i=0;i<list.size();i++){
			map.put(list.get(i), list.get(i));
		}
		return map;
	}
	
	
	/**
	 * 获取其他SORC财年(除了当前)
	 * @param workPeriod
	 * @return
	 */
	public List<String> getOtherPeriods(SqlSession conn,String curWorkPeriod){
		Map<String,String> map = this.getAllWorkPeriods(conn);
		
		if(map.containsKey(curWorkPeriod)){
			map.remove(curWorkPeriod);
		}
		
		Set<String> set = map.keySet();
	    Iterator<String> iterator = set.iterator();
		
	    List<String> list = new ArrayList<String>();
	    while(iterator.hasNext()){
	    	list.add("<< "+iterator.next());
	    }
		return list;
	}
	
	/**
	 * 其他财年
	 * @param conn
	 * @param errors
	 * @param workPeriod
	 * @return
	 * @throws Exception
	 */
	public List<BadLossSummaryForm> searchOtherPeriodsLossSummary(SqlSession conn, List<MsgInfo> errors,String workPeriod) throws Exception {
		BadLossSummaryMapper dao = conn.getMapper(BadLossSummaryMapper.class);
		
		
		int[] months = new int[]{4,5,6,7,8,9,10,11,12,1,2,3};
		int year = RvsUtils.getBussinessYearStringRever(workPeriod);
		BadLossSummaryEntity judgeisExistLossSummary=null;
		
		for(int i = 0;i<months.length;i++){
			//判断当前月之前的月份数据是否已经存在
			judgeisExistLossSummary = dao.searchLossSummaryFromOfYear(workPeriod,months[i]+"");
			if(judgeisExistLossSummary == null){
				judgeisExistLossSummary = new BadLossSummaryEntity();
				
				if(i>=9){
					int nextYear = year+1;
					judgeisExistLossSummary.setYear(nextYear);
				}else{
					judgeisExistLossSummary.setYear(year);
				}
				
				judgeisExistLossSummary.setWork_period(workPeriod);
				judgeisExistLossSummary.setMonth(months[i]);
				dao.insertLossSummary(judgeisExistLossSummary);				
			}
		}
		
		
		List<BadLossSummaryForm> badLossSummaryForms = new ArrayList<BadLossSummaryForm>();
		List<BadLossSummaryEntity> list = new ArrayList<BadLossSummaryEntity>();
		
		// 查询损金总计详细数据
		List<BadLossSummaryEntity> badLossSummaryEntities = dao.searchLossSummaryFrom(workPeriod);

		List<BadLossSummaryEntity> badLossSummaryOfOthersEntities = null;
		
		for (BadLossSummaryEntity badLossSummaryEntity : badLossSummaryEntities) {
			String year_month = "";
			// 如果月份是小于9月
			if (badLossSummaryEntity.getMonth() <= 9) {
				year_month = badLossSummaryEntity.getYear() + "-0" + badLossSummaryEntity.getMonth() + "-01";
			} else {
				year_month = badLossSummaryEntity.getYear() + "-" + badLossSummaryEntity.getMonth() + "-01";
			}
			
			// 模块上线前的旧数据
			if (year_month.compareTo("2014-07-01") < 0)
				getOldData(badLossSummaryEntity);
			else {
				badLossSummaryOfOthersEntities = dao.searchLossSummaryOfBelongs(year_month);
	
				// 查询出保内返品不良的维修对象的损金金额
				BadLossSummaryEntity summaryEntitiesOfRepair = dao.searchLossSummaryOfRepair(year_month);
				BigDecimal loss_price;
				if (summaryEntitiesOfRepair == null) {
					loss_price = BigDecimal.ZERO;
				} else {
					loss_price = summaryEntitiesOfRepair.getLoss_price();
				}
				setSummary(badLossSummaryEntity, badLossSummaryOfOthersEntities, loss_price);
			}
			list.add(badLossSummaryEntity);
		}
		
		BeanUtil.copyToFormList(list, badLossSummaryForms, CopyOptions.COPYOPTIONS_NOEMPTY,BadLossSummaryForm.class);
		return badLossSummaryForms;
		
	}
	
	
	
}

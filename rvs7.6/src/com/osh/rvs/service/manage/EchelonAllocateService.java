package com.osh.rvs.service.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.manage.EchelonAllocateEntity;
import com.osh.rvs.form.manage.EchelonAllocateForm;
import com.osh.rvs.mapper.manage.EchelonAllocateMapper;
import com.osh.rvs.mapper.manage.UserDefineCodesMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class EchelonAllocateService {
	// 梯队设定历史
	public List<EchelonAllocateForm> searchEchelonAllocate(SqlSession conn) {
		List<EchelonAllocateForm> echelonAllocateFormList = new ArrayList<EchelonAllocateForm>();
		EchelonAllocateMapper dao = conn.getMapper(EchelonAllocateMapper.class);

		// 梯队设定历史
		List<EchelonAllocateEntity> echelonAllocateEntityList = dao.searchEchelonAllocate();
		CopyOptions cos = new CopyOptions();
		cos.excludeNull();
		cos.excludeEmptyString();
		cos.dateConverter(DateUtil.ISO_DATE_PATTERN, "start_date", "end_date", "updated_time");
		BeanUtil.copyToFormList(echelonAllocateEntityList, echelonAllocateFormList, cos, EchelonAllocateForm.class);

		return echelonAllocateFormList;
	}

	// 梯队设定历史记录
	public List<EchelonAllocateForm> searchEchelonHistorySet(EchelonAllocateEntity echelonAllocateEntity,
			SqlSession conn) {
		List<EchelonAllocateForm> echelonAllocateFormList = new ArrayList<EchelonAllocateForm>();
		EchelonAllocateMapper dao = conn.getMapper(EchelonAllocateMapper.class);

		// 梯队设定历史记录
		List<EchelonAllocateEntity> echelonAllocateEntityList = dao.searchEchelonHistorySet(echelonAllocateEntity);
		
		BeanUtil.copyToFormList(echelonAllocateEntityList, echelonAllocateFormList, CopyOptions.COPYOPTIONS_NOEMPTY,
				EchelonAllocateForm.class);
		
		return echelonAllocateFormList;
	}

	// 等级型号设定详细
	public List<EchelonAllocateForm> searchModelLevelSet(EchelonAllocateForm echelonAllocateForm,HttpServletRequest request,SqlSession conn) {
		List<EchelonAllocateForm> echelonAllocateFormList = new ArrayList<EchelonAllocateForm>();
		EchelonAllocateMapper dao = conn.getMapper(EchelonAllocateMapper.class);
		
		EchelonAllocateEntity   echelonAllocateBean = new EchelonAllocateEntity();
		BeanUtil.copyToBean(echelonAllocateForm, echelonAllocateBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 等级型号设定详细
		List<EchelonAllocateEntity> echelonAllocateEntityList = dao.searchModelLevelSet(echelonAllocateBean);
		
		UserDefineCodesMapper userDefineCodesDao = conn.getMapper(UserDefineCodesMapper.class);	
		
		for (int i = 0; i < echelonAllocateEntityList.size(); i++) {
			
			//第一梯队容纳件数
			int level1Limit = Integer.parseInt(userDefineCodesDao.searchUserDefineCodesValueByCode("FIRST_ECHELON_QUALIFYING"));
			//第二梯队同意数界限
			int level2Limit = Integer.parseInt(userDefineCodesDao.searchUserDefineCodesValueByCode("SECOND_ECHELON_DIVIDING_LINE"));
			
			EchelonAllocateEntity echelonAllocateEntity = echelonAllocateEntityList.get(i);

			// 按照同意维修数的数量降序排序，前二十条的设置成第一梯队
			if (i < level1Limit) {
				echelonAllocateEntity.setEchelon("1");

				// 前20条以后,同意维修数>2,设置成第二梯队
			} else if (Integer.parseInt(echelonAllocateEntity.getAgreed_count())>= level2Limit) {
				echelonAllocateEntity.setEchelon("2");

				// 同意维修数=0时，设置成第四梯队
			} else if (Integer.parseInt(echelonAllocateEntity.getAgreed_count()) == 0) {
				echelonAllocateEntity.setEchelon("4");

				// 同意维修数<2时，设置成第三梯队
			} else if (Integer.parseInt(echelonAllocateEntity.getAgreed_count())< level2Limit) {
				echelonAllocateEntity.setEchelon("3");
			}
		}
		//将每次查询的结果放在Session中，进行更新时取查询过后的值进行更新
		request.getSession().setAttribute("echelonAllocateEntityList", echelonAllocateEntityList);
		
		BeanUtil.copyToFormList(echelonAllocateEntityList, echelonAllocateFormList, CopyOptions.COPYOPTIONS_NOEMPTY,
				EchelonAllocateForm.class);

		return echelonAllocateFormList;
	}

	// 更新梯队
	public void updateEchelonHistorySet(HttpServletRequest request,SqlSessionManager conn) {
		EchelonAllocateMapper dao = conn.getMapper(EchelonAllocateMapper.class);

		//每次检索之后的所有 等级型号设定详细
		@SuppressWarnings("unchecked")
		List<EchelonAllocateEntity> echelonAllocateEntityList =(List<EchelonAllocateEntity>)request.getSession().getAttribute(
				"echelonAllocateEntityList");

		UserDefineCodesMapper userDefineCodesDao = conn.getMapper(UserDefineCodesMapper.class);	
		
		for (int i = 0; i < echelonAllocateEntityList.size(); i++) {
			
			//第一梯队容纳件数
			int level1Limit = Integer.parseInt(userDefineCodesDao.searchUserDefineCodesValueByCode("FIRST_ECHELON_QUALIFYING"));
			//第二梯队同意数界限
			int level2Limit = Integer.parseInt(userDefineCodesDao.searchUserDefineCodesValueByCode("SECOND_ECHELON_DIVIDING_LINE"));
			
			EchelonAllocateEntity echelonAllocateEntity = echelonAllocateEntityList.get(i);

			// 按照同意维修数的数量降序排序，前二十条的设置成第一梯队
			if (i < level1Limit) {
				echelonAllocateEntity.setEchelon("1");

				// 前20条以后,同意维修数>2,设置成第二梯队
			} else if (Integer.parseInt(echelonAllocateEntity.getAgreed_count())>= level2Limit) {
				echelonAllocateEntity.setEchelon("2");

				// 同意维修数=0时，设置成第四梯队
			} else if (Integer.parseInt(echelonAllocateEntity.getAgreed_count()) == 0) {
				echelonAllocateEntity.setEchelon("4");

				// 同意维修数<2时，设置成第三梯队
			} else if (Integer.parseInt(echelonAllocateEntity.getAgreed_count())< level2Limit) {
				echelonAllocateEntity.setEchelon("3");
			}
			// 更新梯队
			dao.updateEchelonHistorySet(echelonAllocateEntity);
		}
	}

	// 判断梯队历史的最后时间
	public String  searchEndDate(SqlSession conn) {
		EchelonAllocateMapper dao = conn.getMapper(EchelonAllocateMapper.class);
		SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd");

		// 查询历史梯队的最后时间
		Date endDate = dao.searchEndDate();
		EchelonAllocateEntity echelonAllocateEntity  = new EchelonAllocateEntity();
		Calendar calendar = Calendar.getInstance();
		if (endDate==null){
			calendar.add(Calendar.MONTH, -6);
			echelonAllocateEntity.setEnd_date(calendar.getTime());
		}else {
			calendar.add(Calendar.MONTH, -3);
			//取出来的最后时间和在当前时间的三个月之前的时间相比较
			if(endDate.after(calendar.getTime())){
				//如果大于设置当前的时间-3个月
				echelonAllocateEntity.setEnd_date(calendar.getTime());
			} else{
				//如果小于设置取出来的最后时间
				echelonAllocateEntity.setEnd_date(endDate);
			}
		}
		echelonAllocateEntity.setEnd_date(calendar.getTime());
		//获取的最后确定时间
		String  strEndDate = df.format(echelonAllocateEntity.getEnd_date());
		return strEndDate;
	}
}

package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.DailyCheckResultEntity;


public interface DailyCheckResultMapper {
	
	/*日常点检结果前半部分详细数据*/
	public List<DailyCheckResultEntity> searchDailyCheckResult(DailyCheckResultEntity dailyCheckResultEntity);
	
	/*日常点检结果=课室+工程+工位*/
	public DailyCheckResultEntity searchSectionLinePosition(DailyCheckResultEntity dailyCheckResultEntity);
	
	/*设备工具点检记录--查询当前月的所有详细*/
	public List<DailyCheckResultEntity> searchCheckResult(DailyCheckResultEntity dailyCheckResultEntity);
}

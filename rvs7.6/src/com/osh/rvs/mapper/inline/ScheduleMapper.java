package com.osh.rvs.mapper.inline;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.osh.rvs.bean.inline.ScheduleEntity;

public interface ScheduleMapper {

	public List<String> searchMaterialIdsByCondition(ScheduleEntity entity);
	
	public List<ScheduleEntity> searchMaterialByIds(List<String> ids);
	
	public List<ScheduleEntity> searchScheduleByCondition(ScheduleEntity entity);

	public List<ScheduleEntity> searchReportScheduleByCondition(ScheduleEntity entity);

	public void deleteSchedule(ScheduleEntity entity);
	
	public void updateToPuse(String id);

	public List<Map<String, Object>> getWorkingOfCategories();

	public Set<String> getCcdLineTargets();
}

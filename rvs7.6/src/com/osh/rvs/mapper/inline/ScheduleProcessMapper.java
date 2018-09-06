package com.osh.rvs.mapper.inline;

import java.util.List;

import com.osh.rvs.bean.inline.ScheduleEntity;

public interface ScheduleProcessMapper {

	public List<String> searchMaterialIdsByCondition(ScheduleEntity entity);
	
	public List<ScheduleEntity> searchMaterialByIds(List<String> ids);
	
	public List<ScheduleEntity> searchSchedule(ScheduleEntity entity);
	
	public void updateSchedule(ScheduleEntity entity);
	
	public void deleteSchedule(ScheduleEntity entity);
	
	public void updateToPuse(String id);
	
	public List<ScheduleEntity> searchOutSchedule();
}

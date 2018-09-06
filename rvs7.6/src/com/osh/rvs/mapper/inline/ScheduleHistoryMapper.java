package com.osh.rvs.mapper.inline;

import com.osh.rvs.bean.inline.ScheduleHistoryEntity;


public interface ScheduleHistoryMapper {
	public ScheduleHistoryEntity getByKey(ScheduleHistoryEntity entity);

	public void append(ScheduleHistoryEntity entity) throws Exception;
	
	public void appendTodayAsUpdate(ScheduleHistoryEntity entity) throws Exception;

	public void removeToday(ScheduleHistoryEntity entity) throws Exception;

	public ScheduleHistoryEntity getOtherInfo(String material_id);

	public void updatePeriod(ScheduleHistoryEntity entity) throws Exception;
}

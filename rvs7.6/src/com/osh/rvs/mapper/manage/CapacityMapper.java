package com.osh.rvs.mapper.manage;

import java.util.List;

import com.osh.rvs.bean.inline.ScheduleEntity;

public interface CapacityMapper {

	//查询产能设定的课室
	public List<ScheduleEntity> searchSectionName();
	
	//查询所有产能
	public List<ScheduleEntity> searchCapacitySetting();
	
	//查询所有产能
	public String checkIsExist(ScheduleEntity conditionEntity);
	
	//更新修改的最大产能
	public void updateUpperLimit(ScheduleEntity conditionEntity);
	
	//新建产能数据
	public void insertCapacity(ScheduleEntity conditionEntity);

	//删除产能数据
	public void deleteCapacity(ScheduleEntity conditionEntity);
}

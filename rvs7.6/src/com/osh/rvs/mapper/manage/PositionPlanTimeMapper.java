package com.osh.rvs.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.manage.PositionPlanTimeEntity;

public interface PositionPlanTimeMapper {
	
	public List<PositionPlanTimeEntity> search(PositionPlanTimeEntity entity);

	public PositionPlanTimeEntity checkPositionUnreach(PositionPlanTimeEntity entity);
	public PositionPlanTimeEntity checkLineUnreach(PositionPlanTimeEntity entity);

	public int requestCountdownUnreach(PositionPlanTimeEntity entity);

	public int setCountdownUnreach(PositionPlanTimeEntity entity);

	public List<String> checkCausedByPartial(@Param("material_id") String material_id, @Param("line_id") String line_id);
	public int checkCausedByHeap(@Param("material_id") String material_id, @Param("section_id") String section_id, 
			@Param("line_id") String line_id, @Param("position_id") String position_id);

	public List<PositionPlanTimeEntity> searchCountdownUnreach(
			PositionPlanTimeEntity entity);

	public PositionPlanTimeEntity getUnreachDetail(@Param("material_id") String material_id, @Param("line_id") String line_id);
}

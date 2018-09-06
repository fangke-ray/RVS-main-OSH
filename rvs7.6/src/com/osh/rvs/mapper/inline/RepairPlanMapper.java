package com.osh.rvs.mapper.inline;

import java.util.Map;

import org.apache.ibatis.annotations.Param;


/**
 * 维修计划
 * @author Gong
 *
 */
public interface RepairPlanMapper {

	public Map<String, Integer> getShippingPlan(@Param("planYear") String planYear, @Param("planMonth") String planMonth);

}
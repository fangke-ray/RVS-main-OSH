package com.osh.rvs.mapper.report;

import java.util.List;

import com.osh.rvs.bean.report.WorkTimeAnalysisEntity;

/**
 * 
 * @Title WorkTimeAnalysisMapping.java
 * @Project rvs
 * @Package com.osh.rvs.mapper.report
 * @ClassName: WorkTimeAnalysisMapping
 * @Description: 工时分析
 * @author liuxb
 * @date 2016-10-8 下午1:56:08
 */
public interface WorkTimeAnalysisMapper {
	// 实际平均用时月查询
	public List<WorkTimeAnalysisEntity> searchAvgWorkTimeByMonth(WorkTimeAnalysisEntity entity);
	
	// 实际平均用时按周查询
	public List<WorkTimeAnalysisEntity> searchAvgWorkTimeByWeek(WorkTimeAnalysisEntity entity);
	
	// 异常工时
	public List<WorkTimeAnalysisEntity> searchAnomaly(WorkTimeAnalysisEntity entity);
}

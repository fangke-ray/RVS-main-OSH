package com.osh.rvs.mapper.report;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.osh.rvs.bean.report.FoundryEntity;

public interface FoundryMapper {
	// 工位代工时间
	public List<FoundryEntity> searchFoundryOfPosition(FoundryEntity entity);

	// 操作人员代工时间
	public List<FoundryEntity> searchFoundryOfOperator(FoundryEntity entity);

	// 每个工位中每人代工时间
	public List<FoundryEntity> searchFoundryOfPositionAndOperator(FoundryEntity entity);

	public Date getStartTime(String operator_id);

	public int insert(Map<String, String> map) throws Exception;

	public int update(Map<String, String> map) throws Exception;

	public int delete(Map<String, String> map) throws Exception;

	public List<FoundryEntity> searchFoundryOfLine(FoundryEntity entity);
}

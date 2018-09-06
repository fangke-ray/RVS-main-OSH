package com.osh.rvs.mapper.qa;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;

public interface QualityAssuranceMapper {

	public List<MaterialEntity> getWaitings(String position_id);

	public List<MaterialEntity> getFinished(String position_id);

	public MaterialEntity getMaterialDetail(String material_id);

	public int updateMaterial(MaterialEntity entity) throws Exception;

	public int forbidMaterial(String material_id) throws Exception;

	public List<MaterialEntity> getWaitingsFiling(String position_id);

	public List<MaterialEntity> getFinishedFiling();

	public int updateMaterialFiling(String material_id) throws Exception;

	public HashMap<String, Object> getQualityAssuranceDataForMonth(String yearMonth);

	public List<HashMap<String, Object>> getQualityAssuranceDataForWeek(@Param("year_month") String yearMonth);

	public void updateQualityAssuranceDataForWeek(@Param("year_month") String sYearMonth,
			@Param("week_of_month") String week_of_month, @Param("process_count") String process_count,
			@Param("fail_count") String fail_count, @Param("start_date") String start_date,  @Param("end_date") String end_date);

	public void addQaWorkCount(@Param("year_month") String yearMonth, @Param("date") String sDate, 
			@Param("proceed") String sProceed, @Param("forbid") String sForbid);
}

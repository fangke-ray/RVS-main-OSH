package com.osh.rvs.mapper.inline;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.inline.LineLeaderEntity;

public interface LineLeaderMapper {

	// 取得工程内各工位，及各工位的仕掛
	public List<Map<String, String>> getWorkingOfPositions(@Param("section_id") String section_id, @Param("line_id") String line_id);

	// 取得工程内全部
	public List<LineLeaderEntity> getWorkingMaterials(@Param("section_id") String section_id, @Param("line_id") String line_id, @Param("position_id") String position_id, 
			@Param("checked_group") String checked_group, @Param("today") String today);

	// 切换线内加急
	public void switchLeaderExpedite(@Param("material_id") String material_id, @Param("line_id") String line_id) throws Exception;

	public List<MaterialEntity> getBeforePerformanceList(MaterialEntity bean);

	public void switchQuotationFirst(String material_id);

	public List<String> getOverTimePosition(@Param("section_id") String section_id, @Param("line_id") String line_id);
	
	public List<Map<String, Object>> getGroupedPositions(@Param("section_id") String section_id, @Param("line_id") String line_id);
}

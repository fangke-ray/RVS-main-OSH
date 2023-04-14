package com.osh.rvs.mapper.report;

import java.util.List;
import java.util.Map;

import com.osh.rvs.bean.report.WaittingTimeReportEntity;

public interface WaittingTimeReportMapper {
	public List<WaittingTimeReportEntity> getMaterailIds(WaittingTimeReportEntity entity);

	public List<WaittingTimeReportEntity> search(List<String> material_ids);
	
	public List<WaittingTimeReportEntity> searchDetails(List<String> material_ids);

	public List<Map<String, Object>> getTimeNodes(List<String> material_ids);

	public List<WaittingTimeReportEntity> searchBoldDetails(
			List<String> material_ids);
}

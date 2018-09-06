package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.ToolsCheckResultEntity;


public interface ToolsCheckResultMapper {
	
	/*治具点检结果前半部分详细数据*/
	public List<ToolsCheckResultEntity> searchToolsCheckResult(ToolsCheckResultEntity toolsCheckResultEntity);
	
	/*治具点检结果=课室+工程+工位*/
	public ToolsCheckResultEntity searchSectionLinePosition(ToolsCheckResultEntity toolsCheckResultEntity);
	
	/*治具点检记录--查询当前月的所有详细*/
	public List<ToolsCheckResultEntity> searchCheckResult(ToolsCheckResultEntity toolsCheckResultEntity);
}

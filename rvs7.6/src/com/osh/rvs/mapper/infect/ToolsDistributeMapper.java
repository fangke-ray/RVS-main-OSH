package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.ToolsDistributeEntity;


public interface ToolsDistributeMapper {
	
	/*治具分布详细数据*/
   public List<ToolsDistributeEntity> searchToolsDistribute(ToolsDistributeEntity toolsDistributeEntity);
   
}

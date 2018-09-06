package com.osh.rvs.mapper.qa;

import java.util.List;

import com.osh.rvs.bean.qa.ServiceRepairResolveEntity;


public interface ServiceRepairResolveMapper {
	//查询保内QIS管理 保内返品对策对应--保内QIS分析数据
	public List<ServiceRepairResolveEntity> searchServiceRepairResolve(ServiceRepairResolveEntity serviceRepairResolveEntity);
	
	//更新保内返品对策对应内容
	public void updateSServiceRepairResolve(ServiceRepairResolveEntity serviceRepairResolveEntity);
	
	//查询担当人
	public String searchJobNo(ServiceRepairResolveEntity serviceRepairResolveEntity);
}

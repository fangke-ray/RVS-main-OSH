package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.SystemImageManageEntity;


public interface SystemImageManageMapper {
	
	//查询详细
	public List<SystemImageManageEntity> searchImageDescription(SystemImageManageEntity condition);
	
	//编辑工程检查票内图说明
	public void replaceImageDescription(SystemImageManageEntity condition);

}

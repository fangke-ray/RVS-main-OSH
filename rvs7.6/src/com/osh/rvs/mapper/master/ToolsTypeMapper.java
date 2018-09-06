package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.ToolsTypeEntity;


public interface ToolsTypeMapper {
	/*治具品名详细*/
	public List<ToolsTypeEntity> searchToolsType(ToolsTypeEntity devicesTypeEntity);	
	
	/*新建治具品名*/
	public void insertToolsType(ToolsTypeEntity devicesTypeEntity);
	
	/*删除治具品名*/
	public void deleteToolsType(ToolsTypeEntity devicesTypeEntity);
	
	/*修改治具品名*/
	public void updateToolsType(ToolsTypeEntity devicesTypeEntity);
	
	/*所有治具品名*/
	public List<ToolsTypeEntity> getAllToolsName();
}

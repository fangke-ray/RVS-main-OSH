package com.osh.rvs.mapper.master;

import java.util.Date;
import java.util.List;

import com.osh.rvs.bean.master.ToolsManageEntity;

public interface ToolsManageMapper {

	/* 治具管理详细数据 */
	public List<ToolsManageEntity> searchToolsManage(
			ToolsManageEntity toolsManageEntity);

	/* 修改治具管理详细 */
	public void updateToolsManage(ToolsManageEntity toolsManageEntity);

	/* 插入治具管理数据 */
	public void insertToolsManage(ToolsManageEntity toolsManageEntity);

	/* 删除治具管理 */
	public void deleteToolsManage(ToolsManageEntity toolsManageEntity);

	/* 查询所有的管理编号 */
	public List<String> searchManageCode(ToolsManageEntity toolsManageEntity);

	/* 查询最大管理编号 */
	public List<String> searchMaxManageCode(ToolsManageEntity toolsManageEntity);

	public void replace(ToolsManageEntity toolsManageEntity);

	public ToolsManageEntity getByKey(String manage_id);

	/* 批量交付 */
	public void deliverToolsManage(ToolsManageEntity toolsManageEntity);

	/** 确认区间内是否发生交付 */
	public Date checkProvideInPeriod(ToolsManageEntity toolsManageEntity);

	/** 确认区间内是否发生废弃 */
	public Date checkWasteInPeriod(ToolsManageEntity toolsManageEntity);
	
	public void disband(ToolsManageEntity toolsManageEntity);

}

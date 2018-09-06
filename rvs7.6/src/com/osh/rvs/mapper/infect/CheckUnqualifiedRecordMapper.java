package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.CheckUnqualifiedRecordEntity;
import com.osh.rvs.bean.master.DevicesManageEntity;
import com.osh.rvs.bean.master.ToolsManageEntity;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.mapper.infect
 * @ClassName: CheckUnqualifiedRecordMapper
 * @Description:点检不合格记录Mapper
 * @author lxb
 * @date 2014-8-13 下午12:16:48
 * 
 */
public interface CheckUnqualifiedRecordMapper {

	/* 一览 */
	public List<CheckUnqualifiedRecordEntity> search(CheckUnqualifiedRecordEntity entity);

	/* 设备工具名称下拉框 */
	public List<DevicesManageEntity> getDevicesNameReferChooser(DevicesManageEntity entity);

	/* 治具名称下拉框 */
	public List<ToolsManageEntity> getToolsNameReferChooser(ToolsManageEntity entity);

	public CheckUnqualifiedRecordEntity getById(CheckUnqualifiedRecordEntity entity);

	/* 线长确认 */
	public void updateByLineLeader(CheckUnqualifiedRecordEntity entity);

	/* 经理确认 */
	public void updateByManage(CheckUnqualifiedRecordEntity entity);

	/* 设备管理员确认 */
	public void updateByTechnology(CheckUnqualifiedRecordEntity entity);

	/** 建立不合格记录 */
	public void create(CheckUnqualifiedRecordEntity entity);

	public CheckUnqualifiedRecordEntity getSectionAndLine(
			CheckUnqualifiedRecordEntity entity);

	public void updateStatus(CheckUnqualifiedRecordEntity entity);

	public boolean checkBlockedToolsOnPosition(CheckUnqualifiedRecordEntity entity);
	public boolean checkBlockedDevicesOnPosition(CheckUnqualifiedRecordEntity entity);
}

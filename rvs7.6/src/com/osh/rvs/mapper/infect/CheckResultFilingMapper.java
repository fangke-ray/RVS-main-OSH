package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.CheckResultFilingEntity;

public interface CheckResultFilingMapper {

	/* 一览 */
	public List<CheckResultFilingEntity> searchCheckResultFiling(CheckResultFilingEntity checkResultFilingEntity);

	/* 双击点检结果文档详细 */
	public List<CheckResultFilingEntity> searchCheckedFileStorage(CheckResultFilingEntity checkResultFilingEntity);
	
	/* 上传附表--点检表名称list*/
	public List<CheckResultFilingEntity> searchCheckFileNames();
	
	/* 上传附表--设备名称list*/
	public List<CheckResultFilingEntity> searchDeviceNames();
	
	/* 新建点检归档记录*/
	public void insertCheckedFileStorage(CheckResultFilingEntity checkResultFilingEntity);
}

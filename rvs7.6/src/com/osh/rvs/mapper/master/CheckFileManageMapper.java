package com.osh.rvs.mapper.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.infect.CheckResultEntity;
import com.osh.rvs.bean.master.CheckFileManageEntity;
import com.osh.rvs.bean.master.DeviceCheckItemEntity;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.mapper.master
 * @ClassName: CheckFileManageMapper
 * @Description: 点检表管理Mapper
 * @author lxb
 * @date 2014-8-11 下午12:41:12
 * 
 */
public interface CheckFileManageMapper {
	/* 一览 */
	public List<CheckFileManageEntity> search(CheckFileManageEntity entity);
	public CheckFileManageEntity getByKey(String check_file_manage_id);

	/*删除*/
	public void delete(CheckFileManageEntity entity);
	
	/*新建点检表*/
	public void insert(CheckFileManageEntity entity);
	
	/*检查点检表管理号是否已经存在*/
	public int checkManageCodeIsExist(CheckFileManageEntity entity);
	
	/*检查是否是当前IDs*/
	public String checkIdIsCurrent(CheckFileManageEntity entity);
	
	/*更新点检表*/
	public void update(CheckFileManageEntity entity);

	/** 根据点检者列出全部相关表单  */
	public List<CheckFileManageEntity> searchManageCodeByOperator(CheckResultEntity condEntity);
	/** 根据管理者列出全部相关表单  */
	public List<CheckFileManageEntity> searchManageCodeByManager(CheckResultEntity condEntity);

	/** 保存点检项目 */
	public void addSeqItem(DeviceCheckItemEntity itemEntity);

	public List<DeviceCheckItemEntity> getSeqItemsByFile(@Param("check_file_manage_id") String check_file_manage_id);
	
	public void deleteDevicesCheckItem(@Param("check_file_manage_id") String check_file_manage_id);

	/*检查使用前的数据是否已经存在*/
	public List<CheckFileManageEntity> checkDataIsExist(CheckFileManageEntity entity);
}

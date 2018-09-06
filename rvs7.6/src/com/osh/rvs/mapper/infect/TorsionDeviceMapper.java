package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.CheckResultEntity;
import com.osh.rvs.bean.infect.TorsionDeviceEntity;
import com.osh.rvs.bean.master.DevicesManageEntity;


public interface TorsionDeviceMapper {
   //新建力矩设备
   public void insertTorsionDevice(TorsionDeviceEntity conditionEntity);
	
   //力矩设备详细一览
   public List<TorsionDeviceEntity> searchTorsionDevice(TorsionDeviceEntity conditionEntity);
   
   //修改力矩设备详细信息
   public void updateTorsionDevice(TorsionDeviceEntity conditionEntity);
   
   //管理编号--referchoose
   public List<TorsionDeviceEntity> searchManageCodes();
   
   //删除力矩设备
   public void deleteTorsionDevice(TorsionDeviceEntity conditionEntity);
   
   //判断管理编号是否已经存在
   public List<String> searchManageCode(TorsionDeviceEntity conditionEntity);

public List<DevicesManageEntity> searchTorsionDeviceOnLineByManager(
		CheckResultEntity condEntity);

public List<DevicesManageEntity> searchTorsionDeviceOnLineByOperator(
		CheckResultEntity condEntity);

public List<String> getSeqItemsByTorsionSetting(String devices_manage_id);
}

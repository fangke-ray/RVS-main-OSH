package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.DevicesTypeEntity;


public interface DevicesTypeMapper {
	/*设备工具品名详细*/
	public List<DevicesTypeEntity> searchDeviceType(DevicesTypeEntity devicesTypeEntity);	
	
	/*新建设备工具品名*/
	public void insertDevicesType(DevicesTypeEntity devicesTypeEntity);
	
	/*删除设备工具品名*/
	public void deleteDevicesType(DevicesTypeEntity devicesTypeEntity);
	
	/*修改设备工具品名*/
	public void updateDevicesType(DevicesTypeEntity devicesTypeEntity);
	
	/*查询所有设备品名--referChooser*/
	public List<DevicesTypeEntity> getAllDeviceName();
}

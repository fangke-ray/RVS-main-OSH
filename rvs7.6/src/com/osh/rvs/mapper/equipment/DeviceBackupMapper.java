package com.osh.rvs.mapper.equipment;

import java.util.List;

import com.osh.rvs.bean.equipment.DeviceBackupEntity;

public interface DeviceBackupMapper {
	public List<DeviceBackupEntity> searchAll();

	public List<DeviceBackupEntity> getRelation(String manage_id);

	public int deleteRelation(DeviceBackupEntity entity);

	public int insertRelation(DeviceBackupEntity entity);

	public int updateRelation(DeviceBackupEntity entity);

	public int replaceCorresponding(DeviceBackupEntity entity);
}

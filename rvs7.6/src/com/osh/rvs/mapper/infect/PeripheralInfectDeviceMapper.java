package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.PeripheralInfectDeviceEntity;

public interface PeripheralInfectDeviceMapper {
	
	/* 周边设备点检关系详细数据 */
	public List<PeripheralInfectDeviceEntity> search(PeripheralInfectDeviceEntity entity);

	public String getMaxSeq(PeripheralInfectDeviceEntity entity);

	public int insert(PeripheralInfectDeviceEntity entity) throws Exception;

	public int delete(PeripheralInfectDeviceEntity entity) throws Exception;

	public List<PeripheralInfectDeviceEntity> getPeripheralDataByMaterialId(PeripheralInfectDeviceEntity entity);

	public int insertFinishedData(PeripheralInfectDeviceEntity entity) throws Exception;

	public String getGroupedInfectMessage(PeripheralInfectDeviceEntity entity);
}

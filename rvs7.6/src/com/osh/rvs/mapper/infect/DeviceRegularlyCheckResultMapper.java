package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.DeviceRegularlyCheckResultEntity;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.mapper.infect
 * @ClassName: DeviceRegularlyCheckResultMapper
 * @Description: 设备工具定期点检Mapper
 * @author lxb
 * @date 2014-8-19 上午10:05:12
 * 
 */
public interface DeviceRegularlyCheckResultMapper {
	public List<DeviceRegularlyCheckResultEntity> search(DeviceRegularlyCheckResultEntity entity);
	
	public List<DeviceRegularlyCheckResultEntity> searchByWeek(DeviceRegularlyCheckResultEntity entity);
	
	public List<DeviceRegularlyCheckResultEntity> searchDetail(DeviceRegularlyCheckResultEntity entity);
	
	
}

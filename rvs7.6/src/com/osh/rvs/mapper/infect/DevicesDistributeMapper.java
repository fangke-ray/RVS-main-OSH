package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.master.DevicesManageEntity;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.mapper.infect
 * @ClassName: DevicesDistributeMapper
 * @Description: 设备工具分布Mapper
 * @author lxb
 * @date 2014-8-28 下午12:51:11
 * 
 */
public interface DevicesDistributeMapper {
	/* 一览 */
	public List<DevicesManageEntity> search(DevicesManageEntity entity);
}

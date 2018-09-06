package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.DryingOvenDeviceEntity;

public interface DryingOvenDeviceMapper {
	public List<DryingOvenDeviceEntity> search(DryingOvenDeviceEntity entity);

	// 查询所有烘箱管理
	public List<DryingOvenDeviceEntity> searchAllDryingOvenDevice();

	// 判断烘箱管理是否存在
	public DryingOvenDeviceEntity checkIsExist(DryingOvenDeviceEntity entity);

	// 新建烘箱管理
	public void insert(DryingOvenDeviceEntity entity) throws Exception;

	// 更新烘箱管理
	public void update(DryingOvenDeviceEntity entity) throws Exception;

	// 删除烘箱管理
	public void delete(DryingOvenDeviceEntity entity) throws Exception;
}

package com.osh.rvs.mapper.qf;

import java.util.List;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.qf.SteelWireContainerWashProcessEntity;

/**
 * @Description: 物料加工
 * @author liuxb
 * @date 2018-5-14 下午1:13:23
 */
public interface SteelWireContainerWashProcessMapper {
	public List<SteelWireContainerWashProcessEntity> search(
			SteelWireContainerWashProcessEntity entity) throws Exception;

	public void insert(SteelWireContainerWashProcessEntity entity)
			throws Exception;
	
	public void update(SteelWireContainerWashProcessEntity entity)
			throws Exception;
	
	public void updateMaterial(SteelWireContainerWashProcessEntity entity)
			throws Exception;
	
	public List<MaterialEntity> searchMaterial();
}

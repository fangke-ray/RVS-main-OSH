package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;

public interface ConsumablePositionMapper {
	public int create(MaterialPartialDetailEntity entity);

	public List<MaterialPartialDetailEntity> findPositionBelong(MaterialPartialDetailEntity condition);

	public int delete(MaterialPartialDetailEntity entity);
	public List<String> locateUsePositions();
}

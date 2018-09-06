package com.osh.rvs.mapper.data;

import java.util.List;

import com.osh.rvs.bean.data.PositionProductionEntity;

public interface PositionProductionMapper {

	public List<PositionProductionEntity> getProductionFeatureByPosition(PositionProductionEntity entity);
	
	public PositionProductionEntity getDetail(PositionProductionEntity entity);
	
	public List<PositionProductionEntity> getProductionFeatureByKey(PositionProductionEntity entity);
}

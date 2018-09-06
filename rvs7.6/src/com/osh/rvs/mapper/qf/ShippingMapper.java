package com.osh.rvs.mapper.qf;

import java.util.List;

import com.osh.rvs.bean.data.MaterialEntity;

public interface ShippingMapper {

	public List<MaterialEntity> getWaitings();

	public List<MaterialEntity> getFinished();

	public MaterialEntity getMaterialDetail(String material_id);

}

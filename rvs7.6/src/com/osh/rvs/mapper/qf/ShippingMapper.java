package com.osh.rvs.mapper.qf;

import java.util.List;

import com.osh.rvs.bean.data.MaterialEntity;

public interface ShippingMapper {

	public List<MaterialEntity> getWaitings(String postion_id);

	public List<MaterialEntity> getFinished(String postion_id);

	public MaterialEntity getMaterialDetail(String material_id);

}

package com.osh.rvs.mapper.qf;

import java.util.List;

import com.osh.rvs.bean.data.MaterialEntity;

public interface QuotationMapper {

	public List<MaterialEntity> getWaitings(String[] position_id);

	public List<MaterialEntity> getPaused(String[] position_id);

	public List<MaterialEntity> getFinished(String[] position_id);

	public MaterialEntity getMaterialDetail(String material_id);

	public int updateMaterial(MaterialEntity entity) throws Exception;
}

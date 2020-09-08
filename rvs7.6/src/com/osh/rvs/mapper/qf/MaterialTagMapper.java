package com.osh.rvs.mapper.qf;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.qf.MaterialTagEntity;

public interface MaterialTagMapper {
	public void insert(MaterialTagEntity entity);
	
	public void deleteByMaterialId(@Param("material_id") String material_id);
}

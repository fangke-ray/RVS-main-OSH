package com.osh.rvs.mapper.data;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialTagEntity;

public interface MaterialTagMapper {
	public void insert(MaterialTagEntity entity);
	
	public void deleteByMaterialId(@Param("material_id") String material_id);
}

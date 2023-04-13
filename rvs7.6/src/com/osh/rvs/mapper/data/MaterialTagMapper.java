package com.osh.rvs.mapper.data;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialTagEntity;

public interface MaterialTagMapper {
	public void insert(MaterialTagEntity entity);
	
	public void deleteTagsByMaterialId(@Param("material_id") String material_id, @Param("tag_types") String[] tag_types);

	public List<Integer> checkTagByMaterialId(@Param("material_id") String material_id, @Param("tag_type") String tag_type);

	public int deleteTagByMaterialId(@Param("material_id") String material_id, @Param("tag_type") String tag_type);

	public List<String> getAnmlWipMaterials();
}

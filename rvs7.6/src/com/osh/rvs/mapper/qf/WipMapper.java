package com.osh.rvs.mapper.qf;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;

public interface WipMapper {

	/**
	 * 维修对象一览，根据条件查询对象(不包含纳期）
	 * @param entity
	 * @return
	 */
	public List<MaterialEntity> searchMaterial(MaterialEntity entity);

	public List<String> getWipHeaped();

	public void insert(MaterialEntity insertBean) throws Exception; 

	public void remove(String material_id) throws Exception; 

	public void warehousing(MaterialEntity insertBean) throws Exception;

	public int getresystemcount(MaterialEntity mEntity);

	public void resystem(MaterialEntity mEntity) throws Exception;
	
	public void stop(String material_id) throws Exception;

	public void copyProductionFeature(@Param("material_id") String material_id,@Param("new_material_id")  String new_material_id) throws Exception;

	public void changeLocation(@Param("material_id") String material_id, @Param("wip_location") String wip_location);

	public boolean checkImgCheckReworking(@Param("material_id") String material_id, @Param("position_id") String position_id);
}

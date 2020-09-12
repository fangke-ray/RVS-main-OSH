package com.osh.rvs.mapper.qf;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.qf.FactReceptMaterialEntity;

public interface FactReceptMaterialMapper {
	/** 查询受理维修品 **/
	public List<FactReceptMaterialEntity> searchReceptMaterial();

	/** 新建临时维修品实物受理/测漏 **/
	public void insertFactReceptMaterialTemp(FactReceptMaterialEntity entity);

	public List<FactReceptMaterialEntity> searchTemp(FactReceptMaterialEntity entity);

	public void updateFactReceptMaterialTemp(FactReceptMaterialEntity entity);

	public FactReceptMaterialEntity getFactReceptTemp(@Param("fact_recept_id") String fact_recept_id);

}

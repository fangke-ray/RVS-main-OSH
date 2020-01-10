package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.WastePartialArrangementEntity;

/**
 * 废弃零件整理
 * 
 * @Description
 * @author dell
 * @date 2019-12-26 下午3:50:18
 */
public interface WastePartialArrangementMapper {
	public List<WastePartialArrangementEntity> search(WastePartialArrangementEntity entity);

	public void insert(WastePartialArrangementEntity entity);

	public Integer getMaxPartByMaterialId(@Param("material_id") String material_id);
}

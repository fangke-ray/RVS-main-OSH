package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;

/**
 * 修理品消耗品使用记录
 * 
 * @Description
 * @author gonglm
 * @date 2023-05-13 下午6:09:33
 */
public interface MaterialConsumableDetailMapper {

	/**
	 * 查询
	 * 
	 * @param entity
	 * @return
	 */
	public List<MaterialPartialDetailEntity> search(MaterialPartialDetailEntity entity);


	public List<MaterialPartialDetailEntity> searchForMaterialWithLine(
			@Param("material_id")String material_id, @Param("line_id")String line_id);

	/**
	 * 新建使用记录
	 * 
	 * @param entity
	 */
	public void insert(MaterialPartialDetailEntity entity);

	/**
	 * 更新使用数量
	 * 
	 * @param entity
	 */
	public void updateQuantity(MaterialPartialDetailEntity entity);

	/**
	 * 删除使用记录
	 * 
	 * @param entity
	 */
	public void remove(MaterialPartialDetailEntity entity);

	/**
	 * 修理品全部使用消耗品一览
	 * @param material_id
	 * @return
	 */
	public List<MaterialPartialDetailEntity> archiveOfConsumablesRecept(
			String material_id);

}

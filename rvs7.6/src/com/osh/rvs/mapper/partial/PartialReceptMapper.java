package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;

/**
 * 零件签收
 * @author lxb
 *
 */
public interface PartialReceptMapper {
	/**
	 * 零件一览
	 * @return
	 */
	public List<MaterialPartialDetailEntity> secrchPartialRecept(MaterialPartialDetailEntity entity);
	
	public void updatePartialRecept(MaterialPartialDetailEntity entity);

	public List<MaterialPartialDetailEntity> getPartialDetailByPosition(
			@Param("material_id") String material_id,@Param("position_id")  String position_id);

	public void updatePartialUnnessaray(MaterialPartialDetailEntity entity) throws Exception;

	public String getPremakePartialAddition(String partial_id);
	
}

package com.osh.rvs.mapper.manage;


import java.util.Date;

import org.apache.ibatis.annotations.Param;

public interface MaterialRemainTimeMapper {

	/**
	 * 取得维修对象在工程内的预计划时间
	 * @param material_id
	 * @param line_id
	 * @return
	 */
	public Date getMaterialRemainTime(@Param("material_id") String material_id, @Param("line_id") String line_id);

	public int checkMaterialRemainTime(@Param("material_id") String material_id, @Param("line_id") String line_id);
}

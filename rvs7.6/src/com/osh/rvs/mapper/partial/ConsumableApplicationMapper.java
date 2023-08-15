package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.ConsumableApplicationEntity;

public interface ConsumableApplicationMapper {

	/* 查询数据 */
	public List<ConsumableApplicationEntity> search(ConsumableApplicationEntity entity);

	//获取单个消耗品申请单
	public ConsumableApplicationEntity searchConsumableApplicationById(String consumable_application_key);

	/** 取得指定工程现存申请表 */ 
	public ConsumableApplicationEntity getCollectingApplicationByLine(
			@Param("section_id") String section_id,
			@Param("line_id") String line_id);

	public String getMaxApplicationByLine(@Param("section_id") String section_id,
			@Param("line_id") String line_id,
			@Param("application_no") String monthString);

	public void update(ConsumableApplicationEntity entity);

	/** 插入新申请表单 */
	public void insert(ConsumableApplicationEntity insertEntity) throws Exception;
	public void confirm(ConsumableApplicationEntity entity) throws Exception;

	public ConsumableApplicationEntity getCcdAdvancedByMaterial(String material_id);

	public void autoSupply(String key);
}

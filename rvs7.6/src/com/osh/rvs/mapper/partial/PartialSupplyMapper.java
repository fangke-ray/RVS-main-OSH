package com.osh.rvs.mapper.partial;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.PartialSupplyEntity;

public interface PartialSupplyMapper {

	/** 取得某日零件补充信息 */
	public List<PartialSupplyEntity> getPartialSupplyOfDate(@Param("supply_date") Date supply_date);

	public int insertPartialSupply(PartialSupplyEntity entity) throws Exception;

	public int deletePartialSupplyOfDate(@Param("supply_date") Date supply_date) throws Exception;

	
	public void updatePartialSupplyOfQuantity(PartialSupplyEntity entity);


	public int deletePartialSupply(PartialSupplyEntity parital) throws Exception;
	
	public PartialSupplyEntity getPartialSupply(PartialSupplyEntity entity);

}

package com.osh.rvs.mapper.partial;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.PartialStorageEntity;

public interface PartialStorageMapper {

	/** 取得某日零件库存信息 */
	public List<PartialStorageEntity> getPartialStorageByDate(@Param("storage_date") Date storage_date);

	public int insert(PartialStorageEntity entity) throws Exception;

	public int deleteByDateAndIdentification(PartialStorageEntity entity) throws Exception;

	public int delete(PartialStorageEntity parital) throws Exception;
	
	public void updateQuantity(PartialStorageEntity entity);
	
	public PartialStorageEntity getPartialStorage(PartialStorageEntity entity);

}

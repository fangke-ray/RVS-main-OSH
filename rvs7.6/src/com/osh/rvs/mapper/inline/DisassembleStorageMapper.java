package com.osh.rvs.mapper.inline;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.inline.DisassembleStorageEntity;

/**
 * 分解库位管理
 * 
 * @author Gonglm
 * 
 */
public interface DisassembleStorageMapper {
	// 检索
	public List<DisassembleStorageEntity> searchStorage(DisassembleStorageEntity condition);

	public DisassembleStorageEntity getStorageByKey(DisassembleStorageEntity condition);

	public List<DisassembleStorageEntity> getStorageOfPosition(String position_id);

	public DisassembleStorageEntity getStorageByMaterialInPosition(DisassembleStorageEntity condition);

	public int create(DisassembleStorageEntity entity);

	public int changeSetting(DisassembleStorageEntity entity);

	public int remove(DisassembleStorageEntity entity);

	public int putin(DisassembleStorageEntity entity);

	public int warehouse(DisassembleStorageEntity entity);

	public List<DisassembleStorageEntity> getStorageByMaterial(DisassembleStorageEntity entity);

	public List<DisassembleStorageEntity> getStorageFingerOfPosition(@Param("section_id") String section_id, @Param("position_id") String position_id);
}

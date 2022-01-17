package com.osh.rvs.mapper.qf;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.qf.TurnoverCaseEntity;

public interface TurnoverCaseMapper {

	public List<TurnoverCaseEntity> searchTurnoverCase(TurnoverCaseEntity condition);

	public List<String> getStorageHeaped();

	public void putin(TurnoverCaseEntity condition);

	public String getNextEmptyLocation(@Param("location")String location);

	public void checkStorage(@Param("location")String location);

	public void warehousing(@Param("location")String location);

	public List<TurnoverCaseEntity> getStoragePlan();

	public List<TurnoverCaseEntity> getWarehousingPlan();

	public List<TurnoverCaseEntity> getListOnShelf(String shelf);

	public TurnoverCaseEntity getEntityByLocation(String location);

	public TurnoverCaseEntity getEntityByLocationForStorage(String location);

	public TurnoverCaseEntity getEntityByLocationForShipping(String location);

	public List<TurnoverCaseEntity> getIdleMaterialList();

	public TurnoverCaseEntity checkEmpty(String location);

	public List<TurnoverCaseEntity> getAllSpaceShelf(@Param("kind")String kind, @Param("shelf")String shelf);

	/** 取得最空余的货架 **/
	public String getMostSpacialShelf(@Param("kind")Integer kind, @Param("layer")Integer layer, @Param("shelfs") List<String> mostSpacialShelf);

	public void setToPrepare(String key);

	public void setToLocation(@Param("location")String location, @Param("material_id")String material_id);

	public List<TurnoverCaseEntity> gerPreprintedLocations();

	public List<TurnoverCaseEntity> getAnimalExpLocations();

	public List<TurnoverCaseEntity> getAllEmptyLocations();
}

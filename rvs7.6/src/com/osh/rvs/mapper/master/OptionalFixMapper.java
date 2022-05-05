package com.osh.rvs.mapper.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.master.OptionalFixEntity;

public interface OptionalFixMapper {

	public List<OptionalFixEntity> searchOptionalFix(OptionalFixEntity entity);

	public OptionalFixEntity getOptionalFix(String optional_fix_id);

	public int checkCodeIsExist(@Param("standard_code")String standard_code, @Param("optional_fix_id")String optional_fix_id);

	public void insertOptionalFix(OptionalFixEntity entity);

	public void updateOptionalFix(OptionalFixEntity entity);

	public void deleteOptionalFix(OptionalFixEntity entity);

	public List<String> getRanks(String optional_fix_id);

	public void insertRank(OptionalFixEntity entity);

	public void deleteRank(String optional_fix_id);

	public List<OptionalFixEntity> getOptionalFixByRank(String rank);
}

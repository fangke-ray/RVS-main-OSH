package com.osh.rvs.mapper.qf;

import java.util.List;

import com.osh.rvs.bean.master.OptionalFixEntity;

public interface MaterialOptionalFixMapper {

	public List<OptionalFixEntity> searchMaterialOptionalFix(OptionalFixEntity condition);

	public int deleteMaterialOptionalFix(OptionalFixEntity delete);

	public int insertMaterialOptionalFix(OptionalFixEntity insert);

}

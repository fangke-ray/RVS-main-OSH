package com.osh.rvs.mapper.inline;

import java.util.List;

import com.osh.rvs.bean.inline.MaterialOgzEntity;

public interface MaterialOgzMapper {
	/* OGZ维修对象进度信息一览 */
	public List<MaterialOgzEntity> search(MaterialOgzEntity entity);

	/**/
	public MaterialOgzEntity getMaterialOgzById(String material_id);
	
	public void update(MaterialOgzEntity entity);
}

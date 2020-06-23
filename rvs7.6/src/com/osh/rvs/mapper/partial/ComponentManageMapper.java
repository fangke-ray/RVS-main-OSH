package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.ComponentManageEntity;

public interface ComponentManageMapper {

	// 查询数据
	public List<ComponentManageEntity> searchComponentManage(ComponentManageEntity ComponentManageEntity);
	
	/*查询所有的零件code和name*/
	/** search partial
	 * @param code */
	public List<ComponentManageEntity> getAllPartial(String code); 
}

package com.osh.rvs.mapper.inline;

import java.util.List;

import com.osh.rvs.bean.inline.SteelWireContainerWashProcessEntity;

/**
 * @Description: 钢丝固定件清洗记录
 * @author liuxb
 * @date 2018-5-14 下午1:13:23
 */
public interface SteelWireContainerWashProcessMapper {
	public List<SteelWireContainerWashProcessEntity> search(
			SteelWireContainerWashProcessEntity entity) throws Exception;

	public void insert(SteelWireContainerWashProcessEntity entity)
			throws Exception;
	
	public void update(SteelWireContainerWashProcessEntity entity)
			throws Exception;
}

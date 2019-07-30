package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.PartialBussinessStandardEntity;

/**
 * 零件出入库工时标准
 *
 * @author liuxb
 *
 */
public interface PartialBussinessStandardMapper {
	public List<PartialBussinessStandardEntity> search();

	public void update(PartialBussinessStandardEntity entity);
}

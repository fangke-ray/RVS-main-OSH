package com.osh.rvs.mapper.master;

import com.osh.rvs.bean.master.PartialUnpackEntity;

/**
 *
 * @author liuxb
 *
 */
public interface PartialUnpackMapper {
	public void insert(PartialUnpackEntity entity) throws Exception;

	public void update(PartialUnpackEntity entity) throws Exception;

	public void delete(PartialUnpackEntity entity) throws Exception;

	public PartialUnpackEntity getPartialUnpack(PartialUnpackEntity entity);

}

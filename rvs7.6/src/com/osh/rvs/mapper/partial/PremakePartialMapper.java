package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.PremakePartialEntity;

/**
 * 
 * @Title PremakePartialMapper.java
 * @Project rvs
 * @Package com.osh.rvs.mapper.partial
 * @ClassName: PremakePartialMapper
 * @Description: 零件预制
 * @author lxb
 * @date 2016-3-24 下午5:27:34
 */
public interface PremakePartialMapper {
	public List<PremakePartialEntity> search(PremakePartialEntity entity);

	public void update(PremakePartialEntity entity);
	
	public PremakePartialEntity checkExist(PremakePartialEntity entity);
	
	public void insert(PremakePartialEntity entity);
}

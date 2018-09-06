package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;

/**
 * 零件发放
 * 
 * @author lxb
 * 
 */
public interface PartialReleaseMapper {
	/* 零件发放维修对象一览 */
	public List<MaterialPartialEntity> searchMaterialPartialRelease(MaterialPartialEntity entity);

	/* 零件一览 */
	public List<MaterialPartialDetailEntity> secrchPartialOfRelease(MaterialPartialDetailEntity entity);

	/* 更新未发放数量和状态 */
	public void updateWaitingQuantityAndStatus(MaterialPartialDetailEntity entity);
	
	public void deletePartial(MaterialPartialDetailEntity entity);
	
	/*更新未发放数量和状态,消耗品发放*/
	public void updateWaitingQuantityAndStatuOfAppend(MaterialPartialDetailEntity entity);

	public void updateStatusOfReady(MaterialPartialDetailEntity entity);
}

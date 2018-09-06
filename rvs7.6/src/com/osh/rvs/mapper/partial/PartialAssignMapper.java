package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;

/**
 * 零件发放
 * @author lxb
 *
 */
public interface PartialAssignMapper {
	public List<MaterialPartialDetailEntity> searchMaterialPartialDetail();
	
	public void updateMaterialPartialDetail(MaterialPartialDetailEntity entity);
	
	public int searchStatus(MaterialPartialDetailEntity entity);
	
	public int checkWaiting(MaterialPartialDetailEntity entity);

	/*更新入库预定日*/
	public void updateArrivePlanDate(MaterialPartialDetailEntity entity);
}

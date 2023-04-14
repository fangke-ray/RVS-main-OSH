package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.StandardPartialAdditionEntity;

public interface StandardPartialAdditionMapper {

	public int insert(StandardPartialAdditionEntity entity);

	public List<StandardPartialAdditionEntity> search(StandardPartialAdditionEntity entity);

	public int update(StandardPartialAdditionEntity entity);

	public int delete(StandardPartialAdditionEntity entity);

	public List<StandardPartialAdditionEntity> getAllByPosition(String position_id);

	public List<StandardPartialAdditionEntity> getAllByModel(String model_id);

}

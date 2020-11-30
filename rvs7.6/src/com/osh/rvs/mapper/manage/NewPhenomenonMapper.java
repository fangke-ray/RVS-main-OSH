package com.osh.rvs.mapper.manage;

import java.util.List;

import com.osh.rvs.bean.manage.NewPhenomenonEntity;

public interface NewPhenomenonMapper {

	public NewPhenomenonEntity getNewPhenomenon(String alarmMessageId);

	public List<NewPhenomenonEntity> searchNewPhenomenons(NewPhenomenonEntity condition);

	public List<NewPhenomenonEntity> getAllLocations();

	public void insert(NewPhenomenonEntity entity);

	public void update(NewPhenomenonEntity entity);
}

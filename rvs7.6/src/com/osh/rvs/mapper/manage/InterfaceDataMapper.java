package com.osh.rvs.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.manage.InterfaceDataEntity;

public interface InterfaceDataMapper {

	/** search all */
	public List<InterfaceDataEntity> searchAllContent();

	/** search by key */
	public InterfaceDataEntity searchContentByKey(
			@Param("if_sap_message_key") String if_sap_message_key,
			@Param("seq") String seq);

	public void updateResolved(InterfaceDataEntity entity);

	public void updateContent(InterfaceDataEntity entity);

	public void deleteContent(
			@Param("if_sap_message_key") String if_sap_message_key,
			@Param("seq") String seq);
}

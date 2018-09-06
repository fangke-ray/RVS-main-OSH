package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.PrivacyEntity;
import com.osh.rvs.bean.master.PrivacyGroupEntity;

public interface PrivacyMapper {

	/** insert single */
	public int insertPrivacy(PrivacyEntity category) throws Exception;

	public int updatePrivacy(PrivacyEntity category) throws Exception;

	/** search all */
	public List<PrivacyEntity> getAllPrivacy();

	public PrivacyEntity getPrivacyByID(String category_id);

	/** search*/
	public List<PrivacyEntity> searchPrivacy(PrivacyEntity category);

	public void deletePrivacy(PrivacyEntity updateBean) throws Exception;

	/** search all */
	public List<PrivacyGroupEntity> searchPrivacyGroup();
}

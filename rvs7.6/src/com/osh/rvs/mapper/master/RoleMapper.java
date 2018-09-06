package com.osh.rvs.mapper.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.master.RoleEntity;

public interface RoleMapper {

	/** insert single */
	public int insertRole(RoleEntity role) throws Exception;

	public int updateRole(RoleEntity role) throws Exception;

	/** search all */
	public List<RoleEntity> getAllRole();

	public RoleEntity getRoleByID(String role_id);

	/** search
	 * @param privacy_id */
	public List<RoleEntity> searchRole(@Param("condition") RoleEntity role, @Param("privacy_id") String privacy_id);

	public void deleteRole(RoleEntity updateBean) throws Exception;

	/** 得到某角色的全部权限 */
	public List<String> getPrivaciesOfRole(String role_id);

	/** 插入某角色的权限 */
	public void insertPrivacyOfRole(@Param("role_id") String role_id, @Param("privacy_id") String privacy_id);

	/** 删除某角色的权限 */
	public void deletePrivacyOfRole(@Param("role_id") String role_id);

}

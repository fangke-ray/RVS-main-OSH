package com.osh.rvs.service.qf;

import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.qf.MaterialTagEntity;
import com.osh.rvs.mapper.qf.MaterialTagMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 
 * @Description 维修对象属性标签
 * @author dell
 * @date 2020-9-2 上午11:18:15
 */
public class MaterialTagService {
	/**
	 * 新建
	 * 
	 * @param form
	 * @param conn
	 */
	public void insert(ActionForm form, SqlSessionManager conn) {
		MaterialTagMapper dao = conn.getMapper(MaterialTagMapper.class);

		MaterialTagEntity entity = new MaterialTagEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.insert(entity);
	}

	/**
	 * 根据维修对象ID删除 维修对象属性标签
	 * 
	 * @param materialId 维修对象ID
	 * @param conn
	 */
	public void deleteByMaterialId(String materialId, SqlSessionManager conn) {
		MaterialTagMapper dao = conn.getMapper(MaterialTagMapper.class);
		dao.deleteByMaterialId(materialId);
	}
}

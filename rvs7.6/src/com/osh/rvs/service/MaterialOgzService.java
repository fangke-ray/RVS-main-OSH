package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.inline.MaterialOgzEntity;
import com.osh.rvs.form.inline.MaterialOgzForm;
import com.osh.rvs.mapper.inline.MaterialOgzMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.service
 * @ClassName: MaterialOgzService
 * @Description: OGZ维修对象进度信息
 * @author lxb
 * @date 2014-7-2 上午9:16:56
 * 
 */
public class MaterialOgzService {
	/**
	 * OGZ维修对象进度信息一览
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<MaterialOgzForm> search(ActionForm form, SqlSession conn) {
		MaterialOgzEntity entity = new MaterialOgzEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		MaterialOgzMapper dao = conn.getMapper(MaterialOgzMapper.class);

		List<MaterialOgzEntity> entityList = dao.search(entity);
		List<MaterialOgzForm> returnFormList = new ArrayList<MaterialOgzForm>();

		// 复制数据到表单对象
		BeanUtil.copyToFormList(entityList, returnFormList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialOgzForm.class);

		return returnFormList;
	}

	/**
	 * 
	 * @param form
	 * @param coon
	 * @return
	 */
	public MaterialOgzForm getMaterialOgzById(ActionForm form, SqlSession conn) {
		MaterialOgzForm tempForm = (MaterialOgzForm) form;
		String material_id = tempForm.getMaterial_id();

		MaterialOgzMapper dao = conn.getMapper(MaterialOgzMapper.class);
		MaterialOgzEntity entity = dao.getMaterialOgzById(material_id);

		MaterialOgzForm returnForm = new MaterialOgzForm();
		// 复制数据到表单对象
		BeanUtil.copyToForm(entity, returnForm, CopyOptions.COPYOPTIONS_NOEMPTY);

		return returnForm;
	}

	/**
	 *  更新
	 * @param form
	 * @param conn
	 */
	public void update(ActionForm form, SqlSessionManager conn) {
		MaterialOgzEntity entity = new MaterialOgzEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		MaterialOgzMapper dao = conn.getMapper(MaterialOgzMapper.class);

		dao.update(entity);

	}

}

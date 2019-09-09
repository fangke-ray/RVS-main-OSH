package com.osh.rvs.service.partial;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.FactPartialReleaseEntity;
import com.osh.rvs.form.partial.FactPartialReleaseForm;
import com.osh.rvs.mapper.partial.FactPartialReleaseMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 零件出库作业数
 * 
 * @author liuxb
 * 
 */
public class FactPartialReleaseService {
	/**
	 * 新建零件出库作业数
	 * 
	 * @param form
	 * @param conn
	 */
	public void insert(ActionForm form, SqlSessionManager conn) {
		FactPartialReleaseMapper dao = conn.getMapper(FactPartialReleaseMapper.class);

		FactPartialReleaseEntity entity = new FactPartialReleaseEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.insert(entity);
	}

	/**
	 * 更新零件出库作业数
	 * 
	 * @param form
	 * @param conn
	 */
	public void update(ActionForm form, SqlSessionManager conn) {
		FactPartialReleaseMapper dao = conn.getMapper(FactPartialReleaseMapper.class);

		FactPartialReleaseEntity entity = new FactPartialReleaseEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.update(entity);
	}

	public FactPartialReleaseForm getPartialRelease(ActionForm form, SqlSession conn) {
		FactPartialReleaseMapper dao = conn.getMapper(FactPartialReleaseMapper.class);

		FactPartialReleaseEntity connd = new FactPartialReleaseEntity();
		BeanUtil.copyToBean(form, connd, CopyOptions.COPYOPTIONS_NOEMPTY);

		FactPartialReleaseForm respForm = null;
		FactPartialReleaseEntity entity = dao.getPartialRelease(connd);

		if (entity != null) {
			respForm = new FactPartialReleaseForm();
			BeanUtil.copyToForm(entity, respForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		}

		return respForm;
	}

	/**
	 * 更新零件订购单编辑记录
	 * @param af_pf_key
	 * @param operator_id
	 * @param material_id
	 * @param conn
	 */
	public void updatePartialOrderEdit(String af_pf_key, String operator_id,
			String material_id, SqlSessionManager conn) {
		FactPartialReleaseMapper mapper = conn.getMapper(FactPartialReleaseMapper.class);
		
		FactPartialReleaseEntity cond = new FactPartialReleaseEntity();
		cond.setAf_pf_key(af_pf_key);
		cond.setMaterial_id(material_id);
		cond.setOperator_id(operator_id);
		cond.setSpec_kind(0);

		FactPartialReleaseEntity hitEntity = mapper.getPartialRelease(cond);

		if (hitEntity == null) {
			cond.setQuantity(1);
			mapper.insert(cond);
		} else {
			cond.setQuantity(hitEntity.getQuantity() + 1);
			mapper.update(cond);
		}
	}
}

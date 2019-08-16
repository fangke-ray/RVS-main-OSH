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
}

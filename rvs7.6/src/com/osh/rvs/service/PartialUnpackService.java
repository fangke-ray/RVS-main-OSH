package com.osh.rvs.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.master.PartialUnpackEntity;
import com.osh.rvs.form.master.PartialUnpackForm;
import com.osh.rvs.mapper.master.PartialUnpackMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 分装零件
 *
 * @author liuxb
 *
 */
public class PartialUnpackService {

	/**
	 * 新建分装零件记录
	 *
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void insert(ActionForm form, SqlSessionManager conn) throws Exception {
		PartialUnpackMapper dao = conn.getMapper(PartialUnpackMapper.class);
		PartialUnpackEntity entity = new PartialUnpackEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.insert(entity);
	}

	public void update(ActionForm form, SqlSessionManager conn) throws Exception {
		PartialUnpackMapper dao = conn.getMapper(PartialUnpackMapper.class);
		PartialUnpackEntity entity = new PartialUnpackEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.update(entity);
	}

	public void delete(ActionForm form, SqlSessionManager conn) throws Exception {
		PartialUnpackMapper dao = conn.getMapper(PartialUnpackMapper.class);
		PartialUnpackEntity entity = new PartialUnpackEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.delete(entity);
	}

	public PartialUnpackForm getPartialUnpack(ActionForm form, SqlSession conn) {
		PartialUnpackMapper dao = conn.getMapper(PartialUnpackMapper.class);
		PartialUnpackEntity entity = new PartialUnpackEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		entity = dao.getPartialUnpack(entity);

		PartialUnpackForm respForm = null;
		if (entity != null) {
			respForm = new PartialUnpackForm();
			BeanUtil.copyToForm(entity, respForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		}

		return respForm;
	}
}

package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.master.PrivacyEntity;
import com.osh.rvs.form.master.PrivacyForm;
import com.osh.rvs.mapper.master.PrivacyMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PrivacyService {

	/**
	 * 检索记录列表
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return List<PrivacyForm> 查询结果表单
	 */
	public List<PrivacyForm> search(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		PrivacyEntity coditionBean = new PrivacyEntity();
		BeanUtil.copyToBean(form, coditionBean, null);

		// 从数据库中查询记录
		PrivacyMapper dao = conn.getMapper(PrivacyMapper.class);
		List<PrivacyEntity> lResultBean = dao.searchPrivacy(coditionBean);

		// 建立页面返回表单
		List<PrivacyForm> lResultForm = new ArrayList<PrivacyForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, PrivacyForm.class);
		
		return lResultForm;
	}

	/**
	 * 按照主键检索单条记录
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return PrivacyForm 查询结果表单
	 */
	public PrivacyForm getDetail(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		PrivacyEntity coditionBean = new PrivacyEntity();
		BeanUtil.copyToBean(form, coditionBean, null);
		String privacy_id = coditionBean.getPrivacy_id();

		// 从数据库中查询记录
		PrivacyMapper dao = conn.getMapper(PrivacyMapper.class);
		PrivacyEntity cb = dao.getPrivacyByID(privacy_id);

		if (cb == null) {
			// 检索不到的情况下
			MsgInfo error = new MsgInfo();
			error.setComponentid("privacy_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "权限"));
			errors.add(error);
			return null;
		} else {
			// 建立页面返回表单
			PrivacyForm cf = new PrivacyForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(cb, cf, null);
			return cf;
		}
	}

	/**
	 * 执行插入
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @throws Exception
	 */
	public void insert(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		PrivacyEntity insertBean = new PrivacyEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 新建记录插入到数据库中
		PrivacyMapper dao = conn.getMapper(PrivacyMapper.class);
		dao.insertPrivacy(insertBean);
	}

	/**
	 * 执行更新
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @throws Exception
	 */
	public void update(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		PrivacyEntity updateBean = new PrivacyEntity();
		BeanUtil.copyToBean(form, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 更新数据库中记录
		PrivacyMapper dao = conn.getMapper(PrivacyMapper.class);
		dao.updatePrivacy(updateBean);
	}

	/**
	 * 执行逻辑删除
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @throws Exception
	 */
	public void delete(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		PrivacyEntity deleteBean = new PrivacyEntity();
		BeanUtil.copyToBean(form, deleteBean, null);

		// 在数据库中逻辑删除记录
		PrivacyMapper dao = conn.getMapper(PrivacyMapper.class);
		dao.deletePrivacy(deleteBean);
	}
}

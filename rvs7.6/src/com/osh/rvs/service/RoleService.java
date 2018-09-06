package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.PrivacyEntity;
import com.osh.rvs.bean.master.PrivacyGroupEntity;
import com.osh.rvs.bean.master.RoleEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.PrivacyForm;
import com.osh.rvs.form.master.RoleForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.PrivacyMapper;
import com.osh.rvs.mapper.master.RoleMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class RoleService {

	/**
	 * 取得角色选择项标签集
	 * @param conn 数据库连接
	 * @return String 角色选择项标签集
	 */
	public String getOptions(SqlSession conn) {
		RoleMapper dao = conn.getMapper(RoleMapper.class);
		List<RoleEntity> l = dao.getAllRole();
		Map<String, String> codeMap = new HashMap<String, String>();
		for (RoleEntity bean : l) {
			codeMap.put(bean.getRole_id(), bean.getName());
		}
		return CodeListUtils.getSelectOptions(codeMap, null, "", false);
	}

	/**
	 * 检索记录列表
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param privacy_id 拥有权限
	 * @param errors 错误内容列表
	 * @return List<RoleForm> 查询结果表单
	 */
	public List<RoleForm> search(ActionForm form, SqlSession conn, String privacy_id, List<MsgInfo> errors) {

		// 表单复制到数据对象
		RoleEntity conditionBean = new RoleEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// 从数据库中查询记录
		RoleMapper dao = conn.getMapper(RoleMapper.class);
		List<RoleEntity> lResultBean = dao.searchRole(conditionBean, privacy_id);

		// 建立页面返回表单
		List<RoleForm> lResultForm = new ArrayList<RoleForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, RoleForm.class);

		return lResultForm;
	}

	/**
	 * 按照主键检索单条记录
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return RoleForm 查询结果表单
	 */
	public RoleForm getDetail(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		RoleEntity coditionBean = new RoleEntity();
		BeanUtil.copyToBean(form, coditionBean, null);
		String role_id = coditionBean.getRole_id();

		// 从数据库中查询记录
		RoleMapper dao = conn.getMapper(RoleMapper.class);
		RoleEntity resultBean = dao.getRoleByID(role_id);

		if (resultBean == null) {
			// 检索不到的情况下
			MsgInfo error = new MsgInfo();
			error.setComponentid("role_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "角色"));
			errors.add(error);
			return null;
		} else {
			// 建立页面返回表单
			RoleForm resultForm = new RoleForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(resultBean, resultForm, null);

			List<String> pResultBeans = dao.getPrivaciesOfRole(role_id);

			resultForm.setPrivacies(pResultBeans);

			return resultForm;
		}
	}

	/**
	 * 标准检查以外的合法性检查
	 * @param roleForm 表单
	 * @param errors 错误内容列表
	 */
	public void customValidate(RoleForm roleForm, List<MsgInfo> errors) {
		// 必须选择一项权限
		if (roleForm.getPrivacies().size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("privacies");
			error.setErrcode("validator.required.multidetail");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.multidetail", "权限"));
			errors.add(error);
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
	public void insert(RoleForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		RoleEntity insertBean = new RoleEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		insertBean.setUpdated_by(user.getOperator_id());

		// 新建记录插入到数据库中
		RoleMapper dao = conn.getMapper(RoleMapper.class);
		dao.insertRole(insertBean);

		// 取得刚才插入的主键
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		insertBean.setRole_id(commonMapper.getLastInsertID());

		// 拥有权限关系插入到数据库中
		List<String> insertPrivacies = form.getPrivacies();
		for (String sPrivacy_id : insertPrivacies) {
			dao.insertPrivacyOfRole(insertBean.getRole_id(), sPrivacy_id);
		}
	}

	/**
	 * 执行更新
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @throws Exception
	 */
	public void update(RoleForm roleForm, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		RoleEntity updateBean = new RoleEntity();
		BeanUtil.copyToBean(roleForm, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		// 更新数据库中记录
		RoleMapper dao = conn.getMapper(RoleMapper.class);
		dao.updateRole(updateBean);

		// 删除原有权限关系
		dao.deletePrivacyOfRole(updateBean.getRole_id());

		// 拥有权限关系插入到数据库中
		List<String> updatePrivacies = roleForm.getPrivacies();
		for (String sPrivacy_id : updatePrivacies) {
			dao.insertPrivacyOfRole(updateBean.getRole_id(), sPrivacy_id);
		}
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
		RoleEntity deleteBean = new RoleEntity();
		BeanUtil.copyToBean(form, deleteBean, null);

		System.out.println(deleteBean.getRole_id());

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		deleteBean.setUpdated_by(user.getOperator_id());

		// 在数据库中逻辑删除记录
		RoleMapper dao = conn.getMapper(RoleMapper.class);
		dao.deleteRole(deleteBean);
	}

	public List<Integer> getUserPrivacies(String role_id, SqlSession conn) {
		RoleMapper dao = conn.getMapper(RoleMapper.class);
		List<String> pResultBeans = dao.getPrivaciesOfRole(role_id);
		List<Integer> irResultBeans = new ArrayList<Integer>();
		for (String privacy_id :pResultBeans) {
			irResultBeans.add(Integer.parseInt(privacy_id));
		}
		return irResultBeans;
	}

	public void getPrivacyList(HttpServletRequest req, SqlSession conn) {
		// 从数据库中查询记录
		PrivacyMapper dao = conn.getMapper(PrivacyMapper.class);
		List<PrivacyEntity> lpb = dao.getAllPrivacy();

		// 建立页面返回表单
		List<PrivacyForm> lpf = new ArrayList<PrivacyForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lpb, lpf, null, PrivacyForm.class);
		List<String[]> pList = new ArrayList<String[]>();
		for(PrivacyForm pf : lpf) {
			String[] pline = new String[3];
			pline[0] = pf.getId();
			pline[1] = pf.getName();
			pline[2] = pf.getComments();
			pList.add(pline);
		}

		List<PrivacyGroupEntity> pGist = dao.searchPrivacyGroup();
		String pGroupChooser = "";
		for (PrivacyGroupEntity pg : pGist) {
			pGroupChooser += pg.toTr();
		}

		String pReferChooser = CodeListUtils.getReferChooser(pList);
		req.setAttribute("pReferChooser", pReferChooser);
		req.setAttribute("pGroupChooser", pGroupChooser);

	}
}

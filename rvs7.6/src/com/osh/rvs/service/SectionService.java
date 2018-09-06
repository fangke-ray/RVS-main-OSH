package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.SectionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.SectionForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.SectionMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class SectionService {

	/**
	 * 取得课室选择项标签集
	 * @param conn 数据库连接
	 * @return String 课室选择项标签集
	 */
	public String getOptions(SqlSession conn, String empty) {
		return getOptions(conn, empty, "");
	}
	public String getOptions(SqlSession conn, String empty, String defaultValue) {
		  Map<String, String> codeMap = new TreeMap<String, String>();
		  List<SectionEntity> l = getSectionInline(conn);
		  for (SectionEntity bean : l) {
			  codeMap.put(bean.getSection_id(), bean.getName());
		  }
		  return CodeListUtils.getSelectOptions(codeMap, defaultValue, empty, false);
	}
	public List<SectionEntity> getSectionInline(SqlSession conn) {
	  SectionMapper dao = conn.getMapper(SectionMapper.class);
	  return dao.getInlineSection();
	}

	/**
	 * 取得课室选择项标签集/担当人用
	 * @param conn 数据库连接
	 * @return String 课室选择项标签集
	 */
	public String getAllOptions(SqlSession conn) {
		SectionMapper dao = conn.getMapper(SectionMapper.class);
		List<SectionEntity> l = dao.getAllSection();
		Map<String, String> codeMap = new TreeMap<String, String>();
		for (SectionEntity bean : l) {
			codeMap.put(bean.getSection_id(), bean.getName());
		}
		return CodeListUtils.getSelectOptions(codeMap, null, "", false);
	}

	/**
	 * 检索记录列表
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param privacy_id 拥有工位
	 * @param errors 错误内容列表
	 * @return List<SectionForm> 查询结果表单
	 */
	public List<SectionForm> search(ActionForm form, SqlSession conn, String privacy_id, List<MsgInfo> errors) {

		// 表单复制到数据对象
		SectionEntity conditionBean = new SectionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// 从数据库中查询记录
		SectionMapper dao = conn.getMapper(SectionMapper.class);
		List<SectionEntity> lResultBean = dao.searchSection(conditionBean);

		// 建立页面返回表单
		List<SectionForm> lResultForm = new ArrayList<SectionForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, SectionForm.class);

		return lResultForm;
	}

	/**
	 * 按照主键检索单条记录
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return SectionForm 查询结果表单
	 */
	public SectionForm getDetail(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		SectionEntity coditionBean = new SectionEntity();
		BeanUtil.copyToBean(form, coditionBean, null);
		String section_id = coditionBean.getSection_id();

		// 从数据库中查询记录
		SectionMapper dao = conn.getMapper(SectionMapper.class);
		SectionEntity resultBean = dao.getSectionByID(section_id);

		if (resultBean == null) {
			// 检索不到的情况下
			MsgInfo error = new MsgInfo();
			error.setComponentid("section_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "课室"));
			errors.add(error);
			return null;
		} else {
			// 建立页面返回表单
			SectionForm resultForm = new SectionForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(resultBean, resultForm, null);

			List<String> pResultBeans = dao.getPositionsOfSection(section_id);

			resultForm.setPositions(pResultBeans);

			return resultForm;
		}
	}

	/**
	 * 标准检查以外的合法性检查
	 * @param sectionForm 表单
	 * @param errors 错误内容列表
	 */
	public void customValidate(SectionForm sectionForm, List<MsgInfo> errors) {
		// 必须选择一项工位
		if (sectionForm.getPositions().size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("positions");
			error.setErrcode("validator.required.multidetail");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.multidetail", "工位"));
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
	public void insert(SectionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		synchronized (sectionLaPosition) {
			// 表单复制到数据对象
			SectionEntity insertBean = new SectionEntity();
			BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
			insertBean.setUpdated_by(user.getOperator_id());

			// 新建记录插入到数据库中
			SectionMapper dao = conn.getMapper(SectionMapper.class);
			dao.insertSection(insertBean);

			// 取得刚才插入的主键
			CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
			insertBean.setSection_id(commonMapper.getLastInsertID());

			// 拥有工位关系插入到数据库中
			List<String> insertPositions = form.getPositions();
			for (String sPosition_id : insertPositions) {
				dao.insertPositionOfSection(insertBean.getSection_id(), sPosition_id);
			}

			// 清空工位/课室关系
			sectionLaPosition.clear();
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
	public void update(SectionForm sectionForm, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		synchronized (sectionLaPosition) {
			// 表单复制到数据对象
			SectionEntity updateBean = new SectionEntity();
			BeanUtil.copyToBean(sectionForm, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);

			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
			updateBean.setUpdated_by(user.getOperator_id());

			// 更新数据库中记录
			SectionMapper dao = conn.getMapper(SectionMapper.class);
			dao.updateSection(updateBean);

			// 删除原有工位关系
			dao.deletePositionOfSection(updateBean.getSection_id());

			// 拥有工位关系插入到数据库中
			List<String> updatePositions = sectionForm.getPositions();
			for (String sPosition_id : updatePositions) {
				dao.insertPositionOfSection(updateBean.getSection_id(), sPosition_id);
			}

			// 清空工位/课室关系
			sectionLaPosition.clear();
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
		synchronized (sectionLaPosition) {
			// 表单复制到数据对象
			SectionEntity deleteBean = new SectionEntity();
			BeanUtil.copyToBean(form, deleteBean, null);

			System.out.println(deleteBean.getSection_id());

			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
			deleteBean.setUpdated_by(user.getOperator_id());

			// 在数据库中逻辑删除记录
			SectionMapper dao = conn.getMapper(SectionMapper.class);
			dao.deleteSection(deleteBean);

			// 清空工位/课室关系
			sectionLaPosition.clear();
		}
	}

	/**
	 * 查询人员可到达课室
	 * @param operator_id
	 * @param conn
	 * @return
	 */
	public List<SectionEntity> getSectionsByOperate(String operator_id,
			SqlSession conn) {
		SectionMapper mapper = conn.getMapper(SectionMapper.class);
		return mapper.getSectionByOperator(operator_id);
	}

	private static Map<String, List<String>> sectionLaPosition = new HashMap<String, List<String>>();

	/**
	 * 取得存在工位的课室信息
	 * @param section_id 延续前的课室
	 * @param position_id 要延续到的工位
	 * @param conn
	 * @return 存在的课室
	 */
	public String getSectionLaPosition(String section_id, String position_id,
			SqlSession conn) {
		synchronized (sectionLaPosition) {
			if (sectionLaPosition.isEmpty()) {
				resetSectionLaPosition(conn);
			}

			List<String> sections = sectionLaPosition.get(position_id);

			// 无关联，则按原设
			if (sections == null || sections.size() == 0) return section_id;

			for (String section : sections) {
				// 有匹配则延续前课室
				if (section.equals(section_id)) return section_id;
			}
			// 无匹配自动采用第一个合法课室
			return sections.get(0);
		}
	}

	private void resetSectionLaPosition(SqlSession conn) {
		SectionMapper mapper = conn.getMapper(SectionMapper.class);
	}
}

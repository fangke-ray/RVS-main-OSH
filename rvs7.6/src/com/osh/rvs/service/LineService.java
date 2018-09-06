package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.LineEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.LineForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.LineMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class LineService {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 取得工程选择项标签集
	 * @param conn 数据库连接
	 * @return String 工程选择项标签集
	 */
	public String getOptions(SqlSession conn) {
		return getOptions(conn, "(不选)", "");
	}
	public String getOptions(SqlSession conn, String empty, String defaultValue) {
		LineMapper dao = conn.getMapper(LineMapper.class);
		LineEntity flg = new LineEntity();
		List<LineEntity> l = dao.searchLine(flg);
		Map<String, String> codeMap = new TreeMap<String, String>();
		for (LineEntity bean : l) {
			codeMap.put(bean.getLine_id(), bean.getName());
		}
		return CodeListUtils.getSelectOptions(codeMap, defaultValue, empty, false);
	}

	/**
	 * 取得工程选择项标签集
	 * @param conn 数据库连接
	 * @return String 工程选择项标签集
	 */
	public String getInlineOptions(SqlSession conn) {
		LineMapper dao = conn.getMapper(LineMapper.class);
		LineEntity condi = new LineEntity();
		condi.setInline_flg(true);
		List<LineEntity> l = dao.searchLine(condi);
		Map<String, String> codeMap = new TreeMap<String, String>();
		for (LineEntity bean : l) {
			codeMap.put(bean.getLine_id(), bean.getName());
		}
		return CodeListUtils.getSelectOptions(codeMap, null, "", false);
	}

	/**
	 * 检索记录列表
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return List<LineForm> 查询结果表单
	 */
	public List<LineForm> search(ActionForm form, SqlSession conn, List<MsgInfo> errors) {

		// 表单复制到数据对象
		LineEntity conditionBean = new LineEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// 从数据库中查询记录
		LineMapper dao = conn.getMapper(LineMapper.class);
		List<LineEntity> lResultBean = dao.searchLine(conditionBean);

		// 建立页面返回表单
		List<LineForm> lResultForm = new ArrayList<LineForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, LineForm.class);

		return lResultForm;
	}

	/**
	 * 按照主键检索单条记录
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return LineForm 查询结果表单
	 */
	public LineForm getDetail(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		LineEntity coditionBean = new LineEntity();
		BeanUtil.copyToBean(form, coditionBean, null);
		String line_id = coditionBean.getLine_id();

		// 从数据库中查询记录
		LineMapper dao = conn.getMapper(LineMapper.class);
		LineEntity resultBean = dao.getLineByID(line_id);

		if (resultBean == null) {
			// 检索不到的情况下
			MsgInfo error = new MsgInfo();
			error.setComponentid("line_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "工程"));
			errors.add(error);
			return null;
		} else {
			// 建立页面返回表单
			LineForm resultForm = new LineForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(resultBean, resultForm, null);

			return resultForm;
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
		LineEntity insertBean = new LineEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		insertBean.setUpdated_by(user.getOperator_id());

		// 新建记录插入到数据库中
		LineMapper dao = conn.getMapper(LineMapper.class);
		dao.insertLine(insertBean);

		// 取得刚才插入的主键
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		insertBean.setLine_id(commonMapper.getLastInsertID());
	}

	/**
	 * 执行更新
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @throws Exception
	 */
	public void update(ActionForm lineForm, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		LineEntity updateBean = new LineEntity();
		BeanUtil.copyToBean(lineForm, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		// 更新数据库中记录
		LineMapper dao = conn.getMapper(LineMapper.class);
		dao.updateLine(updateBean);
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
		LineEntity deleteBean = new LineEntity();
		BeanUtil.copyToBean(form, deleteBean, null);

		log.info(deleteBean.getLine_id());

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		deleteBean.setUpdated_by(user.getOperator_id());

		// 在数据库中逻辑删除记录
		LineMapper dao = conn.getMapper(LineMapper.class);
		dao.deleteLine(deleteBean);
	}
}

package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.PositionForm;
import com.osh.rvs.mapper.master.PositionMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PositionService {

	private static Set<String> dividePositions = null;
	private static String inlineOptions = null;

	/**
	 * 检索记录列表
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return List<PositionForm> 查询结果表单
	 */
	public List<PositionForm> search(ActionForm form, SqlSession conn, List<MsgInfo> errors) {

		// 表单复制到数据对象
		PositionEntity conditionBean = new PositionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// 从数据库中查询记录
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		List<PositionEntity> lResultBean = dao.searchPosition(conditionBean);

		// 建立页面返回表单
		List<PositionForm> lResultForm = new ArrayList<PositionForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, PositionForm.class);

		return lResultForm;
	}

	/**
	 * 按照主键检索单条记录
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return PositionForm 查询结果表单
	 */
	public PositionForm getDetail(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		PositionEntity coditionBean = new PositionEntity();
		BeanUtil.copyToBean(form, coditionBean, null);
		String position_id = coditionBean.getPosition_id();

		// 从数据库中查询记录
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		PositionEntity resultBean = dao.getPositionByID(position_id);

		if (resultBean == null) {
			// 检索不到的情况下
			MsgInfo error = new MsgInfo();
			error.setComponentid("position_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "角色"));
			errors.add(error);
			return null;
		} else {
			// 建立页面返回表单
			PositionForm resultForm = new PositionForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(resultBean, resultForm, null);

			return resultForm;
		}
	}

	/**
	 * 标准检查以外的合法性检查
	 * @param positionForm 表单
	 * @param errors 错误内容列表
	 */
	public void customValidate(PositionForm positionForm, SqlSession conn, List<MsgInfo> errors) {
		// 工位ID不重复
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		// 表单复制到数据对象
		PositionEntity conditionBean = new PositionEntity();
		BeanUtil.copyToBean(positionForm, conditionBean, (new CopyOptions()).include("id", "process_code"));
		List<PositionEntity> resultBean = dao.searchPosition(conditionBean);
		if (resultBean != null && resultBean.size() > 0 && !resultBean.get(0).getPosition_id().equals(conditionBean.getPosition_id())) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("process_code");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "进度代码",
					conditionBean.getProcess_code(), "工位"));
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
	public void insert(PositionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		PositionEntity insertBean = new PositionEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		if(insertBean.getLight_division_flg()==null || insertBean.getLight_division_flg()==2){//独立小修理工位标记 空,否
			insertBean.setLight_division_flg(0);
		}

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		insertBean.setUpdated_by(user.getOperator_id());

		// 新建记录插入到数据库中
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		dao.insertPosition(insertBean);

		dividePositions = null;
		inlineOptions = null;
	}

	/**
	 * 执行更新
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @throws Exception
	 */
	public void update(PositionForm positionForm, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		PositionEntity updateBean = new PositionEntity();
		BeanUtil.copyToBean(positionForm, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		if(updateBean.getLight_division_flg()==2){//独立小修理工位标记 否
			updateBean.setLight_division_flg(0);
		}

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		// 更新数据库中记录
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		dao.updatePosition(updateBean);

		dividePositions = null;
		inlineOptions = null;
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
		PositionEntity deleteBean = new PositionEntity();
		BeanUtil.copyToBean(form, deleteBean, null);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		deleteBean.setUpdated_by(user.getOperator_id());

		// 在数据库中逻辑删除记录
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		dao.deletePosition(deleteBean);
		dividePositions = null;
		inlineOptions = null;
	}

	/**
	 * 取得全工位
	 * @param conn
	 * @return
	 */
	public List<PositionEntity> getAllPosition(SqlSession conn) {
		
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		List<PositionEntity> lResultBean = dao.getAllPosition();
		
		return lResultBean;
	}
	
	/**
	 * 取得工位参照选择项
	 * @param conn
	 * @return
	 */
	public String getOptions(SqlSession conn) {
		List<String[]> lst = new ArrayList<String[]>();
		
		List<PositionEntity> allPosition = this.getAllPosition(conn);
		
		for (PositionEntity position: allPosition) {
			String[] p = new String[3];
			p[0] = position.getPosition_id();
			p[1] = position.getName();
			p[2] = position.getProcess_code();
			lst.add(p);
		}
		
		String pReferChooser = CodeListUtils.getReferChooser(lst);
		
		return pReferChooser;
	}

	/**
	 * 根据主键查找工位信息
	 * @param position_id
	 * @param conn
	 * @return
	 */
	public PositionEntity getPositionEntityByKey(String position_id, SqlSession conn) {
		// 从数据库中查询记录
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		PositionEntity resultBean = dao.getPositionByID(position_id);
		return resultBean;
	}

	/**
	 * 取得工位
	 * @param conn
	 * @return
	 */
	public List<PositionEntity> getPositionByInlineFlg(SqlSession conn) {
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		List<PositionEntity> lResultBean = dao.getPositionByInlineFlg();

		return lResultBean;
	}

	/**
	 * 取得分平行线工位
	 * @param conn
	 * @return
	 */
	public static Set<String> getDividePositions(SqlSession conn) {
		if (dividePositions == null) {
			dividePositions = new HashSet<String>();
			PositionMapper mapper = conn.getMapper(PositionMapper.class);
			List<String> l = mapper.getDividePositions();
			for (String pos_id : l) {
				dividePositions.add(pos_id);
			}
		}
		return dividePositions;
	}

	/**
	 * 取得在线工位可选择项
	 * @param conn
	 * @return
	 */
	public String getInlineOptions(SqlSession conn) {
		if (inlineOptions == null) {
			StringBuffer optStr = new StringBuffer();
			
			List<PositionEntity> inlinePositions = this.getPositionByInlineFlg(conn);
			
			for (PositionEntity position: inlinePositions) {
				optStr.append("<option value='"+position.getPosition_id()+"'>"+position.getProcess_code()+"</option>");
			}

			inlineOptions = optStr.toString();
		}

		return inlineOptions;
	}
}

package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.master.OperatorNotifyEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.RoleEntity;
import com.osh.rvs.bean.master.SectionEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.OperatorForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.OperatorMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.CryptTool;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class OperatorService {

	/**
	 * 检索记录列表
	 * 
	 * @param form
	 *            提交表单
	 * @param conn
	 *            数据库连接
	 * @param privacy_id
	 *            拥有权限
	 * @param errors
	 *            错误内容列表
	 * @return List<OperatorForm> 查询结果表单
	 */
	public List<OperatorForm> search(ActionForm form, SqlSession conn, List<MsgInfo> errors) {

		// 表单复制到数据对象
		OperatorEntity conditionBean = new OperatorEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// 从数据库中查询记录
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		List<OperatorNamedEntity> lResultBean = dao.searchOperator(conditionBean);

		// 建立页面返回表单
		List<OperatorForm> lResultForm = new ArrayList<OperatorForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, OperatorForm.class);

		return lResultForm;
	}

	/**
	 * 按照主键检索单条记录用于编辑
	 * 
	 * @param form
	 *            提交表单
	 * @param conn
	 *            数据库连接
	 * @param errors
	 *            错误内容列表
	 * @return OperatorForm 查询结果表单
	 */
	public OperatorForm getShowedit(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		OperatorEntity coditionBean = new OperatorEntity();
		BeanUtil.copyToBean(form, coditionBean, null);
		String operator_id = coditionBean.getOperator_id();

		// 从数据库中查询记录
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		OperatorEntity resultBean = dao.getOperatorByID(operator_id);

		if (resultBean == null || resultBean.isDelete_flg()) {
			// 检索不到的情况下
			MsgInfo error = new MsgInfo();
			error.setComponentid("operator_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "用户"));
			errors.add(error);
			return null;
		} else {
			// 建立页面返回表单
			OperatorForm resultForm = new OperatorForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(resultBean, resultForm, null);

			// 取得主要工位
			List<String> pResultBeans = dao.getPositionsOfOperator(operator_id, "1");
			if (pResultBeans.size() == 0 && !CommonStringUtil.isEmpty(resultBean.getPosition_id())) {
				pResultBeans.add(resultBean.getPosition_id());
			}
			resultForm.setMain_positions(pResultBeans);

			// 取得可选工位
			pResultBeans = dao.getPositionsOfOperator(operator_id, "0");
			resultForm.setAbilities(pResultBeans);

			// 取得关注工位
			pResultBeans = dao.getPositionsOfOperator(operator_id, "2");
			resultForm.setNotice_positions(pResultBeans);

			List<String> afAbilities = dao.getAfAbilitiesOfOperator(operator_id);
			// 取得间接作业能力
			resultForm.setAf_abilities(afAbilities);

			// 取得兼任权限
			List<String> rResultBeans = dao.getRolesOfOperator(operator_id);
			resultForm.setTemp_role(rResultBeans);

			return resultForm;
		}
	}

	/**
	 * 按照主键检索单条记录
	 * 
	 * @param form
	 *            提交表单
	 * @param conn
	 *            数据库连接
	 * @param errors
	 *            错误内容列表
	 * @return OperatorForm 查询结果表单
	 */
	public OperatorForm getDetail(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		OperatorEntity coditionBean = new OperatorEntity();
		BeanUtil.copyToBean(form, coditionBean, null);
		String operator_id = coditionBean.getOperator_id();

		// 从数据库中查询记录
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		OperatorEntity resultBean = dao.getOperatorNamedByID(operator_id);

		if (resultBean == null) {
			// 检索不到的情况下
			MsgInfo error = new MsgInfo();
			error.setComponentid("operator_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "用户"));
			errors.add(error);
			return null;
		} else {
			// 建立页面返回表单
			OperatorForm resultForm = new OperatorForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(resultBean, resultForm, null);

			// 取得主要工位
			List<String> pResultBeans = dao.getPositionsOfOperator(operator_id, "1");
			if (pResultBeans.size() == 0 && !CommonStringUtil.isEmpty(resultBean.getPosition_id())) {
				pResultBeans.add(resultBean.getPosition_id());
			}
			resultForm.setMain_positions(pResultBeans);

			// 取得可选工位
			pResultBeans = dao.getPositionsOfOperator(operator_id, "0");
			resultForm.setAbilities(pResultBeans);

			List<String> afAbilities = dao.getAfAbilitiesOfOperator(operator_id);
			// 取得间接作业能力
			resultForm.setAf_abilities(afAbilities);

			return resultForm;
		}
	}

	/**
	 * 标准检查以外的合法性检查
	 * 
	 * @param operatorForm
	 *            表单
	 * @param errors
	 *            错误内容列表
	 */
	public void customValidate(OperatorForm operatorForm, SqlSession conn, List<MsgInfo> errors) {
		// 用户角色是操作工的情况下,必须至少选择一项技能(工位) TODO 按权限
		if ("00000000006".equals(operatorForm.getRole_id()) && operatorForm.getMain_positions().size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("position_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "主要工位"));
			errors.add(error);
		}
		// 用户角色是线长的情况下,必须选择课室和工程
		if ("00000000005".equals(operatorForm.getRole_id()) && CommonStringUtil.isEmpty(operatorForm.getSection_id())) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("section_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "课室"));
			errors.add(error);
		}
		if ("00000000005".equals(operatorForm.getRole_id()) && CommonStringUtil.isEmpty(operatorForm.getLine_id())) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("line_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "工程"));
			errors.add(error);
		}
		// 工号不可重复
		if (errors.size() == 0 && CommonStringUtil.isEmpty(operatorForm.getId())) {
			OperatorMapper dao = conn.getMapper(OperatorMapper.class);
			// 表单复制到数据对象
			OperatorEntity conditionBean = new OperatorEntity();
			BeanUtil.copyToBean(operatorForm, conditionBean, (new CopyOptions()).include("job_no"));
			List<OperatorNamedEntity> resultBean = dao.searchOperator(conditionBean);
			if (resultBean != null && resultBean.size() > 0) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("job_no");
				error.setErrcode("dbaccess.columnNotUnique");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "工号",
						conditionBean.getJob_no(), "用户"));
				errors.add(error);
			}
		}
		// 主要工位和可负责工位不可重复
		for (String sPosition : operatorForm.getMain_positions()) {
			if (operatorForm.getAbilities().contains(sPosition)) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("position_id");
				error.setErrmsg("主要负责工位和可负责工位重复。");
				errors.add(error);
				break;
			}
		}
	}

	/**
	 * 执行插入
	 * 
	 * @param form
	 *            提交表单
	 * @param session
	 *            当前用户会话
	 * @param conn
	 *            数据库连接
	 * @param errors
	 *            错误内容列表
	 * @throws Exception
	 */
	public void insert(OperatorForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		// 新建自动生成密码 TODO
		form.setPwd("111asD");

		// 表单复制到数据对象
		OperatorEntity insertBean = new OperatorEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		insertBean.setPwd(CryptTool.encrypttoStr(insertBean.getPwd() + insertBean.getJob_no().toUpperCase()));

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		insertBean.setUpdated_by(user.getOperator_id());

		// 新建记录插入到数据库中
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		dao.insertOperator(insertBean);

		// 取得刚才插入的主键
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		insertBean.setOperator_id(commonMapper.getLastInsertID());

		// 拥有权限关系插入到数据库中
		// 关注工位
		List<String> noticeMainPositions = form.getNotice_positions();

		// 主要工位
		List<String> insertMainPositions = form.getMain_positions();
		for (String sPosition_id : insertMainPositions) {
			if (noticeMainPositions.contains(sPosition_id)) {
				dao.insertPositionOfOperator(insertBean.getOperator_id(), sPosition_id, "3");
			} else {
				dao.insertPositionOfOperator(insertBean.getOperator_id(), sPosition_id, "1");
			}
		}
		// 可选工位
		List<String> insertAbilities = form.getAbilities();
		for (String sPosition_id : insertAbilities) {
			if (noticeMainPositions.contains(sPosition_id)) {
				dao.insertPositionOfOperator(insertBean.getOperator_id(), sPosition_id, "2");
			} else {
				dao.insertPositionOfOperator(insertBean.getOperator_id(), sPosition_id, "0");
			}
		}

		// 可选间接作业
		List<String> updateAfAbilities = form.getAf_abilities();
		for (String pType : updateAfAbilities) {
			dao.insertAfAbilitiesOfOperator(insertBean.getOperator_id(), pType);
		}

		// 兼任角色关系插入到数据库中
		List<String> temproles = form.getTemp_role();
		for (String role_id : temproles) {
			dao.insertRoleOfOperator(insertBean.getOperator_id(), role_id, "9999/12/31");
		}
	}

	/**
	 * 执行更新
	 * 
	 * @param form
	 *            提交表单
	 * @param session
	 *            当前用户会话
	 * @param conn
	 *            数据库连接
	 * @param errors
	 *            错误内容列表
	 * @throws Exception
	 */
	public void update(OperatorForm operatorForm, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		// 表单复制到数据对象
		OperatorEntity updateBean = new OperatorEntity();
		BeanUtil.copyToBean(operatorForm, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		// 更新数据库中记录
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		dao.updateOperator(updateBean);

		// 删除原有权限关系
		dao.deletePositionOfOperator(updateBean.getOperator_id());
		dao.deleteAfAbiltiesOfOperator(updateBean.getOperator_id());

		// 拥有权限关系插入到数据库中
		// 关注工位
		List<String> noticeMainPositions = operatorForm.getNotice_positions();

		// 主要工位
		List<String> insertMainPositions = operatorForm.getMain_positions();
		for (String sPosition_id : insertMainPositions) {
			if (noticeMainPositions.contains(sPosition_id)) {
				dao.insertPositionOfOperator(updateBean.getOperator_id(), sPosition_id, "3");
			} else {
				dao.insertPositionOfOperator(updateBean.getOperator_id(), sPosition_id, "1");
			}
		}
		// 可选工位
		List<String> insertAbilities = operatorForm.getAbilities();
		for (String sPosition_id : insertAbilities) {
			if (noticeMainPositions.contains(sPosition_id)) {
				dao.insertPositionOfOperator(updateBean.getOperator_id(), sPosition_id, "2");
			} else {
				dao.insertPositionOfOperator(updateBean.getOperator_id(), sPosition_id, "0");
			}
		}

		// 可选间接作业
		List<String> updateAfAbilities = operatorForm.getAf_abilities();
		for (String pType : updateAfAbilities) {
			dao.insertAfAbilitiesOfOperator(updateBean.getOperator_id(), pType);
		}

		// 删除原有兼任角色关系
		dao.deleteRoleOfOperator(updateBean.getOperator_id());

		// 兼任角色关系插入到数据库中
		List<String> temproles = operatorForm.getTemp_role();
		for (String role_id : temproles) {
			dao.insertRoleOfOperator(updateBean.getOperator_id(), role_id, "9999/12/31");
		}
	}

	/**
	 * 执行逻辑删除
	 * 
	 * @param form
	 *            提交表单
	 * @param session
	 *            当前用户会话
	 * @param conn
	 *            数据库连接
	 * @param errors
	 *            错误内容列表
	 * @throws Exception
	 */
	public void delete(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		// 表单复制到数据对象
		OperatorEntity deleteBean = new OperatorEntity();
		BeanUtil.copyToBean(form, deleteBean, null);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		deleteBean.setUpdated_by(user.getOperator_id());

		// 在数据库中逻辑删除记录
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		dao.deleteOperator(deleteBean);
	}

	public void generatepasswd(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		// 表单复制到数据对象
		OperatorEntity updateBean = new OperatorEntity();
		BeanUtil.copyToBean(form, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		updateBean.setPwd(CryptTool.encrypttoStr(updateBean.getPwd() + updateBean.getJob_no().toUpperCase()));

		// 在数据库中更新密码
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		dao.updatePassword(updateBean);
	}

	public Map<String, String> getUserRoles(String operator_id, SqlSession conn) {
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		List<RoleEntity> rResultBeans = dao.getRolesOfOperatorNamed(operator_id);

		Map<String, String> ret = new TreeMap<String, String>();

		for (RoleEntity rResultBean : rResultBeans) {
			ret.put(rResultBean.getRole_id(), rResultBean.getName());
		}
		return ret;
	}

	// 取得全部工位
	public List<PositionEntity> getUserPositions(String operator_id, SqlSession conn) {
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		List<PositionEntity> pResultBeans = dao.getPositionsOfOperatorNamed(operator_id);
		pResultBeans.addAll(dao.getGroupPositionsOfOperatorNamed(operator_id));

		return pResultBeans;
	}

	public String getResolverReferChooser(SqlSession conn) {
		List<String[]> lst = new ArrayList<String[]>();

		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		List<OperatorNamedEntity> allOperator = dao.getResolver();

		for (OperatorNamedEntity operator : allOperator) {
			String[] p = new String[3];
			p[0] = operator.getOperator_id();
			p[1] = operator.getName();
			p[2] = operator.getRole_name();
			lst.add(p);
		}

		String pReferChooser = CodeListUtils.getReferChooser(lst);

		return pReferChooser;
	}

	// 取得品保担当人
	public String getOptions(SqlSession conn) {
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		int privacy_id =RvsConsts.PRIVACY_QUALITY_ASSURANCE;
		List<OperatorEntity> list = dao.getOperatorWithPrivacy(privacy_id);
		Map<String, String> map = new TreeMap<String, String>();

		for (OperatorEntity bean : list) {
			map.put(bean.getOperator_id(), bean.getName());
		}

		return CodeListUtils.getSelectOptions(map, null, "", false);
	}
	
	//取得所有的操作人员
	public String getAllOperatorName(SqlSession conn){
		List<String[]> lst = new ArrayList<String[]>();

		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		List<OperatorNamedEntity> allOperator = dao.searchOperator(null);

		for (OperatorNamedEntity operator : allOperator) {
			String[] p = new String[4];
			p[0] = operator.getOperator_id();
			p[1] = operator.getName();
			p[2] = operator.getRole_name();
			if(CommonStringUtil.isEmpty(operator.getLine_name())){
				p[3] ="";
			}else{
				p[3] = operator.getLine_name();
			}
			
			lst.add(p);
		}

		String pReferChooser = CodeListUtils.getReferChooser(lst);

		return pReferChooser;
	}
	
   //取得所有治具点检人员
	public String getAllToolsOperatorName(SqlSession conn){
		List<String[]> lst = new ArrayList<String[]>();

		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		List<OperatorNamedEntity> allOperator = dao.searchToolsOperator(null);

		for (OperatorNamedEntity operator : allOperator) {
			String[] p = new String[4];
			p[0] = operator.getOperator_id();
			p[1] = operator.getName();
			p[2] = operator.getRole_name();
			if(CommonStringUtil.isEmpty(operator.getLine_name())){
				p[3] = "";
			}else{
				p[3] = operator.getLine_name();
			}
			
			lst.add(p);
		}

		String pReferChooser = CodeListUtils.getReferChooser(lst);

		return pReferChooser;
	}

	public List<PositionEntity> getUserAfAbilities(String operator_id,
			SqlSession conn) {
		OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
		List<PositionEntity> retList = new ArrayList<PositionEntity>();

		List<String> codes = mapper.getAfAbilitiesOfOperator(operator_id);

		for (String code : codes) {
			PositionEntity aaEntity = new PositionEntity();
			aaEntity.setPosition_id(code);
			if (AcceptFactService.typeMap.containsKey(code)) {
				aaEntity.setLine_name(AcceptFactService.moduleMap.get(code.substring(0, 2)));
				aaEntity.setProcess_code(AcceptFactService. typeMap.get(code));
				retList.add(aaEntity);
			}
		}

		return retList;
	}

	public static boolean hasAfAbility(List<PositionEntity> afAbilities,
			String ability_code) {
		for (PositionEntity afAbility : afAbilities) {
			if (afAbility.getPosition_id().equals(ability_code)) {
				return true;
			}
		}
		return false;
	}

	private List<OperatorNotifyEntity> operatorNotifyEntities = null;
	public List<OperatorNotifyEntity> getOperatorNotifyEntity(SqlSession conn) {
		if (operatorNotifyEntities == null) {
			OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
			operatorNotifyEntities = mapper.getOperatorNotifyEntity();
		}
		return operatorNotifyEntities;
	}

	public void setNotify(Map<String, String[]> parameterMap,
			SqlSessionManager conn, List<MsgInfo> errors) {
		List<OperatorNotifyEntity> onEntities = new AutofillArrayList<OperatorNotifyEntity>(OperatorNotifyEntity.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("notify".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					// TODO 全
					if ("position_id".equals(column)) {
						onEntities.get(icounts).setPosition_id(value[0]);
					} else if ("category_id".equals(column)) {
						onEntities.get(icounts).setCategory_id(value[0]);
					} else if ("operator_id".equals(column)) {
						onEntities.get(icounts).setOperator_id(value[0]);
					} else if ("manager_operator_id".equals(column)) {
						onEntities.get(icounts).setManager_operator_id(value[0]);
					}
				}
			}
		}

		OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
		mapper.deleteOperatorNotify();

		for (OperatorNotifyEntity onEntity : onEntities) {
			if ("".equals(onEntity.getOperator_id())) onEntity.setOperator_id(null);
			if ("".equals(onEntity.getManager_operator_id())) onEntity.setManager_operator_id(null);
			mapper.insertOperatorNotify(onEntity);
		}

		operatorNotifyEntities = null;
	}

	public String notifyAnmlBreak(String category_id,
			String position_id, SqlSession conn) {
		String managerId = null, defaultManagerId = null;
		List<OperatorNotifyEntity> operatorNotifyEntities = this.getOperatorNotifyEntity(conn);
		for (OperatorNotifyEntity entity : operatorNotifyEntities) {
			if (position_id.equals(entity.getPosition_id())) {
				if ("00000000000".equals(entity.getCategory_id())) {
					defaultManagerId = entity.getManager_operator_id();
				} else if (category_id.equals(entity.getCategory_id())) {
					managerId = entity.getManager_operator_id();
				}
			}
		}

		if (managerId != null) {
			return managerId;
		}

		return defaultManagerId;
	}

	// 离线登记
	private static Map<String, List<String>> offPositionList = null;
	// 离线上限
	private static Map<String, Integer> offPositionLimit = new HashMap<String, Integer>();

	/**
	 * 清除离岗证内存记录
	 * @param conn
	 */
	public static void resetOffPositionList(SqlSession conn) {
		offPositionList = new HashMap<String, List<String>>();

		OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
		List<OperatorEntity> lOffPos = mapper.getOffPositions();
		for (OperatorEntity offPos : lOffPos) {
			String lineKey = offPos.getSection_id() + "+" + offPos.getLine_id();
			if (!offPositionList.containsKey(lineKey)) {
				offPositionList.put(lineKey, new ArrayList<String>());
			}
			offPositionList.get(lineKey).add(offPos.getOperator_id());
		}
	}

	public static void resetOffPositionLimit() {
		offPositionLimit.clear();
	}

	/**
	 * 查询离岗证状态与数量
	 * @param user
	 * @param callbackResponse
	 * @param conn
	 */
	public void getOffposPermit(LoginData user,
			Map<String, Object> callbackResponse, SqlSession conn) {

		String userLineKey = user.getSection_id() + "+" + user.getLine_id();

		if (offPositionList == null) {
			resetOffPositionList(conn);
		}

		int posgot = 0;
		List<String> lLine = offPositionList.get(userLineKey);
		if (lLine != null) {
			if (lLine.contains(user.getOperator_id())) {
				OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
				Date offTime = mapper.getOffPositionByOperator(user.getOperator_id());
				callbackResponse.put("self", user.getName());
				callbackResponse.put("off_time", DateUtil.toString(offTime, DateUtil.ISO_TIME_PATTERN));
				return;
			} else {
				posgot = lLine.size();
			}
		}

		// 离岗证数量
		int limit = getOffPositionLimit(user);

		if (posgot < limit) {
			callbackResponse.put("permit", (limit - posgot) + "/" + limit);
		}
	}

	/**
	 * 设定当前用户离岗
	 * @param user
	 * @param errors
	 * @param callbackResponse
	 * @param conn
	 */
	public void setOffpos(LoginData user,
			List<MsgInfo> errors, Map<String, Object> callbackResponse, SqlSessionManager conn) {

		String userLineKey = user.getSection_id() + "+" + user.getLine_id();

		if (offPositionList == null) {
			resetOffPositionList(conn);
		}

		int posgot = 0;
		List<String> lLine = offPositionList.get(userLineKey);
		if (lLine != null) {
			if (lLine.contains(user.getOperator_id())) {
				OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
				Date offTime = mapper.getOffPositionByOperator(user.getOperator_id());
				callbackResponse.put("self", user.getName());
				callbackResponse.put("off_time", DateUtil.toString(offTime, DateUtil.ISO_TIME_PATTERN));
				return;
			} else {
				posgot = lLine.size();
			}
		}
	
		int limit = getOffPositionLimit(user);
		if (posgot + 1 > limit) {
			MsgInfo e = new MsgInfo();
			e.setErrmsg("本工程当前没有足够可用的离岗证，请稍后再试。");
			errors.add(e);
			return;
		}

		OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
		OperatorEntity insertBean = new OperatorEntity();
		insertBean.setOperator_id(user.getOperator_id());
		insertBean.setSection_id(user.getSection_id());
		insertBean.setLine_id(user.getLine_id());
		mapper.insertOffPositions(insertBean);

		lLine = offPositionList.get(userLineKey);
		if (lLine == null) {
			offPositionList.put(userLineKey, new ArrayList<String>());
			lLine = offPositionList.get(userLineKey);
		}

		lLine.add(user.getOperator_id());

		callbackResponse.put("self", user.getName());
		callbackResponse.put("off_time", DateUtil.toString(new Date(), DateUtil.ISO_TIME_PATTERN));
	}

	/**
	 * 回归岗位
	 * @param user
	 * @param conn
	 */
	public void closeOffpos(LoginData user, SqlSessionManager conn) {

		OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
		mapper.deleteOffPositions(user.getOperator_id());

		resetOffPositionList(conn);
	}

	/**
	 * 从配置取得所在工程离岗证数量
	 * @param user
	 * @return
	 */
	private int getOffPositionLimit(LoginData user) {
		// offPosition.报价物料课.受理报价 = 2
		String key = "offPosition." + user.getSection_name() + "." + user.getLine_name();
		if (offPositionLimit.containsKey(key)) {
			return offPositionLimit.get(key);
		}
		String limitProperty = PathConsts.POSITION_SETTINGS.getProperty(key);
		if (limitProperty == null) {
			return 1; 
		} else {
			int iLimit = 1;
			try {
				iLimit = Integer.parseInt(limitProperty.trim());
			} catch (Exception e) {
			}
			offPositionLimit.put(key, iLimit);
			return iLimit;
		}
	}

	/**
	 * 查询人员可到达课室
	 * @param operator_id
	 * @param section_id
	 * @param conn
	 * @return
	 */
	public List<SectionEntity> getSectionsByOperator(LoginData user,
			SqlSession conn) {
		List<SectionEntity> retSection = new ArrayList<SectionEntity>();
		SectionEntity selfSection = new SectionEntity();
		if (user.getSection_id() == null) {
			return retSection;
		}
		selfSection.setSection_id(user.getSection_id());
		selfSection.setName(user.getSection_name());
		retSection.add(selfSection);

		if (!RvsConsts.ROLE_MANAGER.equals(user.getRole_id())
				&& !RvsConsts.ROLE_SYSTEM.equals(user.getRole_id())) {

			if (RvsConsts.ROLE_LINELEADER.equals(user.getRole_id())
					|| RvsConsts.ROLE_QA_MANAGER.equals(user.getRole_id())) {
				SectionService sService = new SectionService();
				return sService.getSectionsByOperate(user.getOperator_id(), conn);
			}

			OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
			OperatorEntity entity = new OperatorEntity();
			entity.setOperator_id(user.getOperator_id());
			entity.setExpire_date(new Date());
			List<OperatorEntity> sectionPermits = mapper.findOperatorSection(entity);
			for (OperatorEntity sectionPermit : sectionPermits) {
				if (!selfSection.getSection_id().equals(sectionPermit.getSection_id())) {
					SectionEntity section = new SectionEntity();
					section.setSection_id(sectionPermit.getSection_id());
					section.setName(sectionPermit.getSection_name());
					retSection.add(section);
				}
			}
		}
	
		return retSection;
	}

	public List<OperatorEntity> getAllOperatorSections(SqlSession conn) {
		OperatorMapper mapper = conn.getMapper(OperatorMapper.class);

		OperatorEntity entity = new OperatorEntity();
		entity.setExpire_date(new Date());
		return mapper.findOperatorSection(entity);
	}

	public List<OperatorEntity> getWorkflgOperators(SqlSession conn) {
		OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
		List<OperatorEntity> privList = mapper.getOperatorWithPrivacy(RvsConsts.PRIVACY_POSITION);

		List<OperatorEntity> ret = new ArrayList<OperatorEntity>();

		for (OperatorEntity op : privList) {
			if (op.getWork_count_flg() == 1 && op.getSection_id() != null) {
				op.setSection_id(CommonStringUtil.fillChar(op.getSection_id(), '0', 11, true));
				op.setOperator_id(CommonStringUtil.fillChar(op.getOperator_id(), '0', 11, true));
				ret.add(op);
			}
		}
		return ret;
	}

	public void insertOperatorSection(HttpServletRequest req,
			SqlSessionManager conn) {
		OperatorMapper mapper = conn.getMapper(OperatorMapper.class);
		OperatorEntity entity = new OperatorEntity();
		entity.setOperator_id(req.getParameter("operator_id"));
		entity.setSection_id(req.getParameter("section_id"));

		if (req.getParameter("active_date") != null) {
			entity.setActive_date(DateUtil.toDate(req.getParameter("active_date"), DateUtil.DATE_PATTERN));
			entity.setExpire_date(DateUtil.toDate(req.getParameter("expire_date"), DateUtil.DATE_PATTERN));
			mapper.insertOperatorSection(entity);
		} else {
			mapper.deleteOperatorSection(entity.getOperator_id(), entity.getSection_id());
		}
	}
}

package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.ProcessAssignEntity;
import com.osh.rvs.bean.master.ProcessAssignTemplateEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.ProcessAssignForm;
import com.osh.rvs.form.master.ProcessAssignTemplateForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.ProcessAssignMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ProcessAssignService {
	private static final String FLOW_MAIN = "9000000";

	private static Logger logger = Logger.getLogger(ProcessAssignService.class);

	private static Map<String, Boolean> hasNsMap = new HashMap<String, Boolean>(); 

	/** S1等级时越过的工位 */
	public static Integer[] S1PASSES = new Integer[0];
	static {
		String sS1pass = PathConsts.POSITION_SETTINGS.getProperty("s1pass");
		if (sS1pass != null) {
			String[] passProcessCodes = sS1pass.split(",");
			int iPassProcessCodeslength = passProcessCodes.length;
			S1PASSES = new Integer[iPassProcessCodeslength];

			try {
				for (int i = 0; i < iPassProcessCodeslength; i++) {
					String sPosition_id = ReverseResolution.getPositionByProcessCode(passProcessCodes[i], null);
					S1PASSES[i] = Integer.parseInt(sPosition_id);
				}
			} catch (Exception e) {
				logger.error("错误的s1pass配置：" + e.getMessage());
				S1PASSES = new Integer[0];
			}
		}
	}

	public List<ProcessAssignTemplateForm> searchTemplate(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		ProcessAssignTemplateEntity conditionBean = new ProcessAssignTemplateEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// 从数据库中查询记录
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		List<ProcessAssignTemplateEntity> lResultBean = dao.searchProcessAssignTemplate(conditionBean);

		// 建立页面返回表单
		List<ProcessAssignTemplateForm> lResultForm = new ArrayList<ProcessAssignTemplateForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, ProcessAssignTemplateForm.class);

		return lResultForm;
	}

	public ProcessAssignTemplateForm getDetail(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 取得模板详细
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		ProcessAssignTemplateEntity e = dao.getProcessAssignTemplateByID(((ProcessAssignTemplateForm) form).getId());
		ProcessAssignTemplateForm result = new ProcessAssignTemplateForm();

		BeanUtil.copyToForm(e, result, null);

		return result;
	}

	public ProcessAssignTemplateEntity getDetail(String id, SqlSession conn) {
		// 取得模板详细
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		return dao.getProcessAssignTemplateByID(id);
	}

	public void insert(ActionForm form, Map<String, String[]> parameterMap, HttpSession session,
			SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		ProcessAssignTemplateEntity insertBean = new ProcessAssignTemplateEntity();
		BeanUtil.copyToBean(form, insertBean, null);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		insertBean.setUpdated_by(user.getOperator_id());

		// 插入
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		dao.insertProcessAssignTemplate(insertBean);

		CommonMapper cmDao = conn.getMapper(CommonMapper.class);
		String refer_id = cmDao.getLastInsertID();

		List<ProcessAssignForm> processAssigns = new AutofillArrayList<ProcessAssignForm>(ProcessAssignForm.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("process_assign".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					// TODO 全
					if ("line_id".equals(column)) {
						processAssigns.get(icounts).setLine_id(value[0]);
					} else if ("position_id".equals(column)) {
						processAssigns.get(icounts).setPosition_id(value[0]);
					} else if ("sign_position_id".equals(column)) {
						processAssigns.get(icounts).setSign_position_id(value[0]);
					} else if ("prev_position_id".equals(column)) {
						processAssigns.get(icounts).setPrev_position_id(value[0]);
					} else if ("next_position_id".equals(column)) {
						processAssigns.get(icounts).setNext_position_id(value[0]);
					}
				}
			}
		}

		// 放入数据库
		for (ProcessAssignForm processAssign : processAssigns) {
			ProcessAssignEntity e = new ProcessAssignEntity();
			BeanUtil.copyToBean(processAssign, e, null);
			e.setRefer_id(refer_id);
			e.setRefer_type(1);
			dao.insertProcessAssign(e);
		}

		// 取得所有流程的最终工位
		evalLastPositions(conn);
	}

	public void delete(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		// 表单复制到数据对象
		ProcessAssignTemplateEntity updateBean = new ProcessAssignTemplateEntity();
		BeanUtil.copyToBean(form, updateBean, null);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		dao.deleteProcessAssignTemplate(updateBean);

		// 取得所有流程的最终工位
		evalLastPositions(conn);
	}

	public void update(ActionForm form, Map<String, String[]> parameterMap, HttpSession session,
			SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		synchronized (hasNsMap) {
			// 表单复制到数据对象
			ProcessAssignTemplateEntity updateBean = new ProcessAssignTemplateEntity();
			BeanUtil.copyToBean(form, updateBean, null);

			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
			updateBean.setUpdated_by(user.getOperator_id());

			// 更新模板名
			ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
			dao.updateProcessAssignTemplate(updateBean);

			String refer_id = updateBean.getProcess_assign_template_id();

			List<ProcessAssignForm> processAssigns = new AutofillArrayList<ProcessAssignForm>(ProcessAssignForm.class);
			Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

			// 整理提交数据
			for (String parameterKey : parameterMap.keySet()) {
				Matcher m = p.matcher(parameterKey);
				if (m.find()) {
					String entity = m.group(1);
					if ("process_assign".equals(entity)) {
						String column = m.group(2);
						int icounts = Integer.parseInt(m.group(3));
						String[] value = parameterMap.get(parameterKey);

						// TODO 全
						if ("line_id".equals(column)) {
							processAssigns.get(icounts).setLine_id(value[0]);
						} else if ("position_id".equals(column)) {
							processAssigns.get(icounts).setPosition_id(value[0]);
						} else if ("sign_position_id".equals(column)) {
							processAssigns.get(icounts).setSign_position_id(value[0]);
						} else if ("prev_position_id".equals(column)) {
							processAssigns.get(icounts).setPrev_position_id(value[0]);
						} else if ("next_position_id".equals(column)) {
							processAssigns.get(icounts).setNext_position_id(value[0]);
						}
					}
				}
			}

			// 删除原有明细
			dao.deleteProcessAssignByTemplateID(refer_id);

			// 放入数据库
			for (ProcessAssignForm processAssign : processAssigns) {
				ProcessAssignEntity e = new ProcessAssignEntity();
				BeanUtil.copyToBean(processAssign, e, null);
				e.setRefer_id(refer_id);
				e.setRefer_type(1);
				dao.insertProcessAssign(e);
			}

			hasNsMap.remove(refer_id);
		}

		// 取得所有流程的最终工位
		evalLastPositions(conn);
	}

	public List<Map<String, String>> getInlinePositions(SqlSession conn) {

		// 从数据库中查询记录
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		List<Map<String, String>> lResultBean = dao.getInlinePositions();

		for (Map<String, String> position : lResultBean) {
			String process_code = position.get("process_code");
			if (process_code != null) {
				position.put("text",  process_code + "\n" + position.get("text"));
			}
		}

		return lResultBean;
	}

	public List<ProcessAssignForm> getAssigns(String template_id, SqlSession conn, List<MsgInfo> errors) {
		// 从数据库中查询记录
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		List<ProcessAssignEntity> entities = dao.getProcessAssignByTemplateID(template_id);
		List<ProcessAssignForm> result = new ArrayList<ProcessAssignForm>();
		BeanUtil.copyToFormList(entities, result, null, ProcessAssignForm.class);
		return result;
	}

	/**
	 * 取得维修流程选择项标签集
	 * 
	 * @param conn 数据库连接
	 * @return String 维修流程选择项标签集
	 */
	public String getOptions(String empty, SqlSession conn) {
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		List<ProcessAssignTemplateEntity> l = dao.getAllProcessAssignTemplate();
		Map<String, String> codeMap = new TreeMap<String, String>();
		for (ProcessAssignTemplateEntity bean : l) {
			if (!bean.isDelete_flg())
				codeMap.put(bean.getProcess_assign_template_id(), bean.getName());
		}
		return CodeListUtils.getSelectOptions(codeMap, null, empty, false);
	}

	public String getGroupOptions(String empty, SqlSession conn) {
		return getGroupOptions(empty, null, conn);
	}
	public String getGroupOptions(String empty, String defaultValue, SqlSession conn) {
		if (empty == null) empty = "";
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		List<ProcessAssignTemplateEntity> l = dao.getAllProcessAssignTemplate();
		Map<String, String> codeMapNormal = new TreeMap<String, String>();
		Map<String, String> codeMapDerived = new TreeMap<String, String>();
		Map<String, String> codeMapRefer = new TreeMap<String, String>();
		for (ProcessAssignTemplateEntity bean : l) {
			if (!bean.isDelete_flg())
				if (bean.getFix_type() == 0) {
					codeMapRefer.put(bean.getProcess_assign_template_id(), bean.getName());
				} else if (bean.getDerive_kind() != null) {
					if (bean.getDerive_kind() != 5) { // NS 组装 非完整流水线流程
						codeMapDerived.put(bean.getProcess_assign_template_id(), bean.getName());
					}
				} else {
					codeMapNormal.put(bean.getProcess_assign_template_id(), bean.getName());
				}
		}
		return "<optgroup label=\"\"><option value=\"\">" + empty + "</option></optgroup>" 
		+ "<optgroup label=\"原形\">" + CodeListUtils.getSelectOptions(codeMapNormal, defaultValue, null, false) + "</optgroup>"
		+ "<optgroup label=\"派生\">" + CodeListUtils.getSelectOptions(codeMapDerived, defaultValue, null, false) + "</optgroup>"
		+ "<optgroup label=\"独立流程编制\">" + CodeListUtils.getSelectOptions(codeMapRefer, defaultValue, null, false) + "</optgroup>";
	}

	public String getAllGroupOptions(String empty, SqlSession conn) {
		if (empty == null) empty = "";
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		List<ProcessAssignTemplateEntity> l = dao.getAllProcessAssignTemplate();
		Map<String, String> codeMapNormal = new TreeMap<String, String>();
		Map<String, String> codeMapDerived = new TreeMap<String, String>();
		Map<String, String> codeMapRefer = new TreeMap<String, String>();
		Map<String, String> codeMapHistory = new TreeMap<String, String>();
		for (ProcessAssignTemplateEntity bean : l) {
			if (!bean.isDelete_flg())
				if (bean.getFix_type() == 0) {
					codeMapRefer.put(bean.getProcess_assign_template_id(), bean.getName());
				} else if (bean.getDerive_kind() != null) {
					codeMapDerived.put(bean.getProcess_assign_template_id(), bean.getName());
				} else {
					codeMapNormal.put(bean.getProcess_assign_template_id(), bean.getName());
				}
			else
				codeMapHistory.put(bean.getProcess_assign_template_id(), bean.getName());
		}
		return "<optgroup label=\"\"><option value=\"\">" + empty + "</option></optgroup>" 
		+ "<optgroup label=\"原形\">" + CodeListUtils.getSelectOptions(codeMapNormal, null, null, false) + "</optgroup>"
		+ "<optgroup label=\"派生\">" + CodeListUtils.getSelectOptions(codeMapDerived, null, null, false) + "</optgroup>"
		+ "<optgroup label=\"独立流程编制\">" + CodeListUtils.getSelectOptions(codeMapRefer, null, null, false) + "</optgroup>"
		+ "<optgroup label=\"停用\">" + CodeListUtils.getSelectOptions(codeMapHistory, null, null, false) + "</optgroup>";
	}

	/**
	 * 取得流程里的首工位(可能复数可能嵌套但是现在的RVS里没有这些情况)
	 * @param template_id
	 * @param line_id
	 * @param conn
	 * @return
	 */
	public List<String> getFirstPositionIds(String template_id, SqlSession conn) {
		return getFirstPositionIds(template_id, "" + RvsConsts.PROCESS_ASSIGN_LINE_BASE, conn);
	}
	public List<String> getFirstPositionIds(String template_id, String pas_line_id, SqlSession conn) {
		ProcessAssignMapper mapper = conn.getMapper(ProcessAssignMapper.class);
		List<String> ret = mapper.getFirstPosition(template_id, pas_line_id);
		List<String> lines = new ArrayList<String> ();
		for (String pos_id : ret) {
			if (pos_id.length() >= 7) {
				lines.add(pos_id);
			}
		}

		for (String lin_id : lines) {
			ret.remove(lin_id);
			ret.addAll(getFirstPositionIds(template_id, lin_id, conn));
		}

		return ret;
	}

	public boolean checkPatHasNs(String pat_id, SqlSession conn) {
		if (pat_id == null) {
			return false;
		}
		if (hasNsMap.containsKey(pat_id)) {
			return hasNsMap.get(pat_id);
		} else {
			synchronized (hasNsMap) {
				ProcessAssignMapper mapper = conn.getMapper(ProcessAssignMapper.class);
				ProcessAssignTemplateEntity patEntity = mapper.getProcessAssignTemplateByID(pat_id);
				if (patEntity == null) {
					return false;
				}
				if (patEntity.getDerive_kind() != null
						&& patEntity.getDerive_kind() == 2) {
					// 补胶不排NS计划
					hasNsMap.put(pat_id, false);
					return false;
				}

				boolean has = mapper.checkHasLine(pat_id, "00000000013");
				hasNsMap.put(pat_id, has);
				return has;
			}
		}
	}

	public void customValidate(ActionForm form, List<MsgInfo> errors) {
		ProcessAssignTemplateForm patForm = (ProcessAssignTemplateForm) form;
		if (!CommonStringUtil.isEmpty(patForm.getDerive_kind())
				&& CommonStringUtil.isEmpty(patForm.getDerive_from_id())) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("derive_from_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "模板派生来源"));
			errors.add(error);
		}
	}

	public Map<String, Object> getDerivePair(String default_pat_id, String derive_kind,
			SqlSession conn) {
		ProcessAssignMapper mapper = conn.getMapper(ProcessAssignMapper.class);
		Map<String, Object> result = mapper.getDerivePair(default_pat_id, derive_kind);
		if (result != null && result.get("derive_id") != null) {
			result.put("base_id", default_pat_id);

			return result;
		} else {
			return null;
		}
	}

	public String getPositionsOfLineByPat(String patId, String lineId,
			SqlSession conn) {
		List<String> spareStrings = new ArrayList<String>();

		ProcessAssignMapper mapper = conn.getMapper(ProcessAssignMapper.class);
		List<Map<String, String>> pMaps = mapper.getPositionsOfLineByPat(patId, lineId);
		for (Map<String, String> pMap : pMaps) {
			spareStrings.add(pMap.get("process_code"));
		}

		return CommonStringUtil.joinBy(",", spareStrings.toArray(new String[spareStrings.size()]));
	}

	/**
	 * DerivedId取得
	 * @param conn
	 * @return String
	 */
	public String getDerivedId(String model_id, String derive_type, boolean closeToDeriveType, SqlSession conn) {
		ProcessAssignMapper dao = conn.getMapper(ProcessAssignMapper.class);
		String derivedId = dao.getDerivedIdByModel(model_id, derive_type);
		if (derivedId != null || !closeToDeriveType) {
			return derivedId;
		}
		return dao.getDerivedIdByModel(null, derive_type);
	}

	private static Set<String> lastFLowPositions = null;

	/**
	 * 取得所有流程的最终工位
	 * 
	 * @param conn
	 */
	private static void evalLastPositions(SqlSessionManager conn) {
		if (lastFLowPositions == null) {
			lastFLowPositions = new HashSet<String> ();
		}
		synchronized (lastFLowPositions) {
			lastFLowPositions.clear();

			ProcessAssignMapper mapper = conn.getMapper(ProcessAssignMapper.class);
			ProcessAssignEntity condi = new ProcessAssignEntity();
			condi.setLine_id(FLOW_MAIN);
			List<ProcessAssignEntity> listMain = mapper.evalLastPositions(condi);

			// 流程结束的是分支线的列表
			List<ProcessAssignEntity> endParts = new ArrayList<ProcessAssignEntity>();

			// 记录所有主流程上的结束工位
			for (ProcessAssignEntity pae : listMain) {
				if (pae.getPosition_id().length() >= FLOW_MAIN.length()) { // 所以这个表里position_id不是zerofill的
					endParts.add(pae);
				} else {
					lastFLowPositions.add(CommonStringUtil.fillChar(pae.getPosition_id(), '0', 11, true));
					if (pae.getSign_position_id() != null) {
						lastFLowPositions.add(pae.getSign_position_id());
					}
				}
			}

			// 查找流程结束的是分支线
			for (ProcessAssignEntity endPart : endParts) {
				List<ProcessAssignEntity> listPart = mapper.evalLastPositions(endPart);
				// 记录所有结尾分支线上的结束工位
				for (ProcessAssignEntity pae : listPart) {
					lastFLowPositions.add(CommonStringUtil.fillChar(pae.getPosition_id(), '0', 11, false));
					if (pae.getSign_position_id() != null) {
						lastFLowPositions.add(pae.getSign_position_id());
					}
				}
			}

			// 清除维修结束工位表
			mapper.deleteProcessEndPosition();
			// 插入维修结束工位表
			for (String position_id : lastFLowPositions) {
				mapper.insertProcessEndPosition(position_id);
			}
		}
	}

	/*
		取得维修结束工位
	 */
	public static Set<String> getLastFLowPositions(SqlSessionManager conn) {
		if (lastFLowPositions == null) {
			evalLastPositions(conn);
		}
		return lastFLowPositions;
	}

	/*
		取得动物内镜用流程
	 */
	private static List<String> anmlPats = null;
	public static List<String> getAnmlProcesses(SqlSession conn) {
		// 取得动物内镜用流程
		if (anmlPats == null) {
			ProcessAssignMapper mapper = conn.getMapper(ProcessAssignMapper.class);
			anmlPats = mapper.getAnmlProcesses();
			// anmlPats.add("73"); anmlPats.add("74");
		}
		return anmlPats;
	}
}

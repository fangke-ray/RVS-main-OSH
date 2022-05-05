package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.OptionalFixEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.OptionalFixForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.OptionalFixMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class OptionalFixService {

	public List<OptionalFixForm> search(
			OptionalFixForm optionalFixForm, SqlSession conn) {
		// 表单复制到数据对象
		OptionalFixEntity conditionBean = new OptionalFixEntity();
		BeanUtil.copyToBean(optionalFixForm, conditionBean, null);

		// 从数据库中查询记录
		OptionalFixMapper mapper = conn.getMapper(OptionalFixMapper.class);
		List<OptionalFixEntity> lResultBean = mapper.searchOptionalFix(conditionBean);

		// 建立页面返回表单
		List<OptionalFixForm> lResultForm = new ArrayList<OptionalFixForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, OptionalFixForm.class);

		for (OptionalFixForm resultForm : lResultForm) {
			if (!CommonStringUtil.isEmpty(resultForm.getRank())) {
				resultForm.setRank(toTextValue(resultForm.getRank()));
			}
		}

		return lResultForm;
	}

	public OptionalFixForm getDetail(
			String optional_fix_id, SqlSession conn) {
		// 从数据库中查询记录
		OptionalFixMapper mapper = conn.getMapper(OptionalFixMapper.class);
		OptionalFixEntity resultBean = mapper.getOptionalFix(optional_fix_id);

		// 建立页面返回表单
		OptionalFixForm resultForm = new OptionalFixForm();
		BeanUtil.copyToForm(resultBean, resultForm, null);

		List<String> ranks_list = mapper.getRanks(optional_fix_id);
		if (ranks_list != null && ranks_list.size() > 0) {
			resultForm.setRank(CommonStringUtil.joinBy(",", ranks_list.toArray(new String[ranks_list.size()])));
		}

		return resultForm;
	}

	public void insert(OptionalFixForm optionalFixForm, HttpSession session,
			SqlSessionManager conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		OptionalFixEntity insertBean = new OptionalFixEntity();
		BeanUtil.copyToBean(optionalFixForm, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		insertBean.setUpdated_by(user.getOperator_id());

		// 新建记录插入到数据库中
		OptionalFixMapper mapper = conn.getMapper(OptionalFixMapper.class);
		
		// 检查修理代码和机种是否存在
		int duplicatedCode = mapper.checkCodeIsExist(insertBean.getStandard_code(), null);
		
		if(duplicatedCode > 0){
			MsgInfo error = new MsgInfo();
			error.setComponentid("standard_code");
			error.setErrcode("dbaccess.recordDuplicated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "检查标准号"));
			errors.add(error);
			return;
		}
		
		mapper.insertOptionalFix(insertBean);

		String postRank = optionalFixForm.getRank();
		if (!CommonStringUtil.isEmpty(postRank)) {
			// 取得刚才插入的主键
			CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
			String lastInsertId = commonMapper.getLastInsertID();
			insertBean.setOptional_fix_id(lastInsertId);

			// 选择修理对应等级插入到数据库中
			String[] rankArr = postRank.split(",");
			for (String rank : rankArr) {
				insertBean.setRank(rank);
				mapper.insertRank(insertBean);
			}
		}
	}

	public void update(OptionalFixForm optionalFixForm,
			HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		OptionalFixEntity updateBean = new OptionalFixEntity();
		BeanUtil.copyToBean(optionalFixForm, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		// 更新数据库中记录
		OptionalFixMapper mapper = conn.getMapper(OptionalFixMapper.class);
		
		// 检查修理代码和机种是否存在
		int duplicatedCode = mapper.checkCodeIsExist(updateBean.getStandard_code(), updateBean.getOptional_fix_id());
		
		if(duplicatedCode > 0){
			MsgInfo error = new MsgInfo();
			error.setComponentid("standard_code");
			error.setErrcode("dbaccess.recordDuplicated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "检查标准号"));
			errors.add(error);
			return;
		}
		
		// 选择修理对应等级更新到数据库中
		mapper.deleteRank(updateBean.getOptional_fix_id());

		String postRank = optionalFixForm.getRank();
		if (!CommonStringUtil.isEmpty(postRank)) {
			String[] rankArr = postRank.split(",");
			for (String rank : rankArr) {
				updateBean.setRank(rank);
				mapper.insertRank(updateBean);
			}
		}

		mapper.updateOptionalFix(updateBean);
	}

	public void delete(String optional_fix_id, HttpSession session, SqlSessionManager conn) {
		// 在数据库中逻辑删除记录
		OptionalFixMapper mapper = conn.getMapper(OptionalFixMapper.class);
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		OptionalFixEntity updateBean = new OptionalFixEntity();
		updateBean.setUpdated_by(user.getOperator_id());
		updateBean.setOptional_fix_id(optional_fix_id);
		mapper.deleteOptionalFix(updateBean);
		mapper.deleteRank(optional_fix_id);
	}

	private static Map<String, String> rankTextCache = new HashMap<String, String>();
	private String toTextValue(String rank) {
		if (!rankTextCache.containsKey(rank)) {
			String rankText = "";
			for (String r : rank.split(",")) {
				rankText += CodeListUtils.getValue("material_level_inline", r) + " ";
			}
			rankTextCache.put(rank, rankText);
		}
		return rankTextCache.get(rank);
	}

}

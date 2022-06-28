package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.OptionalFixEntity;
import com.osh.rvs.common.PcsUtils;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.master.OptionalFixForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.OptionalFixMapper;
import com.osh.rvs.mapper.qf.MaterialOptionalFixMapper;

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

	public Map<String, String> getPcsByItemName(String infection_item) {
		String pcsContent = PcsUtils.getContentFromPath(
				PcsUtils.getFileName("报价\n选择修理", infection_item));
		if (pcsContent == null) {
			return null;
		}
		Map<String, String> pcsContents = new HashMap<String, String>();
		pcsContents.put("htmlContent", pcsContent);
		Map<String, String> pcsHtml = PcsUtils.toHtmlBlank(pcsContents , "（修理型号）");
		return pcsHtml;
	}

	public List<String> searchMaterialOptionalFix(ActionForm form,
			SqlSession conn) {
		MaterialOptionalFixMapper mapper = conn.getMapper(MaterialOptionalFixMapper.class);

		OptionalFixForm opfForm = (OptionalFixForm) form;
		OptionalFixEntity condition = new OptionalFixEntity();
		condition.setMaterial_id(opfForm.getMaterial_id());
		List<OptionalFixEntity> lEntities = mapper.searchMaterialOptionalFix(condition);

		List<String> ret = new ArrayList<String>();
		for (OptionalFixEntity entity : lEntities) {
			ret.add(entity.getOptional_fix_id());
		}
		return ret;
	}

	public void getMaterialOptionalFix(String material_id,
			MaterialForm mform, Map<String, Object> cbResponse, SqlSession conn) {
		OptionalFixEntity condition = new OptionalFixEntity();
		condition.setMaterial_id(material_id);

		List<String> itemNames = getMaterialOptionalFixItems(material_id, conn);

		if (itemNames == null) {
			cbResponse.put("optionalFixLabelText", "（无选择修理项）");
		} else {
			cbResponse.put("optionalFixLabelText", CommonStringUtil.joinBy("；", itemNames.toArray()));
			
			if (mform != null) {
				cbResponse.put("appendPcses", getOptionalFixPcses(itemNames, mform, null, conn));
			}
		}
	}

	public List<String> getMaterialOptionalFixItems(String material_id, SqlSession conn) {
		MaterialOptionalFixMapper mapper = conn.getMapper(MaterialOptionalFixMapper.class);
		
		OptionalFixEntity condition = new OptionalFixEntity();
		condition.setMaterial_id(material_id);
		List<OptionalFixEntity> lEntities = mapper.searchMaterialOptionalFix(condition);

		if (lEntities.size() == 0) {
			return null;
		} else {
			List<String> itemNames = new ArrayList<String>();
			for (OptionalFixEntity ofEntity : lEntities) {
				itemNames.add(ofEntity.getInfection_item());
			}

			return itemNames;
		}
	}

	public List<OptionalFixForm> getOptionalFixByRank(ActionForm form, SqlSession conn) {
		List<OptionalFixForm> retF = new ArrayList<OptionalFixForm>();
		OptionalFixMapper mapper = conn.getMapper(OptionalFixMapper.class);

		OptionalFixForm opfForm = (OptionalFixForm) form;

		String rank = opfForm.getRank();
		if (CommonStringUtil.isEmpty(rank)) {
			rank = null;
		} else if (RvsUtils.isLightFix(rank)) {
			rank = "9";
		} else if (RvsUtils.isPeripheral(rank)) {
			rank = "5";
		}

		List<OptionalFixEntity> retE = mapper.getOptionalFixByRank(rank);

		BeanUtil.copyToFormList(retE, retF, CopyOptions.COPYOPTIONS_NOEMPTY, OptionalFixForm.class);
		return retF;
	}

	public boolean compareAndUpdate(List<String> orgSelList, ActionForm form,
			Map<String, Object> cbResponse, SqlSession conn) {
		boolean changed = false;

		MaterialOptionalFixMapper mapper = conn.getMapper(MaterialOptionalFixMapper.class);

		List<String> appendList = new ArrayList<String>();
		List<String> removeList = new ArrayList<String>();

		OptionalFixForm opfForm = (OptionalFixForm) form;
		String optionalFixIds = opfForm.getOptional_fix_id();
		String[] arrOptionalFixId = new String[0];
		if (!CommonStringUtil.isEmpty(optionalFixIds)) {
			arrOptionalFixId = optionalFixIds.split(",");
		}

		for (String optionalFixId : arrOptionalFixId) {
			if (!orgSelList.contains(optionalFixId)) {
				appendList.add(optionalFixId);
			}
		}
	
		for (String optionalFixId : orgSelList) {
			boolean hit = false;
			for (String orgOptionalFixId : arrOptionalFixId) {
				if (orgOptionalFixId.equals(optionalFixId)) {
					hit = true;
					break;
				}
			}
			if (!hit) {
				removeList.add(optionalFixId);
			}
		}

		if (!removeList.isEmpty()) {
			OptionalFixEntity delEntity = new OptionalFixEntity();
			delEntity.setMaterial_id(opfForm.getMaterial_id());
			for (String optionalFixId : removeList) {
				delEntity.setOptional_fix_id(optionalFixId);
				mapper.deleteMaterialOptionalFix(delEntity);
			}
			cbResponse.put("removeList", removeList);
			changed = true;
		}

		if (!appendList.isEmpty()) {
			OptionalFixEntity insEntity = new OptionalFixEntity();
			insEntity.setMaterial_id(opfForm.getMaterial_id());
			for (String optionalFixId : appendList) {
				insEntity.setOptional_fix_id(optionalFixId);
				mapper.insertMaterialOptionalFix(insEntity);
			}

			changed = true;
		}

		if (CommonStringUtil.isEmpty(optionalFixIds)) {
			cbResponse.put("labelText", "（无选择修理项）");
		} else {
			OptionalFixEntity condition = new OptionalFixEntity();;
			condition.setMaterial_id(opfForm.getMaterial_id());
			// 取得新选择修理项目
			List<OptionalFixEntity> list = mapper.searchMaterialOptionalFix(condition);

			String labelText = "";
			List<String> itemNames = new ArrayList<String>();
			for (OptionalFixEntity ofEntity: list) {
				labelText += ofEntity.getInfection_item() + "；";
				// 获得增加的工程检查表
				if (appendList.contains(ofEntity.getOptional_fix_id())) {
					itemNames.add(ofEntity.getInfection_item());
				}
			}
			if (labelText.length() > 0) {
				labelText = labelText.substring(0, labelText.length() - 1);
			}
			cbResponse.put("labelText", labelText);

			if (itemNames.size() > 0) {
				MaterialService mService = new MaterialService();
				MaterialForm mform = mService.loadSimpleMaterialDetail(conn, opfForm.getMaterial_id());
				cbResponse.put("appendPcses", getOptionalFixPcses(itemNames, mform, null, conn));
			}
		}

		return changed;
	}

	/**
	 * 按已取得的选择修理项目取得显示检查票
	 * 
	 * @param itemNames
	 * @param mform
	 * @param getHistory
	 * @param conn
	 * @return
	 */
	public Map<String, String> getOptionalFixPcses(List<String> itemNames, MaterialForm mform, String getHistory, SqlSession conn) {
		Map<String, String> fileTempl = PcsUtils.getOptionalFixXmlContents(itemNames, getHistory != null, mform.getMaterial_id(), conn);

		if (!fileTempl.isEmpty()) {
			Map<String, String> fileHtml = PcsUtils.toHtml(fileTempl, mform.getMaterial_id(), mform.getSorc_no(),
					mform.getModel_name(), mform.getSerial_no(), mform.getLevel(), "151", null, 
					false, conn);
			return fileHtml;
		}

		return null;
	}

	public Map<String, String> makeOptionalFixPcses(List<String> itemNames, MaterialForm mform, SqlSession conn) {
		return PcsUtils.getOptionalFixXlsContents(itemNames, mform.getMaterial_id(), conn);
	}

}

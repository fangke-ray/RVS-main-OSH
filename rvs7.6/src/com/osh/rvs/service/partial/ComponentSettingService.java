package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.partial.ComponentSettingEntity;
import com.osh.rvs.form.partial.ComponentSettingForm;
import com.osh.rvs.form.partial.PremakePartialForm;
import com.osh.rvs.mapper.inline.SoloProductionFeatureMapper;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.partial.ComponentSettingMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ComponentSettingService {

	private static Set<String> nsCompModels = null;
	private static Map<String, String> snoutCompModels = null;
	private static Map<String, String> snoutCompActiveModels = null;

	public static void clearComps() {
		nsCompModels = null;
		snoutCompModels = null;
		snoutCompActiveModels = null;
	}

	/**
	 * 取得全部型号(参照列表)
	 * @param conn
	 * @return
	 */
	public String getComponentSettings(SqlSession conn) {
		List<ComponentSettingForm> allModel = this.getAllComponentSettings(conn);
		Map<String, String> modelMap = new LinkedHashMap<String, String>();
		for (ComponentSettingForm model: allModel) {
			modelMap.put(model.getModel_id(), model.getModel_name());
		}

		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		List<ComponentSettingEntity> historyModel = dao.getHistoryComponentModel(conn);
		for (ComponentSettingEntity model: historyModel) {
			modelMap.put(model.getModel_id(), model.getModel_name());
		}

		String mReferChooser = CodeListUtils.getSelectOptions(modelMap, "", "(未选择)", false);

		return mReferChooser;
	}
	
	/**
	 * 取得全部型号
	 * @param conn
	 * @return
	 */
	public List<ComponentSettingForm> getAllComponentSettings(SqlSession conn) {
		
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);

		List<ComponentSettingEntity> lResultBean = dao.getAllComponentSettings();
		
		List<ComponentSettingForm> lResultForm = new ArrayList<ComponentSettingForm>();
		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, ComponentSettingForm.class);

		return lResultForm;
	}
	
	/**
	 * NS组件库存一览数据取得
	 * @param conn
	 * @return
	 */
	public List<ComponentSettingForm> searchComponentSetting(SqlSession conn) {
		
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);

		List<ComponentSettingEntity> lResultBean = dao.searchComponentSetting();
		
		List<ComponentSettingForm> lResultForm = new ArrayList<ComponentSettingForm>();
		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, ComponentSettingForm.class);

		return lResultForm;
	}

	/**
	 * 确认型号是否是NS 组件组装目标型号，并返回值
	 * @param conn
	 * @return
	 */
	public ComponentSettingForm getComponentSetting(String model_id, SqlSession conn) {
		
		ComponentSettingMapper mapper = conn.getMapper(ComponentSettingMapper.class);

		List<ComponentSettingEntity> lResultBeans = mapper.searchComponentSetting();
		
		ComponentSettingForm lResultForm = null;

		for (ComponentSettingEntity lResultBean : lResultBeans) {
			if (model_id.equals(lResultBean.getModel_id())) {
				lResultForm = new ComponentSettingForm();
				// 数据对象复制到表单
				BeanUtil.copyToForm(lResultBean, lResultForm, CopyOptions.COPYOPTIONS_NOEMPTY);
				break;
			}
		}

		return lResultForm;
	}

	/**
	 * 组件设置追加
	 * 
	 * @param ComponentSettingEntity
	 * @param conn
	 */
	public void insertSetting(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ComponentSettingEntity insertBean = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, insertBean, null);

		/* component_setting表插入数据 */
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		if ("".equals(insertBean.getSafety_lever())) {
			insertBean.setSafety_lever(null);
		}
		dao.insertSetting(insertBean);

		// 清空型号集
		nsCompModels = null;
	}

	/**
	 * 根据型号ID判定组件是否存在
	 * 
	 * @param ComponentSettingEntity
	 * @param conn
	 */
	public int isExitsByModelId(ActionForm form, HttpSession session, SqlSessionManager conn)
			throws Exception {
		ComponentSettingEntity searchBean = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, searchBean, null);
		
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		ComponentSettingEntity tempEntity = dao.searchComponentSettingByModelId(searchBean.getModel_id());
		if(tempEntity!=null){
			return 1;
		}else{
			return 0;
		}
	}

	/**
	 * 根据识别代码判定组件是否存在
	 * 
	 * @param ComponentSettingEntity
	 * @param conn
	 */
	public int isExitsByIdentifyCode(ActionForm form, HttpSession session, SqlSessionManager conn)
			throws Exception {
		ComponentSettingEntity searchBean = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, searchBean, null);
		
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		ComponentSettingEntity tempEntity = dao.searchComponentSettingByIdentifyCode(searchBean.getIdentify_code());
		if(tempEntity!=null){
			return 1;
		}else{
			return 0;
		}
	}
	
	/**
	 * 取得NS组件设置详细信息
	 * 
	 * @param consumableListEntity
	 * @param conn
	 * @return 如果存在返回NS组件设置详细信息
	 */
	public List<ComponentSettingForm> searchComponentSettingDetail(
			ComponentSettingEntity settingEntity, SqlSession conn) {
		ComponentSettingMapper settingMapper = conn.getMapper(ComponentSettingMapper.class);
		List<ComponentSettingForm> resultForm = new ArrayList<ComponentSettingForm>();
		// 型号 ID
		String model_id = settingEntity.getModel_id();
		// 取得NS组件设置详细信息
		List<ComponentSettingEntity> resultList = settingMapper.getSettingDetail(model_id);
		BeanUtil.copyToFormList(resultList, resultForm, null, ComponentSettingForm.class);
		return resultForm;
	}
	public ComponentSettingEntity getComponentSettingDetail(
			String model_id, SqlSession conn) {
		ComponentSettingMapper settingMapper = conn.getMapper(ComponentSettingMapper.class);
		List<ComponentSettingEntity> resultList = settingMapper.getSettingDetail(model_id);

		if (resultList.size() == 0) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

	/**
	 * 组件设置更新
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void updateSetting(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ComponentSettingEntity updateBean = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, updateBean, null);

		/* component_setting表插入数据 */
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		if ("".equals(updateBean.getSafety_lever())) {
			updateBean.setSafety_lever(null);
		}
		dao.updateSetting(updateBean);
	}

	/**
	 * 组件设置删除
	 * @param form
	 * @param conn
	 */
	public void deleteSetting(ActionForm form, SqlSessionManager conn) {
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);

		// 复制表单数据到对象
		ComponentSettingEntity entity = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 删除指定型号ID的NS组件子零件
		dao.deleteSetting(entity);

		// 清空型号集
		nsCompModels = null;
	}

	/**
	 * 重复性检查
	 * @param req
	 * @param conn
	 * @param errors
	 * @return 
	 */
	public List<PremakePartialForm> premakePartialValidateEdit(HttpServletRequest req, SqlSession conn,
			List<MsgInfo> errors) {
		List<PremakePartialForm> rets = new AutofillArrayList<>(PremakePartialForm.class);

		String modelId = req.getParameter("model_id");

		Map<String, String[]> parameterMap = req.getParameterMap();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("partial".equals(entity)) {
					String column = m.group(2);
					Integer index = Integer.parseInt(m.group(3));
					rets.get(index).setModel_id(modelId);
					rets.get(index).setStandard_flg("3");
					
					String[] value = parameterMap.get(parameterKey);

					if ("partial_id".equals(column)) {
						rets.get(index).setPartial_id(value[0]);
					} else if ("partial_code".equals(column)) {
						rets.get(index).setCode(value[0]);
					} else if ("partial_name".equals(column)) {
						rets.get(index).setPartial_name(value[0]);
					} else if ("quantity".equals(column)) {
						rets.get(index).setQuantity(value[0]);
					}
				}
			}
		}

		if (rets.size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("partial_id");
			error.setErrcode("validator.required.multidetail");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.multidetail", "NS组件零件"));
			errors.add(error);
			return null;
		}

		// 比对稽核
		Set<String> checkSet = new HashSet<String>();
		for (PremakePartialForm ret : rets) {
			String checkKey = ret.getPartial_id();
			if (checkSet.contains(checkKey)) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("partial_id");
				error.setErrcode("info.peripheralInfectDevice.duplicatedInSeq");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.premake.partial.existed", ret.getCode()));
				errors.add(error);
				return null;
			}

			// 加入比对对象
			checkSet.add(checkKey);

			// 数量
			if ("0".equals(ret.getQuantity())) {
				MsgInfo msg = new MsgInfo();
				msg.setComponentid("quantity");
				msg.setErrcode("validator.invalidParam.invalidMoreThanZero");
				msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanZero", "“数量”"));
				errors.add(msg);
			}

			//零件编码
			String code = ret.getCode();

			/*
			 * 如果没有选择零件编码,而是手动输入或者复制粘贴
			 * 则根据零件编码查询零件是否存在,零件不存在返回提示信息
			 */
			if(CommonStringUtil.isEmpty(ret.getPartial_id())){
				PartialMapper partialMapper = conn.getMapper(PartialMapper.class);
				List<PartialEntity> listPartial = partialMapper.getPartialByCode(code);
				
				//零件不存在
				if(listPartial.size() == 0){				
					MsgInfo msg = new MsgInfo();
					msg.setComponentid("partial_id");
					msg.setErrcode("dbaccess.recordNotExist");
					msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "零件编码为:" + code));
					errors.add(msg);
				}
				
				ret.setPartial_id(listPartial.get(0).getPartial_id());
			}
		}

		return rets;
	}

	/**
	 * NS组件库存一览数据取得
	 * @param conn
	 * @return
	 */
	public List<ComponentSettingForm> searchSnoutComponentSetting(SqlSession conn) {
		
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);

		List<ComponentSettingEntity> lResultBean = dao.searchSnoutComponentSetting();
		
		List<ComponentSettingForm> lResultForm = new ArrayList<ComponentSettingForm>();
		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, ComponentSettingForm.class);

		return lResultForm;
	}

	/**
	 * 取得NS 组件组装对应型号
	 * 
	 * @param conn
	 * @return
	 */
	public static Set<String> getNsCompModels(SqlSession conn) {
		if (nsCompModels == null) {
			nsCompModels = new HashSet<String> ();

			ComponentSettingMapper csMapper = conn.getMapper(ComponentSettingMapper.class);
			List<ComponentSettingEntity> csBeans = csMapper.getAllComponentSettings();
			for (ComponentSettingEntity csBean : csBeans) {
				nsCompModels.add(csBean.getModel_id());
			}
		}
		return nsCompModels;
	}

	/**
	 * 取得NS 组件组装对应型号
	 * 
	 * @param conn
	 * @return
	 */
	public static Map<String, String> getSnoutCompModels(SqlSession conn) {
		if (snoutCompModels == null) {
			snoutCompModels = new HashMap<String, String> ();

			ComponentSettingMapper csMapper = conn.getMapper(ComponentSettingMapper.class);
			List<ComponentSettingEntity> csBeans = csMapper.getAllSnoutComponentModel();
			for (ComponentSettingEntity csBean : csBeans) {
				snoutCompModels.put(csBean.getModel_id(), csBean.getModel_name());
			}
		}
		return snoutCompModels;
	}

	public static Map<String, String> getSnoutCompModelsActive(SqlSession conn) {
		if (snoutCompActiveModels == null) {
			snoutCompActiveModels = new HashMap<String, String> ();

			ComponentSettingMapper csMapper = conn.getMapper(ComponentSettingMapper.class);
			List<ComponentSettingEntity> csBeans = csMapper.getAllSnoutComponentModel();
			for (ComponentSettingEntity csBean : csBeans) {
				if (!"0".equals(csBean.getSafety_lever())) {
					snoutCompActiveModels.put(csBean.getModel_id(), csBean.getModel_name());
				}
			}
		}
		return snoutCompActiveModels;
	}

	public static String getModelReferChooser(SqlSession conn) {

		if (snoutCompModels == null) {
			snoutCompModels = new LinkedHashMap<String, String> ();

			ComponentSettingMapper csMapper = conn.getMapper(ComponentSettingMapper.class);
			List<ComponentSettingEntity> csBeans = csMapper.getAllSnoutComponentModel();
			for (ComponentSettingEntity csBean : csBeans) {
				snoutCompModels.put(csBean.getModel_id(), csBean.getModel_name());
			}
		}
		return CodeListUtils.getSelectOptions(snoutCompModels, null, "(不选)", false);

	}

	public static String getModelHistoryChooser(SqlSession conn) {
		LinkedHashMap<String, String> historyModels = new LinkedHashMap<String, String> ();

		ComponentSettingMapper csMapper = conn.getMapper(ComponentSettingMapper.class);
		List<ComponentSettingEntity> csBeans = csMapper.getAllHsitoryModel();
		for (ComponentSettingEntity csBean : csBeans) {
			historyModels.put(csBean.getModel_id(), csBean.getModel_name());
		}
		return CodeListUtils.getSelectOptions(historyModels, null, "(不选)", false);
	}

	/**
	 * 取得全部先端预制型号
	 * @param conn
	 * @return
	 */
	public List<ComponentSettingForm> getAllSnoutComponentSettings(SqlSession conn) {
		
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		SoloProductionFeatureMapper soloMapper = conn.getMapper(SoloProductionFeatureMapper.class);

		// 取得设定
		List<ComponentSettingEntity> lResultBean = dao.searchSnoutComponentSetting();

		// 取得先端头数量/组装中数/组装完成数
		List<SoloProductionFeatureEntity> snouts = soloMapper.getOnProcessesByModel();

		List<ComponentSettingForm> lResultForm = new ArrayList<ComponentSettingForm>();
		for (ComponentSettingEntity result : lResultBean) {
			ComponentSettingForm form = new ComponentSettingForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(result, form, CopyOptions.COPYOPTIONS_NOEMPTY);
			form.setCnt_partial_step0("0");
			form.setCnt_partial_step2("0");
			form.setCnt_partial_step3("0");
			if (snouts != null) {
				for (SoloProductionFeatureEntity snout : snouts) {
					if (snout.getModel_id().equals(result.getModel_id())) {
						Integer cnt_partial_step0 = snout.getPace();
						Integer cnt_partial_step3 = snout.getUsed();
						Integer all = snout.getOperate_result();
						Integer cnt_partial_step2 = all - (cnt_partial_step0 + cnt_partial_step3);
						form.setCnt_partial_step0("" + cnt_partial_step0);
						form.setCnt_partial_step3("" + cnt_partial_step3);
						form.setCnt_partial_step2("" + cnt_partial_step2);
						break;
					}
				}
			}
			lResultForm.add(form);
		}

		return lResultForm;
	}

	/**
	 * 根据型号ID判定先端组件是否存在
	 * 
	 * @param ComponentSettingEntity
	 * @param conn
	 */
	public int isExistsSnoutByModelId(ActionForm form, SqlSessionManager conn)
			throws Exception {
		ComponentSettingEntity searchBean = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, searchBean, null);

		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		ComponentSettingEntity tempEntity = dao.getSnoutSettingDetail(searchBean.getModel_id());
		if(tempEntity!=null){
			return 1;
		}else{
			return 0;
		}
	}

	/**
	 * 组件设置追加
	 * 
	 * @param ComponentSettingEntity
	 * @param conn
	 */
	public void insertSnoutSetting(ActionForm form, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ComponentSettingEntity insertBean = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, insertBean, null);

		/* component_setting表插入数据 */
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		if ("".equals(insertBean.getSafety_lever())) {
			insertBean.setSafety_lever(null);
		}
		String shelf = "-";
		String layer = "-";
		String sIdentifyCode = insertBean.getIdentify_code();
		if (sIdentifyCode != null 
				&& !"".equals(sIdentifyCode)) {
			if (sIdentifyCode.indexOf("|") >= 0) {
				int lastHiphen = sIdentifyCode.lastIndexOf("|");
				shelf = sIdentifyCode.substring(0, lastHiphen);
				layer = sIdentifyCode.substring(lastHiphen + 1);
			}
		}
		insertBean.setShelf(shelf);
		insertBean.setLayer(layer);
		if (!"-1".equals(insertBean.getCnt_partial_step1())) {
			insertBean.setCnt_partial_step1("0");
		}
		if ("".equals(insertBean.getComponent_partial_id())) {
			insertBean.setComponent_partial_id(null);
		}
		dao.insertSnoutSetting(insertBean);

		snoutCompModels = null;
		snoutCompActiveModels = null;
	}

	public ComponentSettingEntity getSnoutComponentSettingDetail(
			String model_id, SqlSession conn) {
		ComponentSettingMapper settingMapper = conn.getMapper(ComponentSettingMapper.class);
		ComponentSettingEntity resultList = settingMapper.getSnoutSettingDetail(model_id);

		return resultList;
	}

	/**
	 * 组件设置更新
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void updateSnoutSetting(ActionForm form, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ComponentSettingEntity updateBean = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, updateBean, null);

		/* component_setting表插入数据 */
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		if ("".equals(updateBean.getSafety_lever())) {
			updateBean.setSafety_lever(null);
		}
		String shelf = "-";
		String layer = "-";
		String sIdentifyCode = updateBean.getIdentify_code();
		if (sIdentifyCode != null 
				&& !"".equals(sIdentifyCode)) {
			if (sIdentifyCode.indexOf("|") >= 0) {
				int lastHiphen = sIdentifyCode.lastIndexOf("|");
				shelf = sIdentifyCode.substring(0, lastHiphen);
				layer = sIdentifyCode.substring(lastHiphen + 1);
			}
		}
		updateBean.setShelf(shelf);
		updateBean.setLayer(layer);
		if ("".equals(updateBean.getComponent_partial_id())) {
			updateBean.setComponent_partial_id(null);
		}
		dao.updateSnoutSetting(updateBean);
	}

	public void deleteSnoutSetting(ActionForm form, SqlSessionManager conn) {
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);

		// 复制表单数据到对象
		ComponentSettingEntity entity = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 删除指定型号ID的NS组件子零件
		dao.deleteSnoutSetting(entity);

		// 清空型号集
		snoutCompModels = null;
		snoutCompActiveModels = null;
	}

	public void inventSnoutSubPartSets(String model_id, int sub_part_sets,
			SqlSessionManager conn) {
		inventSnoutSubPartSets(model_id, "" + sub_part_sets, conn);
	}
	public void inventSnoutSubPartSets(String model_id, String sub_part_sets,
			SqlSessionManager conn) {
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);
		ComponentSettingEntity settingEntity = new ComponentSettingEntity();

		settingEntity.setCnt_partial_step1(sub_part_sets);
		settingEntity.setModel_id(model_id);
		dao.inventSnoutSubPartSets(settingEntity);
	}

	public String getSnoutStorageHtml(String model_id, SqlSession conn) {
		ComponentSettingMapper dao = conn.getMapper(ComponentSettingMapper.class);

		ComponentSettingEntity entity = dao.getSnoutSettingDetail(model_id);

		String ret = "<div class=\"ui-widget-content\"><div style=\"margin: 15px; float: left;\">" +
			"<div class=\"ui-widget-header\" style=\"width: 372px; text-align: center;\">D／E 组件零件放置架　　　　　　　　　　　　" + entity.getShelf() + "</div>" +
			"<table class=\"condform wip-table\" style=\"width: 372px;\"><tbody><tr><td class=\"ui-state-default wip-empty\">" + entity.getLayer() + " <span class=\"model_indicate\">" +
			entity.getModel_name() + "</span></td>";

		int benchmark = 3;
		try {
			benchmark = Integer.parseInt(entity.getSafety_lever());
		} catch(Exception e){};

		for (int i = 0; i < benchmark; i++) {
			String slot = CommonStringUtil.fillChar("" + (i+1), '0', 2, true);
			ret += "<td slot=\"" + slot + "\" class=\"wip-empty\">" +  slot + "</td>";
		}

		ret += "</tr></tbody></table></div><div class=\"clear\"></div></div>";
		return ret;
	}
}

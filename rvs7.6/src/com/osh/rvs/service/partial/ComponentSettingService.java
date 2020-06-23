package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.ComponentSettingEntity;
import com.osh.rvs.form.partial.ComponentSettingForm;
import com.osh.rvs.mapper.partial.ComponentSettingMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ComponentSettingService {

	/**
	 * 取得全部型号(参照列表)
	 * @param conn
	 * @return
	 */
	public String getComponentSettings(SqlSession conn) {
		List<ComponentSettingForm> allModel = this.getAllComponentSettings(conn);
		Map<String, String> modelMap = new HashMap<String, String>();
		for (ComponentSettingForm model: allModel) {
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
	}
}

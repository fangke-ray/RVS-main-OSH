package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.partial.ConsumableListEntity;
import com.osh.rvs.bean.partial.ConsumableSubstituteEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.partial.ConsumableListMapper;
import com.osh.rvs.mapper.partial.ConsumableSubstituteMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.mapper.partial.PartialReleaseMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * 零件发放
 * 
 * @author lxb
 * 
 */
public class PartialReleaseService {
	private Logger _log=Logger.getLogger(getClass());
	/**
	 * 零件发放维修对象一览
	 * 
	 * @return
	 */
	public List<MaterialPartialForm> searchMaterialPartialRelease(ActionForm form,HttpServletRequest request,SqlSession conn) {
		
		MaterialPartialEntity entity=new MaterialPartialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		List<MaterialPartialForm> returnFormList=new ArrayList<MaterialPartialForm>();
		PartialReleaseMapper dao=conn.getMapper(PartialReleaseMapper.class);
		
		// 取得登录用户权限
		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();
		// 零件管理
		if (!privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)) {//不是零件管理员
			entity.setIsHistory(1);
		}

		List<MaterialPartialEntity> returnList=dao.searchMaterialPartialRelease(entity);
		//复制数据到表单
		BeanUtil.copyToFormList(returnList, returnFormList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialPartialForm.class);
		
		return returnFormList;
	}
	
	/**
	 * 维修对象详细信息
	 * @param form
	 * @param occur_times
	 * @param conn
	 * @return
	 */
	public MaterialForm searchMaterialPartialDetail(ActionForm form, String occur_times,SqlSession conn){
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		MaterialEntity entity = new MaterialEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		String material_id = entity.getMaterial_id();
		_log.info("recieved material_id : " + material_id);
		MaterialEntity responseEntity = dao.searchMaterialReceptByMaterialID(material_id,occur_times);
		MaterialForm responseForm = new MaterialForm();
		
		// 复制数据到到表单对象
		BeanUtil.copyToForm(responseEntity, responseForm, CopyOptions.COPYOPTIONS_NOEMPTY);

		String ocm = responseForm.getOcm();
		String ocm_name = "";// 客户
		if (!CommonStringUtil.isEmpty(ocm)) {
			ocm_name = CodeListUtils.getValue("material_direct_ocm", "" + ocm);
		}
		
		String level = responseForm.getLevel();
		String level_name = "";// 等级
		if (!CommonStringUtil.isEmpty(level)) {
			level_name = CodeListUtils.getValue("material_level", "" + level);
		}
		
		String direct = (responseForm.getDirect_flg() != null && responseForm.getDirect_flg() == "1") ? "直送" : "";// 直送

		String service_repair_flg = responseForm.getService_repair_flg();
		String service_repair_flg_name = "";// 返修标记
		if (!CommonStringUtil.isEmpty(service_repair_flg)) {
			service_repair_flg_name = CodeListUtils.getValue("material_service_repair", "" + service_repair_flg);
		}
		
		String service_free_flg = responseForm.getService_free_flg();
		String service_free_flg_name = "";// 有无偿
		if (!CommonStringUtil.isEmpty(service_free_flg)) {
			service_free_flg_name = CodeListUtils.getValue("service_free_flg", "" + service_free_flg);
		}
		
		String repairCategory = CommonStringUtil.joinBy(" ", direct, service_repair_flg_name, service_free_flg_name);// 修理分类

		responseForm.setOcmName(ocm_name);
		responseForm.setLevelName(level_name);
		responseForm.setStatus(repairCategory);
		
		return responseForm;
	}
	
	/**
	 * 零件一览
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<MaterialPartialDetailForm> secrchPartialOfRelease(ActionForm form,SqlSession conn){
		PartialReleaseMapper dao=conn.getMapper(PartialReleaseMapper.class);
		MaterialPartialDetailEntity entity=new MaterialPartialDetailEntity();
		
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		List<MaterialPartialDetailEntity> returnList=dao.secrchPartialOfRelease(entity);
		
		List<MaterialPartialDetailForm> responseFormList=new ArrayList<MaterialPartialDetailForm>();
		 
		 //复制数据到表单
		BeanUtil.copyToFormList(returnList, responseFormList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialPartialDetailForm.class);
		 
		return responseFormList;
	}
	
	/**
	 * 更新未发放数量和状态
	 * @param form
	 * @param parameterMap
	 * @param conn
	 * @throws Exception 
	 */
	public void updateWaitingQuantityAndStatus(ActionForm form,Map<String, String[]> parameterMap, String operator_id,
			SqlSessionManager conn,List<MsgInfo> errors) throws Exception{
		List<MaterialPartialDetailForm> materialPartialDetails = new AutofillArrayList<MaterialPartialDetailForm>(MaterialPartialDetailForm.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		
		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("exchange".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					if ("material_partial_detail_key".equals(column)) {
						materialPartialDetails.get(icounts).setMaterial_partial_detail_key(value[0]);
					} else if ("cur_quantity".equals(column)) {
						materialPartialDetails.get(icounts).setCur_quantity(value[0]);
					} else if("waiting_quantity".equals(column)){
						materialPartialDetails.get(icounts).setWaiting_quantity(value[0]);
					}else if("status".equals(column)){
						materialPartialDetails.get(icounts).setStatus(value[0]);
					}else if("partial_id".equals(column)){
						materialPartialDetails.get(icounts).setPartial_id(value[0]);
					}
					
				}
			}
		}
		
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		PartialMapper pMapper = conn.getMapper(PartialMapper.class);
		
		for (MaterialPartialDetailForm materialPartialDetail : materialPartialDetails) {
			MaterialPartialDetailEntity entity = new MaterialPartialDetailEntity();
			BeanUtil.copyToBean(materialPartialDetail, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

			Integer status=entity.getStatus();//消耗品标记
			Integer cur_quantity = entity.getCur_quantity();//发放数量

			ConsumableListEntity consumableListEntity;
			
			if(status == 5){//消耗品
				List<ConsumableListEntity> consumableListEntities = 
						consumableListMapper.getConsumableDetail(Integer.valueOf(entity.getPartial_id()));
				if (consumableListEntities.size() > 0) {
					consumableListEntity = consumableListEntities.get(0);
					Integer available_inventory = consumableListEntity.getAvailable_inventory();//当前有效库存
					String code = consumableListEntity.getCode();//零件code
					
					if(available_inventory < cur_quantity){ //  当前有效库存 < 发放数量
						MsgInfo msg = new MsgInfo();
						msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.consumableStorageLeak",code));
						errors.add(msg);
					}
				} else {
					PartialEntity partial = pMapper.getPartialByID(entity.getPartial_id());
					MsgInfo msg = new MsgInfo();
					msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.consumableStorageLeak",partial.getCode()));
					errors.add(msg);
				}
			}
		}

		if(errors.size()>0){
			return;
		}

		PartialReleaseMapper dao=conn.getMapper(PartialReleaseMapper.class);
		ConsumableSubstituteMapper csMapper = conn.getMapper(ConsumableSubstituteMapper.class);
		MaterialPartialMapper mpMapper = conn.getMapper(MaterialPartialMapper.class);

		for (MaterialPartialDetailForm materialPartialDetail : materialPartialDetails) {
			MaterialPartialDetailEntity updEntity = new MaterialPartialDetailEntity();
			BeanUtil.copyToBean(materialPartialDetail, updEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
			Integer status=updEntity.getStatus();
			if(status==5){ // 消耗品替代
				
				MaterialPartialDetailEntity orgEntity = mpMapper.getMaterialPartialDetailByKey(
						materialPartialDetail.getMaterial_partial_detail_key());

				if (orgEntity.getStatus() != 5) {
					ConsumableListEntity consumableListEntity  = new ConsumableListEntity();
					consumableListEntity.setPartial_id(Integer.valueOf(updEntity.getPartial_id()));
					consumableListEntity.setAvailable_inventory_temp(-updEntity.getCur_quantity());
					consumableListEntity.setOn_passage_temp(0);
					consumableListMapper.doAdjust(consumableListEntity);

					ConsumableSubstituteEntity csEntity = new ConsumableSubstituteEntity();
					BeanUtil.copyToBean(form, csEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
					csEntity.setOccasion_flg(1);
					csEntity.setPartial_id(materialPartialDetail.getPartial_id());
					csEntity.setPosition_id(orgEntity.getPosition_id());
					csEntity.setOperator_id(operator_id);
					csEntity.setQuantity(updEntity.getCur_quantity());

					csMapper.insert(csEntity);
				}

				dao.updateWaitingQuantityAndStatuOfAppend(updEntity);

			}else{
				dao.updateWaitingQuantityAndStatus(updEntity);//更新未发放数量和状态
			}
		}
	}
	
	/**
	 * 更新零件BO状态
	 * @param form
	 * @param conn
	 */
	public void updateBoFlg(ActionForm form,SqlSessionManager conn,String flag){
		PartialReleaseMapper dao=conn.getMapper(PartialReleaseMapper.class);
		MaterialPartialDetailEntity entity=new MaterialPartialDetailEntity();
		
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		List<MaterialPartialDetailEntity> resultList=dao.secrchPartialOfRelease(entity);
		if(resultList!=null){
			if(resultList.size()>0){
				Integer boFlg=null;
				for(int i=0;i<resultList.size();i++){
					MaterialPartialDetailEntity tempEntity=resultList.get(i);
					Integer status=tempEntity.getStatus();
					Integer waiting_quantity=tempEntity.getWaiting_quantity();
				
					if(waiting_quantity>0){
						boFlg=1;
						break;
					}else if(status==4 || status==3){
						boFlg=2;
					}
				}
				if(boFlg==null){
					boFlg=0;
				}
					
				MaterialPartialEntity materialPartialEntity=new MaterialPartialEntity();
				// 复制表单到数据对象
				BeanUtil.copyToBean(form, materialPartialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
				materialPartialEntity.setBo_flg(boFlg);
				MaterialPartialMapper materialPartialdao=conn.getMapper(MaterialPartialMapper.class);
				
				if("big".equals(flag)){
					materialPartialdao.updateBoFlgAndOrderDate(materialPartialEntity);
				}else if("small".equals(flag)){
					materialPartialdao.updateBoFlg(materialPartialEntity);
				}
			}
		}
	}
	/**
	 * 删除零件
	 * @param form
	 * @param parameterMap
	 * @param conn
	 * @param flag
	 */
	public void deletePartial(ActionForm form,Map<String, String[]> parameterMap,SqlSessionManager conn,String flag){
		List<MaterialPartialDetailForm> materialPartialDetails = new AutofillArrayList<MaterialPartialDetailForm>(MaterialPartialDetailForm.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("delete".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);
					if ("material_partial_detail_key".equals(column)) {
						materialPartialDetails.get(icounts).setMaterial_partial_detail_key(value[0]);
					}
				}
			}
		}
		PartialReleaseMapper dao=conn.getMapper(PartialReleaseMapper.class);
		for (MaterialPartialDetailForm materialPartialDetail : materialPartialDetails) {
			MaterialPartialDetailEntity entity = new MaterialPartialDetailEntity();
			BeanUtil.copyToBean(materialPartialDetail, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
			dao.deletePartial(entity);
		}
		
		this.updateBoFlg(form, conn, flag);
	}

	public void checkBoFlg(ActionForm form, Map<String, String[]> parameterMap,
			SqlSessionManager conn) {
		boolean loss = false;
		List<MaterialPartialDetailForm> materialPartialDetails = new AutofillArrayList<MaterialPartialDetailForm>(MaterialPartialDetailForm.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		
		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("exchange".equals(entity)) {
					String column = m.group(2);
					String[] value = parameterMap.get(parameterKey);
					int icounts = Integer.parseInt(m.group(3));

					if ("material_partial_detail_key".equals(column)) {
						materialPartialDetails.get(icounts).setMaterial_partial_detail_key(value[0]);
					} else if ("cur_quantity".equals(column)) {
						materialPartialDetails.get(icounts).setCur_quantity(value[0]);
						if ("0".equals(value[0])) {
							loss = true;
						}
					}
				}
			}
		}

		MaterialPartialEntity mpE = new MaterialPartialEntity();
		MaterialPartialMapper mpM = conn.getMapper(MaterialPartialMapper.class);
		BeanUtil.copyToBean(form, mpE, CopyOptions.COPYOPTIONS_NOEMPTY);
		if (!loss) {
			mpE.setBo_flg(0);
		} else {
			mpE.setBo_flg(1);
		}
		mpM.updateBoFlg(mpE);

		// 标记评估发放的零件为无BO
		PartialReleaseMapper dao=conn.getMapper(PartialReleaseMapper.class);
		for (MaterialPartialDetailForm materialPartialDetail : materialPartialDetails) {
			MaterialPartialDetailEntity entity = new MaterialPartialDetailEntity();
			BeanUtil.copyToBean(materialPartialDetail, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

			Integer cur_quantity = entity.getCur_quantity(); //发放数量
			if (cur_quantity > 0) {
				entity.setStatus(2);
				dao.updateStatusOfReady(entity);
			} else {
				entity.setStatus(1);
				dao.updateStatusOfReady(entity);
			}
		}
	}
	
}

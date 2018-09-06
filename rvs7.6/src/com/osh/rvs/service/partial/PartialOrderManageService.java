package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.partial.PartialOrderManageEntity;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.PartialOrderManageForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.partial.PartialOrderManageMapper;
import com.osh.rvs.service.PositionService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PartialOrderManageService {

	// 维修对象详细信息
	public List<MaterialForm> searchMaterialDetail(MaterialEntity materialEntity, SqlSession conn) {
		List<MaterialForm> materialList = new ArrayList<MaterialForm>();
		MaterialMapper materialDao = conn.getMapper(MaterialMapper.class);
		MaterialForm materialForm = new MaterialForm();
		
		//根据维修对象ID查询出维修对象的详细信息
		MaterialEntity materialDetailEntity = materialDao.loadMaterialDetail(materialEntity.getMaterial_id());
		
		CopyOptions cos = new CopyOptions();
		cos.excludeEmptyString();
		cos.excludeNull();
		cos.dateConverter(DateUtil.DATE_PATTERN, "reception_time");
		BeanUtil.copyToForm(materialDetailEntity, materialForm, cos);
		materialForm.setLevel(CodeListUtils.getValue("material_level", materialForm.getLevel()));
		materialForm.setService_repair_flg(CodeListUtils.getValue("material_service_repair",
				materialForm.getService_repair_flg()));
		materialForm.setOcm(CodeListUtils.getValue("material_direct_ocm", materialForm.getOcm()));
		
		String direct = (materialForm.getDirect_flg() != null && materialForm.getDirect_flg() == "1") ? "直送" : "";// 直送

		String service_repair_flg = materialForm.getService_repair_flg();
		String service_repair_flg_name = "";// 返修标记
		if (!CommonStringUtil.isEmpty(service_repair_flg)) {
			service_repair_flg_name = CodeListUtils.getValue("material_service_repair", "" + service_repair_flg);
		}

		String service_free_flg = materialForm.getService_free_flg();
		String service_free_flg_name = "";// 有无偿
		if (!CommonStringUtil.isEmpty(service_free_flg)) {
			service_free_flg_name = CodeListUtils.getValue("service_free_flg", "" + service_free_flg);
		}

		String repairCategory = CommonStringUtil.joinBy(" ", direct, service_repair_flg_name, service_free_flg_name);// 修理分类

		materialForm.setService_free_flg(repairCategory);
		materialList.add(materialForm);
		return materialList;
	}

	// 维修对象订购零件详细信息
	public List<PartialOrderManageForm> searchPartialOrder(PartialOrderManageForm partialOrderManageForm,
			SqlSession conn) {
		List<PartialOrderManageForm> partialOrderManageFormList = new ArrayList<PartialOrderManageForm>();
		PartialOrderManageEntity conditionBean = new PartialOrderManageEntity();
		PartialOrderManageMapper dao = conn.getMapper(PartialOrderManageMapper.class);

		BeanUtil.copyToBean(partialOrderManageForm, conditionBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//根据追加类型查询订购零件对象的信息
		List<PartialOrderManageEntity> partialOrderEntityList = dao.searchPartialOrder(conditionBean);
		BeanUtil.copyToFormList(partialOrderEntityList, partialOrderManageFormList, null, PartialOrderManageForm.class);
		PartialOrderManageForm resultForm  = null;
		if(partialOrderManageFormList.size()>0){
			for(int i =0;i<partialOrderManageFormList.size();i++){
				resultForm  =partialOrderManageFormList.get(i);
				resultForm.setBelongs(CodeListUtils.getValue("partial_append_belongs", resultForm.getBelongs()));
			}
		}
		
		return partialOrderManageFormList;
	}

	public List<PartialOrderManageForm> searchMaterial(ActionForm form, String fact, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		PartialOrderManageEntity conditionBean = new PartialOrderManageEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// 从数据库中查询记录
		PartialOrderManageMapper dao = conn.getMapper(PartialOrderManageMapper.class);
		List<PartialOrderManageEntity> lResultBean = dao.searchMaterial(conditionBean);

		// 建立页面返回表单
		List<PartialOrderManageForm> partialOrderManageForm = new ArrayList<PartialOrderManageForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, partialOrderManageForm, null, PartialOrderManageForm.class);

		return partialOrderManageForm;
	}

	// 维修对象零件详细信息显示(零件编码，订购零件件数，零件工位)-- 将零件追加分配的时候弹出的dialog
	public PartialOrderManageForm getDBMaterialPartialDetail(String material_partial_detail_key,String modelID,
			 SqlSession conn, List<MsgInfo> errors, Map<String, Object> listResponse) {
		PartialOrderManageMapper dao = conn.getMapper(PartialOrderManageMapper.class);
		PartialOrderManageForm partialOrderManageForm = new PartialOrderManageForm();
		
		PartialOrderManageEntity partialOrderManageEntity = dao.searchMaterialPartialDetailByKey(material_partial_detail_key);
		
		//工位选择下拉列表
		Map<String, String> optionsMap = new HashMap<String, String>();
		
		//订购者选择下拉列表
		Map<String,String> belongsOptionsMap = new HashMap<String,String>();

		PositionService pservice = new PositionService();
		optionsMap.put(partialOrderManageEntity.getPartial_id(), pservice.getInlineOptions(conn));
		belongsOptionsMap.put(partialOrderManageEntity.getPartial_id(), CodeListUtils.getSelectOptions("partial_append_belongs", null));

		BeanUtil.copyToForm(partialOrderManageEntity, partialOrderManageForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		listResponse.put("positionOptionsMap", optionsMap);
		listResponse.put("belongsOptionsMap", belongsOptionsMap);
		return partialOrderManageForm;
	}

	/* 定位完成之后将零件插入到维修对象零件中(只更新工位) */
	public void updateMaterialPartialDetailProcesscode(PartialOrderManageForm partialOrderManageForm,SqlSessionManager conn,
			List<MsgInfo> errors) {
		PartialOrderManageMapper dao = conn.getMapper(PartialOrderManageMapper.class);

		PartialOrderManageEntity origEntity  = new PartialOrderManageEntity();

		PartialOrderManageEntity updateEntity  = new PartialOrderManageEntity();
		BeanUtil.copyToBean(partialOrderManageForm, updateEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//新建insert
		PartialOrderManageEntity insertEntity  = new PartialOrderManageEntity();	
		BeanUtil.copyToBean(partialOrderManageForm, insertEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//原先订购数量
		int beforeQuantity =updateEntity.getQuantity();
		//输入订购数量
		int inputQuantity=0;
		
		//验证输入数量不能是空
		if (CommonStringUtil.isEmpty(partialOrderManageForm.getNew_quantity())) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_partial_detail_key");
			error.setErrcode("validator.invalidParam.invalidNumberValue");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
					"validator.invalidParam.invalidNumberValue", "定位件数", 1,
					beforeQuantity));
			errors.add(error);
		}else{
			inputQuantity=updateEntity.getNew_quantity();
		}

		//验证分割的工位和原来工位不能是同一个
		if((inputQuantity < beforeQuantity) && !CommonStringUtil.isEmpty(updateEntity.getPosition_id()) && updateEntity.getPosition_id().equals(updateEntity.getNew_position_id())){
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_partial_detail_key");
			error.setErrcode("info.partial.chooseNewProcessCode");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
					"info.partial.chooseNewProcessCode", "订购工位"));
			errors.add(error);
		}

		//验证输入数量不能是0
		if ("0".equals(partialOrderManageForm.getNew_quantity())) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_partial_detail_key");
			error.setErrcode("validator.invalidParam.invalidMoreThanZero");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
					"validator.invalidParam.invalidMoreThanZero", "定位件数", 1,
					beforeQuantity));
			errors.add(error);
		}
		
		// 验证输入数量不能大于订购数量
		if (inputQuantity > beforeQuantity) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_partial_detail_key");
			error.setErrcode("validator.invalidParam.invalidNumberRangeValue");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
					"validator.invalidParam.invalidNumberRangeValue", "定位件数", 1,
					beforeQuantity));
			errors.add(error);
		}
		
		if(errors.size()==0){
			// 验证输入数量等于订购数量
			if (beforeQuantity == inputQuantity) {
				updateEntity.setPosition_id(updateEntity.getNew_position_id());
				updateEntity.setBelongs(updateEntity.getNew_belongs());
				// 更新工位和订购者
				dao.setOverBom(updateEntity);
			}
			//待发放数量
			int waiting_quantity =updateEntity.getWaiting_quantity();
			//未签收数量
			int waiting_receive_quantity =updateEntity.getWaiting_receive_quantity();
			// 验证输入数量小于原先的订购数量
			if (beforeQuantity > inputQuantity) {
				//当待发放数量小于等于所输入的订购数量时，新建插入的待发放数量等于待发放数量(自己)，update的待发放数量等于0
				if(waiting_quantity<=inputQuantity){
					insertEntity.setWaiting_quantity(waiting_quantity);
					updateEntity.setWaiting_quantity(0);
				}
				//当待发放数量大于所输入的订购数量时，新建插入的待发放数量等于输入订购数量,update的待发放数量等于待发放数量(自己)-输入的订购数量
				if(waiting_quantity>inputQuantity){
					insertEntity.setWaiting_quantity(inputQuantity);
					updateEntity.setWaiting_quantity(waiting_quantity-inputQuantity);
				}
				
				//当未签收数量小于等于所输入的订购数量时，新建插入的未签收数量等于未签收数量(自己)，update的未签收数量等于0
				if(waiting_receive_quantity<=inputQuantity){
					insertEntity.setWaiting_receive_quantity(waiting_receive_quantity);
					updateEntity.setWaiting_receive_quantity(0);
				}
				//当未签收数量大于所输入的订购数量时，新建插入的未签收数量等于输入订购数量,update的未签收数量等于未签收数量(自己)-输入的订购数量
				if(waiting_receive_quantity>inputQuantity){
					insertEntity.setWaiting_receive_quantity(inputQuantity);
					updateEntity.setWaiting_receive_quantity(waiting_receive_quantity-inputQuantity);
				}
				
				//输入数量小于订购数量--插入剩余输入数量、责任工位、订购者
				int quantity=beforeQuantity-inputQuantity;
				//输入数量小于订购数量--插入新输入数量、责任工位、订购者
				updateEntity.setQuantity(quantity);
				// 更新工位和订购者
				dao.setOverBom(updateEntity);
				
				insertEntity.setQuantity(inputQuantity);
				insertEntity.setPosition_id(insertEntity.getNew_position_id());
				insertEntity.setBelongs(insertEntity.getNew_belongs());
				
				// 更新数量工位和订购者
				dao.insertDividedDetail(insertEntity);
			}else{
				// 更新数量工位和订购者
				dao.setOverBom(updateEntity);
			}		
		}
		// 更新工位和订购者
		//dao.setOverBom(partialOrderManageEntity);
	}
	
	/*删除选择的零件*/
	public void deletePartial(PartialOrderManageForm partialOrderManageForm,SqlSessionManager conn,
			List<MsgInfo> errors){
		PartialOrderManageMapper dao = conn.getMapper(PartialOrderManageMapper.class);
		PartialOrderManageEntity partialOrderManageEntity  = new PartialOrderManageEntity();	
		BeanUtil.copyToBean(partialOrderManageForm, partialOrderManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		/*删除零件*/
		dao.deletePartial(partialOrderManageEntity);
		
	}
	/*取消发放零件*/
	public void updateMaterialPartialDetail(PartialOrderManageForm partialOrderManageForm,SqlSessionManager conn,
			List<MsgInfo> errors){
		PartialOrderManageMapper dao = conn.getMapper(PartialOrderManageMapper.class);
		PartialOrderManageEntity partialOrderManageEntity  = new PartialOrderManageEntity();	
		BeanUtil.copyToBean(partialOrderManageForm, partialOrderManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		dao.updateMaterialPartialDetail(partialOrderManageEntity);
		
	}
	/*取消订购*/
	public void deleteMaterialPartial(PartialOrderManageForm partialOrderManageForm, SqlSessionManager conn,
			List<MsgInfo> errors) {
		PartialOrderManageMapper dao = conn.getMapper(PartialOrderManageMapper.class);
		PartialOrderManageEntity partialOrderManageEntity  = new PartialOrderManageEntity();	
		BeanUtil.copyToBean(partialOrderManageForm, partialOrderManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		//删除维修对象零件订购中的订购对象
		dao.deleteMaterialPartial(partialOrderManageEntity);
		
		//根据订购次数和material_id删除material_partial_detail表的订购对象记录
		dao.deleteMaterialPartialDetail(partialOrderManageEntity);
	}
	public void deleteMaterialPartial(String material_id, SqlSessionManager conn) {
		PartialOrderManageMapper dao = conn.getMapper(PartialOrderManageMapper.class);
		PartialOrderManageEntity partialOrderManageEntity  = new PartialOrderManageEntity();
		partialOrderManageEntity.setMaterial_id(material_id);

		//删除维修对象零件订购中的订购对象
		dao.deleteMaterialPartial(partialOrderManageEntity);
		
		//根据订购次数和material_id删除material_partial_detail表的订购对象记录
		dao.deleteMaterialPartialDetail(partialOrderManageEntity);
	}

	/*无BO*/
	public void nobo(ActionForm form, SqlSessionManager conn){
		PartialOrderManageMapper dao = conn.getMapper(PartialOrderManageMapper.class);
		PartialOrderManageEntity partialOrderManageEntity  = new PartialOrderManageEntity();
		BeanUtil.copyToBean(form, partialOrderManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//更新零件订购对象bo_flg(BO解决==>无BO)
		dao.updateMaterialPartialBoflg(partialOrderManageEntity);
		
		//更新零件订购签收明细状态(BO解决==>无BO)
		dao.updateMaterialPartialDetailStatus(partialOrderManageEntity);
	}
}

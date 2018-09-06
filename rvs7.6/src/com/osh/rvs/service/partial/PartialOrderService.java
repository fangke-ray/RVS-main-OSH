package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.mapper.partial.PartialOrderMapper;
import com.osh.rvs.service.PositionService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.copy.IntegerConverter;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PartialOrderService {

//	private static final String DEFAULT_OPTIONS = "<option value='00000000021'>252</option><option value='00000000022'>242</option><option value='00000000023'>261</option>"
//			+ "<option value='00000000025'>302</option><option value='00000000060'>303</option><option value='00000000028'>331</option><option value='00000000029'>341</option><option value='00000000030'>351</option><option value='00000000031'>361</option>"
//			+ "<option value='00000000033'>411</option><option value='00000000034'>421</option><option value='00000000035'>431</option><option value='00000000036'>441</option><option value='00000000037'>451</option><option value='00000000038'>461</option><option value='00000000039'>471</option><option value='00000000040'>481</option><option value='00000000041'>491</option>"
//			+ "<option value='00000000042'>511</option><option value='00000000043'>521</option><option value='00000000044'>531</option><option value='00000000045'>541</option><option value='00000000050'>571</option><option value='00000000063'>811</option>";

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

	//未完成分配的订购零件
	public List<MaterialPartialForm> searchMaterialPartialDetailWithStatus(SqlSession conn){	
		List<MaterialPartialForm> partialFormList = new ArrayList<MaterialPartialForm>();
		MaterialPartialMapper materialPartialDao = conn.getMapper(MaterialPartialMapper.class);
		PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);
		List<MaterialPartialDetailEntity> materialPartialDetailEntityList = dao.searchMaterialPartialDetailWithStatus();
		
		List<MaterialPartialEntity> materialPartialEntityList =new ArrayList<MaterialPartialEntity>();
		for(int i=0;i<materialPartialDetailEntityList.size();i++){
			MaterialPartialDetailEntity materialPartialDetailEntity = materialPartialDetailEntityList.get(i);
			MaterialPartialEntity materialPartialEntity = materialPartialDao.getMaterialWithPositionedByMaterialId(materialPartialDetailEntity);

			materialPartialEntity.setOccur_times(materialPartialDetailEntity.getOccur_times());

			materialPartialEntityList.add(materialPartialEntity);
		}

		// Bean复制选项
		CopyOptions cos = new CopyOptions();
		cos.excludeEmptyString();
		cos.excludeNull();
		cos.converter(IntegerConverter.getInstance(), "level", "isHistory");
		cos.fieldRename("isHistory", "positioning");

		BeanUtil.copyToFormList(materialPartialEntityList, partialFormList, cos, MaterialPartialForm.class);
		return partialFormList;
	}
	
	// 维修对象订购零件详细信息
	public List<MaterialPartialDetailForm> searchPartialOrder(MaterialPartialDetailForm materialPartialDetailForm,
			SqlSession conn) {
		List<MaterialPartialDetailForm> partialOrderFormList = new ArrayList<MaterialPartialDetailForm>();
		MaterialPartialDetailEntity conditionBean = new MaterialPartialDetailEntity();
		PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);

		BeanUtil.copyToBean(materialPartialDetailForm, conditionBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//根据追加类型查询订购零件对象的信息
		List<MaterialPartialDetailEntity> partialOrderEntityList = dao.searchPartialOrder(conditionBean);

		BeanUtil.copyToFormList(partialOrderEntityList, partialOrderFormList, null, MaterialPartialDetailForm.class);

		return partialOrderFormList;
	}

	// 维修对象零件详细信息显示(零件编码，订购零件件数，零件工位)-- 将零件追加分配的时候弹出的dialog
	public List<MaterialPartialDetailForm> getParam(MaterialPartialDetailForm materialPartialDetailForm,
			HttpServletRequest req, SqlSessionManager conn, List<MsgInfo> errors, Map<String, Object> listResponse,
			String modelID) {
		PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);
		
		//主键(material_partial_detail_key)、追加类型(belongs)
		Map<String, Object> param = new HashMap<String, Object>();		
		
		//零件多选时的所有被选择零件的主键
		List<String> keys = this.getPostKeys(req.getParameterMap());
		String belongs =  materialPartialDetailForm.getBelongs();
		if (keys.size() > 0) {
			param.put("belongs",belongs);
			param.put("keys", keys);
		} else {
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_partial_detail_key");
			error.setErrcode("validator.required.multidetail");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.multidetail", "维修对象零件",
					null, "维修对象零件"));
			errors.add(error);
		}
		/* 获取所有选中的零件在追加编辑中显示 */
		List<MaterialPartialDetailForm> materialPartialDetailFormList = new ArrayList<MaterialPartialDetailForm>();
		List<MaterialPartialDetailEntity> materialPartialDetailEntityList = dao.searchMaterialPartialDetailCode(param);
		
		//select下的options
		Map<String, String> optionsMap = new HashMap<String, String>();

		PositionService pService = new PositionService();

		/* 如果所选择的零件是1件就不需要进行追加编辑件数，否则的话要进行追加编辑分配工位 */
		for (int i = 0; i < materialPartialDetailEntityList.size(); i++) {
			MaterialPartialDetailEntity materialPartialDetailEntity = materialPartialDetailEntityList.get(i);
		
			// 如果该零件有工位（工位只有一个）
			if (!(CommonStringUtil.isEmpty(materialPartialDetailEntity.getProcess_code()))&& materialPartialDetailEntity.getProcess_code() != null) {
				//前台区分是双击弹出的零件定位 还是在追加button弹出的零件定位
					materialPartialDetailEntity.setBelongs(Integer.parseInt(belongs));
					//直接更新到其他追加 
					dao.updateByBelongs(materialPartialDetailEntity);
								
			// 如果该零件没有工位（1、零件position_id=0; 2、有多个工位）
			} else {
				// 查询维修对象下的零件的工位信息
				List<MaterialPartialDetailEntity> processCodePositionIDList = dao.searchPartialPositionBelongProcessCode(modelID, materialPartialDetailEntity.getPartial_id());
				if (processCodePositionIDList.size() > 1) {

					Map<String, String> processCodePositionMap = new HashMap<String, String>();

					// position_id作为KEY，放入Map(制作options)
					for (MaterialPartialDetailEntity materialPartialDetailBean : processCodePositionIDList) {
						materialPartialDetailEntity.setProcess_code(materialPartialDetailBean.getProcess_code());
						processCodePositionMap.put(materialPartialDetailBean.getPosition_id(),materialPartialDetailBean.getProcess_code());
					}

					// 工位options
					String processCodePosition = CodeListUtils.getSelectOptions(processCodePositionMap, "", "", false);

					optionsMap.put(materialPartialDetailEntity.getPartial_id(), processCodePosition);

				} else if (processCodePositionIDList.size() == 1) {
					optionsMap.put(materialPartialDetailEntity.getPartial_id(), pService.getInlineOptions(conn));
				} else if (processCodePositionIDList.size() == 0) {
					optionsMap.put(materialPartialDetailEntity.getPartial_id(), pService.getInlineOptions(conn));
				}
				MaterialPartialDetailForm retForm = new MaterialPartialDetailForm();
				BeanUtil.copyToForm(materialPartialDetailEntity, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);
				materialPartialDetailFormList.add(retForm);
			}
		}
		// 页面工位select组建下的options
		listResponse.put("positionOptionsMap", optionsMap);
		return materialPartialDetailFormList;
	}
	
	// 维修对象零件详细信息显示(零件编码，订购零件件数，零件工位)-- 将零件追加分配的时候弹出的dialog
	public MaterialPartialDetailForm getDBMaterialPartialDetail(String material_partial_detail_key,String modelID,
			 SqlSession conn, List<MsgInfo> errors, Map<String, Object> listResponse) {
		PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);
		MaterialPartialDetailForm materialPartialDetailForm = new MaterialPartialDetailForm();
		
		MaterialPartialDetailEntity materialPartialDetailEntity = dao.searchMaterialPartialDetailByKey(material_partial_detail_key);
		
		//select下的options
		Map<String, String> optionsMap = new HashMap<String, String>();

		// 查询维修对象下的零件的工位信息
		List<MaterialPartialDetailEntity> processCodePositionIDList = dao.searchPartialPositionBelongProcessCode(modelID, materialPartialDetailEntity.getPartial_id());
		PositionService pService = new PositionService();
		if (processCodePositionIDList.size() > 1) {

			Map<String, String> processCodePositionMap = new HashMap<String, String>();

			// position_id作为KEY，放入Map(制作options)
			for (MaterialPartialDetailEntity materialPartialDetailBean : processCodePositionIDList) {
				materialPartialDetailEntity.setProcess_code(materialPartialDetailBean.getProcess_code());
				processCodePositionMap.put(materialPartialDetailBean.getPosition_id(),materialPartialDetailBean.getProcess_code());
			}

			// 工位options
			String processCodePosition = CodeListUtils.getSelectOptions(processCodePositionMap, "", "", false);

			optionsMap.put(materialPartialDetailEntity.getPartial_id(), processCodePosition);

		} else if (processCodePositionIDList.size() == 1) {
			optionsMap.put(materialPartialDetailEntity.getPartial_id(), pService.getInlineOptions(conn));
		} else if (processCodePositionIDList.size() == 0) {
			optionsMap.put(materialPartialDetailEntity.getPartial_id(), pService.getInlineOptions(conn));
		}
		BeanUtil.copyToForm(materialPartialDetailEntity, materialPartialDetailForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		// 页面工位select组建下的options
		listResponse.put("positionOptionsMap", optionsMap);
		return materialPartialDetailForm;
	}

	// 获取被选择的零件的主键(多选)
	public List<String> getPostKeys(Map<String, String[]> parameters) {

		List<String> keys = new AutofillArrayList<String>(String.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameters.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("keys".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameters.get(parameterKey);

					if ("material_partial_detail_key".equals(column)) {
						keys.set(icounts, value[0]);
					}
				}
			}
		}

		return keys;
	}

	// 在维修对象进行分配的时候，如果未分配零件已经完成分配，定位完成(显示OK）
	public List<MaterialPartialForm> confirmProcessCode(MaterialPartialDetailForm materialPartialDetailForm,SqlSession conn,HttpServletRequest request,List<MsgInfo> errors ) {
		@SuppressWarnings("unchecked")
		List<MaterialPartialForm> partialFormList = (List<MaterialPartialForm>) request.getSession().getAttribute("partialFormPositioning");//购维修对象一览

		PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);
		MaterialPartialDetailEntity materialPartialDetailEntity = new MaterialPartialDetailEntity();
		BeanUtil.copyToBean(materialPartialDetailForm, materialPartialDetailEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		List<MaterialPartialDetailEntity> MaterialPartialDetailEntityList = dao.searchPartialOrder(materialPartialDetailEntity);

		Integer fix_type = materialPartialDetailEntity.getFix_type();//修理流程
		if(fix_type==2){//单元
			if (checkRepeatPositionOfPartial(MaterialPartialDetailEntityList)) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("partial_id");
				error.setErrcode("info.partial.needMultiPositions");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.needMultiPositions"));
				errors.add(error);
				return null;
			}
		}else{
			for(int i=0;i<MaterialPartialDetailEntityList.size();i++){
				MaterialPartialDetailEntity materialPartialDetailBean =MaterialPartialDetailEntityList.get(i);
				if(CommonStringUtil.isEmpty(materialPartialDetailBean.getProcess_code())) {
					MsgInfo error = new MsgInfo();
					error.setComponentid("material_id");
					error.setErrcode("info.partial.partialPositionValue");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.partialPositionValue", "定位工位", 1,materialPartialDetailEntity.getPartial_code()));
					errors.add(error);
					return null;
				}
			}
			
			if (checkRepeatPositionOfPartial(MaterialPartialDetailEntityList)) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("partial_id");
				error.setErrcode("info.partial.needMultiPositions");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.needMultiPositions"));
				errors.add(error);
				return null;
			}
			if(errors.size()>0) return null;
		}

		if (partialFormList != null && partialFormList.size() > 0) {
			for (int j = 0; j < partialFormList.size(); j++) {
				MaterialPartialForm resultForm = partialFormList.get(j);
				//判断当前操作对象存在于session,将当前操作对象显示成OK
				if(resultForm.getMaterial_id().equals(materialPartialDetailForm.getMaterial_id()) 
						&& resultForm.getOccur_times().equals(materialPartialDetailForm.getOccur_times())){
					// 设置1代表OK
					resultForm.setPositioning("1");
					break;
				}
			}
		}

		return partialFormList;
	}
	
	
	/**
	 * 检查零件重复定位
	 * @param list
	 * @param errors
	 */
	private boolean checkRepeatPositionOfPartial(List<MaterialPartialDetailEntity> list){
		Map<String,String> map = new HashMap<String,String>();
		
		for(int i=0;i<list.size();i++){
			MaterialPartialDetailEntity materialPartialDetailBean =list.get(i);
			String partial_id = materialPartialDetailBean.getPartial_id();//零件ID
			String process_code = materialPartialDetailBean.getProcess_code();//工位
			if(CommonStringUtil.isEmpty(process_code)){
				process_code = "";
				continue;
			}
			
			if(map.containsKey(partial_id+process_code)){
				return true;
			}else{
				map.put(partial_id+process_code, "");
			}
		}
		return false;
	}
	
	
	// 在维修对象进行分配的时候，如果未分配零件已经完成分配，定位完成(显示OK）
	public List<MaterialPartialForm> searchPartialOrderConfirm(MaterialPartialDetailForm materialPartialDetailForm,HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		List<MaterialPartialForm> partialFormList = (List<MaterialPartialForm>) request.getSession().getAttribute(
				"partialFormPositioning");
		
		if (partialFormList != null && partialFormList.size() > 0) {
			MaterialPartialForm resultForm = null;
			for (int i = 0; i < partialFormList.size(); i++) {
				resultForm = partialFormList.get(i);
				//判断当前操作对象存在于session,将当前操作对象显示成OK
				if(resultForm.getMaterial_id().equals(materialPartialDetailForm.getMaterial_id())
						&& resultForm.getOccur_times().equals(materialPartialDetailForm.getOccur_times())){
					// 设置1代表OK
					resultForm.setPositioning("1");
					break;
				}
			}
		}

		return partialFormList;
	}

	/*
	 * 1、将分配完成零件的维修对象插入到material_partial表中2、分配零件完成之后的零件更新成已经订购的状态
	 */
	public List<MaterialPartialForm> positioningComplete(HttpServletRequest request, SqlSessionManager conn, List<MsgInfo> errors) {
		// 未分配里的零件完成分配之后，完成定位，显示OK
		@SuppressWarnings("unchecked")
		List<MaterialPartialForm> partialFormList = (List<MaterialPartialForm>) request.getSession().getAttribute(
				"partialFormPositioning");
		PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);
		MaterialPartialMapper mpdao = conn.getMapper(MaterialPartialMapper.class);
		MaterialPartialForm partialForm = null;

		// 复制主键
		CopyOptions cosKey = new CopyOptions();
		cosKey.include("material_id", "occur_times");

		List<MaterialPartialForm> resultList  = new ArrayList<MaterialPartialForm>();
		for (int i = 0; i < partialFormList.size(); i++) {

			partialForm = partialFormList.get(i);

			if ("1".equals(partialForm.getPositioning())) {

				MaterialPartialEntity materialDetailEntity = new MaterialPartialEntity();
				MaterialPartialDetailEntity materialPartialDetailEntity = new MaterialPartialDetailEntity();

				// 更新维修对象订购表
				BeanUtil.copyToBean(partialForm, materialDetailEntity, cosKey);
				String occurTime = "";
				if (partialForm.getOccur_times() != null) {
					occurTime = partialForm.getOccur_times();
				} else {
					occurTime = "1";
				}
//				materialDetailEntity.setMaterial_id(partialForm.getMaterial_id());
				materialDetailEntity.setBo_flg(RvsConsts.BO_FLG_WAITING);
//				materialDetailEntity.setOccur_times(Integer.parseInt(occurTime));
				materialDetailEntity.setBo_contents(null);
				materialDetailEntity.setPosition_id(null);
				materialDetailEntity.setArrival_date(null);
				materialDetailEntity.setArrival_plan_date(null);

				MaterialPartialEntity materialPartial = mpdao.getMaterialByKey(partialForm.getMaterial_id(), occurTime);

				// 订购单已经经过定位后, 个别零件追加需定位的情况 15.11.10 SAP接口导入后
				if (materialPartial != null && materialPartial.getBo_flg() < 7) {
//					MsgInfo error = new MsgInfo();
//					error.setComponentid("material_id");
//					error.setErrcode("dbaccess.recordDuplicated");
//					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated",
//							"维修对象订购", partialForm.getSorc_no(), "维修对象订购"));
//					errors.add(error);

				} else {
					// 并行 Replace
					dao.insertMaterialPartial(materialDetailEntity);
				}

				// 更新维修对象零件明细表
				BeanUtil.copyToBean(partialForm, materialPartialDetailEntity, cosKey);
				/* 更新状态是1 导入订购后 */
//				materialPartialDetailEntity.setMaterial_id(partialForm.getMaterial_id());
//				materialPartialDetailEntity.setOccur_times(Integer.parseInt(occurTime));
				materialPartialDetailEntity.setStatus(1);

				dao.formalizeMaterialPartialDetail(materialPartialDetailEntity);
//				}
				resultList.add(partialForm);
			} else {
				continue;
			}
		}
		//移除显示OK的订购维修对象
		for(int i=0;i<resultList.size();i++){
			MaterialPartialForm pForm = resultList.get(i);
			partialFormList.remove(pForm);
		}

		return partialFormList;
	}

		// 获取被选择的零件的主键、数量、和工位ID
		public List<MaterialPartialDetailForm> getMaterialPartialDetailPositions(Map<String, String[]> parameters) {

			List<MaterialPartialDetailForm> materialPartialDetailForms = new AutofillArrayList<MaterialPartialDetailForm>(MaterialPartialDetailForm.class);
			Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

			// 整理提交数据
			for (String parameterKey : parameters.keySet()) {
				Matcher m = p.matcher(parameterKey);
				if (m.find()) {
					String entity = m.group(1);
					if ("materialPartialDetail".equals(entity)) {
						String column = m.group(2);
						int icounts = Integer.parseInt(m.group(3));
						String[] value = parameters.get(parameterKey);

						if ("material_partial_detail_key".equals(column)) {
							materialPartialDetailForms.get(icounts).setMaterial_partial_detail_key(value[0]);
						} else 	if ("quantity".equals(column)) {
							materialPartialDetailForms.get(icounts).setQuantity(value[0]);
						} else 	if ("position_id".equals(column)) {
							materialPartialDetailForms.get(icounts).setPosition_id(value[0]);
						}
					}
				}
			}

			return materialPartialDetailForms;
		}
	
	/* 定位完成之后将零件插入到维修对象零件中(更新输入的数量和工位) */
	public void updateMaterialPartialDetailQuantityAndProcesscode(HttpServletRequest req,String belongs, SqlSessionManager conn,
			List<MsgInfo> errors) {
		PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);
		Map<String, Object> param = new HashMap<String, Object>();
		
		// 获取被选择的零件的主键、数量、和工位ID
		List<MaterialPartialDetailForm> materialPartialDetailFormList = this.getMaterialPartialDetailPositions(req
				.getParameterMap());
		for (int j = 0; j < materialPartialDetailFormList.size(); j++) {
			MaterialPartialDetailForm materialPartialDetailForm = materialPartialDetailFormList.get(j);
			MaterialPartialDetailEntity materialPartialDetailEntity = new MaterialPartialDetailEntity();
			
			BeanUtil.copyToBean(materialPartialDetailForm, materialPartialDetailEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
			materialPartialDetailEntity.setMaterial_partial_detail_key((materialPartialDetailForm.getMaterial_partial_detail_key()));
			
			if (materialPartialDetailForm.getQuantity() == null
					|| materialPartialDetailForm.getQuantity() == "") {
				MsgInfo error = new MsgInfo();
				error.setComponentid("material_partial_detail_key");
				// TODO
				error.setErrcode("validator.invalidParam.invalidNumberValue");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
						"validator.invalidParam.invalidNumberValue", "定位件数", 1,
						materialPartialDetailEntity.getQuantity()));
				errors.add(error);
				continue;
			}

			if (materialPartialDetailForm.getQuantity() == "0") {
				MsgInfo error = new MsgInfo();
				error.setComponentid("material_partial_detail_key");
				error.setErrcode("validator.invalidParam.invalidNumberValue");
				// TODO
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
						"validator.invalidParam.invalidNumberValue", "定位件数", 1,
						materialPartialDetailEntity.getQuantity()));
				errors.add(error);
				continue;
			}

			List<String> keys = new ArrayList<String>();
			keys.add(materialPartialDetailEntity.getMaterial_partial_detail_key());
			param.put("keys", keys);
			// param.put("belongs",materialPartialDetailEntity.getBelongs());
			// 获取所有的被选择的订购零件信息
			List<MaterialPartialDetailEntity> materialPartialDetailEntityList = dao.searchMaterialPartialDetailCode(param);
			if (materialPartialDetailEntityList.size() == 0) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("material_partial_detail_key");
				error.setErrcode("validator.invalidParam.invalidNumberRangeValue");
				// TODO 更新对象不存在
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
						"validator.invalidParam.invalidNumberRangeValue", "定位件数", 1,
						materialPartialDetailEntity.getQuantity()));
				errors.add(error);
				continue;
			}

			// 现有记录
			materialPartialDetailEntity  = materialPartialDetailEntityList.get(0);

			// 前台输入的数量
			int input_quantity = Integer.parseInt(materialPartialDetailForm.getQuantity());

			if (input_quantity == 0) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("material_partial_detail_key");
				error.setErrcode("validator.invalidParam.invalidNumberValue");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
						"validator.invalidParam.invalidNumberValue", "定位件数", 1,
						materialPartialDetailEntity.getQuantity()));
				errors.add(error);
				continue;
			}
			// 

			// 验证输入数量不能大于订购数量
			if (input_quantity > materialPartialDetailEntity.getQuantity()) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("material_partial_detail_key");
				error.setErrcode("validator.invalidParam.invalidNumberRangeValue");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
						"validator.invalidParam.invalidNumberRangeValue", "定位件数", 1,
						materialPartialDetailEntity.getQuantity()));
				errors.add(error);
				continue;
			}

			// 检查无误，进行处理

			// 输入数量正好等于订购数量
			if (input_quantity == materialPartialDetailEntity.getQuantity()) {
				materialPartialDetailEntity.setPosition_id(materialPartialDetailForm.getPosition_id());
				materialPartialDetailEntity.setBelongs(Integer.parseInt(belongs));
				// 直接更新到其他追加
				dao.setOverBom(materialPartialDetailEntity);

			// 当输入的数量小于订购的数量时，输入的件数部分移动到追加部分(并且数量更新成输入数量，工位是选择的工位)
			} else if (input_quantity < materialPartialDetailEntity.getQuantity()) {
				// Updated by Gonglm 2014/1/9 start
				int remain = materialPartialDetailEntity.getQuantity() - input_quantity;
				// Updated by Gonglm 2014/1/9 end

				materialPartialDetailEntity.setPosition_id(materialPartialDetailForm.getPosition_id());
				materialPartialDetailEntity.setQuantity(input_quantity);
				materialPartialDetailEntity.setBelongs(Integer.parseInt(belongs));
				// 更新数量和belongs
				dao.insertMaterialPartialDetail(materialPartialDetailEntity);

				// Updated by Gonglm 2014/1/9 start
				materialPartialDetailEntity.setQuantity(remain);
				//materialPartialDetailEntity.setQuantity(materialPartialDetailEntity.getQuantity() - input_quantity);
				// Updated by Gonglm 2014/1/9 end

				dao.substractSelf(materialPartialDetailEntity);
			} 
		}
	}


	public void cancel(String material_id, String occur_times, SqlSessionManager conn, List<MsgInfo> errors) {
		PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);
		dao.cancelPartialOrder(material_id, occur_times);
	}	
	/* 定位完成之后将零件插入到维修对象零件中(更新工位+订购数量) */
	public void updateMaterialPartialDetailProcesscode(MaterialPartialDetailForm materialPartialDetailForm,SqlSessionManager conn,
			List<MsgInfo> errors) {
		PartialOrderMapper dao = conn.getMapper(PartialOrderMapper.class);
		//更新update
		MaterialPartialDetailEntity updateEntity  = new MaterialPartialDetailEntity();	
		BeanUtil.copyToBean(materialPartialDetailForm, updateEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//新建insert
		MaterialPartialDetailEntity insertEntity  = new MaterialPartialDetailEntity();
		BeanUtil.copyToBean(materialPartialDetailForm, insertEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//原先订购数量
		int beforeQuantity = updateEntity.getQuantity();
		//输入订购数量
		int inputQuantity  = 0;
		
		//验证输入的订购数量不能是空
		if(CommonStringUtil.isEmpty(materialPartialDetailForm.getNew_quantity())){
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_partial_detail_key");
			error.setErrcode("validator.invalidParam.invalidNumberValue");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
					"validator.invalidParam.invalidNumberValue", "定位件数", 1,
					beforeQuantity));
			errors.add(error);
		}else{
			inputQuantity = updateEntity.getNew_quantity();
		}
		
		//原先的订购工位
		String position_id = updateEntity.getPosition_id();
		//重新选择的订购工位
		String new_position_id = updateEntity.getNew_position_id();
		
		if(!CommonStringUtil.isEmpty(position_id)){
			//验证原先的订购工位和重新选择的订购工位不相同
			if(position_id.equals(new_position_id)){
				MsgInfo error = new MsgInfo();
				error.setComponentid("material_partial_detail_key");
				error.setErrcode("info.partial.chooseNewProcessCode");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
						"info.partial.chooseNewProcessCode", "订购工位"));
				errors.add(error);
			}	
		}
		insertEntity.setPosition_id(new_position_id);			
		
		//验证输入的数量不能小于等于0
		if(updateEntity.getNew_quantity() <=0){
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_partial_detail_key");
			error.setErrcode("validator.invalidParam.invalidMoreThanZero");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
					"validator.invalidParam.invalidMoreThanZero", "定位件数", 1,
					beforeQuantity));
			errors.add(error);
		}
		
		//验证输入的数量不能大于原先的订购数量
		if(inputQuantity > beforeQuantity){
			MsgInfo error = new MsgInfo();
			error.setComponentid("material_partial_detail_key");
			error.setErrcode("validator.invalidParam.invalidNumberRangeValue");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
					"validator.invalidParam.invalidNumberRangeValue", "定位件数", 1,
					beforeQuantity));
			errors.add(error);
		}
		
		//待发放数量
		int waiting_quantity =updateEntity.getWaiting_quantity();
		//未签收数量
		int waiting_receive_quantity =updateEntity.getWaiting_receive_quantity();
		
		//当未出现错误的情况下
		if(errors.size()==0){
			// 输入数量等于订购数量
			if (beforeQuantity == inputQuantity) {
				updateEntity.setPosition_id(new_position_id);
				// 更新工位和订购者
				dao.setOverBom(updateEntity);
			}
			//输入数量小于原先订购数量
			if(beforeQuantity >inputQuantity){
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
				
				// 更新数量工位和订购者
				dao.insertDetail(insertEntity);
			}else{
				// 直接更新到其他追加
				dao.setOverBom(updateEntity);
			}
		}
		/*// 直接更新到其他追加
		dao.setOverBom(updateEntity);*/
	}

}

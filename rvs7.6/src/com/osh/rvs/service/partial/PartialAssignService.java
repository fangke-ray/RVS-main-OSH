package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.mapper.partial.PartialAssignMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 零件签收
 * 
 * @author lxb
 * 
 */
public class PartialAssignService {

	/**
	 * 取得数据库内当前零件订购信息
	 * @param conn
	 * @return
	 */
	public Map<String, List<MaterialPartialDetailEntity>> searchMaterialPartialDetailMap(SqlSession conn) {
		Map<String, List<MaterialPartialDetailEntity>> responseMap = new HashMap<String, List<MaterialPartialDetailEntity>>();
		PartialAssignMapper dao = conn.getMapper(PartialAssignMapper.class);

		List<MaterialPartialDetailEntity> entityList = dao.searchMaterialPartialDetail();

		MaterialPartialDetailEntity enity = null;

		for (int i = 0; i < entityList.size(); i++) {
			enity = entityList.get(i);
			String materialId = enity.getMaterial_id();// 维修对象 ID
			Integer occurTimes = enity.getOccur_times();// 订购次数

			if (!responseMap.containsKey(materialId + occurTimes)) {
				List<MaterialPartialDetailEntity> mList = new ArrayList<MaterialPartialDetailEntity>();
				mList.add(enity);
				responseMap.put(materialId + occurTimes, mList);
			} else {
				List<MaterialPartialDetailEntity> mList = responseMap.get(materialId + occurTimes);
				mList.add(enity);
				responseMap.put(materialId + occurTimes, mList);
			}
		}
		return responseMap;
	}

	/**
	 * 零件发放维修对象详细数据
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public MaterialForm searchMaterialAssignDetail(ActionForm form, String occur_times,SqlSession conn) {
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		MaterialEntity entity = new MaterialEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		MaterialEntity responseEntity = dao.searchMaterialReceptByMaterialID(entity.getMaterial_id(),occur_times);
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
	 * 零件签收数据分配
	 * @param partialSessionMap 
	 * @param partialReceptMap 
	 * 
	 * @param form
	 * @param request
	 * @param conn
	 * @return
	 */

	public Map<String, Map<String, List<MaterialPartialDetailForm>>> searchPartialAssignDetail(String materialIDAndoccureTimes,
			Map<String, List<MaterialPartialDetailEntity>> partialSessionMap, Map<String, Map<String, Integer>> partialReceptMap, HttpServletRequest request) {
		Map<String, Map<String, List<MaterialPartialDetailForm>>> resMap = new HashMap<String, Map<String, List<MaterialPartialDetailForm>>>();

		Map<String, List<MaterialPartialDetailForm>> returnMap = new HashMap<String, List<MaterialPartialDetailForm>>();

		Map<String, List<MaterialPartialDetailEntity>> dbMap = partialSessionMap;
		if (dbMap == null) {

			@SuppressWarnings("unchecked")
			Map<String, List<MaterialPartialDetailEntity>> redredMap = (Map<String, List<MaterialPartialDetailEntity>>) request
					.getSession().getAttribute("partialSessionMap");
			dbMap = redredMap;
		}		

		Map<String, Map<String, Integer>> redMap = partialReceptMap;
		if (redMap == null) {

			@SuppressWarnings("unchecked")
			Map<String, Map<String, Integer>> redredMap = (Map<String, Map<String, Integer>>) request.getSession().getAttribute("partialReceptMap");// 文件里
			redMap = redredMap;
		}

		List<MaterialPartialDetailForm> arriveList = new ArrayList<MaterialPartialDetailForm>();// 到货零件
		List<MaterialPartialDetailForm> unarriveList = new ArrayList<MaterialPartialDetailForm>();// 未到货零件
		
		Set<String> materialIDAndOccurTimesSet = redMap.keySet();
		Iterator<String> iterator = materialIDAndOccurTimesSet.iterator();

		//List<MaterialPartialDetailForm> updateList = new ArrayList<MaterialPartialDetailForm>();
		@SuppressWarnings("unchecked")
		Map<String, List<MaterialPartialDetailForm>> updatemap =
				(Map<String, List<MaterialPartialDetailForm>>) request.getSession().getAttribute("updateSessionMap");

		if (updatemap == null)
			updatemap = new HashMap<String, List<MaterialPartialDetailForm>>();// 用于更新

		while (iterator.hasNext()) {
			String materialIDAndOccurTimes = iterator.next();
			if(!materialIDAndoccureTimes.equals(materialIDAndOccurTimes)){
			}else{
				if (dbMap.containsKey(materialIDAndOccurTimes)) {
					List<MaterialPartialDetailEntity> partialSessionList = dbMap.get(materialIDAndOccurTimes);// DB
					List<MaterialPartialDetailForm> formList = new ArrayList<MaterialPartialDetailForm>();
					BeanUtil.copyToFormList(partialSessionList, formList, null, MaterialPartialDetailForm.class);

					Map<String, Integer> partialMap = redMap.get(materialIDAndOccurTimes);// 文件里


					for (int i = 0; i < formList.size(); i++) {
						MaterialPartialDetailForm mForm = formList.get(i);
						//updateList.add(mForm);
						String partial_id = mForm.getPartial_id();// 零件ID
						if (partialMap.containsKey(partial_id)) {
							arriveList.add(mForm);
						} else {
							unarriveList.add(mForm);
						}
					}

					updatemap.put(materialIDAndOccurTimes, arriveList);
				}
			}
		}

		returnMap.put("arriveList", arriveList); // 到货零件
		returnMap.put("unarriveList", unarriveList); // 未到货零件

		request.getSession().setAttribute("updateSessionMap", updatemap);
		resMap.put("partial", returnMap);

		return resMap;
	}

	/**
	 * 更新零件发放对象状态
	 * 
	 * @param request
	 * @return
	 */
	public List<MaterialPartialForm> changeMaterialStatus(ActionForm form,HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		List<MaterialPartialForm> sessionFormList = (List<MaterialPartialForm>) request.getSession().getAttribute(
				"responseSessionForms");
		
		MaterialPartialDetailForm materialPartialDetailForm=(MaterialPartialDetailForm)form;
		String materialID=materialPartialDetailForm.getMaterial_id();
		String occur_times= materialPartialDetailForm.getOccur_times();
		
		List<MaterialPartialForm> responseFormList = new ArrayList<MaterialPartialForm>();
		if (sessionFormList.size() > 0) {
			MaterialPartialForm partialForm = null;
			for (int index = 0; index < sessionFormList.size(); index++) {
				partialForm = sessionFormList.get(index);
				if(partialForm.getIsHistory()=="1"){
				}else{
					if(materialID.equals(partialForm.getMaterial_id()) && occur_times.equals(partialForm.getOccur_times())){
						partialForm.setIsHistory("1");
					}else{
						partialForm.setIsHistory("");
					}
				}
				responseFormList.add(partialForm);
			}
		}

		return responseFormList;
	}

	/**
	 *  更新零件订购签收明细
	 * @param request
	 * @param conn
	 */
	public void updateMaterialPartialDetail(HttpServletRequest request, SqlSessionManager conn, List<MsgInfo> errors) {
		@SuppressWarnings("unchecked")
		Map<String, List<MaterialPartialDetailForm>> formMap = (Map<String, List<MaterialPartialDetailForm>>) request
				.getSession().getAttribute("updateSessionMap");

		@SuppressWarnings("unchecked")
		List<MaterialPartialForm> sessionFormList = (List<MaterialPartialForm>) request.getSession().getAttribute(
				"responseSessionForms");

		PartialAssignMapper dao = conn.getMapper(PartialAssignMapper.class);

		if (sessionFormList != null) {
			MaterialPartialForm partialForm = null;
			List<MaterialPartialForm> delList = new ArrayList<MaterialPartialForm>(); 
			for (int i = 0; i < sessionFormList.size(); i++) {
				// 维修对象行
				partialForm = sessionFormList.get(i);
				String isHistory = partialForm.getIsHistory();
				String materialID = partialForm.getMaterial_id();
				String occureTimes = partialForm.getOccur_times();
				if ("1".equals(isHistory)){	//判断是否确认发放
					if(formMap!=null && !formMap.isEmpty()){
						Set<String> set = formMap.keySet();
						Iterator<String> iterator = set.iterator();
						while (iterator.hasNext()) {
							String materialIDAndOccureTimes=iterator.next();
							// 可以更新的行
							if(materialIDAndOccureTimes.equals(materialID+occureTimes)){
								List<MaterialPartialDetailForm> formList = formMap.get(materialID + occureTimes);
								if (formList != null) {
									MaterialPartialDetailEntity entity = new MaterialPartialDetailEntity();
									MaterialPartialDetailForm form = null;
									// deleted by Gonglm 2014/1/10 start
//									// 不是首次更新
//									boolean hasRecepted = true;
									// deleted by Gonglm 2014/1/10 end
									for (int index = 0; index < formList.size(); index++) {
										form = formList.get(index);
										BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
										// deleted by Gonglm 2014/1/10 start
										// if (entity.getStatus() == 1) hasRecepted = false;
										// deleted by Gonglm 2014/1/10 end

										dao.updateMaterialPartialDetail(entity);
									}
									// 更新Bo状态改为每日事务脚本
//									MaterialPartialDetailEntity materialpartialDetailEntity=new MaterialPartialDetailEntity();
//									materialpartialDetailEntity.setMaterial_id(materialID);
//									materialpartialDetailEntity.setOccur_times(Integer.parseInt(occureTimes));
//									materialpartialDetailEntity.setStatus(3);
//									int status=dao.searchStatus(materialpartialDetailEntity);
//									int bo_flg=0;
//									if(status==1){
//										bo_flg=1;
//									}else{
//										materialpartialDetailEntity.setStatus(4);
//										status=dao.searchStatus(materialpartialDetailEntity);
//										if(status==1){
//											bo_flg=2;
//										}else{
//											 bo_flg=0;
//										}
//									}

									// 更新维修对象订购信息
									MaterialPartialEntity materialPartialEntity=new MaterialPartialEntity();
									BeanUtil.copyToBean(partialForm, materialPartialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
									MaterialPartialMapper mDao=conn.getMapper(MaterialPartialMapper.class);
									// deleted by Gonglm 2014/1/10 start
//									if(hasRecepted){
//										materialPartialEntity.setBo_flg(bo_flg);
//										mDao.updateBoFlg(materialPartialEntity);
//									}else{
										// deleted by Gonglm 2014/1/10 start
										// 首次发放后，更新零件到货日
										mDao.updateOrderDate(materialPartialEntity);
								// deleted by Gonglm 2014/1/10 start
//									}
								// deleted by Gonglm 2014/1/10 end
								}
							}
						}
						delList.add(partialForm);// sessionFormList.remove(i);
					}
				}
			}
			
			for (MaterialPartialForm del : delList) {
				sessionFormList.remove(del);
			}

			request.getSession().removeAttribute("responseSessionForms");
			request.getSession().setAttribute("responseSessionForms", sessionFormList);
		}
	}
}

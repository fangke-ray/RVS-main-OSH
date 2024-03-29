/**  
* @Title: QuotationService.java
* @Package com.osh.rvs.service
* @Description: TODO
* @author liuxb
* @date 2017-3-9 下午2:08:28
*/ 
package com.osh.rvs.service.qf;

import static com.osh.rvs.common.RvsConsts.POSITION_QUOTATION_D;
import static com.osh.rvs.common.RvsConsts.POSITION_QUOTATION_D_PREV;
import static com.osh.rvs.common.RvsConsts.POSITION_QUOTATION_N;
import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.partial.MaterialPartPrelistEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.inline.MaterialCommentMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.inline.ScheduleMapper;
import com.osh.rvs.mapper.qf.QuotationMapper;
import com.osh.rvs.service.CustomerService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.OptionalFixService;
import com.osh.rvs.service.ProcessAssignService;
import com.osh.rvs.service.partial.MaterialPartInstructService;

import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**  
 * @Title: QuotationService.java
 * @Package com.osh.rvs.service
 * @Description: TODO
 * @author liuxb
 * @date 2017-3-9 下午2:08:28
 */
public class QuotationService {
	Logger _log = Logger.getLogger(getClass());
	
	/**
	 * 更新维修对象信息
	 * @param bean
	 * @param conn
	 * @throws Exception
	 */
	public void updateMaterial(MaterialEntity entity, SqlSessionManager conn) throws Exception {
		QuotationMapper dao = conn.getMapper(QuotationMapper.class);
		if (!isEmpty(entity.getCustomer_name())) {
			CustomerService cservice = new CustomerService();
			entity.setCustomer_id(cservice.getCustomerStudiedId(entity.getCustomer_name(), entity.getOcm(), conn));
		}

		String series = null;
		if (entity.getModel_id() != null) {
			ModelService mdlService = new ModelService();
			ModelEntity model = mdlService.getDetailEntity(entity.getModel_id(), conn);
			series = model.getSeries();

			if ("URF".equals(series)) {
				MaterialTagService tagService = new MaterialTagService();
				if (!tagService.checkTagsXorByMaterialId(entity.getMaterial_id(), 
						MaterialTagService.TAG_CONTRACT_RELATED, MaterialTagService.TAG_SHIFT_CONTRACT_RELATED,
						conn)) {
					series = "URF-UNCONTRACT_RELATED";
				}
			}
		}
		Date[] workDates = RvsUtils.getTimeLimit(entity.getAgreed_date(), entity.getLevel(), 
				entity.getFix_type(), entity.getScheduled_expedited(), series, conn, false);
		Date workDate = workDates[0];
		entity.setScheduled_date(workDate);

		dao.updateMaterial(entity);
	}

	public void getProccessingData(Map<String, Object> responseBean, String material_id, 
			LoginData user, SqlSession conn) throws Exception {
		QuotationService service = new QuotationService();
		// 取得维修对象信息。
		MaterialForm mform = service.getMaterialInfo(material_id, user,conn);
		responseBean.put("mform", mform);

		boolean anmlExp = "true".equals(mform.getAnml_exp());
		List<String> anmlProcesses = null;
		ProcessAssignService paService = new ProcessAssignService();

		if (anmlExp) {
			anmlProcesses = ProcessAssignService.getAnmlProcesses(conn);

			String pat_id = mform.getPat_id();

			if (!RvsUtils.isLightFix(mform.getLevel()) && pat_id != null && anmlProcesses.size() > 0) {
				if (!anmlProcesses.contains(pat_id)) {
					mform.setPat_id(anmlProcesses.get(0));
					String patName = paService.getDetail(anmlProcesses.get(0), conn).getName();
					mform.setSection_name(patName);
				}
			}
		}

		if ("00000000259".equals(mform.getModel_id()) 
				|| "00000000319".equals(mform.getModel_id())) {
			responseBean.put("special_notice", mform.getModel_name());
		}

		// 判断是否CCD盖玻璃更换对象，是的话可标记
		if (RvsUtils.getCcdModels(conn).contains(mform.getModel_id())) {
			MaterialTagService mtService = new MaterialTagService();
			if (mtService.checkTagByMaterialId(material_id, 
					MaterialTagService.TAG_FOR_CCD_REPLACE, conn).size() > 0) {
				responseBean.put("tag_ccd", "1");
			} else {
				responseBean.put("tag_ccd", "-1");
			}
		}

		// 判断是否CCD线更换对象，是的话可选择流程
		if (RvsUtils.getCcdLineModels(conn).contains(mform.getModel_id())) {
			Map<String, String> patState = new HashMap<String, String>();
			patState.put("pat_id", mform.getPat_id());
			patState.put("pat_name", mform.getSection_name());
			responseBean.put("patState", patState);

			if (anmlExp) {
				if (anmlProcesses.size() > 0) {
					responseBean.put("dpResult", 
							paService.getDerivePair(anmlProcesses.get(0), "3", conn));
				}
			} else {
				ModelService mService = new ModelService();
				ModelEntity mEntity = mService.getDetailEntity(mform.getModel_id(), conn);

				if (mEntity != null && mEntity.getDefault_pat_id() != null) {
					responseBean.put("dpResult", 
							paService.getDerivePair(mEntity.getDefault_pat_id(), "3", conn));
				}
			}
		}

		// 判断是否NS组件组装对象 22.11.15 暂停 NS组件组装， 不做显示
//		Set<String> nsCompModels = ComponentSettingService.getNsCompModels(conn);
//		if (nsCompModels.contains(mform.getModel_id())) {
//			responseBean.put("component_setting", "csBean.getIdentify_code()");
//		}

		// 取得维修对象的作业标准时间。
		responseBean.put("leagal_overline", RvsUtils.getZeroOverLine(mform.getModel_name(), mform.getCategory_name(), user, null));

		// 判断维修品是否已经取得零件指示单
		MaterialPartInstructService mpiService = new MaterialPartInstructService();
		List<MaterialPartPrelistEntity> instuctListForMaterial = mpiService.getInstuctListForMaterial(material_id, conn);
		if (instuctListForMaterial != null && instuctListForMaterial.size() > 0) {
			responseBean.put("instuct_obj", true);
		}

		// 取得维修品的选择报价一览
		if (!RvsUtils.isPeripheral(mform.getLevel())) {
			OptionalFixService ofService = new OptionalFixService();
			ofService.getMaterialOptionalFix(material_id, mform, responseBean, conn);
		}
	}

	public MaterialForm getMaterialInfo(String material_id, LoginData user, SqlSession conn) {
		MaterialForm materialForm = new MaterialForm();
		QuotationMapper dao = conn.getMapper(QuotationMapper.class);
		MaterialEntity materialEntity = dao.getMaterialDetail(material_id);
		if (materialEntity.getScheduled_expedited() != null && materialEntity.getScheduled_expedited() >= 4) {
			materialEntity.setOutline_time(RvsUtils.switchWorkDate(materialEntity.getReception_time(), 4, conn));
		}
		BeanUtil.copyToForm(materialEntity, materialForm, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 取得维修对象备注
		MaterialCommentMapper mapper = conn.getMapper(MaterialCommentMapper.class);
		String comment = mapper.getMyMaterialComment(material_id, user.getOperator_id());
		materialForm.setComment(comment);

		String otherComment = mapper.getMaterialComments(material_id, user.getOperator_id());
		materialForm.setScheduled_manager_comment(otherComment);

		if (MaterialTagService.getAnmlMaterials(conn).contains(material_id)) {
			materialForm.setAnml_exp("true");
		}

		return materialForm;
	}

	public void listRefresh(LoginData user, Map<String, Object> listResponse, SqlSession conn) {
		String position_id = user.getPosition_id();
		boolean join151And161 = "00000000013".equals(position_id) || "00000000014".equals(position_id);
		String[] position_ids = null; 
		if (join151And161) {
			position_ids = new String[]{"00000000013", "00000000014"};
		} else {
			position_ids = new String[]{position_id};
		}

		QuotationMapper qDao = conn.getMapper(QuotationMapper.class);
		// 取得待报价处理对象一览 151 or 161
		List<MaterialEntity> waitings = qDao.getWaitings(position_ids);

		// 取得暂停一览 151 or 161
		List<MaterialEntity> paused = qDao.getPaused(position_ids);

		// 取得今日已完成处理对象一览
		List<MaterialEntity> finished = qDao.getFinished(position_ids);

		List<MaterialForm> waitingsForm = new ArrayList<MaterialForm>();
		List<MaterialForm> pausedForm = new ArrayList<MaterialForm>();
		List<MaterialForm> finishedForm = new ArrayList<MaterialForm>();

		BeanUtil.copyToFormList(waitings, waitingsForm, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);

		String process_code = "";

		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);

		for (MaterialEntity pe : paused) {
			MaterialForm pausedMaterialForm = new MaterialForm();
			BeanUtil.copyToForm(pe, pausedMaterialForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			// 工位特殊暂停理由
			// 优先 特殊暂停 》 暂停 》  其他暂停 》 未处理 》 中断
			if (pe.getNow_pause_reason() != null)
				if (pe.getNow_pause_reason() >= 70) {
					process_code = "";
					if (POSITION_QUOTATION_N.equals(pe.getProcessing_position())) process_code = "151";//TODO zhenggui
					else if (POSITION_QUOTATION_D.equals(pe.getProcessing_position())) process_code = "161";
					else if (POSITION_QUOTATION_D_PREV.equals(pe.getProcessing_position())) process_code = "160";

					String sReason = PathConsts.POSITION_SETTINGS.getProperty("step." + process_code + "." + pe.getNow_pause_reason());
					pausedMaterialForm.setStatus("" + pe.getNow_pause_reason());
					if (sReason == null) {
						pausedMaterialForm.setOperate_result("");
					} else {
						pausedMaterialForm.setOperate_result(sReason);
					}
					if (pe.getNow_operate_result() != null) {
						Integer operateResult302 = pfMapper.getMaterialPositionProcessing(pe.getMaterial_id(), "00000000025");
						if (operateResult302 == null || operateResult302 == 0) {
							pausedMaterialForm.setProcessing_position2("未处理");
						} else if (operateResult302 == 2) {
							pausedMaterialForm.setProcessing_position2("已完成");
						} else {
							pausedMaterialForm.setProcessing_position2("进行中");
						}
					}
				} else if (pe.getNow_pause_reason() >= 40){
					pausedMaterialForm.setOperate_result("暂停");
					pausedMaterialForm.setStatus("30");
				} else if (pe.getNow_pause_reason() < 20){
					pausedMaterialForm.setOperate_result("中断");
					pausedMaterialForm.setStatus("10");
				} else {
					pausedMaterialForm.setOperate_result("未处理");
					pausedMaterialForm.setStatus("20");
				}
			else {
				_log.warn(pausedMaterialForm.getMaterial_id() + "出现未分类的暂停:" + pe.getNow_pause_reason());
				pausedMaterialForm.setOperate_result("其他暂停");
				pausedMaterialForm.setStatus("30");
			}
			pausedForm.add(pausedMaterialForm);
		}

		ScheduleMapper saMapper = conn.getMapper(ScheduleMapper.class);
		Set<String> ccdLineTargets = saMapper.getCcdLineTargets();

		for (MaterialEntity pe : finished) {
			MaterialForm finMaterialForm = new MaterialForm();
			BeanUtil.copyToForm(pe, finMaterialForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			if (RvsUtils.getCcdModels(conn).contains(finMaterialForm.getModel_id())) {
				if (!"1".equals(finMaterialForm.getScheduled_expedited())) {
					finMaterialForm.setScheduled_expedited("-1");
				}
			} else {
				finMaterialForm.setScheduled_expedited(null);
			}

			if (RvsUtils.getCcdLineModels(conn).contains(finMaterialForm.getModel_id())
					&& !"1".equals(finMaterialForm.getLevel())) {
				if (ccdLineTargets.contains(pe.getMaterial_id())) {
					finMaterialForm.setProcessing_position2("更换");
				} else {
					finMaterialForm.setProcessing_position2("不更换");
				}
			} else {
				finMaterialForm.setProcessing_position2("-");
			}

			if (pe.getLevel() == null) {
				finMaterialForm.setLevel("0");
			}

			finishedForm.add(finMaterialForm);
		}

//		BeanUtil.copyToFormList(finished, finishedForm, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);

		listResponse.put("waitings", waitingsForm);
		listResponse.put("paused", pausedForm);
		listResponse.put("finished", finishedForm);
	}

	public void listFinishRefresh(LoginData user, Map<String, Object> listResponse, SqlSession conn) {
		String position_id = user.getPosition_id();
		boolean join151And161 = "00000000013".equals(position_id) || "00000000014".equals(position_id);
		String[] position_ids = null; 
		if (join151And161) {
			position_ids = new String[]{"00000000013", "00000000014"};
		} else {
			position_ids = new String[]{position_id};
		}

		QuotationMapper qDao = conn.getMapper(QuotationMapper.class);

		// 取得今日已完成处理对象一览
		List<MaterialEntity> finished = qDao.getFinished(position_ids);

		List<MaterialForm> finishedForm = new ArrayList<MaterialForm>();

		for (MaterialEntity pe : finished) {
			MaterialForm finMaterialForm = new MaterialForm();
			BeanUtil.copyToForm(pe, finMaterialForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			if (RvsUtils.getCcdModels(conn).contains(finMaterialForm.getModel_id())) {
				if (!"1".equals(finMaterialForm.getScheduled_expedited())) {
					finMaterialForm.setScheduled_expedited("-1");
				}
			} else {
				finMaterialForm.setScheduled_expedited(null);
			}

			if (pe.getLevel() == null) {
				finMaterialForm.setLevel("0");
			}

			finishedForm.add(finMaterialForm);
		}

		listResponse.put("finished", finishedForm);
	}

	public void updateComment(MaterialForm materialForm,LoginData user,SqlSessionManager conn){
		//画面上提交的备注内容为空时，进一步查询原来是否已经存在备注了
		if(CommonStringUtil.isEmpty(materialForm.getComment())){
			MaterialService materialService = new MaterialService();
			materialService.removeComment(materialForm.getMaterial_id(), user.getOperator_id(), conn);
		}else{//画面上提交的备注内容不为空
			// 更新维修对象备注
			MaterialService materialService = new MaterialService();
			materialService.updateMaterialComment(materialForm.getMaterial_id(), user.getOperator_id(), materialForm.getComment(),conn);
		}
	}
}

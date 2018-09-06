package com.osh.rvs.service.inline;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.infect.DryingJobEntity;
import com.osh.rvs.bean.inline.DryingProcessEntity;
import com.osh.rvs.form.inline.DryingProcessForm;
import com.osh.rvs.mapper.infect.DryingJobMapper;
import com.osh.rvs.mapper.inline.DryingProcessMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class DryingProcessService {
	private static final Integer GET_SELF = 1;
	private static final Integer GET_LIKEY = 2;

	public List<DryingProcessForm> search(ActionForm form,SqlSession conn){
		//复制表单数据到对象
		DryingProcessEntity entity = new DryingProcessEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		DryingProcessMapper dao = conn.getMapper(DryingProcessMapper.class);
		List<DryingProcessEntity> list = dao.search(entity);
		
		List<DryingProcessForm> respList = new ArrayList<DryingProcessForm>();
		if(list!=null && list.size()>0){
			//复制数据到表单对象
			BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, DryingProcessForm.class);
		}
		
		return respList;
	}

	/**
	 * 取得维修对象在工位的干燥作业信息
	 *
	 * @param material_id
	 * @param section_id
	 * @param position_id
	 * @param conn
	 * @return
	 */
	public List<DryingProcessForm> getJobsOnMaterial(String material_id, 
			String section_id, String position_id, SqlSession conn) {
		DryingJobMapper djMapper = conn.getMapper(DryingJobMapper.class);

		List<DryingJobEntity> jobs = djMapper.getDryingJobWithMaterialInPosition(material_id, section_id, position_id);

		return getJobs(jobs, conn);
	}

	public List<DryingProcessForm> getJobsOnModel(String model_id,
			String section_id, String position_id, SqlSession conn) {
		DryingJobMapper djMapper = conn.getMapper(DryingJobMapper.class);

		List<DryingJobEntity> jobs = djMapper.getDryingJobWithModelInPosition(model_id, section_id, position_id);

		return getJobs(jobs, conn);
	}

	public List<DryingProcessForm> getJobs(List<DryingJobEntity> jobs, SqlSession conn) {

		DryingProcessMapper dpMapper = conn.getMapper(DryingProcessMapper.class);

		List<DryingProcessForm> result = new ArrayList<DryingProcessForm>();
		Map<String, DryingProcessEntity> devices = new HashMap<String, DryingProcessEntity>();

		for (DryingJobEntity job : jobs) {
			DryingProcessForm dpForm = new DryingProcessForm();
			BeanUtil.copyToForm(job, dpForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			// 硬化条件
			dpForm.setHardening_condition(
					CodeListUtils.getValue("drying_hardening_condition", dpForm.getHardening_condition()));

			String sdeviceManageId = job.getDevice_manage_id();
			if (sdeviceManageId != null) {
				if (!devices.containsKey(sdeviceManageId)) {
					DryingProcessEntity condition = new DryingProcessEntity();
					condition.setDevice_manage_id(sdeviceManageId);
					condition.setStatus(GET_SELF);
					List<DryingProcessEntity> dryingJobs = dpMapper.getDryingJobWithUsedSlots(condition);
					if (dryingJobs.size() > 0) {
						DryingProcessEntity dryingJob = dryingJobs.get(0);
						devices.put(sdeviceManageId, dryingJob);
					}
				}

				DryingProcessEntity dpEntity = devices.get(sdeviceManageId);
				if (dpEntity != null) {
					BeanUtil.copyToForm(dpEntity, dpForm, CopyOptions.COPYOPTIONS_NOEMPTY);
				}
			}

			result.add(dpForm);
		}
		return result;
	}
	/**
	 * 取得烘干设备库位使用情况
	 * @param req
	 * @param conn
	 * @return
	 */
	public List<DryingProcessForm> getSlotsByDevice(HttpServletRequest req,
			SqlSession conn) {
		DryingProcessMapper dpMapper = conn.getMapper(DryingProcessMapper.class);
		List<DryingProcessForm> result = new ArrayList<DryingProcessForm>();

		DryingProcessEntity condition = new DryingProcessEntity();
		condition.setDevice_manage_id(req.getParameter("device_manage_id"));
		String status = req.getParameter("status");
		if ("self".equals(status)) {
			condition.setStatus(GET_SELF);
		} else if ("likey".equals(status)) {
			condition.setStatus(GET_LIKEY);
		}

		List<DryingProcessEntity> dryingJobs = dpMapper.getDryingJobWithUsedSlots(condition);
		BeanUtil.copyToFormList(dryingJobs, result, CopyOptions.COPYOPTIONS_NOEMPTY, DryingProcessForm.class);

		return result;
	}

	/**
	 * 检查烘干作业数据
	 * @param req
	 * @param errors
	 */
	public void checkDryingProcess(HttpServletRequest req, List<MsgInfo> errors) {
		String drying_job_id = req.getParameter("drying_job_id");
		if (isEmpty(drying_job_id)) {
			MsgInfo e = new MsgInfo();
			e.setComponentid("drying_job_id");
			e.setErrcode("validator.required.singledetail");
			e.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.singledetail", "烘干作业"));
			errors.add(e);
		}
	}

	/**
	 * 建立烘干作业
	 * @param material_id
	 * @param req
	 * @param conn
	 * @throws Exception
	 */
	public void createDryingProcess(String material_id, HttpServletRequest req, SqlSessionManager conn) throws Exception {
		DryingProcessMapper dpMapper = conn.getMapper(DryingProcessMapper.class);
		DryingProcessEntity dryingProcess = new DryingProcessEntity();

		dryingProcess.setDrying_job_id(req.getParameter("drying_job_id"));
		dryingProcess.setDevice_manage_id(req.getParameter("device_manage_id"));
		String sSlot = req.getParameter("slot");
		if (sSlot != null) {
			Integer iSlot = Integer.parseInt(sSlot);
			dryingProcess.setSlot(iSlot);
		}
		dryingProcess.setMaterial_id(material_id);

		dpMapper.createProcess(dryingProcess);
	}

	/**
	 * 结束烘干作业
	 * @param material_id
	 * @param position_id
	 * @param conn
	 * @throws Exception
	 */
	public void finishDryingProcess(String material_id, String position_id, SqlSessionManager conn) throws Exception {
		DryingProcessMapper dpMapper = conn.getMapper(DryingProcessMapper.class);
		DryingProcessEntity dryingProcess = new DryingProcessEntity();

		dryingProcess.setMaterial_id(material_id);
		dryingProcess.setPosition_id(position_id);

		List<DryingProcessEntity> dryingProcesses = dpMapper.getToFinishProcess(dryingProcess);
		for(DryingProcessEntity toFinish : dryingProcesses) {
			dpMapper.finishProcess(toFinish);
		}
	}

	/**
	 * 取得维修对象在工位上的烘干作业信息
	 * @param material_id
	 * @param position_id
	 * @param conn
	 * @return
	 */
	public DryingProcessEntity getDryingProcessByMaterialInPositionEntity(String material_id, String position_id, SqlSession conn) {
		DryingProcessMapper dpMapper = conn.getMapper(DryingProcessMapper.class);

		DryingProcessEntity entity = dpMapper.getProcessByMaterialInPosition(material_id, position_id);
		return entity;
	}
	public DryingProcessForm getDryingProcessByMaterialInPosition(String material_id, String position_id, SqlSession conn) {
		DryingProcessMapper dpMapper = conn.getMapper(DryingProcessMapper.class);
		DryingProcessForm dryingProcess = new DryingProcessForm();

		DryingProcessEntity entity = dpMapper.getProcessByMaterialInPosition(material_id, position_id);

		if (entity == null) return null;

		BeanUtil.copyToForm(entity, dryingProcess, CopyOptions.COPYOPTIONS_NOEMPTY);
		dryingProcess.setHardening_condition(
				CodeListUtils.getValue("drying_hardening_condition", dryingProcess.getHardening_condition()));

		return dryingProcess;
	}
}

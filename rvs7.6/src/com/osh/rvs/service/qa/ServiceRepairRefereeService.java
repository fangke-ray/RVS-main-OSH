package com.osh.rvs.service.qa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.qa.ServiceRepairManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.qa.ServiceRepairManageForm;
import com.osh.rvs.mapper.inline.SoloProductionFeatureMapper;
import com.osh.rvs.mapper.qa.ServiceRepairManageMapper;
import com.osh.rvs.mapper.qa.ServiceRepairRefereeMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;


public class ServiceRepairRefereeService {
	private static final String JUDGE_POSITION = "00000000051";
	private static final String PROCESS_CODE = "601";
	private Logger _log=Logger.getLogger(getClass());

	public List<ServiceRepairManageForm> searchServiceRepair(SqlSession conn,String material_id){
		ServiceRepairRefereeMapper dao=conn.getMapper(ServiceRepairRefereeMapper.class);
		List<ServiceRepairManageForm> lResultForm=new ArrayList<ServiceRepairManageForm>();
		List<ServiceRepairManageEntity> lResultBean=dao.searchServiceRepair(material_id);
		if(lResultBean!=null){
			//复制数据对象到表单
			BeanUtil.copyToFormList(lResultBean, lResultForm, null,ServiceRepairManageForm.class);
			return lResultForm;
		}else{
			return null;
		}
	}
	
	//查询维修对象是否存在
	public ServiceRepairManageEntity checkServiceRepairManageExist(String material_id, SqlSessionManager conn, List<MsgInfo> errors){
		ServiceRepairRefereeMapper dao=conn.getMapper(ServiceRepairRefereeMapper.class);
		ServiceRepairManageEntity bean = dao.checkServiceRepairManageExist(material_id);
		if(bean==null){
			MsgInfo thisError = new MsgInfo();
			thisError.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.invalidCode", "待判维修品"));
			thisError.setComponentid("scanner_inputer");
			thisError.setErrcode("info.linework.invalidCode");
			errors.add(thisError);
			return null;
		}else{
			return bean;
		}
	}
	
	//更新受理时间
	public void updateQareceptionTime(SqlSessionManager conn,String material_id){
		ServiceRepairRefereeMapper dao=conn.getMapper(ServiceRepairRefereeMapper.class);
		dao.updateQareceptionTime(material_id);
	}
	
	/*//查询QIS请款信息
	public ServiceRepairManageForm searchQisPayout(ActionForm form,SqlSession conn){
		ServiceRepairRefereeMapper dao=conn.getMapper(ServiceRepairRefereeMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		//复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		//查询数据
		ServiceRepairManageEntity tempEntity=dao.searchQisPayout(entity);
		
		if(tempEntity!=null){
			ServiceRepairManageForm resultForm=new ServiceRepairManageForm();
			//复制数据到表单对象
			BeanUtil.copyToForm(tempEntity, resultForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			return resultForm;
		}else{
			return null;
		}
	}*/
	
	public void updateServiceRepair(ActionForm form,SqlSessionManager conn){
		ServiceRepairRefereeMapper dao=conn.getMapper(ServiceRepairRefereeMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		dao.updateServiceRepair(entity);
	}

	/**
	 * 待判定返品信息在暂停等待区
	 * @param conn
	 * @param material_id
	 * @return
	 */
	public List<ServiceRepairManageEntity> searchPausedServiceRepair(SqlSession conn, String material_id) {
		ServiceRepairRefereeMapper dao=conn.getMapper(ServiceRepairRefereeMapper.class);
		return dao.findPausing(material_id);
	}

	/**
	 * 取得全部修理中断/暂停的保内品
	 * @param conn
	 * @return
	 */
	public List<ServiceRepairManageForm> searchServiceRepairPaused(SqlSession conn) {
		ServiceRepairRefereeMapper dao=conn.getMapper(ServiceRepairRefereeMapper.class);
		List<ServiceRepairManageForm> lResultFormList=new ArrayList<ServiceRepairManageForm>();
		List<ServiceRepairManageEntity> lResultBeans=dao.findPausing(null);
		//复制数据对象到表单
		BeanUtil.copyToFormList(lResultBeans, lResultFormList, null,ServiceRepairManageForm.class);
		return lResultFormList;
	}
	
	
	/**删除QIS请款信息**/
	public void deleteQisPayout(ActionForm form,SqlSessionManager conn){
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		dao.deleteQisPayout(entity);
	}
	
	/**更新QIS请款信息**/
	public void updateQisPayout(ActionForm form,SqlSessionManager conn){
		ServiceRepairManageMapper dao=conn.getMapper(ServiceRepairManageMapper.class);
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 增加作为主键
		if (entity.getQuality_info_no() == null)
			entity.setQuality_info_no("");

		dao.updateQisPayout(entity);
	}

	public void createProductionFeature(SqlSessionManager conn, ServiceRepairManageEntity resultEntity, 
			String operator_id, String section_id, Integer operate_result) throws Exception {
		SoloProductionFeatureEntity productionFeatureEntity = new SoloProductionFeatureEntity();
		productionFeatureEntity.setSection_id(section_id);
		productionFeatureEntity.setPosition_id(JUDGE_POSITION);
		productionFeatureEntity.setModel_name(resultEntity.getModel_name());
		productionFeatureEntity.setJudge_date(resultEntity.getRc_mailsend_date());
		productionFeatureEntity.setSerial_no(resultEntity.getSerial_no());
		productionFeatureEntity.setOperate_result(operate_result);
		productionFeatureEntity.setOperator_id(operator_id);

		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);

		productionFeatureEntity.setPace(mapper.getMaxPace(productionFeatureEntity) + 1);
		// #{pace}, 
		
		mapper.insert(productionFeatureEntity);
	}

	public SoloProductionFeatureEntity checkWorkingPfServiceRepair(String operator_id, SqlSession conn, List<MsgInfo> msgInfos) {
		// 判断
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		
		SoloProductionFeatureEntity pfBean = new SoloProductionFeatureEntity();
		pfBean.setOperator_id(operator_id);
		pfBean.setAction_time_null(0);
		pfBean.setFinish_time_null(1);

		// 判断是否有在进行中的维修对象
		List<SoloProductionFeatureEntity> workingPfs = mapper.searchSoloProductionFeature(pfBean);
		if (workingPfs.size() > 0) {
			return workingPfs.get(0);
		}
		return null;
	}

	public ServiceRepairManageForm checkWorkingServiceRepair(String operator_id, SqlSession conn, List<MsgInfo> errors) {
		// 工作信息
		SoloProductionFeatureEntity retSolo = checkWorkingPfServiceRepair(operator_id, conn, errors);

		if (retSolo != null) {
			ServiceRepairManageForm ret = new ServiceRepairManageForm();

			// 判定信息
			ServiceRepairManageEntity conditon = new ServiceRepairManageEntity();
			conditon.setSerial_no(retSolo.getSerial_no());
			conditon.setModel_name(retSolo.getModel_name());
			conditon.setRc_mailsend_date(retSolo.getJudge_date());

			ServiceRepairManageMapper srmMapper = conn.getMapper(ServiceRepairManageMapper.class);
			List<ServiceRepairManageEntity> serviceRepair = srmMapper.searchServiceRepair(conditon);

			if (serviceRepair.size() > 0) {
				BeanUtil.copyToForm(serviceRepair.get(0), ret, CopyOptions.COPYOPTIONS_NOEMPTY);
				ret.setOperate_result(String.valueOf(retSolo.getOperate_result()));
				// 已处理步骤
				ret.setMention(getWorkedSteps(retSolo, conn));
				return ret;
			} else {
				return null;
			}
		}

		return null;
	}

	public String getWorkedSteps(ServiceRepairManageEntity condSrme, SqlSession conn) {
		SoloProductionFeatureEntity condSolo = new SoloProductionFeatureEntity();;
		condSolo.setSerial_no(condSrme.getSerial_no());
		condSolo.setModel_name(condSrme.getModel_name());
		condSolo.setJudge_date(condSrme.getRc_mailsend_date());

		return getWorkedSteps(condSolo, conn);
	}
	
	public String getWorkedSteps(SoloProductionFeatureEntity condSolo, SqlSession conn) {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		String sRet = "";

		SoloProductionFeatureEntity condition = new SoloProductionFeatureEntity();
		condition.setSerial_no(condSolo.getSerial_no());
		condition.setModel_name(condSolo.getModel_name());
		condition.setJudge_date(condSolo.getJudge_date());

		List<SoloProductionFeatureEntity> list = mapper.searchSoloProductionFeature(condition);

		Set<String> names = new TreeSet<String>();
		try {
		for (SoloProductionFeatureEntity entity : list) {
			String comment = entity.getPcs_comments();
			if (comment != null && comment.contains("GC6210100")) {
				@SuppressWarnings("unchecked")
				Map<String, String> jsonStep_inputs = JSON.decode(comment, Map.class);
				String steps = jsonStep_inputs.get("GC6210100");
				if (steps != null) {
					String[] stepArr = steps.split("\n");
					for (String step : stepArr) {
						names.add(step);
					}
				}
			}
		}
		} catch(Exception e) {
			_log.error(e.getMessage(),e);
		}

		sRet = CommonStringUtil.joinBy("<br>", names.toArray(new String[names.size()]));
		if (sRet.length() > 0) {
			sRet = "<b>已经处理过以下步骤：</b><br>" + sRet;
		}
		return sRet;
	}

	/**
	 * 工位结束
	 * @param tempForm
	 * @param operator_id
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void finishWorkingServiceRepair(ServiceRepairManageForm tempForm, String operator_id, String pcs_comments,
			SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 判断
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);

		SoloProductionFeatureEntity condition = new SoloProductionFeatureEntity();
		condition.setPcs_comments(pcs_comments);
		condition.setOperator_id(operator_id);
		if (tempForm.getQa_referee_time() == null) {
			condition.setOperate_result(RvsConsts.OPERATE_RESULT_BREAK);
		} else {
			condition.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
		}
		mapper.finishOnOperator(condition);
	}

	/**暂停
	 * @throws Exception */
	public void pauseToSelf(SoloProductionFeatureEntity productionFeatureEntity, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		productionFeatureEntity.setOperate_result(RvsConsts.OPERATE_RESULT_PAUSE);
		mapper.finishOnOperator(productionFeatureEntity);

		productionFeatureEntity.setPace(productionFeatureEntity.getPace() + 1);
		productionFeatureEntity.setAction_time(new Date());
		// #{pace}, 

		mapper.insert(productionFeatureEntity);
	}

	/**暂停再开
	 * @throws Exception */
	public void pauseToResume(SoloProductionFeatureEntity productionFeatureEntity, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);

		mapper.updateToResume(productionFeatureEntity);
	}

	public String getSteps() {
		String stepOptions = "";
		String steps = PathConsts.POSITION_SETTINGS.getProperty("steps."+PROCESS_CODE);
		if (steps != null) {
			String[] steparray = steps.split(",");
			for (String step : steparray) {
				step = step.trim();
				String stepname = PathConsts.POSITION_SETTINGS.getProperty("step." + PROCESS_CODE + "." + step);
				stepOptions += "<option value=\"" + step + "\">" + stepname + "</option>";
			}
		}
		return stepOptions;
	}

	public String getWorkedSteps(ServiceRepairManageForm resultForm) {
		// TODO Auto-generated method stub
		return null;
	}
}

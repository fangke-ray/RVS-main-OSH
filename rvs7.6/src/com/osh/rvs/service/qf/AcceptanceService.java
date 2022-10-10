package com.osh.rvs.service.qf;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.qf.FactReceptMaterialEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.qf.AcceptanceMapper;
import com.osh.rvs.mapper.qf.FactReceptMaterialMapper;
import com.osh.rvs.service.CustomerService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.ProductionFeatureService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

public class AcceptanceService {

	private ProductionFeatureService pfService = new ProductionFeatureService();
	
	/**
	 * 建立维修对象信息
	 * @param newId
	 * @param session
	 * @param needId 
	 * @param conn
	 * @param errors
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public String insert(ActionForm form, boolean needId, SqlSessionManager conn, List<MsgInfo> errors) throws NumberFormatException, Exception {

		MaterialEntity insertBean = new MaterialEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		if (!isEmpty(insertBean.getCustomer_name())) {
			CustomerService cservice = new CustomerService();
			insertBean.setCustomer_id(cservice.getCustomerStudiedId(insertBean.getCustomer_name(), insertBean.getOcm(), conn));
		}

		AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);
		dao.insertMaterial(insertBean);

		if (insertBean.getAnml_exp() != null || insertBean.getContract_related() != null) {
			needId = true;
		}

		if (needId) {
			CommonMapper cmapper = conn.getMapper(CommonMapper.class);
			insertBean.setMaterial_id(cmapper.getLastInsertID());
		}

		if (insertBean.getAnml_exp() != null) {
			MaterialTagService mtService = new MaterialTagService();
			mtService.updataTagByMaterialId(insertBean.getMaterial_id(), MaterialTagService.TAG_ANIMAL_EXPR, insertBean.getAnml_exp() == 1, conn);
		}

		if (insertBean.getContract_related() != null) {
			MaterialTagService mtService = new MaterialTagService();
			mtService.updataTagByMaterialId(insertBean.getMaterial_id(), MaterialTagService.TAG_CONTRACT_RELATED, insertBean.getContract_related() == 1, conn);
		}

		return insertBean.getMaterial_id();
	}

	/**
	 * 建立受理的作业记录
	 * @param newId
	 * @param session
	 * @param triggerList 
	 * @param conn
	 * @throws Exception
	 */
	public void accept(String newId, HttpSession session, List<String> triggerList, SqlSessionManager conn) throws Exception {
		ProductionFeatureEntity entity = new ProductionFeatureEntity();
		entity.setMaterial_id(newId);
		entity.setPosition_id("00000000009");
		entity.setPace(0);
		LoginData loginData = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		entity.setOperator_id(loginData.getOperator_id());
		entity.setSection_id(loginData.getSection_id());
		entity.setOperate_result(2);
		entity.setRework(0);

		// 标准作业时间
		Integer use_seconds = Integer.valueOf(RvsUtils.getZeroOverLine("_default", null, loginData, "111")) * 60;
		entity.setUse_seconds(use_seconds);
		pfService.insertAcceptance(entity, conn);

		// 获取通箱位置
		MaterialService mService = new MaterialService();
		MaterialEntity mEntity = mService.loadSimpleMaterialDetailEntity(conn, newId);

		// 放通箱
		String kind = mEntity.getKind();
		// UDI
		if ("06".equals(kind) && RvsConsts.CATEGORY_UDI.equals(mEntity.getCategory_id())) {
			kind = "UDI";
		}
		if (mEntity.getFix_type() != null && mEntity.getFix_type() != 3) { // 备品通箱不入库也不消毒通箱(备品必须单都包含通箱信息)
			triggerList.add("http://localhost:8080/rvspush/trigger/assign_tc_space/" + newId
					+ "/" + kind + "/" + mEntity.getFix_type() + "/" + mEntity.getUnrepair_flg()); // with_case as unrepair_flg 
		}

		FactReceptMaterialService frmService = new FactReceptMaterialService();
		FactReceptMaterialMapper frmMapper = conn.getMapper(FactReceptMaterialMapper.class);
		FactReceptMaterialEntity frmEntity = new FactReceptMaterialEntity();
		frmEntity.setSerial_no(mEntity.getSerial_no());
		frmEntity.setModel_id(mEntity.getModel_id());
		List<FactReceptMaterialEntity> list = frmMapper.searchTemp(frmEntity);
		if (list.size() > 0) {
			frmService.tempMatch(newId, list.get(0), conn);
		}
	}

	/**
	 * 批量插入数据-从Excel
	 * @param parameterMap
	 * @param session
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void batchinsert(Map<String, String[]> parameterMap, HttpSession httpSession,
			SqlSessionManager conn, List<MsgInfo> errors) throws Exception {

		List<MaterialForm> materialForms = new AutofillArrayList<MaterialForm>(MaterialForm.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		
		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("materials".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					// TODO 全
					if ("sorc_no".equals(column)) {
						materialForms.get(icounts).setSorc_no(value[0]);
					} else if ("esas_no".equals(column)) {
						materialForms.get(icounts).setEsas_no(value[0]);
					} else if ("model_id".equals(column)) {
						materialForms.get(icounts).setModel_id(value[0]);
					} else if ("model_name".equals(column)) {
						materialForms.get(icounts).setModel_name(value[0]);
					} else if ("serial_no".equals(column)) {
						materialForms.get(icounts).setSerial_no(value[0]);
					} else if ("ocm".equals(column)) {
						materialForms.get(icounts).setOcm(value[0]);
					} else if ("ocm_rank".equals(column)) {
						materialForms.get(icounts).setOcm_rank(value[0]);
					} else if ("customer_name".equals(column)) {
						materialForms.get(icounts).setCustomer_name(value[0]);
					} else if ("ocm_deliver_date".equals(column)) {
						materialForms.get(icounts).setOcm_deliver_date(value[0]);
					} else if ("level".equals(column)) {
						materialForms.get(icounts).setLevel(value[0]);
					} else if ("agreed_date".equals(column)) {
						materialForms.get(icounts).setAgreed_date(value[0]);
					} else if ("package_no".equals(column)) {
						materialForms.get(icounts).setPackage_no(value[0]);
					} else if ("storager".equals(column)) {
						materialForms.get(icounts).setStorager(value[0]);
					} else if ("direct_flg".equals(column)) {
						materialForms.get(icounts).setDirect_flg(value[0]);
					} else if ("service_repair_flg".equals(column)) {
						materialForms.get(icounts).setService_repair_flg(value[0]);
					} else if ("fix_type".equals(column)) {
						materialForms.get(icounts).setFix_type(value[0]);
					} else if ("selectable".equals(column)) {
						materialForms.get(icounts).setSelectable(value[0]);
					}
				}
			}
		}

		MaterialService mservice = new MaterialService();

		boolean isAidRc = false;
		boolean isSpare = false;

		// 检查每个Form
		for (MaterialForm materialForm : materialForms) {
			isAidRc = "4".equals(materialForm.getFix_type());
			isSpare = "3".equals(materialForm.getFix_type());

			String cdsTypeName = materialForm.getOcm_rank(); 
			if (isAidRc || isSpare) {
				materialForm.setOcm_rank(null);
				materialForm.setProcessing_position(cdsTypeName);
			}

			Validators v = BeanUtil.createBeanValidators(materialForm, BeanUtil.CHECK_TYPE_ALL);
			v.delete("level", "ocm");
			v.add("ocm", v.integerType());
			v.add("level", v.integerType());
			List<MsgInfo> thisErrors = v.validate();
			if (thisErrors.size() > 0) {
				for (MsgInfo thisError : thisErrors) {
					thisError.setErrmsg("型号" + materialForm.getModel_name() + "机身号" + materialForm.getSerial_no() + "的维修对象：" + thisError.getErrmsg());
					errors.add(thisError);
				}
			}

			// 对比客户ID
//			String ocm = materialForm.getOcm();
//			String value = CodeListUtils.getValue("material_ocm", ocm);

			mservice.checkRepeatNo("", materialForm, conn, errors);

//			if ("OCM-SHRC".equals(value)) {
//				materialForm.setAm_pm("1");
//			} else {
				materialForm.setAm_pm("2");
//			}
		}

		Date aidRcAcceptStartAt = null;
		LoginData user = null;
		if (isAidRc || isSpare) {
			aidRcAcceptStartAt = (Date) httpSession.getAttribute("AidRcAcceptStartAt");
			user = (LoginData) httpSession.getAttribute(RvsConsts.SESSION_USER);
		}

		// 放入数据库
		if (errors.size() == 0) {
			for (MaterialForm materialForm : materialForms) {
				String material_id = insert(materialForm, (isAidRc || isSpare), conn, errors);
				if ((isAidRc || isSpare) && material_id != null) {
					// 自动受理完成
					acceptComplete(material_id, aidRcAcceptStartAt, user, materialForm.getProcessing_position(), conn);
				}
			}
		}
	}

	/**
	 * 批量受理数据
	 * @param parameterMap
	 * @param session
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void accept(Map<String, String[]> parameterMap,HttpSession session,
			SqlSessionManager conn, List<MsgInfo> errors) throws Exception {

		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		List<String> triggerList = new ArrayList<String>();

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("materials".equals(entity)) {
					String column = m.group(2);

					String[] value = parameterMap.get(parameterKey);

					if ("material_id".equals(column)) {
						accept(value[0], session, triggerList, conn);
					}
				}
			}
		}

		if (errors.size() == 0 && triggerList.size() > 0) {
			RvsUtils.sendTrigger(triggerList);
		}
	}

	/**
	 * 更新维修对象数据（受理完成前）
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 * @throws Exception 
	 */
	public void update(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		CustomerService cservice = new CustomerService();

		MaterialEntity insertBean = new MaterialEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);
	
		if (!isEmpty(insertBean.getCustomer_name())) {
			insertBean.setCustomer_id(cservice.getCustomerStudiedId(insertBean.getCustomer_name(), insertBean.getOcm(), conn));
		}

		AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);
		dao.updateMaterial(insertBean);

		// 标签更新
		if (insertBean.getAnml_exp() != null) {
			MaterialTagService mtService = new MaterialTagService();
			mtService.updataTagByMaterialId(insertBean.getMaterial_id(), MaterialTagService.TAG_ANIMAL_EXPR, insertBean.getAnml_exp() == 1, conn);
		}

		// 标签更新
		if (insertBean.getContract_related() != null) {
			MaterialTagService mtService = new MaterialTagService();
			mtService.updataTagByMaterialId(insertBean.getMaterial_id(), MaterialTagService.TAG_CONTRACT_RELATED, insertBean.getContract_related() == 1, conn);
		}
	}

	/**
	 * 取得维修对象详细信息
	 * @param conn
	 * @return
	 */
	public List<MaterialForm> getMaterialDetail(SqlSession conn) {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

		List<MaterialEntity> lResultBean = dao.getMaterialDetailForRecept(null);

		List<MaterialForm> lResultForm = new ArrayList<MaterialForm>();

		CopyOptions cos = new CopyOptions();
		cos.dateConverter(DateUtil.DATE_TIME_PATTERN, "reception_time", "finish_time");
		cos.fieldRename("finish_time", "doreception_time");
		BeanUtil.copyToFormList(lResultBean, lResultForm, cos, MaterialForm.class);

		return lResultForm;
	}

	/**
	 * 未修理返还（受理完成前）
	 * @param ids
	 * @param conn
	 * @throws Exception
	 */
	public void acceptReturn(List<String> ids, SqlSessionManager conn) throws Exception{
		MaterialMapper mDao = conn.getMapper(MaterialMapper.class);
		mDao.updateMaterialReturn(ids);

		TurnoverCaseService tcService = new TurnoverCaseService();

		// FSE 数据同步
		try{
			for (String id: ids) {
				FseBridgeUtil.toUpdateMaterial(id, "br111");

				tcService.triggerUndoStorage(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新受理时间（数据获取时间->正式受理完成时间）
	 * @param ids
	 * @param conn
	 * @throws Exception
	 */
	public void updateFormalReception(String material_id, Date fact_finish_time, SqlSessionManager conn) throws Exception {
		AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);
		MaterialEntity material = new MaterialEntity();
		material.setMaterial_id(material_id);
		material.setReception_time(fact_finish_time);
		dao.updateFormalReception(material);

		ProductionFeatureEntity pfEntity = new ProductionFeatureEntity();
		pfEntity.setMaterial_id(material_id);
		pfEntity.setPosition_id("00000000009");
		pfEntity.setFinish_time(fact_finish_time);
		Integer use_seconds = Integer.valueOf(RvsUtils.getZeroOverLine("_default", null, null, "111")) * 60;
		pfEntity.setUse_seconds(use_seconds);
		dao.updateReceptionTime(pfEntity);

		try {
			FseBridgeUtil.toUpdateMaterial(material_id, "111");
			FseBridgeUtil.toUpdateMaterialProcess(material_id, "111");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void importOgz(HttpServletRequest req, SqlSessionManager conn, List<MsgInfo> errors) {
		Map<String, String[]> parameterMap = req.getParameterMap();
		Map<String, String> condition = new HashMap<String, String>();

		for (String key : parameterMap.keySet()) {
			condition.put(key, parameterMap.get(key)[0]);
		}

		AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);
		dao.importOgz(condition);
	}

	public Map<String, String> getOgz(HttpServletRequest req, SqlSession conn,
			List<MsgInfo> errors) {
		AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);
		return dao.loadOgz();
	}

	/**
	 * 自动受理完成
	 * 
	 * @param material_id
	 * @param httpSession
	 * @param processing_position
	 * @param conn
	 * @throws Exception 
	 */
	private void acceptComplete(String material_id, Date aidRcAcceptStartAt ,LoginData user,
			String processing_position, SqlSessionManager conn) throws Exception {
		String nextPosition = null;
		if ("灭菌".equals(processing_position)) {
			nextPosition = "00000000011";
		} else if ("消毒".equals(processing_position)) {
			nextPosition = "00000000010";
		}

		if (aidRcAcceptStartAt == null) {
			aidRcAcceptStartAt = new Date(new Date().getTime() - 300000l);
		}

		// 开始受理
		ProductionFeatureMapper mapper = conn.getMapper(ProductionFeatureMapper.class);
		ProductionFeatureEntity entity = new ProductionFeatureEntity();
		entity.setMaterial_id(material_id);
		entity.setPosition_id(RvsConsts.POSITION_ACCEPTANCE);
		entity.setPace(0);
		entity.setSection_id("00000000009");
		entity.setAction_time(aidRcAcceptStartAt);
		if (user != null) entity.setOperator_id(user.getOperator_id());
		entity.setOperate_result(RvsConsts.OPERATE_RESULT_WORKING);
		entity.setRework(0);
		mapper.insertProductionFeature(entity);

		Date aidRcAcceptEndAt = new Date();

		// 完成受理
		entity.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
		entity.setUse_seconds(new Long(
				(aidRcAcceptEndAt.getTime() - aidRcAcceptStartAt.getTime()) / 1000).intValue());
		mapper.finishProductionFeature(entity);

		// 开始消毒/灭菌等待
		if (nextPosition != null) {
			entity.setPosition_id(nextPosition);
			entity.setAction_time(null);
			entity.setOperator_id(null);
			entity.setOperate_result(RvsConsts.OPERATE_RESULT_NOWORK_WAITING);
			entity.setRework(0);
			mapper.insertProductionFeature(entity);
		}
	}

	/**
	 * 取得没有实物受理记录的维修品
	 * 
	 * @param conn
	 * @return
	 */
	public List<MaterialForm> getReceptionsWithoutFact(SqlSession conn) {
		AcceptanceMapper mapper = conn.getMapper(AcceptanceMapper.class);
		List<MaterialEntity> lEntities = mapper.searchReceptionsWithoutFact(); 

		List<MaterialForm> lRet = new ArrayList<MaterialForm>();

		BeanUtil.copyToFormList(lEntities, lRet, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);

		return lRet;
	}

	public Integer getPlanTarget(SqlSession conn) {
		AcceptanceMapper mapper = conn.getMapper(AcceptanceMapper.class);
		return mapper.getSparePlan(8);
	}

	public void updateSparePlan(Integer iPlanTarget, SqlSessionManager conn) {
		AcceptanceMapper mapper = conn.getMapper(AcceptanceMapper.class);
		mapper.updateSparePlan(8, iPlanTarget);
	}
}

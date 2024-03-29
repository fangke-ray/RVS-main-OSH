package com.osh.rvs.service.qf;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.master.ProcessAssignEntity;
import com.osh.rvs.bean.master.ProcessAssignTemplateEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.bean.qf.WipStorageEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.pda.PdaMaterialForm;
import com.osh.rvs.form.qf.WipStorageForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.inline.MaterialProcessAssignMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.master.ModelMapper;
import com.osh.rvs.mapper.master.ProcessAssignMapper;
import com.osh.rvs.mapper.master.SectionMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.mapper.qa.QualityAssuranceMapper;
import com.osh.rvs.mapper.qf.WipMapper;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.ProcessAssignService;
import com.osh.rvs.service.UserDefineCodesService;
import com.osh.rvs.service.partial.PartialOrderManageService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.IntegerConverter;
import framework.huiqing.common.util.message.ApplicationMessage;

public class WipService {

	Logger _logger = Logger.getLogger(WipService.class);

	/**
	 * 检索WIP中的全部维修对象
	 * @param form 提交表单
	 * @param manager 管理库位权限
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return List<ModelForm> 查询结果表单
	 */
	public List<MaterialForm> searchMaterial(ActionForm form, boolean manager, SqlSession conn, List<MsgInfo> errors) {
		MaterialEntity conditionBean = new MaterialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		List<MaterialEntity> lResultBean = new ArrayList<MaterialEntity>();
		WipMapper dao = conn.getMapper(WipMapper.class);
		lResultBean = dao.searchMaterial(conditionBean);
		
		List<MaterialForm> lResultForm = new ArrayList<MaterialForm>();
		
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, MaterialForm.class);

		if (manager) {
			WipStorageEntity emptyCond = new WipStorageEntity();
			emptyCond.setWip_storage_code(conditionBean.getWip_location());
			emptyCond.setOccupied(-1);
			List<WipStorageEntity> emptyStorages = dao.searchWipStorage(emptyCond);
			for (WipStorageEntity emptyStorage : emptyStorages) {
				MaterialForm empty = new MaterialForm();
				empty.setWip_location(emptyStorage.getWip_storage_code());
				lResultForm.add(empty);
			}
		}

		return lResultForm;
	}

	public List<String> getWipHeaped(ActionForm form, SqlSession conn, ActionErrors errors) {
		WipMapper dao = conn.getMapper(WipMapper.class);
		return dao.getWipHeaped();
	}

	public void checkRepeatNo(String id, ActionForm form, SqlSessionManager conn, List<MsgInfo> errors) {
		MaterialService service = new MaterialService();
		String existId1 = service.checkSorcNo(form, conn);
//		String existId2 = service.checkEsasNo(form, conn);
		String existId3 = service.checkModelSerialNo(form, conn);
		
		if (existId1 != null) {
			MsgInfo info = new MsgInfo();
			info.setErrcode("dbaccess.columnNotUnique");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "修理单号", ((MaterialForm)form).getSorc_no(), "维修对象"));
			errors.add(info);
//		} else if (existId2 != null) {
//			MsgInfo info = new MsgInfo();
//			info.setErrcode("dbaccess.columnNotUnique");
//			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "ESAS No.", ((MaterialForm)form).getEsas_no(), "维修对象"));
//			errors.add(info);
		}  else if (existId3 != null) {
			MsgInfo info = new MsgInfo();
			info.setErrcode("dbaccess.columnNotUnique");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号 + 机身号", ((MaterialForm)form).getModel_id() +","+ ((MaterialForm)form).getSerial_no(), "维修对象"));
			errors.add(info);
		}
	}

	/**
	 * 手动入库
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 * @throws Exception 
	 */
	public void insert(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		MaterialEntity insertBean = new MaterialEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		WipMapper dao = conn.getMapper(WipMapper.class);
		dao.insert(insertBean);
	}

	/**
	 * 手动入库
	 * @param form
	 * @param session
	 * @param conn
	 * @param errors
	 * @throws Exception 
	 */
	public void putin(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		MaterialEntity insertBean = new MaterialEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		WipMapper dao = conn.getMapper(WipMapper.class);
		dao.warehousing(insertBean);
	}

	/**
	 * 删除手动入库品
	 */
	public void remove(SqlSessionManager conn, String material_id) throws Exception {
		WipMapper dao = conn.getMapper(WipMapper.class);
		dao.remove(material_id);
	}

	/**
	 * 流水线入出库
	 * @param wip_location 
	 */
	public void warehousing(SqlSessionManager conn, String material_id, String wip_location) throws Exception {
		WipMapper dao = conn.getMapper(WipMapper.class);
		MaterialEntity mBean = new MaterialEntity();
		mBean.setMaterial_id(material_id);
		mBean.setWip_location(wip_location);
		dao.warehousing(mBean);
	}

	/**
	 * 流水线出库
	 */
	public void warehousing(Map<String, String[]> parameterMap, SqlSessionManager conn) throws Exception {
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		List<MaterialForm> materials = new AutofillArrayList<MaterialForm>(MaterialForm.class);
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("material".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);
	
					// TODO 全
					if ("material_id".equals(column)) {
						materials.get(icounts).setMaterial_id(value[0]);
					} else if ("break_back_flg".equals(column)) {
						materials.get(icounts).setBreak_back_flg(value[0]);
					} else if ("fix_type".equals(column)) {
						materials.get(icounts).setFix_type(value[0]);
					}
				}
			}
		}

		for(MaterialForm material : materials) {
			if ("1".equals(material.getBreak_back_flg())) { // 备品
				remove(conn, material.getMaterial_id());
			} else if ("0".equals(material.getBreak_back_flg())) {
				warehousing(conn, material.getMaterial_id(), null);
				// 单元出库直达711工位 // 三期对应单元投线
//				if ("2".equals(material.getFix_type())) {
//					ModelService mdlService = new ModelService();
//					MaterialService mservice = new MaterialService();
//					String model_id = mservice.loadMaterialDetailBean(conn, material.getMaterial_id()).getModel_id();
//					ModelForm modelForm = mdlService.getDetail(model_id, conn);
//					// EndoEye除外
//					if (modelForm != null && !"06".equals(modelForm.getKind())) {
//						ProductionFeatureService pfService = new ProductionFeatureService();
//						ProductionFeatureEntity nextWorkingPf = new ProductionFeatureEntity();
//						nextWorkingPf.setPosition_id(RvsConst.POSITION_SHIPPING);
//						nextWorkingPf.setSection_id("00000000000");
//						nextWorkingPf.setRework(0);
//						pfService.fingerSpecifyPosition(material.getMaterial_id(), true, nextWorkingPf, conn);
//					}
//				}
			}
		}
	}

	/**
	 * 系统返还
	 */
	public String resystem(SqlSessionManager conn, String material_id, MaterialForm mform) throws Exception {
		WipMapper dao = conn.getMapper(WipMapper.class);

		// 取得当前维修对象实体
		MaterialMapper mdao = conn.getMapper(MaterialMapper.class);
		MaterialEntity mbean = mdao.getMaterialEntityByKey(material_id);

		// 当前维修对象系统返还化
		mbean.setBreak_back_flg(dao.getresystemcount(mbean));
		dao.resystem(mbean);

		// FSE 数据同步
		try{
			FseBridgeUtil.toUpdateMaterial(material_id, "resystem");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 新建返还后维修对象
		CopyOptions cos = new CopyOptions();
		cos.include("sorc_no", "esas_no", "model_id", "serial_no", "direct_flg", "service_repair_flg");
		cos.converter(IntegerConverter.getInstance(), "direct_flg", "service_repair_flg");
		BeanUtil.copyToBean(mform, mbean, cos);

		mbean.setWip_location("");
		mdao.insertMaterial(mbean);

		// 得到刚才插入的新纪录ID
		CommonMapper cdao = conn.getMapper(CommonMapper.class);
		String new_material_id = cdao.getLastInsertID();

		// 报价之前的工位作业报告复制
		dao.copyProductionFeature(material_id, new_material_id);

		// 报价工位产生等待作业
		String position_id = "";
		Integer direct_flg = mbean.getDirect_flg();
		Integer service_repair_flg = mbean.getService_repair_flg();
		if ((direct_flg != null && direct_flg == 1)
				|| (service_repair_flg != null && (service_repair_flg == 1 || service_repair_flg == 2))) {
			position_id = "00000000014";
		} else {
			position_id = "00000000013";
		}

		// FSE 数据同步
		try{
			FseBridgeUtil.toUpdateMaterial(new_material_id, "TODO");
			// TODO Time
		} catch (Exception e) {
			e.printStackTrace();
		}

		ProductionFeatureMapper pfDao = conn.getMapper(ProductionFeatureMapper.class);
		ProductionFeatureEntity entity = new ProductionFeatureEntity();
		entity.setMaterial_id(new_material_id);
		entity.setPosition_id(position_id);
		entity.setPace(0);
		entity.setSection_id("00000000009"); // TODO
		entity.setOperate_result(0);
		entity.setRework(0);
		pfDao.insertProductionFeature(entity);

		return new_material_id;
	}
	
	/**
	 * 未修理返还
	 */
	public void stop(SqlSessionManager conn, String material_id) throws Exception {
		// 取得当前维修对象实体

		MaterialMapper mMapper = conn.getMapper(MaterialMapper.class);
		MaterialEntity mBean = mMapper.getMaterialNamedEntityByKey(material_id);

		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);

		WipMapper dao = conn.getMapper(WipMapper.class);
		dao.stop(material_id);

		ProductionFeatureMapper pfDao = conn.getMapper(ProductionFeatureMapper.class);
		ProductionFeatureEntity entity = new ProductionFeatureEntity();
		entity.setMaterial_id(material_id);
		entity.setPace(0);
		entity.setOperate_result(0);

		boolean check = pfMapper.checkPositionDid(material_id, "00000000012", ""+RvsConsts.OPERATE_RESULT_FINISH, null);
		if (!check) { // RVS3 如果没完成测漏

			// 如果是周边设备则归档 V6
			if ("07".equals(mBean.getKind()) && mBean.getFix_type() == 1) {

				QualityAssuranceMapper qaMapper = conn.getMapper(QualityAssuranceMapper.class);
				mBean.setQa_check_time(new Date());
				mBean.setOutline_time(null);
				qaMapper.updateMaterial(mBean);

				if (!(mBean.getLevel() == null || mBean.getLevel() == 57)) {
					// 182 不修理返还
					entity.setPosition_id(RvsConsts.POSITION_PERP_UNREPIAR);
					entity.setSection_id("00000000009");
					entity.setRework(0);
				} else {
					// 出货
					entity.setMaterial_id(material_id);
					entity.setPosition_id(RvsConsts.POSITION_SHIPPING);
					entity.setSection_id("00000000000");
					entity.setRework(0);
				}

				String sorcNo = mBean.getSorc_no();
				String path = PathConsts.BASE_PATH + PathConsts.PCS + "\\" + 
					("OMRN-" + sorcNo + "________").substring(0, 8) + "\\" + sorcNo;
				if (!new File(path).exists()) {
					// 如果进行过181则认定为归档 TODO
					// 推送归档
					try {
						URL url = new URL("http://localhost:8080/rvs/download.do?method=file&material_id=" + material_id);
						url.getQuery();
						URLConnection urlconn = url.openConnection();
						urlconn.setReadTimeout(1); // 不等返回
						urlconn.connect();
						urlconn.getContentType(); // 这个就能触发
					} catch (Exception e) {
						_logger.error("Failed", e);
					}
				}
			} else if (mBean.getCategory_id().equals(RvsConsts.CATEGORY_UDI)) {
				entity.setPosition_id(RvsConsts.POSITION_SHIPPING);
				entity.setSection_id("00000000000");
				entity.setRework(0);
			} else {
				entity.setPosition_id(RvsConsts.POSITION_SHIPPING);
				entity.setSection_id("00000000000");
				entity.setRework(0);
			}
		} else { // 如果完成测漏
			if (mBean.getInline_time() == null) {
				// 图象检查
				Map<String, Object> params = new HashMap<String, Object>();
				List<String> positions = new ArrayList<String>();
				positions.add("00000000015");
				params.put("material_id", material_id);
				params.put("position_ids", positions);

				int rework = pfDao.getReworkCountWithPositions(params);
				entity.setPosition_id("00000000015");
				entity.setSection_id("00000000009"); // TODO
				entity.setRework(rework + 1);
			} else { // 已投线
				// 出货
				entity.setPosition_id(RvsConsts.POSITION_SHIPPING);
				entity.setSection_id("00000000000");
				entity.setRework(0);
			}
		}

		// 下一工位
		pfDao.insertProductionFeature(entity);		

		// 如果是周边或小修理/光学视管，则取消零件订购。
		if ("07".equals(mBean.getKind()) || RvsConsts.CATEGORY_UDI.equals(mBean.getCategory_id())
			|| RvsUtils.isLightFix(mBean.getLevel())) {
			PartialOrderManageService pomService = new PartialOrderManageService();
			pomService.deleteMaterialPartial(material_id, conn);
		}

		// FSE 数据同步
		try{
			conn.commit();
			FseBridgeUtil.toUpdateMaterial(material_id, "stop");
			FseBridgeUtil.toUpdateMaterialProcess(material_id, "stop");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void changelocation(SqlSessionManager conn, String material_id, String wip_location) {
		WipMapper dao = conn.getMapper(WipMapper.class);
		dao.changeLocation(material_id, wip_location);
	}

	public int getImgCheckMaxRework(String material_id, SqlSession conn) {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

		List<String> positionImgCheck = new ArrayList<String>();
		positionImgCheck.add("00000000015");

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("material_id", material_id);
		param.put("position_ids", positionImgCheck);

		return dao.getReworkCountWithPositions(param);
	}

	public boolean checkImgCheckMaxReworking(String material_id, SqlSessionManager conn) {
		WipMapper dao = conn.getMapper(WipMapper.class);
		return dao.checkImgCheckReworking(material_id, "00000000015");
	}

	public boolean checkLocateInuse(MaterialForm materialForm,
			SqlSession conn, List<MsgInfo> errors) {
		List<MaterialEntity> lHit = new ArrayList<MaterialEntity>();
		WipMapper dao = conn.getMapper(WipMapper.class);

		MaterialEntity conditionBean = new MaterialEntity();
		conditionBean.setWip_location(materialForm.getWip_location());
		lHit = dao.searchMaterial(conditionBean);

		if (lHit.size() == 0) {
			return false;
		} else if (lHit.size() > 1) {
			return true;
		} else {
			String hitMaterialId = lHit.get(0).getMaterial_id();
			if (hitMaterialId.equals(materialForm.getMaterial_id())) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * 取得BO库位表格
	 * 
	 * @param material_id
	 * @param position_id
	 * @param recomment
	 * @param lResponseResult
	 * @param conn
	 * @param msgInfos
	 */
	public void getBoLocationMap(Map<String, Object> lResponseResult, SqlSession conn,
			List<MsgInfo> msgInfos) {
		UserDefineCodesService uService = new UserDefineCodesService();
		// BO库位表格
		int iBoWipShelfCount = 10;
		String sBoWipShelfCount = uService.searchUserDefineCodesValueByCode("BO_WIP_SHELF_COUNT", conn);
		try {
			iBoWipShelfCount = Integer.parseInt(sBoWipShelfCount);
		} catch (NumberFormatException e) {
		}

		StringBuffer retSb = new StringBuffer("");
		char shelfName = 'A';

		WipMapper mapper = conn.getMapper(WipMapper.class);
		List<String> wipBoHeaped = mapper.getWipBoHeaped();
		Set<String> wipBoHeapedSet = new HashSet<String>();
		wipBoHeapedSet.addAll(wipBoHeaped);

		retSb.append("<div style=\"margin: 15px; float: left;\"><div class=\"ui-widget-header\" style=\"width: " + (10 * 64) + "px; text-align: center;\">货架 ");
		retSb.append("</div><table class=\"condform storage-table\" style=\"width: " + (10 * 64) + "px;\">");

		for (int i = 0; i < iBoWipShelfCount; i++) {
			String shelf = new String(new char[]{shelfName});
			StringBuffer shelfSb = new StringBuffer("<tr>");

			for (int j = 0; j < 10; j++) {
				String sCaseCode = "BO-" + shelf + j;
				shelfSb.append("<td class=\"");

				if (wipBoHeapedSet.contains(sCaseCode)) {
					shelfSb.append("ui-storage-highlight");
				} else {
					shelfSb.append("wip-empty");
				}
				shelfSb.append("\">");
				shelfSb.append(sCaseCode);
				shelfSb.append("</td>");
			}
			shelfSb.append("</tr>");

			retSb.append(shelfSb);
			shelfName++;
		}
		retSb.append("</table></div>");
		retSb.append("<div class=\"clear\"></div>");

		lResponseResult.put("storageHtml", retSb.toString());
	}

	/**
	 * 建立库位
	 * 
	 * @param form
	 * @param msgInfos
	 * @param conn
	 */
	public void create(ActionForm form, List<MsgInfo> msgInfos,
			SqlSessionManager conn) {
		WipMapper mapper = conn.getMapper(WipMapper.class);

		WipStorageEntity entity = new WipStorageEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		WipStorageEntity keyEntity = new WipStorageEntity();
		keyEntity.setWip_storage_code(entity.getWip_storage_code());
		
		List<WipStorageEntity> target = mapper.searchWipStorage(keyEntity);
		if (target != null && target.size() > 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("wip_storage_code");
			error.setErrcode("dbaccess.recordDuplicated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "WIP 库位"));
			msgInfos.add(error);
			return;
		}

		mapper.createStorage(entity);
	}

	/**
	 * x修改库位设置
	 * 
	 * @param form
	 * @param msgInfos
	 * @param conn
	 */
	public void changeSetting(ActionForm form, List<MsgInfo> msgInfos, SqlSessionManager conn) {
		WipMapper mapper = conn.getMapper(WipMapper.class);

		WipStorageEntity entity = new WipStorageEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		WipStorageEntity keyEntity = new WipStorageEntity();
		keyEntity.setWip_storage_code(entity.getOrigin_wip_storage_code());

		List<WipStorageEntity> target = mapper.searchWipStorage(keyEntity);

		if (target == null ||  target.size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("wip_storage_code");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "WIP 库位"));
			msgInfos.add(error);
			return;
		}

		if (!entity.getWip_storage_code().equals(entity.getOrigin_wip_storage_code())) {
			keyEntity = new WipStorageEntity();
			keyEntity.setWip_storage_code(entity.getWip_storage_code());
			
			target = mapper.searchWipStorage(keyEntity);
			if (target != null && target.size() > 0) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("wip_storage_code");
				error.setErrcode("dbaccess.recordDuplicated");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "WIP 库位"));
				msgInfos.add(error);
				return;
			}
		}
		mapper.updateStorage(entity);
	}

	/**
	 * 删除库位
	 * 
	 * @param form
	 * @param msgInfos
	 * @param conn
	 */
	public void remove(ActionForm form, List<MsgInfo> msgInfos, SqlSessionManager conn) {
		WipMapper mapper = conn.getMapper(WipMapper.class);

		WipStorageEntity entity = new WipStorageEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<WipStorageEntity> target = mapper.searchWipStorage(entity);
		if (target == null ||  target.size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("case_code");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "WIP 库位"));
			msgInfos.add(error);
			return;
		}

		mapper.removeStorage(entity.getWip_storage_code());
	}


	public void getLocationMap(ActionForm form, Map<String, Object> callbackResponse,
			SqlSession conn, List<MsgInfo> errors) {
		WipStorageForm storageForm =  (WipStorageForm) form;

		WipStorageEntity entity = new WipStorageEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		if (storageForm.getMaterial_id() != null) {
			if (MaterialTagService.getAnmlMaterials(conn).contains(storageForm.getMaterial_id())) {
				entity.setAnml_exp(1);
				entity.setFor_agreed(null);
				entity.setKind(null);
			} else {
				entity.setAnml_exp(0);
				MaterialService ms = new MaterialService();
				MaterialEntity material = ms.loadSimpleMaterialDetailEntity(conn, storageForm.getMaterial_id());
				if (entity.getFor_agreed() == null) {
					entity.setFor_agreed(material.getAgreed_date() == null ? -1 : 1);
				}
				if (material.getKind().equals("7") || material.getKind().equals("07")) {
					entity.setKind(7);
				} else if (material.getKind().equals("6") || material.getKind().equals("06")) {
					if (material.getCategory_name().toUpperCase().indexOf("ENDOEYE") >= 0) {
						entity.setKind(6);
					} else { // UDI
						entity.setKind(9);
					}
				} else {
					entity.setKind(1);
				}
			}
		}

		StringBuffer retSb = new StringBuffer("<style>.wip-table td[anml_exp] {border-color : #AA7700;border-radius : 20%;}</style><div class=\"ui-widget-header ui-corner-top ui-helper-clearfix areaencloser\"><span class=\"areatitle\">WIP区域一览</span></div>");
		StringBuffer retShelfHead = null;
		StringBuffer retShelf = null;
		List<String> headChars = new ArrayList<String>();
		int cols = 120;
		int rowCols = 0;

		WipMapper mapper = conn.getMapper(WipMapper.class);
		List<WipStorageEntity> list = mapper.searchWipStorage(entity);

		String shelf = "-";
		String layer = "-";

		WipStorageEntity storageS = null;
		for (WipStorageEntity storage : list) {
			storageS = storage;
			if (!shelf.equals(storage.getShelf())) {
				if (retShelf != null) {
					if (!headChars.isEmpty()) {
						retShelfHead.append("<tr>");

						retShelfHead.append("<tr>");
						for (String headChar : headChars) {
							retShelfHead.append("<th class=\"ui-state-default\" style=\"width: 14px;\">" + headChar + "</th>");
						}
						retShelfHead.append("</tr>");
					}
					headChars = new ArrayList<String>();

					rowCols += storage.getSimple_code().length() * 14;
					if (cols < rowCols) {
						cols = rowCols;
					}
					rowCols = storage.getSimple_code().length() * 16;
					retShelf.append("</tr></tbody></table></div>");
					retSb.append(retShelfHead.toString().replaceAll("%w%", "" + cols))
						.append(retShelf);
					cols = 120;
				}
				shelf = storage.getShelf();
				retShelf = new StringBuffer("");
				retShelfHead = new StringBuffer("<div style=\"margin: 15px; float: left;\">"
						+ "<div class=\"ui-widget-header\" style=\"width: %w%px; text-align: center;\">货架 " + shelf + "</div>"
						+ "<table class=\"condform wip-table\" style=\"width: %w%px;\"><tbody>");
				layer = "" + storage.getLayer();
				retShelf.append("<tr>");
			} else {
				if (!layer.equals("" + storage.getLayer())) {
					retShelf.append("</tr><tr>");
					layer = "" + storage.getLayer();
					if (cols < rowCols) {
						cols = rowCols;
					}
					rowCols = storage.getSimple_code().length() * 16;
				} else {
					rowCols += storage.getSimple_code().length() * 14;
				}
			}
			setHeadChars(headChars, storage.getWip_storage_code());
			retShelf.append("<td wipid='" + storage.getWip_storage_code() 
					+ ((storage.getOccupied() != null && storage.getOccupied() == 1) ? "' class=\"ui-storage-highlight wip-heaped\"" : "' class=\"wip-empty\"")
					+ ((storage.getAnml_exp() != null && storage.getAnml_exp() == 1) ? " anml_exp" : "")
					+ ">" + storage.getSimple_code() + "</td>");
		}

		if (retShelf != null) {
			if (!headChars.isEmpty()) {
				retShelfHead.append("<tr>");

				retShelfHead.append("<tr>");
				for (String headChar : headChars) {
					retShelfHead.append("<th class=\"ui-state-default\" style=\"width: 14px;\">" + headChar + "</th>");
				}
				retShelfHead.append("</tr>");
			}
			rowCols += storageS.getSimple_code().length() * 16;
			if (cols < rowCols) {
				cols = rowCols;
			}
			retShelf.append("</tr></tbody></table></div>");
			retSb.append(retShelfHead.toString().replaceAll("%w%", "" + (cols)))
				.append(retShelf);
		}

		callbackResponse.put("storageHtml", retSb.toString());
	}

	/**
	 * 
	 * @param headChars
	 * @param wip_storage_code
	 */
	private void setHeadChars(List<String> headChars, String wip_storage_code) {
		String headChar = wip_storage_code.replaceAll("\\d", "");
		if (!CommonStringUtil.isEmpty(headChar) && headChar.length() == 1) {
			if (!headChars.contains(headChar)) {
				headChars.add(headChar);
			}
		}
	}

	private static String SECTION_1 = "00000000001";
	private static String SECTION_2 = "00000000003";
	private static String SECTION_3 = "00000000012";

	public void searchForPda(HttpServletRequest req, ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		PdaMaterialForm pdaMaterialForm = (PdaMaterialForm) form;
		pdaMaterialForm.setMaterial_id("");
		pdaMaterialForm.setWip_location("");
		String message = "";
		req.setAttribute("notice", message);
		req.setAttribute("storageMap", "");

		String materialId = req.getParameter("material_id");
		if (materialId == null) {
			MsgInfo error = new MsgInfo();error.setErrmsg("扫描失败，请重试。");
			errors.add(error);
			return;
		}

		boolean searchByMaterial = true;
		Integer ableToInline = 0;

		MaterialEntity entity = null;
		if (materialId.length() == 11) {
			MaterialMapper mMapper = conn.getMapper(MaterialMapper.class);
			entity = mMapper.getMaterialNamedEntityByKey(materialId);
			if (MaterialTagService.getAnmlMaterials(conn).contains(materialId)) {
				List<String> anmlPats = ProcessAssignService.getAnmlProcesses(conn);
				entity.setPat_id(anmlPats.get(0));
			}

			if (entity == null) {
				MsgInfo error = new MsgInfo();error.setErrmsg("扫描的维修品小票号或者库位号不存在。");
				errors.add(error);
				return;
			}
		} else {
			WipMapper wipMapper = conn.getMapper(WipMapper.class);
			MaterialEntity cndi = new MaterialEntity();
			cndi.setWip_location(materialId);
			List<MaterialEntity> l = wipMapper.searchMaterial(cndi);

			if (l == null || l.size() == 0) {
				WipStorageEntity cndiStor = new WipStorageEntity();
				cndiStor.setWip_storage_code(materialId);
				List<WipStorageEntity> lWip = wipMapper.searchWipStorage(cndiStor);
				if (lWip.size() == 0) {
					MsgInfo error = new MsgInfo();error.setErrmsg("扫描的维修品小票号或者库位号不存在。");
					errors.add(error);
				} else {
					MsgInfo error = new MsgInfo();error.setErrmsg("扫描的库位号存在，但系统中此库位无修理品。");
					errors.add(error);
				}
				return;
			} else {
				entity = l.get(0);
				searchByMaterial = false;
			}
		}

		boolean isPeripheral = RvsUtils.isPeripheral(entity.getLevel());

		pdaMaterialForm.setPat_id("");
		pdaMaterialForm.setPat_name("-");
		pdaMaterialForm.setCcd_operate_result("-");

		if (entity.getWip_location() == null || isPeripheral) {
			if (entity.getInline_time() != null) {
				MsgInfo error = new MsgInfo();error.setErrmsg("修理品" + entity.getSorc_no() + "系统中已经投线，无法处理。");
				errors.add(error);
				return;
			} else if (entity.getAgreed_date() == null) {
				MsgInfo error = new MsgInfo();error.setErrmsg("修理品" + entity.getSorc_no() + "系统中已经既不在库也未同意，无法处理。");
				errors.add(error);
				return;
			}

			// 待出库处理
			ableToInline = 1;
			boolean isLight = RvsUtils.isLightFix(entity.getLevel());

			if (entity.getPat_id() != null) {
				pdaMaterialForm.setPat_id(entity.getPat_id());
			} else {
				if (!isLight) {
					if (MaterialTagService.getAnmlMaterials(conn).contains(materialId)) {
						List<String> anmlPats = ProcessAssignService.getAnmlProcesses(conn);
						entity.setPat_id(anmlPats.get(0));
					} else {
						ModelMapper mdlMapper = conn.getMapper(ModelMapper.class);
						pdaMaterialForm.setPat_id(
								mdlMapper.getModelByID(entity.getModel_id()).getDefault_pat_id());
					}
				}
			}

			if (!isLight) {
				if (pdaMaterialForm.getPat_id() != null) {
					ProcessAssignMapper paMapper = conn.getMapper(ProcessAssignMapper.class);
					ProcessAssignTemplateEntity pat = paMapper.getProcessAssignTemplateByID(pdaMaterialForm.getPat_id());

					if (pat != null) {
						pdaMaterialForm.setPat_name(pat.getName());
					}
				} else {
					ableToInline += 2;
				}
			} else {
				// 中小修 首工位
				MaterialProcessAssignMapper paMapper = conn.getMapper(MaterialProcessAssignMapper.class);
				ProcessAssignEntity firstPos = paMapper.getFirstPosition(materialId);
				if (firstPos != null) {
					String firstPosId = firstPos.getPosition_id();
					PositionService posService = new PositionService();
					pdaMaterialForm.setPat_name("首工位：" + posService.getPositionEntityByKey(firstPosId, conn).getProcess_code());
				} else {
					ableToInline += 2;
				}
			}

			if (RvsUtils.getCcdModels(conn).contains(entity.getModel_id())) {
				ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);
				ProductionFeatureEntity cndt = new ProductionFeatureEntity();
				cndt.setMaterial_id(materialId);
				cndt.setPosition_id("00000000025");

				List<ProductionFeatureEntity> founds = pfMapper.searchProductionFeature(cndt);
				if (founds.size() == 0) {
					MaterialTagService mtService = new MaterialTagService();
					List<Integer> lForCcdRepalce = mtService.checkTagByMaterialId(materialId, MaterialTagService.TAG_FOR_CCD_REPLACE, conn);
					if (lForCcdRepalce.size() > 0) {
						pdaMaterialForm.setCcd_operate_result("待作业");
					} else {
						pdaMaterialForm.setCcd_operate_result("-");
					}
				} else {
					pdaMaterialForm.setCcd_operate_result("进行中");
					for (ProductionFeatureEntity found : founds) {
						if (found.getOperate_result() == RvsConsts.OPERATE_RESULT_FINISH) {
							pdaMaterialForm.setCcd_operate_result("完成");
							break;
						}
					}
				}

				if (!"完成".equals(pdaMaterialForm.getCcd_operate_result())
						&& !"-".equals(pdaMaterialForm.getCcd_operate_result())) {
					ableToInline += 4;
				}
			}
		}

		pdaMaterialForm.setMaterial_id(entity.getMaterial_id());
		pdaMaterialForm.setOmr_notifi_no(entity.getSorc_no());
		pdaMaterialForm.setModel_name(entity.getModel_name());
		pdaMaterialForm.setSerial_no(entity.getSerial_no());
		pdaMaterialForm.setWip_location(entity.getWip_location());

		if (MaterialTagService.getAnmlMaterials(conn).contains(entity.getMaterial_id())) {
			pdaMaterialForm.setSection_id(SECTION_1);
			pdaMaterialForm.setSection_name("动物实验");
		} else {
			String section_id = entity.getSection_id();
			if (section_id == null) {
				Integer level = entity.getLevel();
				boolean isLightFix = RvsUtils.isLightFix(level);

				section_id = SECTION_1;
				if (isLightFix) {
					section_id = SECTION_2;
				} else if (isPeripheral) {
					section_id = SECTION_3;
				} else if ("03".equals(entity.getKind())
						|| "04".equals(entity.getKind())
						|| "06".equals(entity.getKind())){
					section_id = SECTION_2;
				} else if ("07".equals(entity.getKind())){
					section_id = SECTION_3;
				} else if (entity.getCategory_name() != null && entity.getCategory_name().indexOf("超声") >= 0) {
					section_id = SECTION_2;
				}
			}
			SectionMapper sMappper = conn.getMapper(SectionMapper.class);
			pdaMaterialForm.setSection_id(section_id);
			pdaMaterialForm.setSection_name(sMappper.getSectionByID(section_id).getName());
		}

		// part_status
		MaterialPartialMapper mpMapper = conn.getMapper(MaterialPartialMapper.class);
		MaterialPartialEntity mpCndi = new MaterialPartialEntity();
		mpCndi.setMaterial_id(entity.getMaterial_id());
		mpCndi.setOccur_times(1);
		MaterialPartialEntity mp = mpMapper.loadMaterialPartial(mpCndi);
		if (mp == null) {
			pdaMaterialForm.setPart_status("未定购");
		} else if (mp.getArrival_date() == null) {
			pdaMaterialForm.setPart_status("待发放");
		} else {
			pdaMaterialForm.setPart_status("已发放");
		}

		if (ableToInline > 0) {
			if (!"已发放".equals(pdaMaterialForm.getPart_status())
					&& entity.getLevel() != 57) {
				ableToInline += 8;
			} 
		}

		if (searchByMaterial) {
			if (ableToInline == null || ableToInline == 0) {
				message = "已扫描维修品单号为[" + pdaMaterialForm.getOmr_notifi_no() + "]。请扫描对应库位号以出库。";
			} else {
				
			}
		} else {
			message = "已扫描库位号为[" + pdaMaterialForm.getWip_location() + "]。请扫描对应库位号。扫描其他修理单号会取消作业。";
		}
		req.setAttribute("notice", message);
		req.setAttribute("ableToInline", "" + ableToInline);

		if (entity.getWip_location() != null) {
			req.setAttribute("storageMap", getStoragePositionInShelf(entity.getWip_location(), conn));
		}

	}

	public String getStoragePositionInShelf(String location, SqlSession conn) {
		StringBuffer sbRet = new StringBuffer("");

		WipMapper wipMapper = conn.getMapper(WipMapper.class);
		WipStorageEntity cndiStor = new WipStorageEntity();
		cndiStor.setWip_storage_code(location);
		List<WipStorageEntity> lWip = wipMapper.searchWipStorage(cndiStor);

		if (lWip.size() > 0) {
			String shelf = lWip.get(0).getShelf();
			List<String> headChars = new ArrayList<String>();

			cndiStor.setWip_storage_code(null);
			cndiStor.setShelf(shelf);
			sbRet.append("<table style='width:99%;'><tr><th colspan=4>货架 " + shelf + "</th>");
			lWip = wipMapper.searchWipStorage(cndiStor);

			String layer = "-";

			for (WipStorageEntity storage : lWip) {
				if (!layer.equals("" + storage.getLayer())) {
					sbRet.append("</tr><tr style='height:1em;'>");
					layer = "" + storage.getLayer();
				}
				setHeadChars(headChars, storage.getWip_storage_code());
				if (location.equals(storage.getWip_storage_code())) {
					sbRet.append("<td>■</td>");
				} else {
					sbRet.append("<td>□</td>");
				}
			}

			sbRet.append("</tr></table>");
		}
		return sbRet.toString();
	}

	public WipStorageEntity getLocationDetail(
			String wip_storage_code, SqlSession conn) {

		WipMapper wipMapper = conn.getMapper(WipMapper.class);

		WipStorageEntity codeCond = new WipStorageEntity();
		codeCond.setWip_storage_code(wip_storage_code);
		List<WipStorageEntity> emptyStorages = wipMapper.searchWipStorage(codeCond);

		if (emptyStorages.size() > 0) {
			return emptyStorages.get(0);
		}

		return null;
	}
}
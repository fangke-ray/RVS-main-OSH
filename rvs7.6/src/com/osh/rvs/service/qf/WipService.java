package com.osh.rvs.service.qf;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.qa.QualityAssuranceMapper;
import com.osh.rvs.mapper.qf.WipMapper;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.partial.PartialOrderManageService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.IntegerConverter;
import framework.huiqing.common.util.message.ApplicationMessage;

public class WipService {

	Logger _logger = Logger.getLogger(WipService.class);

	/**
	 * 检索WIP中的全部维修对象
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return List<ModelForm> 查询结果表单
	 */
	public List<MaterialForm> searchMaterial(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		MaterialEntity conditionBean = new MaterialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		List<MaterialEntity> lResultBean = new ArrayList<MaterialEntity>();
		WipMapper dao = conn.getMapper(WipMapper.class);
		lResultBean = dao.searchMaterial(conditionBean);
		
		List<MaterialForm> lResultForm = new ArrayList<MaterialForm>();
		
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, MaterialForm.class);
		
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
//						nextWorkingPf.setPosition_id("00000000047");
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

		// RVS3 quandao 711
//		MaterialMapper mdao = conn.getMapper(MaterialMapper.class);
//		MaterialEntity mbean = mdao.getMaterialEntityByKey(material_id);

		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);

		WipMapper dao = conn.getMapper(WipMapper.class);
		dao.stop(material_id);

		boolean check = pfMapper.checkPositionDid(material_id, "00000000012", ""+RvsConsts.OPERATE_RESULT_FINISH, null);
		if (!check) { // RVS3 如果没完成测漏
			// 出货
			ProductionFeatureMapper pfDao = conn.getMapper(ProductionFeatureMapper.class);
			ProductionFeatureEntity entity = new ProductionFeatureEntity();
			entity.setMaterial_id(material_id);
			entity.setPosition_id("00000000047");
			entity.setPace(0);
			entity.setSection_id("00000000000");
			entity.setOperate_result(0);
			entity.setRework(0);
			pfDao.insertProductionFeature(entity);

			// 如果是周边设备则归档 V6
			MaterialMapper mMapper = conn.getMapper(MaterialMapper.class);
			MaterialEntity mBean = mMapper.getMaterialNamedEntityByKey(material_id);
			if ("07".equals(mBean.getKind()) && mBean.getFix_type() == 1) {
				// 如果进行过181则认定为归档 TODO
				QualityAssuranceMapper qaMapper = conn.getMapper(QualityAssuranceMapper.class);
				mBean.setQa_check_time(new Date());
				mBean.setOutline_time(null);
				qaMapper.updateMaterial(mBean);

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

			// 如果是周边或小修理/光学视管，则取消零件订购。
			if ("07".equals(mBean.getKind()) || RvsConsts.CATEGORY_UDI.equals(mBean.getCategory_id())
				|| RvsUtils.isLightFix(mBean.getLevel())) {
				PartialOrderManageService pomService = new PartialOrderManageService();
				pomService.deleteMaterialPartial(material_id, conn);
			}
		} else {
			// 图象检查
			ProductionFeatureMapper pfDao = conn.getMapper(ProductionFeatureMapper.class);

			Map<String, Object> params = new HashMap<String, Object>();
			List<String> positions = new ArrayList<String>();
			positions.add("00000000015");
			params.put("material_id", material_id);
			params.put("position_ids", positions);

			int rework = pfDao.getReworkCountWithPositions(params);
			ProductionFeatureEntity entity = new ProductionFeatureEntity();
			entity.setMaterial_id(material_id);
			entity.setPosition_id("00000000015");
			entity.setPace(0);
			entity.setSection_id("00000000009"); // TODO
			entity.setOperate_result(0);
			entity.setRework(rework + 1);
			pfDao.insertProductionFeature(entity);
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
}

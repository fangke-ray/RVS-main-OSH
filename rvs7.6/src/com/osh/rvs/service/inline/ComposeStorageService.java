package com.osh.rvs.service.inline;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.ComposeStorageEntity;
import com.osh.rvs.bean.inline.MaterialProcessEntity;
import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.ProductionFeatureForm;
import com.osh.rvs.form.inline.ComposeStorageForm;
import com.osh.rvs.mapper.inline.ComposeStorageMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.inline.SupportMapper;
import com.osh.rvs.service.MaterialProcessService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ComposeStorageService {
	public List<ComposeStorageForm> search(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 复制表单到数据对象
		ComposeStorageEntity condition = new ComposeStorageEntity();
		BeanUtil.copyToBean(form, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 从数据库中 查询记录
		ComposeStorageMapper dao = conn.getMapper(ComposeStorageMapper.class);
		List<ComposeStorageEntity> lResultBean = dao.searchComposeStroage(condition);

		// 不针对维修品查询时
		if (condition.getCategory_id() == null &&
				condition.getSorc_no() == null &&
				condition.getSerial_no() == null &&
				condition.getScheduled_date_start() == null &&
				condition.getScheduled_date_end() == null &&
				condition.getArrival_plan_date_start() == null &&
				condition.getArrival_plan_date_end() == null &&
				condition.getCom_scheduled_date_start() == null &&
				condition.getCom_scheduled_date_end() == null &&
				condition.getBo_flg() == null) {
			condition.setGoods_id("null");
			List<ComposeStorageEntity> lResultBeanEmpty = dao.getComposeStorageEntities(condition);
			if (lResultBeanEmpty != null) {
				lResultBean.addAll(lResultBeanEmpty);
			}
		}

		// 建立页面返回表单
		List<ComposeStorageForm> lResultForm = new ArrayList<ComposeStorageForm>();

		// 将数据复制到表单对象
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, ComposeStorageForm.class);
		return lResultForm;
	}

	public List<ComposeStorageForm> getComposEmpty(SqlSession conn, List<MsgInfo> errors) {
		// 从数据库中 查询记录
		ComposeStorageMapper dao = conn.getMapper(ComposeStorageMapper.class);
		List<ComposeStorageEntity> lResultBean = dao.getComposNotEmpty();

		// 建立页面返回表单
		List<ComposeStorageForm> lResultForm = new ArrayList<ComposeStorageForm>();

		// 将数据复制到表单对象
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, ComposeStorageForm.class);
		return lResultForm;
	}

	/**
	 * 检查维修对象是否存在
	 * 
	 * @param conn
	 * @param material_id
	 * @param errors
	 * @return 如果存在返回维修对象信息
	 */
	public ComposeStorageForm checkMaterialExist(SqlSession conn, String material_id, List<MsgInfo> errors) {
		ComposeStorageMapper dao = conn.getMapper(ComposeStorageMapper.class);
		ComposeStorageEntity bean = dao.checkMaterialExist(material_id);

		if (bean == null) {
			MsgInfo thisError = new MsgInfo();
			thisError.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.invalidCode", "总组工程签收"));
			errors.add(thisError);
			return null;
		} else {
			// 建立页面返回表单
			ComposeStorageForm form = new ComposeStorageForm();
			// 复制对象数据到表单
			BeanUtil.copyToForm(bean, form, CopyOptions.COPYOPTIONS_NOEMPTY);
			return form;
		}
	}

	public String checkMaterialPutin(String material_id, String line_id,
			SqlSession conn) {
		ComposeStorageMapper mapper = conn.getMapper(ComposeStorageMapper.class);

		return mapper.checkMaterialPutin(material_id, line_id);
	}

	/**
	 * 移动库位
	 * 
	 * @param conn
	 * @param goods_id
	 * @param old_scan_code
	 * @param scan_code
	 */
	public void changelocation(SqlSessionManager conn, String goods_id, String old_scan_code, String scan_code,
			List<MsgInfo> msgInfos) {
		ComposeStorageMapper dao = conn.getMapper(ComposeStorageMapper.class);
		ComposeStorageEntity entity = dao.searchShelfNameExits(scan_code);
		MsgInfo msg = new MsgInfo();
		if (entity != null) {//货架存在
			ComposeStorageEntity comentity = dao.searchGoodsExits(scan_code);
			if (comentity == null) {//货架上没有东西
				dao.updateLocation(old_scan_code);
				dao.changeLocation(goods_id, scan_code);
			} else {
				msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.composeShelf.notEmpty"));
				msg.setComponentid("scanner_com");
				msg.setErrcode("info.composeShelf.notEmpty");
				msgInfos.add(msg);
			}
		} else {
			msg.setComponentid("scanner_com");
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.composeShelf.notExist"));
			msg.setErrcode("info.composeShelf.notExist");
			msgInfos.add(msg);
		}

	}

	/**
	 * 入库
	 * 
	 * @param conn
	 * @param material_id
	 * @param scan_code
	 */
	public void insertCom(SqlSessionManager conn, String material_id, String case_code, List<MsgInfo> msgInfos) {
		ComposeStorageMapper dao = conn.getMapper(ComposeStorageMapper.class);
		ComposeStorageEntity entity = dao.searchShelfNameExits(case_code);
		MsgInfo msg = new MsgInfo();
		if (entity != null) {//货架存在
			ComposeStorageEntity comentity = dao.searchGoodsExits(case_code);
			if (comentity == null) {//货架上没有东西
				dao.insertCom(material_id, case_code);
			} else {
				msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.composeShelf.notEmpty"));
				msg.setComponentid("scanner_com");
				msg.setErrcode("info.composeShelf.notEmpty");
				msgInfos.add(msg);
			}
		} else {
			msg.setComponentid("scanner_com");
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.composeShelf.notExist"));
			msg.setErrcode("info.composeShelf.notExist");
			msgInfos.add(msg);
		}

	}

	/**
	 * 总组签收作业启动
	 * 
	 * @param conn
	 * @param form
	 * @param request
	 * @throws Exception
	 */
	public void insertFeature(SqlSessionManager conn, ComposeStorageForm form, HttpServletRequest request)
			throws Exception {
		ProductionFeatureMapper ProductionFeatureDao = conn.getMapper(ProductionFeatureMapper.class);
		Integer rework = ProductionFeatureDao.getReworkCount(form.getMaterial_id());// 返工作业

		ProductionFeatureEntity enity = new ProductionFeatureEntity();
		BeanUtil.copyToBean(form, enity, CopyOptions.COPYOPTIONS_NOEMPTY);
		enity.setPosition_id("32");
		enity.setRework(rework);
		SupportMapper SupportMapperDao = conn.getMapper(SupportMapper.class);
		int pace = SupportMapperDao.getSupportPace(enity);// 分段号

		enity.setPace(pace);

		LoginData data = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = data.getOperator_id();
		enity.setOperator_id(operator_id);

		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		dao.supportProductionFeature(enity);
	}

	/**
	 * 更新production_feature finish_time
	 * 
	 * @param conn
	 * @param request
	 * @param goods_id
	 * @throws Exception
	 */
	public void updateFeature(SqlSessionManager conn, HttpServletRequest request, String goods_id) throws Exception {
		ProductionFeatureEntity entity = new ProductionFeatureEntity();

		LoginData data = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = data.getOperator_id();

		entity.setOperator_id(operator_id);
		entity.setPosition_id("32");
		entity.setMaterial_id(goods_id);
		SupportMapper SupportMapperDao = conn.getMapper(SupportMapper.class);
		ProductionFeatureEntity tempEntity = SupportMapperDao.searchSupportingProductionFeature(entity);

		ProductionFeatureMapper productionFeatureMapper = conn.getMapper(ProductionFeatureMapper.class);

		if (tempEntity != null) {
			productionFeatureMapper.finishProductionFeature(tempEntity);
		}
	}

	/**
	 * 查询维修对象是否正在操作
	 * 
	 * @param conn
	 * @param request
	 * @param errors
	 * @return 如果存在返回维修对象信息
	 */
	public ProductionFeatureForm find(SqlSession conn, HttpServletRequest request, List<MsgInfo> errors) {
		ProductionFeatureEntity entity = new ProductionFeatureEntity();

		LoginData data = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);// 获取session
		String operator_id = data.getOperator_id();
		entity.setOperator_id(operator_id);

		SupportMapper SupportMapperDao = conn.getMapper(SupportMapper.class);
		ProductionFeatureEntity tempEntity = SupportMapperDao.searchComposeStorageWork(entity);
		ProductionFeatureForm form = new ProductionFeatureForm();
		if (tempEntity != null) {
			BeanUtil.copyToForm(tempEntity, form, CopyOptions.COPYOPTIONS_NOEMPTY);
			return form;
		} else {
			return null;
		}
	}

	/**
	 * 取得范围内推荐的放入库位
	 * 
	 * @param line_id
	 * @param px
	 * @param spec_type
	 * @param conn
	 * @return
	 */
	public String getRecommendCase(String line_id, Integer px, Integer spec_type, SqlSession conn) {
		ComposeStorageMapper mapper = conn.getMapper(ComposeStorageMapper.class);
		ComposeStorageEntity condition = new ComposeStorageEntity();
		condition.setLine_id(line_id);
		condition.setPx(px);
		condition.setSpec_type(spec_type);
		List<ComposeStorageEntity> allList = mapper.getComposeStorageEntities(condition);

		if (allList == null || allList.size() == 0) {
			return null;
		}

		Long newestRefreshTime = 0l;
		String hitCase = null;
		String firstEmptyCase = null;

		for (ComposeStorageEntity entity : allList) {
			if (entity.getGoods_id() == null) {
				if (firstEmptyCase == null) firstEmptyCase = entity.getCase_code();
				if (hitCase == null) hitCase = entity.getCase_code();
			} else {
				if (entity.getRefresh_time() != null && entity.getRefresh_time().getTime() > newestRefreshTime) {
					newestRefreshTime = entity.getRefresh_time().getTime();
					hitCase = null;
				}
			}
		}

		if (newestRefreshTime > 0 && hitCase == null) {
			hitCase = firstEmptyCase;
		}

		return hitCase;
	}

	public String checkRecommendCase(ModelEntity model, String line_id, SqlSession conn) {
		// 特殊库位要求
		Integer spec_kind = null;
		MaterialProcessService mpService = new MaterialProcessService();
		int px = mpService.evalPx(model.getModel_id(), "00000000014", false, conn);

		if (px == MaterialProcessService.PX_B_OF_2 && "00000000013".equals(line_id)) {
			// B2 线根据型号判断分类
			// 不含混合细镜、超声细镜
			if ("04".equals(model.getKind()) || "05".equals(model.getKind())) {
				return null;
			}

			spec_kind = 0;
			// 取得滑石粉机型
			String talcumModels = PathConsts.POSITION_SETTINGS.getProperty("com_storage.talcum.models");
			if (talcumModels != null) {
				String[] talcumModelArray = talcumModels.split(",");
				for (String specModel : talcumModelArray) {
					if (specModel.equals(model.getName())) {
						spec_kind = 1; // 滑石粉
						break;
					}
				}
			}
		}

		if (px == MaterialProcessService.PX_B_OF_1 && "00000000012".equals(line_id)) {
			spec_kind = 0;
			// 取得大量零件机型
			String talcumModels = PathConsts.POSITION_SETTINGS.getProperty("com_storage.massPart.models");
			if (talcumModels != null) {
				String[] massPartModelArray = talcumModels.split(",");
				for (String specModel : massPartModelArray) {
					if (specModel.equals(model.getName())) {
						spec_kind = 2; // 大量零件
						break;
					}
				}
			}
		}

		String recommendCase = getRecommendCase(line_id, px, spec_kind, conn);

		if (recommendCase == null) {
			return RvsConsts.COM_STORAGE_INSTABLE;
		} else {
			return recommendCase;
		}
	}

	public String checkRecommendCase(MaterialEntity mEntity, String line_id, int reworkFromPf,
			SqlSession conn) {
		// 判断放入总组库位
		if (!mEntity.getSection_id().equals("00000000001")) { // 不是 1课
			return null;
		}

		boolean isLightFix = RvsUtils.isLightFix(mEntity.getLevel());
		if (isLightFix) {
			return null;
		}

		// 只放A/B1/B2库位 = 粗镜/细镜分类
		switch (mEntity.getKind()) {
		case "01" :
		case "02" :
			break;
		default :
			return null;
		}

		// 取得总组工程
		MaterialProcessService mpService = new MaterialProcessService();

		MaterialProcessEntity mpOfCom = mpService.loadMaterialProcessOfLine(mEntity.getMaterial_id(), "00000000014", conn);
		if (mpOfCom == null || mpOfCom.getPx() == null || mpOfCom.getPx() == MaterialProcessService.PX_C) { // C 线不排
			return null;
		}

		// 判断总组是否有作业
		if (reworkFromPf > 0) {
			ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);

			ProductionFeatureEntity pfEntity = new ProductionFeatureEntity();
			pfEntity.setMaterial_id(mEntity.getMaterial_id());
			pfEntity.setLine_id("00000000014");
			List<ProductionFeatureEntity> retEntities = pfMapper.searchProductionFeature(pfEntity);
			if (retEntities.size() > 0) {
				ProductionFeatureEntity reworkRecord = null;
				for (ProductionFeatureEntity retEntity : retEntities) {
					if (retEntity.getOperate_result() != RvsConsts.OPERATE_RESULT_NOWORK_WAITING) {
						reworkRecord = retEntity;
						break;
					}
				}
				if (reworkRecord != null) { // 总组作业过工不需要排
					return RvsConsts.COM_STORAGE_PROCESSED;
				}
			}

		}

		// 特殊库位要求
		Integer spec_kind = null;
		if (mpOfCom.getPx() == MaterialProcessService.PX_B_OF_2 && "00000000013".equals(line_id)) {
			// B2 线根据型号判断分类
			// 不含混合细镜、超声细镜
			if ("04".equals(mEntity.getKind()) || "05".equals(mEntity.getKind())) {
				return null;
			}

			spec_kind = 0;
			// 取得滑石粉机型
			String talcumModels = PathConsts.POSITION_SETTINGS.getProperty("com_storage.talcum.models");
			if (talcumModels != null) {
				String[] talcumModelArray = talcumModels.split(",");
				for (String talcumModel : talcumModelArray) {
					if (talcumModel.equals(mEntity.getModel_name())) {
						spec_kind = 1; // 滑石粉
						break;
					}
				}
			}
		}
		if (mpOfCom.getPx() == MaterialProcessService.PX_B_OF_1 && "00000000012".equals(line_id)) {
			spec_kind = 0;
			// 取得大量零件机型
			String talcumModels = PathConsts.POSITION_SETTINGS.getProperty("com_storage.massPart.models");
			if (talcumModels != null) {
				String[] massPartModelArray = talcumModels.split(",");
				for (String specModel : massPartModelArray) {
					if (specModel.equals(mEntity.getModel_name())) {
						spec_kind = 2; // 大量零件
						break;
					}
				}
			}
		}

		String recommendCase = getRecommendCase(line_id, mpOfCom.getPx(), spec_kind, conn);

		if (recommendCase == null) {
			return RvsConsts.COM_STORAGE_INSTABLE;
		} else {
			return recommendCase;
		}
	}

}

package com.osh.rvs.service.inline;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.ComposeStorageEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.ProductionFeatureForm;
import com.osh.rvs.form.inline.ComposeStorageForm;
import com.osh.rvs.mapper.inline.ComposeStorageMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.inline.SupportMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ComposeStorageService {
	public List<ComposeStorageForm> search(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 复制表单到数据对象
		ComposeStorageEntity composeStorageEntity = new ComposeStorageEntity();
		BeanUtil.copyToBean(form, composeStorageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 从数据库中 查询记录
		ComposeStorageMapper dao = conn.getMapper(ComposeStorageMapper.class);
		List<ComposeStorageEntity> lResultBean = dao.searchComposeStroage(composeStorageEntity);

		// 建立页面返回表单
		List<ComposeStorageForm> lResultForm = new ArrayList<ComposeStorageForm>();

		// 将数据复制到表单对象
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, ComposeStorageForm.class);
		return lResultForm;
	}

	public List<ComposeStorageForm> getComposEmpty(SqlSession conn, List<MsgInfo> errors) {
		// 从数据库中 查询记录
		ComposeStorageMapper dao = conn.getMapper(ComposeStorageMapper.class);
		List<ComposeStorageEntity> lResultBean = dao.getComposEmpty();

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
	public void insertCom(SqlSessionManager conn, String material_id, String scan_code, List<MsgInfo> msgInfos) {
		ComposeStorageMapper dao = conn.getMapper(ComposeStorageMapper.class);
		ComposeStorageEntity entity = dao.searchShelfNameExits(scan_code);
		MsgInfo msg = new MsgInfo();
		if (entity != null) {//货架存在
			ComposeStorageEntity comentity = dao.searchGoodsExits(scan_code);
			if (comentity == null) {//货架上没有东西
				dao.insertCom(material_id, scan_code);
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

}

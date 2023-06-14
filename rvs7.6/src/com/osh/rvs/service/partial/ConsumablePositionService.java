package com.osh.rvs.service.partial;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.mapper.inline.MaterialProcessAssignMapper;
import com.osh.rvs.mapper.master.ConsumablePositionMapper;
import com.osh.rvs.mapper.partial.MaterialConsumableDetailMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.mapper.partial.PartialReceptMapper;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.inline.PositionPanelService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ConsumablePositionService {

	private Logger _logger = Logger.getLogger(ConsumablePositionService.class);
	
	/**
	 * 获取单个消耗品申请单信息
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<MaterialPartialDetailEntity> findPositionBelong(MaterialPartialDetailEntity condition,SqlSession conn){
		ConsumablePositionMapper mapper = conn.getMapper(ConsumablePositionMapper.class);

		return mapper.findPositionBelong(condition);
	}

	public void setPositionBelong(HttpServletRequest request,
			SqlSessionManager conn, List<MsgInfo> errors) {
		List<MaterialPartialDetailEntity> entities = new AutofillArrayList<MaterialPartialDetailEntity>(MaterialPartialDetailEntity.class);
		Pattern p = Pattern.compile("(\\w+)\\[(\\d+)\\].(\\w+)");

		String partial_id = request.getParameter("partial_id");

		Map<String, String[]> parameterMap = request.getParameterMap();
		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("position".equals(entity)) {
					String column = m.group(3);
					int icounts = Integer.parseInt(m.group(2));
					String[] value = parameterMap.get(parameterKey);

					switch(column) {
					case "model_id" : entities.get(icounts).setModel_id(value[0]); break;
					case "position_id" : entities.get(icounts).setPosition_id(value[0]); break;
					case "quantity" : entities.get(icounts).setQuantity(Integer.parseInt(value[0])); break;
					case "updated" : entities.get(icounts).setProcess_code(value[0]); break;
					case "org_position_id" : entities.get(icounts).setParent_partial_id(value[0]); break;
					}
				}
			}
		}

		ConsumablePositionMapper cpMapper = conn.getMapper(ConsumablePositionMapper.class);
		for (MaterialPartialDetailEntity entity : entities) {
			entity.setPartial_id(partial_id);
			switch(entity.getProcess_code()) {
			case "rmv" : {
				entity.setPosition_id(entity.getParent_partial_id());
				cpMapper.delete(entity);
				break;
			}
			case "upd" : {
				String newPositionId = entity.getPosition_id();
				entity.setPosition_id(entity.getParent_partial_id());
				cpMapper.delete(entity);
				// continue insert
				entity.setPosition_id(newPositionId);
			}
			case "ins" : {
				cpMapper.create(entity);
			}
			}
		}
	}

	private static List<String> usePositions = null;

	public static boolean isConsumableInlinePositions(String position_id, SqlSession conn) {
		ConsumablePositionMapper cpMapper = conn.getMapper(ConsumablePositionMapper.class);
//		if (usePositions == null) {
			usePositions = new ArrayList<String>();
			for (String posId : cpMapper.locateUsePositions()) {
				usePositions.add(CommonStringUtil.fillChar(posId, '0', 11, true));
			}
//		}
		return usePositions.contains(position_id);
	}

	/**
	 * 按修理品取得所在工位的可使用消耗品一览
	 * 
	 * @param form
	 * @param position_id
	 * @param isLeader 
	 * @param conn
	 * @return
	 */
	public List<MaterialPartialDetailForm> getByMaterialOfPosition(ActionForm form,
			String position_id, boolean isLeader, SqlSession conn) {
		MaterialPartialDetailEntity cond = new MaterialPartialDetailEntity();
		BeanUtil.copyToBean(form, cond, CopyOptions.COPYOPTIONS_NOEMPTY);
		_logger.info("search for material:" + cond.getMaterial_id() + ",belongs" + cond.getBelongs());

		ConsumablePositionMapper mapper = conn.getMapper(ConsumablePositionMapper.class);
		// 没有线长权限的话，限定本工位
		if (!isLeader) {
			cond.setPosition_id(position_id);
		}
		List<MaterialPartialDetailEntity> result = mapper.findPositionBelong(cond);

		List<String> gPositions = new ArrayList<String>(); 
		if (PositionService.getPositionMappings(conn).containsKey(position_id)) {
			// 集合工位
			MaterialProcessAssignMapper mpaMapper = conn.getMapper(MaterialProcessAssignMapper.class);
			List<String> selectedMappingsIds = mpaMapper.getSelectedMappingsId(cond.getMaterial_id(), position_id);
			gPositions.addAll(selectedMappingsIds);
		}
		if (PositionService.getPositionUnitizeds(conn).containsKey(position_id)) {
			// 动物内镜单元工位
			gPositions = PositionService.getPositionUnitizeds(conn).get(position_id);
		}

		if (isLeader) {
			// 当前工位（或集合工位）提前
			result = currentUpper(position_id, gPositions, result);
		}

		List<MaterialPartialDetailForm> rstForm = new ArrayList<MaterialPartialDetailForm>();
		Map<String, Integer> partialRegisted = new HashMap<String, Integer>();
		MaterialPartialDetailForm rst = null;
		CopyOptions copyToMpd = new CopyOptions();
		copyToMpd.excludeEmptyString(); copyToMpd.excludeNull();
		copyToMpd.exclude("level");

		for (MaterialPartialDetailEntity entity : result) {
			Integer hasCurrentPartial = partialRegisted.get(entity.getPartial_id());
			if (hasCurrentPartial == null) {
				partialRegisted.put(entity.getPartial_id(), rstForm.size());
				if (rst != null) {
					if (new File(PathConsts.BASE_PATH + PathConsts.PHOTOS + "/consumable/" + rst.getCode() + "_fix.jpg").exists()) {
						rst.setSpec_kind("1");
					}
					rstForm.add(rst);
				}
				rst = new MaterialPartialDetailForm();
				BeanUtil.copyToForm(entity, rst, copyToMpd);
				rst.setLevel(entity.getLevel());
			} else if (rst.getQuantity() != null) {
				continue;
			} else {
				rst.setBom_quantity(rst.getBom_quantity() + " | " + entity.getBom_quantity());
			}
			if (position_id.equals(entity.getPosition_id())) {
				rst.setStatus("1");
			}
		}
		if (rst != null) {
			if (new File(PathConsts.BASE_PATH + PathConsts.PHOTOS + "/consumable/" + rst.getCode() + "_fix.jpg").exists()) {
				rst.setSpec_kind("1");
			}
			rstForm.add(rst);
		}

		return rstForm;
	}

	/**
	 * 排列当前工位到顶部
	 * @param position_id
	 * @param gPositions
	 * @param result
	 * @return
	 */
	private List<MaterialPartialDetailEntity> currentUpper(String position_id,
			List<String> gPositions, List<MaterialPartialDetailEntity> result) {
		List<MaterialPartialDetailEntity> newResult = new ArrayList<MaterialPartialDetailEntity>();
		int upperIndex = 0;
		for (MaterialPartialDetailEntity entity : result) {
			if (position_id.equals(entity.getPosition_id())) {
				newResult.add(upperIndex++, entity);
			} else if (gPositions.contains(entity.getPosition_id())) {
				// 有同名零件，则累加数量，否则作为集合工位存放
				boolean exist = false;
				for (int i = 0; i < upperIndex; i++) {
					exist = newResult.get(i).getPartial_id().equals(entity.getPartial_id());
					if (exist){
						MaterialPartialDetailEntity hitEntity = newResult.get(i);

						Integer oQuantity = hitEntity.getQuantity();
						if (oQuantity == null) oQuantity = 0;
						Integer tQuantity = entity.getQuantity();
						if (tQuantity == null) tQuantity = 0;
						hitEntity.setQuantity(oQuantity + tQuantity);

						Integer obom_quantity = hitEntity.getBom_quantity();
						if (obom_quantity == null) obom_quantity = 0;
						Integer tbom_quantity = entity.getBom_quantity();
						if (tbom_quantity == null) tbom_quantity = 0;
						hitEntity.setBom_quantity(obom_quantity + tbom_quantity);

						break;
					}
				}
				if (!exist) {
					// 作为集合工位存放
					entity.setPosition_id(position_id);
					newResult.add(upperIndex++, entity);
				}
			} else {
				newResult.add(entity);
			}
		}
		return newResult;
	}

	/**
	 * 取得修理品已使用消耗品一览
	 * 
	 * @param form
	 * @param user
	 * @param conn
	 * @return
	 */
	public List<MaterialPartialDetailForm> getConsumableReceptOfMaterial(
			String material_id, String line_id, SqlSession conn) {
		MaterialConsumableDetailMapper mapper = conn.getMapper(MaterialConsumableDetailMapper.class);

		List<MaterialPartialDetailEntity> list = mapper.searchForMaterialWithLine(material_id, line_id);

		List<MaterialPartialDetailForm> formList = new ArrayList<MaterialPartialDetailForm>();
		BeanUtil.copyToFormList(list, formList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialPartialDetailForm.class);
		return formList;
	}

	public List<MaterialPartialDetailEntity> getConsumableReceptOfMaterialForPosition(
			ActionForm form, LoginData user, ProductionFeatureEntity workingPf, SqlSession conn) {
		MaterialConsumableDetailMapper mapper = conn.getMapper(MaterialConsumableDetailMapper.class);
		MaterialPartialDetailEntity mpdCond = new MaterialPartialDetailEntity();
		mpdCond.setPosition_id(user.getPosition_id());

		// 当前作业
		PositionPanelService posPService = new PositionPanelService();
		if (workingPf == null) workingPf = posPService.getProcessingPf(user, conn);
		mpdCond.setMaterial_id(workingPf.getMaterial_id());
		mpdCond.setOccur_times(workingPf.getRework());

		List<MaterialPartialDetailEntity> list = mapper.search(mpdCond);
		for (MaterialPartialDetailEntity entity : list) {
			if (!entity.getR_operator_id().equals(user.getOperator_id())) {
				entity.setBelongs(-1); // 他人在本工位使用数量
			}
			entity.setMaterial_id(null);
			entity.setPosition_id(null);
			entity.setR_operator_id(null);
		}

		return list;
	}

	/**
	 * 提交消耗品使用一览
	 * 
	 * @param req
	 * @param user
	 * @param conn
	 */
	public void commitAssemble(HttpServletRequest request, LoginData user, Integer rework,
			SqlSessionManager conn) throws Exception {
		List<MaterialPartialDetailEntity> entities = new AutofillArrayList<MaterialPartialDetailEntity>(MaterialPartialDetailEntity.class);
		Pattern p = Pattern.compile("(\\w+)\\[(\\d+)\\].(\\w+)");

		String material_id = request.getParameter("material_id");

		Map<String, String[]> parameterMap = request.getParameterMap();
		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("assemble".equals(entity)) {
					String column = m.group(3);
					int icounts = Integer.parseInt(m.group(2));
					String[] value = parameterMap.get(parameterKey);

					switch(column) {
					case "partial_id" : entities.get(icounts).setPartial_id(value[0]); break;
					case "recept_quantity" : entities.get(icounts).setRecept_quantity(Integer.parseInt(value[0])); break;
					}
				}
			}
		}

		MaterialConsumableDetailMapper mcdMapper = conn.getMapper(MaterialConsumableDetailMapper.class);
		for (MaterialPartialDetailEntity entity : entities) {
			entity.setMaterial_id(material_id);
			entity.setPosition_id(user.getPosition_id());
			entity.setR_operator_id(user.getOperator_id());
			entity.setOccur_times(rework);

			List<MaterialPartialDetailEntity> orgEntity = mcdMapper.search(entity);
			if (orgEntity == null || orgEntity.size() == 0) {
				if (entity.getRecept_quantity() > 0) {
					mcdMapper.insert(entity);
				}
			} else {
				if (entity.getRecept_quantity() == 0) {
					mcdMapper.remove(entity);
				} else if (entity.getRecept_quantity() != orgEntity.get(0).getRecept_quantity()) {
					mcdMapper.updateQuantity(entity);
				}
			}
		}
	}
}

package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.inline.MaterialProcessEntity;
import com.osh.rvs.form.inline.MaterialProcessForm;
import com.osh.rvs.mapper.inline.MaterialProcessMapper;

import framework.huiqing.common.util.copy.BeanUtil;

public class MaterialProcessService {

	public static final int PX_A = 0;
	private static final int PX_B = 1;
	public static final int PX_B_OF_1 = 4;
	public static final int PX_C = 2;
	public static final int PX_B_OF_2 = 7;

	public MaterialProcessForm loadMaterialProcess(SqlSession conn, String id) {

		MaterialProcessForm form = null;

		MaterialProcessMapper dao = conn.getMapper(MaterialProcessMapper.class);
		MaterialProcessEntity entity = dao.loadMaterialProcess(id);

		if (entity != null) {
			form = new MaterialProcessForm();
			BeanUtil.copyToForm(entity, form, null);
		}

		return form;
	}

	public void updateMaterialProcess(ActionForm form, SqlSession conn) throws Exception {
		MaterialProcessEntity conditionBean = new MaterialProcessEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		MaterialProcessMapper dao = conn.getMapper(MaterialProcessMapper.class);

		if (conditionBean.getDec_finish_date() != null || conditionBean.getDec_plan_date() != null) {
			conditionBean.setFinish_date(conditionBean.getDec_finish_date());
			conditionBean.setScheduled_date(conditionBean.getDec_plan_date());
			conditionBean.setLine_id("00000000012");
			dao.updateMaterialProcess(conditionBean);
		}

		if (conditionBean.getNs_finish_date() != null || conditionBean.getNs_plan_date() != null) {
			conditionBean.setFinish_date(conditionBean.getNs_finish_date());
			conditionBean.setScheduled_date(conditionBean.getNs_plan_date());
			conditionBean.setLine_id("00000000013");
			dao.updateMaterialProcess(conditionBean);
		}

		if (conditionBean.getCom_finish_date() != null || conditionBean.getCom_plan_date() != null) {
			conditionBean.setFinish_date(conditionBean.getCom_finish_date());
			conditionBean.setScheduled_date(conditionBean.getCom_plan_date());
			conditionBean.setLine_id("00000000014");
			dao.updateMaterialProcess(conditionBean);
		}
	}

	/**
	 * 已完成的工程完成时间取消
	 * @param material_id 维修对象ID
	 * @param line_id 工程ID
	 * @param conn
	 */
	public void undoLineComplete(String material_id, String line_id, SqlSession conn) throws Exception {
		MaterialProcessMapper mapper = conn.getMapper(MaterialProcessMapper.class);
		MaterialProcessEntity entity = new MaterialProcessEntity();
		entity.setMaterial_id(material_id);
		entity.setLine_id(line_id);
		mapper.undoLineComplete(entity);
	}

	/**
	 * 未修理返还后,还未结束的工程工作情况删除
	 * @param material_id 维修对象ID
	 * @param conn
	 */
	public void removeByBreak(String material_id, SqlSessionManager conn) throws Exception {
		MaterialProcessMapper mapper = conn.getMapper(MaterialProcessMapper.class);
		mapper.removeByBreak(material_id);
	}

	/**
	 * 结束工程
	 * @param material_id 维修对象ID
	 * @param line_id 工程ID
	 * @param triggerList
	 * @param conn
	 */
	public void finishMaterialProcess(String material_id, String line_id, List<String> triggerList, SqlSessionManager conn) throws Exception {
		MaterialProcessMapper dao = conn.getMapper(MaterialProcessMapper.class);
		MaterialProcessEntity materialProcess = new MaterialProcessEntity();
		materialProcess.setMaterial_id(material_id);
		materialProcess.setLine_id(line_id);
		dao.finishMaterialProcess(materialProcess);

//		// 停止计时计算，总组全部停止
//		if ("00000000014".equals(line_id)) {
//			line_id = "any";
//		}
//		triggerList.add("http://localhost:8080/rvspush/trigger/delete_finish_time/"
//				+ material_id + "/" + line_id);
	}

	/**
	 * 更新出货安排日（投线前也可设定）
	 * @param mpEntity
	 * @param conn
	 * @throws Exception
	 */
	public void updateScheduleAssignDate(MaterialProcessEntity mpEntity,
			SqlSessionManager conn) throws Exception {
		MaterialProcessMapper mapper = conn.getMapper(MaterialProcessMapper.class);
		// 确定有没有
		MaterialProcessEntity ret = mapper.loadMaterialProcessOfLine(mpEntity.getMaterial_id(), mpEntity.getLine_id());

		if (ret == null) {
			mapper.insertMaterialProcessAssign(mpEntity);
		} else {
			// 更新出货安排日
			mapper.updateMaterialProcess(mpEntity);
		}

	}

	public void pxExchange(String material_id, String line_id, SqlSessionManager conn) {
		MaterialProcessMapper mapper = conn.getMapper(MaterialProcessMapper.class);
		mapper.updatePx(material_id, line_id);
	}

	/**
	 *  取得分线位置
	 * @param modelId 型号 ID
	 * @param midLightFix 中小修
	 * @param conn
	 * @return
	 */
	public int evalPx(String modelId, String lineId, boolean midLightFix,
			SqlSession conn) {
		ModelService mdlService = new ModelService();
		boolean isImbalance = mdlService.checkImbalance(modelId, lineId, conn);
		String kind = mdlService.getKind(modelId, conn);

		boolean comLine = "00000000014".equals(lineId);
		if (midLightFix) {
			if (comLine) {
				// 小修理总组默认C线
				return PX_C;
			} else {
				// 小修理默认B线
				return PX_B;
			}
//			// 小修理默认B线
//			if ("03".equals(kind) && comLine) { // 纤维镜总组
//				return PX_C;
//			} else {
//				if (comLine) {
//					if (("02".equals(kind) || "04".equals(kind)) && comLine) { // 细镜总组
//						return PX_B_OF_2;
//					}
//					return PX_B_OF_1;
//				}
//				return PX_B;
//			}
		} else {
			// 大修理
			if (isImbalance) {
				if ("03".equals(kind) && comLine) { // 纤维镜总组
					return PX_C;
				} else {
					if (comLine) {
						if (("02".equals(kind) || "04".equals(kind)) && comLine) { // 细镜总组
							return PX_B_OF_2;
						}
						return PX_B_OF_1;
					}
					return PX_B;
				}
			} else {
				if ("03".equals(kind) && comLine) { // 纤维镜总组
					return PX_C;
				} else {
					return PX_A;
				}
			}
		}
	}

	public MaterialProcessEntity loadMaterialProcessOfLine(String material_id,
			String line_id, SqlSession conn) {
		MaterialProcessMapper mapper = conn.getMapper(MaterialProcessMapper.class);
		return mapper.loadMaterialProcessOfLine(material_id, line_id);
	}

	public List<String> loadMaterialProcessLineIds(String id, SqlSession conn) {

		List<String> res = new ArrayList<String>();

		MaterialProcessMapper dao = conn.getMapper(MaterialProcessMapper.class);
		List<MaterialProcessEntity> entities = dao.loadMaterialProcessLines(id);

		for (MaterialProcessEntity entity : entities) {
			res.add(entity.getLine_id());
		}

		return res;
	}

	public void insertMaterialProcess(MaterialProcessEntity insertBean, SqlSessionManager conn) throws Exception {
		MaterialProcessMapper mapper = conn.getMapper(MaterialProcessMapper.class);
		mapper.insertMaterialProcess(insertBean);
	}
}

package com.osh.rvs.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.PauseFeatureEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.mapper.inline.PauseFeatureMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PauseFeatureService {

	Logger logger = Logger.getLogger("PauseFeatureService");

	public void createPauseFeature(ProductionFeatureEntity productionFeature, String reason, String comments, String alarm_messsage_id,
			SqlSessionManager conn) throws Exception {
		createPauseFeature(productionFeature, reason, comments, alarm_messsage_id, conn, null);
	}
	public void createPauseFeature(ProductionFeatureEntity productionFeature, String reason, String comments, String alarm_messsage_id,
			SqlSessionManager conn, String snout_serial_no) throws Exception {
		PauseFeatureEntity entity = new PauseFeatureEntity();

		// 担当人 ID
		entity.setOperator_id(productionFeature.getOperator_id());
		// 暂停原因
		try {
			entity.setReason(Integer.parseInt(reason.trim()));
		} catch (NumberFormatException ne) {
			logger.error("暂停理由代码不是数字：" + ne.getMessage());
			entity.setReason(40); // 其他
		}
		// 维修对象ID
		entity.setMaterial_id(productionFeature.getMaterial_id());
		// 课室ID
		entity.setSection_id(productionFeature.getSection_id());
		// 工位ID
		entity.setPosition_id(productionFeature.getPosition_id());
		// 注释
		if (comments.length() > 100) comments = comments.substring(0, 100);
		entity.setComments(comments);
		// 警报信息ID
		entity.setAlarm_messsage_id(alarm_messsage_id);
		// 序列号（先端头用）
		entity.setSnout_serial_no(snout_serial_no);

		PauseFeatureMapper dao = conn.getMapper(PauseFeatureMapper.class);

		dao.makePauseFeature(entity);
	}

	public void createPauseFeature(SoloProductionFeatureEntity productionFeature, String reason, String comments, String section_id,
			String alarm_messsage_id, SqlSessionManager conn) throws Exception {
		PauseFeatureEntity entity = new PauseFeatureEntity();

		// 担当人 ID
		entity.setOperator_id(productionFeature.getOperator_id());
		// 暂停原因
		try {
			entity.setReason(Integer.parseInt(reason.trim()));
		} catch (NumberFormatException ne) {
			logger.error("暂停理由代码不是数字：" + ne.getMessage());
			entity.setReason(40); // 其他
		}
		// 维修对象ID
		entity.setSnout_serial_no(productionFeature.getSerial_no());
		// 课室ID
		entity.setSection_id(section_id);
		// 工位ID
		entity.setPosition_id(productionFeature.getPosition_id());
		// 注释
		entity.setComments(comments);
		// 警报信息ID
		entity.setAlarm_messsage_id(alarm_messsage_id);

		PauseFeatureMapper dao = conn.getMapper(PauseFeatureMapper.class);

		dao.makePauseFeature(entity);
	}

	public void finishPauseFeature(String finish_operator_id, SqlSessionManager conn) throws Exception {
		finishPauseFeature(null, null, null, finish_operator_id, null, conn);
	}
	public void finishPauseFeature(String material_id, String section_id, String position_id, String finish_operator_id,
			SqlSessionManager conn) throws Exception {
		finishPauseFeature(material_id, section_id, position_id, finish_operator_id, null, conn);
	}
	public void finishPauseFeature(String material_id, String section_id, String position_id, String finish_operator_id,
			String snout_serial_no, SqlSessionManager conn) throws Exception {
		PauseFeatureMapper dao = conn.getMapper(PauseFeatureMapper.class);

		// 其他普通的暂停结束掉/个人用
		PauseFeatureEntity hitPause = dao.checkOperatorPauseFeature(finish_operator_id);
		if (hitPause != null) {
			hitPause.setFinisher_id(finish_operator_id);
			dao.finishPauseFeature(hitPause);
		}

		if (material_id != null) {
			// 工序必要的暂停结束掉/维修对象用
			List<PauseFeatureEntity> hitList = dao.checkPauseFeature(material_id, section_id, position_id);
			if (hitList != null) {
				for (PauseFeatureEntity hitBreak : hitList) {
					hitBreak.setFinisher_id(finish_operator_id);
					dao.finishPauseFeature(hitBreak);
				}
			}
		}
		if (snout_serial_no != null) {
			// 工序必要的暂停结束掉/先端预制用
			dao.stopPauseFeatureSnout(snout_serial_no, section_id, position_id, finish_operator_id);
		}
	}

	/**
	 * 暂停理由列表
	 */
	private static String pauseReasonSelectOptions = null;
	private static Map<String, String> pauseReasonSelectOptionMap = new HashMap<String, String>();
	private static Map<String, String> pauseReasonSelectCommentMap = new HashMap<String, String>();
	private static Map<String, Map<String, String>> pauseReasonIndirectGroupMap = new HashMap<String, Map<String, String>>();

	/**
	 * 取得暂停理由选项
	 * @return
	 */
	public static String getPauseReasonSelectOptions() {
		Map<String, String> optGroups = new LinkedHashMap<String, String>();

//		Map<String, String> pause_reasons = CodeListUtils.getList("pause_reason");
		if (pauseReasonSelectOptions != null) {
			return pauseReasonSelectOptions;
		} else {
			pauseReasonSelectOptions = "";
			pauseReasonSelectOptionMap.clear();
			pauseReasonSelectCommentMap.clear();
			pauseReasonIndirectGroupMap.clear();
		}

		for (int pause_code = 101; pause_code < 300; pause_code++) {
			String pause_reason = PathConsts.POSITION_SETTINGS.getProperty("workRecord.reason." + pause_code);
			if (pause_reason == null) {
				continue;
			}
			boolean forDirect = true, forIndirect = true;
			if (pause_reason.indexOf('<') >= 0) {
				String[] sPpart = pause_reason.split("<");
				pause_reason = sPpart[0];
				forDirect =  (sPpart[1].indexOf('Z') >= 0);
				forIndirect =  (sPpart[1].indexOf('J') >= 0);
			}

			// WDT 直接辅助作业 WY 间接作业 MD 管理时间 MW 等待时间 H 休息离线
			String kind = pause_reason.replaceAll("(\\d)+:.*", "");

			if (forDirect) {
				pauseReasonSelectOptionMap.put("" + pause_code, pause_reason);

				pauseReasonSelectCommentMap.put("" + pause_code, getComments(pause_reason));

				if (!optGroups.containsKey(kind)) {
					optGroups.put(kind, "<optgroup label=\"" + declare(kind) + "\">");
				}
				optGroups.put(kind, optGroups.get(kind) + "<option value=\"" + pause_code + "\">" + pause_reason + "</option>");
			}
			if (forIndirect) {
				if (!pauseReasonIndirectGroupMap.containsKey(kind)) {
					pauseReasonIndirectGroupMap.put(kind, new TreeMap<String, String>());
				}
				pauseReasonIndirectGroupMap.get(kind).put("" + pause_code, pause_reason);
			}
		}

		for (String kind : optGroups.keySet()) {
			if ("其".equals(kind)) {
				pauseReasonSelectOptions = optGroups.get(kind) + "</optgroup>" + pauseReasonSelectOptions;
			} else {
				pauseReasonSelectOptions += optGroups.get(kind) + "</optgroup>";
			}
		}

		return pauseReasonSelectOptions;
	}

	public static String getPauseReasonSelectComments() {
		if (pauseReasonSelectOptionMap.isEmpty()) {
			getPauseReasonSelectOptions();
		}
		return JSON.encode(pauseReasonSelectCommentMap);
	}

	public static Map<String, Map<String, String>> getPauseReasonIndirectGroupMap() {
		if (pauseReasonIndirectGroupMap.isEmpty()) {
			getPauseReasonSelectOptions();
		}
		return pauseReasonIndirectGroupMap;
	}

	private static String getComments(String pauseReason) {
		String[] dv = pauseReason.split(":");
		if (dv.length == 0) return null;
		String code = dv[0];
		String pauseComments = PathConsts.POSITION_SETTINGS.getProperty("workRecord.comments." + code);
		return pauseComments;
	}

	/**
	 * 取得暂停理由列表
	 * @return
	 */
	public static Map<String, String> getPauseReason() {
		if (pauseReasonSelectOptionMap.isEmpty()) {
			getPauseReasonSelectOptions();
		}
		return pauseReasonSelectOptionMap;
	}
	public static void resetPauseReason() {
		pauseReasonSelectOptions = null;
		pauseReasonSelectOptionMap.clear();
		pauseReasonSelectCommentMap.clear();
		pauseReasonIndirectGroupMap.clear();

		getPauseReasonSelectOptions();
	}
	public static String getPauseReasonByCode(String reasonCode) {
		if (pauseReasonSelectOptionMap.isEmpty()) {
			getPauseReasonSelectOptions();
		}
		String rt = PauseFeatureService.getPauseReason().get(reasonCode);
		if (CommonStringUtil.isEmpty(rt)) {
			Map<String, Map<String, String>> prigm = PauseFeatureService.getPauseReasonIndirectGroupMap();
			for (String prigk : prigm.keySet()) {
				if (prigm.get(prigk).containsKey(reasonCode)) {
					rt = prigm.get(prigk).get(reasonCode);
					break;
				}
			}

			if (CommonStringUtil.isEmpty(rt)) {
				return CodeListUtils.getValue("pause_reason", reasonCode);
			} else {
				return rt;
			}
		} else {
			return rt;
		}
	}

	/**
	 * 暂停信息分类
	 * @param kind
	 * @return
	 */
	private static String declare(String kind) {
		if ("其".equals(kind)) {
			return "(无分类)";
		} else if ("WDT".equals(kind)) {
			return "WDT 直接辅助作业";
		} else if ("WY".equals(kind)) {
			return "WY 间接作业";
		} else if ("MD".equals(kind)) {
			return "MD 管理时间";
		} else if ("MW".equals(kind)) {
			return "MW 等待时间";
		} else if ("H".equals(kind)) {
			return "H 休息离线";
		}
		return null;
	}

	public String checkPauseForm(String comments, List<MsgInfo> errors) {
		if (comments != null && comments.length() > 100) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("comments");
			error.setErrcode("validator.invalidParam.invalidMaxLengthValue");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage
					("validator.invalidParam.invalidMaxLengthValue", "备注", "100"));
			errors.add(error);
		}
		return comments;
	}
}

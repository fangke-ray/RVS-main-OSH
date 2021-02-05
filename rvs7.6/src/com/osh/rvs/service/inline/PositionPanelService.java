package com.osh.rvs.service.inline;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.infect.CheckResultEntity;
import com.osh.rvs.bean.infect.CheckUnqualifiedRecordEntity;
import com.osh.rvs.bean.infect.PeriodsEntity;
import com.osh.rvs.bean.infect.PeripheralInfectDeviceEntity;
import com.osh.rvs.bean.inline.DryingProcessEntity;
import com.osh.rvs.bean.inline.ForSolutionAreaEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.inline.WaitingEntity;
import com.osh.rvs.bean.master.DevicesManageEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.PositionGroupEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.PcsUtils;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.infect.CheckResultMapper;
import com.osh.rvs.mapper.infect.CheckUnqualifiedRecordMapper;
import com.osh.rvs.mapper.infect.PeripheralInfectDeviceMapper;
import com.osh.rvs.mapper.inline.AbnormalWorkStateMapper;
import com.osh.rvs.mapper.inline.ComposeStorageMapper;
import com.osh.rvs.mapper.inline.PositionPanelMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.inline.SoloProductionFeatureMapper;
import com.osh.rvs.mapper.master.DevicesManageMapper;
import com.osh.rvs.mapper.master.ProcessAssignMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.service.CheckResultService;
import com.osh.rvs.service.DevicesTypeService;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.partial.ComponentManageService;
import com.osh.rvs.service.partial.ComponentSettingService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.JustlengthValidator;
import framework.huiqing.common.util.validator.LongTypeValidator;

public class PositionPanelService {
	protected static final Logger _log = Logger.getLogger("Production");

	/**
	 * 取得工位当前基本信息
	 * @param section_id
	 * @param position_id
	 * @param level
	 * @param conn
	 * @return
	 */
	public Map<String, Integer> getPositionMap(String section_id, String position_id, String px, SqlSession conn) {
		List<String> position_ids = new ArrayList<String>();
		position_ids.add(position_id);
		return getPositionMap(section_id, position_ids, px, conn);
	}
	public Map<String, Integer> getPositionMap(String section_id, List<String> position_ids, String level, SqlSession conn) {
		Map<String, Integer> retMap = new HashMap<String, Integer>();

		/// 从数据库中查询记录
		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);

		int runCost = 0;
		int operatorCost = 0;
		int finishCount = 0;
		int supportCount = 0;

		int countPause = 0;
		int countBreak = 0;

		for (String position_id : position_ids) {
			// 取得今日作业时间
			runCost += dao.checkPositionStartedWorkTime(section_id, position_id, level) / 60;

			// 取得今日操作时间
			operatorCost += dao.checkTodayWorkCost(section_id, position_id, level) / 60;

			// 取得处理件数
			finishCount += dao.getFinishCount(section_id, position_id, level);

			// 取得代工件数</td>
			supportCount += dao.getLeaderSupportFinishCount(section_id, position_id, level);

			// 取得暂停次数
			// 取得中断次数
			List<Map<String, Number>> breakcounts = dao.getTodayBreak(section_id, position_id, level);
			if (breakcounts != null) {
				for (Map<String, Number> breakcount : breakcounts) {
					int reasonCode = breakcount.get("reason").intValue();
					if ((reasonCode >= 49 && reasonCode < 70) || reasonCode >= 100) // 暂停
						countPause += breakcount.get("count_reason").intValue();
					else if (reasonCode < 20) // 中断
						countBreak += breakcount.get("count_reason").intValue();
					// 作业流程不算
				}
			}
		}

		retMap.put("run_cost", runCost);

		// 取得今日操作时间
		retMap.put("operator_cost", operatorCost);

		// 取得处理件数
		retMap.put("finish_count", finishCount);

		// 取得代工件数</td>
		retMap.put("support_count", supportCount);
		retMap.put("pause_count", countPause);
		retMap.put("break_count", countBreak);

//		retMap.put("waiting_count", getWaiting(section_id, position_id, level, conn).size());
		return retMap;
	}

	/**
	 * 取得等待维修品
	 * @param section_id
	 * @param position_id
	 * @param level
	 * @param conn
	 * @return
	 */
	public List<ProductionFeatureEntity> getWaiting(String section_id, String position_id, String level, SqlSession conn) {
		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		// 取得等待维修品
		List<ProductionFeatureEntity> waitings = dao.getWaiting(null, section_id, position_id, level);

		return waitings;
	}

	/**
	 * 判断是否所在工位的等待区中维修对象，如是则返回等待作业
	 * @param userPositionId 
	 * @param material_id
	 * @param session
	 * @param errors
	 */
	public List<WaitingEntity> checkWaitingMaterial(LoginData user, String userPositionId, SqlSession conn) {

		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);

		List<WaitingEntity> ret = null;
		if ("121".equals(user.getProcess_code()) || "131".equals(user.getProcess_code())) {
			ret = dao.getWaitingCdsMaterial(user.getSection_id(), userPositionId);
		} else {
			ret = dao.getWaitingMaterial(user.getLine_id(), user.getSection_id(), userPositionId, null, user.getOperator_id(), user.getPx(), "true");
		}

		return ret;
	}

	/**
	 * 判断是否所在工位的等待区中维修对象
	 * @param material_id
	 * @param session
	 * @param errors
	 */
	public List<ProductionFeatureEntity> isWaitingMaterial(String material_id, String section_id, String position_id,
			String level, SqlSession conn) {

		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		// 取得等待维修品
		List<ProductionFeatureEntity> waitings = dao.getWaiting(material_id, section_id, position_id, level);

		return waitings;
	}

	/**
	 * 检查扫描维修对象合法性
	 * @param material_id
	 * @param confirmed 
	 * @param user
	 * @param errors
	 * @param conn
	 * @return
	 */
	public ProductionFeatureEntity checkMaterialId(String material_id, String confirmed, LoginData user, List<MsgInfo> errors, SqlSession conn) {
		return checkMaterialId(material_id, confirmed, user, null, errors, conn);
	}
	public ProductionFeatureEntity checkMaterialId(String material_id, String confirmed, LoginData user, String resPositionId, List<MsgInfo> errors, SqlSession conn) {
		ProductionFeatureEntity retWaiting = null;

		Map<String, Object> parameters = new HashMap<String, Object>();
		int operate_result = 0;
		if (material_id.contains("_")) {
			String[] split = material_id.split("_");
			material_id = split[0];
			operate_result = Integer.parseInt(split[1]);
		}
		parameters.put("material_id", material_id);
		if (CommonStringUtil.isEmpty(material_id)) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("material_id");
			msgInfo.setErrcode("validator.required");
			msgInfo.setErrmsg("扫描失敗！");
			errors.add(msgInfo);
		}

		String message1 = new LongTypeValidator("扫描号码").validate(parameters, "material_id");
		if (message1 != null) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("material_id");
			msgInfo.setErrcode("validator.invalidParam.invalidIntegerValue");
			msgInfo.setErrmsg(message1);
			errors.add(msgInfo);
		}
		String message2 = new JustlengthValidator("扫描号码", 11).validate(parameters, "material_id");
		if (message2 != null) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("material_id");
			msgInfo.setErrcode("validator.invalidParam.invalidJustLengthValue");
			msgInfo.setErrmsg(message2);
			errors.add(msgInfo);
		}

		String userPositionId = user.getPosition_id();
		if (resPositionId != null) {
			userPositionId = resPositionId;
		}

		MsgInfo msgInfo;
		if (errors.size() == 0) {
			// 存在于等待区check

			// 在工位上等待的维修对象
			List<WaitingEntity> waitings = checkWaitingMaterial(user, userPositionId, conn);
			int count = waitings.size();
			if (count == 0) {
				// 等待区内没有维修对象
				msgInfo = new MsgInfo();
				msgInfo.setComponentid("material_id");
				msgInfo.setErrcode("info.linework.notInWaiting");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.notInWaiting"));
				errors.add(msgInfo);

				return retWaiting;
			} else {

				WaitingEntity scan = null;
				List<WaitingEntity> pExpedited = new ArrayList<WaitingEntity>();
				List<WaitingEntity> lExpedited = new ArrayList<WaitingEntity>();
				List<WaitingEntity> today = new ArrayList<WaitingEntity>();

				for (WaitingEntity waiting : waitings) {
					if (material_id.equals(waiting.getMaterial_id())) { // 是开始对象的话
						scan = waiting;
						// 在工位上等待的维修对象
						List<ProductionFeatureEntity> productionFeature = isWaitingMaterial(material_id,
								user.getSection_id(), userPositionId, user.getPx(), conn);
						count = productionFeature.size();
						if (count == 0) {
							// 维修对象不在用户所在等待区
							msgInfo = new MsgInfo();
							msgInfo.setComponentid("material_id");
							msgInfo.setErrcode("info.linework.notInWaiting");
							msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.notInWaiting"));
							errors.add(msgInfo);
						} else {
							// 如有则返回等待中的作业信息。
							if (count == 1) {
								retWaiting = productionFeature.get(0);
							} else {
								for (int i = 0; i < count; i++) {
									if (operate_result == productionFeature.get(i).getOperate_result()) {
										retWaiting = productionFeature.get(i);
									}
								}
							}

							if (errors.size() == 0) {
								ForSolutionAreaService fsaService = new ForSolutionAreaService();
								List<ForSolutionAreaEntity> blocks = fsaService.checkBlock(material_id, userPositionId, user.getLine_id(), conn);
								if (blocks != null && blocks.size() > 0) {
									ForSolutionAreaEntity block = blocks.get(0);
									String blockReason = CodeListUtils.getValue("offline_reason", ""+block.getReason());
									msgInfo = new MsgInfo();
									msgInfo.setComponentid("material_id");
									msgInfo.setErrcode("info.linework.notInWaiting");
									msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.blockedForSolve"
											, waiting.getSorc_no(), blockReason, block.getComment()));
									errors.add(msgInfo);
								}
							}
						}
					} else {
						if ("0".equals(waiting.getWaitingat()))
							// 限未处理的的需要优先项目
							if (waiting.getExpedited() != null && waiting.getExpedited() >= 10) { // 计划加急
								pExpedited.add(waiting);
							} else if (waiting.getExpedited() != null && waiting.getExpedited() > 0) { // 线长加急
								lExpedited.add(waiting);
							} else if (waiting.getToday() != null && waiting.getToday() == 1) { // 今日
								today.add(waiting);
							}
					}
				}

				if (scan == null) {
					// 维修对象不在用户所在等待区
					msgInfo = new MsgInfo();
					msgInfo.setComponentid("material_id");
					msgInfo.setErrcode("info.linework.notInWaiting");
					msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.notInWaiting"));
					errors.add(msgInfo);
				} else {
					String process_code = user.getProcess_code();
					if ("241".equals(process_code) || "252".equals(process_code) || "321".equals(process_code) || "400".equals(process_code)) { // 代线长工位不按次序
//					} else if (scan.getExpedited() >= 10) { // 本身是计划加急 
//					} else if (!"0".equals(scan.getWaitingat())) { // 中断的无关
//					} else if (scan.getExpedited() != null && scan.getExpedited() > 0) {
//						if (pExpedited.size() > 0) { // 本身是线长加急，等待区有计划加急
//							msgInfo = new MsgInfo();
//							msgInfo.setComponentid("material_id");
//							msgInfo.setErrcode("info.linework.expeditedFirst");
//							msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.expeditedFirst", pExpedited.get(0).getSorc_no()));
//							errors.add(msgInfo);
//						}
					} else if (scan.getToday() != null && scan.getToday() == 1) {
//						if (pExpedited.size() > 0) { // 本身是今日，等待区有加急
//							msgInfo = new MsgInfo();
//							msgInfo.setComponentid("material_id");
//							msgInfo.setErrcode("info.linework.expeditedFirst");
//							msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.expeditedFirst", pExpedited.get(0).getSorc_no()));
//							errors.add(msgInfo);
//						} else if (lExpedited.size() > 0) { // 本身是今日，等待区有加急
//							msgInfo = new MsgInfo();
//							msgInfo.setComponentid("material_id");
//							msgInfo.setErrcode("info.linework.expeditedFirst");
//							msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.expeditedFirst", lExpedited.get(0).getSorc_no()));
//							errors.add(msgInfo);
//						}
//					} else if (pExpedited.size() > 0) { // 本身不是加急，不是今日，等待区有加急
//						msgInfo = new MsgInfo();
//						msgInfo.setComponentid("material_id");
//						msgInfo.setErrcode("info.linework.expeditedFirst");
//						msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.expeditedFirst", pExpedited.get(0).getSorc_no()));
//						errors.add(msgInfo);
//					} else if (lExpedited.size() > 0) { // 本身不是加急，不是今日，等待区有加急
//						msgInfo = new MsgInfo();
//						msgInfo.setComponentid("material_id");
//						msgInfo.setErrcode("info.linework.expeditedFirst");
//						msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.expeditedFirst", lExpedited.get(0).getSorc_no()));
//						errors.add(msgInfo);
					} else if (today.size() > 0) { // 本身不是加急，不是今日，等待区有今日
//						msgInfo = new MsgInfo();
//						msgInfo.setComponentid("material_id");
//						msgInfo.setErrcode("info.linework.todayPlanFirst");
//						msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.todayPlanFirst", today.get(0).getSorc_no()));
//						errors.add(msgInfo);
					}
				}
			}
		}

		// 烘干信息
		if (errors.size()==0 && !"true".equals(confirmed) && retWaiting.getOperate_result() != 0) {
			DryingProcessService dpSevice = new DryingProcessService();
			DryingProcessEntity dpInfo = dpSevice.getDryingProcessByMaterialInPositionEntity(material_id, userPositionId ,conn);
			if (dpInfo != null) {
				Date startTime = dpInfo.getStart_time();

				msgInfo = new MsgInfo();
				msgInfo.setComponentid("material_id");
				msgInfo.setErrcode("info.dryingJob.finishDryingProcess");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.dryingJob.finishDryingProcess"
						, dpInfo.getContent()
						, (new Date().getTime() - startTime.getTime()) / (1000 * 60) + ""));
				errors.add(msgInfo);
			}
		}

		return retWaiting;
	}

	/**
	 * 检查再开维修对象合法性
	 * @param material_id
	 * @param user
	 * @param errors
	 * @param conn
	 * @return
	 */
	public ProductionFeatureEntity checkPausingMaterialId(String material_id, LoginData user, List<MsgInfo> errors, SqlSession conn) {
		ProductionFeatureEntity retPausing = null;

		if (CommonStringUtil.isEmpty(material_id)) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("material_id");
			msgInfo.setErrcode("info.linework.notInWaiting");
			msgInfo.setErrmsg("你要再开作业的维修对象已不存在于您的工作区。");
			errors.add(msgInfo);
		}

		if (errors.size() == 0) {
			// 取得用户信息

			PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
			// 在工位上暂停处理的维修对象
			retPausing = dao.getPausing(user.getOperator_id());

			if (retPausing == null) {
				// 维修对象不在用户所在等待区
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("material_id");
				msgInfo.setErrcode("info.linework.notInWaiting");
				msgInfo.setErrmsg("你要再开作业的维修对象已不存在于您的工作区。");
				errors.add(msgInfo);
			}
		}
		return retPausing;
	}

	public SoloProductionFeatureEntity checkPausingSerialNo(String serial_no, LoginData user, List<MsgInfo> errors,
			SqlSession conn) {
		SoloProductionFeatureEntity retPausing = null;

		if (CommonStringUtil.isEmpty(serial_no)) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("serial_no");
			msgInfo.setErrcode("info.linework.notInWaiting");
			msgInfo.setErrmsg("你要再开作业的维修对象已不存在于您的工作区。");
			errors.add(msgInfo);
		}

		if (errors.size() == 0) {
			// 取得用户信息

			SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
			// 在工位上暂停处理的维修对象
			retPausing = dao.getPausing(user.getOperator_id());

			if (retPausing == null) {
				// 维修对象不在用户所在等待区
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("serial_no");
				msgInfo.setErrcode("info.linework.notInWaiting");
				msgInfo.setErrmsg("你要再开作业的维修对象已不存在于您的工作区。");
				errors.add(msgInfo);
			}
		}
		return retPausing;
	}

	/**
	 * 取得维修对象基本信息表单
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public MaterialForm getMaterialInfo(String material_id, SqlSession conn) {
		MaterialForm materialForm = new MaterialForm();
		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		MaterialEntity materialEntity = dao.getMaterialDetail(material_id);
		BeanUtil.copyToForm(materialEntity, materialForm, CopyOptions.COPYOPTIONS_NOEMPTY);

		return materialForm;
	}

	/**
	 * 取得维修对象工作信息
	 * @param material_id
	 * @param user
	 * @param conn
	 * @return
	 */
	public List<ProductionFeatureEntity> getPositionWorksByMaterial(String material_id, LoginData user,
			SqlSession conn) {
		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		// 取得用户信息
		String position_id = user.getPosition_id();

		return dao.getPositionWorksByMaterial(material_id, position_id);
	}

	/**
	 * 取得维修对象在指定工位(单次返工中)总使用时间 
	 * @param waitingPf
	 * @param conn
	 * @return
	 */
	public Integer getTotalTimeByRework(ProductionFeatureEntity waitingPf, SqlSession conn) {
		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		Integer totalTimeByRework = dao.getTotalTimeByRework(waitingPf);
		if (totalTimeByRework == null) {
			return 0;
		} else {
			return totalTimeByRework;
		}
	}

	/**
	 * 取得等待区信息一览
	 * @param waitingPf
	 * @param conn
	 * @return
	 */
	public List<WaitingEntity> getWaitingMaterial(String section_id, String position_id, String line_id,
			String operator_id, String pxLevel, String process_code, SqlSession conn) {
		PositionPanelMapper mapper = conn.getMapper(PositionPanelMapper.class);

		List<WaitingEntity> ret = null;
		if ("121".equals(process_code) || "131".equals(process_code)) {
			ret = mapper.getWaitingCdsMaterial(section_id, position_id);
		} else {
			ret = mapper.getWaitingMaterial(line_id, section_id, position_id, null, operator_id, pxLevel, null);
		}

		signWaitingEntity(ret, position_id, process_code, null, null, conn);

		return ret;
	}
	public List<WaitingEntity> getGroupWaitingMaterial(String section_id,
			String group_position_id, String line_id, String operator_id,
			String pxLevel, List<WaitingEntity> completes, SqlSession conn) {
		PositionPanelMapper mapper = conn.getMapper(PositionPanelMapper.class);

		List<WaitingEntity> ret = mapper.getWaitingMaterial(line_id, section_id, null, group_position_id, operator_id, pxLevel, null);

		signWaitingEntity(ret, null, null, group_position_id, completes, conn);

		// 组仕挂量提前
		List<WaitingEntity> retUp = new ArrayList<WaitingEntity>();
		List<WaitingEntity> retDown = new ArrayList<WaitingEntity>();
		for(WaitingEntity waiting : ret) {
			if (waiting.getImbalance() != null && waiting.getImbalance() == 2) {
				retUp.add(waiting);
			} else {
				retDown.add(waiting);
			}
		}
		retUp.addAll(retDown);

		return retUp;
	}
	private void signWaitingEntity(List<WaitingEntity> ret, String singlePositionId, String singleProcessCode, String groupPositionId,
			List<WaitingEntity> completes, SqlSession conn) {
		for (WaitingEntity we : ret) {
			String position_id = singlePositionId;
			if (position_id == null) position_id = we.getPosition_id();

			String process_code = singleProcessCode;
			if (process_code == null) process_code = we.getProcess_code();

			if ("0".equals(we.getWaitingat())) we.setWaitingat("未处理");
			else if ("4".equals(we.getWaitingat())) { // 暂停
				// 工位特殊暂停理由
				if (we.getPause_reason() != null && we.getPause_reason() >= 70) {
					DryingProcessService dpService = new DryingProcessService();

					if (we.getPause_reason() == 99) {
						we.setWaitingat("烘干作业");
						we.setDrying_process(
								dpService.getDryingProcessByMaterialInPosition(we.getMaterial_id(), position_id, conn));
					} else {
						String sReason = PathConsts.POSITION_SETTINGS.getProperty("step." + process_code + "." + we.getPause_reason());
						if (sReason == null) {
							we.setWaitingat("正常中断流程");
						} else {
							we.setWaitingat(sReason);
						}
					}
				} else {
					we.setWaitingat("中断恢复");
				}
			}
			else if ("3".equals(we.getWaitingat())) we.setWaitingat("中断等待再开");
			else if ("7".equals(we.getWaitingat())) we.setWaitingat("待投入");
			else if ("5".equals(we.getWaitingat())) we.setWaitingat("通箱");
		}

		// 根据后工程的仕挂量数提升优先度
		if (groupPositionId != null) {
			List<PositionGroupEntity> subs = PositionService.getGroupPositionSubs(conn).get(groupPositionId);
			Map<String, Integer> nextCounts = new HashMap<String, Integer>();
			Map<String, String> prevSiblings = new HashMap<String, String>();
			for (PositionGroupEntity sub : subs) {
				if (sub.getControl_trigger() != null) {
					nextCounts.put(sub.getNext_position_id(), sub.getControl_trigger());
					prevSiblings.put(sub.getSub_position_id(), sub.getNext_position_id());
				}
			}
			for (WaitingEntity complete : completes) {
				String nextPositionId = complete.getPosition_id();
				if (nextCounts.containsKey(nextPositionId)) {
					nextCounts.put(nextPositionId, nextCounts.get(nextPositionId) - 1);
				}
			}
			for (WaitingEntity we : ret) {
				String prevPositionId = we.getPosition_id();
				if (prevSiblings.containsKey(prevPositionId)) {
					String nextPositionId = prevSiblings.get(prevPositionId);
					if (nextCounts.containsKey(nextPositionId)) {
						if (nextCounts.get(nextPositionId) > 0) {
							we.setImbalance(2);
							nextCounts.put(nextPositionId, nextCounts.get(nextPositionId) - 1);
						}
					}
				}
			}
		}
	}

	public List<WaitingEntity> getGroupCompleteMaterial(String section_id,
			String group_position_id, String px, SqlSession conn) {
		PositionPanelMapper mapper = conn.getMapper(PositionPanelMapper.class);
		List<WaitingEntity> ret = mapper.getGroupCompleteMaterial(section_id, group_position_id, px);

		return ret;
	}

	/**
	 * 得到当前用户处理中维修品
	 * @param user
	 * @param conn
	 * @return
	 */
	public ProductionFeatureEntity getWorkingPf(LoginData user, SqlSession conn) {

		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		// 取得作业维修品
		ProductionFeatureEntity working = dao.getWorking(user.getOperator_id());

		return working;
	}

	/**
	 * 得到当前用户处理中或暂停中维修品
	 * @param user
	 * @param conn
	 * @return
	 */
	public ProductionFeatureEntity getProcessingPf(LoginData user, SqlSession conn) {
		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		// 取得作业/暂停维修品
		ProductionFeatureEntity working = dao.getProcessing(user.getOperator_id());

		return working;
	}

	/**
	 * 得到当前用户处理中或辅助中维修品
	 * @param user
	 * @param conn
	 * @return
	 */
	public ProductionFeatureEntity getWorkingOrSupportingPf(LoginData user, SqlSession conn) {
		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		// 取得辅助维修品
		ProductionFeatureEntity working = dao.getSupporting(user.getOperator_id());

		return working;
	}

	/**
	 * 得到当前用户暂停中维修品
	 * @param user
	 * @param conn
	 * @return
	 */
	public ProductionFeatureEntity getPausingPf(LoginData user, SqlSession conn) {

		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		// 取得等待维修品
		ProductionFeatureEntity pausing = dao.getPausing(user.getOperator_id());

		return pausing;
	}

	/**
	 * 得到当前用户处理中
	 * @param user
	 * @param errors
	 * @param conn
	 * @return
	 */
	public List<ProductionFeatureEntity> getWorkingPfs(LoginData user, SqlSession conn) {

		PositionPanelMapper dao = conn.getMapper(PositionPanelMapper.class);
		// 取得等待维修品
		List<ProductionFeatureEntity> workings = dao.getWorkingBatch(user.getPosition_id(), user.getOperator_id());

		return workings;
	}

	/**
	 * 取得工程检查票
	 * @param listResponse
	 * @param pf
	 * @param user
	 * @param conn
	 */
	public static void getPcses(Map<String, Object> listResponse, ProductionFeatureEntity pf, String sline_id,
			SqlSession conn) {
		getPcses(listResponse, pf, sline_id, false, conn);
	}
	public static void getPcses(Map<String, Object> listResponse, ProductionFeatureEntity pf, String sline_id,
			boolean isLeader, SqlSession conn) {
		String material_id = pf.getMaterial_id();
		MaterialForm mform = (MaterialForm) listResponse.get("mform");

		List<Map<String, String>> pcses = new ArrayList<Map<String, String>>();

		boolean enterCom = false;
		String[] showLines = {};
		if (mform.getLevel()==null) {
			showLines = new String[2];
			showLines[0] = "检查卡"; // 181 用
			showLines[1] = "NS 工程"; // 302 预先CCD盖玻璃时用
		} else if (mform.getLevel().startsWith("5")) {
			showLines = new String[1];
			showLines[0] = "检查卡";
		} else {
			if ("00000000012".equals(sline_id)) {
				if (MaterialTagService.getAnmlMaterials(conn).contains(material_id)) {
					showLines = new String[3];
					showLines[0] = "分解工程";
					showLines[1] = "NS 工程";
					showLines[2] = "总组工程";
				} else {
					showLines = new String[2];
					showLines[0] = "分解工程";
					showLines[1] = "总组工程"; // TODO 判断有无总组返工
				}
			} else if ("00000000013".equals(sline_id)) {
				showLines = new String[1];
				showLines[0] = "NS 工程";
			} else if ("00000000014".equals(sline_id)) {
				showLines = new String[3];
				showLines[0] = "总组工程";
				showLines[1] = "分解工程";
				showLines[2] = "NS 工程";
				enterCom =true;
			} else if ("00000000015".equals(sline_id)) {
				showLines = new String[4];
				showLines[0] = "最终检验";
				showLines[1] = "分解工程";
				showLines[2] = "NS 工程";
				showLines[3] = "总组工程";
				enterCom =true;
			}
		}

		for (String showLine : showLines) {
			Map<String, String> fileTempl = PcsUtils.getXmlContents(showLine, mform.getModel_name(), null, material_id, 
					RvsUtils.isLightFix(mform.getLevel()), conn);

			if ("NS 工程".equals(showLine)) filterSolo(fileTempl, material_id, conn);

			Map<String, String> fileHtml = PcsUtils.toHtml(fileTempl, material_id, mform.getSorc_no(),
					mform.getModel_name(), mform.getSerial_no(), mform.getLevel(), pf.getProcess_code(), isLeader ? sline_id : null, 
							MaterialTagService.getAnmlMaterials(conn).contains(material_id), conn);
			fileHtml = RvsUtils.reverseLinkedMap(fileHtml);
			pcses.add(fileHtml);
		}

		// 采用NS 组件，得到工程检查票
		if (enterCom && "1".equals(mform.getLevel())) {
			// 判断是否NS组件组装对象
			Set<String> nsCompModels = ComponentSettingService.getNsCompModels(conn);
			boolean isNsCompModel = nsCompModels.contains(mform.getModel_id());

			if (isNsCompModel) {
				ComponentManageService cmService = new ComponentManageService();
				String serialNos = cmService.getSerialNosForTargetMaterial(material_id, conn);
				if (!isEmpty(serialNos)) {
					// 有分配

					// 判断组件签收
					MaterialPartialMapper mpMapper = conn.getMapper(MaterialPartialMapper.class);
					List<MaterialPartialDetailEntity> mpdEntities = mpMapper.getMpdForComponent(material_id);

					String[] serialNoArray = serialNos.split(",");
					for (int i = 0;i < serialNoArray.length; i++) {
						if (i >= mpdEntities.size()) { // 没有签收组件信息
							break;
						}
						if (mpdEntities.get(i).getRecent_receive_time() == null) { // 没有签收操作
							break;
						}

						Map<String, Object> partResponse = new HashMap<String, Object>();
						SoloProductionFeatureEntity spf = new SoloProductionFeatureEntity();
						spf.setModel_name(mform.getModel_name());
						spf.setSerial_no(serialNoArray[i]);
						spf.setProcess_code(pf.getProcess_code());
						PositionPanelService.getSoloPcses(partResponse, spf, sline_id, conn);

						List<Map<String, String>> partPcses = (List<Map<String, String>>) partResponse.get("pcses");
						if (partPcses.size() > 0) {
							Map<String, String> nsLine = null;

							for (int isl = 0 ; isl < showLines.length; isl++) {
								if ("NS 工程".equals(showLines[isl])) {
									nsLine = pcses.get(isl);
									break;
								}
							}

							if (nsLine != null) {
								for (String pcsName : partPcses.get(0).keySet()) {
									if (i == 0) {
										nsLine.put(pcsName, partPcses.get(0).get(pcsName));
									} else {
										nsLine.put(pcsName + "(" + i + ")", partPcses.get(0).get(pcsName));
									}
								}
							}
						}
					}
				}
			}
		}

		listResponse.put("pcses", pcses);
	}

	/**
	 * 取得工程检查票(工位全完成)
	 * @param listResponse
	 * @param pf
	 * @param user
	 * @param conn
	 */
	public static void getPcsesFinish(Map<String, Object> listResponse, ProductionFeatureEntity pf,
			SqlSession conn) {
		String material_id = pf.getMaterial_id();
		MaterialForm mform = (MaterialForm) listResponse.get("mform");

		List<Map<String, String>> pcses = new ArrayList<Map<String, String>>();

		String[] showLines = new String[6];
		showLines[0] = "最终检验";
		showLines[1] = "检查卡";
		showLines[2] = "外科硬镜修理工程";
		showLines[3] = "分解工程";
		showLines[4] = "NS 工程";
		showLines[5] = "总组工程";

		for (String showLine : showLines) {
			Map<String, String> fileTempl = PcsUtils.getXmlContents(showLine, mform.getModel_name(), null, 
					material_id, RvsUtils.isLightFix(mform.getLevel()), conn);

			if ("NS 工程".equals(showLine)) filterSolo(fileTempl, material_id, conn);

			Map<String, String> fileHtml = PcsUtils.toHtml(fileTempl, material_id, mform.getSorc_no(),
					mform.getModel_name(), mform.getSerial_no(), mform.getLevel(), "619", null, 
					MaterialTagService.getAnmlMaterials(conn).contains(material_id), conn);
			fileHtml = RvsUtils.reverseLinkedMap(fileHtml);
			pcses.add(fileHtml);
		}

		// 组件
		if ("1".equals(mform.getLevel())) {
			Set<String> nsCompModels = ComponentSettingService.getNsCompModels(conn);
			boolean isNsCompModel = nsCompModels.contains(mform.getModel_id());

			if (isNsCompModel) {
				ComponentManageService cmService = new ComponentManageService();
				String serialNos = cmService.getSerialNosForTargetMaterial(material_id, conn);
				if (!isEmpty(serialNos)) {
					// 有分配

					String[] serialNoArray = serialNos.split(",");
					for (int i = 0;i < serialNoArray.length; i++) {

						Map<String, Object> partResponse = new HashMap<String, Object>();
						SoloProductionFeatureEntity spf = new SoloProductionFeatureEntity();
						spf.setModel_name(mform.getModel_name());
						spf.setSerial_no(serialNoArray[i]);
						spf.setProcess_code(pf.getProcess_code());
						PositionPanelService.getSoloPcses(partResponse, spf, null, conn);

						List<Map<String, String>> partPcses = (List<Map<String, String>>) partResponse.get("pcses");
						if (partPcses.size() > 0) {
							Map<String, String> nsLine = null;

							for (int isl = 0 ; isl < showLines.length; isl++) {
								if ("NS 工程".equals(showLines[isl])) {
									nsLine = pcses.get(isl);
									break;
								}
							}

							if (nsLine != null) {
								for (String pcsName : partPcses.get(0).keySet()) {
									if (i == 0) {
										nsLine.put(pcsName, partPcses.get(0).get(pcsName));
									} else {
										nsLine.put(pcsName + "(" + i + ")", partPcses.get(0).get(pcsName));
									}
								}
							}
						}
					}
				}
			}
		}

		listResponse.put("pcses", pcses);
	}

	/**
	 * 取得工程检查票
	 * @param listResponse
	 * @param pf
	 * @param user
	 * @param conn
	 */
	public static void getSoloPcses(Map<String, Object> listResponse, SoloProductionFeatureEntity pf, String sline_id,
			SqlSession conn) {
		List<Map<String, String>> pcses = new ArrayList<Map<String, String>>();

		String[] showLines = new String[1];
		showLines[0] = "NS 工程";

		for (String showLine : showLines) {
			Map<String, String> fileTempl = PcsUtils.getXmlContents(showLine, pf.getModel_name(), null, conn);

			Map<String, String> fileTemplSolo = new HashMap<String, String>();
			for (String key : fileTempl.keySet()) {
				if (key.contains("NS组件组装")) {
					fileTemplSolo.put(key, fileTempl.get(key));
					break;
				}
			}

			Map<String, String> fileHtml = PcsUtils.toHtmlSnout(fileTemplSolo,
					pf.getModel_name(), pf.getSerial_no(), pf.getProcess_code(), null, conn);

			fileHtml = RvsUtils.reverseLinkedMap(fileHtml);
			pcses.add(fileHtml);
		}

		listResponse.put("pcses", pcses);
	}

	/**
	 * 根据实际工作履历，过滤可选的工位相关工程检查票
	 * @param fileTempl
	 * @param material_id
	 * @param conn
	 */
	private static void filterSolo(Map<String, String> fileTempl, String material_id, SqlSession conn) {

		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

		List<String> snouts = new ArrayList<String>(); 
		List<String> ccds = new ArrayList<String>(); 
		List<String> eyeLens = new ArrayList<String>(); 
		List<String> ccdls = new ArrayList<String>(); 
		List<String> nscomps = new ArrayList<String>(); 

		for (String key : fileTempl.keySet()) {
			if (key.contains("先端预制")) {
				snouts.add(key);
			}
			else if (key.contains("CCD盖玻璃")) {
				ccds.add(key);
			}
			else if (key.contains("LG")) {
				eyeLens.add(key);
			}
			else if (key.contains("CCD线")) { // TODO
				ccdls.add(key);
			}
			else if (key.contains("NS组件组装")) {
				nscomps.add(key);
			}
		}
		// 如果有先端预制工程检查票
		if (snouts.size() > 0) {
			// 检查是否做过301工位
			if (!dao.checkPositionDid(material_id, "00000000024", null, null)) {
				for (String snout : snouts) {
					fileTempl.remove(snout);
				}
			}
		}
		
		// 如果有CCD盖玻璃工程检查票
		if (ccds.size() > 0) {
			// 检查是否做过302工位
			if (!dao.checkPositionDid(material_id, "00000000025", null, null)) {
				for (String ccd : ccds) {
					fileTempl.remove(ccd);
				}
			}
		}

		// 如果有LG玻璃工程检查票
		if (eyeLens.size() > 0) {
			// 检查是否做过303工位
			if (!dao.checkPositionDid(material_id, "00000000060", null, null)) {
				for (String lg : eyeLens) {
					fileTempl.remove(lg);
				}
			}
		}

		// 如果有CCD线工程检查票
		if (ccdls.size() > 0) {
			// 检查是否做过304工位
			if (!dao.checkPositionDid(material_id, "00000000066", null, null)) {
				for (String ccdl : ccdls) {
					fileTempl.remove(ccdl);
				}
			}
		}

		// 如果有NS组件组装工程检查票
		if (nscomps.size() > 0) {
			
			// 检查是否使用过组件
			ComponentManageService cmService = new ComponentManageService();
			String serialNos = cmService.getSerialNosForTargetMaterial(material_id, conn);
			if (isEmpty(serialNos)) {
				for (String nscomp : nscomps) {
					fileTempl.remove(nscomp);
				}
			}
		}
	}

	/**
	 * 检查当前是否存在未完成的辅助
	 * @param material_id
	 * @param position_id
	 * @param errors
	 * @param conn
	 */
	public void checkSupporting(String material_id, String position_id, List<MsgInfo> errors, SqlSession conn) {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		List<String> supportors = dao.checkSupporting(material_id, position_id);
		if (supportors.size() > 0) {
			MsgInfo info = new MsgInfo();
			info.setComponentid("material_id");
			info.setErrcode("info.linework.waitForSupporter");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.waitForSupporter", 
					CommonStringUtil.joinBy("，", supportors.toArray(new String[supportors.size()]))));
			errors.add(info);
		}
	}

	/**
	 * 确定工程检查票是否全填写
	 * @param material_id
	 * @param position_id
	 * @param errors
	 * @param conn
	 */
	public void checkPcsEmpty(String pcs_input, List<MsgInfo> infoes) {
		if (CommonStringUtil.isEmpty(pcs_input)) return;
		if (pcs_input.contains("\"\"")) {
			MsgInfo info = new MsgInfo();
			info.setComponentid("material_id");
			info.setErrcode("info.linework.pcsCheck");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.pcsCheck"));
			infoes.add(info);
		}
	}

	/**
	 * 取得维修对象工作信息
	 */
	public void getProccessingData(Map<String, Object> listResponse, String material_id, ProductionFeatureEntity pf,
			LoginData user, SqlSession conn) throws Exception {
		// 取得维修对象信息。
		MaterialForm mform = this.getMaterialInfo(material_id, conn);
		mform.setOperate_result(String.valueOf(pf.getOperate_result()));

		// 动物内镜用
		if (MaterialTagService.getAnmlMaterials(conn).contains(material_id)) {
			mform.setAnml_exp("true");
		}

		listResponse.put("mform", mform);

		// 取得维修对象在本工位作业信息。 TODO v2
		// List<ProductionFeatureEntity> productionFeatureEntities = service.getPositionWorksByMaterial(material_id, user, conn);

		// 取到等待作业记录的本次返工总时间
		listResponse.put("spent_mins", this.getTotalTimeByRework(pf, conn) / 60);

		// 取得维修对象的作业标准时间。
		String leagal_overline = RvsUtils.getLevelOverLine(mform.getModel_name(), mform.getCategory_name(), mform.getLevel(), user, null);
		String process_code = pf.getProcess_code();
		Map<String, String> snoutModels = RvsUtils.getSnoutModels(conn);
		Set<String> snoutSaveTime341Models = RvsUtils.getSnoutSavetime341Models(conn);
		// 新的算法331一律55分钟，341对应机型减40分钟
		if (("331".equals(process_code) && snoutModels.containsKey(mform.getModel_id()))
				|| ("341".equals(process_code) && snoutSaveTime341Models.contains(mform.getModel_id()))) { // 判断先端预制使用改变时间
			ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
			// 判断先端预制
			if (dao.checkPositionDid(material_id, "00000000024", "2", null)) { // "" + pf.getRework())) {
				// 用过先端预制的话减去15分钟
				leagal_overline = "" + (Integer.parseInt(leagal_overline) - 15);
			}
//		} else if ("151".equals(process_code) || "161".equals(process_code)) {
		}
		listResponse.put("leagal_overline", leagal_overline);

		// 取得维修对象在本工位中断/作业流程信息。

		// remove at 7 start
//		// 取得维修对象在本工程中的计划时间
//		Date materialRemainTime = this.checkMaterialRemainTime(material_id, user.getLine_id(), conn);
//		if (materialRemainTime != null) {
//			listResponse.put("material_remain_time", materialRemainTime.getTime());
//		}
		// remove at 7 end

	}

//	private Date checkMaterialRemainTime(String material_id, String line_id,
//			SqlSession conn) {
//		MaterialRemainTimeMapper ppMapper = conn.getMapper(MaterialRemainTimeMapper.class);
//
//		return ppMapper.getMaterialRemainTime(material_id, line_id);
//	}

	/**
	 * 多件并行信息
	 */
	public void searchWorkingBatch(Map<String, Object> listResponse, LoginData user, SqlSession conn) {
		// 判断是否有在进行中的维修对象
		List<ProductionFeatureEntity> workingPfs = this.getWorkingPfs(user, conn);
		// 进行中的话
		if (workingPfs != null && workingPfs.size() > 0) {
			List<MaterialEntity> mForms = new ArrayList<MaterialEntity>();
			MaterialMapper mDao = conn.getMapper(MaterialMapper.class);
			Date firstAction_time = new Date(4000000000000l); // 2096年
			for (ProductionFeatureEntity workingPf : workingPfs) {
				// 取得作业信息
				Date thisAction_time = workingPf.getAction_time();
				if (firstAction_time.after(thisAction_time)) {
					firstAction_time = thisAction_time;
				}
				// 取得维修对象表示信息
				MaterialEntity mEntity = mDao.getMaterialNamedEntityByKey(workingPf.getMaterial_id());

				MaterialEntity mForm = new MaterialEntity();
				mForm.setMaterial_id(mEntity.getMaterial_id());
				mForm.setSorc_no(mEntity.getSorc_no());
				mForm.setCategory_name(mEntity.getCategory_name());
				mForm.setModel_name(mEntity.getModel_name());
				mForm.setSerial_no(mEntity.getSerial_no());
				mForm.setFix_type(mEntity.getFix_type());
				mForm.setOperate_result(workingPf.getOperate_result());
				mForms.add(mForm);
			}

			listResponse.put("workingPfs", mForms);
			listResponse.put("action_time", DateUtil.toString(firstAction_time, "HH:mm"));
			// 页面设定为编辑模式
			listResponse.put("workstauts", "1");
		} else {
			// 准备中
			listResponse.put("workstauts", "0");
		}
	}

	/**
	 * 工程中最后一个工位时(目前仅261)，检查工程中所有工位是否已完成
	 * @param workingPf
	 * @param infoes
	 * @param conn
	 */
	public void checkLineOver(ProductionFeatureEntity workingPf, List<MsgInfo> infoes, SqlSession conn) {
		String processCode = workingPf.getProcess_code();
		if ("261".equals(processCode)) { // TODO all
			ProcessAssignMapper paDao = conn.getMapper(ProcessAssignMapper.class);
			boolean finishedByLine = paDao.getFinishedByLine(workingPf.getMaterial_id(), workingPf.getLine_id());
			if (!finishedByLine) {
				MsgInfo info = new MsgInfo();
				info.setComponentid("material_id");
				info.setErrmsg("本工程内还有作业未完成，请等待全部完成后再结束本工位。"); // TODO temp
				infoes.add(info);
			}
		}
	}

	/**
	 * 执行工位扫描后的特殊事件
	 * @param waitingPf
	 * @param user
	 * @param triggerList
	 * @param conn
	 */
	public void executeActionByPosition(ProductionFeatureEntity waitingPf, LoginData user, List<String> triggerList,
			SqlSessionManager conn) throws Exception {
		String positionId = waitingPf.getPosition_id();

		if (waitingPf.getOperate_result() == 0 
				&& ("00000000033".equals(positionId) || "00000000042".equals(positionId) 
						|| "00000000048".equals(positionId) || "00000000050".equals(positionId))) { // TODO properties?
			// 如果投入总组开始, 取出库位
			ComposeStorageMapper scDao = conn.getMapper(ComposeStorageMapper.class);
			scDao.stockRemoval(waitingPf.getMaterial_id());
		}

		// 倒计时管理，只做翻修1课
		// 工位首次开始作业或者中断再开 TODO
		String processCode = user.getProcess_code();
		if ("00000000001".equals(user.getSection_id())
				&& waitingPf.getOperate_result() == 0
				&& ("261".equals(processCode)
					|| "331".equals(processCode) || "341".equals(processCode)
					|| "411".equals(processCode) || "511".equals(processCode)
					|| "410".equals(processCode) || "412".equals(processCode) || "471".equals(processCode))) {
			// 计算预估完成日
			triggerList.add("http://localhost:8080/rvspush/trigger/expected_finish_time/"
					+ waitingPf.getMaterial_id() + "/" + user.getLine_id() + "/" + positionId);
		}
	}

	/**
	 * 取得使用设备
	 * @param position_id
	 * @return
	 */
	public String getManageNo(String position_id,SqlSession conn) {
		if ("00000000010".equals(position_id)) {

			Map<String,String> map = new LinkedHashMap<String,String>();

			DevicesManageMapper dao = conn.getMapper(DevicesManageMapper.class);
			DevicesManageEntity entity = new DevicesManageEntity();
			entity.setSpecialized(DevicesTypeService.SPECIALIZED_FOR_DISINFECT_DEVICE);
			entity.setStatus("1");

			List<DevicesManageEntity> list = dao.searchDeviceManage(entity);

			for(DevicesManageEntity devicesManageEntity:list){
				map.put(devicesManageEntity.getDevices_manage_id(), devicesManageEntity.getName() + " " + devicesManageEntity.getManage_code());
			}
//			map.put("00000000000", "(手动)");

			return "<span class=\"device_manage_select\"><select class=\"manager_no\" code=\"ER12101\">" +
				CodeListUtils.getSelectOptions(map, "", null, false) + "</select></span>" +
				"<span class=\"device_manage_item ui-state-default\">设备管理No. 选择: </span>";

		} else if ("00000000011".equals(position_id)) {

			Map<String,String> map = new LinkedHashMap<String,String>();

			DevicesManageMapper dao = conn.getMapper(DevicesManageMapper.class);
			DevicesManageEntity entity = new DevicesManageEntity();
			entity.setSpecialized(DevicesTypeService.SPECIALIZED_FOR_STERILIZE_DEVICE);
			entity.setStatus("1");

			List<DevicesManageEntity> list = dao.searchDeviceManage(entity);

			for(DevicesManageEntity devicesManageEntity:list){
				map.put(devicesManageEntity.getDevices_manage_id(), devicesManageEntity.getManage_code());
			}

			return "<span class=\"device_manage_select\"><select class=\"manager_no\" code=\"ER13101\">" +
				CodeListUtils.getSelectOptions(map, "", null, false) + "</select></span>" +
				"<span class=\"device_manage_item ui-state-default\">设备管理No. 选择: </span>";
		}
		return null;
	}

	/**
	 * 取得点检相关信息
	 * @param section_id
	 * @param position_id
	 * @param conn
	 * @param line_id 
	 * @return
	 * @throws Exception 
	 */
	public String getInfectMessageByPosition(String section_id,
			String position_id, String line_id, SqlSession conn) throws Exception {
		// 查找点检不合格中断的项目
		CheckUnqualifiedRecordMapper curMapper 
			= conn.getMapper(CheckUnqualifiedRecordMapper.class);
		CheckUnqualifiedRecordEntity curEntity = new CheckUnqualifiedRecordEntity();
		curEntity.setSection_id(section_id);
		curEntity.setPosition_id(position_id);
		boolean hasBlocked = curMapper.checkBlockedToolsOnPosition(curEntity);
		if (hasBlocked) {
			return "本工位治具点检发生不合格且未解决，将限制工作。";
		}
		hasBlocked = curMapper.checkBlockedDevicesOnPosition(curEntity);
		if (hasBlocked) {
			return "本工位设备工具点检发生不合格且未解决，将限制工作。";
		}

		Date today = new Date();
		String todayString = DateUtil.toString(today, DateUtil.ISO_DATE_PATTERN);
		today = DateUtil.toDate(todayString, DateUtil.ISO_DATE_PATTERN);

		CheckResultMapper crMapper = conn.getMapper(CheckResultMapper.class);
		CheckResultEntity cond = new CheckResultEntity();
		cond.setSection_id(section_id);
		cond.setPosition_id(position_id);
		cond.setLine_id(line_id);
		// cond.setOperator_id(operator_id); TODO

		PeriodsEntity period = CheckResultService.getPeriodsOfDate(todayString, conn);
		cond.setCheck_confirm_time_start(period.getStartOfMonth());
		cond.setCheck_confirm_time_end(period.getEndOfMonth());

		List<CheckResultEntity> list = crMapper.searchToolUncheckedOnPosition(cond);

		String retComments = "";
		if (list.size() > 0) {
			if (DateUtil.compareDate(today, period.getExpireOfMonthOfJig()) >= 0) {
				retComments += "本工位有"+list.size()+"件治具在期限前未作点检，将限制工作。\n";
			} else {
				retComments += "本工位有"+list.size()+"件治具尚未点检，期限为"+
						DateUtil.toString(period.getExpireOfMonthOfJig(), DateUtil.ISO_DATE_PATTERN)+"，请在期限前完成点检。\n";
			}
		}

		// 设备
		String dailyDevices = crMapper.searchDailyDeviceUncheckedOnPosition(cond);
		if (!CommonStringUtil.isEmpty(dailyDevices)) {
			Calendar now = Calendar.getInstance();
			if (now.get(Calendar.HOUR_OF_DAY) >= 14) { // TODO SYSTEM PARAM 14
				// 下午2点锁定
				retComments += "本工位有以下日常点检设备："+dailyDevices+"在期限前未作点检，将限制工作。\n";
			} else {
				// 否则提醒
				retComments += "本工位有以下日常点检设备："+dailyDevices+"将到达点检期限，请尽快进行点检。\n";
			}
		}

		cond.setCycle_type(1);
		cond.setCheck_confirm_time_start(period.getStartOfWeek());
		cond.setCheck_confirm_time_end(period.getEndOfWeek());
		String regularDevices = crMapper.searchRegularyDeviceUncheckedOnPosition(cond);
		if (!CommonStringUtil.isEmpty(regularDevices)) {
			if (today.after(period.getStartOfWeek())) {
				// 期限内锁定
				retComments += "本工位有以下周点检设备："+regularDevices+"在期限前未作点检，将限制工作。\n";
			} else {
				// 否则提醒
				retComments += "本工位有以下周点检设备："+regularDevices+"将到达点检期限，请尽快进行点检。\n";
			}
		}

		cond.setCycle_type(2);
		cond.setCheck_confirm_time_start(period.getStartOfMonth());
		cond.setCheck_confirm_time_end(period.getEndOfMonth());
		regularDevices = crMapper.searchRegularyDeviceUncheckedOnPosition(cond);
		if (!CommonStringUtil.isEmpty(regularDevices)) {
			if (today.getTime() >= period.getExpireOfMonth().getTime()) {
				// 期限内锁定
				retComments += "本工位有以下月点检设备："+regularDevices+"在期限前未作点检，将限制工作。\n";
			} else {
				// 否则提醒
				retComments += "本工位有以下月点检设备："+regularDevices+"将到达点检期限("+
						DateUtil.toString(period.getExpireOfMonth(), DateUtil.ISO_DATE_PATTERN)+")，请尽快进行点检。\n";
			}
		}

		cond.setCheck_confirm_time_start(period.getStartOfHMonth());
		cond.setCheck_confirm_time_end(period.getEndOfHMonth());
		regularDevices = crMapper.searchTorsionDeviceUncheckedOnPosition(cond);
		if (!CommonStringUtil.isEmpty(regularDevices)) {
			if (today.getTime() >= period.getExpireOfHMonth().getTime()) {
				// 期限内锁定
				retComments += "本工位有以下半月点检设备："+regularDevices+"在期限前未作点检，将限制工作。\n";
			} else {
				// 否则提醒
				retComments += "本工位有以下半月点检设备："+regularDevices+"将到达点检期限("+
						DateUtil.toString(period.getExpireOfHMonth(), DateUtil.ISO_DATE_PATTERN)+")，请尽快进行点检。\n";
			}
		}

		cond.setCycle_type(3);
		cond.setCheck_confirm_time_start(period.getStartOfHbp());
		cond.setCheck_confirm_time_end(period.getEndOfHbp());
		regularDevices = crMapper.searchRegularyDeviceUncheckedOnPosition(cond);
		if (!CommonStringUtil.isEmpty(regularDevices)) {
			if (today.getTime() >= period.getExpireOfHbp().getTime()) {
				// 期限内锁定
				retComments += "本工位有以下半期点检设备："+regularDevices+"在期限前未作点检，将限制工作。\n";
			} else {
				// 否则提醒
				retComments += "本工位有以下半期点检设备："+regularDevices+"将到达点检期限("+
						DateUtil.toString(period.getExpireOfHbp(), DateUtil.ISO_DATE_PATTERN)+")，请尽快进行点检。\n";
			}
		}

		return retComments;
	}

	public String getAbnormalWorkStateByOperator(String operator_id, SqlSession conn) throws Exception {
		String retComments = "";
		Calendar now = Calendar.getInstance();

		// 查找工时异动
		AbnormalWorkStateMapper awsMapper = conn.getMapper(AbnormalWorkStateMapper.class);
		List<Map<String, Object>> list = awsMapper.getAbnormalWorkStateByOperator(operator_id);
		for (Map<String, Object> m:list) {
			// abnormal_type
			Date ab = (Date) m.get("occur_date");
			if (now.get(Calendar.HOUR_OF_DAY) < 10) { // TODO SYSTEM PARAM 10
				// 限制
				retComments += ""+
						DateUtil.toString(ab, DateUtil.ISO_DATE_PATTERN)+"的工作日报不完整。"
								+ "请[lickid='opd_loader_refill'onclick='javascript:opd_pop(null, null, \"" + DateUtil.toString(ab, DateUtil.DATE_PATTERN) + "\")'>进行补充]。\n";
			} else {
				// 限制
				retComments += ""+
						DateUtil.toString(ab, DateUtil.ISO_DATE_PATTERN)+"的工作日报不完整，将限制工作。"
								+ "请[lickid='opd_loader_refill'onclick='javascript:opd_pop(null, null, \"" + DateUtil.toString(ab, DateUtil.DATE_PATTERN) + "\")'>进行补充]。\n";
			}
		}
		return retComments;
	}

	public void notifyPosition(String section_id, String position_id, String material_id, boolean isLight) {
		// 通知
		try {
			HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
			httpclient.start();
			try {  
				String inUrl = "http://localhost:8080/rvspush/trigger/in/" + position_id + "/" + section_id;
				if (material_id != null) {
					inUrl += "/" + material_id + "/" + (isLight ? "1" : "0");
				}
	            HttpGet request = new HttpGet(inUrl);
	            _log.info("finger:"+request.getURI());
	            httpclient.execute(request, null);

	        } catch (Exception e) {
			} finally {
				Thread.sleep(100);
				httpclient.shutdown();
			}
		} catch (IOReactorException | InterruptedException e1) {
			_log.error(e1.getMessage(), e1);
		}
	}

//	public Map<String, List<WaitingEntity>> getWaitingMaterialOtherPx(String section_id,
//			String position_id, String line_id, String operator_id, String px, String process_code, boolean hasC, SqlSession conn) {
//		Map<String, List<WaitingEntity>> ret = new HashMap<String, List<WaitingEntity>>();
//		if (hasC) {
//			if ("1".equals(px)) {
//				ret.put("2", getWaitingMaterial(section_id, position_id, line_id, operator_id, "2", process_code, conn));
//				ret.put("3", getWaitingMaterial(section_id, position_id, line_id, operator_id, "3", process_code, conn));
//			} else if ("2".equals(px)) {
//				ret.put("3", getWaitingMaterial(section_id, position_id, line_id, operator_id, "3", process_code, conn));
//			} else if ("3".equals(px)) {
//				ret.put("2", getWaitingMaterial(section_id, position_id, line_id, operator_id, "2", process_code, conn));
//			}
//		} else {
//			if ("1".equals(px)) {
//				ret.put("2", getWaitingMaterial(section_id, position_id, line_id, operator_id, "2", process_code, conn));
//			} else if ("2".equals(px)) {
//				ret.put("1", getWaitingMaterial(section_id, position_id, line_id, operator_id, "1", process_code, conn));
//			}
//		}
//		return ret;
//	}

	/**
	 * 取得周边设备检查使用设备工具 
	 * @param material_id
	 * @param callbackResponse 
	 * @param retEntity 
	 * @param waitingPf
	 * @param conn
	 * @return 当前工位点检是否完成
	 * @throws Exception 
	 */
	public boolean getPeripheralData(String material_id, ProductionFeatureEntity pfEntity,
			List<PeripheralInfectDeviceEntity> retEntity, SqlSession conn) {
		PeripheralInfectDeviceMapper dao = conn.getMapper(PeripheralInfectDeviceMapper.class);

		// 当前工位点检是否完成
		boolean infectFinishFlag = true;

		PeripheralInfectDeviceEntity condEntity = new PeripheralInfectDeviceEntity();
		condEntity.setMaterial_id(material_id);
		condEntity.setPosition_id(pfEntity.getPosition_id());
		condEntity.setRework(pfEntity.getRework());
		// 取得可点检项目
		List<PeripheralInfectDeviceEntity> resultEntities = dao.getPeripheralDataByMaterialId(condEntity);
		// 各组的内容
		// Map<Integer, PeripheralInfectDeviceEntity> resultEntityOfSeq = new HashMap<Integer, PeripheralInfectDeviceEntity>();

		DevicesManageMapper devicesManageDao = conn.getMapper(DevicesManageMapper.class);
		int seqTag = -1, seqCursor = -1, seqCount = 0;
		for (int i = 0; i < resultEntities.size(); i++) {
			PeripheralInfectDeviceEntity result = resultEntities.get(i);
			// 根据每一项的品名及型号，取得manageCodeList
			DevicesManageEntity devicesManageEntity = new DevicesManageEntity();
			devicesManageEntity.setDevices_type_id(result.getDevice_type_id());
			devicesManageEntity.setModel_name(result.getModel_name());
			List<DevicesManageEntity> manageCodeList = devicesManageDao.getManageCode(devicesManageEntity);

			result.setGroup(0);
			if (seqTag != result.getSeq()) {
				// 新的编号
				// 整理上一个重复
				if (seqCount > 0) {
					resultEntities.get(seqCursor).setGroup(seqCount + 1);
					for (int j = 1; j <= seqCount; j++) {
						resultEntities.get(seqCursor + j).setGroup(-1);
					}
				}

				// 重新标记
				seqTag = result.getSeq();
				seqCursor = i;
				seqCount = 0;
			} else {
				// 重复的编号
				seqCount++;
			}

			Map<String, String> codeMap = new TreeMap<String, String>();
			String codeId = "";
			for (DevicesManageEntity bean : manageCodeList) {
				if (!CommonStringUtil.isEmpty(result.getDevice_manage_id())
						&& result.getDevice_manage_id().equals(bean.getDevices_manage_id())) {
					// 取得已选中的设备
					codeId = bean.getDevices_manage_id() + "," + bean.getCheck_result();
					// 同组第一个已点检
					resultEntities.get(seqCursor).setCheck_result("已点检");
				}
				codeMap.put(bean.getDevices_manage_id() + "," + bean.getCheck_result(), bean.getManage_code());
			}

			// 每一项的设备可选项
			result.setManageCodeOptions(CodeListUtils.getSelectOptions(codeMap, codeId, "", true));

			if (isEmpty(result.getDevice_manage_id())) {
				infectFinishFlag = false;
			}
		}

		// 整理上一个重复
		if (seqCount > 0) {
			resultEntities.get(seqCursor).setGroup(seqCount + 1);
			for (int j = 1; j <= seqCount; j++) {
				resultEntities.get(seqCursor + j).setGroup(-1);
			}
		}

		retEntity.addAll(resultEntities);

		return infectFinishFlag;
	}

	/**
	 * 插入点检完成的数据
	 * @param req
	 * @param user
	 * @param conn
	 * @return
	 * @throws Exception 
	 */
	public void finishcheck(HttpServletRequest req, LoginData user, SqlSession conn)
			throws Exception {
		List<PeripheralInfectDeviceEntity> list = new AutofillArrayList<PeripheralInfectDeviceEntity>(
				PeripheralInfectDeviceEntity.class);
		Map<String, String[]> map = (Map<String, String[]>) req.getParameterMap();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : map.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String table = m.group(1);
				if ("finishcheck".equals(table)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = map.get(parameterKey);
					if ("manage_id".equals(column)) {
						list.get(icounts).setDevice_manage_id(value[0]);
					} else if ("seq".equals(column)) {
						list.get(icounts).setSeq(Integer.parseInt(value[0]));
					}
				}
			}
		}

		// 取得当前作业中作业信息
		ProductionFeatureEntity workingPf = getWorkingPf(user, conn);

		PeripheralInfectDeviceMapper dao = conn.getMapper(PeripheralInfectDeviceMapper.class);
		for (PeripheralInfectDeviceEntity insertBean : list) {	
			insertBean.setMaterial_id(workingPf.getMaterial_id());
			insertBean.setPosition_id(workingPf.getPosition_id());
			insertBean.setRework(workingPf.getRework());
			insertBean.setUpdated_by(user.getOperator_id());
			// 新建记录插入到数据库中
			dao.insertFinishedData(insertBean);
		}
	}

	public String getOtherPx(String px, String process_code) {
		if ("1".equals(px) || "3".equals(px)) {
			return "2";
		} else if ("2".equals(px)) {
			if (process_code != null && process_code.startsWith("5"))
				return "3";
			else
				return "1";
		} else {
			return "";
		}
	}

	/**
	 * 取得虚拟组工位名显示
	 * @param process_code
	 * @param user
	 * @param subPositions
	 * @param conn
	 * @return
	 */
	public String getGroupShowPositionName(String process_code, LoginData user,
			List<String> subPositions, SqlSession conn) {
		PositionService pService = new PositionService();

		String showPositionName = process_code;
		if (process_code == null) { // 会话当前非虚拟组工位
			PositionEntity pEntity = pService.getPositionEntityByKey(user.getGroup_position_id(), conn);
			showPositionName = pEntity.getProcess_code();
		}

		showPositionName += "(";
		for (String subPositionId : subPositions) {
			PositionEntity pEntity = pService.getPositionEntityByKey(subPositionId, conn);
			if (pEntity != null) {
				showPositionName += pEntity.getProcess_code() + ",";
			}
		}
		showPositionName = showPositionName.substring(0, showPositionName.length() - 1) + ") " + user.getPosition_name();
		return showPositionName;
	}

	/**
	 * 取得工位的正常中断选项
	 * @param process_code
	 * @return
	 */
	public String getStepOptions(String process_code) {
		String stepOptions = "";
		// 设定正常中断选项
		String steps = PathConsts.POSITION_SETTINGS.getProperty("steps." + process_code);
		if (steps != null) {
			String[] steparray = steps.split(",");
			for (String step : steparray) {
				step = step.trim();
				String stepname = PathConsts.POSITION_SETTINGS.getProperty("step." + process_code + "." + step);
				stepOptions += "<option value=\"" + step + "\">" + stepname + "</option>";
			}
		}
		return stepOptions;
	}
	/**
	 * 取得工位的异常中断选项
	 * @param process_code
	 * @return
	 */
	public String getBreakOptions(String process_code) {
		String breakOptions = "";
		if ("121".equals(process_code) || "131".equals(process_code)
				|| "171".equals(process_code) || "241".equals(process_code)
				|| "252".equals(process_code)) { // TODO 正规化，不会中断的
		} else {
			// 设定异常中断选项
			String steps = PathConsts.POSITION_SETTINGS.getProperty("break." + process_code);
			if (steps != null) {
				String[] steparray = steps.split(",");
				for (String step : steparray) {
					step = step.trim();
					String stepname = PathConsts.POSITION_SETTINGS.getProperty("break." + process_code + "." + step);
					breakOptions += "<option value=\"" + step + "\">" + stepname + "</option>";
				}
			}
			// 设定一般中断选项
			breakOptions += CodeListUtils.getSelectOptions("break_reason", null);
		}
		return breakOptions;
	}

	/**
	 * 取得工作页面统一用消息
	 * 
	 * @return
	 */
	public String getWorkInfo() {
		String varWorkInfo = "var WORKINFO={";
		varWorkInfo += "confirmPcsWhenBreak : \"" + ApplicationMessage.WARNING_MESSAGES.getMessage("info.positionwork.confirmPcsWhenBreak") + "\",";
		varWorkInfo += "pcsCheck : \"" + ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.pcsCheck") + "\",";
		varWorkInfo += "needFillBreak : \"" + ApplicationMessage.WARNING_MESSAGES.getMessage("info.positionwork.needFillBreak") + "\",";
		varWorkInfo += "chooseDryingProcessStock : \"" + ApplicationMessage.WARNING_MESSAGES.getMessage("info.positionwork.chooseDryingProcessStock") + "\",";
		varWorkInfo += "confirmPcsWhenBreak : \"" + ApplicationMessage.WARNING_MESSAGES.getMessage("info.positionwork.chooseDryingProcess") + "\",";
		varWorkInfo += "chooseDryingProcess : \"" + ApplicationMessage.WARNING_MESSAGES.getMessage("info.positionwork.confirmPcsWhenBreak") + "\",";
		varWorkInfo += "confirmSelectFirstSnout : \"" + ApplicationMessage.WARNING_MESSAGES.getMessage("info.positionwork.confirmSelectFirstSnout") + "\",";
		varWorkInfo += "abnormalSnoutOrigin : \"" + ApplicationMessage.WARNING_MESSAGES.getMessage("info.positionwork.abnormalSnoutOrigin") + "\",";
		varWorkInfo += "animalExpNotice : \"" + ApplicationMessage.WARNING_MESSAGES.getMessage("info.positionwork.animalExpNotice") + "\",";
		varWorkInfo += "animalExpClean : \""
				+ CommonStringUtil.decodeHtmlText(ApplicationMessage.WARNING_MESSAGES.getMessage("info.positionwork.animalExpClean"))
				+ "\"";
		varWorkInfo += "};";
		return varWorkInfo;
	}

	public static boolean allowPartRecieve(String process_code, MaterialForm mEntity) {
		switch (process_code) {
		case "331":
		case "242":
		case "304":
			return true;
		case "252":
			return "EndoEye".equals(mEntity.getCategory_id());
		}
		return false;
	}

}
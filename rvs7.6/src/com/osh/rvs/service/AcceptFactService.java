package com.osh.rvs.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.OperatorProductionEntity;
import com.osh.rvs.bean.qf.AfProductionFeatureEntity;
import com.osh.rvs.bean.qf.FactMaterialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.qf.AfProductionFeatureForm;
import com.osh.rvs.mapper.data.OperatorProductionMapper;
import com.osh.rvs.mapper.qf.AfProductionFeatureMapper;
import com.osh.rvs.service.qf.FactMaterialService;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.copy.IntegerConverter;

public class AcceptFactService {

	public static Map<String, String> moduleMap = CodeListUtils.getList("qf_production_module");
	public static Map<String, String> typeMap = CodeListUtils.getList("qf_production_type");

	// workRecord.reason.157 = WY7:作业准备
	private final int REASON_WY7 = 157;

	/**
	 * 取得全部间接作业选项
	 * 
	 * @param conn
	 * @return
	 */
	public String getOptions(SqlSession conn) {

		List<String[]> lst = new ArrayList<String[]>();
		for (String key : typeMap.keySet()) {
			String[] p = new String[3];
			p[0] = key;
			p[2] = moduleMap.get(key.substring(0, 2));
			p[1] = typeMap.get(key);
			lst.add(p);
		}

		return CodeListUtils.getReferChooser(lst);
	}

	public static void resetMap() {
		moduleMap = CodeListUtils.getList("qf_production_module");
		typeMap = CodeListUtils.getList("qf_production_type");
	}

	/**
	 * 根据操作者ID查找未结束作业信息
	 * 
	 * @param operatorID
	 * @param conn
	 * @return
	 */
	public AfProductionFeatureForm getUnFinish(String operatorID, SqlSession conn) {
		AfProductionFeatureMapper dao = conn.getMapper(AfProductionFeatureMapper.class);

		AfProductionFeatureForm respForm = null;
		AfProductionFeatureEntity entity = dao.getUnfinishByOperator(operatorID);

		if (entity != null) {
			respForm = new AfProductionFeatureForm();
			BeanUtil.copyToForm(entity, respForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		}

		return respForm;
	}
	public AfProductionFeatureEntity getUnFinishEntity(String operatorID, SqlSession conn) {
		AfProductionFeatureMapper dao = conn.getMapper(AfProductionFeatureMapper.class);

		AfProductionFeatureEntity entity = dao.getUnfinishByOperator(operatorID);

		return entity;
	}

	/**
	 * 结束当前作业或间歇事件
	 */
	public void finishCurrentAfWorkingOrPausingForOperator(String operatorId, boolean assertWorking, SqlSessionManager conn) throws Exception {
		AfProductionFeatureMapper afMapper = conn.getMapper(AfProductionFeatureMapper.class);
		AfProductionFeatureEntity entity = afMapper.getUnfinishByOperator(operatorId);

		if (entity != null) {
			afMapper.updateFinishTime(entity.getAf_pf_key());
		} else if (!assertWorking) {
			PauseFeatureService pfService = new PauseFeatureService();
			pfService.finishPauseFeature(null, null, null, operatorId, conn);
		}
	}

	public AfProductionFeatureForm getProcessOfOperator(LoginData user, SqlSession conn) throws Exception {
		AfProductionFeatureForm workingForm = getUnFinish(user.getOperator_id(), conn);
		
		if (workingForm == null) {
			OperatorProductionMapper mapper = conn.getMapper(OperatorProductionMapper.class);
			OperatorProductionEntity processingPauseStart = mapper.getProcessingPauseStart(user.getOperator_id());
			if (processingPauseStart == null) {
				boolean isManager = user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING);
				if (!isManager) {
					// 建立“WY7:作业准备”状态
					AfProductionFeatureForm pausingForm = new AfProductionFeatureForm();
					OperatorProductionService opService = new OperatorProductionService();
					Date autoStartTime = opService.getAutoStartTime(); 
					opService.createSimplePauseFeature(user.getOperator_id(), REASON_WY7, autoStartTime, "", null);
					pausingForm.setProduction_type("" + REASON_WY7);
					pausingForm.setAction_time(DateUtil.toString(autoStartTime, DateUtil.DATE_TIME_PATTERN));
					pausingForm.setIs_working("0");
					return pausingForm;
				} else {
					return null;
				}
			} else {
				AfProductionFeatureForm pausingForm = new AfProductionFeatureForm();
				pausingForm.setProduction_type("" + processingPauseStart.getReason());
				pausingForm.setAction_time(DateUtil.toString(processingPauseStart.getPause_start_time(), 
						DateUtil.DATE_TIME_PATTERN));
				pausingForm.setIs_working("0");
				return pausingForm;
			}
		} else {
			workingForm.setIs_working("1");
			return workingForm;
		}
	}

	public static Map<String, BigDecimal> storedStandardFactors = new HashMap<String, BigDecimal>();
	static {
		storedStandardFactors.put("TICKET_PER_MAT", new BigDecimal(1)); // 维修品票单单印 FOR 101

		storedStandardFactors.put("FACT_RECEPT_PER_MAT", new BigDecimal(2)); // 维修品实物受理 FOR 102
		storedStandardFactors.put("FACT_TESTMR_PER_MAT", new BigDecimal(5)); // 维修品/备品实物测漏 FOR 102

		storedStandardFactors.put("SYS_RECEPT_ENSC_PER_MAT", new BigDecimal("2.5")); // 维修品/备品内窥镜系统受理 FOR 103/105
		storedStandardFactors.put("SYS_RECEPT_PERI_PER_MAT", new BigDecimal(2)); // 维修品/备品周边系统受理 FOR 103/105
		storedStandardFactors.put("SYS_RECEPT_SURGI_PER_MAT", new BigDecimal("0.5")); // 手术器械清点 FOR 105
		storedStandardFactors.put("SYS_RECEPT_ACCENA_PER_MAT", new BigDecimal("0.2")); // 附件清点 FOR 105

		storedStandardFactors.put("UPLOAD_SPARE_PER_PRO", new BigDecimal(5)); // 备品系统导入 FOR 104

		storedStandardFactors.put("SYS_RECEPT_SPARE_PER_MAT", new BigDecimal(2)); // 维修品实物受理 FOR 105

		storedStandardFactors.put("TC_INSTOR_TRANS_PER_PRO", new BigDecimal(6)); // 镜箱入库搬运 FOR 106 
		storedStandardFactors.put("TC_INSTOR_ONSH_PER_MAT", new BigDecimal(5)); // 镜箱入库系统操作 FOR 106

		storedStandardFactors.put("DISINF_ENSC_PER_MAT", new BigDecimal("1.2")); // 维修品/备品内窥镜消毒 FOR 111/112
		storedStandardFactors.put("DISINF_PERI_PER_MAT", new BigDecimal("1.2")); // 维修品/备品周边消毒 FOR 111/112
		storedStandardFactors.put("DISINF_MANUAL_PER_MAT", new BigDecimal(3)); // 维修品/备品手动消毒 FOR 111/112
		storedStandardFactors.put("DISINF_SURGI_PER_MAT", new BigDecimal(3)); // 备品手术器械消毒 FOR 112
		storedStandardFactors.put("DISINF_ACCENA_PER_MAT", new BigDecimal(3)); // 备品附件消毒 FOR 112

		storedStandardFactors.put("DISINF_TC_PER_MAT", new BigDecimal("1.5")); // 备品附件消毒 FOR 113

		storedStandardFactors.put("STERI_PER_PRO", new BigDecimal(10)); // 维修品灭菌 FOR 121/122

		storedStandardFactors.put("TC_OUTSTOR_TRANS_PER_PRO", new BigDecimal(6)); // 镜箱出库搬运 FOR 131 
		storedStandardFactors.put("TC_OUTSTOR_OFFSH_PER_MAT", new BigDecimal(1)); // 镜箱出库系统操作 FOR 131

		storedStandardFactors.put("SHIPPING_PER_MAT", new BigDecimal(4)); // 维修品出货 FOR 132

		storedStandardFactors.put("INLINE_PER_MAT", new BigDecimal("1.5")); // 系统投线 FOR 201
		storedStandardFactors.put("DISTRIB_PER_PRO", new BigDecimal(20)); // 整理/运输内镜 FOR 201

		storedStandardFactors.put("PART_RECPET_TRANS_PER_PRO", new BigDecimal(11)); // 收货（开箱/运输） FOR 211

		storedStandardFactors.put("UPLOAD_PART_RECPET_PER_PRO", new BigDecimal(5)); // 上传零件入库文档 FOR 212

		storedStandardFactors.put("PART_ORDER_PER_MAT", new BigDecimal(10)); // 维修零件订购单时间 FOR 221

		storedStandardFactors.put("PART_RELEASE_NS_PER_MAT", new BigDecimal("2")); // 维修零件发放派送 NS FOR 231
		storedStandardFactors.put("PART_RELEASE_NO_NS_PER_MAT", new BigDecimal("1.5")); // 维修零件发放派送 NO_NS FOR 231

		storedStandardFactors.put("SHIPPING_ORDER_PER_MAT", new BigDecimal("1.5"));  // 出货单制作 FOR 241

		storedStandardFactors.put("UNREPAIR_RETURN_PER_MAT", new BigDecimal("5"));  // 未修理返送 FOR 242

		storedStandardFactors.put("CSM_RECPET_TRANS_PER_PRO", new BigDecimal(11));  // 消耗品收货 FOR 251

		storedStandardFactors.put("CSM_INSTOR_ONSH_L_PER_CD", new BigDecimal("2")); // 消耗品入库核对/上架长 FOR 252
		storedStandardFactors.put("CSM_INSTOR_ONSH_M_PER_CD", new BigDecimal("1")); // 消耗品入库核对/上架中 FOR 252
		storedStandardFactors.put("CSM_INSTOR_ONSH_S_PER_CD", new BigDecimal("0.3")); // 消耗品入库核对/上架短 FOR 252

		storedStandardFactors.put("CSM_OUTSTOR_OFFSH_L_PER_CD", new BigDecimal("2")); // 消耗品出库下架长 FOR 261
		storedStandardFactors.put("CSM_OUTSTOR_OFFSH_M_PER_CD", new BigDecimal("1")); // 消耗品出库下架中 FOR 261
		storedStandardFactors.put("CSM_OUTSTOR_OFFSH_S_PER_CD", new BigDecimal("0.3")); // 消耗品出库下架短 FOR 261
		storedStandardFactors.put("CSM_RELEASE_PER_PRO", new BigDecimal(2)); // 消耗品出库发放现场 FOR 261

		storedStandardFactors.put("SWC_WASH_PER_CD", new BigDecimal(2)); // 钢丝固定件清洗 FOR 271
	}

	/** 
	 * 取得标准工时
	 * @param processForm
	 * @param conn
	 * @return
	 */
	public Integer getStandardMinutes(AfProductionFeatureForm processForm, SqlSession conn) {
		return getStandardMinutes(processForm, conn, false);
	}
	public Integer getStandardMinutes(AfProductionFeatureForm processForm, SqlSession conn, boolean justStart) {
		String key = processForm.getAf_pf_key();
		String productionType = processForm.getProduction_type();

		// 作业中不计时；目前没有标准时间
		switch (productionType) {
		case "102" : // 维修品实物受理/测漏
		case "105" : // 备品实物受理/测漏
		case "211" : // 零件收货
			// 现场不统计
			return null;
		case "272" : // 热缩管切割
		case "273" : // 耗材分装
		case "274" : // NS插入管零件预包装
		case "275" : // S1级组件拆包装/分配
			// 暂时不要
			return null;
		}

		// 作业次数 part1
		BigDecimal standardPart1 = BigDecimal.ZERO;
		switch (productionType) {
		case "104" : // 备品系统导入
			// 按每次固定
			standardPart1 = storedStandardFactors.get("UPLOAD_SPARE_PER_PRO"); break;
		case "106" : // 镜箱入库
			// 每次搬运时间固定
			standardPart1 = storedStandardFactors.get("TC_INSTOR_TRANS_PER_PRO"); break;
		case "121" : // 维修品灭菌
			// 按每次固定
			standardPart1 = storedStandardFactors.get("STERI_PER_PRO"); break;
		case "122" : // 备品灭菌
			// 按每次固定
			standardPart1 = storedStandardFactors.get("STERI_PER_PRO"); break;
		case "131" : // 镜箱出库
			// 每次搬运时间固定
			standardPart1 = storedStandardFactors.get("STERI_PER_PRO"); break;
		case "201" : // 投线
			// 每次整理/运输内镜时间固定
			standardPart1 = storedStandardFactors.get("DISTRIB_PER_PRO"); break;
		case "212" : // 入库单导入
			// 按每次固定
			standardPart1 = storedStandardFactors.get("UPLOAD_PART_RECPET_PER_PRO"); break;
		case "251" : // 消耗品收货
			// 按每次固定
			standardPart1 = storedStandardFactors.get("CSM_RECPET_TRANS_PER_PRO"); break;
		case "261" : // 消耗品出库
			// 消耗品出库发放现场
			standardPart1 = storedStandardFactors.get("CSM_RELEASE_PER_PRO"); break;
		}

		BigDecimal standardPart2 = BigDecimal.ZERO;
		if (!justStart) { // 刚切换时，不需要统计
			// 作业单位计时 part2
			switch (productionType) {
				case "101" : // 票单打印
					// 通过fact_material表取得现品票打印数量
					// 按照RVS中实际现品票单数统计
					// 乘以每单用时
					standardPart2 = calcFromFactMaterial(key, "TICKET_PER_MAT", conn);
					break;
				case "103" : // 维修品/备品系统受理 TODO
					// 按key查询af_pf时间段中完成的111工位记录数，
					// 分内窥镜、周边设备、手术器械和附件
					// 分别乘以每单用时
					// 返回合计
				case "106" : // 镜箱入库
					// 通过fact_material表取得上架数量
					// 乘以每单镜箱上架用时
					standardPart2 = calcFromFactMaterial(key, "TC_INSTOR_ONSH_PER_MAT", conn);
					break;
				case "111" : // 维修品消毒 TODO
					// 按key查询af_pf时间段中完成的121工位记录数，
					// 分手动和设备
					// 设备中分内窥镜、周边设备
					// 分别乘以每单用时
					// 返回合计
				case "112" : // 备品消毒 TODO
					// 按key查询af_pf时间段中完成的121工位记录数，
					// 分手动和设备
					// 设备中分内窥镜、周边设备、手术器械和附件
					// 分别乘以每单用时
					// 返回合计
				case "113" : // 镜箱消毒
					// 按key查询af_pf时间段中完成的121工位记录数，
					// 其中的镜箱
					// 乘以每单用时
					standardPart2 = calcFromFactMaterial(key, "DISINF_TC_PER_MAT", conn);
					break;
				case "131" : // 镜箱出库
					// 通过fact_material表取得下架数量
					// 乘以每单镜箱下架用时
					standardPart2 = calcFromFactMaterial(key, "TC_OUTSTOR_OFFSH_PER_MAT", conn);
					break;
				case "132" : // 维修品出货 TODO
					// 按key查询af_pf时间段中完成的711工位记录数，
					// 乘以每单用时
				case "201" : // 投线 TODO
					// 按key查询af_pf时间段中material.inline_time，
					// 乘以每单用时
				case "213" : // 零件核对/上架 TODO
					// 按零件类别统计零件数
					// 各自乘以单位核对时间
				case "214" : // 零件分装 TODO
					// 按零件类别统计分装零件数
					// 各自乘以单位分装时间
				case "221" : // 维修零件订购 TODO
					// 按key统计个人的订购单订单*修改次数
					// 乘以维修零件订购单时间
				case "231" : // 维修零件发放 TODO
					// 1）维修零件发放派送判断
					// 1-1） 中小修理并且首工位NS ==> NS零件发放时间
					// 1-2） 中小修理并且首工位非NS ==> 分解/总组零件发放时间
					// 1-3） 大修理并且流程包含NS ==> NS零件发放 + 分解/总组零件发放时间
					// 1-4） 大修理并且流程不包含NS ==> 分解/总组零件发放时间
					// 2) 按零件类别统计零件数
					// 各自乘以单位下架时间
					// 返回 1） + 2）
				case "241" : // 维修出货单制作
					// 通过fact_material表取得出货单制作数量
					standardPart2 = calcFromFactMaterial(key, "SHIPPING_ORDER_PER_MAT", conn);
					break;
				case "242" : // 未修理返送 TODO
					// 按key查询af_pf时间段中outline_time bbf = 2记录数，
					// 乘以每单用时
				case "252" : // 消耗品核对/上架 TODO
					// 按消耗品上架时长统计品名数（借用partialwarehousedetail）
					// 各自乘以单位上架时间
				case "261" : // 消耗品出库 TODO
					// 按消耗品下架时长统计品名数（借用partialwarehousedetail）
					// 各自乘以单位下架时间
				case "271" : // 固定件清洗 TODO
					// 按key查询af_pf时间段中`steel_wire_container_wash_process` process_time记录数，
					// 乘以单位固定件清洗时间
			}
		}

		int ret = standardPart1.add(standardPart2).setScale(0, BigDecimal.ROUND_CEILING).intValue();
		if (ret > 0) {
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * 按现品作业表来统计
	 * @param key
	 * @param string
	 * @return
	 */
	private BigDecimal calcFromFactMaterial(String key, String factorName, SqlSession conn) {

		FactMaterialService fmService = new FactMaterialService();
		FactMaterialEntity fmCondition = new FactMaterialEntity();
		fmCondition.setAf_pf_key(key);
		List<FactMaterialEntity> list = fmService.searchEntities(fmCondition, conn);

		BigDecimal factor = storedStandardFactors.get(factorName);

		return factor.multiply(new BigDecimal(list.size()));
	}

	/**
	 * 切换作业
	 * @param form
	 * @param user
	 * @param conn
	 * @throws Exception 
	 */
	public AfProductionFeatureForm switchTo(ActionForm form, LoginData user, SqlSessionManager conn) throws Exception {
		AfProductionFeatureForm apfForm = (AfProductionFeatureForm) form;
		AfProductionFeatureForm retForm = new AfProductionFeatureForm();

		String[] isWorking = apfForm.getIs_working().split(">");
		// 切换自直接/间接作业
		boolean fromWorking = isWorking[0].equals("1");
		// 切换到直接/间接作业
		boolean toWorking = isWorking[1].equals("1");

		retForm.setIs_working((toWorking ? "1" : "0"));

		// 作业内容
		String production_type = apfForm.getProduction_type();
		retForm.setProduction_type(production_type);

		// 操作者 ID
		String operator_id = user.getOperator_id();

		// 是管理员
		// is_manager
		Date actionTime = switchTo(fromWorking, toWorking, production_type, operator_id, false, conn);
		retForm.setAction_time(DateUtil.toString(actionTime, DateUtil.DATE_TIME_PATTERN));

		// 取得标准工时
		Integer standard_minutes = getStandardMinutes(retForm, conn, true);
		if (standard_minutes != null) {
			retForm.setStandard_minutes("" + standard_minutes);
		}

		return retForm;
	}

	/**
	 * 切换作业
	 * @param from_working 当前是TRUE=直接作业；FALSE=其他；NULL=不明
	 * @param to_working 要切换到TRUE=直接作业；FALSE=其他
	 * @param production_type 作业内容
	 * @param operator_id 操作者 ID
	 * @param check_status 判断是否已经是需要切换的状态了
	 * @param conn
	 * @throws Exception 
	 */
	public Date switchTo(Boolean from_working, boolean to_working, String production_type, 
			String operator_id, boolean check_status, SqlSessionManager conn) throws Exception {
		if (operator_id == null || production_type == null) return null;

		AfProductionFeatureMapper apfMapper = conn.getMapper(AfProductionFeatureMapper.class);
		PauseFeatureService pfService = new PauseFeatureService();

		if (from_working == null) {
			// 取得操作者当前进行中作业
			AfProductionFeatureEntity apfEntity = apfMapper.getUnfinishByOperator(operator_id);
			from_working = (apfEntity != null);

			if (to_working && from_working && check_status) {
				if (production_type.equals(apfEntity.getProduction_type() + "")) {
					return null; // 已经开始此作业内容则不需要操作
				}
			}
		}

		if (from_working) { 
			// 结束当前直接作业
			apfMapper.finishProductionOfOperator(operator_id);
		} else {
			// 结束当前间隔作业
			pfService.finishPauseFeature(operator_id, conn);
		}

		Date action_time = new Date();
		if (to_working) {
			// 建立新直接作业
			AfProductionFeatureEntity insertEntity = new AfProductionFeatureEntity();
			insertEntity.setProduction_type(IntegerConverter.getInstance().getAsObject(production_type));
			insertEntity.setOperator_id(operator_id);
			insertEntity.setAction_time(action_time);
			apfMapper.insert(insertEntity);
		} else {
			// 建立新间隔作业
			OperatorProductionService opService = new OperatorProductionService();
			opService.createSimplePauseFeature(operator_id, IntegerConverter.getInstance().getAsObject(production_type), 
					action_time, null, conn);
		}
		return action_time;
	}

	/**
	 * 根据作业内容查找未结束作业信息
	 * 
	 * @param production_type
	 * @param conn
	 * @return
	 */
	public AfProductionFeatureEntity getUnFinishByType(int production_type, SqlSession conn) {
		AfProductionFeatureMapper dao = conn.getMapper(AfProductionFeatureMapper.class);
		return dao.getUnfinishByType(production_type);
	}

	/**
	 * 建立作业信息
	 * 
	 * @param entity
	 * @param conn
	 * @return
	 */
	public void insert(AfProductionFeatureEntity entity, SqlSession conn) {
		AfProductionFeatureMapper dao = conn.getMapper(AfProductionFeatureMapper.class);
		dao.insert(entity);
	}

	/**
	 * 通知后台刷新作业标记
	 * @param operator_id
	 */
	public void fingerOperatorRefresh(String operator_id) {
		RvsUtils.sendTrigger("http://localhost:8080/rvspush/trigger/refreshProcess/" + operator_id + "/0/0");
	}
}

package com.osh.rvs.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.OperatorProductionEntity;
import com.osh.rvs.bean.manage.UserDefineCodesEntity;
import com.osh.rvs.bean.partial.FactPartialReleaseEntity;
import com.osh.rvs.bean.qf.AfProductionFeatureEntity;
import com.osh.rvs.bean.qf.FactMaterialEntity;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.qf.AfProductionFeatureForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.data.OperatorProductionMapper;
import com.osh.rvs.mapper.manage.UserDefineCodesMapper;
import com.osh.rvs.mapper.partial.FactPartialReleaseMapper;
import com.osh.rvs.mapper.partial.PartialWarehouseDetailMapper;
import com.osh.rvs.mapper.qf.AfProductionFeatureMapper;
import com.osh.rvs.mapper.qf.FactMaterialMapper;
import com.osh.rvs.service.partial.FactPartialReleaseService;
import com.osh.rvs.service.qf.FactMaterialService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.copy.IntegerConverter;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.JustlengthValidator;
import framework.huiqing.common.util.validator.LongTypeValidator;

public class AcceptFactService {

	private static final Integer DIVISITON_CATEGROY_KIND = 1; // 机种四分类
	private static final Integer DIVISITON_REPAIR_MANUAL_DEVICE_AND_CATEGROY_KIND = 2; // 分手动后设备中机种二分类 
	private static final Integer DIVISITON_SPAIR_MANUAL_DEVICE_AND_CATEGROY_KIND = 3; // 分手动后设备中机种四分类 

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
				boolean isManager = user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)
						|| user.getPrivacies().contains(RvsConsts.PRIVACY_LINE)
						|| user.getPrivacies().contains(RvsConsts.PRIVACY_RECEPT_EDIT);
				if (!isManager) {
					// 判断已经下班
					OperatorProductionService pfService = new OperatorProductionService();
					if (!pfService.checkFinishDayWork(user.getOperator_id(), conn)) {
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
		storedStandardFactors.put("FACT_TESTMR_PER_MAT", new BigDecimal(5)); // 维修品/备品实物测漏 FOR 102/105

		storedStandardFactors.put("SYS_RECEPT_ENSC_PER_MAT", new BigDecimal("2.5")); // 维修品/备品内窥镜系统受理 FOR 103/105
		storedStandardFactors.put("SYS_RECEPT_PERI_PER_MAT", new BigDecimal(2)); // 维修品/备品周边系统受理 FOR 103/105
		storedStandardFactors.put("SYS_RECEPT_SURGI_PER_MAT", new BigDecimal("0.5")); // 手术器械清点 FOR 105
		storedStandardFactors.put("SYS_RECEPT_ACCENA_PER_MAT", new BigDecimal("0.2")); // 附件清点 FOR 105

		storedStandardFactors.put("UPLOAD_SPARE_PER_PRO", new BigDecimal(5)); // 备品系统导入 FOR 104

		storedStandardFactors.put("FACT_RECEPT_ENSC_SPARE_PER_MAT", new BigDecimal(2)); // 备品内窥镜实物受理 FOR 105
		storedStandardFactors.put("FACT_RECEPT_PERI_SPARE_PER_MAT", new BigDecimal(5)); // 备品周边实物受理 FOR 105
		storedStandardFactors.put("FACT_RECEPT_SURGI_SPARE_PER_MAT", new BigDecimal("2.5")); // 手术器械实物受理 FOR 105
		storedStandardFactors.put("FACT_RECEPT_ACCENA_SPARE_PER_MAT", new BigDecimal("1.5")); // 备品附件实物受理 FOR 105

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

		storedStandardFactors.put("PACKAGE_PER_MAT", new BigDecimal(4)); // 维修品包装 FOR 132

//		storedStandardFactors.put("SHIPPING_PER_PRO", new BigDecimal(4)); // 维修品出货基本时间 FOR 133

		storedStandardFactors.put("SHIPPING_ENSC_CNT2_TROLLEY", new BigDecimal(12)); // 维修品出货维修品装车 FOR 133
		storedStandardFactors.put("SHIPPING_PERI_CNT2_TROLLEY", new BigDecimal(6)); // 维修品出货周边装车 FOR 133
		storedStandardFactors.put("SHIPPING_UDI_CNT2_TROLLEY", new BigDecimal(999)); // 维修品出货光学视管装车 FOR 133

		storedStandardFactors.put("SHIPPING_PER_TROLLEY", new BigDecimal(4)); // 维修品出货每车 FOR 133

		storedStandardFactors.put("INLINE_PER_MAT", new BigDecimal("1.5")); // 系统投线 FOR 201
		storedStandardFactors.put("DISTRIB_PER_PRO", new BigDecimal(20)); // 整理/运输内镜 FOR 202 (5单/车)

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

		SqlSession conn = ReverseResolution.getTempConn();
		resetStoredStandardFactors(conn);
		conn.close();
		conn = null;
	}

	public static void resetStoredStandardFactors(SqlSession conn) {
		UserDefineCodesMapper dao = conn.getMapper(UserDefineCodesMapper.class);

		/* 查询用户定义数值 */
		List<UserDefineCodesEntity> userDefineCodesEntityList = dao.searchUserDefineCodes();
		for (UserDefineCodesEntity userDefineCodesEntity : userDefineCodesEntityList) {
			if (userDefineCodesEntity.getCode().startsWith("AFST-")) {
				String code = userDefineCodesEntity.getCode().substring(5);
				if (storedStandardFactors.containsKey(code)) {
					try {
						BigDecimal newValue = new BigDecimal(userDefineCodesEntity.getValue());
						storedStandardFactors.put(code, newValue);
					} catch(NumberFormatException e) {
						// DO NOT recept
						e.printStackTrace();
					}
				}
			}
		}
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
		PartialWarehouseDetailMapper partialWarehouseDetailMapper = conn.getMapper(PartialWarehouseDetailMapper.class);
		AfProductionFeatureMapper afProductionFeatureMapper = conn.getMapper(AfProductionFeatureMapper.class);
		
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
			standardPart1 = storedStandardFactors.get("TC_OUTSTOR_TRANS_PER_PRO"); break;
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
		if (!justStart || "133".equals(productionType)) { // 刚切换时，不需要统计 // 维修品出货是以之前的作业为基准，因此刚开始就可以计算
			// 作业单位计时 part2
			switch (productionType) {
				case "101" : // 票单打印
					// 通过fact_material表取得现品票打印数量
					// 按照RVS中实际现品票单数统计
					// 乘以每单用时
					standardPart2 = calcFromFactMaterial(key, "TICKET_PER_MAT", conn);
					break;
				case "103" : // 维修品/备品系统受理
					// 按key查询af_pf时间段中完成的111工位记录数，
					// 分内窥镜、周边设备、手术器械和附件
					// 分别乘以每单用时
					// 返回合计
					standardPart2 = calcFromPositionProcess(key, "00000000009", "2", 
							new String[]{"SYS_RECEPT_ENSC_PER_MAT", "SYS_RECEPT_PERI_PER_MAT", "SYS_RECEPT_SURGI_PER_MAT", "SYS_RECEPT_ACCENA_PER_MAT"}, 
							DIVISITON_CATEGROY_KIND, conn);
					break;
				case "106" : // 镜箱入库
					// 通过fact_material表取得上架数量
					// 乘以每单镜箱上架用时
					standardPart2 = calcFromFactMaterial(key, "TC_INSTOR_ONSH_PER_MAT", conn);
					break;
				case "111" : // 维修品消毒
					// 按key查询af_pf时间段中完成的121工位记录数，
					// 分手动和设备
					// 设备中分内窥镜、周边设备
					// 分别乘以每单用时
					// 返回合计
					standardPart2 = calcFromPositionProcess(key, "00000000010", "2", 
							new String[]{"DISINF_MANUAL_PER_MAT", "DISINF_ENSC_PER_MAT", "DISINF_PERI_PER_MAT"}, 
							DIVISITON_REPAIR_MANUAL_DEVICE_AND_CATEGROY_KIND, conn);
					break;
				case "112" : // 备品消毒
					// 按key查询af_pf时间段中完成的121工位记录数，
					// 分手动和设备
					// 设备中分内窥镜、周边设备、手术器械和附件
					// 分别乘以每单用时
					// 返回合计
					standardPart2 = calcFromPositionProcess(key, "00000000010", "2", 
							new String[]{"DISINF_MANUAL_PER_MAT", "DISINF_ENSC_PER_MAT", "DISINF_PERI_PER_MAT", "DISINF_SURGI_PER_MAT", "DISINF_ACCENA_PER_MAT"}, 
							DIVISITON_SPAIR_MANUAL_DEVICE_AND_CATEGROY_KIND, conn);
					break;
				case "113" : // 镜箱消毒
					// 按key查询af_pf时间段中完成的121工位记录数，
					// 其中的镜箱
					// 乘以每单用时
					standardPart2 = calcFromPositionProcess(key, "00000000010", "5", 
							new String[]{"DISINF_TC_PER_MAT"}, 
							null, conn);
					break;
				case "131" : // 镜箱出库
					// 通过fact_material表取得下架数量
					// 乘以每单镜箱下架用时
					standardPart2 = calcFromFactMaterial(key, "TC_OUTSTOR_OFFSH_PER_MAT", conn);
					break;
				case "132" : // 维修品包装
					// 按key查询af_pf时间段中完成的711工位记录数，
					// 乘以每单用时
				{
					standardPart2 = calcFromPositionProcess(key, RvsConsts.POSITION_SHIPPING, "2", 
							new String[]{"PACKAGE_PER_MAT"}, 
							null, conn);
					BigDecimal standardPart2_1 = calcFromPositionProcess(key, RvsConsts.POSITION_ANML_SHPPING, "2", 
							new String[]{"PACKAGE_PER_MAT"}, 
							null, conn);
					standardPart2 = standardPart2.add(standardPart2_1);
					
					break;
				}
				case "133" : // 维修品出货
					// 按key查询af_pf时间，以及最近一次同样的维修品出货之间完成的711工位记录数，
					// 分内窥镜、周边设备、光学视管
					// 分别计算为车数
					// 总车数乘以每车时间
					// 返回合计
				{
					standardPart2 = calcFromPositionBetweenCloseProcesses(key, RvsConsts.POSITION_SHIPPING, "2", productionType,
							new String[]{"SHIPPING_ENSC_CNT2_TROLLEY", "SHIPPING_PERI_CNT2_TROLLEY", "SHIPPING_UDI_CNT2_TROLLEY"}, 
							"SHIPPING_PER_TROLLEY",
							null, conn);
					BigDecimal standardPart2_1 = calcFromPositionBetweenCloseProcesses(key, RvsConsts.POSITION_ANML_SHPPING, "2", productionType,
							new String[]{"SHIPPING_ENSC_CNT2_TROLLEY", "SHIPPING_PERI_CNT2_TROLLEY", "SHIPPING_UDI_CNT2_TROLLEY"}, 
							"SHIPPING_PER_TROLLEY",
							null, conn);
					standardPart2 = standardPart2.add(standardPart2_1);
					break;
				}
				case "201" : // 投线
					// 通过fact_material表取得投线系统数量
					// 乘以每单用时
					standardPart2 = calcFromFactMaterial(key, "INLINE_PER_MAT", conn);
					break;
				case "202" : // 投线运输
					// 每次整理/运输内镜时间固定
					standardPart2 = calcFromFactMaterialCountTrolley(key, "DISTRIB_PER_PRO", conn); 
					break;
				case "213" : // 零件核对/上架
					// 按零件类别统计零件数
					// 各自乘以单位核对时间
					standardPart2 = partialWarehouseDetailMapper.countCollationOnShelfStandardTime(key);
					break;
				case "214" : // 零件分装
					// 按零件类别统计分装零件数
					// 各自乘以单位分装时间
					standardPart2 = partialWarehouseDetailMapper.countUnpackStandardTime(key);
					break;
				case "221" : // 维修零件订购 fact_partial_release kind-0
					// 按key统计个人的订购单订单*修改次数
					// 乘以维修零件订购单时间
					standardPart2 = calcFromMaterialPartialRelease(key, conn);
					break;
				case "231" : // 维修零件发放
					// 1）维修零件发放派送判断
					// 1-1） 中小修理并且首工位NS ==> NS零件发放时间
					// 1-2） 中小修理并且首工位非NS ==> 分解/总组零件发放时间
					// 1-3） 大修理并且流程包含NS ==> NS零件发放 + 分解/总组零件发放时间
					// 1-4） 大修理并且流程不包含NS ==> 分解/总组零件发放时间
					BigDecimal standardPart2_1 = calcFromMaterialPartialSendLines(key, conn);
					// 2) 按零件类别统计零件数
					// 各自乘以单位下架时间
					// 返回 1） + 2）
					BigDecimal standardPart2_2 = partialWarehouseDetailMapper.countOffShelfStandardTime(key);
					standardPart2 = standardPart2_1.add(standardPart2_2);
					break;
				case "241" : // 维修出货单制作
					// 通过fact_material表取得出货单制作数量
					standardPart2 = calcFromFactMaterial(key, "SHIPPING_ORDER_PER_MAT", conn);
					break;
				case "242" : // 未修理返送
					// 通过fact_material表取得未修理返送数量
					// 乘以每单用时
					standardPart2 = calcFromFactMaterial(key, "UNREPAIR_RETURN_PER_MAT", conn);
					break;
				case "252" : // 消耗品核对/上架
					// 按消耗品上架时长统计品名数（fact_consumable_warehouse）
					// 各自乘以单位上架时间
					standardPart2 = calcFromConsumableWarehouse(key, 
							new String[]{"CSM_INSTOR_ONSH_L_PER_CD", "CSM_INSTOR_ONSH_M_PER_CD", "CSM_INSTOR_ONSH_S_PER_CD"}, conn);
					break;
				case "261" : // 消耗品出库
					// 按消耗品下架时长统计品名数（fact_consumable_warehouse）
					// 各自乘以单位下架时间
					standardPart2 = calcFromConsumableWarehouse(key, 
							new String[]{"CSM_OUTSTOR_OFFSH_L_PER_CD", "CSM_OUTSTOR_OFFSH_M_PER_CD", "CSM_OUTSTOR_OFFSH_S_PER_CD"}, conn);
					break;
				case "271" : // 固定件清洗
					// 按key查询af_pf时间段中`steel_wire_container_wash_process` process_time记录数，
					// 乘以单位固定件清洗时间
					Integer cn = afProductionFeatureMapper.countSWCWash(key,1);
					BigDecimal factor = storedStandardFactors.get("SWC_WASH_PER_CD");
					
					standardPart2 = factor.multiply(new BigDecimal(cn));
					break;
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
	 * 按维修对象发送表查询数量
	 */
	private BigDecimal calcFromMaterialPartialRelease(String key,
		SqlSession conn) {
		BigDecimal bdRet = new BigDecimal(0);
		FactPartialReleaseMapper mapper = conn.getMapper(FactPartialReleaseMapper.class);
		Integer sumPartialOrderEdit = mapper.countPartialOrderEditInAfpf(key);
		if (sumPartialOrderEdit != null) {
			bdRet = new BigDecimal(sumPartialOrderEdit)
				.multiply(storedStandardFactors.get("PART_ORDER_PER_MAT"));
		}
		return bdRet;
	}

	/**
	 * 按维修对象发送工程计算工时
	 * @param key
	 * @param conn
	 * @return
	 */
	private BigDecimal calcFromMaterialPartialSendLines(String key,
			SqlSession conn) {
		AfProductionFeatureMapper mapper = conn.getMapper(AfProductionFeatureMapper.class);
		BigDecimal bdRet = new BigDecimal(0);

		// 取得fact_partial_release表中相应发送掉的中小修理
		List<AfProductionFeatureEntity> listMinor = mapper.countMinorOnNsProceed(key);
		for (AfProductionFeatureEntity minor : listMinor) {
			if (minor.getDivision() == 1) {
				// division = 1 先到 NS
				// 1-1） 中小修理并且首工位NS ==> NS零件发放时间
				bdRet = bdRet.add(storedStandardFactors.get("PART_RELEASE_NS_PER_MAT")
						.multiply(new BigDecimal(minor.getCnt())));
			} else if (minor.getDivision() == 0) {
				// division = 0 先到 NS 以外
				// 1-2） 中小修理并且首工位非NS ==> 分解/总组零件发放时间
				bdRet = bdRet.add(storedStandardFactors.get("PART_RELEASE_NO_NS_PER_MAT")
						.multiply(new BigDecimal(minor.getCnt())));
			}
		}
		
		// 取得fact_partial_release表中相应发送掉的大修理
		List<AfProductionFeatureEntity> listMajor = mapper.countMajorOnNsProceed(key);
		for (AfProductionFeatureEntity major : listMajor) {
			if (major.getDivision() == 1) {
				// division = 1 有 NS
				// 1-3） 大修理并且流程包含NS ==> NS零件发放 + 分解/总组零件发放时间
				bdRet = bdRet.add(storedStandardFactors.get("PART_RELEASE_NS_PER_MAT")
						.add(storedStandardFactors.get("PART_RELEASE_NO_NS_PER_MAT"))
						.multiply(new BigDecimal(major.getCnt())));
			} else if (major.getDivision() == 0) {
				// division = 0 无 NS
				// 1-4） 大修理并且流程不包含NS ==> 分解/总组零件发放时间
				bdRet = bdRet.add(storedStandardFactors.get("PART_RELEASE_NO_NS_PER_MAT")
						.multiply(new BigDecimal(major.getCnt())));
			}
		}

		return bdRet;
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
	 * 按现品作业表来统计
	 * @param key
	 * @param string
	 * @return
	 */
	private BigDecimal calcFromFactMaterialCountTrolley(String key, String factorName, SqlSession conn) {

		FactMaterialMapper mapper = conn.getMapper(FactMaterialMapper.class);
		Integer count = mapper.countByTrolley(key);

		if (count == null) return BigDecimal.ZERO;

		BigDecimal factor = storedStandardFactors.get(factorName);

		return factor.multiply(new BigDecimal(count));
	}

	/**
	 * 按完成工位来统计
	 * @param key 间接人员作业记录主键
	 * @param position_id
	 * @param operate_result
	 * @param factorNames
	 * @param cond_division
	 * @param conn
	 * @return
	 */
	private BigDecimal calcFromPositionProcess(String key, String position_id,
			String operate_result, String[] factorNames, Integer cond_division, SqlSession conn) {
		AfProductionFeatureMapper mapper = conn.getMapper(AfProductionFeatureMapper.class);
		AfProductionFeatureEntity condition = new AfProductionFeatureEntity();
		condition.setAf_pf_key(key);
		condition.setPosition_id(position_id);
		condition.setOperate_result(operate_result);
		condition.setDivision(cond_division);

		List<AfProductionFeatureEntity> results = mapper.countPositionProcessBetweenAfProcess(condition);

		BigDecimal calc = new BigDecimal(0);

		for (AfProductionFeatureEntity result : results) {
			int division = result.getDivision();
			BigDecimal factor = storedStandardFactors.get(factorNames[division]);
			calc = calc.add(factor.multiply(new BigDecimal(result.getCnt())));
		}

		return calc;
	}

	/**
	 * 按完成工位来统计
	 * @param key 间接人员作业记录主键
	 * @param position_id
	 * @param operate_result
	 * @param productionType 
	 * @param loadNames
	 * @param factorName 
	 * @param cond_division
	 * @param conn
	 * @return
	 */
	private BigDecimal calcFromPositionBetweenCloseProcesses(String key, String position_id,
			String operate_result, String productionType, String[] loadNames, String factorName, Integer cond_division, SqlSession conn) {
		AfProductionFeatureMapper mapper = conn.getMapper(AfProductionFeatureMapper.class);
		AfProductionFeatureEntity condition = new AfProductionFeatureEntity();
		condition.setAf_pf_key(key);
		condition.setProduction_type(Integer.parseInt(productionType));
		condition.setPosition_id(position_id);
		condition.setOperate_result(operate_result);
		condition.setDivision(cond_division);

		List<AfProductionFeatureEntity> results = mapper.countPositionProcessBetweenCloseAfProcesses(condition);

		BigDecimal calc = new BigDecimal(0);

		for (AfProductionFeatureEntity result : results) {
			int division = result.getDivision();
			BigDecimal load = storedStandardFactors.get(loadNames[division]);
			BigDecimal factor = storedStandardFactors.get(factorName);
			calc = calc.add(new BigDecimal(result.getCnt())
					.divide(load, 0, BigDecimal.ROUND_UP)
					.multiply(factor));
		}

		return calc;
	}

	/**
	 * 按消耗品作业记录来统计
	 * @param key 间接人员作业记录主键
	 * @param factorNames
	 * @param conn
	 * 
	 */
	private BigDecimal calcFromConsumableWarehouse(String key, String[] factorNames, SqlSession conn) {
		AfProductionFeatureMapper mapper = conn.getMapper(AfProductionFeatureMapper.class);

		List<AfProductionFeatureEntity> results = mapper.countConsumableWarehouseOfAfProcess(key);

		BigDecimal calc = new BigDecimal(0);

		for (AfProductionFeatureEntity result : results) {
			int division = result.getDivision();
			BigDecimal factor = storedStandardFactors.get(factorNames[division - 1]);
			calc = calc.add(factor.multiply(new BigDecimal(result.getCnt())));
		}

		return calc;
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
		if (toWorking) {
			Integer standard_minutes = getStandardMinutes(retForm, conn, true);
			if (standard_minutes != null) {
				retForm.setStandard_minutes("" + standard_minutes);
			}
		} else {
			retForm.setStandard_minutes(null);
		}

		return retForm;
	}

	/**
	 * 结束计时
	 * @param form
	 * @param user
	 * @param conn
	 * @throws Exception
	 */
	public void end(ActionForm form, LoginData user, SqlSessionManager conn) throws Exception {
		// 操作者 ID
		String operator_id = user.getOperator_id();

		boolean isManager = user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)
				|| user.getPrivacies().contains(RvsConsts.PRIVACY_LINE)
				|| user.getPrivacies().contains(RvsConsts.PRIVACY_RECEPT_EDIT);
		// 是管理员, 只结束不开始下一个
		if (isManager) {
			switchTo(null, null, null, operator_id, false, conn);
		} else {
			// 操作人员，将间隔时间的结束时间延长到下班OR加班时段定时
			OperatorProductionService opService = new OperatorProductionService();
			Calendar now = Calendar.getInstance();

			// 自动结束时间
			Date autoFinishTime = opService.getAutoFinishTime(now);
			// 更新当前间隔结束于自动
			opService.assertFinishPauseFeature(operator_id, autoFinishTime, conn);
		}

	}

	/**
	 * 切换作业
	 * @param from_working 当前是TRUE=直接作业；FALSE=其他；NULL=不明
	 * @param to_working 要切换到TRUE=直接作业；FALSE=其他；NULL=没有下一个记录时间
	 * @param production_type 作业内容
	 * @param operator_id 操作者 ID
	 * @param check_status 判断是否已经是需要切换的状态了
	 * @param conn
	 * @throws Exception 
	 */
	public Date switchTo(Boolean from_working, Boolean to_working, String production_type, 
			String operator_id, boolean check_status, SqlSessionManager conn) throws Exception {
		if (operator_id == null || (to_working != null && production_type == null)) return null;

		AfProductionFeatureMapper apfMapper = conn.getMapper(AfProductionFeatureMapper.class);
		PauseFeatureService pfService = new PauseFeatureService();

		if (from_working == null) {
			// 取得操作者当前进行中作业
			AfProductionFeatureEntity apfEntity = apfMapper.getUnfinishByOperator(operator_id);
			from_working = (apfEntity != null);

			if (to_working != null && to_working && from_working && check_status) {
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
		if (to_working != null) {
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
	public AfProductionFeatureEntity getUnspecUnfinishByType(int production_type, SqlSession conn) {
		AfProductionFeatureMapper dao = conn.getMapper(AfProductionFeatureMapper.class);
		List<AfProductionFeatureEntity> results = dao.getUnfinishByType(production_type);
		for (AfProductionFeatureEntity result : results) {
			if ("00000000000".equals(result.getOperator_id())) {
				return result;
			}
		}
		return null;
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

	/**
	 * 切换作业(无可写连接时)
	 * @param production_type
	 * @param user
	 * @throws Exception 
	 */
	public void switchWorking(String production_type, LoginData user) throws Exception {

		// 操作者 ID
		String operator_id = user.getOperator_id();

		SqlSessionManager writableConn = RvsUtils.getTempWritableConn();
		try {
			writableConn.startManagedSession();
			// 切换作业
			AcceptFactService afService = new AcceptFactService();
			Date switchedStart = afService.switchTo(null, true, production_type, operator_id, true, writableConn);
			if (switchedStart != null) {
				writableConn.commit();
				fingerOperatorRefresh(user.getOperator_id());
			}
		} catch (Exception e) {
			if (writableConn!= null && writableConn.isManagedSessionStarted()) {
				writableConn.rollback();
			}
		} finally {
			try {
				writableConn.close();
			} catch (Exception e) {
			} finally {
				writableConn = null;
			}
		}
	}

	/**
	 * 当前作业者当天所有维修品零件订单编辑记录
	 * @param operator_id
	 * @param conn
	 * @return
	 */
	public List<FactPartialReleaseEntity> getTodayPartialOrderEditBySelf(String operator_id,
			SqlSession conn) {
		FactPartialReleaseMapper fprMapper = conn.getMapper(FactPartialReleaseMapper.class);
		List<FactPartialReleaseEntity> ret = fprMapper.getTodayPartialOrderEdit(operator_id);
		for (FactPartialReleaseEntity entity : ret) {
			Integer level = entity.getLevel();
			if (level != null) {
				entity.setLevel_name(CodeListUtils.getValue("material_level", "" + level));
			}
		}
		return ret;
	}

	/**
	 * SAP传送的未分配到人所有维修品零件订单编辑记录
	 * @param operator_id
	 * @return
	 */
	public List<FactPartialReleaseEntity> getTodayPartialOrderEditFromSap(SqlSession conn) {
		FactPartialReleaseMapper fprMapper = conn.getMapper(FactPartialReleaseMapper.class);
		List<FactPartialReleaseEntity> ret = fprMapper.getTodayPartialOrderEdit("00000000000");
		for (FactPartialReleaseEntity entity : ret) {
			Integer level = entity.getLevel();
			if (level != null) {
				entity.setLevel_name(CodeListUtils.getValue("material_level", "" + level));
			}
		}
		return ret;
	}

	/**
	 * 维修对象订购记录更新
	 * 
	 * @param omr_notifi_no
	 * @param user
	 * @param errors 
	 * @param conn
	 */
	public boolean editPartialOrder(String omr_notifi_no, LoginData user,
			List<MsgInfo> errors, SqlSessionManager conn) {
		MaterialMapper mMapper = conn.getMapper(MaterialMapper.class);
		MaterialEntity mEntity = mMapper.loadMaterialByOmrNotifiNo(omr_notifi_no);

		if (mEntity == null) {
			// 维修对象不存在
			MsgInfo error = new MsgInfo();
			error.setComponentid("omr_notifi_no");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "维修对象"));
			errors.add(error);
			return false;
		} else {
			if (mEntity.getOutline_time() != null) {
				// 维修对象已不在线
				MsgInfo error = new MsgInfo();
				error.setComponentid("omr_notifi_no");
				error.setErrcode("info.linework.outLine");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.outLine"));
				errors.add(error);
				return false;
			} else if (mEntity.getFix_type() == null || mEntity.getFix_type() != 1) {
				// 不是维修对象
				MsgInfo error = new MsgInfo();
				error.setComponentid("omr_notifi_no");
				error.setErrcode("info.material.isNotForRepairItem");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.material.isNotForRepairItem"));
				errors.add(error);
				return false;
			}
		}

		AfProductionFeatureEntity workingAfpf = getUnFinishEntity(user.getOperator_id(), conn);

		FactPartialReleaseService fprService = new FactPartialReleaseService();
		fprService.updatePartialOrderEdit(workingAfpf.getAf_pf_key(), user.getOperator_id(), mEntity.getMaterial_id(), conn);

		return true;
	}

	/**
	 * 检查扫描维修对象合法性
	 * @param material_id
	 * @param errors
	 * @param conn
	 */
	public MaterialEntity checkMaterialId(String material_id, List<MsgInfo> errors, SqlSession conn) {
		MaterialEntity materialEntity = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		if (material_id.contains("_")) {
			String[] split = material_id.split("_");
			material_id = split[0];
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
		
		if(errors.size() == 0){
			MaterialMapper materialMapper = conn.getMapper(MaterialMapper.class);
			materialEntity = materialMapper.getMaterialNamedEntityByKey(material_id);
			
			if(materialEntity == null){
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("material_id");
				msgInfo.setErrcode("info.linework.invalidCode");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.invalidCode"));
				errors.add(msgInfo);
			}
		}
		
		return materialEntity;
	}
}

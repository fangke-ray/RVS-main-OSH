package com.osh.rvs.service;

import static framework.huiqing.common.util.CommonStringUtil.joinBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.MaterialProcessEntity;
import com.osh.rvs.bean.master.LineEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.ProcessAssignEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.ProductionFeatureForm;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.inline.LeaderPcsInputMapper;
import com.osh.rvs.mapper.inline.MaterialProcessAssignMapper;
import com.osh.rvs.mapper.inline.ProductionAssignMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.master.PositionMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.service.inline.ForSolutionAreaService;
import com.osh.rvs.service.proxy.ProcessAssignProxy;

import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ProductionFeatureService {
	protected static final Logger logger = Logger.getLogger("Production");

	public void insert(ProductionFeatureEntity entity, SqlSession conn) throws Exception{
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		dao.insertProductionFeature(entity);
	}

	public void insertAcceptance(ProductionFeatureEntity entity, SqlSession conn){
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		dao.insertAcceptanceProductionFeature(entity);
	}

	public int checkOperateResult(String materialId, SqlSession conn) {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		return dao.checkOperateResult(materialId);
	}

	public List<ProductionFeatureForm> getProductionFeatureByMaterialId(ActionForm from, SqlSession conn) {
		ProductionFeatureEntity entity = new ProductionFeatureEntity();
		BeanUtil.copyToBean(from, entity, null);
		
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		List<ProductionFeatureEntity> list = dao.getProductionFeatureByMaterialId(entity);
		
		List<ProductionFeatureForm> listForm = new ArrayList<ProductionFeatureForm>();
		BeanUtil.copyToFormList(list, listForm, null, ProductionFeatureForm.class);
		
		return listForm;
	}
	
	public List<ProductionFeatureForm> getNoBeforeRework(String id, SqlSession conn) {
		List<ProductionFeatureForm> listForm = new ArrayList<ProductionFeatureForm>();
		
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		List<ProductionFeatureEntity> list = dao.getNoBeforeRework(id);
		
		BeanUtil.copyToFormList(list, listForm, null, ProductionFeatureForm.class);
		
		return listForm;
	}
	
	public List<ProductionFeatureForm> getFinishProductionFeature(String id, SqlSession conn) {
		List<ProductionFeatureForm> listForm = new ArrayList<ProductionFeatureForm>();
		
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		List<ProductionFeatureEntity> list = dao.getFinishProductionFeature(id);
		
		BeanUtil.copyToFormList(list, listForm, null, ProductionFeatureForm.class);
		
		return listForm;
	}
	

	/**
	 * 作业中发生暂停后，产生作业下一步暂停等待信息
	 * 作业中发生中断后恢复，产生作业下一步暂停等待信息
	 * @param productionFeature
	 * @param conn
	 */
	public void pauseToNext(ProductionFeatureEntity productionFeature, SqlSession conn){
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

		// Pace + 1
		productionFeature.setPace(productionFeature.getPace() + 1);
		// 还没有处理者
		productionFeature.setOperator_id(null);
		// 状态总是暂停再开
		productionFeature.setOperate_result(RvsConsts.OPERATE_RESULT_PAUSE);
		productionFeature.setAction_time(null);
		productionFeature.setFinish_time(null);
		// 其他沿用

		// 作成新等待记录
		dao.insertProductionFeature(productionFeature);
	}


	/**
	 * 作业中发生暂停后，产生作业下一步暂停进行信息
	 * @param productionFeature
	 * @param conn
	 */
	public void pauseToSelf(ProductionFeatureEntity productionFeature, SqlSessionManager conn) {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

		// Pace + 1
		Integer newPace = productionFeature.getPace() + 1;

		productionFeature.setPace(newPace);
		// 继承处理者
		// 状态总是暂停再开
		productionFeature.setOperate_result(RvsConsts.OPERATE_RESULT_PAUSE);

		productionFeature.setAction_time(getFirstPaceOnRework(productionFeature, conn).getAction_time());
		productionFeature.setFinish_time(null);
		// 其他沿用

		// 作成新等待记录
		dao.insertProductionFeature(productionFeature);
	}

	/**
	 * 取到本次返工中，？
	 * @param currentPace
	 * @param conn
	 * @return
	 */
	public ProductionFeatureEntity getFirstPaceOnRework(ProductionFeatureEntity currentPace, SqlSession conn) {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

		// 设定为延续开始时间
		ProductionFeatureEntity firstPf = new ProductionFeatureEntity();
		firstPf.setMaterial_id(currentPace.getMaterial_id());
		firstPf.setPosition_id(currentPace.getPosition_id());
		firstPf.setSection_id(currentPace.getSection_id());
		firstPf.setRework(currentPace.getRework());
		firstPf.setPace(0);
		List<ProductionFeatureEntity> firstPfs = dao.searchProductionFeature(firstPf);
		if (firstPfs ==  null || firstPfs.size() == 0) {
			logger.error("No pace 0");
			//firstPf = new Date();
		} else {
			firstPf = firstPfs.get(0);
		}
		return firstPf;
	}

	/**
	 * 作业中发生中断后，产生中断等待信息
	 * @param productionFeature
	 * @param conn
	 */
	public void breakToNext(ProductionFeatureEntity productionFeature, SqlSession conn){
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

		// Pace
		productionFeature.setPace(productionFeature.getPace() + 1);
		// 保持处理者
		// productionFeature.setOperator_id(null);
		// 状态总是暂停再开
		productionFeature.setOperate_result(RvsConsts.OPERATE_RESULT_BREAK);
		productionFeature.setAction_time(null);
		productionFeature.setFinish_time(null);
		// 其他沿用

		// 作成新等待记录
		dao.insertProductionFeature(productionFeature);
	}

	/**
	 * 发动工位
	 * @param material_id
	 * @param workingPf
	 * @param conn
	 * @throws Exception 
	 * @return 是否发动
	 */
	public void fingerPosition(String fromPositionId, MaterialEntity mEntity, boolean fixed,
			ProductionFeatureEntity workingPf, SqlSessionManager conn, ProductionFeatureMapper pfDao, ProcessAssignProxy paProxy, List<String> retList, List<String> triggerList) throws Exception {
		fingerPosition(fromPositionId, mEntity, fixed, workingPf, conn, pfDao, paProxy, retList, triggerList, true);
	}
	public void fingerPosition(String fromPositionId, MaterialEntity mEntity, boolean fixed,
			ProductionFeatureEntity workingPf, SqlSessionManager conn, ProductionFeatureMapper pfDao, ProcessAssignProxy paProxy, List<String> retList, List<String> triggerList, boolean isFact) throws Exception {

		if (retList == null) retList = new ArrayList<String> ();

		String material_id = mEntity.getMaterial_id();
		String fingerPositionId = workingPf.getPosition_id();
		// 维修对象的课室
		String section_id = paProxy.getMaterial_section_id();
		if (section_id == null) { // (投线前) 
			section_id = workingPf.getSection_id();
		}
//		if ("00000000025".equals(position_id) 
//				|| "00000000060".equals(position_id) 
//				|| "25".equals(position_id) 
//				|| "60".equals(position_id)) {
//			section_id = "00000000003"; // 302 301 工位固定2课
//		} else if (paProxy.isLightFix &&
//				("00000000031".equals(position_id) 
//					|| "31".equals(position_id))) {
//			section_id = "00000000001"; 
//		} // 小修理361工位自动到1课

		if (fixed) { 
			// 固定工位生成等待区信息
			ProductionFeatureEntity entity = new ProductionFeatureEntity();
			entity.setMaterial_id(material_id);
			entity.setPosition_id(fingerPositionId);
			entity.setPace(0);
			entity.setSection_id(section_id);
			entity.setOperate_result(getPutinOperateResult(fromPositionId, fingerPositionId, conn));
			entity.setRework(workingPf.getRework());
			pfDao.insertProductionFeature(entity);

			if (isFact && triggerList!=null) {
				// 通知
				triggerList.add("http://localhost:8080/rvspush/trigger/in/" + fingerPositionId + "/"
		            		+ section_id + "/" + material_id + "/" + (paProxy.isLightFix ? "1" : "0"));

				// 小修理不判断
				if (!paProxy.isLightFix) {
					checkFso(material_id, fingerPositionId, triggerList, conn);
				}
			}
			retList.add(fingerPositionId);

		} else {
			// 判断得到的工程是否有完成

// logger.error(position_id + "<EEE>" + paDao.checkWorking(material_id, position_id));
			// 有完成，并且其先决也已完成，则由这个工位继续触发
			if (paProxy.checkWorked(fingerPositionId)) {
				// 取得先决
				List<String> prevPositions = new ArrayList<String>();

				getPrev(paProxy, material_id, mEntity.getPat_id(), fingerPositionId, mEntity.getLevel(), prevPositions);

				if (prevPositions.size() == 0 || !isFact 
						|| paProxy.getFinishedCountByPositions(prevPositions) == prevPositions.size()) {
					workingPf.setPosition_id(fingerPositionId);
					List<String> x = fingerNextPosition(material_id, workingPf, conn, triggerList, isFact);
					if (x!=null) {
						retList.addAll(x);
					}
				}
			}
			// 判断得到的工程是否有未完成。
			else if (paProxy.checkWorking(fingerPositionId) > 0) {
				// 有的话则不影响原有工作
				logger.info(fingerPositionId+"工位进行中。");
			}
			// 没有完成则判断先决的工位是否都已经结束。
			else {
				// 取得先决
				List<String> prevPositions = new ArrayList<String>();

				getPrev(paProxy, material_id, mEntity.getPat_id(), fingerPositionId, mEntity.getLevel(), prevPositions);

				logger.info(fingerPositionId+"工位de先决："+prevPositions.size());

				Map<String, Object> params = new HashMap<String, Object>();
				params.put("material_id", material_id);
				params.put("position_ids", prevPositions);

				if (prevPositions.size() == 0 || !isFact
						|| paProxy.getFinishedCountByPositions(prevPositions) == prevPositions.size()) {
					// 都已经结束生成等待区信息
					logger.info("维修对象"+material_id+"进行至"+fingerPositionId+"工位。( " + isFact + ")");
					ProductionFeatureEntity entity = new ProductionFeatureEntity();
					entity.setMaterial_id(material_id);
					entity.setPosition_id(fingerPositionId);
					entity.setPace(0);
					entity.setSection_id(section_id);
					entity.setOperate_result(getPutinOperateResult(fromPositionId, fingerPositionId, conn));

					Integer neoRework = workingPf.getRework();

					if (prevPositions.size() > 1) { // 取得先决的最大Rework
						int rework = pfDao.getReworkCountWithPositions(params);
						if (rework > neoRework) neoRework = rework;
					}
					entity.setRework(neoRework);
					pfDao.insertProductionFeature(entity);

					if (isFact) {
						// 通知
						triggerList.add("http://localhost:8080/rvspush/trigger/in/" + fingerPositionId + "/" 
				            		+ section_id + "/" + material_id + "/" + (paProxy.isLightFix ? "1" : "0"));
						checkFso(material_id, fingerPositionId, triggerList, conn);
					}

					logger.info("tranover");
					retList.add(fingerPositionId);
				}
			}
		}
	}

	private void checkFso(String material_id, String position_id,
			List<String> triggerList, SqlSessionManager conn) throws Exception {
		// 检查工位上BO零件
		ForSolutionAreaService fsoService = new ForSolutionAreaService();
		String bo_partials = "";
		MaterialPartialMapper mpMapper = conn.getMapper(MaterialPartialMapper.class);

		// 400工位则判断全工程内是否有BO
		if ("00000000032".equals(position_id) || "32".equals(position_id)) {
			bo_partials = mpMapper.getBoPartialOfLineOfPosition(material_id, position_id);
		} else if ("00000000021".equals(position_id) || "21".equals(position_id)
				|| "00000000022".equals(position_id) || "22".equals(position_id)
				|| "00000000027".equals(position_id) || "27".equals(position_id)
				|| "00000000028".equals(position_id) || "28".equals(position_id)) { // TODO 允许进入工位
		}
		else
			bo_partials = mpMapper.getBoPartialOfPosition(material_id, position_id);

		if (!CommonStringUtil.isEmpty(bo_partials)) {
			fsoService.create(material_id, bo_partials, 1, position_id, conn, true);

//			PositionMapper pMapper = conn.getMapper(PositionMapper.class);
//			PositionEntity pEntity = pMapper.getPositionByID(position_id);
//			String line_id = pEntity.getLine_id();
//
//			triggerList.add("http://localhost:8080/rvspush/trigger/delete_finish_time/"
//					+ material_id + "/" + line_id);
//
//			if (!"00000000014".equals(line_id)) {
//				triggerList.add("http://localhost:8080/rvspush/trigger/delete_finish_time/"
//						+ material_id + "/" + "00000000014");
//			}
		}
	}

	private Integer getPutinOperateResult(String prePositionId, String nextPositionId, SqlSession conn) {
		// 虚拟组工位后续工位
		prePositionId = CommonStringUtil.fillChar(prePositionId, '0', 11, true);
		nextPositionId = CommonStringUtil.fillChar(nextPositionId, '0', 11, true);
		Map<String, String> groupNextPositions = PositionService.getGroupNextPositions(conn);
		if (groupNextPositions.containsKey(nextPositionId)
				&& groupNextPositions.get(nextPositionId).equals(prePositionId)) {
			return RvsConsts.OPERATE_RESULT_BATCHWORKING;
		} else {
			return RvsConsts.OPERATE_RESULT_NOWORK_WAITING;
		}
	}

	/** 判断线长点检 */
	public String checkLpi(String id, String process_code, SqlSession conn) {
		LeaderPcsInputMapper lpiMapper = conn.getMapper(LeaderPcsInputMapper.class);

		switch (process_code) {
		case "301" : 
			return lpiMapper.checkSnoutConfirm(id);
		case "302" : 
			return lpiMapper.checkCcdConfirm(id);
		}
		return null;
	}

	public int getReworkCountWithLine(String material_id, String line_id, SqlSession conn) {
		ProductionFeatureMapper pfDao = conn.getMapper(ProductionFeatureMapper.class);

		return pfDao.getReworkCountWithLine(material_id, line_id);
	}

	public boolean checkPositionDid(String material_id, String position_id, String operate_result, String rework, SqlSession conn) {
		ProductionFeatureMapper ppDao = conn.getMapper(ProductionFeatureMapper.class);
		return ppDao.checkPositionDid(material_id, position_id, operate_result, rework);
	}

	/**
	 * 发动下一个工位
	 * @param material_id
	 * @param workingPf
	 * @param conn
	 * @return 
	 * @throws Exception 
	 */
	public List<String> fingerNextPosition(String material_id, ProductionFeatureEntity workingPf, SqlSessionManager conn, List<String> triggerList) throws Exception {
		return fingerNextPosition(material_id, workingPf, conn, triggerList, true);
	}
	public List<String> fingerNextPosition(String material_id, ProductionFeatureEntity workingPf, SqlSessionManager conn, List<String> triggerList, boolean isFact) throws Exception {

		// 为了reset工位consts
		PositionService.getPositionUnitizeds(conn);

		MaterialProcessService mpService = new MaterialProcessService();
		// 发动工位
		List<String> nextPositions = new ArrayList<String>();

		MaterialMapper mDao = conn.getMapper(MaterialMapper.class);
		MaterialEntity mEntity = mDao.getMaterialNamedEntityByKey(material_id);

		Integer level = mEntity.getLevel();
//		boolean isLightFix = (level != null) &&
//				(level == 9 || level == 91 || level == 92 || level == 93);
		boolean isLightFix = RvsUtils.isLightFix(level);

		String position_id = workingPf.getPosition_id();
		String pat_id = mEntity.getPat_id(); // 维修流程主键

		ProcessAssignProxy paProxy = new ProcessAssignProxy(material_id, pat_id, mEntity.getSection_id(), isLightFix, conn);

		boolean fixed = true;
	
		// 固定流程工位 (有特殊界面的不列在内)
		if ("00000000010".equals(position_id) || "00000000011".equals(position_id)) { // 消毒灭菌
			if (mEntity.getFix_type() == 3 || mEntity.getFix_type() == 4) {
				// outline_time设成当前时间（业务处理全部结束了）
				mDao.updateMaterialOutlineTime(material_id);
				return null;
			} else {
				if ("07".equals(mEntity.getKind()) && mEntity.getFix_type() == 1) {
					nextPositions.add(RvsConsts.POSITION_QUOTATION_P_181); // 周边报价
				} else if (RvsConsts.CATEGORY_UDI.equals(mEntity.getCategory_id())) { // 光学视管
					boolean anml_flg = MaterialTagService.getAnmlMaterials(conn).contains(workingPf.getMaterial_id());
					if (anml_flg) {
						nextPositions.add(RvsConsts.POSITION_ANML_QUOTAION); // 报价
					} else {
						nextPositions.add(RvsConsts.POSITION_QUOTATION_N); // 报价
					}
				} else {
					nextPositions.add("00000000012");
				}
				if (isFact) {
					// FSE 数据同步
					try{
						FseBridgeUtil.toUpdateMaterialProcess(material_id, "fr" + workingPf.getProcess_code());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if ("00000000012".equals(position_id)) { // 测漏

			boolean anml_flg = MaterialTagService.getAnmlMaterials(conn).contains(workingPf.getMaterial_id());

			if (mEntity.getDirect_flg()!=null && 1 == mEntity.getDirect_flg()) {
				nextPositions.add("00000000101"); // IISE确认
			} else if (anml_flg) {
				nextPositions.add(RvsConsts.POSITION_ANML_QUOTAION); // 报价
			} else if ((mEntity.getService_repair_flg()!=null && (mEntity.getService_repair_flg() == 1 || mEntity.getService_repair_flg() == 2))) {
				nextPositions.add(RvsConsts.POSITION_QUOTATION_D); // 直送报价
			} else {
				nextPositions.add(RvsConsts.POSITION_QUOTATION_N); // 报价
			}
		} else if ("00000000101".equals(position_id)) { // IISE

			boolean anml_flg = MaterialTagService.getAnmlMaterials(conn).contains(workingPf.getMaterial_id());

			if (anml_flg) {
				nextPositions.add(RvsConsts.POSITION_ANML_QUOTAION); // 报价
			} else {
				nextPositions.add("00000000014"); // 直送报价
			}
		} else if (RvsConsts.POSITION_ANML_QA.equals(position_id) || RvsConsts.POSITION_ANML_QA_UDI.equals(position_id)) { // 品保
			nextPositions.add(RvsConsts.POSITION_ANML_SHPPING); // 出货
		} else if ("00000000046".equals(position_id) || "00000000052".equals(position_id) 
				|| RvsConsts.POSITION_QA_P_613.equals(position_id) || RvsConsts.POSITION_QA_P_614.equals(position_id)) { // 品保
			nextPositions.add(RvsConsts.POSITION_SHIPPING);
		} else if ("00000000015".equals(position_id)
				|| RvsConsts.POSITION_PERP_UNREPIAR.equals(position_id)) { // 图像检查 OR 周边不修理返还
			if (mEntity.getBreak_back_flg() != null && mEntity.getBreak_back_flg() == 2) { // 未修理返还
				nextPositions.add(RvsConsts.POSITION_SHIPPING); // 出货
			}
		} else if (!isLightFix 
				&& ("00000000025".equals(position_id) || "00000000060".equals(position_id))) { //CCD or LG
			// 已投线
			if (mEntity.getInline_time() != null) {
				// 确认已经是否完成作业
				ProductionFeatureMapper ppDao = conn.getMapper(ProductionFeatureMapper.class);
				if (!ppDao.checkPositionDid(material_id, "00000000016", ""+RvsConsts.OPERATE_RESULT_FINISH, null)) {
					nextPositions.add("00000000016"); // 分解
				}
			}
			
//			// CCD 更换线长确认移到此处
//			if (isFact && "00000000025".equals(position_id)) {
//				ProductionFeatureService pfService = new ProductionFeatureService();
//				String sGot = pfService.checkLpi(material_id, "302", conn);
//				if (sGot == null) {
//					conn.rollback();
//					throw new Exception(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.uncheckedCcd"));
//				}
//			}

		} else { // 维修流程上

			Set<String> lastFlowPositions = ProcessAssignService.getLastFLowPositions(conn);

			if ("00000000020".equals(position_id)) { // 零件订购
// 2期进行后取消 2014/1/9
//				//if (isFact) {
//					// 如果没有零件BO信息
//					MaterialPartialEntity entity = new MaterialPartialEntity();
//					entity.setMaterial_id(material_id);
//					MaterialPartialMapper mpDao = conn.getMapper(MaterialPartialMapper.class);
//					List<MaterialPartialEntity> lResultBean = mpDao.searchMaterialByKey(material_id);
//	
//					// 就新建BO信息
//					if (lResultBean.size() == 0) {
//						mpDao.insertMaterialPartial(entity);
//					}
//				//}
			} else

			if (!isLightFix && (
					lastFlowPositions.contains(position_id)
//					"00000000041".equals(position_id) || "00000000045".equals(position_id) 
//					|| "00000000049".equals(position_id) || "00000000050".equals(position_id) 
//					|| RvsConsts.POSITION_811.equals(position_id) || RvsConsts.POSITION_470.equals(position_id)
//					|| RvsConsts.POSITION_461.equals(position_id) || RvsConsts.POSITION_481.equals(position_id)
//					|| RvsConsts.POSITION_462.equals(position_id) || RvsConsts.POSITION_572.equals(position_id)
					)
				) { // 总组Over TODO
				if (isFact) {
					// 检查是否存在未签收的零件
					MaterialPartialMapper mpDao = conn.getMapper(MaterialPartialMapper.class);
					List<MaterialPartialDetailEntity> wP = mpDao.searchWaitingMaterialPartialDetail(material_id, null);
					if (wP.size() > 0) {
						conn.rollback();
						throw new Exception(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.lineWaiting", wP.get(0).getPartial_code() + "等" + wP.size()));
					}
				}

				if (isFact) {

					mpService.finishMaterialProcess(material_id, "00000000014", triggerList, conn);

					// FSE 数据同步
					try{
						FseBridgeUtil.toUpdateMaterialProcess(material_id, "COM");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else
			if (!isLightFix && "00000000031".equals(position_id)) { // NSOver TODO
				if (isFact) {
					mpService.finishMaterialProcess(material_id, "00000000013", triggerList, conn);

					// 检查是否存在未签收的零件
					MaterialPartialMapper mpDao = conn.getMapper(MaterialPartialMapper.class);
					List<MaterialPartialDetailEntity> wP = mpDao.searchWaitingMaterialPartialDetail(material_id, "00000000013");
					if (wP.size() > 0) {
						conn.rollback();
						throw new Exception(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.lineWaiting", wP.get(0).getPartial_code() + "等" + wP.size()));
					}
					// FSE 数据同步
					try{
						FseBridgeUtil.toUpdateMaterialProcess(material_id, "NS");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else
//			if (!isLightFix && "00000000022".equals(position_id)) { // 试图触发400 不要5.18
//				nextPositions.add("00000000032");
//			} else
			if (!isLightFix && "00000000110".equals(position_id)) { // fenjieOver TODO
				if (isFact) {
					mpService.finishMaterialProcess(material_id, "00000000012", triggerList, conn);

					// 检查是否存在未签收的零件
					MaterialPartialMapper mpDao = conn.getMapper(MaterialPartialMapper.class);
					List<MaterialPartialDetailEntity> wP = mpDao.searchWaitingMaterialPartialDetail(material_id, "00000000012");
					if (wP.size() > 0) {
						conn.rollback();
						throw new Exception(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.lineWaiting", wP.get(0).getPartial_code() + "等" + wP.size()));
					}
					// FSE 数据同步
					try{
						FseBridgeUtil.toUpdateMaterialProcess(material_id, "DEC");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else
			if (!isLightFix && "00000000021".equals(position_id)) { // fenjieOver TODO

				mEntity = mDao.getMaterialNamedEntityByKey(material_id);
				if ("00000000016".equals(mEntity.getCategory_id())) { // 外科镜

					if (isFact) {
						mpService.finishMaterialProcess(material_id, "00000000012", triggerList, conn);

						// 检查是否存在未签收的零件
						MaterialPartialMapper mpDao = conn.getMapper(MaterialPartialMapper.class);
						List<MaterialPartialDetailEntity> wP = mpDao.searchWaitingMaterialPartialDetail(material_id, "00000000012");
						if (wP.size() > 0) {
							conn.rollback();
							throw new Exception(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.lineWaiting", wP.get(0).getPartial_code() + "等" + wP.size()));
						}
						// FSE 数据同步
						try{
							FseBridgeUtil.toUpdateMaterialProcess(material_id, "ENDOEYE_DEC");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else

			if (!isLightFix && "00000000026".equals(position_id)) { // 主动触发251
//				// NS 线长提交零件订购单
//				mDao.updateMaterialNsPartial(material_id);
//
//				// 试图触发251工位
//				ProductionFeatureService pfService = new ProductionFeatureService();
//				ProductionFeatureEntity fsPf = new ProductionFeatureEntity();
//
//				ProductionFeatureMapper ppDao = conn.getMapper(ProductionFeatureMapper.class);
//				// 本工位作业状态等待的记录中取得Rework
//				int rework = 0;
//				rework = ppDao.getReworkCount(material_id);
//
//				fsPf.setMaterial_id(material_id);
//				fsPf.setPosition_id("00000000020"); // 251工位
//				fsPf.setRework(rework); // 返工号
//				fsPf.setSection_id(workingPf.getSection_id());
//				// 指定251工位开始(关联检查)
//				pfService.fingerSpecifyPosition(material_id, false, fsPf, triggerList, conn);
			}

			 // 维修流程参照
			 getNext(paProxy, material_id, mEntity, pat_id, position_id, mEntity.getLevel(), nextPositions, conn);

			 // 一堆品保 TODO
			 if (nextPositions.size() == 1 && "00000000046".equals(nextPositions.get(0))) fixed = true;
			 else if (nextPositions.size() == 1 && "00000000052".equals(nextPositions.get(0))) fixed = true;
			 else if (nextPositions.size() == 1 && RvsConsts.POSITION_QA_P_613.equals(nextPositions.get(0))) fixed = true;
			 else if (nextPositions.size() == 1 && RvsConsts.POSITION_QA_P_614.equals(nextPositions.get(0))) fixed = true;
			 else if (nextPositions.size() == 1 && RvsConsts.POSITION_ANML_QA.equals(nextPositions.get(0))) fixed = true;
			 else if (nextPositions.size() == 1 && RvsConsts.POSITION_ANML_QA_UDI.equals(nextPositions.get(0))) fixed = true;
			 else fixed = false;

			// 没投线无流程 TODO 小修理报价中先做CCD盖玻璃更换
			if (mEntity.getInline_time() == null && "00000000025".equals(position_id)) {
				nextPositions.clear();
			}

			if (isLightFix && isFact) {
				// 小修理
				MaterialProcessAssignMapper mpaMapper = conn.getMapper(MaterialProcessAssignMapper.class);
				// 取得覆盖工程
				List<LineEntity> lines = mpaMapper.getWorkedLines(material_id);

				for (LineEntity line : lines) {
					if (CommonStringUtil.isEmpty(line.getName())
							&& line.getInline_flg()) {

						mpService.finishMaterialProcess(material_id, line.getLine_id(), triggerList, conn);

						if ("00000000014".equals(line.getLine_id())) {
							// 检查是否存在未签收的零件
							MaterialPartialMapper mpDao = conn.getMapper(MaterialPartialMapper.class);
							List<MaterialPartialDetailEntity> wP = mpDao.searchWaitingMaterialPartialDetail(material_id, null);
							if (wP.size() > 0) {
								conn.rollback();
								throw new Exception(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.lineWaiting", wP.get(0).getPartial_code() + "等" + wP.size()));
							}
							// FSE 数据同步
							try{
								FseBridgeUtil.toUpdateMaterialProcess(material_id, "COM");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		ProductionFeatureEntity nPf = new ProductionFeatureEntity();
		nPf.setSection_id(workingPf.getSection_id());
		nPf.setRework(workingPf.getRework());

		ProductionFeatureMapper pfDao = conn.getMapper(ProductionFeatureMapper.class);

		List<String> ret = new ArrayList<String>();

		// 建立后续工位的初始作业信息
		for (String nextPosition_id : nextPositions) {
			if (!isLightFix
					&& ("20".equals(nextPosition_id) || "00000000020".equals(nextPosition_id))
					&& (("00000000002".equals(mEntity.getPat_id()) || "00000000004".equals(mEntity.getPat_id())) 
							&& mEntity.getLevel() != null && mEntity.getLevel() != 1) ) {
				// NS零件订购单已交付
				Integer ns_partial_order = mEntity.getNs_partial_order();
				if (ns_partial_order != null && 1 != ns_partial_order) continue; 
			}
			nPf.setPosition_id(nextPosition_id);
			fingerPosition(workingPf.getPosition_id(), mEntity, fixed, nPf, conn, pfDao, paProxy, ret, triggerList, isFact);
		}

		PositionMapper ps = conn.getMapper(PositionMapper.class);
		for (int i = 0; i < ret.size(); i++) {
			String ret_position_id = ret.get(i);
			if (ret_position_id.indexOf("[") < 0) { 
				PositionEntity position = ps.getPositionByID(ret_position_id);
				if ("00000000001".equals(workingPf.getSection_id())
						&& position.getLight_division_flg() == 1 
						) { // 分 A B线
					// 只有总组自动切线
//					boolean isCom = "00000000014".equals(position.getLine_id());
					MaterialProcessEntity mpEntity = mpService.loadMaterialProcessOfLine(mEntity.getMaterial_id(), position.getLine_id(), conn);

					if (mpEntity.getPx() == 1 || mpEntity.getPx() == 3) {
						ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " B线]"); // B线
//						if (isLightFix) {
//							ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " B线]"); // 小修理原来在B线
//						} else {
//							// 判断是否需要切回
//							if (!isCom 
//									|| checkSwitchPx(workingPf.getSection_id(), ret_position_id, position.getProcess_code(), conn)) {
//								ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " A线]"); // 大修理回A线
//								// 切线
//								if (isFact) mDao.updatePx(material_id);
//							} else {
//								ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " B线]"); // 大修理保持在B线
//							}
//						}
					} else if (mpEntity.getPx() == 2) {
						ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " C线]");
					} else {
						ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " A线]");
//						if (isLightFix) {
//							ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " B线]"); // 小修理回B线
//							// 切线
//							if (isFact) mDao.updatePx(material_id);
//						} else {
//							// 判断是否需要切线
//							if (!isCom 
//									|| checkSwitchPx(workingPf.getSection_id(), ret_position_id, position.getProcess_code(), conn)) {
//								ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " A线]"); // 大修理保持在A线
//							} else {
//								ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + " B线]"); // 大修理切B线
//							}
//						}
					}
				} else {
					ret.set(i, "[" + position.getProcess_code() + " " + position.getName() + "]");
				}
			}
		}

		return ret;
	}

	/** 
	 * 判断A线是否还有容量
	 * @param section_id
	 * @param ret_position_id
	 * @param process_code
	 * @param conn
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean checkSwitchPx(String section_id, String ret_position_id,
			String process_code, SqlSessionManager conn) {
		String waitingflow = RvsUtils.getWaitingflow(section_id, null, process_code);
		Integer iwaitingflow = null;
		try {
			iwaitingflow = Integer.parseInt(waitingflow);
		} catch (NumberFormatException e) {
		} catch (NullPointerException e) {
		}

		if (iwaitingflow == null) {
			return true;
		}

		ProductionFeatureMapper mapper = conn.getMapper(ProductionFeatureMapper.class);
		int iwaitingFact = mapper.getPositionHeap(section_id, ret_position_id, "A");
		if (iwaitingFact > iwaitingflow) {
			return false;
		}
		return true;
	}

	/**
	 * 发动指定工位
	 * @param material_id
	 * @param fixed 已判断关联完成
	 * @param workingPf need Position/Section/Rework
	 * @param conn
	 * @throws Exception 
	 */
	public void fingerSpecifyPosition(String material_id, boolean fixed, ProductionFeatureEntity workingPf, List<String> triggerList, SqlSessionManager conn) throws Exception {
		MaterialMapper mDao = conn.getMapper(MaterialMapper.class);
		MaterialEntity mEntity = mDao.getMaterialEntityByKey(material_id);

		ProductionFeatureMapper pfDao = conn.getMapper(ProductionFeatureMapper.class);
		Integer level = mEntity.getLevel();//等级

//		boolean isLightFix = (level != null) &&  
//				(mEntity.getLevel() == 9 || mEntity.getLevel() == 91 || mEntity.getLevel() == 92 || mEntity.getLevel() == 93);
		boolean isLightFix = RvsUtils.isLightFix(level);

		ProcessAssignProxy paProxy = new ProcessAssignProxy(material_id, mEntity.getPat_id(), mEntity.getSection_id(), isLightFix, conn);

		fingerPosition(workingPf.getPosition_id(), mEntity, fixed, workingPf, conn, pfDao, paProxy, null, triggerList);
	}

	public void getNext(ProcessAssignProxy paProxy, String material_id, MaterialEntity mEntity, String pat_id, String position_id, Integer level, List<String> nextPositions, SqlSession conn) {
		// 得到下一个工位
		List<PositionEntity> nextPositionsByPat = paProxy.getNextPositions(position_id);

		String this_position_id = null;
		if (nextPositionsByPat.size() > 0) {
			this_position_id = nextPositionsByPat.get(0).getPosition_id();
		}

		if (this_position_id == null || ((RvsConsts.PROCESS_ASSIGN_LINE_END+"").equals(this_position_id))) {
			// 下一个工位是PROCESS_ASSIGN_LINE_END
			this_position_id = RvsConsts.PROCESS_ASSIGN_LINE_END + "";
			// 用它自身的Line_id区分
			ProcessAssignEntity pa = paProxy.getProcessAssign(position_id);
			if (pa == null) return; // 未设流程时
			String line_id = pa.getLine_id();
			if ((RvsConsts.PROCESS_ASSIGN_LINE_BASE + "").equals(line_id)) {
				if (MaterialTagService.getAnmlMaterials(conn).contains(material_id)) {
					// 判断是否UDI
					if (mEntity.getCategory_id().equals(RvsConsts.CATEGORY_UDI)) {
						nextPositions.add(RvsConsts.POSITION_ANML_QA_UDI);
					} else {
						nextPositions.add(RvsConsts.POSITION_ANML_QA);
					}
					
				} else {
//					if (paProxy.getFinishedByLine(line_id)) { TODO 其实要这个的 像OGZ有最后一个工位并行的就会两次611。总之目前省下一次查询
					if (level == 56 || level == 57 || level == 58 || level == 59) {
						nextPositions.add(RvsConsts.POSITION_QA_P_613);
					} else if (RvsConsts.CATEGORY_UDI.equals(mEntity.getCategory_id())) { // 光学视管
						nextPositions.add(RvsConsts.POSITION_QA_P_614);
					} else if (paProxy.isLightFix) {
						// 并且是主流程时，612工位
						nextPositions.add(RvsConsts.POSITION_QA_LIGHT);
					} else {
						// 并且是主流程时，611工位
						nextPositions.add(RvsConsts.POSITION_QA);
					}
//					}
				}

			} else {
				// 并且不是主流程时，

				// 所在流程判断是否全部完成
				if (paProxy.getFinishedByLine(line_id)) {
					// 如果所在流程全部完成，触发流程的下一个工位
					getNext(paProxy, material_id, mEntity, pat_id, line_id, level, nextPositions, conn);
				}
			}
		} else {
			for (PositionEntity nextPosition : nextPositionsByPat) {
				// 判断每个工位是不是分线名
				if (Integer.parseInt(nextPosition.getPosition_id()) > RvsConsts.PROCESS_ASSIGN_LINE_BASE) {
					// 是分线得到分线的由0开始的工位 ,不考虑嵌套分线
					List<String> positions = paProxy.getPartStart(nextPosition.getPosition_id());
					for (String position : positions) {
						nextPositions.add(position);
					}
				} else {
					// 增加单个工位
					nextPositions.add(nextPosition.getPosition_id());
				}
			}
		}

		// S1等级略过工位
		if (level == 1 && !"00000000008".equals(pat_id) && !"00000000009".equals(pat_id)) {
			int lNextPositions = nextPositions.size();
			for (int i = lNextPositions - 1; i >= 0; i--) {
				String nextPosition = nextPositions.get(i);
				for (int j = 0; j < ProcessAssignService.S1PASSES.length; j++) {
					if (ProcessAssignService.S1PASSES[j] == Integer.parseInt(nextPosition)) {
						nextPositions.remove(i);
						break;
					}
				}
			}
		}
	}

	public void getPrev(ProcessAssignProxy paProxy, String material_id, String pat_id, String position_id, Integer level, List<String> prevPositions) {
		// 得到先决工位
		List<PositionEntity> prevPositionsByPat = paProxy.getPrevPositions(position_id);

		String this_position_id = null;
		if (prevPositionsByPat.size() > 0) {
			this_position_id = prevPositionsByPat.get(0).getPosition_id();
		}

		if (this_position_id == null || ((RvsConsts.PROCESS_ASSIGN_LINE_START+"").equals(this_position_id))) {
			// 上一个工位是0，用它自身的Line_id区分
			ProcessAssignEntity pa = paProxy.getProcessAssign(position_id);
			String line_id = pa.getLine_id();
			if ((RvsConsts.PROCESS_ASSIGN_LINE_BASE + "").equals(line_id)) {
				// 并且是主流程时，不需要先决
				return;
			} else {
				// 并且不是主流程时，

				// 取得所在流程的先决
				getPrev(paProxy, material_id, pat_id, line_id, level, prevPositions);
			}
		} else {
			for (PositionEntity prevPosition : prevPositionsByPat) {
				// 判断每个工位是不是分线名
				if (Integer.parseInt(prevPosition.getPosition_id()) > RvsConsts.PROCESS_ASSIGN_LINE_BASE) {
					// 是分线的话，分线中的全部工位都是先决工位 ,不考虑嵌套分线
					List<String> positions = paProxy.getPartAll(prevPosition.getPosition_id());
					for (String position : positions) {
						prevPositions.add(position);
					}
				} else {
					// 增加单个工位
					prevPositions.add(prevPosition.getPosition_id());
				}
			}
		}

		// S1等级略过工位
		if (level == 1 && !"00000000008".equals(pat_id) && !"00000000009".equals(pat_id)) {
			int lNextPositions = prevPositions.size();
			for (int i = lNextPositions - 1; i >= 0; i--) {
				String nextPosition = prevPositions.get(i);
				for (int j = 0; j < ProcessAssignService.S1PASSES.length; j++) {
					if (ProcessAssignService.S1PASSES[j] == Integer.parseInt(nextPosition)) {
						prevPositions.remove(i);
						break;
					}
				}
			}
		}
	}

	/**
	 * 按条件检索作业情报，期望返回一条记录
	 * @param pf 检索条件
	 * @param conn
	 * @return
	 */
	public ProductionFeatureEntity searchProductionFeatureOne(ProductionFeatureEntity pf, SqlSessionManager conn) {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		List<ProductionFeatureEntity> results = dao.searchProductionFeature(pf);
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	public void changeSection(String material_id, String section_id, SqlSessionManager conn) throws Exception {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		dao.changeSection(material_id, section_id);
	}

	/**
	 * 按照当前的维修流程，重新自起点起寻找接下来需要开始作业的工位
	 * @param material_id
	 * @param rework
	 * @param ppDao
	 * @param conn
	 * @throws Exception
	 */
	public void reprocess(String material_id, int rework, ProductionFeatureMapper ppDao, List<String> triggerList, SqlSessionManager conn) throws Exception {
		// 重新开始执行流程
		// 取得维修流程
		MaterialMapper mDao = conn.getMapper(MaterialMapper.class);
		MaterialEntity mEntity = mDao.getMaterialEntityByKey(material_id);

		// 找到整个流程的起点，开始触发。
		Integer level = mEntity.getLevel();
//		boolean isLightFix = (level != null) &&
//				(level == 9 || level == 91 || level == 92 || level == 93);
		boolean isLightFix = RvsUtils.isLightFix(level);

		String pat_id = mEntity.getPat_id(); // 维修流程主键
		ProcessAssignProxy paProxy = new ProcessAssignProxy(material_id, pat_id, mEntity.getSection_id(), isLightFix, conn);

		List<String> startPositions = paProxy.getPartStart("" + RvsConsts.PROCESS_ASSIGN_LINE_BASE);
		List<String> groupPositions = new ArrayList<String>(); // 作为大分支
		for (String startPosition : startPositions) {
			if (Integer.parseInt(startPosition) > RvsConsts.PROCESS_ASSIGN_LINE_BASE) {
				startPositions.addAll(paProxy.getPartStart(startPosition));
				groupPositions.add(startPosition);
			}
		}
		for (String startPosition : groupPositions) {
			startPositions.remove(startPosition);
		}

		ProductionFeatureEntity workingPf = new ProductionFeatureEntity();
		workingPf.setSection_id(mEntity.getSection_id());
		workingPf.setRework(rework);

		for (String startPosition : startPositions) {
			workingPf.setPosition_id(startPosition);
			fingerPosition(workingPf.getPosition_id(), mEntity, false, workingPf, conn, ppDao, paProxy, null, triggerList);
		}
	}

	public void removeWorking(String material_id, String position_id, SqlSessionManager conn) throws Exception {
		ProductionFeatureMapper paDao = conn.getMapper(ProductionFeatureMapper.class);

		paDao.removeWaiting(material_id, position_id);
		if (position_id != null) 
			paDao.reworkOperateResult(material_id, position_id);
	}

	/**
	 * 检查是否有多处中断
	 * @param id
	 * @param conn
	 * @return
	 */
	public int checkOtherBreak(String id, SqlSessionManager conn) {
		AlarmMesssageMapper dao = conn.getMapper(AlarmMesssageMapper.class);
		return dao.countBreakUnPushedAlarmMessage(id);
	}

	public void startProductionFeature(ProductionFeatureEntity waitingPf, SqlSessionManager conn) throws Exception {
		// 作业信息状态改为，作业中
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		dao.startProductionFeature(waitingPf);
	}

	public void startBatchProductionFeature(ProductionFeatureEntity waitingPf, SqlSessionManager conn) throws Exception {
		// 作业信息状态改为，批量作业/存放中
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		dao.startBatchProductionFeature(waitingPf);
	}

	public void changeWaitProductionFeature(ProductionFeatureEntity workwaitingPf,
			SqlSessionManager conn) throws Exception {
		// 作业信息状态改为，作业中
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		dao.pauseWaitProductionFeature(workwaitingPf);
	}

	public void finishProductionFeature(ProductionFeatureEntity workingPf,
			SqlSessionManager conn) throws Exception {
		ProductionFeatureMapper pfdao = conn.getMapper(ProductionFeatureMapper.class);
		pfdao.finishProductionFeature(workingPf);
	}

	public void finishProductionFeatureSetFinish(ProductionFeatureEntity workingPf,
			SqlSessionManager conn) throws Exception {
		ProductionFeatureMapper pfdao = conn.getMapper(ProductionFeatureMapper.class);
		pfdao.finishProductionFeatureSetFinish(workingPf);
	}

	public String getFingerString(String material_id, List<String> fingerList, SqlSession conn, boolean isFact) {
		MaterialMapper mDao = conn.getMapper(MaterialMapper.class);
		MaterialEntity mEntity = mDao.getMaterialEntityByKey(material_id);

		String idNo = mEntity.getSorc_no();
		if (idNo == null) idNo = "(机身号" + mEntity.getSerial_no() + ")";
		if (isFact)
			return ApplicationMessage.WARNING_MESSAGES
				.getMessage("info.transfer.justFinshed", idNo, joinBy(", ", fingerList.toArray(new String[fingerList.size()])));
		else
			return ApplicationMessage.WARNING_MESSAGES
				.getMessage("info.transfer.justNow", idNo, joinBy(", ", fingerList.toArray(new String[fingerList.size()])));
	}

	public Map<String, String> getPoistionsOfMaterial(String materialId, SqlSession conn) {
		ProductionFeatureMapper mapper = conn.getMapper(ProductionFeatureMapper.class);
		List<ProductionFeatureEntity> results = mapper.getWorkedPositionOfMaterial(materialId);
		Map<String, String> ret = new TreeMap<String, String>();
		for (ProductionFeatureEntity result : results) {
			ret.put(result.getPosition_id(), result.getProcess_code() + " " + result.getPosition_name());
		}
		return ret;
	}

	/** 维修等待区删除 */
	public void removeWaiting(String material_id, String position_id,
			SqlSessionManager conn) {
		ProductionAssignMapper mapper = conn.getMapper(ProductionAssignMapper.class);
		mapper.remove(material_id, position_id);
	}

	/**
	 * 查询实际已完成计划工位
	 * @param material_id
	 * @param line_id
	 * @param conn
	 * @return
	 */
	public List<ProductionFeatureForm> getFinishedProductionFeatureByMaterialId(String material_id, String line_id,
			SqlSession conn) {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		List<ProductionFeatureEntity> list = dao.getFinishedProductionFeatureByMaterialId(material_id, line_id);
		
		List<ProductionFeatureForm> listForm = new ArrayList<ProductionFeatureForm>();
		
		if(list!=null && list.size()>0){
			BeanUtil.copyToFormList(list, listForm, null, ProductionFeatureForm.class);
		}
		
		return listForm;
	}

	/**
	 * 判断是否完成消毒
	 * @param material_id
	 * @param conn
	 */
	public int checkFinishedDisinfection(String material_id, SqlSession conn) {
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		return dao.checkFinishedDisinfection(material_id);
	}
}

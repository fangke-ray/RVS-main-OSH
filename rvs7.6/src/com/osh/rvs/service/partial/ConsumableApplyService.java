package com.osh.rvs.service.partial;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.partial.ConsumableApplicationDetailEntity;
import com.osh.rvs.bean.partial.ConsumableApplicationEntity;
import com.osh.rvs.bean.partial.ConsumableListEntity;
import com.osh.rvs.bean.partial.ConsumableOnlineEntity;
import com.osh.rvs.bean.partial.FactConsumableWarehouseEntity;
import com.osh.rvs.bean.qf.AfProductionFeatureEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.ConsumableApplicationForm;
import com.osh.rvs.form.pda.PdaApplyDetailForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.partial.ConsumableApplicationDetailMapper;
import com.osh.rvs.mapper.partial.ConsumableApplicationMapper;
import com.osh.rvs.mapper.partial.ConsumableListMapper;
import com.osh.rvs.mapper.partial.ConsumableOnlineMapper;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.PostMessageService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ConsumableApplyService {

	/** 维修品即时领用 */
	private static final String SUBMIT_TYPE_EXCHANGE = "ex";
	private Logger _logger = Logger.getLogger(ConsumableApplyService.class);

	/**
	 * 申请领用记录一览
	 * 
	 * @param entity
	 * @param conn
	 * @throws Exception
	 */
	public List<ConsumableApplicationEntity> searchEntities(ActionForm form, SqlSession conn) throws Exception {
		ConsumableApplicationForm consumableApplicationForm  = (ConsumableApplicationForm) form;
		ConsumableApplicationEntity entity = new ConsumableApplicationEntity();
		
		if(!CommonStringUtil.isEmpty(consumableApplicationForm.getConsumable_application_key())){
			entity.setConsumable_application_key(consumableApplicationForm.getConsumable_application_key());
		}else{
			BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		}

		ConsumableApplicationMapper dao = conn.getMapper(ConsumableApplicationMapper.class);
		List<ConsumableApplicationEntity> applyList = dao.search(entity);

		return applyList;
	}
	public List<ConsumableApplicationForm> search(ActionForm form, SqlSession conn) throws Exception {

		List<ConsumableApplicationEntity> applyList = searchEntities(form, conn);
		
		List<ConsumableApplicationForm> lResultForm = new ArrayList<ConsumableApplicationForm>();
		BeanUtil.copyToFormList(applyList, lResultForm, null,ConsumableApplicationForm.class);

		return lResultForm;
	}

	/**
	 * 取得指定工程现存申请表
	 * @param isEx 
	 * 
	 * @param entity
	 * @param conn
	 * @throws Exception
	 */
	public ConsumableApplicationEntity getConsumableApplicationByLine(String section_id, String line_id,
			boolean isEx, SqlSession conn) {
		ConsumableApplicationMapper caMapper = conn.getMapper(ConsumableApplicationMapper.class);
		ConsumableApplicationEntity collectingApplication = null;

		if (!isEx)
			collectingApplication = caMapper.getCollectingApplicationByLine(section_id, line_id);

		// 没有收集中的申请表
		if (collectingApplication == null) {
			// 取得最新申请单号
			String newApplicationNo = getNewApplicationNo(section_id, line_id, caMapper);
			collectingApplication = new ConsumableApplicationEntity();
			collectingApplication.setApplication_no(newApplicationNo);
		}
		return collectingApplication;
	}
	public ConsumableApplicationEntity getConsumableApplicationByLine(String section_id, String line_id,
			SqlSession conn) {
		return getConsumableApplicationByLine(section_id, line_id ,false, conn);
	}

	/**
	 * 取得指定工程新建申请表号
	 * @param caMapper 
	 * 
	 * @param entity
	 * @param conn
	 * @throws Exception
	 */
	private String getNewApplicationNo(String section_id, String line_id, ConsumableApplicationMapper caMapper) {
		Calendar now = Calendar.getInstance();
		String monthString = RvsUtils.getBussinessYearString(now) 
				+ CommonStringUtil.fillChar(now.get(Calendar.MONTH) + 1 + "", '0', 2, true);
		String maxApplication = caMapper.getMaxApplicationByLine(section_id, line_id, monthString);
		int serial = 1;
		if (maxApplication != null && maxApplication.length() > 6) {
			serial = Integer.parseInt(maxApplication.substring(6)) + 1;
		}
		return monthString + CommonStringUtil.fillChar(""+serial, '0', 4, true);
	}

	
	
	/**
	 * 获取单个消耗品申请单信息
	 * @param form
	 * @param conn
	 * @return
	 */
	public ConsumableApplicationForm getConsumableApplication(ActionForm form,SqlSession conn){
		ConsumableApplicationForm consumableApplicationForm  = (ConsumableApplicationForm) form;
		
		ConsumableApplicationMapper dao = conn.getMapper(ConsumableApplicationMapper.class);
		ConsumableApplicationEntity entity = dao.searchConsumableApplicationById(consumableApplicationForm.getConsumable_application_key());
		
		
		ConsumableApplicationForm returnForm = new ConsumableApplicationForm();
		BeanUtil.copyToForm(entity, returnForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		
		return returnForm;
		
	}
	
	
	/**
	 * 更新
	 * @param form
	 * @param formList
	 * @param conn
	 * @param errors
	 * @return
	 */
	public boolean update(ActionForm form,List<ConsumableApplicationForm> formList,SqlSessionManager conn,List<MsgInfo> errors,HttpServletRequest req) throws Exception{

		if (formList.size() == 0) {
			return true;
		}

		//消耗品申请单Key
		String key = "";
		//消耗品申请单编号
		String application_no = "";
		//部分发放
		String part_supply = "";
		//课室ID
		String section_id = "";
		//工程ID
		String line_id = "";
		//确认框中
		String part_supply_comfrim = "";

		if (form instanceof ConsumableApplicationForm) {
			ConsumableApplicationForm consumableApplicationForm = (ConsumableApplicationForm)form;

			key = consumableApplicationForm.getConsumable_application_key();
			application_no = consumableApplicationForm.getApplication_no();
			part_supply = consumableApplicationForm.getPart_supply();
			section_id = consumableApplicationForm.getSection_id();
			line_id = consumableApplicationForm.getLine_id();
			part_supply_comfrim = consumableApplicationForm.getPart_supply_comfrim();
		} else if (form instanceof PdaApplyDetailForm) {
			PdaApplyDetailForm detailForm = (PdaApplyDetailForm)form;

			key = detailForm.getConsumable_application_key();
			application_no = detailForm.getApplication_no();
			section_id = detailForm.getSection_id();
			line_id = detailForm.getLine_id();
			part_supply_comfrim = detailForm.getSupplied_flg();
			if ("0".equals(part_supply_comfrim)) {
				part_supply = detailForm.getSupplied_flg();
			}
		}

		// 取得当前在库数量
		Map<String, Integer> nowAvailableInventoryMap = new HashMap<String, Integer>();
		Set<String> targetPartialIds = new HashSet<String>();
		Map<String, Integer> partialCostMap = new HashMap<String, Integer>();
		Map<Integer, Integer> quantityMap = new HashMap<Integer, Integer>();

		List<ConsumableApplicationDetailEntity> list = new ArrayList<ConsumableApplicationDetailEntity>();
		for(ConsumableApplicationForm caForm:formList){
			ConsumableApplicationDetailEntity entity = new ConsumableApplicationDetailEntity();
			BeanUtil.copyToBean(caForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
			list.add(entity);
			if (entity.getSupply_quantity() > 0) {
				targetPartialIds.add(entity.getPartial_id());
			}
		}

		ConsumableListMapper clMapper = conn.getMapper(ConsumableListMapper.class);
		if (targetPartialIds.size() > 0) {
			List<ConsumableListEntity> availableInventories = clMapper.getAvailableInventories(targetPartialIds);
			for (ConsumableListEntity availableInventory : availableInventories) {
				nowAvailableInventoryMap.put(CommonStringUtil.fillChar("" + availableInventory.getPartial_id(), '0', 11, true), 
						availableInventory.getAvailable_inventory());
			}

			List<ConsumableListEntity> cEntitys = clMapper.getConsumableDetails(targetPartialIds);
			for (ConsumableListEntity cEntity : cEntitys) {
				partialCostMap.put(CommonStringUtil.fillChar("" + cEntity.getPartial_id(), '0', 11, true), 
						cEntity.getOut_shelf_cost());
			}
		}
		
		//发放完成标记
		Integer all_supplied = null;

		//如果没有选择[部分发放]
		if(CommonStringUtil.isEmpty(part_supply)){//页面当中没有选择

			if("1".equals(part_supply_comfrim)){//全部发放
				all_supplied = 1;
			}else if("0".equals(part_supply_comfrim)){//部分发放
				all_supplied = 0;
			}else{

				boolean flg = false;

				// 发放数量与待发放数量比较
				for(ConsumableApplicationDetailEntity entity:list){

					Integer supply_quantity = entity.getSupply_quantity();// 发放数量(用户输入)
					Integer waitting_quantity = entity.getWaitting_quantity();// 待发放数量

					if(supply_quantity < waitting_quantity){
						flg =true;
						break;//跳出当前循环判断
					}
				}

				//存在 发放数量 < 待发放数量 的任何一条记录
				if(flg == true){

					//返回页面弹出确认框
					return false;

				}else{	//没有上述任何一条的记录   [发放完成标记] = 1
					all_supplied = 1;
				}
			}
		}else{
			//选择[部分发放]     不管发放数量如何[发放完成标记] = 0
			all_supplied = 0;
		}

	
		LoginData loginData = (LoginData)req.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();

		//更新申请单
		ConsumableApplicationEntity consumableApplicationEntity = new ConsumableApplicationEntity();
		consumableApplicationEntity.setConsumable_application_key(key);
		consumableApplicationEntity.setSupplier_id(operator_id);
		consumableApplicationEntity.setAll_supplied(all_supplied);

		ConsumableApplicationMapper consumableApplicationDao = conn.getMapper(ConsumableApplicationMapper.class);
		consumableApplicationDao.update(consumableApplicationEntity);

		ConsumableApplicationDetailMapper consumableApplicationDetailDao = conn.getMapper(ConsumableApplicationDetailMapper.class);
		ConsumableOnlineMapper consumableOnlineDao = conn.getMapper(ConsumableOnlineMapper.class);

		Map<String,String> postMap = new HashMap<String,String>();

		boolean sendNothing = true;

		for(ConsumableApplicationDetailEntity entity:list){

			Integer supply_quantity = entity.getSupply_quantity();// 发放数量 (画面)

			if (supply_quantity == 0) continue;

			String partial_id = entity.getPartial_id();//零件ID
			Integer db_supply_quantity = entity.getDb_supply_quantity(); //发放数量(数据库中)
			String openflg = entity.getOpenflg();//开封标记
			Integer available_inventory = entity.getAvailable_inventory();//当前有效库存
			String petitioner_id = entity.getPetitioner_id();//申请人

			if("D".equals(openflg) || "E".equals(openflg)){
				entity.setSupply_quantity(supply_quantity + db_supply_quantity);  //supply_quantity = supply_quantity + [画面提交的发放数量]
				//更新申请单明细
				consumableApplicationDetailDao.updateSupplyQuantity(entity);
				postMap.put(petitioner_id, petitioner_id);

			}else if("B".equals(openflg) || "C".equals(openflg)){ 				  //选择 "已开封提供", "新开封提供"  supply_quantity = 1
				entity.setSupply_quantity(1);
				//更新申请单明细
				consumableApplicationDetailDao.updateSupplyQuantity(entity);
				postMap.put(petitioner_id, petitioner_id);
			}

			if (sendNothing && entity.getSupply_quantity() > 0) sendNothing = false;

			//更新仓库库存
			if("A".equals(openflg) || "B".equals(openflg)){//不发放  或者 已开封提供  不做更新
			}else if("C".equals(openflg)){//新开封
				available_inventory = nowAvailableInventoryMap.get(entity.getPartial_id());
				nowAvailableInventoryMap.put(entity.getPartial_id(), available_inventory--);
				entity.setAvailable_inventory(available_inventory);
				entity.setUnseal_items(1);
				consumableApplicationDetailDao.updateAvailableInventory(entity);
			}else if("D".equals(openflg) || "E".equals(openflg)){
				available_inventory = nowAvailableInventoryMap.get(entity.getPartial_id());
				nowAvailableInventoryMap.put(entity.getPartial_id(), available_inventory -= supply_quantity);
				entity.setAvailable_inventory(available_inventory);
				consumableApplicationDetailDao.updateAvailableInventory(entity);
			}
			if (available_inventory < 0) {
				MsgInfo msg = new MsgInfo();
				msg.setErrmsg("消耗品代码为: " + entity.getCode() +"的发放数量大于有效库存");
				errors.add(msg);					
				conn.rollback();
				return false;
			}

			Integer shelf_cost = partialCostMap.get(partial_id);
			Integer quantity = 1;
			if (quantityMap.containsKey(shelf_cost)) {
				quantity = quantityMap.get(shelf_cost) + 1;
			}
			quantityMap.put(shelf_cost, quantity);

			//更新线上库存
			ConsumableOnlineEntity consumableOnlineEntity = new ConsumableOnlineEntity();
			consumableOnlineEntity.setPartial_id(Integer.valueOf(partial_id));
			consumableOnlineEntity.setCourse(Integer.valueOf(section_id));
			consumableOnlineEntity.setProject(Integer.valueOf(line_id));
			consumableOnlineEntity.setQuantity(0);

			ConsumableOnlineEntity onlineEntity = consumableOnlineDao.getOnlineStorage(consumableOnlineEntity);//消耗品在线库存

			Integer type = entity.getType();//类型

			if(onlineEntity ==null){
				if(type == 3){//螺丝

					consumableOnlineEntity.setQuantity(supply_quantity);
					consumableOnlineDao.insert(consumableOnlineEntity);
				}
				continue;
			}


			if("A".equals(openflg)){	//选择"不发放"时, 不更新
			}else if("B".equals(openflg) || "C".equals(openflg)){	//选择 "已开封提供", "新开封提供" : quantity = 1;
				consumableOnlineEntity.setQuantity_modify(1);
				consumableOnlineDao.updateOnlineList(consumableOnlineEntity);
			}else if("D".equals(openflg) || "E".equals(openflg)){
				consumableOnlineEntity.setQuantity_modify(onlineEntity.getQuantity() + supply_quantity);
				consumableOnlineDao.updateOnlineList(consumableOnlineEntity);
			}
		}

		FactConsumableWarehouseService fcwSerivce = new FactConsumableWarehouseService();
		AcceptFactService acceptFactService = new AcceptFactService();
		// 根据操作者ID查找未结束作业信息
		AfProductionFeatureEntity afpfEntity = acceptFactService.getUnFinishEntity(loginData.getOperator_id(), conn);
		if (afpfEntity != null) {
			for (Integer out_shelf_cost : quantityMap.keySet()) {
				// 记录到消耗品出入库作业数
				Integer nowQuantity = fcwSerivce.getQuantity(afpfEntity.getAf_pf_key(), out_shelf_cost, conn);

				FactConsumableWarehouseEntity fcwEntity = new FactConsumableWarehouseEntity();
				fcwEntity.setAf_pf_key(afpfEntity.getAf_pf_key());
				fcwEntity.setShelf_cost(out_shelf_cost);
				if (nowQuantity == null) {
					fcwEntity.setQuantity(quantityMap.get(out_shelf_cost));
					fcwSerivce.insert(fcwEntity, conn);
				} else {
					fcwEntity.setQuantity(nowQuantity + quantityMap.get(out_shelf_cost));
					fcwSerivce.update(fcwEntity, conn);
				}
			}
		}

		//处理推送人员
		OperatorMapper operatorMapper = conn.getMapper(OperatorMapper.class);

		OperatorEntity operatorEntity = new OperatorEntity();
		operatorEntity.setSection_id(section_id);
		operatorEntity.setLine_id(line_id);

		List<OperatorNamedEntity> operatorNamedEntityList;

		for(String petitioner_id:postMap.keySet()){//Map  key不重复 如果有key=00000000000,只会有一条
			if(!"00000000000".equals(petitioner_id)){

				operatorEntity.setRole_id(RvsConsts.ROLE_LINELEADER);

				operatorNamedEntityList = operatorMapper.searchOperator(operatorEntity);
				if (operatorNamedEntityList.size() == 0 && "00000000003".equals(section_id)) {
					operatorEntity.setLine_id(null);
					operatorNamedEntityList = operatorMapper.searchOperator(operatorEntity);
					operatorEntity.setLine_id(line_id);
				}
				for(OperatorNamedEntity operatorNamedEntity:operatorNamedEntityList){
					postMap.put(operatorNamedEntity.getOperator_id(),operatorNamedEntity.getOperator_id());
				}

				operatorEntity.setRole_id(RvsConsts.ROLE_QT_MANAGER);

				operatorNamedEntityList = operatorMapper.searchOperator(operatorEntity);
				for(OperatorNamedEntity operatorNamedEntity:operatorNamedEntityList){
					postMap.put(operatorNamedEntity.getOperator_id(),operatorNamedEntity.getOperator_id());
				}

				operatorEntity.setRole_id(RvsConsts.ROLE_QA_MANAGER);

				operatorNamedEntityList = operatorMapper.searchOperator(operatorEntity);
				for(OperatorNamedEntity operatorNamedEntity:operatorNamedEntityList){
					postMap.put(operatorNamedEntity.getOperator_id(),operatorNamedEntity.getOperator_id());
				}
				break;
			}else{
				postMap.clear();

				operatorNamedEntityList = operatorMapper.searchOperator(operatorEntity);
				for(OperatorNamedEntity operatorNamedEntity:operatorNamedEntityList){
					postMap.put(operatorNamedEntity.getOperator_id(),operatorNamedEntity.getOperator_id());
				}
				break;
			}
		}

		//发送推送
		PostMessageMapper postMessageMapper = conn.getMapper(PostMessageMapper.class);

		PostMessageEntity postMessageEntity = new PostMessageEntity();
		postMessageEntity.setLevel(1);
		postMessageEntity.setSender_id(operator_id);
		postMessageEntity.setRoot_post_message_id(null);

		// 查看发放记录
		if (all_supplied == 1) {
			postMessageEntity.setReason(PostMessageService.CONSUMABLE_APPLY_COMPLETE);
			if (sendNothing)
				postMessageEntity.setContent(loginData.getName() + "结束了<a href='consumable_manage.do?page=page_apply&key="
						+ key + "'>申请单[" + application_no + "]</a>的发放，本次没有发放任何消耗品。");
			else
				postMessageEntity.setContent(loginData.getName() + "根据<a href='consumable_manage.do?page=page_apply&key="
						+ key + "'>申请单[" + application_no + "]</a>全部发放了消耗品，请清点实物是否一致。");

			// 清除其他人的信息
			PostMessageEntity clearEntity = new PostMessageEntity();
			clearEntity.setRoot_post_message_id(key);
			List<Integer> reasons = new ArrayList<Integer>();
			reasons.add(42);
			reasons.add(44);
			postMessageMapper.closePostMessage(clearEntity, reasons);
		} else {
			postMessageEntity.setReason(PostMessageService.CONSUMABLE_APPLY_IMCOMPLETE);
			if (sendNothing)
				return true;
			else
				postMessageEntity.setContent(loginData.getName() + "根据<a href='consumable_manage.do?page=page_apply&key="
						+ key + "'>申请单[" + application_no + "]</a>发放了<b>部分</b>消耗品，请清点实物是否一致并等待后续发放。");
		}

		postMessageMapper.createPostMessage(postMessageEntity);


		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String last_insert_id = commonMapper.getLastInsertID();

		//推送到人
		String pushString = "";
		for(String receiver_id:postMap.keySet()){
			postMessageEntity = new PostMessageEntity();
			postMessageEntity.setPost_message_id(last_insert_id);
			postMessageEntity.setReceiver_id(receiver_id);
			postMessageMapper.createPostMessageSendation(postMessageEntity);

			// push
			pushString += "/" + receiver_id;
		}

		if (pushString.length() > 0) {
			// 通知
			HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
			httpclient.start();
			try {
				HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/postMessage/" + pushString);
				_logger.info("finger:"+request.getURI());
				httpclient.execute(request, null);
			} catch (Exception e) {
			} finally {
				Thread.sleep(100);
				httpclient.shutdown();
			}
		}

		if (quantityMap.size() > 0) {
			conn.commit();
			acceptFactService.fingerOperatorRefresh(loginData.getOperator_id());
		}

		return true;
}

	/**
	 * 取得已登录消耗品(如果有ID)
	 * @param consumable_application_key
	 * @param result
	 * @param conn
	 */
	public void getConsumableList(String consumable_application_key, Map<String, Object> result, String operator_id,
			SqlSession conn) {
		if (isEmpty(consumable_application_key)) return;
		ConsumableApplicationDetailMapper mapper = conn.getMapper(ConsumableApplicationDetailMapper.class);
		ConsumableListMapper clMapper = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableApplicationDetailEntity> applicationDetail = mapper.getDetailForEditById(consumable_application_key, operator_id);
		// 分别自己和别人填写的申请品
		if (!"00000000000".equals(operator_id)) {
			for (ConsumableApplicationDetailEntity entity : applicationDetail) {
				if (!operator_id.equals(entity.getPetitioner_id())) {
					entity.setPetitioner_id(null);
				} else {
					if (entity.getCut_length() != null) { // 取得剪裁长度选项
						entity.setCut_length_options(clMapper.getHeatshrinkableLengthString(entity.getPartial_id()));
					}
				}
			}
		} else {
			for (ConsumableApplicationDetailEntity entity : applicationDetail) {
				if (entity.getCut_length() != null) { // 取得剪裁长度选项
					entity.setCut_length_options(clMapper.getHeatshrinkableLengthString(entity.getPartial_id()));
				}
			}
		}
		result.put("applicationDetail", applicationDetail);
	}

	/**
	 * 取得消耗品信息
	 * @param consumable_application_key
	 * @param result
	 * @param conn
	 */
	public void getDetailForEditByPartial(ActionForm form, Map<String, Object> result, SqlSession conn) {
		ConsumableApplicationDetailMapper mapper = conn.getMapper(ConsumableApplicationDetailMapper.class);
		ConsumableApplicationDetailEntity condition = new ConsumableApplicationDetailEntity();
		BeanUtil.copyToBean(form, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<ConsumableApplicationDetailEntity> partials = mapper.getDetailForEditByPartial(condition);
		if (partials.size() == 1) {
			ConsumableApplicationDetailEntity applicationDetail = partials.get(0);
			result.put("applicationDetail", applicationDetail);
			if (applicationDetail.getType() == 6) { // 热缩管
				ConsumableListMapper clMapper = conn.getMapper(ConsumableListMapper.class);
				String chooses = clMapper.getHeatshrinkableLengthString(applicationDetail.getPartial_id());
				result.put("lengths", chooses);
			}
		}
	}

	/**
	 * 编辑/修改申请表
	 * @param form
	 * @param formList
	 * @param conn
	 * @param errors
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	public boolean edit(ActionForm form,
			List<ConsumableApplicationForm> formList, SqlSessionManager conn,
			List<MsgInfo> errors, HttpServletRequest req) throws Exception {
		ConsumableApplicationMapper caMapper = conn.getMapper(ConsumableApplicationMapper.class);
		ConsumableApplicationDetailMapper cadMapper = conn.getMapper(ConsumableApplicationDetailMapper.class);
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);

		ConsumableApplicationForm consumableApplicationForm = (ConsumableApplicationForm)form;

		LoginData user = (LoginData)req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();
		// 判断自身是否有线长权限
		boolean iAmLeader = privacies.contains(RvsConsts.PRIVACY_LINE)
				|| privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)
				|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT);

		// 是否要提交
		String doSubmit = consumableApplicationForm.getFlg();
		
		if (!iAmLeader && SUBMIT_TYPE_EXCHANGE.equals(doSubmit)) {
			consumableApplicationForm.setConsumable_application_key(null);
		}

		// 取得当前工程申请单
		String current_section_id = user.getSection_id();
		String current_line_id = user.getLine_id();
		String operator_id = user.getOperator_id();
		String position_id = user.getPosition_id();

		// 取得本工程最新的订购单
		ConsumableApplicationEntity cmf = null;
		if (isEmpty(consumableApplicationForm.getConsumable_application_key())) {
			cmf = getConsumableApplicationByLine(current_section_id, current_line_id, SUBMIT_TYPE_EXCHANGE.equals(doSubmit), conn); // TODO
		} else {
			cmf = caMapper.searchConsumableApplicationById(consumableApplicationForm.getConsumable_application_key());
		}

		if (iAmLeader && SUBMIT_TYPE_EXCHANGE.equals(doSubmit)) {
			doSubmit = "true";
		}

		if (iAmLeader && !SUBMIT_TYPE_EXCHANGE.equals(doSubmit)) {
			cmf.setConsumable_application_key(consumableApplicationForm.getConsumable_application_key());
		}
		// 没有申请单时
		if (isEmpty(cmf.getConsumable_application_key())) {
			ConsumableApplicationEntity insertEntity = new ConsumableApplicationEntity();
			CopyOptions cop = new CopyOptions();
			cop.include("consumable_application_key", "material_id", "apply_reason");

			BeanUtil.copyToBean(form, insertEntity, cop);
			insertEntity.setApplication_no(cmf.getApplication_no());
			insertEntity.setSection_id(current_section_id);
			insertEntity.setLine_id(current_line_id);
			if (current_line_id == null) insertEntity.setLine_id("00000000000");
			if (insertEntity.getMaterial_id() != null) {
				// 在线维修消耗所需的工位
				insertEntity.setPosition_id(position_id);
			}
			if ("true".equals(doSubmit)) {
				insertEntity.setApply_time(new Date());
			}
			if ("true".equals(doSubmit) && iAmLeader) {
				// 线长确认
				insertEntity.setApply_time(new Date());
				insertEntity.setConfirmer_id(user.getOperator_id());
			}
			// 新建记录
			caMapper.insert(insertEntity);

			// 并取得Key
			consumableApplicationForm.setConsumable_application_key(commonMapper.getLastInsertID());
		} else if ("true".equals(doSubmit) || SUBMIT_TYPE_EXCHANGE.equals(doSubmit)) {
			// 更新提交
			ConsumableApplicationEntity updateEntity = new ConsumableApplicationEntity();
			CopyOptions cop = new CopyOptions();
			cop.include("consumable_application_key", "material_id", "apply_reason");
			
			BeanUtil.copyToBean(form, updateEntity, cop);
			if ("true".equals(doSubmit)) {
				// 日常
				updateEntity.setApply_time(new Date());
			}
			if (iAmLeader) {
				// 线长确认
				updateEntity.setApply_time(new Date());
				updateEntity.setConfirmer_id(user.getOperator_id());
			}
			caMapper.confirm(updateEntity);
		}

		// 处理(插入/修改/删除)申请单明细消耗品
		for (ConsumableApplicationForm detail : formList) {
			ConsumableApplicationDetailEntity cadEntity = new ConsumableApplicationDetailEntity();
			CopyOptions cop = new CopyOptions();
			cop.include("partial_id", "apply_method", "apply_quantity", "pack_method", "cut_length");
			BeanUtil.copyToBean(detail, cadEntity, cop);
			cadEntity.setConsumable_application_key(consumableApplicationForm.getConsumable_application_key());
			if ("me".equals(detail.getPetitioner_id())) {
				cadEntity.setPetitioner_id(operator_id);
			} else {
				cadEntity.setPetitioner_id(detail.getPetitioner_id());
			}

			if ("new".equals(detail.getFlg())) {
				cadMapper.insertDetail(cadEntity);
			} else if ("edit".equals(detail.getFlg())) {
				cadMapper.editApplyQuantity(cadEntity);
			} else if ("delete".equals(detail.getFlg())) {
				cadMapper.deleteDetail(cadEntity);

				ConsumableApplicationDetailEntity org = cadMapper.checkConsumableApplicationParticular(cadEntity);
				if (org != null) {
					cadEntity.setCut_length(null);
					cadMapper.deleteParticular(cadEntity);
				}
			}

			// 处理附加信息（热缩管裁剪长度）
			if (cadEntity.getCut_length() != null) {
				ConsumableApplicationDetailEntity org = cadMapper.checkConsumableApplicationParticular(cadEntity);

				if (org == null) {
					cadMapper.insertParticular(cadEntity);
				} else {
					if (org.getCut_length() != cadEntity.getCut_length()) {
						cadMapper.updateParticular(cadEntity);
					}
				}
			}
		}

		// 发送通知
		if ("true".equals(doSubmit) || SUBMIT_TYPE_EXCHANGE.equals(doSubmit)) {
			PostMessageMapper postMessageMapper = conn.getMapper(PostMessageMapper.class);

			Set<String> postSet = new HashSet<>();

			// 工程线长
			OperatorMapper lMapper = conn.getMapper(OperatorMapper.class);
			OperatorEntity condition = new OperatorEntity();
			condition.setSection_id(current_section_id);
			condition.setLine_id(current_line_id);
			condition.setRole_id(RvsConsts.ROLE_LINELEADER);
			List<OperatorNamedEntity> leaderList = lMapper.searchOperator(condition);
			for (OperatorNamedEntity leader : leaderList) {
				postSet.add(leader.getOperator_id());
			}
			condition.setRole_id(RvsConsts.ROLE_QT_MANAGER);
			leaderList = lMapper.searchOperator(condition);
			for (OperatorNamedEntity leader : leaderList) {
				postSet.add(leader.getOperator_id());
			}
			condition.setRole_id(RvsConsts.ROLE_QA_MANAGER);
			leaderList = lMapper.searchOperator(condition);
			for (OperatorNamedEntity leader : leaderList) {
				postSet.add(leader.getOperator_id());
			}
	
			PostMessageEntity postMessageEntity = new PostMessageEntity();
			postMessageEntity.setLevel(1);
			postMessageEntity.setSender_id(operator_id);
			postMessageEntity.setRoot_post_message_id(consumableApplicationForm.getConsumable_application_key());

			// 直接提交
			if ("true".equals(doSubmit)) {
				condition = new OperatorEntity();
				condition.setRole_id(RvsConsts.ROLE_FACTINLINE);
				List<OperatorNamedEntity> facterList = lMapper.searchOperator(condition);
				for (OperatorNamedEntity facter : facterList) {
					postSet.add(facter.getOperator_id());
				}

				//发送推送
				postMessageEntity.setReason(PostMessageService.CONSUMABLE_ORDER_DIRECT);
	
				String lineName = user.getSection_name() + CommonStringUtil.nullToAlter(user.getLine_name(), "");
				postMessageEntity.setContent(user.getName() + "发送了" + lineName 
						+ "的<a href='consumable_manage.do?page=page_apply&key="
						+ consumableApplicationForm.getConsumable_application_key() + "'>消耗品领用申请单[" 
						+ cmf.getApplication_no() + "]</a>，请处理。");

				String from = user.getProcess_code();
				if (isEmpty(from)) {
					from = user.getLine_name();
				}
				// 提交提醒声音
				if (iAmLeader && SUBMIT_TYPE_EXCHANGE.equals(consumableApplicationForm.getFlg())) 
					RvsUtils.sendTrigger("http://localhost:8080/rvspush/trigger/ring_consumble_apply/" + from + "/" + cmf.getApplication_no());

			} else if (SUBMIT_TYPE_EXCHANGE.equals(doSubmit)) {
				// 提醒线长
				//发送推送
				postMessageEntity.setReason(PostMessageService.CONSUMABLE_ORDER_CONFIRM);
				postMessageEntity.setContent(user.getName() + "发送了有即时更换需求" 
						+ "的<a href='consumable_manage.do?page=page_apply&key="
						+ consumableApplicationForm.getConsumable_application_key() + "'>消耗品领用申请单[" 
						+ cmf.getApplication_no() + "]</a>，请确认并发送。");
			}

			postMessageMapper.createPostMessage(postMessageEntity);

			String last_insert_id = commonMapper.getLastInsertID();

			//推送到人
			String pushString = "";
			for(String receiver_id:postSet){
				if (!receiver_id.equals(operator_id)) { // 不是本人
					postMessageEntity = new PostMessageEntity();
					postMessageEntity.setPost_message_id(last_insert_id);
					postMessageEntity.setReceiver_id(receiver_id);
					postMessageMapper.createPostMessageSendation(postMessageEntity);

					// push
					pushString += "/" + receiver_id;
				}
			}

			if (pushString.length() > 0) {
				// 通知
				HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
				httpclient.start();
				try {
		            HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/postMessage/" + pushString);
		            _logger.info("finger:"+request.getURI());
		            httpclient.execute(request, null);
					Thread.sleep(100);
		        } catch (Exception e) {
				} finally {
					httpclient.shutdown();
				}
			}

		}

		return false;
	}

	public List<MaterialForm> getPerformanceList(String section_id,
			String position_id, SqlSession conn) {
		List<MaterialForm> ret = new ArrayList<MaterialForm>();

		if (position_id != null) {
			ProductionFeatureMapper mapper = conn.getMapper(ProductionFeatureMapper.class);

			List<MaterialEntity> list = mapper.getSikakeMaterialOfPosition(section_id, position_id);

			BeanUtil.copyToFormList(list, ret ,CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);

			if (ret.size() == 0 && ( // TODO 暂不入系统的工位
					"00000000048".equals(position_id) 
					|| "00000000049".equals(position_id) 
					|| "00000000050".equals(position_id) 
					)) { 
				MaterialForm kariForm = new MaterialForm();
				kariForm.setMaterial_id("0000000000X");
				ret.add(kariForm);
			}
			if (RvsConsts.POSITION_QA_601.equals(position_id)) {
				MaterialForm kariForm = new MaterialForm();
				kariForm.setMaterial_id("0000000000Y");
				ret.add(kariForm);
			}
		}
		return ret;
	}

	public String getSupplyOrderPrivacy(LoginData loginData) {
		for (PositionEntity position : loginData.getPositions()) {
			if ("00000000057".equals(position.getPosition_id())) {
				return "1";
			}
		}
		List<Integer> privacies = loginData.getPrivacies();
		// 判断自身是否有线长权限
		if (privacies.contains(RvsConsts.PRIVACY_LINE)
				|| privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)
				|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT)) {
			return "2";
		}
		return null;
	}

	/**
	 * 取得CCD盖玻璃预发放零件
	 */
	public Map<String, ConsumableApplicationDetailEntity> getCcdAdvancedByMaterial(String material_id, SqlSession conn) {
		ConsumableApplicationMapper appMapper = conn.getMapper(ConsumableApplicationMapper.class);
		ConsumableApplicationEntity app = appMapper.getCcdAdvancedByMaterial(material_id);
		if (app == null) {
			return null;
		}

		// 取得现有订购明细
		ConsumableApplicationDetailMapper appdMapper = conn.getMapper(ConsumableApplicationDetailMapper.class);
		List<ConsumableApplicationDetailEntity> orgLst = appdMapper.getDetailSimple(app.getConsumable_application_key());

		Map<String, ConsumableApplicationDetailEntity> retMap = new HashMap<String, ConsumableApplicationDetailEntity>();
		for (ConsumableApplicationDetailEntity org : orgLst) {
			retMap.put(org.getPartial_id(), org);
		}

		return retMap;
	}

	/**
	 * 提交CCD盖玻璃
	 * 
	 * @param request
	 * @param user
	 * @param conn
	 * @throws Exception 
	 */
	public void commitAssemble(HttpServletRequest request, LoginData user,
			SqlSessionManager conn) throws Exception {
		List<ConsumableApplicationDetailEntity> entities = new AutofillArrayList<ConsumableApplicationDetailEntity>(ConsumableApplicationDetailEntity.class);
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
					case "partial_id" : entities.get(icounts).setPartial_id(CommonStringUtil.fillChar(value[0], '0', 11, true)); break;
					case "recept_quantity" : entities.get(icounts).setApply_quantity(Integer.parseInt(value[0])); break;
					}
				}
			}
		}

		ConsumableApplicationMapper appMapper = conn.getMapper(ConsumableApplicationMapper.class);
		ConsumableApplicationEntity app = appMapper.getCcdAdvancedByMaterial(material_id);
		String key = null;
		if (app == null) {
			MaterialService mServ = new MaterialService();
			MaterialForm mBean = mServ.loadSimpleMaterialDetail(conn, material_id);
			ConsumableApplicationEntity insertEntity = new ConsumableApplicationEntity();
			insertEntity.setApplication_no("CCD" + mBean.getSorc_no());
			insertEntity.setSection_id(user.getSection_id());
			insertEntity.setLine_id(user.getLine_id());
			insertEntity.setPosition_id(user.getPosition_id());
			insertEntity.setApply_time(new Date());
			insertEntity.setMaterial_id(material_id);
			insertEntity.setApply_reason("CCD盖玻璃消耗品提前发放");
			insertEntity.setConfirmer_id(user.getOperator_id());
			appMapper.insert(insertEntity);

			CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
			key = commonMapper.getLastInsertID();

			// 更新为自动发放
			appMapper.autoSupply(key);

		} else {
			key = app.getConsumable_application_key();
		}

		// 取得现有订购明细
		ConsumableApplicationDetailMapper appdMapper = conn.getMapper(ConsumableApplicationDetailMapper.class);
		List<ConsumableApplicationDetailEntity> orgLst = appdMapper.getDetailSimple(key);
		Map<String, Integer> orgApplyMap = new HashMap<String, Integer>();
		Map<String, Integer> changedApplyMap = new HashMap<String, Integer>();
		for (ConsumableApplicationDetailEntity orgEntity : orgLst) {
			orgApplyMap.put(orgEntity.getPartial_id(), orgEntity.getApply_quantity());
		}

		// 更新申请单明细
		for (ConsumableApplicationDetailEntity entity : entities) {
			entity.setConsumable_application_key(key);
			entity.setApply_method(3);
			entity.setPetitioner_id(user.getOperator_id());
			entity.setPack_method(1);

			if (!orgApplyMap.containsKey(entity.getPartial_id())) {
				if (entity.getApply_quantity() > 0) {
					appdMapper.insertDetail(entity);
					changedApplyMap.put(entity.getPartial_id(), -entity.getApply_quantity());
				}
			} else {
				if (entity.getApply_quantity() == 0) {
					appdMapper.deleteDetail(entity);
					changedApplyMap.put(entity.getPartial_id(), orgApplyMap.get(entity.getPartial_id()));
				} else if (entity.getApply_quantity() != orgApplyMap.get(entity.getPartial_id())) {
					appdMapper.editApplyQuantity(entity);
					changedApplyMap.put(entity.getPartial_id(), 
							orgApplyMap.get(entity.getPartial_id()) - entity.getApply_quantity());
				}
			}
		}

		if (!changedApplyMap.isEmpty()) {
			// 更新为自动发放
			appdMapper.autoSupply(key);

			// 取得当前在库数量
			Map<String, Integer> nowAvailableInventoryMap = new HashMap<String, Integer>();
			ConsumableListMapper clMapper = conn.getMapper(ConsumableListMapper.class);
			Set<String> targetPartialIds = changedApplyMap.keySet();
			if (targetPartialIds.size() > 0) {
				List<ConsumableListEntity> availableInventories = clMapper.getAvailableInventories(targetPartialIds);
				for (ConsumableListEntity availableInventory : availableInventories) {
					nowAvailableInventoryMap.put(CommonStringUtil.fillChar("" + availableInventory.getPartial_id(), '0', 11, true), 
							availableInventory.getAvailable_inventory());
				}
			}

			// 更新库存
			for (String partial_id : changedApplyMap.keySet()) {
				Integer available_inventory = nowAvailableInventoryMap.get(partial_id);
				available_inventory += changedApplyMap.get(partial_id);

				ConsumableApplicationDetailEntity entity = new ConsumableApplicationDetailEntity();
				entity.setPartial_id(partial_id);
				entity.setAvailable_inventory(available_inventory);
				appdMapper.updateAvailableInventory(entity);
			}
		}

	}
}

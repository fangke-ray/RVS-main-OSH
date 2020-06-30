package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.ComponentManageForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.inline.SoloProductionFeatureMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.partial.ComponentManageMapper;
import com.osh.rvs.service.PostMessageService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;

public class ComponentManageService {
	
	public static final String POSITION_ID_28 = "28";
	
	/**
	 * NS组件库存管理数据检索
	 * @param componentManageEntity
	 * @param conn
	 * @return
	 */
	public List<ComponentManageForm> searchComponentManage(ComponentManageEntity componentManageEntity, SqlSession conn) {
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		
		// NS组件状态检索条件设置（String To List）
		String steps = componentManageEntity.getSearch_step();
		List<String> searchSteps = new ArrayList<String>();
		if (steps != null) {
			String[] arrStep = steps.split(",");
			for (String step : arrStep) {
				searchSteps.add(step);
			}
			componentManageEntity.setSearch_step_list(searchSteps);
		}
		List<ComponentManageForm> resultForm = new ArrayList<ComponentManageForm>();
		List<ComponentManageEntity> resultList = dao.searchComponentManage(componentManageEntity);
		BeanUtil.copyToFormList(resultList, resultForm, null, ComponentManageForm.class);
		return resultForm;
	}
	
	/** 零件集合 **/
	public List<ComponentManageForm> getPartialAutoCompletes(String code, SqlSession conn) {
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		List<ComponentManageForm> resultForm = new ArrayList<ComponentManageForm>();
		List<ComponentManageEntity> resultList =dao.getAllPartial(code);
		BeanUtil.copyToFormList(resultList, resultForm, null, ComponentManageForm.class);
		return resultForm;
	}
	
	/**
	 * 虚拟单号订购子零件
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void insertVirtualManage(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		ComponentManageEntity insertBean = new ComponentManageEntity();
		BeanUtil.copyToBean(form, insertBean, null);

		insertBean.setOrigin_material_id("0");
		insertBean.setStep("0");
		/* Consumable_manage表插入数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.insert(insertBean);
	}
	
	/**
	 * 订购子零件
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void insert(ActionForm form, HttpSession session, SqlSessionManager conn)
			throws Exception {
		ComponentManageEntity insertBean = new ComponentManageEntity();
		BeanUtil.copyToBean(form, insertBean, null);

		/* Consumable_manage表插入数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.insert(insertBean);
	}
	
	/**
	 * 子零件入库处理
	 * 
	 * @param updateBean
	 * @param conn
	 */
	public void partialInstock(ComponentManageEntity updateBean, HttpSession session, SqlSessionManager conn)
			throws Exception {

		/* Consumable_manage表更新数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.partialInstock(updateBean);
	}	
	
	/**
	 * 子零件出库处理
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void partialOutstock(ComponentManageEntity updateBean, HttpSession session, SqlSessionManager conn)
			throws Exception {
		
		// 子零件状态
		updateBean.setStep("2");
		/* Consumable_manage表更新数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.partialOutstock(updateBean);
	}	
	
	/**
	 * 序列号生成
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public String getNewSerialNo(String identifyCode, SqlSessionManager conn)
			throws Exception {
		// 第一部分6位 为投入日期(current_date)的yyMMdd格式，如200619
		String serialNo1 = DateUtil.toString(new Date(), "yyMMdd");
		
		/* Consumable_manage表更新数据 */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		String serialNo3 = dao.getNewSerialNo(identifyCode);
		
		// 序列号为3部分
		String maxSerialNo = serialNo1.concat(identifyCode).concat(serialNo3);
		
		return maxSerialNo;
	}
	
	/**
	 * 在（SOLO_PRODUCTION_FEATURE）表新建记录
	 * @param productionFeatureEntity
	 * @param conn
	 * @throws Exception
	 */
	public void insertToSoloProductionFeature(SoloProductionFeatureEntity productionFeatureEntity, SqlSessionManager conn) throws Exception {
		SoloProductionFeatureMapper mapper = conn.getMapper(SoloProductionFeatureMapper.class);
		// TODO position_id暂定28. 之后会提供专门的取得方法
		productionFeatureEntity.setPosition_id("28");
		// section_id
		productionFeatureEntity.setSection_id("1");
		// judge_date
		productionFeatureEntity.setJudge_date(null);
		// #{pace}
		productionFeatureEntity.setPace(0);
		// operator_id
		productionFeatureEntity.setOperator_id("0");
		// operate_result
		productionFeatureEntity.setOperate_result(0);

		mapper.insert(productionFeatureEntity);
	}	
	
	/**
	 * 在（POST_MESSAGE）表新建记录
	 * @param resultMsg
	 * @param conn
	 * @throws Exception
	 */
	public void insertToPOST(String resultMsg, HttpSession session, SqlSessionManager conn) throws Exception {
		LoginData user = (LoginData)session.getAttribute(RvsConsts.SESSION_USER);
		
		PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
		
		PostMessageEntity pmEntity = new PostMessageEntity();
		pmEntity.setLevel(1);
		pmEntity.setReason(PostMessageService.NS_COMPONENT_MANAGE);
		pmEntity.setSender_id(user.getOperator_id());
		pmEntity.setContent(resultMsg);
		
		// （POST_MESSAGE）表新建记录
		pmMapper.createPostMessage(pmEntity);
		
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String lastInsertID = commonMapper.getLastInsertID();
		pmEntity.setPost_message_id(lastInsertID);

		// 查询系统管理员
		OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

		// 工位关注者
		List<String> operatorId0 = oMapper.getPositionsOfOperator(POSITION_ID_28, "2");
		for (String operatorId : operatorId0) {
			pmEntity.setReceiver_id(operatorId);
			pmMapper.createPostMessageSendation(pmEntity);
		}

		// NS 线长
		List<String> operatorId2 = oMapper.getOperatorByRoleAndLine("5", "13");
		for (String operatorId : operatorId2) {
			pmEntity.setReceiver_id(operatorId);
			pmMapper.createPostMessageSendation(pmEntity);
		}
		
		// 触发http://localhost:8080/rvspush/trigger/in/28/1/0/0
		RvsUtils.sendTrigger("http://localhost:8080/rvspush/trigger/in/28/1/0/0");
	}
	
	
	/**
	 * 序列号生成
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public ComponentManageEntity searchComponentManageDetail(String componentKey, SqlSessionManager conn)
			throws Exception {

		/* Consumable_manage表详细数据取得  */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		ComponentManageEntity detail = dao.searchComponentManageDetail(componentKey);

		return detail;
	}
	
	/**
	 * 判定指定作业是否正在进行中
	 * @param conn
	 * @return String
	 */
	public String getPosition(SoloProductionFeatureEntity soloEntity, SqlSessionManager conn) 
			throws Exception {

		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		List<SoloProductionFeatureEntity> allSole = dao.findPositionByModelAndSerialNo(soloEntity);
		
		if (allSole == null || allSole.size() == 0) {
			return null;
		} else {
			return allSole.get(0).getPosition_id();
		}
	}
	
	/**
	 * 独立工位操作记录删除
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void deleteSoloProductionFeature(SoloProductionFeatureEntity soloEntity, SqlSessionManager conn)
			throws Exception {

		/* 独立工位操作记录删除  */
		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		dao.deleteSoloByModelAndSerialNo(soloEntity);
	}	
	
	/**
	 * 组件废弃处理
	 * 
	 * @param componentManageEntity
	 * @param conn
	 */
	public void cancleManage(ComponentManageEntity componentManageEntity, SqlSessionManager conn)
			throws Exception {

		/* 组件废弃处理  */
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		dao.cancleManage(componentManageEntity);
	}
	

	/**
	 * 取得当前仓库所有库存编号
	 * @param conn
	 * @return List<String>
	 */
	public List<String> getNSStock(SqlSession conn) {
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		return dao.getNSStock();
	}
}

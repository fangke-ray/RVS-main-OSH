package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.AlarmMesssageSendationEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.mapper.inline.MaterialProcessAssignMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.mapper.partial.PartialReceptMapper;
import com.osh.rvs.service.PositionService;

import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 零件签收
 * 
 * @author lxb
 * 
 */
public class PartialReceptService {
	/**
	 * 零件签收对象一览
	 * 
	 * @param form
	 * @param lineID 
	 * @param conn
	 * @return 如果有数据返回responseList 没有返回null
	 */
	public List<MaterialPartialForm> secrchMaterialPartial(ActionForm form, String lineID, String sectionId, SqlSession conn) {
		MaterialPartialMapper dao = conn.getMapper(MaterialPartialMapper.class);

		MaterialPartialDetailForm detailForm = (MaterialPartialDetailForm) form;
		MaterialPartialEntity entity = new MaterialPartialEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(detailForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		entity.setLine_id(lineID);
		entity.setSection_id(sectionId);

		List<MaterialPartialEntity> responseEntityList = dao.searchMaterialPartialRecept(entity);

		List<MaterialPartialForm> responseList = new ArrayList<MaterialPartialForm>();
		// 复制数据到表单对象
		BeanUtil.copyToFormList(responseEntityList, responseList, null, MaterialPartialForm.class);

		if (responseList.size() > 0) {
			return responseList;
		} else {
			return null;
		}
	}

	/**
	 * 零件一览
	 * 
	 * @param form
	 * @param request
	 * @param conn
	 * @return 如果有数据返回responseFormList 没有返回null
	 */
	public List<MaterialPartialDetailForm> searchPartialRecept(ActionForm form, HttpServletRequest request,
			SqlSession conn) {
		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String lineID = loginData.getLine_id();// 工程 ID

		MaterialPartialDetailForm materialPartialDetailForm = (MaterialPartialDetailForm) form;
		MaterialPartialDetailEntity entity = new MaterialPartialDetailEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(materialPartialDetailForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		entity.setLine_id(lineID);

		PartialReceptMapper dao = conn.getMapper(PartialReceptMapper.class);

		List<MaterialPartialDetailEntity> responseEntityList = dao.secrchPartialRecept(entity);

		List<MaterialPartialDetailForm> responseFormList = new ArrayList<MaterialPartialDetailForm>();
		// 复制数据到表单对象
		BeanUtil.copyToFormList(responseEntityList, responseFormList, null, MaterialPartialDetailForm.class);

		if (responseFormList.size() > 0) {
			return responseFormList;
		} else {
			return null;
		}
	}

	
	public void updatePartialRecept(HttpServletRequest req,SqlSessionManager conn) {
		PartialReceptMapper dao = conn.getMapper(PartialReceptMapper.class);	
		List<MaterialPartialDetailEntity> keys = this.getKeysAndWaitingReceiveQuantity(req.getParameterMap(), req.getSession());

		if(keys.size()>0){
			MaterialPartialDetailEntity entity=null;
			for(int i=0;i<keys.size();i++){
				entity=keys.get(i);
				dao.updatePartialRecept(entity);
			}
		}
	}
	
	public List<MaterialPartialDetailEntity> getKeysAndWaitingReceiveQuantity(Map<String, String[]> parameters, HttpSession session) {

		List<MaterialPartialDetailEntity> keys = new AutofillArrayList<MaterialPartialDetailEntity>(MaterialPartialDetailEntity.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 整理提交数据
		for (String parameterKey : parameters.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("keys".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameters.get(parameterKey);

					if ("material_partial_detail_key".equals(column)) {
						keys.get(icounts).setMaterial_partial_detail_key(value[0]);
						keys.get(icounts).setR_operator_id(user.getOperator_id());
					}else if("recept_quantity".equals(column)){
						keys.get(icounts).setRecept_quantity(Integer.valueOf(value[0]));
					}
				}
			}
		}

		return keys;
	}

	/**
	 * 查询工位上的未签收零件
	 * @param material_id
	 * @param position_id
	 * @param conn
	 * @return
	 */
	public List<MaterialPartialDetailEntity> getPartialsForPosition(
			String material_id, String position_id, SqlSession conn) {
		return getPartialsForPosition(material_id, position_id, conn, false);
	}
	public List<MaterialPartialDetailEntity> getPartialsForPosition(
			String material_id, String position_id, SqlSession conn, boolean passSnout) {
		PartialReceptMapper mapper = conn.getMapper(PartialReceptMapper.class);

		// 映射工位
		List<MaterialPartialDetailEntity> ret = null;
		if (PositionService.getPositionMappings(conn).containsKey(position_id)) {
			ret = mapper.getPartialDetailByPosition(material_id, position_id);

			MaterialProcessAssignMapper mpaMapper = conn.getMapper(MaterialProcessAssignMapper.class);
			List<String> selectedMappingsIds = mpaMapper.getSelectedMappingsId(material_id, position_id);
			for (String selectedMappingsId : selectedMappingsIds) {
				ret.addAll(mapper.getPartialDetailByPosition(material_id, selectedMappingsId));
			}
		} else {
			ret = mapper.getPartialDetailByPosition(material_id, position_id);
		}

		boolean completed = true;
		for (MaterialPartialDetailEntity mpdEntity: ret) {
			if (mpdEntity.getWaiting_receive_quantity() > 0) {
				if (passSnout) {
					// 如果是先端组件非标零件可以略过
					String partial_id = mapper.getPremakePartialAddition(mpdEntity.getPartial_id());
					if (partial_id == null) {
						completed = false;
						// break;
					}
				} else {
					completed = false;
					// break;
				}
			}

			// 如果是组装组件，给出序列号信息
			if (mpdEntity.getStatus() == 7) {
				mpdEntity.setAppend("7");
				if (mpdEntity.getWaiting_quantity() > 0) {
					// mpdEntity.setName("未分配");
				} else {
					ComponentManageService cmService = new ComponentManageService();

					String serialNo = cmService.getSerialNosForTargetMaterial(material_id, conn);
					// name作为序列号
					mpdEntity.setName(serialNo);
				}
			}
		}
		if (completed) {
			return null;
		} else {
			return ret;
		}
	}

	public void updatePartialUnnessaray(HttpServletRequest req,
			SqlSessionManager conn, String operater_id) throws Exception {
		PartialReceptMapper prMapper = conn.getMapper(PartialReceptMapper.class);	
		List<MaterialPartialDetailEntity> keys = this.getKeysAndWaitingReceiveQuantity(req.getParameterMap(), req.getSession());

		if(keys.size()>0){
			MaterialPartialDetailEntity entity=null;
			for(int i=0;i<keys.size();i++){
				entity=keys.get(i);
				prMapper.updatePartialUnnessaray(entity);
			}
		}

		String alarm_id = req.getParameter("alarm_id");
		String reason = req.getParameter("reason");

		AlarmMesssageMapper amMapper = conn.getMapper(AlarmMesssageMapper.class);	
		AlarmMesssageSendationEntity sendation = new AlarmMesssageSendationEntity();
		sendation.setAlarm_messsage_id(alarm_id);
		sendation.setSendation_id(operater_id);

		AlarmMesssageSendationEntity me = amMapper.getBreakAlarmMessageBySendation(alarm_id, operater_id);
		sendation.setRed_flg(0);
		if (me == null) {
			// 新建一条
			sendation.setComment(reason);
			amMapper.createAlarmMessageSendation(sendation);
		} else {
			if (me.getComment() != null) {
				sendation.setComment(me.getComment() + reason);
			} else {
				sendation.setComment(reason);
			}
			amMapper.updateAlarmMessageSendation(sendation);
		}
	}

}

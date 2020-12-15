package com.osh.rvs.service.manage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.AlarmMesssageEntity;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.inline.PauseFeatureEntity;
import com.osh.rvs.bean.manage.NewPhenomenonEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.manage.NewPhenomenonForm;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.manage.NewPhenomenonMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class NewPhenomenonService {

	private static Map<String, List<String>> locationMap = new LinkedHashMap<String, List<String>>();
	static {
		locationMap.put("其他", new ArrayList<String>());
		locationMap.get("其他").add("其他");

		{
			List<String> l = new ArrayList<String>();
			locationMap.put("导光插头部", l);
			l.add("其他");
			l.add("插头本体");
			l.add("铭牌标签");
			l.add("导光杆");
			l.add("电气触点");
			l.add("S电缆插座");
			l.add("吸引接头");
			l.add("送气管");
			l.add("送气送水接头");
			l.add("电气接头");
			l.add("锥形套");
		}

		{
			List<String> l = new ArrayList<String>();
			locationMap.put("导光软管部", l);
			l.add("其他");
			l.add("导光软管");
			l.add("锥形套");
		}

		{
			List<String> l = new ArrayList<String>();
			locationMap.put("操作部", l);

			l.add("其他");
			l.add("目镜");
			l.add("角度旋钮");
			l.add("角度卡锁");
			l.add("开关盒");
			l.add("遥控按钮");
			l.add("S盖");
			l.add("色环");
			l.add("把持部");
			l.add("吸引按钮");
			l.add("吸引活塞");
			l.add("送水送气按钮");
			l.add("送水送气活塞");
			l.add("钳子管道口");
			l.add("锥形套");
		}

		{
			List<String> l = new ArrayList<String>();
			locationMap.put("插入部", l);

			l.add("其他");
			l.add("锥形套");
			l.add("插入管");
			l.add("弯曲部");
			l.add("先端部");
		}

		{
			List<String> l = new ArrayList<String>();
			locationMap.put("光学视管插入部", l);

			l.add("其他");
			l.add("视管本体");
			l.add("物镜部");
		}

		{
			List<String> l = new ArrayList<String>();
			locationMap.put("光学视管导光束接头部", l);

			l.add("其他");
			l.add("导光束接头");
			l.add("序列号环");
		}

		{
			List<String> l = new ArrayList<String>();
			locationMap.put("光学视管目镜部", l);

			l.add("其他");
			l.add("目镜杯");
			l.add("连接器");
		}

	}

	/**
	 * 检索不良现象
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<NewPhenomenonForm> search(ActionForm form, SqlSession conn,
			List<MsgInfo> errors) {
		NewPhenomenonMapper mapper = conn.getMapper(NewPhenomenonMapper.class);

		NewPhenomenonEntity condition = new NewPhenomenonEntity();
		BeanUtil.copyToBean(form, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<NewPhenomenonEntity> lEntites = mapper.searchNewPhenomenons(condition);
		List<NewPhenomenonForm> ret = new ArrayList<NewPhenomenonForm>();

		BeanUtil.copyToFormList(lEntites, ret, CopyOptions.COPYOPTIONS_NOEMPTY, NewPhenomenonForm.class);
		return ret;
	}

	public NewPhenomenonForm getNewNewPhenomenon(String key, SqlSession conn) {
		NewPhenomenonMapper mapper = conn.getMapper(NewPhenomenonMapper.class);
		NewPhenomenonEntity entity = mapper.getNewPhenomenon(key);
		if (entity == null) return null;

		NewPhenomenonForm retForm = new NewPhenomenonForm();
		BeanUtil.copyToForm(entity, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);

		MaterialMapper mMapper = conn.getMapper(MaterialMapper.class);
		MaterialEntity mBean = mMapper.loadMaterialDetail(entity.getMaterial_id());
		
		retForm.setLevel_name(CodeListUtils.getValue("material_level", "" + mBean.getLevel(), "/"));
		retForm.setCategory_name(mBean.getModel_name());
		retForm.setSerial_no(mBean.getSerial_no());

		return retForm;
	}

	public NewPhenomenonForm getNewNewPhenomenonFormAlarm(String key, SqlSession conn) {
		AlarmMesssageMapper mapper = conn.getMapper(AlarmMesssageMapper.class);
		AlarmMesssageEntity breakAlarmMessage = mapper.getBreakAlarmMessageByKey(key);
		PauseFeatureEntity pEntity = mapper.getBreakOperatorMessageByID(key);

		NewPhenomenonForm retForm = new NewPhenomenonForm();

		retForm.setKey(key);
		retForm.setMaterial_id(breakAlarmMessage.getMaterial_id());
		retForm.setLevel_name(CodeListUtils.getValue("material_level", "" + breakAlarmMessage.getLevel(), "/"));
		retForm.setOccur_time(DateUtil.toString(breakAlarmMessage.getOccur_time(), DateUtil.DATE_PATTERN));
		retForm.setOmr_notifi_no(breakAlarmMessage.getSorc_no());
		retForm.setCategory_name(breakAlarmMessage.getModel_name());
		retForm.setSerial_no(breakAlarmMessage.getSerial_no());
		retForm.setLine_name(breakAlarmMessage.getLine_name());
		retForm.setKind(CommonStringUtil.fillChar("" + breakAlarmMessage.getKind(), '0', 2, true) );

		if ("07".equals(retForm.getKind())) {
			retForm.setLocation_desc("其他");
		}
		if (pEntity != null)
			retForm.setDescription(pEntity.getComments());
		return retForm;
	}

	public Map<String, List<String>> getLocationMap(SqlSession conn) {
		NewPhenomenonMapper mapper = conn.getMapper(NewPhenomenonMapper.class);
		List<NewPhenomenonEntity> allLocations = mapper.getAllLocations();

		for (NewPhenomenonEntity location : allLocations) {
			if (!locationMap.containsKey(location.getLocation_group_desc())) {
				locationMap.put(location.getLocation_group_desc(), new ArrayList<String>());
			}
			List<String> mapOfLocation = locationMap.get(location.getLocation_group_desc());
			if (!mapOfLocation.contains(location.getLocation_desc())) {
				mapOfLocation.add(location.getLocation_desc());
			}
		}
		return locationMap;
	}

	/**
	 * 
	 * @param form
	 * @param session
	 * @param needPost 更新后发送
	 * @param conn
	 * @param errors
	 */
	public void update(ActionForm form, HttpSession session,
			boolean needPost, SqlSessionManager conn, List<MsgInfo> errors) {
		NewPhenomenonMapper mapper = conn.getMapper(NewPhenomenonMapper.class);

		NewPhenomenonEntity entity = new NewPhenomenonEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 判断存在
		NewPhenomenonEntity hit = mapper.getNewPhenomenon(entity.getKey());
		if (hit == null) {
			entity.setOperator_id(user.getOperator_id());
			mapper.insert(entity);
		}

		if (needPost) {
			entity.setDetermine_operator_id(user.getOperator_id());
			mapper.update(entity);

			conn.commit();

			RvsUtils.sendTrigger("http://10.220.142.227:8080/rvsIf/phenomenon/" + entity.getKey());
		} else if (hit != null) {
			mapper.update(entity);
		}

	}

}

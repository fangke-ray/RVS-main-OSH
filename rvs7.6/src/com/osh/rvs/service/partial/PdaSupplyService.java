package com.osh.rvs.service.partial;

import java.util.Calendar;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.ConsumableListEntity;
import com.osh.rvs.bean.partial.ConsumableSupplyEntity;
import com.osh.rvs.bean.pda.PdaSupplyEntity;
import com.osh.rvs.form.pda.PdaSupplyForm;
import com.osh.rvs.mapper.partial.ConsumableListMapper;
import com.osh.rvs.mapper.partial.ConsumableSupplyMapper;
import com.osh.rvs.mapper.pda.PdaSupplyMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class PdaSupplyService {
	public PdaSupplyForm search(ActionForm form, SqlSession conn,List<MsgInfo> errors) {

		// 复制表单数据对象
		PdaSupplyEntity pdaSupplyEntity = new PdaSupplyEntity();
		BeanUtil.copyToBean(form, pdaSupplyEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		String code = pdaSupplyEntity.getCode().trim();//消耗品代码
		
		//判断是否是消耗品
		ConsumableListMapper consumableListMapper = conn.getMapper(ConsumableListMapper.class);
		List<ConsumableListEntity> list =  consumableListMapper.getAdjustSearch(code);
		if(list.size()==0 || list.size() > 1){
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg("消耗品:" + code + "不存在!");
			errors.add(msg);
			return null;
		}
		ConsumableListEntity consumableListEntity = list.get(0);
		
		
		PdaSupplyMapper dao = conn.getMapper(PdaSupplyMapper.class);
		pdaSupplyEntity.setPartial_id(String.valueOf(consumableListEntity.getPartial_id()));
		PdaSupplyEntity returnBean = dao.search(pdaSupplyEntity);
		
		PdaSupplyForm pdaSupplyForm = new PdaSupplyForm();

		if (returnBean != null) {
			// 复制数据到表单对对象
			BeanUtil.copyToForm(returnBean, pdaSupplyForm,CopyOptions.COPYOPTIONS_NOEMPTY);
			String type = pdaSupplyForm.getType();
			pdaSupplyForm.setType_name(CodeListUtils.getValue("consumable_type", type, ""));
		}

		return pdaSupplyForm;
	}
	
	
	/**
	 * 今日已入库数量
	 * @param conn
	 * @return
	 */
	public int getCurrentDaySupplyNum(SqlSession conn){
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		ConsumableSupplyMapper consumableSupplyMapper = conn.getMapper(ConsumableSupplyMapper.class);
		ConsumableSupplyEntity entity = new ConsumableSupplyEntity();
		entity.setSupply_date(cal.getTime());
		
		List<ConsumableSupplyEntity> list = consumableSupplyMapper.searchSupplyList(entity);
		
		return list.size();
		
	}
	
}

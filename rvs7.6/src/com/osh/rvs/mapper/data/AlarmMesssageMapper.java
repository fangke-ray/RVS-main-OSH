package com.osh.rvs.mapper.data;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.AlarmMesssageEntity;
import com.osh.rvs.bean.data.AlarmMesssageSendationEntity;
import com.osh.rvs.bean.inline.PauseFeatureEntity;

public interface AlarmMesssageMapper {

	public void createAlarmMessage(AlarmMesssageEntity entity) throws Exception;

	public void createAlarmMessageSendation(AlarmMesssageSendationEntity sendation) throws Exception;

	public List<AlarmMesssageEntity> getBreakAlarmMessage(@Param("material_id") String material_id, @Param("position_id") String position_id);
	public AlarmMesssageEntity getBreakAlarmMessageByKey(@Param("alarm_messsage_id") String alarm_messsage_id);

	public List<AlarmMesssageEntity> getBreakAlarmMessages(@Param("material_id") String material_id, @Param("position_id") String position_id);
	public List<AlarmMesssageSendationEntity> getBreakAlarmMessageSendation(@Param("alarm_messsage_id") String alarm_messsage_id);
	public AlarmMesssageSendationEntity getBreakAlarmMessageBySendation(@Param("alarm_messsage_id") String alarm_messsage_id, @Param("sendation_id") String sendation_id);

	public PauseFeatureEntity getBreakOperatorMessage(@Param("operator_id") String operator_id, @Param("material_id") String material_id, @Param("position_id") String position_id);
	public PauseFeatureEntity getBreakOperatorMessageByID(@Param("alarm_messsage_id") String alarm_messsage_id);

	public int updateAlarmMessageSendation(AlarmMesssageSendationEntity sendation) throws Exception;
	public int countAlarmMessageSendation(AlarmMesssageSendationEntity sendation);

	public int countAlarmMessageOfSendation(String operator_id);
	public List<AlarmMesssageEntity> getAlarmMessageBySendation(String operator_id);
	public String getBreakLevelByMaterialId(@Param("material_id") String material_id, @Param("position_id") String position_id);

	public void updateLevel(AlarmMesssageEntity entity) throws Exception;

	public AlarmMesssageEntity getBreakPushedAlarmMessage(String material_id);
	
	public int countBreakUnPushedAlarmMessage(String material_id);

	public List<AlarmMesssageEntity> searchAlarmMessages(AlarmMesssageEntity entity);
	public List<AlarmMesssageEntity> searchAlarmMessagesFromSolo(AlarmMesssageEntity entity);

	/** 工位发生过未解决的点检故障  */
	public List<AlarmMesssageEntity> searchAlarmMessagesInfect(AlarmMesssageEntity entity);

	public boolean isFixed(String alarm_messsage_id);

	public List<AlarmMesssageEntity> searchAlarmMessagesByMaterialInline(
			@Param("material_id") String material_id, @Param("line_id") String line_id);

	public List<String> getToolInfectByPosition(@Param("section_id") String section_id,
			@Param("position_id") String position_id);

	public int getOccursByMaterialIdOfPosition(@Param("material_id") String material_id, @Param("position_id") String position_id);

	public String searchAlarmMessagesResolve(@Param("material_id") String material_id, @Param("position_id") String position_id);
}

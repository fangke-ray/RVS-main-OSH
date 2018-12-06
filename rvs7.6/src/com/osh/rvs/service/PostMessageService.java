package com.osh.rvs.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.mapper.data.PostMessageMapper;

public class PostMessageService {

	public static final Integer PCS_FIX_ORDER = 1;
	public static final Integer PCS_FIX_COMPLETE = 2;
	public static final Integer PARTIAL_RELATIVE_LOST = 4;
	public static final Integer ARRIVAL_PLAN_DATE_CHANGED = 7;
	public static final Integer INFECT_ERROR_CONFIRMED = 9;
	public static final Integer AGREE_DATE_CHANGE = 10;
	public static final Integer SET_PARTIAL_POSITION = 12;
	public static final Integer UNDO_ACCEPTANCE = 17;
	public static final Integer SORC_TO_OGZ = 18;
	public static final Integer SORC_TO_OSH = 19;
	public static final Integer INLINE_LATE = 20;
	public static final Integer CONSUMABLE_ORDER_CONFIRM = 42;
	public static final Integer CONSUMABLE_ORDER_DIRECT = 44;
	public static final Integer CONSUMABLE_APPLY_COMPLETE = 45;
	public static final Integer CONSUMABLE_APPLY_IMCOMPLETE = 46;
	public static final Integer SNOUT_LEAK_BY_MODEL = 50;
	public static final Integer SIKAKE_OVERTIME = 51;
	public static final Integer LINE_PLAN_OVERTIME = 52;
	public static final Integer COUNTDOWN_UNREACH = 60;

	/**
	 * 根据登录者显示全部推送信息
	 * @param conn
	 * @param operator_id 登录者
	 * @return
	 */
	public int getMessageCountsByOperator(SqlSession conn, String operator_id) {
		PostMessageMapper mapper = conn.getMapper(PostMessageMapper.class);
		return mapper.countPostMessageOfSendation(operator_id);
	}

	public List<PostMessageEntity> getMessageByOperator(SqlSession conn, String operator_id) {
		PostMessageMapper mapper = conn.getMapper(PostMessageMapper.class);
		return mapper.getPostMessageBySendation(operator_id);
	}

	public String getMessageGroupContent(String post_message_id, SqlSession conn) {
		String ret = "";
		PostMessageMapper mapper = conn.getMapper(PostMessageMapper.class);
		List<PostMessageEntity> gmessage = mapper.getPostMessageGroup(post_message_id);
		for (PostMessageEntity message : gmessage) {
			// TODO
			ret += message.getContent();
		}
		return ret;
	}

	/**
	 * 读取推送信息
	 * @param post_messsage_id
	 * @param operator_id
	 * @param conn
	 * @throws Exception 
	 */
	public void readPostMessage(String post_message_id, String operator_id,
			SqlSessionManager conn) throws Exception {
		PostMessageMapper mapper = conn.getMapper(PostMessageMapper.class);
		PostMessageEntity entity = new PostMessageEntity();
		entity.setRed_flg(1);
		entity.setReceiver_id(operator_id);
		entity.setPost_message_id(post_message_id);
		mapper.updatePostMessageSendation(entity);
	}

}

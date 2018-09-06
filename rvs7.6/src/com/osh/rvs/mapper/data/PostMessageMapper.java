package com.osh.rvs.mapper.data;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.PostMessageEntity;

public interface PostMessageMapper {

	// 建立推送信息
	public void createPostMessage(PostMessageEntity entity) throws Exception;

	// 建立推送信息接收人
	public void createPostMessageSendation(PostMessageEntity sendation) throws Exception;

	// 按主键得到推送信息
	public PostMessageEntity getPostMessageByKey(@Param("post_message_id") String key);

	// 更新推送信息 已读/回信状态
	public int updatePostMessageSendation(PostMessageEntity sendation) throws Exception;

	// 取得接受者视角的推送信息件数
	public int countPostMessageOfSendation(String operator_id);
	// 得到接受者视角的推送信息，最大6条信息
	public List<PostMessageEntity> getPostMessageBySendation(String operator_id);

	// 等级变化
	public void updateLevel(PostMessageEntity entity) throws Exception;

	// 查询推送信息
	public List<PostMessageEntity> searchPostMessages(PostMessageEntity entity);

	// 按主键得到推送信息
	public List<PostMessageEntity> getPostMessageGroup(@Param("post_message_id") String key);

	public void closePostMessage(@Param("entity") PostMessageEntity entity, @Param("reasons") List<Integer> reasons);
}

/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：消耗品在线一览Mapper<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.ConsumableOnlineEntity;

public interface ConsumableOnlineMapper {

	/** 消耗品在线一览查询 */
	public List<ConsumableOnlineEntity> searchOnlineList(ConsumableOnlineEntity entity);

	/** 消耗品在线一览清点 */
	public void updateOnlineList(ConsumableOnlineEntity consumableOnlineEntity);

	/** 获取消耗品在线库存 **/
	public ConsumableOnlineEntity getOnlineStorage(ConsumableOnlineEntity entity);
	
	public void insert(ConsumableOnlineEntity entity);
	
}

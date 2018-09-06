package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.ConsumableOrderEntity;

public interface ConsumableOrderMapper {

	/* 查询数据 */
	public List<ConsumableOrderEntity> searchOrderList(ConsumableOrderEntity entity);

	/* 查询明细数据 */
	public List<ConsumableOrderEntity> searchOrderDetail(ConsumableOrderEntity entity);

	public List<ConsumableOrderEntity> searchConsumableOrderDetailById(ConsumableOrderEntity entity);

	/* 查询所有的零件code和name */
	public List<ConsumableOrderEntity> getPartialByCode(String code);

	/* 修改订购单*/
	public void updateOrderDetail(ConsumableOrderEntity entity);

	public void updateOrder(ConsumableOrderEntity entity);

	/* 增加订购单*/
	public void insertOrderDetail(ConsumableOrderEntity entity);

	/* 删除订购单 */
	public void deleteOrder(ConsumableOrderEntity entity);

	/* 删除订购单 */
	public void deleteOrderDetail(ConsumableOrderEntity entity);

	/* 删除订购单 */
	public void deleteOrderDetailById(ConsumableOrderEntity entity);

	/* 消耗品修正数据_更新*/
	public void updateConsumableManage(ConsumableOrderEntity entity);
}

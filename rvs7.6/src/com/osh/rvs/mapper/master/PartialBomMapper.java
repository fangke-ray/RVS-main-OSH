package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.PartialBomEntity;

public interface PartialBomMapper {
	/* 删除 零件BOM表信息 */
	public void deleteBom();

	/* 导入BOM表 */
	public void insertBom(PartialBomEntity entity);

	/* 零件BOM信息一览 */
	public List<PartialBomEntity> searchPartialBom(PartialBomEntity entity);

	/*查询有效截止日期之前且零件ID修改过的零件*/
	public List<PartialBomEntity> searchNewPartial();

	/* 更新有效截止日期之前且零件ID修改过的零件的ID */
	public void updateBom(PartialBomEntity entity);

	/*查询有效截止日期之前且零件ID没有修改过的零件*/
	public List<PartialBomEntity> searchHistoryPartial();

	/*删除有效截止日期之前且零件ID没有修改过的零件的ID*/
	public void deleteHistoryPartialBom(PartialBomEntity entity);
}

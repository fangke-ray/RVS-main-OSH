package com.osh.rvs.mapper.partial;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.osh.rvs.bean.partial.PartialBaseLineValueEntity;

public interface PartialBaseLineValueMapping {
	/*检索*/
	public List<PartialBaseLineValueEntity> searchPartialBaseLineValue(PartialBaseLineValueEntity entity);
	
	public List<PartialBaseLineValueEntity> searchPartialBaseLineValueByfactor(PartialBaseLineValueEntity entity);
	
	public void updateOshForeboardCount(PartialBaseLineValueEntity entity);
	
	/*周期平均使用量非标准*/
	public BigDecimal getAverageQuantityOfNonStandardCycle(PartialBaseLineValueEntity entity);
	
	/*周期平均使用量合计*/
	public PartialBaseLineValueEntity getTotalQuantityOfCycle(PartialBaseLineValueEntity entity);
	
	/*设定非标使用量 */
	public Integer getNonBomSaftyCount(PartialBaseLineValueEntity entity);
	
	/*按照拉动台数计算标准使用量*/
	public List<PartialBaseLineValueEntity> searchForecastSettingAndCountOfSnandard(PartialBaseLineValueEntity entity);
	
	/*平均订购量*/
	public Double getOrderCountOfAverage(PartialBaseLineValueEntity entity);
	
	/*标准零件使用数*/
	public Integer getTotalForeboardCount(PartialBaseLineValueEntity entity);
	
	/*当月平均使用量非标准*/
	public BigDecimal getAverageQuantityOfNonStandardCycleOfCurMonth(PartialBaseLineValueEntity entity);
	
	/*当月平均使用量合计*/
	public PartialBaseLineValueEntity getTotalQuantityOfCycleOFCurMonth(PartialBaseLineValueEntity entity);
	
	/*拉动台数合计使用量*/
	public Integer getTotalOFForecastSetting(PartialBaseLineValueEntity entity);
	
	/*基准量当前设置*/
	public Integer getCurSetOFForeboardCount(PartialBaseLineValueEntity entity);
	
	/*更新整备信息零件基准值*/
	public void updateForeboardCount(PartialBaseLineValueEntity entity);
	
	/*更新零件整备设定历史有效区间终了日期*/
	public void updatePartialPrepairHistroyEndDate(PartialBaseLineValueEntity entity);
	
	/*插入零件整备设定历史*/
	public void insertPartialPrepairHistroy(PartialBaseLineValueEntity entity);
	
	/*查询当前零件整备设定最近一次有效区间的开始日期*/
	public Date searchLastStartDate(PartialBaseLineValueEntity entity);
	
	/*查询零件基准值 */
	public PartialBaseLineValueEntity searchPartialBaseValue(PartialBaseLineValueEntity entity);
	
	/*获取采样日期*/
	public Date getSampleDate(PartialBaseLineValueEntity entity);
	
	/*采样周期内 补充量合计*/
	public Integer searchSupplyQuantityOfCycle(PartialBaseLineValueEntity entity);
	
	/* 基准值下载*/
	public List<PartialBaseLineValueEntity> dowloadPartialBaseLineValue(PartialBaseLineValueEntity entity);
	
	/* 基准值下载*/
	public List<PartialBaseLineValueEntity> dowloadPartialBaseLineValueByfactor(PartialBaseLineValueEntity entity);

	/*获取采样日期*/
	public Date getNewestDateOfOsh(String partial_id);
	
	public int checkExist(PartialBaseLineValueEntity entity);
	
	public void insertPartialPrepair(PartialBaseLineValueEntity entity);

}

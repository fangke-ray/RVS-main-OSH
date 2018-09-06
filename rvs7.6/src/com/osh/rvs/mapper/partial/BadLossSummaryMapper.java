package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.BadLossSummaryEntity;

public interface BadLossSummaryMapper {
	
	//查询损金总计详细数据
	public List<BadLossSummaryEntity> searchLossSummaryFrom(@Param("work_period") String work_period);
	
	//查询损金总计详细数据--根据年--year
	public BadLossSummaryEntity searchLossSummaryFromOfYear(@Param("work_period") String work_period,@Param("month") String month);
	
	//根据财年月查询出某一月的详细数据
	public BadLossSummaryEntity searchLossSummaryOfMonth(BadLossSummaryEntity badLossSummaryEntity);
	
	//更新损金详细数据
	public void updateLossSummary(BadLossSummaryEntity badLossSummaryEntity);
	
	//更新财年月下的备注信息
	public void updateLossSummaryOfComment(BadLossSummaryEntity badLossSummaryEntity);
	
	//更新当前年月的汇率
	public void updateLossSummaryOfSettlement(BadLossSummaryEntity badLossSummaryEntity);
	
	//其他损金详细数据
	public List<BadLossSummaryEntity> searchLossSummaryOfBelongs(@Param("ocm_shipping_month")String ocm_shipping_month);
	
	//根据当前月份算出保修期内不良的损金金额
	public BadLossSummaryEntity searchLossSummaryOfRepair(@Param("ocm_shipping_month")String ocm_shipping_month);
	
	//损金总计-(除了保修期内不良)
	public BadLossSummaryEntity searchTotalSummary(@Param("ocm_shipping_month")String ocm_shipping_month);
	
	//当loss_summary中没有选择的月份数据---则按照财年+月插入一条全新的数据
	public void insertLossSummary(BadLossSummaryEntity badLossSummaryEntity);
	
	//获取所有SORC财年
	public List<String> getAllWorkPeriods();
}

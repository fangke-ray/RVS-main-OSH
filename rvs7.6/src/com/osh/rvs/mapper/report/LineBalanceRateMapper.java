package com.osh.rvs.mapper.report;

import java.util.List;

import com.osh.rvs.bean.report.LineBalanceRateEntity;

/**
 * 
 * @Title LineBalanceRateMapper.java
 * @Project rvs
 * @Package com.osh.rvs.mapper.report
 * @ClassName: LineBalanceRateMapper
 * @Description: 流水线平衡率分析
 * @author houp
 * @date 2016-10-9 下午1:56:08
 */
public interface LineBalanceRateMapper {

	public List<LineBalanceRateEntity> searchList(LineBalanceRateEntity entity);
}

package com.osh.rvs.mapper.report;

import java.util.List;

import com.osh.rvs.bean.report.RemainTimeReportEntity;

/**
 * 
 * @Title RemainTimeReportMapper.java
 * @Project rvs
 * @Package com.osh.rvs.mapper.report
 * @ClassName: RemainTimeReportMapper
 * @Description: 倒计时达成率
 * @author houp
 * @date 2017-02-22 下午4:56:08
 */
public interface RemainTimeReportMapper {

	public List<RemainTimeReportEntity> searchRateList(RemainTimeReportEntity entity);

	public List<RemainTimeReportEntity> searchPartialRateList(RemainTimeReportEntity entity);

	public List<RemainTimeReportEntity> searchDetailList(RemainTimeReportEntity entity);

	public List<RemainTimeReportEntity> searchPartialDetailList(RemainTimeReportEntity entity);
}

package com.osh.rvs.mapper.report;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.report.ProductivityEntity;

/**
 * 
 * @Title ProductivityMapper.java
 * @Project rvs
 * @Package com.osh.rvs.mapper.report
 * @ClassName: ProductivityMapper
 * @Description: 工时分析
 * @author houp
 * @date 2016-10-9 下午1:56:08
 */
public interface ProductivityMapper {

	public List<ProductivityEntity> searchList(ProductivityEntity entity);

	public int checkMonthData(ProductivityEntity entity);

	public void insertMonthData(ProductivityEntity entity) throws Exception;

	public void updateMonthData(ProductivityEntity entity) throws Exception;

	public int getworkdays(@Param("start_date") String start_date, @Param("end_date") String end_date );
}

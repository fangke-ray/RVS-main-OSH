package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.WastePartialRecycleCaseEntity;

/**
 * 废弃零件回收箱
 * 
 * @Description
 * @author dell
 * @date 2019-12-26 下午4:33:37
 */
public interface WastePartialRecycleCaseMapper {

	/**
	 * 查询
	 * 
	 * @param entity
	 * @return
	 */
	public List<WastePartialRecycleCaseEntity> search(WastePartialRecycleCaseEntity entity);

	/**
	 * 新建回收箱
	 * 
	 * @param entity
	 */
	public void insert(WastePartialRecycleCaseEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 */
	public void update(WastePartialRecycleCaseEntity entity);

	/**
	 * 更新重量
	 * 
	 * @param entity
	 */
	public void updateWeight(WastePartialRecycleCaseEntity entity);

	/**
	 * 根据装箱编号查询回收箱
	 * 
	 * @param case_code 装箱编号
	 * @return
	 */
	public WastePartialRecycleCaseEntity getCaseByCode(@Param("case_code") String case_code);

	/**
	 * 更新打包日期
	 * 
	 * @param case_id
	 */
	public void updatePackageDate(@Param("case_id") String case_id);
}

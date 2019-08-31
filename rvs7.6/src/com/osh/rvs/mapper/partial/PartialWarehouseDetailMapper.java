package com.osh.rvs.mapper.partial;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.PartialWarehouseDetailEntity;

/**
 * 零件入库明细
 * 
 * @author liuxb
 * 
 */
public interface PartialWarehouseDetailMapper {
	/**
	 * 新建零件入库明细
	 * 
	 * @param entity
	 */
	public void insert(PartialWarehouseDetailEntity entity);

	/**
	 * 根据零件入库单KEY查询零件入库明细
	 * 
	 * @param key
	 * @return
	 */
	public List<PartialWarehouseDetailEntity> searchByKey(@Param("key") String key);

	/**
	 * 根据零件入库单KEY查询零件入库分装明细
	 * 
	 * @param key
	 * @return
	 */
	public List<PartialWarehouseDetailEntity> searchUnpackByKey(@Param("key") String key);

	/**
	 * 根据KEY统计每种规格种别核对/上架总数
	 * 
	 * @param key
	 * @return
	 */
	public List<PartialWarehouseDetailEntity> countCollactionQuantityOfKind(@Param("key") String key);

	/**
	 * 根据KEY统计每种规格种别上架总数
	 * 
	 * @param key
	 * @return
	 */
	public List<PartialWarehouseDetailEntity> countUnpackQuantityOfKind(@Param("key") String key);
	
	/**
	 * 根据作业af_pf_key统计零件核对上架标准工时
	 * @param af_pf_key
	 * @return
	 */
	public BigDecimal countCollationOnShelfStandardTime(@Param("af_pf_key") String af_pf_key);
	
	/**
	 * 根据作业af_pf_key统计零件分装标准工时
	 * @param af_pf_key
	 * @return
	 */
	public BigDecimal countUnpackStandardTime(@Param("af_pf_key") String af_pf_key);
	
	/**
	 * 根据作业af_pf_key统计零件下架标准工时
	 * @param af_pf_key
	 * @return
	 */
	public BigDecimal countOffShelfStandardTime(@Param("af_pf_key") String af_pf_key);
	
}

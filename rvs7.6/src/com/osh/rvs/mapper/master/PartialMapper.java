package com.osh.rvs.mapper.master;

import java.util.List;
import java.util.Map;

import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.master.PartialPositionEntity;

public interface PartialMapper {

	public List<PartialEntity> searchPartial(PartialEntity conditionBean);
	
	public List<String> checkPartial(PartialEntity partial);
	
	public List<PartialEntity> getPartialByCode(String code);
	
	public int insertPartial(PartialEntity partial) throws Exception;
	
	public PartialEntity getPartialByID(String id);
	
	public int insertPartialPrice(PartialEntity partial) throws Exception;
	
	public void deletePartial(PartialEntity partial) throws Exception;
    
	/*双击修改页面内容*/
	public int updatePartial(PartialEntity partial) throws Exception;
	
	public int updatePartialPrice(PartialEntity partialprice) throws Exception;
	
	/*点击垃圾桶删除条数据*/
	public void deleteModel(PartialEntity partial) throws Exception;
	
	/*更新code和name  partial表*/
	public void insertPartialCodeName(PartialEntity partialEntity) throws Exception;
	
	/*更新零件的new_partial_id和history_limit_date partial_price表*/
	public void updatePartialHistorylimitdate(PartialEntity partialEntity) throws Exception;
	
	/*更新零件中的有效日期 partial_price表*/
	public void updatePriceAvaribleenddate(PartialEntity partialEntity) throws Exception;
	
	/*用页面提交的price和value_currency和avarible_end_date 新建一条partial_price记录*/
	public void insertPriceValuecurrencyAvaribleenddate(PartialEntity partialEntity) throws Exception;

	/** 继承更新更名对应零件 ID */
	public void updateNewPaterialIdExtends(PartialEntity partialEntity) throws Exception;
	
	/*查询所有的零件code和name*/
	/** search partial
	 * @param code */
	public List<Map<String, String>> getAllPartial(String code); 
	
	/*查询出所有的型号*/
	public List<ModelEntity> getModelOfPartialAvaliable(PartialEntity partialEntity);
	
	/*根据零件型号查出型号工位有效期*/
	public List<PartialPositionEntity> searchPartialModelNameProcessCodeActiveData(PartialPositionEntity partialPositionEntity);
	
	public void  updatePartialPosition(Map<String,Object> param);
	
	public void insertPartialWasteModifyHistory(PartialPositionEntity partialPositionEntity);
	
	public void insertPartialPosition(PartialPositionEntity partialPositionEntity);
	
	public List<PartialEntity>searchAllPartial();
	
	/****
	 * 更新零件价格
	 * @param partialEntity
	 */
	public void updatePrice(PartialEntity partialEntity);
	
}

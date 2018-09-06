package com.osh.rvs.mapper.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.master.PositionEntity;

public interface ModelMapper {

	/** insert single */
	public int insertModel(ModelEntity model) throws Exception;

	public int updateModel(ModelEntity model) throws Exception;

	/** search all */
	public List<ModelEntity> getAllModel(); 

	public ModelEntity getModelByID(String id);

	/** search*/
	public List<ModelEntity> searchModel(ModelEntity model);

	public void deleteModel(ModelEntity model) throws Exception;

	String[] getFeature1AutoCompletes();
	String[] getFeature2AutoCompletes();
	String[] getFeature3AutoCompletes();

	public String getCategoryNameByModelName(String model_name);

	public String getModelByName(String model_name); 
	public String getModelByItemCode(String item_code);

	public List<String> checkModelByName(ModelEntity model);
	
	public List<PositionEntity> getPositionsOfModel(String model_id);
	
	public List<ModelEntity> searchAbolishOfModelLevel(ModelEntity entity);
	
	public void updateAvaliablEndDate(ModelEntity entity);
	
	public void updateOperator(ModelEntity entity);
	
	/**追加**/
	public void insertModelLevel(ModelEntity entity);
	
	public void insertModelLevelSetHistory(ModelEntity entity);
	
	public ModelEntity searchExitsModelLevelSetHistory(ModelEntity entity);

	public String checkModelDepacy(@Param("model_id")String model_id, @Param("level")String level);
	
	String[] getElBaseTypeAutoCompletes();
	String[] getSConnectorBaseTypeAutoCompletes();
	String[] getOperatePartTypeAutoCompletes();
	String[] getOcularTypeAutoCompletes();
	
	//查询等级型号设定
	public ModelEntity getModeLevelSet(ModelEntity entity);
	
	//新建等级型号设定
	public void insertModeLevelSet(ModelEntity entity);
	
	//更新等级型号设定
	public void updateModeLevelSet(ModelEntity entity);

	public List<ModelEntity> getModelByKind7();

	public List<String> getModelImbalanceLine(@Param("model_id")String model_id);
	public List<String> getLineImbalanceModel(@Param("line_id")String line_id);
	public void deleteModelImbalanceLine(ModelEntity entity);
	public void insertModelImbalanceLine(ModelEntity entity);
}

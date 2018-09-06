package com.osh.rvs.mapper.master;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.ProcessAssignEntity;
import com.osh.rvs.bean.master.ProcessAssignTemplateEntity;

public interface ProcessAssignMapper {

	/** insert single */
	public int insertProcessAssignTemplate(ProcessAssignTemplateEntity processAssignTemplate) throws Exception;

	public int updateProcessAssignTemplate(ProcessAssignTemplateEntity processAssignTemplate) throws Exception;

	/** search all */
	public List<ProcessAssignTemplateEntity> getAllProcessAssignTemplate();

	public ProcessAssignTemplateEntity getProcessAssignTemplateByID(String processAssignTemplate_id);

	/** search */
	public List<ProcessAssignTemplateEntity> searchProcessAssignTemplate(ProcessAssignTemplateEntity processAssignTemplate);

	public void deleteProcessAssignTemplate(ProcessAssignTemplateEntity updateBean) throws Exception;

	public List<ProcessAssignEntity> getProcessAssignByTemplateID(String processAssignTemplate_id);

	public int insertProcessAssign(ProcessAssignEntity processAssignTemplate) throws Exception;
	public void deleteProcessAssignByTemplateID(String processAssignTemplate_id) throws Exception;
	public List<Map<String, String>> getInlinePositions();
	public List<Map<String, String>> getPositionsOfLine(String line_id);
	public List<Map<String, String>> getPositionsOfLineByPat(@Param("process_assign_template_id") String template_id, @Param("line_id") String line_id);

	public ProcessAssignEntity getProcessAssign(@Param("process_assign_template_id") String template_id, @Param("position_id") String trigger_position_id);

	/** 得到触发开始的PositionIds */
	public List<PositionEntity> getNextPositions(@Param("process_assign_template_id") String template_id, @Param("position_id") String trigger_position_id);

	/** 得到工位前提的PositionIds */
	public List<PositionEntity> getPrevPositions(@Param("process_assign_template_id") String template_id, @Param("position_id") String trigger_position_id);

	/** 得到本PositionId是否全完成 */
	public boolean checkWorked(@Param("material_id") String material_id, @Param("position_id") String doing_position_id , @Param("process_assign_template_id") String process_assign_template_id);
	public int checkWorking(@Param("material_id") String material_id, @Param("position_id") String doing_position_id , @Param("process_assign_template_id") String process_assign_template_id);

	/** 所在线的所有关联工位是否都有完成记录 */
	public boolean getFinishedByLine(@Param("material_id") String material_id, @Param("line_id") String line_id);

	/** 得到一个分支中最初的工位 **/ 
	public List<String> getPartStart(@Param("refer_id") String pat_id, @Param("line_id")  String line_id);

	/** 得到一个分支中全部的工位 **/ 
	public List<String> getPartAll(@Param("refer_id") String pat_id, @Param("line_id")  String line_id);

	public int get32(String material_id);

	public int get20(String material_id);

	/** 得到工程中已经完成的工作信息 **/ 
	public List<ProductionFeatureEntity> getFinishedPositionsByLine(@Param("material_id") String material_id, @Param("line_id") String line_id);

	/** 指定复数工位上是否都有完成记录 */
	public int getFinishedCountByPositions(Map<String, Object> params);

	public List<ProductionFeatureEntity> getFinishedPositionsInline(String material_id);

	public List<String> getNonfinishedPositions(@Param("material_id") String material_id);

	/** 取得流程的首工位 */
	public List<String> getFirstPosition(@Param("template_id") String template_id, @Param("line_id") String line_id);

	public boolean checkHasLine(@Param("process_assign_template_id") String pat_id, @Param("line_id") String line_id);

	public Map<String, Object> getDerivePair(@Param("process_assign_template_id") String pat_id,
			@Param("derive_kind") String derive_kind);
}

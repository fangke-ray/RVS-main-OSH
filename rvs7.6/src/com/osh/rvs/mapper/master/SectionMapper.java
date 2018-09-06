package com.osh.rvs.mapper.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.master.SectionEntity;

public interface SectionMapper {

	/** insert single */
	public int insertSection(SectionEntity section) throws Exception;

	public int updateSection(SectionEntity section) throws Exception;

	/** search all */
	public List<SectionEntity> getAllSection();

	/** 得到所有生产课室 */
	public List<SectionEntity> getInlineSection();

	public SectionEntity getSectionByID(String section_id);

	/** search */
	public List<SectionEntity> searchSection(SectionEntity section);

	public void deleteSection(SectionEntity updateBean) throws Exception;

	/** 得到某课室的全部工位 */
	public List<String> getPositionsOfSection(String section_id);

	/** 得到某工位的全部课室 */
	public List<String> getSectionsOfPosition(String position_id);

	/** 插入某课室的工位 */
	public void insertPositionOfSection(@Param("section_id") String section_id, @Param("position_id") String position_id);

	/** 删除某课室的工位 */
	public void deletePositionOfSection(@Param("section_id") String section_id);

	public List<SectionEntity> getSectionByOperator(String operator_id);

}

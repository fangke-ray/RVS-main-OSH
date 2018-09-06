package com.osh.rvs.mapper.inline;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.inline.ForSolutionAreaEntity;
import com.osh.rvs.bean.inline.ForSolutionAreaEventEntity;

/**
 * 待解决等待区域
 * 
 * @author gonglm
 * 
 */
public interface ForSolutionAreaMapper {

	public ForSolutionAreaEntity getByKey(String for_solution_area_key);
	
	public List<ForSolutionAreaEntity> search(ForSolutionAreaEntity entity);

	public void solve(ForSolutionAreaEntity entity) throws Exception;

	public void updateToPushed(ForSolutionAreaEntity entity) throws Exception;

	public void updateToAppend(ForSolutionAreaEntity entity) throws Exception;

	public String findByAlarmMesssage(String message_id);

	public List<ForSolutionAreaEntity> checkOffline(@Param("material_id") String material_id, @Param("position_id") String position_id, @Param("line_id") String line_id);

	public void create(ForSolutionAreaEntity entity) throws Exception;

	public List<ForSolutionAreaEntity> getOfflineOfMaterial(String material_id);

	public void remove(ForSolutionAreaEntity entity) throws Exception;

	public void solveAsStop(ForSolutionAreaEntity entity) throws Exception;

	public void createEvent(ForSolutionAreaEventEntity event) throws Exception;

	public List<String> getLeadersByObject(@Param("material_id") String material_id, @Param("position_id") String position_id);

	public List<String> getLeadersByKey(String for_solution_area_key);

	public List<String> getInlineManagers();

	public List<String> getSolvedPositionByLine(ForSolutionAreaEntity entity);

	public void finishSolve(ForSolutionAreaEntity entity) throws Exception;
}

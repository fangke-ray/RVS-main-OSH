package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.LineEntity;

public interface LineMapper {

	/** insert single */
	public int insertLine(LineEntity line) throws Exception;

	public int updateLine(LineEntity line) throws Exception;

	/** search all */
	public List<LineEntity> getAllLine();

	public LineEntity getLineByID(String line_id);

	/** search */
	public List<LineEntity> searchLine(LineEntity line);

	public void deleteLine(LineEntity updateBean) throws Exception;

}

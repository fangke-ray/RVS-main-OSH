package com.osh.rvs.service.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.ProcessAssignEntity;
import com.osh.rvs.mapper.inline.MaterialProcessAssignMapper;
import com.osh.rvs.mapper.master.ProcessAssignMapper;

public class ProcessAssignProxy {
	private ProcessAssignMapper heavyMapper = null;
	private MaterialProcessAssignMapper lightMapper = null;
	private String material_id;
	private String pat_id;
	private String material_section_id;
	public boolean isLightFix;

	public ProcessAssignProxy(String material_id, String pat_id, String section_id,
			boolean isLightFix, SqlSessionManager conn) {
		this.material_id = material_id;
		this.pat_id = pat_id;
		this.isLightFix = isLightFix;
		this.material_section_id = section_id;

		if (isLightFix) {
			lightMapper = conn.getMapper(MaterialProcessAssignMapper.class);
		}
		heavyMapper = conn.getMapper(ProcessAssignMapper.class);
	}

	public int checkWorking(String position_id) {
		if (isLightFix) {
			return heavyMapper.checkWorking(material_id, position_id, null);
		} else {
			return heavyMapper.checkWorking(material_id, position_id, pat_id);
		}
	}

	public boolean checkWorked(String position_id) {
		if (isLightFix) {
			return lightMapper.checkWorked(material_id, position_id);
		} else {
			return heavyMapper.checkWorked(material_id, position_id, pat_id);
		}
	}

	public int getFinishedCountByPositions(List<String> prevPositions) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("material_id", material_id);
		params.put("position_ids", prevPositions);

		return heavyMapper.getFinishedCountByPositions(params);
	}

	public List<PositionEntity> getPrevPositions(String position_id) {
		if (isLightFix) {
			return lightMapper.getPrevPositions(material_id, position_id);
		} else {
			return heavyMapper.getPrevPositions(pat_id, position_id);
		}
	}

	public List<PositionEntity> getNextPositions(String position_id) {
		if (isLightFix) {
			return lightMapper.getNextPositions(material_id, position_id);
		} else {
			return heavyMapper.getNextPositions(pat_id, position_id);
		}
	}

	public ProcessAssignEntity getProcessAssign(String position_id) {
		if (isLightFix) {
			return lightMapper.getProcessAssign(material_id, position_id);
		} else {
			return heavyMapper.getProcessAssign(pat_id, position_id);
		}
	}

	public boolean getFinishedByLine(String line_id) {
		return heavyMapper.getFinishedByLine(material_id, line_id);
	}

	public List<String> getPartStart(String line_id) {
		if (isLightFix) {
			return lightMapper.getPartStart(material_id, line_id);
		} else {
			return heavyMapper.getPartStart(pat_id, line_id);
		}
	}

	public List<String> getPartAll(String position_id) {
		if (isLightFix) {
			return lightMapper.getPartAll(material_id, position_id);
		} else {
			return heavyMapper.getPartAll(pat_id, position_id);
		}
	}

	public String toString() {
		return "isLightFix: " + isLightFix + " material_id: " + material_id + " pat_id: " + pat_id;
	}

	public String getMaterial_section_id() {
		return material_section_id;
	}
}

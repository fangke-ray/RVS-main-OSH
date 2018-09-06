package com.osh.rvs.mapper.inline;

import java.util.List;
import java.util.Map;

/**
 * 工时记录异动
 * 
 * @author gonglm
 * 
 */
public interface AbnormalWorkStateMapper {

	public List<Map<String, Object>> getAbnormalWorkStateByOperator(String operator_id);

}

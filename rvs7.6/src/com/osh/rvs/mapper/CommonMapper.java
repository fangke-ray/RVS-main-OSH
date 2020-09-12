package com.osh.rvs.mapper;

import java.util.Date;


public interface CommonMapper {

	/** 取得本连接最后取得的自增ID */
	public String getLastInsertID();
	
	/** 取得下一个工作日 **/
	public Date getNextWorkday();

}

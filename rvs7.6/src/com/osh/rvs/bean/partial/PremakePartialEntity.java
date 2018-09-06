package com.osh.rvs.bean.partial;

import java.io.Serializable;

/**
 * 
 * @Title PremakePartialEntity.java
 * @Project rvs
 * @Package com.osh.rvs.bean.partial
 * @ClassName: PremakePartialEntity
 * @Description: 零件预制
 * @author lxb
 * @date 2016-3-24 下午3:42:20
 */
public class PremakePartialEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4148679560337601453L;

	private String partial_id;// 零件

	private String model_id;// 型号

	private Integer standard_flg;// 标配零件

	private String code;

	private String model_name;

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public Integer getStandard_flg() {
		return standard_flg;
	}

	public void setStandard_flg(Integer standard_flg) {
		this.standard_flg = standard_flg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

}

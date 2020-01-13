package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 废弃零件回收箱
 * 
 * @Description
 * @author dell
 * @date 2019-12-26 下午3:54:35
 */
public class WastePartialRecycleCaseEntity implements Serializable {
	private static final long serialVersionUID = 7922985961683310994L;

	/**
	 * 回收箱 ID
	 */
	private String case_id;

	/**
	 * 装箱编号
	 */
	private String case_code;

	/**
	 * 回收箱用途种类
	 */
	private Integer collect_kind;

	/**
	 * 备注说明
	 */
	private String comment;

	/**
	 * 打包日期
	 */
	private Date package_date;

	/**
	 * 重量
	 */
	private BigDecimal weight;

	/**
	 * 废弃申请日期
	 */
	private Date waste_apply_date;

	/**
	 * 废弃标记
	 */
	private Integer waste_flg;

	/**
	 * 打包标记
	 */
	private Integer package_flg;

	/**
	 * 打包开始日期
	 */
	private Date package_date_start;

	/**
	 * 打包结束日期
	 */
	private Date package_date_end;

	/**
	 * 废弃申请日期开始
	 */
	private Date waste_apply_date_start;

	/**
	 * 废弃申请日期结束
	 */
	private Date waste_apply_date_end;

	public String getCase_id() {
		return case_id;
	}

	public void setCase_id(String case_id) {
		this.case_id = case_id;
	}

	public String getCase_code() {
		return case_code;
	}

	public void setCase_code(String case_code) {
		this.case_code = case_code;
	}

	public Integer getCollect_kind() {
		return collect_kind;
	}

	public void setCollect_kind(Integer collect_kind) {
		this.collect_kind = collect_kind;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getPackage_date() {
		return package_date;
	}

	public void setPackage_date(Date package_date) {
		this.package_date = package_date;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public Date getWaste_apply_date() {
		return waste_apply_date;
	}

	public void setWaste_apply_date(Date waste_apply_date) {
		this.waste_apply_date = waste_apply_date;
	}

	public Integer getWaste_flg() {
		return waste_flg;
	}

	public void setWaste_flg(Integer waste_flg) {
		this.waste_flg = waste_flg;
	}

	public Integer getPackage_flg() {
		return package_flg;
	}

	public void setPackage_flg(Integer package_flg) {
		this.package_flg = package_flg;
	}

	public Date getPackage_date_start() {
		return package_date_start;
	}

	public void setPackage_date_start(Date package_date_start) {
		this.package_date_start = package_date_start;
	}

	public Date getPackage_date_end() {
		return package_date_end;
	}

	public void setPackage_date_end(Date package_date_end) {
		this.package_date_end = package_date_end;
	}

	public Date getWaste_apply_date_start() {
		return waste_apply_date_start;
	}

	public void setWaste_apply_date_start(Date waste_apply_date_start) {
		this.waste_apply_date_start = waste_apply_date_start;
	}

	public Date getWaste_apply_date_end() {
		return waste_apply_date_end;
	}

	public void setWaste_apply_date_end(Date waste_apply_date_end) {
		this.waste_apply_date_end = waste_apply_date_end;
	}

}

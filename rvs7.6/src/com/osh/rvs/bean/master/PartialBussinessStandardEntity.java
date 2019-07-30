package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 零件出入库工时标准
 *
 * @author liuxb
 *
 */
public class PartialBussinessStandardEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2661180533296310882L;
	/**
	 * 规格种别
	 */
	private Integer spec_kind;

	/**
	 * 装箱数量
	 */
	private Integer box_count;

	/**
	 * 收货
	 */
	private BigDecimal recept;

	/**
	 * 拆盒
	 */
	private BigDecimal  collect_case;

	/**
	 * 核对上架
	 */
	private BigDecimal collation_on_shelf;

	/**
	 * 下架
	 */
	private BigDecimal off_shelf;

	/**
	 * 分装
	 */
	private BigDecimal unpack;

	/**
	 * 每批一箱标记
	 */
	private Integer box_count_flg;

	public Integer getSpec_kind() {
		return spec_kind;
	}

	public void setSpec_kind(Integer spec_kind) {
		this.spec_kind = spec_kind;
	}

	public Integer getBox_count() {
		return box_count;
	}

	public void setBox_count(Integer box_count) {
		this.box_count = box_count;
	}

	public BigDecimal getRecept() {
		return recept;
	}

	public void setRecept(BigDecimal recept) {
		this.recept = recept;
	}

	public BigDecimal getCollect_case() {
		return collect_case;
	}

	public void setCollect_case(BigDecimal collect_case) {
		this.collect_case = collect_case;
	}

	public BigDecimal getCollation_on_shelf() {
		return collation_on_shelf;
	}

	public void setCollation_on_shelf(BigDecimal collation_on_shelf) {
		this.collation_on_shelf = collation_on_shelf;
	}

	public BigDecimal getOff_shelf() {
		return off_shelf;
	}

	public void setOff_shelf(BigDecimal off_shelf) {
		this.off_shelf = off_shelf;
	}

	public BigDecimal getUnpack() {
		return unpack;
	}

	public void setUnpack(BigDecimal unpack) {
		this.unpack = unpack;
	}

	public Integer getBox_count_flg() {
		return box_count_flg;
	}

	public void setBox_count_flg(Integer box_count_flg) {
		this.box_count_flg = box_count_flg;
	}
}

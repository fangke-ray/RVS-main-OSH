package com.osh.rvs.bean.qf;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 物料加工
 * @author liuxb
 * @date 2018-5-14 下午1:03:27
 */
public class SteelWireContainerWashProcessEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -628466444815369632L;

	/** 清洗品 ID **/
	private String partial_id;

	/** 物料加工作业类别 **/
	private Integer process_type;

	/** 入库批号 **/
	private String lot_no;

	/** 处理时间 **/
	private Date process_time;

	/** 作业数量 **/
	private Integer quantity;

	/** 责任人 ID **/
	private String operator_id;

	/** 分配维修对象 ID **/
	private String material_id;

	/** 零件编码 **/
	private String code;

	/** 责任人 名称 **/
	private String operator_name;

	/** 处理开始时间 **/
	private Date process_time_start;

	/** 处理结束时间 **/
	private Date process_time_end;

	/** 修理单号 **/
	private String sorc_no;

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getLot_no() {
		return lot_no;
	}

	public void setLot_no(String lot_no) {
		this.lot_no = lot_no;
	}

	public Date getProcess_time() {
		return process_time;
	}

	public void setProcess_time(Date process_time) {
		this.process_time = process_time;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public Date getProcess_time_start() {
		return process_time_start;
	}

	public void setProcess_time_start(Date process_time_start) {
		this.process_time_start = process_time_start;
	}

	public Date getProcess_time_end() {
		return process_time_end;
	}

	public void setProcess_time_end(Date process_time_end) {
		this.process_time_end = process_time_end;
	}

	public Integer getProcess_type() {
		return process_type;
	}

	public void setProcess_type(Integer process_type) {
		this.process_type = process_type;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

}

package com.osh.rvs.form.qf;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * @Description: 钢丝固定件清洗记录
 * @author liuxb
 * @date 2018-5-14 下午1:06:51
 */
public class SteelWireContainerWashProcessForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1932557731699967875L;

	/** 清洗品 ID **/
	@BeanField(title = "清洗品 ID", name = "partial_id", type = FieldType.String, length = 11, notNull = true)
	private String partial_id;

	/** 物料加工作业类别 **/
	@BeanField(title = "物料加工作业类别", name = "process_type", type = FieldType.Integer, length = 1, notNull = true)
	private String process_type;

	/** 入库批号 **/
	@BeanField(title = "入库批号", name = "lot_no", type = FieldType.String, length = 15)
	private String lot_no;

	/** 处理时间 **/
	@BeanField(title = "处理时间", name = "process_time", type = FieldType.DateTime, notNull = true)
	private String process_time;

	/** 作业数量 **/
	@BeanField(title = "作业数量", name = "quantity", type = FieldType.Integer, length = 3, notNull = true)
	private String quantity;

	/** 责任人 ID **/
	@BeanField(title = "责任人 ID", name = "operator_id", type = FieldType.String, length = 11, notNull = true)
	private String operator_id;

	/** 分配维修对象 ID **/
	@BeanField(title = "分配维修对象 ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;

	/** 零件编码 **/
	@BeanField(title = "零件编码", name = "code", type = FieldType.String, length = 9)
	private String code;

	/** 责任人 名称 **/
	@BeanField(title = "责任人 名称", name = "operator_name", type = FieldType.String, length = 8)
	private String operator_name;

	/** 处理开始时间 **/
	@BeanField(title = "处理开始时间", name = "process_time_start", type = FieldType.Date)
	private String process_time_start;

	/** 处理结束时间 **/
	@BeanField(title = "处理结束时间", name = "process_time_end", type = FieldType.Date)
	private String process_time_end;

	/** 修理单号 **/
	@BeanField(title = "修理单号", name = "sorc_no", type = FieldType.String)
	private String sorc_no;

	/** 分线 **/
	@BeanField(title = "分线", name = "px", type = FieldType.Integer, length = 1)
	private String px;

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

	public String getProcess_time() {
		return process_time;
	}

	public void setProcess_time(String process_time) {
		this.process_time = process_time;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
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

	public String getProcess_time_start() {
		return process_time_start;
	}

	public void setProcess_time_start(String process_time_start) {
		this.process_time_start = process_time_start;
	}

	public String getProcess_time_end() {
		return process_time_end;
	}

	public void setProcess_time_end(String process_time_end) {
		this.process_time_end = process_time_end;
	}

	public String getProcess_type() {
		return process_type;
	}

	public void setProcess_type(String process_type) {
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

	public String getPx() {
		return px;
	}

	public void setPx(String px) {
		this.px = px;
	}

}

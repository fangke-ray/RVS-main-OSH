package com.osh.rvs.form.partial;

import java.io.Serializable;

import com.osh.rvs.form.UploadForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PartialWarehouseForm extends UploadForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8231985194725137030L;

	@BeanField(title = "KEY", name = "key", type = FieldType.String, length = 11, notNull = true)
	private String key;

	@BeanField(title = "日期", name = "warehouse_date", type = FieldType.Date, notNull = true)
	private String warehouse_date;

	@BeanField(title = "DN 编号", name = "dn_no", type = FieldType.String, length = 8, notNull = true)
	private String dn_no;

	@BeanField(title = "入库进展", name = "step", type = FieldType.Integer, length = 1, notNull = true)
	private String step;

	@BeanField(title = "数量", name = "quantity", type = FieldType.Integer)
	private String quantity;

	@BeanField(title = "入库单日期开始", name = "warehouse_date_start", type = FieldType.Date)
	private String warehouse_date_start;

	@BeanField(title = "入库单日期结束", name = "warehouse_date_end", type = FieldType.Date)
	private String warehouse_date_end;

	@BeanField(title = "完成上架日期开始", name = "finish_date_start", type = FieldType.Date)
	private String finish_date_start;

	@BeanField(title = "完成上架日期结束", name = "finish_date_end", type = FieldType.Date)
	private String finish_date_end;

	@BeanField(title = "核对数量", name = "collation_quantity", type = FieldType.Integer)
	private String collation_quantity;

	private String production_type;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getWarehouse_date() {
		return warehouse_date;
	}

	public void setWarehouse_date(String warehouse_date) {
		this.warehouse_date = warehouse_date;
	}

	public String getDn_no() {
		return dn_no;
	}

	public void setDn_no(String dn_no) {
		this.dn_no = dn_no;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getWarehouse_date_start() {
		return warehouse_date_start;
	}

	public void setWarehouse_date_start(String warehouse_date_start) {
		this.warehouse_date_start = warehouse_date_start;
	}

	public String getWarehouse_date_end() {
		return warehouse_date_end;
	}

	public void setWarehouse_date_end(String warehouse_date_end) {
		this.warehouse_date_end = warehouse_date_end;
	}

	public String getCollation_quantity() {
		return collation_quantity;
	}

	public void setCollation_quantity(String collation_quantity) {
		this.collation_quantity = collation_quantity;
	}

	public String getProduction_type() {
		return production_type;
	}

	public void setProduction_type(String production_type) {
		this.production_type = production_type;
	}

	public String getFinish_date_start() {
		return finish_date_start;
	}

	public void setFinish_date_start(String finish_date_start) {
		this.finish_date_start = finish_date_start;
	}

	public String getFinish_date_end() {
		return finish_date_end;
	}

	public void setFinish_date_end(String finish_date_end) {
		this.finish_date_end = finish_date_end;
	}

}

package com.osh.rvs.form.master;

import java.io.Serializable;

import com.osh.rvs.form.UploadForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Description 常用采购清单
 * @author liuxb
 * @date 2021-11-30 上午9:28:26
 */
public class SuppliesReferListForm extends UploadForm implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -37426070310666438L;

	@BeanField(name = "refer_key", title = "常用采购清单KEY", type = FieldType.String, length = 11, primaryKey = true)
	private String refer_key;

	@BeanField(name = "product_name", title = "品名", type = FieldType.String, length = 64, notNull = true)
	private String product_name;

	@BeanField(name = "model_name", title = "规格", type = FieldType.String, length = 32)
	private String model_name;

	@BeanField(name = "unit_price", title = "预定单价", type = FieldType.UDouble, length = 6, scale = 2)
	private String unit_price;

	@BeanField(name = "unit_text", title = "单位", type = FieldType.String, length = 3)
	private String unit_text;

	@BeanField(name = "supplier", title = "供应商", type = FieldType.String, length = 64)
	private String supplier;

	@BeanField(name = "photo_uuid", title = "照片 UUID", type = FieldType.String, length = 32)
	private String photo_uuid;

//	ALTER TABLE `supplies_refer_list` 
//	CHANGE COLUMN `refer_key` `refer_key` INT(11) UNSIGNED ZEROFILL NOT NULL COMMENT 'KEY' ,
//	ADD COLUMN `capacity` INT(3) NOT NULL COMMENT '包装数量' DEFAULT 1 AFTER `model_name`,
//	ADD COLUMN `package_unit_text` VARCHAR(5) NULL COMMENT '采购单位' AFTER `unit_text`,
//	ADD COLUMN `goods_serial` VARCHAR(45) NULL COMMENT '商品编号' AFTER `package_unit_text`;

	@BeanField(name = "capacity", title = "包装数量", type = FieldType.UInteger, length = 3)
	private String capacity;

	@BeanField(name = "package_unit_text", title = "采购单位", type = FieldType.String, length = 5)
	private String package_unit_text;

	@BeanField(name = "goods_serial", title = "商品编号", type = FieldType.String, length = 45)
	private String goods_serial;

	public String getRefer_key() {
		return refer_key;
	}

	public void setRefer_key(String refer_key) {
		this.refer_key = refer_key;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(String unit_price) {
		this.unit_price = unit_price;
	}

	public String getUnit_text() {
		return unit_text;
	}

	public void setUnit_text(String unit_text) {
		this.unit_text = unit_text;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getPhoto_uuid() {
		return photo_uuid;
	}

	public void setPhoto_uuid(String photo_uuid) {
		this.photo_uuid = photo_uuid;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public String getPackage_unit_text() {
		return package_unit_text;
	}

	public void setPackage_unit_text(String package_unit_text) {
		this.package_unit_text = package_unit_text;
	}

	public String getGoods_serial() {
		return goods_serial;
	}

	public void setGoods_serial(String goods_serial) {
		this.goods_serial = goods_serial;
	}
}
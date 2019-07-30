package com.osh.rvs.form.master;

import com.osh.rvs.form.UploadForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PartialForm extends UploadForm{
	
    /**
	 * 零件管理
	 */
	
	private static final long serialVersionUID = 8931917335686576264L;
	
	
	/*订购对象标记*/	
	@BeanField(title="订购对象标记",name="order_flg",type=FieldType.Integer,notNull=true,length=1)
	private String order_flg;
	
	/*消耗品标记*/	
	@BeanField(title="消耗品标记",name="consumable_flg",type=FieldType.Integer,length=1)
	private String consumable_flg;
	
	/*最新价格*/	
	@BeanField(title="最新价格",name="new_price",type=FieldType.Double)
	private String new_price;
	
	/*有效截至日期*/
	@BeanField(title="有效截止日期",name="avarible_end_date",type=FieldType.Date)
	private String avarible_end_date;
	/*通货*/
	@BeanField(title="通货",name="value_currency",type=FieldType.Integer)
	private String value_currency;
	/*价格*/	
	@BeanField(title="价格",name="price",type=FieldType.UDouble)
	private String price;
	/*零件 ID*/
	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.String, primaryKey = true, length = 11)
	private String partial_id;
    /*零件编码*/
	@BeanField(title = "零件编码", name = "code", length = 8,notNull = true)
	private String code;
	/*零件名称*/
	@BeanField(title = "零件名称", name = "name", length = 120)
	private String name;
	/*有效截止日期*/
	@BeanField(title = "有效截止日期", name = "is_exists",type=FieldType.Integer)
	private String is_exists;
	/*更名对应零件 ID*/
	@BeanField(title = "更名对应零件 ID", name = "new_partial_id", length = 11)
	private String new_partial_id;
	/*最后更新人*/
	@BeanField(title = "最后更新人", name = "updated_by")
	private String updated_by;
	/*最后更新时间*/
	@BeanField(title = "最后更新时间", name = "updated_time",type=FieldType.TimeStamp)
	private String updated_time;
	
	/**
	 * 规格种别
	 */
	@BeanField(title = "规格种别", name = "spec_kind", type = FieldType.Integer, length = 1, notNull = true)
	private String spec_kind;
	
	/**
	 * 是否分装
	 */
	private String unpack_flg;

	/**
	 * 分装数量
	 */
	private String split_quantity;
	
	public String getOrder_flg() {
		return order_flg;
	}
	public void setOrder_flg(String order_flg) {
		this.order_flg = order_flg;
	}
	public String getConsumable_flg() {
		return consumable_flg;
	}
	public void setConsumable_flg(String consumable_flg) {
		this.consumable_flg = consumable_flg;
	}
	public String getIs_exists() {
		return is_exists;
	}
	public void setIs_exists(String is_exists) {
		this.is_exists = is_exists;
	}
	public String getNew_price() {
		return new_price;
	}
	public void setNew_price(String new_price) {
		this.new_price = new_price;
	}
	public String getAvarible_end_date() {
		return avarible_end_date;
	}
	public void setAvarible_end_date(String avarible_end_date) {
		this.avarible_end_date = avarible_end_date;
	}
	public String getPartial_id() {
		return partial_id;
	}
	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNew_partial_id() {
		return new_partial_id;
	}
	public void setNew_partial_id(String new_partial_id) {
		this.new_partial_id = new_partial_id;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getUpdated_time() {
		return updated_time;
	}
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}
	public String getValue_currency() {
		return value_currency;
	}
	public void setValue_currency(String value_currency) {
		this.value_currency = value_currency;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSpec_kind() {
		return spec_kind;
	}
	public void setSpec_kind(String spec_kind) {
		this.spec_kind = spec_kind;
	}
	public String getUnpack_flg() {
		return unpack_flg;
	}
	public void setUnpack_flg(String unpack_flg) {
		this.unpack_flg = unpack_flg;
	}
	public String getSplit_quantity() {
		return split_quantity;
	}
	public void setSplit_quantity(String split_quantity) {
		this.split_quantity = split_quantity;
	}
	
}

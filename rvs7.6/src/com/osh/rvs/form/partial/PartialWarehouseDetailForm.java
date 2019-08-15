package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;

/**
 * 零件入库明细
 * 
 * @author liuxb
 * 
 */
public class PartialWarehouseDetailForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8612709913848931983L;

	@BeanField(title = "KEY", name = "key", type = FieldType.String, length = 11, notNull = true)
	private String key;

	@BeanField(title = "零件ID", name = "partial_id", type = FieldType.String, length = 11, notNull = true)
	private String partial_id;

	@BeanField(title = "数量", name = "quantity", type = FieldType.Integer, length = 5, notNull = true)
	private String quantity;

	@BeanField(title = "零件名称", name = "partial_name")
	private String partial_name;

	@BeanField(title = "零件编码", name = "code")
	private String code;

	@BeanField(title = "规格种别", name = "spec_kind", type = FieldType.Integer, length = 1)
	private String spec_kind;

	@BeanField(title = "规格种别名称", name = "spec_kind_name")
	private String spec_kind_name;

	@BeanField(title = "是否分装", name = "unpack_flg", type = FieldType.Integer)
	private String unpack_flg;

	@BeanField(title = "分装数量", name = "split_quantity", type = FieldType.Integer)
	private String split_quantity;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSpec_kind() {
		return spec_kind;
	}

	public void setSpec_kind(String spec_kind) {
		this.spec_kind = spec_kind;
	}

	public String getSpec_kind_name() {
		if (spec_kind != null) {
			return CodeListUtils.getValue("partial_spec_kind", spec_kind);
		}
		return spec_kind_name;
	}

	public void setSpec_kind_name(String spec_kind_name) {
		this.spec_kind_name = spec_kind_name;
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

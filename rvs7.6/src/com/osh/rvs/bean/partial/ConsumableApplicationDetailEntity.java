package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @Title ConsumableApplicationDetailEntity.java
 * @Project rvs
 * @Package com.osh.rvs.bean.partial
 * @ClassName: ConsumableApplicationDetailEntity
 * @Description: 消耗品申请单明细
 * @author lxb
 * @date 2015-5-21 上午8:42:37
 */
public class ConsumableApplicationDetailEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3735122812970771220L;

	/** 消耗品申请单Key **/
	private String consumable_application_key;

	/** 零件 ID **/
	private String partial_id;

	/** 补充方式 **/
	private Integer apply_method;

	/** 申请人D **/
	private String petitioner_id;

	/** 计量单位 **/
	private Integer unit_code;

	/** 申请数量 **/
	private Integer apply_quantity;

	/** 提供数量 **/
	private Integer supply_quantity;

	/** 包装 **/
	private Integer pack_method;
	
	/**消耗品代码**/
	private String code;
	
	/**零件名称**/
	private String partial_name;

	/**当前有效库存**/
	private Integer available_inventory;
	
	/**待发放数量**/
	private Integer waitting_quantity;
	
	/**分类 **/
	private Integer type;
	
	/**内容量**/
	private Integer content;
	
	
	/**开封标记**/
	private String openflg;
	
	/**发放数量DB**/
	private Integer db_supply_quantity;
	
	/**开启库存**/
	private Integer unseal_items;
	
	/**单位名称**/
	private String unit_name;

	/**存放位置**/
	private String stock_code;

	/**价格**/
	private BigDecimal price;

	/**剪裁长度**/
	private Integer cut_length;
	private String cut_length_options;

	public String getConsumable_application_key() {
		return consumable_application_key;
	}

	public void setConsumable_application_key(String consumable_application_key) {
		this.consumable_application_key = consumable_application_key;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public Integer getApply_method() {
		return apply_method;
	}

	public void setApply_method(Integer apply_method) {
		this.apply_method = apply_method;
	}

	public String getPetitioner_id() {
		return petitioner_id;
	}

	public void setPetitioner_id(String petitioner_id) {
		this.petitioner_id = petitioner_id;
	}

	public Integer getUnit_code() {
		return unit_code;
	}

	public void setUnit_code(Integer unit_code) {
		this.unit_code = unit_code;
	}

	public Integer getApply_quantity() {
		return apply_quantity;
	}

	public void setApply_quantity(Integer apply_quantity) {
		this.apply_quantity = apply_quantity;
	}

	public Integer getSupply_quantity() {
		return supply_quantity;
	}

	public void setSupply_quantity(Integer supply_quantity) {
		this.supply_quantity = supply_quantity;
	}

	public Integer getPack_method() {
		return pack_method;
	}

	public void setPack_method(Integer pack_method) {
		this.pack_method = pack_method;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public Integer getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(Integer available_inventory) {
		this.available_inventory = available_inventory;
	}

	public Integer getWaitting_quantity() {
		return waitting_quantity;
	}

	public void setWaitting_quantity(Integer waitting_quantity) {
		this.waitting_quantity = waitting_quantity;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getContent() {
		return content;
	}

	public void setContent(Integer content) {
		this.content = content;
	}

	public String getOpenflg() {
		return openflg;
	}

	public void setOpenflg(String openflg) {
		this.openflg = openflg;
	}

	public Integer getDb_supply_quantity() {
		return db_supply_quantity;
	}

	public void setDb_supply_quantity(Integer db_supply_quantity) {
		this.db_supply_quantity = db_supply_quantity;
	}

	public Integer getUnseal_items() {
		return unseal_items;
	}

	public void setUnseal_items(Integer unseal_items) {
		this.unseal_items = unseal_items;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getCut_length() {
		return cut_length;
	}

	public void setCut_length(Integer cut_length) {
		this.cut_length = cut_length;
	}

	public String getCut_length_options() {
		return cut_length_options;
	}

	public void setCut_length_options(String cut_length_options) {
		this.cut_length_options = cut_length_options;
	}

}

package com.osh.rvs.bean.pda;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @Title PdaApplyEntity.java
 * @Project rvs
 * @Package com.osh.rvs.bean.pda
 * @ClassName: PdaApplyEntity
 * @Description: 消耗品申请单明细
 * @author lxb
 * @date 2015-5-21 上午8:42:37
 */
public class PdaApplyEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1043284972896651274L;

	/** 消耗品申请单Key **/
	private String consumable_application_key;
	
	/**消耗品ID**/
	private String partial_id;

	/** 编号**/
	private String application_no;

	/** 课室ID **/
	private String section_id;

	/** 工程ID **/
	private String line_id;

	/** 工程 **/
	private String line_name;
	
	/** 即时**/
	private String flg;

	/** SAP修理通知单No**/
	private String omr_notifi_no;

	/** 理由 **/
	private String apply_reason;

	/** 补充方式 **/
	private Integer apply_method;

	/** 类型 **/
	private Integer type;

	/** 代码 **/
	private String code;

	/** 零件名称 **/
	private String name;

	/** 库位编号* */
	private String stock_code;

	/** 申请数量* */
	private Integer apply_quantity;

	/** 已发放数量**/
	private Integer supply_quantity;

	/** DB已发放数量**/
	private Integer db_supply_quantity;

	/** 当前有效库存**/
	private Integer available_inventory;

	/** 申请人D **/
	private String petitioner_id;

	/** 待处理件数**/
	private Integer count;

	/*内容量*/
	private Integer content;

	/** 总价 **/
	private BigDecimal total_price;

	/**剪裁长度**/
	private Integer cut_length;

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

	public String getApplication_no() {
		return application_no;
	}

	public void setApplication_no(String application_no) {
		this.application_no = application_no;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getFlg() {
		return flg;
	}

	public void setFlg(String flg) {
		this.flg = flg;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getApply_reason() {
		return apply_reason;
	}

	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}

	public Integer getApply_method() {
		return apply_method;
	}

	public void setApply_method(Integer apply_method) {
		this.apply_method = apply_method;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
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

	public Integer getDb_supply_quantity() {
		return db_supply_quantity;
	}

	public void setDb_supply_quantity(Integer db_supply_quantity) {
		this.db_supply_quantity = db_supply_quantity;
	}

	public Integer getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(Integer available_inventory) {
		this.available_inventory = available_inventory;
	}

	public String getPetitioner_id() {
		return petitioner_id;
	}

	public void setPetitioner_id(String petitioner_id) {
		this.petitioner_id = petitioner_id;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getTotal_price() {
		return total_price;
	}

	public void setTotal_price(BigDecimal total_price) {
		this.total_price = total_price;
	}

	public Integer getContent() {
		return content;
	}

	public void setContent(Integer content) {
		this.content = content;
	}

	public Integer getCut_length() {
		return cut_length;
	}

	public void setCut_length(Integer cut_length) {
		this.cut_length = cut_length;
	}
}

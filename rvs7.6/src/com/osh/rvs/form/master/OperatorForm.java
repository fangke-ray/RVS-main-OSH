package com.osh.rvs.form.master;

import java.util.List;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.AutofillArrayList;

public class OperatorForm extends ActionForm {

	/** serialVersionUID */
	private static final long serialVersionUID = 3185654622960643339L;

	/** 担当人 ID */
	@BeanField(title = "担当人 ID", name = "operator_id", primaryKey = true, length = 11)
	private String id;
	/** 担当人姓名 */
	@BeanField(title = "担当人姓名", name = "name", type = FieldType.String, notNull=true, length = 8)
	private String name;
	/** 工号 */
	@BeanField(title = "工号", name = "job_no", notNull=true, length = 8)
	private String job_no;
	/** 密码 */
	@BeanField(title = "密码", name = "pwd", type = FieldType.String, cipher = true)
	private String pwd;
	/** 密码修改日 */
	@BeanField(title = "密码修改日", name = "pwd_date", type = FieldType.Date)
	private String pwd_date;
	/** 是否记录工时 */
	@BeanField(title = "是否记录工时", name = "work_count_flg", type = FieldType.Integer)
	private String work_count_flg;
	/** 课室 */
	@BeanField(title = "课室", name = "section_id")
	private String section_id = "";
	private String section_name;
	/** 工程 */
	@BeanField(title = "工程", name = "line_id")
	private String line_id = "";
	private String line_name;
	/** 主要担当工位 */
	@BeanField(title = "主要担当工位", name = "position_id")
	private String position_id;
	private String position_name;
	/** 所在角色 */
	@BeanField(title = "所在角色", name = "role_id", notNull=true)
	private String role_id = "";
	private String role_name;
	@BeanField(title = "", name = "rank_kind", type = FieldType.Integer)
	private String rank_kind;
	/** 邮箱地址 */
	@BeanField(title = "邮箱地址", name = "email", length = 45, type = FieldType.String)
	private String email;
	/** 最后更新人 */
	@BeanField(title = "更新者", name = "updated_by")
	private String updated_by;
	/** 最后更新时间 */
	@BeanField(title = "更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;

	@BeanField(title = "分线", name = "px", type = FieldType.Integer)
	private String px;

	@BeanField(title = "间接作业能力", name = "af_ability", type = FieldType.Integer)
	private String af_ability;

	/** 拥有技能 */
	private List<String> abilities = new AutofillArrayList<String>(String.class);
	/** 拥有临时角色 */
	private List<String> temp_role = new AutofillArrayList<String>(String.class);
	/** 主要担当工位 */
	private List<String> main_positions = new AutofillArrayList<String>(String.class);
	/** 主要担当工位 */
	private List<String> af_abilities = new AutofillArrayList<String>(String.class);
	/** 关注工位 */
	private List<String> notice_positions = new AutofillArrayList<String>(String.class);

	/**
	 * 取得担当人 ID
	 * @return id 担当人 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 担当人 ID设定
	 * @param id 担当人 ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 取得角色 ID
	 * @return role_id 角色 ID
	 */
	public String getRole_id() {
		return role_id;
	}

	/**
	 * 角色 ID设定
	 * @param role_id 角色 ID
	 */
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}

	public String getJob_no() {
		return job_no;
	}

	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getPwd_date() {
		return pwd_date;
	}

	public void setPwd_date(String pwd_date) {
		this.pwd_date = pwd_date;
	}

	public String getWork_count_flg() {
		return work_count_flg;
	}

	public void setWork_count_flg(String work_count_flg) {
		this.work_count_flg = work_count_flg;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getTemp_role() {
		return temp_role;
	}

	public void setTemp_role(List<String> temp_role) {
		this.temp_role = temp_role;
	}

	/**
	 * 取得担当人姓名
	 * @return name 担当人姓名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 角色担当人姓名定
	 * @param name 担当人姓名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得最后更新人
	 * @return updated_by 最后更新人
	 */
	public String getUpdated_by() {
		return updated_by;
	}

	/**
	 * 最后更新人设定
	 * @param updated_by 最后更新人
	 */
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	/**
	 * 取得最后更新时间
	 * @return updated_time 最后更新时间
	 */
	public String getUpdated_time() {
		return updated_time;
	}

	/**
	 * 最后更新时间设定
	 * @param updated_time 最后更新时间
	 */
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	/**
	 * 取得课室 ID
	 * @return section_id 课室 ID
	 */
	public String getSection_id() {
		return section_id;
	}

	/**
	 * 课室 ID设定
	 * @param section_id 课室 ID
	 */
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	/**
	 * 取得课室名称
	 * @return name 课室名称
	 */
	public String getSection_name() {
		return section_name;
	}

	/**
	 * 课室名称设定
	 * @param name 课室名称
	 */
	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	/**
	 * 取得工程 ID
	 * @return line_id 工程 ID
	 */
	public String getLine_id() {
		return line_id;
	}

	/**
	 * 工程 ID设定
	 * @param line_id 工程 ID
	 */
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	/**
	 * 取得工程名称
	 * @return name 工程名称
	 */
	public String getLine_name() {
		return line_name;
	}

	/**
	 * 工程名称设定
	 * @param name 工程名称
	 */
	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	/**
	 * 取得拥有技能
	 * @return abilities 拥有技能
	 */
	public List<String> getAbilities() {
		return abilities;
	}

	/**
	 * 拥有技能设定
	 * @param abilities 拥有技能
	 */
	public void setAbilities(List<String> abilities) {
		this.abilities = abilities;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getPosition_name() {
		return position_name;
	}

	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}

	public String getRank_kind() {
		return rank_kind;
	}

	public void setRank_kind(String rank_kind) {
		this.rank_kind = rank_kind;
	}

	public List<String> getMain_positions() {
		return main_positions;
	}

	public void setMain_positions(List<String> main_positions) {
		this.main_positions = main_positions;
	}

	public String getPx() {
		return px;
	}

	public void setPx(String px) {
		this.px = px;
	}

	public List<String> getAf_abilities() {
		return af_abilities;
	}

	public void setAf_abilities(List<String> af_abilities) {
		this.af_abilities = af_abilities;
	}

	public String getAf_ability() {
		return af_ability;
	}

	public void setAf_ability(String af_ability) {
		this.af_ability = af_ability;
	}

	public List<String> getNotice_positions() {
		return notice_positions;
	}

	public void setNotice_positions(List<String> notice_positions) {
		this.notice_positions = notice_positions;
	}
}

package com.osh.rvs.form.master;

import java.io.Serializable;

import com.osh.rvs.form.UploadForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class SystemImageManageForm extends UploadForm implements Serializable {

	/**
	 * 系统图片管理
	 */
	private static final long serialVersionUID = -3943425379272921791L;
	
	//上传文件名
	@BeanField(title = "上传文件名", name = "file_name", type = FieldType.String,notNull = true)
	private String file_name;
	
	//分类
	@BeanField(title = "分类", name = "classify", type = FieldType.String,notNull = true)
	private String classify;
	
	//说明
	@BeanField(title = "说明", name = "description", type = FieldType.String, length = 200)
	private String description;

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}
}

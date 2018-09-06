package com.osh.rvs.bean.master;

import java.io.Serializable;

public class SystemImageManageEntity implements Serializable {

	private static final long serialVersionUID = 5980439877150504140L;
	
	//上传文件名
	private String file_name;
	
	//分类
	private String classify;
	
	//说明
	private String description;

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

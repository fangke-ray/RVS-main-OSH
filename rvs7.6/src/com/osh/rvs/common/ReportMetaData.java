package com.osh.rvs.common;

public class ReportMetaData {

	public static final String[] materialTitles = { "修理单号", "ESAS No.", "机种",
		"型号", "机身号", "等级", "客户同意", "委托处", "投线日期", "入库预定日", "零件到货", "零件BO",
		"零件缺品详细", "维修课", "进展工位", "拆镜时间", "零件订购安排", "分解产出安排", "分解实际产出",
		"NS进展工位", "NS产出安排", "NS实际产出", "6天纳期", "总组出货安排", "AM/PM", "加急",
		"工程内发现/不良", "备注" };
	public static final String[] materialColNames = { "sorc_no", "esas_no",
		"category_name", "model_name", "serial_no", "levelName",
		"agreed_date", "ocmName", "inline_time", "arrival_plan_date",
		"arrival_date", "bo_flg", "bo_contents", "section_name",
		"processing_position", "dismantle_time", "order_date",
		"dec_plan_date", "dec_finish_date", "ns_processing_position",
		"ns_plan_date", "ns_finish_date", "com_plan_date",
		"com_finish_date", "am_pm", "scheduled_expedited", "break_message",
		"scheduled_manager_comment" };

	public static final String[] scheduleTitles = { "计划工程", "修理单号", "机种", "型号", "机身号",
		"等级", "客户同意", "委托处", "入库预定日", "零件BO", "加急", "维修课", "进展工位", "6天纳期", "总组出货安排" };
	public static final String[] scheduleColNames = { "line_name", "sorc_no",
		"category_name", "model_name", "serial_no", "levelName", "agreed_date", "ocmName",
		"arrival_plan_date", "bo_flg", "scheduled_expedited", "section_name", "processing_position", "com_plan_date", "com_finish_date" };

	
	public static final String[] boPartialTitles = {"修理单号", "型号", "机身号", "等级", "零件订购日", "入库预定日", "零件到货", "零件BO", "零件缺品详细", "加急","发生次数","发生工位"};
	public static final String[] boPartialColNames = {"sorc_no","model_name","serial_no","levelName","order_date","arrival_plan_date","arrival_date","bo_flg","bo_contents","scheduled_expedited","occur_times","process_code"};
	
	public static final String[] factMaterialTitles = {"投线时间","修理单号", "ESAS No.", "型号", "机身号", "等级", "投入课室", "客户同意日"};
	public static final String[] factMaterialColNames = {"inline_time","sorc_no","esas_no","model_name","serial_no","levelName","section_name","agreed_date"};
}

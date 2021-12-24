var quotation_listdata = {};
/** 服务器处理路径 */
var servicePath = "qaResult.do";

var doInit_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);//计算输出的总数

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			qaresult_list(resInfo.finished);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doInit=function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,//同步的请求
		url : servicePath + '?method=search',//发送请求的地址执行的方法
		cache : false,
		data : {complete_date_start : $("#search_qa_pass_start").val()},//发送到服务器的数据
		type : "post",//请求方式是post，默认的是get
		dataType : "json",//预期服务器返回的数据类型json
		success : ajaxSuccessCheck,//当请求成功后调用的回调函数
		error : ajaxError,//请求失败时被调用的函数
		complete : doInit_ajaxSuccess//请求完成后调用的回调函数（请求失败或成功均调用）
	});
};

/** 
 * 检索处理
 */
var findit = function() {
	// 读取已记录检索条件提交给后台
	var data = {
		"category_id" : $("#search_category_id").data("post"),
		"model_id":$("#search_model_id").data("post"),
		"serial_no":$("#search_serialno").data("post"),
		"sorc_no":$("#search_sorcno").data("post"),
		"section_id":$("#search_section_id").data("post"),
		"reception_time_start":$("#search_reception_time_start").data("post"),
		"reception_time_end":$("#search_reception_time_end").data("post"),
		"complete_date_start":$("#search_qa_pass_start").val(),
		"complete_date_end":$("#search_qa_pass_end").val(),
		"scheduled_date_start":$("#search_scheduled_date_start").data("post"),
		"scheduled_date_end":$("#search_scheduled_date_end").data("post"),
		"fix_type":$("#search_fix_id").data("post"),
		"service_repair_flg":$("#search_repair_id").data("post"),
		"direct_flg":$("#search_direct_id").data("post"),
		"operator_id":$("#search_person_id").data("post"),
		"procedure" :$("#search_procedure").data("post")
	}

// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,//异步的请求
		url : servicePath + '?method=search',//请求提交的地址
		cache : false,//缓存
		data : data,//需要提交的数据
		type : "post",//提交类型post
		dataType : "json",//数据类型是json
		success : ajaxSuccessCheck,//请求成功以后需要执行的函数
		error : ajaxError,//请求错误后需要执行的函数
		complete : doInit_ajaxSuccess//请求完成需要做的事（不管是请求成功或者是错误的情况）
	});
};
//重置值为空
var reset = function() {
	$("#search_category_id").val("").trigger("change").data("post", "");
	$("#search_txt_modelname").val("").data("post", "");
	$("#search_model_id").val("").data("post", "").prev().val("");
	$("#search_serialno").val("").data("post", "");
	$("#search_sorcno").val("").data("post", "");
	$("#search_section_id").val("").trigger("change").data("post", "");
	$("#search_reception_time_start").val("").data("post", "");
	$("#search_reception_time_end").val("").data("post", "");
	$("#search_qa_pass_start").val($("#h_date_start").val()).data("post", $("#h_date_start").val());
	$("#search_qa_pass_end").val("").data("post", "");
	$("#search_scheduled_date_start").val("").data("post", "");
	$("#search_scheduled_date_end").val("").data("post", "");
	$("#search_person_id").val("").trigger("change").data("post", "");
	$("#search_fix_id").val("").trigger("change").data("post", "");
	$("#search_repair_id").val("").trigger("change").data("post", "");
	$("#search_direct_id").val("").trigger("change").data("post", "");
	$("#search_person_id").val("").data("post","");
	$("#search_procedure").data("post","");
	$("#search_procedure_0").attr("checked", true).trigger("change");
};

//页面加载完毕需要进行的操作
$(function() {	
	$("input.ui-button").button();//检索和清除点击事件
	$("a.areacloser").hover(//检索条件的右上角的超链接将鼠标移动上去和移走鼠标后的样式
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	$("#body-mdl span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#search_procedure").buttonset();

	$("#search_category_id, #search_section_id,#search_person_id").select2Buttons();
//	$("#search_fix_id").select2Buttons();
	$("#search_fix_id,#search_repair_id,#search_direct_id").select2Buttons();
//	$("#search_fix_id,#search_repair_id,#search_direct_id").select2Buttons();

	$("#search_reception_time_start, #search_reception_time_end, #search_qa_pass_start, #search_qa_pass_end, #search_scheduled_date_start, #search_scheduled_date_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	
	//当开始点击检索按钮时触发的事件
	$("#searchbutton").click(function() {//按照按钮的ID的获取其值，再将值使用post提交
		$("#search_category_id").data("post", $("#search_category_id").val());
		$("#search_model_id").data("post", $("#search_model_id").val());
		$("#search_serialno").data("post", $("#search_serialno").val());
		$("#search_sorcno").data("post", $("#search_sorcno").val());
		$("#search_section_id").data("post", $("#search_section_id").val());
		$("#search_reception_time_start").data("post", $("#search_reception_time_start").val());
		$("#search_reception_time_end").data("post", $("#search_reception_time_end").val());
		$("#search_qa_pass_start").data("post", $("#search_qa_pass_start").val());
		$("#search_qa_pass_end").data("post", $("#search_qa_pass_end").val());
		$("#search_scheduled_date_start").data("post", $("#search_scheduled_date_start").val());
		$("#search_scheduled_date_end").data("post", $("#search_scheduled_date_end").val());
        $("#search_person_id").data("post",$("#search_person_id").val());
        $("#search_fix_id").data("post",$("#search_fix_id").val());
        $("#search_repair_id").data("post",$("#search_repair_id").val());
        $("#search_direct_id").data("post",$("#search_direct_id").val());
        $("#search_person_id").data("post",$("#search_person_id").val());
        $("#search_procedure").data("post",$("#search_procedure input:checked").val());
		findit();//检索开始并提交给后台
	});

	setReferChooser($("#search_model_id"), $("#model_refer"));//维修对象型号 下拉作用选择

	$("#resetbutton").click(function() {
		reset();//点击清除将执行reset();将所有的检索值清空
	}); 
	doInit();
    });
//JqGrid表格
function qaresult_list(q_listdata){
	quotation_listdata = q_listdata;
	if ($("#gbox_exd_list").length > 0) {
		$("#exd_list").jqGrid().clearGridData();//清除
		$("#exd_list").jqGrid('setGridParam',{data:quotation_listdata}).trigger("reloadGrid", [{current:false}]);//刷新列表
	} else {
		$("#exd_list").jqGrid({
			toppager : true,
			data:quotation_listdata,//表中数据
			width: 992,
			height: 390,
			rowheight: 23,
			datatype: "local",//设定将要得到的数据类型
			colNames:['','受理日','同意日','品保日','出检人员', '修理单号', '型号 ID', '型号' , '机身号','品保结果','修理分类','返修分类','直送'],//字符串数组，用于指定各列的题头文本，与列的顺序是对应的
			colModel:[//最重要的数组之一，用于设定各列的参数，可以用对象进行替换
					{name:'material_id',index:'material_id', hidden:true},
					{
						name : 'reception_time',
						index : 'reception_time',
						width : 40,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'agreed_date',
						index : 'agreed_date',
						width : 40,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'finish_time',
						index : 'finish_time',
						width : 40,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					},
				{name:'operation_name',index:'operation_name',width:50,align:'left'},
				{name:'sorc_no',index:'sorc_no', width:100,align:'left'},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_name', width:125,align:'left'},
				{name:'serial_no',index:'serial_no', width:90, align:'left'},
				{name:'result',index:'result', width:50, align:'center', formatter:'select', editoptions:{value:"2:合格;6:不合格"}},
				{name:'fix_type',index:'fix_type',width:50,align:'center', formatter:'select', editoptions:{value:$("#h_goMaterialFixType").val()}},
				{name:'service_repair_flg',index:'service_repair_flg',width:50,align:'center',formatter:'select', editoptions:{value:$("#h_goMaterialServiceRepaire").val()}},
				{name:'direct_flg',index:'direct_flg',width:50,align:'center',formatter:'select', editoptions:{value:$("#h_goMaterialDirect").val()}}
			],
			rowNum: 20,//由于表格的中一次显示的行数，默认值为20。正是这个选项将参数rows（prmNames中设置的）通过
			//url选项是指的链接传递到server。注意如果server返回的数据行数超过了rowNum的设定，则grid也只显示rowNum的行数
			toppager: false,
			pager: "#exd_listpager",//设置分页的,在页面中是使用<div/>来放置的
			viewrecords: true,//设置是否在page bar 显示所有记录的总数
			hidegrid : false,//
			caption: "完成维修一览",//标题
			gridview: true, // 
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			ondblClickRow : function(rid, iRow, iCol, e) {
				var data = $("#exd_list").getRowData(rid);
				var material_id = data["material_id"];
				showPcsDetail(material_id, true);
			},
			gridComplete: function(){				
				var records=quotation_listdata.length;					
				var j=0;//不合格台数
				
				var qualify_611=0;	//611工位的合格台数			
				var qualify_612 = 0;//612工位的合格台数
				var qualify_613 = 0;//613工位的合格台数
				var qualify_614 = 0;//614工位的合格台数
				var qualify_6A1 = 0;//6A1工位的合格台数
				var qualify_6A2 = 0;//6A1工位的合格台数
			
				var unqualify_611=0;	//611工位的不合格台数			
				var unqualify_612 = 0;//612工位的不合格台数
				var unqualify_613 = 0;//613工位的不合格台数
				var unqualify_614 = 0;//614工位的不合格台数
				var unqualify_6A1 = 0;//6A1工位的不合格台数
				var unqualify_6A2 = 0;//6A1工位的不合格台数
				
				for(var i=0;i<records;i++){		
					//不合格
					if(quotation_listdata[i].result==6){
						j++;

						switch(quotation_listdata[i].process_code) {
							case '611': unqualify_611++; break;
							case '612': unqualify_612++; break;
							case '613': unqualify_613++; break;
							case '614': unqualify_614++; break;
							case '65D': unqualify_6A1++; break;
							case '69D': unqualify_6A2++; break;
						}	
					}
					
					//合格
					if(quotation_listdata[i].result==2){
					
						switch(quotation_listdata[i].process_code) {
							case '611': qualify_611++; break;
							case '612': qualify_612++; break;
							case '613': qualify_613++; break;
							case '614': qualify_614++; break;
							case '65D': qualify_6A1++; break;
							case '69D': qualify_6A2++; break;
						}	
					}
				}
				var c=" - ";
				if (records > 0) {
					c=(records-j)/records*100;
					c=c.toFixed(1);
				}
				var countText = "当前检索范围内，检查总数"+records+"台（611 : "+qualify_611+"，612 : "+qualify_612+"，613 : "+qualify_613+"，614 : "+qualify_614+"，65D : "+qualify_6A1+"，69D : "+qualify_6A2+"）；" +
							"不合格"+j+"台（611 : "+unqualify_611+"，612 : "+unqualify_612+"，613 : "+unqualify_613+"，614 : "+unqualify_614+"，65D : "+unqualify_6A1+"，69D : "+unqualify_6A2+"）；检查合格率"+c+"%";
				if($("#count_result").length==0){
					$("#exd_listarea .ui-jqgrid-titlebar").append("<span id='count_result' style='float:right;margin-right:6px;font-size:15px;'>" +
							countText + "</span>");
				} else {
					$("#count_result").text(countText).attr("font-size","15px");
				}
		}
	});	
   }
};
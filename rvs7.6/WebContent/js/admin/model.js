/** 模块名 */
var modelname = "维修对象型号";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "model.do";
/** 自动完成数据 */
var autocomp1 = {};
var autocomp2 = {};
var autocomp3 = {};
/**
 * 页面加载处理
 */
$(function() {
	// 适用jquery按钮
	$("input.ui-button").button();

	// 右上图标效果
	$("a.areacloser").hover(function() { $(this).addClass("ui-state-hover");},
		function() {$(this).removeClass("ui-state-hover");});
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
	$("#cancelbutton, #editarea span.ui-icon").click(function() {
		showList();
	});

	// 初始状态隐藏编辑和详细显示
	$("#editarea").hide();
	$("#detailarea").hide();

	// 检索表单认证(没有前台认证也要写,为了对应后台出错时效果)
	$("#searchform").validate({});

	$("#selectable").buttonset();
	// ///////////////////以上共通///////////////////////

	AutoComplete();

	$("#b_idx").buttonset().hide();

	// 检索处理
	$("#searchbutton").click(function() {
		// 保存检索条件
		$("#cond_id").data("post", $("#cond_id").val());
		$("#cond_name").data("post", $("#cond_name").val());
		$("#cond_category_id").data("post", $("#cond_category_id").val());
		$("#cond_feature1").data("post", $("#cond_feature1").val());
		$("#cond_feature2").data("post", $("#cond_feature2").val());
		$("#cond_series").data("post", $("#cond_series").val());
		$("#cond_el_base_type").data("post",$("#cond_el_base_type").val());
		$("#cond_s_connector_base_type").data("post",$("#cond_s_connector_base_type").val());
		$("#cond_operate_part_type").data("post",$("#cond_operate_part_type").val());
		$("#cond_ocular_type").data("post",$("#cond_ocular_type").val());
		$("#cond_imbalance_line_id").data("post",$("#cond_imbalance_line_id").val());

		// 查询
		findit();
	});

	// 清空检索条件
	$("#resetbutton").click(function() {
		// hidden reset
		$("#cond_id").val("");
		$("#cond_name").val("");
		$("#cond_category_id").val("").trigger("change");
		$("#cond_feature1").val("");
		$("#cond_feature2").val("");
		$("#cond_series").val("");
		$("#cond_imbalance_line_id").val("").trigger("change");

		$("#cond_id").data("post", "");
		$("#cond_name").data("post", "");
		$("#cond_category_id").data("post", "");
		$("#cond_feature1").data("post", "");
		$("#cond_feature2").data("post", "");
		$("#cond_series").data("post", "");

		$("#cond_el_base_type").data("post", "").val("");
		$("#cond_s_connector_base_type").data("post", "").val("");
		$("#cond_operate_part_type").data("post", "").val("");
		$("#cond_ocular_type").data("post", "").val("");
		$("#cond_imbalance_line_id").data("post", "").val("");
		
	});

	$("#body-mdl select").select2Buttons();
//	$("#input_imbalance_line_id").change(function(){
//		var lines = $(this).val();
//		if (lines == null) {
//			$("#b_idx").hide();
//		} else if ($.inArray("00000000014", lines) >= 0) {
//			$("#b_idx").show();
//		} else {
//			$("#b_idx").hide();
//		}
//	});

	findit();
});

function AutoComplete(){
	// 获取自动完成数据
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getautocomp',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj){
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.autocomp != null) {
					autocomp1 = resInfo.autocomp.feature1;
					autocomp2 = resInfo.autocomp.feature2;
					autocomp3 = resInfo.autocomp.series;
					autocomp4 = resInfo.autocomp.el_base_type;
					autocomp5 = resInfo.autocomp.s_connector_base_type;
					autocomp6 = resInfo.autocomp.operate_part_type;
					autocomp7 = resInfo.autocomp.ocular_type;
				}
			} catch (e) {
			};
			$("#cond_feature1").autocomplete({source : autocomp1, minLength: 0, delay: 100});
			$("#cond_feature2").autocomplete({source : autocomp2, minLength: 0});
			$("#cond_series").autocomplete({source : autocomp3, minLength: 0});
			$("#input_feature1").autocomplete({source : autocomp1, minLength: 0, delay: 100});
			$("#input_feature2").autocomplete({source : autocomp2, minLength: 0});
			$("#input_series").autocomplete({source : autocomp3, minLength: -1});
			$("#cond_el_base_type").autocomplete({source : autocomp4, minLength:0});
			$("#cond_s_connector_base_type").autocomplete({source : autocomp5, minLength:0});
			$("#cond_operate_part_type").autocomplete({source : autocomp6, minLength:0});
			
			$("#input_el_base_type").autocomplete({source : autocomp4, minLength:0});
			$("#input_s_connector_base_type").autocomplete({source : autocomp5, minLength:0});
			$("#input_operate_part_type").autocomplete({source : autocomp6, minLength:0});
			
			$("#cond_ocular_type").autocomplete({source : autocomp7, minLength:0});//接眼类别
			$("#input_ocular_type").autocomplete({source : autocomp7, minLength:0});
		}
	});
}

/**
 * 检索Ajax通信成功时的处理
 */
var search_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			// 标题修改
			top.document.title = modelname + "一览";

			// 读取一览
			listdata = resInfo.list;
			// HTML Decode
			for (var line in listdata) {
				var linedata = listdata[line];
				linedata.name = decodeText(linedata.name);
				linedata.category_name = decodeText(linedata.category_name);
			}

			if ($("#gbox_list").length > 0) {
				// jqGrid已构建的情况下,重载数据并刷新
				$("#list").jqGrid().clearGridData();
				$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			} else {
				// 构建jqGrid对象
				$("#list").jqGrid({
					data : listdata,
					height : 461,
					width : gridWidthMiddleRight,
					rowheight : 23,
					datatype : "local",
					colNames : ['', '维修对象型号 ID', '机种 ID', '维修对象机种', '维修对象型号名称', '备注1', '备注2', '系列','EL 座<br>类别','S 连接座<br>类别','操作部<br>类别','接眼<br>类别','最后更新人', '最后更新时间'],
					colModel : [
					{name:'myac', width:48, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true, editbutton:false}},
					{ // 工程 ID
						name : 'id',
						index : 'id',
						width : 90,
						sorttype : "integer",
						hidden : true
					},
					{name:'category_id',index:'category_id', hidden:true},
					{name:'category_name',index:'category_name', width:100},
					{ // 维修对象型号名称
						name : 'name',
						index : 'name',
						width : 100
					}, 
					{name:'feature1',index:'feature1', width:55},
					{name:'feature2',index:'feature2', width:55},
					{name:'series',index:'series', width:55},
					{name:'el_base_type',index:'el_base_type', width:55},
					{name:'s_connector_base_type',index:'s_connector_base_type', width:55},
					{name:'operate_part_type',index:'operate_part_type', width:55},
					{name:'ocular_type',index:'ocular_type', width:55},
					{ // 最后更新人
						name : 'updated_by',
						index : 'updated_by',
						width : 50
					}, { // 最后更新时间
						name : 'updated_time',
						index : 'updated_time',
						width : 90
					}],
					toppager : false,
					rowNum : 20,
					pager : "#listpager",
					viewrecords : true,
					caption : modelname + "一览",
					ondblClickRow : showEdit,
					gridview : true, // Speed up
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true]
				});
				$(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
						'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建'+ modelname +'" role="button" aria-disabled="false">' +
					'</div>');
				$("#addbutton").button();
				// 追加处理
				$("#addbutton").click(showAdd);
			}

		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/**
 * 更新完成Ajax通信成功时的处理
 */
var update_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#editarea", resInfo.errors);
			// 编辑确认按钮重新有效
			$("#editbutton").enable();
			$("#executebutton").enable();
		} else {
			// 重新查询
			findit();
			// 切回一览画面
			showList();
			AutoComplete();
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

/**
 * 读取修改详细信息Ajax通信成功时的处理
 */
var showedit_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			// 默认画面变化 s
			top.document.title = modelname + "修改";
			$("#searcharea").hide();
			$("#listarea").hide();
			$("#editarea span.areatitle:eq(0)").html(modelname + "：" + encodeText(resInfo.modelForm.name));
			$("#editarea").show();
			$("#editform tr").show();
			$("#editbutton").val("修改");
			$("#editbutton").enable();
			$("#detailarea tr:has(#label_detail_updated_by)").hide();
			$("#detailarea tr:has(#label_detail_updated_time)").hide();
			$(".errorarea-single").removeClass("errorarea-single");
			// 默认画面变化 e

			// 详细数据
			$("#label_edit_id").text(resInfo.modelForm.id);
			$("#input_name").val(encodeText(resInfo.modelForm.name));
			$("#input_category_id").unbind("change", postCategoryChange);
			$("#input_category_id").val(resInfo.modelForm.category_id).trigger("change");
			$("#input_category_id").bind("change", postCategoryChange);
			$("#input_feature1").val(resInfo.modelForm.feature1);
			$("#input_feature2").val(resInfo.modelForm.feature2);
			$("#input_series").val(resInfo.modelForm.series);
			$("#input_el_base_type").val(resInfo.modelForm.el_base_type);
			$("#input_s_connector_base_type").val(resInfo.modelForm.s_connector_base_type);
			$("#input_operate_part_type").val(resInfo.modelForm.operate_part_type);
			$("#label_edit_updated_by").text(resInfo.modelForm.updated_by);
			$("#label_edit_updated_time").text(resInfo.modelForm.updated_time);
			$("#input_ocular_type").val(resInfo.modelForm.ocular_type);
			$("#input_item_code").val(resInfo.modelForm.item_code);
			$("#input_description").val(resInfo.modelForm.description);
			$("#input_default_pat_id").val(fillZero(resInfo.modelForm.default_pat_id, 11)).trigger("change");

			if(resInfo.modelForm.selectable==1){
				$("#selectable_yes").attr("checked", "checked").trigger("change");
			}else{
				$("#selectable_no").attr("checked", "checked").trigger("change");
			}
			$("#input_imbalance_line_id option").attr("selected", "").trigger("change");
			for (var ii in resInfo.modelForm.imbalance_line_ids) {
				var imbalance_line_id = resInfo.modelForm.imbalance_line_ids[ii];
		    	$("#input_imbalance_line_id option[value='"+imbalance_line_id+"']").attr("selected","selected").trigger("change");
			}

			$("#editform").validate({
				rules : {
					category_id : {
						required : true
					},
					name : {
						required : true
					}
				}
			});

			// 切换按钮效果
			$("#editbutton").unbind("click");
			$("#editbutton").click(function() {
				// 前台Validate
				if ($("#editform").valid()) {
					// 通过Validate,切到修改确认画面
					$("#editbutton").disable();

					var data = {
						"id" : $("#label_edit_id").text(),
						"category_id" : $("#input_category_id").val(),
						"name" : $("#input_name").val(),
						"feature1" : $("#input_feature1").val(),
						"feature2" : $("#input_feature2").val(),
						"series" : $("#input_series").val(),
						"selectable": $("#selectable input:checked").val(),
						"el_base_type" : $("#input_el_base_type").val(),
						"s_connector_base_type" : $("#input_s_connector_base_type").val(),
						"operate_part_type" : $("#input_operate_part_type").val(),
						"ocular_type" : $("#input_ocular_type").val(),
						"item_code":$("#input_item_code").val(),
						"description":$("#input_description").val(),
//						"imbalance": $("#input_imbalance").val()
						"imbalance_line_id": $("#input_imbalance_line_id").val() && $("#input_imbalance_line_id").val().toString()
					};
					var input_default_pat_id = $("#input_default_pat_id").val();
					if (input_default_pat_id) data.default_pat_id = input_default_pat_id;

					warningConfirm("确认要修改记录吗？", function() {
						// Ajax提交
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doupdate',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : update_handleComplete
						})
					}, function() {
						$("#editbutton").enable();
					}, "确定修改");
				};
			});
            
            //change_category();
			type_list(resInfo.responseList);
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

/** 
 * 检索处理
 */
var findit = function() {
	// 读取已记录检索条件提交给后台
	var data = {
		"id" : $("#cond_id").data("post"),
		"name" : $("#cond_name").data("post"),
		"category_id" : $("#cond_category_id").data("post"),
		"feature1" : $("#cond_feature1").data("post"),
		"feature2" : $("#cond_feature2").data("post"),
		"series" : $("#cond_series").data("post"),
		"el_base_type" : $("#cond_el_base_type").data("post"),
		"s_connector_base_type" : $("#cond_s_connector_base_type").data("post"),
		"operate_part_type" : $("#cond_operate_part_type").data("post"),
		"ocular_type" : $("#cond_ocular_type").data("post"),
		"imbalance_line_id" : $("#cond_imbalance_line_id").data("post")
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};

/**
 * 切到一览画面
 */
var showList = function() {
	top.document.title = modelname + "一览";
	$("#searcharea").show();
	$("#listarea").show();
	$("#editarea").hide();
	$("#selectable_no").attr("checked", "checked").trigger("change");
	$("#imbalance_no").attr("checked", "checked").trigger("change");
	$("#detailarea").hide();
};

/**
 * 切到新建画面
 */
var showAdd = function() {
	// 默认画面变化 s
	top.document.title = "新建" + modelname;
	$("#searcharea").hide();
	$("#listarea").hide();
	$("#title_header").html("新建" + modelname);
	$("#editarea").show();
	$("#editform tr:not(:has(input,textarea,select))").hide();
	$("#editform input[type!='button'][type!='radio'], #editform textarea").val("");
	$("#input_category_id").unbind("change", postCategoryChange);//
	$("#editform select").val("").trigger("change");
	$("#editform label").not("[for]").html("");
	$("#editbutton").val("新建");
	$("#editbutton").enable();
	$(".errorarea-single").removeClass("errorarea-single");
	$("#type").hide();
	// 默认画面变化 e

	// 前台Validate设定
	$("#editform").validate({
		rules : {
			category_id : {
				required : true
			},
			name : {
				required : true
			}
		}
	});
	// 切换按钮效果
	$("#editbutton").unbind("click");
	$("#editbutton").click(function() {
		// 前台Validate
		if ($("#editform").valid()) {
			// 通过Validate,切到新建确认画面
			$("#editbutton").disable();
			// 新建画面输入项提交给后台
			var data = {
				"category_id" : $("#input_category_id").val(),
				"name" : $("#input_name").val(),
				"feature1" : $("#input_feature1").val(),
				"feature2" : $("#input_feature2").val(),
				"series" : $("#input_series").val(),
				"selectable": $("#selectable input:checked").val(),
				"el_base_type" : $("#input_el_base_type").val(),
				"s_connector_base_type" : $("#input_s_connector_base_type").val(),
				"operate_part_type" : $("#input_operate_part_type").val(),
				"ocular_type" : $("#input_ocular_type").val(),
				"item_code":$("#input_item_code").val(),
				"description":$("#input_description").val(),
//				"imbalance": $("#imbalance input:checked").val()
				"imbalance_line_id": $("#input_imbalance_line_id").val() && $("#input_imbalance_line_id").val().toString()
			};
			var input_default_pat_id = $("#input_default_pat_id").val();
			if (input_default_pat_id) data.default_pat_id = input_default_pat_id;

			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=doinsert',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : update_handleComplete
			});
		};
	});
};

/**
 * 切到修改画面
 */
var showEdit = function(rid) {
	$("#type").show();
	// 读取修改行
	var rowData = $("#list").getRowData(rid);
	var data = {
		"id" : rowData.id,
        "category_id":rowData.category_id
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=detail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : showedit_handleComplete
	});
};

/**
 * 切到删除画面
 */
var showDelete = function(rid) {

	// 读取删除行
	var rowData = $("#list").getRowData(rid);
	var data = {
		"id" : rowData.id
	}

	warningConfirm("删除不能恢复。确认要删除["+encodeText(rowData.name)+"]的记录吗？",
		function() {
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : servicePath + '?method=dodelete',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : update_handleComplete
			});
		}, null, "删除确认"
	);
};

var type_list=function(listdata){
	if ($("#gbox_typelist").length > 0) {
		$("#typelist").jqGrid().clearGridData();
		$("#typelist").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#typelist").jqGrid({
			data:listdata,
			height: 70,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','等级','现属梯队','终止维修日','当前设定拉动台数','维修同意总数','操作'],
			colModel:[
				{
					name:'status',
					index:'status',
					hidden:true
				},
				{
					name:'level',
					index:'level',
					width:100,
					align:'center',
					sorttype:'integer',
					formatter:'select',
					editoptions:{value:$("#goMaterial_level").val()}
				},
				{
					name:'echelon',
					index:'echelon',
					width:100,
					align:'center',
					sorttype:'integer',
					formatter:'select',
					editoptions:{value:$("#goEchelon_code").val()}
				},
				{
					name:'avaliable_end_date',
					index:'avaliable_end_date',
					width:100,
					align:'center',
					sorttype:'date',
					formatter:function(cellValue,options,rowData){
						if(cellValue!=null){
							if(cellValue=="9999/12/31"){
								return "";
							}else{
								return cellValue;
							}
						}else{
							return "";
						}
						
					}
				},
				{
					name:'forecast_setting',
					index:'forecast_setting',
					width:100,
					align:'center',
					type:'integer',
					sorttype:'integer'
				},
				{
					name:'agree_count',
					index:'agree_count',
					type:'integer',
					align:'center',
					sorttype:'integer',
					width:100
				},
				{
					name:'operate',
					index:'operate',
					width:100,
					align:'center',
					formatter:function(cellValue,options,rowData){
						var category_id = $("#input_category_id").val();
						var input = "";
						if(category_id=="00000000013"){//单元机种
						}else{
							input = "&nbsp;<a style='text-decoration:none;' href='javascript:change_echelon(" + rowData['level'] + ");'><input type='button' value='改变梯队' class='button_add ui-state-default'/></a>";
						}
						if(rowData.status!=1){
							if(rowData.avaliable_end_date=="9999/12/31"){
								return "<a style='text-decoration:none;' href='javascript:update_end_date(" + rowData['level']+","+ "\"\"" + ");'><input type='button' value='终止' class='button_add ui-state-default'/></a>" + input;
							}else{
								return "<a style='text-decoration:none;' href='javascript:update_avaliable_end_date(" + rowData['level']+","+"\"\"" + ");'><input type='button' value='起效' class='button_add ui-state-default'/></a>"+ input;
							}
						}else{
								return "<a style='text-decoration:none;' href='javascript:addModelLevel(" + rowData['level']+ ");'><input type='button' value='追加' class='button_add ui-state-default'/></a>"+ input;
						}
					}
				}
			],
			rowNum: 17,
			toppager: false,
			pager: null,
			caption : modelname + "受理维修等级设定/废止",
			viewrecords: true,
			rownumbers : true,/*行号*/
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			gridComplete:function(){
				var $trs= $("#typelist").find("tr.ui-widget-content");
				$trs.each(function(){
					var status= $(this).find("td[aria\\-describedby=typelist_status]").text();//追加状态值
					if(status==1){
						$(this).find("td").addClass("overdue_row");
					}
				});
               
               // if($("#hidden_categrory_id").val()!=null && $("#hidden_categrory_id").val()=="00000000013"){//单元机种
                if($("#input_category_id").val()!=null && $("#input_category_id").val()=="00000000013"){
	                $("#typelist").jqGrid('hideCol',"level").trigger("reloadGrid");
                   // $("#typelist").find("td[aria-describedby=\"typelist_echelon\"]").text("单元机种");
                }else{
                    $("#typelist").jqGrid('showCol',"level").trigger("reloadGrid");;
                }
                $("#typelist").jqGrid('setGridWidth', '992');
			}
		});
	}
};

/**
 * 改变梯队
 * @param {} level  等级
 */
var change_echelon = function(level){
	var data = {
		"id" :$("#label_edit_id").text(),
        "category_id":$("#input_category_id").val(),
        "level":level
	};
	
	$("#echelonForm").validate({
		rules:{	
			echelon:{
				required:true
			}
		}
	});
	
	var this_dialog =  $("#change_echelon_dialog").dialog({
		title : "选择梯队",
		width : 500,
		height : 160,
		resizable : false,
		modal : true,
		close : function(){
			$(this).dialog("close");
		},
		buttons : {
			"确定":function(){
				if($("#echelonForm").valid()){
					data.echelon = $("#input_echelon").val();
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : servicePath + '?method=doChangeEchelon',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete :function(xhrobj, textStatus){
							var resInfo = null;
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages("", resInfo.errors);
								} else {
									type_list(resInfo.responseList);
									this_dialog.dialog("close");
								}
							} catch (e) {
								console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
							};
						}
					});
			  	}
			},
			"关闭":function(){ 
				 $(this).dialog("close");
			}
		}
	});
	
	this_dialog.show();
	$("#input_echelon").val("").trigger("change");
};

var update_end_date = function(level,data) {
	$("#update_avaliable_end_date_dialog").html("<form id='edit_date_form'><input type='text' name='edit_date' id='edit_date' alt='终止维修日' value='"+(data=="" ? "" : data)+"'/></form>");
    $("#edit_date_form").validate({
        rules : {
            edit_date : {
                required : true
            }
        }
    });
    
	$("#edit_date").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
	
	$("#update_avaliable_end_date_dialog").dialog({
		title : "选择日期",
		width : 280,
		show: "blind",
		height : 180,// 'auto' ,
		resizable : false,
		modal : true,
		minHeight : 200,
		close : function(){
			$("#update_avaliable_end_date_dialog").html("");
		},
		buttons : {
			"确定":function(){
                if($("#edit_date_form").valid()){
                   var date = $("#edit_date").val();
                   update_avaliable_end_date(level,date,1);
                }
			}, 
			"关闭":function(){ 
				   $(this).dialog("close"); 
			}
		}
	});
};

/**
 * 终止,起效
 * @param {} level 等级
 * @param {} date 日期
 * @param {} flg 终止标记
 */
var update_avaliable_end_date=function(level,date,flg){
	var data={
		"id" :$("#label_edit_id").text(),
		"level":level,
		"avaliable_end_date":date,
        "flg":flg,
        "category_id":$("#input_category_id").val()
	};
    
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doUpdateAvaliableEndDate',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doUpdateAvaliableEndDate_Complete
	});
};

var doUpdateAvaliableEndDate_Complete=function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#editarea", resInfo.errors);
		} else {
			// 重新查询
			$("#editarea").show();
            type_list(resInfo.responseList);
            $("#update_avaliable_end_date_dialog").dialog("close");
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/**
 * 追加
 * @param {} level 等级
 */
var addModelLevel=function(level){
	var data={
		"id" :$("#label_edit_id").text(),
		"level":level,
        "category_id":$("#input_category_id").val()
	};
    
    
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doInsertMoldeLevel',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInsertMoldeLevel_Complete
	});
};

var doInsertMoldeLevel_Complete=function(xhrobj,textStatus){
	var resInfo=null;
	try{
		eval('resInfo='+xhrobj.responseText);
		if(resInfo.errors.length>0){
			// 共通出错信息框
			treatBackMessages("#editarea", resInfo.errors);
		}else{
			// 重新查询
			//$("#searcharea").show();
			//$("#listarea").show();
			$("#editarea").show();
			//findit();
             type_list(resInfo.responseList);
		}
	}catch(e){
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	}
};

/**
 * 维修对象类别change事件
 */
var postCategoryChange = function(){
	var data = {
		"id" : $("#label_edit_id").text(),
		"category_id":$(this).val()
	};
    $.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath + '?method=change',
        cache : false,
        data : data,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : change_Complete
    });
};
var change_category = function(){
	$("#input_category_id").unbind("change", postCategoryChange);
	$("#input_category_id").bind("change", postCategoryChange);
};

var change_Complete=function(xhrobj,textStatus){
    var resInfo=null;
    try{
        eval('resInfo='+xhrobj.responseText);
        if(resInfo.errors.length>0){
            // 共通出错信息框
            treatBackMessages("#editarea", resInfo.errors);
        }else{
             type_list(resInfo.responseList);
             $("#editarea").show();
        }
    }catch(e){
        console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    }
};
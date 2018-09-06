var current_tab = "page_drying_oven_device_tab";
var showDelete = function(rid){
	if(current_tab == "page_drying_oven_device_tab"){
		drying_oven_device_action.del(rid);
	}else{
		drying_job_action.del(rid);
	}
};

/**
 * 初始话页面元素
 */
function init(){
	$("input.ui-button").button();
	$("div.ui-buttonset").buttonset();
	
	//默认选中烘箱管理
	$("#page_drying_oven_device_tab").attr("checked","checked").trigger("change");
	dryingOvenDeviceManage();
	dryingJob();
	
	$("#tabs input:radio").click(function(e){
		var target =  e.target.id;
		current_tab = target;
		if(target == "page_drying_oven_device_tab"){
			$("#page_drying_oven_device").show();
			$("#page_drying_job").hide();
			$("title").html("烘干设备管理");
			
			$("#drying_oven_device_init").show();
			$("#drying_oven_device_add,#drying_oven_device_update").hide();
		}else if(target == "page_drying_job_tab"){
			$("#page_drying_oven_device").hide();
			$("#page_drying_job").show();
			$("title").html("烘干作业");
			
			$("#page_drying_job_init").show();
			$("#page_drying_job_add,#page_drying_job_update").hide();
		}
	});
};

var drying_oven_device_action = {
	servicePath : "drying_oven_device.do",
	
	reset:function(){
		$("#search_m_manage_code,#hidden_search_m_device_manage_id").val("");
		$("#search_m_setting_temperature").val("").trigger("change");
	},
	
	findit : function() {
		var searchData = {
			"device_manage_id" : $("#hidden_search_m_device_manage_id").val(),
			"setting_temperature" : $("#search_m_setting_temperature").val()
		};

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : drying_oven_device_action.servicePath + '?method=search',
			cache : false,
			data : searchData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : drying_oven_device_action.doInit_ajaxSuccess
		});
	},
	
	doInit_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				drying_oven_device_action.show_list(resInfo.finished);
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	show_list : function(list) {
		if ($("#gbox_drying_oven_device_list").length > 0) {
			// jqGrid已构建的情况下,重载数据并刷新
			$("#drying_oven_device_list").jqGrid().clearGridData();
			$("#drying_oven_device_list").jqGrid('setGridParam', {data : list}).trigger("reloadGrid", [{current : false}]);
		} else {
			$("#drying_oven_device_list").jqGrid({
				data : list,
				height : 461,
				width : 992,
				rowheight : 23,
				datatype : "local",
				colNames : ['','管理编号ID', '管理编号', '型号名称','设定温度', '实测温度下限', '实测温度上限','库位数'],
				colModel : [
					{name:'myac', width:30, fixed:true, sortable:false, resize:false,formatter:'actions',formatoptions:{keys:true, editbutton:false}},
					{name:'device_manage_id',index :'device_manage_id',hidden:true},
					{name:'manage_code',index :'manage_code',width :160},
					{name:'model_name',index :'model_name',width :160},
					{name:'setting_temperature',index : 'setting_temperature',width:160,formatter:'select', editoptions:{value:$("#hidden_sDryingOvenSettingTemperature").val()}},
					{name:'lower_limit',index :'lower_limit',align:'right'},
				    {name:'upper_limit',index :'upper_limit',align:'right'}, 
				    {name:'slot',index:'slot',align:'right'}
				],
				rowNum : 35,
				rownumbers:true,
				toppager : false,
				pager : "#drying_oven_device_list_pager",
				viewrecords : true,
				gridview : true,
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				hidegrid : false,
				deselectAfterSort : false,
				ondblClickRow : drying_oven_device_action.showDryingOvenDeviceEdit,
				viewsortcols : [true, 'vertical', true]
			});
			$("#gbox_drying_oven_device_list .ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
				'<input type="button" class="ui-button" id="add_drying_oven_device_button" value="新建烘干设备管理" aria-disabled="false">' +
			'</div>');
			$("#add_drying_oven_device_button").button();
			
			//新建烘箱
			$("#add_drying_oven_device_button").unbind("click");
			$("#add_drying_oven_device_button").click(function(){
				$("#add_m_form input,#add_m_form select").removeClass("valid errorarea-single");
				$("#add_m_manage_code,#hidden_add_m_device_manage_id,#add_m_slot").val("");
				$("#add_m_setting_temperature").val("").trigger("change");
				
				$("#drying_oven_device_add").show();
				$("#drying_oven_device_init").hide();
			});
		}
	},
	
	add : function(){
		$("#add_m_form").validate({
			rules:{	
				device_manage_id:{
				   required:true
			   },
			   setting_temperature:{
				   required:true,
				   digits:true
			   },
			   slot:{
			   	   required:true,
				   digits:true,
				   range:[1,99]
			   }
			}
	     });
		
		if( $("#add_m_form").valid()) {
			var data = {
				"device_manage_id" : $("#hidden_add_m_device_manage_id").val(),
				"manage_code" : $("#add_m_manage_code").val(),
				"setting_temperature" : $("#add_m_setting_temperature").val(),
				"slot" : $("#add_m_slot").val()
			};
			
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : drying_oven_device_action.servicePath + '?method=doInsert',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : drying_oven_device_action.doInsert_ajaxSuccess
			});
		}
	},
	
	doInsert_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#confirmmessage").text("新建已经完成。");
	            $("#confirmmessage").dialog({
	               width : 320,
	               height : 'auto',
	               resizable : false,
	               show : "blind",
	               modal : true,
	               title : "新建",
	               buttons : {
	                   "关闭" : function() {                                 
	                       $(this).dialog("close");
	                       drying_oven_device_action.findit();
	                       $("#drying_oven_device_init").show();
						   $("#drying_oven_device_add").hide();
	                   }
	               }
	           });
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	showDryingOvenDeviceEdit : function(){
		var rowID = $("#drying_oven_device_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#drying_oven_device_list").getRowData(rowID);
		
		$("#update_m_form input,#update_m_form select").removeClass("valid errorarea-single");
		
		$("#label_m_manage_code").text(rowData.manage_code);//管理编号
		$("#label_m_model_name").text(rowData.model_name);//型号名称
		$("#update_m_setting_temperature").val(rowData.setting_temperature).trigger("change");//设定温度
		$("#update_m_slot").val(rowData.slot);//库位数
		
		$("#drying_oven_device_update").show();
		$("#drying_oven_device_init").hide();
	},
	
	update:function(){
		var rowID = $("#drying_oven_device_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#drying_oven_device_list").getRowData(rowID);
		
		$("#update_m_form").validate({
			rules:{	
			   setting_temperature:{
				   required:true,
				   digits:true
			   },
			   slot:{
			   	   required:true,
				   digits:true,
				   range:[1,99]
			   }
			}
	     });
		
		if( $("#update_m_form").valid()) {
			var data = {
				"device_manage_id" : rowData.device_manage_id,
				"setting_temperature" : $("#update_m_setting_temperature").val(),
				"slot" : $("#update_m_slot").val()
			};
			
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : drying_oven_device_action.servicePath + '?method=doUpdate',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : drying_oven_device_action.doUpdate_ajaxSuccess
			});
		}
	},
	
	doUpdate_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#confirmmessage").text("更新已经完成。");
	            $("#confirmmessage").dialog({
	               width : 320,
	               height : 'auto',
	               resizable : false,
	               show : "blind",
	               modal : true,
	               title : "更新",
	               buttons : {
	                   "关闭" : function() {                                 
	                       $(this).dialog("close");
	                       drying_oven_device_action.findit();
	                       $("#drying_oven_device_init").show();
						   $("#drying_oven_device_update").hide();
	                   }
	               }
	           });
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	del : function(rid){
		var rowData = $("#drying_oven_device_list").getRowData(rid);
		var data = {
			"device_manage_id" : rowData.device_manage_id
	    };
	    $("#confirmmessage").text("删除不能恢复。确认要删除设备管理编号:"+encodeText(rowData.manage_code)+"的记录吗？");
	    
	    $("#confirmmessage").dialog({
			resizable : false,
			modal : true,
			title : "删除确认",
			buttons : {
				"确认" : function() {
					$(this).dialog("close");
					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : drying_oven_device_action.servicePath + '?method=doDelete',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : drying_oven_device_action.delete_handleComplete
					});
				},
				"取消" : function() {
					$(this).dialog("close");
				}
			}
		});
	},
	
	delete_handleComplete : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
	            drying_oven_device_action.findit();
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	}
};

//烘箱管理
function dryingOvenDeviceManage(){
	$("#search_m_setting_temperature,#add_m_setting_temperature,#update_m_setting_temperature").select2Buttons();

	/*管理编号*/
	setReferChooser($("#hidden_search_m_device_manage_id"),$("#search_m_device_manage_id_referchooser"));
	setReferChooser($("#hidden_add_m_device_manage_id"),$("#add_m_device_manage_id_referchooser"));
	
	/*清除*/
	$("#reset_m_button").click(function() {
		drying_oven_device_action.reset();
	});
	
	//检索
	$("#search_m_button").click(function() {
		drying_oven_device_action.findit();
	});
	
	//新建取消
	$("#add_m_resetbutton").click(function(){
		$("#drying_oven_device_init").show();
		$("#drying_oven_device_add").hide();
	});
	
	//新建确认
	$("#add_m_comfirmbutton").click(function(){
		drying_oven_device_action.add();
	});
	
	//更新取消
	$("#update_m_resetbutton").click(function(){
		$("#drying_oven_device_init").show();
		$("#drying_oven_device_update").hide();
	});
	
	//更新确认
	$("#update_m_comfirmbutton").click(function(){
		drying_oven_device_action.update();
	});
	
	//$("#search_m_button").trigger("click");
	drying_oven_device_action.findit();
};

//烘干作业
function dryingJob(){
	/*硬化条件*/
	$("#search_j_hardening_condition,#add_j_hardening_condition,#add_j_category_id,#update_j_category_id").select2Buttons();
	
	$("#update_j_hardening_condition").html($("#search_j_hardening_condition").html()).find("option").first().remove();
	$("#update_j_hardening_condition").select2Buttons();
	
	/*工位*/
	setReferChooser($("#hidden_search_j_position_id"),$("#search_j_position_id_referchooser"));
	setReferChooser($("#hidden_add_j_position_id"),$("#add_j_position_id_referchooser"));
	setReferChooser($("#hidden_update_j_position_id"),$("#update_j_position_id_referchooser"));
	
	
	/*清除*/
	$("#reset_j_button").click(function() {
		drying_job_action.reset();
	});
	
	//检索
	$("#search_j_button").click(function() {
		drying_job_action.findit();
	});
	
	//新建取消
	$("#add_j_resetbutton").click(function(){
		$("#page_drying_job_init").show();
		$("#page_drying_job_add").hide();
	});
	
	//新建确认
	$("#add_j_comfirmbutton").click(function(){
		drying_job_action.add();
	});
	
	//更新确认
	$("#update_j_comfirmbutton").click(function(){
		drying_job_action.update();
	});
	
	//更新取消
	$("#update_j_resetbutton").click(function(){
		$("#page_drying_job_update").hide();
		$("#page_drying_job_init").show();
	});
	
	//硬化条件change事件
	$("#add_j_hardening_condition").change(function(){
		var value = this.value;
		if(value == "0"){
			$("#add_j_form tr.appoint").hide();
		}else{
			$("#add_j_form tr.appoint").show();
		}
	});
	
	
	//硬化条件change事件
	$("#update_j_hardening_condition").change(function(){
		var value = this.value;
		if(value == "0"){
			$("#update_j_form tr.appoint").hide();
		}else{
			$("#update_j_form tr.appoint").show();
		}
	});
	
	$("#add_j_form input:radio").click(function(){
		if($("#add_j_no_appoint").attr("checked")=="checked"){
			$("#s2b_add_j_slots").hide();
		}else{
			$("#s2b_add_j_slots").show();
		}
	});
	
	$("#update_j_form input:radio").click(function(){
		if($("#update_j_no_appoint").attr("checked")=="checked"){
			$("#s2b_update_j_slots").hide();
		}else{
			$("#s2b_update_j_slots").show();
		}
	});
	
	
	drying_job_action.findit();
};

var drying_job_action = {
	servicePath : "drying_job.do",
	
	reset : function(){
		$("#search_j_content,#search_j_position_name,#hidden_search_j_position_id").val("");
		$("#search_j_hardening_condition").val("").trigger("change");
	},
	
	findit : function() {
		var searchData = {
			"content" : $("#search_j_content").val(),
			"hardening_condition" : $("#search_j_hardening_condition").val(),
			"position_id" : $("#hidden_search_j_position_id").val()
		};

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : drying_job_action.servicePath + '?method=search',
			cache : false,
			data : searchData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : drying_job_action.doInit_ajaxSuccess
		});
	},
	
	doInit_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				drying_job_action.show_list(resInfo.finished);
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	show_list : function(list) {
		if ($("#gbox_drying_job_list").length > 0) {
			// jqGrid已构建的情况下,重载数据并刷新
			$("#drying_job_list").jqGrid().clearGridData();
			$("#drying_job_list").jqGrid('setGridParam', {data : list}).trigger("reloadGrid", [{current : false}]);
		} else {
			$("#drying_job_list").jqGrid({
				data : list,
				height : 461,
				width : 992,
				rowheight : 23,
				datatype : "local",
				colNames : ['','烘干作业 ID', '工位代码', '管理编号','设备名称','型号','作业内容','干燥时间','硬化条件','指定库位','机种'],
				colModel : [
					{name:'myac', width:30, fixed:true, sortable:false, resize:false,formatter:'actions',formatoptions:{keys:true, editbutton:false}},
					{name:'drying_job_id',index :'drying_job_id',hidden:true},
					{name:'process_code',index :'process_code',width :60,align:'center'},
					{name:'manage_code',index :'manage_code',width :60},
					{name:'device_name',index :'device_name',width :60},
				    {name:'model_name',index:'model_name',width :60},
					{name:'content',index :'content',width :220},
					{name:'drying_time',index :'drying_time',width :50,sorttype:'currency',formatter:'currency',formatoptions:{decimalPlaces:0,suffix:'分钟'}},
					{name:'hardening_condition',index : 'hardening_condition',width:60,formatter:'select', editoptions:{value:$("#hidden_sDryingHardeningCondition").val()}},
				    {name:'slots',index:'slots',width:120},
				    {name:'category_name',index:'category_name',width:100}
				],
				rowNum : 35,
				rownumbers:true,
				toppager : false,
				pager : "#drying_job_pager",
				viewrecords : true,
				gridview : true,
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				hidegrid : false,
				deselectAfterSort : false,
				ondblClickRow : drying_job_action.showDryingJobEdit,
				viewsortcols : [true, 'vertical', true]
			});
			$("#gbox_drying_job_list .ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
				'<input type="button" class="ui-button" id="add_ddrying_job_button" value="新建烘干作业" aria-disabled="false">' +
			'</div>');
			$("#add_ddrying_job_button").button();
			
			//新建烘箱
			$("#add_ddrying_job_button").unbind("click");
			$("#add_ddrying_job_button").click(function(){
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : drying_job_action.servicePath + '?method=searchDryingOvenDevice',
					cache : false,
					data : null,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : drying_job_action.searchDryingOvenDevice_ajaxSuccess
				});
			});
		}
	},
	
	searchDryingOvenDevice_ajaxSuccess :function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#add_j_device_manage_id_referchooser").find("table").eq(1).html(resInfo.dryingOvenDeviceReferChooser);
				//使用设备
				setReferChooser($("#hidden_add_j_device_manage_id"),$("#add_j_device_manage_id_referchooser"),null,drying_job_action.get_add_j_slots);
				
				$("#add_j_form table input").removeClass("valid errorarea-single").val("");
				$("#add_j_hardening_condition").val("").trigger("change");//硬化条件
				$("#add_j_no_appoint").attr("checked",true).trigger("change");//不指定库位
				
				$("#add_j_slots").hide().html("");
				$("#s2b_add_j_slots").remove();

				if($("#add_j_category_id").val()!=null && $("#add_j_category_id").val().toString()!=""){
					$("#add_j_category_id").val("").trigger("change");//机种
				}
				
				$("#page_drying_job_add").show();
				$("#page_drying_job_init").hide();
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	get_add_j_slots : function(){
		var device_manage_id = $("#hidden_add_j_device_manage_id").val();
		var $tds = $("#add_j_device_manage_id_referchooser").find("table").eq(1).find("td");
		
		$tds.each(function(index,ele){
			var $td = $(ele);
			if($td.text().trim() == device_manage_id){
				//库位数
				var solts = $td.parent().find("td").last().text().trim().split(":")[1];
				solts = parseInt(solts);
				
				var content = "";
				for(var i = 1;i <= solts;i++){
					content += "<option value=" + i +">" + i + "</option>";
				}
				
				$("#s2b_add_j_slots").remove();
				$("#add_j_slots").hide().html(content).select2Buttons();
				
				if($("#add_j_no_appoint").attr("checked")=="checked"){
					$("#s2b_add_j_slots").hide();
				}else{
					$("#s2b_add_j_slots").show();
				}
				
				return;
			}
		});
	},
	
	add :function(){
		$("#add_j_form").validate({
			rules:{	
			   position_id:{
				   required:true
			   },
			   content:{
				   required:true,
				   maxlength:20
			   },
			   
			   drying_time:{
				   required:true,
				   digits:true,
				   max:999
			   },
			   hardening_condition:{
			   	   required:true
			   }
			}
	     });
		
		if( $("#add_j_form").valid()) {
	        var slots = null;
	        if($("#add_j_appoint").attr("checked")=="checked"){
	        	slots = $("#add_j_slots").val().toString();
	        }
	        
			var data = {
				"position_id" : $("#hidden_add_j_position_id").val(),
				"content" : $("#add_j_content").val(),
				"drying_time" : $("#add_j_drying_time").val(),
				"hardening_condition" : $("#add_j_hardening_condition").val(),
				"device_manage_id" : $("#hidden_add_j_device_manage_id").val(),
				"slots" : slots
			};
			
			var category_ids = $("#add_j_category_id").val();
			for (var i in category_ids) {
				if(category_ids[i]!=null && category_ids[i]!=""){
					data["drying_job_of_category.category_id[" + i + "]"] = category_ids[i];
				}
			}
			
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : drying_job_action.servicePath + '?method=doInsert',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : drying_job_action.doInsert_ajaxSuccess
			});
		}
	},
	
	doInsert_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#confirmmessage").text("新建已经完成。");
	            $("#confirmmessage").dialog({
	               width : 320,
	               height : 'auto',
	               resizable : false,
	               show : "blind",
	               modal : true,
	               title : "新建",
	               buttons : {
	                   "关闭" : function() {                                 
	                       $(this).dialog("close");
	                       drying_job_action.findit();
	                      	$("#page_drying_job_add").hide();
							$("#page_drying_job_init").show();
	                   }
	               }
	           });
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	showDryingJobEdit:function(){
		var rowID = $("#drying_job_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#drying_job_list").getRowData(rowID);
		
		var data = {"drying_job_id" : rowData.drying_job_id};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : drying_job_action.servicePath + '?method=getDetail',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : drying_job_action.getDetail_ajaxSuccess
		});
	},
	
	getDetail_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#update_j_form input").removeClass("valid errorarea-single");
				
				var dryingJobDetail = resInfo.dryingJobDetail
				
				//工位
				$("#hidden_update_j_position_id").val(dryingJobDetail.position_id);
				$("#update_j_position_name").val(dryingJobDetail.position_name);
				//作业内容
				$("#update_j_content").val(dryingJobDetail.content);
				//干燥时间
				$("#update_j_drying_time").val(dryingJobDetail.drying_time);
				//硬化条件
				var hardening_condition = dryingJobDetail.hardening_condition
				$("#update_j_hardening_condition").val(hardening_condition).trigger("change");
				
				if(hardening_condition == "0"){
					$("#update_j_form tr.appoint").hide();
				}else{
					$("#update_j_form tr.appoint").show();
				}
				//使用设备
				$("#update_j_device_manage_id_referchooser").find("table").eq(1).html(resInfo.dryingOvenDeviceReferChooser);
				setReferChooser($("#hidden_update_j_device_manage_id"),$("#update_j_device_manage_id_referchooser"),null,drying_job_action.get_update_j_slots);
				$("#hidden_update_j_device_manage_id").val(dryingJobDetail.device_manage_id);
				$("#update_j_device_manage_code").val(dryingJobDetail.manage_code);
				
				//指定库位
				var slots = dryingJobDetail.slots;
				//库位数
				var slot = dryingJobDetail.slot;
				slot = parseInt(slot);
				var content = "";
				for(var i = 1;i <= slot;i++){
					content += "<option value=" + i +">" + i + "</option>";
				}
				$("#s2b_update_j_slots").remove();
				$("#update_j_slots").html(content).select2Buttons().hide();
				
				if(slots == null || slots == ""){//不指定
					$("#update_j_no_appoint").attr("checked","checked").trigger("change");
					$("#s2b_update_j_slots").hide();
				}else{
					$("#update_j_appoint").attr("checked","checked").trigger("change");
					$("#s2b_update_j_slots").show();
					
					var arraySlots = slots.split(",");
					for(var index = 0; index < arraySlots.length;index ++){
						$("#update_j_slots option[value='" + arraySlots[index] + "']").attr("selected","selected").trigger("change");
					}
				}
				
				$("#update_j_category_id option").attr("selected",false).trigger("change");
				var categorys = resInfo.categorys;
				for(var i in categorys){
					var category_id = categorys[i].category_id;
					$("#update_j_category_id option[value='" + category_id + "']").attr("selected","selected").trigger("change");
				}
				
				$("#page_drying_job_init").hide();
				$("#page_drying_job_update").show();
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	get_update_j_slots : function(){
		var device_manage_id = $("#hidden_update_j_device_manage_id").val();
		var $tds = $("#update_j_device_manage_id_referchooser").find("table").eq(1).find("td");
		
		$tds.each(function(index,ele){
			var $td = $(ele);
			if($td.text().trim() == device_manage_id){
				//库位数
				var solts = $td.parent().find("td").last().text().trim().split(":")[1];
				solts = parseInt(solts);
				
				var content = "";
				for(var i = 1;i <= solts;i++){
					content += "<option value=" + i +">" + i + "</option>";
				}
				
				$("#s2b_update_j_slots").remove();
				$("#update_j_slots").hide().html(content).select2Buttons();
				
				if($("#update_j_no_appoint").attr("checked")=="checked"){
					$("#s2b_update_j_slots").hide();
				}else{
					$("#s2b_update_j_slots").show();
				}
				
				return;
			}
		});
	},
	
	update : function(){
		var rowID = $("#drying_job_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#drying_job_list").getRowData(rowID);
		
		$("#update_j_form").validate({
			rules:{	
			   position_id:{
				   required:true
			   },
			   content:{
				   required:true,
				   maxlength:20
			   },
			   
			   drying_time:{
				   required:true,
				   digits:true,
				   max:999
			   }
			}
	     });
	     
	     if( $("#update_j_form").valid()) {
	        var slots = null;
	        if($("#update_j_appoint").attr("checked")=="checked"){
	        	slots = $("#update_j_slots").val().toString();
	        }
	        
			var data = {
				"drying_job_id" : rowData.drying_job_id,
				"position_id" : $("#hidden_update_j_position_id").val(),
				"content" : $("#update_j_content").val(),
				"drying_time" : $("#update_j_drying_time").val(),
				"hardening_condition" : $("#update_j_hardening_condition").val(),
				"device_manage_id" : $("#hidden_update_j_device_manage_id").val(),
				"slots" : slots
			};
			
			var category_ids = $("#update_j_category_id").val();
			for (var i in category_ids) {
				if(category_ids[i]!=null && category_ids[i]!=""){
					data["drying_job_of_category.category_id[" + i + "]"] = category_ids[i];
				}
			}
			
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : drying_job_action.servicePath + '?method=doUpdate',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : drying_job_action.doUpdate_ajaxSuccess
			});
		}
	},
	
	doUpdate_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#confirmmessage").text("更新已经完成。");
	            $("#confirmmessage").dialog({
	               width : 320,
	               height : 'auto',
	               resizable : false,
	               show : "blind",
	               modal : true,
	               title : "更新",
	               buttons : {
	                   "关闭" : function() {                                 
	                       $(this).dialog("close");
	                       drying_job_action.findit();
	                       $("#page_drying_job_update").hide();
						   $("#page_drying_job_init").show();
	                   }
	               }
	           });
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	del : function(rid){
		var rowData = $("#drying_job_list").getRowData(rid);
		var data = {
			"drying_job_id" : rowData.drying_job_id
	    };
	    $("#confirmmessage").text("删除不能恢复。确认要删除烘干作业记录吗？");
	    
	    $("#confirmmessage").dialog({
			resizable : false,
			modal : true,
			title : "删除确认",
			buttons : {
				"确认" : function() {
					$(this).dialog("close");
					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : drying_job_action.servicePath + '?method=doDelete',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : drying_job_action.delete_handleComplete
					});
				},
				"取消" : function() {
					$(this).dialog("close");
				}
			}
		});
	},
	
	delete_handleComplete : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
	            drying_job_action.findit();
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	}
}

$(function() {
	init();
});
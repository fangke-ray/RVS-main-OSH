/** 模块名 */
var modelname = "零件";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "partial.do";
$(function() {
    $("input.ui-button").button();
    
    /*初始化隐藏新建页面*/
    $("#editarea").hide();
    
    /*改废增按钮enable、disable*/
    enableButton();
    
    /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
    $("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});  
    
    /*取消按钮事件 + 新建页面的折叠按钮△▲--返回初始检索画面*/
    $("#cancelbutton, #editarea span.ui-icon").click(showList);
    
    /*radio-->button*/
    $("#edit_order_flg,#operation_id,#search_consumable_flg,#edit_unpack").buttonset();
	$("#searchbutton").addClass("ui-button-primary");
	
	$("#edit_spec_kind option:eq(0)").remove();
	$("#search_spec_kind,#edit_spec_kind").select2Buttons();
	
	/*初始化页面*/
	 findit();
	$("#searchbutton").click(function(){
		$("#search_code").data("post", $("#search_code").val());
		$("#search_name").data("post", $("#search_name").val());
		findit();
	});
	
	/*检索button事件*/
	$("#resetbutton").click(function(){
		$("#search_code").val("").data("post", "");
		$("#search_name").val("").data("post", "");
		$("#search_consumable_flg_all").attr("checked","checked").trigger("change");
		$("#search_spec_kind").val("").trigger("change");
	});
	
	/*改废增button*/
	$("#waste_revision_button").click(waste_revision_edit);
	
	/*零件入库工时标准*/
	$("#standard_button").click(bussiness_standard);
	$("#edit_unpack input[type='radio']").click(function(){
		if(this.value == 1){
			$("#edit_split_quantity").val("").closest("tr").show();
		}else{
			$("#edit_split_quantity").val("").closest("tr").hide();
		}
	});
	
	$("#upload_price_button").click(function(){
		$("#upload_price_dialog").dialog({
		    resizable : false,
			modal : true,
			title : "零件价格",
			width : 640,
			buttons : {
				"确认" : function() {
					$.ajaxFileUpload({
						url : servicePath + '?method=doUploadPrice', // 需要链接到服务器地址
						secureuri : false,
						fileElementId : 'uploadFile', // 文件选择框的id属性
						dataType : 'json', // 服务器返回的格式
						success : function(responseText, textStatus) {
							var resInfo = null;
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + responseText);
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages("", resInfo.errors);
								} else {
									$("#upload_price_dialog").dialog("close");
									$("#uploadFile").val("");
									infoPop("上传成功!",null);
								}
							} catch(e) {
							}
						}
					});
				},
				"取消" : function() {
					$(this).dialog("close");
				}
			}
		});
	});
	
	$("#download_price_button").click(function(){
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=reportPrice',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhjObject) {
				var resInfo = null;
				eval("resInfo=" + xhjObject.responseText);
				if (resInfo && resInfo.fileName) {
					if ($("iframe").length > 0) {
						$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
					} else {
						var iframe = document.createElement("iframe");
						iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
						iframe.style.display = "none";
						document.body.appendChild(iframe);
					}
				} else {
					errorPop("文件导出失败！");
				}
			}
		});
	});
});


/*零件入库工时标准*/
function bussiness_standard(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'partial_bussiness_standard.do?method=search',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_BussinessStandardComplete
	});
};

var search_BussinessStandardComplete = function(xhrobj,textStatus){
	var resInfo = null;
	try {
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			//零件出入库工时标准
			let list = resInfo.finished;
			
			let content = "";
			
			//记录每个规格种别出入库工时标准(key:规格种别)
			let map = new Map();

			list.forEach((standard,index)=>{
				let spec_kind = standard.spec_kind,//规格种别
					spec_kind_name = standard.spec_kind_name,//规格种别名称
					box_count = standard.box_count,//装箱数量
					recept = standard.recept,//收货
					collect_case = standard.collect_case,//拆盒
					collation_on_shelf = standard.collation_on_shelf,//核对上架
					off_shelf = standard.off_shelf,//下架
					unpack = standard.unpack || "";//分装
				
				map.set(spec_kind,{box_count,recept,collect_case,collation_on_shelf,off_shelf,unpack});
					
				content += `<tr spec_kind="${spec_kind}">
						  		<td class="ui-state-default td-title">${spec_kind_name}</td>
						  		<td class="td-content">
							  		<input type="checkbox" class="box-count-input" id="box_count_input${index}"/>
							  		<label for="box_count_input${index}">每批一箱</label>
							  		<input type="text" class="ui-widget-content box-count" style="margin-left:2px;"/>
						  		</td>
						  		<td class="td-content">
						  			<input type="text" class="ui-widget-content recept"/>
						  		</td>
						  		<td class="td-content">
					  				<input type="text" class="ui-widget-content collect-case"/>
					  			</td>
						  		<td class="td-content">
						  			<input type="text" class="ui-widget-content collation-on-shelf"/>
						  		</td>
						  		<td class="td-content">
							  		<input type="text" class="ui-widget-content off-shelf"/>
						  		</td>
						  		<td class="td-content">
						  			<input type="text" class="ui-widget-content unpack"/>
						  		</td>
						    </tr>`;
			});

			//生成table
			$("#standard_dialog tbody").empty().append(content);

			//初始赋值
			$("#standard_dialog tbody tr").each((index,tr) => {
				let $tr = $(tr);
				let spec_kind = $tr.attr("spec_kind");
				
				//零件出入库工时标准
				let obj = map.get(spec_kind);
				
				//装箱数量
				let box_count = obj.box_count;
				if(box_count == -1){//每批一箱
					$tr.find(".box-count").val(box_count).addClass("hidden");
					$tr.find(".box-count-input").prop("checked",true).trigger("change");
				}else{
					$tr.find(".box-count").val(box_count);
				}
				
				//收货
				$tr.find(".recept").val(obj.recept);
				
				//拆盒
				$tr.find(".collect-case").val(obj.collect_case);
				
				//核对上架
				$tr.find(".collation-on-shelf").val(obj.collation_on_shelf);
				
				//下架
				$tr.find(".off-shelf").val(obj.off_shelf);
				
				//分装
				$tr.find(".unpack").val(obj.unpack);
			});
			
			$("#standard_dialog .td-content").buttonset();
			
			//每批一箱事件
			$(".box-count-input").click(function(){
				let $input = $(this).nextAll("[type='text']");
				this.checked == true ? $input.addClass("hidden").val("-1") : $input.removeClass("hidden").val("");
			});
			
			$("#standard_dialog").dialog({
				resizable : false,
				modal : true,
				title : "零件入出库工时标准",
				width : 1000,
				buttons : {
					"确认" : function() {
						let data = {};
						$(this).find("tbody tr").each(function(index,tr){
							let $tr = $(tr);
							data["partial_bussiness_standard.spec_kind[" + index + "]"] = $tr.attr("spec_kind");
							data["partial_bussiness_standard.box_count[" + index + "]"] = $tr.find(".box-count").val().trim();
							data["partial_bussiness_standard.collect_case[" + index + "]"] = $tr.find(".collect-case").val().trim();
							
							if($tr.find(".box-count-input").prop("checked") == true){
								data["partial_bussiness_standard.box_count_flg[" + index + "]"] = "1";
							}else{
								data["partial_bussiness_standard.box_count_flg[" + index + "]"] = "0";
							}
							
							data["partial_bussiness_standard.recept[" + index + "]"] = $tr.find(".recept").val().trim();
							data["partial_bussiness_standard.collation_on_shelf[" + index + "]"] = $tr.find(".collation-on-shelf").val().trim();
							data["partial_bussiness_standard.off_shelf[" + index + "]"] = $tr.find(".off-shelf").val().trim();
							data["partial_bussiness_standard.unpack[" + index + "]"] = $tr.find(".unpack").val().trim();
						});
						
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : 'partial_bussiness_standard.do?method=doUpdate',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete :update_BussinessStandardComplete
						});
					},
					"取消" : function() {
						$(this).dialog("close");
					}
				}
			});
			
		}
	}catch(e){}
};

var update_BussinessStandardComplete = function(xhrobj,textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#standard_dialog").dialog("close");
			infoPop("零件入出库工时标准更新完成！",null,"提示信息")
		}
	}catch(e){}
};

/*判断改废增按钮enable、disable(当选择了零件之后enable；否则是disable)*/
var enableButton= function(){
	//选择行，并获取行数
	var row = $("#list").jqGrid("getGridParam", "selrow");
	
	if(row>0){
		$("#waste_revision_button").enable();
	}else{
		$("#waste_revision_button").disable();
	}   
}
/*废改订button事件*/
var waste_revision_edit = function(){
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
	var rowData = $("#list").getRowData(row);
	var data = {
			"partial_id": rowData.partial_id
			};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=edit',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : show_adit_Complete
	})
}
/*废改订加载页面事件*/
var show_adit_Complete = function(xhrobj,textStatus){
  var resInfo = null;
	try {
		// 以Object形式读取JSON
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		/*消除上一次改废增操作的有效时间和改动零件的数据再一次加载到本次操作*/
		$("#edit_effective_time,#edit_change_time,#search_partial_id").val("");
		
		/*每点击改废增button,都会再一次将型号的select进行创建(为了消除上一点击改废增的操作数据对下次操作的影响)*/
		$("#select_model_name").next("div").remove();	
		
		$("#edit_effective_time,#edit_change_time").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
	    });
		
		$("#select_model_name").html(resInfo.smodelNameOptions).select2Buttons();
		$("#edit_label_code").text(resInfo.returnForm.code);
		$("#edit_label_name").text(resInfo.returnForm.name);
		$("#edit_partial_id").val(resInfo.returnForm.partial_id);		
		
		//操作默认被选中废止
		$("#edit_choose_discontinue").attr("checked",function(){	
			$(".discontinue_edit").show();
			$(".decided_edit").hide();
		});
		
	    //废止radio button
		$("#edit_choose_discontinue").click(function(){	
			$(".discontinue_edit").show();
			$(".decided_edit").hide();
			
		})
		//改定radio button
		$("#edit_decided").click(function(){
			$(".discontinue_edit").hide();
			$(".decided_edit").show();
		});	
		
		// autoComplete(零件code) 
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=getAutocomplete',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					partialCode = resInfo.sPartialCode;
					$("#search_partial_id").autocomplete({
						source : partialCode,
						minLength :3,
						delay : 100,
						focus: function( event, ui ) {
							 $(this).val( ui.item.label );
							 return false;
						},
						select: function( event, ui ) {
							$("#edit_new_partial_id").val(ui.item.value);
							$(this).val( ui.item.label );
							 return false;
						}
					});				
				} catch (e) {
				}
			}
		});		
		
	   /*验证*/
	   $("#abandon_modify").validate({
			rules:{	
				history_limit_date:{
				   required:true
			   },
			   model_id:{
				   required:true
			   },
			   code:{
				   required:true
			   }
			}
	     });	 
			 $("#abandon_modify").dialog({
				    resizable : false,
					modal : true,
					title : "改废增",
					width : 640,
					buttons : {
						"确认" : function() {
							if($("#edit_choose_discontinue").attr("checked")){
								if( $("#abandon_modify").valid()) {
									$("#confirmmessage").text("废止已经完成！");
									$("#confirmmessage").dialog({
										width : 320,
										height : 'auto',
										resizable : false,
										show : "blind",
										modal : true,
										title : "改废增",
										buttons : {
											"关闭" : function() {
												$("#confirmmessage").dialog("close");
											}
										}
									});
									$(this).dialog("close");
									update_effective_time();
								}
							}
							if($("#edit_decided").attr("checked")){
								if( $("#abandon_modify").valid()) {
									$("#confirmmessage").text("改定已经完成！");
									$("#confirmmessage").dialog({
										width : 320,
										height : 'auto',
										resizable : false,
										show : "blind",
										modal : true,
										title : "改废增",
										buttons : {
											"关闭" : function() {
												$("#confirmmessage").dialog("close");
											}
										}
									});
									$(this).dialog("close");
									update_effective_time_code();
								}
							}
						},
						"取消" : function() {
							$(this).dialog("close");
						}
					}
				});
			 
	}
}catch(e){}   

}
/*更新有效期*/
var update_effective_time = function(){
	var data ={
			"history_limit_date":$("#edit_effective_time").val(),			
			"model_id":$("#select_model_name").val() && $("#select_model_name").val().toString(),
			"partial_id":$("#edit_partial_id").val(),
			"new_partial_id":$("#edit_partial_id").val()
	}
	 $.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doupdateActiveTime',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : new_update_handleComplete
	});
}
/*更新有效期 新建零件*/
var update_effective_time_code = function(){
	var data ={
			"history_limit_date":$("#edit_change_time").val(),
			"model_id":$("#select_model_name").val() && $("#select_model_name").val().toString(),
			"code":$("#search_partial_id").val(),
			"partial_id":$("#edit_partial_id").val(),
			"new_partial_id":$("#edit_new_partial_id").val(),
				"order_flg":1,
				"consumable_flg":1
			// TODO
		}
	 $.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doupdateActiveTimeCode',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : new_update_handleComplete
	});
}

function new_update_handleComplete(xhrobj, textStatus) {

	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
	    findit();
	}
};
/*双击零件-修改页面的jqgrid*/
function show_ext_list(finished){
	if ($("#gbox_ext_list").length > 0) {
		$("#ext_list").jqGrid().clearGridData();
		$("#ext_list").jqGrid('setGridParam',{data:finished}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#ext_list").jqGrid({
			data:finished,
			width: 505,
			rowheight: 23,
			rownumbers : true,
			datatype: "local",
			colNames:['零件ID','使用该零件型号','工位','有效期'],
			colModel:[
			           {name:'partial_id',index:'partial_id',width : 90,align:'left',hidden:true},
					   {name:'model_name',index:'model_name',width : 90,align:'left'},
					   {name:'process_code',index:'process_code',width : 40,align:'center',formatter : function(value, options, rData){
							if(!value || value=='0'){
								return '' ;
							}else{
								return value;
							}							
						}},
					   {name:'history_limit_date',index:'history_limit_date',width : 90,align:'center',formatter : function(value, options, rData){
							if(value=='9999/12/31'){
								return '' ;
							}else{
								return "～ "+value;
							}							
						}}
			         ],
			rowNum: 100,
			toppager : false,
			pager : "#ext_listpager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true, 
			pginput : false,					
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false				
		});
			
	}
};
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
            var listdata = resInfo.servicePartial;
			if ($("#gbox_list").length > 0) {
				$("#list").jqGrid().clearGridData();
				$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			} else {			
				$("#list").jqGrid({
					data : listdata,
					height : 461,
					width : gridWidthMiddleRight,
					rowheight : 23,
					datatype : "local",
					colNames : [  '','零件ID', '零件编码', '零件名称', '消耗品','参考价格', '规格种别','最后更新人', '最后更新时间','是否存在有效期大于当前时间'],
					colModel : [
					{name:'myac', width:40, fixed:true, sortable:false, resize:false,formatter:'actions',formatoptions:{keys:true, editbutton:false}},
					{name:'partial_id',index:'partial_id', hidden:true},
					{name:'code',index:'code',width:40},
					{name:'name',index:'name',width : 150}, 
					{name:'consumable_flg',index:'consumable_flg',width:20,formatter:function(value, options, rData) {
						if(value==1){
							return '是';
						}else{
							return '否';
						}
					}},
					{name:'price',index:'price', width:30,align:'right',sorttype:'currency',formatter:'currency',formatoptions:{thousandsSeparator:',',defaultValue: ''}},
					{name:'spec_kind',index:'spec_kind', width:40,formatter:'select',editoptions:{value:$("#hide_grid_spec_kind").val()}},
					{name:'updated_by',index:'updated_by',width : 35}, 
					{name:'updated_time',index:'updated_time',width : 60},
					{name:'is_exists',index:'is_exists',hidden:true,editable:false,formatoptions:function(value, options, rData) {
					}}
					],
					toppager : false,
					rowNum : 20,rownumbers : true,
					pager : "#listpager",//翻页
					viewrecords : true,//显示总记录数
					caption : modelname + "一览",
					gridview : true, 
					//altRows:true,altclass:'ui-priority-secondary',
					pagerpos : 'right',
					ondblClickRow : showEdit,
					onSelectRow :enableButton,
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true],
					gridComplete:function(){
						// 得到显示到界面的id集合
						var IDS = $("#list").getDataIDs();
						// 当前显示多少条
						var length = IDS.length;
						var pill = $("#list");
						for (var i = 0; i < length; i++) {
							// 从上到下获取一条信息
							var rowData = pill.jqGrid('getRowData', IDS[i]);
							var is_exists = rowData["is_exists"];
							//如果有效截止日期不是9999/12/31的话，蒸行变成灰色
							if(is_exists!='1'){
								pill.find("tr#" + IDS[i] +  " td ").addClass("overdue_row");
							}
						}
					}
				});
				var tname=['myac'];
				//当不是系统管理员和零件管理员时隐藏所有可以修改和更新功能
				if($("#hidden_isOperator").val()=='other'){
					$("#list").jqGrid('hideCol',tname);
					$("#gbox_list").next().hide();
					$("#edit_price,#edit_name,#edit_code").attr("readonly",true);
					$("#list").jqGrid('setGridWidth', '992');
				}else if($("#hidden_isOperator").val()=='operator'){
					
					$(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
							'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建'+ modelname +'" role="button" aria-disabled="false">' +
						'</div>');

					$("#addbutton").button();
					// 追加处理
					$("#addbutton").click(showAdd);

					$("#list").jqGrid('showCol',tname);
					$("#addbutton").show();
					$("#gbox_list").next().show();
				}
			}
		}
	} catch (e) {};
};
var showList = function() {
	top.document.title = modelname + "一览";
	$("#searcharea").show();
	$("#listarea").show();
}
var showAdd = function() {
	// 默认画面变化 s
	top.document.title = "新建" + modelname;
	$("#searcharea").hide();
	$("#listarea,#view_edit_list").hide();
	$("#editarea span.areatitle").html("新建" + modelname);
	$("#editarea").show();
	$("#editform tr:not(:has(input,textarea,select)),#editform .changeFunction").hide();
	$("#editform input[type!='button'][type!='radio'], #editform textarea").val("");
	$("#editform select").val("").trigger("change");
	$("#editform label:not([radio])").html("");
	$("#edit_order_flg_yes").attr("checked",true);
	$("#editbutton").val("新建");
	$("#editbutton").enable();
	$(".errorarea-single").removeClass("errorarea-single");
	//规格种别默认为:小零件
	$("#edit_spec_kind").val("1").trigger("change");
	$("#edit_unpack_no").attr("checked",true).trigger("change");
	$("#edit_split_quantity").val("").closest("tr").hide();
	
	// 默认画面变化 e

	// 前台Validate设定
	$("#editform").validate({
		rules:{		
	       code:{
	    	   required:true,
	    	   maxlength:9
	       },
	       name:{
	       	// Deleted by Gonglm 2/11 Start
//	    	   required:true,
	       	// Deleted by Gonglm 2/11 End
	    	   maxlength:120
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
				"code" : $("#edit_code").val(),
				"name" : $("#edit_name").val(),
				"price":$("#edit_price").val(),
				"order_flg":$("#edit_order_flg input:checked").val(),
				"spec_kind":$("#edit_spec_kind").val(),
				"unpack_flg":$("#edit_unpack input:checked").val(),
				"split_quantity":$("#edit_split_quantity").val()
			};

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
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}
/** 
 * 检索处理
 */
var keepSearchData;
var findit = function(data) {
	if (!data) {
		KeepSearchData = {
			"code":$("#search_code").val(),
			"name":$("#search_name").val(),
			"consumable_flg":$("#search_consumable_flg input:checked").val(),
			"spec_kind":$("#search_spec_kind").val()
	    };
	 } else {
			keepSearchData = data;
	 } 
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=search',
			cache : false,
			data : KeepSearchData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : search_handleComplete
		});
	
};

var showEdit = function(){
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
	var rowData = $("#list").getRowData(row);
	var data = {
			"partial_id": rowData.partial_id
			};
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
}
var showedit_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			treatBackMessages(null, resInfo.errors);
		} else {
			top.document.title = modelname + "修改";	
			$("#searcharea").hide();
			$("#listarea").hide();
			$("#editarea span.areatitle").html(modelname + "：" + encodeText(resInfo.partialForm.code));
			$("#editarea,#view_edit_list").show();		
			$("#editform tr,.changeFunction").show();
			$("#editbutton").val("修改");
			$("#editbutton").enable();
			$("#editbutton").button();
			$("#label_detail_updated_by,#label_detail_updated_time").hide();
			
			$(".errorarea-single").removeClass("errorarea-single");
			
			show_ext_list(resInfo.returnPartial);
			
			//价格清除
			$("#label_partial_id").text("");
			$("#edit_code").val("");
			$("#edit_name").val("");
			$("#edit_price").data("post","");
			$("#label_update_by").text("");
			$("#label_update_time").text("");
			$("#edit_avarible_end_date").data("post","");
			
			// 详细数据
			$("#edit_order_flg input[value="+resInfo.partialForm.order_flg+"]").attr("checked", true).trigger('change');
			var consumable_flg = resInfo.partialForm.consumable_flg;
			if(consumable_flg==0){
				$("#edit_consumable_flg").text("否");
			}else{
				$("#edit_consumable_flg").text("是");
			}
			
			$("#label_history_limit_date").data("post",resInfo.partialForm.history_limit_date);
			$("#label_partial_id").text(resInfo.partialForm.partial_id);
			$("#edit_code").val(encodeText(resInfo.partialForm.code));
			$("#edit_name").val(resInfo.partialForm.name);			
			$("#edit_price").data("post",resInfo.partialForm.price);		
			$("#edit_price").val($("#edit_price").data("post"));
//			$("#label_price").val();
			$("#label_update_by").text(resInfo.partialForm.updated_by);
			$("#label_price").text(resInfo.partialForm.new_price || "");
			$("#label_update_time").text(resInfo.partialForm.updated_time);		
			$("#edit_avarible_end_date").data("post",resInfo.partialForm.avarible_end_date)
			$("#edit_spec_kind").val(resInfo.partialForm.spec_kind).trigger('change');
			
			//是否分装
			var partialUnpackForm = resInfo.partialUnpackForm;
			if(partialUnpackForm){
				$("#edit_unpack_yes").attr("checked",true).trigger("change");
				$("#edit_split_quantity").val(partialUnpackForm.split_quantity).closest("tr").show();//分装数量
			}else{
				$("#edit_unpack_no").attr("checked",true).trigger("change");
				$("#edit_split_quantity").val("").closest("tr").hide();
			}
			
			// 切换按钮效果
			$("#editbutton").unbind("click");
			$("#editbutton").click(function() {
				// 前台Validate
				if ($("#editform").valid()) {
					// 通过Validate,切到修改确认画面
					$("#editbutton").disable();

					warningConfirm("确认要修改记录吗？", function() {
						// 有无更新金额 返回false or true给后台代码 后台可以根据request.getParamter("")
						//var priceNotChanged = ($("#edit_value_currency").val() ==($("#edit_value_currency").data("post"))&& $("#edit_price").val()==$("#edit_price").data("post"));
						var data = {
							"partial_id":$("#label_partial_id").text(),
							"code" : $("#edit_code").val(),
							"name" : $("#edit_name").val(),
							"price":$("#edit_price").val(),
							"order_flg":$("#edit_order_flg input:checked").val(),
							"spec_kind":$("#edit_spec_kind").val(),
							"unpack_flg":$("#edit_unpack input:checked").val(),
							"split_quantity":$("#edit_split_quantity").val()
							//"priceNotChanged" : priceNotChanged
						}
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
						});
					}, function() {
						$("#editbutton").enable();
					}, "修改确认")
				};
			});
		
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
}

/**
 * 切到一览画面
 */
var showList = function() {
	top.document.title = modelname + "一览";
	$("#searcharea").show();
	$("#listarea").show();
	$("#editarea").hide();
	$("#detailarea").hide();
};
var showDelete = function(rid) {
	var rowData = $("#list").getRowData(rid);
	var data = {"partial_id" : rowData.partial_id}

	warningConfirm("删除不能恢复。确认要删除["+encodeText(rowData.code)+"]的记录吗？", function() {
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
	}, null, "删除确认");
};

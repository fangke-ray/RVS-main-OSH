var servicePath="steel_wire_container_wash_process.do";
var process_type = null;
var heatshrinkableMap = new Map();

$(function(){
	$("#btn-group").buttonset();
 	$("input.ui-button").button();
 	
    /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
    $("span.ui-icon").bind("click", function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().show("blind");
        } else {
            $(this).parent().parent().next().hide("blind");
        }
    });
    
    $("input[type='text'][readonly]").datepicker({
		showButtonPanel : true,
		currentText : "今天",
		maxDate : 0
	});
    
    $("#canelbutton").bind("click",function(){
		$("#body-mdl").show();
		$("#add").hide();
	});
    
    $("#canel2button").bind("click",function(){
		$("#body-mdl").show();
		$("#update").hide();
	});
    
	setReferChooser($("#L1_hidden_operator_id"),$("#search_operator_name_refer"));
	setReferChooser($("#L2_hidden_operator_id"),$("#search_operator_name_refer"));
	setReferChooser($("#L3_hidden_operator_id"),$("#search_operator_name_refer"));
	setReferChooser($("#L4_hidden_operator_id"),$("#search_operator_name_refer"));
	setReferChooser($("#L5_hidden_operator_id"),$("#search_operator_name_refer"));
	
	setReferChooser($("#hidden_cut_add_partial_id"),$("#search_partial_code_refer"),null,function(tr){
		if(tr == null){
			$("#add_cut_length").html("").next().remove();
			return;
		}
		
		var cut_length = $(tr).find("td").last().text().trim();
		var arr = cut_length.split(",");
		
		var content = "<option value=''></option>";
		arr.forEach(function(item){
			content +="<option value='" + item + "'>" + item+ "</option>";
		});
		
		$("#add_cut_length").html(content).select2Buttons();
	});
	
	list([]);

	$("#btn-group input[type='radio']").click(function(){
		var $this = $(this);
		if($this.hasClass("selected")){
			return;
		}
		
		$("#btn-group input[type='radio']").removeClass("selected");
		$this.addClass("selected");
		
		process_type = $this.attr("process_type");
		var id = $this.attr("id");
		
		$("#load > div[for]").hide();
		$("#load > div[for='" + id + "']").show();
		
		var title = $("label[for='" + id +"']").text();
		$("#listModelName").text(title + "一览");
		
		if(process_type == 1){
			$("#list").jqGrid('showCol','lot_no');
			$("#list").jqGrid('hideCol',['cut_length','sorc_no']);
			L1.findit();
		} else if (process_type == 2){
			$("#list").jqGrid('showCol','cut_length');
			$("#list").jqGrid('hideCol',['lot_no','sorc_no']);
			L2.findit();
		} else if (process_type == 3){
			$("#list").jqGrid('hideCol',['lot_no','cut_length','sorc_no']);
			L3.findit();
		} else if (process_type == 4){
			$("#list").jqGrid('hideCol',['lot_no','cut_length','sorc_no']);
			L4.findit();
		} else if (process_type == 5){
			$("#list").jqGrid('showCol','sorc_no');
			$("#list").jqGrid('hideCol',['lot_no','cut_length']);
			L5.findit();
		}
		
		$("#list").jqGrid('setGridWidth', '992');
		
		if(process_type == 5){
			$("#executes").show();
		}else{
			$("#executes").hide();
		}
	});
	
	$("#L1Button").prop("checked",true).trigger("change").trigger("click");
	
	$("#applyToMaterialButton").click(distribut);
	
	
	$("#search_partial_code_refer table:last-child tr").each(function(){
		var key = $(this).find("td:first-child").text();
		var value = $(this).find("td:last-child").text();
		
		heatshrinkableMap.set(key,value);
	});
	
	jsinit();

	L1.init();
	L2.init();
	L3.init();
	L4.init();
	L5.init();

});

//固定件清洗
var L1 = Object.create(null,{
	init : {
		value : function(){
			$("#L1searchbutton").click(L1.findit);
			$("#L1resetbutton").click(L1.reset);
		}
	},
	
	reset :	{
		value : function(){
			$("div[for='L1Button'] form[name='searchform']").find("input[type='text'],input[type='hidden']").val("");
		}
	},

	findit : {
		value : function() {
			var data={
				"process_type" : process_type,
		        "code": $("#L1_search_partial_code").val(),
		        "lot_no": $("#L1_search_lot_no").val(),
		        "process_time_start":  $("#L1_search_process_time_start").val(),
		        "process_time_end": $("#L1_search_process_time_end").val(),
		        "operator_id": $("#L1_hidden_operator_id").val()
		    };
			search(data);
		}
	}
});

//热缩管切割
var L2 = Object.create(null,{
	init : {
		value : function(){
			$("#L2searchbutton").click(L2.findit);
			$("#L2resetbutton").click(L2.reset);
		}
	},
	
	reset : {
		value : function(){
			$("div[for='L2Button'] form[name='searchform']").find("input[type='text'],input[type='hidden']").val("");
		}
	},
	
	findit : {
		value : function(){
			var data={
				"process_type" : process_type,
		        "code": $("#L2_search_partial_code").val(),
		        "process_time_start":  $("#L2_search_process_time_start").val(),
		        "process_time_end": $("#L2_search_process_time_end").val(),
		        "operator_id": $("#L2_hidden_operator_id").val()
		    };
			search(data);
		}
	}
});

var L3 = Object.create(null,{
	init : {
		value : function(){
			$("#L3searchbutton").click(L3.findit);
			$("#L3resetbutton").click(L3.reset);
		}
	},
	
	reset : {
		value : function(){
			$("div[for='L3Button'] form[name='searchform']").find("input[type='text'],input[type='hidden']").val("");
		}
	},
	
	findit : {
		value : function(){
			var data={
				"process_type" : process_type,
		        "code": $("#L3_search_partial_code").val(),
		        "process_time_start":  $("#L3_search_process_time_start").val(),
		        "process_time_end": $("#L3_search_process_time_end").val(),
		        "operator_id": $("#L3_hidden_operator_id").val()
		    };
			search(data);
		}
	}
});

var L4 = Object.create(null,{
	init : {
		value : function(){
			$("#L4searchbutton").click(L4.findit);
			$("#L4resetbutton").click(L4.reset);
		}
	},
	
	reset : {
		value : function(){
			$("div[for='L4Button'] form[name='searchform']").find("input[type='text'],input[type='hidden']").val("");
		}
	},
	
	findit : {
		value : function(){
			var data={
				"process_type" : process_type,
		        "code": $("#L4_search_partial_code").val(),
		        "process_time_start":  $("#L4_search_process_time_start").val(),
		        "process_time_end": $("#L4_search_process_time_end").val(),
		        "operator_id": $("#L4_hidden_operator_id").val()
		    };
			search(data);
		}
	}
});

var L5 = Object.create(null,{
	init : {
		value : function(){
			$("#L5searchbutton").click(L5.findit);
			$("#L5resetbutton").click(L5.reset);
		}
	},
	
	reset : {
		value : function(){
			$("div[for='L5Button'] form[name='searchform']").find("input[type='text'],input[type='hidden']").val("");
		}
	},
	
	findit : {
		value : function(){
			var data={
				"process_type" : process_type,
		        "code": $("#L5_search_partial_code").val(),
		        "process_time_start":  $("#L5_search_process_time_start").val(),
		        "process_time_end": $("#L5_search_process_time_end").val(),
		        "operator_id": $("#L5_hidden_operator_id").val()
		    };
			search(data);
		}
	}
});

function jsinit(){
	$.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath + '?method=jsinit',
        cache : false,
        data : null,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : function(xhrobj, textStatus) {
        	var resInfo = null;
    	    try {
    	        // 以Object形式读取JSON
    	        eval('resInfo =' + xhrobj.responseText);
    	        if (resInfo.errors.length > 0) {
    	            // 共通出错信息框
    	            treatBackMessages(null, resInfo.errors);
    	        } else {
	            	$("#add_partial_code").autocomplete({
						source : resInfo.sPartialCode,
						minLength :3,
						delay : 100,
						focus: function( event, ui ) {
							 $(this).val( ui.item.label );
							 return false;
						},
						select: function( event, ui ) {
							$("#hidden_add_partial_id").val(ui.item.value);
							$(this).val( ui.item.label ).data("data",ui.item.label);
							return false;
						},
						change: function( event, ui ) {
							if ($(this).data("data") && this.value != $(this).data("data"))
								$("#hidden_add_partial_id").val("");
						}
					});
    	        }
    	    } catch (e) {};
        }
    });
};

function search(data){
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
        complete : function(xhrobj, textStatus) {
        	var resInfo = null;
            try {
                // 以Object形式读取JSON
                eval('resInfo =' + xhrobj.responseText);
                if (resInfo.errors.length > 0) {
                    // 共通出错信息框
                    treatBackMessages(null, resInfo.errors);
                } else {
                    list(resInfo.finished);
                }
            } catch (e) {};
        }
    });
};

function list(listdata){
	if ($("#gbox_list").length > 0) {
        $("#list").jqGrid().clearGridData();
        $("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
    } else {
    	$("#list").jqGrid({
            data:listdata,
            height: 461,
            width: 992,
            rowheight: 23,
            datatype: "local",
            colNames:['partial_id','process_type','hid_process_time','品名','入库批号','切割长度','数量','处理日期','责任人','维修对象修理单号'],
            colModel:[
            	  {name:'partial_id',index:'partial_id',hidden:true},
            	  {name:'process_type',index:'process_type',hidden:true},
            	  {name:'hid_process_time',index:'hid_process_time',hidden:true,formatter : function(value, options, rData) {return rData["process_time"];	}},
	              {name:'code',index:'code',width:100},
                  {name:'lot_no',index:'lot_no',width:50},
                  {name:'cut_length',index:'cut_length',width:50,align:'right',formatter : function(value, options, rData) {
                	  if(!rData["lot_no"]){
                		  return "-"; 
                	  }else{
                		  return rData["lot_no"];
                	  }
                   }},
                  {name:'quantity',index:'quantity',width:50,align:'right'},
                  {name:'process_time',index:'process_time',width:90,align:'center',sorttype:'date',formatter:'date',formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'Y-m-d'}},
                  {name:'operator_name',index:'operator_name',width:100},
                  {name:'sorc_no',index:'sorc_no',width:100}
            ],
            rowNum: 20,
            toppager : false,
            pager : "#listpager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true, 
			rownumbers : true,
            pginput : false,
            recordpos : 'left',
            hidegrid : false,
            deselectAfterSort : false,
            onSelectRow:enableButton,
            ondblClickRow : function(){
            	if($("#hidPrivacyButton").val().trim()==="true"){
            		showEdit();
            	}
            },
            viewsortcols : [true,'vertical',true],
            gridComplete:function(){
            	if(process_type == 5){
            		$("#applyToMaterialButton").disable();
            	}
            }
        }); 
        
        $(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
				'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="记录">' +
			'</div>');
		$("#addbutton").button();
		// 追加处理
		$("#addbutton").click(function(){
			afObj.applyProcess(270 + parseInt(process_type), this, showAdd, arguments);
		});
    }
};

function enableButton(){
	var rowId=$("#list").jqGrid("getGridParam","selrow");
	
	if(rowId && process_type == 5){
		$("#applyToMaterialButton").enable();
	}else{
		$("#applyToMaterialButton").disable();
	}
};

function showAdd(){
	var title = $("input[type='radio'][process_type='" + process_type + "']").next().text();
	
	$("#body-mdl").hide();
	$("#add").show().find(".areatitle").text("新建" + title);
	
	$("#addform table input[type='text'],#addform table input[type='hidden']").val("").removeClass("errorarea-single");
	$("#add_cut_length").html("").next().remove();
	$("#add_partial_code").data("data","");
	
	if(process_type == 1){
		$("#hidden_add_partial_id,#add_lot_no").closest("tr").show();
		$("#hidden_cut_add_partial_id,#add_cut_length").closest("tr").hide();
	} else if(process_type == 2){
		$("#hidden_add_partial_id,#add_lot_no").closest("tr").hide();
		$("#hidden_cut_add_partial_id,#add_cut_length").closest("tr").show();
	} else if(process_type == 3){
		$("#hidden_add_partial_id").closest("tr").show();
		$("#add_lot_no,#hidden_cut_add_partial_id,#add_cut_length").closest("tr").hide();
	} else if(process_type == 4){
		$("#hidden_add_partial_id").closest("tr").show();
		$("#add_lot_no,#hidden_cut_add_partial_id,#add_cut_length").closest("tr").hide();
	} else if(process_type == 5){
		$("#hidden_add_partial_id").closest("tr").show();
		$("#add_lot_no,#hidden_cut_add_partial_id,#add_cut_length").closest("tr").hide();
	}
	
	if(process_type == 5){
		$("#add_quantity").val("1").disable();
	}else{
		$("#add_quantity").enable();
	}
	
	$("#insertbutton").unbind("click").bind("click",function(){
		if(process_type == 2){
			if($("#hidden_cut_add_partial_id").val() == ""){
				blink($("#add_cut_partial_code"), "errorarea-single", 2);
				return;
			}
		}else{
			if($("#hidden_add_partial_id").val() == ""){
				var label = $("#add_partial_code").val();
				$("#add_partial_code").autocomplete("search", label);
				$("ul.ui-autocomplete a").filter(function(){
					return $(this).text() === label;
				}).trigger("click");

				if($("#hidden_add_partial_id").val() == ""){
					blink($("#add_partial_code"), "errorarea-single", 2);
					return;
				}
			}
		}
		
		var data = {
			"process_type":process_type
		};
		
		if(process_type == 1){
			data["lot_no"] = $("#add_lot_no").val();
		} else if(process_type == 2){
			data["lot_no"] = $("#add_cut_length").val();
		} else if(process_type == 3){
			data["code"] = $("#add_partial_code").val();
		}
		
		if(process_type == 2){
			data["partial_id"] = $("#hidden_cut_add_partial_id").val();
		}else{
			data["partial_id"] = $("#hidden_add_partial_id").val();
		}
		
		if(process_type == 5){
			data["quantity"] = "1";
		}else{
			data["quantity"] = $("#add_quantity").val();
		}

		afObj.applyProcess(270 + parseInt(process_type), this, doInsertExecute, [data]);
	});
};

function doInsertExecute(postData) {
	$.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath + '?method=doInsert',
        cache : false,
        data : postData,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : insert_handleComplete
    });
}

function insert_handleComplete (xhrobj, textStatus){
	var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#addform", resInfo.errors);
        } else {
            $("#body-mdl").show();
			$("#add").hide();
        	
			if(process_type == 1){
				L1.findit();
			} else if (process_type == 2){
				L2.findit();
			} else if (process_type == 3){
				L3.findit();
			} else if (process_type == 4){
				L4.findit();
			} else if (process_type == 5){
				L5.findit();
			}
        }
    } catch (e) {};
};

function showEdit () {
	var title = $("input[type='radio'][process_type='" + process_type + "']").next().text();
	
	$("#updateform table input[type='text'],#updateform table input[type='hidden']").removeClass("errorarea-single");
	$("#update_cut_length").html("");
	
	$("#body-mdl").hide();
	$("#update").show().find(".areatitle").text("更新" + title);
	
	$("#update_lot_no,#update_quantity").enable();
	$("#updatebutton").show();
	
	if(process_type == 1){
		$("#update_lot_no").closest("tr").show();
		$("#update_lot_no").parent().prev().text("入库批号");
		$("#update_cut_length").closest("tr").hide();
	} else if(process_type == 2){
		$("#update_lot_no").closest("tr").hide();
		$("#update_cut_length").closest("tr").show();
		$("#update_lot_no").parent().prev().text("切割长度");
	} else if(process_type == 3){
		$("#update_lot_no,#update_cut_length").closest("tr").hide();
	} else if(process_type == 4){
		$("#update_lot_no,#update_cut_length").closest("tr").hide();
	} else if(process_type == 5){
		$("#update_lot_no,#update_cut_length").closest("tr").hide();
		$("#update_quantity").disable();
		$("#updatebutton").hide();
	}
	
	var rowID = $("#list").jqGrid("getGridParam","selrow");
	var rowData = $("#list").getRowData(rowID);
	
	$("#update_partial_code").text(rowData.code);
	$("#update_lot_no").val(rowData.lot_no);
	$("#update_quantity").val(rowData.quantity);
	$("#update_process_time").text(rowData.hid_process_time);
	$("#update_operator_name").text(rowData.operator_name);
	
	if(process_type == 2){
		var arr = heatshrinkableMap.get((rowData.partial_id).replace(/^0+/,"")).split(",");
		var content = "<option value=''></option>";
		arr.forEach(function(item){
			content +="<option value='" + item + "'>" + item+ "</option>";
		});
		
		$("#update_cut_length").html(content).select2Buttons().val(rowData.lot_no).trigger("change");
	}
	
	$("#updatebutton").unbind("click").bind("click",function(){
		var data={
			"partial_id": rowData.partial_id,
			"process_type" : rowData.process_type,
			"process_time": rowData.hid_process_time
		};
		
		if(process_type == 1){
			data["lot_no"] = $("#update_lot_no").val();
			data["quantity"] = $("#update_quantity").val();
		} else if(process_type == 2){
			data["lot_no"] = $("#update_cut_length").val();
			data["quantity"] = $("#update_quantity").val();
		}else if( process_type == 3 || process_type == 4){
			data["quantity"] = $("#update_quantity").val();
		}
		
		$.ajax({
	        beforeSend : ajaxRequestType,
	        async : true,
	        url : servicePath + '?method=doUpdate',
	        cache : false,
	        data : data,
	        type : "post",
	        dataType : "json",
	        success : ajaxSuccessCheck,
	        error : ajaxError,
	        complete : update_handleComplete
	    });
	});
};

function update_handleComplete (xhrobj, textStatus){
  	var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#updateform", resInfo.errors);
        } else {
            $("#body-mdl").show();
			$("#update").hide();
        	
			if(process_type == 1){
				L1.findit();
			} else if (process_type == 2){
				L2.findit();
			} else if (process_type == 3){
				L3.findit();
			} else if (process_type == 4){
				L4.findit();
			} else if (process_type == 5){
				L5.findit();
			}
        }
    } catch (e) {}
};

function distribut(){
	$.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath + '?method=searchMaterial',
        cache : false,
        data : null,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : function (xhrobj, textStatus){
        	var resInfo = null;
            try {
                // 以Object形式读取JSON
                eval('resInfo =' + xhrobj.responseText);
                if (resInfo.errors.length > 0) {
                    // 共通出错信息框
                    treatBackMessages(null, resInfo.errors);
                } else {
                	materialList(resInfo.list);
                	
                	$("#materialDialog").dialog({
            		    resizable : false,
            			modal : true,
            			title : "分配维修对象",
            			width : 640,
            			buttons : {
            				"确认" : function() {
            					var rowID = $("#materiallist").jqGrid("getGridParam","selrow");
            					if(!rowID){
            						errorPop("请选择需要分配的维修对象。");
            						return;
            					}
            					var rowData = $("#materiallist").getRowData(rowID);
            					var material_id = rowData.material_id;
            					
            					rowID = $("#list").jqGrid("getGridParam","selrow");
            					rowData = $("#list").getRowData(rowID);
            					
            					var data = {
        							"partial_id": rowData.partial_id,
        							"process_type" : rowData.process_type,
        							"process_time": rowData.hid_process_time,
        							"material_id" : material_id
        						};
            					
            					$.ajax({
            				        beforeSend : ajaxRequestType,
            				        async : true,
            				        url : servicePath + '?method=doApplyToMaterial',
            				        cache : false,
            				        data : data,
            				        type : "post",
            				        dataType : "json",
            				        success : ajaxSuccessCheck,
            				        error : ajaxError,
            				        complete : function (xhrobj, textStatus){
            				        	 var resInfo = null;
            				     	    try {
            				     	        // 以Object形式读取JSON
            				     	        eval('resInfo =' + xhrobj.responseText);
            				     	        if (resInfo.errors.length > 0) {
            				     	            // 共通出错信息框
            				     	            treatBackMessages(null, resInfo.errors);
            				     	        } else {
            				     	        	$("#materialDialog").dialog("close");
            				     	        	
            				     				if(process_type == 1){
            				     					L1.findit();
            				     				} else if (process_type == 2){
            				     					L2.findit();
            				     				} else if (process_type == 3){
            				     					L3.findit();
            				     				} else if (process_type == 4){
            				     					L4.findit();
            				     				} else if (process_type == 5){
            				     					L5.findit();
            				     				}
            				     	        }
            				     	    } catch (e) {}
            				        }
            				    });
            				},
            				"取消" : function() {
            					$(this).dialog("close");
            				}
            			}
            		});
                }
            } catch (e) {}
        }
    });
};

function materialList(listdata){
	if ($("#gbox_materiallist").length > 0) {
        $("#materiallist").jqGrid().clearGridData();
        $("#materiallist").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
    } else {
    	$("#materiallist").jqGrid({
            data:listdata,
            height: 231,
            width: 620,
            rowheight: 23,
            datatype: "local",
            colNames:['material_id','修理单号','型号','等级'],
            colModel:[
            	  {name:'material_id',index:'material_id',hidden:true},
            	  {name:'sorc_no',index:'sorc_no',width:100},
            	  {name:'model_name',index:'model_name',width:100},
	              {name:'levelName',index:'levelName',width:100,align:'center'}
            ],
            rowNum: 20,
            toppager : false,
            pager : "#materiallistpager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true, 
			rownumbers : true,
            pginput : false,
            recordpos : 'left',
            hidegrid : false,
            deselectAfterSort : false,
            onSelectRow:null,
            ondblClickRow : function(){},
            viewsortcols : [true,'vertical',true],
            gridComplete:function(){
            }
        }); 
    }
};
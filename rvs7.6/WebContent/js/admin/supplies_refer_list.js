var servicePath = "supplies_refer_list.do";
var waringUnitPrice = 2000;

$(function() {
	$("input.ui-button").button();
	
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
	
	$("#searchbutton").click(function(){
		$("#search_product_name").data("post", $("#search_product_name").val());
		$("#search_model_name").data("post", $("#search_model_name").val());
		$("#search_supplier").data("post", $("#search_supplier").val());
		
		findit();
	});
	
	$("#resetbutton").click(function(){
		reset();
	});
	
	$("#addarea span.ui-icon,#addCancelButton").click(function(){
		$("#addarea").hide();
		$("#searcharea").show();
	});
	
	$("#updatearea span.ui-icon,#updateCancelButton").click(function(){
		$("#updatearea").hide();
		$("#searcharea").show();
	});
	
	$("#update_upload_file").parent().on("change", "#update_upload_file", uploadPhoto);

	$("#add_capacity, #update_capacity").on("keyup", function(){
		var capacity = this.value;

		if (capacity && capacity != 0 && capacity != 1 && !isNaN(capacity)) {
			$(this).closest("form").addClass("part_vis");
			var $package_unit_price = $(this).closest("table").find("input[name='package_unit_price']");
			var package_unit_price = $package_unit_price.val().trim();
			if (package_unit_price && !isNaN(package_unit_price)) {
				$package_unit_price.trigger("keyup");
			}
		} else {
			$(this).closest("form").removeClass("part_vis");
		}
	});

	$("#add_unit_price, #update_unit_price").on("keyup", function(){
		var unit_price = this.value;
		var capacity = $(this).closest("table").find("input[name='capacity']").val().trim();

		if (capacity && !isNaN(capacity)
			&& unit_price && !isNaN(unit_price)) {
			var pack_unit_price = +unit_price * +capacity;
			$(this).parent().children("input[name='package_unit_price']").val(pack_unit_price.toFixed(2));
		}		
	});

	$("#add_package_unit_price, #update_package_unit_price").on("keyup", function(){
		if (!$(this).closest("form").hasClass("part_vis")) {
			return;
		}
		var package_unit_price = this.value;
		var capacity = $(this).closest("table").find("input[name='capacity']").val().trim();

		if (capacity && !isNaN(capacity)
			&& package_unit_price && !isNaN(package_unit_price)) {
			var unit_price = +package_unit_price / +capacity;
			$(this).parent().children("input[name='unit_price']").val(unit_price.toFixed(2));
		}		
	});

	$("td[contenteditable]").on("paste", imgPaste);
	findit();
});

var reset = function(){
	$("#search_product_name").data("post", "").val("");
	$("#search_model_name").data("post", "").val("");
	$("#search_supplier").data("post", "").val("");
};

var findit = function() {
	var data = {
		"product_name" : $("#search_product_name").data("post"),
		"model_name" : $("#search_model_name").data("post"),
		"supplier" : $("#search_supplier").data("post")
	};
	
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

var search_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			var listdata = resInfo.list;
			filed_list(listdata);
		}
	}catch (e) {};
};

function filed_list(listdata){
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else  {
		$("#list").jqGrid({
			data:listdata,
			height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','KEY','品名','规格','capacity','unit_text','内容数量','预定单价','单位','供应商','商品编号', '照片',''],
			colModel:[
				{name:'myac',width:40, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true, editbutton:false}},
				{name:'refer_key',index:'refer_key', hidden : true},
				{name:'product_name',index:'product_name', width : 80},
				{name:'model_name',index:'model_name',width : 50},
				{name:'capacity',index:'capacity',hidden : true},
				{name:'unit_text',index:'unit_text',hidden : true},
				{name:'capacity_text',index:'capacity',width : 35, align:'right', formatter:function(value, options, rData){
					var capacity_value = rData["capacity"].trim();
					if (!capacity_value || capacity_value == 1) {
						return "-";
					} else {
						return capacity_value + " " + rData["unit_text"];
					}
				}},
				{name:'unit_price',index:'unit_price',width : 35,align:'right',sorttype:'currency',formatter:'currency',formatoptions:{thousandsSeparator:',',defaultValue: '-'}},
				{name:'package_unit_text',index:'package_unit_text',width : 25},
				{name:'supplier',index:'supplier',width : 40},
				{name:'goods_serial',index:'goods_serial',width : 50},
				{name:'photo_uuid',index:'photo_uuid',width:15,align:'center',formatter:function(value, options, rData) {
					if(value){
						return "有";
					} else {
						return "";
					}
				}},
				{name:'hide_photo_uuid',index:'hide_photo_uuid',hidden:true,formatter:function(value, options, rData) {
					if(rData.photo_uuid){
						return rData.photo_uuid;
					} else {
						return "";
					}
				}}
				],
			rowNum: 20,
			toppager : false,
			pager : "#listpager",
			viewrecords : true,
			rownumbers : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			ondblClickRow : showEdit,
			viewsortcols : [true,'vertical',true],
			gridComplete:function(){
			}
		});
		
		$("#gbox_list .ui-jqgrid-hbox").before(
			'<div class="ui-widget-content" style="padding:4px;">' +
				'<input type="button" id="showAddButton" value="新建常用采购清单">' +
			'</div>'
		);
		$("#showAddButton").button().click(showAdd);
	}
}

function showAdd(){
	$("#searcharea").hide();
	$("#addarea").show();
	$("#addform").removeClass("part_vis");
	$("#addform input[type='text']").val("").removeClass("errorarea-single");
	$("#add_upload_file").val("");
	
	// 前台Validate验证
	$("#addform").validate({
        rules:{
        	product_name:{
               required:true,
               maxlength:64
            },
			model_name:{
				maxlength:32
			},
			package_unit_price:{
				number:true,
				range:[0.00,9999.99]
			},
			unit_text:{
				maxlength:3
			},
			package_unit_text:{
				maxlength:3
			},
			supplier:{
				maxlength:64
			},
			goods_serial: {
				maxlength:45
			}
        }
    });

	$("#addbutton").unbind("click").bind("click",function(){
		if ($("#addform").valid()) {
			var unit_price = $("#add_package_unit_price").val().trim();
			unit_price = +unit_price;
			if(unit_price > waringUnitPrice){
				warningConfirm("预定单价高于" + waringUnitPrice +"，请确认是否要创建采购清单？",function(){
					doInsert();
				},null);
			} else {
				doInsert();
			}
		}
	});
}

function doInsert(){
	var postData = {
		"product_name" : $("#add_product_name").val().trim(),
		"model_name" : $("#add_model_name").val().trim(),
		"unit_price" : $("#add_package_unit_price").val().trim(),
		"package_unit_text" : $("#add_package_unit_text").val().trim(),
		"supplier" : $("#add_supplier").val().trim(),
		"goods_serial" : $("#add_goods_serial").val().trim()
	};

	var capacity = $("#add_capacity").val().trim();
	if (capacity && capacity != 0 && capacity != 1 && !isNaN(capacity)) {
		postData["capacity"] = capacity;
		postData["unit_text"] = $("#add_unit_text").val().trim();
	} else {
		postData["capacity"] = 1;
	}

	$.ajaxFileUpload({
        url : servicePath + "?method=doInsert",
        secureuri : false,
        data: postData,
        fileElementId : 'add_upload_file', // 文件选择框的id属性
        dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			var resInfo = $.parseJSON(responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("#addform", resInfo.errors);
			} else {
				$("#searcharea").show();
				$("#addarea").hide();
				findit();
			}
		}
    });
}

function showEdit(){
	var row = $("#list").jqGrid("getGridParam","selrow");//得到选中的行ID
    var rowData = $("#list").getRowData(row);
    
	$("#updateform input[type='text']").removeClass("errorarea-single");
	
	$("#update_upload_file").val("").prop({
		"refer_key":rowData.refer_key,
		"photo_uuid":rowData.hide_photo_uuid
	});
	
	$("#update_product_name").val(rowData.product_name);
	$("#update_model_name").val(rowData.model_name || "");
	var unit_price = rowData.unit_price;
	if (unit_price == "-") unit_price = "";
	$("#update_package_unit_price").val(rowData.unit_price || "");
	$("#update_package_unit_text").val(rowData.package_unit_text || "");
	$("#update_supplier").val(rowData.supplier || "");
	$("#update_goods_serial").val(rowData.goods_serial || "");

	$("#update_unit_price").val("");
	$("#update_unit_text").val(rowData.unit_text || "")
	$("#update_capacity").val(rowData.capacity || "").trigger("keyup");

	var imgUrl = "images/noimage128x128.gif";
	if(rowData.hide_photo_uuid){
		imgUrl = "http://" + document.location.hostname + "/photos/supplies_refer_list/" + rowData.hide_photo_uuid + "?_s=" + new Date().getTime();
	}
	$("#updateform .show_photo").attr("src",imgUrl);
	
	// 前台Validate验证
	$("#updateform").validate({
        rules:{
        	product_name:{
               required:true,
               maxlength:64
            },
			model_name:{
				maxlength:32
			},
			unit_price:{
				number:true,
				range:[0.01,9999.99]
			},
			unit_text:{
				maxlength:3
			},
			supplier:{
				maxlength:64
			}
        }
    });
	
	$("#updatebutton").unbind("click").bind("click",function(){
		if ($("#updateform").valid()) {
			var unit_price = $("#update_unit_price").val().trim();
			unit_price = +unit_price;
			if(unit_price > waringUnitPrice){
				warningConfirm("预定单价高于" + waringUnitPrice +"，请确认是否要更新采购清单？",function(){
					doUpdate(rowData.refer_key);
				},null);
			} else {
				doUpdate(rowData.refer_key);
			}
		}
	});
	
	$("#searcharea").hide();
	$("#updatearea").show();
}

function doUpdate(refer_key){
	var data = {
		"refer_key" : refer_key,
		"product_name" : $("#update_product_name").val(),
		"model_name" : $("#update_model_name").val(),
		"capacity" : $("#update_capacity").val(),
		"unit_price" : $("#update_package_unit_price").val(),
		"unit_text" : $("#update_unit_text").val(),
		"package_unit_text" : $("#update_package_unit_text").val(),
		"supplier" : $("#update_supplier").val(),
		"goods_serial" : $("#update_goods_serial").val()
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath +'?method=doUpdate',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("#updateform", resInfo.errors);
			} else {
				$("#searcharea").show();
				$("#updatearea").hide();
				findit();
			}
		}
	});
}

function uploadPhoto(){
	var postData = {
		"refer_key" : $(this).prop("refer_key"),
		"photo_uuid" : $(this).prop("photo_uuid")//for delete file
	};
	
	$.ajaxFileUpload({
        url : servicePath + "?method=doUploadImage",
        secureuri : false,
        data: postData,
        fileElementId : 'update_upload_file', // 文件选择框的id属性
        dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			var resInfo = $.parseJSON(responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				var imgUrl = "http://" + document.location.hostname + "/photos/supplies_refer_list/" + resInfo.fileName + "?_s=" + new Date().getTime();
				$("#updateform .show_photo").attr("src",imgUrl);
				$("#update_upload_file").val("");
				findit();
			}
		}
    });
}

function showDelete(rid) {
	var rowData = $("#list").getRowData(rid);
	
	warningConfirm("删除不能恢复。确认要删除["+encodeText(rowData.product_name)+"]的记录吗？",function(){
		var data = {
			"refer_key" : rowData.refer_key
		};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath +'?method=doDelete',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				var resInfo = $.parseJSON(xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					findit();
				}
			}
		});
	},null,"删除确认");
}

var imgPaste = function(e){
	var pastedText = undefined;
	var browserClipboardData = undefined;
	if (window.clipboardData && window.clipboardData.getData) { // IE
		browserClipboardData = window.clipboardData;
	} else {
		browserClipboardData = e.originalEvent.clipboardData;
	}
	pastedText = browserClipboardData.getData('Text');
	if(pastedText) {
		e.preventDefault();
		return false;
	}

	var cbFiles = browserClipboardData.files;
	if (!cbFiles || cbFiles.length == 0) {
		e.preventDefault();
		return false;
	}
	for (var i in browserClipboardData.items) {
		var cdItem = browserClipboardData.items[i];
		if (cdItem.type.indexOf("image") == 0) {
			var $target = $(e.target);
			if ($target.length == 1) {
				$target.html("");
				var $inpFile = $target.closest("form").find("input:file");
				if ($inpFile.length == 1) {
					$inpFile[0].files = cbFiles;
					$inpFile.trigger("change");
				}
				setTimeout(function(){
					$target.find("img").addClass("show_photo");
				},20);
				return true;
			}
		}
	}

	e.preventDefault();
	return false;
}
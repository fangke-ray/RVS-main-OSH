
var servicePath="peripheral_infect_device.do";
var strSeqTr = '<tr><td><select alt="组序号" name="seq" class="add_seq"><select></td><td class="td-content"><input type="text" alt="设备类别" name="device_type_name" class="add_device_type_name" class="ui-widget-content" style="width:215px;"/><input class="hidden_add_device_type_name" name="device_type_name" type="hidden"></td><td class="td-content"><input type="text" alt="型号" name="model_name" class="add_model_name" class="ui-widget-content" style="width:215px;"/></td></tr>';

$(function(){
	$("input.ui-button").button();
	$("#searcharea span.ui-icon,#listarea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	setReferChooser($("#hidden_search_model_type_name"),$("#search_model_referchooser"));
	setReferChooser($("#hidden_search_device_type_name"),$("#search_device_type_referchooser"));

	setReferChooser($("#hidden_add_model_type_name"),$("#add_model_referchooser"));

	/*检索*/
	$("#searchbutton").click(function(){
		findit();
	});
	/*清除*/
	$("#resetbutton").click(function(){
		reset();
	});
	findit();
});

/**检索详细*/
var findit = function(arg) {
	var data = {
		"model_id":$("#hidden_search_model_type_name").val(),
		"device_type_id":$("#hidden_search_device_type_name").val(),
		"model_name":$("#search_model_name").val()
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
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			var listdata = resInfo.peripheralInfectDevices;

			filed_list(listdata);
			showList();
		}
	}catch (e) {};
};


/**清除*/
var reset = function(){
	$("#search_model_type_name").data("post","").val("");
	$("#search_device_type_name").data("post","").val("");
	$("#search_model_name").data("post","").val("");
	$("#hidden_search_model_type_name").data("post","").val("");
	$("#hidden_search_device_type_name").data("post","").val("");
};

/**jqGrid*/
var filed_list=function(listdata){
	if($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#list").jqGrid({
			data:listdata,
			height: 390,
			width: 990,
			rowheight: 23,
			datatype: "local",
			colNames:['','','周边设备机种','周边设备型号','点检组数','点检用设备类别/型号'],
			colModel:[
				{name:'myac',fixed:true,width:40,sortable:false,resize:false,formatter:'actions',formatoptions:{keys:true, editbutton:false}},
				{name:'model_id',index:'model_id',hidden:true,key:true},
				{name:'category_name',index:'category_name',width:80},
				{name:'model_type_name',index:'model_type_name',width:100},
				{name:'seq',index:'seq',align:'right', sorttype:'integer', width:60},
				{name:'device_type_name',index:'device_type_name',width:280}
			],
			rownumbers:true,
			toppager : false,
			rowNum : 17,
			pager : "#listpager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			ondblClickRow : showEdit,
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			viewsortcols : [true, 'vertical', true]
		});
		$("#gbox_list .ui-jqgrid-hbox").before(
			'<div class="ui-widget-content" style="padding:4px;">' +
			'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建关联点检的周边设备" role="button" aria-disabled="false">' +
			'</div>'
		);
		$("input.ui-button").button();
		$("#addbutton").click(showAdd);
	}
};

var showList = function() {
	$("#mainarea").show();
	$("#addarea").hide();
};

var sortSeq = function(trigger){
	if (!trigger.tagName) trigger = this;
	var thisValue = trigger.value;
	var $tr = $(trigger).parent().parent();
	var $toprev = $tr.prevAll().filter(function(){
		return $(this).find("select").val() > thisValue;
	}).last();

	if ($toprev.length > 0) {
		$toprev.before($tr.detach());
	} else {
		var $tonext = $tr.nextAll().not(".addseqTr").filter(function(){
			return $(this).find("select").val() < thisValue;
		}).last();
		if ($tonext.length > 0) {
			$tonext.after($tr.detach());
		}
	}
}

var getSeqTr = function(bean){
	if (bean && bean.length > 0) {
		var $trs = $("");
		for (var i in bean) {
			var en = bean[i];
			var $tr = $(strSeqTr);
			$tr.append("<td><input type='button' class='seqdel' value='×'></td>");
			$tr.find("select").html("<option>01</option><option>02</option><option>03</option><option>04</option><option>05</option><option>06</option><option>07</option><option>08</option><option>09</option><option>10</option><option>11</option><option>12</option><option>13</option><option>14</option><option>15</option><option>16</option><option>17</option><option>18</option><option>19</option><option>20</option>")
				.change(sortSeq)
				.find("option").filter(function(){
					return parseInt($(this).val(), 10) == en.seq;
				}).prop("selected", true);
			$tr.find("input:eq(0)").val(en.device_type_name);
			$tr.find("input:eq(1)").val(en.device_type_id);
			setReferChooser($tr.find("input:eq(1)"),$("#add_device_type_referchooser"));
			$tr.find(".add_model_name").val(en.model_name);
			$trs.after($tr);
		}
		$trs.after(getSeqTr());
		$trs.find(".seqdel").click(function(){$(this).parent().parent().remove()});
		return $trs;
	} else {
		$tr = $(strSeqTr);
		$tr.append("<td><input type='button' class='seqadd' value='＋'></td>");
		$tr.find("select").html("<option></option><option>01</option><option>02</option><option>03</option><option>04</option><option>05</option><option>06</option><option>07</option><option>08</option><option>09</option><option>10</option><option>11</option><option>12</option><option>13</option><option>14</option><option>15</option><option>16</option><option>17</option><option>18</option><option>19</option><option>20</option>");
		setReferChooser($tr.find(".hidden_add_device_type_name"),$("#add_device_type_referchooser"));
		$tr.addClass("addseqTr");
		$tr.find(".seqadd").click(function(){
			$(this).removeClass("seqadd").addClass("seqdel").val('×')
				.unbind("click").click(function(){$tr.remove()});
			$tr.removeClass("addseqTr").after(getSeqTr())
				.find("select").change(sortSeq);
			sortSeq($tr.find("select")[0]);
		});
		return $tr;
	}
}

/*新建页面*/
var showAdd = function() {
	$("#add_model_type_name").val("").enable();
	$(".add_device_type_name").val("");
	$("#hidden_add_model_type_name").val("");
	$(".hidden_add_device_type_name").val("");
	$(".add_model_name").val("");
	$(".errorarea-single").removeClass("errorarea-single");
	$("#mainarea").hide();
	$("#addarea").show();

	$("#addform > table:eq(1) tbody").html(getSeqTr());

	// 前台Validate验证
	$("#addform").validate({
		rules:{
			 model_type_name:{
				 required:true
//      	},
//      	device_type_name:{
//       	    required:true
			}
		}
	});

	$("#savebutton").val("新建").unbind("click");
	$("#savebutton").click(function() {
		doEdit("确认要新建周边设备型号为["+$("#add_model_type_name").val()+"]的记录吗？");
	});

	$("#cancelbutton").unbind("click");
	$("#cancelbutton").click(function() {
		showList();
	});
};

var doEdit = function(message) {

	if ($("#addform").valid()) {
		var data = {
			"model_id" : $("#hidden_add_model_type_name").val()
		};
		var details = $("#addform > table:eq(1) tbody tr").not(".addseqTr");
		if (!details.length) {
			errorPop("请设定至少一组关联设备。");
			return;
		}
		var errorMessage = null;
		details.each(function(idx, ele){
			var $tr = $(ele);
			var seq = $tr.find(".add_seq").val();
			data["device.seq[" + idx + "]"] = seq;
			var device_type_id = $tr.find(".hidden_add_device_type_name").val();
			if (!device_type_id) {
				errorMessage = "请选择点检用设备类别。";
				return;
			}
			data["device.device_type_id[" + idx + "]"] = device_type_id;
			data["device.model_name[" + idx + "]"] = $tr.find(".add_model_name").val() || "";
		});
		if (errorMessage != null) {
			errorPop(errorMessage);
			return;
		}

		warningConfirm(message, function() {
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
				complete : insert_handleComplete
			});
		});
	};
}

/*编辑页面*/
var showEdit = function(id) {
	var data = {
		"model_id" : id
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
		complete : showEditComplete
	});
}

var showEditComplete = function(xhrObj) {
	var resInfo = $.parseJSON(xhrObj.responseText);

	if (!resInfo.peripheralInfectDevices || !resInfo.peripheralInfectDevices.length) {
		return;
	}

	$("#add_model_type_name")
			.val(resInfo.peripheralInfectDevices[0].model_type_name).disable();
	$("#hidden_add_model_type_name")
			.val(resInfo.peripheralInfectDevices[0].model_id);
	$(".errorarea-single").removeClass("errorarea-single");
	$("#mainarea").hide();
	$("#addarea").show();

	$("#addform > table:eq(1) tbody")
			.html(getSeqTr(resInfo.peripheralInfectDevices));

	$("#savebutton").val("提交编辑").unbind("click");
	$("#savebutton").click(function(){
		doEdit("确认要修改记录吗？")
	});

	$("#cancelbutton").unbind("click");
	$("#cancelbutton").click(function() {
		showList();
	});
};

var insert_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#addarea", resInfo.errors);
		} else {
			infoPop("处理已经完成。", null, "更新周边设备点检关系");

			// 重新查询
			findit();
		}
	}catch (e) {};
};

/*删除*/
var showDelete = function(rid) {
	var rowData = $("#list").getRowData(rid);
	var data = {
		"model_id" : rowData.model_id
	};
	warningConfirm("删除不能恢复。确认要删除[" + encodeText(rowData.model_type_name) + "]的记录吗？(与点检设备的关联也一并删除)", function() {
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=dodelete',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : delete_handleComplete
		});
	}, null, "删除确认");
};

var delete_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			infoPop("删除已经完成。", null, "删除");
			findit();
		}
	}catch (e) {};
};

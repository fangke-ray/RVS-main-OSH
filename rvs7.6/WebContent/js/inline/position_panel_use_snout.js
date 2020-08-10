"use strict"
var posUseSnoutObj = (function() {

var pusoHasPcs = false;
var pusoMaterialId = null;
var $sReferChooser = null;
var $usesnoutarea = null;
var $inputSnout = null;
var $scanSnout = null;
var $snoutPaneTds = null;
var $unuseButton = null;
var $insteadDialog = null;

var douse_complete = function(xhrobj) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages("#inlineForm", resInfo.errors);
		if (resInfo.sReferChooser) {
			mySetReferChooser(resInfo.sReferChooser);
		}
		return;
	}

	treatUsesnout(xhrobj);

	// 工程检查票
	if (resInfo.pcses && resInfo.pcses.length > 0 && pusoHasPcs) {
		pcsO.generate(resInfo.pcses);
	}
}

var mySetReferChooser = function(sReferChooser) {
	$sReferChooser.find("tbody").html(sReferChooser);
	var shower = $inputSnout.prev("input:text");
	$sReferChooser.css({"top" : shower.position().top, "left" : shower.position().left - 40}).show("fast");
}

var treatUsesnout = function(xhrobj) {
	var resInfo = null;

	// 以Object形式读取JSON
	var resInfo = $.parseJSON(xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		var isLightFix = false;
		if (resInfo.mform && resInfo.mform.level) {
			var level = resInfo.mform.level;
			isLightFix = f_isLightFix(level);
		}
		if (resInfo.snout_model >= 1 && !isLightFix) {
			setUse(resInfo);

			if (resInfo.leagal_overline) {
				posClockObj.setLeagalAndSpent(resInfo.leagal_overline);
			}
		} else {
			$usesnoutarea.hide();
		}
	}
}

var setUse = function(resInfo){
	$usesnoutarea.show();
	$snoutPaneTds.eq(1).text($("#material_details td:eq(3)").text());

	if (resInfo.snout_model == 2) {
		showUsedSnout(false, resInfo.sReferChooser, resInfo.used_snout);
	} else if (resInfo.used_snout){
		showUsedSnout(true, null, resInfo.used_snout);
	} else {
		showUsedSnout(false, resInfo.sReferChooser);
	}

	$inputSnout.val("").prev().val("");
}

var showUsedSnout = function(switchOn, sReferChooser, used_snout){
	if (switchOn) {
		$snoutPaneTds.eq(2).hide();
		$snoutPaneTds.eq(3).hide();
		$snoutPaneTds.eq(6).hide();
		$snoutPaneTds.eq(7).hide();
		$sReferChooser.hide();

		$unuseButton.show();
	} else {
		$snoutPaneTds.eq(2).show();
		$snoutPaneTds.eq(3).show();
		$snoutPaneTds.eq(6).show();
		$snoutPaneTds.eq(7).show();
		$sReferChooser.show();

		$unuseButton.hide();

		// 关联先端头参照
		if (sReferChooser != null) {
			mySetReferChooser(sReferChooser);
		}
	}

	if (used_snout){
		// 已使用先端头
		$snoutPaneTds.eq(4).show();
		$snoutPaneTds.eq(5).text(used_snout).show();

		$unuseButton.unbind("click").click(function() {
			dounuse(used_snout);
		});
	} else {
		$snoutPaneTds.eq(4).hide();
		$snoutPaneTds.eq(5).text("").hide();
	}
}

var dounuse = function(serial_no) {
	var data = {serial_no : serial_no};
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : "position_panel_snout.do" + '?method=dounusesnout',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : douse_complete
	});
}

var douse = function(serial_no) {
	var data = {serial_no : serial_no};
	// 检查是否第一个
	if (!(serial_no == $sReferChooser.find("tr.firstMatchSnout .referId").text())) {
		warningConfirm("您选择的不是该型号最早完成的先端组件，继续吗？"
		, function(){douse_send(data);}
		);
	} else {
		douse_send(data);
	}
}

var douse_send = function(data) {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : "position_panel_snout.do" + '?method=dousesnout',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : douse_complete
	});
}

var checkSnoutPartial = function(callback) {
	var data = {
		material_id: pusoMaterialId,
		occur_times: 1
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'materialPartial.do?method=getSnouts',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj) {
			edit_snout_Complete(xhrObj, pusoMaterialId, 1, $("#material_details > table td:eq(1)").text(), callback);
		}
	});
}

var edit_snout_Complete = function(xhrObj, material_id, occur_times, sorc_no, callback) {
	var resInfo = $.parseJSON(xhrObj.responseText);

	if (resInfo.errors && resInfo.errors.length > 0) {
		if (resInfo.never_order) {
			warningConfirm("尚未做成零件订单，目前无法替代。");
		} else {
			// 这里是无需替代零件的情况下(error是无可替代零件) 
			callback();
		}
		return;
	}
	setInsteadList(resInfo.Snouts_list);
	$insteadDialog.dialog({
		title : "预制零件替代",
		width : 800,
		show : "blind",
		height : 'auto' ,
		resizable : false,
		modal : true,
		minHeight : 200,
		buttons : {
			"确定":function(){
				$insteadDialog.dialog('close');

				var postData = {material_id:material_id, occur_times:occur_times,
				sorc_no:sorc_no};
				var iii = 0;
				$("#instead_list").find("tr").each(function(idx, ele) {
					
					$input = $(ele).find("input[type=number]");
					if ($input && $input.val()) {
						var ival = $input.val();
						var ilimit = $input.attr("limit");
						var iTotal = $input.attr("total");
						if (ival.match(/^0*$/) == null && ival.match(/^[0-9]*$/) != null) {
							if (ival > ilimit) $input.val(ilimit);
							postData["exchange.quantity[" + iii + "]"] = $input.val();
							postData["exchange.total[" + iii + "]"] = iTotal;
							postData["exchange.material_partial_detail_key[" + iii + "]"] = $(ele).find("td[aria\\-describedby=instead_list_material_partial_detail_key]").text();
							iii ++;
						}
					};
				});
				if (postData["exchange.material_partial_detail_key[0]"] != null) {
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'materialPartial.do?method=doUpdateSnouts',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(){
							callback();
						}
					});
				} else {
					callback();
				}
			},
			"取消": function(){
				$insteadDialog.dialog('close');
			}
		}
	});
	
	$insteadDialog.show();
}


/*jqgrid表格*/
function setInsteadList(instead_list){
	if ($("#gbox_instead_list").length > 0) {
		$("#instead_list").jqGrid().clearGridData();
		$("#instead_list").jqGrid('setGridParam',{data:instead_list}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#instead_list").jqGrid({
			data:instead_list,
			height: 461,
			width: 768,
			rowheight: 23,
			datatype: "local",
			colNames:['','零件编号','零件名称','可签收数量','工位','目前签收状态','签收日期','消耗品'],
			colModel:[
				{name:'material_partial_detail_key',index:'material_partial_detail_key', hidden:true},		   
				{name : 'code',index : 'code',width : 60,align : 'left'},
				{name : 'partial_name',index : 'partial_name',width : 300,align : 'left'},
				{name : 'waiting_quantity',index : 'waiting_quantity',width : 100,align : 'right',formatter:function(r,i,rowData){
					return '<input type="number" value="0" limit="'+r+'" total="'+rowData.quantity+'" > / ' + r;
				}},
				{name :'process_code',index:'process_code',width:60,align:'center' },
				{name :'status',index:'status',width:60,align:'center', formatter:'select', editoptions:{value:"1:未发放;2:已签收无BO;3:BO中;4:BO解决;5:消耗品签收"} },
				{name :'recent_receive_time',index:'recent_receive_time',width:60,align:'center',
						formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
				{name:'append',index:'append', hidden:true}		   
			 ],
			rowNum: 100,
			toppager: false,
			pager: "#instead_listpager",
			viewrecords: true,
			multiselect: true,
			hidegrid : false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,			
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			onSelectRow : changevalue,
			onSelectAll : changevalue,
			gridComplete:function(){
				changevalue([] ,false);
			}
		});
	}
};

var changevalue = function(rowid,status,e){
	if (rowid instanceof Array) {
		// 全选
		var $inum = $("#instead_list").find("input[type=number]");
		$inum.each(function(){
			if (status) {
				$(this).val($(this).attr("limit"));
			} else {
				$(this).val(0);
			}
		});
	} else {
		var $inum = $("#instead_list").find("tr#"+rowid).find("input[type=number]");
		if (status) {
			$inum.val($inum.attr("limit"));
		} else {
			$inum.val(0);
		}
	}
}

return {
	/** sample : posUseSnoutObj.init(hasPcs, 
	 *	$("#usesnoutarea")
	 *  ); 
	 */
	init : function(hasPcs, $use_snout_container) {
		pusoHasPcs = hasPcs;
		$usesnoutarea = $use_snout_container;
		$sReferChooser = $use_snout_container.find(".referchooser");
		$snoutPaneTds = $use_snout_container.find("#snoutpane td");
		$unuseButton = $use_snout_container.find("#unusesnoutbutton");
		$inputSnout = $use_snout_container.find("#input_snout");
		$scanSnout = $use_snout_container.find("#snout_origin");

		$scanSnout.keypress(function(){
		if (this.value.length === 11) {
			var snout_origin = this.value;
			this.value = "";
			var serial_no = $("#snouts .originId:contains(" + snout_origin + ")").parent().children(".referId").text();
			if (serial_no) checkSnoutPartial(function(){douse(serial_no)}); else errorPop("该先端来源相关的先端头不可使用。");
		}
		});
		$scanSnout.keyup(function(){
		if (this.value.length >= 11) {
			var snout_origin = this.value;
			this.value = "";
			var serial_no = $("#snouts .originId:contains(" + snout_origin + ")").parent().children(".referId").text();
			if (serial_no) checkSnoutPartial(function(){douse(serial_no)}); else errorPop("该先端来源相关的先端头不可使用。");
		}
		});

		$insteadDialog = $use_snout_container.find("#instead_dialog");
		if ($insteadDialog.length == 0) {
			$("body").append('<div id="instead_dialog"><style>#instead_list input[type=number] {text-align:right;width:3em;border: 1px solid darkgray;}</style><table id="instead_list"></table><div id="instead_listpager"></div></div>');
			$insteadDialog = $use_snout_container.find("#instead_dialog");
		}
	},
	getUsesnout : function(material_id) {
		if (!pusoMaterialId) return;
		pusoMaterialId = material_id;

		// 取得可使用先端头信息
		var data = {material_id : pusoMaterialId};
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : "position_panel_snout.do" + '?method=getMaterialUse',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : treatUsesnout
		});
	}
}
})();
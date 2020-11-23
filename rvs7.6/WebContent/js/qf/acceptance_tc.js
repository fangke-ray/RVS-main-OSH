$(function() {

	$("#tcStorageButton").click(function() {
		// showStoragePlan in turnover_case_common.js
		afObj.applyProcess(106, this, showStoragePlan, arguments);
	});

	$("#tcPrintButton").click(showTcPrintDialog);
});

var showTcPrintDialog = function(){
	$.ajax({
		data : null,
		url: servicePath + "?method=getTcLoad",
		async: true, 
		dataType : "json",
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		type : "post",
		complete : tcLoadShow
	});
}

var tcLoadShow = function(xhrObj) {
	var resInfo = $.parseJSON(xhrObj.responseText);

	if (resInfo.errors && resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);

		return;
	}

	var linkTrolley = "widgets/qf/turnover_case_preassign.jsp";
	var $to_trolley = $("#to_trolley");
	if ($to_trolley.length == 0) {
		$("body").append("<div id='to_trolley'></div>")
		$to_trolley = $("#to_trolley");
	}
	$to_trolley.hide();
	$to_trolley.load(linkTrolley, function(responseText, textStatus, XMLHttpRequest) {
		$to_trolley.find(".ui-button").button();

		$to_trolley.find("#tcpt_formal input[type=number]").val(resInfo.iPrePrintFormal || 0);
		$to_trolley.find("#tcpt_endoeye input[type=number]").val(resInfo.iPrePrintEndoeye || 0);
		$to_trolley.find("#tcpt_udi input[type=number]").val(resInfo.iPrePrintUdi || 0);

		tcSet($to_trolley.find("#tcpt_formal"), resInfo.nextLocations);
		tcSet($to_trolley.find("#tcpt_endoeye"), resInfo.nextEndoeyeLocations);
		tcSet($to_trolley.find("#tcpt_udi"), resInfo.nextUdiLocations);

		$to_trolley.find(".click_all").click(
			function(){
				if($(this).is(":checked")) {
					$(this).parent().find("input:checkbox[id]:not(:checked)").attr("checked", true);
				} else {
					$(this).parent().find("input:checkbox[id]:checked").removeAttr("checked");
				}
			}
		);

		$to_trolley.find(".print").click(tcPreprint);

		$to_trolley.find(".regain").click(tcRegain);

		$to_trolley.dialog({
			title : "通箱库位预打印",
			width : '88%',
			resizable : false,
			modal : true,
			minHeight : 280,
			buttons : {
				"关闭" : function(){ $to_trolley.dialog("close"); }
			}
		});
	});
}

var tcSet = function($tcpt, nextLocations){
	for (var i in nextLocations) {
		var nextLocation = nextLocations[i];
		$tcpt.append("<div><input type='checkbox' id='cb_"+nextLocation.location+"' " + 
			(nextLocation.execute ? "printed" : "checked") + "><label for='cb_"+nextLocation.location+"'>"+ nextLocation.location + "</label></div>");
	}
}

var tcRegain = function() {
	var $button = $(this);
	var $prelist = $button.closest(".turnover_case_prelist");

	var kind = $prelist.attr("id").substring(5);
	var num = $prelist.find("input[type=number]").val();
	var chkNum = false;
	if (!num || isNaN(num)) {
		chkNum = true;
	} else {
		num = parseInt(num);
		if (num <=0 || num > 99) {
			chkNum = true;
		}
	}

	if (chkNum) {
		errorPop("请输入正确的取得数量。");
		return;
	}

	$.ajax({
		data : {'kind' : kind, 'count': num},
		url: servicePath + "?method=regainTcLabels",
		async: true, 
		dataType : "json",
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		type : "post",
		complete : function(xhrObj) {
			regainTcLabelsResponse($prelist, xhrObj);
		}
	});
}

var regainTcLabelsResponse = function($prelist, xhrObj) {
	var resInfo = $.parseJSON(xhrObj.responseText);

	if (resInfo.errors && resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);

		return;
	}

	if (resInfo.nextLocations) {
		$prelist.children("div").remove();

		tcSet($prelist, resInfo.nextLocations);
	}
}

var tcPreprint = function(){
	var $printList = $(this).closest(".turnover_case_prelist").find("input:checkbox[id]:checked");
	var printList = $printList.map(function(){return this.id.substring(3)}).get().join(";");
	if (!printList.length) {
		errorPop("未选择库位号码。");
	}

	$.ajax({
		data : {'labels' : printList},
		url: servicePath + "?method=doPrintTcLabels",
		async: false, 
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		dataType : "json",
		type : "post",
		complete : function(xhrObj){
			tcLabelsDonwload(xhrObj);
			$printList.attr({"printed": true, "checked": false});
		}
	});
}

var tcLabelsDonwload = function(xhrObj){
	var resInfo = $.parseJSON(xhrObj.responseText);
	if (resInfo.passMessage) {
		infoPop(resInfo.passMessage);
	}
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do"+"?method=output&fileName=preCodes.pdf&filePath=" + resInfo.path);
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do"+"?method=output&fileName=preCodes.pdf&filePath=" + resInfo.path;
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}
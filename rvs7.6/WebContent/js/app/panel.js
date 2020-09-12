/** 服务器处理路径 */
var servicePath = "panel.do";

var checkedPosition = "";
var checkedSection = "";
var checkedLine = "";
var dontTrigger = false;

$(function() {
//	$("#body-rgt").load("./messages.html",
//		function(responseText, textStatus, XMLHttpRequest) {
//	});

	$(".ui-button").button();
	$("#positions, #sections, #lines, #privacies").buttonset();

	if (typeof (WebSocket) == "undefined") {
		$("#oldbrowser").show();
	}

	$("#positions").find("input").click(function() {
		if (dontTrigger) {
			dontTrigger = false;
		} else {
			if (this.value == checkedPosition) return;
			changePosition(this.value);
		}
	})

	$("#sections").find("input").click(function() {
		if (dontTrigger) {
			dontTrigger = false;
		} else {
			if (this.value == checkedSection) return;
			changeSection(this.value);
		}
	})

	$("#lines").find("input").click(function() {
		if (dontTrigger) {
			dontTrigger = false;
		} else {
			if (this.value == checkedLine) return;
			changeLine(this.value);
		}
	})

	checkedPosition = $("#positions").find("input:checked").val();
	checkedSection = $("#sections").find("input:checked").val();
	checkedLine = $("#lines").find("input:checked").val();
});

var changePosition_complete = function(xhrobj) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
			dontTrigger = true;
			$("#positions").find("input[value="+ checkedPosition +"]").trigger("click");
			$("#sections").find("input[value="+ checkedSection +"]").trigger("click");
			$("#lines").find("input[value="+ checkedLine +"]").trigger("click");

		} else {
			var sName = $("#sections").find("input:checked").length > 0 ?
				$("#sections").find("input:checked").next("label").children("span").children("span").text()
				: "";
			var lName = $("#lines").find("input:checked").next("label").children("span").children("span").text();
			var pName = $("#positions").find("input:checked").next("label").children("span").children("span").text();
			$("#userPosition").text("您在" + sName + " " + (pName ? pName + "工位" : lName) + "。");
			checkedPosition = $("#positions").find("input:checked").val();
			checkedSection = $("#sections").find("input:checked").val();
			checkedLine = $("#lines").find("input:checked").val();

			// 可能影响菜单，需即时刷新
			$("#body-lft").load("appmenu.do?t=" + new Date().getMilliseconds(),
				function(responseText, textStatus, XMLHttpRequest) {
			});
		}
	} catch (e) {
		console.log("panel's name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};	
}

var changePosition = function(checked_position_id) {
	var data = {
		position_id : checked_position_id
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=changeposition',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : changePosition_complete
	});
}

var changeSection = function(checked_section_id) {
	var data = {
		section_id : checked_section_id
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=changeposition',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : changePosition_complete
	});

	if ("00000000012" == checked_section_id) {
		$("#lines [for=line_00000000014] span").text("维修工程");
	} else {
		$("#lines [for=line_00000000014] span").text("总组工程");
	}
}

var changeLine = function(checked_line_id) {
	var data = {
		line_id : checked_line_id
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=changeposition',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : changePosition_complete
	});
}
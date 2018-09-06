var servicePath = "qaStatictis.do";

var resetMOptions = function(mOptions, sMonth, yearMonthValue, select_month) {
	var jselect_month = $("#select_month");
	jselect_month.html(mOptions);
	jselect_month.next("div").remove();
	jselect_month.select2Buttons();
	jselect_month.val(select_month).trigger("change");
	if(sMonth) $("#sMonth").text(sMonth);
	if(yearMonthValue) $("#year_month").val(yearMonthValue);
}

var showStatictisData = function(retData) {
	var tbodyContent = "";
	for (var idx in retData.tLines) {
		var tLine = retData.tLines[idx];
		var axisText = tLine.axisText;
		if (axisText.indexOf("月") >= 0) {
			tbodyContent += '<tr class="ui-widget-content jqgrow ui-row-ltr">' 
				+ '<td style="text-align:center;"">'+ axisText +'</td>'
				+ '<td style="text-align:right;"">'+ tLine.process_count +'</td>'
				+ '<td style="text-align:right;"">'+ tLine.fail_count +'</td>'
				+ '<td style="text-align:right;"">'+ ((tLine.process_count > 0) ? ((1 - (tLine.fail_count / tLine.process_count)) * 100).toFixed(2) + "%" : ' - ') +'</td>'
				+ '<td style="text-align:right;"">'+ tLine.targetRate +'</td>'
				+ '</tr>'
		} else {
			tbodyContent += '<tr class="ui-widget-content jqgrow ui-row-ltr">' 
				+ '<td style="text-align:center;"">'+ axisText +'</td>'
				+ '<td style="text-align:right;""><input style="text-align:right;" value="'+ tLine.process_count +'"/></td>'
				+ '<td style="text-align:right;""><input style="text-align:right;" value="'+ tLine.fail_count + '"/></td>'
				+ '<td style="text-align:right;"">'+ (tLine.process_count > 0 ? ((1 - (tLine.fail_count / tLine.process_count)) * 100).toFixed(2) + "%"  : ' - ') +'</td>'
				+ '<td style="text-align:right;"">'+ tLine.targetRate +
				'<input type="hidden" value="'+ tLine.start_date +'" /><input type="hidden" value="'+ tLine.end_date +'" /></td>'
				+ '</tr>'
		}
	}
	$("#edit_value > table > tbody").html(tbodyContent);
}

var setMonthData = function(select_year, select_month) {
	var reqdata = {
		select_year : select_year,
		select_month : select_month
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doSetMonthData',
		cache : false,
		data : reqdata,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo;
			try{
				eval("resInfo=" + xhrobj.responseText);
				resetMOptions(resInfo.mOptions, resInfo.sMonth, resInfo.yearMonthValue, select_month);
				showStatictisData(resInfo.retData);
			} catch(e) {
				
			}
		}
	});
}

var saveMonth = function(){
	// 检查输入内容
	var isAllDigit = true;
	$("#edit_value > table input").each(function(idx){
		var inputValue = this.value
		if (inputValue == null || inputValue == "" || !/^[0-9]+/.test(inputValue)) {
			isAllDigit = false;
		}
	});

	if (isAllDigit) {
		// 提交
		var reqData = {year_month : $("#year_month").val()};
		$("#edit_value > table tr:has('input')").each(function(idx){
			var jthis = $(this);
			reqData["weeks.process_count["+ idx +"]"] = jthis.find("input:eq(0)").val();
			reqData["weeks.fail_count["+ idx +"]"] = jthis.find("input:eq(1)").val();
			reqData["weeks.start_date["+ idx +"]"] = jthis.find("input:eq(2)").val();
			reqData["weeks.end_date["+ idx +"]"] = jthis.find("input:eq(3)").val();
		});

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doUpdate',
			cache : false,
			data : reqData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				var resInfo;
				try{
					$("#complete_dialog").text("数据更新成功！");
					$("#complete_dialog").dialog({
						resizable : false,
						modal : true,
						title : "更新成功确认",
						buttons : {
							"确认" : function() {
								$(this).dialog("close");
							}
						}
					});
				} catch(e) {
				}
			}
		});
		
	}

}



$(document).ready(function() {
	$("#selectform select").select2Buttons();
	$(".ui-button").button();
	// 前台Validate设定
	$("#selectform").validate({
		rules : {
			year : {
				required : true
			},
			month : {
				required : true
			}
		}
	});

	$("#select_year").find("option:eq(2)").attr("selected", true)
		.trigger("change");
	$("#select_year").change(function(){
		if (this.value != "") {
			var reqdata = {select_year : this.value};
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=changeYear',
				cache : false,
				data : reqdata,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj) {
					var resInfo;
					try{
						eval("resInfo=" + xhrobj.responseText);
						resetMOptions(resInfo.mOptions, null, $("#select_month").val());
					} catch(e) {
					}
				}
			});
		}
	});

	$("#selectbutton").click(function(){
		if ($("#selectform").valid()) {
			setMonthData($("#select_year").val(), $("#select_month").val());			
		}
	});

	$("#savebutton").click(function(){
		saveMonth();
	});

	setMonthData();	
});

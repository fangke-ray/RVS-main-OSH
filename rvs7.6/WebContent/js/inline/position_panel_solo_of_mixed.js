var doStartComp=function(evt, chosedData){

	// 作业等待时间
	if ($(".opd_re_comment").length > 0) {
		var pause_start_time = new Date($(".opd_re_comment").attr("pause_start_time"));
		var leak = $(".opd_re_comment").attr("leak");
		if((leak && leak != "0") && !header_today_holiday 
			&& new Date().getTime() - pause_start_time.getTime() > 120000) {
			errorPop("之前的暂停时间没有填写作业或等待类别，请先填写后开始作业。");
			return;
		}
	}

	var data = {
		model_id : $("#input_model_id").val(),
		model_name : toLabelValue($("#input_model_id")),
		serial_no : $("#input_snout_no").val()
	}
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePathMixed + '?method=dostart',
		cache : false,
		data : chosedData || data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStartComp_ajaxSuccess
	});
};

var doStartComp_ajaxSuccess = function(xhrobj, textStatus, postData){
	var resInfo = $.parseJSON(xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		var error1 = resInfo.errors[0];
		if (error1.errcode === "info.dryingJob.finishDryingProcess") {
			warningConfirm(error1.errmsg, function() {
				postData.confirmed = true;
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : servicePathMixed + '?method=dostart',
					cache : false,
					data : chosedData || data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : doStartComp_ajaxSuccess
				});
			});
		} else if (resInfo.infectString) {
			showBreakOfInfect(resInfo.infectString);
			return;
		} else {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		}
	} else {
		$("#hidden_workstauts").val(resInfo.workstauts);
		if (resInfo.workstauts == 1 || resInfo.workstauts == 4) {
			treatStart(resInfo);
			showFoundry();
			if (resInfo.mform) {
				getJustWorkingFingers(null, resInfo.mform.model_id, resInfo.mform.serial_no);
			}
		} else if (resInfo.workstauts == 3.9) {
			doFinish();
		}
		dryProcesses = getDryProcessesTable(resInfo.dryProcesses);
	}
};

var setCompInstore = function($instore, stock_code){
	var $printTicketer = $instore.find("input:button").button().eq(1);
	if (!stock_code) $printTicketer.disable();
	
	var $printSheet = $instore.find("input:button").button().eq(2);
	if (!stock_code) $printSheet.disable();

	$instore.find("label").addClass("instockCode").text(stock_code || "");
	$instore.find("input:button").eq(0).click(showNSMap);

	$printTicketer.click(printNSLabelTicket);
	$printTicketer.click(printNSInfoSheet);
}

var showNSMap=function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'component_manage.do?method=getNSempty',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				if ($("#ns_pop").length == 0) {
					$("body").append("<div id='ns_pop'></div>");
				}
				var $ns_pop= $("#ns_pop").hide();
				
				$ns_pop.load("widgets/partial/ns_map.jsp", function(responseText, textStatus, XMLHttpRequest) {
					$(".wip-table:eq(1)").addClass("close");
					 //新增
			
					$ns_pop.dialog({
						position : [ 800, 20 ],
						title : "NS子零件入库选择",
						width : 1000,
						show: "blind",
						height : 'auto' ,
						resizable : false,
						modal : true,
						minHeight : 200,
						buttons : {}
					});

					var model_name = $("#material_details td:eq(3)").text();

					$ns_pop.find(".ui-widget-content .wip-table").each(function(){
						var $model_match = $(this).find(".model_indicate:contains(" + model_name + ")");
						if ($model_match.length > 1) {
							var matchEqaul = false;
							$model_match.each(function(){
								if (matchEqaul) return;
								var modelIndis = this.innerText.split("|");
								for (var idx in modelIndis) {
									if (modelIndis[idx] === model_name) {
										matchEqaul = true;
										$model_match = $(this);
										break;
									}
								}
							});
							if (!matchEqaul) $model_match = $model_match.eq(0);
						}

						$model_match.addClass("model_match");
					});

					$ns_pop.find("td").addClass("wip-empty");
					for (var iheap in resInfo.heaps) {
						$ns_pop.find("td[nsid="+resInfo.heaps[iheap]+"]").removeClass("wip-empty").addClass("ui-storage-highlight wip-heaped");
					}

					//$("#wip_pop").css("cursor", "pointer");
					$ns_pop.find(".ui-widget-content .wip-table").not(".close").click(function(e){
						if ("TD" == e.target.tagName) {
							if (!$(e.target).hasClass("wip-heaped")) {
								selnsid = $(e.target).attr("nsid");
								if (selnsid) {
									$("#flowtext .instockCode").text(selnsid || "");
									updateStockcode(selnsid);
								}
							}
						}
					});

					$ns_pop.show();
				});
			}
		}
	});
};

var updateStockcode = function(stock_code){
	var postData = {
		serial_no : $("#material_details td:eq(5)").text(),
		stock_code : stock_code
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'component_manage.do?method=doUpdateStock',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			$("#flowtext input:button").enable();
			$("#ns_pop").dialog("close");
		}
	});
}

var printNSLabelTicket = function() {
	var model_name = $("#material_details td:eq(3)").text();
	var serial_no = $("#material_details td:eq(5)").text();
	
	var data = {
		"serial_no" : serial_no
	};

	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: 'component_manage.do?method=printNSLabelTicket', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete:  function(xhrobj){
			var resInfo = $.parseJSON(xhrobj.responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				var iframe = document.createElement("iframe");
	            iframe.src = "download.do"+"?method=output&fileName="+ model_name + "-" + serial_no +"-label.pdf&filePath=" + resInfo.tempFile;
	            iframe.style.display = "none";
	            document.body.appendChild(iframe);
			}
		}
	});
};

var printNSInfoSheet = function() {
	var model_name = $("#material_details td:eq(3)").text();
	var serial_no = $("#material_details td:eq(5)").text();
	
	var data = {
		"serial_no" : serial_no
	};

	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: 'component_manage.do?method=printNSInfoTicket', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete:  function(xhrobj){
			var resInfo = $.parseJSON(xhrobj.responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				var iframe = document.createElement("iframe");
	            iframe.src = "download.do"+"?method=output&fileName="+ model_name + "-" + serial_no +"-sheet.pdf&filePath=" + resInfo.tempFile;
	            iframe.style.display = "none";
	            document.body.appendChild(iframe);
			}
		}
	});
};

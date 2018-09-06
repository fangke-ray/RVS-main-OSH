var servicePath ="work_duration.do";
$(function(){
	//按钮点击效果
	$("input.ui-button").button();
	
	$("#searchform select").select2Buttons();

	$("#search_action_date").datepicker();

	var timeAxis = function(evt) {
		// alert("222");
		var pageY = evt.clientY + (window.scrollY || document.documentElement.scrollTop)-document.documentElement.clientTop - abY - 8;
		if (pageY > 560) pageY = 560;
		if (pageY < 0) pageY = 0;
		
		$("#time_axis").css("top", pageY)
			.attr("minute", minuteFormat(pageY));
	}
	var minuteFormat = function(minute) {
		var hm = 1080 - minute;
		return fillZero(parseInt(hm / 60)) + ":" + fillZero(hm % 60);
	}

	$("#axis_base").mousemove(timeAxis);
	var abY = parseInt($("#axis_base").position().top + $("#axis_base").parent().position().top);

	var clearContent=function(){
		$("#search_section_id").val("").trigger("change");
		$("#search_line_id").val("").trigger("change");
		$("#search_action_date").val("");
	};
	$("#resetbutton").click(clearContent);
	
	var doSearch = function() {
		// 读取已记录检索条件提交给后台
		var data = {
			"section_id" : $("#search_section_id").val(),
			"line_id" : $("#search_line_id").val(),
			"action_date" : $("#search_action_date").val()
		}
	
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
	var search_handleComplete = function(xhjObj) {
		var resInfo = $.parseJSON(xhjObj.responseText);
		if (resInfo.errors && resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#editarea", resInfo.errors);
		} else {
			showChart(resInfo);
		}
	};
	var showChart = function(resInfo) {
		var $y_columns = $(".y_columns .operator_flex").detach();
		$y_columns.html("");

		var pfs = resInfo.productionFeatures;
		var foundries = resInfo.foundryList;

		var dirxTime = {};

		var workStart = 525;
		var elapse = 435 + 50 + 10 + 10;
		var off_work = elapse + 30;

		for (var ipf in pfs) {
			var pf = pfs[ipf];

			if (pf.action_time > off_work) {
				continue;
			}

			if (pf.spare_minutes) {
				var endTime = parseInt(pf.action_time) + parseInt(pf.spare_minutes);

				if (endTime > off_work) {
					pf.spare_minutes = off_work - pf.action_time;
					endTime = off_work;
				}
				var $item = $('<div class="production_feature" d_type="' + pf.d_type + '" style="bottom:' + 
					pf.action_time + 'px;height:' + pf.spare_minutes + 'px;"' + '>' + 
					(pf.finish ? "<div class='count_no'>" + getPositionCountNo(pf.job_no) + "</div>" : "") +
					'</div>');
				var $y_column = $y_columns.children(".y_column[for=" + pf.job_no + "]");
				if ($y_column.length == 0) {
					$y_column = $("<div class=\"y_column\" " + getWorkCountAs(pf.work_count_flg) +
							"for=\"" + pf.job_no + "\"><div class=\"position_intro\">" + pf.operator_name + "</div></div>");
					$y_columns.append($y_column);
				}
				$y_column.append($item);

				// 建立用户的分钟状态
				if (dirxTime[pf.job_no] == null) {
					var dirxTimeOfJobNo = {};
					for (var ix = 1; ix <= elapse; ix++) {
						dirxTimeOfJobNo[ix] = 0;
					}
					for (var ix = 406; ix <= 415; ix++) {
						dirxTimeOfJobNo[ix] = null;
					}
					for (var ix = 191; ix <= 240; ix++) {
						dirxTimeOfJobNo[ix] = null;
					}
					for (var ix = 136; ix <= 145; ix++) {
						dirxTimeOfJobNo[ix] = null;
					}
					dirxTime[pf.job_no] = dirxTimeOfJobNo;
				}

				var dirxTimeOfJobNo = dirxTime[pf.job_no];

				if (pf.d_type == 0 || pf.d_type == 1) {
					for (var ix = parseInt(pf.action_time); ix <= endTime; ix++) {
						if (dirxTimeOfJobNo[ix] == 0) {
							dirxTimeOfJobNo[ix] = 1;
						}
						dirxTime[pf.job_no] = dirxTimeOfJobNo;
					}
				}
			}

			if (pf.unknownFrom) {
				var $pitem = $('<div class="production_feature" d_type="9" style="bottom:' + 
					pf.unknownFrom + 'px;height:' + pf.unknownTime + 'px;"></div>');
				$y_column.append($pitem);
			}
		}

		$y_columns.children().each(function(){
			var $y_column = $(this);
			var job_no = $y_column.attr("for");
			var rate = checkDirxTime(dirxTime[job_no]);
			if ($y_column.is("[leader]")) {
				$y_column.attr("work-rate", "管理人员");
			} else if ($y_column.is("[super]")) {
				$y_column.attr("work-rate", "超级员工");
			} else if ($y_column.is(".y_column_sync")) {
				$y_column.attr("work-rate", rate + "%"); // dirxTime[position]
			} else {
				$y_column.attr("work-rate", rate + "%"); // dirxTime[position]
			}
		});

		for (var ifd in foundries) {
			var foundry = foundries[ifd];
			var startF = foundry.foundryWork - workStart;
			if (startF < 0) startF = 0; 
			var endF = foundry.mainWork - workStart;
			if (endF > off_work) endF = off_work; 
			var lastF = endF - startF;
			var $y_column = $y_columns.children("[for=" + foundry.job_no + "]");
			if ($y_column.length > 0) {
				var $item = $('<div class="foundry_feature" style="bottom:' + 
					startF + 'px;height:' +lastF + 'px;"' + '>' + 
					'</div>');
				$y_column.append($item);
			}
		}

		$(".y_columns").append($y_columns);
	};
	var getWorkCountAs = function(work_count_flg) {
		if (work_count_flg == null || work_count_flg == "null") {
			return "";
		}
		if (work_count_flg == 0 || work_count_flg == 2) {
			return "leader ";
		}
		if (work_count_flg == 4) {
			return "super ";
		}
		return "";
	};
	var checkDirxTime = function(dirxTime){
		if (!dirxTime) return 0;
		var cnt = 0;
		var length = 0;
		for (var item in dirxTime) {
			if (dirxTime[item] == 0) cnt++;
			if (dirxTime[item] != null) length++;
		}
		return ((length - cnt) / length * 100).toFixed(1);
	}

	$("#searchbutton").click(doSearch);
})
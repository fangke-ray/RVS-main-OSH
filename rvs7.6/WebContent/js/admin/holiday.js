/** 模块名 */
var modelname = "节假日调整";
/** 服务器处理路径 */
var servicePath = "holiday.do";

var holidays = [];

var inholidays = function(date) {
	for (var iholidays = 0;iholidays < holidays.length; iholidays++) {
		if (holidays[iholidays] == date) {
			return iholidays;
		}
	}
	return -1;
}

var getMonthHoliday = function(year, month) {
	var data = {month : year + "/" + prevzero(month)};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=getHolidays',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : resetSign
	});
}

var resetSign = function(xhjObj) {
	var resInfo = null;
	try {
		eval("resInfo=" + xhjObj.responseText);

		holidays = resInfo.signed;
	} catch(e) {
	}
}

var prevzero =function(i) {
	if (i < 10) {
		return "0" + i; 
	} else {
		return "" + i; 
	}
}

$(function() {
	$("input.ui-button").button();

	var now = new Date();

	var data = {month : now.getFullYear() + "/" + prevzero(now.getMonth() + 1)};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=getHolidays',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : resetSign
	});

	$("#calendararea").datepicker({
		minDate : "-1m",
		dayNamesMin: ['<span style="color:red;">星期日</span>','星期一','星期二','星期三','星期四','星期五','<span style="color:red;">星期六</span>'],
		prevText: "前月",
		nextText: "后月",
		dateFormat: "d",
		beforeShowDay : function(date) {
			var weekday = date.getDay();
			var thedate = date.getDate();
			var bWeekend = (weekday===0 || weekday===6);
			var bInholidays = (inholidays(thedate) >= 0);
			if ((bWeekend && !bInholidays) || (!bWeekend && bInholidays)) {
				return [true, 'ui-datepicker-holiday'];
			} else {
				return [true, ''];
			}
		},
		onSelect : function(dateText, select) {
			var idate = parseInt(dateText);
			var iInholidays = inholidays(idate);
			if (iInholidays >= 0) {
				holidays.splice(iInholidays, 1);
			} else {
				holidays.push(idate);
			}
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : servicePath + '?method=doupdate',
				cache : false,
				data : {date : select.selectedYear + "/" + prevzero(select.selectedMonth + 1) + "/" + prevzero(select.selectedDay)},
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError
			});
		},
		onChangeMonthYear : getMonthHoliday
	});

	$("#calendararea .ui-datepicker").css("margin", "auto").css("width", "600px");

	$("#recountbutton").click(function(){
		warningConfirm("确实要根据更新的休假日设置修改在线维修品的纳期吗？", doFixScheduledDate);
	});

	var doFixScheduledDate = function() {
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=doFixScheduledDate',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj) {
				var resInfo = $.parseJSON(xhrObj.responseText);
				if (resInfo.accessedMessage) {
					infoPop(resInfo.accessedMessage);
				} else {
					errorPop("并没有维修对象因此纳期改变。");
				}
			}
		});
	}
});


var p_start_at = 0;
var $pause_clock = null;

// 暂停中
var wttime = function(){
	var minute = Math.floor((new Date().getTime() - p_start_at) / 60000);
	if (minute < 0) {
		$pause_clock.text("当日下班");
		$(".opd_re_comment").hide();
	} else {
		$pause_clock.text(minuteFormat(minute));
	}
}

var minuteFormat =function(iminute) {
	if (!iminute) return "-";
	var hours = parseInt(iminute / 60);
	var minutes = iminute % 60;

	return fillZero(hours, 2) + ":" + fillZero(minutes, 2);
}

// 间隔时间计时
var setPauseClock = function(processingPauseStart) {
	var leak = processingPauseStart.leak;
	if ($("#td_of_operator").attr("work_count_flg") != "1") {
		leak = "0";
	}
	$(".opd_re_comment").html(processingPauseStart.comments || "(未设定)")
		.attr({"pause_start_time": processingPauseStart.pause_start_time,
		leak: leak,
		reason: processingPauseStart.reason,
		title: processingPauseStart.comments || ""});
	// 计算暂停用时
	p_start_at = new Date(processingPauseStart.pause_start_time).getTime();
	$pause_clock = $(".opd_re_comment").parent().next();
	wttime();
	wtInterval = setInterval(wttime, 30000);
}

var saveLeakDataCallback = function(data){
	if (!data.leak)  {
		$('.opd_re_comment').attr({leak: 0,
				reason: data.reason, title: data.full_comments})
			.text(data.full_comments);
	} else {
		var leak = "1";
		if ($("#td_of_operator").attr("work_count_flg") != "1") {
			leak = "0";
		}
		$('.opd_re_comment').attr({leak: leak,
				reason: null, title: null, pause_start_time: formatDatetime(new Date())})
			.text("(未设定)");
		p_start_at = new Date().getTime();
		wttime();
	}
}

var formatDatetime = function(d){
	return d.getFullYear() + "/" +　fillZero(d.getMonth() + 1) + "/" + fillZero(d.getDate())
		+ " " + fillZero(d.getHours()) + ":" + fillZero(d.getMinutes()) + ":" + fillZero(d.getSeconds());
}
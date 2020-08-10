"use strict"
var posClockObj = (function() {




// 已启动作业时间
var p_time = 0;
// 定时处理对象
var oInterval, ttInterval;
// 定时处理间隔（1分钟）
var iInterval = 60000;
// 取到的标准作业时间
var leagal_overline;
var t_operator_cost = 0;
var t_run_cost = 0;

var $material_detail_action = null;
var $material_detail_standard = null;
var $material_detail_spend = null;
var $material_detail_spend_lbl = null;
var $p_rate = null;

// 进行中效果
var _ctime=function(){
	p_time++;
	$material_detail_spend_lbl.text(_minuteFormat(p_time));

	var rate = parseInt((p_time + 1) / leagal_overline * 100);

	if (rate >= 100) rate = 99;

	t_operator_cost++;

	var $liquid = $p_rate.find("div");

	var $resets = $(".roll_cell > .anim_act");
	if ($resets.length > 0) {
		if (!$resets.eq(0).css("top") != "0px") {
			$resets.removeClass("anim_act");
			setTimeout(function(){$resets.addClass("anim_act");} , 4);
		}
	}

	if ($liquid.css("width") != "99%") {
		$liquid.css({width : rate + "%"});
	}

	if (rate == 99) {
		$liquid.addClass("tube-orange");
	} else {
		dyeLiquid(rate, $liquid);
	}
};

var dyeLiquid = function(rate, $liquid) {
	if (rate > 80) {
		$liquid.removeClass("tube-green");
		if (rate > 95) {
			$liquid.removeClass("tube-yellow");
			$liquid.addClass("tube-orange");
		} else {
			$liquid.addClass("tube-yellow");
		}
	} else {
		$liquid.addClass("tube-green");
	}
}

// 进行中效果
var _ttime=function(){
	$("#p_run_cost").text(_minuteFormat(t_run_cost));
	t_run_cost++;
};

var _minuteFormat = function(iminute) {
	if (!iminute && iminute != 0) return "-";
	var hours = parseInt(iminute / 60);
	var minutes = iminute % 60;

	return fillZero(hours, 2) + ":" + fillZero(minutes, 2);
}

var _convertMinute =function(sminute) {
	if(sminute.endsWith(":")) sminute = sminute.substring(0, sminute.length - 1);

	var hours = sminute.replace(/(.*):(.*)/, "$1");
	var minutes = sminute.replace(/(.*):(.*)/, "$2");

	return hours * 60 + parseInt(minutes);
}


return {
	/** sample : posClockObj.init($("#material_details td:eq(7)"), $("#material_details td:eq(9)"), $("#dtl_process_time"), $("#p_rate")); */
	init : function($action_container, $standard_container, $spend_container, $rate_viewer) {
		$material_detail_action = $action_container;
		$material_detail_standard = $standard_container;
		$material_detail_spend = $spend_container;
		$material_detail_spend_lbl = $material_detail_spend.find("label");
		$p_rate = $rate_viewer;
	},
	setAction : function(action_time){
		if (action_time) {
			$material_detail_action.text(action_time);
		} else {
			var thistime=new Date();
			var hours=thistime.getHours();
			var minutes=thistime.getMinutes();
			var seconds=thistime.getSeconds();

			$material_detail_action.text(fillZero(hours, 2) + ":" + fillZero(minutes, 2) + ":" + fillZero(seconds, 2));
		}
	},
	setLeagalAndSpent : function(param_leagal_overline, spent_mins) {
		leagal_overline = param_leagal_overline;

		$material_detail_standard.text(_minuteFormat(leagal_overline));

		if (!spent_mins) {
			spent_mins = _convertMinute($material_detail_spend_lbl.text()) || 0;
			if (!spent_mins) {
				$material_detail_spend_lbl.text("00:00");
			}
		} else {
			$material_detail_spend_lbl.text(_minuteFormat(spent_mins));
			$(".roll_cell > *").addClass("anim_pause");
		}

		var frate = 0;
		frate = parseInt(spent_mins / leagal_overline * 100);

		if (frate > 99) {
			frate = 99;
		}
		$p_rate.html("<div class='tube-liquid tube-green' style='width:"+ frate +"%;text-align:right;transition-duration:" + (iInterval / 1000) + "s'></div>");
		dyeLiquid(frate, $p_rate.find("div"));
	},
	pauseClock : function(){
		clearInterval(oInterval);
		$(".roll_cell > *").addClass("anim_pause");
	},
	startClock : function(spent_mins, spent_secs){
		p_time = (spent_mins || 0);

		spent_secs = (spent_secs || 0) % 60;
		var remain_secs = 0;
		if (spent_secs == 0) {
			p_time--;
		} else {
			remain_secs = 60 - spent_secs;
		}

		if (spent_secs == 0) {
			$(".roll_cell > *").removeClass("anim_pause").addClass("anim_act");
			_ctime();
			oInterval = setInterval(_ctime, iInterval);
		} else {
			setTimeout(function(){
				$(".roll_cell > *").removeClass("anim_pause").addClass("anim_act");
				_ctime();
				oInterval = setInterval(_ctime, iInterval);
			}, remain_secs * 1000);
		}
	},
	stopClock : function(){
		if ($material_detail_action) $material_detail_action.text("");
		$material_detail_spend_lbl.text("");
		$p_rate.html("");
		p_time = 0;
		clearInterval(oInterval);
		$(".roll_cell > *").removeClass("anim_pause").removeClass("anim_act");
	},
	initTopClock : function() {
		// 计算当前用时
		var p_operator_cost = $("#p_operator_cost").text();
		if (p_operator_cost.indexOf(':') < 0) {
			t_operator_cost = p_operator_cost;
			$("#p_operator_cost").text(_minuteFormat(t_operator_cost));
		}

		// 计算总用时
		var p_run_cost = $("#p_run_cost").text();
		if (p_run_cost.indexOf(':') < 0) {
			if (p_run_cost != "0" && p_run_cost != "") {
				t_run_cost = p_run_cost;
				$("#p_run_cost").text(_minuteFormat(t_run_cost));
				ttInterval = setInterval(_ttime, iInterval);
			}
		}
	},
	recountTopClock : function() {
		var p_operator_cost = $("#p_operator_cost").text();
		t_operator_cost = _convertMinute(p_operator_cost);// + spent_mins;
	}
}

})();
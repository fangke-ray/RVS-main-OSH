<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<style>
	#position_instore {
	float: right;
	margin-right: 2em;
	line-height: 2em;
	background-color: lightyellow;
	padding: 0 .5em;
	border-radius: 0.5em;
	color: orange;
	height: 2em;
	min-width:19em;
	overflow-y:hidden;
	position: relative;
	}
	#position_instore:empty {
		display: none;
	}
	#position_instore > span{
		position: absolute;
		top:-5em;
	}
</style>
<div id="position_instore"></div>

<script type="text/javascript">
var currentShow = 0;
var rerollNotice = function(){
	var $divs = $("#position_instore > span");
	var lenIns = $divs.length;
	if (lenIns == 0) return;
	if (lenIns == 1) {
		$divs.css("top", 0);
		return;
	}
	var $thisShow = null, $prevShow = null;
	if (currentShow >= lenIns) {
		currentShow = 0;
	}
	$thisShow = $divs.eq(currentShow);
	$divs.css("top", "-5em");
	$thisShow.css("top", 0);
	currentShow++;
};
var anmlNotice = function(instorage, anmlNotice){
	warningConfirm("动物实验用维修品【" + instorage.material + "】已经进入【" + instorage.process_code + "】工位仕挂，请相关人员着手作业。");
	if (anmlNotice) {
		$("#position_instore").html(anmlNotice);
	} else {
		$("#position_instore").html("");
	}
	currentShow = 0;
	rerollNotice();
}
setInterval(rerollNotice, 2500);
</script>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>

<% String sMessageType = (String)request.getAttribute("message_type"); %>

<style>
.lightBox {
	position:fixed;
	top:96px;
	background-color:white;
	padding: 0.2em 1em;
	border: 2px solid rgb(147, 195, 205);
	border-radius: .5em;
	opacity: 0.8;
	cursor: pointer;
	z-index:4;
	-moz-user-select:none;
	-webkit-user-select:none;
	user-select:none;
	box-shadow: 0 0 0 2px white;
}
.lightBox {
	right:4px;
}
.lightBox:hover
{
	opacity: 1;
}
.lightBox table {
	border-collapse: collapse;
}
.lightBox.overtime {
	border: 2px solid rgb(255, 128, 0);
}
.lightBox .your {
	background-color: rgb(147, 195, 205);
}
.lightBox.overtime .your {
	background-color: rgb(255, 210, 176);
}
.lightBox.overtime td.overtime {
	background-color: red;
}
.lightBox tbody td,
.noticePositions tbody td {
	padding:0 .3em;
}
.noticePositions {
	float: right;
	color: white;
	padding-right: 2.5em;
	clear:left;
}
.noticePositions li {
	display: inline;
	line-height: 2.5;
	margin-right: 2.5em;
	position: relative;
	cursor: pointer;
}
.noticePositions li:after {
	content : attr(cnt);
	position: absolute;
	bottom: 0;
	left: 100%;
	font-weight: bold;
	font-size: 12px;
}
.noticePositions li[stat='free'] {
	color:lightblue;
}
.noticePositions li[stat='free']:after {
	color:lightblue;
	text-shadow:1px 1px blue, -1px -1px blue;
}
.noticePositions li[stat='over'] {
	color: yellow;
	font-weight:bold;
}
.noticePositions li[stat='over']:after {
	color: yellow;
	text-shadow: 0px 2px darkorange, 0px -2px darkorange;
}
</style>

<div class="lightBox" style="display:none;">
	<table>
		<thead><tr>
			<th colspan="3">等待作业相关中小修</th>
		</tr></thead>
		<tbody></tbody>
	</table>
</div>
<div class="noticePositions"><!-- 关注工位仕挂数 -->
${has_notice}
</div>
<script type="text/javascript">
	var dragged = false;
	var xM = 0;
	var yM = 0;
	var $lightBox = $(".lightBox");
	$lightBox
	.bind("mousedown", function(evt){
		dragged = true;xM = evt.pageX;yM = evt.pageY;} );
	$("body")
	.bind("mouseup", function(){dragged = false} )
	.bind("mousemove", function(evt){
		if (dragged) {
			var xD = evt.pageX - xM;
			var yD = evt.pageY - yM;
			
			$lightBox.css("right",(parseInt($lightBox.css("right")) - xD) + "px");
			$lightBox.css("top",(parseInt($lightBox.css("top")) + yD) + "px");

			xM = evt.pageX;
			yM = evt.pageY;
		}
	} );
	var refreshLightWaiting = function(list){
		if (!list || list.length == 0) {
			$lightBox.hide();
			return;
		} else {
			$lightBox.show();
			var tBodyContent = "";
			for (var idx in list) {
				var item = list[idx];
				if (item.assigned_operator_id) {
					tBodyContent += "<tr class='your'>";
				} else {
					tBodyContent += "<tr>";
				}
				tBodyContent += "<td material_id='" + item.material_id +"'>"+item.omr_notifi_no+"</td><td position_id='" + item.position_id +"'>"+item.process_code+"</td>";
				tBodyContent += checkLWTime(item.in_place_time);
<% if ("le".equals(sMessageType)) {  %>
				var tempAssignedOperatorOption = "";
				var belongsOperatorOptions = "";
				for (var iB in header_belongs) {
					var bOperator = header_belongs[iB];
					var belongsOperatorOption = '<option value="' + bOperator.operator_id + '">' + bOperator.name + '</option>';
					if (bOperator.operator_id == item.assigned_operator_id) {
						if (item.assigned_flg == 0) {
							tempAssignedOperatorOption = '<option value="0">*' + bOperator.name + '</option>';
						} else {
							belongsOperatorOption = '<option value="' + bOperator.operator_id + '" selected>' + bOperator.name + '</option>';
						}
					}
					belongsOperatorOptions += belongsOperatorOption;
				}
				tBodyContent += '<td><select>' + tempAssignedOperatorOption + belongsOperatorOptions + '</select></td>';
<% }  %>
				tBodyContent += "</tr>";
			}
			$lightBox.find("tbody").html(tBodyContent);
<% if ("le".equals(sMessageType)) {  %>
			$lightBox.on("change", "select", function(){
				var bAssignOperatorId = this.value;
				if (bAssignOperatorId == "0") return;
				var $bl_ATr = $(this).parent().parent();
				if(operator_ws) operator_ws.send('assignOperator:{"material_id":"'+$bl_ATr.children().eq(0).attr("material_id")
					+'","position_id":"'+$bl_ATr.children().eq(1).attr("position_id")+'","operator_id":"'+bAssignOperatorId+'"}');
			})
<% }  %>
			if (tBodyContent.indexOf("overtime") >= 0) {
				$lightBox.addClass("overtime");
			} else {
				$lightBox.removeClass("overtime");
			} 
		}
	}
	var checkLWTime = function(wTime) {
		var now = new Date().getTime();
		var wTimeDate = new Date(wTime);
		var wTimelimit = 1200000;
<% if ("le".equals(sMessageType)) {  %>
		wTimelimit = 3600000;
<% }  %>
		if (now - wTime > wTimelimit) {
			return "<td class='overtime'>" + fillZero("" + wTimeDate.getHours()) + ":" + fillZero("" + wTimeDate.getMinutes()) + "</td>";
		} else {
			return "<td>" + fillZero("" + wTimeDate.getHours()) + ":" + fillZero("" + wTimeDate.getMinutes()) + "</td>";
		}
	}
</script>

<script type="text/javascript">
if (typeof getPositionWork === "undefined") loadJs("js/common/change_position.js");

$(".noticePositions li").click(function(){
	getPositionWork(this.getAttribute("position_id"), this.getAttribute("px") || "0");
});
var refreshNotice = function(list){
	$(".noticePositions li").attr({"cnt": 0, "stat": 'free'});

	if (!list || list.length == 0) {
		return;
	}
	var posGroup = {}, overPosGroup = {};
	for (var iL in list) {
		var posData = list[iL];
		var posPx = (!posData.px || posData.px == 0 || posData.px == 2 || posData.px == 7) ? 1 : 2;

		var $pos = $(".noticePositions li[position_id=" + posData.position_id + "][px=" + posPx + "]")
		if ($pos.length == 0) {
			if (posGroup[posData.position_id] == undefined) {
				posGroup[posData.position_id] = 0;
			}
			posGroup[posData.position_id] += posData.cnt;
			if (posData.fix_response) overPosGroup[posData.position_id] = 1;
		} else {
			$pos.attr("cnt", posData.cnt);
			if (posData.cnt == 0) {
				$pos.attr("stat", 'free');
			} else if (posData.fix_response) {
				$pos.attr("stat", 'over');
			} else {
				$pos.removeAttr("stat");
			}
		}
	}

	for (var item in posGroup) {
		var $pos = $(".noticePositions li[position_id=" + item + "]");
		if ($pos.length > 0) {
			$pos.attr("cnt", posGroup[item]);
			if (posGroup[item] == 0) {
				$pos.attr("stat", 'free');
			} else if (overPosGroup[item]) {
				$pos.attr("stat", 'over');
			} else {
				$pos.removeAttr("stat");
			}
		}
	}
}
</script>

<script type="text/javascript">
var anmlNotice = function(instorage){
	warningConfirm("动物实验用维修品【" + instorage.material + "】已经进入【" + instorage.process_code + "】工位仕挂，请相关人员着手作业。");
}
</script>
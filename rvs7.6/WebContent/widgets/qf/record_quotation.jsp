<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<input type="button" class="ui-button" style="float:right;margin-right:4px;" value="报价计划数" id="quotePlanButton" />
<script type="text/javascript">
var quotePlanPop = function(){
	var $quotePlanDialog = $("#quotePlanDialog");
	$quotePlanDialog.dialog({
		title : "记录计划数量",
		resizable : false,
		modal : true,
		buttons : {
			"确定":function(){
				console.log("220");
				var postData = {
					plan_target_1 : $(".plan_target[cell=1]").val() || 0,
					plan_target_2 : $(".plan_target[cell=2]").val() || 0,
					plan_target_3 : $(".plan_target[cell=3]").val() || 0,
					plan_target_13 : $(".plan_target[cell=13]").val() || 0,
					plan_target_4 : $(".plan_target[cell=4]").val() || 0
				};
				console.log(postData);
				$.ajax({
					data : postData,
					url: "beforeLineLeader.do?method=doSetPlanTarget",
					async: false, 
					beforeSend: ajaxRequestType, 
					success: ajaxSuccessCheck, 
					error: ajaxError, 
					type : "post",
					complete : function(xhrObj){
						$quotePlanDialog.dialog("close");
					}
				});
			}, 
			"关闭" : function(){ $quotePlanDialog.dialog("close"); }
		}		
	});
};
$("#quotePlanButton").click(function(){
	$.ajax({
		data : null,
		url: "beforeLineLeader.do?method=getPlanTarget",
		async: false, 
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		type : "post",
		complete : function(xhrObj){
			var resObj = $.parseJSON(xhrObj.responseText);
			if (resObj) {
				var plan_target = resObj.plan_target;
				$(".plan_target[cell=1]").val(plan_target["1"] || 0);
				$(".plan_target[cell=2]").val(plan_target["2"] || 0);
				$(".plan_target[cell=3]").val(plan_target["3"] || 0);
				$(".plan_target[cell=13]").val(plan_target["13"] || 0);
				$(".plan_target[cell=4]").val(plan_target["4"] || 0);
				quotePlanPop();
			}
		}
	});
});
</script>
<div id="quotePlanDialog" style="display:none;">
	<table class="condform">
		<tbody>
			<tr>
				<td class="ui-state-default td-title" rowspan="4">软性镜</td>
				<td class="ui-state-default td-title">QIS/返品</td>
				<td class="td-content" style="width: 240px;">
					<input class="plan_target" cell="1" type="number"></input>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">非直送：中小修</td>
				<td class="td-content" style="width: 240px;">
					<input class="plan_target" cell="2" type="number"></input>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">直送品</td>
				<td class="td-content" style="width: 240px;">
					<input class="plan_target" cell="3" type="number"></input>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">非直送：大修理</td>
				<td class="td-content" style="width: 240px;">
					<input class="plan_target" cell="13" type="number"></input>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">硬性镜</td>
				<td class="ui-state-default td-title">光学试管/ENDOEYE</td>
				<td class="td-content" style="width: 240px;">
					<input class="plan_target" cell="4" type="number"></input>
				</td>
			</tr>
		</tbody>
	</table>
</div>
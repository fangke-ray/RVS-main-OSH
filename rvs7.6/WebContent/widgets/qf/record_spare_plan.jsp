<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<input type="button" class="ui-button" value="备品到货数" id="sparePlanButton" />
<script type="text/javascript">
var sparePlanPop = function(){
	var $sparePlanDialog = $("#sparePlanDialog");
	$sparePlanDialog.dialog({
		title : "记录计划数量",
		resizable : false,
		modal : true,
		buttons : {
			"确定":function(){
				var postData = {
					plan_target : $("#plan_target").val() || 0
				};
				$.ajax({
					data : postData,
					url: "acceptance.do?method=doSetPlanTarget",
					async: false, 
					beforeSend: ajaxRequestType, 
					success: ajaxSuccessCheck, 
					error: ajaxError, 
					type : "post",
					complete : function(xhrObj){
						$sparePlanDialog.dialog("close");
					}
				});
			}, 
			"关闭" : function(){ $sparePlanDialog.dialog("close"); }
		}		
	});
};
$("#sparePlanButton").click(function(){
	$.ajax({
		data : null,
		url: "acceptance.do?method=getPlanTarget",
		async: false, 
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		type : "post",
		complete : function(xhrObj){
			var resObj = $.parseJSON(xhrObj.responseText);
			if (resObj) {
				$("#plan_target").val(resObj.plan_target || 0);
				sparePlanPop();
			}
		}
	});
});
</script>
<div id="sparePlanDialog" style="display:none;">
	<table class="condform">
		<tbody>
			<tr>
				<td class="ui-state-default td-title">今日备品到货数</td>
				<td class="td-content" style="width: 240px;">
					<input id="plan_target" type="number"></input>
				</td>
			</tr>
		</tbody>
	</table>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<style>
#report_of_week td.td-content {
width:80px;
text-align:right;
}
#report_of_week input {
width:4em;
text-align:right;
}
.for_complete {
width:750px;
height:7em;
resize:none;
}
</style>
<!--script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script-->
	<form id="report_of_week">
		<input type="hidden" id="material_id">
		<table class="condform" style="width:100%">
			<thead>
				<tr>
					<th class="ui-state-default td-title" rowspan="2">项目</th>
					<th class="ui-state-default td-title" rowspan="2">目标</th>
					<th class="ui-state-default td-title" colspan="6">实绩</th>
				</tr>
				<tr>
					<th class="ui-state-default td-title">周一</th>
					<th class="ui-state-default td-title">周二</th>
					<th class="ui-state-default td-title">周三</th>
					<th class="ui-state-default td-title">周四</th>
					<th class="ui-state-default td-title">周五</th>
					<th class="ui-state-default td-title">周六/日</th>
				</tr>
			</thead>
			<tbody>
				<tr for="half_period_complete">
					<td class="ui-state-default td-title">147PB出货目标</td>
					<td class="ui-state-default td-subtitle">2952 台</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<tr for="half_period_light_complete">
					<td class="ui-state-default td-title">147PB 11月出货目标</td>
					<td class="ui-state-default td-subtitle">480 台</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<tr for="half_period_peripheral_complete">
					<td class="ui-state-default td-title">147PB 11月出货目标</td>
					<td class="ui-state-default td-subtitle">480 台</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<!--tr for="service_repair_back">
					<td class="ui-state-default td-title">保修期内返品率</td>
					<td class="ui-state-default td-subtitle">≦0.06%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr-->
				<tr for="final_inspect_pass">
					<td class="ui-state-default td-title">最终检查合格率</td>
					<td class="ui-state-default td-subtitle">≧99.98%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<tr for="intime_complete">
					<td class="ui-state-default td-title">大修理5天内纳期遵守比率</td>
					<td class="ui-state-default td-subtitle">≧85%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<tr for="section2_plan_processed">
					<td class="ui-state-default td-title">中小修理2天内纳期遵守比率</td>
					<td class="ui-state-default td-subtitle">≧90%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<tr for="section1_plan_processed">
					<td class="ui-state-default td-title">周边修理4天内纳期遵守比率</td>
					<td class="ui-state-default td-subtitle">≧90%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<tr for="total_plan_processed">
					<td class="ui-state-default td-title">每日生产计划达成率</td>
					<td class="ui-state-default td-subtitle">≧90%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<!-- tr for="ns_regenerate">
					<td class="ui-state-default td-title">NS再生率</td>
					<td class="ui-state-default td-subtitle">≧96%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<tr for="inline_passthrough">
					<td class="ui-state-default td-title">工程内直行率</td>
					<td class="ui-state-default td-subtitle">≧95%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr -->
				<tr for="quotation_lt">
					<td class="ui-state-default td-title">报价周期LT达成率</td>
					<td class="ui-state-default td-subtitle">1天内达成比率≧85%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
				<tr for="direct_quotation_lt">
					<td class="ui-state-default td-title">直送报价周期LT达成率</td>
					<td class="ui-state-default td-subtitle">1天内达成比率≧82%</td>
					<td class="td-content" weekday_index="1"></td>
					<td class="td-content" weekday_index="2"></td>
					<td class="td-content" weekday_index="3"></td>
					<td class="td-content" weekday_index="4"></td>
					<td class="td-content" weekday_index="5"></td>
					<td class="td-content" weekday_index="6"></td>
				</tr>
			</tbody>
		</table>
		<div>
			<div id="tab-comment">
				<label class="ui-widget-header" style="padding: 0 2em;">特记事项</label>
				<input type="radio" name="tab-comment" id="tab-comment_1" value="1"/><label for="tab-comment_1">周一</label>
				<input type="radio" name="tab-comment" id="tab-comment_2" value="2"/><label for="tab-comment_2">周二</label>
				<input type="radio" name="tab-comment" id="tab-comment_3" value="3"/><label for="tab-comment_3">周三</label>
				<input type="radio" name="tab-comment" id="tab-comment_4" value="4"/><label for="tab-comment_4">周四</label>
				<input type="radio" name="tab-comment" id="tab-comment_5" value="5"/><label for="tab-comment_5">周五</label>
				<input type="radio" name="tab-comment" id="tab-comment_6" value="6"/><label for="tab-comment_6">周六/日</label>
			</div>
			<div id="content-comment_1" class="content-comment" weekday_index="1" style="width:100%;">
				<textarea class="for_complete"></textarea>
			</div>
			<div id="content-comment_2" class="content-comment" weekday_index="2" style="width:100%;display:none;">
				<textarea class="for_complete"></textarea>
			</div>
			<div id="content-comment_3" class="content-comment" weekday_index="3" style="width:100%;display:none;">
				<textarea class="for_complete"></textarea>
			</div>
			<div id="content-comment_4" class="content-comment" weekday_index="4" style="width:100%;display:none;">
				<textarea class="for_complete"></textarea>
			</div>
			<div id="content-comment_5" class="content-comment" weekday_index="5" style="width:100%;display:none;">
				<textarea class="for_complete"></textarea>
			</div>
			<div id="content-comment_6" class="content-comment" weekday_index="6" style="width:100%;display:none;">
				<textarea class="for_complete"></textarea>
			</div>
		</div>
		<input type="hidden" id="weekstart" />
		<input type="hidden" id="now_column" />
	</form>
<script type="text/javascript">
		$("#tab-comment").buttonset();
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'scheduleProcessing.do?method=getDayKpiOfWeek',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj, textStatus) {
				try {
			 	var resInfo = $.parseJSON(xhrObj.responseText);
					if (resInfo.errors && resInfo.errors.length > 0) {
						treatBackMessages(null, resInfo.errors);
						return;
					}

					$("#tab-comment input").click(function() {
						var target_id = this.id.replace("tab-", "content-");
						$(".content-comment").hide();
						$("#"+target_id).show();
					});

					$("#weekstart").val(resInfo.weekstart || "");
					$("#now_column").val(resInfo.now_column || "");
// $("#for_complete").val(resInfo.comment || "");
					$("#tab-comment input:eq(" + (resInfo.now_column - 2) + ")").trigger("click");

					$("tr[for=half_period_complete] td:eq(0)").text(resInfo.period + "大修理出货目标");
					$("tr[for=half_period_light_complete] td:eq(0)").text(resInfo.period + "中小修出货目标");
					$("tr[for=half_period_peripheral_complete] td:eq(0)").text(resInfo.period + "周边出货目标");

					$("tr[for=half_period_complete] td:eq(1)").text(resInfo.shippingPlanOfHpHeavy || 0);
					$("tr[for=half_period_light_complete] td:eq(1)").text(resInfo.shippingPlanOfHpLight || 0);
					$("tr[for=half_period_peripheral_complete] td:eq(1)").text(resInfo.shippingPlanOfHpPeripheral || 0);

//					$("tr[for=month_complete] td:eq(0)").text(resInfo.period + " " + resInfo.planMonth + "月出货目标");
//					$("tr[for=month_complete] td:eq(1)").text(resInfo.shippingPlanOfMonth + " 台");

					if (resInfo.dkdList) {
						var lLength = resInfo.dkdList.length;
						for (var ii=0; ii<lLength; ii++) {
							var dkd = resInfo.dkdList[ii];

							$("tr[for=half_period_complete] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.half_period_complete||"") + "' /> 台");
							$("tr[for=half_period_light_complete] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.half_period_light_complete||"") + "' /> 台");
							$("tr[for=half_period_peripheral_complete] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.half_period_peripheral_complete||"") + "' /> 台");
//							$("tr[for=month_complete] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.month_complete||"") + "' /> 台");
							var service_repair_back_rate = "";
							if(dkd.service_repair_back_rate != null) {
								service_repair_back_rate = dkd.service_repair_back_rate;
							}
							$("tr[for=service_repair_back] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + service_repair_back_rate + "' /> %");
							$("tr[for=final_inspect_pass] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.final_inspect_pass_rate||"") + "' /> %");
							$("tr[for=intime_complete] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.intime_complete_rate||"") + "' /> %");
							$("tr[for=total_plan_processed] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.total_plan_processed_rate||"") + "' /> %");
							$("tr[for=section1_plan_processed] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.section1_plan_processed_rate||"") + "' /> %");
							$("tr[for=section2_plan_processed] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.section2_plan_processed_rate||"") + "' /> %");
							$("tr[for=ns_regenerate] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.ns_regenerate_rate||"") + "' /> %");
							$("tr[for=inline_passthrough] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.inline_passthrough_rate||"") + "' /> %");
							$("tr[for=quotation_lt] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.quotation_lt_rate||"") + "' /> %");
							$("tr[for=direct_quotation_lt] td[weekday_index=" + (ii+1) + "]").html("<input type='number' value='" + (dkd.direct_quotation_lt_rate||"") + "' /> %");
							$(".content-comment[weekday_index=" + (ii+1) + "] textarea").val(dkd.comment);
						}
						$("#report_of_week input, #report_of_week textarea").not("[type=radio]").change(function(){
							$(this).attr("changed", "true");
						});
					}
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
			}
		});
</script>
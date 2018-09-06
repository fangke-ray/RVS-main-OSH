<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<div id="show">
	<div style="float:left;">
		<div style="height: 40px; border-bottom:0;" id="simple_tyle">
			<input type="radio" name="answer_in_deadline" id="base_value" class="ui-button ui-corner-up" value="" checked="checked"><label for="base_value" aria-pressed="false">基本</label>
			<input type="radio" name="answer_in_deadline" id="sample_value" class="ui-button ui-corner-up" value=""><label for="sample_value" aria-pressed="false">模拟拉动</label>
		</div>
		
		<div id="base" class="ui-widget-content" style="width:500px;margin-top:5px;height:300px;"> 
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title" style="width:210px;" >型号</td>
					<td class="td-content" style="width:440px;">
						<label id="label_model_name"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content">
						<label id="label_level"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">梯队</td>
					<td class="td-content">
						<label id="label_echelon"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">拉动台数计算结果</td>
					<td class="td-content">
						<label id="label_forecast_result"></label>
					</td>
				<tr>
				<tr>
					<td class="ui-state-default td-title">拉动台数当前设置</td>
					<td class="td-content" colspan="3">
						<input id="input_forecast_setting" type="text" name="forecast_setting" class="ui-widget-content"/>
					</td>
				</tr>
			</table>
		</div>
		
		<div id="sample" class="ui-widget-content"style="display:none;width:500px;margin-top:5px;height:300px;">
			<style>
					.sample .td-content{
						width:auto;
					}
					.position{
						text-align:right;
					}
	
					 .sample input.ui-widget-content{
						width:65px;
					}
			</style>
			<form id="calculate">
				<table class="condform">
					<tbody class="sample">
						<tr>
							<td class="ui-state-default td-title" style="width:50px;">开始日期</td>
							<td class="ui-state-default td-title" style="width:50px;">截止日期</td>
							<td class="ui-state-default td-title">受理数</td>
							<td class="ui-state-default td-title">同意数</td>
							<td class="ui-state-default td-title">修理同意<br>平均比例</td>
							<td class="ui-state-default td-title">周期修理<br>订购量</td>
						</tr>
					</tbody>
				</table>
				<table class="condform">
					<tbody>
					<tr>
						<td class="ui-state-default td-title">波动系数</td>
						<td class="td-content" style="width:60px;">
							<label id="label_coefficient_of_variation"></label>
						</td>
						<td class="ui-state-default td-title"  style="width:200px;">拉动台数计算结果</td>
						<td class="td-content" style="width:60px;">
							<label id="label_forecast_result_calculate"></label>
						</td>
						<td class="ui-state-default td-title">覆盖率</td>
						<td class="td-content" style="width:80px;">
							<input id="label_coverage" style="width:34px;" type="text" class="ui-widget-content" maxlength="3"/>%
						</td>
					</tr>
				</tbody>
				</table>
			</form>
			<div class="ui-widget-header areabase"style="padding-top:8px;margin-top:5px;">
				<div id="executes" style="margin-top:3px;">
					<input type="button" id="calculated" class="ui-button ui-widget ui-state-default ui-corner-all"  value="计算" role="button" style="right:8px;float:right;">
				</div>
			</div> 
		</div>
	</div>
	<div id="container" style="float:right;min-width:450px;height:347px;"></div>
</div>
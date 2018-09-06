<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>

<style>
#light_repair_process .subform td {
	cursor:pointer;
}
#light_repair_process .subform tr.unact:hover td {
	background-color: lightyellow;
}
</style>
<div id="light_repair_process">
	<div style="float:left;width:420px;height:480px;overflow:auto;">
		<table style="width:400px;">
			<tr>
				<td valign="top">
					<table style="width:90%;">
						<thead>
							<tr>
								<td class="ui-state-default td-title">设定维修流程</td>
								<td class="td-content">
									<select name="ref_template" id="ref_template" class="ui-widget-content"></select>
								</td>
							</tr>
						</thead>
					</table>
					<table class="subform" style="width:90%;">
						<thead>
							<tr>
								<th colspan="2" class="ui-state-default td-title">中小修理维修项目</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</td>
			</tr>
		</table>
	</div>
	<div style="height:480px;overflow:auto;">
		<div id="pa_main" class="chartarea ui-corner-all"></div>
	</div>
	<div class="clear"></div>
</div>
<div id="light_repair_record">
<span class="ui-widget-header" style="padding:0 20em 0 2em;">中小修理维修内容选择结果</span>
<div style="overflow-y: auto; max-height: 4em;"/>
</div>
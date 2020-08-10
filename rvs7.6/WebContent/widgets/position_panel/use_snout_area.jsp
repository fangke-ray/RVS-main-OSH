<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
	<span class="areatitle">使用先端组件</span>
</div>
<div class="ui-widget-content dwidth-full" id="select_snout">
	<!--label>使用先端组件可使【先端 1 工程】和【先端 2 工程】各自节省15分钟的直接工时。</label-->
	<table class="condform" id="snoutpane">
		<tbody>
			<tr>
				<td class="ui-state-default td-title">当前维修型号</td>
				<td class="td-content-text"></td>
				<td class="ui-state-default td-title">可使用先端头</td>
				<td class="td-content-text"><input type="text" readonly></input><input type="hidden" name="privacy" id="input_snout"></input></td>
				<td class="ui-state-default td-title">已使用先端头</td>
				<td class="td-content-text"><label type="text" id="used_snouts" /></td>
				<td class="ui-state-default td-title">先端头来源</td>
				<td class="td-content-text"><input type="text" id="snout_origin"></input></td>
			</tr>
		</tbody>
	</table>
	<div style="height: 44px">
		<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="unusesnoutbutton" value="取消使用先端头" role="button" aria-disabled="false" style="float: right; right: 2px;">
	</div>
</div>
<div class="ui-state-default ui-corner-bottom areaencloser dwidth-full"></div>
<div class="referchooser ui-widget-content" tabindex="-1" style="z-index:80;">
	<table class="subform" id="snouts">
	<thead><th class="ui-state-default" style="padding: 0 0.5em;">先端头来源<br>维修编号</th><th class="ui-state-default" style="padding: 0 0.5em;">先端头管理号</th></thead>
	<tbody></tbody></table>
</div>

<div class="clear areaencloser"></div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
.turnover_case_prelist {
	width:33%;
	float:left;
	height:480px;
}
.turnover_case_prelist input[type=number]{
	width:3em;
	text-align:right;
}
.turnover_case_prelist label {
	width: 10em;
	display: inline-block;
}
.turnover_case_prelist .ui-button {
	padding : 0 0.5em;
}
.turnover_case_prelist .regain {
	margin-left: 8em;
}
.turnover_case_prelist input[printed] + label:after {
	content: '已打印';
	padding-left : 1em;
}
</style>
<div>
	<div class="turnover_case_prelist" id="tcpt_formal" title="一般维修品">
		<span>一般维修品<input type="button" class="print ui-button" value="预打印">
		<input class="ui-widget-content" min=1 max=99 type="number"></input>张通箱库位号</span>
		<br>
		<input type=checkbox class="click_all" checked>
		<input type="button" class="regain ui-button" value="重新取得">	
	</div>
	<div class="turnover_case_prelist" id="tcpt_endoeye" title="Endoeye">
		<span>Endoeye <input type="button" class="print ui-button" value="预打印">
		<input class="ui-widget-content" min=1 max=99 type="number"></input>张通箱库位号</span>
		<br>
		<input type=checkbox class="click_all" checked>
		<input type="button" class="regain ui-button" value="重新取得">	
	</div>
	<div class="turnover_case_prelist" id="tcpt_udi" title="UDI">
		<span>光学视管 <input type="button" class="print ui-button" value="预打印">
		<input class="ui-widget-content" min=1 max=99 type="number"></input>张通箱库位号</span>
		<br>
		<input type=checkbox class="click_all" checked>
		<input type="button" class="regain ui-button" value="重新取得">	
	</div>
</div>
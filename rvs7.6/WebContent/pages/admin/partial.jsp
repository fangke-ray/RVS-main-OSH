<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>

<script type="text/javascript" src="js/admin/partial.js"></script>

<title>零件</title>
</head>
<% 
	String role = (String) request.getAttribute("role");
	boolean isOperator = ("operator").equals(role);
%>
<body>
<style>
 .overdue_row{
 background-color:#CCCCCC;
 }
  #standard_dialog input.ui-widget-content{
 width:60px;
 }
 .hidden{
 visibility: hidden;
 }
</style>
<div id="searcharea">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">检索条件</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
	
	<!-- 检索条件 -->
		<form id="searchform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">零件编码</td>
					<td class="td-content"><input type="text" name="code" id="search_code" class="ui-widget-content"/></td>
					<td class="ui-state-default td-title">零件名称</td>
					<td class="td-content"><input type="text" name="name" id="search_name" maxlength="120" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">消耗品</td>
					<td class="td-content" id="search_consumable_flg">
						<input type="radio" name="consumable_flg" alt="消耗品" id="search_consumable_flg_all" value="" class="ui-widget-content" checked/><label for="search_consumable_flg_all" radio>(全部)</label>
						<input type="radio" name="consumable_flg" alt="消耗品" id="search_consumable_flg_yes" value="1" class="ui-widget-content" /><label for="search_consumable_flg_yes" radio>是</label>
						<input type="radio" name="consumable_flg" alt="消耗品" id="search_consumable_flg_no" value="2" class="ui-widget-content" /><label for="search_consumable_flg_no" radio>否</label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">规格种别</td>
					<td class="td-content" colspan="5"><select id="search_spec_kind" class="ui-widget-content">${specKind}</select></td>
				</tr>
			</table>
				<div style="height:44px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				</div>
		</form>
		
	</div>
	<div class="clear dwidth-middleright"></div>
</div>

<div id="editarea" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle"></span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
	
	<!-- 新建编辑页面 -->
		<form id="editform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">零件编码</td>
					<td class="td-content"><input name="code" alt="零件编码" id="edit_code" class="ui-widget-content"/></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">零件名称</td>
					<td class="td-content"><input name="name" alt="零件名称"id="edit_name" class="ui-widget-content"/></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">参考价格</td>
					<td class="td-content"><input name="price" alt="参考价格" id="edit_price"class="ui-widget-content"/></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">最新价格</td>
					<td class="td-content"><label id="label_price"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">规格种别</td>
					<td class="td-content"><select id="edit_spec_kind" class="ui-widget-content">${specKind}</select></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">是否分装</td>
					<td class="td-content" id="edit_unpack">
						<input type="radio" name="unpack" id="edit_unpack_yes" value="1" class="ui-widget-content">
						<label for="edit_unpack_yes" radio>是</label>
						<input type="radio" name="unpack" id="edit_unpack_no" value="0" class="ui-widget-content">
						<label for="edit_unpack_no" radio>否</label>
					</td>
				</tr>
				<tr style="display: none;">
					<td class="ui-state-default td-title">分装数量</td>
					<td class="td-content"><input name="split_quantity" alt="分装数量" id="edit_split_quantity" class="ui-widget-content"/></td>
				</tr>
					<tr>
					<td class="ui-state-default td-title">订购对象</td>
					<td class="td-content" id="edit_order_flg">
						<input type="radio" name="order_flg" alt="订购对象" id="edit_order_flg_yes" value="1" class="ui-widget-content"
<% if (!isOperator) { %> disabled<% } %>>
						<label for="edit_order_flg_yes" radio>是</label>
						<input type="radio" name="order_flg" alt="订购对象" id="edit_order_flg_yes_no" value="0" class="ui-widget-content"
<% if (!isOperator) { %> disabled<% } %>>
						<label for="edit_order_flg_yes_no" radio>否</label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">显示消耗品</td>
					<td class="td-content">
						<label id="edit_consumable_flg"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">最后更新人</td>
					<td class="td-content"><label id="label_update_by"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">最后更新时间</td>
					<td class="td-content"><label id="label_update_time"></label></td>
				</tr>			
			</table>
			<!-- partial_id -->
			<input type="hidden" id="label_partial_id"/>
			<!-- 有效截止日期 history_limit_date-->
			<input type="hidden" id="label_history_limit_date"/>
			<!-- 有效截止时间 avarible_end_date-->
			<input type="hidden" id="edit_avarible_end_date"/>
			

			<div style="height:36px;margin-top:5px;">
<% if (isOperator) { %>
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editbutton" value="新建" role="button" aria-disabled="false" style="float:left;left:4px;">
<% } %>
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="cancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
				<input type="hidden" id="h_value_currency" value="${govalue_currency}"/>
			</div>

			<!-- 第二个Jqgrid -->
			<div id="view_edit_list" class="width-middleright" style="margin:4px;">
			<table id="ext_list"></table>
	        <div id="ext_listpager"></div>
			</div>
		</form>		
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
</div>

<div class="clear areaencloser"></div>

  <input type="hidden" id="hidden_isOperator"  value=${role}>

		<!-- JqGrid表格  -->
		<div id="listarea" class="width-middleright">
			<table id="list"></table>
			<div id="listpager"></div>
			<div class="ui-widget-header areabase"style="width:992px;padding-top:4px;margin-top:0px;">
			      <div id="executes" style="margin-left:4px;margin-top:2px;">
					<input type="button" id="waste_revision_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="改废增" role="button">
				 	<input type="button" id="upload_price_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="零件价格上传">
				  	<input type="button" id="download_price_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="零件价格下载">
				  	<input type="button" id="standard_button" class="ui-button" value="零件入出库工时标准">
				  </div>
			</div>
		</div>
		<input type="hidden" id="hide_grid_spec_kind" value="${gridSpecKind}">
		<div id="confirmmessage"></div>
<div class="clear areaencloser"></div>
	<form id="abandon_modify" style="display:none">
	   <table class="condform">				
					<tr >
						<td class="ui-state-default td-title" style="width:350px">型号</td>
						<td class="td-content" style="width:800px;">
						   <select  id="select_model_name" class="ui-widget-content" name="model_id" alt="型号" multiple></select> 	
						</td>					
					</tr>
					<tr> 
					    <td class="ui-state-default td-title" >零件编码</td>
						<td class="td-content">
							<label id="edit_label_code" name="code"alt="零件编码"></label>
					    </td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">零件名称</td>
						<td class="td-content">
							<label id="edit_label_name" name="name" alt="零件名称"></label>
						</td>					
					</tr>
					<tr>
					    <td class="ui-state-default td-title">操作</td>
						<td class="td-content" id="operation_id">
							<input type="radio" name="operation" id="edit_choose_discontinue" class="ui-widget-content ui-helper-hidden-accessible" value="0" checked="checked"/><label for="edit_choose_discontinue" aria-pressed="false">废止</label>
						    
							<input type="radio" name="operation" id="edit_decided" class="ui-widget-content ui-helper-hidden-accessible" value="1"/><label for="edit_decided" aria-pressed="false">改定</label>
						</td>	
					</tr>
					<tr class="discontinue_edit">
							<td class="ui-state-default td-title">有效时间</td>
							<td class="td-content">
								<input type="text" id="edit_effective_time"readonly="readonly" name="history_limit_date" alt="有效时间" class="ui-widget-content" />
							</td>
						</tr>
					<tr class="decided_edit">
					
						<td class="ui-state-default td-title">改动时间</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content"  id="edit_change_time" readonly="readonly" name="history_limit_date" alt="改动时间" />
						</td>					
					</tr>
					<tr class="decided_edit" style="display:none">
					    <td class="ui-state-default td-title">改动零件</td>
						<td class="td-content">
							<input type="hidden" name="partial_id" id="edit_new_partial_id"/>
							<input type="text" class="ui-widget-content" name="code" alt="改动零件"  id="search_partial_id"/>
						</td>					
					</tr>
			</table>	
			<input type="hidden" id="edit_model_id"/>
			<input type="hidden" id="edit_partial_id"/>
			<input type="hidden" id="edit_position_id"/>
			
	</form>
	<div id="upload_price_dialog" class="ui-widget-content" style="display: none;">
		<form>
			<table class="condform">
				 <tr>
					<td class="ui-state-default td-title">上传文件</td>
					<td class="td-content">
						<input type="file" name="file" id="uploadFile" class="ui-widget-content" />
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="standard_dialog" style="display: none;">
		<div class="ui-widget-content">
			<table class="condform">
				<thead>
					<tr style="height: 30px;">
						<th class="ui-state-default td-title" style="width:200px;">规格种别</th>
						<th class="ui-state-default td-title" style="width:260px;">装箱数量</th>
						<th class="ui-state-default td-title">收货标准工时(分钟/箱)</th>
						<th class="ui-state-default td-title">拆盒标准工时(分钟/盒)</th>
						<th class="ui-state-default td-title" style="width:290px;">核对上架标准工时(分钟/个)</th>
						<th class="ui-state-default td-title" style="width:290px;">下架标准工时(分钟/个)</th>
						<th class="ui-state-default td-title">分装标准工时(分钟/袋)</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>
</body>
</html>

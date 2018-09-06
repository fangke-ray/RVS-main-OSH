<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<style>
#add_regular_torque,#add_deviation,#edit_regular_torque,#edit_deviation,#add_seq,#edit_seq{
	ime-mode: disabled;
}

</style>

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/infect/torsion_device.js"></script>
	
<title>力矩设备画面</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2" />
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px; width: 1266px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=tinit" flush="true">
					<jsp:param name="linkto" value="设备工具/治具信息管理" />
				</jsp:include>
			</div>


<!---------------------start----------------------------->
<!--力矩设备画面开始-->
<div id="body_mdl">
<div id="usage_check_search" style="width: 994px; float: left;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">力矩设备画面</span>
		<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content">
		<form id="searchform" method="POST">
			<table class="condform">
				<tbody>
					 <tr>
						<td class="ui-state-default td-title" style="width: 111px;">管理号码</td>
						<td class="td-content">
						   <input type="text"  name="manage_code" id="search_manage_code" alt="管理号码" class="ui-widget-content"/>
						</td>
						<!-- <td class="ui-state-default td-title"  style="width: 111px;">力矩点检序号</td>
						<td class="td-content">
							<input type="text" name="seq" id="search_seq" alt="力矩点检序号"  class="ui-widget-content">
						</td> -->
						<td class="ui-state-default td-title"  style="width: 111px;">使用的工程</td>
						<td class="td-content">
							<input type="text" name="usage_point" id="search_usage_point" alt="使用的工程"  class="ui-widget-content">
						</td>
						<td class="ui-state-default td-title">HP-10<br>HP-100</td>
						<td class="td-content">
							<select id="search_hp_scale" name="hp_scale" alt="点检设备精度">${sHpScale}</select>
						</td>
					</tr>
				</tbody>
			</table>
			<div style="height:44px">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
				<input id="isLeader" type="hidden" value="${isLeader}"/>
			</div>
		</form>
	</div>

	<div class="clear areaencloser"></div>

	<div id="listarea" class="">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">力矩设备一览</span>
			<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
				<span class="ui-icon ui-icon-circle-triangle-n"></span>
			</a>
		</div>
		<!-- <div class="ui-widget-content" style="padding:4px;">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建力矩设备" role="button" aria-disabled="false">
		</div> -->
		<table id="torsion_device_list"></table>
		<div id="torsion_device_listpager"></div>
		
		<input type="hidden" id="hidden_hp_scale" value="${goHpScale}"/>
	</div>
	<div class="clear"></div>
</div>
</div>
<!--力矩设备一览开始-->


<!--新建力矩设备开始-->
<div id="body_add" style="width: 994px; float: left;display:none" class="ui-widget-content">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">新建力矩设备</span>
		<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	
	<form id="add_form" method="POST">	   
	    <div>
		<table class="condform" style="border:1px solid #aaaaaa;margin-left:2px;">
			<tbody>	
			    <tr>
					<td style="width:153px;" class="ui-state-default td-title">管理编号</td>
					<td class="td-content">
						<input type="text" readonly="readonly" id="add_manage_code" name="manage_id" alt="管理编号"  class="ui-widget-content">
						<input type="hidden" id="hidden_add_manage_id"/>
					</td>
					<td class="ui-state-default td-title">力矩点检序号</td>
					<td class="td-content">
						<input type="text" id="add_seq" name="seq" alt="力矩点检序号" class="ui-widget-content">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">规格力矩值</td>
					<td class="td-content">
						<input type="text" id="add_regular_torque" name="regular_torque" alt="规格力矩值"  class="ui-widget-content">			
					</td>
					<td class="ui-state-default td-title">±</td>
					<td class="td-content">
						<input type="text" id="add_deviation" name="deviation" alt="偏差值"  class="ui-widget-content">	
					</td>
				</tr>					
				<tr>
				    <td class="ui-state-default td-title">使用的工程</td>
					<td class="td-content">
						<input type="text" id="add_usage_point" name="usage_point" alt="使用的工程" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">HP-10<br>HP-100</td>
					<td class="td-content">
						<select id="add_hp_scale" name="hp_scale" alt="HP-10~HP-100">${sHpScale}</select>
					</td>
				</tr>
			</tbody>
		</table>
	    </div>	
		
		<div style="height:44px">
			<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addConfirebutton" value="确认" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
			<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addGoback" value="返回" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
		</div>
	</form>
</div>
<!--新建力矩设备结束-->


<!--修改力矩设备开始-->
<div id="body_edit" style="width: 994px; float: left;display:none" class="ui-widget-content">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">修改力矩设备</span>
		<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	
	<form id="edit_form" method="POST">	   
	    <div>
		<table class="condform" style="border:1px solid #aaaaaa;margin-left:2px;">
			<tbody>	
			    <tr>
					<td style="width:153px;" class="ui-state-default td-title">管理编号</td>
					<td class="td-content">
						<label id="edit_manage_code" name="manage_id" alt="管理编号"></label>
						<input type="hidden" id="hidden_edit_manage_id"/>
					</td>
					<td class="ui-state-default td-title">力矩点检序号</td>
					<td class="td-content">
						<label id="edit_seq" name="seq" alt="力矩点检序号"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">规格力矩值</td>
					<td class="td-content">
						<input type="text" id="edit_regular_torque" name="regular_torque" alt="规格力矩值"  class="ui-widget-content">			
					</td>
					<td class="ui-state-default td-title">±</td>
					<td class="td-content">
						<input type="text" id="edit_deviation" name="deviation" alt="偏差值"  class="ui-widget-content">	
					</td>
				</tr>					
				<tr>
				    <td class="ui-state-default td-title">使用的工程</td>
					<td class="td-content">
						<input type="text" id="edit_usage_point" name="usage_point" alt="使用的工程" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">HP-10<br>HP-100</td>
					<td class="td-content">
						<select id="edit_hp_scale" name="hp_scale" alt="HP-10~HP-100">${eHpScale}</select>
					</td>
				</tr>
				
				<tr>
					<td class="ui-state-default td-title">点检力矩合格下限(N.M)</td>
					<td class="td-content">
						<label  id="edit_regular_torque_lower_limit" name="regular_torque_lower_limit"></label>
					</td>
				    <td class="ui-state-default td-title">点检力矩合格上限(N.M)</td>
					<td class="td-content">
						<label  id="edit_regular_torque_upper_limit" name="regular_torque_upper_limit"></label>
					</td>
				</tr>
			</tbody>
		</table>
	    </div>	
		
		<div style="height:44px">
			<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editConfirebutton" value="确认" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
			<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editGoback" value="返回" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
		</div>
	</form>
</div>
<!--修改力矩设备结束-->


<div id="confirmmessage"></div>
<div class="clear"></div>
<!--双击一栏画面结束-->

<!-- 新建管理编号下拉选择 -->
<div class="referchooser ui-widget-content" id="add_choose_manage_code" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${mReferChooser}</table>
</div>
</div>

<!-- 双击修改管理编号下拉选择 -->
<div class="referchooser ui-widget-content" id="edit_choose_manage_code" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${mReferChooser}</table>
</div>
</div>

<!----------------------end----------------------------->
</div>
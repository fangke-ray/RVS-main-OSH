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

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/partial/partial_assign.js"></script>
<title>零件发放</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		
		<div id="body-2" class="ui-widget-panel ui-corner-all" style="align: center; padding-top: 16px; padding-bottom: 16px;">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件入出库"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				   <span class="areatitle">零件发放导入</span>
				    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<div class="ui-widget-content dwidth-middleright">
					<form id="uploadform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">上传文件</td>
									<td class="td-content"><input type="file" name="file" id="file" class="ui-widget-content"/></td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="uploadbutton" value="载入" role="button" aria-disabled="false" style="float: right; right: 2px">
						</div>
					</form>
				</div>
				
				<div class="clear areaencloser"></div>
				
				<div id="listarea" class="">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">零件发放维修对象一览</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<table id="recept_list"></table>
					<div id="recept_listpager"></div>
				</div>
				
				<div id="functionarea" style="margin:auto;height:44px;">
					<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
						<div id="executes" style="margin-left:4px;margin-top:4px;">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="submitbutton" value="提交" role="button" aria-disabled="false" style="float: right; right: 2px">
						</div>
					</div>
					<div class="clear"></div>
				</div>
				
				<div class="clear areaencloser"></div>
				
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">载入零件入库预定日</span>
					<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n" id="toggle"></span>
					</a>
				</div>
				<div class="ui-widget-content dwidth-middleright">
					<form id="uploadOpdform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">上传文件</td>
									<td class="td-content"><input type="file" name="file" id="opd_file" class="ui-widget-content"/></td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="uploaddatebutton" value="载入" role="button" aria-disabled="false" style="float: right; right: 2px">
						</div>
					</form>
				</div>
			</div>
			
			<div id="body-detail" style="width: 994px; float: left;display:none;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">零件发放</span>
					<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<div class="ui-widget-content" id="main">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">受理日</td>
								<td class="td-content">
									<label id="label_reception_date"></label>
								</td>
								<td class="ui-state-default td-title">客户</td>
								<td class="td-content">
									<label id="label_customer_name"></label>
								</td>
								<td class="ui-state-default td-title">同意日</td>
								<td class="td-content">
									<label id="label_agreed_date"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">修理单号</td>
								<td class="td-content">
									<label id="label_sorc_no"></label>
								</td>
								<td class="ui-state-default td-title">型号</td>
								<td class="td-content">
									<label id="label_model_name"></label>
								</td>
								<td class="ui-state-default td-title">机身号</td>
								<td class="td-content">
									<label id="label_serial_no"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">维修等级</td>
								<td class="td-content">
									<label id="label_level"></label>
								</td>
								<td class="ui-state-default td-title">修理分类</td>
								<td class="td-content">
									<label id="label_service_repair_flg"></label>
								</td>
								<td class="ui-state-default td-title">入库预定日</td>
								<td class="td-content">
									<label id="label_arrival_plan_date"></label>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
				
				<div class="clear areaencloser"></div>
				
				<div id="planned_listarea" class="ui-widget-content">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">零件对象一览</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div style="height: 40px; border-bottom: 0;" id="infoes" >
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="arrive_partial_button" value="12" role="button" checked="true"><label for="arrive_partial_button">本次出库零件</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="unarrive_partial_button" value="" role="button" ><label for="unarrive_partial_button">本次出库以外零件</label>
					</div>
					<div id="unarrive_partial" style="display:none">
						<table id="unarrive_partial_list"></table>
						<div id="unarrive_partial_listpager"></div>
					</div>
					<div id="arrive_partial">
						<table id="arrive_partial_list"></table>
						<div id="arrive_partial_listpager"></div>
					</div>
				</div>
				
				<div id="functionarea" style="margin:auto;height:44px;">
					<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
						<div id="executes" style="margin-left:4px;margin-top:4px;">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="取消" role="button" aria-disabled="false" style="float: right; right: 2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="comfirebutton" value="确定" role="button" aria-disabled="false" style="float: right; right: 2px">
						</div>
						<input type="hidden" id="hide_materialID">
						<input type="hidden" id="hide_occur_times">
					</div>
					<div class="clear"></div>
				</div>
				
			</div>
			
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
	</div>
</body>
</html>
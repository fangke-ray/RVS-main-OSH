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
<style>
tr.addseqTr td {
	background-color:gray;
}
tr.addseqTr select,
tr.addseqTr input {
	background-color:lightgray;
}
</style>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>

<script type="text/javascript" src="js/infect/peripheral_infect_device.js"></script>

<title>周边设备点检关系</title>
</head>
<body class="outer" style="overflow: auto;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=tinit" flush="true">
					<jsp:param name="linkto" value="设备工具/治具信息管理"/>
				</jsp:include>
			</div>
			<div id="body-mdl" style="width: 1012px; float: left;">
				<div id="mainarea" class="dwidth-middleright" style="margin: auto;">
					<div id="searcharea" style="width: 994px; float: left;">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">检索条件</span>
							<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
				        </div>
	
						<div class="ui-widget-content dwidth-middleright">
							<form id="searchform" method="POST">
								<table class="condform">
									<tbody>
										<tr>
											<td class="ui-state-default td-title">周边设备型号</td>
											<td class="td-content">
												<input id="search_model_type_name" name="model_type_name" readonly="readonly" class="ui-widget-content" type="text">
											    <input id="hidden_search_model_type_name" name="model_type_name" type="hidden">
											</td>
											<td class="ui-state-default td-title">点检用设备类别</td>
											<td class="td-content">
												<input id="search_device_type_name" name="device_type_name" readonly="readonly" class="ui-widget-content" type="text">
											    <input id="hidden_search_device_type_name" name="search_device_type_name" type="hidden">
											</td>
											<td class="ui-state-default td-title">点检用设备型号</td>
											<td class="td-content">
												<input id="search_model_name" name="model_name" class="ui-widget-content" type="text">
											</td>
										</tr>
									</tbody>
								</table>
								<div style="height:44px">
									<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
									<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
								</div>
							</form>
						</div>
					</div>
	
					<div class="clear areaencloser"></div>
	
					<div id="listarea" class="">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle">关联点检的周边设备一览</span>
							<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<table id="list"></table>
						<div id="listpager"></div>
					</div>
				</div>

				<div id="addarea" style="display:none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
						<span class="areatitle">新建周边设备点检关系</span>			
					</div>
				
					<div class="ui-widget-content dwidth-middleright">	
					<!-- 新建页面 -->
						<form id="addform" method="POST" onsubmit="return false;">
						<table class="condform">
							<tr>
								<td class="ui-state-default td-title">周边设备型号</td>
								<td class="td-content">
									<input type="text" alt="周边设备型号" name="model_type_name" id="add_model_type_name" class="ui-widget-content" style="width:215px;"/>
									<input id="hidden_add_model_type_name" name="model_type_name" type="hidden">
								</td>
							</tr>
							<!--tr>
								<td class="ui-state-default td-title">点检用设备类别</td>
								<td class="td-content">
									<input type="text" alt="设备类别" name="device_type_name" id="add_device_type_name" class="ui-widget-content" style="width:215px;"/>
									<input id="hidden_add_device_type_name" name="device_type_name" type="hidden">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">点检用设备型号</td>
								<td class="td-content"><input type="text" alt="型号" name="model_name" id="add_model_name" class="ui-widget-content" style="width:215px;"/></td>
							</tr-->
						</table>
						<table>
							<thead>
							<tr>
								<th class="ui-state-default td-title">组序号</th>
								<th class="ui-state-default td-title">点检用设备类别</th>
								<th class="ui-state-default td-title">点检用设备型号</th>
								<th class="ui-state-default td-title">操作</th>
							</tr>
							</thead>
							<tbody>

							</tbody>
						</table>

						<div style="height:44px;margin-top:5px;">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="savebutton" value="新建" role="button" aria-disabled="false" style="float:left;left:4px;">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="cancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
						</div>
					</form>	
						<div class="ui-widget-content">
							说明：
							<ol>
							<li>登记为需要“作业前点检”的周边设备型号，必须要与至少一组关联设备对应。否则不应当保留此设备型号的设置。</li>
							<li>“组序号”表示了该周边设备型号在要求“作业前点检”时，显示出需要点检的设备组的顺序。</li>
							<li>“点检用设备类别”和“点检用设备型号”表示需要选择符合此类别和型号的设备进行点检及关联维修。</li>
							<li>“点检用设备类别”不可以为空。</li>
							<li>如“点检用设备型号”为空，则表示可选此设备类别的任意型号设备。</li>
							<li>同一“组序号”的行代表着：周边维修时可以选择满足该组中任意一种类别和型号的设备来使用。</li>
							<li>同一“组序号”中，不可以设置完全相同的类别和型号。“完全相同”也包括类别相同，但是其中一个是任意型号，一个是指定型号的情况。</li>
							<li>不同“组序号”中，可以设置完全相同的类别和型号。代表这样的设备要使用两个以上。</li>
							</ol>
						</div>
					</div>
				</div>

				<div id="confirmmessage"></div>

				<!-- 品名referChooser -->
				<div class="referchooser ui-widget-content" id="search_model_referchooser" tabindex="-1">
					<table width="200px">
						<tr>
							<td></td>
							<td width="50%">过滤字:<input type="text"/></td>
							<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
						</tr>
					</table>
					<table  class="subform">${mReferChooser}</table>
				</div>
	
				<!-- 型号referChooser -->
				<div class="referchooser ui-widget-content" id="search_device_type_referchooser" tabindex="-1">
					<table width="200px">
						<tr>
							<td></td>
							<td width="50%">过滤字:<input type="text"/></td>
							<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
						</tr>
					</table>
					<table  class="subform">${nReferChooser}</table>
				</div>

				<!-- 品名referChooser -->
				<div class="referchooser ui-widget-content" id="add_model_referchooser" tabindex="-1">
					<table width="200px">
						<tr>
							<td></td>
							<td width="50%">过滤字:<input type="text"/></td>
							<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
						</tr>
					</table>
					<table  class="subform">${mReferChooser}</table>
				</div>
	
				<!-- 型号referChooser -->
				<div class="referchooser ui-widget-content" id="add_device_type_referchooser" tabindex="-1">
					<table width="200px">
						<tr>
							<td></td>
							<td width="50%">过滤字:<input type="text"/></td>
							<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
						</tr>
					</table>
					<table  class="subform">${nReferChooser}</table>
				</div>
			</div>
			<div class="clear areaencloser"></div>			
		</div>
	</div>
</body>

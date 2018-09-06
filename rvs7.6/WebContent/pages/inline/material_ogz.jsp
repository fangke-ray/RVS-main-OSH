<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/inline/jquery.datetimepicker.css">
<style>
/*备注*/
.note {
	position: absolute;
	font-size: 12px;
}

.circle {
	width: 30px;
	height: 30px;
	-moz-border-radius: 50%;
	-webkit-border-radius: 50%;
	border-radius: 50%;
	float: left;
	cursor: pointer;
	position: relative;
	z-index: 99;
}

.center_radius {
	width: 150px;
	height: 90px;
	border-radius: 95px/65px;
	border: 10px solid #ffc000;
	position: absolute;
	float: left;
}

/*弧绿色*/
.center_blue{
   border: 10px solid #17375e;
}
/*弧灰色*/
.center_grzy{
    border: 10px solid #7f7f7f;
}
/*弧红色*/
.center_red{
    border: 10px solid #cb0000;
}

/*圆绿色*/
.circle_blue {
	background-color: #17375e;
}
/*圆黄色*/
.circle_yellow {
	background-color: #fcc200;
}
/*圆灰色*/
.circle_grzy {
	background-color: #7f7f7f;
}
/*圆红色*/
.circle_red {
	width: 20px;
	height: 20px;
	background-color: #fff;
	border: 5px solid #cb0000;
}

/*长方形*/
.rectangle {
	width: 65px;
	height: 10px;
	float: left;
}
/*长方形绿色*/
.rectangle_blue {
	background-color: #17375e;
}
/*长方形黄色*/
.rectangle_yellow {
	background-color: #fcc200;
}
/*长方形灰色*/
.rectangle_grzy {
	background-color: #7f7f7f;
}
/*长方形红色*/
.rectangle_red {
	background-color: #cb0000;
}
</style>


<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>

<script type="text/javascript" src="js/adddate.js"></script>

<script type="text/javascript" src="js/inline/material_ogz.js"></script>
<script type="text/javascript" src="js/inline/jquery.datetimepicker.js"></script>

<title>OGZ维修对象进度</title>
</head>
<body class="outer">
	<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align:center;padding-top:16px;padding-bottom:16px;" id="body-1">
			<div id="searcharea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">检索条件</span>
					<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<div class="ui-widget-content dwidth-full">
					<form id="searchform" method="post">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content">
										<input type="text" id="search_sorc_no" maxlength="15" class="ui-widget-content">
									</td>
									<td class="ui-state-default td-title">SFDC No.</td>
									<td class="td-content">
										<input type="text" id="search_sfdc_no" maxlength="14" class="ui-widget-content">
									</td>
									<td class="ui-state-default td-title">ESAS No.</td>
									<td class="td-content">
										<input type="text" id="search_esas_no" maxlength="14" class="ui-widget-content">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">型号</td>
									<td class="td-content">
										<input type="text" id="search_model_name" maxlength="14" class="ui-widget-content">
										<input name="model_name" id="hidden_model_id" type="hidden">
									</td>
									<td class="ui-state-default td-title">机身号</td>
									<td class="td-content">
										<input type="text" id="search_serial_no" maxlength="12" class="ui-widget-content">
									</td>
									<td class="ui-state-default td-title">委托处</td>
									<td class="td-content">
										<select id="search_ocm">${sMaterialOcm }</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">受理日期</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="search_reception_time_start" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="search_reception_time_end" readonly="readonly">止<br>
									</td>
									<td class="ui-state-default td-title">同意日期</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="search_agreed_date_start" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="search_agreed_date_end" readonly="readonly">止<br>
									</td>
									<td class="ui-state-default td-title">(品保)完成日期</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="search_outline_time_start" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="search_outline_time_end" readonly="readonly">止<br>
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
							<input type="hidden" id="goMaterialOcm" value="${goMaterialOcm }">
						</div>
					</form>
				</div>
			</div>
			
			<div class="clear areaencloser dwidth-full"></div>
			
			<div id="listarea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">OGZ进度一览</span>
					<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				<table id="material_ogz_list" ></table>
				<div id="material_ogz_listpager"></div>
			</div>
			<div class="clear areaencloser dwidth-full"></div>
			
			<div class="referchooser ui-widget-content" id="model_name_referchooser" tabindex="-1">
				<table>
					<tr>
						<td width="50%">过滤字:<input type="text"/></td>	
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table class="subform">${mReferChooser }</table>
			</div>
				
		</div>
		
		<div id="detail_dialog"></div>
		<div id = "confirm_dialog">  
		  <input id="choose_time" type="text" readonly="readonly" style="display:none;">
		  <input id="choose_date" type="text" readonly="readonly" style="display:none;">
		</div>
		<div id="update_confirm"></div>
	</div>
</body>
</html>
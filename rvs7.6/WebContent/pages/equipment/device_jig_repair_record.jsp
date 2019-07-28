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
#list img {
	width : 30px;
	height : 30px;
	cursor : pointer;
}
</style>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/equipment/device_jig_repair_record.js"></script>
<script type="text/javascript" src="js/equipment/device_jig_repair_edit.js"></script>

<title>设备工具维修记录</title>
</head>
<body class="outer" style="overflow: auto;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
		<jsp:include page="/header.do" flush="true">
			<jsp:param name="part" value="1"/>
			<jsp:param name="sub" value="t"/>
		</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
			<div class="dwidth-full">
				<div id="searcharea">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					   	 <span class="areatitle">检索条件</span>
					     <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						 	<span class="ui-icon ui-icon-circle-triangle-n"></span>
						 </a>
					</div>
					<div class="ui-widget-content">
						<form id="searchform" method="POST">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">报修时间</td>
								   		<td class="td-content">
											<input id="search_submit_time_start" type="text" class="ui-widget-content" value="${submitTimeStart}" readonly> 起<br>
											<input id="search_submit_time_end" type="text" class="ui-widget-content" readonly> 止
										</td>
										<td class="ui-state-default td-title">报修品</td>
								   		<td class="td-content">
											<input id="search_manage_code" type="text" class="ui-widget-content">
										</td>
								   	    <td class="ui-state-default td-title">修理完毕时间</td>
								   		<td class="td-content">
											<input id="search_repair_complete_time_start" type="text" class="ui-widget-content" readonly> 起<br>
											<input id="search_repair_complete_time_end" type="text" class="ui-widget-content" readonly> 止
										</td>
								   </tr>
								   <tr>
								   		<td class="ui-state-default td-title">发生工程</td>
								   		<td class="td-content" colspan="5">
											<select id="search_line_id">${lOptions}</select>
										</td>
								   </tr>
								   <tr>
								   		<td class="ui-state-default td-title" rowspan="2">维修品类别</td>
								   		<td class="td-content" rowspan="2">
											<select id="search_object_type">
												<option></option>
												<option value=1>设备／一般工具</option>
												<option value=2>专用工具</option>
												<option value=3>生产环境</option>
												<option value=9>其他</option>
											</select>
										</td>
								   		<td class="ui-state-default td-title" rowspan="2">修理状态</td>
								   		<td class="td-content" rowspan="2">
											<select id="search_halt_minute">
												<option></option>
												<option value=1>当天修理完毕</option>
												<option value=2>一天以上修理完毕</option>
												<option value=9>尚未修理完毕</option>
											</select>
										</td>
										<td class="ui-state-default td-title">更换/替换/修理品</td>
								   		<td class="td-content">
											<input id="search_device_type_name" type="text" class="ui-widget-content">
										</td>
								   </tr>
								   <tr>
										<td class="ui-state-default td-title">备注</td>
								   		<td class="td-content">
											<input id="search_comment" type="text" class="ui-widget-content">
										</td>
								   </tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input type="button" class="ui-button" id="resetbutton" value="清除" style="float:right;right:2px">
								<input type="button" class="ui-button" id="searchbutton" value="检索" style="float:right;right:2px">	
							</div>
						</form>
					</div>
					
					<div class="areaencloser"></div>
					
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
						<span class="areatitle">设备工具维修记录一览表</span>
					</div>
					<table id="list"></table>
					<div id="listpager"></div>
					<div class="ui-widget-header areabase"style="padding-top:4px;">
					    <div id="executes" style="margin-left:4px;margin-top:4px;">
							<input type="button" id="submitbutton" class="ui-button" value="报修">
							<input type="button" id="repairbutton" class="ui-button" value="修理">
							<input type="button" id="confirmbutton" class="ui-button" value="验收">
						</div>
					</div>
				</div><!-- id="searcharea" -->
				<div id="submitarea" style="display:none;">
					<form id="submitform" method="POST">
						<table class="condform">
						   <tr>
						   		<td class="ui-state-default td-title">发生工程</td>
						   		<td class="td-content" style="width: 396px;">
									<select id="submit_line_id">${lOptions}</select>
								</td>
						   </tr>
						   <tr>
								<td class="ui-state-default td-title">对象类型</td>
						   		<td class="td-content">
									<select id="submit_object_type">
										<option value="1">设备•一般工具</option>
										<option value="2">专用工具</option>
										<option value="3">生产环境</option>
										<option value="9">其他(无管理编号)</option>
									</select>
								</td>
						   </tr>
						   <tr>
						   		<td class="ui-state-default td-title">管理编号</td>
						   		<td class="td-content">
									<input id="submit_manage_code_dev" type="text" class="ui-widget-content" readonly>
									<input type="hidden" id="submit_manage_id_dev" name="manage_id">
									<input id="submit_manage_code_jig" type="text" class="ui-widget-content" readonly>
									<input type="hidden" id="submit_manage_id_jig" name="manage_id">
									<label id="submit_manage_code_free">－</label>
								</td>
						   </tr>
						   <tr>
						   		<td class="ui-state-default td-title">维修故障</td>
						   		<td class="td-content">
									<input id="submit_object_name" type="text" class="ui-widget-content">
								</td>
						   </tr>
						   <tr>
						   		<td class="ui-state-default td-title">故障现象</td>
						   		<td class="td-content">
									<textarea id="submit_phenomenon" style="width:320px;" class="ui-widget-content"></textarea>
								</td>
						   </tr>
						</table>
					</form>
				</div><!-- id="submitarea" -->
			</div><!-- body-mdl -->
			<div class="clear areaencloser"></div>
		</div>
	</div>

	<div id="dev_managecode_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="z-index:1050">
		 <table>
			<tbody>
			   <tr>
					<td width="50%">过滤字:<input type="text"></td>	
					<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
			   </tr>
		   </tbody>
	  	 </table>
	  	 <table class="subform">${dReferChooser}</table>
	</div>

	<div id="jig_managecode_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="z-index:1050">
		 <table>
			<tbody>
			   <tr>
					<td width="50%">过滤字:<input type="text"></td>	
					<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
			   </tr>
		   </tbody>
	  	 </table>
	  	 <table class="subform">${jReferChooser}</table>
	</div>

</body>
</html>
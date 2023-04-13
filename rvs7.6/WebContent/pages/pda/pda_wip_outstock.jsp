<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>

<%
String  user_name  = (String)request.getSession().getAttribute("user_name");

String message = (String) request.getAttribute("notice");
String errors = (String) request.getAttribute("errors");
String storageMap = (String) request.getAttribute("storageMap");

String ableToInline = (String) request.getAttribute("ableToInline");

%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html:html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
	window.onload = function(){
		
		var errmsg = "<%=errors%>";
		if (errmsg != "null" && errmsg != "") {
			alert(errmsg);
		}

		var objScanner = document.getElementById("scanner");
		if(objScanner!=null){
			objScanner.focus();//获取焦点
			objScanner.onkeyup = function(e){
				var keyCode = event.keyCode || e.keyCode;
				if(keyCode == 13){// 回车 
					var objMaterialId = document.getElementById("h_material_id");
					var objWipLocation = document.getElementById("h_wip_location");

					if (objMaterialId.value) {
						if (this.value.length == 11 && this.value != objMaterialId.value) {
							objMaterialId.value = this.value;
							document.forms["pdaMaterialForm"].action = "pda_wip_outstock.do?method=search";
						} else {
							if (!objWipLocation.value) {
								if (this.value.length == 11) {
									objMaterialId.value = this.value;
									document.forms["pdaMaterialForm"].action = "pda_wip_outstock.do?method=search";
								} else {
									objWipLocation.value = this.value;
									document.forms["pdaMaterialForm"].action = "pda_wip_outstock.do?method=doOut";
								}
							} else {
								if (objWipLocation.value != this.value) {
									if (!confirm("扫描位置与系统记录不一致，确认实物是在这个位置：" + this.value + "吗？")) {
										return;
									}
								}
								document.forms["pdaMaterialForm"].action = "pda_wip_outstock.do?method=doOut";
							}
						}
					} else if (!objMaterialId.value && objWipLocation.value) {
						objMaterialId.value = this.value;
						document.forms["pdaMaterialForm"].action = "pda_wip_outstock.do?method=doOut";
					} else {
						objMaterialId.value = this.value;
						document.forms["pdaMaterialForm"].action = "pda_wip_outstock.do?method=search";
					}

					document.forms["pdaMaterialForm"].submit();
				}
			};
		}

		var inlineBtn = document.getElementById("inline_btn");

		if(inlineBtn!=null){
			if (!inlineBtn.disabled) {
				inlineBtn.focus();
			}
			inlineBtn.onclick = function(){
				var objMaterialId = document.getElementById("h_material_id");
				document.forms["pdaMaterialForm"].action = "pda_wip_outstock.do?method=doInline";
				document.forms["pdaMaterialForm"].submit();
			};
		};

		var objGoback = document.getElementById("goback");
		objGoback.onclick = function(){
			window.location.href="appmenu.do?method=pdaMenu";
		};

		// history.forward();
	};
</script>
<title>修理品待投线出库</title>
<link rel="stylesheet" type="text/css" href="css/pda/pda_custom.css">
</head>
<body>
	<html:form action="pda_wip_outstock.do" method="post" onsubmit="return false">
		<div class="width-full">
		
			<div class="header" style="height:40px;position:relative;">
				<div class="left" style="color:#fff;margin-left:10px;position:absolute;">
					<h5>
						<label>你好! <%=user_name %></label><br>
					</h5>
				</div>
				<div class="right" style="color:#fff;margin-right:10px;">
					<div style="text-align:right;">
						<h5><%=message%></h5>
					</div>
					<div style="position:absolute;top: 0;right: 0;background-color:#091277;">
						<div style="cursor:pointer;display:inline-block;" id="goback">←</div>
					</div>
				</div>
				<div class="clear"></div>
			</div>

			<div class="main" style="height:130px;padding:10px;">
				<div class="left" style="width:50%;">
					<table style="width:100%;height:130px;">
						<tr>
							<td class="td-title" style="width:35%;"><nobr>修理单号</nobr></td>
							<td style="width:60%;">
				<html:hidden name="pdaMaterialForm" property="omr_notifi_no" />
				<logic:notEmpty name="pdaMaterialForm"><bean:write name="pdaMaterialForm" property="omr_notifi_no"/></logic:notEmpty>
							</td>
						</tr>
						<tr>
							<td class="td-title"><nobr>型号</nobr></td>
							<td><logic:notEmpty name="pdaMaterialForm"><bean:write name="pdaMaterialForm" property="model_name"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title"><nobr>机身号</nobr></td>
							<td><logic:notEmpty name="pdaMaterialForm"><bean:write name="pdaMaterialForm" property="serial_no"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title"><nobr>投入课室</nobr></td>
							<td>
								<logic:notEmpty name="pdaMaterialForm">
									<bean:write name="pdaMaterialForm" property="section_name"/>
								</logic:notEmpty>
							</td>
						</tr>
						<tr>
							<td class="td-title"><nobr>零件订购</nobr></td>
							<td><logic:notEmpty name="pdaMaterialForm"><bean:write name="pdaMaterialForm" property="part_status"/></logic:notEmpty></td>
						</tr>
					</table>
				</div>

				<div class="right" style="width:128px;border:1px solid;<%=(ableToInline != null && !"0".equals(ableToInline)) ? "display:none;" : ""%>">
<style>
	#location_map table {
		height: 100px;
	}
	#location_map td {
		font-size: 10px;
	}
	#location_map th,
	#location_map td {
		text-align:center;
		padding:0;
		height:.5em;
		line-height:1em;
	}
</style>
					<table style="width:100%;height:130px;">
						<tr>
							<td class="td-title" style="width:35%;height:16px;"><nobr>存放库位</nobr></td>
							<td><logic:notEmpty name="pdaMaterialForm"><bean:write name="pdaMaterialForm" property="wip_location"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td colspan="2">
								<div id="location_map">
									<%=storageMap%>
								</div>
							</td>
						</tr>
					</table>
				</div>

				<div class="right" style="width:128px;border:1px solid;<%=(ableToInline == null || "0".equals(ableToInline)) ? "display:none;" : ""%>">
					<table style="width:100%;height:130px;">
						<tr>
							<td class="td-title" style="width:35%;height:16px;"><nobr>CCD 盖玻璃</nobr></td>
							<td><logic:notEmpty name="pdaMaterialForm"><bean:write name="pdaMaterialForm" property="ccd_operate_result"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title" style="width:35%;height:16px;"><nobr>选择流程</nobr></td>
							<td>
								<logic:notEmpty name="pdaMaterialForm">
									<bean:write name="pdaMaterialForm" property="pat_name"/>
								</logic:notEmpty>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<div id="inline_area" style="text-align: center;">
				<% if ("1".equals(ableToInline)) { %>
					<input type="button" id="inline_btn" value="投线" style="background-color: rgb(255, 179, 0);">
				<% } else { %>
					<input type="button" id="inline_btn" value="不可投线" disabled style="background-color: lightgray;">
				<% }%>
								</div>
							</td>
						</tr>
					</table>
				</div>
				<div class="clear"></div>
			</div>

			<div class="bottom" style="height:110px;">
				<div style="width:70%;margin:0 auto;font-size:14px;">
					<label>扫描区域</label>&nbsp;&nbsp;
					<input type="text" class="input-txt" id="scanner" style="ime-mode:disabled;" name="scanner"/>
				</div>
			</div>
		</div>
		
		<logic:notEmpty name="pdaMaterialForm">
			<html:hidden name="pdaMaterialForm" styleId="h_material_id" property="material_id" />
			<html:hidden name="pdaMaterialForm" styleId="h_wip_location" property="wip_location" />
			<html:hidden name="pdaMaterialForm" property="section_id" />
			<html:hidden name="pdaMaterialForm" property="pat_id" />
		</logic:notEmpty>
		
	</html:form>
</body>
</html:html>

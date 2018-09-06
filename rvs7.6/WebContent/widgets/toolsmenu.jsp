<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" import="java.util.Map" isELIgnored="false"%>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<script>
$(function() {
	$("a").attr("target", "_parent");
	var accordion_idx = $("#accordion h3").index($("#accordion h3:contains('" + $("#linkto").val() + "')"));
	if (accordion_idx < 0) accordion_idx = 0;
	$("#accordion" ).accordion({active: accordion_idx});
	$('#datepicker').datepicker({
		inline: true,
		width: 224
	});
	$('#datepicker div.ui-datepicker-inline').css("width","219px");

	$("#menucontainner").hide();
});
</script>
<body>
<% Map<String, Boolean> links = (Map<String, Boolean>) request.getAttribute("menuLinks"); %>
<div id="modelmenuarea">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-left">
		<span class="areatitle">功能菜单</span>
		<input type="hidden" id="linkto" value="${linkto}">
	</div>
	<div class="ui-widget-content dwidth-left">
		<div id="accordion">
			
			<h3 style="padding-left:30px;">设备工具/治具信息管理</h3>
		    <div>
<% if(links.get("dt_admin")){ %>
			   <a target="_parent" href="devices_type.do">设备工具品名</a><br>
		       <a target="_parent" href="devices_manage.do">设备工具管理</a><br>		      
		       <a target="_parent" href="tools_manage.do">治具管理</a><br>		      
			   <a target="_parent" href="torsion_device.do">力矩工具一览</a><br>
			   <a target="_parent" href="electric_iron_device.do">电烙铁工具一览</a><br>	 
			   <a target="_parent" href="drying_oven_device_forward.do">烘箱一览</a><br>     
		       <a target="_parent" href="peripheral_infect_device.do">周边设备点检关系</a><br>
<% } else { %>
		       <a target="_parent" href="devices_distribute.do">设备工具分布</a><br>		      
		       <a target="_parent" href="tools_distribute.do">治具分布</a><br>
<% } %>
		    </div>
		   
		    <h3 style="padding-left:30px;">设备工具/治具点检</h3>
		    <div>
<% if(links.get("dt_admin")){ %>
				<a target="_parent" href="check_file_manage.do">点检表管理</a><br>
<% } %>
				<a target="_parent" href="usage_check.do">点检操作</a><br>
				<a target="_parent" href="daily_check_result.do">日常点检结果</a><br>
				<a target="_parent" href="device_regularly_check_result.do">定期点检结果</a><br>
				<a target="_parent" href="tools_check_result.do">治具点检结果</a><br>
				<a target="_parent" href="check_unqualified_record.do">点检不合格记录</a><br>
<% if(links.get("dt_admin")){ %>
				<a target="_parent" href="external_adjustment.do">检查机器校验</a><br>
<% } %>
				<a target="_parent" href="check_result_filing.do">点检结果归档</a><br>
		    </div>

		</div>
	</div>
	<div id="datepicker" class="dwidth-left"></div>
	<div class="clear areaencloser"></div>
</div>



</body></html>
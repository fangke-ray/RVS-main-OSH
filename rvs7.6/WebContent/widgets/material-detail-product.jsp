<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="js/common/material_detail_product.js"></script>
<body>
<div style="margin:auto;">
<div id="material_detail_product_listarea" class="dwidth-middle" style="margin-top:22px;margin-left:9px;margin-bottom:22px;">
	<table id="material_detail_product_list"></table>
	<div id="material_detail_product_listpager"></div>
</div>
<div class="clear areacloser"></div>
<!--div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
	<input type="button" class="ui-button" id="nobeforereworkbutton" value="不显示返工前" style="float: left;margin-top: 4px;margin-left: 4px;">
	<input type="button" class="ui-button" id="finishproductionbutton" value="只显示完成" style="float: left;margin-top: 4px;margin-left: 4px;">
</div-->
<div id="material_detail_product_chart">
</div>

<div class="clear areacloser"></div>
</div>

</body>
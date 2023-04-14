<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<body>
<div id="material_detail_content" style="margin:auto;">

<%
	String from = (String) request.getAttribute("from");
	boolean fromPartial = "partial".equals(from);
	boolean showDjLoan = (request.getAttribute("showDjLoan") != null);
	boolean editable = (request.getAttribute("editable") != null);
%>


<div style="height:44px;width:770px;" id="material_detail_infoes" class="dwidth-full">

	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_base" role="button" <%=fromPartial ? "" : "checked"%>><label for="material_detail_infoes_base" title="">基本信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_product" role="button"><label for="material_detail_infoes_product" title="">作业信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_partical" role="button" <%=fromPartial ? "checked" : ""%>><label for="material_detail_infoes_partical">零件信息</label>
<% if (showDjLoan) { %>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_dj_loan" role="button"><label for="material_detail_infoes_dj_loan">设备工具清洗记录</label>
<% } %>
</div>
<input type="hidden" id="global_material_id" value="${global_material_id}"><input type="hidden" id="global_occur_times" value="${global_occur_times}">
<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_base" style="width:786px;text-align:left;" lazyload="material.do?method=getDetail">
</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_product" style="width:786px;text-align:center;display:none;" lazyload="widgets/material-detail-product.jsp">
</div>

<!-- 维修对象零件信息 -->
<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_partical" style="width:786px;text-align:left;display:none;" lazyload="materialPartial.do?method=getDetail">
</div>

<% if (showDjLoan) { %>

<!-- 维修违治具借用信息 -->
<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_dj_loan" style="width:786px;text-align:center;display:none;" lazyload="device_jig_loan.do?method=detailMaterial">
</div>

<% } %>

<div class="clear areacloser"></div>
</div>

<%
if (editable) {
%>	
<editable from="<%=from%>"/>
<% } %>

<script type="text/javascript" src="js/common/material_detail_init_view.js"></script>

</body>
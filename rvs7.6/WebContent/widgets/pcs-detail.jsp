<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<body>
	<div style="margin: auto;">

		<div id="pcs_detail_basearea" class="dwidth-middleright">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">维修对象信息</span>
			</div>
			<div class="ui-widget-content dwidth-middleright">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">维修对象型号</td>
							<td class="td-content"><label></label></td>
							<td class="ui-state-default td-title">维修对象机身号</td>
							<td class="td-content"><label></label></td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">维修等级</td>
							<td class="td-content"><label></label></td>
							<td class="ui-state-default td-title">维修课室</td>
							<td class="td-content"><label></label></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="clear areaencloser dwidth-middleright"></div>
		</div>

		<div id="pcs_detail_pcsarea" style="margin-bottom: 16px;">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">工程检查票</span>
			</div>
			<div class="ui-widget-content dwidth-middleright" id="pcs_detail_container">
				<div id="pcs_detail_pcs_pages">
				</div>
				<div id="pcs_detail_pcs_contents">
				</div>
			</div>
			<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
		</div>

		<div class="clear areacloser"></div>

	</div>
</body>

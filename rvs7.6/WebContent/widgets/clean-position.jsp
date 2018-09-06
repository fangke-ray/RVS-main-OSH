<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<body>
	<div style="margin: auto;">

		<div id="pcs_detail_basearea" class="dwidth-middle">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
				<span class="areatitle">取消工位信息</span>
			</div>
			<div class="ui-widget-content dwidth-middle">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">维修对象型号</td>
							<td class="td-content"><label>${model_name}</label></td>
							<td class="ui-state-default td-title">维修对象机身号</td>
							<td class="td-content"><label>${serial_no}</label></td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">取消工位</td>
							<td class="td-content"><select id="clean_target">${pOptions}</select></td>
							<td class="ui-state-default td-title">更改说明</td>
							<td class="td-content"><textarea id="clean_comment" style="height: 80%;"></textarea></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="clear areaencloser dwidth-middle"></div>
		</div>

		<div class="clear areacloser"></div>

	</div>
</body>

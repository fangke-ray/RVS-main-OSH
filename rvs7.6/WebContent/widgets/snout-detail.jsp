<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<body>
	<div style="margin: auto;">

		<div id="pcs_detail_basearea" class="dwidth-middleright">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">先端组件信息</span>
			</div>
			<div class="ui-widget-content dwidth-middleright">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">先端组件型号</td>
							<td class="td-content" style="width: 648px;">
								<select id="snout_detail_model_id">
									${mOptions}
								</select>
								<input id="snout_detail_model_id_org" type="hidden" value="${snout.model_id}">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">先端组件序列号</td>
							<td class="td-content">
								<input id="snout_detail_serial_no" type="text" value="${snout.serial_no}">
								<input id="snout_detail_serial_no_org" type="hidden" value="${snout.serial_no}">
							</td>
						</tr>
						<input type="hidden" id="snout_detail_serial_no" value="${snout.serial_no}">
					</tbody>
				</table>
			</div>
			<div class="clear areaencloser dwidth-middleright"></div>
		</div>

			<div id="pcs_detail_pcsarea" style="margin-bottom: 16px;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
					<span class="areatitle">工程检查票</span>
				</div>
				<div class="ui-widget-content dwidth-middleright">
					<div id="pcs_detail_pcs_contents">
						<div id="pcs_detail_pcs_content">
							${pcs}
						</div>
					</div>
				</div>
				<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
			</div>

		<div class="clear areacloser"></div>
	</div>
</body>

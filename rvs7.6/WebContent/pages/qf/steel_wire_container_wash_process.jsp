<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<%
	String privacy = (String)request.getAttribute("privacy");
	boolean hasPrivacy = "processing".equals(privacy);
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/qf/steel_wire_container_wash_process.js"></script>

<style type="text/css">
	#search_partial_code_refer table:last-child tr td:last-child{
		display: none;
	}
</style>
<title>物料加工记录</title>
</head>
<body class="outer">
	<div class="width-full" style="margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all" style="padding-top: 16px; padding-bottom: 16px;">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="现品管理"/>
				</jsp:include>
			</div>
			<div style="width: 994px; float: left;">
				<div id="body-mdl">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">物料加工记录</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>

					<div class="ui-widget-content" style="border-bottom: 0;">
						<div id="btn-group">
							<input type="radio" name="buttonset" class="ui-button ui-corner-up" id="L1Button" value="271" process_type="1">
							<label for="L1Button">钢丝固定件清洗</label>
							<input type="radio" name="buttonset" class="ui-button ui-corner-up" id="L2Button" value="272" process_type="2">
							<label for="L2Button">热缩管切割</label>
							<input type="radio" name="buttonset" class="ui-button ui-corner-up" id="L3Button" value="273" process_type="3">
							<label for="L3Button">耗材分装</label>
							<input type="radio" name="buttonset" class="ui-button ui-corner-up" id="L4Button" value="274" process_type="4">
							<label for="L4Button">NS插入管零件</label>
							<input type="radio" name="buttonset" class="ui-button ui-corner-up" id="L5Button" value="275" process_type="5">
							<label for="L5Button">S1级组件拆包</label>
						</div>
					</div>

					<div id="load">
						<div for="L1Button" style="display: none;">
							<div class="ui-widget-content dwidth-middleright">
								<form name="searchform" method="POST">
									<table class="condform">
										<tr>
											<td class="ui-state-default td-title">品名</td>
											<td class="td-content">
												<input type="text" id="L1_search_partial_code" class="ui-widget-content">
											</td>
											<td class="ui-state-default td-title">入库批号</td>
											<td class="td-content">
												<input type="text" id="L1_search_lot_no" maxlength="15" class="ui-widget-content">
											</td>
											<td class="ui-state-default td-title">责任人</td>
											<td class="td-content">
												<input type="text" id="L1_search_operator_name" class="ui-widget-content">
												<input type="hidden" id="L1_hidden_operator_id">
											</td>
										</tr>
										<tr>
											<td class="ui-state-default td-title">处理日期</td>
											<td class="td-content">
												<input type="text" class="ui-widget-content" id="L1_search_process_time_start" readonly="readonly" value="${curDate}">起<br>
												<input type="text" class="ui-widget-content" id="L1_search_process_time_end" readonly="readonly">止<br>
											</td>
											<td class="ui-state-default td-title"></td>
											<td class="td-content"></td>
											<td class="ui-state-default td-title"></td>
											<td class="td-content"></td>
										</tr>
									</table>
									<div style="height:44px">
										<input class="ui-button" id="L1resetbutton" value="清除" style="float:right;right:2px" type="button">
										<input class="ui-button" id="L1searchbutton" value="检索" style="float:right;right:2px" type="button">
									</div>
								</form>
							</div>
						</div>

						<div for="L2Button" style="display: none;">
							<div class="ui-widget-content dwidth-middleright">
								<form name="searchform" method="POST">
									<table class="condform">
										<tr>
											<td class="ui-state-default td-title">品名</td>
											<td class="td-content">
												<input type="text" id="L2_search_partial_code" class="ui-widget-content">
											</td>
											<td class="ui-state-default td-title">责任人</td>
											<td class="td-content">
												<input type="text" id="L2_search_operator_name" class="ui-widget-content">
												<input type="hidden" id="L2_hidden_operator_id">
											</td>
												<td class="ui-state-default td-title">处理日期</td>
											<td class="td-content">
												<input type="text" class="ui-widget-content" id="L2_search_process_time_start" readonly="readonly" value="${curDate}">起<br>
												<input type="text" class="ui-widget-content" id="L2_search_process_time_end" readonly="readonly">止<br>
											</td>
										</tr>
									</table>
									<div style="height:44px">
										<input class="ui-button" id="L2resetbutton" value="清除" style="float:right;right:2px" type="button">
										<input class="ui-button" id="L2searchbutton" value="检索" style="float:right;right:2px" type="button">
									</div>
								</form>
							</div>
						</div>

						<div for="L3Button" style="display: none;">
							<div class="ui-widget-content dwidth-middleright">
								<form name="searchform" method="POST">
									<table class="condform">
										<tr>
											<td class="ui-state-default td-title">品名</td>
											<td class="td-content">
												<input type="text" id="L3_search_partial_code" class="ui-widget-content">
											</td>
											<td class="ui-state-default td-title">责任人</td>
											<td class="td-content">
												<input type="text" id="L3_search_operator_name" class="ui-widget-content">
												<input type="hidden" id="L3_hidden_operator_id">
											</td>
											<td class="ui-state-default td-title">处理日期</td>
											<td class="td-content">
												<input type="text" class="ui-widget-content" id="L3_search_process_time_start" readonly="readonly" value="${curDate}">起<br>
												<input type="text" class="ui-widget-content" id="L3_search_process_time_end" readonly="readonly">止<br>
											</td>
										</tr>
									</table>
									<div style="height:44px">
										<input class="ui-button" id="L3resetbutton" value="清除" style="float:right;right:2px" type="button">
										<input class="ui-button" id="L3searchbutton" value="检索" style="float:right;right:2px" type="button">
									</div>
								</form>
							</div>
						</div>

						<div for="L4Button" style="display: none;">
							<div class="ui-widget-content dwidth-middleright">
								<form name="searchform" method="POST">
									<table class="condform">
										<tr>
											<td class="ui-state-default td-title">品名</td>
											<td class="td-content">
												<input type="text" id="L4_search_partial_code" class="ui-widget-content">
											</td>
											<td class="ui-state-default td-title">责任人</td>
											<td class="td-content">
												<input type="text" id="L4_search_operator_name" class="ui-widget-content">
												<input type="hidden" id="L4_hidden_operator_id">
											</td>
											<td class="ui-state-default td-title">处理日期</td>
											<td class="td-content">
												<input type="text" class="ui-widget-content" id="L4_search_process_time_start" readonly="readonly" value="${curDate}">起<br>
												<input type="text" class="ui-widget-content" id="L4_search_process_time_end" readonly="readonly">止<br>
											</td>
										</tr>
									</table>
									<div style="height:44px">
										<input class="ui-button" id="L4resetbutton" value="清除" style="float:right;right:2px" type="button">
										<input class="ui-button" id="L4searchbutton" value="检索" style="float:right;right:2px" type="button">
									</div>
								</form>
							</div>
						</div>

						<div for="L5Button" style="display: none;">
							<div class="ui-widget-content dwidth-middleright">
								<form name="searchform" method="POST">
									<table class="condform">
										<tr>
											<td class="ui-state-default td-title">品名</td>
											<td class="td-content">
												<input type="text" id="L5_search_partial_code" class="ui-widget-content">
											</td>
											<td class="ui-state-default td-title">责任人</td>
											<td class="td-content">
												<input type="text" id="L5_search_operator_name" class="ui-widget-content">
												<input type="hidden" id="L5_hidden_operator_id">
											</td>
											<td class="ui-state-default td-title">处理日期</td>
											<td class="td-content">
												<input type="text" class="ui-widget-content" id="L5_search_process_time_start" readonly="readonly" value="${curDate}">起<br>
												<input type="text" class="ui-widget-content" id="L5_search_process_time_end" readonly="readonly">止<br>
											</td>
										</tr>
									</table>
									<div style="height:44px">
										<input class="ui-button" id="L5resetbutton" value="清除" style="float:right;right:2px" type="button">
										<input class="ui-button" id="L5searchbutton" value="检索" style="float:right;right:2px" type="button">
									</div>
								</form>
							</div>		
						</div>
						
						<div class="areaencloser"></div>
							
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle" id="listModelName"></span>
							<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<table id="list"></table>
						<div id="listpager"></div>
						<div class="ui-widget-header areabase" id="executes" style="padding-top:4px;">
				        	<div style="margin-left:4px;margin-top:2px;">
								<input type="button" id="applyToMaterialButton" class="ui-button"  value="分配维修对象">
					    	</div>
						</div>
					</div>
				</div>
				
				<div id="add" style="display: none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle"></span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					
					<div class="ui-widget-content dwidth-middleright">
						<form id="addform" method="POST">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">品名</td>
										<td class="td-content">
											<input type="text" id="add_partial_code" class="ui-widget-content">
											<input type="hidden" id="hidden_add_partial_id">
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">品名</td>
										<td class="td-content">
											<input type="text" id="add_cut_partial_code" class="ui-widget-content">
											<input type="hidden" id="hidden_cut_add_partial_id">
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">入库批号</td>
										<td class="td-content">
											<input type="text" id="add_lot_no" maxlength="15" class="ui-widget-content">
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">切割长度</td>
										<td class="td-content">
											<select id="add_cut_length" style="display: none;"></select>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">作业数量</td>
										<td class="td-content">
											<input type="text" id="add_quantity" class="ui-widget-content">
										</td>
									</tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input class="ui-button" id="canelbutton" value="取消" style="float:right;right:2px" type="button">
								<input class="ui-button" id="insertbutton" value="记录" style="float:right;right:2px" type="button">
							</div>
						</form>
					</div>
				</div>
<%
	if(hasPrivacy){
%>
				<div id="update" style="display: none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle"></span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div class="ui-widget-content dwidth-middleright">
						<form id="updateform" method="POST">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">品名</td>
										<td class="td-content">
											<label id="update_partial_code"></label>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">入库批号</td>
										<td class="td-content">
											<input type="text" id="update_lot_no" maxlength="15" class="ui-widget-content">
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">切割长度</td>
										<td class="td-content">
											<select id="update_cut_length" style="display: none;"></select>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">作业数量</td>
										<td class="td-content">
											<input type="text" id="update_quantity" class="ui-widget-content">
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">处理日期</td>
										<td class="td-content">
											<label id="update_process_time"></label>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">责任人</td>
										<td class="td-content">
											<label id="update_operator_name"></label>
										</td>
									</tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input class="ui-button" id="canel2button" value="取消" style="float:right;right:2px" type="button">
								<input class="ui-button" id="updatebutton" value="更新" style="float:right;right:2px" type="button">
							</div>
						</form>
					</div>
				</div>
<%
	}
%>
			</div> 
			<div class="clear"></div>
		</div>
		
		<div id="materialDialog">
			<table id="materiallist"></table>
			<div id="materiallistpager"></div>
		</div>
		
		<div id="search_operator_name_refer" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
			 <table>
				<tbody>
				   <tr>
						<td width="50%">过滤字:<input type="text"></td>	
						<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
				   </tr>
			   </tbody>
		  	 </table>
		  	 <table class="subform">${oReferChooser}</table>
		</div>
		
		<div id="search_partial_code_refer" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
			 <table>
				<tbody>
				   <tr>
						<td width="50%">过滤字:<input type="text"></td>	
						<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
				   </tr>
			   </tbody>
		  	 </table>
		  	 <table class="subform">${hReferChooser}</table>
		</div>
		
		<input type="hidden" id="hidPrivacyButton" value="<%=hasPrivacy %>">
		
	</div>
</body>
</html>
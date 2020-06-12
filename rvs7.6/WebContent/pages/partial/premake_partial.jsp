<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/lte-style.css">
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/partial/premake_partial.js"></script>


<title>零件预制</title>
</head>

<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件基础数据管理"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div id="main">
					<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
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
										<td class="ui-state-default td-title">零件编码</td>
										<td class="td-content">
											<input type="text" id="search_partial_code" class="ui-widget-content">
										</td>
										<td class="ui-state-default td-title" rowspan="2">型号名称</td>
										<td class="td-content" rowspan="2" style="width: 498px;">
											<select id="search_model_id">${mReferChooser}</select>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">标配零件</td>
										<td class="td-content">
											<div id="search_standard_flg">
												<input type="radio" name="tandard_flg" id="search_standard_flg_all" class="ui-widget-content" value="" checked="checked"><label for="search_standard_flg_all">(全)</label>
												<input type="radio" name="tandard_flg" id="search_standard_flg_yes" class="ui-widget-content" value="1"><label for="search_standard_flg_yes">是</label>
												<input type="radio" name="tandard_flg" id="search_standard_flg_no" class="ui-widget-content" value="-1"><label for="search_standard_flg_no">否</label>
											</div>
										</td>
									</tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input id="resetbutton" class="ui-button" value="清除" type="button" style="float:right;right:2px">
								<input id="searchbutton" class="ui-button" value="检索" type="button" style="float:right;right:2px">
							</div>
						</form>
					</div>
					
					<div class="clear areaencloser"></div>
					
					<div id="listarea" >
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle">零件预制一览</span>
							<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<table id="list"></table>
						<div id="listpager"></div>
					</div>
				</div>
				
				<!-- 新建  -->
				<div id="create" style="display:none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					   <span class="areatitle">新建零件预制</span>
					    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					
					<div class="ui-widget-content">	
						<form id="update_form" method="POST">
							<table class="condform">
								<tr>
									<td class="ui-state-default td-title">零件编码</td>
									<td class="td-content">
										<input type="hidden" id="insert_partial_id" alt="零件ID"></input>
										<input type="text" id="insert_partial_code" class="ui-widget-content">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">型号名称</td>
									<td class="td-content">
										<select id="insert_model_id">${mReferChooser}</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">数量</td>
									<td class="td-content">
										<input type="number" id="insert_quantity" class="ui-widget-content">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">标配零件</td>
									<td class="td-content">
										<div id="insert_standard_flg">
											<input type="radio" name="insert_standard_flg" id="insert_standard_flg_all" class="ui-widget-content" checked="checked" value=""><label for="insert_standard_flg_all">(全)</label>
											<input type="radio" name="insert_standard_flg" id="insert_standard_flg_yes" class="ui-widget-content" value="1"><label for="insert_standard_flg_yes">是</label>
											<input type="radio" name="insert_standard_flg" id="insert_standard_flg_no" class="ui-widget-content" value="0"><label for="insert_standard_flg_no">否</label>
										</div>
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input id="insertbutton" class="ui-button" type="button"  value="新建" style="float:left;left:2px">
								<input id="cancelbutton2" class="ui-button" type="button" value="取消" style="float:left;left:2px">
							</div>
						</form>
					</div>
				</div>
				
				<!-- 更新 -->
				<div id="update" style="display:none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					   <span class="areatitle">修改零件预制</span>
					    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					
					<div class="ui-widget-content">	
						<form id="update_form" method="POST">
							<table class="condform">
								<tr>
									<td class="ui-state-default td-title">零件编码</td>
									<td class="td-content">
										<label id="label_partial_code"></label>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">型号名称</td>
									<td class="td-content">
										<label id="label_model_name"></label>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">数量</td>
									<td class="td-content">
										<input type="number" id="update_quantity" class="ui-widget-content">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">标配零件</td>
									<td class="td-content">
										<div id="update_standard_flg">
											<input type="radio" name="update_tandard_flg" id="update_standard_flg_yes" class="ui-widget-content" value="1"><label for="update_standard_flg_yes">是</label>
											<input type="radio" name="update_tandard_flg" id="update_standard_flg_no" class="ui-widget-content" value="0"><label for="update_standard_flg_no">否</label>
										</div>
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input id="updatebutton" class="ui-button" type="button"  value="更新" style="float:left;left:2px">
								<input id="cancelbutton" class="ui-button"  type="button" value="取消" style="float:left;left:2px">
							</div>
						</form>
					</div>
				</div>
				
			</div>
			<div class="clear areaencloser"></div>
		</div>
	</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
.show_photo {
	max-width:320px;height:auto;
}
.pack_part {
	width: 70px;
}
.part_prop {
	display:none;
}
form.part_vis .part_prop {
	display: inline;
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
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/admin/supplies_refer_list.js"></script>

<title>常用采购清单</title>
</head>
<body class="outer">
	<div class="width-full" style="margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="padding-top: 16px;">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=init" flush="true">
					<jsp:param name="linkto" value="资源功能"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" class="dwidth-middleright" style="margin: auto;float:left;">
				<div id="searcharea">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
						<span class="areatitle">检索条件</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div class="ui-widget-content dwidth-middleright">
						<form id="searchform" method="POST" onsubmit="return false;">
							<table class="condform">
								<tr>
									<td class="ui-state-default td-title">品名</td>
									<td class="td-content">
										<input type="text" id="search_product_name" class="ui-widget-content">
									</td>
									<td class="ui-state-default td-title">规格</td>
									<td class="td-content">
										<input type="text" id="search_model_name" class="ui-widget-content">
									</td>
									<td class="ui-state-default td-title">供应商</td>
									<td class="td-content">
										<input type="text" id="search_supplier" class="ui-widget-content">
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input type="button" class="ui-button" id="resetbutton" value="清除" style="float:right;right:2px">
								<input type="button" class="ui-button" id="searchbutton" value="检索" style="float:right;right:2px">
							</div>
						</form>
					</div>
					
					<div class="clear areaencloser"></div>
					
					<div id="listarea">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">常用采购清单一览</span>
							<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<table id="list"></table>
						<div id="listpager"></div>
					</div>
				</div>
				
				<div id="addarea" style="display: none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
						<span class="areatitle">新建常用采购清单</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					<div class="ui-widget-content dwidth-middleright">
						<form id="addform" method="POST" onsubmit="return false;">
							<table class="condform">
								<tr>
									<td class="ui-state-default td-title">品名</td>
									<td class="td-content">
										<input type="text" id="add_product_name" name="product_name" class="ui-widget-content" alt="品名">
									</td>
									<td class="ui-state-default td-title" style="width:600px;">采购清单照片</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">规格</td>
									<td class="td-content">
										<input type="text" id="add_model_name" name="model_name" class="ui-widget-content" alt="规格">
										<br>
										（内容数量: <input type="text" id="add_capacity" name="capacity" class="ui-widget-content pack_part" alt="包装数量">）
									</td>
									<td class="td-content">
										<input type="file" id="add_upload_file" name="file" />
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">预定单价</td>
									<td class="td-content">
										<input type="text" id="add_unit_price" name="unit_price" class="ui-widget-content pack_part part_prop" alt="预定单价">
										<span class="part_prop"> / </span>
										<input type="text" id="add_package_unit_price" name="package_unit_price" class="ui-widget-content pack_part" alt="预定单价">
									</td>
									<td class="td-content" rowspan="4" contenteditable="true">
										<img class="show_photo"></img>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">单位</td>
									<td class="td-content">
										<input type="text" id="add_unit_text" name="unit_text" class="ui-widget-content pack_part part_prop" alt="单件单位">
										<span class="part_prop"> / </span>
										<input type="text" id="add_package_unit_text" name="package_unit_text" class="ui-widget-content pack_part" alt="采购单位">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">供应商</td>
									<td class="td-content">
										<input type="text" id="add_supplier" name="supplier" class="ui-widget-content" alt="供应商">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">商品编号</td>
									<td class="td-content">
										<input type="text" id="add_goods_serial" name="goods_serial" class="ui-widget-content" alt="商品编号">
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input type="button" class="ui-button" id="addCancelButton" value="取消" style="float:right;right:2px">
								<input type="button" class="ui-button" id="addbutton" value="新建" style="float:right;right:2px">
							</div>
						</form>
					</div>
				</div>
				
				<div id="updatearea" style="display: none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
						<span class="areatitle">更新常用采购清单</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					<div class="ui-widget-content dwidth-middleright">
						<form id="updateform" method="POST" onsubmit="return false;">
							<table class="condform">
								<tr>
									<td class="ui-state-default td-title">品名</td>
									<td class="td-content">
										<input type="text" id="update_product_name" name="product_name" class="ui-widget-content" alt="品名">
									</td>
									<td class="ui-state-default td-title" style="width:600px;">采购清单照片</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">规格</td>
									<td class="td-content">
										<input type="text" id="update_model_name" name="model_name" class="ui-widget-content" alt="规格">
										<br>
										（内容数量: <input type="text" id="update_capacity" name="capacity" class="ui-widget-content pack_part" alt="包装数量">）
									</td>
									<td class="td-content">
										<input type="file" name="file" id="update_upload_file" />	
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">预定单价</td>
									<td class="td-content">
										<input type="text" id="update_unit_price" name="unit_price" class="ui-widget-content pack_part part_prop" alt="单件单价">
										<span class="part_prop"> / </span>
										<input type="text" id="update_package_unit_price" name="package_unit_price" class="ui-widget-content pack_part" alt="采购单价">
									</td>
									<td class="td-content" rowspan="4" contenteditable="true">
										<img class="show_photo"></img>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">单位</td>
									<td class="td-content">
										<input type="text" id="update_unit_text" name="unit_text" class="ui-widget-content pack_part part_prop" alt="单件单位">
										<span class="part_prop"> / </span>
										<input type="text" id="update_package_unit_text" name="package_unit_text" class="ui-widget-content pack_part" alt="采购单位">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">供应商</td>
									<td class="td-content">
										<input type="text" id="update_supplier" name="supplier" class="ui-widget-content" alt="供应商">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">商品编号</td>
									<td class="td-content">
										<input type="text" id="update_goods_serial" name="goods_serial" class="ui-widget-content" alt="商品编号">
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input type="button" class="ui-button" id="updateCancelButton" value="取消" style="float:right;right:2px">
								<input type="button" class="ui-button" id="updatebutton" value="更新" style="float:right;right:2px">
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
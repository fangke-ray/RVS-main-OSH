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

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery.mtz.monthpicker.min.js"></script>
<script type="text/javascript" src="js/support/supplies_detail.es5.js?v=2"></script>

<style type="text/css">
ul.container{
	list-style: none;
	margin: 0;
	padding:4px;
	display: grid;
	justify-content:space-around;
	grid-template-columns: repeat(3,1fr);
	gap: 4px;
}
.item {
	box-sizing: border-box;
	padding: 2px;
	border:1px solid #aaaaaa;
	border-radius: 4px;
	background-color: #fff;
	overflow: hidden;
}
.item:hover {
	box-shadow: 0 0 10px 2px #aaaaaa;
}
.item:hover img {
	transform: scale(1.01);
}
.item .grid-container {
	display: grid;
	grid-template-columns: 134px 1fr;
}
.item .grid-container .grid-item {
	display: grid;
	grid-template-columns: 60px 1fr;
	font-size: 14px;
}
.item .grid-container .grid-item.add {
	grid-template-columns: 1fr;
}
.item .grid-container .grid-item .title {
	font-weight: bold;
}
.item .grid-container .grid-item .price {
	color: red;
	font-size: 15px;
}
.item .grid-container .grid-item .price::before {
	content: '￥';
}
.item .grid-container .grid-item .model-name select {
	box-sizing: border-box;
	min-width: 80px;
	height: 21px;
	border: 1px solid #aaa;
	padding: 0;
	margin: 0;
	border-radius: 2px;
	font-size: 12px;
}

.item .grid-container .grid-item span.capacity,
.item .grid-container .grid-item span.goods_serial {
	margin-left: 1em;
	font-size: smaller;
}

.item .grid-container .grid-item.supplier-item-hidden {
	display:none;
}

#cutOffTip{
	display:none;
	color:#000;
	font-size: 16px;
	font-weight: bold;
	color: gold;
	float: right;
	line-height: 2em;
}

#updateform textarea {
	resize: none;
	font-size: 12px;
	width: 550px;
	height: 44px;
}
#ball {
	position: fixed;
	z-index: 2000;
}
#list button.ui-button-text-only {
	float: right;
}
#list button.ui-button-text-only .ui-button-text {
	padding: 0 .4em;
}
</style>

<title>物品申购</title>

<% 
	boolean isMamager = (Boolean) request.getAttribute("isMamager");
	boolean isSupport = (Boolean) request.getAttribute("isSupport");
	boolean isLiner = (Boolean) request.getAttribute("isLiner");
	boolean signEdit = (Boolean) request.getAttribute("signEdit");
%>

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
									<td class="ui-state-default td-title" rowspan="2">申请课室</td>
									<td class="td-content" rowspan="2" colspan="3">
										<select id="search_section_id">${sectionOptions }</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">规格</td>
									<td class="td-content">
										<input type="text" id="search_model_name" class="ui-widget-content">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">申购人员</td>
									<td class="td-content">
										<input type="text" id="search_applicator_name" style="width:6em;" class="ui-widget-content">
										<input type="hidden" id="hidden_search_applicator_id">
										<input type="button" id="btn_self_applicator" class="ui-button" value="本人">
									</td>
									<td class="ui-state-default td-title">申请日期</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="search_applicate_date_start" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="search_applicate_date_end" readonly="readonly">止
									</td>
									<td class="ui-state-default td-title">上级确认者</td>
									<td class="td-content">
										<input type="text" id="search_confirmer_name" class="ui-widget-content">
										<input type="hidden" id="hidden_search_confirmer_id">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">申购单号</td>
									<td class="td-content">
										<input type="text" id="search_order_no" class="ui-widget-content">
									</td>
									<td class="ui-state-default td-title">预计到货日期</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="search_scheduled_date_start" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="search_scheduled_date_end" readonly="readonly">止
									</td>
									<td class="ui-state-default td-title">收货日期</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="search_recept_date_start" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="search_recept_date_end" readonly="readonly">止
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">预算月</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="search_budget_month" readonly="readonly">
									</td>
									<td class="ui-state-default td-title" rowspan=2>验收日期</td>
									<td class="td-content" rowspan=2>
										<input type="text" class="ui-widget-content" id="search_inline_recept_date_start" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="search_inline_recept_date_end" readonly="readonly">止
									</td>
									<td class="ui-state-default td-title" rowspan=2>进行状态</td>
									<td class="td-content" rowspan=2 id="search_step_set">
										<input type="checkbox" name="step" <%=(isLiner || isMamager ?"checked" : "")%> id="step_a" class="ui-helper-hidden-accessible" value="1"><label for="step_a" aria-pressed="false">申购提出</label>
										<input type="checkbox" name="step" <%=(isSupport || signEdit ?"checked" : "")%> id="step_o" class="ui-helper-hidden-accessible" value="2"><label for="step_o" aria-pressed="false">申购过程</label>
										<input type="checkbox" name="step" <%=(isLiner || isMamager || isSupport ?"checked" : "")%> id="step_t" class="ui-helper-hidden-accessible" value="4"><label for="step_t" aria-pressed="false">等待到货/验收</label>
										<input type="checkbox" name="step" id="step_f" class="ui-helper-hidden-accessible" value="8"><label for="step_f" aria-pressed="false">已验收</label>
										<input type="hidden" name="step" id="step_reset">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">发票号</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="search_invoice_no">
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input class="ui-button" id="resetbutton" value="清除" style="float:right;right:2px" type="button">
								<input class="ui-button" id="searchbutton" value="检索" style="float:right;right:2px" type="button">
							</div>
						</form>
					</div>
					
					<div class="areaencloser"></div>
					
					<div id="listarea">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">物品申购明细一览</span>
							<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<table id="list"></table>
						<div id="listpager"></div>
						<div class="ui-widget-header areabase dwidth-middleright" style="padding-top:4px;">
							<div id="executes" style="margin-left:4px;margin-top:4px;">
								<input class="ui-button" id="confirmOrderButton" value="确认订购单" type="button" style="float:right;margin-right: 4px;">
<%if(isMamager || isLiner || isSupport){ %>						
								<input class="ui-button" id="applicationButton" value="申请" type="button">
	<% if(isLiner){ %>
								<input class="ui-button" id="postButton" value="发送通知" type="button" style="margin-left: 2px;">
	<% } %>
	<% if(isMamager){ %>
								<input class="ui-button" id="batchOkButton" value="批量确认" type="button" style="margin-left: 2px;">
	<% } %>
	<% if(isSupport){ %>
								<input class="ui-button" id="editButton" value="编辑订购单" type="button" style="float:right;margin-right: 4px;">
								<input class="ui-button" id="receptButton" value="收货" type="button" style="float:right;margin-right: 4px;">
								<input class="ui-button" id="invoiceButton" value="填写发票号" type="button" style="float:right;margin-right: 4px;">
	<% } %>
								<input class="ui-button" id="inlineReceptbutton" value="验收" type="button" style="margin-left: 2px;">
<% } %>
							</div>
						</div>
					</div>
				</div>

				<div id="updatearea" style="display: none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
						<span class="areatitle">编辑物品申购明细</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					<div class="ui-widget-content dwidth-middleright">
						<form id="updateform" method="POST" onsubmit="return false;"></form>
					</div>
				</div>
			</div>
			<div class="clear areaencloser"></div>
			
			<div id="add_supplies_detail" style="display: none;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">常用采购清单</span>
				</div>
				<div class="ui-widget-content">
					<form method="POST" onsubmit="return false;">
						<table class="condform">
							<tr>
								<td class="ui-state-default td-title">品名</td>
								<td class="td-content">
									<input type="text" id="filter_product_name" class="ui-widget-content">
								</td>
								<td style="border:0;">
									<input type="button" class="ui-button" id="filterSearchButton" value="筛选">
								</td>
							</tr>
						</table>
					</form>
				</div>

				<div class="ui-widget-content" id="filterContainer" style="overflow-y:scroll;height: 266px;margin-top: 4px;">
					<ul class="container"></ul>
				</div>

				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser" style="margin-top: 4px;">
					<span class="areatitle">新建物品申购明细</span>
					<div id="cutOffTip" class="icon-bell">
						目前已过本周申请截至日期，您的申请将延至下周受理。
					</div>
				</div>
				<div class="ui-widget-content">
					<form id="addForm" method="POST" onsubmit="return false;">
						<table class="condform">
							<tr>
								<td class="ui-state-default td-title">品名</td>
								<td class="td-content">
									<input type="text" id="add_product_name" name="product_name" class="ui-widget-content" alt="品名">
								</td>
								<td class="ui-state-default td-title">规格</td>
								<td class="td-content">
									<input type="text" id="add_model_name" name="model_name" class="ui-widget-content" alt="规格">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">数量</td>
								<td class="td-content">
									<input type="text" id="add_quantity" name="quantity" class="ui-widget-content" alt="数量">
								</td>
								<td class="ui-state-default td-title">单位</td>
								<td class="td-content">
									<input type="text" id="add_unit_text" name="unit_text" class="ui-widget-content" alt="单位">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">预定单价</td>
								<td class="td-content">
									<input type="text" id="add_unit_price" name="unit_price" class="ui-widget-content" alt="预定单价">
								</td>
								<td class="ui-state-default td-title">供应商</td>
								<td class="td-content">
									<input type="text" id="add_comment" name="comment" class="ui-widget-content" alt="供应商">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">
									用途
									<input type="checkbox" id="urgent_flg" class="ui-button"></input>
									<label style="border:1px solid #db4865;" for="urgent_flg">加急申请</label>
								</td>
								<td class="td-content" colspan="3">
									<textarea id="add_nesssary_reason" name="nesssary_reason" class="ui-widget-content" alt="用途" style="resize: none;font-size: 12px; width: 560px; height: 44px;"></textarea>
								</td>
							</tr>
						</table>
					</form>
				</div>
			</div>

			<div id="edit_supplise" style="display: none;">
				<div class="ui-widget-content" style="margin-bottom: 4px;">
					<form id="addOrderForm" method="POST" onsubmit="return false;">
						<table class="condform">
							<tr>
								<td class="ui-state-default td-title">申购单号</td>
								<td class="td-content" style="width: 220px;">
									<span>RC-FY<span id="order_no_prefix"></span>-</span>
									<input type="text" id="add_order_no" name="order_no" class="ui-widget-content" alt="申购单号">
								</td>
							</tr>
						</table>
					</form>
				</div>
				<table id="edit_list"></table>
				<div id="edit_listpager"></div>
			</div>

			<div id="edit_recept" style="display: none;">
				<table id="recept_list"></table>
				<div id="recept_listpager"></div>
			</div>

			<div id="confirmmessage"></div>

			<div id="confirm_order" style="display:none">
				<table id="order_list"></table>
				<div id="order_listpager"></div>
			</div>

			<div id="sign_order" style="display: none;">
				<div class="ui-widget-content">
					<form method="POST" onsubmit="return false;">
						<table class="condform">
							<tr style="height: 38px;">
								<td class="ui-state-default td-title">经理印</td>
								<td class="td-content">
<% if(signEdit) { %>
									<input type="button" id="confirm_sign_manager_id" class="ui-button" value="盖章" style="display: none;">
<% } %>
									<img id="confirm_sign_manager_id_pic" style="display: none"></img>
								</td>
								<td class="ui-state-default td-title">部长印</td>
								<td class="td-content">
<% if(signEdit) { %>
									<input type="button" id="confirm_sign_minister_id" class="ui-button" value="盖章" style="display: none;">
<% } %>
									<img id="confirm_sign_minister_id_pic" style="display: none"></img>
								</td>
							</tr>
						</table>
					</form>
				</div>

				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser" style="margin-top: 4px;">
					<span class="areatitle">物品申购明细</span>
				</div>
				<table id="order_detail_list"></table>
				<div id="order_detail_listpager"></div>
			</div>
			
			<!-- 申购人员referChooser -->
			<div class="referchooser ui-widget-content" id="operator_referchooser" tabindex="-1">
				<table width="200px">
					<tr>
						<td></td>
						<td width="50%">过滤字:<input type="text"/></td>
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table class="subform">${oReferChooser}</table>
			</div>

			<input type="hidden" id="loginID" value="${loginID }">
			<input type="hidden" id="jobNo" value="${jobNo }">
			<input type="hidden" id="isMamager" value="${isMamager }">
			<input type="hidden" id="isLiner" value="${isLiner }">
			<input type="hidden" id="isSupport" value="${isSupport }">
			<input type="hidden" id="signEdit" value="${signEdit }">
		</div>
	</div>
	<div id="ball"></div>

	<div id="postDialog" style="display:none;">
		<table class="subform" style="cursor:pointer;">${oReferChooser}</table>
	</div>

</body>
</html>
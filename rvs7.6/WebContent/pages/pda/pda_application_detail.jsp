<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.osh.rvs.form.pda.PdaApplyElementForm"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>


<%
	String user_name = (String)request.getSession().getAttribute("user_name");
	String errors = (String) request.getAttribute("errors");
%>

<html:html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	window.onload = function(){
		var table = document.getElementById("consumable_application_table");
		var tr = table.getElementsByTagName('tr');
		for(var i = 0;i<tr.length;i++) {
			if (tr[i].className !== "th-2") continue;

		    var next = get_nextsibling(tr[i]);
		    var emptyType = false;
		    if (next == null) {
		    	emptyType = true;
		    } else {
		    	emptyType = (next.className != "cnt-1");
		    }
		    if (!emptyType) continue;
		    var prev = get_prevsibling(tr[i]);

	    	tr[i].style.display = "none";
	    	prev.style.display = "none";

		}

		var errmsg = '<%=errors%>';
		if (errmsg != 'null' && errmsg != "") {
			alert(errmsg);
		}

		var objCode = document.getElementsByName("code")[0];
		objCode.value="";
		objCode.focus();//获取焦点 

		var time = null;
		objCode.onkeyup = function(e){
			if (this.value.length >= 6 && this.value.length <=9){
				clearTimeout(time);
				time = setTimeout(function(){
					document.forms["pdaApplyDetailForm"].action = "pda_apply_detail.do?method=getDetail";
					document.forms["pdaApplyDetailForm"].submit();
					clearTimeout(time);
				},1000);
			}
		};
		objCode.onmousedown = function(e){
			this.style.background = "white";
			this.style.color = "black";
		}

		var objDiv = document.getElementById("div_main");
		
		var objScrollTop = document.getElementById("scrollTop");
		objDiv.scrollTop = objScrollTop.value;
		
		objDiv.onscroll = function(){
			objScrollTop.value = this.scrollTop;
			objCode.focus();
		};
		objDiv.onclick = function(){
			objCode.focus();
		};

		var objGoback = document.getElementById("goback");
		objGoback.onclick = function(){
			window.location.href="pda_apply.do?method=init";
		};

		history.forward();
	};

	function get_prevsibling(n)
	{
		var x=n.previousSibling;
		if (x == null) return null;
		while (x != null && x.nodeType!=1)
		{
			x=x.previousSibling;
		}
		return x;
	}

	function get_nextsibling(n)
	{
		var x=n.nextSibling;
		if (x == null) return null;
		while (x != null && x.nodeType!=1)
		{
			x=x.nextSibling;
		}
		return x;
	}

	function doUpdate(supplied_flg) {
		var objBtn_flg = document.getElementsByName("btn_flg")[0];
		if (supplied_flg == "0" && objBtn_flg.value == "all") {
			return false;
		}
		document.getElementsByName("supplied_flg")[0].value = supplied_flg;
		document.forms["pdaApplyDetailForm"].action = "pda_apply_detail.do?method=doUpdate";
		document.forms["pdaApplyDetailForm"].submit();
	};
</script>
<title>消耗品申请单详细</title>
<link rel="stylesheet" type="text/css" href="css/pda/pda_custom.css">

</head>
<body>
	<html:form action="/pda_apply_detail" method="post" onsubmit="return false">
	<div class="width-full">

		<div class="header" style="height:40px;">
			<div class="left" style="color:#fff;margin-left:10px;">
				<h5>
					<label>你好! <%=user_name %></label><br>
					<bean:write name="pdaApplyDetailForm" property="application_no"/>
				</h5>
			</div>
			<div class="right" style="color:#fff;margin-right:10px;">
				<div>
					<html:text name="pdaApplyDetailForm" property="code" style="ime-mode:disabled;background:transparent;border:0;height:15px;color:transparent;width:50px;"/>
					<div style="cursor:pointer;display:inline;" id="goback">←</div>
				</div>
				<div style="text-align:right">
					<h5>待处理 <bean:write name="pdaApplyDetailForm" property="count"/> 单</h5>
				</div>
			</div>
			<div class="clear"></div>
		</div>

		<div id="div_main" class="main" style="height:150px;padding-left:10px;overflow-y:scroll;overflow-x:hidden;">
		<style>
		.con-up {
			border-bottom-style : dashed;
		}
		.con-down {
			border-top-style : dashed;
		}
		</style>
			<table id="consumable_application_table" style="width:95%;font-size:14px;">
				<html:hidden name="pdaApplyDetailForm" property="consumable_application_key"/>
				<logic:notEmpty name="pdaApplyDetailForm" property="omr_notifi_no">
					<tr style="height:10px;">
						<td class="td-title" colspan="2">修理单号及理由</td>
					</tr>
					<tr>
						<td colspan="2"><bean:write name="pdaApplyDetailForm" property="omr_notifi_no"/></td>
					</tr>
					<tr>
						<td colspan="2"><bean:write name="pdaApplyDetailForm" property="apply_reason"/></td>
					</tr>
				</logic:notEmpty>

				<logic:iterate id="element" name="pdaApplyDetailForm" property="detail_list" type="PdaApplyElementForm" indexId="index">
				<logic:equal name="element" property="header_flg" value="1">
					<tr class="th-1">
						<logic:notEqual name="element" property="type" value="9">
							<td class="td-title con-up" style="width:55px;">类别</td>
							<td class="td-title con-up" style="width:50px;">代码</td>
						</logic:notEqual>
						<logic:equal name="element" property="type" value="9">
							<td class="td-title con-up" style="width:55px;" colspan="2">说明</td>
						</logic:equal>
					</tr>
					<tr class="th-2">
						<td class="td-title con-down">库位编号</td>
						<td class="td-title con-down">已发放/申请</td>
					</tr>
				</logic:equal>

				<bean:define id="tr_color" value=""/>
<!--			<logic:equal name="element" property="disp_flg" value="1">
				</logic:equal>
-->

				<logic:notEqual name="element" property="disp_flg" value="1">
				<logic:notEqual name="element" property="disp_flg" value="G1">
				<logic:notEqual name="element" property="disp_flg" value="H1">
					<tr class="cnt-1">
						<logic:notEqual name="element" property="type" value="9">
							<td class="con-up">
								<logic:notEmpty name="element" property="cut_length">
									<span class="label_cut_length"><bean:write name="element" property="cut_length"/> MM</span> /
								</logic:notEmpty>
								<bean:write name="element" property="type_name"/>
							</td>
							<td class="con-up"><bean:write name="element" property="code"/></td>
						</logic:notEqual>
						<logic:equal name="element" property="type" value="9">
							<td class="con-up" colspan="2"><bean:write name="element" property="name"/></td>
						</logic:equal>
					</tr>
					<tr class="cnt-2">
						<td class="con-down"><bean:write name="element" property="stock_code"/></td>
						<td class="con-down" class="qty"><bean:write name="element" property="disp_supply_quantity"/>/<bean:write name="element" property="apply_quantity"/></td>
					</tr>
				</logic:notEqual>
				</logic:notEqual>
				</logic:notEqual>
				</logic:iterate>
			</table>
		</div>

		<div class="bottom" style="height:20px;">
			<html:hidden name="pdaApplyDetailForm" property="btn_flg"/>
			<html:hidden name="pdaApplyDetailForm" property="supplied_flg"/>
			<logic:empty name="pdaApplyDetailForm" property="scrollTop">
				<input type="hidden" name="scrollTop" id="scrollTop" value="0" />
			</logic:empty>
			<logic:notEmpty name="pdaApplyDetailForm" property="scrollTop">
				<input type="hidden" name="scrollTop" id="scrollTop" value='<bean:write name="pdaApplyDetailForm" property="scrollTop"/>' />
			</logic:notEmpty>
			
			<div class="button button-small" style="float:right;margin-right:20px;" onClick="doUpdate('0');">部分发放</div>
			<div class="button button-small" style="float:right;margin-right:20px;" onClick="doUpdate('1');">发放完成</div>
		</div>
	</div>
	</html:form>
</body>
</html:html>
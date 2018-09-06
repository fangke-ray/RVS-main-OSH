<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>

<%
String  user_name  = (String)request.getSession().getAttribute("user_name");

String errors = (String) request.getAttribute("errors");
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html:html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
	window.onload = function(){
		var errmsg = "<%=errors%>";
		if (errmsg != "null" && errmsg != "") {
			alert(errmsg);
		}
		
		var objCode = document.getElementById("code");
		objCode.focus();//获取焦点

		var time = null;
		objCode.onkeyup = function(e){
			if (this.value.length >= 6 && this.value.length <=9){
				clearTimeout(time);
				time = setTimeout(function(){
					document.forms["PdaSupplyForm"].action = "/rvs/pda_supply.do?method=search";
					document.forms["PdaSupplyForm"].submit();
				},200);
			}
		};
		objCode.onmousedown = function(e){
			this.style.background = "white";
			this.style.color = "black";
		}
		
		var objQuantity = document.getElementById("quantity");
		var objPartialId = document.getElementById("partial_id");
		
		if(objPartialId!=null){
			objQuantity.disabled = "";
			objQuantity.focus();
			objQuantity.onkeyup = function(e){
				var keyCode = event.keyCode || e.keyCode;
				if(keyCode == 13){// 回车
					if(/^\d+$/.test(this.value) == false){
						alert("请输入数字");
						return;
					}
					
					if(this.value.length > 5){
						alert("请输入长度小于6的值");
						return;
					}
					
					document.forms["PdaSupplyForm"].action = "/rvs/pda_supply.do?method=doSupply";
					document.forms["PdaSupplyForm"].submit();
				}
			};
		}else{
			objQuantity.disabled = "disabled";
		}

		var objGoback = document.getElementById("goback");
		objGoback.onclick = function(){
			window.location.href="appmenu.do?method=pdaMenu";
		};

		// history.forward();
	};
</script>
<title>消耗品入库</title>
<link rel="stylesheet" type="text/css" href="css/pda/pda_custom.css">
</head>
<body>
	<html:form onsubmit="return false">
		<div class="width-full">
		
			<div class="header" style="height:40px;">
				<div class="left" style="color:#fff;margin-left:10px;">
					<h5>
						<label>你好! <%=user_name %></label><br>
						消耗品入库
					</h5>
				</div>
				<div class="right" style="color:#fff;margin-right:10px;">
					<div>
						<input type="text" id="code" name="code" style="background:transparent;border:0;height:15px;color:transparent;width:50px;margin-right:19px;">
						<div style="cursor:pointer;display:inline;" id="goback">←</div>
					</div>
					<div  style="txet-align:right;">
						<h5>今日已入库 <%=request.getAttribute("supply_num") %> 项</h5>
					</div>
				</div>
				<div class="clear"></div>
			</div>
			
			<div class="main" style="height:130px;padding:10px;">
				<div class="left" style="width:50%;">
					<table style="width:100%;height:130px;">
						<tr>
							<td class="td-title" style="width:40%;">消耗品</td>
							<td style="width:40%;"><logic:notEmpty name="pdaSupplyForm"><bean:write name="pdaSupplyForm" property="code"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">类型</td>
							<td><logic:notEmpty name="pdaSupplyForm"><bean:write name="pdaSupplyForm" property="type_name"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">有效数量</td>
							<td class="qty"><logic:notEmpty name="pdaSupplyForm"><bean:write name="pdaSupplyForm" property="available_inventory"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">在途数量</td>
							<td class="qty"><logic:notEmpty name="pdaSupplyForm"><bean:write name="pdaSupplyForm" property="on_passage"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">库位</td>
							<td><logic:notEmpty name="pdaSupplyForm"><bean:write name="pdaSupplyForm" property="stock_code"/></logic:notEmpty></td>
						</tr>
					</table>
				</div>
				<div class="right" style="width:128px;border:1px solid;">
					<div style="width:128px;height:128px;">
						<img src="...jpg" alt="128*128图片" onerror="javascript:this.src='images/noimage128x128.gif'"/>
					</div>
				</div>
				<div class="clear"></div>
			</div>
			
			<div class="bottom" style="height:110px;">
				<div style="width:70%;margin:0 auto;font-size:14px;">
					<label>入库数量</label>&nbsp;&nbsp;
					<input type="text" class="input-txt" id="quantity" name="quantity"/>
				</div>
			</div>
		</div>
		<logic:notEmpty name="pdaSupplyForm">
			<input type="hidden" id="partial_id" name="partial_id" value='<bean:write name="pdaSupplyForm" property="partial_id"/>'/>
		</logic:notEmpty>
	</html:form>
</body>
</html:html>

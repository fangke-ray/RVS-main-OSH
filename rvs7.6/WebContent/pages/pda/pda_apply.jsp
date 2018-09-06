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

		var objQuantity = document.getElementById("quantity");
		if(objQuantity!=null){
			objQuantity.focus();//获取焦点
			objQuantity.onkeyup = function(e){
				var keyCode = event.keyCode || e.keyCode;
				if(keyCode == 13){// 回车 
					if(/^\d+$/.test(this.value) == false){
						alert("请输入数字");
						return;
					}
					
					if(this.value.length > 4){
						alert("请输入长度小于5的值");
						return;
					}
					
					var content = document.getElementsByName("content")[0].value;
					var pack_method  = document.getElementsByName("pack_method")[0].value;
					
					if(content!=1 && pack_method == 0){
						document.forms["PdaApplyElementForm"].supply_quantity.value =objQuantity.value * content;
					}
					
					
					document.forms["PdaApplyElementForm"].action = "pda_apply_element.do?method=update";
					document.forms["PdaApplyElementForm"].submit();
				}
			};
		}
		
		var objBtn = document.getElementById("supply_btn");
		var objopen_flg = document.getElementById("open_flg");
		if(objBtn!=null){
			var i=1;
			var names = ["不发放","已开封提供","新开封提供"];
			var values = ["A","B","C"];
			objBtn.onclick = function(){
				if(i > 2){
					i =0;
				}
				this.value = names[i];
				objopen_flg.value = values[i];
				i++;
			};
		};
		
		var btn = document.getElementById("btn");
		if(btn!=null){
			btn.onclick = function(){
				document.forms["PdaApplyElementForm"].action = "pda_apply_element.do?method=update";
				document.forms["PdaApplyElementForm"].submit();
			};
		}
		
		var objGoback = document.getElementById("goback");
		objGoback.onclick = function(){
			document.forms["PdaApplyElementForm"].action = "pda_apply.do?method=getDetail";
			document.forms["PdaApplyElementForm"].submit();
		};

		// history.forward();
	};
</script>
<title>消耗品申请单发放</title>
<link rel="stylesheet" type="text/css" href="css/pda/pda_custom.css">
</head>
<body>
	<html:form action="/pda_apply_element" method="post" onsubmit="return false">
		<div class="width-full">
		
			<div class="header" style="height:40px;">
				<div class="left" style="color:#fff;margin-left:10px;">
					<h5>
						<label>你好! <%=user_name %></label><br>
						<logic:notEmpty name="pdaApplyElementForm"><bean:write name="pdaApplyElementForm" property="application_no"/></logic:notEmpty>
					</h5>
				</div>
				<div class="right" style="color:#fff;margin-right:10px;">
					<div>
						<div style="cursor:pointer;display:inline-block;margin-left:45px;" id="goback">←</div>
					</div>
					<div style="txet-align:right;">
						<h5>待处理<logic:notEmpty name="pdaApplyElementForm"><bean:write name="pdaApplyElementForm" property="count"/></logic:notEmpty>项</h5>
					</div>
				</div>
				<div class="clear"></div>
			</div>
			
			<div class="main" style="height:130px;padding:10px;">
				<div class="left" style="width:50%;">
					<table style="width:100%;height:130px;">
						<tr>
							<td class="td-title" style="width:40%;">消耗品</td>
							<td style="width:40%;"><logic:notEmpty name="pdaApplyElementForm"><bean:write name="pdaApplyElementForm" property="code"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">类型</td>
							<td><logic:notEmpty name="pdaApplyElementForm"><bean:write name="pdaApplyElementForm" property="type_name"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">有效数量</td>
							<td class="qty"><logic:notEmpty name="pdaApplyElementForm"><bean:write name="pdaApplyElementForm" property="available_inventory"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">在途数量</td>
							<td class="qty"><logic:notEmpty name="pdaApplyElementForm"><bean:write name="pdaApplyElementForm" property="on_passage"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">库位</td>
							<td><logic:notEmpty name="pdaApplyElementForm"><bean:write name="pdaApplyElementForm" property="stock_code"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">申请数量</td>
							<td class="qty"><logic:notEmpty name="pdaApplyElementForm"><bean:write name="pdaApplyElementForm" property="apply_quantity"/></logic:notEmpty></td>
						</tr>
						<tr>
							<td class="td-title">申请总价</td>
							<td class="qty"><logic:notEmpty name="pdaApplyElementForm"><logic:notEmpty name="pdaApplyElementForm" property="total_price"><bean:write name="pdaApplyElementForm" property="total_price"/></logic:notEmpty></logic:notEmpty></td>
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
				<div style="width:80%;margin:0 auto;font-size:14px;">
					<label>发放数量</label>&nbsp;&nbsp;
					<logic:notEmpty name="pdaApplyElementForm">
						<logic:equal name="pdaApplyElementForm" property="content" value="1">
							<input type="button" id="supply_btn" value="不发放" class="button button-small" style="height:20px;padding:2px; 4px;"/>
							<input type="hidden" name="open_flg" id="open_flg" value="A">
							<input type="button" value="提交" id="btn" class="button button-small" style="height:20px;padding:0px; 1px;width:50px;"/>
						</logic:equal>
						<logic:notEqual name="pdaApplyElementForm" property="content" value="1">
							<logic:equal name="pdaApplyElementForm" property="pack_method"  value="0">
								<input type="hidden" name="open_flg" id="open_flg" value="D">
								<input type="text" class="input-txt half" id="quantity" name="supply_quantity" style="ime-mode: disabled;"/><bean:write name="pdaApplyElementForm" property="unit_name"/>
							</logic:equal>
							<logic:notEqual name="pdaApplyElementForm" property="pack_method" value="0">
								<input type="hidden" name="open_flg" id="open_flg" value="E">
								<input type="text" class="input-txt half" id="quantity" name="supply_quantity" style="ime-mode: disabled;"/>
							</logic:notEqual>
						</logic:notEqual>
					</logic:notEmpty>
				</div>
			</div>
		</div>
		
		<logic:notEmpty name="pdaApplyElementForm">
			<html:hidden name="pdaApplyElementForm"  property="consumable_application_key" />
			<html:hidden name="pdaApplyElementForm"  property="partial_id" />
			<html:hidden name="pdaApplyElementForm"  property="content" />
			<html:hidden name="pdaApplyElementForm"  property="pack_method" />
		</logic:notEmpty>
		
	</html:form>
</body>
</html:html>

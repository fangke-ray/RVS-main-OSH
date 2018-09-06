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
<style>
#setarea form 
{
	padding-top:2px;
}
</style>

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<title>用户配置设置</title>
</head>
<body>
	<div id="setarea">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle">用户系统数据配置</span>
		</div>
		<div class="ui-widget-content dwidth-middleright">
			<div>
				<div class="ui-state-default" style="padding:4px;"><label>工位参数配置文件</label></div>
				<div style="float:left;padding:10px;width:400px;">positionsettings.properties</div>
				<form>
					<input type="button" class="download" value="最新配置取得"/>
					<input type="file" name="file"/>
					<input type="button" class="upload" value="最新配置导入"/>
				</form>
				<div class="clear"/>
			</div>
			<!--div>
				<div class="ui-state-default" style="padding:4px;"><label>展示用用户配置文件</label></div>
				<div style="float:left;padding:10px;width:400px;">G:\rvs\PROPERTIES\shippingamount.settings</div>
				<input type="button" value="最新配置导入" style="float:left;">
				<div class="clear"/>
			</div-->
			<div>
				<div class="ui-state-default" style="padding:4px;"><label>RVS推送邮件配置文件</label></div>
				<div style="float:left;padding:10px;width:400px;">mail.properties</div>
				<form>
					<input type="button" class="download" value="最新配置取得">
					<input type="file" name="file"/>
					<input type="button" class="upload" value="最新配置导入">
				</form>
				<div class="clear"/>
			</div>
			<div>
				<div class="ui-state-default" style="padding:4px;"><label>计划参数配置文件</label></div>
				<div style="float:left;padding:10px;width:400px;">schedule.properties</div>
				<form>
					<input type="button" class="download" value="最新配置取得">
					<input type="file" name="file"/>
					<input type="button" class="upload" value="最新配置导入">
				</form>
				<div class="clear"/>
			</div>
		</div>
		<div class="clear dwidth-middle"></div>
	</div>
</body>
</html>
<script>
	var downProp = function(file_name) {
	if ($("iframe").length > 0) {
			$("iframe").attr("src", "download.do"+"?method=output&fileName="+ file_name +"&from=prop");
		} else {
			var iframe = document.createElement("iframe");
			iframe.src = "download.do"+"?method=output&fileName="+ file_name +"&from=prop";
			iframe.style.display = "none";
			document.body.appendChild(iframe);
		}
	}

	var upProp = function(file_name) {
		$.ajaxFileUpload({
			url : 'upload.do?method=doProp', // 需要链接到服务器地址
			secureuri : false,
			fileElementId : file_name, // 文件选择框的id属性
			dataType : 'json', // 服务器返回的格式
			data: {prop_name : file_name},
			success : function(responseText, textStatus) {

				var resInfo = null;

				try {
					// 以Object形式读取JSON
					eval('resInfo =' + responseText);
				
					if (resInfo.errors.length > 0) {
						// 共通出错信息框
						treatBackMessages(null, resInfo.errors);
					} else {
						infoPop(file_name + "更新完成");
					}
				} catch(e) {
					
				}
			}
		});
	}

	$("input:button").button();
	$(".download").click(function(){
		var fileName = $(this).parent().prev().text();
		if (fileName) {
			downProp(fileName);
		}
	});

	$(".upload").click(function(){
		var fileName = $(this).parent().prev().text().replace(".properties", "");
		if (fileName) {
			upProp(fileName);
		}
	});

	$("#setarea form").each(function(){
		var fileName = $(this).prev().text().replace(".properties", "");
		$(this).children("input[type=file]").attr("id", fileName)
			.attr("accept", "text/x-java/properties");
	});
</script>


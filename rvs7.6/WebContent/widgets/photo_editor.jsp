<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
</head>
	<div id="photo_edit_area" class="d-cropper bg-content bg-content-border-h" style="background-color:white;width:100%;height:640px;z-index: 200;">
		<div style="position:relative;">
			<section class="detail-buttons" style="margin:1em;width:800px;border:1px solid black;text-align:left;
				left:30em;top:0;padding-left:1em;">
				<span>${no_image}</span>
			<div style="float:left;width:50%;">
			<form id="photo_upload_form" method="POST">
				
				<!--如果是单个文件上传的话name使用file -->
				<input type="file" name="file" id="photo_file"  value="上传"></input>
				<!--如果是多个文件上传的话name使用files(注：多个上传功能也支持单个文件上传)-->
				<!--<input type="file" name="file" id="photo_file" value="上传"></input>-->			
				
				<input type="button" id="photo_upload_button" class="ui-button" value="上传"></input>
				<input type="hidden" id="photo_uuid" value="${photo_uuid}"></input>
			</form>
			</div>
			<div style="float:left;width:50%;">
				<input type="button" id="photo_reset_button" class="ui-button" value="改回原图"></input>
			</div>
			<div class="clear"/>
			</section>
			<section class="detail-buttons" style="margin:1em;width:800px;border:1px solid black;text-align:left;
				left:30em;top:0;padding-left:1em;">
			<div style="float:left;width:50%;">
				请在图片上划定显示区域，并点击：
				<input type="button" id="image_crop_button" class="ui-button" value="选定"></input>
			</div>
			<!--div style="float:left;width:50%;">
				<input type="button" id="photo_mark_button" value="标记"></input>
			</div-->
			<div class="clear"/>
			</section>
			<div class="samp_pic" style="top: 1em;position: relative;margin:1em;width:800px;height:480px;">
				<img id="editted_image"></img>
			</div>
		</div>
	</div>

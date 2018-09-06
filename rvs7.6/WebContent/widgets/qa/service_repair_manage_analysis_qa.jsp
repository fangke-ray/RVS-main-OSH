<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>

<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<style>
  .delete_pic :hover{
     border:1px solid;
  }
</style>

<form id="ins_serviceRepairManage">
<div id="photo_edit_area" style="background:#fff;width:724px;">
		<div class="manageNo">
		  <u style="position:absolute;">管理NO.</u>
		  <input type="text" id="text_analysis_no" name="analysis_no" alt="管理NO." style="margin-top:7px;margin-left:100px;"/>
		</div>	
		<div class="top-title">保修期内返修品分析表</div>

	<div class="clear"></div>
   
    <div class="clear" style="margin-top:20px;"></div>
	<div>		
			<table id="table1" style="border-collapse:collapse;" width="100%">
               <tbody>
				<tr>
                    <td style="width:24px;border:1px solid black;"class="ui-state-default td-title td-border" rowspan="3">产品信息</td>
                    <td style="text-align:left;border:1px solid black;"class="ui-state-default td-title td-border ">产品名称</td>
                    <td class="td-border">
					   <label id="label_model_name"></label>
					</td>
                    <td class="td-border" rowspan="3"><u>不合格内容/用户提出</u></td>
                </tr>
                <tr>
                    <td style="text-align:left;border:1px solid black;" class="ui-state-default td-title td-border">BodyNo.</td>
					<td class="td-content td-border">
					   <label id="label_serial_no"></label>
					</td>
                </tr>
				<tr>
                    <td style="text-align:left;border:1px solid black;" class="ui-state-default td-title td-border">客户名称</td>
					<td class="td-content td-border">
						 <input id="edit_customer_name"style="text-align:center;width:95%;" type="text" name="customer_name" alt="客户名称"/>
					</td>					
                </tr>
				<tr>
				    <td style="width:24px;border:1px solid black;" class="ui-state-default td-title td-border" rowspan="3">
					上<br>次</td>
					<td style="text-align:left;border:1px solid black;" class="ui-state-default td-title td-border">完成日</td>	
					<td class="td-content td-border">
						<input id="text_last_shipping_date" style="text-align:center;"readonly="readonly" type="text" name="last_shipping_date" alt="完成日"/>
					</td>	
					<td class="td-content td-border"rowspan="3">
						<textarea id="text_last_trouble_feature"></textarea>
					</td>	
				</tr>  
				<tr>
				    <td style="text-align:left;border:1px solid black;" class="ui-state-default td-title td-border">修理单号</td>
					<td class="td-content td-border">
						<input id="text_last_sorc_no" style="text-align:center;" type="text" name="last_sorc_no" alt="修理单号"/>
					</td>
				</tr>
				<tr>
				    <td style="text-align:left;border:1px solid black;" class="ui-state-default td-title td-border">等级</td>
					<td class="td-content td-border">
					OCM：<input id="text_last_ocm_rank" style="width:50px;" type="text" name="last_ocm_rank" alt="等级"/>  
					翻修技术部：<input id="text_last_rank" style="width:50px;" type="text" name="last_rank" alt="翻修技术部"/>  
					</td>
				</tr>
				<tr>
				    <td style="width:24px;border:1px solid black;" class="ui-state-default td-title td-border" rowspan="3">此<br>次</td>
					<td style="text-align:left;border:1px solid black;" class="ui-state-default td-title td-border">受理日</td>	
					<td class="td-content td-border">
						<label id="label_reception_date"></label>
					</td>	
					<td class="td-content td-border"rowspan="3">
						<textarea  id="text_fix_demand"></textarea>
					</td>	
				</tr>  
				<tr>
				    <td style="text-align:left;border:1px solid black;" class="ui-state-default td-title td-border">修理单号</td>
					<td class="td-content td-border">
						<label id="label_sorc_no"></label>
					</td>
				</tr>
				<tr>
				    <td style="text-align:left;border:1px solid black;" class="ui-state-default td-title td-border">使用情况</td>
					<td class="td-content td-border">
						<label id="text_usage_frequency"></label>
					</td>
				</tr>
				</tbody>
            </table>
	</div>
	<div class="clear "></div>

	<div style="margin-top:20px;">	
			<table id="table_checkbox" style="border:1px solid #000000;border-collapse:collapse;" width="100%">
               <tbody>
				<tr>
                    <td style="width:30px;border:1px solid black;" class="ui-state-default td-title td-border"  rowspan="4">责<br>任<br>区<br>分</td>
                    <td style="border-top:1px solid #000000;"class="ui-state-default" align="center">1</td>
                    <td style="border-top:1px solid #000000;width:115px;"class="ui-state-default">自责:</td>
                    <td width="262px;" id="remorse">
						<input type='radio' name='analysis_result' id="huhu" value=11>零件问题
						<input type='radio' name='analysis_result' value=12>修理问题
					</td>
					<td class="td-checkbox-border td-border" rowspan="4">再修理方案:<br><label id="label_countermeasures" style="margin-left:80px;"></label></td>
                </tr>
                <tr>
                    <td class="ui-state-default" align="center">2</td>
                    <td class="ui-state-default">他责:</td>
                    <td>
						<input type='radio' name='analysis_result' value=21 checked="checked">用户问题
					</td>
                </tr>
				<tr>
                    <td class="ui-state-default" align="center">3</td>
                    <td class="ui-state-default">非自责:</td>
                    <td>
						<input type='radio' name='analysis_result' value=31>修理风险
						<input type='radio' name='analysis_result' value=32>非上次修理部分发生故障
					</td>					
                </tr>
				<tr>
				    <td style="border-bottom:1px solid #000000;"class="ui-state-default" align="center" >4</td>
                    <td style="border-bottom:1px solid #000000;"class="ui-state-default">其他:</td>
                    <td>
						<input type='radio' name='analysis_result' value=91>并未发现问题
						<input type='radio' name='analysis_result' value=92>其他
					</td>
				</tr>
				</tbody>
            </table>				
	</div>
	<div>
			<table id="table3" style="border:1px solid #000000;border-collapse:collapse;" width="100%">
               <tbody>
				<tr>
                    <td style="width:40px;border:1px solid black;" class="ui-state-default td-border" rowspan="3">故<br>障</td>
                    <td style="text-align:left;border:1px solid black;" class="ui-state-default td-border">现象原因(使用环境、频率等的次要原因)</td>
                </tr>
                <tr>
                    <td><b>技术检测/分析详述</b></td>             
                </tr>
				<tr>
			        <td>
					 <textarea id="text_trouble_discribe" name="trouble_discribe" style="height:100px;width:666px;"></textarea>
					</td>    
				</tr>
				<tr>
				   <td style="width:40px;border:1px solid black;" class="ui-state-default td-border" rowspan="3">原<br>因</td>
                   <td><b>结论:</b></td>
				</tr>  
				<tr>
				   <td>
					<textarea id="text_trouble_cause"style="height:100px;width:666px;"></textarea>
				   </td>
				</tr>
				<tr>
				   <td><b>故障处理日</b><label id="label_qa_referee_date"></label>
				</tr>
				</tbody>
            </table>				
	</div>
	<div style="margin-top:20px;">
			<table id="table4" style="border:1px solid #000000;border-collapse:collapse;" width="100%">
                <tbody>
				<tr>
                    <td style="width:40px;border:1px solid black;" class="ui-state-default td-border">对<br>应</td>
                    <td>
					  <textarea id="textarea_analysis_correspond_suggestion" style="height:40px;width:666px;"></textarea>
					</td>
                </tr>
				</tbody>
            </table>	
            
            <input type="button" id="import_pic"class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" role="button" aria-disabled="false" value="上传图片"/>
            <input id="get_uuid" type="text"/>
            <div id="photo_dialog"></div>
            
            <div id="append_images">
            	<!-- <img src="./images/delete_photo.png" style=""/> -->
            </div>
            
            <input type="hidden" id="hidden_detail_uuid"/>
            
	<%-- <div id="edit_importPic" style="display:none;">
	 <table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">上传图片</td>
						<td class="td-content">
							<input type="file" name="files" multiple="multiple" alt="上传图片路径" id="photo_file" class="ui-widget-content" />
						</td>
						<td>
							<input type="button" id="photo_upload_button" value="上传"/>
							<input type="hidden" id="photo_uuid" value="${photo_uuid}"></input>
						</td>
					   	<td>
							<div style="width:265px;">
								请在图片上划定显示区域，并点击：
								<input type="button" id="image_crop_button" value="选定"></input>
							</div>
						</td>
						<td>
							<div style="float:left;width:50%;">
								<input type="button" id="photo_reset_button" value="改回原图"></input>
							</div>
						</td>
					</tr>
				</tbody>				
		 </table>
		
		<div style="top: 1em;position: relative;margin:1em;width:auto;">
			<div id="delete_photo"></div>
			<img id="editted_image"></img>
		</div>
		<input id="got_uuid" type="hidden" value="fj7710"/>
	</div>	 --%>
		
	</div>
</div>
</form>

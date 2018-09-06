<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>

<div id="base">
	<table class="condform">
		<tbody>
			<tr>
				<td class="ui-state-default td-title">修理单号</td>
				<td class="td-content" style="width:330px;">
					<label id="label_sorc_no" name="label_sorc_no" alt="SORC_NO." ></label>
				</td>
				<td class="ui-state-default td-title">SFDC No.</td>
				<td class="td-content" style="width:330px;">
					<label id="label_sfdc_no" name="label_sfdc_no" alt="SFDC_NO."></label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">ESAS No.</td>
				<td class="td-content">
					<label id="label_esas_no" name="label_esas_no" alt="ESAS_NO." ></label>
				</td>
				<td class="ui-state-default td-title">委托处</td>
				<td class="td-content">
					<label id="label_ocm" name="label_ocm" alt="ocm"></label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">型号</td>
				<td class="td-content">
					<label id="label_model_name" name="label_model_name" alt="型号" ></label>
				</td>
				<td class="ui-state-default td-title">SERIAL No.</td>
				<td class="td-content">
					<label id="label_serial_no" name="label_serial_no" alt="SERIAL_NO."></label>
				</td>
			</tr>
		</tbody>
	</table>
</div>
<div style="margin-top:90px;"></div>
<div id="main">
	<div class="circle circle_blue" id="ocm_deliver" style="margin-left:10px;"></div><!--物流配送-->	
	
	<div class="rectangle rectangle_yellow" before="recept" style="margin-top:10px;margin-left:-1px;"></div>		
	<div class="circle circle_yellow" id="recept" style="margin-left:-1px;"></div><!--受理-->	
	
	<div class="rectangle rectangle_yellow" for="follow_recept" before="disfin" style="margin-top:10px;margin-left:-1px;"></div>	
	<div class="circle circle_yellow" id="disfin" style="margin-left:-1px;"></div><!--消毒·灭菌-->	
	
	<div class="rectangle rectangle_yellow" for="follow_disfin" before="quotate" style="margin-top:10px;margin-left:-1px;"></div>	
	<div class="circle circle_yellow" id="quotate" style="margin-left:-1px;"></div><!--报价完成-->	
	
	<div class="rectangle rectangle_yellow" for="follow_quotate" before="inline" style="margin-top:10px;margin-left:-1px;"></div>	
	<div class="circle circle_yellow" id="inline" style="margin-left:-1px;"></div><!--投线修理-->

	<div style="float:left;position:relative;left:100px;" class="circle-group">
	    <!-- 投线---NS -->
		<div class="center_radius" style="clip:rect(0,85px,55px,0);margin-left:-118px;margin-top:-41px;"></div>
		<div id="ns"  class="circle circle_yellow" style="position:relative;left:-44px;top:-50px;"></div>
		<!-- NS---总组 -->
		<div class="center_radius" style="clip:rect(0px,170px,55px,85px);margin-left:-105px;margin-top:-41px;"></div>
		<!--投线---分解 -->
	    <div class="center_radius" style="clip:rect(55px, 85px, 110px, 0px);margin-left:-117px;margin-top:-34px;"></div>
		<div id="dec" class="circle circle_yellow" id="dec" style="position:relative;left:-69px;top:52px;"></div>
		<!--分解---总组-->
		<div class="center_radius" style="clip:rect(55px, 170px, 110px, 85px);margin-left:-107px;margin-top:-34px;"></div>
	</div>
	
	<div class="circle circle_yellow" id="com" style="margin-left:87px;"></div><!--总组-->	
	
	<div class="rectangle rectangle_yellow" for="follow_com" before="outline" style="margin-left:-1px;margin-top:10px;"></div>	
	<div class="circle circle_yellow" id="outline" style="margin-left:-1px;margin-top:0px;"></div><!-- 品保 -->
		
	<div class="rectangle rectangle_yellow" for="follow_outline" before="shipping" style="margin-top:10px;margin-left:-1px;"></div>	
	<div class="circle circle_yellow" id="shipping" style="margin-left:-1px;margin-top:0px;"></div><!-- 包装出货 -->	
	
	<div class="rectangle rectangle_yellow" for="follow_shipping" before="ocm_shipping" style="margin-top:10px;margin-left:-1px;"></div>	
	<div class="circle circle_yellow" id="ocm_shipping" style="margin-left:-1px;margin-top:0px;"></div><!-- 物流配送 -->
	
</div>

<div style="width:1px;height:120px;position:absolute;left:370px;top:150px;background-color:blue"></div><!--分割线-->
<div class="circle circle_blue" id="agree" style="position:absolute;left:361px;top:250px;width:20px;height:20px;"></div><!--小圆-->
<label id="label_agree_date" class="note" alt="同意日期" name="agreed_date" style="position:absolute;left:337px;margin-top:-74px;"></label>
	

<!--流程名称-->
<div style="position:relative;">
	<label class="note" style="left:3px;margin-top:-20px;">物流配送</label>
	<label class="note" style="left:106px;margin-top:35px;">受理</label>
	<label class="note" style="left:186px;margin-top:-20px;">消毒·灭菌</label>
	<label class="note" style="left:283px;margin-top:35px;">报价完成</label>
	<label class="note" style="left:336px;margin-top:74px;">同意日期</label>
	<label class="note" style="left:359px;margin-top:-20px;">投线修理</label>
	<label class="note" style="left:475px;margin-top:-18px;">NS</label>
	<label class="note" style="left:473px;margin-top:33px;">分解</label>
	<label class="note" style="left:579px;margin-top:35px;">总组</label>
	<label class="note" style="left:652px;margin-top:-22px;">品保</label>
	<label class="note" style="left:730px;margin-top:35px;">包装出货</label>
	<label class="note" style="left:830px;margin-top:-21px;">物流配送</label>
</div>

<div style="position:relative;">
	<label id="label_ocm_deliver_date" class="note" alt="物流配送" name="ocm_deliver_date" for="ocm_deliver" style="left:0px;margin-top:35px;"></label>
	<label id="label_recept_time" class="note" alt="受理时间" name="recept_time" for="recept" style="left:87px;margin-top:-20px;"></label>
	<label id="label_disfin_time" class="note" alt="消毒·灭菌时间" name="disfin_time" for="disfin" style="left:184px;margin-top:35px;"></label>
	<label id="label_quotate_time" class="note" alt="报价时间" name="quotate_time" for="quotate" style="left:276px;margin-top:-20px;"></label>
	<label id="label_inline_time" class="note" alt="投线时间" name="inline_time" for="inline" style="left:364px;margin-top:35px;"></label>
	<label id="label_ns_time" class="note" alt="NS时间" name="ns_time" for="ns" style="left:467px;margin-top:-73px;"></label>
	<label id="label_dec_time" class="note" alt="分解时间" name="dec_time" for="dec" style="left:471px;margin-top:85px;"></label>
	<label id="label_com_time" class="note" alt="总组时间" name="com_time" for="com" style="left:568px;margin-top:-26px;"></label>
	<label id="label_outline_time" class="note" alt="品保时间" name="outline_time" for="outline" style="left:643px;margin-top:35px;"></label>
	<label id="label_shipping_time" class="note" alt="出货时间" name="shipping_time" for="shipping" style="left:728px;margin-top:-23px;"></label>
	<label id="label_ocm_shipping_date" class="note" alt="物流配送" name="ocm_shipping_date" for="ocm_shipping" style="left:823px;margin-top:35px;"></label>
    
    <input type="hidden"id="hidden_ocm_deliver_time"/>
	<input type="hidden"id="hidden_reception_time"/>
	<input type="hidden"id="hidden_sterilization_time"/>
	<input type="hidden"id="hidden_quotation_time"/>
	<input type="hidden"id="hidden_agreed_date"/>
	<input type="hidden"id="hidden_inline_time"/>
	<input type="hidden"id="hidden_dec_time"/>
	<input type="hidden"id="hidden_ns_time"/>
	<input type="hidden"id="hidden_com_time"/>
	<input type="hidden"id="hidden_outline_time"/>
	<input type="hidden"id="hidden_shipping_time"/>
	<input type="hidden"id="hidden_ocm_shipping_date"/>
</div>

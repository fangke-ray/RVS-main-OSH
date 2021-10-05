<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<style>
#report_of_week td.td-content {
width:80px;
text-align:right;
}
#report_of_week input {
width:4em;
text-align:right;
}
.for_complete {
width:750px;
height:7em;
resize:none;
}
#capacity_of_upper_limit input{
 	ime-mode: disabled;
 	width:6em;
 	text-align:right;
}
</style>
	<form id="capacity_of_upper_limit">
		<table class="condform" style="width:100%">
			<thead>
				<tr>
					<td class="ui-state-default td-title" colspan="5">翻修一课</td>
				</tr>
			</thead>
			<tbody>
			 <tr>
				<th class="ui-state-default td-title">总组</td>
				<th class="ui-state-default td-title">A 线</td>
				<th class="ui-state-default td-title">B1 线</td>
				<th class="ui-state-default td-title">B2 线</td>
				<th class="ui-state-default td-title">C 线</td>
			 </tr>
			 <tr>
				<td class="ui-state-default td-title">大修理</td>
				<td class="td-content">
					<input type="number" section_id="00000000001" line_id="00000000014" px="0" light_fix_flg="0"></input>
				</td>
				<td class="td-content">
					<input type="number" section_id="00000000001" line_id="00000000014" px="4" light_fix_flg="0"></input>
				</td>
				<td class="td-content">
					<input type="number" section_id="00000000001" line_id="00000000014" px="7" light_fix_flg="0"></input>
				</td>
				<td class="td-content">
					<input type="number" section_id="00000000001" line_id="00000000014" px="2" light_fix_flg="0"></input>
				</td>
			 </tr>
			 <tr>
				<td class="ui-state-default td-title">中小修理</td>
				<td class="td-content">
					<input type="number" section_id="00000000001" line_id="00000000014" px="0" light_fix_flg="1"></input>
				</td>
				<td class="td-content">
					<input type="number" section_id="00000000001" line_id="00000000014" px="4" light_fix_flg="1"></input>
				</td>
				<td class="td-content">
					<input type="number" section_id="00000000001" line_id="00000000014" px="7" light_fix_flg="1"></input>
				</td>
				<td class="td-content">
					<input type="number" section_id="00000000001" line_id="00000000014" px="2" light_fix_flg="1"></input>
				</td>
			 </tr>
			</tbody>
		</table>

		<table class="condform" style="width:100%">
			<thead>
				<tr>
					<td class="ui-state-default td-title" colspan="5">翻修二课</td>
				</tr>
			</thead>
			<tbody>
			 <tr>
			 	<th class="ui-state-default td-title"></td>
				<th class="ui-state-default td-title">分解</td>
				<th class="ui-state-default td-title">NS</td>
				<th class="ui-state-default td-title">总组</td>
			 </tr>
			 <tr>
				<td class="ui-state-default td-title">大修理</td>
				<td class="td-content">
					<input type="number" section_id="00000000003" line_id="00000000012" px="0" light_fix_flg="0"></input>
				</td>
				<td class="td-content">
					
				</td>
				<td class="td-content">
					<input type="number" section_id="00000000003" line_id="00000000014" px="0" light_fix_flg="0"></input>
				</td>
			 </tr>
			 <tr>
				<td class="ui-state-default td-title">中小修理</td>
				<td class="td-content">
					<input type="number" section_id="00000000003" line_id="00000000012" px="0" light_fix_flg="1"></input>
				</td>
				<td class="td-content">
					<input type="number" section_id="00000000003" line_id="00000000013" px="0" light_fix_flg="1"></input>
				</td>
				<td class="td-content">
					<input type="number" section_id="00000000003" line_id="00000000014" px="0" light_fix_flg="1"></input>
				</td>
			 </tr>
			</tbody>
		</table>
	</form>
<script type="text/javascript">
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'scheduleProcessing.do?method=getUpperLimit',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj, textStatus) {
				try {
			 	var resInfo = $.parseJSON(xhrObj.responseText);
					if (resInfo.errors && resInfo.errors.length > 0) {
						treatBackMessages(null, resInfo.errors);
						return;
					}
//					//课室显示
//					var resultSectionNames = resInfo.resultSectionNames;
//					var rsnLength = resultSectionNames.length;
					
//					var $sec = $("#capacity_of_upper_limit >table > thead > tr td");
//					if (resultSectionNames) {
//						var secContent = "";
//						for(var i = 0;i<rsnLength;i++){
//							secContent +="<td class='ui-state-default td-title'>"+resultSectionNames[i].section_name+"</td>";
//						}	
//						$sec.after(secContent);
//					}
					
					//维修对象+最大功能显示
					var resultBeans = resInfo.resultBeans;
					var rsLength = resultBeans.length;

					var $detail = $("#capacity_of_upper_limit >table > tbody");
//		if (resultSectionNames) {
//			var detailContent = "";
//			var line_id = "";
			for(var j = 0;j<rsLength;j++){
//				if (j == 0) {
				line_id = resultBeans[j].line_id;
				var $target = $detail.find("input[line_id=" + line_id + "][section_id=" + resultBeans[j].section_id + "][light_fix_flg=" + resultBeans[j].level + "][px=" + resultBeans[j].px + "]");
//				}
//				detailContent += "<tr>"+
//									"<td class='ui-state-default td-title'>"+resultBeans[j].category_name+"</td>";
//				for(var i = 0;i<rsnLength;i++){
//					detailContent += "<td section_id='"+resultSectionNames[i].section_id + "'><input type='number' value=''/></td>";
//				}
//				detailContent += "<input class ='category_id' type='hidden' value='"+resultBeans[j].category_id+"'/>"+
//								"</tr>";
				$target.val(resultBeans[j].upper_limit);
			}
//			detailContent += "<input class ='line_id' type='hidden' value='"+line_id+"'/>";
//			$detail.html(detailContent);
//
//			for(var i = 0;i<rsLength;i++){
//				if (resultBeans[i].upper_limit != undefined) {
//					var str = resultBeans[i].upper_limit.split(";");
//					for(var j = 0;j<str.length;j++){
//						var temp = str[j].split(":");
//						var section_id = temp[0];
//						var upper_limit = temp[1];
//						$detail.find("tr").eq(i).find("td[section_id="+section_id+"] input").val(upper_limit);
//					}
//				}
//			}
//		}

					//修改任一个input可输入数字之后，给其设置changed="true"
					//产能只能输入数字
					$("#capacity_of_upper_limit input[type='number']").change(function(){
						$(this).attr("changed", "true");
				        var ival = $(this).val();
				        if (ival.match(/^[0-9]*$/) == null) {
							var msgInfos=[];
							var msgInfo={};
							msgInfo.errmsg = "请输入0-9之间的数字";
							msgInfos.push(msgInfo);
							treatBackMessages("", msgInfos);
							$(this).val("");
				        }
					});
					
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
			}
		});
</script>
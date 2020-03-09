<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<style>
#waste_content .dwidth-half{
	width: 500px;
}

#search_case table {
	width:99.4%;
}
#search_case table tr td:nth-child(1){
	text-align: center;
	width:5%;
	padding: 0;
}
#search_case table tr td:nth-child(2){
	width: 13%;
}
#search_case table tr td:nth-child(3){
	width: 13%;
	text-align: right;
}
#search_case table tr td:nth-last-child(1){
	text-align: center;
	width: 22%;
}
#search_case table tr td:nth-last-child(1) input{
	padding:.2em .6em;
}

#waster_partial_list tr.newest > td {
	background-color: greenyellow;
}
</style>

<div class="ui-helper-clearfix" id="waste_content">
	<div style="float:left;" class="dwidth-half">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">当日完成废弃零件整理维修品一览</span>
		</div>
		<table id="waster_partial_list"></table>
	    <div id="waster_partial_listpager"></div>
	</div>

	<div style="float:right;width:550px;" class="dwidth-half">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">维修对象信息</span>
		</div>
		<div class="ui-widget-content" id="scanner_waster_container" style="min-height: 150px;">
			<div class="ui-state-default td-title">扫描录入区域</div>
			<input type="text" id="scanner_waster_inputer" title="扫描前请点入此处" class="scanner_inputer dwidth-half" style="height:65px;width: 542px;">
			<div style="text-align: center;">
				<img src="images/barcode.png" style="margin: auto; width: 150px; padding-top: 4px;">
			</div>
		</div>
		<div class="ui-widget-content" id="material_waster_details" style="display: none;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content"><span id="label_omr_notifi_no"></span></td>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content"><span id="label_model_name"></span></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content"><span id="label_serial_no"></span></td>
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content"><span id="label_level_name"></span></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">返修分类</td>
					<td class="td-content"><span id="label_service_repair_flg"></span></td>
				</tr>
			</table>
		</div>
		
		<div class="areaencloser"></div>
		
		<div id="search_case">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				<span class="areatitle">零件回收箱一览</span>
				<input type="button" class="ui-button" id="addCaseButton" value="新建回收箱" style="float: right;;right: 2px;">
			</div>
			<div class="ui-widget-content" style="height:304px;overflow-y:scroll; ">
				<table class="condform">
					<thead>
						<tr style="text-align:center;">
							<td class='td-content' style="display: none;"><input type='checkbox' id="checkAll"></td>
							<td class='ui-state-default td-title'>装箱编号</td>
							<td class='ui-state-default td-title'>重量(kg)</td>
							<td class='ui-state-default td-title'>备注说明</td>
							<td class='ui-state-default td-title'></td>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
			</div>
			<div style="height:36px;margin-top:5px;display: none;" id="prel">
				<input type="button" class="ui-button" id="prelAddArrangementButton" value="放入回收箱">
			</div>
		</div>

		<div id="add_case" style="display: none;">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				<span class="areatitle">新建回收箱</span>
			</div>
			<div class="ui-widget-content">
				<table class="condform">
					<tr>
						<td class='ui-state-default td-title'>装箱编号</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content" id="add_case_code">
						</td>
					</tr>
					<tr>
						<td class='ui-state-default td-title'>用途种类</td>
						<td class="td-content" id="add_widget_collect_kind">
							<input type="radio" name="collect_kind" id="add_widget_collect_kind_all" value="" class="ui-widget-content" checked/><label for="add_widget_collect_kind_all">(无)</label>
							<input type="radio" name="collect_kind" id="add_widget_collect_kind_endo" value="1" class="ui-widget-content" /><label for="add_widget_collect_kind_endo">内窥镜</label>
							<input type="radio" name="collect_kind" id="add_widget_collect_kind_perl" value="2" class="ui-widget-content" /><label for="add_widget_collect_kind_perl">周边设备</label>
						</td>
					</tr>
					<tr>
						<td class='ui-state-default td-title'>重量</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content" id="add_weight">
						</td>
					</tr>
					<tr>
						<td class='ui-state-default td-title'>备注说明</td>
						<td class="td-content">
							<textarea rows="1" cols="50" style="resize:none;" id="add_comment"></textarea>
						</td>
					</tr>
				</table>
				<div style="height:36px;margin-top:5px;" class="ui-helper-clearfix">
					<input type="button" class="ui-button" id="cancleAddCaseButton" value="取消" style="float:right;right:4px;">
					<input type="button" class="ui-button" id="insertCaseButton" value="新建" style="float:right;right:4px;">
				</div>
			</div>
		</div>
		
		<div id="update_case" style="display: none;">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				<span class="areatitle">更新回收箱</span>
			</div>
			<div class="ui-widget-content">
				<table class="condform">
					<tr>
						<td class='ui-state-default td-title'>装箱编号</td>
						<td class="td-content"><span id="dialog_label_case_code"></span></td>
					</tr>
					<tr>
						<td class='ui-state-default td-title'>用途种类</td>
						<td class="td-content"><span id="dialog_label_collect_kind_name"></span></td>
					</tr>
					<tr>
						<td class='ui-state-default td-title'>重量</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content" id="dialog_update_weight">(kg)
						</td>
					</tr>
					<tr>
						<td class='ui-state-default td-title'>备注说明</td>
						<td class="td-content">
							<textarea rows="1" cols="50" disabled="disabled" readonly="readonly" style="resize: none;" id="dialog_label_comment"></textarea>
						</td>
					</tr>
				</table>
				<div style="height:36px;margin-top:5px;" class="ui-helper-clearfix">
					<input type="button" class="ui-button" id="cancleUpdateCaseButton" value="取消" style="float:right;right:4px;">
					<input type="button" class="ui-button" id="updateCaseButton" value="更新" style="float:right;right:4px;">
				</div>
			</div>
		</div>
	</div>
	
	<input type="hidden" id="hide_service_repair_flg">
</div>

<script type="text/javascript">
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};
Date.prototype.addDays = function (d) {
    var adjustDate = new Date(this.getTime() + 24*60*60*1000*d)
    return adjustDate;
};

	var wastePartialJs = {
		"scan_kind" : null,
		"init" : function () {
			$("#waste_content .ui-button").button();
			
			$("#add_widget_collect_kind").buttonset();
			$("#add_widget_collect_kind_all").attr("checked","checked").trigger("change");
			
			$("#addCaseButton").click(function(){
				$("#add_case input[type='text']").val("");
				$("#add_case textarea").val("");
				$("#add_widget_collect_kind_all").attr("checked","checked").trigger("change");  
				
				$("#add_case").show();
				$("#search_case").hide();
			});
			
			$("#cancleAddCaseButton").click(function(){
				$("#add_case").hide();
				$("#search_case").show();
			});
			
			$("#checkAll").click(function(){
				if(this.checked){
					$("#search_case tr[collect_kind='2']").find("input[type='checkbox']").attr("checked",true);
				} else {
					$("#search_case tr[collect_kind='2']").find("input[type='checkbox']").attr("checked",false);
				}
			});
			
			$("#insertCaseButton").click(function(){
				var data = {
					"case_code" : $("#add_case_code").val(),
					"collect_kind" : $("#add_widget_collect_kind input:checked").val(),
					"weight" : $("#add_weight").val(),
					"comment" : $("#add_comment").val()
				};
				wastePartialJs.doAjax('waste_partial_recycle_case.do?method=doInsert',data,function(resInfo){
					var searchData = {
						"package_flg" : "1",
						"waste_flg" : "1"
					};
					wastePartialJs.doAjax('waste_partial_recycle_case.do?method=search',searchData,function(resInfo2){
						wastePartialJs.caseList(resInfo2.finished);
						$("#add_case").hide();
						$("#search_case").show();
					});
				});
			});
			
			$("#cancleUpdateCaseButton").click(function(){
				$("#update_case").hide();
				$("#search_case").show();
			});
			
			wastePartialJs.showList([]);
			
			$("#scanner_waster_inputer").keyup(function(evt){
				if (this.value.length >= 11) {
					wastePartialJs.doStart();
				}
			});

			wastePartialJs.doAjax('af_production_feature.do?method=getWastePartial',null,function(resInfo){
				wastePartialJs.showList(resInfo.complate);
				$("#hide_service_repair_flg").val(resInfo.goMaterialServiceRepaire);
				$("#scanner_waster_inputer").focus();
				wastePartialJs.caseList(resInfo.caseList);
			});
		},
		"doAjax" : function(url,data,callback){
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : url,
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhjObj) {
					var resInfo;
					try {
						// 以Object形式读取JSON
						resInfo = $.parseJSON(xhjObj.responseText);
						if (resInfo.errors.length > 0) {
							// 共通出错信息框
							treatBackMessages(null, resInfo.errors);
						} else {
							if(callback){
								callback(resInfo);
							}
						}
					} catch(e) {}
				}
			});
		},
		"showList" : function (listdata) {
			if ($("#gbox_waster_partial_list").length > 0) {
				$("#waster_partial_list").jqGrid().clearGridData();
				$("#waster_partial_list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			} else {
				$("#waster_partial_list").jqGrid({
					data : listdata,
					height : 461,
					width : 498,
					rowheight : 23,
					datatype : "local",
					colNames : ['修理单号','型号','机身号','等级','返修分类','回收<br>部分','收集<br>时间','装箱<br>编号'],
					colModel : [
						{name:'omr_notifi_no',index:'omr_notifi_no',width:40},
						{name:'model_name',index:'model_name',width:50},
						{name:'serial_no',index:'serial_no',width:35},
						{name:'levelName',index:'levelName',width:30,align:'center'},
						{name:'service_repair_flg',index:'service_repair_flg',width:40,formatter:'select',editoptions:{value:$("#hide_service_repair_flg").val()}},
						{name:'part',index:'part',width:30,align:'center'},
						{name:'collect_time',index:'collect_time',width:35,align:'center',formatter:'date',formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'H:i:s'}},
						{name:'case_code',index:'case_code',width:35}
					],
					toppager : false,
					rowNum : 20,
					rownumbers : true,
					pager : "#waster_partial_listpager",//翻页
					viewrecords : true,//显示总记录数
					gridview : true, 
					pagerpos : 'right',
					ondblClickRow : null,
					onSelectRow :null,
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true],
					gridComplete:function() {
						var lastCollectTime = null;
						var ldata = $("#waster_partial_list").jqGrid('getGridParam', 'data');

						if (ldata && ldata.length) {
							for (var i in ldata) {
								
								if (lastCollectTime == null || lastCollectTime < ldata[i]["collect_time"]) {
									lastCollectTime = ldata[i]["collect_time"];
								}
							}
							lastCollectTime = (lastCollectTime + "").substring(11);
							$("#waster_partial_list td[aria\\-describedby='waster_partial_list_collect_time']")
								.filter(function(){return this.innerText == lastCollectTime})
								.closest("tr").addClass("newest");
						}
					}
				});
			}
		},
		"caseList" : function(list){
			var content = "";
			list.forEach(function(item,index){
				var weight = item.weight || '';
				var comment = item.comment || '';
				
				content += "<tr case_id='" + item.case_id + "' case_code='" + item.case_code + "' weight='" + weight + "' collect_kind='" + item.collect_kind + "' comment='" + comment + "'>";
				if(item.collect_kind == 2){
					content += "<td class='td-content'><input type='checkbox'></td>";
				}else{
					content += "<td class='td-content'></td>";
				}
				content += "<td class='td-content'>" + item.case_code + "</td>";
				content += "<td class='td-content'>" + weight + "</td>";
				if(comment.length > 18){
					content += "<td class='td-content'>" + comment.substring(0,18) + "...</td>";
				}else{
					content += "<td class='td-content'>" + comment + "</td>";
				}
				content += "<td class='td-content package'>";
				content += "<input type='button' class='ui-button package' value='打包'>";
				content += "<input type='button' class='ui-button edit' value='编辑' style='margin-left:5px;'></td>";
				content += "</tr>";
			});
			
			$("#search_case table tbody").html(content);
			if(wastePartialJs.scan_kind && wastePartialJs.scan_kind == 2){
				$("#search_case table tbody tr[collect_kind='1']").hide();
				$("#search_case tr").find("td:eq(0)").show();
			}else{
				$("#search_case table tbody tr").show();
				$("#search_case tr").find("td:eq(0)").hide();
			}
			
			$("#search_case input.ui-button").button();
			$("#checkAll").attr("checked",false);
			
			// 打包
			$("#search_case input.package").each(function(){
				$(this).click(function(){
					var $tr = $(this).closest("tr");
					var warnData = "确定将装箱编号为【" + $tr.attr("case_code") + "】箱子打包。<br>";
					warnData+="<table class='condform'>";
					warnData+="<tr>";
					warnData+='<td class="ui-state-default td-title">重量</td>';
					warnData+='<td class="td-content"><input type="text" class="ui-widget-content" id="dialog_package_update_weight" value="' +  $tr.attr("weight") + '" style="text-align:right;">(kg)</td>';
					warnData+="</tr>";
					warnData+="</table>";
					
					warningConfirm(warnData,function(){
						var data = {
							"case_id" : $tr.attr("case_id"),
							"weight" : $("#dialog_package_update_weight").val()
						};
						wastePartialJs.doAjax('waste_partial_recycle_case.do?method=doPackage',data,function(resInfo){
							var searchData = {
								"package_flg" : "1",
								"waste_flg" : "1"
							};
							wastePartialJs.doAjax('waste_partial_recycle_case.do?method=search',searchData,function(resInfo2){
								wastePartialJs.caseList(resInfo2.finished);
							});
						});
					});
				});
			});
			
			// 编辑
			$("#search_case input.edit").each(function(){
				$(this).click(function(){
					var $tr = $(this).closest("tr");
					
					$("#dialog_label_case_code").text($tr.attr("case_code"));
					var collect_kind = $tr.attr("collect_kind");
					if(collect_kind == 1){
						$("#dialog_label_collect_kind_name").text("内窥镜");
					}else if(collect_kind == 2){
						$("#dialog_label_collect_kind_name").text("周边设备");
					}
					$("#dialog_update_weight").val($tr.attr("weight"));
					$("#dialog_label_comment").val($tr.attr("comment"));
					
					$("#update_case").show();
					$("#search_case").hide();
					
					$("#updateCaseButton").unbind("click").bind("click",function(){
						var data = {
							"case_id" : $tr.attr("case_id"),
							"weight" : $("#dialog_update_weight").val().trim()
						};
						wastePartialJs.doAjax('waste_partial_recycle_case.do?method=doUpdateWeight',data,function(resInfo){
							var searchData = {
								"package_flg" : "1",
								"waste_flg" : "1"
							};
							wastePartialJs.doAjax('waste_partial_recycle_case.do?method=search',searchData,function(resInfo2){
								wastePartialJs.caseList(resInfo2.finished);
								$("#update_case").hide();
								$("#search_case").show();
							});
						});
					});
				});
			});
		},
		"doStart" : function(){
			var scandata = {
				"material_id" : $("#scanner_waster_inputer").val()
			};
			$("#scanner_waster_inputer").val("");

			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : 'af_production_feature.do?method=wastePartialScan',
				cache : false,
				data : scandata,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhjObj) {
					var resInfo;
					try {
						// 以Object形式读取JSON
						resInfo = $.parseJSON(xhjObj.responseText);
						$("#scanner_waster_inputer").val("");
						
						if (resInfo.errors.length > 0) {
							// 共通出错信息框
							treatBackMessages("", resInfo.errors);
						} else {
							var collectFlg = resInfo.collectFlg;
							var collectKind = resInfo.collectKind;

							//不能收集
							if(!collectFlg){
								// 提示信息(多次扫描不会被记录)
								var $this_dialog = $("#tooltip");
								if ($this_dialog.length === 0) {
									$("body.outer").append("<div id='tooltip'/>");
									$this_dialog = $("#tooltip");
								}
								$this_dialog.text("请勿重复收集。");
								
								$this_dialog.dialog({
									dialogClass : 'ui-info-dialog',
							        position : [800, 20],
							        title : "提示信息",
							        width :300,
							        height : 100,
							        resizable : false,
							        modal : true,
							        show : "blind"
							    });
								
								setTimeout(function(){
									$this_dialog.dialog("close");
									$("#scanner_waster_inputer").focus();
								},2000);
							} else {
								// 回收箱
								var caseList = resInfo.caseList;
								
								//回收箱不存在
								if(caseList.length == 0){
									if(collectKind == 1){
										infoPop("请新建放置内窥镜回收箱。");
									} else {
										infoPop("请新建放置周边设备回收箱。");
									}
								} else {
									if(collectKind == 1){
										var insertData = {
											"material_id" : scandata.material_id,
											"collect_case_id" : caseList[0].case_id,
											"collect_kind" : collectKind
										};
										
										wastePartialJs.doAjax('waste_partial_arrangement.do?method=doInsert',insertData,function(resInfo){
											$("#search_case tr").find("td:eq(0)").hide();
											$("#search_case tbody tr").show();
											$("#prel").hide();
											
											var date  = new Date();
											var searchData = {
												"collect_time_start" : date.Format("yyyy/MM/dd"),
												"collect_time_end" : (date.addDays(1)).Format("yyyy/MM/dd")
											};											
											wastePartialJs.doAjax('waste_partial_arrangement.do?method=search',searchData,function(resInfo2){
												wastePartialJs.showList(resInfo2.finished);
											});
										});
									}else{
										var arrangementList = resInfo.arrangementList;
										var collectSize = arrangementList.length;
										
										if(collectSize == 9){
											// 提示信息(多次扫描不会被记录)
											var $this_dialog = $("#tooltip");
											if ($this_dialog.length === 0) {
												$("body.outer").append("<div id='tooltip'/>");
												$this_dialog = $("#tooltip");
											}
											$this_dialog.text("请勿重复收集。");
											
											$this_dialog.dialog({
												dialogClass : 'ui-info-dialog',
										        position : [800, 20],
										        title : "提示信息",
										        width :300,
										        height : 100,
										        resizable : false,
										        modal : true,
										        show : "blind"
										    });
											
											setTimeout(function(){
												$this_dialog.dialog("close");
												$("#scanner_waster_inputer").focus();
											},2000);
											return;
										}
										
										// 维修对象基本信息
										var materialForm = resInfo.materialForm;
										wastePartialJs.setMaterialInfo(materialForm);
										wastePartialJs.scan_kind = collectKind;
										
										$("#search_case tr[collect_kind='2']").find("input[type='checkbox']").attr("checked",false);
										$("#search_case tbody tr[collect_kind='1']").hide();
										$("#search_case tr").find("td:eq(0)").show();
										
										$("#material_waster_details").show();
										$("#scanner_waster_container").hide();
										$("#prel").show();
										
										$("#prelAddArrangementButton").unbind("click").bind("click",function(){
											var selectSize = $("#search_case tr[collect_kind='2']").find("input[type='checkbox']:checked").length;
											
											var diff = 9 - collectSize;
											
											if(selectSize == 0){
												errorPop("请至少选择一个回收箱。");
											} else if(selectSize > diff){
												errorPop("选择的回收箱不能多于" + diff +"个。");
											} else {
												var insertData = {
													"material_id" : scandata.material_id,
													"collect_kind" : collectKind
												};
												
												var ii = 0;
												$("#search_case tr[collect_kind='2']").each(function(){
													var $tr = $(this);
													var checked = $tr.find("input[type='checkbox']").prop("checked");
													if(checked){
														insertData["waste_partial_arrangement.collect_case_id[" + ii + "]"] = $tr.attr("case_id");
														ii++;
													}
												});
												
												wastePartialJs.doAjax('waste_partial_arrangement.do?method=doInsert',insertData,function(resInfo){
													$("#material_waster_details").hide();
													$("#scanner_waster_container").show();
													$("#scanner_waster_inputer").focus();
													
													$("#search_case tr").find("td:eq(0)").hide();
													$("#search_case tbody tr").show();
													$("#prel").hide();
													$("#checkAll").attr("checked",false);
													wastePartialJs.scan_kind = null;
													
													var date  = new Date();
													var searchData = {
														"collect_time_start" : date.Format("yyyy/MM/dd"),
														"collect_time_end" : (date.addDays(1)).Format("yyyy/MM/dd")
													};											
													wastePartialJs.doAjax('waste_partial_arrangement.do?method=search',searchData,function(resInfo2){
														wastePartialJs.showList(resInfo2.finished);
													});
												});
											}
										});
									}
								}
							}
						}
					} catch(e) {}
				}
			});
		},
		"setMaterialInfo" : function(materialForm){
			$("#label_omr_notifi_no").text(materialForm.sorc_no);
			$("#label_model_name").text(materialForm.model_name);
			$("#label_serial_no").text(materialForm.serial_no);
			$("#label_level_name").text(materialForm.levelName);
			
			var service_repair_flg = materialForm.service_repair_flg;
			if(service_repair_flg == 1){
				$("#label_service_repair_flg").text("保内返修");
			} else if(service_repair_flg == 2){
				$("#label_service_repair_flg").text("QIS");
			} else if(service_repair_flg == 3){
				$("#label_service_repair_flg").text("备品");
			} else{
				$("#label_service_repair_flg").text("");
			}
		}
	};

	wastePartialJs.init();
</script>
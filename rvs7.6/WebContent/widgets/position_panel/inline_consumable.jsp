<!DOCTYPE html>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<style>
#cnsmCnfmList td.image-link {
	cursor:pointer;
	text-decoration: underline solid blue;
}
#cnsmCnfmList tr.otherPosition td[aria-describedby='cnsmCnfmList_code'] {
	background-color: lightgray;
}
#cnsmCnfmList td > input.cur_quantity {
	width: 2em;
	text-align: right;
}
#cnsmCnfmList span.recmd {
	cursor:pointer;
	background-color: yellow;
	padding:0 0.4em;
	margin-left: 0.2em;
}
#cnsmCnfmList span.recmd.cnsm {
	background-color: lightskyblue;
}
#radCnsmCurrent +.ui-button-text-only .ui-button-text,
#radCnsmNotCurrent +.ui-button-text-only .ui-button-text {
	padding: 0 0.5em;
	font-size:12px;
}
</style>
<div id="cnsmCnfmArea" style="display:none;">
	<table id='cnsmCnfmList'></table>
</div>
<script type="text/javascript">
if (typeof ($.fn.preview) == "undefined") {
	loadJs("js/jquery.imagePreview.1.0.js");
}
var inline_consumable = ${inlineConsumable};
var cnsm_obj = function() {
	var icon = {"3" : "icon-screw", "1" : "icon-aperture"};

	var $cnsm_icon = $('<a id="cnsm_icon" role="link" href="javascript:void(0)" style="text-decoration: none;" class="HeaderButton areacloser"><span class="'
		+ icon[inline_consumable] + '"></span></a>');
	var assembleMap = {};
	var lastestAssembleList = [];
	var this_material_id = null;
	var loaded = false;
	var radCnsmPosSel = function(){
		$("#cnsmCnfmList tr").show();

		if (!$("#cnsmCnfmList tr.otherPosition").length) {
			$("#radCnsmNotCurrent + label").hide();
		} else {
			$("#radCnsmNotCurrent + label").show();
		}

		if ($('#radCnsmCurrent').is(":checked")) {
			$("#cnsmCnfmList tr.otherPosition").hide()
		} else {
			$("#cnsmCnfmList tr:not(.otherPosition):not(.jqgfirstrow)").hide()
		}
	}

	var showRecommendedSpan = function(val, isCnsmSet){
		if (!val) return "";
		if (val.indexOf('|') >= 0) {
			var spans = val.split("|");
			var ret="";
			for (var ix in spans) {
				ret += "<span class='recmd'>" + spans[ix].trim() + "</span>";
			}
			return ret;
		} else {
			return "<span class='recmd" + (isCnsmSet ? " cnsm" : "") + "'>" + val + "</span>";
		}
	}

	var commitRegist =function() {
		var postData = {
			material_id : this_material_id, 
			type : inline_consumable
		};
		var idx = 0;
		var errorMsg = "";
		for (var partial_id in assembleMap) {
			if (assembleMap[partial_id].updated) {
				var cur_quantity = assembleMap[partial_id].recept_quantity;
				if (isNaN(cur_quantity)) {
					var code = $("#cnsmCnfmList tr#" + partial_id).find("td[aria\\-describedby='cnsmCnfmList_code']").text();
					errorMsg += "请为消耗品" + code + "的安装量指定为数值。<br>";
					continue;
				}
				if (cur_quantity.length == 0) cur_quantity = "0";
				cur_quantity = parseInt(cur_quantity.trim());
				if (cur_quantity < 0) {
					var code = $("#cnsmCnfmList tr#" + partial_id).find("td[aria\\-describedby='cnsmCnfmList_code']").text();
					errorMsg += "请为消耗品" + code + "的安装量指定为正整数。<br>";
					continue;
				}
				var lesser = false;
				$("#cnsmCnfmList tr#" + partial_id).find("span.recmd").each(function(idx, ele){
					if (!lesser && cur_quantity <= +(ele.innerText)) {
						lesser = true;
					}
				});
				if (!lesser) {
					var code = $("#cnsmCnfmList tr#" + partial_id).find("td[aria\\-describedby='cnsmCnfmList_code']").text();
					errorMsg += "请为消耗品" + code + "的安装量指定为不大于参考值的数量。<br>";
					continue;
				}
				postData["assemble[" + idx + "].partial_id"] = partial_id;
				postData["assemble[" + idx + "].recept_quantity"] = cur_quantity;
				idx++;
			}
		}

		if (errorMsg.length) {
			errorPop(errorMsg);
			return;
		}

		if (idx == 0) {
			$('#cnsmCnfmArea').dialog("close");
			return;
		}

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'material_consumable.do?method=doCommit',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				var resInfo = JSON.parse(xhrobj.responseText);

				if (resInfo.errors.length !== 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					toAssembleMap(resInfo.assembleList);
					$('#cnsmCnfmArea').dialog("close");
				}
			}
		});
	}

	$cnsm_icon.click(function(){
		if (!loaded) return;
		$('#cnsmCnfmArea').dialog({
			modal : true,
			resizable:false,
			width : 'auto',
			title : "清点本工位安装消耗品",
			open : function(){
				var $thisDialog = $(this);
				if ($thisDialog.prev().children("input:radio").length) {
					$('#radCnsmCurrent').attr("checked", true).trigger("change");
					radCnsmPosSel();
					showAssembleMap();
					return;
				}

				$thisDialog.parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();//去掉右上角关闭按钮

				var $radCnsmPos = $("<input type='radio' id='radCnsmCurrent' value='1' name='radCnsmPos' checked><label for='radCnsmCurrent'>本工位定位</label><input type='radio' id='radCnsmNotCurrent' value='0' name='radCnsmPos'><label for='radCnsmNotCurrent'>其他</label>");
				$thisDialog.prev().children("span:eq(0)")
					.after($radCnsmPos);
				$thisDialog.prev().children("input:radio").button().click(radCnsmPosSel);
				radCnsmPosSel();
			},
			closeOnEscape: false,
			buttons: {
				"登记" : commitRegist,
				"关闭":function(){
					toAssembleMap(lastestAssembleList);
					$('#cnsmCnfmArea').dialog("close");
				}
			}
		});
	});

	var toAssembleMap = function(assembleList) {
		var used = false;
		assembleMap = {};
		lastestAssembleList = assembleList;
		for (var i in assembleList) {
			var assembleEntity = assembleList[i];
			var partial_id = assembleEntity.partial_id.replace(/^0*/, "");
			if (assembleMap[partial_id]) {
				if (assembleEntity.belongs == -1 && assembleEntity.recept_quantity > 0) {
					assembleMap[partial_id].other_recept_quantity 
						= (assembleMap[partial_id].other_recept_quantity || 0) + assembleEntity.recept_quantity;
					assembleEntity.recept_quantity = 0;
				} else {
					if (assembleEntity.recept_quantity > 0){
						assembleMap[partial_id].recept_quantity 
							= (assembleMap[partial_id].recept_quantity || 0) + assembleEntity.recept_quantity;
						if (!used) used = true;
					}
				}
			} else {
				if (assembleEntity.belongs == -1 && assembleEntity.recept_quantity > 0) {
					assembleEntity.other_recept_quantity = assembleEntity.recept_quantity;
					assembleEntity.recept_quantity = 0;
				} else if (!used && assembleEntity.recept_quantity > 0){
					used = true; 
				}
				assembleMap[partial_id] = $.extend({}, assembleEntity);
			}
		}
		return used;
	}

	var recAssembleMap = function(partial_id, cur_quantity){
		if (assembleMap[partial_id]) {
			assembleMap[partial_id].recept_quantity = cur_quantity;
			assembleMap[partial_id].updated = true;
		} else {
			assembleMap[partial_id] = {"recept_quantity" : cur_quantity, "updated" : true};
		}
	}

	var showAssembleMap = function(){
		$("#cnsmCnfmList tr input.cur_quantity").val("")
			.prevAll().remove();
		for (var partial_id in assembleMap) {
			var $target = $("#cnsmCnfmList tr#" + partial_id).find("input.cur_quantity");
			if ($target.length) {
				$target.val(assembleMap[partial_id].recept_quantity);
				if (assembleMap[partial_id].other_recept_quantity) {
					$target.parent().prepend("<span>" + assembleMap[partial_id].other_recept_quantity + " + </span>");
				}
			}
		}
	}

	$("#cnsmCnfmList").jqGrid({
		data:[],
		height: 346,
		width: 600,
		rowheight: 23,
		datatype: "local",
		colNames:['','partial_id','消耗品分类','品名','描述','库存量','安装量','参考安装量','当前工位','Rank','有图片'],
		colModel:[
			{
				name:'checked',
				width:20, hidden: true
			},
			{
				name:'partial_id',
				index:'partial_id',
				hidden:true,
				key:true
			},
			{
				name:'append',
				index:'append',
				width:35,
				formatter: function(value, col, rdata){return ((inline_consumable == 3)? '螺丝' :
					(rdata["level"] == "1" ? 'CCD 标配' : 'CCD 非标')
					)}
			},
			{
				name:'code',
				index:'code',
				width:40
			},
			{
				name:'partial_name',
				index:'partial_name',
				width:120
			},
			{
				name:'available_inventory',
				index:'available_inventory',
				width:25,
				align: 'right',
				hidden: (inline_consumable != 1),
				sortable:false
			},
			{
				name:'cur_quantity',
				index:'cur_quantity',
				width:30,
				align:'right',
				formatter:function(){
					return "<input class='cur_quantity'></input>";
				},
				sortable:false
			},
			{
				name:'bom_quantity',
				index:'bom_quantity',
				width:30,
				align:'right',
				formatter:function(value, col, rdata){
					if (rdata["quantity"]) return showRecommendedSpan(rdata["quantity"], true);
					return showRecommendedSpan(value);
				},
				sortable:false
			},
			{
				name:'status',
				index:'status',
				hidden:true
			},
			{
				name:'level',
				index:'level',
				hidden:true
			},
			{
				name:'spec_kind',
				index:'spec_kind',
				hidden:true
			}
		],
		rowNum: 99,
		toppager: false,
		pager: null,
		viewrecords: true,
		pagerpos: 'right',
		pgbuttons: true,
		pginput: false,
		hidegrid: false, 
		recordpos:'left',
		multiselect:false,
		onSelectRow:function(rowid,statue){
		},
		onSelectAll:function(rowids,statue){
		},
		gridComplete:function(){
			var img_v = (new Date()).getMilliseconds();
			// 得到显示到界面的id集合
			var IDS = $("#cnsmCnfmList").getDataIDs();
			// 当前显示多少条
			var length = IDS.length;
			var pill = $("#cnsmCnfmList");

			for (var i = 0; i < length; i++) {
				// 从上到下获取一条信息
				var rowData = pill.jqGrid('getRowData', IDS[i]);

				if (rowData["spec_kind"] == "1") {
					pill.find("tr#" + IDS[i] + " td[aria\\-describedby='cnsmCnfmList_code']")
						.attr("href", "/photos/consumable/" + rowData["code"] + "_fix.jpg?v=" + img_v)
						.addClass("image-link")
						.preview();
				}
				if (!rowData["status"]) {
					pill.find("tr#" + IDS[i]).addClass("otherPosition");
				}
			}

			radCnsmPosSel();
			showAssembleMap();
		}
	});
	loaded = false;
	$("#working_detail").after($cnsm_icon.hide());

	$("#cnsmCnfmList").on("click", "span.recmd", function(){
		$(this).closest("tr").find("input.cur_quantity").val(this.innerText).trigger("change");
		return false;
	});

	$("#cnsmCnfmList").on("change", "input.cur_quantity", function(){
		recAssembleMap($(this).closest("tr").attr("id"), this.value);
		return false;
	});

	return {
		"load" : function(material_id) {
			var postData = {
				material_id : material_id,
				belongs : inline_consumable
			};
			this_material_id = material_id;

			loaded = false;
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : 'material_consumable.do?method=search',
				cache : false,
				data : postData,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj, textStatus) {
					var resInfo = JSON.parse(xhrobj.responseText);

					if (resInfo.errors.length !== 0) {
						// 共通出错信息框
						treatBackMessages(null, resInfo.errors);
					} else {
						loaded = true;
						var used = toAssembleMap(resInfo.assembleList);
						$("#cnsmCnfmList").jqGrid().clearGridData();
						$("#cnsmCnfmList").jqGrid('setGridParam',{data:resInfo.bomList}).trigger("reloadGrid", [{current:false}]);
						if (resInfo.bomList && resInfo.bomList.length) {
							$cnsm_icon.show();
							if (!used) {
								$cnsm_icon.trigger("click");
							}
						} else {
							$cnsm_icon.hide();
						}
					}
				}
			});
		},
		"open" : function(){
			$cnsm_icon.trigger("click");
		},
		"valued" : function(){
			var cCount = $("#cnsmCnfmList tr:not(.otherPosition):not(.jqgfirstrow)").length;
			if (cCount == 0) {
				return "";
			}

			if ($("#cnsmCnfmList input.cur_quantity").filter(function(){
				if (this.value != "") return true;
				if ($(this).prev().length > 0) return true;
				return false;
			}).length == 0) return "禁止";

			var unmatch = "";

			$("#cnsmCnfmList .recmd.cnsm").each(function(idx, ele) {
				var $recmd = $(ele);
				var recmd = +($recmd.text());
				var $tr = $recmd.closest("tr");

				var partial_id = $tr.attr("id");
				var inp = (assembleMap[partial_id].recept_quantity || 0) 
					+ (assembleMap[partial_id].other_recept_quantity || 0);

				if ($tr.is(".otherPosition") && inp == 0) {
					return;
				}	
				if (inp != recmd) {
					unmatch += "消耗品" + $tr.find("td[aria\\-describedby='cnsmCnfmList_code']").text()
						+ "的安装量与参考值不符。<br>";
				}
			})
			return unmatch;
		}
		"valuedCcd" : function(){

			if ($("#cnsmCnfmList input.cur_quantity").length == 0) {
				return "";
			}

			if ($("#cnsmCnfmList input.cur_quantity").filter(function(){
				if (this.value != "") return true;
				if ($(this).prev().length > 0) return true;
				return false;
			}).length == 0) return "禁止";

			var unmatch = "";
			$("#cnsmCnfmList .recmd.cnsm").each(function(idx, ele) {
				var $recmd = $(ele);
				var recmd = +($recmd.text());
				var $tr = $recmd.closest("tr");

				var partial_id = $tr.attr("id");
				var inp = (assembleMap[partial_id].recept_quantity || 0) 
					+ (assembleMap[partial_id].other_recept_quantity || 0);

				if ($tr.is(".otherPosition") && inp == 0) {
					return;
				}	
				if (inp != recmd) {
					unmatch += "CCD 零件" + $tr.find("td[aria\\-describedby='cnsmCnfmList_code']").text()
						+ "的安装量与参考值不符。<br>";
				}
			})
			return unmatch;
		}
	}
}();
</script>
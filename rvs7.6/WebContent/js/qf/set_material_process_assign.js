var setMpaObj = (function() {


var lightRepairs=[];
var chosedPat = {};
var chosedPos = {};
var positionMapping={};
var lf_material_id = null;
var isLightHandled = false;

var showeditLightHandleComplete = function(xhrobj){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#pa_main").flowchart("fill", resInfo.processAssigns);
			//对象机型
			if (resInfo.isCcdModel)
				// 增加302工位选择
				$("#pa_main").prepend('<div class="edgeposition"><div class="just"><div code="25" posid="25" nextcode="9999999" prevcode="0" class="pos"><span>302\nCCD 盖玻璃更换</span></div></div>');
			if (resInfo.isLgModel)
				// 增加303工位选择
				$("#pa_main").prepend('<div class="edgeposition"><div class="just"><div code="60" posid="60" nextcode="0" prevcode="0" class="pos"><span>303\nLG 玻璃更换</span></div></div>');
			if ($("#major_pat").attr("value")) {
				var $pos331 = $(".pos[code=28]").closest(".edgeposition");
				// 增加304工位选择
				$pos331.before('<div class="edgeposition"><div class="just"><div code="66" posid="66" nextcode="0" prevcode="0" class="pos"><span>304\nCCD 线更换</span></div></div>');
			}

			$("#light_repair_process .subform tbody tr").each(function(index,ele){
				var $tr = $(ele);

				$tr.removeClass("ui-state-active");
				$tr.addClass("unact");
			});

			chosedPat = {};
			chosedPos = {};

			$("#pa_main .pos[posid] span").on("click",function(){
				var $span = $(this);
				if ($span.hasClass("suceed")) {
					return;
				} else {
					var pos_id = $span.parent().attr("code");
					if ($span.hasClass("point")) {
						$span.removeClass("point");
						chosedPos[pos_id] = 0;
					} else {
						$span.addClass("point");
						chosedPos[pos_id] = 1;
					}
				}
				showResult(3.3);
			}).each(function(idx, ele) {
				var $span = $(ele);
				var $tdp = $span.parent();
				var pos_id = $tdp.attr("code");
				if (positionMapping[pos_id] != null) {
					$tdp.attr({ "map_id": positionMapping[pos_id][0] });
					$span.attr({ "map_code" : positionMapping[pos_id][1] });
				}
			});

			showResult(3);
			isLightHandled = true;

			$("#pa_main").parent().trigger("scroll").scrollTop(0);
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
	};
};

var getDetail_ajaxSuccess=function(xhrobj){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {

			$("#ref_template").html($("#paOptions").val());
			$("#ref_template").select2Buttons();

			lightRepairs = resInfo.lightFixs;//小修理标准编制
			var processAssigns = resInfo.processAssigns;//维修对象独有修理流程
			var materialForm = resInfo.materialForm;
			positionMapping = resInfo.positionMapping;//对应的小修理工位

			//工位
			$("#pa_main").html("");
			$("#pa_main").flowchart({},{editable:false, selections: resInfo.list});

			if(materialForm.pat_id!=null && materialForm.pat_id!=''){
				$("#ref_template").val(materialForm.pat_id).trigger("change");
			}else{
				$("#ref_template").val("").trigger("change");
			}

			//生产小修理流程工位内容
			var patContent = "";
			for (var index in lightRepairs) {
				var lightRepair = lightRepairs[index];
				patContent += "<tr lf_id='"+ lightRepair.light_fix_id
						+ (lightRepair.correlated_pat_id ? "' pat_id='" + lightRepair.correlated_pat_id : "") +"' class='unact'>"
						+ "<td>" + lightRepair.activity_code + "</td>"
						+ "<td>" + lightRepair.description + "</td>"
						+ "</tr>";
			};

			$("#light_repair_process .subform tbody").html(patContent);

			var timeoutFunc = function(){

				if (!isLightHandled) {
					setTimeout(timeoutFunc, 310);
					return;
				}
				var materialLightFixs = resInfo.materialLightFixs;//维修对象选用小修理
				$("#light_repair_process .subform tbody tr").removeClass("ui-state-active").addClass("unact");
				for(var index in materialLightFixs){
					var materialLightFix = materialLightFixs[index];
					var lf_id = materialLightFix.light_fix_id;
					chosedPat[lf_id] = 1;
					$("#light_repair_process .subform tbody tr").each(function(){
						var $tr = $(this);
						if(lf_id==$tr.attr("lf_id")) $tr.addClass("ui-state-active").removeClass("unact");
					});
				}

				changeFlow();
				$("#pa_main div.pos").each(function(index,ele){
					var $div = $(ele);
					var code = $div.attr("code");
					var $span = $div.find("span");
					for(var i=0;i<processAssigns.length;i++){
						var obj = processAssigns[i];
						if(code == obj.position_id && !$span.hasClass("suceed")){
							$span.addClass("point");
							chosedPos[code]=1;
							break;
						}
					}
				});

				showResult(4);
			}

			if (isLightHandled) {
				timeoutFunc();
			} else {
				setTimeout(timeoutFunc, 310);
			}

			//小修理流程工位TR单击事件
			$("#light_repair_process .subform tbody tr").click(function(){
				var $tr = $(this);
				var lf_id = $tr.attr("lf_id");

				if ($tr.hasClass("ui-state-active")) {
					$tr.removeClass("ui-state-active");
					$tr.addClass("unact");
					chosedPat[lf_id] = 0;
					changeFlow();
					showResult(5.0);
				}else{
					$tr.addClass("ui-state-active");
					$tr.removeClass("unact");
					chosedPat[lf_id] = 1;

					var correlated_pat_id = $tr.attr("pat_id");
					if (correlated_pat_id) {
						var $patTr = $("#ref_template option[value='" + correlated_pat_id + "']");
						if ($patTr.length > 0 && !$patTr.attr("selected")) {
							var curRefText = $("#ref_template option:selected").text();
							warningConfirm("维修项目：" + $tr.children("td:eq(0)").text() + "具有关联的参考流程，是否将流程切换到【" + $patTr.text() + "】？"
								+ "<br>（现有流程【" + curRefText + "】）",
								function() {
									$("#ref_template").val(correlated_pat_id).trigger("change");
								},
								function() {
									changeFlow();
									showResult(5.3);
								}, "是否切换流程", "切换后重新选择", "保持现有流程继续"
							)
						} else {
							changeFlow();
							showResult(5.2);
						}
					} else {
						changeFlow();
						showResult(5.1);
					}
				}
			});

			$("#light_fix_dialog").dialog({
				title : "中小修理维修内容流程设定",
				modal : true,
				width: 980,
				height : 660,
				resizable: false,
				buttons:{
					"确定":function(){
						update_material_process_assign();
					},
					"取消":function(){
						$(this).dialog("close");
					}
				}
			});

			$("#light_fix_dialog").show();
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
	};
};

var changeFlow = function(){
	var resultPos = {};
	for (var lf_id in chosedPat) {
		if (chosedPat[lf_id] == 1) {
			for (var iLightRepair in lightRepairs) {
				var lightRepair = lightRepairs[iLightRepair];
				if (lf_id == lightRepair.light_fix_id) {
					for (var iprocesses in lightRepair.position_list) {
						var process = lightRepair.position_list[iprocesses];
						resultPos[process] = 1;
					}
				}
			}
		}
	};
	$(".pos[posid]").find("span").removeClass("suceed").removeClass("point");//清楚所有样式

	for (var process in resultPos) {
		$(".pos[code="+ process +"]").find("span").addClass("suceed");
	};

	for (var process in chosedPos) {
		if (chosedPos[process] == 1)
		$(".pos[code="+ process +"]").find("span:not('.suceed')").addClass("point");
	}
};

var update_material_process_assign=function(){
	var data={
		"material_id": lf_material_id,
		"pad_id":$("#ref_template").val()
	};

	var i=0;
	for (var lf_id in chosedPat) {
		if (chosedPat[lf_id] == 1) {
			data["material_light_fix.light_fix_id[" + i + "]"] = lf_id;
			i++;
		}
	}

	//工位
	var count=0;
	var chainHead = null;
	var chain = {};
	var mappedmap = {}, tomap = {};
	var $checked = $("#pa_main div.pos span").filter(".point, .suceed");
	var total = $checked.length;
	var mappingCursor = null;

	var nextUsed = {};

	$checked.each(function(index,ele){
		var $span = $(ele);

		var $posData = $span.parent();
		var code = $posData.attr("code");
		var mappingCode = $posData.attr("map_id");
		if (code != "25") {
			var item = {code: code};
			if ($posData.attr("prevcode") == "0" && $posData.parents(".just-multi").length == 0) {
				chainHead = item;
			}
			item["prev"] = getPrevPos($posData);
			chain[code] = item;
		}
		data["material_process_assign.position_id[" + count + "]"] = code;

		if (mappingCursor != mappingCode) {
			if (mappingCode && mappedmap[mappingCode] != null) {
//				errorPop("中小修对应工位不能在流程中多次出现，请保持连续的工位对应同一个中小修工位。");
//				return;
				nextUsed[code] = 9999999;
			} else {
				mappingCursor = mappingCode;
			}
		}

		if (nextUsed[code] == null) {
			for (var itm in nextUsed) {
				if (nextUsed[itm] == 9999999) {
					nextUsed[itm] = code;
				}
			}
		}

		if (mappingCode) {
			if (mappedmap[mappingCode] == null) {
				mappedmap[mappingCode] = {list : []};
			}
			mappedmap[mappingCode].list.push(code);
			tomap[code] = mappingCode;
		}

		if (count > 0) {
			data["material_process_assign.next_position_id[" + (count-1) + "]"] = code;
		}
		if (count < (total - 1)) {
			data["material_process_assign.prev_position_id[" + (count+1) + "]"] = code;
		}
		count++;
	});

	for (var idx = 0; idx < total; idx++) {
		var code = data["material_process_assign.position_id[" + idx + "]"];
		if (tomap[code] != null) {
			var mappingCode = tomap[code];
			if (mappedmap[mappingCode].prev == null)
				mappedmap[mappingCode].prev
					= data["material_process_assign.prev_position_id[" + idx + "]"] || 0;
			data["material_process_assign.prev_position_id[" + idx + "]"] = 0;

			var next_code = data["material_process_assign.next_position_id[" + idx + "]"];
			if (nextUsed[code] == null) {
				mappedmap[mappingCode].next = next_code;
			}

			data["material_process_assign.next_position_id[" + idx + "]"] = 0;

			data["material_process_assign.sign_position_id[" + idx + "]"] = mappingCode;
		}
	}

	for (var mappingCode in mappedmap) {
		data["material_process_assign.position_id[" + count + "]"] = mappingCode;
		data["material_process_assign.sign_position_id[" + count + "]"] = mappingCode;

		data["material_process_assign.prev_position_id[" + count + "]"] = mappedmap[mappingCode].prev || 0;

		var next_code = mappedmap[mappingCode].next || 9999999;
		if (nextUsed[next_code] != null) next_code = nextUsed[next_code]; 
		data["material_process_assign.next_position_id[" + count + "]"] = next_code;

		count++;
	}

	for (var idx = 0; idx < count; idx++) {
		var prev = data["material_process_assign.prev_position_id[" + idx + "]"];
		var next = data["material_process_assign.next_position_id[" + idx + "]"];

		if (prev == null) {
			data["material_process_assign.prev_position_id[" + idx + "]"] = 0;
		}else if (tomap[prev] != null) {
			data["material_process_assign.prev_position_id[" + idx + "]"] = tomap[prev];
		}

		if (next == null) {
			data["material_process_assign.next_position_id[" + idx + "]"] = 0;
		}else if (tomap[next] != null) {
			if (nextUsed[next] != null) {
				next = nextUsed[next];
			}
			if (tomap[next] != null) {
				data["material_process_assign.next_position_id[" + idx + "]"] = tomap[next];
			} else {
				data["material_process_assign.next_position_id[" + idx + "]"] = next;
			}
		}
	}

	if (Object.keys(nextUsed).length) {
		var prevUsed = {};
		for (var idx = 0; idx < count; idx++) {
			var next = data["material_process_assign.next_position_id[" + idx + "]"];
			if (next && next != 9999999) {
				prevUsed[next] = data["material_process_assign.position_id[" + idx + "]"];
			}
		}
		for (var next in prevUsed) {
			for (var idx = 0; idx < count; idx++) {
				if (data["material_process_assign.position_id[" + idx + "]"] == next) {
					data["material_process_assign.prev_position_id[" + idx + "]"] = prevUsed[next];
				}
			}
		}
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'material_process_assign.do?method=doUpdateMaterialProcessAssign',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			var resInfo = null;

			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);

				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#light_fix_dialog").dialog("close");
				}
			} catch(e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
			}
		}
	});
};

function getPrevPos($posData){
	var posid = $posData.attr("posid");
	var code = $posData.attr("code");
	var prevcode = $posData.attr("prevcode");
	var nextcode = $posData.attr("nextcode");
	if (code == null) return null;

	if (posid == code) {
		if (prevcode == "0") {
			var $justMulti = $posData.parents(".just-multi:first");
			if ($justMulti.length == 0) {
				return "0";
			} else {
				return getPrevPos($justMulti.parent().parent());
			}
		}
		var $thePrev = $("#pa_main").find(".div[code="+code+"]");
		if ($thePrev.children("span").hasClass("point") || $thePrev.children("span").hasClass("suceed")) {
			return $thePrev.attr("code");
		} else {
			return getPrevPos($thePrev);
		}
	}
	return null;
}

var showResult= function(intt) {

	var $showTarget = $("#light_repair_record > div");
	var processText = "";
	$("#light_repair_process .ui-state-active").each(function(idx, ele){
		processText += "; " + $(ele).find("td:eq(1)").text();
	});
	var positionText = "";
	var mapedMapper = {}; var mapedList = []; //  var mappingCursor = false;
	$("#pa_main .suceed,#pa_main .point").each(function(idx, ele){
		var eleText = $(ele).text();
		if (eleText.indexOf("\n") >= 0) {
			eleText = eleText.split("\n")[0];
		}

		var mapCode = $(ele).attr("map_code");
		if (mapCode) {
			if (mapedMapper[mapCode] == null) {
				mapedMapper[mapCode] = eleText;
				mapedList.push(mapCode);
//				if (mappingCursor) positionText += ")";
//				positionText += "->" + mapCode + "(" + eleText;
			} else {
				mapedMapper[mapCode] += "," + eleText;
			}
//			mappingCursor = true;
		} else {
//			if (mappingCursor) positionText += ")";
			mapedList.push(eleText);
//			mappingCursor = false;
		}
	});
	
//	if (positionText && mappingCursor) positionText += ")";
	for (var il in mapedList) {
		var codes = mapedMapper[mapedList[il]];
		if (codes == null) {
			positionText += "->" + mapedList[il];
		} else {
			positionText += "->" + mapedList[il] + "(" + codes + ")";
		}
	}

	var showText = "";
	if (processText.length) {
		showText += "所选择的中小修理流程为：" + processText.substring(2);
		if (positionText.length) {
			showText += "<BR>作业流程为：" + positionText.substring(2);
		}
	} else {
		if (positionText.length) {
			showText += "作业流程为：" + positionText.substring(2);
		}
	}
	$showTarget.html(showText);
}

return {
	initDialog : function($light_fix_dialog, arr_material_id, arr_level, arr_model_id, allcheckable){
		isLightHandled = false;

		lf_material_id = arr_material_id;

		$light_fix_dialog.load("widgets/light_fix.jsp", function(responseText, textStatus, XMLHttpRequest) {

			$(this).hide();

			if (allcheckable) {
				var $allpos = $("<tr><td colspan='2'><input type='button' type='ui-button' id='ref_allpos' value='选择全部工位'></td></tr>");
				$allpos.find("#ref_allpos").button().click(function(){
					$(".pos span").each(function(idx, ele){
						var $span = $(ele);
						if ($span.hasClass("suceed")) {
							return;
						} else {
							var pos_id = $span.parent().attr("code");
							if ($span.hasClass("point")) {
							} else {
								$span.addClass("point");
								chosedPos[pos_id] = 1;
							}
						}
					});

					showResult(1);
				});
				$("#ref_template").parents("table").eq(0).append($allpos);
			}

			lightRepairs=[];
			positionMapping={};
			chosedPat = {};
			chosedPos = {};
			var data={
				"material_id": lf_material_id,
				"level" : arr_level
			}

			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url :'material_process_assign.do?method=getDetail',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : getDetail_ajaxSuccess
			});

			//设定维修流程
			$("#ref_template").change(function(){
				if (this.value === "") {
					$("#pa_main").html("");
					$("#light_repair_process .subform tbody tr").each(function(index,ele){
						var $tr = $(ele);

						$tr.removeClass("ui-state-active");
						$tr.addClass("unact");
					});
					chosedPat = {};
					chosedPos = {};
					showResult(1.1);
				}else {
					var data = {
						"id" : this.value,
						lf_model_id : arr_model_id
					};

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : 'materialFact.do?method=getPa',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : showeditLightHandleComplete
					});
				}
			});
	
		});
	}
}
})();

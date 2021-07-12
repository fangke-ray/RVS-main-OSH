var instruction_filter = '<table id="inst_filter" class="condform"><tbody><tr><td class="ui-state-default td-title">零件代码</td><td class="td-content" style="width: 128px;"><input type="text" id="inst_partial_code" alt="零件代码" class="ui-widget-content"></td>' +
'<td class="ui-state-default td-title">Rank BOM</td><td class="td-content"><select id="inst_rank_bom" alt="Rank BOM"><option value="">(全部)</option><option value="1">BOM</option><option value="-1">非 BOM</option></select></td>' +
'<td class="ui-state-default td-title">定位工程</td><td class="td-content"><select id="inst_line_id" alt="定位工程"><option value="">(全部)</option><option value="00000000012">分解</option><option value="00000000013">NS</option><option value="00000000014">总组</option></select></td>' +
'<td class="ui-state-default"><input type="button" value="清除过滤"></td>' +
'</tr></tbody></table>';

var instruction_construct = function(modelName, instructLists, forMaterial, highprice, edittype) {
	var $ht = $("<div id='inst_sheet'></div>");
	var $htPage = $("<div id='inst_page'></div>");
	var $htList = $("<div id='inst_list' class='ui-widget-content ui-jqgrid'></div>");

	var htHead = null;
	if (forMaterial) {
		htHead = "<table id='inst_head' cellspacing='0' border='0'>" +
			"<thead><tr><th class='ui-state-default'>零件代码</th><th class='ui-state-default'>说明</th>" +
			"<th class='ui-state-default'>指示数</th><th class='ui-state-default'>参考单价</th><th class='ui-state-default'>定位</th>" +
			"<th class='ui-state-default'>RANK</th><th class='ui-state-default'>SHIP</th><th class='ui-state-default'>报价</th><th class='ui-state-default'>在线</th>" +
			"</tr></thead></table>";
	} else {
		htHead = "<table id='inst_head' cellspacing='0' border='0'>" +
			"<thead><tr><th class='ui-state-default'>零件代码</th><th class='ui-state-default'>说明</th><th class='ui-state-default'>BOM 代码</th>" +
			"<th class='ui-state-default'>Rank BOM</th><th class='ui-state-default'>指示数</th><th class='ui-state-default'>参考单价</th><th class='ui-state-default'>定位</th></tr></thead></table>";
	}

	if (highprice) {
		highprice = parseFloat(highprice);
	}

	var editable = (edittype == 1 || edittype == 2);

	for (var pageName in instructLists) {
		var instructList = instructLists[pageName];
		$htPage.append("<input type='radio' class='inst_page' name='inst_page' id='inst_page_" + pageName + "'></input><label for='inst_page_" + pageName + "'>" 
			+ (pageName === "@" ? modelName : pageName) + "</label>");
		var htTable = "<table class='ui-jqgrid-btable' for='" + pageName + "' cellspacing='0' border='0'><tbody><tr class='tr-eq0'>";
		if (forMaterial) {
			htTable += "<td></td><td></td></td><td for='quantity'></td><td for='price'></td><td for='process_code'></td><td></td><td></td><td></td><td></td></tr>";
		} else {
			htTable += "<td></td><td></td><td></td><td for='rank'></td><td for='quantity'></td><td for='price'></td><td for='process_code'></td></tr>";
		}

		if (forMaterial) {
			for (var iIl in instructList) {
				var partial = instructList[iIl];
				var priceHtml = "";
				if (!partial.price) {
					priceHtml = "<td for='price'>unknown</td>";
				} else {
					if (highprice && highprice < partial.price) {
						priceHtml = "<td for='price' class='noticeprice'>"+ partial.price +"</td>";
					} else {
						priceHtml = "<td for='price'>"+ partial.price +"</td>";
					}
				}

				var quantityHtml = "";
				if (editable) {
					quantityHtml = "<td for='quantity' class='for_edit'><span>" + partial.quantity + "</span><button class='ui-icon ui-icon-plus'/></td>";
				} else {
					quantityHtml = "<td for='quantity'><span>" + partial.quantity + "</span></td>";
				}

				htTable += "<tr class='ui-widget-content jqgrow ui-row-ltr' bom_code='" + partial.bom_code + "' partial_id='" + partial.partial_id + "'"
					+ (partial.level ? " level=" + partial.level : "") + ">"
					+ "<td>" + partial.code + "</td><td>" 
					+ (partial.name || "") + "</td>" + quantityHtml + priceHtml
					+ "<td for='process_code'" + instru_func.getLineAttr(partial.line_id) + ">" + instru_func.getProcessCode(partial.process_code) + "</td>"
					+ "<td></td><td></td><td></td><td></td></tr>";
			}
		} else {
			for (var iIl in instructList) {
				var partial = instructList[iIl];
				htTable += "<tr class='ui-widget-content jqgrow ui-row-ltr' partial_id='" + partial.partial_id + "'"
					+ (partial.level ? " level=" + partial.level : "") + ">"
					+ "<td>" + partial.code + "</td><td>" 
					+ (partial.name || "")  + "</td><td>" + partial.bom_code
					+ "</td><td for='rank'></td><td for='quantity'>" + (partial.quantity || "unknown") 
					+ "</td><td for='price'>" + (partial.price || "unknown")
					+ "</td><td for='process_code'" + instru_func.getLineAttr(partial.line_id) + ">" + instru_func.getProcessCode(partial.process_code) + "</td></tr>";
			}
		}
		htTable += "</tbody></table>";
		$htPage.buttonset();
		$htPage.children("input").click(function(){
			var thisFor = this.id.substring('inst_page_'.length);
			var $sTable = $("#inst_list").children("table").hide().end()
				.children("table[for='"+ thisFor +"']").show();
			var $inst_head = $("#inst_head");
			$sTable.find("tr:eq(0) > td").each(function(idx, ele){
				$inst_head.find("th:eq(" + idx + ")").css("width", parseInt($(ele).css("width")) - 2);
			});
		});
		$htList.append(htTable);
	}

	if (forMaterial) {
		$htList.children("table").find("tr.tr-eq0").each(function(idx, ele){
			var $tr = $(ele);
			$tr.find("td[for=quantity]").css("width", "4em");
			$tr.find("td[for=process_code]").css("width", "4em");
			$tr.find("td:eq(0)").css("width", "8em");
			$tr.find("td:eq(1)").css("width", "32em");
		});
	} else {
		$htList.children("table").find("tr.tr-eq0").each(function(idx, ele){
			var $tr = $(ele);
			$tr.find("td[for=rank]").css("width", "3em");
			$tr.find("td[for=quantity]").css("width", "4em");
			$tr.find("td[for=process_code]").css("width", "4em");
			$tr.find("td:eq(0)").css("width", "8em");
			$tr.find("td:eq(1)").css("width", "40em");
		});
	}

	$htList.children("table").find("tr[level]").each(function(idx, ele){
		var $tr = $(ele);
		var $parentTr = $tr.prevUntil("tr:not([level])");
		if ($parentTr.length == 0) {
			$tr.prev().addClass("hasChildren");
		}
	});

	$ht.append(instruction_filter).append($htPage).append(htHead).append($htList);
	return $ht;
}

var instruct_load_by_working = function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'materialPartInstruct.do?method=instructLoadByWorking',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors && resInfo.errors.length) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
				return;
			}
			var $instructDialog = $("#instruct_dialog");
			if ($instructDialog.length == 0) {
				$(document.body).append("<div id='instruct_dialog'></div>");
				$instructDialog = $("#instruct_dialog");
			}
			if (!(resInfo.instructLists && resInfo.instructLists['@'])) {
				errorPop("此型号尚未导入工作指示单。");
				return;
			}
			if (resInfo.highprice) {
				material.highprice = resInfo.highprice;
			}
			instruction_show($instructDialog, resInfo.material.model_name, resInfo.instructLists, resInfo.components, resInfo.material, 
				resInfo.instuctForMaterial, resInfo.rankBom, resInfo.partialBom, resInfo.edittype);
		}
	});
}

var instruction_show = function($container, modelName, instructLists, components, materialEntity, instuctForMaterial, rankBom, partialBom, edittype) {
	cur_edit_type = edittype;

	var editable = edittype > 0;
	var forMaterial = !!(instuctForMaterial && instuctForMaterial.length);
	var highprice = null;
	if (materialEntity && materialEntity.highprice) highprice = materialEntity.highprice;

	var $instruction_html = instruction_construct(modelName, instructLists, forMaterial, highprice, edittype);

	$container.html($instruction_html);
	var $inst_page  = $container.find("#inst_page");
	var $inst_list  = $container.find("#inst_list");

	setFilter_func($container.find("#inst_filter"));

	if (components && components.length > 0) {
		var $baseList = $inst_list.find("table:eq(0) .jqgrow");
		for (var iCom in components) {
			var component_code = components[iCom];
			$inst_page.find("#inst_page_" + component_code).next().addClass("component");
			for (var idxTr in $baseList) {
				var tr = $baseList[idxTr];
				if (!tr.firstChild) {
					continue;
				}
				if (tr.firstChild.innerText === component_code) {
					tr.firstChild.className = "component";
					break;
				}
			}
		}
	}

	if (forMaterial) {
		instruction_bind($inst_list, instuctForMaterial);
	}

	var dialogTitle = modelName;

	if (materialEntity && materialEntity.omr_notifi_no) {
		dialogTitle = "修理单号 " + materialEntity.omr_notifi_no;
	}

	if (materialEntity && !rankBom && !partialBom) {
		materialEntity.level = null;
	}
	if (materialEntity == null || !materialEntity.level ) {
		$("#inst_rank_bom").parent().hide()
			.prev().hide();
	} else {
		if (!materialEntity.omr_notifi_no) {
			dialogTitle += "(" + materialEntity.rankName + "等级)";
		}

		if (rankBom) {
			alert("rankBom:" +rankBom.length);
		} else if (partialBom) {
			var msg = "";
			for (var iBom in partialBom) {
				var partial_id = partialBom[iBom];
				var $hitTr = $inst_list.find("tr.jqgrow[partial_id=" + partial_id + "]");
				if ($hitTr.length > 0) {
					$hitTr.attr("rank", true);
					$hitTr.children("td[for=rank]").text("*");
				} else {
					msg += partial_id+";";
				}
			}
			if (msg) instr_msg.warning += ("无法匹配的bom" + msg + "\n");
		}
	}
	dialogTitle += " 的工作指示单";

	if (materialEntity && materialEntity.material_id) {
		this_instruction_material_id = materialEntity.material_id;
	} else {
		this_instruction_material_id = null;
	}

	setTimeout(function(){
		$inst_page.children("input:eq(0)").trigger("click");
	}, 250)

	$container.dialog({
	    resizable : false,
		modal : true,
		width: 1024,
		height: 660,
		title : dialogTitle,
		buttons : {
			"关闭" : function() {
				$container.dialog("close");
			}
		}
	});
}

var instruction_bind = function($inst_list, instuctForMaterial) {

	for (var iv in instuctForMaterial) {
		var itm = instuctForMaterial[iv];
		var $targetTr = $inst_list.find("tr[bom_code='" + itm.bom_code + "']");
		if ($targetTr.length == 1) {
			var rankText = (itm.rank ? (itm.rank > 1 ? "SORC-R" : "RANK") : "") 
			var $td = $targetTr.children("[for=process_code]")
				.next().text(rankText) // RANK
				.next(); // SHIP
			if (itm.ship) {
				$td.text("　√");
			}
			$td = $td.next(); // quote_job_no
			if (itm.quote_job_no) {
				$td.html("<img src='/images/sign/"+itm.quote_job_no+"'></img>");
			}
			$td = $td.next(); // inline_job_no
			if (itm.inline_job_no) {
				$td.html("<img src='/images/sign/"+itm.inline_job_no+"'></img>");
				if (itm.inline_comment) {
					$td.attr("inline_comment", itm.inline_comment);
				}
				if (itm.my_priv) {
					$targetTr.attr("my_priv");
				}
			}
			$targetTr.addClass("instructed");
			$targetTr.find(".ui-icon-plus").removeClass("ui-icon-plus").addClass("ui-icon-minus");
		} else {
			instr_msg.warning += "维修单零件列表中在[" + itm.bom_code + "]BOM数据中异常。\n";
		}
	}

	if (cur_edit_type > 0) {
		$inst_list.on("mouseenter", "td[for='quantity']", function(){
			$(this).css("backgroundColor", "lightgreen");
		}).on("mouseleave", "td[for='quantity']", function(){
			$(this).removeAttr("style");
		}).on("click", "td[for='quantity']", instru_func.updateItem);
	}
}

var instr_msg = {
	warning : "",
	notice : ""
}

var cur_edit_type = 0;
var this_instruction_material_id = null;

var setFilter_func = function($filter) {
	$filter.find("select").select2Buttons();
	$filter.find("input:button").button()
		.click(function(){
			$filter.find("#inst_partial_code").val("");
			$filter.find("select").val("").trigger("change");
		});
	$filter.find("#inst_partial_code")// .on("change", triggerFilter)
		.on("keyup", triggerFilter);
	$filter.find("#inst_rank_bom").on("change", triggerFilter);
	$filter.find("#inst_line_id").on("change", triggerFilter);
}

var triggerFilter = function(evt) {
	if (evt.type === "keyup") {
		var kCode = evt.keyCode;
		if (kCode <= 57 && kCode >= 48) {
		} else if (kCode <= 90 && kCode >= 65) {
		} else {
			switch(kCode) {
				case $.ui.keyCode.DELETE :
				case $.ui.keyCode.BACKSPACE :
					break;
				default:
					return;
			}
		}
	}

	var noFilter = true;
	var partial_code = $("#inst_partial_code").val().toUpperCase(); if (partial_code) noFilter = false;
	var rank_bom = $("#inst_rank_bom").val(); if (rank_bom) noFilter = false;
	var line_id = $("#inst_line_id").val(); if (line_id) noFilter = false;

	$("#inst_list > table tr.jqgrow").removeClass("hide_by_filter");
	if (noFilter) {
		$("#inst_page .inst_page + label").removeAttr("cnt");
	} else {
		var showRankTd =  $("#inst_list td[for=rank]").length;

		$("#inst_list > table tr.jqgrow").filter(function(a, ele){
			var $tr = $(ele);
			if (partial_code) {
				if ($tr.children("td:eq(0)").text().startsWith(partial_code)) {
					return false;
				}
			}
			if (rank_bom == 1) {
				if (showRankTd) {
					if ($tr.children("td[for=rank]").text()) {
						return false;
					}
				} else {
					if ($tr.attr("rank")) {
						return false;
					}
				}
			}
			if (rank_bom == -1) {
				if (showRankTd) {
					if (!$tr.children("td[for=rank]").text()) {
						return false;
					}
				} else {
					if (!$tr.attr("rank")) {
						return false;
					}
				}
			}
			if (line_id) {
				var $tdProcessCode = $tr.children("td[for=process_code]")
				if ($tdProcessCode.text() === "未设定") {
					return false;
				} else if ($tdProcessCode.attr("ln_" + line_id)) {
					return false;
				}
			}
			return true;
		}).addClass("hide_by_filter");

		$("#inst_list > table tr.show_for_child").removeClass("show_for_child");

		$("#inst_list > table tr.jqgrow[level]:not(.hide_by_filter)").each(function(idx,ele){
			var $tr = $(ele);

			instru_func.showForChild($(ele));

		});

		$("#inst_page .inst_page + label").each(function(idx,ele){
			var $label = $(ele);
			var forPage = $label.attr("for").substring('inst_page_'.length);
			var $sTable = $("#inst_list")
				.children("table[for='"+ forPage +"']");
			var hitCnt = $sTable.find("tr.jqgrow").not(".hide_by_filter").length;
			if (hitCnt) {
				$label.attr("cnt", hitCnt);
			} else {
				$label.removeAttr("cnt");
			}
		});
	}
}

var instru_func = {
	getLineAttr : function(line_id) {
		if (!line_id) {
			return "";
		}
		if (line_id.indexOf(";") >= 0) {
			var retAttr = "";
			var line_array = line_id.split(";");
			for (var il in line_array) {
				retAttr += " ln_" + line_id + "=1";
			}
			return retAttr;
		} else {
			return " ln_" + line_id + "=1";
		}
	},
	getProcessCode : function(process_codes) {
		if (!process_codes) {
			return "未设定";
		}
		if (process_codes.indexOf(";") >= 0) {
			var process_array = process_codes.split(";");
			var ret = "";
			for (var ip in process_array) {
				ret += "<br>" + process_array[ip];
			}
			return ret.substring(4);
		} else {
			return process_codes.substring(0, 3);
		}
	},
	showForChild : function($tr) {
		var thisLevel = $tr.attr("level");
		var $parentTr = null;

		switch (thisLevel) {
			case "2" :
				$parentTr = $tr.prevUntil("tr.hasChildren");
				break;
			case "3" :
				$parentTr = $tr.prevUntil("tr[level=2]");
				break;
			case "4" :
				$parentTr = $tr.prevUntil("tr[level=3]");
				break;
		}
		if ($parentTr == null) return;
		if ($parentTr.length == 0) {
			$parentTr = $tr.prev();
		} else {
			$parentTr = $parentTr.eq(-1).prev();
		}

		if ($parentTr.length > 0 && $parentTr.hasClass("hide_by_filter")) {
			$parentTr.addClass("show_for_child");
			switch (thisLevel) {
				case "3" :
				case "4" :
					instru_func.showForChild($parentTr);
					break;
			}
		}
	},
	updateItem : function(){
		var $tdQua = $(this);
		var $trItem = $tdQua.parent();
		if ($trItem.hasClass("instructed")) {
			if (!$trItem.hasClass("cancel")) {
				$trItem.addClass("cancel");
				$tdQua.children("button").removeClass("ui-icon-minus").addClass("ui-icon-plus");
				instru_func($trItem);
			} else {
				$trItem.removeClass("cancel");
				$tdQua.children("button").removeClass("ui-icon-plus").addClass("ui-icon-minus");
				instru_func($trItem);
			}
		} else {
			if (!$trItem.hasClass("append")) {
				$trItem.addClass("append");
				$tdQua.children("button").removeClass("ui-icon-plus").addClass("ui-icon-minus");
				if (cur_edit_type == 2) {
					// 追加理由
				}
				// updateItemPost($trItem.attr("bom_code"), "append");
			} else {
				if ($trItem.attr("no_priv")) {
					errorPop("其他在线人员追加的项目不可以取消。");
					return;
				}
				$trItem.removeClass("append");
				$tdQua.children("button").removeClass("ui-icon-minus").addClass("ui-icon-plus");
			}
		}
	},
	updateItemPost : function(bom_code, action) {
		var postData = {
			"material_id" : this_instruction_material_id,
			"bom_code" : bom_code,
			"action" : action
		}
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'materialPartInstruct.do?method=doUpdateItem',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				
			}
		})
	},
	commitUpdateItem : function($trs){
		if ($trs.length > 0) {
			var postData = {};
			var idx = 0;
			$trs.each(function(idx, ele){
				var $tr = $(ele);
				postData["item[" + idx + "].partial_id"] = $tr.attr("partial_id");
				postData["item[" + idx + "].bom_code"] = $tr.attr("bom_code");
				if ($trItem.hasClass("append")) {
					postData["item[" + idx + "].quantity"] = $tr.children("td[for=quantity]").children("span").text();
					postData["item[" + idx + "].inline_comment"] = $tr.children("td[inline_comment]").attr("inline_comment");
				}
				idx++;
			});
		}
	}
}
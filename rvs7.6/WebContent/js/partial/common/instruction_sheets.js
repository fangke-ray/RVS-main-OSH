var INS_PROCEDURE = {
	NONE : 0,
	QUOTE : 1,
	INLINE : 2,
	CONFIRM : 3,
	TO_SUBMIT : 4,
	ORDERED : 5
}

var instruction_filter = '<table id="inst_filter" class="condform"><tbody><tr><td class="ui-state-default td-title">零件代码</td><td class="td-content" style="width: 128px;"><input type="text" id="inst_partial_code" alt="零件代码" class="ui-widget-content"></td>' +
'<td class="ui-state-default td-title">Rank BOM</td><td class="td-content"><select id="inst_rank_bom" alt="Rank BOM"><option value="">(全部)</option><option value="1">标配</option><option value="-1">非标</option><option value="9">★ 非标</option></select></td>' +
'<td class="ui-state-default td-title">定位工程</td><td class="td-content"><select id="inst_line_id" alt="定位工程"><option value="">(全部)</option><option value="00000000012">分解</option><option value="00000000013">NS</option><option value="00000000014">总组</option></select></td>' +
'<td class="ui-state-default"><input type="button" value="清除过滤"></td>' +
'</tr></tbody></table>';

var instruction_comment ="<input type='text' id='inst_comment_inputer' style='width:80%;'><hr><span class='ui-state-default' style='padding:4px;'>最近输入理由</span><br><div class='recently'></div><hr><span class='ui-state-default' style='padding:4px;'>历史输入理由</span><br><div class='history'></div>";

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

	var editable = (edittype == INS_PROCEDURE.QUOTE || edittype == INS_PROCEDURE.INLINE);

	for (var pageName in instructLists) {
		var instructList = instructLists[pageName];
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
				htTable += "<tr class='ui-widget-content jqgrow ui-row-ltr' bom_code='" + partial.bom_code + "' partial_id='" + partial.partial_id + "'"
					+ (partial.level ? " level=" + partial.level : "") + ">"
					+ "<td>" + partial.code + "</td><td>" 
					+ (partial.name || "")  + "</td><td>" + partial.bom_code
					+ "</td><td for='rank'></td><td for='quantity'>" + (partial.quantity || "unknown") 
					+ "</td><td for='price'>" + (partial.price || "unknown")
					+ "</td><td for='process_code'" + instru_func.getLineAttr(partial.line_id) + ">" + instru_func.getProcessCode(partial.process_code) + "</td></tr>";
			}
		}
		htTable += "</tbody></table>";
		$htList.append(htTable);

		var discr = null;
		if (pageName !== "@") {
			$htList.children("table:eq(0)").find("tr.jqgrow").each(function(idx, ele){
				if (discr != null) return;
				var $tr = $(ele);
				if ($tr.children("td:eq(0)").text() == pageName) {
					discr = $tr.children("td:eq(1)").text();
				}
			});
		}
		$htPage.append("<input type='radio' class='inst_page' name='inst_page' id='inst_page_" + pageName + "'></input><label for='inst_page_" + pageName + "'" + (discr ? " title='" + discr + "'" : "") + ">" 
			+ (pageName === "@" ? modelName : pageName) + "</label>");
	}
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
				resInfo.material.highprice = resInfo.highprice;
			}
			if (resInfo.callReasons) {
				setTimeout(resaon_load_by_working, 107);
			}
//			if (resInfo.job_no) cur_job_no = resInfo.job_no;
			instruction_show($instructDialog, resInfo.material.model_name, resInfo.instructLists, resInfo.components, resInfo.material, 
				resInfo.instuctForMaterial, resInfo.rankBom, resInfo.partialBom, resInfo.edittype);
		}
	});
}

var resaon_load_by_working = function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'materialPartInstruct.do?method=reasonsLoadByWorking',
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

			instr_reasons.history_reasons = resInfo.reasons;
		}
	});
}

var instruction_show = function($container, modelName, instructLists, components, materialEntity, instuctForMaterial, rankBom, partialBom, edittype) {
	cur_edit_type = edittype;

	var editable = (edittype > INS_PROCEDURE.NONE && edittype < INS_PROCEDURE.ORDERED);
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
		instru_func.setSheetEdited(null);
	} else {
		$("#s2b_inst_rank_bom > .select-buttons > li:eq(-1)").hide();
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
			var msg = "";
			for (var iBom in rankBom) {
				var bom_code = rankBom[iBom];
				var $hitTr = $inst_list.find("tr.jqgrow[bom_code='" + bom_code + "']");
				if ($hitTr.length > 0) {
					$hitTr.attr("rank", true);
					$hitTr.children("td[for=rank]").text("*");
				} else {
					msg += bom_code+";";
				}
			}
			// if (msg) instr_msg.warning += ("无法匹配的bom" + msg + "\n");
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
			// if (msg) instr_msg.warning += ("无法匹配的bom" + msg + "\n");
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
		open : function(){
			if (editable) {
				$container.after("<div id='inst_warning_content'></div><div id='inst_cascade_content' title='点击自动除去'></div>" +
						"<div id='inst_async_content'></div><div id='inst_loss_content'></div>");
				$("#inst_cascade_content").click(instru_func.sendCascadeRemove);
				instru_func.countLoss();
			}
			if (instruct_ws != null && this_instruction_material_id) {
				instruct_ws.send("start:"+this_instruction_material_id);
			}
		},
		close : function(){
			$container.parent().children("#inst_warning_content, #inst_async_content, #inst_cascade_content, #inst_loss_content").remove();
			instr_msg.warning = "";
			instr_msg.notice = "";
			if (instruct_ws != null && this_instruction_material_id) {
				instruct_ws.send("leave:");
			}
		},
		buttons : {
			"关闭" : function() {
				$container.dialog("close");
			}
		}
	});

	if (instr_msg.warning) {
		instru_func.setWarningMessage();		
	}
}

var instruction_bind = function($inst_list, instuctForMaterial) {

	for (var iv in instuctForMaterial) {
		var itm = instuctForMaterial[iv];
		var $targetTr = $inst_list.find("tr[bom_code='" + itm.bom_code + "']");
		if ($targetTr.length == 0) {
			var $refillTr = $("<tr class=\"ui-widget-content mismatch jqgrow ui-row-ltr\" bom_code=\"" + itm.bom_code + "\" partial_id=\"" + itm.partial_id + "\"></tr>");
			var priceHtml = "<td for='price'>unknown</td>";

			var quantityHtml = "<td for='quantity'><span>" + itm.quantity + "</span></td>";
			var $tableMain = $("#inst_list > .ui-jqgrid-btable:eq(0) > tbody"); 
			if ($tableMain.find("button.ui-icon").length > 0) {
				quantityHtml = "<td for='quantity'><span>" + itm.quantity + "</span><button class='ui-icon ui-icon-plus'></button></td>";
			}

			$refillTr.html("<td>" + itm.code + "</td><td>" 
					+ (itm.name || "(与BOM主数据不一致)") + "</td>" + quantityHtml + priceHtml
					+ "<td for='process_code'" + instru_func.getLineAttr(itm.line_id) + ">" + instru_func.getProcessCode(itm.process_code) + "</td>"
					+ "<td></td><td></td><td></td><td></td>");
			$targetTr = $refillTr;

			instr_msg.warning += "维修单零件列表中在[" + itm.bom_code + "]BOM数据中缺失。\n";
			$tableMain.append($refillTr);
		}
		if ($targetTr.length == 1) {
			var rankText = ((itm.rank && itm.rank < 9) ? (itm.rank > 1 ? "SORC-R" : "RANK") : "") 
			var $td = $targetTr.children("[for=process_code]")
				.next().text(rankText) // RANK
				.next(); // SHIP
			if (itm.ship) {
				$td.text("　√");
			}
			if (itm.quantity) {
				var $qspan = $targetTr.children("td[for='quantity']").children("span");
				if ($qspan.text() != itm.quantity) {
					instr_msg.warning += "维修单零件列表中在[" + itm.bom_code + "]BOM数量不一致。\n";
					$qspan.text(itm.quantity).addClass("mismatch");
				}
			}
			var isCancel = false;
			$td = $td.next(); // quote_job_no
			$targetTr.addClass("instructed");
			if (itm.quote_job_no) {
				$td.html("<img src='/images/sign/"+itm.quote_job_no+"'></img>").attr("sign", "q");
				if (itm.quote_adjust > 0) {
					$targetTr.removeClass("instructed").addClass("append");
				} else if (itm.quote_adjust < 0) {
					isCancel = true;
				}
			} else {
				if (itm.rank == 9) {
					$targetTr.removeClass("instructed").addClass("append");
				}
			}
			$td = $td.next(); // inline_job_no
			if (itm.inline_job_no) {
				$td.html("<img src='/images/sign/"+itm.inline_job_no+"'></img>").attr("sign", "i");
				if (itm.inline_comment) {
					$td.attr("inline_comment", itm.inline_comment);
				}
				isCancel = !isCancel;
			}
			if (isCancel && $targetTr.is(".instructed")) {
				$targetTr.addClass("cancel");
			} else {
				$targetTr.find(".ui-icon-plus").removeClass("ui-icon-plus").addClass("ui-icon-minus");
			}

			if (itm.no_priv) {
				$targetTr.attr("no_priv", itm.no_priv);
			}
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

var instr_reasons = {
	recent_reason : [],
	history_reasons : {}
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

		$("#inst_list > table tr.jqgrow").not(function(a, ele){

			var $tr = $(ele);
			if (partial_code) {
				if (!$tr.children("td:eq(0)").text().startsWith(partial_code)) {
					return false;
				}
			}
			if (rank_bom == 1) {
				if (showRankTd) {
					if (!$tr.children("td[for=rank]").text()) {
						return false;
					}
				} else {
					if (!$tr.attr("rank")) {
						return false;
					}
				}
			} else
			if (rank_bom == -1) {
				if (showRankTd) {
					if ($tr.children("td[for=rank]").text()) {
						return false;
					}
				} else {
					if ($tr.attr("rank")) {
						return false;
					}
				}
			} else
			if (rank_bom == 9) {
				if (!showRankTd) {
					if ($tr.attr("rank")) {
						return false;
					}
					if ($tr.hasClass("instructed")) {
					} else if ($tr.hasClass("append")) {
						if (!$tr.children("td[sign=q]").length) {
							return false;
						}
					} else {
						return false;
					}
				}
			}
			if (line_id) {
				var $tdProcessCode = $tr.children("td[for=process_code]")
				if (!$tdProcessCode.text() === "未设定") {
					return false;
				} else if (!$tdProcessCode.attr("ln_" + line_id)) {
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

var $cascadeTrs = null;

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
	setWarningMessage : function(){
		if ($("#inst_warning_content").length == 0) return; 
		$("#inst_warning_content").html(decodeText(instr_msg.warning))
			[0].scrollTo(0, 9999);
	},
	setSheetEdited : function($tb){
		if ($tb == null) {
			$tb = $("#inst_list > table");
		}
		$tb.each(function(idx, ele){
			var $table = $(ele);
			var tbFor = $table.attr("for");
			if (tbFor == "@") tbFor = "\\@";
			var $radio = $("#inst_page > #inst_page_" + tbFor);

			if ($table.find("tr.append, tr.cancel").length) {
				$radio.addClass("edited");
			} else {
				$radio.removeClass("edited");
			}
		});
	},
	updateItem : function(){
		var $tdQua = $(this);
		var $trItem = $tdQua.parent();

		if (cur_edit_type == INS_PROCEDURE.INLINE) {
			var iComment = $trItem.children("td[inline_comment]").attr("inline_comment");
			if (!iComment) {
				instru_func.showCommentInputer($trItem, function(){
					instru_func.updateItemForward($tdQua, $trItem);
				});
			} else {
				instru_func.updateItemForward($tdQua, $trItem);
			}
		} else {
			instru_func.updateItemForward($tdQua, $trItem);
		}
	},
	updateItemForward : function($tdQua, $trItem){
		if ($trItem.hasClass("instructed")) {
			if (!$trItem.hasClass("cancel")) {
				instru_func.updateItemPost($trItem, "cancel", function(){
					$trItem.addClass("cancel");
					$tdQua.children("button").removeClass("ui-icon-minus").addClass("ui-icon-plus");
					instru_func.setSheetEdited($trItem.closest("table"));
				});
			} else {
				instru_func.updateItemPost($trItem, "append", function(){
					$trItem.removeClass("cancel");
					$tdQua.children("button").removeClass("ui-icon-plus").addClass("ui-icon-minus");
					instru_func.setSheetEdited($trItem.closest("table"));
				});
			}
		} else {
			if (!$trItem.hasClass("append")) {
				if (cur_edit_type == INS_PROCEDURE.INLINE) {
					// 追加理由
				}
				instru_func.updateItemPost($trItem, "append", function(){
					$trItem.addClass("append");
					$tdQua.children("button").removeClass("ui-icon-plus").addClass("ui-icon-minus");
					instru_func.setSheetEdited($trItem.closest("table"));
				});
			} else {
				if ($trItem.attr("no_priv") == "1") {
					errorPop("其他在线人员追加的项目不可以取消。");
					return;
				}
				instru_func.updateItemPost($trItem, "cancel", function(){
					$trItem.removeClass("append");
					$tdQua.children("button").removeClass("ui-icon-minus").addClass("ui-icon-plus");
					instru_func.setSheetEdited($trItem.closest("table"));
				});
			}
		}	
	},
	updateItemRemote : function($tdQua, $trItem){
		if ($trItem.hasClass("instructed")) {
			if (!$trItem.hasClass("cancel")) {
				$trItem.addClass("cancel");
				$tdQua.children("button").removeClass("ui-icon-minus").addClass("ui-icon-plus");
				instru_func.setSheetEdited($trItem.closest("table"));
			} else {
				$trItem.removeClass("cancel");
				$tdQua.children("button").removeClass("ui-icon-plus").addClass("ui-icon-minus");
				instru_func.setSheetEdited($trItem.closest("table"));
			}
		} else {
			if (!$trItem.hasClass("append")) {
				$trItem.addClass("append");
				$tdQua.children("button").removeClass("ui-icon-plus").addClass("ui-icon-minus");
				instru_func.setSheetEdited($trItem.closest("table"));
			} else {
				$trItem.removeClass("append");
				$tdQua.children("button").removeClass("ui-icon-minus").addClass("ui-icon-plus");
				instru_func.setSheetEdited($trItem.closest("table"));
			}
		}	
	},
	updateItemPost : function($trs, action, success_callback){
		if ($trs.length > 0) {
			var postData = {
				"material_id" : this_instruction_material_id,
				"upd_action" : action
			}
			var idx = 0;
			$trs.each(function(idx, ele) {
				var $tr = $(ele);
				postData["item[" + idx + "].partial_id"] = $tr.attr("partial_id");
				postData["item[" + idx + "].bom_code"] = $tr.attr("bom_code");
				postData["item[" + idx + "].quantity"] = $tr.children("td[for=quantity]").children("span").text();
				if (cur_edit_type == INS_PROCEDURE.INLINE) {
					postData["item[" + idx + "].inline_comment"] = $tr.children("td[inline_comment]").attr("inline_comment");
				}
				idx++;
			});
			$.ajax({
				beforeSend : ajaxRequestType,
				async : ($trs.length == 1),
				url : 'materialPartInstruct.do?method=doUpdateItem',
				cache : false,
				data : postData,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrObj){
					instru_func.updateItemPostComplete(xhrObj, $trs, success_callback);
					$cascadeTrs = null;
					$("#inst_cascade_content").text("");
					if (action == "append" && $trs.length == 1) {
						var tbFor = $trs.closest("table").attr("for");
						if (tbFor == "@") {
							var $subSheet = $("#inst_list > table[for='" + $trs.children("td:eq(0)").text() + "']");
							if ($subSheet.length) {
								$cascadeTrs = $subSheet.find("tr.instructed, tr.append").not(".cancel");
								if ($cascadeTrs != null && $cascadeTrs.length) {
									var text = "追加的零件有以下子零件";
									$cascadeTrs.each(function(idx, ele){
										text += $(ele).children("td:eq(0)").text() + ",";
									});
									text += "已经有指示数量，是否需要取消。"
									$("#inst_cascade_content").text(text);
								}
							}
						} else {
							var $homeSheet = $("#inst_list > table[for='\\@']");
							$cascadeTrs = $homeSheet.find("tr.instructed, tr.append").not(".cancel").filter(function(idx, ele){
								return $(ele).children("td:eq(0)").text() == tbFor;
							});
							if ($cascadeTrs != null && $cascadeTrs.length) {
								var text = "追加的零件有母零件" + tbFor + "已经指示，是否需要取消。"
								$("#inst_cascade_content").text(text);
							}
						}
					}
				}
			})
		}
	},
	updateItemPostComplete : function(xhrObj, $trs, success_callback){
		var resInfo = $.parseJSON(xhrObj.responseText);
		if (resInfo.errors && resInfo.errors.length) {
			for (var idxE in resInfo.errors) {
				instr_msg.warning += resInfo.errors[idxE].errmsg + "\n";
			}
			instru_func.setWarningMessage();
		} else {
			if (typeof success_callback === "function") success_callback();
			instru_func.updateSign($trs, resInfo.job_no, resInfo.target);
			instru_func.countLoss();
		}
	},
	updateSign : function($trs, cur_job_no, target) {
		var tdCur = (target == "q" ? "td:eq(-2)" : "td:eq(-1)" );

		if (cur_job_no) {
			$trs.each(function(idx, ele){
				$(ele).children(tdCur).html("<img src=\"/images/sign/" + cur_job_no + "\">").attr("sign", target);
			})
		} else {
			$trs.each(function(idx, ele){
				$(ele).children(tdCur).html("").removeAttr("sign");
			})
		}
	},
	sendCascadeRemove : function() {
		if ($("#inst_cascade_content").text()) {
			var needComments = (cur_edit_type == INS_PROCEDURE.INLINE);

			if (needComments) {
				var lastReason = null;
	
				if (instr_reasons.recent_reason.length > 0) {
					lastReason = instr_reasons.recent_reason[0];
				}
				if (lastReason == null) {
					errorPop("请输入理由。");
					return;
				}
				$cascadeTrs.each(function(idx, ele){
					ele.setAttribute("inline_comment", lastReason);
				});
			}

			instru_func.updateItemPost($cascadeTrs, "cancel", function(){
				$cascadeTrs.each(function(idx, ele){
					var $trItem = $(ele);
					if ($trItem.hasClass("instructed")) {
						$trItem.addClass("cancel");
					} else {
						$trItem.removeClass("append");
					}
					$trItem.children("td[for='quantity']").children("button").removeClass("ui-icon-minus").addClass("ui-icon-plus");
				})
				instru_func.setSheetEdited($cascadeTrs.eq(0).closest("table"));

				$cascadeTrs = null;
				$("#inst_cascade_content").text("");
			});
		}
	},
	showCommentInputer : function($tr, onSuccess) {
		var $commentDialog = $("#instruct_comment_dialog");
		if ($commentDialog.length == 0) {
			$(document.body).append("<div id='instruct_comment_dialog'></div>");
			$commentDialog = $("#instruct_comment_dialog");
		}
		$commentDialog.html(instruction_comment);

		var $tdComment = $tr.children("td[inline_comment]");
		if ($tdComment.length) {
			$("#inst_comment_inputer").val($tdComment.attr("inline_comment"));
		} else {
			$tdComment = $tr.children("td:eq(-1)");
		}

		if (instr_reasons.recent_reason) {
			var txtHisResaons = "";
			for (var idx in instr_reasons.recent_reason) {
				var text = instr_reasons.recent_reason[idx];
				if (text.length > 8) {
					text = text.substring(0, 8) + "...";
					txtHisResaons += "<input type='button' title='" + instr_reasons.recent_reason[idx] + "' value='" + text + "'>"
				} else {
					txtHisResaons += "<input type='button' value='" + text + "'>"
				}
			}
			$commentDialog.children(".recently").html(txtHisResaons)
				.children("input").button().click(function(){
					$("#inst_comment_inputer").val(this.getAttribute("title") || this.value || "");
				});
		}

		var partial_id = $tr.attr("partial_id");
		if (instr_reasons.history_reasons[partial_id]) {
			var hisResaons = instr_reasons.history_reasons[partial_id];
			var txtHisResaons = "";
			for (var idx in hisResaons) {
				var text = hisResaons[idx];
				if (text.length > 8) {
					text = text.substring(0, 8) + "...";
					txtHisResaons += "<input type='button' title='" + hisResaons[idx] + "' value='" + text + "'>"
				} else {
					txtHisResaons += "<input type='button' value='" + text + "'>"
				}
			}
			$commentDialog.children(".history").html(txtHisResaons)
				.children("input").button().click(function(){
					$("#inst_comment_inputer").val(this.getAttribute("title") || this.value || "");
				});
		}

		$commentDialog.dialog({
			resizable : false,
			modal : true,
			width: 600,
			height: 500,
			title : "输入追加/取消理由",
			buttons : {
				"确定" : function() {
					var postComment = $("#inst_comment_inputer").val();
					if (postComment) {
						postComment = postComment.trim();
					}
					if (!postComment) {
						errorPop("请输入理由。");
						return;
					} else {
						postComment = postComment.replaceAll("<","＜").replaceAll(">","＞").replaceAll("\"","＂");
					}

					if (instr_reasons.recent_reason.indexOf(postComment) == -1) {
						instr_reasons.recent_reason.unshift(postComment);
						if (instr_reasons.recent_reason.length > 5) {
							instr_reasons.recent_reason.pop();
						}
					}
					$tdComment.attr("inline_comment", postComment);
					$commentDialog.dialog("close");
					if (typeof onSuccess === "function") {
						onSuccess();
					}
				},
				"关闭" : function() {
					$commentDialog.dialog("close");
				}
			}
		});

	},
	countLoss : function() {
		var loss = 0, lossSelf = 0;
		var $trLoss = $("#inst_list").find("tr.append");
		$trLoss.each(function(idx, ele){
			var $tr = $(ele);
			var no_priv = $tr.attr("no_priv");

			var price = $tr.find("td[for='price']").text();
			if (!price || price == "unknown" ) {
				return;
			}

			var quantity = $tr.find("td[for='quantity'] > span").text();

			var trPrice = parseFloat(price) * parseInt(quantity);
			loss += trPrice;
			if (no_priv != "1") {
				lossSelf += trPrice;
			}
		});
		if (loss) {
			$("#inst_loss_content").text("追加损金￥ " + loss.toFixed(2) + " 。" 
				+ (lossSelf ? ("本人追加损金￥ " + lossSelf.toFixed(2) + " 。") : "" ));
		} else {
			$("#inst_loss_content").text("");
		}
	},
	asynTO : 0,
	showAsycnMessage : function(item, upd_action) {
		var actionText = ("append" == upd_action ? "追加" : "取消");
		if (instru_func.asynTO) clearTimeout(instru_func.asynTO);
		instru_func.asynTO = setTimeout(function(){
			instru_func.asynTO = 0;
			$("#inst_async_content").text("");
		}, 10000);

		var $trItem = $("#instruct_dialog tr[bom_code='" + item.bom_code + "']");
		if ($trItem.length > 0) {
			var $tdQua = $trItem.children("td[for=quantity]");
			instru_func.updateItemRemote($tdQua, $trItem);
			$("#inst_async_content").text("操作者" + (item.quote_job_no || item.inline_job_no) + actionText 
				+ "了零件[" + $trItem.children("td:eq(0)").text() + "]，已经同步表示。");

			var $td = $trItem.children("[for=process_code]")
				.next().next().next();
			if (item.quote_job_no) {
				$td.html("<img src='/images/sign/"+item.quote_job_no+"'></img>").attr("sign", "q");
				$trItem.attr("no_priv", "1");
			} else {
				$td.html("");
			}
			$td = $td.next(); // inline_job_no
			if (item.inline_job_no) {
				$td.html("<img src='/images/sign/"+item.inline_job_no+"'></img>").attr("sign", "i");
				if (item.inline_comment) {
					$td.attr("inline_comment", item.inline_comment);
				}
				$trItem.attr("no_priv", "1");
			} else {
				$td.html("");
			}
		}
	}
}

var instruct_ws = null;
try {
	// 创建WebSocket  
	instruct_ws = new WebSocket(wsPath + "/instruct");
	// 收到消息时在消息框内显示  
	instruct_ws.onmessage = function(evt) {
    	var resInfo = {};
    	try {
			resInfo = $.parseJSON(evt.data);
    		if ("asycn_message" == resInfo.method) {
	   			instru_func.showAsycnMessage(resInfo.item, resInfo.upd_action);
    		} else if ("ping" == resInfo.method) {
    			instruct_ws.send("pong:"+resInfo.id + "+" + instruct_ws.readyState);
    		}
    	} catch(e) {
    	}
	};  
	// 断开时会走这个方法  
	instruct_ws.onclose = function() {   
	};  
	// 连接上时走这个方法  
	instruct_ws.onopen = function() {     
		instruct_ws.send("entach:"+$("#op_id").val());
	}; 
	} catch(e) {
}

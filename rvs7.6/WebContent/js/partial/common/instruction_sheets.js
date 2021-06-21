var instruction_filter = '<table id="inst_filter" class="condform"><tbody><tr><td class="ui-state-default td-title">零件代码</td><td class="td-content" style="width: 128px;"><input type="text" id="inst_partial_code" alt="零件代码" class="ui-widget-content"></td>' +
'<td class="ui-state-default td-title">Rank BOM</td><td class="td-content"><select id="inst_rank_bom" alt="Rank BOM"><option value="">(全部)</option><option value="1">BOM</option><option value="-1">非 BOM</option></select></td>' +
'<td class="ui-state-default td-title">定位工程</td><td class="td-content"><select id="inst_line_id" alt="定位工程"><option value="">(全部)</option><option value="00000000012">分解</option><option value="00000000013">NS</option><option value="00000000014">总组</option></select></td>' +
'<td class="ui-state-default"><input type="button" value="清除过滤"></td>' +
'</tr></tbody></table>';

var instruction_construct = function(modelName, instructLists) {
	var $ht = $("<div id='inst_sheet'></div>");
	var $htPage = $("<div id='inst_page'></div>");
	var $htList = $("<div id='inst_list' class='ui-widget-content ui-jqgrid'></div>");
	var htHead = "<table id='inst_head' cellspacing='0' border='0'>" +
			"<thead><tr><th class='ui-state-default'>零件代码</th><th class='ui-state-default'>说明</th><th class='ui-state-default'>BOM 代码</th>" +
			"<th class='ui-state-default'>Rank BOM</th><th class='ui-state-default'>指示数</th><th class='ui-state-default'>定位</th></tr></thead></table>";
	for (var pageName in instructLists) {
		var instructList = instructLists[pageName];
		$htPage.append("<input type='radio' class='inst_page' name='inst_page' id='inst_page_" + pageName + "'></input><label for='inst_page_" + pageName + "'>" 
			+ (pageName === "@" ? modelName : pageName) + "</label>");
		var htTable = "<table class='ui-jqgrid-btable' for='" + pageName + "' cellspacing='0' border='0'><tbody><tr class='tr-eq0'>";
		htTable += "<td></td><td></td><td></td><td for='rank'></td><td for='quantity'></td><td for='process_code'></td></tr>";

		for (var iIl in instructList) {
			var partial = instructList[iIl];
			htTable += "<tr class='ui-widget-content jqgrow ui-row-ltr'" + (partial.level ? " level=" + partial.level : "") + ">"
				+ "<td>" + partial.code + "</td><td>" 
				+ (partial.name || "")  + "</td><td>" + partial.bom_code
				+ "</td><td for='rank'></td><td for='quantity'>" + (partial.quantity || "unknown") 
				+ "</td><td for='process_code'" + instru_func.getLineAttr(partial.line_id) + ">" + instru_func.getProcessCode(partial.process_code) + "</td></tr>";
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
	$htList.children("table").find("tr.tr-eq0").each(function(idx, ele){
		var $tr = $(ele);
		$tr.find("td[for=rank]").css("width", "3em");
		$tr.find("td[for=quantity]").css("width", "4em");
		$tr.find("td[for=process_code]").css("width", "4em");
		$tr.find("td:eq(0)").css("width", "8em");
		$tr.find("td:eq(1)").css("width", "42em"); // 36
	});
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

var instruction_show = function($container, modelName, instructLists, components, materialEntity, quotationList, edittype) {
	var editable = edittype > 0;
	
	var $instruction_html = instruction_construct(modelName, instructLists);

	$container.html($instruction_html);
	var $inst_page  = $container.find("#inst_page");
	var $inst_list  = $container.find("#inst_list");
	$inst_page.children("input:eq(0)").trigger("click");

	setFilter_func($container.find("#inst_filter"));

	if (components && components.length > 0) {
		var $baseList = $inst_list.find("table:eq(0) .jqgrow");
		for (var iCom in components) {
			var component_code = components[iCom];
			$inst_page.find("#inst_page_" + component_code).next().addClass("component");
			for (var idxTr in $baseList) {
				var tr = $baseList[idxTr];
				if (tr.firstChild.innerText === component_code) {
					tr.firstChild.className = "component";
					break;
				}
			}
		}
	}

	if (materialEntity == null || !materialEntity.level ) {
		$("#inst_rank_bom").parent().hide()
			.prev().hide();
	}
	

	$container.dialog({
	    resizable : false,
		modal : true,
		width: 1024,
		height: 640,
		title : modelName + " 的工作指示单",
		buttons : {
			"关闭" : function() {
				$(this).dialog("close");
			}
		}
	});
}

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

		console.log(evt.type + ">>" + evt.target);

	var noFilter = true;
	var partial_code = $("#inst_partial_code").val().toUpperCase(); if (partial_code) noFilter = false;
	var rank_bom = $("#inst_rank_bom").val(); if (rank_bom) noFilter = false;
	var line_id = $("#inst_line_id").val(); if (line_id) noFilter = false;

	$("#inst_list > table tr.jqgrow").removeClass("hide_by_filter");
	if (noFilter) {
		$("#inst_page .inst_page + label").removeAttr("cnt");
	} else {
		$("#inst_list > table tr.jqgrow").filter(function(a, ele){
			var $tr = $(ele);
			if (partial_code) {
				if ($tr.children("td:eq(0)").text().startsWith(partial_code)) {
					return false;
				}
			}
			if (rank_bom == 1) {
				if ($tr.children("td[for=rank]").text()) {
					return false;
				}
			}
			if (rank_bom == -1) {
				if (!$tr.children("td[for=rank]").text()) {
					return false;
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
				ret += "<br>" + process_array;
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
	}
}
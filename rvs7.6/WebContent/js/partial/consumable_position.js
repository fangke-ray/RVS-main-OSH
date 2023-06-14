$(function() {
	var registPos = {};
	var thisId = null;
	var thisCode = null;

	var getPositions = function(){
		var row = $("#consumable_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#consumable_list").getRowData(row);
		thisId = rowData.partial_id;
		thisCode = rowData.code;
		var postData = {
			"partial_id" : rowData.partial_id
		}
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'consumable_list.do?method=getPositions',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : getPositions_handleComplete
		});
	}

	var getPositions_handleComplete = function(xhrobj, textStatus){
		var resInfo = JSON.parse(xhrobj.responseText);
		if(resInfo.errors && resInfo.errors.length) {
			treatBackMessages(null, resInfo.errors);
		} else {
			showPositions(resInfo.listSetting);
		}
	}

	var filterModelName = function(e){
		var findix = $("#txtModelFilter").val();
		if (!findix) return;
		findix = findix.toUpperCase();

		if (e.keyCode == 13) {
			if (typeof window.find == "function") {
				window.find(findix);
			} else {
				var hit = false;
				var $hitTd = $("#consumable_position_dialog td.ui-state-default[rowspan]").filter(function(idx, ele){
					if (hit) return false;
					if (ele.innerText.indexOf(findix) == 0) {
						return hit = true;
					}
				}).eq(0);
				if ($hitTd.length == 0) return;
				var posTop = $hitTd.position().top;
				$("#consumable_position_dialog")[0].scrollTop = $("#consumable_position_dialog")[0].scrollTop + posTop - 22;
			}
		}
	}

	var showPositions = function(listSetting){
		var bodyHtml = "";
		var model = "-";
		var position_id = "";
		var for_consumable = 0;
		registPos = {};

		for (var d in listSetting) {
			var pd = listSetting[d];
			if (pd.model_name != model) {
				if (for_consumable == 1) {
					bodyHtml += "</td></tr><tr><td>BOM无关联";
				}
				if (bodyHtml.length) {
					bodyHtml += "</td></tr>";
				}
				model = pd.model_name;
				bodyHtml += "<tr model_id='" + pd.model_id + "'><td class='ui-state-default' rowspan=2>" + pd.model_name + "</td><td>";
				position_id = "";
				for_consumable = 0;
			}
			if (pd.quantity) {
				for_consumable = 1;
			} else {
				if (for_consumable == 0) bodyHtml+= "<input type='text' readonly><input type='number'>";
				if (for_consumable >= 0) {
					bodyHtml+= "</td></tr><tr><td>";
					position_id = "";
				}
				for_consumable = -1;
			}
			if (pd.position_id && pd.position_id != 0) {
				registPos[pd.position_id] = pd.process_code;
			}
			if (pd.position_id != position_id) {
				if (position_id != "") {
					bodyHtml += "<br>";
				}
				if (pd.quantity) {
					bodyHtml+= "<input type='text' org_position_id='" + pd.position_id + "' position_id='" + pd.position_id + 
					"' value='" + pd.process_code + "' readonly><input type='number' value='" + pd.quantity + "'>";
				} else {
					bodyHtml += (pd.process_code || '未定工位') + ":";
				}
				position_id = pd.position_id;
			} else {
				if (!pd.quantity) {
					bodyHtml += " | ";
				}
			}
			if (pd.bom_quantity > 0) bodyHtml += pd.bom_quantity;
		}

		var $dialog = $("#consumable_position_dialog");
		if ($dialog.length == 0) {
			$("body").append("<div id='consumable_position_dialog'>");
			$dialog = $("#consumable_position_dialog");
			$dialog.html("<table class='subform'><thead><tr><th  class='ui-widget-header' style='width: 24em;'>型号</th><th class='ui-widget-header'>工位及指示数</th></tr></thead><tbody></tbody></table>");
			$dialog.find("tbody").on("click", "input[type=text]", choosePosition);
			$dialog.find("tbody").on("dblclick", ".ui-state-default[rowspan]", function(){
				$(this).next().append("<br><input type='text' readonly><input type='number'>");
			});
			$dialog.find("tbody").on("change", "input[type=number]", function(){
				var $pos = $(this).prev();
				if (!$pos.attr("updated") && $pos.val()) {
					$pos.attr("updated", "upd");
				}
			});
		}

		$dialog.find("tbody").html(bodyHtml);

		$dialog.dialog({
			resizable : false,
			width : 720,
			height : 420,
			modal : true,
			title : thisCode + " 定位及指示用量",
			open : function(){
				var $thisDialog = $(this);
				if ($thisDialog.prev().children("input:text").length) {
					$('#txtModelFilter').val("");
					return;
				}

				var $modelFilter = $("<span style='margin-left:3em;font-size:12px;'>型号查询: </span><input type='text' id='txtModelFilter'>");
				$thisDialog.prev().children("span:eq(0)")
					.after($modelFilter);
				$("#txtModelFilter").on("keyup", filterModelName);
				$("#txtModelFilter").on("click", function(){
					this.focus();
					this.select();
				});
			},

			buttons : {
				"确认" : function() {
					var errorMessage = "";
					var postData = {partial_id : thisId};
					$dialog.find("input[type='text'][updated]").each(function(idx, ele){
						var $posInputer = $(ele);
						var updated = $posInputer.attr("updated");
						var quantity = $posInputer.next().val();

						if (updated != 'rmv') {
							if (!quantity || isNaN(quantity)) {
								errorMessage += "请输入用于" + $posInputer.closest("tr").children(":eq(0)").text() 
									+ "，" + $posInputer.val() + "工位的数量。<br>";
							} else {
								quantity = parseInt(quantity.trim());
								if (quantity < 1) {
									errorMessage += "请为用于" + $posInputer.closest("tr").children(":eq(0)").text() 
										+ "，" + $posInputer.val() + "工位的数量输入一个大于0的整数。<br>";
								}
							}
						} else {
							quantity = 0;
						}
						postData["position[" + idx + "].model_id"] = $posInputer.closest("tr").attr("model_id");
						postData["position[" + idx + "].position_id"] = $posInputer.attr("position_id");
						postData["position[" + idx + "].quantity"] = quantity;
						postData["position[" + idx + "].updated"] = updated;
						if ($posInputer.attr("org_position_id")) {
							postData["position[" + idx + "].org_position_id"] = $posInputer.attr("org_position_id");
						}
					});
					if (errorMessage.length) {
						errorPop(errorMessage);
					} else {
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : 'consumable_list.do?method=doSetPositions',
							cache : false,
							data : postData,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(xhrobj, textStatus){
								var resInfo = JSON.parse(xhrobj.responseText);
								if(resInfo.errors && resInfo.errors.length) {
									treatBackMessages(null, resInfo.errors);
								} else {
									$dialog.dialog("close");
								}
							}
						});
					}
				},
				"取消" : function() {
					$dialog.dialog("close");
				}
			}
		});
	}

	var $handler = null;

	var choosePosition = function(evh){
		var $dialog = $("#positions_dialog");
		$handler = $(evh.target);

		if ($dialog.length == 0) {
			$("body").append("<div id='positions_dialog'>");
			$dialog = $("#positions_dialog");
			$dialog.on("click", "tr", function(evs){
				var $target = $(evs.target);
				if (!$target.is("tr")) {
					$target = $target.closest("tr");
				}
				var id = $target.children(".referId").text();
				var code = $target.children().last().text();

				$handler.val(code).attr("position_id", id);
				if ($handler.attr("org_position_id") == undefined) {
					$handler.attr("updated", "ins");
				} else {
					$handler.attr("updated", "upd");
				}
				$dialog.dialog("close");
			});
		}
		$dialog.html($("#pReferChooser").html());
		for (var pos_id in registPos) {
			$dialog.find("td.referId:contains(" + fillZero(pos_id, 11) + ")")
				.parent().addClass("ui-state-highlight");
		}
		$dialog.dialog({
			resizable : false,
			height : 420,
			modal : true,
			open : function(){
				var nowScrollTop = $dialog[0].scrollTop;
				var $highlights = $dialog.find("tr.ui-state-highlight");
				if ($highlights.length > 0) {
					$dialog[0].scrollTop = nowScrollTop + $highlights.eq(0).position().top - 24;
				} else {
					$dialog[0].scrollTop = 0;
				}
			},
			title : "选择定位工位",
			buttons : {
				"删除" : function() {
					$handler.val("").removeAttr("position_id");
					if ($handler.attr("org_position_id") == undefined) {
						$handler.removeAttr("updated");
					} else {
						$handler.attr("updated", "rmv");
					}
					$handler.next().val("");
					$dialog.dialog("close");
				},
				"关闭" : function() {
					$dialog.dialog("close");
				}
			}
		});
	};

	$("#position_button").hide().bind("click", getPositions);
});
var ca_edit_callback = null;

var consumable_application_edit = function(assert_key, callback)
{
	ca_edit_callback = callback;
	// 取得当前工程的未法放申请单
	var $editor = $("#consumable_application_editor");
	if ($editor.length == 0) {
		$("body").append("<div id='consumable_application_editor'/>");
		$editor = $("#consumable_application_editor");
	}
	$editor.hide();

	// autoprogresstime
	var now = new Date();
	if (now.getHours() == 13 && now.getMinutes() < 5) {
		errorPop("消耗品申请单自动发送时间，请稍候处理。");
		return;
	}

	var postLoadData = {consumable_application_key: assert_key};
	$editor.load("consumable_apply.do?method=edit", postLoadData, 
		function(responseText, textStatus, XMLHttpRequest) {

		var key = $("#edit_consumable_application_key").val(); // tqeus
		ca_evts.getConsumableApplicationDetail(key, 
		function(){
			$("#application_sheet_form").on("click", ".ca_add_button", function(){
				var apply_method = $(this).attr("apply_method");
				var material_id = $("#application_sheet_material").attr("material_id");
				// 选择使用消耗品的维修对象
				if (apply_method == 1 && !material_id) {
					ca_evts.selectMaterial();
				} else {
					ca_evts.addConsumable(apply_method);
				}
			});
			$("#chosser_4_popular_item_list tbody").on("click", "tr", function(){
				ca_evts.partialAutoComplete($(this).children("td:eq(0)").text());
			});

			$("#ca_add_item_code").bind("keyup", function(e){
				var kcode =e.keyCode;
		
				if (this.value.length >= 6){
					if( kcode > 105 || kcode < 48){
						return;
					}
					$("#ca_add_item_code").next().remove();
					ca_evts.partialAutoComplete();
				}
			});

			$("#add_item_button").click(ca_evts.addItem);

			$("#application_sheet_form .ui-button").button();

			$("#ca_edit_button").click(function(){ca_evts.editComplete(false)});
			$("#ca_submit_button").click(function(){ca_evts.editComplete(true)});
			$("#ca_close_button").click(function(){$editor.dialog("close")});

			$("#application_sheet_form").on("click", ".ca_remove_button", function() {
				$tr = $(this).parents("tr");
				if ($tr.attr("state") == "new") {
					$tr.remove();
				} else {
					$tr.attr("state", "delete");
				}
				ca_evts.sumPrice.apply(this);
			});

			$editor.dialog({
				title : "消耗品申请单",
				modal : true,
				width: 960,
				height : 640,
				resizable : false
			});

			$("#ca_add_order_quantity_with_measure").buttonset();

			$("#ca_add_order_quantity").change(ca_evts.getTotalPrice);
			$("#ca_add_order_quantity_with_measure input:radio").click(ca_evts.getTotalPrice);
		});

	});
};

var ca_evts = {
	selectMaterial : function () {
		$("#chosser_4_material_list").parent().show().next().show();
		$("#chosser_4_partial").hide();
		$("#chosser_4_application_sheet").show();
		$("#application_sheet_content").css("pointer-events", "none")
			.next().css("pointer-events", "none")
			.find("input:button").disable();
	},
	addConsumable : function (apply_method) {
		$("#chosser_4_material_list").parent().hide().next().hide();
		$("#chosser_4_partial").show();
		$("#chosser_4_application_sheet").show();
		$("#ca_add_item_id").val("");
		$("#ca_add_item_type").val("").attr("apply_method", apply_method);
		$("#ca_add_item_code").val("").focus();
		$("#ca_add_item_decription").text("");
		$("#ca_add_item_available_inventory").text("");
		$("#ca_add_unit_price").text("");
		$("#ca_add_total_price").text("");

		$("#ca_add_order_quantity_input").show();
		$("#ca_add_order_quantity_with_measure").hide();
		$("#ca_add_order_quantity_inpack").hide();
		$("#ca_add_order_content").val(1);
		$("#ca_add_order_quantity").val("");

		$("#add_item_button").disable();
		$("#application_sheet_content").css("pointer-events", "none")
			.next().css("pointer-events", "none")
			.find("input:button").disable();

	},
	closeChooser : function () {
		$("#chosser_4_application_sheet").hide();
		$("#application_sheet_content").css("pointer-events", "all")
			.next().css("pointer-events", "all")
			.find("input:button").enable();
	},
	getConsumableApplicationDetail : function (consumable_application_key, callback) {
		// 取得现有数据
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'consumable_apply.do?method=editInit',
			cache : false,
			data : {consumable_application_key:consumable_application_key},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj){
				var resInfo = $.parseJSON(xhrobj.responseText);
				if (resInfo.errors.length == 0) {
					// 维修对象
					var performanceList = resInfo.performanceList;
					var performanceTbody = "";
					for (var iPt = 0; iPt < performanceList.length; ) {
						var performanceMaterial = performanceList[iPt];
						iPt++;
						if (performanceMaterial.material_id == "0000000000X") {
							performanceTbody += "<tr material_id='00000000000' class='breaking c_material'>"
								+ "<td colspan='6'>单元维修无法选择维修对象，请在理由里填入。</td></tr>";
						} else if (performanceMaterial.material_id == "0000000000Y") {
							performanceTbody += "<tr material_id='00000000000' class='breaking c_material'>"
								+ "<td colspan='6'>当前工程无法选择维修对象，请在理由里填入。</td></tr>";
						} else {
							performanceTbody += "<tr material_id='"+performanceMaterial.material_id+"'" +
									((performanceMaterial.operate_result == 3) ? " class='breaking'" : "") + ">"
								+ "<td>"+iPt+"</td><td>"+(performanceMaterial.sorc_no || "")+"</td>"
								+ "<td>"+performanceMaterial.model_name+"</td><td>"+performanceMaterial.serial_no+"</td>"
								+ "<td>"+(performanceMaterial.scheduled_date || "")+"</td>"
								+ "<td>"+(performanceMaterial.level || "")+"</td></tr>"
						}
					}

					$("#chosser_4_material_list tbody").html(performanceTbody);
					$("#chosser_4_material_list tbody tr").click(function(){
						$("#chosser_4_material_list tbody tr").removeClass("c_material");
						$(this).addClass("c_material");
					});

					$("#materialSelector").click(function(){
						if ($("#chosser_4_material_list tbody tr.c_material").length == 0
							|| !$("#exchange_reason").val()) {
							errorPop("请选择使用修理单号和领用理由", $("#exchange_reason"));
						} else {
							var $material = $("#chosser_4_material_list tbody tr.c_material");
							$application_sheet_material = $("#application_sheet_material");
							$application_sheet_material.attr("material_id", $material.attr("material_id"));
							$application_sheet_material.children("td:eq(1)").text($material.children("td:eq(1)").text());
							$application_sheet_material.find("td:eq(2) label").text($("#exchange_reason").val());

							ca_evts.addConsumable(1);
						}
					});

					// 常用消耗品
					var popularItemList = resInfo.popularItems;
					$("#chosser_4_popular_item_list tbody").html(popularItemList);

					// 目前详细信息
					var applicationDetail = resInfo.applicationDetail;
					if (applicationDetail && applicationDetail.length > 0) {

					var $exchange_sheet = $("#e_exchange_sheet").detach();
					var $assistant_sheet = $("#e_assistant_sheet").detach();
					var $domestic_sheet = $("#e_domestic_sheet").detach();
					var $screw_sheet = $("#e_screw_sheet").detach();

					for (var iDetail in applicationDetail) {
						var partialApply = applicationDetail[iDetail];
						var $add_target = null;
						var type = partialApply.type;
						if (partialApply.apply_method  == 1) {
							$add_target = $exchange_sheet;
						} else if (type == 8) {
							$add_target = $assistant_sheet;
						} else if (type == 9) {
							$add_target = $domestic_sheet;
						} else {
							$add_target = $screw_sheet;
						}
						var petitioner_id = partialApply.petitioner_id;
						var $tdString = $("<tr partial_id='" + partialApply.partial_id + 
									(petitioner_id == null ? "" : "' petitioner_id='" + petitioner_id) + "'>" +
									"<td class='td-content'>" +
										(petitioner_id == null ? "他人" : "<input type='button' class='ca_remove_button ui-button' value='-'></input>") +
									"</td><td class='td-content'><label>" + partialApply.code + 
									"</label></td><td class='td-content'><label>" + partialApply.partial_name + 
									"</label></td><td class='td-content'>" +
									(petitioner_id == null ? ca_evts.getQuantityLabel(partialApply.apply_quantity, partialApply.pack_method, partialApply.unit_name, partialApply.content, partialApply.available_inventory)
									 : ca_evts.getQuantityOfData(partialApply.apply_quantity, partialApply.pack_method, partialApply.unit_name, partialApply.content, partialApply.available_inventory)
									) +
									"</td><td class='td-content' unit_price='" + partialApply.price + "'>" +
									"</td></tr>");
						$add_target.append($tdString);
					}

					$("#application_sheet_content").append($exchange_sheet).append($screw_sheet).append($assistant_sheet).append($domestic_sheet)
					.find(".ui-button").button().click(function(){
						$(this).parents("tr[partial_id]").attr("state", "delete");
					})
					.end().find(".edit_a_apply_quantiy").bind("change", function(){
						$(this).parents("tr").attr("state", "edit");
						ca_evts.sumPrice.apply(this);
					});
					}
					if (resInfo.supplyOrderPrivacy) {
						$("#ca_edit_button").attr("supplyOrderPrivacy", resInfo.supplyOrderPrivacy);
					}

					if (performanceList.length == 0 && $("#e_exchange_sheet tr[partial_id]").length == 0) {
						$("#e_exchange_sheet").hide();
					}

					ca_evts.sumPrice();

					if (callback) callback();
				}
			}
		});
	},
	partialAutoComplete : function(partial_id){
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'consumable_apply.do?method=getPartialForEdit',
			cache : false,
			data : {
				code: partial_id ? "" : $("#ca_add_item_code").val(), 
				partial_id : partial_id
			},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj){
				var resInfo = $.parseJSON(xhrobj.responseText);
				if (resInfo.errors.length == 0) {
					if (resInfo.applicationDetail) {
						$("#ca_add_item_id").val(resInfo.applicationDetail.partial_id);
						$("#ca_add_item_type").val(resInfo.applicationDetail.type);
						$("#ca_add_item_code").val(resInfo.applicationDetail.code).blur();
						$("#ca_add_item_decription").text(resInfo.applicationDetail.partial_name);
						$("#ca_add_item_available_inventory").text(resInfo.applicationDetail.available_inventory);
						$("#ca_add_unit_price").text(resInfo.applicationDetail.price || "");
						$("#ca_add_order_quantity").val("");
						$("#ca_add_total_price").text("");
						$("#add_item_button").enable();
						var item_content = resInfo.applicationDetail.content;
						if (item_content) {
							if (item_content == 1) {
								$("#ca_add_order_quantity_input").hide();
								$("#ca_add_order_quantity_with_measure").hide();
								$("#ca_add_order_quantity_inpack").show();
							} else {
								$("#ca_add_order_quantity_input").show();
								$("#ca_add_order_quantity_with_measure").show();
								$("#with_measure_y")//.attr("checked","checked")
									.next().find("span").text(resInfo.applicationDetail.unit_name || "包")
									.end().trigger("click");
								$("#ca_add_order_quantity_inpack").hide();
							}
							$("#ca_add_order_content").val(item_content);
						} else {
							$("#ca_add_order_quantity_input").show();
							$("#ca_add_order_quantity_with_measure").hide();
							$("#ca_add_order_quantity_inpack").hide();
							$("#ca_add_order_content").val(1);
						}
					}
				}
			}
		});
	},
	addItem : function(){
		var partial_id = $("#ca_add_item_id").val();
		if (!partial_id) {
			errorPop("请选择正确的消耗品代码。" , $("#ca_add_item_code"));
			return;
		}

		if ($("#ca_add_item_type").attr("apply_method") == 1) {
			// 在线维修消耗时，清除其它类别，形成新订单
			$("#e_screw_sheet, #e_assistant_sheet, #e_domestic_sheet").find(".ca_add_button").remove()
			.end().find("tr[partial_id]").remove(); 
			$("#edit_consumable_application_key").val("")
				.next().text("(新申请单)");
		}

		var $existed = $("#application_sheet_content tr[partial_id="+partial_id+"][petitioner_id]");

		if ($existed.length > 0) {
			errorPop("该消耗品已有申请，如有需要请修改其申请量。" , $existed.find(".edit_a_apply_quantiy"));
			if ($existed.attr("state") == "delete") {

				$existed.attr("state", "edit");

				var $add_target = null;
				var type = $("#ca_add_item_type").val();
				if ($("#ca_add_item_type").attr("apply_method") == 1) {
					$add_target = $("#e_exchange_sheet");
				} else if (type == 8) {
					$add_target = $("#e_assistant_sheet");
				} else if (type == 9) {
					$add_target = $("#e_domestic_sheet");
				} else {
					$add_target = $("#e_screw_sheet");
				}

				// 移动
				var $now_del_table = $existed.parents("#application_sheet_content .condform");
				if ($add_target[0].id != $now_del_table[0].id) {
					$add_target.append($existed.remove());
				}
			}

			ca_evts.closeChooser();
			return;
		}

		var quantity = 0;
		var available_inventory = parseInt($("#ca_add_item_available_inventory").text());
		if ($("#ca_add_order_quantity_inpack").is(":hidden")) {
			var $quantity_with_measure = $("#ca_add_order_quantity_with_measure");
			var $choosed_measure = $quantity_with_measure.find("input:checked");

			var quantity_input = ca_evts.checkQuantityInput(
				$("#ca_add_order_quantity"), 
				!$quantity_with_measure.is(":hidden"), 
				$("#ca_add_order_content").val(), 
				$choosed_measure, 
				available_inventory);
			if (quantity_input == null) {
				return;
			}

			quantity = quantity_input;
		} else {
			quantity = 1;
		}
		var $add_target = null;
		var type = $("#ca_add_item_type").val();
		if ($("#ca_add_item_type").attr("apply_method") == 1) {
			$add_target = $("#e_exchange_sheet");
		} else if (type == 8) {
			$add_target = $("#e_assistant_sheet");
		} else if (type == 9) {
			$add_target = $("#e_domestic_sheet");
		} else {
			$add_target = $("#e_screw_sheet");
		}
		var $tdString = $("<tr state='new' partial_id='" + partial_id + "' petitioner_id='me'>" +
					"<td class='td-content'><input type='button' class='ca_remove_button ui-button' value='-'></input></td>" +
					"<td class='td-content'><label>" + $("#ca_add_item_code").val() + 
					"</label></td><td class='td-content'><label>" + $("#ca_add_item_decription").text() + 
					"</label></td><td class='td-content'>" +
					ca_evts.getQuantity(quantity_input, available_inventory) + 
					"</td><td class='td-content' unit_price='" + $("#ca_add_unit_price").text()
					+ "'></td></tr>");
		$tdString.find(".ui-button").button().click(function(){
			$(this).parents("tr[partial_id]").attr("state", "delete");
		});
		$tdString.find(".edit_a_apply_quantiy")
			.attr("available_inventory", available_inventory)
			.change(ca_evts.sumPrice);
		$add_target.append($tdString);
		$tdString.find(".edit_a_apply_quantiy").trigger("change");
		ca_evts.closeChooser();
	},
	getQuantity : function(quantity, available_inventory) {
		if (available_inventory == null) available_inventory = 9999999;
		if ($("#ca_add_order_quantity_inpack").is(":hidden")) {
			if ($("#ca_add_order_quantity_with_measure").is(":hidden")) {
				return "<input type='number' name='supply_quantiy' value='" + quantity + "' class='edit_a_apply_quantiy' available_inventory='"+ available_inventory + "'></input>";
			} else {
				var $choosed_measure = $("#ca_add_order_quantity_with_measure input:checked");
				var packsage = $choosed_measure.val();
				var unit_name = $choosed_measure.next().find("span").text();
				return "<input type='number' name='supply_quantiy' value='" + quantity + "' class='edit_a_apply_quantiy' available_inventory='"+ available_inventory + "'></input>" +
						"<span " + (packsage == 0 ? ("inpack=" + $("#ca_add_order_content").val()) : "" ) + ">" + unit_name + "</span>";
			}
		} else {
			return "<label class='inpack_q'>分装取用</label>"
		}
	},
	getQuantityOfData : function(quantity, pack_method, unit_name, content, available_inventory) {
		if (available_inventory == null) available_inventory = 9999999;
		if (content != 1) {
			if (pack_method == 1 || !content) {
				return "<input type='number' name='supply_quantiy' value='" + quantity + "' class='edit_a_apply_quantiy' available_inventory='"+ available_inventory + "'></input>";
			} else {
				var $choosed_measure = $("#ca_add_order_quantity_with_measure input:checked");
				var measured_quantity = quantity;
				if (pack_method == 1) { unit_name = "个"; } else {measured_quantity /= content}
				return "<input type='number' name='supply_quantiy' value='" + measured_quantity + "' class='edit_a_apply_quantiy' available_inventory='"+ available_inventory + "'></input>" +
						"<span " + (pack_method == 0 ? ("inpack=" + content) : "" ) + ">" + unit_name + "</span>";
			}
		} else {
			return "<label class='inpack_q'>分装取用</label>"
		}
	},
	getQuantityLabel : function(quantity, pack_method, unit_name, content) {
		var input;
		
		if(content == 1){
			input = "<span class='readonly_apply_quantiy' quantity='" + 1 + "'>分装领用</span>";
			
		}else if(pack_method == 0){//包装
			input = "<span class='readonly_apply_quantiy' quantity='" + quantity + "'>" + parseInt(quantity / content) + " " + unit_name + "</span>";
		}else{//单品
			input = "<span class='readonly_apply_quantiy' quantity='" + quantity + "'>" + quantity + "</span>";
		}
		return input;
	},
	editComplete : function(doSubmit) {
		var material_id = $("#application_sheet_material").attr("material_id");
		var hasExchange = ($("#e_exchange_sheet tr[partial_id]").length > 0);
		if ($("#application_sheet_form tr[partial_id]").length == 0) {
			errorPop("申请单没有任何申领的消耗品。");
			return;
		}
		if (doSubmit == true) {
			if (!material_id && 
				hasExchange) {
				errorPop("即时领用消耗品时，必须填写对应维修对象和原因。");
				return;
			}
			if (!$("#ca_edit_button").attr("supplyOrderPrivacy") && !hasExchange) {
				errorPop("你现在无法为工程领用定点补充消耗品，但可以记下你需要的消耗品并保存。");
				return;
			}
		} else {
			if (hasExchange) {
				errorPop("即时领用必须马上发送申请。");
				return;
			}
		}

		var checkDetailInput = true;
		$("#application_sheet_content").find("input[type=number]").each(function(idx, ele){
			if (!checkDetailInput) return;

			var $ele = $(ele);
			if ($ele.closest("tr[partial_id]").attr("state") == "delete") return;
			var inpackContent = $ele.next("span").attr("inpack");
			var ret = ca_evts.checkQuantityInput($ele, inpackContent, inpackContent);
			if (ret == null) checkDetailInput = false;
		});
		if (!checkDetailInput) return;

		if (hasExchange) {
			var otherOfEx = $("#application_sheet_content table").not("#e_exchange_sheet").find("tr[partial_id]").length > 0;
			if (otherOfEx) {
				warningConfirm("即时领用消耗品现在不可以与定点补充消耗品同时申请，将会单独提出申请单。",
					function(){
						ca_evts.postEdit(hasExchange, doSubmit, material_id);
					});
				return;			
			}
		}

		ca_evts.postEdit(hasExchange, doSubmit, material_id);
	},
	postEdit : function(hasExchange, doSubmit, material_id){
		var postData = {
			consumable_application_key : $("#edit_consumable_application_key").val(),
			apply_reason : $("#application_sheet_material td[colspan] label").text(),
			flg : hasExchange ? "ex" : doSubmit
		};
		var $collection = $("#application_sheet_form tr[partial_id]");
		if (hasExchange) {
			postData.material_id = material_id;
			$collection = $("#e_exchange_sheet").find("tr[partial_id]")
		}
		var iPdetail = 0;
		$collection.each(function(){
			$tr = $(this);
			var state = $tr.attr("state");
			if (!state || state=="nochange") {
				return;
			}
			var partial_id = $tr.attr("partial_id");
			var petitioner_id = $tr.attr("petitioner_id");
			var apply_method = ($tr.parents("#e_exchange_sheet").length > 0 ? 1 : 2 );
			var $inpack = $tr.find("span[inpack]");
			var pack_method = 0;
			var apply_quantity = 0;
			if ($inpack.length > 0) {
				pack_method = 0;
				apply_quantity = $tr.find(".edit_a_apply_quantiy").val() * parseInt($inpack.attr("inpack"));
			} else {
				pack_method = 1;
				apply_quantity = $tr.find(".edit_a_apply_quantiy").val() || 1;
			}

			postData["consumable_application.partial_id["+ iPdetail +"]"] = partial_id;
			postData["consumable_application.petitioner_id["+ iPdetail +"]"] = petitioner_id;
			postData["consumable_application.apply_method["+ iPdetail +"]"] = apply_method;
			postData["consumable_application.apply_quantity["+ iPdetail +"]"] = apply_quantity;
			postData["consumable_application.pack_method["+ iPdetail +"]"] = pack_method;
			postData["consumable_application.flg["+ iPdetail +"]"] = state;

			iPdetail++;
		});

		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'consumable_apply.do?method=doEdit',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj){
				var resInfo = $.parseJSON(xhrobj.responseText);
				if (resInfo.errors.length == 0) {
					$("#consumable_application_editor").dialog("close");
					if (ca_edit_callback) ca_edit_callback();
				} else {
		            // 共通出错信息框
		            treatBackMessages(null, resInfo.errors);
				}
			}
		});
	},
	getTotalPrice : function () {
		var num = $("#ca_add_order_quantity").val();
		if (isNaN(num)) {
			$("#ca_add_total_price").text("");
		} else {
			var unit_price = $("#ca_add_unit_price").text();
			if (unit_price) {
				var nums = parseInt(num);
				if ($("#ca_add_order_quantity_with_measure").is(":visible")) {
					var measure = $("#ca_add_order_quantity_with_measure input:checked").val();
					if (measure == "0") {
						nums = parseInt(num) * parseInt($("#ca_add_order_content").val());
					}
				}
				$("#ca_add_total_price").text(" x " + nums + " = " +
					(parseFloat(unit_price) * nums).toFixed(2));
			} else {
				$("#ca_add_total_price").text("");
			}
		}
	},
	sumPrice : function () {
		var $targets = null;
		if (this.tagName) {
			$targets = $(this);
		} else {
			$targets = $("#application_sheet_content").find(".edit_a_apply_quantiy, .readonly_apply_quantiy");
		}

		$targets.each(function(i, ele) {
			var $count = $(ele);
			var count = $count.val();
			var inpack = 1;
			if ($count.hasClass("edit_a_apply_quantiy")) {
				inpack = $count.next("span[inpack]").attr("inpack");
				if (!inpack) inpack = 1;
			} else {
				count = $count.attr("quantity");
			}
			var $total_price = $count.parent().next();
			var unit_price = $total_price.attr("unit_price");
			if (unit_price && unit_price != "undefined") {
				$total_price.text((parseInt(count) * parseInt(inpack) * parseFloat(unit_price)).toFixed(2));
			}
		});

		var sum_price = 0;
		$("#application_sheet_content").find("td[unit_price]").each(function(idx, ele){
			var $unit_price = $(ele);
			if ($unit_price.closest("tr[partial_id]").attr("state") == "delete") return;
			if ($unit_price.text()) {
				sum_price += parseFloat($unit_price.text());
			}
		});
		if (sum_price)
			$("#t_total_price span").text("$ " + sum_price.toFixed(2));
	},
	checkQuantityInput : function($target, packed, pack_content, $choosed_measure, available_inventory){
		var quantity_input = $target.val();
		if (available_inventory == null) available_inventory = $target.attr("available_inventory");
		if (available_inventory == null) available_inventory = 9999999999;

		if (!quantity_input) {
			errorPop("请选择申请数量。", $target);
			return null;
		}
		if (isNaN(quantity_input) || quantity_input.indexOf('e') >= 0) {
			errorPop("请为申请数量填写一个正确的数字。" , $target);
			return null;
		}
		if (quantity_input.indexOf('.') >= 0) {
			errorPop("请为申请数量填写一个整数。" , $target);
			return null;
		}
		quantity_input = parseInt(quantity_input);
		if (quantity_input <= 0) {
			errorPop("请填写大于0的申请数量。" , $target);
			return null;
		}

		if (packed) {
			if ($choosed_measure == null || $choosed_measure.val() == 0) {
				if (quantity_input > 10) {
					errorPop("按包装申请时，不可超过10包装。" , $target);
					return null;
				}
				if (quantity_input * pack_content > available_inventory) {
					errorPop("不可申请超过现有库存的数量。" , $target);
					return null;
				}
			} else
			if (quantity_input > available_inventory) {
				errorPop("不可申请超过现有库存的数量。" , $target);
				return null;
			}
		} else {
			if (quantity_input > available_inventory) {
				errorPop("不可申请超过现有库存的数量。" , $target);
				return null;
			}
		}
		return quantity_input;
	}
}

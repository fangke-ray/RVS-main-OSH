var st_func = {
	showAdd : function(){
		var $pop_window = $("#st_pop_window_new");
		// 清空输入项
		$pop_window.find("input[type!='button'][type!='radio'],textarea").val("");
		$("#need_compose").attr("checked", false);
		//$("#add_code").next("p").remove();
		$pop_window.find("select").val("").trigger("change");
		
		$("#add_code").unbind("keyup");
		$("#add_code").bind("keyup", function(e){
			var kcode =e.keyCode;
	
			if (this.value.length >= 6){
				if( kcode > 105 || kcode < 48){
					return;
				}
				$("#add_code").next().remove();
				st_func.autoSearch(null, 1);
			}
		});
		$("#add_safety_lever").unbind("keyup").unbind("change")
			.bind("keyup", function(e){
			var inp = this.value;
			if (inp && !isNaN(inp)) {
				$("#label_add_safety_lever").text(Math.ceil(parseInt(inp) / 2));
			} else {
				$("#label_add_safety_lever").text("-");
			}
		}).bind("change", function(e){
			var inp = this.value;
			if (inp && !isNaN(inp)) {
				$("#label_add_safety_lever").text(Math.ceil(parseInt(inp) / 2));
			} else {
				$("#label_add_safety_lever").text("-");
			}
		})

		/*验证*/
		$("#st_new_form").validate({
			rules:{	
				model_id:{
					required:true
				},
				safety_lever:{
					number:true,
					maxlength:2
				}
			}
		});
		$pop_window.dialog({
			resizable : false,
			width : '640px',
			modal : true,
			title : "加入组件设置",
			buttons : {
				"确认" : function() {
					if($("#add_safety_lever").val() && $("#add_safety_lever").val() <= 0 ){
						errorPop("基准库存不能小于0");
						return false;
					}
					if($("#new_partial_id").val() == "" || $("#new_partial_id").val() == null ){
						errorPop("请输入正确的组件代码");
						return false;
					}
					if($("#st_new_form").valid()){
						var data = {
							"model_id":$("#add_model_id").val(),
							"component_partial_id" : $("#new_partial_id").val(),
							"identify_code" : ($("#add_shelf ").val() || "") + "|" + ($("#add_layer").val() || ""),
							"safety_lever": $("#add_safety_lever").val()
						};
						if ($("#need_compose").is(":checked")) {
							data.cnt_partial_step1 = -1;
						}

						$.ajax({
							beforeSend : ajaxRequestType,
							async : false,
							url : 'component_manage.do?method=doInsertSnoutSetting',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : st_func.postAdd_handleComplete
						});
					
				}
			},
				"取消" : function() {
					$pop_window.dialog("close");
				}
			}
		});
	},
	postAdd_handleComplete : function(xhrobj){
		var resInfo = $.parseJSON(xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#st_pop_window_new").dialog("close");
		    getSettings();
		}		
	},
	showEdit : function(){
		var row = $("#snout_component_setting").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#snout_component_setting").getRowData(row);
		var data = {
			"model_id" : rowData.model_id
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'component_manage.do?method=editSnoutSetting',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj){
				st_func.showEdit_handleComplete(xhrobj, rowData);
			}
		});
	},
	showEdit_handleComplete : function(xhrobj, rowData){
		var resInfo = $.parseJSON(xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			// 修改库存设置弹出框初期化
			
			$("#st_pop_window_modify input[type='text']").removeClass("valid errorarea-single");
			$("#label_modelname").text(resInfo.returnForm.model_name);
			$("#edit_model_id").val(resInfo.returnForm.model_id);
			$("#edit_partial_id").val(resInfo.returnForm.component_partial_id);
			$("#edit_code").val(resInfo.returnForm.component_code);

			$("#edit_shelf ").val("-");
			$("#edit_layer").val("-");
			var identify_code = resInfo.returnForm.identify_code;
			if (identify_code && identify_code.lastIndexOf("|") >= 0) {
				var idxHiphen = identify_code.lastIndexOf("|");
				$("#edit_shelf ").val(identify_code.substring(0, idxHiphen));
				$("#edit_layer").val(identify_code.substring(idxHiphen + 1));
			}

			$("#edit_safety_lever").val(resInfo.returnForm.safety_lever);
			if (resInfo.returnForm.safety_lever) {
				$("#label_edit_safety_lever").text(Math.ceil(parseInt(resInfo.returnForm.safety_lever) / 2));
			} else {
				$("#label_edit_safety_lever").text("-");
			}

			$("#label_refurbished_code").text(rowData.refurbished_code || "-");
			$("#label_partial_code").text(rowData.partial_code || "-");

			$("#edit_code").unbind("keyup");
			$("#edit_code").bind("keyup", function(e){
				var kcode =e.keyCode;

				if (this.value.length >= 6){
					if( kcode > 105 || kcode < 48){
						return;
					}
					$("#edit_code").next().remove();
					st_func.autoSearch(null, 2);
				}
			});
			$("#edit_safety_lever").unbind("keyup").unbind("change")
				.bind("keyup", function(e){
				var inp = this.value;
				if (inp && !isNaN(inp)) {
					$("#label_edit_safety_lever").text(Math.ceil(parseInt(inp) / 2));
				} else {
					$("#label_edit_safety_lever").text("-");
				}
			}).bind("change", function(e){
				var inp = this.value;
				if (inp && !isNaN(inp)) {
					$("#label_edit_safety_lever").text(Math.ceil(parseInt(inp) / 2));
				} else {
					$("#label_edit_safety_lever").text("-");
				}
			})
			
			/*验证*/
			$("#st_modify_form").validate({
				rules:{	
					safety_lever:{
						digits:true,
						maxlength:2
					}
				}
			});

			var $pop_window = $("#st_pop_window_modify");
			$pop_window.dialog({
			    resizable : false,
				modal : true,
				title : "修改库存设置",
				width : '640px',
				buttons : {
					"确认" : function() {
						if( $("#st_modify_form").valid()) {
							st_func.postEdit();
						}
					},
					"取消" : function() {
						$pop_window.dialog("close");
					}
				}
			});
		}		
	},
	postEdit : function(){
		var data = {
			"model_id":$("#edit_model_id").val(),
			"component_partial_id" : $("#edit_partial_id").val(),
			"identify_code" : ($("#edit_shelf ").val() || "") + "|" + ($("#edit_layer").val() || ""),
			"safety_lever": $("#edit_safety_lever").val()
		};
				
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'component_manage.do?method=doUpdateSnoutSetting',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : st_func.postEdit_handleComplete
		});
	},
	postEdit_handleComplete : function(xhrobj){
		var resInfo = $.parseJSON(xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#st_pop_window_modify").dialog("close");
		    getSettings();
		}		
	},
	showDelete : function(){
		var row = $("#snout_component_setting").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#snout_component_setting").getRowData(row);

		var data = {
			"model_id" : rowData.model_id
		};
		var cnt_partial_step0 = parseInt(rowData.cnt_partial_step0);
		var cnt_partial_step1 = parseInt(rowData.cnt_partial_step1);
		if ((cnt_partial_step0 + cnt_partial_step1) > 0 ) {
			warningConfirm("库存中还有目镜·软管筒。确认要取消【" + encodeText(rowData.model_name) + " 型号】的 D/E 组件作业吗？", function() {
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : 'component_manage.do?method=doSnoutDeleteSetting',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : st_func.postDelete_handleComplete
				});
			
			}, null, "删除确认");
		} else {
			warningConfirm("删除不能恢复。确认要取消【" + encodeText(rowData.model_name) + " 型号】的 D/E 组件作业吗？", function() {
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : 'component_manage.do?method=doSnoutDeleteSetting',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : st_func.postDelete_handleComplete
				});
			}, null, "删除确认");
		}
	},
	postDelete_handleComplete : function(xhrobj){
		var resInfo = $.parseJSON(xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			infoPop("删除已经完成。", null, "删除");
			getSettings();
		}
	},
	showAdjust : function(){
		var row = $("#snout_component_setting").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#snout_component_setting").getRowData(row);
		var data = {
			"model_id" : rowData.model_id
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'component_manage.do?method=editSnoutSetting',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : st_func.showAdjust_handleComplete
		});
	},
	showAdjust_handleComplete : function(xhrobj){
		var resInfo = $.parseJSON(xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			// 修改库存设置弹出框初期化
			$("#adjust_label_modelname").text(resInfo.returnForm.model_name);
			$("#adjust_model_id").val(resInfo.returnForm.model_id);
			$("#adjust_partial_id").val(resInfo.returnForm.component_partial_id);
			$("#adjust_code").text(resInfo.returnForm.component_code || '-');
			$("#adjust_benchmark").text(resInfo.returnForm.safety_lever);
			if (resInfo.returnForm.safety_lever) {
				$("#adjust_safety_lever").text(Math.ceil(parseInt(resInfo.returnForm.safety_lever) / 2));
			} else {
				$("#adjust_safety_lever").text("-");
			}
			$("#adjust_sub_set_cnt_org").text(resInfo.returnForm.cnt_partial_step1 || 0);
			$("#adjust_sub_set_cnt").val(resInfo.returnForm.cnt_partial_step1 || 0);

			var $pop_window = $("#st_pop_window_adjust");
			$pop_window.dialog({
			    resizable : false,
				modal : true,
				title : "调整目镜·软管筒库存套数",
				width : '640px',
				buttons : {
					"确认" : function() {
						var cnt = $("#adjust_sub_set_cnt").val();
						if (!cnt || isNaN(cnt)) {
							$("#adjust_sub_set_cnt").val("0");
						} else {
							$("#adjust_sub_set_cnt").val($("#adjust_sub_set_cnt").val().trim());
						}
						cnt = parseInt($("#adjust_sub_set_cnt").val());
						if ($("#adjust_sub_set_cnt").val() != cnt) {
							$("#adjust_sub_set_cnt").val(cnt);
						}
						if (cnt < 0) {
							errorPop("不能设置库存数为负数。");
							return;
						}

						if (resInfo.returnForm.safety_lever) {
							if (cnt > parseInt(resInfo.returnForm.safety_lever)) {
								warningConfirm("设置的库存数超过了基准库存，是否确定是这个数量？", st_func.postAdjust);
								return;
							}
						}

						st_func.postAdjust();
					},
					"取消" : function() {
						$pop_window.dialog("close");
					}
				}
			});
		}
	},
	postAdjust : function(){
		var data = {
			"model_id":$("#adjust_model_id").val(),
			"sub_set_cnt" : $("#adjust_sub_set_cnt").val()
		};
				
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'component_manage.do?method=doAdjustSubPartSet',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : st_func.postAdjust_handleComplete
		});
	},
	postAdjust_handleComplete : function(xhrobj){
		var resInfo = $.parseJSON(xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			infoPop("调整已经完成。", null, "调整");
			$("#st_pop_window_adjust").dialog("close");
			getSettings();
		}
	},
	showSnoutList : function(){
		var row = $("#snout_component_setting").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#snout_component_setting").getRowData(row);
		var data = {
			"model_id" : rowData.model_id
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'snouts.do?method=showPrepairSnoutList',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				st_func.showSnoutList_handleComplete(xhrobj, rowData["model_name"])
			}
		});
	},
	snoutListdata : null,
	snoutListUpd : false,
	showSnoutList_handleComplete : function(xhrobj, model_name){
		var resInfo = $.parseJSON(xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			st_func.snoutListdata = resInfo.list;
			if ($("#gbox_snout_prepairlist").length > 0) {
				$("#snout_prepairlist").jqGrid().clearGridData();
				$("#snout_prepairlist").jqGrid('setGridParam', {data : st_func.snoutListdata}).trigger("reloadGrid", [{current : false}]);
			} else {
				$("#snout_prepairlist").jqGrid({
					toppager : true,
					data : st_func.snoutListdata,
					height : 277,
					width : 380,
					rowheight : 23,
					datatype : "local",
					colNames : [ 'material_id', '来源修理单号', '修理等级',
							'C 本体翻新', 'confirmer_name', '灭菌时间'],
					colModel : [
					{name:'origin_material_id',index:'origin_material_id', hidden:true, key: true},
					{
						name : 'origin_omr_notifi_no',
						index : 'origin_omr_notifi_no',
						width : 50
					}, {
						name : 'status',
						index : 'status',
						align : 'center',
						formatter: "select", editoptions:{value:"1:S1;3:S3"},
						width : 30
					},{
						name : 'refurbished',
						index : 'refurbished',
						align : 'right',
						width : 50,
						formatter: function(value,col,rowData) {
							if (value || value == '0') {
								if (value == '0') {
									return value + " 次";
								} else {
									return "<a href='javascript:showRefurb(" + rowData["material_id"] + ")'>第 " + value + " 次</a>"
								}
							}
						}
					}, {
						name : 'confirmer_name',
						index : 'confirmer_name',
						hidden:true
					}, {
						name :'finish_time',
						index :'finish_time',
						align : 'center',
						width : 80
					}],
					rowNum : 20,
					toppager : false,
					rownumbers : true,
					pager : "#snout_prepairlist_pager",
					viewrecords : true,
					gridview : true, // Speed up
					onSelectRow: function(rid){
						$("#sth_remove_button").enable();
						var rowData = $("#snout_prepairlist").getRowData(rid);
						if (rowData["confirmer_name"] == "0") {
							$("#sth_wash_button").enable();
						} else {
							$("#sth_wash_button").disable();
						}
					},
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true],
					gridComplete: function(){
						$("#sth_remove_button, #sth_wash_button").disable();
					}
				});
			}
		}
		if (resInfo.tobe_list) {
			$("#sth_add_button").enable().off("click").on("click", function(){
				st_func.showTobeSnoutList(resInfo.tobe_list);
			});
		} else {
			$("#sth_add_button").disable().off("click");
		}
		$("#sth_remove_button").off("click").on("click", st_func.abandonSnout);
		$("#sth_wash_button").off("click").on("click", st_func.continueCycle);

		$("#st_pop_window_prepairlist").dialog({
			resizable : false,
			width : 'auto',
			modal : true,
			title : model_name + " C 本体预备",
			close : function () {
				if (st_func.snoutListUpd) {
					getSettings();
				}
				st_func.snoutListUpd = false;
			},
			buttons : {
				"关闭" : function() {
					$("#st_pop_window_prepairlist").dialog("close");
				}
			}
		})
	},
	showTobeSnoutList : function(tobe_list){

		$("#tobe_list > tbody").html("");
		var tbodyHtml = "";
		
		for (var il in tobe_list) {
			var tobe = tobe_list[il];
			tbodyHtml += "<tr material_id='" + tobe.material_id + "'><td>" + tobe.sorc_no + "</td>";
			tbodyHtml += "<td>" + tobe.serial_no + "</td>";
			tbodyHtml += "<td>S" + tobe.level + "</td>";
			if (tobe.isHistory == '0') {
				tbodyHtml += "<td>" + tobe.isHistory + " 次</td></tr>";
			} else {
				tbodyHtml += "<td><a href='javascript:showRefurb(" + tobe.material_id + ")'>" + tobe.isHistory + " 次</a></td></tr>";
			}
		}
		$("#tobe_list > tbody").html(tbodyHtml);
		$("#tobe_list > tbody").children("tr").click(function(){
			st_func.getRecoverPcs($(this).attr("material_id"));
		});
		$("#st_pop_tobe").dialog({
			resizable : false,
			width : 'auto',
			modal : true,
			title : "选择 C 本体收集",
			buttons : {
				"关闭" : function() {
					$("#st_pop_tobe").dialog("close");
				}
			}
		})
	},
	getRecoverPcs : function(material_id) {
		var data = {
			"material_id" : material_id
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'snouts.do?method=getRecoverPcs',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				var resInfo = $.parseJSON(xhrobj.responseText);

				if (typeof pcsO === "object") {
					st_func.confirmRecover(material_id, resInfo.pcses, resInfo.registed); // resInfo.registed == "true"??
				} else {
					loadJs("js/common/pcs_editor.js",
						function(){
							st_func.confirmRecover(material_id, resInfo.pcses, resInfo.registed);
						}
					);
				}
			}
		});		
	},
	confirmRecover : function(material_id, pcses, registed){
		var $confirm_recover_dialog = $("#confirm_recover_dialog"); 
		if ($confirm_recover_dialog.length == 0) {
			$(document.body).append("<div id='confirm_recover_dialog'></div>");
			$confirm_recover_dialog = $("#confirm_recover_dialog");
		}
		$confirm_recover_dialog.html("<div id='dialog_pcs_container'><div id='pcs_detail_pcs_pages'></div><div id='pcs_detail_pcs_contents'></div></div>");
		var $pcs_container = $confirm_recover_dialog.children("#dialog_pcs_container");
		pcsO.init($pcs_container, false);
		pcsO.generate(pcses, true);

		var cycleButtons = {
			"仅登录" : function(){
				var postData = {"material_id" : material_id, step : 1};

				st_func.recoverSnout(postData);
			},
			"回收清洗" : function(){
				var postData = {"material_id" : material_id};
				if (registed) {
					postData.step = 3;
				}
				var empty = pcsO.valuePcs(postData);

				if (empty) {
					errorPop("请填完的全部的工程检查票选项。");
					return;
				}
				st_func.recoverSnout(postData);
			},
			"关闭" : function(){
				$confirm_recover_dialog.dialog("close");
			}
		}

		if (registed) {
			delete cycleButtons["仅登录"];
		}

		$confirm_recover_dialog.dialog({
			resizable : false,
			modal : true,
			width:960,
			title : "确认回收",
			buttons : cycleButtons
		});
	},
	recoverSnout : function(postData){
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'snouts.do?method=doRecover',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				st_func.snoutListUpd = true;
				$("#confirm_recover_dialog").dialog("close");
				$("#st_pop_tobe").dialog("close");
				$("#st_pop_window_prepairlist").dialog("close");
				
			}
		});
	},
	abandonSnout : function(){
		var material_id = $("#snout_prepairlist").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#snout_prepairlist").getRowData(material_id);

		warningConfirm("确认要废弃源自【" + encodeText(rowData.origin_omr_notifi_no) + "】的 C 本体吗？", function() {

			var data = {
				"material_id" : material_id
			};
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : 'snouts.do?method=doAbandon',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj) {
					st_func.snoutListUpd = true;
					griddata_remove(st_func.snoutListdata, "origin_material_id", material_id, false);
					$("#snout_prepairlist").jqGrid().clearGridData();
					$("#snout_prepairlist").jqGrid('setGridParam', {data : st_func.snoutListdata}).trigger("reloadGrid", [{current : false}]);
				}
			});
		});
	}, 
	continueCycle : function(){
		var material_id = $("#snout_prepairlist").jqGrid("getGridParam", "selrow");
		st_func.getRecoverPcs(material_id);
	},
	autoSearch : function ($tr, mode){
		var component_code;
		if (mode == 1) {
			component_code = $("#add_code").val();
		} else if (mode == 2) {
			component_code = $("#edit_code").val();
		} else {
			component_code = $tr.find("input:eq(0)").val();
		}
		var ConsumableFlgdata ={
			"component_code" : component_code
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'component_manage.do?method=getAutocomplete',
			cache : false,
			data : ConsumableFlgdata,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj){
				var resInfo = $.parseJSON(xhrobj.responseText);
				partialCode = resInfo.sPartialCode;
				resultFlg = resInfo.reasult_flg;
				if(resultFlg == "1"){
					if (mode == 1) {
						if(partialCode.partial_id != $("#new_partial_id").val()){
							$("#new_partial_id").val(partialCode.partial_id);
							$("#add_code").val(partialCode.component_code).blur();
						}
					} else if (mode == 2) {
						if(partialCode.partial_id != $("#edit_partial_id").val()){
							$("#edit_partial_id").val(partialCode.partial_id);
							$("#edit_code").val(partialCode.component_code).blur();
						}
					} else {
						$tr.find("input:eq(0)").val(partialCode.component_code);
						$tr.find(".add_partial_name").val(partialCode.partial_name);
						$tr.find(".add_partial_id").val(partialCode.partial_id);
					}
				}
			}
		});
	}
}
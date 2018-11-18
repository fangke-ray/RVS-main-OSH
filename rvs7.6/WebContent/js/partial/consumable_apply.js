var page_apply = {
	servicePath : "consumable_apply.do",

	reset : function() {
		$("#search_application_no").val("");
		$("#search_section_id").val("").trigger("change");
		$("#search_line_id").val("").trigger("change");
		$("#search_apply_time_start").val("");
		$("#search_apply_time_end").val("");
		$("#all_supplied_all").attr("checked","checked").trigger("change");
		$("#flg_all").attr("checked","checked").trigger("change");
	},
	
	findit : function() {
		var searchData = {
			"consumable_application_key":$("#hidden_key").val(),
			"application_no" : $("#search_application_no").val(),
			"section_id" : $("#search_section_id").val(),
			"line_id" : $("#search_line_id").val(),
			"all_supplied" : $("#search_all_supplied input:checked").val(),
			"flg" : $("#search_flg input:checked").val(),
			"apply_time_start" : $("#search_apply_time_start").val(),
			"apply_time_end" : $("#search_apply_time_end").val()
		};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : page_apply.servicePath + '?method=search',
			cache : false,
			data : searchData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : page_apply.doInit_ajaxSuccess
		});
	},
	
	doInit_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				page_apply.show_apply_list(resInfo.apply_list);
				$("#hidden_key").val("");
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	show_apply_list : function(applyList) {
		if ($("#gbox_apply_list").length > 0) {
			// jqGrid已构建的情况下,重载数据并刷新
			$("#apply_list").jqGrid().clearGridData();
			$("#apply_list").jqGrid('setGridParam', {data : applyList}).trigger("reloadGrid", [{current : false}]);
		} else {
			$("#apply_list").jqGrid({
				data : applyList,
				height : 461,
				width : 992,
				rowheight : 23,
				datatype : "local",
				colNames : ['消耗品申请单Key', '申请单编号', '申请工程', '申请日时', '即时领用', '申请理由', '发放日时', '发放总价', ''],
				colModel : [{
					name : 'consumable_application_key',
					index : 'consumable_application_key',
					hidden : true
				}, {
					name : 'application_no',
					index : 'application_no',
					width : 80
				}, {
					name : 'line_name',
					index : 'line_name',
					width : 80
				}, {
					name : 'apply_time',
					index : 'apply_time',
					align : 'center',
							width : 50,
							formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H\\h'},
					sorttype:'date'	
				}, {
					name : 'flg',
					index : 'flg',
					width : 40,
					align : 'center',
					formatter:'select', editoptions:{value:"1:含;0:无;"},
					sorttype:'integer'
				}, {
					name : 'apply_reason',
					index : 'apply_reason'
				}, {
					name : 'supply_time',
					index : 'supply_time',
					align : 'center',
							width : 50,
							formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H\\h'},
					sorttype:'date'		
				}, {
					name : 'price',
					index : 'price',
					sorttype:'currency',formatter:'currency',formatoptions:{prefix:'$ ', thousandsSeparator:',',defaultValue: '/'},
					align : 'right',
					width: 60
				},
				{
					name : 'all_supplied',
					index : 'all_supplied',
					hidden : true
				}
				],
				rowNum : 35,
				rownumbers:true,
				toppager : false,
				pager : "#apply_list_pager",
				viewrecords : true,
				gridview : true,
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				hidegrid : false,
				deselectAfterSort : false,
				ondblClickRow : page_apply.showDetail,
				onSelectRow :null,
				viewsortcols : [true, 'vertical', true]
			});
		}
	},
	
	showDetail  : function(){
		var isFact = $('#hidden_isFact').val();
		var isApplier = $('#hidden_isApplier').val();
		if (isFact == "false" && isApplier == "false") {
			return;
		}

		var rowID = $("#apply_list").jqGrid("getGridParam", "selrow");
		var rowData = $("#apply_list").getRowData(rowID);
		var all_supplied = rowData.all_supplied;//发放完成标记
		var apply_time = rowData.apply_time.trim();//申请日时

		var elseDetail = true;
		if(isFact == "true") {
			if(all_supplied == 0 && apply_time){//未发放
				var consumable_application_key  = rowData.consumable_application_key;//消耗品申请单Key
				
				var data= {
					"consumable_application_key":consumable_application_key
				};

				elseDetail = false;

				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : "consumable_apply.do?method=getDetail",
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : page_apply.getDetail_ajaxSuccess
				});
			}
		}

		if(elseDetail && isApplier == "true") {
			if(!apply_time){
				elseDetail = false;
				consumable_application_edit(
					rowData.consumable_application_key, page_apply.findit)
			}
		}

		if (elseDetail) {
			var consumable_application_key  = rowData.consumable_application_key;//消耗品申请单Key

			var data= {
				"consumable_application_key":consumable_application_key
			};
			
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : "consumable_apply.do?method=getDetail",
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : page_apply.getDetailReadOnly_ajaxSuccess
			});
		}
	},

	getDetail_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				//页面赋值
				page_apply.setValue(resInfo, "");
				
				//弹出窗体
				var $pop_window = $("#pop_window_apply_edit");
				$pop_window.dialog({
					resizable : false,
					width : '990px',
					maxHeight : 200,
					modal : true,
					title : "申请单提供",
					buttons : {
						"确认并发放" : function() {
							page_apply.update($("#consumable_application_key").val());
						},
						"取消" : function() {
							$pop_window.dialog("close");
						}
					}
				});
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},
	getDetailReadOnly_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				//页面赋值
				page_apply.setValue(resInfo, "_readonly");
				
				//弹出窗体
				var $pop_window = $("#pop_window_application_detail");
				$pop_window.dialog({
					resizable : false,
					width : '990px',
					maxHeight : 200,
					modal : true,
					title : "申请单详细",
					buttons : {
						"关闭" : function() {
							$pop_window.dialog("close");
						}
					}
				});
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
		};
	},

	setValue : function(resInfo, appedix){

		$("#edit_apply_time" + appedix).text("");//申请时间
		$("#part_supply_s").attr("checked",false).trigger("change");
		if($(".treason").length > 0){
			$(".treason").remove();
		}
		$(".application_sheet" + " tr:nth-child(n+3)").remove();		

		var consumableApplicationForm = resInfo.consumableApplicationForm;

		$("#consumable_application_key").val(consumableApplicationForm.consumable_application_key);
		$("#hidden_section_id").val(consumableApplicationForm.section_id);
		$("#hidden_line_id").val(consumableApplicationForm.line_id);
		$("#edit_application_no" + appedix).text(consumableApplicationForm.application_no); //申请单编号
		$("#edit_line_name" + appedix).text(consumableApplicationForm.line_name); //申请工程
		$("#edit_apply_time" + appedix).text(consumableApplicationForm.apply_time); //申请时间

		var list = resInfo.list;
		
		var aOnline = new Array();//在线维修消耗
		var aDailyApply = new Array();//日常补充消耗品
		var aDailyApplyB = new Array();//日常补充乙材
		var aDailyDomestic = new Array();//日常补充国内耗材
		
		for(var i=0;i<list.length;i++){
			var form = list[i];
			
			var apply_method = form.apply_method;//补充方式

			var type = form.type;//分类

			if(apply_method == 1){//在线
				aOnline.push(form);
			}else if(type == 8){ //乙材
				aDailyApplyB.push(form);
			}else if(type == 9){//国内耗材
				aDailyDomestic.push(form);
			}else{//日常补充
				aDailyApply.push(form);
			}
		}

		//在线维修消耗 
		if(aOnline.length > 0){
			var content = "<tr class='treason'>"
						   +  "<td class='ui-state-default td-title'>使用单号</td>"
						   +  "<td class='td-content'>" + (consumableApplicationForm.use_no || "") +  "</td>"
						   +  "<td class='ui-state-default td-title'>申请工位</td>"
						   +  "<td class='td-content'>" + (consumableApplicationForm.process_code || "") +  "</td>"
						   +  "<td class='ui-state-default td-title'>申请人</td>"
						   +  "<td class='td-content'>" + (consumableApplicationForm.operator_name || "") +  "</td>";
						if (!appedix)
						  content +=  "<td style='display:none'></td>"
						   +  "<td style='display:none'></td>"
						   +  "<td style='display:none'></td>";

			  content += "</tr>";
			  content +=  "<tr class='treason'><td class='ui-state-default td-title'>原因</td>"
						   +  "<td class='td-content' colspan='5'>" + (consumableApplicationForm.apply_reason || "") +  "</td>";
						if (!appedix)
						  content +=  "<td style='display:none'></td>"
						   +  "<td style='display:none'></td>"
						   +  "<td style='display:none'></td>";

			  content += "</tr>";
			$("#online_sheet" + appedix + " tbody tr:nth-child(2)").before(content);		   
		}

		if (!appedix) {
			page_apply.createHTML(aOnline,"online_sheet");
	
			//日常补充消耗品 
			page_apply.createHTML(aDailyApply,"screw_sheet");
	
			//日常补充乙材 
			page_apply.createHTML(aDailyApplyB,"assistant_sheet");
	
			//日常补充国内耗材
			page_apply.createHTML(aDailyDomestic,"domestic_sheet");
		} else {
			page_apply.createHTML4Read(aOnline,"online_sheet"  + appedix);
	
			//日常补充消耗品 
			page_apply.createHTML4Read(aDailyApply,"screw_sheet" + appedix);
	
			//日常补充乙材 
			page_apply.createHTML4Read(aDailyApplyB,"assistant_sheet" + appedix);
	
			//日常补充国内耗材
			page_apply.createHTML4Read(aDailyDomestic,"domestic_sheet" + appedix);
		}

		$(".ui-buttonset").buttonset();
	},
	
	check_pack_method : function(form){
		var pack_method = form.pack_method;//包装
		var content = form.content;//内容量
		
		var value="";
		
		if(content == 1){//分装
			value ="-";
		}else if(pack_method == 0){//包装
			var supply_quantity = form.supply_quantity;//提供数量
			if(supply_quantity > 0){
				value = parseInt(form.waitting_quantity / form.content) + " " + form.unit_name + "/" + parseInt( form.apply_quantity /form.content) + " " + form.unit_name;
			}else{
				value = parseInt( form.apply_quantity /form.content) + " " + form.unit_name;
			}
		}else{
			var supply_quantity = form.supply_quantity;//提供数量
			if(supply_quantity > 0){
				value =  form.waitting_quantity +"/" + form.apply_quantity;
			}else{
				value = form.apply_quantity;
			}
		}

		return value;
	},
	
	check_pack_method_4_read : function(form){
		var pack_method = form.pack_method;//包装
		var content = form.content;//内容量
		
		var value="";
		
		if(content == 1){//分装
			value ="-";
		}else if(pack_method == 0){//包装
			value = parseInt(form.apply_quantity /form.content) + " " + form.unit_name;
		}else{
			value = form.apply_quantity;
		}
		
		return value;
	},
	
	createHTML : function(array,id){
		
		var htmlContent="";

		var partialMap = {};
		var partialIndex = [];

		// 按照零件+附加归并
		for(var i=0;i<array.length;i++){
			var form = array[i];
			
			var partialKey = form.partial_id;
			if (form.cut_length) {
				partialKey += "_" + form.cut_length;
			}
			var petitioner_id = form.petitioner_id;//申请人

			if (!partialMap[partialKey]) {
				partialMap[partialKey] = {data: [form]};
				partialIndex.push(partialKey);
			} else {
				partialMap[partialKey].data.push(form);
			}
		}

		for(var i=0;i<partialIndex.length;i++){
			var partialKey = partialIndex[i];

			var form = null;
			if (partialMap[partialKey].data.length == 1) {
				form = partialMap[partialKey].data[0];
			} else {
				var t_apply_quantity = 0;
				var t_waitting_quantity = 0;
				var t_supply_quantity = 0;
				var t_pack_method = 0;
				// 合计
				for (var iP=0;iP < partialMap[partialKey].data.length; iP ++) {
					form = partialMap[partialKey].data[iP];
					var apply_label = page_apply.check_pack_method(partialMap[partialKey].data[iP]);

					var content = form.content;  //内容量
					var pack_method = form.pack_method;//包装

					var input;

					if(content == 1){
						if (iP == 0) {
						input = "<div>"
							  +		"<input type='radio' name='" + id + i + "_" + iP + "' class='ui-widget-content' value='0' id='" + id + "_n"+ i +"' data-flg='A' checked='checked'/>"
							  +		"<input type='radio' name='" + id + i + "_" + iP + "' class='ui-widget-content' value='1' id='" + id+ "_y"+ i +"' data-flg='B' />"
							  +		"<input type='radio' name='"+ id + i + "_" + iP + "' class='ui-widget-content' value='1' id='" + id + "_new"+ i +"' data-flg='C' />"
							  + "</div>";
						} else {
						input = "<div>"
							  +		"<input type='radio' name='" + id + i + "_" + iP + "' class='ui-widget-content' value='-1' id='" + id + "_n"+ i +"' data-flg='Z' checked='checked'/>"
							  + "</div>";
						}
					}else{//单品
						input = "<input type='number' value='" + form.waitting_quantity + "' class='edit_a_supply_quantiy' min='0' data-flg='E'></input>";
					}

					htmlContent = htmlContent + "<tr style='display:none;' partial_id='" + form.partial_id + "' petitioner_id='" + form.petitioner_id + "' type='"+ form.type + "'>" 
							+ "<td>" + form.code + "</td><td></td><td></td>" // code partial_name stock_code
							+ "<td class='td-content'>" + apply_label + "</td>"
							+ "<td class='td-content'>" + input + "</td>" // input
							+ "<td></td>" // 发放总价
							+ "<td>"+ form.waitting_quantity + "</td>"
							+ "<td>" + form.available_inventory + "</td>"
							+ "<td>" + form.supply_quantity + "</td>"
							+ "</tr>";

							
					t_apply_quantity += parseInt(form.apply_quantity);
					t_waitting_quantity += parseInt(form.waitting_quantity);
					t_supply_quantity += parseInt(form.supply_quantity);
					if (pack_method != 0) t_pack_method = pack_method;
				}
				// 显示的汇总
				form.petitioner_id = null;
				form.apply_quantity = t_apply_quantity;
				form.waitting_quantity = t_waitting_quantity;
				form.supply_quantity = t_supply_quantity;
				form.pack_method = t_pack_method;
			}

			var supply_quantity = form.supply_quantity;//提供数量(已经发放)
			var petitioner_id = form.petitioner_id;//申请人

			var apply_label = page_apply.check_pack_method(form);

			var content = form.content;  //内容量
			var pack_method = form.pack_method;//包装

			var input;

			if(content == 1){
				input = "<div class='ui-buttonset'>"
					  +		"<input type='radio' name='" + id + i + "' class='ui-widget-content' value='0' id='" + id + "_n"+ i +"' data-flg='A' checked='checked'/>"
					  +		"<label for='" + id + "_n" + i +"'>不发放</label>"
					  +		"<input type='radio' name='" + id + i + "' class='ui-widget-content' value='1' id='" + id+ "_y"+ i +"' data-flg='B' />"
					  +     "<label for='" + id + "_y" + i +"'>已开封提供</label>"
					  +		"<input type='radio' name='"+ id + i + "' class='ui-widget-content' value='1' id='" + id + "_new"+ i +"' data-flg='C' />"
					  +     "<label for='" + id+ "_new" + i +"'>新开封提供</label>"
					  + "</div>";

			}else if(pack_method == 0){//包装
				input = "<input type='number' value='" + parseInt(form.waitting_quantity / content) + "' class='edit_a_supply_quantiy" + (form.petitioner_id == null ? " grouper" : "") + "' min='0' data-flg='D'></input>"  + form.unit_name;
			}else{//单品
				input = "<input type='number' value='" + form.waitting_quantity + "' class='edit_a_supply_quantiy" + (form.petitioner_id == null ? " grouper" : "") + "' min='0' data-flg='E'></input>";
			}

			var description = form.partial_name || "";
			if (form.cut_length) {
				description = "<span class='label_cut_length'>裁剪：(" + form.cut_length + " MM)</span><br>" + description;
			}

			htmlContent = htmlContent + "<tr partial_id='" + form.partial_id + "' petitioner_id='" + petitioner_id +"' type='"+ form.type + "' " + (content!=null && pack_method == 0 ? " content='" + content+ "'" :"") +	">"
											+ 	"<td class='td-content'>" + form.code + "</td>"
											+ 	"<td class='td-content'>" + description + "</td>"
											+ 	"<td class='td-content'>" + (form.stock_code || "-") + "</td>"
											+ 	"<td class='td-content'>" + apply_label + "</td>"
											+ 	"<td class='td-content'>"
											+		input
											+   "</td>"
											+   "<td class='td-content' unit_price='" + form.price + "'></td>"
											+ 	"<td>"+ form.waitting_quantity + "</td>"
											+   "<td>" + form.available_inventory + "</td>"
											+   "<td>" + form.supply_quantity + "</td>"
											+ "</tr>";

		}

		$("#"+ id +" tbody").append(htmlContent);
		$("#"+ id +" tbody").find("input").change(function(){
			page_apply.countPrice($(this).closest("tr"));
		}).filter(".grouper").change(function(){
			page_apply.arrestGroup(this.value, $(this).closest("tr"));
		});
		$("#"+ id +" tbody tr[partial_id]").each(function(){
			page_apply.countPrice($(this));
		});
	},
	createHTML4Read : function(array,id){
		var htmlContent="";
		
		var partialMap = {};
		var partialIndex = [];

		// 按照零件归并
		for(var i=0;i<array.length;i++){
			var form = array[i];
			
			var partial_id = form.partial_id;
			var petitioner_id = form.petitioner_id;//申请人

			if (!partialMap[partial_id]) {
				partialMap[partial_id] = {data: [form]};
				partialIndex.push(partial_id);
			} else {
				partialMap[partial_id].data.push(form);
			}
		}

		for(var i=0;i<partialIndex.length;i++){
			var partial_id = partialIndex[i];

			var form = null;
			if (partialMap[partial_id].data.length == 1) {
				form = partialMap[partial_id].data[0];
			} else {
				var t_apply_quantity = 0;
				var t_waitting_quantity = 0;
				var t_supply_quantity = 0;
				var t_pack_method = 0;
				// 合计
				for (var iP=0;iP < partialMap[partial_id].data.length; iP ++) {
					form = partialMap[partial_id].data[iP];

					var pack_method = form.pack_method;//包装

					t_apply_quantity += parseInt(form.apply_quantity);
					t_waitting_quantity += parseInt(form.waitting_quantity);
					t_supply_quantity += parseInt(form.supply_quantity);
					if (pack_method != 0) t_pack_method = pack_method;
				}
				// 显示的汇总
				form.petitioner_id = null;
				form.apply_quantity = t_apply_quantity;
				form.waitting_quantity = t_waitting_quantity;
				form.supply_quantity = t_supply_quantity;
				form.pack_method = t_pack_method;
			}

			var supply_quantity = form.supply_quantity;//提供数量(已经发放)
			var petitioner_id = form.petitioner_id;//申请人
			
			var apply_label = page_apply.check_pack_method_4_read(form);
			
			var content = form.content;  //内容量
			var pack_method = form.pack_method;//包装
			
			var input;
			
			if(content == 1){
				input = "分装领用";
				supply_quantity = 1; // TODO
				
			}else if(pack_method == 0){//包装
				input = "<span>" + parseInt(form.supply_quantity / content) + "</span> "  + form.unit_name;
			}else{//单品
				input = "<span>" + form.supply_quantity + "</span>";
			}

			var total_price = "";
			if (form.price) {
				total_price = (supply_quantity * form.price).toFixed(2);
			}
			
			htmlContent = htmlContent + "<tr partial_id='" + form.partial_id + "' petitioner_id='" + petitioner_id +"' type='"+ form.type + "' " + (content!=null && pack_method == 0 ? " content='" + content+ "'" :"") +	">"
											+ 	"<td class='td-content'>" + form.code + "</td>"
											+ 	"<td class='td-content'>" + form.partial_name + "</td>"
											+ 	"<td class='td-content'>" + (form.stock_code || "-") + "</td>"
											+ 	"<td class='td-content'>" + apply_label + "</td>"
											+ 	"<td class='td-content'>"
											+		input
											+   "</td>"
											+ 	"<td class='td-content'>"
											+		total_price
											+   "</td>"
											+ "</tr>"
		
		}
		
		$("#"+ id +" tbody").append(htmlContent);
	},
	countPrice : function($tr) {
		var content = $tr.attr("content");
		var  $input = $tr.find("td:nth-child(5) input");
		var supply_quantity = $input.val();//发放数量   type[radio]  已开封提供,新开封提供 =1  不发放 =0
		if(content){
			supply_quantity  = supply_quantity * content;
		}

		if($input.attr("type") == "radio"){
			var openflg = $tr.find("td:nth-child(5) input:checked").attr("data-flg");
			if ("C" === openflg) {
				supply_quantity = 1;
			} else {
				supply_quantity = 0;
			}
		}

		var $price = $tr.find("td:nth-child(6)");
		var unit_price = $price.attr("unit_price");

		if (unit_price && unit_price != "undefined") {
			var total_price = (supply_quantity * parseFloat(unit_price));
			if (total_price) {
				$price.text("$ " + total_price.toFixed(2));
			} else {
				$price.text("");
			}
		}
	},
	update : function(key){
		
		var data={};
		page_apply.setUpdate(data,key);
		
		var doUpdate_ajaxSuccess = function(xhrobj, textStatus){
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
		
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					
					var success = resInfo.success;
					if(success == false){//	存在 发放数量 < 待发放数量 的任何一条记录
						
						$("#confirmmessage").html("");
						$("#confirmmessage").html("目前没有按照申请单内容全数发放, 是否确定已经对此单发放完毕?");
						$("#confirmmessage").dialog({
							resizable : false,
							width : '400px',
							maxHeight : 200,
							modal : true,
							title : "发放确认",
							buttons : {
								"返回" : function() {
									$(this).dialog("close");
								},
								"全部发放" : function() {
									data.part_supply_comfrim = 1;
									$.ajax({
										beforeSend : ajaxRequestType,
										async : false,
										url : "consumable_apply.do?method=doUpdate",
										cache : false,
										data : data,
										type : "post",
										dataType : "json",
										success : ajaxSuccessCheck,
										error : ajaxError,
										complete : doUpdate_ajaxSuccess
									});
									$(this).dialog("close");
								},
								"部分发放":function(){
									data.part_supply_comfrim = 0;
									$.ajax({
										beforeSend : ajaxRequestType,
										async : false,
										url : "consumable_apply.do?method=doUpdate",
										cache : false,
										data : data,
										type : "post",
										dataType : "json",
										success : ajaxSuccessCheck,
										error : ajaxError,
										complete : doUpdate_ajaxSuccess
									});
									$(this).dialog("close");
								}
							}
						});
						
					}else{
						$("#pop_window_apply_edit").dialog("close");
						page_apply.findit();
					}
					
				}
			} catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
			};
		};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : "consumable_apply.do?method=doUpdate",
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete :doUpdate_ajaxSuccess
		});
	},
	
	
	setUpdate : function(data,key){
		
		data.consumable_application_key = key;
		data.part_supply = $("#part_supply input:checked").val() == null ? "":$("#part_supply input:checked").val();//部分发放
		data.section_id = $("#hidden_section_id").val().trim();
		data.line_id = $("#hidden_line_id").val().trim();
		data.application_no = $("#edit_application_no").text().trim();
		
		var i = 0;
		
		$(".application_sheet tbody tr:nth-last-child(n+1)").each(function(index,ele){
			
			var $tr = $(ele);
			
			if($tr.attr("partial_id")== null || $tr.attr("partial_id") =="") return;
			
			var partial_id = $tr.attr("partial_id");//零件Id
			var petitioner_id = $tr.attr("petitioner_id");//申请人
			if ("null" == petitioner_id) return;

			var type = $tr.attr("type");//分类
			var content = $tr.attr("content");//内容量
			
			var code = $tr.find("td:first-child").text().trim();//消耗品代码(Code)
			
			var db_supply_quantity = $tr.find("td:last-child").text().trim();//提供数量(DB)
	
			var  available_inventory = $tr.find("td:nth-last-child(2)").text().trim();//当前有效库存
			var  waitting_quantity = $tr.find("td:nth-last-child(3)").text().trim();//待发放数量

			var  supply_quantity = $tr.find("td:nth-child(5) input").val();//发放数量   type[radio]  已开封提供,新开封提供 =1  不发放 =0
			if(content){
				supply_quantity  = supply_quantity * content;
			}

			var  $input = $tr.find("td:nth-child(5) input");
			
			var openflg;//开封标记
			if($input.attr("type") == "radio"){
				openflg = $tr.find("td:nth-child(5) input:checked").attr("data-flg");
				supply_quantity = $tr.find("td:nth-child(5) input:checked").val();
				if (openflg == "Z") return;
			}else if($input.attr("type") == "number"){
				openflg = $input.attr("data-flg");
			}

			data["consumable_application.consumable_application_key["+ i +"]"] = key;
			data["consumable_application.partial_id["+ i +"]"] = partial_id;
			data["consumable_application.petitioner_id["+ i +"]"] = petitioner_id;
			data["consumable_application.type["+ i +"]"] = type;
			data["consumable_application.code["+ i +"]"] = code;
			data["consumable_application.available_inventory["+ i +"]"] = available_inventory;
			data["consumable_application.waitting_quantity["+ i +"]"] = waitting_quantity;
			data["consumable_application.supply_quantity["+ i +"]"] = supply_quantity;
			data["consumable_application.openflg["+ i +"]"] = openflg;
			data["consumable_application.db_supply_quantity["+ i +"]"] = db_supply_quantity;
			
			i++;
		});
	},
	arrestGroup : function(groupValue, $groupTr) {
		console.log(groupValue)

		var content = $groupTr.attr("content");
		var waitting_quantity = parseInt($groupTr.find("td:nth-last-child(3)").text().trim());//待发放数量
		if (content) {
			groupValue = groupValue * parseInt(content);
		}
		if (groupValue > waitting_quantity) {
			groupValue = waitting_quantity;
			if (content) {
				groupValue = groupValue / parseInt(content);
			}
			$groupTr.find(".edit_a_supply_quantiy").val(groupValue).trigger("change");
			return;
		}

		$(".ui-dialog-content tr[partial_id=" + $groupTr.attr("partial_id") + "]").each(function(idx, ele){
			var $ele = $(ele);
			if ("null" == $ele.attr("petitioner_id")) return;
			var w_quantity = parseInt($ele.find("td:nth-last-child(3)").text().trim());//待发放数量
			if (groupValue == 0) {
				$ele.find("input").val(0);
			} else if (groupValue >= w_quantity) {
				$ele.find("input").val(w_quantity);
				groupValue -= w_quantity;
			} else {
				$ele.find("input").val(groupValue);
				groupValue = 0;
			}
		});
	}
};

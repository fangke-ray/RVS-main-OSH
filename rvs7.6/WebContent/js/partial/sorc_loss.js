var servicePath="sorc_loss.do";

//发现工程
var line_id="";
//责任区分
var service_free_flg ="";
//有无偿
var liability_flg ="";

var edit_material_partial_detail_key = "";

var obj_discover_project = {};
var obj_ocm = {};
var obj_ocm_rank = {};
var obj_sorc_rank = {};

var nogood_description;

$(function(){
    $("input.ui-button").button();  

 	$("#searchbutton").addClass("ui-button-primary");
  
    /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
    $("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	}); 
    
    line_id=$("#hidden_discoverProject").html();
    service_free_flg=$("#hidden_service_free_flg").html();
    liability_flg   =$("#hidden_liability_flg").html();
    
	$("#search_ocm_shipping_date,#search_order_date").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	$("#search_ocm_shipping_month").monthpicker({
		showButtonPanel : true,
		pattern: "yyyy/mm",
		startYear: 2013,
		finalYear: (new Date()).getFullYear(),
		monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
	});

    //判断 --如果选择了出货月  出货日期的值清空
    $("#search_ocm_shipping_month").change(function(){
        $("#search_ocm_shipping_date").data("post",$("#search_ocm_shipping_date").val(""));
    })    
    //选择了出货日期  出货月值清空
    $("#search_ocm_shipping_date").change(function(){
         $("#search_ocm_shipping_month").data("post",$("#search_ocm_shipping_month").val(""));
    })  
   
    //点击重置按钮之后 出货月和出货日期全部显示
	$("#resetbutton").click(function() {
        $("#search_ocm_shipping_date").data("post",$("#search_ocm_shipping_date").val(""));	
        $("#search_ocm_shipping_month").data("post",$("#search_ocm_shipping_month").val("")); 
        $("#search_sorc_no").data("post",$("#search_sorc_no").val(""));
        $("#search_order_date").data("post",$("#search_order_date").val(""));
	});
	
	$("#searchbutton").click(function(){
        $("#search_ocm_shipping_month").data("post",$("#search_ocm_shipping_month").val());
        $("#search_ocm_shipping_date").data("post",$("#search_ocm_shipping_date").val());
        $("#search_sorc_no").data("post",$("#search_sorc_no").val());
        $("#search_order_date").data("post",$("#search_order_date").val());
		  findit();	
	});	

    //单元/保内返修损金导入
    $("#loss_import_button").click(function(){
       loss_import();
    });
    
    //月损金一览表导出
    $("#month_loss_export_button").click(function(){
       download_month_loss();
    });

	obj_discover_project = selecOptions2Object($("#hidden_discover_project").val());
	obj_ocm = selecOptions2Object($("#hidden_ocm").val());
	obj_ocm_rank = selecOptions2Object($("#hidden_ocm_rank").val());
	obj_sorc_rank = selecOptions2Object($("#hidden_sorc_rank").val());

	findit();
    
    /* autoComplete （型号、等级） */
    $.ajax({
        beforeSend : ajaxRequestType,
        async : true,
         url : servicePath + '?method=getAutoComplete',
        cache : false,
        data : null,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : function(xhrobj) {
                // 以Object形式读取JSON
                eval('resInfo =' + xhrobj.responseText);
                nogood_description = resInfo.nogoodDescriptionList;
            }
    });
})
//月损金一览表导出
var download_month_loss = function(){
     var reportdata ={
         "ocm_shipping_date":$("#search_ocm_shipping_date").val(),
         "ocm_shipping_month":$("#search_ocm_shipping_month").val()
        }
       $.ajax({
            beforeSend : ajaxRequestType,
            async : false,
            url : servicePath + '?method=report',
            cache : false,
            data : reportdata,
            type : "post",
            dataType : "json",
            success : ajaxSuccessCheck,
            error : ajaxError,
            complete : function(xhjObject) {
                var resInfo = null;
                try {
                    eval("resInfo=" + xhjObject.responseText);
			        if (resInfo.errors.length > 0) {
			            // 共通出错信息框
			            treatBackMessages("#editarea", resInfo.errors);
			        } else {
			            if (resInfo && resInfo.fileName) {
		                    if ($("iframe").length > 0) {
		                        $("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
		                    } else {
		                        var iframe = document.createElement("iframe");
		                        iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
		                        iframe.style.display = "none";
		                        document.body.appendChild(iframe);
		                    }
		                } else {
		                    alert("文件导出失败！"); // TODO dialog
		                }
					}
			    } catch (e) {
			        alert("name: " + e.name + " message: " + e.message + " lineNumber: "
			                + e.lineNumber + " fileName: " + e.fileName);
			    };
            }
        });
}

//单元/保内返修损金导入
var loss_import = function(){
    $("#confirm_message").hide();
    $("#confirm_message").html("<input name='file' id='make_upload_file' type='file'/>");    
    $("#confirm_message").dialog({
        title : "选择上传文件",
        width : 280,
        show: "blind",
        height : 180,
        resizable:false,
        modal : true,
        minHeight : 200,
        close : function(){
            $("#confirm_message").html("");
        },
        buttons : {
            "上传":function(value){     
                    importfile();               
            }, "关闭" : function(){ 
                $(this).dialog("close");        
            }
        }
    });
    $("#confirm_message").show();    
}

var importfile = function() {
	// 覆盖层
	panelOverlay++;
	makeWindowOverlay();
	$.ajaxFileUpload({
	    url : 'upload_loss.do?method=doloss', // 需要链接到服务器地址
	    secureuri : false,
	    fileElementId : 'make_upload_file', // 文件选择框的id属性
	    dataType : 'json', // 服务器返回的格式
	    success : function(responseText, textStatus) {
	        panelOverlay--;
	        killWindowOverlay();
	        var resInfo = null;
	        try {
	            // 以Object形式读取JSON
	            eval('resInfo =' + responseText);
	            if (resInfo.errors.length > 0 ) {
	                // 共通出错信息框
	                treatBackMessages("#confirm_message", resInfo.errors);
	            }else if(resInfo.msgInfo.length >0 ){
	                treatBackMessages("#confirm_message", resInfo.msgInfo);
	            }else{
	                $("#confirm_message").dialog('close');
	                $("#confirm_message").text("导入单元损金完成。");
	                $("#confirm_message").dialog({
	                    resizable : false,
	                    modal : true,
	                    title : "导入确认",
	                    buttons : {
	                        "确认" : function() {
	                            $(this).dialog("close");
	                        }
	                    }
	                });
	            }

	        } catch(e) {
	            
	        }
	    }
    });
};

/*检索按钮事件*/
var keepSearchData;
var findit = function(data) {
     var ocm_shipping_month ="";
     if($("#search_ocm_shipping_month").val() !=""){
         ocm_shipping_month =$("#search_ocm_shipping_month").val()+"/01";
     }else{
         ocm_shipping_month =$("#search_ocm_shipping_month").val();
     }

     var ocm_shipping_time ="";
     if($("#search_ocm_shipping_date").val() !=""){
        ocm_shipping_time =$("#search_ocm_shipping_date").val();
     }else if($("#search_ocm_shipping_month").val() !=""){
        ocm_shipping_time =$("#search_ocm_shipping_month").val()+"/01";
     }
     
    if(!data){   
        keepSearchData ={
         "ocm_shipping_date":$("#search_ocm_shipping_date").val(),
         "ocm_shipping_time":ocm_shipping_time,
         "ocm_shipping_month":ocm_shipping_month,
         "sorc_no":$("#search_sorc_no").data("post"),
         "order_date":$("#search_order_date").val()
        }
     }else{
        keepSearchData =data;
     }
     $.ajax({
        beforeSend : ajaxRequestType,
        async : false,
        url : servicePath + '?method=search',
        cache : false,
        data : keepSearchData,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : search_handleComplete
    });		 
};

function search_handleComplete(xhrobj, textStatus) {
    var resInfo = null;
    // 以Object形式读取JSON responseText获取来自服务器响应的数据
    eval('resInfo =' + xhrobj.responseText);
    if (resInfo.errors.length > 0) {
        // 共通出错信息框
        treatBackMessages("#searcharea", resInfo.errors);
    } else {
        filed_list(resInfo.sorcLossForms);
    }
};

/*jqgrid表格*/
function filed_list(finished){
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:finished}).trigger("reloadGrid", [{current:false}]);
	} else {

		$("#list").jqGrid({
			data:finished,
			height: 445,
			width: 1240,
			rowheight: 23,
			datatype: "local",
			colNames : ['', '维修对象ID', '出货日期','修理单号','型号ID','型号', '机身号',
				        '分室', 'OCM<br>RANK', 'SORC<br>RANK', '等级变更', '发现工程', 
				        '责任区分', '不良简述','隐藏不良简述', '零件型号','数量','零件单价',
						'报价差异损金','有偿与否','备注'],
			colModel : [
						{name : 'material_partial_detail_key', hidden:true},
						{
						name : 'material_id',
						index : 'material_id',
						hidden : true
						}, {
							name : 'ocm_shipping_date',
							index : 'ocm_shipping_date',
							width : 40,
							align : "center",
							sorttype : 'date',
							formatter : 'date',
							formatoptions : {
								srcformat : 'Y/m/d',
								newformat : 'm-d'
							}
						}, {
							name : 'sorc_no',
							index : 'sorc_no',
							width : 80,
							align : 'left'
						}, {
							name : 'model_id',
							index : 'model_id',
							hidden : true
						}, {
							name : 'model_name',
							index : 'model_name',
							width : 70,
							align : 'left'
						}, {
							name : 'serial_no',
							index : 'serial_no',
							width : 40,
							align : 'left'
						}, {
							name : 'ocm',
							index : 'ocm',
							width : 50,
							align : 'left',
							formatter : "select",
							editoptions : {
								value : $("#hidden_ocm").val()
							}
						}, {
							name : 'ocm_rank',
							index : 'ocm_rank',
							width : 30,
							align : 'center',
							formatter : function (v,idx,rowData) {
								return (obj_ocm_rank[v] ? obj_ocm_rank[v] + (rowData["kind"] == 6 ? "W" : ""): "");
							}
						}, {
							name : 'level',
							index : 'level',
							width : 30,
							align : 'center',
							formatter : "select",
							editoptions : {
								value : $("#hidden_sorc_rank").val()
							}
						}, {
							name : 'change_rank',
							index : 'change_rank',
							width : 35,
							align : 'center'
						}, {
							name : 'belongs',
							index : 'belongs',
							width : 40,
							align : 'left',
							formatter : "select",
							editoptions : {
								value : $("#hidden_discover_project").val() // 
							}
						}, {
							name : 'liability_flg',
							index : 'liability_flg',
							width : 40,
							formatter : "select",
							editoptions : {
								value : $("#hidden_liabliity_flg").val()
							}
						}, {
							name : 'nogood_description',
							index : 'nogood_description',
							width : 70,
							align : 'left',
			                formatter : function(value, options, rData){
                                    if(rData["nogood_description"]!=null){
                                      return "<input type=\"hidden\" value="+ rData['material_partial_detail_key'] +"><label>"+rData['nogood_description']+"</label>";
                                    }else{
                                      return "<input type=\"hidden\" value="+ rData['material_partial_detail_key'] +"><label></label>";
                                    }
			                }
						}, {
                            name : 'nogood_description',
                            index : 'nogood_description',
                            width : 70,
                            hidden:true
                        }, {
							name : 'code',
							index : 'code',
							width : 50,
							align : 'left'
						}, {
							name : 'quantity',
							index : 'quantity',
							width : 20,
							align : 'right'
						}, {
							name : 'price',
							index : 'price',
							width : 40,
							align : 'right'
						}, {
							name : 'loss_price',
							index : 'loss_price',
							width : 60,
							align : 'right',
							formatter : function (v,idx,rowData) {
								if (rowData["kind"] == 6) {
									return "€ " + v;
								} else {
									return "$ " + v;
								}
							}
						}, {
							name : 'service_free_flg',
							index : 'service_free_flg',
							width : 40,
							align : 'center',
							formatter : "select",
							editoptions : {
								value : $("#hidden_service_freeFlg").val()
							}
						}, {
							name : 'comment',
							index : 'comment',
							width : 45,
							align : 'center'
						}],
			rowNum : 50,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			hidegrid : false,
			caption: "SORC损金一览",
			gridview: true,
			rownumbers : true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			ondblClickRow:showedit_loss_gold,
			viewsortcols : [true,'vertical',true],
            gridComplete : function() {
                $("#list").find("td[aria-describedby=\"list_nogood_description\"]").addClass("td-nogood-active");
	            $("#list").find("td[aria-describedby=\"list_nogood_description\"]").click(function(){
               
                //零件订购签收明细key
                var hkey=$(this).find("input").val();
                
	            $("#process_dialog").hide();
	            $("#process_dialog").html("<input type='text' id =\"nogood_description\"/>");
	            $("#nogood_description").autocomplete({
	                source : nogood_description,
	                minLength : 0,
	                delay : 100
	            });
                
                var $nogood=$(this);
	            $("#process_dialog").dialog({
	                    title : "修改不良简述",
	                    width : 200,
	                    show : "blind",
	                    height : 'auto' ,
	                    resizable : false,
	                    modal : true,
	                    minHeight : 200,
	                    buttons : {
	                    "确定":function(){
                                //前台选择不良简述切换
                                $nogood.find("label").text($("#nogood_description").val());
                                
	                            var data ={"material_partial_detail_key":hkey,"nogood_description":$("#nogood_description").val()};
                                $.ajax({
							        beforeSend : ajaxRequestType,
							        async : false,
							        url : servicePath + '?method=doupdateNogoodDescription',
							        cache : false,
							        data : data,
							        type : "post",
							        dataType : "json",
							        success : ajaxSuccessCheck,
							        error : ajaxError,
							        complete : update_nogoodDescriptionComplete
							    }); 
                                $("#process_dialog").dialog('close');
                                
	                    },
	                    "取消": function(){
	                            $("#process_dialog").dialog('close');
	                        }
	                    }
	              });
	           });
            }
		});
      }
};

//选择任意一行的不良简述，点击一下，弹出修改画面
var update_nogoodDescriptionComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    // 以Object形式读取JSON responseText获取来自服务器响应的数据
    eval('resInfo =' + xhrobj.responseText);
    if (resInfo.errors.length > 0) {
        // 共通出错信息框
        treatBackMessages("#searcharea", resInfo.errors);
    } else {
        //findit();
    }
};

/* 双击弹出画面 */
var showedit_loss_gold = function() {
	var row =$("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
    var rowData = $("#list").getRowData(row); //获取行数据

    edit_material_partial_detail_key = rowData["material_partial_detail_key"];

    $("#process_dialog").hide();
	
	$("#process_dialog").load("widgets/partial/sorc_loss_edit.jsp",function(){	
		
                $("#edit_discover_project").html(line_id);
                $("#edit_liability_flg").html(liability_flg);
                $("#edit_service_free_flg").html(service_free_flg);

                $("#edit_discover_project").children("option:eq(1),option:eq(2)").remove();
                
		        $("#edit_service_free_flg,#edit_liability_flg,#edit_discover_project").select2Buttons();
                
                $("#edit_fix_demand").autocomplete({
                    source : nogood_description,
                    minLength : 0,
                    delay : 100
                });

			    $("#label_model_name").text(rowData.model_name);
				$("#label_serial_no").text(rowData.serial_no);
				$("#label_sorc_no").text(rowData.sorc_no);

				$("#label_compartment").text(obj_ocm[rowData.ocm] || "");
				$("#label_ocm_rank").text(rowData.ocm_rank);
				$("#label_sorc_rank").text(obj_sorc_rank[rowData.level] || "");

				$("#edit_change_rank").val(rowData.change_rank);
				
				$("#edit_discover_project").val(rowData.belongs).trigger("change");
				$("#edit_org_belongs").val(rowData.belongs);
				
                $("#edit_liability_flg").val(rowData.liability_flg).trigger("change");
                
				$("#edit_fix_demand").val(rowData.nogood_description || "");
                $("#label_code").text(rowData.code);
				$("#label_quantity").text(rowData.quantity);
				$("#label_price").text(rowData.price);
				$("#label_loss_amount").text(rowData.loss_price);
				$("#edit_repair_original_price").val(rowData.repair_original_price);
				$("#edit_update_price").val(rowData.update_price);				
			
				$("#edit_service_free_flg").val(rowData.service_free_flg).trigger("change");

				$("#edit_osh_ocm_liability_flg").val(rowData.osh_ocm_liability_flg);
				$("#edit_comment").val(rowData.comment);

	            $("#process_dialog").dialog({
					title : "SORC损金编辑",
					width : 780,
					show : "blind",
					height : 'auto' ,
					resizable : false,
					modal : true,
					minHeight : 200,
					buttons : {
					"确定":function(){
                           data ={
                           		material_partial_detail_key :edit_material_partial_detail_key,
				             //"line_id":$("#edit_discover_project").val(),
				             "liability_flg":$("#edit_liability_flg").val(),
				             "nogood_description":$("#edit_fix_demand").val(),
                             "service_free_flg":$("#edit_service_free_flg").val(),
                             "comment":$("#edit_comment").val()
				            }

				            // 追加分类有改变
							if ($("#edit_discover_project").val() != $("#edit_org_belongs").val()) {
								data["belongs"] = $("#edit_discover_project").val();
							}

                            $.ajax({
						        beforeSend : ajaxRequestType,
						        async : true,
						        url : servicePath + '?method=doupdate',
						        cache : false,
						        data : data,
						        type : "post",
						        dataType : "json",
						        success : ajaxSuccessCheck,
						        error : ajaxError,
						        complete : function(xhrObj) {
						        	
								    eval('resInfo =' + xhrObj.responseText);
								    if (resInfo.errors.length > 0) {
								    	
								    } else {
			                            $("#confirm_message").text("损金更新已经完成！");
										$("#confirm_message").dialog({
											width : 320,
											height : 'auto',
											resizable : false,
											show : "blind",
											modal : true,
											title : "确认",
											buttons : {
												"关闭" : function() {
													$("#confirm_message").dialog("close");
												}
											}
										});
										$("#process_dialog").dialog('close');
										findit();
								    }

						        	// search_handleComplete(xhrObj);
						        }
						    });
                            
					},
					"取消": function(){
							$("#process_dialog").dialog('close');
						}
					}
				});
				
				$("#process_dialog").show();
	  });
};
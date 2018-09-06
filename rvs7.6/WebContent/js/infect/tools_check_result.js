
var servicePath="tools_check_result.do";

$(function(){
    $("input.ui-button").button();
    /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
    $("#searcharea span.ui-icon").bind("click", function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().show("blind");
        } else {
            $(this).parent().parent().next().hide("blind");
        }
    }); 

    //点检人员
    setReferChooser($("#hidden_search_operator_id"),$("#search_operator_name_referchooser"),$("#search_search_operator_id"));
    //责任工位
    setReferChooser($("#hidden_search_position_id"),$("#search_position_name_referchooser"),$("#search_search_position_id"));
    
    $("#search_section_id,#search_line_id").select2Buttons();

    //清除button
    $("#resetbutton").click(clearCondition);

    //检索button
    $("#searchbutton").click(function(){
    	findit();
    });
    findit();
});

/*清空检索条件*/
var clearCondition= function(){
   $("#search_manage_no").val("");
   $("#search_tools_no").val("");
   $("#search_tools_name").val("");
   $("#search_tools_type_id").val("");
   $("#search_section_id").val("").trigger("change");
   $("#search_line_id").val("").trigger("change");
   $("#hidden_search_position_id").val("");
   $("#search_position_id").val("");  
   $("#hidden_search_operator_id").val("");
   $("#search_operator_id").val("");
}
/*检索方法*/
var findit=function(){
    var data={
            "manage_code":$("#search_manage_no").val(),       
            "tools_no":$("#search_tools_no").val(),     
            "tools_name":$("#search_tools_name").val(),
            "section_id":$("#search_section_id").val(),           
            "line_id":$("#search_line_id").val(),  
            "position_id":$("#hidden_search_position_id").val(),  
            "operator_id":$("#hidden_search_operator_id").val() 
    }
    $.ajax({
        beforeSend:ajaxRequestType,
        async:true,//异步请求
        url:servicePath+'?method=search',//提交地址
        cache:false,//是否缓存
        data:data,//提交的数据
        type:"post",
        dataType:"json",//提交的数据类型
        success:ajaxSuccessCheck,
        error:ajaxError,
        complete:search_handleComplete//不管成功都执行
    });
};

var search_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
            var listdata = resInfo.resultForms;
            filed_list(listdata);
        }
    }catch (e) {};
}

/*重置*/
var reset=function(){
    $("#search_category_id").val("").trigger("change");
    $("#search_sorcno").val("");
    $("#search_serial_no").val("");
    $("#search_section_id").val("").trigger("change");
    $("#search_scheduled_date_start").val("");
    $("#search_scheduled_date_end").val("");
    $("#search_arrival_plan_date_start").val("");
    $("#search_arrival_plan_date_end").val("");
    $("#cond_work_procedure_order_template").val("");
    $("#cond_work_procedure_order_template_a").attr("checked","checked").trigger("change");
    $("#search_com_scheduled_date_start").val("");
    $("#search_com_scheduled_date_end").val("");
};

/**jqGrid**/
var filed_list=function(listdata){
    if($("#gbox_exd_list").length>0){
        $("#exd_list").jqGrid().clearGridData();//清空数据
        $("#exd_list").jqGrid("setGridParam",{data:listdata}).trigger("reloadGrid",[{current:false}]);//重新加载数据
    }else{
        $("#exd_list").jqGrid({
            //toppager:true,//是否在表格上部显示分页条
            data:listdata,
            width:1248,
            height:806,//rowheight*rowNum+1
            rowhight:23,//行高
            datatype:'local',//从服务器端返回的数据类型
            colNames:['管理ID','分发课室ID','责任工程ID','责任工位ID','治具名称ID','管理号码','治具号码','治具名称',
                      '4月','5月','6月','7月','8月','9月','10月','11月','12月',
                      '1月','2月','3月'],
            colModel:[
                      {name:'manage_id',index:'manage_id',hidden:true},
                      {name:'section_id',indedx:'section_id',hidden:true},
                      {name:'line_id',indedx:'line_id',hidden:true},
                      {name:'position_id',indedx:'position_id',hidden:true},
                      {name:'tools_type_id',index:'tools_type_id', width:40,hidden:true},
                      {name:'manage_code',index:'manage_code',width:40,align:'center'},
                      {name:'tools_no',index:'tools_no',width:30,algin:'center'},
                      {name:'tools_name',index:'tools_name', width:60,align:'left'},                    
                      {name:'april',index:'april',width:30, align:'center',sortable: false,formatter:'select',align:'center',
	                          editoptions:{
	                              value:$("#select_Checked_status").val()
	                          }
                      },
                      {name:'may',index:'may',width:30, align:'center',sortable: false,formatter:'select',align:'center',
	                          editoptions:{
	                              value:$("#select_Checked_status").val()
	                          }
                      },
                      {name:'june',index:'june',width:30, align:'center',sortable: false,formatter:'select',align:'center',
	                          editoptions:{
	                              value:$("#select_Checked_status").val()
	                          }
                      },
                      {name:'july',index:'july',width:30, align:'center',sortable: false,formatter:'select',align:'center',
	                          editoptions:{
	                              value:$("#select_Checked_status").val()
	                          }
                      },
                      {name:'august',index:'august',width:30, align:'center',sortable: false,formatter:'select',align:'center',
		                      editoptions:{
		                          value:$("#select_Checked_status").val()
		                      }
                          },
                      {name:'september',index:'september',width:30, align:'center',sortable: false,formatter:'select',align:'center',
	                          editoptions:{
	                              value:$("#select_Checked_status").val()
	                          }
                      },
                      {name:'october',index:'october',width:30, align:'center',sortable: false,formatter:'select',align:'center',
                          editoptions:{
                              value:$("#select_Checked_status").val()
                          }
                      },
                      {name:'november',index:'november',width:30, align:'center',sortable: false,formatter:'select',align:'center',
                          editoptions:{
                              value:$("#select_Checked_status").val()
                          }
                      },
                      {name:'december',index:'december',width:30, align:'center',sortable: false,formatter:'select',align:'center',
                          editoptions:{
                              value:$("#select_Checked_status").val()
                          }
                      },
                      {name:'january',index:'january',width:30, align:'center',sortable: false,formatter:'select',align:'center',
                          editoptions:{
                              value:$("#select_Checked_status").val()
                          }
                      },
                      {name:'february',index:'february',width:30, align:'center',sortable: false,formatter:'select',align:'center',
                          editoptions:{
                              value:$("#select_Checked_status").val()
                          }
                      },
                      {name:'march',index:'march',width:30, align:'center',sortable: false,formatter:'select',align:'center',
                          editoptions:{
                              value:$("#select_Checked_status").val()
                          }
                      }               
            ],
            rowNum : 35,
            pager: "#exd_listpager",//设置分页的,在页面中是使用<div/>来放置的
            viewrecords: true,//设置是否在page bar 显示所有记录的总数         
            hidegrid : false,//
            rownumbers : true,
            gridview: true, // 
            ondblClickRow : showEdit,
            //caption:'治具定期清点保养记录一览',
            pagerpos: 'right',//指定分页栏的位置
            pgbuttons: true,//是否显示翻页按钮
            pginput: false,//是否显示跳转页面的输入框
            recordpos: 'left',
            gridComplete : function() {// 当表格所有数据都加载完成而且其他的处理也都完成时触发
            	/*****计算合格台数和不合格台数*******/
                /*****计算合格台数和不合格台数*******/
                
                // 得到显示到界面的id集合
                var IDS = $("#exd_list").getDataIDs();
                // 当前显示多少条
                var length = IDS.length;
                var pill = $("#exd_list");
                var qualified=0;
                for (var i = 0; i < length; i++) {
                    var $tr = pill.find("tr#"+IDS[i]);
                    var len=$tr.find("td").length;
                    
                    for(var j=len-1;j>=9;j--){
                        var $td=$tr.find("td:eq("+j+")");
                        if($td.text().trim()=='△' || $td.text().trim()=='×'){
                           $td.nextAll().each(function(index,ele){
                               $(ele).html('-');
                           });
                           break;
                        }
                    }
	                //当前几号
	                var cur_month=$("#hidden_currMonth").val();
	                for(var z=9;z<parseInt(cur_month)+5;z++){
	                    var $td=$tr.find("td:eq("+z+")");
	                    if($td.text().trim()==null || $td.text().trim()==''){
	                         $td.text("/");
	                    }
	                }
	             }
                
                //合格数
                var qualified=0;
                //不合格数
                var unqualified=0;
                // 当前显示多少条
                for(var i =0;i<listdata.length;i++){
                var jsonobj = listdata[i];
                var jsonArr=[jsonobj['april'],jsonobj['may'],jsonobj['june'],jsonobj['july'],jsonobj['august'],jsonobj['september'],
                			jsonobj['october'],jsonobj['november'],jsonobj['december'],jsonobj['january'],jsonobj['february'],jsonobj['march']
                			];
               
                for(var x=jsonArr.length;x>0;x--){
					if (jsonArr[x] == 1 || jsonArr[x] == 4) {
						qualified++;
						break;
					} else if (jsonArr[x] == 2) {
						unqualified++;
						break;
					}
                   }
                }
                //$("#qualified").text(qualified); 
                $("#unqualified").text(unqualified);
             }
        });
    }
};

/*编辑页面*/
var showEdit = function() {
     var row = $("#exd_list").jqGrid("getGridParam","selrow");//得到选中的行ID
     var rowData = $("#exd_list").getRowData(row);   
        $("#label_manage_code").text(rowData.manage_code);
        $("#label_tools_no").text(rowData.tools_no);
        $("#label_tools_name").text(rowData.tools_name);
        
        var data = {
                "manage_id":rowData.manage_id
        };
	    
        // Ajax提交
	    $.ajax({
	        beforeSend : ajaxRequestType,
	        async : true,
	        url : servicePath + '?method=detailSectionLinePosition',
	        cache : false,
	        data : data,
	        type : "post",
	        dataType : "json",
	        success : ajaxSuccessCheck,
	        error : ajaxError,
	        complete : detail_handleComplete
	    });
}

var detail_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
            var detailForms = resInfo.detailForms;
            
            $("#label_section_name").text(detailForms.section_name);
	        $("#label_line_name").text(detailForms.line_name);
	        $("#label_process_code").text((detailForms.process_code==null? "" : detailForms.process_code) +" "+(detailForms.position_name==null ? "" : detailForms.position_name));
            var data = {
                "manage_id":detailForms.tools_manage_id
	        };
	        // Ajax提交
	        $.ajax({
	            beforeSend : ajaxRequestType,
	            async : true,
	            url : servicePath + '?method=detailCheckResult',
	            cache : false,
	            data : data,
	            type : "post",
	            dataType : "json",
	            success : ajaxSuccessCheck,
	            error : ajaxError,
	            complete :function(xhrobj, textStatus) {
						    var resInfo = null;
						    try {
						        // 以Object形式读取JSON
						        eval('resInfo =' + xhrobj.responseText);
						        if (resInfo.errors.length > 0) {
						            // 共通出错信息框
						            treatBackMessages("#searcharea", resInfo.errors);
						        } else {
						            var resultForms = resInfo.resultForms;
						            if(resultForms==null || resultForms==""){
						            	 $("#label_manage_code").text("");
						                 $("#label_tools_no").text("");
						                 $("#label_tools_name").text("");
						                 $("#label_section_name").text("");
						     	         $("#label_line_name").text("");
						     	         $("#label_process_code").text("");
						            }else{
						            	detail_list(resultForms);
						            }
                                    
						        }
						    }catch (e) {};
						}
	        });
            
            $("#show_detail").dialog({
                position : 'center',
                title : "日常点检结果详细信息",
                width : 630,
                height : 600,
                resizable : false,
                modal : true,
                buttons : {
                    "取消":function(){
                        $("#show_detail").dialog('close');
                    }
                }
           });
        }
    }catch (e) {};
}

function detail_list(resultForms){
	if($("#gbox_detail_list").length > 0) {
        $("#detail_list").jqGrid().clearGridData();
        $("#detail_list").jqGrid('setGridParam',{data:resultForms}).trigger("reloadGrid", [{current:false}]);
    }else{
        $("#detail_list").jqGrid({
            data:resultForms,
            height: 300,
            width: 600,
            rowheight: 23,
            datatype: "local",
            colNames:['点检日期','点检者','点检状态'],
            colModel:[
                {name:'check_confirm_time',index:'check_confirm_time',align:'center'},
                {name:'operator',index:'operator'},
                {name:'checked_status',index:'checked_status',align:'center',formatter:'select',align:'center',
                    editoptions:{
                        value:$("#select_Checked_status").val()
                    }
                }
            ],
            toppager: false,
            pager: "#detail_listpager",
            viewrecords: true,
            pagerpos: 'right',
            pgbuttons: true,
            pginput: false,
            hidegrid: false, 
            recordpos:'left',
            viewsortcols : [ true, 'vertical', true ]
        });
    }
}
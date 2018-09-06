var servicePath="daily_check_result.do";
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
    
    $("#search_section_id,#search_line_id").select2Buttons();
    //品名
    setReferChooser($("#hidden_search_name"),$("#search_name_referchooser"));
    //工位
    setReferChooser($("#hidden_search_position_id"),$("#search_position_name_referchooser"));
    //管理员
    setReferChooser($("#hidden_search_manager_operator_id"),$("#search_operator_name_referchooser"));
    
    /*检索*/
    $("#searchbutton").click(function(){
          findit();   
    }); 
    
    /*清除*/
    $("#resetbutton").click(function(){
       $("#search_manage_code").val("");
       $("#search_model_name").val("");
       $("#search_check_item").val("");
       $("#search_name").val("");
       $("#hidden_search_name").val("");
       $("#search_section_id").val("").trigger("change").data("post","");
       $("#search_line_id").val("").trigger("change").data("post","");
       $("#search_operator_name").val("").trigger("change").data("post","");
       $("#search_position_id").val("").trigger("change").data("post","");
       $("#hidden_search_position_id").val("");
       $("#search_responsible_operator").val("");
       $("#search_manager_operator_id").val("");
       $("#hidden_search_manager_operator_id").val("");
    });
     findit();
})

/*检索按钮事件*/
var findit = function() {
    var data = {
            "manage_code":$("#search_manage_code").val(),
            "devices_type_id":$("#hidden_search_name").val(),
            "model_name":$("#search_model_name").val(),
            "section_id":$("#search_section_id").val(),
            "line_id":$("#search_line_id").val(),            
            "position_id":$("#hidden_search_position_id").val(),
            "manager_operator_id":$("#hidden_search_manager_operator_id").val()
        };
    // Ajax提交
    $.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath + '?method=search',
        cache : false,
        data : data,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : search_handleComplete
    });
    
    // doInit_ajaxSuccess();
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
            var maxDay=resInfo.maxDay;
            //判断当前月的所有天数--按照当前月天数显示数据
            $("#hidden_max_day").val(maxDay);
            if(maxDay==30){    
                $("#list").find("tr td[aria\\-describedby='list_thirty_one']").remove();
                $("#jqgh_list_thirty_one").remove();
            }
            if(maxDay==29){
                $("#list").find("tr td[aria\\-describedby='list_thirty'][aria\\-describedby='list_thirty_one']").remove();
                $("#jqgh_list_thirty,#jqgh_list_thirty_one").remove();
            }
            
            if(maxDay==28){
                $("#list").find("tr td[aria\\-describedby='list_twenty_nine'][aria\\-describedby='list_thirty'][aria\\-describedby='list_thirty_one']").remove();
                $("#jqgh_list_twenty_nine,#jqgh_list_thirty,#jqgh_list_thirty_one").remove();
            }
            filed_list(listdata,maxDay);
        }
    }catch (e) {};
}

/*jqgrid表格*/
function filed_list(finished){
    if ($("#gbox_list").length > 0) {
        $("#list").jqGrid().clearGridData();
        $("#list").jqGrid('setGridParam',{data:finished}).trigger("reloadGrid", [{current:false}]);
    } else {
        $("#list").jqGrid({
            data:finished,
            height: 461,
            width: 1248,
            rowheight: 23,
            datatype: "local",
            colNames : ['管理ID','分发课室ID','责任工程ID','责任工位ID','设备工具品名ID','管理编号','品名','型号','1', '2','3','4','5', '6','7', '8', '9', '10', 
                        '11', '12', '13','14','15','16','17','18','19','20',
                        '21','22','23','24','25','26','27','28','29','30','31'],
            colModel : [
                     {name:'manage_id',indedx:'manage_id',hidden:true},
                     {name:'section_id',indedx:'section_id',hidden:true},
                     {name:'line_id',indedx:'line_id',hidden:true},
                     {name:'position_id',indedx:'position_id',hidden:true},
                     {name:'devices_type_id',index:'devices_type_id', width:40,hidden:true},
                     {name:'manage_code',index:'manage_code',width:40},
                     {name:'name',index:'name', width:40},
                     {name:'model_name',index:'model_name', width:40},
                     {
                     name: 'one',
                        index: 'one',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                     },{
                        name : 'two',
                        index : 'two',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                     }, {
                        name : 'three',
                        index : 'three',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },{
                        name : 'four',
                        index : 'four',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'five',
                        index : 'five',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'six',
                        index : 'six',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'seven',
                        index : 'seven',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'eight',
                        index : 'eight',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'nine',
                        index : 'nine',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'ten',
                        index : 'ten',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'eleven',
                        index : 'eleven',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'twelve',
                        index : 'twelve',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }              
                    }, {
                        name : 'thirteen',
                        index : 'thirteen',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'fourteen',
                        index : 'fourteen',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'fiveteen',
                        index : 'fiveteen',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'sixteen',
                        index : 'sixteen',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'seventeen',
                        index : 'seventeen',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'eighteen',
                        index : 'eighteen',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'nineteen',
                        index : 'nineteen',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'twenty',
                        index : 'twenty',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'twenty_one',
                        index : 'twenty_one',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'twenty_two',
                        index : 'twenty_two',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }               
                    }, {
                        name : 'twenty_three',
                        index : 'twenty_three',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'twenty_four',
                        index : 'twenty_four',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'twenty_five',
                        index : 'twenty_five',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'twenty_six',
                        index : 'twenty_six',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'twenty_seven',
                        index : 'twenty_seven',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }, {
                        name : 'twenty_eight',
                        index : 'twenty_eight',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },  {
                        name : 'twenty_nine',
                        index : 'twenty_nine',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },{
                        name:'thirty',
                        index:'thirty',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    },{
                        name:'thirty_one',
                        index:'thirty_one',
                        align:'center',
                        sortable: false,
                        width:20,
                        formatter:'select',
                        editoptions:{
                            value:$("#select_Checked_status").val()
                        }
                    }
            ],
            rowNum : 50,
            toppager: false,
            pager: "#listpager",
            viewrecords: true,
            hidegrid : false,
            //caption: "日常点检一览",
            gridview: true,
            rownumbers : true,
            ondblClickRow : showEdit,
            pagerpos: 'right',
            pgbuttons: true,
            pginput: false,
            recordpos: 'left',          
            viewsortcols : [true,'vertical',true],
            gridComplete : function() {// 当表格所有数据都加载完成而且其他的处理也都完成时触发
                //当前月的天数
                var maxDay=$("#hidden_max_day").val();
                //判断当前月的所有天数--按照当前月天数显示数据
                if(maxDay==30){    
                    $("#list").find("tr td[aria\\-describedby='list_thirty_one']").remove();
                    $("#jqgh_list_thirty_one").remove();
                }
                if(maxDay==29){
                    $("#list").find("tr td[aria\\-describedby='list_thirty'][aria\\-describedby='list_thirty_one']").remove();
                    $("#jqgh_list_thirty,#jqgh_list_thirty_one").remove();
                }
                
                if(maxDay==28){
                    $("#list").find("tr td[aria\\-describedby='list_twenty_nine'][aria\\-describedby='list_thirty'][aria\\-describedby='list_thirty_one']").remove();
                    $("#jqgh_list_twenty_nine,#jqgh_list_thirty,#jqgh_list_thirty_one").remove();
                }
                
                /*****计算合格台数和不合格台数*******/
                //合格数
                var qualified=0;
                //不合格数
                var unqualified=0;
                // 当前显示多少条
                for(var i =0;i<finished.length;i++){
                var jsonobj = finished[i];
                var jsonArr=[jsonobj['one'],jsonobj['two'],jsonobj['three'],jsonobj['four'],jsonobj['five'],jsonobj['six'],
                			jsonobj['seven'],jsonobj['eight'],jsonobj['nine'],jsonobj['ten'],jsonobj['eleven'],jsonobj['twelve'],
                			jsonobj['thirteen'],jsonobj['fourteen'],jsonobj['fiveteen'],jsonobj['sixteen'],jsonobj['seventeen'],
                			jsonobj['eighteen'],jsonobj['nineteen'],jsonobj['twenty'],jsonobj['twenty_one'],jsonobj['twenty_two'],
                			jsonobj['twenty_three'],jsonobj['twenty_four'],jsonobj['twenty_five'],jsonobj['twenty_six'],
                			jsonobj['twenty_seven'],jsonobj['twenty_eight'],jsonobj['twenty_nine'],jsonobj['thirty'],jsonobj['thirty-one']
                            ];
               
                for(var x=jsonArr.length;x>0;x--){
                	if(jsonArr[x]==2 || jsonArr[x]==3){
                		unqualified++;
                         break;
                     }else if(jsonArr[x]==1 || jsonArr[x]==4){
                    	 qualified++;
                    	 break;
                     }
                   }
                }
                $("#qualified").text(qualified); 
                $("#unqualified").text(unqualified); 
                /*****计算合格台数和不合格台数*******/
                
                // 得到显示到界面的id集合
                var IDS = $("#list").getDataIDs();
                // 当前显示多少条
                var length = IDS.length;
                var pill = $("#list");
                
                for (var i = 0; i < length; i++) {
                    var $tr = pill.find("tr#"+IDS[i]);
                    var len=$tr.find("td").length;
                    
                    for(var j=len-1;j>=9;j--){
                        var $td=$tr.find("td:eq("+j+")");
                        if($td.text().trim()==2 || $td.text().trim()==3){
                           $td.nextAll().each(function(index,ele){
                               $(ele).html('-');
                           });
                           break;
                        }
                    }
                    
                //当前几号
                var cur_day=$("#hidden_currDay").val();
                for(var z=9;z<parseInt(cur_day)+8;z++){
                    var $td=$tr.find("td:eq("+z+")");
                    if($td.text().trim()==null || $td.text().trim()==''){
                         $td.text("/");
                    }
                 }
               }
             }
         });
     }
};
/*编辑页面*/
var showEdit = function() {
     var row = $("#list").jqGrid("getGridParam","selrow");//得到选中的行ID
     var rowData = $("#list").getRowData(row);   
        $("#label_manage_code").text(rowData.manage_code);
        $("#label_name").text(rowData.name);
        $("#label_model_name").text(rowData.model_name);
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
	        $("#label_responsible_line_name").text(detailForms.line_name);
	        $("#label_responsible_position_name").text(detailForms.process_code+"   "+detailForms.position_name);
            var data = {
                "manage_id":detailForms.devices_manage_id
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
						                $("#label_name").text("");
						                $("#label_model_name").text("");
						                $("#label_section_name").text("");
						    	        $("#label_responsible_line_name").text("");
						    	        $("#label_responsible_position_name").text("");
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
                height : 700,
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

function detail_list(finished){
        if($("#gbox_detail_list").length > 0) {
            $("#detail_list").jqGrid().clearGridData();
            $("#detail_list").jqGrid('setGridParam',{data:finished}).trigger("reloadGrid", [{current:false}]);
        }else{
            $("#detail_list").jqGrid({
                data:finished,
                height: 300,
                width: 600,
                rowheight: 23,
                datatype: "local",
                colNames:['点检日期','点检者','点检状态'],
                colModel:[
                    {name:'check_confirm_time',index:'check_confirm_time',align:'center'},
                    {name:'operator',index:'operator'},
                    {name:'checked_status',index:'checked_status',align:'center',formatter:'select',
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

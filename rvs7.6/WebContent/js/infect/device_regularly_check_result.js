var servicePath="device_regularly_check_result.do";


$(function(){
    $("#searcharea span.ui-icon,#listarea span.ui-icon").bind("click",function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().slideToggle("blind");
        } else {
            $(this).parent().parent().next().slideToggle("blind");
        }
    });
    
    /*按钮事件*/
    $("input.ui-button").button();
    
    $("#check_regularly").buttonset();
    
    //分发课室  责任工程
    $("#search_section_id,#search_responsible_line_id").select2Buttons();
    //责任工位
    setReferChooser($("#hidden_responsible_position_id"),$("#position_refer"));
    //责任人员
    setReferChooser($("#hidden_responsible_operator_id"),$("#responsible_operator_refer"));
    
    setReferChooser($("#hidden_devices_type_id"),$("#name_refer"));
    
    //检索
    $("#searchbutton").click(function(){
        $("#search_manage_code").data("post",$("#search_manage_code").val());//管理编号
        $("#hidden_devices_type_id").data("post",$("#hidden_devices_type_id").val());//品名
        $("#search_model_name").data("post",$("#search_model_name").val());//型号
        $("#search_section_id").data("post",$("#search_section_id").val());//分发课室
        $("#search_responsible_line_id").data("post",$("#search_responsible_line_id").val());//责任工程
        $("#hidden_responsible_position_id").data("post",$("#hidden_responsible_position_id").val());//责任工位
        $("#hidden_responsible_operator_id").data("post",$("#hidden_responsible_operator_id").val());//责任人员
        
       /* if($("#check_regularly input:checked").val()==1){
            
        }else if($("#check_regularly input:checked").val()==2){
            findit(); 
        }else{
            
        }*/
        
        findit(); 
        
       
    });
    
    //清除
    $("#resetbutton").click(function(){
        reset();
    });
    
    
    $("#week").click(function(){
        $("#check_week").show();
        $("#check_month").hide();
        $("#check_year").hide();
        $("#show_week").show();
        $("#show_month").hide();
        $("#show_year").hide();
    }); 
    
    $("#month").click(function(){
        $("#check_week").hide();
        $("#check_month").show();
        $("#check_year").hide();
        $("#show_week").hide();
        $("#show_month").show();
        $("#show_year").hide();
    });
    
    $("#year").click(function(){
        $("#check_week").hide();
        $("#check_month").hide();
        $("#check_year").show();
        $("#show_week").hide();
        $("#show_month").hide();
        $("#show_year").show();
    });
      
   
    
    reset();
    findit();
    
    $("#week").attr("checked","checked").trigger("change");
    $("#show_week").show();
    
});

//清除函数
var reset=function(){
      $("#search_manage_code").data("post","").val("");//管理编号
      $("#search_name").data("post","").val("");//品名
      $("#hidden_devices_type_id").val("");
      $("#search_model_name").data("post","").val("");//型号
      $("#search_section_id").data("post","").val("").trigger("change");//分发课室
      $("#search_responsible_line_id").data("post","").val("").trigger("change");//责任工程
      $("#hidden_responsible_position_id").data("post","").val("");//责任工位
      $("#search_responsible_position_name").val("");
      $("#hidden_responsible_operator_id").data("post","").val("");//责任人员
      $("#search_responsible_operator_name").val("");
};

var findit=function(){
    
    var data={
        "manage_code":$("#search_manage_code").data("post"),
        "devices_type_id":$("#hidden_devices_type_id").data("post"),
        "model_name":$("#search_model_name").data("post"),
        "section_id": $("#search_section_id").data("post"),
        "responsible_line_id":  $("#search_responsible_line_id").data("post"),
        "responsible_position_id": $("#hidden_responsible_position_id").data("post"),
        "responsible_operator_id": $("#hidden_responsible_operator_id").data("post")
    };
    
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
};

var search_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
           device_regularly_check_result_month_list(resInfo.month_finished,resInfo.curMonth);
           device_regularly_check_result_year_list(resInfo.year_finished)
           device_regularly_check_result_week_list(resInfo.week_finished,resInfo.weeks,resInfo.cur_week)
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};


//月
var device_regularly_check_result_month_list=function(month_datalist,curMonth){
    if($("#gbox_month_list").length>0){
        $("#month_list").jqGrid().clearGridData();
        $("#month_list").jqGrid("setGridParam",{data:month_datalist}).trigger("reloadGrid",[{current:false}]);
    }else{
         $("#month_list").jqGrid({
            data:month_datalist,
            height: 390,
            width: 1248,
            rowheight: 23,
            datatype: "local",
            colNames:['','管理编号','品名','型号','4月','5月','6月','7月','8月','9月','10月','11月','12月','1月','2月','3月'],
            colModel:[
                {name:'manage_id',index:'manage_id',hidden:true},
                {name:'manage_code',index:'manage_code',width:90},
                {name:'name',index:'name',width:90},
                {name:'model_name',index:'model_name',width:90},
                {name:'april_checked_status',index:'april_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'may_checked_status',index:'may_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'june_checked_status',index:'june_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'july_checked_status',index:'july_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'august_checked_status',index:'august_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'september_checked_status',index:'september_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'october_checked_status',index:'october_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'november_checked_status',index:'november_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'december_checked_status',index:'december_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'janurary_checked_status',index:'janurary_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'february_checked_status',index:'february_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'march_checked_status',index:'march_checked_status',width:30,sortable:false,formatter:'select',align:'center',
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                }
            ],
            rownumbers:true,
            toppager : false,
            rowNum : 20,
            pager : "#month_listpager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true,
            pginput : false,
            recordpos : 'left',
            ondblClickRow:function(rowid,iRow,iCol,e){
                var rowData=$("#month_list").getRowData(rowid);
                var manage_id=rowData.manage_id;
                showDetail(manage_id,2);
            },
            viewsortcols : [true, 'vertical', true],
            gridComplete:function(){
                var IDS=$("#month_list").getDataIDs();
                var pill = $("#month_list");
                var length=IDS.length;
                
                for(var i=0;i<length;i++){
                    var $tr = pill.find("tr#"+IDS[i]);
                    var len=$tr.find('td').length;
                    for(var j=len-1;j>=5;j--){
                        var $td = $tr.find("td:eq("+j+")");
                        if($td.text().trim()=="×" || $td.text().trim()=="△"){
                            $td.nextAll().each(function(iNext,eNext){
                                $(eNext).text("-");
                           });
                           break;
                        }else if($td.text().trim()=="○" || $td.text().trim()=="●"){
                             break;
                        }
                    }
                    
                  
                    if(curMonth<=3){
                        curMonth=curMonth+13;
                    }else if(curMonth>3 && curMonth<=12){
                    	curMonth=curMonth+1
                    }
                    
                    for(var z=5;z<curMonth;z++){
	                    var $td=$tr.find("td:eq("+z+")");
	                    if($td.text().trim()==null || $td.text().trim()==''){
	                            $td.text("/");
	                    }
                    }
                }
                
                 var unqualified=0;
                 var qualified=0;
                 for(var i =0;i<month_datalist.length;i++){
	                 var jsonobj = month_datalist[i];
                     var jsonArr=[jsonobj['april_checked_status'],
                                  jsonobj['may_checked_status'],
                                  jsonobj['june_checked_status'],
                                  jsonobj['july_checked_status'],
                                  jsonobj['august_checked_status'],
                                  jsonobj['september_checked_status'],
                                  jsonobj['october_checked_status'],
                                  jsonobj['november_checked_status'],
                                  jsonobj['december_checked_status'],
                                  jsonobj['janurary_checked_status'],
                                  jsonobj['february_checked_status'],
                                  jsonobj['march_checked_status']
                     ];
                     
	                 for(var x=jsonArr.length-1;x>=0;x--){
                       if(jsonArr[x]==1 || jsonArr[x]==4){
	                        qualified++;
	                        break;
                       }else if(jsonArr[x]==2 || jsonArr[x]==3){
                            unqualified++;
                            break;
                       }
	                 }
                }
                
               $("#show_month").find("label:eq(0)").text(qualified);
               $("#show_month").find("label:eq(1)").text(unqualified);
            }
         });
    }
};


//年
var device_regularly_check_result_year_list=function(year_datalist){
     if($("#gbox_year_list").length>0){
        $("#year_list").jqGrid().clearGridData();
        $("#year_list").jqGrid("setGridParam",{data:year_datalist}).trigger("reloadGrid",[{current:false}]);
    }else{
        $("#year_list").jqGrid({
            data:year_datalist,
	        height: 390,
	        width: 1248,
	        rowheight: 23,
	        datatype: "local",
	        colNames:['','管理编号','品名','型号','上半年','下半年'], 
            colModel:[
                {name:'manage_id',index:'manage_id',hidden:true},
                {name:'manage_code',index:'manage_code',width:200},
                {name:'name',index:'name',width:200},
                {name:'model_name',index:'model_name',width:200},
                {name:'upper_half_year_status',index:'upper_half_year_status',width:30,sortable:false,formatter:'select',align:'center',width:200,
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                {name:'lower_half_year_status',index:'lower_half_year_status',width:30,sortable:false,formatter:'select',align:'center',width:200,
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                }
            ],
	        rownumbers:true,
	        toppager : false,
	        rowNum : 20,
	        pager : "#year_listpager",
	        viewrecords : true,
	        gridview : true,
	        pagerpos : 'right',
	        pgbuttons : true,
	        pginput : false,
	        recordpos : 'left',
	        ondblClickRow:function(rowid,iRow,iCol,e){
                var rowData=$("#year_list").getRowData(rowid);
                var manage_id=rowData.manage_id;
                showDetail(manage_id,2);
            },
	        viewsortcols : [true, 'vertical', true],
	        gridComplete:function(){
	            var IDS=$("#year_list").getDataIDs();
                var pill = $("#year_list");
                var length=IDS.length;
                
                 for(var i=0;i<length;i++){
                    var $tr = pill.find("tr#"+IDS[i]);
                    var $td = $tr.find("td:eq(5)");
                    if($td.text().trim()=="×" || $td.text().trim()=="△"){
                        $td.next().html('-');
                    }
                     
                    var date=new Date();
                    var now_month=date.getMonth()+1;//当前月
                   
                    if(now_month>=10 || now_month<4){
                        var $td=$tr.find("td:eq(5)");
                        if($td.text().trim()==null || $td.text().trim()==''){
                            $td.text("/");
                        }
                    }
                     
                 }
                 
                 
                 var unqualified=0;
                 var qualified=0;
                 for(var i =0;i<year_datalist.length;i++){
                     var jsonobj = year_datalist[i];
                     var jsonArr=[jsonobj['upper_half_year_status'],
                                  jsonobj['lower_half_year_status']
                     ];
             
                     for(var x=jsonArr.length-1;x>=0;x--){
                       if(jsonArr[x]==1 || jsonArr[x]==4){
                            qualified++;
                            break;
                       }else if(jsonArr[x]==2 || jsonArr[x]==3){
                            unqualified++;
                            break;
                       }
                     }
                }
        
               $("#show_year").find("label:eq(0)").text(qualified);
               $("#show_year").find("label:eq(1)").text(unqualified);
                 
                 
                 
                 
	        }
        });
       
    }
};

//周
var device_regularly_check_result_week_list=function(week_datalist,weeks,cur_week){
    if($("#gbox_week_list").length > 0) {
        $("#week_list").jqGrid().clearGridData();
        $("#week_list").jqGrid('setGridParam',{data:week_datalist}).trigger("reloadGrid", [{current:false}]);
    }else{
	    $("#week_list").jqGrid({
	        data:week_datalist,
	        height: 390,
	        width: 1248,
	        rowheight: 23,
	        datatype: "local",
	        colNames:['','管理编号','品名','型号','第一周','第二周','第三周','第四周','第五周','第六周'],
	        colModel:[
	            {name:'manage_id',index:'manage_id',hidden:true},
                {name:'manage_code',index:'manage_code',width:200},
                {name:'name',index:'name',width:200},
                {name:'model_name',index:'model_name',width:200},
                {name:'one_week_status',index:'one_week_status',sortable:false,formatter:'select',width:30,align:'center',width:200,
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                 {name:'two_week_status',index:'two_week_status',sortable:false,formatter:'select',width:30,align:'center',width:200,
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                 {name:'three_week_status',index:'three_week_status',sortable:false,formatter:'select',width:30,align:'center',width:200,
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                 {name:'four_week_status',index:'four_week_status',sortable:false,formatter:'select',width:30,align:'center',width:200,
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                 {name:'five_week_status',index:'five_week_status',sortable:false,formatter:'select',width:30,align:'center',width:200,
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                },
                 {name:'six_week_status',index:'six_week_status',sortable:false,formatter:'select',width:30,align:'center',width:200,
                    editoptions:{
                        value:$("#sChecked_status").val()
                    }
                }],
				rownumbers:true,
				toppager : false,
		        rowNum : 20,
		        pager : "#week_listpager",
				viewrecords : true,
				gridview : true,
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				ondblClickRow:function(rowid,iRow,iCol,e){
				    var rowData=$("#week_list").getRowData(rowid);
				    var manage_id=rowData.manage_id;
				    showDetail(manage_id,1);
				},
				viewsortcols : [true, 'vertical', true],
                gridComplete:function(){
                         var tname;
                         if(weeks==4){
                            tname=['five_week_status','six_week_status'];
                            $("#week_list").jqGrid('hideCol',tname);
                         }else if(weeks==5){
                            tname=['six_week_status']
                            $("#week_list").jqGrid('hideCol',tname);
                         }
                         $("#week_list").jqGrid('setGridWidth', '1248');
                         
                         
                         var IDS=$("#week_list").getDataIDs();
			             var pill = $("#week_list");
			             var length=IDS.length;
                         
                         for(var i=0;i<length;i++){
                                var $tr = pill.find("tr#"+IDS[i]);
			                    var len=$tr.find('td').length;
			                    for(var j=len-1;j>=5;j--){
                                    var $td = $tr.find("td:eq("+j+")");
			                        if($td.text().trim()=="×" || $td.text().trim()=="△"){
			                            $td.nextAll().each(function(iNext,eNext){
			                                $(eNext).text("-");
			                           });
			                           break;
					                 }else if($td.text().trim()=="○" || $td.text().trim()=="●"){
		                                  break;
		                             }
                                }
                                
			                    
			                    for(var z=5;z<cur_week+4;z++){
			                        var $td=$tr.find("td:eq("+z+")");
			                        if($td.text().trim()==null || $td.text().trim()==''){
			                                $td.text("/");
			                        }
			                    }
                         }
                         
                         var unqualified=0;
		                 var qualified=0;
		                 for(var i =0;i<week_datalist.length;i++){
		                     var jsonobj = week_datalist[i];
		                     var jsonArr=[jsonobj['one_week_status'],
		                                  jsonobj['two_week_status'],
		                                  jsonobj['three_week_status'],
		                                  jsonobj['four_week_status'],
		                                  jsonobj['five_week_status'],
		                                  jsonobj['six_week_status']
		                                  
		                     ];
                     
		                     for(var x=jsonArr.length-1;x>=0;x--){
		                       if(jsonArr[x]==1 || jsonArr[x]==4){
		                            qualified++;
		                            break;
		                       }else if(jsonArr[x]==2 || jsonArr[x]==3){
		                            unqualified++;
		                            break;
		                       }
		                     }
                        }
                
		               $("#show_week").find("label:eq(0)").text(qualified);
		               $("#show_week").find("label:eq(1)").text(unqualified);
                         
                         
                }
	    });
    }
}

var  showDetail=function(manage_id,type){
    var data={
       "manage_id":manage_id,
       "type":type
    };
    
    $.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath + '?method=detail',
        cache : false,
        data : data,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : detail_handleComplete
    });
};

var detail_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
            if(resInfo.returnForm!=null && resInfo.returnForm!=''){
                 $("#label_manage_code").text(resInfo.returnForm.manage_code);
		         $("#label_section_name").text(resInfo.returnForm.section_name);
		         $("#label_name").text(resInfo.returnForm.name);
		         $("#label_responsible_line_name").text(resInfo.returnForm.responsible_line_name);
		         $("#label_model_name").text(resInfo.returnForm.model_name);
		         $("#label_responsible_position_name").text(resInfo.returnForm.responsible_position_name);
            }else{
                $("#label_manage_code").text("");
	            $("#label_section_name").text("");
	            $("#label_name").text("");
	            $("#label_responsible_line_name").text("");
	            $("#label_model_name").text("");
	            $("#label_responsible_position_name").text("");
            }
            
            
            
            if($("#gbox_detail_list").length > 0) {
		        $("#detail_list").jqGrid().clearGridData();
		        $("#detail_list").jqGrid('setGridParam',{data:resInfo.finished}).trigger("reloadGrid", [{current:false}]);
		    }else{
		        $("#detail_list").jqGrid({
		            data:resInfo.finished,
		            height: 300,
		            width: 600,
		            rowheight: 23,
		            datatype: "local",
		            colNames:['点检日期','点检者','点检状态'],
		            colModel:[
		                {name:'check_confirm_time',index:'check_confirm_time',align:'center',formatter:'date',
	                        formatoptions:{
                                    srcformat: 'Y/m/d',
                                    newformat: 'Y-m-d'
	                        }
                        },
		                {name:'responsible_operator_name',index:'responsible_operator_name'},
		                {name:'checked_status',index:'checked_status',formatter:'select',align:'center',
		                    editoptions:{
		                        value:$("#sChecked_status").val()
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
		            viewsortcols : [ true, 'vertical', true ],
                    gridComplete:function(){
                          var IDS=$("#detail_list").getDataIDs();
			              var pill = $("#detail_list");
			              var length=IDS.length;
                          
                          for (var i = 0; i < length; i++) {
                             // 从上到下获取一条信息
                             var rowData = pill.jqGrid('getRowData', IDS[i]);
                             var checked_status=rowData.checked_status;
                             if(checked_status==null || checked_status==""){
                                   pill.find("tr#" +IDS[i] +  " td[aria\\-describedby='detail_list_checked_status']").html('/');
                             }
                          }
                    }
		        });
		    }
            
            $("#show_detail").dialog({
	            position : 'center',
	            title : "设备工具定期点检详细信息",
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
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};











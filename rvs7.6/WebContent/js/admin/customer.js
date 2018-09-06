var servicePath="customer.do";
var listdata=[];
var original_customer_id;
$(function(){
	$("#searcharea span.ui-icon,#listarea span.ui-icon").bind("click",function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().slideToggle("blind");
        } else {
            $(this).parent().parent().next().slideToggle("blind");
        }
    });
    
    $("input.ui-button").button();
    
    $("#search_ocm,#add_ocm,#update_ocm").select2Buttons();
    $(".ui-buttonset").buttonset();

    
    /*检索*/
    $("#searchbutton").click(function(){
    	$("#search_name").data("post",$("#search_name").val());//客户名称
    	$("#search_ocm").data("post",$("#search_ocm").val());//所属分室
    	$("#search_vip input[type='radio']:checked").data("post",$("#search_vip input[type='radio']:checked").val());//优先对应客户
    	findit();
    });
    
    /*清除*/
    $("#resetbutton").click(function(){
    	reset();
    });
    
    reset();
    findit();
    $("#merge").disable();
});

/*清除函数*/
var reset=function(){
	$("#search_name").data("post","").val("");//客户名称
	$("#search_ocm").data("post","").val("").trigger("change");//所属分室
	$("#search_vip input[type='radio']:eq(0)").attr("checked", "checked").trigger("change");//优先对应客户
};

/*检索函数*/
var findit=function(){
	var data={
		"name":$("#search_name").data("post"),
		"ocm":$("#search_ocm").data("post"),
		"vip":$("#search_vip input[type='radio']:checked").data("post")
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
		complete :search_handleComplete
	});
};

//检索完成函数
var search_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
            customer_list(resInfo.finished);
            $("#merge").disable();
        }
    } catch (e) {
        console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

var customer_list=function(listdata){
	if($("#gbox_list").length > 0) {
        $("#list").jqGrid().clearGridData();
        $("#list").jqGrid("setGridParam",{data:listdata}).trigger("reloadGrid",[{current:false}]);
    }else{
    	$("#list").jqGrid({
    		  data:listdata,
              height: 461,
              width: 992,
              rowheight: 23,
              datatype: "local",
              colNames:['','客户名称','所属分室','优先对应客户','最后更新人','最后更新时间',''],
              colModel:[
                    {name:'customer_id',index:'customer_id',hidden:true},
                    {name:'name',index:'name',width:200},
                    {name:'ocm',index:'ocm',width:40,align:'center',formatter:'select',
                      editoptions:{
                          value:$("#sMaterial_ocm").val()
                      }
                    },
                    {name:'vip',index:'vip',width:30,align:'center',
                		formatter:function(value, options, rData){
                			if(value==0){
                                return "否";
    	                    }else{
    	                        return "是";
    	                    }     
                		}
                    },
                    {name:'operation_name',index:'operation_name',width:40},
                    {name:'updated_time',index:'updated_time',width:45},
                    {name:'hide_vip',index:'hide_vip',hidden:true,formatter:function(value, options, rData){return rData.vip}}
              ],
              rownumbers:true,
              toppager : false,
              pager : "#listpager",
              viewrecords : true,
              gridview : true,
              pagerpos : 'right',
              pgbuttons : true, 
              pginput : false,
              recordpos : 'left',
              hidegrid : false,
              onSelectRow:enableButton,
              ondblClickRow : showDetail,
              viewsortcols : [true,'vertical',true]    
    	});
    	
    	$(".ui-jqgrid-hbox").before(
            '<div class="ui-widget-content" style="padding:4px;">' +
                '<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建客户" role="button" aria-disabled="false">' +
            '</div>'
        );
        $("input.ui-button").button(); 
        $("#addbutton").click(showAdd);
    }
};


var enableButton=function(){
	$("#merge").enable();
	
	var rowId=$("#list").jqGrid("getGridParam","selrow");
    var rowData=$("#list").getRowData(rowId);
    original_customer_id=rowData.customer_id;//归并源客户ID
    
	$("#merge").unbind("click");
	$("#merge").bind("click",function(){
		$("#merge_search_name").val("");
		$("#executebutton").disable();
		merge_list("");
		
		$("#merge_dialog").dialog({
	    	position : 'center',
            title : "归并",
            width :450,
            height : 600,
            resizable : false,
            modal : true
	     });
	});
	
	//查询归并目标
	$("#searchbutton2").unbind("click");
	$("#searchbutton2").bind("click",function(){
		var data={
        	"name":$("#merge_search_name").val(),
        	"customer_id":original_customer_id
        };
		 $.ajax({
             beforeSend : ajaxRequestType,
             async : true,
             url : servicePath + '?method=searchMergeTarget',
             cache : false,
             data : data,
             type : "post",
             dataType : "json",
             success : ajaxSuccessCheck,
             error : ajaxError,
             complete : search_merge_target_handleComplete
         });
		
	});
	
	/*取消*/
	$("#cancelbutton3").unbind("click");
	$("#cancelbutton3").bind("click",function(){
		$("#merge_dialog").dialog('close');
	});
	
	/*实行归并*/
	$("#executebutton").unbind("click");
	$("#executebutton").bind("click",function(){
		var mergerowId=$("#merge_list").jqGrid("getGridParam","selrow");
	    var mergerowData=$("#merge_list").getRowData(mergerowId);
	    
	    
	     $("#confirmmessage").html("");
	     $("#confirmmessage").html("是否将 ["+rowData.name+"] 归并为 ["+mergerowData.name+"]");
	     $("#confirmmessage").dialog({
	       position : 'center',
	       title : "归并确认",
	       width :350,
	       height : 150,
	       resizable : false,
	       modal : true,
	       buttons : {
	           "确定":function(){
	        	   var data={
	       	    		"original_customer_id":original_customer_id,//归并源客户ID
	       	    		"original_vip":rowData.hide_vip,
	       	    		"target_customer_id":mergerowData.customer_id,//归并目标客户ID
	       	    		"targer_vip":mergerowData.hide_vip
	       	    	};
	           	  // Ajax提交
		       	    $.ajax({
		                   beforeSend : ajaxRequestType,
		                   async : true,
		                   url : servicePath + '?method=doMerge',
		                   cache : false,
		                   data : data,
		                   type : "post",
		                   dataType : "json",
		                   success : ajaxSuccessCheck,
		                   error : ajaxError,
		                   complete : do_merge_handleComplete
		            });
	           },
	           "取消":function(){
	               $("#confirmmessage").html("");
	               $("#confirmmessage").dialog('close');
	           }
	       }
	     });
	});
	
};

var do_merge_handleComplete=function(xhrobj, textStatus){
	var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
        	findit();
        	$("#confirmmessage").dialog('close');
        	$("#merge_dialog").dialog('close');
        }
    } catch (e) {
        console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};


var search_merge_target_handleComplete=function(xhrobj, textStatus){
	var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
        	merge_list(resInfo.finished);
        }
    } catch (e) {
        console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

/*归并目标一览*/
var merge_list=function(listdata){
	if($("#gbox_merge_list").length > 0) {
        $("#merge_list").jqGrid().clearGridData();
        $("#merge_list").jqGrid("setGridParam",{data:listdata}).trigger("reloadGrid",[{current:false}]);
    }else{
    	$("#merge_list").jqGrid({
    		  data:listdata,
              height: 361,
              width: 424,
              rowheight: 15,
              datatype: "local",
              colNames:['','客户名称','所属分室','优先对应客户',''],
              colModel:[
                    {name:'customer_id',index:'customer_id',hidden:true},
                    {name:'name',index:'name',width:100},
                    {name:'ocm',index:'ocm',width:30,align:'center',formatter:'select',
                      editoptions:{
                          value:$("#sMaterial_ocm").val()
                      }
                    },
                    {name:'vip',index:'vip',width:40,align:'center',
                		formatter:function(value, options, rData){
                			if(value==0){
                                return "否";
    	                    }else{
    	                        return "是";
    	                    }     
                		}
                    },
                    {name:'hide_vip',index:'hide_vip',hidden:true,formatter:function(value, options, rData){return rData.vip}}
              ],
              rownumbers:true,
              toppager : false,
              pager : "#merge_listpager",
              viewrecords : true,
              gridview : true,
              pagerpos : 'right',
              pgbuttons : true, 
              pginput : false,
              recordpos : 'left',
              hidegrid : false,
              onSelectRow:function(){
            	  $("#executebutton").enable();
              },
              viewsortcols : [true,'vertical',true]    
    	});
    }
}

/*新建客户*/
var showAdd=function(){
	$("#main").hide();
	$("#add").show();
	
	$("#add_name").val("").removeClass("errorarea-single");//客户名称
	$("#add_ocm").val("").trigger("change");//所属科室
	$("#add_vip input[type='radio']:eq(1)").attr("checked", "checked").trigger("change");//优先对应客户
	
	//表单验证
	$("#add_form").validate({
        rules:{
        	name:{
                  required:true,
                  maxlength:100
             }
        }
    });
	
	/*确认*/
	$("#confirmbutton").unbind("click");
	$("#confirmbutton").bind("click",function(){
		if($("#add_form").valid()){
			 $("#confirmmessage").html("");
		     $("#confirmmessage").html("是否新建客户名称为: "+$("#add_name").val()+"的客户?");
		     $("#confirmmessage").dialog({
		    	position : 'center',
	            title : "新建确认",
	            width :350,
	            height : 150,
	            resizable : false,
	            modal : true,
	            buttons : {
	                "确定":function(){
	                	var data={
	                		"name":$("#add_name").val(),
	                		"ocm":$("#add_ocm").val(),
	                		"vip":$("#add_vip input[type='radio']:checked").val()
	                	};
	                	  // Ajax提交
	                    $.ajax({
	                        beforeSend : ajaxRequestType,
	                        async : true,
	                        url : servicePath + '?method=doInsert',
	                        cache : false,
	                        data : data,
	                        type : "post",
	                        dataType : "json",
	                        success : ajaxSuccessCheck,
	                        error : ajaxError,
	                        complete : insert_handleComplete
	                    });
	                },
	                "取消":function(){
                        $("#confirmmessage").html("");
                        $("#confirmmessage").dialog('close');
	                }
	            }
		     });
		}
	});
	
	
	/*取消*/
	$("#cancelbutton,#add span.ui-icon").unbind("click");
	$("#cancelbutton,#add span.ui-icon").bind("click",function(){
		$("#main").show();
		$("#add").hide();
	});
};

var insert_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
        	$("#confirmmessage").dialog('close');
        	$("#main").show();
    		$("#add").hide();
            findit();
        }
    }catch (e) {};
};

/*更新客户信息*/
var showDetail=function(){
	var rowId=$("#list").jqGrid("getGridParam","selrow");
    var rowData=$("#list").getRowData(rowId);
    
    $("#main").hide();
    $("#update").show();
    
    $("#update_name").val(rowData.name).removeClass("errorarea-single");//客户名称
    $("#update_ocm").val(rowData.ocm).trigger("change");//所属分室
    
    var vip=rowData.hide_vip;
    if(vip==0){
    	$("#update_vip_c").attr("checked", "checked").trigger("change");//优先对应客户
    }else{
    	$("#update_vip_b").attr("checked", "checked").trigger("change");//优先对应客户
    }
  
    $("#lable_operation_name").text(rowData.operation_name);//最后更新人
    $("#lable_updated_time").text(rowData.updated_time);//最后更新时间
    
    //表单验证
    $("#update_form").validate({
        rules:{
        	name:{
                  required:true,
                  maxlength:100
             }
        }
    });
    
    /*修改*/
    $("#updatebutton").unbind("click");
    $("#updatebutton").bind("click",function(){
    	if($("#update_form").valid()){
    		 $("#confirmmessage").html("");
		     $("#confirmmessage").html("是否更新客户名称为: "+$("#update_name").val()+"的客户?");
		     $("#confirmmessage").dialog({
			    	position : 'center',
		            title : "新建确认",
		            width :350,
		            height : 150,
		            resizable : false,
		            modal : true,
		            buttons : {
		                "确定":function(){
		                	var data={
		                		"name":$("#update_name").val(),
		                		"ocm":$("#update_ocm").val(),
		                		"vip":$("#update_vip input[type='radio']:checked").val(),
		                		"customer_id":rowData.customer_id
		                	};
		                	  // Ajax提交
		                    $.ajax({
		                        beforeSend : ajaxRequestType,
		                        async : true,
		                        url : servicePath + '?method=doUpdate',
		                        cache : false,
		                        data : data,
		                        type : "post",
		                        dataType : "json",
		                        success : ajaxSuccessCheck,
		                        error : ajaxError,
		                        complete : update_handleComplete
		                    });
		                },
		                "取消":function(){
	                        $("#confirmmessage").html("");
	                        $("#confirmmessage").dialog('close');
		                }
		            }
			     })
    	}
    });
    	
    /*取消*/
    $("#cancelbutton2,#update span.ui-icon").unbind("click");
    $("#cancelbutton2,#update span.ui-icon").bind("click",function(){
    	 $("#main").show();
    	 $("#update").hide();
    });
};

var update_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
        	$("#confirmmessage").dialog('close');
        	$("#main").show();
    		$("#update").hide();
            findit();
        }
    }catch (e) {};
};
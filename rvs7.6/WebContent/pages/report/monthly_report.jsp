<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<title>SORC 月报</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
				<jsp:include page="/header.do?method=init" flush="true">
				<jsp:param name="part" value="2" />
				</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px; width: 1266px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=init" flush="true">
					<jsp:param name="linkto" value="文档管理"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				
				<div id="listarea">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">月报下载</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div>
						<table style="width:100%">
						<tr>
						<td>
							<table id="report_list"></table>
							<div id="report_listpager"></div>
						</td>
						<td>
							<table id="detail_list"></table>
							<div id="detail_listpager"></div>
						</td>
						</tr>
						<tr>
						<td>
							<table id="overwork_list"></table>
							<div id="overwork_listpager"></div>
						</td>
						<td>
							<table id="af_report_list"></table>
							<div id="af_report_listpager"></div>
						</td>
						</tr>
						</table>
					</div>
				</div>
				
			</div>
			<div class="clear"></div>
		</div>
		
	</div>
</body>
</html>
<script type="text/javascript">

function initMonthFilesGrid(listName, listdata){
	$("#" + listName).jqGrid({
		toppager : true,
		data : listdata,
		height : 400,
		width : 480,
		rowheight :20,
		datatype : "local",
		colNames:['文件名','文件大小'],
		colModel:[
		         {name:'file_name',index:'file_name',width:100,align:'left',
		            	formatter : function(value, options, rData){
							return "<a href='javascript:downExcel(\"" + rData['file_name'] + "\");' >" + rData['file_name'] + "</a>";
		   				}},
		         {name:'file_size',index:'file_size',width:40,align:'right'}
		         ],
        rowNum : 50,
 		toppager : false,
 		pager : "#" + listName + "pager",
 		viewrecords : true,
 		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true]
	});
}

/*下载*/
function downExcel(file_name) {
	if (file_name.indexOf(" ") >= 0) {
		file_name = file_name.replace(" ", "%20")
	}
	if ($("iframe").length > 0) {
		$("iframe").attr("src","operatorProduction.do?method=output&fileName="+ file_name);
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "operatorProduction.do?method=output&fileName="+ file_name;
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}

function search_files_handleComplete(xhrobj, textStatus){
	var resInfo = null;

	// 以Object形式读取JSON
	eval('resInfo =' + xhrobj.responseText);
	
	//月档案详细显示
	initMonthFilesGrid("report_list", resInfo.reportList);	
	initMonthFilesGrid("detail_list", resInfo.detailList);	
	initMonthFilesGrid("overwork_list", resInfo.overworkList);	
	initMonthFilesGrid("af_report_list", resInfo.afReportList);
}

//月报表显示详细
$.ajax({
	beforeSend : ajaxRequestType,
	async : false,
	url : 'operatorProduction.do?method=searchMonthFiles',
	cache : false,
	data : {"nest": "1"},
	type : "post",
	dataType : "json",
	success : ajaxSuccessCheck,
	error : ajaxError,
	complete : search_files_handleComplete
});	

</script>

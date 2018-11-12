
fileDetail = {
    tableInit:function(){
    	//详情列表初始化
    	var listInit = {
    			url: "/classinfo/getFileDetail?"+Math.random(),
    			method: 'post',
    			contentType : "application/x-www-form-urlencoded",
    			dataType: "json",
    			pagination: true,
    			showColumns: false,
    			iconSize: "outline",
    			sidePagination: "client",
                pageNumber: 1,
                queryParams : function(params) {
                    return {
                    	messageType: $("#fileDetialMessageType").val()
                        };
                },
                pageSize: 10,
                pageList: [10, 25, 50, 100, 200],
    			icons: {
    				refresh: "glyphicon-repeat",
    				toggle: "glyphicon-list-alt",
    				columns: "glyphicon-list",
    			},
    			striped:true,  //是否行间隔色显示
    			sortable: true,//是否启用排序
                sortOrder: "asc",//排序方式
    			clickToSelect:true, //是否启用点击选中行
    			columns:[{field:'messageNo',title:'策略ID'},
    				{field:'mainPolicy',title:'<span title="策略成功数/异常数">策略成功数/异常数</span>',width:'20%',
                	formatter:function(value,row,index){
                		if(value=="0/0"){
                			return "0/0";
                		}else{
                			return "<a href='#' onclick='getPolicyDetail("+index+",0)' title='详情'>"+value+"</a>";
                		}
                	}
    				},
    				{field:'server',title:'<span title="生成列表服务器">生成列表服务器</span>',formatter:function(value,row,index){
    					return "<span title='"+value+"'>"+value+"</span>";
    				}},
    		        {field:'fileName',title:'文件名',formatter:function(value,row,index){
    		        	return '<a title="'+value+'"href="/classinfo/downLoadFile?fileName='+value+'&messageType='+row.messageType+'" target="_self">'+value+'</a>';
    		        }},
    		        {field:'createTime',title:'生成日期',formatter:function(value,row,index){
    					return "<span title='"+value+"'>"+value+"</span>";
    				}},
    		        {title:'操作',
    		         field:'deviceType',
    		         formatter:function(value,row,index){
                         var fileDetailType = $("#fileDetialMessageType").val();
                         var format = "";
                         if (fileDetailType==200){
                             if ($("#zf104002_redo").val()==1){
                                 format += "<a href='#' title='重发' onclick='reSend("+index+",1)' class='m-r'><i class='fa fal fa-share resend'></i></a>";
                             } else {
                                 format += "<a href='#' style='display:none;' title='重发' onclick='reSend("+index+",1)' class='m-r'><i class='fa fal fa-share resend'></i></a>";
                             }
                         } else if (fileDetailType==202) {
                             if ($("#zf104003_redo").val()==1){
                                 format += "<a href='#' title='重发' onclick='reSend("+index+",1)' class='m-r'><i class='fa fal fa-share resend'></i></a>";
                             } else {
                                 format += "<a href='#' style='display:none;' title='重发' onclick='reSend("+index+",1)' class='m-r'><i class='fa fal fa-share resend'></i></a>";
                             }
                         } else if (fileDetailType==201) {
                             if ($("#zf104004_redo").val()==1){
                                 format += "<a href='#' title='重发' onclick='reSend("+index+",1)' class='m-r'><i class='fa fal fa-share resend'></i></a>";
                             } else {
                                 format += "<a href='#' style='display:none;' title='重发' onclick='reSend("+index+",1)' class='m-r'><i class='fa fal fa-share resend'></i></a>";
                             }
                         } else if (fileDetailType==207) {
                             if ($("#zf104005_redo").val()==1){
                                 format += "<a href='#' title='重发' onclick='reSend("+index+",1)' class='m-r'><i class='fa fal fa-share resend'></i></a>";
                             } else {
                                 format += "<a href='#' style='display:none;' title='重发' onclick='reSend("+index+",1)' class='m-r'><i class='fa fal fa-share resend'></i></a>";
                             }
                         }
                         return format;
					 }}],
				onLoadSuccess: function(data){  //加载成功时执行
                    $("#detialList").bootstrapTable('mergeCells', {index: 0, field: 'messageNo', colspan: 1, rowspan: 3});
                    $("#detialList").bootstrapTable('mergeCells', {index: 0, field: 'mainPolicy', colspan: 1, rowspan: 3});
				}
    		};
        $("#detialList").bootstrapTable('destroy').bootstrapTable(listInit);
    },
    getList:function(){
    	$("#detialList").bootstrapTable('refresh', { url: "/classinfo/getFileDetail",query:{'messageType':$("#fileDetialMessageType").val() }});
    }
}

function reSend(inedx){
	var fileDetail = $("#detialList").bootstrapTable('getData');
	if(fileDetail[inedx].zongfenId==null){
		swal({
            title:"综分设备已删除，请重新生成下发！",
            html: false
        });
		return false;
	}
	$.ajax({
		url:"/classinfo/reSend",
		type:"POST",
		data:JSON.stringify(fileDetail[inedx]),
        contentType: "application/json",
		success: function (data) {
			if(data.result==1){
				swal({
                    title:"重发成功",
                    text: "",
                    html: false
                }, function(isConfirm) {
                	$("#detialList").bootstrapTable('refresh', { url: "/classinfo/getFileDetail",query:{'messageType':$("#fileDetialMessageType").val() }});
                });
			}else{
				swal({
		            title:data.message,
		            html: false
		        });
				return false;
			}
		}
	});
}

function getPolicyDetail(index){
	var fileDetail = $("#detialList").bootstrapTable('getData');
	var resendFlag = 0;
	if(fileDetail[index].server!=null && fileDetail[index].server!=undefined){
		resendFlag = 1;
	}

    var fileDetailType = $("#fileDetialMessageType").val();
	var hasResendPermission = 0;
    if (fileDetailType==200){
        if ($("#zf104002_redo").val()==1){
            hasResendPermission = 1;
        }
    } else if (fileDetailType==202) {
        if ($("#zf104003_redo").val()==1){
            hasResendPermission = 1;
        }
    } else if (fileDetailType==201) {
        if ($("#zf104004_redo").val()==1){
            hasResendPermission = 1;
        }
    } else if (fileDetailType==207) {
        if ($("#zf104005_redo").val()==1){
            hasResendPermission = 1;
        }
    }
	PolicyDetail.showDetail(fileDetail[index].messageNo,$("#fileDetialMessageType").val(),1,resendFlag&&hasResendPermission);
}

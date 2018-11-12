
ipAddress= {
indexListInit:function(){
	var deviceStatus = {
			method: 'post',
			contentType : "application/x-www-form-urlencoded",
			dataType: "json",
			pagination: true,
			showColumns: !0,
            striped: true,
			iconSize: "outline",
			sidePagination: "server", //启用分页
			dataField: "list",
			url:"getIndex",
            searchOnEnterKey: true, //ENTER键搜索
            pageNumber: 1,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            queryParamsType:'limit',
			queryParams:queryParams,//请求服务器时所传的参数
			responseHandler: responseHandler,
			icons: {
				refresh: "glyphicon-repeat",
				toggle: "glyphicon-list-alt",
				columns: "glyphicon-list",
			},
			toolbar:"#commonButton",
			sortable: true,//是否启用排序
            sortOrder: "asc",//排序方式
			clickToSelect:true, //是否启用点击选中行
			columns: [
				{
                    field: 'ipType',
                    title: 'IP类型',
                    formatter: function (value, row, index) {
                    	var data = "";
                    	if(value=='0x04'){
                    		data = 'IPv4';
                    	}else if(value=='0x06'){
                    		data = 'IPv6';
                    	}
						return "<span title='"+data+"'>"+data+"</span>";
                    }
                },{
                    field: 'ipSegment',
                    title: 'IP地址段',formatter:function(value,row,index){
                        return "<span title='"+value+"'>"+value+"</span>";
                    }
                },{
                    field: 'areaName',
                    title: '区域',formatter:function(value,row,index){
                        return "<span title='"+value+"'>"+value+"</span>";
                    }
                },{
                    field: 'updateTime',
                    title: '更新日期',formatter:function(value,row,index){
                        return "<span title='"+value+"'>"+value+"</span>";
                    }
                }]
			};
	
	$("#IpAddressList").bootstrapTable(deviceStatus);
    //请求服务数据时所传参数
    function queryParams(params){
        return{
            //每页多少条数据
            pageSize: params.limit,
            //请求第几页
            pageIndex:params.offset/params.limit+1,
            ipType:$("#ipType").val().trim(),
            ipaddress:$("#ipaddress").val().trim(),
        }
    };

    /**
     * 获取返回的数据的时候做相应处理，让bootstrap table认识我们的返回格式
     * @param {Object} res
     */
    function responseHandler(result) { //数据筛选
        if (result) {
            return {
                "list" : result.rows,
                "total" : result.total
            };
        } else {
            return {
                "list" : [],
                "total" : 0
            };
        }
    };
    
	$('#searchFormButton').click(function(){
		ipAddress.searchMethod();
	   });
	
	$('#import').click(function(){
		$('#typeId').val("");
	});
},
alarmInit:function(){
	$.ajax({
	   	 	url: '/generalIPAddress/getStatus',
	        type: 'POST',
	        success:function(data){
	        	if(data==1){
	        		$("#importAlert").show();
	        	}
	        }
		});
},
searchMethod:function(){
	$('#IpAddressList').bootstrapTable('refresh', {url: 'getIndex'});
},
initExportButton:function () {
    $('#export').on('click',function () {
    	swal({
			title: "<span class='text-success'>正在导出，请稍后...</span>",
			html: true,
			text: "<img src='/static/js/plugins/layer/skin/default/loading-1.gif' />",
			showCancelButton: false,
			showConfirmButton:false,
			allowOutsideClick:false   //点击模态框外，模态框消失 
		});
    	$.ajax({
    		url:'/generalIPAddress/exportFile',
    		type:'POST',
    		contentType: "application/json",
    		success:function(data){
    			if(data!=''){
    				$("#downLoadFileName").val(data);
    				swal.close();
    				$('#searchForm').attr('action', '/classinfo/exportExcelFile.do').attr('method', 'get').submit();
    			}
    		}
    	});
        return false;
        })
},
importFile:function(){
    $('#importButton').click(function() {

        clearWarn($('#addType'));
     	if($("#typeId").val()==""){
     		warn('addType','请选择操作类型');
     		return false;
     	}

    	var fileName = $("#ipAddressFile").val();
        if(fileName=="" || fileName==undefined){
            warn('addFile','请选择上传文件');
            return false;
        }

    	var reg = /^.*\.(?:xls|xlsx)$/i;//文件名可以带空格
        if (fileName !="" && !reg.test(fileName)) {//校验不通过
            warn('addFile','请导入正确格式的文件！');
    		return false;
        }
        if($("#typeId").val()==0){
            swal({
                title:"",
                text: "全量导入先清除数据然后将excel内容导入，请确认是否全量导入？",
                html: false
            }, function(isConfirm) {
                swal({
                    title: "<span class='text-success'>正在导入，请稍后...</span>",
                    html: true,
                    text: "<img src='/static/js/plugins/layer/skin/default/loading-1.gif' />",
                    showCancelButton: false,
                    showConfirmButton:false,
                    allowOutsideClick:false   //点击模态框外，模态框消失
                });
                var formData = new FormData($('#importIpAddress')[0]);
                var type = $('#typeId').val();
                $.ajax({
                    url: '/generalIPAddress/importFile?type='+type,
                    type: 'POST',
                    data:formData,
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function (data) {
                        $('.progress-bar').css("width","0");
                        if(data.status==""){
                            swal({
                                title:"",
                                text: data.message+",正在解析导入文件！请查看解析进度。",
                                html: false
                            }, function(isConfirm) {
                                $("#importButton").attr("disabled",true);
                                $("#importIpAddress").find("#exceptionResult a").text("");
                                getProgress();
                            });
                        }else{
                            swal({
                                title:"导入失败",
                                text: data.message,
                                html: false
                            }, function(isConfirm) {
                                $("#ipAddressFile").val("");
                            });
                        }
                    }
                });
            });
        }else{
            swal({
                title: "<span class='text-success'>正在导入，请稍后...</span>",
                html: true,
                text: "<img src='/static/js/plugins/layer/skin/default/loading-1.gif' />",
                showCancelButton: false,
                showConfirmButton:false,
                allowOutsideClick:false   //点击模态框外，模态框消失
            });
            var formData = new FormData($('#importIpAddress')[0]);
            var type = $('#typeId').val();
            $.ajax({
                url: '/generalIPAddress/importFile?type='+type,
                type: 'POST',
                data:formData,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    $('.progress-bar').css("width","0");
                    if(data.status==""){
                        swal({
                            title:"",
                            text: data.message+",正在解析导入文件！请查看解析进度。",
                            html: false
                        }, function(isConfirm) {
                            $("#importButton").attr("disabled",true);
                            $("#importIpAddress").find("#exceptionResult a").text("");
                            getProgress();
                        });
                    }else{
                        swal({
                            title:"导入失败",
                            text: data.message,
                            html: false
                        }, function(isConfirm) {
                            $("#ipAddressFile").val("");
                        });
                    }
                }
            });
        }
    });
},
creatAndSend:function(){
	$("#createAndSend").click(function(){
		$.ajax({url: '/classinfo/getServerList',
	        type: 'POST',
	        success:function(data){
	        	var html = "<option value=''>请选择</option>";
	        	if(data!=null){
	        		data.forEach(function(val,index){
						html +='<option value="'+val.zongfenId+'">服务器('+val.zongfenIp+')</option>';
					});
	        		$("#servers").empty().append($(html));
	        	}
	        }
		});
	});
	
	$("#ensure").click(function(){
		var servers = $("#servers").val();
		if(servers==""){
			warn('serverMessage','请选择服务器');
			return false;
		}
		$.ajax({
			url:"/generalIPAddress/createAndSend",
			data:{"serverId":servers},
			type:"POST",
			success:function(data){
				if(data=="success"){
	        		 swal({
	                     title:"",
	                     text: "操作成功",
	                     html: false
	                 }, function(isConfirm) {
	                	$("#importAlert").hide();
	 	        		$("#createFile").modal("hide");
	                 });
	        	}else if(data=="error"){
	       			swal({
	                       title:"操作失败",
	                       text: data,
	                       html: false
	                   }, function(isConfirm) {
	                   });
			    }else if(data=="sftp_error"){
		       		swal({
		                   title:"SFTP服务器配置错误",
		                   text: "错误",
		                   html: false
		               }, function(isConfirm) {
		               });
			    }else if(data=="is empty"){
			    	swal({
		                   title:"请先导入数据",
		                   text: "错误",
		                   html: false
		               }, function(isConfirm) {
		            });
			    }
			}
		});
		
	});
},
fileDetail:function(){
	$("#fileDetialMessageType").val("202");
	fileDetail.tableInit();
	
	$("#getDetail").click(function(){
		fileDetail.getList();
	});
},
init:function(){
	ipAddress.alarmInit();
	ipAddress.importFile();
	ipAddress.indexListInit();
	ipAddress.creatAndSend();
	ipAddress.initExportButton();
	ipAddress.fileDetail();
    }
};
ipAddress.init();

var task1=""
function getProgress() {
    $.ajax({
        type : "POST",
        url : "/generalIPAddress/queryProgress",
        dataType : "json",
        success : function(data) {
            var resultFileName = data.resultFileName;
            var process=data.process;
            $('.progress-bar').css("width",process*3);
                if(data.process=='100'){
                    $(".progress-bar").children().html('100%');
                    $("#importIpAddress").find("#exceptionResult a").text(resultFileName);
                    $("#ipAddressFile").val("");
                    $("#typeId").val("");
                    ipAddress.searchMethod();
                    $("#importButton").removeAttr("disabled");
                    $("#upLoad").modal("hide");
                }else if(data.process=='-1'){
                    $('.progress-bar').css("width","100px");
                    $(".progress-bar").children().html("import failure");
                    $("#importIpAddress").find("#exceptionResult a").text(resultFileName);
                    $("#ipAddressFile").val("");
                    $("#typeId").val("");
                    ipAddress.searchMethod();
                    $("#importButton").removeAttr("disabled");
                    $("#upLoad").modal("hide");
                }else{
                    var num = ((data.process/100)*100).toString();
                    var result = num.substring(0,num.toString().indexOf(".")+3);
                    $(".progress-bar").children().html(result+"%");
                	setTimeout(getProgress,500);
				}
        },
        error :function (XMLHttpRequest, textStatus, errorThrown) {
        }
    });
}


(function () {
    $.ajax({
        type : "POST",
        url : "/generalIPAddress/queryProgress",
        dataType : "json",
        success : function(data) {
            var resultFileName = data.resultFileName;
            var process=data.process;
            $('.progress-bar').css("width",process*3);
            if(data.process=='100'){
                $(".progress-bar").children().html('100%');
                $("#importIpAddress").find("#exceptionResult a").text(resultFileName);
                $("#importButton").removeAttr("disabled");
                $("#upLoad").modal("hide");
            }else if(data.process=='-1'){
                $('.progress-bar').css("width","100px");
                $(".progress-bar").children().html("import failure");
                $("#importIpAddress").find("#exceptionResult a").text(resultFileName);
                $("#importButton").removeAttr("disabled");
                $("#upLoad").modal("hide");
            }else{
                var num = ((data.process/100)*100).toString();
                var result = num.substring(0,num.toString().indexOf(".")+3);
                $(".progress-bar").children().html(result+"%");
                setTimeout(getProgress,500);
            }
        },
        error :function (XMLHttpRequest, textStatus, errorThrown) {
        }
    });
})()
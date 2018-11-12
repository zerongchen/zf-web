
webCategory= {
indexListInit:function(){
	loadSel('webType','/select/getWebType',true,undefined,false,undefined);
	var deviceStatus = {
			method: 'post',
			contentType : "application/x-www-form-urlencoded",
			dataType: "json",
			pagination: true,
			showColumns: !0,
			iconSize: "outline",
			sidePagination: "server",
			dataField: "list",
			url:"getIndex",
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
                    field: 'hostName',title: 'Host名称',formatter:function(value,row,index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'webTypes',title: '类型ID',formatter:function(value,row,index){
                        return "<span title='"+value+"'>"+value+"</span>";
                    }
                },{
                    field: 'webName',title: '类型名称',formatter:function(value,row,index){
                        return "<span title='"+value+"'>"+value+"</span>";
                    }
                },{
                    field: 'updateTime',title: '更新日期',formatter:function(value,row,index){
                        return "<span title='"+value+"'>"+value+"</span>";
                    }
                }]
			};
	
	$("#webCategoryList").bootstrapTable(deviceStatus);
    //请求服务数据时所传参数
    function queryParams(params){
        return{
            //每页多少条数据
            pageSize: params.limit,
            //请求第几页
            pageIndex:params.offset/params.limit+1,
            webType:$("#webType").val().trim(),
            hostName:$("#hostName").val().trim(),
        }
    };
    
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
		webCategory.searchMethod();
	   });
	
	$('#import').click(function(){
		$('#operation').val("");
	});
},
alarmInit:function(){
	$.ajax({
	   	 	url: '/generalWEBCategory/getStatus',
	        type: 'POST',
	        success:function(data){
	        	if(data==1){
	        		$("#importAlert").show();
	        	}
	        }
		});
},
searchMethod:function(){
	$('#webCategoryList').bootstrapTable('refresh', {url: 'getIndex'});
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
    		url:'/generalWEBCategory/exportFile',
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
    	 $("#operation").val();
    	if($("#operation").val()==""){
    		swal({
                title:"请选择操作类型！",
                html: false
            });
    		$("#webCategoryFile").val("");
    		return false;
    	}
    	
    	var fileName = $("#webCategoryFile").val();
    	var reg = /^.*\.(?:xls|xlsx)$/i;//文件名可以带空格
        if (fileName !="" && !reg.test(fileName)) {//校验不通过
        	swal({
                title:"请导入正确格式的文件！",
                html: false
            });
    		$("#webCategoryFile").val("");
    		return false;
        }
    	
    	if(fileName !="" && fileName!=undefined){
    		var formData = new FormData($('#importWEBCategory')[0]);
    		swal({
    			title: "<span class='text-success'>正在导入，请稍后...</span>",
    			html: true,
    			text: "<img src='/static/js/plugins/layer/skin/default/loading-1.gif' />",
    			showCancelButton: false,
    			showConfirmButton:false,
    			allowOutsideClick:false   //点击模态框外，模态框消失 
    		});
        	$.ajax({
    	   	 	url: '/generalWEBCategory/importFile',
    	        type: 'POST',
    	        data:formData,
    	        cache: false,  
    	        contentType: false,  
    	        processData: false,  
    	        success: function (data) {
    	        	if(data.message==""){
    	        		 swal({
    	                     title:"",
    	                     text: "导入成功,请生成并下发文件",
    	                     html: false
    	                 }, function(isConfirm) {
    	                	 $("#webCategoryFile").val("");
    	                	 $("#operation").val("");
    	                	 $("#upLoad").modal("hide");
    	                	 webCategory.searchMethod();
    	                 });
    	        		 $("#importAlert").show();
    	        		 return false;
    	        		}else{
    	        			swal({
    	                        title:"导入失败",
    	                        text: '<a href="#" onclick= \"saveTxt(\''+data.message+'\',\'web分类库导入错误详情\')\";return false>点击下载详情</a>',
    	                        html: true
    	                    }, function(isConfirm) {
    	                    	$("#webCategoryFile").val("");
    	                    });
    	        	}
    	        }
    	    });
    	}else{
    		swal({
                title:"请选择上传文件！",
                html: false
            });
    		$("#webCategoryFile").val("");
    		return false;
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
			url:"/generalWEBCategory/createAndSend",
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
	$("#fileDetialMessageType").val("200");
	fileDetail.tableInit();
	
	$("#getDetail").click(function(){
		fileDetail.getList();
	});
},
init:function(){
	webCategory.alarmInit();
	webCategory.importFile();
	webCategory.indexListInit();
	webCategory.creatAndSend();
	webCategory.initExportButton();
	webCategory.fileDetail();
    }
};
webCategory.init();

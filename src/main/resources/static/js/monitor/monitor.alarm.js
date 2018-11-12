/***********************************************************************************
alarmObject	 *         
			 *          refreshData：刷新数据
			 *          initData：获取数据
			 *          searchData：搜索数据
			 *          init：初始化所有的方法
 **********************************************************************************
 function    *          
             *          
             *          
 ********************************************************************************/  
var alarmObject = {
		//1:根据查询条件刷新列表
		refreshData:function(){
			var data = $("#searchForm").formToJSON();
			data.startTime = $('#start').val();
			data.endTime = $('#end').val();
			$("#table").bootstrapTable('refresh', { url: "list",query:data});
		},
		clickMore:function(){
			$("#moreBtn").click(function(){
				var count = $("#count").val();
				$("#count").val(5+parseInt(count));
				alarmObject.initDetail();
			});
		},
		//4:主页数据及初始化
		initData: function(){
			//主页列表初始化
			var listInit = {
					url: "list?"+Math.random(),
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					pagination: true,
					showColumns: false,
					toolbar: "#table-toolbar",
					iconSize: "outline",
					sidePagination: "server",
					dataField: "list",
	                pageNumber: 1,
	                pageSize: 10,
	                pageList: [10, 25, 50, 100, 200],
	                queryParamsType:'limit',
	    			queryParams:function(params){
	    				 return{
	    				        //每页多少条数据
	    				        pageSize: params.limit,
	    				        //请求第几页
	    				        pageIndex:params.offset/params.limit+1,
	    				        //form表单的数据
	    				        startTime:$('#start').val(),
	    				        endTime:$('#end').val(),
	    				        alarmContent:$('#alarmContent').val(),
	    				        alarmParams:$('#alarmParams').val(),
	    				        dealStatus:$('#dealStatus').val()
	    				       
	    				    }
	    			},//请求服务器时所传的参数
	    			responseHandler:function(result){
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
	    			},
					striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "asc",//排序方式
					clickToSelect:true, //是否启用点击选中行
					columns: [
						{ // 列设置
							field: 'alarmContent',
		                    title: '告警信息',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'alarmId',
		                    title: '告警ID',
		                    visible:false
		                },{
		                    field: 'monitorTaskId',
		                    title: '任务监控ID',
		                    visible:false
		                },{
		                    field: 'taskType',
		                    title: '任务类型',
		                    visible:false
		                },{
		                    field: 'taskSubtype',
		                    title: '任务子类型',
		                    visible:false
		                },{
		                    field: 'alarmTitle',
		                    title: '详情标题',
		                    visible:false
		                },{
		                    field: 'alarmParams',
		                    title: '告警项参数',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'alarmTime',
		                    title: '告警时间',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            },
			                width:148
		                },{
		                    field: 'dealStatus',
		                    title: '处理状态',
		                    formatter: function (value, row, index) {  
		                    	if(value==0){
	                        		return "<span class='badge badge-warning'>未处理</span>";
	                        	}else if(value==1){
	                        		return "<span class='badge badge-success'>已处理</span>";
	                        	}
	                        },
			                width:75  
		                },{
		                    field: 'dealUser',
		                    title: '处理人',
		                    formatter:function(value, row, index){
		                    	if(value==null){return ""; }else{return "<span title='"+value+"'>"+value+"</span>";}
		                    },
			                width:80  
		                },{
		                    field: 'dealTime',
		                    title: '处理时间',
		                    formatter:function(value, row, index){
		                    	if(value==null){return ""; }else{return "<span title='"+value+"'>"+value+"</span>";}
		                    },
			                width:148
		                },{           
		                    title:'操作',
		                	formatter:function(value,row,index){
		                    	var format ="";
                                if($("#zf401001_deal").val()==1){
                                    format+="<a href='#' title='处理' data-toggle='modal' data-target='#dealModal' onclick='dealRow("+index+")' class='m-r'><i class='fa fa-wrench fa-lg'></i></a>";
                                }else{
                                    format+="<a href='#' title='处理'  style='display: none' data-toggle='modal' data-target='#dealModal' onclick='dealRow("+index+")' class='m-r'><i class='fa fa-wrench fa-lg'></i></a>";
                                }
                                if($("#zf401001_query").val()==1 && row.taskType != 5 ){
                                    format+="<a href='#' title='详情' data-toggle='modal' data-target='#detailModal' onclick='detailRow("+index+")' class='m-r'><i class='fa fa-file-text fa-lg'></i></a>";
                                // 文件告警信息
                                }if($("#zf401001_query").val()==1 && row.taskType == 5 ){
                                    format+="<a href='#' title='详情' data-toggle='modal' data-target='#detailExceptionFileModal' onclick='detailExceptionFileRow("+index+")' class='m-r'><i class='fa fa-file-text fa-lg'></i></a>";
                                } else {
                                    format+="<a href='#' title='详情'  style='display: none' data-toggle='modal' data-target='#detailModal' onclick='detailRow("+index+")' class='m-r'><i class='fa fa-file-text fa-lg'></i></a>";
                                }

		                		return format;
		                	},
			                width:65  
		                }
		            ] 
				};
			
			$("#table").bootstrapTable(listInit);
		},
		initQueryParam:function(json){
			$('#taskTypeFile').val(json.taskType);
		    $('#monitorTaskIdFile').val(json.monitorTaskId);
		    $('#taskIdFile').val(json.taskId);
		    
		    $('#titleFile').val(json.title);
		    $('#countFile').val(json.count);
		    $('#contentFile').val(json.content);
		    $('#paramsFile').val(json.params);
		    
		},
		initJson:function(e,title,count,content,params){
			var json = {
					'taskType':e.taskType,
					'monitorTaskId':e.monitorTaskId,
					'taskId':e.taskId,
					'title':title,
					'count':count,
					'params':params
			};
			return json;
		},
		initDetail:function(){
			var monitorTaskId=$("#monitorTaskId").val(),
			title=$("#title").val(),
			count=$("#count").val(),
			content=$("#content").val(),
			params=$("#params").val();
			$('#paramTitle').text(title);
			$('#tracingTitle').text(title);
			//记得要先清除js里的缓存数据
			$('.detail-append').remove();
			$('#detailParam').append('<div class="detail-append form-group form-inline m-r-md">告警内容：'+content+'</div>')
							 .append('<div class="detail-append form-group form-inline m-r-md">告警参数：'+params+'</div>');
			$.ajax({
				url: "/monitor/detail/list",
				async:false,
				data:{
						monitorTaskId:monitorTaskId,
						count:count
					},
				type:'POST',
				success : function(result) {
					var detailTracingStr = "",executing="",execResult="",moreDetail="";
					$.each(result.details,function(i, e){
						if(e.taskType==4){//DPI策略
							executing='<p>执行环节：'+title+'下发至DPI</p>';
							/**
						     * 处理状态,3=失败，4=成功，5=超时
						     */
							if(e.status==4){
								execResult='<p>执行结果：<span class="text-navy">策略下发成功 ' ;
                                if($("#zf401001_redo").val()==1){
                                    execResult+='<a href="javascript:void(0)" class="btn btn-success btn-xs demo1" onclick="resend('+e.dataId+','+e.taskSubtype+','+e.taskId+','+e.monitorTaskId+')">重试</a>';
								}
                                execResult+='</span></p>';
							}else if(e.status==3){
								execResult='<p>执行结果：<span class="text-danger">策略下发失败 ' ;
                                if($("#zf401001_redo").val()==1){
                                    execResult+='<a href="javascript:void(0)" class="btn btn-success btn-xs demo1" onclick="resend('+e.dataId+','+e.taskSubtype+','+e.taskId+','+e.monitorTaskId+')">重试</a>' ;
                                }
                                execResult+='</span></p>';
							}else if(e.status==5){
								execResult='<p>执行结果：<span class="text-warning">策略下发超时 ';
                                if($("#zf401001_redo").val()==1){
                                    execResult+='<a href="javascript:void(0)" class="btn btn-success btn-xs demo1" onclick="resend('+e.dataId+','+e.taskSubtype+','+e.taskId+','+e.monitorTaskId+')">重试</a>';
                                }
                                execResult+='</span></p>';
							}
						}else if(e.taskType==5){//文件上报
							
							var jsonObj = alarmObject.initJson(e,title,count,content,params);
							var json = JSON.stringify(jsonObj).replace(/\"/g,"'");
							execResult = '<p>执行结果：<span class="text-success">文件上报成功 '
								   + '<span class="text-info m-r-sm">生成文件个数（'+e.createNum+'）</span>'
								   + '<span class="text-danger m-r-sm">失败文件数（'+e.failNum+'）</span>'
								   + '<span class="text-success m-r-sm">成功文件数（'+e.successNum+'）</span>'
								   + '<a href="#" data-toggle="modal" data-target="#detailFile" class="btn btn-info btn-xs demo1" onclick="showFile('+json+')">详情</a>'
								   + "</span></p>";
						}else if(e.taskType==2){//主动上报
							
							var jsonObj = alarmObject.initJson(e,title,count,content,params);
							var json = JSON.stringify(jsonObj).replace(/\"/g,"'");
							execResult = '<p>执行结果：<span class="text-success">'+title
								   + '<span class="text-danger m-r-sm">失败文件数（'+e.failNum+'）</span>'
								   + '<span class="text-success m-r-sm">成功文件数（'+e.successNum+'）</span>'
							       + "<a href='#' data-toggle='modal' data-target='#detailFile' class='btn btn-info btn-xs demo1' onclick='showFile("+json+")'>详情</a>"
								   + "</span></p>";
						}else{
							if(e.status==4){
								execResult='<p>执行结果：<span class="text-navy">'+title+'成功 </span></p>';
							}else if(e.status==3){
								execResult='<p>执行结果：<span class="text-danger">'+title+'失败 </span></p>';
							}else if(e.status==5){
								execResult='<p>执行结果：<span class="text-warning">'+title+'超时 </span></p>';
							}
						}
						detailTracingStr += '<li class="detail-append color"><div class="pointer slategray"><i class="fa fa-compass"></i></div><div class="el-container"><div class="content">'
							             + '<p><span class="time"><i class="fa fa-clock-o"></i>'+e.completetimeStr+'</span></p>'
							             + '<p>监控项参数：'+e.monitorParams+'</p>'
							             + executing + execResult;
					});
					$('#detailTracing').append(detailTracingStr);
					if(result.total > result.details.length){
						moreDetail = '<a class="detail-append timeline-heading" id="moreBtn"><i class="fa fa-angle-double-down"></i> 更多</a> ';
						$('#detailTracing').after(moreDetail);
						//在这里执行更多下拉的操作
						alarmObject.clickMore();
					}
				},
				error : function() {
					swal({
						title: "失败",
						text: "对不起，没有数据",
						type: "error"
					});
				}
			});
		},
		//查询
		searchData:function(){
			$("#searchFormButton").click(function(){
				alarmObject.refreshData();
			});
		},
		//返回
		returnToDetail:function(){
			$(":button[name='returnFileBtn']").click(function(){
				//detail的初始化
				alarmObject.initDetail();
			});
		},
		//刷新列表
		refreshFileData:function(){
			var data = $("#searchFileForm").formToJSON();
			$("#detail-table").bootstrapTable('refresh', { url: "/monitor/detail/listFile",query:data});
		},
		initFileStatus:function(type){//初始化文件的页面：下来选项
			$("#statusFile").empty();
			if(type==5){
				$("#statusFile").append("<option value='-1'>请选择</option>")
				.append("<option value='0'>生成文件</option>")
				.append("<option value='1'>上报失败</option>")
				.append("<option value='2'>上报成功</option>");
			}else{
				$("#statusFile").append("<option value='-1'>请选择</option>")
				.append("<option value='1'>上报失败</option>")
				.append("<option value='2'>上报成功</option>");
			}
		},
		//文件页面
		initFileData: function(){
			//主页列表初始化
			var listInit = {
					url: "/monitor/detail/listFile?"+Math.random(),
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					pagination: true,
					showColumns: false,
					toolbar: "#table-file-toolbar",
					iconSize: "outline",
					sidePagination: "server",
					dataField: "list",
	                pageNumber: 1,
	                pageSize: 10,
	                pageList: [10, 25, 50, 100, 200],
	                queryParamsType:'limit',
	    			queryParams:function(params){
	    				 return{
	    				        //每页多少条数据
	    				        pageSize: params.limit,
	    				        //请求第几页
	    				        pageIndex:params.offset/params.limit+1,
	    				        //form表单的数据
	    				        taskType:$('#taskTypeFile').val(),
	    				        monitorTaskId:$('#monitorTaskIdFile').val(),
	    				        taskId:$('#taskIdFile').val()
	    				    }
	    			},//请求服务器时所传的参数
	    			responseHandler:function(result){
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
	    			},
					striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "asc",//排序方式
					clickToSelect:true, //是否启用点击选中行
					columns: [
						{ // 列设置
							field: 'serverIp',
		                    title: 'IP地址'
		                },{
		                    field: 'recordNum1',
		                    title: '记录数'
		                },
		                {
		                    field: 'status',
		                    title: '文件状态',
		                    formatter: function (value, row, index) {  
		                    	if(value==1){
	                        		return "<span class='badge badge-danger'>上报失败</span>";
	                        	}else if(value==2){
	                        		return "<span class='badge badge-success'>上报成功</span>";
	                        	}else{
	                        		return "<span class='badge badge-info'>生成文件</span>";
	                        	}
	                        }   
		                },{
		                    field: 'createTimeStr',
		                    title: '创建时间'
		                }
		            ] 
				};
			
			$("#detail-table").bootstrapTable(listInit);
		},
		//查询
		searchFileData:function(){
			$("#searchFormFileButton").click(function(){
				alarmObject.refreshFileData();
			});
		},
		//刷新列表
		refreshDealData:function(){
			$("#deal-table").bootstrapTable('refresh', { url: "solution"});
		},
		saveDeal:function(){
			$("#dealBtn").click(function(){
				$.ajax({
					url: "addSolution",
					async:false,
					data:{
						alarmId:$("#alarmIdDeal").val(),
						dealSolution:$("#dealSolution").val()
					},
					type:'POST',
					success : function(data) {
						if(data.result==0){
							swal({
								title: "成功",
								text: data.message,
								type: "success"
							});
							$('#dealModal').modal('hide');
							alarmObject.refreshData();
						}else if(data.result==1){
							swal({
								title: "失败",
								text: data.message,
								type: "error"
							});
						}
					},
					error : function() {
						swal({
							title: "失败",
							text: "对不起，没有数据",
							type: "error"
						});
					}
				});
			});
		},
		//处理页面
		initDealData: function(){
			//主页列表初始化
			var listInit = {
					url: "solution?"+Math.random(),
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					pagination: true,
					showColumns: false,
					iconSize: "outline",
					sidePagination: "server",
					dataField: "list",
	                pageNumber: 1,
	                pageSize: 10,
	                pageList: [10, 25, 50, 100, 200],
	                queryParamsType:'limit',
	                clickToSelect: true, //是否启用点击选中行
	    			queryParams:function(params){
	    				 return{
	    				        //每页多少条数据
	    				        pageSize: params.limit,
	    				        //请求第几页
	    				        pageIndex:params.offset/params.limit+1,
	    				        //form表单的数据
	    				        taskType:$("#taskTypeDeal").val(),
	    				        taskSubtype:$("#taskSubtypeDeal").val()
	    				    }
	    			},//请求服务器时所传的参数
	    			responseHandler:function(result){
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
	    			},
					striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "asc",//排序方式
					clickToSelect:true, //是否启用点击选中行 
					columns: [
						{
		                    title: '选取',
		                    formatter: function (value, row, index) {  
		                    	return "<a id='solution_"+index+"' onclick='addSolution("+index+")' class='text-navy'><i class='fa fa-arrow-left fa-lg'></i></a>";
	                        }   
		                },
						{
		                    field: 'dealSolution',
		                    title: '处理方案'
		                },
		                {
		                    field: 'dealTime',
		                    title: '处理时间'  
		                }
		            ] 
				};
			
			$("#deal-table").bootstrapTable(listInit);
		},
		//初始化
		init:function(){
			initDate(3,false,false);
			alarmObject.initData();
			alarmObject.searchData();
			alarmObject.initFileData();
			alarmObject.searchFileData();
			alarmObject.initDealData();
			alarmObject.saveDeal();
		}
	};

alarmObject.init();

//定时刷新主页面数据。
function getRefreshTime(){
    var times=null;
    $.ajax({
        url: "getRefreshTime",
        async:false,
        data:{},
        type:'POST',
        success : function(data) {
            times=data*1000;
        },
        error : function() {
        	times =30*1000;
            swal({
                title: "失败",
                text: "对不起，没有数据",
                type: "error"
            });
        }
    });
    return times;
}
var reTime = getRefreshTime();
function refresh(){
	var pageNum=$('#table').bootstrapTable('getOptions').pageNumber;
	if(pageNum == 1){
        alarmObject.refreshData();
	}
    setTimeout("refresh()",reTime);
}
refresh();

function detailExceptionFileRow(index) {
    var obj = $("#table").bootstrapTable('getData');
    var alarmObj=obj[index];
	var fileType =0;
	var reportType=0;
    var date = alarmObj.alarmContent.slice(0,8);
    var hours = alarmObj.alarmContent.slice(8,10);
    if(alarmObj.alarmContent.indexOf("HTTPGET") != -1){
        fileType=768;
        if(alarmObj.alarmContent.indexOf("上报")!=-1){
            reportType=1;
		}else if(alarmObj.alarmContent.indexOf("接收")!=-1){
            reportType=0;
		}
	}else if(alarmObj.alarmContent.indexOf("AAA") != -1){
        fileType=1023;
	}else if(alarmObj.alarmContent.indexOf("流量流向") != -1){
        fileType=452;
    }else if(alarmObj.alarmContent.indexOf("AAA") != -1){
        fileType=1023;
    }else{
        fileType=768;
	}
    $('#statTime').val(date+hours);

    let colum=[];
    if(fileType==768){
        colum = [
            { // 列设置
                field: 'file_name',
                title: '文件名称'
            },{
                field: 'dpi_ip',
                title: '<span title="DPI上报服务器">DPI上报服务器</span>'
            },{
                field:'software_provider',
                title: '<span title="来源厂家">来源厂家</span>',
                formatter:providerFormatter
            },{
                field: 'rfile_size',
                title: '<span title="接收文件大小(KB)">接收文件大小(KB)</span>',
                formatter:function(value,row,index){
                    if(value==null){return "";}else{ return value;}
                }
            },{
                field: 'ufile_size',
                title: '<span title="上报文件大小 (KB)">上报文件大小 (KB)</span>',
                formatter:function(value,row,index){
                    if(value==null){return "";}else{ return value;}
                }
            },{
                field: 'server_ip',
                title: '<span title="上报服务器">上报服务器</span>'
            },{
                field: 'filecreate_time',
                sortable:true,
                title: '<span title="文件生成时间">文件生成时间</span>',
                formatter:function(value,row,index){
                    if(value==null){return "";}else{ return value;}
                }
            },{
                field: 'filereceived_time',
                title: '<span title="文件接收时间">文件接收时间</span>',
                formatter:function(value,row,index){
                    if(value==null){return "";}else{ return value;}
                }
            },{
                field: 'fileupload_time',
                title: '<span title="文件上报时间">文件上报时间</span>',
                formatter:function(value,row,index){
                    if(value==null){return "";}else{ return value;}
                }
            },{
                field: 'warn_type',
                title: '<span title="文件异常描述">文件异常描述</span>',
                formatter:operatingFormatter
            }

        ]
    }else {
        colum = [
            { // 列设置
                field: 'file_name',
                title: '文件名'
            },{
                field: 'cfileSize',
                title: '<span title="生成文件大小(KB)">生成文件大小(KB)</span>',
            },{
                field: 'ufileSize',
                title: '<span title="上报文件大小(KB)">上报文件大小(KB)</span>',
                formatter:function(value,row,index){
                    if(value==null){return "";}else{ return value;}
                }
            },{
                field: 'server_ip',
                title: '<span title="上报服务器">上报服务器</span>',
                formatter:function(value,row,index){
                    if(value==null){return "";}else{ return value;}
                }
            },{
                field: 'cfileTime',
                sortable:true,
                title: '<span title="文件生成时间">文件生成时间</span>',
                formatter:function(value,row,index){
                    if(value==null){return "";}else{ return value;}
                }
            },{
                field: 'ufileTime',
                title: '<span title="文件上报时间">文件上报时间</span>',
                formatter:function(value,row,index){
                    if(value==null){return "";}else{ return value;}
                }
            },{
                field: 'warn_type',
                title: '<span title="文件异常描述">文件异常描述</span>',
                formatter:operatingFormatter
            }

        ]
    }

    var listInit = {
        url: "listDetail",
        method: 'post',
        contentType : "application/x-www-form-urlencoded",
        dataType: "json",
        pagination: true,
        undefinedText: '',
        dataField: "list",
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100, 200],
        queryParamsType:'limit',
        queryParams:function(params){
            return{
                //每页多少条数据
                pageSize: params.limit,
                //请求第几页
                pageIndex:params.offset/params.limit+1,
                //form表单的数据
                statTime:$('#statTime').val(),
                fileType:fileType,
                reportType:reportType,
                order:params.order,
                sort:params.sort
            }
        },//请求服务器时所传的参数
        responseHandler:function(result){
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
        },
        striped:true,  //是否行间隔色显示
        sortable: true,//是否启用排序
        sortOrder: "desc",//排序方式
        clickToSelect:false, //是否启用点击选中行
        onLoadSuccess:function(){
            $('.bootstrap-table tr td').each(function () {
                $(this).attr("title", $(this).text());
                $(this).css("cursor", 'pointer');
            });
        },
        columns: colum

    };

    $("#detailExceptionFileTable").bootstrapTable('destroy').bootstrapTable(listInit);
}
//AOT:傲天，HW:华为，KUG:宽广，XIF:绿网,LVW:信风,ZTE:中兴,ZZN:直真
function providerFormatter(value, row, index) {
    return '<span title="'+value+'">'+value+'</span>';
}
//0=稽核反馈异常，1=文件接收延时，2=文件上报延时，3=文件大小不一致   ,-1=为正常文件
function operatingFormatter(value, row, index) {
    var errorType = "";
    if(Number(value)==0){
        errorType = "稽核反馈异常";
    }else if(Number(value)==1){
        errorType = "文件接收延时";
    }else if(Number(value)==2){
        errorType = "文件上报延时";
    }else if (Number(value)==3){
        errorType = "文件大小不一致";
    }
    return '<span title="'+errorType+'">'+errorType+'</span>';
}
//详情页面
function detailRow(index) {
	var obj = $("#table").bootstrapTable('getData');
	var alarmObj=obj[index];
	
	var monitorTaskId=alarmObj.monitorTaskId,
    taskType=alarmObj.taskType,
    taskSubtype=alarmObj.taskSubtype,
    title=alarmObj.alarmTitle,
    content=alarmObj.alarmContent,
    params=alarmObj.alarmParams;
	
	$("#monitorTaskId").val(monitorTaskId);
	$("#title").val(title);
	$("#count").val(5);
	$("#content").val(content);
	$("#params").val(params);
	//detail的初始化
	alarmObject.initDetail();
}
//处理页面
function dealRow(index){
	var obj = $("#table").bootstrapTable('getData');
	var alarmObj=obj[index];
	//先清空值
	$('#dealSolution').val("");
	//再设置新的值
	var taskType=alarmObj.taskType,
    taskSubtype=alarmObj.taskSubtype
    alarmId=alarmObj.alarmId;
	$("#taskTypeDeal").val(taskType);
	$("#taskSubtypeDeal").val(taskSubtype);
	$("#alarmIdDeal").val(alarmId);
	
	alarmObject.refreshDealData();
}
//点击列表添加处理方法
function addSolution(index){
	var obj = $("#deal-table").bootstrapTable('getData');
	var alarmObj=obj[index];
	$('#dealSolution').val(alarmObj.dealSolution);
}

//重发策略 messageType, messageNo
function resend(messageNo,messageType,taskId,monitorTaskId){
	$.ajax({
		url: "/monitor/detail/resend",
		async:false,
		data:{
			    dataId:messageNo,
			    taskSubtype:messageType,
                taskId:taskId,
                monitorTaskId:monitorTaskId
		},
		type:'POST',
		success : function(data) {
			if(data.result==0){
				swal({
					title: "成功",
					text: data.message,
					type: "success"
				});
			}else if(data.result==1){
				swal({
					title: "失败",
					text: data.message,
					type: "error"
				});
			}
		},
		error : function() {
			swal({
				title: "失败",
				text: "对不起，没有数据",
				type: "error"
			});
		}
	});
}

function showFile(json){
	alarmObject.initQueryParam(json);
	alarmObject.initFileStatus(json.taskType);
	alarmObject.refreshFileData();
	alarmObject.returnToDetail();
}




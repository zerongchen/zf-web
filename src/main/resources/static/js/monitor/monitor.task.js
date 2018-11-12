/***********************************************************************************
taskObject	 *         
			 *          refreshData：刷新数据
			 *          initData：获取数据
			 *          searchData：搜索数据
			 *          init：初始化所有的方法
 **********************************************************************************
 function    *          
             *          
             *          
 ********************************************************************************/  
var taskObject = {
		//1:根据查询条件刷新列表
		refreshData:function(){
			var data = $("#searchForm").formToJSON();
			data.startTime=$('#start').val();
			data.endTime=$('#end').val();
			$("#table").bootstrapTable('refresh', { url: "list",query:data});
		},
		clickMore:function(){
			$("#moreBtn").click(function(){
				var count = $("#count").val();
				$("#count").val(5+parseInt(count));
				taskObject.initDetail();
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
					showColumns: false,  //显示下拉框勾选要显示的列
		            showRefresh: false,  //显示刷新按钮
		            showToggle:false,  //设置数据显示格式
					toolbar: "#table-toolbar",
					iconSize: "outline",
					sidePagination: "server",
					dataField: "list",
	                pageNumber: 1,
	                pageSize: 10,
	                pageList: [10, 25, 50, 100, 200],
					striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "asc",//排序方式
					clickToSelect:true, //是否启用点击选中行
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
		    				        monitorName:$('#monitorName').val(),
		    				        monitorParams:$('#monitorParams').val(),
		    				        taskType:$('#taskType').val(),
		    				        status:$('#status').val()
		    				       
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
					columns: [
						{ // 列设置
							field: 'monitorTaskId',
		                    title: '监控项ID',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            },
                            width:70
		                },{
		                    field: 'taskSubtype',
		                    title: '任务子类型',
		                    visible:false
		                },{
		                    field: 'taskTitle',
		                    title: '详情标题',
		                    visible:false
		                },{ // 列设置
							field: 'monitorName',
		                    title: '监控项名称',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            },
    		                width:160
		                },{
		                    field: 'monitorParams',
		                    title: '监控项参数',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'taskType',
		                    title: '任务类型',
		                    formatter: function (value, row, index) {  
		                    	var type = "";
	                        	if(value==2){
	                        		type = '主动上报';
	                        	}else if(value==3){
	                        		type = '导出任务';
	                        	}else if(value==4){
	                        		type = 'DPI策略';
	                        	}else if(value==5){
	                        		type = '文件上报 ';
	                        	}else if(value==6){
	                        		type = 'Azkaban任务 ';
	                        	}else if(value==99){
	                        		type = '系统消息 ';
	                        	}
	                        	return "<span title='"+type+"'>"+type+"</span>";
	                        },
                            width:80   
		                },{
		                    field: 'status',
		                    title: '处理状态',
		                    formatter: function (value, row, index) {  
	                        	if(value==0){
	                        		return "<span class='badge badge-primary'>正    常</span>";
	                        	}else if(value==1){
	                        		return "<span class='badge badge-danger'>异    常</span>";
	                        	}
	                        },
			                width:70   
		                },{
		                    field: 'createTime',
		                    title: '创建时间',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            },
			                width:148 
		                },{
		                    field: 'modifyTime',
		                    title: '更新时间',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            },
			                width:148
		                },{           
		                    title:'操作',
		                	formatter:function(value,row,index){
		                		var detailBtn = "<a href='#' id='id_"+row.monitorTaskId+"' onclick='detailRow("+index+")' class='m-r'><i class='fa fa-file-text fa-lg'></i></a>";
		                		return detailBtn;
		                	},
			                width:50 
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
			$('#detailParam').append('<div class="detail-append form-group form-inline m-r-md">监控内容：'+content+'</div>')
			 				 .append('<div class="detail-append form-group form-inline m-r-md">监控参数：'+params+'</div>');
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
                                if($("#zf401002_redo").val()==1){
                                    execResult+='<a href="javascript:void(0)" class="btn btn-success btn-xs demo1" onclick="resend('+e.dataId+','+e.taskSubtype+','+e.taskId+','+e.monitorTaskId+')">重试</a>';
                                }
                                execResult+='</span></p>';

								//execResult='<a href="javascript:void(0)" class="btn btn-success btn-xs demo1" onclick="resend('+e.dataId+','+e.taskSubtype+','+e.monitorTaskId+')">重试</a></span></p>';
							}else if(e.status==3){
                                execResult='<p>执行结果：<span class="text-danger">策略下发失败 ' ;
                                if($("#zf401002_redo").val()==1){
                                    execResult+='<a href="javascript:void(0)" class="btn btn-success btn-xs demo1" onclick="resend('+e.dataId+','+e.taskSubtype+','+e.taskId+','+e.monitorTaskId+')">重试</a>' ;
                                }
                                execResult+='</span></p>';
								//execResult='<p>执行结果：<span class="text-danger">策略下发失败 <a href="javascript:void(0)" class="btn btn-success btn-xs demo1" onclick="resend('+e.dataId+','+e.taskSubtype+','+e.monitorTaskId+')">重试</a></span></p>';
							}else if(e.status==5){
                                execResult='<p>执行结果：<span class="text-warning">策略下发超时 ';
                                if($("#zf401002_redo").val()==1){
                                    execResult+='<a href="javascript:void(0)" class="btn btn-success btn-xs demo1" onclick="resend('+e.dataId+','+e.taskSubtype+','+e.taskId+','+e.monitorTaskId+')">重试</a>';
                                }
                                execResult+='</span></p>';
								//execResult='<p>执行结果：<span class="text-warning">策略下发失败 <a href="javascript:void(0)" class="btn btn-success btn-xs demo1" onclick="resend('+e.dataId+','+e.taskSubtype+','+e.monitorTaskId+')">重试</a></span></p>';
							}
						}else if(e.taskType==5){//文件上报
							
							var jsonObj = taskObject.initJson(e,title,count,content,params);
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
								   + '<span class="text-danger m-r-sm">失败文件数（'+e.failNtaskObjectum+'）</span>'
								   + '<span class="text-success m-r-sm">成功文件数（'+e.successNum+'）</span>'
							       + "<a href='#' data-toggle='modal' data-target='#detailFile' class='btn btn-info btn-xs demo1' onclick='showFile("+json+")'>详情</a>"
								   + "</span></p>";
						}else{
							if(e.status==4){
								execResult='<p>执行结果：<span class="text-navy">'+title+'成功 </span></p>';
							}else if(e.status==3){
								execResult='<p>执行结果：<span class="text-danger">'+title+'失败 </span></p>';
							}else if(e.status==5){
								execResult='<p>执行结果：<span class="text-warning">'+title+'失败 </span></p>';
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
						taskObject.clickMore();
					}
				}
			});
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
		//查询
		searchData:function(){
			$("#searchFormButton").click(function(){
				taskObject.refreshData();
			});
		},
		//返回
		returnToDetail:function(){
			$(":button[name='returnFileBtn']").click(function(){
				
				//locationdetail的初始化
				taskObject.initDetail();
			});
		},
		//1:根据查询条件刷新列表
		refreshFileData:function(){
			var data = $("#searchFileForm").formToJSON();
			$("#detail-table").bootstrapTable('refresh', { url: "/monitor/detail/listFile",query:data});
		},
		//4:主页数据及初始化
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
				taskObject.refreshFileData();
			});
		},
		//初始化
		init:function(){
			initDate(3,false,false);
			taskObject.initData();
			taskObject.searchData();
			taskObject.initFileData();
			taskObject.searchFileData();
			
		}
	};

taskObject.init();
//定时刷新主页面数据。
//setInterval(function(){taskObject.refreshData()},getRefreshTime());

function refresh(){

    var pageNum=$('#table').bootstrapTable('getOptions').pageNumber;
    if(pageNum == 1){
        taskObject.refreshData();
    }
    setTimeout("refresh()",getRefreshTime());

/*    taskObject.refreshData();
	setTimeout("refresh()",getRefreshTime());*/
}
refresh();

function getRefreshTime(){
    var times=null;
    $.ajax({
        url: "getRefreshTime",
        async:false,
        data:{},
        type:'POST',
        success : function(data) {
            times=data*1000;
        }
    });
    return times;
}
function detailRow(index) {
	$('#detailModal').modal();
	var obj = $("#table").bootstrapTable('getData');
	var taskObj=obj[index];
	
	var monitorTaskId=taskObj.monitorTaskId,
    taskType=taskObj.taskType,
    taskSubtype=taskObj.taskSubtype,
    title=taskObj.taskTitle,
    content=taskObj.monitorName,
    params=taskObj.monitorParams;
	
	$("#monitorTaskId").val(monitorTaskId);
	$("#title").val(title);
	$("#count").val(5);
	$("#content").val(content);
	$("#params").val(params);
	//detail的初始化
	taskObject.initDetail();
}
//重发策略 messageType, messageNo
function resend(messageNo,messageType,taskId,monitorTaskId){
	$.ajax({
		url: "/monitor/detail/resend2",
		async:false,
		data:{
		    dataId:messageNo,
		    taskSubtype:messageType,
            taskId:taskId,
            monitorTaskId:monitorTaskId,
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
		}
	});
}

function showFile(){
	taskObject.initQueryParam(json);
	taskObject.initFileStatus(json.taskType);
	taskObject.refreshFileData();
	taskObject.returnToDetail();
}


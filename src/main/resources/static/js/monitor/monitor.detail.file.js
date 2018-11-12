/***********************************************************************************
TableObject	 *         
			 *          refreshData：刷新数据
			 *          initData：获取数据
			 *          searchData：搜索数据
			 *          init：初始化所有的方法
 **********************************************************************************
 function    *          
             *          
             *          
 ********************************************************************************/  
var TableObject = {
		//1:根据查询条件刷新列表
		refreshData:function(){
			var data = $("#searchForm").formToJSON();
			$("#detail-table").bootstrapTable('refresh', { url: "/monitor/detail/listFile",query:data});
		},
		//4:主页数据及初始化
		initData: function(){
			//主页列表初始化
			var listInit = {
					url: "/monitor/detail/listFile?"+Math.random(),
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
	    				        taskType:$('#taskType').val(),
	    				        monitorTaskId:$('#monitorTaskId').val(),
	    				        taskId:$('#taskId').val()
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
		                    title: 'IP地址',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'recordNum1',
		                    title: '记录数',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
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
		                    title: '创建时间',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                }
		            ] 
				};
			
			$("#detail-table").bootstrapTable(listInit);
		},
		//查询
		searchData:function(){
			$("#searchFormButton").click(function(){
				TableObject.refreshData();
			});
		},
		autoClick:function(url,monitorTaskId){
		    $("#monitorTaskId").val(monitorTaskId);
		    $("#searchFormButton").trigger('click');
		   $("#id_"+monitorTaskId).off("click").trigger('click');
	     },
	     returnHtml:function(htmlUrl){
	    	 window.location.href = htmlUrl;
	     },
		//返回
		returnIndex:function(){
			$("#returnBtn").click(function(){
				var type=$("#type").val();
				if(type==1){
					var htmlUrl = "/monitor/alarm/index";
					TableObject.returnHtml(htmlUrl);
					
					var url = "/monitor/alarm/list";
					TableObject.autoClick(url,$('#monitorTaskId').val());
				}else{
					var htmlUrl = "/monitor/task/index";
					TableObject.returnHtml(htmlUrl);
					
					var url = "/monitor/task/list";
					TableObject.autoClick(url,$('#monitorTaskId').val());
				}
				
			});
		},
		//初始化
		init:function(){
			TableObject.initData();
			TableObject.searchData();
			TableObject.returnIndex();
		}
	};

TableObject.init();


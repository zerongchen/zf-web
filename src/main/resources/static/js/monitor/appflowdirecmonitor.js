/***********************************************************************************
TableObject	 *          initEchart：echarts图的加载
			 *          refreshData：刷新数据
			 *          initData：获取数据
			 *          refreshDetail：刷新详情
			 *          initDetail：初始化详情页面
			 *          searchData：搜索数据
			 *          init：初始化所有的方法
 **********************************************************************************
 function    *          queryParams:查询条件
             *          zTreeOnClick：ztree的点击事件
             *          detailRow:详情页面
 ********************************************************************************/  
 
 /******************* code beginning*********************************/
var TableObject = {
		buttonClick:function(){
			//时间粒度按钮组的处理事件
			$(":button[name='btnType']").click(function(){
				$(":button[name='btnType']").each(function(){
					if($(this).hasClass("btn-success")){
						$(this).removeClass("btn-success").addClass("btn-white");
					}
				});
				$(this).removeClass("btn-white").addClass("btn-success");
				$("#tableType").val($(this).val());
				TableObject.refreshData();
			});
		},
	    initEchart:function(jsonData){
	    	if(jsonData==undefined){
	    		jsonData = $("#searchForm").formToJSON();
	    	}
	    	jsonData.dateType=$('#searchForm').find('.btn-success').attr("value");
			var myChart = echarts.init(document.getElementById('mychart'));
			$.ajax({
				url: "getChart",
				async:false,
				data:jsonData,
				type:'POST',
				success : function(result) {
					if(result == null){
						myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
					}else{
						option = {
							 title: result.title,
							 tooltip: {
							        trigger: 'axis',
							        axisPointer: {
							            type: 'cross',
							            crossStyle: {
							                color: '#999'
							            }
							        }
							   },
							  toolbox: {
								    show : false,
							        feature: {
							            dataView: {show: true, readOnly: false},
							            magicType: {show: true, type: ['line', 'bar']},
							            restore: {show: true},
							            saveAsImage: {show: true}
							        }
							    },
							    legend: {
							        data:result.legend
							   },
							   xAxis: {
							        type: 'category',
							        data: result.axis,
							        axisPointer: {
						                type: 'shadow'
						            }
							    },
							    yAxis: [
							    	{
							            type: 'value',
							            name: '文件大小',
							            axisLabel: {
							            	formatter: '{value} '+ result.unit
							            }
							        },
							        {
							            type: 'value',
							            name: '文件数',
							            axisLabel: {
							                formatter: '{value} 个'
							            }
							        }
							    ],
							    dataZoom: result.dataZoom,
							    series: result.series
							};
						
						myChart.setOption(option);
					}
				},
				error : function() {
				}
			});
		},
		refreshData:function(){
			var data = $("#searchForm").formToJSON();
			data.dateType = $('#searchForm').find('.btn-success').attr("value");
			if (data.startTime == "" || data.endTime == "") {
                swal("请选择上报时间段");
                return false;
            }
			$("#table").bootstrapTable('refresh', { url: "list",query:data});
			TableObject.initEchart(data);
		},
		initData: function(){
			//主页列表初始化
			var listInit = {
					url: "list?"+Math.random(),
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
	    				        dateType:$('#searchForm').find('.btn-success').attr("value")
	    				    }
	    			},
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
	                pageList: [10, 25, 50, 100, 200],
					icons: {
						refresh: "glyphicon-repeat",
						toggle: "glyphicon-list-alt",
						columns: "glyphicon-list",
					},
					striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "asc",//排序方式
					clickToSelect:false, //是否启用点击选中行
					columns: [
						{ // 列设置
							field: 'createTime',
		                    title: '生成时间',
		                    formatter:function(value,row,index){
		                    	if(value==null){return "";}else{ return value;}
		                    }
		                },{
		                    field: 'creatFileSizeUnit',
		                    title: '文件总大小',
		                    formatter:function(value,row,index){
		                    	if(value==null){return "";}else{ return value;}
		                    }
		                },{
		                    field: 'createFileNum',
		                    title: '生成文件数（个）',
		                    formatter:function(value,row,index){
		                    	if(value==null){return "";}else{ return value;}
		                    }
		                },{
		                    field: 'createFileRecord',
		                    title: '生成记录数（条）',
		                    formatter:function(value,row,index){
		                    	if(value==null){return "";}else{ return value;}
		                    }
		                },{           
		                    title:'详情',
		                	formatter:function(value,row,index){
		                		var detailBtn = "<a href='#' title='文件详情' data-toggle='modal' data-target='#detailModal' onclick='detailRow("+index+")' class='m-r'><i class='fa fa-file-text-o fa-lg'></i></a>" ;
		                		return detailBtn ;
		                	}
		                }
		            ] 
				};
			
			$("#table").bootstrapTable(listInit);
		},
		refreshDetail:function(jsonObj){
			$("#detailTable").bootstrapTable('refresh', { url: "listDetail",query:jsonObj});
		},
		initDetail: function(){
			var cloum = [
				{ // 列设置
					field: 'fileName',
                    title: '文件名',
                    formatter:function(value,row,index){
                    	if(value==null){return "";}else{ return value;}
                    },
                },{
                    field: 'createFileSize',
                    title: '生成文件大小（KB）',
                    formatter:function(value,row,index){
                    	if(value==null){return "";}else{ return value;}
                    }
                },{
                    field: 'uploadFileSize',
                    title: '上报文件大小（KB）',
                    formatter:function(value,row,index){
                    	if(value==null){return "";}else{ return value;}
                    }
                },{
                    field: 'filecreateTime',
                    title: '文件生成时间',
                    sortable:true,
                    formatter:function(value,row,index){
                    	if(value==null){return "";}else{ return value;}
                    }
                },{
                    field: 'fileuploadTime',
                    title: '文件上报时间',
                    formatter:function(value,row,index){
                    	if(value==null){return "";}else{ return value;}
                    }
                },{
                    field: 'dpiIp',
                    title: '上报服务器',
                    formatter:function(value,row,index){
                    	if(value==null){return "";}else{ return value;}
                    }
                }
            ] ;
			var listInit = {
					url: "listDetail",
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					pagination: true,
					dataField: "list",
					sidePagination: "server",
	                pageNumber: 1,
	                pageSize: 10,
	                pageList: [10, 25, 50, 100, 200],
	                queryParamsType:'limit',
	                striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "desc",//排序方式
					clickToSelect:false, //是否启用点击选中行
		    		queryParams:function(params){
		    				 return {
		    				        //每页多少条数据
		    				        pageSize: params.limit,
		    				        //请求第几页
		    				        pageIndex:params.offset/params.limit+1,
		    				        //form表单的数据
		    				        statTime:$('#statTime').val(),
		    				        dateType:$('#searchForm').find('.btn-success').attr("value"),
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
	    			onLoadSuccess:function(){
				            $('.bootstrap-table tr td').each(function () {
				                $(this).attr("title", $(this).text());
				                $(this).css("cursor", 'pointer');
				            });
				        },
					columns: cloum
				};
			
			$("#detailTable").bootstrapTable('destroy').bootstrapTable(listInit);
		},
		//查询
		searchData:function(){
			$("#searchFormButton").click(function(){
				TableObject.refreshData();
			});
		},
	    /*****************zTree code ---end********************************/
		//初始化
		init:function(){
			initDate(3,true,false);
			TableObject.initData();
			TableObject.initDetail();
			TableObject.searchData();
			TableObject.initEchart();
			TableObject.buttonClick();
			
		}
	};

TableObject.init();

//
function detailRow(index) {
	var obj = $("#table").bootstrapTable('getData');
	var appflow=obj[index];
	var tableType = $('#tableType').val();
	$("#statTime").val(appflow.statTime);
	var jsonObj = {
			statTime:appflow.statTime,
			dateType:appflow.dateType
	};
	//detail的初始化
	TableObject.refreshDetail(jsonObj);
	
	
}
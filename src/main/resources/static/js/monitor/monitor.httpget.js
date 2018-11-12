/***********************************************************************************
TableObject  * zTree:
		     *      变量 setting: 树的设置
		     *      方法 getNodes：根据类型获取树节点
			 *          initTree：初始化树
			 *          clickAction：初始化查询时间粒度
			 *          initEchart：echarts图的加载
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
		
		setting: {
				view: {
					showIcon: false
				},
				data: {
					simpleData: {
						enable: true,
						pIdKey: "pid",
					}
				},
				callback: {
					onClick: zTreeOnClick
				}
		},
		getNodes:function(){
			var nodeData;
			$.ajax({
				url: "/tree/getUploadTree",
				async:false,
				type:'POST',
				dataType:"json",
				success : function(result) {
					nodeData = result;
				},
				error : function() {
					alert("对不起，没有数据！");
				}
			});
			return nodeData;
		},
		initTree: function(){
			$.fn.zTree.init($("#areaTree"), TableObject.setting, TableObject.getNodes()).expandAll(true);
		},
		clickAction:function(){
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
							            name: '文件数',
							            axisLabel: {
							                formatter: '{value} 个'
							            }
							        },
							        {
							            type: 'value',
							            name: '文件总大小',
							            axisLabel: {
							                formatter: '{value}'+result.unit
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
					alert("对不起，没有数据！");
				}
			});
		},
		refreshData:function(){
			var data = $("#searchForm").formToJSON();
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
					showColumns: false,  //显示下拉框勾选要显示的列
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
	    				        tableType:$('#tableType').val(),
	    				        level:$('#level').val(),
	    				        probeType:$('#probeType').val(),
	    				        areaId:$('#areaId').val(),
	    				        softwareProvider:$('#softwareProvider').val()
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
					clickToSelect:false, //是否启用点击选中行
					onLoadSuccess:function(){
			            $('.bootstrap-table tr td').each(function () {
			                $(this).attr("title", $(this).text());
			                $(this).css("cursor", 'pointer');
			            });
			        },
					columns: [
						{ // 列设置
							field: 'receivedTime',
		                    title: '接收时间',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{ // 列设置
							field: 'statTime',
		                    title: '统计时间',
		                    visible:false,formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'areaId',
		                    title: '区域ID',
		                    visible:false,formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'probeType',
		                    title: '上报类别',
		                    visible:false,formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'softwareProvider',
		                    title: '上报厂家',
		                    visible:false,formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'receivedFileSizeStr',
		                    title: '接收文件总大小',
		                	class:"text-warning",formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'uploadFileSizeStr',
		                    title: '上报文件总大小',
		                	class:"text-success",formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'receivedFileNum',
		                    title: '接收文件数',
		                    class:"text-warning",formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'uploadFileNum',
		                    title: '上报文件数',
		                    class:"text-success",formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{           
		                    title:'详情',
		                	formatter:function(value,row,index){
		                		var detailBtn = "<a href='#' title='文件详情' data-toggle='modal' data-target='#detailModal' onclick='detailRow("+index+")' class='m-r'><i class='fa fa-file-text fa-lg'></i></a>" ;
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
			var listInit = {
					url: "listDetail",
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					pagination: true,
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
	    				        statTime:$('#statTime').val(),
	    				        tableType:$('#tableType').val(),
	    				        probeType:$('#probeType').val(),
	    				        areaId:$('#areaId').val(),
	    				        softwareProvider:$('#softwareProvider').val()
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
	                showColumns: false,  //显示下拉框勾选要显示的列
					striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "asc",//排序方式
					clickToSelect:false, //是否启用点击选中行
					onLoadSuccess:function(){
			            $('.bootstrap-table tr td').each(function () {
			                $(this).attr("title", $(this).text());
			                $(this).css("cursor", 'pointer');
			            });
			        },
					columns: [
						{ // 列设置
							field: 'fileName',
		                    title: '文件名',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'dpiIp',
		                    title: '上报服务器',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'filecreateTime',
		                    title: '文件生成时间',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'filereceivedTime',
		                    title: '文件接收时间',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'fileSize',
		                    title: '文件大小',
		                    formatter:function(value,row,index){
		                		return value +" KB" ;
		                	}
		                },{
		                    field: 'fileuploadTime',
		                    title: '文件上报时间',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                }
		            ] 
				};
			
			$("#detailTable").bootstrapTable(listInit);
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
			initDate(3,false);
			TableObject.initTree();
			TableObject.initData();
			TableObject.initDetail();
			TableObject.searchData();
			TableObject.initEchart();
			TableObject.clickAction();
			
		}
	};

TableObject.init();
//树点击的回调函数
function zTreeOnClick(event, treeId, treeNode) {
    // console.log("level = " + treeNode.level+ ", name = "+treeNode.name +", id = "+ treeNode.id+",probeType = "+treeNode.probeType);
    if(treeNode.level == 0){//全省
    	$("#level").val(treeNode.level);
    	TableObject.refreshData();
    }else if(treeNode.level == 1){//DPI、EU
    	$("#level").val(treeNode.level);
    	$("#probeType").val(treeNode.probeType);
    	TableObject.refreshData();
    }else if(treeNode.level == 2){//区域、机房
    	$("#level").val(treeNode.level);
    	$("#probeType").val(treeNode.probeType);
    	$("#areaId").val(treeNode.id);
    	TableObject.refreshData();
    }else if(treeNode.level == 3){//厂商
    	var id = treeNode.id;
    	$("#level").val(treeNode.level);
    	$("#probeType").val(treeNode.probeType);
    	$("#areaId").val(treeNode.pid);
    	$("#softwareProvider").val(id.split("_")[1]);
    	TableObject.refreshData();
    }
}

function detailRow(index) {
	var obj = $("#table").bootstrapTable('getData');
	var httpget=obj[index];
	var tableType = $('#tableType').val();
	$('#statTime').val(httpget.statTime);
	var jsonObj = {
			statTime:httpget.statTime,
			tableType:tableType,
			areaId:httpget.areaId,
			probeType:httpget.probeType,
			softwareProvider:httpget.softwareProvider
	};
	// console.log("index="+index+", statTime = "+httpget.statTime);
	//detail的初始化
	TableObject.refreshDetail(jsonObj);
}
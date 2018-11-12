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
	    initEchart:function(jsonData){
	    	if(jsonData == undefined){
	    		jsonData = $("#searchForm").formToJSON();
	    	}
	    	jsonData.dateType = $(".btn-success").val();
	    	for(var i =0; i<4; i++) {
	    		var $currDiv = $('#mychart'+i);
				var myChart = echarts.init($currDiv[0]);
				jsonData.fileType = $currDiv.attr("value");
				$.ajax({
					url: "getChart",
					async: false,
					data: jsonData,
					type: 'POST',
					success : function(responResult) {
						$('#mychart'+i+'Title').text(responResult.message);
						let result=responResult.data.data;
						let fileType = responResult.data.type;
						if(result == null){
							myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
						} else {
							option = {
                                title : {
                                	text:'',
                                    left:'center',
                                    itemGap:20,
                                    subtext: '',
									textStyle:{
                                		fontSize:12
									}
                                },
                                calculable : true,
                                grid: {y: 100, y2:30,x1:120, x2:120},
                                tooltip: {
                                    trigger: 'axis',
                                    axisPointer: {
                                        type: 'cross',
                                        crossStyle: {
                                            color: '#999'
                                        }
                                    },
                                },
								toolbox:{
                                	show:true,
                                    feature : {
                                        mark : {show: false},
                                        dataView : {show: false, readOnly: false},
                                        magicType : {show: false, type: ['line', 'bar']},
                                        restore : {show: false},
                                        saveAsImage : {show: false}
                                    }
                                },
                                legend: {
                                    data:result.legend,
                                    top:'10%'
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
			};
		}
		,
		refreshData:function(){
			TableObject.initEchart();
		},
		initData: function(fileType){
			//主页列表初始化
			var listInit = {
					url: "list?"+Math.random(),
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					dataField: "list",
					pagination: true,
					showColumns: false,  //显示下拉框勾选要显示的列
					toolbar: "#table-toolbar",
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
							 	dateType:$(".btn-success").val(),
	    				        startTime:$('#start').val(),
							    fileType:fileType,
	    				        endTime:$('#end').val()
	    				        
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
							field: 'uploadTime',
		                    title: '上报时间',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'uploadFileSizeStr',
		                    title: '上报文件总大小',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'uploadFileNum',
		                    title: '上报文件数（个）',                    				
		                	formatter:function(value,row,index){
		                		if(value<=0){
		                			return "0";
		                		}else{
		                			return "<a href='#' title='文件详情' onclick='detail(1)'  class='m-r fileDetail'>"+value+"</a>" ;
		                		}
		                	},
                            events:operatingEvents,
		                },{ 
		                  	field: 'totalAbnormalFileNum',                         
		                    title: '异常文件数（个）',			                    				
		                	formatter:function(value,row,index){
		                		if(value<=0){
		                			return "0";
		                		}else{
		                			return "<a href='#' title='文件详情'  onclick='detail(2)'  class='m-r fileDetail'>"+value+"</a>" ;
		                		}
		                	},
                            events:operatingEvents,
		                }
		            ] 
				};
				$("#table").bootstrapTable('destroy').bootstrapTable(listInit);
  		},
		refreshDetail:function(jsonObj){
			$("#detailTable").bootstrapTable('refresh', { url: "listDetail",query:jsonObj});
		},
  
		initDetail: function(jsondata){
			let colum=[];
			if(jsondata.fileType==768){
                colum = [
                    { // 列设置
                        field: 'fileName',
                        title: '文件名'
                    },{
                        field: 'dpiIp',
                        title: '<span title="DPI上报服务器">DPI上报服务器</span>'
                    },{
                        field:'factoryFrom',
                        title: '<span title="来源厂家">来源厂家</span>',
                        formatter:providerFormatter
                    },{
                        field: 'recieveFileSize',
                        title: '<span title="接收文件大小(KB)">接收文件大小(KB)</span>',
                        formatter:function(value,row,index){
                        	if(value==null){return "";}else{ return value;}
                        }
                    },{
                        field: 'uploadFileSize',
                        title: '<span title="上报文件大小 (KB)">上报文件大小 (KB)</span>',
                        formatter:function(value,row,index){
                        	if(value==null){return "";}else{ return value;}
                        }
                    },{
                        field: 'uploadIp',
                        title: '<span title="上报服务器">上报服务器</span>'
                    },{
                        field: 'filecreateTime',
                        sortable:true,
                        title: '<span title="文件生成时间">文件生成时间</span>',
                        formatter:function(value,row,index){
                        	if(value==null){return "";}else{ return value;}
                        }
                    },{
                        field: 'filerecieveTime',
                        title: '<span title="文件接收时间">文件接收时间</span>',
                        formatter:function(value,row,index){
                        	if(value==null){return "";}else{ return value;}
                        }
                    },{
                        field: 'fileuploadTime',
                        title: '<span title="文件上报时间">文件上报时间</span>',
                        formatter:function(value,row,index){
                        	if(value==null){return "";}else{ return value;}
                        }
                    },{
                        field: 'warnType',
                        title: '<span title="文件异常描述">文件异常描述</span>',
						formatter:operatingFormatter
                    }

                ]
            }else {
                colum = [
                    { // 列设置
                        field: 'fileName',
                        title: '文件名'
                    },{
                        field: 'createFileSize',
                        title: '<span title="生成文件大小(KB)">生成文件大小(KB)</span>',
                    },{
                        field: 'uploadFileSize',
                        title: '<span title="上报文件大小(KB)">上报文件大小(KB)</span>',
                        formatter:function(value,row,index){
                        	if(value==null){return "";}else{ return value;}
                        }
                    },{
                        field: 'uploadIp',
                        title: '<span title="上报服务器">上报服务器</span>',
                        formatter:function(value,row,index){
                        	if(value==null){return "";}else{ return value;}
                        }
                    },{
                        field: 'filecreateTime',
                        sortable:true,
                        title: '<span title="文件生成时间">文件生成时间</span>', 
                        formatter:function(value,row,index){
                        	if(value==null){return "";}else{ return value;}
                        }
                    },{
                        field: 'fileuploadTime',
                        title: '<span title="文件上报时间">文件上报时间</span>',
                        formatter:function(value,row,index){
                        	if(value==null){return "";}else{ return value;}
                        }
                    },{
                        field: 'warnType',
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
	    				        dateType:$(".btn-success").val(),
	    				        fileType:jsondata.fileType,
	    				        detailType:jsondata.detailType,
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

			$("#detailTable").bootstrapTable('destroy').bootstrapTable(listInit);
		},
		//查询
		searchData:function(){
			$("#searchFormButton").click(function(){
				TableObject.refreshData();
			});
		},
	    buttonInit:function(){
	    	$('#aaaDataDetail').click(function(){
	    		$('#moreDetail').modal('show');
                TableObject.initData(1023);
	    	});
	    	$('#httpGetDetail').click(function(){
	    		$('#moreDetail').modal('show');
                TableObject.initData(768);
	    	});
	    	$('#appFlowDetail').click(function(){
	    		$('#moreDetail').modal('show');
                TableObject.initData(452);
	    	});
	    	$('#allFlowDetail').click(function(){
	    		$('#moreDetail').modal('show');
                TableObject.initData(258);
	    	});
	    	$('#mychart0Title').click(function(){
	    		$('#moreDetail').modal('show');
                TableObject.initData(1023);
	    	});
	    	$('#mychart1Title').click(function(){
	    		$('#moreDetail').modal('show');
                TableObject.initData(768);
	    	});
	    	$('#mychart2Title').click(function(){
	    		$('#moreDetail').modal('show');
                TableObject.initData(452);
	    	});
	    	$('#mychart3Title').click(function(){
	    		$('#moreDetail').modal('show');
                TableObject.initData(258);
	    	});
	    	$(":button[name='btnType']").click(function(){
				$(":button[name='btnType']").each(function(){
					if($(this).hasClass("btn-success")){
						$(this).removeClass("btn-success").addClass("btn-white");
					}
				});
				$(this).removeClass("btn-white").addClass("btn-success");
				TableObject.refreshData();
			});
	    },
		//初始化
		init:function(){
			initDate(3,true,false);
			// TableObject.initData();
			// TableObject.initDetail();
			TableObject.searchData();
			TableObject.initEchart();
			TableObject.buttonInit();
			
		}
	};

$(document).ready(function() {
    TableObject.init();
});
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
//AOT:傲天，HW:华为，KUG:宽广，XIF:绿网,LVW:信风,ZTE:中兴,ZZN:直真
function providerFormatter(value, row, index) {
	return '<span title="'+value+'">'+value+'</span>';
}
var  detailListType;
function detail(detailType){

	 detailListType=detailType;
return detailListType;
}
//点击事件
window.operatingEvents = {

    'click .fileDetail': function (e, value, row, index) {
        e.preventDefault();
        if(value==0){
            swal("没有异常文件");
        }else{
			$('#detailModal').modal('show');
            $('#statTime').val(row.uploadTime);
            var jsonObj = {
                statTime:row.uploadTime,
                fileType:row.fileType,
				detailType:detailListType
            };
            //detail的初始化
            TableObject.initDetail(jsonObj);
        }
    },
};
/***********************************************************************************
TableObject  * zTree:
		     *      变量 setting: 树的设置
		     *      方法 getNodes：根据类型获取树节点
			 *          initTree：初始化树
			 *          clickAction：点击事件的处理
			 *          initEchart：echarts图的加载
			 *          refreshData：刷新数据
			 *          getData：获取数据
			 *          searchData：搜索数据
			 *          init：初始化所有的方法
 **********************************************************************************
 function    *          queryParams:查询条件
             *          zTreeOnClick：ztree的点击事件
 ********************************************************************************/

 /******************* code beginning*********************************/
var TableObject = {

		setting: {
				view: {
					showIcon: true
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

			//图类型的切换
			$(":input[name='chartType']").click(function(){

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
				async:true,
				data:jsonData,
				type:'POST',
				success : function(result) {
					if(result == null){
						myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
					}else{
						option = {
							 title: '',
							 tooltip : {
							        trigger: 'axis',
							        axisPointer: {
							            type: 'cross',
							            label: {
							                backgroundColor: '#6a7985'
							            }
							        }
							    },
							    grid: {
							        left: '3%',
							        right: '4%',
							        bottom: '3%',
							        containLabel: true
							    },
							    legend: {
							        data:result.legend,
									top:'-1%',
									left:'12%',
                                    textStyle:{
                                        fontSize:10
                                    }
							   },
							   xAxis: {
							        type: 'category',
							        data: result.axis,
							        boundaryGap : false
							    },
							    yAxis : [
							        {
							            type : 'value',
                                        name: '接收记录数',
                                        axisLabel: {
                                            formatter: '{value} 条'
                                        }
							        }
							    ],
							    dataZoom: result.dataZoom,
							    series: result.series
							};
                        var data = $("#searchForm").formToJSON();
						if(data.chartType==1){
                            option.yAxis[0].name="接收记录数";
						}else if(data.chartType==2){
                            option.yAxis[0].name="入库记录数";
						}
                        myChart.clear();
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
	    				        fileType:$('#fileType').val(),
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
		                    field: 'receivednum',
		                    title: '接收记录数(条)',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'savenum',
		                    title: '入库记录数(条)',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'invalidrecordernum',
		                    title: '异常记录数(条)',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                },{
		                    field: 'writerkafkanum',
		                    title: '解析记录数' ,formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            }
		                }
		            ]
				};

			$("#table").bootstrapTable(listInit);
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
			TableObject.initTree();
            TableObject.initEchart();
			TableObject.initData();
			TableObject.searchData();
			TableObject.clickAction();
		}
	};
$(document).ready(function() {
    //目录树的固定宽度转化为百分比来计算
    var w = $(window).width();
    var h = $(window).height();
    $('#treeWidthFixed').css('width', (220 / w) * 100 + '%');
    //计算目录树的高度
    $('#areaTree').css('height', h - 65);
    //右侧内容所占百分比
    $('#containWidthFixed').css('width', (1 - (240 / w)) * 100 + '%');
    TableObject.init();
});

//树点击的回调函数
function zTreeOnClick(event, treeId, treeNode) {

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


var imgUrl = "";

IllegalRoutes = {
		queryCriteria:function(){
			//查询时间条件
			$('#dateTypeSelect >button').click(function () {
		          $("#dateTypeSelect").find(".btn-success").removeClass("btn-success").addClass('btn-white').end();
		          $(this).removeClass('btn-white').addClass('btn-success').end();
		          var data = $('#searchForm').formToJSON();
		          data.dateType=$('#searchForm').find('.btn-success').attr("value");
		          initStateDateV5(data.dateType,"state",true);
		          if(data.stateTime=="" || data.stateTime==undefined){
		              swal("请选择时间");
		              return false;
		          }
		      });
			//地区下拉框
			loadSel("areacode", "/select/getAreaList", true, undefined,false);
			
			loadSel("carrieroperator", "/select/getCarrieroperators", true, undefined,false);
		},
		initTable:function(){
			var listInit = {
					url: "getIndexData",
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
		    			var data = $('#searchForm').formToJSON();
		    	        data.dateType=$('#searchForm').find('.btn-success').attr("value");
		    	        if(data.stateTime==''){
		    	            data.stateTime=undefined;
		    	        }
		    	        data.order=params.order;
		    	        data.sort=params.sort;
		    	        data.pageSize=params.limit;
		    	        data.pageIndex=params.offset/params.limit+1;
		    	        return data;
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
					columns: [{
	                    field: 'stattime',
	                    title: '统计时间',
	                    formatter:function(value,row,index){
	                    	if(value==null){return "";}else{
	                    		var dataType = $('#searchForm').find('.btn-success').attr("value");
	                    		var str = String(value);
	                    		var formatter = "";
	                    		if(dataType==4){
	                    			formatter = str.replace(/^(\d{4})(\d{2})$/, "$1-$2");
	                    		}else{
	                    			formatter = str.replace(/^(\d{4})(\d{2})(\d{2})$/, "$1-$2-$3");
	                    		}
	                    		return formatter;}
	                    }
	                },{
	                    field: 'nodeip',
	                    title: '接入点位置',
	                    formatter:function(value,row,index){
	                    	if(value==null){return "";}else{ return value;}
	                    }
	                },{
	                    field: 'inputflows',
	                    title: '流入流量',
	                    sortable:true,
	                    formatter:function(value,row,index){
	                    	if(value==null){return "";}else{ return value;}
	                    }
	                },{
	                    field: 'outputflows',
	                    title: '流出流量',
	                    sortable:true,
	                    formatter:function(value,row,index){
	                    	if(value==null){return "";}else{ return value;}
	                    }
	                }]
				};
			
			$("#table").bootstrapTable('destroy').bootstrapTable(listInit);
		},
		initChart:function(){
	        var data = $('#searchForm').formToJSON();
	        data.dateType=$('#searchForm').find('.btn-success').attr("value");
	        if(data.stateTime==''){
	            data.stateTime=undefined;
	        }
			var myChart = echarts.init(document.getElementById('mychart'));
			myChart.clear();
			$.ajax({
				url: "getChartData",
	            data: data,
	            dataType: 'json',
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
							            name: '流入/流出总流量',
							            axisLabel: {
							            	formatter: '{value} '+ result.unit.split("|")[0]
							            }
							        },
							        {
							            type: 'value',
							            name: '流出运营商流量',
							            axisLabel: {
							                formatter: '{value} '+ result.unit.split("|")[1]
							            }
							        }
							    ],
							    dataZoom: result.dataZoom,
							    series: result.series,
							    animation:false
							};
						
						myChart.setOption(option);
						imgUrl = myChart.getDataURL('png');
					}
				},
				error : function() {
				}
			});
		},
		refreshData:function(){
			 $("#table").bootstrapTable('refresh', { url: "getIndexData"});
			 IllegalRoutes.initChart();
		},
		buttonInit:function(){
			$('#searchFormButton').click(function () {
		          var data = $('#searchForm').formToJSON();
		          data.dateType=$('#searchForm').find('.btn-success').attr("value");
		          if(data.stateTime=="" || data.stateTime==undefined){
		            swal("请选择时间");
		            return false;
		          }
		          IllegalRoutes.refreshData();
		      });
			
		      $('#dateTypeSelect >button').click(function () {
		          $("#dateTypeSelect").find(".btn-success").removeClass("btn-success").addClass('btn-white').end();
		          $(this).removeClass('btn-white').addClass('btn-success').end();
		          var data = $('#searchForm').formToJSON();
		          data.dateType=$('#searchForm').find('.btn-success').attr("value");
		          initStateDateV5(data.dateType,"state",true);
		          if(data.stateTime=="" || data.stateTime==undefined){
		              swal("请选择时间");
		              return false;
		          }
		          IllegalRoutes.refreshData();

		      });
		      
		      $('#searchForm .exportHead >li').click(function () {
		            var text = $(this).text();
		            var exportType = 1;
		            if(text=='EXCEL'){
		                exportType = 0;
		            } else if (text=='WORLD'){
		                exportType = 1;
		            } else if (text=='PDF'){
		                exportType = 2;
		            }
		            var data = $('#searchForm').formToJSON();
		            var form = '<form class="hide" id="export">';
		            form += '<input name="exportType" value="'+exportType+'" />';
		            form += '<input name="stateTime" value="' + data.stateTime + '" />';
		            form += '<input name="dateType" value="'+ $('#searchForm').find('.btn-success').attr("value") + '" />';
		            form += '<input name="areaCode" value="'+ data.areaCode + '" />';
		            form += '<input name="cp" value="'+ data.cp + '" />';
		            form += '<input name="imgUrl" value="' + imgUrl + '" />';
		            form += '</form>';
		            $('body').append(form);
		            $('#export').attr('action', 'export?ran='+Math.random()).attr('method', 'post').submit();

		            $('#export').remove();
		        });
		},
	init:function(){
		initStateDateV5(2,"state",true);
		IllegalRoutes.queryCriteria();
		IllegalRoutes.initTable();
		IllegalRoutes.buttonInit();
		IllegalRoutes.initChart();
	}	
};
IllegalRoutes.init();

function detail(index){
	var obj = $("#table").bootstrapTable('getData');
	var appuer=obj[index];
	$("#detailStateTime").val(appuer.stattime);
	$("#detailAreaCode").val(appuer.areaid);
	$("#detailAppType").val(appuer.apptypeid);
	$("#detailAppName").val(appuer.appid);
	//detail的初始化
	IllegalRoutes.refreshDetailData();
	$('#detailModal').modal('show');
}
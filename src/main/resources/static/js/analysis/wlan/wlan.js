

Wlan = {
		queryCriteria:function(){
			//查询时间条件
			initStateDateV5(2,"state",true);
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
			//用户组
			$.ajax({
		        url: "/select/getUserGroup",
		        type: 'GET',
		        async: false,
		        dataType: 'json',
		        success: function(data){
		            var option = '';
		            $("#userGroup").children().remove();
		                option = '<option value="" >全用户</option>';
		            $.each(data, function(i, n){
		            	option += '<option value="' + n.value + '">' + n.title + '</option>';
		            });
		            $("#userGroup").append(option);
		        }
		    });
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
		    	        if(data.accounts==''){
		    	            data.accounts=undefined;
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
	                    field: 'useraccount',
	                    title: '用户账号',
	                    formatter:function(value,row,index){
	                    	if(value==null){return "";}else{ return value;}
	                    }
	                },{
	                    field: 'devicecnt',
	                    title: '终端数',
	                    sortable:true,
	                    formatter:function(value,row,index){
	                    	if(value==null){return "0";}else{ return "<a href='#' title='详情' onclick='detail("+index+")'  class='m-r detail'>"+value+"</a>" ;}
	                    }
	                }]
				};
			
			$("#table").bootstrapTable('destroy').bootstrapTable(listInit);
			
			var listDetailInit = {
					url: "getDetailData",
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					pagination: true,
					dataField: "list",
					sidePagination: "client",
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
		    	        data.dateType = $('#searchForm').find('.btn-success').attr("value");
	    	            data.stateTime = $("#detailStateTime").val();
	    	            data.areaCode = $("#detailAreaCode").val();
	    	            data.accounts = $("#detailUseraccount").val();
		    	        data.pageSize=params.limit;
		    	        data.pageIndex=params.offset/params.limit+1;
		    	        return data;
		    			},
					columns: [{
	                    field: 'devType',
	                    title: '终端类型',
	                    formatter:function(value,row,index){
	                    	if(value==null){return "";}else{ return value;}
	                    }
	                },{
	                    field: 'phoneNumber',
	                    title: '手机号码',
	                    formatter:function(value,row,index){
	                    	if(value==null){return "0";}else{ return value;}
	                    }
	                }]
				};
			
			$("#detailTable").bootstrapTable('destroy').bootstrapTable(listDetailInit);
		},
		refreshData:function(){
			 $("#table").bootstrapTable('refresh', { url: "getIndexData"});
		},
		refreshDetailData:function(){
			 $("#detailTable").bootstrapTable('refresh', { url: "getDetailData"});
		},
		buttonInit:function(){
			$('#searchFormButton').click(function () {
		          var data = $('#searchForm').formToJSON();
		          data.dateType=$('#searchForm').find('.btn-success').attr("value");
		          if(data.stateTime=="" || data.stateTime==undefined){
		            swal("请选择时间");
		            return false;
		          }
		          Wlan.refreshData();
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
		          Wlan.refreshData();

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
		            if(data.userGroups!=""){
		            	form += '<input name="userGroups" value="'+ data.userGroups + '" />';
		            }
		            if(data.accounts!=""){
		            	form += '<input name="accounts" value="'+ data.accounts + '" />';
		            }
		            form += '</form>';
		            $('body').append(form);
		            $('#export').attr('action', 'export?ran='+Math.random()).attr('method', 'post').submit();

		            $('#export').remove();
		        });
		},
	init:function(){
		Wlan.queryCriteria();
		Wlan.initTable();
		Wlan.buttonInit();
	}	
};
Wlan.init();

function detail(index){
	var obj = $("#table").bootstrapTable('getData');
	var wlan=obj[index];
	$("#detailStateTime").val(wlan.stattime);
	$("#detailAreaCode").val(wlan.areaid);
	$("#detailUseraccount").val(wlan.useraccount);
	//detail的初始化
	Wlan.refreshDetailData();
	$('#detailModal').modal('show');
}
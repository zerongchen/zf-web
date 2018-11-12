

AppUser = {
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
			//应用类别下拉框
			loadSel("apptype", "/select/getAppType", true, undefined,false);
			//应用名称下拉框
			loadSel("appname", "/select/getAppByType?appType=", true, undefined,false);
			$('#apptype').change(function(){
				var type = $(this).children('option:selected').val();
				if(type==""){
					$("#appname").children().remove();
					$("#appname").append('<option value="" >请选择</option>');
				}else{
					loadSel("appname", "/select/getAppByType?appType="+type, true, undefined,false);
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
		    	        if(data.appType=='-1'){
		    	            data.appType=undefined;
		    	        }
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
	                    field: 'apptype',
	                    title: '应用类别',
	                    formatter:function(value,row,index){
	                    	if(value==null){return "";}else{ return value;}
	                    }
	                },{
	                    field: 'appname',
	                    title: '应用名称',
	                    formatter:function(value,row,index){
	                    	if(value==null){return "";}else{ return value;}
	                    }
	                },{
	                    field: 'count',
	                    title: '访问用户数',
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
		    	        data.dateType = $('#searchForm').find('.btn-success').attr("value");
	    	            data.stateTime = $("#detailStateTime").val();
	    	            data.appType = $("#detailAppType").val();
	    	            data.areaCode = $("#detailAreaCode").val();
	    	            data.appName = $("#detailAppName").val();
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
	                    field: 'usertype',
	                    title: '用户类型',
	                    formatter:function(value,row,index){
	                    	let type = "";
	                    	if(value==0){
	                    		type = "全用户";
	            			}else if(value==1){
	            				type = "账号";
	            			}else if(value==2){
	            				type = "IP地址";
	            			}else if(value==3){
	            				type = "用户组";
	            			} 
	                    	return type;
	                    }
	                },{
	                    field: 'useraccount',
	                    title: '用户帐号',
	                    formatter:function(value,row,index){
	                    	if(value==null){return "";}else{ return value;}
	                    }
	                },{
	                    field: 'usagecount',
	                    title: '统计信息(次)',
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
		          AppUser.refreshData();
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
		          AppUser.refreshData();

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
		            form += '<input name="appType" value="'+ data.appType + '" />';
		            form += '<input name="appName" value="'+ data.appName + '" />';

		            form += '</form>';
		            $('body').append(form);
		            $('#export').attr('action', 'export?ran='+Math.random()).attr('method', 'post').submit();

		            $('#export').remove();
		        });
		},
	init:function(){
		AppUser.queryCriteria();
		AppUser.initTable();
		AppUser.buttonInit();
	}	
};
AppUser.init();

function detail(index){
	var obj = $("#table").bootstrapTable('getData');
	var appuer=obj[index];
	$("#detailStateTime").val(appuer.stattime);
	$("#detailAreaCode").val(appuer.areaid);
	$("#detailAppType").val(appuer.apptypeid);
	$("#detailAppName").val(appuer.appid);
	//detail的初始化
	AppUser.refreshDetailData();
	$('#detailModal').modal('show');
}
function clientTable(url,colums,id,toolId){
	var table = {
		url: url+"?"+Math.random(),
		method: 'post',
		contentType : "application/x-www-form-urlencoded",
		dataType: "json",
		pagination: true,
		showColumns: false,
		toolbar: "#"+toolId,
		iconSize: "outline",
		sidePagination: "client",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100, 200],
		icons: {
			refresh: "glyphicon-repeat",
			toggle: "glyphicon-list-alt",
			columns: "glyphicon-list",
		},
		sortable: true,//是否启用排序
        sortOrder: "asc",//排序方式
		clickToSelect:false, //是否启用点击选中行
		columns:colums
	};
	
	$("#"+id).bootstrapTable(table);
}
var ParamSetting ={
buttonInit:function(){
	//搜索引擎模块按钮事件绑定
	$("#addSearch").click(function(){
		$(".searchtips").remove();
		$("#seaInputAdd").show();
		if($("#addSearchContent").css("display") == "none"){
			$("#searchInput").val("");
			$("#seId").val("");
			$(".search").parent().parent().parent().remove();
		}
		if($("#seaContent").css("display") == "none"){
			$("#addSearchContent").show();
			$("#seaColl").attr("class","fa fa-chevron-down");
			$("#seaContent").show();
		}else{
			$("#addSearchContent").show();
		}
	});
	
	$("#searchCancel").click(function(){
		$("#addSearchContent").hide();
	});
	
	$("#searchModify").click(function(){
		var search = $("#searchEngine").bootstrapTable('getSelections');
		if(search.length != 1){
			swal("请选择一条搜索引擎修改！");
			return false;
		}
		$("#searchInput").val(search[0].sename);
		$("#seId").val(search[0].seId);
		$("#seaInputAdd").hide();
		$(".searchtips").remove();
		$(".search").parent().parent().parent().remove();
		$("#addSearchContent").show();
	});
	
	var searchAdd="<div class='form-group'><label class='col-md-3 control-label p-n'>搜索引擎</label><div class='col-md-4'>"+ "<input type='text' name='searchEngine' class='form-control frominput'></div>"+"<div class='col-md-1'><span class='help-block m-b-none tips'><a class='sourse-a m-l-n m-r-sm check-minus search'><i class='fa fa-minus'></i></a></span></div></div>"
	
	$("#seaInputAdd").click(function () {
    $(this).parent().parent().parent().parent().after(searchAdd);
		$(".check-minus").click(function () {
           $(this).parent().parent().parent().remove();
	    });
	});
	
	//cookie
	//搜索引擎模块按钮事件绑定
	$("#addCookie").click(function(){
		$("#cookieInputAdd").show();
		$(".ccoo").show();
		if($("#addCookieContent").css("display") == "none"){
			$("#website").val("");
			$("#keyValue").val("");
			$("#CookieId").val("");
			$("#cookieFile").val("");
			$(".cookie").parent().parent().parent().remove();
		}
		if($("#cookieContent").css("display") == "none"){
			$("#cookieColl").attr("class","fa fa-chevron-down");
			$("#addCookieContent").show();
			$("#cookieContent").show();
		}else{
			$("#addCookieContent").show();
		}
	});
	
	$("#cookieCancel").click(function(){
		$("#addCookieContent").hide();
	});
	
	$("#cookieModify").click(function(){
		var cookie = $("#cookieTable").bootstrapTable('getSelections');
		if(cookie.length != 1){
			swal("请选择一条数据修改！");
			return false;
		}
		$("#website").val(cookie[0].cookieHostName);
		$("#keyValue").val(cookie[0].cookieKeyValue);
		$("#CookieId").val(cookie[0].cookieId);
		$("#cookieInputAdd").hide();
		$("#cookieFile").val("");
		$(".cookie").parent().parent().parent().remove();
		$(".ccoo").hide();
		$("#addCookieContent").show();
	});
	
	var cookieAdd="<div class='form-group'><label class='col-md-2 control-label p-n'>网址,Key值</label><div class='col-md-3'>"+ "<input type='text' name='cookieHostName' class='form-control frominput'></div><div class='col-md-2'>"+ "<input type='text' name='cookieKeyValue' class='form-control frominput'></div>"+"<div class='col-md-1'><span class='help-block m-b-none tips'><a class='sourse-a m-l-n m-r-sm check-minus cookie'><i class='fa fa-minus'></i></a></span></div></div>"
	
	$("#cookieInputAdd").click(function () {
    $(this).parent().parent().parent().parent().after(cookieAdd);
		$(".check-minus").click(function () {
           $(this).parent().parent().parent().remove();
	    });
	});
	
},
dataBind:function(){
	var search = [
		{ // 列设置
            checkbox: true // 使用单选框
        },{
            field: 'sename',
            title: '搜索引擎',formatter:function(value,row,index){
    			return "<span title='"+value+"'>"+value+"</span>";
    		}
        }];
	clientTable("getSearchData",search,"searchEngine","searchToolbar");
	
	var paramDetail = [
		{
            field: 'messageNo',
            title: '策略ID',formatter:function(value,row,index){
    			return "<span title='"+value+"'>"+value+"</span>";
    		}
        },{
            field: 'policyCount',
            title:'<span title="策略成功数/异常数">策略成功数/异常数</span>',
        	formatter:function(value,row,index){	
        		if(value=="0/0"){
        			return "0/0";
        		}else{
        			return "<a href='#' onclick='getDetail("+index+",0)' title='详情'>"+value+"</a>";
        		}
        	}
        },{
            field: 'createTime',
            title: '<span title="生成日期">生成日期</span>',formatter:function(value,row,index){
    			return "<span title='"+value+"'>"+value+"</span>";
    		}
        },{
            field: 'policyCount',
            title: '操作', 
            formatter:function(value,row,index){
    			return "<a href='#' title='重发' onclick='policyResend()' class='m-r'><i class='fa fal fa-share resend'></i></a>";
        	}
        }];
	clientTable("getPolicyDetail",paramDetail,"paramDetailTable","111");
	var cookieTable = {
			method: 'post',
			contentType : "application/x-www-form-urlencoded",
			dataType: "json",
			pagination: true,
			showColumns: false,
			iconSize: "outline",
			sidePagination: "server",
			dataField: "list",
			url:"getCookieData",
            pageNumber: 1,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            queryParamsType:'limit',
			queryParams:queryParams,//请求服务器时所传的参数
			responseHandler: responseHandler,
			icons: {
				refresh: "glyphicon-repeat",
				toggle: "glyphicon-list-alt",
				columns: "glyphicon-list",
			},
			toolbar:"#cookieToolbar",
			sortable: true,//是否启用排序
            sortOrder: "asc",//排序方式
			clickToSelect:false, //是否启用点击选中行
			columns: [
				{ // 列设置
                    checkbox: true // 使用单选框
                },{
                    field: 'cookieHostName',
                    title: '网站',formatter:function(value,row,index){
            			return "<span title='"+value+"'>"+value+"</span>";
            		}
                },{
                	field:'cookieKeyValue',
                	title:'key值',formatter:function(value,row,index){
            			return "<span title='"+value+"'>"+value+"</span>";
            		}
                }]
			};
	
	$("#cookieTable").bootstrapTable(cookieTable);
    //请求服务数据时所传参数
    function queryParams(params){
        return{
            //每页多少条数据
            pageSize: params.limit,
            //请求第几页
            pageIndex:params.offset/params.limit+1
        }
    };
    
    function responseHandler(result) { //数据筛选
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
    };
	
	$.ajax({
		url:"getThreshold",
		type:"GET",
		success:function(data){
			$("#webHitThreshold").val(data.webHitThreshold);
			$("#kwThreholds").val(data.kwThreholds);
			$("#commonId").val(data.commonId)
		}
	});
	
},
refreshData:function(){
	$.ajax({
		url:"getThreshold",
		type:"GET",
		success:function(data){
			$("#webHitThreshold").val(data.webHitThreshold);
			$("#kwThreholds").val(data.kwThreholds);
		}
	});
	 $('#searchEngine').bootstrapTable('refresh', {url: 'getSearchData'});
	 $('#cookieTable').bootstrapTable('refresh', {url: 'getCookieData'});
},
//保存数据
saveData:function(){
	$("#searchSave").click(function(){
		$(".searchtips").remove();
		var data = document.getElementsByName("searchEngine");
		var searchEngine = [];
		for(var i=0;i<data.length;i++){
			if(data[i].value==undefined || data[i].value==""){
				var html = '<div class="col-md-3 searchtips"><span class="help-block m-b-none tips" style="color: #f00">请输入搜索引擎！</span></div>';
				$("#addSearchContent").find("a:eq("+i+")").parent().parent().after(html);
				return false;
			}else if(data[i].value.length>50){
				$("#addSearchContent").find("a:eq("+i+")").parent().parent().after('<div class="col-md-3 searchtips"><span class="help-block m-b-none tips" id="sitetips" style="color: #f00">输入值长度超过50！</span></div>');
				return false;
			}
			searchEngine.push(data[i].value.trim());
		};
		var URL = "";
		if($("#seId").val()==undefined || $("#seId").val()==""){
			URL = "searchSave";
		}else{
			URL = "searchupdate";
		}
		$.ajax({
			url:URL,
			type:'POST',
			data:{"searchEngine":searchEngine,"seId":$("#seId").val()},
			success:function(data){
				if(data.result==1){
					 $("#importAlert").show();
					swal({
	                     title:"",
	                     text: "保存成功",
	                     html: false
	                 }, function(isConfirm) {
	                	 $("#addSearchContent").hide();
	                	 $('#searchEngine').bootstrapTable('refresh', {url: 'getSearchData'});
	                 });
				}else{
					swal("保存", data.message, "error");
				}
			}});
	});
	
	$("#cookieSave").click(function(){
		$(".cookietips").remove();
		var website = document.getElementsByName("cookieHostName");
		var websiteValie="";
		var key = document.getElementsByName("cookieKeyValue");
		var cookies = "";
		if(website.length>1){
			for(var i=0;i<website.length;i++){
				if( website[i].value=="" || website[i].value==undefined ){
					var html = '<div class="col-md-2 cookietips"><span class="help-block m-b-none tips" style="color: #f00">请输入网址！</span></div>';
					$("#addCookieContent").find("a:eq("+i+")").parent().parent().after(html);
					return false;
				}
				if( key[i].value=="" || key[i].value==undefined){
					var html = '<div class="col-md-2 cookietips"><span class="help-block m-b-none tips" style="color: #f00">请输入Key值！</span></div>';
					$("#addCookieContent").find("a:eq("+i+")").parent().parent().after(html);
					return false;
				}
				websiteValie+=website[i].value+",";
				cookies+=key[i].value+",";
			};
		}else if(($("#cookieFile").val() == "" ||  $("#cookieFile").val() == undefined) 
				&& ( website[0].value=="" || website[0].value==undefined)
				&& (key[0].value=="" || key[0].value==undefined)){
			swal("请录入数据或导入文件！");
			return false;
		}else{
			if($("#cookieFile").val() == "" ||  $("#cookieFile").val() == undefined){
				if( website[0].value=="" || website[0].value==undefined ){
					var html = '<div class="col-md-2 cookietips"><span class="help-block m-b-none tips" style="color: #f00">请输入网址！</span></div>';
					$("#addCookieContent").find("a:eq(0)").parent().parent().after(html);
					return false;
				}
				if( key[0].value=="" || key[0].value==undefined){
					var html = '<div class="col-md-2 cookietips"><span class="help-block m-b-none tips" style="color: #f00">请输入Key值！</span></div>';
					$("#addCookieContent").find("a:eq(0)").parent().parent().after(html);
					return false;
				}
			}
		}
		
		if(website.length==1){
			websiteValie = website[0].value;
			cookies = key[0].value;
		};
		var existFile = 1;
		if($("#cookieFile").val() == "" ||  $("#cookieFile").val() == undefined){
			existFile = 0;
		}
		var cookieId = $("#CookieId").val();
		var cookie = {cookieHostName:websiteValie,
				  	  cookieKeyValue:cookies,
				  	  cookieId:cookieId,
				  	  existFile:existFile
					};
		var URL = "";
		if(cookieId==undefined || cookieId==""){
			URL='cookieSave';
		}else{
			URL='cookieupdate';
		}
		$.ajaxFileUpload({
			url:URL,
			type:'POST',
			secureuri: false,
			data:cookie,
			fileElementId:'cookieFile',
            dataType: "JSON",
			success:function(data){
				var result = JSON.parse(data);
				if(result.result==1){
					 $("#importAlert").show();
					swal({
	                     title:"",
	                     text: "保存成功",
	                     html: false
	                 }, function(isConfirm) {
	                	 $("#addCookieContent").hide();
	                	 $('#cookieTable').bootstrapTable('refresh', {url: 'getCookieData'});
	                 });
				}else{
					swal("保存", result.message, "error");
				}
			}});
	});
	
	$("#thresSave").click(function(){
		var formData = $('#threshold').formToJSON();
		var webHitThreshold = $("#webHitThreshold").val();
		var kwThreholds = $("#kwThreholds").val();
		var rule = /^\+?[1-9][0-9]*$/;
		if(!rule.test(webHitThreshold) || webHitThreshold<0 || webHitThreshold>65535 ){
			swal("请输入正确的web流量点击阈值!");
			return false;
		}
		if(!rule.test(kwThreholds) || kwThreholds<0 || kwThreholds>65535){
			swal("请输入正确的关键词搜索阈值！");
			return false;
		}
		$.ajax({
			url:"saveAndSync",
			type:"POST",
			data:formData,
			success:function(data){
				if(data.result==1){
					$("#importAlert").show();
					swal({
	                     title:"",
	                     text: "保存成功",
	                     html: false
	                 }, function(isConfirm) {
	                	 ParamSetting.refreshData();
	                 });
				}else{
					swal("保存", data.message, "error");
				}
			}
		});
	});
},
//删除操作
deleteData:function(){

	$("#searchDelete").click(function(){
		var result = $("#searchEngine").bootstrapTable('getSelections');
		if(result.length <= 0){
			swal({  
				title:"删除",
	            text: "请至少选择一条记录删除！",  
	            type: "warning",  
	            confirmButtonText: '确认',  
	            confirmButtonColor: '#f27474',  
	        }); 
			return false;
		}
		
		swal({
			title: "确定要删除搜索引擎信息吗",
			text: "删除后将无法恢复，请谨慎操作！",
			type: "warning",
			showCancelButton: true,
			confirmButtonColor: "#DD6B55",
			confirmButtonText: "是的，我要删除！",
			cancelButtonText: "让我再考虑一下…",
			closeOnConfirm: false,
			closeOnCancel: false
		}, function(isConfirm) {
			if(isConfirm) {
				//表格中的删除操作
				var data = [];
				result.forEach(function(val,index){
					data.push(val.seId);
				});
				$.ajax({
					url:'deleteSearch',
					type:'POST',
					data:{"searchId":data},
					success:function(data){
						if(data.result==1){
							 swal({title:"删除成功！",  
		                            text:"已经永久删除了记录。",  
		                            type:"success"},
		                            function(){$('#searchEngine').bootstrapTable('refresh', {url: 'getSearchData'});});
							 $("#importAlert").show();
						}else if(data.result==0){
							swal("删除", data.message, "error");
							return false;
						}
					}
				});
			} else {
				swal("已取消", "取消了删除操作！", "error")
			}
		})
	});
	
	$("#cookieDelete").click(function(){
		var result = $("#cookieTable").bootstrapTable('getSelections');
		if(result.length <= 0){
			swal({  
				title:"删除",
	            text: "请至少选择一条记录删除！",  
	            type: "warning",  
	            confirmButtonText: '确认',  
	            confirmButtonColor: '#f27474',  
	        }); 
			return false;
		}
		
		swal({
			title: "确定要删除Cookie信息吗",
			text: "删除后将无法恢复，请谨慎操作！",
			type: "warning",
			showCancelButton: true,
			confirmButtonColor: "#DD6B55",
			confirmButtonText: "是的，我要删除！",
			cancelButtonText: "让我再考虑一下…",
			closeOnConfirm: false,
			closeOnCancel: false
		}, function(isConfirm) {
			if(isConfirm) {
				//表格中的删除操作
				var data = [];
				result.forEach(function(val,index){
					data.push(val.cookieId);
				});
				$.ajax({
					url:'deleteCookie',
					type:'POST',
					data:{"cookieId":data},
					success:function(data){
						if(data.result==1){
							 swal({title:"删除成功！",  
		                            text:"已经永久删除了记录。",  
		                            type:"success"},
		                            function(){ $('#cookieTable').bootstrapTable('refresh', {url: 'getCookieData'});});
							 $("#importAlert").show();
						}else if(data.result==0){
							swal("删除", data.message, "error");
							return false;
						}
					}
				});
			} else {
				swal("已取消", "取消了删除操作！", "error")
			}
		})
	});
},
alarmInit:function(){
	$.ajax({
	   	 	url: 'getStatus',
	        type: 'POST',
	        success:function(data){
	        	if(data==1){
	        		$("#importAlert").show();
	        	}
	        }
		});
},
sendPolicy:function(){
	$("#sendPolicy").click(function(){
		$.ajax({
			url:'sendPolicy?'+Math.random(),
			type:'POST',
			success:function(data){
				if(data.result==1){
					swal({
	                     title:"",
	                     text: "策略下发成功",
	                     html: false
	                 }, function(isConfirm) {
	                	$("#importAlert").hide();
	                 });
				}else{
					swal("策略下发", data.message, "error");
				}
			}
		});
	});
},
policyDetail:function(){
	$("#detailButton").click(function(){
		$("#paramDetailTable").bootstrapTable("refresh",{url:'getPolicyDetail'});
		$("#detailModal").modal('show');
	});
},
init:function(){
	ParamSetting.buttonInit();
	ParamSetting.dataBind();
	ParamSetting.saveData();
	ParamSetting.deleteData();
	ParamSetting.alarmInit();
	ParamSetting.sendPolicy();
	ParamSetting.policyDetail();
}
};

ParamSetting.init();

function getDetail(index){
	var common = $("#paramDetailTable").bootstrapTable('getData');
	PolicyDetail.showDetail(common[0].messageNo,0,1,1);
};

function policyResend(){
	var common = $("#paramDetailTable").bootstrapTable('getData');
	$.ajax({
		url:'policyResend?'+Math.random(),
		type:'POST',
		data:{'messageNo':common[0].messageNo},
		success:function(data){
			if(data.result==1){
				swal({
                    title:"",
                    text: "重发成功",
                    html: false
                }, function(isConfirm) {
                });
       	}else if(data.result==0){
       		swal("重发", data.message, "error");
				return false;
       	}
		}
	});
	};
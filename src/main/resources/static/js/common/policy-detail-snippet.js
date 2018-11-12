/***********************************************************************************
PolicyDetail *     方法  showDetail：页面数据展示控制
			 *          detailTableInit：页面数据初始化
			 *          initButton:button初始化
 **********************************************************************************
 function    *      tableInit:初始化表
             *               变量：tableId-表ID colums-列内容 types-1主策略 2绑定策略
             *      policyReSend:策略重发
             *               变量：index-第几行操作 type-那个tab操作(1-主策略 2-绑定策略)
 ********************************************************************************/  
   function tableInit(tableId,colums,types){
    	//详情列表初始化
    	var listInit = {
    			url: "/policyStatus/getDetail?"+Math.random(),
    			method: 'post',
    			contentType : "application/json; charset=UTF-8",
    			dataType: "json",
    			pagination: true,
    			showColumns: false,
    			iconSize: "outline",
    			sidePagination: "server",
    			dataField: "list",
                pageNumber: 1,
                queryParams : function(params) {
        			if(types==1){
        				return JSON.stringify({
        		            //每页多少条数据
        		            pageSize: params.limit,
        		            //请求第几页
        		            pageIndex:params.offset/params.limit+1,
        		            messageNo:$("#policyDetialMessageNo").val(),
        		            messageType:$("#policyDetialMessageType").val(),
        		            searchType:1,
        		            status:$("#statusTypeMain").val()
        		        });
        			}else if(types==2){
        				return JSON.stringify({
        		            //每页多少条数据
        		            pageSize: params.limit,
        		            //请求第几页
        		            pageIndex:params.offset/params.limit+1,
        		            messageNo:$("#policyDetialMessageNo").val(),
        		            messageType:$("#policyDetialMessageType").val(),
        		            searchType:2,
        		            status:$("#statusTypeBind").val(),
        		            userType:$("#policyDetialUserType").val()
        				});
        			}
        		},
                pageSize: 10,
                pageList: [10, 25, 50, 100, 200],
    			icons: {
    				refresh: "glyphicon-repeat",
    				toggle: "glyphicon-list-alt",
    				columns: "glyphicon-list",
    			},
    			striped:true,  //是否行间隔色显示
    			sortable: true,//是否启用排序
                sortOrder: "asc",//排序方式
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
    			clickToSelect:false, //是否启用点击选中行
    			columns:colums
    		}
    	$("#"+tableId).bootstrapTable('destroy').bootstrapTable(listInit);
    }

PolicyDetail = {
	/**
	 * messageNo:策略号
	 * messageType:策略类型
	 * type:展示tab 1-只有主策略展示 2-主策略和绑定策略展示
	 * reSendFlag: 0-不可以重发    1-可以重发 (默认可以重发)
	 * showTab:0-显示主策略tab 1-用户显示绑定策略tab
	 * modelType:上报策略具体模块
	 */
	showDetail:function(messageNo,messageType,type,reSendFlag,showTab,modelType){
		if(modelType!=null && modelType!=undefined){
			$('#modelType').val(modelType);
		}
		if(reSendFlag==undefined || reSendFlag==null){
			$("#policyDetialreSendFlag").val(1);
		}else{
			$("#policyDetialreSendFlag").val(reSendFlag);
		}
		if(reSendFlag==0){
			$("#resendMain").hide();
			$("#resendBind").hide();
		}else{
			$("#resendMain").show();
			$("#resendBind").show();
		}
		$("#policyDetialMessageNo").val(messageNo);
		$("#policyDetialMessageType").val(messageType);
		if(type==1){
			$("#mainPolicyTable").bootstrapTable("refresh",{url:"/policyStatus/getDetail?"+Math.random()});
			$("#bindTab").hide();
			$("#flowTab").hide();
		}else if(type==2){
			$("#mainPolicyTable").bootstrapTable("refresh",{url:"/policyStatus/getDetail?"+Math.random()});
			$("#bindPolicyTable").bootstrapTable("refresh",{url:"/policyStatus/getDetail?"+Math.random()});
			$("#bindTab").show();
			$("#flowTab").hide();
		}
		if(showTab==0){
			$('#mainTab').parent().addClass('active');
			$('#bindTab').parent().removeClass('active');
			$('#flowTab').parent().removeClass('active');
			$('#mainPolicy').addClass('active');
			$('#bindPolicy').removeClass('active');
		}else if(showTab==1){
			$('#mainTab').parent().removeClass('active');
			$('#bindTab').parent().addClass('active');
			$('#flowTab').parent().removeClass('active');
			$('#mainPolicy').removeClass('active');
			$('#bindPolicy').addClass('active');
		}
		$("#policyDetail").modal("show");
	},
    detailTableInit:function(){
    	var mainColums = [ {checkbox: true},
    		{field:'deviceName',title:'设备名称', formatter:function(value,row,index){ if(value==null){return "";}else{return "<span title='"+value+"'>"+value+"</span>";}}},
			{field:'dpiIp',title:'设备IP',formatter:function(value,row,index){if(value==null){return "";}else{return "<span title='"+value+"'>"+value+"</span>";}}},
	        {field:'areaName',title:'地区',formatter:function(value,row,index){if(value==null){return "";}else{return "<span title='"+value+"'>"+value+"</span>";}}},
	        {field:'status',title:'<span title="策略下发状态">策略下发状态</span>',formatter:function(value,row,index){if(value==1){return "成功";}else if(value==0){return '<font color="red">失败</font>';}else if(value==2){return '<font color="red">连接失败</font>';}}},
	        {field:'createTime',title:'<span title="最后处理时间">最后处理时间</span>',formatter:function(value,row,index){
				return "<span title='"+value+"'>"+value+"</span>";
			}},
	        {title:'操作',field:'deviceType',formatter:function(value,row,index){
	        	if($("#policyDetialreSendFlag").val()==0){
	    			return "";
	    		}else{
	    			return "<a href='#' onclick='policyReSend("+index+",1)' title='重发'><i class='fa fal fa-share resend'></i></a>";
	    		}
	        	}}];
    	tableInit("mainPolicyTable",mainColums,1);
    	var bindColums = [ {checkbox: true},
    		{field:'messageNo',title:'绑定策略ID'},
    		{field:'userType',title:'<span title="绑定用户类型">绑定用户类型</span>',formatter:function(value,row,index){
    			var typeUser = "";
    			if(value==0){
    				typeUser = "全用户";
    			}else if(value==1){
    				typeUser = "账号";
    			}else if(value==2){
    				typeUser = "IP地址";
    			}else if(value==3){
    				typeUser = "用户组";
    			} 
    			return "<span title='"+typeUser+"'>"+typeUser+"</span>";	
    		}},
    		{field:'userGroupName',title:'<span title="绑定用户名称">绑定用户名称</span>',formatter:function(value,row,index){if(value==null){return "";}else{return "<span title='"+value+"'>"+value+"</span>";}}},
    		{field:'deviceName',title:'设备名称', formatter:function(value,row,index){ if(value==null){return "";}else{return "<span title='"+value+"'>"+value+"</span>";}}},
			{field:'dpiIp',title:'设备IP',formatter:function(value,row,index){if(value==null){return "";}else{return "<span title='"+value+"'>"+value+"</span>";}}},
	        {field:'areaName',title:'地区',formatter:function(value,row,index){if(value==null){return "";}else{return "<span title='"+value+"'>"+value+"</span>";}}},
	        {field:'status',title:'<span title="策略下发状态">策略下发状态</span>',formatter:function(value,row,index){if(value==1){return "成功";}else if(value==0){return '<font color="red">失败</font>';}else if(value==2){return '<font color="red">连接失败</font>';}}},
	        {field:'createTime',title:'<span title="最后处理时间">最后处理时间</span>',formatter:function(value,row,index){
				return "<span title='"+value+"'>"+value+"</span>";
			}},
	        {title:'操作',field:'deviceType',formatter:function(value,row,index){
	        	if($("#policyDetialreSendFlag").val()==0){
	    			return "";
	    		}else{
	    			return "<a href='#' onclick='policyReSend("+index+",2)' title='重发'><i class='fa fal fa-share resend'></i></a>";
	    		}
	        	}}];
    	tableInit("bindPolicyTable",bindColums,2);
    	
    },
    initButton:function(){
    	$("#searchMain").click(function(){
    		$("#mainPolicyTable").bootstrapTable("refresh",{url:"/policyStatus/getDetail?"+Math.random()});
    	});
    	$("#searchBind").click(function(){
    		$("#bindPolicyTable").bootstrapTable("refresh",{url:"/policyStatus/getDetail?"+Math.random()});
    	});
    	$("#searchFlow").click(function(){
    		$("#flowPolicyTable").bootstrapTable("refresh",{url:"/policyStatus/getDetail?"+Math.random()});
    	});
    },
    init:function(){
    	PolicyDetail.detailTableInit();
    	PolicyDetail.initButton();
    }
}
PolicyDetail.init();

function policyReSend(index,type){
	if($("#policyDetialreSendFlag").val() == 0){
		 swal({title:"此策略不能重发", text: "",html: false});
		 return false;
	}
	var url = $("#policyDetialUrl").val();
	var model = $('#modelType').val();
	if(index==null){
		var data = {};
		if(type==1){
			var result = $("#mainPolicyTable").bootstrapTable('getSelections');
			if(result.length <= 0){
				swal({title:"请至少选择一条记录重发", text: "",html: false});
				return false;
			}
			var ips = [];
			var messageNos = [];
			messageNos.push(result[0].messageNo);
			result.forEach(function(val,index){
				ips.push(val.dpiIp);
			});
			data = {
					"messageNos":messageNos,
					"messageType":result[0].messageType,
					"ips":ips,
					"model":model
			};
		}else if(type==2){
			var result = $("#bindPolicyTable").bootstrapTable('getSelections');
			if(result.length <= 0){
				swal({title:"请至少选择一条记录重发", text: "",html: false});
				return false;
			}
			var ips = [];
			var messageNos = [];
			result.forEach(function(val,index){
				ips.push(val.dpiIp);
				messageNos.push(val.messageNo)
			});
			data = {
					"messageNos":messageNos,
					"messageType":result[0].messageType,
					"ips":ips,
					"model":model
			};
		}
		$.ajax({
			url:'/policyStatus/commonPolicyResend.do?'+Math.random(),
			type:'POST',
			data:data,
	        success:function(data){
	        	if(data.result==1){
	        		swal({title:"重发成功", text: "",html: false});
	        	}else if(data.result==0){
	        		swal("重发", data.message, "error");
					return false;
	        	}
	        }
		});
	}else{
		var data = {};
		if(type==1){
			var result = $("#mainPolicyTable").bootstrapTable('getData');
			var ips = [];
			var messageNos = [];
			messageNos.push(result[index].messageNo)
			ips.push(result[index].dpiIp);
			data = {
					"messageNos":messageNos,
					"messageType":result[index].messageType,
					"ips":ips,
					"model":model
			};
		}else if(type==2){
			var result = $("#bindPolicyTable").bootstrapTable('getData');
			var ips = [];
			var messageNos = [];
			messageNos.push(result[index].messageNo)
			ips.push(result[index].dpiIp);
			data = {
					"messageNos":messageNos,
					"messageType":result[index].messageType,
					"ips":ips,
					"model":model
			};
		}
		$.ajax({
			url: '/policyStatus/commonPolicyResend.do?'+Math.random(),
			type:'POST',
			data:data,
	        success:function(data){
	        	if(data.result==1){
	        		 swal({title:"重发成功",text: "", html: false});
	        	}else if(data.result==0){
	        		swal({title:data.message,text: "", html: false});
					return false;
	        	}
	        }
		});
	}
}

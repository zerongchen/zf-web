/*
 * Share:1拖N管理
 *        方法： tableInit     表格初始化，已经相关按钮绑定事件
 *             editInit      编辑页面初始化
 *             refreshData   首页列表表格数据刷新
 *             save          新增和编辑保存
 *             loadUserGroup 用户组下拉框内容加载
 *             addInit       新增页面初始化
 *             deleteData    批量删除
 *             deleteInit    数据删除
 *             init          方法初始化
 *
 * resend     重发
 * getDetail  策略下发详情
 * deleteRow  单行删除事件绑定
 * edit       单行编辑事件绑定
 * 
 * 
 */

var Share = {
//列表加载
tableInit:function(){
    //查询条件时间控件初始化
    initSearchDate(3,false);
    function controlType(value){
		if(value==1){return "推送警示信息";}else if(value==32){return "重置所有Http链接";}
		else if(value==33){ return "按1/2比例重置Http链接";}else if(value==34){return "按1/3比例重置Http链接";
		}else if(value==35){return "按1/4比例重置Http链接";}else if(value==36){return "按1/5比例重置Http链接"
		}else if(value==37){return "按1/6比例重置Http链接";}else if(value==38){return "按1/7比例重置Http链接";
		}else if(value==39){return "按1/8比例重置Http链接";}else if(value==40){return "按1/9比例重置Http链接";
		}else if(value==41){return "按1/10比例重置Http链接";}else if(value==42){return "按1/11比例重置Http链接";
		}else if(value==43){return "按1/12比例重置Http链接";}else if(value==44){return "按1/13比例重置Http链接";
		}else if(value==45){return "按1/14比例重置Http链接";}else if(value==46){return "按1/15比例重置Http链接";
		}else if(value==47){return "按1/16比例重置Http链接";}else if(value==48){return "重置TCP链接";
		}else if(value==49){return "按1/2比例重置TCP链接";}else if(value==50){return "按1/3比例重置TCP链接";
		}else if(value==51){return "按1/4比例重置TCP链接";}else if(value==52){return "按1/5比例重置TCP链接";
		}else if(value==53){return "按1/6比例重置TCP链接";}else if(value==54){return "按1/7比例重置TCP链接";
		}else if(value==55){return "按1/8比例重置TCP链接";}else if(value==56){return "按1/9比例重置TCP链接";
		}else if(value==57){return "按1/10比例重置TCP链接";}else if(value==58){return "按1/11比例重置TCP链接";
		}else if(value==59){return "按1/12比例重置TCP链接";}else if(value==60){return "按1/13比例重置TCP链接";
		}else if(value==61){return "按1/14比例重置TCP链接";}else if(value==62){return "按1/15比例重置TCP链接";
		}else if(value==63){return "按1/16比例重置TCP链接";}else if(value==64){return "对某类应用进行丢弃";}
    }
    
    
	var share = {
			method: 'post',
			contentType : "application/x-www-form-urlencoded",
			dataType: "json",
			pagination: true,
			showColumns: !0,
			iconSize: "outline",
			sidePagination: "server",
			dataField: "list",
			url:"getList",
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
			toolbar:"#commonButton",
			sortable: true,//是否启用排序
            sortOrder: "asc",//排序方式
			clickToSelect:false, //是否启用点击选中行
			columns: [
				{ // 列设置
                    checkbox: true // 使用单选框
                },{
                	field:'messageNo',
                	title:'策略ID',
                	formatter:function(value,row,index){
                		if(value==null){
                			return "";
                		}else{
                			return "<span title='"+value+"'>"+value+"</span>";
                		}
                	}
                },{
                	field:'messageName',
                	title:'<span title="策略名称">策略名称</span>',formatter:function(value,row,index){
            			return "<span title='"+value+"'>"+value+"</span>";
            		}
                },{
                	field:'ctype',
                	title:'<span title="控制类型">控制类型</span>',
                	formatter:function(value,row,index){
                		return "<span title='"+controlType(value)+"'>"+controlType(value)+"</span>";
                	}
                },{
                	field:'appTypeName',
                	title:'<span title="应用类型">应用类型</span>',formatter:function(value,row,index){
            			return "<span title='"+value+"'>"+value+"</span>";
            		}
                },{
                	field:'appPolicy',
                	title:'<span title="策略成功数/异常数">策略成功数/异常数</span>',
                	formatter:function(value,row,index){	
                		if(value=="0/0"){
                			return "0/0";
                		}else{
                			return "<a href='#' onclick='getDetail("+index+",0)' title='详情'>"+value+"</a>";
                		}
                	}
                },{
                	field:'bindPolicy',
                	title:'<span title="绑定成功数/异常数">绑定成功数/异常数</span>',
                	formatter:function(value,row,index){	
                		if(value=="0/0"){
                			return "0/0";
                		}else{
                			return "<a href='#' onclick='getDetail("+index+",1)' title='详情'>"+value+"</a>";
                		}
                	}
                },{
                	field:'startTimeShow',
                	title:'<span title="策略开始时间">策略开始时间</span>',formatter:function(value,row,index){
            			return "<span title='"+value+"'>"+value+"</span>";
            		}
                },{
                	field:'endTimeShow',
                	title:'<span title="策略结束时间">策略结束时间</span>',formatter:function(value,row,index){
            			return "<span title='"+value+"'>"+value+"</span>";
            		}
                },{
                	field:'createTime',
                	title:'<span title="创建时间">创建时间</span>',formatter:function(value,row,index){
            			return "<span title='"+value+"'>"+value+"</span>";
            		}
                },{
                	visible:false,
                	field:'modifyOper',
                	title:'<span title="操作账号">操作账号</span>',formatter:function(value,row,index){
            			return "<span title='"+value+"'>"+value+"</span>";
            		}
                },{
                	field:'type',
                	title:'操作',
                	formatter:function(value,row,index){
                		var redo = $("#redoFlag").val();
                		var deleteFlag = $("#deleteFlag").val();
                		var modify = $("#modifyFlag").val();
                		var format="";
                		if(redo==1){
                			if(value==1){
                		        format +="<a href='#' title='重发' onclick='resend("+index+")' class='m-r'><i class='fa fal fa-share resend'></i></a>";
                		    }
                		}
                		if(modify==1){
                			 format+="<a href='#' title='编辑' data-toggle='modal' data-target='#myModaladd' onclick='edit("+index+")' class='m-r'><i class='fa fa-edit fa-lg'></i></a>";
                		}
                		if(deleteFlag==1){
                			format+="<a href='#' onclick='deleteRow("+index+")' title='删除'><i class='fa fa-close fa-lg'></i></a>";
                		}
                	    return format;
                	}
                }]
	};
	$("#table").bootstrapTable(share);
    //请求服务数据时所传参数
    function queryParams(params){
        return{
            //每页多少条数据
            pageSize: params.limit,
            //请求第几页
            pageIndex:params.offset/params.limit+1,
            policyName:$("#policyName").val(),
            startTime:$("#searchStart").val(),
            endTime:$("#searchEnd").val()
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
    $("#searchFormButton").click(function(){
    	Share.refreshData();
    });
    $("#addPlush").click(function() {
        var inputPlus = "<div class='form-group newInput' ><label class='col-md-1 control-label p-n'></label>"
            +"<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
            +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
            +"</div>";
        $(this).parent().parent().parent().after(inputPlus);
        $(".check-minus").click(function() {
            $(this).parent().parent().parent().remove()
        });
    });
    $('#userType').change(function () {
        var userType = this.value;
        if(userType == 0){
            $('#userNameDiv').hide();
            $('#puserGroup').hide();
            $('#userNameDiv .newInput').remove();
        }else if(userType == 1 || userType == 2){
            $('#userNameDiv').show();
            $('#puserGroup').hide();
            $('#userNameDiv .newInput').remove();
        }else if(userType == 3){
            $('#userNameDiv').hide();
            $('#puserGroup').show();
            $('#userNameDiv .newInput').remove();
            loadSelectPicker('puserGroupid','/select/getUserGroup',false);
        }
    });
    $('#newAddGroup').click(function () {
        document.getElementById("createUserGroupForm").reset();
        $('#add-usergroup-snippet').modal('show');
    });
},
editInit:function(share){
	$("#title").text("修改");
	$(".check-minus").parent().parent().parent().remove();
	$(".has-error").remove();
	$(".timeul>li>input[type='button']").attr('class', '');
	$("#start").val(share.startTimeShow).attr("disabled",true);
	$("#end").val(share.endTimeShow).attr("disabled",true);
	$("#controlType").val(share.ctype);
	$("#shareId").val(share.shareId);
	$("#advUrl").val(share.advUrl);
	$("#addPolicyName").val(share.messageName);
	$("#messageNo").val(share.messageNo);
	loadSel('appType','/select/getAppType',true,undefined,false,share.dropProtocol);
	$("#packetDropRatio").val(share.packetDropRatio);
	//管理时间赋值
	var timeBar = share.time.toString(2).split("");
	setTimeBar(timeBar);
	Share.loadUserGroup();
	$("#userType").val(share.userGroups[0].userType);
	if(share.userGroups[0].userType==0){
		$("#userNameDiv").hide();
		$("#puserGroup").hide();
	}else if(share.userGroups[0].userType==1 || share.userGroups[0].userType==2){
		$("#userNameDiv").show();
		$("#puserGroup").hide();
		for(var i=0;i<share.userGroups.length;i++){
			if(i==0){
				$("#userName").val(share.userGroups[i].userName);
			}else{
				var htmlUsers = "<div class='form-group newInput' ><label class='col-md-1 control-label p-n'></label>"
                +"<div class='col-md-3'><input type='text' name='userName' value='"+share.userGroups[i].userName+"' class='form-control'></div>"
                +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
                +"</div>";
				$("#addPlush").parent().parent().parent().after(htmlUsers);
			}
			
		}
	}else if(share.userGroups[0].userType==3){
		var userGroups = [];
		share.userGroups.forEach(function(val,index){
			userGroups.push(val.userGroupId);
		});
		$("#puserGroupid").selectpicker('refresh');
		$("#puserGroupid").selectpicker('val',userGroups);
		$("#userNameDiv").hide();
		$("#puserGroup").show();
	}
	
},
//列表数据刷新
refreshData:function(){
	$("#table").bootstrapTable("refresh",{url:'getList'});
},
//保存
save:function(){
	var policyName = $("#addPolicyName").val().trim();
	var controlType = $("#controlType").val()
	var appType = $("#appType").val();
	if(policyName==""){
		 warn('nameMessage','请输入策略名称');
		return false;
	}else if(policyName.length>50){
		warn('nameMessage','策略名称不能超过50个字符');
		return false;
	}
	
	if(controlType==""){
		warn('typeMessage','请选择控制类型');
		return false;
	}
	if(appType==""){
		warn('appTypeMessage','请选择应用类型');
		return false;
	}
	var advUrl = $("#advUrl").val();
	if(advUrl==""){
		warn('advUrlMessage','请输入警示信息');
		return false;
	}
	var packetDropRatio = $("#packetDropRatio").val();
	if(packetDropRatio==""){
		warn('ratioMessage','请输入丢包比例');
		return false;
	}
	if(!isNmber0(packetDropRatio) || packetDropRatio<0 || packetDropRatio>100){
		warn('ratioMessage','请输入正确丢包比例');
		return false;
	}
	var start = $("#start").val();
	var end = $("#end").val();
	var timeBar = getTimeBarValue("webTimebar");
	if(timeBar.size==0){
		var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请设置管理时间</span></div>';
		$(".timeul").parent().after(html);
		return false;
	}
	var shareId = $("#shareId").val();
	//用户组绑定校验
	var userGroups = [];
	var userType = $("#userType").val();
	if(userType == 0){
		var userGroup = {
				shareId:shareId,
				userType:userType
		};
		userGroups.push(userGroup);
	}else if(userType == 1 || userType == 2){
		var userName = document.getElementsByName("userName");
		for(var i=0;i<userName.length;i++){
			if( userName[i].value=="" && userType == 1){
				var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入账号</span></div>';
				$("#userNameDiv").find("a:eq("+i+")").parent().parent().after(html);
				return false;
			}else if( userName[i].value=="" && userType == 2){
				var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入IP地址</span></div>';
				$("#userNameDiv").find("a:eq("+i+")").parent().parent().after(html);
				return false;
			}else if( !checkIP(userName[i].value) && userType == 2){
				var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入正确IP地址</span></div>';
				$("#userNameDiv").find("a:eq("+i+")").parent().parent().after(html);
				return false;
			}
			var userGroup = {
					shareId:shareId,
					userType:userType,
					userName:userName[i].value
			};
			userGroups.push(userGroup);
		}
	}else if(userType == 3){
		var puserGroupids = $("#puserGroupid").val();
		if(puserGroupids==null){
			warn('puserGroup','请选择用户组');
			return false;
		}
		for(var i=0;i<puserGroupids.length;i++){
			var userGroup = {
					shareId:shareId,
					userType:userType,
					userGroupId:puserGroupids[i]
			};
			userGroups.push(userGroup);
		}
	}
	var messageNo = $("#messageNo").val();
	//管理时间转换
	var ctime = parseInt(timeBar.value,2);
	var share = {
			shareId:shareId,
			messageNo:messageNo,
			messageName:policyName,
			ctype:controlType,
			advUrl:advUrl,
			startTimeShow:start,
			endTimeShow:end,
			time:ctime,
			dropProtocol:appType,
			packetDropRatio:packetDropRatio,
			userGroups:userGroups
	};
	if(shareId=="" || shareId==undefined){
		$.ajax({
			url:"save",
			type:"POST",
			data:JSON.stringify(share),
			headers: {
	            'Accept': 'application/json',
	            'Content-Type': 'application/json; charset=UTF-8'
	        },
			dataType:"json",
			success:function(data){
				if(data.result==1){
					swal({
	                    title:"",
	                    text: "保存成功",
	                    html: false
	                }, function(isConfirm) {
	                	$("#myModaladd").modal('hide');
	    				Share.refreshData();
	                });
				}else if(data.result==0){
					swal({
						title: "保存失败",
						text: data.message,
						type: "error"
					});
				}
			}
			
		});
	}else{
		$.ajax({
			url:"update",
			type:"POST",
			data:JSON.stringify(share),
			headers: {
	            'Accept': 'application/json',
	            'Content-Type': 'application/json; charset=UTF-8'
	        },
			dataType:"json",
			success:function(data){
				if(data.result==1){
					swal({
	                    title:"",
	                    text: "保存成功",
	                    html: false
	                }, function(isConfirm) {
	                	$("#myModaladd").modal('hide');
	    				Share.refreshData();
	                });
				}else if(data.result==0){
					swal({
						title: "保存失败",
						text: data.message,
						type: "error"
					});
				}
			}
		});
	}

},
//加载用户列表
loadUserGroup:function(){
	loadSelectPicker('puserGroupid','/select/getUserGroup',true,undefined,false,undefined);
},
//新增页面初始化
addInit:function(){
	initDate(3,false);
	$("#add").click(function(){
		loadSel('appType','/select/getAppType',true,undefined,false,undefined);
		$("#title").text("新增");
		$(".has-error").remove();
		$(".timeul>li>input[type='button']").attr('class', '');
		$("#start").val("").removeAttr("disabled");
		$("#end").val("").removeAttr("disabled");
		$("#advUrl").val("");
		$("#puserGroupid").val("");
		$(".checkall").removeClass('fa-check-square-o');
		$(".checkall").addClass('fa-square-o');
		$("#packetDropRatio").val("");
		$("#controlType").val("");
		$("#addPolicyName").val("");
		$("#shareId").val("");
		$("#messageNo").val("");
		$("#userType").val(0);
		$("#bindmessageNo").val("");
		$("#userName").val("");
		Share.loadUserGroup();
		$("#userNameDiv").hide();
		$("#puserGroup").hide();
	});
	
	$("#addSubmitBut").click(function(){
		$(".has-error").remove();
		Share.save();
	});
},
deleteData:function(){
	$("#delete").click(function(){
		var result = $("#table").bootstrapTable('getSelections');
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
		var data = [];
		result.forEach(function(val,index){
			data.push(val.shareId);
		});
		Share.deleteInit(data);
	});
},
deleteInit:function(policyIds){
	swal({
		title: "确定要删除策略信息吗",
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
			$.ajax({
				url:'delete',
				type:'POST',
				data:{"policyId":policyIds},
				success:function(data){
					if(data.result==1){
						 swal({title:"删除成功！",  
	                            text:"已经永久删除了记录。",  
	                            type:"success"},
	                            Share.refreshData());
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
},
init:function(){
	Share.tableInit();
	Share.addInit();
	Share.deleteData();
	timeBarInit();
}
}

Share.init();

function edit(index){
	var share = $("#table").bootstrapTable('getData');
	Share.editInit(share[index]);
}

function deleteRow(index){
	var share = $("#table").bootstrapTable('getData');
	var data = [];
	data.push(share[index].shareId);
	Share.deleteInit(data);
}

function getDetail(index,type){
	var share = $("#table").bootstrapTable('getData');
	var redo = $("#redoFlag").val();
    if(redo==1 && share[index].type==1){
    	PolicyDetail.showDetail(share[index].messageNo,66,2,1,type);
    }else{
    	PolicyDetail.showDetail(share[index].messageNo,66,2,0,type);
    }
}

function resend(index){
	var share = $("#table").bootstrapTable('getData');
	var data = [];
	data.push(share[index].shareId);
	$.ajax({
		url:'policyResend?'+Math.random(),
		type:'POST',
		data:{'shareId':data},
		success:function(data){
			if(data.result==1){
				swal({
                    title:"",
                    text: "重发成功",
                    html: false
                }, function(isConfirm) {
    				Share.refreshData();
                });
       	}else if(data.result==0){
       		swal("重发", data.message, "error");
				return false;
       	}
		}
	});
	
}

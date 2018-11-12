/**
 * object：deviceManage  设备管理页面
 *     方法：initTable  页面的展示表格初始化
 *     		refreshDPIupl  综分设备页面表格刷新
 *     		refreshDPIRec  DPI信息接收设备页面表格刷新
 *     		refreshZongFen  DPI信息上报设备页面表格刷新
 *     		cleanData  新增编辑页面数据清除
 *     		dpiUplSave  DPI信息接收设备新增编辑页面保存
 *     		dpiRecSave  DPI信息上报设备新增编辑页面保存
 *     		zongFenSave  综分设备新增编辑页面保存
 *     		buttonInit  按钮初始化绑定事件
 *     		graphInit  网络拓扑图初始化展示
 *     		deleteInit  删除
 *     		editInit  编辑
 * *****************************************
 * 
 * function：
 *          initTable  后台页面分页table初始化
 * 			deleteRow  单行删除事件绑定
 * 			edit       单行编辑事件绑定
 * 
 * 
 */

//定义一个全局的画布
var canvas = document.getElementById('canvas');
//定义一个全局的stage,为了可以刷新画布
var stage = new JTopo.Stage(canvas);

function initTable(tableId,columns,url){
	$("#"+tableId).bootstrapTable({
		method: 'post',
		contentType : "application/x-www-form-urlencoded",
		dataType: "json",
		pagination: true,
		showColumns: false,
		iconSize: "outline",
		sidePagination: "server",
		dataField: "list",
		url:url,
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100, 200],
        queryParamsType:'limit',
		queryParams:function(params){
			if(tableId=="dpiRecTable"){
				return{
		            //每页多少条数据
		            pageSize: params.limit,
		            //请求第几页
		            pageIndex:params.offset/params.limit+1,
		            deviceName:$("#dpiRecName").val()
		        };
			}else if(tableId=="dpiUplTable"){
				return{
		            //每页多少条数据
		            pageSize: params.limit,
		            //请求第几页
		            pageIndex:params.offset/params.limit+1,
		            deviceType:$("#deviceTypeSea").val(),
		            startIp:$("#startIpSea").val(),
		            endIp:$("#endIpSea").val()
				};
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
		clickToSelect:false, //是否启用点击选中行
		columns: columns
});
};

deviceManage= {
initTable:function(){
	//综分设备
	var columns = [
		{field:'zongfenName',title:'设备名称',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		},width:100},
		{field:'deviceUser',title:'设备用途',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field:'zongfenIp',title:'虚拟IP地址',
        	formatter:function(value,row,index){
        		if(value==null) {
        			return "";
        		}else{
        			return "<span title='"+value+"'>"+value+"</span>";
        		};
        	},width:150},
        {field:'realIp',title:'物理IP地址',
        	formatter:function(value, row, index){
        		var str = "";
        		if(value!=null){
        			value.forEach(function(val,index){
            			if(str==""){
            				str+=val;
            			}else{
            				str+=";"+val;
            			}
            		});
        		}
        		return "<span title='"+str+"'>"+str+"</span>";;
        	},width:150
        },
        {field:'zongfenPort',title:'端口',
        	formatter:function(value,row,index){
        		if(value==null) {
        			return "";
        		}else{
        			return "<span title='"+value+"'>"+value+"</span>";
        		};
        	},width:70
        },{
        	title:'操作',
        	field:'deviceType',
        	formatter:function(value,row,index){
        		var deleteFlag = $("#deleteFlag").val();
        		var modify = $("#modifyFlag").val();
        		var format="";
        		if(modify==1 && value!=1){
        				format+="<a href='#' title='编辑' data-toggle='modal' data-target='#myModaladd' onclick='edit("+index+",1)' class='m-r'><i class='fa fa-edit fa-lg'></i></a>";
        		}
        		if(deleteFlag==1){
        			format+="<a href='#' onclick='deleteRow("+index+",1)' title='删除'><i class='fa fa-close fa-lg'></i></a>";
        		}
        	    return format;
        	},width:80
        }];
	//前端分页
	var listInit = {
			url: "getZongFenData?"+Math.random(),
			method: 'post',
			contentType : "application/x-www-form-urlencoded",
			dataType: "json",
			pagination: true,
			showColumns: false,
			iconSize: "outline",
			sidePagination: "client",
            pageNumber: 1,
            queryParams : function(params) {
                return {
                	deviceName: $("#zonfenName").val()
                    };
            },
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
			icons: {
				refresh: "glyphicon-repeat",
				toggle: "glyphicon-list-alt",
				columns: "glyphicon-list",
			},
			striped:true,  //是否行间隔色显示
			sortable: false,//是否启用排序
			clickToSelect:false, //是否启用点击选中行
			columns:columns
		}
	$("#zongFenTable").bootstrapTable('destroy').bootstrapTable(listInit);
	
	//DPI接收数据设备
	var columnDPIRec = [
		{field:'dpiDevName',title:'设备名称',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		},width:120},
		{field:'probeType',title:'设备类型',
        	formatter: function (value, row, index) {  
             	if(value==0){
             		return 'DPI';
             	}else if(value==1){
             		return 'EU';
             	}
             },width:80
        },
        {field:'dpiSiteName',title:'站点',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		},width:120},
        {field:'areaName',title:'所属地区',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		},width:80},
        {field:'ipPort',title:'IP:端口',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		},width:190},
        {field:'zongFenServer',title:'关联综分设备',
        	formatter:function(value,row,index){
        		if(value!=null){
        			return "<span title='策略下发服务器（"+value+"）'>策略下发服务器（"+value+"）</span>";
        		}else{
        			return "";
        		}
        	}
        },{field:'connectFlag',title:'连接状态',formatter:function(value,row,index){
        	var status = "";
        	if(value=='0'){
        		status = "正常";
        	}else{
        		status = "连接异常";
        	}
        	return "<span title='"+status+"'>"+status+"</span>";
        },width:100},{
        	title:'操作',
        	field:'zongfenType',
        	formatter:function(value,row,index){
        		var deleteFlag = $("#deleteFlag").val();
        		var modify = $("#modifyFlag").val();
        		var format="";
        		if(modify==1){
    				format+="<a href='#' title='编辑' data-toggle='modal' data-target='#myModaladd' onclick='edit("+index+",2)' class='m-r'><i class='fa fa-edit fa-lg'></i></a>";
        		}
        		if(deleteFlag==1){
        			format+="<a href='#' onclick='deleteRow("+index+",2)' title='删除'><i class='fa fa-close fa-lg'></i></a>";
        		}
        	    return format;
        	},width:80
        }];
	initTable("dpiRecTable",columnDPIRec,"getDPIRecData");
	
	//DPI上报数据设备
	var columnDPIUpl = [
		{field:'probeType',title:'设备类型',
        	formatter: function (value, row, index) {  
             	if(value==0){
             		return 'DPI';
             	}else if(value==1){
             		return 'EU';
             	}
             }
        },
        {field:'areaName',title:'地区/机房',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field:'startIp',title:'开始IP',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field:'endIp',title:'结束IP',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field:'factoryName',title:'来源厂家',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}}
        ,{
        	title:'操作',
        	field:'zongfenType',
        	formatter:function(value,row,index){
        		var deleteFlag = $("#deleteFlag").val();
        		var modify = $("#modifyFlag").val();
        		var format="";
        		if(modify==1){
    				format+="<a href='#' title='编辑' data-toggle='modal' data-target='#myModaladd' onclick='edit("+index+",3)' class='m-r'><i class='fa fa-edit fa-lg'></i></a>";
        		}
        		if(deleteFlag==1){
        			format+="<a href='#' onclick='deleteRow("+index+",3)' title='删除'><i class='fa fa-close fa-lg'></i></a>";
        		}
        	    return format;
        	}
        }];
	initTable("dpiUplTable",columnDPIUpl,"getDPIUplData");
},
//刷新综分设备管理列表
refreshZongFen:function(){
	deviceManage.graphInit();
	$("#zongFenTable").bootstrapTable('refresh', { url: "getZongFenData",query:{'deviceName':$("#zonfenName").val() }});
},
//刷新DPI接收设备管理列表
refreshDPIRec:function(){
	deviceManage.graphInit();
	$("#dpiRecTable").bootstrapTable("refresh",{url:'getDPIRecData'});
},
//刷新DPI上报数据设备管理列表
refreshDPIupl:function(){
	$("#dpiUplTable").bootstrapTable("refresh",{url:'getDPIUplData'});
},
//清除编辑页面数据
cleanData:function(){
	$("#addDeviceName").val("");
	$("#deviceType").val("");
	$("#virtualIp").val("");
	$("#realIpandPort").val("");
	$("#deviceIP").val("");
	$("#devicePort").val("");
	$("#sftpNM").val("");
	$("#sftpPW").val("");
	$("#sftpPort").val("");
	$("#zongFenId").val("");
	
	$("#dpiRecId").val("");
	$("#addDeviceNamedpirec").val("");
	$("#deviceTypedpirec").val("");
	$("#area").val("");
	$("#idcHouse").val("");
	$("#deviceIPdpirec").val("");
	$("#devicePortdpirec").val("");
	$("#messageNo").val("");
	$("#servers").val("");
	$("input[name=aaaData]").removeAttr("checked");
	
	$("#dpiUplId").val("");
	$("#deviceTypedpiupl").val("");
	$("#areaUpl").val("");
	$("#idcHouseUpl").val("");
	$("#softwareProvider").val("");
	$("#startIp").val("");
	$("#endIp").val("");
},
//按钮初始化
buttonInit:function(){
	//综分设备查询按钮
	$("#searchZongFen").click(function(){
		deviceManage.refreshZongFen();
	});
	
	//综分设备服务器登录密码增加、减少按钮
	var sftpInputAdd='<div class="form-group" ><label class="col-md-1 control-label p-n">设备用途</label><div class="col-md-2"><select class="form-control" name="deviceTypes"><option selected="" value="">请选择</option>'
		+'<option value="2_0">HTTP</option><option value="2_1">RTSP</option><option value="2_2">VoIP</option><option value="2_3">FTP</option>'
	    +'<option value="2_4">SMTP</option><option value="2_5">POP3</option><option value="2_6">IMAP</option>'
	    +'<option value="2_7">DNS</option><option value="2_8">P2P</option><option value="2_9">Game</option>'
	    +'<option value="2_10">IM</option><option value="3_0">HTTP GET数据接收</option><option value="3_1">WLAN终端数据接收</option>'
	    +'<option value="9_0">分类库下发设备</option></select></div><label class="col-md-2 control-label p-n">物理服务器登录名:密码</label><div class="col-md-1"><input type="text" name="sftpNMs" class="form-control"/></div><div class="col-md-1"><input onKeyUp="getType(this)" name="sftpPWs"  class="form-control"/></div><div class="col-md-1"><span class="help-block m-b-none tips"><a class="sourse-a m-l-n m-r-sm check-minus search"><i class="fa fa-minus"></i></a></span></div></div>';
	var deviceTypes = document.getElementsByName("deviceTypes");
	$("#sftpInputAdd").click(function () {
		if(deviceTypes.length<($("#deviceType option").length-1)){
			$(this).parent().parent().parent().parent().after(sftpInputAdd);
		}
		$(".check-minus").click(function () {
			if($(this).parent().parent().parent().parent().find('select:first').attr('disabled')!='disabled'){
				$(this).parent().parent().parent().remove();
			}
			});
	});
	
	//综分设备增加按钮
	$("#zongfenAdd").click(function(){
		$(".has-error").remove();
		deviceManage.cleanData();
		$("#zftitle").text("新增");
		$("#deviceType").attr("disabled", false);
		$("#virtualIp").attr("readOnly",false);
		$("#isVirtual").removeAttr("disabled");
		$("#isVirtual").attr("checked",false);
		$("#realIpandPort").removeAttr("disabled");
		$("#sftpPW").removeAttr("type");
		$(".seDev").hide();
		$(".vir").hide();
		$(".virCheck").show();
		$(".check-minus").parent().parent().parent().remove();
		$("#deviceType").removeAttr("disabled");
		$.ajax({
			url:"getRealIpList",
			type:'POST',
			success:function(data){
				var html = "";
				data.forEach(function(val,index){
					html +='<option value="'+val.ip+":"+val.port+'">综分设备('+val.ip+':'+val.port+')</option>';
				});
				$("#realIpandPort").children().remove();
				$("#realIpandPort").append(html);
				$("#realIpandPort").selectpicker('refresh');
			}
		});
	});
	//综分下拉框切换
	$("#isVirtual").change(function() {
		var s = $("#isVirtual").is(":checked");
		if($("#isVirtual").is(":checked")){
			$(".vir").show();
		}else{
			$(".vir").hide();
		}
		});
	//综分查询
	$("#addButZF").click(function(){
		deviceManage.zongFenSave();
	});
	
	//DPI接收设备
	$("#deviceTypedpirec").change(function(val){
		if($("#deviceTypedpirec").val()==1){
			$(".idc").show();
		}else if($("#deviceTypedpirec").val()==0){
			$(".idc").hide();
		}
	});
	//dpi接收设备增加按钮
	$("#dpiRecAdd").click(function(){
		$(".has-error").remove();
		deviceManage.cleanData();
		$("#dpiRectitle").text("新增");
		$(".idc").hide();
		$("#deviceTypedpirec").removeAttr("disabled");
		$("#deviceIPdpirec").removeAttr("disabled");
		$("#devicePortdpirec").removeAttr("disabled");
		$("input[name=aaaDate]").removeAttr("checked");
		$.ajax({
			url:"getDictDataList",
			type:'POST',
			data:{"dpiId":0},
			success:function(data){
				if(data!=null && data.areaList.length>0){
					var areaHtml = "<option value=''>请选择</option>";
					data.areaList.forEach(function(val,index){
						areaHtml +="<option value='"+val.areaCode+"'>"+val.areaName+"</option>";
					});
					$("#area").empty().append($(areaHtml));
				}
				if(data!=null && data.houseList.length>0){
					var html = "";
					data.houseList.forEach(function(val,index){
						html +='<option value="'+val.houseId+'">'+val.houseName+'</option>';
					});
					$("#idcHouse").children().remove();
					$("#idcHouse").append(html);
					$("#idcHouse").selectpicker('refresh');
				}
			}
		});
		$.ajax({
			url:"getIsAuto",
			type:'POST',
			success:function(data){
				if(data==0){
					$("#isAutoFlag").val("1");
					$(".ser").show();
					$.ajax({
						url:"getRealIpList",
						type:'POST',
						success:function(data){
							var html = "";
							data.forEach(function(val,index){
								html +='<option value="'+val.ip+'">综分设备('+val.ip+':'+val.port+')</option>';
							});
							$("#servers").children().remove();
							$("#servers").append(html);
						}
					});
				}else{
					$("#isAutoFlag").val("0");
					$(".ser").hide();
				}
			}
		});
	});
	//DPI接收设备查询
	$("#searchDPIRec").click(function(){
		deviceManage.refreshDPIRec();
	});
	$("#addButdpirec").click(function(){
		deviceManage.dpiRecSave();
	})
	
	//DPI上报数据设备
	var ipAdd='<div class="form-group addip"><label class="col-md-1 control-label p-n">开始IP:结束IP</label><div class="col-md-3"><input type="text" name="startIps" class="form-control frominput"></div><div class="col-md-3"><input type="text" name="endIps" class="form-control frominput"></div><div class="col-md-1"><span class="help-block m-b-none tips"><a class="sourse-a m-l-n m-r-sm check-minus search"><i class="fa fa-minus"></i></a></span></div></div>';
	$("#ipInputAdd").click(function () {
		$(this).parent().parent().parent().parent().after(ipAdd);
		$(".check-minus").click(function () {
           $(this).parent().parent().parent().remove();
	    });
	});
	
	$("#deviceTypedpiupl").change(function(val){
		if($("#deviceTypedpiupl").val()==1){
			$(".area").hide();
			$(".upl").show();
		}else if($("#deviceTypedpiupl").val()==0){
			$(".upl").hide();
			$(".area").show();
		}
	});
	
	$("#dpiUplAdd").click(function(){
		$(".has-error").remove();
		$(".area").show();
		$(".addip").remove();
		deviceManage.cleanData();
		$("#dpiUpltitle").text("新增");
		$(".upl").hide();
		$("#ipInputAdd").show();
		$.ajax({
			url:"getDictDataList",
			type:'POST',
			data:{"dpiId":0},
			success:function(data){
				if(data!=null && data.areaList.length>0){
					var areaHtml = "<option value=''>请选择</option>";
					data.areaList.forEach(function(val,index){
						areaHtml +="<option value='"+val.areaCode+"'>"+val.areaName+"</option>";
					});
					$("#areaUpl").empty().append($(areaHtml));
				}
				if(data!=null && data.houseList.length>0){
					var html = "<option value=''>请选择</option>";
					data.houseList.forEach(function(val,index){
						html +='<option value="'+val.houseId+'">'+val.houseName+'</option>';
					});
					$("#idcHouseUpl").empty().append($(html));
				}
				if(data!=null && data.factory.length>0){
					var factoryHtml = "<option value=''>请选择</option>";
					data.factory.forEach(function(val,index){
						factoryHtml +='<option value="'+val.facotryCode+'">'+val.facotryName+'</option>';
					});
					$("#softwareProvider").empty().append($(factoryHtml));
				}
			}
		});
	});
	
	$("#addButdpiupl").click(function(){
		deviceManage.dpiUplSave();
	});
	
	$("#searchDPIUpl").click(function(){
		deviceManage.refreshDPIupl();
	});
},
//综分设备保存
zongFenSave:function(){
	$(".has-error").remove();
	var zongfenName = $("#addDeviceName").val();
	if(zongfenName==""){
		 warn('nameMessage','请输入设备名称');
		return false;
	}else if(zongfenName.length>50){
		warn('nameMessage','设备名称不能超过50个字符');
		return false;
	}
	
	var zongfenIp = "";
	var realIp = $("#realIpandPort").val();
	var isVirtualIp = 0;
	if(realIp==null){
		var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请选择物理IP地址:端口</span></div>';
		$("#isVirtual").parent().after(html);
		return false;
	}
	if($("#isVirtual").is(":checked")){
		zongfenIp = $("#virtualIp").val();
		if(zongfenIp==""){
			warn('virtualIpMessage','请输入虚拟IP');
			return false;
		}else if(!(checkIP(zongfenIp))){
			warn('virtualIpMessage','请输入正确虚拟IP');
			return false;
		}
		isVirtualIp = 1;
	}
	
	var users = [];
	var deviceTypes = document.getElementsByName("deviceTypes");
	var sftpNMs = document.getElementsByName("sftpNMs");
	var sftpPWs = document.getElementsByName("sftpPWs");
	var message = "";
	start:
	for(var m=0;m<deviceTypes.length;m++){
		for(var n=m+1;n<deviceTypes.length;n++){
			if(deviceTypes[m].value!=null && deviceTypes[n].value!=null 
					&& deviceTypes[m].value==deviceTypes[n].value){
				message = "设备用途重复";
				break start;
			}
			if(sftpNMs[m].value!=null && sftpNMs[n].value!=null 
					&& sftpNMs[m].value==sftpNMs[n].value){
				message = "服务器登录名重复";
				break start;
			}
		}
	}
	if(message != ""){
		swal({
			title: "保存失败",
			text: message,
			type: "error"
		});
		return false;
	}
	for(var i=0;i<deviceTypes.length;i++){
		if( deviceTypes[i].value==""){
			var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请选择设备用途</span></div>';
			$("#isZongFen").find("a:eq("+i+")").parent().parent().after(html);
			return false;
		}
		if( sftpNMs[i].value==""){
			var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入服务器登录名</span></div>';
			$("#isZongFen").find("a:eq("+i+")").parent().parent().after(html);
			return false;
		}
		if( sftpPWs[i].value==""){
			var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入服务器登录密码</span></div>';
			$("#isZongFen").find("a:eq("+i+")").parent().parent().after(html);
			return false;
		}
		var allType = deviceTypes[i].value.split("_");
		var	packetType = allType[0];
		var	packetSubType = allType[1];
		var user = {zongfenId:$("#zongFenId").val(),
				zongfenFtpUser:sftpNMs[i].value,
				zongfenFtpPwd:sftpPWs[i].value,
				packetType:packetType,
				packetSubType:packetSubType};
		users.push(user);
	}
	var sftpPort = $("#sftpPort").val();
	if(sftpPort!=""){
		if(!isNmber0(sftpPort) || sftpPort<0 || sftpPort>65535){
			var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入正确端口</span></div>';
			$("#sftpPortMessage").after(html);
			return false;
		}
	}else if(sftpPort==""){
		var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入端口</span></div>';
		$("#sftpPortMessage").after(html);
		return false;
	}
	var zfDev = {
			zongfenId:$("#zongFenId").val(),
			zongfenIp:zongfenIp,
			zongfenFtpPort:sftpPort,
			zongfenName:zongfenName,
			realIp:realIp,
			isVirtualIp:isVirtualIp,
			deviceUsers:users
	};
	var URL = "";
	if($("#zongFenId").val()==undefined || $("#zongFenId").val()==""){
		URL = "saveZongFenDev";
	}else{
		URL = "updateZongFenDev";
	}
	$.ajax({
		url:URL,
		type:"POST",
		data:JSON.stringify(zfDev),
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
                	$("#zongfenModal").modal('hide');
    				deviceManage.refreshZongFen();
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
},
//dpi接收设备保存
dpiRecSave:function(){
	$(".has-error").remove();
	var dpiId = $("#dpiRecId").val();
	var dpiDevName = $("#addDeviceNamedpirec").val();
	var probeType = $("#deviceTypedpirec").val();
	if(dpiDevName==""){
		warn('nameMessagedpirec','请输入设备名称');
		return false;
	}else if(dpiDevName.length>50){
		warn('nameMessagedpirec','设备名称不能超过50个字符');
		return false;
	}
	if(probeType ==""){
		warn('typeMessagedpirec','请选择设备类型');
		return false;
	}
	var areaCode = $("#area").val();
	if(areaCode==""){
		warn('areaMessage','请选择所属区域');
		return false;
	}
	var idcHouseIds =[""];
	if(probeType==1){
		idcHouseIds = $("#idcHouse").val();
		if(idcHouseIds==null){
			warn('idcHouseMessage','请选择机房');
			return false;
		}
	}
	var isAuto = $("#isAutoFlag").val();
	var policyIp = "";
	if(isAuto=="1"){
		policyIp = $("#servers").val();
	}
	var aaaData = $('input[name="aaaData"]:checked').val();
	if(aaaData=="" || aaaData==undefined){
		warn('aaaDataMessage','请选择是否接收AAA数据');
		return false;
	}
	var dpiIp = $("#deviceIPdpirec").val();
	var dpiPort = $("#devicePortdpirec").val();
	if(dpiIp == ""){
		var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入IP</span></div>';
		$("#devicePortdpirec").parent().after(html);
		return false;
	}else if(!checkIP(dpiIp)){
		var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入正确IP</span></div>';
		$("#devicePortdpirec").parent().after(html);
		return false;
	}
	
	if(dpiPort == ""){
		var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入端口</span></div>';
		$("#devicePortdpirec").parent().after(html);
		return false;
	}else if(!isNmber0(dpiPort) || dpiPort<0 || dpiPort>65535){
		var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入正确端口</span></div>';
		$("#devicePortdpirec").parent().after(html);
		return false;
	}
	var messageNo = $("#messageNo").val();
	var dpiRecDevice = {
			"dpiId":dpiId,
			"dpiDevName":dpiDevName,
			"probeType":probeType,
			"areaCode":areaCode,
			"dpiIp":dpiIp,
			"dpiPort":dpiPort,
			"messageNo":messageNo,
			"idchouses":idcHouseIds,
			"policyIp":policyIp,
			"radiusFlag":aaaData
	};
	if(dpiId=="" || dpiId==null){
		$.ajax({
			url:"dpiRecDeviceInsert",
			type:"POST",
			data:JSON.stringify(dpiRecDevice),
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
	                	$("#dpiRecModal").modal('hide');
	    				deviceManage.refreshDPIRec();
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
			url:"dpiRecDeviceUpdate",
			type:"POST",
			data:JSON.stringify(dpiRecDevice),
			dataType:"JSON",
			headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            },
			success:function(data){
				if(data.result==1){
					swal({
	                    title:"",
	                    text: "保存成功",
	                    html: false
	                }, function(isConfirm) {
	                	$("#dpiRecModal").modal('hide');
	    				deviceManage.refreshDPIRec();
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
//dpi上报数据设备保存
dpiUplSave:function(){
	$(".has-error").remove();
	var dpiId = $("#dpiUplId").val();
	var probeType = $("#deviceTypedpiupl").val();
	if(probeType ==""){
		warn('typeMessagedpiupl','请选择设备类型');
		return false;
	}
	var areaId = "";
	if(probeType==0){
		areaId = $("#areaUpl").val();
		if(areaId==""){
			warn('areaUplMessage','请选择所属区域');
			return false;
		}
	}else{
		areaId	= $('#idcHouseUpl').val();
		if(areaId==""){
			warn('idcHouseUplMessage','请选择机房');
			return false;
		}
		
	}
	
	var softwareProvider = $("#softwareProvider").val();
	if(softwareProvider=="" || softwareProvider==null){
		warn('softwareMessage','请选择厂家来源');
		return false;
	}
	
	//多组IP校验
	var startIps = document.getElementsByName("startIps");
	var endIps = document.getElementsByName("endIps");
	var startIp = [];
	var endIp = [];
	if(startIps.length>0){
		for(var i=0;i<startIps.length;i++){
			if( startIps[i].value=="" || endIps[i].value=="" ){
				var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入IP</span></div>';
				$("#ipsUpload").find("a:eq("+i+")").parent().parent().after(html);
				return false;
			}
			
			if(!(checkIP(startIps[i].value) && checkIP(endIps[i].value))){
				var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入正确IP</span></div>';
				$("#ipsUpload").find("a:eq("+i+")").parent().parent().after(html);
				return false;
			}
			
			if(ip2int(startIps[i].value) > ip2int(endIps[i].value)){
				var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入正确IP地址段</span></div>';
				$("#ipsUpload").find("a:eq("+i+")").parent().parent().after(html);
				return false;
			}
			for(var j=i+1;j<startIps.length;j++){
				if((ip2int(startIps[i].value) <= ip2int(startIps[j].value) && ip2int(endIps[i].value)>= ip2int(endIps[j].value)) 
						|| (ip2int(startIps[i].value)>= ip2int(startIps[j].value) && ip2int(endIps[i].value)<= ip2int(endIps[j].value))
						|| (ip2int(startIps[i].value)<= ip2int(startIps[j].value) && ip2int(endIps[i].value)>= ip2int(startIps[j].value))
						|| (ip2int(startIps[i].value)<= ip2int(startIps[j].value) && ip2int(endIps[i].value)>= ip2int(endIps[j].value))){
					var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>存在IP地址段覆盖</span></div>';
					$("#ipsUpload").find("a:eq("+j+")").parent().parent().after(html);
					$("#ipsUpload").find("a:eq("+i+")").parent().parent().after(html);
					return false;
				}
			}
			startIp.push(startIps[i].value);
			endIp.push(endIps[i].value);
		};
	}
	
	var dpiRecDevice = {
			"dpiId":dpiId,
			"startIps":startIp,
			"endIps":endIp,
			"softwareProvider":softwareProvider,
			"areaId":areaId,
			"probeType":probeType
	};
	var URL = "";
	if(dpiId==undefined || dpiId==""){
		URL = "saveUploadDev";
	}else{
		URL = "updateUploadDev";
	}
	$.ajax({
		url:URL,
		type:"POST",
		data:JSON.stringify(dpiRecDevice),
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
                	$("#dpiUplModal").modal('hide');
    				deviceManage.refreshDPIupl();
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
},
editInit:function(device,flag){
	$(".has-error").remove();
	deviceManage.cleanData();
	$(".check-minus").parent().parent().parent().remove();
	if(flag==1){
		$("#zftitle").text("修改");
		$("#addDeviceName").val(device.zongfenName);
		$("#deviceType").val(device.zongfenType);
		$("#sftpNM").val(device.zongfenFtpUser);
		$("#sftpPW").val(device.zongfenFtpPwd);
		$("#sftpPW").attr("type","password");
		$("#zongFenId").val(device.zongfenId);
		if(device.isVirtualIp==1){
			$("#realIpandPort").attr("disabled",false);
			$("#virtualIp").val(device.zongfenIp);
			$("#virtualIp").attr("readOnly",true);
			$(".virCheck").show();
			$("#isVirtual").prop('checked',true);
			$("#isVirtual").attr("disabled", "disabled");
			$(".vir").show();
		}else{
			$("#realIpandPort").attr("disabled",true);
			$(".virCheck").hide();
			$(".vir").hide();
		}
		var ips = [];
		device.realIp.forEach(function(val,index){
			ips.push(val+":"+device.zongfenPort);
		});
		$.ajax({
			url:"getRealIpList",
			type:'POST',
			success:function(data){
				var html = "";
				data.forEach(function(val,index){
					html +='<option value="'+val.ip+":"+val.port+'">服务器('+val.ip+':'+val.port+')</option>';
				});
				$("#realIpandPort").children().remove();
				$("#realIpandPort").append(html);
				$("#realIpandPort").selectpicker('refresh');
				$("#realIpandPort").selectpicker('val',ips);
			}
		});
		//综分设备服务器登录密码增加、减少按钮
		if(device.deviceUsers.length>1){
			$("#deviceType").attr("disabled","disabled");
			var sftpInputAdd="";
			for(var i=0;i<device.deviceUsers.length;i++){
				var type = device.deviceUsers[i].packetType+"_"+device.deviceUsers[i].packetSubType;
				if(i==0){
					$("#deviceType").val(type);
					$("#sftpNM").val(device.deviceUsers[0].zongfenFtpUser);
					$("#sftpPW").val(device.deviceUsers[0].zongfenFtpPwd);
				}else{
					var html = '<div class="form-group" ><label class="col-md-1 control-label p-n">设备用途</label><div class="col-md-2"><select class="form-control" disabled="disabled" id="'+type+'" name="deviceTypes"><option selected="" value="">请选择</option><option value="2_0">HTTP</option>'
					+'<option value="2_1">RTSP</option><option value="2_2">VoIP</option><option value="2_3">FTP</option>'
                    +'<option value="2_4">SMTP</option><option value="2_5">POP3</option><option value="2_6">IMAP</option>'
                    +'<option value="2_7">DNS</option><option value="2_8">P2P</option><option value="2_9">Game</option>'
                    +'<option value="2_10">IM</option><option value="3_0">HTTP GET数据接收</option><option value="3_1">WLAN终端数据接收</option>'
                    +'<option value="9_0">分类库下发设备</option></select></div><label class="col-md-2 control-label p-n">物理服务器登录名:密码</label><div class="col-md-1"><input type="text" name="sftpNMs" value="'
                    +device.deviceUsers[i].zongfenFtpUser+'"class="form-control"/></div><div class="col-md-1"><input type="password" name="sftpPWs" value="'+device.deviceUsers[i].zongfenFtpPwd
                    +'" class="form-control"/></div><div class="col-md-1"><span class="help-block m-b-none tips"><a class="sourse-a m-l-n m-r-sm check-minus search"><i class="fa fa-minus"></i></a></span></div></div>';
					if(sftpInputAdd!=""){
						sftpInputAdd = sftpInputAdd + html;
					}else{
						sftpInputAdd = html;
					}
				}
			}
			$("#deviceType").parent().parent().after($(sftpInputAdd));
			for(var j=1;j<device.deviceUsers.length;j++){
				var type = device.deviceUsers[j].packetType+"_"+device.deviceUsers[j].packetSubType;
				$("#"+type).val(type);
			}
		}else{
			var type = device.deviceUsers[0].packetType+"_"+device.deviceUsers[0].packetSubType;
			$("#deviceType").val(type);
			$("#sftpNM").val(device.deviceUsers[0].zongfenFtpUser);
			$("#sftpPW").val(device.deviceUsers[0].zongfenFtpPwd);
		}
		$(".check-minus").click(function () {
			$(".has-error").remove();
			var type = $(this).parent().parent().parent().find("select:first").val();
			if(type!="9_0"){
				var data = {
					'zongFenId':$("#zongFenId").val(),
					'type':type
				};
				$.ajax({
					url:"checkDeviceUseage",
					type:'POST',
					data:data,
					success:function(data){
						if(data>0){
							var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>该用途被关联不能删除</span></div>';
							$("#"+type).parent().parent().find('i:first').parent().parent().parent().after(html);
						}else{
							$("#"+type).parent().parent().remove();
						}
					}
				});
			}else{
				$(this).parent().parent().parent().remove();
			}
		});
		$("#sftpPort").val(device.zongfenFtpPort)
		$("#zongfenModal").modal('show');
	}else if(flag==2){
		$("#dpiRectitle").text("修改");
		$("#dpiRecId").val(device.dpiId);
		$("#addDeviceNamedpirec").val(device.dpiDevName);
		$("#deviceTypedpirec").val(device.probeType);
		$("#deviceIPdpirec").val(device.dpiIp);
		$("#devicePortdpirec").val(device.dpiPort);
		$("#messageNo").val(device.messageNo);
		if(device.probeType==1){
			$(".idc").show();
		}else if(device.probeType==0){
			$(".idc").hide();
		}
		$.ajax({
			url:"getDictDataList",
			type:'POST',
			data:{"dpiId":device.dpiId},
			success:function(data){
				if(data!=null && data.areaList.length>0){
					var areaHtml = "<option value=''>请选择</option>";
					data.areaList.forEach(function(val,index){
						areaHtml +="<option value='"+val.areaCode+"'>"+val.areaName+"</option>";
					});
					$("#area").empty().append($(areaHtml));
					$("#area").val(device.areaCode);
				}
				if(data!=null && data.houseList.length>0){
					var html = "";
					data.houseList.forEach(function(val,index){
						html +='<option value="'+val.houseId+'">'+val.houseName+'</option>';
					});
					$("#idcHouse").children().remove();
					$("#idcHouse").append(html);
					$("#idcHouse").selectpicker('refresh');
					if(device.probeType==1){
						$("#idcHouse").selectpicker("val",data.selectIdc);
					}
				}
			}
		});
		$.ajax({
			url:"getIsAuto",
			type:'POST',
			success:function(data){
				if(data==0){
					$(".ser").show();
					$.ajax({
						url:"getRealIpList",
						type:'POST',
						success:function(data){
							var html = "";
							data.forEach(function(val,index){
								html +='<option value="'+val.ip+'">服务器('+val.ip+':'+val.port+')</option>';
							});
							$("#servers").children().remove();
							$("#servers").append(html);
							$("#servers").val(device.policyIp);
						}
					});
					$("#isAutoFlag").val("1");
				}else{
					$("#isAutoFlag").val("0");
					$(".ser").hide();
				}
			}
		});
		$(":radio[name='aaaData'][value='" + device.radiusFlag + "']").prop("checked", "checked");
		$("#deviceTypedpirec").attr("disabled","disabled");
		$("#deviceIPdpirec").attr("disabled","disabled");
		$("#devicePortdpirec").attr("disabled","disabled");
		$("#dpiRecModal").modal('show');
	}else if(flag==3){
		$("#dpiUpltitle").text("修改");
		$("#dpiUplId").val(device.dpiId);
		$("#deviceTypedpiupl").val(device.probeType);
		$.ajax({
			url:"getDictDataList",
			type:'POST',
			data:{"dpiId":0},
			success:function(data){
				if(data!=null && data.areaList.length>0){
					var areaHtml = "<option value=''>请选择</option>";
					data.areaList.forEach(function(val,index){
						areaHtml +="<option value='"+val.areaCode+"'>"+val.areaName+"</option>";
					});
					$("#areaUpl").empty().append($(areaHtml));
				}
				if(data!=null && data.houseList.length>0){
					var html = "<option value=''>请选择</option>";
					data.houseList.forEach(function(val,index){
						html +='<option value="'+val.houseId+'">'+val.houseName+'</option>';
					});
					$("#idcHouseUpl").empty().append($(html));
				}
				if(data!=null && data.factory.length>0){
					var factoryHtml = "<option value=''>请选择</option>";
					data.factory.forEach(function(val,index){
						factoryHtml +='<option value="'+val.facotryCode+'">'+val.facotryName+'</option>';
					});
					$("#softwareProvider").empty().append($(factoryHtml));
				}
				
				if(device.probeType==0){
					$("#areaUpl").val(device.areaId);
					$(".area").show();
					$(".upl").hide()
				}else{
					$("#idcHouseUpl").val(device.areaId);
					$(".area").hide();
					$(".upl").show()
				}
				$("#softwareProvider").val(device.softwareProvider);
			}
		});
		$("#startIp").val(device.startIp);
		$("#endIp").val(device.endIp);
		$("#ipInputAdd").hide();
		$("#dpiUplModal").modal('show');
	}
},
deleteInit:function(dev,flag){
	swal({
		title: "确定要删除设备信息吗",
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
			var data = {};
			data.flag = flag;
			if(flag==1){
				data.deviceId = dev.zongfenId;
				if(dev.deviceUsers==null){
					data.devType = 0;
				}else{
					for(var i=0;i<dev.deviceUsers.length;i++){
						if(dev.deviceUsers[i].packetSubType != 0 && dev.deviceUsers[i].packetType!=9){
							data.devType = 1;
							break;
						}else{
							data.devType = 2;
						}
					}
				}
				data.dpiType = null;
				data.ip = dev.realIp[0];
			}else if(flag==2){
				data.deviceId = dev.dpiId;
				data.dpiType = dev.probeType;
				data.devType = null;
				data.ip = null;
			}else if(flag==3){
				data.deviceId = dev.dpiId;
				data.dpiType = dev.probeType;
				data.devType = null;
				data.ip = null;
			}
			//表格中的删除操作
			$.ajax({
				url:'deleteDevice',
				type:'POST',
				data:data,
				success:function(data){
					if(data.result==1){
                        if(flag==1){
                        	swal({title:"删除成功！",  
	                            text:"已经永久删除了记录。",  
	                            type:"success"},deviceManage.refreshZongFen());
                        }else if(flag==2){
                        	swal({title:"删除成功！",  
	                            text:"已经永久删除了记录。",  
	                            type:"success"},deviceManage.refreshDPIRec());
                        }else if(flag==3){
                        	swal({title:"删除成功！",  
	                            text:"已经永久删除了记录。",  
	                            type:"success"},function(){
	                            	$("#dpiUplTable").bootstrapTable("refresh",{url:'getDPIUplData'});
	                            });
                        }
					}else if(data.result==0){
						swal("删除", data.message, "error");
						return false;
					}
				}
			});
		} else {
			swal("已取消", "取消了删除操作！", "error")
		}
	});
},
graphInit:function(){
	stage.clear();
	stage.wheelZoom = 0.85;
    var scene = new JTopo.Scene(stage);
    scene.alpha = 1;
    //节点位置、背景、字体、字体位置 
	function addNode( text,img, position) {
		var node = new JTopo.Node(text);
		node.setImage('/static/img/' + img, true);
		node.fontColor = "0, 0, 0";
		scene.add(node);
		return node;
	}
	//连线
	function addLink(nodeA, nodeZ,flag) {
		var link = new JTopo.FlexionalLink(nodeA, nodeZ);
		if(flag==1){
			link.strokeColor = '255, 0, 0';
			link.dashedPattern = 5;//虚线的间隙
		}else{
			link.strokeColor = '204,204,204';
		}
		link.lineWidth = 2; //连线的宽度
		scene.add(link);
		return link;
	}
	$.ajax({
		url:'getNetGraph',
		type:'POST',
		success:function(data){
			//根节点为了排版
			var rootNode = addNode('root', 'icon01.png');
		    var removeNode = [];
		    var alarmNodes = [];
		    removeNode.push(rootNode);
	        if(data.existVirtual==1){
	        	if(data.children!=null){
		        	for(var i=0; i<data.children.length; i++){
				        var node = addNode(data.children[i].ip, 'icon01.png');
				        if(data.children[i].deviceFlag==1){
				        	removeNode.push(node);
				        }
				        addLink(rootNode,node,data.children[i].connectFlag);
			        	if(data.children[i].children!=null && data.children[i].children.length>0){
				            for(var j=0; j<data.children[i].children.length; j++){
				                var thirdNode = addNode(data.children[i].children[j].ip, 'icon03.png');
				                if(data.children[i].children[j].deviceFlag==1){
				                	removeNode.push(thirdNode);
				                }
				                addLink(node, thirdNode,data.children[i].children[j].connectFlag);
				                if(data.children[i].children[j].children!=null && data.children[i].children[j].children.length>0){
				                    for(var k=0; k<data.children[i].children[j].children.length; k++){
				                        var kNode = addNode(data.children[i].children[j].children[k].ip, 'icon04.png');
										if(data.children[i].children[j].children[k].connectFlag==1){
											kNode.alarm = '连接异常';
											var obj = {
													node:kNode,
													alarm:kNode.alarm
											}
											alarmNodes.push(obj);
										}
				                        addLink(thirdNode, kNode,data.children[i].children[j].children[k].connectFlag);
				                    }
				                }
				            }
				        }
		        	}
				}
	        }else{
	        	if(data.children!=null && data.children.length>0){
		            for(var j=0; j<data.children.length; j++){
		                var thirdNode = addNode(data.children[j].ip, 'icon03.png');
		                if(data.children[j].deviceFlag==1){
		                	removeNode.push(thirdNode);
		                }
		                addLink(rootNode, thirdNode,data.children[j].connectFlag);
		                if(data.children[j].children!=null && data.children[j].children.length>0){
		                    for(var k=0; k<data.children[j].children.length; k++){
		                        var kNode = addNode(data.children[j].children[k].ip, 'icon04.png');
								if(data.children[j].children[k].connectFlag==1){
									kNode.alarm = '连接异常';
									var obj = {
											node:kNode,
											alarm:kNode.alarm
									}
									alarmNodes.push(obj);
								}
		                        addLink(thirdNode, kNode,data.children[j].children[k].connectFlag);
		                    }
		                }
		            }
		        }
	        
	        }
			if(alarmNodes.length>0){
	    		setInterval(function() {
	    			for (var id in alarmNodes) {
	    	            var alarmNode = alarmNodes[id]['node'];
	    	            if (!alarmNode) {
	    	                continue;
	    	            }
	    	            if (alarmNode.alarm == null) {
	    	                alarmNode.alarm = "连接异常";
	    	            } else {
	    	                alarmNode.alarm = null;
	    	            }
	    	        }
				}, 600);
			   }
			// 树形布局
		    scene.doLayout(JTopo.layout.TreeLayout('down', 90, 115));
		    if(removeNode.length>0){
		    	for(var n=0;n<removeNode.length;n++){
			    	scene.remove(removeNode[n]);
			    }
		    }
		}
	});
},
init:function(){
	deviceManage.initTable();
	deviceManage.buttonInit();
	deviceManage.graphInit();
	}
};
deviceManage.init();

function edit(index,flag){
	if(flag==1){
		var ZFDevice = $("#zongFenTable").bootstrapTable('getData');
		deviceManage.editInit(ZFDevice[index],flag);
	}else if(flag==2){
		var DpiRecDevice = $("#dpiRecTable").bootstrapTable('getData');
		deviceManage.editInit(DpiRecDevice[index],flag);
	}else if(flag==3){
		var DpiUplDevice = $("#dpiUplTable").bootstrapTable('getData');
		deviceManage.editInit(DpiUplDevice[index],flag);
	}
}

function deleteRow(index,flag){
	if(flag==1){
		var ZFDevice = $("#zongFenTable").bootstrapTable('getData');
		deviceManage.deleteInit(ZFDevice[index],flag);
	}else if(flag==2){
		var DpiRecDevice = $("#dpiRecTable").bootstrapTable('getData');
		deviceManage.deleteInit(DpiRecDevice[index],flag);
	}else if(flag==3){
		var DpiUplDevice = $("#dpiUplTable").bootstrapTable('getData');
		deviceManage.deleteInit(DpiUplDevice[index],flag);
	}
	
}

function getType(obj){
	$(obj).attr('type','password');
}


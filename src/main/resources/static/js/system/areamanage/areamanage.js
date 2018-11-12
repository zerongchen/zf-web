/*
 * AreaManage:区域设置
 *        方法： tableInit     表格初始化，已经相关按钮绑定事件
 *             editInit      编辑页面初始化
 *             refreshData   首页列表表格数据刷新
 *             save          新增和编辑保存
 *             addInit       新增页面初始化
 *             deleteData    批量删除
 *             deleteInit    数据删除
 *             init          方法初始化
 *
 * 
 */

var AreaManage = {
//列表加载
tableInit:function(){
	var areamanage = {
			method: 'post',
			contentType : "application/x-www-form-urlencoded",
			dataType: "json",
			pagination: true,
			showColumns: !0,
			iconSize: "outline",
			sidePagination: "server",
			dataField: "list",
			url:"getAreaManageData",
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
                	field:'areaName',
                	title:'区域名称',
                	formatter:function(value,row,index){
                		var data = "";
                		if(value==null){
                			data = "";
                		} else {
                			data = value;
                		}
						return "<span title='"+data+"'>"+data+"</span>";

                	}
                },{
                	field:'areaShort',
                	title:'区域简称',
                	formatter:function(value,row,index){
                        var data = "";
                        if(value==null){
                            data = "";
                        } else {
                            data = value;
                        }
                        return "<span title='"+data+"'>"+data+"</span>";
                	}
                },{
                	field:'areaCode',
                	title:'区域编码',
                	formatter:function(value,row,index){
                        var data = "";
                        if(value==null){
                            data = "";
                        } else {
                            data = value;
                        }
                        return "<span title='"+data+"'>"+data+"</span>";
                	}
                },{
                	field:'type',
                	title:'操作',
                	formatter:function(value,row,index){
                		var deleteFlag = $("#deleteFlag").val();
                		var modify = $("#modifyFlag").val();
                		var format="";
                		if(value==0){
                			if(modify==1){
                   			 	format+="<a href='#' title='编辑' data-toggle='modal' data-target='#myModaladd' onclick='edit("+index+")' class='m-r'><i class='fa fa-edit fa-lg'></i></a>";
                			}
                			if(deleteFlag==1){
                				format+="<a href='#' onclick='deleteRow("+index+")' title='删除'><i class='fa fa-close fa-lg'></i></a>";
                			}
                			return format; 
                		}else if(value==1){
                			return "";
                		}
                		
                	}
                }]
	};
	$("#table").bootstrapTable(areamanage);
    //请求服务数据时所传参数
    function queryParams(params){
        return{
            //每页多少条数据
            pageSize: params.limit,
            //请求第几页
            pageIndex:params.offset/params.limit+1,
            searchName:$("#searchName").val()
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
    	AreaManage.refreshData();
    });
    $("#addPlush").click(function() {
        var inputPlus = '<div class="form-group newInput" ><label class="col-md-1 control-label p-n">区域名称</label>'+
        	'<div class="col-md-2"><input type="text" name="areaName" id="areaName" class="form-control">'+
        	'</div><label class="col-md-1 control-label p-n">区域简称</label><div class="col-md-1">'+
        	'<input type="text" name="areaShort" id="areaShort" class="form-control"></div>'+
        	'<label class="col-md-1 control-label p-n">区域编码</label><div class="col-md-1">'+
        	'<input type="text" name="areaCode" id="areaCode" class="form-control"></div>'+
        	'<div class="col-md-1"><span class="help-block m-b-none" ><a class="sourse-a m-l-n m-r-sm check-minus">'+
        	'<i class="fa fa-minus"></i></a></span></div></div>';
        $(this).parent().parent().parent().after(inputPlus);
        $(".check-minus").click(function() {
            $(this).parent().parent().parent().remove()
        });
    });
    
    $("#addSubmitBut").click(function(){
    	AreaManage.save();
    });
},
//列表数据刷新
refreshData:function(){
	$("#table").bootstrapTable("refresh",{url:'getAreaManageData'});
},
//保存
save:function(){
	$(".has-error").remove();
	var areaName = document.getElementsByName('areaName'); 
	var areaShort = document.getElementsByName('areaShort');
	var areaCode = document.getElementsByName('areaCode');
	var listObj = [];
	for(var i=0;i<areaName.length;i++){
		if(areaName[i].value==''){
			var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入区域名称</span></div>';
			$("#addOrupdateForm").find("a:eq("+i+")").parent().parent().after(html);	
			return false;
		}
		if(areaShort[i].value==''){
			var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入区域简称</span></div>';
			$("#addOrupdateForm").find("a:eq("+i+")").parent().parent().after(html);	
			return false;
		}
		if(areaCode[i].value==''){
			var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请输入区域编码</span></div>';
			$("#addOrupdateForm").find("a:eq("+i+")").parent().parent().after(html);	
			return false;
		}else if(!isNmber0(areaCode[i].value)){
			var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>区域编码为整数</span></div>';
			$("#addOrupdateForm").find("a:eq("+i+")").parent().parent().after(html);	
			return false;
		}
		for(j=i+1;j<areaName.length;j++){
			if(areaName[i].value==areaName[j].value){
				var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>区域名称重复</span></div>';
				$("#addOrupdateForm").find("a:eq("+i+")").parent().parent().after(html);
				$("#addOrupdateForm").find("a:eq("+j+")").parent().parent().after(html);
				return false;
			}
			if(areaShort[i].value==areaShort[j].value){
				var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>区域简称重复</span></div>';
				$("#addOrupdateForm").find("a:eq("+i+")").parent().parent().after(html);
				$("#addOrupdateForm").find("a:eq("+j+")").parent().parent().after(html);
				return false;
			}
			if(areaCode[i].value==areaCode[j].value){
				var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>区域编码重复</span></div>';
				$("#addOrupdateForm").find("a:eq("+i+")").parent().parent().after(html);
				$("#addOrupdateForm").find("a:eq("+j+")").parent().parent().after(html);
				return false;
			}
		}
		var obj = {
				areaName:areaName[i].value,
				areaCode:areaCode[i].value,
				areaShort:areaShort[i].value,
				parent:$("#parentCode").val()
		};
		listObj.push(obj);
	}
	var areaFlag = $("#oldAreaCode").val();
	if(areaFlag==""){
		$.ajax({
			url:"areasave",
			type:"POST",
			data:JSON.stringify(listObj),
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
	    				AreaManage.refreshData();
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
			url:"areaupdate",
			type:"POST",
			data:JSON.stringify(listObj),
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
	                	AreaManage.refreshData();
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
claeanDate:function(){
	$("#areaCode").val("");
	$("#oldAreaCode").val("");
	$("#areaShort").val("");
	$("#areaName").val("");
},
//新增页面初始化
addInit:function(){
	$("#add").click(function(){
		$(".has-error").remove();
		$(".check-minus").parent().parent().parent().remove();
		$("#title").text("新增");
		$("#areaCode").removeAttr("readOnly");
		$("#addPlush").show();
		AreaManage.claeanDate();
		$.ajax({
			url:"havesetprovince",
			type:"POST",
			dataType:"json",
			success:function(data){
				if(data==null || data==""){
					swal({
						title: "",
						text: "请先设置省份",
						type: "error"
					});
					return false;
				}else{
					$("#parentCode").val(data);
					$("#myModaladd").modal('show');
				}
			}
		});
	});
	
},
editInit:function(area){
	$(".has-error").remove();
	AreaManage.claeanDate();
	$(".check-minus").parent().parent().parent().remove();
	$("#areaCode").val(area.areaCode);
	$("#areaName").val(area.areaName);
	$("#areaShort").val(area.areaShort);
	$("#oldAreaCode").val(area.areaCode);
	$("#areaCode").attr("readOnly",true);
	$("#addPlush").hide();
	$("#title").text("修改");
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
		var flag = false;
		for(var i=0;i<result.length;i++){
			if(result[i].areaCode==result[i].parent){
				swal({  
					title:"删除",
		            text: "省份不能删除！",  
		            type: "warning",  
		            confirmButtonText: '确认',  
		            confirmButtonColor: '#f27474',  
		        }); 
				return false;
			}
			data.push(result[i].areaCode);
		}
		AreaManage.deleteInit(data);
	});
},
deleteInit:function(ids){
	swal({
		title: "确定要删除区域信息吗",
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
				data:{"ids":ids},
				success:function(data){
					if(data.result==1){
						 swal({title:"删除成功！",  
	                            text:"已经永久删除了记录。",  
	                            type:"success"},
	                            AreaManage.refreshData());
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
provinceset:function(){
	$("#provinceSetBtn").click(function(){
		$.ajax({
			url:'getprovincepist',
			type:'POST',
			success:function(data){
				var html = "<option value=''>请选择</option>";
				data.provinceList.forEach(function(val,index){
					html +='<option value="'+val.areaCode+'" shortp="'+val.areaShort+'">'+val.areaName+'</option>';
				});
				$("#province").children().remove();
				$("#province").append(html);
				if(data.province!=null){
					$("#province").val(data.province);
				}
			}
		});
	});
	
	$("#proviceSubmitBut").click(function(){
		var province = $("#province").val();
		var areaname =$("#province option:selected").text();
		if(province == ""){
			warn('provinceMessage','请选择部署省份');
			return false;
		}
		var areaShort = $("#province option:selected").attr('shortp');
		$.ajax({
			url:'setprovince',
			type:'POST',
			data:{"province":province,"areaName":areaname,"areaShort":areaShort},
			success:function(data){
				if(data.result==1){
					swal({
	                    title:"",
	                    text: "设置成功",
	                    html: false
	                }, function(isConfirm) {
	                	$("#myModalProvince").modal('hide');
	                	AreaManage.refreshData();
	                });
				}else if(data.result==0){
					swal({
						title: "设置失败",
						text: data.message,
						type: "error"
					});
				}
			}
		});
	});
},
init:function(){
	AreaManage.tableInit();
	AreaManage.addInit();
	AreaManage.deleteData();
	AreaManage.provinceset();
}
}

AreaManage.init();

function edit(index){
	var area = $("#table").bootstrapTable('getData');
	AreaManage.editInit(area[index]);
}

function deleteRow(index){
	var area = $("#table").bootstrapTable('getData');
	var data = [];
	data.push(area[index].areaCode);
	AreaManage.deleteInit(data);
}

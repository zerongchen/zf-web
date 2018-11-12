
operationLogJs= {

    // 根据条件查询记录
    searchFormSubBut:function () {
        $("#searchFormButton").on('click', function(){
            initTableJs.refreshData();
        })
    },

    initSelector : function(){

        $('#operate').on("change",function(){
            clearWarn($('#operateDiv'));
        })
        $('#userGroupType').on("change",function(){
            clearWarn($('#usertypeDiv'));
        })

        loadSel('appType','/select/getAppType',true,undefined,true);
        $('#appType').on("change",function(){
            loadSel('appId','/select/getAppByType',true,{appType:$("#appType").val()},true,undefined);
        })

        $('#appId').on("change",function(){
            initAppNameInput(this);
        })


    } ,



    //弹出框新增或者
    addOrUpdateData:function(){
        $('#addSubmitBut').on('click', function() {
            $(".has-error").remove();

            var formData = $('#addOrupdateForm').formToJSON();
            var url = "";
            var appUserStrategy = new Object();
            var messageNo = $("#messageNo").val();
            if (messageNo ==undefined || messageNo == ''){
                url = "/apppolicy/appuser/save.do";
            } else {
                appUserStrategy.messageNo = messageNo;
                url = "/apppolicy/appuser/update.do";
            }

            appUserStrategy.messageName = $('#addOrupdateForm').find('input[name="messageName"]').val();
            if (appUserStrategy.messageName==undefined||appUserStrategy.messageName==''){
                // swal("操作失败", "请输入策略名称", "error");
                warn('messageName','请输入策略名称');
                return false;
            }
            appUserStrategy.appType = $('#appType').val();
            if (appUserStrategy.appType==undefined||appUserStrategy.appType==''){
                warnSelect('appTypeDiv');
                // swal("操作失败", "请选择应用类型", "error");
                return false;
            }
            appUserStrategy.appId = $('#appId').val();
            if (appUserStrategy.appId==undefined||appUserStrategy.appId==''){
                warnSelect('appIdDiv');
                // swal("操作失败", "请选择子应用类型", "error");
                return false;
            }

            appUserStrategy.appName = $('#addOrupdateForm').find('input[name="appName"]').val();
            if (appUserStrategy.appId==0){
                if (appUserStrategy.appName==undefined||appUserStrategy.appName==''){
                    warn('appName','请输入自定义子应用名');
                    // swal("操作失败", "请输入自定义子应用名", "error");
                    return false;
                }
            }

            appUserStrategy.userType = $('#userType').val();
            if ($('#start').val()==undefined||$('#start').val()==''){
                warn('startTime','请选择统计开始时间');
                // swal("操作失败", "请选择统计开始时间", "error");
                return false;
            }
            if ($('#end').val()==undefined||$('#end').val()==''){
                warn('endTime','请选择统计结束时间');
                // swal("操作失败", "请选择统计结束时间", "error");
                return false;
            }
            appUserStrategy.rStartTime = $('#start').val();
            appUserStrategy.rEndTime = $("#end").val();

            $.ajax({
                url: url,
                type: 'POST',
                dataType:'json',
                data:{"messageName":appUserStrategy.messageName,"appType":appUserStrategy.appType,
                      "appId":appUserStrategy.appId,"appName":appUserStrategy.appName,
                    "userType":appUserStrategy.userType,"countStartTime":appUserStrategy.rStartTime,
                    "countEndTime":appUserStrategy.rEndTime,"messageNo":appUserStrategy.messageNo},
                success: function (data) {
                    if (data.result==0){
                        $("#messageNo").val('');
                        $('#myModaladd').modal('hide');
                        swal(data.message);
                        initTableJs.refreshData();
                    } else {
                        // $("#messageNo").val('');
                        swal("操作失败", "存在策略名称相同数据", "error");
                    }

                },
                error:function () {

                    swal("操作失败", "取消新增/删除操作！", "error");

                }

            })

        })
    },

    init:function(){
        operationLogJs.initSelector();
        operationLogJs.searchFormSubBut();
        operationLogJs.addOrUpdateData();

        // addUserGroupJs.createUserGroup();
        // addUserGroupJs.exportTemplate();
    }
}

$(document).ready(function() {
    timeBarInit();
    initDate(3,false);

    initTableJs.initParam('/apppolicy/appuser/list.do','/apppolicy/appuser/delete.do',getColumnFunction)
    initTableJs.init2();
    operationLogJs.init();

    // 初始化查询时间条
    initDefinedEleIdDate(3,false,false,"searchStart","searchEnd");
});

function initAppNameInput (row) {
    var appId = $("#appId").val();
    if (appId==0){
        $("#addOrupdateForm").find('input[name="appName"]').val(row.appName);
        $("#addOrupdateForm").find('input[name="appName"]').attr('readonly',false);
    } else {
        $("#addOrupdateForm").find('input[name="appName"]').val('');
        $("#addOrupdateForm").find('input[name="appName"]').attr('readonly',true);
    }
}

function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'messageNo',title: '策略ID',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'messageName',title: '<span title="策略名称">策略名称</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'appTypeName',title: '<span title="WEB分类">WEB分类</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        /*{field:'appType',title:'<span title="WEB分类">WEB分类</span>',
            formatter:function webTypeFormatter(value,row,index){
                var webType = "";
                if (row['appType']==0){
                    webType = "未识别应用";
                } else if (row['appType']==1){
                    webType = "Web 视频类";
                } else if (row['appType']==2){
                    webType = "其它 Web 类";
                } else if (row['appType']==3){
                    webType = "P2PStream";
                } else if (row['appType']==4){
                    webType = "P2PDownload";
                } else if (row['appType']==5){
                    webType = "VideoStream";
                } else if (row['appType']==6){
                    webType = "Download";
                } else if (row['appType']==7){
                    webType = "Email";
                } else if (row['appType']==8){
                    webType = "IM";
                } else if (row['appType']==9){
                    webType = "Game";
                } else if (row['appType']==10){
                    webType = "VoIP";
                } else if (row['appType']==11){
                    webType = "电信自营";
                } else if (row['appType']==12){
                    webType = "其它";
                }
                return "<span title='"+webType+"'>"+webType+"</span>";
            }},*/
        {field:'appPolicy',title:'<span title="策略成功数/异常数">策略成功数/异常数</span>',
            formatter:function(value,row,index){
                if(value=="0/0"){
                    return "0/0";
                }else{
                    return "<a href='#' onclick='getDetail(0,"+index+")' title='详情'>"+value+"</a>";
                }
            }
        },
        {field: 'createTime',title: '<span title="创建时间">创建时间</span>',
            formatter : function (value, row, index) {
                return "<span title='"+timestamp2Time(row['createTime'],'-')+"'>"+timestamp2Time(row['createTime'],'-')+"</span>";
            }
        },
/*        {field: 'modifyTime',title: '修改时间',
            formatter : function (value, row, index) {
                return timestamp2Time(row['modifyTime'],'-');
            }
        },*/
        {field: 'createOper',title: '<span title="操作人">操作人</span>'},
        {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
    ]
    return column;
}


function newAddButtonFunction(){
    $(".has-error").remove();
    $('#myModaladd').find("h5").text("新增");

    $('#messageNo').val('');

    $('#addOrupdateForm').find('input[name="messageName"]').val('');

    loadSel('appType','/select/getAppType',true,undefined,true);
    loadSel('appId','/select/getAppByType',true,{appType:0},true,undefined);
    $("#addOrupdateForm").find('input[name="appName"]').attr('readonly',false);

    $('#addOrupdateForm').find('input[name="appName"]').val('');
    $('#start').val('');
    $('#end').val('');

}


// 修改
function modifyButtonFunction(row) {
    $(".has-error").remove();
    $('#myModaladd').find("h5").text("修改");

    //使用全局变量保存修改时的messageNo 避免swal时messageNo input被清除无法设置
    messageNo = row.messageNo;

    $('#myModaladd').modal('show');

    $('#messageNo').val(row.messageNo);

    $('#addOrupdateForm').find('input[name="messageName"]').val(row.messageName);

    $('#appType').val(row.appType);

    $('#addOrupdateForm').find('input[name="appName"]').val(row.appName);

    loadSel('appId','/select/getAppByType',true,{appType:$("#appType").val()},true,undefined);
    $('#appId').val(row.appId);
    initAppNameInput(row);

    var startTime = row.rstartTime+"000";
    $('#start').val(timestamp2Time(startTime,"-").substring(0,10));
    var endTime = row.rendTime+"000";
    $("#end").val(timestamp2Time(endTime,"-").substring(0,10));

}


// 删除
function deleteFunction(url,id){

    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var messageNos = new Array();
    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        messageNos.push(item.messageNo);
    }
    if (messageNos.length==0){
        messageNos.push(id);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{"messageNos":messageNos},
        success: function (data) {
            swal("删除成功！", "已经永久删除了这条记录。", "success");
            $("#table>tbody>.hidetr").remove();
            initTableJs.refreshData();
        },
        error:function () {
            swal("删除失败", "取消了删除操作！", "error");
        }
    })
}


function queryParams(params){
    var data = $('#searchForm').formToJSON();
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    if(data.messageName==''){
        data.messageName = undefined;
    }
    if (data.searchStartTime==''){
        data.searchStartTime = undefined;
    }
    if (data.searchEndTime==''){
        data.searchEndTime = undefined;
    }
    return data;
}


//样式格式化
function operatingFormatter(value, row, index){
	var redo = $("#redoFlag").val();
	var deleteFlag = $("#deleteFlag").val();
	var modify = $("#modifyFlag").val();
	var format="";
	if(redo=="1"){
        format +="<a href='#' title='重发' class='m-r'><i class='fa fal fa-share resend'></i></a>";
	}
	if(modify=="1"){
		 format+="<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
	}
	if(deleteFlag=="1"){
		format+="<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
	}
    return format;
}

//点击事件
window.operatingEvents = {

    'click .resend': function (e, value, row, index) {
        e.preventDefault();
        resendPolicy(row);
    },

    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $('#myModaladd').find("h4").text("修改");
        $('#addSubmitBut').show();

        modifyButtonFunction(row);
    },


    'click .refresh': function (e, value, row, index) {
        e.preventDefault();
        initTableJs.refreshData();
    },

    'click .delete': function (e, value, row, index) {
        e.preventDefault();
        swal({
            title: "确定要删除这条信息吗",
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
                deleteFunction('/apppolicy/appuser/delete.do',row.messageNo);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}

function getDetail(showTable,index){
    var webFlow = $("#table").bootstrapTable('getData');
	var redo = $("#redoFlag").val();
    if(redo==1){
    	PolicyDetail.showDetail(webFlow[index].messageNo,8,1,1,showTable);
    }else{
    	PolicyDetail.showDetail(webFlow[index].messageNo,8,1,0,showTable);    	
    }
}


// 重发指定应用用户策略 和用户绑定策略
function resendPolicy(row){
    $.ajax({
        url: "/apppolicy/appuser/resend.do",
        type: 'POST',
        dataType:'json',
        data:{"messageNo":row.messageNo},
        success: function (data) {
            swal("策略重发成功！");
        },
        error:function () {
            swal("操作失败", "重发策略失败！", "error");
        }
    })
}


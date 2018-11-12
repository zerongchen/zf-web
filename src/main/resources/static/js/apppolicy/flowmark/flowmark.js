/********************用户组相关js begin*******************/
var flowMarkStrategy = {
    "messageNo":"",
    "messageName":"",
    "appType":"",
    "appId":"",
    "appName":"",
    "userType":"",
    "userNames":"",
    "qosLabelUp":"",
    "qosLabelDn":""
}


// userType=0时用户组状态
// userType=0时用户组状态
function userTypeOne(){
    $('#userNameDiv').hide();
    $('#puserGroup').hide();
    $('#userNameDiv .newInput').remove();
    $('#userType').val(0);
}

// 用户类型为账号或者IP时显示的div
function otherUserTypeDiv(){
    $('#userNameDiv').show();
    $('#puserGroup').hide();
    $('#userNameDiv .newInput').remove();
}

// 用户组时显示的div
function userGroupDiv() {
    $('#userNameDiv').hide();
    $('#puserGroup').show();
    $('#userNameDiv .newInput').remove();
}

//点击+追加input框,编辑根据userName 个数回填
function addInputEvent() {
    var inputPlus = "<div class='form-group newInput' ><label class='col-md-2 control-label p-n'></label>"
        +"<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
        +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
        +"</div>";
    $("#userNameDiv .form-group").last().after(inputPlus);
    $(".check-minus").click(function() {
        $(this).parent().parent().parent().remove()
    });
}
/********************用户组相关js end*******************/


/*******************流量标记相关事件绑定*********************/
flowMarkJs= {

    // 查询按钮单击事件绑定
    searchFormSubBut:function () {
        $("#searchFormButton").on('click', function(){
            initTableJs.refreshData();
        })
    },

    // 初始化appType和appId下拉选
    initAppTypeAndAppIdSelector : function () {
        loadSel('appType','/select/getAppType',true,undefined,true);
        $('#appType').on("change",function(){
            loadSel('appId','/select/getAppByType',true,{appType:$("#appType").val()},true,undefined);
        })

        $('#appId').on("change",function(){
            initAppNameInput(this);
        })
    },

    // 初始化用户组相关操作
    initUserGroup :function () {

        $('#newAddGroup').click(function () {
            document.getElementById("createUserGroupForm").reset();
            $('#add-usergroup-snippet').modal('show');
            $('#operate').val(0);
            $('#operateDiv').hide();
        });

        $("#addPlush").click(function() {
            var inputPlus = "<div class='form-group newInput' ><label class='col-md-2 control-label p-n'></label>"
                +"<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
                +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
                +"</div>";
            $(this).parent().parent().parent().after(inputPlus);
            $(".check-minus").click(function() {
                $(this).parent().parent().parent().remove();
            });
        });

        $('#userType').change(function () {
            clearWarn($('#addOrupdateForm'));
            var userType = this.value;

            $('#addOrupdateForm').find('input[name="userName"]').val('');
            if(userType == 0){
                userTypeOne();
            }else if(userType == 1 || userType == 2){
                otherUserTypeDiv();
            }else if(userType == 3){
                userGroupDiv();
                loadUserGroup();
            }

        });
    },

    clearWarnDiv : function(){
        $('#operate').on("change",function(){
            clearWarn($('#operateDiv'));
        })
        $('#userGroupType').on("change",function(){
            clearWarn($('#usertypeDiv'));
        })
    } ,

    //弹出框新增或者
    addOrUpdateData:function(){
        $('#addSubmitBut').on('click', function() {
            $(".has-error").remove();

            var formData = $('#addOrupdateForm').formToJSON();
            var url = "";
            var names = new Array();
            var puserGroup = new Array();

            flowMarkStrategy.messageNo = $("#messageNo").val();
            if (flowMarkStrategy.messageNo ==undefined || flowMarkStrategy.messageNo == ''){
                url = "/apppolicy/flowmark/save.do";
            } else {
                url = "/apppolicy/flowmark/update.do";
            }

            if(formData.userType == 1 || formData.userType == 2){

                var $obj = $('#addOrupdateForm').find('input[name="userName"]');
                var num = $obj.length;
                for (var i=0;i<num;i++){
                    var value  = $obj.eq(i).val();
                    if(value==''){
                        $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入</i>');
                        $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                        return false;
                    }
                    if(formData.userType == 1){
                        if(value.length>50){
                            $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入长度不超过50个字符</i>')
                            $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                            return false;
                        }
                    }else {
                        if(!checkIP(value)){
                            $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入正确IP地址用户</i>')
                            $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                            return false;
                        }
                    }
                    names.push(value);
                }
                formData.userName = names;
                formData.puserGroup=undefined;
            }else if(formData.userType == 3){

                if(formData.puserGroup==undefined){
                    swal("请选择关联的用户组");
                    return false;
                }

                puserGroup = formData.puserGroup.split(",");
                formData.userName = undefined;
                formData.puserGroup = puserGroup;
            }else {
                formData.puserGroup = undefined;
                formData.userName = undefined;
            }

            flowMarkStrategy.messageName = $('#addOrupdateForm').find('input[name="messageName"]').val();
            if (flowMarkStrategy.messageName==undefined||flowMarkStrategy.messageName==''){
                warn('messageName','请输入策略名称');
                // swal("操作失败", "请输入策略名称", "error");
                return;
            }
            flowMarkStrategy.appType = $('#appType').val();
            if (flowMarkStrategy.appType==undefined||flowMarkStrategy.appType==''){
                warnSelect("appTypeDiv");
                // swal("操作失败", "请选择应用类型", "error");
                return;
            }
            flowMarkStrategy.appId = $('#appId').val();
            if (flowMarkStrategy.appId==undefined||flowMarkStrategy.appId==''){
                warnSelect("appIdDiv");
                // swal("操作失败", "请选择子应用类型", "error");
                return;
            }

            flowMarkStrategy.appName = $('#addOrupdateForm').find('input[name="appName"]').val();
            if (flowMarkStrategy.appId==0){
                if (flowMarkStrategy.appName==undefined||flowMarkStrategy.appName==''){
                    warn('appName','请输入自定义子应用名');
                    // swal("操作失败", "请输入自定义子应用名", "error");
                    return;
                }
            }

            flowMarkStrategy.userType = $('#userType').val();
            flowMarkStrategy.qosLabelUp = $('#qosLabelUp').val();
            flowMarkStrategy.qosLabelDn = $('#qosLabelDn').val();
            if ( !(isNmber0(flowMarkStrategy.qosLabelUp) && (flowMarkStrategy.qosLabelUp>=0&&flowMarkStrategy.qosLabelUp<=255)) ){
                warn('appThresholdUpAbsDIV','请输入上行差异化标记值(只能输入0-255数字)');
                // swal("操作失败", "请输入上行差异化标记值(只能输入0-255数字)", "error");
                return;
            }
            if ( !(isNmber0(flowMarkStrategy.qosLabelDn) && (flowMarkStrategy.qosLabelDn>=0&&flowMarkStrategy.qosLabelDn<=255)) ){
                warn('appThresholdDnAbsDIV','请输入下行差异化标记值(只能输入0-255数字)');
                // swal("操作失败", "请输入下行差异化标记值(只能输入0-255数字)", "error");
                return;
            }

            $.ajax({
                url: url,
                type: 'POST',
                dataType:'json',
                data:{"messageNo":flowMarkStrategy.messageNo,"messageName":flowMarkStrategy.messageName,
                    "appType":flowMarkStrategy.appType,"appId":flowMarkStrategy.appId,
                    "appName":flowMarkStrategy.appName,"userType":flowMarkStrategy.userType,
                    "qosLabelUp":flowMarkStrategy.qosLabelUp,"qosLabelDn":flowMarkStrategy.qosLabelDn,
                    "names":names,"groupIds":puserGroup},
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
                    swal("操作失败", "取消操作！", "error");
                }

            })

        })
    },

    init:function(){
        flowMarkJs.searchFormSubBut();
        flowMarkJs.initAppTypeAndAppIdSelector();
        flowMarkJs.initUserGroup();
        flowMarkJs.clearWarnDiv();
        flowMarkJs.addOrUpdateData();

        addUserGroupJs.createUserGroup();
        addUserGroupJs.exportTemplate();
    }

}
/*******************流量标记相关事件绑定*********************/



$(document).ready(function() {

    initSearchDate(3,false);
    initTableJs.initParam('/apppolicy/flowmark/list.do','/apppolicy/flowmark/delete.do',getColumnFunction)
    initTableJs.init2();
    flowMarkJs.init();
});


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
        {field: 'qosLabelUp',title: '<span title="上行差异化标记值">上行差异化标记值</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'qosLabelDn',title: '<span title="下行差异化标记值">下行差异化标记值</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field:'appPolicy',title:'<span title="策略成功数/异常数">策略成功数/异常数</span>',
            formatter:function(value,row,index){
                if(value=="0/0"){
                    return "0/0";
                }else{
                    return "<a href='#' onclick='getDetail(0,"+index+")' title='详情'>"+value+"</a>";
                }
            }
        },
        {field:'bindPolicy',title:'<span title="绑定成功数/异常数">绑定成功数/异常数</span>',
            formatter:function(value,row,index){
                if(value=="0/0"){
                    return "0/0";
                }else{
                    return "<a href='#' onclick='getDetail(1,"+index+")' title='详情'>"+value+"</a>";
                }
            }
        },
        {field: 'createTime',title: '<span title="创建时间">创建时间</span>',
            formatter : function (value, row, index) {
            	var time = timestamp2Time(row['createTime'],'-');
                return "<span title='"+time+"'>"+time+"</span>";
            }
        },
        {field: 'createOper',title: '<span title="操作账号">操作账号</span>',formatter:function(value,row,index){
    		return "<span title='"+value+"'>"+value+"</span>";
    	}},
        {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
    ]
    return column;
}

// 复原新增时页面状态
function newAddButtonFunction(){
    $(".has-error").remove();
    $('#myModaladd').find("h5").text("新增");

    $('#messageNo').val('');

    $('#addOrupdateForm').find('input[name="messageName"]').val('');

    loadSel('appType','/select/getAppType',true,undefined,true);
    loadSel('appId','/select/getAppByType',true,{appType:0},true,undefined);
    $("#addOrupdateForm").find('input[name="appName"]').attr('readonly',false);

    $('#addOrupdateForm').find('input[name="appName"]').val('');
    $('#userType').val(0);
    $('#qosLabelUp').val('');
    $('#qosLabelDn').val('');

    userTypeOne();
}

// 复原修改时页面状态
function modifyButtonFunction(row) {
    $(".has-error").remove();
    $('#myModaladd').find("h5").text("修改");

    $('#myModaladd').modal('show');
    $('#messageNo').val(row.messageNo);
    $('#addOrupdateForm').find('input[name="messageName"]').val(row.messageName);
    $('#appType').val(row.appType);
    $('#addOrupdateForm').find('input[name="appName"]').val(row.appName);

    loadSel('appId','/select/getAppByType',true,{appType:$("#appType").val()},true,undefined);
    $('#appId').val(row.appId);
    initAppNameInput(row);

    var userType = row.bindUser[0].userType;

    $('#userType').val(userType);
    $('#qosLabelUp').val(row.qosLabelUp);
    $("#qosLabelDn").val(row.qosLabelDn);


    // 还原用户组信息
    clearWarn($('#addOrupdateForm'));
    $('#addOrupdateForm').find('input[name="userName"]').val('');
    if(userType == 0){
        userTypeOne();
    }else if(userType == 1 || userType == 2){
        $('#userNameDiv').show();
        $('#puserGroup').hide();
        $('#userNameDiv .newInput').remove();
        $('#userType').val(userType);

        for (var i =0;i<row.bindUser.length;i++){
            if(i==0){
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(row.bindUser[i].userName);
            }else {
                addInputEvent();
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(row.bindUser[i].userName);
            }
        }
    }else if(userType == 3){

        $('#userNameDiv').hide();
        $('#puserGroup').show();
        $('#userNameDiv .newInput').remove();
        $('#userType').val(userType);

        var userGroups = new Array();
        for (var i =0;i<row.bindUser.length;i++){
            userGroups.push(row.bindUser[i].userGroupId);
        }
        loadSelectPicker('puserGroupid','/select/getUserGroup',false,userGroups);
    }

    // initAppNameInput(row);
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

// 获取查询条件
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
	if(redo==1){
        format +="<a href='#' title='重发' class='m-r'><i class='fa fal fa-share resend'></i></a>";
	}
	if(modify==1){
		 format+="<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
	}
	if(deleteFlag==1){
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
                deleteFunction('/apppolicy/flowmark/delete.do',row.messageNo);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}

function getDetail(showTable,index){
	// debugger;
    var webFlow = $("#table").bootstrapTable('getData');
	var redo = $("#redoFlag").val();
    if(redo==1){
    	PolicyDetail.showDetail(webFlow[index].messageNo,7,2,1,showTable);
    }else{
    	PolicyDetail.showDetail(webFlow[index].messageNo,7,2,0,showTable);    	
    }
}


// 重发指定应用用户策略 和用户绑定策略
function resendPolicy(row){
    $.ajax({
        url: "/apppolicy/flowmark/resend.do",
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

/**
 *
 * @param datetype 选择日期格式
 * @param flag 统一系统默认时间 或者为空
 */
function initSearchDate(datetype,flag){

    var startTime = new Date();
    startTime.setDate(startTime.getDate()-1);
    startTime.setHours(0)
    startTime.setMinutes(0)
    startTime.setSeconds(0)

    var endTime = new Date();
    endTime.setDate(endTime.getDate()-1);
    endTime.setHours(23)
    endTime.setMinutes(59)
    endTime.setSeconds(59)

    var dateFormate="YYYY-MM-DD hh:mm:ss";
    var showTime=true;
    if(datetype ==2){
        dateFormate="YYYY-MM-DD hh";
        showTime=true;
    }else if(datetype ==3){
        dateFormate="YYYY-MM-DD";
        showTime=false;
    }
    var start = {
        elem: "#searchStart",
        format: dateFormate,
        // min: laydate.now(),
        max: endTime.Format(dateFormate),
        istime: showTime,
        istoday: false,
        choose: function(datas) {
            end.min = datas;
            end.start = datas
        }
    };
    var end = {
        elem: "#searchEnd",
        format: dateFormate,
        min:startTime.Format(dateFormate),
        max: "2099-06-16 23:59:59",
        istime: showTime,
        istoday: false,
        choose: function(datas) {
            start.max = datas
        }
    };
    var start1 = {
        elem: "#searchStart",
        format: dateFormate,
        // min: laydate.now(),
        // max: endTime.Format(dateFormate),
        istime: showTime,
        istoday: false,
        choose: function(datas) {
            end1.min = datas;
            end1.start = datas
        }
    };
    var end1 = {
        elem: "#searchEnd",
        format: dateFormate,
        min:laydate.now(),
        max: "2099-06-16 23:59:59",
        istime: showTime,
        istoday: false,
        choose: function(datas) {
            start1.max = datas
        }
    };
    if(flag){
        laydate(start);
        laydate(end);
        $('#searchStart').val(startTime.Format(dateFormate));
        $('#searchEnd').val(endTime.Format(dateFormate));
    }else {
        laydate(start1);
        laydate(end1);
    }
}

function loadUserGroup() {
    loadSelectPicker('puserGroupid','/select/getUserGroup',false);
}

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
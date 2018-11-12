var seq = 0;

//全用户时显示的div
function allUserDiv() {
    $('#userNameDiv').hide();
    $('#puserGroup').hide();
    $('#userNameDiv .newInput').remove();
    $('#userType').val(0);
}

// 用户类型为账号或者IP时显示的div
function otherUserTypeDiv() {
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
//未绑定时显示的div
function allUnBindDiv() {
    $('#userNameDiv').hide();
    $('#puserGroup').hide();
    $('#userNameDiv .newInput').remove();
    $('#userType').val(4);
}

$(document).ready(function () {

    ddosFlowJs.addCircleButton();

    initDate(3, false);
    // initSearchDate(3,false);

    initTableJs.initParam('/apppolicy/ddosflow/list', '/apppolicy/ddosflow/delete', getColumnFunction)
    initTableJs.init2();
    ddosFlowJs.init();
    //查看按钮时间

});


var seeq = 0;

ddosFlowJs = {
    init: function () {
        ddosFlowJs.searchFormSubBut();
    },

    searchFormSubBut: function () {
        $("#searchFormButton").on('click', function () {
            initTableJs.refreshData();
        })
    },
    addCircleButton: function () {
        $('#addOrupdateForm .circle-picture').on('click', function () {

            var $last = $('#addOrupdateForm').find('div[name="attackTypeInsert"]').last();
            var $clone = $last.clone();
            $clone.addClass("newType")
            $clone.find('input[name="attackThreshold"]').val('');
            $clone.find('input[name="attackControl"]').val('');
            $clone.addClass("newInput").find('a.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle').end();
            $last.after($clone);
            seeq++;
            $('#addOrupdateForm a.fa-minus-circle').on('click', function (e) {
                e.preventDefault();
                $(this).parents('div[name="attackTypeInsert"]').remove();
            });
        });
        $('#addSubmitBut').click(function () {
            var formData = $('#addOrupdateForm').formToJSON();

            ddosFlowJs.userType = formData.userType;
            if (formData.userType == 1 || formData.userType == 2) {
                var names = "";
                var $obj = $('#addOrupdateForm').find('input[name="userName"]');
                var num = $obj.length;
                for (var i = 0; i < num; i++) {
                    var value = $obj.eq(i).val();
                    if (value == '') {
                        $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入</i>');
                        $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                        return false;
                    }
                    if (formData.userType == 1) {
                        if (value.length > 50) {
                            $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入长度不超过50个字符</i>')
                            $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                            return false;
                        }
                    } else {
                        if (!checkIP(value)) {
                            $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入正确IP地址用户</i>')
                            $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                            return false;
                        }
                    }
                    names += value;
                    names += ",";
                }
                formData.userName = removeLastComma(names);
                ddosFlowJs.userName = formData.userName;
                ddosFlowJs.userGroupId = undefined;
            } else if (formData.userType == 3) {
                if (formData.userGroupId == undefined) {
                    swal("请选择关联的用户组");
                    return false;
                }
                ddosFlowJs.userGroupIds = formData.userGroupId;
                ddosFlowJs.userName = undefined;
            } else {
                ddosFlowJs.userGroupId = undefined;
                ddosFlowJs.userName = undefined;
            }

            var url = "";
            if ((formData.ddosId == undefined || formData.ddosId == '')
                && (formData.messageNo == undefined || formData.messageNo == '')) {
                url = '/apppolicy/ddosflow/add';
            } else {
                url = '/apppolicy/ddosflow/update';
            }
            // 检查所有参数
            if (checkAllParameter(formData)) {

                filterParameter(formData);
                $.ajax({
                    url: url,
                    type: 'POST',
                    dataType: 'json',
                    // data:JSON.stringify(formData),
                    data: formData,
                    success: function (data) {
                        if (data.result == 0) {
                            $("#messageNo").val('');
                            cleanAllInput();
                            $('#myModaladd').modal('hide');
                            swal(data.message);
                            initTableJs.refreshData();
                        } else {
                            swal("操作失败", "存在策略名称相同数据", "error");
                        }

                    },
                    error: function () {
                        swal("操作失败", "取消新增/删除操作！", "error");
                    }

                })
            }

        });
        // 用户类型改变时绑定改变事件
        $('#userType').change(function () {
            clearWarn($('#addOrupdateForm'));
            var userType = this.value;
            $('#addOrupdateForm').find('input[name="userName"]').val('');
            if (userType == 0) {
                allUserDiv();
            } else if (userType == 1 || userType == 2) {
                otherUserTypeDiv();
            } else if (userType == 3) {
                userGroupDiv();
                loadUserGroup();
            }else if (userType == 4) {
                allUnBindDiv();
            }
        });

    },

}

//绑定新增或者删除账号输入框
$("#addPlush").click(function () {
    var inputPlus = "<div class='form-group newInput' ><label class='col-md-1 control-label p-n'></label>"
        + "<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
        + "<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
        + "</div>";
    $(this).parent().parent().parent().after(inputPlus);
    $(".check-minus").click(function () {
        $(this).parent().parent().parent().remove();
    });
});

//点击+追加input框,编辑根据userName 个数回填
function addInputEvent() {
    var inputPlus = "<div class='form-group newInput' ><label class='col-md-1 control-label p-n'></label>"
        + "<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
        + "<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
        + "</div>";
    $("#userNameDiv .form-group").last().after(inputPlus);
    $(".check-minus").click(function () {
        $(this).parent().parent().parent().remove()
    });
}


function loadUserGroup() {
    loadSelectPicker('userGroupId', '/select/getUserGroup', false);
}

// 保存前对参数进行过滤  去掉为设值的参数
function filterParameter(voipFlow) {

    if (voipFlow.messageNo == '') {
        voipFlow.messageNo = undefined;
    }
}

// 参数合法性校验
function checkAllParameter(formData) {

    ddosFlowJs.messageNo = formData.messageNo;
    ddosFlowJs.messageName = formData.messageName;
    ddosFlowJs.appAttackType = formData.appAttackType;
    ddosFlowJs.attackThreshold = formData.attackThreshold;
    ddosFlowJs.attackControl = formData.attackControl;
    ddosFlowJs.userType = formData.userType;
    ddosFlowJs.userName = formData.userName;
    ddosFlowJs.userGroupId = formData.userGroupId;

    if (formData.messageName == undefined || formData.messageName == "") {
        swal("请输入策略名称");
        return false;
    }
    if (formData.appAttackType == undefined || formData.appAttackType == "") {
        swal("请选择攻击类型");
        return false;
    }
    if (formData.attackThreshold == undefined || formData.attackThreshold == "") {
        swal("请输入攻击阈值");
        return false;
    }
    var atArray=formData.attackThreshold.split(",");
    var acArray=formData.attackControl.split(",");

    for(var i=0;i<atArray.length;i++){
        var at = atArray[i];
        if (!isNumber(at)) {
            swal("攻击阈值请输入整数");
            return false;
        }
        if (at == undefined || at == "") {
            swal("请输入控制阈值");
            return false;
        }

        var ac = acArray[i];
        if (!isNumber(ac)) {
            swal("控制阈值请输入整数");
            return false;
        }
        if (ac == undefined || ac == "") {
            swal("请输入控制阈值");
            return false;
        }
        if (parseInt(ac) > parseInt(at)) {
            swal("控制阈值不能大于攻击阈值");
            return false;
        }
    }

    if (ddosFlowJs.userType == 1 && (formData.userName == undefined || formData.userName == "")) {
        swal("请输入用户名");
        return false;
    }
    if (ddosFlowJs.userType == 2 && (formData.userName == undefined || formData.userName == "")) {
        swal("请输入IP地址");
        return false;
    }
    return true;
}


function addAttackTypeEvent(i) {
    var $last = $('#addOrupdateForm').find('div[name="attackTypeInsert"]').last();
    var $clone = $last.clone();
    $clone.addClass("newInput").find('a.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle').end();
    $last.after($clone);
    $('#addOrupdateForm a.fa-minus-circle').on('click', function (e) {
        e.preventDefault();
        $(this).parents('div[name="attackTypeInsert"]').remove();
    });
}


// 获取所有参数值
function revertProperties(row) {
    $('#ddosId').val(row.ddosId);
    $('#messageNo').val(row.messageNo);
    $('#messageName').val(row.messageName);
    var appAttackType = row.appAttackType.split(",");
    var attackThreshold = row.attackThreshold.split(",");
    var attackControl = row.attackControl.split(",");

    for (var i = 0; i < appAttackType.length; i++) {

        if (i == 0) {
            var opts = $('#addOrupdateForm').find('div[name="attackTypeInsert"]').last().find('select[name="appAttackType"]')
            for (var j = 0; j < opts[0].options.length; j++) {
                if (opts[0].options[j].value == appAttackType[i]) {
                    opts[0].options[j].selected = true;
                    break;
                }
            }
            $('#addOrupdateForm').find('input[name="attackThreshold"]').eq(i).val(attackThreshold[i]);
            $('#addOrupdateForm').find('input[name="attackControl"]').eq(i).val(attackControl[i]);
        } else {
            addAttackTypeEvent(i);
            var opts = $('#addOrupdateForm').find('select[name="appAttackType"]').last();
            for (var j = 0; j < opts[0].options.length; j++) {
                if (opts[0].options[j].value == appAttackType[i]) {
                    opts[0].options[j].selected = true;
                    break;
                }
            }
            $('#addOrupdateForm').find('input[name="attackThreshold"]').eq(i).val(attackThreshold[i]);
            $('#addOrupdateForm').find('input[name="attackControl"]').eq(i).val(attackControl[i]);
        }
    }
    // 还原用户组信息
  //  revertUserTypeInfo(row);
}

//还原用户组信息
function revertUserTypeInfo(row) {
    // 还原用户组信息
    var userType = row.userType;
    clearWarn($('#addOrupdateForm'));
    $('#addOrupdateForm').find('input[name="userName"]').val('');
    if (userType == 0) {
        allUserDiv();
    } else if (userType == 1 || userType == 2) {
        otherUserTypeDiv();
        $('#userType').val(userType);
        var userNames = row.userName.split(",");
        for (var i = 0; i < userNames.length; i++) {
            if (i == 0) {
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(userNames[i]);
            } else {
                addInputEvent();
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(userNames[i]);
            }
        }
    } else if (userType == 3) {
        userGroupDiv();
        $('#userType').val(userType);
        var selectGroup = row.userGroupIds.toString().split(',');
        loadSelectPicker('puserGroupid', '/select/getUserGroup', false, selectGroup);
    }else if(userType == null){
        allUnBindDiv();
    }
}

// 设置参数值
function cleanAllInput() {
    $('#messageNo').val('');
    $('#messageName').val('');

    //清空用户组信息
    allUserDiv();
    $('#addOrupdateForm').find('select[name="appAttackType"]').val("");
    $(".newInput").remove();
    $(".newType").remove();
    $("#addOrupdateForm").find('input[name="attackThreshold"]').val("");
    $("#addOrupdateForm").find('input[name="attackControl"]').val("");
}

function getColumnFunction() {

    var column = [
        {field: 'state', checkbox: true},
        {field: 'messageNo', title: '策略ID',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'messageName', title: '<span title="策略名称">策略名称</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {
            field: 'appAttackType', title: '<span title="攻击类型">攻击类型</span>',
            formatter: function (value, row, index) {
                var appAttackTypeArr = row.appAttackType.split(",");
                var v = "";
                for (var i = 0; i < appAttackTypeArr.length; i++) {
                    v += attactTypeObject[appAttackTypeArr[i]] + "/";
                }
                v = v.substring(0, v.length - 1);
                
                return "<span title='"+v+"'>"+v+"</span>";
            }
        },
        {
            field: 'ddosPolicy', title: '<span title="策略成功数/异常数">策略成功数/异常数</span>',
            formatter: function (value, row, index) {
                if (value == "0/0") {
                    return "0/0";
                } else {
                    return "<a href='#' onclick='getDetail(" + index + ")' title='详情'>" + value + "</a>";
                }
            }
        },
        {
            field: 'createTime', title: '<span title="创建时间">创建时间</span>',
            formatter: function (value, row, index) {
            	var time = timestamp2Time(row['createTime'],'-');
                return "<span title='"+time+"'>"+time+"</span>";
            }
        },

        {field: 'createOper', title: '<span title="操作账号">操作账号</span>'},
        {field: 'operating', title: '操作', formatter: operatingFormatter, events: operatingEvents}
    ]
    return column;
}


function newAddButtonFunction() {

    $('#myModaladd').find("h4").text("新增");
    cleanAllInput();

}


// 修改
function modifyButtonFunction(row) {

    $('#myModaladd').modal('show');
    revertProperties(row);

}


// 删除
function deleteFunction(url,id) {

    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var messageNos = new Array();
    for (var i = 0; i < result.length; i++) {
        var item = result[i];
        messageNos.push(item.messageNo);
    }
    if (messageNos.length==0){
        messageNos.push(id);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data: {"messageNos": messageNos},
        success: function (data) {
            swal("删除成功！", "已经永久删除了这条记录。", "success");
            $("#table>tbody>.hidetr").remove();
            initTableJs.refreshData();
        },
        error: function () {
            swal("删除失败", "取消了删除操作！", "error");
        }
    })
}


function queryParams(params) {
    var data = $('#searchForm').formToJSON();
    data.page = params.offset / params.limit + 1;
    data.pageSize = params.limit;
    if (data.messageName == '') {
        data.messageName = undefined;
    }
    if (data.startTime == '') {
        data.startTime = undefined;
    }
    if (data.endTime == '') {
        data.endTime = undefined;
    }
    return data;
}


//样式格式化
function operatingFormatter(value, row, index) {
    // 策略在有效期才有重发按钮
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
        add_type = "modify";
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
        }, function (isConfirm) {
            if (isConfirm) {
                //表格中的删除操作
                deleteFunction('/apppolicy/ddosflow/delete',row.messageNo);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}

// 去掉字符串最后一个逗号
function removeLastComma(str) {
    str = (str.substring(str.length - 1) == ',') ? str.substring(0, str.length - 1) : str;
    return str;
}

// 去掉字符串第一个逗号
function removeFirstComma(str) {
    str = (str.substring(0) == ',') ? str.substring(1, str.length) : str;
    return str;
}

/**
 *
 * @param datetype 选择日期格式
 * @param flag 统一系统默认时间 或者为空
 */
function initSearchDate(datetype, flag) {

    var startTime = new Date();
    startTime.setDate(startTime.getDate() - 1);
    startTime.setHours(0)
    startTime.setMinutes(0)
    startTime.setSeconds(0)

    var endTime = new Date();
    endTime.setDate(endTime.getDate() - 1);
    endTime.setHours(23)
    endTime.setMinutes(59)
    endTime.setSeconds(59)

    var dateFormate = "YYYY-MM-DD hh:mm:ss";
    var showTime = true;
    if (datetype == 2) {
        dateFormate = "YYYY-MM-DD hh";
        showTime = true;
    } else if (datetype == 3) {
        dateFormate = "YYYY-MM-DD";
        showTime = false;
    }
    var start = {
        elem: "#searchStart",
        format: dateFormate,
        // min: laydate.now(),
        max: endTime.Format(dateFormate),
        istime: showTime,
        istoday: false,
        choose: function (datas) {
            end.min = datas;
            end.start = datas
        }
    };
    var end = {
        elem: "#searchEnd",
        format: dateFormate,
        min: startTime.Format(dateFormate),
        max: "2099-06-16 23:59:59",
        istime: showTime,
        istoday: false,
        choose: function (datas) {
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
        choose: function (datas) {
            end1.min = datas;
            end1.start = datas
        }
    };
    var end1 = {
        elem: "#searchEnd",
        format: dateFormate,
        min: laydate.now(),
        max: "2099-06-16 23:59:59",
        istime: showTime,
        istoday: false,
        choose: function (datas) {
            start1.max = datas
        }
    };
    if (flag) {
        laydate(start);
        laydate(end);
        $('#searchStart').val(startTime.Format(dateFormate));
        $('#searchEnd').val(endTime.Format(dateFormate));
    } else {
        laydate(start1);
        laydate(end1);
    }
}

function getDetail(index) {
    var webFlow = $("#table").bootstrapTable('getData');
	var redo = $("#redoFlag").val();
    if(redo==1){
    	PolicyDetail.showDetail(webFlow[index].messageNo,67,1,1);
    }else{
    	PolicyDetail.showDetail(webFlow[index].messageNo,67,1,0);    	
    }
}

// 重发voip流量管理策略
// 重发指定应用用户策略
function resendPolicy(row) {
    $.ajax({
        url: "/apppolicy/ddosflow/resend.do",
        type: 'POST',
        dataType: 'json',
        data: {"messageNo": row.messageNo},
        success: function (data) {
            swal("策略重发成功！");
        },
        error: function () {
            swal("操作失败", "重发策略失败！", "error");
        }
    })
}

/**
 * @type {{}}
 */
var attactTypeObject = {
    1: "ICMP Redirection",
    2: "ICMP Unreacheble",
    3: "TCP anomaly Connection",
    4: "DNS Query Application Attack",
    5: "DNS Reply Application Attack",
    6: "SIP Application Attack",
    7: "HTTPS Application Attack",
    8: "HTTP Get Application Attack",
    9: "Challenge Collapsar Attack"
}

$("#adddismissBut").click(function () {
    $(".newInput").remove();
    $('#userNameDiv').hide();
    $('#myModaladd').modal('hide');
})

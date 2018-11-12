var bindMessageTypeObject = {};

$(document).ready(function () {
    initTableJs.initParam('/userGroupBind/initTable.do', '/userGroupBind/delete.do', getColumnFunction)
    initTableJs.init2();
    loadMessageType();
    userbindObject.init();
});


function getColumnFunction() {
    //查询条件时间控件初始化
    initSearchDate(3, false);
    var column = [
        {field: 'state', checkbox: true},
        {field: 'bindId', title: '主键', visible: false},
        {field: 'messageNo', title: '策略ID'},
        {
            field: 'userType', title: '用户类型', formatter: function (value, row, index) {
                if (value == "0") {
                    return "全用户";
                } else if (value == "1") {
                    return "账号";
                } else if (value == "2") {
                    return "IP地址";
                } else if (value == "3") {
                    return "用户组";
                }
            }
        },
        {field: 'userName', title: '对应用户',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
        {
            field: 'bindMessageType', title: '策略类型',
            formatter: function (value, row, index) {
                var f= bindMessageTypeObject[value];
                return "<span title='"+f+"'>"+f+"</span>";
            }
        },
        {field: 'bindMessageName', title: '策略名称',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
        {field: 'bindMessageNo', title: '策略编号', visible: false},
        {
            field: 'appPolicy', title: '策略成功数/异常数',
            formatter: function (value, row, index) {
                if (value == "0/0") {
                    return "0/0";
                } else {
                    return "<a href='#' onclick='getDetail(" + index + ")' title='详情'>" + value + "</a>";
                }
            }
        },
        {field: 'createTime', title: '创建时间',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
        {field: 'operating', title: '操作', formatter: operatingFormatter, events: operatingEvents}
    ]
    return column;
}

function newAddButtonFunction() {
    $('#myModaladd').find("h4").text("新增");
    $('#addOrupdateForm').find('select[name="adduserType"]').val('0');
    $('#addOrupdateForm').find('#userNameDiv').hide();
    $('#addOrupdateForm').find('#puserGroup').hide();
    $('#addOrupdateForm').find(".newBindMessageType").remove();
    $('#addOrupdateForm').find('select[name="addbindMessageType"]').val('');
    $('#addOrupdateForm').find('select[name="addbindMessageNo"]').val('');
    loadaddMessageType();
}

function deleteFunction(url,rowbindId,rowbindMessageType,rowbindMessageNo,rowmessageNo) {
    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var bindId = [];
    var bindMessageType = [];
    var bindMessageNo = [];
    var messageNo = [];
    for (var i = 0; i < result.length; i++) {
        var item = result[i];
        // $('#table>tbody>tr').remove('.selected');
        bindId.push(item.bindId);
        bindMessageType.push(item.bindMessageType);
        bindMessageNo.push(item.bindMessageNo);
        messageNo.push(item.messageNo);
    }
    if (bindId.length==0){
        bindId.push(rowbindId);
        bindMessageType.push(rowbindMessageType);
        bindMessageNo.push(rowbindMessageNo);
        messageNo.push(rowmessageNo);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data: {bindId: bindId, bindMessageType: bindMessageType, bindMessageNo: bindMessageNo,messageNo:messageNo},
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
    if (data.bindMessageType == '') {
        data.bindMessageType = undefined;
    }
    if (data.bindMessageNo == '') {
        data.bindMessageNo = undefined;
    }
    if (data.userType == '') {
        data.userType = undefined;
    }
    return data;
}

//样式格式化
function operatingFormatter(value, row, index) {
    var format = ""

    if ($("#zf103003_redo").val() == 1) {
        format += "<a href='#' title='重发' class='m-r'><i class='fa fal fa-share resend'></i></a>";
    } else {
        format += "<a href='#' title='重发' style='display: none' class='m-r'><i class='fa fal fa-share resend'></i></a>";
    }
    if ($("#zf103003_delete").val() == 1) {
        format += "<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
    } else {
        format += "<a href='#' style='display: none' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
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
        $('#addOrupdateForm').find('input[name="userGroupName"]').attr("readOnly", true);
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
                deleteFunction('/userGroupBind/delete',row.bindId,row.bindMessageType,row.bindMessageNo,row.messageNo);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}

/** 新增详情相关js */
function getDetail(index) {
    var webFlow = $("#table").bootstrapTable('getData');
    PolicyDetail.showDetail(webFlow[index].messageNo, 133, 1, 1, 0);
}

/** 重发用户组策略 */
function resendPolicy(row) {
    $.ajax({
        url: "/userGroupBind/resend",
        type: 'POST',
        dataType: 'json',
        data: {"bindId": row.bindId},
        success: function (data) {
            swal("策略重发成功！");
        },
        error: function () {
            swal("操作失败", "重发策略失败！", "error");
        }
    })
}


var userbindObject = {
    searchFormSubBut: function () {
        $("#searchFormButton").on('click', function () {
            initTableJs.refreshData();
        })
    },

    loadBindMessageType: function () {
        var url = '/select/getMessageTypeName';
        var data = {};
        $.ajax({
            url: url,
            type: 'GET',
            data: data,
            async: false,
            dataType: 'json',
            success: function (data) {
                $.each(data, function (i, n) {
                    bindMessageTypeObject[n.value] = n.title;
                });

            }
        });
    },
    init: function () {
        userbindObject.searchFormSubBut();
        userbindObject.loadBindMessageType();
    }
}

// 策略类型改变时绑定改变事件
$('#bindMessageType').change(function () {
    $('#searchForm').find('select[name="bindMessageNo"]').show();
    clearWarn($('#searchForm'));
    var bindMessageType = this.value;
    $('#searchForm').find('select[name="bindMessageNo"]').val('');
    var data = {};
    data.bindMessageType = bindMessageType;
    loadSel('bindMessageNo', '/userGroupBind/getbindMessageName', true, data);
});

function loadMessageType() {
    loadSel('bindMessageType', '/select/getMessageTypeName', true);
}

function loadaddMessageType() {
    loadNameSel($('#addOrupdateForm').find("select[name='addbindMessageType']"), '/select/getMessageTypeName', true);
}

// 策略类型改变时绑定改变事件
$('#addOrupdateForm').find("select[name='addbindMessageType']").change(function () {
    clearWarn($('#addOrupdateForm'));
    var bindMessageType = this.value;
    $('#addOrupdateForm').find('select[name="addbindMessageNo"]').val('');
    var data = {};
    data.bindMessageType = bindMessageType;
    loadNameSel($('#addOrupdateForm').find("select[name='addbindMessageNo']"), '/userGroupBind/getbindMessageName', true, data);
});

// 用户类型改变时绑定改变事件
$('#adduserType').change(function () {
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
    }
});

//全用户时显示的div
function allUserDiv() {
    $('#userNameDiv').hide();
    $('#puserGroup').hide();
    $('#userNameDiv .newInput').remove();
    $('#userType').val("");
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
    $('#userNameDiv .newInput').remove();
    $('#puserGroup').show();
}

function loadUserGroup() {
    loadSelectPicker('userGroupId', '/select/getUserGroup', false);
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

function addNameSelectEvent(oneOpts, twoOpts) {
    oneOpts.change(function () {
        // clearWarn($('#addOrupdateForm'));
        var oneOptsValue = this.value;
        twoOpts.val('');
        var data = {};
        data.bindMessageType = oneOptsValue;
        loadNameSel(twoOpts, '/userGroupBind/getbindMessageName', true, data);
    });
}

$('#addOrupdateForm .circle-picture').on('click', function () {
    var $last = $('#addOrupdateForm').find('div[name="bindMessageTypeInsert"]').last();
    var $clone = $last.clone();
    $clone.find('select[name="addbindMessageType"]').val('');
    $clone.find('select[name="addbindMessageNo"]').val('');
    $clone.find('.fa-info-circle').remove();
    $clone.addClass("newBindMessageType").find('a.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle').end();
    $last.after($clone);

    addNameSelectEvent($clone.find('select[name="addbindMessageType"]'), $clone.find('select[name="addbindMessageNo"]'));

    $('#addOrupdateForm a.fa-minus-circle').on('click', function (e) {
        e.preventDefault();
        $(this).parents('div[name="bindMessageTypeInsert"]').remove();
    });
});

$('#addSubmitBut').click(function () {
    var formData = $('#addOrupdateForm').formToJSON();

    if (formData.adduserType == 1 || formData.adduserType == 2) {
        var names = "";
        var $obj = $('#addOrupdateForm').find('input[name="userName"]');
        var num = $obj.length;
        for (var i = 0; i < num; i++) {
            var value = $obj.eq(i).val();
            if (value == '') {
                $('#addOrupdateForm').find('.fa-info-circle').remove();
                if(formData.adduserType == 1){
                    $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入账号</i>');
                    $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                }else if(formData.adduserType == 2){
                    $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入IP地址</i>');
                    $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                }
                return false;
            }
            if (formData.adduserType == 1) {
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
        formData.userGroupId = undefined;
    } else if (formData.adduserType == 3) {
        if (formData.userGroupId == undefined) {
            swal("请选择关联的用户组");
            return false;
        }
        formData.userName = undefined;
    } else {
        formData.userGroupId = undefined;
        formData.userName = undefined;
    }

    var url = '/userGroupBind/add';
    // 检查所有参数
    if (checkAllParameter(formData)) {

        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            data: formData,
            success: function (data) {
                if (data.result == 1) {
                    cleanAllInput();
                    $('#myModaladd').modal('hide');
                    swal(data.message);
                    initTableJs.refreshData();
                } else {
                    swal("操作失败", "绑定失败", "error");
                }

            },
            error: function () {
                swal("操作失败", "取消新增/删除操作！", "error");
            }
        })
    }
});

// 设置参数值
function cleanAllInput() {
    $('#addOrupdateForm').find('select[name="adduserType"]').val('');
    //清空用户组信息
    allUserDiv();
    $('#addOrupdateForm').find('select[name="addbindMessageType"]').val("");
    $('#addOrupdateForm').find('select[name="addbindMessageNo"]').val("");
    $('#addOrupdateForm').find(".newBindMessageType").remove();
}

// 参数合法性校验
function checkAllParameter(formData) {

    formData.userType = formData.adduserType;
    formData.userName = formData.userName;
    formData.userGroupId = formData.userGroupId;
    formData.addbindMessageType = formData.addbindMessageType;
    formData.addbindMessageNo = formData.addbindMessageNo;

    if(formData.userType ==undefined || formData.userType == ""){
        $('#addOrupdateForm').find('#userTypeDIV').find('.fa-info-circle').remove();
        var $obj = $('#addOrupdateForm').find('select[name="adduserType"]');
        $obj.eq(0).after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请选择用户类型</i>');
        $obj.eq(0).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
        return false;
    }

    var addbindMessageTypeArr = formData.addbindMessageType.split(",");

    for(var i=0;i<addbindMessageTypeArr.length;i++){
        var value=addbindMessageTypeArr[i];
        if(!isNmber0(value)){
            $('#addOrupdateForm').find('div[name="bindMessageTypeInsert"]').find('.fa-info-circle').remove();
            var $obj = $('#addOrupdateForm').find('div[name="bindMessageTypeInsert"]');
            $obj.eq(i).find('a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入策略类型</i>');
            $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
            return false;
        }
    }

    var addbindMessageNoArr = formData.addbindMessageNo.split(",");
    for(var i=0;i<addbindMessageNoArr.length;i++){
        var value=addbindMessageNoArr[i];
        if(value == undefined || value == ""){
            $('#addOrupdateForm').find('div[name="bindMessageTypeInsert"]').find('.fa-info-circle').remove();
            var $obj = $('#addOrupdateForm').find('div[name="bindMessageTypeInsert"]');
            $obj.eq(i).find('a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入策略名称</i>');
            $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
            return false;
        }
    }

    return true;
}

function loadNameSel($opts, url, flag, data, cus, val) {
    $.ajax({
        url: url,
        type: 'GET',
        data: data,
        async: false,
        dataType: 'json',
        success: function (data) {
            var $selId = $opts,
                option = '';
            $selId.children().remove();
            if (flag) {
                option = '<option value="" >请选择</option>';
            }
            if (cus) {
                option += '<option value="0">自定义</option>';
            }
            $.each(data, function (i, n) {
                if (val == n.value) {
                    option += '<option selected="selected" value="' + n.value + '">' + n.title + '</option>';
                } else {
                    option += '<option value="' + n.value + '">' + n.title + '</option>';
                }
            });
            $selId.append(option);
        }
    });
}

// 去掉字符串最后一个逗号
function removeLastComma(str) {
    str = (str.substring(str.length - 1) == ',') ? str.substring(0, str.length - 1) : str;
    return str;
}

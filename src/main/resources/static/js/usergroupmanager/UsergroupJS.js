var seq = 0;
var importurl;
var add = true;

userGroupOperateJs = {

    initparam: function (importUrl) {
        importurl = importUrl
    },
    initcirclePictureClickEvent: function () {
        $('#addOrupdateForm .circle-picture').on('click', function () {
        	
            var fileinput = $('#addOrupdateForm').find('input[name="importFile"]');
            if (fileinput.length >= 10) {
                swal("最多导入10个文件");
                return false;
            }
            var $clone = $('#fileImport').clone(),
                $last = $('#addOrupdateForm').children('div[id^="fileImport"]').last();
            $clone.attr('id', $clone.attr('id') + seq)
                .find('a.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle').end()
                .find('input[name="importFile"]').val('').attr('id', $clone.find('input[name="importFile"]').attr('id') + seq).end()
                .find('div.tooltip').remove(); //删除tooltip元素;
            $last.after($clone);
            seq++;
            initTooltips();
            inputBlurEvent();
            $('#addOrupdateForm a.fa-minus-circle').on('click', function (e) {
                e.preventDefault();
                $(this).parents('div[id^="fileImport"]').remove();
            });
        });
    },

    submitFieButton: function () {
        $('#addSubmitBut').on('click', function () {
        	$(".has-error").remove();
            var addMethod = $("#operationType").find("input[name='operationType']:checked").val();
            // 导入方式增加用户组
            if (addMethod == 0) {
                var formData = $('#addOrupdateForm').formToJSON();
                var fileTypes = ['xlsx', 'xls'];
                var usergroup = formData.userGroupName;
                var operate = formData.operate;
                // var usertype=formData.userGroupType;
                var text = '';
                if (usergroup == '') {
                    clearWarn($('#namegroupDiv'));
                    $('#namegroupDiv').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请输入用户组名</span></div>');
                    $('#namegroupDiv').find('input[name="userGroupName"]').attr('onfocus', 'clearWarn($(\'#namegroupDiv\'))');
                    return false;
                } else if (usergroup.length > 50) {
                    clearWarn($('#namegroupDiv'));
                    $('#namegroupDiv').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>最多支持输入50个字符</span></div>');
                    $('#namegroupDiv').find('input[name="userGroupName"]').attr('onfocus', 'clearWarn($(\'#namegroupDiv\'))');
                    return false;
                }
                if (operate == '') {
                    clearWarn($('#operateDiv'));
                    $('#operateDiv').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请选择</span></div>');
                    return false;
                }

                var fileName = $('#addOrupdateForm').find('input[name="importFile"]');    //得到name为file的控件
                var arrId = new Array();                                //定义一个ID数组
                if (fileName.length > 10) {
                    swal("最多导入10个文件");
                    return false;
                }
                if ($("input[type='file']").val() == "") {
                    swal("请选择文件");
                    return false;
                }
                var isTypeError = false;
                var fileArray = [];
                var isFileRepeat = false;
                for (var i = 0; i < fileName.length; i++) {
                    var filePath = fileName[i].value;
                    var suffix = filePath.substring(filePath.lastIndexOf('.') + 1, filePath.length);
                    if ($.inArray(suffix, fileTypes) == -1) {
                        isTypeError = true;
                        break;
                    }
                    if (fileArray.indexOf(filePath) > -1) {
                        isFileRepeat = true;
                    } else {
                        fileArray.push(filePath);
                    }
                }
                if (isTypeError) {
                    // swal("文件类型错误,仅支持 .xls .xlsx 文件");
                    swal({
                        title: "<small>文件类型错误,或者为空</small>",
                        text: " 仅支持 .xls .xlsx 文件",
                        html: true
                    })
                    return false;
                }
                if (isFileRepeat) {
                    swal({
                        title: "<small>文件名重复,相同的数据只会保留一份</small>",
                        text: " 导入继续 ... ",
                        html: true
                    }, function (isConfirm) {
                        if (isConfirm) {
                            upFile();
                        }
                    })
                } else {
                    upFile();
                }

                function upFile() {
                    for (var j = 0; j < fileName.length; j++) {
                        if (fileName[j] != "") {                            //当file控件里的值不为空时就往ID数组里塞值
                            arrId[j] = fileName[j].id;
                        }
                    }
                    $.ajaxFileUpload({
                        url: importurl,
                        secureuri: false,
                        data: formData,
                        fileElementId: arrId,
                        dataType: "json",
                        success: function (results) {
                            var failLog = '';
                            var result = '';
                            var title = '';
                            var data = results.data;
                            if (data != null && data.length > 0) {
                                for (var i = 0; i < data.length; i++) {
                                    if (data[i].result >= 1) {

                                        title = '<span class="text-danger">操作失败</span>';
                                        failLog += '文件 导入失败,信息如下\r\n'
                                        if (data[i].describtion != null) {
                                            failLog += data[i].describtion + '\r\n';
                                        }
                                        if (data[i].dataErrorInfoList != null && data[i].dataErrorInfoList.length > 0) {
                                            for (var j = 0; j < data[i].dataErrorInfoList.length; j++) {
                                                var array = data[i].dataErrorInfoList[j];
                                                failLog += '第 ' + array.sheet + ' 个表, ' + array.row + ' 行, ' + array.column + ' 列' + array.errorType + '\r\n'
                                            }
                                        }
                                        failLog = encodeURI(failLog);
                                        result = '<a href="#" onclick= \"saveTxt(\'' + failLog + '\',\'用户组新增详情\')\";return false>点击下载详情</a>';
                                    } else {
                                        title = '操作成功';

                                        result = '';
                                    }
                                }
                                swal({
                                    title: title,
                                    text: result,
                                    html: true
                                }, function (isConfirm) {
                                    $('#myModaladd').modal('hide');
                                    initTableJs.refreshData();
                                });
                            } else {
                                swal("操作失败");
                            }
                        },
                        error: function () {
                            swal("操作失败");
                        }

                    })
                }
            } else if (addMethod == 1) { // 手动新增方式

                var userGroupName = $("#userGroupName").val();
                var userGroupId = $('#addOrupdateForm').find('input[name="userGroupId"]').val();
                var messageNo = $('#addOrupdateForm').find('input[name="messageNo"]').val();
                if (userGroupName == undefined || userGroupName == '') {
                    warn("namegroupDiv","用户组名称不能为空");
                    return;
                }
                var userTypeArr = $("#usrTypeDiv .form-group");
                var userArrObj = new Array();
                for (var i = 0; i < userTypeArr.length; i++) {
                    var userObj = {"userName": "", "userType": 0};
                    userObj.userType = $("#usrTypeDiv .form-group").eq(i).find("select[name='user_type']").val();
                    if (userObj.userType == undefined || userObj.userType == '') {
                        swal("用户类型不能为空");
                        return;
                    }
                    if (userObj.userType == 1 ) {
                        userObj.userName = $("#usrTypeDiv .form-group").eq(i).find("input[name='userName']").val();
                        if (userObj.userName == undefined || userObj.userName == '') {
                            $("#usrTypeDiv .form-group").eq(i).find('.help-block.m-b-none').parent().last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" >用户名不能为空</i>');
                            return;
                        }
                    } else if (userObj.userType == 2){
                        userObj.userName = $("#usrTypeDiv .form-group").eq(i).find("input[name='userName']").val();
                        if ( !checkIP(userObj.userName) ) {
                            $("#usrTypeDiv .form-group").eq(i).find('.help-block.m-b-none').parent().last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" >不是正确的IP用户</i>');
                            return;
                        }
                    } else if (userObj.userType == 3 || userObj.userType == 4) {
                        userObj.userName = $("#usrTypeDiv .form-group").eq(i).find("select[name='userName']").val();
                        if (userObj.userName == undefined || userObj.userName == '' || userObj.userName == -1) {
                            $("#usrTypeDiv .form-group").eq(i).find('.help-block.m-b-none').parent().last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" >请选择用户</i>');
                            return;
                        }
                    }

                    userArrObj.push(userObj);
                }
                var param = {
                    "userGroupId": userGroupId,
                    "messageNo": messageNo,
                    "userGroupName": userGroupName,
                    "userInfo": userArrObj,
                }

                $.ajax({
                    url: "/userGroupManager/manualAddUserGroup.do",
                    type: 'POST',
                    dataType: 'json',
                    contentType: "application/json",
                    data: JSON.stringify(param),
                    success: function (data) {
                        if (data.result == 0) {
                            swal("增加成功!");
                            $('#myModaladd').modal('hide');
                            $("#userGroupName").val('');
                            $('#addOrupdateForm').find('input[name="userGroupId"]').val('');
                            $('#addOrupdateForm').find('input[name="messageNo"]').val('');

                            $('#usrTypeDiv').empty();
                            var addedUserDiv = '<div class="form-group" >';
                            addedUserDiv += '       <label class="col-md-1 control-label p-n">用户类型</label>';
                            addedUserDiv += '           <div class="col-md-3">';
                            addedUserDiv += '               <select name="user_type" class="form-control user-group">';
                            addedUserDiv += '                   <option value="1" selected="true">账号用户</option>';
                            addedUserDiv += '                   <option value="2">IP地址段用户</option>';
                            addedUserDiv += '                   <option value="3">DPI链路分组</option>';
                            addedUserDiv += '                   <option value="4">BRAS IP地址段用户组</option>';
                            addedUserDiv += '               </select>';
                            addedUserDiv += '           </div>';
                            addedUserDiv += '       <label class="col-md-2 control-label p-n">用户名  </label>';
                            addedUserDiv += '       <div class="col-md-2" name="userNameDiv">';
                            addedUserDiv += '           <input type="text" name="userName" class="form-control"/>';
                            addedUserDiv += '       </div>';
                            addedUserDiv += '       <div class="col-md-1">';
                            addedUserDiv += '           <span class="help-block m-b-none tips">';
                            addedUserDiv += '               <a class="m-l-n m-r-sm"><i class="fa fa-plus" id="userAddBtn" aria-hidden="true"></i></a>';
                            addedUserDiv += '           </span>';
                            addedUserDiv += '       </div>';
                            addedUserDiv += '   </div>';
                            $('#usrTypeDiv').append(addedUserDiv);
                            initTableJs.refreshData();
                        } else {
                            swal(data.message);
                        }
                    },
                    error: function () {
                        swal("操作失败");
                    }
                });

            }

        })
    },

    searchFormSubBut: function () {
        $("#searchFormButton").on('click', function () {
            initTableJs.refreshData();
        })
    },

    initSelector: function () {
        $('#operate').on("change", function () {
            clearWarn($('#operateDiv'));
        })
        $('#userGroupType').on("change", function () {
            clearWarn($('#usertypeDiv'));
        })
        $('#usrTypeDiv').on("click", function () {
            $("#usrTypeDiv .has-error").remove();
        })
    },

    initClick: function () {
        $("#operationType").find("[name='operationType']").click(
            function () {

                if ($(this).val() == 1) {
                    $("#operationType").find("[name='operationType']").eq(0).removeProp("checked");
                    $("#operationType").find("[name='operationType']").eq(1).prop("checked", "checked");
                    $("#usrTypeDiv").show();
                    $('#operateDiv').hide();
                    $("#fileImport").hide();

                } else {
                    $("#operationType").find("[name='operationType']").eq(0).prop("checked", "checked");
                    $("#operationType").find("[name='operationType']").eq(1).removeProp("checked");

                    $("#usrTypeDiv").hide();
                    $("#fileImport").show();

                    if (add) {
                        $('#operateDiv').hide();
                    } else {
                        $('#operateDiv').show();
                    }
                }
            }
        );

        $(document).on('change', '#usrTypeDiv .form-control.user-group', function () {
            var userType = $(this).val();
            var userNameDiv = $(this).parent().parent().find("div[name='userNameDiv']");
            userNameDiv.empty();
            if (userType == 1 || userType == 2) {
                var selectHtml = "<input type='text' name='userName' class='form-control'/>";
                userNameDiv.append(selectHtml);
            } else if (userType == 3 || userType == 4) {
                var selectHtml = "<select name='userName' class='selectpicker form-control' data-live-search='true'></select>";
                userNameDiv.append(selectHtml);
                var select = $(this).parent().parent().find("div[name='userNameDiv']").find("select[name='userName']");

                var url = "";
                if (userType == 3) {
                    url = "/userGroupManager/getLinkUser.do";
                } else if (userType == 4) {
                    url = "/userGroupManager/getBrasUser.do";
                }
                $.ajax({
                    url: url,
                    data: {},
                    type: "POST",
                    success: function (data) {
                        if (data != null) {
                            var html = "<option value='-1' selected='selected'>请选择用户</option>";
                            for (var i = 0; i < data.length; i++) {
                                if (userType == 3) {
                                    html += "<option  value='" + data[i].mlinkid + "'>" + data[i].mlinkdesc + "</option>";
                                } else if (userType == 4) {
                                    html += "<option  value='" + data[i].brasIp + "'>" + data[i].brasName + "</option>";
                                }

                            }
                            select.append(html);
                            $('.selectpicker').selectpicker('refresh');//加载select框选择器
                        }
                    }
                });

            }

        })
    },


    addMoreUserButton: function () {
        $("#userAddBtn").on('click', function () {
            var addedUserDiv = '<div class="form-group" >';

            addedUserDiv += '       <label class="col-md-1 control-label p-n">用户类型</label>';
            addedUserDiv += '           <div class="col-md-3">';
            addedUserDiv += '               <select name="user_type" class="form-control user-group">';
            addedUserDiv += '                   <option value="1" selected="true">账号用户</option>';
            addedUserDiv += '                   <option value="2">IP地址段用户</option>';
            addedUserDiv += '                   <option value="3">DPI链路分组</option>';
            addedUserDiv += '                   <option value="4">BRAS IP地址段用户组</option>';
            addedUserDiv += '               </select>';
            addedUserDiv += '           </div>';
            addedUserDiv += '       <label class="col-md-2 control-label p-n">用户名  </label>';
            addedUserDiv += '       <div class="col-md-2" name="userNameDiv">';
            addedUserDiv += '           <input type="text" name="userName" class="form-control"/>';
            addedUserDiv += '       </div>';
            addedUserDiv += '       <div class="col-md-1">';
            addedUserDiv += '           <span class="help-block m-b-none tips">';
            addedUserDiv += '               <a class="m-l-n m-r-sm check-minus user"><i class="fa fa-minus" aria-hidden="true"></i></a>';
            addedUserDiv += '           </span>';
            addedUserDiv += '       </div>';
            addedUserDiv += '   </div>';
            $("#usrTypeDiv").append(addedUserDiv);
        })
    },

    minusUserButton: function () {
        $(document).on('click', ".check-minus.user", function () {
            $("#usrTypeDiv .form-group:last").remove();
        })
    },

    init: function () {
        userGroupOperateJs.submitFieButton();
        userGroupOperateJs.initSelector();
        userGroupOperateJs.searchFormSubBut();
        userGroupOperateJs.initClick();
        userGroupOperateJs.addMoreUserButton();
        userGroupOperateJs.minusUserButton();
    }
}

$(document).ready(function () {
    $('.selectpicker').selectpicker({
        noneSelectedText: '',
        noneResultsText: '',
        liveSearch: true,
        size: 10   //设置select高度，同时显示5个值
    });
    initTableJs.initParam('/userGroupManager/initTable.do', '/userGroupManager/delete.do', getColumnFunction)
    initTableJs.init();
    userGroupOperateJs.initparam("/userGroupManager/import.do");
    userGroupOperateJs.init();
});


function getColumnFunction() {
    //查询条件时间控件初始化
    initSearchDate(3, false);
    var column = [
        {field: 'state', checkbox: true},
        {field: 'messageNo', title: '策略ID'},
        {field: 'userGroupId', title: '用户组ID'},
        {field: 'userGroupName', title: '用户组名称',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
        {
            field: 'appPolicy', title: '<span title="策略成功数/异常数">策略成功数/异常数</span>',
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
        // {field: 'status',title: '策略状态'},
//        {field: 'modifyTime',title: '最近修改时间'},
        {field: 'modifyOper', title: '操作账号'},
        {field: 'operating', title: '操作', formatter: operatingFormatter, events: operatingEvents}
    ]
    return column;
}

function exportTemplete() {
    $('#exportTem').remove();
    var form = '<form class="hide" id="exportTem">';
    form += '</form>';
    $('body').append(form);

    $('#exportTem').attr('action', '/userGroupManager/exportTemplate.do').attr('method', 'get').submit();
    return false;
}

function newAddButtonFunction() {
	$(".has-error").remove();
    add = true;
    $('#operateDiv').hide();

    $('#title').text("新增");
    $('#addOrupdateForm').find('input[name="userGroupName"]').val('').attr("readOnly", false);
    $('#addOrupdateForm').find('input[name="importFile"]').val('');
    $('#addOrupdateForm').find('input[name="userGroupId"]').val('');
    $('#addOrupdateForm').find('input[name="messageNo"]').val('');
    $('#operate').val(0)

}

function deleteFunction(url,row) {
    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var userGroupId = [];
    var messageNo = [];
    if(!row){
        for (var i = 0; i < result.length; i++) {
            var item = result[i];
            // $('#table>tbody>tr').remove('.selected');
            userGroupId.push(item.userGroupId);
            messageNo.push(item.messageNo);
        }
    }else {
        userGroupId.push(row.userGroupId);
        messageNo.push(row.messageNo);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data: {userGroupId: userGroupId, messageNo: messageNo},
        success: function (data) {
            swal("删除成功！", "已经永久删除了这条记录。", "success");
            $("#table>tbody>.hidetr").remove();
            initTableJs.refreshData();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {

            swal("删除失败", "取消了删除操作！", "error");
        }
    })
}

function modifyButtonFunction(row) {
    add = false;
    $('#operateDiv').show();
    $('#fileImport').show();
    $('#usrTypeDiv').hide();


    $('#operationType').find("[name='operationType']").eq(1).removeProp("checked");
    $('#operationType').find("[name='operationType']").eq(0).prop("checked", "checked");

    $('#myModaladd').modal('show');
    $('#addOrupdateForm').find('input[name="userGroupName"]').val(row.userGroupName);
    $('#addOrupdateForm').find('input[name="userGroupId"]').val(row.userGroupId);
    $('#addOrupdateForm').find('input[name="messageNo"]').val(row.messageNo);
    $('#addOrupdateForm').find('input[name="importFile"]').val('');
    $('#operate').val('');

}

function detailButtonFunction() {

}

function queryParams(params) {
    var data = $('#searchForm').formToJSON();
    if (data.userGroupName == '') {
        data.userGroupName = undefined;
    }
    return data;
}

function clickOnRowFunction(e, row, element) {
}

//样式格式化
function operatingFormatter(value, row, index) {
    var format = ""

    if ($("#zf103001_query").val() == 1) {
        format += "<a href='#' title='详情' class='m-r'><i class='fa fa-file-text-o fa-lg detail'></i></a>";
    } else {
        format += "<a href='#' title='详情' style='display: none' class='m-r'><i class='fa fa-file-text-o fa-lg detail'></i></a>";
    }
    if ($("#zf103001_redo").val() == 1) {
        format += "<a href='#' title='重发' class='m-r'><i class='fa fal fa-share resend'></i></a>";
    } else {
        format += "<a href='#' title='重发' style='display: none' class='m-r'><i class='fa fal fa-share resend'></i></a>";
    }
    if ($("#zf103001_modify").val() == 1) {
        format +="<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
    } else {
        format +="<a href='#' title='编辑'  style='display: none' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
    }
    if ($("#zf103001_delete").val() == 1) {
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

    'click .detail': function (e, value, row, index) {
        e.preventDefault();
        window.location.href = '/userGroupManager/users?usergroupid=' + row.userGroupId;
    },

    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $(".has-error").remove();
        $('#title').text("修改");
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
                deleteFunction('/userGroupManager/delete.do',row);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}

/** 新增详情相关js */
function getDetail(index) {
    var webFlow = $("#table").bootstrapTable('getData');

    if ($("#zf103001_redo").val() == 1) {
        PolicyDetail.showDetail(webFlow[index].messageNo, 64, 1, 1, 0);
    } else {
        PolicyDetail.showDetail(webFlow[index].messageNo, 64, 1, 0, 0);
    }

}

/** 重发用户组策略 */
function resendPolicy(row) {
    $.ajax({
        url: "/userGroupManager/resend.do",
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
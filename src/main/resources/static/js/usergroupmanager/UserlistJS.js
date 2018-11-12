/***********************************************************************************
 userlistJS	 * action:
 *     方法  searchFormSubBut：页面表单提交按钮，查询按钮，下拉框切换按钮初始化
 *          initClick:新增按钮的 "+" 号
 *          initExportButton：初始化导出按钮(暂时没用)
 *          initExportButton：返回上级
 *          init：初始化对象
 ***********************************************************************************
 function 	 *
            submitForm：form表单提交
 *          newAddButtonFunction：新增页面的初始化
 *          deleteFunction：删除操作
 *          queryParams：表格数据的分页参数设置
 *          getColumnFunction：表头重新设置
 *          window.operatingEvents：点击事情，主要针对于编辑操作
 *          operatingFormatter：表格按钮格式化
 ********************************************************************************/
userlistJS= {
    initClick:function(){
        $(document).on('change', '#detailUsrTypeDiv .form-control.user-group', function () {
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

        $("#detailUserAddBtn").on('click', function () {
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
            $("#detailUsrTypeDiv").append(addedUserDiv);
        })

        $(document).on('click', ".check-minus.user", function () {
            $("#detailUsrTypeDiv .form-group:last").remove();
        })

        $(document).on('click', "#dismissDetailUsrModal", function () {
            $("#detailUsrTypeDiv .check-minus").parent().parent().parent().remove();
        })

        $('#detailUsrTypeDiv').on("click", function () {
            $("#detailUsrTypeDiv .has-error").remove();
        })

    },

    searchFormSubBut:function () {
        $("#searchFormButton").on('click', function(){
            initTableJs.refreshData();
        });
        $('#userType').change(function () {
            clearWarnSelect("insertForm");
        });
        $("#addSubmitBut").click(function () {
        	$(".has-error").remove();
            var dataForm = $('#insertForm').formToJSON();

            var userTypeArr = $("#detailUsrTypeDiv .form-group");
            var userArrObj = new Array();
            for (var i = 0; i < userTypeArr.length; i++) {
                var userObj = {"userGroupId":dataForm.userGroupId,"userName": "", "userType": 0};
                userObj.userType = $("#detailUsrTypeDiv .form-group").eq(i).find("select[name='user_type']").val();
                if (userObj.userType == undefined || userObj.userType == '') {
                    swal("用户类型不能为空");
                    return;
                }
                if (userObj.userType == 1 ) {
                    userObj.userName = $("#detailUsrTypeDiv .form-group").eq(i).find("input[name='userName']").val();
                    if (userObj.userName == undefined || userObj.userName == '') {
                        $("#detailUsrTypeDiv .form-group").eq(i).find('.help-block.m-b-none').parent().last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" >用户名不能为空</i>');
                        return;
                    }
                } else if (userObj.userType == 2){
                    userObj.userName = $("#detailUsrTypeDiv .form-group").eq(i).find("input[name='userName']").val();
                    if ( !checkIP(userObj.userName) ) {
                        $("#detailUsrTypeDiv .form-group").eq(i).find('.help-block.m-b-none').parent().last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" >不是正确的IP用户</i>');
                        return;
                    }
                } else if (userObj.userType == 3 || userObj.userType == 4) {
                    userObj.userName = $("#detailUsrTypeDiv .form-group").eq(i).find("select[name='userName']").val();
                    if (userObj.userName == undefined || userObj.userName == '' || userObj.userName == -1) {
                        $("#detailUsrTypeDiv .form-group").eq(i).find('.help-block.m-b-none').parent().last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" >请选择用户</i>');
                        return;
                    }
                }

                userArrObj.push(userObj);
            }

            $.ajax({
                url: '/userGroupManager/addUser',
                data: JSON.stringify(userArrObj),
                type: 'POST',
                async: false,
                dataType: 'json',
                contentType: "application/json",
                success: function (data) {
                    var title = "";
                    if (data != null) {
                        title = data.message;
                        if(data.result == 0){
                            swal({
                                title: title,
                                text: '',
                                html: true
                            }, function (isConfirm) {
                            });
                        }else {
                            swal({
                                title: title,
                                text: '',
                                html: true
                            }, function (isConfirm) {
                                $('#addUserSnippet').modal('hide');
                                initTableJs.refreshData();
                            });
                        }
                    } else {
                        title ="操作成功";
                        swal({
                            title: title,
                            text: '',
                            html: true
                        }, function (isConfirm) {
                            $('#myModaladd').modal('hide');
                            initTableJs.refreshData();
                        });
                    }
                },
                error:function () {
                    swal("失败，请刷新页面重试");
                }

            })



        })
    },
    initExportButton:function () {
        $('#exportData').on('click',function () {
            $('#searchForm').attr('action', '/userGroupManager/export.do').attr('method', 'get').submit() ;return false;
        })
    },
    returnUserGroup:function () {
      $('#returnUserGroup').on('click',function () {
          window.location.href='/userGroupManager/index';
      })  
    },

    init:function(){
        userlistJS.searchFormSubBut();
        userlistJS.initExportButton();
        userlistJS.returnUserGroup();
        userlistJS.initClick();
    },
}




$(document).ready(function() {
    initTableJs.initParam('/userGroupManager/getUsers.do','/userGroupManager/deleteUsers.do',getColumnFunction)
    initTableJs.init();
    userlistJS.init();

});





function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'userGroupName',title: '所属分组',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
        {field: 'userTypeDesc',title: '用户类型'},
        {field: 'userName',title: '用户账号',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
        {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
    ]
    return column;
}

function deleteFunction(url,data){

    var users = [];
    if(data==undefined){
        var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
        for(var i = 0; i < result.length; i++) {
            var item = result[i];
            // $('#table>tbody>tr').remove('.selected');
            users.push({messageNo:item.messageNo,userGroupId:item.userGroupId,userType:item.userType,userName:item.userName});
        }
    }else {
        users.push(data);
    }

    $.ajax({
        url: url,
        type: 'POST',
        data:JSON.stringify(users),
        contentType: "application/json",
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
function modifyButtonFunction() {

}
function newAddButtonFunction() {
    // $('#title').text("新增");
    $('#addUserSnippet').modal('show');
    document.getElementById("insertForm").reset();
    var userGroupId =$('#searchForm').find('input[name="userGroupId"]').val();
    $('#insertForm').find('input[name="userGroupId"]').val(userGroupId);
    $('#insertForm .newInput').remove();
}

function queryParams(params){
    var data = $('#searchForm').formToJSON();
    if(data.userGroupName ==''){
        data.userGroupName =undefined;
    }
    if(data.userGroupId ==''){
        data.userGroupId =undefined;
    }
    return data;
}

function operatingFormatter(value, row, index){
    var format=""
       /* +"<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";*/

    if ($("#zf103001_delete").val()==1){
        format+="<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
    } else{
        format+="<a href='#' style='display: none' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
    }
    return format;
}
//点击事件
window.operatingEvents = {
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
                deleteFunction('/userGroupManager/deleteUsers',{messageNo:row.messageNo,userGroupId:row.userGroupId,userType:row.userType,userName:row.userName});
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
};
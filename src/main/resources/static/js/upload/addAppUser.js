var appUserAddModalJs = {

    initSelector : function(){

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
        $('#appUserAddSubmitBut').on('click', function() {
            $(".has-error").remove();

            var formData = $('#appUserAddOrupdateForm').formToJSON();
            var url = "";
            var appUserStrategy = new Object();
            var messageNo = $("#messageNo").val();
            if (messageNo ==undefined || messageNo == ''){
                url = "/apppolicy/appuser/save.do";
            } else {
                appUserStrategy.messageNo = messageNo;
                url = "/apppolicy/appuser/update.do";
            }


            appUserStrategy.messageName = $('#appUserAddOrupdateForm').find('input[name="messageName"]').val();
            if (appUserStrategy.messageName==undefined||appUserStrategy.messageName==''){
                // swal("操作失败", "请输入策略名称", "error");
                $("#appUserAddOrupdateForm .form-group").children('.col-md-3.messageName').last().after(
                    '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入策略名称'+'</span></div>');
                $("#appUserAddOrupdateForm .form-group").children('.col-md-3.messageName').find('input').attr('onfocus', 'clearWarn($(\'#appUserAddOrupdateForm\'))');
                // $("#srcIpAndPrefixDiv .form-group").eq(i).children('.col-md-3').find('input').attr('onfocus', 'clearWarn($(\'#'+section+'\'))');
                return ;
                return;
            }
            appUserStrategy.appType = $('#appType').val();
            if (appUserStrategy.appType==undefined||appUserStrategy.appType==''){
                // swal("操作失败", "请选择应用类型", "error");
                $('#appUserAddOrupdateForm .form-group').children('.col-md-3.appType').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请选择</span></div>');
                return;
            }
            appUserStrategy.appId = $('#appId').val();
            if (appUserStrategy.appId==undefined||appUserStrategy.appId==''){
                // swal("操作失败", "请选择子应用类型", "error");
                $('#appUserAddOrupdateForm .form-group').children('.col-md-3.appId').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请选择</span></div>');
                return;
            }

            appUserStrategy.appName = $('#appUserAddOrupdateForm').find('input[name="appName"]').val();
            if (appUserStrategy.appId==0){
                if (appUserStrategy.appName==undefined||appUserStrategy.appName==''){
                    // swal("操作失败", "请输入自定义子应用名", "error");
                    $("#appUserAddOrupdateForm .form-group").children('.col-md-3.appName').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入自定义子应用名'+'</span></div>');
                    $("#appUserAddOrupdateForm .form-group").children('.col-md-3.appName').find('input').attr('onfocus', 'clearWarn($(\'#appUserAddOrupdateForm\'))');
                    return;
                }
            }

            appUserStrategy.userType = $('#userType').val();
            if ($('#appStart').val()==undefined||$('#appStart').val()==''){
                // swal("操作失败", "请选择统计开始时间", "error");
                $("#appUserAddOrupdateForm .form-group").children('.col-md-3.appStart').last().after(
                    '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请选择统计开始时间'+'</span></div>');
                $("#appUserAddOrupdateForm .form-group").children('.col-md-3.appStart').find('input').attr('onfocus', 'clearWarn($(\'#appUserAddOrupdateForm\'))');
                return;
            }
            if ($('#appEnd').val()==undefined||$('#appEnd').val()==''){
                // swal("操作失败", "请选择统计结束时间", "error");
                $("#appUserAddOrupdateForm .form-group").children('.col-md-3.appEnd').last().after(
                    '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请选择统计结束时间'+'</span></div>');
                $("#appUserAddOrupdateForm .form-group").children('.col-md-3.appEnd').find('input').attr('onfocus', 'clearWarn($(\'#appUserAddOrupdateForm\'))');
                return;
            }
            appUserStrategy.rStartTime = $('#appStart').val();
            appUserStrategy.rEndTime = $("#appEnd").val();

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
                        cleanAllInput();
                        $('#addAppUserModal').modal('hide');
                        swal(data.message);
                        initTableJs.refreshData();
                        loadSel('appUserMessageNo','/select/getAppUserPolicy',true);
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

    initClick: function () {
        $("#appUserAddCloseBut").on('click',function () {
            $("#appUserAddOrupdateForm .has-error").remove();
        })
    },

    init:function(){
        appUserAddModalJs.initSelector();
        appUserAddModalJs.addOrUpdateData();
        appUserAddModalJs.initClick();
    }

}

function initAppNameInput (row) {
    var appId = $("#appId").val();
    if (appId==0){
        $("#appUserAddOrupdateForm").find('input[name="appName"]').val(row.appName);
        $("#appUserAddOrupdateForm").find('input[name="appName"]').attr('readonly',false);
    } else {
        $("#appUserAddOrupdateForm").find('input[name="appName"]').val('');
        $("#appUserAddOrupdateForm").find('input[name="appName"]').attr('readonly',true);
    }
}

function cleanAllInput () {
    $("#messageNo").val('');
    $('#appUserAddOrupdateForm').find('input[name="messageName"]').val('');
    $('#appType').val('');
    $("#appId").val('');
    $("#appUserAddOrupdateForm").find('input[name="appName"]').val('');
    $("#appStart").val('');
    $("#appEnd").val('');
    $("#userType").val(0);
}
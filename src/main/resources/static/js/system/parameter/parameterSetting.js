// 记录新增还是修改操作
var add =true;
var set = false;

parameterSettingJs= {

    // 根据条件查询记录
    searchFormSubBut:function () {
        $("#searchFormButton").on('click', function(){
            initTableJs.refreshData();
        })
    },

    //弹出框新增或者
    addOrUpdateData:function(){
        $('#addSubmitBut').on('click', function() {
            var systemParameter = new Object();

            var configId = $("#configId").val();

            systemParameter.configDesc = $('#addOrupdateForm').find('input[name="configDesc"]').val();
            systemParameter.configKey = $('#addOrupdateForm').find('input[name="configKey"]').val();
            systemParameter.configValue = $('#addOrupdateForm').find('input[name="configValue"]').val();
            systemParameter.inputType = $('#addOrupdateForm').find('select[name="inputType"]').val();
            systemParameter.modelType = $('#addOrupdateForm').find('select[name="modelType"]').val();

            var input4Ok= true;
            if (systemParameter.inputType>=2){
                systemParameter.inputItems = $('#addOrupdateForm').find('textarea[name="inputItems"]').val();
                input4Ok = checkParameter4(systemParameter.inputItems);
                systemParameter.inputItems = systemParameter.inputItems.trim();
            }

            var input1Ok= checkParameter1(systemParameter.configDesc);
            var input2Ok= checkParameter2(systemParameter.configKey);
            var input3Ok= checkParameter3(systemParameter.configValue);

            var url = "";
            if ( add ) {
                url =  "/system/parameter/add.do";
            } else {
                if (set){
                    url = "/system/parameter/set.do";
                } else {
                    url = "/system/parameter/update.do";
                }
            }

            if (input1Ok && input2Ok && input3Ok && input4Ok) {
                $.ajax({
                    url: url,
                    type: 'POST',
                    data:{"configDesc":systemParameter.configDesc.trim(),"configKey":systemParameter.configKey.trim(),
                        "configValue":systemParameter.configValue.trim(),"inputType":systemParameter.inputType.trim(),
                        "inputItem":systemParameter.inputItems,"configId":configId,
                        "modelType":systemParameter.modelType},
                    success: function (data) {
                        if (data.result == 0) {
                            $('#myModaladd').modal('hide');
                            // revertTips();
                            if (add) {
                                swal("新增记录成功!");
                            } else {
                                swal("修改记录成功!");
                            }
                            initTableJs.refreshData();
                        } else {
                            swal("操作失败", data.message,"error");
                        }

                    },
                    error:function () {
                        // revertTips();
                        swal("操作失败", "取消新增/删除操作！", "error");

                    }

                })
            } else {
                swal("参数不合法，请重新输入参数!");
            }

        })
    },



    initSelector : function(){
        $('#operate').on("change",function(){
            clearWarn($('#operateDiv'));
        })
        $('#userGroupType').on("change",function(){
            clearWarn($('#usertypeDiv'));
        })

        $('#addOrupdateForm').find('select[name="inputType"]').on("change",function () {

            var inputType = this.value;
            $('#addOrupdateForm').find('textarea[name="inputItems"]').val('');
            if (inputType >= 2) {
                $("#inputItems").removeClass('hide');
                $("#inputItems").addClass('show');
                $("#inputItemsTipDiv").show();
            } else {
                $("#inputItems").removeClass('show');
                $("#inputItems").addClass('hide');
            }
        });

        // 单选框改变事件
        $(document).on('change','input[type=radio]',function() {
            updateConfigValue(this,this.value);
        });

        // 多选框改变事件
        $(document).on('click','input[type=checkbox]',function() {
            $(this).attr("checked", "checked");

            var configValue = "";
            $(this).parent().parent().find("input[type=checkbox]:checked").each(function () {
                //alert(this.value);
                configValue += this.value + ",";
            });
            configValue = (configValue.substring(configValue.length - 1) == ',') ? configValue.substring(0, configValue.length - 1) : configValue;
            updateConfigValue(this, configValue);
        });

    } ,

    // 校验输入参数
    validateParameter : function(){

        var configDescInput = $('#addOrupdateForm').find('input[name="configDesc"]');
        var configKeyInput = $('#addOrupdateForm').find('input[name="configKey"]');
        var configValueInput = $('#addOrupdateForm').find('input[name="configValue"]');

        var inputItemsInput = $('#addOrupdateForm').find('textarea[name="inputItems"]');

        //失去焦点
        configDescInput.blur(function(){
            var value = configDescInput.val();
            if (checkParameter1(value)) {
                $("#configDescTipDiv").hide();

            } else {
                $("#configDescTipDiv").show();
                $("#configDescTipDiv").addClass("has-error");
            }
        })

        //失去焦点
        configKeyInput.blur(function(){
            var value = configKeyInput.val();
            if (checkParameter2(value)) {
                $("#configKeyTipDiv").hide();

            } else {
                $("#configKeyTipDiv").show();
                $("#configKeyTipDiv").addClass("has-error");
            }
        })

        //失去焦点
        configValueInput.blur(function(){
            var value = configValueInput.val();
            if (checkParameter3(value)) {
                $("#configValueTipDiv").hide();

            } else {
                $("#configValueTipDiv").show();
                $("#configValueTipDiv").addClass("has-error");
            }
        })

        inputItemsInput.blur(function(){

            var value = inputItemsInput.val();
            if (checkParameter4(value)) {
                $("#inputItemsTipDiv").hide();

            } else {
                $("#inputItemsTipDiv").show();
                $("#inputItemsTipDiv").addClass("has-error");
            }
        })
    },


    init:function(){
        parameterSettingJs.initSelector();
        parameterSettingJs.searchFormSubBut();
        parameterSettingJs.addOrUpdateData();
        parameterSettingJs.validateParameter();
    }
}

// 只含有汉字、数字、字母、下划线_ * /，下划线位置不限
function checkParameter1(s){
    // var regu =/^[\u4e00-\u9fa5_*/()a-zA-Z0-9]{1,50}$/g;
    // var re = new RegExp(regu);
    // if (re.test(s)) {
    //     return true;
    // }else{
    //     return false;
    // }
    // var regu =/^[\u4e00-\u9fa5_*/()a-zA-Z0-9]{1,50}$/g;
    // var re = new RegExp(regu);
    if (s==undefined||s==''||s.trim()=='') {
        return false;
    }else{
        if (s.length>=1&&s.length<=50){
            return true;
        }
        return false;

    }
}

// 只含有汉字、数字、字母、下划线，下划线位置不限
function checkParameter2(s){
    // var regu =/^[\u4e00-\u9fa5._a-zA-Z0-9]{1,50}$/g;
    // var re = new RegExp(regu);
    // if (re.test(s)) {
    //     return true;
    // }else{
    //     return false;
    // }

    if (s==undefined||s==''||s.trim()=='') {
        return false;
    }else{
        if (s.length>=1&&s.length<=50){
            return true;
        }
        return false;

    }

}

function checkParameter3(s){
    var regu =/^.{0,300}$/g;
    var re = new RegExp(regu);
    if (re.test(s)) {
        return true;
    }else{
        return false;
    }
}

function checkParameter4(s){

    var regu =/^(.+,.+\n*)+$/g;
    var re = new RegExp(regu);
    if (re.test(s)) {
        return true;
    }else{
        return false;
    }
}

function revertTips(){
    $("#configDescTipDiv").show();
    $("#configKeyTipDiv").show();
    $("#configValueTipDiv").show();
}

$(document).ready(function() {
    initTableJs.initParam('/system/parameter/list.do','/system/parameter/delete.do',getColumnFunction)
    initTableJs.init2();
    parameterSettingJs.init();
    addRolePermissionForButton();
});

function addRolePermissionForButton() {
    if ($("#zf501002_add").val()!=1){
        $("#addButtonForRole").hide();
    }
    if ($("#zf501002_delete").val()!=1){
        $("#deleteButtonForRole").hide();
    }
}

function getColumnFunction(){

    var column =[
        {field: 'state',width:"5%",checkbox: true},
        {field: 'modelType',title: '所属模块',width:"20%",formatter:modelFormatter},
        {field: 'configDesc',title: '参数名称',width:"15%",formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'configKey',title: 'key值',width:"15%",formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'configValue',title: 'value值',width:"35%",formatter:valueFormatter},
        {field: 'operating',title:'操作',width:"10%",formatter:operatingFormatter, events:operatingEvents }
    ]
    return column;
}

function newAddButtonFunction(){
    add = true;

    $('#myModaladd').find("h5").text("新增");

    $("#configDescTipDiv").show();
    $("#configDescTipDiv").removeClass("has-error");
    $("#configKeyTipDiv").show();
    $("#configKeyTipDiv").removeClass("has-error");
    $("#configValueTipDiv").show();
    $("#configValueTipDiv").removeClass("has-error");

    $("#inputItemsTipDiv").removeClass("has-error");

    $('#addOrupdateForm').find('input[name="configDesc"]').val('');
    $('#addOrupdateForm').find('input[name="configKey"]').val('').attr('readOnly',false);
    $('#addOrupdateForm').find('input[name="configValue"]').val('');
    $('#addOrupdateForm').find('select[name="inputType"]').val('0');
    $('#addOrupdateForm').find('select[name="modelType"]').val('1');
    // $('#myModaladd').modal('show');

    //还原多行文本框
    $('#addOrupdateForm').find('textarea[name="inputItems"]').val('');
    $("#inputItems").removeClass('show');
    $("#inputItems").addClass('hide');

}

function deleteFunction(url,configId,configKey){

    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var configIds = new Array();
    var configKeys = new Array();
    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        configIds.push(item.configId);
        configKeys.push(item.configKey);
    }
    if (result.length==0){
        configIds.push(configId);
        configKeys.push(configKey);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{"configIds":configIds,"configKeys":configKeys},
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

function modifyButtonFunction(row) {
    set = true;
    add = false;
    $('#myModaladd').find("h5").text("修改");
    $('#myModaladd').modal('show');

    $("#configDescTipDiv").hide();
    $("#configKeyTipDiv").hide();
    $("#configValueTipDiv").hide();
    $("#inputItemsTipDiv").hide();

    $('#configId').val(row.configId);

    $('#addOrupdateForm').find('input[name="configDesc"]').val(row.configDesc);
    $('#addOrupdateForm').find('input[name="configKey"]').val(row.configKey).attr("readOnly",true);
    $('#addOrupdateForm').find('input[name="configValue"]').val(row.configValue);
    $('#addOrupdateForm').find('select[name="inputType"]').val(row.inputType);
    $('#addOrupdateForm').find('select[name="modelType"]').val(row.modelType);

    if(row.inputType >= 2){
        $("#inputItems").removeClass('hide');
        $("#inputItems").addClass('show');
        $('#addOrupdateForm').find('textarea[name="inputItems"]').val(row.inputItems);
        $("#inputItemsTipDiv").removeClass("has-error");
    } else {
        $("#inputItems").removeClass('show');
        $("#inputItems").addClass('hide');
    }

}

function detailButtonFunction(){

}

function queryParams(params){
    var data = $('#searchForm').formToJSON();
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    if(data.userGroupName ==''){
        data.userGroupName =undefined;
    }
    return data;
}

function updateConfigValue(row,value){
    // 是否是设置
    set = false;

    var configKey = $(row).parent().parent().find('input[name="configKey"]').val();
    var configValue = '';
    if (value==undefined){
        configValue = $(row).val();
    } else {
        configValue = value;
    }

    if (checkParameter3(configValue)){
        $.ajax({
            url: "/system/parameter/update.do",
            type: 'POST',
            data:{"configKey":configKey.trim(),"configValue":configValue.trim()},
            success: function (data) {
                if (data.result == 0) {
                    // $('#myModaladd').modal('hide');
                    // revertTips();
                    // swal("修改记录成功!");
                } else {
                    swal("操作失败", data.message,"error");
                }

            },
            error:function () {
                // revertTips();
                swal("操作失败", "取消修改操作！", "error");
            }
        })
    } else {
        swal("操作失败", "value值允许长度范围为1-300，请重新输入！", "error");
    }

}

// 格式化所属模块
function modelFormatter(value,row,index){
    var format = "";
    if (value==1){
        format = "系统参数";
    } else if (value==2){
        format = "告警参数";
    } else if (value==3){
        format = "radius-relay参数";
    } else if (value==4){
        format = "policy参数";
    }
    return "<span title='"+format+"'>"+format+"</span>";
}

function valueFormatter(value, row, index){

    var format = "";
    if (row.inputType==0){
        format += "<div class='col-md-12'>"
        format += "  <input class='hidden ' type='text'  name='configKey' value='"+row.configKey+"'/>";
        if ($("#zf501002_modify").val()==1){
            format += "  <input class='full-width' type='text' onchange='updateConfigValue(this)' name='configValue' value=' "+row.configValue+" '/>";
        } else {
            format += "  <input disabled='true' class='full-width' type='text' onchange='updateConfigValue(this)' name='configValue' value=' "+row.configValue+" '/>";
        }

        format += "</div >"
    } else if (row.inputType==1) {
        format += "<div class='col-md-12'>"
        format += "  <input class='hidden ' type='text'  name='configKey' value='"+row.configKey+"'/>";
        if ($("#zf501002_modify").val()==1) {
            format += "  <input class='full-width' type='password' onchange='updateConfigValue(this)' name='configValue' value=' " + row.configValue + " '/>";
        } else {
            format += "  <input disabled='true' class='full-width' type='password' onchange='updateConfigValue(this)' name='configValue' value=' " + row.configValue + " '/>";
        }
        format += "</div >"
    } else if (row.inputType==2){
        //下拉框
        format += "<div class='col-md-8'>"
        format += "  <input class='hidden full-width' type='text'  name='configKey' value='"+row.configKey+"'/>";
        if ($("#zf501002_modify").val()==1) {
            format += "   <select class='form-control ' onchange='updateConfigValue(this)'>";
        } else {
            format += "   <select disabled='disabled' class='form-control ' onchange='updateConfigValue(this)'>";
        }

        var inputItems = row.inputItems.trim();
        var inputArray = inputItems.split("\n");
        for (var i = 0; i < inputArray.length; i++) {
            if (inputArray[i].split(",")[0] == row.configValue.trim()) {
                format += "    <option selected='selected' value=' " + inputArray[i].split(",")[0] + " '>";
                format += inputArray[i].split(",")[1];
                format += "    </option>";
            } else {
                format += "    <option  value=' " + inputArray[i].split(",")[0] + " '>";
                format += inputArray[i].split(",")[1];
                format += "    </option>";
            }

        }


        format += "   </select>";
        format += "</div >"
    } else if (row.inputType==3) {
        //单选框
        format += "   <div class='col-md-10'>";
        format += "  <input class='hidden' type='text'  name='configKey' value='"+row.configKey+"'/>";
        var inputItems = row.inputItems.trim();
        var inputArray = inputItems.split("\n");
        var name = 'configValue' + Math.random();
        for (var i=0;i<inputArray.length;i++){
            format += "   <label class='checkbox-inline'>";
            if (inputArray[i].split(",")[0] == row.configValue.trim()){
                if ($("#zf501002_modify").val()==1) {
                    format += "     <input  type='radio' name=" + name + " value='" + inputArray[i].split(",")[0] + "' checked />";
                } else {
                    format += "     <input  type='radio' disabled='disabled' name=" + name + " value='" + inputArray[i].split(",")[0] + "' checked />";
                }
            } else {
                if ($("#zf501002_modify").val() == 1) {
                    format += "     <input  type='radio' name=" + name + " value='" + inputArray[i].split(",")[0] + "'/>";
                } else {
                    format += "     <input  type='radio' disabled='disabled' name=" + name + " value='" + inputArray[i].split(",")[0] + "'/>";
                }
            }
            format += inputArray[i].split(",")[1];
            format += "  </label>";
        }
        format += "</div>";
    } else if (row.inputType==4) {

        var checkedBox = row.configValue.trim();
        //多选框
        format += "   <div class='col-md-10'>";
        format += "  <input class='hidden' type='text'  name='configKey' value='"+row.configKey+"'/>";
        var inputItems = row.inputItems.trim();
        var inputArray = inputItems.split("\n");

        var name = 'configValue' + Math.random();
        for (var i=0;i<inputArray.length;i++){
            format += "   <label class='radio-inline'>";
            if (checkedBox.indexOf(inputArray[i].split(",")[0])>=0){
                if ($("#zf501002_modify").val() == 1) {
                    format += "     <input  type='checkbox'  name="+name+" value='"+inputArray[i].split(",")[0]+"' checked/>";
                } else {
                    format += "     <input  type='checkbox' disabled='disabled' name="+name+" value='"+inputArray[i].split(",")[0]+"' checked/>";
                }

            } else {
                if ($("#zf501002_modify").val() == 1) {
                    format += "     <input  type='checkbox' name=" + name + "  value='" + inputArray[i].split(",")[0] + "'/>";
                } else {
                    format += "     <input  type='checkbox' disabled='disabled' name=" + name + "  value='" + inputArray[i].split(",")[0] + "'/>";
                }
            }
            format += inputArray[i].split(",")[1];
            format += "  </label>";
        }

        format += "</div>";

    }

    return format;
}

//样式格式化
function operatingFormatter(value, row, index){
    var format="";
        if ($("#zf501002_set").val()==1){
            format += "<a href='#' title='设置' class='m-r'><i class='fa fa-gear fa-lg edit'></i></a>";
        } else {
            format += "<a href='#' style='display: none' title='设置' class='m-r'><i class='fa fa-gear fa-lg edit'></i></a>";
        }
        if ($("#zf501002_delete").val()==1){
            format += "<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
        } else {
            format += "<a href='#' style='display: none' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
        }
    return format;
}

//点击事件
window.operatingEvents = {

    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $('#myModaladd').find("h4").text("修改");
        $('#addSubmitBut').show();
        $('#addOrupdateForm').find('input[name="configKey"]').attr("readOnly",true);
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
                deleteFunction('/system/parameter/delete.do',row.configId,row.configKey);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}
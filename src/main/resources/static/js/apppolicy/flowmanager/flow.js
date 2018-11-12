FlowManager = {

    initButton:function () {
        $('#newAddGroup').click(function () {
            document.getElementById("createUserGroupForm").reset();
            $('#add-usergroup-snippet').modal('show');
            $('#operate').val(0);
            $('#operateDiv').hide();
        });
        $('#addSubmitBut').click(function () {
            var formData = $('#addOrupdateForm').formToJSON();

            if(formData.appFlowId!=""){
                //修改
                insertOrUpdate('/flowmanager/update',formData);
            }else {
                //新增
                insertOrUpdate('/flowmanager/insert',formData);
            }
            function insertOrUpdate(URL,formData) {
                clearWarn($('#addOrupdateForm'));
                if(formData.messageName=="" || formData.messageName.length>50){
                    warn('messageNameDiv','请输入策略名称，并且不超过50个字符');
                    return false;
                }
                if(formData.apptype==''){
                    warnSelect('apptypeDIV');
                    return false;
                }
                if(formData.appid==''){
                    warnSelect('appidDIV');
                    return false;
                }else {
                    if(formData.appid==0){
                        if(formData.appname==''){
                            warn('appnameDIV','输入自定义的应用名称');
                            return false;
                        }
                    }
                }
                if(!isNumber(formData.appThresholdUpAbs) ){
                    clearWarn($('#appThresholdUpAbsDIV'));
                    $('#appThresholdUpAbsDIV').children('.col-md-1').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i> 请输入数值</span></div>');
                    $('#appThresholdUpAbsDIV').find('input').attr('onfocus', 'clearWarn($(\'#appThresholdUpAbsDIV\'))');
                    return false;
                }
                if(formData.appThresholdUpAbs>Math.pow(2,32)){
                    clearWarn($('#appThresholdUpAbsDIV'));
                    $('#appThresholdUpAbsDIV').children('.col-md-1').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i> 请输入数值小于2^32</span></div>');
                    $('#appThresholdUpAbsDIV').find('input').attr('onfocus', 'clearWarn($(\'#appThresholdUpAbsDIV\'))');
                    return false;
                }
                if(!isNumber(formData.appThresholdDnAbs) ){
                    clearWarn($('#appThresholdDnAbsDIV'));
                    $('#appThresholdDnAbsDIV').children('.col-md-1').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i> 请输入数值</span></div>');
                    $('#appThresholdDnAbsDIV').find('input').attr('onfocus', 'clearWarn($(\'#appThresholdDnAbsDIV\'))');
                    return false;
                }
                if(formData.appThresholdDnAbs > Math.pow(2,32) ){
                    clearWarn($('#appThresholdDnAbsDIV'));
                    $('#appThresholdDnAbsDIV').children('.col-md-1').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i> 请输入数值小于2^32</span></div>');
                    $('#appThresholdDnAbsDIV').find('input').attr('onfocus', 'clearWarn($(\'#appThresholdDnAbsDIV\'))');
                    return false;
                }
                var timeBarValue = getTimeBarValue("addOrupdateForm");
                if(timeBarValue.size==0){
                    swal("请选择管理时间");
                    return false;
                }
                if(timeBarValue.size>4){
                    swal("时间段数不能超过4个");
                    return false;
                }
                formData.timeBar = timeBarValue.value;

                if(formData.startTime==undefined){
                    formData.startTime = $('#start').val();
                }
                if(formData.endTime==undefined){
                    formData.endTime = $('#end').val();
                }

                if(formData.userType == 1 || formData.userType == 2){
                    var names = new Array();
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
                    formData.puserGroup = formData.puserGroup.split(",");
                    formData.userName = undefined;
                }else {
                    formData.puserGroup = undefined;
                    formData.userName = undefined;
                }

                $.ajax({
                    url: URL,
                    data: JSON.stringify(formData),
                    type: 'POST',
                    async: false,
                    dataType: 'json',
                    contentType: "application/json",
                    success: function (data) {
                        var title = "";
                        if (data != null) {
                            title = data.message;
                        } else {
                            title ="操作成功";
                        }
                        swal({
                            title: title,
                            text: '',
                            html: true
                        }, function (isConfirm) {
                            $('#myModaladd').modal('hide');
                            initTableJs.refreshData();
                        });
                    },
                    error:function () {
                        swal("失败，请刷新页面重试");
                    }

                })
            }

        });
        $('#searchFormButton').click(function () {
            initTableJs.refreshData();
        })
    },
    initSelect:function () {
        $('#apptype').change(function () {
            clearWarnSelect("apptypeDIV");
            var appType = this.value;
            if(appType!=''){
                $('#appid').empty();
                loadSel('appid','/select/getAppByType',true,{appType:appType},true,undefined);
            }else {
                $('#appid').empty().append('<option value="">请选择</option>');
            }
        });
        $('#appid').change(function () {
            clearWarnSelect("appidDIV");
            clearWarn($('#appnameDIV'));
            var appid = this.value;
            var appTxt ;
            if(appid!=''){
                var node = this.childNodes;
                for (var i =0 ;i<node.length;i++){
                    if(node[i].selected == true){
                        appTxt =node[i].text;
                        break;
                    }
                }
                if(appid == 0){
                    $('#appnameDIV').find('input[name="appname"]').attr('disabled',false);
                }else {
                    $('#appnameDIV').find('input[name="appname"]').val("").attr('disabled',true);
                }
            }else {
                $('#appnameDIV').find('input[name="appname"]').val("").attr('disabled',true);
            }
        });
        $("#addPlush").click(function() {
            var inputPlus = "<div class='form-group newInput' ><label class='col-md-2 control-label p-n'></label>"
                +"<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
                +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
                +"</div>";
            $(this).parent().parent().parent().after(inputPlus);
            $(".check-minus").click(function() {
                $(this).parent().parent().parent().remove()
            });
        });
        $('#userType').change(function () {
            clearWarn($('#addOrupdateForm'));
            var userType = this.value;
            $('#addOrupdateForm').find('input[name="userName"]').val('');
            if(userType == 0){
                $('#userNameDiv').hide();
                $('#puserGroup').hide();
                $('#userNameDiv .newInput').remove();
            }else if(userType == 1 || userType == 2){
                $('#userNameDiv').show();
                $('#puserGroup').hide();
                $('#userNameDiv .newInput').remove();
            }else if(userType == 3){
                $('#userNameDiv').hide();
                $('#puserGroup').show();
                $('#userNameDiv .newInput').remove();
                loadUserGroup();
            }

        });
    },
    init:function () {
        FlowManager.initSelect();
        FlowManager.initButton();
        addUserGroupJs.createUserGroup();
        addUserGroupJs.exportTemplate();
    }
};

$(document).ready(function() {
    timeBarInit();
    initDate(3,false);
    loadSel('apptype','/select/getAppType',true,undefined,false);
    FlowManager.init();
    initTableJs.initParam('/flowmanager/getList','/flowmanager/delete.do',getColumnFunction)
    initTableJs.init2();
    // 初始化查询时间条
    initDefinedEleIdDate(3,false,false,"searchStart","searchEnd");
});
//表头
function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'messageNo',title: '策略ID',formatter:function (value, row, index) {
            if(row.messageNo==""  || row.messageNo==undefined){
                return "-";
            }else {
                return row.messageNo;
            }
        }},
        {field: 'messageName',title: '<span title="策略名称">策略名称</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'apptypeDesc',title: '<span title="应用类型">应用类型</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'appThresholdUpAbs',title: '<span title="流量门限上行（Kbps）">流量门限上行（Kbps）</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'appThresholdDnAbs',title: '<span title="流量门限下行（Kbps）">流量门限下行（Kbps）</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'policyCount',title: '<span title="策略成功数/异常数">策略成功数/异常数</span>',formatter:function (value, row, index) {
            if(value=="0/0"){
                return "0/0";
            }else {
                return "<a href='#' onclick='getDetail("+index+",0)' title='详情'>"+value+"</a>";
            }
        }},
        {field: 'bindPolicyCount',title: '<span title="绑定成功数/异常数">绑定成功数/异常数</span>',formatter:function (value, row, index){
            if(value=="0/0"){
                return "0/0";
            }else {
                return "<a href='#' onclick='getDetail("+index+",1)' title='详情'>"+value+"</a>";
            }
        }},
        {field: 'startTime',title: '<span title="策略开始时间">策略开始时间</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'endTime',title: '<span title="策略结束时间">策略结束时间</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'createTime',title: '<span title="创建时间">创建时间</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
         {field: 'modifyOper',title: '<span title="操作账号">操作账号</span>',visible:false,formatter:function(value,row,index){
 			return "<span title='"+value+"'>"+value+"</span>";
 		}},
        {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
    ];
    return column;
}
//删除
function deleteFunction(url,data){
    var flows = [];
    if(data==undefined){
        let result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
        for(let i = 0; i < result.length; i++) {
            let item = result[i];
            // $('#table>tbody>tr').remove('.selected');
            flows.push(item.appFlowId);
        }
    }else {
        flows.push(data);
    }

    $.ajax({
        url: url,
        type: 'POST',
        data:{flows:flows},
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
//点击+追加input框,编辑根据userName 个数回填
function addInputEvent() {
    var inputPlus = "<div class='form-group newInput' ><label class='col-md-2 control-label p-n'></label>"
        +"<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
        +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
        +"</div>";
    $("#userNameDiv .form-group").last().after(inputPlus);
    $(".check-minus").click(function() {
        $(this).parent().parent().parent().remove();
    });
}
//新增
function newAddButtonFunction(){
    $('#addSubmitBut').show();
    $('#userName').hide();
    $('#puserGroup').hide();
    document.getElementById("addOrupdateForm").reset();
    $('#addOrupdateForm').find('input[type="hidden"]').val('');
    $('#appid').children().remove();
    $('#addOrupdateForm .has-error').remove();
    initTimeBar('timeBar');
    $('#start').attr("disabled",false);
    $('#end').attr("disabled",false);
    resetInput();
    $('#title').text("新增");
}
//改
function modifyButtonFunction(row) {
    document.getElementById("addOrupdateForm").reset();
    $('#myModaladd').modal('show');
    $('#addOrupdateForm').find('input[name="appFlowId"]').val(row.appFlowId);
    $('#addOrupdateForm').find('input[name="messageNo"]').val(row.messageNo);
    $('#addOrupdateForm').find('input[name="messageName"]').val(row.messageName);
    $('#apptype').val(row.apptype);
    loadSel('appid','/select/getAppByType',true,{appType:row.apptype},true,undefined);
    if(row.appid==0){
        $('#appid').val(row.appid);
        $('#addOrupdateForm').find('input[name="appname"]').val(row.appname).attr('disabled',false);
    }else {
        $('#appid').val(row.appid);
        $('#addOrupdateForm').find('input[name="appname"]').val("").attr('disabled',true);
    }
    $('#addOrupdateForm').find('input[name="appThresholdUpAbs"]').val(row.appThresholdUpAbs);
    $('#addOrupdateForm').find('input[name="appThresholdDnAbs"]').val(row.appThresholdDnAbs);
    $('#start').val(row.startTime).attr("disabled",true);
    $('#end').val(row.endTime).attr("disabled",true);

    initTimeBar('timeBar');

    var timeBar = row.timeBar;
    var j = 0;
    for(var i=timeBar.length-1;i>=0;i--){
        if(timeBar[i]=='1'){
            $("#addOrupdateForm .timeul").find("li:eq("+j+")").find("input").attr('class', 'active');
        }
        j++;
    }
    var userType = row.userType;
    clearWarn($('#addOrupdateForm'));
    $('#addOrupdateForm').find('input[name="userName"]').val('');
    if(userType == 0){
        $('#userNameDiv').hide();
        $('#puserGroup').hide();
        $('#userNameDiv .newInput').remove();
        $('#userType').val(0)
    }else if(userType == 1 || userType == 2){
        $('#userNameDiv').show();
        $('#puserGroup').hide();
        $('#userNameDiv .newInput').remove();
        $('#userType').val(userType);
        var userNames = row.userName;
        for (var i =0;i<userNames.length;i++){
            if(i==0){
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(userNames[i]);
            }else {
                addInputEvent();
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(userNames[i]);
            }
        }
    }else if(userType == 3){
        $('#userNameDiv').hide();
        $('#puserGroup').show();
        $('#userNameDiv .newInput').remove();
        $('#userType').val(userType);
        loadSelectPicker('puserGroupid','/select/getUserGroup',false,row.puserGroup);
    }

}
//表格参数
function queryParams(params){
    var data = $('#searchForm').formToJSON();
    if(data.messageName ==''){
        data.messageName =undefined;
    }
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
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
		if(row.activeFlag == true){
	        format +="<a href='#' title='重发' class='m-r'><i class='fa  fal fa-share resend'></i></a>";
	    }
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
        $.ajax({
            url: '/flowmanager/resend',
            data: JSON.stringify(row),
            type: 'POST',
            async: false,
            contentType: "application/json",
            success: function (data) {
                swal(data.message)
            },
            error:function () {
                swal("重发失败")
            }
        });

    },
    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $('#title').text("修改");
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
                deleteFunction('/flowmanager/delete.do',row.appFlowId);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
};


function loadUserGroup() {
    loadSelectPicker('puserGroupid','/select/getUserGroup',false);
}

function customFunction() {
    loadUserGroup();
}
function clearWarn($secion){
    $secion.children('.has-error').remove();
    $secion.find('.has-error').remove();
}

function resetInput() {
    document.getElementById("addOrupdateForm").reset();
    $('#addOrupdateForm').find('input[type="hidden"]').val('');
    resetUserOption();
}

function resetUserOption() {
    $('#userType').val(0);
    $('#userNameDiv').hide();
    $('#puserGroup').hide();
    $('#userNameDiv .newInput').remove()
}

function getDetail(index,tab){
    var flow = $("#table").bootstrapTable('getData');
    var redo = $("#redoFlag").val();
    if(redo==1 && flow[index].activeFlag==true){
    	PolicyDetail.showDetail(flow[index].messageNo,messageType.GENERAL_FLOW_POLICY.value,2,1,tab);
    }else{
    	PolicyDetail.showDetail(flow[index].messageNo,messageType.GENERAL_FLOW_POLICY.value,2,0,tab);    	
    }
}

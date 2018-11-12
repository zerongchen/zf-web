var webpush ={};

// 根据条件查询记录
function searchFormSubBut() {
    $("#searchFormButton").on('click', function(){
        initTableJs.refreshData();
    })
};
function queryParams(params){
    var data = $('#searchForm').formToJSON();
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    if(data.messageName==''){
        data.messageName = undefined;
    }
    if (data.startTime==''){
        data.startTime = undefined;
    }
    if (data.endTime==''){
        data.endTime = undefined;
    }
    return data;
}

$(document).ready(function() {

    initSearchDate(3,false);

    initTableJs.initParam('/apppolicy/webpush/list','/apppolicy/webpush/delete',getColumnFunction)
    initTableJs.init2();

    //弹出框确定按钮单击事件
    addOrUpdateData();
    //查询按钮事件。
    searchFormSubBut();
});

//弹出框确定按钮单击事件
function addOrUpdateData(){
    $('#addSubmitBut').on('click', function() {

        var formData = $('#addOrupdateForm').formToJSON();

        var url = "";
        if( (formData.advId==undefined||formData.advId=='')
            && (formData.messageNo==undefined||formData.messageNo=='') ){
            url = '/apppolicy/webpush/add';
        } else {
            url = '/apppolicy/webpush/update';
        }

        // 检查所有参数
        if (checkAllParameter(formData)){

            filterParameter();
            $.ajax({
                url: url,
                type: 'POST',
                dataType:'json',
                //contentType: 'application/json',
                // data:JSON.stringify(formData),
                data:formData,
                success: function (data) {
                    if (data.result==0){
                        $("#messageNo").val('');
                        cleanAllInput();
                        $('#myModaladd').modal('hide');
                        swal(data.message);
                        initTableJs.refreshData();
                    } else {
                        swal("操作失败", "存在策略名称相同数据", "error");
                    }

                },
                error:function () {
                    swal("操作失败", "取消新增/删除操作！", "error");
                }

            })
        }
    });
};

// 参数合法性校验
function checkAllParameter(formData) {

    webpush.advId = formData.advId;
    webpush.messageNo = formData.messageNo
    webpush.messageName = formData.messageName
    webpush.advType = formData.advType
    webpush.advWhiteHostListId = formData.advWhiteHostListId
    webpush.triggerHostListId = formData.triggerHostListId
    webpush.triggerKwListId = formData.triggerKwListId
    webpush.advFramDPIrl = formData.advFramDPIrl
    webpush.advToken = formData.advToken
    webpush.advDelay = formData.advDelay
    webpush.rStartTime = formData.start;
    webpush.rEndTime = formData.end;

    if (webpush.messageName==undefined||webpush.messageName==''){
        swal("操作失败", "请输入策略名称", "error");
        return false;
    }
    if (webpush.advType==undefined||webpush.advType==''){
        swal("操作失败", "推送信息类型", "error");
        return false;
    }
    if (webpush.advFramDPIrl==undefined||webpush.advFramDPIrl==''){
        swal("推送的URL", "推送的URL不能为空", "error");
        return false;
    }
    if (webpush.advToken==undefined||webpush.advToken==''){
        swal("推送配额", "推送配额不能为空", "error");
        return false;
    }
    if (webpush.advToken < 1||webpush.advToken>255){
        swal("推送配额", "取值1-255之间", "error");
        return false;
    }
    if (webpush.advDelay==undefined||webpush.advDelay==''){
        swal("推送延时", "推送延时不能为空", "error");
        return false;
    }
    if (webpush.advDelay < 0||webpush.advDelay>255){
        swal("推送延时", "取值0-255之间", "error");
        return false;
    }
    return true;
}

// 保存前对参数进行过滤  去掉为设值的参数
function filterParameter(){
    if (webpush.advId==''){
        webpush.advId = undefined;
    }
    if (webpush.messageNo==''){
        webpush.messageNo = undefined;
    }
    if (webpush.rStartTime==''){
        var now = new Date();
        var year = now.getFullYear();
        var month = now.getMonth()+1;
        var day = now.getDate();
        webpush.rStartTime = new Date(year+"-"+month+"-"+day+" 00:00:00").getTime()/1000;
    } else {
        webpush.rStartTime = new Date(webpush.rStartTime+" 00:00:00").getTime()/1000;
    }
    if (webpush.rEndTime==''){
        webpush.rEndTime = 0;
    } else {
        webpush.rEndTime = new Date(webpush.rEndTime+" 00:00:00").getTime()/1000;
    }
}
// 推送类型改变时绑定改变事件
$('#advType').change(function () {
    clearWarn($('#addOrupdateForm'));
    var advType = this.value;
    if(advType == "0"){
        showNone();
    }else if(advType == "1"){
        showWhiteLisList();
        loadWhiteHostList();
    }else if(advType == "2"){
        showWhiteLisList();
        loadWhiteHostList();
    }else if(advType == "3"){
        showHostLisList();
        loadHostList();
    }else if(advType == "4"){
        showWhiteTriggerKw();
        loadWhiteHostList();
        loadKwList();
    }else{
        showNone();
        loadWhiteHostList();
    }
});
//推送白名单的div
function showWhiteLisList() {
    $('#d_advWhiteHostListId').show();
    $('#d_triggerHostListId').hide();
    $('#d_triggerKwListId').hide();
}

//推送触发网站的div
function showHostLisList() {
    $('#d_advWhiteHostListId').hide();
    $('#d_triggerHostListId').show();
    $('#d_triggerKwListId').hide();
}
//推送网站白名单,触发关键字显示的div
function showWhiteTriggerKw() {
    $('#d_advWhiteHostListId').show();
    $('#d_triggerHostListId').hide();
    $('#d_triggerKwListId').show();
}
//
function showNone() {
    $('#d_advWhiteHostListId').show();
    $('#d_triggerHostListId').hide();
    $('#d_triggerKwListId').hide();
    $('#d_triggerHostListId .selectpicker').selectpicker('refresh');
    $('#d_triggerKwListId .selectpicker').selectpicker('refresh');
}

// 白名单
function loadWhiteHostList() {
    var data = {infoType:1};
    loadSelectPicker1('advWhiteHostListId','/infoLibrary/getTriggerHost',JSON.stringify(data),true,'','POST');
}
// 触发网站
function loadHostList() {
    loadSelectPicker2('triggerHostListId','/infoLibrary/getTriggerHost',{infoType:2},false,'','POST');
}
// 触发关键字
function loadKwList() {
    loadSelectPickerKW('triggerKwListId','/infoLibrary/getTriggerKW',{},false,'','POST');
}



// 删除
function deleteFunction(url){

    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var messageNos = new Array();
    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        messageNos.push(item.messageNo);
    }
    $.ajax({
        url: url,
        type: 'POST',
    //    data:JSON.stringify({"messageNos":messageNos}),
        data:{"messageNos":messageNos},
        success: function (data) {
        	if(data.result==0){
        		swal("删除成功！", "已经永久删除了这条记录。", "success");
                $("#table>tbody>.hidetr").remove();
                initTableJs.refreshData();
        	}else if(data.result==1){
        		  swal("删除失败", data.message, "error");
        	}
        },
        error:function () {
            swal("删除失败", "取消了删除操作！", "error");
        }
    })
}

function deleteRow(index){
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
        	var result = $('#table').bootstrapTable('getData'); //获取表格的所有内容行
            var messageNos = new Array();
            messageNos.push(result[index].messageNo);
            $.ajax({
                url: '/apppolicy/webpush/delete',
                type: 'POST',
                data:{"messageNos":messageNos},
                success: function (data) {
                	if(data.result==0){
                		swal("删除成功！", "已经永久删除了这条记录。", "success");
                        $("#table>tbody>.hidetr").remove();
                        initTableJs.refreshData();
                	}else if(data.result==1){
                		  swal("删除失败", data.message, "error");
                	}
                },
                error:function () {
                    swal("删除失败", "取消了删除操作！", "error");
                }
            });
        } else {
            swal("已取消", "取消了删除操作！", "error")
        }
    });
}


/**
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

function newAddButtonFunction(){
    cleanAllInput();
}
// 设置参数值
function cleanAllInput(){
    $('#messageNo').val('');
    $('#advId').val('');
    $('#messageName').val('');
    $("#advType").val("");

    $("#advWhiteHostListId").empty();
    $("#triggerHostListId").empty();
    $("#triggerKwListId").empty();

    $("#advFramDPIrl").val("");
    $("#advToken").val("");
    $("#advDelay").val("");
    $("#start").val("");
    $("#end").val("");
    showNone();
}
function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'messageNo',title: '策略ID',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'advId',title: '<span title="推送ID">推送ID</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'messageName',title: '<span title="策略名称">策略名称</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'advType',title: '<span title="推送类型">推送类型</span>',
            formatter:function(value,row,index){
            	return "<span title='"+advTypeObject[value.toString()]+"'>"+advTypeObject[value.toString()]+"</span>";
            }
        },
        {field:'webPushPolicy',title:'<span title="策略成功数/异常数">策略成功数/异常数</span>',
            formatter:function(value,row,index){
                if(value=="0/0"){
                    return "0/0";
                }else{
                    return "<a href='#' onclick='getDetail("+index+")' title='详情'>"+value+"</a>";
                }
            }
        },
        {field: 'createTime',title: '<span title="创建时间">创建时间</span>',
            formatter : function (value, row, index) {
                return "<span title='"+timestamp2Time(row['createTime'],'-')+"'>"+timestamp2Time(row['createTime'],'-')+"</span>";
            }
        },

        {field: 'createOper',title: '<span title="操作账号">操作账号</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'operating',title:'操作',formatter:operatingFormatter,events:operatingEvents }
    ]
    return column;
}

//样式格式化
function operatingFormatter(value, row, index){
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
		format+="<a href='#' title='删除' onclick='deleteRow("+index+")'><i class='fa fa-close fa-lg'></i></a>";
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
                deleteFunction('/apppolicy/webpush/delete');
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}

// 修改
function modifyButtonFunction(row) {
    $('#myModaladd').modal('show');
    revertProperties(row);
}
// 获取所有参数值
function revertProperties(row){
    $('#messageNo').val(row.messageNo);
    $('#advId').val(row.advId);
    $('#messageName').val(row.messageName);
    var advType = row.advType;

    var selects = $("#addOrupdateForm").find('#advType');
    for(var i=0;i<selects[0].options.length;i++){
        if (selects[0].options[i].value == advType){
            selects[0].options[i].selected = true;
            break;
        }
    }
    if(advType==1){
        loadSelectPicker1('advWhiteHostListId','/infoLibrary/getTriggerHost',{infoType:1},true,row.advWhiteHostListId,'POST');
    }else if(advType==2){
        loadSelectPicker1('advWhiteHostListId','/infoLibrary/getTriggerHost',{infoType:1},true,row.advWhiteHostListId,'POST');
    }else if(advType==3){
        showHostLisList();
        loadSelectPicker2('triggerHostListId','/infoLibrary/getTriggerHost',{infoType:2},false,row.triggerHostListId.split(","),'POST');
    }else if(advType==4){
        showWhiteTriggerKw();
        loadSelectPicker1('advWhiteHostListId','/infoLibrary/getTriggerHost',{infoType:1},true,row.advWhiteHostListId,'POST');
        loadSelectPickerKW('triggerKwListId','/infoLibrary/getTriggerKW',{infoType:1},false,row.triggerKwListId.split(","),'POST');
    }
    $('#advFramDPIrl').val(row.advFramDPIrl);
    $('#advToken').val(row.advToken);
    $('#advDelay').val(row.advDelay);
}

var advTypeObject={
    "1":"电信自营应用",
    "2":"主动推送",
    "3":"Host触发",
    "4": "关键字触发",
}

/**
 * 加载下拉框 bootstrap-select
 * selId 下拉框id
 * url 请求地址
 * flag 特殊定义
 * val 默认选中项值
 */
function loadSelectPicker1(selId, url,datav, flag, val,methodType){
    $.ajax({
        url: url,
        data:datav,
        type: methodType,
        async: false,
        dataType: 'json',
        success: function(data){
            var data = eval(data);
            var $selId = $('#' + selId),
                option = '';
            if(flag){
                option+='<option value=0">请选择</option>'
            }
            $.each(data, function (i,n) {
                option += '<option value="' + n.triggerHostListid + '">' + n.triggerHostListname + '</option>'
            });
            $selId.children().remove();
            $selId.append(option);
            for(var i=0;i<$selId[0].options.length;i++){
                if ($selId[0].options[i].value == val){
                    $selId[0].options[i].selected = true;
                    break;
                }
            }
            /*if(val!=undefined){
                $('#' + selId)[0].selected=true;//默认选中
            }*/
        }
    });
}

/**
 * 加载下拉框 bootstrap-select
 * selId 下拉框id
 * url 请求地址
 * flag 特殊定义
 * val 默认选中项值
 */
function loadSelectPicker2(selId, url,datav, flag, val,methodType){
    $.ajax({
        url: url,
        data:datav,
        type: methodType,
        async: false,
        dataType: 'json',
        success: function(data){
            var data = eval(data);
            var $selId = $('#' + selId),
                option = '';
            $.each(data, function (i,n) {
                option += '<option value="' + n.triggerHostListid + '">' + n.triggerHostListname + '</option>'
            });
            $selId.children().remove();
            $selId.append(option);
            $('#' + selId+".selectpicker").selectpicker({
                style: 'btn-boostrap-sl'
            });
            $('#' + selId+".selectpicker").selectpicker('refresh');
            if(val!=undefined){
                $('#' + selId+".selectpicker").selectpicker('val', val);//默认选中
            }

        }
    });
}

/**
 * 加载下拉框 bootstrap-select
 * selId 下拉框id
 * url 请求地址
 * flag 特殊定义
 * val 默认选中项值
 */
function loadSelectPickerKW(selId, url,datab, flag, val,methodType){
    $.ajax({
        url: url,
        data:datab,
        type: methodType,
        async: false,
        dataType: 'json',
        success: function(data){
            var data = eval(data);
            var $selId = $('#' + selId),
                option = '';
            $.each(data, function (i,n) {
                option += '<option value="' + n.triggerKwListid + '">' + n.triggerKwListname + '</option>'
            });
            $selId.children().remove();
            $selId.append(option);
            $('#' + selId+".selectpicker").selectpicker({
                style: 'btn-boostrap-sl'
            });
            $('#' + selId+".selectpicker").selectpicker('refresh');
            if(val!=undefined){
                $('#' + selId+".selectpicker").selectpicker('val', val);//默认选中
            }

        }
    });
}
$("#adddismissBut").click(function() {
    cleanAllInput();
})


// 去掉字符串最后一个逗号
function removeLastComma(str){
    str=(str.substring(str.length-1)==',')?str.substring(0,str.length-1):str;
    return str;
}

// 去掉字符串第一个逗号
function removeFirstComma(str){
    str=(str.substring(0)==',')?str.substring(1,str.length):str;
    return str;
}

function getDetail(index){
    var webFlow = $("#table").bootstrapTable('getData');
	var redo = $("#redoFlag").val();
    if(redo==1){
    	PolicyDetail.showDetail(webFlow[index].messageNo,65,1,1);
    }else{
    	PolicyDetail.showDetail(webFlow[index].messageNo,65,1,0);    	
    }
}

// 重发webpush流量管理策略
function resendPolicy(row){
    $.ajax({
        url: "/apppolicy/webpush/resend",
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

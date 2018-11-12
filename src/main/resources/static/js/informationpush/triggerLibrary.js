var clickWS = 0;
var clickKW = 0;
var clickWL = 0;
var detailAddCount = 0; //计数，用于新增或者修改,两者只能进行其中之一
var data_url;
var data_list_url ;
var arg_type ;
var sectionid;

infolibrary={

    initPara:function(id,url,list_url,arg){
        sectionid=id;
        data_url = url;
        data_list_url = list_url;
        arg_type = arg;
    },

    refreshInfoData:function(){
        $('#'+sectionid).bootstrapTable('refresh', { url: data_url});
    },
    refreshInfolISTData:function(){
            $('#triggerDetailTable').bootstrapTable('refresh', { url: data_list_url});
    },

    //Host KW WL
    initInfoTable: function(){
        $('#'+sectionid).bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: data_url,
            queryParams: QueryParams(arg_type),
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            // showRefresh: !0,
            showColumns: !0,
            toolbar: "#commonButton",
            pagination: true,
            sidePagination: 'client', //分页方式：client客户端分页，server服务端分页（*）
            iconSize: "outline",
            icons: {
                columns: "glyphicon-list",
            },
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn(arg_type)
        });

        $("#info_ws div.bars.pull-right").hide();
        $("#info_kw div.bars.pull-right").hide();
        $("#info_wl div.bars.pull-right").hide();


    },

    // List
    initInfoListTable: function(){
        $('#triggerDetailTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: data_list_url,
            queryParams: listQueryParams,
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            // showRefresh: !0,
            // showToggle: !0,
            showColumns: !0,
            toolbar: "#commonButton",
            pagination: true,
            sidePagination: 'server', //分页方式：client客户端分页，server服务端分页（*）
            iconSize: "outline",
            icons: {
                columns: "glyphicon-list",
            },
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn(arg_type+"_LIST")
        });
    },

    minusListButton : function () {
        $(document).on('click',".check-minus",function () {
            // $(this).parent().parent().parent().remove();
            $('#triggerDetailTable>tbody>tr:first').remove();
            detailAddCount -= 1;
        })
    },

    initClick:function () {
    	//查询条件时间控件初始化
    	initSearchDate(3,false);
        $('#ws_click').on('click', function () {
            wsClickFunction();
            clickWS++;
        })
        $('#kw_click').on('click', function () {
            kwClickFunction();
            clickKW++;
        })
        $('#wl_click').on('click', function () {
            wlClickFunction();
            clickWL++;
        })
        $('#importFormButton').on('click', function () {
            upfile();
        })
        loadSel('listID', '/trigger/getWSOption', true);
        $('#searchformbutton').on('click', function () {
            searchformbuttonFunc();
        })
        $('#operate_button .import').click(function () {
            clearText();
        })
    }
    ,
    //列表页面点击导出
    initExportButton:function () {
        $('#exportData').on('click',function () {

            var type = $('#paramForm').find("input[name='infoType']").val();
            var id ,name,messageNo;
            var result;
            if(type == 1 ){
                result = $('#info_ws_table').bootstrapTable('getSelections');
                if(result.length!=1){
                    swal("请选择一条数据");
                    return false;
                }
                id = result[0].triggerHostListid;
                name = result[0].triggerHostListname;
                messageNo = result[0].messageNo;
            }else if(type == 2){
                result = $('#info_wl_table').bootstrapTable('getSelections');
                if(result.length!=1){
                    swal("请选择一条数据");
                    return false;
                }
                id = result[0].triggerHostListid;
                name = result[0].triggerHostListname;
                messageNo = result[0].messageNo;
            }
            else if(type == 0){
                result = $('#info_kw_table').bootstrapTable('getSelections');
                if(result.length!=1){
                    swal("请选择一条数据");
                    return false;
                }
                id = result[0].triggerKwListid;
                name = result[0].triggerKwListname;
                messageNo = result[0].messageNo;
            }
            $('#exportTriggerList').remove();
            var form = '<form class="hide" id="exportTriggerList">';
            form += '<input name="infoType" value="' + type + '" />';
            form += '<input name="id" value="' + id + '" />';
            form += '<input name="name" value="' + name + '" />';
            form += '<input name="messageNo" value="' + messageNo + '" />';
            form += '</form>';
            $('body').append(form);
            $('#exportTriggerList').attr('action', '/infoLibrary/export.do').attr('method', 'get').submit() ;return false;
        })
    },
    //列表页面点击删除
    initdeleteButton:function(){

        $(".deleteButton").click(function() {

            var type = $('#paramForm').find("input[name='infoType']").val();
            var result,array = new Array();
            var messageNoArray = new Array();
            if(type == 1 ){
                result = $('#info_ws_table').bootstrapTable('getSelections');
                if(result.length<1){
                    swal("请选择数据");
                    return false;
                }
            }else if(type == 2){
                result = $('#info_wl_table').bootstrapTable('getSelections');
                if(result.length<1){
                    swal("请选择数据");
                    return false;
                }
            }
            else if(type == 0){
                result = $('#info_kw_table').bootstrapTable('getSelections');
                if(result.length<1){
                    swal("请选择数据");
                    return false;
                }

            }
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
                    var trigger = [];
                    if(type == 1 ){
                        for(var i = 0; i < result.length; i++) {
                            var item = result[i];
                            $('#info_ws_table>tbody>tr').remove('.selected')
                            array.push(item.triggerHostListid);
                            messageNoArray.push(item.messageNo);
                        }

                    }else if(type == 2){
                        for(var i = 0; i < result.length; i++) {
                            var item = result[i];
                            $('#info_wl_table>tbody>tr').remove('.selected')
                            array.push(item.triggerHostListid);
                            messageNoArray.push(item.messageNo);
                        }
                    }else if(type == 0){
                        for(var i = 0; i < result.length; i++) {
                            var item = result[i];
                            $('#info_kw_table>tbody>tr').remove('.selected')
                            array.push(item.triggerKwListid);
                            messageNoArray.push(item.messageNo);
                        }
                    }
                    $.ajax({
                        url: '/infoLibrary/delete.do',
                        type: 'POST',
                        data:{ids:array,infoType:type,messageNos:messageNoArray},
                        success: function (data) {
                            swal("删除成功！", "已经永久删除了这条记录。", "success");
                            $("#table>tbody>.hidetr").remove();
                            infolibrary.refreshInfoData();
                        },
                        error:function () {
                            swal("删除失败", "取消了删除操作！", "error");
                        }
                    })
                } else {
                    swal("已取消", "取消了删除操作！", "error")
                }
            })
        })
    },
    //列表页面点击详情
    initdetailButton:function(){
        $('.detailButton').on('click', function() {
            detailAddCount=0;//详情页面重新请求，将该参数重新设置为0
            var type = $('#paramForm').find("input[name='infoType']").val();
            var result,id;
            if(type == 1 ){
                result = $('#info_ws_table').bootstrapTable('getSelections');
                if(result.length!=1){
                    swal("请选择一条数据");
                    return false;
                }
                $('#triggerDetailForm').find("input[name='id']").val(result[0].triggerHostListid);
                $('#triggerDetailForm').find("input[name='name']").val(result[0].triggerHostListname);
            }else if(type == 2){
                result = $('#info_wl_table').bootstrapTable('getSelections');
                if(result.length!=1){
                    swal("请选择一条数据");
                    return false;
                }
                $('#triggerDetailForm').find("input[name='id']").val(result[0].triggerHostListid);
                $('#triggerDetailForm').find("input[name='name']").val(result[0].triggerHostListname);

            }
            else if(type == 0){
                result = $('#info_kw_table').bootstrapTable('getSelections');
                if(result.length!=1){
                    swal("请选择一条数据");
                    return false;
                }
                $('#triggerDetailForm').find("input[name='id']").val(result[0].triggerKwListid);
                $('#triggerDetailForm').find("input[name='name']").val(result[0].triggerKwListname);

            }
            $('#triggerDetails').modal('show');
            infolibrary.initInfoListTable();

        })
    },
    //详情页面点击修改
    initTriggerlibModify:function () {
        $('#triggerDetailModify').click(function() {
            var result = $('#triggerDetailTable').bootstrapTable('getSelections'); //获取表格的所有内容行
            if(detailAddCount!=0){
                swal("不支持批量操作，请对现有操作进行保存或者取消");
                return false;
            }
            if(result.length!=1){
                swal("请选择一条数据");
                return false;
            }
            detailAddCount++;
            var formData = $('#triggerDetailForm').formToJSON();
            var childId ;
            var valueName;
            for(var i = 0; i < result.length; i++) {
                var item = result[i];
                var paramFormData = $('#paramForm').formToJSON();
                //网站和白名单
                if(paramFormData.infoType==1 || paramFormData.infoType==2){
                    valueName = item.hostName;
                    childId =item.hostId;
                }else {
                    valueName = item.kwName;
                    childId =item.kwId;
                }
                $('#triggerDetailForm').find("input[name='childId']").val(childId);
                $('#triggerDetailTable>tbody>tr.selected').html('');
                $('#triggerDetailTable>tbody>tr.selected').attr("id","add"+detailAddCount);
                $('#triggerDetailTable>tbody>tr.selected').html("<td class='bs-checkbox'><input data-index='108' name='btSelectItem' type='checkbox' checked='checked'></td>" +
                    "<td><span class=''>"+formData.name+" </span></td>" +
                    "<td><input type='text' name='valueName' value='"+valueName+"' class='form-control input2' ><span class='span2'></span></td><td><a class='save'>保存</a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class='drop' >取消</a></td>")
                infolibrary.initEvent();
                $(".input2").blur(function() {

                });
            }
        });
    },
    //详情页面点击新增
    initTriggerlibAdd:function () {

        $('#triggerDetailAdd').click(function () {
            $('#myModaladdnew').modal('show');
            $('#triggerDetails').modal('hide');
            var formData = $('#triggerDetailForm').formToJSON();
            var paramForm = $('#paramForm').formToJSON();

            $('#addOrupdateFormV2_10').find('#id').val(formData.id);
            $('#addOrupdateFormV2_10').find('#messageNo').val(formData.messageNo);
            $('#addOrupdateFormV2_10').find('#messageName').val(formData.messageName);
            $('#addOrupdateFormV2_10').find('#infoType').val(paramForm.infoType);

            //信息推送触发网站
            if(paramForm.infoType==1){
                $('#addOrupdateFormV2_10').find('#typeName').val('网站');
            }
            //信息推送触发关键字
            if(paramForm.infoType==0){
                $('#addOrupdateFormV2_10').find('#typeName')[0].innerHTML="关键字";
            }
            //信息推送触发白名单
            if(paramForm.infoType==2){
                $('#addOrupdateFormV2_10').find('#typeName')[0].innerHTML="白名单";
            }
            $('#addOrupdateFormV2_10 a.fa-minus-circle').parents('div[name="bindMessageTypeInsert"]').remove();
            $('#addOrupdateFormV2_10').find('input[name="newRecord"]').val('');

        });
    },
    //详情删除按钮
    initTriggerlibDelete:function () {
        $('#triggerDetailDelete').click(function () {
            var result = $('#triggerDetailTable').bootstrapTable('getSelections'); //获取表格的所有内容行
            if(result.length<1){
                swal("请选择存在的数据");
                return false;
            }
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
                    var type = $('#paramForm').find("input[name='infoType']").val();
                    var id = $('#triggerDetailForm').find("input[name='id']").val();
                    var messageNo = $('#triggerDetailForm').find("input[name='messageNo']").val();
                    var array = new Array();
                    if(type == 1 || type == 2){
                        for (var i = 0; i< result.length;i++){
                            array.push(result[i].hostId);
                        }
                    }else if(type == 0){
                        for (var i = 0; i< result.length;i++){
                            array.push(result[i].kwId);
                        }
                    }
                    $.ajax({
                        url: '/infoLibrary/deleteList.do',
                        type: 'POST',
                        data:{childIds:array,infoType:type,chooseId:id,messageNo:messageNo},
                        success: function (data) {
                            swal("删除成功！", "已经永久删除了这条记录。", "success");
                            $("#triggerDetailTable>tbody>.hidetr").remove();
                            infolibrary.refreshInfoData();
                            infolibrary.refreshInfolISTData();
                        },
                        error:function () {
                            swal("删除失败", "取消了删除操作！", "error");
                        }
                    })
                } else {
                    swal("已取消", "取消了删除操作！", "error")
                }
            })
        })

    },
    initReIssued:function () {
        $('#triggerDetailReIssued').click(function () {
            var formData = $('#triggerDetailForm').formToJSON();
            formData.infoType = $('#paramForm').find("input[name='infoType']").val();
                $.ajax({
                    url: '/infoLibrary/reIssued.do',
                    type: 'POST',
                    data: formData,
                    success: function (data) {
                        swal("策略已经成功下发")
                    },
                    error:function () {
                        swal("未知错误,请刷新页面重新点击需要下发的列表")
                    }

                })
        })
    },
    init:function () {
        infolibrary.initPara('info_ws_table','/infoLibrary/getTriggerHost','/infoLibrary/getTriggerHostList','WS');
        infolibrary.minusListButton();

        infolibrary.initClick();
        infolibrary.initExportButton();
        infolibrary.initdeleteButton();
        infolibrary.initdetailButton();
        infolibrary.initTriggerlibModify();
        infolibrary.initTriggerlibAdd();
        infolibrary.initTriggerlibDelete();
       /* infolibrary.initEvent();*/

        infolibrary.initInfoTable();
        infolibrary.initReIssued();
    }

}

$(document).ready(function() {
    infolibrary.init();

});



function getColumn(args){
    var column ;
    if(args == "WS"){
        column = [
            {field: 'state',checkbox: true},
            {field: 'messageNo',title: '策略ID',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'triggerHostListid',title:'<span title="信息推送触发网站列表ID">信息推送触发网站列表ID</span>',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'triggerHostListname',title: '策略名称',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field:'appPolicy',title:'策略成功数/异常数',
                formatter:function(value,row,index){
                    if(value=="0/0"){
                        return "0/0";
                    }else{
                        return "<a href='#' onclick='getDetail("+index+")' title='详情'>"+value+"</a>";
                    }
                }
            },
            {field: 'hostNum',title: '包含网站数量（个）',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'createTimeStr',title: '创建日期',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
        ]
    }
    if(args == "WS_LIST"){
        column = [
            {field: 'state',checkbox: true},
            {field: 'triggerHostListname',title: '所属列表名称',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'hostName',title: '网站',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'operating',title:'操作',formatter:operatingFormatterDetail, events:operatingEventsDetail }
        ]
    }
    if(args == "KW"){
        column = [
            {field: 'state',checkbox: true},
            {field: 'messageNo',title: '策略ID',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'triggerKwListid',title: '<span title="信息推送触发关键字列表ID">信息推送触发关键字列表ID</span>',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'triggerKwListname',title: '策略名称',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field:'appPolicy',title:'策略成功数/异常数',
                formatter:function(value,row,index){
                    if(value=="0/0"){
                        return "0/0";
                    }else{
                        return "<a href='#' onclick='getDetail("+index+")' title='详情'>"+value+"</a>";
                    }
                }
            },
            {field: 'kwNum',title:'<span title="包含关键字段数量（个）">包含关键字段数量（个）</span>',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'createTimeStr',title: '创建日期',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
        ]

    }
    if(args == "KW_LIST"){
        column = [
            {field: 'state',checkbox: true},
            {field: 'triggerKwListname',title: '所属列表名称',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'kwName',title: '关键字',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'operating',title:'操作',formatter:operatingFormatterDetail, events:operatingEventsDetail }
        ]
    }
    if(args == 'WL'){

        column = [
            {field: 'state',checkbox: true},
            {field: 'messageNo',title: '策略ID',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'triggerHostListid',title: '白名单网站列表ID',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'triggerHostListname',title: '策略名称',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field:'appPolicy',title:'策略成功数/异常数',
                formatter:function(value,row,index){
                    if(value=="0/0"){
                        return "0/0";
                    }else{
                        return "<a href='#' onclick='getDetail("+index+")' title='详情'>"+value+"</a>";
                    }
                }
            },
            {field: 'hostNum',title: '包含网站数量（个）',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'createTimeStr',title: '创建日期',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
        ]
    }
    if(args == "WL_LIST"){
        column = [
            {field: 'state',checkbox: true},
            {field: 'triggerHostListname',title: '所属列表名称',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'hostName',title: '网站',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'operating',title:'操作',formatter:operatingFormatterDetail, events:operatingEventsDetail }
        ]
    }

    return column;
}

function QueryParams(args){
    if(args == "WS"){
        return WsQueryParams(args);
    }else if(args == "KW"){
        return KWQqueryParams(args);
    }else if(args == "WL"){
        return WLQqueryParams(args);
    }
}
function listQueryParams(params){
    if(arg_type == "WS"){
        return WsListQueryParams(params);
    }else if(arg_type == "KW"){
        return KwListQueryParams(params);
    }else if(arg_type == "WL"){
        return WLListQueryParams(params);
    }
}



//host
function WsQueryParams(params){
    var data = $('#paramForm').formToJSON();
    var searchFromData = $('#searchform').formToJSON();
    data.listName = searchFromData.listName;
    data.startTime = searchFromData.startTime;
    data.endTime = searchFromData.endTime;
    return data;
}

//host list
function WsListQueryParams(params){
    var data = $('#triggerDetailForm').formToJSON();
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    return data;
}

//KW
function KWQqueryParams(params){
    var data = $('#paramForm').formToJSON();
    var searchFromData = $('#searchform').formToJSON();
    data.listName = searchFromData.listName;
    data.startTime = searchFromData.startTime;
    data.endTime = searchFromData.endTime;
    return data;
}

//KW list
function KwListQueryParams(params){
    var data = $('#triggerDetailForm').formToJSON();
    data.page = params.offset/params.limit +1;
    data.pageSize = params.limit;
    return data;
}

//WL
function WLQqueryParams(params){
    var data = $('#paramForm').formToJSON();
    var searchFromData = $('#searchform').formToJSON();
    data.listName = searchFromData.listName;
    data.startTime = searchFromData.startTime;
    data.endTime = searchFromData.endTime;
    return data;
}

//WL list
function WLListQueryParams(params){
    var data = $('#triggerDetailForm').formToJSON();
    data.page = params.offset/params.limit +1;
    data.pageSize = params.limit;
    return data;
}

//click 查询
function searchformbuttonFunc() {
    infolibrary.initInfoTable();
}

// click 信息推送触发网站
function wsClickFunction() {
    $('#paramForm').find("input[name='infoType']").val("1");
    infolibrary.initPara('info_ws_table','/infoLibrary/getTriggerHost','/infoLibrary/getTriggerHostList','WS');
    if(clickWS==0){
        infolibrary.initInfoTable();
    }else if($('#info_ws_table').bootstrapTable('getData').length<1){
        infolibrary.initInfoTable();
    }
    // loadSel('listID','/trigger/getWSOption',true);

}

// click 信息推送触发关键字
function kwClickFunction() {
    $('#paramForm').find("input[name='infoType']").val("0");
    infolibrary.initPara('info_kw_table','/infoLibrary/getTriggerKW','/infoLibrary/getTriggerKWList','KW');
    if(clickKW==0){
        infolibrary.initInfoTable();
    }else if($('#info_kw_table').bootstrapTable('getData').length<1){
        infolibrary.initInfoTable();
    }
    // loadSel('listID','/trigger/getKWOption',true);

}
// click 白名单
function wlClickFunction() {
    $('#paramForm').find("input[name='infoType']").val("2");
    infolibrary.initPara('info_wl_table','/infoLibrary/getTriggerHost','/infoLibrary/getTriggerHostList','WL');
    if(clickWL==0){
        infolibrary.initInfoTable();
    }else if($('#info_wl_table').bootstrapTable('getData').length<1){
        infolibrary.initInfoTable();
    }
    // loadSel('listID','/trigger/getWLOption',true);
}
//模板下载
function exportTemplete(){
    var type = $('#paramForm').find("input[name='infoType']").val();
    $('#exportTem').remove();
    var form = '<form class="hide" id="exportTem" method="post" action="/infoLibrary/exportTemplate.do" >';
    form += '<input type="text" name="infoType" value='+type+'>';
    form += '</form>';
    $('body').append(form);
    $('#exportTem').submit() ;
    return false;
}
function upfile() {
    var importFormData = $('#importForm').formToJSON();
    if(importFormData.listName==''){
        swal("请输入列表名称");
        return false;
    }
    if(importFormData.infoType==''){
        swal("请选择类型");
        return false;
    }
    var file = $('#importForm').find("input[type='file']").val();
    if(file==""){
        swal("请选择文件");
        return false;
    }
    var fileTypes=['xlsx','xls'];
    var	suffix = file.substring(file.lastIndexOf('.') + 1,file.length);
    if($.inArray(suffix,fileTypes) == -1){
        swal("文件类型错误");
        return false;
    }

    $.ajaxFileUpload({
        url: '/infoLibrary/import.do',
        secureuri: false,
        data: importFormData,
        fileElementId: 'importFile',
        dataType: "json",
        success: function (data) {

            var failLog = '';
            var result = '';
            var title = '';
            if(data!=null){
                    if(data.result==1){
                        title = '<span class="text-danger">导入失败</span>';
                        failLog +='文件 导入失败,信息如下\r\n'
                        if(data.describtion !=null){
                            failLog += data.describtion+'\r\n';
                        }
                        if(data.dataErrorInfoList!=null && data.dataErrorInfoList.length>0){
                            for(var j = 0;j<data.dataErrorInfoList.length;j++){
                                var array = data.dataErrorInfoList[j];
                                failLog +='第 '+array.sheet+' 个表, '+array.row+' 行, '+ (array.column-1)+' 列'+array.errorType+'\r\n'
                            }
                        }
                        failLog = encodeURI(failLog);
                        result = '<a href="#" onclick= \"saveTxt(\''+failLog+'\',\'信息推送触发库详情\')\";return false>点击下载详情</a>';
                    }else if (data.result==2){
                        title = "导入失败";
                        result = '存在列表名称相同的记录';
                    } else {
                        title = '导入成功';
                        result = '';
                    }
                $("input[name='listName']").val('');
                swal({
                    title: title,
                    text: result,
                    html: true
                }, function(isConfirm) {
                    $('#leadto').modal('hide');
                    infolibrary.refreshInfoData();
                    //重置点击tab事件
                    clickWS = 0;
                    clickKW = 0;
                    clickWL = 0;

                    var infoTypeId =$('#infoTypeId').val();
                    if(infoTypeId == 1){
                        loadSel('listID','/trigger/getWSOption',true);
                    }else if(infoTypeId == 2){
                        loadSel('listID','/trigger/getWLOption',true);
                    }else if(infoTypeId == 0){
                        loadSel('listID','/trigger/getKWOption',true);
                    }
                });
            }else{
                $("input[name='listName']").val('');
                swal("导入失败");
            }
        },
        error:function(){
            $("input[name='listName']").val('');
            swal("导入失败");
        }

    });
}
function clearText() {
    $("input[name='listName']").val('');
    $('#infoTypeId').val('');
    $('#importForm').find("input[name='importFile']").val('');
}

//样式格式化
function operatingFormatter(value, row, index){
    var format="";
    if($("#zf104001_query").val()==1){
        format+="<a href='#' title='详情' class='m-r'><i class='fa fa-file-text-o fa-lg detail'></i></a>";
    } else {
        format+="<a href='#' title='详情' style='display: none' class='m-r'><i class='fa fa-file-text-o fa-lg detail'></i></a>";
    }
    if($("#zf104001_redo").val()==1){
        format+="<a href='#' title='重发' class='m-r'><i class='fa fal fa-share resend'></i></a>";
    }else{
        format+="<a href='#' title='重发' style='display: none' class='m-r'><i class='fa fal fa-share resend'></i></a>";
    }
    if ($("#zf104001_delete").val()==1){
        format+="<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
    } else{
        format+="<a href='#' style='display: none' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
    }
    return format;
}
//点击事件
window.operatingEvents = {
    'click .detail': function (e, value, row, index) {
        e.preventDefault();
        detailAddCount = 0;//详情页面重新请求，将该参数重新设置为0
        var type = $('#paramForm').find("input[name='infoType']").val();
        $('#triggerDetailForm').find("input[name='messageNo']").val(row.messageNo);
        if (type == 1) {
            $('#triggerDetailForm').find("input[name='id']").val(row.triggerHostListid);
            $('#triggerDetailForm').find("input[name='name']").val(row.triggerHostListname);
        } else if (type == 2) {
            $('#triggerDetailForm').find("input[name='id']").val(row.triggerHostListid);
            $('#triggerDetailForm').find("input[name='name']").val(row.triggerHostListname);
        }
        else if (type == 0) {
            $('#triggerDetailForm').find("input[name='id']").val(row.triggerKwListid);
            $('#triggerDetailForm').find("input[name='name']").val(row.triggerKwListname);
        }
        $('#triggerDetails').modal('show');
        infolibrary.initInfoListTable();
    },

    'click .resend': function (e, value, row, index) {
        e.preventDefault();
        // 重发指定应用用户策略
        resendPolicy(row);
    },

    'click .delete': function (e, value, row, index) {
        e.preventDefault();
        var type = $('#paramForm').find("input[name='infoType']").val();
        var array = new Array();
        var messageNoArray = new Array();
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
                if (type == 1) {
                    $('#info_ws_table>tbody>tr').remove('.selected')
                    array.push(row.triggerHostListid);
                    messageNoArray.push(row.messageNo);
                } else if (type == 2) {
                    $('#info_wl_table>tbody>tr').remove('.selected')
                    array.push(row.triggerHostListid);
                    messageNoArray.push(row.messageNo);
                } else if (type == 0) {
                    $('#info_kw_table>tbody>tr').remove('.selected')
                    array.push(row.triggerKwListid);
                    messageNoArray.push(row.messageNo);
                }
                $.ajax({
                    url: '/infoLibrary/delete.do',
                    type: 'POST',
                    data: {ids: array, infoType: type,messageNos:messageNoArray},
                    success: function (data) {
                        swal("删除成功！", "已经永久删除了这条记录。", "success");
                        $("#table>tbody>.hidetr").remove();
                        infolibrary.refreshInfoData();
                    },
                    error: function () {
                        swal("删除失败", "取消了删除操作！", "error");
                    }
                })
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}//样式格式化
function operatingFormatterDetail(value, row, index){
    var format="";
        if($("#zf104001_modify").val()==1){
            format+="<a href='#' title='删除'><i class='fa fa-close fa-lg delete detail'></i></a>";
        } else {
            format+="<a href='#' style='display: none' title='删除'><i class='fa fa-close fa-lg delete detail'></i></a>";
        }

    return format;
}
//点击事件
window.operatingEventsDetail = {

    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        // var result = $('#triggerDetailTable').bootstrapTable('getSelections'); //获取表格的所有内容行
        if(detailAddCount!=0){
            swal("不支持批量操作，请对现有操作进行保存或者取消");
            return false;
        }
        // if(result.length!=1){
        //     swal("请选择一条数据");
        //     return false;
        // }
        detailAddCount++;
        var formData = $('#triggerDetailForm').formToJSON();
        var childId ;
        var valueName;
        var messageNo;
        // for(var i = 0; i < result.length; i++) {
        //     var item = result[i];
            var paramFormData = $('#paramForm').formToJSON();
            //网站和白名单
            if(paramFormData.infoType==1 || paramFormData.infoType==2){
                valueName = row.hostName;
                childId =row.hostId;
                messageNo = row.messageNo;
            }else {
                valueName = row.kwName;
                childId =row.kwId;
                messageNo = row.messageNo;
            }
            $('#triggerDetailForm').find("input[name='childId']").val(childId);
            $('#triggerDetailForm').find("input[name='messageNo']").val(messageNo);
            $('#triggerDetailTable>tbody>tr.selected').html('');
            $('#triggerDetailTable>tbody>tr.selected').attr("id","add"+detailAddCount);
            $('#triggerDetailTable>tbody>tr.selected').html("<td class='bs-checkbox'><input data-index='108' name='btSelectItem' type='checkbox' checked='checked'></td>" +
                "<td><span class=''>"+formData.name+" </span></td>" +
                "<td><input type='text' name='valueName' value='"+valueName+"' class='form-control input2' ><span class='span2'></span></td>"+
                "<td><a class='save'>保存</a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class='drop' >取消</a></td>")
            infolibrary.initEvent();
            $(".input2").blur(function() {
                // console.log($(".input2").val());
            });

        // }
    },

    'click .refresh': function (e, value, row, index) {
        e.preventDefault();
        infolibrary.refreshInfoData();
    },

    'click .delete': function (e, value, row, index) {

        e.preventDefault();
        var type = $('#paramForm').find("input[name='infoType']").val();
        var id = $('#triggerDetailForm').find("input[name='id']").val();
        var messageNo = $('#triggerDetailForm').find("input[name='messageNo']").val();

        var array = new Array();
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
                if (type == 1) {
                    $('#info_ws_table>tbody>tr').remove('.selected')
                    array.push(row.hostId);
                } else if (type == 2) {
                    $('#info_wl_table>tbody>tr').remove('.selected')
                    array.push(row.hostId);
                } else if (type == 0) {
                    $('#info_kw_table>tbody>tr').remove('.selected')
                    array.push(row.kwId);
                }
                $.ajax({
                    url: '/infoLibrary/deleteList.do',
                    type: 'POST',
                    data: {childIds: array, infoType: type,chooseId:id,messageNo:messageNo},
                    success: function (data) {
                        swal("删除成功！", "已经永久删除了这条记录。", "success");
                        $("#table>tbody>.hidetr").remove();
                        infolibrary.refreshInfoData();
                        infolibrary.refreshInfolISTData();
                    },
                    error: function () {
                        swal("删除失败", "取消了删除操作！", "error");
                    }
                })
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    },

}

function getDetail(index){
    var type = $('#paramForm').find("input[name='infoType']").val();
    var resend = $("#zf104001_redo").val()==1?1:0;
    var webFlow = "";
    if (type==0){
        webFlow = $("#info_kw_table").bootstrapTable('getData');
        PolicyDetail.showDetail(webFlow[index].messageNo,204,1,resend,0);
    } else if (type==1){
        webFlow = $("#info_ws_table").bootstrapTable('getData');
        PolicyDetail.showDetail(webFlow[index].messageNo,203,1,resend,0);
    } else if (type==2){
        webFlow = $("#info_wl_table").bootstrapTable('getData');
        PolicyDetail.showDetail(webFlow[index].messageNo,203,1,resend,0);
    }

}

// 重发策略
function resendPolicy(row){
    var type = $('#paramForm').find("input[name='infoType']").val();
    $.ajax({
        url: "/infoLibrary/resend.do",
        type: 'POST',
        dataType:'json',
        data:{"messageNo":row.messageNo,"infoType":type},
        success: function (data) {
            swal("策略重发成功！");
        },
        error:function () {
            swal("操作失败", "重发策略失败！", "error");
        }
    })
}

function saveDetail(){
    var id = $('#triggerDetailForm').find("input[name='id']").val();
    var type = $('#paramForm').find("input[name='infoType']").val();
    var messageNo = $('#triggerDetailForm').find("input[name='messageNo']").val();
    var names = "";

    if (type ==1||type==2){
        for (var i=0;i<detailAddCount;i++){
            var value = $('#triggerDetailTable>tbody>tr').eq(i).find("input[name='valueName']").val();
            if(value==""){
                swal("请输入网站");
                return false;
            }else {
                var checkmessage = checkSite(value);
                if(checkmessage!=""){
                    swal(checkmessage);
                    return false;
                }
            }
            names += value+",";
        }
    } else {
        for (var i=0;i<detailAddCount;i++){
            var value = $('#triggerDetailTable>tbody>tr').eq(i).find("input[name='valueName']").val();
            if(value==""){
                swal("请输入关键字");
                return false;
            }
            names += value+",";
        }
    }


    $.ajax({
        url: '/infoLibrary/insertOrUpdate',
        type: 'POST',
        data: {infoType:type,chooseId:id,"names":names,messageNo:messageNo},
        success: function (data) {
            if(data!=null){
                if(data.result==1){
                    swal({
                        title: "保存成功",
                        text: "",
                        html: false
                    }, function(isConfirm) {
                        infolibrary.refreshInfolISTData();
                        infolibrary.refreshInfoData();
                        detailAddCount = 0;
                    });
                }else {
                    swal(data.message);
                }
            }
        }
    });
}

function dropSaveDetail(){
    infolibrary.refreshInfolISTData();
    detailAddCount = 0;
}

$('#addOrupdateFormV2_10 .circle-picture').on('click', function () {
    var $last = $('#addOrupdateFormV2_10').find('div[name="bindMessageTypeInsert"]').last();
    var $clone = $last.clone();
    $clone.find('input[name="newRecord"]').val('');
    $clone.find('a.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle').end();
    $last.after($clone);
    $('#addOrupdateFormV2_10 a.fa-minus-circle').on('click', function (e) {
        e.preventDefault();
        $(this).parents('div[name="bindMessageTypeInsert"]').remove();
    });
});

function checkAllParameter(formData) {
    formData.chooseId=formData.id;
    return true;
}

$('#addSubmitBut').click(function () {
    var formData = $('#addOrupdateFormV2_10').formToJSON();
        $('#addOrupdateFormV2_10').find('.has-error').remove();
        var names = "";
        var $obj = $('#addOrupdateFormV2_10').find('input[name="newRecord"]');
        var num = $obj.length;
        for (var i = 0; i < num; i++) {
            var value = $obj.eq(i).val();
            if (value == '') {
                $obj.eq(i).parent().parent().find('div>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入网址</i>');
                $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateFormV2_10\'))');
                return false;
            }
            if(formData.infoType==1||formData.infoType==2){
                var checkmessage = checkSite(value);
                if(checkmessage!=""){
                    $obj.eq(i).parent().parent().find('div>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 格式不正确</i>');
                    $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateFormV2_10\'))');
                    return false;
                }
            }
            names += value;
            names += ",";
        }
    formData.names=names;
    var url = '/infoLibrary/insertOrUpdate';
    // 检查所有参数
    if (checkAllParameter(formData)) {
        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            data: formData,
            success: function (data) {
                if(data!=null){
                    if(data.result==1){
                        swal({
                            title: "保存成功",
                            text: "",
                            html: false
                        }, function(isConfirm) {
                            $('#myModaladdnew').modal('hide');
                            $('#triggerDetails').modal('show');
                            infolibrary.refreshInfolISTData();
                            infolibrary.refreshInfoData();
                            detailAddCount = 0;
                        });
                    }else {
                        swal(data.message);
                    }
                }
            },
            error: function () {
                swal("操作失败", "取消新增/删除操作！", "error");
            }
        })
    }
});

function checkIP(value){
    var exp=/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
    var reg = value.match(exp);
    if(reg==null)
    {
        return false;
    }
    return true;
}
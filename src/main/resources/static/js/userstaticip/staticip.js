var seq = 0 ;
UserStaticIPJS = {

    buttonFunc:function () {
    	//查询条件时间控件初始化
        initSearchDate(3,false);
        $('.import').click(function () {
            importFunc();
        });
        $('#addOrupdateForm .circle-picture').on('click', function () {

            var $clone = $('#useripInsert').clone(),
                $last = $('#addOrupdateForm').children('div[id^="useripInsert"]').last();
            $clone.attr('id', $clone.attr('id') + seq).addClass("newInput")
                .find('a.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle').end()
                .find('input[name="userips"]').val('').attr('id', $clone.find('input[name="userips"]').attr('id') + seq).end()
                .find('input[name="useripPrefixs"]').val('').attr('id', $clone.find('input[name="useripPrefixs"]').attr('id') + seq).end()
                .find('div.tooltip').remove(); //删除tooltip元素;
            $last.after($clone);
            seq++;
            initTooltips();
            inputBlurEvent();
            $('#addOrupdateForm a.fa-minus-circle').on('click', function (e) {
                e.preventDefault();
                $(this).parents('div[id^="useripInsert"]').remove();
            });
        });
        $('#addSubmitBut').click(function () {
            var data = $('#addOrupdateForm').formToJSON();
            if (data.userName == "" || data.userName.length > 50) {
                clearWarn($('#userNameDIV'));
                $('#userNameDIV').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请输入用户，且最多支持50个字符</span></div>');
                $('#userNameDIV').find('input[name="userName"]').attr('onfocus', 'clearWarn($(\'#userNameDIV\'))');
                return false;
            }
            var ipArray = data.userips.split(",");
            var prefixArray = data.useripPrefixs.split(",");
            var array = new Array();

            for (var i = 0; i < ipArray.length; i++) {
                if (ipArray[i] == "") {
                    swal("请输入IP地址");
                    return false;
                } else {
                    if (checkIP(ipArray[i])) {
                        if (!checkIpv4Prefix(prefixArray[i])) {
                            swal("请输入整正确的Ipv4前缀");
                            return false;
                        }
                        if (array.length>0){
                            for (var j = 0 ;j<array.length;j++ ){
                                var var0 =array[j]
                                if(var0.userIp == ipArray[i] && var0.prefix == prefixArray[i] ){
                                    swal("表单存在重复IP地址");
                                    return false;
                                }
                            }
                        }
                        array.push({userIp:ipArray[i],prefix:prefixArray[i]})
                    } else if (checkIpv6(ipArray[i])) {
                        if (!checkIpv6Prefix(prefixArray[i])) {
                            swal("请输入整正确的Ipv6前缀");
                            return false;
                        }
                        if (array.length>0){
                            for (var j = 0 ;j<array.length;j++ ){
                                var var0 =array.get(j)
                                if(var0.userIp == ipArray[i] && var0.prefix == prefixArray[i] ){
                                    swal("表单存在重复IP地址");
                                    return false;
                                }
                            }
                        }
                        array.push({userIp:ipArray[i],prefix:prefixArray[i]})
                    } else {
                        swal("请输入整正确的Ip地址");
                        return false;
                    }
                }
            }
            insertOrUpdate('/userip/insert', data);
            function insertOrUpdate(URL, data) {
                $.ajax({
                    url: URL,
                    data: data,
                    type: 'POST',
                    async: false,
                    dataType: 'json',
                    success: function (data) {
                        if (data != null) {
                            var title = "";
                            var value = "";
                            if (data.result == 1) {
                                title = data.message;
                                swal({
                                    title: title,
                                    text: value,
                                    html: true
                                }, function (isConfirm) {
                                    $('#myModaladd').modal('hide');
                                    initTableJs.refreshData();
                                });
                            } else {
                                title = data.message;
                                if (data.data != null) {
                                    var failLog = encodeURI("以下IP已经存在:\r\n " + data.data);
                                    value = '<a href="#" onclick= \"saveTxt(\'' + failLog + '\',\'新增静态IP详情\')\";return false>点击下载详情</a>';
                                }
                                swal({
                                    title: title,
                                    text: value,
                                    html: true
                                }, function (isConfirm) {
                                });
                            }

                        } else {
                            swal("操作失败");
                        }
                    }

                })
            }
        });
        //导入
        $('#importFormButton').click(function () {

            var importForm = $('#importForm').formToJSON();

            if(importForm.operate==""){
                swal("请选择操作类型");
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
            var url = '';
            if(importForm.operate == 0){
                url = '/userip/overrideImport.do';
            }else {
                url = '/userip/incrementalImport.do';
            }


           if(importForm.operate==0){
               swal({
                   title:"",
                   text: "全量导入先清除数据然后将excel内容导入，请确认是否全量导入？",
                   html: false
               }, function(isConfirm) {
                   swal({
                       title: "<span class='text-success'>正在导入，请稍后...</span>",
                       html: true,
                       text: "<img src='/static/js/plugins/layer/skin/default/loading-1.gif' />",
                       showCancelButton: false,
                       showConfirmButton:false,
                       allowOutsideClick:false  //点击模态框外，模态框消失
                   });
                   $.ajaxFileUpload({
                       url: url,
                       secureuri: false,
                       data: importForm,
                       fileElementId: 'importFile',
                       dataType: "json",
                       success: function (results) {
                           if(results!=null) {
                               var failLog = '';
                               var result = '';
                               var title = '';
                               var data = results.data;
                               if (data.result == 1) {
                                   title = '<span class="text-danger">导入失败</span>';
                                   failLog += '文件 导入失败,信息如下\r\n';
                                   if (data.describtion != null) {
                                       failLog += data.describtion + '\r\n';
                                   }
                                   if (data.dataErrorInfoList != null && data.dataErrorInfoList.length > 0) {
                                       for (var j = 0; j < data.dataErrorInfoList.length; j++) {
                                           var array = data.dataErrorInfoList[j];
                                           failLog += '第 ' + array.sheet + ' 个表, ' + array.row + ' 行, ' + array.column + ' 列' + array.errorType + '\r\n'
                                       }
                                   }
                                   failLog = encodeURI(failLog);
                                   result = '<a href="#" onclick= \"saveTxt(\'' + failLog + '\',\'用户IP导入详情\')\";return false>点击下载详情</a>';
                               } else {
                                   title = '导入成功';
                                   result = '';
                               }
                               swal({
                                   title: title,
                                   text: result,
                                   html: true
                               }, function (isConfirm) {
                                   $('#leadto').modal('hide');
                                   initTableJs.refreshData();
                               });
                           }
                       },
                       error:function(e){

                           swal("导入失败");
                       }
                   });
               })
           }else{
               swal({
                   title: "<span class='text-success'>正在导入，请稍后...</span>",
                   html: true,
                   text: "<img src='/static/js/plugins/layer/skin/default/loading-1.gif' />",
                   showCancelButton: false,
                   showConfirmButton:false,
                   allowOutsideClick:false  //点击模态框外，模态框消失
               });
               $.ajaxFileUpload({
                   url: url,
                   secureuri: false,
                   data: importForm,
                   fileElementId: 'importFile',
                   dataType: "json",
                   success: function (results) {
                       if(results!=null) {
                           var failLog = '';
                           var result = '';
                           var title = '';
                           var data = results.data;
                           if (data.result == 1) {
                               title = '<span class="text-danger">导入失败</span>';
                               failLog += '文件 导入失败,信息如下\r\n';
                               if (data.describtion != null) {
                                   failLog += data.describtion + '\r\n';
                               }
                               if (data.dataErrorInfoList != null && data.dataErrorInfoList.length > 0) {
                                   for (var j = 0; j < data.dataErrorInfoList.length; j++) {
                                       var array = data.dataErrorInfoList[j];
                                       failLog += '第 ' + array.sheet + ' 个表, ' + array.row + ' 行, ' + array.column + ' 列' + array.errorType + '\r\n'
                                   }
                               }
                               failLog = encodeURI(failLog);
                               result = '<a href="#" onclick= \"saveTxt(\'' + failLog + '\',\'用户IP导入详情\')\";return false>点击下载详情</a>';
                           } else {
                               title = '导入成功';
                               result = '';
                           }
                           swal({
                               title: title,
                               text: result,
                               html: true
                           }, function (isConfirm) {
                               $('#leadto').modal('hide');
                               initTableJs.refreshData();
                           });
                       }
                   },
                   error:function(e){

                       swal("导入失败");
                   }
               });
           }
        });
        $('#searchFormButton').click(function () {
            initTableJs.refreshData();
        })
    },
    init:function () {
        initTableJs.initParam('/userip/getList','/userip/deleteUser',getColumn);
        initTableJs.init2();
        UserStaticIPJS.buttonFunc();
    }
};

detailmodify={
    initButton:function () {
        $('#modifyButton').click(function () {

            var dataForm = $('#modifyForm').formToJSON();
            if (checkIP(dataForm.userip)) {
                if (!checkIpv4Prefix(dataForm.useripPrefix)) {
                    swal("请输入整正确的Ipv4前缀");
                    return false;
                }

            } else if (checkIpv6(dataForm.userip)) {
                if (!checkIpv6Prefix(dataForm.useripPrefix)) {
                    swal("请输入整正确的Ipv6前缀");
                    return false;
                }
            } else {
                swal("请输入整正确的Ip地址");
                return false;
            }
            swal({
                title: "<span class='text-success'>重新绑定策略中，请稍后...</span>",
                html: true,
                text: "<img src='/static/js/plugins/layer/skin/default/loading-1.gif' />",
                showCancelButton: false,
                showConfirmButton:false,
                allowOutsideClick:false  //点击模态框外，模态框消失
            });
                $.ajax({
                    url: "/userip/update",
                    data: dataForm,
                    type: 'POST',
                    async: false,
                    dataType: 'json',
                    success: function (data) {
                        if (data != null) {
                            if(data.result==0){
                                swal(data.message);
                            }else {
                                swal({
                                    title: data.message,
                                    text: "",
                                    html: true
                                }, function (isConfirm) {
                                    $('#detailModify').modal('hide');
                                    initDetailTable(data.data);
                                    initTableJs.refreshData();
                                });
                            }
                        } else {
                            swal("操作失败");
                        }
                    }
                })
        })
    }


};

$(document).ready(function() {
    UserStaticIPJS.init();
    detailmodify.initButton();
});

function queryParams(param) {
    var data = $('#searchForm').formToJSON();
    data.page = param.offset/param.limit +1 ;
    data.pageSize = param.limit;
    if(data.userName==''){
        data.userName = undefined;
    }
    return data;
}

/** 新增详情相关js */
function getDetail(index) {
    var webFlow = $("#table").bootstrapTable('getData');
    PolicyDetail.showDetail(webFlow[index].messageNo, 130, 1, 1, 0);
}

function getColumn() {
    var column =[
        {field: 'state',checkbox: true},
        {field: 'messageNo',title: '策略ID'},
        {field: 'userName',title: '用户账号',formatter:function(value,row,index){
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
        {field: 'createTime',title: '创建时间',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
        {filed: "operate",title:"操作",formatter:operatingFormatter,events:operatingevent}
    ];
    return column;
}
function newAddButtonFunction() {
    document.getElementById("addOrupdateForm").reset();
    $('#addOrupdateForm').find('input[type="hidden"]').val('');
    $('#addOrupdateForm').find('input[name="userName"]').attr("readOnly",false);
    $('#myModaladd').modal('show');
    $('#addOrupdateForm .newInput').remove();
}
function modifyButtonFunction(row) {
    $('#myModaladd').modal('show');
    revertProperties(row);
}

// 获取所有参数值
function revertProperties(row) {
    $('#messageNo').val(row.messageNo);
    $('#addOrupdateForm').find('input[name="userName"]').val(row.userName);

    var userInfo = row.userInfo;
    for(var i=0;i<userInfo.length;i++){
        if(i==0){
            $('#addOrupdateForm').find('input[name="userips"]').val(userInfo[i].userIp);
            $('#addOrupdateForm').find('input[name="useripPrefixs"]').val(userInfo[i].userIpPrefix);
        }else{
            addUserInfo();
        }
    }
}

function addUserInfo() {
    var $clone = $('#useripInsert').clone(),
        $last = $('#addOrupdateForm').children('div[id^="useripInsert"]').last();
    $clone.addClass("newInput")
        .find('a.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle').end()
        .find('input[name="userips"]').val('').attr('id', $clone.find('input[name="userips"]').attr('id') + seq).end()
        .find('input[name="useripPrefixs"]').val('').attr('id', $clone.find('input[name="useripPrefixs"]').attr('id') + seq).end()
        .find('div.tooltip').remove(); //删除tooltip元素;
    $last.after($clone);
    $('#addOrupdateForm a.fa-minus-circle').on('click', function (e) {
        e.preventDefault();
        $(this).parents('div[id^="useripInsert"]').remove();
    });
}
//删除
function deleteFunction(url,data) {

    var userid = [];
    if(data==undefined){
        var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
        for(var i = 0; i < result.length; i++) {
            var item = result[i];
            $('#table>tbody>tr').remove('.selected')
            userid.push(item.userId);
        }
    }else {
        userid.push(data);
    }

    $.ajax({
        url: '/userip/deleteUser',
        type: 'POST',
        data:{userid:userid},
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
//导入
function importFunc() {
    document.getElementById("importForm").reset();
}


function detailButtonFunction() {
//none
}
//模板下载
function exportTemplate(){
    $('#exportTem').remove();
    var form = '<form class="hide" id="exportTem">';
    form += '</form>';
    $('body').append(form);

    $('#exportTem').attr('action', '/userip/exportTemplate.do').attr('method', 'get').submit() ;return false;
}

//样式格式化
function operatingFormatter(value, row, index){
    var format=""
    if($("#zf103002_query").val()==1){
        format+="<a href='#' title='详情' class='m-r'><i class='fa fa-file-text-o fa-lg detail'></i></a>";
    } else {
        format+="<a href='#' title='详情' style='display: none' class='m-r'><i class='fa fa-file-text-o fa-lg detail'></i></a>";
    }
    if($("#zf103002_redo").val()==1){
        format+="<a href='#' title='重发' class='m-r'><i class='fa fal fa-share resend'></i></a>";
    }else{
        format+="<a href='#' title='重发' style='display: none' class='m-r'><i class='fa fal fa-share resend'></i></a>";
    }
    if ($("#zf103002_delete").val()==1){
        format+="<a href='#' title='删除' class='m-r'><i class='fa fa-close fa-lg delete'></i></a>";
    } else{
        format+="<a href='#' title='删除' style='display: none'  class='m-r'><i class='fa fa-close fa-lg delete'></i></a>";
    }
    if ($("#zf103002_modify").val()==1){
        format+= "<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
    } else{
        format+= "<a href='#' style='display: none' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
    }

    return format;
}

// 重发ip地址用户策略
function resendPolicy(row) {
    $.ajax({
        url: "/userip/resend",
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

//点击事件
window.operatingevent = {
    'click .detail': function (e, value, row, index) {
        e.preventDefault();
        var userInfo = row.userInfo;
        var array = new Array();
        if(userInfo.length>0){
            for (var j =0 ;j<userInfo.length;j++){
                array.push({ipId:userInfo[j].ipId,userip:userInfo[j].userIp,
                    useripPrefix:userInfo[j].userIpPrefix,userName:row.userName,userid:row.userId})
            }
        }
        $('#userIpDetail').modal('show');
        $('#ipDetailList').bootstrapTable('destroy').bootstrapTable({
            data: array,
            queryParams: queryParams,
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            pagination: true,
            sidePagination: 'client',
            iconSize: "outline",
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: [
                {field: 'state',checkbox: true},
                {field: 'userip',title: 'IP'},
                {field: 'useripPrefix',title: '前缀'}
            ]
        });
    },
    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        var userInfo = row.userInfo;
        var array = new Array();
        if(userInfo.length>0){
            for (var j =0 ;j<userInfo.length;j++){
                array.push({ipId:userInfo[j].ipId,userip:userInfo[j].userIp,
                    useripPrefix:userInfo[j].userIpPrefix,userName:row.userName,userid:row.userId})
            }
        }
        $('#userIpDetail').modal('show');
        initDetailTable(array);
    },
    'click .resend': function (e, value, row, index) {
        e.preventDefault();
        resendPolicy(row);
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
                deleteFunction("",row.userId);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        });
    }
};


window.detailoperatingevent = {
    'click .edit': function (e, value, row, index) {
        $('#detailModify').modal("show");
        $("#modifyForm").find('input[name="ipId"]').val(row.ipId);
        $("#modifyForm").find('input[name="userid"]').val(row.userid);
        $("#modifyForm").find('input[name="userName"]').val(row.userName).attr('disabled',true);
        $("#modifyForm").find('input[name="userip"]').val(row.userip);
        $("#modifyForm").find('input[name="useripPrefix"]').val(row.useripPrefix);
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
                //詳情表格中的删除操作
                $('#ipDetailList>tbody>tr').remove('.selected');
                var ipId = [];
                ipId.push(row.ipId);
                $.ajax({
                    url: '/userip/deleteIp',
                    type: 'POST',
                    data:{ipId:ipId},
                    success: function (data) {
                        swal("删除成功！", "已经永久删除了这条记录。", "success");
                        $("#table>tbody>.hidetr").remove();
                        initTableJs.refreshData();
                    },
                    error:function () {
                        swal("删除失败", "取消了删除操作！", "error");
                    }
                })
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
};

function initDetailTable(array) {

    $('#ipDetailList').bootstrapTable('destroy').bootstrapTable({
        data: array,
        queryParams: queryParams,
        contentType: 'application/x-www-form-urlencoded',
        striped: true,
        undefinedText: '',
        pagination: true,
        sidePagination: 'client',
        iconSize: "outline",
        clickToSelect:true,
        pageSize: 10,
        pageList: [10, 25, 50, 100, 200],
        columns: [
            {field: 'state',checkbox: true},
            {field: 'userip',title: 'IP'},
            {field: 'useripPrefix',title: '前缀'},
            {filed: "operate",title:"操作",formatter:function () {
                    var format=""
                        +"<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>"
                        +"<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>"
                    return format;
                },events:detailoperatingevent}
        ]
    });
}
var domain_white_click = 0;
var domain_black_click = 0;
var url_black_click = 0;
var countSeq = 0;
var section , data_url ;
var curRow = {};
GETListJS = {
    initParam:function(id,url){
        section=id;
        data_url = url;
    },
    resetClickNum:function(){
        domain_white_click = 0;
        domain_black_click = 0;
        url_black_click = 0;
    },
    refreshTable:function () {
        $('#'+section+'_table').bootstrapTable('refresh', { url: data_url});
        Warming();
    },
    initInfoListTable: function(){
        $('#'+section+'_table').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: data_url,
            queryParams: queryParams,
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            // showRefresh: !0,
            // showToggle: !0,
            showColumns: !0,
            toolbar: "#commonButton",
            pagination: true,
            sidePagination: 'server',
            iconSize: "outline",
            icons: {
                columns: "glyphicon-list",
            },
            clickToSelect:true,
            idField: "id",
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn(),
        });
    },
    initClick:function () {
        $('#domain_white').on('click', function () {
            dwClickFunction();
        })
        $('#domain_black').on('click', function () {
            dbClickFunction();
        })
        $('#url_black').on('click', function () {
            ubClickFunction();
        })
    },
    handleImport:function () {
        $('.import').click(function () {
            document.getElementById("importForm").reset()
        });

        $('#importFormButton').click(function () {
            var importFormData = $('#importForm').formToJSON();
            if(importFormData.type==''){
                swal("请选择名单类型");
                return false;
            }
            if(importFormData.operatetype==''){
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
            var listurl = '';
            if(importFormData.operatetype == 0){
                listurl = '/httpget/overrideImport.do';
            }else {
                listurl = '/httpget/incrementalImport.do';
            }
            $.ajaxFileUpload({
                url: listurl,
                secureuri: false,
                data: importFormData,
                fileElementId: 'importFile',
                dataType: "json",
                success: function (data) {
                    var title = '<span class="text-danger">导入失败</span>';
                    var result = '';
                    if(data!=null) {
                        if (data.result == 0) {
                            title = '导入成功';
                            result = '';
                            swal({
                                title: title,
                                text: result,
                                html: true
                            }, function(isConfirm) {
                                $('#leadto').modal('hide');
                                GETListJS.refreshTable();
                                //重置点击tab事件
                                GETListJS.resetClickNum();
                            });
                        }else if(data.result == 1){
                            title = '<span class="text-danger">导入失败</span>';
                            if(data.describtion !=null){
                                result = data.describtion ;
                            }
                            swal({
                                title: title,
                                text: result,
                                html: true
                            }, function(isConfirm) {
                                // $('#leadto').modal('hide');
                                GETListJS.refreshTable();
                                //重置点击tab事件
                                GETListJS.resetClickNum();
                            });
                        }
                    }

                },
                error:function(){
                    swal("导入失败");
                }

            });
        })
    },
    handleExport:function () {
        $('#exportData').click(function(){
            $('#exportForm').remove();
            var type =$('#searchform').find('input[name="type"]').val();
            if(type==""){
                type = 0;
            }
            var form = '<form class="hide" id="exportForm">';
            form += '<input name="type" value="' +type + '" />';
            form += '</form>';
            $('body').append(form);

            $('#exportForm').attr('action', '/httpget/export.do').attr('method', 'get').submit() ;return false;
        })
    },
    searchButtonFun:function () {
      $('#searchformbutton').click(function () {
          GETListJS.refreshTable();
          GETListJS.resetClickNum();
      })  
    },
    createAndSend:function () {
      $('#createAndSend').click(function () {

          document.getElementById("SendForm").reset();
          $('#sendSnippet').modal('show');
          loadDev();
      });
        $('#SendButton').click(function () {
            var zongfenId =$('#destIp').val();
            if(zongfenId=="" || zongfenId==undefined){
                swal("请选择服务器");
                return false;
            }
            swal({
                title: "<span class='text-success'>正在下发，请稍后...</span>",
                html: true,
                text: "<img src='/static/js/plugins/layer/skin/default/loading-1.gif' />",
                showCancelButton: false,
                showConfirmButton:false,
                allowOutsideClick:false  //点击模态框外，模态框消失
            });
            $.ajax({
                url: '/httpget/createAndSend',
                data:{zongfenId:zongfenId},
                type: 'POST',
                async: true,
                success: function (data) {
                    // swal.close(); //关闭弹窗
                    if(data!=null){
                        if(data == "error"){
                            swal("操作失败！");
                        }else if(data == "no_dev"){
                            swal("操作失败，没有配置下发设备！");
                        }else if(data == "sftp_error"){
                            swal("操作失败，sftp连接失败！");
                        }else if(data == "success"){
                            swal({
                                title: "操作成功",
                                text: '',
                                html: true
                            }, function(isConfirm) {
                                $('#sendSnippet').modal('hide');
                                Warming();
                            });
                        }
                    }else {
                        swal({
                            title: "操作成功",
                            text: '',
                            html: true
                        }, function(isConfirm) {
                            $('#sendSnippet').modal('hide');
                        });
                    }
                },
                error:function () {
                    // swal.close(); //关闭弹窗
                    swal("操作失败");
                }
            })
        })
    },
    initBatchDeleteButton:function(){

        $(".deleteButton").click(function() {
            var result =  $('#'+section+'_table').bootstrapTable('getSelections'); //获取表格的所有内容行
            if(result.length<1){
                swal("请选择至少一条数据");
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
                    //表格中的删除操作
                    deleteFunction('/httpget/batchDelete');
                } else {
                    swal("已取消", "取消了删除操作！", "error")
                }
            })
        })
    },
    initModifyButton:function () {
        $('#modifyButton').click(function () {

            $.ajax({
                url: "/httpget/update",
                type: 'POST',
                data:$('#modifyForm').formToJSON(),
                success: function (data) {
                    if(data.result==0){
                        swal(data.message);
                    }else {
                        swal({
                            title: data.message,
                            text: '',
                            html: true
                        }, function(isConfirm) {
                            $('#modifySnippet').modal('hide');
                            GETListJS.refreshTable();
                        });
                    }

                },
            })
        });
    },
    fileDetail:function(){
        $("#fileDetialMessageType").val("207");
        fileDetail.tableInit();

        $("#getDetail").click(function(){
            fileDetail.getList();
        });
    },
    init:function () {
        GETListJS.initClick();
        GETListJS.initParam('domain_white','/httpget/listDomain');
        GETListJS.initInfoListTable();
        GETListJS.resetClickNum();
        GETListJS.handleImport();
        GETListJS.handleExport();
        GETListJS.searchButtonFun();
        GETListJS.createAndSend();
        GETListJS.initBatchDeleteButton();
        GETListJS.initModifyButton();
        GETListJS.fileDetail();Warming();
    }

};
$(document).ready(function() {

    GETListJS.init();

});

//列
function getColumn() {

    var column = [];
    if(section == 'domain_white'){
        column = [
            {field: 'state',checkbox: true},
            {field: 'type',title: '类型',formatter: operateFormatter},
            {field: 'domain',title: '域名',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'updateTimeStr',title: '更新日期',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'operate',title: '操作',formatter:operatingFormatter,events:operatingevent},
        ]
    }else if(section == 'domain_black'){
        column = [
            {field: 'state',checkbox: true},
            {field: 'type',title: '类型',formatter: operateFormatter},
            {field: 'domain',title: '域名',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'updateTimeStr',title: '更新日期',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'operate',title: '操作',formatter:operatingFormatter,events:operatingevent},
        ]
    }else if(section == 'url_black'){
        column = [
            {field: 'state',checkbox: true},
            {field: 'type',title: '类型',formatter: operateFormatter},
            {field: 'domain',title: 'URL',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'updateTimeStr',title: '更新日期',formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }},
            {field: 'operate',title: '操作',formatter:operatingFormatter,events:operatingevent},
        ]
    }
    return column;
}
//参数
function queryParams(params) {
    var data = $('#searchform').formToJSON();
    if(section == 'domain_white'){
        data.type = 0;
    }else if(section == 'domain_black'){
        data.type = 1;
    }else if(section == 'url_black'){
        data.type = 2;
    }
    if(data.domain==""){
        data.domain =undefined;
    }
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    return data;
}
//domain White click
function  dwClickFunction(){
    GETListJS.initParam('domain_white','/httpget/listDomain');
    if(domain_white_click == 0){
        GETListJS.initInfoListTable();
    }
    $('#searchform').find('input[name="type"]').val(0);

    domain_white_click++;
}
//domain black click
function  dbClickFunction(){
    GETListJS.initParam('domain_black','/httpget/listDomain');
    if(domain_black_click == 0){
        GETListJS.initInfoListTable();
    }
    $('#searchform').find('input[name="type"]').val(1);
    domain_black_click ++;
}
//URL black click
function  ubClickFunction(){
    GETListJS.initParam('url_black','/httpget/listDomain');
    if(url_black_click == 0){
        GETListJS.initInfoListTable();
    }
    $('#searchform').find('input[name="type"]').val(2);
    url_black_click++;
}

function exportTemplate(){
    $('#exportTem').remove();
    var form = '<form class="hide" id="exportTem">';
    form += '</form>';
    $('body').append(form);

    $('#exportTem').attr('action', '/httpget/exportTemplate.do').attr('method', 'get').submit() ;return false;
}
//列表的类型格式化
function operateFormatter(value, row, index) {
    var data = "";
    if (value == 0){
        data = "域名白名单";
    }else if(value == 1){
        data = "域名黑名单";
    }else if(value == 2){
        data = "URL黑名单";
    }
    return "<span title='"+data+"'>"+data+"</span>";
}
// function valueFormatter(value, row, index) {
//     return "<a href=\"#\" class='domain' name=\"domain\" data-type=\"text\" data-pk=\""+row.id+"\" data-title=\"域名（URL）\">" + value + "</a>";
// }

function loadDev() {
    loadSel('destIp','/select/getZongfenDev',true,{packetType:9,packetSubtype:0});
}
//样式格式化
function operatingFormatter(value, row, index){
    var format="";
        if ($("#zf104005_modify").val()==1){
            format +=  "<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
        } else {
            format +=  "<a href='#' style='display: none' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
        }
        if ($("#zf104005_delete").val()==1) {
            format += "<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
        } else{
            format += "<a href='#' style='display: none' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
        }
    return format;
}
//点击事件
window.operatingevent = {
    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $('#modifySnippet').modal('show');
        $('#modifyForm').find('input[name="id"]').val(row.id);
        $('#modifyForm').find('input[name="domain"]').val(row.domain);
        $('#modifyForm').find('input[name="type"]').val(row.type);
        var type = row.type ,text;
        if (type == 0){
            text= "域名白名单";
        }else if(type == 1){
            text= "域名黑名单";
        }else if(type == 2){
            text= "URL黑名单";
        }
        $('#type').val(text).attr("disabled",true);

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
                deleteFunction('/httpget/delete',{id:row.id,type:row.type});
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
};

function deleteFunction(url,data) {
    var result;
    var type;
    var shuju = [];
    if(data==undefined){
        result = $('#'+section+'_table').bootstrapTable('getSelections'); //获取表格的所有内容行
        for(var i = 0; i < result.length; i++) {
            var item = result[i];
            shuju.push(item.id);
            type = item.type;
        }
    }else {
        shuju.push(data.id);
        type = data.type;
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{ids:shuju,type:type},
        success: function (data) {

            swal("删除成功！", "已经永久删除了这条记录。", "success");
            $("#"+section+"_table>tbody>.hidetr").remove();
            GETListJS.refreshTable();

        },
        error:function () {
            swal("删除失败", "取消了删除操作！", "error");
        }
    })
}
function Warming() {

    $.ajax({
        url: "/httpget/getWarming",
        type: 'POST',
        dataType:"json",
        success: function (data) {

            var value = "";
            if(data!=null){
                if(data.HFDW==1){
                    value +="域名白名单 ";
                }
                if(data.HFDB==1){
                    value +="域名黑名单 ";
                }
                if(data.HFUB==1){
                    value +="URL黑名单 ";
                }
                if(value==""){
                    $('#warmSection').removeClass("alert-danger").addClass("alert-success");
                    $('#warmSection').html("最新文件已经下发");
                }else {
                    $('#warmSection').removeClass("alert-success").addClass("alert-danger");
                    $('#warmSection').html(value+"已更新，请点击“生成文件并下发”进行内容更新");
                }
            }
        }
    })
}
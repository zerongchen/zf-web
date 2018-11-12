//              域白    域黑    U黑
var fileType = ["HFDW","HFDB","HFUB"];

//新增，修改JS
classInfoJS= {

    submitFieButton:function() {


        $('#addSubmitBut').on('click', function () {
            var formData = $('#addOrupdateForm').formToJSON();
            if(formData.classId !=''){
                formData.classType = $('#classType').val();
            }
            if(formData.className.length>50 || formData.className.length<1){
                clearWarn($('#className'));
                $('#className').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请输入策略名称，且最多支持50个字符</span></div>');
                $('#className').find('input[name="className"]').attr('onfocus','clearWarn($(\'#className\'))');
                return false;
            }
            if(formData.classType ==''){
                clearWarn($('#classTypeDIV'));
                $('#classTypeDIV').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请选择</span></div>');
                return false;
            }
            if(formData.classFileName ==''){
                var type = formData.classType;
                if(type==5){
                    clearWarn($('#domainBlackDiv'));
                    $('#domainBlackDiv').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>没有HTTP GET 清洗文件,不可下发</span></div>');
                    return false;
                }else {
                    clearWarn($('#classFileNameDIV'));
                    $('#classFileNameDIV').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>没有对应文件不可下发</span></div>');
                    return false;
                }

            }
            if(formData.serverIp.length<1 || !checkIP(formData.serverIp)){
                clearWarn($('#serverIp'));
                $('#serverIp').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请输入IP，请确保IP格式正确</span></div>');
                $('#serverIp').find('input[name="serverIp"]').attr('onfocus','clearWarn($(\'#serverIp\'))');
                return false;
            }
            if(formData.serverPort.length<1 || !checkPort(formData.serverPort)){
                clearWarn($('#serverPort'));
                $('#serverPort').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请输入端口，请确保端口正确</span></div>');
                $('#serverPort').find('input[name="serverPort"]').attr('onfocus','clearWarn($(\'#serverPort\'))');
                return false;
            }
            if(formData.sftpUsername.length>50 || formData.sftpUsername.length<1){
                clearWarn($('#sftpUsername'));
                $('#sftpUsername').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请输入Sftp用户名，且最多支持50个字符</span></div>');
                $('#sftpUsername').find('input[name="sftpUsername"]').attr('onfocus','clearWarn($(\'#sftpUsername\'))');
                return false;
            }
            if(formData.sftpPassword.length>50 || formData.sftpPassword.length<1){
                clearWarn($('#sftpPassword'));
                $('#sftpPassword').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请输入sftpPassword，且最多支持50个字符</span></div>');
                $('#sftpPassword').find('input[name="sftpPassword"]').attr('onfocus','clearWarn($(\'#sftpPassword\'))');
                return false;
            }
            $.ajax({
                url: '/classinfo/insertOrUpdate',
                type: 'POST',
                data:formData,
                success: function (data) {
                    if(data!=null){
                        if(data.result==1){

                            swal({
                                title:"",
                                text: data.message,
                                html: false
                            }, function(isConfirm) {
                                $('#myModaladd').modal('hide');
                                initTableJs.refreshData();
                            });
                        }else{
                            swal("新增失败");
                        }
                    }else{
                        swal("新增失败");
                    }
                },
                error:function () {
                    swal("新增失败");
                }
            })
        })
    },
    initSelector : function(){
        $('#classTypeDIV').on("change",function(){
            clearWarn($('#classTypeDIV'));
            clearWarn($('#classFileNameDIV'));
            clearWarn($('#domainBlackDiv'));
        });
        // $('#classFileNameDIV').on("change",function(){
        //     clearWarn($('#classFileNameDIV'));
        // });
        $('#classType').change(function () {
            var value = this.value;
            loadFile('classFileName','/select/getClassFileInfo',{classType:value});
            $('#classFileName').attr("readOnly",true);
            if(value==5){
                $('#httpGetDetail').show();
                $('#classFileNameDIV').hide();
                $('#domainWrite').attr("readOnly",true);
                $('#domainBlack').attr("readOnly",true);
                $('#urlBlack').attr("readOnly",true);
                var files = $('#classFileName').val();
                var fileArray = files.split(",");
                for (var i = 0 ;i<fileArray.length;i++){
                    if(fileArray[i].indexOf("HFDW")>-1){ // 域白
                        $('#domainWrite').val(fileArray[i]);
                    }else if(fileArray[i].indexOf("HFDB")>-1){ // 域黑
                        $('#domainBlack').val(fileArray[i]);
                    }else if(fileArray[i].indexOf("HFUB")>-1){ // 域黑
                        $('#urlBlack').val(fileArray[i]);
                    }
                }
            }else {
                $('#httpGetDetail').hide();
                $('#classFileNameDIV').show();
            }

        });
    },
    searchFormSubBut:function () {
        $("#searchFormButton").on('click', function(){
            initTableJs.refreshData();
        })
    },
    init:function(){
        classInfoJS.submitFieButton();
        classInfoJS.initSelector();
        classInfoJS.searchFormSubBut();
    }


}
//详情js
var detailURL;
detailJS ={

    initParam:function(initDetailURL){
        detailURL = initDetailURL;
    }
    ,
    refreshData:function(){
        $("#detailTable").bootstrapTable('refresh', { url: detailURL});
    },
    initDetailTable:function () {
        $('#detailTable').bootstrapTable('destroy').bootstrapTable({
            // method: 'post',
            url: detailURL,
            queryParams: queryParams,
            // contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            pagination: true,
            toolbar: "#ActButtons",
            sidePagination: 'client', //分页方式：client客户端分页，server服务端分页（*）
            iconSize: "outline",
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getDetailColumns(),
            onLoadSuccess: function(data){  //加载成功时执行
                for(var i = 0; i < data.length; i++) {
                    var item = data[i];
                    if(item.statuss==1){
                        $('#detailTable>tbody>tr').eq(i).find('input').attr("checked",true);
                    }
                }
            },
        });
    },
    initRepeatAndrefreshButton:function(){
        $('#detailForm div .btn-primary').on('click',function(){
            alert("重新下发");
        })
        $('#detailForm div .btn-info').on('click',function(){
            detailJS.refreshData()
        })
    },
    autoSelect:function(){
        var result = $('#detailTable').bootstrapTable('getData');
        for(var i = 0; i < result.length; i++) {
            var item = result[i];
            if(item.statuss==1){
                $('#table>tbody>tr').attr("checked",true);
            }
        }
    },

    init:function(){
        detailJS.initDetailTable();
        detailJS.initRepeatAndrefreshButton();

    }

}

$(document).ready(function() {
    initTableJs.initParam('/classinfo/initTable.do','/classinfo/delete.do',getColumnFunction)
    initTableJs.init();
    classInfoJS.init();
});


function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'classId',title: '策略ID'},
        {field: 'className',title: '策略名称',formatter: operateFormatter,events:operateEvents},
        {field: 'createTimeStr',title: '创建时间'},
        {field: 'statusDec',title: '策略状态'},
        {field: 'modifyTimeStr',title: '最近修改时间'},
        {field: 'modifyOper',title: '操作账号'}
    ]
    return column;
}
function getDetailColumns(){
    var column =[
        {field: 'state',checkbox: true},
        {field: 'deviceName',title: '设备名称'},
        {field: 'deviceIP',title: '设备ID'},
        {field: 'area',title: '地区'},
        {field: 'status',title: '下发状态'},
        {field: 'time',title: '下发时间'}
    ]
    return column;
}


function newAddButtonFunction(){
    $('#myModaladd').find("h4").text("新增");
    $('#addOrupdateForm').find('input[name="className"]').val('');
    $('#addOrupdateForm').find('input[name="classId"]').val('');
    $('#addOrupdateForm').find('input[name="serverIp"]').val('');
    $('#addOrupdateForm').find('input[name="serverPort"]').val('');
    $('#addOrupdateForm').find('input[name="sftpUsername"]').val('');
    $('#addOrupdateForm').find('input[name="sftpPassword"]').val('');
    $('#addOrupdateForm').find('input[name="createTimeStr"]').val('');
    $('#classType').val('').attr("disabled",false);;
    $('#classFileName').val('');
    $('#classFileNameDIV').show();
    $('#httpGetDetail').hide();
    $('#myModaladd').modal('show');

}
function deleteFunction(url){
    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var classIds = [];  

    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        $('#table>tbody>tr').remove('.selected')
        classIds.push(item.classId);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{classIds:classIds},
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
    var result = $('#table').bootstrapTable('getSelections');
    if(result.length!=1){
        swal("请选择一条数据");
        return false;
    }
    $('#myModaladd').find("h4").text("修改");
    $('#myModaladd').modal('show');
    $('#addOrupdateForm').find('input[name="className"]').val(result[0].className);
    $('#addOrupdateForm').find('input[name="serverIp"]').val(result[0].serverIp);
    $('#addOrupdateForm').find('input[name="serverPort"]').val(result[0].serverPort);
    $('#addOrupdateForm').find('input[name="sftpUsername"]').val(result[0].sftpUsername);
    $('#addOrupdateForm').find('input[name="sftpPassword"]').val(result[0].sftpPassword);
    $('#addOrupdateForm').find('input[name="classId"]').val(result[0].classId);
    $('#addOrupdateForm').find('input[name="createTimeStr"]').val(result[0].createTimeStr);
    $('#classType').val(result[0].classType).attr("disabled",true);
    $('#classFileName').val(result[0].classFileName); //文件的key 和 类型的KEY一样
    $('#classFileName').attr("readOnly",true);

    var type = result[0].classType;
    if(type==5){
        $('#httpGetDetail').show();
        $('#classFileNameDIV').hide();
        $('#domainBlack').attr("readOnly",true);
        $('#domainWrite').attr("readOnly",true);
        $('#urlBlack').attr("readOnly",true);
        var files = $('#classFileName').val();
        var fileArray = files.split(",");
        for (var i = 0 ;i<fileArray.length;i++){
            if(fileArray[i].indexOf("HFDW")>-1){ // 域白
                $('#domainWrite').val(fileArray[i]);
            }else if(fileArray[i].indexOf("HFDB")>-1){ // 域黑
                $('#domainBlack').val(fileArray[i]);
            }else if(fileArray[i].indexOf("HFUB")>-1){ // 域黑
                $('#urlBlack').val(fileArray[i]);

            }
        }
    }else {
        $('#httpGetDetail').hide();
        $('#classFileNameDIV').show();
    }

}
function detailButtonFunction(){
}
function queryParams(params){
    var data = $('#searchForm').formToJSON();
    if(data.className ==''){
        data.className =undefined;
    }
    return data;
}

function operateFormatter(value, row, index){
    return  '<a class="edit" title="点击查看策略下发状态">'+value+'</a>'
}
window.operateEvents = {

    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $('#details').modal('show');
        var detailURL = '/static/js/usergroupmanager/detailData.json?'+row.messageNo;
        detailJS.initParam(detailURL);
        detailJS.init();
        detailJS.autoSelect();

    }
};

/**
 * 为input 框赋值
 * @param selId
 * @param url
 * @param flag
 */
function loadFile(selId, url, data){
    $.ajax({
        url: url,
        type: 'GET',
        data:data,
        async: false,
        dataType: 'json',
        success: function(data){
            var $selId = $('#' + selId);
            var txt = "";
            $selId.val("");
            $.each(data, function(i, n){
                txt += n.value + ",";
            });
            txt = txt.substring(0,txt.lastIndexOf(","))
            $selId.val(txt);
            $selId.attr("title",txt)
        }
    });
}

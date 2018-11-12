
triggerJS= {

    initAddSubmit:function(){
        $('#addSubmitBut').on('click', function() {
            var formData = $('#addOrupdateForm').formToJSON();

                if(formData.triggerName.length>50 || formData.triggerName.length<1){
                    clearWarn($('#triggerName'));
                    $('#triggerName').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请输入策略名称，且最多支持50个字符</span></div>');
                    $('#triggerName').find('input[name="triggerName"]').attr('onfocus','clearWarn($(\'#triggerName\'))');
                    return false;
                }
                //触发网站
                if(formData.triggerType ==0 && formData.HostlinkId==undefined ){
                    swal("请选择需要关联的列表");
                    return false;
                }//触发关键字
                if(formData.triggerType ==2 && formData.KwlinkId==undefined ){
                    swal("请选择需要关联的列表");
                    return false;
                }//触发白名单
                if(formData.triggerType ==1 && (formData.WllinkId==undefined || formData.WllinkId=='' )){
                    swal("请选择需要关联的列表");
                    return false;
                }

                $.ajax({
                    url: '/trigger/insertOrUpdate',
                    type: 'POST',
                    data:formData,
                    success: function (data) {
                        if(data!=null){
                            if(data.result==1){
                                swal({
                                    title:"",
                                    text: "操作成功",
                                    html: false
                                }, function(isConfirm) {
                                    $('#myModaladd').modal('hide');
                                    initTableJs.refreshData();
                                });
                            }else{
                                swal(data.message);
                            }
                        }else{

                            swal("操作失败");
                        }
                    },
                    error:function () {
                        swal("操作失败");
                    }
                })
        });
    },
    searchFormSubBut:function () {
        $("#searchformbutton").on('click', function(){
            initTableJs.refreshData();
        })
    },
    initradio:function () {
      $('#ws_radio').on('change',function () {
          initSelect('ws');
      }),
      $('#kw_radio').on('change',function () {
          initSelect('kw');
      }),
      $('#wl_radio').on('change',function () {
          initSelect('wl');
      })
    },
    init:function(){
        triggerJS.searchFormSubBut();
        triggerJS.initAddSubmit();
        triggerJS.initradio();
    }
}
var detailURL= "";
triggerDetailJs= {

        initParam:function(initDetailURL){
            detailURL = initDetailURL;
        },
        refreshData:function () {
            $("#detailTable").bootstrapTable('refresh', { url: detailURL});
        },
        initDetailTable:function () {
            $('#detailTable').bootstrapTable('destroy').bootstrapTable({
                // method: 'post',
                url: detailURL,
                queryParams: detailQueryParams,
                // contentType: 'application/x-www-form-urlencoded',
                striped: true,
                undefinedText: '',
                pagination: true,
                sidePagination: 'client', //分页方式：client客户端分页，server服务端分页（*）
                iconSize: "outline",
                toolbar: "#ActButtons",
                clickToSelect:true,
                pageSize: 10,
                pageList: [10, 25, 50, 100, 200],
                columns: [
                    {field: 'state',checkbox: true},
                    {field: 'deviceName',title: '设备名称'},
                    {field: 'deviceIP',title: '设备ID'},
                    {field: 'area',title: '地区'},
                    {field: 'status',title: '下发状态'},
                    {field: 'time',title: '下发时间'}
                ],
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
                triggerDetailJs.refreshData()
            })
        },
        init:function () {
            triggerDetailJs.initDetailTable();
            triggerDetailJs.initRepeatAndrefreshButton();
        }
}


$(document).ready(function() {
    initTableJs.initParam('/trigger/getTrigger','/trigger/delete.do',getColumnFunction)
    initTableJs.init();
    triggerJS.init();
});


function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'triggerId',title: '策略ID'},
        {field: 'triggerName',title: '策略名称',formatter: operateFormatter,events:operateEvents},
        {field: 'createTimeStr',title: '创建时间'},
        {field: 'status',title: '策略状态'},
        {field: 'modifyTimeStr',title: '最近修改时间'},
        {field: 'modifyOper',title: '操作账号'}
    ]
    return column;
}

function deleteFunction(url){
    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var trigger = [];

    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        $('#table>tbody>tr').remove('.selected')
        trigger.push("{'triggerId':"+item.triggerId+",'triggerFlag':" + item.triggerFlag+"}");
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{trigger:trigger},
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
function newAddButtonFunction(){

    $('#addOrupdateForm').find('input[name="triggerId"]').val('');
    $('#addOrupdateForm').find('input[name="triggerName"]').val('');
    $('#addOrupdateForm').find('input[name="createTimeStr"]').val('');
    initSelect('ws');
    $('#myModaladd').modal('show');
}
function modifyButtonFunction() {
    var result = $('#table').bootstrapTable('getSelections');
    if(result.length!=1){
        swal("请选择一条数据");
        return false;
    }

    $('#myModaladd').modal('show');
    $('#addOrupdateForm').find('input[name="triggerId"]').val(result[0].triggerId);
    $('#addOrupdateForm').find('input[name="triggerName"]').val(result[0].triggerName);
    $('#addOrupdateForm').find('input[name="triggerType"]').val(result[0].triggerType);
    $('#addOrupdateForm').find('input[name="createTimeStr"]').val(result[0].createTimeStr);

    initSelect('ws');
    var triggerType = result[0].triggerType;
    var relation =  result[0].relation;
    var array = new Array();
    if(triggerType == 0){
        // $('#ws_radio').click();
        initSelect('ws');
        for(var i = 0;i < relation.length;i++){
            array.push(relation[i].triggerHostListid);
        }
        $('#HostlinkId.selectpicker').selectpicker('val', array);
    }
    if(triggerType == 2){
        initSelect('kw');
        for(var i = 0;i < relation.length;i++){
            array.push(relation[i].triggerKwListid);
        }
        $('#KwlinkId.selectpicker').selectpicker('val', array);
    }
    if(triggerType == 1){
        initSelect('wl');
        $('#WllinkId').val(relation[0].triggerHostListid);
    }

}

function queryParams(params){
    var data = $('#searchform').formToJSON();
    if(data.triggerName ==''){
        data.triggerName =undefined;
    }

    return data;
}
function initSelect(args) {

    if(args == 'ws'){
        $('#ws_radio').click();
        $('#ws_select').show();
        $('#kw_select').hide();
        $('#wl_select').hide();
        $('#ws_radio').val(0);
        loadSelectPicker('HostlinkId','/trigger/getWSOption',false);
    }else if(args == 'kw'){
        $('#kw_radio').click();
        $('#ws_select').hide();
        $('#kw_select').show();
        $('#wl_select').hide();
        $('#kw_radio').val(2);
        loadSelectPicker('KwlinkId','/trigger/getKWOption',false);
    }else if(args == 'wl'){
        $('#wl_radio').click();
        $('#ws_select').hide();
        $('#kw_select').hide();
        $('#wl_select').show();
        $('#wl_radio').val(1);
        loadSel('WllinkId','/trigger/getWLOption',true);
    }

}


function operateFormatter(value, row, index){
    return  '<a class="edit" title="点击查看策略下发状态">'+value+'</a>'
}
window.operateEvents = {
    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $('#details').modal('show');
        var detailURL = '/static/js/usergroupmanager/detailData.json';
        triggerDetailJs.initParam(detailURL);
        triggerDetailJs.init();
        $('#detailForm').find('input[name="triggerId"]').val(row.triggerId);
    }
}

function detailQueryParams(params) {
    var data =$('#detailForm').formToJSON();
    data.page = params.offset/params.limit +1;
    data.pageSize = params.limit;
    return data;
}
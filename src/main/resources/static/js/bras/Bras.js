
$(document).ready(function() {
    initTableJs.initParam('/bras/getBras','',getColumnFunction)
    initTableJs.init();
    initSearchButton();
    initExport();
    addRolePermissionForButton();
});

function addRolePermissionForButton(){
    if ($("#zf104006_export").val()!=1){
        $("#exportData").hide();
    }
}

function getColumnFunction() {
    var column =[
        {field: 'state',checkbox: true},
        {field: 'brasIp',title: 'IP地址',formatter:function(value,row,index){
        return "<span title='"+value+"'>"+value+"</span>";
    }},
        {field: 'brasName',title: 'BRAS名称',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'firstTimeStr',title: '首次发现时间',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'lastTimeStr',title: '最近发现时间',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }}
    ]
    return column;
}
function queryParams() {
    var data = $('#searchForm').formToJSON();
    if(data.brasIp == ''){
        data.brasIp = undefined;
    }
    return data;
}
function newAddButtonFunction() {}
function modifyButtonFunction() {}
function detailButtonFunction() {}
function initSearchButton() {
    $('#searchForm').click(function () {
        initTableJs.refreshData();
    })

}
function initExport() {
    $('#exportData').click(function () {
        $('#searchForm').attr('action', '/bras/export.do').attr('method', 'get').submit() ;return false;
    })

}

//列表 DPI设备升级管理
function initUpgradeTable() {
    var upgrademanage = {
        method: 'post',
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        pagination: true,
        /*showColumns: !0,*/
        iconSize: "outline",
        sidePagination: "server",
        dataField: "list",
        url: "getDpiUpgrade",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100, 200],
        queryParamsType: 'limit',
        queryParams: queryParams,//请求服务器时所传的参数
        responseHandler: responseHandler,
        sortable: true,//是否启用排序
        sortOrder: "asc",//排序方式
        clickToSelect: false, //是否启用点击选中行
        columns: [
            /*{ // 列设置
                checkbox: true // 使用单选框
            }, */{
                field: 'DeploySiteName',
                title: '站点名称',
                formatter: function (value, row, index) {
                    return "<span title='" + value + "'>" + value + "</span>";
                }
            }, {
                field: 'SoftwareVersion',
                title: '版本号',
                formatter: function (value, row, index) {
                    return "<span title='" + value + "'>" + value + "</span>";
                }
            }, {
                field: 'update_time',
                title: '升级时间',
                formatter: function (value, row, index) {
                    return "<span title='" + value + "'>" + value + "</span>";
                }
            }]
    };
    $("#info_upgrade_table").bootstrapTable('destroy').bootstrapTable(upgrademanage);

    //请求服务数据时所传参数
    function queryParams(params) {
        return {
            //每页多少条数据
            pageSize: params.limit,
            //请求第几页
            pageIndex: params.offset / params.limit + 1,
            DeploySiteName: $("#siteName").val(),
            update_time: $("#start").val()
        }
    };

    function responseHandler(result) { //数据筛选
        if (result) {
            return {
                "list": result.rows,
                "total": result.total
            };
        } else {
            return {
                "list": [],
                "total": 0
            };
        }
    };
};

//列表 DPI设备端口扩容管理
function initExpansionTable() {
    var expansionmanage = {
        method: 'post',
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        pagination: true,
        /*showColumns: !0,*/
        iconSize: "outline",
        sidePagination: "server",
        dataField: "list",
        url: "getDpiUpgradePort",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100, 200],
        queryParamsType: 'limit',
        queryParams: queryParams,//请求服务器时所传的参数
        responseHandler: responseHandler,
        sortable: true,//是否启用排序
        sortOrder: "asc",//排序方式
        clickToSelect: false, //是否启用点击选中行
        columns: [
            /*{ // 列设置
                checkbox: true // 使用单选框
            },*/ {
                field: 'DeploySiteName',
                title: '站点名称'
            }, {
                field: 'PortNoCount',
                title: '端口数',
                formatter: function (value, row, index) {
                    return "<a href='#' onclick='getPortNum("+index+")' class='m-r fileDetail'>"+value+"</a>";
                }
            }, {
                field: 'update_time',
                title: '升级时间'
            }]
    };
    $("#info_expansion_table").bootstrapTable('destroy').bootstrapTable(expansionmanage);

    //请求服务数据时所传参数
    function queryParams(params) {
        return {
            //每页多少条数据
            pageSize: params.limit,
            //请求第几页
            pageIndex: params.offset / params.limit + 1,
            DeploySiteName: $("#siteName").val(),
            update_time: $("#start").val()
        }
    };

    function responseHandler(result) { //数据筛选
        if (result) {
            return {
                "list": result.rows,
                "total": result.total
            };
        } else {
            return {
                "list": [],
                "total": 0
            };
        }
    };
};

$(document).ready(function () {
    regesClick();
    initPeriodDateV5(2, "start", "end");
    initUpgradeTable();
    loadSel("siteName",'getDpiSiteName',{});
})

function regesClick() {
    $('#upgrade_click').click(function () {
        $('#searchform').find('#lName').html("升级时间");
        initUpgradeTable();
        $('#paramForm').find('input[name="upgradeType"]').val(1);
    });
    $('#expansion_click').click(function () {
        $('#searchform').find('#lName').html("扩容时间");
        initExpansionTable();
        $('#paramForm').find('input[name="upgradeType"]').val(0);
    });
    $('#searchformbutton').click(function () {
        var type = $('#paramForm').find('input[name="upgradeType"]').val();
        if (type == 1) {
            initUpgradeTable();
        } else if (type == 0) {
            initExpansionTable();
        }
    })
}

function getPortNum(index){

    var obj = $("#info_expansion_table").bootstrapTable('getData');
    var row = obj[index];
    var Upgrade_Id=row.Upgrade_Id;
    var DeploySiteName=row.DeploySiteName;
    var PortNoCount=row.PortNoCount;
    $('#portDetail').modal();
    $("#portList").find('.upgradePortDetail').remove();
    $.ajax({
        url: "getDpiUpgradePortDetail",
        type: 'GET',
        data:{Upgrade_Id:Upgrade_Id,DeploySiteName:DeploySiteName},
        async: false,
        dataType: 'json',
        success: function(data){
            if(data == null){
            }else{
                $("#portList").find('div[name="portItem"]:gt(0)').remove();
                data=data.rows;
                $.each(data, function(i, item){
                    $newChartDiv = $("#portList").find('#port').clone().css('width', $(window).width() - 36).addClass("upgradePortDetail");
                    $newChartDiv.attr("id","port_"+item.PortNo).show();
                    $("#portList").append($newChartDiv);
                    createEchartsById(item.PortNo,item.PortDescription);
                });
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest);
        }
    });
}


function createEchartsById(portno,portinfo) {
    var myChart = echarts.init(document.getElementById('port_'+portno));
    $.ajax({
        url: "/device/dpiDynamic/obtainEchartByPort",
        async:false,
        data:{
            portno : portno,
            portinfo : portinfo
        },
        type:'POST',
        success : function(result) {
            if(result == null){
            }else{
                option = {
                    title: result.title,
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            crossStyle: {
                                color: '#999'
                            }
                        }
                    },
                    xAxis: {
                        type: 'category',
                        data: result.axis
                    },
                    yAxis: {
                        type: 'value'
                    },
                    dataZoom: result.dataZoom,
                    series: result.series
                };
                myChart.clear();
                myChart.setOption(option);
            }
        }
    });
}

/**
 * 加载下拉框
 * selId 下拉框id
 * url 请求地址
 * flag 是否包含“请选择”选项
 * val 默认选中项值
 * cus 自定义
 */
function loadSel(selId, url, flag, data,cus,val){
    $.ajax({
        url: url,
        type: 'POST',
        data:data,
        async: false,
        dataType: 'json',
        success: function(data){
            var $selId = $('#' + selId),
                option = '';
            $selId.children().remove();
            if(flag){
                option = '<option value="" >请选择</option>';
            }
            if(cus){
                option += '<option value="0">自定义</option>';
            }
            data=data.rows;
            $.each(data, function(i, n){
                if(val == n.dpiSiteName){
                    option += '<option selected="selected" value="' + n.dpiSiteName + '">' + n.dpiSiteName+ '</option>';
                }else{
                    option += '<option value="' + n.dpiSiteName + '">' + n.dpiSiteName + '</option>';
                }
            });
            $selId.append(option);
        }
    });
}
/***********************************************************************************
 TableObject  * zTree:
 *      变量 setting: 树的设置
 *      方法 getNodes：根据类型获取树节点
 *          initTree：初始化树
 *          clickAction：初始化查询时间粒度
 *          initEchart：echarts图的加载
 *          refreshData：刷新数据
 *          initData：获取数据
 *          refreshDetail：刷新详情
 *          initDetail：初始化详情页面
 *          searchData：搜索数据
 *          init：初始化所有的方法
 **********************************************************************************
 function    *          queryParams:查询条件
 *          zTreeOnClick：ztree的点击事件
 *          detailRow:详情页面
 ********************************************************************************/

/******************* code beginning*********************************/
var TableObject = {

    setting: {
        view: {
            showIcon: true,
        },
        check: {
            enable: true
        },
        data: {
            simpleData: {
                enable: true,
                pIdKey: "pid",
            }
        },
        callback: {
            onClick: zTreeOnClick
        }
    },
    getNodes: function () {
        var nodeData;
        $.ajax({
            url: "/tree/getUploadTree",
            async: false,
            type: 'POST',
            dataType: "json",
            success: function (result) {
                nodeData = result;
            },
            error: function () {
                alert("对不起，没有数据！");
            }
        });
        return nodeData;
    },
    initTree: function () {
        $.fn.zTree.init($("#areaTree"), TableObject.setting, TableObject.getNodes()).expandAll(true);
    },
    clickAction: function () {
        $(":button[name='btnType']").click(function () {
            $(":button[name='btnType']").each(function () {
                if ($(this).hasClass("btn-success")) {
                    $(this).removeClass("btn-success").addClass("btn-white");
                }
            });
            $(this).removeClass("btn-white").addClass("btn-success");
            $("#tableType").val($(this).val());
            TableObject.refreshData();
        });
        $("#loadMore").click(function () {
            var chartListPageVal = $("#chartListPage").val();
            if (chartListPageVal == 1) {
                $("#chartListPage").val(2);
                $("#wlan").show();
                $("#im").show();
                $("#game").show();
                $("#p2p").show();
                $("#chartList").val("mychart768:mychart512:mychart513:mychart514:mychart769:mychart522:mychart521:mychart520");
                var chartArr = "mychart769:mychart522:mychart521:mychart520".split(":");
                TableObject.initEchart(undefined, chartArr);


            } else if (chartListPageVal == 2) {
                $("#chartListPage").val(3);
                $("#dns").show();
                $("#imap").show();
                $("#pop3").show();
                $("#smtp").show();
                $("#chartList").val("mychart768:mychart512:mychart513:mychart514:mychart769:mychart522:mychart521:mychart520:mychart519:mychart518:mychart517:mychart516");
                var chartArr = "mychart519:mychart518:mychart517:mychart516".split(":");
                TableObject.initEchart(undefined, chartArr);

            } else if (chartListPageVal == 3) {
                $("#chartListPage").val(4);
                $("#ftp").show();
                $("#loadMore").hide();
                $("#chartList").val("mychart768:mychart512:mychart513:mychart514:mychart769:mychart522:mychart521:mychart520:mychart519:mychart518:mychart517:mychart516:mychart515");
                var chartArr = "mychart515".split(":");
                TableObject.initEchart(undefined, chartArr);
            }
        })
    },
    initOneChart: function (jsonData, currChartDiv) {
        if (jsonData == undefined) {
            jsonData = $("#searchForm").formToJSON();
        }
        var $currChartDiv = $('#' + currChartDiv);
        var myChart = echarts.init($currChartDiv[0]);
        jsonData.fileType = $currChartDiv.attr("value");
        $.ajax({
            url: "getChart",
            async: false,
            data: jsonData,
            type: 'POST',
            success: function (result) {
                if (result == null) {
                    myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
                } else {
                    option = {
                        /* title: result.title,*/
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: {
                                type: 'cross',
                                crossStyle: {
                                    color: '#999'
                                }
                            }
                        },
                        toolbox: {
                            show: false,
                            feature: {
                                dataView: {show: true, readOnly: false},
                                magicType: {show: true, type: ['line', 'bar']},
                                restore: {show: true},
                                saveAsImage: {show: true}
                            }
                        },
                        legend: {
                            data: result.legend
                        },
                        xAxis: {
                            type: 'category',
                            data: result.axis,
                            axisPointer: {
                                type: 'shadow'
                            }
                        },
                        yAxis: [
                            {
                                type: 'value',
                                name: '接收文件总大小',
                                axisLabel: {
                                    formatter: '{value} ' + result.unit
                                }
                            },
                            {
                                type: 'value',
                                name: '接收文件数(个)',
                                axisLabel: {
                                    formatter: '{value} '
                                }
                            }
                        ],
                        dataZoom: result.dataZoom,
                        series: result.series
                    };

                    myChart.setOption(option);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }

        });
    },
    initEchart: function (jsonData, chartArr) {
        if (jsonData == undefined) {
            jsonData = $("#searchForm").formToJSON();
        }
        if (chartArr == undefined) {
            chartArr = $('#chartList').val().split(":");
        }
        for (var i = 0; i < chartArr.length; i++) {
            TableObject.initOneChart(jsonData, chartArr[i]);
        }
    },
    refreshData: function () {
        var data = $("#searchForm").formToJSON();
        TableObject.initEchart(data);
    },
    initData: function (fileType) {
        //主页列表初始化
        var listInit = {
            url: "list?" + Math.random(),
            method: 'post',
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            pagination: true,
            showColumns: false,  //显示下拉框勾选要显示的列
            toolbar: "#table-toolbar",
            iconSize: "outline",
            sidePagination: "server",
            dataField: "list",
            pageNumber: 1,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            queryParamsType: 'limit',
            queryParams: function (params) {
                return {
                    //每页多少条数据
                    pageSize: params.limit,
                    //请求第几页
                    pageIndex: params.offset / params.limit + 1,
                    //form表单的数据
                    startTime: $('#start').val(),
                    endTime: $('#end').val(),
                    fileType: fileType,
                    tableType: $('#tableType').val(),
                    level: $('#level').val(),
                    probeType: $('#probeType').val(),
                    areaId: $('#areaId').val(),
                    softwareProvider: $('#softwareProvider').val()
                }
            },//请求服务器时所传的参数
            responseHandler: function (result) {
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
            },
            striped: true,  //是否行间隔色显示
            sortable: true,//是否启用排序
            sortOrder: "asc",//排序方式
            clickToSelect: false, //是否启用点击选中行
            onLoadSuccess: function () {
                $('.bootstrap-table tr td').each(function () {
                    $(this).attr("title", $(this).text());
                    $(this).css("cursor", 'pointer');
                });
            },
            columns: [
                { // 列设置
                    field: 'receivedTime',
                    title: '时间'
                }, {
                    field: 'receivedFileSizeStr',
                    title: '接收文件总大小', formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }, {
                    field: 'receivedFileNum',
                    title: '接收文件数(个)',
                    formatter: function (value, row, index) {
                        if (value <= 0) {
                            return "0";
                        } else {
                            return "<a href='#' title='文件详情'  onclick='detailRow(" + index + ")'  class='m-r fileDetail'>" + value + "</a>";
                        }
                    }
                }
            ]
        };
        $("#table").bootstrapTable('destroy').bootstrapTable(listInit);
    },
    refreshDetail: function (jsonObj) {
        $("#detailTable").bootstrapTable('refresh', {url: "listDetail", query: jsonObj});
    },
    initDetail: function (jsonObj) {
        var listInit = {
            url: "listDetail",
            method: 'post',
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            pagination: true,
            showColumns: false,  //显示下拉框勾选要显示的列
            toolbar: "#table-toolbar",
            iconSize: "outline",
            sidePagination: "server",
            dataField: "list",
            pageNumber: 1,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            queryParamsType: 'limit',
            queryParams: function (params) {
                return {
                    //每页多少条数据
                    pageSize: params.limit,
                    //请求第几页
                    pageIndex: params.offset / params.limit + 1,
                    //form表单的数据
                    statTime: jsonObj.statTime,
                    tableType: jsonObj.tableType,
                    fileType: jsonObj.fileType,
                    probeType: jsonObj.probeType,
                    areaId: jsonObj.areaId,
                    softwareProvider: jsonObj.softwareProvider,
                    order: params.order,
                    sort: params.sort
                }
            },//请求服务器时所传的参数
            responseHandler: function (result) {
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
            },
            striped: true,  //是否行间隔色显示
            sortable: true,//是否启用排序
            sortOrder: "asc",//排序方式
            clickToSelect: false, //是否启用点击选中行
            onLoadSuccess: function () {
                $('.bootstrap-table tr td').each(function () {
                    $(this).attr("title", $(this).text());
                    $(this).css("cursor", 'pointer');
                });
            },
            columns: [
                { // 列设置
                    field: 'fileName',
                    title: '<span title="文件名称">文件名称</span>',
                    formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    },
                    width: 410
                }, {
                    field: 'dpiIp',
                    title: '<span title="DPI服务器">DPI服务器</span>',
                    formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }, {
                    field: 'softwareProvider',
                    title: '<span title="来源厂家">来源厂家</span>',
                    formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    },
                    width: 50
                }, {
                    field: 'fileSizeStr',
                    title: '<span title="接收文件大小(KB)">接收文件大小(KB)</span>',
                    formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }, {
                    field: 'uploadFileSize',
                    title: '<span title="上报文件大小(KB)">上报文件大小(KB)</span>',
                    formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }, {
                    field: 'uploadIp',
                    title: '<span title="上报服务器">上报服务器</span>',
                    formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }, {
                    field: 'filereceivedTime',
                    title: '<span title="文件接收时间">文件接收时间</span>',
                    formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }, {
                    field: 'filecreateTime',
                    sortable: true,
                    title: '<span title="文件生成时间">文件生成时间</span>',
                    formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }, {
                    field: 'fileuploadTime',
                    title: '<span title="文件上报时间">文件上报时间</span>',
                    formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }, {
                    field: 'warnType',
                    title: '<span title="文件异常描述">文件异常描述</span>',
                    formatter: operatingFormatter
                }
            ]
        };
        $("#detailTable").bootstrapTable('destroy').bootstrapTable(listInit);
    },
    //查询
    searchData: function () {
        $("#searchFormButton").click(function () {
            TableObject.refreshData();
        });
    },
    buttonInit: function () {
        $('#httpGetDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(768);
        });
        $('#httpDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(512);
        });
        $('#rtspDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(513);
        });
        $('#voipDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(514);
        });
        $('#ftpDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(515);
        });
        $('#smtpDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(516);
        });
        $('#pop3DataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(517);
        });
        $('#imapDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(518);
        });
        $('#dnsDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(519);
        });
        $('#p2pDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(520);
        });
        $('#gameDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(521);
        });
        $('#imDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(522);
        });
        $('#wlanDataDetail').click(function () {
            $('#moreDetail').modal('show');
            var data = $("#searchForm").formToJSON();
            TableObject.initData(769);
        });
    },
    /*****************zTree code ---end********************************/
    //初始化
    init: function () {
        initDate(3, true, false);
        TableObject.initTree();
        TableObject.searchData();
        TableObject.initEchart(undefined, undefined);
        TableObject.clickAction();
        TableObject.buttonInit();
    }
};

$(document).ready(function () {
    //目录树的固定宽度转化为百分比来计算
    var w = $(window).width();
    var h = $(window).height();
    $('#treeWidthFixed').css('width', (220 / w) * 100 + '%');
    //计算目录树的高度
    // $('#areaTree').css('height', h - 65);
    $('#areaTree').css('height', h);
    //右侧内容所占百分比
    $('#containWidthFixed').css('width', (1 - (240 / w)) * 100 + '%');
    TableObject.init();
});

//树点击的回调函数
function zTreeOnClick(event, treeId, treeNode) {
    if (treeNode.level == 0) {//全省
        $("#level").val(treeNode.level);
        TableObject.refreshData();
    } else if (treeNode.level == 1) {//DPI、EU
        $("#level").val(treeNode.level);
        $("#probeType").val(treeNode.probeType);
        TableObject.refreshData();
    } else if (treeNode.level == 2) {//区域、机房
        $("#level").val(treeNode.level);
        $("#probeType").val(treeNode.probeType);
        $("#areaId").val(treeNode.id);
        TableObject.refreshData();
    } else if (treeNode.level == 3) {//厂商
        var id = treeNode.id;
        $("#level").val(treeNode.level);
        $("#probeType").val(treeNode.probeType);
        $("#areaId").val(treeNode.pid);
        $("#softwareProvider").val(id.split("_")[1]);
        TableObject.refreshData();
    }
}

function detailRow(index) {
    var obj = $("#table").bootstrapTable('getData');
    var sftp = obj[index];
    var tableType = $('#tableType').val();
    $('#statTime').val(sftp.statTime);
    var jsonObj = {
        statTime: sftp.statTime,
        tableType: tableType,
        areaId: sftp.areaId,
        fileType: sftp.fileType,
        probeType: sftp.probeType,
        softwareProvider: sftp.softwareProvider
    };
    $('#detailModal').modal('show');
    //detail的初始化
    TableObject.initDetail(jsonObj);
}

//0=稽核反馈异常，1=文件接收延时，2=文件上报延时，3=文件大小不一致   ,-1=为正常文件
function operatingFormatter(value, row, index) {
    var errorType = "";
    if (Number(value) == 0) {
        errorType = "稽核反馈异常";
    } else if (Number(value) == 1) {
        errorType = "文件接收延时";
    } else if (Number(value) == 2) {
        errorType = "文件上报延时";
    } else if (Number(value) == 3) {
        errorType = "文件大小不一致";
    }
    return '<span title="' + errorType + '">' + errorType + '</span>';
}
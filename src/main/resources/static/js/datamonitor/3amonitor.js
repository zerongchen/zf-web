    /***********************************************************************************
 aaamonitor	 * 3A数据监控
 *  action:
 *     参数  option : ECharts 参数
 *     方法  initBar：初始化Echarts图
 *          initTable：初始化表
 *          initClick:初始化页面上的点击事件
 *
 *
 ***********************************************************************************
 function 	 *
            getColumn：表格表头
 *          queryParams：表格参数
 *          showFormatter：表格内容格式
 *          RedirectFormatter：中转发包数按钮
 *          sendFormatter：发包数按钮
 *          fileDetailFormatter：文件详情
 *          operatingevent:操作事件定义域
 *
 ********************************************************************************/

aaamonitor={
    option : {
        title : {
        },
        tooltip : {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                crossStyle: {
                    color: '#999'
                }
            }
        },
        legend: {
        },
        toolbox: {
            show : false,
            feature : {
                mark : {show: true},
                dataView : {show: true, readOnly: false},
                magicType : {show: true, type: ['line', 'bar']},
                restore : {show: true},
                saveAsImage : {show: true}
            }
        },
        calculable : true,
        grid: {
            left: '10%',
            right: '13%',
            bottom: '3%',
            containLabel: true
        },
        xAxis :
            {
                type : 'category'
            }
        ,
        yAxis: [
            {
                type: 'value',
            }
        ],
        dataZoom: [],
        series : [
        ]
    },
    option2 : {
        legend: {
        },
        xAxis: {
            type: 'category',
            data: []
        },
        tooltip: {
            trigger: 'axis'
        },
        yAxis: [
            {
                type: 'value',
                name: '在线用户数',
            }
        ],
        dataZoom: [
            {
                start:0,
                end:100

            }
        ],
        series: [{
            name:'在线用户数',
            data: [820, 932, 901, 934, 1290, 1330, 1320],
            type: 'line'
        }
        ]
    },

    initClick:function () {

        $('#searchFormButton').click(function () {

            var data = $('#searchForm').formToJSON();
            data.dateType = $('#searchForm').find('.btn-success').attr("value");
            if (data.startTime == "" || data.endTime == "") {
                swal("请选择上报时间段");
                return false;
            }
            initTableJs.refreshData();
            var statType = $('input:radio:checked').val();
            if (statType == 2) {
                aaamonitor.initBar();
            } else if (statType == 3) {
                aaamonitor.initBar2();
            }
        });

        $('#dateTypeSelect >button').click(function () {
            $("#dateTypeSelect").find(".btn-success").removeClass("btn-success").addClass('btn-white').end();
            $(this).removeClass('btn-white').addClass('btn-success').end();

            var data = $('#searchForm').formToJSON();
            data.dateType = $('#searchForm').find('.btn-success').attr("value");
            if (data.startTime == "" || data.endTime == "") {
                swal("请选择上报时间段");
                return false;
            }
            initTableJs.refreshData();
            var statType = $('input:radio:checked').val();
            if (statType == 2) {
                aaamonitor.initBar();
            } else {
                aaamonitor.initBar2();
            }

        });
        $('#dataReportType').click(function () {
            $("#dateTypeH").show();
            $("#dateTypeD").show();
            $("#dateTypeMin").removeClass("btn-success").addClass('btn-white');
            $("#dateTypeD").removeClass("btn-success").addClass('btn-white');
            $("#dateTypeH").removeClass('btn-white').addClass('btn-success').end();

            var data = $('#searchForm').formToJSON();
            data.dateType = $('#searchForm').find('.btn-success').attr("value");
            if (data.startTime == "" || data.endTime == "") {
                swal("请选择上报时间段");
                return false;
            }
            $('#userOnline').hide();
            $('#radiusDetail').show();
            aaamonitor.initBar();
        });

        $('#onlineType').click(function () {

            $("#dateTypeH").hide();
            $("#dateTypeD").hide();
            $("#staticSelect").find(".btn-success").removeClass("btn-success").addClass('btn-white').end();
            $("#dateTypeMin").removeClass('btn-white').addClass('btn-success').end();

            var data = $('#searchForm').formToJSON();
            data.dateType = $('#searchForm').find('.btn-success').attr("value");
            if (data.startTime == "" || data.endTime == "") {
                swal("请选择上报时间段");
                return false;
            }
            $('#userOnline').show();
            $('#radiusDetail').hide();
            initTableJs.refreshData();
            aaamonitor.initBar2();
        });
        $('#pcapDetail').click(function () {
            aaamonitor.initTable("pcapTable","/aaa/getRadiusPcapDetail",1);
        });
        $('#relayDetail').click(function () {
            aaamonitor.initTable("relayTable","/aaa/getRadiusRelaySecondDetail",2);
        });
        $('#policyDetail').click(function () {
            aaamonitor.initTable("policyTable","/aaa/getRadiusPolicySecondDetail",3);
        });
        $('#createFileDetail').click(function () {
            aaamonitor.initTable("createFileTable","/aaa/getFileSecondDetail",24);
        });
    }
    ,
    initBar2:function () {
        var myChart = echarts.init(document.getElementById("aaaechart"));
        var data = $('#searchForm').formToJSON();
        data.dateType=$('#searchForm').find('.btn-success').attr("value");
        //拿在线用户数据
        data.statType=0;
        $.ajax({
            url: '/aaa/getEChartsOnlineList',
            data: JSON.stringify(data),
            type: 'POST',
            async: false,
            contentType: "application/json",
            success: function (data) {
                if(data!=""){
                    aaamonitor.option2.legend.data=['在线用户数'];
                    aaamonitor.option2.series[0].name = "在线用户数";
                    aaamonitor.option2.xAxis.data=data.axis;
                    aaamonitor.option2.series[0].data = data.series[0].data;
                    myChart.setOption(aaamonitor.option2);
                }
            },
            error:function (e) {

            }
        });
        aaamonitor.initTable2();
    },
    initBar:function () {
        if(document.getElementById("radiusPcapEcharts")==null){
            return false;
        }
        let myChart1 = echarts.init(document.getElementById("radiusPcapEcharts"));
        let myChart2 = echarts.init(document.getElementById("radiusRelayEcharts"));
        let myChart3 = echarts.init(document.getElementById("radiusPolicyEcharts"));
        let myChart4 = echarts.init(document.getElementById("radiusCreateEcharts"));
        myChart1.showLoading();
        myChart2.showLoading();
        myChart3.showLoading();
        myChart4.showLoading();
        let chartArray=[myChart1,myChart2,myChart3,myChart4];
        var data = $('#searchForm').formToJSON();
        data.dateType=$('#searchForm').find('.btn-success').attr("value");
        $.ajax({
            url: '/aaa/getEChartsList',
            data: JSON.stringify(data),
            type: 'POST',
            async: false,
            contentType: "application/json",
            success: function (data) {
                if(data!=null && data.length>0){
                    for (let i=0;i<4;i++){
                        let echat = data[i];
                        let mychart = chartArray[i];
                        if (echat.series.length>0){
                            mychart.hideLoading();
                            let option1 = aaamonitor.option;
                            option1.legend.data = echat.legend;
                            option1.series= echat.series;
                            option1.xAxis.data= echat.axis;
                            option1.dataZoom = echat.dataZoom;
                            mychart.setOption(option1);
                        }
                    }
                }
            },
            error:function () {

            }
        });
    },
    initTable:function (section,url,type) {
        $('#'+section).bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: url,
            queryParams: DetailQueryParams,
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            pagination: true,
            sidePagination: 'server',
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn(type)
        });
    },
    initTable2:function () {
        $('#table').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: '/aaa/getOnlineUserList',
            queryParams: queryParams,
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            pagination: true,
            sidePagination: 'server',
            iconSize: "outline",
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn2()
        });
    },
    init:function () {
        aaamonitor.initClick();
        aaamonitor.initBar();
    }
};


$(document).ready(function() {
    loadDateType();
    aaamonitor.init();
});

function getColumn2() {
    var column =[
        {field: 'statTime',title: '时间',formatter:showFormatter},
        {field: 'onlineusernum',title: '用户实时在线人数',formatter:showFormatter},
    ];
    return column;
}

function getColumn(type) {
    let column=[];
    if(type==1){
        column=[
            {field: 'statTime',title: '时间',formatter:showFormatter},
            {field: 'capturepacketnum',title: '采集包数',formatter:showFormatter},
            {field: 'invalidpacketnum',title: '错误包数',formatter:showFormatter},
            {field: 'type',title: '操作',formatter: function (value, row, index) {
                    return "<a href='#' title='删除' ><i class='fa fa-file-text fa-lg pcapThirdDetail'></i></a>";
                }, events: relayDetailOperatingEvents}
        ]
    }else if(type==2){
        column=[
            {field: 'stat_time',title: '时间',formatter:showFormatter},
            {field: 'sendnum',title: '发送包数',formatter:showFormatter},
            {field: 'sendfailednum',title: '发送失败包数',formatter:showFormatter},
            {field: 'receivednum',title: '接收包数',formatter:showFormatter},
            {field: 'parsefailednum',title: '解析失败包数',formatter:showFormatter},
            {field: 'type',title: '操作',formatter: function (value, row, index) {
                    return "<a href='#' title='删除' ><i class='fa fa-file-text fa-lg relayThirdDetail'></i></a>";
                }, events: relayDetailOperatingEvents}
        ]
    }else if(type==3){
        column=[
            {field: 'stat_time',title: '时间',formatter:showFormatter},
            {field: 'sendnum',title: '发送包数',formatter:showFormatter},
            {field: 'sendfailednum',title: '错误包数',formatter:showFormatter},
            {field: 'type',title: '操作',formatter: function (value, row, index) {
                    return "<a href='#' title='删除' ><i class='fa fa-file-text fa-lg policyThirdDetail'></i></a>";
                }, events: relayDetailOperatingEvents}
        ]
/*
        column=[
            {field: 'statTime',title: '时间',formatter:showFormatter},
            {field: 'srcIp',title: '源IP地址',formatter:showFormatter},
            {field: 'sendnum',title: '发送包数',formatter:showFormatter},
            {field: 'sendfailednum',title: '错误包数',formatter:showFormatter},
            {field: 'dstIp',title: '目的IP地址',formatter:showFormatter},
        ]
*/
    }else if(type==24){
        column=[
            {field: 'stat_time',title: '生成时间',formatter:showFormatter},
            {field: 'file_size',title: '生成文件总大小',formatter:showFormatter},
            {field: 'file_num',title: '生成文件个数',formatter:showFormatterC},
            {field: 'warnTypeFileCount',title: '异常文件数',formatter:showFormatterW}
        ]
    }
    return column;
}

// 中转包监控详情事件
window.relayDetailOperatingEvents={
    'click .relayThirdDetail': function (e, value, row, index) {
        e.preventDefault();
        $('#relayThirdModal').modal();
        $('#relayThirdTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: "getRelayThirdDetail",
            queryParams: function (params) {
                var startTime=row.stat_time;
                var endTime="";
                var dateType=$('#searchForm').find('.btn-success').attr("value");
                if(dateType==1){
                    var crtTime = new Date(startTime+':00');
                    var s = crtTime.getTime()+300*1000;
                    endTime = dateFtt("yyyy-MM-dd hh:mm",new Date(s));
                }else if(dateType==2){
                    var crtTime = new Date(startTime+":00:00");
                    var s = crtTime.getTime()+3600*1000;
                    endTime = dateFtt("yyyy-MM-dd hh",new Date(s));
                }else if(dateType==3){
                    var crtTime = new Date(startTime+" 00:00:00");
                    var s = crtTime.getTime()+3600*24*1000;
                    endTime = dateFtt("yyyy-MM-dd",new Date(s));
                }
                return {
                    //每页多少条数据
                    pageSize: params.limit,
                    //请求第几页
                    pageIndex:params.offset/params.limit+1,
                    stat_time:row.stat_time,
                    startTime:startTime,
                    endTime:endTime,
                    dateType:$('#searchForm').find('.btn-success').attr("value"),
                    order: params.order,
                    sort: params.sort
                }
            },
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            pagination: true,
            sidePagination: 'server',
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: [
                { // 列设置
                    field: 'stat_time',
                    title: '时间', formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'src_ip',
                    title: '<span title="源服务器IP">源服务器IP</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'sendnum',
                    title: '<span title="发送包数">发送包数</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'sendfailednum',
                    title: '<span title="发送失败的包数">发送失败的包数</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'receivednum',
                    sortable:true,
                    title: '<span title="接收包数">接收包数</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'parsefailednum',
                    title: '<span title="解析失败包数">解析失败包数</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'dst_ip',
                    title: '<span title="目地服务器IP">目地服务器IP</span>',
                    formatter:function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }
            ]
        });
    },
    'click .pcapThirdDetail': function (e, value, row, index) {
        e.preventDefault();
        $('#pcapThirdModal').modal();
        $('#pcapThirdTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: "getPcapThirdDetail",
            queryParams: function (params) {
                var startTime=row.statTime;
                var endTime="";
                var dateType=$('#searchForm').find('.btn-success').attr("value");
                if(dateType==1){
                    var crtTime = new Date(startTime+':00');
                    var s = crtTime.getTime()+300*1000;
                    endTime = dateFtt("yyyy-MM-dd hh:mm",new Date(s));
                }else if(dateType==2){
                    var crtTime = new Date(startTime+":00:00");
                    var s = crtTime.getTime()+3600*1000;
                    endTime = dateFtt("yyyy-MM-dd hh",new Date(s));
                }else if(dateType==3){
                    var crtTime = new Date(startTime+" 00:00:00");
                    var s = crtTime.getTime()+3600*24*1000;
                    endTime = dateFtt("yyyy-MM-dd",new Date(s));
                }
                return {
                    //每页多少条数据
                    pageSize: params.limit,
                    //请求第几页
                    pageIndex:params.offset/params.limit+1,
                    stat_time:row.stat_time,
                    startTime:startTime,
                    endTime:endTime,
                    dateType:$('#searchForm').find('.btn-success').attr("value"),
                    order: params.order,
                    sort: params.sort
                }
            },
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            pagination: true,
            sidePagination: 'server',
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: [
                { // 列设置
                    field: 'stat_time',
                    title: '时间', formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'server_ip',
                    title: '<span title="采集服务器IP">采集服务器IP</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'capturepacketnum',
                    title: '<span title="采集包数">采集包数</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'invalidpacketnum',
                    title: '<span title="错误包数">错误包数</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }
            ]
        });
    },
    'click .policyThirdDetail': function (e, value, row, index) {
        e.preventDefault();
        $('#policyThirdModal').modal();
        $('#policyThirdTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: "getPolicyThirdDetail",
            queryParams: function (params) {
                var startTime=row.stat_time;
                var endTime="";
                var dateType=$('#searchForm').find('.btn-success').attr("value");
                if(dateType==1){
                    var crtTime = new Date(startTime+':00');
                    var s = crtTime.getTime()+300*1000;
                    endTime = dateFtt("yyyy-MM-dd hh:mm",new Date(s));
                }else if(dateType==2){
                    var crtTime = new Date(startTime+":00:00");
                    var s = crtTime.getTime()+3600*1000;
                    endTime = dateFtt("yyyy-MM-dd hh",new Date(s));
                }else if(dateType==3){
                    var crtTime = new Date(startTime+" 00:00:00");
                    var s = crtTime.getTime()+3600*24*1000;
                    endTime = dateFtt("yyyy-MM-dd",new Date(s));
                }
                return {
                    //每页多少条数据
                    pageSize: params.limit,
                    //请求第几页
                    pageIndex:params.offset/params.limit+1,
                    stat_time:row.stat_time,
                    startTime:startTime,
                    endTime:endTime,
                    dateType:$('#searchForm').find('.btn-success').attr("value"),
                    order: params.order,
                    sort: params.sort
                }
            },
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            pagination: true,
            sidePagination: 'server',
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: [
                { // 列设置
                    field: 'stat_time',
                    title: '时间', formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'src_ip',
                    title: '<span title="采集服务器IP">采集服务器IP</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'sendnum',
                    title: '<span title="发送包数">发送包数</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'sendfailednum',
                    title: '<span title="错误包数">错误包数</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'dst_ip',
                    title: '<span title="目地服务器IP">目地服务器IP</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                }
            ]
        });
    }
}

function showFormatter(value, row, index) {
        if(value==null){
            return "-";
        }
        return value;
}
function showFormatterW(value, row, index) {
    if(value==null||value==0){return "0";}
    var stat_time = "'"+row['stat_time']+"'";
    var createFileBtn = '<a href="#" onclick="gotoWarnTypeFileCountPage('+stat_time+')" >'+value+'<i class="fa fa-lg"></i></a>' ;
    return createFileBtn;
}

function showFormatterC(value, row, index) {
    if(value==null){return "-";}
    if(value==0){return "0";}
    var stat_time = "'"+row['stat_time']+"'";
    var createFileBtn = '<a href="#" onclick="gotoCreateFileDetailPage('+stat_time+')" >'+value+'<i class="fa fa-lg"></i></a>' ;
    return createFileBtn;
}
    // 生成文件监控 - 获取异常文件数详情
    function gotoWarnTypeFileCountPage(stat_time) {
        $('#createFileThirdModal').modal('show');
        $('#createFileThirdTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: "getWarnFileThirdDetail",
            queryParams: function (params) {
                var startTime=stat_time;
                var endTime="";
                var dateType=$('#searchForm').find('.btn-success').attr("value");
                if(dateType==1){
                    var crtTime = new Date(startTime+':00');
                    var s = crtTime.getTime()+300*1000;
                    endTime = dateFtt("yyyy-MM-dd hh:mm",new Date(s));
                }else if(dateType==2){
                    var crtTime = new Date(startTime+":00:00");
                    var s = crtTime.getTime()+3600*1000;
                    endTime = dateFtt("yyyy-MM-dd hh",new Date(s));
                }else if(dateType==3){
                    var crtTime = new Date(startTime+" 00:00:00");
                    var s = crtTime.getTime()+3600*24*1000;
                    endTime = dateFtt("yyyy-MM-dd",new Date(s));
                }
                return {
                    //每页多少条数据
                    pageSize: params.limit,
                    //请求第几页
                    pageIndex:params.offset/params.limit+1,
                    stat_time:stat_time,
                    startTime:startTime,
                    endTime:endTime,
                    dateType:$('#searchForm').find('.btn-success').attr("value"),
                    fileType:1023,
                    order: params.order,
                    sort: params.sort
                }
            },
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            pagination: true,
            sidePagination: 'server',
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: [
                { // 列设置
                    field: 'file_name',
                    title: '文件名', formatter: function (value, row, index) {
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'cfileSize',
                    title: '<span title="生成文件大小(KB)">生成文件大小(KB)</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'ufileSize',
                    title: '<span title="上报文件大小(KB)">上报文件大小(KB)</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'server_ip',
                    title: '<span title="上报服务器">上报服务器</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'cfileTime',
                    sortable:true,
                    title: '<span title="文件生成时间">文件生成时间</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'ufileTime',
                    title: '<span title="文件上报时间">文件上报时间</span>', formatter: function (value, row, index) {
                        if(value == undefined){
                            value=""
                        }
                        return "<span title='" + value + "'>" + value + "</span>";
                    }
                },{
                    field: 'warn_type',
                    title: '<span title="文件异常描述">文件异常描述</span>',
                    formatter:warnShowFormatter
                }
            ]
        });
    }

// 生成文件监控 - 获取生成文件数详情
function gotoCreateFileDetailPage(stat_time) {
    $('#createFileThirdModal').modal('show');
    $('#createFileThirdTable').bootstrapTable('destroy').bootstrapTable({
        method: 'post',
        url: "getFileThirdDetail",
        queryParams: function (params) {
            var startTime=stat_time;
            var endTime="";


            var dateType=$('#searchForm').find('.btn-success').attr("value");
            if(dateType==1){
                var crtTime = new Date(startTime+':00');
                var s = crtTime.getTime()+300*1000;
                endTime = dateFtt("yyyy-MM-dd hh:mm",new Date(s));
            }else if(dateType==2){
                var crtTime = new Date(startTime+":00:00");
                var s = crtTime.getTime()+3600*1000;
                endTime = dateFtt("yyyy-MM-dd hh",new Date(s));
            }else if(dateType==3){
                var crtTime = new Date(startTime+" 00:00:00");
                var s = crtTime.getTime()+3600*24*1000;
                endTime = dateFtt("yyyy-MM-dd",new Date(s));
            }
            return {
                //每页多少条数据
                pageSize: params.limit,
                //请求第几页
                pageIndex:params.offset/params.limit+1,
                stat_time:stat_time,
                startTime:startTime,
                endTime:endTime,
                dateType:$('#searchForm').find('.btn-success').attr("value"),
                fileType:1023,
                order: params.order,
                sort: params.sort
            }
        },
        contentType: 'application/x-www-form-urlencoded',
        striped: true,
        undefinedText: '',
        pagination: true,
        sidePagination: 'server',
        clickToSelect:true,
        pageSize: 10,
        pageList: [10, 25, 50, 100, 200],
        columns: [
            { // 列设置
                field: 'file_name',
                title: '文件名', formatter: function (value, row, index) {
                    return "<span title='" + value + "'>" + value + "</span>";
                }
            },{
                field: 'cfileSize',
                title: '<span title="生成文件大小(KB)">生成文件大小(KB)</span>', formatter: function (value, row, index) {
                    return "<span title='" + value + "'>" + value + "</span>";
                }
            },{
                field: 'ufileSize',
                title: '<span title="上报文件大小(KB)">上报文件大小(KB)</span>', formatter: function (value, row, index) {
                    if(value == undefined){
                        value=""
                    }
                    return "<span title='" + value + "'>" + value + "</span>";
                }
            },{
                field: 'server_ip',
                title: '<span title="上报服务器">上报服务器</span>', formatter: function (value, row, index) {
                    return "<span title='" + value + "'>" + value + "</span>";
                }
            },{
                field: 'cfileTime',
                sortable:true,
                title: '<span title="文件生成时间">文件生成时间</span>', formatter: function (value, row, index) {
                    return "<span title='" + value + "'>" + value + "</span>";
                }
            },{
                field: 'ufileTime',
                title: '<span title="文件上报时间">文件上报时间</span>', formatter: function (value, row, index) {
                    return "<span title='" + value + "'>" + value + "</span>";
                }
            },{
                field: 'warn_type',
                title: '<span title="文件异常描述">文件异常描述</span>',
                formatter:warnShowFormatter
            }
        ]
    });
}


function warnShowFormatter(value, row, index) {
    if(Number(value)==0){
        return "-";
    }else if(Number(value)==1){
        return "文件接收延时";
    }else if(Number(value)==2){
        return "文件上报延时";
    }else if(Number(value)==3){
        return "文件大小不一致";
    }else return "";
}

function queryParams(params) {
    var data = $('#searchForm').formToJSON();
    data.dateType=$('#searchForm').find('.btn-success').attr("value");
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    data.statType=0;
    return data;
}
function DetailQueryParams(params) {
    var data = $('#searchForm').formToJSON();
    data.dateType=$('#searchForm').find('.btn-success').attr("value");
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    return data;
}
function loadDateType() {
   initDate(3,true,false);
}
    function dateFtt(fmt,date)
    { //author: meizz
        var o = {
            "M+" : date.getMonth()+1,                 //月份
            "d+" : date.getDate(),                    //日
            "h+" : date.getHours(),                   //小时
            "m+" : date.getMinutes(),                 //分
            "s+" : date.getSeconds(),                 //秒
            "q+" : Math.floor((date.getMonth()+3)/3), //季度
            "S"  : date.getMilliseconds()             //毫秒
        };
        if(/(y+)/.test(fmt))
            fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));
        for(var k in o)
            if(new RegExp("("+ k +")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        return fmt;
    }
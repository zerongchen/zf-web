/***********************************************************************************
 apptraffic	 * 流量流向
 *  action:
 *     参数  option : ECharts 参数
 *     方法  initD3：初始化sankey图
 *          initTable：初始化表
 *          initClick:初始化页面上的点击事件
 *
 *
 ***********************************************************************************
 appdetailTraffic	 * 流量流向

 *  action:
 *     参数  option : ECharts bar 参数
 *     参数  optionLine : ECharts line 参数
 *     方法  initClickAct：初始化页面上的点击事件
 *          initRankingBar 初始排名柱形图
 *          initTrendLine  初始趋势图
 *          initRankingTable 初始排名表
 *          initTrendTable 初始趋势表
 *          initRank 初始排名页面
 *          initTrend 初始趋势页面
 *
 *
 ***********************************************************************************
 function 	 *
            getColumn：表格表头
 *          queryParams：表格参数
 *          showFormatter：表格内容格式
 *          operatingevent:操作事件定义域
 *
 ********************************************************************************/
var allChart=new Array();
// var allChartChild=new Array();
var cos = {};
apptraffic={

    option:{
        title: {
            text: ''
        },
        tooltip: {
            trigger: 'item',
                triggerOn: 'mousemove'
        },
        series: [
            {
                type: 'sankey',
                layout: 'none',
                // data: [],
                // links: [],
                itemStyle: {
                    normal: {
                        borderWidth: 1,
                        borderColor: '#aaa'
                    }
                },
                lineStyle: {
                    normal: {
                        color: 'target',
                        curveness: 0.5
                    }
                }
            }
        ]
    },
    initClick:function () {

      $('#searchFormButton').click(function () {

          var data = $('#searchForm').formToJSON();
          data.dateType=$('#searchForm').find('.btn-success').attr("value");
          if(data.stateTime=="" || data.stateTime==undefined){
            swal("请选择时间");
            return false;
          }
          apptraffic.refreshData();
          apptraffic.initSankey();

      });
      
      $('#dateTypeSelect >button').click(function () {
          $("#dateTypeSelect").find(".btn-success").removeClass("btn-success").addClass('btn-white').end();
          $(this).removeClass('btn-white').addClass('btn-success').end();

          var data = $('#searchForm').formToJSON();
          data.dateType=$('#searchForm').find('.btn-success').attr("value");
          loadDateType(data.dateType);

          if(data.stateTime=="" || data.stateTime==undefined){
              swal("请选择时间");
              return false;
          }
          apptraffic.initSankey();
          apptraffic.refreshData();
          // initStateDate(data.dateType);

      });
    },
    initSankey:function () {

        $('#noDataWarn').remove();
        if(document.getElementById('echart')==null){
            return false;
        }
        let myChart = echarts.init(document.getElementById('echart'));
        myChart.showLoading();
        myChart.off('click');
        myChart.on('click', function (params) {
            if(params.data.sourceVal!=undefined){
                initChilePage(params.data);
            }
        });
        let data = $('#searchForm').formToJSON();
        data.dateType=$('#searchForm').find('.btn-success').attr("value");
        if(data.appType=='-1'){
            data.appType=undefined;
        }
        if(data.stateTime==''){
            data.stateTime=undefined;
        }
        data.sort=undefined;
        data.order=undefined;
        $.ajax({
            url: "/analysis/apptraffic/getAreaSankey",
            type: 'POST',
            data: JSON.stringify(data),
            dataType: 'json',
            contentType: "application/json",
            success: function (data) {
                if(data!=null && data!=""){
                    myChart.hideLoading();
                    $('#noDataWarn').remove();
                    myChart.clear();
                    apptraffic.option.series[0].links=data.links;
                    apptraffic.option.series[0].data=data.nodes;
                    let pageType = $('#searchForm').find('input[name="pageType"]').val();
                    if(pageType==1){
                        apptraffic.option.series[0].lineStyle.normal.color='target';
                    }else {
                        apptraffic.option.series[0].lineStyle.normal.color='source';
                    }
                    myChart.setOption(apptraffic.option,true);
                    allChart=[myChart];
                }else {
                    myChart.hideLoading();
                    myChart.clear();
                   /* $('#echart').append('<span id="noDataWarn" style="color:gray;text-align: center;display: block;">暂 无 数 据</span>');*/
                }
            },
            error:function () {
                myChart.hideLoading();
                // $('#echart').children().remove();
                myChart.clear();
               /* $('#echart').append('<span id="noDataWarn" style="color:gray;text-align: center;display: block;">暂 无 数 据</span>');*/
            }
        });

    },
    initTable:function () {
        $('#table').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: '/analysis/apptraffic/getMainList',
            queryParams: queryParams,
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            //showColumns: !0,
            pagination: true,
            sidePagination: 'server',
            sortable: true,//是否启用排序
            sortOrder: "desc",//排序方式
            //iconSize: "outline",
            //icons: {
            //   columns: "glyphicon-list",
            // },
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn(),
            onLoadSuccess:function () {
                let column = getColumn();
                let coos=new Array();
                let field= new Array();
                $.each(column,function (i, n) {
                    coos.push(n.title);
                    field.push(n.field)
                });
                cos.headers=coos;
                cos.fields=field;
            }
        });
    },
    refreshData:function(){
        $("#table").bootstrapTable('refresh', { url: "/analysis/apptraffic/getMainList"});
    },
    init:function () {
        loadDateType(2);
        loadSel('apptype','/select/getAppType',true,undefined,false);
        apptraffic.initSankey();
        apptraffic.initClick();
        apptraffic.initTable();
    }
};
appdetailTraffic={
    option : {
        tooltip : {
            trigger: 'axis',
            axisPointer : {
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        legend: {
            data:[]
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                data : []
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : []
    },
    optionLine: {
        title: {
            text: '',
            subtext: '',
            x: 'center'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false
            }
        },
        legend: {
            data:[],
            x: 'left'
        },
        toolbox: {
            show : false,
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        axisPointer: {
            link: {xAxisIndex: 'all'}
        },
        dataZoom: [
            {
                show: true,
                realtime: true,
                start: 30,
                end: 70,
                xAxisIndex: [0, 1]
            },
            {
                type: 'inside',
                realtime: true,
                start: 30,
                end: 70,
                xAxisIndex: [0, 1]
            }
        ],
        grid: [{
            left: 50,
            right: 50,
            height: '35%'
        }, {
            left: 50,
            right: 50,
            top: '55%',
            height: '35%'
        }],
        xAxis : [
            {
                type : 'category',
                boundaryGap : false,
                axisLine: {onZero: true},
                data: []
            },
            {
                gridIndex: 1,
                type : 'category',
                boundaryGap : false,
                axisLine: {onZero: true},
                data: [],
                position: 'top'
            }
        ],
        yAxis : [
            {
                name : '上行流量(G)',
                type : 'value',
            },
            {
                gridIndex: 1,
                name : '下行流量(G)',
                type : 'value',
                inverse: true
            }
        ],
        series : []
    },
    initClickAct:function () {
        $('#appDateTypeSelect >button').click(function () {
            $("#appDateTypeSelect").find(".btn-success").removeClass("btn-success").addClass('btn-white').end();
            $(this).removeClass('btn-white').addClass('btn-success').end();

            var data = $('#appSearchForm').formToJSON();
            data.dateType=$('#appSearchForm').find('.btn-success').attr("value");

            if(data.type==2){
                initPeriodDateV5(data.dateType,"start","end");
                initRankTime();
                $('#end').val('');
                appdetailTraffic.initRank();
            }else {
                initPeriodDateV5(data.dateType,"start","end");
                iniTrendTime();
                appdetailTraffic.initTrend();
            }
        });
        $("#returnToMain").click(function () {
            $('#main').show();
            $('#child').hide();
            apptraffic.refreshData();
            apptraffic.initSankey();
        });
        $('#trendAct').click(function () {
            $('#appSearchForm').find('input[name="type"]') .val(1);
            $('#end').attr("type","");
            $('#start').attr("placeholder","开始时间");
            iniTrendTime();
            appdetailTraffic.initTrend();
        });
        $('#rankAct').click(function () {
            $('#appSearchForm').find('input[name="type"]') .val(2);
            $('#start').attr("placeholder","统计时间");
            initRankTime();
            $('#end').attr("type","hidden");
            appdetailTraffic.initRank();
        });
        $("#appSearchFormButton").click(function () {
            let data = $('#appSearchForm').formToJSON();
            if(data.type==1){
                appdetailTraffic.initTrend();
            }else if(data.type==2){
                appdetailTraffic.initRank();
            }

        })
    },
    initRankingBar:function () {

        let myChart = echarts.init(document.getElementById('appRankingEchart'));
        myChart.showLoading();
        myChart.off('click');

        let data = $('#appSearchForm').formToJSON();
        data.dateType=$('#appSearchForm').find('.btn-success').attr("value");
        if(data.startTime==""){
            data.startTime=undefined;
        }
        if(data.appType=='-1') {
            data.appType = undefined;
        }
        data.sort=undefined;
        data.order=undefined;
        $.ajax({
            url: "/analysis/apptraffic/getEchartsRankingData",
            type: 'POST',
            data: JSON.stringify(data),
            dataType: 'json',
            contentType: "application/json",
            success: function (result) {
                myChart.hideLoading();
                myChart.clear();
                myChart.resize();
                appdetailTraffic.option.series=result.series;
                appdetailTraffic.option.series[0].itemStyle={normal:{color:["#eb547c"]}};
                appdetailTraffic.option.series[1].itemStyle={normal:{color:["#2ec7c9"]}};

                appdetailTraffic.option.legend.data=result.legend;
                appdetailTraffic.option.xAxis[0].data=result.axis;
                myChart.setOption(appdetailTraffic.option);
                allChart=[myChart];
            },
            error:function () {
                myChart.hideLoading();
                myChart.clear();
            }
        });
    },
    initTrendLine:function () {

        let vChart = echarts.init(document.getElementById('appTrendEchart'));
        vChart.showLoading();
        let data = $('#appSearchForm').formToJSON();
        data.dateType=$('#appSearchForm').find('.btn-success').attr("value");
        if(data.appType=='-1') {
            data.appType = undefined;
        }
        if(data.endTime==""){
            if(data.startTime!=""){
                swal("请选择结束时间");
                return false;
            }
        }
        data.sort=undefined;
        data.order=undefined;
        $.ajax({
            url: "/analysis/apptraffic/getEchartsTrendData",
            type: 'POST',
            data: JSON.stringify(data),
            dataType: 'json',
            contentType: "application/json",
            success: function (result) {
                vChart.hideLoading();
                // vChart.clear();
                appdetailTraffic.optionLine.series=result.series;
                appdetailTraffic.optionLine.legend.data=result.legend;
                appdetailTraffic.optionLine.xAxis[0].data=result.axis;
                appdetailTraffic.optionLine.xAxis[1].data=result.axis;
                vChart.resize();
                vChart.setOption(appdetailTraffic.optionLine,true);
                allChart=[vChart];
            },
            error:function () {
                vChart.hideLoading();
                vChart.clear();
            }
        });
    },
    initRankingTable:function () {

        $('#appRankingTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: '/analysis/apptraffic/getTableRankingData',
            queryParams: function (params) {
                let data = $('#appSearchForm').formToJSON();
                data.dateType = $('#appSearchForm').find('.btn-success').attr("value");
                data.page = params.offset/params.limit +1 ;
                data.pageSize = params.limit;
                data.sort=params.sort;
                data.order=params.order;
                if(data.stateTime==""){
                    data.stateTime=undefined;
                }
                if(data.appType=='-1') {
                    data.appType = undefined;
                }
                return data;
            },
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            // showColumns: !0,
            sortable: true,//是否启用排序
            sortOrder: "desc",//排序方式
            pagination: true,
            sidePagination: 'server',
            // iconSize: "outline",
            // icons: {
            //     columns: "glyphicon-list",
            // },
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: [
                {field: 'srcAreaDesc',title: '源区域'},
                {field: 'dstAreaDesc',title: '目的区域'},
                {field: 'appName',title: '应用子分类'},
                {field: 'flowUp',sortable:true,title: '上行流量(G)'},
                {field: 'flowDn',sortable:true,title: '下行流量(G)'},
                {field: 'totalFlow',sortable:true,title: '总流量(G)'},
            ],
            onLoadSuccess:function () {
                cos.headers=['源区域','目的区域','应用子分类','上行流量(G)','下行流量(G)','总流量(G)'];
                cos.fields=['srcAreaDesc','dstAreaDesc','appName','flowUp','flowDn','totalFlow'];
            }
        });

    },
    initTrendTable:function () {

        $('#appTrendTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: '/analysis/apptraffic/getTableTrendData',
            queryParams: function (params) {
                let data = $('#appSearchForm').formToJSON();
                data.dateType = $('#appSearchForm').find('.btn-success').attr("value");
                data.page = params.offset/params.limit +1 ;
                data.pageSize = params.limit;
                data.sort=params.sort;
                data.order=params.order;
                if(data.stateTime==""){
                    data.stateTime=undefined;
                }
                if(data.appType=='-1') {
                    data.appType = undefined;
                }
                return data;
            },
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            // showColumns: !0,
            sortable: true,//是否启用排序
            sortOrder: "desc",//排序方式
            pagination: true,
            sidePagination: 'server',
            // iconSize: "outline",
            // icons: {
            //     columns: "glyphicon-list",
            // },
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: [
                {field: 'stateTime',sortable:true,title: '统计时间'},
                {field: 'srcAreaDesc',title: '源区域'},
                {field: 'dstAreaDesc',title: '目的区域'},
                {field: 'appName',title: '应用子分类'},
                {field: 'flowUp',sortable:true,title: '上行流量(G)'},
                {field: 'flowDn',sortable:true,title: '下行流量(G)'},
                {field: 'totalFlow',sortable:true,title: '总流量(G)'},
            ],
            onLoadSuccess:function () {
                cos.headers=['统计时间','源区域','目的区域','应用子分类','上行流量(G)','下行流量(G)','总流量(G)'];
                cos.fields=['stateTime','srcAreaDesc','dstAreaDesc','appName','flowUp','flowDn','totalFlow'];
            }
        });

    },
    initRank:function () {
        appdetailTraffic.initRankingBar();
        appdetailTraffic.initRankingTable();
    },
    initTrend:function () {
        appdetailTraffic.initTrendLine();
        appdetailTraffic.initTrendTable();

    }
};

$(document).ready(function() {
    $('#main').show();
    apptraffic.init();
    appdetailTraffic.initClickAct();
    // initDate(1,false,true);
    // initPeriodDateV5
    initExportClick("searchForm",'export',$('#searchForm').find('.btn-success').attr("value"));
    initExportClick("appSearchForm",'exportChild',$('#searchForm').find('.btn-success').attr("value"));
});

function getColumn() {
    let column =[];
    let pageType = $('#searchForm').find('input[name="pageType"]').val();
    if(Number(pageType)==1){
        column=[
            {field: 'target',title: '省份|运营商',formatter:RedirectFormatter,events:operatingevent},
            {field: 'flowUp',title: '上行流量（G）',sortable:true,formatter:showFormatter},
            {field: 'flowDn',title: '下行流量（G）',sortable:true,formatter:showFormatter},
            {field: 'totalFlow',title: '总流量(G)',sortable:true,formatter:showFormatter},
        ];
    }else {
        column=[
            {field: 'source',title: '省份|运营商',formatter:RedirectFormatter,events:operatingevent},
            {field: 'flowUp',title: '上行流量（G）',sortable:true,formatter:showFormatter},
            {field: 'flowDn',title: '下行流量（G）',sortable:true,formatter:showFormatter},
            {field: 'totalFlow',title: '总流量(G)',sortable:true,formatter:showFormatter},
        ];
    }
    return column;
}
function showFormatter(value, row, index) {
    if(value==null){
        return "-";
    }
    return value;
}

function RedirectFormatter(value, row, index) {
    if(value==null){
        return "-";
    }
    return "<a href='#' title='"+value+"' class='redirectDetail' >"+value+"</a>";
}

function queryParams(params) {
    var data = $('#searchForm').formToJSON();
    data.dateType=$('#searchForm').find('.btn-success').attr("value");
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    if(data.appType=='-1'){
        data.appType=undefined;
    }
    if(data.stateTime==''){
        data.stateTime=undefined;
    }
    // $('#searchForm').find('input[name="sort"]').val(params.sort);
    // $('#searchForm').find('input[name="order"]').val(params.order);
    data.sort=params.sort;
    data.order=params.order;
    return data;
}
function loadDateType(datetype) {

    initStateDateV5(datetype,"state",true);
}

function initChilePage(row) {


    $('#appSearchForm').find('input[name="type"]').val(2);
    $('#trendAct').removeClass("active");
    $('#rankAct').addClass("active");

    loadSel('appType2','/select/getAppType',true,undefined,false);
    $('#main').hide();
    $('#child').show();
    let pageType = $('#searchForm').find('input[name="pageType"]').val();
    if(Number(pageType)==1){
        //"运营商流量流向"
        if(row.sourceVal=="10001"){
            $('#appSearchForm').find('input[name="areaType"]') .val(4);
        }else if(row.sourceVal=="10002"){//区域流量流向
            $('#appSearchForm').find('input[name="areaType"]') .val(1);
        }
        $('#appSearchForm').find('input[name="destArea"]') .val(row.targetVal);
    }else {
        //省到IDC
        if(row.targetVal=="10003"){
            $('#appSearchForm').find('input[name="areaType"]') .val(30);
        }else if(row.targetVal=="10004"){//运营商到IDC
            $('#appSearchForm').find('input[name="areaType"]') .val(31);
        }
        $('#appSearchForm').find('input[name="srcArea"]') .val(row.sourceVal);
    }

    let $d=$('#appDateTypeSelect >button');
    $('#appDateTypeSelect').find(".btn-success").removeClass("btn-success").addClass("btn-white").end();
    let dateType = $('#dateTypeSelect').find('.btn-success').attr("value");
    for (let i=0;i<4;i++){
        if($d.eq(i).attr("value")==dateType){
            $d.eq(i).removeClass("btn-white").addClass("btn-success").end();
        }
    }
    initPeriodDateV5(dateType,"start","end");
    initChileOpt();

    $('#appSearchForm').find('input[name="type"]').val(2);
    $('#trendAct').removeClass("active");
    $('#trend').removeClass("active");
    $('#rankAct').addClass("active");
    $('#ranking').addClass("active");
    $('#start').attr("placeholder","统计时间");
    $('#end').attr("type","hidden");
    // let type =$('#appSearchForm').find('input[name="type"]').val();
    // if(type==2){
        appdetailTraffic.initRank();
    // }else if(type==1){
    //     appdetailTraffic.initTrend();
    // }
}
function initChileOpt() {
    let type =$('#appSearchForm').find('input[name="type"]').val();
    let stateTime = $('#searchForm').find('input[name="stateTime"]').val();
    let appType = $('#searchForm').find('select[name="appType"]').val();
    if(type==2){
        $('#start').val(stateTime);
    }else {
        iniTrendTime();
    }
    $('#appSearchForm').find('select[name="appType"]').val(appType);

}
function iniTrendTime() {
    let begainTime = new Date();
    let endTime = new Date();
    let dateType = $('#appDateTypeSelect').find('.btn-success').attr("value");
    if(dateType==1){
        begainTime.setHours(begainTime.getHours()-24);
        $('#start').val(begainTime.Format("YYYY-MM-DD hh"));
        $('#end').val(endTime.Format("YYYY-MM-DD hh"));
    }else if(dateType==2){
        begainTime.setDate(begainTime.getDate()-30);
        $('#start').val(begainTime.Format("YYYY-MM-DD"));
        $('#end').val(endTime.Format("YYYY-MM-DD"));
    }else if(dateType==3){
        begainTime.setDate(begainTime.getDate()-70);
        $('#start').val(begainTime.Format("YYYY-MM-DD"));
        $('#end').val(endTime.Format("YYYY-MM-DD"));
    }else {
        begainTime.setMonth(begainTime.getMonth()-10);
        $('#start').val(begainTime.Format("YYYY-MM"));
        $('#end').val(endTime.Format("YYYY-MM"));
    }
}
function initRankTime() {
    let begainTime = new Date();
    let mainDateType = $('#dateTypeSelect').find('.btn-success').attr("value");
    let dateType = $('#appDateTypeSelect').find('.btn-success').attr("value");
    if (mainDateType==dateType){
        $('#start').val($('#searchForm').find('input[name="stateTime"]').val());
    }else {
        if (dateType == 1) {
            begainTime.setHours(begainTime.getHours() - 1);
            $('#start').val(begainTime.Format("YYYY-MM-DD hh"));
        } else if (dateType == 2) {
            begainTime.setDate(begainTime.getDate() - 1);
            $('#start').val(begainTime.Format("YYYY-MM-DD"));
        } else if (dateType == 3) {
            begainTime.setDate(begainTime.getDate() - 7);
            $('#start').val(begainTime.Format("YYYY-MM-DD"));
        } else {
            begainTime.setMonth(begainTime.getMonth() - 1);
            $('#start').val(begainTime.Format("YYYY-MM"));
        }
    }
}

window.operatingevent = {
    'click .redirectDetail': function (e, value, row, index) {
        initChilePage(row);
    }
};
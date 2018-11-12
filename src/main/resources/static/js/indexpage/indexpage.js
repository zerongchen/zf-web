var indexObject = {

    //4:主页数据及初始化
    initData: function(){
        //主页列表初始化
        var listInit = {
            url: "/indexpage/initTable?"+Math.random(),
            method: 'post',
            contentType : "application/x-www-form-urlencoded",
            dataType: "json",
            pagination: false,
            showColumns: false,
            toolbar: "#table-toolbar",
            iconSize: "outline",
            sidePagination: "server",
            dataField: "list",
            pageNumber: 1,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            queryParamsType:'limit',
            queryParams:function(params){
                return{
                    //每页多少条数据
                    pageSize: 10,
                    //请求第几页
                    pageIndex:0,
                }
            },//请求服务器时所传的参数
            responseHandler:function(result){
                if (result) {
                    return {
                        "list" : result.rows,
                        "total" : result.total
                    };
                } else {
                    return {
                        "list" : [],
                        "total" : 0
                    };
                }
            },
            striped:true,  //是否行间隔色显示
            sortable: true,//是否启用排序
            sortOrder: "asc",//排序方式
            clickToSelect:true, //是否启用点击选中行
            columns: [
                {
                    field: 'alarmTime',
                    title: '告警时间',formatter:function(value,row,index){
                        return "<span title='"+value+"'>"+value+"</span>";
                    }
                   /* ,
                    width:148*/
                },
                { // 列设置
                    field: 'alarmContent',
                    title: '告警信息',formatter:function(value,row,index){
                        return "<span title='"+value+"'>"+value+"</span>";
                    }
                },{
                    field: 'alarmParams',
                    title: '告警项参数',formatter:function(value,row,index){
                        return "<span title='"+value+"'>"+value+"</span>";
                    }
                }
            ]
        };
        $("#exampleTableToolbar18").bootstrapTable(listInit);
    },

    init_idc_traffic_pie: function () {
        var myChart = echarts.init($("#idc_traffic")[0]);
        $.ajax({
            url: "/indexpage/getPie0",
            async: false,
            data: {},
            type: 'POST',
            success: function (result) {
                if (result == null) {
                    myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
                } else {
                    option = {
                        tooltip : {
                            trigger: 'axis',
                            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                            }
                        },
                        calculable : true,
                        series : [
                            {
                                name:'访问来源',
                                type:'pie',
                                radius : '55%',
                                center: ['50%', '60%'],
                                data:result
                            }
                        ],
                        color: ["#ffbf7a","#247ba0", "#5ab1ef", "#70c1b3", "#b2dbbf", "#d6e593", "#eb547c", "#2ec7c9", "#b6a2de", "#07a2a4"],
                    };
                    myChart.clear();
                    myChart.setOption(option);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }

        });
    },
    init_idc_traffic_bar: function () {
        var myChart = echarts.init($("#dpi_traffic")[0]);
        $.ajax({
            url: "/indexpage/getBar0",
            async: false,
            data: {},
            type: 'POST',
            success: function (result) {
                if (result == null) {
                    myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
                } else {
                    option = {
                        tooltip : {
                            trigger: 'axis',
                            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                            }
                        },
                        legend: {
                            data:['上行流量','下行流量']
                        },
                        calculable : true,
                        xAxis : [
                            {
                                type : 'category',
                                data : result.axis
                            }
                        ],
                        yAxis : [
                            {
                                type : 'value',
                                name: '总流量',
                                axisLabel: {
                                    formatter: '{value} ' + result.unit
                                }
                            }
                        ],
                        series :result.series,
                      color: ["#247ba0", "#5ab1ef", "#70c1b3", "#b2dbbf", "#d6e593", "#eb547c", "#2ec7c9", "#b6a2de", "#ffbf7a", "#07a2a4"],
                        //    color: ['rgb(36,123,160)','rgb(90,177,239)','rgb(112,193,179)','rgb(178,219,191)'],
                    };
                    myChart.clear();
                    myChart.setOption(option);
                }
            }
        });
    },
    init_appflow_pie: function () {
        var myChart = echarts.init($("#appflow")[0]);
        $.ajax({
            url: "/indexpage/getPie1",
            async: false,
            data: {},
            type: 'POST',
            success: function (result) {
                if (result == null) {
                    myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
                } else {
                    option = {
                        tooltip : {
                            trigger: 'axis',
                            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                            }
                        },
                        calculable : true,
                        series : [
                            {
                                name:'访问来源',
                                type:'pie',
                                radius : '55%',
                                center: ['50%', '60%'],
                                data:result
                            }
                        ]
                    };
                    myChart.clear();
                    myChart.setOption(option);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    },

    // 全业务流量应用流量TOP10
    init_flowTop10_bar: function () {
        var myChart = echarts.init($("#flowTop10")[0]);
        $.ajax({
            url: "/indexpage/getBar1",
            async: false,
            data: {},
            type: 'POST',
            success: function (result) {
                if (result == null) {
                    myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
                } else {
                    option = {
                        tooltip : {
                            trigger: 'axis',
                            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                            }
                        },
                        legend: {
                            data:['流量']
                        },
                        calculable : true,
                        xAxis : [
                            {
                                type : 'category',
                                data : result.axis
                            }
                        ],
                        yAxis : [
                            {
                                type : 'value',
                                name: '总流量',
                                axisLabel: {
                                    formatter: '{value} ' + result.unit
                                }
                            }
                        ],
                        series : result.series
                    };
                    myChart.clear();
                    myChart.setOption(option);
                }
            }
        });
    },

}

$(document).ready(function () {
   // TableObject.init();
  //  indexObject.initData();
    indexObject.init_idc_traffic_pie();
    indexObject.init_idc_traffic_bar()
    indexObject.init_appflow_pie();
    indexObject.init_flowTop10_bar();
    loadTable();
});

function loadTable(){
    $.ajax({
        url: "/indexpage/initTable?"+Math.random(),
        data:{},
        type: 'GET',
        async: false,
        dataType: 'json',
        success: function(data){
            var data = eval(data);
            $('#alarmCount')[0].innerHTML='告警监控（'+data.total+'）';
            var rows = data.rows;
            var $selId = $('#exampleTableToolbar18');
            option = '<tbody>';
            $.each(rows, function (i,n) {
                option +='<tr><td>'+n.alarmTime+'</td><td>'+n.alarmContent+'</td><td>'+n.alarmParams+'</td></tr>'
            });
            option += '</tbody>';
            $selId.children().remove();
            $selId.append(option);
        }
    });
}

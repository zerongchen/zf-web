var myChart1 = "";
var myChart2 = "";
var choosedAppType = 0;
var choosedAppId = 0;

var imgUrl1 = "";
var data1 = {};
var imgUrl2 = "";
var data2 = {};
var imgUrl3 = "";
var data3 = {};

apptrafficInitJs = {

    // 用户数 报文数 柱状图配置项
    option : {
        title : {
        },
        tooltip : {
            trigger: 'axis'
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
        grid: {y: 70, y2:30, x2:120},
        xAxis : [
            {
                type : 'category'
            },
            {
                type : 'category',
                axisLine: {show:false},
                axisTick: {show:false},
                axisLabel: {show:false},
                splitArea: {show:false},
                splitLine: {show:false},
            }
        ],
        yAxis : [
            {
                type : 'value',
                axisLabel:{formatter:'{value}'}
            }
        ],
        dataZoom: [],
        series : [
            {
                name:'',
                type:'bar',
                barWidth:'60',
                itemStyle: {normal: {color:'rgba(181,195,52,1)', label:{show:true,textStyle:{color:'#27727B'}}}},
                data:[]

            },
            {
                name:'',
                type:'bar',
                barWidth:'60',
                itemStyle: {normal: {color:'rgba(181,195,52,0.5)', label:{show:true}}},
                data:[]
            }

        ]
    },

    option2 : {
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
        dataZoom: [
            {
                show: true,
                realtime: true,
                start: 0,
                end: 100,
                xAxisIndex: [0, 1]
            },
            {
                type: 'inside',
                realtime: true,
                start: 0,
                end: 100,
                xAxisIndex: [0, 1]
            }
        ],
        grid: [{
            left: 80,
            right: 80,
            height: '35%'
        }, {
            left: 80,
            right: 80,
            top: '55%',
            height: '35%'
        }],
        xAxis : [],
        yAxis : [
            {
                type : 'value',
            },
            {
                gridIndex: 1,
                type : 'value',
                inverse: true
            }
        ],
        series : [
            {
                name:'',
                type:'line',
                symbolSize: 8,
                hoverAnimation: false,
                data:[]
            },
            {
                name:'',
                type:'line',
                xAxisIndex: 1,
                yAxisIndex: 1,
                symbolSize: 8,
                hoverAnimation: false,
                data: []
            }
        ]
    },

    basicLineOption : {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false
            }
        },
        xAxis: {
            type: 'category',
            data: []
        },
        yAxis: {
            type: 'value'
        },
        grid: {x:80,y: 50,x2:80, y2:30},
        series: [{
            data: [],
            type: 'line'
        }]
    },

    // 初始化table
    initTable:function () {
        $('#table').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: '/userbehavioranalysis/apptraffic/listIdcData',
            queryParams: queryParams,
            contentType: 'application/json',
            striped: true,
            undefinedText: '',
            sortable: true,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            pagination: true,
            sidePagination: 'server',
            // iconSize: "outline",
            // icons: {
            //     columns: "glyphicon-list",
            // },
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn()
        });
    },

    // 初始化table
    initTable2:function (appType) {
        $('#appTypeDetailTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: '/userbehavioranalysis/apptraffic/listIdcData',
            queryParams: queryParams2,
            contentType: 'application/json',
            striped: true,
            undefinedText: '',
            sortable: true,                     //是否启用排序
            sortOrder: "asc",
            pagination: true,
            sidePagination: 'server',
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn2()
        });
    },

    // 初始化table
    initTable3:function () {


        $('#appIdDetailTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: '/userbehavioranalysis/apptraffic/listIdcDefinedAppIdData',
            queryParams: queryParams3,
            contentType: 'application/json',
            striped: true,
            undefinedText: '',
            sortable: true,                     //是否启用排序
            sortOrder: "asc",
            pagination: true,
            sidePagination: 'server',
            // iconSize: "outline",
            // icons: {
            //     columns: "glyphicon-list",
            // },
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn3()
        });
    },

    // 初始化柱状图
    initBar:function () {

        var statType = $("#analysisType").find('input:radio:checked').val();
        if(document.getElementById("appTrafficChart")==null){
            return false;
        }
        myChart1 = echarts.init(document.getElementById("appTrafficChart"));
        myChart1.clear();

        var data = queryParams();
        data.dateType = $('#searchForm').find('.btn-success').attr("value");
        data.statType = statType;

        $.ajax({
            url: '/userbehavioranalysis/apptraffic/getIdcEChartsData',
            data: JSON.stringify(data),
            type: 'POST',
            async: false,
            contentType: "application/json",
            success: function (data) {

                for(var i=0;i<data.axis.length;i++){
                    // data.axis[i] = getAppTypeNameById(data.axis[i]);
                    data.axis[i] = data.axis[i];
                }

                apptrafficInitJs.option.xAxis[0].data=data.axis;
                apptrafficInitJs.option.legend.data = [];

                var seriesObjArray = new Array();
                var seriesObj = {
                    name:'',
                    type:'bar',
                    barMaxWidth:'40',
                    label: {
                        normal: {
                            show: true,
                            // position: 'top',
                            offset: [0, 0],
                            textStyle: {
                                color: '#191970',
                            }
                        }

                    },
                    markPoint : {
                        symbolSize:[120, 80],
                        effect:{
                            show:true,
                            color:'#9AFF9A',
                        },
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    },
                    markLine : {
                        data : [
                            {type : 'average', name: '平均值'}
                        ]
                    },
                    data:[],
                    itemStyle:{
                        normal:{
                            color:["#eb547c"]
                        }
                    }
                };

                var seriesObj2 = {
                    name:'',
                    type:'bar',
                    barMaxWidth:'40',
                    label: {
                        normal: {
                            show: true,
                            position: 'top',
                            offset: [0, 0],
                            textStyle: {
                                color: '#191970',
                            }
                        }
                    },
                    markPoint : {
                        symbolSize:[120, 80],
                        effect:{
                            show:true,
                            color:'#9AFF9A',
                        },
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    },
                    data:[],
                    itemStyle:{
                        normal:{
                            color:["#2ec7c9"]
                        }
                    }
                };

                if (data!=""){

                    if (statType==1){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].name = "上行流量";
                        seriesObjArray[0].data = data.series[0].data;
                        seriesObjArray[0].markPoint.data = [];
                        seriesObjArray[0].markLine.data = [];

                        seriesObjArray[1] = seriesObj2;
                        seriesObjArray[1].name = "下行流量";
                        seriesObjArray[1].data = data.series[1].data;
                        seriesObjArray[1].markPoint.data = [];
                        apptrafficInitJs.option.legend.data = ['上行流量','下行流量'];
                        apptrafficInitJs.option.yAxis=[
                            {
                                type: 'value',
                                axisLabel: {
                                    formatter: '{value} ' + data.unit
                                }
                            }
                        ]
                    } else if (statType==2){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].data = data.series[0].data;
                        apptrafficInitJs.option.yAxis=[
                            {
                                type: 'value',
                                axisLabel: {
                                    formatter: '{value} '
                                }
                            }
                        ]
                    } else if (statType==3){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].data = data.series[0].data;
                        apptrafficInitJs.option.yAxis=[
                            {
                                type: 'value',
                                axisLabel: {
                                    formatter: '{value} '
                                }
                            }
                        ]
                    } else if (statType==4){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].name = "并发session";
                        seriesObjArray[0].data = data.series[0].data;
                        seriesObjArray[0].markPoint.data = [];
                        seriesObjArray[0].markLine.data = [];

                        seriesObjArray[1] = seriesObj2;
                        seriesObjArray[1].name = "新建session";
                        seriesObjArray[1].data = data.series[1].data;
                        seriesObjArray[1].markPoint.data = [];
                        apptrafficInitJs.option.legend.data = ['并发session','新建session'];
                        apptrafficInitJs.option.yAxis=[
                            {
                                type: 'value',
                                axisLabel: {
                                    formatter: '{value} '
                                }
                            }
                        ]
                    }
                    apptrafficInitJs.option.series = seriesObjArray;
                    myChart1.setOption(apptrafficInitJs.option,true);
                    imgUrl1 = myChart1.getDataURL('png');//获取base64编码
                }

            },
            error:function () {

            }
        });
    },

    // 初始化柱状图
    initBar2:function () {

        myChart2 = echarts.init(document.getElementById("appTypeChart"));
        myChart2.clear();

        var data = queryParams2();
        var statType = $("#analysisType2").find('input:radio:checked').val();
        data.dateType=$('#searchForm2').find('.btn-success').attr("value");
        data.statType = statType;
        // if (appType==undefined){
        //     data.appType = choosedAppType;
        // } else {
        //     data.appType = getAppTypeByAppTypeName(appType);
        // }

        $.ajax({
            url: '/userbehavioranalysis/apptraffic/getIdcSecondEChartsData',
            data: JSON.stringify(data),
            type: 'POST',
            async: false,
            contentType: "application/json",
            success: function (data) {

                apptrafficInitJs.option.xAxis[0].data=data.axis;
                apptrafficInitJs.option.legend.data = [];

                var seriesObjArray = new Array();
                var seriesObj = {
                    name:'',
                    type:'bar',
                    barMaxWidth:'40',
                    label: {
                        normal: {
                            show: true,
                            // position: 'top',
                            offset: [0, 0],
                            textStyle: {
                                color: '#191970',
                            }
                        }
                    },
                    markPoint : {
                        symbolSize:[120, 80],
                        effect:{
                            show: true,
                            color:'#9AFF9A',
                        },
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    },
                    markLine : {
                        data : [
                            {type : 'average', name: '平均值'}
                        ]
                    },
                    data:[],
                    itemStyle:{
                        normal:{
                            color:["#eb547c"]
                        }
                    }
                };

                var seriesObj2 = {
                    name:'',
                    type:'bar',
                    barMaxWidth:'40',
                    label: {
                        normal: {
                            show: true,
                            position: 'top',
                            offset: [0, 0],
                            textStyle: {
                                color: '#191970',
                            }
                        }
                    },
                    markPoint : {
                        symbolSize:[120, 80],
                        effect:{
                            show: true,
                            color:'#9AFF9A',
                        },
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    },
                    data:[],
                    itemStyle:{
                        normal:{
                            color:["#2ec7c9"]
                        }
                    }
                };

                if (data!=""){

                    if (statType==1){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].name = "上行流量";
                        seriesObjArray[0].data = data.series[0].data;
                        seriesObjArray[0].markPoint.data = [];
                        seriesObjArray[0].markLine.data = [];

                        seriesObjArray[1] = seriesObj2;
                        seriesObjArray[1].name = "下行流量";
                        seriesObjArray[1].data = data.series[1].data;
                        seriesObjArray[1].markPoint.data = [];
                        apptrafficInitJs.option.legend.data = ['上行流量','下行流量'];
                        apptrafficInitJs.option.yAxis=[
                            {
                                type: 'value',
                                axisLabel: {
                                    formatter: '{value} ' + data.unit
                                }
                            }
                        ]
                    } else if (statType==2){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].data = data.series[0].data;
                        apptrafficInitJs.option.yAxis=[
                            {
                                type: 'value',
                                axisLabel: {
                                    formatter: '{value} '
                                }
                            }
                        ]
                    } else if (statType==3){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].data = data.series[0].data;
                        apptrafficInitJs.option.yAxis=[
                            {
                                type: 'value',
                                axisLabel: {
                                    formatter: '{value} '
                                }
                            }
                        ]
                    } else if (statType==4){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].name = "并发session";
                        seriesObjArray[0].data = data.series[0].data;
                        seriesObjArray[0].markPoint.data = [];
                        seriesObjArray[0].markLine.data = [];

                        seriesObjArray[1] = seriesObj2;
                        seriesObjArray[1].name = "新建session";
                        seriesObjArray[1].data = data.series[1].data;
                        seriesObjArray[1].markPoint.data = [];
                        apptrafficInitJs.option.legend.data = ['并发session','新建session'];
                        apptrafficInitJs.option.yAxis=[
                            {
                                type: 'value',
                                axisLabel: {
                                    formatter: '{value} '
                                }
                            }
                        ]
                    }
                    apptrafficInitJs.option.series = seriesObjArray;
                    myChart2.setOption(apptrafficInitJs.option,true);
                    imgUrl2 = myChart2.getDataURL('png');//获取base64编码
                }

            },
            error:function () {

            },

        });


        myChart2.on('click', function(params) {
            var data = $('#searchForm2').formToJSON();
            data.dateType = $('#searchForm2').find('.btn-success').attr("value");

            var appId = getAppIdByAppIdName(params.name);
            choosedAppId = appId;

            $("#appIdModal").modal('show');
            apptrafficInitJs.loadSelectOption3();
            $("#appType3").val(choosedAppType);
            $("#appId3").val(choosedAppId);
            $("#areaId3").val(data.areaId);
            $("#userGroupNo3").val(data.userGroupNo);

            var dateTypeObj = $("#dateTypeSelect3").find("[name='dateType3']");
            $("#dateTypeSelect3").find('.btn-success').addClass("btn-white");
            $("#dateTypeSelect3").find('.btn-success').removeClass("btn-success");
            for (var i=0;i<dateTypeObj.length;i++){
                if ($("#dateTypeSelect3").find("[name='dateType3']").eq(i).val()==data.dateType){
                    $("#dateTypeSelect3").find("[name='dateType3']").eq(i).removeClass("btn-white");
                    $("#dateTypeSelect3").find("[name='dateType3']").eq(i).addClass("btn-success");
                    break;
                }
            }

            apptrafficInitJs.initLine();
            apptrafficInitJs.initTable3();
        });
    },

    // 初始化折线图
    initLine:function () {

        var myChart = echarts.init(document.getElementById("appIdChart"));
        myChart.clear();

        var paramData = queryParams3();



        $.ajax({
            url: '/userbehavioranalysis/apptraffic/geIdctLineData',
            data: JSON.stringify(paramData),
            type: 'POST',
            async: false,
            contentType: "application/json",
            success: function (data) {

                apptrafficInitJs.option2.legend.data = [];

                var seriesObjArray = new Array();
                var seriesObj = {
                    name:'',
                    type:'line',
                    symbolSize: 8,
                    hoverAnimation: false,
                    data:[]
                };

                var seriesObj2 = {
                    name:'',
                    type:'line',
                    xAxisIndex: 1,
                    yAxisIndex: 1,
                    symbolSize: 8,
                    hoverAnimation: false,
                    data: []
                };

                var xaxisObjArray = new Array();
                var xaxisObj = {
                    type : 'category',
                    boundaryGap : false,
                    axisLine: {onZero: true},
                    data: []
                };
                var xaxisObj2 = {
                    gridIndex: 1,
                    type : 'category',
                    boundaryGap : false,
                    axisLine: {onZero: true},
                    data: [],
                    position: 'top'
                };

                var yaxisObjArray = new Array();
                var yaxisObj = {
                    type : 'value'
                };
                var yaxisObj2 = {
                    gridIndex: 1,
                    type : 'value',
                    inverse: true
                };

                if (data!=""){

                    if (paramData.statType==1){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].name = "上行流量";
                        seriesObjArray[0].data = data.series[0].data;

                        seriesObjArray[1] = seriesObj2;
                        seriesObjArray[1].name = "下行流量";
                        seriesObjArray[1].data = data.series[1].data;

                        xaxisObjArray[0] = xaxisObj;
                        xaxisObjArray[0].data = data.axis;
                        xaxisObjArray[1] = xaxisObj2;
                        xaxisObjArray[1].data = data.axis;

                        yaxisObjArray[0] = yaxisObj;
                        yaxisObjArray[1] = yaxisObj2;

                        apptrafficInitJs.option2.legend.data = ['上行流量','下行流量'];
                    } else if (paramData.statType==2){

                        apptrafficInitJs.basicLineOption.xAxis.data=data.axis;
                        apptrafficInitJs.basicLineOption.series[0].data = data.series[0].data;

                    } else if (paramData.statType==3){

                        apptrafficInitJs.basicLineOption.xAxis.data = data.axis;
                        apptrafficInitJs.basicLineOption.series[0].data = data.series[0].data;

                    } else if (paramData.statType==4){
                        seriesObjArray[0] = seriesObj;
                        seriesObjArray[0].name = "并发session";
                        seriesObjArray[0].data = data.series[0].data;



                        seriesObjArray[1] = seriesObj2;
                        seriesObjArray[1].name = "新建session";
                        seriesObjArray[1].data = data.series[1].data;

                        xaxisObjArray[0] = xaxisObj;
                        xaxisObjArray[0].data = data.axis;
                        xaxisObjArray[1] = xaxisObj2;
                        xaxisObjArray[1].data = data.axis;

                        yaxisObjArray[0] = yaxisObj;
                        yaxisObjArray[1] = yaxisObj2;

                        apptrafficInitJs.option2.legend.data = ['并发session','新建session'];
                    }

                    if (paramData.statType==1||paramData.statType==4){
                        apptrafficInitJs.option2.xAxis = xaxisObjArray;
                        apptrafficInitJs.option2.yAxis = yaxisObjArray;
                        apptrafficInitJs.option2.series = seriesObjArray;
                        myChart.setOption(apptrafficInitJs.option2,true);
                    } else {
                        myChart.setOption(apptrafficInitJs.basicLineOption,true);
                    }
                    //  折线图有动画效果，不能马上获取否则只会获取到几个点
                    setTimeout(function () {
                        imgUrl3 = myChart.getDataURL('png');//获取base64编码
                    },3000);

                }

            },
            error:function () {

            }
        });
    },


    initClick : function () {

        $("#searchForm .btn.btn-outline.btn-link.moreSearchCondition").on('click',function(){
            $("#searchForm").find('div[name="moreSearchDiv"]').toggle();
        });

        $("#searchForm2 .btn.btn-outline.btn-link.moreSearchCondition").on('click',function(){
            $("#searchForm2").find('div[name="moreSearchDiv"]').toggle();
        });

        $("#searchForm3 .btn.btn-outline.btn-link.moreSearchCondition").on('click',function(){
            $("#searchForm3").find('div[name="moreSearchDiv"]').toggle();
        });


        $('#exportButton111').find('li').click(function () {
            var exportType = 1;
            var text = $(this).text();
            if(text=='EXCEL'){
                exportType = 0;
            } else if (text=='WORLD'){
                exportType = 1;
            } else if (text=='PDF'){
                exportType = 2;
            }
            var data = queryParams();
            var form = '<form class="hide" id="export1">';
            form += '<input name="probeType" value="1" />';

            form += '<input name="imgUrl" value="' + imgUrl1 + '" />';
            form += '<input name="exportType" value="'+exportType+'" />';
            form += '<input name="tableType" value="1" />';
            form += '<input name="startTime" value="' + data.startTime + '" />';
            form += '<input name="dateType" value="'+ data.dateType + '" />';
            form += '<input name="areaId" value="'+ data.areaId + '" />';
            form += '<input name="userGroupNo" value="'+ data.userGroupNo + '" />';
            form += '<input name="appType" value="'+ data.appType + '" />';
            form += '<input name="appId" value="'+ data.appId + '" />';
            form += '<input name="listType" value="1" />';

            form += '</form>';
            $('body').append(form);
            $('#export1').attr('action', '/userbehavioranalysis/apptraffic/exportIdcData.do?ran='+Math.random()).attr('method', 'post').submit();
            $('#export1').remove();
        });

        $('#exportButton222').find('li').click(function () {
            var data = queryParams2();
            var exportType = 1;
            var text = $(this).text();
            if(text=='EXCEL'){
                exportType = 0;
            } else if (text=='WORLD'){
                exportType = 1;
            } else if (text=='PDF'){
                exportType = 2;
            }
            var tableType = 2;
            var form = '<form class="hide" id="export2">';
            form += '<input name="probeType" value="1" />';

            form += '<input name="imgUrl" value="' + imgUrl2 + '" />';
            form += '<input name="exportType" value="' + exportType + '" />';
            form += '<input name="tableType" value="' + tableType + '" />';
            form += '<input name="startTime" value="' + data.startTime + '" />';
            form += '<input name="dateType" value="'+ data.dateType + '" />';
            form += '<input name="areaId" value="'+ data.areaId + '" />';
            form += '<input name="userGroupNo" value="'+ data.userGroupNo + '" />';
            form += '<input name="appType" value="'+ data.appType + '" />';
            form += '<input name="appId" value="'+ data.appId + '" />';
            form += '<input name="listType" value="2" />';

            form += '</form>';
            $('body').append(form);
            $('#export2').attr('action', '/userbehavioranalysis/apptraffic/exportIdcData.do?ran='+Math.random()).attr('method', 'post').submit();
            $('#export2').remove();
        });

        $('#exportButton333').find('li').click(function () {
            var data = queryParams3();
            var exportType = 1;
            var text = $(this).text();
            if(text=='EXCEL'){
                exportType = 0;
            } else if (text=='WORLD'){
                exportType = 1;
            } else if (text=='PDF'){
                exportType = 2;
            }
            var tableType = 3;
            var form = '<form class="hide" id="export3">';
            form += '<input name="probeType" value="1" />';

            form += '<input name="imgUrl" value="' + imgUrl3 + '" />';
            form += '<input name="exportType" value="' + exportType + '" />';
            form += '<input name="tableType" value="' + tableType + '" />';
            form += '<input name="dateType" value="'+ data.dateType + '" />';
            form += '<input name="areaId" value="'+ data.areaId + '" />';
            form += '<input name="userGroupNo" value="'+ data.userGroupNo + '" />';
            form += '<input name="appType" value="'+ data.appType + '" />';
            form += '<input name="appId" value="'+ data.appId + '" />';

            form += '</form>';
            $('body').append(form);
            $('#export3').attr('action', '/userbehavioranalysis/apptraffic/exportIdcData.do?ran='+Math.random()).attr('method', 'post').submit();
            $('#export3').remove();
        });

        $('#searchFormButton').click(function () {
            initTableJs.refreshData();
            apptrafficInitJs.initBar();

        });

        $('#searchFormButton2').click(function () {
            apptrafficInitJs.initTable2();
            apptrafficInitJs.initBar2();

        });

        $('#searchFormButton3').click(function () {
            apptrafficInitJs.initTable3();
            apptrafficInitJs.initLine();

        });

        $('#analysisType').find("[name='analysisType']").click(function () {
            apptrafficInitJs.initBar();

        });

        $('#analysisType2').find("[name='analysisType2']").click(function () {
            apptrafficInitJs.initBar2();

        });

        $('#analysisType3').find("[name='analysisType3']").click(function () {
            apptrafficInitJs.initLine();

        });

        $("#dateTypeSelect").find("[name='dateType']").click(function(){
            $("#dateTypeSelect").find('.btn-success').addClass("btn-white");
            $("#dateTypeSelect").find('.btn-success').removeClass("btn-success");
            $(this).removeClass("btn-white");
            $(this).addClass("btn-success");

            var dateType=$('#searchForm').find('.btn-success').attr("value");
            initPeriodDateV5(dateType,"start","end");
            initDefaultSearchValue('searchForm','start');
        });

        $("#dateTypeSelect2").find("[name='dateType2']").click(function(){
            $("#dateTypeSelect2").find('.btn-success').addClass("btn-white");
            $("#dateTypeSelect2").find('.btn-success').removeClass("btn-success");
            $(this).removeClass("btn-white");
            $(this).addClass("btn-success");

            var dateType=$('#searchForm2').find('.btn-success').attr("value");
            initPeriodDateV5(dateType,"start2","end2");
            initDefaultSearchValue('searchForm2','start2');
        });

        $("#dateTypeSelect3").find("[name='dateType3']").click(function(){
            $("#dateTypeSelect3").find('.btn-success').addClass("btn-white");
            $("#dateTypeSelect3").find('.btn-success').removeClass("btn-success");
            $(this).removeClass("btn-white");
            $(this).addClass("btn-success");

            // var dateType=$('#searchForm3').find('.btn-success').attr("value");
            // initPeriodDateV5(dateType,"start3","end3");
            // initDefaultSearchValue('searchForm3','start3');
        });

        if (myChart1!=""){
            myChart1.on('click', function(params) {
                var data = $('#searchForm').formToJSON();
                // 设置统计周期
                data.dateType = $('#searchForm').find('.btn-success').attr("value");

                $('#detailModal').modal('show');
                choosedAppType = getAppTypeByAppTypeName(params.name);
                apptrafficInitJs.loadSelectOption2();
                loadSel('appId2','/select/getAppByType',true,{appType:choosedAppType},true,undefined);
                $("#appType2").val(choosedAppType);
                $("#start2").val(data.startTime);
                $("#areaId2").val(data.areaId);
                $("#userGroupNo2").val(data.userGroupNo);
                var dateTypeObj = $("#dateTypeSelect2").find("[name='dateType2']");
                $("#dateTypeSelect2").find('.btn-success').addClass("btn-white");
                $("#dateTypeSelect2").find('.btn-success').removeClass("btn-success");
                for (var i=0;i<dateTypeObj.length;i++){
                    if ($("#dateTypeSelect2").find("[name='dateType2']").eq(i).val()==data.dateType){
                        $("#dateTypeSelect2").find("[name='dateType2']").eq(i).removeClass("btn-white");
                        $("#dateTypeSelect2").find("[name='dateType2']").eq(i).addClass("btn-success");
                        break;
                    }
                }

                apptrafficInitJs.initTable2(params.name);
                apptrafficInitJs.initBar2(params.name);
                // initDefinedEleIdDate(2,false,false,"start2","end2");
            });
        }

        // 避免多次弹出框滚动条失效
        $("#appIdDismissBtn").on("click",function(){
            $('#appIdModal').modal('hide');
            $(this).parents().find("body").addClass("modal-open");
        });

    },

    // 加载下拉选数据
    loadSelectOption : function(){
        loadSel('appType','/select/getAppType',true,undefined,true);
        loadSel('appId','/select/getAppByType',true,{appType:0},true,undefined);
        $('#appType').on("change",function(){
            loadSel('appId','/select/getAppByType',true,{appType:$("#appType").val()},true,undefined);
        })
        loadSelect("areaId","/userbehavioranalysis/apptraffic/getAreaList");
        loadSelect("userGroupNo","/userbehavioranalysis/apptraffic/getUserGroup");

    },

    loadSelectOption2 : function() {
        loadSel('appType2','/select/getAppType',true,undefined,true);
        loadSel('appId2','/select/getAppByType',true,{appType:0},true,undefined);
        $('#appType2').on("change",function(){
            loadSel('appId2','/select/getAppByType',true,{appType:$("#appType2").val()},true,undefined);
        })
        loadSelect("areaId2","/userbehavioranalysis/apptraffic/getAreaList");
        loadSelect("userGroupNo2","/userbehavioranalysis/apptraffic/getUserGroup");
    },

    loadSelectOption3 : function() {
        loadSel('appType3','/select/getAppType',true,undefined,true);
        loadSel('appId3','/select/getAppByType',true,{appType:choosedAppType},true,undefined);
        $('#appType3').on("change",function(){
            loadSel('appId3','/select/getAppByType',true,{appType:$("#appType3").val()},true,undefined);
        })
        loadSelect("areaId3","/userbehavioranalysis/apptraffic/getAreaList");
        loadSelect("userGroupNo3","/userbehavioranalysis/apptraffic/getUserGroup");
    },

    init : function(){
        apptrafficInitJs.loadSelectOption();
        apptrafficInitJs.initTable();
        apptrafficInitJs.initBar();
        apptrafficInitJs.initClick();

    }
}

$(document).ready(function() {
    // initDate(2,false,true);
    // initDefaultSearchValue();
    // initDefinedEleIdDate(2,false,false,"start2","end2");
    initPeriodDateV5(2,"start","end");
    initPeriodDateV5(2,"start2","end2");
    initDefaultSearchValue('searchForm','start');
    apptrafficInitJs.init();
});

function initDefaultSearchValue(searchForm,ele){
    var initValue = getDefaultTimeSearchValue(searchForm);
    $("#"+ele).val(initValue);
}

/**
 * 根据统计周期获取默认统计时间
 * @returns {string}
 */
function getDefaultTimeSearchValue(searchForm){
    var date = new Date();
    var dateType=$('#'+searchForm).find('.btn-success').attr("value");
    if (dateType==1){
        date.setHours(date.getHours()-1);
    } else if (dateType==2){
        date.setDate(date.getDate()-1);
    } else if (dateType==3){
        date.setDate(date.getDate()-7);
    } else if (dateType==4){
        date.setMonth(date.getMonth()-1);
    }
    var year = date.getFullYear();
    var month = date.getMonth()+1;
    var day = date.getDate();
    var hour = date.getHours();
    if (month<10){
        month = "0"+month;
    }
    if(day<10){
        day = "0"+day;
    }
    if (hour<10){
        hour = "0"+hour;
    }

    var initValue = "";
    if (dateType==1){
        initValue = year+"-"+month+"-"+day+" "+hour;
    } else if(dateType==2||dateType==3){
        initValue = year+"-"+month+"-"+day;
    } else if(dateType==4){
        initValue = year+"-"+month;
    }
    return initValue;
}

// table显示项
function getColumn() {
    var column =[
        {field: 'appTypeName',title: '<span title="应用分类">应用分类</span>',formatter:getAppTypeLink},
        {field: 'appTrafficUp',sortable:true,title: '<span title="上行流量">上行流量</span>',formatter:function(value,row,index){
            return "<span title='"+row.appTrafficUp+"'>"+row.appTrafficUpStr+"</span>";
        }},
        {field: 'appTrafficDn',sortable:true,title: '<span title="下行流量">下行流量</span>',formatter:function(value,row,index){
            return "<span title='"+row.appTrafficDnStr+"'>"+row.appTrafficDnStr+"</span>";
        }},
        {field: 'appTrafficSum',sortable:true,title: '<span title="总流量">总流量</span>',formatter:function(value,row,index){
            return "<span title='"+row.appTrafficSumStr+"'>"+row.appTrafficSumStr+"</span>";
        }},
        {field: 'appUserNum',sortable:true,title: '<span title="用户数">用户数</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appPacketsNum',sortable:true,title: '<span title="报文数（次）">报文数（次）</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appSessionsNum',sortable:true,title: '<span title="并发session数（次）">并发session数（次）</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appNewSessionNum',sortable:true,title: '<span title="新建session数(次)">新建session数(次)</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }}
    ];
    return column;
}

// table显示项
function getColumn2() {
    var column =[
        {field: 'appNameOfId',title: '<span title="应用名称">应用名称</span>',formatter:getAppIdLink},
        {field: 'appTrafficUp',sortable:true,title: '<span title="上行流量">上行流量</span>',formatter:function(value,row,index){
                return "<span title='"+row.appTrafficUp+"'>"+row.appTrafficUpStr+"</span>";
            }},
        {field: 'appTrafficDn',sortable:true,title: '<span title="下行流量">下行流量</span>',formatter:function(value,row,index){
                return "<span title='"+row.appTrafficDnStr+"'>"+row.appTrafficDnStr+"</span>";
            }},
        {field: 'appTrafficSum',sortable:true,title: '<span title="总流量">总流量</span>',formatter:function(value,row,index){
                return "<span title='"+row.appTrafficSumStr+"'>"+row.appTrafficSumStr+"</span>";
            }},
        {field: 'appUserNum',sortable:true,title: '<span title="用户数">用户数</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appPacketsNum',sortable:true,title: '<span title="报文数（次）">报文数（次）</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appSessionsNum',sortable:true,title: '<span title="并发session数（次）">并发session数（次）</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appNewSessionNum',sortable:true,title: '<span title="新建session数(次)">新建session数(次)</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }}
    ];
    return column;
}

// table显示项
function getColumn3() {
    var column =[
        {field: 'statTime',title: '<span title="统计时间">统计时间</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appTrafficUp',sortable:true,title: '<span title="上行流量">上行流量</span>',formatter:function(value,row,index){
                return "<span title='"+row.appTrafficUp+"'>"+row.appTrafficUpStr+"</span>";
            }},
        {field: 'appTrafficDn',sortable:true,title: '<span title="下行流量">下行流量</span>',formatter:function(value,row,index){
                return "<span title='"+row.appTrafficDnStr+"'>"+row.appTrafficDnStr+"</span>";
            }},
        {field: 'appTrafficSum',sortable:true,title: '<span title="总流量">总流量</span>',formatter:function(value,row,index){
                return "<span title='"+row.appTrafficSumStr+"'>"+row.appTrafficSumStr+"</span>";
            }},
        {field: 'appUserNum',sortable:true,title: '<span title="用户数">用户数</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appPacketsNum',sortable:true,title: '<span title="报文数（次）">报文数（次）</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appSessionsNum',sortable:true,title: '<span title="并发session数（次）">并发session数（次）</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'appNewSessionNum',sortable:true,title: '<span title="新建session数(次)">新建session数(次)</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }}
    ];
    return column;
}


function queryParams(params) {
    var data = $('#searchForm').formToJSON();
    data.dateType=$('#searchForm').find('.btn-success').attr("value");

    data.probeType = 1;
    data.listType = 1;

    if (params!=undefined && params!=''){
        data.page = params.offset/params.limit +1 ;
        data.pageSize = params.limit;
        data.sort = params.sort;      //排序列名
        data.sortOrder = params.order;
    }

    if (data.areaId==undefined||data.areaId==''){
        data.areaId = -1;
    }
    if (data.userGroupNo==undefined||data.userGroupNo==''){
        data.userGroupNo = -1;
    }
    if (data.appId==undefined||data.appId==''){
        data.appId = -1;
    }
    if (data.appType==undefined||data.appType==''){
        data.appType = -1;
    }
    if (data.startTime==undefined||data.startTime==''){
        data.startTime = getDefaultTimeSearchValue('searchForm');
    }

    if(data.dateType==1){
        data.startTime = new Date(data.startTime+":00:00").getTime();
    } else if (data.dateType==2||data.dateType==3){
        data.startTime = new Date(data.startTime.trim()+" 00:00:00").getTime();
    } else if (data.dateType==4){
        data.startTime = new Date(data.startTime.trim()+"-01 00:00:00").getTime();
    }
    return data;
}

function queryParams2(params){
    var data = $('#searchForm2').formToJSON();

    data.probeType = 1;
    data.listType = 2;
    data.dateType=$('#searchForm2').find('.btn-success').attr("value");
    if (params!=undefined && params!=''){
        data.page = params.offset/params.limit +1 ;
        data.pageSize = params.limit;
        data.sort = params.sort;      //排序列名
        data.sortOrder = params.order;
    }

    if (data.areaId==undefined||data.areaId==''){
        data.areaId = -1;
    }
    if (data.userGroupNo==undefined||data.userGroupNo==''){
        data.userGroupNo = -1;
    }
    if (data.appId==undefined||data.appId==''){
        data.appId = -1;
    }
    if (data.appType==undefined||data.appType==''){
        data.appType = -1;
    }
    if (data.startTime==undefined||data.startTime==''){
        data.startTime = getDefaultTimeSearchValue('searchForm');
    }

    if(data.dateType==1){
        data.startTime = new Date(data.startTime+":00:00").getTime();
    } else if (data.dateType==2||data.dateType==3){
        data.startTime = new Date(data.startTime.trim()+" 00:00:00").getTime();
    } else if (data.dateType==4){
        data.startTime = new Date(data.startTime.trim()+"-01 00:00:00").getTime();
    }
    return data;
}

function queryParams3(params){
    var data = $('#searchForm3').formToJSON();
    data.probeType = 1;

    data.dateType=$('#dateTypeSelect3').find('.btn-success').attr("value");
    var statType = $("#analysisType3").find('input:radio:checked').val();
    data.statType = statType;

    if (params!=undefined && params!=''){
        data.page = params.offset/params.limit +1 ;
        data.pageSize = params.limit;
        data.sort = params.sort;      //排序列名
        data.sortOrder = params.order;
    }

    if (data.areaId==undefined||data.areaId==''){
        data.areaId = -1;
    }
    if (data.userGroupNo==undefined||data.userGroupNo==''){
        data.userGroupNo = -1;
    }
    if (data.appId==undefined||data.appId==''){
        data.appId = -1;
    }
    if (data.appType==undefined||data.appType==''){
        data.appType = -1;
    }

    return data;
}

function loadSelect(id,url) {
    $("#"+id).empty();//清空select列表数据
    $.ajax({
        type : "POST",
        url : url,
        dataType : "JSON",
        async : false,
        data : {},
        success : function(msg)
        {

            for (var i = 0; i < msg.rows.length; i++) {
                if (id == 'areaId' || id == 'areaId2' || id=='areaId3'){
                    if (i==0){
                        $("#"+id).prepend("<option value='-1' >省份</option>");//添加第一个option值
                    }
                    //如果在select中传递其他参数，可以在option 的value属性中添加参数
                    $("#"+id).append("<option value='"+msg.rows[i].areaCode+"'>"+msg.rows[i].areaName+"</option>");
                } else if (id=='userGroupNo' || id == 'userGroupNo2' || id=='userGroupNo3'){
                    if (i==0){
                        $("#"+id).prepend("<option value='-1' >全用户</option>");//添加第一个option值
                    }
                    $("#"+id).append("<option value='"+msg.rows[i].value+"'>"+msg.rows[i].title+"</option>");
                }
            }

        },error:function(){
            swal("获取数据失败","error");
        }
    });
}

function getAppTypeNameById(value,row,index){
    var webType = "";
    if (value==0){
        webType = "未识别应用";
    } else if (value==1){
        webType = "Web 视频类";
    } else if (value==2){
        webType = "其它 Web 类";
    } else if (value==3){
        webType = "P2PStream";
    } else if (value==4){
        webType = "P2PDownload";
    } else if (value==5){
        webType = "VideoStream";
    } else if (value==6){
        webType = "Download";
    } else if (value==7){
        webType = "Email";
    } else if (value==8){
        webType = "IM";
    } else if (value==9){
        webType = "Game";
    } else if (value==10){
        webType = "VoIP";
    } else if (value==11){
        webType = "电信自营";
    } else if (value==12){
        webType = "其它";
    }
    return webType;
}

function getAppTypeLink(value,row,index){
    var appTypeBtn = '<a href="#" onclick="gotoDefinedAppTypePage('+row['appType']+')">'+value+'<i class="fa fa-lg"></i></a>' ;
    return appTypeBtn;
}

function getAppIdLink(value,row,index){
    var appIdBtn = '<a href="#" onclick="gotoDefinedAppIdPage('+row['appId']+')">'+value+'<i class="fa fa-lg"></i></a>' ;
    return appIdBtn;
}

function gotoDefinedAppTypePage(appType){

    var data = $('#searchForm').formToJSON();
    // 设置统计值
    data.dateType = $('#searchForm').find('.btn-success').attr("value");
    choosedAppType = parseInt(appType);

    $('#detailModal').modal('show');
    apptrafficInitJs.loadSelectOption2();
    loadSel('appId2','/select/getAppByType',true,{appType:choosedAppType},true,undefined);
    $("#appType2").val(choosedAppType);
    $("#start2").val(data.startTime);
    $("#areaId2").val(data.areaId);
    $("#userGroupNo2").val(data.userGroupNo);
    var dateTypeObj = $("#dateTypeSelect2").find("[name='dateType2']");
    $("#dateTypeSelect2").find('.btn-success').addClass("btn-white");
    $("#dateTypeSelect2").find('.btn-success').removeClass("btn-success");
    for (var i=0;i<dateTypeObj.length;i++){
        if ($("#dateTypeSelect2").find("[name='dateType2']").eq(i).val()==data.dateType){
            $("#dateTypeSelect2").find("[name='dateType2']").eq(i).removeClass("btn-white");
            $("#dateTypeSelect2").find("[name='dateType2']").eq(i).addClass("btn-success");
            break;
        }
    }
    apptrafficInitJs.initTable2();
    apptrafficInitJs.initBar2();
}

function gotoDefinedAppIdPage(appId){

    var data = $('#searchForm2').formToJSON();
    data.dateType = $('#searchForm2').find('.btn-success').attr("value");
    choosedAppId = parseInt(appId);

    $("#appIdModal").modal('show');
    apptrafficInitJs.loadSelectOption3();
    $("#appType3").val(choosedAppType);
    $("#appId3").val(choosedAppId);
    $("#areaId3").val(data.areaId);
    $("#userGroupNo3").val(data.userGroupNo);

    var dateTypeObj = $("#dateTypeSelect3").find("[name='dateType3']");
    $("#dateTypeSelect3").find('.btn-success').addClass("btn-white");
    $("#dateTypeSelect3").find('.btn-success').removeClass("btn-success");
    for (var i=0;i<dateTypeObj.length;i++){
        if ($("#dateTypeSelect3").find("[name='dateType3']").eq(i).val()==data.dateType){
            $("#dateTypeSelect3").find("[name='dateType3']").eq(i).removeClass("btn-white");
            $("#dateTypeSelect3").find("[name='dateType3']").eq(i).addClass("btn-success");
            break;
        }
    }
    apptrafficInitJs.initLine();
    apptrafficInitJs.initTable3();
}

function getAppTypeByAppTypeName(appTypeName){
    var appType = "";
    $.ajax({
        type : "POST",
        url : "/userbehavioranalysis/apptraffic/getAppTypeByName",
        dataType : "JSON",
        async: false,
        data : {"appTypeName":appTypeName},
        success : function(msg)
        {
            appType = msg.appType;
        },error:function(){
            swal("获取数据失败","error");
        }
    });
    return appType;
}

function getAppIdByAppIdName(appIdName){
    var appId = "";
    $.ajax({
        type : "POST",
        url : "/userbehavioranalysis/apptraffic/getAppIdByName",
        dataType : "JSON",
        async: false,
        data : {"appIdName":appIdName,"appType":choosedAppType},
        success : function(msg)
        {
            appId = msg.appId;
        },error:function(){
            swal("获取数据失败","error");
        }
    });
    return appId;
}
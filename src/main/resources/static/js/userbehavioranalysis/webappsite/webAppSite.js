var myChart1 = "";
var myChart2 = "";
var imgUrl1 = "";
var imgUrl2 = "";
urlConstant = {
    appTypeUrl: '/select/getWebType',
    areaIdUrl : '/userbehavioranalysis/apptraffic/getAreaList',
    userGroupNoUrl: '/userbehavioranalysis/apptraffic/getUserGroup',

    exportUrl : '/userbehavioranalysis/webappsite/exportData.do?ran='+Math.random(),
    exportIdcUrl : '/userbehavioranalysis/webappsite/exportIdcData.do?ran='+Math.random(),
    tableUrl : '/userbehavioranalysis/webappsite/listData',
    tableUrl2 : '/userbehavioranalysis/webappsite/listData2',
    chartUrl : '/userbehavioranalysis/webappsite/getEChartsData',
    lineUrl : '/userbehavioranalysis/webappsite/getLineData',

    getSiteType: '/userbehavioranalysis/webappsite/getSiteTypeByName',
},

webAppSiteJs = {
    // 图表的配置项
    option : {
        tooltip : {
            trigger: 'axis',
            axisPointer : {
                animation: false,
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            },
            formatter:function(params)//数据格式
            {
                var relVal = "";
                relVal += params[0].name+"<br/>";
                relVal += params[0].seriesName+ ' : ' + getFormattedValue(params[0].value)+"<br/>";
                relVal +=params[1].seriesName+ ' : ' +getFormattedValue(params[1].value)+"<br/>";
                return relVal;
            }
        },
        legend: {
            data:["上行流量","下行流量"]
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        axis : [],
        xAxis : [
            {
                splitLine:{
                    show:false
                },
                data : []
            }
        ],
        yAxis : [
            {
                axisLabel: {
                    margin: 2,
                    formatter: function (value, index) {
                        return getFormattedValue(value);
                    }
                },
                splitLine:{
                    show:false
                }
            }
        ],
        series : []
    },

    basicLineOption : {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false
            },
            formatter:function(params)//数据格式
            {
                var relVal = "";
                relVal += params[0].name+"<br/>";
                relVal += params[0].seriesName+ ' : ' + getFormattedValue(params[0].value)+"<br/>";
                relVal +=params[1].seriesName+ ' : ' +getFormattedValue(params[1].value)+"<br/>";
                return relVal;
            }
        },
        legend: {
            data:['上行流量','下行流量'],
            x: 'left'
        },
        axisPointer: {
            link: {xAxisIndex: 'all'}
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
            height: '30%'
        }, {
            left: 80,
            right: 80,
            top: '60%',
            height: '30%'
        }],
        xAxis : [],
        yAxis : [
            {
                axisLabel: {
                    margin: 2,
                    formatter: function (value, index) {
                        return getFormattedValue(value);
                    }
                },
                type : 'value',
                // max : 500
            },
            {
                axisLabel: {
                    margin: 2,
                    formatter: function (value, index) {
                        return getFormattedValue(value);
                    }
                },
                gridIndex: 1,
                type : 'value',
                inverse: true
            }
        ],
        series : [
            {
                name:'上行流量',
                type:'line',
                symbolSize: 8,
                hoverAnimation: false,
                data:[]
            },
            {
                name:'下行流量',
                type:'line',
                xAxisIndex: 1,
                yAxisIndex: 1,
                symbolSize: 8,
                hoverAnimation: false,
                data: []
            }
        ]
    },

    // 初始化点击事件
    initClick : function () {

        // 更多条件按钮单击事件
        $(".btn.btn-outline.btn-link.moreSearchCondition").on('click',function(){
            $(this).parent().parent().find('div[name="moreSearchDiv"]').toggle();
        });


        /*************************** 页面1相关单击事件begin ****************************/
        // 时间粒度切换单击事件
        $("#dateTypeSelect").find("[name='dateType']").click(function(){
            $(this).parent().find('.btn-success').addClass("btn-white");
            $(this).parent().find('.btn-success').removeClass("btn-success");
            $(this).removeClass("btn-white");
            $(this).addClass("btn-success");

            var dateType=$('#searchForm').find('.btn-success').attr("value");
            initStateDateV5(dateType,"start");
            initDefaultSearchValue('searchForm','start');
        });

        // 查询按钮单击事件
        $('#searchFormButton').click(function () {
            webAppSiteJs.initTable();
            webAppSiteJs.initChart();
        });

        // 查询按钮单击事件
        $('#searchFormButton2').click(function () {
            webAppSiteJs.initTable2();
            webAppSiteJs.initLine();
        });

        // 导出按钮
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
            form += '<input name="probeType" value="'+data.probeType+'" />';

            form += '<input name="imgUrl" value="' + imgUrl1 + '" />';
            form += '<input name="exportType" value="'+exportType+'" />';
            form += '<input name="startTime" value="' + data.startTime + '" />';
            form += '<input name="statType" value="'+ data.statType + '" />';
            form += '<input name="areaId" value="'+ data.areaId + '" />';
            form += '<input name="userGroupNo" value="'+ data.userGroupNo + '" />';
            form += '<input name="siteType" value="'+ data.siteType + '" />';
            form += '<input name="listType" value="1" />';

            form += '</form>';
            $('body').append(form);
            $('#export1').attr('action', urlConstant.exportUrl).attr('method', 'post').submit();
            $('#export1').remove();
        });

        // 导出按钮
        $('#exportButton222').find('li').click(function () {
            var exportType = 1;
            var text = $(this).text();
            if(text=='EXCEL'){
                exportType = 0;
            } else if (text=='WORLD'){
                exportType = 1;
            } else if (text=='PDF'){
                exportType = 2;
            }
            var data = queryParams2();
            var form = '<form class="hide" id="export2">';
            form += '<input name="probeType" value="'+data.probeType+'" />';

            form += '<input name="imgUrl" value="' + imgUrl2 + '" />';
            form += '<input name="exportType" value="'+exportType+'" />';
            form += '<input name="startTime" value="' + data.startTime + '" />';
            form += '<input name="statType" value="'+ data.statType + '" />';
            form += '<input name="areaId" value="'+ data.areaId + '" />';
            form += '<input name="userGroupNo" value="'+ data.userGroupNo + '" />';
            form += '<input name="siteType" value="'+ data.siteType + '" />';
            form += '<input name="siteName" value="'+ data.siteName + '" />';
            form += '<input name="listType" value="2" />';

            form += '</form>';
            $('body').append(form);
            $('#export2').attr('action', urlConstant.exportUrl).attr('method', 'post').submit();
            $('#export2').remove();
        });

        if (myChart1!="") {
            myChart1.on('click', function (params) {
                var siteTypeName = params.name.split("-")[0];
                var siteName = params.name.split("-")[1];
                var siteType = getSiteTypeByName(siteTypeName);
                gotoSecondPage(siteName,siteType);

            });
        }

        /*************************** 页面1相关单击事件end ****************************/

        /*************************** 页面2相关单击事件begin ****************************/
        $("#dateTypeSelect2").find("[name='dateType']").click(function(){
            $(this).parent().find('.btn-success').addClass("btn-white");
            $(this).parent().find('.btn-success').removeClass("btn-success");
            $(this).removeClass("btn-white");
            $(this).addClass("btn-success");

            // var dateType=$('#searchForm2').find('.btn-success').attr("value");
            // initStateDateV5(dateType,"start2");
            // initDefaultSearchValue('searchForm2','start2');
        });

    },

    // 加载查询下拉选数据
    loadSelectOption : function () {
        loadSel('appType',urlConstant.appTypeUrl,true,undefined,false);
        loadSelect("areaId",urlConstant.areaIdUrl);
        loadSelect("userGroupNo",urlConstant.userGroupNoUrl);
    },

    // 初始化table
    initTable : function () {
        $('#table').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: urlConstant.tableUrl,
            queryParams: queryParams,
            contentType: 'application/json',
            striped: true,
            undefinedText: '',
            sortable: true,                     //是否启用排序
            sortOrder: "desc",                   //排序方式
            pagination: true,
            sidePagination: 'server',
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn()
        });
    },

    // 初始化table
    initTable2 : function () {
        $('#hostTable').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: urlConstant.tableUrl2,
            queryParams: queryParams2,
            contentType: 'application/json',
            striped: true,
            undefinedText: '',
            sortable: true,                     //是否启用排序
            sortOrder: "desc",                   //排序方式
            pagination: true,
            sidePagination: 'server',
            clickToSelect:true,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: getColumn2()
        });
    },

    // 初始化echart图表
    initChart : function(){
        if(document.getElementById("webTypeDetailChart")==null){
            return false;
        }
        myChart1 = echarts.init(document.getElementById("webTypeDetailChart"));
        myChart1.clear();

        var data = queryParams();
        data.statType = $('#searchForm').find('.btn-success').attr("value");

        $.ajax({
            url: urlConstant.chartUrl,
            data: JSON.stringify(data),
            type: 'POST',
            async: false,
            contentType: "application/json",
            success: function (data) {
                // webAppSiteJs.option.axis = data.axis;
                webAppSiteJs.option.xAxis[0].data = data.axis;
                webAppSiteJs.option.series=data.series;
                webAppSiteJs.option.series[0].itemStyle={normal:{color:["#ffbf7a"]}}
                webAppSiteJs.option.series[1].itemStyle={normal:{color:["#70c1b3"]}}
                myChart1.setOption(webAppSiteJs.option,true);
                imgUrl1 = myChart1.getDataURL('png');//获取base64编码


            },
            error:function () {

            },

        });
    },

    // 初始化折线图
    initLine:function () {

        myChart2 = echarts.init(document.getElementById("webAppSiteChart"));
        myChart2.clear();

        var paramData = queryParams2();

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

        $.ajax({
            url: urlConstant.lineUrl,
            data: JSON.stringify(paramData),
            type: 'POST',
            async: false,
            contentType: "application/json",
            success: function (data) {

                xaxisObjArray[0] = xaxisObj;
                xaxisObjArray[0].data = data.axis;
                xaxisObjArray[1] = xaxisObj2;
                xaxisObjArray[1].data = data.axis;
                webAppSiteJs.basicLineOption.xAxis = xaxisObjArray;
                webAppSiteJs.basicLineOption.series[0].data = data.series[0].data;
                webAppSiteJs.basicLineOption.series[1].data = data.series[1].data;
                myChart2.setOption(webAppSiteJs.basicLineOption,true);
                //  折线图有动画效果，不能马上获取否则只会获取到几个点
                setTimeout(getImageData,3000);

                // console.log(imgUrl2);
            },
            error:function () {
                swal("获取数据失败");
            }
        });
    },

    init : function(){
        webAppSiteJs.loadSelectOption();
        webAppSiteJs.initTable();
        webAppSiteJs.initChart();

        webAppSiteJs.initClick();
    },
}

function getImageData() {
    imgUrl2 = myChart2.getDataURL('png');//获取base64编码
}

$(document).ready(function() {
    initStateDateV5(2,"start");
    initDefaultSearchValue('searchForm','start');
    webAppSiteJs.init();
});


/*************************************
 *     根据选择的时间粒度初始化时间搜索框*
 *                                   *
 ************************************/
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

// 根据id和url获取对应下拉选数据
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
                if (id == 'areaId' || id == 'areaId2' ){
                    if (i==0){
                        $("#"+id).prepend("<option value='-1' >省份</option>");//添加第一个option值
                    }
                    //如果在select中传递其他参数，可以在option 的value属性中添加参数
                    $("#"+id).append("<option value='"+msg.rows[i].areaCode+"'>"+msg.rows[i].areaName+"</option>");
                } else if (id=='userGroupNo' || id == 'userGroupNo2' ){
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

function queryParams(params) {
    var data = $('#searchForm').formToJSON();
    data.statType=$('#searchForm').find('.btn-success').attr("value");

    data.probeType = 0;

    if (params!=undefined && params!=''){
        data.page = params.offset/params.limit +1 ;
        data.pageSize = params.limit;
        data.sort = params.sort;      //排序列名
        data.sortOrder = params.order;
    }

    if (data.siteType==undefined||data.siteType==''){
        data.siteType = -1;
    }
    if (data.areaId==undefined||data.areaId==''){
        data.areaId = -1;
    }
    if (data.userGroupNo==undefined||data.userGroupNo==''){
        data.userGroupNo = -1;
    }
    if (data.startTime==undefined||data.startTime==''){
        data.startTime = getDefaultTimeSearchValue('searchForm');
    }

    if(data.statType==1){
        data.startTime = new Date(data.startTime+":00:00").getTime();
    } else if (data.statType==2||data.statType==3){
        data.startTime = new Date(data.startTime.trim()+" 00:00:00").getTime();
    } else if (data.statType==4){
        data.startTime = new Date(data.startTime.trim()+"-01 00:00:00").getTime();
    }
    return data;
}

// 获取table2查询条件
function queryParams2(params) {
    var data = $('#searchForm2').formToJSON();
    data.statType=$('#searchForm2').find('.btn-success').attr("value");

    data.probeType = 0;

    if (params!=undefined && params!=''){
        data.page = params.offset/params.limit +1 ;
        data.pageSize = params.limit;
        data.sort = params.sort;      //排序列名
        data.sortOrder = params.order;
    }

    if (data.siteType==undefined||data.siteType==''){
        data.siteType = -1;
    }
    if (data.areaId==undefined||data.areaId==''){
        data.areaId = -1;
    }
    if (data.userGroupNo==undefined||data.userGroupNo==''){
        data.userGroupNo = -1;
    }
    if (data.startTime==undefined||data.startTime==''){
        data.startTime = getDefaultTimeSearchValue('searchForm');
    }

    if(data.statType==1){
        data.startTime = new Date(data.startTime+":00:00").getTime();
    } else if (data.statType==2||data.statType==3){
        data.startTime = new Date(data.startTime.trim()+" 00:00:00").getTime();
    } else if (data.statType==4){
        data.startTime = new Date(data.startTime.trim()+"-01 00:00:00").getTime();
    }
    return data;
}

// table显示项
function getColumn() {
    var column =[
        {field: 'siteName',title: '<span title="Host">Host</span>',formatter:getHostLink},
        {field: 'siteTypeName',title: '<span title="网站类型">网站类型</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'siteTrafficUp',sortable:true,title: '<span title="上行流量">上行流量</span>',
            formatter:function(value,row,index){
                if (value<1024){
                    value = value + "KB";
                } else if (value < 1024*1024){
                    value = (value/1024).toFixed(2) + "MB";
                } else if (value < 1024*1024*1024){
                    value = (value/(1024*1024)).toFixed(2) + "GB";
                }
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'siteTrafficDn',sortable:true,title: '<span title="下行流量">下行流量</span>',
            formatter:function(value,row,index){
                if (value<1024){
                    value = value + "KB";
                } else if (value < 1024*1024){
                    value = (value/1024).toFixed(2) + "MB";
                } else if (value < 1024*1024*1024){
                    value = (value/(1024*1024)).toFixed(2) + "GB";
                }
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'siteTrafficSum',sortable:true,title: '<span title="总流量">总流量</span>',
            formatter:function(value,row,index){
                if (value<1024){
                    value = value + "KB";
                } else if (value < 1024*1024){
                    value = (value/1024).toFixed(2) + "MB";
                } else if (value < 1024*1024*1024){
                    value = (value/(1024*1024)).toFixed(2) + "GB";
                }
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'siteHitFreq',sortable:true,title: '<span title="点击次数">点击次数</span>',
            formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }
        }
    ];
    return column;
}

// table显示项
function getColumn2() {
    var column =[
        {field: 'statTime',title: '<span title="统计时间">统计时间</span>'},
        {field: 'siteTrafficUp',sortable:true,title: '<span title="上行流量">上行流量</span>',
            formatter:function(value,row,index){
                if (value<1024){
                    value = value + "KB";
                } else if (value < 1024*1024){
                    value = (value/1024).toFixed(2) + "MB";
                } else if (value < 1024*1024*1024){
                    value = (value/(1024*1024)).toFixed(2) + "GB";
                }
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'siteTrafficDn',sortable:true,title: '<span title="下行流量">下行流量</span>',
            formatter:function(value,row,index){
                if (value<1024){
                    value = value + "KB";
                } else if (value < 1024*1024){
                    value = (value/1024).toFixed(2) + "MB";
                } else if (value < 1024*1024*1024){
                    value = (value/(1024*1024)).toFixed(2) + "GB";
                }
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'siteTrafficSum',sortable:true,title: '<span title="总流量">总流量</span>',
            formatter:function(value,row,index){
                if (value<1024){
                    value = value + "KB";
                } else if (value < 1024*1024){
                    value = (value/1024).toFixed(2) + "MB";
                } else if (value < 1024*1024*1024){
                    value = (value/(1024*1024)).toFixed(2) + "GB";
                }
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'siteHitFreq',sortable:true,title: '<span title="点击次数">点击次数</span>',
            formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }
        }
    ];
    return column;
}

function gotoSecondPage(siteName,siteType){
    $('#detailModal').modal('show');

    // 加载第二个页面下拉选
    loadSel('appType2',urlConstant.appTypeUrl,true,undefined,true);
    loadSelect("areaId2",urlConstant.areaIdUrl);
    loadSelect("userGroupNo2",urlConstant.userGroupNoUrl);

    var data = $('#searchForm').formToJSON();
    // 设置统计类型
    data.statType = $('#searchForm').find('.btn-success').attr("value");

    // 为第二个页面初始查询条件赋值
    // $("#start2").val(data.startTime);
    $("#areaId2").val(data.areaId);
    $("#userGroupNo2").val(data.userGroupNo);
    if (data.siteType==undefined||data.siteType==''){
        $("#appType2").val(siteType);
    } else {
        $("#appType2").val(data.siteType);
    }

    $("#siteName2").val(siteName);

    var dateTypeObj = $("#dateTypeSelect2").find("[name='dateType']");
    $("#dateTypeSelect2").find('.btn-success').addClass("btn-white");
    $("#dateTypeSelect2").find('.btn-success').removeClass("btn-success");
    for (var i = 0; i < dateTypeObj.length; i++) {
        if ($("#dateTypeSelect2").find("[name='dateType']").eq(i).val() == data.statType) {
            $("#dateTypeSelect2").find("[name='dateType']").eq(i).removeClass("btn-white");
            $("#dateTypeSelect2").find("[name='dateType']").eq(i).addClass("btn-success");
            break;
        }
    }

    webAppSiteJs.initTable2();
    webAppSiteJs.initLine();
}

function getHostLink(value,row,index){
    var hostLink = '<a href="#" onclick=gotoSecondPage("'+value+'",'+row['siteType']+')>'+value+'<i class="fa fa-lg"></i></a>' ;
    return hostLink;
}

// 根据value返回自适应带单位的值
function getFormattedValue(value){
    if (value < 1024 ) {
        value = value  + "KB";
    } else if (value < 1024*1024) {
        value = (value / 1024).toFixed(2) + "MB";
    } else if (value < 1024*1024*1024) {
        value = (value / (1024*1024)).toFixed(2) + "GB";
    }
    return value;
}

// 根据siteTypeName获取对应的siteType
function getSiteTypeByName(siteTypeName){
    var siteType = 0;
    $.ajax({
        type : "POST",
        url : urlConstant.getSiteType,
        dataType : "JSON",
        async : false,
        data : {"siteTypeName":siteTypeName},
        success : function(msg)
        {
            siteType = msg.siteType;
        },error:function(){
            swal("获取数据失败","error");
        }
    });
    return siteType;
}
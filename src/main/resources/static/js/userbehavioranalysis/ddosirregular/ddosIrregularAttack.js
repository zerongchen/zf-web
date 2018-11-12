var imgUrl1 = "";
var searchAttackStartTime = "";
var searchAttackEndTime = "";
var searchUserGroupNo = "";
var searchAppAttackType = "";

urlConstant = {
    userGroupNoUrl: '/userbehavioranalysis/apptraffic/getUserGroup',

    tableUrl : '/userbehavioranalysis/ddosirregular/listData',
    chartUrl : '/userbehavioranalysis/ddosirregular/getChartData',
    exportUrl : '/userbehavioranalysis/ddosirregular/exportData',

    tableUrl2: '/userbehavioranalysis/ddosirregular/listAreaData',
},

DdosIrregularAttackJs = {

    option : {
        legend: {
            data:[]
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        dataZoom: [
            {
                id: 'dataZoomX',
                type: 'slider',
                xAxisIndex: [0],
                filterMode: 'filter', // 设定为 'filter' 从而 X 的窗口变化会影响 Y 的范围。
                start: 0,
                end: 100,
                top: '93%'
            }
        ],
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: []
        },
        yAxis: {
            axisLabel: {
                margin: 2,
                formatter: function (value, index) {
                    return getFormattedValue(value);
                }
            },
            type: 'value'
        },
        series: []
    },

    // 初始化点击事件
    initClick : function () {

        $("#returnBtn").on('click',function () {
            $("#detailModal").hide();
        });
        // 更多条件按钮单击事件
        $(".btn.btn-outline.btn-link.moreSearchCondition").on('click',function(){
            $(this).parent().parent().find('div[name="moreSearchDiv"]').toggle();
        });
        // 导出按钮
        $("#exportButton").on('click',function(){
            $('#exportModal').modal();
            loadTable();
            loadSelect("userGroupNo_export",urlConstant.userGroupNoUrl);
            $('#appAttackType_export').val(-1);
        });

        // 查询按钮单击事件
        $('#searchFormButton').click(function () {
            DdosIrregularAttackJs.initTable();
            DdosIrregularAttackJs.initChart();
        });
        $('#exportButton_export').click(function () {
            var data = $('#exportForm').formToJSON();
            var fileName="ddosIrregularAttrack_"+formatDate(new Date());
            data.file_name=fileName;
            data.userGroupNo=data.userGroupNo_export;
            data.appAttackType=data.appAttackType_export;
            data.export_status=0;
            $.ajax({
                type : "POST",
                url : "createExportTask",
                dataType : "JSON",
                async : false,
                data : data,
                success : function(msg){
                    console.log(msg);
                    if(msg.status=="0"){
                        swal("新增导出任务成功！");
                        $("#table_export").bootstrapTable('refresh');
                        loadSelect("userGroupNo_export",urlConstant.userGroupNoUrl);
                        $('#appAttackType_export').val(-1);
                    }else{
                        swal("新增导出任务失败！");
                    }
                },
                error:function(){
                    swal("新增导出任务失败！");
                }
            });
        });

        $('#downStatus').click(function () {

            loadTable();
        });
    },

    // 加载查询下拉选数据
    loadSelectOption : function () {
        loadSelect("userGroupNo",urlConstant.userGroupNoUrl);
        loadSelect("userGroupNo_export",urlConstant.userGroupNoUrl);
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

    initTable2 : function(){
        $('#areaTable').bootstrapTable('destroy').bootstrapTable({
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

        var myChart1 = echarts.init(document.getElementById("ddosIrregularAttackChart"));
        var data = queryParams();
        if (! data.ok ){
            swal(data.msg);
            return ;
        }

        $.ajax({
            url: urlConstant.chartUrl,
            data: JSON.stringify(data),
            type: 'POST',
            async: false,
            contentType: "application/json",
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
                        legend:{
                            show:true,
                            data:result.legend,
                        },
                        xAxis: {
                            type: 'category',
                            data: result.axis
                        },
                        yAxis: {
                            type: 'value',
                            axisLabel: {
                                formatter: '{value} ' + result.unit
                            }
                        },
                        series: result.series,
                        dataZoom: result.dataZoom,
                    };

                    myChart1.clear();
                    myChart1.setOption(option);
                }
            },
            error:function () {
                swal("获取数据失败");
            },

        });
    },


    init : function(){
        DdosIrregularAttackJs.loadSelectOption();
        DdosIrregularAttackJs.initTable();
        DdosIrregularAttackJs.initChart();
        DdosIrregularAttackJs.initClick();
    },
}


$(document).ready(function() {
    initDate(3,false);
    initExportDate(3);
    // initPeriodDateV5(2,"start","end");
    $("#start").val(moment().add(-7,'days').format('YYYY-MM-DD'));
    $("#end").val(moment().add(-1,'days').format('YYYY-MM-DD'));

    $("#start_export").val(moment().add(-7,'days').format('YYYY-MM-DD'));
    $("#end_export").val(moment().add(-1,'days').format('YYYY-MM-DD'));

    DdosIrregularAttackJs.init();
});

// table显示项
function getColumn() {
    var column =[
        {field: 'attackStartTime',title: '<span title="攻击开始时间">攻击开始时间</span>',formatter:function(value,row,index){
            return "<span title='"+formatUnixTimeStamp(value,'YYYY-MM-DD HH:mm')+"'>"+formatUnixTimeStamp(value,'YYYY-MM-DD HH:mm')+"</span>";
        }},
        {field: 'attackEndTime',title: '<span title="攻击结束时间">攻击结束时间</span>',formatter:function(value,row,index){
            return "<span title='"+formatUnixTimeStamp(value,'YYYY-MM-DD HH:mm')+"'>"+formatUnixTimeStamp(value,'YYYY-MM-DD HH:mm')+"</span>";
        }},
        {field: 'userGroupName',title: '<span title="受攻击用户组">受攻击用户组</span>',
            formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'appAttackTypeName',title: '<span title="攻击类型">攻击类型</span>',
            formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'appAttackTraffic',sortable:true,title: '<span title="应用层攻击流量">应用层攻击流量</span>',
            formatter:function(value,row,index){
                return "<span title='"+getFormattedValue(value)+"'>"+getFormattedValue(value)+"</span>";
            }
        },
        {field: 'appAttackRate',sortable:true,title: '<span title="应用层攻击速率">应用层攻击速率</span>',
            formatter:function(value,row,index){
                return "<span title='"+getFormattedValue(value)+"'>"+getFormattedValue(value)+"</span>";
            }
        },
        {field: 'attackAreaNum',title: '<span title="攻击源所在区域数">攻击源所在区域数</span>',
            formatter: linkForAreaPage
        },
    ];
    return column;
}

// areaTable显示项
function getColumn2(){
    var column =[
        {field: 'attackAreaName',title: '<span title="攻击来源">攻击来源</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'attackTraffic',sortable:true,title: '<span title="发起的攻击流量">发起的攻击流量</span>',
            formatter:function(value,row,index){
                return "<span title='"+getFormattedValue(value)+"'>"+getFormattedValue(value)+"</span>";
            }
        },
        {field: 'attackNum',sortable:true,title: '<span title="发起的攻击次数">发起的攻击次数</span>',
            formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'sourceIpNum',sortable:true,title: '<span title="攻击源地址数">攻击源地址数</span>',
            formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },

    ];
    return column;
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
                } else if (id=='userGroupNo' || id == 'userGroupNo2' || id == 'userGroupNo_export' ){
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

    if (params!=undefined && params!=''){
        data.page = params.offset/params.limit +1 ;
        data.pageSize = params.limit;
        data.sort = params.sort;      //排序列名
        data.sortOrder = params.order;
    }

    if (data.startTime==undefined||data.startTime==''){
        data.ok = false;
        data.msg = '开始时间不能为空';
        return data;
    } else {
        data.ok = true;
        data.startTime = new Date(data.startTime.trim()+" 00:00:00").getTime();
    }
    if (data.endTime==undefined||data.endTime==''){
        data.ok = false;
        data.msg = '结束时间不能为空';
        return data;
    } else {
        data.ok = true;
        data.endTime = new Date(data.endTime.trim()+" 00:00:00").getTime();
    }
    return data;
}

function queryParams2(params) {
    var data = $('#searchForm').formToJSON();

    if (params!=undefined && params!=''){
        data.page = params.offset/params.limit +1 ;
        data.pageSize = params.limit;
        data.sort = params.sort;      //排序列名
        data.sortOrder = params.order;
    }

    data.startTime = searchAttackStartTime+"000";
    data.endTime = searchAttackEndTime+"000";
    data.userGroupNo = searchUserGroupNo;
    data.appAttackType = searchAppAttackType;
    return data;
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

/**
 * 将指定的unix时间戳转换成指定格式
 * @param value  unix时间戳精确到s
 * @param formatter 待格式化的样式 默认为yyyy-MM-dd
 */
function formatUnixTimeStamp(value,formatter){
    var day = moment.unix(value);
    if (formatter==undefined||formatter==''){
        formatter = "yyyy-MM-dd";
    }
    return day.format(formatter);
}

/**
 *
 * @returns {string}
 */
function linkForAreaPage(value,row,index) {
    var areaLink = '';
    if (value<=0){
        areaLink+=value;
    } else {
        areaLink+='<a href="#" onclick="gotoAreaPage('+row['attackStartTime']+','+row['attackEndTime']+','+row['userGroupNo']+','+row['appAttackType']+')">'+value+'<i class="fa fa-lg"></i></a>' ;
    }

    return areaLink;
}

function gotoAreaPage(attackStartTime,attackEndTime,userGroupNo,appAttackType){
    $("#detailModal").show();
    searchAttackStartTime = attackStartTime;
    searchAttackEndTime = attackEndTime;
    searchUserGroupNo = userGroupNo;
    searchAppAttackType = appAttackType;
    DdosIrregularAttackJs.initTable2();

}

function loadTable(){
    var exporttaskmanage = {
        method: 'post',
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        pagination: true,
        /*showColumns: !0,*/
        iconSize: "outline",
        sidePagination: "server",
        dataField: "list",
        url: "loadExportTask",
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
            {
                field: 'file_name',
                title: '文件名'
            },{
                field: 'export_status',
                title: '任务状态',formatter:function (value,row,index) {
                    if (value == 0) {
                        return "<span class=' btn-primary'>未执行</span>";
                    } else if (value == 1) {
                        return "<span class=' btn-info'>正在执行</span>";
                    } else if (value == 2) {
                        return "<span class=' btn-success'>执行完成</span>";
                    }else if (value == 3) {
                        return "<span class=' btn-danger'>执行失败</span>";
                    }
                }
            },  {field: 'operating',title:'操作',formatter:operatingFormatter,events:operatingEvents }
            ]
    };
    $("#table_export").bootstrapTable('destroy').bootstrapTable(exporttaskmanage);

    //请求服务数据时所传参数
    function queryParams(params) {
        return {
            //每页多少条数据
            pageSize: params.limit,
            //请求第几页
            pageIndex: params.offset / params.limit + 1,
            download_status: $('input:radio:checked').val(),
            file_type: 448
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
}
//样式格式化
function operatingFormatter(value, row, index){

    var format="";
    if(row.export_status!=1) {
        format += "&nbsp;&nbsp;<a href='#' title='删除' class='delete'>删除</a>&nbsp;&nbsp;";
    }
    if(row.export_status==2){
        format +="&nbsp;&nbsp;<a href='#' title='下载' class='download'>下载</a>&nbsp;&nbsp;";
    }
    return format;
}

//点击事件
window.operatingEvents = {

    'click .download': function (e, value, row, index) {
        e.preventDefault();
/*        resendPolicy(row);*/
        downLoadTaskFile(index);
    },

    'click .delete': function (e, value, row, index) {
        e.preventDefault();
        swal({
            title: "确定要删除这条信息吗",
            text: "删除后将无法恢复，请谨慎操作！",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "是的，我要删除！",
            cancelButtonText: "让我再考虑一下…",
            closeOnConfirm: false,
            closeOnCancel: false
        }, function(isConfirm) {
            if(isConfirm) {
                //表格中的删除操作
                deleteTaskList(index,'/apppolicy/webpush/delete');
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}
function deleteTaskList(index,url) {
    var obj = $("#table_export").bootstrapTable('getData');
    var row = obj[index];
    var taskId = row.id;
    $.ajax({
        type : "POST",
        url : "deleteExportTask",
        dataType : "JSON",
        async : false,
        data : {taskId:taskId},
        success : function(msg){
            if(msg.status=="0"){
                swal("删除成功！", "success");
                $("#table_export").bootstrapTable('refresh');
            }else{
                swal("删除失败！", "error");
            }
        },
        error:function(){
            swal("删除失败！", "error");
        }
    });
}

function downLoadTaskFile(index){
    var obj = $("#table_export").bootstrapTable('getData');
    var row = obj[index];
    var file_name = row.file_name;
    var id = row.id;

    var form = '<form class="hide" id="downloadForm">';
    form += '<input name="file_name" value="'+ file_name + '" />';
    form += '<input name="id" value="'+ id + '" />';
    form += '</form>';
    $('body').append(form);
    $('#downloadForm').attr('action', 'downloadFile?ran='+Math.random()).attr('method', 'post').submit();
    $('#downloadForm').remove();
}

/**
 *
 * @param datetype 选择日期格式
 * @param flag 统一系统默认时间 或者为空
 */
function initExportDate(datetype,flag,isTime){

    var startTime = new Date();
    startTime.setDate(startTime.getDate()-1);

    var endTime = new Date();
    endTime.setDate(endTime.getDate());

    var dateFormate;
    var showTime;

    switch (Number(datetype)){
        case 0: dateFormate="YYYY-MM-DD hh:mm:ss"; showTime=isTime; break;
        case 1: dateFormate="YYYY-MM-DD hh:mm"; showTime=isTime; break;
        case 2: dateFormate="YYYY-MM-DD hh"; showTime=isTime; break;
        case 3: dateFormate="YYYY-MM-DD"; showTime=isTime; break;
    }
        var start1 = {
            elem: "#start_export",
            format: dateFormate,
            // min: laydate.now(),
            // max: endTime.Format(dateFormate),
            istime: showTime,
            istoday: false,
            choose: function(datas) {
                end1.min = datas;
                end1.start = datas
            }
        };
        var end1 = {
            elem: "#end_export",
            format: dateFormate,
            min:laydate.now(),
            max: "2099-06-16 23:59:59",
            istime: showTime,
            istoday: false,
            choose: function(datas) {
                start1.max = datas
            }
        };
        laydate(start1);
        laydate(end1);
}

function formatDate(time){
    var date = new Date(time);

    var year = date.getFullYear(),
        month = date.getMonth() + 1,//月份是从0开始的
        day = date.getDate(),
        hour = date.getHours(),
        min = date.getMinutes(),
        sec = date.getSeconds();
    var newTime = year + '' +
        check(month) + '' +
        check(day) + '' +
        check(hour) + '' +
        check(min) + '' +
        check(sec);
    return newTime;
}

function check(str){
    str=str.toString();
    if(str.length<2){
        str='0'+ str;
    }
    return str;
}
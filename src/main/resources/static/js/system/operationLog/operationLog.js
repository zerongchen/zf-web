
operationLogJs= {

    // 根据条件查询记录
    searchFormSubBut:function () {
        $("#searchFormButton").on('click', function(){
            initTableJs.refreshData();
        })
    },

    // @Deprecated
    initSelector : function(){
        $('#operate').on("change",function(){
            clearWarn($('#operateDiv'));
        })
        $('#userGroupType').on("change",function(){
            clearWarn($('#usertypeDiv'));
        })
    } ,

    //弹出框新增或者 @Deprecated
    addOrUpdateData:function(){
        $('#addSubmitBut').on('click', function() {

            var systemParameter = new Object();
            systemParameter.configDesc = $('#addOrupdateForm').find('input[name="configDesc"]').val();
            systemParameter.configKey = $('#addOrupdateForm').find('input[name="configKey"]').val();
            systemParameter.configValue = $('#addOrupdateForm').find('input[name="configValue"]').val();
            systemParameter.inputType = $('#addOrupdateForm').find('select[name="inputType"]').val();

            $.ajax({
                url: "/system/parameter/addOrUpdate.do",
                type: 'POST',
                data:{"configDesc":systemParameter.configDesc,"configKey":systemParameter.configKey,
                    "configValue":systemParameter.configValue,"inputType":systemParameter.inputType},
                success: function (data) {

                    $('#myModaladd').modal('hide');
                    initTableJs.refreshData();
                },
                error:function () {

                    swal("操作失败", "取消新增/删除操作！", "error");

                }

            })

        })
    },

    init:function(){
        // operationLogJs.initSelector();
        operationLogJs.searchFormSubBut();
        // operationLogJs.addOrUpdateData();
    }
}


$(document).ready(function() {
    // timeBarInit();
    initDate(3,false);
    initTableJs.initParam('/system/log/list.do','/system/log/delete.do',getColumnFunction)
    initTableJs.init2();
    operationLogJs.init();
});

function getColumnFunction(){

    var column =[
        /*{field: 'state',checkbox: true},*/
        {field: 'operUser',title: '用户名',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'operModel',title: '操作模块',
            formatter : function (value, row, index) {
                var operModel = "";
                if(row['operModel']==101001){
                    operModel = "通用类流量";
                } else if (row['operModel']==101002) {
                    operModel = "指定应用用户";
                } else if (row['operModel']==101003) {
                    operModel = "Web类流量";
                } else if (row['operModel']==101004) {
                    operModel = "应用流量流向";
                } else if (row['operModel']==101005) {
                    operModel = "非法路由用户";
                } else if (row['operModel']==101006) {
                    operModel = "1拖N检测";
                } else if (row['operModel']==101007) {
                    operModel = "Web推送结果";
                } else if (row['operModel']==101008) {
                    operModel = "CP/SP服务器";
                } else if (row['operModel']==101009) {
                    operModel = "DDoS异常流量";
                } else if (row['operModel']==101010) {
                    operModel = "特有协议信息";
                } else if (row['operModel']==101011) {
                    operModel = "HTTPGET数据";
                } else if (row['operModel']==101012) {
                    operModel = "移动终端";
                } else if (row['operModel']==101013) {
                    operModel = "VOip类流量";
                } else if (row['operModel']==101014) {
                    operModel = "Download类";
                } else if (row['operModel']==101015) {
                    operModel = "用户偏好";
                } else if (row['operModel']==101016) {
                    operModel = "P2P应用流量流向";
                } else if (row['operModel']==101017) {
                    operModel = "IP地址流量TOPN流量";
                }

                else if (row['operModel']==102001) {
                    operModel = "参数设置";
                } else if (row['operModel']==102002) {
                    operModel = "通用流量管理";
                } else if (row['operModel']==102003) {
                    operModel = "Web流量管理";
                } else if (row['operModel']==102004) {
                    operModel = "Voip流量管理";
                } else if (row['operModel']==102005) {
                    operModel = "流量标记";
                } else if (row['operModel']==102006) {
                    operModel = "指定应用用户管理";
                } else if (row['operModel']==102007) {
                    operModel = "1拖N用户管理";
                } else if (row['operModel']==102008) {
                    operModel = "Web信息推送管理";
                } else if (row['operModel']==102009) {
                    operModel = "DDoS异常流量管理";
                } else if (row['operModel']==102010) {
                    operModel = "流量镜像";
                } else if (row['operModel']==102011) {
                    operModel = "应用特征自定义";
                }else if (row['operModel']==102012) {
                    operModel = "流量流向管理";
                }
                else if (row['operModel']==103001) {
                    operModel = "用户组管理";
                } else if (row['operModel']==103002) {
                    operModel = "IP地址用户信息";
                } else if (row['operModel']==103003) {
                    operModel = "用户组策略绑定";
                }
                else if (row['operModel']==104001) {
                    operModel = "信息推送触发库";
                } else if (row['operModel']==104002) {
                    operModel = "Web分类库";
                } else if (row['operModel']==104003) {
                    operModel = "IP地址库";
                } else if (row['operModel']==104004) {
                    operModel = "应用名称对应表";
                } else if (row['operModel']==104005) {
                    operModel = "HTTPGET黑白名单";
                } else if (row['operModel']==104006) {
                    operModel = "链路信息管理";
                } else if (row['operModel']==104007) {
                    operModel = "BRAS信息管理";
                }
                else if (row['operModel']==201001) {
                    operModel = "城域网全应用流量分析";
                } else if (row['operModel']==201002) {
                    operModel = "应用流量流向分析";
                }  else if (row['operModel']==201003) {
                    operModel = "Web应用站点分析";
                } else if (row['operModel']==201004) {
                    operModel = "访问指定应用用户分析";
                } else if (row['operModel']==201005) {
                    operModel = "非法路由用户分析";
                }  else if (row['operModel']==201006) {
                    operModel = "WLAN终端分析";
                } else if (row['operModel']==201007) {
                    operModel = "1拖N用户分析";
                }

                else if (row['operModel']==202001) {
                    operModel = "IDC全应用流量分析";
                } else if (row['operModel']==202002) {
                    operModel = "IDC应用流量流向分析";
                } else if (row['operModel']==202003) {
                    operModel = "网站流量分析";
                } else if (row['operModel']==202004) {
                    operModel = "假接入真互联分析";
                }

                else if (row['operModel']==203001) {
                    operModel = "DDoS异常流量分析";
                }

                else if (row['operModel']==301001) {
                    operModel = "设备信息管理";
                } else if (row['operModel']==301002) {
                    operModel = "DPI设备静态信息";
                } else if (row['operModel']==301003) {
                    operModel = "DPI设备动态信息";
                } else if (row['operModel']==302001) {
                    operModel = "机房信息管理";
                }
                else if (row['operModel']==401001) {
                    operModel = "告警监控";
                } else if (row['operModel']==401002) {
                    operModel = "任务监控管理";
                }
                else if (row['operModel']==402001) {
                    operModel = "数据上报监控";
                } else if (row['operModel']==402002) {
                    operModel = "AAA数据监控";
                } else if (row['operModel']==402003) {
                    operModel = "HTTPGET数据监控";
                } else if (row['operModel']==402004) {
                    operModel = "流量流向文件生成监控";
                }else if (row['operModel']==402005) {
                    operModel = "全业务流量文件生成监控";
                } else if (row['operModel']==402006) {
                    operModel = "Socket数据接收监控";
                } else if (row['operModel']==402007) {
                    operModel = "SFTP数据接收监控";
                }
                else if (row['operModel']==501001) {
                    operModel = "操作日志";
                } else if (row['operModel']==501002) {
                    operModel = "系统参数设置";
                } else if (row['operModel']==501003) {
                    operModel = "地区管理";
                } else if (row['operModel']==501004) {
                    operModel = "DPI厂家管理";
                } else {
                    operModel = "其它";
                }
                return "<span title='"+operModel+"'>"+operModel+"</span>";
            }
        },
        {field: 'operType',title: '操作类型',
            formatter : function (value, row, index) {
                var opetType = "";
                if(row['operType']==1){
                    opetType = "新增";
                } else if (row['operType']==2) {
                    opetType = "修改";
                } else if (row['operType']==3) {
                    opetType = "删除";
                }  else if (row['operType']==4) {
                    opetType = "查询";
                }  else if (row['operType']==5) {
                    opetType = "全量导入";
                }  else if (row['operType']==6) {
                    opetType = "增量导入";
                }  else if (row['operType']==7) {
                    opetType = "导出";
                }  else if (row['operType']==8) {
                    opetType = "重发";
                }  else if (row['operType']==9) {
                    opetType = "生成";
                }  else if (row['operType']==10) {
                    opetType = "下载";
                }  else if (row['operType']==11) {
                    opetType = "处理";
                }  else if (row['operType']==12) {
                    opetType = "下发";
                }  else if (row['operType']==13) {
                    opetType = "设置";
                }  else {
                    opetType = "其它";
                }
                return "<span title='"+opetType+"'>"+opetType+"</span>";
            }
        },
        {field: 'blobDataJson',title: '信息ID',
            formatter:function(value,row,index){
                return "<span title='"+value+"'>"+value+"</span>";
            }
        },
        {field: 'operTime',title: '时间',
            formatter : function (value, row, index) {
                var time =  timestamp2Time(row['operTime'],'-');
                return "<span title='"+time+"'>"+time+"</span>";
            }
        },
        //去掉后面的操作栏
        // {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
    ]
    return column;
}

//两个参数，第一个是要格式化的字符串，第二个是以什么分割符将年，月，日连接
function timestamp2Time(timestamp, separator) {
    var result = "";
    if(timestamp) {
        var reg = new RegExp(/\D/, "g"); //提取数字字符串
        var timestamp_str = (''+timestamp+'').replace(reg, "");

        var d = new Date();
        d.setTime(timestamp_str);
        var year = d.getFullYear();
        var month = d.getMonth() + 1;
        var day = d.getDate();
        var hour = d.getHours();
        var minute = d.getMinutes();
        var second = d.getSeconds();
        if(month < 10) {
            month = "0" + month;
        }
        if(day < 10) {
            day = "0" + day;
        }
        if (hour<10){
            hour = "0" + hour  ;
        }
        if (minute<10){
            minute = "0" + minute  ;
        }
        if (second<10){
            second = "0" + second;
        }
        result = year + separator + month + separator + day + " " + hour + ":"+ minute + ":" + second;
    }
    return result;
}

// @Deprecated
function newAddButtonFunction(){
    $('#myModaladd').find("h5").text("新增");

    $('#addOrupdateForm').find('input[name="operModel"]').val('');
    $('#addOrupdateForm').find('input[name="operType"]').val('');
    $('#addOrupdateForm').find('input[name="dataId1"]').val('');
    $('#myModaladd').modal('show');

}

// 修改 @Deprecated
function modifyButtonFunction(row) {
    $('#myModaladd').find("h5").text("修改");
    $('#myModaladd').modal('show');

    $('#addOrupdateForm').find('input[name="operModel"]').val('');
    $('#addOrupdateForm').find('input[name="operType"]').val('');
    $('#addOrupdateForm').find('input[name="dataId1"]').val('');

}

// 删除 @Deprecated
function deleteFunction(url){

    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var ids = new Array();
    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        ids.push(item.id);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{"ids":ids},
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


function queryParams(params){
    var data = $('#searchForm').formToJSON();
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    if(data.userName==''){
        data.userName = undefined;
    }
    return data;
}


//@Deprecated
function operatingFormatter(value, row, index){
    var format=""
        +"<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>"
        +"<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>"
    return format;
}

//点击事件
//@Deprecated
window.operatingEvents = {

    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $('#myModaladd').find("h4").text("修改");
        $('#addSubmitBut').show();
        $('#addOrupdateForm').find('input[name="configKey"]').attr("readOnly",true);
        modifyButtonFunction(row);
    },

    //@Deprecated
    'click .refresh': function (e, value, row, index) {
        e.preventDefault();
        initTableJs.refreshData();
    },

    //@Deprecated
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
                deleteFunction('/system/log/delete.do');
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}
/*
 * SoftwareManage:区域设置
 *        方法： tableInit     表格初始化，已经相关按钮绑定事件
 *             editInit      编辑页面初始化
 *             refreshData   首页列表表格数据刷新
 *             save          新增和编辑保存
 *             addInit       新增页面初始化
 *             deleteData    批量删除
 *             deleteInit    数据删除
 *             init          方法初始化
 *
 *
 */
var providerNameArray = {}
var providerCodeArray = {}

var SoftwareManage = {
//列表加载
    tableInit: function () {
        var softwaremanage = {
            method: 'post',
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            pagination: true,
            showColumns: !0,
            iconSize: "outline",
            sidePagination: "server",
            dataField: "list",
            url: "getSoftwareProviderManageData",
            pageNumber: 1,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            queryParamsType: 'limit',
            queryParams: queryParams,//请求服务器时所传的参数
            responseHandler: responseHandler,
            icons: {
                refresh: "glyphicon-repeat",
                toggle: "glyphicon-list-alt",
                columns: "glyphicon-list",
            },
            toolbar: "#commonButton",
            sortable: true,//是否启用排序
            sortOrder: "asc",//排序方式
            clickToSelect: false, //是否启用点击选中行
            columns: [
                { // 列设置
                    checkbox: true // 使用单选框
                }, {
                    field: 'software_provider_name',
                    title: '厂家名称',
                    formatter: function (value, row, index) {
                        var data = "";
                        if (value == null) {
                            data = "";
                        } else {
                            data = value;
                        }
                        return "<span title='" + data + "'>" + data + "</span>";

                    }
                }, {
                    field: 'software_provider',
                    title: '厂家编号',
                    formatter: function (value, row, index) {
                        var data = "";
                        if (value == null) {
                            data = "";
                        } else {
                            data = value;
                        }
                        return "<span title='" + data + "'>" + data + "</span>";
                    }
                }, {
                    field: 'type',
                    title: '操作',
                    formatter: function (value, row, index) {
                        var deleteFlag = $("#deleteFlag").val();
                        var modify = $("#modifyFlag").val();
                        var format = "";
                        if (modify == 1) {
                            format += "<a href='#' title='编辑' data-toggle='modal' data-target='#myModaladd' class='m-r'><i class='fa fa-edit fa-lg'></i></a>";
                        }
                        if (deleteFlag == 1) {
                            format += "<a href='#' title='删除' ><i class='fa fa-close fa-lg delete'></i></a>";
                        }
                        return format;
                    },
                    events: SoftwareManage.operatingEvents
                }]
        };
        $("#table").bootstrapTable(softwaremanage);

        //请求服务数据时所传参数
        function queryParams(params) {
            return {
                //每页多少条数据
                pageSize: params.limit,
                //请求第几页
                pageIndex: params.offset / params.limit + 1,
                searchName: $("#searchName").val()
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
        $("#searchFormButton").click(function () {
            SoftwareManage.refreshData();
        });
    },
    operatingEvents: {
        'click .fa-edit': function (e, value, row, index) {
            e.preventDefault();
            //去掉前面的提示
            $('#addPlush').find('.has-error').remove();
            $('#myModaladd').find("h5").text("修改");
            $('#addOrupdateForm').find('input[name="providerName"]').val(row.software_provider_name);
            $('#addOrupdateForm').find('input[name="providerShort"]').val(row.software_provider);
            $('#addOrupdateForm').find('input[name="providerShort"]').attr("readOnly", true);
            $('#addOrupdateForm').find('#addPlush').css('display', 'none');
            $('#operate').val('update');
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
            }, function (isConfirm) {
                if (isConfirm) {
                    //表格中的删除操作
                    SoftwareManage.deleteFunction('delete', row.software_provider);
                } else {
                    swal("已取消", "取消了删除操作！", "error")
                }
            })
        }
    },
    deleteFunction: function (url, software_provider) {
        var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
        var providerShort = [];
        for (var i = 0; i < result.length; i++) {
            var item = result[i];
            providerShort.push(item.software_provider);
        }
        if (providerShort.length == 0) {
            providerShort.push(software_provider);
        }
        $.ajax({
            url: url,
            type: 'POST',
            data: {
                providerShort: providerShort,
            },
            success: function (data) {
                swal("删除成功！", "已经永久删除了这条记录。", "success");
                $("#table>tbody>.hidetr").remove();
                SoftwareManage.refreshData();
            },
            error: function () {
                swal("删除失败", "取消了删除操作！", "error");
            }
        })
    },
    addInit: function () {
        $("#add").click(function () {
            $('#myModaladd').modal();
            $('#addOrupdateForm').find('div[name="addOrupdateProvider"]').find('.fa-minus').parent().parent().parent().parent().remove();
            $('#addOrupdateForm').find('div[name="addOrupdateProvider"]').first().find('input[name="providerName"]').val("");
            $('#addOrupdateForm').find('div[name="addOrupdateProvider"]').first().find('input[name="providerShort"]').val("");
            $('#addOrupdateForm').find('div[name="addOrupdateProvider"]').first().find('input[name="providerShort"]').removeAttr("readOnly");
          //  $('#addOrupdateForm').find('input[name="providerShort"]').attr("readOnly", true);
            $('#addOrupdateForm').find('#addPlush').show();
            $('#operate').val('add');
            var pDiv = $('#addOrupdateForm').find('div[name="addOrupdateProvider"]').first().find('.has-error').remove();;

        });

        $('#addPlush').click(function () {
            var base = $('#addOrupdateForm').find('div[name="addOrupdateProvider"]').last();
            var clone = base.clone();
            clone.find('i').removeClass('fa fa-plus').addClass('fa fa-minus');
            clone.find('.fa-info-circle').remove();
            clone.find('input[name="providerName"]').val("");
            clone.find('input[name="providerShort"]').val("");
            base.after(clone);
            $('.fa-minus').click(function () {
                $(this).parent().parent().parent().parent().remove();
            })
        });

        $('#addSubmitBut').click(function () {
            var formData = $('#addOrupdateForm').formToJSON();
            var pDiv = $('#addOrupdateForm').find('div[name="addOrupdateProvider"]')
            var num = pDiv.length;
            var addName = [];
            var addCode = [];
            for (var i = 0; i < num; i++) {
                var $cDiv = pDiv.eq(i);
                var providerName = $cDiv.find('input[name="providerName"]').val();
                var providerShort = $cDiv.find('input[name="providerShort"]').val();
                //去掉前面的提示
                $cDiv.find('.has-error').remove();
                if (providerName == "") {
                    $cDiv.find('span>a').after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入厂家名称</i>');
                    return false;
                }
                if (providerShort == "") {
                    $cDiv.find('span>a').after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入厂家编码</i>');
                    return false;
                }

                if (IsInArray(providerNameArray, providerName) || IsInArray(addName, providerName)) {
                    $cDiv.find('span>a').after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 厂家名称有重复</i>');
                    return false;
                }
                //新增时判断厂家编码是否重复
                if ($('#operate').val() != 'update') {
                    if (IsInArray(providerCodeArray, providerShort) || IsInArray(addCode, providerShort)) {
                        $cDiv.find('span>a').after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 厂家编码有重复</i>');
                        return false;
                    }
                }
                addName.push(providerName);
                addCode.push(providerShort);
            }
            var methodUrl = "addProvider";
            if ($('#operate').val() == 'update') {
                methodUrl = "updateProvider";
            }
            $.ajax({
                url: methodUrl,
                type: 'POST',
                dataType: 'json',
                data: formData,
                success: function (data) {
                    $('#myModaladd').modal('hide');
                    SoftwareManage.refreshData();
                    SoftwareManage.initExistProvider();
                    $('#addOrupdateForm').find('div[name="addOrupdateProvider"]').find('.fa-minus').parent().parent().parent().parent().remove();
                    $('#addOrupdateForm').find('div[name="addOrupdateProvider"]').first().find('input[name="providerName"]').val("");
                    $('#addOrupdateForm').find('div[name="addOrupdateProvider"]').first().find('input[name="providerShort"]').val("");
                }
            })

        });
    },
    //列表数据刷新
    refreshData: function () {
        var formData = $('#searchForm').formToJSON();
        $("#table").bootstrapTable("refresh",{
            url: "getSoftwareProviderManageData",
            silent: true,
            query:formData,
            //data:formData
        });
    },
    initExistProvider: function () {
        $.ajax({
            url: "getSoftwareProvider",
            type: 'GET',
            success: function (data) {
                var d = data.rows[0];
                if (d != null && d != undefined) {
                    providerNameArray = d.software_provider_name;
                    providerCodeArray = d.software_provider;
                }
                console.log(providerNameArray);
                console.log(providerCodeArray);
            },
        });
    },
    deleteData:function () {
        $('#delete').click(function () {
            var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
            var providerShort = [];
            for (var i = 0; i < result.length; i++) {
                var item = result[i];
                providerShort.push(item.software_provider);
            }
            swal({
                title: "确定要删除厂家信息吗",
                text: "删除后将无法恢复，请谨慎操作！",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "是的，我要删除！",
                cancelButtonText: "让我再考虑一下…",
                closeOnConfirm: false,
                closeOnCancel: false
            }, function (isConfirm) {
                if (isConfirm) {
                    $.ajax({
                        url: "delete",
                        type: 'POST',
                        data: {
                            providerShort: providerShort,
                        },
                        success: function (data) {
                            swal("删除成功！", "已经永久删除了这条记录。", "success");
                            $("#table>tbody>.hidetr").remove();
                            SoftwareManage.refreshData();
                        },
                        error: function () {
                            swal("删除失败", "取消了删除操作！", "error");
                        }
                    })
                } else {
                    swal("已取消", "取消了删除操作！", "error")
                }
            })
        })
    },
    init: function () {
        SoftwareManage.tableInit();
        SoftwareManage.addInit();
        SoftwareManage.initExistProvider();
        SoftwareManage.deleteData();
        /*      SoftwareManage.deleteData();
              SoftwareManage.provinceset();*/
    }
}
function IsInArray(arr, val) {
    var testStr = ',' + arr.join(",") + ",";
    return testStr.indexOf("," + val + ",") != -1;
}
SoftwareManage.init();
function edit(index) {
    SoftwareManage.updataProvider(index);
}
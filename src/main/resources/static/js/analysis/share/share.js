var allChart=new Array();
var cos = {};
share_kw={

    loadSelectOption : function(){
        loadSel("areaId","/select/getAreaList",true);
    },
    initClick:function () {
        $('#dateTypeSelect >button').click(function () {
            $("#dateTypeSelect").find(".btn-success").removeClass("btn-success").addClass('btn-white').end();
            $(this).removeClass('btn-white').addClass('btn-success').end();
            var data = $('#searchForm').formToJSON();
            data.dateType=$('#searchForm').find('.btn-success').attr("value");
            loadDateType(data.dateType);
            share_kw.initTable();
        });
        $("#searchForm .btn.btn-outline.btn-link.moreSearchCondition").on('click',function(){
            $("#searchForm").find('div[name="moreSearchDiv"]').toggle();
        });
        $('#searchFormButton').click(function () {
            share_kw.initTable();
        })
    },
    initTable:function () {
        if(document.getElementById("table")!=null){

            $('#table').bootstrapTable('destroy').bootstrapTable({
                method: 'post',
                url: '/analysis/share/getList',
                queryParams: queryParams,
                contentType: 'application/x-www-form-urlencoded',
                striped: true,
                undefinedText: '',
                // showColumns: !0,
                pagination: true,
                sidePagination: 'server',
                sortable: true,//是否启用排序
                sortOrder: "desc",//排序方式
                // iconSize: "outline",
                // icons: {
                //     columns: "glyphicon-list",
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
        }
    },

    init:function () {
        loadDateType(2);
        share_kw.loadSelectOption();
        share_kw.initClick();
        share_kw.initTable();
    }


};
$(document).ready(function() {
    share_kw.init();
    initExportClick("searchForm",'export',$('#searchForm').find('.btn-success').attr("value"));
});
function loadDateType(datetype) {
    initStateDateV5(datetype,"state",true);
}
function getColumn() {
   let column=[
        {field: 'useraccount',title: '用户帐号'},
        {field: 'userip',title: '用户IP'},
        {field: 'hostcnt',title: '主机数（台）',sortable:true},
        {field: 'qqnocnt',title: 'QQ数量(条)',sortable:true},
        {field: 'natipcnt',title: 'NatIP数量(条)',sortable:true},
        {field: 'cookiecnt',title: 'Cookie数量(个)',sortable:true},
        {field: 'devnamecnt',title: '设备数量(台)',sortable:true},
        {field: 'osnamecnt',title: '操作系统数量(台)',sortable:true},
    ];
    return column;
}
function queryParams(params) {
    var data = $('#searchForm').formToJSON();
    data.dateType=$('#searchForm').find('.btn-success').attr("value");
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    if(data.stateTime==''){
        data.stateTime=undefined;
    }
    data.sort=params.sort;
    data.order=params.order;
    return data;
}
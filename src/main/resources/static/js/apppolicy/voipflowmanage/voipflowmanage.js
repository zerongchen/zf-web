var voipFlow = {
	"voipFlowId":'',
    "messageNo":'',
	"messageName":'',
	"interfereType":'', //干扰类型
	"interfereDir":'',  //干扰方向
	"gwIp":"", //网关IP
	"gwKeeperIp":"", //网守IP
	"rStartTime":"",    //策略开始时间
	"rEndTime":"",      //策略结束时间
	"cTime":"",  //策略管理时间
	"userType":"",     //用户类型
	"userNames":"",     //用户名称      多个用户用逗号,隔开
	"userGroupIds":""     //用户组        多个用户组用逗号隔开
};

//全用户时显示的div
function allUserDiv() {
    $('#userNameDiv').hide();
    $('#puserGroup').hide();
    $('#userNameDiv .newInput').remove();
    $('#userType').val(0);
}

// 用户类型为账号或者IP时显示的div
function otherUserTypeDiv(){
    $('#userNameDiv').show();
    $('#puserGroup').hide();
    $('#userNameDiv .newInput').remove();
}

// 用户组时显示的div
function userGroupDiv() {
    $('#userNameDiv').hide();
    $('#puserGroup').show();
    $('#userNameDiv .newInput').remove();
}

//点击+追加input框,编辑根据userName 个数回填
function addInputEvent() {
    var inputPlus = "<div class='form-group newInput' ><label class='col-md-2 control-label p-n'></label>"
        +"<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
        +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
        +"</div>";
    $("#userNameDiv .form-group").last().after(inputPlus);
    $(".check-minus").click(function() {
        $(this).parent().parent().parent().remove()
    });
}

voipFlowJs= {



    // 根据条件查询记录
    searchFormSubBut:function () {
        $("#searchFormButton").on('click', function(){
            initTableJs.refreshData();
        })
    },

    initSelector : function(){

        $('#operate').on("change",function(){
            clearWarn($('#operateDiv'));
        })
        $('#userGroupType').on("change",function(){
            clearWarn($('#usertypeDiv'));
        })

        $('#newAddGroup').click(function () {
            document.getElementById("createUserGroupForm").reset();
            $('#add-usergroup-snippet').modal('show');
            $('#operate').val(0);
            $('#operateDiv').hide();
        });

		//绑定新增或者删除账号输入框
        $("#addPlush").click(function() {
            var inputPlus = "<div class='form-group newInput' ><label class='col-md-2 control-label p-n'></label>"
                +"<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
                +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
                +"</div>";
            $(this).parent().parent().parent().after(inputPlus);
            $(".check-minus").click(function() {
                $(this).parent().parent().parent().remove();
            });
        });

        // 用户类型改变时绑定改变事件
        $('#userType').change(function () {
            clearWarn($('#addOrupdateForm'));
            var userType = this.value;
            $('#addOrupdateForm').find('input[name="userName"]').val('');
            if(userType == 0){
                allUserDiv();
            }else if(userType == 1 || userType == 2){
                otherUserTypeDiv();
            }else if(userType == 3){
                userGroupDiv();
                loadUserGroup();
            }

        });
    } ,


    //弹出框确定按钮单击事件
    addOrUpdateData:function(){
        $('#addSubmitBut').on('click', function() {

            $(".has-error").remove();
            
            var formData = $('#addOrupdateForm').formToJSON();

            voipFlow.userType = formData.userType;

            if(formData.userType == 1 || formData.userType == 2){
                var names = "";
                var $obj = $('#addOrupdateForm').find('input[name="userName"]');
                var num = $obj.length;
                for (var i=0;i<num;i++){
                    var value  = $obj.eq(i).val();
                    if(value==''){
                        $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入</i>');
                        $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                        return false;
                    }
                    if(formData.userType == 1){
                        if(value.length>50){
                            $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入长度不超过50个字符</i>')
                            $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                            return false;
                        }
                    }else {
                        if(!checkIP(value)){
                            $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入正确IP地址用户</i>')
                            $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                            return false;
                        }
                    }
                    names += value;
                    names += ",";
                }
                voipFlow.userNames = names;
                voipFlow.userGroupIds = undefined;

            }else if(formData.userType == 3){
                if(formData.puserGroup==undefined){
                    swal("请选择关联的用户组");
                    return false;
                }
                voipFlow.userGroupIds = formData.puserGroup;
                voipFlow.userNames = undefined;
            }else {
                voipFlow.userGroupIds = undefined;
                voipFlow.userNames = undefined;
            }

            var url = "";
            if( (voipFlow.voipFlowId==undefined||voipFlow.voipFlowId=='')
                && (voipFlow.messageNo==undefined||voipFlow.messageNo=='') ){
                url = '/apppolicy/voipflow/add.do';
            } else {
                url = '/apppolicy/voipflow/update.do';
            }

            // 检查所有参数
            if (checkAllParameter()){

                filterParameter(voipFlow);

                $.ajax({
                    url: url,
                    type: 'POST',
                    dataType:'json',
                    // contentType: 'application/json',
                    // data:JSON.stringify(voipFlow),
                    data:voipFlow,
                    success: function (data) {
                        if (data.result==0){
                            $("#messageNo").val('');
                            $('#myModaladd').modal('hide');
                            swal(data.message);
                            initTableJs.refreshData();
                        } else {
                            swal("操作失败", "存在策略名称相同数据", "error");
                        }

                    },
                    error:function () {
                        swal("操作失败", "取消新增/删除操作！", "error");
                    }

                })
            }

        })
    },

    init:function(){
        voipFlowJs.initSelector();
        voipFlowJs.searchFormSubBut();
        voipFlowJs.addOrUpdateData();

        addUserGroupJs.createUserGroup();
        addUserGroupJs.exportTemplate();




    }
}

$(document).ready(function() {
    timeBarInit();
    initDate(3,false);
    initSearchDate(3,false);

    initTableJs.initParam('/apppolicy/voipflow/list.do','/apppolicy/voipflow/delete.do',getColumnFunction)
    initTableJs.init2();
    voipFlowJs.init();
});

function loadUserGroup() {
    loadSelectPicker('puserGroupid','/select/getUserGroup',false);
}

// 保存前对参数进行过滤  去掉为设值的参数
function filterParameter(voipFlow){
    if (voipFlow.voipFlowId==''){
        voipFlow.voipFlowId = undefined;
    }
    if (voipFlow.messageNo==''){
        voipFlow.messageNo = undefined;
    }
    if (voipFlow.rStartTime==''){
        var now = new Date();
        var year = now.getFullYear();
        var month = now.getMonth()+1;
        var day = now.getDate();
        voipFlow.rStartTime = new Date(year+"-"+month+"-"+day+" 00:00:00").getTime()/1000;
    } else {
        voipFlow.rStartTime = new Date(voipFlow.rStartTime+" 00:00:00").getTime()/1000;
    }
    if (voipFlow.rEndTime==''){
        voipFlow.rEndTime = 0;
    } else {
        voipFlow.rEndTime = new Date(voipFlow.rEndTime+" 00:00:00").getTime()/1000;
    }
    //当多网关/网守时去掉拼接时多余的逗号
    voipFlow.gwIp=(voipFlow.gwIp.substring(voipFlow.gwIp.length-1)==',')?voipFlow.gwIp.substring(0,voipFlow.gwIp.length-1):voipFlow.gwIp;
    voipFlow.gwKeeperIp=(voipFlow.gwKeeperIp.substring(voipFlow.gwKeeperIp.length-1)==',')?voipFlow.gwKeeperIp.substring(0,voipFlow.gwKeeperIp.length-1):voipFlow.gwKeeperIp;
}

// 参数合法性校验
function checkAllParameter() {

    voipFlow.voipFlowId = $("#voipFlowId").val();
    voipFlow.messageNo = $("#messageNo").val();

    voipFlow.messageName = $('#messageName').val();
    voipFlow.interfereType = $('#interfereTypeSel').val();
    voipFlow.interfereDir = $('#interfereSel').val();
    voipFlow.gwIp = "";
    voipFlow.gwKeeperIp = "";
    voipFlow.rStartTime = $('#start').val();
    voipFlow.rEndTime = $("#end").val();

    if (voipFlow.messageName==undefined||voipFlow.messageName==''){
        warn("nameMessage","请输入策略名称");
        // swal("操作失败", "请输入策略名称", "error");
        return false;
    }
    if (voipFlow.interfereType==undefined||voipFlow.interfereType==''){
        warnSelect("interfereTypeSelDiv");
        // swal("操作失败", "请选择干扰类型", "error");
        return false;
    }
    if (voipFlow.interfereDir==undefined||voipFlow.interfereDir==''){
        warnSelect("dirMessage");
        // swal("操作失败", "请选择干扰方向", "error");
        return false;
    }
    //网关IP
    var voipGws = $('#gwIp').val().trim().split("\n");
    for(var i=0;i<voipGws.length;i++){
        if (!checkIP(voipGws[i])){
            warn("gwMessage","请输入正确的网关IP");
            // swal("操作失败", "请输入正确的网关IP", "error");
            return false;
        }
        voipFlow.gwIp += voipGws[i]+",";
    }

    //网守IP
    var voipGwKeppers = $('#gwKeeperIp').val().trim().split("\n");
    for(var i=0;i<voipGwKeppers.length;i++){
        if (!checkIP(voipGwKeppers[i])){
            warn("keeperMessage","请输入正确的网守IP");
            // swal("操作失败", "请输入正确的网守IP", "error");
            return false;
        }
        voipFlow.gwKeeperIp += voipGwKeppers[i]+",";
    }

    //管理时间
    var timeBar = getTimeBarValue("voipTimebar");
    if(timeBar.size==0){
        var html = '<div class="col-md-2 has-error"><span class="help-block m-b-none"><i class="fa fa-info-circle"></i>请设置管理时间</span></div>';
        $(".timeul").parent().after(html);
        return false;
    }
    voipFlow.cTime = parseInt(timeBar.value,2);
    return true;
}

// 获取所有参数值
function revertProperties(row){

    voipFlow.voipFlowId = row.voipFlowId;
    voipFlow.messageNo = row.messageNo;

    $('#voipFlowId').val(row.voipFlowId);
    $('#messageNo').val(row.messageNo);
    $('#messageName').val(row.messageName);
    $('#interfereTypeSel').val(row.interfereType);
    $('#interfereSel').val(row.interfereDir);
    // 设置网关网守ip
    var gwIps = removeLastComma(removeFirstComma(row.gwIp)).split(",");
    var gwIpValue = "";
    for (var i=0;i<gwIps.length;i++){
        gwIpValue += gwIps[i] + "\n";
    }
    $('#gwIp').val(gwIpValue.trim());

    var gwKeeperIps = removeFirstComma(row.gwKeeperIp).split(",");
    var gwKeeperIpValue = "";
    for (var i=0;i<gwKeeperIps.length;i++){
        gwKeeperIpValue += gwKeeperIps[i] + "\n";
    }
    $('#gwKeeperIp').val(gwKeeperIpValue.trim());

    // 设置策略有效期
    $('#start').val(timestamp2Date(row.rstartTime+"000","-")).attr("disabled",true);
    if (row.rendTime==0){
        $("#end").val('').attr("disabled",true);
    } else {
        $("#end").val(timestamp2Date(row.rendTime+"000","-")).attr("disabled",true);
    }

    //管理时间赋值
    var timeBar = row.ctime.toString(2).split("");
    setTimeBar(timeBar);
    // 还原用户组信息
    revertUserTypeInfo(row);
}

//还原用户组信息
function revertUserTypeInfo(row){
    // 还原用户组信息
    var userType = row.userType;
    clearWarn($('#addOrupdateForm'));
    $('#addOrupdateForm').find('input[name="userName"]').val('');
    if(userType == 0){
        allUserDiv();
    }else if(userType == 1 || userType == 2){
        otherUserTypeDiv();
        $('#userType').val(userType);
        var userNames = row.userNames.split(",");
        for (var i =0;i<userNames.length;i++){
            if(i==0){
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(userNames[i]);
            }else {
                addInputEvent();
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(userNames[i]);
            }
        }
    }else if(userType == 3){
        userGroupDiv();
        $('#userType').val(userType);
        loadSelectPicker('puserGroupid','/select/getUserGroup',false,row.userGroupIds.split(","));
    }
}

// 设置参数值
function cleanAllInput(){
    $('#voipFlowId').val('');
    $('#messageNo').val('');
    $('#messageName').val('');
    $('#interfereTypeSel').val('');
    $('#interfereSel').val('');
    $('#gwIp').val('');
    $('#gwKeeperIp').val('');
    $('#start').val('').attr("disabled",false);
    $("#end").val('').attr("disabled",false);

    $(".timeul>li>input[type='button']").attr('class', '');
    //清空用户组信息
    allUserDiv();
    voipFlow.voipFlowId = "";
    voipFlow.messageNo = "";
    voipFlow.messageName = "";
    voipFlow.interfereType = "";
    voipFlow.interfereDir = "";
    voipFlow.gwIp = "";
    voipFlow.gwKeeperIp = "";
    voipFlow.rStartTime = "";
    voipFlow.rEndTime = "";
    voipFlow.cTime = "";
}

function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'messageNo',title: '策略ID'},
        {field: 'messageName',title: '<span title="策略名称">策略名称</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'interfereType',title: '<span title="干扰类型">干扰类型</span>',
            formatter:function(value,row,index){
            	var typeStr = "";
                if(row['interfereType']==1){
                	typeStr = "信令干扰";
                }else if (row['interfereType']==2){
                	typeStr = "噪音干扰";
                }else if (row['interfereType']==32){
                	typeStr = "回音干扰";
                }else if (row['interfereType']==33){
                	typeStr = "其它";
                }
                return "<span title='"+typeStr+"'>"+typeStr+"</span>";
            }
        },
        {field:'appPolicy',title:'<span title="策略成功数/异常数">策略成功数/异常数</span>',
            formatter:function(value,row,index){
                if(value=="0/0"){
                    return "0/0";
                }else{
                    return "<a href='#' onclick='getDetail(0,"+index+")' title='详情'>"+value+"</a>";
                }
            }
        },
        {field:'bindPolicy',title:'<span title="绑定成功数/异常数">绑定成功数/异常数</span>',
            formatter:function(value,row,index){
                if(value=="0/0"){
                    return "0/0";
                }else{
                    return "<a href='#' onclick='getDetail(1,"+index+")' title='详情'>"+value+"</a>";
                }
            }
        },
        {field: 'rStartTime',title: '<span title="策略开始时间">策略开始时间</span>',
            formatter : function (value, row, index) {
            	var time1 = timestamp2Date(row['rstartTime']+"000",'-');
                return "<span title='"+time1+"'>"+time1+"</span>";
            }
        },
        {
            field: 'rEndTime', title: '<span title="策略结束时间">策略结束时间</span>',
            formatter: function (value, row, index) {
                var time2 = "";
                if(row['rendTime']==0) {
                    time2 = '长期有效'
                } else {
                    time2 = timestamp2Date(row['rendTime'] + "000", '-');
                }
                return "<span title='" + time2 + "'>" + time2 + "</span>";
            }
        },
        {field: 'createTime',title: '<span title="创建时间">创建时间</span>',
            formatter : function (value, row, index) {
            	var time3 = timestamp2Time(row['createTime'],'-');
                return "<span title='"+time3+"'>"+time3+"</span>";
            }
        },

        {field: 'createOper',title: '<span title="操作账号">操作账号</span>',
            formatter : function (value, row, index) {
                return "<span title='"+value+"'>"+value+"</span>";
            }},
        {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
    ]
    return column;
}



function newAddButtonFunction(){
    $(".has-error").remove();
    $('#myModaladd').find("h5").text("新增");
    cleanAllInput();

}


// 修改
function modifyButtonFunction(row) {
    $(".has-error").remove();
    $('#myModaladd').find("h5").text("修改");
    $('#myModaladd').modal('show');
    revertProperties(row);

}


// 删除
function deleteFunction(url,id){
    // debugger;
    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var voipFlowIds = new Array();
    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        voipFlowIds.push(item.voipFlowId);
    }
    if (voipFlowIds.length==0){
        voipFlowIds.push(id);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{"voipFlowIds":voipFlowIds},
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
    if(data.messageName==''){
        data.messageName = undefined;
    }
    if (data.startTime==''){
        data.startTime = undefined;
    }
    if (data.endTime==''){
        data.endTime = undefined;
    }
    return data;
}


//样式格式化
function operatingFormatter(value, row, index){
    // 策略在有效期才有重发按钮
    var needResend = false;
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var todayTimestamp = Date.parse(year+"-"+month+"-"+day+" 00:00:00");
    var now = (todayTimestamp/1000);

    if (row['rstartTime'] <= now && (now <= row['rendTime']||row['rendTime']==0)){
        needResend = true;
    };
    var format="";
	var redo = $("#redoFlag").val();
	var deleteFlag = $("#deleteFlag").val();
	var modify = $("#modifyFlag").val();
	if(redo==1){
		 if ( needResend){
		        format += "<a href='#' title='重发' class='m-r'><i class='fa fal fa-share resend'></i></a>";
		    }
	}
	if(modify==1){
		format += "<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
	}
	if(deleteFlag==1){
		format += "<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
	}
    return format;
}

//点击事件
window.operatingEvents = {

    'click .resend': function (e, value, row, index) {
        e.preventDefault();
        resendPolicy(row);
    },

    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $('#myModaladd').find("h4").text("修改");
        $('#addSubmitBut').show();

        modifyButtonFunction(row);
    },


    'click .refresh': function (e, value, row, index) {
        e.preventDefault();
        initTableJs.refreshData();
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
                deleteFunction('/apppolicy/voipflow/delete.do',row.voipFlowId);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}

// 去掉字符串最后一个逗号
function removeLastComma(str){
    str=(str.substring(str.length-1)==',')?str.substring(0,str.length-1):str;
    return str;
}

// 去掉字符串第一个逗号
function removeFirstComma(str){
    str=(str.substring(0)==',')?str.substring(1,str.length):str;
    return str;
}

/**
 *
 * @param datetype 选择日期格式
 * @param flag 统一系统默认时间 或者为空
 */
function initSearchDate(datetype,flag){

    var startTime = new Date();
    startTime.setDate(startTime.getDate()-1);
    startTime.setHours(0)
    startTime.setMinutes(0)
    startTime.setSeconds(0)

    var endTime = new Date();
    endTime.setDate(endTime.getDate()-1);
    endTime.setHours(23)
    endTime.setMinutes(59)
    endTime.setSeconds(59)

    var dateFormate="YYYY-MM-DD hh:mm:ss";
    var showTime=true;
    if(datetype ==2){
        dateFormate="YYYY-MM-DD hh";
        showTime=true;
    }else if(datetype ==3){
        dateFormate="YYYY-MM-DD";
        showTime=false;
    }
    var start = {
        elem: "#searchStart",
        format: dateFormate,
        // min: laydate.now(),
        max: endTime.Format(dateFormate),
        istime: showTime,
        istoday: false,
        choose: function(datas) {
            end.min = datas;
            end.start = datas
        }
    };
    var end = {
        elem: "#searchEnd",
        format: dateFormate,
        min:startTime.Format(dateFormate),
        max: "2099-06-16 23:59:59",
        istime: showTime,
        istoday: false,
        choose: function(datas) {
            start.max = datas
        }
    };
    var start1 = {
        elem: "#searchStart",
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
        elem: "#searchEnd",
        format: dateFormate,
        min:laydate.now(),
        max: "2099-06-16 23:59:59",
        istime: showTime,
        istoday: false,
        choose: function(datas) {
            start1.max = datas
        }
    };
    if(flag){
        laydate(start);
        laydate(end);
        $('#searchStart').val(startTime.Format(dateFormate));
        $('#searchEnd').val(endTime.Format(dateFormate));
    }else {
        laydate(start1);
        laydate(end1);
    }
}

function getDetail(showTable,index){
    var webFlow = $("#table").bootstrapTable('getData');
	var redo = $("#redoFlag").val();
    if(redo==1){
    	PolicyDetail.showDetail(webFlow[index].messageNo,5,2,1,showTable);
    }else{
    	PolicyDetail.showDetail(webFlow[index].messageNo,5,2,0,showTable);
    }
    
}

// 重发voip流量管理策略
// 重发指定应用用户策略
function resendPolicy(row){
    $.ajax({
        url: "/apppolicy/voipflow/resend.do",
        type: 'POST',
        dataType:'json',
        data:{"messageNo":row.messageNo},
        success: function (data) {
            swal("策略重发成功！");
        },
        error:function () {
            swal("操作失败", "重发策略失败！", "error");
        }
    })
}



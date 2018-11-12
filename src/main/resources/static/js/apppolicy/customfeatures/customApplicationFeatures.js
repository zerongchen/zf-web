var ipPortcount = 1;
var maxLength = 999999999;

var customFeatureStrategy = {
    "messageNo":"",
    "messageName":"",
    "appType":"",
    "appId":"",
    "appName":"",
    "userType":"",
    "userNames":new Array(),
    "userGroupIds":new Array(),
    "signature":new Array()
}

/********************用户组相关js begin*******************/
// userType=0时用户组状态
function userTypeOne(){
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
/********************用户组相关js end*******************/


/********************源IP目标IP增删节点js begin**********************************/
ipPortPlusOrMinusJs = {
    //初始化源目标ip增加单击事件
    initIpPortInputPlus : function(){
        $(document).on('click',"#ipPortAddButton",function () {
            if (ipPortcount>=maxLength){
                swal("最多只能输入3组");
            } else {
                var inputPlus =  '';
                inputPlus += '<div name="ip-port-group" class="ip-port-group">';

                inputPlus += '  <div class="form-group" >';
                inputPlus += '      <label class="col-md-2 control-label p-n">类型</label>';
                inputPlus += '      <div class="col-md-1"><select class="form-control"  name="protocolType" >';
                inputPlus += '          <option selected="" value="6">TCP</option><option value="17">UDP</option>';
                inputPlus += '      </select></div>';
                inputPlus += '      <label class="col-md-1 control-label p-n">源IP:端口  </label>';
                inputPlus += '      <div class="col-md-2"><input type="text" name="srcIp" class="form-control"/></div>';
                inputPlus += '      <div class="col-md-1"><input type="text" name="srcPort" class="form-control"/></div>';
                inputPlus += '      <label class="col-md-1 control-label p-n">目标IP:端口  </label>';
                inputPlus += '      <div class="col-md-2"><input type="text" name="destIp" class="form-control"/></div>';
                inputPlus += '      <div class="col-md-1"><input type="text" name="destPort" class="form-control"/></div>';
                inputPlus += '      <div class="col-md-1 "><span class="help-block m-b-none" >';
                inputPlus += '          <a class="sourse-a m-l-n m-r-sm check-minus ip-port-kw"><i class="fa fa-minus"></i></a>';
                inputPlus += '      </span></div>';
                inputPlus += '  </div>';

                inputPlus += '  <div name="inlineDiv" >';
                inputPlus += '      <div class="form-group kw-group">';
                inputPlus += '          <input type="hidden" class="col-md-1 "/>';
                inputPlus += '          <label class="col-md-2 control-label p-n">关键字偏移位置</label>';
                inputPlus += '          <div class="col-md-2">';
                inputPlus += '              <select class="form-control" name="keyWordOffSetPosition" >';
                inputPlus += '                   <option selected="" value="0">从载荷头</option><option value="1">从载荷尾</option>';
                inputPlus += '              </select>';
                inputPlus += '          </div>';
                inputPlus += '          <label class="col-md-1 control-label p-n">偏移量</label>';
                inputPlus += '          <div class="col-md-2"><input type="text" name="offSet" class="form-control" /></div>';
                inputPlus += '          <label class="col-md-1 control-label p-n">关键词</label>';
                inputPlus += '          <div class="col-md-2"><input type="text" name="keyWord" class="form-control" /></div>';
                inputPlus += '          <div class="col-md-1 "><span class="help-block m-b-none" >';
                inputPlus += '              <a class="sourse-a m-l-n m-r-sm check-plus kw"><i class="fa fa-plus kw"></i></a>';
                inputPlus += '          </span></div>';
                inputPlus += '      </div>';
                inputPlus += '  </div>';
                inputPlus += '</div>';
                $(this).parent().parent().parent().parent().parent().after(inputPlus);
                ipPortcount++;


            }
        });

    },

    //初始化源目标ip减少单击事件
    initIpPortInputMinus :function(){
        $(document).on('click',".check-minus.ip-port-kw",function () {
            $(this).parent().parent().parent().parent().remove();
            ipPortcount--;

        });
    },

    kwInputPlus : function() {
        $(document).on('click',".check-plus.kw",function () {

            var inputPlus =  '<div class="form-group kw-group" >';
            inputPlus +=     '    <input type="hidden" class="col-md-2 "><label class="col-md-2 control-label p-n">关键字偏移位置</label>';
            inputPlus +=     '    <div class="col-md-2"><select class="form-control" name="keyWordOffSetPosition" >';
            inputPlus +=     '         <option selected="" value="0">从载荷头</option><option value="1">从载荷尾</option>';
            inputPlus +=     '    </select></div>';
            inputPlus +=     '    <label class="col-md-1 control-label p-n">偏移量</label>';
            inputPlus +=     '    <div class="col-md-2"><input type="text"  name="offSet" class="form-control" /></div>';
            inputPlus +=     '    <label class="col-md-1 control-label p-n">关键词</label>';
            inputPlus +=     '    <div class="col-md-2"> <input type="text" name="keyWord" class="form-control" /></div>';
            inputPlus +=     '    <div class="col-md-1">';
            inputPlus +=     '         <span class="help-block m-b-none tips">';
            inputPlus +=     '               <a class="m-l-n m-r-sm check-minus kw"><i class="fa fa-minus" aria-hidden="true"></i></a>';
            inputPlus +=     '         </span>';
            inputPlus +=     '    </div>';
            inputPlus +=     '</div>';
            $(this).parent().parent().parent().after(inputPlus);
        });
    },

    kwInputMinus : function(){
        $(document).on('click',".check-minus.kw",function () {
            $(this).parent().parent().parent().remove();
        });
    },

    init:function(){
        ipPortPlusOrMinusJs.initIpPortInputPlus();
        ipPortPlusOrMinusJs.initIpPortInputMinus();
        ipPortPlusOrMinusJs.kwInputPlus();
        ipPortPlusOrMinusJs.kwInputMinus();
    }
}
/*********************源IP目标IP增删节点js end*********************************/


/*******************流量标记相关事件绑定*********************/
customFeaturesJs= {

    // 查询按钮单击事件绑定
    searchFormSubBut:function () {
        $("#searchFormButton").on('click', function(){
            initTableJs.refreshData();
        })
    },

    // 初始化appType和appId下拉选
    initAppTypeAndAppIdSelector : function () {
        loadSel('appType','/select/getAppType',true,undefined,true);
        $('#appType').on("change",function(){
            loadSel('appId','/select/getAppByType',true,{appType:$("#appType").val()},true,undefined);
        })

        $('#appId').on("change",function(){
            initAppNameInput(this);
        })
    },


    // 初始化用户组相关操作
    initUserGroup :function () {

        $('#newAddGroup').click(function () {
            document.getElementById("createUserGroupForm").reset();
            $('#add-usergroup-snippet').modal('show');
            $('#operate').val(0);
            $('#operateDiv').hide();
        });

        $("#addPlush").click(function() {
            var inputPlus = "<div class='form-group newInput' ><label class='col-md-2 control-label p-n'></label>"
                +"<div class='col-md-3'><input type='text' name='userName' class='form-control'></div>"
                +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus user-group'><i class='fa fa-minus'></i></a></span></div>"
                +"</div>";
            $(this).parent().parent().parent().after(inputPlus);
            $(".check-minus.user-group").click(function() {
                $(this).parent().parent().parent().remove();
            });
        });

        $('#userType').change(function () {
            clearWarn($('#addOrupdateForm'));
            var userType = this.value;

            $('#addOrupdateForm').find('input[name="userName"]').val('');
            if(userType == 0){
                userTypeOne();
            }else if(userType == 1 || userType == 2){
                otherUserTypeDiv();
            }else if(userType == 3){
                userGroupDiv();
                loadUserGroup();
            }

        });
    },

    clearWarnDiv : function(){
        $('#operate').on("change",function(){
            clearWarn($('#operateDiv'));
        })
        $('#userGroupType').on("change",function(){
            clearWarn($('#usertypeDiv'));
        })
    } ,

    //弹出框新增或者
    addOrUpdateData:function(){
        $('#addSubmitBut').on('click', function() {
            $(".has-error").remove();
            
            var formData = $('#addOrupdateForm').formToJSON();
            var url = "";
            var names = new Array();
            var puserGroup = new Array();

            customFeatureStrategy.messageNo = $("#messageNo").val();
            customFeatureStrategy.definedId = $("#definedId").val();
            if (customFeatureStrategy.messageNo ==undefined || customFeatureStrategy.messageNo == ''){
                url = "/apppolicy/customfeatures/save.do";
            } else {
                url = "/apppolicy/customfeatures/update.do";
            }

            if(formData.userType == 1 || formData.userType == 2){

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
                    names.push(value);
                }
                formData.userName = names;
                formData.puserGroup=undefined;
            }else if(formData.userType == 3){

                if(formData.puserGroup==undefined){
                    swal("请选择关联的用户组");
                    return false;
                }

                puserGroup = formData.puserGroup.split(",");
                formData.userName = undefined;
                formData.puserGroup = puserGroup;
            }else {
                formData.puserGroup = undefined;
                formData.userName = undefined;
            }

            customFeatureStrategy.messageName = $('#addOrupdateForm').find('input[name="messageName"]').val();
            if (customFeatureStrategy.messageName==undefined||customFeatureStrategy.messageName==''){
                // swal("操作失败", "请输入策略名称", "error");
                warn("messageName","请输入策略名称");
                return;
            }
            customFeatureStrategy.appType = $('#appType').val();
            if (customFeatureStrategy.appType==undefined||customFeatureStrategy.appType==''){
                // swal("操作失败", "请选择应用类型", "error");
                warnSelect("appTypeDiv");
                return;
            }
            customFeatureStrategy.appId = $('#appId').val();
            if (customFeatureStrategy.appId==undefined||customFeatureStrategy.appId==''){
                // swal("操作失败", "请选择子应用类型", "error");
                warnSelect("appIdDiv");
                return;
            }

            customFeatureStrategy.appName = $('#addOrupdateForm').find('input[name="appName"]').val();
            if (customFeatureStrategy.appId==0){
                if (customFeatureStrategy.appName==undefined||customFeatureStrategy.appName==''){
                    // swal("操作失败", "请输入自定义子应用名", "error");
                    warn("appName","请输入自定义子应用名");
                    return;
                }
            }

            customFeatureStrategy.userType = $('#userType').val();
            customFeatureStrategy.userNames = names;
            customFeatureStrategy.userGroupIds = puserGroup;

            var signatureArray = new Array();
            var length = $("#outlineDiv .ip-port-group").length;
            for (var i=0;i<length;i++){
                var appDefinedQuintet = {
                    "quintetId":"",
                    "definedId":"",
                    "protocol":"",
                    "sourceIp":"",
                    "sourcePort":"",
                    "destIp":"",
                    "destPort":"",
                    "kw":new Array(),
                }

                var ipPortObj = $("#outlineDiv .ip-port-group").eq(i);
                appDefinedQuintet.protocol =  ipPortObj.find("select[name='protocolType']").val();
                if (appDefinedQuintet.protocol==undefined||appDefinedQuintet.protocol==''){
                    swal("操作失败", "请选择类型", "error");
                    return;
                }
                appDefinedQuintet.sourceIp =  ipPortObj.find("input[name='srcIp']").val();
                if ( !checkIP(appDefinedQuintet.sourceIp )){
                    swal("操作失败", "请输入正确的源IP", "error");
                    return;
                }
                appDefinedQuintet.sourcePort =  ipPortObj.find("input[name='srcPort']").val();
                if ( !checkPort(appDefinedQuintet.sourcePort) ) {
                    swal("操作失败", "请输入正确的源PORT", "error");
                    return;
                }
                appDefinedQuintet.destIp =  ipPortObj.find("input[name='destIp']").val();
                if ( !checkIP(appDefinedQuintet.destIp )){
                    swal("操作失败", "请输入正确的目的IP", "error");
                    return;
                }
                appDefinedQuintet.destPort =  ipPortObj.find("input[name='destPort']").val();
                if ( !checkPort(appDefinedQuintet.destPort) ) {
                    swal("操作失败", "请输入正确的目的PORT", "error");
                    return;
                }

                var kwObjLength = ipPortObj.find("div[name='inlineDiv']").find("div[class='form-group kw-group']").length;
                var kwArray = new Array();
                for (var j=0;j<kwObjLength;j++){
                    var appDefinedKey = {
                        "quintetId":"",
                        "offSetBase":"",
                        "offSet":"",
                        "kwValue":""
                    }
                    var kwObj = ipPortObj.find("div[name='inlineDiv']").find("div[class='form-group kw-group']").eq(j);
                    appDefinedKey.offSetBase = kwObj.find("select[name='keyWordOffSetPosition']").val();
                    if (appDefinedKey.offSetBase==undefined||appDefinedKey.offSetBase==''){
                        swal("操作失败", "请选择偏移位置", "error");
                        return;
                    }
                    appDefinedKey.offSet = kwObj.find("input[name='offSet']").val();
                    if ( !isNmber0(appDefinedKey.offSet)){
                        swal("操作失败", "请输入正确的偏移量", "error");
                        return;
                    }
                    appDefinedKey.kwValue = kwObj.find("input[name='keyWord']").val();
                    if (appDefinedKey.kwValue==undefined||appDefinedKey.kwValue==''){
                        swal("操作失败", "请输入关键词", "error");
                        return;
                    }
                    kwArray.push(appDefinedKey);
                }
                appDefinedQuintet.kw = kwArray;
                signatureArray.push(appDefinedQuintet);
            }
            customFeatureStrategy.signature = signatureArray;

            $.ajax({
                url: url,
                type: 'POST',
                dataType:'json',
                contentType:"application/json",
                data:JSON.stringify(customFeatureStrategy),
                success: function (data) {
                    if (data.result==0){
                        $("#messageNo").val('');
                        $('#definedId').val('');
                        $('#myModaladd').modal('hide');
                        swal(data.message);
                        initTableJs.refreshData();
                    } else {
                        // $("#messageNo").val('');
                        swal("操作失败", "存在策略名称相同数据", "error");
                    }

                },
                error:function () {
                    swal("操作失败", "取消操作！", "error");
                }

            })

        })
    },

    init:function(){
        customFeaturesJs.searchFormSubBut();
        customFeaturesJs.initAppTypeAndAppIdSelector();
        customFeaturesJs.initUserGroup();
        customFeaturesJs.clearWarnDiv();
        customFeaturesJs.addOrUpdateData();

        addUserGroupJs.createUserGroup();
        addUserGroupJs.exportTemplate();
    }

}
/*******************流量标记相关事件绑定*********************/



$(document).ready(function() {
    initSearchDate(3,false);
    initTableJs.initParam('/apppolicy/customfeatures/list.do','/apppolicy/customfeatures/delete.do',getColumnFunction)
    initTableJs.init2();
    customFeaturesJs.init();
    ipPortPlusOrMinusJs.init();
});


function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'messageNo',title: '策略ID',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'messageName',title: '<span title="策略名称">策略名称</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'appTypeName',title: '<span title="WEB分类">WEB分类</span>',formatter:function(value,row,index){
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        /*{field:'appType',title:'<span title="WEB分类">WEB分类</span>',
            formatter:function webTypeFormatter(value,row,index){
                var webType = "";
                if (row['appType']==0){
                    webType = "未识别应用";
                } else if (row['appType']==1){
                    webType = "Web 视频类";
                } else if (row['appType']==2){
                    webType = "其它 Web 类";
                } else if (row['appType']==3){
                    webType = "P2PStream";
                } else if (row['appType']==4){
                    webType = "P2PDownload";
                } else if (row['appType']==5){
                    webType = "VideoStream";
                } else if (row['appType']==6){
                    webType = "Download";
                } else if (row['appType']==7){
                    webType = "Email";
                } else if (row['appType']==8){
                    webType = "IM";
                } else if (row['appType']==9){
                    webType = "Game";
                } else if (row['appType']==10){
                    webType = "VoIP";
                } else if (row['appType']==11){
                    webType = "电信自营";
                } else if (row['appType']==12){
                    webType = "其它";
                }
                return "<span title='"+webType+"'>"+webType+"</span>";
            }},*/
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
        {field: 'createTime',title: '<span title="创建时间">创建时间</span>',
            formatter : function (value, row, index) {
                return "<span title='"+timestamp2Time(row['createTime'],'-')+"'>"+timestamp2Time(row['createTime'],'-')+"</span>";
            }
        },
        {field: 'createOper',title: '<span title="操作人">操作人</span>'},
        {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
    ]
    return column;
}

// 复原新增时页面状态
function newAddButtonFunction(){
    $(".has-error").remove();

    $('#myModaladd').find("h5").text("新增");

    $('#messageNo').val('');
    $('#definedId').val('');

    $('#addOrupdateForm').find('input[name="messageName"]').val('');

    loadSel('appType','/select/getAppType',true,undefined,true);
    loadSel('appId','/select/getAppByType',true,{appType:0},true,undefined);
    $("#addOrupdateForm").find('input[name="appName"]').attr('readonly',false);

    $('#addOrupdateForm').find('input[name="appName"]').val('');
    $('#userType').val(0);
    $('#qosLabelUp').val('');
    $('#qosLabelDn').val('');

    userTypeOne();
    //去掉所有子节点
    $("#outlineDiv").empty();
    //添加初始节点
    addInitialDom();
}

function addInitialDom(){
    var initialHtml = '<div name="ip-port-group" class="ip-port-group">';

        initialHtml +='     <div class="form-group" >';
        initialHtml +='         <label class="col-md-2 control-label p-n">类型</label>';
        initialHtml +='         <div class="col-md-1">';
        initialHtml +='             <select class="form-control"  id="protocolType_0" name="protocolType" >';
        initialHtml +='                 <option selected="" value="6">TCP</option>';
        initialHtml +='                 <option value="17">UDP</option>';
        initialHtml +='             </select> ';
        initialHtml +='         </div>';
        initialHtml +='         <label class="col-md-1 control-label p-n">源IP:端口  </label> ';
        initialHtml +='         <div class="col-md-2"> ';
        initialHtml +='             <input type="text" id="srcIp_0"name="srcIp" class="form-control"/> ';
        initialHtml +='         </div> ';
        initialHtml +='         <div class="col-md-1"> ';
        initialHtml +='             <input type="text" id="srcPort_0" name="srcPort" class="form-control"/> ';
        initialHtml +='         </div> ';
        initialHtml +='         <label class="col-md-1 control-label p-n">目标IP:端口  </label> ';
        initialHtml +='         <div class="col-md-2"> ';
        initialHtml +='             <input type="text" id="destIp_0"name="destIp" class="form-control"/> ';
        initialHtml +='         </div> ';
        initialHtml +='         <div class="col-md-1"> ';
        initialHtml +='             <input type="text" id="destPort_0" name="destPort" class="form-control"/> ';
        initialHtml +='         </div> ';
        initialHtml +='         <div class="col-md-1"> ';
        initialHtml +='             <span class="help-block m-b-none tips"> ';
        initialHtml +='             <a class="m-l-n m-r-sm"><i class="fa fa-plus ip-port-plus" id="ipPortAddButton" aria-hidden="true"></i></a> ';
        initialHtml +='             </span> ';
        initialHtml +='         </div> ';
        initialHtml +='     </div> ';

        initialHtml +='     <div name="inlineDiv" id="inlineDiv_0"> ';
        initialHtml +='         <div class="form-group kw-group"> ';
        initialHtml +='             <input type="hidden" class="col-md-2 "> ';
        initialHtml +='             <label class="col-md-2 control-label p-n">关键字偏移位置</label> ';
        initialHtml +='             <div class="col-md-2"> ';
        initialHtml +='                 <select class="form-control"  id="keyWordOffSetPosition" name="keyWordOffSetPosition" > ';
        initialHtml +='                     <option selected="" value="0">从载荷头</option> ';
        initialHtml +='                     <option value="1">从载荷尾</option> ';
        initialHtml +='                 </select> ';
        initialHtml +='             </div>';
        initialHtml +='             <label class="col-md-1 control-label p-n">偏移量</label>';
        initialHtml +='             <div class="col-md-2"><input type="text" id="offSet" name="offSet" class="form-control" /></div> ';
        initialHtml +='             <label class="col-md-1 control-label p-n">关键词</label>';
        initialHtml +='             <div class="col-md-2"><input type="text" id="keyWord" name="keyWord" class="form-control" /></div>';
        initialHtml +='             <div class="col-md-1">';
        initialHtml +='                 <span class="help-block m-b-none tips">';
        initialHtml +='                 <a class="m-l-n m-r-sm check-plus kw"><i class="fa fa-plus kw" aria-hidden="true"></i></a>';
        initialHtml +='                 </span>';
        initialHtml +='             </div>';
        initialHtml +='         </div>';
        initialHtml +='     </div> ';

        initialHtml +='</div> ';

    $("#outlineDiv").html(initialHtml);
}

// 复原修改时页面状态
function modifyButtonFunction(row) {
    $(".has-error").remove();

    $('#myModaladd').find("h5").text("修改");
    $('#myModaladd').modal('show');
    $('#messageNo').val(row.messageNo);
    $('#definedId').val(row.definedId);
    $('#addOrupdateForm').find('input[name="messageName"]').val(row.messageName);
    $('#appType').val(row.appType);
    $('#addOrupdateForm').find('input[name="appName"]').val(row.appName);

    loadSel('appId','/select/getAppByType',true,{appType:$("#appType").val()},true,undefined);
    $('#appId').val(row.appId);
    initAppNameInput(row);

    var userType = row.bindUser[0].userType;
    $('#userType').val(userType);
    // 还原用户组信息
    clearWarn($('#addOrupdateForm'));
    $('#addOrupdateForm').find('input[name="userName"]').val('');
    if(userType == 0){
        userTypeOne();
    }else if(userType == 1 || userType == 2){
        $('#userNameDiv').show();
        $('#puserGroup').hide();
        $('#userNameDiv .newInput').remove();
        $('#userType').val(userType);

        for (var i =0;i<row.bindUser.length;i++){
            if(i==0){
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(row.bindUser[i].userName);
            }else {
                addInputEvent();
                $('#userNameDiv').find('input[name="userName"]').eq(i).val(row.bindUser[i].userName);
            }
        }
    }else if(userType == 3){

        $('#userNameDiv').hide();
        $('#puserGroup').show();
        $('#userNameDiv .newInput').remove();
        $('#userType').val(userType);

        var userGroups = new Array();
        for (var i =0;i<row.bindUser.length;i++){
            userGroups.push(row.bindUser[i].userGroupId);
        }
        loadSelectPicker('puserGroupid','/select/getUserGroup',false,userGroups);
    }


    //去掉所有子节点
    $("#outlineDiv").empty();
    //添加初始节点
    // addInitialDom();
    // // 还原多元组输入框
    // $("#protocolType_0").val(row.signature[0].protocol);
    // $("#srcIp_0").val(row.signature[0].sourceIp);
    // $("#srcPort_0").val(row.signature[0].sourcePort);
    // $("#destIp_0").val(row.signature[0].destIp);
    // $("#destPort_0").val(row.signature[0].destPort);
    //
    // $("#keyWordOffSetPosition").val(row.signature[0].kw[0].offSetBase);
    // $("#offSet").val(row.signature[0].kw[0].offSet);
    // $("#keyWord").val(row.signature[0].kw[0].kwValue);
    //还原第一组多元组输入框
    // revertFirstAddedDom(row);
    //还原其它多元组输入框
    revertOtherAddedDom(row);
}

// 还原增加的dom节点
function revertFirstAddedDom(row){
    for (var i=1;i<row.signature[0].kw.length;i++){
        var inputPlus =  '<div class="form-group kw-group" >';
        inputPlus +=     '    <input type="hidden" class="col-md-2 "><label class="col-md-2 control-label p-n">关键字偏移位置</label>';
        inputPlus +=     '    <div class="col-md-2"><select class="form-control" name="keyWordOffSetPosition" >';
        if (row.signature[0].kw[i].offSetBase==0){
            inputPlus +=     '    <option selected="true" value="0">从载荷头</option><option value="1">从载荷尾</option>';
        } else {
            inputPlus +=     '    <option value="0">从载荷头</option><option selected="true" value="1">从载荷尾</option>';
        }
        inputPlus +=     '    </select></div>';
        inputPlus +=     '    <label class="col-md-1 control-label p-n">偏移量</label>';
        inputPlus +=     '    <div class="col-md-2"><input type="text"  name="offSet" value='+row.signature[0].kw[i].offSet+' class="form-control" /></div>';
        inputPlus +=     '    <label class="col-md-1 control-label p-n">关键词</label>';
        inputPlus +=     '    <div class="col-md-2"> <input type="text" name="keyWord" value='+row.signature[0].kw[i].kwValue+' class="form-control" /></div>';
        inputPlus +=     '    <div class="col-md-1">';
        inputPlus +=     '         <span class="help-block m-b-none tips">';
        inputPlus +=     '               <a class="m-l-n m-r-sm check-minus kw"><i class="fa fa-minus" aria-hidden="true"></i></a>';
        inputPlus +=     '         </span>';
        inputPlus +=     '    </div>';
        inputPlus +=     '</div>';
    }
    var oldHtml = $("#inlineDiv_0").html();
    oldHtml += inputPlus;
    $("#inlineDiv_0").empty();
    $("#inlineDiv_0").html(oldHtml);
    // $("#inlineDiv_0").find(".form-group.kw-group:last").after(inputPlus);
}

function revertOtherAddedDom(row){
    for (var i=0;i<row.signature.length;i++){
        var inputPlus =  '';
        inputPlus += '<div name="ip-port-group" class="ip-port-group">';

        inputPlus += '  <div class="form-group" >';
        inputPlus += '      <label class="col-md-2 control-label p-n">类型</label>';
        inputPlus += '      <div class="col-md-1"><select class="form-control"  name="protocolType" >';
        if (row.signature[i].protocol==6){
            inputPlus += '      <option selected="true" value="6">TCP</option><option value="17">UDP</option>';
        } else if (row.signature[i].protocol==17){
            inputPlus += '      <option value="6">TCP</option><option selected="true" value="17">UDP</option>';
        }

        inputPlus += '      </select></div>';
        inputPlus += '      <label class="col-md-1 control-label p-n">源IP:端口  </label>';
        inputPlus += '      <div class="col-md-2"><input type="text" name="srcIp" value='+row.signature[i].sourceIp+' class="form-control"/></div>';
        inputPlus += '      <div class="col-md-1"><input type="text" name="srcPort" value='+row.signature[i].sourcePort+' class="form-control"/></div>';
        inputPlus += '      <label class="col-md-1 control-label p-n">目标IP:端口  </label>';
        inputPlus += '      <div class="col-md-2"><input type="text" name="destIp" value='+row.signature[i].destIp+' class="form-control"/></div>';
        inputPlus += '      <div class="col-md-1"><input type="text" name="destPort" value='+row.signature[i].destPort+' class="form-control"/></div>';
        if ( i==0 ){
            inputPlus += '      <div class="col-md-1 "><span class="help-block m-b-none" >';
            inputPlus += '          <a class="m-l-n m-r-sm"><i class="fa fa-plus ip-port-plus" id="ipPortAddButton" aria-hidden="true"></i></a>';
            inputPlus += '      </span></div>';
        } else {
            inputPlus += '      <div class="col-md-1 "><span class="help-block m-b-none tips" >';
            inputPlus += '          <a class="sourse-a m-l-n m-r-sm check-minus ip-port-kw"><i class="fa fa-minus"></i></a>';
            inputPlus += '      </span></div>';
        }

        inputPlus += '  </div>';

        for (var j=0;j<row.signature[i].kw.length;j++){
            inputPlus += '  <div name="inlineDiv" >';
            inputPlus += '      <div class="form-group kw-group">';
            inputPlus += '          <input type="hidden" class="col-md-1 "/>';
            inputPlus += '          <label class="col-md-2 control-label p-n">关键字偏移位置</label>';
            inputPlus += '          <div class="col-md-2">';
            if (row.signature[i].kw[j].offSetBase==0){
                inputPlus += '          <select class="form-control" name="keyWordOffSetPosition" >';
                inputPlus += '               <option selected="true" value="0">从载荷头</option><option value="1">从载荷尾</option>';
                inputPlus += '          </select>';
            } else if (row.signature[i].kw[j].offSetBase==1) {
                inputPlus += '          <select class="form-control" name="keyWordOffSetPosition" >';
                inputPlus += '               <option value="0">从载荷头</option><option selected="true" value="1">从载荷尾</option>';
                inputPlus += '          </select>';
            }
            inputPlus += '          </div>';
            inputPlus += '          <label class="col-md-1 control-label p-n">偏移量</label>';
            inputPlus += '          <div class="col-md-2"><input type="text" name="offSet" value='+row.signature[i].kw[j].offSet+' class="form-control" /></div>';
            inputPlus += '          <label class="col-md-1 control-label p-n">关键词</label>';
            inputPlus += '          <div class="col-md-2"><input type="text" name="keyWord" value='+row.signature[i].kw[j].kwValue+' class="form-control" /></div>';
            if (j==0){
                inputPlus += '      <div class="col-md-1 "><span class="help-block m-b-none" >';
                inputPlus += '          <a class="sourse-a m-l-n m-r-sm check-plus kw"><i class="fa fa-plus kw"></i></a>';
                inputPlus += '      </span></div>';
            } else {
                inputPlus +=     '    <div class="col-md-1">';
                inputPlus +=     '         <span class="help-block m-b-none tips">';
                inputPlus +=     '               <a class="m-l-n m-r-sm check-minus kw"><i class="fa fa-minus" aria-hidden="true"></i></a>';
                inputPlus +=     '         </span>';
                inputPlus +=     '    </div>';
            }

            inputPlus += '      </div>';
            inputPlus += '  </div>';

        }
        inputPlus += '</div>';

        $("#outlineDiv").append(inputPlus);

    }
}

// 删除
function deleteFunction(url,id){

    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var definedIds = new Array();
    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        definedIds.push(item.definedId);
    }
    if (definedIds.length==0){
        definedIds.push(id);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{"definedIds":definedIds},
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

// 获取查询条件
function queryParams(params){
    var data = $('#searchForm').formToJSON();
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    if(data.messageName==''){
        data.messageName = undefined;
    }
    if (data.searchStartTime==''){
        data.searchStartTime = undefined;
    }
    if (data.searchEndTime==''){
        data.searchEndTime = undefined;
    }
    return data;
}

//样式格式化
function operatingFormatter(value, row, index){
	var redo = $("#redoFlag").val();
	var deleteFlag = $("#deleteFlag").val();
	var modify = $("#modifyFlag").val();
	var format="";
	if(redo==1){
        format +="<a href='#' title='重发' class='m-r'><i class='fa fal fa-share resend'></i></a>";
	}
	if(modify==1){
		 format+="<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
	}
	if(deleteFlag==1){
		format+="<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
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
                deleteFunction('/apppolicy/customfeatures/delete.do',row.definedId);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}

function getDetail(showTable,index){
    var webFlow = $("#table").bootstrapTable('getData');
	var redo = $("#redoFlag").val();
    if(redo==1){
    	PolicyDetail.showDetail(webFlow[index].messageNo,10,2,1,showTable);
    }else{
    	PolicyDetail.showDetail(webFlow[index].messageNo,10,2,0,showTable);    	
    }
}


// 重发指定应用用户策略 和用户绑定策略
function resendPolicy(row){
    $.ajax({
        url: "/apppolicy/customfeatures/resend.do",
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



function loadUserGroup() {
    loadSelectPicker('puserGroupid','/select/getUserGroup',false);
}

function initAppNameInput (row) {
    var appId = $("#appId").val();
    if (appId==0){
        $("#addOrupdateForm").find('input[name="appName"]').val(row.appName);
        $("#addOrupdateForm").find('input[name="appName"]').attr('readonly',false);
    } else {
        $("#addOrupdateForm").find('input[name="appName"]').val('');
        $("#addOrupdateForm").find('input[name="appName"]').attr('readonly',true);
    }
}
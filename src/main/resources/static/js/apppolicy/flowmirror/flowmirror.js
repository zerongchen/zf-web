var srcIpPrefixCount = 1;
var srcPortCount = 1;
var destIpPrefixCount = 1;
var destPortCount = 1;
var offSetCount = 1;

var maxLength = 999999999;

var mSrcIpSegmentArray = new Array();

var mDstIpSegmentArray = new Array();

var mSrcPortArray = new Array();

var mDestPortArray = new Array();

var mDataMatchArray = new Array();



var flowMirrorStrategy = {
    "policyId":"",
    "messageNo":"",
    "messageName":"",
    "appType":"",
    "appId":"",
    "appName":"",
    "startTime":"",
    "endTime":"",
    "groupNo":"",
    "direction":"",
    "flowAdd":"",
    "cutLength":"",
    "srcIpSegment":mSrcIpSegmentArray,
    "dstIpSegment":mDstIpSegmentArray,
    "srcPort":mSrcPortArray,
    "destPort":mDestPortArray,
    "dataMatch":mDataMatchArray,
    "userType":"",
    "userNames":new Array(),
    "userGroupIds":new Array()    //用户组        多个用户组用逗号隔开

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
    //初始化源ip和前缀单击事件(增加)
    initSrcIpPrefixInputPlus : function(){
        $("#srcIpPrefixAddButton").click(function () {
            if (srcIpPrefixCount>=maxLength){
                swal("最多只能输入3组");
            } else {
                var inputPlus =  '';
                inputPlus += '  <div class="form-group" >';
                inputPlus += '     <label class="col-md-2 control-label p-n">源IP  </label>';
                inputPlus += '     <div class="col-md-2 srcIp"><input type="text" name="srcIp" class="form-control"/></div>';
                inputPlus += '     <label class="col-md-2 control-label p-n">IP前缀  </label>';
                inputPlus += '     <div class="col-md-2 srcIpPrefix"><input type="text" name="srcIpPrefix" class="form-control"/></div>';
                inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
                inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus src-ip-prefix"><i class="fa fa-minus"></i></a>';
                inputPlus += '     </span></div>';
                inputPlus += '  </div>';

                $(this).parent().parent().parent().parent().after(inputPlus);
                srcIpPrefixCount++;
            }
        });
    },

    //初始化源ip和前缀单击事件(减少)
    initSrcIpPrefixInputMinus :function(){
        $(document).on('click',".check-minus.src-ip-prefix",function () {
            $(this).parent().parent().parent().remove();
            srcIpPrefixCount--;
        });
    },


    //初始化目标ip和前缀单击事件(增加)
    initDestIpPrefixInputPlus : function(){
        $("#destIpPrefixAddButton").click(function () {
            if (destIpPrefixCount>=maxLength){
                swal("最多只能输入3组");
            } else {
                var inputPlus =  '';

                inputPlus += '  <div class="form-group" >';
                inputPlus += '     <label class="col-md-2 control-label p-n">目的IP  </label>';
                inputPlus += '     <div class="col-md-2 destIp"><input type="text" name="destIp" class="form-control"/></div>';
                inputPlus += '     <label class="col-md-2 control-label p-n">IP前缀  </label>';
                inputPlus += '     <div class="col-md-2 destIpPrefix"><input type="text"  name="destIpPrefix" class="form-control"/></div>';
                inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
                inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus dest-ip-prefix"><i class="fa fa-minus"></i></a>';
                inputPlus += '     </span></div>';
                inputPlus += '  </div>';

                $(this).parent().parent().parent().parent().after(inputPlus);
                destIpPrefixCount++;
            }
        });
    },

    //初始化目标ip和前缀单击事件(减少)
    initDestIpPrefixInputMinus :function(){
        $(document).on('click',".check-minus.dest-ip-prefix",function () {
            $(this).parent().parent().parent().remove();
            destIpPrefixCount--;
        });
    },

    //初始化源端口单击事件(增加)
    initSrcPortInputPlus : function(){
        $("#srcPortAddButton").click(function () {
            if (srcPortCount>=maxLength){
                swal("最多只能输入3组");
            } else {
                var inputPlus =  '';

                inputPlus += '  <div class="form-group" >';
                inputPlus += '     <label class="col-md-2 control-label p-n">源端口起始值  </label>';
                inputPlus += '     <div class="col-md-2 srcPortBegin"><input type="text" name="srcPortBegin" class="form-control"/></div>';
                inputPlus += '     <label class="col-md-2 control-label p-n">源端口结束值  </label>';
                inputPlus += '     <div class="col-md-2 srcPortEnd"><input type="text"  name="srcPortEnd" class="form-control"/></div>';
                inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
                inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus src-port"><i class="fa fa-minus"></i></a>';
                inputPlus += '     </span></div>';
                inputPlus += '  </div>';

                $(this).parent().parent().parent().parent().after(inputPlus);
                srcPortCount++;
            }
        });
    },

    //初始化源端口单击事件(减少)
    initSrcPortInputMinus :function(){
        $(document).on('click',".check-minus.src-port",function () {
            $(this).parent().parent().parent().remove();
            srcPortCount--;
        });
    },

    //初始化源端口单击事件(增加)
    initDestPortInputPlus : function(){
        $("#destPortAddButton").click(function () {
            if (destPortCount>=maxLength){
                swal("最多只能输入3组");
            } else {
                var inputPlus =  '';

                inputPlus += '  <div class="form-group" >';
                inputPlus += '     <label class="col-md-2 control-label p-n">目的端口起始值  </label>';
                inputPlus += '     <div class="col-md-2 destPortBegin"><input type="text" name="destPortBegin" class="form-control"/></div>';
                inputPlus += '     <label class="col-md-2 control-label p-n">目的端口结束值  </label>';
                inputPlus += '     <div class="col-md-2 destPortEnd"><input type="text"  name="destPortEnd" class="form-control"/></div>';
                inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
                inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus dest-port"><i class="fa fa-minus"></i></a>';
                inputPlus += '     </span></div>';
                inputPlus += '  </div>';

                $(this).parent().parent().parent().parent().after(inputPlus);
                destPortCount++;
            }
        });
    },

    //初始化源端口单击事件(减少)
    initDestPortInputMinus :function(){
        $(document).on('click',".check-minus.dest-port",function () {
            $(this).parent().parent().parent().remove();
            destPortCount--;
        });
    },

    init:function(){
        ipPortPlusOrMinusJs.initSrcIpPrefixInputPlus();
        ipPortPlusOrMinusJs.initSrcIpPrefixInputMinus();
        ipPortPlusOrMinusJs.initDestIpPrefixInputPlus();
        ipPortPlusOrMinusJs.initDestIpPrefixInputMinus();
        ipPortPlusOrMinusJs.initSrcPortInputPlus();
        ipPortPlusOrMinusJs.initSrcPortInputMinus();
        ipPortPlusOrMinusJs.initDestPortInputPlus();
        ipPortPlusOrMinusJs.initDestPortInputMinus();
    }
}
/*********************源IP目标IP增删节点js end*********************************/

/*********************特征值偏移量增删节点js begin******************************/
offSetPlusOrMinusJs = {
    initInputPlus : function () {
        $("#offSetAddButton").click(function () {
            if (offSetCount>=maxLength){
                swal("最多只能输入3组");
            } else {
                var inputPlus =  '';

                inputPlus += '  <div class="form-group" >';
                inputPlus += '     <label class="col-md-2 control-label p-n">特征字偏移量</label>';
                inputPlus += '     <div class="col-md-2 offSet"><input type="text" name="offSet" class="form-control" /></div>';
                inputPlus += '     <label class="col-md-2 control-label p-n">匹配字符串</label>';
                inputPlus += '     <div class="col-md-2 matchStr"><input type="text" class="form-control" name="matchStr" /></div>';
                inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
                inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus offset-match-str"><i class="fa fa-minus"></i></a>';
                inputPlus += '     </span></div>';
                inputPlus += '  </div>';

                $(this).parent().parent().parent().parent().after(inputPlus);
                offSetCount++;
            }
        });
    },
    initInputMinus :function () {
        $(document).on('click',".check-minus.offset-match-str",function () {
            $(this).parent().parent().parent().remove();
            offSetCount--;
        });
    },
    init : function () {
        offSetPlusOrMinusJs.initInputPlus();
        offSetPlusOrMinusJs.initInputMinus();
    }
}

/*********************特征值偏移量增删节点js begin******************************/

/*******************流量标记相关事件绑定*********************/
flowMirrorJs= {

    // 查询按钮单击事件绑定
    searchFormSubBut:function () {

        $('#newAddGroup').click(function () {
            document.getElementById("createUserGroupForm").reset();
            $('#add-usergroup-snippet').modal('show');
            $('#operate').val(0);
            $('#operateDiv').hide();
        });

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

            var url = "";
            var formData = $('#addOrupdateForm').formToJSON();

            var names = new Array();
            var puserGroup = new Array();

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
                    warnSelect("puserGroup");
                    // swal("请选择关联的用户组");
                    return false;
                }

                puserGroup = formData.puserGroup.split(",");
                formData.userName = undefined;
                formData.puserGroup = puserGroup;
            }else {
                formData.puserGroup = undefined;
                formData.userName = undefined;
            }

            flowMirrorStrategy.policyId = $('#policyId').val();
            flowMirrorStrategy.messageNo = $("#messageNo").val();
            if (flowMirrorStrategy.messageNo ==undefined || flowMirrorStrategy.messageNo == ''){
                url = "/apppolicy/flowmirror/save.do";
            } else {
                url = "/apppolicy/flowmirror/update.do";
            }
            flowMirrorStrategy.messageName = $('#messageName').val();
            flowMirrorStrategy.appType = $('#appType').val();
            flowMirrorStrategy.appId = $('#appId').val();
            flowMirrorStrategy.appName = $('#appName').val();
            flowMirrorStrategy.startTime = $('#start').val();
            flowMirrorStrategy.endTime = $('#end').val();
            flowMirrorStrategy.groupNo = $('#mGroupNo').val();
            flowMirrorStrategy.direction = $('#mDirection').val();
            flowMirrorStrategy.flowAdd = $('#mFlowAdd').val();
            flowMirrorStrategy.cutLength = $('#mCutLength').val();

            if (flowMirrorStrategy.messageName==undefined||flowMirrorStrategy.messageName==''){
                // swal("请输入策略名称");
                warn("messageNameDiv","请输入策略名称");
                return false;
            }

            if (flowMirrorStrategy.appType==undefined||flowMirrorStrategy.appType==''){
                // swal("请选择应用类型");
                warnSelect("appTypeDiv");
                return false;
            }

            if (flowMirrorStrategy.appId==undefined||flowMirrorStrategy.appId==''){
                // swal("操作失败", "请选择子应用类型", "error");
                warnSelect("appIdDiv");
                return false;
            }

            if (flowMirrorStrategy.appId==0){
                if (flowMirrorStrategy.appName==undefined||flowMirrorStrategy.appName==''){
                    // swal("请输入自定义子应用名");
                    warn("appNameDiv","请输入自定义子应用名");
                    return false;
                }
            }


            var srcIpAndPrefixDom = $("#srcIpAndPrefixDiv .form-group");
            mSrcIpSegmentArray = new Array();
            for (var i=0;i<srcIpAndPrefixDom.length;i++){
                var mSrcIpSegmentObj = {"ipAddr":"","ipPrefixLen":""};
                mSrcIpSegmentObj.ipAddr = $("#srcIpAndPrefixDiv .form-group").eq(i).find('input[name="srcIp"]').val();
                mSrcIpSegmentObj.ipPrefixLen = $("#srcIpAndPrefixDiv .form-group").eq(i).find('input[name="srcIpPrefix"]').val();
                // debugger;
                if ( !checkIP(mSrcIpSegmentObj.ipAddr) ){
                    // swal("请输入正确的源ip地址");
                    $("#srcIpAndPrefixDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入正确的源ip地址'+'</span></div>');
                    $("#srcIpAndPrefixDiv .form-group").eq(i).children('.col-md-2.srcIp').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    // $("#srcIpAndPrefixDiv .form-group").eq(i).children('.col-md-3').find('input').attr('onfocus', 'clearWarn($(\'#'+section+'\'))');
                    return ;
                }
                if ( !isNmber0(mSrcIpSegmentObj.ipPrefixLen) ){
                    // swal("请输入正确的源ip前缀");
                    $("#srcIpAndPrefixDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入正确的源ip前缀'+'</span></div>');
                    $("#srcIpAndPrefixDiv .form-group").eq(i).children('.col-md-2.srcIpPrefix').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return ;
                }
                mSrcIpSegmentArray.push(mSrcIpSegmentObj);
            }

            var srcPortDom = $("#srcPortDiv .form-group");
            mSrcPortArray = new Array();
            for (var i=0;i<srcPortDom.length;i++){
                var mSrcPortObj = {"portStart":"","portEnd":""};
                mSrcPortObj.portStart = $("#srcPortDiv .form-group").eq(i).find('input[name="srcPortBegin"]').val();
                mSrcPortObj.portEnd = $("#srcPortDiv .form-group").eq(i).find('input[name="srcPortEnd"]').val();
                if ( !checkPort(mSrcPortObj.portStart) ){
                    // swal("请输入正确的源端口起始值");
                    $("#srcPortDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入源端口起始值'+'</span></div>');
                    $("#srcPortDiv .form-group").eq(i).children('.col-md-2.srcPortBegin').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return ;
                }
                if ( !(checkPort(mSrcPortObj.portEnd) && (parseInt(mSrcPortObj.portEnd)>parseInt(mSrcPortObj.portStart))) ){
                    // swal("请输入正确的源端口结束值(必须大于起始值)");
                    $("#srcPortDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span c' +
                        'lass="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入源端口结束值(大于起始值)'+'</span></div>');
                    $("#srcPortDiv .form-group").eq(i).children('.col-md-2.srcPortEnd').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return ;
                }
                mSrcPortArray.push(mSrcPortObj);
            }

            var destIpAndPrefixDom = $("#destIpAndPrefixDiv .form-group");

            mDstIpSegmentArray = new Array();
            for (var i=0;i<destIpAndPrefixDom.length;i++){
                var mDestIpSegmentObj = {"ipAddr":"","ipPrefixLen":""};
                mDestIpSegmentObj.ipAddr = $("#destIpAndPrefixDiv .form-group").eq(i).find('input[name="destIp"]').val();
                mDestIpSegmentObj.ipPrefixLen = $("#destIpAndPrefixDiv .form-group").eq(i).find('input[name="destIpPrefix"]').val();
                if ( !checkIP(mDestIpSegmentObj.ipAddr) ){
                    // swal("请输入正确的目的ip地址");
                    $("#destIpAndPrefixDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入正确的目的ip地址'+'</span></div>');
                    $("#destIpAndPrefixDiv .form-group").eq(i).children('.col-md-2.destIp').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return ;
                }
                if ( !isNmber0(mDestIpSegmentObj.ipPrefixLen) ){
                    // swal("请输入正确的目的ip前缀");
                    $("#destIpAndPrefixDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入正确的目的ip前缀'+'</span></div>');
                    $("#destIpAndPrefixDiv .form-group").eq(i).children('.col-md-2.destIpPrefix').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return ;
                }
                mDstIpSegmentArray.push(mDestIpSegmentObj);
            }

            var destPortDom = $("#destPortDiv .form-group");
            mDestPortArray = new Array();
            for (var i=0;i<destPortDom.length;i++){
                var mDestPortObj = {"portStart":"","portEnd":""};
                mDestPortObj.portStart = $("#destPortDiv .form-group").eq(i).find('input[name="destPortBegin"]').val();
                mDestPortObj.portEnd = $("#destPortDiv .form-group").eq(i).find('input[name="destPortEnd"]').val();
                if ( !checkPort(mDestPortObj.portStart) ){
                    // swal("请输入正确的目的端口起始值");
                    $("#destPortDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入目的端口起始值'+'</span></div>');
                    $("#destPortDiv .form-group").eq(i).children('.col-md-2.destPortBegin').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return ;
                }
                if ( !(checkPort(mDestPortObj.portEnd) && (parseInt(mDestPortObj.portEnd)>parseInt(mDestPortObj.portStart))) ){
                    // swal("请输入正确的目的端口结束值(必须大于起始值)");
                    $("#destPortDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入目的端口结束值(大于起始值)'+'</span></div>');
                    $("#destPortDiv .form-group").eq(i).children('.col-md-2.destPortEnd').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return ;
                }
                mDestPortArray.push(mDestPortObj);
            }

            var dataMatchDom = $("#offSetDiv .form-group");
            mDataMatchArray = new Array();
            for (var i=0;i<dataMatchDom.length;i++){
                var mDataMatchObj = {"dataMatchOffset":"","dataMatchContent":""};
                mDataMatchObj.dataMatchOffset = $("#offSetDiv .form-group").eq(i).find('input[name="offSet"]').val();
                mDataMatchObj.dataMatchContent = $("#offSetDiv .form-group").eq(i).find('input[name="matchStr"]').val();
                if ( !isNmber0(mDataMatchObj.dataMatchOffset) ){
                    // swal("请输入正确的偏移量");
                    $("#offSetDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入正确的偏移量'+'</span></div>');
                    $("#offSetDiv .form-group").eq(i).children('.col-md-1').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return ;
                }
                if ( mDataMatchObj.dataMatchContent==undefined||mDataMatchObj.dataMatchContent=='' ){
                    // swal("请输入正确的匹配字符串");
                    $("#offSetDiv .form-group").eq(i).children('.col-md-1').last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'请输入正确的匹配字符串'+'</span></div>');
                    $("#offSetDiv .form-group").eq(i).children('.col-md-1').find('input').attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return ;
                }
                mDataMatchArray.push(mDataMatchObj);
            }

            if (flowMirrorStrategy.groupNo==undefined||flowMirrorStrategy.groupNo==''){
                // swal("请选择镜像端口组");
                warnSelect("mpGroup");
                return false;
            }

            if (flowMirrorStrategy.direction==undefined||flowMirrorStrategy.direction==''){
                // swal("请选择镜像方向");
                warnSelect("mirrorDirection");
                return false;
            }

            if (flowMirrorStrategy.flowAdd==undefined||flowMirrorStrategy.flowAdd==''){
                // swal( "请选择是否需要转发整条流");
                warnSelect("mirrorFlowAdd");
                return false;
            }

            if (!isNmber0(flowMirrorStrategy.cutLength)){
                // swal("请输入合法的镜像报文截长");
                warn("mirrorCutLength","请输入合法的镜像报文截长");
                return false;
            }

            if ( flowMirrorStrategy.startTime==undefined||flowMirrorStrategy.startTime=='' ){
                // swal("请输入起始时间");
                warn("startTime","请选择起始时间");
                return ;
            }
            changeLongStartTime();



            if ( flowMirrorStrategy.endTime==undefined||flowMirrorStrategy.endTime=='' ){
                // swal("请输入结束时间");
                warn("endTime","请选择结束时间");
                return ;
            }
            changeLongEndTime();

            flowMirrorStrategy.userType = $('#userType').val();
            flowMirrorStrategy.userNames = names;
            flowMirrorStrategy.userGroupIds = puserGroup;
            flowMirrorStrategy.srcIpSegment = mSrcIpSegmentArray;
            flowMirrorStrategy.dstIpSegment = mDstIpSegmentArray;
            flowMirrorStrategy.srcPort = mSrcPortArray;
            flowMirrorStrategy.destPort = mDestPortArray;
            flowMirrorStrategy.dataMatch = mDataMatchArray;


            if ( true ){
                $.ajax({
                    url: url,
                    type: 'POST',
                    dataType:'json',
                    data:JSON.stringify(flowMirrorStrategy),
                    contentType:"application/json",
                    success: function (data) {
                        if (data.result==0){
                            $("#messageNo").val('');
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
            }

        })
    },

    init:function(){
        flowMirrorJs.searchFormSubBut();
        flowMirrorJs.initAppTypeAndAppIdSelector();
        flowMirrorJs.initUserGroup();
        flowMirrorJs.clearWarnDiv();
        flowMirrorJs.addOrUpdateData();

        addUserGroupJs.createUserGroup();
        addUserGroupJs.exportTemplate();
    }

}
/*******************流量标记相关事件绑定*********************/



$(document).ready(function() {
    initDate(3,false);
    initSearchDate(3,false);
    initTableJs.initParam('/apppolicy/flowmirror/list.do','/apppolicy/flowmirror/delete.do',getColumnFunction)
    initTableJs.init2();
    flowMirrorJs.init();
    ipPortPlusOrMinusJs.init();
    offSetPlusOrMinusJs.init();

});


function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'messageNo',title: '策略ID',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";}},
        {field: 'messageName',title: '<span title="策略名称">策略名称</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";}},
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
        {field: 'direction',title: '<span title="镜像方向">镜像方向</span>',
            formatter:function(value,row,index){
            	var dir = "";
                if ( value == 1 ) {
                	dir = "镜像上行方向流量(from用户)";
                } else if ( value == 2 ) {
                	dir = "镜像下行方向流量(to用户)";
                } else if ( value == 3 ) {
                	dir = "镜像双向流量";
                }
                return "<span title='"+dir+"'>"+dir+"</span>";;
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
        {field: 'startTime',title: '<span title="镜像开始时间">镜像开始时间</span>',
            formatter : function (value, row, index) {
            	var time1 = timestamp2Date(row['startTime']+"000",'-');
                return "<span title='"+time1+"'>"+time1+"</span>";
            }
        },
        {field: 'endTime',title: '<span title="镜像结束时间">镜像结束时间</span>',
            formatter : function (value, row, index) {
            	var time2 = timestamp2Date(row['endTime']+"000",'-');
            	return "<span title='"+time2+"'>"+time2+"</span>";
            }
        },
        {field: 'createTime',title: '<span title="创建时间">创建时间</span>',
            formatter : function (value, row, index) {
            	var time3 = timestamp2Time(row['createTime'],'-');
                return "<span title='"+time3+"'>"+time3+"</span>";
            }
        },
        {field: 'createOper',title: '<span title="操作账号">操作账号</span>',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";}},
        {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
    ]
    return column;
}

// 复原新增时页面状态
function newAddButtonFunction(){
    $(".has-error").remove();
    $('#myModaladd').find("h5").text("新增");

    $('#policyId').val('');
    $('#messageNo').val('');
    $('#messageName').val('');
    $('#start').val('');
    $('#end').val('');
    $('#mGroupNo').val('');
    $('#mDirection').val('');
    $('#mFlowAdd').val('');
    $('#mCutLength').val('');

    // 还原可多输入的初始输入框
    $('#srcIp_0').val('');
    $('#srcIpPrefix_0').val('');
    $('#srcPortBegin_0').val('');
    $('#srcPortEnd_0').val('');
    $('#destIp_0').val('');
    $('#destIpPrefix_0').val('');
    $('#destPortBegin_0').val('');
    $('#destPortEnd_0').val('');
    $('#offSet').val('');
    $('#matchStr').val('');


    loadSel('appType','/select/getAppType',true,undefined,true);
    loadSel('appId','/select/getAppByType',true,{appType:0},true,undefined);
    $("#appName").attr('readonly',false);
    $('#appName').val('');

    $('#userType').val(0);
    userTypeOne();

    flowMirrorStrategy.srcIpSegment = mSrcIpSegmentArray = new Array();
    flowMirrorStrategy.dstIpSegment = mDstIpSegmentArray = new Array();
    flowMirrorStrategy.srcPort = mSrcPortArray = new Array();
    flowMirrorStrategy.destPort = mDestPortArray = new Array();
    flowMirrorStrategy.dataMatch = mDataMatchArray = new Array();
    revertInputPlusDiv();
}

// 新增时还原可增加的div
function revertInputPlusDiv(){
    // 还原新增条原始值
    srcIpPrefixCount = 1;
    srcPortCount = 1;
    destIpPrefixCount = 1;
    destPortCount = 1;
    offSetCount = 1;
    $(".check-minus").parent().parent().parent().remove();
}

// 复原修改时页面状态
function modifyButtonFunction(row) {
    $(".has-error").remove();
    $('#myModaladd').find("h5").text("修改");
    $('#myModaladd').modal('show');
    $('#policyId').val(row.policyId);
    $('#messageNo').val(row.messageNo);
    $('#messageName').val(row.messageName);
    $('#appType').val(row.appType);
    $('#appName').val(row.appName);

    loadSel('appId','/select/getAppByType',true,{appType:$("#appType").val()},true,undefined);
    $('#appId').val(row.appId);
    initAppNameInput(row);

    // 设置策略有效期
    $('#start').val(timestamp2Date(row.startTime+"000","-"));
    if (row.endTime==0){
        $("#end").val('');
    } else {
        $("#end").val(timestamp2Date(row.endTime+"000","-"));
    }
    $('#mGroupNo').val(row.groupNo);
    $('#mDirection').val(row.direction);
    $('#mFlowAdd').val(row.flowAdd);
    $('#mCutLength').val(row.cutLength);

    revertInputPlusDiv();
    mSrcIpSegmentArray = row.srcIpSegment;
    mDstIpSegmentArray = row.dstIpSegment;
    mSrcPortArray = row.srcPort;
    mDestPortArray = row.destPort;
    mDataMatchArray = row.dataMatch;
    $('#srcIp_0').val(mSrcIpSegmentArray[0].ipAddr);
    $('#srcIpPrefix_0').val(mSrcIpSegmentArray[0].ipPrefixLen);
    $('#srcPortBegin_0').val(mSrcPortArray[0].portStart);
    $('#srcPortEnd_0').val(mSrcPortArray[0].portEnd);
    $('#destIp_0').val(mDstIpSegmentArray[0].ipAddr);
    $('#destIpPrefix_0').val( mDstIpSegmentArray[0].ipPrefixLen);
    $('#destPortBegin_0').val(mDestPortArray[0].portStart);
    $('#destPortEnd_0').val(mDestPortArray[0].portEnd);
    $('#offSet').val(mDataMatchArray[0].dataMatchOffset);
    $('#matchStr').val(mDataMatchArray[0].dataMatchContent);
    revertAddedInput();
    mSrcIpSegmentArray = new Array();
    mDstIpSegmentArray = new Array();
    mSrcPortArray = new Array();
    mDestPortArray = new Array();
    mDataMatchArray = new Array();
    flowMirrorStrategy.userNames = new Array();
    flowMirrorStrategy.userGroupIds = new Array();

    // 还原用户组信息
    var userType = row.bindUser[0].userType;
    $('#userType').val(userType);
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


}

function revertAddedInput(){

    if (mSrcIpSegmentArray.length>1){
        for (var i=1;i<mSrcIpSegmentArray.length;i++){
            var inputPlus =  '';
            inputPlus += '  <div class="form-group" >';
            inputPlus += '     <label class="col-md-2 control-label p-n">源IP  </label>';
            inputPlus += '     <div class="col-md-2 srcIp"><input type="text" name="srcIp" value='+mSrcIpSegmentArray[i].ipAddr+' class="form-control"/></div>';
            inputPlus += '     <label class="col-md-2 control-label p-n">IP前缀  </label>';
            inputPlus += '     <div class="col-md-2 srcIpPrefix"><input type="text" name="srcIpPrefix" value='+mSrcIpSegmentArray[i].ipPrefixLen+' class="form-control"/></div>';
            inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
            inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus src-ip-prefix"><i class="fa fa-minus"></i></a>';
            inputPlus += '     </span></div>';
            inputPlus += '  </div>';
            $("#srcIpAndPrefixDiv .form-group:last").after(inputPlus);
        }
    }
    if (mDstIpSegmentArray.length>1){
        for (var i=1;i<mDstIpSegmentArray.length;i++){
            var inputPlus =  '';
            inputPlus += '  <div class="form-group" >';
            inputPlus += '     <label class="col-md-2 control-label p-n">目的IP  </label>';
            inputPlus += '     <div class="col-md-2 destIp"><input type="text" name="destIp" value='+mDstIpSegmentArray[i].ipAddr+' class="form-control"/></div>';
            inputPlus += '     <label class="col-md-2 control-label p-n">IP前缀  </label>';
            inputPlus += '     <div class="col-md-2 destIpPrefix"><input type="text"  name="destIpPrefix" value='+mDstIpSegmentArray[i].ipPrefixLen+' class="form-control"/></div>';
            inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
            inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus dest-ip-prefix"><i class="fa fa-minus"></i></a>';
            inputPlus += '     </span></div>';
            inputPlus += '  </div>';
            $("#destIpAndPrefixDiv .form-group:last").after(inputPlus);
        }

    }
    if (mSrcPortArray.length>1) {
        for (var i=1;i<mSrcPortArray.length;i++){
            var inputPlus =  '';
            inputPlus += '  <div class="form-group" >';
            inputPlus += '     <label class="col-md-2 control-label p-n">源端口起始值  </label>';
            inputPlus += '     <div class="col-md-2 srcPortBegin"><input type="text" name="srcPortBegin" value='+mSrcPortArray[i].portStart+' class="form-control"/></div>';
            inputPlus += '     <label class="col-md-2 control-label p-n">源端口结束值  </label>';
            inputPlus += '     <div class="col-md-2 srcPortEnd"><input type="text"  name="srcPortEnd" value='+mSrcPortArray[i].portEnd+' class="form-control"/></div>';
            inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
            inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus src-port"><i class="fa fa-minus"></i></a>';
            inputPlus += '     </span></div>';
            inputPlus += '  </div>';
            $("#srcPortDiv .form-group:last").after(inputPlus);
        }

    }
    if (mDestPortArray.length>1){
        for (var i=1;i<mDestPortArray.length;i++){
            var inputPlus =  '';
            inputPlus += '  <div class="form-group" >';
            inputPlus += '     <label class="col-md-2 control-label p-n">目的端口起始值  </label>';
            inputPlus += '     <div class="col-md-2 destPortBegin"><input type="text" name="destPortBegin" value='+mDestPortArray[i].portStart+' class="form-control"/></div>';
            inputPlus += '     <label class="col-md-2 control-label p-n">目的端口结束值  </label>';
            inputPlus += '     <div class="col-md-2 destPortEnd"><input type="text"  name="destPortEnd" value='+mDestPortArray[i].portEnd+' class="form-control"/></div>';
            inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
            inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus dest-port"><i class="fa fa-minus"></i></a>';
            inputPlus += '     </span></div>';
            inputPlus += '  </div>';
            $("#destPortDiv .form-group:last").after(inputPlus);
        }
    }
    if (mDataMatchArray.length>1){
        for (var i=1;i<mDataMatchArray.length;i++){
            var inputPlus =  '';
            inputPlus += '  <div class="form-group" >';
            inputPlus += '     <label class="col-md-2 control-label p-n">特征字偏移量</label>';
            inputPlus += '     <div class="col-md-2 offSet"><input type="text" name="offSet" value='+mDataMatchArray[i].dataMatchOffset+' class="form-control" /></div>';
            inputPlus += '     <label class="col-md-2 control-label p-n">匹配字符串</label>';
            inputPlus += '     <div class="col-md-2 matchStr"><input type="text" name="matchStr" value='+mDataMatchArray[i].dataMatchContent+'  class="form-control"/></div>';
            inputPlus += '     <div class="col-md-1 "><span class="help-block m-b-none" >';
            inputPlus += '           <a class="sourse-a m-l-n m-r-sm check-minus offset-match-str"><i class="fa fa-minus"></i></a>';
            inputPlus += '     </span></div>';
            inputPlus += '  </div>';
            $("#offSetDiv .form-group:last").after(inputPlus);
        }
    }

}

// 删除
function deleteFunction(url,id){

    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var policyIds = new Array();
    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        policyIds.push(item.policyId);
    }
    if (policyIds.length==0){
        policyIds.push(id);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{"policyIds":policyIds},
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
    // 策略在有效期才有重发按钮
    var needResend = false;
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var todayTimestamp = Date.parse(year+"-"+month+"-"+day+" 00:00:00");
    var now = (todayTimestamp/1000);
    if (row['startTime'] <= now && now <= row['endTime']){
        needResend = true;
    };

	var redo = $("#redoFlag").val();
	var deleteFlag = $("#deleteFlag").val();
	var modify = $("#modifyFlag").val();
	var format="";
	if(redo==1){
		if (true){
			format +="<a href='#' title='重发' class='m-r'><i class='fa fal fa-share resend'></i></a>";
		}
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
                deleteFunction('/apppolicy/flowmirror/delete.do',row.policyId);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}

function getDetail(showTable,index){
    var webFlow = $("#table").bootstrapTable('getData');
	var redo = $("#redoFlag").val();
    if(redo==1 ){
    	PolicyDetail.showDetail(webFlow[index].messageNo,9,2,1,showTable);
    }else{
    	PolicyDetail.showDetail(webFlow[index].messageNo,9,2,0,showTable);    	
    }
}


// 重发指定应用用户策略 和用户绑定策略
function resendPolicy(row){
    $.ajax({
        url: "/apppolicy/flowmirror/resend.do",
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

function changeLongStartTime(){
    if (flowMirrorStrategy.startTime==''){
        var now = new Date();
        var year = now.getFullYear();
        var month = now.getMonth()+1;
        var day = now.getDate();
        flowMirrorStrategy.startTime = new Date(year+"-"+month+"-"+day+" 00:00:00").getTime()/1000;
    } else {
        flowMirrorStrategy.startTime = new Date(flowMirrorStrategy.startTime+" 00:00:00").getTime()/1000;
    }

}

function changeLongEndTime(){
    if (flowMirrorStrategy.endTime==''){
        flowMirrorStrategy.endTime = 0;
    } else {
        flowMirrorStrategy.endTime = new Date(flowMirrorStrategy.endTime+" 00:00:00").getTime()/1000;
    }
}

function checkParameterValid(){
    if (flowMirrorStrategy.messageName==undefined||flowMirrorStrategy.messageName==''){
        // swal("请输入策略名称");
        warn("messageNameDiv","请输入策略名称");
        return false;
    }

    if (flowMirrorStrategy.appType==undefined||flowMirrorStrategy.appType==''){
        // swal("请选择应用类型");
        warnSelect("appTypeDiv");
        return false;
    }

    if (flowMirrorStrategy.appId==undefined||flowMirrorStrategy.appId==''){
        // swal("操作失败", "请选择子应用类型", "error");
        warnSelect("appIdDiv");
        return false;
    }

    if (flowMirrorStrategy.appId==0){
        if (flowMirrorStrategy.appName==undefined||flowMirrorStrategy.appName==''){
            // swal("请输入自定义子应用名");
            warn("appNameDiv","请输入自定义子应用名");
            return false;
        }
    }

    if (flowMirrorStrategy.groupNo==0){
        // swal("请选择镜像端口组");
        warnSelect("mpGroup");
        return false;
    }

    if (flowMirrorStrategy.direction==0){
        // swal("请选择镜像方向");
        warnSelect("mirrorDirection");
        return false;
    }

    if (flowMirrorStrategy.flowAdd==undefined||flowMirrorStrategy.flowAdd==''){
        // swal( "请选择是否需要转发整条流");
        warnSelect("mirrorFlowAdd");
        return false;
    }

    if (!isNmber0(flowMirrorStrategy.cutLength)){
        // swal("请输入合法的镜像报文截长");
        warn("appNameDiv","mirrorCutLength");
        return false;
    }

    return true;
}
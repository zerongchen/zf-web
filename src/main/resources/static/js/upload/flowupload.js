/***********************************************************************************
flowUpload	 * 
             *  zTree:
		     *      变量 setting: 树的设置
		     *      方法 getNodes：根据类型获取树节点
			 *          initTree：初始化树
			 *          getCheckedData：获取选中的树节点
			 *          initCheck：初始化选中的树
             *  action:
			 *     方法  initClick：初始化所有的click事件
			 *          init：初始化对象
			 *          
***********************************************************************************          
function 	 *          saveData：保存数据（新增或修改）
             *          submitForm：form表单提交
			 *          addInputEvent：点击+追加input框,编辑根据userName 个数回填
			 *          newAddButtonFunction：新增页面的初始化
			 *          deleteFunction：删除操作
			 *          queryParams：表格数据的分页参数设置
			 *          getColumnFunction：表头重新设置
			 *          window.operatingevent：点击事情，主要针对于编辑操作
			 *          customFunction：定制方法，主要添加用户组
			 *          loadDev：下拉加载综分设备
 ********************************************************************************/  

var flowUpload={
		/***********************************************ztree***********************************************/
		setting: {
			view: {
				expandSpeed: "", //zTree 节点展开、折叠时的动画速度，设置方法同 JQuery 动画效果中 speed 参数。
				selectedMulti: false // 禁止多点同时选中的功能
			},
	    	check: {
			enable: true
		   },
			data: {
			    key: {
			   		isParent: "isParent",
			   		name: "areaName"
		        },
				simpleData: {
					enable: true,
					idKey: "areaId",
					pIdKey: "pareaId",
					rootPId: '0'
				}
			}
	},
	getNodes:function(areaType){
		var nodeData;
		$.ajax({
			url: "/flowDirection/obtainTree",
			async:false,
			type:'POST',
			dataType:"json",
			data:{
				areaType:areaType
			},
			success : function(result) {
				nodeData = result;
			},
			error : function() {
			}
		});
		return nodeData;
	},
	initTree: function(){
		$.fn.zTree.init($("#internalAreaTree"), flowUpload.setting, flowUpload.getNodes(2)).expandAll(true);
		$.fn.zTree.init($("#internalIdcTree"), flowUpload.setting, flowUpload.getNodes(3)).expandAll(true);
		$.fn.zTree.init($("#externalAreaTree"), flowUpload.setting, flowUpload.getNodes(2)).expandAll(true);
		$.fn.zTree.init($("#externalIdcTree"), flowUpload.setting, flowUpload.getNodes(3)).expandAll(true);
	},
	 //获取节点
	 getCheckedData:function(ztreeId) {
	        var treeObj = $.fn.zTree.getZTreeObj(ztreeId);
	        var nodes = treeObj.getCheckedNodes(true);
	        
	        /**********************************构造一个构造函数，用来实例化json对象 *****************************/
	        var jsonArray = new Array();
	        function AreaObject(areaName,areaId,pareaId,isParent,areaType){
	            this.areaName = areaName;
	            this.areaId = areaId;
	            this.pareaId = pareaId;
	            this.isParent = isParent;
	            this.areaType = areaType;
	          };
	        for(var i=0; i<nodes.length; i++){
	        	var node = nodes[i];
	        	jsonArray.push(new AreaObject(node.areaName,node.areaId,node.pareaId,node.isParent,node.areaType));
	        	
	        }
	        var json = JSON.stringify(jsonArray);
	        return json;
	 },
    //初始化节点
	initCheck:function(ztreeId,list){
		var treeObj = $.fn.zTree.getZTreeObj(ztreeId);
		var nodes = treeObj.transformToArray(treeObj.getNodes());
		//返回根节点集合 
		$.each(list,function(i,value){
			for (var n=0;n<nodes.length;n++){
				if(nodes[n].areaId == value.areaId){
					treeObj.checkNode(nodes[n], true, true);
				}else {
                    nodes[n].checked = false;
                }
			}
		});
    },
    /***********************************************ztree***end********************************************/
    //流量流向策略的保存
    saveData:function(){
    	$("#saveBtn").click(function(){
    		// debugger;
			$(".has-error").remove();
			var internalAreaGroupList = flowUpload.getCheckedData("internalAreaTree"),
			internalIdcGroupList = flowUpload.getCheckedData("internalIdcTree"),
			externalAreaGroupList = flowUpload.getCheckedData("externalAreaTree"),
			externalIdcGroupList = flowUpload.getCheckedData("externalIdcTree"),
			messageName = $("#areaMessageName").val();
			
			if(messageName == "" || messageName == undefined){
				customWarn('messageNameMsg','请输入策略名称','col-md-4'); 
				return false;
			}else if(messageName.length>50){
				customWarn('messageNameMsg','策略名称不能超过50个字符','col-md-4'); 
				return false;
			}else if(internalAreaGroupList == '[]' && internalIdcGroupList == '[]'){
				swal({
					title: "添加失败！",
					text: "请至少选中一个目的区域",
					type: "error"
				});
				return false;
			}else if(externalAreaGroupList == '[]' && externalIdcGroupList == '[]'){
				swal({
					title: "添加失败！",
					text: "请至少选中一个目的区域",
					type: "error"
				});
				return false;
			}	    
			
			$.ajax({
				url: "/flowDirection/save",
				async:false,
				type:'POST',
				dataType:'json',
				data:{
					messageName: messageName,
					internalAreaGroupJson: internalAreaGroupList,
					internalIdcGroupJson: internalIdcGroupList,
					externalAreaGroupJson: externalAreaGroupList,
					externalIdcGroupJson: externalIdcGroupList
				},
				success : function(data) {
					if(data.result==0){
						$("#areaGroupModal").modal('hide');
						swal({
							title: "添加成功",
							type: "success"
						});
						//加载流量流向策略
				        loadSel('areaGroupMessageNo','/select/getAreaGroupPolicy',true);
					}else if(data.result==1){
						swal({
							title: "添加失败",
							text: data.message,
							type: "error"
						});
					}else if(data.result==2){
						swal({
							title: "添加失败",
							text: data.message,
							type: "error"
						});
					}
				}
				
			});
		});
    },
    /*******web信息推送保存******************/
    webPushSave:function(){
    	var webpush ={};
    	// 参数合法性校验
    	function checkAllParameter(formData) {
    	    webpush.advId = formData.advId;
    	    webpush.messageNo = formData.messageNo;
    	    webpush.messageName = formData.messageName;
    	    webpush.advType = formData.advType;
    	    webpush.advWhiteHostListId = formData.advWhiteHostListId;
    	    webpush.triggerHostListId = formData.triggerHostListId;
    	    webpush.triggerKwListId = formData.triggerKwListId;
    	    webpush.advFramDPIrl = formData.advFramDPIrl;
    	    webpush.advToken = formData.advToken;
    	    webpush.advDelay = formData.advDelay;
    	    webpush.rStartTime = formData.start;
    	    webpush.rEndTime = formData.end;

    	    if (webpush.messageName==undefined||webpush.messageName==''){
    	        swal("操作失败", "请输入策略名称", "error");
    	        return false;
    	    }
    	    if (webpush.advType==undefined||webpush.advType==''){
    	        swal("操作失败", "推送信息类型", "error");
    	        return false;
    	    }
    	    if (webpush.advFramDPIrl==undefined||webpush.advFramDPIrl==''){
    	        swal("推送的URL", "推送的URL不能为空", "error");
    	        return false;
    	    }
    	    if (webpush.advToken==undefined||webpush.advToken==''){
    	        swal("推送配额", "推送配额不能为空", "error");
    	        return false;
    	    }
    	    if (webpush.advToken < 1||webpush.advToken>255){
    	        swal("推送配额", "取值1-255之间", "error");
    	        return false;
    	    }
    	    if (webpush.advDelay==undefined||webpush.advDelay==''){
    	        swal("推送延时", "推送延时不能为空", "error");
    	        return false;
    	    }
    	    if (webpush.advDelay < 0||webpush.advDelay>255){
    	        swal("推送延时", "取值0-255之间", "error");
    	        return false;
    	    }
    	    return true;
    	}
    	
    	// 保存前对参数进行过滤  去掉为设值的参数
    	function filterParameter(){
    	    if (webpush.advId==''){
    	        webpush.advId = undefined;
    	    }
    	    if (webpush.messageNo==''){
    	        webpush.messageNo = undefined;
    	    }
    	    if (webpush.rStartTime==''){
    	        var now = new Date();
    	        var year = now.getFullYear();
    	        var month = now.getMonth()+1;
    	        var day = now.getDate();
    	        webpush.rStartTime = new Date(year+"-"+month+"-"+day+" 00:00:00").getTime()/1000;
    	    } else {
    	        webpush.rStartTime = new Date(webpush.rStartTime+" 00:00:00").getTime()/1000;
    	    }
    	    if (webpush.rEndTime==''){
    	        webpush.rEndTime = 0;
    	    } else {
    	        webpush.rEndTime = new Date(webpush.rEndTime+" 00:00:00").getTime()/1000;
    	    }
    	}
    	
    	$("#webPushAddSubmitBut").click(function(){
            var formData = $('#webPushAddOrupdateForm').formToJSON();
            var url = "";
            if( (formData.advId==undefined||formData.advId=='')
                && (formData.messageNo==undefined||formData.messageNo=='') ){
                url = '/apppolicy/webpush/add';
            } else {
                url = '/apppolicy/webpush/update';
            }

            // 检查所有参数
            if (checkAllParameter(formData)){
                filterParameter();
                $.ajax({
                    url: url,
                    type: 'POST',
                    dataType:'json',
                    data:formData,
                    success: function (data) {
                        if (data.result==0){
                            $("#webPushMessageNo").val('');
                            cleanAllInput();
                            $('#webPushGroupModal').modal('hide');
                            swal(data.message);
                            loadSel('webPushMessageNo','/select/getWebPushManagePolicy',true);
                        } else {
                            swal("操作失败", "存在策略名称相同数据", "error");
                        }
                    },
                    error:function () {
                        swal("操作失败", "取消新增/删除操作！", "error");
                    }
                })
            }
    	});
    	
    	// 推送类型改变时绑定改变事件
    	$('#webPushAdvType').change(function () {
    	    clearWarn($('#addOrupdateForm'));
    	    var advType = this.value;
    	    if(advType == "0"){
    	        showNone();
    	    }else if(advType == "1"){
    	        showWhiteLisList();
    	        loadWhiteHostList();
    	    }else if(advType == "2"){
    	        showWhiteLisList();
    	        loadWhiteHostList();
    	    }else if(advType == "3"){
    	        showHostLisList();
    	        loadHostList();
    	    }else if(advType == "4"){
    	        showWhiteTriggerKw();
    	        loadWhiteHostList();
    	        loadKwList();
    	    }else{
    	        showNone();
    	        loadWhiteHostList();
    	    }
    	});
    	//推送白名单的div
    	function showWhiteLisList() {
    	    $('#d_advWhiteHostListId').show();
    	    $('#d_triggerHostListId').hide();
    	    $('#d_triggerKwListId').hide();
    	}

    	//推送触发网站的div
    	function showHostLisList() {
    	    $('#d_advWhiteHostListId').hide();
    	    $('#d_triggerHostListId').show();
    	    $('#d_triggerKwListId').hide();
    	}
    	//推送网站白名单,触发关键字显示的div
    	function showWhiteTriggerKw() {
    	    $('#d_advWhiteHostListId').show();
    	    $('#d_triggerHostListId').hide();
    	    $('#d_triggerKwListId').show();
    	}
    	//
    	function showNone() {
    	    $('#d_advWhiteHostListId').show();
    	    $('#d_triggerHostListId').hide();
    	    $('#d_triggerKwListId').hide();
    	    $('#d_triggerHostListId .selectpicker').selectpicker('refresh');
    	    $('#d_triggerKwListId .selectpicker').selectpicker('refresh');
    	}

    	// 白名单
    	function loadWhiteHostList() {
    	    var data = {infoType:1};
    	    loadSelectPicker1('advWhiteHostListId','/infoLibrary/getTriggerHost',JSON.stringify(data),true,'','POST');
    	}
    	// 触发网站
    	function loadHostList() {
    	    loadSelectPicker2('triggerHostListId','/infoLibrary/getTriggerHost',{infoType:2},false,'','POST');
    	}
    	// 触发关键字
    	function loadKwList() {
    	    loadSelectPickerKW('triggerKwListId','/infoLibrary/getTriggerKW',{},false,'','POST');
    	}
    	function newAddButtonFunction(){
    	    cleanAllInput();
    	}
    	// 设置参数值
    	function cleanAllInput(){

    	    $('#webPushMessageNo').val('');
    	    $('#webPushAdvId').val('');
    	    $('#webPushMessageName').val('');
    	    $("#webPushAdvType").val("");

    	    $("#advWhiteHostListId").empty();
    	    $("#triggerHostListId").empty();
    	    $("#triggerKwListId").empty();

    	    $("#webPushAdvFramDPIrl").val("");
    	    $("#webPushAdvToken").val("");
    	    $("#webPushdAvDelay").val("");
    	    $("#webPushstart").val("");
    	    $("#webPushend").val("");
    	    showNone();
    	}
    	
    	/**
    	 * 加载下拉框 bootstrap-select
    	 * selId 下拉框id
    	 * url 请求地址
    	 * flag 特殊定义
    	 * val 默认选中项值
    	 */
    	function loadSelectPicker1(selId, url,datav, flag, val,methodType){
    	    $.ajax({
    	        url: url,
    	        data:datav,
    	        type: methodType,
    	        async: false,
    	        dataType: 'json',
    	        success: function(data){
    	            var data = eval(data);
    	            var $selId = $('#' + selId),
    	                option = '';
    	            if(flag){
    	                option+='<option value=0">请选择</option>'
    	            }
    	            $.each(data, function (i,n) {
    	                option += '<option value="' + n.triggerHostListid + '">' + n.triggerHostListname + '</option>'
    	            });
    	            $selId.children().remove();
    	            $selId.append(option);
    	            for(var i=0;i<$selId[0].options.length;i++){
    	                if ($selId[0].options[i].value == val){
    	                    $selId[0].options[i].selected = true;
    	                    break;
    	                }
    	            }
    	            /*if(val!=undefined){
    	                $('#' + selId)[0].selected=true;//默认选中
    	            }*/
    	        }
    	    });
    	}

    	/**
    	 * 加载下拉框 bootstrap-select
    	 * selId 下拉框id
    	 * url 请求地址
    	 * flag 特殊定义
    	 * val 默认选中项值
    	 */
    	function loadSelectPicker2(selId, url,datav, flag, val,methodType){
    	    $.ajax({
    	        url: url,
    	        data:datav,
    	        type: methodType,
    	        async: false,
    	        dataType: 'json',
    	        success: function(data){
    	            var data = eval(data);
    	            var $selId = $('#' + selId),
    	                option = '';
    	            $.each(data, function (i,n) {
    	                option += '<option value="' + n.triggerHostListid + '">' + n.triggerHostListname + '</option>'
    	            });
    	            $selId.children().remove();
    	            $selId.append(option);
    	            $('#' + selId+".selectpicker").selectpicker({
    	                style: 'btn-boostrap-sl'
    	            });
    	            $('#' + selId+".selectpicker").selectpicker('refresh');
    	            if(val!=undefined){
    	                $('#' + selId+".selectpicker").selectpicker('val', val);//默认选中
    	            }

    	        }
    	    });
    	}

    	/**
    	 * 加载下拉框 bootstrap-select
    	 * selId 下拉框id
    	 * url 请求地址
    	 * flag 特殊定义
    	 * val 默认选中项值
    	 */
    	function loadSelectPickerKW(selId, url,datab, flag, val,methodType){
    	    $.ajax({
    	        url: url,
    	        data:datab,
    	        type: methodType,
    	        async: false,
    	        dataType: 'json',
    	        success: function(data){
    	            var data = eval(data);
    	            var $selId = $('#' + selId),
    	                option = '';
    	            $.each(data, function (i,n) {
    	                option += '<option value="' + n.triggerKwListid + '">' + n.triggerKwListname + '</option>'
    	            });
    	            $selId.children().remove();
    	            $selId.append(option);
    	            $('#' + selId+".selectpicker").selectpicker({
    	                style: 'btn-boostrap-sl'
    	            });
    	            $('#' + selId+".selectpicker").selectpicker('refresh');
    	            if(val!=undefined){
    	                $('#' + selId+".selectpicker").selectpicker('val', val);//默认选中
    	            }

    	        }
    	    });
    	}
    },
    /*******流量流向策略 保存结束**************/
    
    seq:0,
    initClick:function () {
        $("#addPlush").click(function() {
            flowUpload.seq++;
            var inputPlus = "<div class='form-group newInput' ><label class='col-md-2 control-label p-n'></label>"
                +"<div class='col-md-3'><input type='text' name='userName' id='newUserNameInp'+flowUpload.seq class='form-control'></div>"
                +"<div class='col-md-3 '><span class='help-block m-b-none' ><a class='sourse-a m-l-n m-r-sm check-minus'><i class='fa fa-minus'></i></a></span></div>"
                +"</div>";
            $(this).parent().parent().parent().after(inputPlus);
            $(".check-minus").click(function() {
                $(this).parent().parent().parent().remove()
            });
        });
        $('#userType').change(function () {
            clearWarn($('#addOrupdateForm'));
            var userType = this.value;
            $('#addOrupdateForm').find('input[name="userName"]').val('');
            if(userType == 0){
                $('#userNameDiv').hide();
                $('#puserGroup').hide();
                $('#userNameDiv .newInput').remove();
            }else if(userType == 1 || userType == 2){
                $('#userNameDiv').show();
                $('#puserGroup').hide();
                $('#userNameDiv .newInput').remove();
            }else if(userType == 3){
                $('#userNameDiv').hide();
                $('#puserGroup').show();
                $('#userNameDiv .newInput').remove();
                loadUserGroup();
            }

        });
        $('#addSubmitBut').click(function () {

            var formData = $('#addOrupdateForm').formToJSON();
            if(formData.messageNo!="" && formData.messageNo!=undefined){
                //修改
                submitForm('/upload/update',formData);
            }else {
                //新增
                formData.messageNo =undefined;
                submitForm('/upload/insert',formData);
            }
            
        });
        
        $('#searchFormButton').click(function () {
            initTableJs.refreshData();
        });
        
        $('#newAddGroup').click(function () {
            document.getElementById("createUserGroupForm").reset();
            $('#add-usergroup-snippet').modal('show');
            $('#operate').val(0);
            $('#operateDiv').hide();
        });
        
        $("#packetSubtype").on('change',function(e){
            clearWarn($('#addOrupdateForm'));
            var data = $('#searchForm').formToJSON();
            data.packetSubtype = this.value;
            loadDev({packetType:data.packetType,packetSubtype:data.packetSubtype});
        });
        
        $("#rFreqSl").on('change',function(e){
            clearWarn($('#addOrupdateForm'));

        });
        
        $("#rDestidSl").on('change',function(e){
            clearWarn($('#addOrupdateForm'));
        });
        
    },

    init:function () {
        flowUpload.initClick();
        flowUpload.initTree();
        flowUpload.saveData();
        flowUpload.webPushSave();
    }
};

$(document).ready(function() {
    initTableJs.initParam('/upload/getList', '/upload/delete.do', getColumnFunction);
    initTableJs.init2();
    flowUpload.init();
    initDate(3,false);
	//初始化查询页面时间条
	if(document.getElementById("searchStart")!=null){
        initDefinedEleIdDate(3,false,false,"searchStart","searchEnd");
    }
    addUserGroupJs.exportTemplate();
    addUserGroupJs.createUserGroup();
    //ddos
    ddosManage.saveDdosBtn();
	// 新增指定应用用户策略
    appUserAddModalJs.init();
    initDefinedEleIdDate(3,false,false,"appStart","appEnd");
});
//submit方法
function submitForm(url,data) {
    clearWarn($('#addOrupdateForm'));
    if(data.messageName == "" || data.messageName.length>50 ){
        warn('messageNameDiv','请输入策略名称，并且不超过50个字符');
        // swal('请输入策略名称，并且不超过50个字符');
        return false;
    }
    data.packetType = $('#searchForm').find('input[name="packetType"]').val();
    var packetSubtype = $('#searchForm').find('input[name="packetSubtype"]').val();
    if(data.packetSubtype == undefined || data.packetSubtype==''){
        if(data.packetType == 2){
            if(data.packetSubtype==''){
                warnSelect('packetSubtypeSl100');
                return false;
            }else {
                data.packetSubtype = $('#packetSubtype').val();
            }
        }else if(data.packetType == 1 && packetSubtype ==0){
            var value = $('#packetSubtypeSl0').find('input[type="radio"]').val();
            data.packetSubtype = value;
        }else if(data.packetType == 2 && packetSubtype ==130){
            var value = $('#packetSubtypeSl130').find('input[type="radio"]').val();
            data.packetSubtype = value;
        }else if(data.packetType == 1 && packetSubtype ==196){
        	if(data.areaGroupMessageNo=='' || data.areaGroupMessageNo==undefined){
                warnSelect('packetSubtypeSl196');
                return false;
            }else{
            	data.packetSubtype = packetSubtype;
            }
        }else if(data.packetType == 1 && packetSubtype ==132){
        	if(data.webPushMessageNo=='' || data.webPushMessageNo==undefined){
                warnSelect('packetSubtypeSl132');
                return false;
            }else{
            	data.packetSubtype = packetSubtype;
            }
        } else if(data.packetType == 1 && packetSubtype == 3){
            if(data.appUserMessageNo==undefined || data.appUserMessageNo==''){
                warnSelect('packetSubtypeSl3');
                return false;
            }else{
                data.packetSubtype = packetSubtype;
            }
        } else {
            data.packetSubtype = packetSubtype;
        }
    }
    if(data.rfreq=='' ){
        warnSelect('rFreqSlDiv');
        return false;
    }
    if(data.zongfenId=='' || data.zongfenId==undefined){
        warnSelect('rDestidSlDiv');
        return false;
    }
    if(data.userType==null || data.userType==undefined){
    	swal("请选择绑定用户类型");
    	return false;
	}
    if(data.userType == 1 || data.userType == 2){
        var names = new Array();
        var $obj = $('#addOrupdateForm').find('input[name="userName"]');
        var num = $obj.length;
        for (var i=0;i<num;i++){
            var value  = $obj.eq(i).val();
            if(value==''){
                $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入</i>');
                $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                return false;
            }
            if(data.userType == 1){
                if(value.length>50){
                    $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入长度不超过50个字符</i>')
                    $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return false;
                }
            }else {
                if(!checkIP(value)){
                    $obj.eq(i).parent().parent().find('span>a').last().after('<i class="fa fa-info-circle has-error" style="color:#a94442" > 请输入正确IP用户</i>')
                    $obj.eq(i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return false;
                }
            }
            names.push(value);
        }
        data.userName = names;
        data.puserGroup=undefined;
    }else if(data.userType == 3){
        if(data.puserGroup==undefined){
            swal("请选择关联的用户组");
            return false;
        }
        data.puserGroup = data.puserGroup.split(",");
        data.userName = undefined;
    }else {
        data.puserGroup = undefined;
        data.userName = undefined;
    }
    $.ajax({
        url: url,
        data: JSON.stringify(data),
        type: 'POST',
        async: true,
        dataType: 'json',
        contentType: "application/json",
        success: function (data) {
            var title = "";
            if (data != null) {
                title = data.message;
                if(data.result == 0){
                    swal({
                        title: title,
                        text: '',
                        html: true
                    }, function (isConfirm) {
                    });
                }else {
                    swal({
                        title: title,
                        text: '',
                        html: true
                    }, function (isConfirm) {
                        $('#myModaladd').modal('hide');
                        if(document.getElementById("table")!=null){
                            initTableJs.refreshData();
						}
                    });
                }
            } else {
                title ="操作成功";
                swal({
                    title: title,
                    text: '',
                    html: true
                }, function (isConfirm) {
                    $('#myModaladd').modal('hide');
                    initTableJs.refreshData();
                });
            }
        },
        error:function () {
            swal("失败，请刷新页面重试");
        }

    })


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

function newAddButtonFunction() {
    $('#myModaladd').modal('show');
    $('#h5title').text('新增');
    resetInput();
     
    var data = $('#searchForm').formToJSON();

    loadDev({packetType:data.packetType,packetSubtype:data.packetSubtype});
    $('#packetSubtypeSl0').find('input[type="radio"]').attr('disabled',false);
    $('#packetSubtypeSl130').find('input[type="radio"]').attr('disabled',false);
    $('#packetSubtype').attr('disabled',false);
    //加载流量流向策略
    loadSel('areaGroupMessageNo','/select/getAreaGroupPolicy',true);
    //加载ddos异常流量管理策略
    loadSel('ddosManageMessageNo','/select/getDdosManagePolicy',true);
    //加载WEB推送管理策略
    loadSel('webPushMessageNo','/select/getWebPushManagePolicy',true);
    if (data.packetType==1 && data.packetSubtype==3){
    	loadSel('appUserMessageNo','/select/getAppUserPolicy',true);
	}
}
//删除
function deleteFunction(url,data){
    let result;
    let shuju = [];
    let packType,packSubType;
    if(data==undefined){
        result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
            for(var i = 0; i < result.length; i++) {
                var item = result[i];
                // $('#table>tbody>tr').remove('.selected');
                shuju.push(item.messageNo);
            }
        packType=result[0].packetType;
        packSubType=result[0].packetSubtype;
    }else {
        shuju.push(data.messageNo);
        packType=data.packetType;
        packSubType=data.packetSubtype;
    }

    $.ajax({
        url: url,
        type: 'POST',
        data:{messageNos:shuju,packType:packType,packSubType:packSubType},
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
function queryParams(params) {
    var data = $('#searchForm').formToJSON();
    if(data.messageName==''){
        data.messageName=undefined;
    }
    if (data.searchStartTime==''){
    	data.searchStartTime = undefined;
	}
	if (data.searchEndTime==''){
    	data.searchEndTime = undefined;
	}
    data.page = params.offset/params.limit +1 ;
    data.pageSize = params.limit;
    return data;
}
function getColumnFunction() {
    var packetType = $('#searchForm').find('input[name="packetType"]').val();
    var packetSubtype = $('#searchForm').find('input[name="packetSubtype"]').val();
    var column =[
        {field: 'state',checkbox: true},
        {field: 'messageNo',title: '策略ID'},
        {field: 'messageName',title: '策略名称'},
        {field: 'rfreq',title: '上报周期',formatter:function (value, row, index) {
            return getRfreq(row.rfreq);
        }},
        {field: 'policyCount',title: '<span title="策略成功数/异常数">策略成功数/异常数</span>',formatter:function (value, row, index) {
            if(value=="0/0"){
                return "0/0";
            }else {
                return "<a href='#' onclick='getDetail("+index+",0)' title='详情'>"+value+"</a>";
            }
        }},
        {field: 'bindPolicyCount',title: '<span title="绑定成功数/异常数">绑定成功数/异常数</span>',formatter:function (value, row, index){
            if(value=="0/0"){
                return "0/0";
            }else {
                return "<a href='#' onclick='getDetail("+index+",1)' title='详情'>"+value+"</a>";
            }
        }},
        {field: 'startTime',title: '上报开始时间',formatter:function (value,row,index) {
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'endTime',title: '上报结束时间',formatter:function (value,row,index) {
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'createTime',title: '创建时间',formatter:function (value,row,index) {
            return "<span title='"+value+"'>"+value+"</span>";
        }},
        {field: 'operating',title:'操作',formatter:operatingFormatter,events:operatingevent }
    ];
    if(packetType == 1 && (packetSubtype ==0 || packetSubtype==5)){
        column.splice(3,0,{field: 'packetSubtype',title: '资源来源',formatter: function (value, row, index) {
            if(value == 0) return "<span title='资源在网外'>资源在网外</span>";
            else if(value == 5) return "<span title='资源在网内'>资源在网内</span>";
        } });
    }

    if(packetType == 1 && (packetSubtype ==130 || packetSubtype==131)){
        column.splice(3,0,{field: 'packetSubtype',title: '上报类型',formatter: function (value, row, index) {
            if(value == 130) return "<span title='一拖N监测结果'>一拖N监测结果</span>";
            else if(value == 131) return "<span title='关键字段'>关键字段</span>";
        } });
    }
    //流量流向上报策略
    if(packetType == 1 && packetSubtype ==196){
    	column.splice(3,0,{field: 'areaGroupMessageName',title: '<span title="流量流向策略名称">流量流向策略名称</span>',formatter:function (value,row,index) {
            return "<span title='"+value+"'>"+value+"</span>";
        } });
    	column.splice(4,0,{field: 'areaGroupMessageNo',title: '流量流向策略ID', visible:false,formatter:function (value,row,index) {
            return "<span title='"+value+"'>"+value+"</span>";
        }});
    }

    //DDOS异常流量上报策略
    if(packetType == 1 && packetSubtype ==192){
        column.splice(3,0,{field: 'ddosMessageName',title: '<span title="ddos异常流量策略名称">ddos异常流量策略名称</span>',formatter:function (value,row,index) {
            return "<span title='"+value+"'>"+value+"</span>";
        } });
        column.splice(4,0,{field: 'ddosManageMessageNo',title: 'ddos异常流量策略ID', visible:false,formatter:function (value,row,index) {
            return "<span title='"+value+"'>"+value+"</span>";
        }});
    }

    //DDOS异常流量上报策略
    if(packetType == 1 && packetSubtype ==3){
        column.splice(3,0,{field: 'appUserMessageName',title: '<span title="">指定应用用户统计策略名称</span>',formatter:function (value,row,index) {
            return "<span title='"+value+"'>"+value+"</span>";
        } });
        column.splice(4,0,{field: 'appUserMessageNo',title: '指定应用用户统计策略ID', visible:false,formatter:function (value,row,index) {
            return "<span title='"+value+"'>"+value+"</span>";
        }});
    }
    return column;
}
function loadUserGroup() {
    loadSelectPicker('puserGroupid','/select/getUserGroup',false);
}
function resetInput() {
    document.getElementById("addOrupdateForm").reset();
    $('#rFreqSl').val('');
    $('#rDestidSl').val('');
    $('#areaGroupMessageNo').val('');
    $('#addOrupdateForm').find('input[type="hidden"]').val('');
    clearWarn($('#addOrupdateForm'));
    resetUserOption();
}

function resetUserOption() {
    $('#userType').val(0);
    $('#userNameDiv').hide();
    $('#puserGroup').hide();
    $('#userNameDiv .newInput').remove()
}
//样式格式化
function operatingFormatter(value, row, index){
	let modifyAu = $('#modifyAu').val();
	let redoAu = $('#redoAu').val();
	let deleteAu = $('#deleteAu').val();
    var format="";
    if(redoAu){
        format +="<a th:authorized='${authorized}_redo' href='#' title='重发' class='m-r'><i class='fa  fal fa-share resend'></i></a>";
	}
	if(modifyAu){
        format +="<a th:authorized='${authorized}_modify' href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>";
	}
	if(deleteAu){
        format +="<a th:authorized='${authorized}_delete' href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>";
	}
    return format;
}
//点击事件
window.operatingevent = {
    'click .resend': function (e, value, row, index) {
        e.preventDefault();
        $.ajax({
            url: '/upload/resend',
            data: JSON.stringify(row),
            type: 'POST',
            async: false,
            contentType: "application/json",
            success: function (data) {
                swal(data.message);
            },
            error:function () {
                swal("重发失败");
            }
        });
    },
    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        loadDev({packetType:row.packetType,packetSubtype:row.packetSubtype});
      //加载流量流向策略
        loadSel('areaGroupMessageNo','/select/getAreaGroupPolicy',true);

        //加载ddos异常流量管理策略
        loadSel('ddosManageMessageNo','/select/getDdosManagePolicy',true);
        
        //加载web推送量管理策略
        loadSel('webPushMessageNo','/select/getWebPushManagePolicy',true);

        if (row.packetType==1 && row.packetSubtype==3){
            loadSel('appUserMessageNo','/select/getAppUserPolicy',true);
        }

        $('#h5title').text("修改");
        $('#myModaladd').modal('show');
        var $dataForm = $('#addOrupdateForm');
        $dataForm.find('input[name="messageNo"]').val(row.messageNo);
        $dataForm.find('input[name="messageName"]').val(row.messageName);
        var packetType = $('#searchForm').find('input[name="packetType"]').val();
        var packetSubtype = $('#searchForm').find('input[name="packetSubtype"]').val();
        if(packetType == 1 && packetSubtype ==0){
            $dataForm.find("input[name=packetSubtype][value=" + row.packetSubtype + "]").attr("checked", true);
            $('#packetSubtypeSl0').find('input[type="radio"]').attr('disabled',true);
        }
        if(packetType == 1 && packetSubtype ==130){
            $dataForm.find("input[name=packetSubtype][value=" + row.packetSubtype + "]").attr("checked", true);
            $('#packetSubtypeSl130').find('input[type="radio"]').attr('disabled',true);
        }
        if(packetType == 2){
            $('#packetSubtype').val(row.packetSubtype);
            $('#packetSubtype').attr('disabled',true);
        }
        $('#rFreqSl').val(row.rfreq);
        $('#rDestidSl').val(row.zongfenId);
      	//加载流量流向策略
        if(packetType == 1 && packetSubtype ==196){
            $('#areaGroupMessageNo').val(row.areaGroupMessageNo);
            $('#lastAreaGroupMessageNo').val(row.areaGroupMessageNo);
        }
        //加载ddos流量策略
        if(packetType == 1 && packetSubtype ==192){
            $('#ddosManageMessageNo').val(row.ddosManageMessageNo);
            $('#lastDdosManageMessageNo').val(row.lastDdosManageMessageNo);
        }
        
      	//加载web推送流量策略
        if(packetType == 1 && packetSubtype ==132){
            $('#webPushMessageNo').val(row.webPushMessageNo);
            $('#lastWebPushGroupMessageNo').val(row.lastWebPushGroupMessageNo);
        }

        if (packetType == 1 && packetSubtype ==3){
            $('#appUserMessageNo').val(row.appUserMessageNo);
            $('#lastAppUserMessageNo').val(row.appUserMessageNo);
		}

        if(row.startTime!=null){
            $('#start').val(row.startTime);
        }
        if(row.endTime!=null){
            $('#end').val(row.endTime);
        }
        var userType = row.userType;
        clearWarn($('#addOrupdateForm'));
        $('#addOrupdateForm').find('input[name="userName"]').val('');
        if(userType == 0){
            $('#userNameDiv').hide();
            $('#puserGroup').hide();
            $('#userNameDiv .newInput').remove();
            $('#userType').val(0)
        }else if(userType == 1 || userType == 2){
            $('#userNameDiv').show();
            $('#puserGroup').hide();
            $('#userNameDiv .newInput').remove();
            $('#userType').val(userType);
            var userNames = row.userName;
            for (var i =0;i<userNames.length;i++){
                if(i==0){
                    $('#userNameDiv').find('input[name="userName"]').eq(i).val(userNames[i]);
                }else {
                    addInputEvent();
                    $('#userNameDiv').find('input[name="userName"]').eq(i).val(userNames[i]);
                }
            }
        }else if(userType == 3){
            $('#userNameDiv').hide();
            $('#puserGroup').show();
            $('#userNameDiv .newInput').remove();
            $('#userType').val(userType);
            loadSelectPicker('puserGroupid','/select/getUserGroup',false,row.puserGroup);
        }else {
        	$('#userType').val("");
		}
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
                deleteFunction('/upload/delete.do',row);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
};
function customFunction() {
    loadUserGroup();
}
function loadDev(data) {
    if(data.packetType==2 && data.packetSubtype==100){
        //不加载,在选择协议类型的时候加载
    }else {
        loadSel('rDestidSl','/select/getZongfenDev',true,data);
    }
}
function getRfreq(id) {
    var array = ["即时上报","每5分钟上报(整点时间)","每1小时上报(整点时间)","每3小时上报(整点时间)","每天上报","每30秒上报",
    "每1分钟上报","每3分钟上报"];
    var desc = array[id];
    if (desc==undefined){
        desc ="-"
    }
    return "<span title='"+desc+"'>"+desc+"</span>";
}
function getDetail(index,tab){
    var flow = $("#table").bootstrapTable('getData');
    var packetType = flow[index].packetType;
    var packetSubtype = flow[index].packetSubtype;

    let redoAu = $('#redoAu').val();
    //为下发详情中的重发提供模块编号
    var model = $('#modelTypes').val().substring(2);
    if(packetType == 1 && packetSubtype == 196){
    	PolicyDetail.showDetail(flow[index].messageNo,messageType.FLOW_UPLOAD_POLICY.value,2,redoAu,tab,model);
    }else if(packetType == 1 && packetSubtype == 67){
        PolicyDetail.showDetail(flow[index].messageNo,messageType.DDOS_UPLOAD_POLICY.value,2,redoAu,tab,model);
    }
    // else if(packetType == 1 && packetSubtype == 3){
    //     PolicyDetail.showDetail(flow[index].messageNo,messageType.APP_USER_POLICY.value,3,"指定应用用户统计策略",flow[index].appUserMessageNo,1,tab);
    // }
    else{
    	PolicyDetail.showDetail(flow[index].messageNo,messageType.FLOW_UPLOAD_POLICY.value,2,redoAu,tab,model);
    }
}


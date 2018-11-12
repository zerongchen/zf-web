/*

var flowUpload={
		/!***********************************************ztree***********************************************!/
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
				alert("对不起，没有数据！");
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
	        
	        /!**********************************构造一个构造函数，用来实例化json对象 *****************************!/
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
	        	console.log(ztreeId+': areaId='+node.areaId+", isParent="+node.isParent);
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
    /!***********************************************ztree***end********************************************!/
    //流量流向策略的保存
    saveData:function(){
    	$("#saveBtn").click(function(){
    		debugger;
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
				url: "/flowDirection/saveOrUpdate",
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
    /!*******流量流向策略 保存结束**************!/
    
    seq:0,
    initClick:function () {
        $("#addPlush").click(function() {
            flowUpload.seq++;
            var inputPlus = "<div class='form-group newInput' ><label class='col-md-1 control-label p-n'></label>"
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
    }
};



$(document).ready(function() {
    initTableJs.initParam('/upload/getList', '/upload/delete.do', getColumnFunction);
    initTableJs.init2();
    flowUpload.init();
    initDate(3,false);
    addUserGroupJs.exportTemplate();
    addUserGroupJs.createUserGroup();
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
        }else {
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
    console.log(data);
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
                        initTableJs.refreshData();
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
    var inputPlus = "<div class='form-group newInput' ><label class='col-md-1 control-label p-n'></label>"
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
}
//删除
function deleteFunction(url,data){
    var result;
    var shuju = [];
    if(data==undefined){
        result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
            for(var i = 0; i < result.length; i++) {
                var item = result[i];
                // $('#table>tbody>tr').remove('.selected');
                shuju.push(item.messageNo);
            }
    }else {
        shuju.push(data);
    }

    $.ajax({
        url: url,
        type: 'POST',
        data:{messageNos:shuju},
        success: function (data) {
            // console.log(data);
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
        {field: 'policyCount',title: '上报策略下发成功<br\/>数/下发异常数',formatter:function (value, row, index) {
            if(value=="0/0"){
                return "0/0";
            }else {
                return "<a href='#' onclick='getDetail("+index+",0)' title='详情'>"+value+"</a>";
            }
        }},
        {field: 'bindPolicyCount',title: '绑定策略下发成功<br\/>数/下发异常数',formatter:function (value, row, index){
            if(value=="0/0"){
                return "0/0";
            }else {
                return "<a href='#' onclick='getDetail("+index+",1)' title='详情'>"+value+"</a>";
            }
        }},
        {field: 'startTime',title: '上报开始时间'},
        {field: 'endTime',title: '上报结束时间'},
        {field: 'createTime',title: '创建时间'},
        {field: 'modifyOper',title: '操作账号'},
        {field: 'operating',title:'操作',formatter:operatingFormatter,events:operatingevent }
    ];
    if(packetType == 1 && (packetSubtype ==0 || packetSubtype==5)){
        column.splice(3,0,{field: 'packetSubtype',title: '资源来源',formatter: function (value, row, index) {
            if(value == 0) return "资源在网外";
            else if(value == 5) return "资源在网内";
        } });
    }

    if(packetType == 1 && (packetSubtype ==130 || packetSubtype==131)){
        column.splice(3,0,{field: 'packetSubtype',title: '上报类型',formatter: function (value, row, index) {
            if(value == 130) return "一拖N监测结果";
            else if(value == 131) return "关键字段";
        } });
    }
    //流量流向上报策略
    if(packetType == 1 && packetSubtype ==196){
    	column.splice(3,0,{field: 'areaGroupMessageName',title: '流量流向策略名称' });
    	column.splice(4,0,{field: 'areaGroupMessageNo',title: '流量流向策略ID', visible:false});
    	column.splice(7,0,{field: 'bindFlowPolicyCount',title: '流量流向绑定策略下发成功<br\/>数/下发异常数',formatter:function (value, row, index){
            if(value==""  || value==undefined){
                return "0/0";
            }else {
                return "<a href='#' onclick='getDetail("+index+",2)' title='详情'>"+value+"</a>";
            }
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
    var format=""
        +"<a href='#' title='重发' class='m-r'><i class='fa  fal fa-share resend'></i></a>"
        +"<a href='#' title='编辑' class='m-r'><i class='fa fa-edit fa-lg edit'></i></a>"
        +"<a href='#' title='删除'><i class='fa fa-close fa-lg delete'></i></a>"
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

        $('#myModaladd').find("h4").text("修改");
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
            console.log("---areaGroupMessageNo = "+row.areaGroupMessageNo);
            $('#areaGroupMessageNo').val(row.areaGroupMessageNo);
            $('#lastAreaGroupMessageNo').val(row.areaGroupMessageNo);
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
                deleteFunction('/upload/delete.do',row.messageNo);
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
    var array = ["即时上报","每5分钟上报(整点时间)","每1小时上报(整点时间)","每1小时上报(整点时间)","每3小时上报(整点时间)","每天上报","每30秒上报",
    "每1分钟上报","每3分钟上报"];
    var desc = array[id];
    if (desc==undefined){
        desc ="-"
    }
    return descnh;
}
function getDetail(index,tab){
    var flow = $("#table").bootstrapTable('getData');
    var packetType = flow[index].packetType;
    var packetSubtype = flow[index].packetSubtype;
    if(packetType == 1 && packetSubtype == 196){
    	PolicyDetail.showDetail(flow[index].messageNo,messageType.FLOW_UPLOAD_POLICY.value,3,"流量上报策略",flow[index].areaGroupMessageNo,1,tab);
    }else{
    	PolicyDetail.showDetail(flow[index].messageNo,messageType.FLOW_UPLOAD_POLICY.value,2,"流量上报策略",1,tab);
    }
}

*/


var ddosManage = {
    //流量流向策略的保存
    saveDdosBtn: function () {
        $("#saveDdosBtn").click(function () {
            var formData = $('#ddosAddOrupdateForm').formToJSON();
            formData.messageName=formData.ddosMessageName;
            // debugger;
            var url = '/apppolicy/ddosflow/add';
            // 检查所有参数
            if (ddosManage.checkAllParameter(formData)) {

                ddosManage.filterParameter(formData);
                $.ajax({
                    url: url,
                    type: 'POST',
                    dataType: 'json',
                    // data:JSON.stringify(formData),
                    data: formData,
                    success: function (data) {
                        if (data.result == 0) {
                            $("#messageNo").val('');
                            ddosManage.cleanAllInput();
                            //加载ddos异常流量管理策略
                            loadSel('ddosManageMessageNo','/select/getDdosManagePolicy',true);
                            $('#ddosManageModal').modal('hide');
                        } else {
                            swal("操作失败", "存在策略名称相同数据", "error");
                        }
                    },
                    error: function () {
                        swal("操作失败", "取消新增/删除操作！", "error");
                    }

                })
            }

        });
        $('#ddosAddOrupdateForm .circle-picture').on('click', function () {

            var $last = $('#ddosAddOrupdateForm').find('div[name="attackTypeInsert"]').last();
            var $clone = $last.clone();
            $clone.addClass("newType")
            $clone.find('input[name="attackThreshold"]').val('');
            $clone.find('input[name="attackControl"]').val('');
            $clone.addClass("newInput").find('a.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle').end();
            $last.after($clone);
            $('#ddosAddOrupdateForm a.fa-minus-circle').on('click', function (e) {
                e.preventDefault();
                $(this).parents('div[name="attackTypeInsert"]').remove();
            });
        });
    },
    /*******流量流向策略 保存结束**************/

    // 参数合法性校验
    checkAllParameter: function (formData) {
        if (formData.ddosMessageName == undefined || formData.ddosMessageName == "") {
            swal("请输入策略名称");
            return false;
        } else if (formData.ddosMessageName.length > 50) {
            customWarn('messageNameMsg', '策略名称不能超过50个字符', 'col-md-4');
            return false;
        }
        if (formData.appAttackType == undefined || formData.appAttackType == "") {
            swal("请选择攻击类型");
            return false;
        }
        if (formData.attackThreshold == undefined || formData.attackThreshold == "") {
            swal("请输入攻击阈值");
            return false;
        }
        var atArray = formData.attackThreshold.split(",");
        for (var i in atArray) {
            if (!isNumber(atArray[i])) {
                swal("攻击阈值请输入整数");
                return false;
            }
        }
        if (formData.attackControl == undefined || formData.attackControl == "") {
            swal("请输入控制阈值");
            return false;
        }
        var acArray = formData.attackControl.split(",");
        for (var i in acArray) {
            if (!isNumber(acArray[i])) {
                swal("控制阈值请输入整数");
                return false;
            }
        }
        if (parseInt(formData.attackControl) > parseInt(formData.attackThreshold)) {
            swal("控制阈值不能大于攻击阈值");
            return false;
        }
        return true;
    },
    // 保存前对参数进行过滤  去掉为设值的参数
    filterParameter: function (voipFlow) {

        if (voipFlow.messageNo == '') {
            voipFlow.messageNo = undefined;
        }
    },
    // 设置参数值
    cleanAllInput: function () {
        $('#messageNo').val('');
        $('#ddosAddOrupdateForm').find('#ddosMessageName').val('');
        $('#ddosAddOrupdateForm').find('select[name="appAttackType"]').val("");
        $(".newInput").remove();
        $(".newType").remove();
        $("#ddosAddOrupdateForm").find('input[name="attackThreshold"]').val("");
        $("#ddosAddOrupdateForm").find('input[name="attackControl"]').val("");
    }
}

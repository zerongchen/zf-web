var TreeObject = {
	initTree:function (ztreeId,type,customId,customPid,customName,dataUrl) {
        var setting = {
            view: {
                expandSpeed: "", //zTree 节点展开、折叠时的动画速度，设置方法同 JQuery 动画效果中 speed 参数。
                selectedMulti: false // 禁止多点同时选中的功能
            },
            check: {
                enable: true
            },
            data: {
                key : {
                    name : customName
                },
                simpleData: {
                    enable: true,
                    idKey: customId,
                    pIdKey: customPid,
                    rootPId: '0'
                }
            }
        };
        $.ajax({
            url: dataUrl,
            type: 'POST',
            data:{areaType:type},
            async: false,
            dataType: 'json',
            success: function(data){
                zNodes = JSON.stringify(data);
                $.fn.zTree.init($("#"+ztreeId), setting, zNodes);
            },
			error:function () {
                $.fn.zTree.init($("#"+ztreeId), setting, null);
            }
        });
    },
    //获取节点
    getCheckedData:function (ztreeId) {
        var treeObj = $.fn.zTree.getZTreeObj("#"+ztreeId);
        return treeObj.getCheckedNodes();
    },
//初始化节点
	initCheck:function(ztreeId,dataObj){
        if(dataObj instanceof Array){
            var treeObj = $.fn.zTree.getZTreeObj(ztreeId);
            var nodes = treeObj.transformToArray(treeObj.getNodes());
            loop1:for (var n=0;n<nodes.length;n++){
                loop2:for (var i=0;i<dataObj.length;i++){
                    if(nodes[n].areaId == dataObj[i].areaId){
                        treeObj.checkNode(nodes[n], true, true);
                        break loop2;
                    }else {
                        nodes[n].checked = false;
                    }
                }
            }
        }

    }
};

$(document).ready(function() {

    //初始化 ztreeId,type,customId,customPid,customName,dataUrl
    TreeObject.initTree("ztree1",2,"areaId","pareaId","areaName","obtainTree");
    //获取打钩的数组
    TreeObject.getCheckedData("ztree1");

    //初始化打钩选项,数组格式如上
    TreeObject.initCheckOption("ztree1",zNodes);

});

var addRow = 1;
// 记录是新增操作还是修改操作
var add = true;

houseManageJs= {

    // 根据条件查询记录
    searchFormSubBut:function () {
        $("#searchFormButton").on('click', function(){
            initTableJs.refreshData();
        })
    },

    addButton:function () {
        $("#add").on('click',function(){
            $(".has-error").remove();
            add = true;

            $('#myModaladd').find("h5").text("新增");

            // $('#myModaladd').modal('show');
            revertAddHouseDiv();
            //新增时显示后面的+号
            $('#idcHouseAdd').parent().parent().show();

            $('#addOrupdateForm').find('input[name="houseId"]').val('').attr('readOnly',false);
            $('#addOrupdateForm').find('input[name="houseName"]').val('');
        });
    },

    addMoreHouseButton:function () {
        $("#idcHouseAdd").on('click',function () {
            var houseId = 'houseId_'+addRow;
            var houseName = 'houseName_'+addRow;
            var addedHouseDiv = '<div class="form-group" >';
            addedHouseDiv +='       <label class="col-md-2 control-label p-n">机房编号 : 机房名称  </label>';
            addedHouseDiv +='       <div class="col-md-2">';
            addedHouseDiv +='           <input type="text" id =' + houseId;
            addedHouseDiv +='                              name="houseId" class="form-control"/>';
            addedHouseDiv +='       </div>';
            addedHouseDiv +='       <div class="col-md-3">';
            addedHouseDiv +='           <input type="text" id =' + houseName;
            addedHouseDiv +='                              name="houseName" class="form-control"/>';
            addedHouseDiv +='       </div>';
            addedHouseDiv +='       <div class="col-md-1">';
            addedHouseDiv +='           <span class="help-block m-b-none tips">';
            addedHouseDiv +='               <a class="m-l-n m-r-sm check-minus"><i class="fa fa-minus" aria-hidden="true"></i></a>';
            addedHouseDiv +='           </span>';
            addedHouseDiv +='       </div>';
            addedHouseDiv +='   </div>';
            $("#addOrupdateForm").append(addedHouseDiv);
            addRow += 1;
        })
    },

    minusHouseButton:function () {
        $(document).on('click',".check-minus",function () {
            // $(this).parent().parent().parent().remove();
            $("#addOrupdateForm .form-group:last").remove();
            addRow -= 1;
        })
    },

    //弹出框新增或者修改
    addOrUpdateData:function(){

        $('#addSubmitBut').on('click', function() {
            $(".has-error").remove();

            var idcHouses = [];
            var idcHouseDiv = $("#addOrupdateForm").find('.form-group');
            for (var i=0;i<idcHouseDiv.length;i++){
                var emptyId = checkIdParameter($("#houseId_"+i).val());
                if (!emptyId){
                    // swal("机房编号只能包括字母数字以及特殊字符_ - .");
                    // warn("houseId_"+i,"机房编号只能包括字母数字以及特殊字符_ - .");
                    $("#houseId_"+i).parent().last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'字母数字以及特殊字符_ - .组成'+'</span></div>');
                    $("#houseId_"+i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return;
                }
                var emptyName = checkParameter($("#houseName_"+i).val());
                if ( !emptyName){
                    // swal("机房名称不能为空！");
                    $("#houseName_"+i).parent().last().after(
                        '<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+'机房名称不能为空！'+'</span></div>');
                    $("#houseName_"+i).attr('onfocus', 'clearWarn($(\'#addOrupdateForm\'))');
                    return;
                }
                idcHouses[i] = {"houseId":$("#houseId_"+i).val(),"houseName":$("#houseName_"+i).val()};
            }

            //检查提交的内容是否有重名
            var result = renameCheck(idcHouses);
            if ( !result ){
                swal("记录id或name不能相同");
                return;
            }

            var url = "";
            if (add){
                url = "/device/housemanage/add.do";
            } else {
                url = "/device/housemanage/update.do";
            }

            $.ajax({
                url: url,
                type: 'POST',
                data:{"idcHouses":JSON.stringify(idcHouses)},
                success: function (data) {
                    if (data.result == 0){
                        $('#myModaladd').modal('hide');
                        //再次打开新增弹出框时去掉刚才增加的行
                        // revertAddHouseDiv();
                        if(add){
                            swal("新增记录成功！");
                        } else {
                            swal("修改记录成功！");
                        }

                        initTableJs.refreshData();
                    } else {
                        // revertAddHouseDiv();
                        swal("操作失败", data.message, "error");
                    }

                },
                error:function () {
                    //再次打开新增弹出框时去掉刚才增加的行
                    // revertAddHouseDiv();
                    swal("操作失败", "取消新增/删除操作！", "error");

                }

            })

        })
    },

    initSelector : function(){
        $('#operate').on("change",function(){
            clearWarn($('#operateDiv'));
        })
        $('#userGroupType').on("change",function(){
            clearWarn($('#usertypeDiv'));
        })
    } ,


    init:function(){
        houseManageJs.initSelector();
        houseManageJs.searchFormSubBut();
        houseManageJs.addOrUpdateData();
        houseManageJs.addMoreHouseButton();
        houseManageJs.minusHouseButton();
        houseManageJs.addButton();
    }
}

//有name或id相同 返回false 无重名返回true
function renameCheck(idcHouse){
    // debugger;
    if (idcHouse.length==1){
        return true;
    } else if (idcHouse.length==2){
        return !(idcHouse[0].houseId==idcHouse[1].houseId||idcHouse[0].houseName==idcHouse[1].houseName);
    } else {
        for (var i=0;i<idcHouse.length;i++){
            for (var j=1;j<idcHouse.length;j++){
                if (idcHouse[i].houseId==idcHouse[j].houseId||idcHouse[i].houseName==idcHouse[j].houseName){
                    return false;
                }
            }
        }
        return true;
    }

}

// ID非空检查
function checkIdParameter(s){
    var regu =/^[._\-a-zA-Z0-9]{1,300}$/g;
    var re = new RegExp(regu);
    if (re.test(s)) {
        return true;
    }else{
        return false;
    }
    // if (s==undefined || s=='' || s.length==0 ) {
    //     return false;
    // }else{
    //     return true;
    // }
}

// 机房名称非空检查
function checkParameter(s){
    if (s==undefined || s=='' || s.length==0 ) {
        return false;
    }else{
        return true;
    }
}

$(document).ready(function() {
    initTableJs.initParam('/device/housemanage/list.do','/device/housemanage/delete.do',getColumnFunction)
    initTableJs.init();
    houseManageJs.init();
});

/*function addMoreHouseButton() {
 var addedHouseDiv = '<div class="form-group" >';
 addedHouseDiv +='       <label class="col-md-2 control-label p-n">机房编号 : 机房名称  </label>';
 addedHouseDiv +='       <div class="col-md-2">';
 addedHouseDiv +='           <input type="text" name="houseId" class="form-control"/>';
 addedHouseDiv +='       </div>';
 addedHouseDiv +='       <div class="col-md-3">';
 addedHouseDiv +='           <input type="text" name="houseName" class="form-control"/>';
 addedHouseDiv +='       </div>';
 addedHouseDiv +='       <div class="col-md-1">';
 addedHouseDiv +='           <span class="help-block m-b-none tips">';
 addedHouseDiv +='               <a class="m-l-n m-r-sm"><i class="fa fa-minus check-minus" id="idcHouseMinus" aria-hidden="true"></i></a>';
 addedHouseDiv +='           </span>';
 addedHouseDiv +='       </div>';
 addedHouseDiv +='   </div>';
 $("#idcHouseAdd").on('click',function () {
 $(this).parent().parent().parent().parent().after(addedHouseDiv);
 })
 }*/

/*function minusHouseButton() {
 $("#idcHouseMinus").on('click',function () {
 console.log("==========");
 $(this).parent().parent().parent().remove();
 })
 }*/

function revertAddHouseDiv(){
    // 还原新增条原始值
    addRow = 1;
    $(".check-minus").parent().parent().parent().remove();
}

function getColumnFunction(){

    var column =[
        {field: 'state',checkbox: true},
        {field: 'houseId',title: '机房编号',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'houseName',title: '机房名称',formatter:function(value,row,index){
			return "<span title='"+value+"'>"+value+"</span>";
		}},
        {field: 'operating',title:'操作',formatter:operatingFormatter, events:operatingEvents }
    ]
    return column;
}

function newAddButtonFunction(){
    $(".has-error").remove();
    add = true;

    $('#myModaladd').find("h5").text("新增");

    // $('#myModaladd').modal('show');
    revertAddHouseDiv();
    //新增时显示后面的+号
    $('#idcHouseAdd').parent().parent().show();

    $('#addOrupdateForm').find('input[name="houseId"]').val('').attr('readOnly',false);
    $('#addOrupdateForm').find('input[name="houseName"]').val('');



}

function deleteFunction(url,houseId){

    var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
    var houseIds = new Array();
    for(var i = 0; i < result.length; i++) {
        var item = result[i];
        houseIds.push(item.houseId);
    }
    if (houseIds.length==0){
        houseIds.push(houseId);
    }
    $.ajax({
        url: url,
        type: 'POST',
        data:{"houseIds":houseIds},
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

function modifyButtonFunction(row) {
    $(".has-error").remove();
    add = false;


    $('#myModaladd').find("h5").text("修改");
    $('#myModaladd').modal('show');
    //修改时隐藏后面的+号
    $('#idcHouseAdd').parent().parent().hide();

    revertAddHouseDiv();

    $('#addOrupdateForm').find('input[name="houseId"]').val(row.houseId).attr("readOnly",true);
    $('#addOrupdateForm').find('input[name="houseName"]').val(row.houseName);

}


function queryParams(params){
    // var data = $('#searchForm').formToJSON();
    var data = new Object();
    data.houseName = $('#houseName').val();
    if(data.houseName ==''){
        data.houseName = undefined;
    }
    return data;
}

//样式格式化
function operatingFormatter(value, row, index){
	var deleteFlag = $("#deleteFlag").val();
	var modify = $("#modifyFlag").val();
	var format="";
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

    'click .edit': function (e, value, row, index) {
        e.preventDefault();
        $('#myModaladd').find("h5").text("修改");
        $('#addSubmitBut').show();
        $('#addOrupdateForm').find('input[name="houseId"]').attr("readOnly",true);
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
                deleteFunction('/device/housemanage/delete.do',row.houseId);
            } else {
                swal("已取消", "取消了删除操作！", "error")
            }
        })
    }
}
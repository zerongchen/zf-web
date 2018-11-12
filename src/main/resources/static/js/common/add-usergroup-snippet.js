var fileTypes=['xlsx','xls'];

addUserGroupJs = {
    createUserGroup:function(){
        $('#createUserGroup').on('click', function() {
            var formData = $('#createUserGroupForm').formToJSON();
            formData.operate = 0;
            var usergroup =formData.userGroupName;
            if(usergroup==''){
                clearWarn($('#namegroupDiv'));
                $('#namegroupDiv').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请输入用户组名</span></div>');
                $('#namegroupDiv').find('input[name="userGroupName"]').attr('onfocus','clearWarn($(\'#namegroupDiv\'))');
                return false;
            }else if (usergroup.length>50){
                clearWarn($('#namegroupDiv'));
                $('#namegroupDiv').children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>最多支持输入50个字符</span></div>');
                $('#namegroupDiv').find('input[name="userGroupName"]').attr('onfocus','clearWarn($(\'#namegroupDiv\'))');
                return false;
            }

            var fileName=$('#createUserGroupForm').find('input[name="importFile"]');    //得到name为file的控件
            var arrId=new Array();                                //定义一个ID数组

            if($("input[type='file']").val()==""){
                swal("请选择文件");
                return false;
            }
            var isTypeError =false;
            var fileArray = [];
            var isFileRepeat  = false;
            for(var i =0;i<fileName.length;i++ ){
                var filePath = fileName[i].value;
                var	suffix = filePath.substring(filePath.lastIndexOf('.') + 1,filePath.length);
                if($.inArray(suffix,fileTypes) == -1){
                    isTypeError=true;
                    break;
                }
                if(fileArray.indexOf(filePath)>-1){
                    isFileRepeat = true;
                }else {
                    fileArray.push(filePath);
                }
            }
            if(isTypeError){
                swal({
                    title: "<small>文件类型错误,或者为空</small>",
                    text: " 仅支持 .xls .xlsx 文件",
                    html: true
                })
                return false;
            }

            if(isFileRepeat){
                swal({
                    title: "<small>文件名重复,相同的数据只会保留一份</small>",
                    text: " 导入继续 ... ",
                    html: true
                },function (isConfirm) {
                    if(isConfirm){
                        upFile();
                    }
                })
            }else{
                upFile();
            }

            function upFile(){
                for(var j=0;j<fileName.length;j++){
                    if(fileName[j]!=""){                            //当file控件里的值不为空时就往ID数组里塞值
                        arrId[j]=fileName[j].id;
                    }
                }
                $.ajaxFileUpload({
                    url: '/userGroupManager/import.do',
                    secureuri: false,
                    data: formData,
                    fileElementId: arrId,
                    dataType: "json",
                    success: function (results) {
                        var failLog = '';
                        var result = '';
                        var title = '';
                        var data = results.data;
                        if(data!=null && data.length>0){
                            for(var i = 0 ; i<data.length; i++){
                                if(data[i].result>=1){

                                    title = '<span class="text-danger">操作失败</span>';
                                    failLog +='文件 '+data[i].fileName+' 导入失败,信息如下\r\n'
                                    if(data[i].describtion !=null){
                                        failLog += data[i].describtion+'\r\n';
                                    }
                                    if(data[i].dataErrorInfoList!=null && data[i].dataErrorInfoList.length>0){
                                        for(var j = 0;j<data[i].dataErrorInfoList.length;j++){
                                            var array = data[i].dataErrorInfoList[j];
                                            failLog +='第 '+array.sheet+' 个表, '+array.row+' 行, '+ array.column+' 列'+array.errorType+'\r\n'
                                        }
                                    }
                                    failLog = encodeURI(failLog);
                                    result = '<a href="#" onclick= \"saveTxt(\''+failLog+'\',\'用户组新增详情\')\";return false>点击下载详情</a>';
                                }else{
                                    title = '操作成功';

                                    result = '';
                                }
                            }
                            swal({
                                title: title,
                                text: result,
                                html: true
                            }, function(isConfirm) {
                                $('#add-usergroup-snippet').modal('hide');
                                //刷新下拉框内容
                                if(typeof refreshData ==="function"){
                                    refreshData();
                                }
                                //调用主页自定义函数，有就执行,没有忽略
                                if(typeof customFunction ==="function"){
                                    customFunction();
                                }
                            });
                        }else{
                            swal("操作失败");
                        }
                    },
                    error:function(){
                        swal("操作失败");
                    }

                })
            }
        })
    },
    exportTemplate:function(){

        $('#exportTemplate').click(function () {
            $('#exportTem').remove();
            var form = '<form class="hide" id="exportTem">';
            form += '</form>';
            $('body').append(form);

            $('#exportTem').attr('action', '/userGroupManager/exportTemplate.do').attr('method', 'get').submit() ;return false;
        })

    }

}
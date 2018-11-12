var errorTime = 0;

loginJs = {
    initClick : function () {
        $("#flushBtn").on('click',function () {

            $("#validateImg").attr("src",'/code?ran='+Math.random())
        });
        
        $("#loginForm").find('input').on('focus',function () {
            $("#errorCode").remove();
            // $("errorCode2").html("");
        })

        // $("#submitBtn").on('click',function () {
        //     debugger;
        //     var data = $("#loginForm").formToJSON();
        //     $.ajax({
        //
        //         url: "/dealLogin",
        //         type: 'POST',
        //         data: {"username":data.username,"password":data.password,"verificationCode":data.verificationCode},
        //         success: function (msg) {
        //             // errorTime = 0;
        //             // warn("username","用户名或者密码错误");
        //         },
        //         error:function () {
        //             // errorTime++;
        //         }
        //     })
        // })
    },


    init :function () {
        loginJs.initClick();
    }
}


$(document).ready(function() {
    loginJs.init();
});

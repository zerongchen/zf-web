
var deleteUrl = "";
var tableURL = "";
var column=[];
initTableJs= {

    initParam:function(initTableURL,initDeleteURL,getColumnFunction){
        tableURL=initTableURL;
        deleteUrl = initDeleteURL;
        column = getColumnFunction();
    },
    refreshData:function(){
        $("#table").bootstrapTable('refresh', { url: tableURL});
    },
    initNewAddButton:function(){
        $('.newAddButton').on('click', function() {
            $('#myModaladd').find("h4").text("新增");
            //新的新增页面标题
            $('#title').text("新增");
            newAddButtonFunction();
        })
    },
    initModifyButton:function(){
        $('.modifyButton').on('click', function() {
            $('#myModaladd').find("h4").text("修改");
            modifyButtonFunction();
        })
    },
    initdetailButton:function(){
        $('.detailButton').on('click', function() {
            detailButtonFunction();
        })
    },
    initBoostrapTable: function(){
        $('#table').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: tableURL,
            queryParams: queryParams,
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            showColumns: !0,
            toolbar: "#commonButton",
            pagination: true,
            sidePagination: 'client', //分页方式：client客户端分页，server服务端分页（*）
            iconSize: "outline",
            icons: {
                columns: "glyphicon-list",
            },
            clickToSelect:false,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: column
        });
    },
    initBoostrapTable2: function(){
        $('#table').bootstrapTable('destroy').bootstrapTable({
            method: 'post',
            url: tableURL,
            queryParams: queryParams,
            contentType: 'application/x-www-form-urlencoded',
            striped: true,
            undefinedText: '',
            showColumns: !0,
            toolbar: "#commonButton",
            pagination: true,
            sidePagination: 'server', //分页方式：client客户端分页，server服务端分页（*）
            iconSize: "outline",
            icons: {
                columns: "glyphicon-list",
            },
            clickToSelect:false,
            pageSize: 10,
            pageList: [10, 25, 50, 100, 200],
            columns: column
        });
    },

    initdeleteButton:function(){

        $(".deleteButton").click(function() {
            var result = $('#table').bootstrapTable('getSelections'); //获取表格的所有内容行
            if(result.length<1){
                swal("请选择至少一条数据");
                return false;
            }
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
                   deleteFunction(deleteUrl);
                } else {
                    swal("已取消", "取消了删除操作！", "error")
                }
            })
        })
    },
    init:function(){
        initTableJs.initBoostrapTable();
        initTableJs.initdeleteButton();
        initTableJs.initNewAddButton();
        initTableJs.initModifyButton();
        initTableJs.initdetailButton();
    },
    init2:function(){
        initTableJs.initBoostrapTable2();
        initTableJs.initdeleteButton();
        initTableJs.initNewAddButton();
        initTableJs.initModifyButton();
        initTableJs.initdetailButton();
    }



}

function checkIP(value){
    var exp=/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
    var reg = value.match(exp);
    if(reg==null)
    {
        return false;
    }
    return true;
}
function checkPort(value) {
    var exp = /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/;
    var reg = value.match(exp);
    if(reg==null)
    {
        return false;
    }
    return true;
}

function checkMac(address) {
    var c = '';
    var i = 0, j = 0;

    if ((address.toLowerCase() == 'ff:ff:ff:ff:ff:ff') || (address.toLowerCase() == '00:00:00:00:00:00')) {
        return false;
    }

    var addrParts = address.split(':');
    if (addrParts.length != 6) {
        return false;
    }
    for (i = 0; i < 6; i++){
        if (addrParts[i] == ''){
            return false;
        }
        if (addrParts[i].length != 2) {
            return false;
        }
        for (j = 0; j < addrParts[i].length; j++) {
            c = addrParts[i].toLowerCase().charAt(j);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <='f')) {
                continue;
            } else {
                return false;
            }
        }
    }
    if ((parseInt(addrParts[0], 16) % 2) == 1) {
        return false;
    }

    return true;
}

function saveTxt(data,fileName) {
    saveAs( new Blob([decodeURI(data)], {type: "text/plain;charset=utf-8"}), fileName);
}
//域名校验
function checkSite(domain) {

    var RegUrl = new RegExp();
    var valueLength = 0;
    if(domain.indexOf(".")==-1){
        return '不符合网站格式';
    }
    if("."==domain.substring(domain.length-1,domain.length)){
        return "不符合网站格式";
    }
    if(domain.startsWith(".")){
        return "不符合网站格式";
    }

    var reg = "^\\d{0,}$";
    var str = domain.substring(domain.lastIndexOf(".")+1,domain.length);
    RegUrl.compile(reg);
    if (RegUrl.test(str)) {
        return "顶级域名不符合规范";
    }
    if(domain.indexOf("*.")==0){
        domain= domain.substring(domain.indexOf("*.")+2,domain.length);
    }
    var domins = domain.split(".");
    for (var j =0 ;j< domins.length;j++) {
        var eachdomain = domins[j];

        //可以包含中文、字母a-z（大小写等价）、数字0-9或者半角的连接符"-"，"-"不能放在开头或结尾
        reg = "^(?!-.)(?!.*?-$)[-a-zA-Z0-9\\u4e00-\\u9fa5]*$";

        var m = eachdomain.match(reg);

        if (m==null) {
            return "不符合域名注册规定";
        }

        str = m[0];

        reg = "^[\\u4e00-\\u9fa5]+$";//纯汉字必须大于1位
        var b = str.match(reg);

        if (b!=null) {
            var  chinese = b[0];
            if (chinese.length < 2 || chinese.length > 20) {
                return "纯中文必须大于1个小于21个";
            }
        } else {
            //判断punycode长度
            if(str.length >= 3) {
                //如果第一位、二位不是中文，就判断第三、四位是否是“-”
                var str1 = str.substring(0, 3);
                var reg1 = "^[-a-zA-Z0-9]*$";
                var r = str1.match(reg1);

                if (r!=null) {
                    if (str.indexOf("-") == 2 || str.indexOf("-") == 3) {
                        return " “-”符号不能出现在第三和第四位 ";
                    }
                }
                //判断输入的域名是否超长
                var chineseReg = "[\u4e00-\u9fa5]";
                RegUrl.compile(chineseReg);
                for (var i = 0; i < str.length; i++) {
                    var temp = str.substring(i, i + 1);
                    if (RegUrl.test(temp)) {
                        valueLength += 2;
                    } else {
                        valueLength++;
                    }
                }
            }
        }
    }
    if (valueLength > 63) {
        return "您输入的域名长度大于63位";
    }
    return "";
}

function isURL(str_url) {// 验证url
    var strRegex = "^((https|http|ftp|rtsp|mms)?://)"
        + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
        + "(([0-9]{1,3}\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
        + "|" // 允许IP和DOMAIN（域名）
        + "([0-9a-z_!~*'()-]+\.)*" // 域名- www.
        + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\." // 二级域名
        + "[a-z]{2,6})" // first level domain- .com or .museum
        + "(:[0-9]{1,4})?" // 端口- :80
        + "((/?)|" // a slash isn't required if there is no file name
        + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
    var re = new RegExp(strRegex);
    return re.test(str_url);

}
function checkIpv6(value) {
    var matchStr = "((([0-9a-f]{1,4}:){7}([0-9a-f]{1,4}|:))|(([0-9a-f]{1,4}:){6}(:[0-9a-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9a-f]{1,4}:){5}(((:[0-9a-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9a-f]{1,4}:){4}(((:[0-9a-f]{1,4}){1,3})|((:[0-9a-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9a-f]{1,4}:){3}(((:[0-9a-f]{1,4}){1,4})|((:[0-9a-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9a-f]{1,4}:){2}(((:[0-9a-f]{1,4}){1,5})|((:[0-9a-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9a-f]{1,4}:){1}(((:[0-9a-f]{1,4}){1,6})|((:[0-9a-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9a-f]{1,4}){1,7})|((:[0-9a-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$";
    var ret = value.match(matchStr);
    if (ret) {
        return true;
    } else {
        return false;
    }
}
function checkIpv4Prefix(value) {
    var matchStr = /^\+?[1-9][0-9]*$/;
    var ret = value.match(matchStr);
    if(ret){
        if (value <=32) {return true;}
    }
    return false
}
//非0正整数
function isNumber(value) {
    var matchStr = /^\+?[1-9][0-9]*$/;
    var ret = value.match(matchStr);
    if(ret){
       return true;
    }
    return false
}
//包括0的正整数
function isNmber0(value) {
	var matchStr = /^(0|[1-9]\d*)$/;
	var ret = value.match(matchStr);
    if(ret){
       return true;
    }
    return false
}
function checkIpv6Prefix(value) {
    var matchStr = /^\+?[1-9][0-9]*$/;
    var ret = value.match(matchStr);
    if(ret){
        if (value <=64) {return true;}
    }
    return false
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

function timeBarInit(){
	//时间条的点击事件
	for(var i = 0; i < $(".timeul>li").length - 2; i++) {
		$(".timeul>li>input").eq(i).click(function() {
			if($(this).hasClass('active')) {
				$(this).attr('class', '');

			} else {
				$(this).attr('class', 'active');
			}
		})
	}
	//时间条的全选
	$(".checkall").click(function() {
		if($(".checkall").hasClass('fa-check-square-o')) {
			$(".timeul>li>input[type='button']").attr('class', '');
			$(this).removeClass('fa-check-square-o');
			$(this).addClass('fa-square-o');
		} else {
			$(".timeul>li>input[type='button']").attr('class', 'active');
  	$(this).removeClass('fa-square-o');
			$(this).addClass('fa-check-square-o');
		}
	});

}

/**
 * 时间条初始化
 */
function initTimeBar(section) {
    var bars = $('#'+section).find('input[type="button"]');
    for (var i =0;i<bars.length;i++){
        if(bars.eq(i).hasClass('active')){
            bars.eq(i).removeClass('active')
        }
    }
    $('.checkall').removeClass('fa-check-square-o').addClass('fa-square-o');
}

/**
 * 获取时间条的值
 */
function getTimeBarValue(formId) {
    var cTimeObj = $("#"+formId+" .timeul>li>input");
    var cTimeStr="";
    var obj = new Object();
    for(var i = 23; i >=0; i--) {
        if(cTimeObj.eq(i).attr("class")!=undefined && cTimeObj.eq(i).attr("class").indexOf("active")>-1){
            cTimeStr+="1";
        }else {
            cTimeStr+="0";
        }
    }
    obj.value = cTimeStr;
    var array = new Array();
    cTimeStr+="0";
    while (cTimeStr.indexOf("1")>-1){
        cTimeStr = cTimeStr.substring(cTimeStr.indexOf("1"),cTimeStr.length);
        array.push(cTimeStr.substring(0,cTimeStr.indexOf("0")));
        cTimeStr = cTimeStr.substring(cTimeStr.indexOf("0"),cTimeStr.length);
    }
    obj.size = array.length;
    return obj;
}
//针对input框 section只有单个input
function warn(section,warning) {
    clearWarn($('#'+section));
    $('#'+section).children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+warning+'</span></div>');
    $('#'+section).find('input').attr('onfocus', 'clearWarn($(\'#'+section+'\'))');
    return false;
}

function customWarn(section,warning,clazz) {
    clearWarn($('#'+section));
    $('#'+section).children('.'+clazz).last().after('<div class="'+clazz+' has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>'+warning+'</span></div>');
    $('#'+section).find('input').attr('onfocus', 'clearWarn($(\'#'+section+'\'))');
    return false;
}

function warnSelect(section) {
    $('#'+section).children('.col-md-3').last().after('<div class="col-md-3 has-error"><span class="help-block m-b-none"> <i class="fa fa-info-circle"></i>请选择</span></div>');
    return false;
}
function clearWarnSelect(section) {
    clearWarn($('#'+section));
}

//设置时间轴时间
function setTimeBar(timeBar){
	var j = 0;
	var check = 0;
	for(var i=timeBar.length-1;i>=0;i--){
		if(timeBar[i]=='1'){
			$(".timeul").find("li:eq("+j+")").find("input").attr('class', 'active');
			check++;
		}
		j++;
	}
	if(check==24){
		$(".checkall").removeClass('fa-square-o');
		$(".checkall").addClass('fa-check-square-o');
	}else{
		$(".checkall").removeClass('fa-check-square-o');
		$(".checkall").addClass('fa-square-o'); 
	}
}

//ipv4转换成整数
function ip2int(ip) 
{
    var num = 0;
    ip = ip.split(".");
    num = Number(ip[0]) * 256 * 256 * 256 + Number(ip[1]) * 256 * 256 + Number(ip[2]) * 256 + Number(ip[3]);
    num = num >>> 0;
    return num;
}

//两个参数，第一个是要格式化的字符串，第二个是以什么分割符将年，月，日连接
function timestamp2Time(timestamp, separator) {
    var result = "";
    if(timestamp) {
        var reg = new RegExp(/\D/, "g"); //提取数字字符串
        var timestamp_str = (''+timestamp+'').replace(reg, "");

        var d = new Date();
        d.setTime(timestamp_str);
        var year = d.getFullYear();
        var month = d.getMonth() + 1;
        var day = d.getDate();
        var hour = d.getHours();
        var minute = d.getMinutes();
        var second = d.getSeconds();
        if(month < 10) {
            month = "0" + month;
        }
        if(day < 10) {
            day = "0" + day;
        }
        if (hour<10){
            hour = "0" + hour  ;
        }
        if (minute<10){
            minute = "0" + minute  ;
        }
        if (second<10){
            second = "0" + second;
        }
        result = year + separator + month + separator + day + " " + hour + ":"+ minute + ":" + second;
    }
    return result;
}

//将时间戳转化为Date类型 separator代表中间分隔符 yyyy-MM-dd
function timestamp2Date(timestamp,separator) {
    var result = "";
    if(timestamp) {
        var reg = new RegExp(/\D/, "g"); //提取数字字符串
        var timestamp_str = (''+timestamp+'').replace(reg, "");

        var d = new Date();
        d.setTime(timestamp_str);
        var year = d.getFullYear();
        var month = d.getMonth() + 1;
        var day = d.getDate();

        if(month < 10) {
            month = "0" + month;
        }
        if(day < 10) {
            day = "0" + day;
        }

        result = year + separator + month + separator + day;
    }
    return result;
}

/**
 * 更多条件查询
 * @returns
 */
function showsearch() {
	if($('.prompt-search').css('display') == 'none') {
		$('.prompt-search').show();
	} else {
		$('.prompt-search').hide();
	}
};
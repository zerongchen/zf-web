$(function(){
    initTooltips();
    inputBlurEvent();

});



//表单元素focus事件(当input/textarea/select/button得到焦点隐藏tooltip)
function inputBlurEvent(){
    $(':input').on('focus', function(){
        $(this).tooltip('hide');
    });
}
function initTooltips(){
    //设置当前页Tooltips选项
    var options = {
        animation: true,
        trigger: 'manual' //手动触发
    };
    $('[data-toggle="tooltips"]').tooltip(options);
}
function clearWarn($secion){
    $secion.children('.has-error').remove();
    $secion.find('.has-error').remove();
}

/**
 *
 * @param datetype 选择日期格式
 * @param flag 统一系统默认时间 或者为空
 */
function initDate(datetype,flag,isTime){

    var startTime = new Date();
    startTime.setDate(startTime.getDate()-1);

    var endTime = new Date();
    endTime.setDate(endTime.getDate());

    var dateFormate;
    var showTime;

        switch (Number(datetype)){
            case 0: dateFormate="YYYY-MM-DD hh:mm:ss"; showTime=isTime; break;
            case 1: dateFormate="YYYY-MM-DD hh:mm"; showTime=isTime; break;
            case 2: dateFormate="YYYY-MM-DD hh"; showTime=isTime; break;
            case 3: dateFormate="YYYY-MM-DD"; showTime=isTime; break;
        }

    if(flag){
        var start = {
            elem: "#start",
            format: dateFormate,
            max: endTime.Format(dateFormate),
            istime: showTime,
            istoday: false,
            choose: function(datas) {
                end.min = datas;
                end.start = datas
            }
        };
        var end = {
            elem: "#end",
            format: dateFormate,
            min:startTime.Format(dateFormate),
            max: "2099-06-16 23:59:59",
            istime: showTime,
            istoday: false,
            choose: function(datas) {
                start.max = datas
            }
        };

        laydate(start);
        laydate(end);
        $('#start').val(startTime.Format(dateFormate));
        $('#end').val(endTime.Format(dateFormate));
    }else {

        var start1 = {
            elem: "#start",
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
            elem: "#end",
            format: dateFormate,
            min:laydate.now(),
            max: "2099-06-16 23:59:59",
            istime: showTime,
            istoday: false,
            choose: function(datas) {
                start1.max = datas
            }
        };
        laydate(start1);
        laydate(end1);
    }
}

/**
 *
 * @param datetype 选择日期格式
 * @param flag 统一系统默认时间 或者为空
 * @Param startEleId endEleId 元素id
 */
function initDefinedEleIdDate(datetype,flag,isTime,startEleId,endEleId){

    var startTime = new Date();
    startTime.setDate(startTime.getDate()-1);

    var endTime = new Date();
    endTime.setDate(endTime.getDate());

    var dateFormat;
    var showTime;
    if(isTime){
        showTime = true;
    }else {
        switch (Number(datetype)){
            case 0: dateFormat="YYYY-MM-DD hh:mm:ss"; showTime=true; break;
            case 1: dateFormat="YYYY-MM-DD hh:mm"; showTime=true; break;
            case 2: dateFormat="YYYY-MM-DD hh"; showTime=true; break;
            case 3: dateFormat="YYYY-MM-DD"; showTime=false; break;
        }
    }

    if (startEleId==undefined||startEleId==''){
        startEleId = "#start";
    } else {
        startEleId = "#"+startEleId;
    }

    if (endEleId==undefined||endEleId==''){
        endEleId = "#end";
    } else {
        endEleId = "#"+endEleId;
    }

    if(flag){

        var start = {
            elem: startEleId,
            format: dateFormat,
            max: endTime.Format(dateFormat),
            istime: showTime,
            istoday: false,
            choose: function(datas) {
                end.min = datas;
                end.start = datas
            }
        };
        var end = {
            elem: endEleId,
            format: dateFormat,
            min:startTime.Format(dateFormat),
            max: "2099-06-16 23:59:59",
            istime: showTime,
            istoday: false,
            choose: function(datas) {
                start.max = datas
            }
        };

        laydate(start);
        laydate(end);
        $(startEleId).val(startTime.Format(dateFormat));
        $(endEleId).val(endTime.Format(dateFormat));
    }else {

        var start1 = {
            elem: startEleId,
            format: dateFormat,
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
            elem: endEleId,
            format: dateFormat,
            // min:laydate.now(),
            max: "2099-06-16 23:59:59",
            istime: showTime,
            istoday: false,
            choose: function(datas) {
                start1.max = datas
            }
        };
        laydate(start1);
        laydate(end1);
    }
}

/**
 *
 * @param datetype 选择日期格式(1H,2D,3W,4M)
 */
function initStateDate(datetype,section,flag){
    let startTime = new Date();
    let dateFormate;
    let showTime;

    switch (Number(datetype)){
        case 1:
            dateFormate="YYYY-MM-DD hh:mm";
            showTime=true;
            startTime.setHours(startTime.getHours()-1);
            break;
        case 2: dateFormate="YYYY-MM-DD hh:mm"; showTime=true;startTime.setDate(startTime.getDate()-1); break;
        case 3: dateFormate="YYYY-MM-DD"; showTime=false; break;
        case 4: dateFormate="YYYY-MM-DD"; showTime=false; break;
    }


    if (flag) $("#"+section).val(startTime.Format(dateFormate));
    laydate.reset();
    laydate({
                elem: "#"+section+"",
                format: dateFormate,
                max: "2099-12-30",
                istime: showTime,
                issure: true, //是否显示确认
                isclear: false, //是否显示清空
                istoday: false,

    });

        return false;
}

/**
 *
 * @param datetype 选择日期格式(1H,2D,3W,4M)
 */
function initStateDateV5(datetype,section,flag){
    let startTime = new Date();
    let value ;

    switch (Number(datetype)){
        case 1:
            replaceDom(section);
            startTime.setHours(startTime.getHours()-1);
            value=startTime.Format("YYYY-MM-DD hh");
            laydate.render({
                elem: "#"+section+"",
                type: 'datetime', //日期时间 可选择：年、月、日、时、分、秒
                format: "yyyy-MM-dd HH", //定义显示样式
                value: value, //获取当前时间
                theme: 'molv', //设置主题颜色
                change: function(value, date, endDate) {
                }
            });

                break;
        case 2:
            replaceDom(section);
            startTime.setDate(startTime.getDate()-1);
            value = startTime.Format("YYYY-MM-DD");
            laydate.render({
                elem: "#"+section+"",
                value: value, //获取当前时间
                theme: 'molv', //设置主题颜色
                change: function(value, date, endDate) {
                    // console.log(value);
                }
            });

            break;
        case 3:
            replaceDom(section);
            startTime.setDate(startTime.getDate()-7);
            value = startTime.Format("YYYY-MM-DD");
            laydate.render({
                elem: "#"+section+"",
                value: value, //获取当前时间
                theme: 'molv', //设置主题颜色
                change: function(value, date, endDate) {
                }
            });
                break;
        case 4:
            replaceDom(section);
            startTime.setMonth(startTime.getMonth()-1);
            value = startTime.Format("YYYY-MM");
            laydate.render({
                elem: "#"+section+"",
                type: 'month', //日期时间 可选择：年、月、日、时、分、秒
                value: value, //获取当前时间
                theme: 'molv', //设置主题颜色
                change: function(value, date, endDate) {
                }
            });
                break;
    }
    $("#"+section).val(value);
    return true;
}
function replaceDom(section) {
    let obj = $("#"+section)[0];
    obj.removeAttribute("lay-key");
    let html =obj.outerHTML;
    let father = $("#"+section).parent();
    $("#"+section).off("onclick").remove();
    father.children().last().after(html);
}

/**
 *
 * @param datetype 选择日期格式(1H,2D,3W,4M)
 */
function initPeriodDateV5(datetype,start,end,flag){

    switch (Number(datetype)){
        case 1:
            replacePeriodDom(start,end);
            let startDate1= laydate.render({
                elem: "#"+start+"",
                type: 'datetime', //日期时间 可选择：年、月、日、时、分、秒
                format: "yyyy-MM-dd HH", //定义显示样式
                theme: 'molv', //设置主题颜色
                done: function (value, dates) {
                    endDate1.config.min ={
                        year:dates.year,
                        month:dates.month-1, //关键
                        date: dates.date,
                        hours: dates.hours,
                        minutes: 0,
                        seconds : 0
                    };
                }
            });
            let endDate1= laydate.render({
                elem: "#"+end+"",
                type: 'datetime', //日期时间 可选择：年、月、日、时、分、秒
                format: "yyyy-MM-dd HH", //定义显示样式
                theme: 'molv', //设置主题颜色
                done: function (value, dates) {
                    startDate1.config.max={
                        year:dates.year,
                        month:dates.month-1,//关键
                        date: dates.date,
                        hours:dates.hours,
                        minutes: 0,
                        seconds : 0
                    }
                }
            });
            break;
        case 2:
            replacePeriodDom(start,end);
            let startDate2 = laydate.render({
                elem: "#"+start+"",
                format: "yyyy-MM-dd", //定义显示样式
                theme: 'molv', //设置主题颜色
                done: function (value, dates) {
                    endDate2.config.min ={
                        year:dates.year,
                        month:dates.month-1, //关键
                        date: dates.date,
                        hours: 0,
                        minutes: 0,
                        seconds : 0
                    };
                }
            });
            let endDate2= laydate.render({
                elem: "#"+end+"",
                format: "yyyy-MM-dd", //定义显示样式
                theme: 'molv', //设置主题颜色
                done: function (value, dates) {
                    startDate2.config.max={
                        year:dates.year,
                        month:dates.month-1,//关键
                        date: dates.date,
                        hours: 0,
                        minutes: 0,
                        seconds : 0
                    }
                }
            });
            break;
        case 3:
            replacePeriodDom(start,end);
            let startDate3 = laydate.render({
                elem: "#"+start+"",
                format: "yyyy-MM-dd", //定义显示样式
                theme: 'molv', //设置主题颜色
                done: function (value, dates) {
                    endDate3.config.min ={
                        year:dates.year,
                        month:dates.month-1, //关键
                        date: dates.date,
                        hours: 0,
                        minutes: 0,
                        seconds : 0
                    };
                }
            });
            let endDate3= laydate.render({
                elem: "#"+end+"",
                format: "yyyy-MM-dd", //定义显示样式
                theme: 'molv', //设置主题颜色
                done: function (value, dates) {
                    startDate3.config.max={
                        year:dates.year,
                        month:dates.month-1,//关键
                        date: dates.date,
                        hours: 0,
                        minutes: 0,
                        seconds : 0
                    }
                }
            });
            break;
        case 4:
            replacePeriodDom(start,end);
            let startDate4 = laydate.render({
                elem: "#"+start+"",
                type: 'month',
                theme: 'molv', //设置主题颜色
                done: function (value, dates) {
                    endDate4.config.min ={
                        year:dates.year,
                        month:dates.month-1, //关键
                        date: dates.date,
                        hours: 0,
                        minutes: 0,
                        seconds : 0
                    };
                }
            });
            let endDate4= laydate.render({
                elem: "#"+end+"",
                type: 'month', //日期时间 可选择：年、月、日、时、分、秒
                theme: 'molv', //设置主题颜色
                done: function (value, dates) {
                    startDate4.config.max={
                        year:dates.year,
                        month:dates.month-1,//关键
                        date: dates.date,
                        hours: 0,
                        minutes: 0,
                        seconds : 0
                    }
                }
            });
            break;
    }
    return false;
}
function replacePeriodDom(start,end) {
    let $start = $("#"+start)[0];
    let $end = $("#"+end)[0];
    $start.removeAttribute("lay-key");
    $end.removeAttribute("lay-key");
    let father = $("#"+start).parent();
    $("#"+start).remove();
    $("#"+end).remove();
    father.children().last().after($start.outerHTML+$end.outerHTML);
}
/**
 * 加载下拉框
 * selId 下拉框id
 * url 请求地址
 * flag 是否包含“请选择”选项
 * val 默认选中项值
 * cus 自定义
 */
function loadSel(selId, url, flag, data,cus,val){
    $.ajax({
        url: url,
        type: 'GET',
        data:data,
        async: false,
        dataType: 'json',
        success: function(data){
            var $selId = $('#' + selId),
                option = '';
            $selId.children().remove();
            if(flag){
                option = '<option value="" >请选择</option>';
            }
            if(cus){
                option += '<option value="0">自定义</option>';
            }
            $.each(data, function(i, n){
                if(val == n.value){
                    option += '<option selected="selected" value="' + n.value + '">' + n.title+ '</option>';
                }else{
                    option += '<option value="' + n.value + '">' + n.title + '</option>';
                }
            });
            $selId.append(option);
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
function loadSelectPicker(selId, url, flag, val){
    $.ajax({
        url: url,
        type: 'GET',
        async: false,
        dataType: 'json',
        success: function(data){
            var data = eval(data);
            var $selId = $('#' + selId),
                option = '';
            $.each(data, function (i,n) {
                option += '<option value="' + n.value + '">' + n.title + '</option>'
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
function initExportClick(section,url,dateType) {
    $('#'+section+' .exportHead >li').off('click');
    $('#'+section+' .exportHead >li').click(function () {
        let data_export_type = $(this).attr(('data-export_type'));

        $('#chartsExport').remove();
        var form = '<form class="hide" id="chartsExport">';
        var dataURLs = '';
        if(allChart!=undefined || allChart!=null){
            $.each(allChart, function(i, n){ //支持多报表导出
                dataURLs += n.getDataURL().split(',')[1] + ",";
            });
        }
        if(dataURLs) dataURLs = dataURLs.substring(0, dataURLs.length - 1);
        var data = $.extend({}, $('#'+section).formToJSON(),cos, {"exportType": data_export_type}, {"dataURLs": dataURLs},{"dateType":dateType});
        $.each(data, function(i, n){
            form += '<input name="' + i + '" value="' + n + '" />';
        });
        form += '</form>';
        $('body').append(form);
        $('#chartsExport').attr('action', url).attr('method', 'post').submit();


    })
    
}


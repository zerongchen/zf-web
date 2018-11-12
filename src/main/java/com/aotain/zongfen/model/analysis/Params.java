package com.aotain.zongfen.model.analysis;

import lombok.Data;

@Data
public class Params {

    //1 城域网 2IDC
    private Integer pageType;

    //源数据类型(左图)
    private String srcUbasDataTypeLeft;
    //源数据类型(左图)
    private String dstUbasDataTypeLeft;

    //源数据类型(右图)
    private String srcUbasDataTypeRight;
    //源数据类型(右图)
    private String dstUbasDataTypeRight;

    private Integer dateType;
    //查询的表,小时，天，周，月
    private String table;
    //省份或者IDC(左图)
    private String srcAreaTypeLeft;
    private String dstAreaTypeLeft;

    //省份或者IDC(右图)
    private String srcAreaTypeRight;
    private String dstAreaTypeRight;

    //对应zf_v2_gen_ip_area AREA_TYPE
    //1: 城域网 省->省 4:城域网 省->运营商 30: IDC省-> IDC 31:IDC运营商->IDC
    private Integer areaType;
    //当前部署环境所在地
    private String currentAreaCode ;


    //目标地方区域（运营商） 只用于城域网
    private String destArea;

    //目标地方区域（运营商） 只用于IDC
    private String srcArea;

    private Integer appType;

    //1趋势图,2统计图
    private Integer type;
    //统计时间
    private String stateTime;
    //查询时间
    private String startTime;
    private String endTime;

    //排序
    private String sort;
    private String order;
}

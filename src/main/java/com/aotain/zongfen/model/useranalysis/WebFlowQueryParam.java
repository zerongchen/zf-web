package com.aotain.zongfen.model.useranalysis;

import lombok.Getter;
import lombok.Setter;

/**
 * 查询参数实体类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/13
 */
@Getter
@Setter
public class WebFlowQueryParam {
    /**范围查询起始时间*/
    private Long startTime;
    /**范围查询结束时间*/
    private Long endTime;

    /** 统计周期 **/
    private Integer statType;
    /** 统计时间 **/
    private Long statTime;
    /** 地区 **/
    private String areaId;
    /** 用户组 **/
    private Long userGroupNo;
    /** 网站类型 **/
    private Integer siteType;
    /** 网址 **/
    private String siteName;


    private Integer probeType;

    /** 分页参数 **/
    private Integer page;
    private Integer pageSize;

    /** 排序参数 **/
    private String sort;
    private String sortOrder;

    /** 用于区分导出的table   **/
    private Integer listType;
}

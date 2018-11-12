package com.aotain.zongfen.model.useranalysis;

import lombok.Getter;
import lombok.Setter;

/**
 * DDos异常流量攻击分析查询参数类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/21
 */
@Getter
@Setter
public class DDoSQueryParam {

    private Long startTime;
    private Long endTime;

    private Long userGroupNo;
    private Integer appAttackType;

    /** for search  YYYYMMdd格式的long型数据 用于查询中时间比较**/
    private Long searchStart;
    private Long searchEnd;

    private Integer page;
    private Integer pageSize;
    private String sort;
    private String sortOrder;
}

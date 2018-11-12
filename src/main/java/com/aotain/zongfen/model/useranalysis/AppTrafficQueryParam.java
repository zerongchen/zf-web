package com.aotain.zongfen.model.useranalysis;

import lombok.Getter;
import lombok.Setter;

/**
 * AppTraffic查询条件实体类
 *
 * @author daiyh@aotain.com
 * @date 2018/04/23
 */
@Getter
@Setter
public class AppTrafficQueryParam {

    private Integer listType;

    /**获取前端参数*/
    private Long startTime;

    private Long endTime;

    private Long statTime;
    private String areaId;
    private Long userGroupNo;
    private Long appType;
    private Long appId;
    private Integer page;
    private Integer pageSize;


    private Integer statType;
    private Integer dateType;
    private String tableName;

    private Integer probeType;

    private String sort;
    private String sortOrder;
}

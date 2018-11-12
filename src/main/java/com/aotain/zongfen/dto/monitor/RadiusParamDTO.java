package com.aotain.zongfen.dto.monitor;

import lombok.Data;

@Data
public class RadiusParamDTO {

    /**
     * 用户中转，发包数
     */
    private String statTime;
    private Integer dateType;

    private String order;

    /**
     * 用户查询列表详情
     */
    private String startTime;
    private String endTime;

}

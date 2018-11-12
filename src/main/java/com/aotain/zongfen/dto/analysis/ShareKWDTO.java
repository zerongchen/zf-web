package com.aotain.zongfen.dto.analysis;

import lombok.Data;

@Data
public class ShareKWDTO {

    private Integer dateType;

    private String stateTime;

    private String areaid;

    private String useraccount;

    private String userip;

    private Integer hostcnt;
    private Integer qqnocnt;
    private Integer natipcnt;
    private Integer cookiecnt;
    private Integer devnamecnt;
    private Integer osnamecnt;
    private Integer probetype;

    private String sort;
    private String order;

    private Integer page;
    private Integer pageSize;


}

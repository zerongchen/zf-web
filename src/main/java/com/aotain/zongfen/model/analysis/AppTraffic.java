package com.aotain.zongfen.model.analysis;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AppTraffic implements Serializable {

    private Long statTime;

    private Long srcgroupId;

    private Long dstgroupId;

    private Integer apptype;

    private Integer appid;

    private String appname;

    private Long apptrafficUp;

    private Long apptrafficDn;

    private Integer probeType;

    private String areaId;

    private String srcAreasubid1;

    private String srcAreasubid2;

    private String srcAreasubid3;

    private String srcAreasubid4;

    private String dstAreasubid1;

    private String dstAreasubid2;

    private String dstAreasubid3;

    private String dstAreasubid4;

    private static final long serialVersionUID = 1L;


}
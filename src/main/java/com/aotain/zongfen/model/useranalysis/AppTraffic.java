package com.aotain.zongfen.model.useranalysis;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;

/**
 * 应用流量分析实体类
 *
 * @author daiyh@aotain.com
 * @date 2018/04/23
 */
@Getter
@Setter
public class AppTraffic {

    private Long statTime;
    private Long userGroupNo;
    private Long appType;
    private Long appId;
    private String appName;
    private Long appUserNum;
    private Long appTrafficUp;
    private String appTrafficUpStr;
    private Long appTrafficDn;
    private String appTrafficDnStr;
    @Transient
    private Long appTrafficSum;
    private String appTrafficSumStr;
    private Long appPacketsNum;
    private Long appSessionsNum;
    private Long appNewSessionNum;
    private Long probeType;
    private String areaId;

    private String appNameOfId;
    private String appTypeName;

}

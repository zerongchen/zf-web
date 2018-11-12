package com.aotain.zongfen.model.useranalysis;

import lombok.Getter;
import lombok.Setter;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/24
 */
@Getter
@Setter
public class AppTrafficChartBean {
    private Long statTime;

    private Long appType;
    private Long appId;
    private Long appTrafficUp;
    private Long appTrafficDn;
    private Long appUserNum;
    private Long appPacketsNum;
    private Long appSessionsNum;
    private Long appNewSessionNum;

    private String appNameOfId;
    private String appNameOfType;
}

package com.aotain.zongfen.model.useranalysis;

import lombok.Getter;
import lombok.Setter;

/**
 * DDoS异常流量攻击实体类
 * refer table (zf_v2_ubas_ddos_d)
 * @author daiyh@aotain.com
 * @date 2018/06/21
 */
@Getter
@Setter
public class DDoSUbas {
    private Long statTime;
    private Long userGroupNo;
    private Integer appAttackType;
    private Long appAttackTraffic;
    private Long appAttackRate;
    private Integer probeType;
    private String areaId;
}

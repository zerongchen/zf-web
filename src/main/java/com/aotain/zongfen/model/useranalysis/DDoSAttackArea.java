package com.aotain.zongfen.model.useranalysis;

import lombok.Getter;
import lombok.Setter;

/**
 * DDoS异常分析区域详情实体类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/25
 */
@Getter
@Setter
public class DDoSAttackArea {
    /********* 攻击来源 *************/
    private String attackAreaName;
    /********* 攻击流量 *************/
    private Long attackTraffic;
    /********* 攻击源次数 *************/
    private Integer attackNum;
    /********* 攻击源地址数 *************/
    private Integer sourceIpNum;
}

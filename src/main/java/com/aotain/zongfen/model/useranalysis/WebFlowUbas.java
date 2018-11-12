package com.aotain.zongfen.model.useranalysis;

import com.aotain.zongfen.utils.CacheUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * web应用站点分析实体类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/13
 */
@Getter
@Setter
public class WebFlowUbas {
    /**
     stat_time           	int
     usergroupno         	bigint
     sitename            	string
     sitetype            	int
     sitehitfreq         	bigint
     sitetraffic_up      	bigint
     sitetraffic_dn      	bigint
     packetsubtype       	int
     probe_type          	int
     area_id             	string
     dt                  	string
     **/
    private Integer statTime;
    private Long userGroupNo;
    private String siteName;
    private Integer siteType;
    /**点击次数**/
    private Long siteHitFreq;
    /**上行流量**/
    private Long siteTrafficUp;
    /**下行流量**/
    private Long siteTrafficDn;
    private Integer packetSubtype;
    private Integer probeType;
    private String areaId;


    /**总流量**/
    private Long siteTrafficSum;

    private String siteTypeName;

    public String getSiteTypeName(){
        return siteTypeName;
    }

    public void setSiteType(int siteType){
        // 给siteType赋值的同时将siteTypeName也赋值
        this.siteType = siteType;
        String siteTypeName = CacheUtil.getWebTypeName(siteType);
        this.siteTypeName = siteTypeName;
    }
}

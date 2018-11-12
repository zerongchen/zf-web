package com.aotain.zongfen.dto.analysis;

import com.aotain.zongfen.utils.basicdata.TrafficDataConstant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AppTrafficDetailResult implements Serializable {


    private String stateTime;
    //源数据类别
    private String srcAreasubid1;
    //目的区域类别/运营商ID
    private String srcAreasubid2;
    private String srcAreasub2;
    //目的省份/大洲
    private String srcAreasub3;
    //目的城域网/国家
    private String srcAreasub4;
    private String srcAreaDesc;

    //应用大类
    private Double totalFlow;
    private Double flowUp;
    private Double flowDn;
    //应用小类
    private Integer appId;
    private String appName;

    //目的数据类别
    private String dstAreasubid1;
    //目的区域类别/运营商ID
    private String dstAreasubid2;
    private String dstAreasub2;
    //目的省份/大洲
    private String dstAreasub3;
    //目的城域网/国家
    private String dstAreasub4;
    private String dstAreaDesc;

    public String getDstAreaDesc() {
        if(StringUtils.isEmpty(getDstAreasub3())){
            if(StringUtils.isEmpty(getDstAreasub4())){
                return dstAreaDesc;
            }else {
                return getDstAreasub4();
            }

        }else {
            return getDstAreasub3()+"["+getDstAreasub4()+"]";
        }
    }

    public String getSrcAreaDesc() {
        if(StringUtils.isEmpty(getSrcAreasub3())){
            if(StringUtils.isEmpty(getSrcAreasub4())){
                return srcAreaDesc;
            }else {
                return getSrcAreasub4();
            }
        }else {
            return getSrcAreasub3()+"["+getSrcAreasub4()+"]";
        }

    }

    private static final long serialVersionUID = 1L;


}
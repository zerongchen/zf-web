package com.aotain.zongfen.model.apppolicy;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class AppFlowManager implements Serializable {

    private Long appFlowId;

    private Long messageNo;

    private String messageName;

    private Integer apptype;

    private Integer appid;

    private String appname;

    private Long appThresholdUpAbs;

    private Long appThresholdDnAbs;

    private Long rStarttime;

    private Long rEndtime;

    private Long cTime;

    private Integer operateType;

    private String createOper;

    private String modifyOper;

    private Date createTime;

    private Date modifyTime;

    private static final long serialVersionUID = 1L;

}
package com.aotain.zongfen.model.apppolicy;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class FlowManager implements Serializable {
    private Long messageNo;

    private Long appflowId;

    private Integer apptype;

    private Integer appid;

    private String appname;

    private Long appThresholdUpAbs;

    private Long appThresholdDnAbs;

    private Long cTime;

    private static final long serialVersionUID = 1L;


}
package com.aotain.zongfen.model.monitor;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RadiusPolicyDetail extends RadiusPolicyDetailKey implements Serializable {
    private Long sendnum;

    private Long sendsuccessnum;

    private Long sendfailednum;

    private Date createTime;

}
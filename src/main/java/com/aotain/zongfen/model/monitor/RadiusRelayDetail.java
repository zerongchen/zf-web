package com.aotain.zongfen.model.monitor;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RadiusRelayDetail extends RadiusRelayDetailKey implements Serializable {


    private Long receivednum;
    private Long parsesuccessnum;
    private Long parsefailednum;

    private Long sendnum;
    private Long sendsuccessnum;
    private Long sendfailednum;
    private Date createTime;

    private static final long serialVersionUID = 1L;

}
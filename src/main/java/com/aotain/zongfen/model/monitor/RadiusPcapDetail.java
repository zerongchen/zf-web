package com.aotain.zongfen.model.monitor;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RadiusPcapDetail implements Serializable {

    private String statTime;
    private String serverIp;
    private Long capturepacketnum;
    private Long validpacketnum;
    private Long invalidpacketnum;
    private Long sendnum;
    private Long sendsuccessnum;
    private Long sendfailednum;
    private Date createTime;
    private static final long serialVersionUID = 1L;

}
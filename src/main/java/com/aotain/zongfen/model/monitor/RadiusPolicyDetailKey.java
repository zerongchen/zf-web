package com.aotain.zongfen.model.monitor;

import lombok.Data;

import java.io.Serializable;

@Data
public class RadiusPolicyDetailKey implements Serializable {
    private String statTime;

    private String srcIp;

    private String dstIp;

    private static final long serialVersionUID = 1L;
}
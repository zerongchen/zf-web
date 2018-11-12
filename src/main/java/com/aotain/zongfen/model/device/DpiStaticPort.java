package com.aotain.zongfen.model.device;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DpiStaticPort implements Serializable {
    private Integer portno;//端口编号

    private String deploysitename;//部署站点名

    private Integer porttype;//端口类型

    private String portdescription;//端口描述信息

    private Integer mlinkid;//链路编号

    private String mlinkdesc;//链路描述

    private Date modifyTime;

    private static final long serialVersionUID = 1L;

}
package com.aotain.zongfen.model.general;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class TriggerHostList implements Serializable {

    private Long hostId;

    private Long triggerHostListid;

    private String hostName;

    private Integer operateType;

    private String createOper;

    private String modifyOper;

    private Date createTime;

    private Date modifyTime;

    private static final long serialVersionUID = 1L;



}
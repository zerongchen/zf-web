package com.aotain.zongfen.model.general;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
public class Trigger implements Serializable {
    private Integer triggerId;

    private String triggerName;

    private Integer triggerFlag;

    private String createOper;

    private String modifyOper;

    private Timestamp createTime;

    private Timestamp modifyTime;

    private Integer operateType;

    private Integer triggerType;

    private static final long serialVersionUID = 1L;


}
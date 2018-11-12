package com.aotain.zongfen.model.general;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class TriggerKWList implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long kwId;

    private Long triggerKwListid;

    private String kwName;

    private Integer operateType;

    private String createOper;

    private String modifyOper;

    private Date createTime;

    private Date modifyTime;

}
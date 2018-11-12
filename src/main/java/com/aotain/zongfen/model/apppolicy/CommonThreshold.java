package com.aotain.zongfen.model.apppolicy;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class CommonThreshold implements Serializable {
    private Long messageNo;

    private Integer webHitThreshold;

    private Integer kwThreholds;

    private String createOper;

    private String modifyOper;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
    private Date createTime;

    private Date modifyTime;
    
    private static final long serialVersionUID = 1L;

    private String policyCount;
}
package com.aotain.zongfen.model.apppolicy;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class CommonSearch implements Serializable {
    private Long seId;

    private Long messageNo;
    
    private String sename;

    private String createOper;

    private String modifyOper;

    private Date createTime;

    private Date modifyTime;
    
    private Integer operateType;
    
    private static final long serialVersionUID = 1L;

}
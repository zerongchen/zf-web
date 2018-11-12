package com.aotain.zongfen.model.general.user;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class UserGroup implements Serializable {

    private Integer messageType;

    private Long messageNo;

    private Long userGroupId;

    private String userGroupName;

    private String userGroupDesc;

    private String createOper;

    private String modifyOper;

    private String appPolicy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer operateType;
    
    private String startTime;
    
    private String endTime;

    private static final long serialVersionUID = 1L;


}
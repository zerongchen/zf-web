package com.aotain.zongfen.model.general.user;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements Serializable {

    private Long userGroupId;

    private Integer userType;

    private String userName;

    private Integer operateType;

    private Date createTime;

    private Date modifyTime;

    private String createOper;

    private String modifyOper;

    private static final long serialVersionUID = 1L;


}
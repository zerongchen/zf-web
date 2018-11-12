package com.aotain.zongfen.model.apppolicy;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AppFlowManagerUserGroup implements Serializable {

    private Long appflowId;

    private Integer userType;

    private Long userGroupId;

    private String userName;

    private static final long serialVersionUID = 1L;


}
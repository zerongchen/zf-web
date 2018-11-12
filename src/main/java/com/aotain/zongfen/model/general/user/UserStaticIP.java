package com.aotain.zongfen.model.general.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class UserStaticIP implements Serializable {

    private Long ipId;

    private Long userid;

    @NonNull private String userip;

    @NonNull  private Integer useripPrefix;

    private Long messageSequenceno;

    private Integer operateType;

    private String createOper;

    private String modifyOper;

    private Date createTime;

    private Date modifyTime;

    private static final long serialVersionUID = 1L;

}
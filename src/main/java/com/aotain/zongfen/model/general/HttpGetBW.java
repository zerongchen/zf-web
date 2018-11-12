package com.aotain.zongfen.model.general;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class HttpGetBW implements Serializable {
    private Long id;

    private Integer type;//0=白名单，1=黑名单,2=URL黑名单（domain存的url值）

    private String domain;

    private Date updateTime;

    private static final long serialVersionUID = 1L;


}
package com.aotain.zongfen.dto.general.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserStaticIPDTO implements Serializable {

    private Long ipId;

    private Long userid;

    private String userName;

    private Long messageNo;

    private String userip;

    private Integer useripPrefix;

    private String[] userips;

    private Integer[] useripPrefixs;

    private Integer operatetype;

    private static final long serialVersionUID = 1L;


}
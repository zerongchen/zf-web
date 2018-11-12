package com.aotain.zongfen.model.general;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Bras implements Serializable {

    private String brasIp;

    private String brasName;

    private Date firstTime;

    private Date lastTime;

    private static final long serialVersionUID = 1L;

}
package com.aotain.zongfen.model.general.userbind;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserBindModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long bindId;

    private Long messageNo;

    private Integer userType;

    private String userName;

    private String appPolicy;

    private String userGroupId;

    private Integer bindMessageType;

    private Long bindMessageNo;

    private String bindMessageName;

    @Transient
    @JSONField(serialize = false)
    /**  此字段只用于接收前端传递参数或展示  */
    private String createOper;

    @Transient
    @JSONField(serialize = false)
    /**  此字段只用于接收前端传递参数或展示  */
    private String modifyOper;

    @Transient
    @JSONField(serialize = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    /**  此字段只用于接收前端传递参数或展示  */
    private Date createTime;

    @Transient
    @JSONField(serialize = false)
    /**  此字段只用于接收前端传递参数或展示  */
    private Date modifyTime;
    
    @Transient
    @JSONField(serialize = false)
    /**  此字段只用于接收前端传递参数或展示  */
    private String startTime;

    @Transient
    @JSONField(serialize = false)
    /**  此字段只用于接收前端传递参数或展示  */
    private String endTime;
}

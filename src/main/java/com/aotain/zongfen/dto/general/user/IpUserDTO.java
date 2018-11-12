package com.aotain.zongfen.dto.general.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.aotain.common.policyapi.model.msg.IpMsg;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class IpUserDTO implements Serializable {

    private Long userId;

    private Integer messageType;

    @NonNull private Long messageNo;

    @NonNull private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private List<IpMsg> userInfo;

    private static final long serialVersionUID = 1L;

    @JSONField(serialize = false)
    /**  应用策略下发成功数/下发异常数  */
    private String appPolicy;
    
    @JSONField(serialize = false)
    private String startTime;
    @JSONField(serialize = false)
    private String endTime;

}
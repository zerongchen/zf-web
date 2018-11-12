package com.aotain.zongfen.dto.apppolicy;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowManagerDTO implements Serializable {

    /**
     * 策略ID
     */
    private Long messageNo;
    private Long messageSequenceNo;

    /**
     * 策略名称
     */
    private String messageName;

    private Integer messageType;

    /**
     * 是否有效策略
     */
    private Boolean activeFlag;

    private String timeBar;

    /**
     * 绑定策略 用户帐号或静态IP地址
     */
    private List<String> userName;

    /**
     * 绑定用户组
     */
    private List<Long> puserGroup;

    /**
     * 用户类型 0-全局策略 1-账号用户 2-静态IP用户 3-用户组
     */
    private Integer userType;

    /**
     * 策略有效期 startTime - endTime
     */
    private String startTime;

    private Long appFlowId;

    private String endTime;

    private Integer apptype;

    private String apptypeDesc;

    private Integer appid;

    private String appname;

    private Long appThresholdUpAbs;

    private Long appThresholdDnAbs;

    private Long rStarttime;

    private Long rEndtime;

    private Long cTime;

    private Integer operateType;

    private String createOper;

    private String modifyOper;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date searchStartTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date searchEndTime;




    //    应用策略下发成功数/下发异常数
    private String policyCount;

    //    绑定策略下发成功数/下发异常数
    private String bindPolicyCount;

}
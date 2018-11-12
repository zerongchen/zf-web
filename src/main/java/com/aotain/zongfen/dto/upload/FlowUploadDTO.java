package com.aotain.zongfen.dto.upload;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.aotain.common.policyapi.model.WebPushStrategy;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
public class FlowUploadDTO implements Serializable ,Cloneable{


//    private Integer seqId;

    private Integer packetType;

    private Integer packetSubtype;

//    private List<Integer> packetSubtypes;

    private Long rStarttime;

    private String startTime;

    private Long rEndtime;

    private String endTime;

    private Integer rfreq;

    private Integer zongfenId;

    private Integer rMethod;

    private Integer operatetype;

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

    private Long messageNo;//本策略的messageNo
    
    private Long userGroupMessageNo;//用户组绑定策略的messageNo，zf_v2_policy_userpolicy_bind
    private Long lastAreaGroupMessageNo;//修改前的messageNo
    private Long areaGroupMessageNo;//流量流向策略的messageNo
    private String areaGroupMessageName;//流量流向策略


    private Long ddosManageMessageNo;// ddos异常流量管理
    private String ddosMessageName;//ddos异常流量管理
    private Long lastDdosManageMessageNo;//修改前的messageNo
    
    private Long webPushMessageNo;// web信息推送管理
    private String webPushMessageName;//web信息推送管理
    private Long lastWebPushGroupMessageNo;//修改前的messageNo

    private Long appUserMessageNo;// 指定应用用户策略
    private String appUserMessageName;//指定应用用户策略对应名称
    private Long lastAppUserMessageNo;//修改前的messageNo

    private Long messageSequenceNo;

    private Integer messageType;

    private String messageName;

    private Integer userType;

    private List<String> userName;

    private List<Long> puserGroup;

    //    应用策略下发成功数/下发异常数
    private String policyCount;
    //    绑定策略下发成功数/下发异常数
    private String bindPolicyCount;
    
    //   流量流量 绑定策略下发成功数/下发异常数
    private String bindFlowPolicyCount;

    //   ddos 绑定策略下发成功数/下发异常数
    private String bindDdosPolicyCount;

    //   指定应用用户策略下发成功数/下发异常数
    private String bindAppUserPolicyCount;

    private static final long serialVersionUID = 1L;
    
    private WebPushStrategy webPushStrategy;

    @Override
    public Object clone() throws CloneNotSupportedException {
        FlowUploadDTO dto= null;
        dto = (FlowUploadDTO)super.clone();
        return dto;
    }
}
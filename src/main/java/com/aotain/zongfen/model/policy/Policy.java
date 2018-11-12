package com.aotain.zongfen.model.policy;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
public class Policy implements Serializable {
	@NonNull private Long messageNo;

	@NonNull private String messageName;

	@NonNull private Integer operateType;
	
    @NonNull private Integer messageType;

    private static final long serialVersionUID = 1L;

    @Deprecated
    private Date startTime;

    @Deprecated
    private Date endTime;
    
    @Deprecated
    private Long flag;
    
    //新表结构
    private Date createTime;

    private Date modifyTime;

    private String modifyOper;
    
    private String createOper;
    
    private Long messageSequenceno;
    
    private String modifyTimeStr;
    
    private String createTimeStr;
    
    //    应用策略下发成功数/下发异常数
    @JSONField(serialize = false)
    private String policyCount;

    //    绑定策略下发成功数/下发异常数
    @JSONField(serialize = false)
    private String bindPolicyCount;

    /**   just for time search     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date searchStartTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date searchEndTime;
    /**   just for time search     */
    
}
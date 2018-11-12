package com.aotain.zongfen.model.general;

import com.alibaba.fastjson.annotation.JSONField;
import com.aotain.common.policyapi.model.base.BaseVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class TriggerKW extends BaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long triggerKwListid;

    private String triggerKwListname;

    private Long kwNum;

    private Date createTime;

    private Date modifyTime;

    private String createOper;

    private String modifyOper;

    /**
     * 查询
     */
    @JSONField(serialize = false)
    private String startTime;
    @JSONField(serialize = false)
    private String endTime;
}
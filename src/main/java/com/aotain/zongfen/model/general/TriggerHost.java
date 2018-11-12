package com.aotain.zongfen.model.general;

import com.alibaba.fastjson.annotation.JSONField;
import com.aotain.common.policyapi.model.base.BaseVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class TriggerHost extends BaseVO implements Serializable {
    private Long triggerHostListid;

    private String triggerHostListname;

    private Date createTime;

    private Date modifyTime;

    private String createOper;

    private String modifyOper;

    private Integer hostListtype;

    private Long hostNum;
    /**
     * 查询
     */
    @JSONField(serialize = false)
    private String startTime;
    @JSONField(serialize = false)
    private String endTime;
    private static final long serialVersionUID = 1L;


}
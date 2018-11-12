package com.aotain.zongfen.dto.general;

import java.io.Serializable;
import java.sql.Timestamp;

import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TriggerKWDTO implements Serializable {

    private String appPolicy;

    private Long messageNo;

    private Long triggerKwListid;

    private String triggerKwListname;

    private Long kwNum;

    private Integer operateType;

    private String createOper;

    private String modifyOper;

    private Timestamp createTime;

    private Timestamp modifyTime;

    private String createTimeStr;

    private static final long serialVersionUID = 1L;

    public String getCreateTimeStr(){
        return DateUtils.getDateTime(getCreateTime(), DateFormatConstant.DATETIME_CHS);
    }

}
package com.aotain.zongfen.dto.general;

import com.aotain.zongfen.model.general.TriggerRelation;
import com.aotain.zongfen.utils.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class TriggerDTO implements Serializable {
    private Integer triggerId;

    private String triggerName;

    private Integer triggerFlag;

    private String status;

    private String createOper;

    private String modifyOper;

    private Timestamp createTime;

    private Timestamp modifyTime;

    private String createTimeStr;

    private String modifyTimeStr;

    private Integer operateType;

    private Integer triggerType;

    private List<TriggerRelation> relation;

    private static final long serialVersionUID = 1L;

    public String getStatus(){
        if(getTriggerFlag()==1){
            return "生效";
        }else {
            return "失效";
        }
    }

    public String getCreateTimeStr(){
        return DateUtils.getDateTime(getCreateTime());
    }

    public String getModifyTimeStr(){
        return DateUtils.getDateTime(getModifyTime());
    }


}
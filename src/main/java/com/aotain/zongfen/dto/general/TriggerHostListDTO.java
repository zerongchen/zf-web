package com.aotain.zongfen.dto.general;

import com.aotain.zongfen.annotation.ExpSheet;
import com.aotain.zongfen.annotation.Export;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@ExpSheet(name="网站")
public class TriggerHostListDTO implements Serializable {

    private Long triggerHostListid;

    private String triggerHostListname;

    @Export(title="内容", id=1)
    private String hostName;

    private Integer hostId;

    private Integer operateType;

    private String createOper;

    private String modifyOper;

    private Timestamp createTime;

    private Timestamp modifyTime;

    /**
     * 查询
     */
    private String startTime;
    private String endTime;
    
    private static final long serialVersionUID = 1L;

}
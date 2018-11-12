package com.aotain.zongfen.dto.general;

import com.aotain.zongfen.annotation.ExpSheet;
import com.aotain.zongfen.annotation.Export;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@ExpSheet(name="关键字")
public class TriggerKWListDTO implements Serializable {
    private Long triggerKwListid;

    private String triggerKwListname;

    @Export(title="内容", id=1)
    private String kwName;

    private Integer kwId;

    private Integer operateType;

    private String createOper;

    private String modifyOper;

    private Timestamp createTime;

    private Timestamp modifyTime;

    private static final long serialVersionUID = 1L;

}
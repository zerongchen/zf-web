package com.aotain.zongfen.dto.general;

import com.aotain.zongfen.annotation.ExpSheet;
import com.aotain.zongfen.annotation.Export;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ExpSheet(name="域名(URL)")
public class HttpGetBWDTO implements Serializable {

    private Long id;

    private Integer type;

    @Export(title="域名(URL)", id=1)
    private String domain;

    private Timestamp updateTime;

    private String updateTimeStr;

    private static final long serialVersionUID = 1L;

    public String getUpdateTimeStr(){
        return DateUtils.getDateTime(getUpdateTime(), DateFormatConstant.DATE_CHS_HYPHEN);
    }

    public String toTxtString(){
        return domain;
    }
}
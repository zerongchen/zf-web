package com.aotain.zongfen.dto.general;

import com.aotain.zongfen.annotation.ExpSheet;
import com.aotain.zongfen.annotation.Export;
import com.aotain.zongfen.utils.DateUtils;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@ExpSheet(name="bras信息")
public class BrasDTO implements Serializable {

    @Export(title = "IP地址" ,id = 1)
    private String brasIp;

    @Export(title = "BRAS名称" ,id = 2)
    private String brasName;

    private Timestamp firstTime;

    @Export(title = "首次发现时间" ,id = 3)
    private String firstTimeStr;

    private Timestamp lastTime;

    @Export(title = "最近发现时间" ,id = 4)
    private String lastTimeStr;

    public String getFirstTimeStr(){
        return DateUtils.getDateTime(getFirstTime());
    }
    public String getLastTimeStr(){
        return DateUtils.getDateTime(getLastTime());
    }

}
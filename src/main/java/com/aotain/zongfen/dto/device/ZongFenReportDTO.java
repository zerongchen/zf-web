package com.aotain.zongfen.dto.device;

import com.aotain.zongfen.utils.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
public class ZongFenReportDTO implements Serializable {

    private Long reportServerId;

    private String reportServerName;

    private String zongfenName;

    private Long zongfenPort;

    private String  zongfenMac;

    private String  zongfenIp;

    private Timestamp reportTime;

    private String reportTimeStr;

    private String startTime;

    private String endTime;

    private String reportFile;

    private String reportPath;

    private String order;

    private static final long serialVersionUID = 1L;

    public String getReportTimeStr(){
        return DateUtils.getDateTime(getReportTime());
    }

}
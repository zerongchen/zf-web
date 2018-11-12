package com.aotain.zongfen.dto.monitor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class RadiusMonitorDTO implements Serializable {


    /**
     * 时间跨度
     */
    private String startTime;

    private String endTime;

    /**
     * 时间粒度 (1:分钟，2：小时，3：天)
     */
    private Integer dateType;

    /**
     * 统计时间
     */
    private String statTime;

    /**
     * 采集包数(Radius数据采集监控)
     */
    private Long capturepacketnum;

    /**
     * 有效包数(Radius数据采集监控)
     */
    private Long validpacketnum;

    /**
     * 无效包数(Radius数据采集监控)
     */
    private Long invalidpacketnum;

    /**
     * 接收包数(中转)
     */
    private Long receivednum;
    /**
     * 解析成功包数(中转)
     */
    private Long parsesuccessnum;
    /**
     * 解析失败包数(中转)
     */
    private Long parsefailednum;

    /**
     * 发送包数(Radius数据中转监控表)
     */
    private Long sendnumRedirect;

    /**
     * 发送成功的包数(Radius数据中转监控表)
     */
    private Long sendsuccessnumRedirect;

    /**
     * 发送失败的包数(Radius数据中转监控表)
     */
    private Long sendfailednumRedirect;


    /**
     * 发送包数(Radius数据policy下发监控表)
     */
    private Long sendnumPolicy;

    /**
     * 发送成功的包数(Radius数据policy下发监控表)
     */
    private Long sendsuccessnumPolicy;

    /**
     * 发送失败的包数(Radius数据policy下发监控表)
     */
    private Long sendfailednumPolicy;

    /**
     * 文件类型:1=AAA生成文件信息,2=全业务流量生成文件信息,3=业务流量流向生成文件信息(生成文件监控统计表)
     */
    private Integer fileType;

    /**
     * 文件数,文件大小(表的单位是KB，前端展示是GB) ,文件记录数 (生成文件监控统计表)
     */
    private Long fileNumCreate;
    private Double fileSizeCreate;
    private Long fileRecordCreate;

    /**
     * 文件数,文件大小(表的单位是KB，前端展示是GB)  (上报文件监控统计表)
     */
//    private Long fileNumUpload;
//    private Double fileSizeUpload;

    private static final long serialVersionUID = 1L;

}
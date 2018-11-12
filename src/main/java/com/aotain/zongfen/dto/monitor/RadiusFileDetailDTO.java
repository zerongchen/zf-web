package com.aotain.zongfen.dto.monitor;

import lombok.Data;

@Data
public class RadiusFileDetailDTO {

    private String fileName;//文件名称

    private String serverCreatedIp;//服务器IP

    private Integer fileType;//文件类型。1=AAA生成文件信息,2=全业务流量生成文件信息,3=业务流量流向生成文件信息

    private String fileCreatedTime;//文件生成时间,UTC格式(前端展示需要将长整形转成String格式)

    private String fileUploadTime;//文件生成时间,UTC格式(前端展示需要将长整形转成String格式)

    private Double fileCreateSize;//文件大小,单位KB

    private Long fileCreateRecode;//文件记录数

    private String serverUploadIp;//服务器IP

    private String serverUploadName;//服务器名称

    private String serverReceivedIp;//接收服务器IP

    private Double fileUploadSize;//文件大小,表单位KB,前端展示M

    private Integer warnType;//告警类型

}

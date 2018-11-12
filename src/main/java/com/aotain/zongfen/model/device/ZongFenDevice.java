package com.aotain.zongfen.model.device;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZongFenDevice implements Serializable {
    private Integer zongfenId;

    private String zongfenIp;

    private Integer zongfenPort;
    
    private Integer zongfenFtpPort;

    private String zongfenName;

    private String createOper;

    private Date createTime;

    private String modifyOper;

    private Date modifyTime;
    
    private Integer isVirtualIp;

    private static final long serialVersionUID = 1L;
    
    /**
     * 组合物理服务器IP和端口用
     */
    private List<String> realIp;
    
    private String ips;
    
    private List<ZongFenDeviceUser> deviceUsers;
    
    /**
     * 设备用途页面展示
     */
    private String deviceUser;
    
    /**
     * 页面判断是否可以修改
     */
    private Integer deviceType;
}
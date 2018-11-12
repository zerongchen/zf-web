package com.aotain.zongfen.utils.basicdata;

/**
 * <P>数据上报类型</P>
 * @author Chenzr
 * @since 2018-02-30
 */
public enum UdUploadType {

    WEB_FLOW_UPLOAD_OUTSIDESOURCE(1,0,"Web类流量统计（资源在网外）"),
    VOIP_FLOW_UPLOAD(1,1,"VoIP类流量统计"),
    GENERAL_FLOW_UPLOAD(1,2,"通用类流量统计"),
    APP_USER_FLOW_UPLOAD(1,3,"访问指定应用的用户统计"),
    DOWNLOAD_FLOW_UPLOAD(1,4,"Download类分析功能模块"),
    WEB_FLOW_UPLOAD_INSIDESOURCE(1,5,"WEB类流量统计（资源在网内）"),
    USER_PREFERENCE_UPLOAD(1,128,"用户偏好分析"),
    ILLEGAL_ROUTER_CHECK_UPLOAD(1,129,"非法路由用户分析"),
    ONE_N_USERACTION_CR_UPLOAD(1,130,"一拖N用户行为分析（检测结果）"),
    ONE_N_USERACTION_KW_UPLOAD(1,131,"一拖N用户行为分析（关键字段）"),
    WEB_PUSHRESULT_UPLOAD(1,132,"Web信息推送结果上报"),
    DDOS_ANALYSE_UPLOAD(1,192,"应用层DDoS异常流量分析功能模块"),
    CP_SP_UPLOAD(1,193,"CP/SP 资源服务器分析上报模块"),
    P2P_FLOW_ANALYSE_UPLOAD(1,194,"P2P应用流量流向分析模块"),
    IP_ADDRESS_TOPN_UPLOAD(1,198,"IP地址流量TOP N流量分析"),
    APP_FLOW_DIRECTION_UPLOAD(1,196,"流量流向分析结果上报"),
    PROTOCOL_INFO_UPLOAD(2,100,"特有协议信息"),
    HTTPGET_BW_KEY_UPLOAD(3,0,"HTTP GET报文关键字段上报"),
    WLAN_INFO_UPLOAD(3,1,"移动终端分析")
    ;

    //UD报文类型
    private Integer packetType;

    //Ud 报文子类型
    private Integer packetSubtype;

    //描述
    private String  description;


    UdUploadType(Integer packetType,Integer packetSubtype,String  description){
        this.packetType=packetType;
        this.packetSubtype=packetSubtype;
        this.description=description;
    }

    public Integer getPacketType() {
        return packetType;
    }

    public void setPacketType( Integer packetType ) {
        this.packetType = packetType;
    }

    public Integer getPacketSubtype() {
        return packetSubtype;
    }

    public void setPacketSubtype( Integer packetSubtype ) {
        this.packetSubtype = packetSubtype;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }
}

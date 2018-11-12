package com.aotain.zongfen.log.constant;

import lombok.Getter;
import lombok.Setter;

public enum ModelType {
	//流量上报 messageType = messageType * 100000 + packetType * 1000 + packetSubtype
	MODEL_UPLOAD_NET_FLOW_IN(101003,"Web类流量统计",101000),
	MODEL_UPLOAD_VOIP(101013,"VOIP类流量",101001),
	MODEL_UPLOAD_FLOW_MANAGER(101001,"通用流量类",101002),
	MODEL_UPLOAD_APP_USER(101002,"指定应用用户",101003),
	MODEL_UPLOAD_DOWNLOAD(101014,"Download类分析",101004),
	MODEL_UPLOAD_NET_FLOW_OUT(101003,"Web类流量统计",101005),
	MODEL_UPLOAD_USER_PREFERENCE(101015,"用户偏好分析",101128),
	MODEL_UPLOAD_ILLEGAL_ROUTER(101005,"非法路由",101129),
	MODEL_UPLOAD_1toN_CR(101006,"1拖N检测",101130),
	MODEL_UPLOAD_1toN_KW(101006,"1拖N检测",101131),
	MODEL_UPLOAD_DDOS(101009,"DDOS异常流量",101192),
	MODEL_UPLOAD_CP_SP(101008,"CP/SP服务器",101193),
	MODEL_UPLOAD_P2P(101016,"P2P应用流量流向",101194),
	MODEL_UPLOAD_FLOW_DIRECTION(101004,"应用流量流向",101196),
	MODEL_UPLOAD_IP_TOPN(101017,"IP地址流量TOPN流量",101198),
	MODEL_UPLOAD_WEB_PUSH(101007,"WEB推送结果",101132),
	MODEL_UPLOAD_SPECIFIC_PROTOL(101010,"特有协议信息",102000),
	MODEL_UPLOAD_HTTPGET(101011,"HTTPGET数据",103000),
	MODEL_UPLOAD_WLAN(101012,"移动终端分析",103001),

	//其他策略
	MODEL_PARAM_SET(102001,"参数设置",0),
	MODEL_FLOW_MANAGER(102002,"通用流量管理",6),
	MODEL_WEB_FLOW(102003,"WEB流量管理",2),
	MODEL_VOIP_FLOW(102004,"VOIP流量管理 ",5),
	MODEL_FLOW_MARK(102005,"流量标记 ",7),
	MODEL_APP_USER(102006,"指定应用用户 ",8),
	SHARE_MANAGE_POLICY(102007,"一拖N用户管理策略",66),

	MOEL_WEBPUSH_LIB(104001,"信息推送触发库",203),
	MODEL_URL_LIB(104002,"WEB分类库",200),
	MODEL_IP_ADDR_LIB(104003,"IP地址库",202),
	MODEL_APPNAME_LIB(104004,"应用名称对应表",201),
	MODEL_HTTPGET_LIB(104005,"HTTPGET黑白名单",207),
	MODEL_LINK(104006,"链路信息管理",104006),
	MODEL_BRASE(104007,"BRAS信息管理",104007),

	MODEL_WEB_PUSH(102008,"WEB信息推送管理",65),
	MODEL_FLOW_MIRROR(102010,"流量镜像",9),
	MODEL_APP_DEFINED(102011,"用户特征自定义",10),
	MODEL_FLOW_DIRECTION(102012,"流量流向管理 ",69),
	MODEL_USER_GROUP(103001,"用户组管理 ",64),
	MODEL_USER_STATICIP(103002,"IP地址用户信息 ",130),
	MODEL_APP_USER_BIND(103003,"用户/应用策略信息",133),
	MODEL_DEVICE_MANAGE(301001,"设备管理",192),
	MODEL_DPI_STATIC(301002,"DPI静态信息",196001),
	MODEL_DPI_DYNAMIC(301003,"DPI动态信息",196002),


	MODEL_USERTRAFFICANALYSIS(201002,"应用流量流向",201002),
	MODEL_SHARE_KW_ANALUSIS(201007,"1拖N用户分析",201007),
	MODEL_USERTRAFFICANALYSIS_IDC(202001,"应用流量流向",202001),
	MODEL_USERAPPANALYSIS(201004,"访问指定应用用户分析",201004),
	MODEL_ILLEGALROUTEANALYSIS(201005,"非法路由检测分析",201005),
	MODEL_WLANANALYSIS(201006,"WLAN终端分析",201006),
	MODEL_IDC_SHARE_KW_ANALUSIS(202007,"假接入真互联分析",202007),

	//告警：model和messageType 一致
	MODEL_MONITOR_ALARM(401001,"告警监控管理",401001),
	MODEL_MONITOR_TASK(401002,"任务监控管理",401002);
	
	//      暂时占用 再次添加勿重复   1+级联菜单位置(01、02)+messageType(三位)
	//      102007 流量标记
	//      102009 流量镜像
	//      102010 用户特征自定义
	//      102000 指定应用用户
	//      102004 VOIP流量管理
//	链路信息管理	104006
//	BRAS信息管理	104007


	@Getter
	@Setter
	private int model;
	
	@Getter
	@Setter
	private int messageType;
	
	@Getter
	@Setter
	private String description;
	
	
	private ModelType(int model, String description, int messageType){
		this.model = model;
		this.description = description;
		this.messageType = messageType;
	}	
	
	/**
	 * 
	 * @param messageType
	 * @return
	 */
	public static int getModelType(int messageType){
		for(ModelType item : ModelType.values()){
			if(item.getMessageType() == messageType ) {
				return item.getModel();
			}
		}
		return -1;
	}
	
	/**
	 * 根据ordinal值获取枚举对象
	 * @param ordinal
	 * @return ModelType
	 */
	public static ModelType valueOf(int ordinal) {
		ModelType[] modelTypes = ModelType.values();
        if (ordinal < 0 || ordinal >= modelTypes.length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return modelTypes[ordinal];
    }
}

package com.aotain.zongfen.dto.apppolicy;

import com.alibaba.fastjson.annotation.JSONField;
import com.aotain.common.policyapi.model.base.BaseVO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import javax.validation.constraints.Max;
import java.util.Date;

/**
 * <P>web 推送策略</P>
 * @author Chenzr
 *
 *  key: Strategy_0_65
    field：123
    value:
        {
        "messageNo":123,
        "messageSequenceNo":122,
        "messageType":133,
        "operationType":1,
        "advType":4,
        "advWhiteHostListId":345,
        "triggerHostListId":"",
        "triggerKwListId":12,
        "advId":134,
        "advFrameUrl":”http://www.baidu.com”,
        "advToken":10,
        "advDelay":24
        }
 *
 *
 */

@Getter
@Setter
public class WebPushStrategyVo extends BaseVO{

    /** 
	* @Fields serialVersionUID :
	*/
	private static final long serialVersionUID = 2938586901151283411L;

	private Long advId;

	private Integer advType;

    /**
     * 信息推送网站白名单ID，为0的时候，表示没有白名单
     */
    private Integer advWhiteHostListId;

    /**
     * 触发信息推送的Host列表编号：空表示没有，多个以逗号隔开
     */
    private String triggerHostListId;

    /**
     * 触发关键词列表编号：空表示没有，多个以逗号隔开
     */
    private String triggerKwListId;

    // 信息框架服务器推送的URL
    private String advFramDPIrl;

    // 推送配额 取值1-255
    private Integer advToken;

    // 信息推送时间,单位秒, 取值0-255'
    private Integer advDelay;

    @Transient
    @JSONField(serialize = false)
    /**  策略创建时间  */
    private Date createTime;

    @Transient
    @JSONField(serialize = false)
    /**  策略修改时间  */
    private Date modifyTime;

    @Transient
    @JSONField(serialize = false)
    /**  此字段只用于接收前端传递参数或展示  */
    private String createOper;

    private String startTime;
    private String endTime;

    @Transient
    @JSONField(serialize = false)
    /**  此字段只用于接收前端传递参数或展示  */
    private String modifyOper;

    @JSONField(serialize = false)
    /**  应用策略下发成功数/下发异常数  */
    private String webPushPolicy;

}

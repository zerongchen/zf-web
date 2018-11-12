/**   
* @Title: DdosExceptFlowStrategy.java 
* @Package com.aotain.zf.common.policy.model 
* @Description: TODO(用一句话描述该文件做什么) 
* @author DongBoye   
* @date 2018年1月18日 上午9:07:21   
*/
package com.aotain.zongfen.dto.apppolicy;

import com.alibaba.fastjson.annotation.JSONField;
import com.aotain.common.policyapi.model.base.BaseVO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/** 
* @ClassName: DdosExceptFlowStrategy 
* @Description: DDOS异常流量策略(这里用一句话描述这个类的作用) 

* @author chenym
* @date 2018年4月12日 上午9:07:21
*  
*/
@Getter
@Setter
public class DdosExceptFlowStrategyPo extends BaseVO {

	/** 
	* @Fields serialVersionUID :
	*/
	private static final long serialVersionUID = 3013479687781076240L;

	private Integer ddosId;
	/**
	 * 攻击类型
	 */
	private String appAttackType;
	/**
	 * 判定为攻击的阈值
	 */
	private String attackThreshold;
	/**
	 * 对攻击进行控制的阈值
	 */
	private String attackControl;

	@Transient
	@JSONField(serialize = false)
	/**  此字段只用于接收前端传递参数或展示  */
	private String createOper;

	@Transient
	@JSONField(serialize = false)
	/**  此字段只用于接收前端传递参数或展示  */
	private String modifyOper;

	@Transient
	@JSONField(serialize = false)
	/**  此字段只用于接收前端传递参数或展示  */
	private Date createTime;

	@Transient
	@JSONField(serialize = false)
	/**  此字段只用于接收前端传递参数或展示  */
	private Date modifyTime;

	private String startTime;
	private String endTime;

	@JSONField(serialize = false)
	/**  应用策略下发成功数/下发异常数  */
	private String ddosPolicy;

	@JSONField(serialize = false)
	/**  绑定策略下发成功数/下发异常数  */
	private String bindPolicy;

	private Integer userType;

	@JSONField(serialize = false)
	private String userName;

	@JSONField(serialize = false)
	private String userGroupId;

}

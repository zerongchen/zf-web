package com.aotain.zongfen.service.apppolicy.webpushmanage;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.policyapi.model.WebPushStrategy;
import com.aotain.zongfen.dto.apppolicy.WebPushStrategyVo;

import java.util.List;

public interface WebPushManageService {


	boolean addWebPushPolicyAndUserBindPolicy(WebPushStrategy webPushStrategy);

	/**
	 * 查询分页数据
	 * @param page
	 */
	List<WebPushStrategyVo> listData(WebPushStrategyVo page);


	/**
	 * 根据主键查询记录
	 * @param webPushStrategy
	 * @return
	 */
	WebPushStrategyVo selectByPrimaryKey(WebPushStrategyVo webPushStrategy);

	/**
	 * 批量删除数据
	 * @param webPushStrategy
	 * @return
	 */
	boolean deletePolicys(List<WebPushStrategy> webPushStrategy);


	/**
	 * 重发策略
	 * @param topTaskId
	 * @param messageNo
	 * @param dpiIp
	 * @param needSendBindPolicy 是否需要发送绑定策略
	 * @return
	 */
	boolean resendPolicy(long topTaskId, long messageNo, boolean needSendBindPolicy, List<String> dpiIp);


	boolean modifyWebPushManageAndUserBindPolicy(WebPushStrategy webPushStrategy);
	
	/**
	 * 
	* @Title: isExistBind
	* @Description: 是否与上报策略有关联
	* @param @param messageNo
	* @param @return
	* @return int
	* @throws
	 */
	int isExistBind(Long[] messageNo);
}

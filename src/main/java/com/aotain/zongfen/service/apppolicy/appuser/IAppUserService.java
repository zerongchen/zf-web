package com.aotain.zongfen.service.apppolicy.appuser;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.policyapi.model.AppUserStrategy;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/03/05
 */
public interface IAppUserService {

    AppUserStrategy selectByPrimaryKey(AppUserStrategy appUserStrategy);

    /**
     * 分页查询数据
     * @param queryMap
     * @return
     */
    List<AppUserStrategy> listData(Map<String,Object> queryMap);

    /**
     * 根据messageNo批量删除数据
     * @param messageNos
     * @return
     */
    int batchDeleteAppUser(List<Long> messageNos);

    /**
     * 批量删除关联表中的数据
     * @param messageNos
     * @param messageType
     * @return
     */
    int batchDeleteRelativeMessageNo(List<Long> messageNos,long messageType);

    /**
     * 批量删除数据包括关联表的数据
     * @param messageNos
     * @param messageType
     */
    void batchDelete(List<Long> messageNos,long messageType);

    /**
     * 重发策略
     * @param topTaskId
     * @param messageNo
     * @param dpiIp
     * @param needSendBindPolicy 是否需要发送绑定策略
     * @return
     */
    boolean resendPolicy(long topTaskId,long messageNo, boolean needSendBindPolicy, List<String> dpiIp);
}

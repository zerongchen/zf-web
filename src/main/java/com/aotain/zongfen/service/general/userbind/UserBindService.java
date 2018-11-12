package com.aotain.zongfen.service.general.userbind;

import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.model.general.userbind.UserBindModel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserBindService {


   /**
     * 用户组策略新增
     * @param request
     * @return
     */

   List<String> addUserBind(HttpServletRequest request) throws Exception;


    /**
     * 获取用户组信息
     * @param userBindModel
     * @return
     */
    List<UserBindModel> getUserGroupList(UserBindModel userBindModel);

    List<Multiselect> getbindMessageName(Long bindMessageType);

    boolean deleteBindPolicy(String[] bindId,String[] bindMessageTypes,String[] bindMessageNos);


    /**
     * 重发策略
     * @param topTaskId
     * @param dpiIp
     * @return
     */
    boolean resendPolicy(long topTaskId, long bindId, List<String> dpiIp);

}

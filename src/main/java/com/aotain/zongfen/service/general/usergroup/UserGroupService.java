package com.aotain.zongfen.service.general.usergroup;

import com.aotain.common.policyapi.model.UserGroupStrategy;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.BaseEntity;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.device.DpiStaticPort;
import com.aotain.zongfen.model.general.Bras;
import com.aotain.zongfen.model.general.user.UserGroup;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserGroupService {

    /**
     * 用户组策略新增
     * @param request
     * @param userGroup
     * @return
     */
    ResponseResult<List<ImportResultList>> addOrUpdateUserGroup( HttpServletRequest request, UserGroup userGroup ) throws ImportException;

    /**
     * 获取用户组信息
     * @param userGroup
     * @return
     */
    List<UserGroup> getUserGroupList( UserGroup userGroup );

    /**
     * 删除用户组策略，和对应的messageNos，支持多组操作
     * @param groupIds
     * @param messageNos
     */
    void deleteUserGroupPolicy(String[] groupIds,String[] messageNos);

    void updateUserGroupName(UserGroup userGroup);

    /**
     * 重发策略
     * @param topTaskId
     * @param messageNo
     * @param dpiIp
     * @return
     */
    boolean resendPolicy(long topTaskId,long messageNo, List<String> dpiIp);

    /**
     * 重名判断
     * @param userGroup
     * @return
     */
    boolean existSameGroupName(UserGroup userGroup);

    /**
     * 手工添加用户组信息
     * @param userGroupStrategy
     * @return
     */
    void manualAddOrUpdateUserGroup(UserGroupStrategy userGroupStrategy, BaseEntity baseEntity);

    /**
     * 获取dpi链路用户信息
     * @return
     */
    List<DpiStaticPort> getLinkUser();

    /**
     * 获取bras链路用户信息
     * @return
     */
    List<Bras> getBrasUser();

}

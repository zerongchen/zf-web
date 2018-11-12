package com.aotain.zongfen.service.general.userbind;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.DdosExceptFlowStrategy;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.mapper.general.userbind.UserBindMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.general.userbind.UserBindModel;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.userbind.UserPolicyBindService;
import com.aotain.zongfen.utils.SpringUtil;
import com.google.common.collect.Maps;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class UserBindServiceImpl extends BaseService implements UserBindService {

    private static Logger LOG = LoggerFactory.getLogger(UserBindServiceImpl.class);

    @Autowired
    private UserBindMapper userBindMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;

    @Autowired
    private UserPolicyBindService userPolicyBindService;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Override
    public List<String> addUserBind(HttpServletRequest request) throws Exception {
        List<String> messageNos= new ArrayList<>();

        String userType = request.getParameter("userType");
        String userName = request.getParameter("userName");
        String userGroupId = request.getParameter("userGroupId");
        String addbindMessageType = request.getParameter("addbindMessageType");
        String addbindMessageNo = request.getParameter("addbindMessageNo");
        List<String> addbindMessageTypeList=null;
        List<String> addbindMessageNoList = null;
        if(!StringUtils.isEmpty(addbindMessageType)&&!StringUtils.isEmpty(addbindMessageNo)){
            addbindMessageTypeList = Arrays.asList(addbindMessageType.split(","));
            addbindMessageNoList = Arrays.asList(addbindMessageNo.split(","));
        }
        List<UserBindModel> lists = new ArrayList<UserBindModel>();
        for (int i = 0; i < addbindMessageTypeList.size(); i++) {
            UserBindModel model = new UserBindModel();
            model.setUserType(Integer.valueOf(userType));
            model.setUserName(userName);
            model.setUserGroupId(userGroupId);
            model.setBindMessageType(Integer.valueOf(addbindMessageTypeList.get(i)));
            model.setBindMessageNo(Long.valueOf(addbindMessageNoList.get(i)));
            model.setCreateOper(SpringUtil.getSysUserName());
            model.setCreateTime(new Date());
            model.setModifyOper(SpringUtil.getSysUserName());
            model.setModifyTime(new Date());
            lists.add(model);
        }
        List<UserPolicyBindStrategy> userPolicyBindStrategyAllList = new ArrayList<>();

        for (UserBindModel m : lists) {
            List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(m);
            userPolicyBindStrategyAllList.addAll(userPolicyBindStrategyList);
        }

        for (int i = 0; i < userPolicyBindStrategyAllList.size(); i++) {
            UserPolicyBindStrategy v = userPolicyBindStrategyAllList.get(i);
            userPolicyBindService.addPolicy(v);
            messageNos.add(v.getMessageNo()+"");
        }

        return messageNos;
    }

    @Override
    public boolean deleteBindPolicy(String[] bindIds, String[] bindMessageTypes, String[] bindMessageNos) {
        try {
            for (int i = 0; i < bindIds.length; i++) {
                deleteExistBindPolicy(bindIds[i], bindMessageTypes[i], bindMessageNos[i], true);
            }
        } catch (Exception e) {
            LOG.error("",e);
            return false;
        }
        return true;
    }

    /**
     * @param sendStrategyChannel 是否发送通道
     */
    private void deleteExistBindPolicy(String bindId, String bindMessageType, String bindMessageNo, boolean sendStrategyChannel) {
        try {
            Map<String, Object> queryCondition = Maps.newHashMap();
            queryCondition.put("bindId", bindId);
            UserPolicyBindStrategy userPolicyBindStrategy = userBindMapper.getByBindMessageNoAndType(queryCondition);

            List<BindMessage> bindMessages = new ArrayList<>();
            BindMessage bindMessage = new BindMessage();
            bindMessage.setBindMessageNo(Long.valueOf(bindMessageNo));
            bindMessage.setBindMessageType(Integer.valueOf(bindMessageType));
            bindMessages.add(bindMessage);
            userPolicyBindStrategy.setBindInfo(bindMessages);
            if (sendStrategyChannel) {
                userPolicyBindService.deletePolicy(userPolicyBindStrategy);
            } else {
                userPolicyBindService.deleteDb(userPolicyBindStrategy);
            }
        } catch (NumberFormatException e) {
            LOG.error("", e);
        }
    }

    @Override
    public List<UserBindModel> getUserGroupList(UserBindModel userBindModel) {
        List<UserBindModel> result = userBindMapper.getUserBindList(userBindModel);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i = 0; i < result.size(); i++) {
            UserBindModel po = result.get(i);
            PolicyStatus ddosPolicy = new PolicyStatus();
            if (po.getMessageNo() != null) {
                PolicyStatus query2 = new PolicyStatus();
                query2.setMessageNo(po.getMessageNo());
                query2.setMessageType(MessageTypeConstants.MESSAGE_TYPE_USER_BIND_POLICY);
                ddosPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if (ddosPolicy != null) {
                po.setAppPolicy(ddosPolicy.getMainCount() == null ? "0/0" : ddosPolicy.getMainCount());
            } else {
                po.setAppPolicy("0/0");
            }
        }
        return result;
    }

    /**
     * @param
     * @return
     */
    private List<UserPolicyBindStrategy> createUserPolicyBindBeanByBaseVo(UserBindModel m) {
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = new ArrayList<>();

        if (m.getUserType() == 0) {
            // 全用户
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            // 将userName设置为空字符串
            userPolicyBindStrategy.setUserName("");
            copyProperties(m, userPolicyBindStrategy);
            userPolicyBindStrategyList.add(userPolicyBindStrategy);

        } else if (m.getUserType() == 1 || m.getUserType() == 2) {
            // 账号或者IP地址
            for (int i = 0; i < m.getUserName().split(",").length; i++) {
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserName(m.getUserName().split(",")[i]);
                copyProperties(m, userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }
        } else if (m.getUserType() == 3) {
            // 用户组
            for (int i = 0; i < m.getUserGroupId().toString().split(",").length; i++) {
                long userGroupId = Long.valueOf(m.getUserGroupId().toString().split(",")[i]);
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserGroupId(userGroupId);
                userPolicyBindStrategy.setUserName(commonService.getUserGroupName(userGroupId));
                copyProperties(m, userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }
        }
        return userPolicyBindStrategyList;
    }

    /**
     * 赋值相关属性
     *
     * @param userPolicyBindStrategy
     */
    private void copyProperties(UserBindModel m, UserPolicyBindStrategy userPolicyBindStrategy) {
        userPolicyBindStrategy.setUserBindMessageNo(m.getBindMessageNo());
        userPolicyBindStrategy.setUserBindMessageType(m.getBindMessageType());
        userPolicyBindStrategy.setUserType(m.getUserType());

        List<BindMessage> bindMessages = new ArrayList<>();
        BindMessage bindMessage = new BindMessage();
        bindMessage.setBindMessageNo(m.getBindMessageNo());
        bindMessage.setBindMessageType(m.getBindMessageType());
        bindMessages.add(bindMessage);
        userPolicyBindStrategy.setBindInfo(bindMessages);

        if (!StringUtils.isEmpty(m.getCreateOper())) {
            userPolicyBindStrategy.setCreateOper(m.getCreateOper());
        }
        if (!StringUtils.isEmpty(m.getModifyOper())) {
            userPolicyBindStrategy.setModifyOper(m.getModifyOper());
        }
        if (m.getCreateTime() != null) {
            userPolicyBindStrategy.setCreateTime(m.getCreateTime());
        }
        if (m.getModifyTime() != null) {
            userPolicyBindStrategy.setModifyTime(m.getModifyTime());
        }
    }

    @Override
    public boolean resendPolicy(long topTaskId, long bindId, List<String> dpiIp) {

        // 重发关联的绑定策略
        Map<String, Object> queryMap = new HashedMap();
        queryMap.put("bindId", bindId);
        UserPolicyBindStrategy userPolicyBindStrategy = userBindMapper.getByBindMessageNoAndType(queryMap);

        manualRetryPolicy(topTaskId, 0, MessageType.USER_POLICY_BIND.getId(),
                userPolicyBindStrategy.getMessageNo(), dpiIp);
        return true;
    }

    @Override
    public List<Multiselect> getbindMessageName(Long bindMessageType) {
        return userBindMapper.getbindMessageName(bindMessageType);
    }


    @Override
    protected boolean addDb(BaseVO policy) {
        return false;
    }

    @Override
    protected boolean deleteDb(BaseVO policy) {
        return false;
    }

    @Override
    protected boolean modifyDb(BaseVO policy) {
        return false;
    }

    @Override
    protected boolean addCustomLogic(BaseVO policy) {
        return false;
    }

    @Override
    protected boolean modifyCustomLogic(BaseVO policy) {
        return false;
    }

    @Override
    protected boolean deleteCustomLogic(BaseVO policy) {
        return false;
    }
}

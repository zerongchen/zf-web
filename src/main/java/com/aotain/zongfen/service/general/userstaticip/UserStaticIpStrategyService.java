package com.aotain.zongfen.service.general.userstaticip;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.BindAction;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.UserStaticIpStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.IpMsg;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.dto.general.user.IpUserDTO;
import com.aotain.zongfen.mapper.general.user.IpUserMapper;
import com.aotain.zongfen.mapper.general.user.UserStaticIPMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.model.general.user.IpUser;
import com.aotain.zongfen.model.general.user.UserStaticIP;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class UserStaticIpStrategyService extends BaseService {

    private static final Logger LOG = LoggerFactory.getLogger(UserStaticIpStrategyService.class);
    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private IpUserMapper ipUserMapper;

    @Autowired
    private UserStaticIPMapper userStaticIPMapper;


    /**
     *
     * @param staticIP
     * @param ipMsg
     * @param userId
     * @param messageSqNo
     * @param operationType
     */
    private void initStaticIpOIbj(UserStaticIP staticIP, IpMsg ipMsg,Long userId,Long messageSqNo ,Integer operationType){
        Date time = new Date();
        staticIP.setUserid(userId);
        staticIP.setUserip(ipMsg.getUserIp());
        staticIP.setUseripPrefix(ipMsg.getUserIpPrefix());
        staticIP.setMessageSequenceno(messageSqNo);
        staticIP.setCreateOper(SpringUtil.getSysUserName());
        staticIP.setModifyOper(SpringUtil.getSysUserName());
        staticIP.setCreateTime(time);
        staticIP.setModifyTime(time);
        staticIP.setOperateType(operationType);
    }

    @Override
    protected boolean addDb( BaseVO vo ) {
        vo.setOperationType(OperationConstants.OPERATION_SAVE);
        if(vo instanceof UserStaticIpStrategy) {

            UserStaticIpStrategy staticIpStrategy = (UserStaticIpStrategy)vo;
            String userName =staticIpStrategy.getUserName().trim();

            IpUserDTO dto = new IpUserDTO();
            dto.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
            dto.setUserName(userName);
            IpUser ipUser = ipUserMapper.selectOne(dto);

            Long userId = null;
            Long messageNo = null;
            long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.USER_STATICIP_POLICY.getId());

            Policy policy = new Policy();
            policy.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
            Date time = new Date();
            policy.setModifyTime(time);
            policy.setModifyOper(SpringUtil.getSysUserName());
            policy.setMessageName(userName);
            policy.setMessageSequenceno(messageSqNo);

            if(ipUser!=null){
                userId = ipUser.getUserId();
                messageNo = ipUser.getMessageNo();
                policy.setMessageNo(messageNo);
                policy.setOperateType(2);
                policyMapper.updatePolicyByMessageNoAndType(policy);
            }else {
                messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.USER_STATICIP_POLICY.getId());
                policy.setCreateTime(time);
                policy.setCreateOper(SpringUtil.getSysUserName());
                policy.setMessageNo(messageNo);
                policy.setOperateType(1);
                policyMapper.insertSelective(policy);
                IpUser user = new IpUser(messageNo,userName);
                ipUserMapper.insertSelective(user);
                userId = user.getUserId();
            }
            List<IpMsg> list =  staticIpStrategy.getUserIpInfo();
            List<UserStaticIP> ips = new ArrayList<UserStaticIP>();
            for (IpMsg ipMsg:list){
                UserStaticIP staticIP = new UserStaticIP();
                initStaticIpOIbj(staticIP,ipMsg,userId,messageSqNo,1);
                ips.add(staticIP);
            }
            userStaticIPMapper.insertSelectiveList(ips);
            vo.setProbeType(ProbeType.DPI.getValue());
            staticIpStrategy.setBindAction(BindAction.BIND.getValue());
            staticIpStrategy.setMessageNo(policy.getMessageNo());
            staticIpStrategy.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
            staticIpStrategy.setMessageSequenceNo(messageSqNo);
            staticIpStrategy.setMessageName(staticIpStrategy.getUserName());

            return true;
        }
        return false;

    }

    @Override
    protected boolean deleteDb( BaseVO baseVO ) {
        if(baseVO instanceof UserStaticIpStrategy) {

            UserStaticIpStrategy staticIpStrategy = (UserStaticIpStrategy)baseVO;
//            IpUser ipUser = ipUserMapper.selectByPrimaryKey(staticIpStrategy.getUserid());
            Long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.USER_STATICIP_POLICY.getId());
            Policy policy = new Policy();
            policy.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
            policy.setMessageSequenceno(messageSqNo);
            policy.setMessageNo(staticIpStrategy.getMessageNo());
            Date time = new Date();
            policy.setModifyTime(time);
            policy.setModifyOper(SpringUtil.getSysUserName());
            policy.setOperateType(3);
            policyMapper.updatePolicyByMessageNoAndType(policy);

            List<IpMsg> list =  staticIpStrategy.getUserIpInfo();
            LOG.info("[IP address user info] messageNo="+staticIpStrategy.getMessageNo()+",userId="+staticIpStrategy.getUserId()+",userIpInfo="+list);
            try {
                if(list!=null && list.size()>0){
                    List<UserStaticIP> ips = new ArrayList<UserStaticIP>();
                    for (IpMsg ipMsg:list){
                        UserStaticIP staticIP = new UserStaticIP();
                        initStaticIpOIbj(staticIP,ipMsg,staticIpStrategy.getUserId(),messageSqNo,3);
                        ips.add(staticIP);
                    }
                    userStaticIPMapper.updateOrDelete(ips);
                }
            } catch (Exception e) {
                LOG.error("用户ip地址删除策略,e=",e);
            }
            staticIpStrategy.setProbeType(ProbeType.DPI.getValue());
            staticIpStrategy.setBindAction(BindAction.UNBIND.getValue());
            staticIpStrategy.setOperationType(2);
            staticIpStrategy.setMessageNo(policy.getMessageNo());
            staticIpStrategy.setMessageSequenceNo(messageSqNo);
            staticIpStrategy.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
            staticIpStrategy.setMessageName(staticIpStrategy.getUserName());

            return true;
        }
        return false;
    }

    /**
     * 修改删除
     * @param baseVO
     * @return
     */
    @Override
    protected boolean modifyDb( BaseVO baseVO ) {


        if(baseVO instanceof UserStaticIpStrategy) {

            UserStaticIpStrategy staticIpStrategy = (UserStaticIpStrategy)baseVO;
//            IpUser ipUser = ipUserMapper.selectByPrimaryKey(staticIpStrategy.getUserid());
            Long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.USER_STATICIP_POLICY.getId());
            Policy policy = new Policy();
            policy.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
            policy.setMessageSequenceno(messageSqNo);
            policy.setMessageNo(staticIpStrategy.getMessageNo());
            Date time = new Date();
            policy.setModifyTime(time);
            policy.setModifyOper(SpringUtil.getSysUserName());
            policy.setOperateType(2);
            policyMapper.updatePolicyByMessageNoAndType(policy);

            List<IpMsg> list =  staticIpStrategy.getUserIpInfo();
            List<UserStaticIP> ips = new ArrayList<UserStaticIP>();
            for (IpMsg ipMsg:list){
                UserStaticIP staticIP = new UserStaticIP();
                initStaticIpOIbj(staticIP,ipMsg,staticIpStrategy.getUserId(),messageSqNo,3);
                ips.add(staticIP);
            }
            userStaticIPMapper.updateOrDelete(ips);
            staticIpStrategy.setProbeType(ProbeType.DPI.getValue());
            staticIpStrategy.setBindAction(BindAction.UNBIND.getValue());
            staticIpStrategy.setOperationType(2);
//            staticIpStrategy.setMessageNo(policy.getMessageNo());
            staticIpStrategy.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
            staticIpStrategy.setMessageSequenceNo(messageSqNo);
            staticIpStrategy.setMessageName(staticIpStrategy.getUserName());
            return true;
        }
        return false;
    }

    @Override
    protected boolean addCustomLogic( BaseVO baseVO ) {
        return setPolicyOperateSequenceToRedis(baseVO) && addTaskAndChannelToRedis(baseVO);
    }

    @Override
    protected boolean modifyCustomLogic( BaseVO baseVO ) {
        return setPolicyOperateSequenceToRedis(baseVO) && addTaskAndChannelToRedis(baseVO);
    }

    @Override
    protected boolean deleteCustomLogic( BaseVO baseVO ) {

        if(baseVO instanceof UserStaticIpStrategy){
            UserStaticIpStrategy staticIpStrategy = (UserStaticIpStrategy)baseVO;
            List<IpMsg> list =  staticIpStrategy.getUserIpInfo();
            if(list!=null && list.size()>0){
                return setPolicyOperateSequenceToRedis(baseVO) && addTaskAndChannelToRedis(baseVO);
            }
        }
        return true;
    }
}

package com.aotain.zongfen.service.general.userstaticip;

import java.io.*;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.utils.redis.AlarmClassInfoUtil;
import com.aotain.common.utils.tools.FileUtils;
import com.aotain.common.utils.tools.Tools;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.general.ZfV2GenIpImporttaskMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.general.GenIPAddress;
import com.aotain.zongfen.model.general.ZfV2GenIpImporttask;
import com.aotain.zongfen.model.general.user.UserGroup;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.general.ipaddress.impl.IPAddressServiceImpl;
import com.aotain.zongfen.validate.dataImport.general.IpAddressImportMgr;
import com.google.common.collect.Maps;
import org.apache.commons.collections.map.HashedMap;
import org.kohsuke.rngom.parse.host.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.model.UserStaticIpStrategy;
import com.aotain.common.policyapi.model.msg.IpMsg;
import com.aotain.zongfen.dto.general.user.IpUserDTO;
import com.aotain.zongfen.dto.general.user.UserStaticIPDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.mapper.general.user.IpUserMapper;
import com.aotain.zongfen.mapper.general.user.UserStaticIPMapper;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.general.user.UserStaticIP;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.validate.dataImport.general.UserStaticIpImportMgr;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

@Service
public class UserStaticIpServiceImpl extends BaseService implements UserStaticIpService {

    private static final Logger LOG = LoggerFactory.getLogger(IPAddressServiceImpl.class);


    @Autowired
    private UserStaticIPMapper userStaticIPMapper;

    @Autowired
    private IpUserMapper ipUserMapper;

    @Autowired
    private UserStaticIpStrategyService userStaticIpStrategyService;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Autowired
    private ZfV2GenIpImporttaskMapper zfV2GenIpImporttaskMapper;

    @Autowired
    private UserStaticIpImportMgr userStaticIpImportMgr;

    private static String filePath = System.getProperty("user.dir") + File.separator + "ipimportfile";

    @Override
    public List<IpUserDTO> getList( IpUserDTO recode ) {
        recode.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
        List<IpUserDTO> lists = ipUserMapper.getList(recode);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<lists.size();i++){
            IpUserDTO ipUserDTO1 = lists.get(i);
            PolicyStatus appPolicy = new PolicyStatus();
            if(ipUserDTO1.getMessageNo()!=null) {
                PolicyStatus query2 = new PolicyStatus();
                query2.setMessageNo(ipUserDTO1.getMessageNo());
                query2.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
                appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(appPolicy!=null) {
                ipUserDTO1.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
            }else {
                ipUserDTO1.setAppPolicy("0/0");
            }
        }
        return  lists;
    }

    @Override
    public ResponseResult deleteByUserIds( String[] ids ) {
        int len =ids.length;
        if(len<1){
            return new ResponseResult();
        }
        ResponseResult responseResult  = new ResponseResult();
        responseResult.setMessage("success");
        responseResult.setResult(1);
        List<BaseKeys> keys = new ArrayList<>();
        BaseKeys bk = new BaseKeys();
        bk.setOperType(OperationType.DELETE.getType());
        bk.setMessageType(ModelType.MODEL_USER_STATICIP.getMessageType());
        bk.setDataType(DataType.OTHER.getType());
        for (int i = 0 ; i < len ; i++){
            IpUserDTO ip = new IpUserDTO();
            ip.setUserId(Long.valueOf(ids[i]));
            ip.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
            List<IpUserDTO> list = ipUserMapper.getList(ip);
            if(list==null || list.size()<1) break;
            IpUserDTO dto = list.get(0);

            UserStaticIpStrategy staticIpStrategy = new UserStaticIpStrategy();
            staticIpStrategy.setUserName(dto.getUserName());
            staticIpStrategy.setMessageNo(dto.getMessageNo());
            staticIpStrategy.setUserIpInfo(dto.getUserInfo());
            staticIpStrategy.setUserId(dto.getUserId());
            userStaticIpStrategyService.deletePolicy(staticIpStrategy);
            bk.setMessage("messageNo="+staticIpStrategy.getMessageNo());
            keys.add(bk);
        }
        responseResult.setKeys(keys);
        return responseResult;
    }

    @Override
    public ResponseResult deleteByIpIds( String[] ids ) {
        int len =ids.length;
        if(len<1){
            return new ResponseResult();
        }
        ResponseResult responseResult  = new ResponseResult();
        responseResult.setMessage("success");
        responseResult.setResult(1);
        List<BaseKeys> keys = new ArrayList<>();
        BaseKeys bk = new BaseKeys();
        bk.setOperType(OperationType.DELETE.getType());
        bk.setMessageType(ModelType.MODEL_USER_STATICIP.getMessageType());
        bk.setDataType(DataType.OTHER.getType());

        for (int i = 0 ; i < len ; i++){
            UserStaticIPDTO userStaticIPDTO = userStaticIPMapper.selectOne(Long.valueOf(ids[i]));
            IpMsg ipMsg = new IpMsg();
            ipMsg.setUserIp(userStaticIPDTO.getUserip());
            ipMsg.setUserIpPrefix(userStaticIPDTO.getUseripPrefix());
            UserStaticIpStrategy staticIpStrategy = new UserStaticIpStrategy();
            staticIpStrategy.setMessageNo(userStaticIPDTO.getMessageNo());
            staticIpStrategy.setUserName(userStaticIPDTO.getUserName());
            staticIpStrategy.setUserIpInfo(Arrays.asList(ipMsg));
            userStaticIpStrategyService.modifyPolicy(staticIpStrategy);
            bk.setMessage("messageNo="+staticIpStrategy.getMessageNo());
            keys.add(bk);
        }
        responseResult.setKeys(keys);
        return responseResult;
    }

    @Override
    public ResponseResult insert( UserStaticIPDTO record , Integer operateType) {

        ResponseResult responseResult = new ResponseResult();
        String[] userips = record.getUserips();
        Integer[] userPrefs = record.getUseripPrefixs();
        ArrayList<UserStaticIP> newAdd = new ArrayList<UserStaticIP>();
        List<UserStaticIP> exits = userStaticIPMapper.getExitList(new UserStaticIP());
        boolean isExit = false;
        loop1:for (int i=0;i<userips.length;i++){
            newAdd.add(new UserStaticIP(userips[i],userPrefs[i]));
            if(exits.size()<1) break;
            loop2:for (UserStaticIP ip:exits){
                if(userips[i].equals(ip.getUserip()) && userPrefs[i] == ip.getUseripPrefix()){
                    isExit=true;
                    break loop1;
                }
            }
            if(!isExit){
                exits.add(new UserStaticIP(userips[i],userPrefs[i]));
            }
        }
        if(isExit){
            responseResult.setResult(0);
            responseResult.setMessage("IP+前缀 存在重复");
            return responseResult;
        }
        IpUserDTO dto = new IpUserDTO();
        dto.setUserName(record.getUserName().trim());
        List<IpMsg> ipMsgs = new ArrayList<IpMsg>();
        List<BaseKeys> keys = new ArrayList<BaseKeys>();
        for (UserStaticIP userStaticIP : newAdd){
            IpMsg ipMsg = new IpMsg();
            ipMsg.setUserIp(userStaticIP.getUserip());
            ipMsg.setUserIpPrefix(userStaticIP.getUseripPrefix());
            ipMsgs.add(ipMsg);

        }
        BaseKeys bk = new BaseKeys();
        bk.setOperType(OperationType.CREATE.getType());
        bk.setMessageType(ModelType.MODEL_USER_STATICIP.getMessageType());
        bk.setDataType(DataType.OTHER.getType());

        UserStaticIpStrategy staticIpStrategy = new UserStaticIpStrategy();
        staticIpStrategy.setUserName(dto.getUserName().trim());
        staticIpStrategy.setUserIpInfo(ipMsgs);
        userStaticIpStrategyService.addPolicy(staticIpStrategy);
        bk.setMessage("messageNo="+staticIpStrategy.getMessageNo());
        keys.add(bk);
        responseResult.setKeys(keys);
        responseResult.setResult(1);
        responseResult.setMessage("新增成功");
        return responseResult;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
    public ResponseResult update( UserStaticIP record ) {

        UserStaticIP ip = new UserStaticIP();
        List<UserStaticIP> lists = userStaticIPMapper.getExitList(ip);
        boolean isExit = false;
        if(lists!=null && lists.size()>0 ){
            for (UserStaticIP userStaticIP :lists){
                if(record.getUserip().equals(userStaticIP.getUserip()) && record.getUseripPrefix() == userStaticIP.getUseripPrefix()){
                    isExit = true;
                    break;
                }
            }
        }
        ResponseResult<List<UserStaticIPDTO>> responseResult = new ResponseResult();
        if(isExit){
            responseResult.setResult(0);
            responseResult.setMessage("该IP+前缀 已经存在,若不修改,请点击取消");
            return responseResult;
        }
        List<BaseKeys> keys = new ArrayList<BaseKeys>();
        //解绑存在的IP
        UserStaticIPDTO exitOne = userStaticIPMapper.selectOne(record.getIpId());
        IpMsg ipMsg = new IpMsg();
        ipMsg.setUserIp(exitOne.getUserip());
        ipMsg.setUserIpPrefix(exitOne.getUseripPrefix());
        UserStaticIpStrategy staticIpStrategy = new UserStaticIpStrategy();
        staticIpStrategy.setMessageNo(exitOne.getMessageNo());
        staticIpStrategy.setUserName(exitOne.getUserName());
        staticIpStrategy.setUserIpInfo(Arrays.asList(ipMsg));
        userStaticIpStrategyService.modifyPolicy(staticIpStrategy);

        BaseKeys bk = new BaseKeys();
        bk.setOperType(OperationType.MODIFY.getType());
        bk.setMessageType(ModelType.MODEL_USER_STATICIP.getMessageType());
        bk.setDataType(DataType.OTHER.getType());
        bk.setMessage("messageNo="+staticIpStrategy.getMessageNo());
        keys.add(bk);

        //新增修改后的IP
        IpMsg msg = new IpMsg();
        msg.setUserIp(record.getUserip());
        msg.setUserIpPrefix(record.getUseripPrefix());
        UserStaticIpStrategy strategy = new UserStaticIpStrategy();
        strategy.setUserName(exitOne.getUserName());
        strategy.setUserIpInfo(Arrays.asList(msg));
        userStaticIpStrategyService.addPolicy(strategy);
        List<UserStaticIPDTO> ipdtos =  userStaticIPMapper.getUserIpInfo(record.getUserid());

        responseResult.setResult(1);
        responseResult.setMessage("修改成功");
        responseResult.setData(ipdtos);
        responseResult.setKeys(keys);
        return responseResult;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
    public ImportResultList handleImport( HttpServletRequest request) throws ImportException {

        int operate = Integer.parseInt(request.getParameter("operate"));

        InputStream inputStream = ExcelUtil.getInputStream(request);
        UserStaticIpImportMgr userStaticIpImportMgr = getUserStaticIpImportMgr(operate);
        Map<Integer,Map<Integer, String[]>> map = userStaticIpImportMgr.readDataFromStream(inputStream);
        userStaticIpImportMgr.validate(map);
        Map<String,List> dataMap = userStaticIpImportMgr.getImportResultList().getDatas();
//        userStaticIPMapper.insertList(userStaticIpImportMgr.getImportResultList().getDatas().get("userStaticIp"));
        //全量导入
        if(operate == 0){
            IpUserDTO dto = new IpUserDTO();
            dto.setMessageType(MessageType.USER_STATICIP_POLICY.getId());
            List<IpUserDTO> list = ipUserMapper.getList(dto);
            for (IpUserDTO ipUserDTO:list){
                UserStaticIpStrategy staticIpStrategy = new UserStaticIpStrategy();
                staticIpStrategy.setUserName(ipUserDTO.getUserName());
                staticIpStrategy.setMessageNo(ipUserDTO.getMessageNo());
                staticIpStrategy.setUserIpInfo(ipUserDTO.getUserInfo());
                staticIpStrategy.setUserId(ipUserDTO.getUserId());
                userStaticIpStrategyService.deletePolicy(staticIpStrategy);
            }
        }

        for (String name:dataMap.keySet()){
            List<IpMsg> list = dataMap.get(name);
            UserStaticIpStrategy staticIpStrategy = new UserStaticIpStrategy();
            staticIpStrategy.setUserName(name);
            staticIpStrategy.setUserIpInfo(list);
            userStaticIpStrategyService.addPolicy(staticIpStrategy);
        }
        return userStaticIpImportMgr.getImportResultList();
    }

    private synchronized UserStaticIpImportMgr getUserStaticIpImportMgr(int operate){
        UserStaticIpImportMgr userStaticIpImportMgr = (UserStaticIpImportMgr) SpringUtil.getBean("userStaticIpImportMgr");
        userStaticIpImportMgr.setOperate(operate);
        return userStaticIpImportMgr;
    }

    @Override
    public Map<String, String> uploadFile(HttpServletRequest request) {
        Map<String, String> retMap = Maps.newHashMap();
        List<ZfV2GenIpImporttask> allTask = zfV2GenIpImporttaskMapper.selectAll();
        for (ZfV2GenIpImporttask task : allTask) {
            if (0 == task.getStatus()) {
                retMap.put("status", "1");
                retMap.put("message", "已经存在导入任务。");
                return retMap;
            }
        }
        MultipartFile file = ((MultipartRequest) request).getFile("importFile");
        String getOriginalFilename = file.getOriginalFilename();
        String suffix= getOriginalFilename.substring(getOriginalFilename.lastIndexOf("."));
        String saveFileName = getOriginalFilename.substring(0,getOriginalFilename.lastIndexOf("."))+"_"+System.currentTimeMillis()/1000+suffix;
        try {
            //获取输出流
            OutputStream os = new FileOutputStream(new File(filePath, saveFileName));
            //获取输入流CommonsMultipartFile中可以直接得到文件的流
            InputStream is = file.getInputStream();
            int temp;
            //一个一个字节的读取并写入
            while ((temp = is.read()) != (-1)) {
                os.write(temp);
            }
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            LOG.error("", e);
            retMap.put("status", "2");
            retMap.put("message", "保存导入文件失败。");
            return retMap;
        }
        try {
            String type = request.getParameter("operate");
            String fileName = saveFileName;
            int status = 0;
            String resultFile = fileName + "_result";
            int userId = 0;
            Date createDate = new Date();

            ZfV2GenIpImporttask task = new ZfV2GenIpImporttask();
            task.setCreateTime(createDate);
            task.setFilename(fileName);
            task.setImportType(type);
            task.setResultFile(resultFile);
            task.setStatus(status);
            task.setUserid(userId);
            task.setServerIp(Tools.getHostAddressAndIp());
            zfV2GenIpImporttaskMapper.insert(task);
       //     new Thread(new IPAddressServiceImpl.IpAddressThread()).start();
        } catch (Exception e) {
            LOG.error("", e);
            retMap.put("status", "3");
            retMap.put("message", "添加导入任务失败。");
            return retMap;
        }
        retMap.put("status", "");
        retMap.put("message", "添加导入任务成功。");
        return retMap;
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

    @Override
    public boolean resendPolicy(long l, Long messageNo, boolean b, String s) {
        // 重发主策略
        manualRetryPolicy(0L,0,MessageType.USER_STATICIP_POLICY.getId(),messageNo,null);
        // 重发关联的绑定策略

        return true;
    }
}

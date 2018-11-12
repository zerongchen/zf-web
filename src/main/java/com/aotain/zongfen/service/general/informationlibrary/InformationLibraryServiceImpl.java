package com.aotain.zongfen.service.general.informationlibrary;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.TriggerHostName;
import com.aotain.common.policyapi.model.TriggerHostStrategy;
import com.aotain.common.policyapi.model.TriggerKwName;
import com.aotain.common.policyapi.model.TriggerKwStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.general.TriggerHostDTO;
import com.aotain.zongfen.dto.general.TriggerHostListDTO;
import com.aotain.zongfen.dto.general.TriggerKWDTO;
import com.aotain.zongfen.dto.general.TriggerKWListDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.mapper.general.*;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.model.BaseEntity;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.general.TriggerHost;
import com.aotain.zongfen.model.general.TriggerHostList;
import com.aotain.zongfen.model.general.TriggerKW;
import com.aotain.zongfen.model.general.TriggerKWList;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.common.PolicyService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.validate.dataImport.general.InformationPushImportMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class InformationLibraryServiceImpl extends BaseService implements InformationLibraryService {

    private static final Logger logger = LoggerFactory.getLogger(InformationLibraryServiceImpl.class);

    private static int triggerHostMessageType = MessageTypeConstants.MESSAGE_TYPE_TRIGGER_HOST;

    private static int triggerKWMessageType = MessageTypeConstants.MESSAGE_TYPE_TRIGGER_KEY_WORDS;

    @Autowired
    private TriggerHostMapper triggerHostMapper;

    @Autowired
    private TriggerHostListMapper triggerHostListMapper;

    @Autowired
    private TriggerKWMapper triggerKWMapper;

    @Autowired
    private TriggerKWListMapper triggerKWListMapper;

    @Autowired
    private TriggerRelationMapper triggerRelationMapper;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private PolicyMapper policyMapper;


    @Override
    protected boolean addDb(BaseVO policy) {
        return true;
    }

    @Override
    protected boolean deleteDb(BaseVO policy) {
        return true;
    }

    @Override
    protected boolean modifyDb(BaseVO policy) {
        return true;
    }

    @Override
    protected boolean addCustomLogic(BaseVO policy) {
        return sendRedisMessage(policy);
    }

    @Override
    protected boolean modifyCustomLogic(BaseVO policy) {
        return sendRedisMessage(policy);
    }

    @Override
    protected boolean deleteCustomLogic(BaseVO policy) {
        return sendRedisMessage(policy);
    }

    @Override
    public List<TriggerHostDTO> getTriggerHost( TriggerHost triggerHost ) {
        List<TriggerHostDTO> result = triggerHostMapper.getTriggerHost(triggerHost);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<result.size();i++){
            TriggerHostDTO triggerHostDTO = result.get(i);
            PolicyStatus appPolicy = new PolicyStatus();
            if(triggerHostDTO.getMessageNo()!=null) {
                PolicyStatus query2 = new PolicyStatus();
                query2.setMessageNo(triggerHostDTO.getMessageNo());
                query2.setMessageType(MessageTypeConstants.MESSAGE_TYPE_TRIGGER_HOST);
                appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(appPolicy!=null) {
                triggerHostDTO.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
            }else {
                triggerHostDTO.setAppPolicy("0/0");
            }

        }
        return result;
    }

    @Override
    public List<TriggerHostListDTO> getTriggerHostList( TriggerHostListDTO triggerHostListDTO ) {

        return triggerHostListMapper.getTriggerHostList(triggerHostListDTO.getTriggerHostListid());
    }

    @Override
    public List<TriggerKWDTO> getTriggerKW( TriggerKW triggerKW ) {
        List<TriggerKWDTO> result = triggerKWMapper.getTriggerKW(triggerKW);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<result.size();i++){
            TriggerKWDTO triggerKWDTO = result.get(i);
            PolicyStatus appPolicy = new PolicyStatus();
            if(triggerKWDTO.getMessageNo()!=null) {
                PolicyStatus query2 = new PolicyStatus();
                query2.setMessageNo(triggerKWDTO.getMessageNo());
                query2.setMessageType(MessageTypeConstants.MESSAGE_TYPE_TRIGGER_KEY_WORDS);
                appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(appPolicy!=null) {
                triggerKWDTO.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
            }else {
                triggerKWDTO.setAppPolicy("0/0");
            }
        }
        return result;
    }

    @Override
    public List<TriggerKWListDTO> getTriggerKWList( TriggerKWListDTO triggerKWListDTO ) {
        return triggerKWListMapper.getTriggerKWList(triggerKWListDTO.getTriggerKwListid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogAnnotation(module = 104001,type = 5)
    public boolean handleImport(HttpServletRequest request, BaseEntity baseEntity) throws ImportException {

        InputStream inputStream = ExcelUtil.getInputStream(request,"importFile");
        String originalFileName = ExcelUtil.getFileName(request,"importFile");

//        originalFileName = originalFileName.substring(0,originalFileName.lastIndexOf("."));

        Integer infoType =Integer.parseInt(request.getParameter("infoType"));
        String listName = request.getParameter("listName");

        long messageNo = 0L;

        if (existSameNameRecord(infoType,listName)) {
            return false;
        }

        InformationPushImportMgr informationPushImportMgr = getInformationPushImportMgr(infoType);
        Map<Integer,Map<Integer, String[]>> maps = informationPushImportMgr.readDataFromStream(inputStream);
        if(maps!=null && maps.size()>0){
            informationPushImportMgr.validate(maps);
            final List list = informationPushImportMgr.getImportResultList().getDatas().get("1");

            //触发关键字
            if(infoType==0){
                TriggerKW triggerKW = new TriggerKW();

                triggerKW.setProbeType(0);
                triggerKW.setMessageType(triggerKWMessageType);
                triggerKW.setMessageNo(MessageNoUtil.getInstance().getMessageNo(triggerKWMessageType));
                triggerKW.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(triggerKWMessageType));
                triggerKW.setCreateOper(baseEntity.getCreateOper());
                triggerKW.setModifyOper(baseEntity.getModifyOper());
                triggerKW.setCreateTime(baseEntity.getCreateTime());
                triggerKW.setModifyTime(baseEntity.getModifyTime());

                triggerKW.setTriggerKwListname(listName);
                triggerKW.setKwNum(Long.valueOf(list.size()));
                triggerKW.setOperationType(OperationConstants.OPERATION_SAVE);


                // addDb
                triggerKWMapper.insertSelective(triggerKW);
                triggerKWListMapper.inserKwList(triggerKW,list);

                TriggerKwStrategy triggerKwStrategy = createStrategyKWBean(triggerKW,list);
                Policy policy = createPolicyBeanByTriggerKwStrategy(triggerKwStrategy,baseEntity);
                policy.setMessageName(listName);
                policyMapper.insert(policy);

                addPolicy(triggerKwStrategy);

                messageNo = triggerKwStrategy.getMessageNo();
            }
            //触发网站 //触发白名单
            if(infoType==1 || infoType==2){

                TriggerHost host = new TriggerHost();
                host.setHostListtype(infoType);

                host.setProbeType(0);
                host.setMessageType(triggerHostMessageType);

                host.setMessageNo(MessageNoUtil.getInstance().getMessageNo(triggerHostMessageType));
                host.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(triggerHostMessageType));

                host.setCreateOper(baseEntity.getCreateOper());
                host.setModifyOper(baseEntity.getModifyOper());
                host.setCreateTime(baseEntity.getCreateTime());
                host.setModifyTime(baseEntity.getModifyTime());
                host.setOperationType(OperationConstants.OPERATION_SAVE);

                host.setTriggerHostListname(listName);
                host.setHostNum(Long.valueOf(list.size()));

                // addDb
                triggerHostMapper.insertSelective(host);
                triggerHostListMapper.insertHostList(host,list);

                TriggerHostStrategy triggerHostStrategy = createStrategyHostBean(host,list);
                Policy policy = createPolicyBeanByTriggerHostStrategy(triggerHostStrategy,baseEntity);
                policy.setMessageName(listName);
                policyMapper.insert(policy);

                addPolicy(triggerHostStrategy);

                messageNo = triggerHostStrategy.getMessageNo();
            }
            //保存导入文件到本地，返回保存的文件名
            String saveFileName =  commonService.saveFile(request, "importFile","informationPush");
            try{
                String dataJson = "type="+infoType+",messageNo="+messageNo+",fileName="+originalFileName+",saveFile="+saveFileName;
                ProxyUtil.changeVariable(InformationLibraryServiceImpl.class,"handleImport",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }

        }
        return true;
    }

    @Override
    public void deleteByType( Integer infoType, String[] ids ) {
        int len = ids.length;
        if(len<1){
            return ;
        }
        final Long[] array = new Long[len];
        for (int i=0;i<len;i++){
            array[i]=Long.valueOf(ids[i]);
        }
        if(infoType == 1 || infoType == 2){
            triggerHostMapper.deleteById(array);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    triggerHostListMapper.deleteByListIds(array);
                }
            }).start();
        }else {
            triggerKWMapper.deleteById(array);
            new Thread(new Runnable() {
                @Override
                public void run() {
                triggerKWListMapper.deleteByListIds(array);
                }
            }).start();
        }
        //删除关系表中trigger 和 Host（kw）之间的关系之前需要获取与被删除列表关联的TriggerID(触发条件的ID),并重新下发
    }

    @Override
    public void deleteList( Integer infoType, String[] ids,Long chooseId) {
        int len = ids.length;
        if(len<1){
            return ;
        }
        Long[] array = new Long[len];
        for (int i=0;i<len;i++){
            array[i]=Long.valueOf(ids[i]);
        }
        if(infoType == 1 || infoType == 2){
            TriggerHost triggerHost = triggerHostMapper.selectByHostListId(chooseId);
            triggerHost.setHostNum(triggerHost.getHostNum()-ids.length);
            triggerHostListMapper.deleteByIds(array);
            triggerHostMapper.updateSelective(triggerHost);
        }else {
            TriggerKW triggerKW = triggerKWMapper.selectByListId(chooseId);
            triggerKW.setKwNum(triggerKW.getKwNum()-ids.length);
            triggerKWListMapper.deleteByIds(array);
            triggerKWMapper.updateSelective(triggerKW);
        }
    }

    @Override
    @Transactional
    public ResponseResult insertOrUpdateHostList( TriggerHostList triggerHostList ) {
        ResponseResult responseResult =  new ResponseResult();

        if(triggerHostList.getHostId()==null){
            int len = triggerHostListMapper.countHostName(triggerHostList);
            if(len>0){
                responseResult.setResult(0);
                responseResult.setMessage("网站已经存在");
                return responseResult;
            }
             triggerHostListMapper.insertSelective(triggerHostList);
        }else {

            int len = triggerHostListMapper.countHostName(triggerHostList);
            if(len>0){
                responseResult.setResult(0);
                responseResult.setMessage("网站已经存在，不修改请点击取消");
                return responseResult;
            }
             triggerHostListMapper.deleteById(triggerHostList);
             triggerHostListMapper.insertSelective(triggerHostList);
        }

        //更新列表数量
        Long num = triggerHostListMapper.getNumByListId(triggerHostList.getTriggerHostListid());
        TriggerHost triggerHost = new TriggerHost();
        triggerHost.setHostNum(num);
        triggerHost.setTriggerHostListid(triggerHostList.getTriggerHostListid());
        triggerHostMapper.updateNum(triggerHost);

        responseResult.setResult(1);
        return responseResult;
    }

    @Override
    @Transactional
    public ResponseResult insertOrUpdateKwList( TriggerKWList triggerKWList ) {
        ResponseResult responseResult =  new ResponseResult();
        if(triggerKWList.getKwId()==null){
            int len = triggerKWListMapper.countkwName(triggerKWList);
            if(len>0) {
                responseResult.setResult(0);
                responseResult.setMessage("关键字已经存在");
                return responseResult;
            }
             triggerKWListMapper.insertSelective(triggerKWList);
        }else{

            int len = triggerKWListMapper.countkwName(triggerKWList);
            if(len>0) {
                responseResult.setResult(0);
                responseResult.setMessage("关键字已经存在，不修改请点击取消");
                return responseResult;
            }
            triggerKWListMapper.deleteById(triggerKWList);
            triggerKWListMapper.insertSelective(triggerKWList);
        }
        long num = triggerKWListMapper.getNumByListId(triggerKWList.getTriggerKwListid());
        TriggerKW triggerKW = new TriggerKW();
        triggerKW.setKwNum(num);
        triggerKW.setTriggerKwListid(triggerKWList.getTriggerKwListid());
        triggerKWMapper.updateNum(triggerKW);
        responseResult.setResult(1);
        return responseResult;
    }

    @Override
    public void handReIssuedBaseTypeAndId( Integer infoType, Integer id ) {
        //网站，白名单
        if(infoType == 1 || infoType == 2){
          List<Integer> list = triggerRelationMapper.getRelationTriggerIdByHostListId(id);
          for (int i =0 ;i <list.size();i++){
              sendHostPolicy(list.get(i));
          }
        }else {
            List<Integer> list =  triggerRelationMapper.getRelationTriggerIdByKwListId(id);
            for (int i =0 ;i <list.size();i++){
                sendKwPolicy(list.get(i));
            }
        }
    }

    @Override
    public void resendPolicy(int infoType,long topTaskId,long messageNo,List<String> ips){
        // 重发主策略
        if (ips==null){
            ips = new ArrayList<>();
        }
        if (infoType==0){
            // 触发关键字
            manualRetryPolicy(topTaskId,0,MessageTypeConstants.MESSAGE_TYPE_TRIGGER_KEY_WORDS,messageNo,ips);
        } else if (infoType==1||infoType==2){
            // 触发网站列表
            manualRetryPolicy(topTaskId,0,MessageTypeConstants.MESSAGE_TYPE_TRIGGER_HOST,messageNo,ips);
        }

    }

    @Override
    public int countHostName(TriggerHostList triggerHostList){
        return triggerHostListMapper.countHostName(triggerHostList);
    }

    @Override
    public int countkwName(TriggerKWList triggerKWList){
        return triggerKWListMapper.countkwName(triggerKWList);
    }

    @Override
    public boolean sendPolicyAfterUpdateOrDelete(int infoType,long chooseId){
        if (infoType==0){
            TriggerKW triggerKw = triggerKWMapper.selectByListId(chooseId);
            // 同时修改triggerKw
            triggerKWMapper.updateSelective(triggerKw);
            triggerKw.setModifyTime(new Date());
            List<String> kwNames = triggerKWListMapper.getTriggerKWName(triggerKw);

            triggerKw.setMessageType(triggerKWMessageType);
            triggerKw.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(triggerKWMessageType));
            triggerKw.setProbeType(0);
            triggerKw.setOperationType(1);

            TriggerKwStrategy triggerKwStrategy = createStrategyKWBean(triggerKw,kwNames);
            return addPolicy(triggerKwStrategy);
        } else {
            TriggerHost triggerHost = triggerHostMapper.selectByHostListId(chooseId);
            // 同时修改triggerKw
            triggerHost.setModifyTime(new Date());
            triggerHostMapper.updateSelective(triggerHost);

            List<String> hostNames = triggerHostListMapper.getTriggerHostName(triggerHost);

            triggerHost.setMessageType(triggerHostMessageType);
            triggerHost.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(triggerKWMessageType));
            triggerHost.setProbeType(0);
            triggerHost.setOperationType(1);

            TriggerHostStrategy triggerHostStrategy = createStrategyHostBean(triggerHost,hostNames);
            return addPolicy(triggerHostStrategy);
        }

    }

    @Override
    public boolean existSameNameRecord(int infoType,String name){
        if (infoType==0){
            return triggerKWMapper.selectByListName(name)>0?true:false;
        } else {
            return triggerHostMapper.selectByHostListName(name)>0?true:false;
        }
    }

    /**
     * 根据不同类型返回不同的校验器
     * @param info_type
     * @return
     */
    private InformationPushImportMgr getInformationPushImportMgr( int info_type) {
        InformationPushImportMgr informationPushImportMgr = (InformationPushImportMgr) SpringUtil.getBean("informationPushImportMgr");
        if (informationPushImportMgr != null) {
            // 初始用户类型
            informationPushImportMgr.initInfoTypeParam(info_type);
            // 初始化校验管理器
            informationPushImportMgr.initValidator();
        }
        return informationPushImportMgr;
    }

    /**
     * 触发条件的ID 重发网站，白名单策略
     * @param id
     */
    public void sendHostPolicy(Integer id){
        //此处后续需要调用API
    }
    /**
     * 触发条件的ID 重发关键字策略
     * @param id
     */
    public void sendKwPolicy(Integer id){
        //此处后续需要调用API
    }

    /**
     * 构造发送策略所需类
     * @return
     */
    private TriggerHostStrategy createStrategyHostBean(TriggerHost host,List list){
        TriggerHostStrategy triggerHostStrategy = new TriggerHostStrategy();

        triggerHostStrategy.setMessageNo(host.getMessageNo());
        triggerHostStrategy.setMessageSequenceNo(host.getMessageSequenceNo());
        triggerHostStrategy.setProbeType(host.getProbeType());
        triggerHostStrategy.setMessageType(host.getMessageType());
        if (host.getOperationType()==0){
            triggerHostStrategy.setOperationType(OperationConstants.OPERATION_SAVE);
        } else {
            triggerHostStrategy.setOperationType(host.getOperationType());
        }

        triggerHostStrategy.setTriggerHostListId(host.getTriggerHostListid());
        List<TriggerHostName> triggerHostNames = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            TriggerHostName triggerHostName = new TriggerHostName();
            triggerHostName.setHostName((String)list.get(i));
            triggerHostNames.add(triggerHostName);
        }
        triggerHostStrategy.setHostInfo(triggerHostNames);
        return  triggerHostStrategy;
    }

    /**
     * 构造发送策略所需类
     * @return
     */
    private TriggerKwStrategy createStrategyKWBean(TriggerKW triggerKW,List list){
        TriggerKwStrategy triggerKwStrategy = new TriggerKwStrategy();
        triggerKwStrategy.setMessageNo(triggerKW.getMessageNo());
        triggerKwStrategy.setMessageSequenceNo(triggerKW.getMessageSequenceNo());
        triggerKwStrategy.setProbeType(triggerKW.getProbeType());
        triggerKwStrategy.setMessageType(triggerKW.getMessageType());
        if (triggerKW.getOperationType()==0){
            triggerKwStrategy.setOperationType(OperationConstants.OPERATION_SAVE);
        } else {
            triggerKwStrategy.setOperationType(triggerKW.getOperationType());
        }

        triggerKwStrategy.setTriggerKwListId(triggerKW.getTriggerKwListid());
        List<TriggerKwName> triggerKwNames = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            TriggerKwName triggerKwName = new TriggerKwName();
            triggerKwName.setKwName((String)list.get(i));
            triggerKwNames.add(triggerKwName);
        }
        triggerKwStrategy.setKwInfo(triggerKwNames);
        return triggerKwStrategy;
    }

    /**
     * 根据AppUserStrategy实体构造Policy实体
     * @param triggerHostStrategy
     * @return
     */
    private Policy createPolicyBeanByTriggerHostStrategy(TriggerHostStrategy triggerHostStrategy,BaseEntity baseEntity){
        Policy policyNo = new Policy();
        policyNo.setMessageNo(triggerHostStrategy.getMessageNo());
        policyNo.setMessageSequenceno(triggerHostStrategy.getMessageSequenceNo());
        policyNo.setMessageType(triggerHostStrategy.getMessageType());
        policyNo.setMessageName(triggerHostStrategy.getMessageName()==null?"":triggerHostStrategy.getMessageName());
        policyNo.setOperateType(triggerHostStrategy.getOperationType());
        if ( !StringUtils.isEmpty(baseEntity.getCreateOper()) ){
            policyNo.setCreateOper(baseEntity.getCreateOper());
        }
        if ( !StringUtils.isEmpty(baseEntity.getModifyOper()) ){
            policyNo.setModifyOper(baseEntity.getModifyOper());
        }
        if ( baseEntity.getCreateTime() != null ){
            policyNo.setCreateTime(baseEntity.getCreateTime());
        }
        if ( baseEntity.getModifyTime() != null ){
            policyNo.setModifyTime(baseEntity.getModifyTime());
        }
        return policyNo;
    }

    /**
     * 根据AppUserStrategy实体构造Policy实体
     * @param triggerKwStrategy
     * @return
     */
    private Policy createPolicyBeanByTriggerKwStrategy(TriggerKwStrategy triggerKwStrategy,BaseEntity baseEntity){
        Policy policyNo = new Policy();
        policyNo.setMessageNo(triggerKwStrategy.getMessageNo());
        policyNo.setMessageSequenceno(triggerKwStrategy.getMessageSequenceNo());
        policyNo.setMessageType(triggerKwStrategy.getMessageType());
        policyNo.setMessageName(triggerKwStrategy.getMessageName()==null?"":triggerKwStrategy.getMessageName());
        policyNo.setOperateType(triggerKwStrategy.getOperationType());
        if ( !StringUtils.isEmpty(baseEntity.getCreateOper()) ){
            policyNo.setCreateOper(baseEntity.getCreateOper());
        }
        if ( !StringUtils.isEmpty(baseEntity.getModifyOper()) ){
            policyNo.setModifyOper(baseEntity.getModifyOper());
        }
        if ( baseEntity.getCreateTime() != null ){
            policyNo.setCreateTime(baseEntity.getCreateTime());
        }
        if ( baseEntity.getModifyTime() != null ){
            policyNo.setModifyTime(baseEntity.getModifyTime());
        }
        return policyNo;
    }

}

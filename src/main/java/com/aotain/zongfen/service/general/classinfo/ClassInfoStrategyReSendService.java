package com.aotain.zongfen.service.general.classinfo;

import java.util.List;

import com.aotain.zongfen.mapper.general.ClassFileInfoMapper;
import com.aotain.zongfen.model.general.ClassFileInfo;
import com.aotain.zongfen.model.general.ClassFileInfoKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.FileTypeConstant;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.ClassInfoLibsStrategy;
import com.aotain.common.policyapi.model.HttpGetStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.zongfen.dto.general.FileDetailListDTO;
import com.aotain.zongfen.mapper.device.ZongFenDeviceMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.device.ZongFenDeviceUser;

@Service
public class ClassInfoStrategyReSendService extends BaseService {

    @Autowired
    private ZongFenDeviceMapper zongFenDeviceMapper;

    @Autowired
    private ClassFileInfoMapper classFileInfoMapper;

    private int messageType;
    private int fileType;

    public void setMessageType( int messageType ) {
        this.messageType = messageType;
    }
    public int getMessageType() {
        return messageType;
    }

    public void setFileType( int fileType ) {
        this.fileType = fileType;
    }

    public int getFileType() {
        return fileType;
    }

    public String strategyReSend( FileDetailListDTO detailListDTO){
        ZongFenDevice zongFenDevice = zongFenDeviceMapper.getZongfenDevByPrimary(detailListDTO.getZongfenId());
        if (zongFenDevice ==null) return "综分设备已删除，请重新生成下发！";
        List<ZongFenDeviceUser> list = zongFenDevice.getDeviceUsers();
        String userName = null;
        String passwd = null;
        for (ZongFenDeviceUser deviceUser:list){
            if(deviceUser.getPacketType()==9 && deviceUser.getPacketSubType()==0 ){
                userName = deviceUser.getZongfenFtpUser();
                passwd = deviceUser.getZongfenFtpPwd();
                break;
            }
        }
        setMessageType(detailListDTO.getMessageType());
        if(getMessageType() == MessageType.HTTP_GET_POLICY.getId()) {
            HttpGetStrategy strategy = new HttpGetStrategy();
            //重发sequenceNo不变
            strategy.setMessageSequenceNo(detailListDTO.getMessageSequenceno());
            strategy.setMessageNo(detailListDTO.getMessageNo());
            strategy.setMessageType(MessageType.HTTP_GET_POLICY.getId());
            strategy.setOperationType(OperationConstants.OPERATION_SAVE);
            strategy.setProbeType(ProbeType.DPI.getValue());
            strategy.setIp(zongFenDevice.getZongfenIp());
            strategy.setPort(zongFenDevice.getZongfenFtpPort());
            strategy.setUserName(userName);
            strategy.setPassword(passwd);
            setFileType(detailListDTO.getFileType());

            ClassFileInfoKey key = new ClassFileInfoKey();
            key.setMessageType(MessageType.HTTP_GET_POLICY.getId());
            List<ClassFileInfo> classFileInfos = classFileInfoMapper.selectListByPrimaryKey(key);
            for (ClassFileInfo classFileInfo:classFileInfos){
                if (classFileInfo.getFileType() == (int)FileTypeConstant.HFDB.getValue()){
                        strategy.setHfdbVersionNo(classFileInfo.getVersionNo());
                        strategy.setHfdbFileName(classFileInfo.getClassFileName());
                }else if(classFileInfo.getFileType() == (int)FileTypeConstant.HFDW.getValue()){
                        strategy.setHfdwVersionNo(classFileInfo.getVersionNo());
                        strategy.setHfdwFileName(classFileInfo.getClassFileName());
                }else if(classFileInfo.getFileType() == (int)FileTypeConstant.HFUB.getValue()){
                        strategy.setHfubVersionNo(classFileInfo.getVersionNo());
                        strategy.setHfubFileName(classFileInfo.getClassFileName());
                }
            }
            addPolicy(strategy);
        } else {
        	ClassInfoLibsStrategy webClassStrategy =new ClassInfoLibsStrategy();
    		webClassStrategy.setMessageNo(detailListDTO.getMessageNo());
    		//重发sequenceNo不变
    		webClassStrategy.setMessageSequenceNo(detailListDTO.getMessageSequenceno());
    		webClassStrategy.setMessageType(messageType);
    		webClassStrategy.setOperationType(OperationConstants.OPERATION_SAVE);
    		webClassStrategy.setProbeType(ProbeType.DPI.getValue());
    		webClassStrategy.setIp(zongFenDevice.getZongfenIp());
    		webClassStrategy.setPort(zongFenDevice.getZongfenFtpPort());
    		webClassStrategy.setUserName(userName);
    		webClassStrategy.setPassword(passwd);
    		webClassStrategy.setFileName(detailListDTO.getFileName());
    		webClassStrategy.setZongfenId(detailListDTO.getZongfenId());
    		addPolicy(webClassStrategy);
        }
        return "success";
    }


    @Override
    protected boolean addDb( BaseVO baseVO ) {
        return true;
    }

    @Override
    protected boolean deleteDb( BaseVO baseVO ) {
        return true;
    }

    @Override
    protected boolean modifyDb( BaseVO baseVO ) {
        return true;
    }

    @Override
    protected boolean addCustomLogic( BaseVO baseVO ) {

        if (baseVO instanceof HttpGetStrategy){
            HttpGetStrategy strategy = (HttpGetStrategy)baseVO;
            if(getFileType() == (int) FileTypeConstant.HFDW.getValue()) {
                strategy.setHfdbVersionNo((long)0);
                strategy.setHfdbFileName("");
                strategy.setHfubVersionNo((long)0);
                strategy.setHfubFileName("");
            }else if(getFileType() == (int)FileTypeConstant.HFDB.getValue()) {
                strategy.setHfdwVersionNo((long)0);
                strategy.setHfdwFileName("");
                strategy.setHfubVersionNo((long)0);
                strategy.setHfubFileName("");
            }else if(getFileType() == (int)FileTypeConstant.HFUB.getValue()) {
                strategy.setHfdwVersionNo((long)0);
                strategy.setHfdwFileName("");
                strategy.setHfdbVersionNo((long)0);
                strategy.setHfdbFileName("");
            }
            return  setPolicyOperateSequenceToRedis(strategy) && addTaskAndChannelToRedis(strategy);
        }else {
            return  setPolicyOperateSequenceToRedis(baseVO) && addTaskAndChannelToRedis(baseVO);
        }
    }

    @Override
    protected boolean modifyCustomLogic( BaseVO baseVO ) {
        return true;
    }

    @Override
    protected boolean deleteCustomLogic( BaseVO baseVO ) {
        return true;
    }
}

package com.aotain.zongfen.service.general.classinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.zongfen.dto.general.FileDetailListDTO;
import com.aotain.zongfen.mapper.device.ZongFenDeviceMapper;
import com.aotain.zongfen.mapper.device.ZongFenRelMapper;
import com.aotain.zongfen.mapper.general.ClassInfoMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.device.ZongFenDeviceUser;
import com.aotain.zongfen.model.general.ClassFileSendMessage;
import com.aotain.zongfen.model.general.ClassInfo;
import com.aotain.zongfen.model.policy.PolicyStatus;

@Service
@Transactional
public class ClassInfoServiceImple  implements ClassInfoService {

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Autowired
    private ZongFenDeviceMapper zongFenDeviceMapper;

	@Autowired
	private ZongFenRelMapper zongFenRelMapper;

	@Autowired
	private ClassInfoStrategyReSendService classInfoStrategyReSendService;
	
	@Autowired
    private PolicyStatusMapper policyStatusMapper;
    
    @Override
    public List<ClassInfo> getClassInfos( ClassInfo classInfo ) {
        return classInfoMapper.getClassInfos(classInfo);
    }

    @Override
    public int deleteClassInfos( String[] strs ) {
        int len = strs.length;
        if(len<1){
            return 0;
        }
        Integer[] array =  new Integer[len];
        for(int i=0;i<len;i++){
            array[i] = Integer.parseInt(strs[i]);
        }
//        return classInfoMapper.deleteClassInfos(array);
        return -1;
    }

    @Override
    public ResponseResult<ClassInfo> insertOrUpdate( ClassInfo classInfo ) {
        ResponseResult<ClassInfo> rs = new ResponseResult<ClassInfo>();
        //修改
       /* classInfo.setBindUserType(0);
        classInfo.setCreateOper("admin");
        classInfo.setModifyOper("admin");
        classInfo.setClassFileName(classInfo.getClassFileName());
        if(classInfo.getClassId()!=null){
            classInfoMapper.deleteSingleClassInfo(classInfo);
            if(classNameIsExit(classInfo)){
                rs.setResult(1);
                rs.setMessage("相同策略存在");
                return rs;
            }
            classInfoMapper.insertSelective(classInfo);
            rs.setResult(1);
            rs.setMessage("更新成功");
        }else{
            //新增
            Integer maxId = classInfoMapper.getMaxClassId();
            maxId = maxId==null?1:maxId+1;
            if(classIsExit(classInfo)){
                classInfoMapper.deleteSingleClassInfo(classInfo);
                classInfo.setClassId(maxId);
                if(classNameIsExit(classInfo)){
                    rs.setResult(1);
                    rs.setMessage("相同策略存在");
                    return rs;
                }
                classInfoMapper.insertSelective(classInfo);
                rs.setResult(1);
                rs.setMessage("已经删除已经存在的策略,并新增成功");
            }else {
                rs.setResult(1);
                rs.setMessage("新增成功");
                classInfo.setClassId(maxId);
                if(classNameIsExit(classInfo)){
                    rs.setResult(1);
                    rs.setMessage("相同策略存在");
                    return rs;
                }
                classInfoMapper.insertSelective(classInfo);
            }

        }*/
        return rs;
    }


    /**
     * 判断指定分类可以是否存在
     * @param classInfo
     * @return
     */
    private boolean classIsExit(ClassInfo classInfo){
       /* if(classInfoMapper.selectCount(classInfo)>0){
            return true;
        }*/
        return false;
    }

    /**
     * 判断新增的分类库是否同名
     * @param classInfo
     * @return
     */
     private boolean classNameIsExit(ClassInfo classInfo){
       /* if(classInfoMapper.CountClassName(classInfo)>0){
            return true;
        }*/
        return false;
    }

	@Override
	public ClassFileSendMessage getMessageByType(Integer messageType) {
		ClassFileSendMessage msg = new ClassFileSendMessage();
		ClassInfo classInfo = classInfoMapper.getByType(messageType);//
    	String ip=null;
    	Integer port=null;
    	String username = null;
    	String password= null;
    	Integer zongfenId = null;
    	if(classInfo == null) {//如果分类库中没有数据，就到综分设备的表取,并且要写表分类库 zf_v2_gen_class_info
    		List<ZongFenDeviceUser> ftpUserList = zongFenDeviceMapper.getDeviceUserByType(9,0);
        	if(ftpUserList != null && ftpUserList.size() >0) {//要有数据,否则报错
        		ZongFenDeviceUser ftpUser = ftpUserList.get(0);
        		ZongFenDevice zongfenDevice = zongFenDeviceMapper.getZongfenDevByPrimary(ftpUser.getZongfenId());
        		ip = zongfenDevice.getZongfenIp();
        		port = zongfenDevice.getZongfenPort();           
        		username = ftpUser.getZongfenFtpUser();
        		password = ftpUser.getZongfenFtpPwd();
        		zongfenId = ftpUser.getZongfenId();
        		
        		ClassInfo obj = new  ClassInfo();
        		obj.setMessageType(messageType);
        		obj.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
        		obj.setZongfenId(zongfenId);
        		classInfoMapper.insertSelective(obj);
        	}
    	}else {//如果分类库中有
    		zongfenId = classInfo.getZongfenId();
    		ZongFenDevice zongfenDevice = zongFenDeviceMapper.getZongfenDevByPrimary(zongfenId);
    		ip = zongfenDevice.getZongfenIp();
    		port = zongfenDevice.getZongfenPort();
    		ZongFenDeviceUser ftpUser = zongFenDeviceMapper.getDeviceUser(zongfenId);
    		username = ftpUser.getZongfenFtpUser();
    		password = ftpUser.getZongfenFtpPwd();
    	}
    	msg.setIp(ip);
    	msg.setPort(port);
    	msg.setUsername(username);
    	msg.setPassword(password);
		return msg;
	}

	@Override
	public List<ClassFileSendMessage> getMessageByTypes( Integer messageType,ZongFenDevice zongFenDevice) {
		List<ClassFileSendMessage> list= new ArrayList<ClassFileSendMessage>();
		String ip=null;
		Integer port=null;
		String username = null;
		String password= null;
		List<ZongFenDeviceUser> zongFenDeviceUsers= zongFenDevice.getDeviceUsers();
		for (ZongFenDeviceUser device:zongFenDeviceUsers){
			if(device.getPacketType()==9 && device.getPacketSubType()==0){
				username = device.getZongfenFtpUser();
				password = device.getZongfenFtpPwd();
				break;
			}
		}
		port = zongFenDevice.getZongfenFtpPort();
		//非虚拟
		if(zongFenDevice.getIsVirtualIp()==0){
			ip = zongFenDevice.getZongfenIp();
			ClassFileSendMessage msg = new ClassFileSendMessage();
			msg.setIp(ip);
			msg.setPort(port);
			msg.setUsername(username);
			msg.setPassword(password);
			list.add(msg);
		}else {//虚拟IP，取出对应的真实IP
			List<String> ips = zongFenRelMapper.selectListById(zongFenDevice.getZongfenId());
			if (ips!=null && !ips.isEmpty()){
				for (String ipString:ips){
					ClassFileSendMessage msg = new ClassFileSendMessage();
					msg.setIp(ipString);
					msg.setPort(port);
					msg.setUsername(username);
					msg.setPassword(password);
					list.add(msg);
				}
			}
		}
		return list;
	}

	@Override
	public List<FileDetailListDTO> getFileDetail(Integer messageType) {
		List<FileDetailListDTO> result = classInfoMapper.getFileDetailByType(messageType);
		if(messageType!= MessageType.HTTP_GET_POLICY.getId()) {
			List<FileDetailListDTO> resultOne = new ArrayList<FileDetailListDTO>();
			if(result.size()>0) {
				PolicyStatus policy = new PolicyStatus();
	            if(result.get(0).getMessageNo()!=null) {
	            	PolicyStatus query2 = new PolicyStatus();
	            	query2.setMessageNo(result.get(0).getMessageNo());
	            	query2.setMessageType(result.get(0).getMessageType());
	            	policy = policyStatusMapper.getCountTotalAndFail(query2);
	            }
	            if(policy!=null) {
	            	result.get(0).setMainPolicy(policy.getMainCount() == null ? "0/0" : policy.getMainCount());
	            }else {
	            	result.get(0).setMainPolicy("0/0");
	            }
            	
				resultOne.add(result.get(0));
				return resultOne;
			}else {
				return result;
			}
		}else {
			if(result.size()>0) {
				PolicyStatus policy = new PolicyStatus();
	            if(result.get(0).getMessageNo()!=null) {
	            	PolicyStatus query2 = new PolicyStatus();
	            	query2.setMessageNo(result.get(0).getMessageNo());
	            	query2.setMessageType(result.get(0).getMessageType());
	            	policy = policyStatusMapper.getCountTotalAndFail(query2);
	            }
	            if(policy!=null) {
	            	for(FileDetailListDTO temp:result) {
		            	temp.setMainPolicy(policy.getMainCount() == null ? "0/0" : policy.getMainCount());
					}
	            }else {
	            	for(FileDetailListDTO temp:result) {
		            	temp.setMainPolicy("0/0");
					}
	            }
			}
			return result;
		}
	}

	@Override
	public String reSend(FileDetailListDTO detailListDTO ) {

		return classInfoStrategyReSendService.strategyReSend(detailListDTO);
	}

	@Override
	public List<ZongFenDevice> getServerList() {
		Map<String,Integer> query = new HashMap<String, Integer>();
		query.put("packetType",9);
		query.put("packetSubType",0);
		return zongFenDeviceMapper.getZongfenDevByType(query);
	}
}

package com.aotain.zongfen.service.general.appname;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.aotain.common.policyapi.constant.FileTypeConstant;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.ClassInfoLibsStrategy;
import com.aotain.common.utils.file.SftpClientUtil;
import com.aotain.common.utils.redis.AlarmClassInfoUtil;
import com.aotain.common.utils.redis.FileTypeVersionUtil;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.mapper.common.MultiSelectMapper;
import com.aotain.zongfen.mapper.device.ZongFenDeviceMapper;
import com.aotain.zongfen.mapper.general.ClassFileInfoMapper;
import com.aotain.zongfen.mapper.general.GeneralAppMapper;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.general.ClassFileInfo;
import com.aotain.zongfen.model.general.ClassFileInfoKey;
import com.aotain.zongfen.model.general.ClassFileSendMessage;
import com.aotain.zongfen.model.general.GeneralApp;
import com.aotain.zongfen.service.general.classinfo.ClassInfoLibsStrategyService;
import com.aotain.zongfen.service.general.classinfo.ClassInfoService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.validate.dataImport.general.AppNameImportMgr;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jcraft.jsch.ChannelSftp;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class AppNameServiceImpl implements AppNameService {
	
	private static Logger logger = LoggerFactory.getLogger(AppNameServiceImpl.class);
	
	@Autowired
	private GeneralAppMapper generalAppMapper;
	
	@Autowired
	private ClassFileInfoMapper classFileInfoMapper;

	@Autowired
	private ClassInfoLibsStrategyService classInfoLibsStrategyService;
	
	@Autowired
	private ClassInfoService classInfoService;
	
	@Autowired
	private ZongFenDeviceMapper zongFenDeviceMapper;
	
	@Autowired
	private MultiSelectMapper multiSelectMapper;
	
	public Map<String,String> uploadFile(HttpServletRequest request) throws Exception{
		List<Multiselect> appTypeList = multiSelectMapper.getAppType();
		Map<String,String> appType = new HashMap<String, String>();
		for(Multiselect tem : appTypeList) {
			String id = Integer.toHexString(Integer.valueOf(tem.getValue())).toString();
			if(id.length()==1) {
				appType.put("0x0"+id,tem.getTitle());
			}else {
				appType.put("0x"+id,tem.getTitle());
			}
		}
		Map<String,String> message = new HashMap<String, String>();
		String operation = request.getParameter("operation");
		Map<String,InputStream> fileMap = ExcelUtil.getInputStreamList(request,"appNameFile");
		AppNameImportMgr appNameImportMgr = new AppNameImportMgr();
		Map<String,Set<GeneralApp>> maps = appNameImportMgr.readDataFromStreas(fileMap,appType);
		Set<String> set = maps.keySet();
		Iterator<String> it= set.iterator();
        if(appNameImportMgr.getErrorMsg()==null) {
        	for(;it.hasNext();){
        		String fileName = it.next().toString();
        		Set<GeneralApp> dataSet = maps.get(fileName);
        		if(!dataSet.isEmpty()) {
        			if(operation.equals("0")) {//全量
        				//删除原来数据
        				generalAppMapper.deleteAll();
	     				List<GeneralApp> array = new ArrayList<GeneralApp>(dataSet);
	     				generalAppMapper.insertList(array);
        			}else if(operation.equals("1")) {//增量
        				List<GeneralApp> array = new ArrayList<GeneralApp>(dataSet);
	     				Map<String,String> query = new HashMap<String, String>();
	     				query.put("appType", null);
	     				query.put("appName", null);
	     				List<GeneralApp> allList = generalAppMapper.getIndexList(query);
	     				List<GeneralApp> addUrl = new ArrayList<GeneralApp>();
	     				for(GeneralApp newUrl: array) {
	     					boolean isNew = true;
	     					for(GeneralApp oldUrl: allList) {
	     						String typeId = Integer.toHexString(Integer.valueOf(oldUrl.getAppType())).toString();
	     						if(typeId.length()==1) {
	     							oldUrl.setAppTypes("0x0"+typeId);
	     						}else {
	     							oldUrl.setAppTypes("0x"+typeId);
	     						}
	     						String appId = Integer.toHexString(Integer.valueOf(oldUrl.getAppId())).toString();
	     						int len = appId.length();
	     						for(int i=0;i<4-len;i++) {
	     							appId = "0"+appId;
	     						}
	     						oldUrl.setAppIds("0x"+appId);
	     						if(newUrl.equals(oldUrl)) {
	     							isNew = false;
	     							break;
	     						}
	     					}
	     					if(isNew) {
	     						addUrl.add(newUrl);
	     					}
	     				}
	     				if(addUrl.size()>0) {
	     					generalAppMapper.insertList(addUrl);
	     				}
        			}
        		}else {
        			message.put("message", "文件为空，请重新导入！");
        			message.put("tips", "0");
        			return message;
        		}
        	}
        } else {
        	message.put("message", appNameImportMgr.getErrorMsg());
			message.put("tips", "0");
			return message;
        }
        AlarmClassInfoUtil.getInstance().setToAlarmByType(MessageType.APP_NAME_TABLE_POLICY.getId());
        message.put("message", "");
		message.put("tips", AlarmClassInfoUtil.getInstance().getAlarmType(MessageType.APP_NAME_TABLE_POLICY.getId()));
		return message;
	
	}

	@Override
	public PageResult<GeneralApp> getIndexList(Integer pageSize, Integer pageIndex, String appType, String appName) {
		Map<String,String> query = new HashMap<String, String>();
		query.put("appType", appType);
		query.put("appName", appName);
		PageResult<GeneralApp> result = new PageResult<GeneralApp>();
		PageHelper.startPage(pageIndex, pageSize);
		List<GeneralApp> info = generalAppMapper.getIndexList(query);
		PageInfo<GeneralApp> pageResult = new PageInfo<GeneralApp>(info);
		for(GeneralApp tem:info) {
			String appId = Integer.toHexString(Integer.valueOf(tem.getAppId())).toString();
			int len = appId.length();
			for(int i=0;i<4-len;i++) {
				appId = "0"+appId;
			}
			tem.setAppIds("0x"+appId);
		}
		result.setTotal(pageResult.getTotal());
		result.setRows(info);
		return result;
	}

	/**
	 * 
	* @Title: sendAppNameLibMessage 
	* @Description: 发策略(这里用一句话描述这个方法的作用) 
	* @param @param fileName    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	private String sendAppNameLibMessage(String fileName,Long versionNo,Integer serverId) {
		ZongFenDevice zongFenDevice = zongFenDeviceMapper.getZongfenDevByPrimary(serverId);
        List<ClassFileSendMessage> classFileMessage = classInfoService.getMessageByTypes(MessageType.WEB_CLASS_TABLE_POLICY.getId(),zongFenDevice);
        if(classFileMessage != null) {
			try {
				for (ClassFileSendMessage tem:classFileMessage) {
					//上传文件到SFTP服务器
		        	String src = System.getProperty("user.dir")+"/productFile/"+fileName;
		        	String dst = fileName;
					ChannelSftp chSftp = SftpClientUtil.getChannel(tem.getUsername(), tem.getIp(), tem.getPassword(), tem.getPort());
					chSftp.put(src, dst, ChannelSftp.OVERWRITE);//上传文件到目前用户根目录
		        	chSftp.quit();
				}
	        	SftpClientUtil.closeChannel();
	        	AlarmClassInfoUtil.getInstance().cancleAlarmByType(MessageType.APP_NAME_TABLE_POLICY.getId());//取消消息提示
			} catch (Exception e) {
				logger.error("sftp upload fail !");
				return "sftp_error";
			}
			String ip = null;
        	Integer port = null;
        	String username = null;
        	String password = null;
        	if(zongFenDevice.getIsVirtualIp()==1) {//负载均衡
        		ip = zongFenDevice.getZongfenIp();
            	port = zongFenDevice.getZongfenFtpPort();
            	username = classFileMessage.get(0).getUsername();
            	password = classFileMessage.get(0).getPassword();
        	}else if(zongFenDevice.getIsVirtualIp()==0 && classFileMessage.size()==1){//非负载均衡
        		ip = classFileMessage.get(0).getIp();
            	port = classFileMessage.get(0).getPort();
            	username = classFileMessage.get(0).getUsername();
            	password = classFileMessage.get(0).getPassword();
        	}
        	if(ip != null && port != null && username != null && password != null) {//可以成功发策略
        		//1:设置策略的对象值
        		ClassInfoLibsStrategy appNameStrategy =new ClassInfoLibsStrategy();
        		appNameStrategy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(MessageType.APP_NAME_TABLE_POLICY.getId()));
        		appNameStrategy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.APP_NAME_TABLE_POLICY.getId()));
        		appNameStrategy.setMessageType(MessageType.APP_NAME_TABLE_POLICY.getId());
        		appNameStrategy.setOperationType(OperationConstants.OPERATION_SAVE);
        		appNameStrategy.setProbeType(ProbeType.DPI.getValue());
        		appNameStrategy.setIp(ip);
        		appNameStrategy.setPort(port);
        		appNameStrategy.setUserName(username);
        		appNameStrategy.setPassword(password);
        		appNameStrategy.setVersionNo(versionNo);
        		appNameStrategy.setFileName(fileName);
        		appNameStrategy.setZongfenId(zongFenDevice.getZongfenId());
        		//2:发策略
        		classInfoLibsStrategyService.addPolicy(appNameStrategy);
        		
        		return "success";
        	}else {//不能成功发策略
        		logger.error("it has no sftp user infomations !");
        		return "no_dev";
        	}
        }else {
        	logger.error("it has no sftp user infomations !");
    		return "no_dev";
        }
    }

	
	@Override
	public Map<String,String> createAndSend(Integer serverId) {
		Map<String,String> resultMap = new HashMap<String, String>();
		Map<String,String> query = new HashMap<String, String>();
		query.put("appType", null);
		query.put("appName", null);
		List<GeneralApp> allList = generalAppMapper.getIndexList(query);
		if(allList!=null && allList.size()>0) {
			String filePath =  System.getProperty("user.dir")+"/productFile/";
			File directory = new File(filePath);
			File[] files = directory.listFiles();
			//获取版本号
			Long versionNo = FileTypeVersionUtil.getInstance().getFileType(FileTypeConstant.APPLIB.getValue());
			String fileName = "";
			for(File temFile:files) {
				fileName = temFile.getPath().substring(temFile.getPath().lastIndexOf(System.getProperty("file.separator")));
				if(fileName.indexOf("ANT")>0) {
					temFile.delete();
				}
			}
			File newFile = new File(filePath+"ANT_"+versionNo+"_000.txt");
			BufferedWriter buffer = null;
			try {
				newFile.createNewFile();
				buffer = new BufferedWriter(new java.io.FileWriter(newFile));
				StringBuffer writerData = new StringBuffer();
				for(GeneralApp app:allList) {
					String id = Integer.toHexString(Integer.valueOf(app.getAppType())).toString();
					if(id.length()==1) {
						app.setAppTypes("0x0"+id);
					}else {
						app.setAppTypes("0x"+id);
					}
					String appId = Integer.toHexString(Integer.valueOf(app.getAppId())).toString();
					int len = appId.length();
					for(int i=0;i<4-len;i++) {
						appId = "0"+appId;
					}
					app.setAppIds("0x"+appId);
					//中文名称文件生成
					if(!StringUtils.isEmpty(app.getAppZHName())) {
						writerData.append(app.getAppZHName()).append("\t").
						append(app.getAppTypes()).append("\t").
						append(app.getAppIds()).append("\r\n");
					}
					
				}
				buffer.write(writerData.toString());
				buffer.flush();
				buffer.close();
				//后面还要先删除原来相关联的分类库策略，然后新增相应的分类库策略
				ClassFileInfo record = new ClassFileInfo();
				
				record.setMessageType(MessageType.APP_NAME_TABLE_POLICY.getId());
				record.setFileType(FileTypeConstant.APPLIB.getValue());
		        ClassFileInfoKey classFileInfoKey = new ClassFileInfoKey();
		        classFileInfoKey.setFileType(FileTypeConstant.APPLIB.getValue());
		        classFileInfoKey.setMessageType(FileTypeConstant.APPLIB.getValue());
				record.setClassFileName("ANT_"+versionNo+"_000.txt");
				record.setVersionNo(versionNo);
				record.setModifyOper(SpringUtil.getSysUserName());
				record.setModifyTime(new Date());
				record.setCreateOper(SpringUtil.getSysUserName());
				record.setCreateTime(record.getModifyTime());
				ClassFileInfo result = classFileInfoMapper.selectByPrimaryKey(classFileInfoKey);
				if(result!=null && result.getClassFileName()!=null) {
					classFileInfoMapper.updateByPrimaryKeySelective(record);
				}else {
					classFileInfoMapper.insert(record);
				}
				resultMap.put("fileName",record.getClassFileName());
				String msg =  sendAppNameLibMessage(record.getClassFileName(),versionNo,serverId);
				resultMap.put("message",msg);
			} catch (IOException e) {
				resultMap.put("message","error");
				return resultMap;
			}
			return resultMap;
		}else {
			resultMap.put("message","is empty");
			return resultMap;
		}
	}

	@Override
	public List<GeneralApp> getAllAppName() {
		Map<String,String> query = new HashMap<String, String>();
		query.put("appType", null);
		query.put("appName", null);
		List<GeneralApp> allList = generalAppMapper.getIndexList(query);
		for(GeneralApp app:allList) {
			String id = Integer.toHexString(Integer.valueOf(app.getAppType())).toString();
			if(id.length()==1) {
				app.setAppTypes("0x0"+id);
			}else {
				app.setAppTypes("0x"+id);
			}
			String appId = Integer.toHexString(Integer.valueOf(app.getAppId())).toString();
			int len = appId.length();
			for(int i=0;i<4-len;i++) {
				appId = "0"+appId;
			}
			app.setAppIds("0x"+appId);
		}
		return allList;
	}

}

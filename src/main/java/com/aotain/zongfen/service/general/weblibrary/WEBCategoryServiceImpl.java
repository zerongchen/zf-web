package com.aotain.zongfen.service.general.weblibrary;

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
import com.aotain.zongfen.mapper.general.GeneralURLMapper;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.general.ClassFileInfo;
import com.aotain.zongfen.model.general.ClassFileInfoKey;
import com.aotain.zongfen.model.general.ClassFileSendMessage;
import com.aotain.zongfen.model.general.GeneralURL;
import com.aotain.zongfen.service.general.classinfo.ClassInfoLibsStrategyService;
import com.aotain.zongfen.service.general.classinfo.ClassInfoService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.validate.dataImport.general.WebCategoryImportMgr;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jcraft.jsch.ChannelSftp;

@Service
public class WEBCategoryServiceImpl implements WEBCategoryService {
	
	private static Logger logger = LoggerFactory.getLogger(WEBCategoryServiceImpl.class);
	
	@Autowired
	private GeneralURLMapper generalURLMapper;
	
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
	
	@Override
	public Map<String,String> uploadFile(HttpServletRequest request) throws Exception {
		Map<String,String> message = new HashMap<String, String>();
		String operation = request.getParameter("operation");
		Map<String,InputStream> fileMap = ExcelUtil.getInputStreamList(request,"webCategoryFile");
		WebCategoryImportMgr webCategoryImportMgr = new WebCategoryImportMgr();
		Map<String,Set<GeneralURL>> maps = webCategoryImportMgr.readDataFromStreams(fileMap,getWebType());
		Set<String> set = maps.keySet();
		Iterator<String> it= set.iterator();
        if(webCategoryImportMgr.getErrorMsg()==null) {
        	for(;it.hasNext();){
        		String fileName = it.next().toString();
        		Set<GeneralURL> dataSet = maps.get(fileName);
        		if(!dataSet.isEmpty()) {
        			if(operation.equals("0")) {//全量
        				//删除原来数据
	     				generalURLMapper.deleteAll();
	     				List<GeneralURL> array = new ArrayList<GeneralURL>(dataSet);
	     				generalURLMapper.insertList(array);
        			}else if(operation.equals("1")) {//增量
        				List<GeneralURL> array = new ArrayList<GeneralURL>(dataSet);
	     				Map<String,String> query = new HashMap<String, String>();
	     				query.put("webType", null);
	     				query.put("hostName", null);
	     				List<GeneralURL> allList = generalURLMapper.getIndexList(query);
	     				List<GeneralURL> addUrl = new ArrayList<GeneralURL>();
	     				for(GeneralURL newUrl: array) {
	     					boolean isNew = true;
	     					for(GeneralURL oldUrl: allList) {
     							String id = Integer.toHexString(Integer.valueOf(oldUrl.getWebType())).toString();
     							if(id.length()==1) {
     								oldUrl.setWebTypes("0x0"+id);
     							}else {
     								oldUrl.setWebTypes("0x"+id);
     							}
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
	     					generalURLMapper.insertList(addUrl);
	     				}
        			}
        		}else {
        			message.put("message", "文件为空，请重新导入！");
        			message.put("tips", "0");
        			return message;
        		}
        	}
        } else {
        	message.put("message", webCategoryImportMgr.getErrorMsg());
			message.put("tips", "0");
			return message;
        }
        AlarmClassInfoUtil.getInstance().setToAlarmByType(MessageType.WEB_CLASS_TABLE_POLICY.getId());
        message.put("message", "");
		message.put("tips", AlarmClassInfoUtil.getInstance().getAlarmType(MessageType.WEB_CLASS_TABLE_POLICY.getId()));
		return message;
	}

	public Map<String,String> getWebType(){
		List<Multiselect> webTypeList = multiSelectMapper.getWebType();
		Map<String,String> webType = new HashMap<String, String>();
		for(Multiselect tem : webTypeList) {
			String id = Integer.toHexString(Integer.valueOf(tem.getValue())).toString();
			if(id.length()==1) {
				webType.put("0x0"+id,tem.getTitle());
			}else {
				webType.put("0x"+id,tem.getTitle());
			}
		}
		return webType;
	}
	
	@Override
	public PageResult<GeneralURL> getIndexList(Integer pageSize, Integer pageIndex, String webType, String hostName) {
		Map<String,String> query = new HashMap<String, String>();
		query.put("webType", webType);
		query.put("hostName", hostName);
		PageResult<GeneralURL> result = new PageResult<GeneralURL>();
		PageHelper.startPage(pageIndex, pageSize);
		List<GeneralURL> info = generalURLMapper.getIndexList(query);
		PageInfo<GeneralURL> pageResult = new PageInfo<GeneralURL>(info);
		for(GeneralURL tem : info) {
			String id = Integer.toHexString(Integer.valueOf(tem.getWebType())).toString();
			if(id.length()==1) {
				tem.setWebTypes("0x0"+id);
			}else {
				tem.setWebTypes("0x"+id);
			}
		}
		result.setTotal(pageResult.getTotal());
		result.setRows(info);
		return result;
	}
	
	/**
	 * 
	* @Title: sendWebClassLibMessage 
	* @Description: 发策略(这里用一句话描述这个方法的作用) 
	* @param @param fileName    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	private String sendWebClassLibMessage(String fileName,Long versionNo,Integer serverId) {
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
	        	AlarmClassInfoUtil.getInstance().cancleAlarmByType(MessageType.WEB_CLASS_TABLE_POLICY.getId());//取消消息提示
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
        		ClassInfoLibsStrategy webClassStrategy =new ClassInfoLibsStrategy();
        		webClassStrategy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(MessageType.WEB_CLASS_TABLE_POLICY.getId()));
        		webClassStrategy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.WEB_CLASS_TABLE_POLICY.getId()));
        		webClassStrategy.setMessageType(MessageType.WEB_CLASS_TABLE_POLICY.getId());
        		webClassStrategy.setOperationType(OperationConstants.OPERATION_SAVE);
        		webClassStrategy.setProbeType(ProbeType.DPI.getValue());
        		webClassStrategy.setIp(ip);
        		webClassStrategy.setPort(port);
        		webClassStrategy.setUserName(username);
        		webClassStrategy.setPassword(password);
        		webClassStrategy.setVersionNo(versionNo);
        		webClassStrategy.setFileName(fileName);
        		webClassStrategy.setZongfenId(zongFenDevice.getZongfenId());
        		//2:发策略
        		classInfoLibsStrategyService.addPolicy(webClassStrategy);
        		
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
		query.put("webType", null);
		query.put("hostName", null);
		List<GeneralURL> allList = generalURLMapper.getIndexList(query);
		if(allList!=null && allList.size()>0) {
			String filePath = System.getProperty("user.dir")+"/productFile/";
			File directory = new File(filePath);
			File[] files = directory.listFiles();
			//获取版本号
			Long versionNo = FileTypeVersionUtil.getInstance().getFileType(FileTypeConstant.WEBLIB.getValue());
			String fileName = "";
			for(File temFile:files) {
				fileName = temFile.getPath().substring(temFile.getPath().lastIndexOf(System.getProperty("file.separator")));
				if(fileName.indexOf("WCL")>0) {
					temFile.delete();
				}
			}
			File newFile = new File(filePath+"WCL_"+versionNo+"_000.txt");
			BufferedWriter buffer = null;
			try {
				newFile.createNewFile();
				buffer = new BufferedWriter(new java.io.FileWriter(newFile));
				StringBuffer writerData = new StringBuffer();
				for(GeneralURL webCategory:allList) {
					String id = Integer.toHexString(Integer.valueOf(webCategory.getWebType())).toString();
					if(id.length()==1) {
						webCategory.setWebTypes("0x0"+id);
					}else {
						webCategory.setWebTypes("0x"+id);
					}
					writerData.append(webCategory.getHostName()).append("\t").
						append(webCategory.getWebTypes()).append("\t").
						append(webCategory.getWebName()).append("\r\n");
				}
				buffer.write(writerData.toString());
				buffer.flush();
				buffer.close();
				//后面还要先删除原来相关联的分类库策略，然后新增相应的分类库策略
				
				ClassFileInfo record = new ClassFileInfo();
				record.setMessageType(MessageType.WEB_CLASS_TABLE_POLICY.getId());
				record.setFileType(FileTypeConstant.WEBLIB.getValue());
		        ClassFileInfoKey classFileInfoKey = new ClassFileInfoKey();
		        classFileInfoKey.setFileType(FileTypeConstant.WEBLIB.getValue());
		        classFileInfoKey.setMessageType(FileTypeConstant.WEBLIB.getValue());
		        record.setClassFileName("WCL_"+versionNo+"_000.txt");
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
				String msg =  sendWebClassLibMessage(record.getClassFileName(),versionNo,serverId);
				resultMap.put("message",msg);
				return resultMap;
			} catch (IOException e) {
				resultMap.put("message","error");
				return resultMap;
			}
		}else {
			resultMap.put("message","is empty");
			return resultMap;
		}
		
	}

	@Override
	public List<GeneralURL> getAllUrl() {
		Map<String,String> query = new HashMap<String, String>();
		query.put("webType", null);
		query.put("hostName", null);
		List<GeneralURL> allList = generalURLMapper.getIndexList(query);
		for(GeneralURL tem : allList) {
			String id = Integer.toHexString(Integer.valueOf(tem.getWebType())).toString();
			if(id.length()==1) {
				tem.setWebTypes("0x0"+id);
			}else {
				tem.setWebTypes("0x"+id);
			}
		}
		return allList;
	}

}

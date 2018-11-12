package com.aotain.zongfen.service.general.httpget;

import com.alibaba.fastjson.JSON;
import com.aotain.common.policyapi.constant.FileTypeConstant;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.HttpGetStrategy;
import com.aotain.common.utils.file.SftpClientUtil;
import com.aotain.common.utils.redis.AlarmClassInfoUtil;
import com.aotain.common.utils.redis.FileTypeVersionUtil;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.controller.general.HttpGetListController;
import com.aotain.zongfen.dto.general.HttpGetBWDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.device.ZongFenDeviceMapper;
import com.aotain.zongfen.mapper.general.ClassFileInfoMapper;
import com.aotain.zongfen.mapper.general.HttpGetBWMapper;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.general.ClassFileInfo;
import com.aotain.zongfen.model.general.ClassFileInfoKey;
import com.aotain.zongfen.model.general.ClassFileSendMessage;
import com.aotain.zongfen.model.general.HttpGetBW;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.classinfo.ClassInfoService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.TxtUtil;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.aotain.zongfen.validate.dataImport.general.HttpGetImportMgr;
import com.jcraft.jsch.ChannelSftp;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class HttpGetListServiceImpl implements HttpGetListService{
	
	private static Logger logger = LoggerFactory.getLogger(HttpGetListServiceImpl.class);
    @Autowired
    private HttpGetBWMapper httpGetBWMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ClassFileInfoMapper classFileInfoMapper;

    @Autowired
    private HttpGetStrategyService httpGetStrategyService;

    @Autowired
    private ZongFenDeviceMapper zongFenDeviceMapper;
    
    @Autowired
    private ClassInfoService classInfoService;

    @Override
    public List<HttpGetBWDTO> getDomain( HttpGetBW httpGetBW ) {
        return httpGetBWMapper.getDomain(httpGetBW);
    }

    @Override

    public synchronized ImportResultList handleImport(HttpServletRequest request ) throws ImportException {
        InputStream inputStream = ExcelUtil.getInputStream(request);
        Integer type = request.getParameter("type")!=null?Integer.parseInt(request.getParameter("type")):null;
        Integer operateType = request.getParameter("operatetype")!=null?Integer.parseInt(request.getParameter("operatetype")):null;
        HttpGetImportMgr httpGetImportMgr =getHttpGetImportMgr();
        Map<Integer,Map<Integer, String[]>> map = httpGetImportMgr.readDataFromStream(inputStream);
        Set<String> sets = new HashSet<String>();
        if(map!=null && !map.isEmpty()){
            for (Map.Entry<Integer,Map<Integer, String[]>> entry:map.entrySet()){
                Map<Integer, String[]> childMap = entry.getValue();
                if (childMap==null || childMap.isEmpty()) continue;
                for (Map.Entry<Integer, String[]> lastMap:childMap.entrySet()){
                    String[] values = lastMap.getValue();
                    if(values.length>=1 && !StringUtils.isEmpty(values[0]) ){
                        sets.add(values[0]);
                    }
                }
            }
            if(sets.size()==0){
                httpGetImportMgr.buildErrorResult(httpGetImportMgr.getFiletErrorMsg());
            }
            if(operateType == 0){ //全量
                httpGetBWMapper.deleteByType(type);
                httpGetBWMapper.insertByType(type,sets);
            }else { //增量
                List<String> list = httpGetBWMapper.getListByType(type);
                sets.removeAll(list);
                if (!sets.isEmpty()){
                    httpGetBWMapper.insertByType(type,sets);
                }
            }
            Integer fileType = redirectValue(type);
            AlarmClassInfoUtil.getInstance().setToAlarmByType(fileType);
        }
        ImportResultList importResultList = new ImportResultList();
        importResultList.setResult(ImportConstant.DATA_IMPORT_SUCCESS);
        return importResultList;
    }
    /**
     * 
    * @Title: sendHttpGetMessage 
    * @Description: 发策略(这里用一句话描述这个方法的作用) 
    * @param @param httpgetList    设定文件 
    * @return String    返回类型 
    * @throws
     */
    @Override
    public String sendHttpGetMessage(List<HttpGetInfo> httpgetList,Integer zonfenId) {

        /**
         * 获取messageNo HTTP GET 传SFTP,策略的messageNo 是一样的
         */
        long messageNo= MessageNoUtil.getInstance().getMessageNo(MessageType.HTTP_GET_POLICY.getId());
        long messageSqo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.HTTP_GET_POLICY.getId());

        /***********************************************
       * 发策略代码:HTTP GET发策略比较特殊。得重点关注下      *
       ***********************************************
       */
        ZongFenDevice zongFenDevice = zongFenDeviceMapper.getZongfenDevByPrimary(zonfenId);
    	List<ClassFileSendMessage> classFileMessages = classInfoService.getMessageByTypes(MessageType.HTTP_GET_POLICY.getId(),zongFenDevice);
    	//上传文件到SFTP服务器
    	try {
    		for(HttpGetInfo tem:httpgetList) {
    		    if (classFileMessages==null && classFileMessages.isEmpty()) continue;
    		    for (ClassFileSendMessage classFileMessage:classFileMessages) {
                    ChannelSftp chSftp = SftpClientUtil.getChannel(classFileMessage.getUsername(), classFileMessage.getIp(), classFileMessage.getPassword(), classFileMessage.getPort());
                    if (tem.getIsupdate()) {
                        AlarmClassInfoUtil.getInstance().cancleAlarmByType(tem.getFileType());//取消消息提示
                        String src = System.getProperty("user.dir") + "/productFile/" + tem.getFileName();
                        String dst = tem.getFileName();//上传文件到目前用户根目录
                        chSftp.put(src, dst, ChannelSftp.OVERWRITE);
                        chSftp.quit();
                    }
                }
    		}
        	SftpClientUtil.closeChannel();
		} catch (Exception e) {
			logger.error("sftp upload fail !"+e);
			return "sftp_error";
		}
    	
        if(classFileMessages != null && !classFileMessages.isEmpty()) {
            ClassFileSendMessage classFileSendMessage = classFileMessages.get(0);
        	String ip = zongFenDevice.getZongfenIp();
        	Integer port = zongFenDevice.getZongfenFtpPort();
        	String username = classFileSendMessage.getUsername();
        	String password = classFileSendMessage.getPassword();
        	
        	if(ip != null && port != null && username != null && password != null) {//可以成功发策略
        		//1:设置策略的对象值
        		HttpGetStrategy httpGetStrategy =new HttpGetStrategy();
        		httpGetStrategy.setMessageNo(messageNo);
        		httpGetStrategy.setMessageSequenceNo(messageSqo);
        		httpGetStrategy.setMessageType(MessageType.HTTP_GET_POLICY.getId());
        		httpGetStrategy.setOperationType(OperationConstants.OPERATION_SAVE);
        		httpGetStrategy.setProbeType(ProbeType.DPI.getValue());
        		httpGetStrategy.setIp(ip);
        		httpGetStrategy.setPort(port);
        		httpGetStrategy.setUserName(username);
        		httpGetStrategy.setPassword(password);
        		httpGetStrategy.setZongfenId(zonfenId);
        		//版本号
        		long versionNo = 0;
        		ClassFileInfoKey key = new ClassFileInfoKey();
        		
        		for(HttpGetInfo info : httpgetList) {
        			key.setMessageType(MessageType.HTTP_GET_POLICY.getId());
        			key.setFileType(info.getFileType());
        			ClassFileInfo classFileInfo = classFileInfoMapper.selectByPrimaryKey(key);
//        			if(info.getIsupdate()) {//如果为update的话，就到redis里取seqNo
//        				versionNo = FileTypeVersionUtil.getInstance().getFileType(info.getFileType());
//        			}else {//如果没有更新。查数据库
        			versionNo = classFileInfo== null ? FileTypeVersionUtil.getInstance().getFileType(info.getFileType()): classFileInfo.getVersionNo();
//        			}
        			if(info.getFileType() == FileTypeConstant.HFDW.getValue()) {
        				httpGetStrategy.setHfdwVersionNo(versionNo);
        				httpGetStrategy.setHfdwFileName(info.getFileName());
        			}else if(info.getFileType() == FileTypeConstant.HFDB.getValue()) {
        				httpGetStrategy.setHfdbVersionNo(versionNo);
        				httpGetStrategy.setHfdbFileName(info.getFileName());
        			}else if(info.getFileType() == FileTypeConstant.HFUB.getValue()) {
        				httpGetStrategy.setHfubVersionNo(versionNo);
        				httpGetStrategy.setHfubFileName(info.getFileName());
        			}


        		}
        		//2:发策略
        		httpGetStrategyService.addPolicy(httpGetStrategy);
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
    public int deleteBw( String[] ids ,Integer type) {
        int len = ids.length;
        if (len<1) return 0;
        Long[] array = new Long[len];
        for (int i=0;i<len;i++){
            array[i] = Long.valueOf(ids[i]);
        }
        Integer result = httpGetBWMapper.deleteByIds(array);

        Integer alarmType = redirectValue(type);
        AlarmClassInfoUtil.getInstance().setToAlarmByType(alarmType);
        return result;
    }

    @Override
    public ResponseResult<HttpGetBW> updateBw( HttpGetBW recode ) {
        ResponseResult<HttpGetBW> responseResult=new ResponseResult<HttpGetBW>();
        List<String> list = httpGetBWMapper.getListByType(recode.getType());
        if(list.contains(recode.getDomain())){
            responseResult.setResult(0);
            responseResult.setMessage("域名已经存在,不修改请取消");
            return responseResult;
        }
        recode.setUpdateTime(new Date());
        httpGetBWMapper.updateBw(recode);
        Integer alarmType = redirectValue(recode.getType());
        AlarmClassInfoUtil.getInstance().setToAlarmByType(alarmType);
        responseResult.setResult(1);
        responseResult.setMessage("修改成功");
        return responseResult;
    }

    @Override
    public Map<String, Integer> getWarning() {

        Map<String,Integer> map = new HashMap<>();
        String[] args = new String[]{"HFDW","HFDB","HFUB"};
        for (int i =0;i<3;i++){
            Integer fileType = redirectValue(i);
            Boolean flag = isInAlarm(fileType);
            map.put(args[i],flag==true?1:0);
        }
        return map;
    }

    @Override
    public String createAndSend( HttpGetBW httpGetBW ,Integer zonfenId) {

        String filePath = System.getProperty("user.dir")+"/productFile/";
        List<String> lastFileName = new ArrayList<>();
        List<HttpGetInfo> httpgetList = new ArrayList<HttpGetInfo>();
        HttpGetInfo httpgetInfo = null;
        try {
            for (int i =0;i<=2;i++){
                Map<String,Object> map = makeFile(filePath,i);
                String fileName = (String) map.get("filename");
                Boolean isUpdate = (Boolean) map.get("isupdate");
                Integer fileType = (Integer) map.get("filetype");
                
                httpgetInfo = new HttpGetInfo();
                httpgetInfo.setFileName(fileName);
                httpgetInfo.setFileType(fileType);
                httpgetInfo.setIsupdate(isUpdate);
                httpgetList.add(httpgetInfo);
                
                if (!StringUtils.isEmpty(fileName))
                    lastFileName.add(fileName);
            }

            //发策略:把httpget的list表发下去
            String msg = sendHttpGetMessage(httpgetList,zonfenId);
            if("success".equals(msg)){
                try{
                    String dataJson = "fileName="+ JSON.toJSONString(lastFileName)+",zongfenId="+zonfenId;
                    String inputParam = "zongfenId="+zonfenId;
                    ProxyUtil.changeVariable(HttpGetListController.class,"send",dataJson,inputParam, ModelType.MODEL_HTTPGET_LIB.getModel(), OperationType.PRODUCT);
                } catch (Exception e){
                    logger.info("bangLog==========change LogAnnotation failed...========="+e);
                }
            }
            return msg;

        }catch (Exception e){
            logger.error("it has no sftp user infomations !",e);
            return "error";
        }
    }
    
    @Getter
    @Setter
    class HttpGetInfo{
    	String fileName;
        Boolean isupdate;
        Integer fileType;
    }


   /**
   * 
  * @Title: makeFile 
  * @Description: TODO(这里用一句话描述这个方法的作用) 
  * @param @param path
  * @param @param type
  * @param @return
  * @param @throws InvocationTargetException
  * @param @throws NoSuchMethodException
  * @param @throws IllegalAccessException
  * @param @throws IOException    设定文件 
  * @return Map<String,Object>    返回类型 
  * @throws
   */
    protected Map<String,Object> makeFile(String path,Integer type) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
    	//数据结构：用来存储变更的HTTP GET报文列表txt文件
    	Map<String,Object>  valmap = new HashMap<String, Object>();
    	
        String[] args = new String[]{"HFDW","HFDB","HFUB"};
        HttpGetBW httpGetBW = new HttpGetBW();
        httpGetBW.setType(type);
        String preName = args[type];
       
        String endName = "000";
        Integer fileType = redirectValue(type);

        Boolean flag = isInAlarm(fileType);

        String exitFileName  = commonService.getFileNameByType(path,MessageType.HTTP_GET_POLICY.getId(),fileType);

        if(flag){
        	
        	Long versionNo = FileTypeVersionUtil.getInstance().getFileType(fileType);
        	 
            List<HttpGetBWDTO> list = httpGetBWMapper.getDomain(httpGetBW);
            if(list!=null && !list.isEmpty()){
                String fileName =preName+"_"+versionNo+"_"+endName+".txt";
                String filePath = System.getProperty("user.dir")+"/productFile/"+ fileName;
                TxtUtil.createTxt(filePath,list,HttpGetBWDTO.class);

                //添加记录(更新)
                ClassFileInfo classFileInfo = new ClassFileInfo();
                classFileInfo.setMessageType(MessageType.HTTP_GET_POLICY.getId()); //5=httpge清洗名单
                classFileInfo.setClassFileName(fileName);
                classFileInfo.setFileType(fileType);
                classFileInfo.setVersionNo(versionNo);
               
                classFileInfo.setModifyTime(new Date());
                classFileInfo.setModifyOper(SpringUtil.getSysUserName());
                if(StringUtils.isEmpty(exitFileName)){
                	classFileInfo.setCreateOper(SpringUtil.getSysUserName());
                    classFileInfo.setCreateTime(new Date());
                    classFileInfoMapper.insertSelective(classFileInfo);
                }else {
                    classFileInfoMapper.updateByPrimaryKeySelective(classFileInfo);
                }
                
                valmap.put("filename", fileName);
                valmap.put("isupdate", true);
                valmap.put("filetype", fileType);
                return valmap;
            }
            return null;
        }else {
        	 valmap.put("filename", exitFileName);
             valmap.put("isupdate", false);
             valmap.put("filetype", fileType);
            return valmap;
        }
    }


    private synchronized HttpGetImportMgr getHttpGetImportMgr(){
        return (HttpGetImportMgr) SpringUtil.getBean("httpGetImportMgr");
    }

    /**
     * 前端参数type 和后台定义的文件类型之间的映射
     * @param type
     * @return
     */
    private Integer redirectValue( Integer type){

        Integer fileType = 0;
        switch (type){
            case 0 : fileType = FileTypeConstant.HFDW.getValue(); break;
            case 1 : fileType = FileTypeConstant.HFDB.getValue(); break;
            case 2 : fileType = FileTypeConstant.HFUB.getValue(); break;
        }
        return fileType;
    }
    /**
     * 判断是否需要提示
     * @param alarmType
     * @return
     */
    public Boolean isInAlarm(Integer alarmType){

        String var = AlarmClassInfoUtil.getInstance().getAlarmType(alarmType);
        Boolean flag = true;
        if(!"1".equals(var)){
            flag = false;
        }
        return flag;
    }
}

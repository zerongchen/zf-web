package com.aotain.zongfen.controller.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.utils.redis.AlarmClassInfoUtil;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.general.GeneralApp;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.appname.AppNameService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.PageResult;

@Controller
@RequestMapping("/generalAppName")
public class AppNameController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
	private AppNameService appNameService;
    
    @Autowired
    private CommonService commonService;

    /**
     * 
    * @Title: index
    * @Description: 首页访问
    * @param @return
    * @return String
    * @throws
     */
    @RequestMapping("index")
    public String index(){
        return "/generalAppName/index";
    }
   
	
    /**
     * 
    * @Title: getIndex
    * @Description: 获取首页显示数据 
    * @param @param pageSize
    * @param @param pageIndex
    * @param @param ipType
    * @param @param ipaddress
    * @param @return
    * @return PageResult<GenIPAddress>
    * @throws
     */
    @RequestMapping("getIndex")
    @ResponseBody
    @RequiresPermission(value = "zf104004_query")
    public PageResult<GeneralApp> getIndex(@RequestParam(required = true, name = "pageSize") Integer pageSize,
											@RequestParam(required = true, name = "pageIndex") Integer pageIndex,
											@RequestParam(required = true, name = "appType") String appType,
											@RequestParam(required = true, name = "appName") String appName){
        
    	try {
    		return appNameService.getIndexList(pageSize,pageIndex,appType,appName);
		} catch (Exception e) {
			logger.error("search fail",e);
		}
    	return null;
    }
    
    /**
     * 
    * @Title: impoertFile
    * @Description: 数据导入 
    * @param @param request
    * @param @return
    * @param @throws ImportException
    * @param @throws IOException
    * @return String
    * @throws
     */
    @RequestMapping(value="importFile", method = {  RequestMethod.POST })
    @ResponseBody
    @LogAnnotation()
    @RequiresPermission(value = "zf104004_import")
    public Map<String,String> impoertFile(HttpServletRequest request){
    	Map<String,String> message = new HashMap<String, String>();
    	String type = request.getParameter("operation");
		try {
			message = appNameService.uploadFile(request);
			String fileName =  commonService.saveFile(request, "appNameFile","appNameFile");
            String uploadFileName = ExcelUtil.getFileName(request,"appNameFile");
			String dataJson = "fileName="+uploadFileName+",saveFile="+fileName;
			if("0".equals(type)) {
				ProxyUtil.changeVariable(this.getClass(),"impoertFile",dataJson,dataJson, ModelType.MODEL_APPNAME_LIB.getModel(), OperationType.ALL_IMPORT); 
			 }else {
				ProxyUtil.changeVariable(this.getClass(),"impoertFile",dataJson,dataJson, ModelType.MODEL_APPNAME_LIB.getModel(), OperationType.INC_IMPORT);
			 }
		} catch (Exception e) {
			  logger.error("impoertFile fail",e);
		}
        return message;
    }
    
    /**
     * 
    * @Title: exportTemplate
    * @Description: 数据导出 
    * @param @param request
    * @param @param response
    * @return void
    * @throws
     */
    @RequestMapping(value="exportFile",produces={"text/html;charset=UTF-8;","application/json;"})
    @ResponseBody
//    @LogAnnotation()
    @RequiresPermission(value = "zf104004_export")
    public String exportTemplate(HttpServletRequest request,HttpServletResponse response) {
    	List<GeneralApp> list = appNameService.getAllAppName();
        List<List<?>> dataList = new ArrayList<>();
        List<Class<?>> classList = new ArrayList<Class<?>>();
        dataList.add(list);
        classList.add(GeneralApp.class);
        String fileName = "";
        try{    
        	fileName = commonService.exportData(dataList,classList,"应用名称对应库");
            String dataJson = "fileName="+fileName;
            ProxyUtil.changeVariable(this.getClass(),"exportTemplate",dataJson,dataJson, ModelType.MODEL_APPNAME_LIB.getModel(), OperationType.EXPORT);
        } catch (Exception e){
            logger.error("export fail",e);
        }
        return fileName;
    }
    
    /**
     * 
    * @Title: createAndSend
    * @Description: 生成并下发 
    * @param @return
    * @return String
    * @throws
     */
    @RequestMapping(value="createAndSend", method = {  RequestMethod.POST })
    @ResponseBody
    @LogAnnotation()
    @RequiresPermission(value = "zf104004_create")
    public String createAndSend(@RequestParam("serverId")Integer serverId) {
    	Map<String,String> result  = new HashMap<String, String>();
    	try{
    		result = appNameService.createAndSend(serverId);
            String dataJson = "fileName="+result.get("fileName")+",zongfenId="+serverId;
            ProxyUtil.changeVariable(this.getClass(),"createAndSend",dataJson,dataJson, ModelType.MODEL_APPNAME_LIB.getModel(), OperationType.PRODUCT);
        } catch (Exception e){
            logger.error("createAndSend fail",e);
        }
    	return result.get("message");
    }
    
    /**
     * 
    * @Title: uploadTemplate
    * @Description: 模板下载
    * @param @param request
    * @param @param response
    * @return void
    * @throws
     */
    @RequestMapping(value="uploadTemplate", method = {  RequestMethod.GET })
//    @LogAnnotation()
    public void uploadTemplate(HttpServletRequest request,HttpServletResponse response) {
    	try{
    		commonService.exportTemplete(request, response, "AppNameTableTemplete");
    		String dataJson = "fileName=AppNameTableTemplete.xlsx";
    		ProxyUtil.changeVariable(this.getClass(),"uploadTemplate",dataJson,dataJson, ModelType.MODEL_APPNAME_LIB.getModel(), OperationType.DOWNLOAD);
    	} catch (Exception e){
        logger.error("uploadTemplate fail",e);
    	}
    }
    
    /**
     * 
    * @Title: getStatus
    * @Description: 查询警告状态
    * @param @return
    * @return String
    * @throws
     */
    @RequestMapping(value="getStatus")
    @ResponseBody
    public String getStatus() {
    	return AlarmClassInfoUtil.getInstance().getAlarmType(MessageType.APP_NAME_TABLE_POLICY.getId());
    }
}

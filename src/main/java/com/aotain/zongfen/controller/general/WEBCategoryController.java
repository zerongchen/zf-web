package com.aotain.zongfen.controller.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.utils.ExcelUtil;
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
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.general.GeneralURL;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.weblibrary.WEBCategoryService;
import com.aotain.zongfen.utils.PageResult;

@Controller
@RequestMapping("/generalWEBCategory")
public class WEBCategoryController {
	
	private static Logger logger = LoggerFactory.getLogger(HttpGetListController.class);
	
    @Autowired
	private WEBCategoryService webCategoryService;
    
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
        return "/generalWEBCategory/index";
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
    @RequiresPermission(value = "zf104002_query")
    public PageResult<GeneralURL> getIndex(@RequestParam(required = true, name = "pageSize") Integer pageSize,
											@RequestParam(required = true, name = "pageIndex") Integer pageIndex,
											@RequestParam(required = true, name = "webType") String webType,
											@RequestParam(required = true, name = "hostName") String hostName){
        try {
        	return webCategoryService.getIndexList(pageSize,pageIndex,webType,hostName);
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
    @RequiresPermission(value = "zf104002_import")
    public Map<String,String>  impoertFile(HttpServletRequest request){
    	Map<String,String>  message= new HashMap<String, String>();
		try {
			 message = webCategoryService.uploadFile(request);
			 String operation = request.getParameter("operation");
			 String uploadFileName = ExcelUtil.getFileName(request,"webCategoryFile");
			//保存导入文件到本地，返回保存的文件名
			 String fileName =  commonService.saveFile(request, "webCategoryFile","webCategoryFile");
             String dataJson = "fileName="+ uploadFileName+",saveFile="+fileName;
			 if("0".equals(operation)) {
				 ProxyUtil.changeVariable(this.getClass(),"impoertFile",dataJson,dataJson, ModelType.MODEL_URL_LIB.getModel(), OperationType.ALL_IMPORT); 
			 }else {
				 ProxyUtil.changeVariable(this.getClass(),"impoertFile",dataJson,dataJson, ModelType.MODEL_URL_LIB.getModel(), OperationType.INC_IMPORT);
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
    @RequiresPermission(value = "zf104002_export")
    public String exportTemplate(HttpServletRequest request,HttpServletResponse response) {
        List<GeneralURL> list = webCategoryService.getAllUrl();
        List<List<?>> dataList = new ArrayList<>();
        List<Class<?>> classList = new ArrayList<Class<?>>();
        dataList.add(list);
        classList.add(GeneralURL.class);
        String fileName = "";
        try{
        	fileName = commonService.exportData(dataList,classList,"WEB分类库");
            String dataJson = "fileName="+fileName;
            ProxyUtil.changeVariable(this.getClass(),"exportTemplate",dataJson,dataJson, ModelType.MODEL_URL_LIB.getModel(), OperationType.EXPORT);
        } catch (Exception e){
            logger.error("exportTemplate fail"+e);
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
    @RequiresPermission(value = "zf104002_create")
    public String createAndSend(@RequestParam("serverId")Integer serverId) {
    	Map<String,String> result = new HashMap<String, String>();
    	try{
    		result = webCategoryService.createAndSend(serverId);
            String dataJson = "fileName="+result.get("fileName")+",zongfenId="+serverId;
            ProxyUtil.changeVariable(this.getClass(),"createAndSend",dataJson,dataJson, ModelType.MODEL_URL_LIB.getModel(), OperationType.PRODUCT);
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
    		commonService.exportTemplete(request, response, "WebCategoryLibTemplete");
    		String dataJson = "fileName=WebCategoryLibTemplete.xlsx";
    		ProxyUtil.changeVariable(this.getClass(),"uploadTemplate",dataJson,dataJson, ModelType.MODEL_URL_LIB.getModel(), OperationType.DOWNLOAD);
    	} catch (Exception e){
        logger.error("uploadTemplate fail"+e);
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
    	try {
    		return AlarmClassInfoUtil.getInstance().getAlarmType(MessageType.WEB_CLASS_TABLE_POLICY.getId());
		} catch (Exception e) {
			logger.error("getStatus fail",e);
		}
    	return null;
    }
    
}

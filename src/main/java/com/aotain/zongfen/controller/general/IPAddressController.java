package com.aotain.zongfen.controller.general;

import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.utils.redis.AlarmClassInfoUtil;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.general.ExportIP4Address;
import com.aotain.zongfen.model.general.ExportIP6Address;
import com.aotain.zongfen.model.general.GenIPAddress;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.ipaddress.IPAddressService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.PageResult;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/generalIPAddress")
public class IPAddressController {
	
		private static Logger logger = LoggerFactory.getLogger(HttpGetListController.class);
	
	    @Autowired
		private IPAddressService ipAddressService;
	    
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
	        return "/generalIPAddress/index";
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
		@RequiresPermission(value = "zf104003_query")
	    public PageResult<GenIPAddress> getIndex(@RequestParam(required = true, name = "pageSize") Integer pageSize,
												@RequestParam(required = true, name = "pageIndex") Integer pageIndex,
												@RequestParam(required = true, name = "ipType") String ipType,
												@RequestParam(required = true, name = "ipaddress") String ipaddress){
	        return ipAddressService.getIndexList(pageSize,pageIndex,ipType,ipaddress);
	    }
	    
	    /**
	     * 
	    * @Title: importFile
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
		@RequiresPermission(value = "zf104003_import")
	    public Map<String,String> importFile(HttpServletRequest request){
			Map<String,String> message = Maps.newHashMap();
	    	String type = request.getParameter("type");
			try {
				message = ipAddressService.uploadFile(request);

			} catch (Exception e) {
				logger.error("",e);
				message.put("status", "1");
				message.put("message", "导入任务失败。");
				return message;
			}

			try {
				//保存导入文件到本地，返回保存的文件名
				String fileName =  commonService.saveFile(request, "ipAddressFile","ipAddressFile");
				String uploadFileName = ExcelUtil.getFileName(request,"ipAddressFile");
				String dataJson = "fileName="+uploadFileName+",saveFile="+fileName;
				if("0".equals(type)) {
                    ProxyUtil.changeVariable(this.getClass(),"importFile",dataJson,dataJson, ModelType.MODEL_IP_ADDR_LIB.getModel(), OperationType.ALL_IMPORT);
                }else {
                    ProxyUtil.changeVariable(this.getClass(),"importFile",dataJson,dataJson, ModelType.MODEL_IP_ADDR_LIB.getModel(), OperationType.INC_IMPORT);
                }
			} catch (Exception e) {
				logger.error("",e);
				message.put("status", "2");
				message.put("message", "导入任务写日志失败。");
				return message;
			}
			return message;
	    }
	/**
	 *
	 * @Title: getStatus
	 * @Description: 查询导入进度
	 * @param @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value="queryProgress")
	@ResponseBody
	public Map<String,String> queryProgress(HttpServletRequest request) {
		return ipAddressService.queryProgress(request);
	}
	/**
	 *
	 * @Title: getStatus
	 * @Description: 查询导入文件结果信息
	 * @param @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value="queryResultFile")
	@ResponseBody
	public void queryResultFile(HttpServletRequest request,HttpServletResponse response) {
		 ipAddressService.queryResultFile(request,response);
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
		@RequiresPermission(value = "zf104003_create")
	    public String createAndSend(@RequestParam("serverId")Integer serverId) {
	    	Map<String,String> result = ipAddressService.createAndSend(serverId);
	    	try{
	            String dataJson = "fileName="+result.get("fileName")+",zongfenId="+serverId;
	            ProxyUtil.changeVariable(this.getClass(),"createAndSend",dataJson,dataJson, ModelType.MODEL_IP_ADDR_LIB.getModel(), OperationType.PRODUCT);
	        } catch (Exception e){
	            logger.info("bangLog==========change LogAnnotation failed...========="+e);
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
//	    @LogAnnotation()
	    public void uploadTemplate(HttpServletRequest request,HttpServletResponse response) {
	    	commonService.exportTemplete(request, response, "IpAddressLibTemplete");
	    	try{
	    		String dataJson = "fileName=IpAddressLibTemplete.xlsx";
	    		ProxyUtil.changeVariable(this.getClass(),"uploadTemplate",dataJson,dataJson, ModelType.MODEL_IP_ADDR_LIB.getModel(), OperationType.DOWNLOAD);
	    	} catch (Exception e){
	        logger.info("bangLog==========change LogAnnotation failed...========="+e);
	    	}
	    }
	    
	    
	    @RequestMapping(value="exportFile",produces={"text/html;charset=UTF-8;","application/json;"})
	    @ResponseBody
//	    @LogAnnotation()
		@RequiresPermission(value = "zf104003_export")
	    public String exportFile(HttpServletRequest request,HttpServletResponse response) {
	        List<GenIPAddress> listV4 = ipAddressService.getIpV4();
	        List<GenIPAddress> listV6 = ipAddressService.getIpV6();
	        List<List<?>> dataList = new ArrayList<>();
	        List<Class<?>> classList = new ArrayList<Class<?>>();
	        dataList.add(listV4);
	        classList.add(ExportIP4Address.class);
	        
	        dataList.add(listV6);
	        classList.add(ExportIP6Address.class);
	        
	        String fileName = commonService.exportData(dataList,classList,"IP地址库");
	        try{
	            String dataJson = "fileName="+fileName;
	            ProxyUtil.changeVariable(this.getClass(),"exportFile",dataJson,dataJson, ModelType.MODEL_IP_ADDR_LIB.getModel(), OperationType.EXPORT);
	        } catch (Exception e){
	            logger.info("bangLog==========change LogAnnotation failed...========="+e);
	        }
	        return fileName;
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
	    	return AlarmClassInfoUtil.getInstance().getAlarmType(MessageType.IP_ADDRESS_ALLOCATION_POLICY.getId());
	    }
}

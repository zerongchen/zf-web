package com.aotain.zongfen.controller.general;

import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.controller.apppolicy.FlowMarkController;
import com.aotain.zongfen.dto.general.FileDetailListDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.general.ClassInfo;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.classinfo.ClassInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/classinfo")
public class ClassInfoController extends BaseController{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClassInfoService classInfoService;
    
    @Autowired
    private CommonService commonService;

    @RequestMapping("index")
    public String index(){
        return "/classinfo/index";
    }


    @RequestMapping("initTable")
    @ResponseBody
    public List<ClassInfo> getClassInfos( ClassInfo classInfo){
        return classInfoService.getClassInfos(classInfo);
    }


//    @RequestMapping("insertOrUpdate")
//    @ResponseBody
//    @Deprecated
//    public ResponseResult<ClassInfo> insertOrUpdate( HttpServletRequest request,ClassInfo classInfo){
//        String createTimeStr = request.getParameter("createTimeStr");
//        try {
//            if(!StringUtils.isEmpty(createTimeStr)){
////                classInfo.setCreateTime(DateUtils.parseTimesTampSql(createTimeStr));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return  classInfoService.insertOrUpdate(classInfo);
//    }
//
//
//    @RequestMapping("delete")
//    @ResponseBody
//    @Deprecated
//    public void deleteInfo( HttpServletRequest request){
//        String[] strs = request.getParameterValues("classIds[]");
//        classInfoService.deleteClassInfos(strs);
//    }
    
    @RequestMapping("getFileDetail")
    @ResponseBody
    @RequiresPermission(value = {"zf104002_query","zf104003_query","zf104004_query","zf104005_query"})
    public List<FileDetailListDTO> getFileDetail(@RequestParam("messageType")Integer messageType){
    	try {
    		return classInfoService.getFileDetail(messageType);
		} catch (Exception e) {
			 logger.error("search fail",e);
		}
    	return null;
    }

    @RequestMapping("reSend")
    @ResponseBody
    public ResponseResult reSend(@RequestBody FileDetailListDTO detailListDTO){
        ResponseResult responseResult =new ResponseResult();
        try {
        	String message = classInfoService.reSend(detailListDTO);
            List<BaseKeys> keys = new ArrayList<>();
            if ("success".equals(message)){
                responseResult.setResult(1);

                BaseKeys bk = new BaseKeys();
                bk.setDataType(DataType.OTHER.getType());
                bk.setOperType(OperationType.RESEND.getType());
                bk.setMessageType(detailListDTO.getMessageType());
                bk.setMessage("messageNo="+detailListDTO.getMessageNo());
                keys.add(bk);

            }else {
                responseResult.setResult(0);
                responseResult.setMessage(message);
            }
            responseResult.setKeys(keys);
		} catch (Exception e) {
			logger.error("resend fail",e);
		}
        return responseResult;
    }
    
    @RequestMapping(value="downLoadFile", method = {  RequestMethod.GET })
    @LogAnnotation()
    public ResponseResult downLoadFile(HttpServletRequest request,HttpServletResponse response,@RequestParam("fileName") String fileName,@RequestParam("messageType") Integer messageType) {
    	commonService.downLoadFile(request, response, "productFile", fileName);
        try{
            String dataJson = "fileName="+fileName+",messageType"+messageType;
            ProxyUtil.changeVariable(this.getClass(),"downLoadFile",dataJson,dataJson, ModelType.getModelType(messageType),OperationType.DOWNLOAD);
        } catch (Exception e){
//            logger.error("bangLog==========change dataJson failed...========="+e);
            logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
        }

    	return wrapReturnDate();
    }
    
    /**
     * 
    * @Title: getServerList
    * @Description: 获取服务器列表 
    * @param @return
    * @return List<ZongFenDevice>
    * @throws
     */
    @RequestMapping(value="getServerList")
    @ResponseBody
    public List<ZongFenDevice> getServerList() {
    	try {
    		return classInfoService.getServerList();
		} catch (Exception e) {
			 logger.error("search fail",e);
		}
    	return null;
    }
    
    /**
     * 
    * @Title: exportExcelFile
    * @Description: excel文件导出下载
    * @param @param request
    * @param @param response
    * @param @param fileName
    * @return void
    * @throws
     */
    @RequestMapping(value="exportExcelFile", method = {  RequestMethod.GET })
    public void exportExcelFile(HttpServletRequest request,HttpServletResponse response,@RequestParam("fileName") String fileName) {
    	try {
    		commonService.downLoadFile(request, response, "downLoad", fileName);
		} catch (Exception e) {
			logger.error("export fail",e);
		}
    	
    }
}

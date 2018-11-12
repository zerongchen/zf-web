package com.aotain.zongfen.controller.general;

import com.alibaba.fastjson.JSON;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.general.HttpGetBWDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.general.HttpGetBW;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.httpget.HttpGetListService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("httpget")
public class HttpGetListController {

    private static Logger logger = LoggerFactory.getLogger(HttpGetListController.class);

    @Autowired
    private HttpGetListService httpGetListService;

    @Autowired
    private CommonService commonService;

    @RequestMapping("index")
    public String index(){
        return "/httpgetlist/index";
    }

    @RequestMapping(value = "listDomain",method = {  RequestMethod.POST })
    @ResponseBody
    @RequiresPermission(value = "zf104005_query")
    public PageResult<HttpGetBWDTO> listDomain( HttpGetBW httpGetBW,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize ){
        PageHelper.startPage(page, pageSize);
        List<HttpGetBWDTO> lists = httpGetListService.getDomain(httpGetBW);
        PageInfo<HttpGetBWDTO> pageInfo = new PageInfo<HttpGetBWDTO>(lists);
        PageResult<HttpGetBWDTO> pageResult = new PageResult<HttpGetBWDTO>(pageInfo.getTotal(),lists);
        return pageResult;
    }

    /**
     * 增量导入
     * @param request
     * @return
     */
    @RequestMapping(value = "incrementalImport",method = {  RequestMethod.POST })
    @ResponseBody
    @LogAnnotation()
    @RequiresPermission(value = "zf104005_import")
    public ImportResultList incrementalImportData( HttpServletRequest request){

         ImportResultList imp = handleImport(request);
         if(imp.getResult()==ImportConstant.DATA_IMPORT_SUCCESS){
             try{
            	//保存导入文件到本地，返回保存的文件名
            	 String fileName =  commonService.saveFile(request, "importFile","httpGetFile");
                 String uploadFileName = ExcelUtil.getFileName(request,"importFile");
            	 String dataJson = "fileName="+ uploadFileName+",saveFile="+fileName;
                 ProxyUtil.changeVariable(this.getClass(),"incrementalImportData",dataJson,dataJson, ModelType.MODEL_HTTPGET_LIB.getModel(), OperationType.INC_IMPORT);
             } catch (Exception e){
                 logger.info(" http get incrementalImport failed...",e);
             }

         }
        return imp;
    }

    /**
     * 覆盖导入,全量导
     * @param request
     * @return
     */
    @RequestMapping(value = "overrideImport",method = {  RequestMethod.POST })
    @ResponseBody
    @LogAnnotation()
    @RequiresPermission(value = "zf104005_import")
    public ImportResultList overrideImportData( HttpServletRequest request){
        ImportResultList imp = handleImport(request);
        String fileName =  commonService.saveFile(request, "importFile","httpGetFile");
        if(imp.getResult()==ImportConstant.DATA_IMPORT_SUCCESS){
            try{
                String dataJson = "fileName="+ ExcelUtil.getFileName(request,"importFile")+",saveFile="+fileName;
                ProxyUtil.changeVariable(this.getClass(),"overrideImportData",dataJson,dataJson, ModelType.MODEL_HTTPGET_LIB.getModel(), OperationType.ALL_IMPORT);
            } catch (Exception e){
                logger.info("http get overrideImport failed...",e);
            }
        }
        return imp;
    }


    @RequestMapping("exportTemplate")
    @ResponseBody
//    @LogAnnotation
    public void exportTemplate( HttpServletRequest request, HttpServletResponse response){
        try{
//            String dataJson = "fileName=HttpgetFilterTemplete.xlsx";
//            ProxyUtil.changeVariable(this.getClass(),"exportTemplate",dataJson,"", ModelType.MODEL_HTTPGET_LIB.getModel(), OperationType.EXPORT);
            commonService.exportTemplete(request,response,"HttpgetFilterTemplete");
        } catch (Exception e){
            logger.info("http get exportTemplate failed..",e);
        }
    }

    @RequestMapping(value="export.do", method = {  RequestMethod.GET })
    @ResponseBody
    @LogAnnotation
    @RequiresPermission(value = "zf104005_export")
    public void export( HttpServletRequest request, HttpServletResponse response) {
        try{
            HttpGetBW httpGetBW = new HttpGetBW();
            Integer type = request.getParameter("type")!=null?Integer.parseInt(request.getParameter("type")):0;
            String name=null;
            if(type == 0){
                name = "域名白名单";
            }else if(type ==1){
                name = "域名黑名单";
            }else {
                name= "URL黑名单";
            }
            httpGetBW.setType(type);
            List<HttpGetBWDTO> lists = httpGetListService.getDomain(httpGetBW);
            List<Class<?>> classList = new ArrayList<Class<?>>();
            List<List<?>> dataList = new ArrayList<List<?>>();
            dataList.add(lists);
            classList.add(HttpGetBWDTO.class);
            String fileName = commonService.exportData(dataList,classList,name,response,request);

            String dataJson = "fileName="+fileName;
            ProxyUtil.changeVariable(this.getClass(),"export",dataJson,"{type:"+type+"}", ModelType.MODEL_HTTPGET_LIB.getModel(), OperationType.EXPORT);
        } catch (Exception e){
            logger.info("http get export failed...=========",e);
        }
    }

    @RequestMapping(value = "createAndSend",method = {RequestMethod.POST})
    @ResponseBody
    @LogAnnotation
    @RequiresPermission(value = "zf104005_create")
    public String send(HttpServletRequest request,HttpGetBW httpGetBW){
        try {
            Integer zid = Integer.parseInt(request.getParameter("zongfenId"));
            return httpGetListService.createAndSend(httpGetBW,zid);
        }catch (Exception e){
            logger.error("http get create & send error",e);
            return null;
        }

    }


    @RequestMapping(value = "delete",method = {RequestMethod.POST})
    @ResponseBody
    @LogAnnotation
    @RequiresPermission(value = "zf104005_delete")
    public void deleteBw(HttpServletRequest request){
        try {

            String[] ids = request.getParameterValues("ids[]");
            String type = request.getParameter("type");

            Object data = null;
            if (ids.length==1){
                data = ids[0];
            } else {
                data = JSON.toJSONString(ids);
            }
            String dataJson = "type="+type+",id="+ data;
            ProxyUtil.changeVariable(this.getClass(),"deleteBw",dataJson,dataJson, ModelType.MODEL_HTTPGET_LIB.getModel(), OperationType.DELETE);
            httpGetListService.deleteBw(ids,Integer.parseInt(type));
        } catch (Exception e){
            logger.info("http get deleteBw failed...=========",e);
        }
    }

    @RequestMapping(value = "batchDelete",method = {RequestMethod.POST})
    @ResponseBody
    @LogAnnotation
    @RequiresPermission(value = "zf104005_delete")
    public void batchDeleteBw(HttpServletRequest request){

        try{
            String[] ids = request.getParameterValues("ids[]");
            String type = request.getParameter("type");
            String dataJson = "type="+type+",id="+ JSON.toJSONString(ids);
            ProxyUtil.changeVariable(this.getClass(),"batchDeleteBw",dataJson,dataJson, ModelType.MODEL_HTTPGET_LIB.getModel(), OperationType.DELETE);
            httpGetListService.deleteBw(ids,Integer.parseInt(type));
        } catch (Exception e){
            logger.info("batchDelete  failed...",e);
        }


    }

    @RequestMapping(value = "update",method = {RequestMethod.POST})
    @ResponseBody
    @LogAnnotation
    @RequiresPermission(value = "zf104005_modify")
    public ResponseResult<HttpGetBW> updateBw( HttpGetBW httpGetBW){
        ResponseResult responseResult = new ResponseResult();
        try {
            try{
                String dataJson = "type="+httpGetBW.getType()+",id="+httpGetBW.getId();
                ProxyUtil.changeVariable(this.getClass(),"updateBw",dataJson, JSON.toJSONString(httpGetBW), ModelType.MODEL_HTTPGET_LIB.getModel(), OperationType.MODIFY);
            } catch (Exception e){
                logger.info("bangLog==========change LogAnnotation failed...========="+e);
            }

            return httpGetListService.updateBw(httpGetBW);
        }catch (Exception e){
            logger.error("update HTTP GET domain error",e);
            responseResult.setResult(0);
            responseResult.setMessage("未知错误，请刷新页面重试");
        }
        return responseResult;
    }

    @RequestMapping(value = "getWarming",method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Integer> warmJson(){
        return httpGetListService.getWarning();
    }

    /**
     * 处理导入
     * @param request
     * @return
     */
    private ImportResultList handleImport(HttpServletRequest request){

        try {
            return httpGetListService.handleImport(request);
        } catch (ImportException e) {
            logger.debug("catch import info that cause fail",e);
            return e.getImportResultList();
        } catch (Exception ex){
            logger.error("import Http GET list error",ex);
            ImportResultList importResultList = new ImportResultList();
            importResultList.setResult(ImportConstant.DATA_IMPORT_FAIL);
            importResultList.setDescribtion("未知错误，请刷新页面重试");
            return importResultList;
        }
    }

}

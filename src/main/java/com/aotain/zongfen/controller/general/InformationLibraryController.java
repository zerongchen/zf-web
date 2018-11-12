package com.aotain.zongfen.controller.general;

import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.dto.general.*;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.BaseEntity;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.general.TriggerHostList;
import com.aotain.zongfen.model.general.TriggerKWList;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.informationlibrary.InformationLibraryService;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.model.general.TriggerHost;
import com.aotain.zongfen.model.general.TriggerKW;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/infoLibrary")
public class InformationLibraryController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(InformationLibraryController.class);

    @Autowired
    private InformationLibraryService informationLibraryService;

    @Autowired
    private CommonService commonService;

    @RequestMapping("index")
    public String index(){

        return "/informationpush/triggerlibrary";
    }

    @RequestMapping(value = "getTriggerHost",method = {  RequestMethod.POST })
    @ResponseBody
    @RequiresPermission(value = "zf104001_query")
    public List<TriggerHostDTO> getTriggerHost(TriggerHost triggerHost,
                                               @RequestParam(required = true, defaultValue = "1") Integer infoType,
                                               @RequestParam(required = false) String listName,String startTime,String endTime) throws Exception{
        try{
            triggerHost.setHostListtype(infoType);
            triggerHost.setTriggerHostListname(listName);
            triggerHost.setStartTime(startTime);
            triggerHost.setEndTime(endTime);
            return informationLibraryService.getTriggerHost(triggerHost);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping(value = "getTriggerHostList",method = {  RequestMethod.POST })
    @ResponseBody
    @RequiresPermission(value = "zf104001_query")
    public PageResult getTriggerHostList( HttpServletRequest request,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws Exception{
        try{
            TriggerHostListDTO triggerHostListDTO = new TriggerHostListDTO();
            triggerHostListDTO.setTriggerHostListid(Long.valueOf(request.getParameter("id")));
            PageHelper.startPage(page, pageSize);
            List<TriggerHostListDTO> lists = informationLibraryService.getTriggerHostList(triggerHostListDTO);
            PageInfo<TriggerHostListDTO> pageInfo = new PageInfo<TriggerHostListDTO>(lists);
            PageResult pageResult = new PageResult(pageInfo.getTotal(),lists);
            return pageResult;
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }


    @RequestMapping(value = "getTriggerKW",method = {  RequestMethod.POST })
    @ResponseBody
    @RequiresPermission(value = "zf104001_query")
    public List<TriggerKWDTO> getTriggerKW( TriggerKW triggerKW,
                                            @RequestParam(required = false) String listName,String startTime,String endTime) throws Exception{
        try{
            triggerKW.setTriggerKwListname(listName);
            triggerKW.setStartTime(startTime);
            triggerKW.setEndTime(endTime);
            return informationLibraryService.getTriggerKW(triggerKW);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping(value = "getTriggerKWList",method = {  RequestMethod.POST })
    @ResponseBody
    @RequiresPermission(value = "zf104001_query")
    public PageResult getTriggerKWList( HttpServletRequest request,
                                                    @RequestParam(required = false, defaultValue = "1") Integer page,
                                                    @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws Exception{
        try{
            TriggerKWListDTO triggerKWListDTO  = new TriggerKWListDTO();
            triggerKWListDTO.setTriggerKwListid(Long.valueOf(request.getParameter("id")));
            PageHelper.startPage(page, pageSize);
            List<TriggerKWListDTO> lists = informationLibraryService.getTriggerKWList(triggerKWListDTO);
            PageInfo<TriggerKWListDTO> pageInfo = new PageInfo<TriggerKWListDTO>(lists);
            PageResult pageResult = new PageResult(pageInfo.getTotal(),lists);
            return pageResult;
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping(value = "import",method = {  RequestMethod.POST })
    @ResponseBody
    @RequiresPermission(value = "zf104001_import")
    public ImportResultList importData(HttpServletRequest request, BaseEntity baseEntity) {

        String createOper = SpringUtil.getSysUserName();
        baseEntity.setCreateOper(createOper);
        baseEntity.setModifyOper(createOper);
        baseEntity.setCreateTime(new Date());
        baseEntity.setModifyTime(new Date());


        ImportResultList importResultList = new ImportResultList();
        try {
            boolean result = informationLibraryService.handleImport(request,baseEntity);
            //保存导入文件到本地，返回保存的文件名
            String fileName =  commonService.saveFile(request, "importFile","informationPush");
            if ( !result ){
                importResultList.setResult(ImportConstant.DATA_IMPORT_FAIL_DUPLICATNAME);
                importResultList.setDescribtion("存在列表名称相同的记录");
                return importResultList;
            }
        } catch (ImportException e) {
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            importResultList = e.getImportResultList();
        }
        catch (Exception ex){
            MonitorStatisticsUtils.addEvent(ex);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),ex);
            importResultList.setResult(ImportConstant.DATA_IMPORT_FAIL);
            importResultList.setDescribtion("未知错误，请重试");
        }
        return importResultList;

    }

    @RequestMapping(value="export.do", method = {  RequestMethod.GET })
    @ResponseBody
//    @LogAnnotation(module = 104001,type = 7)
    @RequiresPermission(value = "zf104001_export")
    public void export( HttpServletRequest request, HttpServletResponse response) throws Exception{

        try{
            Integer infoType = Integer.parseInt(request.getParameter("infoType"));
            Long id = Long.valueOf(request.getParameter("id"));
            Long messageNo = Long.valueOf(request.getParameter("messageNo"));
            String name = request.getParameter("name");
            List<List<?>> dataList = new ArrayList<>();
            List<Class<?>> classList = new ArrayList<Class<?>>();
            if(infoType == 1 || infoType == 2){
                TriggerHostListDTO triggerHostListDTO = new TriggerHostListDTO();
                triggerHostListDTO.setTriggerHostListid(id);
                List<TriggerHostListDTO> list = informationLibraryService.getTriggerHostList(triggerHostListDTO);
                dataList.add(list);
                classList.add(TriggerHostListDTO.class);

            }else if(infoType == 0){
                TriggerKWListDTO triggerKWListDTO = new TriggerKWListDTO();
                triggerKWListDTO.setTriggerKwListid(id);
                List<TriggerKWListDTO> list = informationLibraryService.getTriggerKWList(triggerKWListDTO);
                dataList.add(list);
                classList.add(TriggerKWListDTO.class);

            }

            commonService.exportData(dataList,classList,name,response,request);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    /**
     * 删除列表
     * @param request
     */
    @RequestMapping("/delete")
    @ResponseBody
    @LogAnnotation(module = 104001,type = 3)
    @RequiresPermission(value = "zf104001_delete")
    public void delete(HttpServletRequest request) throws Exception{

        try{
            String[] ids = request.getParameterValues("ids[]");
            String[] messageNos = request.getParameterValues("messageNos[]");
            Integer type =Integer.parseInt(request.getParameter("infoType"));
            informationLibraryService.deleteByType(type,ids);
            String dataJson = "";
            Object messageNo = null;
            if (messageNos.length==1){
                messageNo = messageNos[0];
            } else {
                messageNo = Arrays.asList(messageNos);
            }
            dataJson = "type="+type+",messageNo="+ messageNo;
            ProxyUtil.changeVariable(InformationLibraryController.class,"delete",dataJson);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            throw new Exception("应用异常",e);
        }
    }

    /**
     * 删除详情列表数据
     * @param request
     */
    @RequestMapping("/deleteList")
    @ResponseBody
    @LogAnnotation(module = 104001,type = 2)
    @RequiresPermission(value = "zf104001_modify")
    public void deleteList(HttpServletRequest request) throws Exception{

        try{
            String[] childIds = request.getParameterValues("childIds[]");
            Integer type =Integer.parseInt(request.getParameter("infoType"));
            Long chooseId =Long.parseLong(request.getParameter("chooseId"));
            String messageNo =request.getParameter("messageNo");
            informationLibraryService.deleteList(type,childIds,chooseId);
            informationLibraryService.sendPolicyAfterUpdateOrDelete(type,Long.valueOf(chooseId));
            String dataJson = "";
            dataJson = "type="+type+",messageNo="+ messageNo;
            ProxyUtil.changeVariable(InformationLibraryController.class,"deleteList",dataJson);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping("/insertOrUpdate")
    @ResponseBody
    @LogAnnotation(module = 104001,type = 2)
    @RequiresPermission(value = "zf104001_modify")
    public ResponseResult insertOrUpdate( HttpServletRequest request){
        ResponseResult responseResult = new ResponseResult();

        try {
            String oper = SpringUtil.getSysUserName();

            Integer infoType = Integer.parseInt(request.getParameter("infoType"));
            String names = request.getParameter("names");
            String chooseId = request.getParameter("chooseId");
            String messageNo = request.getParameter("messageNo");
            String[] name = names.split(",");

            if (infoType == 1 || infoType == 2) {
                for (int i=0;i<name.length;i++){
                    if (StringUtils.isEmpty(name[i])){
                        continue;
                    }
                    TriggerHostList triggerHostList = new TriggerHostList();
                    triggerHostList.setTriggerHostListid(Long.valueOf(chooseId));
                    triggerHostList.setHostName(name[i]);
                    int len = informationLibraryService.countHostName(triggerHostList);
                    if(len>0){
                        responseResult.setResult(0);
                        responseResult.setMessage("网站"+name[i]+"已经存在，无法添加");
                        return responseResult;
                    }
                }


                for(int i=0;i<name.length;i++){
                    if (StringUtils.isEmpty(name[i])){
                        continue;
                    }
                    TriggerHostList triggerHostList = new TriggerHostList();
                    triggerHostList.setTriggerHostListid(Long.valueOf(chooseId));
                    triggerHostList.setHostName(name[i]);
                    triggerHostList.setOperateType(OperationConstants.OPERATION_SAVE);
                    triggerHostList.setCreateTime(new Date());
                    triggerHostList.setModifyTime(new Date());
                    triggerHostList.setCreateOper(oper);
                    triggerHostList.setModifyOper(oper);
                    responseResult = informationLibraryService.insertOrUpdateHostList(triggerHostList);

                    try{
                        String dataJson = "type="+infoType+",messageNo="+messageNo;
                        ProxyUtil.changeVariable(InformationLibraryController.class,"insertOrUpdate",dataJson,triggerHostList.toString());
                    } catch (Exception e){
                        logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
                    }
                }

                informationLibraryService.sendPolicyAfterUpdateOrDelete(infoType,Long.valueOf(chooseId));
                return responseResult;

            } else {
                for (int i=0;i<name.length;i++){
                    if (StringUtils.isEmpty(name[i])){
                        continue;
                    }
                    TriggerKWList triggerKWList = new TriggerKWList();
                    triggerKWList.setTriggerKwListid(Long.valueOf(chooseId));
                    triggerKWList.setKwName(name[i]);
                    int len = informationLibraryService.countkwName(triggerKWList);
                    if(len>0){
                        responseResult.setResult(0);
                        responseResult.setMessage("网站已经存在，不修改请点击取消");
                        return responseResult;
                    }
                }

                for (int i=0;i<name.length;i++){
                    if (StringUtils.isEmpty(name[i])){
                        continue;
                    }
                    TriggerKWList triggerKWList = new TriggerKWList();
                    triggerKWList.setTriggerKwListid(Long.valueOf(chooseId));
                    triggerKWList.setKwName(name[i]);
                    triggerKWList.setOperateType(OperationConstants.OPERATION_SAVE);
                    triggerKWList.setCreateTime(new Date());
                    triggerKWList.setModifyTime(new Date());
                    triggerKWList.setCreateOper(oper);
                    triggerKWList.setModifyOper(oper);
                    responseResult = informationLibraryService.insertOrUpdateKwList(triggerKWList);

                    try{
                        String dataJson = "type="+infoType+",messageNo="+messageNo;
                        ProxyUtil.changeVariable(InformationLibraryController.class,"insertOrUpdate",dataJson,triggerKWList.toString());
                    } catch (Exception e){
                        logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
                    }
                }

                informationLibraryService.sendPolicyAfterUpdateOrDelete(infoType,Long.valueOf(chooseId));
                return responseResult;

            }
        }catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            responseResult.setResult(0);
            responseResult.setMessage("未知错误，请刷新页面，重新操作");
        }
        return responseResult;
    }

    /**
     * 生成列表，重新下发
     * @param id
     * @param infoType
     */
    @RequestMapping("/reIssued")
    @ResponseBody
    public void reIssued(@RequestParam(required = true) Integer id,
                         @RequestParam(required = true) Integer infoType) throws Exception{
        try{
            informationLibraryService.handReIssuedBaseTypeAndId(infoType,id);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping("exportTemplate")
    @ResponseBody
//    @LogAnnotation(module = 104001,type = 7)
    @RequiresPermission(value = "zf104001_export")
    public void exportTemplate( HttpServletRequest request, HttpServletResponse response) throws Exception{
        try{
            int infoType = Integer.valueOf(request.getParameter("infoType"));
            String fileName = "";
            if (infoType==0){
                fileName = "TriggerKWTemplate";
            } else {
                fileName = "TriggerHostTemplate";
            }
            commonService.exportTemplete(request,response,fileName);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping(value = {"/resend"})
    @ResponseBody
    @LogAnnotation(module = 104001,type = 8)
    @RequiresPermission(value = "zf104001_redo")
    public ResponseResult resendPolicy(@RequestParam(value = "messageNo") Long messageNo,
                                       @RequestParam(value = "infoType") int infoType,
                                       @RequestParam(value = "ips[]",required=false) List<String> ips) throws Exception{
        try{
            if (ips==null||ips.size()==0){
                ips = new ArrayList<>();
            }
            informationLibraryService.resendPolicy(infoType,0L,messageNo,ips);

            try{
                String dataJson = "type="+infoType+",messageNo="+messageNo;
                ProxyUtil.changeVariable(InformationLibraryController.class,"resendPolicy",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }

            return wrapReturnDate();
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }
}

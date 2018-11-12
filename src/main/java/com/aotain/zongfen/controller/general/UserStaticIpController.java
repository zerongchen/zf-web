package com.aotain.zongfen.controller.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.apppolicy.AppUserController;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.dto.general.user.IpUserDTO;
import com.aotain.zongfen.dto.general.user.UserStaticIPDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.general.user.UserStaticIP;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.userstaticip.UserStaticIpService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("userip")
public class UserStaticIpController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(UserStaticIpController.class);

    @Autowired
    private UserStaticIpService staticIpService;

    @Autowired
    private CommonService commonService;

    @RequestMapping("index")
    public String index(){
        return "/userstaticip/index";
    }

    @RequestMapping("getList")
    @RequiresPermission(value = "zf103002_query")
    @ResponseBody
    public PageResult getList( IpUserDTO ipUserDTO,
                               @RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "10") Integer pageSize ){

        PageHelper.startPage(page, pageSize);
        List<IpUserDTO> lists = staticIpService.getList(ipUserDTO);
        PageInfo<IpUserDTO> pageInfo = new PageInfo<IpUserDTO>(lists);
        PageResult pageResult = new PageResult(pageInfo.getTotal(),lists);
        return pageResult;

    }

    @RequestMapping("deleteUser")
    @RequiresPermission(value = "zf103002_delete")
    @ResponseBody
    public ResponseResult deleteUser( HttpServletRequest request){
       String[] idStrs = request.getParameterValues("userid[]");
        return staticIpService.deleteByUserIds(idStrs);
    }

    @RequestMapping("deleteIp")
    @RequiresPermission(value = "zf103002_delete")
    @ResponseBody
    public ResponseResult deleteIp( HttpServletRequest request){
        String[] idStrs = request.getParameterValues("ipId[]");
        staticIpService.deleteByIpIds(idStrs);
        List<BaseKeys> keys = new ArrayList<>();
        BaseKeys bk = new BaseKeys();
        bk.setOperType(OperationType.DELETE.getType());
        bk.setMessageType(ModelType.MODEL_USER_STATICIP.getMessageType());
        bk.setDataType(DataType.OTHER.getType());
        bk.setMessage("IP_ID="+JSON.toJSONString(idStrs));
        keys.add(bk);
        return wrapReturnDate(keys);
    }

    @RequestMapping("insert")
    @RequiresPermission(value = "zf103002_add")
  //  @LogAnnotation(module = 103002,type = OperationConstants.OPERATION_SAVE)
    @ResponseBody
    public ResponseResult insert( UserStaticIPDTO userStaticIPDTO){
        return staticIpService.insert(userStaticIPDTO,1);
    }

    @RequestMapping("update")
    @RequiresPermission(value = "zf103002_modify")
  //  @LogAnnotation(module = 103002,type = OperationConstants.OPERATION_UPDATE)
    @ResponseBody
    public ResponseResult update( UserStaticIP userStaticIP){
        return staticIpService.update(userStaticIP);
    }

    /**
     * 增量导入
     * @param request
     * @return
     */
    @RequestMapping(value = "incrementalImport",method = {  RequestMethod.POST })
    @RequiresPermission(value = "zf103002_import")
    @ResponseBody
    public ResponseResult<ImportResultList> incrementalImportData( HttpServletRequest request){
        ImportResultList importResultList = handleImport(request);
        if(importResultList.getResult()==ImportConstant.DATA_IMPORT_SUCCESS){
            BaseKeys bk = new BaseKeys();
            bk.setOperType(OperationType.INC_IMPORT.getType());
            bk.setMessageType(ModelType.MODEL_USER_STATICIP.getMessageType());
            bk.setDataType(DataType.OTHER.getType());
            //获取导入原文件名
            String originalFileName = ExcelUtil.getFileName(request,"importFile");
            //保存导入文件到本地，返回保存的文件名(originalFileName+时间戳)
            String saveFileName =  commonService.saveFile(request, "importFile",originalFileName.substring(0,originalFileName.lastIndexOf(".")));
            bk.setMessage("filename="+originalFileName+",savefile="+saveFileName);
            return wrapReturnDate(importResultList, Arrays.asList(bk));
        }else {
            return wrapReturnDate(importResultList, new ArrayList<>());
        }
    }

    /**
     * 覆盖导入,全量导
     * @param request
     * @return
     */
    @RequestMapping(value = "overrideImport",method = {  RequestMethod.POST })
    @RequiresPermission(value = "zf103002_import")
    @ResponseBody
    public ResponseResult<ImportResultList> overrideImportData( HttpServletRequest request){
        ImportResultList importResultList = handleImport(request);
        if(importResultList.getResult()==ImportConstant.DATA_IMPORT_SUCCESS){
            BaseKeys bk = new BaseKeys();
            bk.setOperType(OperationType.ALL_IMPORT.getType());
            bk.setMessageType(ModelType.MODEL_USER_STATICIP.getMessageType());
            bk.setDataType(DataType.OTHER.getType());
            //获取导入原文件名
            String originalFileName = ExcelUtil.getFileName(request,"importFile");
            //保存导入文件到本地，返回保存的文件名(originalFileName+时间戳)
            String saveFileName =  commonService.saveFile(request, "importFile",originalFileName.substring(0,originalFileName.lastIndexOf(".")));
            bk.setMessage("filename="+originalFileName+",savefile="+saveFileName);
            return wrapReturnDate(importResultList, Arrays.asList(bk));
        }else {
            return wrapReturnDate(importResultList, new ArrayList<>());
        }

    }

    @RequestMapping("exportTemplate")
    @ResponseBody
    public ResponseResult exportTemplate(HttpServletRequest request, HttpServletResponse response){
        commonService.exportTemplete(request,response,"IpSegmentUserTemplete");
        BaseKeys bk = new BaseKeys();
        bk.setOperType(OperationType.EXPORT.getType());
        bk.setMessageType(ModelType.MODEL_USER_STATICIP.getMessageType());
        bk.setDataType(DataType.FILE.getType());
        bk.setFileName("IpSegmentUserTemplete");
        return wrapReturnDate(Arrays.asList(bk));
    }

    /**
     * import
     * @param request
     * @return
     */
    private ImportResultList handleImport(HttpServletRequest request){

        try {
            ImportResultList imp = staticIpService.handleImport(request);
            return imp;
        } catch (ImportException e) {
            logger.error("while import user ip error"+e);
            return e.getImportResultList();
        } catch (Exception ex){
            ex.printStackTrace();
            ImportResultList importResultList = new ImportResultList();
            importResultList.setResult(ImportConstant.DATA_IMPORT_FAIL);
            importResultList.setDescribtion("未知错误，请按照模板，刷新页面重试");
            return importResultList;
        }
    }

    @RequiresPermission("zf103002_redo")
    @RequestMapping(value = {"/resend"})
    @LogAnnotation(module = 103002,type = OperationConstants.OPERATION_RESEND)
    @ResponseBody
    public ResponseResult resendPolicy(@RequestParam(value = "messageNo") Long messageNo) throws Exception{
        ResponseResult responseResult = null;
        try {
            staticIpService.resendPolicy(0L,messageNo,true,"");
            responseResult = new ResponseResult();
        } catch (Exception e) {
            logger.error("",e);
        }
        try{
            String dataJson = "messageNo="+messageNo;
            ProxyUtil.changeVariable(UserStaticIpController.class,"resendPolicy",dataJson);
        } catch (Exception e){
            logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
        }

        responseResult.setResult(0);
        responseResult.setMessage("重发成功");
        return responseResult;
    }
}

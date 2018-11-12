package com.aotain.zongfen.controller.apppolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.aotain.zongfen.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.apppolicy.FlowManagerDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.apppolicy.flowmanager.FlowManagerService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @author czr
 */
@RequestMapping("flowmanager")
@Controller
public class FlowManagerController {

    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(FlowManagerController.class);

    @Autowired
    private FlowManagerService flowManagerService;

    @RequestMapping("index")
    public String index() {
        return "/apppolicy/flowmanager/index";
    }


    @RequiresPermission("zf102002_query")
    @RequestMapping("getList")
    @ResponseBody
    public PageResult<FlowManagerDTO> getList( HttpServletRequest request, FlowManagerDTO dto,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        try {
            PageHelper.startPage(page, pageSize);
            if (dto.getSearchEndTime()!=null){
                dto.setSearchEndTime(DateUtils.addDateOfMonth(dto.getSearchEndTime(),1));
            }
            List<FlowManagerDTO> lists = flowManagerService.getList(dto);
            PageInfo<FlowManagerDTO> pageInfo = new PageInfo<FlowManagerDTO>(lists);
            PageResult pageResult = new PageResult(pageInfo.getTotal(),lists);
            return pageResult;
        }catch (Exception e){
            logger.error("get show list error",e);
        }
        return new PageResult<>();

    }

    @RequiresPermission("zf102002_add")
    @RequestMapping("insert")
    @ResponseBody
    @LogAnnotation
    public ResponseResult insert( HttpServletRequest request, @RequestBody FlowManagerDTO dto) {
    	ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
    	try {
    		result =  flowManagerService.addDB(dto);
    		String dataJson = "appflowId="+JSONArray.toJSONString(result.getKeys().get(0).getId());
			ProxyUtil.changeVariable(this.getClass(),"insert",dataJson,dataJson, ModelType.MODEL_FLOW_MANAGER.getModel(), OperationType.CREATE);
        }catch (Exception e){
            logger.error("add general flow policy error",e);
            ResponseResult responseResult = new ResponseResult();
            responseResult.setResult(0);
            responseResult.setMessage("添加失败");
            return responseResult;
        }
    	result.setKeys(null);
    	return result;
    }

    @RequiresPermission("zf102002_modify")
    @RequestMapping("update")
    @ResponseBody
    @LogAnnotation
    public ResponseResult<BaseKeys> update( HttpServletRequest request,@RequestBody FlowManagerDTO dto) {
    	ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
    	try {
    		result =  flowManagerService.modifyDB(dto);
    		String dataJson = "appflowId="+JSONArray.toJSONString(result.getKeys().get(0).getId());
			ProxyUtil.changeVariable(this.getClass(),"update",dataJson,dataJson, ModelType.MODEL_FLOW_MANAGER.getModel(), OperationType.MODIFY);
        }catch (Exception e){
            logger.error("modify general flow policy error",e);
            ResponseResult responseResult = new ResponseResult();
            responseResult.setResult(0);
            responseResult.setMessage("更新失败");
            return responseResult;
        }
    	result.setKeys(null);
    	return result;
    }

    @RequiresPermission("zf102002_delete")
    @RequestMapping("delete")
    @ResponseBody
    @LogAnnotation
    public ResponseResult delete( HttpServletRequest request) {
        ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
        String[] ids = request.getParameterValues("flows[]");
        String dataJson = "appflowId="+JSONArray.toJSONString(ids);
        try {
            result =  flowManagerService.deleteMessage(ids);
        }catch (Exception e){
            logger.error("flow manager delete error ",e);
        }
		try {
			ProxyUtil.changeVariable(FlowManagerController.class,"delete",dataJson,dataJson, ModelType.MODEL_FLOW_MANAGER.getModel(), OperationType.DELETE);
		} catch (Exception e) {
			logger.error("flow manager LogAnnotation writer fail",e);
		}
        return result;
    }

    @RequiresPermission("zf102002_redo")
    @RequestMapping("resend")
    @ResponseBody
    @LogAnnotation
    public ResponseResult reSendPolicy( HttpServletRequest request, @RequestBody FlowManagerDTO flowManagerDTO) {
    	ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
    	try {
    		result = flowManagerService.reSendPolicy(flowManagerDTO);
            String dataJson = "messageNo="+JSONArray.toJSONString(flowManagerDTO.getMessageNo())+";bindMessageNo="+JSONArray.toJSONString(result.getBindMessageNo());
    		ProxyUtil.changeVariable(FlowManagerController.class,"reSendPolicy",dataJson,dataJson, ModelType.MODEL_FLOW_MANAGER.getModel(), OperationType.RESEND);
        }catch (Exception e){
            logger.error("resend policy fail",e);
            return new ResponseResult(0,"重发失败",null,null,null);
        }
    	result.setKeys(null);
    	return result;
    }
}

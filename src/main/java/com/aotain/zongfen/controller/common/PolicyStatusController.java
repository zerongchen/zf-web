package com.aotain.zongfen.controller.common;

import java.util.List;

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
import com.aotain.zongfen.dto.common.PolicyStatusDto;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.common.PolicyStatusService;
import com.aotain.zongfen.utils.PageResult;

@Controller
@RequestMapping("/policyStatus")  
public class PolicyStatusController {
	 /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(PolicyStatusController.class);
    
    @Autowired
    private PolicyStatusService policyStatus;
    
    /**
     * 
    * @Title: getPolicyDetail
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param messageNo
    * @param @param messageType
    * @param @param searchType 1：主策略查询  2：用户组绑定查询
    * @param @return
    * @return List<PolicyStatusDto>
    * @throws
     */
    @RequestMapping("/getDetail")
    @ResponseBody
    public PageResult<PolicyStatusDto> getPolicyDetail(@RequestBody PolicyStatusDto record) {
    	PageResult<PolicyStatusDto> result = new PageResult<PolicyStatusDto>();
    	try {
    		if(record.getMessageNo()!=null) {
    			result =  policyStatus.getPolicyDetail(record);
    		}
		} catch (Exception e) {
			logger.error("get policy detail fail ",e);
		}
    	return result;
    }
    
    @RequestMapping("/commonPolicyResend")
    @ResponseBody
    @LogAnnotation()
    public ResponseResult commonPolicyResend(@RequestParam("messageNos[]") List<Long> messageNos,@RequestParam("messageType")Integer messageType,@RequestParam("ips[]") List<String> ips,@RequestParam("model")Integer modelType){
    	ResponseResult result = new ResponseResult();
		String message = null;
		try {
			message = policyStatus.policyResend(messageNos,messageType,ips);
			if(message==null) {
				result.setResult(1);
			}else {
				result.setMessage(message);
				result.setResult(0);
			}
			if(result.getResult()==1) {
				String dataJson = "messageNo="+JSONArray.toJSONString(messageNos);
				if(modelType!=null) {
					ProxyUtil.changeVariable(this.getClass(),"commonPolicyResend",dataJson,dataJson, modelType,OperationType.RESEND);
				}else {
					ProxyUtil.changeVariable(this.getClass(),"commonPolicyResend",dataJson,dataJson, ModelType.getModelType(messageType),OperationType.RESEND);
				}
			}
		} catch (Exception e) {
			result.setMessage("重发失败");
			logger.error("webflowmanage delete fail ",e);
			result.setResult(0);
			return result;
		}
		return result;
    }
}

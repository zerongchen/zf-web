package com.aotain.zongfen.controller.apppolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.DdosExceptFlowStrategy;
import com.aotain.common.policyapi.model.msg.DdosFlowAppAttachNormal;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.apppolicy.DdosExceptFlowStrategyPo;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.service.apppolicy.ddosflowmanage.DdosFlowManageService;
import com.aotain.zongfen.service.common.PolicyServiceImpl;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


/**
 * 
 * ClassName: ddos流量管理
 * date: 2018年4月4日 上午10:26:56
 * 
 * @author chenym
 * @version  
 * @since JDK 1.8
 */
@RequestMapping("/apppolicy/ddosflow")
@Controller
public class DdosFlowManageController{

	private static Logger LOG = LoggerFactory.getLogger(DdosFlowManageController.class);

	@Autowired
	private DdosFlowManageService ddosFlowManageService;

	@Autowired
	private PolicyServiceImpl policyService;

	/**
	 * 
	* @Title: getIndex
	* @Description: 首页跳转
	* @param 
	* @return String
	* @throws
	 */
	@RequestMapping("index")
	public String getIndex() {
		return "/apppolicy/ddosflowmanage/index";
	}

	@RequiresPermission("zf102009_query")
	@RequestMapping("list")
	@ResponseBody
	public PageResult<DdosExceptFlowStrategyPo> listData(DdosExceptFlowStrategyPo dto,
														  @RequestParam(required = false, defaultValue = "1") Integer page,
														  @RequestParam(required = false, defaultValue = "10") Integer pageSize){
		try {
			PageHelper.startPage(page, pageSize);
			List<DdosExceptFlowStrategyPo> lists =  ddosFlowManageService.listData(dto);
			PageInfo<DdosExceptFlowStrategyPo> pageInfo = new PageInfo<DdosExceptFlowStrategyPo>(lists);
			PageResult pageResult = new PageResult(pageInfo.getTotal(),lists);
			return pageResult;
		} catch (Exception e) {
			LOG.error("error ",e);
			return new PageResult<DdosExceptFlowStrategyPo>();
		}
	}

	@RequiresPermission("zf102009_add")
    @RequestMapping(value = {"/add"},method= {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	@LogAnnotation(module = 102009,type = OperationConstants.OPERATION_SAVE)
	public ResponseResult addData(DdosExceptFlowStrategyPo po) throws Exception{
		ResponseResult responseResult = new ResponseResult();
    	try {

			String createOper = SpringUtil.getSysUserName();
			Policy policy = new Policy();
			policy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_DDOS_EXCEPT_FLOW);
			policy.setMessageName(po.getMessageName());
			if (policyService.isSamePolicyNameByType(policy)){
				responseResult.setResult(0);
				responseResult.setMessage("添加失败");
            }
            List<DdosFlowAppAttachNormal> list = new ArrayList();
			for(int u=0;u<po.getAppAttackType().split(",").length;u++){
				DdosFlowAppAttachNormal model = new DdosFlowAppAttachNormal();
				model.setAppAttackType(Integer.valueOf(po.getAppAttackType().split(",")[u]));
				model.setAttackThreshold(Integer.valueOf(po.getAttackThreshold().split(",")[u]));
				model.setAttackControl(Integer.valueOf(po.getAttackControl().split(",")[u]));
				list.add(model);
			}
			DdosExceptFlowStrategy ddosExceptFlowStrategy = new DdosExceptFlowStrategy();
			ddosExceptFlowStrategy.setMessageName(po.getMessageName());
			ddosExceptFlowStrategy.setAppAttachNormal(list);
			ddosExceptFlowStrategy.setUserType(po.getUserType());
			if(!StringUtils.isEmpty(po.getUserName())){
				ddosExceptFlowStrategy.setUserName(Arrays.asList(po.getUserName().split(",")));
			}
			if(!StringUtils.isEmpty(po.getUserGroupId())){
				ddosExceptFlowStrategy.setUserGroupId(Arrays.asList(po.getUserGroupId().split(",")));
			}
			ddosExceptFlowStrategy.setCreateOper(createOper);
			ddosExceptFlowStrategy.setModifyOper(createOper);
			ddosExceptFlowStrategy.setCreateTime(new Date());
			ddosExceptFlowStrategy.setModifyTime(new Date());
			long bindMessageNo = ddosFlowManageService.addddosFlowPolicyAndUserBindPolicy(ddosExceptFlowStrategy);
			String dataJson = "messageNo="+bindMessageNo;
			ProxyUtil.changeVariable(DdosFlowManageController.class,"addData",dataJson);
    	} catch (Exception e) {
			LOG.error("controller ddosflow add error ",e);
			responseResult.setResult(1);
			responseResult.setMessage("添加失败");
		}
    	responseResult.setKeys(null);
		responseResult.setResult(0);
		responseResult.setMessage("添加成功");
		return responseResult;
	}

	@RequiresPermission("zf102009_modify")
	@RequestMapping(value = {"/update"},method= {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	@LogAnnotation(module = 102009,type = OperationConstants.OPERATION_UPDATE)
	public ResponseResult modifyData(DdosExceptFlowStrategyPo po) throws Exception{
		String createOper = SpringUtil.getSysUserName();
		ResponseResult responseResult = new ResponseResult();
		try {
			List<DdosFlowAppAttachNormal> list = new ArrayList();
			for(int u=0;u<po.getAppAttackType().split(",").length;u++){
				DdosFlowAppAttachNormal model = new DdosFlowAppAttachNormal();
				model.setAppAttackType(Integer.valueOf(po.getAppAttackType().split(",")[u]));
				model.setAttackThreshold(Integer.valueOf(po.getAttackThreshold().split(",")[u]));
				model.setAttackControl(Integer.valueOf(po.getAttackControl().split(",")[u]));
				list.add(model);
			}
			DdosExceptFlowStrategy ddosExceptFlowStrategy = new DdosExceptFlowStrategy();
			ddosExceptFlowStrategy.setMessageNo(po.getMessageNo());
			ddosExceptFlowStrategy.setMessageName(po.getMessageName());
			ddosExceptFlowStrategy.setAppAttachNormal(list);
			ddosExceptFlowStrategy.setUserType(po.getUserType());
			if(!StringUtils.isEmpty(po.getUserName())){
				ddosExceptFlowStrategy.setUserName(Arrays.asList(po.getUserName().split(",")));
			}
			if(!StringUtils.isEmpty(po.getUserGroupId())){
				ddosExceptFlowStrategy.setUserGroupId(Arrays.asList(po.getUserGroupId().split(",")));
			}
			ddosExceptFlowStrategy.setCreateOper(createOper);
			ddosExceptFlowStrategy.setModifyOper(createOper);
			ddosExceptFlowStrategy.setCreateTime(new Date());
			ddosExceptFlowStrategy.setModifyTime(new Date());

			ddosFlowManageService.modifyDdosFlowManageAndUserBindPolicy(ddosExceptFlowStrategy);
			String dataJson = "messageNo="+ddosExceptFlowStrategy.getMessageNo();
			ProxyUtil.changeVariable(DdosFlowManageController.class,"modifyData",dataJson);
		} catch (Exception e) {
			LOG.error("ddosflow update error,",e);
			responseResult.setResult(1);
			responseResult.setMessage("修改失败");
		}
		responseResult.setResult(0);
		responseResult.setMessage("修改成功");
		return responseResult;
	}

	@RequiresPermission("zf102009_delete")
	@RequestMapping(value = {"/delete"},method= {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	@LogAnnotation(module = 102009,type = OperationConstants.OPERATION_DELETE)
	public ResponseResult deleteData(@RequestParam(value = "messageNos[]") Long[] messageNos) throws Exception{
		String createOper = SpringUtil.getSysUserName();
		List<DdosExceptFlowStrategy> ddosFlowStrategies = new ArrayList<>();
		ResponseResult responseResult = new ResponseResult();
		for(int i=0;i<messageNos.length;i++){
			DdosExceptFlowStrategy ddosFlowStrategy = new DdosExceptFlowStrategy();
			ddosFlowStrategy.setMessageNo(messageNos[i]);
			DdosExceptFlowStrategy result = ddosFlowManageService.selectByPrimaryKey(ddosFlowStrategy);
			result.setModifyOper(createOper);
			result.setModifyTime(new Date());
			ddosFlowStrategies.add(result);
		}
		
		ddosFlowManageService.deletePolicys(ddosFlowStrategies);
		String dataJson = "messageNo="+JSONArray.toJSONString(messageNos);
		ProxyUtil.changeVariable(DdosFlowManageController.class,"deleteData",dataJson);
		responseResult.setResult(0);
		responseResult.setMessage("删除成功");
		return responseResult;
	}

	@RequiresPermission("zf102009_redo")
	@RequestMapping(value = {"/resend"})
	@ResponseBody
	@LogAnnotation(module = 102009,type = OperationConstants.OPERATION_RESEND)
	public ResponseResult resendPolicy(@RequestParam(value = "messageNo") Long messageNo,
									   @RequestParam(value = "ips[]",required=false) List<String> ips) throws Exception{
		ResponseResult responseResult = null;
		try {
			if (ips==null||ips.size()==0){
                ips = new ArrayList<>();
            }
			ddosFlowManageService.resendPolicy(0L,messageNo,true,ips);
			responseResult = new ResponseResult();
		} catch (Exception e) {
			LOG.error("",e);
		}
		String dataJson = "messageNo="+messageNo;
		ProxyUtil.changeVariable(DdosFlowManageController.class,"resendPolicy",dataJson);
		responseResult.setResult(0);
		responseResult.setMessage("重发成功");
		return responseResult;
	}

	@RequestMapping(value = {"/resendSpecificDpi"})
	@ResponseBody
	public ResponseResult resendSpecificDpi(@RequestParam(value = "messageNo") Long messageNo,
									   @RequestParam(value = "ips[]") List<String> ips) throws Exception{
		ResponseResult responseResult = new ResponseResult();
    	if (ips==null||ips.size()==0){
			ips = new ArrayList<>();
		}
		ddosFlowManageService.resendPolicy(0L,messageNo,false,ips);
		responseResult.setResult(0);
		responseResult.setMessage("重发成功");
		return responseResult;
	}

}

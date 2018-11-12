package com.aotain.zongfen.controller.apppolicy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.WebPushStrategy;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.dto.apppolicy.WebPushStrategyVo;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.service.apppolicy.webpushmanage.WebPushManageService;
import com.aotain.zongfen.service.common.PolicyServiceImpl;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 
 * ClassName: web消息推送
 * Description:
 * date: 2018年4月10日 上午10:36:08
 * 
 * @author cym
 * @version  
 * @since JDK 1.8
 */
@RequestMapping("/apppolicy/webpush")
@Controller
public class WebPushManageController extends BaseController {

	private static Logger LOG = LoggerFactory.getLogger(WebPushManageController.class);
	@Autowired
	private WebPushManageService webPushManageService;

	@Autowired
	private PolicyServiceImpl policyService;
	/**
	 * 
	* @Title: getIndex
	* @Description: 页面跳转
	* @param @return
	* @return String
	* @throws
	 */
	@RequestMapping("index")
	public String getIndex() {
		return "/apppolicy/webpushmanage/index";
	}


	@RequiresPermission("zf102008_query")
	@RequestMapping(value = {"/list"})
	@ResponseBody
	public PageResult<WebPushStrategyVo> listData(WebPushStrategyVo vo,
																   @RequestParam(required = false, defaultValue = "1") Integer page,
																   @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws Exception{


		PageHelper.startPage(page, pageSize);
		List<WebPushStrategyVo> lists =  webPushManageService.listData(vo);
		PageInfo<WebPushStrategyVo> pageInfo = new PageInfo<WebPushStrategyVo>(lists);
		PageResult pageResult = new PageResult(pageInfo.getTotal(),lists);
		return pageResult;
	}

	@RequiresPermission("zf102008_add")
	@RequestMapping(value={"/add"},method= {RequestMethod.POST})
	@ResponseBody
	@LogAnnotation(module = 102008,type = OperationConstants.OPERATION_SAVE)
	public ResponseResult addData(WebPushStrategy webPushStrategy) throws Exception{
		String createOper = SpringUtil.getSysUserName();
		if(webPushStrategy.getAdvWhiteHostListId()==null){
			webPushStrategy.setAdvWhiteHostListId(0);
		}
		try {
			webPushStrategy.setCreateOper(createOper);
			webPushStrategy.setModifyOper(createOper);
			webPushStrategy.setCreateTime(new Date());
			webPushStrategy.setModifyTime(new Date());

			Policy policy = new Policy();
			policy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_WEB_PUSH_POLICY);
			policy.setMessageName(webPushStrategy.getMessageName());
			if (policyService.isSamePolicyNameByType(policy)){
                return wrapReturnDate(1);
            }
			webPushManageService.addWebPushPolicyAndUserBindPolicy(webPushStrategy);
			String dataJson = "messageNo="+webPushStrategy.getMessageNo();
            ProxyUtil.changeVariable(WebPushManageController.class,"addData",dataJson);
		} catch (Exception e) {
			LOG.error(" add webpush error,",e);
			return wrapReturnDate("新增策略失败",1);
		}
		return wrapReturnDate("新增策略成功",0);
	}

	@RequiresPermission("zf102008_delete")
	@RequestMapping(value = {"/delete"})
	@ResponseBody
	@LogAnnotation(module = 102008,type = OperationConstants.OPERATION_DELETE)
	public ResponseResult deleteData(@RequestParam(value = "messageNos[]") Long[] messageNos) throws Exception{
		String createOper = SpringUtil.getSysUserName();
		List<WebPushStrategy> webPushStrategys = new ArrayList<>();

		for(int i=0;i<messageNos.length;i++){


			WebPushStrategyVo vo = new WebPushStrategyVo();
			vo.setMessageNo(messageNos[i]);
			vo = webPushManageService.selectByPrimaryKey(vo);

			WebPushStrategy webPushStrategy = getWebPushStrategy(createOper, vo);

			webPushStrategys.add(webPushStrategy);
		}
		int count = webPushManageService.isExistBind(messageNos);
		if(count>0) {
			ResponseResult responseResult  = new ResponseResult();
	        responseResult.setMessage("存在关联的Web信息结果上报策略！");
	        responseResult.setResult(1);
	        return responseResult;
		}
		webPushManageService.deletePolicys(webPushStrategys);
		String dataJson = "messageNo="+JSONArray.toJSONString(messageNos);
        ProxyUtil.changeVariable(WebPushManageController.class,"deleteData",dataJson);
		return wrapReturnDate();
	}


	@RequiresPermission("zf102008_modify")
	@RequestMapping(value = {"/update"})
	@ResponseBody
	@LogAnnotation(module = 102008,type = OperationConstants.OPERATION_UPDATE)
	public ResponseResult modifyData(WebPushStrategy webPushStrategy) throws Exception{
		String createOper = SpringUtil.getSysUserName();
		try {
			webPushStrategy.setModifyOper(createOper);
			webPushStrategy.setModifyTime(new Date());

			webPushManageService.modifyWebPushManageAndUserBindPolicy(webPushStrategy);
			String dataJson = "messageNo="+webPushStrategy.getMessageNo();
            ProxyUtil.changeVariable(WebPushManageController.class,"modifyData",dataJson);
		} catch (Exception e) {
			return wrapReturnDate("修改策略失败",1);
		}
		return wrapReturnDate("修改策略成功",0);
	}

	@RequiresPermission("zf102008_redo")
	@RequestMapping(value = {"/resend"})
	@ResponseBody
	@LogAnnotation(module = 102008,type = OperationConstants.OPERATION_RESEND)
	public ResponseResult resendPolicy(@RequestParam(value = "messageNo") Long messageNo,
									   @RequestParam(value = "ips[]",required=false) List<String> ips) throws Exception{
		if (ips==null||ips.size()==0){
			ips = new ArrayList<>();
		}
		webPushManageService.resendPolicy(0L,messageNo,true,ips);
		String dataJson = "messageNo="+messageNo;
        ProxyUtil.changeVariable(WebPushManageController.class,"resendPolicy",dataJson);
		return wrapReturnDate();
	}



	private WebPushStrategy getWebPushStrategy(String createOper, WebPushStrategyVo vo) {

		try {
			List<Integer> hostList = new ArrayList<>();
			List<Integer> kwList = new ArrayList<>();
			String hostStr = vo.getTriggerHostListId();
			String kwStr = vo.getTriggerKwListId();
			if(StringUtils.isNotBlank(hostStr)){
                String[] s =hostStr.split(",");
                if(s.length>0){
                    for(int h=0;h<s.length;h++){
                        hostList.add(Integer.valueOf(s[h]));
                    }
                }
            }
			if(StringUtils.isNotBlank(kwStr)){
                String[] s =kwStr.split(",");
                if(s.length>0){
                    for(int h=0;h<s.length;h++){
                        kwList.add(Integer.valueOf(s[h]));
                    }
                }
            }
			WebPushStrategy webPushStrategy = new WebPushStrategy();

			webPushStrategy.setAdvId(vo.getAdvId());
			webPushStrategy.setAdvType(vo.getAdvType());
			webPushStrategy.setAdvWhiteHostListId(vo.getAdvWhiteHostListId());
			webPushStrategy.setTriggerHostListId(hostList);
			webPushStrategy.setTriggerKwListId(kwList);
			webPushStrategy.setAdvFramDPIrl(vo.getAdvFramDPIrl());
			webPushStrategy.setAdvToken(vo.getAdvToken());
			webPushStrategy.setAdvDelay(vo.getAdvDelay());
			webPushStrategy.setMessageNo(vo.getMessageNo());
			webPushStrategy.setMessageName(vo.getMessageName());
			webPushStrategy.setModifyOper(createOper);
			webPushStrategy.setModifyTime(new Date());
			return webPushStrategy;
		} catch (NumberFormatException e) {
			LOG.error("webpush delete stategy to vo error,",e);
		}
		return null;
	}








	/**
	 * ================================================================================================
	 */

	/**
	 *
	 * @Title: getWebPushData
	 * @Description: 首页数据加载
	 * @param @param pageSize
	 * @param @param pageIndex
	 * @param @param policyName
	 * @param @param status
	 * @param @return
	 * @return PageResult<WebPushDTO>
	 * @throws
	 *//*
	@RequestMapping("getWebPushData")
	@ResponseBody
	public PageResult<WebPushDTO> getWebPushData(@RequestParam(required = true, name = "pageSize") Integer pageSize,
												 @RequestParam(required = true, name = "pageIndex") Integer pageIndex,
												 @RequestParam(required = true, name = "policyName") String policyName,
												 @RequestParam(required = true, name = "status") Integer status){
		return webPushManageService.getWebPushData(pageSize, pageIndex, policyName, status);

	}


	//保存
	@RequestMapping("webpushsave")
	@ResponseBody
	public ResponseResult webflowSave(@RequestParam("webPushId") Integer webPushId, @RequestParam("policyName")String  policyName,
									  @RequestParam("advType") Short advType,@RequestParam("advToken")Long advToken,
									  @RequestParam("start") String start,@RequestParam("end") String end,
									  @RequestParam("common")String common,@RequestParam("advDelay")Long advDelay,
									  @RequestParam("userGroup")Long userGroup, HttpServletRequest request,
									  @RequestParam("bindUser")Integer bindUser,@RequestParam("bindmessageNo")Long bindmessageNo,
									  @RequestParam("messageNo")Long messageNo,@RequestParam("advUrl")String advUrl,
									  @RequestParam("idList")Long idList,@RequestParam("idType")Integer idType) {
		ResponseResult result = new ResponseResult();
		String message = null;
		try {
			message = webPushManageService.webPushSave(webPushId, policyName, advType, idList, start, end, common,userGroup,bindUser,bindmessageNo,messageNo,advUrl,advToken,advDelay,idType);
		} catch (Exception e) {
			result.setMessage("保存失败！");
			result.setResult(0);
			return result;
		}
		if(message==null) {
			result.setResult(1);
		}else {
			result.setMessage(message);
			result.setResult(0);
		}
		return result;
		
	}
	
	*//**
	 * 
	* @Title: getEditData
	* @Description: 获取详细信息
	* @param @param messageNo
	* @param @return
	* @return Map<String,Object>
	* @throws
	 *//*
	@RequestMapping("getEditData")
	@ResponseBody
	public Map<String,Object> getEditData(@RequestParam("messageNo")Long messageNo){
		return webPushManageService.getEditData(messageNo);
	}
	
	*//**
	 * 
	* @Title: deleteWebPush
	* @Description: 删除
	* @param @param policys
	* @param @return
	* @return ResponseResult
	* @throws
	 *//*
	@RequestMapping("deleteWebPush")
	@ResponseBody
	public ResponseResult deleteWebPush(@RequestParam("policyId[]")String[] policys) {
		ResponseResult result = new ResponseResult();
		String message = null;
		try {
			message = webPushManageService.deleteWebPush(policys);
		} catch (Exception e) {
			result.setMessage("保存失败！");
			result.setResult(0);
			return result;
		}
		if(message==null) {
			result.setResult(1);
		}else {
			result.setMessage(message);
			result.setResult(0);
		}
		return result;
	}
	
	*//**
	 * 
	* @Title: getAdvList
	* @Description:查询相应的触发条件
	* @param @param advType
	* @param @return
	* @return List<Trigger>
	* @throws
	 *//*
	@RequestMapping("/getAdvList")
	@ResponseBody
	public List<Trigger> getAdvList(@RequestParam("advType")Integer advType) {
		return webPushManageService.getAdvList(advType);
	}*/
}

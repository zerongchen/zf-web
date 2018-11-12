package com.aotain.zongfen.controller.apppolicy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.policyapi.model.CommonCookie;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.apppolicy.CommonSearch;
import com.aotain.zongfen.model.apppolicy.CommonThreshold;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.apppolicy.paramset.ParamSetService;
import com.aotain.zongfen.service.apppolicy.webflowmanage.WebFlowManageServiceImpl;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.utils.PageResult;

/**
 * 
 * ClassName: ParamSetController
 * Description: 通用参数设置策略
 * date: 2018年4月13日 上午11:00:14
 * 
 * @author tanzj
 * @version  
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/param")
public class ParamSetController {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WebFlowManageServiceImpl.class);
	
	@Autowired
	private ParamSetService paramSetService;
	
	@Autowired
	private CommonService commonService;
	
	@RequestMapping("/index")
	public String getIndex() {
		return "/apppolicy/paramSetting/index";
	}
	
	@RequiresPermission("zf102001_modify")
	@RequestMapping(value="/saveAndSync")
	@ResponseBody
	@LogAnnotation()
	public ResponseResult<BaseKeys> saveAndSync(@RequestParam("webHitThreshold")Integer webHitThreshold,
								@RequestParam("kwThreholds")Integer kwThreholds,
								@RequestParam("commonId")Integer commonId) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = paramSetService.saveAndSync(webHitThreshold, kwThreholds, commonId);
			if(result.getKeys()!=null && result.getKeys().size()>0) {
				String dataJson = "messageNo="+result.getKeys().get(0).getMessageNo();
				ProxyUtil.changeVariable(this.getClass(),"saveAndSync",dataJson,dataJson, ModelType.MODEL_PARAM_SET.getModel(), result.getKeys().get(0).getOperType()==OperationType.CREATE.getType()?OperationType.CREATE:OperationType.MODIFY);
			}
		} catch (Exception e) {
			logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
			result.setMessage("保存失败，请刷新页面！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102001_query")
	@RequestMapping(value="/getThreshold")
	@ResponseBody
	public CommonThreshold getThreshold() {
		return paramSetService.getThreshold();
	}
	
	@RequiresPermission("zf102001_add")
	@RequestMapping(value="/searchSave")
	@ResponseBody
	@LogAnnotation()
	public ResponseResult<BaseKeys> searchSave(HttpServletRequest request,@RequestParam("seId")Long seId) {
		String[] searchEngine = request.getParameterValues("searchEngine[]");
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = paramSetService.searchSave(searchEngine,seId);
			if(result.getKeys()!=null && result.getKeys().size()>0) {
				List<Long> idsList = new ArrayList<Long>();
				for(BaseKeys temp : result.getKeys()) {
					idsList.add(temp.getId());
				}
				String dataJson = "seId="+JSONArray.toJSONString(idsList);
				ProxyUtil.changeVariable(this.getClass(),"searchSave",dataJson,dataJson, ModelType.MODEL_PARAM_SET.getModel(), OperationType.CREATE);
			}
		} catch (Exception e) {
			result.setMessage("保存失败，请刷新页面！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102001_modify")
	@RequestMapping(value="/searchupdate")
	@ResponseBody
	@LogAnnotation()
	public ResponseResult<BaseKeys> searchUpdate(HttpServletRequest request,@RequestParam("seId")Long seId) {
		String[] searchEngine = request.getParameterValues("searchEngine[]");
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = paramSetService.searchSave(searchEngine,seId);
			if(result.getKeys()!=null && result.getKeys().size()>0) {
				List<Long> idsList = new ArrayList<Long>();
				for(BaseKeys temp : result.getKeys()) {
					idsList.add(temp.getId());
				}
				String dataJson = "seId="+JSONArray.toJSONString(idsList);
				ProxyUtil.changeVariable(this.getClass(),"searchUpdate",dataJson,dataJson, ModelType.MODEL_PARAM_SET.getModel(), OperationType.MODIFY);
			}
		} catch (Exception e) {
			result.setMessage("保存失败，请刷新页面！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102001_query")
	@RequestMapping(value="/getSearchData")
	@ResponseBody
	public List<CommonSearch> getAllSearch() {
		return paramSetService.getAllSearch();
	}
	
	@RequiresPermission("zf102001_delete")
	@RequestMapping(value="/deleteSearch")
	@ResponseBody
	@LogAnnotation()
	public ResponseResult<BaseKeys> deleteSearch(HttpServletRequest request) {
		String[] ids = request.getParameterValues("searchId[]");
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = paramSetService.deleteSearch(ids);
			List<Integer> idList = new ArrayList<Integer>();
			for(String id:ids) {
				idList.add(Integer.valueOf(id));
			}
			String dataJson = "seId="+ JSONArray.toJSONString(idList);
			ProxyUtil.changeVariable(this.getClass(),"deleteSearch",dataJson,dataJson, ModelType.MODEL_PARAM_SET.getModel(), OperationType.DELETE);
		} catch (Exception e) {
			result.setMessage("删除失败，请刷新页面！");
			result.setResult(0);
			return result;
		}
		return result;
	}
	
	@RequiresPermission("zf102001_add")
	@RequestMapping(value="/cookieSave")
	@ResponseBody
	@LogAnnotation()
	public ResponseResult<BaseKeys> cookieSave(HttpServletRequest request,
										@RequestParam("cookieHostName")String cookieHostName,
										@RequestParam("cookieKeyValue")String cookieKeyValue,
										@RequestParam("cookieId")Long cookieId,
										@RequestParam("existFile")Integer existFile) {
		CommonCookie cookies = new CommonCookie ();
		cookies.setCookieHostName(cookieHostName);
		cookies.setCookieKeyValue(cookieKeyValue);
		cookies.setCookieId(cookieId);
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = paramSetService.cookieSave(request,cookies,existFile);
			if(result.getKeys()!=null && result.getKeys().size()>0) {
				List<Long> idsList = new ArrayList<Long>();
				for(BaseKeys temp : result.getKeys()) {
					idsList.add(temp.getId());
				}
				String dataJson = "cookieId="+JSONArray.toJSONString(idsList);
				ProxyUtil.changeVariable(this.getClass(),"cookieSave",dataJson,dataJson, ModelType.MODEL_PARAM_SET.getModel(), OperationType.CREATE);
			}
		} catch (Exception e) {
			result.setMessage("保存失败，请刷新页面！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102001_modify")
	@RequestMapping(value="/cookieupdate")
	@ResponseBody
	@LogAnnotation()
	public ResponseResult<BaseKeys> cookieUpdate(HttpServletRequest request,
										@RequestParam("cookieHostName")String cookieHostName,
										@RequestParam("cookieKeyValue")String cookieKeyValue,
										@RequestParam("cookieId")Long cookieId,
										@RequestParam("existFile")Integer existFile) {
		CommonCookie cookies = new CommonCookie ();
		cookies.setCookieHostName(cookieHostName);
		cookies.setCookieKeyValue(cookieKeyValue);
		cookies.setCookieId(cookieId);
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = paramSetService.cookieSave(request,cookies,existFile);
			if(result.getKeys()!=null && result.getKeys().size()>0) {
				List<Long> idsList = new ArrayList<Long>();
				for(BaseKeys temp : result.getKeys()) {
					idsList.add(temp.getId());
				}
				String dataJson = "cookieId="+JSONArray.toJSONString(idsList);
				ProxyUtil.changeVariable(this.getClass(),"cookieUpdate",dataJson,dataJson, ModelType.MODEL_PARAM_SET.getModel(), OperationType.MODIFY);
			}
		} catch (Exception e) {
			result.setMessage("保存失败，请刷新页面！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102001_delete")
	@RequestMapping(value="/deleteCookie")
	@ResponseBody
	@LogAnnotation()
	public ResponseResult<BaseKeys> deleteCookie(HttpServletRequest request) {
		String[] ids = request.getParameterValues("cookieId[]");
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = paramSetService.deleteCookie(ids);
			List<Integer> idList = new ArrayList<Integer>();
			for(String id:ids) {
				idList.add(Integer.valueOf(id));
			}
			String dataJson = "cookieId="+JSONArray.toJSONString(idList);
			ProxyUtil.changeVariable(this.getClass(),"deleteCookie",dataJson,dataJson, ModelType.MODEL_PARAM_SET.getModel(), OperationType.DELETE);
		} catch (Exception e) {
			result.setMessage("保存失败，请刷新页面！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102001_query")
	@RequestMapping("getCookieData")
    @ResponseBody
    public PageResult<CommonCookie> getCookieData(@RequestParam(required = true, name = "pageSize") Integer pageSize,
											@RequestParam(required = true, name = "pageIndex") Integer pageIndex){
        return paramSetService.getCookieData(pageSize,pageIndex);
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
    @LogAnnotation
    public void uploadTemplate(HttpServletRequest request,HttpServletResponse response) {
    	commonService.exportTemplete(request, response, "CookieTemplete");
    	try{
            String dataJson = "fileName=CookieTemplete.xlsx";
            ProxyUtil.changeVariable(this.getClass(),"uploadTemplate",dataJson,dataJson, ModelType.MODEL_PARAM_SET.getModel(), OperationType.DOWNLOAD);
        } catch (Exception e){
            logger.error("uploadTemplate fail",e);
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
    	return paramSetService.getAlarmType();
    }
    
    @RequiresPermission("zf102001_send")
    @RequestMapping(value="sendPolicy")
    @ResponseBody
    public ResponseResult<BaseKeys> sendPolicy() {
    	ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = paramSetService.sendPolicy();
		} catch (Exception e) {
			result.setMessage("保存失败！");
			result.setResult(0);
			return result;
		}
		return result;
    }
    
    @RequiresPermission("zf102001_query")
    @RequestMapping(value="getPolicyDetail")
    @ResponseBody
    public List<CommonThreshold> getPolicyDetail() {
    	return paramSetService.getPolicyDetail();
    }
    
    @RequiresPermission("zf102001_redo")
	@RequestMapping("policyResend")
	@ResponseBody
	public ResponseResult<BaseKeys> policyResend(@RequestParam("messageNo") long messageNo) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = paramSetService.policyResend(messageNo);
		} catch (Exception e) {
			result.setMessage("重发失败");
			logger.error("commonParameter resend fail :",e);
			result.setResult(0);
			return result;
		}
		return result;
	}
    
}

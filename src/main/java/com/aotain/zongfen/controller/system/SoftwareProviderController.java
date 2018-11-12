package com.aotain.zongfen.controller.system;

import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.system.SoftwareManageService;
import com.aotain.zongfen.utils.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/system/softwareprovidermanage")
public class SoftwareProviderController {
	
	private static Logger logger = LoggerFactory.getLogger(SoftwareProviderController.class);

	@Autowired
	private SoftwareManageService softwareManageService;
	
	@RequestMapping("index")
	public String getIndex() {
		return "/system/softwareprovidermanage/index";
	}

	@RequiresPermission(value = "zf501004_query")
	@RequestMapping(value = {"/getSoftwareProviderManageData"})
	@ResponseBody
	public PageResult<Map<String,String>> getInitTable(@RequestParam(required = true, name = "pageSize") Integer pageSize,
																		@RequestParam(required = true, name = "pageIndex") Integer pageIndex,
																		HttpServletRequest request){
		try {
			Map<String,String> params = new HashMap<>();
			String providerName = request.getParameter("providerName");
			String providerShort = request.getParameter("providerShort");
			params.put("providerName",providerName);
			params.put("providerShort",providerShort);
			PageResult<Map<String,String>> pageResult = softwareManageService.getInitTable(pageSize,pageIndex,params);
			return pageResult;
		} catch (Exception e) {
			logger.error("get softwareManage Data fail ",e);
		}
		return null;
	}

	@RequiresPermission("zf501004_add")
	@RequestMapping("/addProvider")
	@ResponseBody
	@LogAnnotation(module = 501004,type = OperationConstants.OPERATION_SAVE)
	public ResponseResult<BaseKeys> addProvider(HttpServletRequest request) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {

			String providerName = request.getParameter("providerName");
			String providerShort = request.getParameter("providerShort");

			List<String> providerNameList = null;
			List<String> providerShortList = null;
			if(!StringUtils.isEmpty(providerName)&&!StringUtils.isEmpty(providerShort)){
				providerNameList = Arrays.asList(providerName.split(","));
				providerShortList = Arrays.asList(providerShort.split(","));
			}
			List<Map<String,String>> pMapList = new ArrayList<>();
			for(int i=0;i<providerNameList.size();i++){
				Map<String,String> pMap = new HashMap<>();
				pMap.put("providerName",providerNameList.get(i));
				pMap.put("providerShort",providerShortList.get(i));
				pMapList.add(pMap);
			}
			int insertCount = softwareManageService.addProvider(pMapList);

			String dataJson = "providerShort="+providerShortList;
			ProxyUtil.changeVariable(SoftwareProviderController.class,"addProvider",dataJson);
		} catch (Exception e) {
			logger.error("AreaManage save fail ",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf501004_modify")
	@RequestMapping("updateProvider")
	@ResponseBody
	@LogAnnotation(module = 501004,type = OperationConstants.OPERATION_UPDATE)
	public ResponseResult<BaseKeys> updateProvider(HttpServletRequest request) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			String providerName = request.getParameter("providerName");
			String providerShort = request.getParameter("providerShort");
			Map<String,String> pMap = new HashMap<>();
			pMap.put("providerName",providerName);
			pMap.put("providerShort",providerShort);
			softwareManageService.updateProvider(pMap);

			/**
			 * t添加操作日志
			 */
			String dataJson = "providerShort="+providerShort;
			ProxyUtil.changeVariable(SoftwareProviderController.class,"updateProvider",dataJson);
		} catch (Exception e) {
			logger.error("updateProvider update fail ",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		return result;
	}

	@RequiresPermission("zf501004_delete")
	@RequestMapping("delete")
	@ResponseBody
	@LogAnnotation(module = 501004,type = OperationConstants.OPERATION_DELETE)
	public ResponseResult<BaseKeys> deleteProvider(HttpServletRequest request,@RequestParam(value = "providerShort[]") String[] providerShort) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			List<String> providerShortList = null;
			if(!StringUtils.isEmpty(providerShort)){
				providerShortList = Arrays.asList(providerShort);
			}
			for(String m:providerShortList){
				softwareManageService.deleteProvider(m);
			}
			/**
			 * t添加操作日志
			 */
			String dataJson = "providerShort="+Arrays.asList(providerShort);
			ProxyUtil.changeVariable(SoftwareProviderController.class,"deleteProvider",dataJson);
		} catch (Exception e) {
			logger.error("delete fail ",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		return result;
	}


	@RequestMapping(value = {"/getSoftwareProvider"})
	@ResponseBody
	public PageResult<Map<String,List<String>>> getSoftwareProvider(HttpServletRequest request){
		try {
			PageResult<Map<String,List<String>>> pageResult = softwareManageService.getSoftwareProvider();
			return pageResult;
		} catch (Exception e) {
			logger.error("get getSoftwareProvider fail ",e);
		}
		return null;
	}

}

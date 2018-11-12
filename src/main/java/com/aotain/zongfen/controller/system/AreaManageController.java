package com.aotain.zongfen.controller.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ChinaArea;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.system.AreaManageService;
import com.aotain.zongfen.utils.PageResult;

@Controller
@RequestMapping("system/area")
public class AreaManageController {
	
	private static Logger logger = LoggerFactory.getLogger(AreaManageController.class);

	@Autowired
	private AreaManageService areaManageService;
	
	@RequestMapping("index")
	public String getIndex() {
		return "/system/areamanage/index";
	}
	
	@RequiresPermission("zf501003_query")
	@RequestMapping("getAreaManageData")
	@ResponseBody
	public PageResult<ChinaArea> getAreaManageData(@RequestParam(required = true, name = "pageSize") Integer pageSize,
												 @RequestParam(required = true, name = "pageIndex") Integer pageIndex,
												 @RequestParam("searchName") String searchName){
		try {
			return areaManageService.getAreaManageData(pageSize, pageIndex, searchName);
		} catch (Exception e) {
			logger.error("get AreaManage Data fail ",e);
		}
		return null;
  	}

	@RequiresPermission("zf501003_add")
	@RequestMapping("areasave")
	@ResponseBody
	@LogAnnotation(module = 501003,type = OperationConstants.OPERATION_SAVE)
	public ResponseResult<BaseKeys> areaSave(@RequestBody List<ChinaArea> areaList) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = areaManageService.areaSave(areaList);
			if(result.getResult()==1) {
				List<Long> areacodes = new ArrayList<Long>();
				for(ChinaArea area:areaList) {
					areacodes.add(area.getAreaCode());
				}
				String dataJson = "areacode="+JSONArray.toJSONString(areacodes);
	            ProxyUtil.changeVariable(AreaManageController.class,"areaSave",dataJson);
			}
		} catch (Exception e) {
			logger.error("AreaManage save fail ",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf501003_modify")
	@RequestMapping("areaupdate")
	@ResponseBody
	@LogAnnotation(module = 501003,type = OperationConstants.OPERATION_UPDATE)
	public ResponseResult<BaseKeys> areaUpdate(@RequestBody List<ChinaArea> areaList) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = areaManageService.areaUpdate(areaList.get(0));
			if(result.getResult()==1) {
				List<Long> areacodes = new ArrayList<Long>();
				for(ChinaArea area:areaList) {
					areacodes.add(area.getAreaCode());
				}
				String dataJson = "areacode="+JSONArray.toJSONString(areacodes);
	            ProxyUtil.changeVariable(AreaManageController.class,"areaUpdate",dataJson);
			}
		} catch (Exception e) {
			logger.error("AreaManage update fail ",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		return result;
	}
	
	@RequestMapping("havesetprovince")
	@ResponseBody
	public String haveSetProvince() {
		try {
			return areaManageService.haveSetProvince();
		} catch (Exception e) {
			logger.error("check province set fail ",e);
		}
		return null;
	}
	
	@RequiresPermission("zf501003_delete")
	@RequestMapping("delete")
	@ResponseBody
	@LogAnnotation(module = 501003,type = OperationConstants.OPERATION_DELETE)
	public ResponseResult<BaseKeys> delete(@RequestParam("ids[]") List<Integer> ids) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		String message = null;
		try {
			message = areaManageService.delete(ids);
			if(message==null) {
				String dataJson = "areacode="+JSONArray.toJSONString(ids);
	            ProxyUtil.changeVariable(AreaManageController.class,"delete",dataJson);
	            result.setResult(1);
			}else {
				result.setResult(0);
				result.setMessage(message);
			}
		} catch (Exception e) {
			logger.error("delete fail ",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		return result;
	}
	
	
	@RequestMapping("getprovincepist")
	@ResponseBody
	public Map<String,Object> getProvinceList() {
		try {
			return areaManageService.getProvinceList();
		} catch (Exception e) {
			logger.error("get Province List fail",e);
		}
		return null;
	}
	
	@RequiresPermission("zf501002_modify")
	@RequestMapping("setprovince")
	@ResponseBody
	public ResponseResult<BaseKeys> setProvince(@RequestParam("province")String province,@RequestParam("areaName")String areaName,
												@RequestParam("areaShort")String areaShort) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = areaManageService.setProvince(province,areaName,areaShort);
		} catch (Exception e) {
			logger.error("set Province fail",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		return result;
	}
}

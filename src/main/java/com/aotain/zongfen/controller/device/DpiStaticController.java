package com.aotain.zongfen.controller.device;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.DpiDeviceStatusStrategy;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.device.DpiStatic;
import com.aotain.zongfen.model.device.DpiStaticPort;
import com.aotain.zongfen.service.device.DpiStaticPortService;
import com.aotain.zongfen.service.device.DpiStaticService;
import com.aotain.zongfen.service.device.DpiStatusService;
import com.aotain.zongfen.utils.PageResult;

@Controller
@RequestMapping("/device/dpiStatic")
public class DpiStaticController {
	
	private static Logger logger = LoggerFactory.getLogger(DpiStaticController.class);	
	
	@Autowired
	private DpiStaticService dpiStaticService;
	
	@Autowired
	private DpiStaticPortService dpiStaticPortService;
	
	@Autowired
	private DpiStatusService dpiStatusService;
	
	@RequestMapping(value ="/index")
    public ModelAndView index(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("/devicemanage/dpiStatic/index");
		return mav;
    }
	
	@RequiresPermission("zf301002_query")
	@RequestMapping(value="/list")
	@ResponseBody
	public PageResult<DpiStatic> listDpiStatic(DpiStatic dto,
			@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize) {
		try {
			return dpiStaticService.getList(pageIndex,pageSize, dto);
		} catch (Exception e) {
			logger.error("listDpiStatic fail",e);
		}
		return null;
	}
	
	@RequiresPermission("zf301002_query")
	@RequestMapping(value="/obtainOne")
	@ResponseBody
	public DpiStatic obtainOne(String deploysitename) {
		DpiStatic dpiStatic = new DpiStatic();
		try {
			dpiStatic = dpiStaticService.getByPK(deploysitename);
		} catch (Exception e) {
			logger.error("obtainOne fail",e);
		}
		return dpiStatic;
	}
	
	@RequiresPermission("zf301002_query")
	@RequestMapping(value="/listPorts")
	@ResponseBody
	public PageResult<DpiStaticPort> listDpiStaticPort(String deploysitename,
			@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize){
		try {
			return dpiStaticPortService.getList(pageIndex, pageSize, deploysitename);
		} catch (Exception e) {
			logger.error("listDpiStaticPort fail",e);
		}
		return null;
	}
	
	@RequiresPermission("zf301002_query")
	@RequestMapping(value = "/obtain")
    @ResponseBody
	public DpiDeviceStatusStrategy obtainOne() {
		try {
			return dpiStatusService.getOne(1);
		} catch (Exception e) {
			logger.error("obtainOne fail",e);
		}
		return null;
	}
	
	@RequiresPermission("zf301002_modify")
	@RequestMapping(value = "/add")
    @ResponseBody
    @LogAnnotation(module = 301002,type = OperationConstants.OPERATION_SAVE)
	public ResponseResult<DpiDeviceStatusStrategy> add(DpiDeviceStatusStrategy record){
		ResponseResult<DpiDeviceStatusStrategy> result = new ResponseResult<DpiDeviceStatusStrategy>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		try {
			if(record == null ) {
				result.setMessage("保存失败！");
				result.setResult(0);
			}else {
				/**
				 * 1)发redis策略，写DB
				 */
				dpiStatusService.addPolicy(record);
				BaseKeys bk = new BaseKeys();
				bk.setMessageNo(record.getMessageNo());
				bk.setMessageType(MessageType.DPI_DEVICE_QUERY_POLICY_STATIC.getId());
				bk.setOperType(OperationType.MODIFY.getType());
				bk.setDataType(DataType.POLICY.getType());
				keys.add(bk);
				result.setKeys(keys);
				result.setMessage("成功");
				result.setResult(1);
				String dataJson = "freq="+record.getRFreq();
	            ProxyUtil.changeVariable(DpiStaticController.class,"add",dataJson);
			}
		}catch(Exception e) {
			logger.error("add fail",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf301002_modify")
	@RequestMapping(value = "/modify")
    @ResponseBody
    @LogAnnotation(module = 301002,type = OperationConstants.OPERATION_UPDATE)
	public ResponseResult<DpiDeviceStatusStrategy> modify(DpiDeviceStatusStrategy record){
		ResponseResult<DpiDeviceStatusStrategy> result = new ResponseResult<DpiDeviceStatusStrategy>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		try {
			if(record == null) {
				result.setMessage("保存失败！");
				result.setResult(0);
			}else {
				dpiStatusService.modifyPolicy(record);
				BaseKeys bk = new BaseKeys();
				bk.setMessageNo(record.getMessageNo());
				bk.setMessageType(MessageType.DPI_DEVICE_QUERY_POLICY_STATIC.getId());
				bk.setOperType(OperationType.MODIFY.getType());
				bk.setDataType(DataType.POLICY.getType());
				keys.add(bk);
				result.setKeys(keys);
				result.setMessage("成功");
				result.setResult(1);
				String dataJson = "freq="+record.getRFreq();
	            ProxyUtil.changeVariable(DpiStaticController.class,"modify",dataJson);
			}
		}catch(Exception e) {
			logger.error("modify fail",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			
		}
		result.setKeys(null);
		return result;
	}

}


package com.aotain.zongfen.controller.device;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.aotain.zongfen.model.device.DpiDynamicCpu;
import com.aotain.zongfen.model.device.DpiDynamicPort;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.service.device.DpiDynamicCpuService;
import com.aotain.zongfen.service.device.DpiDynamicPortService;
import com.aotain.zongfen.service.device.DpiStatusService;

@Controller
@RequestMapping("/device/dpiDynamic")
public class DpiDynamicController {
	
	private static Logger logger = LoggerFactory.getLogger(DpiDynamicController.class);
	
	@Autowired
	private DpiStatusService dpiStatusService;
	
	@Autowired
	private DpiDynamicCpuService dpiDynamicCpuService;
	
	@Autowired
	private DpiDynamicPortService dpiDynamicPortService;
	
	@RequestMapping(value ="/index")
    public ModelAndView index(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("/devicemanage/dpiDynamic/index");
		return mav;
    }
	
	@RequiresPermission("zf301003_query")
	@RequestMapping(value = "/obtain")
    @ResponseBody
	public DpiDeviceStatusStrategy obtainOne() {
		try {
			return dpiStatusService.getOne(2);
		} catch (Exception e) {
			logger.error("obtainOne fail",e);
		}
		return null;
	}
	
	@RequiresPermission("zf301003_modify")
	@RequestMapping(value = "/add")
    @ResponseBody
    @LogAnnotation(module = 301003,type = OperationConstants.OPERATION_SAVE)
	public ResponseResult<DpiDeviceStatusStrategy> add(DpiDeviceStatusStrategy record){
		ResponseResult<DpiDeviceStatusStrategy> result = new ResponseResult<DpiDeviceStatusStrategy>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		try {
			if(record == null) {
				result.setMessage("保存失败！");
				result.setResult(0);
			}else {	
								
				/**
				 * 1)发redis策略，写DB
				 */
				dpiStatusService.addPolicy(record);
				BaseKeys bk = new BaseKeys();
				bk.setMessageNo(record.getMessageNo());
				bk.setMessageType(MessageType.DPI_DEVICE_QUERY_POLICY_DYNAMIC.getId());
				bk.setOperType(OperationType.MODIFY.getType());
				bk.setDataType(DataType.POLICY.getType());
				keys.add(bk);
				result.setKeys(keys);
				result.setMessage("成功");
				result.setResult(1);
				String dataJson = "freq="+record.getRFreq();
	            ProxyUtil.changeVariable(DpiDynamicController.class,"add",dataJson);
			}
		}catch(Exception e) {
			logger.error("add fail",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf301003_modify")
	@RequestMapping(value = "/modify")
    @ResponseBody
    @LogAnnotation(module = 301003,type = OperationConstants.OPERATION_UPDATE)
	public ResponseResult<DpiDeviceStatusStrategy> modify(DpiDeviceStatusStrategy record){
		ResponseResult<DpiDeviceStatusStrategy> result = new ResponseResult<DpiDeviceStatusStrategy>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		try {
			if(record == null ) {
				result.setMessage("保存失败！");
				result.setResult(0);
			}else {
				dpiStatusService.modifyPolicy(record);
				BaseKeys bk = new BaseKeys();
				bk.setMessageNo(record.getMessageNo());
				bk.setMessageType(MessageType.DPI_DEVICE_QUERY_POLICY_DYNAMIC.getId());
				bk.setOperType(OperationType.MODIFY.getType());
				bk.setDataType(DataType.POLICY.getType());
				keys.add(bk);
				result.setKeys(keys);
				result.setMessage("成功");
				result.setResult(1);
				String dataJson = "freq="+record.getRFreq();
	            ProxyUtil.changeVariable(DpiDynamicController.class,"modify",dataJson);
			}
		}catch(Exception e) {
			logger.error("modify fail",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf301003_query")
	@RequestMapping(value = "/obtainAllCPU")
    @ResponseBody
	public List<DpiDynamicCpu> obtainAllCPU(){
		try {
			return dpiDynamicCpuService.getCpuList();
		} catch (Exception e) {
			logger.error("obtainAllCPU fail",e);
		}
		return null;
	}
	
	@RequiresPermission("zf301003_query")
	@RequestMapping(value = "/obtainEchartByCpu")
    @ResponseBody
	public ECharts<Integer> obtainEchartByCpu(Integer cpuNo){
		try {
			return dpiDynamicCpuService.getChartByCpu(cpuNo);
		} catch (Exception e) {
			logger.error("obtainEchartByCpu fail",e);
		}
		return null;
	}
	
	@RequiresPermission("zf301003_query")
	@RequestMapping(value = "/obtainAllPort")
    @ResponseBody
	public List<DpiDynamicPort> obtainAllPort(){
		try {
			return dpiDynamicPortService.getPortList();
		} catch (Exception e) {
			logger.error("obtainAllPort fail",e);
		}
		return null;
	}
	
	@RequiresPermission("zf301003_query")
	@RequestMapping(value = "/obtainEchartByPort")
    @ResponseBody
	public ECharts<Integer> obtainEchartByPort(DpiDynamicPort dpiDynamicPort){
		try {
			return dpiDynamicPortService.getChartByPort(dpiDynamicPort);
		} catch (Exception e) {
			logger.error("obtainEchartByPort fail",e);
		}
		return null;
	}
}

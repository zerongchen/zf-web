package com.aotain.zongfen.controller.device;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.DpiDeviceInfoStrategy;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.device.GraphDto;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.device.DpiUploadDevice;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.service.device.DeviceManageService;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;

@Controller
@RequestMapping("/device/devicemanage")
public class DeviceManageController {
	
	private static Logger logger = LoggerFactory.getLogger(DeviceManageController.class);

    @Autowired
    private DeviceManageService deviceMnageService;


    @RequestMapping("index")
    public String home(ModelMap modelMap) throws Exception{
        return "/devicemanage/deviceManage/devicemanageIndex";
    }
    
    @RequiresPermission("zf301001_add")
    @RequestMapping("dpiRecDeviceInsert")
	@ResponseBody
	@LogAnnotation()
    public ResponseResult<BaseKeys> dpiRecDeviceInsert(@RequestBody DpiDeviceInfoStrategy dpiRecDevice) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			dpiRecDevice.setOperationType(OperationConstants.OPERATION_SAVE);
			result = deviceMnageService.dpiRecDeviceInsert(dpiRecDevice);
			if(result.getKeys()!=null && result.getKeys().size()>0 && result.getResult()==1) {
				for(BaseKeys temp : result.getKeys()) {
					String dataJson = "dpiRecId="+temp.getId();
					ProxyUtil.changeVariable(this.getClass(),"dpiRecDeviceInsert",dataJson,dataJson, ModelType.MODEL_DEVICE_MANAGE.getModel(), OperationType.CREATE);
				}
			}
		} catch (Exception e) {
			logger.error("dpiRecDeviceInsert fail ",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
    }
    
    @RequiresPermission("zf301001_modify")
    @RequestMapping("dpiRecDeviceUpdate")
	@ResponseBody
	@LogAnnotation()
    public ResponseResult<BaseKeys> dpiRecDeviceUpdate(@RequestBody DpiDeviceInfoStrategy dpiRecDevice) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			dpiRecDevice.setOperationType(OperationConstants.OPERATION_UPDATE);
			result = deviceMnageService.dpiRecDeviceUpdate(dpiRecDevice);
			if(result.getKeys()!=null && result.getKeys().size()>0 && result.getResult()==1) {
				for(BaseKeys temp : result.getKeys()) {
					String dataJson = "dpiRecId="+temp.getId();
					ProxyUtil.changeVariable(this.getClass(),"dpiRecDeviceUpdate",dataJson,dataJson, ModelType.MODEL_DEVICE_MANAGE.getModel(), OperationType.MODIFY);
				}
			}
		} catch (Exception e) {
			logger.error("dpiRecDeviceUpdate fail ",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
    }
    
    @RequiresPermission("zf301001_add")
    @RequestMapping("saveUploadDev")
	@ResponseBody
	@LogAnnotation()
    public ResponseResult<BaseKeys> saveUploadDev(@RequestBody DpiUploadDevice uploadDevice) {
    	ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			uploadDevice.setModifyOper(SpringUtil.getSysUserName());
			uploadDevice.setModifyTime(new Date());
			result = deviceMnageService.uploadDeviceSaveOrUpdate(uploadDevice);
			if(result.getKeys()!=null && result.getKeys().size()>0 && result.getResult()==1) {
				String dataJson = "dpiUploadId="+ (uploadDevice.getDpiId()!=null?uploadDevice.getDpiId().toString():result.getKeys().get(0).getMessageName());
				ProxyUtil.changeVariable(this.getClass(),"saveUploadDev",dataJson,dataJson, ModelType.MODEL_DEVICE_MANAGE.getModel(),OperationType.CREATE);
			}
		} catch (Exception e) {
			logger.error("saveUploadDev fail ",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
    }
    
    @RequiresPermission("zf301001_modify")
    @RequestMapping("updateUploadDev")
	@ResponseBody
	@LogAnnotation()
    public ResponseResult<BaseKeys> updateUploadDev(@RequestBody DpiUploadDevice uploadDevice) {
    	ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			uploadDevice.setModifyOper(SpringUtil.getSysUserName());
			uploadDevice.setModifyTime(new Date());
			result = deviceMnageService.uploadDeviceSaveOrUpdate(uploadDevice);
			if(result.getKeys()!=null && result.getKeys().size()>0 && result.getResult()==1) {
				String dataJson = "dpiUploadId="+ (uploadDevice.getDpiId()!=null?uploadDevice.getDpiId().toString():result.getKeys().get(0).getMessageName());
				ProxyUtil.changeVariable(this.getClass(),"updateUploadDev",dataJson,dataJson, ModelType.MODEL_DEVICE_MANAGE.getModel(),OperationType.MODIFY);
			}
		} catch (Exception e) {
			logger.error("updateUploadDev fail ",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
    }
    
    @RequiresPermission("zf301001_add")
    @RequestMapping("saveZongFenDev")
	@ResponseBody
	@LogAnnotation()
    public ResponseResult<BaseKeys> saveZongFenDev(@RequestBody ZongFenDevice zongFenDevice) {
    	ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			zongFenDevice.setModifyOper(SpringUtil.getSysUserName());
			zongFenDevice.setModifyTime(new Date());
			result = deviceMnageService.saveOrUpdateZongFenDev(zongFenDevice);
			if(result.getKeys()!=null && result.getKeys().size()>0 && result.getResult()==1) {
				String dataJson = "zongfenId="+ (zongFenDevice.getZongfenId()!=null?zongFenDevice.getZongfenId().toString():result.getKeys().get(0).getMessageName());
				ProxyUtil.changeVariable(this.getClass(),"saveZongFenDev",dataJson,dataJson, ModelType.MODEL_DEVICE_MANAGE.getModel(),result.getKeys().get(0).getOperType()==OperationType.MODIFY.getType()?OperationType.MODIFY:OperationType.CREATE);
			}
		} catch (Exception e) {
			logger.error("saveZongFenDev fail ",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
    }
    
    @RequiresPermission("zf301001_modify")
    @RequestMapping("updateZongFenDev")
	@ResponseBody
	@LogAnnotation()
    public ResponseResult<BaseKeys> updateZongFenDev(@RequestBody ZongFenDevice zongFenDevice) {
    	ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			zongFenDevice.setModifyOper(SpringUtil.getSysUserName());
			zongFenDevice.setModifyTime(new Date());
			result = deviceMnageService.saveOrUpdateZongFenDev(zongFenDevice);
			if(result.getKeys()!=null && result.getKeys().size()>0 && result.getResult()==1) {
				String dataJson = "zongfenId="+ (zongFenDevice.getZongfenId()!=null?zongFenDevice.getZongfenId().toString():result.getKeys().get(0).getMessageName());
				ProxyUtil.changeVariable(this.getClass(),"updateZongFenDev",dataJson,dataJson, ModelType.MODEL_DEVICE_MANAGE.getModel(),result.getKeys().get(0).getOperType()==OperationType.MODIFY.getType()?OperationType.MODIFY:OperationType.CREATE);
			}
		} catch (Exception e) {
			logger.error("updateZongFenDev fail ",e);
			result.setMessage("保存失败！");
			result.setResult(0);
			return result;
		}
		result.setKeys(null);
		return result;
    }
    
    /**
     * 
    * @Title: getZongFenData
    * @Description: 获取综分设备列表数据 
    * @param  pageSize
    * @param  pageIndex
    * @param  deviceName
    * @return PageResult<ZongFenDevice>
    * @throws
     */
    @RequiresPermission("zf301001_query")
    @RequestMapping("getZongFenData")
	@ResponseBody
	public List<ZongFenDevice> getZongFenData(@RequestParam("deviceName") String deviceName) {
    	try {
    		return deviceMnageService.getZongFenData(deviceName);
		} catch (Exception e) {
			logger.error("getZongFenData fail ",e);
		}
    	return null;
    }
 
    /**
     * 
    * @Title: getDPIRecData
    * @Description: 获取DPI接收设备列表数据 
    * @param  pageSize
    * @param  pageIndex
    * @param  deviceName
    * @return PageResult<DpiDeviceInfoStrategy>
    * @throws
     */
    @RequiresPermission("zf301001_query")
    @RequestMapping("getDPIRecData")
   	@ResponseBody
   	public PageResult<DpiDeviceInfoStrategy> getDPIRecData(@RequestParam(required = true, name = "pageSize") Integer pageSize,
   													 @RequestParam(required = true, name = "pageIndex") Integer pageIndex,
   													 @RequestParam(required = true, name = "deviceName") String deviceName) {
       	try {
       		return deviceMnageService.getDPIRecData(pageSize,pageIndex,deviceName);
		} catch (Exception e) {
			logger.error("getZongFenData fail ",e);
		}
    	return null;
    }
    
    /**
     * 
    * @Title: getDPIUplData
    * @Description: DPI上报数据设备列表查询
    * @param @param pageSize
    * @param @param pageIndex
    * @param @param deviceName
    * @param @return
    * @return PageResult<DpiUploadDevice>
    * @throws
     */
    @RequiresPermission("zf301001_query")
    @RequestMapping("getDPIUplData")
   	@ResponseBody
   	public PageResult<DpiUploadDevice> getDPIUplData(@RequestParam(required = true, name = "pageSize") Integer pageSize,
   													 @RequestParam(required = true, name = "pageIndex") Integer pageIndex,
   													 @RequestParam(required = true, name = "deviceType") String deviceType,
   													@RequestParam(required = true, name = "startIp") String startIp,
   													@RequestParam(required = true, name = "endIp") String endIp) {
       	try {
       		return deviceMnageService.getDPIUplData( pageSize, pageIndex,  deviceType,  startIp, endIp);
		} catch (Exception e) {
			logger.error("getDPIUplData fail ",e);
		}
    	return null;
    }
    
    /**
     * 
    * @Title: getRealIpList
    * @Description: 获取物理服务器信息
    * @param 
    * @return List<Map<String,String>>
    * @throws
     */
    @RequestMapping("getRealIpList")
    @ResponseBody
    public List<Map<String,Object>> getRealIpList(){
    	try {
    		return deviceMnageService.getRealZongFenIp();
		} catch (Exception e) {
			logger.error("search fail ",e);
		}
    	return null;
    }
    
    @RequestMapping("getDictDataList")
	@ResponseBody
    public Map<String,Object> getDictDataList(@RequestParam("dpiId")Integer dpiId){
    	try {
    		return deviceMnageService.getDictDataList(dpiId);
		} catch (Exception e) {
			logger.error("search fail ",e);
		}
    	return null;
    }
    
    @RequiresPermission("zf301001_delete")
	@RequestMapping("deleteDevice")
	@ResponseBody
	@LogAnnotation()
	public ResponseResult deleteDevice(@RequestParam("deviceId")Integer deviceId,@RequestParam("flag")Integer flag,
										@RequestParam("devType")Integer devType,@RequestParam("dpiType")Integer dpiType,
										@RequestParam("ip")String ip) {
		ResponseResult result = new ResponseResult();
		String message = null;
		try {
			message = deviceMnageService.deleteDevice(deviceId,flag,devType,dpiType,ip);
			if(message==null) {
				result.setResult(1);
			}else {
				result.setMessage(message);
				result.setResult(0);
			}
			if(result.getResult()==1) {
				String dataJson ="";
				if(flag==1) {
					if(deviceId!=null) {
						dataJson = "zongfenId="+deviceId.toString();
					}else {
						dataJson = "zongfenIp="+ip;
					}
				}else if(flag==2) {
					dataJson = "dpiRecId="+deviceId.toString();
				}else if(flag==3) {
					dataJson = "dpiUploadId="+deviceId.toString();
				}
				ProxyUtil.changeVariable(this.getClass(),"deleteDevice",dataJson,dataJson, ModelType.MODEL_DEVICE_MANAGE.getModel(),OperationType.DELETE);
			}
			result.setKeys(null);
		} catch (Exception e) {
			logger.error("delete fail ",e);
			result.setMessage("删除失败！");
			result.setResult(0);
			return result;
		}
		return result;
	}
	
	@RequestMapping("checkDeviceUseage")
	@ResponseBody
	public Integer checkDeviceUseage(@RequestParam("zongFenId")Integer zongFenId,@RequestParam("type")String type) {
		try {
			return deviceMnageService.checkDeviceUseage(zongFenId,type);
		} catch (Exception e) {
			logger.error("check fail",e);
		}
		return null;
	}
	
	@RequiresPermission("zf301001_query")
	@RequestMapping("getNetGraph")
	@ResponseBody
	public GraphDto getNetGraph() {
		try {
			return deviceMnageService.getDeviceGraph();
		} catch (Exception e) {
			logger.error("get chart data fail ",e);
		}
		return null;
	}
	
	@RequestMapping("getIsAuto")
	@ResponseBody
	public String getIsAuto() {
		return deviceMnageService.getIsAuto();
	}
}

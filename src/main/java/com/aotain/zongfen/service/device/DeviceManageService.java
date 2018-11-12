package com.aotain.zongfen.service.device;

import java.util.List;
import java.util.Map;

import com.aotain.common.policyapi.model.DpiDeviceInfoStrategy;
import com.aotain.zongfen.dto.device.GraphDto;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.device.DpiUploadDevice;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.utils.PageResult;

public interface DeviceManageService{

	
	/**
	 * 
	* @Title: getDictDataList
	* @Description: 下拉框值获取
	* @param @param dpiId
	* @param @return
	* @return Map<String,Object>
	* @throws
	 */
	public Map<String, Object> getDictDataList(Integer dpiId);

	/**
	 * 
	* @Title: dpiRecDeviceInsert
	* @Description: 新增DPI接收数据设备
	* @param @param dpiDev
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> dpiRecDeviceInsert(DpiDeviceInfoStrategy dpiDev);

	/**
	 * 
	* @Title: getDPIRecData
	* @Description: 查询DPI接收数据设备信息
	* @param @param pageSize
	* @param @param pageIndex
	* @param @param deviceName
	* @param @return
	* @return PageResult<DpiDeviceInfoStrategy>
	* @throws
	 */
	public PageResult<DpiDeviceInfoStrategy> getDPIRecData(Integer pageSize, Integer pageIndex, String deviceName);

	/**
	 * 
	* @Title: dpiRecDeviceUpdate
	* @Description: DPI接收数据设备信息更新 
	* @param @param dpiDev
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> dpiRecDeviceUpdate(DpiDeviceInfoStrategy dpiDev);
	
	/**
	 * 
	* @Title: uploadDeviceSaveOrUpdate
	* @Description: 保存或更新DPI数据上报设备信息 
	* @param @param uploadDevice
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> uploadDeviceSaveOrUpdate(DpiUploadDevice uploadDevice);
	
	/**
	 * 
	* @Title: getDPIUplData
	* @Description: 获取DPI数据上报设备信息列表
	* @param @param pageSize
	* @param @param pageIndex
	* @param @param deviceType
	* @param @param startIp
	* @param @param endIp
	* @param @return
	* @return PageResult<DpiUploadDevice>
	* @throws
	 */
	public PageResult<DpiUploadDevice> getDPIUplData(Integer pageSize,Integer pageIndex, String deviceType, String startIp,String endIp);

	/**
	 * 
	* @Title: saveOrUpdateZongFenDev
	* @Description: 综分设备信息新增和修改 
	* @param @param zongFenDevice
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> saveOrUpdateZongFenDev(ZongFenDevice zongFenDevice);
	
	/**
	 * 
	* @Title: getRealZongFenIp
	* @Description: 获取物理IP地址列表
	* @param @return
	* @return List<Map<String,Object>>
	* @throws
	 */
	public List<Map<String,Object>> getRealZongFenIp();
	
	/**
	 * 
	* @Title: getZongFenData
	* @Description: 综分设备数据查询
	* @param @param deviceName
	* @param @return
	* @return List<ZongFenDevice>
	* @throws
	 */
	public List<ZongFenDevice> getZongFenData(String deviceName);
	
	/**
	 * 
	* @Title: deleteDevice
	* @Description: 设备删除
	* @param @param deviceId
	* @param @param flag
	* @param @param devType
	* @param @param dpiType
	* @param @param ip
	* @param @return
	* @return String
	* @throws
	 */
	public String deleteDevice(Integer deviceId,Integer flag,Integer devType,Integer dpiType,String ip);
	
	/**
	 * 
	* @Title: checkDeviceUseage
	* @Description: 校验设备是否被使用
	* @param @param zongFenId
	* @param @return
	* @return Integer
	* @throws
	 */
	public Integer checkDeviceUseage(Integer zongFenId,String type);
	
	/**
	 * 
	* @Title: getDeviceGraph
	* @Description: 获取网络拓扑图组成数据
	* @param @return
	* @return GraphDto
	* @throws
	 */
	public GraphDto getDeviceGraph();
	
	/**
	 * 
	* @Title: getIsAuto
	* @Description: 是否自动连接policy
	* @param @return
	* @return String
	* @throws
	 */
	public String getIsAuto();
	
}

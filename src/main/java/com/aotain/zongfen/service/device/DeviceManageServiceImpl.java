package com.aotain.zongfen.service.device;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.config.LocalConfig;
import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.DevFlag;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.DpiDeviceInfoStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.utils.constant.RedisKeyConstant;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.cache.CommonCache;
import com.aotain.zongfen.dto.device.GraphDto;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.common.ChinaAreaMapper;
import com.aotain.zongfen.mapper.common.DictFactoryMapper;
import com.aotain.zongfen.mapper.device.DPIRecDeviceMapper;
import com.aotain.zongfen.mapper.device.DpiRecHouseMapper;
import com.aotain.zongfen.mapper.device.DpiUploadDeviceMapper;
import com.aotain.zongfen.mapper.device.IdcHouseMapper;
import com.aotain.zongfen.mapper.device.ZongFenDeviceMapper;
import com.aotain.zongfen.mapper.device.ZongFenDeviceUserMapper;
import com.aotain.zongfen.mapper.device.ZongFenRelMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ChinaArea;
import com.aotain.zongfen.model.common.DictFactory;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.device.DpiRecHouse;
import com.aotain.zongfen.model.device.DpiUploadDevice;
import com.aotain.zongfen.model.device.IdcHouse;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.device.ZongFenDeviceUser;
import com.aotain.zongfen.model.device.ZongFenRel;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.utils.IPUtil;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class DeviceManageServiceImpl extends BaseService implements DeviceManageService {

	private static Logger logger = LoggerFactory.getLogger(DeviceManageServiceImpl.class);
	
	@Autowired
	private DPIRecDeviceMapper dpiRecDeviceMapper;
	
	@Autowired
	private DpiRecHouseMapper dpiRecHouseMapper;
	
	@Autowired
	private IdcHouseMapper idcHouseMapper;
	
	@Autowired
	private ChinaAreaMapper chinaAreaMapper;
	
	@Autowired
	private DpiUploadDeviceMapper dpiUploadDeviceMapper;
	
	@Autowired
	private DictFactoryMapper dictFactoryMapper;
	
	@Autowired
	private PolicyMapper policyMapper;
	
	@Autowired
	private ZongFenDeviceMapper zongFenDeviceMapper;
	
	@Autowired
	private ZongFenRelMapper zongFenRelMapper;

	@Autowired
	private ZongFenDeviceUserMapper zongFenDeviceUserMapper;
	
	/**
	 * 
	* @Title: getDictDataList
	* @Description: 下拉框值获取
	* @param @param dpiId
	* @param @return
	* @return Map<String,Object>
	* @throws
	 */
	public Map<String, Object> getDictDataList(Integer dpiId) {
		Map<String, Object> result = new HashMap<String, Object>();
		//获取部署位置
		String deployMode = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.SYSTEM_DEPLOY_MODE);
		String parentCode = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.PROVINCE_AREA);
		if(parentCode==null || "".equals(parentCode)) {
			result.put("areaList", null);
		}else {
			Map<String,Object> query = new HashMap<String, Object>();
			query.put("type",deployMode);
			query.put("parentCode",parentCode);
			List<ChinaArea> areaList = chinaAreaMapper.getAreaList(query);
			result.put("areaList", areaList);
		}
		
		List<IdcHouse> houseList = idcHouseMapper.getIdcHouseList();
		result.put("houseList", houseList);
		if(dpiId!=0) {
			List<String> selectIdc = dpiRecHouseMapper.getListByDpiId(dpiId);
			result.put("selectIdc", selectIdc);
		}
		 List<DictFactory> factory = dictFactoryMapper.getDictFactoryList();
		 result.put("factory", factory);
		return result;
	}

	/**
	 * 
	* @Title: dpiRecDeviceInsert
	* @Description: 新增DPI接收数据设备
	* @param @param dpiDev
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> dpiRecDeviceInsert(DpiDeviceInfoStrategy dpiDev) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		Map<String,Object> queryIp = new HashMap<String, Object>();
		queryIp.put("ip", dpiDev.getDpiIp());
		queryIp.put("id", null);
		int samIPcount = dpiRecDeviceMapper.getSamIPCount(queryIp);
		if(samIPcount>0) {
			result.setResult(0);
			result.setMessage("存在相同IP的DPI信息接收设备");
			return result;
		}
		Map<String,Object> query = new HashMap<String, Object>();
		query.put("name", dpiDev.getDpiDevName());
		query.put("id", null);
		int samName = dpiRecDeviceMapper.getSamNameCount(query);
		if(samName>0) {
			result.setResult(0);
			result.setMessage("存在相同名称的设备");
			return result;
		}
		//发策略
		addPolicy(dpiDev);
		key.setId((long)dpiDev.getDpiId());
		keys.add(key);
		result.setKeys(keys);
		result.setResult(1);
		return result;
	}

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
	public PageResult<DpiDeviceInfoStrategy> getDPIRecData(Integer pageSize, Integer pageIndex, String deviceName) {
		PageResult<DpiDeviceInfoStrategy> result = new PageResult<DpiDeviceInfoStrategy>();
		PageHelper.startPage(pageIndex, pageSize);
		List<DpiDeviceInfoStrategy> info = dpiRecDeviceMapper.getInfoByName(deviceName);
		PageInfo<DpiDeviceInfoStrategy> pageResult = new PageInfo<DpiDeviceInfoStrategy>(info);
		result.setTotal(pageResult.getTotal());
		//综分设备通用信息
		String serviceFlag = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.CENTER_POLICY_ENABLEZOOKEEPER);
		if(serviceFlag!=null && "1".equals(serviceFlag)) {
			Map<String,String> ZFdev = rediscluster.getHashs(RedisKeyConstant.ZF_GENERAL_INFORMATION);
			if(ZFdev!=null && !ZFdev.isEmpty()) {
				Set<Map.Entry<String,String>> ZFSite = ZFdev.entrySet();
				Iterator<Map.Entry<String,String>>  ZFiterator = ZFSite.iterator();
				while(ZFiterator.hasNext()) {
					String zfIp = ZFiterator.next().getKey();
					//综分设备与DPI设备关联信息
					Map<String,String> ZFDpi = rediscluster.getHashs("ZF_DPI_Relation_"+zfIp);
					if(ZFDpi!=null && !ZFDpi.isEmpty()) {
						for(DpiDeviceInfoStrategy temp:info) {
							if(ZFDpi.containsKey(temp.getDpiIp()+"|"+temp.getDpiPort())) {
								temp.setZongFenServer(zfIp);
								temp.setConnectFlag(ZFDpi.get(temp.getDpiIp()+"|"+temp.getDpiPort()));
							}
						}
					}
				}
			}
		}else {
			for(DpiDeviceInfoStrategy temp:info) {
				Map<String,String> ZFDpi = rediscluster.getHashs("ZF_DPI_Relation_"+temp.getPolicyIp());
				if(ZFDpi!=null && !ZFDpi.isEmpty() && ZFDpi.containsKey(temp.getDpiIp()+"|"+temp.getDpiPort())) {
					temp.setZongFenServer(temp.getPolicyIp());
					temp.setConnectFlag(ZFDpi.get(temp.getDpiIp()+"|"+temp.getDpiPort()));
				} else {
					temp.setConnectFlag("1");
				}
			}
		}
		result.setRows(info);
		return result;
	}

	/**
	 * 
	* @Title: dpiRecDeviceUpdate
	* @Description: DPI接收数据设备信息更新 
	* @param @param dpiDev
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> dpiRecDeviceUpdate(DpiDeviceInfoStrategy dpiDev) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		Map<String,Object> queryIp = new HashMap<String, Object>();
		queryIp.put("ip", dpiDev.getDpiIp());
		queryIp.put("id", dpiDev.getDpiId());
		int samIPcount = dpiRecDeviceMapper.getSamIPCount(queryIp);
		if(samIPcount>0) {
			result.setResult(0);
			result.setMessage("存在相同IP的DPI信息接收设备");
			return result;
		}
		Map<String,Object> query = new HashMap<String, Object>();
		query.put("name", dpiDev.getDpiDevName());
		query.put("id", dpiDev.getDpiId());
		int samName = dpiRecDeviceMapper.getSamNameCount(query);
		if(samName>0) {
			result.setResult(0);
			result.setMessage("存在相同名称的设备");
			return result;
		}
		dpiDev.setMessageType(MessageType.DEVICE_INFO_POLICY.getId());
		String serviceFlag = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.CENTER_POLICY_ENABLEZOOKEEPER);
		if(serviceFlag!=null && "0".equals(serviceFlag)) {
			DpiDeviceInfoStrategy old = dpiRecDeviceMapper.getInfoById(Integer.valueOf(dpiDev.getDpiId()));
			if(!old.getPolicyIp().equals(dpiDev.getPolicyIp())) {
				deleteRecDevice(old);
			}
		}
		deleteRelative(dpiDev.getDpiIp(), dpiDev.getDpiPort());
		//发策略
		modifyPolicy(dpiDev);
		key.setId((long)dpiDev.getDpiId());
		keys.add(key);
		result.setKeys(keys);
		result.setResult(1);
		return result;
	}
	
	
	public void deleteRecDevice(BaseVO arg0) {
		DpiDeviceInfoStrategy dpiDev = (DpiDeviceInfoStrategy)arg0;
		deleteRelative(dpiDev.getDpiIp(), dpiDev.getDpiPort());
		long messageSequenceNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.DEVICE_INFO_POLICY.getId());
		dpiDev.setMessageSequenceNo(messageSequenceNo);
		dpiDev.setMessageType(MessageType.DEVICE_INFO_POLICY.getId());
		dpiDev.setOperationType(OperationConstants.OPERATION_DELETE);
		if(dpiDev.getProbeType()==0) {
			List<String> idcs = new ArrayList<String>();
			dpiDev.setIdcHouseIds(idcs);
		}
		boolean success = deleteServerStatusToRedis(arg0);
		if(!success) {
	            logger.error("addServerStatusToRedis failed..."+arg0.objectToJson());
	        }
		
		success = addTaskAndChannelToRedis(arg0);
		if(!success) {
            logger.error("addTaskAndChannelToRedis failed..."+arg0.objectToJson());
        }
	}
	
	
	
	/**
	 * 
	* @Title: uploadDeviceSaveOrUpdate
	* @Description: 保存或更新DPI数据上报设备信息 
	* @param @param uploadDevice
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> uploadDeviceSaveOrUpdate(DpiUploadDevice uploadDevice) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		Map<String,String> query = new HashMap<String, String>();
		query.put("dpiType", "");
		query.put("startIp", "");
		query.put("endIp", "");
		if(uploadDevice.getDpiId()==null) {
			query.put("dpiId","");
		}else {
			query.put("dpiId",uploadDevice.getDpiId().toString());
		}
		//是否存在覆盖的IP地址段校验
		List<DpiUploadDevice>  allList =  dpiUploadDeviceMapper.getUplDpiList(query);
		for(int j=0;j<uploadDevice.getStartIps().length;j++) {
			long start = IPUtil.ipToLong(uploadDevice.getStartIps()[j]);
			long end = IPUtil.ipToLong(uploadDevice.getEndIps()[j]);
			for(int m=0;m<allList.size();m++) {
				if(!(end<IPUtil.ipToLong(allList.get(m).getStartIp()) || start>IPUtil.ipToLong(allList.get(m).getEndIp()))) {
					result.setResult(0);
					result.setMessage("存在覆盖的IP地址段，请重新输入");
					return result;
				}
			}
		}
		Gson jsonStr = new Gson();
		//保存到redis中的信息 {设备类型+地区码+厂家代码}
		Map<String,Object> devInfo = new HashMap<String, Object>();
		devInfo.put("probeType",uploadDevice.getProbeType());
		devInfo.put("areaId",uploadDevice.getAreaId());
		devInfo.put("softwareProvider",uploadDevice.getSoftwareProvider());
		if(uploadDevice.getDpiId()==null) {//新增
			List<DpiUploadDevice> devList = new ArrayList<DpiUploadDevice>();
			for(int i=0;i<uploadDevice.getStartIps().length;i++) {
				DpiUploadDevice uploadDeviceTemp = new DpiUploadDevice();
				uploadDeviceTemp.setAreaId(uploadDevice.getAreaId());
				uploadDeviceTemp.setModifyOper(uploadDevice.getModifyOper());
				uploadDeviceTemp.setModifyTime(uploadDevice.getModifyTime());
				uploadDeviceTemp.setCreateOper(uploadDevice.getModifyOper());
				uploadDeviceTemp.setCreateTime(uploadDevice.getModifyTime());
				uploadDeviceTemp.setProbeType(uploadDevice.getProbeType());
				uploadDeviceTemp.setSoftwareProvider(uploadDevice.getSoftwareProvider());
				uploadDeviceTemp.setStartIp(uploadDevice.getStartIps()[i]);
				uploadDeviceTemp.setEndIp(uploadDevice.getEndIps()[i]);
				devList.add(uploadDeviceTemp);
			}
			if(devList.size()>0) {
				dpiUploadDeviceMapper.insertList(devList);
				for(int n=0;n<uploadDevice.getStartIps().length;n++) {
					long start = IPUtil.ipToLong(uploadDevice.getStartIps()[n]);
					long end = IPUtil.ipToLong(uploadDevice.getEndIps()[n]);
					for(;start<=end;start++) {
						rediscluster.putHash(RedisKeyConstant.DPI_ATTRIBUTE_INFO, IPUtil.longToIP(start), jsonStr.toJson(devInfo));
					}
				}
				List<Integer> idList = new ArrayList<Integer>();
				for(DpiUploadDevice temp:devList) {
					idList.add(temp.getDpiId());
				}
				key.setMessageName(JSONArray.toJSONString(idList));
				key.setOperType(OperationType.CREATE.getType());
			}
		}else {//修改
			uploadDevice.setStartIp(uploadDevice.getStartIps()[0]);
			uploadDevice.setEndIp(uploadDevice.getEndIps()[0]);
			DpiUploadDevice uplInfo = dpiUploadDeviceMapper.getUplDpiInfoById(uploadDevice.getDpiId());
			long oldStart = IPUtil.ipToLong(uplInfo.getStartIp());
			long oldEnd = IPUtil.ipToLong(uplInfo.getEndIp());
			dpiUploadDeviceMapper.updateByPrimaryKeySelective(uploadDevice);
			for(;oldStart<=oldEnd;oldStart++) {
				rediscluster.removeHash(RedisKeyConstant.DPI_ATTRIBUTE_INFO, IPUtil.longToIP(oldStart));
			}
			long newStart = IPUtil.ipToLong(uploadDevice.getStartIp());
			long newEnd = IPUtil.ipToLong(uploadDevice.getEndIp());
			for(;newStart<=newEnd;newStart++) {
				rediscluster.putHash(RedisKeyConstant.DPI_ATTRIBUTE_INFO, IPUtil.longToIP(newStart), jsonStr.toJson(devInfo));
			}
			key.setOperType(OperationType.MODIFY.getType());
		}
		keys.add(key);
		result.setKeys(keys);
		result.setResult(1);
		return result;
	}
	
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
	public PageResult<DpiUploadDevice> getDPIUplData(Integer pageSize,Integer pageIndex, String deviceType, String startIp,String endIp){
		PageResult<DpiUploadDevice> result = new PageResult<DpiUploadDevice>();
		PageHelper.startPage(pageIndex, pageSize);
		Map<String,String> query = new HashMap<String, String>();
		query.put("dpiType", deviceType);
		query.put("startIp", startIp);
		query.put("endIp", endIp);
		query.put("dpiId","");
		List<DpiUploadDevice> info = dpiUploadDeviceMapper.getUplDpiList(query);
		PageInfo<DpiUploadDevice> pageResult = new PageInfo<DpiUploadDevice>(info);
		result.setTotal(pageResult.getTotal());
		result.setRows(info);
		return result;
	}

	/**
	 * 
	* @Title: saveOrUpdateZongFenDev
	* @Description: 综分设备信息新增和修改 
	* @param @param zongFenDevice
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> saveOrUpdateZongFenDev(ZongFenDevice zongFenDevice){
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		if(zongFenDevice.getZongfenId()==null) {//新增
			List<ZongFenRel> zongFenRellist = new ArrayList<ZongFenRel>();
			for(String str:zongFenDevice.getRealIp()) {
				String[] tempStr = str.split(":");
				ZongFenRel zongFenRel = new ZongFenRel();
				zongFenRel.setIp(tempStr[0]);
				zongFenRellist.add(zongFenRel);
			}
			int existIp =  zongFenDeviceMapper.getExistIp(zongFenRellist);
			if(existIp>0) {
				result.setResult(0);
				result.setMessage("已存在相同物理IP的设备");
				return result;
			}
			if(zongFenDevice.getIsVirtualIp()==1) {//负载均衡
				zongFenDevice.setZongfenPort(Integer.valueOf(zongFenDevice.getRealIp().get(0).split(":")[1]));
				int count = zongFenDeviceMapper.getSameIpCount(zongFenDevice);
				if(count>0) {
					result.setResult(0);
					result.setMessage("已存在相同虚拟IP的设备");
					return result;
				}
				zongFenDevice.setCreateOper(zongFenDevice.getModifyOper());
				zongFenDevice.setCreateTime(zongFenDevice.getModifyTime());
				zongFenDeviceMapper.insert(zongFenDevice);
				key.setMessageName(zongFenDevice.getZongfenId().toString());
				for(ZongFenRel str:zongFenRellist) {
					str.setZongfenId(zongFenDevice.getZongfenId());
				}
				if(zongFenRellist.size()>0) {
					zongFenRelMapper.insertList(zongFenRellist);
				}
				if(zongFenDevice.getDeviceUsers().size()>0) {
					for(ZongFenDeviceUser user : zongFenDevice.getDeviceUsers()) {
						user.setZongfenId(zongFenDevice.getZongfenId());
					}
					zongFenDeviceUserMapper.insertList(zongFenDevice.getDeviceUsers());
				}
			} else {//同时新增多个
				List<Integer> idList = new ArrayList<Integer>();
				for(String strTemp:zongFenDevice.getRealIp()) {
					String[] queryStr = strTemp.split(":");
					ZongFenDevice queryDev = new ZongFenDevice();
					queryDev.setZongfenIp(queryStr[0]);
					queryDev.setZongfenPort(Integer.valueOf(queryStr[1]));
					queryDev.setIsVirtualIp(0);
					int sameDev = zongFenDeviceMapper.getSameIpCount(queryDev);
					if(sameDev>0) {
						result.setResult(0);
						result.setMessage("已存在IP和端口相同的设备");
						return result;
					}
				}
				for(String str:zongFenDevice.getRealIp()) {
					ZongFenDevice zongFenDevices = new ZongFenDevice();
					zongFenDevices.setModifyOper(zongFenDevice.getModifyOper());
					zongFenDevices.setModifyTime(zongFenDevice.getModifyTime());
					zongFenDevices.setCreateOper(zongFenDevice.getModifyOper());
					zongFenDevices.setCreateTime(zongFenDevice.getModifyTime());
					zongFenDevices.setZongfenFtpPort(zongFenDevice.getZongfenFtpPort());
					zongFenDevices.setZongfenName(zongFenDevice.getZongfenName());
					zongFenDevices.setIsVirtualIp(zongFenDevice.getIsVirtualIp());
					String[] tempStr = str.split(":");
					zongFenDevices.setZongfenIp(tempStr[0]);
					zongFenDevices.setZongfenPort(Integer.valueOf(tempStr[1]));
					zongFenDeviceMapper.insert(zongFenDevices);
					ZongFenRel zongFenRels = new ZongFenRel();
					zongFenRels.setIp(tempStr[0]);
					zongFenRels.setZongfenId(zongFenDevices.getZongfenId());
					zongFenRelMapper.insert(zongFenRels);
					if(zongFenDevice.getDeviceUsers().size()>0) {
						for(ZongFenDeviceUser user : zongFenDevice.getDeviceUsers()) {
							user.setZongfenId(zongFenDevices.getZongfenId());
						}
						zongFenDeviceUserMapper.insertList(zongFenDevice.getDeviceUsers());
					}
					idList.add(zongFenDevices.getZongfenId());
				}
				key.setMessageName(idList.toString());
			}
			key.setOperType(OperationType.CREATE.getType());
			keys.add(key);
		}else {//修改
			List<ZongFenRel> zongFenRellist = new ArrayList<ZongFenRel>();
			for(String str:zongFenDevice.getRealIp()) {
				String[] tempStr = str.split(":");
				ZongFenRel zongFenRel = new ZongFenRel();
				zongFenRel.setIp(tempStr[0]);
				zongFenRel.setZongfenId(zongFenDevice.getZongfenId());
				zongFenRellist.add(zongFenRel);
			}
			int existIp =  zongFenDeviceMapper.getExistIp(zongFenRellist);
			if(existIp>0) {
				result.setResult(0);
				result.setMessage("已存在相同物理IP的设备");
				return result;
			}
			zongFenDevice.setZongfenPort(Integer.valueOf(zongFenDevice.getRealIp().get(0).split(":")[1]));
			zongFenDeviceMapper.updateById(zongFenDevice);
			if(zongFenRellist.size()>0) {
				zongFenRelMapper.deleteByZfId(zongFenDevice.getZongfenId());
				zongFenRelMapper.insertList(zongFenRellist);
			}
			if(zongFenDevice.getDeviceUsers().size()>0) {
				zongFenDeviceUserMapper.deleteByZfId(zongFenDevice.getZongfenId());
				zongFenDeviceUserMapper.insertList(zongFenDevice.getDeviceUsers());
			}
			key.setOperType(OperationType.MODIFY.getType());
			keys.add(key);
		}
		//刷新缓存
		CommonCache commonCache =new CommonCache();
		commonCache.refreshCache();
		result.setKeys(keys);
		result.setResult(1);
		return result;
	}
	
	/**
	 * 
	* @Title: getRealZongFenIp
	* @Description: 获取物理IP地址列表
	* @param @return
	* @return List<Map<String,Object>>
	* @throws
	 */
	public List<Map<String,Object>> getRealZongFenIp(){
		List<Map<String,Object>> result =new ArrayList<Map<String,Object>>();
		Map<String,String> ZFdev = rediscluster.getHashs(RedisKeyConstant.ZF_GENERAL_INFORMATION);
		if(ZFdev!=null && !ZFdev.isEmpty()) {
			 Set<Map.Entry<String,String>> ZFSite = ZFdev.entrySet();
			 Iterator<Map.Entry<String,String>>  ZFiterator = ZFSite.iterator();
			 Gson json= new Gson();
			 while(ZFiterator.hasNext()) {
				 String zfIp = ZFiterator.next().getKey();
				 Map<String,Object> zfIpInfo= json.fromJson(ZFdev.get(zfIp),HashMap.class);
				 result.add(zfIpInfo);
			 }
		}
    	return result;
	}
	
	/**
	 * 
	* @Title: getZongFenData
	* @Description: 综分设备数据查询
	* @param @param deviceName
	* @param @return
	* @return List<ZongFenDevice>
	* @throws
	 */
	public List<ZongFenDevice> getZongFenData(String deviceName){
		List<ZongFenDevice> info = new ArrayList<ZongFenDevice>();
		//查redis策略下发服务器
		Map<String,String> zfdev = rediscluster.getHashs(RedisKeyConstant.ZF_GENERAL_INFORMATION);
		if(zfdev!=null && !zfdev.isEmpty()) {
			 Set<Map.Entry<String,String>> zfSite = zfdev.entrySet();
			 Iterator<Map.Entry<String,String>>  zfIterator = zfSite.iterator();
			 Gson json= new Gson();
			 DecimalFormat decimalFormat = new DecimalFormat("0");
			 while(zfIterator.hasNext()) {
				 String zfIp = zfIterator.next().getKey();
				 if(deviceName==null || "".equals(deviceName)) {
					 List<String> realIp = new ArrayList<String>();
					 ZongFenDevice zfInfo = new ZongFenDevice();
					 Map<String,Object> zfIpInfo= json.fromJson(zfdev.get(zfIp),HashMap.class);
					 zfInfo.setZongfenName(zfIpInfo.get("hostName").toString());
					 realIp.add(zfIpInfo.get("ip").toString());
					 zfInfo.setRealIp(realIp);
					 zfInfo.setZongfenPort(Integer.valueOf(decimalFormat.format(zfIpInfo.get("port"))));
					 zfInfo.setDeviceUser("服务器");
					 zfInfo.setDeviceType(1);
					 info.add(zfInfo);
				 } else {
					 List<String> realIp = new ArrayList<String>();
					 ZongFenDevice zfInfo = new ZongFenDevice();
					 Map<String,Object> zfIpInfo= json.fromJson(zfdev.get(zfIp),HashMap.class);
					 if(zfIpInfo.get("hostName").toString().indexOf(deviceName)>=0) {
						 zfInfo.setZongfenName(zfIpInfo.get("hostName").toString());
						 realIp.add(zfIpInfo.get("ip").toString());
						 zfInfo.setRealIp(realIp);
						 zfInfo.setZongfenPort(Integer.valueOf(decimalFormat.format(zfIpInfo.get("port"))));
						 zfInfo.setDeviceUser("服务器");
						 zfInfo.setDeviceType(1);
						 info.add(zfInfo);
					 }
				 }
			 }
		}
		//查数据库综分设备
		List<ZongFenDevice> infoDb = zongFenDeviceMapper.getListByName(deviceName);
		for(ZongFenDevice dev:infoDb) {
			dev.setDeviceType(0);
			//拼接物理IP栏
			List<String> ipList = zongFenRelMapper.selectListById(dev.getZongfenId());
			dev.setRealIp(ipList);
			if(dev.getIsVirtualIp()==0) {
				dev.setZongfenIp(null);
			}
			//拼接设备用途栏
			String useage = ""; 
			for(ZongFenDeviceUser devuser:dev.getDeviceUsers()) {
				String type = "";
				if(devuser.getPacketType().equals(9)) {
					type = "分类库下发设备";
				}else if(devuser.getPacketType().equals(3)) {
					if(devuser.getPacketSubType().equals(0)) {
						type = "HTTP GET数据接收";
					}else if(devuser.getPacketSubType().equals(1)) {
						type = "WLAN终端数据接收";
					}
				}else if(devuser.getPacketType().equals(2)){
					if(devuser.getPacketSubType().equals(0)) {
						type = "HTTP";
					}else if(devuser.getPacketSubType().equals(1)) {
						type = "RTSP";
					}else if(devuser.getPacketSubType().equals(2)) {
						type = "VoIP";
					}else if(devuser.getPacketSubType().equals(3)) {
						type = "FTP";
					}else if(devuser.getPacketSubType().equals(4)) {
						type = "SMTP";
					}else if(devuser.getPacketSubType().equals(5)) {
						type = "POP3";
					}else if(devuser.getPacketSubType().equals(6)) {
						type = "IMAP";
					}else if(devuser.getPacketSubType().equals(7)) {
						type = "DNS";
					}else if(devuser.getPacketSubType().equals(8)) {
						type = "P2P";
					}else if(devuser.getPacketSubType().equals(9)) {
						type = "Game";
					}else if(devuser.getPacketSubType().equals(10)) {
						type = "IM";
					}
				}
				if("".equals(useage)) {
					useage = type;
				}else {
					useage = useage + "," + type;
				}
			}
			dev.setDeviceUser(useage);
		}
		info.addAll(infoDb);
		return info;
	}
	
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
	public String deleteDevice(Integer deviceId,Integer flag,Integer devType,Integer dpiType,String ip) {
		if(flag==1) {//综分设备删除
			if(devType==0) {
				int count = zongFenRelMapper.selectCountByIp(ip);
				if(count>0) {
					return "请先删除关联的综分设备";
				}
				rediscluster.removeHash(RedisKeyConstant.ZF_GENERAL_INFORMATION, ip);
			}else{
				if(devType==1) {
					Map<String,String> query = new HashMap<String, String>();
					query.put("zongFenId",deviceId.toString());
					query.put("packageType",null);
					query.put("packageSubType",null);
					int count = zongFenDeviceMapper.selectIsUploadUsed(query);
					if(count>0) {
						return "请先删除与该设备关联的流量上报策略";
					}
				}
				zongFenDeviceMapper.deleteById(deviceId);
				zongFenRelMapper.deleteByZfId(deviceId);
				zongFenDeviceUserMapper.deleteByZfId(deviceId);
			}
		}else if(flag==2) {//DPI接收数据设备删除
			DpiDeviceInfoStrategy dpiInfo = dpiRecDeviceMapper.getInfoById(deviceId);
			if(dpiType==DevFlag.IDC.getValue()) {
				List<String> idc = dpiRecHouseMapper.getListByDpiId(dpiInfo.getDpiId());
				dpiInfo.setIdcHouseIds(idc);
			}
			deletePolicy(dpiInfo);
		}else if(flag==3) {//DPI数据上报设备删除
			DpiUploadDevice uploadDevice = dpiUploadDeviceMapper.getUplDpiInfoById(deviceId);
			long start = IPUtil.ipToLong(uploadDevice.getStartIp());
			long end = IPUtil.ipToLong(uploadDevice.getEndIp());
			for(;start<=end;start++) {
				rediscluster.removeHash(RedisKeyConstant.DPI_ATTRIBUTE_INFO, IPUtil.longToIP(start));
			}
			dpiUploadDeviceMapper.deleteByPrimaryKey(deviceId);
		}
		return null;
	}
	
	@Override
	protected boolean addCustomLogic(BaseVO arg0) {
		boolean success = addServerStatusToRedis(arg0);
		if(!success) {
	            logger.error("addServerStatusToRedis failed..."+arg0.objectToJson());
	            return false;
	        }
		
		success = addTaskAndChannelToRedis(arg0);
		if(!success) {
            logger.error("addTaskAndChannelToRedis failed..."+arg0.objectToJson());
            return false;
        }
		return true;
	}

	@Override
	protected boolean addDb(BaseVO arg0) {
		DpiDeviceInfoStrategy dpiDev = (DpiDeviceInfoStrategy)arg0;
		//站点组成需要从redis获取部署位置
		ChinaArea chinaArea = chinaAreaMapper.getAreaByCode((long)dpiDev.getAreaCode());
		//获取部署位置
		String deployMode = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.SYSTEM_DEPLOY_MODE);
		long messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.DEVICE_INFO_POLICY.getId());
		long messageSequenceNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.DEVICE_INFO_POLICY.getId());
		Policy policy = new Policy();
		policy.setMessageType(MessageType.DEVICE_INFO_POLICY.getId());
		policy.setMessageNo(messageNo);
        policy.setMessageName(dpiDev.getDpiDevName());
        policy.setOperateType(OperationConstants.OPERATION_SAVE);
        policy.setCreateOper(SpringUtil.getSysUserName());
        policy.setCreateTime(new Date());
        policy.setMessageSequenceno(messageSequenceNo);
        policy.setModifyOper(SpringUtil.getSysUserName());
        policy.setModifyTime(policy.getCreateTime());
        policyMapper.insert(policy);
		dpiDev.setMessageNo(messageNo);
		dpiDev.setMessageSequenceNo(messageSequenceNo);
		dpiDev.setMessageType(MessageType.DEVICE_INFO_POLICY.getId());
		if(dpiDev.getProbeType()==DevFlag.IDC.getValue()) {
			String areaName = chinaArea.getAreaShort();
			if(!chinaArea.getAreaCode().equals(chinaArea.getParent())) {
				ChinaArea parentArea = chinaAreaMapper.getAreaByCode(chinaArea.getParent());
				areaName = parentArea.getAreaShort();
			}
			if(dpiDev.getIdchouses().length>1) {
				dpiDev.setIdcHouseId("Nonhouse_id");
				dpiDev.setDpiSiteName("I-"+areaName+"-Nonhouse_id");
			}else {
				dpiDev.setIdcHouseId(dpiDev.getIdchouses()[0].toString());
				dpiDev.setDpiSiteName("I-"+areaName+"-"+dpiDev.getIdchouses()[0].toString());
			}
			dpiDev.setIdcHouseIds(Arrays.asList(dpiDev. getIdchouses()));
		}else {
			dpiDev.setIdcHouseId("Nonhouse_id");
			if(deployMode.equals("0")) {
				dpiDev.setDpiSiteName("P-"+chinaArea.getAreaShort());
			}else{
				dpiDev.setDpiSiteName("M-"+chinaArea.getAreaShort());
			}
		}
		dpiRecDeviceMapper.insert(dpiDev);
		if(dpiDev.getProbeType()==DevFlag.IDC.getValue()) {
			List<DpiRecHouse> idcList = new ArrayList<DpiRecHouse>();
			for(String temp:dpiDev.getIdchouses()) {
				DpiRecHouse house = new DpiRecHouse();
				house.setDpiId(dpiDev.getDpiId());
				house.setHouseId(temp);
				idcList.add(house);
			}
			if(idcList.size()>0) {
				dpiRecHouseMapper.insertList(idcList);
			}
		}
		if(dpiDev.getProbeType()==0) {
			List<String> idcs = new ArrayList<String>();
			dpiDev.setIdcHouseIds(idcs);
		}
		return true;
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO arg0) {
		boolean success = deleteServerStatusToRedis(arg0);
		if(!success) {
	            logger.error("addServerStatusToRedis failed..."+arg0.objectToJson());
	            return false;
	        }
		
		success = addTaskAndChannelToRedis(arg0);
		if(!success) {
            logger.error("addTaskAndChannelToRedis failed..."+arg0.objectToJson());
            return false;
        }
		return true;
	}

	@Override
	protected boolean deleteDb(BaseVO arg0) {
		DpiDeviceInfoStrategy dpiDev = (DpiDeviceInfoStrategy)arg0;
		long messageSequenceNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.DEVICE_INFO_POLICY.getId());
		Policy policy = new Policy();
		policy.setMessageType(MessageType.DEVICE_INFO_POLICY.getId());
		policy.setMessageNo(dpiDev.getMessageNo());
		policy.setMessageSequenceno(messageSequenceNo);
		policy.setOperateType(OperationType.DELETE.getType());
        policyMapper.updatePolicyByMessageNoAndType(policy);
		dpiDev.setMessageSequenceNo(messageSequenceNo);
		dpiDev.setMessageType(MessageType.DEVICE_INFO_POLICY.getId());
		dpiDev.setOperationType(OperationConstants.OPERATION_DELETE);
		if(dpiDev.getProbeType()==0) {
			List<String> idcs = new ArrayList<String>();
			dpiDev.setIdcHouseIds(idcs);
		}
		return true;
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO arg0) {
		boolean success = modifyServerStatusToRedis(arg0);
		if(!success) {
            logger.error("addServerStatusToRedis failed..."+arg0.objectToJson());
            return false;
	    }
		
		success = addTaskAndChannelToRedis(arg0);
		if(!success) {
            logger.error("addTaskAndChannelToRedis failed..."+arg0.objectToJson());
            return false;
        }
		return true;
	}

	@Override
	protected boolean modifyDb(BaseVO arg0) {
		DpiDeviceInfoStrategy dpiDev = (DpiDeviceInfoStrategy)arg0;
		//站点组成需要从redis获取部署位置
		ChinaArea chinaArea = chinaAreaMapper.getAreaByCode((long)dpiDev.getAreaCode());
		//获取部署位置
		String deployMode = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.SYSTEM_DEPLOY_MODE);
		long messageSequenceNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.DEVICE_INFO_POLICY.getId());
		Policy policy = new Policy();
		policy.setMessageType(MessageType.DEVICE_INFO_POLICY.getId());
		policy.setMessageNo(dpiDev.getMessageNo());
        policy.setMessageName(dpiDev.getDpiDevName());
        policy.setOperateType(OperationConstants.OPERATION_UPDATE);
        policy.setMessageSequenceno(messageSequenceNo);
        policy.setModifyOper(SpringUtil.getSysUserName());
        policy.setModifyTime(new Date());
    	policyMapper.updatePolicyByMessageNoAndType(policy);
		dpiDev.setMessageSequenceNo(messageSequenceNo);
		if(dpiDev.getProbeType()==DevFlag.IDC.getValue()) {
			String areaName = chinaArea.getAreaShort();
			if(!chinaArea.getAreaCode().equals(chinaArea.getParent())) {
				ChinaArea parentArea = chinaAreaMapper.getAreaByCode(chinaArea.getParent());
				areaName = parentArea.getAreaShort();
			}
			if(dpiDev.getIdchouses().length>1) {
				dpiDev.setIdcHouseId("Nonhouse_id");
				dpiDev.setDpiSiteName("I-"+areaName+"-Nonhouse_id");
			}else {
				dpiDev.setIdcHouseId(dpiDev.getIdchouses()[0].toString());
				dpiDev.setDpiSiteName("I-"+areaName+"-"+dpiDev.getIdchouses()[0].toString());
			}
			dpiDev.setIdcHouseIds(Arrays.asList(dpiDev. getIdchouses()));
		}else {
			dpiDev.setIdcHouseId("Nonhouse_id");
			if(deployMode.equals("0")) {
				dpiDev.setDpiSiteName("P"+"-"+chinaArea.getAreaShort());
			}else{
				dpiDev.setDpiSiteName("M"+"-"+chinaArea.getAreaShort());
			}
		}
		dpiRecDeviceMapper.updateByPrimaryKeySelective(dpiDev);
		if(dpiDev.getProbeType()==DevFlag.IDC.getValue()) {
			List<DpiRecHouse> idcList = new ArrayList<DpiRecHouse>();
			for(String temp:dpiDev.getIdchouses()) {
				DpiRecHouse house = new DpiRecHouse();
				house.setDpiId(dpiDev.getDpiId());
				house.setHouseId(temp);
				idcList.add(house);
			}
			if(idcList.size()>0) {
				dpiRecHouseMapper.deleteByDpiId(dpiDev.getDpiId());
				dpiRecHouseMapper.insertList(idcList);
			}
		}
		if(dpiDev.getProbeType()==0) {
			List<String> idcs = new ArrayList<String>();
			dpiDev.setIdcHouseIds(idcs);
		}
		return true;
	}
	
	
	private boolean addServerStatusToRedis(BaseVO arg0) {
        try{
        	DpiDeviceInfoStrategy dpiV1CfgDpiinfo = (DpiDeviceInfoStrategy) arg0;
            Map<String,Long> info = Maps.newHashMap();

            info.put("status",0L);
            info.put("lastUpdateTime",System.currentTimeMillis()/1000);
            Gson json = new Gson();
            if (dpiV1CfgDpiinfo.getDpiPort()!=null&&dpiV1CfgDpiinfo.getDpiPort()!=0){
            	rediscluster.putHash(RedisKeyConstant.REDIS_KEY_DPI_SERVER_STATUS,dpiV1CfgDpiinfo.getDpiIp(),json.toJson(info));
            }

            rediscluster.putHash(RedisKeyConstant.REDIS_KEY_DPI_SERVER_STATUS,dpiV1CfgDpiinfo.getDpiIp(), json.toJson(info));
        } catch (Exception e) {
            logger.error(this.getClass().getName()+" addServerStatusToRedis error,Param[policy=]"+arg0.objectToJson(),e);
            return false;
        }
        return true;
    }
	
    private boolean modifyServerStatusToRedis(BaseVO arg0) {
        try{
        	DpiDeviceInfoStrategy dpiV1CfgDpiinfo = (DpiDeviceInfoStrategy) arg0;
            Map<String,Long> info = Maps.newHashMap();
            Gson json = new Gson();
            info.put("status",0L);
            info.put("lastUpdateTime",System.currentTimeMillis()/1000);
            if (dpiV1CfgDpiinfo.getDpiPort()==null||dpiV1CfgDpiinfo.getDpiPort()==0){
            	rediscluster.removeHash("ServerStatus_0_193",dpiV1CfgDpiinfo.getDpiIp());
            } else {
            	rediscluster.putHash("ServerStatus_0_193",dpiV1CfgDpiinfo.getDpiIp(),json.toJson(info));
            }
            rediscluster.putHash("ServerStatus_1_193",dpiV1CfgDpiinfo.getDpiIp(), json.toJson(info));
        } catch (Exception e) {
            logger.error(this.getClass().getName()+" modifyServerStatusToRedis error,Param[policy=]"+arg0.objectToJson(),e);
            return false;
        }

        return true;
    }

    private boolean deleteServerStatusToRedis(BaseVO arg0) {
        try{
        	DpiDeviceInfoStrategy dpiV1CfgDpiinfo = (DpiDeviceInfoStrategy) arg0;
            rediscluster.removeHash("ServerStatus_0_193",dpiV1CfgDpiinfo.getDpiIp());
            rediscluster.removeHash("ServerStatus_1_193",dpiV1CfgDpiinfo.getDpiIp());
        } catch (Exception e) {
            logger.error(this.getClass().getName()+" deleteServerStatusToRedis error,Param[policy=]"+arg0.objectToJson(),e);
            return false;
        }
        return true;
    }

	@Override
	public Integer checkDeviceUseage(Integer zongFenId,String type) {
		Map<String,String> query = new HashMap<String, String>();
		String[] types = type.split("_");
		query.put("zongFenId",zongFenId.toString());
		query.put("packageType",types[0]);
		query.put("packageSubType",types[1]);
		return zongFenDeviceMapper.selectIsUploadUsed(query);
	}
	
	
	public GraphDto getDeviceGraph() {
		//根节点构建，为了可以自动排版
		GraphDto result =  new GraphDto();
		result.setDeviceFlag(1);
		result.setDeviceName("root");
		result.setId(1);
		List<GraphDto> zfGraphDto = new ArrayList<GraphDto>();
		Map<String,String> zfdev = rediscluster.getHashs(RedisKeyConstant.ZF_GENERAL_INFORMATION);
		//统计redis中policy设备
		if(zfdev!=null && !zfdev.isEmpty()) {
			Set<Map.Entry<String,String>> zfSite = zfdev.entrySet();
			Iterator<Map.Entry<String,String>>  zfIterator = zfSite.iterator();
			Gson json= new Gson();
			while(zfIterator.hasNext()) {
				String zfIp = zfIterator.next().getKey();
				Map<String,Object> zfIpInfo= json.fromJson(zfdev.get(zfIp),HashMap.class);
				GraphDto zfDto = new GraphDto();
				zfDto.setDeviceName(zfIpInfo.get("hostName").toString());
				zfDto.setIp(zfIpInfo.get("ip").toString());
				zfDto.setDeviceFlag(0);
				zfGraphDto.add(zfDto);
			 }
		}
		//查数据库综分设备
		List<ZongFenDevice> infoDb = zongFenDeviceMapper.getListByName(null);
		boolean existVirtual = false;
		//判断是否存在虚拟IP地址的设备
		for(ZongFenDevice dbtmp:infoDb) {
			if(dbtmp.getIsVirtualIp().equals(1)) {
				existVirtual = true;
				break;
			}
		}
		if(existVirtual) {
			result.setExistVirtual(1);
			List<String> existDevice = new ArrayList<String>();
			//虚拟设备数据构建
			for(ZongFenDevice zftmp:infoDb) {
				if(zftmp.getIsVirtualIp().equals(1)) {
					GraphDto zfDto = new GraphDto();
					zfDto.setDeviceName(zftmp.getZongfenName());
					zfDto.setIp(zftmp.getZongfenIp());
					zfDto.setDeviceFlag(0);
					zfDto.setConnectFlag(1);
					List<String> ipList = zongFenRelMapper.selectListById(zftmp.getZongfenId());
					if(zfGraphDto.size()>0 && ipList.size()>0) {
						for(GraphDto zfdto:zfGraphDto) {
							if(ipList.contains(zfdto.getIp())) {
								if(!existDevice.contains(zfdto.getIp())) {
									existDevice.add(zfdto.getIp());
								}
								GraphDto zfDto1 = new GraphDto();
								zfDto1.setIp(zfdto.getIp());
								zfDto1.setDeviceFlag(0);
								zfDto1.setConnectFlag(0);
								if(zfDto.getChildren()==null) {
									List<GraphDto> newList = new ArrayList<GraphDto>();
									newList.add(zfDto1);
									zfDto.setChildren(newList);
								}else {
									zfDto.getChildren().add(zfDto1); 
								}
							}
						}
					}
					if(result.getChildren()==null) {
						List<GraphDto> newList = new ArrayList<GraphDto>();
						newList.add(zfDto);
						result.setChildren(newList);
					}else {
						result.getChildren().add(zfDto); 
					}
				}
			}
			//没有虚拟ip的策略下发服务器数据构建
			if(zfGraphDto.size()>0) {
				GraphDto tmpDev =  new GraphDto();
				tmpDev.setDeviceFlag(1);
				tmpDev.setConnectFlag(1);
				for(GraphDto tmp1:zfGraphDto) {
					if(!existDevice.contains(tmp1.getIp())) {
						GraphDto tmpDev1 =  new GraphDto();
						tmpDev1.setDeviceFlag(0);
						tmpDev1.setConnectFlag(1);
						tmpDev1.setIp(tmp1.getIp());
						if(tmpDev.getChildren()==null) {
							List<GraphDto> newList = new ArrayList<GraphDto>();
							newList.add(tmpDev1);
							tmpDev.setChildren(newList);
						}else {
							tmpDev.getChildren().add(tmpDev1); 
						}
					}
				}
				if(result.getChildren()==null) {
					List<GraphDto> newList = new ArrayList<GraphDto>();
					newList.add(tmpDev);
					result.setChildren(newList);
				}else {
					result.getChildren().add(tmpDev); 
				}
			}
			//关联DPI信息接收设备数据构建
			List<String> dpiList = new ArrayList<String>();
			List<String> dpiconList = new ArrayList<String>();
			List<DpiDeviceInfoStrategy> info = dpiRecDeviceMapper.getInfoByName(null);
			List<String> dbIps = new ArrayList<String>();
			for(DpiDeviceInfoStrategy dbip:info) {
				dbIps.add(dbip.getDpiIp());
			}
			if(result.getChildren()!=null) {
				for(GraphDto temp2:result.getChildren()) {
					if(temp2.getChildren()!=null) {
						for(GraphDto temp3:temp2.getChildren()) {
							//综分设备与DPI设备关联信息
							Map<String,String> ZFDpi = rediscluster.getHashs("ZF_DPI_Relation_"+temp3.getIp());
							if(ZFDpi!=null && !ZFDpi.isEmpty()) {
								Set<Map.Entry<String,String>> ZFSite = ZFDpi.entrySet();
								Iterator<Map.Entry<String,String>>  ZFiterator = ZFSite.iterator();
								while (ZFiterator.hasNext()) {
									String key = ZFiterator.next().getKey();
									String ip = key.substring(0,key.indexOf("|"));
									if(dbIps.contains(ip)) {
										GraphDto newgraph = new GraphDto();
										if(!dpiList.contains(ip)) {
											dpiList.add(ip);
										}
										newgraph.setDeviceFlag(0);
										newgraph.setIp(ip);
										if("0".equals(ZFDpi.get(key))) {
											if(!dpiconList.contains(ip)) {
												dpiconList.add(ip);
											}
											newgraph.setConnectFlag(0);
										}else {
											newgraph.setConnectFlag(1);
										}
										if(temp3.getChildren()==null) {
											List<GraphDto> newList = new ArrayList<GraphDto>();
											newList.add(newgraph);
											temp3.setChildren(newList);
										}else {
											temp3.getChildren().add(newgraph); 
										}
									}
									
								}
							 }
						}
					}
				}
			}
			//独立的DPI信息接收设备数据构建
			GraphDto vir2 = new GraphDto();
			vir2.setDeviceFlag(1);
			for(DpiDeviceInfoStrategy temp4:info) {
				if(!dpiList.contains(temp4.getDpiIp())) {
					GraphDto vir3 = new GraphDto();
					vir3.setDeviceFlag(0);
					vir3.setConnectFlag(1);
					vir3.setIp(temp4.getDpiIp());
					if(vir2.getChildren()==null) {
						List<GraphDto> newList = new ArrayList<GraphDto>();
						newList.add(vir3);
						vir2.setChildren(newList);
					}else {
						vir2.getChildren().add(vir3); 
					}
				}
			}
			GraphDto vir1 = new GraphDto();
			vir1.setDeviceFlag(1);
			if(vir1.getChildren()==null) {
				List<GraphDto> newList = new ArrayList<GraphDto>();
				newList.add(vir2);
				vir1.setChildren(newList);
			}else {
				vir1.getChildren().add(vir2); 
			}
			if(result.getChildren()==null) {
				List<GraphDto> newList = new ArrayList<GraphDto>();
				newList.add(vir1);
				result.setChildren(newList);
			}else {
				result.getChildren().add(vir1); 
			}
		}else {
			result.setExistVirtual(0);
			//没有虚拟ip的策略下发服务器数据构建
			if(zfGraphDto.size()>0) {
				for(GraphDto tmp1:zfGraphDto) {
					GraphDto tmpDev1 =  new GraphDto();
					tmpDev1.setDeviceFlag(0);
					tmpDev1.setConnectFlag(1);
					tmpDev1.setIp(tmp1.getIp());
					if(result.getChildren()==null) {
						List<GraphDto> newList = new ArrayList<GraphDto>();
						newList.add(tmpDev1);
						result.setChildren(newList);
					}else {
						result.getChildren().add(tmpDev1); 
					}
				}
			}
			//关联DPI信息接收设备数据构建
			List<String> dpiList = new ArrayList<String>();
			List<String> dpiconList = new ArrayList<String>();
			List<DpiDeviceInfoStrategy> info = dpiRecDeviceMapper.getInfoByName(null);
			List<String> dbIps = new ArrayList<String>();
			for(DpiDeviceInfoStrategy dbip:info) {
				dbIps.add(dbip.getDpiIp());
			}
			if(result.getChildren()!=null) {
				for(GraphDto temp3:result.getChildren()) {
					//综分设备与DPI设备关联信息
					Map<String,String> ZFDpi = rediscluster.getHashs("ZF_DPI_Relation_"+temp3.getIp());
					if(ZFDpi!=null && !ZFDpi.isEmpty()) {
						Set<Map.Entry<String,String>> ZFSite = ZFDpi.entrySet();
						Iterator<Map.Entry<String,String>>  ZFiterator = ZFSite.iterator();
						while (ZFiterator.hasNext()) {
							String key = ZFiterator.next().getKey();
							String ip = key.substring(0,key.indexOf("|"));
							if(dbIps.contains(ip)) {
								GraphDto newgraph = new GraphDto();
								if(!dpiList.contains(ip)) {
									dpiList.add(ip);
								}
								newgraph.setDeviceFlag(0);
								newgraph.setIp(ip);
								if("0".equals(ZFDpi.get(key))) {
									if(!dpiconList.contains(ip)) {
										dpiconList.add(ip);
									}
									newgraph.setConnectFlag(0);
								}else {
									newgraph.setConnectFlag(1);
								}
								if(temp3.getChildren()==null) {
									List<GraphDto> newList = new ArrayList<GraphDto>();
									newList.add(newgraph);
									temp3.setChildren(newList);
								}else {
									temp3.getChildren().add(newgraph); 
								}
							}
						}
					 }
				}
			}
			//独立的DPI信息接收设备数据构建
			GraphDto vir2 = new GraphDto();
			vir2.setDeviceFlag(1);
			for(DpiDeviceInfoStrategy temp4:info) {
				if(!dpiList.contains(temp4.getDpiIp())) {
					GraphDto vir3 = new GraphDto();
					vir3.setDeviceFlag(0);
					vir3.setConnectFlag(1);
					vir3.setIp(temp4.getDpiIp());
					if(vir2.getChildren()==null) {
						List<GraphDto> newList = new ArrayList<GraphDto>();
						newList.add(vir3);
						vir2.setChildren(newList);
					}else {
						vir2.getChildren().add(vir3); 
					}
				}
			}
			if(result.getChildren()==null) {
				List<GraphDto> newList = new ArrayList<GraphDto>();
				newList.add(vir2);
				result.setChildren(newList);
			}else {
				result.getChildren().add(vir2); 
			}
		}
		return result;
	}

	@Override
	public String getIsAuto() {
		return LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.CENTER_POLICY_ENABLEZOOKEEPER);
	}
	
	
	public void deleteRelative(String ip,Integer port) {
		Map<String,String> ZFdev = rediscluster.getHashs(RedisKeyConstant.ZF_GENERAL_INFORMATION);
		if(ZFdev!=null && !ZFdev.isEmpty()) {
			Set<Map.Entry<String,String>> ZFSite = ZFdev.entrySet();
			Iterator<Map.Entry<String,String>>  ZFiterator = ZFSite.iterator();
			while(ZFiterator.hasNext()) {
				String zfIp = ZFiterator.next().getKey();
				//综分设备与DPI设备关联信息
				Map<String,String> ZFDpi = rediscluster.getHashs("ZF_DPI_Relation_"+zfIp);
				if(ZFDpi!=null && !ZFDpi.isEmpty()) {
					if(ZFDpi.containsKey(ip+"|"+port.toString())) {
						rediscluster.removeHash("ZF_DPI_Relation_"+zfIp, ip+"|"+port.toString());
					}
				}
			}
		}
	}
}

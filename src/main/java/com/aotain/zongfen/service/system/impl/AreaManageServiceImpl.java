package com.aotain.zongfen.service.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.common.config.constant.ConfigRedisConstant;
import com.aotain.common.config.redis.BaseRedisService;
import com.aotain.common.utils.constant.RedisKeyConstant;
import com.aotain.zongfen.mapper.common.ChinaAreaMapper;
import com.aotain.zongfen.mapper.general.IpAddressAreaMapper;
import com.aotain.zongfen.mapper.system.SystemParameterMapper;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ChinaArea;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.system.SystemParameter;
import com.aotain.zongfen.service.system.AreaManageService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class AreaManageServiceImpl implements AreaManageService{
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AreaManageServiceImpl.class);
	
	@Autowired
	private ChinaAreaMapper chinaAreaMapper;

    @Autowired
    private BaseRedisService<String,String,String> baseRedisService;
    
    @Autowired
    private IpAddressAreaMapper ipAddressAreaMapper;
    
    @Autowired
    private SystemParameterMapper systemParameterMapper;
    
	@Override
	public PageResult<ChinaArea> getAreaManageData(Integer pageSize, Integer pageIndex, String searchName) {
		Map<String,Object> query = new HashMap<String, Object>();
		String provinceCode = baseRedisService.getHash(ConfigRedisConstant.SYSTEM_CONFIG_DICT, RedisKeyConstant.PROVINCE_AREA);
		if(provinceCode==null || "".equals(provinceCode)) {
			return null;
		}
		query.put("areaName", searchName);
		query.put("provinceCode", provinceCode);
		PageResult<ChinaArea> result = new PageResult<ChinaArea>();
		PageHelper.startPage(pageIndex, pageSize);
		List<ChinaArea> info = chinaAreaMapper.getIndexList(query);
		PageInfo<ChinaArea> pageResult = new PageInfo<ChinaArea>(info);
		result.setTotal(pageResult.getTotal());
		result.setRows(info);
		return result;
	}

	@Override
	public ResponseResult<BaseKeys> areaSave(List<ChinaArea> areaList) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		String provinceCode = baseRedisService.getHash(ConfigRedisConstant.SYSTEM_CONFIG_DICT, RedisKeyConstant.PROVINCE_AREA);
		if(provinceCode==null || "".equals(provinceCode)) {
			result.setMessage("请先设置省份");
			result.setResult(0);
			return result;
		}
		int count = chinaAreaMapper.isDuplicateArea(areaList);
		if(count>0) {
			result.setMessage("已存在相同的区域");
			result.setResult(0);
			return result;
		}
		for(ChinaArea areaTmp:areaList) {
			if(areaTmp.getAreaCode().equals(Long.valueOf(provinceCode))) {
				areaTmp.setAreaType("省，直辖市");
			}else {
				areaTmp.setAreaType("市，地区（州、盟）");
			}
			areaTmp.setParent(Long.valueOf(provinceCode));
		}
		chinaAreaMapper.insertList(areaList);
		result.setResult(1);
		keys.add(key);
		result.setKeys(keys);
		return result;
	}

	@Override
	public ResponseResult<BaseKeys> areaUpdate(ChinaArea area) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		int count = chinaAreaMapper.isSameArea(area);
		if(count>0) {
			result.setMessage("已存在相同名称的区域");
			result.setResult(0);
			return result;
		}
		key.setId(area.getAreaCode());
		chinaAreaMapper.update(area);
		result.setResult(1);
		keys.add(key);
		result.setKeys(keys);
		return result;
	}

	@Override
	public String haveSetProvince() {
		return baseRedisService.getHash(ConfigRedisConstant.SYSTEM_CONFIG_DICT, RedisKeyConstant.PROVINCE_AREA);
	}

	@Override
	public String delete(List<Integer> ids) {
		String message = null;
		try {
			chinaAreaMapper.deleteByIds(ids);
		} catch (Exception e) {
			message = "删除失败";
			logger.error("delete fail ids :"+ids);
		}
		
		return message;
	}

	@Override
	public Map<String, Object> getProvinceList() {
		String province = baseRedisService.getHash(ConfigRedisConstant.SYSTEM_CONFIG_DICT, RedisKeyConstant.PROVINCE_AREA);
		Map<String,Object> result = new HashMap<String, Object>();
		List<ChinaArea> list = chinaAreaMapper.getProvinceList();
		result.put("provinceList",list);
		result.put("province", province);
		return result;
	}

	@Override
	public ResponseResult<BaseKeys> setProvince(String province,String areaName,String areaShort) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		/*List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();*/
		baseRedisService.putHash(ConfigRedisConstant.SYSTEM_CONFIG_DICT, RedisKeyConstant.PROVINCE_AREA,province);
		baseRedisService.putHash(ConfigRedisConstant.SYSTEM_CONFIG_DICT, RedisKeyConstant.SYSTEM_DEPLOY_PROVINCE_SHORTNAME,areaShort);
		String ipAreaCode = ipAddressAreaMapper.selectByName(areaName);
		if(ipAreaCode!=null) {
			//流量流向省份设置
			SystemParameter systemParameter1 = new SystemParameter();
			systemParameter1.setConfigKey(RedisKeyConstant.PROVINCE_IPAREA);
			systemParameter1.setConfigValue(ipAreaCode);
			baseRedisService.putHash(ConfigRedisConstant.SYSTEM_CONFIG_DICT, RedisKeyConstant.PROVINCE_IPAREA,province);
			systemParameterMapper.updateByConfigKey(systemParameter1);
		}
		//行政
		SystemParameter systemParameter = new SystemParameter();
		systemParameter.setConfigKey(RedisKeyConstant.PROVINCE_AREA);
		systemParameter.setConfigValue(province);
		systemParameterMapper.updateByConfigKey(systemParameter);
		//省份简称
		SystemParameter systemParameter2 = new SystemParameter();
		systemParameter2.setConfigKey(RedisKeyConstant.SYSTEM_DEPLOY_PROVINCE_SHORTNAME);
		systemParameter2.setConfigValue(areaShort);
		systemParameterMapper.updateByConfigKey(systemParameter2);
		
		result.setResult(1);
	/*	keys.add(key);
		result.setKeys(keys);*/
		return result;
	}
	
}

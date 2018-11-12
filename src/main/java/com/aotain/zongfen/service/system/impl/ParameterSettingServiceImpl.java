package com.aotain.zongfen.service.system.impl;

import com.aotain.common.config.constant.ConfigRedisConstant;
import com.aotain.common.config.redis.BaseRedisService;
import com.aotain.zongfen.mapper.system.SystemParameterMapper;
import com.aotain.zongfen.model.system.SystemParameter;
import com.aotain.zongfen.service.system.IParameterSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/02/26
 */
@Service
public class ParameterSettingServiceImpl implements IParameterSettingService{

    private static Logger logger = LoggerFactory.getLogger(ParameterSettingServiceImpl.class);

    @Autowired
    private SystemParameterMapper systemParameterMapper;

    @Autowired
    private BaseRedisService<String,String,String> baseRedisService;

    /**
     * 根据条件查询所有的系统参数
     * @param systemParameter
     *      根据systemParameter.configValue   systemParameter.configDesc进行模糊查询
     * @return
     */
    @Override
    public List<SystemParameter> listSystemParameter(SystemParameter systemParameter){
        return systemParameterMapper.listSystemParameter(systemParameter);
    }

    /**
     * 批量删除数据
     * @param configIds
     * @return
     */
    @Override
    public int batchDelete(List<Integer> configIds){
        try{
            for (int i=0;i<configIds.size();i++){
                SystemParameter result = systemParameterMapper.selectByConfigId(configIds.get(i));
                if (result!=null){
                    baseRedisService.removeHash(ConfigRedisConstant.SYSTEM_CONFIG_DICT,result.getConfigKey());
                }
            }
        } catch (Exception e){
            logger.error("delete data from redis error",e);
        }
        return systemParameterMapper.batchDelete(configIds);
    }

    @Override
    public boolean existRecord(SystemParameter systemParameter){
        boolean exist = false;
        SystemParameter result = systemParameterMapper.selectByConfigKey(systemParameter.getConfigKey());
        if ( result!=null ){
            exist = true;
        } else {
            exist = false;
        }
        return exist;
    }

    @Override
    public void addOrUpdate(SystemParameter systemParameter){
        boolean exist = existRecord(systemParameter);
        if ( exist ){
            systemParameterMapper.updateByConfigKey(systemParameter);
        } else {
            systemParameterMapper.insertSelective(systemParameter);
        }
        try{
            baseRedisService.putHash(ConfigRedisConstant.SYSTEM_CONFIG_DICT,
                    systemParameter.getConfigKey(),systemParameter.getConfigValue());
        } catch (Exception e){
            logger.error("add data to redis error",e);
        }

    }

    @Override
    public boolean existSameNameRecord(SystemParameter systemParameter){
        SystemParameter result = systemParameterMapper.selectByConfigDesc(systemParameter);
        return result==null?false:true;
    }

    @Override
    public SystemParameter selectByConfigKey(String configKey){
        return systemParameterMapper.selectByConfigKey(configKey);
    }
}

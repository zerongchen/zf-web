package com.aotain.zongfen.utils;

import com.aotain.common.config.ContextUtil;
import com.aotain.zongfen.service.common.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存工具类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/19
 */
public class CacheUtil {


    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheUtil.class);

    /**
     * 根据webType获取对应的名称
     * @param webType
     * @return
     */
    public static String getWebTypeName(int webType){
        CacheService cacheService = ContextUtil.getContext().getBean(CacheService.class);
        return cacheService.getWebTypeName(webType);
    }

    /**
     * 根据用户组号获取用户组名称
     * @param groupNo
     * @return
     */
    public static String getGroupNameByNo(long groupNo){
        CacheService cacheService = ContextUtil.getContext().getBean(CacheService.class);
        return cacheService.getUserGroupName(groupNo);
    }
}

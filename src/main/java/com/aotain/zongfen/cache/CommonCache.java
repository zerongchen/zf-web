package com.aotain.zongfen.cache;

import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.service.common.CacheService;
import com.google.common.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <P>缓存</P>
 * @author Chenzr
 * @since 2018/02/02
 */

@Component
public class CommonCache {

    private static Logger logger = LoggerFactory.getLogger(CommonCache.class);

    @Autowired
    private CacheService cacheService;


    private static LoadingCache<Long, String> userGroupNameCache = null;

    private static LoadingCache<Integer, ZongFenDevice> zongFenDevInfoCache = null;

    private static LoadingCache<Map<String,Integer>, String> appNameCache = null;

    private static LoadingCache<Integer, String> appTypeNameCache = null;

    /**
     * 刷新缓存
     */
    public void refreshCache(){
        this.getUserGroupNameCache().invalidateAll();
        this.getZongFenDevInfoCache().invalidateAll();
        logger.info("refresh cache success!");
    }

    /**
     * 刷新缓存
     */
    public void refreshAppCache(){
        this.getAppTypeNameCache().invalidateAll();
        logger.info("refresh cache success!");
    }


    /**
     * 查看缓存统计信息
     */
    public void statCache(){
        logger.info("cache stats:");
        logger.info(this.getUserGroupNameCache().stats().toString());
        logger.info(this.getZongFenDevInfoCache().stats().toString());

    }

    public LoadingCache<Long,String> getUserGroupNameCache(){
        if(userGroupNameCache == null){
            userGroupNameCache = CacheBuilder
                    .newBuilder().maximumSize(1000)
                    .expireAfterWrite(12, TimeUnit.HOURS).recordStats()
                    .refreshAfterWrite(12, TimeUnit.HOURS)
                    .removalListener(new RemovalListener<Object, Object>() {
                        @Override
                        public void onRemoval(RemovalNotification<Object, Object> notification) {
                            logger.info(notification.getKey() + " was removed, cause is " + notification.getCause());
                        }
                    }).build(
                            new CacheLoader<Long, String>() {
                        @Override
                        public String load( Long key) {
                            return cacheService.getUserGroupName(key);
                        }
                    });
        }
        return userGroupNameCache;
    }

    public LoadingCache<Integer, ZongFenDevice> getZongFenDevInfoCache(){
        if(zongFenDevInfoCache == null){
            zongFenDevInfoCache = CacheBuilder
                    .newBuilder().maximumSize(1000)
                    .expireAfterWrite(12, TimeUnit.HOURS).recordStats()
                    .refreshAfterWrite(12, TimeUnit.HOURS)
                    .removalListener(new RemovalListener<Object, Object>() {
                        @Override
                        public void onRemoval(RemovalNotification<Object, Object> notification) {
                            logger.info(notification.getKey() + " was removed, cause is " + notification.getCause());
                        }
                    }).build(
                            new CacheLoader<Integer, ZongFenDevice>() {
                                @Override
                                public ZongFenDevice load( Integer key) {
                                    return cacheService.getZongfenDev(key);
                                }
                            });
        }
        return zongFenDevInfoCache;
    }

    public LoadingCache<Map<String,Integer>, String> getAppNameCache(){
        if(appNameCache == null){
            appNameCache = CacheBuilder
                    .newBuilder().maximumSize(1000)
                    .expireAfterWrite(12, TimeUnit.HOURS).recordStats()
                    .refreshAfterWrite(12, TimeUnit.HOURS)
                    .removalListener(new RemovalListener<Object, Object>() {
                        @Override
                        public void onRemoval(RemovalNotification<Object, Object> notification) {
                            logger.info(notification.getKey() + " was removed, cause is " + notification.getCause());
                        }
                    }).build(
                            new CacheLoader<Map<String,Integer>, String>() {
                                @Override
                                public String load( Map<String,Integer> key) {
                                    return cacheService.getAppName(key);
                                }
                            });
        }
        return appNameCache;
    }

    public LoadingCache<Integer, String> getAppTypeNameCache(){
        if(appTypeNameCache == null){
            appTypeNameCache = CacheBuilder
                    .newBuilder().maximumSize(1000)
                    .expireAfterWrite(12, TimeUnit.HOURS).recordStats()
                    .refreshAfterWrite(12, TimeUnit.HOURS)
                    .removalListener(new RemovalListener<Object, Object>() {
                        @Override
                        public void onRemoval(RemovalNotification<Object, Object> notification) {
                            logger.info(notification.getKey() + " was removed, cause is " + notification.getCause());
                        }
                    }).build(
                            new CacheLoader<Integer, String>() {
                                @Override
                                public String load( Integer key) {
                                    return cacheService.getAppTypeName(key);
                                }
                            });
        }
        return appTypeNameCache;
    }

}

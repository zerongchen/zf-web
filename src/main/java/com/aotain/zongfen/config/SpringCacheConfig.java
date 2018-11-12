package com.aotain.zongfen.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;

/**
 * Spring cache 注解配置类
 *  @Configuration
 *  @EnableCaching 开启缓存支持
 * @author daiyh@aotain.com
 * @date 2018/06/20
 */
@Configuration
@EnableCaching
public class SpringCacheConfig {
    @Bean
    public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
        // 设置自定义的缓存管理器
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        //设置缓存过期时间
        cacheManager.setDefaultExpiration(10000);
        cacheManager.setCacheNames(Arrays.asList(new String[]{"dictCache","userGroupCache"}));
        return cacheManager;
    }
}

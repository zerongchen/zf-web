package com.aotain.zongfen.service.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * redis service的抽象接口
 * @author DongBoye
 * @param <T>
 * @since 2017-11-30
 */

public interface RedisService<T> {
	/**
	 * <pre> Obejct </pre>
	 * 新增
	 * @param o
	 */
	void add(T o);
	/**
	 * <pre> Obejct </pre>
	 * 批量新增 使用pipeline方式
	 * @param list
	 */
	void add(List<T> list);
	/**
	 * 添加字符
	 * @param <V>
	 * @param <K>
	 * @param map
	 */
	void add(Map<String, String> map);
	/**
	 * 
	 * @param key
	 * @param keylist
	 */
	void add(String key, List<String> keylist);
	
	void addMap2Map(Map<String,Map<String,String>> map);
	/**
	 * 获取模糊匹配的key集合
	 * @param key
	 * @return
	 */
	Set<? extends String> keys(final String key);
	
	/**
	 * <pre> Obejct </pre>
	 * 获取 
	 * @param key
	 * @return
	 */
	String get(String key);
	
	List<String> getList(String key,long num);
	
	/**
	 * 获取集合
	 * @param keys
	 * @return
	 */
	List<String> get(List<String> keys);
	/**
	 * 
	 * @return
	 */
	long size();
	
	/**
	 * @return
	 */
	List<String> get();
	
	List<String> getAll(String keyOrValues);
	/**
	 * @param start
	 * @param end
	 * @return
	 */
	List<String> get(long start, long end);
	/**
	 * 删除:Map或者Key
	 * @param key
	 */
	void delete(final String keyOrValue);
	/**
	 * 批量删除:Map或者Key
	 * @param keys
	 */
	void delete(final List<String> keyOrValues);
	
	/**
	 * 订阅消息
	 * @param channel
	 * @param message
	 */
	void sendMessage(String channel, T message);
	
	String getRedisByHK(String hkey);
	
}

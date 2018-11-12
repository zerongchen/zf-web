package com.aotain.zongfen.service.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.service.redis.RedisService;

@Service("redisModelService")
public class RedisModelService implements RedisService<RedisModel> {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Override
	public void add(RedisModel o) {
		// TODO Auto-generated method stub
		String key = "test_redismodel2";
		ValueOperations<String, String> valueops = redisTemplate.opsForValue();
		StringBuffer sb = new StringBuffer();
		sb.append(o.getUsername()+"@").append(o.getPassword()+"@").append(o.getFlag()+"@");
		valueops.set(key, sb.toString());
	}

	@Override
	public void add(List<RedisModel> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(Map<String, String> map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(String key, List<String> keylist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMap2Map(Map<String, Map<String, String>> map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<? extends String> keys(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(String key) {
		ValueOperations<String, String> valueops = redisTemplate.opsForValue();
		return valueops.get(key);
	}

	@Override
	public List<String> getList(String key, long num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> get(List<String> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAll(String keyOrValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> get(long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String keyOrValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(List<String> keyOrValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(String channel, RedisModel message) {
		redisTemplate.convertAndSend(channel, message.toString());
	}

	@Override
	public String getRedisByHK(String hkey) {
		// TODO Auto-generated method stub
		return null;
	}

}

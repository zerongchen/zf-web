package test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aotain.zongfen.ZongfenApplication;
import com.aotain.zongfen.service.redis.RedisModel;
import com.aotain.zongfen.service.redis.RedisModelService;

//import ch.qos.logback.core.util.ContextUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ZongfenApplication.class)
public class TestRedis {
	
	@Autowired
	private RedisModelService redisService;
	
//	private RedisService<RedisModel>  redisService = ContextUtil.getContext().getBean("redisModelService",RedisService.class);
	
//	@Test
	public void test1() {
		/*RedisModel obj = new RedisModel();
		obj.setFlag(1);
		obj.setUsername("username_1");
		obj.setPassword("password_2");
		
		redisService.add(obj);*/
		
		String value = redisService.get("test_redismodel");
		
		System.out.println(value);
	}
	
	@Test
	public void testMessage() {
		RedisModel obj = new RedisModel();
		obj.setFlag(1);
		obj.setUsername("username_1");
		obj.setPassword("password_2");
		
		redisService.add(obj);
		
		redisService.sendMessage("StrategySendChannel", obj);
	}
}

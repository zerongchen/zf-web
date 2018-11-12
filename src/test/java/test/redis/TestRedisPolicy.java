package test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aotain.common.config.redis.BaseRedisService;
import com.aotain.zongfen.ZongfenApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ZongfenApplication.class)
public class TestRedisPolicy {

	@Autowired
    private BaseRedisService<String, String, String> rediscluster;
	
	@Test
	public void test1() {
//		System.out.println("-----------------------userInfoMN_24--------hao128@com---------> value : "+rediscluster.getHashValueByHashKey("userInfoMN_24", "hao128@com"));
		
		System.out.println("==================Strategy_0_200:"+rediscluster.getHashs("Strategy_0_200")+", Strategy_0_207:"+rediscluster.getHashs("Strategy_0_207"));
	}
}

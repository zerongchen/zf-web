package test.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.aotain.zongfen.cache.CommonCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aotain.zongfen.ZongfenApplication;
import com.aotain.zongfen.model.common.CommonTree;
import com.aotain.zongfen.service.common.CommonTreeService;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ZongfenApplication.class)
public class ApplicationTest {
	
	@Autowired
	private CommonTreeService commonTreeService;

	@Autowired
	private CommonCache commonCache;
	
	@Test
	public void test() {
		List<CommonTree> list = commonTreeService.getTree();
		for(CommonTree tree :list ) {
			System.out.println("--------------------------tree=["+tree.getId()+", "+tree.getName() +", "+ tree.getLevel()+", "+tree.getIsParent());
		}
		
	}

	@Test
	public  void testUser(){
		long l=47;
		try {
			String name =commonCache.getUserGroupNameCache().get(l);
			System.out.println(name);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}

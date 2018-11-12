package test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.apppolicy.AppUserController;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.utils.LogUtil;
import com.aotain.zongfen.model.system.OperationLog;
import com.aotain.zongfen.utils.SpringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class TestApp {
	
	@Test
	public void test() {
		String a = "com.aotain.zongfen.controller.apppolicy.FlowManagerController";
		String[] bb = a.split("\\.");
		
		System.out.println(bb.length);
	}
	
	private boolean isBinary(String str) {
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i) != '0' && str.charAt(i) != '1') {
				return false;
			}
		}
		return true;
	}
	@Test
	public void testAo() throws Exception {
		String dataJson = "messageNo=";
		ProxyUtil.changeVariable(AppUserController.class,"modifyPolicy",dataJson,"test");
	}

}

package test.filepath;

import org.junit.Test;

import com.aotain.zongfen.service.general.ipaddress.impl.IPAddressServiceImpl;

public class TestFilePath {
	
	@Test
	public void testPath() {
		String p1 = IPAddressServiceImpl.class.getResource("/").toString();
		System.out.println(p1);
	}
}

package com.aotain.zongfen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import com.aotain.common.utils.monitorstatistics.ModuleConstant;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;

@SpringBootApplication
@ServletComponentScan()
public class ZongfenApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(ZongfenApplication.class, args);
		MonitorStatisticsUtils.initModuleNoThread(ModuleConstant.MODULE_ZFWEB);
	}

}

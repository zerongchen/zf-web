package com.aotain.zongfen.config.xml;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 
* @ClassName: ImportXmlConfig 
* @Description: 导入引用的xml文件到spring boot项目里(这里用一句话描述这个类的作用) 
* @author DongBoye
* @date 2018年2月2日 下午2:59:18 
*
 */
@Configuration  
@ImportResource(locations= {"classpath:spring-application.xml"}) 
public class ImportXmlConfig {
//什么也不做
}

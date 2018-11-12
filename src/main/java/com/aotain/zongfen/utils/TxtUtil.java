package com.aotain.zongfen.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 	<p>TXT 工具类</p>
 *  @author chenzr
 *  @since 2018-01-09
 *  @version 2.0
 */
public class TxtUtil {

	/**
	 * 指定路径下生成文件
	 * @param path
	 * @param dataList
	 */
	public static void createTxt(String path , List<?> dataList ,Class<?> classObj) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		File newFile = new File(path);
		BufferedWriter buffer = null;

		if(!newFile.getParentFile().exists()){
			newFile.getParentFile().mkdirs();
		}
		if(!newFile.exists()){
			newFile.createNewFile();
		}

		buffer = new BufferedWriter(new java.io.FileWriter(newFile));
		StringBuffer writerData = new StringBuffer();

		Method method = classObj.getMethod("toTxtString");
		for(Object obj :dataList) {
			String string = (String) method.invoke(obj);
			writerData.append(string).append("\r\n");
		}
		buffer.write(writerData.toString());
		buffer.flush();
		buffer.close();

	}

}

package com.aotain.zongfen.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 网页参数处理工具类，主要用于解析，封装reqeust的各种参数
 * @author yinzf 
 * @createtime 2015年1月3日 下午5:53:03
 */
public class WebParamUtils {

	private static Logger logger = LoggerFactory.getLogger(WebParamUtils.class);
	
	/**
	 * 将请求参数还原成字符串，如 name=1&title=spring...
	 * @param request
	 * @return
	 */
	public static String requestParametersToString(HttpServletRequest request){
		String queryString = "";
		
		Map<String,String[]> pameters = request.getParameterMap();
		
		for(String name:pameters.keySet()){
			String[] values = pameters.get(name);
			for(String value:values){
				if (!"".equals(value)){
					queryString+="&"+name+"="+value;
				}
			}
		}
		queryString = queryString.replaceFirst("&","");
		return queryString;
	}
	
	/**
	 * 获取 HTTP 请求的路径+参数
	 * @param request HttpServletRequest 请求
	 * @param includePost 是否包括post请求
	 * @return
	 */
	public static String getRequestFullPath( HttpServletRequest request, boolean includePost){
		
		String fullpath = request.getRequestURI();
		String queryString = "";
		queryString = includePost?requestParametersToString(request):request.getQueryString();
		if (StringUtils.hasText(queryString)){
			queryString = "?"+queryString;
			fullpath+=queryString;
		}
		return fullpath;
	}
	
	/**
	 * 获取 HTTP请求的路径+参数 并已特殊处理的UTF8编码
	 * <p>把获取的URL用UTF8编码后，把 '%' 替换成 '|' </p>
	 * @param request HTTP请求
	 * @param includePost 是否包括POST请求
	 * @return
	 */
	public static String getRequestFullPathEncode( HttpServletRequest request, boolean includePost){
		String fullpath = null;
		try {
			fullpath = URLEncoder.encode(getRequestFullPath(request,includePost), "UTF-8");
			fullpath = fullpath.replaceAll("%", "|");
		} catch (UnsupportedEncodingException e) {
			logger.error("delete class info strategy error",e);
		}
		return fullpath;
	}
	
	/**
	 * 获取 HTTP请求的路径+参数 并已特殊处理的UTF8编码,注意：默认只处理GET请求参数
	 * <p>把获取的URL用UTF8编码后，把 '%' 替换成 '|' </p>
	 * @param request HTTP请求
	 * @return
	 */
	public static String getRequestFullPathEncode(HttpServletRequest request){
		return getRequestFullPathEncode(request,false);
	}
	
	/**
	 * 解码经过特殊处理的UTF8编码URL
	 * <p>将URL中的 '|' 还原成 '%' ，再用UTF8解码</p>
	 * @param url http请求
	 * @param keepUTF8Param 参数值是否保持utf8编码
	 * @return
	 */
	public static String decodeUrl(String url,boolean keepUTF8Param){
		String decodedUrl = url;
		if (StringUtils.hasText(url)){
			decodedUrl	= decodedUrl.replaceAll("\\|","%");
			try {
				decodedUrl = URLDecoder.decode(decodedUrl, "UTF-8");
				if (keepUTF8Param){
					return utf8EncodeParameters(decodedUrl);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return decodedUrl;
	}
	
	public static String decodeUrl(String url){
		return decodeUrl(url,false);
	}
	
	/**
	 * 将指定的参数用UTF-8编码
	 * @param url 路径
	 * @return
	 */
	public static String utf8EncodeParameters(String url){
		String encodedPath = url;
		if (url.contains("?")&&url.indexOf("?")<url.length()-1){
			int idx = url.indexOf("?");
			if (idx<url.length()-1){
				encodedPath =  url.substring(0,idx);
				String parameters = url.substring(idx+1);
				String[] params = parameters.split("&");
				String newParams = "";
				for(String p:params){
					if (p.contains("=")){
						String[] pArray = p.split("="); 
						String pName = null;
						String pValue = null;
						if (pArray.length==2){
							 pName = pArray[0];
							pValue = pArray[1];
						}else{
							pName = pArray[0];
							pValue ="";
						}
						newParams+="&"+pName+"=" + encodeUTF8(pValue);
					}else{
						newParams+="&"+p;
					}
				}
				encodedPath+="?"+newParams.replaceFirst("&","");
			}
		}
		return encodedPath;
	}
	
	/**
	 * UTF-8编码
	 * @param input
	 * @return
	 */
	public static String encodeUTF8(String input){
		String output = null;
		try {
			output = URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("encode to UTF8 ",e);
		}
		return output;
	} 
	
	public static String getBrowser(HttpServletRequest request) {
        String UserAgent = request.getHeader("USER-AGENT").toLowerCase();  
        if (UserAgent != null) {  
            if (UserAgent.indexOf("msie") >= 0)  
                return "IE";  
            if (UserAgent.indexOf("firefox") >= 0)  
                return "FF";  
            if (UserAgent.indexOf("safari") >= 0)  
                return "SF";  
        }  
        return null;  
    }

	/**
	 *	16进制转成十进制(0x表示的16进制)
	 */
	public static Integer paseHextoInt(String value){
		return Integer.parseInt(value.substring(2, value.length()), 16);
	}

	/**
	 * 十进制转成16进制(0x表示的16进制,2位的16进制)
	 */
	public static String paseToHexString(Integer value){
		return paseToHexString(value,2);
	}

	/**
	 * 十进制转成16进制(0x表示的16进制,2位的16进制)
	 * @param value
	 * @param bitNum 位数
	 */
	public static String paseToHexString(Integer value,Integer bitNum){
		String hexString  = Integer.toHexString(value);
		if(hexString.length()>=bitNum){
			return "0x"+hexString;
		}
		if(hexString.length()<bitNum){
			int len = bitNum - hexString.length();
			String prefix = "0x";
			for(int i=0;i<len;i++){
				prefix +="0";
			}
			return prefix+hexString;
		}
		return hexString;
	}

	private static String toHex(int num) {
		char[] HEX = "0123456789abcdef".toCharArray();
		if(num == 0) {
			return new String(HEX, 0, 1);
		}
		char[] chs = new char[Integer.SIZE / 4];
		int offset = chs.length - 1;
		while(num != 0) {
			chs[offset--] = HEX[num & 0xf];
			num >>>= 4;
		}
		return new String(chs, offset + 1, chs.length - offset - 1);
	}

	/**
	 * 2进制转成10进制
	 * @param value
	 * @return
	 */
	public static Integer paseBinarytoInt(String value){
		return Integer.valueOf(value,2);
	}

	/**
	 * 十进制转成2进制
	 * @param value
	 * @param index 2 进制位数
	 * @return
	 */
	public static String paseToBinaryString(Long value,Integer index){

		String binaryString="";
		for(int i = index-1;i >= 0; i--)
			binaryString+=(value >>> i & 1);

		return binaryString;
	}

	public static void main(String[] args){
//		System.out.println(paseBinarytoInt("000000000000111100000000"));
//		System.out.println("000000000000111100000000");
//		System.out.println(paseToBinaryString(3840,24).length());
		System.out.println(paseHextoInt("0x82"));
//		System.out.println(paseToHexString(320,4));



	}
}

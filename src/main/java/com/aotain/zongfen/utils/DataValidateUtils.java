package com.aotain.zongfen.utils;

import java.util.regex.Pattern;

/**
 * 数据验证工具类
 * @author yinzf 
 * @createtime 2014年11月27日 下午7:08:47
 */
public class DataValidateUtils {
	/**
	 * 手机号的正则
	 */
	public static final String PHONE_REGEXP = "^((17[0,1,3,5,6,7,8])|(13[0-9])|(14[5,7,9])|(15[^4,\\D])|(18[0-9]))\\d{8}$"; //手机号
	/**
	 * 固定电话的正则
	 */
	public static final String TELEPHONE_REGEXP ="(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" +  
                "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|(400)-(\\d{3}-\\d{4}|\\d{4}-\\d{3})|(400)"+
                "[0,1,6,7,8,9]-(\\d{3}-\\d{3}|\\d{2}-\\d{4}|\\d{4}-\\d{2})|^(400)\\d{7}"; //"^(0\\d{2,3}-?)?(\\d{7,8}|\\d{3,10})$"; 固话  ([0-9]{2,3}-)?([0-9]{3,4}-)?[0-9]{7,8}(-[0-9]{3,5})?
	/**
	 * 邮政编码省级的正则
	 */
	public static final String POST_PROVINCE_REGEXP = "^[0-9]\\d{1}0{4}$";
	/**
	 * 邮政编码地市的正则
	 */
	public static final String POST_CITY_REGEXP = "^[1-9]\\d{3}0{2}$";
	/**
	 * 邮政编码县级的正则
	 */
	public static final String POST_COUNTRY_REGEXP = "^[1-9]\\d{5}$";
	/**
	 * 邮政编码的正则
	 */
	public static final String POST_REGEXP = "^[0-9]\\d{5}$"; //邮编
	/**
	 * 邮箱的正则
	 */
	public static final String EMAIL_REGEXP = "^[a-zA-Z0-9_\\.\\-]+@[a-zA-Z0-9\\-]+\\.\\w+(\\.\\w+)*$"; //邮箱
	/**
	 * 身份证的正则
	 */
	public static final String IDCARD_REGEXP = "^[1-9](\\d{14}|(\\d{16}[0-9xX]))$"; //身份证
	/**
	 * 数字的正则
	 */
	public static final String NUMBER_REGEXP = "^\\d+$";
	/**
	 * 正整数的正则
	 */
	public static final String POSITIVE_NUMBER_REGEXP = "^[1-9]{1}\\d+$"; //正整数
	/**
	 * ipv4的正则
	 */
	public static final String IPV4_REGEXP = "^(25[0-5]?|2[0-4]?\\d?|0|1\\d{0,2}|[3-9]\\d?)(\\.(25[0-5]?|2[0-4]?\\d?|0|1\\d{0,2}|[3-9]\\d?)){3}$";
	/**
	 * ipv6的正则
	 */
	public static final String IPV6_STD_REGEXP = "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
	/**
	 * 16进制的ipv6的正则
	 */
	public static final String IPV6_HEX_REGEXP = "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$"; //ipv6
	/**
	 * 日期的正则
	 */
	public static final String DATE_REGEXP = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2]\\d|3[0-1])$"; //日期
	
	/**
	 * yyyy-MM-dd HH:mm:ss时间格式的正则
	 */
	public static final String DATA_TIME_REGEXP = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
	
	/**
	 * 校验正则
	 * @param regExp
	 * @param data
	 * @return
	 */
	public static boolean validate(String regExp, String data) {
		return Pattern.compile(regExp).matcher(data).matches();
	}
	
}

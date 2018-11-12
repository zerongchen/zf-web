package com.aotain.zongfen.utils.basicdata;

import org.apache.commons.lang3.StringUtils;

/**
 * 基础数据核验错误信息类
 * 方法中涉及到CN（中文提示信息）的，都为field-length.properties中CN结尾的值
 * 方法中涉及到长度的，都为field-length.properties中对应的字段的长度
 */
public class BasicDataValidateMsg {
	
	public static final int TYPE_ID_ILLEGAL_FORMAT = 1;
	public static final int TYPE_ID_ILLEGAL_KEY = 2;
	public static final int TYPE_ID_NO_KEY = 3; 
	public static final int TYPE_ID_NO_VALUE = 4;
	/**
	 * 工商营业执照号码
	 */
	public static final int TYPE_ID_1 = 1; 
	/**
	 * 身份证
	 */
	public static final int TYPE_ID_2 = 2;
	/**
	 * 组织机构代码证书
	 */
	public static final int TYPE_ID_3 = 3;
	/**
	 * 事业法人证书
	 */
	public static final int TYPE_ID_4 = 4; 
	/**
	 * 军队代号
	 */
	public static final int TYPE_ID_5 = 5;
	/**
	 * 社团法人证书
	 */
	public static final int TYPE_ID_6 = 6;
	/**
	 * 护照
	 */
	public static final int TYPE_ID_7 = 7;
	/**
	 * 军官证
	 */
	public static final int TYPE_ID_8 = 8; 
	/**
	 * 台胞证
	 */
	public static final int TYPE_ID_9 = 9; 
	/**
	 * 其他
	 */
	public static final int TYPE_ID_999 = 999;
	
	
	/**
	 * 字段值为空的错误信息 example：XXXX为空
	 * @param name XXXX（名称）
	 * @return
	 */
	public static String getEmptyErrMsg(String name) {
		return name + "为空";
	} 
//	/**
//	 * 字段值为空的错误信息 example：XXXX为空
//	 * @param name XXXX（名称对应的字段）
//	 * @return
//	 */
//	public static String getEmptyErrMsgCN(String cn) {
//		return FieldInfoProps.instance.getCN(cn) + "为空";
//	}
	/**
	 * 字段长度校验的错误信息 example： XX（名称）XX（值）超过长度，实际长度X，最大长度X
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @param valueLength 值的长度
	 * @param maxLength 字段的最大长度
	 * @return
	 */
	public static String getMaxLengthErrMsg(String name, String value, int valueLength, int maxLength) {
		return name + value + "超过长度，实际长度" + valueLength + "，最大长度" + maxLength;
	} 
	/**
	 * 字段长度校验的错误信息 example： XX（名称）XX（值）超过长度，实际长度X，最大长度X
	 * @param value XX（值）
	 * @param valueLength 值的长度
	 * @param maxLength 最大的长度
	 * @return
	 */
	public static String getMaxLengthErrMsgCN(String value, int valueLength, int maxLength) {
		return value + "超过长度，实际长度" + valueLength + "，最大长度" + maxLength;
	}
	/**
	 * 通用的错误信息 example：XX（名称）XX（值）不合法
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @return
	 */
	public static String getCommonErrMsg(String name, String value) {
		return name + value + "不合法";
	}
	/**
	 * 通用的错误信息 example：XX（名称）XX（值）不合法
	 * @param value XX（值）
	 * @return
	 */
	public static String getCommonErrMsgCN(String value) {
		return value + "不合法";
	}
	/**
	 * 一个字段与另一个字段不匹配的错误信息 example：XX（名称）XX（值）与XX（名称）XX（值）不匹配
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @param nameOther XX（另一个名称）
	 * @param valueOther XX（另一个值）
	 * @return
	 */
	public static String getNotMatchErrMsg(String name, String value, String nameOther, String valueOther) {
		return name + value + "与" + nameOther + valueOther + "不匹配";
	}
	/**
	 * 一个字段与另一个字段不匹配的错误信息 example：XX（名称）XX（值）与XX（名称）XX（值）不匹配
	 * @param value XX（值）
//	 * @param cnOther XX（另一个名称对应的字段）
	 * @param valueOther （另一个名称的值）
	 * @return
	 */
	public static String getNotMatchErrMsgCN(String value, String valueOther) {
		return value + "与" + valueOther + "不匹配";
	}
	
	/**
	 * 证件类型值校验不合法的错误信息 example：XX（名称）XX（值）不合法。
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @param typeId 证件类型
	 * @return
	 */
	public static String getTypeValueLegalErrMsg(String name, String value, int typeId) {
		return name + value + "不合法";
	} 
	/**
	 * 证件类型值校验不合法的错误信息 example：XX（名称）XX（值）不合法。
	 * @param value XX（值）
	 * @param typeId 证件类型
	 * @return
	 */
	public static String getTypeValueLegalErrMsgCN(String value, int typeId) {
		return value + "不合法";
	}
	/**
	 * 唯一标识的错误信息 example：XX(名称)XX(值)已经存在
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @return
	 */
	public static String getUniqueErrMsg(String name, String value) {
		return name + value + "已经存在";
	}
	/**
	 * 唯一标识的错误信息 example：XX(名称)XX(值)已经存在
	 * @param value XX（值）
	 * @return
	 */
	public static String getUniqueErrMsgCN(String value) {
		return value + "已经存在";
	}
	/**
	 * Excel唯一标识的错误信息 example：XX（名称）XX（值）重复
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @return
	 */
	public static String getRepeatExcelErrMsg(String name, String value) {
		return name + value + "重复";
	}
	/**
	 * Excel唯一标识的错误信息 example：XX（名称）XX（值）重复
	 * @param value XX（值）
	 * @return
	 */
	public static String getRepeatExcelErrMsgCN(String value) {
		return value + "重复";
	} 
	/**
	 * 依赖关系不存在 example：XX（名称）XX（值）不存在
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @return
	 */
	public static String getNoExistErrMsg(String name, String value) {
		return name + value + "不存在";
	}
	/**
	 * 依赖关系不存在 example：XX（名称）XX（值）不存在
	 * @param value XX（值）
	 * @return
	 */
	public static String getNoExistErrMsgCN(String value) {
		return value + "不存在";
	} 
	/**
	 * IP地址段不合法：起始IP：XXX 大于结束IP：XXX
	 * @param startIP
	 * @param endIP
	 * @return
	 */
	public static String getStartIPgtEndIPMsgCN(String startIP, String endIP) { 
		return "IP地址段不合法，起始IP：" + startIP + " 大于结束IP：" + endIP; 
	} 
	/**
	 * IP地址段不合法：起始IP到结束IP的范围超过了1个C的地址
	 * @param startIP
	 * @param endIP
	 * @return
	 */
	public static String getExceedIPRangeMsgCN(String startIP, String endIP) { 
		return "IP地址段不合法，起始IP:" + startIP + "到结束IP:" + endIP + "的范围超过了1个C的地址"; 
	}
	/**
	 * 邮编格式的错误信息 example：XX（名称）XX（值）不合法，邮编格式为6位数字
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @return
	 */
	public static String getZipCodeErrMsg(String name, String value) {
		return name + value + "不合法，邮编格式为6位数字";
	}
	/**
	 * 邮编格式的错误信息 example：XX（名称）XX（值）不合法，邮编格式为6位数字
	 * @param value XX（值）
	 * @return
	 */
	public static String getZipCodeErrMsgCN(String value) {
		return value + "不合法，邮编格式为6位数字";
	}
	/**
	 * 类型的错误信息 example：XX（名称）XX（值）不合法，类型的格式为类型值-类型名，如1-工商营业执照号码
	 * 			 example：不存在XX（名称）为XX（值）的值
	 * 			 example：XX（名称）XX（值）的类型名称与类型值不匹配
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @return
	 */
	public static String getTypeSplitErrMsg(String name, String value, int type) {
		if (type == TYPE_ID_ILLEGAL_FORMAT) {
			return name + value + "不合法，类型的格式为类型值-类型名";
		} 
		if (type == TYPE_ID_ILLEGAL_KEY) {
			return name + value + "不合法，类型值-类型名的类型值只能为整数";
		}
		if (type == TYPE_ID_NO_KEY) {
			return "不存在" + name + "为" + value + "的值";
		} 
		if (type == TYPE_ID_NO_VALUE) {
			return name + value + "的类型名称与类型值不匹配";
		} 
		return name + value + "不合法，类型的格式为类型值-类型名";
	} 
	/**
	 * 类型的错误信息 example：XX（名称）XX（值）不合法，类型的格式为类型值-类型名，如1-工商营业执照号码
	 * 			 example：不存在XX（名称）为XX（值）的值
	 * 			 example：XX（名称）XX（值）的类型名称与类型值不匹配
	 * @param cn XX（名称对应的字段）
	 * @param value XX（值）
	 * @return
	 */
//	public static String getTypeSplitErrMsgCN(String cn, String value, int type) {
//		if (type == TYPE_ID_ILLEGAL_FORMAT) {
//			return value + "不合法，类型的格式为类型值-类型名";
//		}
//		if (type == TYPE_ID_ILLEGAL_KEY) {
//			return value + "不合法，类型值-类型名的类型值只能为整数";
//		}
//		if (type == TYPE_ID_NO_KEY) {
//			return "不存在" + FieldInfoProps.instance.getCN(cn) + "为" + value + "的值";
//		}
//		if (type == TYPE_ID_NO_VALUE) {
//			return value + "的类型名称与类型值不匹配";
//		}
//		return value + "不合法，类型的格式为类型值-类型名";
//	}
	/**
	 * 获取数据库字段的最大长度
	 * @param field 数据库字段
	 * @return
	 * @throws Exception
	 */
//	public static int getFieldMaxLength(String field) {
//		return FieldInfoProps.instance.getLength(field);
//	}
	/**
	 * 获取字段信息在其它表信息中不存在的错误信息 example：XX（经营者名称）XX（...）在XX（经营者信息）中不存在
	 * @param name XX（名称）
	 * @param value XX（值）
	 * @param cnValue XX（表信息名称）
	 * @return
	 */
	public static String getNoExistInErrMsg(String name, String value, String cnValue) {
		return name + value + "在" + cnValue + "中不存在";
	}
	/**
	 * 获取字段信息在其它表信息中不存在的错误信息 example：XX（经营者名称）XX（...）在XX（经营者信息）中不存在
	 * @param value XX（值）
	 * @param cnTable XX（表信息所对应的名称）
	 * @return
	 */
//	public static String getNoExistInErrMsgCN(String value, String cnTable) {
//		return value + "在" + FieldInfoProps.instance.getCN(cnTable) + "中不存在";
//	}
	/**
	 * 不能大于当前时间的错误信息 example：XX（名称）XX（...）不合法，不能大于当前时间
	 * @param value XX（值）
	 * @return
	 */
	public static String getLaterThanCurrDateErrMsgCN(String value) {
		return value + "不合法，不能大于当前时间";
	} 
	
	/**
	 * 只能为特定分割符的错误信息 example：XX（名称）XX（值）只能用XX（分隔符）隔开
	 * @param value XX（值）
	 * @param separate XX（分隔符）
	 * @return
	 */
	public static String getSplitErrMsgCN(String value, String separate, String illegalSeparate) {
		return (StringUtils.isBlank(illegalSeparate) ? "" : "存在非法的分隔符" + illegalSeparate + ",") + value + "只能用" + separate + "隔开";
	} 
	/**
	 * 需要大于某值的错误信息 example：XX（名称）XX（值）不大于XX（名称）XX（值）
	 * @param compareName XX（需要比较的名称）
	 * @param compareValue XX（需要比较的值）
	 * @param sourceName XX（被比较的名称）
	 * @param sourceValue XX（被比较的值）
	 * @return
	 */
	public static String getMustLargeThanErrMsg(String compareName, String compareValue, String sourceName, String sourceValue) {
		return compareName + compareValue + "不大于" + sourceName + sourceValue;
	}
	/**
	 * 需要大于某值的错误信息 example：XX（名称）XX（值）不大于XX（名称）XX（值）
	 * @param compareValue XX（需要比较的名称的值）
	 * @param sourceValue XX（被比较的名称的值）
	 * @return 
	 */
	public static String getMustLargeThanErrMsgCN(String compareValue, String sourceValue) {
		return compareValue + "不大于" + sourceValue;
	}
	/**
	 * 行政区域不归属于的错误字段 example:XX（名称）XX（值）不归属于XX（名称）XX（值）
	 * @param value XX（下级行政区域）
	 * @param valueUp XX（上级行政区域）
	 * @return
	 */
	public static String getPostCodeErrMsgCN(String value, String valueUp) {
		return value + "不归属于" + valueUp;
	} 
	/**
	 * 自定义错误消息
	 * @param error
	 * @return
	 */
	public static String getSelfDefinedErrorMsg(String error) {
		return StringUtils.isBlank(error) ? "" : error;
	}
	
}

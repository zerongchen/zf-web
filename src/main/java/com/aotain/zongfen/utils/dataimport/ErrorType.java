package com.aotain.zongfen.utils.dataimport;


/**
 * 错误类型
 */
public enum ErrorType {
	
	NULL("为空"),
	TYPE_ERROR("类型错误"),
	PATTERN_ERROR("格式不正确"),
	ILLEGAL("非法"),
	REPEAT("重复"),
	EXIST("已存在"),
	NOT_EXIST("不存在"),
	OUT_OF_RANGE("超出范围"),
	VALUE_OUT_OF_RANGE("长度超出范围");

	private String description;

	private ErrorType( String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}

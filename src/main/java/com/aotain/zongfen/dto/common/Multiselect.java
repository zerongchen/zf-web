package com.aotain.zongfen.dto.common;

import lombok.Data;

/**
 * 单/复选下拉框
 */
@Data
public class Multiselect {
	
	private String label; //描述
	
	private String title; //文本值
	
	private String value; //值
	
	private boolean selected = false; //默认是否选中（true: 选中、false：不选中）
	
	private boolean disabled = false; //是否可选（true: 可选、false：禁选）

}

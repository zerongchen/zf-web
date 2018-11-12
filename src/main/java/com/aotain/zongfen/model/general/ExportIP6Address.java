package com.aotain.zongfen.model.general;

import java.io.Serializable;

import com.aotain.zongfen.annotation.ExpSheet;
import com.aotain.zongfen.annotation.Export;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ExpSheet(name = "地址-IPv6")
public class ExportIP6Address implements Serializable {
	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = -7780561147941564947L;

	// 省份名称
	@Export(title = "省份", id = 1)
	private String provinceName;

	@Export(title = "城域网", id = 2)
	private String areaName;

	@Export(title = "城域网ID", id = 3)
	private String areaId;// 2进制字符串

	@Export(title = "起始地址", id = 4)
	private String startIp;

	@Export(title = "终止地址", id = 5)
	private String endIp;

	private String ipType;

}
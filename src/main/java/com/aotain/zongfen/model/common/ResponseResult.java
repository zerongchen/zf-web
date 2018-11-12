package com.aotain.zongfen.model.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {

	//结果0：失败，1:成功
	@NonNull
	private Integer result;
	
	//返回信息
	@NonNull
	private String message;

	private List<BaseKeys> keys;
	
	//返回数据
	private T data;
	
	private List<Long> bindMessageNo;


}

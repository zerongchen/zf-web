package com.aotain.zongfen.utils;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

	private Long total;
	
	@NonNull private List<T> rows ;

}

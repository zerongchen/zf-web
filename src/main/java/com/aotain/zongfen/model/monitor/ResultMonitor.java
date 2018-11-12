package com.aotain.zongfen.model.monitor;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultMonitor<T> {

	private long total;
	
	private List<T> details;
}

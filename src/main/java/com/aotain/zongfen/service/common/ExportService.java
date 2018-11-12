package com.aotain.zongfen.service.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aotain.zongfen.utils.export.BaseModel;

public interface ExportService<T> {

	String export(BaseModel<T> baseModel,HttpServletResponse response,HttpServletRequest request);
	
}

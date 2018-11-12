package com.aotain.zongfen.model.dataimport;

import com.aotain.zongfen.utils.dataimport.ImportConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class ImportResultList {

	private String fileName; //导入文件

	private int result;  //导入结果标识
	 
	private String describtion;   //总体描述信息
	
	private List<DataErrorInfo> dataErrorInfoList = new ArrayList<DataErrorInfo>(); //详细的错误信息

	private Map<String, List> datas; 
	
	private Map<Integer, Map<Integer, String[]>> initDatasMapList = new HashMap<Integer, Map<Integer,String[]>>();


	public List<DataErrorInfo> getDataErrorInfoList() {
		return dataErrorInfoList;
	}


	public void addError(DataErrorInfo dataErrorInfo) {
		result = ImportConstant.DATA_IMPORT_FAIL;
		dataErrorInfoList.add(dataErrorInfo); 
	} 
	
	public boolean hasError() {
		return result == ImportConstant.DATA_IMPORT_FAIL; 
	}
}

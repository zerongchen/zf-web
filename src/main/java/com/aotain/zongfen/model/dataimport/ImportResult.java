package com.aotain.zongfen.model.dataimport;


import com.aotain.zongfen.utils.dataimport.FileType;

public class ImportResult {
	
	private int count; //成功导入数据的行数
	
	private FileType fileType; //文件类型
	
	private DataErrorInfo dataErrorInfo; //错误信息
	
	public int getCount() {
		return count;
	}

	public FileType getFileType() {
		return fileType;
	}

	public DataErrorInfo getDataErrorInfo() {
		return dataErrorInfo;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public void setDataErrorInfo(DataErrorInfo dataErrorInfo) {
		this.dataErrorInfo = dataErrorInfo;
	}

}

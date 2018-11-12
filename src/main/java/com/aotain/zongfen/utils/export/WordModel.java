package com.aotain.zongfen.utils.export;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;

/**
 * WORD
 * @author yinzf 
 * @createtime 2015年9月6日 下午5:37:32
 */
public class WordModel<T> extends BaseModel<T>{
	
	private String title; //标题  
	
	private Rectangle pageSize = PageSize.A4; //纸张大小
	
	private int writeNum = 1000; //每次写入文件的行数
	
	public String getTitle() {
		return title;
	}

	public Rectangle getPageSize() {
		return pageSize;
	}

	public int getWriteNum() {
		return writeNum;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPageSize(Rectangle pageSize) {
		this.pageSize = pageSize;
	}

	public void setWriteNum(int writeNum) {
		this.writeNum = writeNum;
	}
}

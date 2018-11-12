package com.aotain.zongfen.utils.export;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;


/**
 * PDF
 * @author yinzf 
 * @createtime 2015年9月5日 下午10:01:42
 */
public class PDFModel<T> extends BaseModel<T>{
	
	private String title; //标题  
	
    private float marginBottom = 50; //下边距
    
    private float marginLeft = 50;  //左边距
    
    private float marginTop = 50; //上边距
    
    private float marginRight = 50; //右边距
    
    private Rectangle pageSize = PageSize.A4; //纸张大小
    
    private int writeNum = 2000; //每次写入文件的行数
    
	public String getTitle() {
		return title;
	}

	public float getMarginBottom() {
		return marginBottom;
	}

	public float getMarginLeft() {
		return marginLeft;
	}

	public float getMarginTop() {
		return marginTop;
	}

	public float getMarginRight() {
		return marginRight;
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

	public void setMarginBottom(float marginBottom) {
		this.marginBottom = marginBottom;
	}

	public void setMarginLeft(float marginLeft) {
		this.marginLeft = marginLeft;
	}

	public void setMarginTop(float marginTop) {
		this.marginTop = marginTop;
	}

	public void setMarginRight(float marginRight) {
		this.marginRight = marginRight;
	}

	public void setPageSize(Rectangle pageSize) {
		this.pageSize = pageSize;
	}

	public void setWriteNum(int writeNum) {
		this.writeNum = writeNum;
	}

}

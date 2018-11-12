package com.aotain.zongfen.model.echarts;

public class Style {
	public boolean show;
	public String position;
	public String formatter;
	public Style() {
		super();
	}
	public Style(boolean show) {
		this.show = show;
	}
	public Style(boolean show, String position){
		this.show = show;
		this.position = position;
	}
	public Style(boolean show, String position, String formatter) {
		this.show = show;
		this.position = position;
		this.formatter = formatter;
	}
}

package com.aotain.zongfen.model.echarts;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ECharts<T> {
	
	// 数据分组
	public List<String> legend = new ArrayList<String>();
	
	// 横坐标
	public List<String> axis = new ArrayList<String>();
	
	// 纵坐标
	public List<T> series = new ArrayList<T>();
	
	public Title title;
	
	//图标类型，比如line，pie
	public String type;
	
	//显示单位
	public String unit;
	
	public int height;//高度
	
	public DataZoom dataZoom;
	
	public ECharts() {
	}
	
	public ECharts(List<String> categoryList, List<T> series,DataZoom dataZoom) {
		this.axis = categoryList;
		this.series = series;
		this.dataZoom = dataZoom;
	}
	
	public ECharts(List<String> legend, List<String> axis, List<T> series,DataZoom dataZoom) {
		this.legend = legend;
		this.axis = axis;
		this.series = series;
		this.dataZoom = dataZoom;
	}
	
	public ECharts(Title title, List<String> legend, List<String> axis, List<T> series,DataZoom dataZoom) {
		this.title = title;
		this.legend = legend;
		this.axis = axis;
		this.series = series;
		this.dataZoom = dataZoom;
	}
	
	public ECharts(Title title, List<String> legendList,List<String> categoryList, List<T> seriesList) {
		this.title = title;
		this.legend = legendList;
		this.axis = categoryList;
		this.series = seriesList;
	}
	public ECharts(Title title,List<String> categoryList, List<T> seriesList,DataZoom dataZoom) {
		this.title = title;
		this.axis = categoryList;
		this.series = seriesList;
		this.dataZoom = dataZoom;
	}
	
	public ECharts(String type, Title title, List<String> legendList,List<String> categoryList, List<T> seriesList) {
		this.type = type;
		this.title = title;
		this.legend = legendList;
		this.axis = categoryList;
		this.series = seriesList;
	}
	
	/**
	 * 自动设置高度
	 * @param height
	 * @param axis
	 * @param series
	 */
	public ECharts(int height, List<String> axis, List<T> series) {
		this.height=height;
		this.axis = axis;
		this.series = series;
	}
	/**
	 * 自动设置高度
	 * @param height
	 * @param legend
	 * @param axis
	 * @param series
	 */
	public ECharts(int height, List<String> legend, List<String> axis, List<T> series) {
		this.height=height;
		this.legend = legend;
		this.axis = axis;
		this.series = series;
	}
	/**
	 * 自动设置高度
	 * @param height
	 * @param title
	 * @param legendList
	 * @param categoryList
	 * @param seriesList
	 */
	public ECharts(int height, Title title, List<String> legendList,List<String> categoryList, List<T> seriesList) {
		this.height=height;
		this.title = title;
		this.legend = legendList;
		this.axis = categoryList;
		this.series = seriesList;
	}
}

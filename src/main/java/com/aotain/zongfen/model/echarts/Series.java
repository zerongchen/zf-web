package com.aotain.zongfen.model.echarts;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Series<T> {
    public String name;
    public String type;
    public String stack;//总量
    public Label label;//类型
    public Integer barWidth;//柱状图的宽度
    public Integer barMaxWidth; //柱状图最大宽度
    public Integer xAxisIndex;
    public Integer symbolSize;
    public boolean hoverAnimation;
    public Integer yAxisIndex;
    public List<T> data;
    public MarkLine markLine;
    public MarkPoint markPoint;
    public String areaStyle;
    public Map<String,Object> itemStyle;
    public Series() {
	}
    
	public Series(List<T> data) {
    	this.data = data;
	}
	
    public Series(String name, List<T> data) {
		this.name = name;
		this.data = data;
	}

	public Series(String name, String type, List<T> data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }
	
	public Series(String name, String type, List<T> data, MarkLine markLine) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.markLine = markLine;
    }
	/**
	 * 支持多个纵坐标
	 * @param name
	 * @param type
	 * @param data
	 * @param yAxisIndex
	 */
	public Series(String name, String type, List<T> data, Integer yAxisIndex) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.yAxisIndex = yAxisIndex;
    }

    /**
     * 支持颜色
     * @param name
     * @param type
     * @param data
     * @param yAxisIndex
     */
    public Series(String name, String type, List<T> data, Integer yAxisIndex,Map<String,Object> itemStyle) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.yAxisIndex = yAxisIndex;
        this.itemStyle=itemStyle;
    }

	public Series(String name, String type, List<T> data, Integer yAxisIndex, MarkLine markLine) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.yAxisIndex = yAxisIndex;
        this.markLine = markLine;
    }
	
	public Series(String name, String type, Integer barWidth, List<T> data) {
        this.name = name;
        this.type = type;
        this.barWidth = barWidth;
        this.data = data;
    }
	
	public Series(String name, String type, Integer barWidth, List<T> data, MarkLine markLine) {
        this.name = name;
        this.type = type;
        this.barWidth = barWidth;
        this.data = data;
        this.markLine = markLine;
    }
	/**
	 * 支持多个纵坐标
	 * @param name
	 * @param type
	 * @param data
	 * @param yAxisIndex
	 */
	public Series(String name, String type, Integer barWidth ,List<T> data, Integer yAxisIndex) {
        this.name = name;
        this.type = type;
        this.barWidth = barWidth;
        this.data = data;
        this.yAxisIndex = yAxisIndex;
    }
	
	public Series(String name, String type,Integer barWidth, List<T> data, Integer yAxisIndex, MarkLine markLine) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.barWidth = barWidth;
        this.yAxisIndex = yAxisIndex;
        this.markLine = markLine;
    }
	
	public Series(String name, String type, String stack, List<T> data) {
        this.name = name;
        this.type = type;
        this.stack = stack;
        this.data = data;
    }

	public Series(String name, String type, String stack, Label label,List<T> data) {
		this.name = name;
		this.type = type;
		this.stack = stack;
		this.label = label;
		this.data = data;
	}

	public Series(String name, String type, Label label, List<T> data) {
		this.name = name;
		this.type = type;
		this.label = label;
		this.data = data;
	}
	/**
	 * 柱状图的定制
	 * @param name
	 * @param type
	 * @param label
	 * @param data
	 * @param barWidth
	 */
	public Series(String name, String type, Label label, List<T> data,Integer barWidth) {
		this.name = name;
		this.type = type;
		this.label = label;
		this.data = data;
		this.barWidth = barWidth;
	}
	/**
	 * 
	 * @param name
	 * @param type
	 * @param stack
	 * @param data
	 * @param barWidth
	 */
	public Series(String name, String type, String stack, List<T> data,Integer barWidth) {
        this.name = name;
        this.type = type;
        this.stack = stack;
        this.data = data;
        this.barWidth = barWidth;
    }
	//(legend.get(j), "line",map.get(legend.get(j)), 20, areaStyle)
	public Series(String name, String type, String stack, List<T> data,Integer barWidth,String areaStyle) {
        this.name = name;
        this.type = type;
        this.stack = stack;
        this.data = data;
        this.barWidth = barWidth;
        this.areaStyle = areaStyle;
    }
	
	public Series(String name, String type, String stack, List<T> data,String areaStyle) {
        this.name = name;
        this.type = type;
        this.stack = stack;
        this.data = data;
        this.areaStyle = areaStyle;
    }

    /**
     * 上下 折线图定制(上图)
     * @param name
     * @param type
     * @param symbolSize
     * @param hoverAnimation
     * @param data
     */
    public Series(String name,String type , Integer symbolSize,boolean hoverAnimation,List<T> data){
        this.name=name;
        this.type=type;
        this.symbolSize=symbolSize;
        this.hoverAnimation=hoverAnimation;
        this.data=data;
    }

    /**
     * 上下 折线图定制(下图)
     * @param name
     * @param type
     * @param symbolSize
     * @param hoverAnimation
     * @param data
     */
    public Series(String name,String type , Integer xAxisIndex,Integer yAxisIndex,Integer symbolSize,boolean hoverAnimation,List<T> data){
        this.name=name;
        this.type=type;
        this.xAxisIndex=xAxisIndex;
        this.yAxisIndex=yAxisIndex;
        this.symbolSize=symbolSize;
        this.hoverAnimation=hoverAnimation;
        this.data=data;
    }
}
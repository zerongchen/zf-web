package com.aotain.zongfen.utils;

import java.util.HashMap;
import java.util.Map;

public class StaticData {
	private static Map<Integer,String> portType = new HashMap<Integer,String>();
	
	private static Map<Integer,String> deployMode = new HashMap<Integer,String>();
	
	public static void intitData() {
		portType.put(1, "GE(光口)");
		portType.put(2, "GE(电口)");
		portType.put(3, "10G POS");
		portType.put(4, "10GE");
		portType.put(5, "40G POS");
		portType.put(6, "40GE");
		portType.put(7, "100GE");
		
		deployMode.put(1, "并行单向上行");
		deployMode.put(2, "并行单向下行");
		deployMode.put(3, "并行双向");
		deployMode.put(4, "串行单向上行");
		deployMode.put(5, "串行单向下行");
		deployMode.put(6, "串行双向");
	}
	
	public static String getPortType(int type) {
		return portType.get(type);
	}
	
	public static String getDeployMode(int type) {
		return deployMode.get(type);
	}
}

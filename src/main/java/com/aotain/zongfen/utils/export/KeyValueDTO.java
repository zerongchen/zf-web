package com.aotain.zongfen.utils.export;

public class KeyValueDTO implements java.io.Serializable {

	private static final long serialVersionUID = -1384988812720447808L;

	private String key;

	private String value;

	public KeyValueDTO() {
	}

	public KeyValueDTO(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

package com.aotain.zongfen.model.common;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ChinaArea implements Serializable {
	@NonNull private Long areaCode;

	@NonNull private String areaName;

	@NonNull private Long parent;

	@NonNull private String areaType;

	@NonNull private String areaShort;

	private int type;
	
    private static final long serialVersionUID = 1L;
}
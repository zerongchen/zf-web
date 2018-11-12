package com.aotain.zongfen.comparator;


import com.aotain.zongfen.annotation.Export;

import java.lang.reflect.Field;
import java.util.Comparator;

public class FieldComparator implements Comparator<Field> {

	@Override
	public int compare(Field firstField, Field secondField) {
		Export firstExport = firstField.getAnnotation(Export.class);
		Export secondExport = secondField.getAnnotation(Export.class);
		return firstExport.id() - secondExport.id();
	}

	
}

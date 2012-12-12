package com.ipoint.coursegenerator.core.elementparser;

import java.lang.reflect.Field;

import org.apache.poi.xwpf.usermodel.XWPFNumbering;

public class SecretField {

    public String getListStyle(XWPFNumbering instance)
	    throws IllegalArgumentException, IllegalAccessException {
	String strResult = null;
	Class secretClass = instance.getClass();

	Field[] fields = secretClass.getDeclaredFields();
	for (int i = 0; i < fields.length; i++) {
	    if (fields[i].getName().equals("ctNumbering")) {
		fields[i].setAccessible(true);
		strResult = fields[i].get(instance).toString();
	    }
	}
	if (strResult == null) {
	    strResult = "unknow list style";
	}
	return strResult;
    }
}
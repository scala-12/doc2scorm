package com.ipoint.coursegenerator.core.utils;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;

public class PictureInfo {

	private String name;

	private XWPFPictureData data;

	public PictureInfo(String pictureName, XWPFPictureData pictureData) {
		this.name = pictureName;
		this.data = pictureData;
	}

	public String getName() {
		return this.name;
	}

	public XWPFPictureData getData() {
		return this.data;
	}

}

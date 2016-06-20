package com.ipoint.coursegenerator.core.courseModel;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;

public class PictureInfo {

	private String name;

	private XWPFPictureData data;

	public PictureInfo(String imageName, XWPFPictureData pictureData) {
		this.name = imageName;
		this.data = pictureData;
	}

	public String getImageName() {
		return this.name;
	}

	public XWPFPictureData getPictureData() {
		return this.data;
	}

}

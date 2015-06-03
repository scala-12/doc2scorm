package com.ipoint.coursegenerator.core.internalCourse.items;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;

public class ImageItem extends AbstractItem {

	private XWPFPictureData pictureData;

	public ImageItem(XWPFPictureData pictureData) {
		this.pictureData = pictureData;
	}

	@Override
	public XWPFPictureData getValue() {
		return this.pictureData;
	}

	@Override
	public void setValue(Object pictureData) {
		this.pictureData = (XWPFPictureData) pictureData;
	}

}

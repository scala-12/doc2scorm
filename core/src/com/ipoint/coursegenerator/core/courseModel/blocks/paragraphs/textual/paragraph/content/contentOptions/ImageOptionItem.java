package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.utils.FileWork;

/**
 * Item that includes picture data
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ImageOptionItem extends AbstractContentItem {

	private XWPFPictureData value;

	/**
	 * @param imageData
	 *            Image that can't be null
	 */
	public ImageOptionItem(XWPFPictureData imageData) {
		if (!this.setValue(imageData)) {
			// TODO:exception
		}
	}

	public String getImageName() {
		return FileWork.IMAGE_PREFIX + String.valueOf(this.value.hashCode());
	}

	public XWPFPictureData getValue() {
		return this.value;
	}

	/**
	 * @param imageData
	 *            Image. If it is null then return false
	 * @return If successful then true
	 */
	public boolean setValue(XWPFPictureData imageData) {
		if (imageData == null) {
			return false;
		} else {
			this.value = imageData;
			return true;
		}
	}

	private String getImageType() {
		switch (this.value.getPictureType()) {
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG:
			return "png";
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP:
			return "bmp";
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF:
			return "gif";
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG:
			return "jpg";
		default:
			if (!this.value.getPackagePart().getContentType()
					.equals("image/x-emf")
					|| this.value.getPackagePart().getContentType()
							.equals("image/emf")) {
				return "png";
			} else {
				return null;
			}
		}
	}

	public String getImageFullName() {
		return this.getImageName() + "." + this.getImageType();
	}

	/**
	 * @return html element img
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element img = creatorTags.createElement("img");
		img.setAttribute("src", FileWork.IMAGE_PATH + this.getImageFullName());

		return img;
	}

}

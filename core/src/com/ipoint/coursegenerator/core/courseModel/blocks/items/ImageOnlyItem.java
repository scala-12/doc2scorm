package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.TextBlock;
import com.ipoint.coursegenerator.core.utils.FileWork;

/**
 * Item for {@link TextBlock}. This item includes picture data
 * 
 * @see TextBlock
 * @see HyperlinkBlock
 * @author Kalashnikov Vladislav
 *
 */
public class ImageOnlyItem extends AbstractTextItem {

	private XWPFPictureData value;

	private final static String IMAGE_PREFIX = "img_";

	/**
	 * Create image as Block Item
	 * 
	 * @param imageData
	 *            Image of block. There cannot be null
	 */
	public ImageOnlyItem(XWPFPictureData imageData) {
		if (!this.setValue(imageData)) {
			// TODO:exception
		}
	}

	public String getImageName() {
		return IMAGE_PREFIX.concat(String.valueOf(this.value.hashCode()));
	}

	public XWPFPictureData getValue() {
		return value;
	}

	/**
	 * Set value of item
	 * 
	 * @param imageData
	 *            Image of block
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
		return this.getImageName().concat(".").concat(this.getImageType());
	}

	@Override
	public Element toHtml(Document creatorTag, boolean isHyperlink) {
		Element img = creatorTag.createElement("img");
		img.setAttribute("src",
				FileWork.IMAGE_PATH.concat(this.getImageFullName()));

		return img;
	}

}

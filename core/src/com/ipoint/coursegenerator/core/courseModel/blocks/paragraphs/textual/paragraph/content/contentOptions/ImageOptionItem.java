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
	 * Height of picture in pixels
	 */
	private Integer height;

	/**
	 * Width of picture in pixels
	 */
	private Integer width;

	/**
	 * @param imageData
	 *            Image that can't be null
	 */
	public ImageOptionItem(XWPFPictureData imageData) {
		this(imageData, null);
	}

	/**
	 * 
	 * @param imageData
	 *            Image that can't be null
	 * @param style
	 *            Style of picture
	 */
	public ImageOptionItem(XWPFPictureData imageData, String style) {
		if (!this.setValue(imageData)) {
			// TODO:exception
		}

		this.height = toPxSize(getStyleValue(style, "height"));
		this.width = toPxSize(getStyleValue(style, "width"));
	}

	/**
	 * Returns attribute from style string
	 * 
	 * @param style
	 *            String includes style attributes
	 * @param attributeName
	 *            Style attribute name
	 * @return attribute from style string or null if attribute not founded
	 */
	private static String getStyleValue(String style, String attributeName) {
		if (style != null) {
			int startPos = style.indexOf(attributeName);
			if (startPos != -1) {
				startPos = style.indexOf(":", startPos) + 1;
				int endPos = style.indexOf(";", startPos);
				if (endPos == -1) {
					return style.substring(startPos).trim();
				} else {
					return style.substring(startPos, endPos).trim();
				}
			}
		}

		return null;
	}

	/**
	 * Returns size in pixels from other style
	 * 
	 * @param nonPxSize
	 *            Size in other style that includes name of style (look like
	 *            "12pt")
	 * @return Size in pixels as Integer or null if error
	 */
	private static Integer toPxSize(String nonPxSize) {
		Integer size = null;
		if (nonPxSize != null) {
			if (nonPxSize.endsWith("in")) {
				String sizeIn = nonPxSize.substring(0, nonPxSize.indexOf("in"));
				if (sizeIn != null) {
					size = (int) (4 / 3 * 72 * Float.valueOf(sizeIn));
				}
			} else if (nonPxSize.endsWith("pt")) {
				String sizePt = nonPxSize.substring(0, nonPxSize.indexOf("pt"));
				if (sizePt != null) {
					size = (int) (4 / 3 * Float.valueOf(sizePt));
				}
			}
		}

		return size;
	}

	/**
	 * Returns height of picture in pixels
	 * 
	 * @return height of picture in pixels
	 */
	public Integer getHeight() {
		return this.height;
	}

	public String getImageFullName() {
		return this.getImageName() + "." + this.getImageType();
	}

	public String getImageName() {
		return FileWork.IMAGE_PREFIX + String.valueOf(this.value.hashCode());
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
			if (!this.value.getPackagePart().getContentType().equals("image/x-emf")
					|| this.value.getPackagePart().getContentType().equals("image/emf")) {
				return "png";
			} else {
				return null;
			}
		}
	}

	public XWPFPictureData getValue() {
		return this.value;
	}

	/**
	 * Returns width of picture in pixels
	 * 
	 * @return width of picture in pixels
	 */
	public Integer getWidth() {
		return this.width;
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

	/**
	 * @return html element img
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element img = creatorTags.createElement("img");
		img.setAttribute("src", FileWork.IMAGE_PATH + this.getImageFullName());
		if (this.getHeight() != null) {
			img.setAttribute("height", String.valueOf(this.getHeight()));
		}
		if (this.getWidth() != null) {
			img.setAttribute("width", String.valueOf(this.getWidth()));
		}

		return img;
	}

}

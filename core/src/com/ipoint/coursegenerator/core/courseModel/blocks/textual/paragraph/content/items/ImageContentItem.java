package com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.items;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.AbstractContentItem;

/**
 * Item that includes picture data
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ImageContentItem extends AbstractContentItem<XWPFPictureData> {

	private Integer position;

	private int zPosition;

	public static final int LEFT_POSITION = 0;
	public static final int CENTER_POSITION = 1;
	public static final int RIGHT_POSITION = 2;

	public static final int BEHIND_POSITION = 0;
	public static final int INTO_POSITION = 1;
	public static final int FRONT_POSITION = 2;

	private static final String BEHIND_CLASS = "behind_text";
	private static final String FRON_CLASS = "before_text";

	public final static String IMAGE_DIR_PATH = "img/";
	public final static String IMAGE_PREFIX = "img_";

	/**
	 * Height of picture in pixels
	 */
	private Integer height;

	/**
	 * Width of picture in pixels
	 */
	private Integer width;

	/**
	 * 
	 * @param imageData
	 *            Image that can't be null
	 * @param style
	 *            Style of picture
	 * @param isWrap
	 *            If it is true then picture in text else behind or front
	 * @param multiplierSize
	 *            Multiplier of size
	 * 
	 */
	public ImageContentItem(XWPFPictureData imageData, String style, boolean isWrap, float multiplierSize) {
		this(imageData, style, isWrap, new Float(multiplierSize));
	}

	/**
	 * 
	 * @param imageData
	 *            Image that can't be null
	 * @param style
	 *            Style of picture
	 * @param isWrap
	 *            If it is true then picture in text else behind or front
	 * 
	 */
	public ImageContentItem(XWPFPictureData imageData, String style, boolean isWrap) {
		this(imageData, style, isWrap, null);
	}

	private ImageContentItem(XWPFPictureData imageData, String style, boolean isWrap, Float multiplierSize) {
		super(imageData);

		if (isWrap || (getAttrValue(style, "mso-position-horizontal") == null)) {
			this.zPosition = INTO_POSITION;
		} else if (getAttrValue(style, "z-index").contains("-")) {
			this.zPosition = BEHIND_POSITION;
		} else {
			this.zPosition = FRONT_POSITION;
		}

		this.height = Float
				.valueOf(toPxSize(getAttrValue(style, "height")) * ((multiplierSize == null) ? 1 : multiplierSize))
				.intValue();
		this.width = Float
				.valueOf(toPxSize(getAttrValue(style, "width")) * ((multiplierSize == null) ? 1 : multiplierSize))
				.intValue();
		this.position = getPositionFromMSWord(getAttrValue(style, "mso-position-horizontal"));
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
	private static String getAttrValue(String style, String attributeName) {
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
	private static Float toPxSize(String nonPxSize) {
		if (nonPxSize != null) {
			if (!nonPxSize.isEmpty()) {
				Float size = null;
				nonPxSize = nonPxSize.toLowerCase();
				if (nonPxSize.endsWith("in")) {
					String sizeIn = nonPxSize.substring(0, nonPxSize.indexOf("in"));
					if (sizeIn != null) {
						size = 4 / 3 * 72 * Float.parseFloat(sizeIn);
					}
				} else if (nonPxSize.endsWith("pt")) {
					String sizePt = nonPxSize.substring(0, nonPxSize.indexOf("pt"));
					if (sizePt != null) {
						size = 4 / 3 * Float.parseFloat(sizePt);
					}
				} else if (nonPxSize.endsWith("px")) {
					String sizePx = nonPxSize.substring(0, nonPxSize.indexOf("px"));
					if (sizePx != null) {
						size = Float.parseFloat(sizePx);
					}
				} else {
					try {
						size = Float.parseFloat(nonPxSize);
					} catch (NumberFormatException e) {
						size = null;
					}
				}

				if (size != null) {
					return size;
				}
			}
		}

		return null;
	}

	private static Integer getPositionFromMSWord(String positionMS) {
		if (positionMS != null) {
			if ("right".equalsIgnoreCase(positionMS)) {
				return RIGHT_POSITION;
			} else if ("left".equalsIgnoreCase(positionMS)) {
				return LEFT_POSITION;
			} else if ("center".equalsIgnoreCase(positionMS)) {
				return CENTER_POSITION;
			}
		}

		return null;
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
		return IMAGE_PREFIX + String.valueOf(this.value.hashCode());
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
			if (this.value.getPackagePart().getContentType().equals("image/x-emf")
					|| this.value.getPackagePart().getContentType().equals("image/emf")) {
				return "png";
			} else {
				return null;
			}
		}
	}

	/**
	 * Returns position code of image
	 * 
	 * @return position code of image
	 */
	public Integer getPosition() {
		return this.position;
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
	 * Returns z-index code position of picture
	 * 
	 * @return z-index code position of picture
	 */
	public int getZPosition() {
		return this.zPosition;
	}

	/**
	 * @return html element img
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element img = creatorTags.createElement("img");
		img.setAttribute("src", IMAGE_DIR_PATH + this.getImageFullName());

		if (this.getHeight() != null) {
			img.setAttribute("height", String.valueOf(this.getHeight()));
		}

		if (this.getWidth() != null) {
			img.setAttribute("width", String.valueOf(this.getWidth()));
		}

		if (this.getPosition() != null) {
			switch (this.getPosition()) {
			case CENTER_POSITION:
				img.setAttribute("align", "center");
				break;
			case LEFT_POSITION:
				img.setAttribute("align", "left");
				break;
			case RIGHT_POSITION:
				img.setAttribute("align", "right");
				break;
			}
		}

		if (this.getZPosition() == FRONT_POSITION) {
			img.setAttribute("class", FRON_CLASS);
		} else if (this.getZPosition() == BEHIND_POSITION) {
			img.setAttribute("class", BEHIND_CLASS);
		}

		return img;
	}

}

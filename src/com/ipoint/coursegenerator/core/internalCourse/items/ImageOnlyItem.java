package com.ipoint.coursegenerator.core.internalCourse.items;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

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

}

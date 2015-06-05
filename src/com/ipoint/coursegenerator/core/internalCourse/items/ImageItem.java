package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/**
 * Item for {@link TextBlock} or {@link TextBlock}. This item includes picture
 * data
 * 
 * @see TextBlock
 * @see HyperlinkBlock
 * @author Kalashnikov Vladislav
 *
 */
public class ImageItem extends AbstractItem {

	public ImageItem(Object value) {
		super(value);
	}

	/**
	 * Returns data of picture
	 * 
	 * @return Data of picture
	 */
	@Override
	public XWPFPictureData getValue() {
		return (XWPFPictureData) this.getValue();
	}

	@Override
	public List<Class<?>> getAvailableValueClasses() {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(XWPFPictureData.class);
		return classes;
	}

}

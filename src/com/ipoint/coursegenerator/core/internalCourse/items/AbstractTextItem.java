package com.ipoint.coursegenerator.core.internalCourse.items;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/***
 * Item for {@link TextBlock} data
 * 
 * @see TextBlock
 * @see HyperlinkBlock
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractTextItem extends AbstractItem {

	public Element toHtml(Document creatorTag, boolean isHyperlink) {
		return null;
	}

}

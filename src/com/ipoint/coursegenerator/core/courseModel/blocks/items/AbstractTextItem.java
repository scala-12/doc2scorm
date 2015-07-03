package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/***
 * Item that may be {@link TextOnlyItem} or {@link ImageOnlyItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractTextItem extends AbstractItem {

	/**
	 * @param isHyperlink
	 *            If true then don't uses tags "u" and "font" with parameter
	 *            "color"
	 * @return html-element span. There may have underline and color parameters
	 */
	public Element toHtml(Document creatorTags, boolean isHyperlink) {
		return this.toHtml(creatorTags);
	}

}

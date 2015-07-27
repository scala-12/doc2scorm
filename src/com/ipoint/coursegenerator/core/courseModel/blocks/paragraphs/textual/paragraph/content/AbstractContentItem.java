package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.FormulaOptionItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.ImageOptionItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.TextOptionItem;

/***
 * Item that may be {@link TextOptionItem}, {@link FormulaOptionItem} or
 * {@link ImageOptionItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractContentItem extends AbstractItem {

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

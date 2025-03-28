package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.TextualRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

/**
 * This item is other block: {@link TextualRunsBlock} or {@link HyperlinkRunsBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphSectionItem extends AbstractSectionItem<TextualRunsBlock> {

	public ParagraphSectionItem(TextualRunsBlock value) throws ItemCreationException {
		super(value);
	}

	/**
	 * @see TextualRunsBlock#toHtmlModel(Document)
	 * @see HyperlinkRunsBlock#toHtmlModel(Document)
	 */
	@Override
	public Element toHtmlModel(Document creatorTags) {
		return this.getValue().toHtmlModel(creatorTags);
	}

	@Override
	public String getText() {
		return this.getValue().getText();
	}

}

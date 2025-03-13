package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.list;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.AbstractTextualSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.ParagraphSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

/**
 * Item includes {@link ParagraphSectionBlock} or {@link ListSectionBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListSectionItem extends AbstractSectionItem<AbstractTextualSectionBlock<?>> {

	public ListSectionItem(AbstractTextualSectionBlock<?> paragraph) throws ItemCreationException {
		super(paragraph);
	}

	/**
	 * @return html-element li
	 */
	@Override
	public Element toHtmlModel(Document creatorTags) {
		Element listItem = creatorTags.createElement("li");
		Node itemValue = this.getValue().toHtmlModel(creatorTags);
		if (this.getValue() instanceof ParagraphSectionBlock) {
			// because in this situation tag p equal tag li
			while (itemValue.hasChildNodes()) {
				listItem.appendChild(itemValue.getFirstChild());
			}
		} else {
			listItem.appendChild(itemValue);
		}

		return listItem;
	}

	@Override
	public String getText() {
		return this.getValue().getText();
	}

}

package com.ipoint.coursegenerator.core.courseModel.content.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.TableSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.AbstractTextualSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

/**
 * Abstract paragraph block. These may be {@link TableSectionBlock} or
 * {@link AbstractTextualSectionBlock}
 * 
 * @author Kalashnikov Vladislav
 * 
 * @param <T>
 *            Type of item that included in block
 */
public abstract class AbstractSectionBlock<T extends AbstractSectionItem<?>> extends AbstractBlock<T>
		implements ConvertableWithText {

	protected AbstractSectionBlock(List<T> items) throws BlockCreationException {
		super(items);
	}

	protected AbstractSectionBlock(T item) throws BlockCreationException {
		super(item);
	}

	@Override
	public NodeList toSimpleHtml(Document creatorTags) {
		Element span = creatorTags.createElement("span");
		for (T item : this.getItems()) {
			NodeList subItems = item.toSimpleHtml(creatorTags);
			while (subItems.getLength() != 0) {
				span.appendChild(subItems.item(0));
			}
		}

		return span.getChildNodes();
	}

	@Override
	public String toString() {
		return this.getText();
	}

}

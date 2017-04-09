package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.AbstractTextualBlock;

/**
 * Abstract paragraph block. These may be {@link TableBlock} or
 * {@link AbstractTextualBlock}
 * 
 * @author Kalashnikov Vladislav
 * 
 * @param <T>
 *            Type of item that included in block
 */
public abstract class AbstractParagraphBlock<T extends AbstractParagraphItem<?>> extends AbstractBlock<T>
		implements ConvertableWithText {

	protected AbstractParagraphBlock(List<T> items) {
		super(items);
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

}

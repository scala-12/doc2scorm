package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.items;

import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.TextBlock;

/**
 * Item for {@link TextBlock}. This item includes text and him properties.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextContentItem extends AbstractContentItem<String> {

	/**
	 * Create text item from run of text
	 * 
	 * @param run
	 *            Run of text
	 */
	public TextContentItem(XWPFRun run) {
		super(run, run.toString());
	}

	/**
	 * @param value
	 *            Text of item. If there is null then return false
	 * @return if successful then true
	 */
	public boolean setValue(String text) {
		if (text != null) {
			if (!text.isEmpty()) {
				this.value = text;

				return true;
			}
		}

		return false;
	}

	@Override
	public String getText() {
		return this.getValue();
	}

	@Override
	protected Node getValueAsHtml(Document creatorTags) {
		return creatorTags.createTextNode(this.getValue());
	}

}

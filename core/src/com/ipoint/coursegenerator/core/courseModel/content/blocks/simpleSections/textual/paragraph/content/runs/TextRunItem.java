package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs;

import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.AbstractContentRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.TextualRunsBlock;

/**
 * Item for {@link TextualRunsBlock}. This item includes text and him
 * properties.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextRunItem extends AbstractContentRunItem<String> {

	/**
	 * Create text item from run of text
	 * 
	 * @param run
	 *            Run of text
	 */
	public TextRunItem(XWPFRun run) throws ItemCreationException {
		super(run, run.toString());
	}

	/**
	 * @param value
	 *            Text of item. If there is null then return false
	 * @return if successful then true
	 */
	public boolean isValidValue(String text) {
		if (super.isValidValue(text) && !text.isEmpty()) {
			return true;
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

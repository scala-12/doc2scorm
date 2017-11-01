package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.AbstractContentRunItem;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * Item which includes Math formula
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FormulaRunItem extends AbstractContentRunItem<Node> {

	private boolean paragraphFlag;

	public FormulaRunItem(Node math, boolean paragraphFlag) throws ItemCreationException {
		super(null, math);
		this.setParagraphFlag(paragraphFlag);
	}

	public boolean isParagraph() {
		return this.paragraphFlag;
	}

	public void setParagraphFlag(boolean flag) {
		this.paragraphFlag = flag;
	}

	@Override
	protected Node getValueAsHtml(Document creatorTags) {
		Element mathML = (Element) creatorTags.importNode(this.getValue(), true);

		mathML.removeAttribute("display");
		if (this.isParagraph()) {
			mathML.setAttribute("display", "block");
		} else {
			mathML.setAttribute("display", "inline");
		}

		return mathML;
	}

	@Override
	public String getText() {
		try {
			return this.toHtml(Tools.createEmptyDocument()).getTextContent();
		} catch (DOMException e) {
			return " ";
		}
	}

}

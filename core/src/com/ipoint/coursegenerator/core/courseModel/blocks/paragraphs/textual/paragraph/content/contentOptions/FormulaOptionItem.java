package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.parser.MathInfo;

/**
 * Item which includes Math formula
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FormulaOptionItem extends AbstractContentItem {

	private Node mathML;

	private boolean paragraphFlag;

	public FormulaOptionItem(MathInfo mathInfo, boolean paragraphFlag) {
		if (!this.setValue(mathInfo)) {
			// TODO:exception
		}
		this.setParagraphFlag(paragraphFlag);
	}

	public boolean isParagraph() {
		return this.paragraphFlag;
	}

	public Node getValue() {
		return this.mathML;
	}

	/**
	 * @param formula
	 *            Formula. If it is null then return false
	 * @return If successful then true
	 */
	public boolean setValue(MathInfo mathInfo) {
		if (mathInfo == null) {
			return false;
		} else {
			this.mathML = mathInfo.read();

			return true;
		}
	}

	public void setParagraphFlag(boolean flag) {
		this.paragraphFlag = flag;
	}

	/**
	 * Returns html-object math
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element mathML = (Element) creatorTags
				.importNode(this.getValue(), true);

		mathML.removeAttribute("display");
		if (this.isParagraph()) {
			mathML.setAttribute("display", "block");
		} else {
			mathML.setAttribute("display", "inline");
		}

		return mathML;
	}

}

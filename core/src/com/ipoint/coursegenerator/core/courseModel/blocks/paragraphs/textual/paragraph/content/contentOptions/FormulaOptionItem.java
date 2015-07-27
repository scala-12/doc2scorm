package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions;

import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;

/**
 * Item which includes Math formula
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FormulaOptionItem extends AbstractContentItem {

	private CTOMath value;

	private boolean isParagraph;

	public FormulaOptionItem(CTOMath formula) {
		this(formula, false);
	}

	public FormulaOptionItem(CTOMath formula, boolean paragraph) {
		if (!this.setValue(formula)) {
			// TODO:exception
		}
		this.isParagraph = paragraph;
	}

	public boolean isParagraph() {
		return this.isParagraph;
	}

	public CTOMath getValue() {
		return this.value;
	}

	/**
	 * @param formula
	 *            Formula. If it is null then return false
	 * @return If successful then true
	 */
	public boolean setValue(CTOMath formula) {
		if (formula == null) {
			return false;
		} else {
			this.value = formula;
			return true;
		}
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element span = creatorTags.createElement("span");
		if (this.isParagraph()) {
			span.setAttribute("class", "formuls");
		}

		return span;
	}

}
